#include "tv_jni_util.h"
#include "com_mediatek_tv_service_TVNative.h"

#if 1
#include "u_cfg.h"
#include "config/a_cfg.h"
#include "input/input_service_client.h"
#else
#ifdef LOGD
#undef LOGD
#define LOGD printf
#endif
typedef signed long     INT32;
#define VOID  void
typedef size_t           SIZE_T;
#define TRUE ((BOOL) 1)    /**<        */
#define FALSE ((BOOL) 0)    /**<        */


typedef unsigned char  UINT8;    /**<        */
typedef UINT8  BOOL;    /**<        */
typedef unsigned short  UINT16;    /**<        */
typedef unsigned long    UINT32;    /**<        */
typedef UINT32  HANDLE_T;
typedef  UINT32                DEVICE_TYPE_T;    /**< defination of device type      */
typedef char  CHAR;

#define	 DEV_UNKNOWN           ((DEVICE_TYPE_T) 0x00000000)    /**< Unknown device type       */

#define  DEV_1394              ((DEVICE_TYPE_T) 0x01000000) /* 1394 devices group. */    /**<        */
#define  DEV_TUNER             ((DEVICE_TYPE_T) 0x02000000) /* tuner device group. */    /**<        */
#define	 DEV_AVC               ((DEVICE_TYPE_T) 0x03000000) /* AV Connect group. */    /**<        */
#define  DEV_VTRL              ((DEVICE_TYPE_T) 0x04000000) /* virtual connect group. */    /**<        */
#define   DEV_AVC_COMP_VIDEO        (DEV_AVC  |  0x00000001)    /**<   comp video     */
#define	  DEV_AVC_S_VIDEO           (DEV_AVC  |  0x00000002)    /**<    s video    */
#define	  DEV_AVC_Y_PB_PR           (DEV_AVC  |  0x00000003)    /**<   y pb pr     */
#define	  DEV_AVC_VGA               (DEV_AVC  |  0x00000004)    /**<   vga     */
#define	  DEV_AVC_SCART	            (DEV_AVC  |  0x00000005)    /**<   scart     */
#define	  DEV_AVC_DVI               (DEV_AVC  |  0x00000006)    /**<   dvi     */
#define	  DEV_AVC_HDMI              (DEV_AVC  |  0x00000007)    /**<   hdmi     */
#define	  DEV_AVC_AUDIO_INP         (DEV_AVC  |  0x00000008)    /**<   audio     */
#define	  DEV_AVC_SPDIF             (DEV_AVC  |  0x00000009)    /**<   spdif     */
#define	  DEV_AVC_COMBI             (DEV_AVC  |  0x0000000A)    /**<   combi     */

#define	  DEV_AVC_RESERVED          (DEV_AVC  |  0x0000000B)    /**<   reserved     */



typedef enum
{
    TV_WIN_ID_MAIN = 0,
    TV_WIN_ID_SUB,

    TV_WIN_ID_LAST_VALID_ENTRY  /* only for counting purpose */
} TV_WIN_ID_T;

typedef struct _VSH_REGION_INFO_T
{
    UINT32          ui4_x;
    UINT32          ui4_y;
    UINT32          ui4_width;
    UINT32          ui4_height;
}   VSH_REGION_INFO_T;

#define SYS_NAME_LEN  16     /**<   length of system name     */
#define CONN_SRC_NAME_MAX_STRLEN  SYS_NAME_LEN

typedef enum
{
    INP_SRC_TYPE_UNKNOWN = 0,
    INP_SRC_TYPE_TV,
    INP_SRC_TYPE_AV,
    INP_SRC_TYPE_1394,
    INP_SRC_TYPE_VTRL,
    INP_SRC_TYPE_MM
} INP_SRC_TYPE_T;


typedef struct _SRC_AVC_T  /* SRC_DESC_T's pv_details when e_type = SRC_TYPE_AVC*/
{
    DEVICE_TYPE_T                   e_video_type;  /* e.g DEV_AVC_COMBI     */
    #if 0
    SRC_AVC_HINT_T                  t_video_hint;

    DEVICE_TYPE_T                   e_audio_type;  /* e.g DEV_AVC_AUDIO_INP */
    SRC_AVC_HINT_T                  t_audio_hint;

    AVC_HANDLER_SCART_MODE_T        e_scart_mode;

    SRC_AVC_CHG_TYPE_T              e_src_chg_type; /* user manual change or monitor Scart PIN8 auto change */
    #endif
} SRC_AVC_T;


typedef struct _ISL_REC_T
{
    UINT8               ui1_id;
    UINT8               ui1_internal_id;
    UINT8               ui1_iid_count;   /* the total count of the input sources with the same AV type */
    UINT8               ui1_alike_id;
    INP_SRC_TYPE_T      e_src_type;
    CHAR                s_src_name[CONN_SRC_NAME_MAX_STRLEN+1];
    SRC_AVC_T           t_avc_info;
    UINT8               ui1_scart_bundled_id;
    UINT8               ui1_custom_data;
    UINT32              ui4_attr_bits;
    BOOL*               pb_groupships;
} ISL_REC_T;


#define MAKE_BIT_MASK_16(_val) (((UINT16) 1) << (_val))    /**<        */
#define MAKE_BIT_MASK_32(_val) (((UINT32) 1) << (_val))    /**<        */

typedef VOID (*PF_INPS_SOURCE_DETECTED) ( UINT8 ui1_id );
typedef VOID (*PF_INPS_OPERATION_DONE) ( TV_WIN_ID_T t_win_id, BOOL fg_is_signal_loss );

typedef struct _INPS_NFY_T
{
    PF_INPS_SOURCE_DETECTED     pf_inps_source_detected;
    PF_INPS_OPERATION_DONE      pf_nfy_operation_done;
} INPS_NFY_T;


INT32 x_inps_init( 
    VOID )
{
    return 0;
}

INT32 x_inps_set_nfy( 
    INPS_NFY_T*                 pt_inps_nfy )
    {
    return 0;
    }

INT32 x_inps_get_rec_and_grp_num( 
    UINT8*                      pui1_num_recs,
    UINT8*                      pui1_num_grps )
    {
    *pui1_num_recs = 10;
    *pui1_num_grps = 4;
    return 0;
    }


#define bak_InputSourceType_INPS_TYPE_NUMERICAL_TV          (0)
#define bak_InputSourceType_INPS_TYPE_NUMERICAL_AV          (1+bak_InputSourceType_INPS_TYPE_NUMERICAL_TV       )
#define bak_InputSourceType_INPS_TYPE_NUMERICAL_VGA         (1+bak_InputSourceType_INPS_TYPE_NUMERICAL_AV       )
#define bak_InputSourceType_INPS_TYPE_NUMERICAL_SVIDEO      (1+bak_InputSourceType_INPS_TYPE_NUMERICAL_VGA      )
#define bak_InputSourceType_INPS_TYPE_NUMERICAL_COMPONENT   (1+bak_InputSourceType_INPS_TYPE_NUMERICAL_SVIDEO   )
#define bak_InputSourceType_INPS_TYPE_NUMERICAL_COMPOSITE   (1+bak_InputSourceType_INPS_TYPE_NUMERICAL_COMPONENT)
#define bak_InputSourceType_INPS_TYPE_NUMERICAL_HDMI        (1+bak_InputSourceType_INPS_TYPE_NUMERICAL_COMPOSITE)
#define bak_InputSourceType_INPS_TYPE_NUMERICAL_RESERVED    (1+bak_InputSourceType_INPS_TYPE_NUMERICAL_HDMI     )
#define bak_InputSourceType_INPS_TYPE_NUMERICAL_MAX_NUM     (1+bak_InputSourceType_INPS_TYPE_NUMERICAL_RESERVED )


