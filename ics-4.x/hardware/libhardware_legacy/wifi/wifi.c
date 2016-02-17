/*
 * Copyright 2008, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <stdlib.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include <dirent.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <sys/types.h>
#include <poll.h>
#include <ctype.h>

#include "hardware_legacy/wifi.h"
#include "libwpa_client/wpa_ctrl.h"
#include "wifi_device_id.h"

#define LOG_TAG "WifiHW"
#include "cutils/log.h"
#include "cutils/memory.h"
#include "cutils/misc.h"
#include "cutils/properties.h"
#include "private/android_filesystem_config.h"
#ifdef HAVE_LIBC_SYSTEM_PROPERTIES
#define _REALLY_INCLUDE_SYS__SYSTEM_PROPERTIES_H_
#include <sys/_system_properties.h>
#endif
//add for wifi0_check
#include <linux/if.h>
#include <sys/types.h>
#include <sys/socket.h>

static struct wpa_ctrl *ctrl_conn;
static struct wpa_ctrl *monitor_conn;

static struct wpa_ctrl *p2p_ctrl_conn;
static struct wpa_ctrl *p2p_monitor_conn;

/* socket pair used to exit from a blocking read */
static int exit_sockets[2] = { -1, -1 };
static int p2p_exit_sockets[2] = { -1, -1 };


extern int do_dhcp();
extern int ifc_init();
extern int ifc_up(const char *name);
extern int ifc_down(const char *name);
extern void ifc_close();
extern char *dhcp_lasterror();
extern void get_dhcp_info();
extern int init_module(void *, unsigned long, const char *);
extern int delete_module(const char *, unsigned int);

static char iface[PROPERTY_VALUE_MAX];
// TODO: use new ANDROID_SOCKET mechanism, once support for multiple
// sockets is in

#ifndef WIFI_DRIVER_MODULE_ARG
#define WIFI_DRIVER_MODULE_ARG          ""
#endif
#ifndef WIFI_FIRMWARE_LOADER
#define WIFI_FIRMWARE_LOADER        ""
#endif
#define WIFI_TEST_INTERFACE     "wlan0"
#define WIFI_TEST_INTERFACE_P2P "wlan1"
#define WIFI_TEST_INTERFACE_AP  "wlan0"

#ifndef WIFI_DRIVER_FW_PATH_STA
#define WIFI_DRIVER_FW_PATH_STA     NULL
#endif
#ifndef WIFI_DRIVER_FW_PATH_AP
#define WIFI_DRIVER_FW_PATH_AP      NULL
#endif
#ifndef WIFI_DRIVER_FW_PATH_P2P
#define WIFI_DRIVER_FW_PATH_P2P     NULL
#endif

#ifndef WIFI_DRIVER_FW_PATH_PARAM
#define WIFI_DRIVER_FW_PATH_PARAM   "/sys/module/wlan/parameters/fwpath"
#endif

#define WIFI_DRIVER_LOADER_DELAY    1000000

#define WIFI_DRIVER_MODULE  1 



static const char IFACE_DIR[]           = "/data/misc/wifi/wpa_supplicant";
#ifdef WIFI_DRIVER_MODULE
static  char DRIVER_MODULE_NAME[40]  = "";
static  char DRIVER_MODULE_TAG[40]   = "";
static  char DRIVER_MODULE_PATH[40]  = "";
static  char DRIVER_MODULE_ARG[40]   = "";
#endif
static const char FIRMWARE_LOADER[]     = WIFI_FIRMWARE_LOADER;
static const char DRIVER_PROP_NAME[]    = "wlan.driver.status";
static const char CUR_MODULE_PROP_NAME[]    = "wlan.curmodule.name";
static const char DEBUG_TEST[]                = "wlan.test";

static  char SUPPLICANT_NAME[40]     = "";
static char P2P_SUPPLICANT_NAME[40] = "";
static char SUPP_PROP_NAME[40]      = "";
static char P2P_SUPP_PROP_NAME[40]  = "";
static const char SUPP_CONFIG_TEMPLATE[]= "/system/etc/wifi/wpa_supplicant.conf";
static const char SUPP_CONFIG_FILE[]    = "/data/misc/wifi/wpa_supplicant.conf";
static const char P2P_CONFIG_FILE[]     = "/data/misc/wifi/p2p_supplicant.conf";
static const char CONTROL_IFACE_PATH[]  = "/data/misc/wifi/wpa_supplicant";
static const char MODULE_FILE[]         = "/proc/modules";

static       char WIFI_SET[]             = "wifi_set";

static const char SUPP_ENTROPY_FILE[]   = WIFI_ENTROPY_FILE;
static unsigned char dummy_key[21] = { 0x02, 0x11, 0xbe, 0x33, 0x43, 0x35,
                                       0x68, 0x47, 0x84, 0x99, 0xa9, 0x2b,
                                       0x1c, 0xd3, 0xee, 0xff, 0xf1, 0xe2,
                                       0xf3, 0xf4, 0xf5 };

//for pid/vid recognize dongle
#define No_Dongle                              -1
#define Ralink_5570                             0
#define REALTEK_CU                              1
#define REALTEK_SU                              2
#define ATHEROS_K2                              3
#define ATHEROS_UB134                           4
#define ATHEROS_ATH6K                           5
#define REALTEK_DU								6
#define WIFI_DEV_NUM 6
#define DEFAULT_DEV				REALTEK_CU//for mt36k E5690,default wifi device is rtl8192cu

#define   STA_MODE                           0
#define   AP_MODE                            1

static int wifi_rmmod (int dongle_type);
    
static int wifi_mode = STA_MODE;

static int cur_module = No_Dongle;

static char* supp_prefix[] = {"ra", "rt", "rt", "k2", "ub","6k", "rt"};
static char* module_path[] = {"/data/misc/wifi/rtnet5572sta.ko", "/data/misc/wifi/8192cu.ko", "/data/misc/wifi/8712su.ko", "/data/misc/wifi/ath_usbdrv.ko", "", "/data/misc/wifi/ath6kl_usb.ko", ""};
static char* module_name[] = {"rtnet5572sta", "8192cu", "8712su", "ath_usbdrv", "", "ath6kl_usb", "8192du" };
static char* module_arg[]  = {"", "", "", "", "", "", "", "", "" };

static int driver_is_load = 0;
static struct device_node* pcurr_device = NULL;
 //list for keep device_id
static struct device_node* phead = NULL;

struct device_node
{
    struct device_node *  pnext;
    char  device_id[20];
};
//******************************************************************//
//add to find wifi devices......
static int badname(const char *name)
{
	if(*name == '.' || isalpha(*name)){
		
		return 1;
		
	}
    while(*name) {
		  printf( " enter   %s  %d  dev_name %s \n",__FUNCTION__,__LINE__,name);
        if(':'==(*name++))
		{
		  printf( " enter   %s  %d  dev_name  \n",__FUNCTION__,__LINE__);
		  return 1;
		}
		else
		{
		  printf( " enter   %s  %d  dev_name \n ",__FUNCTION__,__LINE__);
		}
    }
    return 0;
}

int is_wifi_device(int vid,int pid)
{
		char dev_info[20],str_cur_mode[5];
		int cur_module;

//	  LOGD( " enter   %s  %d  dev_name vid  %x  pid %x",__FUNCTION__,__LINE__,vid,pid);
		sprintf(dev_info,"%04x/%04x",vid,pid);
//		LOGD("dev_info is: [%s]",dev_info);

		cur_module = wifi_trans_pid_to_module(dev_info);

	
		LOGD("%s worked %d, cur_module =[%d]\n", __FUNCTION__, __LINE__, cur_module);
	  if(cur_module >= 0)
	  {
			sprintf(str_cur_mode,"%d",cur_module);
			int ret = property_set(CUR_MODULE_PROP_NAME,str_cur_mode);
			
			LOGD("%s worked %d, ret =[%d]\n", __FUNCTION__, __LINE__, ret);
			return 1;
		}

		return 0;
}
int find_existing_devices()
{
    char  devname[60],detail[8],busdir_path[32];
    DIR *busdir , *devdir ;
    struct dirent *de;
	int num,fd,pid,vid;
    int done = 0;
	num=0;

    busdir = opendir("/sys/bus/usb/devices");
	snprintf(busdir_path, sizeof busdir_path, "%s","/sys/bus/usb/devices");
    if(busdir == 0) return 1;

    while ((de = readdir(busdir)) != 0 && !done) {
		
//		LOGE( " enter	%s	%d	%s/%s	\n",__FUNCTION__,__LINE__,busdir_path, de->d_name);
       if(badname(de->d_name)) continue;

		//continue;
		
		snprintf(devname, sizeof devname, "%s/%s/%s", busdir_path, de->d_name,"idProduct");
		

		memset(detail,0,8);
//		LOGE( " enter	%d	 devname %s	\n",__LINE__, devname);
        fd = open(devname, O_RDONLY);
		if(fd < 0){
				LOGE( " enter %d	 devname %s \n",__LINE__, strerror(errno));
			}
		read(fd,detail,31);
		if(fd < 0){
				LOGE( " enter %d	 devname %s \n",__LINE__, strerror(errno));
			}
		pid=atoi(detail);
		sscanf(detail,"%x",&pid);

		close(fd);

		memset(detail,0,8);
		snprintf(devname, sizeof devname, "%s/%s/%s", busdir_path, de->d_name,"idVendor");

		fd = open(devname, O_RDONLY);
		read(fd,detail,31);

	
		close(fd);	
		vid=atoi(detail);
		sscanf(detail,"%x",&vid);

		done = is_wifi_device(vid,pid);
		if(1 == done){
				num++;
		}		
		done=0;
		
//		LOGE( " enter %s	%d	%s	is_wifi_device %d\n",__FUNCTION__,__LINE__, detail,num);
    } //end of busdir while
    closedir(busdir);

    return num;
}
int wifi_get_deviceNum()
{
	int num;

	LOGD("%s worked %d", __FUNCTION__, __LINE__);

	num = find_existing_devices();

//	LOGE("find_existing_devices  exit!!***************%d",num);

	return num;
}

