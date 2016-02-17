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

#include "x_dlna_dmp_api.h"
#include "dlna/EventManager.h"

    struct BrowseTag {
        jobject content;

        BrowseTag(jobject content);
        ~BrowseTag();
    };

    BrowseTag::BrowseTag( jobject content )
        : content(content)
    {
    }

    BrowseTag::~BrowseTag()
    {
    }

VOID x_dlna_dmp_browse_nfy (DLNA_DMP_OBJECT_EVENT_T t_event,
        VOID * pv_tag,
        VOID * pv_arg)
{
    BrowseTag * tag = (BrowseTag *)pv_tag;
    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "x_dlna_dmp_browse_nfy");

    dlna::ContentEvent * event = new dlna::ContentEvent(tag->content, t_event, (DLNA_DMP_OBJECT_INFO_T*)pv_arg);

    dlna::EventManager::send(event);

    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "x_dlna_dmp_browse_nfy send event");

    delete tag;
    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "x_dlna_dmp_browse_nfy end");
}

JNIEXPORT jint JNICALL Java_com_mediatek_dlna_object_MediaServer_nativeBrowse(JNIEnv *env,
    jobject thiz,
    jint    id,
    jstring objectId,
    jint flag,
    jint startIndex,
    jint request,
    jstring filter,
    jstring sort)
{
	__android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Start browse %d ", id);

	int ret = 0;
    int handle = 0;

    const char * _objectId = env->GetStringUTFChars(objectId, NULL);
    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Object Id is : %s", _objectId);
    const char * _filter = env->GetStringUTFChars(filter, NULL);
    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Filter is : %s", _filter);
    const char * _sort = env->GetStringUTFChars(sort, NULL);
    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Sort is : %s", _sort);

    jobject source = env->NewGlobalRef(thiz);
    BrowseTag * tag = new BrowseTag(source);

    ret = x_dlna_dmp_browse_object_async((DLNA_DMP_DEVICE_T)id, (CHAR *)_objectId,
        (DLNA_DMP_BROWSE_FLAG_T)flag, startIndex, request, (CHAR *)_filter, (CHAR *)_sort,
        x_dlna_dmp_browse_nfy, tag, (HANDLE_T*)&handle);

    env->ReleaseStringUTFChars(objectId, _objectId);
    env->ReleaseStringUTFChars(filter, _filter);
    env->ReleaseStringUTFChars(sort, _sort);

	__android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "End to browse %d ", ret);

    return handle;
}

JNIEXPORT void JNICALL Java_com_mediatek_dlna_object_MediaServer_nativeCancel(JNIEnv *env, jobject thiz, jint id, jint handle)
{
	__android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "Start cancel %d ", handle);

    x_dlna_dmp_cancel_object((DLNA_DMP_DEVICE_T)id, NULL, (HANDLE_T)handle);

	__android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "End to cancel");
}

#ifdef __cplusplus
}
#endif