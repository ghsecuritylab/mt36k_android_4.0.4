#include "tv_jni_util.h"
#include "com_mediatek_tv_service_TVNative.h"
#include "c_ci.h"
#include "ci/ci_service_wrapper.h"

EXTERN_C_START

#ifdef LOG_TAG
#undef LOG_TAG
#endif

#define LOG_TAG "ci_service"

#define CI_JNI_NEW_STRING(ENV,JSTR,SIZE)                            ((*ENV)->NewString(ENV,JSTR,SIZE))
#define CI_JNI_GET_STRING_CHARS(ENV,JSTR,ISCOPY)                    ((*ENV)->GetStringChars(ENV,JSTR,ISCOPY))
#define CI_JNI_GET_STRING_LEN(ENV,JSTR)                      ((*ENV)->GetStringLength(ENV,JSTR))
#define CI_JNI_RELEASE_STRING_CHARS(ENV,JSTR,CHARS)                 ((*ENV)->ReleaseStringChars(ENV,JSTR,CHARS))
#define CI_JNI_SET_INT_ARRAY_REGION(ENV,ARRAY,START,LEN,BUF)        ((*ENV)->SetIntArrayRegion(ENV,ARRAY,START,LEN,BUF))
#define CI_JNI_GET_JAVAVM(ENV)                                      ((*ENV)->GetJavaVM(ENV, &g_pt_jni_ci_JavaVM))
#define CI_JNI_ATTACH_CURRENT_THREAD(ENV)                         ((*g_pt_jni_ci_JavaVM)->AttachCurrentThread(g_pt_jni_ci_JavaVM, &ENV, NULL))
#define CI_JNI_DETACH_CURRENT_THREAD(ENV)                         ((*g_pt_jni_ci_JavaVM)->DetachCurrentThread(g_pt_jni_ci_JavaVM))
#define CI_JNI_NEW_OBJECT_ARRAY(ENV,LEN,ELEMTYPE,INITVALUE)         ((*ENV)->NewObjectArray(ENV,LEN,ELEMTYPE,INITVALUE))
#define CI_JNI_SET_OBJECT_ARRAY_ELEMENT(ENV,ARRAY,INDEX,ELEMENT)       ((*ENV)->SetObjectArrayElement(ENV,ARRAY,INDEX,ELEMENT))
#define CI_JNI_GET_INT_ARRAY_REGION(ENV,ARRAY,START,LEN,BUF)        ((*ENV)->GetIntArrayRegion(ENV,ARRAY,START,LEN,BUF))

static JNIEnv *g_pt_jni_ci_env;
static JavaVM *g_pt_jni_ci_JavaVM;

static SIZE_T _getStrSize(UTF16_T* wstr)
{
  UTF16_T* pt_str = wstr;
  SIZE_T size = 0;
  if(pt_str == NULL)
  {
     return size;
  }
  while( *pt_str != NULL)
  {
  
  JNI_LOGD(("%d,", (*pt_str)));
   size++;
   pt_str++;
  }  
  JNI_LOGD(("[%d]{%s},size: %d\n", __LINE__, __func__,size));
  return size;
}
 static jintArray getCamSystemIDInfo
  (JNIEnv *env, jint slotId)
{
     INT32 i4_ret = 0;
     UINT16 sys_id_num = 0;
     SIZE_T size = sizeof(UINT16);
     UINT16 *asys_id = NULL;
     jintArray array = NULL;
     i4_ret = c_ci_get(CI_GET_TYPE_CA_SYSTEM_ID_NUM,(VOID*)((UINT32)slotId),(VOID*)&sys_id_num,(VOID*)&size);
     if (i4_ret != CIR_OK)
     {
        JNI_LOGE(("{%s %d} c_ci_set(CI_GET_TYPE_CA_SYSTEM_ID_NUM) return %d", __FUNCTION__, __LINE__, i4_ret)); 
        return NULL;
     }
     
     JNI_LOGE(("{%s %d} c_ci_set(CI_GET_TYPE_CA_SYSTEM_ID_NUM) return NUM: %d", __FUNCTION__, __LINE__, sys_id_num)); 
     size = sys_id_num*sizeof(UINT16);
     asys_id = JNI_MALLOC(size);
     if(asys_id == NULL)
     {
        return NULL;
     }
     JNI_MEMSET(asys_id,0x0,size);
     i4_ret = c_ci_get(CI_GET_TYPE_CA_SYSTEM_ID,(VOID*)((UINT32)slotId),(VOID*)asys_id ,(VOID*)&size);
     if (i4_ret != CIR_OK)
     {
        JNI_LOGE(("{%s %d} c_ci_set(CI_GET_TYPE_CA_SYSTEM_ID_NUM) return %d", __FUNCTION__, __LINE__, i4_ret)); 
        return NULL;
     }
      JNI_LOGE(("{%s %d} c_ci_set(CI_GET_TYPE_CA_SYSTEM_ID_NUM) return sysid: %d", __FUNCTION__, __LINE__, (*asys_id))); 
  
     array = JNI_NEW_INT_ARRAY(env,(jsize)sys_id_num);
    if(array == NULL)
     {
        return NULL;
     }
     CI_JNI_SET_INT_ARRAY_REGION(env,array,0,sys_id_num,asys_id);
     
     JNI_MEMSET(asys_id,0x0,size);
     CI_JNI_GET_INT_ARRAY_REGION(env,array,0,sys_id_num,asys_id);
     JNI_LOGE(("{%s %d} c_ci_set(CI_GET_TYPE_CA_SYSTEM_ID_NUM) return sysid: %d", __FUNCTION__, __LINE__, (*asys_id))); 
     JNI_FREE(asys_id);
     
     JNI_LOGE(("{%s %d} return  %d", __FUNCTION__, __LINE__, i4_ret)); 
    
    return array;
}