//add to find wifi devices......end
//******************************************************************//

    
int wifi_add_in_list(const char* device_id)
{
    int i ;
    struct device_node *  ptmp = NULL;
    struct device_node *  pthis = NULL;
    //add 0 if the vid is begin with 0 984/5555/111 to 0987/5555/111 
    char *p = strstr(device_id , "/");
    char device_tmp[20] = {0};
    if((p - device_id) == 3)
        sprintf(device_tmp , "0%s",device_id);
    else
        sprintf(device_tmp,"%s",device_id);
    
    if(phead == NULL)
    {
        phead = (struct device_node *)malloc(sizeof(struct device_node));
        if(phead == NULL)
            return -1;
        phead->pnext = NULL;
        strncpy(phead->device_id,device_tmp,sizeof(device_tmp));
        LOGD("wifi_add_in_list head device  %s in--wtf",phead->device_id);
        return 0;
    }
    ptmp = phead->pnext;
    pthis = phead;
    for( i = 0; i < 200 && ptmp != NULL; i++)
    {
        pthis = ptmp;
        ptmp = ptmp->pnext;
    }

    if(ptmp == NULL)
    {
        pthis->pnext = (struct device_node *)malloc(sizeof(struct device_node));
        if(pthis->pnext == NULL)
            return -1;
        ptmp = pthis->pnext;
        ptmp->pnext = NULL;
        strncpy(ptmp->device_id,device_tmp,sizeof(device_tmp));
        LOGD("wifi_add_in_list device  %s in--wtf",ptmp->device_id);
        return 0;
    }
    
    LOGD("wifi_add_in_list ten device already %s in--wtf",device_tmp);
    return (-1);
}

int wifi_delete_from_list(const char* device_id)  
{
    LOGD("wifi_delete_from_list device_id[%s]\n", device_id);
    int i ;
    struct device_node *  ptmp = NULL;
    struct device_node *  pthis = NULL;
    
    //add 0 if the vid is begin with 0 984/5555/111 to 0987/5555/111 
    char *p = strstr(device_id , "/");
    
    char device_tmp[20] = {0};
    if((p - device_id) == 3)
        sprintf(device_tmp , "0%s",device_id);
    else
        sprintf(device_tmp , "%s",device_id);
    
    if(phead == NULL)
    {
        LOGD("wifi_delete_from_list no device in--wtf");
        return 0;
    }
    if(strcmp(phead->device_id,device_tmp) == 0)
    {
        LOGD("wifi_delete_from_list  device head  %s in--wtf",phead->device_id);
        ptmp = phead;
        phead = phead->pnext;
        free(ptmp);
        return 0;
    }
    ptmp = phead->pnext;
    pthis = phead;
    for( i = 0; i < 200 && ptmp != NULL; i++)
    {
        if(strcmp(ptmp->device_id,device_tmp) == 0)
        {
            LOGD("wifi_delete_from_list  device  %s in--wtf",ptmp->device_id);
            pthis->pnext = ptmp->pnext;
            free(ptmp);
            return 0;
        }
        pthis = ptmp;
        ptmp = ptmp->pnext;
    }

    LOGD("wifi_delete_from_list delete nothing %sin--wtf",device_tmp);
    return -1;
} 

int wifi0_check()
{
	int s = 0;
	int ret = -1;
	struct ifreq ifr;
	int i;
    /* wait to open in multi dongle support
    if(!driver_is_load)
    {
        LOGD("wifi0_check driver is not load");
        return -1;        
    }*/
	memset(&ifr, 0, sizeof(struct ifreq));

	LOGD ("wifi0_check \n");
	
	memset(&ifr, 0, sizeof(struct ifreq));
	strncpy(ifr.ifr_name, WIFI_TEST_INTERFACE,strlen(WIFI_TEST_INTERFACE));

	s = socket(AF_INET, SOCK_DGRAM, 0);
	if(s < 0) 
	{
		LOGE("Fail to create socket!");
		return -1;
	}
	ret = ioctl(s, SIOCGIFFLAGS, &ifr);
	close(s);

	if (ret == 0)
	{		
		return cur_module;
	}	
    LOGE("wifi0_check no interface!");
	return -1;
}

int wifi_trans_pid_to_module(const char *info)
{
    LOGD("wifi_trans_pid_to_module %s",info);
    int i; 
    char * pid_temp = NULL;
        
    for( i = 0; strcmp(rt2500usb_device_table[i],"0") != 0; i++)
    {
        if(strncmp(rt2500usb_device_table[i], info, strlen(rt2500usb_device_table[i])) == 0)
        {
            LOGD("wifi_trans_pid_to_module rt2500usb_device_table");
            return Ralink_5570;
        }
    }
    
    for( i = 0; strcmp(rt2800usb_device_table[i],"0") != 0; i++)
    {
        if(strncmp(rt2800usb_device_table[i],info,strlen(rt2800usb_device_table[i])) == 0)
        {
            LOGD("wifi_trans_pid_to_module rt2800usb_device_table");
            return Ralink_5570;
        }
    }
    
    for( i = 0; strcmp(rt73usb_device_table[i],"0") != 0; i++)
    {
        if(strncmp(rt73usb_device_table[i],info,strlen(rt73usb_device_table[i])) == 0)
        {
            LOGD("wifi_trans_pid_to_module rt73usb_device_table");
            return Ralink_5570;
        }
    }
    for( i = 0; strcmp(realtekcu_devive_table[i], "0") != 0; i++)
    {
        if(strncmp(realtekcu_devive_table[i],info,strlen(realtekcu_devive_table[i])) == 0)
        {
            LOGD("wifi_trans_pid_to_module REALTEK_CU");
            return REALTEK_CU;
        }
    }
        
    for( i = 0; strcmp(realteksu_device_table[i], "0") != 0; i++)
    {
        if(strncmp(realteksu_device_table[i],info,strlen(realteksu_device_table[i])) == 0)
        {
            LOGD("wifi_trans_pid_to_module REALTEK_SU");
            return REALTEK_SU;
        }
    }
    for (i = 0;strcmp(ath6k_usb_device_table[i],"0") != 0; i++)
    {
        if(strncmp(ath6k_usb_device_table[i],info,strlen(ath6k_usb_device_table[i])) == 0)
        {   
            LOGD ("ATHEROS_ATH6K device \n");
            return ATHEROS_ATH6K;
        }
    }

    for (i = 0;strcmp(athk2_usb_device_table[i],"0") != 0; i++)
    {
        if(strncmp(athk2_usb_device_table[i],info,strlen(athk2_usb_device_table[i])) == 0)
        {   
            LOGD ("ATHEROS_K2 device \n");
            return ATHEROS_K2;
        }
    }

    LOGD ("No Dongle");
    return No_Dongle;
}

