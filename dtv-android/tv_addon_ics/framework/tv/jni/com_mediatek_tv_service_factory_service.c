#include "tv_jni_util.h"
#include "com_mediatek_tv_service_TVNative.h"
#include "c_iom.h"


EXTERN_C_START

#ifdef LOG_TAG
#undef LOG_TAG
#endif
#define LOG_TAG "[FACTORY_SVC-JNI]"

static JavaVM *g_pt_jni_config_service_JavaVM = NULL;
static JNIEnv *g_pt_jni_config_service_env    = NULL;


#define JNI_GET_JAVAVM(ENV)                 ((*ENV)->GetJavaVM(ENV, &g_pt_jni_config_service_JavaVM))
#define JNI_ATTACH_CURRENT_THREAD(ENV)      ((*g_pt_jni_config_service_JavaVM)->AttachCurrentThread(g_pt_jni_config_service_JavaVM, &ENV, NULL))
#define JNI_DETACH_CURRENT_THREAD(ENV)      ((*g_pt_jni_config_service_JavaVM)->DetachCurrentThread(g_pt_jni_config_service_JavaVM))
#define JNI_NEW_GLOBAL_REF(ENV,CLASS)       ((*ENV)->NewGlobalRef(ENV,CLASS))


#if 1
static VOID _fact_mode_iom_nfy_fct(
                    VOID*                       pv_nfy_tag,
                    IOM_NFY_COND_T              e_nfy_cond,
                    UINT32                      ui4_evt_code,
                    UINT32                      ui4_data
                    )
{
    jclass      jclass_TVCallBack = NULL;
    int         ret;
    jint        withoutAttachDetach   = 0;
    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));

    if (NULL == g_pt_jni_config_service_JavaVM)
    {
        JNI_LOGE(("{%s %d}, g_pt_jni_config_service_JavaVM is NULL", __FUNCTION__, __LINE__));
    }

    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(g_pt_jni_config_service_env);
    if (!withoutAttachDetach)
    {
        ret = JNI_ATTACH_CURRENT_THREAD(g_pt_jni_config_service_env);
        if (ret < 0)
        {
            JNI_LOGD((LOG_TAG"[ERROR][%d]{%s}\n", __LINE__, __func__));
        }
    }
    else
    {
        JNI_LOGD((LOG_TAG"[Warning][%d]{%s}We are already one with VM\n", __LINE__, __func__));
    }

    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));

    switch (e_nfy_cond)
    {
        case IOM_NFY_COND_REC_UART_DATA:     
        {
            jbyteArray  jbyteArray_data;
            IOM_UART_T      *pt_iom_uart;
            JNI_LOGD(("{%s %d}, the e_nfy_cond is 0x%x\n", __FUNCTION__, __LINE__, e_nfy_cond));
            pt_iom_uart = (IOM_UART_T *)ui4_data;
            if (pt_iom_uart != NULL && pt_iom_uart->z_len != 0)
            {
                JNI_METHOD* jni_method_id;
                jbyteArray_data = JNI_NEW_INT_ARRAY(g_pt_jni_config_service_env, pt_iom_uart->z_len);                                        
                JNI_SET_INT_ARRAY_REGION(g_pt_jni_config_service_env, jbyteArray_data, 0, pt_iom_uart->z_len, pt_iom_uart->pui1_data);

                jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(g_pt_jni_config_service_env,"com/mediatek/tv/service/TVCallBack");
                JNI_CALL_STATIC_METHODV(g_pt_jni_config_service_env,jclass_TVCallBack,Void,CMI(TVCallBack_def,TVCallBack_onUARTSerialListener), pv_nfy_tag, e_nfy_cond, ui4_evt_code, jbyteArray_data);
                JNI_DEL_LOCAL_REF(g_pt_jni_config_service_env, jbyteArray_data);
            }
            break;
         }
        case IOM_NFY_COND_XMT_COMPLETE:
        {
            JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));
            jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(g_pt_jni_config_service_env,"com/mediatek/tv/service/TVCallBack");
            JNI_CALL_STATIC_METHODV(g_pt_jni_config_service_env,jclass_TVCallBack,Void,CMI(TVCallBack_def,TVCallBack_onUARTSerialListener), pv_nfy_tag, e_nfy_cond, 0, 0);
            JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));            
            break;
        }
        default:
            JNI_LOGD(("{%s %d}, the e_nfy_cond is 0x%x\n", __FUNCTION__, __LINE__, e_nfy_cond));
            break;
    }    
    
    if (!withoutAttachDetach)
    {
        JNI_DETACH_CURRENT_THREAD(g_pt_jni_config_service_env);
    }

    

    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));
}
#else
/*for debug*/
static VOID _fact_mode_iom_nfy_fct(
                    VOID*                       pv_nfy_tag,
                    IOM_NFY_COND_T              e_nfy_cond,
                    UINT32                      ui4_evt_code,
                    UINT32                      ui4_data
                    )
{
    INT32 i4_i;
    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));
    switch (e_nfy_cond)
    {
        case IOM_NFY_COND_REC_UART_DATA:
            {
                IOM_UART_T      *pt_iom_uart;
                pt_iom_uart = (IOM_UART_T *) ui4_data;

                if (pt_iom_uart != NULL)
                {
                    JNI_LOGD(("{%s %d}Config input: len is %d", __FUNCTION__, __LINE__, pt_iom_uart->z_len));
                    for (i4_i = 0; i4_i < pt_iom_uart->z_len; i4_i++)
                    {
                        JNI_LOGD(( "{%s %d}UART 0x%02X", __FUNCTION__, __LINE__, pt_iom_uart->pui1_data[i4_i]));
                    }
                }
                else
                {
                    JNI_LOGD(("{%s %d}Invalid input data.", __FUNCTION__, __LINE__));
                }
            }
            break;

        case IOM_NFY_COND_XMT_COMPLETE:
            JNI_LOGD(("{%s %d}Output data completed.", __FUNCTION__, __LINE__));
            break;

        default:
            break;
    }
}
#endif

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_openUARTSerial_1native(JNIEnv * env, jclass ckass, jint jint_uartSerialID, jintArray jintArray_uartSerialSetting, jintArray jintArray_handle)
{
    INT32 i4_ret = 0;
    INT32 i4_len;
    jint ajint_uartSerialSetting[4] = {0};
    HANDLE_T        h_dev;
    UART_SETTING_T  t_uart_setting;

    if (NULL == g_pt_jni_config_service_JavaVM)
    {   /*get jniVM for callback notify thread*/
        JNI_GET_JAVAVM(env);
    }

    i4_len = (INT32)JNI_GET_ARRAY_LEN(env, jintArray_handle);
    if (i4_len != 1)            
    {
        JNI_LOGE(("{%s %d}, the input jintArray_handle array size should be 1.", __FUNCTION__, __LINE__));                
        return -1;            
    }    
    
    i4_len = (INT32)JNI_GET_ARRAY_LEN(env, jintArray_uartSerialSetting);
    if (i4_len != 4)            
    {
        JNI_LOGE(("{%s %d}, the input uartSerialSetting array size should be 4.", __FUNCTION__, __LINE__));                
        return -1;            
    }
    
    JNI_GET_INT_ARRAY_REGION(env, jintArray_uartSerialSetting, 0, 4, &(ajint_uartSerialSetting[0]));

    
    t_uart_setting.e_speed    = ajint_uartSerialSetting[0];
    t_uart_setting.e_data_len = ajint_uartSerialSetting[1];
    t_uart_setting.e_parity   = ajint_uartSerialSetting[2];
    t_uart_setting.e_stop_bit = ajint_uartSerialSetting[3];

    JNI_DEL_LOCAL_REF(env, jintArray_uartSerialSetting); 
    
    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));
    i4_ret = c_iom_open(IOM_DEV_TYPE_IO_UART, 
                        (UINT16)jint_uartSerialID,
                        NULL,
                        (VOID*)&t_uart_setting,
                        (VOID*)jint_uartSerialID,
                        _fact_mode_iom_nfy_fct,
                        &h_dev);
    if (i4_ret != IOMR_OK)
    {
        JNI_LOGE(("{%s %d} c_iom_open return %d", __FUNCTION__, __LINE__, i4_ret));
        return -1;
    }
    JNI_LOGD(("{%s %d} c_iom_open return %d", __FUNCTION__, __LINE__, i4_ret));
    
    {
        jint ajint_h_dev[1];
        ajint_h_dev[0] = (jint)h_dev;
        JNI_SET_INT_ARRAY_REGION(env, jintArray_handle, 0, 1, ajint_h_dev);
    }
    JNI_LOGD(("{%s %d}, the uart handle is 0x%x.", __FUNCTION__, __LINE__, h_dev));
    return 0;
}

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_closeUARTSerial_1native
  (JNIEnv * env, jclass ckass, jint jint_handle)
{
    INT32 i4_ret = 0;
    JNI_LOGD(("{%s %d}, the uart handle is 0x%x.", __FUNCTION__, __LINE__, (HANDLE_T)jint_handle));    
    i4_ret = c_iom_close((HANDLE_T)jint_handle);
    if (i4_ret != IOMR_OK)
    {
        JNI_LOGE(("{%s %d} c_iom_close return %d", __FUNCTION__, __LINE__, i4_ret));
        return -1;
    }
    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));
    return 0;
}
    

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getUARTSerialSetting_1native
  (JNIEnv * env, jclass ckass, jint jint_handle, jintArray jintArray_uartSerialSetting)
{
    INT32 i4_ret;
    INT32 i4_len;
    jint ajint_uartSerialSetting[4];
    UART_SETTING_T t_uart_setting;
    SIZE_T         z_size = sizeof(UART_SETTING_T);
    JNI_LOGD(("{%s %d}, the uart handle is 0x%x.", __FUNCTION__, __LINE__, (HANDLE_T)jint_handle));    
    i4_len = (INT32)JNI_GET_ARRAY_LEN(env, jintArray_uartSerialSetting);
    if (i4_len != 4)            
    {
        JNI_LOGE(("{%s %d}, the input jintArray_uartSerialSetting array size should be 4.", __FUNCTION__, __LINE__));                
        return -1;            
    } 
    i4_ret = c_iom_get((HANDLE_T)jint_handle,
                        IOM_GET_UART_SETTING,
                        (VOID*)&t_uart_setting,
                        &z_size);
    if (i4_ret != IOMR_OK)
    {
        JNI_LOGE(("{%s %d} c_iom_get uart setting return %d", __FUNCTION__, __LINE__, i4_ret));
        return -1;
    }

    ajint_uartSerialSetting[0] = t_uart_setting.e_speed;
    ajint_uartSerialSetting[1] = t_uart_setting.e_data_len;
    ajint_uartSerialSetting[2] = t_uart_setting.e_parity;
    ajint_uartSerialSetting[3] = t_uart_setting.e_stop_bit;    
    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));
    JNI_SET_INT_ARRAY_REGION(env, jintArray_uartSerialSetting, 0, 4, ajint_uartSerialSetting);
    
    return 0;
}



JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getUARTSerialOperationMode_1native
  (JNIEnv * env, jclass ckass, jint jint_handle, jintArray jintArray_operationMode)
{
    INT32 i4_ret;
    INT32 i4_len;
    UINT32 ui4_operation_mode = 0;
    jint ajint_operationMode[1];
    SIZE_T         z_size = sizeof(UINT32);
    JNI_LOGD(("{%s %d}, the uart handle is 0x%x.", __FUNCTION__, __LINE__, (HANDLE_T)jint_handle));
    i4_len = (INT32)JNI_GET_ARRAY_LEN(env, jintArray_operationMode);
    if (i4_len != 1)            
    {
        JNI_LOGE(("{%s %d}, the input jintArray_operationMode array size should be 1.", __FUNCTION__, __LINE__));                
        return -1;            
    } 
    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));
    i4_ret = c_iom_get((HANDLE_T)jint_handle,
                        IOM_GET_UART_OPERATION_MODE,
                        (VOID*)&ui4_operation_mode,
                        &z_size);
    if (i4_ret != IOMR_OK)
    {
        JNI_LOGE(("{%s %d} c_iom_get uart operation mode return %d", __FUNCTION__, __LINE__, i4_ret));
        return -1;
    }
    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));

    ajint_operationMode[0] = ui4_operation_mode;

    JNI_SET_INT_ARRAY_REGION(env, jintArray_operationMode, 0, 1, ajint_operationMode);
    
    return 0;
}

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setUARTSerialSetting_1native
  (JNIEnv * env, jclass ckass, jint jint_handle, jintArray jintArray_uartSerialSetting)
{
    INT32 i4_ret;
    INT32 i4_len;
    jint ajint_uartSerialSetting[4];
    UART_SETTING_T t_uart_setting;
    SIZE_T         z_size = sizeof(UART_SETTING_T);

    JNI_LOGD(("{%s %d}, the uart handle is 0x%x.", __FUNCTION__, __LINE__, (HANDLE_T)jint_handle));
    
    i4_len = (INT32)JNI_GET_ARRAY_LEN(env, jintArray_uartSerialSetting);
    if (i4_len != 4)            
    {
        JNI_LOGE(("{%s %d}, the input jintArray_uartSerialSetting array size should be 4.", __FUNCTION__, __LINE__));                
        return -1;            
    } 
    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));
    JNI_GET_INT_ARRAY_REGION(env, jintArray_uartSerialSetting, 0, 4, &(ajint_uartSerialSetting[0]));

    t_uart_setting.e_speed    = ajint_uartSerialSetting[0];
    t_uart_setting.e_data_len = ajint_uartSerialSetting[1];
    t_uart_setting.e_parity   = ajint_uartSerialSetting[2];
    t_uart_setting.e_stop_bit = ajint_uartSerialSetting[3];
    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));
    JNI_DEL_LOCAL_REF(env, jintArray_uartSerialSetting); 
    i4_ret = c_iom_set((HANDLE_T)jint_handle,
                        IOM_SET_UART_SETTING,
                        (VOID*)&t_uart_setting,
                        z_size);
    if (i4_ret != IOMR_OK)
    {
        JNI_LOGE(("{%s %d} c_iom_get uart setting return %d", __FUNCTION__, __LINE__, i4_ret));
        return -1;
    }
    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));
    return 0;
}

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setUARTSerialOperationMode_1native
  (JNIEnv * env, jclass ckass,  jint jint_handle, jint jint_operationMode)
{
    INT32 i4_ret;
    UINT32 ui4_operationMode = (UINT32)jint_operationMode;
    
    JNI_LOGD(("{%s %d}, the uart handle is 0x%x.", __FUNCTION__, __LINE__, (HANDLE_T)jint_handle));    
    
    i4_ret = c_iom_set((HANDLE_T)jint_handle,
                        IOM_SET_UART_OPERATION_MODE,
                        (VOID*)&ui4_operationMode,
                        sizeof(UINT32));
    if (i4_ret != IOMR_OK)
    {
        JNI_LOGE(("{%s %d} c_iom_get uart setting return %d", __FUNCTION__, __LINE__, i4_ret));
        return -1;
    }
    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));
    return 0;
}


JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setUARTSerialMagicString_1native
  (JNIEnv * env, jclass ckass, jint jint_handle, jbyteArray jbyteArray_uartSerialMagicSetting)
{
    INT32 i4_ret;
    INT32 i4_len;
    UINT8 *pui1_uartSerialMagicSetting;
    MAGIC_T t_magic;
    MAGIC_UNIT_T at_magic_unit[1];
    SIZE_T         z_size = sizeof(UART_SETTING_T);

    JNI_LOGD(("{%s %d}, the uart handle is 0x%x.", __FUNCTION__, __LINE__, (HANDLE_T)jint_handle));   

    i4_len = (INT32)JNI_GET_ARRAY_LEN(env, jbyteArray_uartSerialMagicSetting);
    if (i4_len == 0)            
    {
        JNI_LOGE(("{%s %d}, the input jintArray_uartSerialSetting array size should large than 0.", __FUNCTION__, __LINE__));                
        return -1;            
    } 

    pui1_uartSerialMagicSetting = x_mem_alloc(sizeof(UINT8) * i4_len);    
    if (NULL == pui1_uartSerialMagicSetting)
    {
        JNI_LOGE(("{%s %d}, not enough memory(need %d).", __FUNCTION__, __LINE__, i4_len));                
        return -1;
    }
    
    JNI_GET_BYTE_ARRAY_REGION(env, jbyteArray_uartSerialMagicSetting, 0, i4_len, &(pui1_uartSerialMagicSetting[0]));

    JNI_DEL_LOCAL_REF(env, jbyteArray_uartSerialMagicSetting); 

    at_magic_unit[0].z_magic_len    = (SIZE_T)i4_len;
    at_magic_unit[0].pui1_magic     = pui1_uartSerialMagicSetting;
    t_magic.ui1_magic_units_count   = 1;
    t_magic.pt_magic_unit           = at_magic_unit;
    
    i4_ret = c_iom_set((HANDLE_T)jint_handle,
                        IOM_SET_UART_MAGIC_CHAR,
                        (VOID*)&t_magic,
                        z_size);
    x_mem_free(pui1_uartSerialMagicSetting);
    if (i4_ret != IOMR_OK)
    {
        JNI_LOGE(("{%s %d} c_iom_get uart setting return %d", __FUNCTION__, __LINE__, i4_ret));
        return -1;
    }

    return 0;
    
}

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_outputUARTSerial_1native
  (JNIEnv * env, jclass ckass, jint jint_handle, jbyteArray jbyteArray_uartSerialData)
{
    INT32 i4_ret;
    INT32 i4_len;
    UINT8 *pui1_uartSerialData;
    IOM_UART_T t_out_data;
    SIZE_T         z_size = sizeof(IOM_UART_T);
    JNI_LOGD(("{%s %d}, the uart handle is 0x%x.", __FUNCTION__, __LINE__, (HANDLE_T)jint_handle));    
    i4_len = (INT32)JNI_GET_ARRAY_LEN(env, jbyteArray_uartSerialData);
    if (i4_len == 0)            
    {
        JNI_LOGE(("{%s %d}, the input jintArray_uartSerialSetting array size should large than 0.", __FUNCTION__, __LINE__));                
        return -1;            
    } 

    pui1_uartSerialData = x_mem_alloc(sizeof(UINT8) * i4_len);    
    if (NULL == pui1_uartSerialData)
    {
        JNI_LOGE(("{%s %d}, not enough memory(need %d).", __FUNCTION__, __LINE__, i4_len));                
        return -1;
    }
    
    JNI_GET_BYTE_ARRAY_REGION(env, jbyteArray_uartSerialData, 0, i4_len, &(pui1_uartSerialData[0]));


    t_out_data.pui1_data = pui1_uartSerialData;
    t_out_data.z_len     = (SIZE_T)i4_len;
    
    i4_ret = c_iom_output((HANDLE_T)jint_handle, (VOID*)&t_out_data);
    x_mem_free(pui1_uartSerialData);
    if (i4_ret != IOMR_OK)
    {
        JNI_LOGE(("{%s %d} c_iom_output return %d", __FUNCTION__, __LINE__, i4_ret));
        return -1;
    }
    JNI_LOGD(("{%s %d}, c_iom_output ok.", __FUNCTION__, __LINE__));
    return 0;
}


EXTERN_C_END

