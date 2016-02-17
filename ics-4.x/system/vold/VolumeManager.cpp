/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/mount.h>

#include <linux/kdev_t.h>

#define LOG_TAG "Vold"

#include <openssl/md5.h>

#include <cutils/log.h>

#include <sysutils/NetlinkEvent.h>

#include <linux/msdos_fs.h>
#include <linux/ext2_fs.h>

#include "unicode/ucnv.h"
#include "blkid/blkid.h"

#include "VolumeManager.h"
#include "DirectVolume.h"
#include "ResponseCode.h"
#include "Loop.h"
#include "Fat.h"
#include "Devmapper.h"
#include "Process.h"
#include "Asec.h"
#include "cryptfs.h"

#define MASS_STORAGE_FILE_PATH  "/sys/class/android_usb/android0/f_mass_storage/lun/file"

VolumeManager *VolumeManager::sInstance = NULL;
char *VolumeManager::pUuid = NULL;

VolumeManager *VolumeManager::Instance() {
    if (!sInstance)
        sInstance = new VolumeManager();
    return sInstance;
}

VolumeManager::VolumeManager() {
    mDebug = false;
    mVolumes = new VolumeCollection();
    mActiveContainers = new AsecIdCollection();
	mPaths = new PathCollection();
    mBroadcaster = NULL;
    mUmsSharingCount = 0;
    mSavedDirtyRatio = -1;
    // set dirty ratio to 0 when UMS is active
    mUmsDirtyRatio = 0;
    mVolManagerDisabled = 0;
}

VolumeManager::~VolumeManager() {
	PathCollection::iterator it;

    for (it = mPaths->begin(); it != mPaths->end(); ++it)
        free(*it);
    delete mPaths;
	
    delete mVolumes;
    delete mActiveContainers;
    free(pUuid);
}

char *VolumeManager::asecHash(const char *id, char *buffer, size_t len) {
    static const char* digits = "0123456789abcdef";

    unsigned char sig[MD5_DIGEST_LENGTH];

    if (buffer == NULL) {
        SLOGE("Destination buffer is NULL");
        errno = ESPIPE;
        return NULL;
    } else if (id == NULL) {
        SLOGE("Source buffer is NULL");
        errno = ESPIPE;
        return NULL;
    } else if (len < MD5_ASCII_LENGTH_PLUS_NULL) {
        SLOGE("Target hash buffer size < %d bytes (%d)",
                MD5_ASCII_LENGTH_PLUS_NULL, len);
        errno = ESPIPE;
        return NULL;
    }

    MD5(reinterpret_cast<const unsigned char*>(id), strlen(id), sig);

    char *p = buffer;
    for (int i = 0; i < MD5_DIGEST_LENGTH; i++) {
        *p++ = digits[sig[i] >> 4];
        *p++ = digits[sig[i] & 0x0F];
    }
    *p = '\0';

    return buffer;
}

void VolumeManager::setDebug(bool enable) {
    mDebug = enable;
    VolumeCollection::iterator it;
    for (it = mVolumes->begin(); it != mVolumes->end(); ++it) {
        (*it)->setDebug(enable);
    }
}

int VolumeManager::start() {
    return 0;
}

int VolumeManager::stop() {
    return 0;
}

int VolumeManager::addVolume(Volume *v) {
    mVolumes->push_back(v);
	SLOGD("#####addVolume------>mVolumes->size = %d",mVolumes->size());
    return 0;
}

int VolumeManager::delVolume(Volume *v) {
	VolumeCollection::iterator i;
	const char *label = v->getLabel();

    for (i = mVolumes->begin(); i != mVolumes->end(); ++i) {
        if (label[0] == '/') {
            if (!strcmp(label, (*i)->getMountpoint())){
           			mVolumes->erase(i);
					SLOGD("~~~Found mountPoint = %d",label);
					break;
            	}
        } else {
            if (!strcmp(label, (*i)->getLabel())) {
            		mVolumes->erase(i);
					SLOGD("~~~~Found Lable = %s", label);
					break;
            	}
        }
    }
	SLOGD("@@@@@delVolume----->mVolumes->size = %d",mVolumes->size());
    return 0;
}

int VolumeManager::addPath(const char *path) {
    mPaths->push_back(strdup(path));
    return 0;
}


void VolumeManager::handleBlockEvent(NetlinkEvent *evt) {
    const char *devpath = evt->findParam("DEVPATH");
	const char *devtype = evt->findParam("DEVTYPE");
	const char *dn = evt->findParam("DEVNAME");

	memset(mDeviceName, 0, 16);
	strcpy(mDeviceName, dn);
	SLOGI("lkm--handleBlockEvent--DeviceName:%s\n", mDeviceName);

	int major = -1;
	int minor = -1;
	bool isRawDisk = false;
	bool isPartition = false;
	int partIdx = -1;
	bool isSDCard = false;
    /* Lookup a volume to handle this device */
    VolumeCollection::iterator it;	

	/*determ what type device*/
	major = atoi(evt->findParam("MAJOR"));
	minor = atoi(evt->findParam("MINOR"));
	if(major == 8){ /*USB Disk */
	}else if(major == 179){ /*SDCard or EMMC */
		//SLOGE("zzllsd = %d",major);
			if(strncmp(dn,"mmcblk0",7) != 0){
				//SLOGE("zzllsd return because dn=%s",dn);
				return ;
			}
			isSDCard = true;
	}else {	/*other device, ignore*/
			//	SLOGE("zzll = %d",major);
			return ;
	}

	if(strcmp(devtype,"disk") == 0){
		const char *nparts = evt->findParam("NPARTS");
		if(nparts){
			int diskNumParts = atoi(nparts);
			isRawDisk = (diskNumParts == 0);			
		}else{ /*bad uevent*/
			SLOGE("Bad Uevent!");
			return ;
		}
		
	}else{
		const char *tmp = evt->findParam("PARTN");
		if(tmp == NULL){ /*bad uevent*/
			SLOGE("Bad Uevent!");
			return ;
		}
		partIdx = atoi(tmp);
		isPartition = true;
	}
	
	/*forward SDCard or USB mount handler*/	
	if(isRawDisk || isPartition) {
		
		//Lookup a volume to handle this device
		if(isSDCard){
			SLOGD("SDCard found!");
		    for (it = mVolumes->begin(); it != mVolumes->end(); ++it) {
				if((strcmp("sdcard",(*it)->getLabel()) == 0) && (strcmp("/mnt/sdcard",(*it)->getMountpoint())== 0) ){
			        if (!(*it)->handleBlockEvent(evt)) {
			            SLOGD("Device '%s' event handled by volume %s\n", devpath, (*it)->getLabel());
			            break;
			        }
				}
		    }
		}else{ /*USB Disc*/

			char * mountPoint = NULL;
			DirectVolume * volume = NULL;
			const char *dev = NULL;
			char disk[4]={0};
			char * pPath = NULL;
			
			SLOGD("USB Disk found!");
			if(evt->getAction() == NetlinkEvent::NlActionAdd){
			
				dev = evt->findParam("DEVNAME");
				if(dev == NULL){ /*bad uevent*/
					SLOGE("DEVNAME Can't be found!");
					return;
				}
				disk[0]='s';
				disk[1]='d';
				disk[2]=dev[2];
				
				asprintf(&pPath,"/mnt/%s",disk);
				int ret = mkdir(pPath,0755);
				if(ret != 0 && errno != EEXIST){
					SLOGE("create directory %s fail, error info[%d]: %s\n", pPath, errno, strerror(errno));
					return;
				}
				asprintf(&mountPoint,"%s/%s",pPath,dev);
				if(mountPoint == NULL){
					SLOGD("allocate mountPoint Error");
					return;
				}
				SLOGD("Allocate mount point '%s'",mountPoint);
				ret = mkdir(mountPoint, 0755);
				if(ret != 0 && errno != EEXIST)
				{
					SLOGE("create mount point %s fail, error info[%d]: %s\n", mountPoint, errno, strerror(errno));
					return;
				}				
				volume = new DirectVolume(this,dev,mountPoint,partIdx);
				free(mountPoint);
				free(pPath);
				/*copy global path into USB volume so that DirectVolume can handle it*/
				PathCollection::iterator  it;
			    for (it = mPaths->begin(); it != mPaths->end(); ++it) {
					volume->addPath(*it);
	    		}
				
				addVolume(volume);
				if(volume->handleBlockEvent(evt)!=0){
					SLOGD("New add volume failed to handle the event of %s",devpath);
				}else{
					SLOGD("Succeed to adding volume %s to handle the event of %s",dev,devpath);
				}
			}
			else if (evt->getAction() == NetlinkEvent::NlActionRemove
				||evt->getAction() == NetlinkEvent::NlActionChange){
				/*Just follow normal flow*/
				
				dev = evt->findParam("DEVNAME");
				if(dev == NULL){
					return;
				}
				Volume *v = lookupVolume(dev);
			    if (!v) {
			        SLOGE("!!Can not find %s!!",dev);					
			        return ;
			    }
				if (!v->handleBlockEvent(evt)) {
			        SLOGD("Device '%s' event handled by volume %s\n", devpath, v->getLabel());

					/*remove volume from volume list*/	
					delVolume(v);
			        return;
			    }
				
				
			}else{
					SLOGE("!!!!Unknown Action !!!!");
				}
	    }
	}
	
}



