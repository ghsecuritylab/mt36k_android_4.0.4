#include "tv_jni_util.h"
#include "com_mediatek_tv_service_TVNative.h"
#include "scan/dtmb_scan_service_client.h"

EXTERN_C_START

#define LOG_TAG "[scan_service_dtmb]"
;
typedef enum
{
    getScanCountryCode = 0,
    getScanType,
    getScanConfig,
    getScanStartIndex,
    getScanEndIndex,
    getScanFrequency,
    getScanSvlId,
    scanParaDTMBMethodsNum
}   _scanParaDTMBMethods;
jmethodID   ScanParaDtmbClassMethods[scanParaDTMBMethodsNum];
jclass      ScanParaDtmbClass;

typedef enum 
{
    setScanProgress = 0,
    setScanFrequence,
    setScanCompleted,
    setScanCanceled,
    setScanError,
    scanServiceDTMBMethodsNum
}   _scanServiceDTMBMethods;
jmethodID   ScanServiceDtmbClassMethods[scanServiceDTMBMethodsNum];
jclass      ScanServiceClass;

#define SCAN_JNI_GET_CLASS_STATIC_METHOD(ENV,CLASS,NAME,SIG)        ((*ENV)->GetStaticMethodID(ENV,CLASS,NAME,SIG))
#define SCAN_JNI_NEW_GLOBAL_REF(ENV,CLASS)                          ((*ENV)->NewGlobalRef(ENV,CLASS))
#define SCAN_JNI_GET_JAVAVM(ENV)                                    ((*ENV)->GetJavaVM(ENV, &g_pt_jni_scan_JavaVM))
#define SCAN_JNI_ATTACH_CURRENT_THREAD(ENV)                         ((*g_pt_jni_scan_JavaVM)->AttachCurrentThread(g_pt_jni_scan_JavaVM, &ENV, NULL))
#define SCAN_JNI_DETACH_CURRENT_THREAD(ENV)                         ((*g_pt_jni_scan_JavaVM)->DetachCurrentThread(g_pt_jni_scan_JavaVM))

#define CALL_CLASS_INT_METHOD(  className, method )                  (*env)->CallIntMethod(env, className, className##Methods[method])
#define CALL_CLASS_INT_METHODV( className, method, args...)          (*env)->CallIntMethod(env, className, className##Methods[method], args)
#define CALL_CLASS_INT_STATIC_METHOD(  className, method )           (*env)->CallStaticIntMethod(env, className, className##Methods[method])
#define CALL_CLASS_INT_STATIC_METHODV( className, method, args...)   (*env)->CallStaticIntMethod(env, className, className##Methods[method], args)

static JNIEnv *g_pt_jni_scan_env;
static JavaVM *g_pt_jni_scan_JavaVM;

void x_scan_service_dtmb_init_jni (JNIEnv *env)
{
    jclass      _ScanParaDtmbClass;
    jclass      _ScanServiceClass;
    JNI_LOGD((LOG_TAG"[%s] init\n", __func__, "init"));

    if (SCAN_JNI_GET_JAVAVM(env) < 0)
    {
        JNI_LOGD((LOG_TAG"[ERROR][%s] get JVM environment fail\n", __func__));
        return;
    }
    
    g_pt_jni_scan_env = env;

    _ScanParaDtmbClass                              = JNI_GET_CLASS_BY_NAME(env, "com/mediatek/tv/model/ScanParaDtmb");
    ScanParaDtmbClass                               = SCAN_JNI_NEW_GLOBAL_REF(env, _ScanParaDtmbClass);
    ScanParaDtmbClassMethods[getScanCountryCode]    = JNI_GET_CLASS_METHOD(env, ScanParaDtmbClass, "getScanCountryCode",       "()Ljava/lang/String;");
    ScanParaDtmbClassMethods[getScanType]           = JNI_GET_CLASS_METHOD(env, ScanParaDtmbClass, "getScanType",              "()I");
	ScanParaDtmbClassMethods[getScanConfig]         = JNI_GET_CLASS_METHOD(env, ScanParaDtmbClass, "getScanConfig",            "()I");
    ScanParaDtmbClassMethods[getScanStartIndex]     = JNI_GET_CLASS_METHOD(env, ScanParaDtmbClass, "getScanStartIndex",        "()I");
    ScanParaDtmbClassMethods[getScanEndIndex]       = JNI_GET_CLASS_METHOD(env, ScanParaDtmbClass, "getScanEndIndex",          "()I");
    ScanParaDtmbClassMethods[getScanFrequency]      = JNI_GET_CLASS_METHOD(env, ScanParaDtmbClass, "getScanFrequency",         "()I");
    ScanParaDtmbClassMethods[getScanSvlId]          = JNI_GET_CLASS_METHOD(env, ScanParaDtmbClass, "getScanSvlId",             "()I");
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));
	
    _ScanServiceClass                               = JNI_GET_CLASS_BY_NAME(env, "com/mediatek/tv/service/ScanService");
    ScanServiceClass                                = SCAN_JNI_NEW_GLOBAL_REF(env, _ScanServiceClass);
    ScanServiceDtmbClassMethods[setScanProgress]    = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanProgress",        "(II)I");
    ScanServiceDtmbClassMethods[setScanFrequence]   = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanFrequence",       "(I)I");
    ScanServiceDtmbClassMethods[setScanCompleted]   = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanCompleted",       "()I");
    ScanServiceDtmbClassMethods[setScanCanceled]    = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanCanceled",        "()I");
    ScanServiceDtmbClassMethods[setScanError]       = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanError",           "(I)I");
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));

    return 0;
}