void wifi_set_envir_vali(int dongle_type)
{       
    memset(SUPPLICANT_NAME, 0, sizeof(SUPPLICANT_NAME));
    strcpy(SUPPLICANT_NAME, "wpa_supplicant");
    strcat(SUPPLICANT_NAME, supp_prefix[dongle_type]);  

    memset(P2P_SUPPLICANT_NAME, 0, sizeof(P2P_SUPPLICANT_NAME));
    strcpy(P2P_SUPPLICANT_NAME, "p2p_supplicant");
    strcat(P2P_SUPPLICANT_NAME, supp_prefix[dongle_type]);  

    memset(SUPP_PROP_NAME, 0, sizeof(SUPP_PROP_NAME));
    strcpy(SUPP_PROP_NAME, "init.svc.wpa_supplicant");
    strcat(SUPP_PROP_NAME, supp_prefix[dongle_type]);  

    
    memset(P2P_SUPP_PROP_NAME, 0, sizeof(P2P_SUPP_PROP_NAME));
    strcpy(P2P_SUPP_PROP_NAME, "init.svc.p2p_supplicant");
    strcat(P2P_SUPP_PROP_NAME, supp_prefix[dongle_type]);  

    memset(DRIVER_MODULE_PATH, 0, sizeof(DRIVER_MODULE_PATH));
    strcpy(DRIVER_MODULE_PATH, module_path[dongle_type]);

    memset(DRIVER_MODULE_NAME, 0, sizeof(DRIVER_MODULE_NAME));
    strcpy(DRIVER_MODULE_NAME, module_name[dongle_type]);

    memset(DRIVER_MODULE_ARG, 0, sizeof(DRIVER_MODULE_ARG));
    strcpy(DRIVER_MODULE_ARG, module_arg[dongle_type]);

    
    memset(DRIVER_MODULE_TAG, 0, sizeof(DRIVER_MODULE_TAG));
    strcpy(DRIVER_MODULE_TAG, module_name[dongle_type]);
    LOGD("wifi_set_envir_vali:\n");
    LOGD("SUPPLICANT_NAME[%s]P2P_SUPPLICANT_NAME[%s]SUPP_PROP_NAME[%s]DRIVER_MODULE_PATH \
        [%s]DRIVER_MODULE_NAME[%s]DRIVER_MODULE_ARG[%s]DRIVER_MODULE_TAG[%s]", SUPPLICANT_NAME, P2P_SUPPLICANT_NAME\
        , SUPP_PROP_NAME, DRIVER_MODULE_PATH ,DRIVER_MODULE_NAME, DRIVER_MODULE_ARG,DRIVER_MODULE_TAG);
    return;
}

int wifi_set_dongle_inside(const char *info)
{
    LOGD("wifi_set_dongle_inside set %s",info);

	
	LOGD("%s worked %d\n", __FUNCTION__, __LINE__);
    wifi_add_in_list(info);  

   //zengyongying: to save cur module to prop
   char str_cur_mode[20];
		struct device_node *  ptmp = NULL;
    ptmp = phead;
 
	 		while(ptmp != NULL)
	 		{
	    	cur_module = wifi_trans_pid_to_module(ptmp->device_id);
	    	
	    	if(cur_module >= 0)
	    	{
				    sprintf(str_cur_mode,"%d",cur_module);
				    property_set(CUR_MODULE_PROP_NAME,str_cur_mode);
				    return 0;
		  	}
		  	ptmp = ptmp->pnext;
	  	}
   

    return 0;
}

int wifi_check_is_dongle_in()
{
    struct device_node *  ptmp = NULL;
    char str_cur_mode[20];
    
    //if(driver_is_load)	//zengyongying: check driver load status in wifi_load_driver()
   /* if(is_wifi_driver_loaded())
    {
        LOGD("wifi_set_dongle_inside dongle already in");
        return 0;
    }*/
    
	LOGD("%s worked %d", __FUNCTION__, __LINE__);
    if(wifi_get_deviceNum() == 0)
    {
        LOGD("there is no wifi device inserted!");
        return -1;
    	}
    
    ptmp = phead;
    
   // while(ptmp != NULL)
    {
        //cur_module = wifi_trans_pid_to_module(ptmp->device_id);
    		property_get(CUR_MODULE_PROP_NAME,str_cur_mode,"99");
    		
    		        	
        cur_module = atoi(str_cur_mode);//wifi_get_cur_module_frome_str(str_cur_mode);//REALTEK_CU;
       	if(cur_module > WIFI_DEV_NUM)
    		{
    			return -1;	
    		}
    		
        if (cur_module >= 0)
        {
            wifi_set_envir_vali(cur_module);
            pcurr_device = ptmp;
            return wifi_load_driver();
        }
        ptmp = ptmp->pnext;
    }
    
    return -1;
}

int wifi_check_is_dongle_out()
{
    struct device_node *  ptmp = NULL;
    if(driver_is_load && pcurr_device == NULL) 
    {
        LOGD("wifi_set_dongle_inside dongle already in");
        return wifi_unload_driver();
    }
    
    return -1;
}

int wifi_set_dongle_remove(const char *info)
{

    char *p = strstr(info , "/");
    char device_tmp[20] = {0};
    if((p - info) == 3)
        sprintf(device_tmp , "0%s",info);
    else
        sprintf(device_tmp,"%s",info);
    
   LOGD("wifi_set_dongle_remove  info[%s] ",info );
    
    if(driver_is_load && (strcmp(pcurr_device->device_id, device_tmp) == 0))
    {
        pcurr_device = NULL;  // if NULL for unload driver
    }
    wifi_delete_from_list(info);
    
   	//zengyongying: to save cur module to prop
   	if(phead != NULL)
   	{
	   	char str_cur_mode[20];
			struct device_node *  ptmp = NULL;
	    ptmp = phead;
	 		
	 		while(ptmp != NULL)
	 		{
	    	cur_module = wifi_trans_pid_to_module(ptmp->device_id);
	    	
	    	if(cur_module >= 0)
	    	{
				    sprintf(str_cur_mode,"%d",cur_module);
				    property_set(CUR_MODULE_PROP_NAME,str_cur_mode);
				    return 0;
		  	}
		  	ptmp = ptmp->pnext;
	  	}
   	}
   	else//phead is NULL, no device inserted
		{
   		property_set(CUR_MODULE_PROP_NAME,"99");	
   	} 
   		  
    return 0;
}
#include <net/if.h>
typedef struct android_wifi_priv_cmd {
    char *buf;
    int used_len;
    int total_len;
} android_wifi_priv_cmd;

int rtw_issue_driver_cmd_fd(int sockfd, const char *ifname, char *cmd, char *buf, size_t buf_len)
{
    int ret;
    struct ifreq ifr;
    android_wifi_priv_cmd priv_cmd;
    
    memset(&ifr, 0, sizeof(ifr));
    memset(&priv_cmd, 0, sizeof(priv_cmd));
    strncpy(ifr.ifr_name, ifname, IFNAMSIZ);

    memcpy(buf, cmd, strlen(cmd) + 1);

    priv_cmd.buf = buf;
    priv_cmd.used_len = buf_len;
    priv_cmd.total_len = buf_len;
    ifr.ifr_data = &priv_cmd;

    if ((ret = ioctl(sockfd, SIOCDEVPRIVATE + 1, &ifr)) < 0) {
        LOGE("%s: failed to issue private command: error:%d %s", __func__, errno, strerror(errno));
    }else {
        ret = 0;
        if ((strcasecmp(cmd, "LINKSPEED") == 0) ||
            (strcasecmp(cmd, "RSSI") == 0) ||
            (strcasecmp(cmd, "GETBAND") == 0) ||
            (strcasecmp(cmd, "P2P_GET_NOA") == 0))
            ret = strlen(buf);

        //LOGE("%s %s len = %d, %d", __func__, buf, ret, strlen(buf));
    }
    
    return ret;
}

int rtw_issue_driver_cmd(const char *ifname, char *cmd, char *buf, size_t buf_len)
{
    int sockfd;
    int ret;

#if 0
    if (ifc_init() < 0)
        return -1;  
    if (ifc_up(ifname)) {
        LOGD("failed to bring up interface %s: %s\n", ifname, strerror(errno));
        return -1;
    }
#endif
    
    sockfd = socket(PF_INET, SOCK_DGRAM, 0);
    if (sockfd< 0) {
        LOGE("%s socket[PF_INET,SOCK_DGRAM] error:%d %s", __FUNCTION__, errno, strerror(errno));
        ret = -1;
        goto bad;
    }

    ret = rtw_issue_driver_cmd_fd(
        sockfd
        , ifname
        , cmd
        , buf
        , buf_len
    );
    
    close(sockfd);
bad:
    return ret;
}



static int insmod(const char *filename, const char *args)
{
    void *module;
    unsigned int size;
    int ret;

    module = load_file(filename, &size);
    if (!module)
        return -1;
    LOGD ("insmod filename[%s]", filename);
    ret = init_module(module, size, args);

    free(module);

    return ret;
}

static int rmmod(const char *modname)
{
    int ret = -1;
    int maxtry = 10;

    while (maxtry-- > 0) {
        ret = delete_module(modname, O_NONBLOCK | O_EXCL);
        if (ret < 0 && errno == EAGAIN)
            usleep(500000);
        else
            break;
    }

    if (ret != 0)
        LOGD("Unable to unload driver module \"%s\": %s\n",
             modname, strerror(errno));
    return ret;
}

static int insmod_driver(const char *driver_ko, const char *args)
{
	char file_path[256] = {0};
	snprintf (file_path, sizeof (file_path), "/data/misc/wifi/%s", driver_ko);
	if (insmod(file_path,args) != 0)
	{
		LOGD ("insmod %s failed", file_path);
		{
			memset (file_path, 0, sizeof (file_path));
			snprintf (file_path, sizeof (file_path), "/system/lib/modules/%s", driver_ko);
			if (insmod (file_path, args) != 0 )
			{
				 LOGD ("insmod %s failed", file_path);
			}
  		}
	}
	return 0;
}