//lkm
/*
through "/proc/mounts" to get device path & volume filesystem type
*/
static bool getDeviceMountInfo(const char* mount_point, char* buf, int size) {
    char device_path[32];
    char mount_path[32];
    char volume_FsType[32];
    FILE *fp_mount;
    char line[256];

	SLOGI("lkm--mount_point: (%s)\n",mount_point);
    if (mount_path == NULL || buf == NULL || size < 0) {
        return false;
    }

    if (!(fp_mount = fopen("/proc/mounts", "r"))) {
        SLOGE("Error opening /proc/mounts (%s)", strerror(errno));
        return false;
    }

    while(fgets(line, sizeof(line), fp_mount)) {
        line[strlen(line)-1] = '\0';
        sscanf(line, "%31s %31s %31s\n", device_path, mount_path, volume_FsType);
        if (!strcmp(mount_point, mount_path)) {
			SLOGI("lkm-1-device_path: (%s) mount_path: (%s) volume_FsType: (%s)\n",device_path,mount_path,volume_FsType);
            fclose(fp_mount);
            strncpy(buf, line, size);
            return true;
        }
    }

    fclose(fp_mount);
    return false;
}
//lkm


//lkm
/*
through exec "blkid" to get volume label
*/
static int getOriginalVolumeLabel(char* partialname, int volumeLabelLength, char* volumeLabel) {

    SLOGI("lkm--partialname: (%s)\n",partialname);

    FILE* fp_blkid;
    char ps_line[128];  /* getline() will allocate memory for it */
    size_t i4_line_len = 0;
    char devname[128];
    unsigned int ui4_dev_len;
    char* ps_label = NULL;

    const char *blkid_out_file = "/tmp/blkid.tab";

    int i4_ret;
    int i = 0;

    /* Call blkid in busybox. The out put will look like:
     * /dev/sda1: LABEL="KINGSTON" UUID="ODBD-6753"
     * /dev/sdb: LABEL="12345" UUID="F044-EB18"
     * /dev/sde1: LABEL="abcde"
     */
    memset(devname, 0, 128);
    sprintf(devname, "/dev/block/%s", partialname);  
    ui4_dev_len = strlen(devname);
    memset(volumeLabel, 0, volumeLabelLength);
    system("blkid > /tmp/blkid.tab");   
    fp_blkid = fopen(blkid_out_file, "r");       
    if (fp_blkid == NULL)
    {
        SLOGI("open file fail\n");
        return -1;
    }
    /* For each line of blkid output */
    while ((fgets(ps_line, 128, fp_blkid)) != NULL)
    {
        if (ps_line == NULL)
        {
            break;
        }

        /* Test whether line start with right dev node such as "/dev/sda1:" */
        if (ps_line[ui4_dev_len] == ':' 
            && strncmp(ps_line, devname, ui4_dev_len) == 0)
        {
            if (strncmp(ps_line + ui4_dev_len + strlen(": "), "LABEL", strlen("LABEL")) != 0)
            {
                /* There are no LABEL= in the line. */
                volumeLabel[0] = '\0';
                break;
            }
            else
            {
			 
                ps_label = ps_line + ui4_dev_len + strlen(": LABEL=\"");// "\" 
                    
                for (i = 0; i < volumeLabelLength -1; i++)
                {
                    if (*ps_label == '\0')
                    {
                        /* There is no UUID, remove the last  "  */
                        i--;
                        /* Check if there is a new line delimiter */
                        ps_label--;
                        if (*ps_label == '\n')
                        {
                            i--;
                        }
                        break;
                    }
                    
                    if (strncmp(ps_label, "\" UUID=\"", strlen("\" UUID=\"")) == 0)
                    {
                        break;
                    }
                    volumeLabel[i] = *ps_label++;
                }

                volumeLabel[i] = '\0';
		  SLOGI("lkm--volumeLabel--volumeLabel:%s\n", volumeLabel);

                /* Check if the buffer is bigger enough. */
                if (i == volumeLabelLength - 1)
                {
                    /* Find the actual buffer size needed. */
                    while ((*ps_label != '\0')
                            && (strncmp(ps_label, "\" UUID=\"", strlen("\" UUID=\"")) != 0))
                    {
                        i++;
                        ps_label++;
                    }

                    volumeLabelLength = i;

                    fclose(fp_blkid);
                    remove(blkid_out_file);
                }
                break;
            }
        }
    }

    fclose(fp_blkid);
    remove(blkid_out_file);
    return 0;	
}

//lkm

//lkm