static VOID _x_ci_nfy_fct(
    HANDLE_T            h_ci,
    VOID*               pv_nfy_tag,
    CI_NFY_COND_T       e_nfy_cond,
    VOID*               pv_data)
{
       JNIEnv *    env = g_pt_jni_ci_env;
       jclass       jclass_TVCallBack = NULL;
       UINT32 slotId = 0;
       INT32 ret = 0;
       jint        withoutAttachDetach   = 0;        
       /*
        * Check if we're already one with the VM.
        */
       withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(env);
       if (!withoutAttachDetach)
       {
           ret = CI_JNI_ATTACH_CURRENT_THREAD(env);
           if (ret < 0)
           {
               JNI_LOGD(("[ERROR][%d]{%s}\n", __LINE__, __func__));
           }
       }
       else 
       {
           JNI_LOGD((LOG_TAG"[Warning][%d]{%s}We are already one with VM\n", __LINE__, __func__));
       }

       JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));
       switch(e_nfy_cond)
       {
        case CI_NFY_COND_CARD_INSERT:
        case CI_NFY_COND_CARD_NAME:
        case CI_NFY_COND_CARD_REMOVE:
        {
           slotId = (UINT32)pv_data;
           jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
           JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_camStatusUpdated),slotId,e_nfy_cond);
           break;
        }
        case CI_NFY_COND_CA_SYSTEM_ID_WAIT:
        case CI_NFY_COND_CA_SYSTEM_ID_READY:
        {
            slotId = (UINT32)pv_data;			
            jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
            UINT8 status = CA_SYSTEM_ID_WAIT;
            if(CI_NFY_COND_CA_SYSTEM_ID_READY == e_nfy_cond)
            {
                status = CA_SYSTEM_ID_READY;
			     UINT16 sys_id_num = 0;
			     SIZE_T size = sizeof(UINT16);
			     UINT16 *asys_id = NULL;
			     jintArray array = NULL;
			     ret = c_ci_get(CI_GET_TYPE_CA_SYSTEM_ID_NUM,(VOID*)((UINT32)slotId),(VOID*)&sys_id_num,(VOID*)&size);
			     if (ret != CIR_OK)
			     {
			        JNI_LOGE(("{%s %d} c_ci_set(CI_GET_TYPE_CA_SYSTEM_ID_NUM) return %d", __FUNCTION__, __LINE__, ret)); 
			        return NULL;
			     }
			     
			     JNI_LOGE(("{%s %d} c_ci_set(CI_GET_TYPE_CA_SYSTEM_ID_NUM) return NUM: %d", __FUNCTION__, __LINE__, sys_id_num)); 
			     size = sys_id_num*sizeof(UINT16);
			     asys_id = JNI_MALLOC(size);
			     if(asys_id == NULL)
			     {
			        return NULL;
			     }
			     JNI_MEMSET(asys_id,0x0,size);
			     ret = c_ci_get(CI_GET_TYPE_CA_SYSTEM_ID,(VOID*)((UINT32)slotId),(VOID*)asys_id ,(VOID*)&size);
			     if (ret != CIR_OK)
			     {
			        JNI_LOGE(("{%s %d} c_ci_set(CI_GET_TYPE_CA_SYSTEM_ID_INFO) return %d", __FUNCTION__, __LINE__, ret)); 
			        return NULL;
			     }
			      JNI_LOGE(("{%s %d} c_ci_set(CI_GET_TYPE_CA_SYSTEM_ID_INFO) return sysid: %d", __FUNCTION__, __LINE__, (*asys_id))); 
			  
			     array = JNI_NEW_INT_ARRAY(env,(jsize)sys_id_num);
			    if(array == NULL)
			     {
			        return NULL;
			     }
			     CI_JNI_SET_INT_ARRAY_REGION(env,array,0,sys_id_num,asys_id);
			     
			     JNI_MEMSET(asys_id,0x0,size);
			     CI_JNI_GET_INT_ARRAY_REGION(env,array,0,sys_id_num,asys_id);
			     JNI_LOGE(("{%s %d} c_ci_set(CI_GET_TYPE_CA_SYSTEM_ID_INFO) return sysid: %d", __FUNCTION__, __LINE__, (*asys_id))); 
			     
			     JNI_LOGE(("{%s %d} return  %d", __FUNCTION__, __LINE__, ret)); 
				//jintArray array = getCamSystemIDInfo(env,slotId);
				JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_camSystemIDInfoUpdated),slotId,array);
				
				JNI_FREE(asys_id);
            }
            JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_camSystemIDStatusUpdated),slotId,status);
            break;
        }
        case CI_NFY_COND_CA_SYSTEM_ID_MATCH:
        {
           CI_CA_SYSTEM_ID_MATCH_T* pt_match = (CI_CA_SYSTEM_ID_MATCH_T*)pv_data;
           slotId = pt_match->ui1_slot_id;
           UINT8 status = CA_SYSTEM_ID_NOT_MATCH;
           if(pt_match->b_match)
           {
               status = CA_SYSTEM_ID_MATCH;
           }
           jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
           JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_camSystemIDStatusUpdated),slotId,status);
           break;
        }
        case CI_NFY_COND_MMI_CLOSE:
        {
           CI_MMI_ENQ_CLOSE_T* pt_mmi_close = (CI_MMI_ENQ_CLOSE_T*)pv_data;
           slotId = pt_mmi_close->ui1_ci_slot;
           jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
           JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_camMMIClosed),slotId,pt_mmi_close->ui1_mmi_close_delay);
           break;
        }
        case CI_NFY_COND_HOST_TUNE:
        {
           CI_HC_TUNE_T* pt_hc_tune = (CI_HC_TUNE_T*)pv_data;
           slotId = pt_hc_tune->ui4_id;
           
           jclass hostTuneclass = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/HostControlTune");
           jmethodID consFunc = JNI_GET_CLASS_METHOD(env,hostTuneclass, "<init>", "(IIII)V");
           jobject tuneObj = JNI_NEW_OBJECTV(env,hostTuneclass, consFunc,pt_hc_tune->ui2_network_id,pt_hc_tune->ui2_orig_network_id,pt_hc_tune->ui2_ts_id,pt_hc_tune->ui2_service_id);
           jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
           JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_camHostControlTune),slotId,tuneObj);
           break;
        }
        case CI_NFY_COND_HOST_REPLACE:
        {
           CI_HC_REPLACE_T* pt_hc_replace = (CI_HC_REPLACE_T*)pv_data;
           slotId = pt_hc_replace->ui4_id;
           
           jclass hostReplaceclass = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/HostControlReplace");
           jmethodID consFunc = JNI_GET_CLASS_METHOD(env,hostReplaceclass, "<init>", "(BII)V");
           jobject tuneObj = JNI_NEW_OBJECTV(env,hostReplaceclass, consFunc,pt_hc_replace->ui1_ref,pt_hc_replace->ui2_replaced_pid,pt_hc_replace->ui2_replacement_pid);
           jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
           JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_camHostControlReplace),slotId,tuneObj);
           break;
        }
        case CI_NFY_COND_HOST_CLEAR_REPLACE:
        {
           CI_HC_CLR_REPLACE_T* pt_hc_clear_replace = (CI_HC_CLR_REPLACE_T*)pv_data;
           slotId = pt_hc_clear_replace->ui4_id;
           jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
           JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_camHostControlClearReplace),slotId,pt_hc_clear_replace->ui1_ref);
           break;
        }
        case CI_NFY_COND_MMI_ENQUIRY:
        {
           CI_MMI_ENQ_T* pt_enq = (CI_MMI_ENQ_T*)pv_data;
           slotId = 0;
           jsize size = _getStrSize(pt_enq->w2s_text);
           jstring jStr = CI_JNI_NEW_STRING(env,pt_enq->w2s_text,size);
           jclass enqClass = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/MMIEnq");
           jmethodID consFunc = JNI_GET_CLASS_METHOD(env,enqClass, "<init>", "(IBBLjava/lang/String;)V");
           jobject enqObj =JNI_NEW_OBJECTV(env,enqClass, consFunc,pt_enq->ui4_id,pt_enq->ui1_ans_txt_len,pt_enq->b_blind_ans,jStr);
           jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
           JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_camMMIEnqReceived),slotId,enqObj);
           break;
        }
        
        case CI_NFY_COND_MMI_MENU:
		case CI_NFY_COND_MMI_LIST:
        {
           CI_MMI_MENU_T* pt_menu = (CI_MMI_MENU_T*)pv_data;
           slotId = 0;
           JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));
           jsize size = _getStrSize(pt_menu->w2s_title);
           JNI_LOGD(("[%d]{%s} title size:%d\n", __LINE__, __func__,size));
           jstring jTitleStr = CI_JNI_NEW_STRING(env,pt_menu->w2s_title,size);           
           size = _getStrSize(pt_menu->w2s_subtitle);
           JNI_LOGD(("[%d]{%s} subtitle size:%d\n", __LINE__, __func__,size));
           jstring jSubtitleStr = CI_JNI_NEW_STRING(env,pt_menu->w2s_subtitle,size);
           JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));
           size = _getStrSize(pt_menu->w2s_bottom);
           JNI_LOGD(("[%d]{%s}w2s_bottom size%d\n", __LINE__, __func__,size));
           jstring jBottomStr = CI_JNI_NEW_STRING(env,pt_menu->w2s_bottom,size);           
           jclass strClass = JNI_GET_CLASS_BY_NAME(env,"java/lang/String");
           jobjectArray strArray= CI_JNI_NEW_OBJECT_ARRAY(env,pt_menu->ui1_choice_nb,strClass,NULL);
           jstring jItemStr = NULL;
           UINT32 i=0;
           JNI_LOGD(("[%d]{%s}pt_menu->ui1_choice_nb:%d\n", __LINE__, __func__,pt_menu->ui1_choice_nb));
		   if(pt_menu->ui1_choice_nb <= MAX_MENU_ITEMS)
		   {		   
	           for(;i<pt_menu->ui1_choice_nb;i++)
	           {
	              size = _getStrSize(pt_menu->t_list[i]);
	              jItemStr = CI_JNI_NEW_STRING(env,pt_menu->t_list[i],size); 
	              CI_JNI_SET_OBJECT_ARRAY_ELEMENT(env,strArray,i,jItemStr);              
	           }
		   }
           JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));
            jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
           JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_camMMIMenuReceived),slotId,pt_menu->ui4_id,pt_menu->ui1_choice_nb,jTitleStr,jSubtitleStr,jBottomStr,strArray);
           break;
        }
        default:
            break;
       }
      
       if (!withoutAttachDetach)
       {
           CI_JNI_DETACH_CURRENT_THREAD(env);
       }
}