/* scan ScanService for notification */
static VOID _SetScanProgressNfy ( INT32 progress, INT32 channels)
{
    //jclass      ScanService = ScanServiceClass;
    JNIEnv *    env = g_pt_jni_scan_env;
    jclass      jclass_TVCallBack = NULL;
    int         ret;
    jint		withoutAttachDetach = 0;

    JNI_LOGD((LOG_TAG"[%d]{%s}(progress: %d)(channels: %d)\n", __LINE__, __func__, progress, channels));

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
    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env, "com/mediatek/tv/service/TVCallBack");
    JNI_CALL_STATIC_METHODV(env, jclass_TVCallBack, Int, CMI(TVCallBack_def,TVCallBack_notifyScanProgress), progress, channels);
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

static VOID _SetScanFrequenceNfy ( INT32 frequence)
{
    JNIEnv *    env = g_pt_jni_scan_env;
    jclass      jclass_TVCallBack = NULL; 
    int         ret;
    jint		withoutAttachDetach = 0;

    JNI_LOGD((LOG_TAG"[%d]{%s}(frequence: %d)\n", __LINE__, __func__, frequence));

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
    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env, "com/mediatek/tv/service/TVCallBack");
    JNI_CALL_STATIC_METHODV(env, jclass_TVCallBack, Int, CMI(TVCallBack_def,TVCallBack_notifyScanFrequence), frequence);
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

static VOID _SetScanCompletedNfy ( )
{
    JNIEnv *    env = g_pt_jni_scan_env;
    jclass      jclass_TVCallBack = NULL;
    int         ret;
    jint		withoutAttachDetach = 0;

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
    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env, "com/mediatek/tv/service/TVCallBack");
    JNI_CALL_STATIC_METHOD(env, jclass_TVCallBack, Int, CMI(TVCallBack_def,TVCallBack_notifyScanCompleted));
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

static VOID _SetScanCanceledNfy ( )
{
    JNIEnv *    env = g_pt_jni_scan_env;
    jclass      jclass_TVCallBack = NULL;
    int         ret;
    jint		withoutAttachDetach = 0;

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
    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env, "com/mediatek/tv/service/TVCallBack");
    JNI_CALL_STATIC_METHOD(env, jclass_TVCallBack, Int, CMI(TVCallBack_def,TVCallBack_notifyScanCanceled));
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