//zenglei
static int getVolumeLabelAndUuid(char* partialname, int volumeLabelLength, char* volumeLabel, int volumeUuidLength, char* volumeUuid)
{
    FILE* fp_blkid;
    char ps_line[128];  /* getline() will allocate memory for it */
    size_t i4_line_len = 0;
    char devname[128];
    unsigned int ui4_dev_len;

    const char *blkid_out_file = "/tmp/blkid.tab";

    /* Call blkid in busybox. The out put will look like:
     * /dev/sda1: LABEL="KINGSTON" UUID="ODBD-6753"
     * /dev/sdb: LABEL="12345" UUID="F044-EB18"
     * /dev/sde1: LABEL="abcde"
     */
    memset(devname, 0, 128);
    sprintf(devname, "/dev/block/%s", partialname);  
    ui4_dev_len = strlen(devname);
    memset(volumeLabel, 0, volumeLabelLength);
    memset(volumeUuid, 0, volumeUuidLength);

    system("busybox blkid > /tmp/blkid.tab");   
    
    fp_blkid = fopen(blkid_out_file, "r");       
    if (fp_blkid == NULL)
    {
        SLOGE("getVolumeLabelAndUuid open file fail\n");
        return -1;
    }

    /* For each line of blkid output */
    while ((fgets(ps_line, 128, fp_blkid)) != NULL)
    {
        if (ps_line == NULL)
        {
            break;
        }

        /* Test whether line start with right dev node such as "/dev/sda1:" */
        if (ps_line[ui4_dev_len] == ':' 
            && strncmp(ps_line, devname, ui4_dev_len) == 0)
        {
            char* pLabel1 = strstr(ps_line, "LABEL=\"");
            if (pLabel1)
            {
                pLabel1 += 7;
                if (*pLabel1)
                {
                    char* pLabel2 = strchr(pLabel1, '\"');
                    if (pLabel2)
                    {
                        int len = pLabel2 - pLabel1;
                        if (len > volumeLabelLength - 1)
                            len = volumeLabelLength - 1;
                        strncpy(volumeLabel, pLabel1, len);
                        volumeLabel[len] = 0;
                    }
                }
            }
            char* pUuid1 = strstr(ps_line, "UUID=\"");
            if (pUuid1)
            {
                pUuid1 += 6;
                if (*pUuid1)
                {
                    char* pUuid2 = strchr(pUuid1, '\"');
                    if (pUuid2)
                    {
                        int len = pUuid2 - pUuid1;
                        if (len > volumeUuidLength - 1)
                            len = volumeUuidLength - 1;
                        strncpy(volumeUuid, pUuid1, len);
                        volumeUuid[len] = 0;
                    }
                }
            }
            break;
        }
    }

    fclose(fp_blkid);
    remove(blkid_out_file);
    return 0;
}
//zenglei
/*
Got volume label & volume filesystem type then send Msg to MountService
*/
/*
int VolumeManager::getVolumeLabel(SocketClient *cli, const char *mount_point, const char *device_name) {

    char mount_info[256];
    char device_path[32];
    char mount_path[32];
    char volume_FsType[32];

    char* buffer = NULL;
	char buf[32];
	int bRet = 0;

	memset(buf, 0, 32);
	SLOGI("lkm--passed MOUNTPOINT: (%s) DEVNAME: (%s)\n", mount_point, mDeviceName);
	bRet = getOriginalVolumeLabel(mDeviceName, 32, buf);
	if((bRet != 0) || (buf[0] == '\0')) {
    	strcpy(buf,"RemovableDevice");
    }

	SLOGI("lkm--Got volume label: (%s)\n", buf);
   
	if (getDeviceMountInfo(mount_point,mount_info,256)) {
		sscanf(mount_info, "%31s %31s %31s\n", device_path, mount_path, volume_FsType);
		SLOGI("lkm-2-volume filesystem type: (%s)",volume_FsType);

		asprintf(&buffer, "%s %s %s", mount_point, volume_FsType, buf);
		cli->sendMsg(ResponseCode::VolumeDiskLabel, buffer, false);
		
		free(buffer);
		buffer = NULL;
		return 0;
	}

	SLOGI("lkm--Got volume filesystem type fail\n");
	return -1;

}
//lkm

*/
int VolumeManager::listVolumes(SocketClient *cli) {
    VolumeCollection::iterator i;

    for (i = mVolumes->begin(); i != mVolumes->end(); ++i) {
        char *buffer;
        asprintf(&buffer, "%s %s %d",
                 (*i)->getLabel(), (*i)->getMountpoint(),
                 (*i)->getState());
        cli->sendMsg(ResponseCode::VolumeListResult, buffer, false);
        free(buffer);
    }
    cli->sendMsg(ResponseCode::CommandOkay, "Volumes listed.", false);
    return 0;
}

int VolumeManager::formatVolume(const char *label) {
    Volume *v = lookupVolume(label);

    if (!v) {
        errno = ENOENT;
        return -1;
    }

    if (mVolManagerDisabled) {
        errno = EBUSY;
        return -1;
    }

    return v->formatVol();
}
/*
static bool getDeviceMountInfo(const char* path, char* buf, int size) {
    char device[256];
    char mount_path[256];
    char rest[256];
    FILE *fp;
    char line[1024];

    if (path == NULL || buf == NULL || size < 0) {
        return false;
    }

    if (!(fp = fopen("/proc/mounts", "r"))) {
        SLOGE("Error opening /proc/mounts (%s)", strerror(errno));
        return false;
    }

    while(fgets(line, sizeof(line), fp)) {
        line[strlen(line)-1] = '\0';
        sscanf(line, "%255s %255s %255s\n", device, mount_path, rest);
        if (!strcmp(mount_path, path)) {
            fclose(fp);
            strncpy(buf, line, size);
            return true;
        }
    }

    fclose(fp);
    return false;
}
*/
static bool getFatVolumeLabel(const char* devPath, char *label, int len, int* label_len) {
#define FAT16_BSX_OFFSET 36
#define FAT32_BSX_OFFSET 64

#define ATTR_READ_ONLY 0x01
#define ATTR_HIDDEN 0x02
#define ATTR_SYSTEM 0x04
#define ATTR_VOLUME_ID 0x08
#define ATTR_DIRECTORY 0x10
#define ATTR_ARCHIVE 0x20
#define ATTR_LONG_NAME (ATTR_READ_ONLY |ATTR_HIDDEN |ATTR_SYSTEM |ATTR_VOLUME_ID)

    int fd;
    int ret;
    struct fat_boot_sector* fat_boot;
    struct fat_boot_bsx* boot_bsx;
    char buf[2048];
    int size = sizeof(buf);

    unsigned long sector_size;
    unsigned long cluster_sector_number;
    unsigned long reserved_sectors;
    unsigned long fat_number;
    unsigned long fat_sectors;
    unsigned long root_cluster;

    unsigned long root_dir_offset;
    struct msdos_dir_entry entry;

    if (devPath == NULL || label == NULL || len < 0 || label_len == NULL) {
        return false;
    }

    fd = open(devPath,O_RDONLY);
    if (fd < 0) {
        SLOGE("Can not open device %s",devPath);
        return false;
    }

    ret = read(fd,buf,size);
    if (ret < 0) {
        SLOGE("Fail to read fat boot section %s",devPath);
        close(fd);
        return false;
    }

    fat_boot = (struct fat_boot_sector*)(buf);

    sector_size = __le16_to_cpu(*(unsigned short*)(fat_boot->sector_size));
    cluster_sector_number = fat_boot->sec_per_clus;
    reserved_sectors = __le16_to_cpu(fat_boot->reserved);
    fat_number = fat_boot->fats;

    fat_sectors = __le16_to_cpu(fat_boot->fat_length);
    if (fat_sectors == 0 && fat_boot->fat32_length) {
        fat_sectors = __le32_to_cpu(fat_boot->fat32_length);
        root_cluster = __le32_to_cpu(fat_boot->root_cluster);
        root_dir_offset = reserved_sectors + fat_number*fat_sectors + (root_cluster-2);
        root_dir_offset = root_dir_offset * sector_size;
        boot_bsx = (struct fat_boot_bsx*)(buf+FAT32_BSX_OFFSET);
    } else {
        root_dir_offset = reserved_sectors + fat_number*fat_sectors;
        root_dir_offset = root_dir_offset * sector_size;
        boot_bsx = (struct fat_boot_bsx*)(buf+FAT16_BSX_OFFSET);
    }

    ret = lseek(fd,root_dir_offset,0);
    if (ret < 0) {
        SLOGE("Fail to seek fpos %i to roor directory",root_dir_offset);
        close(fd);
        return false;
    }

    size = sizeof(entry);
    while (read(fd,&entry,size) == size) {
        if (entry.name[0] == DELETED_FLAG) {
            continue;
        } else if (entry.attr == ATTR_VOLUME) {
            int i = 0;
            if (entry.starthi != 0 || entry.start != 0 || entry.size != 0) {
                continue;
            }

            while(i < MSDOS_NAME && entry.name[i]) {
                ++i;
            }
            if (i > len) {
                i = len;
            }
            *label_len = i;

            strncpy(label,(const char*)entry.name,i);
            close(fd);
            return true;
        } else {
            if ((entry.attr & ATTR_VOLUME_ID) != 0) {
                if (entry.attr != ATTR_LONG_NAME) {
                    break;
                }
            } else {
                if (ATTR_NONE < entry.attr && entry.attr < ATTR_VOLUME_ID) {
                    continue;
                }

                entry.attr = entry.attr & (~ATTR_LONG_NAME);
                if (entry.attr != ATTR_DIRECTORY && entry.attr != ATTR_ARCHIVE) {
                    break;
                }
            }
        }
    }

    close(fd);

    if (strncmp("NO NAME    ",(char*)boot_bsx->vol_label,11) == 0) {
        return false;
    }

        if (len > MSDOS_NAME) {
            len = MSDOS_NAME;
        }
        *label_len = len;
        strncpy(label, (char*)boot_bsx->vol_label,len);

    return true;
}

