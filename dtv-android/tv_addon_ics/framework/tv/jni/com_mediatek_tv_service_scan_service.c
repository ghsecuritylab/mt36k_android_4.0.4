#include "tv_jni_util.h"
#include "com_mediatek_tv_service_TVNative.h"

#if 1
#include "scan/scan_service_client.h"
#else
typedef signed long     INT32;
#define VOID  void

typedef unsigned char  UINT8;    /**<        */
typedef UINT8  BOOL;    /**<        */
typedef unsigned short  UINT16;    /**<        */
typedef unsigned long    UINT32;    /**<        */
typedef UINT32  HANDLE_T;

#define MAKE_BIT_MASK_16(_val) (((UINT16) 1) << (_val))    /**<        */
#define MAKE_BIT_MASK_32(_val) (((UINT32) 1) << (_val))    /**<        */

typedef enum
{
    COLOR_SYS_UNKNOWN = -1, /* Must be set to '-1' else I loose an entry in the bit mask. */
    COLOR_SYS_NTSC,
    COLOR_SYS_PAL,
    COLOR_SYS_SECAM,
    COLOR_SYS_NTSC_443,
    COLOR_SYS_PAL_M,
    COLOR_SYS_PAL_N,
    COLOR_SYS_PAL_60
}   COLOR_SYS_T;
typedef enum
{
    AUDIO_SYS_UNKNOWN = -1, /* Must be set to '-1' else I loose an entry in the bit mask. */
    AUDIO_SYS_AM,
    AUDIO_SYS_FM_MONO,
    AUDIO_SYS_FM_EIA_J,
    AUDIO_SYS_FM_A2,
    AUDIO_SYS_FM_A2_DK1,
    AUDIO_SYS_FM_A2_DK2,
    AUDIO_SYS_FM_RADIO,
    AUDIO_SYS_NICAM,
    AUDIO_SYS_BTSC
}   AUDIO_SYS_T;
typedef enum
{
    TV_SYS_UNKNOWN = -1, /* Must be set to '-1' else I loose an entry in the bit mask. */
    TV_SYS_A,
    TV_SYS_B,
    TV_SYS_C,
    TV_SYS_D,
    TV_SYS_E,
    TV_SYS_F,
    TV_SYS_G,
    TV_SYS_H,
    TV_SYS_I,
    TV_SYS_J,
    TV_SYS_K,
    TV_SYS_K_PRIME,
    TV_SYS_L,
    TV_SYS_L_PRIME,
    TV_SYS_M,
    TV_SYS_N,
/*  Entries below are defined in AUD_TV_SYS_T, and used by audio driver
    AUD_TV_SYS_A2 = 16,
    AUD_TV_SYS_PAL = 17,
    AUD_TV_SYS_NICAM = 18,
    AUD_TV_SYS_SECAM = 19 */
    TV_SYS_AUTO = 31
}   TV_SYS_T;


#define ANALOG_SCAN_OK                  0
#define ANALOG_SCAN_FAIL               -1
typedef enum
{
    SB_PAL_SECAM_SCAN_TYPE_UNKNOWN = 0,
    SB_PAL_SECAM_SCAN_TYPE_FULL_MODE,
    SB_PAL_SECAM_SCAN_TYPE_RANGE_MODE,
    SB_PAL_SECAM_SCAN_TYPE_UPDATE_MODE,
    SB_PAL_SECAM_SCAN_TYPE_NUM /*  */
} SB_PAL_SECAM_SCAN_TYPE_T;

typedef enum
{
    SB_KEY_TYPE_UNKNOWN = 0,
    SB_KEY_TYPE_TUNER_FREQ_RANGE
} SB_KEY_TYPE_T;

typedef struct _SB_PAL_SECAM_CUSTOM_SVL_CONFIG
{
    UINT32  ui4_tv_sys_mask;
    UINT32  ui4_aud_sys_mask;
} SB_PAL_SECAM_CUSTOM_SVL_CONFIG;

typedef UINT32      SB_PAL_SECAM_CONFIG_T;    /**< SB Pal Secam Engine config type       */


#define SB_PAL_SECAM_CONFIG_PLAY_VIDEO_ONLY                         (MAKE_BIT_MASK_32(0))    /**< To play the video or not during scan                                            */
#define SB_PAL_SECAM_CONFIG_RANGE_SCAN_NO_WRAP_AROUND               (MAKE_BIT_MASK_32(1))    /**< To wrap around to the lowest frequency or not while the range scan cannot find 
                                                                                                  a valid channel till the highest frquency                                       */

#define SB_PAL_SECAM_CONFIG_START_CH_NUM                            (MAKE_BIT_MASK_32(2))    /**< To customize the the start channel numbers                                      */

#define SB_PAL_SECAM_CONFIG_IGNORE_DVB_CH_ON_SORTING                (MAKE_BIT_MASK_32(3))    /**< To ignore the DVB channels or not while sorting the PAL/SECAM channels.
                                                                                                  Note that to set this flag might cause the channel number collision between
                                                                                                  the digital and analog channels                                                 */

#define SB_PAL_SECAM_CONFIG_UPDATE_TO_TEMP_SVL                      (MAKE_BIT_MASK_32(4))    /**< To store the service records to the given temporary SVL                         */

#define SB_PAL_SECAM_CONFIG_CUSTOMIZE_ANAS                          (MAKE_BIT_MASK_32(5))    /**< Enable the customization of Auto-Naming-Auto-Sorting. The engine shall report 
                                                                                                  the CNI codes as well as channel information to the user so that it could 
                                                                                                  determine the channel names and sorting by itself. Also the engine shall 
                                                                                                  store the records into the temporary TSL/SVL.                                   */

#define SB_PAL_SECAM_CONFIG_ENABLE_ACI                              (MAKE_BIT_MASK_32(6))    /**< Enable the ACI functionality.                                                   */

#define SB_PAL_SECAM_CONFIG_IGNORE_TV_SYS_NOT_IN_CANDIDATE_LIST     (MAKE_BIT_MASK_32(7))    /**< Ignore detected format(B/G,D/K/I,L) that is not in the candidate list           */

#define SB_PAL_SECAM_CONFIG_ACI_MULTILVL_RGN                        (MAKE_BIT_MASK_32(8))    /**< Enable multi-level ACI                                                          */

#define SB_PAL_SECAM_CONFIG_TIME_OUT_FINE_TUNE                      (MAKE_BIT_MASK_32(9))    /**< Enable fine tune time out length, unit in miliseconds                           */

#define SB_PAL_SECAM_CONFIG_IGNORE_START_CH_NUM_CUSTOMIZATION       (MAKE_BIT_MASK_32(10))   /**< To ignore the start channel number customization                                */

#define SB_PAL_SECAM_CONFIG_DISABLE_VBIF                            (MAKE_BIT_MASK_32(11))   /**< To disable VBIF for Pan-Asia                                                    */

#define SB_PAL_SECAM_CONFIG_DISABLE_SKIP_DVB_CH_FREQUENCY           (MAKE_BIT_MASK_32(12))   /**< To disable update scan skip DTV channels' frequencies                           */

#define SB_PAL_SECAM_CONFIG_RANGE_SCAN_INFINITE_LOOP                (MAKE_BIT_MASK_32(13))   /**< Enable manual scan with infinite loop (no termination)                          */

