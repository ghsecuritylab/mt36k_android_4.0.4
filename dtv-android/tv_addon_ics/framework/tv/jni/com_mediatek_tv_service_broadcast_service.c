#include "tv_jni_util.h"
#include "com_mediatek_tv_service_TVNative.h"


#include "brdcst/a_brdcst_jni.h"


EXTERN_C_START
#ifdef TAG
#undef TAG
#endif
#define TAG "[BRDCST-JNI] "

static JNIEnv *gEnv;

static JNIEnv *gp_jni_brdcst_env;
static JavaVM *gp_jni_brdcst_JavaVM;

#define BRDCST_JNI_GET_JAVAVM(ENV)                  ((*ENV)->GetJavaVM(ENV, &gp_jni_brdcst_JavaVM))
#define BRDCST_JNI_ATTACH_CURRENT_THREAD(ENV)       ((*gp_jni_brdcst_JavaVM)->AttachCurrentThread(gp_jni_brdcst_JavaVM, &ENV, NULL))
#define BRDCST_JNI_DETACH_CURRENT_THREAD(ENV)       ((*gp_jni_brdcst_JavaVM)->DetachCurrentThread(gp_jni_brdcst_JavaVM))
#define BRDCST_JNI_NEW_GLOBAL_REF(ENV,CLASS)        ((*ENV)->NewGlobalRef(ENV,CLASS))

#define BRDCST_JNI_GET_CLASS_STATIC_METHOD(ENV,CLASS,NAME,SIG)        ((*ENV)->GetStaticMethodID(ENV,CLASS,NAME,SIG))

#define BRDCST_CALL_STATIC_METHODV(ENV, CLASS, METHOD, args...)   ((*ENV)->CallStaticVoidMethod(ENV, CLASS, METHOD, args))


jclass      gBrdcstSvcNtyClass;
jmethodID   gBrdcstSvcNtyMethodID;
static UINT16  ui2_audio_decode_type = 0;