static bool getNTFSVolumeLabel(const char* devPath, char *label, int len, int* label_len) {
    #define IOCTL_GET_VOLUME_LABEL _IOR('r', 0x13, __u32)
    int fd;
    int ret;
    char tmp[512];
    int size = sizeof(tmp);
    int i;
	

    if (devPath == NULL || label == NULL || len < 0 || label_len == NULL) {
        return false;
    }

    fd = open(devPath,O_RDONLY);
    if (fd < 0) {
        SLOGE("Can not open device %s",devPath);
        return false;
    }

    memset(tmp,0,size);
    ret = ioctl(fd,IOCTL_GET_VOLUME_LABEL,tmp);
    if (ret == -1) {
        SLOGE("Can get the NTFS label");
        close(fd);
        return false;
    }

    close(fd);

    i = 0;
    while (i < size && tmp[i]) {
        ++i;
    }
    if (i > len) {
        i = len;
    }
    *label_len = i;

    strncpy(label,tmp,i);
    close(fd);
    return true;
}

static bool getExtVolumeLabel(const char* devPath, char *label, int len, int* label_len) {
    int fd;
    int ret;
    struct ext2_super_block super_block;
    int i;

    if (devPath == NULL || label == NULL || len < 0 || label_len == NULL) {
        return false;
    }

    fd = open(devPath,O_RDONLY);
    if (fd < 0) {
        SLOGE("Can not open device %s",devPath);
        return false;
    }

    ret = lseek(fd,1024,0);
    if (ret < 0) {
        SLOGE("Can not seek to ext2 super block");
        close(fd);
        return false;
    }

    ret = read(fd,&super_block,sizeof(super_block));
    if (ret < 0) {
        SLOGE("Can not read ext2 super block");
        close(fd);
        return false;
    }

    if (__le16_to_cpu(super_block.s_magic) != EXT2_SUPER_MAGIC) {
        SLOGE("Ext2 super block has error magic");
        close(fd);
        return false;
    }
    close(fd);

    i = strlen(super_block.s_volume_name);
    if (i > len) {
        i = len;
    }
    *label_len = i;
    strncpy(label,super_block.s_volume_name,i);
    return true;
}

static const char* getLabelCodeType(char* str, int size) {
    int i;
    int j;
    unsigned char* buf = (unsigned char*)str;

    if (size > 3 && buf[0] ==0xef && buf[1] ==0xbb && buf[2] ==0xbf) {
        buf += 3;
        size -= 3;
    }
    /* Fat volume label from windows is considered as GBK. Other volume
     * labels are considered as utf-8.
     */
    for (i = 0; i < size; ++i) {
        if ((buf[i] & 0x80) == 0) {
            continue;
        } else if((buf[i] & 0x40) == 0) {
            return "GBK";
        } else {
            int following;

            if ((buf[i] & 0x20) == 0) {
                following = 1;
            } else if ((buf[i] & 0x10) == 0) {
                following = 2;
            } else if ((buf[i] & 0x08) == 0) {
                following = 3;
            } else if ((buf[i] & 0x04) == 0) {
                following = 4;
            } else if ((buf[i] & 0x02) == 0) {
                following = 5;
            } else
                return "GBK";

            /* ASCII in utf-8 is always like 0xxxxxxx.
             * Chineses in utf-8 is always like 1110xxxx 10xxxxxx 10xxxxxx.
             * So, if we find "110xxxxx 10xxxxxx", consider it as GBK.
             */
            if (following == 1) {
                return "GBK";
            }

            for (j = 0; j < following; j++) {
                i++;
                if (i >= size)
                    goto done;

                if ((buf[i] & 0x80) == 0 || (buf[i] & 0x40))
                    return "GBK";
            }
        }
    }
done:
    return "UTF-8";
}
//int VolumeManager::getVolumeLabel(SocketClient *cli, const char *mount_point, const char *device_name) {
int VolumeManager::getVolumeLabel(SocketClient *cli, const char *pathStr) {
    char mountInfo[1024];
    char device[256];
    char mountPath[256];
    char fsType[256];
    char rest[256];
    bool bRet = false;
    char label[512];
    int size = sizeof(label);
    int label_len = 0;
    UErrorCode ErrorCode = U_ZERO_ERROR;

    //Add by weijb 
    char* buffer = NULL;
    char buf[32];
    int bRet1 = 0;
	
    if (getDeviceMountInfo(pathStr,mountInfo,1024)) {
        sscanf(mountInfo, "%255s %255s %255s %255s\n", device, mountPath, fsType, rest);

        if (strncmp(fsType,"vfat",4) == 0) {
            bRet = getFatVolumeLabel(device,label,size,&label_len);
        } else if (strncmp(fsType,"fuseblk",7) == 0) {
//================================
       label_len = 32;
	memset(buf, 0, 32);
	
	memset(mDeviceName, 0, 16);
	strncpy(mDeviceName,mountPath+9,4);
	bRet1 = getOriginalVolumeLabel(mDeviceName, 32, label);
	if((bRet1 != 0) || (label[0] == '\0')) {
    		strcpy(label,"RemovableDevice");
    }
		        char target_ntfs[1024+1];
		        int target_len_ntfs = sizeof(target_ntfs) - 1;
		        const char* codeType_ntfs = getLabelCodeType(label,32);
		        int ret_ntfs = 0;
					
		        ret_ntfs = ucnv_convert("UTF-8",codeType_ntfs,target_ntfs,target_len_ntfs,label,32,&ErrorCode);
		        if (ErrorCode == U_BUFFER_OVERFLOW_ERROR) {
		            SLOGE("Too long volume label of %s",pathStr);
		            return -1;
		        } else if(ErrorCode == U_STRING_NOT_TERMINATED_WARNING) {
		            target_ntfs[ret_ntfs] = '\0';
		        }

		        cli->sendMsg(ResponseCode::VolumeListResult,target_ntfs,false);
		        return 0;
        } 
//================================
		else if (strncmp(fsType,"ext",3) == 0) {
            bRet = getExtVolumeLabel(device,label,size,&label_len);
        }
    }

    if (bRet) {
        char target[1024+1];
        int target_len = sizeof(target) - 1;
        const char* codeType = getLabelCodeType(label,label_len);
        int ret = 0;
			
        ret = ucnv_convert("UTF-8",codeType,target,target_len,label,label_len,&ErrorCode);
        if (ErrorCode == U_BUFFER_OVERFLOW_ERROR) {
            SLOGE("Too long volume label of %s",pathStr);
            return -1;
        } else if(ErrorCode == U_STRING_NOT_TERMINATED_WARNING) {
            target[ret] = '\0';
        }

        cli->sendMsg(ResponseCode::VolumeListResult,target,false);
        return 0;
    }

    return -1;
}

//add by zenglei
char* VolumeManager::getVolumeUuid(const char* mountPoint)
{

SLOGD("getDeviceMountInfo mountPoint = %s" , mountPoint);	

 char mountInfo[1024];
    char device[256];
    char mountPath[256];
    char fsType[256];
    char rest[256];
	char label[512];
	char uuid[64];
       int bRet1 = 0;
       memset(uuid, 0, 64);
	if(getDeviceMountInfo(mountPoint, mountInfo, 1024)){
		SLOGD("getDeviceMountInfo is true");	
		 sscanf(mountInfo, "%255s %255s %255s %255s\n", device, mountPath, fsType, rest);
		memset(mDeviceName, 0, 16);
		char *tmp = strstr(mountPath,"sdcard");
		if(tmp!=NULL){
			strncpy(mDeviceName,"mmcblk0p1",9);			
		}
		else{	
		strncpy(mDeviceName,mountPath+9,4);
		}
		SLOGD("mDeviceName = %s" , mDeviceName);	
		bRet1 = getVolumeLabelAndUuid(mDeviceName,32,label,64,uuid);
		pUuid = strdup(uuid);
		SLOGD("getVolumeUuid uuid=%s  label = %s" ,pUuid,label);
		return pUuid;
		}
  
    return NULL;
}