static DEVICE_TYPE_T    _jni_j2c_input_type ( INT32   i4_j_input_type )
{
    DEVICE_TYPE_T   e_video_type;

    switch (i4_j_input_type)
    {
        case bak_InputSourceType_INPS_TYPE_NUMERICAL_TV:        e_video_type = DEV_TUNER;           break;        
        case bak_InputSourceType_INPS_TYPE_NUMERICAL_AV:        e_video_type = DEV_AVC_COMBI;       break;
        case bak_InputSourceType_INPS_TYPE_NUMERICAL_VGA:       e_video_type = DEV_AVC_VGA;         break;
        case bak_InputSourceType_INPS_TYPE_NUMERICAL_SVIDEO:    e_video_type = DEV_AVC_S_VIDEO;     break;
        case bak_InputSourceType_INPS_TYPE_NUMERICAL_COMPONENT: e_video_type = DEV_AVC_Y_PB_PR;     break;
        case bak_InputSourceType_INPS_TYPE_NUMERICAL_COMPOSITE: e_video_type = DEV_AVC_COMP_VIDEO;  break;
        case bak_InputSourceType_INPS_TYPE_NUMERICAL_HDMI:      e_video_type = DEV_AVC_HDMI;        break;
        default:                                                e_video_type = DEV_AVC_SCART;       break;
    }

    return e_video_type;
}

static BOOL     g_ab_groupships[4];
static VOID _jni_set_isl_grp(INT32  grp, ISL_REC_T* pt_isl_rec)
{
    pt_isl_rec->pb_groupships = g_ab_groupships;
    pt_isl_rec->pb_groupships[0] = (1 == grp % 2) ? TRUE :FALSE;    grp /= 2;
    pt_isl_rec->pb_groupships[1] = (1 == grp % 2) ? TRUE :FALSE;    grp /= 2;
    pt_isl_rec->pb_groupships[2] = (1 == grp % 2) ? TRUE :FALSE;    grp /= 2;
    pt_isl_rec->pb_groupships[3] = (1 == grp % 2) ? TRUE :FALSE;
}


INT32 x_inps_get_rec_by_idx( 
    UINT32                      ui4_record_idx,
    ISL_REC_T*                  pt_isl_rec )
{
    switch (ui4_record_idx)
    {
        case 0: pt_isl_rec->t_avc_info.e_video_type = _jni_j2c_input_type(bak_InputSourceType_INPS_TYPE_NUMERICAL_TV          );pt_isl_rec->ui1_internal_id = 0 ;pt_isl_rec->ui1_id = 0 ;_jni_set_isl_grp(0xa,pt_isl_rec);break;//
        case 1: pt_isl_rec->t_avc_info.e_video_type = _jni_j2c_input_type(bak_InputSourceType_INPS_TYPE_NUMERICAL_AV          );pt_isl_rec->ui1_internal_id = 0 ;pt_isl_rec->ui1_id = 1 ;_jni_set_isl_grp(0x8,pt_isl_rec);break;//
        case 2: pt_isl_rec->t_avc_info.e_video_type = _jni_j2c_input_type(bak_InputSourceType_INPS_TYPE_NUMERICAL_COMPONENT   );pt_isl_rec->ui1_internal_id = 0 ;pt_isl_rec->ui1_id = 2 ;_jni_set_isl_grp(0x4,pt_isl_rec);break;//
        case 3: pt_isl_rec->t_avc_info.e_video_type = _jni_j2c_input_type(bak_InputSourceType_INPS_TYPE_NUMERICAL_RESERVED    );pt_isl_rec->ui1_internal_id = 0 ;pt_isl_rec->ui1_id = 3 ;_jni_set_isl_grp(0xe,pt_isl_rec);break;//
        case 4: pt_isl_rec->t_avc_info.e_video_type = _jni_j2c_input_type(bak_InputSourceType_INPS_TYPE_NUMERICAL_RESERVED    );pt_isl_rec->ui1_internal_id = 1 ;pt_isl_rec->ui1_id = 4 ;_jni_set_isl_grp(0xe,pt_isl_rec);break;//
        case 5: pt_isl_rec->t_avc_info.e_video_type = _jni_j2c_input_type(bak_InputSourceType_INPS_TYPE_NUMERICAL_VGA         );pt_isl_rec->ui1_internal_id = 0 ;pt_isl_rec->ui1_id = 5 ;_jni_set_isl_grp(0x4,pt_isl_rec);break;//
        case 6: pt_isl_rec->t_avc_info.e_video_type = _jni_j2c_input_type(bak_InputSourceType_INPS_TYPE_NUMERICAL_HDMI        );pt_isl_rec->ui1_internal_id = 0 ;pt_isl_rec->ui1_id = 6 ;_jni_set_isl_grp(0x1,pt_isl_rec);break;//
        case 7: pt_isl_rec->t_avc_info.e_video_type = _jni_j2c_input_type(bak_InputSourceType_INPS_TYPE_NUMERICAL_HDMI        );pt_isl_rec->ui1_internal_id = 1 ;pt_isl_rec->ui1_id = 7 ;_jni_set_isl_grp(0x1,pt_isl_rec);break;//
        case 8: pt_isl_rec->t_avc_info.e_video_type = _jni_j2c_input_type(bak_InputSourceType_INPS_TYPE_NUMERICAL_HDMI        );pt_isl_rec->ui1_internal_id = 2 ;pt_isl_rec->ui1_id = 8 ;_jni_set_isl_grp(0x1,pt_isl_rec);break;//
        case 9: pt_isl_rec->t_avc_info.e_video_type = _jni_j2c_input_type(bak_InputSourceType_INPS_TYPE_NUMERICAL_RESERVED    );pt_isl_rec->ui1_internal_id = 0 ;pt_isl_rec->ui1_id = 9 ;_jni_set_isl_grp(0xf,pt_isl_rec);break;//
        default: break;
    }
    if (ui4_record_idx > 9)
    {
        return -1;
    }
    
    return 0;
}

INT32 x_inps_bind( 
    TV_WIN_ID_T                 t_output_win_id,
    UINT8                       ui1_input_id )
{
    JNI_LOGD(("[%d]{%s}[win_id:%d][input_id:%d]\n", __LINE__, __func__, t_output_win_id, ui1_input_id));
    return 0;
}

INT32 x_inps_swap( 
    TV_WIN_ID_T                 t_output_win_id1,
    TV_WIN_ID_T                 t_output_win_id2 )
{
    return 0;
}

INT32 x_inps_update_output_rect( 
    TV_WIN_ID_T                 t_output_win_id,
    VSH_REGION_INFO_T*          pt_vs_region )
{
    JNI_LOGD(("[%d]{%s}[win_id:%d][x:%d][y:%d][width:%d][height:%d]\n", __LINE__, __func__, t_output_win_id, pt_vs_region->ui4_x, pt_vs_region->ui4_y, pt_vs_region->ui4_width, pt_vs_region->ui4_height));
    return 0;
}

INT32 x_inps_mute( 
    TV_WIN_ID_T                 t_output_win_id,
    BOOL                        fg_need_mute )
{
    JNI_LOGD(("[%d]{%s}[win_id:%d][need_mute:%d]\n", __LINE__, __func__, t_output_win_id, fg_need_mute));
    return 0;
}
#endif