static void
_pf_jni_brdcst_nfy (const char *msg_type,
                    int        data1,
                    VOID*       pv_tag)
{
    int ret;
    jstring jstr;
    jclass      BrdcstService = gBrdcstSvcNtyClass;
    JNIEnv *    env = gp_jni_brdcst_env;    
    jclass      jclass_TVCallBack  = NULL;
    jint        withoutAttachDetach   = 0;
    
    JNI_LOGD((" _pf_jni_brdcst_nfy -[%s][%d]", msg_type, data1));
    
    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(env);
    if (!withoutAttachDetach)
    {
		    ret = BRDCST_JNI_ATTACH_CURRENT_THREAD(env);
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
    //jstr = (*gEnv)->NewStringUTF(gEnv, msg_type);
    jstr = JNI_NEW_STRING_UTF(env, msg_type);

	  if(jstr != NULL)
	  {
        JNI_CALL_STATIC_METHODV(env,jclass_TVCallBack,Void,CMI(TVCallBack_def,TVCallBack_nfySvctxMsgCB),jstr,data1);
		    JNI_DEL_LOCAL_REF(env,jstr);
	  }
    //BRDCST_CALL_STATIC_METHODV(env, BrdcstService, gBrdcstSvcNtyMethodID, jstr, data1);
    if (!withoutAttachDetach)
    {
        BRDCST_JNI_DETACH_CURRENT_THREAD(env);
    }

    JNI_LOGD((" _pf_jni_brdcst_nfy -[%s][%d] - end", msg_type, data1));
}

void jni_brdcst_svc_init(JNIEnv *env)
{
//    jclass _brdcstSvcNtyClass;

    JNI_LOGD((" initBroadcastService begin\n"));
    
    gp_jni_brdcst_env = env;
    
    if (BRDCST_JNI_GET_JAVAVM(env) < 0)
    {
        JNI_LOGD(("[ERROR][%d]{%s}\n", __LINE__, __func__));
    }            
    JNI_LOGD((" initBroadcastService end\n"));
    
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    channelSelect_native
 * Signature: (Lcom/mediatek/tv/model/ChannelInfo;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_channelSelect_1native
  (JNIEnv *env, jclass clazz, jobject chInfo, jboolean b_focus)
{
    jint svlID, channelID, svlRecID;

    gEnv      = env;
        
    const char* ps_chAudioLang = NULL;
    UINT8       ui1_audio_mts  = 0;
	  UINT8		ui1_audio_index  = 0;
        
    svlID     = (jint)JNI_CALL_OBJECT_METHOD(env,chInfo,Int,CLASS_METHOD_ID(channel_info_def,channel_info_getSvlId));
    svlRecID  = (jint)JNI_CALL_OBJECT_METHOD(env,chInfo,Int,CLASS_METHOD_ID(channel_info_def,channel_info_getSvlRecId));
    channelID = (jint)JNI_CALL_OBJECT_METHOD(env,chInfo,Int,CLASS_METHOD_ID(channel_info_def,channel_info_getChannelId));

    /* to call API*/
    JNI_LOGD((" {%s %d} select channel svl is(%d), recID(%d), channelID(%d) end\n", __FUNCTION__, __LINE__,
                    svlID, svlRecID, channelID));

	
    return a_brdcst_select_channel((UINT16)svlID, (UINT16)svlRecID,
								   (UINT32)channelID, (BOOL)b_focus ,
								    _pf_jni_brdcst_nfy, NULL, 
								    (VOID*)ps_chAudioLang,(UINT8)ui1_audio_mts,(UINT8)ui1_audio_index);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    channelSelectEx_native
 * Signature: (Lcom/mediatek/tv/model/ChannelInfo;Lcom/mediatek/tv/model/ExtraChannelInfo;ZII)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_channelSelectEx_1native
   (JNIEnv *env, jclass clazz, jobject chInfo, jobject exChInfo, jboolean b_focus, jint audioIndex, jint audioMts)	
{
	jint svlID, channelID, svlRecID;
	jstring channelAudioLang;
    const char* ps_chAudioLang = NULL;
	UINT8 ui1_audio_mts = 0;
	INT8  i1_cache_mts  = 0;
	INT8  i1_cache_audioLang_index = 0;

    gEnv      = env;
	
	LOGD("Enter Java_com_mediatek_tv_service_TVNative_channelSelectEx_1native \n");

    svlID     = (jint)JNI_CALL_OBJECT_METHOD(env,chInfo,Int,CLASS_METHOD_ID(channel_info_def,channel_info_getSvlId));
    svlRecID  = (jint)JNI_CALL_OBJECT_METHOD(env,chInfo,Int,CLASS_METHOD_ID(channel_info_def,channel_info_getSvlRecId));
    channelID = (jint)JNI_CALL_OBJECT_METHOD(env,chInfo,Int,CLASS_METHOD_ID(channel_info_def,channel_info_getChannelId));
	
	if(exChInfo == NULL)
	{
		/*do nothing!!!*/
		LOGD("channelSelectEx_1native  exChInfo is NULL!!  \n");
	}
	else
	{
		 i1_cache_mts			  = (INT8)audioMts;
		 i1_cache_audioLang_index = (INT8)audioIndex;

		 if(i1_cache_mts <= 0)
		{
			 LOGD("i1_cache_mts %d is -1!!	\n",i1_cache_mts);
			ui1_audio_mts = 0;
		}
		else
		{
			ui1_audio_mts = i1_cache_mts;
			 LOGD("ui1_audio_mts is %d !!  \n",ui1_audio_mts);
		}

		if(i1_cache_audioLang_index <= 0)
		{	
			 LOGD("i1_cache_audioLang_index is %d small than 1 !!!!!!  \n",i1_cache_audioLang_index);
			i1_cache_audioLang_index = 0;
		}
		else
		{
			LOGD("i1_cache_audioLang_index is %d !!  \n",i1_cache_audioLang_index);
		}

		channelAudioLang = (jstring)JNI_CALL_OBJECT_METHOD(env,exChInfo,Object,CLASS_METHOD_ID(extra_channel_info_def,getChannelAudioLanguage));
		if (channelAudioLang != NULL)
    	{
        	ps_chAudioLang = JNI_GET_STRING_UTF_CHARS(env, channelAudioLang);
			
        	JNI_RELEASE_STRING_UTF_CHARS(env,channelAudioLang, ps_chAudioLang);
    	}
	}
	
	LOGD("Enter Java_com_mediatek_tv_service_TVNative_channelSelectEx_1native 2\n");

	/*in dtv_client : audio type is 0, video type is 1*/

    /* to call API*/
    LOGD(" {%s %d} select channel svl is(%d), recID(%d), channelID(%d) chAudioLang(%s) end\n", __FUNCTION__, __LINE__,
                    svlID, svlRecID, channelID, ps_chAudioLang);
	
	LOGD("Leave Java_com_mediatek_tv_service_TVNative_channelSelectEx_1native \n");
    return a_brdcst_select_channel((UINT16)svlID, (UINT16)svlRecID, 
									(UINT32)channelID, (BOOL)b_focus,
									_pf_jni_brdcst_nfy, NULL,
									(VOID*)ps_chAudioLang,(UINT8)ui1_audio_mts,(UINT8)i1_cache_audioLang_index);
	
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    syncStopService_native
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_syncStopService_1native
  (JNIEnv *env, jclass clazz)
{
    return a_brdcst_sync_stop_svc();
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    syncStopSubtitleStream_native
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_syncStopSubtitleStream_1native
  (JNIEnv *env, jclass clazz)
{
	JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_syncStopSubtitleStream_1native \n"));
    return a_brdcst_stop_subtitle_stream();
	JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_syncStopSubtitleStream_1native \n"));
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    fineTune_native
 * Signature: (Lcom/mediatek/tv/model/ChannelInfo;IZ)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_fineTune_1native
  (JNIEnv *env, jclass clazz, jobject chInfo, jint freq, jboolean b_tuning)
{
    jint svlID, channelID, svlRecID;
    
    svlID     = (jint)JNI_CALL_OBJECT_METHOD(env,chInfo,Int,CLASS_METHOD_ID(channel_info_def,channel_info_getSvlId));
    svlRecID  = (jint)JNI_CALL_OBJECT_METHOD(env,chInfo,Int,CLASS_METHOD_ID(channel_info_def,channel_info_getSvlRecId));
    channelID = (jint)JNI_CALL_OBJECT_METHOD(env,chInfo,Int,CLASS_METHOD_ID(channel_info_def,channel_info_getChannelId));
    
    return a_brdcst_fine_tune((UINT16)svlID, (UINT16)svlRecID, freq, b_tuning);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    freeze_native
 * Signature: (IZ)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_freeze_1native
  (JNIEnv *env, jclass clazz, jint focusID, jboolean b_freeze)
{
	JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_freeze_1native \n"));
    return a_brdcst_freeze((UINT8)focusID, b_freeze);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setVideoMute_native
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setVideoMute_1native
  (JNIEnv *env, jclass clazz)
{    
    return a_brdcst_set_video_mute();
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getVideoResolution_native
 * Signature: (ILcom/mediatek/tv/model/VideoResolution;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getVideoResolution_1native
  (JNIEnv *env, jclass clazz, jint focusID, jobject videRes)
{
    BRDCST_VIDEO_RESOLUTION_T t_brdcst_vieo_resolutin = {0};
    INT32 i4_ret = 0;
    
	JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getVideoResolution_1native \n"));
    i4_ret = a_brdcst_get_video_resolution((UINT8)focusID, &t_brdcst_vieo_resolutin);
	JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_getVideoResolution_1native \n"));
	
    if (i4_ret < 0)
    {
        JNI_LOGD(("get video resolution failed \n"));

        return -1;
    }
    
    JNI_CALL_OBJECT_METHODV(env,videRes,Void,CLASS_METHOD_ID(video_resolution_def,setVideoWidth), (jint)t_brdcst_vieo_resolutin.ui4_width);
    JNI_CALL_OBJECT_METHODV(env,videRes,Void,CLASS_METHOD_ID(video_resolution_def,setVideoHeight), (jint)t_brdcst_vieo_resolutin.ui4_height);
    JNI_CALL_OBJECT_METHODV(env,videRes,Void,CLASS_METHOD_ID(video_resolution_def,setVideoFrameRate), (jint)t_brdcst_vieo_resolutin.ui4_frame_rate);    
    JNI_CALL_OBJECT_METHODV(env,videRes,Void,CLASS_METHOD_ID(video_resolution_def,setProgressive), (jboolean)t_brdcst_vieo_resolutin.b_is_progressive); 
    {
        jobject jo_VideoRes = JNI_NEW_STRING_UTF(env,t_brdcst_vieo_resolutin.s_video_format);
        if (jo_VideoRes != NULL)
        {
            JCOMV(env,videRes,Void,CMI(video_resolution_def,setVideoFormat), (jo_VideoRes) ); 
            JNI_DEL_LOCAL_REF(env,jo_VideoRes);
        }
    }
    return i4_ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getAudioInfo_native
 * Signature: (ILcom/mediatek/tv/model/AudioInfo;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getAudioInfo_1native
  (JNIEnv *env, jclass clazz, jint focusID, jobject audioInfo)
{
    BRDCST_AUDIO_INFO_T t_audio_Info = {0};
    INT32 i4_ret = 0;
    
	JNI_LOGD(("Java_com_mediatek_tv_service_TVNative_getAudioInfo_1native \n"));
    i4_ret = a_brdcst_get_audio_info((UINT8)focusID, &t_audio_Info);
	
    if (i4_ret < 0)
    {
        JNI_LOGD(("get audio info failed \n"));

        return -1;
    }
    
    JNI_CALL_OBJECT_METHODV(env,audioInfo,Void,CLASS_METHOD_ID(audio_info_def,setAlternativeAudio), (jint)t_audio_Info.ui1_alternate_audio);
    JNI_CALL_OBJECT_METHODV(env,audioInfo,Void,CLASS_METHOD_ID(audio_info_def,setMts), (jint)t_audio_Info.ui1_audio_mts);
    return i4_ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getSignalLevelInfo_native
 * Signature: (ILcom/mediatek/tv/model/SignalLevelInfo;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getSignalLevelInfo_1native
 (JNIEnv *env, jclass clazz, jint focusID, jobject signalLevelInfo)
 {
    BRDCST_SIGNAL_LEVEL_T t_signal_level = {0};
    INT32 i4_ret = 0;
    
	JNI_LOGD(("Java_com_mediatek_tv_service_TVNative_getSignalLevelInfo_1native \n"));
    i4_ret = a_brdcst_get_signal_level((UINT8)focusID, &t_signal_level);
	
    if (i4_ret < 0)
    {
        JNI_LOGD(("get signal level failed \n"));

        return -1;
    }
    
    JNI_CALL_OBJECT_METHODV(env,signalLevelInfo,Void,CLASS_METHOD_ID(signalLevel_info_def,setSignalLevel), (jint)t_signal_level.ui1_signal_lvl);
    JNI_CALL_OBJECT_METHODV(env,signalLevelInfo,Void,CLASS_METHOD_ID(signalLevel_info_def,setBer), (jint)t_signal_level.i4_ber);
    return i4_ret;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getDtvAudioLangInfo_native
 * Signature: (Lcom/mediatek/tv/model/AudioLanguageInfo;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getDtvAudioInfo_1native
  (JNIEnv *env, jclass clazz, jobject audioLanguageInfo)
  
{
    BRDCST_DTV_AUDIO_LANG_INFO_T t_dtv_audio_lang_info;
	JNI_MEMSET(&t_dtv_audio_lang_info,0x0,sizeof(BRDCST_DTV_AUDIO_LANG_INFO_T) ) ;
	
	JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getDtvAudioInfo_1native \n"));
    INT32 i4_ret = 0;
    
    i4_ret = a_brdcst_get_dtv_audio_lang_info(&t_dtv_audio_lang_info);
    
    if (i4_ret < 0)
    {
        JNI_LOGD(("get dtv audio language info failed  [%d] \n",i4_ret));

        //return -1;
    }
    
	JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_getDtvAudioInfo_1native before \n"));
    JNI_CALL_OBJECT_METHODV(env,audioLanguageInfo,Void,CLASS_METHOD_ID(audio_language_info_def,setTotalNumber), (jint)t_dtv_audio_lang_info.ui2_total_number);
    JNI_CALL_OBJECT_METHODV(env,audioLanguageInfo,Void,CLASS_METHOD_ID(audio_language_info_def,setDigitalMts), (jint)t_dtv_audio_lang_info.e_channels);
	JNI_CALL_OBJECT_METHODV(env,audioLanguageInfo,Void,CLASS_METHOD_ID(audio_language_info_def,setCurrentAudioLangIndex), (jint)t_dtv_audio_lang_info.ui1_crnt_audio_lang_index);

	ui2_audio_decode_type = t_dtv_audio_lang_info.e_dec_type;
	
	{
        jobject jo_DtvAudioLang = JNI_NEW_STRING_UTF(env,t_dtv_audio_lang_info.s_audio_lang);
        if (jo_DtvAudioLang != NULL)
        {
            JCOMV(env,audioLanguageInfo,Void,CMI(audio_language_info_def,setAudioLanguage), (jo_DtvAudioLang) ); 
            JNI_DEL_LOCAL_REF(env,jo_DtvAudioLang);
        }

		jobject jo_DtvCurrentAudioLang = JNI_NEW_STRING_UTF(env,t_dtv_audio_lang_info.s_current_lang);
        if (jo_DtvCurrentAudioLang != NULL)
        {
            JCOMV(env,audioLanguageInfo,Void,CMI(audio_language_info_def,setCurrentLanguage), (jo_DtvCurrentAudioLang) ); 
            JNI_DEL_LOCAL_REF(env,jo_DtvCurrentAudioLang);
        }
    }
	JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_getDtvAudioInfo_1native before\n"));

	
    return i4_ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setDtvAudioLang_native
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setDtvAudioLang_1native
  (JNIEnv *env, jclass clazz, jstring audioLang)
{
    const char* ps_audioLang = NULL;
    INT32 i4_ret             = 0;
	JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_setDtvAudioLang_1native \n"));

	/*in dtv_client : audio type is 0, video type is 1*/
    if (audioLang != NULL)
    {
        ps_audioLang = JNI_GET_STRING_UTF_CHARS(env, audioLang);
		
		i4_ret = a_brdcst_set_dtv_audio_lang(ps_audioLang);
		
        JNI_RELEASE_STRING_UTF_CHARS(env,audioLang, ps_audioLang);
    }
	JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_setDtvAudioLang_1native \n"));

	return i4_ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setDtvAudioLangByIndex_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setDtvAudioLangByIndex_1native
(JNIEnv *env, jclass clazz, jint focusID, jint audioIndex)
{
	INT32 i4_ret = 0;
	INT8 i1_audioIndex = 0;
	i1_audioIndex = (INT8)audioIndex;
		
	if( i1_audioIndex <= 0)
	{
		JNI_LOGD(("audioIndex  small than 1!! \n"));
		return -1;
	}
	
	i4_ret = a_brdcst_set_dtv_audio_lang_by_index((UINT8)focusID,(UINT8)audioIndex);
		
	JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_setDtvAudioLangByIndex_1native \n"));

	return i4_ret;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getSubtitleInfo_native
 * Signature: (ILcom/mediatek/tv/model/SubtitleInfo;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getSubtitleInfo_1native
  (JNIEnv *env, jclass clazz, jobject subtitleInfo)
{
    BRDCST_SUBTITLE_DVB_T t_sbttlInfo;
	JNI_MEMSET(&t_sbttlInfo,0x0,sizeof(BRDCST_SUBTITLE_DVB_T) ) ;
	
    INT32 i4_ret = 0;
    
	JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getSubtitleInfo_1native \n"));
    i4_ret = a_brdcst_get_subtitle_info(&t_sbttlInfo);
	
	JNI_LOGD(("Enter getSubtitleInfo_1native s_lang=%s  current_sbt_lang=%s  \n",t_sbttlInfo.s_lang,t_sbttlInfo.current_sbt_lang));
    if (i4_ret < 0)
    {
        JNI_LOGD(("get subtitle info failed \n"));
    }

	JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getSubtitleInfo_1native 1\n"));
    
	{
		
		JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getSubtitleInfo_1native 2\n"));
        jobject jo_SubtitleLang = JNI_NEW_STRING_UTF(env,t_sbttlInfo.s_lang);
        if (jo_SubtitleLang != NULL)
        {
			JNI_LOGD(("jo_SubtitleLang \n"));
            JCOMV(env,subtitleInfo,Void,CMI(subtitle_info_def,setSubtitleLang), (jo_SubtitleLang) ); 
            JNI_DEL_LOCAL_REF(env,jo_SubtitleLang);
        }
    }

	{
        jobject jo_CurrentSubtitleLang = JNI_NEW_STRING_UTF(env,t_sbttlInfo.current_sbt_lang);
		JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getSubtitleInfo_1native 3\n"));
        if (jo_CurrentSubtitleLang != NULL)
        {
			JNI_LOGD(("jo_CurrentSubtitleLang \n"));
            JCOMV(env,subtitleInfo,Void,CMI(subtitle_info_def,setCurrentSubtitleLang), (jo_CurrentSubtitleLang) ); 
            JNI_DEL_LOCAL_REF(env,jo_CurrentSubtitleLang);
        }
    }
	JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_getSubtitleInfo_1native \n"));
    return i4_ret;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setSubtitleLang_native
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setSubtitleLang_1native
  (JNIEnv *env, jclass clazz, jstring subtitleLang)
{
	const char* ps_subtitleLang = NULL;
    INT32 i4_ret             = 0;
	JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_setSubtitleLang_1native \n"));

	/*in dtv_client : audio type is 0, video type is 1*/
    if (subtitleLang != NULL)
    {
        ps_subtitleLang = JNI_GET_STRING_UTF_CHARS(env, subtitleLang);
		
		JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_setSubtitleLang_1native 2 \n"));
		i4_ret = a_brdcst_set_subtitle_lang(ps_subtitleLang);
		
		JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_setSubtitleLang_1native 3\n"));
        JNI_RELEASE_STRING_UTF_CHARS(env,subtitleLang, ps_subtitleLang);
    }
	JNI_LOGD(("Leave Java_com_mediatek_tv_service_TVNative_setSubtitleLang_1native \n"));

	return i4_ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getStreamMpegPid_native
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getStreamMpegPid_1native
  (JNIEnv *env, jclass clazz, jstring streamType)
{
	UINT16 ui2_pid         = 0;
    const char* s_TypeName = NULL;

	/*in dtv_client : audio type is 0, video type is 1*/
    if (streamType != NULL)
    {
        s_TypeName = JNI_GET_STRING_UTF_CHARS(env, streamType);
        if (s_TypeName!= NULL){
            if( (strcmp("audio",s_TypeName)) == 0 )
            {
				ui2_pid = a_brdcst_get_stream_mpeg_pid(0);
			}
			if( (strcmp("video",s_TypeName)) == 0 )
            {
				ui2_pid = a_brdcst_get_stream_mpeg_pid(1);
			}
        }

        JNI_RELEASE_STRING_UTF_CHARS(env,(jstring)streamType, s_TypeName);
    }
		
	return ui2_pid;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    selectMpegStreamByPid_native
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_selectMpegStreamByPid_1native
  (JNIEnv * env, jclass clazz, jstring streamType, jint pid)
{
	INT32  i4_ret          = 0;
    const char* s_TypeName = NULL;
	
	/*in dtv_client : audio type is 0, video type is 1*/
    if (streamType != NULL)
    {
        s_TypeName = JNI_GET_STRING_UTF_CHARS(env, streamType);
        if (s_TypeName!= NULL){
            if( (strcmp("audio",s_TypeName)) == 0 )
            {
				i4_ret = a_brdcst_select_mpeg_stream_by_pid(0,pid);
			}
			if( (strcmp("video",s_TypeName)) == 0 )
            {
				i4_ret = a_brdcst_select_mpeg_stream_by_pid(1,pid);
			}
        }

        JNI_RELEASE_STRING_UTF_CHARS(env,(jstring)streamType, s_TypeName);
    }
	    
	return i4_ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    isCaptureLogo_native
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_mediatek_tv_service_TVNative_isCaptureLogo_1native
  (JNIEnv *env, jclass clazz)
{
    BOOL b_capture = FALSE;
	
	a_brdcst_is_capture_logo(&b_capture);
	
    return (jboolean)b_capture;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setMute_native
 * Signature: (Z)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setMute_1native
  (JNIEnv *env, jclass clazz, jboolean b_mute)
{
    return a_brdcst_set_mute((BOOL)b_mute);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getMute_native
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_mediatek_tv_service_TVNative_getMute_1native
  (JNIEnv *env, jclass clazz)
{
    BOOL b_mute = FALSE;

    a_brdcst_get_mute(&b_mute);
    
    return (jboolean)b_mute;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getDtvAudioDecodeType_native
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getDtvAudioDecodeType_1native
  (JNIEnv *env, jclass clazz)
{
    return (jint)ui2_audio_decode_type;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setVideoBlueMute_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setVideoBlueMute_1native
  (JNIEnv *env, jclass clazz, jint focusID,jboolean bBlueMute,jboolean bBlock)
{
	
	INT32  i4_ret  = 0;
    i4_ret = a_brdcst_force_blue_mute_video((UINT8) focusID,(BOOL)bBlueMute,(BOOL)bBlock);
    
	return (jint)i4_ret;
}

/*
 * Class:	  com_mediatek_tv_service_TVNative
 * Method:	  isFreeze_native
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_mediatek_tv_service_TVNative_isFreeze_1native
  (JNIEnv *env, jclass clazz, jint focusID)
{
    BOOL b_freeze = FALSE;

    a_brdcst_is_freeze((UINT8)focusID, &b_freeze);

    return (jboolean)b_freeze;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    isFreeze_native
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_mediatek_tv_service_TVNative_enableFreeze_1native
  (JNIEnv *env, jclass clazz, jint focusID)
{
    BOOL b_enable_freeze = FALSE;

    a_brdcst_enable_freeze((UINT8)focusID, &b_enable_freeze);

    return (jboolean)b_enable_freeze;

}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setDisplayAspectRatio_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setDisplayAspectRatio_1native
  (JNIEnv *env, jclass clazz, jint dispAspRatio)
{
    return a_brdcst_set_disp_aspect_ratio((INT8)dispAspRatio);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getDisplayAspectRatio_native
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getDisplayAspectRatio_1native
  (JNIEnv *env, jclass clazz)
{
    INT8 i1_asp_ratio = 0;
    a_brdcst_get_disp_aspect_ratio(&i1_asp_ratio);
    return (jint)i1_asp_ratio;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    stopStream_native
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_stopStream_1native
  (JNIEnv *env, jclass clazz, jint focusID, jint streamType)
{
    STREAM_TYPE_T e_strm_type = ST_UNKNOWN;
        
    switch((INT32)streamType)
    {
        case 0:
            e_strm_type = ST_AUDIO;
            break;
        case 1:
            e_strm_type = ST_VIDEO;
            break;
        default:
            break;
    }
    return (jint)a_brdcst_stop_stream((UINT8)focusID, (STREAM_TYPE_T)e_strm_type);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    startAudioStream_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_startAudioStream_1native
  (JNIEnv *env, jclass clazz, jint fcousID)
{
    return (jint)a_brdcst_select_audio_stream((UINT8)fcousID);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    syncStopVideoStream_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_syncStopVideoStream_1native
  (JNIEnv *env, jclass clazz, jint fcousID)
{
    return (jint)a_brdcst_sync_stop_video_stream((UINT8) fcousID);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    startVideoStream_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_startVideoStream_1native
  (JNIEnv *env, jclass clazz, jint fcousID)
{
    return (jint)a_brdcst_select_video_stream((UINT8)fcousID);
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    showSnowAsNoSignal_native
 * Signature: (IZ)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_showSnowAsNoSignal_1native
(JNIEnv *env, jclass clazz, jint fcousID, jboolean bSnow)
{
    return (jint)a_brdcst_show_snow_as_no_signal((UINT8) fcousID,(BOOL)bSnow);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    updateTVWindowRegion_native
 * Signature: (IIIII)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_updateTVWindowRegion_1native
  (JNIEnv *env, jclass clazz, jint fcousID, jint winX, jint winY, jint winWidth, jint winHeight)
{
    return (jint)a_brdcst_update_tv_win_region((UINT8)fcousID, (UINT32)winX, (UINT32)winY, (UINT32)winWidth, (UINT32)winHeight);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    updateFocusWindow_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_updateFocusWindow_1native
  (JNIEnv *env, jclass clazz, jint fcousID)
{
    return (jint)a_brdcst_update_focus_win((UINT8)fcousID);
}
/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    updateTVMode_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_updateTVMode_1native
  (JNIEnv *env, jclass clazz, jint tv_mode)
{
    return (jint)a_brdcst_update_tv_mode((TV_MODE_T)tv_mode);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setMTS_native
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setMTS_1native
  (JNIEnv *env, jclass clazz, jint focusID, jint audMTSType)
{
    return (jint)a_brdcst_set_mts((UINT8)focusID, (INT32)audMTSType);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    serviceSet_native
 * Signature: (Ljava/lang/String;Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_serviceSet_1native
  (JNIEnv *env, jclass clazz, jstring setType, jobject setValue)
{
    return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    serviceGet_native
 * Signature: (Ljava/lang/String;Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_serviceGet_1native
  (JNIEnv *env, jclass clazz, jstring getType, jobject getValue)
{
    return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtSetConfig_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtSetConfig_1native
  (JNIEnv *env, jclass clazz, jint configValue)
{
	JNI_LOGD(("{%s %d} configValue = %d.\n", __FUNCTION__, __LINE__, configValue));
	return c_dt_set_config(configValue);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtSetDst_native
 * Signature: (Z)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtSetDst_1native
  (JNIEnv *env, jclass clazz, jboolean setVal)
{
	JNI_LOGD(("{%s %d} setVal = %d.\n", __FUNCTION__, __LINE__, setVal));
	c_dt_set_dst(setVal);

	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtSetTz_native
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtSetTz_1native
  (JNIEnv *env, jclass clazz, jlong setVal)
{
	JNI_LOGD(("{%s %d} setVal = %d.\n", __FUNCTION__, __LINE__, setVal));
	c_dt_set_tz(setVal);

	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtSetUtc_native
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtSetUtc_1native
  (JNIEnv *env, jclass clazz, jlong sec, jint milliSec)
{
	JNI_LOGD(("{%s %d} sec = %d, milliSec = %d.\n", __FUNCTION__, __LINE__, sec, milliSec));
	return c_dt_set_utc(sec, milliSec);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtSetDstCtrl_native
 * Signature: (Z)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtSetDstCtrl_1native
  (JNIEnv *env, jclass clazz, jboolean setVal)
{
	JNI_LOGD(("{%s %d} setVal = %d.\n", __FUNCTION__, __LINE__, setVal));
	c_dt_set_dst_ctrl(setVal);

	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtSetDsChange_native
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtSetDsChange_1native
  (JNIEnv *env, jclass clazz, jlong dsChange)
{
	JNI_LOGD(("{%s %d} dsChange = %d.\n", __FUNCTION__, __LINE__, dsChange));
	c_dt_set_ds_change(dsChange);

	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtSetDsOffset_native
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtSetDsOffset_1native
  (JNIEnv *env, jclass clazz, jlong dsOffset)
{
	JNI_LOGD(("{%s %d} dsOffset = %d.\n", __FUNCTION__, __LINE__, dsOffset));
	c_dt_set_ds_offset(dsOffset);
	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtSetSyncSrc_native
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtSetSyncSrc_1native
  (JNIEnv *env, jclass clazz, jint srcType, jint srcDesc, jstring data)
{	
	CHAR*   s_src_info      	= NULL;
	INT32 	i4_ret;
	
	s_src_info = JNI_GET_STRING_UTF_CHARS(env, data);
	
	i4_ret =  c_dt_set_sync_src(srcType, srcDesc, (VOID*)s_src_info);

	JNI_RELEASE_STRING_UTF_CHARS (env, data, s_src_info);

	return i4_ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtSetTzCtrl_native
 * Signature: (Z)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtSetTzCtrl_1native
  (JNIEnv *env, jclass clazz, jboolean setVal)
{
	JNI_LOGD(("{%s %d} setVal = %d.\n", __FUNCTION__, __LINE__, setVal));
	c_dt_set_tz_ctrl(setVal);
	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtSetSysCountCode_native
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtSetSysCountCode_1native
  (JNIEnv *env, jclass clazz, jbyteArray countCode, jint regionId)
{
	INT32 i4_len, i;
	jbyte   ajbyte_elems[4];
	ISO_3166_COUNT_T t_count_code = {0};
	
	i4_len = (INT32)JNI_GET_ARRAY_LEN(env, countCode);
	if (i4_len != 4)
	{
		JNI_LOGE(("{%s %d}Counrty code len is error. %d.\n",__FUNCTION__, __LINE__, i4_len));
		return -1;
	}

	JNI_LOGD(("{%s %d} len = %d.\n", __FUNCTION__, __LINE__, i4_len));
	JNI_GET_BYTE_ARRAY_REGION(env, countCode, 0, i4_len, ajbyte_elems);
	for(i = 0; i < i4_len; i++)
	{
		if (i != 3)
		{
			JNI_LOGD(("{%s %d}t_count_code[%d] = %c"LOG_TAIL, __FUNCTION__, __LINE__, i, ajbyte_elems[i]));
		}
		JNI_LOGD(("{%s %d} set value.\n", __FUNCTION__, __LINE__));
	    t_count_code[i] = (UINT8)ajbyte_elems[i];
	}

	JNI_LOGD(("{%s %d} regionId = %d.\n", __FUNCTION__, __LINE__, regionId));
	
	return c_dt_set_sys_count_code(t_count_code, regionId);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtGetDst_native
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_mediatek_tv_service_TVNative_dtGetDst_1native
  (JNIEnv *env, jclass clazz)
{
	JNI_LOGD(("{%s %d} Enter.\n", __FUNCTION__, __LINE__));
	return c_dt_get_dst();
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtGetGps_native
 * Signature: ([I)J
 */
JNIEXPORT jlong JNICALL Java_com_mediatek_tv_service_TVNative_dtGetGps_1native
  (JNIEnv *env, jclass clazz, jintArray data)
{
	INT32 i4_gps_leap_sec;
	UINT16	ui2_milli_sec;
	TIME_T	t_time;
	jint   	ajint_val[2]; 
	
	t_time = c_dt_get_gps(&i4_gps_leap_sec, &ui2_milli_sec);

	ajint_val[0] = i4_gps_leap_sec;
	ajint_val[1] = ui2_milli_sec;
	JNI_SET_INT_ARRAY_REGION(env, data, 0, 2, ajint_val);

	JNI_LOGD(("{%s %d} t_time=%d, i4_gps_leap_sec=%d, ui2_milli_sec=%d.\n", __FUNCTION__, __LINE__, t_time, i4_gps_leap_sec, ui2_milli_sec));

	return t_time;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtGetTz_native
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_mediatek_tv_service_TVNative_dtGetTz_1native
  (JNIEnv *env, jclass clazz)
{
	JNI_LOGD(("{%s, %d} Enter.\n", __FUNCTION__, __LINE__));
	return c_dt_get_tz();
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtGetUtc_native
 * Signature: ([I)J
 */
JNIEXPORT jlong JNICALL Java_com_mediatek_tv_service_TVNative_dtGetUtc_1native
  (JNIEnv *env, jclass clazz, jintArray data)
{
	UINT16	ui2_milli_sec;
	DT_COND_T	t_cond;
	TIME_T		t_time;
	jint   	ajint_val[2];
	
	t_time = c_dt_get_utc(&ui2_milli_sec, &t_cond);
	
	ajint_val[0] = ui2_milli_sec;
	ajint_val[1] = t_cond;
	JNI_SET_INT_ARRAY_REGION(env, data, 0, 2, ajint_val);

	JNI_LOGD(("{%s %d} t_time=%d, ui2_milli_sec=%d, t_cond=%d.\n", __FUNCTION__, __LINE__, t_time, ui2_milli_sec, t_cond));
	return t_time;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtGetBrdcstUtc_native
 * Signature: ([I)J
 */
JNIEXPORT jlong JNICALL Java_com_mediatek_tv_service_TVNative_dtGetBrdcstUtc_1native
  (JNIEnv *env, jclass clazz, jintArray data)
{
	UINT16	ui2_milli_sec;
	DT_COND_T	t_cond;
	TIME_T		t_time;
	jint   	ajint_val[2];
	
	t_time = c_dt_get_brdcst_utc(&ui2_milli_sec, &t_cond);

	ajint_val[0] = ui2_milli_sec;
	ajint_val[1] = t_cond;
	JNI_SET_INT_ARRAY_REGION(env, data, 0, 2, ajint_val);

	JNI_LOGD(("{%s %d} t_time=%d, ui2_milli_sec=%d, t_cond=%d.\n", __FUNCTION__, __LINE__, t_time, ui2_milli_sec, t_cond));
	return t_time;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtGetCountCode_native
 * Signature: (I[B[J)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtGetCountCode_1native
  (JNIEnv *env, jclass clazz, jint index, jbyteArray countCode, jlongArray data)
{
	ISO_3166_COUNT_T  t_count_code = {0};
	UINT16		ui2_region_id;
	TIME_T		t_tz_offset;
	INT32 		i4_ret, i;
	jbyte		ajbyte_val[4];
	jlong		ajlong_val[2];

	JNI_LOGD(("{%s %d} Enter.\n", __FUNCTION__, __LINE__));
	
	i4_ret = c_dt_get_count_code(index, &t_count_code, &ui2_region_id, &t_tz_offset);
	if (i4_ret != DTR_OK)
	{
		JNI_LOGE(("{%s %d} c_dt_get_count_code fail. %d\n", __FUNCTION__, __LINE__, i4_ret));
		return -1;
	}

	JNI_LOGD(("{%s %d} c_dt_get_count_code ok.\n", __FUNCTION__, __LINE__));
	for(i = 0; i < 4; i++)
	{
		if (i != 3)
		{
			JNI_LOGD(("{%s %d}t_count_code[%d] = %c"LOG_TAIL, __FUNCTION__, __LINE__, i, t_count_code[i]));
		}
	    ajbyte_val[i] = (UINT8)t_count_code[i];
	}
	JNI_LOGD(("{%s %d} Copy data finish.\n", __FUNCTION__, __LINE__));
	JNI_SET_BYTE_ARRAY_REGION(env, countCode, 0, 4, ajbyte_val);

	JNI_LOGD(("{%s %d} Copy long data.\n", __FUNCTION__, __LINE__));
	ajlong_val[0] = ui2_region_id;
	ajlong_val[1] = t_tz_offset;
	JNI_SET_LONG_ARRAY_REGION(env, data, 0, 2, ajlong_val);

	JNI_LOGD(("{%s %d} ui2_region_id=%d, t_tz_offset=%d.\n", __FUNCTION__, __LINE__, ui2_region_id, t_tz_offset));
	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtGetDstCtrl_native
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_mediatek_tv_service_TVNative_dtGetDstCtrl_1native
  (JNIEnv *env, jclass clazz)
{
	JNI_LOGD(("{%s %d} Enter.\n", __FUNCTION__, __LINE__));
	return c_dt_get_dst_ctrl();
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtGetDsChange_native
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_mediatek_tv_service_TVNative_dtGetDsChange_1native
  (JNIEnv *env, jclass clazz)
{
	JNI_LOGD(("{%s %d} Enter.\n", __FUNCTION__, __LINE__));
	return c_dt_get_ds_change();
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtGetDsOffset_native
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_mediatek_tv_service_TVNative_dtGetDsOffset_1native
  (JNIEnv *env, jclass clazz)
{ 
	JNI_LOGD(("{%s %d} Enter.\n", __FUNCTION__, __LINE__));
	return c_dt_get_ds_offset();
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtGetTzCtrl_native
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_mediatek_tv_service_TVNative_dtGetTzCtrl_1native
  (JNIEnv *env, jclass clazz)
{
	JNI_LOGD(("{%s %d} Enter.\n", __FUNCTION__, __LINE__));
	return c_dt_get_tz_ctrl();
}

	
/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtGetNumCountCode_native
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtGetNumCountCode_1native
  (JNIEnv *env, jclass clazz)
{
	JNI_LOGD(("{%s %d} Enter.\n", __FUNCTION__, __LINE__));
	return c_dt_get_num_count_code();
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtGetSysCountCode_native
 * Signature: ([B[I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtGetSysCountCode_1native
  (JNIEnv *env, jclass clazz, jbyteArray countCode, jintArray data)
{
	ISO_3166_COUNT_T 	t_count_code;
	UINT16 				ui2_region_id;
	INT32				i4_ret, i;
	jbyte				ajbyte_val[4];
	jint				ajint_val[1];
	
	i4_ret = c_dt_get_sys_count_code(&t_count_code,&ui2_region_id);
	if (i4_ret != DTR_OK)
	{
		JNI_LOGE(("{%s %d}c_dt_get_sys_count_code fail. %d.\n ", __FUNCTION__, __LINE__, i4_ret));
		return -1;
	}

	for(i = 0; i < 4; i++)
	{
		if (i != 3)
		{
			JNI_LOGD(("{%s %d}t_count_code[%d] = %c"LOG_TAIL, __FUNCTION__, __LINE__, i, t_count_code[i]));
		}
	    ajbyte_val[i] = (UINT8)t_count_code[i];
	}
	JNI_SET_BYTE_ARRAY_REGION(env, countCode, 0, 4, ajbyte_val);

	ajint_val[0] = ui2_region_id;
	JNI_SET_INT_ARRAY_REGION(env, data, 0, 1, ajint_val);

	JNI_LOGD(("{%s %d} ui2_region_id=%d.\n", __FUNCTION__, __LINE__, ui2_region_id));
	return 0;
}

	
/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtGetLastSyncTblId_native
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_com_mediatek_tv_service_TVNative_dtGetLastSyncTblId_1native
  (JNIEnv *env, jclass clazz)
{
	JNI_LOGD(("{%s %d} Enter.\n", __FUNCTION__, __LINE__));
	return c_dt_get_last_sync_tbl_id();
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtCheckInputTime_native
 * Signature: (Z)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtCheckInputTime_1native
  (JNIEnv *env, jclass clazz, jboolean setVal)
{
	JNI_LOGD(("{%s %d} Enter.\n", __FUNCTION__, __LINE__));
	return c_dt_check_input_time(setVal);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    dtConfigCheckInputTime_native
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_dtConfigCheckInputTime_1native
  (JNIEnv *env, jclass clazz, jint setType, jint setVal)
{
	JNI_LOGD(("{%s %d} setType=%d, setVal=%d .\n", __FUNCTION__, __LINE__, setType, setVal));
	return c_dt_config_check_input_time(setType, &setVal);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    DtUtcSecToDtg_native
 * Signature: (ILcom/mediatek/tv/model/DtDTG;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_DtUtcSecToDtg_1native
  (JNIEnv *env, jclass clazz, jlong utcTime, jobject dtgObj)
{
	INT32 i4_ret;
	DTG_T t_dtg;

	JNI_LOGD(("{%s %d} DtUtcSecToDtg Enter.\n", __FUNCTION__, __LINE__));

	i4_ret = c_dt_utc_sec_to_dtg(utcTime, &t_dtg);
	if (i4_ret != DTR_OK)
	{
		JNI_LOGE(("{%s %d} x_dt_utc_sec_to_dtg error. %d\n", __FUNCTION__, __LINE__, i4_ret));
		return -1;
	}

	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setYear), 	(jint)t_dtg.ui2_yr);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setMonth), 	(jint)t_dtg.ui1_mo);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setDay), 	(jint)t_dtg.ui1_day);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setDow), 	(jint)t_dtg.ui1_dow);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setHour), 	(jint)t_dtg.ui1_hr);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setMin), 	(jint)t_dtg.ui1_min);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setSec), 	(jint)t_dtg.ui1_sec);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setGmt), 	(jboolean)t_dtg.b_gmt);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setDst), 	(jboolean)t_dtg.b_dst);

	JNI_LOGD(("{%s %d} DtUtcSecToDtg Ok.\n", __FUNCTION__, __LINE__));

	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    DtUtcSecToLocDtg_native
 * Signature: (ILcom/mediatek/tv/model/DtDTG;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_DtUtcSecToLocDtg_1native
  (JNIEnv *env, jclass clazz, jlong utcTime, jobject dtgObj)
{
	INT32 i4_ret;
	DTG_T t_dtg;

	JNI_LOGD(("{%s %d} DtUtcSecToLocDtg Enter.\n", __FUNCTION__, __LINE__));
	
	i4_ret = c_dt_utc_sec_to_loc_dtg(utcTime, &t_dtg);
	if (i4_ret != DTR_OK)
	{
		JNI_LOGE(("{%s %d} x_dt_utc_sec_to_loc_dtg error. %d\n", __FUNCTION__, __LINE__, i4_ret));
		return -1;
	}

	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setYear), 	(jint)t_dtg.ui2_yr);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setMonth), 	(jint)t_dtg.ui1_mo);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setDay), 	(jint)t_dtg.ui1_day);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setDow), 	(jint)t_dtg.ui1_dow);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setHour), 	(jint)t_dtg.ui1_hr);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setMin), 	(jint)t_dtg.ui1_min);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setSec), 	(jint)t_dtg.ui1_sec);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setGmt), 	(jboolean)t_dtg.b_gmt);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setDst), 	(jboolean)t_dtg.b_dst);

	JNI_LOGD(("{%s %d} DtUtcSecToLocDtg Ok.\n", __FUNCTION__, __LINE__));
	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    DtConvUtcLocal_native
 * Signature: (Lcom/mediatek/tv/model/DtDTG;Lcom/mediatek/tv/model/DtDTG;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_DtConvUtcLocal_1native
  (JNIEnv *env, jclass clazz, jobject dtgIn, jobject dtgOut)
{
	INT32	i4_ret;
	DTG_T t_input_dtg;
	DTG_T t_output_dtg;

	JNI_LOGD(("{%s %d} DtConvUtcLocal Enter.\n", __FUNCTION__, __LINE__));
	t_input_dtg.ui2_yr 		= JNI_CALL_OBJECT_METHOD(env,dtgOut,Int,CLASS_METHOD_ID(dt_dtg_def,getYear));
	t_input_dtg.ui1_mo 		= JNI_CALL_OBJECT_METHOD(env,dtgOut,Int,CLASS_METHOD_ID(dt_dtg_def,getMonth));
	t_input_dtg.ui1_day 	= JNI_CALL_OBJECT_METHOD(env,dtgOut,Int,CLASS_METHOD_ID(dt_dtg_def,getDay));
	t_input_dtg.ui1_dow 	= JNI_CALL_OBJECT_METHOD(env,dtgOut,Int,CLASS_METHOD_ID(dt_dtg_def,getDow));
	t_input_dtg.ui1_hr 		= JNI_CALL_OBJECT_METHOD(env,dtgOut,Int,CLASS_METHOD_ID(dt_dtg_def,getHour));
	t_input_dtg.ui1_min 	= JNI_CALL_OBJECT_METHOD(env,dtgOut,Int,CLASS_METHOD_ID(dt_dtg_def,getMin));
	t_input_dtg.ui1_sec 	= JNI_CALL_OBJECT_METHOD(env,dtgOut,Int,CLASS_METHOD_ID(dt_dtg_def,getSec));
	t_input_dtg.b_gmt 		= JNI_CALL_OBJECT_METHOD(env,dtgOut,Boolean,CLASS_METHOD_ID(dt_dtg_def,getGmt));
	t_input_dtg.b_dst 		= JNI_CALL_OBJECT_METHOD(env,dtgOut,Boolean,CLASS_METHOD_ID(dt_dtg_def,getDst));
	
	i4_ret = c_dt_conv_utc_local(&t_input_dtg, &t_output_dtg);
	if (i4_ret != DTR_OK)
	{
		JNI_LOGE(("{%s %d} x_dt_conv_utc_local error. %d\n", __FUNCTION__, __LINE__, i4_ret));
		return -1;
	}

	JNI_CALL_OBJECT_METHODV(env,dtgOut,Void,CLASS_METHOD_ID(dt_dtg_def,setYear), 	(jint)t_output_dtg.ui2_yr);
	JNI_CALL_OBJECT_METHODV(env,dtgOut,Void,CLASS_METHOD_ID(dt_dtg_def,setMonth), 	(jint)t_output_dtg.ui1_mo);
	JNI_CALL_OBJECT_METHODV(env,dtgOut,Void,CLASS_METHOD_ID(dt_dtg_def,setDay), 	(jint)t_output_dtg.ui1_day);
	JNI_CALL_OBJECT_METHODV(env,dtgOut,Void,CLASS_METHOD_ID(dt_dtg_def,setDow), 	(jint)t_output_dtg.ui1_dow);
	JNI_CALL_OBJECT_METHODV(env,dtgOut,Void,CLASS_METHOD_ID(dt_dtg_def,setHour), 	(jint)t_output_dtg.ui1_hr);
	JNI_CALL_OBJECT_METHODV(env,dtgOut,Void,CLASS_METHOD_ID(dt_dtg_def,setMin), 	(jint)t_output_dtg.ui1_min);
	JNI_CALL_OBJECT_METHODV(env,dtgOut,Void,CLASS_METHOD_ID(dt_dtg_def,setSec), 	(jint)t_output_dtg.ui1_sec);
	JNI_CALL_OBJECT_METHODV(env,dtgOut,Void,CLASS_METHOD_ID(dt_dtg_def,setGmt), 	(jboolean)t_output_dtg.b_gmt);
	JNI_CALL_OBJECT_METHODV(env,dtgOut,Void,CLASS_METHOD_ID(dt_dtg_def,setDst), 	(jboolean)t_output_dtg.b_dst);

	JNI_LOGD(("{%s %d} DtConvUtcLocal Ok.\n", __FUNCTION__, __LINE__));
	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    DtDtgToSec_native
 * Signature: (Lcom/mediatek/tv/model/DtDTG;)I
 */
JNIEXPORT jlong JNICALL Java_com_mediatek_tv_service_TVNative_DtDtgToSec_1native
  (JNIEnv *env, jclass clazz, jobject dtgObj)
{
	DTG_T t_dtg;

	JNI_LOGD(("{%s %d} DtDtgToSec Enter.\n", __FUNCTION__, __LINE__));
	t_dtg.ui2_yr 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getYear));
	t_dtg.ui1_mo 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getMonth));
	t_dtg.ui1_day 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getDay));
	t_dtg.ui1_dow 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getDow));
	t_dtg.ui1_hr 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getHour));
	t_dtg.ui1_min 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getMin));
	t_dtg.ui1_sec 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getSec));
	JNI_LOGD(("{%s %d} c_dt_dtg_to_sec start11.\n", __FUNCTION__, __LINE__));
	t_dtg.b_gmt 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Boolean,CLASS_METHOD_ID(dt_dtg_def,getGmt));
	t_dtg.b_dst 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Boolean,CLASS_METHOD_ID(dt_dtg_def,getDst));

	JNI_LOGD(("{%s %d} c_dt_dtg_to_sec start.\n", __FUNCTION__, __LINE__));
	
	return c_dt_dtg_to_sec(&t_dtg);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    DtGpsSecToUtcSec_native
 * Signature: (I)I
 */
JNIEXPORT jlong JNICALL Java_com_mediatek_tv_service_TVNative_DtGpsSecToUtcSec_1native
  (JNIEnv *env, jclass clazz, jlong gpsSec)
{
	JNI_LOGD(("{%s %d} DtGpsSecToUtcSec Enter.\n", __FUNCTION__, __LINE__));
	return c_dt_gps_sec_to_utc_sec(gpsSec);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    DtBcdToSec_native
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_DtBcdToSec_1native
  (JNIEnv *env, jclass clazz, jstring bcdTime)
{
	CHAR*   s_bcd_time     	= NULL;
	TIME_T	t_sec;
	INT32	i4_ret;

	JNI_LOGD(("{%s %d} DtBcdToSec Enter.\n", __FUNCTION__, __LINE__));
	/*get tv config type string*/
    s_bcd_time = JNI_GET_STRING_UTF_CHARS(env, bcdTime);
    if (s_bcd_time == NULL) 
    {
    	JNI_LOGE(("{%s %d} s_bcd_time is NULL"LOG_TAIL,  __FUNCTION__, __LINE__));
        return -1;
    }

	JNI_LOGD(("{%s %d}s_bcd_time is %s.\n",  __FUNCTION__, __LINE__, s_bcd_time));
	
	i4_ret = c_dt_bcd_to_sec(s_bcd_time, &t_sec);
	if (i4_ret != DTR_OK)
	{
		JNI_LOGE(("{%s %d} x_dt_bcd_to_sec error. %d\n", __FUNCTION__, __LINE__, i4_ret));
		return -1;
	}

	JNI_LOGD(("{%s %d} DtBcdToSec Ok.\n", __FUNCTION__, __LINE__));
	return t_sec;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    DtMjdBcdToDtg_native
 * Signature: (Ljava/lang/String;Lcom/mediatek/tv/model/DtDTG;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_DtMjdBcdToDtg_1native
  (JNIEnv *env, jclass clazz, jstring MjdBcd, jobject dtgObj)
{
	CHAR*   s_bcd_time     	= NULL;
	DTG_T	t_dtg = {0};
	INT32	i4_ret;

	JNI_LOGD(("{%s %d} DtMjdBcdToDtg Enter.\n", __FUNCTION__, __LINE__));
	/*get tv config type string*/
    s_bcd_time = JNI_GET_STRING_UTF_CHARS(env, MjdBcd);
    if (s_bcd_time == NULL) 
    {
    	JNI_LOGE(("{%s %d} s_bcd_time is NULL"LOG_TAIL,  __FUNCTION__, __LINE__));
        return -1;
    }
	
	i4_ret = c_dt_mjd_bcd_to_dtg(s_bcd_time, &t_dtg);
	if (i4_ret != DTR_OK)
	{
		JNI_LOGE(("{%s %d} x_dt_mjd_bcd_to_dtg error. %d\n", __FUNCTION__, __LINE__, i4_ret));
		return -1;
	}

	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setYear), 	(jint)t_dtg.ui2_yr);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setMonth), 	(jint)t_dtg.ui1_mo);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setDay), 	(jint)t_dtg.ui1_day);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setDow), 	(jint)t_dtg.ui1_dow);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setHour), 	(jint)t_dtg.ui1_hr);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setMin), 	(jint)t_dtg.ui1_min);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setSec), 	(jint)t_dtg.ui1_sec);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setGmt), 	(jboolean)t_dtg.b_gmt);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setDst), 	(jboolean)t_dtg.b_dst);

	JNI_LOGD(("{%s %d} DtMjdBcdToDtg Ok.\n", __FUNCTION__, __LINE__));
	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    DtMjdToDtg_native
 * Signature: (ILcom/mediatek/tv/model/DtDTG;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_DtMjdToDtg_1native
  (JNIEnv *env, jclass clazz, jlong mjd, jobject dtgObj)
{
	DTG_T	t_dtg = {0};
	INT32	i4_ret;
	UINT32	ui4_mid = (UINT32)mjd;

	JNI_LOGD(("{%s %d} DtMjdToDtg Enter.\n", __FUNCTION__, __LINE__));
	
	i4_ret = c_dt_mjd_to_dtg(ui4_mid, &t_dtg);
	if (i4_ret != DTR_OK)
	{
		JNI_LOGE(("{%s %d} x_dt_mjd_to_dtg error. %d\n", __FUNCTION__, __LINE__, i4_ret));
		return -1;
	}
	
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setYear), 	(jint)t_dtg.ui2_yr);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setMonth), 	(jint)t_dtg.ui1_mo);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setDay), 	(jint)t_dtg.ui1_day);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setDow), 	(jint)t_dtg.ui1_dow);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setHour), 	(jint)t_dtg.ui1_hr);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setMin), 	(jint)t_dtg.ui1_min);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setSec), 	(jint)t_dtg.ui1_sec);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setGmt), 	(jboolean)t_dtg.b_gmt);
	JNI_CALL_OBJECT_METHODV(env,dtgObj,Void,CLASS_METHOD_ID(dt_dtg_def,setDst), 	(jboolean)t_dtg.b_dst);

	JNI_LOGD(("{%s %d} DtMjdToDtg Ok.\n", __FUNCTION__, __LINE__));
	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    DtDtgToMjd_native
 * Signature: (Lcom/mediatek/tv/model/DtDTG;)I
 */