static int wifi_insmod (int dongle_type)
{
    LOGD ("wifi_insmod dongle_type = %d wifi type = %d\n", dongle_type,wifi_mode);
    if (dongle_type == Ralink_5570)
    {
        if(wifi_mode == STA_MODE)
        {
            insmod_driver ("rtutil5572sta.ko", ""); 
            insmod_driver ("rt5572sta.ko", ""); 
            insmod_driver ("rtnet5572sta.ko", "");
        }
        else if(wifi_mode == AP_MODE)
        {
            insmod_driver ("rtutil5572ap.ko", ""); 
            insmod_driver ("rt5572ap.ko", ""); 
            insmod_driver ("rtnet5572ap.ko", "");
        }
        else
            return -1;
        LOGD ("execute the wpa_supplicantat_pre shell \n" );
        property_set("ctl.start", "wifi_set:ra_pre");//  it should be in the wifi_set.sh
    }
    else if (dongle_type == REALTEK_CU)
    {
		wifi_rmmod(REALTEK_SU);
		wifi_rmmod(REALTEK_DU);
		wifi_rmmod(ATHEROS_K2);
        insmod_driver ("rfkill.ko", "");
        insmod_driver ("cfg80211.ko", ""); 
        insmod_driver ("8192cu.ko", ""); 
    }
    	else if  (REALTEK_SU == dongle_type)
	{
		wifi_rmmod(REALTEK_CU);
		wifi_rmmod(REALTEK_DU);
		wifi_rmmod(ATHEROS_K2);
        insmod_driver ("rfkill.ko", "");
        insmod_driver ("cfg80211.ko", ""); 
		insmod_driver("8712u.ko","");
	}
	else if  (REALTEK_DU == dongle_type)
	{
		wifi_rmmod(REALTEK_CU);
		wifi_rmmod(REALTEK_SU);
		wifi_rmmod(ATHEROS_K2);
		insmod_driver ("rfkill.ko", "");
        insmod_driver ("cfg80211.ko", ""); 
		insmod_driver("8192du.ko","");
	}
    else if (dongle_type == ATHEROS_ATH6K)
    {
        if (insmod_driver ("rfkill.ko", "") != 0)
        {
            LOGD("insmod rfkill.ko failed\n");
            //return -1; //rfkill will become build-in, skip return -1, if can't insmod this ko
        }
        if (insmod_driver ("compat.ko", "") !=  0)
        {   
            LOGD("insmod compat.ko failed\n");
            return -1;
        }
        insmod_driver ("cfg80211_ath6kl.ko", "");
        insmod_driver ("ath6kl_usb.ko", "ath6kl_p2p=1 chip_pwd_l=0");
    }
    	else if  (dongle_type == ATHEROS_K2)
	{
		wifi_rmmod(REALTEK_CU);
		wifi_rmmod(REALTEK_SU);
		wifi_rmmod(REALTEK_DU);
		insmod_driver ("adf.ko", "");
		insmod_driver ("asf.ko", "");
		insmod_driver ("ath_hif_usb.ko", "");
		insmod_driver ("ath_htc_hst.ko", "");
		insmod_driver ("ath_hal.ko", "");
		insmod_driver ("ath_dfs.ko", "");
		insmod_driver ("ath_rate_atheros.ko", "");
		insmod_driver ("ath_dev.ko", "");
		insmod_driver ("umac.ko", "");
		insmod_driver ("ath_usbdrv.ko", "");
		
        LOGD ("execute the wpa_supplicantat_pre shell \n" );
        property_set("ctl.start", "wifi_set:k2_pre");// todo!!! it should be in the wifi_set.sh
        
    }
    else 
    {
        LOGD ("unknown wifi driver type");
    }

    return 0;
}

static int wifi_rmmod (int dongle_type)
{
    LOGD ("wifi_rmmod dongle_type = %d,mode = \n", dongle_type,wifi_mode);
    if (dongle_type == Ralink_5570)
    {
        if(wifi_mode == STA_MODE)
        {
    		rmmod("rtnet5572sta"); 
    		rmmod("rt5572sta"); 
    		rmmod("rtutil5572sta");
        }
        else if(wifi_mode == AP_MODE)
        {
    		rmmod("rtnet5572ap"); 
    		rmmod("rt5572ap"); 
    		rmmod("rtutil5572ap");
        }
        else
            return -1;
    }
    else if (dongle_type == REALTEK_CU)
    {
        rmmod ("8192cu"); 
        rmmod ("cfg80211"); 
        rmmod ("rfkill");
    }
	else if  (REALTEK_SU == dongle_type)
	{
		rmmod ("8712u");
        rmmod ("cfg80211"); 
        rmmod ("rfkill");
	}
	else if  (REALTEK_DU == dongle_type)
	{
		rmmod("8192du");
        rmmod ("cfg80211"); 
        rmmod ("rfkill");
	}

    else if (dongle_type == ATHEROS_ATH6K)
    {
        rmmod ("ath6kl_usb");
        rmmod ("cfg80211");
        rmmod ("compat");
        rmmod ("rfkill");
    }
	else if  (dongle_type == ATHEROS_K2)
	{
		rmmod ("ath_usbdrv");
		rmmod ("umac");
		rmmod ("ath_dev");
		rmmod ("ath_rate_atheros");
		rmmod ("ath_dfs");
		rmmod ("ath_hal");
		rmmod ("ath_htc_hst");
		rmmod ("ath_hif_usb");
		rmmod ("asf");
		rmmod ("adf");

	}
	else 
	{
		LOGE ("Unknown dongle type");
    }
    return 0;
}


int do_dhcp_request(int *ipaddr, int *gateway, int *mask,
                    int *dns1, int *dns2, int *server, int *lease) {
    /* For test driver, always report success */
    if (strcmp(iface, WIFI_TEST_INTERFACE) == 0)
        return 0;

    if (ifc_init() < 0)
        return -1;

    if (do_dhcp(iface) < 0) {
        ifc_close();
        return -1;
    }
    ifc_close();
    get_dhcp_info(ipaddr, gateway, mask, dns1, dns2, server, lease);
    return 0;
}

const char *get_dhcp_error_string() {
    return dhcp_lasterror();
}

int is_wifi_driver_loaded() {
    char driver_status[PROPERTY_VALUE_MAX];
#ifdef WIFI_DRIVER_MODULE
    FILE *proc;
    char line[sizeof(DRIVER_MODULE_TAG)+10];
#endif

/*
    if (!property_get(DRIVER_PROP_NAME, driver_status, NULL)
            || strcmp(driver_status, "ok") != 0) {
        LOGD ("driver not loaded driver_status [%s]\n", driver_status);
        return 0;  // driver not loaded 
    }
    */
#ifdef WIFI_DRIVER_MODULE
    /*
     * If the property says the driver is loaded, check to
     * make sure that the property setting isn't just left
     * over from a previous manual shutdown or a runtime
     * crash.
     */
	if(cur_module == DEFAULT_DEV) {//modify by gaoyunpei
		LOGD ("gaoyunpei,cur_module == DEFAULT_DEV \n");
		if ((proc = fopen(MODULE_FILE, "r")) == NULL) {
		    LOGW("Could not open %s: %s", MODULE_FILE, strerror(errno));
		    property_set(DRIVER_PROP_NAME, "unloaded");
		    return 0;
		}
		while ((fgets(line, sizeof(line), proc)) != NULL) {
		    if (strncmp(line, DRIVER_MODULE_TAG, strlen(DRIVER_MODULE_TAG)) == 0) {
		        fclose(proc);
		        LOGD ("driver loaded driver insmod name is in \n");
		        return 1;
		    }
		}
		fclose(proc);
	}
	else{
		LOGD ("gaoyunpei,cur_module != DEFAULT_DEV,need reload driver \n");
	}
    property_set(DRIVER_PROP_NAME, "unloaded");
    LOGD ("have not found the driver name \n");
    return 0;
#else
    return 1;
#endif
}