EXTERN_C_START
//#define LOG_TAG     "input_service"
//#define INPUT_LOGD  LOGD( "[inps_client][%d][%s]", __LINE__, __func__ );LOGD
#define INPUT_LOGD  JNI_LOGD



#define _JNI_MAX_INPUT_GRP_SIZE     20
#define _JNI_MAX_INPUT_EXCHANGE_DATA_SIZE   20


CHAR* pc_debug_string[InputSourceType_INPS_TYPE_NUMERICAL_MAX_NUM] = {
    "TV",         
    "AV",         
    "VGA",        
    "SVIDEO",     
    "COMPONENT",  
    "COMPOSITE",  
    "HDMI",
    "RESERVED" }; 

typedef enum {
    setInputType = 0,
    setGourp,
    setGourpSize,
    setInternalIdx,
    setId,
    inputRecordMethodsNum
}   _inputRecordMethods;
jmethodID   InputRecordMethods[inputRecordMethodsNum];
jclass      InputRecordClass;

typedef enum {
    setLeft = 0,
    setRight,
    setTop,
    setBottom,
    inputRegionMethodsNum
}   _inputRegionMethods;
jmethodID   InputRegionMethods[inputRegionMethodsNum];
jclass      InputRegionClass;

typedef enum {
    onOperationDone = 0,
    onSourceDetected,
    onOutputSignalStatus,
    inputServiceMethodsNum
}   _inputServiceMethods;
jmethodID   InputServiceMethods[inputServiceMethodsNum];
jclass      InputServiceClass;


#define INPUT_JNI_GET_CLASS_STATIC_METHOD(ENV,CLASS,NAME,SIG)       ((*ENV)->GetStaticMethodID(ENV,CLASS,NAME,SIG))
#define INPUT_JNI_NEW_GLOBAL_REF(ENV,CLASS)                         ((*ENV)->NewGlobalRef(ENV,CLASS))
#define INPUT_JNI_NEW_INT_ARRAY(ENV,SIZE)                       ((*ENV)->NewIntArray(ENV,SIZE))
#define INPUT_JNI_SET_INT_ARRAY_REGION(ENV,ARRAY,SIZE,args)     ((*ENV)->SetIntArrayRegion(ENV,ARRAY,0,SIZE,args))
#define INPUT_JNI_GET_INT_ARRAY_REGION(ENV,ARRAY,SIZE,args)     ((*ENV)->GetIntArrayRegion(ENV,ARRAY,0,SIZE,args))
#define INPUT_JNI_GET_JAVAVM(ENV)                               ((*ENV)->GetJavaVM(ENV, &g_pt_jni_input_JavaVM))
#define INPUT_JNI_ATTACH_CURRENT_THREAD(ENV)                    ((*g_pt_jni_input_JavaVM)->AttachCurrentThread(g_pt_jni_input_JavaVM, (void**)&ENV, NULL))
#define INPUT_JNI_DETACH_CURRENT_THREAD(ENV)                    ((*g_pt_jni_input_JavaVM)->DetachCurrentThread(g_pt_jni_input_JavaVM))

static JNIEnv *g_pt_jni_input_env;
static JavaVM *g_pt_jni_input_JavaVM;


#define CLASS_INT_METHOD(  className, method )                  (*env)->CallIntMethod(env, className, className##Methods[method])
#define CLASS_INT_METHODV( className, method, args...)          (*env)->CallIntMethod(env, className, className##Methods[method], args)
#define CLASS_INT_STATIC_METHOD(  className, method )           (*env)->CallStaticIntMethod(env, className, className##Methods[method])
#define CLASS_INT_STATIC_METHODV( className, method, args...)   (*env)->CallStaticIntMethod(env, className, className##Methods[method], args)

#define CLASS_VOID_METHOD(  className, method )                 (*env)->CallVoidMethod(env, className, className##Methods[method])
#define CLASS_VOID_METHODV( className, method, args...)         (*env)->CallVoidMethod(env, className, className##Methods[method], args)
#define CLASS_VOID_STATIC_METHOD(  className, method )          (*env)->CallStaticVoidMethod(env, className, className##Methods[method])
#define CLASS_VOID_STATIC_METHODV( className, method, args...)  (*env)->CallStaticVoidMethod(env, className, className##Methods[method], args)


static TV_WIN_ID_T  _jni_j2c_output_id  ( jint output )
{
    return (OutputDevice_OUTPUT_MAIN == output) ? TV_WIN_ID_MAIN : TV_WIN_ID_SUB;
}

static jint         _jni_c2j_output_id  ( TV_WIN_ID_T t_win_id )
{
    return (TV_WIN_ID_MAIN == t_win_id) ? OutputDevice_OUTPUT_MAIN : OutputDevice_OUTPUT_SUB;
}


static INT32        _jni_c2j_input_type ( DEVICE_TYPE_T e_video_type )
{
    INT32   i4_j_input_type = InputSourceType_INPS_TYPE_NUMERICAL_TV;

    switch (e_video_type)
    {
        case DEV_TUNER:             i4_j_input_type = InputSourceType_INPS_TYPE_NUMERICAL_TV;           break;        
        case DEV_AVC_COMBI:         i4_j_input_type = InputSourceType_INPS_TYPE_NUMERICAL_AV;           break;
        case DEV_AVC_VGA:           i4_j_input_type = InputSourceType_INPS_TYPE_NUMERICAL_VGA;          break;
        case DEV_AVC_S_VIDEO:       i4_j_input_type = InputSourceType_INPS_TYPE_NUMERICAL_SVIDEO;       break;
        case DEV_AVC_Y_PB_PR:       i4_j_input_type = InputSourceType_INPS_TYPE_NUMERICAL_COMPONENT;    break;
        case DEV_AVC_COMP_VIDEO:    i4_j_input_type = InputSourceType_INPS_TYPE_NUMERICAL_COMPOSITE;    break;
        case DEV_AVC_HDMI:          i4_j_input_type = InputSourceType_INPS_TYPE_NUMERICAL_HDMI;         break;
        default:                    i4_j_input_type = InputSourceType_INPS_TYPE_NUMERICAL_RESERVED;     break;
    }

    return i4_j_input_type;
}


static VOID _jni_operation_done_nfy ( TV_WIN_ID_T t_win_id, BOOL fg_is_signal_loss, VOID* pv_tag )
{
    jclass      jclass_TVCallBack = NULL;
    jclass      InputService = InputServiceClass;
    JNIEnv *    env = g_pt_jni_input_env;
    int         ret;
    int         output = _jni_c2j_output_id( t_win_id );
    jint        withoutAttachDetach   = 0;
    
    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(env);
    if (!withoutAttachDetach)
    {
        ret = INPUT_JNI_ATTACH_CURRENT_THREAD(env);
        if (ret < 0)
        {
            JNI_LOGD(("[ERROR][%d]{%s}\n", __LINE__, __func__));
        }
    }
    else
    {
        JNI_LOGD(("[Warning][%d]{%s}We are already one with VM\n", __LINE__, __func__));
    }

    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
    JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Void,CMI(TVCallBack_def,TVCallBack_onOperationDone),output, fg_is_signal_loss);

//    CLASS_VOID_STATIC_METHODV( InputService, onOperationDone, output, fg_is_signal_loss );
    if (!withoutAttachDetach)
    {
        INPUT_JNI_DETACH_CURRENT_THREAD(env);
    }

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
}


