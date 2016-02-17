#include "tv_jni_util.h"
#include "com_mediatek_tv_service_TVNative.h"

#include "scan/dvbc_scan_service_client.h"

EXTERN_C_START

#define LOG_TAG "scan_service_dvbc"

/* the value HAVE TO be same as the one in JAVA code -----------------------------------end--------- */

/*dvbc scan config*/

static const int JNI_SB_DVBC_CONFIG_IGNORE_ANALOG_CH_ON_SORTING			 = 1 << 0;
static const int JNI_SB_DVBC_CONFIG_SUPPORT_MHEG5_SERVICES				 = 1 << 1;
static const int JNI_SB_DVBC_CONFIG_START_CH_NUM_FOR_NON_LCN_CH			 = 1 << 2;
static const int JNI_SB_DVBC_CONFIG_ALWAYS_APPLY_LCN 					 = 1 << 3;
static const int JNI_SB_DVBC_CONFIG_UPDATE_TO_TEMP_SVL					 = 1 << 4;
static const int JNI_SB_DVBC_CONFIG_KEEP_DUPLICATE_CHANNELS				 = 1 << 5;
static const int JNI_SB_DVBC_CONFIG_SDT_NIT_TIMEOUT						 = 1 << 6;
static const int JNI_SB_DVBC_CONFIG_SUPPORT_MHP_SERVICES                 = 1 << 7;
static const int JNI_SB_DVBC_CONFIG_RESERVE_CH_NUM_BEFORE_NON_LCN_CH	 = 1 << 8;
static const int JNI_SB_DVBC_CONFIG_NOT_SUPPORT_HDTV					 = 1 << 9;
static const int JNI_SB_DVBC_CONFIG_SIMPLE_SORT_FOR_NON_LCN_CH			 = 1 << 10;
static const int JNI_SB_DVBC_CONFIG_EX_QUICK_BUILD_SVL_BY_SDT			 = 1 << 11;
static const int JNI_SB_DVBC_CONFIG_PRIOR_RF_SCAN_ENABLE				 = 1 << 12;
static const int JNI_SB_DVBC_CONFIG_SCAN_WITHOUT_SCAN_MAP				 = 1 << 13;
static const int JNI_SB_DVBC_CONFIG_TV_RADIO_SEPARATE 					 = 1 << 14;
static const int JNI_SB_DVBC_CONFIG_CUST_1 								 = 1 << 15;
static const int JNI_SB_DVBC_CONFIG_QAM_SR_AUTO_DETECT 					 = 1 << 16;
static const int JNI_SB_DVBC_CONFIG_INSTALL_FREE_SERVICES_ONLY 			 = 1 << 17;
static const int JNI_SB_DVBC_CONFIG_TRUST_NIT_IN_EX_QUICK_SCAN           = 1 << 18;
static const int JNI_SB_DVBC_CONFIG_QUICK_SCAN_IGNORE_SVC_OUT_OF_NETWORK = 1 << 19;


static const int JNI_SB_DVBC_SCAN_INFO_NW_ID_VALID	    = 0x1;    /**< bit mask to indicate nw_id valid        */
static const int JNI_SB_DVBC_SCAN_INFO_BW_VALID         = 0x2;    /**< bit mask to indicate bandwidth valid       */
static const int JNI_SB_DVBC_SCAN_INFO_MOD_VALID        = 0x4;    /**< bit mask to indicate modulation valid       */
static const int JNI_SB_DVBC_SCAN_INFO_SYM_VALID        = 0x8;    /**< bit mask to indicate symbol rate valid        */
static const int JNI_SB_DVBC_SCAN_INFO_START_FREQ_VALID = 0x10;    /**< bit mask to indicate start frequency valid       */
static const int JNI_SB_DVBC_SCAN_INFO_END_FREQ_VALID   = 0x20;    /**< bit mask to inidicate end frequency valid       */

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
    getScanCfg,
    getSvlId,
    getCountryCode,
    getOperatorName,
    getSearchMode,
    getValidMask,
    getNetWorkID,
    getEBw,
    getEMod,
    getSymRate,
    getStartFreq,
    getEndFreq,
    scanParaDVBCMethodsNum
}   _scanParaDVBCMethods;
jmethodID   ScanParaDvbcClassMethods[scanParaDVBCMethodsNum];
jclass      ScanParaDvbcClass;

typedef enum {
    setScanProgress = 0,
    setScanFrequence,
    setScanCompleted,
    setScanCanceled,
    setScanError,
    scanServiceDVBCMethodsNum
}   _scanServiceDVBCMethods;
jmethodID   ScanServiceDvbcClassMethods[scanServiceDVBCMethodsNum];
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
#define SCAN_JNI_GET_JAVAVM(ENV)                                    ((*ENV)->GetJavaVM(ENV, &g_pt_jni_scan_JavaVM))
#define SCAN_JNI_ATTACH_CURRENT_THREAD(ENV)                         ((*g_pt_jni_scan_JavaVM)->AttachCurrentThread(g_pt_jni_scan_JavaVM, &ENV, NULL))
#define SCAN_JNI_DETACH_CURRENT_THREAD(ENV)                         ((*g_pt_jni_scan_JavaVM)->DetachCurrentThread(g_pt_jni_scan_JavaVM))

