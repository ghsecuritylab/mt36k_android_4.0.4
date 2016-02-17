#include "tv_jni_util.h"
#ifndef NATIVE_DEBUG
#include "start/mtk_dtv_svc.h"
#endif


EXTERN_C_START

#define LOG_TAG "onload"

volatile int jni_dbg_level = 0;

void
_pf_jni_tv_svc_status (const char    *msg,
                       int           status,
                       int           data1,
                       int           data2)
{
    return;
}
void _pf_apply_jni_dbg_lvl(int dbg_lvl, void* pv_tag)
{

    jclass      jclass_TVCallBack   = NULL;
    int         ret                 = 0;
    
    jni_dbg_level                   = dbg_lvl;
    
    JNI_LOGD(("g__env=%x g__JavaVM=%x\r\n",g__env,g__JavaVM ));
    ret = JNI_ATTACH_CURRENT_THREAD(g__env);
    if (ret < 0)
    {   
        JNI_LOGD(("LOG_TAG[ERROR][%d]{%s}\n", __LINE__, __func__)); 
    }

    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(g__env,"com/mediatek/tv/service/TVCallBack");
    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));
    //Notify java
    JNI_CALL_STATIC_METHODV(g__env,jclass_TVCallBack,Void,CMI(TVCallBack_def,TVCallBack_configServiceNotifyDbgLevel),dbg_lvl);
    JNI_DETACH_CURRENT_THREAD(g__env);

}


JNIEnv *g__env;
JavaVM *g__JavaVM;


JNIEXPORT jint JNICALL  JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;
    g__JavaVM = vm;
#ifdef __cplusplus
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }
#else
    if ((*vm)->GetEnv(vm,(void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }
#endif
    g__env = env;
#ifndef   NATIVE_DEBUG
    _tv_svc_init_client(_pf_jni_tv_svc_status, _pf_apply_jni_dbg_lvl);
#endif
    /*register */
    //LOG(("Register JNI Class\r\n"));
    LOGD("Register JNI CLass" LOG_TAIL);
    init_jni_def(env);

    /* init broadcast service JNI */
    jni_brdcst_svc_init(env);
    x_channel_service_init();
    x_event_service_init();

    x_scan_service_init_jni(env);
    x_input_service_init_jni(env);
	x_scan_service_dvbc_init_jni(env);
    x_scan_service_dtmb_init_jni(env);
//#ifdef DVBT_CI_ENABLE
    x_ci_service_init_jni(env);
//#endif

    jni_comp_init(env);

    return JNI_VERSION_1_4;
}

EXTERN_C_END
