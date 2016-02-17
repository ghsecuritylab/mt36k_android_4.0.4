#include "tv_jni_util.h"
#include "com_mediatek_tv_service_TVNative.h"

#include "comp/comp_service_client.h"

EXTERN_C_START
#ifdef TAG
#undef TAG
#endif
#define TAG "[COMP-JNI] "

static void
_pf_jni_comp_nfy (const char *msg_type,
                     VOID*       pv_tag)
{
    int ret;
    jstring jstr;
     
    jclass      jclass_TVCallBack  = NULL;
    jint        withoutAttachDetach   = 0;

    JNI_LOGD((" _pf_jni_comp_nfy -[%s]", msg_type));
    JNI_LOGD(("g__env=%x g__JavaVM=%x\r\n",g__env,g__JavaVM ));         
    
    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(g__env);
    if (!withoutAttachDetach)
    {
        ret = JNI_ATTACH_CURRENT_THREAD(g__env); 
        if (ret < 0)
        {
            JNI_LOGD(("[ERROR][%d]{%s}\n", __LINE__, __func__));
        }
    }
    else 
    {
        JNI_LOGD(("[Warning][%d]{%s}We are already one with VM\n", __LINE__,__func__));
    }  
   
    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(g__env,"com/mediatek/tv/service/TVCallBack");
    jstr = JNI_NEW_STRING_UTF(g__env, msg_type);

    if(jstr != NULL)
    {
        JNI_CALL_STATIC_METHODV(g__env,jclass_TVCallBack,Void,CMI(TVCallBack_def,TVCallBack_compServiceNotifyInfo),jstr);
        JNI_DEL_LOCAL_REF(g__env,jstr);
    }

    if (!withoutAttachDetach)
    {
        JNI_DETACH_CURRENT_THREAD(g__env);
    }

    JNI_LOGD((" _pf_jni_compt_nfy -[%s]- end", msg_type));
}

void jni_comp_init(JNIEnv *env)
{
    JNI_LOGD((" initComponentService begin\n")); 

    a_comp_set_nfy(_pf_jni_comp_nfy, NULL);    
}

 /*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    activateComponent_native
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_activateComponent_1native
  (JNIEnv *env, jclass clazz, jstring comp_name)
{
    INT32  i4_ret           = 0;
    const CHAR* s_comp_name = NULL;
    
    JNI_LOGD((" Enter [%s]@L[%d] \n", __func__, __LINE__));
    if (comp_name != NULL)
    {
        s_comp_name = JNI_GET_STRING_UTF_CHARS(env, comp_name);
        if (s_comp_name != NULL){
            i4_ret = a_comp_activatecomponent(s_comp_name);    
            if(COMPR_OK == i4_ret)
            {
                JNI_LOGD((" Leave [%s]@L[%d] successfully \n", __func__, __LINE__));
            }            
        }  

        JNI_RELEASE_STRING_UTF_CHARS(env,(jstring)comp_name, s_comp_name);
    }

    JNI_LOGD((" Leave [%s]@L[%d] Fail\n", __func__, __LINE__));
        
    return (jint)i4_ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    inactivateComponent_native
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_inactivateComponent_1native
  (JNIEnv *env, jclass clazz, jstring comp_name)                        
{
   INT32  i4_ret           = 0;
    const CHAR* s_comp_name = NULL;
    
    JNI_LOGD((" Enter [%s]@L[%d] \n", __func__, __LINE__));
    if (comp_name != NULL)
    {
        s_comp_name = JNI_GET_STRING_UTF_CHARS(env, comp_name);
        if (s_comp_name != NULL){
            i4_ret = a_comp_inactivatecomponent(s_comp_name);    
            if(COMPR_OK == i4_ret)
            {
                JNI_LOGD((" Leave [%s]@L[%d] successfully \n", __func__, __LINE__));
            }            
        }  

        JNI_RELEASE_STRING_UTF_CHARS(env,(jstring)comp_name, s_comp_name);
    }

    JNI_LOGD((" Leave [%s]@L[%d] Fail\n", __func__, __LINE__));
        
    return (jint)i4_ret;
}
/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setStatus_native
 * Signature: (I)I
 */

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_updateSysStatus_1native
  (JNIEnv *env, jclass clazz, jstring Statustype)
{
   INT32  i4_ret             = 0;
   const CHAR* s_status_type = NULL;
    
    JNI_LOGD((" Enter [%s]@L[%d] \n", __func__, __LINE__));
    if (Statustype != NULL)
    {
        s_status_type = JNI_GET_STRING_UTF_CHARS(env, Statustype);
        if (s_status_type != NULL){
            i4_ret = a_comp_updateSysStatus(s_status_type);    
            if(COMPR_OK == i4_ret)
            {
                JNI_LOGD((" Leave [%s]@L[%d] successfully \n", __func__, __LINE__));
            }            
        }  

        JNI_RELEASE_STRING_UTF_CHARS(env,(jstring)Statustype, s_status_type);
    }

    JNI_LOGD((" Leave [%s]@L[%d] Fail\n", __func__, __LINE__));
        
    return (jint)i4_ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getStatus_native
 * Signature: (I)I
 */
JNIEXPORT jboolean JNICALL Java_com_mediatek_tv_service_TVNative_IsTTXAvail_1native
  (JNIEnv *env, jclass clazz)
{       
    JNI_LOGD((" Enter [%s]@L[%d] \n", __func__, __LINE__));

    BOOL fgIsTTXAvail = FALSE;

    a_comp_IsTTXAvail(&fgIsTTXAvail);
    
   JNI_LOGD((" Leave [%s]@L[%d] Fail\n", __func__, __LINE__));
            
   return (jboolean)fgIsTTXAvail;

}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    sendKeyEvent_native
 * Signature: (IIZ)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_sendkeyEventtoComp_1native
  (JNIEnv *env, jclass clazz,jint iKeycode, jint ikeyevent)
{
   INT32 i4_ret = 0;

   JNI_LOGD((" Enter [%s]@L[%d] \n", __func__, __LINE__));
   i4_ret = a_comp_sendkeyEvent(iKeycode,ikeyevent);

   if(COMPR_OK == i4_ret){
     JNI_LOGD((" Enter [%s]@L[%d] Successfully\n", __func__, __LINE__));   
  }
  
  JNI_LOGD((" Enter [%s]@L[%d] Failed Ret = %d\n", __func__, __LINE__, i4_ret));  

  return (jint)i4_ret;
   
}
EXTERN_C_END