#define SB_PAL_SECAM_CONFIG_SCAN_REPLACE_EXISTING_CHANNEL           (MAKE_BIT_MASK_32(14))   /**< Enable manual scan result stored to a specific channel                          */

#define SB_PAL_SECAM_CONFIG_SCAN_WITH_DESIGNATED_TV_SYS             (MAKE_BIT_MASK_32(15))   /**< Scan with fixed TV system                                                       */

#define SB_PAL_SECAM_CONFIG_SCAN_WITH_TV_BYPASS                     (MAKE_BIT_MASK_32(16))   /**< Scan with TV bypass enabled                                                     */

#define SB_PAL_SECAM_CONFIG_SCAN_WITH_MONITOR_BYPASS                (MAKE_BIT_MASK_32(17))   /**< Scan with Monitor bypass enabled                                                */

#define SB_PAL_SECAM_CONFIG_SCAN_CABLE_ANA                          (MAKE_BIT_MASK_32(18))   /**< To use cabel analog scan                                                        */

#if 1
#define SB_PAL_SECAM_CONFIG_SCAN_WITHOUT_DSP_DETECT_TV_SYS          (MAKE_BIT_MASK_32(19))   /**< Scan without DSP detect */
#define SB_PAL_SECAM_CONFIG_SCAN_ALL_CH_IN_RANGE_MODE               (MAKE_BIT_MASK_32(20))   /**< Scan all channel in range mode*/
#define SB_PAL_SECAM_CONFIG_FREQUENCY_AUTO_NFY                      (MAKE_BIT_MASK_32(21))   /**< Frequency automatic notify */
#define SB_PAL_SECAM_CONFIG_SCAN_WITH_DESIGNATED_COLOR_SYS          (MAKE_BIT_MASK_32(22))   /**< Scan with fixed TV system                                                       */
#endif

#define SB_PAL_SECAM_CONFIG_CUSTOM_SVL_CONFIG                       (MAKE_BIT_MASK_32(23))   /**< To use cabel analog scan                                                        */


typedef struct _ANALOG_SCAN_DATA_T
{
    SB_PAL_SECAM_SCAN_TYPE_T              e_scan_type;
    UINT32                                ui4_start_freq; 
    UINT32                                ui4_end_freq;
    UINT32                                ui2_start_ch_id_to_scan;
    UINT32                                ui2_end_ch_id_to_scan;
    
    UINT32                                ui4_designated_chk_tv_sys;        /* For TV sys specified by application to check */
    UINT32                                ui4_designated_chk_tv_audio_sys;  /* For TV sys specified by application to check */
    INT32                                 i4_designated_chk_color_sys;      /* For color sys specified by application to check */
    
    BOOL                                  b_neg_dir;
    SB_PAL_SECAM_CONFIG_T                 t_eng_cfg_flag;
    
    HANDLE_T                              h_svctx;
    HANDLE_T                              h_svctx_monitor_scart_bypass;
    HANDLE_T                              h_svctx_tv_scart_bypass;
    
    SB_PAL_SECAM_CUSTOM_SVL_CONFIG        t_custom_svl_config;
    UINT16                                ui2_temp_svl_id;
} ANALOG_SCAN_DATA_T;


typedef VOID (*SetScanProgressNfy) ( INT32 progress, INT32 channels);
typedef VOID (*SetScanFrequenceNfy) ( INT32 frequence);
typedef VOID (*SetScanCompletedNfy) ( );
typedef VOID (*SetScanCanceledNfy) ( );
typedef VOID (*SetScanErrorNfy) ( INT32 error );


typedef struct _ANALOG_SCAN_NFY_T
{
    SetScanProgressNfy      setScanProgressNfy;
    SetScanFrequenceNfy     setScanFrequenceNfy;
    SetScanCompletedNfy     setScanCompletedNfy;
    SetScanCanceledNfy      setScanCanceledNfy;
    SetScanErrorNfy         setScanErrorNfy;
} ANALOG_SCAN_NFY_T;

typedef struct _SB_FREQ_RANGE_T
{
    UINT32  ui4_upper_freq;
    UINT32  ui4_lower_freq;
}   SB_FREQ_RANGE_T;


ANALOG_SCAN_NFY_T       g_t_scan_nfy;

INT32 analogScanInit( VOID );
INT32 analogScanStart( 
        ANALOG_SCAN_DATA_T*     pt_analog_data,
        ANALOG_SCAN_NFY_T*      pt_scan_nfy )
{
    g_t_scan_nfy = *pt_scan_nfy;
}
INT32 analogScanCancel()
{
    ANALOG_SCAN_NFY_T*      pt_scan_nfy = &g_t_scan_nfy;
    
    pt_scan_nfy->setScanProgressNfy( 123, 321 );
    pt_scan_nfy->setScanFrequenceNfy( 123321 );
    pt_scan_nfy->setScanCompletedNfy();
    pt_scan_nfy->setScanCanceledNfy();
    pt_scan_nfy->setScanErrorNfy( 44444 );
}
INT32 analogScanGet(
        SB_KEY_TYPE_T           e_key_type,
        SB_FREQ_RANGE_T*        pt_freq_rang )
{
}
#endif


EXTERN_C_START

#define LOG_TAG "scan_service"

/* the value HAVE TO be same as the one in JAVA code -----------------------------------start------- */
static const int JNI_SCAN_TV_SYS_A              = 1 << 0;
static const int JNI_SCAN_TV_SYS_B              = 1 << 1;
static const int JNI_SCAN_TV_SYS_C              = 1 << 2;
static const int JNI_SCAN_TV_SYS_D              = 1 << 3;
static const int JNI_SCAN_TV_SYS_E              = 1 << 4;
static const int JNI_SCAN_TV_SYS_F              = 1 << 5;
static const int JNI_SCAN_TV_SYS_G              = 1 << 6;
static const int JNI_SCAN_TV_SYS_H              = 1 << 7;
static const int JNI_SCAN_TV_SYS_I              = 1 << 8;
static const int JNI_SCAN_TV_SYS_J              = 1 << 9;
static const int JNI_SCAN_TV_SYS_K              = 1 <<10;
static const int JNI_SCAN_TV_SYS_K_PRIME        = 1 <<11;
static const int JNI_SCAN_TV_SYS_L              = 1 <<12;
static const int JNI_SCAN_TV_SYS_L_PRIME        = 1 <<13;
static const int JNI_SCAN_TV_SYS_M              = 1 <<14;
static const int JNI_SCAN_TV_SYS_N              = 1 <<15;
static const int JNI_SCAN_TV_SYS_AUTO           = 1 <<31;

static const int JNI_SCAN_COLOR_SYS_UNKNOWN     = -1;
static const int JNI_SCAN_COLOR_SYS_NTSC        = 0;
static const int JNI_SCAN_COLOR_SYS_PAL         = 1;
static const int JNI_SCAN_COLOR_SYS_SECAM       = 2;
static const int JNI_SCAN_COLOR_SYS_NTSC_443    = 3;
static const int JNI_SCAN_COLOR_SYS_PAL_M       = 4;
static const int JNI_SCAN_COLOR_SYS_PAL_N       = 5;
static const int JNI_SCAN_COLOR_SYS_PAL_60      = 6;