int wifi_load_driver()
{
#ifdef WIFI_DRIVER_MODULE
    LOGD ("wifi_load_driver\n");
    char driver_status[PROPERTY_VALUE_MAX];
    int count = 100; /* wait at most 20 seconds for completion */

    if (is_wifi_driver_loaded()) {
        LOGD ("wifi driver have load\n");
        return 0;
    }

    LOGD ("DRIVER_MODULE_PATH [%s]DRIVER_MODULE_ARG[%s]", DRIVER_MODULE_PATH, DRIVER_MODULE_ARG);
   if (wifi_insmod(cur_module) < 0)
  // if (insmod(DRIVER_MODULE_PATH, DRIVER_MODULE_ARG) < 0)
    {
        LOGD ("driver insmod failed");
        return -1;
    }
LOGD ("wifi insmod succ");
    if (strcmp(FIRMWARE_LOADER,"") == 0) {
        /* usleep(WIFI_DRIVER_LOADER_DELAY); */
        LOGD ("DRIVER_PROP_NAME ok\n");
        property_set(DRIVER_PROP_NAME, "ok");
    }
    else {
        
        property_set("ctl.start", FIRMWARE_LOADER);
        
    }
    sched_yield();
    while (count-- > 0) {
        if (property_get(DRIVER_PROP_NAME, driver_status, NULL)) {
            if (strcmp(driver_status, "ok") == 0)
            {
                driver_is_load = 1;
                 return 0;
            }
            else if (strcmp(DRIVER_PROP_NAME, "failed") == 0) {
                wifi_unload_driver();
                return -1;
            }
        }
        usleep(200000);
    }
    LOGD ("DRIVER_PROP_NAME timeout\n");
    property_set(DRIVER_PROP_NAME, "timeout");
    wifi_unload_driver();
    return -1;
#else
    driver_is_load = 1;
    property_set(DRIVER_PROP_NAME, "ok");
    return 0;
#endif
}

int wifi_unload_driver()
{
    LOGD ("wifi_unload_driver");
    usleep(200000); /* allow to finish interface down */
#ifdef WIFI_DRIVER_MODULE
  LOGD("%s %d \n", __FUNCTION__,cur_module);

  if (wifi_rmmod(cur_module) == 0) {
  //   if (rmmod(DRIVER_MODULE_NAME) == 0) {
        int count = 20; /* wait at most 10 seconds for completion */
        while (count-- > 0) {
            if (!is_wifi_driver_loaded())
                break;
            usleep(500000);
        }
        usleep(500000); /* allow card removal */
        if (count) {
            driver_is_load = 0;
            return 0;
        }
        return -1;
    } else
        return -1;
#else
    driver_is_load = 0;
    property_set(DRIVER_PROP_NAME, "unloaded");
    return 0;
#endif
}

int ensure_entropy_file_exists()
{
    int ret;
    int destfd;

    ret = access(SUPP_ENTROPY_FILE, R_OK|W_OK);
    if ((ret == 0) || (errno == EACCES)) {
        if ((ret != 0) &&
            (chmod(SUPP_ENTROPY_FILE, S_IRUSR|S_IWUSR|S_IRGRP|S_IWGRP) != 0)) {
            LOGE("Cannot set RW to \"%s\": %s", SUPP_ENTROPY_FILE, strerror(errno));
            return -1;
        }
        return 0;
    }
    destfd = open(SUPP_ENTROPY_FILE, O_CREAT|O_RDWR, 0660);
    if (destfd < 0) {
        LOGE("Cannot create \"%s\": %s", SUPP_ENTROPY_FILE, strerror(errno));
        return -1;
    }

    if (write(destfd, dummy_key, sizeof(dummy_key)) != sizeof(dummy_key)) {
        LOGE("Error writing \"%s\": %s", SUPP_ENTROPY_FILE, strerror(errno));
        close(destfd);
        return -1;
    }
    close(destfd);

    /* chmod is needed because open() didn't set permisions properly */
    if (chmod(SUPP_ENTROPY_FILE, 0660) < 0) {
        LOGE("Error changing permissions of %s to 0660: %s",
             SUPP_ENTROPY_FILE, strerror(errno));
        unlink(SUPP_ENTROPY_FILE);
        return -1;
    }

    if (chown(SUPP_ENTROPY_FILE, AID_SYSTEM, AID_WIFI) < 0) {
        LOGE("Error changing group ownership of %s to %d: %s",
             SUPP_ENTROPY_FILE, AID_WIFI, strerror(errno));
        unlink(SUPP_ENTROPY_FILE);
        return -1;
    }
    return 0;
}

int update_ctrl_interface(const char *config_file) {

    int srcfd, destfd;
    int nread;
    char ifc[PROPERTY_VALUE_MAX];
    char *pbuf;
    char *sptr;
    struct stat sb;

    if (stat(config_file, &sb) != 0)
        return -1;
//	LOGD("gaoyunpei,config_file:%s", config_file);
    pbuf = malloc(sb.st_size + PROPERTY_VALUE_MAX);
    if (!pbuf)
        return 0;
    srcfd = open(config_file, O_RDONLY);
    if (srcfd < 0) {
        LOGE("Cannot open \"%s\": %s", config_file, strerror(errno));
        free(pbuf);
        return 0;
    }
    nread = read(srcfd, pbuf, sb.st_size);
    close(srcfd);
    if (nread < 0) {
        LOGE("Cannot read \"%s\": %s", config_file, strerror(errno));
        free(pbuf);
        return 0;
    }
    
    if (!strcmp(config_file, SUPP_CONFIG_FILE)) {
//		LOGD("gaoyunpei,ifc == wlan0");
        property_get("wifi.interface", ifc, WIFI_TEST_INTERFACE);
    } else if(!strcmp(config_file, P2P_CONFIG_FILE)){
        //strcpy(ifc, CONTROL_IFACE_PATH);
//		LOGD("gaoyunpei,ifc == wlan1");
        property_get("wifi.interface", ifc, WIFI_TEST_INTERFACE_P2P);
    } else {
//		LOGD("gaoyunpei,ifc == other");
        property_get("wifi.interface", ifc, WIFI_TEST_INTERFACE);
	}
    if ((sptr = strstr(pbuf, "ctrl_interface="))) {
        char *iptr = sptr + strlen("ctrl_interface=");
        int ilen = 0;
        int mlen = strlen(ifc);
        int nwrite;
        if (strncmp(ifc, iptr, mlen) != 0) {
            LOGE("ctrl_interface != %s", ifc);
            while (((ilen + (iptr - pbuf)) < nread) && (iptr[ilen] != '\n'))
                ilen++;
            mlen = ((ilen >= mlen) ? ilen : mlen) + 1;
            memmove(iptr + mlen, iptr + ilen + 1, nread - (iptr + ilen + 1 - pbuf));
            memset(iptr, '\n', mlen);
            memcpy(iptr, ifc, strlen(ifc));
            destfd = open(config_file, O_RDWR, 0660);
            if (destfd < 0) {
                LOGE("Cannot update \"%s\": %s", config_file, strerror(errno));
                free(pbuf);
                return -1;
            }
            write(destfd, pbuf, nread + mlen - ilen -1);
            close(destfd);
        }
    }
    free(pbuf);
    return 0;
}

int ensure_config_file_exists(const char *config_file)
{
    char buf[2048];
    int srcfd, destfd;
    struct stat sb;
    int nread;
    int ret;

    ret = access(config_file, R_OK|W_OK);
    if ((ret == 0) || (errno == EACCES)) {
        if ((ret != 0) &&
            (chmod(config_file, S_IRUSR|S_IWUSR|S_IRGRP|S_IWGRP) != 0)) {
            LOGE("Cannot set RW to \"%s\": %s", config_file, strerror(errno));
            return -1;
        }
        /* return if filesize is at least 10 bytes */
        if (stat(config_file, &sb) == 0 && sb.st_size > 10) {
            //return update_ctrl_interface(config_file);
            return 0;
        }
    } else if (errno != ENOENT) {
        LOGE("Cannot access \"%s\": %s", config_file, strerror(errno));
        return -1;
    }

    srcfd = open(SUPP_CONFIG_TEMPLATE, O_RDONLY);
    if (srcfd < 0) {
        LOGE("Cannot open \"%s\": %s", SUPP_CONFIG_TEMPLATE, strerror(errno));
        return -1;
    }

    destfd = open(config_file, O_CREAT|O_RDWR, 0660);
    if (destfd < 0) {
        close(srcfd);
        LOGE("Cannot create \"%s\": %s", config_file, strerror(errno));
        return -1;
    }

    while ((nread = read(srcfd, buf, sizeof(buf))) != 0) {
        if (nread < 0) {
            LOGE("Error reading \"%s\": %s", SUPP_CONFIG_TEMPLATE, strerror(errno));
            close(srcfd);
            close(destfd);
            unlink(config_file);
            return -1;
        }
        write(destfd, buf, nread);
    }

    close(destfd);
    close(srcfd);

    /* chmod is needed because open() didn't set permisions properly */
    if (chmod(config_file, 0660) < 0) {
        LOGE("Error changing permissions of %s to 0660: %s",
             config_file, strerror(errno));
        unlink(config_file);
        return -1;
    }

    if (chown(config_file, AID_SYSTEM, AID_WIFI) < 0) {
        LOGE("Error changing group ownership of %s to %d: %s",
             config_file, AID_WIFI, strerror(errno));
        unlink(config_file);
        return -1;
    }
    return update_ctrl_interface(config_file);
}

/**
 * wifi_wpa_ctrl_cleanup() - Delete any local UNIX domain socket files that
 * may be left over from clients that were previously connected to
 * wpa_supplicant. This keeps these files from being orphaned in the
 * event of crashes that prevented them from being removed as part
 * of the normal orderly shutdown.
 */
