#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "com_mediatek_dm_DeviceManager.h"
//#include "com_mediatek_dm_doEvent.h"
#include "ExternalVolume.h"
#include "Vold.h"
#include <android/log.h>
#include <pthread.h>

//#define DM_DEBUG

#define DM_TAG "DM"

typedef struct _DeviceManagerEnv
{
    JavaVM* vm;
    jobject dm;
    jclass dmCls;
    jclass mntCls;
    jclass devCls;
    jclass dmEventCls;
    Vold* vl;
    pthread_t thrd;
} DeviceManagerEnv;

DeviceManagerEnv* pDmEnv = NULL;

static void* voldThread(void* data);



/*
 * Class:     com_mediatek_dm_DeviceManager
 * Method:    nativeUmount
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_mediatek_dm_DMServer_nativeUmount
  (JNIEnv* env, jclass clazz, jstring str)
{
    char* deviceName = strdup(env->GetStringUTFChars(str, NULL));
    if(pDmEnv != NULL && pDmEnv != NULL)
    {
        pDmEnv->vl->doCmd(Vold::UMOUNT_DEVICE_CMD, (void*)deviceName);
    }
}

/*
 * Class:     com_mediatek_dm_DeviceManager
  * Method:    nativeMountISO
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_mediatek_dm_DMServer_nativeMountISO
  (JNIEnv* env, jclass clazz, jstring str)
{
    char* isoFilePath = strdup(env->GetStringUTFChars(str, NULL));
    if(pDmEnv != NULL)
    {
        pDmEnv->vl->doCmd(Vold::MOUNT_ISO_CMD, (void*)isoFilePath);
    }
}


/*
 * Class:     com_mediatek_dm_DeviceManager
  * Method:    nativeMountISOex
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_mediatek_dm_DMServer_nativeMountISOex
  (JNIEnv* env, jclass clazz, jstring strpath, jstring strlabel)
{
    char* isoFilePath = strdup(env->GetStringUTFChars(strpath, NULL));
    char* isoLabel = strdup(env->GetStringUTFChars(strlabel, NULL));
    if(pDmEnv != NULL)
    {
        struct mountiso_ex_param param;
        memset(&param, 0, sizeof(struct mountiso_ex_param));
        param.isoFilePath = isoFilePath;
        param.isoLabel = isoLabel;
        pDmEnv->vl->doCmd(Vold::MOUNT_ISO_EX_CMD, (void*)&param);
    }
}

/*
 * Class:     com_mediatek_dm_DeviceManager
 * Method:    nativeUmountISO
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_mediatek_dm_DMServer_nativeUmountISO
  (JNIEnv* env, jclass clazz, jstring str)
{
    char* isoMountPath = strdup(env->GetStringUTFChars(str, NULL));
    if(pDmEnv != NULL)
    {
        pDmEnv->vl->doCmd(Vold::UMOUNT_ISO_CMD, (void*)isoMountPath);
    }
}

/*
 * Class:     com_mediatek_dm_DeviceManager
 * Method:    nativeStart
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_mediatek_dm_DMServer_nativeStart
  (JNIEnv* env, jclass clazz, jobject thiz)
{
    if(pDmEnv != NULL)
    {
        return;
    }
    
    pDmEnv = (DeviceManagerEnv*)malloc(sizeof(DeviceManagerEnv));
    memset(pDmEnv, 0, sizeof(DeviceManagerEnv));
    jint ret = env->GetJavaVM(&pDmEnv->vm);
    if(ret < 0)
    {
        return;
    }

    __android_log_print(ANDROID_LOG_DEBUG, DM_TAG, "class: %d\n", thiz);

    /*jclass DeviceManager = env->FindClass("com/mediatek/dm/DMServer");
    jmethodID getInstanceMethod = env->GetStaticMethodID(DeviceManager, "getInstance", "()Lcom/mediatek/dm/DMServer;");
    pDmEnv->dm = env->NewGlobalRef(env->CallStaticObjectMethod(DeviceManager, getInstanceMethod));
*/

    

    pDmEnv->dmCls = (jclass)env->NewGlobalRef(clazz);
    pDmEnv->dm = env->NewGlobalRef(thiz);
    
    jclass mntClass = env->FindClass("com/mediatek/dm/MountPoint");
    pDmEnv->mntCls = (jclass)env->NewGlobalRef(mntClass); 

    jclass devClass = env->FindClass("com/mediatek/dm/Device");
    pDmEnv->devCls = (jclass)env->NewGlobalRef(devClass); 

    jclass dmEventClass = env->FindClass("com/mediatek/dm/DeviceManagerEvent");
    pDmEnv->dmEventCls = (jclass)env->NewGlobalRef(dmEventClass); 
    
    pDmEnv->vl = new Vold();

    ret = pthread_create(&pDmEnv->thrd, NULL, voldThread, NULL);
    if(ret != 0)
    {
        __android_log_print(ANDROID_LOG_DEBUG, DM_TAG, "thread create fail\n");
    }
    //externalVolumeStartNotify();
}