static const int JNI_SCAN_AUDIO_SYS_UNKNOWN     = 0;
static const int JNI_SCAN_AUDIO_SYS_AM          = 1 << 0;
static const int JNI_SCAN_AUDIO_SYS_FM_MONO     = 1 << 1;
static const int JNI_SCAN_AUDIO_SYS_FM_EIA_J    = 1 << 2;
static const int JNI_SCAN_AUDIO_SYS_FM_A2       = 1 << 3;
static const int JNI_SCAN_AUDIO_SYS_FM_A2_DK1   = 1 << 4;
static const int JNI_SCAN_AUDIO_SYS_FM_A2_DK2   = 1 << 5;
static const int JNI_SCAN_AUDIO_SYS_FM_RADIO    = 1 << 6;
static const int JNI_SCAN_AUDIO_SYS_NICAM       = 1 << 7;
static const int JNI_SCAN_AUDIO_SYS_BTSC        = 1 << 8;
/* the value HAVE TO be same as the one in JAVA code -----------------------------------end--------- */



static const int PAL_SECAM_SCAN_CFG_PLAY_VIDEO_ONLY = 1 << 0;
static const int PAL_SECAM_SCAN_CFG_RANGE_SCAN_NO_WRAP_AROUND = 1 << 1;
static const int PAL_SECAM_SCAN_CFG_START_CH_NUM = 1 << 2;
static const int PAL_SECAM_SCAN_CFG_SCAN_REPLACE_EXISTING_CHANNEL = 1 << 3;
static const int PAL_SECAM_SCAN_CFG_SCAN_WITH_DESIGNATED_TV_SYS = 1 << 4;
static const int PAL_SECAM_SCAN_CFG_SCAN_WITH_TV_BYPASS = 1 << 5;
static const int PAL_SECAM_SCAN_CFG_SCAN_WITH_MONITOR_BYPASS = 1 << 6;
static const int PAL_SECAM_SCAN_CFG_SCAN_WITHOUT_DSP_DETECT_TV_SYS = 1 << 7;
static const int PAL_SECAM_SCAN_CFG_SCAN_ALL_CH_IN_RANGE_MODE = 1 << 8;
static const int PAL_SECAM_SCAN_CFG_SCAN_WITH_DESIGNATED_COLOR_SYS = 1 << 9;
static const int PAL_SECAM_SCAN_CFG_CUSTOM_SVL_CONFIG = 1 << 10;
static const int PAL_SECAM_SCAN_CFG_UPDATE_TO_TEMP_SVL = 1 << 11;
static const int PAL_SECAM_SCAN_CFG_USER_OPERATION = 1 << 12;
static const int PAL_SECAM_SCAN_CFG_ACTUAL_COLOR_SYS = 1 << 13;


typedef enum {
    getmScanType = 0,
    getmScanChannelFrom,
    getmScanChannelTo,
    getmScanFreqFrom,
    getmScanFreqTo,
    scanParamsMethodsNum
}   _scanParamsMethods;
jmethodID   ScanParamsMethods[scanParamsMethodsNum];
jclass      ScanParamsClass;

typedef enum {
    getDesignatedCheckTvSystem = 0,
    getDesignatedCheckAudioSystem,
    getDesignatedCheckColorSystem,
    getIsNegativeDirection,
    getScanCfg,
    getTvInput,
    getTvBypass,
    getMonitorBypass,
    getCustomCfgTvSystem,
    getCustomCfgAudioSystem,
    getSvlId,
    scanParaPalSecamMethodsNum
}   _scanParaPalSecamMethods;
jmethodID   ScanParaPalSecamMethods[scanParaPalSecamMethodsNum];
jclass      ScanParaPalSecamClass;

typedef enum {
    setScanProgress = 0,
    setScanFrequence,
    setScanCompleted,
    setScanCanceled,
    setScanError,
    onScanUserOperation,
    scanServiceMethodsNum
}   _scanServiceMethods;
jmethodID   ScanServiceMethods[scanServiceMethodsNum];
jclass      ScanServiceClass;

typedef enum {
    setTunerLowerBound = 0,
    setTunerUpperBound,
    scanExchangeFrenquenceRangeMethodsNum
}   _scanExchangeFrenquenceRangeMethods;
jmethodID   ScanExchangeFrenquenceRangeMethods[scanExchangeFrenquenceRangeMethodsNum];
jclass      ScanExchangeFrenquenceRangeClass;


#define SCAN_JNI_GET_CLASS_STATIC_METHOD(ENV,CLASS,NAME,SIG)        ((*ENV)->GetStaticMethodID(ENV,CLASS,NAME,SIG))
#define SCAN_JNI_NEW_GLOBAL_REF(ENV,CLASS)                          ((*ENV)->NewGlobalRef(ENV,CLASS))
#define SCAN_JNI_NEW_INT_ARRAY(ENV,SIZE)                            ((*ENV)->NewIntArray(ENV,SIZE))
#define SCAN_JNI_SET_INT_ARRAY_REGION(ENV,ARRAY,SIZE,args)          ((*ENV)->SetIntArrayRegion(ENV,ARRAY,0,SIZE,args))
#define SCAN_JNI_GET_INT_ARRAY_REGION(ENV,ARRAY,SIZE,args)          ((*ENV)->GetIntArrayRegion(ENV,ARRAY,0,SIZE,args))
#define SCAN_JNI_GET_JAVAVM(ENV)                                    ((*ENV)->GetJavaVM(ENV, &g_pt_jni_scan_JavaVM))
#define SCAN_JNI_ATTACH_CURRENT_THREAD(ENV)                         ((*g_pt_jni_scan_JavaVM)->AttachCurrentThread(g_pt_jni_scan_JavaVM, &ENV, NULL))
#define SCAN_JNI_DETACH_CURRENT_THREAD(ENV)                         ((*g_pt_jni_scan_JavaVM)->DetachCurrentThread(g_pt_jni_scan_JavaVM))

#define CLASS_METHOD(  className, method )                  (*env)->CallIntMethod(env, className, className##Methods[method])
#define CLASS_METHODV( className, method, args...)          (*env)->CallIntMethod(env, className, className##Methods[method], args)
#define CLASS_STATIC_METHOD(  className, method )           (*env)->CallStaticIntMethod(env, className, className##Methods[method])
#define CLASS_STATIC_METHODV( className, method, args...)   (*env)->CallStaticIntMethod(env, className, className##Methods[method], args)


static JNIEnv *g_pt_jni_scan_env;
static JavaVM *g_pt_jni_scan_JavaVM;