#define CLASS_METHOD(  className, method )                  (*env)->CallIntMethod(env, className, className##Methods[method])
#define CLASS_METHODV( className, method, args...)          (*env)->CallIntMethod(env, className, className##Methods[method], args)
#define CLASS_STATIC_METHOD(  className, method )           (*env)->CallStaticIntMethod(env, className, className##Methods[method])
#define CLASS_STATIC_METHODV( className, method, args...)   (*env)->CallStaticIntMethod(env, className, className##Methods[method], args)


static JNIEnv *g_pt_jni_scan_env;
static JavaVM *g_pt_jni_scan_JavaVM;

void x_scan_service_dvbc_init_jni (JNIEnv *env)
{
    jclass      _ScanParamsClass;
    jclass      _ScanParaDvbcClass;
    jclass      _ScanServiceClass;
    JNI_LOGD(("LOG_TAG[%-65s] {%s}\n", __func__, "init"));
    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));

    if (SCAN_JNI_GET_JAVAVM(env) < 0)
    {
        JNI_LOGD(("LOG_TAG[ERROR][%d]{%s}\n", __LINE__, __func__));
    }
    
    g_pt_jni_scan_env = env;
    
    _ScanParamsClass                            = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/ScanParams");
    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));
    ScanParamsClass                             = SCAN_JNI_NEW_GLOBAL_REF(env, _ScanParamsClass);
    ScanParamsMethods[getmScanType]             = JNI_GET_CLASS_METHOD(env, ScanParamsClass, "getmScanType",             "()I");
    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));

    _ScanParaDvbcClass                         = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/ScanParaDvbc");
    ScanParaDvbcClass                          = SCAN_JNI_NEW_GLOBAL_REF(env, _ScanParaDvbcClass);
    ScanParaDvbcClassMethods[getScanCfg]            = JNI_GET_CLASS_METHOD(env, ScanParaDvbcClass, "getScanCfg",       "()I");
    ScanParaDvbcClassMethods[getSvlId]              = JNI_GET_CLASS_METHOD(env, ScanParaDvbcClass, "getSvlId",         "()I");
    ScanParaDvbcClassMethods[getCountryCode]        = JNI_GET_CLASS_METHOD(env, ScanParaDvbcClass, "getCountryCode",   "()Ljava/lang/String;");
    ScanParaDvbcClassMethods[getOperatorName]       = JNI_GET_CLASS_METHOD(env, ScanParaDvbcClass, "getOperatorName",  "()I");
    ScanParaDvbcClassMethods[getSearchMode]         = JNI_GET_CLASS_METHOD(env, ScanParaDvbcClass, "getSearchMode",    "()I");
    ScanParaDvbcClassMethods[getValidMask]          = JNI_GET_CLASS_METHOD(env, ScanParaDvbcClass, "getValidMask",     "()I");
    ScanParaDvbcClassMethods[getNetWorkID]          = JNI_GET_CLASS_METHOD(env, ScanParaDvbcClass, "getNetWorkID",     "()I");
    ScanParaDvbcClassMethods[getEBw]                = JNI_GET_CLASS_METHOD(env, ScanParaDvbcClass, "getEBw",     	  "()I");
    ScanParaDvbcClassMethods[getEMod]               = JNI_GET_CLASS_METHOD(env, ScanParaDvbcClass, "getEMod",     	  "()I");
    ScanParaDvbcClassMethods[getSymRate]            = JNI_GET_CLASS_METHOD(env, ScanParaDvbcClass, "getSymRate",       "()I");
    ScanParaDvbcClassMethods[getStartFreq]          = JNI_GET_CLASS_METHOD(env, ScanParaDvbcClass, "getStartFreq",     "()I");
    ScanParaDvbcClassMethods[getEndFreq]            = JNI_GET_CLASS_METHOD(env, ScanParaDvbcClass, "getEndFreq",       "()I");
    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));
	
    _ScanServiceClass                    = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/service/ScanService");
    ScanServiceClass                     = SCAN_JNI_NEW_GLOBAL_REF(env, _ScanServiceClass);
    ScanServiceDvbcClassMethods[setScanProgress]  = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanProgress",        "(II)I");
    ScanServiceDvbcClassMethods[setScanFrequence] = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanFrequence",       "(I)I");
    ScanServiceDvbcClassMethods[setScanCompleted] = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanCompleted",       "()I");
    ScanServiceDvbcClassMethods[setScanCanceled]  = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanCanceled",        "()I");
    ScanServiceDvbcClassMethods[setScanError]     = SCAN_JNI_GET_CLASS_STATIC_METHOD(env, ScanServiceClass, "setScanError",           "(I)I");
    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));
       
     
    JNI_LOGD(( "\n\n--------------------------------------------------------------------------------\n" ));
    JNI_LOGD(("LOG_TAG[%-65s] {%s}\n", __func__, "init"));
   
    
    return 0;
}