void wifi_wpa_ctrl_cleanup(void)
{
    DIR *dir;
    struct dirent entry;
    struct dirent *result;
    size_t dirnamelen;
    size_t maxcopy;
    char pathname[PATH_MAX];
    char *namep;
    char *local_socket_dir = CONFIG_CTRL_IFACE_CLIENT_DIR;
    char *local_socket_prefix = CONFIG_CTRL_IFACE_CLIENT_PREFIX;

    if ((dir = opendir(local_socket_dir)) == NULL)
        return;

    dirnamelen = (size_t)snprintf(pathname, sizeof(pathname), "%s/", local_socket_dir);
    if (dirnamelen >= sizeof(pathname)) {
        closedir(dir);
        return;
    }
    namep = pathname + dirnamelen;
    maxcopy = PATH_MAX - dirnamelen;
    while (readdir_r(dir, &entry, &result) == 0 && result != NULL) {
        if (strncmp(entry.d_name, local_socket_prefix, strlen(local_socket_prefix)) == 0) {
            if (strlcpy(namep, entry.d_name, maxcopy) < maxcopy) {
                unlink(pathname);
            }
        }
    }
    closedir(dir);
}

int wifi_start_supplicant_common(const char *config_file, bool bP2p)
{
    char daemon_cmd[PROPERTY_VALUE_MAX];
    char supp_status[PROPERTY_VALUE_MAX] = {'\0'};
	static char test[PROPERTY_VALUE_MAX] = {0};
	int ret = 0;


	ret = property_get(DEBUG_TEST, test, NULL);
	LOGD("%s worked %d, ret = [%d], test=[%s]\n", __FUNCTION__, __LINE__, ret, test);

	
    int count = 200; /* wait at most 20 seconds for completion */
    
    char str_cur_mode[20];
    
    ret  = property_get(CUR_MODULE_PROP_NAME,str_cur_mode,"99");
	
	LOGD("%s worked %d, ret = [%d]\n", __FUNCTION__, __LINE__, ret);

    ret = property_get("wifi.interface", test, NULL);

	LOGD("%s worked %d, ret = [%d],interface name[%s] \n", __FUNCTION__, __LINE__, ret, test);

	
    cur_module = atoi(str_cur_mode);//wifi_get_cur_module_frome_str(str_cur_mode);//REALTEK_CU;
    if(cur_module > WIFI_DEV_NUM)
    {
    	LOGE("no wifi device inside!!cur_module == %d",cur_module);
//		cur_module = DEFAULT_DEV;
    	return -1;	
    }
    wifi_set_envir_vali(cur_module);

#ifdef HAVE_LIBC_SYSTEM_PROPERTIES
    const prop_info *pi;
    unsigned serial = 0;
#endif

    /* Check whether already running */
    if (bP2p)
    {
        if (property_get(P2P_SUPP_PROP_NAME, supp_status, NULL)
                && strcmp(supp_status, "running") == 0) {
            LOGD ("P2P_SUPP_PROP_NAME is runing");
            return 0;
        }
    }
    else
    {
    if (property_get(SUPP_PROP_NAME, supp_status, NULL)
            && strcmp(supp_status, "running") == 0) {
        return 0;
    }
    }

    /* Before starting the daemon, make sure its config file exists */
    if (ensure_config_file_exists(config_file) < 0) {
        LOGE("Wi-Fi will not be enabled");
        return -1;
    }

    if (ensure_entropy_file_exists() < 0) {
        LOGE("Wi-Fi entropy file was not created");
    }

    /* Clear out any stale socket files that might be left over. */
    wifi_wpa_ctrl_cleanup();

#ifdef HAVE_LIBC_SYSTEM_PROPERTIES
    /*
     * Get a reference to the status property, so we can distinguish
     * the case where it goes stopped => running => stopped (i.e.,
     * it start up, but fails right away) from the case in which
     * it starts in the stopped state and never manages to start
     * running at all.
     */
    pi = __system_property_find(SUPP_PROP_NAME);
    if (pi != NULL) {
        serial = pi->serial;
    }
#endif
    if (bP2p)
    {
    	if (cur_module == ATHEROS_ATH6K )
		{
        LOGD ("start p2p0 script");
	        property_set("ctl.start", "wifi_set:iw");
        usleep(100000);
		}

        memset (iface, 0, sizeof(iface));  

		if (cur_module == ATHEROS_ATH6K || cur_module == Ralink_5570)
		{
			strncpy (iface, "p2p0", strlen("p2p0"));
		}
		else if (cur_module == REALTEK_CU)
		{
			strncpy (iface, "wlan1", strlen("wlan1"));
//			LOGD ("gaoyunpei:interface name[%s]\n", iface);
		}
		else
		{
			strncpy (iface, "wlan0", strlen("wlan0"));// todo!!!
//			LOGD ("gaoyunpei:interface name[%s]\n", iface);
		}
        snprintf(daemon_cmd, PROPERTY_VALUE_MAX, "%s", P2P_SUPPLICANT_NAME);
    }
    else
    {
    property_get("wifi.interface", iface, WIFI_TEST_INTERFACE);
        LOGD ("interface name[%s]\n", iface);
    snprintf(daemon_cmd, PROPERTY_VALUE_MAX, "%s", SUPPLICANT_NAME);
    }

    LOGD (">>>> start wpa_supplicant daemon_cmd[%s]\n", daemon_cmd);
    property_set("ctl.start", daemon_cmd);
    sched_yield();

    while (count-- > 0) {
	    if (bP2p)
	    {
	        if (property_get(P2P_SUPP_PROP_NAME, supp_status, NULL)) {
	            LOGD ("P2P_SUPP_PROP_NAME=%s supp_status=%s", P2P_SUPP_PROP_NAME,supp_status);
	            if (strcmp(supp_status, "running") == 0)
	                return 0;
	        }
			
			LOGD ("P2P_SUPP_PROP_NAME=%s supp_status=%s", P2P_SUPP_PROP_NAME,supp_status);
	    }
	    else
	    {
	        if (property_get(SUPP_PROP_NAME, supp_status, NULL)) {
	            if (strcmp(supp_status, "running") == 0)
	                return 0;
	        }
	    }
        usleep(100000);
    }

	LOGD ("wifi_start_supplicant_common failed");
    return -1;
}

int wifi_start_supplicant()
{
    int ret;
    char ifname[IFNAMSIZ];
    char buf[256];
    
    ret = property_get("wifi.interface", iface, WIFI_TEST_INTERFACE);
    strlcpy(ifname, iface, sizeof(ifname));

	LOGD("%s worked %d, ifname =[%s], ret = [%d]\n", __FUNCTION__, __LINE__, ifname, ret);


	char test[20] = {0};
	ret = property_set(DEBUG_TEST, "test");	
	LOGD("%s worked %d, ret = [%d]\n", __FUNCTION__, __LINE__, ret);

	ret = property_get(DEBUG_TEST, test, NULL);
	LOGD("%s worked %d, ret = [%d], test=[%s]\n", __FUNCTION__, __LINE__, ret, test);
	
     if (cur_module == REALTEK_CU)      
    rtw_issue_driver_cmd(ifname, "BLOCK 1", buf, 256);
    ret = wifi_start_supplicant_common(SUPP_CONFIG_FILE, false);
    
    return ret;
}

int wifi_start_p2p_supplicant()
{
    return wifi_start_supplicant_common(P2P_CONFIG_FILE, true);
}

int wifi_stop_supplicant()
{
    char supp_status[PROPERTY_VALUE_MAX] = {'\0'};
    int count = 50; /* wait at most 5 seconds for completion */

    /* Check whether supplicant already stopped */
    if (property_get(SUPP_PROP_NAME, supp_status, NULL)
        && strcmp(supp_status, "stopped") == 0) {
        return 0;
    }

    property_set("ctl.stop", SUPPLICANT_NAME);
    sched_yield();

    while (count-- > 0) {
        if (property_get(SUPP_PROP_NAME, supp_status, NULL)) {
            if (strcmp(supp_status, "stopped") == 0)
                return 0;
        }
        usleep(100000);
    }
    return -1;
}
int wifi_stop_p2p_supplicant()
{
    char supp_status[PROPERTY_VALUE_MAX] = {'\0'};
    int count = 50; /* wait at most 5 seconds for completion */

    /* Check whether supplicant already stopped */
    if (property_get(P2P_SUPP_PROP_NAME, supp_status, NULL)
        && strcmp(supp_status, "stopped") == 0) {
        return 0;
    }

    property_set("ctl.stop", P2P_SUPPLICANT_NAME);   // todo!!!
    sched_yield();

    while (count-- > 0) {
        if (property_get(P2P_SUPP_PROP_NAME, supp_status, NULL)) {
            if (strcmp(supp_status, "stopped") == 0)
                return 0;
        }
        usleep(100000);
    }
    return -1;
}