void x_ci_service_init_jni (JNIEnv *env)
{
    INT32 i4_ret = 0;
    HANDLE_T h_ci;

      g_pt_jni_ci_env = env;
    if (CI_JNI_GET_JAVAVM(env) < 0)
    {
        JNI_LOGE(("[ERROR][%d]{%s}\n", __LINE__, __func__));
    }
    JNI_LOGE(("[%d]{%s} enter \n", __LINE__, __func__));
    
    i4_ret = c_ci_reg_nfy(&h_ci,
                        NULL,
                         NULL,                        
                        _x_ci_nfy_fct);
    if (i4_ret != CIR_OK)
    {
        JNI_LOGE(("{%s %d} c_ci_reg_nfy return %d", __FUNCTION__, __LINE__, i4_ret));        
    }
    
 }
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getSlotNum_1native
  (JNIEnv *env, jclass class)
{
  INT32 i4_ret = 0;
  UINT8 slot_num = 0;
  SIZE_T slot_num_len = sizeof(UINT8);
  c_ci_get(CI_GET_TYPE_SLOT_NUM,NULL,(VOID*)&slot_num,&slot_num_len);
  if (i4_ret != CIR_OK)
  {
     JNI_LOGE(("{%s %d} c_ci_get(CI_GET_TYPE_SLOT_NUM) return %d", __FUNCTION__, __LINE__, (int)i4_ret)); 
     slot_num = 0;
  }
  JNI_LOGE(("{%s %d} return slot num %d", __FUNCTION__, __LINE__, (int)slot_num)); 
  return (jint)slot_num;    
}