int VolumeManager::getObbMountPath(const char *sourceFile, char *mountPath, int mountPathLen) {
    char idHash[33];
    if (!asecHash(sourceFile, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", sourceFile, strerror(errno));
        return -1;
    }

    memset(mountPath, 0, mountPathLen);
    snprintf(mountPath, mountPathLen, "%s/%s", Volume::LOOPDIR, idHash);

    if (access(mountPath, F_OK)) {
        errno = ENOENT;
        return -1;
    }

    return 0;
}

int VolumeManager::getAsecMountPath(const char *id, char *buffer, int maxlen) {
    char asecFileName[255];
    snprintf(asecFileName, sizeof(asecFileName), "%s/%s.asec", Volume::SEC_ASECDIR, id);

    memset(buffer, 0, maxlen);
    if (access(asecFileName, F_OK)) {
        errno = ENOENT;
        return -1;
    }

    snprintf(buffer, maxlen, "%s/%s", Volume::ASECDIR, id);
    return 0;
}

int VolumeManager::getAsecFilesystemPath(const char *id, char *buffer, int maxlen) {
    char asecFileName[255];
    snprintf(asecFileName, sizeof(asecFileName), "%s/%s.asec", Volume::SEC_ASECDIR, id);

    memset(buffer, 0, maxlen);
    if (access(asecFileName, F_OK)) {
        errno = ENOENT;
        return -1;
    }

    snprintf(buffer, maxlen, "%s", asecFileName);
    return 0;
}

int VolumeManager::createAsec(const char *id, unsigned int numSectors,
                              const char *fstype, const char *key, int ownerUid) {
    struct asec_superblock sb;
    memset(&sb, 0, sizeof(sb));

    sb.magic = ASEC_SB_MAGIC;
    sb.ver = ASEC_SB_VER;

    if (numSectors < ((1024*1024)/512)) {
        SLOGE("Invalid container size specified (%d sectors)", numSectors);
        errno = EINVAL;
        return -1;
    }

    if (lookupVolume(id)) {
        SLOGE("ASEC id '%s' currently exists", id);
        errno = EADDRINUSE;
        return -1;
    }

    char asecFileName[255];
    snprintf(asecFileName, sizeof(asecFileName), "%s/%s.asec", Volume::SEC_ASECDIR, id);

    if (!access(asecFileName, F_OK)) {
        SLOGE("ASEC file '%s' currently exists - destroy it first! (%s)",
             asecFileName, strerror(errno));
        errno = EADDRINUSE;
        return -1;
    }

    /*
     * Add some headroom
     */
    unsigned fatSize = (((numSectors * 4) / 512) + 1) * 2;
    unsigned numImgSectors = numSectors + fatSize + 2;

    if (numImgSectors % 63) {
        numImgSectors += (63 - (numImgSectors % 63));
    }

    // Add +1 for our superblock which is at the end
    if (Loop::createImageFile(asecFileName, numImgSectors + 1)) {
        SLOGE("ASEC image file creation failed (%s)", strerror(errno));
        return -1;
    }

    char idHash[33];
    if (!asecHash(id, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", id, strerror(errno));
        unlink(asecFileName);
        return -1;
    }

    char loopDevice[255];
    if (Loop::create(idHash, asecFileName, loopDevice, sizeof(loopDevice))) {
        SLOGE("ASEC loop device creation failed (%s)", strerror(errno));
        unlink(asecFileName);
        return -1;
    }

    char dmDevice[255];
    bool cleanupDm = false;

    if (strcmp(key, "none")) {
        // XXX: This is all we support for now
        sb.c_cipher = ASEC_SB_C_CIPHER_TWOFISH;
        if (Devmapper::create(idHash, loopDevice, key, numImgSectors, dmDevice,
                             sizeof(dmDevice))) {
            SLOGE("ASEC device mapping failed (%s)", strerror(errno));
            Loop::destroyByDevice(loopDevice);
            unlink(asecFileName);
            return -1;
        }
        cleanupDm = true;
    } else {
        sb.c_cipher = ASEC_SB_C_CIPHER_NONE;
        strcpy(dmDevice, loopDevice);
    }

    /*
     * Drop down the superblock at the end of the file
     */

    int sbfd = open(loopDevice, O_RDWR);
    if (sbfd < 0) {
        SLOGE("Failed to open new DM device for superblock write (%s)", strerror(errno));
        if (cleanupDm) {
            Devmapper::destroy(idHash);
        }
        Loop::destroyByDevice(loopDevice);
        unlink(asecFileName);
        return -1;
    }

    if (lseek(sbfd, (numImgSectors * 512), SEEK_SET) < 0) {
        close(sbfd);
        SLOGE("Failed to lseek for superblock (%s)", strerror(errno));
        if (cleanupDm) {
            Devmapper::destroy(idHash);
        }
        Loop::destroyByDevice(loopDevice);
        unlink(asecFileName);
        return -1;
    }

    if (write(sbfd, &sb, sizeof(sb)) != sizeof(sb)) {
        close(sbfd);
        SLOGE("Failed to write superblock (%s)", strerror(errno));
        if (cleanupDm) {
            Devmapper::destroy(idHash);
        }
        Loop::destroyByDevice(loopDevice);
        unlink(asecFileName);
        return -1;
    }
    close(sbfd);

    if (strcmp(fstype, "none")) {
        if (strcmp(fstype, "fat")) {
            SLOGW("Unknown fstype '%s' specified for container", fstype);
        }

        if (Fat::format(dmDevice, numImgSectors)) {
            SLOGE("ASEC FAT format failed (%s)", strerror(errno));
            if (cleanupDm) {
                Devmapper::destroy(idHash);
            }
            Loop::destroyByDevice(loopDevice);
            unlink(asecFileName);
            return -1;
        }
        char mountPoint[255];

        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id);
        if (mkdir(mountPoint, 0777)) {
            if (errno != EEXIST) {
                SLOGE("Mountpoint creation failed (%s)", strerror(errno));
                if (cleanupDm) {
                    Devmapper::destroy(idHash);
                }
                Loop::destroyByDevice(loopDevice);
                unlink(asecFileName);
                return -1;
            }
        }

        if (Fat::doMount(dmDevice, mountPoint, false, false, false, ownerUid,
                         0, 0000, false)) {
            SLOGE("ASEC FAT mount failed (%s)", strerror(errno));
            if (cleanupDm) {
                Devmapper::destroy(idHash);
            }
            Loop::destroyByDevice(loopDevice);
            unlink(asecFileName);
            return -1;
        }
    } else {
        SLOGI("Created raw secure container %s (no filesystem)", id);
    }

    mActiveContainers->push_back(new ContainerData(strdup(id), ASEC));
    return 0;
}

int VolumeManager::finalizeAsec(const char *id) {
    char asecFileName[255];
    char loopDevice[255];
    char mountPoint[255];

    snprintf(asecFileName, sizeof(asecFileName), "%s/%s.asec", Volume::SEC_ASECDIR, id);

    char idHash[33];
    if (!asecHash(id, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", id, strerror(errno));
        return -1;
    }

    if (Loop::lookupActive(idHash, loopDevice, sizeof(loopDevice))) {
        SLOGE("Unable to finalize %s (%s)", id, strerror(errno));
        return -1;
    }

    snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id);
    // XXX:
    if (Fat::doMount(loopDevice, mountPoint, true, true, true, 0, 0, 0227, false)) {
        SLOGE("ASEC finalize mount failed (%s)", strerror(errno));
        return -1;
    }

    if (mDebug) {
        SLOGD("ASEC %s finalized", id);
    }
    return 0;
}

int VolumeManager::renameAsec(const char *id1, const char *id2) {
    char *asecFilename1;
    char *asecFilename2;
    char mountPoint[255];

    asprintf(&asecFilename1, "%s/%s.asec", Volume::SEC_ASECDIR, id1);
    asprintf(&asecFilename2, "%s/%s.asec", Volume::SEC_ASECDIR, id2);

    snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id1);
    if (isMountpointMounted(mountPoint)) {
        SLOGW("Rename attempt when src mounted");
        errno = EBUSY;
        goto out_err;
    }

    snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id2);
    if (isMountpointMounted(mountPoint)) {
        SLOGW("Rename attempt when dst mounted");
        errno = EBUSY;
        goto out_err;
    }

    if (!access(asecFilename2, F_OK)) {
        SLOGE("Rename attempt when dst exists");
        errno = EADDRINUSE;
        goto out_err;
    }

    if (rename(asecFilename1, asecFilename2)) {
        SLOGE("Rename of '%s' to '%s' failed (%s)", asecFilename1, asecFilename2, strerror(errno));
        goto out_err;
    }

    free(asecFilename1);
    free(asecFilename2);
    return 0;