int wifi_connect_to_supplicant()
{
    char ifname[256];
    char supp_status[PROPERTY_VALUE_MAX] = {'\0'};

    /* Make sure supplicant is running */
    if ((!property_get(SUPP_PROP_NAME, supp_status, NULL)
            || strcmp(supp_status, "running") != 0)&&(!property_get(P2P_SUPP_PROP_NAME, supp_status, NULL)
            || strcmp(supp_status, "running") != 0)) {
        LOGE("Supplicant not running, cannot connect");
        return -1;
    }

    if (access(IFACE_DIR, F_OK) == 0) {
        snprintf(ifname, sizeof(ifname), "%s/%s", IFACE_DIR, iface);
    } else {
        strlcpy(ifname, iface, sizeof(ifname));
    }
    if (cur_module == ATHEROS_ATH6K)
    {
        LOGD ("execute the chmod_ath0 shell\n");
        property_set("ctl.start", "wifi_set:chd");
        usleep(100000);
    }

    ctrl_conn = wpa_ctrl_open(ifname);
    if (ctrl_conn == NULL) {
        LOGE("Unable to open connection to supplicant on \"%s\": %s",
             ifname, strerror(errno));
        return -1;
    }
    monitor_conn = wpa_ctrl_open(ifname);
    if (monitor_conn == NULL) {
        wpa_ctrl_close(ctrl_conn);
        ctrl_conn = NULL;
        return -1;
    }
    if (wpa_ctrl_attach(monitor_conn) != 0) {
        wpa_ctrl_close(monitor_conn);
        wpa_ctrl_close(ctrl_conn);
        ctrl_conn = monitor_conn = NULL;
        return -1;
    }

    if (socketpair(AF_UNIX, SOCK_STREAM, 0, exit_sockets) == -1) {
        wpa_ctrl_close(monitor_conn);
        wpa_ctrl_close(ctrl_conn);
        ctrl_conn = monitor_conn = NULL;
        return -1;
    }

    {   //todo!!!
        char ifname[IFNAMSIZ];
        char buf[256];
    
        strlcpy(ifname, iface, sizeof(ifname));
         if (cur_module == REALTEK_CU)      
        rtw_issue_driver_cmd(ifname, "BLOCK 0", buf, 256);
    }

    return 0;
}
int wifi_connect_to_p2p_supplicant()
{
    char ifname[256];
    char supp_status[PROPERTY_VALUE_MAX] = {'\0'};

    /* Make sure supplicant is running */
    if (!property_get(P2P_SUPP_PROP_NAME, supp_status, NULL)
            || strcmp(supp_status, "running") != 0) {
        LOGE("p2p_Supplicant not running, cannot connect");
        return -1;
    }
    LOGD ("iface = %s", iface);
    if (access(IFACE_DIR, F_OK) == 0) {
        snprintf(ifname, sizeof(ifname), "%s/%s", IFACE_DIR, iface);
    } else {
        strlcpy(ifname, iface, sizeof(ifname));
    }

    if (cur_module == ATHEROS_ATH6K)   
    {
        LOGD ("execute the p2p_chmod_ath0 shell \n" );
        property_set("ctl.start", "wifi_set:chd");
        usleep(100000);
    }

    p2p_ctrl_conn = wpa_ctrl_open(ifname);
    if (p2p_ctrl_conn == NULL) {
        LOGE("Unable to open connection to supplicant on \"%s\": %s",
             ifname, strerror(errno));
        return -1;
    }
    p2p_monitor_conn = wpa_ctrl_open(ifname);
    if (p2p_monitor_conn == NULL) {
        wpa_ctrl_close(p2p_ctrl_conn);
        p2p_ctrl_conn = NULL;
        return -1;
    }
    if (wpa_ctrl_attach(p2p_monitor_conn) != 0) {
        wpa_ctrl_close(p2p_monitor_conn);
        wpa_ctrl_close(p2p_ctrl_conn);
        p2p_ctrl_conn = p2p_monitor_conn = NULL;
        return -1;
    }

    if (socketpair(AF_UNIX, SOCK_STREAM, 0, p2p_exit_sockets) == -1) {  
        wpa_ctrl_close(p2p_monitor_conn);
        wpa_ctrl_close(p2p_ctrl_conn);
        p2p_ctrl_conn = p2p_monitor_conn = NULL;
        return -1;
    }
//	LOGD ("gaoyunpei,p2p_connect_supplicant succ\n");
    return 0;
}


int wifi_send_p2p_command(struct wpa_ctrl *ctrl, const char *cmd, char *reply, size_t *reply_len)
{
    int ret;
    LOGD("p2p send cmd is %s \n", cmd);
    if (ctrl == NULL) {
        LOGV("Not connected to wpa_supplicant - \"%s\" command dropped.\n", cmd);
        return -1;
    }
    ret = wpa_ctrl_request(ctrl, cmd, strlen(cmd), reply, reply_len, NULL);
    if (ret == -2) {
        LOGD("'%s' command timed out.\n", cmd);
        /* unblocks the monitor receive socket for termination */
        write(p2p_exit_sockets[0], "T", 1);
        return -2;
    } else if (ret < 0 || strncmp(reply, "FAIL", 4) == 0) {
    return -1;
}
    if (strncmp(cmd, "PING", 4) == 0) {
        reply[*reply_len] = '\0';
    }
    return 0;

}


int wifi_send_command(struct wpa_ctrl *ctrl, const char *cmd, char *reply, size_t *reply_len)
{
    int ret;
    LOGD("send cmd is %s \n", cmd);
    if (ctrl == NULL) {
        LOGV("Not connected to wpa_supplicant - \"%s\" command dropped.\n", cmd);
        return -1;
    }
    ret = wpa_ctrl_request(ctrl, cmd, strlen(cmd), reply, reply_len, NULL);
    if (ret == -2) {
        LOGD("'%s' command timed out.\n", cmd);
        /* unblocks the monitor receive socket for termination */
        write(exit_sockets[0], "T", 1);
        return -2;
    } else if (ret < 0 || strncmp(reply, "FAIL", 4) == 0) {
        return -1;
    }
    if (strncmp(cmd, "PING", 4) == 0) {
        reply[*reply_len] = '\0';
    }
    return 0;
}

int wifi_ctrl_recv(struct wpa_ctrl *ctrl, char *reply, size_t *reply_len)
{
    int res;
    int ctrlfd = wpa_ctrl_get_fd(ctrl);
    struct pollfd rfds[2];

    memset(rfds, 0, 2 * sizeof(struct pollfd));
    rfds[0].fd = ctrlfd;
    rfds[0].events |= POLLIN;
    rfds[1].fd = exit_sockets[1];
    rfds[1].events |= POLLIN;
    res = poll(rfds, 2, -1);
    if (res < 0) {
        LOGE("Error poll = %d", res);
        return res;
    }
    if (rfds[0].revents & POLLIN) {
        return wpa_ctrl_recv(ctrl, reply, reply_len);
    } else {
        LOGD("Received on exit socket, terminate");
        return -1;
    }
    return 0;
}

int wifi_ctrl_p2p_recv(struct wpa_ctrl *ctrl, char *reply, size_t *reply_len)
{
    int res;
    int ctrlfd = wpa_ctrl_get_fd(ctrl);
    struct pollfd rfds[2];

    memset(rfds, 0, 2 * sizeof(struct pollfd));
    rfds[0].fd = ctrlfd;
    rfds[0].events |= POLLIN;
    rfds[1].fd = p2p_exit_sockets[1];
    rfds[1].events |= POLLIN;
    res = poll(rfds, 2, -1);
    if (res < 0) {
        LOGE("p2p Error poll = %d", res);
        return res;
    }
    if (rfds[0].revents & POLLIN) {
        return wpa_ctrl_recv(ctrl, reply, reply_len);
    } else {
        LOGD("p2p Received on exit socket, terminate");
        return -1;
    }
    return 0;
}


int wifi_wait_for_event(char *buf, size_t buflen)
{
    size_t nread = buflen - 1;
    int fd;
    fd_set rfds;
    int result;
    struct timeval tval;
    struct timeval *tptr;

    if (monitor_conn == NULL) {
        LOGD("Connection closed\n");
        strncpy(buf, WPA_EVENT_TERMINATING " - connection closed", buflen-1);
        buf[buflen-1] = '\0';
        return strlen(buf);
    }

    result = wifi_ctrl_recv(monitor_conn, buf, &nread);
    if (result < 0) {
        LOGD("wifi_ctrl_recv failed: %s\n", strerror(errno));
        strncpy(buf, WPA_EVENT_TERMINATING " - recv error", buflen-1);
        buf[buflen-1] = '\0';
        return strlen(buf);
    }
    buf[nread] = '\0';
    /* LOGD("wait_for_event: result=%d nread=%d string=\"%s\"\n", result, nread, buf); */
    /* Check for EOF on the socket */
    if (result == 0 && nread == 0) {
        /* Fabricate an event to pass up */
        LOGD("Received EOF on supplicant socket\n");
        strncpy(buf, WPA_EVENT_TERMINATING " - signal 0 received", buflen-1);
        buf[buflen-1] = '\0';
        return strlen(buf);
    }
    /*
     * Events strings are in the format
     *
     *     <N>CTRL-EVENT-XXX 
     *
     * where N is the message level in numerical form (0=VERBOSE, 1=DEBUG,
     * etc.) and XXX is the event name. The level information is not useful
     * to us, so strip it off.
     */
    if (buf[0] == '<') {
        char *match = strchr(buf, '>');
        if (match != NULL) {
            nread -= (match+1-buf);
            memmove(buf, match+1, nread+1);
        }
    }
    LOGD("Received event is %s\n",buf);
    return nread;
}