static VOID _SetScanErrorNfy ( INT32 errorCode )
{
    JNIEnv *    env = g_pt_jni_scan_env;
    jclass      jclass_TVCallBack = NULL;
    int         ret;
    jint		withoutAttachDetach = 0;


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
    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(env, "com/mediatek/tv/service/TVCallBack");
    JNI_CALL_STATIC_METHOD(env, jclass_TVCallBack, Int, CMI(TVCallBack_def,TVCallBack_notifyScanError));
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


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    startScanDtmb_native
 * Signature: (Ljava/lang/String;Lcom/mediatek/tv/model/ScanParams;Lcom/mediatek/tv/model/ScanListener;)Z
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_startScanDtmb_1native
(JNIEnv *env, jclass clazz, jstring scanMode, jobject ScanParams )
{   
	char*           scanCountryCode = "CHN";
    int             scanType;	
    int             scanConfig;
    int             scanStartIndex;
    int             scanEndIndex;
    int             scanFrequency;
    int             scanSvlId;

    char*           strScanMode = NULL;

    SB_DTMB_SCAN_DATA_T   t_dtmb_data;
    DTMB_SCAN_NFY_T       t_dtmb_scan_nfy;

    INT32           i4_ret = 0;
	
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));

    strScanMode = JNI_GET_STRING_UTF_CHARS( env, scanMode );
    if (strScanMode == NULL) 
    {
        return JNI_FALSE;
    }
    
	JNI_LOGD((LOG_TAG"[%d]{%s} scan mode: %s\n", __LINE__, __func__, strScanMode));

    t_dtmb_scan_nfy.setScanProgressNfy   = _SetScanProgressNfy;
    t_dtmb_scan_nfy.setScanFrequenceNfy  = _SetScanFrequenceNfy;
    t_dtmb_scan_nfy.setScanCompletedNfy  = _SetScanCompletedNfy;
    t_dtmb_scan_nfy.setScanCanceledNfy   = _SetScanCanceledNfy; 
    t_dtmb_scan_nfy.setScanErrorNfy      = _SetScanErrorNfy;

    memset( &t_dtmb_data, 0, sizeof(t_dtmb_data) );

    if (JNI_IS_INSTANCE_OF( env, ScanParams, ScanParaDtmbClass ))
    { 
        jobject ScanParaDtmbClass   = ScanParams;
        scanType                    = CALL_CLASS_INT_METHOD( ScanParaDtmbClass, getScanType );
        scanConfig                  = CALL_CLASS_INT_METHOD( ScanParaDtmbClass, getScanConfig );
        scanStartIndex              = CALL_CLASS_INT_METHOD( ScanParaDtmbClass, getScanStartIndex );
        scanEndIndex                = CALL_CLASS_INT_METHOD( ScanParaDtmbClass, getScanEndIndex );
        scanFrequency               = CALL_CLASS_INT_METHOD( ScanParaDtmbClass, getScanFrequency );       
        scanSvlId                   = CALL_CLASS_INT_METHOD( ScanParaDtmbClass, getScanSvlId );
		JNI_LOGD((LOG_TAG"[%d]{%s}scanCountryCode: %s, scanType: %d, scanConfig: %x, scanStartIndex: %d, scanEndIndex: %d, scanFrequency: %d, scanSvlId: %d\n", 
                  __LINE__ , __func__, scanCountryCode, scanType, scanConfig, scanStartIndex, scanEndIndex, scanFrequency, scanSvlId));

        memset(&t_dtmb_data, 0, sizeof(t_dtmb_data));
        strncpy(t_dtmb_data.t_country_code, scanCountryCode, ISO_3166_COUNT_LEN);
        t_dtmb_data.t_eng_cfg_flag = scanConfig;
        
		switch(scanType)
		{
        case 1:
            {
                t_dtmb_data.e_scan_type = SB_DTMB_SCAN_TYPE_FULL_MODE;
            }
            break;
        case 3:
            {
                t_dtmb_data.e_scan_type = SB_DTMB_SCAN_TYPE_ADD_ON_MODE;
            }
            break;
        case 4:
            {
                t_dtmb_data.e_scan_type = SB_DTMB_SCAN_TYPE_SINGLE_RF_CHANNEL;
                t_dtmb_data.t_scan_info.t_single.ui2_scan_idx = scanStartIndex;
            }
            break;
        case 5:
            {
                t_dtmb_data.e_scan_type = SB_DTMB_SCAN_TYPE_RANGE_RF_CHANNEL;
                t_dtmb_data.t_scan_info.t_range.ui2_start_scan_idx = scanStartIndex;
                t_dtmb_data.t_scan_info.t_range.ui2_end_scan_idx = scanEndIndex;
            }
            break;
        case 6:
            {
                t_dtmb_data.e_scan_type = SB_DTMB_SCAN_TYPE_MANUAL_FREQ;
                t_dtmb_data.t_scan_info.t_freq.ui4_freq = scanFrequency;
            }
            break;
        default:
            JNI_LOGE((LOG_TAG"[%d]{%s} scan type[%d] is not support\n", __LINE__, __FUNCTION__, scanType));
            if (ScanParams != NULL)
            {
                JNI_DEL_LOCAL_REF(env, ScanParams);
            }
            JNI_RELEASE_STRING_UTF_CHARS(env, scanMode, strScanMode);    
            return -1;
		}
        i4_ret = a_dtmbScanStart( &t_dtmb_data, &t_dtmb_scan_nfy, NULL);
        JNI_LOGD((LOG_TAG"[%d]{%s}, return %d\n", __LINE__, __func__, i4_ret));

    }

    JNI_RELEASE_STRING_UTF_CHARS(env, scanMode, strScanMode);    
    if (ScanParams != NULL)
    {
        JNI_DEL_LOCAL_REF(env, ScanParams);
    }
    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));

    return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    cancelScanDtmb_native
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_cancelScanDtmb_1native
(JNIEnv *env, jclass clazz, jstring scanMode)
{
    const char*     strScanMode = NULL;

    JNI_LOGD((LOG_TAG"[%d]{%s}\n", __LINE__, __func__));

    strScanMode = JNI_GET_STRING_UTF_CHARS( env, scanMode );
    if (strScanMode == NULL) 
    {
        return JNI_FALSE;
    }
    JNI_LOGD((LOG_TAG"[%s] {scanMode: %s}\n", __func__, strScanMode));
    
    if (strcmp(strScanMode, "dtmb-air") == 0)
    {
        a_dtmbScanCancel();
    }
    
    JNI_RELEASE_STRING_UTF_CHARS(env, scanMode, strScanMode);

    return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getDtmbFreqRange_native
 * Signature: (Ljava/lang/String;Lcom/mediatek/tv/model/DtmbFreqRange;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getDtmbFreqRange_1native
(JNIEnv *env, jclass clazz, jstring scanMode, jobject dtmbFreqRange)
{
    JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getDtmbFreqRange_1native line = %d",__LINE__));
	INT32 i4_ret = 0;
	SB_FREQ_RANGE_T t_scan_range;
	memset( &t_scan_range, 0, sizeof(SB_FREQ_RANGE_T) );
	
	i4_ret = a_dtmbGetScanRange( &t_scan_range );
	
    if (i4_ret < 0)
    {
        JNI_LOGD(("get scan range failed  line = %d \n",__LINE__));

        return -1;
    }

    JNI_CALL_OBJECT_METHODV(env, dtmbFreqRange, Void, CLASS_METHOD_ID(dtmb_freq_range_def, setLowerTunerFreqBound), 
		                    (jint)t_scan_range.ui4_lower_freq);
	JNI_CALL_OBJECT_METHODV(env, dtmbFreqRange, Void, CLASS_METHOD_ID(dtmb_freq_range_def, setUpperTunerFreqBound), 
		                    (jint)t_scan_range.ui4_upper_freq);
	
    JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_getDvbcFreqRange_1native line = %d",__LINE__));
	
    if (dtmbFreqRange != NULL)
    {
        JNI_DEL_LOCAL_REF(env, dtmbFreqRange);
    }
	return 0;
}
  

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getFirstDtmbScanRF_native
 * Signature: (Ljava/lang/String;Lcom/mediatek/tv/model/DtmbScanRF;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getFirstDtmbScanRF_1native
(JNIEnv *env, jclass clazz, jstring scanMode, jobject firstRF)
{
    JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getFirstDtmbScanRF_1native line = %d",__LINE__));
	INT32 i4_ret = 0;
	DTMB_SCAN_RF t_scanRF;
	memset( &t_scanRF, 0, sizeof(t_scanRF) );
	
	i4_ret = a_dtmbGetFirstScanRF( &t_scanRF );
	
    if (i4_ret < 0)
    {
        JNI_LOGD(("get first scan RF failed  line = %d \n",__LINE__));

        return -1;
    }
    jstring rfChannel = JNI_NEW_STRING_UTF(env, t_scanRF.rfChannel);
    JNI_CALL_OBJECT_METHODV(env, firstRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFChannel), rfChannel);
    JNI_CALL_OBJECT_METHODV(env, firstRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFScanIndex), (jint)t_scanRF.rfScanIdx);
	JNI_CALL_OBJECT_METHODV(env, firstRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFScanFrequency), (jint)t_scanRF.rfScanFreq);
	
    JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_getFirstDtmbScanRF_1native line = %d",__LINE__));
    if (rfChannel != NULL)
    {
        JNI_DEL_LOCAL_REF(env, rfChannel);
    }
    if (firstRF != NULL)
    {
        JNI_DEL_LOCAL_REF(env, firstRF);
    }
	return 0;
}
  

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getLastDtmbScanRF_native
 * Signature: (Ljava/lang/String;Lcom/mediatek/tv/model/DtmbScanRF;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getLastDtmbScanRF_1native
(JNIEnv *env, jclass clazz, jstring scanMode, jobject lastRF)
{
    JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getLastDtmbScanRF_1native line = %d",__LINE__));
	INT32 i4_ret = 0;
	DTMB_SCAN_RF t_scanRF;
	memset( &t_scanRF, 0, sizeof(t_scanRF) );
	
	i4_ret = a_dtmbGetLastScanRF( &t_scanRF );
	
    if (i4_ret < 0)
    {
        JNI_LOGD(("get first scan RF failed  line = %d \n",__LINE__));

        return -1;
    }
    jstring rfChannel = JNI_NEW_STRING_UTF(env, t_scanRF.rfChannel);
    JNI_CALL_OBJECT_METHODV(env, lastRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFChannel), rfChannel);
    JNI_CALL_OBJECT_METHODV(env, lastRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFScanIndex), (jint)t_scanRF.rfScanIdx);
	JNI_CALL_OBJECT_METHODV(env, lastRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFScanFrequency), (jint)t_scanRF.rfScanFreq);
	
    JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_getLastDtmbScanRF_1native line = %d",__LINE__));
	
    if (lastRF != NULL)
    {
        JNI_DEL_LOCAL_REF(env, lastRF);
    }
    if (rfChannel != NULL)
    {
        JNI_DEL_LOCAL_REF(env, rfChannel);
    }
	return 0;
}
  
  /*
   * Class:     com_mediatek_tv_service_TVNative
   * Method:    getNextDtmbScanRF_native
   * Signature: (Ljava/lang/String;Lcom/mediatek/tv/model/DtmbScanRF;Lcom/mediatek/tv/model/DtmbScanRF;)I
   */
  JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getNextDtmbScanRF_1native
  (JNIEnv *env, jclass clazz, jstring scanMode, jobject currRF, jobject nextRF)
  {
      JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getNextDtmbScanRF_1native line = %d",__LINE__));
      INT32 i4_ret = 0;
      DTMB_SCAN_RF t_currRF, t_nextRF;
      memset( &t_currRF, 0, sizeof(t_currRF) );
      memset( &t_nextRF, 0, sizeof(t_nextRF) );

      jstring curRFChannel =  JNI_CALL_OBJECT_METHOD(env, currRF, Object, CLASS_METHOD_ID(dtmb_scanRF_def,getRFChannel));
      char* sCurRFChannel = JNI_GET_STRING_UTF_CHARS( env, curRFChannel );
      strncpy(t_currRF.rfChannel, sCurRFChannel, RF_CHANNEL_LEN-1);
      JNI_RELEASE_STRING_UTF_CHARS(env, curRFChannel, sCurRFChannel);
      
      t_currRF.rfScanIdx = JNI_CALL_OBJECT_METHOD(env, currRF, Int, CLASS_METHOD_ID(dtmb_scanRF_def,getRFScanIndex));
	  t_currRF.rfScanFreq = JNI_CALL_OBJECT_METHOD(env, currRF, Int, CLASS_METHOD_ID(dtmb_scanRF_def,getRFScanFrequency));
	
      i4_ret = a_dtmbGetNextScanRF( &t_currRF, &t_nextRF );
      
      if (i4_ret < 0)
      {
          JNI_LOGD(("get next scan RF failed  line = %d \n",__LINE__));
          if (currRF != NULL)
          {
              JNI_DEL_LOCAL_REF(env, currRF);
          }
          return -1;
      }
      jstring rfChannel = JNI_NEW_STRING_UTF(env, t_nextRF.rfChannel);
      JNI_CALL_OBJECT_METHODV(env, nextRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFChannel), rfChannel);
      JNI_CALL_OBJECT_METHODV(env, nextRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFScanIndex), (jint)t_nextRF.rfScanIdx);
      JNI_CALL_OBJECT_METHODV(env, nextRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFScanFrequency), (jint)t_nextRF.rfScanFreq);
      
      JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_getNextDtmbScanRF_1native line = %d",__LINE__));
      
      if (currRF != NULL)
      {
          JNI_DEL_LOCAL_REF(env, currRF);
      }
      if (nextRF != NULL)
      {
          JNI_DEL_LOCAL_REF(env, nextRF);
      }
      if (rfChannel != NULL)
      {
          JNI_DEL_LOCAL_REF(env, rfChannel);
      }
      return 0;
  }


  /*
   * Class:     com_mediatek_tv_service_TVNative
   * Method:    getPrevDtmbScanRF_native
   * Signature: (Ljava/lang/String;Lcom/mediatek/tv/model/DtmbScanRF;Lcom/mediatek/tv/model/DtmbScanRF;)I
   */
  JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getPrevDtmbScanRF_1native
  (JNIEnv *env, jclass clazz, jstring scanMode, jobject currRF, jobject prevRF)
  {
      JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getNextDtmbScanRF_1native line = %d",__LINE__));
      INT32 i4_ret = 0;
      DTMB_SCAN_RF t_currRF, t_prevRF;
      memset( &t_currRF, 0, sizeof(t_currRF) );
      memset( &t_prevRF, 0, sizeof(t_prevRF) );

      jstring curRFChannel =  JNI_CALL_OBJECT_METHOD(env, currRF, Object, CLASS_METHOD_ID(dtmb_scanRF_def,getRFChannel));
      char* sCurRFChannel = JNI_GET_STRING_UTF_CHARS( env, curRFChannel );
      strncpy(t_currRF.rfChannel, sCurRFChannel, RF_CHANNEL_LEN-1);
      JNI_RELEASE_STRING_UTF_CHARS(env, curRFChannel, sCurRFChannel);
      
      t_currRF.rfScanIdx = JNI_CALL_OBJECT_METHOD(env, currRF, Int, CLASS_METHOD_ID(dtmb_scanRF_def,getRFScanIndex));
	  t_currRF.rfScanFreq = JNI_CALL_OBJECT_METHOD(env, currRF, Int, CLASS_METHOD_ID(dtmb_scanRF_def,getRFScanFrequency));
	
      i4_ret = a_dtmbGetPrevScanRF( &t_currRF, &t_prevRF );
      
      if (i4_ret < 0)
      {
          JNI_LOGD(("get next scan RF failed  line = %d \n",__LINE__));
          if (currRF != NULL)
          {
              JNI_DEL_LOCAL_REF(env, currRF);
          }
          return -1;
      }
      jstring rfChannel = JNI_NEW_STRING_UTF(env, t_prevRF.rfChannel);
      JNI_CALL_OBJECT_METHODV(env, prevRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFChannel), rfChannel);
      JNI_CALL_OBJECT_METHODV(env, prevRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFScanIndex), (jint)t_prevRF.rfScanIdx);
      JNI_CALL_OBJECT_METHODV(env, prevRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFScanFrequency), (jint)t_prevRF.rfScanFreq);
      
      JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_getNextDtmbScanRF_1native line = %d",__LINE__));
      
      if (currRF != NULL)
      {
          JNI_DEL_LOCAL_REF(env, currRF);
      }
      if (prevRF != NULL)
      {
          JNI_DEL_LOCAL_REF(env, prevRF);
      }
      if (rfChannel != NULL)
      {
          JNI_DEL_LOCAL_REF(env, rfChannel);
      }
      return 0;
  }

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getCurrentDtmbScanRF_1native
  (JNIEnv *env, jclass clazz, jstring scanMode, jint channelId, jobject currRF)
{
      JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getCurrentDtmbScanRF_1native line = %d",__LINE__));
      INT32 i4_ret = 0;
      DTMB_SCAN_RF t_currRF;
      memset( &t_currRF, 0, sizeof(t_currRF) );
	
      i4_ret = a_dtmbGetCurrentScanRF( channelId, &t_currRF );
      
      if (i4_ret < 0)
      {
          JNI_LOGD(("get next scan RF failed  line = %d \n",__LINE__));
  
          return -1;
      }
      jstring rfChannel = JNI_NEW_STRING_UTF(env, t_currRF.rfChannel);
      JNI_CALL_OBJECT_METHODV(env, currRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFChannel), rfChannel);
      JNI_CALL_OBJECT_METHODV(env, currRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFScanIndex), (jint)t_currRF.rfScanIdx);
      JNI_CALL_OBJECT_METHODV(env, currRF, Void, CLASS_METHOD_ID(dtmb_scanRF_def,setRFScanFrequency), (jint)t_currRF.rfScanFreq);
      
      JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_getCurrentDtmbScanRF_1native line = %d",__LINE__));
      
      if (currRF != NULL)
      {
          JNI_DEL_LOCAL_REF(env, currRF);
      }
      if (rfChannel != NULL)
      {
          JNI_DEL_LOCAL_REF(env, rfChannel);
      }
      return 0;
  }


EXTERN_C_END