/*
 * Class:     com_mediatek_dm_DeviceManager
 * Method:    nativeEnd
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_mediatek_dm_DMServer_nativeEnd
  (JNIEnv* env, jclass clazz)
{ 
    pDmEnv->vl->stop();  

    pthread_kill(pDmEnv->thrd, 0);
    
    env->DeleteGlobalRef(pDmEnv->dmCls);
    env->DeleteGlobalRef(pDmEnv->dm);
    env->DeleteGlobalRef(pDmEnv->dmEventCls);
    env->DeleteGlobalRef(pDmEnv->mntCls);
    env->DeleteGlobalRef(pDmEnv->devCls);
   
    delete pDmEnv->vl;

    memset(pDmEnv, 0, sizeof(DeviceManagerEnv));
    delete pDmEnv;
    pDmEnv = NULL;
}


void onEvent(int type, void* data)
{
    JNIEnv* env = NULL;
    jint ret = (pDmEnv->vm)->AttachCurrentThread((JNIEnv**)&env, NULL);

    #ifdef DM_DEBUG
    __android_log_print(ANDROID_LOG_DEBUG, DM_TAG, "event type: %d\n", type);
    #endif
    
    switch(type)
    {
    case ExternalVolumeInfo::VolumeMounted:
        {
            ExternalVolumeInfo* exVolInfo = (ExternalVolumeInfo*)data;
       
            jstring mountPoint = env->NewStringUTF(exVolInfo->mMountPoint);
            jstring deviceName = env->NewStringUTF(exVolInfo->mDeviceName);
            
            int s_len = strlen(exVolInfo->mVolumeLabel);
            jbyteArray volLabeldata = env->NewByteArray(s_len);
            env->SetByteArrayRegion(volLabeldata, 0, s_len, (const jbyte*)exVolInfo->mVolumeLabel);

            jfieldID eventField = env->GetStaticFieldID(pDmEnv->dmEventCls, "mounted", "I");
            jint event = env->GetStaticIntField(pDmEnv->dmEventCls, eventField);

            jmethodID cons = env->GetMethodID(pDmEnv->mntCls, "<init>", "(JJIIILjava/lang/String;Ljava/lang/String;Ljava/lang/Object;I)V");
            
            jobject mnt = env->NewObject(pDmEnv->mntCls, cons, (jlong)exVolInfo->mVolumeTotalSize, (jlong)exVolInfo->mVolumeFreeSize,
                                                           exVolInfo->mVolumeMajor, exVolInfo->mVolumeMinor, 0,
                                                           mountPoint, deviceName, volLabeldata, exVolInfo->mFileSystemType);
            
            jmethodID doEventMethod = env->GetMethodID(pDmEnv->dmCls, "onEvent", "(ILjava/lang/Object;)V");
            
        #ifdef DM_DEBUG 
            __android_log_print(ANDROID_LOG_DEBUG, DM_TAG, "mountPoint: %s\n", env->GetStringUTFChars(mountPoint, NULL));
            __android_log_print(ANDROID_LOG_DEBUG, DM_TAG, "deviceName: %s\n", env->GetStringUTFChars(deviceName, NULL));
            __android_log_print(ANDROID_LOG_DEBUG, DM_TAG, "total size: %d\n", exVolInfo->mVolumeTotalSize);
            __android_log_print(ANDROID_LOG_DEBUG, DM_TAG, "free size: %d\n", exVolInfo->mVolumeFreeSize);
            __android_log_print(ANDROID_LOG_DEBUG, DM_TAG, "major: %d\n", exVolInfo->mVolumeMajor);
            __android_log_print(ANDROID_LOG_DEBUG, DM_TAG, "minor: %d\n", exVolInfo->mVolumeMinor);
            __android_log_print(ANDROID_LOG_DEBUG, DM_TAG, "status: %d\n", exVolInfo->mVolumeStatus);
            __android_log_print(ANDROID_LOG_DEBUG, DM_TAG, "fileSystemType: %d\n", exVolInfo->mFileSystemType); 
        #endif
            
            env->CallVoidMethod(pDmEnv->dm, doEventMethod, event, mnt);   
           
            delete exVolInfo;
        }
        break;
    case ExternalVolumeInfo::VolumeUmounted:
        {
            char* str = (char*)data;
            jstring deviceName = env->NewStringUTF(str);

            jfieldID eventField = env->GetStaticFieldID(pDmEnv->dmEventCls, "umounted", "I");
            jint event = env->GetStaticIntField(pDmEnv->dmEventCls, eventField);

            jmethodID doEventMethod = env->GetMethodID(pDmEnv->dmCls, "onEvent", "(ILjava/lang/Object;)V");
            env->CallVoidMethod(pDmEnv->dm, doEventMethod, event, deviceName);   

            free(str);
        }
        break;
    case Device::DeviceConnected:
        {
            Device* devInfo = (Device*)data;
            jstring deviceName = env->NewStringUTF(devInfo->mDeviceName);

            jfieldID eventField = env->GetStaticFieldID(pDmEnv->dmEventCls, "connected", "I");
            jint event = env->GetStaticIntField(pDmEnv->dmEventCls, eventField);
            
            jmethodID cons = env->GetMethodID(pDmEnv->devCls, "<init>", "(IIILjava/lang/String;)V");           
            jobject dev = env->NewObject(pDmEnv->devCls, cons, devInfo->mMajor, devInfo->mMinor, event, deviceName);

            jmethodID doEventMethod = env->GetMethodID(pDmEnv->dmCls, "onEvent", "(ILjava/lang/Object;)V");
            env->CallVoidMethod(pDmEnv->dm, doEventMethod, event, dev);
            delete devInfo;
        }
        break;
    case Device::DeviceDisconnected:
        {
            char* str = (char*)data;
            jstring deviceName = env->NewStringUTF(str);

            jfieldID eventField = env->GetStaticFieldID(pDmEnv->dmEventCls, "disconnected", "I");
            jint event = env->GetStaticIntField(pDmEnv->dmEventCls, eventField);
       
            jmethodID doEventMethod = env->GetMethodID(pDmEnv->dmCls, "onEvent", "(ILjava/lang/Object;)V");
            env->CallVoidMethod(pDmEnv->dm, doEventMethod, event, deviceName);  
            delete str;
        }
        break;
    case Device::DeviceWifiConnected:
        {
            char* str = (char*)data;
            jstring wifiInterace = env->NewStringUTF(str);

            jfieldID eventField = env->GetStaticFieldID(pDmEnv->dmEventCls, "wificonnected", "I");
            jint event = env->GetStaticIntField(pDmEnv->dmEventCls, eventField);
       
            jmethodID doEventMethod = env->GetMethodID(pDmEnv->dmCls, "onEvent", "(ILjava/lang/Object;)V");
            env->CallVoidMethod(pDmEnv->dm, doEventMethod, event, wifiInterace);  
            delete str;
        }
        break;
    case Device::DeviceWifiDisconnected:
        {
            char* str = (char*)data;
            jstring wifiInterace = env->NewStringUTF(str);

            jfieldID eventField = env->GetStaticFieldID(pDmEnv->dmEventCls, "wifidisconnected", "I");
            jint event = env->GetStaticIntField(pDmEnv->dmEventCls, eventField);
       
            jmethodID doEventMethod = env->GetMethodID(pDmEnv->dmCls, "onEvent", "(ILjava/lang/Object;)V");
            env->CallVoidMethod(pDmEnv->dm, doEventMethod, event, wifiInterace);  
            delete str;
        }
        break;
     case Device::DeviceIsoMountFailed:
        {
            char* str = (char*)data;
            jstring mntpath = env->NewStringUTF(str);

            jfieldID eventField = env->GetStaticFieldID(pDmEnv->dmEventCls, "isomountfailed", "I");
            jint event = env->GetStaticIntField(pDmEnv->dmEventCls, eventField);
       
            jmethodID doEventMethod = env->GetMethodID(pDmEnv->dmCls, "onEvent", "(ILjava/lang/Object;)V");
            env->CallVoidMethod(pDmEnv->dm, doEventMethod, event, mntpath);  
            delete str; 
        }
        break;
    case ExternalVolumeInfo::VolumeUnsupported:
        break;
    default:
        break;
    }
}


static void* voldThread(void* data)
{
    pDmEnv->vl->start();
    while(true)
    {
        void* value = NULL;
        int event = pDmEnv->vl->get(&value);
        if(value != NULL)
        {
            onEvent(event, value);
        }
    }
    return NULL;
}
