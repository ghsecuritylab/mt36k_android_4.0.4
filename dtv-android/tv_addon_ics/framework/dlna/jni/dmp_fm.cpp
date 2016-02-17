#include <list>
#include <string>

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

#include "x_dlna_dmp_fm.h"    

static const int DMS_BUSY = -2;
static const bool LOGGER = false;

    JNIEXPORT jint JNICALL Java_com_mediatek_dlna_object_ContentInputStream_nativeOpen(JNIEnv *env,
        jobject thiz,
        jstring uri, jstring mimeType, jstring dtcpInfo,
        jlong size, jint flag,
        jint mediaType,
        jint drmType, jbyteArray sessionId)
    {
        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Start Open ");

        char * resUri = NULL;
        if (uri != NULL)
        {
            resUri = (char*)env->GetStringUTFChars(uri, NULL);
            __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Res URI is : %s", resUri);
        }

        char * resMimeType = NULL;
        if (mimeType != NULL)
        {
            resMimeType = (char*)env->GetStringUTFChars(mimeType, NULL);
            __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "MIME Type is : %s", resMimeType);
        }

        char * resDtcp = NULL;
        if (dtcpInfo != NULL)
        {
            resDtcp = (char*)env->GetStringUTFChars(dtcpInfo, NULL);
            __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "DTCP is : %s", resDtcp);
        }

        DLNA_DMP_OBJECT_FM_T * fm = (DLNA_DMP_OBJECT_FM_T*)malloc(sizeof(DLNA_DMP_OBJECT_FM_T));
        memset(fm, 0, sizeof(DLNA_DMP_OBJECT_FM_T));

        jsize length = env->GetArrayLength(sessionId);

        fm->i4_flag = flag;
        fm->i4_media_type = mediaType;
        fm->i4_drm_type = drmType;
        fm->ui8_size = size;
        fm->ui4_id_length = length;
        if (resUri != NULL)
        {
            fm->ps_uri = strdup(resUri);
        }
        if (resMimeType != NULL)
        {
            fm->ps_mime = strdup(resMimeType);
        }

        if (resDtcp != NULL)
        {
            fm->ps_dtcp_info = strdup(resDtcp);
        }
        if (length != 0)
        {
            jbyte * sid = env->GetByteArrayElements(sessionId, NULL);
            fm->pui1_session_id = (UINT8*)malloc(sizeof(UINT8) * length);
            memcpy(fm->pui1_session_id, sid, length);
            env->ReleaseByteArrayElements(sessionId, sid, 0);
        }

        if (uri != NULL)
        env->ReleaseStringUTFChars(uri, resUri);
        if (mimeType != NULL)
        env->ReleaseStringUTFChars(mimeType, resMimeType);
        if (dtcpInfo != NULL)
        env->ReleaseStringUTFChars(dtcpInfo, resDtcp);

        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Size is %lld", size);
        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Flag is : %08x", flag);

        int handle = 0;

        x_dlna_dmp_fm_open_by_info(fm, (INT32*)&handle);

        x_dlna_dmp_fm_free_info(&fm);

        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "End to Open and return handle %d ", handle);

        return (jint)handle;
    }

    JNIEXPORT jint JNICALL Java_com_mediatek_dlna_object_ContentInputStream_nativeRead(JNIEnv *env, jobject thiz, jint handle, jbyteArray buffer, jint off, jint len)
    {
        if(LOGGER) __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Start read handle %d off %d len %d", handle , off, len);
        
        jbyte * elements = env->GetByteArrayElements(buffer, NULL);
        UINT32 ui4_read;

        if(LOGGER) __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Start x_dlna_dmp_fm_read");

        int ret = x_dlna_dmp_fm_read(handle, elements+off, len, &ui4_read);
        if (ret != DLNA_FMR_OK)
        {
            env->ReleaseByteArrayElements(buffer, elements, 0);
            if ((ret == DLNA_FMR_BUSY) || (ret == DLNA_FMR_DEVICE_ERROR) || (ret == DLNA_FMR_EOF)|| (ret == DLNA_FMR_TIMEOUT))
            {
                __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "End read fail and return %d must retry", ret);
                return DMS_BUSY;
            }
            
            __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "End read fail and return %d", ret);
            return -1;
        }

        if(LOGGER) __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "End x_dlna_dmp_fm_read");

        env->SetByteArrayRegion(buffer, off, len, (const jbyte*)elements);

        env->ReleaseByteArrayElements(buffer, elements, 0);

        if(LOGGER) __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "End read");
        return (int)ui4_read;
    }

    JNIEXPORT jlong JNICALL Java_com_mediatek_dlna_object_ContentInputStream_nativeSkip(JNIEnv *env, jobject thiz,jint handle, jlong skip)
    {
        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Start handle %d skip %lld", handle, skip);

        jlong current = 0;

        x_dlna_dmp_fm_seek(handle, skip, FM_SEEK_CUR, (UINT64*)&current);

        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "End to Skip current %lld", current);
        return current;
    }

    JNIEXPORT jint JNICALL Java_com_mediatek_dlna_object_ContentInputStream_nativeClose(JNIEnv *env, jobject thiz, jint handle)
    {
        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Start Close %d ", handle);

        x_dlna_dmp_fm_close(handle);

        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "End Close");
        return 0;
    }

#ifdef __cplusplus
}
#endif