void x_scan_service_init_jni (JNIEnv *env)
{
    jclass      _ScanParamsClass;
    jclass      _ScanParaPalSecamClass;
    jclass      _ScanServiceClass;
    jclass      _ScanExchangeFrenquenceRangeClass;
    JNI_LOGD(("[%-65s] {%s}\n", __func__, "init"));
    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));

    if (SCAN_JNI_GET_JAVAVM(env) < 0)
    {
        JNI_LOGD(("[ERROR][%d]{%s}\n", __LINE__, __func__));
    }
    
    g_pt_jni_scan_env = env;
    
    _ScanParamsClass                    = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/ScanParams");
    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    ScanParamsClass                     = SCAN_JNI_NEW_GLOBAL_REF(env, _ScanParamsClass);
    ScanParamsMethods[getmScanType]             = JNI_GET_CLASS_METHOD(env, ScanParamsClass, "getmScanType",             "()I");
    ScanParamsMethods[getmScanChannelFrom]      = JNI_GET_CLASS_METHOD(env, ScanParamsClass, "getmScanChannelFrom",      "()I");
    ScanParamsMethods[getmScanChannelTo]        = JNI_GET_CLASS_METHOD(env, ScanParamsClass, "getmScanChannelTo",        "()I");
    ScanParamsMethods[getmScanFreqFrom]         = JNI_GET_CLASS_METHOD(env, ScanParamsClass, "getmScanFreqFrom",         "()I");
    ScanParamsMethods[getmScanFreqTo]           = JNI_GET_CLASS_METHOD(env, ScanParamsClass, "getmScanFreqTo",           "()I");
    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));

    _ScanParaPalSecamClass              = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/ScanParaPalSecam");
    ScanParaPalSecamClass               = SCAN_JNI_NEW_GLOBAL_REF(env, _ScanParaPalSecamClass);
    ScanParaPalSecamMethods[getDesignatedCheckTvSystem]     = JNI_GET_CLASS_METHOD(env, ScanParaPalSecamClass, "getDesignatedCheckTvSystem",       "()I");
    ScanParaPalSecamMethods[getDesignatedCheckAudioSystem]  = JNI_GET_CLASS_METHOD(env, ScanParaPalSecamClass, "getDesignatedCheckAudioSystem",    "()I");
    ScanParaPalSecamMethods[getDesignatedCheckColorSystem]  = JNI_GET_CLASS_METHOD(env, ScanParaPalSecamClass, "getDesignatedCheckColorSystem",    "()I");
    ScanParaPalSecamMethods[getIsNegativeDirection]         = JNI_GET_CLASS_METHOD(env, ScanParaPalSecamClass, "getIsNegativeDirection",           "()I");
    ScanParaPalSecamMethods[getScanCfg]                     = JNI_GET_CLASS_METHOD(env, ScanParaPalSecamClass, "getScanCfg",                       "()I");
    ScanParaPalSecamMethods[getTvInput]                     = NULL;//JNI_GET_CLASS_METHOD(env, ScanParaPalSecamClass, "getTvInput",                       "com/mediatek/tv/service/BroadcastService");
    ScanParaPalSecamMethods[getTvBypass]                    = NULL;//JNI_GET_CLASS_METHOD(env, ScanParaPalSecamClass, "getTvBypass",                      "com/mediatek/tv/service/BroadcastService");
    ScanParaPalSecamMethods[getMonitorBypass]               = NULL;//JNI_GET_CLASS_METHOD(env, ScanParaPalSecamClass, "getMonitorBypass",                 "com/mediatek/tv/service/BroadcastService");
    ScanParaPalSecamMethods[getCustomCfgTvSystem]           = JNI_GET_CLASS_METHOD(env, ScanParaPalSecamClass, "getCustomCfgTvSystem",             "()I");
    ScanParaPalSecamMethods[getCustomCfgAudioSystem]        = JNI_GET_CLASS_METHOD(env, ScanParaPalSecamClass, "getCustomCfgAudioSystem",          "()I");
    ScanParaPalSecamMethods[getSvlId]                       = JNI_GET_CLASS_METHOD(env, ScanParaPalSecamClass, "getSvlId",                         "()I");
    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));

    _ScanServiceClass                   = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/ScanService");
    ScanServiceClass                    = SCAN_JNI_NEW_GLOBAL_REF(env, _ScanServiceClass);
    ScanServiceMethods[setScanProgress]                     = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanProgress",        "(II)I");
    ScanServiceMethods[setScanFrequence]                    = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanFrequence",       "(I)I");
    ScanServiceMethods[setScanCompleted]                    = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanCompleted",       "()I");
    ScanServiceMethods[setScanCanceled]                     = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanCanceled",        "()I");
    ScanServiceMethods[setScanError]                        = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanError",           "(I)I");
    ScanServiceMethods[onScanUserOperation]                 = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "onScanUserOperation",    "(III)I");
    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    
    _ScanExchangeFrenquenceRangeClass   = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/ScanExchangeFrenquenceRange");
    ScanExchangeFrenquenceRangeClass    = SCAN_JNI_NEW_GLOBAL_REF(env, _ScanExchangeFrenquenceRangeClass);
    ScanExchangeFrenquenceRangeMethods[setTunerLowerBound]  = JNI_GET_CLASS_METHOD(env, ScanExchangeFrenquenceRangeClass, "setTunerLowerBound", "(I)I");
    ScanExchangeFrenquenceRangeMethods[setTunerUpperBound]  = JNI_GET_CLASS_METHOD(env, ScanExchangeFrenquenceRangeClass, "setTunerUpperBound", "(I)I");
    
     
    JNI_LOGD(( "\n\n--------------------------------------------------------------------------------\n" ));
    JNI_LOGD(("[%-65s] {%s}\n", __func__, "init"));
   
    
    return 0;
}

/* scan ScanService for notification */
static VOID _SetScanProgressNfy ( INT32 progress, INT32 channels, VOID* pv_tag)
{
    //jclass      ScanService = ScanServiceClass;
    JNIEnv *    env = g_pt_jni_scan_env;
    jclass      jclass_TVCallBack = NULL;
    int         ret;
    jint        withoutAttachDetach   = 0;

    JNI_LOGD(("[%d]{%s}(progress: %d)(channels: %d)\n", __LINE__, __func__, progress, channels));

    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(env);
    if (!withoutAttachDetach)
    {
        ret = SCAN_JNI_ATTACH_CURRENT_THREAD(env);
        if (ret < 0)
        {
            JNI_LOGD((LOG_TAG"[ERROR][%d]{%s}\n", __LINE__, __func__));
        }
    }
    else
    {
        JNI_LOGD((LOG_TAG"[Warning][%d]{%s}We are already one with VM\n", __LINE__, __func__));
    }

    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));
    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
    JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_notifyScanProgress),progress,channels);
    //CLASS_STATIC_METHODV( ScanService, setScanProgress, progress, channels );
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));

    if (jclass_TVCallBack != NULL)
    {
        JNI_DEL_LOCAL_REF(env, jclass_TVCallBack);
    }
    
    if (!withoutAttachDetach)
    {
		SCAN_JNI_DETACH_CURRENT_THREAD(env);
    }
}

static VOID _SetScanFrequenceNfy ( INT32 frequence, VOID* pv_tag)
{
    //jclass      ScanService = ScanServiceClass;
    JNIEnv *    env = g_pt_jni_scan_env;
    jclass      jclass_TVCallBack = NULL; 
    int         ret;
    jint        withoutAttachDetach   = 0;

    JNI_LOGD(("[%d]{%s}(frequence: %d)\n", __LINE__, __func__, frequence));

    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(env);
    if (!withoutAttachDetach)
    {
        ret = SCAN_JNI_ATTACH_CURRENT_THREAD(env);
        if (ret < 0)
        {
            JNI_LOGD((LOG_TAG"[ERROR][%d]{%s}\n", __LINE__, __func__));
        }
    }
    else
    {
        JNI_LOGD((LOG_TAG"[Warning][%d]{%s}We are already one with VM\n", __LINE__, __func__));
    }
    
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));
    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
    JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_notifyScanFrequence),frequence);
    //CLASS_STATIC_METHODV( ScanService, setScanFrequence, frequence );
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));

    if (jclass_TVCallBack != NULL)
    {
		JNI_DEL_LOCAL_REF(env, jclass_TVCallBack);
    }
    
    if (!withoutAttachDetach)
    {
		SCAN_JNI_DETACH_CURRENT_THREAD(env);
    }
}

