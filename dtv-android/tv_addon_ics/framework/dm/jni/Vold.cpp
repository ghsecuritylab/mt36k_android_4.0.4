#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>

#include <pthread.h>
#include "Vold.h"
#include "ExternalVolume.h"
#include <android/log.h>

//#define VOLD_DEBUG

#define VOLD_TAG "DMVold"

using namespace android;

static const int START_MONITOR = 0;
static const int GET_EVENT = 1;  
static const int DO_CMD = 2;
static const int EXIT_MONITOR = 3;
    
sp<IBinder> binder = NULL;
const char* SERVICE_NAME = "VoldService";

int Vold::start( )
{
    Parcel data;
    Parcel reply;

    binder->transact(START_MONITOR, data, &reply);

    int i = reply.readInt32();
    return i;
}

int Vold::get(void** value)
{       
    Parcel data;
    Parcel reply;
    
    binder->transact(GET_EVENT, data, &reply);

    int event = reply.readInt32();
    #ifdef VOLD_DEBUG
    __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "event type: %d\n", event);
    #endif
    switch(event)
    {
    case ExternalVolumeInfo::VolumeMounted:
        {
            ExternalVolumeInfo* exVolInfo = new ExternalVolumeInfo();
            exVolInfo->mMountPoint = strdup(reply.readCString());
            exVolInfo->mDeviceName = strdup(reply.readCString());
            exVolInfo->mVolumeLabel = strdup(reply.readCString());
            exVolInfo->mVolumeTotalSize = reply.readInt32();
            exVolInfo->mVolumeFreeSize = reply.readInt32();
            exVolInfo->mVolumeMajor = reply.readInt32();
            exVolInfo->mVolumeMinor = reply.readInt32();
            exVolInfo->mVolumeStatus = reply.readInt32();
            exVolInfo->mFileSystemType = (FS_TYPE_E)reply.readInt32();
        #ifdef VOLD_DEBUG
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "mountPoint: %s\n", exVolInfo->mMountPoint);
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "deviceName: %s\n", exVolInfo->mDeviceName);
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "volumeLabel: %s\n", exVolInfo->mVolumeLabel);
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "total size: %d\n", exVolInfo->mVolumeTotalSize);
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "free size: %d\n", exVolInfo->mVolumeFreeSize);
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "major: %d\n", exVolInfo->mVolumeMajor);
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "minor: %d\n", exVolInfo->mVolumeMinor);
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "status: %d\n", exVolInfo->mVolumeStatus); 
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "fileSystemType: %d\n", exVolInfo->mFileSystemType);
        #endif
            
            *value = (void*) exVolInfo;
        }
        break;
    case ExternalVolumeInfo::VolumeUmounted:
        {
            char* deviceName = strdup(reply.readCString());
        #ifdef VOLD_DEBUG
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "device name: %s\n", deviceName);
        #endif
        
            *value = (void*)deviceName;
        }    
        break;
    case Device::DeviceConnected:
        {
            Device *dev = new Device();
            dev->mDeviceName = strdup(reply.readCString());
            dev->mMajor = reply.readInt32();
            dev->mMinor = reply.readInt32();
            dev->mStatus = reply.readInt32();
        #ifdef VOLD_DEBUG       
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "deviceName: %s\n", dev->mDeviceName);
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "major: %d\n", dev->mMajor);
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "minor: %d\n", dev->mMinor);
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "status: %d\n", dev->mStatus);            
        #endif
        
            *value = (void*)dev;
        }
        break;
    case Device::DeviceDisconnected:
        {
            char* deviceName = strdup(reply.readCString());
        #ifdef VOLD_DEBUG
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "device name: %s\n", deviceName);
        #endif
        
            *value = (void*)deviceName;
        }    
        break;
     case Device::DeviceWifiConnected:
        {
             char* wifiInterface = strdup(reply.readCString());
        #ifdef VOLD_DEBUG
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "[VOLD]WIFI Dangle Interace: %s\n", wifiInterface);
        #endif
        
            *value = (void*)wifiInterface;
        }
        break;
     case Device::DeviceWifiDisconnected:
        {
            char* wifiInterface = strdup(reply.readCString());
        #ifdef VOLD_DEBUG
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "[VOLD]WIFI Dangle Interace: %s\n", wifiInterface);
        #endif
        
            *value = (void*)wifiInterface;
        }
        break;  
     case Device::DeviceIsoMountFailed:
        {
            char* mntpath = strdup(reply.readCString());
        #ifdef VOLD_DEBUG
            __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "[VOLD]ISO mount path: %s\n", mntpath);
        #endif
        
            *value = (void*)mntpath;
        }
        break;
    default:
        *value = NULL;
        break;
    }

    return event;
}

int Vold::doCmd(int command, void* value)
{
    Parcel data;
    Parcel reply;

    switch(command)
    {
    case UMOUNT_DEVICE_CMD:
        {
            data.writeInt32(Device::DeviceDisconnected);
            data.writeCString((const char*)value);
        }
        break;
    case MOUNT_ISO_CMD:
        {           
           data.writeInt32(Device::DeviceIsoMounted); 
           data.writeCString((const char*)value);
        }
        break;
    case MOUNT_ISO_EX_CMD:
        {
           data.writeInt32(Device::DeviceIsoExMounted);
           data.writeCString(((struct mountiso_ex_param *)value)->isoFilePath);
           data.writeCString(((struct mountiso_ex_param *)value)->isoLabel);
        }
        break;
    case UMOUNT_ISO_CMD:
        {           
           data.writeInt32(Device::DeviceIsoUMounted);
           data.writeCString((const char*)value);
        }
        break;
    default:
        break;
    }
    
    binder->transact(DO_CMD, data, &reply);
    int ret = reply.readInt32();
    return ret;
}
	
	
int Vold::stop()
{
    Parcel data;
    Parcel reply;

    binder->transact(EXIT_MONITOR, data, &reply);

    int i = reply.readInt32();

    return i;
}

Vold::Vold()
{
    sp<IServiceManager> sm = defaultServiceManager();
    binder = sm->getService(String16(SERVICE_NAME)); 
    if( binder == NULL)
    {
        __android_log_print(ANDROID_LOG_DEBUG, VOLD_TAG, "binder is NULL.\n");  
    }
}

Vold::~Vold()
{
}