static VOID _jni_inps_signal_status ( TV_WIN_ID_T t_win_id, SVCTX_NTFY_CODE_T e_code, VOID* pv_tag )
{
    jclass      jclass_TVCallBack = NULL;
    jclass      InputService = InputServiceClass;
    JNIEnv *    env = g_pt_jni_input_env;
    int         ret;
    int         output = _jni_c2j_output_id( t_win_id );
    int         signalStatus = InputService_INPUT_SIGNAL_UNKOWN;
    jint        withoutAttachDetach   = 0;
    
    switch (e_code)
    {
        case SVCTX_NTFY_CODE_SIGNAL_LOSS:
            signalStatus = InputService_INPUT_SIGNAL_LOSS;
            INPUT_LOGD(("InputService_INPUT_SIGNAL_LOSS[%d]{%s}\n", __LINE__, __func__));
            break;

        case SVCTX_NTFY_CODE_SIGNAL_LOCKED:
            signalStatus = InputService_INPUT_SIGNAL_LOCKED;
            INPUT_LOGD(("InputService_INPUT_SIGNAL_LOCKED[%d]{%s}\n", __LINE__, __func__));
            break;

        case SVCTX_NTFY_CODE_VIDEO_FMT_UPDATE:
        case SVCTX_NTFY_CODE_VIDEO_FMT_UPDATE_AS_BLOCKED:
            signalStatus = InputService_INPUT_VIDEO_UPDATE;
            INPUT_LOGD(("InputService_INPUT_VIDEO_UPDATE[%d]{%s}\n", __LINE__, __func__));
            break;
            
        default:
            return;
    }
    
    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(env);
    if (!withoutAttachDetach)
    {
        ret = INPUT_JNI_ATTACH_CURRENT_THREAD(env);
        if (ret < 0)
        {
            JNI_LOGD(("[ERROR][%d]{%s}\n", __LINE__, __func__));
        }
    }
    else
    {
        JNI_LOGD(("[Warning][%d]{%s}We are already one with VM\n", __LINE__, __func__));
    }

    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
    JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Void,CMI(TVCallBack_def,TVCallBack_onOutputSignalStatus),output, signalStatus);
    //CLASS_VOID_STATIC_METHODV( InputService, onOutputSignalSatus, output, signalStatus );

    if (!withoutAttachDetach)
    {
        INPUT_JNI_DETACH_CURRENT_THREAD(env);
    }

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
}


static VOID _jni_source_detected_nfy ( UINT8 ui1_id, SVCTX_NTFY_CODE_T e_code, VOID* pv_tag )
{
    jclass      jclass_TVCallBack = NULL;
    jclass      InputService = InputServiceClass;
    JNIEnv *    env = g_pt_jni_input_env;
    int         ret;
    int         signalStatus = InputService_INPUT_SIGNAL_UNKOWN;
    jint        withoutAttachDetach   = 0;

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    switch (e_code)
    {
        case SVCTX_NTFY_CODE_SIGNAL_LOSS:
            signalStatus = InputService_INPUT_SIGNAL_LOSS;
            INPUT_LOGD(("InputService_INPUT_SIGNAL_LOSS[%d]{%s}\n", __LINE__, __func__));
            break;

        case SVCTX_NTFY_CODE_SIGNAL_LOCKED:
            signalStatus = InputService_INPUT_SIGNAL_LOCKED;
            INPUT_LOGD(("InputService_INPUT_SIGNAL_LOCKED[%d]{%s}\n", __LINE__, __func__));
            break;
            
        default:
            return;
    }
    
    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(env);
    if (!withoutAttachDetach)
    {
        ret = INPUT_JNI_ATTACH_CURRENT_THREAD(env);
        if (ret < 0)
        {
            JNI_LOGD(("[ERROR][%d]{%s}\n", __LINE__, __func__));
        }
    }
    else
    {
        JNI_LOGD(("[Warning][%d]{%s}We are already one with VM\n", __LINE__, __func__));
    }

    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
    JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Void,CMI(TVCallBack_def,TVCallBack_onSourceDetected),ui1_id, signalStatus);
    //CLASS_VOID_STATIC_METHODV( InputService, onSourceDetected, ui1_id );

    if (!withoutAttachDetach)
    {
        INPUT_JNI_DETACH_CURRENT_THREAD(env);
    }

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
}


void x_input_service_init_jni(JNIEnv *env)
{
    jclass      _InputRecordClass;
    jclass      _InputRegionClass;
    jclass      _InputServiceClass;
    INPS_NFY_T  t_inps_nfy;

    g_pt_jni_input_env = env;
    if (INPUT_JNI_GET_JAVAVM(env) < 0)
    {
        INPUT_LOGD(("[ERROR][%d]{%s}\n", __LINE__, __func__));
    }

    INPUT_LOGD(("[%d]{%s} enter \n", __LINE__, __func__));
    
    _InputRecordClass                       = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/InputRecord");
    InputRecordClass                        = INPUT_JNI_NEW_GLOBAL_REF(env, _InputRecordClass);
    InputRecordMethods[setInputType]        = JNI_GET_CLASS_METHOD(env, InputRecordClass, "setInputType",   "(I)V");
    InputRecordMethods[setGourp]            = JNI_GET_CLASS_METHOD(env, InputRecordClass, "setGourp",       "([I)V");
    InputRecordMethods[setGourpSize]        = JNI_GET_CLASS_METHOD(env, InputRecordClass, "setGourpSize",   "(I)V");
    InputRecordMethods[setInternalIdx]      = JNI_GET_CLASS_METHOD(env, InputRecordClass, "setInternalIdx", "(I)V");
    InputRecordMethods[setId]               = JNI_GET_CLASS_METHOD(env, InputRecordClass, "setId",          "(I)V");

    _InputRegionClass                           = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/InputRegion");
    InputRegionClass                            = INPUT_JNI_NEW_GLOBAL_REF(env, _InputRegionClass);
    InputRegionMethods[setLeft]                 = JNI_GET_CLASS_METHOD(env, InputRegionClass, "setLeft",        "(I)V");
    InputRegionMethods[setRight]                = JNI_GET_CLASS_METHOD(env, InputRegionClass, "setRight",       "(I)V");
    InputRegionMethods[setTop]                  = JNI_GET_CLASS_METHOD(env, InputRegionClass, "setTop",         "(I)V");
    InputRegionMethods[setBottom]               = JNI_GET_CLASS_METHOD(env, InputRegionClass, "setBottom",      "(I)V");

    _InputServiceClass                      = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/InputService");
    InputServiceClass                       = INPUT_JNI_NEW_GLOBAL_REF(env, _InputServiceClass);
    InputServiceMethods[onOperationDone]    = INPUT_JNI_GET_CLASS_STATIC_METHOD(env, InputServiceClass, "onOperationDone",  "(IZ)V");
    InputServiceMethods[onSourceDetected]   = INPUT_JNI_GET_CLASS_STATIC_METHOD(env, InputServiceClass, "onSourceDetected", "(II)V");
    InputServiceMethods[onOutputSignalStatus]    = INPUT_JNI_GET_CLASS_STATIC_METHOD(env, InputServiceClass, "onOutputSignalStatus",  "(II)V");

    INPUT_LOGD(("[%d]{%s} JNI function tabel compeleted\n", __LINE__, __func__));

    t_inps_nfy.pf_inps_source_detected  = _jni_source_detected_nfy;
    t_inps_nfy.pf_nfy_operation_done    = _jni_operation_done_nfy;
    t_inps_nfy.pf_inps_signal_status    = _jni_inps_signal_status;
    a_inps_set_nfy( &t_inps_nfy, NULL );

    INPUT_LOGD(("[%d]{%s} leave \n", __LINE__, __func__));
}