int wifi_wait_for_p2p_event(char *buf, size_t buflen)
{
    size_t nread = buflen - 1;
    int fd;
    fd_set rfds;
    int result;
    struct timeval tval;
    struct timeval *tptr;

    if (p2p_monitor_conn == NULL) {
        LOGD("p2p Connection closed\n");
        strncpy(buf, WPA_EVENT_TERMINATING " - connection closed", buflen-1);
        buf[buflen-1] = '\0';
        return strlen(buf);
    }

    result = wifi_ctrl_p2p_recv(p2p_monitor_conn, buf, &nread);
    if (result < 0) {
        LOGD("p2p wifi_ctrl_recv failed: %s\n", strerror(errno));
        strncpy(buf, WPA_EVENT_TERMINATING " - recv error", buflen-1);
        buf[buflen-1] = '\0';
        return strlen(buf);
    }
    buf[nread] = '\0';
    /* LOGD("wait_for_event: result=%d nread=%d string=\"%s\"\n", result, nread, buf); */
    /* Check for EOF on the socket */
    if (result == 0 && nread == 0) {
        /* Fabricate an event to pass up */
        LOGD("p2p Received EOF on supplicant socket\n");
        strncpy(buf, WPA_EVENT_TERMINATING " - signal 0 received", buflen-1);
        buf[buflen-1] = '\0';
        return strlen(buf);
}
    /*
     * Events strings are in the format
     *
     *     <N>CTRL-EVENT-XXX 
     *
     * where N is the message level in numerical form (0=VERBOSE, 1=DEBUG,
     * etc.) and XXX is the event name. The level information is not useful
     * to us, so strip it off.
     */
    if (buf[0] == '<') {
        char *match = strchr(buf, '>');
        if (match != NULL) {
            nread -= (match+1-buf);
            memmove(buf, match+1, nread+1);
        }
    }
    LOGD("p2p Received event is %s\n",buf);
    return nread;
}


void wifi_close_supplicant_connection()
{
    char supp_status[PROPERTY_VALUE_MAX] = {'\0'};
    int count = 50; /* wait at most 5 seconds to ensure init has stopped stupplicant */

    if (ctrl_conn != NULL) {
        wpa_ctrl_close(ctrl_conn);
        ctrl_conn = NULL;
    }
    if (monitor_conn != NULL) {
        wpa_ctrl_close(monitor_conn);
        monitor_conn = NULL;
    }

    if (exit_sockets[0] >= 0) {
        close(exit_sockets[0]);
        exit_sockets[0] = -1;
    }

    if (exit_sockets[1] >= 0) {
        close(exit_sockets[1]);
        exit_sockets[1] = -1;
    }

    while (count-- > 0) {
        if (property_get(SUPP_PROP_NAME, supp_status, NULL)) {
            if (strcmp(supp_status, "stopped") == 0)
                return;
        }
        usleep(100000);
    }
}

void wifi_close_p2p_supplicant_connection()
{
    char supp_status[PROPERTY_VALUE_MAX] = {'\0'};
    int count = 50; /* wait at most 5 seconds to ensure init has stopped stupplicant */

    if (p2p_ctrl_conn != NULL) {
        wpa_ctrl_close(p2p_ctrl_conn);
        p2p_ctrl_conn = NULL;
    }
    if (p2p_monitor_conn != NULL) {
        wpa_ctrl_close(p2p_monitor_conn);
        p2p_monitor_conn = NULL;
    }

    if (p2p_exit_sockets[0] >= 0) {
        close(p2p_exit_sockets[0]);
        p2p_exit_sockets[0] = -1;
    }

    if (p2p_exit_sockets[1] >= 0) {
        close(p2p_exit_sockets[1]);
        p2p_exit_sockets[1] = -1;
}

    while (count-- > 0) {
        if (property_get(P2P_SUPP_PROP_NAME, supp_status, NULL)) {
            if (strcmp(supp_status, "stopped") == 0)
                return;
        }
        usleep(100000);
    }

}

int wifi_command(const char *command, char *reply, size_t *reply_len)
{
    return wifi_send_command(ctrl_conn, command, reply, reply_len);
}

int wifi_p2p_command(const char *command, char *reply, size_t *reply_len)
{
    return wifi_send_command(ctrl_conn, command, reply, reply_len);
//    return wifi_send_p2p_command(p2p_ctrl_conn, command, reply, reply_len);
}



const char *wifi_get_fw_path(int fw_type)
{
    switch (fw_type) {
    case WIFI_GET_FW_PATH_STA:
        return WIFI_DRIVER_FW_PATH_STA;
    case WIFI_GET_FW_PATH_AP:
        return WIFI_DRIVER_FW_PATH_AP;
    case WIFI_GET_FW_PATH_P2P:
        return WIFI_DRIVER_FW_PATH_P2P;
    }
    return NULL;
}

int wifi_change_fw_path(const char *fwpath)
{
    int len;
    int fd;
    int ret = 0;
    static char previous_fwpath[4];

	LOGD("%s worked %d", __FUNCTION__, __LINE__);

//    if (!fwpath)
//        return ret;

//    LOGE("%s: %s\n", __FUNCTION__, fwpath);
/*    if(strncmp("P2P", fwpath, 3) == 0) {
        ret = wifi_check_is_dongle_in();//wifi_load_driver();
    } else if(strncmp("P2P", previous_fwpath, 3) == 0) {
        ret = wifi_unload_driver();
    }
*/
		ret = wifi_check_is_dongle_in();

    strncpy(previous_fwpath, fwpath, 3);

#if 0
    fd = open(WIFI_DRIVER_FW_PATH_PARAM, O_WRONLY);
    if (fd < 0) {
        LOGE("Failed to open wlan fw path param (%s)", strerror(errno));
        return -1;
    }
    len = strlen(fwpath) + 1;
    if (write(fd, fwpath, len) != len) {
        LOGE("Failed to write wlan fw path param (%s)", strerror(errno));
        ret = -1;
    }
    close(fd);
#endif
    return ret;
}

int wifi_change_wifi_mode(int mode)
{
    int ret = 0;
    LOGD("%s: moudle: %d frome mode: %d change to %d\n", __FUNCTION__,cur_module,wifi_mode,mode);
    if(wifi_mode == mode)
    {
        return 0;   
    }
    if (cur_module == Ralink_5570)
    {
        LOGD ("wifi_change_wifi_mode unload driver");
        do
        {
            if(wifi_mode == STA_MODE)
            {
                ifc_init();
                ret = ifc_down(WIFI_TEST_INTERFACE);
                LOGD ("ifc_down WIFI_TEST_INTERFACE %d",ret);
                
                ret = ifc_down(WIFI_TEST_INTERFACE_P2P);
                LOGD ("ifc_downWIFI_TEST_INTERFACE_P2P %d",ret);
                ifc_close();
            }
            else
            {
                ifc_init();
                ret = ifc_down(WIFI_TEST_INTERFACE_AP);
                LOGD ("ifc_down WIFI_TEST_INTERFACE_AP %d",ret);
                 ifc_close();
}
            ret = wifi_unload_driver();
            if(ret != 0)
            {
                LOGD ("wifi_unload_driver fail in changemode %d",ret);
                break;
            }
            wifi_mode = mode;
            ret = wifi_load_driver();
            if(ret != 0)
            {
                LOGD ("wifi_load_driver fail in changemode %d",ret);
                break;
            }
//do ifc_up(WIFI_TEST_INTERFACE_AP) will send CMD_THER_STATE_CHANGE to statemachine which should be do in softapcontroller
            /*
            if(wifi_mode == STA_MODE)
            {
                ifc_init();
                ret = ifc_up(WIFI_TEST_INTERFACE);
                LOGD ("ifc_up %d",ret);

                ret = ifc_up(WIFI_TEST_INTERFACE_P2P);
                LOGD ("ifc_up %d",ret);
                ifc_close();
            }
            else
            {
                ifc_init();
                ret = ifc_up(WIFI_TEST_INTERFACE_AP);
                LOGD ("ifc_up %d",ret);
                ifc_close();
            }
            */ 
            LOGD ("wifi_change_wifi_mode change successfully");
            return 0;
        }while(0);

        LOGD ("wifi_change_wifi_mode change fail");
        return -1;
    }
    LOGD ("wifi_change_wifi_mode not 5372 driver");
    return -1;
}