out_err:
    free(asecFilename1);
    free(asecFilename2);
    return -1;
}

#define UNMOUNT_RETRIES 5
#define UNMOUNT_SLEEP_BETWEEN_RETRY_MS (1000 * 1000)

static int unmount_asec_reties = UNMOUNT_RETRIES;

int VolumeManager::unmountAsec(const char *id, bool force) {
    char asecFileName[255];
    char mountPoint[255];

    snprintf(asecFileName, sizeof(asecFileName), "%s/%s.asec", Volume::SEC_ASECDIR, id);
    snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id);

    char idHash[33];
    if (!asecHash(id, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", id, strerror(errno));
        return -1;
    }

    return unmountLoopImage(id, idHash, asecFileName, mountPoint, force);
}

int VolumeManager::unmountObb(const char *fileName, bool force) {
    char mountPoint[255];

    char idHash[33];
    if (!asecHash(fileName, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", fileName, strerror(errno));
        return -1;
    }

    snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::LOOPDIR, idHash);

    return unmountLoopImage(fileName, idHash, fileName, mountPoint, force);
}

int VolumeManager::unmountLoopImage(const char *id, const char *idHash,
        const char *fileName, const char *mountPoint, bool force) {
    if (!isMountpointMounted(mountPoint)) {
        SLOGE("Unmount request for %s when not mounted", id);
        errno = ENOENT;
        return -1;
    }

    int i, rc;
    for (i = 1; i <= unmount_asec_reties; i++) {
        rc = umount(mountPoint);
        if (!rc) {
            break;
        }
        if (rc && (errno == EINVAL || errno == ENOENT)) {
            SLOGI("Container %s unmounted OK", id);
            rc = 0;
            break;
        }
        SLOGW("%s unmount attempt %d failed (%s)",
              id, i, strerror(errno));

        int action = 0; // default is to just complain

        if (force) {
            if (i > (unmount_asec_reties - 2))
                action = 2; // SIGKILL
            else if (i > (unmount_asec_reties - 3))
                action = 1; // SIGHUP
        }

        Process::killProcessesWithOpenFiles(mountPoint, action);
        usleep(UNMOUNT_SLEEP_BETWEEN_RETRY_MS);
    }

    if (rc) {
        errno = EBUSY;
        SLOGE("Failed to unmount container %s (%s)", id, strerror(errno));
        return -1;
    }

    int retries = 10;

    while(retries--) {
        if (!rmdir(mountPoint)) {
            break;
        }

        SLOGW("Failed to rmdir %s (%s)", mountPoint, strerror(errno));
        usleep(UNMOUNT_SLEEP_BETWEEN_RETRY_MS);
    }

    if (!retries) {
        SLOGE("Timed out trying to rmdir %s (%s)", mountPoint, strerror(errno));
    }

    if (Devmapper::destroy(idHash) && errno != ENXIO) {
        SLOGE("Failed to destroy devmapper instance (%s)", strerror(errno));
    }

    char loopDevice[255];
    if (!Loop::lookupActive(idHash, loopDevice, sizeof(loopDevice))) {
        Loop::destroyByDevice(loopDevice);
    } else {
        SLOGW("Failed to find loop device for {%s} (%s)", fileName, strerror(errno));
    }

    AsecIdCollection::iterator it;
    for (it = mActiveContainers->begin(); it != mActiveContainers->end(); ++it) {
        ContainerData* cd = *it;
        if (!strcmp(cd->id, id)) {
            free(*it);
            mActiveContainers->erase(it);
            break;
        }
    }
    if (it == mActiveContainers->end()) {
        SLOGW("mActiveContainers is inconsistent!");
    }
    return 0;
}

int VolumeManager::destroyAsec(const char *id, bool force) {
    char asecFileName[255];
    char mountPoint[255];

    snprintf(asecFileName, sizeof(asecFileName), "%s/%s.asec", Volume::SEC_ASECDIR, id);
    snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id);

    if (isMountpointMounted(mountPoint)) {
        if (mDebug) {
            SLOGD("Unmounting container before destroy");
        }
        if (unmountAsec(id, force)) {
            SLOGE("Failed to unmount asec %s for destroy (%s)", id, strerror(errno));
            return -1;
        }
    }

    if (unlink(asecFileName)) {
        SLOGE("Failed to unlink asec '%s' (%s)", asecFileName, strerror(errno));
        return -1;
    }

    if (mDebug) {
        SLOGD("ASEC %s destroyed", id);
    }
    return 0;
}