#if 0 /* just a exmaple */
[index:0][type:TV          ][internal_id:0 ][id:0 ][num_grp:4][grp:0xa]
[index:1][type:AV          ][internal_id:0 ][id:1 ][num_grp:4][grp:0x8]
[index:2][type:COMPONENT   ][internal_id:0 ][id:2 ][num_grp:4][grp:0x4]
[index:3][type:RESERVED    ][internal_id:0 ][id:3 ][num_grp:4][grp:0xe]
[index:4][type:RESERVED    ][internal_id:1 ][id:4 ][num_grp:4][grp:0xe]
[index:5][type:VGA         ][internal_id:0 ][id:5 ][num_grp:4][grp:0x4]
[index:6][type:HDMI        ][internal_id:0 ][id:6 ][num_grp:4][grp:0x1]
[index:7][type:HDMI        ][internal_id:1 ][id:7 ][num_grp:4][grp:0x1]
[index:8][type:HDMI        ][internal_id:2 ][id:8 ][num_grp:4][grp:0x1]
[index:9][type:RESERVED    ][internal_id:0 ][id:9 ][num_grp:4][grp:0xf]
#endif


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    inputServiceGetRecord_native
 * Signature: (ILcom/mediatek/tv/model/InputRecord;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_inputServiceGetRecord_1native
  (JNIEnv *env, jclass clazz, jint index, jobject InputRecord)
{
    INT32                   i4_ret;
    UINT8                   ui1_num_recs    = 0;
    UINT8                   ui1_num_grps    = 0;
    ISL_REC_T               t_isl_rec;
    INT32                   i4_j_input_type;
    SIZE_T                  z_size          = 0;
    int                     ai4_input_grp[_JNI_MAX_INPUT_GRP_SIZE];
    BOOL                    ab_groupships[_JNI_MAX_INPUT_GRP_SIZE];
    jintArray               intArray;
    UINT8                   i;

    INPUT_LOGD(("\n[%d]{%s} enter \n", __LINE__, __func__));

    a_inps_get_rec_and_grp_num( &ui1_num_recs, &ui1_num_grps );

    INPUT_LOGD(("[%d]{%s}[index: %d]\n", __LINE__, __func__, index));

    i4_ret = a_inps_get_rec_by_idx( (UINT8)index, &t_isl_rec, ab_groupships, _JNI_MAX_INPUT_GRP_SIZE);
    if (i4_ret >= 0)
    {
        UINT8   ui1_valid_size = (ui1_num_grps > _JNI_MAX_INPUT_GRP_SIZE) ? _JNI_MAX_INPUT_GRP_SIZE : ui1_num_grps;

        z_size = ui1_valid_size;
        t_isl_rec.pb_groupships = ab_groupships;
        for (i = 0; i < ui1_valid_size; i++)
        {
            if (t_isl_rec.pb_groupships[i])
            {
                ai4_input_grp[i] = 1;
            }
            else
            {
                ai4_input_grp[i] = 0;
            }
        }

        intArray = INPUT_JNI_NEW_INT_ARRAY(env,z_size);                    
        INPUT_JNI_SET_INT_ARRAY_REGION(env,intArray,z_size,ai4_input_grp);

        i4_j_input_type = _jni_c2j_input_type( t_isl_rec.t_avc_info.e_video_type );

        /* log for debug */
        {
            UINT32                  ui4_input_grp = 0;
            for (i = 0; i < ui1_valid_size; i++)
            {
                if (1 == ai4_input_grp[i])
                {
                    ui4_input_grp *= 2;
                    ui4_input_grp++;
                }
                else
                {
                    ui4_input_grp *= 2;
                }
            }

            INPUT_LOGD(("[%d]{%s}[type:%-10s][internal_id:%-2d][id:%-2d][num_grp:%d][grp:%d]\n", 
                __LINE__, __func__,
                pc_debug_string[i4_j_input_type], 
                (int)t_isl_rec.ui1_internal_id, 
                (int)t_isl_rec.ui1_id,
                (int)ui1_num_grps,
                (int)ui4_input_grp ));
        }

        {
            jint                    inputType   = (jint)i4_j_input_type;
            jint                    gourpSize   = (jint)ui1_num_grps;
            jint                    internalIdx = (jint)t_isl_rec.ui1_internal_id;
            jint                    id          = (jint)t_isl_rec.ui1_id;

            CLASS_VOID_METHODV( InputRecord, setInputType,   inputType );
            CLASS_VOID_METHODV( InputRecord, setGourpSize,   gourpSize );
            CLASS_VOID_METHODV( InputRecord, setInternalIdx, internalIdx );
            CLASS_VOID_METHODV( InputRecord, setId,          id );
            CLASS_VOID_METHODV( InputRecord, setGourp,       intArray );
        }
    }
    else
    {
        INPUT_LOGD(("[%d]{%s}[index: %d]END\n", __LINE__, __func__, index));
    }

    INPUT_LOGD(("[%d]{%s} leave \n", __LINE__, __func__));
    
    return (jint)i4_ret;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    inputServiceBind_native
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_inputServiceBind_1native
  (JNIEnv *env, jclass clazz, jint output, jint inputId)
{
    INT32                   i4_ret;
    TV_WIN_ID_T             t_win_id    = _jni_j2c_output_id(output);

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));

    i4_ret = a_inps_bind( t_win_id, (UINT8)inputId );
    if (i4_ret < 0)
    {
        INPUT_LOGD(("[ERROR][%d]{%s}(i4_ret:%d)\n", __LINE__, __func__, (int)i4_ret));
    }

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    
    return (jint)i4_ret;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    inputServiceSwap_native
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_inputServiceSwap_1native
  (JNIEnv *env, jclass clazz, jint output1, jint output2)
{
    INT32                   i4_ret;
    TV_WIN_ID_T             t_win_id1   = _jni_j2c_output_id(output1);
    TV_WIN_ID_T             t_win_id2   = _jni_j2c_output_id(output2);

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    
    i4_ret = a_inps_pip_swap(t_win_id1, t_win_id2);
    if (i4_ret < 0)
    {
        INPUT_LOGD(("[ERROR][%d]{%s}(i4_ret:%d)\n", __LINE__, __func__, (int)i4_ret));
    }

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    
    return (jint)i4_ret;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setScreenOutputRect_native
 * Signature: (IIIII)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setScreenOutputRect_1native
  (JNIEnv *env, jclass clazz, jint output, jint left, jint right, jint top, jint bottom )
{
    INT32                   i4_ret;
    TV_WIN_ID_T             t_win_id    = _jni_j2c_output_id(output);
    VSH_REGION_INFO_T       t_ds_region = {0};

    INPUT_LOGD(("[%d]{%s}[%d][%d][%d][%d]\n", __LINE__, __func__, left, right, top, bottom));
    
    t_ds_region.ui4_x       = (UINT32)left;
    t_ds_region.ui4_y       = (UINT32)top;
    t_ds_region.ui4_width   = (UINT32)(right - left);
    t_ds_region.ui4_height  = (UINT32)(bottom - top);

    i4_ret = a_inps_update_output_rect( t_win_id, &t_ds_region );
    if (i4_ret < 0)
    {
        INPUT_LOGD(("[ERROR][%d]{%s}(i4_ret:%d)\n", __LINE__, __func__, (int)i4_ret)); 
    }

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    
    return (jint)i4_ret;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setScreenOutputRect_native
 * Signature: (IIIII)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setScreenOutputVideoRect_1native
  (JNIEnv *env, jclass clazz, jint output, jint left, jint right, jint top, jint bottom )
{
    INT32                   i4_ret;
    TV_WIN_ID_T             t_win_id    = _jni_j2c_output_id(output);
    VSH_REGION_INFO_T       t_vs_region = {0};

    INPUT_LOGD(("[%d]{%s}[%d][%d][%d][%d]\n", __LINE__, __func__, left, right, top, bottom));
    
    t_vs_region.ui4_x       = (UINT32)left;
    t_vs_region.ui4_y       = (UINT32)top;
    t_vs_region.ui4_width   = (UINT32)(right - left);
    t_vs_region.ui4_height  = (UINT32)(bottom - top);

    i4_ret = a_inps_update_video_rect( t_win_id, &t_vs_region );
    if (i4_ret < 0)
    {
        INPUT_LOGD(("[ERROR][%d]{%s}(i4_ret:%d)\n", __LINE__, __func__, (int)i4_ret));
    }

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    
    return (jint)i4_ret;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getScreenOutputRect_native
 * Signature: (ILcom/mediatek/tv/model/InputRegion;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getScreenOutputRect_1native
  (JNIEnv *env, jclass clazz, jint output, jobject InputRegion)
{
    INT32                   i4_ret;
    TV_WIN_ID_T             t_win_id    = _jni_j2c_output_id(output);
    VSH_REGION_INFO_T       t_ds_region = {0};

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    
    i4_ret = a_inps_get_output_rect( t_win_id, &t_ds_region );
    if (i4_ret < 0)
    {
        INPUT_LOGD(("[ERROR][%d]{%s}(i4_ret:%d)\n", __LINE__, __func__, (int)i4_ret));
    }

    {
        jint                    left    = (jint)(t_ds_region.ui4_x);        
        jint                    top     = (jint)(t_ds_region.ui4_y);
        jint                    right   = (jint)(t_ds_region.ui4_x + t_ds_region.ui4_width);
        jint                    bottom  = (jint)(t_ds_region.ui4_y + t_ds_region.ui4_height);

        INPUT_LOGD(("[%d]{%s}[%d][%d][%d][%d]\n", __LINE__, __func__, left, right, top, bottom));
        
        CLASS_VOID_METHODV( InputRegion, setLeft,   left   );
        CLASS_VOID_METHODV( InputRegion, setRight,  right  ); 
        CLASS_VOID_METHODV( InputRegion, setTop,    top    );   
        CLASS_VOID_METHODV( InputRegion, setBottom, bottom );
    }
    
    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    
    return (jint)i4_ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getScreenOutputVideoRect_native
 * Signature: (ILcom/mediatek/tv/model/InputRegion;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getScreenOutputVideoRect_1native
  (JNIEnv *env, jclass clazz, jint output, jobject InputRegion)
{
    INT32                   i4_ret;
    TV_WIN_ID_T             t_win_id    = _jni_j2c_output_id(output);
    VSH_REGION_INFO_T       t_vs_region = {0};

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    
    i4_ret = a_inps_get_video_rect( t_win_id, &t_vs_region );
    if (i4_ret < 0)
    {
        INPUT_LOGD(("[ERROR][%d]{%s}(i4_ret:%d)\n", __LINE__, __func__, (int)i4_ret));
    }

    {
        jint                    left    = (jint)(t_vs_region.ui4_x);        
        jint                    top     = (jint)(t_vs_region.ui4_y);
        jint                    right   = (jint)(t_vs_region.ui4_x + t_vs_region.ui4_width);
        jint                    bottom  = (jint)(t_vs_region.ui4_y + t_vs_region.ui4_height);

        INPUT_LOGD(("[%d]{%s}[%d][%d][%d][%d]\n", __LINE__, __func__, left, right, top, bottom));
        
        CLASS_VOID_METHODV( InputRegion, setLeft,   left   );
        CLASS_VOID_METHODV( InputRegion, setRight,  right  ); 
        CLASS_VOID_METHODV( InputRegion, setTop,    top    );   
        CLASS_VOID_METHODV( InputRegion, setBottom, bottom );
    }
    
    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    
    return (jint)i4_ret;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    inputSourceGetData
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_inputSourceExchangeData
  (JNIEnv *env, jclass clazz, jintArray InputSourceData)
{
    INT32                   i4_ret = 0;
    INT32                   i4_len;
    INT32                   ai4_input_grp[_JNI_MAX_INPUT_EXCHANGE_DATA_SIZE];

    i4_len = (INT32)JNI_GET_ARRAY_LEN(env, InputSourceData);
    if (_JNI_MAX_INPUT_EXCHANGE_DATA_SIZE < i4_len)
    {
        INPUT_LOGD(("[ERROR][%d]{%s}(jni GET type length is not enough)\n", __LINE__, __func__));
        return INPUT_SOURCE_FAIL;
    }

    if (0 >= i4_len)
    {
        INPUT_LOGD(("[ERROR][%d]{%s}(jni size of GET type array should be bigger than ZERO)\n", __LINE__, __func__));
        return INPUT_SOURCE_FAIL;
    }

    INPUT_LOGD(("[INFO ][%d]{%s} enter\n", __LINE__, __func__));

    INPUT_JNI_GET_INT_ARRAY_REGION(env, InputSourceData, i4_len, (jint *)ai4_input_grp);
    
    {
        TV_WIN_ID_T                     t_win_id    = _jni_j2c_output_id(ai4_input_grp[InputService_INPUT_EXCHANGE_HEADER_OUTPUT_IDX]);
        VSH_REGION_CAPABILITY_INFO_T    t_info      = {0};
        INT32                           i           = InputService_INPUT_EXCHANGE_HEADER_LEN;

        INPUT_LOGD(("[INFO ][%d]{%s}[type:%d]\n", __LINE__, __func__, (int)ai4_input_grp[InputService_INPUT_EXCHANGE_HEADER_TYPE_IDX]));
        
        switch (ai4_input_grp[InputService_INPUT_EXCHANGE_HEADER_TYPE_IDX])
        {
            case InputService_INPUT_GET_TYPE_UNKNOWN                     :
                {
                    INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_GET_TYPE_UNKNOWN\n", __LINE__, __func__));
                }
                break;
                
            case InputService_INPUT_GET_TYPE_OUTPUT_REGION_CAPABILITY:
                {
                    INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_GET_TYPE_OUTPUT_REGION_CAPABILITY\n", __LINE__, __func__));
                    t_info.e_get_type = SM_VSH_GET_TYPE_DISP_REGION;
                    a_inps_get_stream_attr( t_win_id, SM_VSH_GET_TYPE_REGION_CAPABILITY, &t_info, sizeof(t_info) );

                    ai4_input_grp[i++] = (INT32)t_info.b_is_enable;   
                    ai4_input_grp[i++] = t_info.ui4_x_min;
                    ai4_input_grp[i++] = t_info.ui4_x_max;
                    ai4_input_grp[i++] = t_info.ui4_y_min;
                    ai4_input_grp[i++] = t_info.ui4_y_max;
                    ai4_input_grp[i++] = t_info.ui4_width_min;
                    ai4_input_grp[i++] = t_info.ui4_width_max;
                    ai4_input_grp[i++] = t_info.ui4_height_min;
                    ai4_input_grp[i++] = t_info.ui4_height_max;

                    INPUT_LOGD(("[INFO ][%d]{%s} OUTPUT_REGION_CAPABILITY enable:(%d) x:(%d, %d) y:(%d, %d) width:(%d, %d) height:(%d, %d)\n", 
                        __LINE__, __func__, 
                        t_info.b_is_enable, 
                        (int)t_info.ui4_x_min, (int)t_info.ui4_x_max, (int)t_info.ui4_y_min, (int)t_info.ui4_y_max, 
                        (int)t_info.ui4_width_min, (int)t_info.ui4_width_max, (int)t_info.ui4_height_min, (int)t_info.ui4_height_max ));
                }
                break;
                
            case InputService_INPUT_GET_TYPE_VIDEO_REGION_CAPABILITY:
                {
                    INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_GET_TYPE_VIDEO_REGION_CAPABILITY\n", __LINE__, __func__));
                    t_info.e_get_type = SM_VSH_GET_TYPE_VIDEO_REGION;
                    a_inps_get_stream_attr( t_win_id, SM_VSH_GET_TYPE_REGION_CAPABILITY, &t_info, sizeof(t_info) );

                    ai4_input_grp[i++] = (INT32)t_info.b_is_enable;   
                    ai4_input_grp[i++] = t_info.ui4_x_min;
                    ai4_input_grp[i++] = t_info.ui4_x_max;
                    ai4_input_grp[i++] = t_info.ui4_y_min;
                    ai4_input_grp[i++] = t_info.ui4_y_max;
                    ai4_input_grp[i++] = t_info.ui4_width_min;
                    ai4_input_grp[i++] = t_info.ui4_width_max;
                    ai4_input_grp[i++] = t_info.ui4_height_min;
                    ai4_input_grp[i++] = t_info.ui4_height_max;

                    INPUT_LOGD(("[INFO ][%d]{%s} VIDEO_REGION_CAPABILITY enable:(%d) x:(%d, %d) y:(%d, %d) width:(%d, %d) height:(%d, %d)\n", 
                        __LINE__, __func__, 
                        t_info.b_is_enable, 
                        (int)t_info.ui4_x_min, (int)t_info.ui4_x_max, (int)t_info.ui4_y_min, (int)t_info.ui4_y_max, 
                        (int)t_info.ui4_width_min, (int)t_info.ui4_width_max, (int)t_info.ui4_height_min, (int)t_info.ui4_height_max ));
                }
                break;
                
            case InputService_INPUT_GET_TYPE_VIDEO_RESOLUTION:
                {
                    VSH_SRC_RESOLUTION_INFO_T   t_src_res;
                    BOOL                        b_is_hd;

                    INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_GET_TYPE_VIDEO_RESOLUTION\n", __LINE__, __func__));
                    a_inps_get_video_src_resolution( t_win_id, &t_src_res );

                    ai4_input_grp[i++] = t_src_res.ui4_width;
                    ai4_input_grp[i++] = t_src_res.ui4_height;
                    ai4_input_grp[i++] = (t_src_res.b_is_progressive)? 1:0;
                    ai4_input_grp[i++] = t_src_res.ui4_frame_rate;

                    a_inps_get_video_hd( t_src_res.ui4_height, &b_is_hd );

                    ai4_input_grp[i++] = b_is_hd;

                    ai4_input_grp[i++] = (INT32)t_src_res.e_timing_type;

                    ai4_input_grp[i++] = (INT32)t_src_res.e_src_asp_ratio;

                    ai4_input_grp[i++] = (INT32)t_src_res.e_src_tag3d_type;

                    INPUT_LOGD(("[INFO ][%d]{%s} (%d*%d) %c @ %dHZ %s, timing_type(%d), asp_ratio(%d), tag3D(%d)\n", 
                        __LINE__, __func__, 
                        (int)t_src_res.ui4_width, (int)t_src_res.ui4_height, 
                        t_src_res.b_is_progressive?'P':'I', (int)t_src_res.ui4_frame_rate, 
                        b_is_hd? "HD":"SD", (int)t_src_res.e_timing_type,
                        (int)t_src_res.e_src_asp_ratio, (int)t_src_res.e_src_tag3d_type ));
                }
                break;
                
            case InputService_INPUT_GET_TYPE_COLOR_SYS:
                {
                    int                 inps_color_sys = InputService_INPUT_GET_TYPE_COLOR_SYS;
                    SCC_VID_COLOR_SYS_T t_color_sys = SCC_VID_COLOR_SYS_UNKNOWN;

                    INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_GET_TYPE_UNKNOWN\n", __LINE__, __func__));
                    a_inps_get_vid_color_sys( &t_color_sys );

                    switch (t_color_sys)
                    {
                        case SCC_VID_COLOR_SYS_UNKNOWN : inps_color_sys = InputService_INPUT_COLOR_SYS_UNKNOWN ; break;
                        case SCC_VID_COLOR_SYS_NTSC    : inps_color_sys = InputService_INPUT_COLOR_SYS_NTSC    ; break;
                        case SCC_VID_COLOR_SYS_NTSC_443: inps_color_sys = InputService_INPUT_COLOR_SYS_NTSC_443; break;
                        case SCC_VID_COLOR_SYS_PAL     : inps_color_sys = InputService_INPUT_COLOR_SYS_PAL     ; break;
                        case SCC_VID_COLOR_SYS_PAL_N   : inps_color_sys = InputService_INPUT_COLOR_SYS_PAL_N   ; break;
                        case SCC_VID_COLOR_SYS_PAL_M   : inps_color_sys = InputService_INPUT_COLOR_SYS_PAL_M   ; break;
                        case SCC_VID_COLOR_SYS_PAL_60  : inps_color_sys = InputService_INPUT_COLOR_SYS_PAL_60  ; break;
                        case SCC_VID_COLOR_SYS_SECAM   : inps_color_sys = InputService_INPUT_COLOR_SYS_SECAM   ; break;
                        default: break;
                    }

                    ai4_input_grp[i++] = inps_color_sys;

                    INPUT_LOGD(("[INFO ][%d]{%s} (%d)\n", 
                        __LINE__, __func__, 
                        inps_color_sys ));
                }
                break;
                
            case InputService_INPUT_GET_TYPE_PLANE_ORDER:
                {
                    VSH_GET_PLANE_ORDER_INFO_T   t_plane_order;

                    INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_GET_TYPE_PLANE_ORDER\n", __LINE__, __func__));
                    a_inps_get_stream_attr( t_win_id, SM_VSH_GET_TYPE_PLANE_ORDER, &t_plane_order, sizeof(t_plane_order) );

                    ai4_input_grp[i++] = (INT32)t_plane_order.ui1_curr_layer;
                    ai4_input_grp[i++] = (INT32)t_plane_order.ui1_num_layers;

                    INPUT_LOGD(("[INFO ][%d]{%s}: curr_layer=(%d), num_layers=(%d)\n", 
                        __LINE__, __func__, 
                        t_plane_order.ui1_curr_layer, t_plane_order.ui1_num_layers ));
                }
                break;
                
            case InputService_INPUT_SET_TYPE_UNKNOWN:
                {
                    INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_SET_TYPE_UNKNOWN\n", __LINE__, __func__));
                }
                break;
                
            case InputService_INPUT_SET_TYPE_PLANE_ORDER:
                {
                    VSH_SET_PLANE_ORDER_INFO_T  t_plane_order;
                    BOOL                        fg_valid = TRUE;

                    INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_SET_TYPE_PLANE_ORDER\n", __LINE__, __func__));

                    switch (ai4_input_grp[i++])
                    {
                        case InputService_INPUT_SET_PLANE_ORDER_TYPE_TOP    : 
                            t_plane_order.u.ui1_num_layers = ai4_input_grp[i++];
                            t_plane_order.e_order_ctrl = VSH_PLANE_ORDER_CTRL_TOP   ; 
                            break;
                            
                        case InputService_INPUT_SET_PLANE_ORDER_TYPE_UP     : 
                            t_plane_order.u.ui1_num_layers = ai4_input_grp[i++];
                            t_plane_order.e_order_ctrl = VSH_PLANE_ORDER_CTRL_UP    ; 
                            break;
                            
                        case InputService_INPUT_SET_PLANE_ORDER_TYPE_DOWN   : 
                            t_plane_order.u.ui1_num_layers = ai4_input_grp[i++];
                            t_plane_order.e_order_ctrl = VSH_PLANE_ORDER_CTRL_DOWN  ; 
                            break;
                            
                        case InputService_INPUT_SET_PLANE_ORDER_TYPE_BOTTOM : 
                            t_plane_order.u.ui1_num_layers = ai4_input_grp[i++];
                            t_plane_order.e_order_ctrl = VSH_PLANE_ORDER_CTRL_BOTTOM; 
                            break;
                        
                        case InputService_INPUT_SET_PLANE_ORDER_TYPE_UNKNOWN:
                        default: 
                            fg_valid = FALSE;
                            break;
                    }

                    if (fg_valid)
                    {
                        INPUT_LOGD(("[INFO ][%d]{%s} order_ctrl=(%d), num_layers=(%d)\n", __LINE__, __func__, 
                            t_plane_order.e_order_ctrl,
                            t_plane_order.u.ui1_num_layers ));
                        a_inps_set_stream_attr(t_win_id, SM_VSH_SET_TYPE_PLANE_ORDER, &t_plane_order, sizeof(t_plane_order));
                    }
                    else
                    {
                        INPUT_LOGD(("[INFO ][%d]{%s} fg_valid = FALSE!\n", __LINE__, __func__));
                    }
                }
                break;
                
            case InputService_INPUT_SET_TYPE_PLANE_ORDER_SWAP:
                {
                    VSH_SET_PLANE_ORDER_INFO_T  t_plane_order;

                    INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_SET_TYPE_PLANE_ORDER_SWAP\n", __LINE__, __func__));


                    t_plane_order.u.ui1_layer  = ai4_input_grp[i++];
                    t_plane_order.e_order_ctrl = VSH_PLANE_ORDER_CTRL_SWAP; 


                    INPUT_LOGD(("[INFO ][%d]{%s} order_ctrl=(%d), layer=(%d)\n", __LINE__, __func__, 
                        t_plane_order.e_order_ctrl,
                        t_plane_order.u.ui1_layer ));
                    a_inps_set_stream_attr(t_win_id, SM_VSH_SET_TYPE_PLANE_ORDER, &t_plane_order, sizeof(t_plane_order));

                }
                break;
                
            case InputService_INPUT_SET_AUTO_ADJUST:
                {
                    INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_SET_AUTO_ADJUST\n", __LINE__, __func__));
                    switch (ai4_input_grp[i++])
                    {
                        case InputService_INPUT_SET_AUTO_ADJUST_TYPE_VGA_ADJUST: 
                            INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_SET_AUTO_ADJUST_TYPE_VGA_ADJUST\n", __LINE__, __func__));
                        case InputService_INPUT_SET_AUTO_ADJUST_TYPE_PHASE     : 
                            INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_SET_AUTO_ADJUST_TYPE_PHASE\n", __LINE__, __func__));
                            
                            {
                                a_inps_set_stream_attr(t_win_id, SM_VSH_SET_TYPE_AUTO_CLK_PHS_POS, NULL, 0);
                            }
                            
                            break;
                            
                        case InputService_INPUT_SET_AUTO_ADJUST_TYPE_COLOR     : 
                            INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_SET_AUTO_ADJUST_TYPE_COLOR\n", __LINE__, __func__));
                            {
                                a_inps_set_stream_attr(t_win_id, SM_VSH_SET_TYPE_AUTO_COLOR, NULL, 0);
                            }
                            break;
                            
                        case InputService_INPUT_SET_AUTO_ADJUST_TYPE_UNKNOWN   : 
                            INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_SET_AUTO_ADJUST_TYPE_UNKNOWN\n", __LINE__, __func__));
                            break;
                            
                        default: 
                            INPUT_LOGD(("[INFO ][%d]{%s}[Warning]default\n", __LINE__, __func__));
                            break;
                    }
                }
                break;
                
            case InputService_INPUT_SET_ENTER_POP_AND_RETURN_FOCUS:
                INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_SET_ENTER_POP_AND_RETURN_FOCUS\n", __LINE__, __func__));
                a_inps_enter_pop();
                break;
                
            case InputService_INPUT_SET_ENTER_PIP_AND_RETURN_FOCUS:
                INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_SET_ENTER_PIP_AND_RETURN_FOCUS\n", __LINE__, __func__));
                a_inps_enter_pip();
                break;
                
            case InputService_INPUT_SET_ENTER_NORMAL:
                INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_SET_ENTER_NORMAL\n", __LINE__, __func__));
                a_inps_enter_normal();
                break;
                
            case InputService_INPUT_SET_FOCUS_CHANGE_TO:
                {
                    TV_WIN_ID_T                 t_dst_output_win_id = _jni_j2c_output_id(ai4_input_grp[i++]);

                    INPUT_LOGD(("[INFO ][%d]{%s}InputService_INPUT_SET_FOCUS_CHANGE_TO\n", __LINE__, __func__));
                    a_inps_pip_focus_change( t_dst_output_win_id );
                }
                break;

            /* ***************************************************************************************************************** */
            /* For RPC-IPC, the command following InputService_INPUT_SET_FOCUS_CHANGE_TO will use new dtv_svc_client API */
            /* ***************************************************************************************************************** */
            default:
                a_inps_exchange_data(ai4_input_grp, (UINT32)i4_len);

                if (0)
                {
                    UINT16  ui2_data_idx = 0;

                    INPUT_LOGD(("[INFO_exchange]\n"));
                    for (ui2_data_idx = 0; ui2_data_idx < i4_len; ui2_data_idx++)
                    {
                        INPUT_LOGD(("[INFO_exchange][%d]\n", (int)ai4_input_grp[ui2_data_idx]));
                    }
                    INPUT_LOGD(("[INFO_exchange]\n"));
                }
                break;                
        }
    }

    INPUT_LOGD(("[INFO ][%d]{%s} leave\n", __LINE__, __func__));

    INPUT_JNI_SET_INT_ARRAY_REGION(env, InputSourceData, i4_len, (jint *)ai4_input_grp);

    return (jint)i4_ret;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    inputServiceSetOutputMute
 * Signature: (IZ)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_inputServiceSetOutputMute
  (JNIEnv *env, jclass clazz, jint output, jboolean mute)
{
    INT32                   i4_ret;
    BOOL                    fg_need_mute    = mute;
    TV_WIN_ID_T             t_win_id        = _jni_j2c_output_id(output);

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));

    i4_ret = a_inps_mute( t_win_id, fg_need_mute );
    if (i4_ret < 0)
    {
        INPUT_LOGD(("[ERROR][%d]{%s}(i4_ret:%d)\n", __LINE__, __func__, (int)i4_ret));
    }

    INPUT_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    
    return (jint)i4_ret;
}


EXTERN_C_END




