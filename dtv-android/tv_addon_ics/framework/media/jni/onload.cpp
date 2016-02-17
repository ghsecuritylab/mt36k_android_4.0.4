#include <stdio.h>
#include <jni.h>
#include <android/log.h>

#ifdef __cplusplus
extern "C" {
#endif 

JNIEnv *g__env;
JavaVM *g__JavaVM;

JNIEXPORT jint JNICALL  JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;
    g__JavaVM = vm; 
    #ifdef DEBUG_LOG
    __android_log_print(ANDROID_LOG_DEBUG, "JNI_OnLoad", "####  g__JavaVM[X] = %x", g__JavaVM);
    #endif
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        g__env = env;
        return result;
    } 
    #ifdef DEBUG_LOG
      __android_log_print(ANDROID_LOG_DEBUG, "JNI_OnLoad", "####  JNI_VERSION_1_4");
    #endif
    return JNI_VERSION_1_4;
}

#ifdef __cplusplus

}
#endif