int VolumeManager::mountAsec(const char *id, const char *key, int ownerUid) {
    char asecFileName[255];
    char mountPoint[255];

    snprintf(asecFileName, sizeof(asecFileName), "%s/%s.asec", Volume::SEC_ASECDIR, id);
    snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id);

    if (isMountpointMounted(mountPoint)) {
        SLOGE("ASEC %s already mounted", id);
        errno = EBUSY;
        return -1;
    }

    char idHash[33];
    if (!asecHash(id, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", id, strerror(errno));
        return -1;
    }

    char loopDevice[255];
    if (Loop::lookupActive(idHash, loopDevice, sizeof(loopDevice))) {
        if (Loop::create(idHash, asecFileName, loopDevice, sizeof(loopDevice))) {
            SLOGE("ASEC loop device creation failed (%s)", strerror(errno));
            return -1;
        }
        if (mDebug) {
            SLOGD("New loop device created at %s", loopDevice);
        }
    } else {
        if (mDebug) {
            SLOGD("Found active loopback for %s at %s", asecFileName, loopDevice);
        }
    }

    char dmDevice[255];
    bool cleanupDm = false;
    int fd;
    unsigned int nr_sec = 0;

    if ((fd = open(loopDevice, O_RDWR)) < 0) {
        SLOGE("Failed to open loopdevice (%s)", strerror(errno));
        Loop::destroyByDevice(loopDevice);
        return -1;
    }

    if (ioctl(fd, BLKGETSIZE, &nr_sec)) {
        SLOGE("Failed to get loop size (%s)", strerror(errno));
        Loop::destroyByDevice(loopDevice);
        close(fd);
        return -1;
    }

    /*
     * Validate superblock
     */
    struct asec_superblock sb;
    memset(&sb, 0, sizeof(sb));
    if (lseek(fd, ((nr_sec-1) * 512), SEEK_SET) < 0) {
        SLOGE("lseek failed (%s)", strerror(errno));
        close(fd);
        Loop::destroyByDevice(loopDevice);
        return -1;
    }
    if (read(fd, &sb, sizeof(sb)) != sizeof(sb)) {
        SLOGE("superblock read failed (%s)", strerror(errno));
        close(fd);
        Loop::destroyByDevice(loopDevice);
        return -1;
    }

    close(fd);

    if (mDebug) {
        SLOGD("Container sb magic/ver (%.8x/%.2x)", sb.magic, sb.ver);
    }
    if (sb.magic != ASEC_SB_MAGIC || sb.ver != ASEC_SB_VER) {
        SLOGE("Bad container magic/version (%.8x/%.2x)", sb.magic, sb.ver);
        Loop::destroyByDevice(loopDevice);
        errno = EMEDIUMTYPE;
        return -1;
    }
    nr_sec--; // We don't want the devmapping to extend onto our superblock

    if (strcmp(key, "none")) {
        if (Devmapper::lookupActive(idHash, dmDevice, sizeof(dmDevice))) {
            if (Devmapper::create(idHash, loopDevice, key, nr_sec,
                                  dmDevice, sizeof(dmDevice))) {
                SLOGE("ASEC device mapping failed (%s)", strerror(errno));
                Loop::destroyByDevice(loopDevice);
                return -1;
            }
            if (mDebug) {
                SLOGD("New devmapper instance created at %s", dmDevice);
            }
        } else {
            if (mDebug) {
                SLOGD("Found active devmapper for %s at %s", asecFileName, dmDevice);
            }
        }
        cleanupDm = true;
    } else {
        strcpy(dmDevice, loopDevice);
    }

    if (mkdir(mountPoint, 0777)) {
        if (errno != EEXIST) {
            SLOGE("Mountpoint creation failed (%s)", strerror(errno));
            if (cleanupDm) {
                Devmapper::destroy(idHash);
            }
            Loop::destroyByDevice(loopDevice);
            return -1;
        }
    }

    if (Fat::doMount(dmDevice, mountPoint, false, false, true, ownerUid, 0,
                     0222, false)) {
//                     0227, false)) {
        SLOGE("ASEC mount failed (%s)", strerror(errno));
        if (cleanupDm) {
            Devmapper::destroy(idHash);
        }
        Loop::destroyByDevice(loopDevice);
        return -1;
    }

    mActiveContainers->push_back(new ContainerData(strdup(id), ASEC));
    if (mDebug) {
        SLOGD("ASEC %s mounted", id);
    }
    return 0;
}

/**
 * Mounts an image file <code>img</code>.
 */
int VolumeManager::mountObb(const char *img, const char *key, int ownerUid) {
    char mountPoint[255];

    char idHash[33];
    if (!asecHash(img, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", img, strerror(errno));
        return -1;
    }

    snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::LOOPDIR, idHash);

    if (isMountpointMounted(mountPoint)) {
        SLOGE("Image %s already mounted", img);
        errno = EBUSY;
        return -1;
    }

    char loopDevice[255];
    if (Loop::lookupActive(idHash, loopDevice, sizeof(loopDevice))) {
        if (Loop::create(idHash, img, loopDevice, sizeof(loopDevice))) {
            SLOGE("Image loop device creation failed (%s)", strerror(errno));
            return -1;
        }
        if (mDebug) {
            SLOGD("New loop device created at %s", loopDevice);
        }
    } else {
        if (mDebug) {
            SLOGD("Found active loopback for %s at %s", img, loopDevice);
        }
    }

    char dmDevice[255];
    bool cleanupDm = false;
    int fd;
    unsigned int nr_sec = 0;

    if ((fd = open(loopDevice, O_RDWR)) < 0) {
        SLOGE("Failed to open loopdevice (%s)", strerror(errno));
        Loop::destroyByDevice(loopDevice);
        return -1;
    }

    if (ioctl(fd, BLKGETSIZE, &nr_sec)) {
        SLOGE("Failed to get loop size (%s)", strerror(errno));
        Loop::destroyByDevice(loopDevice);
        close(fd);
        return -1;
    }

    close(fd);

    if (strcmp(key, "none")) {
        if (Devmapper::lookupActive(idHash, dmDevice, sizeof(dmDevice))) {
            if (Devmapper::create(idHash, loopDevice, key, nr_sec,
                                  dmDevice, sizeof(dmDevice))) {
                SLOGE("ASEC device mapping failed (%s)", strerror(errno));
                Loop::destroyByDevice(loopDevice);
                return -1;
            }
            if (mDebug) {
                SLOGD("New devmapper instance created at %s", dmDevice);
            }
        } else {
            if (mDebug) {
                SLOGD("Found active devmapper for %s at %s", img, dmDevice);
            }
        }
        cleanupDm = true;
    } else {
        strcpy(dmDevice, loopDevice);
    }

    if (mkdir(mountPoint, 0755)) {
        if (errno != EEXIST) {
            SLOGE("Mountpoint creation failed (%s)", strerror(errno));
            if (cleanupDm) {
                Devmapper::destroy(idHash);
            }
            Loop::destroyByDevice(loopDevice);
            return -1;
        }
    }

    if (Fat::doMount(dmDevice, mountPoint, false, false, true, ownerUid, 0,
                     0227, false)) {
        SLOGE("Image mount failed (%s)", strerror(errno));
        if (cleanupDm) {
            Devmapper::destroy(idHash);
        }
        Loop::destroyByDevice(loopDevice);
        return -1;
    }

    mActiveContainers->push_back(new ContainerData(strdup(img), OBB));
    if (mDebug) {
        SLOGD("Image %s mounted", img);
    }
    return 0;
}

int VolumeManager::mountVolume(const char *label) {
    Volume *v = lookupVolume(label);

    if (!v) {
        errno = ENOENT;
        return -1;
    }

    return v->mountVol();
}

int VolumeManager::listMountedObbs(SocketClient* cli) {
    char device[256];
    char mount_path[256];
    char rest[256];
    FILE *fp;
    char line[1024];

    if (!(fp = fopen("/proc/mounts", "r"))) {
        SLOGE("Error opening /proc/mounts (%s)", strerror(errno));
        return -1;
    }

    // Create a string to compare against that has a trailing slash
    int loopDirLen = sizeof(Volume::LOOPDIR);
    char loopDir[loopDirLen + 2];
    strcpy(loopDir, Volume::LOOPDIR);
    loopDir[loopDirLen++] = '/';
    loopDir[loopDirLen] = '\0';

    while(fgets(line, sizeof(line), fp)) {
        line[strlen(line)-1] = '\0';

        /*
         * Should look like:
         * /dev/block/loop0 /mnt/obb/fc99df1323fd36424f864dcb76b76d65 ...
         */
        sscanf(line, "%255s %255s %255s\n", device, mount_path, rest);

        if (!strncmp(mount_path, loopDir, loopDirLen)) {
            int fd = open(device, O_RDONLY);
            if (fd >= 0) {
                struct loop_info64 li;
                if (ioctl(fd, LOOP_GET_STATUS64, &li) >= 0) {
                    cli->sendMsg(ResponseCode::AsecListResult,
                            (const char*) li.lo_file_name, false);
                }
                close(fd);
            }
        }
    }

    fclose(fp);
    return 0;
}

int VolumeManager::shareEnabled(const char *label, const char *method, bool *enabled) {
    Volume *v = lookupVolume(label);

    if (!v) {
        errno = ENOENT;
        return -1;
    }

    if (strcmp(method, "ums")) {
        errno = ENOSYS;
        return -1;
    }

    if (v->getState() != Volume::State_Shared) {
        *enabled = false;
    } else {
        *enabled = true;
    }
    return 0;
}