static VOID _SetScanCompletedNfy ( VOID* pv_tag )
{
    //jclass      ScanService = ScanServiceClass;
    JNIEnv *    env = g_pt_jni_scan_env;
    jclass      jclass_TVCallBack = NULL;
    int         ret;
    jint        withoutAttachDetach   = 0;

    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(env);
    if (!withoutAttachDetach)
    {
        ret = SCAN_JNI_ATTACH_CURRENT_THREAD(env);
        if (ret < 0)
        {
            JNI_LOGD((LOG_TAG"[ERROR][%d]{%s}\n", __LINE__, __func__));
        }
    }
    else
    {
        JNI_LOGD((LOG_TAG"[Warning][%d]{%s}We are already one with VM\n", __LINE__, __func__));
    }
    
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));
    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
    JNI_CALL_STATIC_METHOD(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_notifyScanCompleted));
    //CLASS_STATIC_METHOD ( ScanService, setScanCompleted );
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));

    if (jclass_TVCallBack != NULL)
    {
		JNI_DEL_LOCAL_REF(env, jclass_TVCallBack);
    }
    
    if (!withoutAttachDetach)
    {
		SCAN_JNI_DETACH_CURRENT_THREAD(env);
    }
}

static VOID _SetScanCanceledNfy ( VOID* pv_tag )
{
    //jclass      ScanService = ScanServiceClass;
    JNIEnv *    env = g_pt_jni_scan_env;
    jclass      jclass_TVCallBack = NULL;
    int         ret;
    jint        withoutAttachDetach   = 0;

    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(env);
    if (!withoutAttachDetach)
    {
        ret = SCAN_JNI_ATTACH_CURRENT_THREAD(env);
        if (ret < 0)
        {
            JNI_LOGD((LOG_TAG"[ERROR][%d]{%s}\n", __LINE__, __func__));
        }
    }
    else
    {
        JNI_LOGD((LOG_TAG"[Warning][%d]{%s}We are already one with VM\n", __LINE__, __func__));
    }
    
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));
    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
    JNI_CALL_STATIC_METHOD(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_notifyScanCanceled));
    //CLASS_STATIC_METHOD ( ScanService, setScanCanceled );
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));

    if (jclass_TVCallBack != NULL)
    {
		JNI_DEL_LOCAL_REF(env, jclass_TVCallBack);
    }
    
    if (!withoutAttachDetach)
    {
		SCAN_JNI_DETACH_CURRENT_THREAD(env);
    }
}

static VOID _SetScanErrorNfy ( INT32 errorCode, VOID* pv_tag )
{
    //jclass      ScanService = ScanServiceClass;
    JNIEnv *    env = g_pt_jni_scan_env;
    jclass      jclass_TVCallBack = NULL;
    int         ret;
    jint        withoutAttachDetach   = 0;

    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(env);
    if (!withoutAttachDetach)
    {
        ret = SCAN_JNI_ATTACH_CURRENT_THREAD(env);
        if (ret < 0)
        {
            JNI_LOGD((LOG_TAG"[ERROR][%d]{%s}\n", __LINE__, __func__));
        }
    }
    else
    {
        JNI_LOGD((LOG_TAG"[Warning][%d]{%s}We are already one with VM\n", __LINE__, __func__));
    }
    
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));
    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
    JNI_CALL_STATIC_METHOD(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_notifyScanError));
    //CLASS_STATIC_METHODV( ScanService, setScanError, errorCode );
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));

    if (jclass_TVCallBack != NULL)
    {
		JNI_DEL_LOCAL_REF(env, jclass_TVCallBack);
    }
    
    if (!withoutAttachDetach)
    {
		SCAN_JNI_DETACH_CURRENT_THREAD(env);
    }
}


static VOID _SetScanUserOperationNfy (INT32 i4_curr_freq, INT32 i4_found_ch_nums, INT32 i4_data, VOID* pv_tag )
{
    JNIEnv *    env = g_pt_jni_scan_env;
    jclass      jclass_TVCallBack = NULL;
    int         ret;
    jint        withoutAttachDetach = 0;

    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(env);
    if (!withoutAttachDetach)
    {
        ret = SCAN_JNI_ATTACH_CURRENT_THREAD(env);
        if (ret < 0)
        {
            JNI_LOGD((LOG_TAG"[ERROR][%d]{%s}\n", __LINE__, __func__));
        }
    }
    else
    {
        JNI_LOGD((LOG_TAG"[Warning][%d]{%s}We are already one with VM\n", __LINE__, __func__));
    }

    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));
    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/TVCallBack");
    JNI_LOGD(("[%d]{%s}[%d][%d][%d]\n", __LINE__, __func__,i4_curr_freq,i4_found_ch_nums,i4_data));
    JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Int,CMI(TVCallBack_def,TVCallBack_notifyScanUserOperation),i4_curr_freq,i4_found_ch_nums,i4_data);
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));

    if (jclass_TVCallBack != NULL)
    {
		JNI_DEL_LOCAL_REF(env, jclass_TVCallBack);
    }
    
    if (!withoutAttachDetach)
    {
		SCAN_JNI_DETACH_CURRENT_THREAD(env);
    }
}


#define COVENT_COLOR_SYS( input, sys, output )  if(JNI_SCAN_##sys == (input)){(output) = sys;}
static VOID _jniScanCoventColorSys(
        int             colorSys,
        INT32*          pi4_svl_bldr_color_sys)
{
    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    *pi4_svl_bldr_color_sys = 0;
    COVENT_COLOR_SYS( colorSys, COLOR_SYS_UNKNOWN   , *pi4_svl_bldr_color_sys ); /* = 0; */
    COVENT_COLOR_SYS( colorSys, COLOR_SYS_NTSC      , *pi4_svl_bldr_color_sys ); /* = 1; */
    COVENT_COLOR_SYS( colorSys, COLOR_SYS_PAL       , *pi4_svl_bldr_color_sys ); /* = 2; */
    COVENT_COLOR_SYS( colorSys, COLOR_SYS_SECAM     , *pi4_svl_bldr_color_sys ); /* = 3; */
    COVENT_COLOR_SYS( colorSys, COLOR_SYS_NTSC_443  , *pi4_svl_bldr_color_sys ); /* = 4; */
    COVENT_COLOR_SYS( colorSys, COLOR_SYS_PAL_M     , *pi4_svl_bldr_color_sys ); /* = 5; */
    COVENT_COLOR_SYS( colorSys, COLOR_SYS_PAL_N     , *pi4_svl_bldr_color_sys ); /* = 6; */
    COVENT_COLOR_SYS( colorSys, COLOR_SYS_PAL_60    , *pi4_svl_bldr_color_sys ); /* = 7; */
    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));
}


