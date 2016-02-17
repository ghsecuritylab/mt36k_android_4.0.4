#include <list>
#include <string>
#include "dlna/EventManager.h"

#ifdef __cplusplus
extern "C" {
#endif

#include <android/log.h>
#include <jni.h>
#include <dlfcn.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/time.h>
#include <dirent.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "x_dlna_dmp_api.h"

 //static JavaVM * global_vm = NULL;
 static jobject  global_dmp = NULL;

VOID dlna_dmp_device_nfy (DLNA_DEVICE_EVENT_T t_event,
		VOID * pv_tag,
		VOID * pv_arg)
{
    int result = 0;

    DLNA_DEVICE_TYPE_T type ;
    result = x_dlna_dmp_get_device_type((DLNA_DMP_DEVICE_T)pv_arg, &type);
    if (result != DLNAR_SUCCESS)
    {
        return ;
    }
    if (DLNA_DEVICE_TYPE_DMS != type)
    {
        return ;
    }

    char * name = NULL;
    x_dlna_dmp_get_device_name((DLNA_DMP_DEVICE_T)pv_arg, &name);
    if (name == NULL)
    {
        return ;
    }

    jobject  dmp = global_dmp;

    if(dmp == NULL) {
        return ;
    }

	__android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####device notify %d and id is %d ", t_event, (int)pv_arg);
    dlna::DeviceEvent * event = new dlna::DeviceEvent(dmp, t_event, (DLNA_DMP_DEVICE_T)pv_arg, name);
    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####device new event %x", (int)event);
    dlna::EventManager::send(event);
    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####device send event ");
}

JNIEXPORT jobject JNICALL Java_com_mediatek_dlna_DigitalMediaPlayer_nativeEvent(JNIEnv *env, jobject thiz)
{
    dlna::Event * event = dlna::EventManager::recv();
    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####device recv event %x ", (int)event);
    jobject obj = dlna::EventManager::build(env, event);
    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####device build event %x ", (int)obj);
    return obj;
}

JNIEXPORT jint JNICALL Java_com_mediatek_dlna_DigitalMediaPlayer_nativeStart(JNIEnv *env, jobject thiz)
{
    //env->GetJavaVM(&global_vm);
    global_dmp = thiz;//env->NewGlobalRef(thiz);

	__android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Start dmp");

	int ret = x_dlna_dmp_start(dlna_dmp_device_nfy, NULL);

	__android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "End to start %d ", ret);

    return ret;
}

JNIEXPORT jint JNICALL Java_com_mediatek_dlna_DigitalMediaPlayer_nativeStop(JNIEnv *env, jobject thiz)
{
	__android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Stop dmp");

	int ret = x_dlna_dmp_stop();

	__android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "End to stop");

    //env->DeleteGlobalRef(global_dmp);
    //global_dmp = NULL;

    return ret;
}

JNIEXPORT void JNICALL Java_com_mediatek_dlna_DigitalMediaPlayer_nativeExit(JNIEnv *env, jobject thiz)
{
	__android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "exit dmp");

    dlna::ExitEvent * event = new dlna::ExitEvent(global_dmp);
    dlna::EventManager::send(event);

	global_dmp = NULL;

	__android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "End to exit");
}

#ifdef __cplusplus
}
#endif