int VolumeManager::shareVolume(const char *label, const char *method) {
    Volume *v = lookupVolume(label);

    if (!v) {
        errno = ENOENT;
        return -1;
    }

    /*
     * Eventually, we'll want to support additional share back-ends,
     * some of which may work while the media is mounted. For now,
     * we just support UMS
     */
    if (strcmp(method, "ums")) {
        errno = ENOSYS;
        return -1;
    }

    if (v->getState() == Volume::State_NoMedia) {
        errno = ENODEV;
        return -1;
    }

    if (v->getState() != Volume::State_Idle) {
        // You need to unmount manually befoe sharing
        errno = EBUSY;
        return -1;
    }

    if (mVolManagerDisabled) {
        errno = EBUSY;
        return -1;
    }

    dev_t d = v->getShareDevice();
    if ((MAJOR(d) == 0) && (MINOR(d) == 0)) {
        // This volume does not support raw disk access
        errno = EINVAL;
        return -1;
    }

    int fd;
    char nodepath[255];
    snprintf(nodepath,
             sizeof(nodepath), "/dev/block/vold/%d:%d",
             MAJOR(d), MINOR(d));

    if ((fd = open(MASS_STORAGE_FILE_PATH, O_WRONLY)) < 0) {
        SLOGE("Unable to open ums lunfile (%s)", strerror(errno));
        return -1;
    }

    if (write(fd, nodepath, strlen(nodepath)) < 0) {
        SLOGE("Unable to write to ums lunfile (%s)", strerror(errno));
        close(fd);
        return -1;
    }

    close(fd);
    v->handleVolumeShared();
    if (mUmsSharingCount++ == 0) {
        FILE* fp;
        mSavedDirtyRatio = -1; // in case we fail
        if ((fp = fopen("/proc/sys/vm/dirty_ratio", "r+"))) {
            char line[16];
            if (fgets(line, sizeof(line), fp) && sscanf(line, "%d", &mSavedDirtyRatio)) {
                fprintf(fp, "%d\n", mUmsDirtyRatio);
            } else {
                SLOGE("Failed to read dirty_ratio (%s)", strerror(errno));
            }
            fclose(fp);
        } else {
            SLOGE("Failed to open /proc/sys/vm/dirty_ratio (%s)", strerror(errno));
        }
    }
    return 0;
}

int VolumeManager::unshareVolume(const char *label, const char *method) {
    Volume *v = lookupVolume(label);

    if (!v) {
        errno = ENOENT;
        return -1;
    }

    if (strcmp(method, "ums")) {
        errno = ENOSYS;
        return -1;
    }

    if (v->getState() != Volume::State_Shared) {
        errno = EINVAL;
        return -1;
    }

    int fd;
    if ((fd = open(MASS_STORAGE_FILE_PATH, O_WRONLY)) < 0) {
        SLOGE("Unable to open ums lunfile (%s)", strerror(errno));
        return -1;
    }

    char ch = 0;
    if (write(fd, &ch, 1) < 0) {
        SLOGE("Unable to write to ums lunfile (%s)", strerror(errno));
        close(fd);
        return -1;
    }

    close(fd);
    v->handleVolumeUnshared();
    if (--mUmsSharingCount == 0 && mSavedDirtyRatio != -1) {
        FILE* fp;
        if ((fp = fopen("/proc/sys/vm/dirty_ratio", "r+"))) {
            fprintf(fp, "%d\n", mSavedDirtyRatio);
            fclose(fp);
        } else {
            SLOGE("Failed to open /proc/sys/vm/dirty_ratio (%s)", strerror(errno));
        }
        mSavedDirtyRatio = -1;
    }
    return 0;
}

extern "C" int vold_disableVol(const char *label) {
    VolumeManager *vm = VolumeManager::Instance();
    vm->disableVolumeManager();
    vm->unshareVolume(label, "ums");
    return vm->unmountVolume(label, true, false);
}

extern "C" int vold_getNumDirectVolumes(void) {
    VolumeManager *vm = VolumeManager::Instance();
    return vm->getNumDirectVolumes();
}

int VolumeManager::getNumDirectVolumes(void) {
    VolumeCollection::iterator i;
    int n=0;

    for (i = mVolumes->begin(); i != mVolumes->end(); ++i) {
        if ((*i)->getShareDevice() != (dev_t)0) {
            n++;
        }
    }
    return n;
}

extern "C" int vold_getDirectVolumeList(struct volume_info *vol_list) {
    VolumeManager *vm = VolumeManager::Instance();
    return vm->getDirectVolumeList(vol_list);
}

int VolumeManager::getDirectVolumeList(struct volume_info *vol_list) {
    VolumeCollection::iterator i;
    int n=0;
    dev_t d;

    for (i = mVolumes->begin(); i != mVolumes->end(); ++i) {
        if ((d=(*i)->getShareDevice()) != (dev_t)0) {
            (*i)->getVolInfo(&vol_list[n]);
            snprintf(vol_list[n].blk_dev, sizeof(vol_list[n].blk_dev),
                     "/dev/block/vold/%d:%d",MAJOR(d), MINOR(d));
            n++;
        }
    }

    return 0;
}

int VolumeManager::unmountVolume(const char *label, bool force, bool revert) {
    Volume *v = lookupVolume(label);

    if (!v) {
        errno = ENOENT;
        return -1;
    }

    if (v->getState() == Volume::State_NoMedia) {
        errno = ENODEV;
        return -1;
    }

    if (v->getState() != Volume::State_Mounted) {
        SLOGW("Attempt to unmount volume which isn't mounted (%d)\n",
             v->getState());
        errno = EBUSY;
        return UNMOUNT_NOT_MOUNTED_ERR;
    }
	const char *externalStorage = getenv("EXTERNAL_STORAGE");
	bool primaryStorage = externalStorage && !strcmp(v->getMountpoint(),externalStorage);

	if(primaryStorage) {
    	cleanupAsec(v, force);
	}

    return v->unmountVol(force, revert);

}

/*
 * Looks up a volume by it's label or mount-point
 */
Volume *VolumeManager::lookupVolume(const char *label) {
    VolumeCollection::iterator i;

    for (i = mVolumes->begin(); i != mVolumes->end(); ++i) {
        if (label[0] == '/') {
            if (!strcmp(label, (*i)->getMountpoint()))
                return (*i);
        } else {
            if (!strcmp(label, (*i)->getLabel()))
                return (*i);
        }
    }
    return NULL;
}

bool VolumeManager::isMountpointMounted(const char *mp)
{
    char device[256];
    char mount_path[256];
    char rest[256];
    FILE *fp;
    char line[1024];

    if (!(fp = fopen("/proc/mounts", "r"))) {
        SLOGE("Error opening /proc/mounts (%s)", strerror(errno));
        return false;
    }

    while(fgets(line, sizeof(line), fp)) {
        line[strlen(line)-1] = '\0';
        sscanf(line, "%255s %255s %255s\n", device, mount_path, rest);
        if (!strcmp(mount_path, mp)) {
            fclose(fp);
            return true;
        }
    }

    fclose(fp);
    return false;
}

int VolumeManager::cleanupAsec(Volume *v, bool force) {
    while(mActiveContainers->size()) {
        AsecIdCollection::iterator it = mActiveContainers->begin();
        ContainerData* cd = *it;
        SLOGI("Unmounting ASEC %s (dependant on %s)", cd->id, v->getMountpoint());
        if (cd->type == ASEC) {
            /* Try 100 times, sure to wait for systemserver to close all the "*.asec" file */
            unmount_asec_reties = 50;//Modify by weijb to fix trigger Watchdog
            if (unmountAsec(cd->id, force)) {
                unmount_asec_reties = UNMOUNT_RETRIES;
                SLOGE("Failed to unmount ASEC %s (%s)", cd->id, strerror(errno));
                return -1;
            }
            unmount_asec_reties = UNMOUNT_RETRIES;
        } else if (cd->type == OBB) {
            if (unmountObb(cd->id, force)) {
                SLOGE("Failed to unmount OBB %s (%s)", cd->id, strerror(errno));
                return -1;
            }
        } else {
            SLOGE("Unknown container type %d!", cd->type);
            return -1;
        }
    }
    return 0;
}