JNIEXPORT jboolean JNICALL Java_com_mediatek_tv_service_TVNative_isSlotActive_1native
  (JNIEnv *env, jclass class, jint slotId)
{
    INT32 i4_ret = 0;
    CI_SLOT_INFO_T t_slot_info;
    SIZE_T slot_info_len = sizeof(CI_SLOT_INFO_T);
    i4_ret = c_ci_get(CI_GET_TYPE_SLOT_INFO,(VOID*)((UINT32)slotId),(VOID*)&t_slot_info,&slot_info_len);
    if (i4_ret != CIR_OK)
    {
       JNI_LOGE(("{%s %d} c_ci_get(CI_GET_TYPE_SLOT_INFO) return %d", __FUNCTION__, __LINE__, (int)i4_ret)); 
    }
    JNI_LOGE(("{%s %d} return slot active %d", __FUNCTION__, __LINE__, t_slot_info.b_active)); 

   return (jboolean)t_slot_info.b_active;
}

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_enterMMI_1native
  (JNIEnv *env, jclass class, jint slotId)
{
    INT32 i4_ret = 0;
    i4_ret = c_ci_set(CI_SET_TYPE_ENTER_MENU,(VOID*)((UINT32)slotId),NULL);
    if (i4_ret != CIR_OK)
    {
       JNI_LOGE(("{%s %d} c_ci_set(CI_SET_TYPE_ENTER_MENU) return %d", __FUNCTION__, __LINE__, i4_ret)); 
    }
    JNI_LOGE(("{%s %d} return  %d", __FUNCTION__, __LINE__, (int)i4_ret)); 

   return (jint)i4_ret;

}
/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getCamName_native
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_mediatek_tv_service_TVNative_getCamName_1native
  (JNIEnv *env, jclass class, jint slotId)
{
     INT32 i4_ret = 0;
     SIZE_T size;
     UTF16_T* utf16_cam_name;
     jstring jNameStr;
         
     i4_ret = c_ci_get(CI_GET_TYPE_APP_NAME,(VOID*)((UINT32)slotId),NULL,(VOID*)&size);
     if (i4_ret != CIR_OK)
     {
        JNI_LOGE(("{%s %d} c_ci_set(CI_GET_TYPE_APP_NAME) return %d", __FUNCTION__, __LINE__, (int)i4_ret)); 
     }
     utf16_cam_name = JNI_MALLOC(size); 
     if(utf16_cam_name == NULL)
     {
        return NULL;
     } 
     JNI_MEMSET(utf16_cam_name,0x0,size);
     
     i4_ret = c_ci_get(CI_GET_TYPE_APP_NAME,(VOID*)((UINT32)slotId),(VOID*)(utf16_cam_name),(VOID*)&size);
     if (i4_ret != CIR_OK)
     {
        JNI_LOGE(("{%s %d} c_ci_set(CI_GET_TYPE_APP_NAME) return %d", __FUNCTION__, __LINE__, (int)i4_ret)); 
     }
     UINT8 i=0;
     for(;i<size/sizeof(UTF16_T);i++)
     {
     JNI_LOGE(("name char %d",*(utf16_cam_name+i))); 
     }
     jNameStr = CI_JNI_NEW_STRING(env,utf16_cam_name,((size/sizeof(UTF16_T))-1));
     JNI_LOGE(("{%s %d} return  %d", __FUNCTION__, __LINE__, (int)i4_ret)); 
     JNI_FREE(utf16_cam_name);
    return jNameStr;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    closeMMI_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_closeMMI_1native
  (JNIEnv *env, jclass class, jint slotId)
{
    INT32 i4_ret = 0;
    i4_ret = c_ci_set(CI_SET_TYPE_CLOSE,(VOID*)((UINT32)slotId),NULL);
    if (i4_ret != CIR_OK)
    {
       JNI_LOGE(("{%s %d} c_ci_set(CI_SET_TYPE_CLOSE) return %d", __FUNCTION__, __LINE__, i4_ret)); 
    }
    JNI_LOGE(("{%s %d} return  %d", __FUNCTION__, __LINE__, i4_ret)); 

   return (jint)i4_ret;

}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setMMIClosed_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setMMIClosed_1native
  (JNIEnv *env, jclass class, jint slotId)
{
     INT32 i4_ret = 0;
     i4_ret = c_ci_set(CI_SET_TYPE_NFY_COLSE_DONE,(VOID*)((UINT32)slotId),NULL);
     if (i4_ret != CIR_OK)
     {
        JNI_LOGE(("{%s %d} c_ci_set(CI_SET_TYPE_NFY_COLSE_DONE) return %d", __FUNCTION__, __LINE__, i4_ret)); 
     }
     JNI_LOGE(("{%s %d} return  %d", __FUNCTION__, __LINE__, i4_ret)); 
    
    return (jint)i4_ret;

}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    answerMMIMenu_native
 * Signature: (IIC)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_answerMMIMenu_1native
  (JNIEnv *env, jclass class, jint slotId, jint menuId, jchar menuItem)
{
    CI_MMI_MENU_ANS_T t_menu_ans;
    INT32 i4_ret = 0;
    t_menu_ans.ui4_id = (UINT32)menuId;
    t_menu_ans.ui1_answer = (UINT8)menuItem;
    i4_ret = c_ci_set(CI_SET_TYPE_MMI_MENU_ANS,NULL,(VOID*)((CI_MMI_MENU_ANS_T*)&t_menu_ans));
    if (i4_ret != CIR_OK)
    {
       JNI_LOGE(("{%s %d} c_ci_set(CI_SET_TYPE_MMI_MENU_ANS) return %d", __FUNCTION__, __LINE__, i4_ret)); 
    }
    JNI_LOGE(("{%s %d} return  %d", __FUNCTION__, __LINE__, i4_ret)); 
   
   return (jint)i4_ret;

}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    answerMMIEnq_native
 * Signature: (IZLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_answerMMIEnq_1native
  (JNIEnv *env, jclass class, jint slotId, jint enqId,jboolean bAnswer, jstring aswerText)
{
     CI_MMI_ENQ_ANS_T t_enq_ans;
	 UTF16_T* pt_str = NULL;
	 jboolean bcopy = JNI_TRUE;
     INT32 i4_ret = 0;
     t_enq_ans.ui4_id = (UINT32)enqId;
     t_enq_ans.b_answer = bAnswer;
     t_enq_ans.w2s_text = NULL;
	 pt_str = CI_JNI_GET_STRING_CHARS(env,aswerText,&bcopy);
	 jsize len = CI_JNI_GET_STRING_LEN(env,aswerText);
     JNI_LOGE(("{%s %d} string len  %d,w2stext:%x", __FUNCTION__, __LINE__, len,pt_str)); 
	 t_enq_ans.w2s_text = x_mem_alloc((len+1) * sizeof(UTF16_T));
     if(t_enq_ans.w2s_text == NULL)
 	 {
	    JNI_LOGE(("{%s %d} c_ci_set(CI_SET_TYPE_MMI_ENQ_ANS) return %d", __FUNCTION__, __LINE__, i4_ret)); 
		return CIR_NOT_ENOUGH_SPACE;
 	 }
	 x_memset(t_enq_ans.w2s_text,0,(len+1) * sizeof(UTF16_T));
	 x_memcpy(t_enq_ans.w2s_text,pt_str,len*sizeof(UTF16_T));
	 _getStrSize(t_enq_ans.w2s_text);
	 
     i4_ret = c_ci_set(CI_SET_TYPE_MMI_ENQ_ANS,NULL,(VOID*)((CI_MMI_MENU_ANS_T*)&t_enq_ans));
     if (i4_ret != CIR_OK)
     {
        JNI_LOGE(("{%s %d} c_ci_set(CI_SET_TYPE_MMI_ENQ_ANS) return %d", __FUNCTION__, __LINE__, i4_ret)); 
     }
	 x_mem_free(t_enq_ans.w2s_text);
	 CI_JNI_RELEASE_STRING_CHARS(env,aswerText,pt_str);
     JNI_LOGE(("{%s %d} return  %d", __FUNCTION__, __LINE__, i4_ret)); 
    return (jint)i4_ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    askRelease_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_askRelease_1native
  (JNIEnv *env, jclass class, jint slotId)
{
    INT32 i4_ret = 0;
    i4_ret = c_ci_set(CI_SET_TYPE_HC_ASK_RELEASE,(VOID*)((UINT32)slotId),NULL);
    if (i4_ret != CIR_OK)
    {
       JNI_LOGE(("{%s %d} c_ci_set(CI_SET_TYPE_HC_ASK_RELEASE) return %d", __FUNCTION__, __LINE__, i4_ret)); 
    }
    JNI_LOGE(("{%s %d} return  %d", __FUNCTION__, __LINE__, i4_ret)); 
   
   return (jint)i4_ret;

}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setCITsPath_native
 * Signature: (IZ)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setCITsPath_1native
  (JNIEnv *env, jclass class, jint slotId, jboolean bSwitch)
{
    INT32 i4_ret = 0;
    CI_SET_TYPE_CI_ON_OFF_T t_ts_on_off = CI_SET_TYPE_TS_OFF;
    if(bSwitch)
    {
        t_ts_on_off = CI_SET_TYPE_TS_ON;
    }
    i4_ret = c_ci_set(CI_SET_TYPE_TS_ON_OFF,(VOID*)((UINT32)t_ts_on_off),NULL);
    if (i4_ret != CIR_OK)
    {
       JNI_LOGE(("{%s %d} c_ci_set(CI_SET_TYPE_TS_ON_OFF) return %d", __FUNCTION__, __LINE__, i4_ret)); 
    }
    JNI_LOGE(("{%s %d} return  %d", __FUNCTION__, __LINE__, i4_ret)); 
   
   return (jint)i4_ret;

}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setCIInputDTVPath_native
 * Signature: (IZ)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setCIInputDTVPath_1native
  (JNIEnv *env, jclass class, jint slotId, jboolean bSwitch)
{
    INT32 i4_ret = 0;
    CI_SET_TYPE_INP_SRC_T t_ci_inp_src = CI_SET_TYPE_NON_DTV;
    if(bSwitch)
    {
        t_ci_inp_src = CI_SET_TYPE_DTV;
    }
    i4_ret = c_ci_set(CI_SET_TYPE_INP_SRC,(VOID*)((UINT32)t_ci_inp_src),NULL);
    if (i4_ret != CIR_OK)
    {
       JNI_LOGE(("{%s %d} c_ci_set(CI_SET_TYPE_CI_ON_OFF) return %d", __FUNCTION__, __LINE__, i4_ret)); 
    }
    JNI_LOGE(("{%s %d} return  %d", __FUNCTION__, __LINE__, i4_ret)); 
   
   return (jint)i4_ret;

}
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getTunedChannel_1native
  (JNIEnv *env, jclass class, jint svlId, jobject tuneObj)
{
    INT32                       i4_ret                    = 0;
	CI_HC_TUNE_T*               pt_ci_tune                = NULL;
	jclass                      jclass_DvbChannelInfo     = NULL;
    CHANNEL_INFO_T*             pt_channel_info               = NULL;
    jobject                     jobject_ChannelInfo       = NULL;
    jobject                     jobject_serviceName       = NULL;
    jobject                     jobject_shortName         = NULL;
    jbyteArray                  jbyteArray_privateData    = NULL;
	
	JNI_LOGE(("[ci]{%s %d} \n", __FUNCTION__, __LINE__)); 
	pt_ci_tune = JNI_MALLOC(sizeof(CI_HC_TUNE_T));
	
    JNI_MEMSET(pt_ci_tune,0x0,sizeof(CI_HC_TUNE_T));
    pt_ci_tune->ui4_id     =  (UINT16)0;
    pt_ci_tune->ui2_network_id	 = (UINT16)JNI_CALL_OBJECT_METHOD(env,tuneObj,Int,CLASS_METHOD_ID(HostControlTune,HostControlTune_getNetworkId));
    pt_ci_tune->ui2_orig_network_id  = (UINT16)JNI_CALL_OBJECT_METHOD(env,tuneObj,Int,CLASS_METHOD_ID(HostControlTune,HostControlTune_getOrigNetworkId));
	pt_ci_tune->ui2_ts_id = (UINT16)JNI_CALL_OBJECT_METHOD(env,tuneObj,Int,CLASS_METHOD_ID(HostControlTune,HostControlTune_getTSId));   
	pt_ci_tune->ui2_service_id	 = (UINT16)JNI_CALL_OBJECT_METHOD(env,tuneObj,Int,CLASS_METHOD_ID(HostControlTune,HostControlTune_getSvcId));
	JNI_LOGE(("[ci]{%s %d} after get funcions\n", __FUNCTION__, __LINE__)); 
	
	//malloc memory for channel list
    pt_channel_info = JNI_MALLOC(sizeof(CHANNEL_INFO_T));
    JNI_MEMSET(pt_channel_info,0x0,sizeof(CHANNEL_INFO_T));   
	
   i4_ret = a_get_tuned_channel_proxy((UINT16)svlId,pt_ci_tune, pt_channel_info);
   if (i4_ret != CIR_OK)
   {
      JNI_LOGE(("[ci]!ERROR{%s %d} a_get_tuned_channel_proxy return %d\n", __FUNCTION__, __LINE__, i4_ret)); 
	  
	  return (jint)i4_ret;
   }

   jclass_DvbChannelInfo = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/DvbChannelInfo");

	jobject_ChannelInfo  = JNI_NEW_OBJECTV(env,jclass_DvbChannelInfo,
											CMI(DvbChannelInfo,DvbChannelInfo_init),
											svlId,(UINT32)pt_channel_info->ui2_svl_rec_id );

   JNI_LOGE(("[ci]From MW:tvSys=%lx audioSys=%lx colorSys=%lx frequency=%lx serviceName=%s "
		"svlId=%lx svlRecId=%lx channelId=%lx channelNumber=%lx brdcstMedium=%d ai1_private_data[0]=%d %d %d "
		"b_no_auto_fine_tune=%lx \n"
		LOG_TAIL,
		/*jobject_channelInfo,*/
		pt_channel_info->ui4_tv_sys,
		pt_channel_info->ui4_audio_sys,
		(UINT32)pt_channel_info->e_vid_color_sys,
		pt_channel_info->ui4_freq,
		pt_channel_info->ac_name,
		(UINT32)pt_channel_info->ui2_svl_id,
		(UINT32)pt_channel_info->ui2_svl_rec_id,
		pt_channel_info->ui4_channel_id,
		pt_channel_info->ui4_channel_number,
		pt_channel_info->ui1_brdcst_medium,
		pt_channel_info->ai1_private_data[0],
		pt_channel_info->ai1_private_data[1],
		pt_channel_info->ai1_private_data[2],
		(UINT32)pt_channel_info->b_no_auto_fine_tune
		));
   /*ChannelInfo common part start*/
   JCOMV(env,jobject_ChannelInfo	  ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setChannelId),   
		 pt_channel_info->ui4_channel_id	  );
   JCOMV(env,jobject_ChannelInfo	  ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setChannelNumber),   
		 pt_channel_info->ui4_channel_number	  );
   
   JNI_LOGE(("[ci]{%s %d} AFTER DvbChannelInfo_setServiceName:\n", __FUNCTION__, __LINE__)); 

   /*Service name*/
   if (strlen(pt_channel_info->ac_name) > 0)
   {
	   jobject_serviceName = JNI_NEW_STRING_UTF(env,pt_channel_info->ac_name);
	   if (jobject_serviceName != NULL)
	   {
		   JCOMV(env,jobject_ChannelInfo	  ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setServiceName),	
				 jobject_serviceName				 );
		   JNI_DEL_LOCAL_REF(env,jobject_serviceName);
	   }
   }

   JNI_LOGE(("[ci]{%s %d} DvbChannelInfo_setPrivateData:\n", __FUNCTION__, __LINE__)); 

   //Set private data
   jbyteArray_privateData = JNI_NEW_BYTE_ARRAY(env,PRIVATE_DATA_LEN);
   JNI_SET_BYTE_ARRAY_REGION(env,jbyteArray_privateData,0,PRIVATE_DATA_LEN,((jbyte*)(pt_channel_info->ai1_private_data)) );
   JCOMV(env,jobject_ChannelInfo	  ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setPrivateData),	
		 jbyteArray_privateData 				  );
   JNI_DEL_LOCAL_REF(env,jbyteArray_privateData);



   /*Short name*/
   if (strlen(pt_channel_info->short_name) > 0)
   {
	   jobject_shortName = JNI_NEW_STRING_UTF(env,pt_channel_info->ac_name);
	   if (jobject_shortName != NULL)
	   {
		   JCOMV(env,jobject_ChannelInfo	  ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setShortName),   
				 jobject_shortName				   );
		   JNI_DEL_LOCAL_REF(env,jobject_shortName);
	   }
   }

   /*Frequency*/
   JCOMV(env,jobject_ChannelInfo	  ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setFrequency),   
		 pt_channel_info->ui4_freq	);

   JNI_LOGE(("[ci]{%s %d} HostControlTune_setTunedChannel\n", __FUNCTION__, __LINE__)); 

   JCOMV(env,tuneObj	  ,Void,   CMI(HostControlTune,HostControlTune_setTunedChannel),  jobject_ChannelInfo);
   
   JNI_FREE(pt_ci_tune);
   
   JNI_FREE(pt_channel_info);
   JNI_LOGE(("[ci]{%s %d}return %d\n", __FUNCTION__, __LINE__,i4_ret)); 

   return (jint)i4_ret;

}

EXTERN_C_END