#define COVENT_AUDIO_SYS( input, sys, output )  if(JNI_SCAN_##sys & (input)){(sys >= 0) ? ((output) |= MAKE_BIT_MASK_32(sys)): (output = 0);}
static VOID _jniScanCoventAudioSys(
        int             audioSys,
        INT32*          pi4_svl_bldr_audio_sys)
{
    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    *pi4_svl_bldr_audio_sys = 0;
    COVENT_AUDIO_SYS( audioSys, AUDIO_SYS_AM,           *pi4_svl_bldr_audio_sys ); 
    COVENT_AUDIO_SYS( audioSys, AUDIO_SYS_FM_MONO,      *pi4_svl_bldr_audio_sys ); 
    COVENT_AUDIO_SYS( audioSys, AUDIO_SYS_FM_EIA_J,     *pi4_svl_bldr_audio_sys ); 
    COVENT_AUDIO_SYS( audioSys, AUDIO_SYS_FM_A2,        *pi4_svl_bldr_audio_sys ); 
    COVENT_AUDIO_SYS( audioSys, AUDIO_SYS_FM_A2_DK1,    *pi4_svl_bldr_audio_sys ); 
    COVENT_AUDIO_SYS( audioSys, AUDIO_SYS_FM_A2_DK2,    *pi4_svl_bldr_audio_sys ); 
    COVENT_AUDIO_SYS( audioSys, AUDIO_SYS_FM_RADIO,     *pi4_svl_bldr_audio_sys ); 
    COVENT_AUDIO_SYS( audioSys, AUDIO_SYS_NICAM,        *pi4_svl_bldr_audio_sys ); 
    COVENT_AUDIO_SYS( audioSys, AUDIO_SYS_BTSC,         *pi4_svl_bldr_audio_sys ); 
    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));
}

