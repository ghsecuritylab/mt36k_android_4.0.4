#include "tv_jni_util.h"
#include "com_mediatek_tv_service_TVNative.h"
#include "start/mtk_dtv_svc.h"

EXTERN_C_START
#ifdef TAG
#undef TAG
#endif
#define TAG "[OSD-JNI] "

#ifndef true
#define true 1
#endif

#ifndef false
#define false 0
#endif

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setOSDColorKey_native
 * Signature: (ZI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_mediatek_tv_service_TVNative_setOSDColorKey_1native
  (JNIEnv *env, jclass clazz, jboolean b_enable, jint color)
{
    int res;

    res = _tv_svc_set_colorkey (b_enable, color);

    JNI_LOGI(("%s %s-%d: status %d, color key %x %s",
            TAG, __FUNCTION__, __LINE__, res, (int) color,
            (b_enable ? "enabled" : "disabled")));

    return ((res == 0) ? true : false);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setOSDOpacity_native
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_mediatek_tv_service_TVNative_setOSDOpacity_1native
  (JNIEnv *env, jclass clazz, jint opacity)
{
    int res;

    res = _tv_svc_set_opacity (opacity);

    JNI_LOGI(("%s %s-%d: status %d, opacity %x",
            TAG, __FUNCTION__, __LINE__, res, (int) opacity));

    return ((res == 0) ? true : false);
}

EXTERN_C_END