/* scan ScanService for notification */
static VOID _SetScanProgressNfy ( INT32 progress, INT32 channels)
{
    //jclass      ScanService = ScanServiceClass;
    JNIEnv *    env = g_pt_jni_scan_env;
    jclass      jclass_TVCallBack = NULL;
    int         ret;
    jint        withoutAttachDetach = 0; 

    JNI_LOGD(("LOG_TAG[%d]{%s}(progress: %d)(channels: %d)\n", __LINE__, __func__, progress, channels));

    /*
    * Check if we're already one with the VM.
    */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(env);
    if (!withoutAttachDetach)
    {
        ret = SCAN_JNI_ATTACH_CURRENT_THREAD(env);
        if (ret < 0)
        {
            JNI_LOGD(("LOG_TAG[ERROR][%d]{%s}\n", __LINE__, __func__));
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

static VOID _SetScanFrequenceNfy ( INT32 frequence)
{
    //jclass      ScanService = ScanServiceClass;
    JNIEnv *    env = g_pt_jni_scan_env;
    jclass      jclass_TVCallBack = NULL; 
    int         ret;
    jint        withoutAttachDetach   = 0;
    
    JNI_LOGD(("LOG_TAG[%d]{%s}(frequence: %d)\n", __LINE__, __func__, frequence));

    /*
    * Check if we're already one with the VM.
    */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(env);
    if (!withoutAttachDetach)
    {
        ret = SCAN_JNI_ATTACH_CURRENT_THREAD(env);
        if (ret < 0)
        {
            JNI_LOGD(("LOG_TAG[ERROR][%d]{%s}\n", __LINE__, __func__));
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

static VOID _SetScanCompletedNfy ( )
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
            JNI_LOGD(("LOG_TAG[ERROR][%d]{%s}\n", __LINE__, __func__));
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

static VOID _SetScanCanceledNfy ( )
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
            JNI_LOGD(("LOG_TAG[ERROR][%d]{%s}\n", __LINE__, __func__));
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

static VOID _SetScanErrorNfy ( INT32 errorCode )
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
            JNI_LOGD(("LOG_TAG[ERROR][%d]{%s}\n", __LINE__, __func__));
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


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    startScanDVBC_native
 * Signature: (Ljava/lang/String;Lcom/mediatek/tv/model/ScanParams;Lcom/mediatek/tv/model/ScanListener;)Z
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_startScanDvbc_1native
(JNIEnv *env, jclass clazz, jstring scanMode, jobject ScanParams )
{
    
    const char*     str;
    char *          s;
    jclass          scanParamsclazz;
    jmethodID       scanParamsGetmScanType;
    int             scanType;
    INT32           i4_ret;
	//jstring         s_countryCode;	

	char*           p_country_code = "CHN";
	int             svlId;
    int             scanCfg;
	int    			operatorName = DVBC_OPERATOR_NAME_OTHERS;
	//int    			scanType;
	int    			nitSearchMode;
	int    			validMask;
	int    			nwID;
	int    			eBw;
	int    			eMod;
	int    			symRate;
	int    			startFreq;
	int    			endFreq;
    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));

    str = JNI_GET_STRING_UTF_CHARS( env, scanMode );
    if (str == NULL) {
        return JNI_FALSE;
    }
    JNI_LOGD(("LOG_TAG[%-65s] {scanMode: %s}\n", __func__, str));

    //if ((s = strcmp(str, "dvb_cable")) == NULL)
    //{
		JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));
        DVBC_SCAN_DATA_T      t_dvbc_data;
        DVBC_SCAN_NFY_T       t_dvbc_scan_nfy;

        JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));

        t_dvbc_scan_nfy.setScanProgressNfy   = _SetScanProgressNfy;
        t_dvbc_scan_nfy.setScanFrequenceNfy  = _SetScanFrequenceNfy;
        t_dvbc_scan_nfy.setScanCompletedNfy  = _SetScanCompletedNfy;
        t_dvbc_scan_nfy.setScanCanceledNfy   = _SetScanCanceledNfy; 
        t_dvbc_scan_nfy.setScanErrorNfy      = _SetScanErrorNfy;

        memset( &t_dvbc_data, 0, sizeof(DVBC_SCAN_DATA_T) );

        scanType = (int) CLASS_METHOD( ScanParams, getmScanType );

        switch (scanType)
        {
            case 2:     /* SCAN_TYPE = 2; */
                t_dvbc_data.e_scan_type = SB_DVBC_SCAN_TYPE_MANUAL_FREQ;
                JNI_LOGD(("LOG_TAG[%-65s] {scanType: %s}\n", __func__, "SB_DVBC_SCAN_TYPE_UPDATE"));
                break;
                
            case 3:     /* SCAN_TYPE = 3; */
                t_dvbc_data.e_scan_type = SB_DVBC_SCAN_TYPE_UPDATE;
                JNI_LOGD(("LOG_TAG[%-65s] {scanType: %s}\n", __func__, "SB_DVBC_SCAN_TYPE_UPDATE"));
                break;
                
            case 1:     /* SCAN_TYPE_FULL = 1; */
            default: 
                t_dvbc_data.e_scan_type = SB_DVBC_SCAN_TYPE_FULL_MODE;
                JNI_LOGD(("LOG_TAG[%-65s] {scanType: %s}\n", __func__, "SB_DVBC_SCAN_TYPE_FULL_MODE"));
                break;
        }

        if (JNI_IS_INSTANCE_OF( env, ScanParams, ScanParaDvbcClass ))
        { 
            jobject ScanParaDvbcClass   = ScanParams;
            svlId                       = CLASS_METHOD( ScanParaDvbcClass, getSvlId );
			JNI_LOGD(("LOG_TAG[%d]{%s}\n", svlId, __func__));
            scanCfg                    	= CLASS_METHOD( ScanParaDvbcClass, getScanCfg );
			JNI_LOGD(("LOG_TAG[%d]{%s}\n", scanCfg, __func__));
            operatorName                = CLASS_METHOD( ScanParaDvbcClass, getOperatorName );
            nitSearchMode               = CLASS_METHOD( ScanParaDvbcClass, getSearchMode );
            validMask                   = CLASS_METHOD( ScanParaDvbcClass, getValidMask );
            nwID                     	= CLASS_METHOD( ScanParaDvbcClass, getNetWorkID );
            eBw                     	= CLASS_METHOD( ScanParaDvbcClass, getEBw );
            eMod                        = CLASS_METHOD( ScanParaDvbcClass, getEMod );
            symRate                     = CLASS_METHOD( ScanParaDvbcClass, getSymRate );
            startFreq                   = CLASS_METHOD( ScanParaDvbcClass, getStartFreq );
            endFreq                     = CLASS_METHOD( ScanParaDvbcClass, getEndFreq );
			JNI_LOGD(("[%d]{%s}\n",svlId , __func__));
		    JNI_LOGD(("[%d]{%s}\n",scanCfg , __func__));
		    JNI_LOGD(("[%d]{%s}\n", operatorName, __func__));
		    JNI_LOGD(("[%d]{%s}\n", nitSearchMode, __func__));
		    JNI_LOGD(("[%d]{%s}\n",validMask , __func__));
		    JNI_LOGD(("[%d]{%s}\n",nwID , __func__));
		    JNI_LOGD(("[%d]{%s}\n", eBw, __func__));
		    JNI_LOGD(("[%s]{%s}\n", p_country_code, __func__));
			JNI_LOGD(("LOG_TAG[%-65s] {scanType: %s}\n", __func__, "SB_DVBC_SCAN_TYPE_FULL_MODE"));
			
			//if (s_countryCode != NULL)
            //{
                //p_country_code = JNI_GET_STRING_UTF_CHARS(env,(jstring)s_countryCode);
            //}
            
			if (p_country_code!= NULL)
			{
				strncpy(t_dvbc_data.t_country_code, p_country_code, ISO_3166_COUNT_LEN);
			}
            t_dvbc_data.ui2_nw_id 		= nwID;
            t_dvbc_data.ui4_sym_rate 	= symRate;
            t_dvbc_data.ui4_start_freq 	= startFreq;
            t_dvbc_data.ui4_end_freq 	= endFreq;
			
            //if (s_countryCode!= NULL)
            //{
                //JNI_RELEASE_STRING_UTF_CHARS(env,(jstring)s_countryCode,p_country_code);
                //JNI_DEL_LOCAL_REF(env,s_countryCode);
            //}

			switch (operatorName)
			{
                /*
				case 1: 	
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_UPC;
					break;

				case 2: 	
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_COMHEM;
					break;
				case 3: 	
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CANAL_DIGITAL;
					break;
					
				case 4: 	
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_TELE2;
					break;
					
				case 5: 	
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_STOFA;
					break;
					
				case 6: 	
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_YOUSEE;
					break;

				case 7: 	
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_ZIGGO;
					break;
					
				case 8: 	
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_UNITYMEDIA;
					break;
					
				case 9: 	
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_NUMERICABLE;
					break;	
				*/
				
				case 1001:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_GUANGZHOU_SHENGWANG;
					break;
				case 1002:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_GUANGZHOU_SHIWANG;
					break;
				case 1003:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_HUNAN_GUANGDIAN;
					break;
				case 1004:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_CHANGSHA_GUOAN;
					break;
				case 1005:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_WUHAN_SHENGWANG;
					break;
				case 1006:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_WUHAN_SHIWANG;
					break;
				case 1007:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_XIAN_GUANGDIAN;
					break;
				case 1008:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_BEIJING_GEHUAYOUXIAN;
					break;
				case 1009:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_QINGDAO_GUANGDIAN;
					break;
				case 1010:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_HUBEI_HUANGSHI;
					break;
				case 1011:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_DALIAN_GUANGDIAN;
					break;				
				case 1012:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_SHAOXING_GUANGDIAN;
					break;
				case 1013:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_NEIMENGGU_YOUXIAN;
					break;
                case 1014:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_EZHOU_YOUXIAN;
					break;
                case 1016:
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_CHINA_SANMING_YOUXIAN;
                    break;
                    
				case 0: 
				default: 
					t_dvbc_data.t_operator_name = DVBC_OPERATOR_NAME_OTHERS;
					break;
			}
			
			switch (nitSearchMode)
			{
				case 1: 	/* operatorName = 1 */
					t_dvbc_data.e_nit_search_mode = SB_DVBC_NIT_SEARCH_MODE_QUICK;
					break;

				case 2: 	/* operatorName = 2 */
					t_dvbc_data.e_nit_search_mode = SB_DVBC_NIT_SEARCH_MODE_EX_QUICK;
					break;
				case 3: 	/* operatorName = 3 */
					t_dvbc_data.e_nit_search_mode = SB_DVBC_NIT_SEARCH_MODE_NUM;
					break;
				
				case 0: 	/* operatorName = 0*/
				default: 
					t_dvbc_data.e_nit_search_mode = SB_DVBC_NIT_SEARCH_MODE_OFF;
					break;
			}

			switch (eBw)
			{
				case 1: 	/* eBw = 1 */
					t_dvbc_data.e_bw = BW_6_MHz;
					break;

				case 2: 	/* eBw = 2 */
					t_dvbc_data.e_bw = BW_7_MHz;
					break;
				case 3: 	/* eBw = 3 */
					t_dvbc_data.e_bw = BW_8_MHz;
					break;
				
				case 0: 	/* eBw = 0*/
				default: 
					t_dvbc_data.e_bw = BW_UNKNOWN;
					break;
			}

            #if 1/*Make sure this validmask defined in Java code is same as the C code in sb_dvbc_eng. */
			if (validMask&JNI_SB_DVBC_SCAN_INFO_NW_ID_VALID)
			{
				t_dvbc_data.ui2_valid_mask |= SB_DVBC_SCAN_INFO_NW_ID_VALID;
			}
			if (validMask&JNI_SB_DVBC_SCAN_INFO_BW_VALID)
			{
				t_dvbc_data.ui2_valid_mask |= SB_DVBC_SCAN_INFO_BW_VALID;
			}
			if (validMask&JNI_SB_DVBC_SCAN_INFO_MOD_VALID)
			{
				t_dvbc_data.ui2_valid_mask |= SB_DVBC_SCAN_INFO_MOD_VALID;
			}
			if (validMask&JNI_SB_DVBC_SCAN_INFO_SYM_VALID)
			{
				t_dvbc_data.ui2_valid_mask |= SB_DVBC_SCAN_INFO_SYM_VALID;
			}
			if (validMask&JNI_SB_DVBC_SCAN_INFO_START_FREQ_VALID)
			{
				t_dvbc_data.ui2_valid_mask |= SB_DVBC_SCAN_INFO_START_FREQ_VALID;
			}
			if (validMask&JNI_SB_DVBC_SCAN_INFO_END_FREQ_VALID)
			{
				t_dvbc_data.ui2_valid_mask |= SB_DVBC_SCAN_INFO_END_FREQ_VALID;
			}
			#else
			switch (validMask)
			{
                case 0x0: 
                    t_dvbc_data.ui2_valid_mask = 0;
					break;
				case 0x1: 	/* validMask = 1 */
					t_dvbc_data.ui2_valid_mask |= SB_DVBC_SCAN_INFO_NW_ID_VALID;
					break;

				case 0xC: 	/* validMask = 2 */
					t_dvbc_data.ui2_valid_mask = SB_DVBC_SCAN_INFO_MOD_VALID | SB_DVBC_SCAN_INFO_SYM_VALID;
					break;
				case 0x1C: 	/* validMask = 3 */
					t_dvbc_data.ui2_valid_mask = SB_DVBC_SCAN_INFO_MOD_VALID | SB_DVBC_SCAN_INFO_SYM_VALID
                                            | SB_DVBC_SCAN_INFO_START_FREQ_VALID;
					break;
				
				case 0x3D: 	/* validMask = 4*/
				default: 
					t_dvbc_data.ui2_valid_mask = SB_DVBC_SCAN_INFO_MOD_VALID | SB_DVBC_SCAN_INFO_SYM_VALID
                                            | SB_DVBC_SCAN_INFO_START_FREQ_VALID | SB_DVBC_SCAN_INFO_END_FREQ_VALID;
					break;
			}
            #endif 

			switch (eMod)
			{
				case 1: 	/* eMod = 1 */
					t_dvbc_data.e_mod = MOD_QAM_16;
					break;
					
				case 2: 	/* eMod = 2 */
					t_dvbc_data.e_mod = MOD_QAM_32;
					break;

				case 3: 	/* eMod = 3 */
					t_dvbc_data.e_mod = MOD_QAM_64;
					break;
					
				case 4: 	/* eMod = 4 */
					t_dvbc_data.e_mod = MOD_QAM_128;
					break;
					
				case 5: 	/* eMod = 5*/
					t_dvbc_data.e_mod = MOD_QAM_256;
					break;
					
				case 0: 	/* eMod = 0*/
				default: 
					t_dvbc_data.e_mod = MOD_UNKNOWN;
					break;
			}

            if (scanCfg & JNI_SB_DVBC_CONFIG_IGNORE_ANALOG_CH_ON_SORTING) /* = 1 << 0; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_IGNORE_ANALOG_CH_ON_SORTING;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_SUPPORT_MHEG5_SERVICES) /* = 1 << 1; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_SUPPORT_MHEG5_SERVICES;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_START_CH_NUM_FOR_NON_LCN_CH) /* = 1 << 2; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_START_CH_NUM_FOR_NON_LCN_CH;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_ALWAYS_APPLY_LCN) /* = 1 << 3; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_ALWAYS_APPLY_LCN;
            }            
            if (scanCfg & JNI_SB_DVBC_CONFIG_UPDATE_TO_TEMP_SVL) /* = 1 << 4; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_UPDATE_TO_TEMP_SVL;
            }
			if (scanCfg & JNI_SB_DVBC_CONFIG_KEEP_DUPLICATE_CHANNELS) /* = 1 << 5; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_KEEP_DUPLICATE_CHANNELS;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_SDT_NIT_TIMEOUT) /* = 1 << 6; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_SDT_NIT_TIMEOUT;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_SUPPORT_MHP_SERVICES) /* = 1 << 7; */
            {
                //t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_SUPPORT_MHP_SERVICES;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_RESERVE_CH_NUM_BEFORE_NON_LCN_CH) /* = 1 << 8; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_RESERVE_CH_NUM_BEFORE_NON_LCN_CH;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_NOT_SUPPORT_HDTV) /* = 1 << 9; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_NOT_SUPPORT_HDTV;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_SIMPLE_SORT_FOR_NON_LCN_CH) /* = 1 << 10; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_SIMPLE_SORT_FOR_NON_LCN_CH;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_EX_QUICK_BUILD_SVL_BY_SDT) /* = 1 << 11; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_EX_QUICK_BUILD_SVL_BY_SDT;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_PRIOR_RF_SCAN_ENABLE) /* = 1 << 12; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_PRIOR_RF_SCAN_ENABLE;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_SCAN_WITHOUT_SCAN_MAP) /* = 1 << 13; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_SCAN_WITHOUT_SCAN_MAP;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_TV_RADIO_SEPARATE) /* = 1 << 14; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_TV_RADIO_SEPARATE;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_CUST_1) /* = 1 << 15; */
            {
                //t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_CUST_1;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_QAM_SR_AUTO_DETECT) /* = 1 << 16; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_QAM_SR_AUTO_DETECT;
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_INSTALL_FREE_SERVICES_ONLY) /* = 1 << 17; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_INSTALL_FREE_SERVICES_ONLY;
            }			
            if (scanCfg & JNI_SB_DVBC_CONFIG_TRUST_NIT_IN_EX_QUICK_SCAN) /* = 1 << 18; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_TRUST_NIT_IN_EX_QUICK_SCAN;                
            }
            if (scanCfg & JNI_SB_DVBC_CONFIG_QUICK_SCAN_IGNORE_SVC_OUT_OF_NETWORK) /* = 1 << 19; */
            {
                t_dvbc_data.t_eng_cfg_flag |= SB_DVBC_CONFIG_QUICK_SCAN_IGNORE_SVC_OUT_OF_NETWORK;                
            }
        }

        JNI_LOGD(("LOG_TAG[%d]{%s}(scan type:%d){temp_svl_id : %d}\n", __LINE__, __func__, t_dvbc_data.e_scan_type, t_dvbc_data.ui2_temp_svl_id));

        i4_ret = a_dvbcScanStart( &t_dvbc_data, &t_dvbc_scan_nfy, NULL);
        JNI_LOGD(("LOG_TAG[%d]{%s}, return %d\n", __LINE__, __func__, i4_ret));

    //}

    JNI_RELEASE_STRING_UTF_CHARS(env, scanMode, str);    

    if (ScanParams != NULL)
    {
		JNI_DEL_LOCAL_REF(env, ScanParams);
    }
    
    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));

    return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    cancelScanDVBC_native
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_cancelScanDvbc_1native
(JNIEnv *env, jclass clazz, jstring scanMode)
{
    const char*     str;
    char *          s;

    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));

    str = JNI_GET_STRING_UTF_CHARS( env, scanMode );
    if (str == NULL) {
        return JNI_FALSE;
    }
    
    if ((s = strstr(str, "dvb-cable")) != NULL)
    {
        JNI_LOGD(("LOG_TAG[%-65s] {scanMode: %s}\n", __func__, str));
        a_dvbcScanCancel();
    }
    
    JNI_RELEASE_STRING_UTF_CHARS(env, scanMode, str);

    return 0;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getDvbcScanTypeNum_native
 * Signature: (Ljava/lang/String;Lcom/mediatek/tv/model/DvbcProgramType;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getDvbcScanTypeNum_1native
  (JNIEnv *env, jclass clazz, jstring scanMode, jobject scanProgramNumber)
{
    JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getDvbcScanTypeNum_1native line = %d",__LINE__));
	INT32 i4_ret = 0;
	DVBC_SCANNED_CH_T t_scan_ch;
	memset( &t_scan_ch, 0, sizeof(DVBC_SCANNED_CH_T) );
	
	i4_ret = a_dvbcGetScanTypeNumber( &t_scan_ch );
	
    if (i4_ret < 0)
    {
        JNI_LOGD(("get scan type number failed  line = %d \n",__LINE__));

        return -1;
    }

    JNI_CALL_OBJECT_METHODV(env,scanProgramNumber,Void,CLASS_METHOD_ID(scan_program_type_def,setRadioNumber), 
		                    (jint)t_scan_ch.ui2_num_radio_ch);
	JNI_CALL_OBJECT_METHODV(env,scanProgramNumber,Void,CLASS_METHOD_ID(scan_program_type_def,setTvNumber), 
		                    (jint)t_scan_ch.ui2_num_tv_ch);
	JNI_CALL_OBJECT_METHODV(env,scanProgramNumber,Void,CLASS_METHOD_ID(scan_program_type_def,setAppNumber), 
		                    (jint)t_scan_ch.ui2_num_app_ch);
	
    JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_getDvbcScanTypeNum_1native line = %d",__LINE__));
	
    if (scanProgramNumber != NULL)
    {
		JNI_DEL_LOCAL_REF(env, scanProgramNumber);
    }
    
	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getDvbcFreqBound_native
 * Signature: (Ljava/lang/String;Lcom/mediatek/tv/model/DvbcFreqBound;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getDvbcFreqRange_1native
(JNIEnv *env, jclass clazz, jstring scanMode, jobject dvbcTunerFreqBound)
{
    JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getDvbcFreqRange_1native line = %d",__LINE__));
	INT32 i4_ret = 0;
	SB_FREQ_RANGE_T t_scan_range;
	memset( &t_scan_range, 0, sizeof(SB_FREQ_RANGE_T) );
	
	i4_ret = a_dvbcGetScanRange( &t_scan_range );
	
    if (i4_ret < 0)
    {
        JNI_LOGD(("get scan range failed  line = %d \n",__LINE__));

        return -1;
    }

    JNI_CALL_OBJECT_METHODV(env,dvbcTunerFreqBound,Void,CLASS_METHOD_ID(dvbc_freq_range_def,setLowerTunerFreqBound), 
		                    (jint)t_scan_range.ui4_lower_freq);
	JNI_CALL_OBJECT_METHODV(env,dvbcTunerFreqBound,Void,CLASS_METHOD_ID(dvbc_freq_range_def,setUpperTunerFreqBound), 
		                    (jint)t_scan_range.ui4_upper_freq);
	
    JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_getDvbcFreqRange_1native line = %d",__LINE__));
	
    if (dvbcTunerFreqBound != NULL)
    {
		JNI_DEL_LOCAL_REF(env, dvbcTunerFreqBound);
    }
    
	return 0;
}

  
/*
* Class:     com_mediatek_tv_service_TVNative
* Method:    getDvbcMainFrequence_native
* Signature: (Ljava/lang/String;Lcom/mediatek/tv/model/MainFrequence;)I
*/
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getDvbcMainFrequence_1native
(JNIEnv *env, jclass clazz, jstring scanMode, jobject mainFrequence)
{
	JNI_LOGD(("[%d]Enter Java_com_mediatek_tv_service_TVNative_getDvbcMainFrequence_1native\n",__LINE__));
	INT32 i4_ret = 0;
	DVBC_MAIN_FREQ_T t_main_freq;
	memset( &t_main_freq, 0, sizeof(DVBC_MAIN_FREQ_T) );
	  
	i4_ret = a_dvbcGetMainFrequence( &t_main_freq );
	  
	if (i4_ret < 0)
	{
		JNI_LOGD(("[%d]get scan type number failed\n",__LINE__));

		return -1;
	}

	JNI_CALL_OBJECT_METHODV(env,mainFrequence,Void,CLASS_METHOD_ID(main_frequence_def,setNitVersion), 
	                          (jint)t_main_freq.ui1_nit_ver);
	JNI_CALL_OBJECT_METHODV(env,mainFrequence,Void,CLASS_METHOD_ID(main_frequence_def,setTsCount), 
	                          (jint)t_main_freq.ui2_ts_count);
	JNI_CALL_OBJECT_METHODV(env,mainFrequence,Void,CLASS_METHOD_ID(main_frequence_def,setMainFrequence), 
	                          (jint)t_main_freq.ui4_freq);
	  
	JNI_LOGD(("[%d]Leave Java_com_mediatek_tv_service_TVNative_getDvbcMainFrequence_1native. freq: %luHz, ts count: %u, nit ver: %u\n",
        			__LINE__, 
        			t_main_freq.ui4_freq, 
        			t_main_freq.ui2_ts_count,
        			t_main_freq.ui1_nit_ver));
	  
	return 0;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getDefaultSymRate_native
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getDefaultSymRate_1native
(JNIEnv *env, jclass clazz , jstring countryCode)
{
	INT32  i4_ret       = 0;
	UINT32 ui4_sym_rate = 0;
	ISO_3166_COUNT_T    t_country_code;
    const char*     str;
    char *          s;

    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));
	if (str!= NULL)
	{
		strncpy(t_country_code, str, ISO_3166_COUNT_LEN);
	}

    str = JNI_GET_STRING_UTF_CHARS( env, countryCode );

    i4_ret = a_dvbcGetDefaultSymRate(t_country_code, &ui4_sym_rate );
	
    JNI_RELEASE_STRING_UTF_CHARS(env, countryCode, str);
	
    if (i4_ret < 0 ) {
        return JNI_FALSE;
    }
	
    return (jint)ui4_sym_rate;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getDefaultFrequence_native
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getDefaultFrequence_1native
(JNIEnv *env, jclass clazz, jstring countryCode)
{
	INT32  i4_ret        = 0;
	UINT32 ui4_frequency = 0;
	ISO_3166_COUNT_T t_country_code;
    const char*      str;
    char *           s;

    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));
	
	if (str!= NULL)
	{
		strncpy(t_country_code, str, ISO_3166_COUNT_LEN);
	}

    str = JNI_GET_STRING_UTF_CHARS( env, countryCode );

    i4_ret = a_dvbcGetDefaultFrequence(t_country_code, &ui4_frequency );
	
    JNI_RELEASE_STRING_UTF_CHARS(env, countryCode, str);
		
    if (i4_ret < 0 ) {
        return JNI_FALSE;
    }
	
    return (jint)ui4_frequency;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getDefaultEMod_native
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getDefaultEMod_1native
(JNIEnv *env, jclass clazz, jstring countryCode)
{
	INT32  i4_ret   = 0;
	UINT8  ui1_emod = 0;
	ISO_3166_COUNT_T t_country_code;
    const char*      str;
    char *           s;

    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));
	
	if (str!= NULL)
	{
		strncpy(t_country_code, str, ISO_3166_COUNT_LEN);
	}

    str = JNI_GET_STRING_UTF_CHARS( env, countryCode );

    i4_ret = a_dvbcGetDefaultEMod(t_country_code, &ui1_emod );
	
    JNI_RELEASE_STRING_UTF_CHARS(env, countryCode, str);
	
    if (i4_ret < 0 ) {
        return JNI_FALSE;
    }
	
    return (jint)ui1_emod;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getDefaultNwID_native
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getDefaultNwID_1native
(JNIEnv *env, jclass clazz, jstring countryCode)
{
	INT32  i4_ret        = 0;
	UINT16 ui2_nw_id_tmp = 0;
	ISO_3166_COUNT_T t_country_code;
    const char*      str;
    char *           s;

    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));
	
	if (str!= NULL)
	{
		strncpy(t_country_code, str, ISO_3166_COUNT_LEN);
	}

    str = JNI_GET_STRING_UTF_CHARS( env, countryCode );

    i4_ret = a_dvbcGetDefaultNwID(t_country_code, &ui2_nw_id_tmp );
	
    JNI_RELEASE_STRING_UTF_CHARS(env, countryCode, str);
	
    if (i4_ret < 0 ) {
        return JNI_FALSE;
    }
	
    return (jint)ui2_nw_id_tmp;
}

EXTERN_C_END