#define COVENT_TV_SYS( input, sys, output )  if(JNI_SCAN_##sys & (input)){(output) |= MAKE_BIT_MASK_32(sys);}
static VOID _jniScanCoventTvSys(
        int             tvSys,
        UINT32*         pui4_svl_bldr_tv_sys)
{
    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));
    *pui4_svl_bldr_tv_sys = 0;
    COVENT_TV_SYS( tvSys, TV_SYS_A,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_B,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_C,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_D,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_E,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_F,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_G,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_H,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_I,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_J,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_K,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_K_PRIME,   *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_L,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_L_PRIME,   *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_M,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_N,         *pui4_svl_bldr_tv_sys ); 
    COVENT_TV_SYS( tvSys, TV_SYS_AUTO,      *pui4_svl_bldr_tv_sys ); 
    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    startScan_native
 * Signature: (Ljava/lang/String;Lcom/mediatek/tv/model/ScanParams;Lcom/mediatek/tv/model/ScanListener;)Z
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_startScan_1native
(JNIEnv *env, jclass clazz, jstring scanMode, jobject ScanParams, jobject ScanServiceForNfy)
{
    
    const char*     str;
    char *          s;
    jclass          scanParamsclazz;
    jmethodID       scanParamsGetmScanType;
    int             scanType;
    int             scanChannelFrom;
    int             scanChannelTo;
    int             scanFreqFrom;
    int             scanFreqTo;
    
    int             svlId;
    int             designatedCheckTvSystem    ;
    int             designatedCheckAudioSystem ;
    int             designatedCheckColorSystem ;
    int             isnegativeDirection        ;
    int             scanCfg                    ;
    int             customCfgTvSystem          ;
    int             customCfgAudioSystem       ;
    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));

    str = JNI_GET_STRING_UTF_CHARS( env, scanMode );
    if (str == NULL) {
        return JNI_FALSE;
    }
    JNI_LOGD(("[%-65s] {scanMode: %s}\n", __func__, str));

    
    if ((s = strstr(str, "pal_secam")) != NULL)
    {
        ANALOG_SCAN_DATA_T      t_analog_data;
        ANALOG_SCAN_NFY_T       t_scan_nfy;

        JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));

        t_scan_nfy.setScanProgressNfy       = _SetScanProgressNfy;
        t_scan_nfy.setScanFrequenceNfy      = _SetScanFrequenceNfy;
        t_scan_nfy.setScanCompletedNfy      = _SetScanCompletedNfy;
        t_scan_nfy.setScanCanceledNfy       = _SetScanCanceledNfy; 
        t_scan_nfy.setScanErrorNfy          = _SetScanErrorNfy;
        t_scan_nfy.setScanUserOperationNfy  = _SetScanUserOperationNfy;

        memset( &t_analog_data, 0, sizeof(ANALOG_SCAN_DATA_T) );
        t_analog_data.ui2_temp_svl_id = 1;
        
        scanType        = (int) CLASS_METHOD( ScanParams, getmScanType );
        scanChannelFrom = (int) CLASS_METHOD( ScanParams, getmScanChannelFrom );
        scanChannelTo   = (int) CLASS_METHOD( ScanParams, getmScanChannelTo );
        scanFreqFrom    = (int) CLASS_METHOD( ScanParams, getmScanFreqFrom );
        scanFreqTo      = (int) CLASS_METHOD( ScanParams, getmScanFreqTo );

        JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));

        switch (scanType)
        {
            case 2:     /* SCAN_TYPE_CHANNEL = 2; */
                t_analog_data.e_scan_type = SB_PAL_SECAM_SCAN_TYPE_UPDATE_MODE;
                JNI_LOGD(("[%-65s] {scanType: %s}\n", __func__, "SB_PAL_SECAM_SCAN_TYPE_UPDATE_MODE"));
                break;
                
            case 3:     /* SCAN_TYPE_FREQUENCY = 3; */
                t_analog_data.e_scan_type = SB_PAL_SECAM_SCAN_TYPE_RANGE_MODE;
                JNI_LOGD(("[%-65s] {scanType: %s}\n", __func__, "SB_PAL_SECAM_SCAN_TYPE_RANGE_MODE"));
                break;
                
            case 1:     /* SCAN_TYPE_FULL = 1; */
            deafault: 
                t_analog_data.e_scan_type = SB_PAL_SECAM_SCAN_TYPE_FULL_MODE;
                JNI_LOGD(("[%-65s] {scanType: %s}\n", __func__, "SB_PAL_SECAM_SCAN_TYPE_FULL_MODE"));
                break;
        }

        t_analog_data.ui4_start_freq            = (UINT32)scanFreqFrom;
        t_analog_data.ui4_end_freq              = (UINT32)scanFreqTo;
        t_analog_data.ui2_start_ch_id_to_scan   = (UINT16)scanChannelFrom;
        t_analog_data.ui2_end_ch_id_to_scan     = (UINT16)scanChannelTo;
        JNI_LOGD(("[%d]{%s}[start_freq: %d][end_freq: %d]\n", __LINE__, __func__, t_analog_data.ui4_start_freq, t_analog_data.ui4_end_freq));

        if (JNI_IS_INSTANCE_OF( env, ScanParams, ScanParaPalSecamClass ))
        {
            jobject ScanParaPalSecam = ScanParams;

            svlId = CLASS_METHOD( ScanParaPalSecam, getSvlId );

            designatedCheckTvSystem    = CLASS_METHOD( ScanParaPalSecam, getDesignatedCheckTvSystem );
            designatedCheckAudioSystem = CLASS_METHOD( ScanParaPalSecam, getDesignatedCheckAudioSystem );
            designatedCheckColorSystem = CLASS_METHOD( ScanParaPalSecam, getDesignatedCheckColorSystem );
            isnegativeDirection        = CLASS_METHOD( ScanParaPalSecam, getIsNegativeDirection );
            scanCfg                    = CLASS_METHOD( ScanParaPalSecam, getScanCfg );
            customCfgTvSystem          = CLASS_METHOD( ScanParaPalSecam, getCustomCfgTvSystem );
            customCfgAudioSystem       = CLASS_METHOD( ScanParaPalSecam, getCustomCfgAudioSystem );

            //JNI_LOGD(("[%-65s] {designatedCheckTvSystem : %d}\n", __func__, designatedCheckTvSystem));
            //JNI_LOGD(("[%-65s] {designatedCheckColorSystem : %d}\n", __func__, designatedCheckColorSystem));
            //JNI_LOGD(("[%-65s] {scanCfg : %d}\n", __func__, scanCfg));

            t_analog_data.ui2_temp_svl_id = svlId;


            if (0 == isnegativeDirection)
            {
                t_analog_data.b_neg_dir = FALSE;
            }
            else
            {
                t_analog_data.b_neg_dir = TRUE;
            }

            if (scanCfg & PAL_SECAM_SCAN_CFG_PLAY_VIDEO_ONLY) /* = 1 << 0; */
            {
                t_analog_data.t_eng_cfg_flag |= SB_PAL_SECAM_CONFIG_PLAY_VIDEO_ONLY;
                JNI_LOGD(("[%-65s] {scanCfg : %s}\n", __func__, "PLAY_VIDEO_ONLY"));
            }
            if (scanCfg & PAL_SECAM_SCAN_CFG_RANGE_SCAN_NO_WRAP_AROUND) /* = 1 << 1; */
            {
                t_analog_data.t_eng_cfg_flag |= SB_PAL_SECAM_CONFIG_RANGE_SCAN_NO_WRAP_AROUND;
                JNI_LOGD(("[%-65s] {scanCfg : %s}\n", __func__, "RANGE_SCAN_NO_WRAP_AROUND"));
            }
            if (scanCfg & PAL_SECAM_SCAN_CFG_START_CH_NUM) /* = 1 << 2; */
            {
                t_analog_data.t_eng_cfg_flag |= SB_PAL_SECAM_CONFIG_START_CH_NUM;
                JNI_LOGD(("[%-65s] {scanCfg : %s}\n", __func__, "START_CH_NUM"));
            }
            if (scanCfg & PAL_SECAM_SCAN_CFG_SCAN_REPLACE_EXISTING_CHANNEL) /* = 1 << 3; */
            {
                t_analog_data.t_eng_cfg_flag |= SB_PAL_SECAM_CONFIG_SCAN_REPLACE_EXISTING_CHANNEL;
                JNI_LOGD(("[%-65s] {scanCfg : %s}\n", __func__, "SCAN_REPLACE_EXISTING_CHANNEL"));
            }
            if (scanCfg & PAL_SECAM_SCAN_CFG_SCAN_WITH_DESIGNATED_TV_SYS) /* = 1 << 4; */
            {
                t_analog_data.t_eng_cfg_flag |= SB_PAL_SECAM_CONFIG_SCAN_WITH_DESIGNATED_TV_SYS;
                _jniScanCoventTvSys(    designatedCheckTvSystem,       &(t_analog_data.ui4_designated_chk_tv_sys) );
                _jniScanCoventAudioSys( designatedCheckAudioSystem,    &t_analog_data.ui4_designated_chk_tv_audio_sys );

                JNI_LOGD(("[%-65s] {scanCfg : %s}: [TvSystem:%d][AudioSystem:%d]\n", __func__, "DESIGNATED_TV_SYS", designatedCheckTvSystem, designatedCheckAudioSystem));
            }
            if (scanCfg & PAL_SECAM_SCAN_CFG_SCAN_WITH_TV_BYPASS) /* = 1 << 5; */
            {
                //t_analog_data.t_eng_cfg_flag |= ;
            }
            if (scanCfg & PAL_SECAM_SCAN_CFG_SCAN_WITH_MONITOR_BYPASS) /* = 1 << 6; */
            {
                //t_analog_data.t_eng_cfg_flag |= ;
            }
            if (scanCfg & PAL_SECAM_SCAN_CFG_SCAN_WITHOUT_DSP_DETECT_TV_SYS) /* = 1 << 7; */
            {
                t_analog_data.t_eng_cfg_flag |= SB_PAL_SECAM_CONFIG_SCAN_WITHOUT_DSP_DETECT_TV_SYS;
                JNI_LOGD(("[%-65s] {scanCfg : %s}\n", __func__, "SCAN_WITHOUT_DSP_DETECT_TV_SYS"));
            }
            if (scanCfg & PAL_SECAM_SCAN_CFG_SCAN_ALL_CH_IN_RANGE_MODE) /* = 1 << 8; */
            {
                t_analog_data.t_eng_cfg_flag |= SB_PAL_SECAM_CONFIG_SCAN_ALL_CH_IN_RANGE_MODE;
                JNI_LOGD(("[%-65s] {scanCfg : %s}\n", __func__, "SCAN_ALL_CH_IN_RANGE_MODE"));
            }
            if (scanCfg & PAL_SECAM_SCAN_CFG_SCAN_WITH_DESIGNATED_COLOR_SYS) /* = 1 << 9; */
            {
                t_analog_data.t_eng_cfg_flag |= SB_PAL_SECAM_CONFIG_SCAN_WITH_DESIGNATED_COLOR_SYS;
                _jniScanCoventColorSys( designatedCheckColorSystem,    &t_analog_data.i4_designated_chk_color_sys );

                if (COLOR_SYS_PAL == t_analog_data.i4_designated_chk_color_sys)
                {
                    JNI_LOGD(("[%-65s] {scanCfg : %s %s}\n", __func__, "SCAN_WITH_DESIGNATED_COLOR_SYS", "COLOR_SYS_PAL"));
                }
                if (COLOR_SYS_NTSC == t_analog_data.i4_designated_chk_color_sys)
                {
                    JNI_LOGD(("[%-65s] {scanCfg : %s %s}\n", __func__, "SCAN_WITH_DESIGNATED_COLOR_SYS", "COLOR_SYS_NTSC"));
                }
            }
            if (scanCfg & PAL_SECAM_SCAN_CFG_CUSTOM_SVL_CONFIG) /* = 1 << 10; */
            {
                t_analog_data.t_eng_cfg_flag |= SB_PAL_SECAM_CONFIG_CUSTOM_SVL_CONFIG;

                _jniScanCoventTvSys(    customCfgTvSystem,     &t_analog_data.t_custom_svl_config.ui4_tv_sys_mask );
                _jniScanCoventAudioSys( customCfgAudioSystem,  &t_analog_data.t_custom_svl_config.ui4_aud_sys_mask );
                JNI_LOGD(("[%-65s] {scanCfg : %s}", __func__, "CUSTOM_SVL_CONFIG"));

                if (MAKE_BIT_MASK_32 (TV_SYS_D) & t_analog_data.t_custom_svl_config.ui4_tv_sys_mask)
                {
                    JNI_LOGD(("{TV_SYS : %s}", "TV_SYS_D"));
                }

                if (MAKE_BIT_MASK_32 (TV_SYS_K) & t_analog_data.t_custom_svl_config.ui4_tv_sys_mask)
                {
                    JNI_LOGD(("{TV_SYS : %s}", "TV_SYS_K"));
                }

                if (MAKE_BIT_MASK_32 (TV_SYS_B) & t_analog_data.t_custom_svl_config.ui4_tv_sys_mask)
                {
                    JNI_LOGD(("{TV_SYS : %s}", "TV_SYS_B"));
                }

                if (MAKE_BIT_MASK_32 (TV_SYS_G) & t_analog_data.t_custom_svl_config.ui4_tv_sys_mask)
                {
                    JNI_LOGD(("{TV_SYS : %s}", "TV_SYS_G"));
                }


                if (MAKE_BIT_MASK_16 (AUDIO_SYS_FM_A2_DK1) & t_analog_data.t_custom_svl_config.ui4_aud_sys_mask)
                {
                    JNI_LOGD(("{AUDIO_SYS : %s}", "AUDIO_SYS_FM_A2_DK1"));
                }

                if (MAKE_BIT_MASK_16 (AUDIO_SYS_FM_A2_DK2) & t_analog_data.t_custom_svl_config.ui4_aud_sys_mask)
                {
                    JNI_LOGD(("{AUDIO_SYS : %s}", "AUDIO_SYS_FM_A2_DK2"));
                }
                
                JNI_LOGD(("\n"));
            }
            if (scanCfg & PAL_SECAM_SCAN_CFG_UPDATE_TO_TEMP_SVL) /* = 1 << 11; */
            {
                t_analog_data.t_eng_cfg_flag |= SB_PAL_SECAM_CONFIG_UPDATE_TO_TEMP_SVL;                
                JNI_LOGD(("[%-65s] {scanCfg : %s}{svlId : %d}\n", __func__, "SB_PAL_SECAM_CONFIG_CUSTOM_SVL_CONFIG", svlId));
            }
            if (scanCfg & PAL_SECAM_SCAN_CFG_USER_OPERATION) /* = 1 << 12; */
            {
                t_analog_data.t_eng_cfg_flag |= SB_PAL_SECAM_CONFIG_USER_OPERATION;
                JNI_LOGD(("[%-65s] {scanCfg : %s}\n", __func__, "USER_OPERATION"));
            }

            if (scanCfg & PAL_SECAM_SCAN_CFG_ACTUAL_COLOR_SYS) /* = 1 << 13; */
            {
                t_analog_data.t_eng_cfg_flag |= SB_PAL_SECAM_CONFIG_ACTUAL_COLOR_SYS;
                JNI_LOGD(("[%-65s] {scanCfg : %s}\n", __func__, "ACTUAL_COLOR_SYS"));
            }
        }

        JNI_LOGD(("[%d]{%s}(scan type:%d){temp_svl_id : %d}\n", __LINE__, __func__, t_analog_data.e_scan_type, t_analog_data.ui2_temp_svl_id));

        a_analogScanStart( &t_analog_data, &t_scan_nfy, NULL);
        JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));

        //JNI_LOGD(("[%-65s] {scanType: %d}\n", __func__, scanType));
    }

    JNI_RELEASE_STRING_UTF_CHARS(env, scanMode, str);    

    if (ScanParams != NULL)
    {
		JNI_DEL_LOCAL_REF(env, ScanParams);
    }
    
    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));

    return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    cancelScan_native
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_cancelScan_1native
(JNIEnv *env, jclass clazz, jstring scanMode)
{
    const char*     str;
    char *          s;

    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));

    str = JNI_GET_STRING_UTF_CHARS( env, scanMode );
    if (str == NULL) {
        return JNI_FALSE;
    }
    
    if ((s = strstr(str, "pal_secam")) != NULL)
    {
        JNI_LOGD(("[%-65s] {scanMode: %s}\n", __func__, str));
        a_analogScanCancel();
    }
    
    JNI_RELEASE_STRING_UTF_CHARS(env, scanMode, str);

    return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getScanData_native
 * Signature: (Ljava/lang/String;ILcom/mediatek/tv/model/ScanExchange;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getScanData_1native
(JNIEnv *env, jclass clazz, jstring scanMode, jint type, jobject scanExchangeData)
{
    jint            ret = 10;
    
    int             getType = (int)type;    
    const char*     str;
    char *          s;
    int             lowerBound = 0;
    int             upperBound = 0;

    JNI_LOGD(("[%d]{%s}\n", __LINE__, __func__));

    str = JNI_GET_STRING_UTF_CHARS( env, scanMode );
    if (str == NULL) {
        return JNI_FALSE;
    }

    if ((s = strstr(str, "pal_secam")) != NULL)
    {
        if (1 == type)   /* java code: SCAN_GET_TYPE_TUNER_FREQUENCE_RANGE = 1; */
        {
            if (JNI_IS_INSTANCE_OF( env, scanExchangeData, ScanExchangeFrenquenceRangeClass ))
            {
                jobject         ScanExchangeFrenquenceRange = scanExchangeData;
                SB_FREQ_RANGE_T t_freq_rang = {0};

                JNI_LOGD(("[%-65s] {scanMode: %s}(%d)\n", __func__, str, type));

                a_analogScanGet( SB_KEY_TYPE_TUNER_FREQ_RANGE, &t_freq_rang );

                lowerBound = t_freq_rang.ui4_lower_freq;
                upperBound = t_freq_rang.ui4_upper_freq;

                CLASS_METHODV( ScanExchangeFrenquenceRange, setTunerLowerBound, lowerBound );
                CLASS_METHODV( ScanExchangeFrenquenceRange, setTunerUpperBound, upperBound );
            }
        }
    }
    
    JNI_RELEASE_STRING_UTF_CHARS(env, scanMode, str);
    
	if (scanExchangeData != NULL)
    {
		JNI_DEL_LOCAL_REF(env, scanExchangeData);
    }

    return ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    scanExchangeData
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_scanExchangeData
  (JNIEnv *env, jclass clazz, jintArray ScanData)
{
    INT32                   i4_ret = 0;
    INT32                   i4_len;
    INT32                   ai4_scan_grp[SCAN_MAX_EXCHANGE_DATA_SIZE];

    i4_len = (INT32)JNI_GET_ARRAY_LEN(env, ScanData);
    if (SCAN_MAX_EXCHANGE_DATA_SIZE < i4_len)
    {
        JNI_LOGD(("[ERROR][%d]{%s}(jni GET type length is not enough)\n", __LINE__, __func__));
        return ANALOG_SCAN_FAIL;
    }

    if (0 >= i4_len)
    {
        JNI_LOGD(("[ERROR][%d]{%s}(jni size of GET type array should be bigger than ZERO)\n", __LINE__, __func__));
        return ANALOG_SCAN_FAIL;
    }

    //JNI_LOGD(("[INFO ][%d]{%s} enter\n", __LINE__, __func__));

    SCAN_JNI_GET_INT_ARRAY_REGION(env, ScanData, i4_len, (jint *)ai4_scan_grp);
    
    {
        //JNI_LOGD(("[INFO ][%d]{%s}[type:%d]\n", __LINE__, __func__, (int)ai4_scan_grp[ScanService_SCAN_EXCHANGE_HEADER_TYPE_IDX]));

        a_scan_exchange_data(ai4_scan_grp, (UINT32)i4_len);

        if (0)
        {
            UINT16  ui2_data_idx = 0;

            JNI_LOGD(("[INFO_exchange]\n"));
            for (ui2_data_idx = 0; ui2_data_idx < i4_len; ui2_data_idx++)
            {
                JNI_LOGD(("[INFO_exchange][%d]\n", (int)ai4_scan_grp[ui2_data_idx]));
            }
            JNI_LOGD(("[INFO_exchange]\n"));
        }
    }

    //JNI_LOGD(("[INFO ][%d]{%s} leave\n", __LINE__, __func__));

    SCAN_JNI_SET_INT_ARRAY_REGION(env, ScanData, i4_len, (jint *)ai4_scan_grp);

    return (jint)i4_ret;
}



EXTERN_C_END