JNIEXPORT jlong JNICALL Java_com_mediatek_tv_service_TVNative_DtDtgToMjd_1native
  (JNIEnv *env, jclass clazz, jobject dtgObj)
{
	DTG_T	t_dtg;
	UINT16	ui2_mjd;
	INT32	i4_ret;

	JNI_LOGD(("{%s %d} DtDtgToMjd Enter.\n", __FUNCTION__, __LINE__));
	
	t_dtg.ui2_yr 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getYear));
	t_dtg.ui1_mo 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getMonth));
	t_dtg.ui1_day 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getDay));
	t_dtg.ui1_dow 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getDow));
	t_dtg.ui1_hr 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getHour));
	t_dtg.ui1_min 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getMin));
	t_dtg.ui1_sec 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getSec));
	t_dtg.b_gmt 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Boolean,CLASS_METHOD_ID(dt_dtg_def,getGmt));
	t_dtg.b_dst 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Boolean,CLASS_METHOD_ID(dt_dtg_def,getDst));
	
	i4_ret = c_dt_dtg_to_mjd(&t_dtg, &ui2_mjd);
	if (i4_ret != DTR_OK)
	{
		JNI_LOGE(("{%s %d} x_dt_dtg_to_mjd error. %d\n", __FUNCTION__, __LINE__, i4_ret));
		return -1;
	}

	JNI_LOGD(("{%s %d} DtDtgToMjd Ok.\n", __FUNCTION__, __LINE__));
	return ui2_mjd;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    DtDtgToMjdBcd_native
 * Signature: (Lcom/mediatek/tv/model/DtDTG;Lcom/mediatek/tv/model/DtInfo;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_DtDtgToMjdBcd_1native
  (JNIEnv *env, jclass clazz, jobject dtgObj, jintArray dtInfo)
{
	DTG_T	t_dtg;
	UINT16	ui2_mjd, ui2_hr_min;
	UINT8	ui1_sec;
	INT32	i4_ret;
	jint	ajint_val[3];

	JNI_LOGD(("{%s %d} DtDtgToMjdBcd Enter.\n", __FUNCTION__, __LINE__));
	
	t_dtg.ui2_yr 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getYear));
	t_dtg.ui1_mo 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getMonth));
	t_dtg.ui1_day 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getDay));
	t_dtg.ui1_dow 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getDow));
	t_dtg.ui1_hr 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getHour));
	t_dtg.ui1_min 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getMin));
	t_dtg.ui1_sec 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Int,CLASS_METHOD_ID(dt_dtg_def,getSec));
	t_dtg.b_gmt 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Boolean,CLASS_METHOD_ID(dt_dtg_def,getGmt));
	t_dtg.b_dst 		= JNI_CALL_OBJECT_METHOD(env,dtgObj,Boolean,CLASS_METHOD_ID(dt_dtg_def,getDst));

	i4_ret = c_dt_dtg_to_mjd_bcd(&t_dtg, &ui2_mjd, &ui2_hr_min, &ui1_sec);
	if (i4_ret != DTR_OK)
	{
		JNI_LOGE(("{%s %d} x_dt_dtg_to_mjd_bcd fail. %d\n", __FUNCTION__, __LINE__, i4_ret));
		return -1;
	}

	ajint_val[0] = ui2_mjd;
	ajint_val[1] = ui2_hr_min;
	ajint_val[2] = ui1_sec;

	JNI_SET_INT_ARRAY_REGION(env, dtInfo, 0, 3, ajint_val);

	JNI_LOGD(("{%s %d} DtDtgToMjdBcd Ok.\n", __FUNCTION__, __LINE__));
	return 0;

}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    DtDiff_native
 * Signature: (Lcom/mediatek/tv/model/DtDTG;Lcom/mediatek/tv/model/DtDTG;)I
 */
JNIEXPORT jlong JNICALL Java_com_mediatek_tv_service_TVNative_DtDiff_1native
  (JNIEnv *env, jclass clazz, jobject dtgFrom, jobject dtgTo)
{
	DTG_T	t_from, t_to;

	JNI_LOGD(("{%s %d} DtDiff Enter.\n", __FUNCTION__, __LINE__));

	t_from.ui2_yr 		= JNI_CALL_OBJECT_METHOD(env,dtgFrom,Int,CLASS_METHOD_ID(dt_dtg_def,getYear));
	t_from.ui1_mo 		= JNI_CALL_OBJECT_METHOD(env,dtgFrom,Int,CLASS_METHOD_ID(dt_dtg_def,getMonth));
	t_from.ui1_day 		= JNI_CALL_OBJECT_METHOD(env,dtgFrom,Int,CLASS_METHOD_ID(dt_dtg_def,getDay));
	t_from.ui1_dow 		= JNI_CALL_OBJECT_METHOD(env,dtgFrom,Int,CLASS_METHOD_ID(dt_dtg_def,getDow));
	t_from.ui1_hr 		= JNI_CALL_OBJECT_METHOD(env,dtgFrom,Int,CLASS_METHOD_ID(dt_dtg_def,getHour));
	t_from.ui1_min 		= JNI_CALL_OBJECT_METHOD(env,dtgFrom,Int,CLASS_METHOD_ID(dt_dtg_def,getMin));
	t_from.ui1_sec 		= JNI_CALL_OBJECT_METHOD(env,dtgFrom,Int,CLASS_METHOD_ID(dt_dtg_def,getSec));
	t_from.b_gmt 		= JNI_CALL_OBJECT_METHOD(env,dtgFrom,Boolean,CLASS_METHOD_ID(dt_dtg_def,getGmt));
	t_from.b_dst 		= JNI_CALL_OBJECT_METHOD(env,dtgFrom,Boolean,CLASS_METHOD_ID(dt_dtg_def,getDst));

	t_to.ui2_yr 		= JNI_CALL_OBJECT_METHOD(env,dtgTo,Int,CLASS_METHOD_ID(dt_dtg_def,getYear));
	t_to.ui1_mo 		= JNI_CALL_OBJECT_METHOD(env,dtgTo,Int,CLASS_METHOD_ID(dt_dtg_def,getMonth));
	t_to.ui1_day 		= JNI_CALL_OBJECT_METHOD(env,dtgTo,Int,CLASS_METHOD_ID(dt_dtg_def,getDay));
	t_to.ui1_dow 		= JNI_CALL_OBJECT_METHOD(env,dtgTo,Int,CLASS_METHOD_ID(dt_dtg_def,getDow));
	t_to.ui1_hr 		= JNI_CALL_OBJECT_METHOD(env,dtgTo,Int,CLASS_METHOD_ID(dt_dtg_def,getHour));
	t_to.ui1_min 		= JNI_CALL_OBJECT_METHOD(env,dtgTo,Int,CLASS_METHOD_ID(dt_dtg_def,getMin));
	t_to.ui1_sec 		= JNI_CALL_OBJECT_METHOD(env,dtgTo,Int,CLASS_METHOD_ID(dt_dtg_def,getSec));
	t_to.b_gmt 			= JNI_CALL_OBJECT_METHOD(env,dtgTo,Boolean,CLASS_METHOD_ID(dt_dtg_def,getGmt));
	t_to.b_dst 			= JNI_CALL_OBJECT_METHOD(env,dtgTo,Boolean,CLASS_METHOD_ID(dt_dtg_def,getDst));
	
	return c_dt_diff(&t_from, &t_to);
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    DtDdd_native
 * Signature: (Lcom/mediatek/tv/model/DtDTG;ILcom/mediatek/tv/model/DtDTG;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_DtAdd_1native
  (JNIEnv *env, jclass clazz, jobject dtgOld, jlong add, jobject dtgNew)
{
	DTG_T	t_dtg_old, t_dtg_new;
	INT32	i4_ret;

	JNI_LOGD(("{%s %d} DtAdd Enter.\n", __FUNCTION__, __LINE__));
	
	t_dtg_old.ui2_yr 		= JNI_CALL_OBJECT_METHOD(env,dtgOld,Int,CLASS_METHOD_ID(dt_dtg_def,getYear));
	t_dtg_old.ui1_mo 		= JNI_CALL_OBJECT_METHOD(env,dtgOld,Int,CLASS_METHOD_ID(dt_dtg_def,getMonth));
	t_dtg_old.ui1_day 		= JNI_CALL_OBJECT_METHOD(env,dtgOld,Int,CLASS_METHOD_ID(dt_dtg_def,getDay));
	t_dtg_old.ui1_dow 		= JNI_CALL_OBJECT_METHOD(env,dtgOld,Int,CLASS_METHOD_ID(dt_dtg_def,getDow));
	t_dtg_old.ui1_hr 		= JNI_CALL_OBJECT_METHOD(env,dtgOld,Int,CLASS_METHOD_ID(dt_dtg_def,getHour));
	t_dtg_old.ui1_min 		= JNI_CALL_OBJECT_METHOD(env,dtgOld,Int,CLASS_METHOD_ID(dt_dtg_def,getMin));
	t_dtg_old.ui1_sec 		= JNI_CALL_OBJECT_METHOD(env,dtgOld,Int,CLASS_METHOD_ID(dt_dtg_def,getSec));
	t_dtg_old.b_gmt 		= JNI_CALL_OBJECT_METHOD(env,dtgOld,Boolean,CLASS_METHOD_ID(dt_dtg_def,getGmt));
	t_dtg_old.b_dst 		= JNI_CALL_OBJECT_METHOD(env,dtgOld,Boolean,CLASS_METHOD_ID(dt_dtg_def,getDst));
	
	i4_ret = c_dt_add(&t_dtg_old, add, &t_dtg_new);
	if (i4_ret != DTR_OK)
	{
		JNI_LOGE(("{%s %d} x_dt_add error. %d\n", __FUNCTION__, __LINE__, i4_ret));
		return -1;
	}

	JNI_CALL_OBJECT_METHODV(env,dtgNew,Void,CLASS_METHOD_ID(dt_dtg_def,setYear), 	(jint)t_dtg_new.ui2_yr);
	JNI_CALL_OBJECT_METHODV(env,dtgNew,Void,CLASS_METHOD_ID(dt_dtg_def,setMonth), 	(jint)t_dtg_new.ui1_mo);
	JNI_CALL_OBJECT_METHODV(env,dtgNew,Void,CLASS_METHOD_ID(dt_dtg_def,setDay), 	(jint)t_dtg_new.ui1_day);
	JNI_CALL_OBJECT_METHODV(env,dtgNew,Void,CLASS_METHOD_ID(dt_dtg_def,setDow), 	(jint)t_dtg_new.ui1_dow);
	JNI_CALL_OBJECT_METHODV(env,dtgNew,Void,CLASS_METHOD_ID(dt_dtg_def,setHour), 	(jint)t_dtg_new.ui1_hr);
	JNI_CALL_OBJECT_METHODV(env,dtgNew,Void,CLASS_METHOD_ID(dt_dtg_def,setMin), 	(jint)t_dtg_new.ui1_min);
	JNI_CALL_OBJECT_METHODV(env,dtgNew,Void,CLASS_METHOD_ID(dt_dtg_def,setSec), 	(jint)t_dtg_new.ui1_sec);
	JNI_CALL_OBJECT_METHODV(env,dtgNew,Void,CLASS_METHOD_ID(dt_dtg_def,setGmt), 	(jboolean)t_dtg_new.b_gmt);
	JNI_CALL_OBJECT_METHODV(env,dtgNew,Void,CLASS_METHOD_ID(dt_dtg_def,setDst), 	(jboolean)t_dtg_new.b_dst);

	JNI_LOGD(("{%s %d} DtAdd Ok.\n", __FUNCTION__, __LINE__));
	return 0;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    DtIsLeapYear_native
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_mediatek_tv_service_TVNative_DtIsLeapYear_1native
  (JNIEnv *env, jclass clazz, jlong year)
{
	JNI_LOGD(("{%s %d} DtIsLeapYear Enter.\n", __FUNCTION__, __LINE__));
	return c_dt_leap_yr(year);
}

static JNIEnv *g_pt_jni_brdcst_service_env    = NULL;

static VOID _dt_pf_nfy(
	HANDLE_T     h_hdl,
    VOID*        pv_tag,
    DT_COND_T    t_dt_cond,
    TIME_T       t_delta)
{
    jclass jclass_TVCallBack = NULL;
    jint        withoutAttachDetach   = 0; 

    JNI_LOGD(("{%s %d} Enter.\n", __FUNCTION__, __LINE__));
           
    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(g_pt_jni_brdcst_service_env);
    if (!withoutAttachDetach)
    {
				if (BRDCST_JNI_ATTACH_CURRENT_THREAD(g_pt_jni_brdcst_service_env) < 0)
				{
					JNI_LOGE(("{%s %d} attach current thread fail", __FUNCTION__, __LINE__));
					return;
				}
    }
    else 
    {
        JNI_LOGD(("[Warning][%d]{%s}We are already one with VM\n", __LINE__, __func__));
    }

    {
        JNI_METHOD* jni_method_id;
        
        jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(g_pt_jni_brdcst_service_env,"com/mediatek/tv/service/TVCallBack");
        JNI_CALL_STATIC_METHODV(g_pt_jni_brdcst_service_env,jclass_TVCallBack,Void,CMI(TVCallBack_def,TVCallBack_notifyDT), h_hdl, t_dt_cond, t_delta);
    }

    if (!withoutAttachDetach)
    {
        BRDCST_JNI_DETACH_CURRENT_THREAD(g_pt_jni_brdcst_service_env);
    }
}



JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_DtRegNfyFct_1native
  (JNIEnv *env, jclass clazz, jlongArray handle)
{
	INT32 i4_ret;
	INT32 i4_tag = 0x123;
	jlong 	ajlong_val[1] = {0};
	INT32	h_handle;

	JNI_LOGD(("{%s %d} c_dt_reg_nfy_fct start.\n", __FUNCTION__, __LINE__));
	
	i4_ret  = c_dt_reg_nfy_fct(_dt_pf_nfy,(VOID*)&i4_tag, &h_handle);
	JNI_LOGD(("{%s %d} handle=%d.\n",__FUNCTION__, __LINE__, h_handle));
	
	ajlong_val[0] = h_handle;
	JNI_SET_LONG_ARRAY_REGION(env, handle, 0, 1, ajlong_val);
	JNI_LOGD(("{%s %d} c_dt_reg_nfy_fct end. %d\n", __FUNCTION__, __LINE__, i4_ret));
	
	return i4_ret;
}

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_lockDigitalTuner_1native
  (JNIEnv *env, jclass clazz, jint frequency)
{
    INT32 i4_ret;
	JNI_LOGD(("{%s %d} lockDigitalTuner_1native(freq:%d) start.\n", __FUNCTION__, __LINE__, frequency));

    i4_ret = a_brdcst_lock_digital_tuner(frequency);

	JNI_LOGD(("{%s %d} lockDigitalTuner_1native end (Return:%d).\n", __FUNCTION__, __LINE__,i4_ret));

    return i4_ret;
}

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_unlockDigitalTuner_1native
  (JNIEnv *env, jclass clazz, jint magicID)
{
    INT32 i4_ret;
	JNI_LOGD(("{%s %d} unlockDigitalTuner_1native(majicID:%d) start.\n", __FUNCTION__, __LINE__, magicID));

    i4_ret = a_brdcst_unlock_digital_tuner(magicID);
    
	JNI_LOGD(("{%s %d} unlockDigitalTuner_1native end (Return:%d).\n", __FUNCTION__, __LINE__,i4_ret));

    return i4_ret;
}

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getCurrentDTVAudioCodec_1native
  (JNIEnv *env, jclass clazz)
{
    INT32 i4_ret;
	JNI_LOGD(("{%s %d} getCurrentDTVAudioCodec_1native start.\n", __FUNCTION__, __LINE__));

    i4_ret = a_brdcst_get_cur_dtv_audio_codec();

	JNI_LOGD(("{%s %d} getCurrentDTVAudioCodec_1native end (Return:%d).\n", __FUNCTION__, __LINE__,i4_ret));

    return i4_ret;
}


JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getCurrentDTVVideoCodec_1native
  (JNIEnv *env, jclass clazz)
{
    INT32 i4_ret;
	JNI_LOGD(("{%s %d} getCurrentDTVVideoCodec_1native start.\n", __FUNCTION__, __LINE__));

    i4_ret = a_brdcst_get_cur_dtv_video_codec();

	JNI_LOGD(("{%s %d} getCurrentDTVVideoCodec_1native end (Return:%ld).\n", __FUNCTION__, __LINE__, i4_ret));

    return i4_ret;
}



EXTERN_C_END
