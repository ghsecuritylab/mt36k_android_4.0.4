/*******************************************************************************
 * LEGAL DISCLAIMER
 *    
 * (Header of MediaTek Software/Firmware Release or Documentation)
 *    
 * BY OPENING OR USING THIS FILE, BUYER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND
 * AGREES THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK 
 * SOFTWARE") RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO
 * BUYER ON AN "AS-IS" BASISONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL 
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR 
 * NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH 
 * RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, 
 * INCORPORATED IN, OR SUPPLIED WITH THE MEDIATEK SOFTWARE, AND BUYER AGREES TO
 * LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 * MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE RELEASES 
 * MADE TO BUYER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR STANDARD OR OPEN
 * FORUM.
 * 
 * BUYER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND CUMULATIVE 
 * LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE, 
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY BUYER TO 
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE. 
 * 
 * THE TRANSACTION CONTEMPLATED HEREUNDER SHALL BE CONSTRUED IN ACCORDANCE WITH
 * THE LAWS OF THE STATE OF CALIFORNIA, USA, EXCLUDING ITS CONFLICT OF LAWS 
 * PRINCIPLES.
 ******************************************************************************/
/*------------------------------------------------------------------------------
 * Copyright (c) 2009, Mediatek Inc.
 * All rights reserved.
 * 
 * Unauthorized use, practice, perform, copy, distribution, reproduction,
 * or disclosure of this information in whole or in part is prohibited.
 *----------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------*/
/*! @file tv_jni_util.h 
 *  $RCSfile: $
 *  $Revision: #3 $
 *  $Date: 2013/06/03 $
 *  $Author: xiangshu.wang $
 *  
 *  @par Description: This header file declares jni util exported APIs.
 */
/*----------------------------------------------------------------------------*/
#ifndef _TV_JNI_UTIL_H_
#define _TV_JNI_UTIL_H_
#include <stdio.h>
#include <jni.h>


#ifndef   NATIVE_DEBUG
#include <utils/Log.h>
#define LOG_TAIL        ""
extern volatile int jni_dbg_level;
#endif

extern JNIEnv *g__env;
extern JavaVM *g__JavaVM;


void* jni_malloc(unsigned int size);
void jni_free(void* pt);

#define JNI_MALLOC(X)      jni_malloc(X)
#define JNI_FREE(X)        jni_free(X)
#define JNI_MEMSET         memset
#define JNI_MEMCPY         memcpy

#ifdef NATIVE_DEBUG
    #ifdef LOGD
         #undef LOGD
         #undef LOGI
         #undef LOGE
    #endif
    #define LOGD    printf("%s[%d]",strndup(__FILE__,10),__LINE__);printf
    #define LOGI    LOGD
    #define LOGE    LOGD

    #define LOG_TAIL        "\r\n"
#endif

#ifdef NATIVE_DEBUG
    #define JNI_LOGD(x)     LOGD("%s[%d]",__FILE__,__LINE__);LOGD x
    #define JNI_LOGE(x)     LOGE("%s[%d]",__FILE__,__LINE__);LOGE x
    #define JNI_LOGI(x)     LOGI("%s[%d]",__FILE__,__LINE__);LOGI x
#else
    #define JNI_DBG_LEVEL_NONE  ((int) 0x00000000)
    #define JNI_DBG_LEVEL_DEBUG ((int) 0x00000001)
    #define JNI_DBG_LEVEL_ERROR ((int) 0x00000002)
    #define JNI_DBG_LEVEL_INFO  ((int) 0x00000004)

    #define JNI_LOGD(x)                           \
    do {                                          \
        if (jni_dbg_level & JNI_DBG_LEVEL_DEBUG) {\
            LOGD("%s[%d]",__FILE__,__LINE__);     \
            LOGD x ;                              \
        }                                         \
    } while(0)
    
    #define JNI_LOGE(x)                           \
    do {                                          \
        if (jni_dbg_level & JNI_DBG_LEVEL_ERROR) {\
            LOGE("%s[%d]",__FILE__,__LINE__);     \
            LOGE x ;                              \
        }                                         \
    } while(0)
    
    #define JNI_LOGI(x)                           \
    do {                                          \
        if (jni_dbg_level & JNI_DBG_LEVEL_INFO) { \
            LOGI("%s[%d]",__FILE__,__LINE__);     \
            LOGI x ;                              \
        }                                         \
    } while(0)
#endif


#ifdef __cplusplus
#define JNI_GET_CLASS_BY_NAME(ENV,NAME)                                ((ENV)->FindClass(NAME))
#define JNI_GET_OBJECT_CLASS(ENV,OBJECT)                               ((ENV)->GetObjectClass(OBJECT))
#define JNI_GET_CLASS_METHOD(ENV,CLASS,NAME,SIG)                       ((ENV)->GetMethodID(CLASS,NAME,SIG))
#define JNI_GET_CLASS_STATIC_METHOD(ENV,CLASS,NAME,SIG)                ((ENV)->GetStaticMethodID(CLASS,NAME,SIG))
#define JNI_IS_INSTANCE_OF(ENV,OBJECT,CLASS)                           ((ENV)->IsInstanceOf(OBJECT,CLASS))
#define JNI_GET_STRING_UTF_CHARS(ENV,OBJECT)                           ((ENV)->GetStringUTFChars(OBJECT,NULL))
#define JNI_RELEASE_STRING_UTF_CHARS(ENV,OBJECT,C_STR)                 ((ENV)->ReleaseStringUTFChars(OBJECT,C_STR))
#define JNI_CALL_OBJECT_METHOD(ENV,OBJECT,TYPE,METHOD)                 ((ENV)->Call##TYPE##Method(OBJECT,METHOD))
#define JNI_CALL_OBJECT_METHODV(ENV,OBJECT,TYPE,METHOD,args...)        ((ENV)->Call##TYPE##Method(OBJECT,METHOD,args))
#define JNI_CALL_STATIC_METHOD(ENV,CLASS,TYPE,METHOD)                  ((ENV)->CallStatic##TYPE##Method(CLASS,METHOD))
#define JNI_CALL_STATIC_METHODV(ENV,CLASS,TYPE,METHOD,args...)         ((ENV)->CallStatic##TYPE##Method(CLASS,METHOD,args))
#define JNI_NEW_STRING_UTF(ENV, CHARS)                                 ((ENV)->NewStringUTF(ENV,CHARS))
#define JNI_NEW_OBJECT(ENV,CLASS,METHOD)                               ((ENV)->NewObject(CLASS,METHOD))
#define JNI_NEW_OBJECTV(ENV,CLASS,METHOD,args...) 
#define JNI_GET_ARRAY_LEN(ENV,JARRAY)                                  ((ENV)->GetArrayLength(ENV,JARRAY))
#define JNI_GET_ARRAY_ELEMENT(ENV,JARRAY,IDX)                          ((ENV)->GetObjectArrayElement(JARRAY,IDX))


#define JNI_GET_BYTE_ARRAY_REGION(ENV,START,LEN,BUF)                   ((ENV)->GetByteArrayRegion(START,LEN,BUF))
#define JNI_NEW_BYTE_ARRAY(ENV,LEN)                                    ((ENV)->NewByteArray(LEN))
#define JNI_SET_BYTE_ARRAY_REGION(ENV,JBYTEARRAY,START,LEN,BUF)        ((ENV)->SetByteArrayRegion(JBYTEARRAY,START,LEN,BUF))

#define JNI_GET_INT_ARRAY_REGION(ENV,START,LEN,BUF)                    ((ENV)->GetIntArrayRegion(START,LEN,BUF))
#define JNI_NEW_INT_ARRAY(ENV,LEN)                                     ((ENV)->NewIntArray(LEN))
#define JNI_SET_INT_ARRAY_REGION(ENV,JINTARRAY,START,LEN,BUF)          ((ENV)->SetIntArrayRegion(JINTARRAY,START,LEN,BUF))

#define JNI_GET_LONG_ARRAY_REGION(ENV,START,LEN,BUF)                   ((ENV)->GetLongArrayRegion(START,LEN,BUF))
#define JNI_NEW_LONG_ARRAY(ENV,LEN)                                    ((ENV)->NewLongArray(LEN))
#define JNI_SET_LONG_ARRAY_REGION(ENV,JINTARRAY,START,LEN,BUF)         ((ENV)->SetLongArrayRegion(JINTARRAY,START,LEN,BUF))


#define JNI_DEL_LOCAL_REF(ENV,LOCAL)                                   ((ENV)->DeleteLocalRef(LOCAL))
#define JNI_ATTACH_CURRENT_THREAD(ENV)                                 ((g__JavaVM)->AttachCurrentThread(&ENV, NULL))
#define JNI_DETACH_CURRENT_THREAD(ENV)                                 ((g__JavaVM)->DetachCurrentThread())
#define JNI_ALREADY_ONE_WITH_VM(ENV)                                   ((g__JavaVM)->AlreadyOneWithVM(&ENV))
#else

#define JNI_GET_CLASS_BY_NAME(ENV,NAME)                                ((*ENV)->FindClass(ENV,NAME))
#define JNI_GET_OBJECT_CLASS(ENV,OBJECT)                               ((*ENV)->GetObjectClass(ENV,OBJECT))
#define JNI_GET_CLASS_METHOD(ENV,CLASS,NAME,SIG)                       ((*ENV)->GetMethodID(ENV,CLASS,NAME,SIG))
#define JNI_GET_CLASS_STATIC_METHOD(ENV,CLASS,NAME,SIG)                ((*ENV)->GetStaticMethodID(ENV,CLASS,NAME,SIG))
#define JNI_IS_INSTANCE_OF(ENV,OBJECT,CLASS)                           ((*ENV)->IsInstanceOf(ENV,OBJECT,CLASS))
#define JNI_GET_STRING_UTF_CHARS(ENV,OBJECT)                           ((*ENV)->GetStringUTFChars(ENV,OBJECT,NULL))
#define JNI_RELEASE_STRING_UTF_CHARS(ENV,OBJECT,C_STR)                 ((*ENV)->ReleaseStringUTFChars(ENV,OBJECT,C_STR))
/*
 * TYPE:
 * Boolean Byte Char Short Int Long Float Double Void Object
 */
#define JNI_CALL_OBJECT_METHOD(ENV,OBJECT,TYPE,METHOD)                 ((*ENV)->Call##TYPE##Method(ENV,OBJECT,METHOD))
#define JNI_CALL_OBJECT_METHODV(ENV,OBJECT,TYPE,METHOD,args...)        ((*ENV)->Call##TYPE##Method(ENV,OBJECT,METHOD,args))
#define JNI_CALL_STATIC_METHOD(ENV,CLASS,TYPE,METHOD)                  ((*ENV)->CallStatic##TYPE##Method(ENV,CLASS,METHOD))
#define JNI_CALL_STATIC_METHODV(ENV,CLASS,TYPE,METHOD,args...)         ((*ENV)->CallStatic##TYPE##Method(ENV,CLASS,METHOD,args))
#define JNI_NEW_STRING_UTF(ENV, CHARS)                                 ((*ENV)->NewStringUTF(ENV,CHARS))

#define JNI_NEW_OBJECT(ENV,CLASS,METHOD)                               ((*ENV)->NewObject(ENV,CLASS,METHOD) )
#define JNI_NEW_OBJECTV(ENV,CLASS,METHOD,args...)                      ((*ENV)->NewObject(ENV,CLASS,METHOD,args) )
#define JNI_GET_ARRAY_LEN(ENV,JARRAY)                                  ((*ENV)->GetArrayLength(ENV,JARRAY) )
#define JNI_GET_ARRAY_ELEMENT(ENV,JARRAY,IDX)                          ((*ENV)->GetObjectArrayElement(ENV,JARRAY,IDX))


#define JNI_GET_BYTE_ARRAY_REGION(ENV,BYTEARRAY,START,LEN,BUF)         ((*ENV)->GetByteArrayRegion(ENV,BYTEARRAY,START,LEN,BUF))
#define JNI_NEW_BYTE_ARRAY(ENV,LEN)                                    ((*ENV)->NewByteArray(ENV,LEN))
#define JNI_SET_BYTE_ARRAY_REGION(ENV,JBYTEARRAY,START,LEN,BUF)        ((*ENV)->SetByteArrayRegion(ENV,JBYTEARRAY,START,LEN,BUF))

#define JNI_GET_INT_ARRAY_REGION(ENV,INTARRAY,START,LEN,BUF)           ((*ENV)->GetIntArrayRegion(ENV,INTARRAY,START,LEN,BUF))
#define JNI_NEW_INT_ARRAY(ENV,LEN)                                     ((*ENV)->NewIntArray(ENV,LEN))
#define JNI_SET_INT_ARRAY_REGION(ENV,JINTARRAY,START,LEN,BUF)          ((*ENV)->SetIntArrayRegion(ENV,JINTARRAY,START,LEN,BUF))

#define JNI_GET_LONG_ARRAY_REGION(ENV,LONGARRAY,START,LEN,BUF)         ((*ENV)->GetLongArrayRegion(ENV,LONGARRAY,START,LEN,BUF))
#define JNI_NEW_LONG_ARRAY(ENV,LEN)                                    ((*ENV)->NewLongArray(ENV,LEN))
#define JNI_SET_LONG_ARRAY_REGION(ENV,JLONGARRAY,START,LEN,BUF)        ((*ENV)->SetLongArrayRegion(ENV,JLONGARRAY,START,LEN,BUF))



#define JNI_DEL_LOCAL_REF(ENV,LOCAL)                                   ((*ENV)->DeleteLocalRef(ENV,LOCAL))
#define JNI_ATTACH_CURRENT_THREAD(ENV)                                 ((*g__JavaVM)->AttachCurrentThread(g__JavaVM,&ENV, NULL))
#define JNI_DETACH_CURRENT_THREAD(ENV)                                 ((*g__JavaVM)->DetachCurrentThread(g__JavaVM))
#define JNI_ALREADY_ONE_WITH_VM(ENV)                                   ((*g__JavaVM)->AlreadyOneWithVM(g__JavaVM,&ENV))
#endif



#define CLASS_METHOD_ID(CLASS_DEF,METHOD_NAME)                         (CLASS_DEF.jmethods[METHOD_NAME].methodId)

#define CMI     CLASS_METHOD_ID
#define JCOM    JNI_CALL_OBJECT_METHOD
#define JCOMV   JNI_CALL_OBJECT_METHODV


#ifdef __cplusplus
#define EXTERN_C_START extern "C" {
#define EXTERN_C_END   }
#else
#define EXTERN_C_START
#define EXTERN_C_END
#endif                          /* __CPLUSPLUS */

typedef struct _JNI_METHOD
{
    char*       method_name;
    char*       method_signature;
    unsigned char   static_method;
    jmethodID   methodId;
}JNI_METHOD;

typedef struct _JNI_CLASS_UTIL{
    char*       class_name;
    int         max_method;
    JNI_METHOD  jmethods[100];
}JNI_CLASS_UTIL;

typedef enum {
    List_size = 0,
    List_get,
    List_add,
}CHANNEL_LIST_DEF;


extern JNI_CLASS_UTIL     List;

/*extern JavaVM *gJavaVM;*/


typedef enum {
    AnalogChannelInfo_getTvSys,
    AnalogChannelInfo_setTvSys,
    AnalogChannelInfo_getAudioSys,
    AnalogChannelInfo_setAudioSys,
    AnalogChannelInfo_getColorSys,
    AnalogChannelInfo_setColorSys,
    AnalogChannelInfo_getFrequency,
    AnalogChannelInfo_setFrequency,
    AnalogChannelInfo_getServiceName,
    AnalogChannelInfo_setServiceName,
    AnalogChannelInfo_getSvlId,       
    AnalogChannelInfo_setSvlId,       
    AnalogChannelInfo_getSvlRecId,    
    AnalogChannelInfo_setSvlRecId,    
    AnalogChannelInfo_getChannelId,   
    AnalogChannelInfo_setChannelId,   
    AnalogChannelInfo_getBrdcstType,   
    AnalogChannelInfo_setBrdcstType,   
    AnalogChannelInfo_getNwMask,   
    AnalogChannelInfo_setNwMask,   
    AnalogChannelInfo_getOptionMask,   
    AnalogChannelInfo_setOptionMask,   
    AnalogChannelInfo_getServiceType,   
    AnalogChannelInfo_setServiceType,  
    AnalogChannelInfo_getChannelNumber,   
    AnalogChannelInfo_setChannelNumber,   
    AnalogChannelInfo_getBrdcstMedium,
    AnalogChannelInfo_setBrdcstMedium,
    AnalogChannelInfo_getPrivateData,
    AnalogChannelInfo_setPrivateData,
    AnalogChannelInfo_isNoAutoFineTune,
    AnalogChannelInfo_setNoAutoFineTune,
    AnalogChannelInfo_init,         
}ANALOG_CHANNEL_INFO_DEF;

extern JNI_CLASS_UTIL     AnalogChannelInfo;


typedef enum {
    DvbChannelInfo_getSvlId,       
    DvbChannelInfo_setSvlId,       
    DvbChannelInfo_getSvlRecId,    
    DvbChannelInfo_setSvlRecId,    
    DvbChannelInfo_getChannelId,   
    DvbChannelInfo_setChannelId,   
    DvbChannelInfo_getBrdcstType,   
    DvbChannelInfo_setBrdcstType,   
    DvbChannelInfo_getNwMask,   
    DvbChannelInfo_setNwMask,   
    DvbChannelInfo_getOptionMask,   
    DvbChannelInfo_setOptionMask,   
    DvbChannelInfo_getServiceType,   
    DvbChannelInfo_setServiceType,  
    DvbChannelInfo_getChannelNumber,   
    DvbChannelInfo_setChannelNumber,   
    DvbChannelInfo_getServiceName,
    DvbChannelInfo_setServiceName,
    DvbChannelInfo_getPrivateData,
    DvbChannelInfo_setPrivateData,
    DvbChannelInfo_getShortName,
    DvbChannelInfo_setShortName,
    DvbChannelInfo_getBrdcstMedium,
    DvbChannelInfo_setBrdcstMedium,
    DvbChannelInfo_getFrequency,
    DvbChannelInfo_setFrequency,
    DvbChannelInfo_getBandWidth,
    DvbChannelInfo_setBandWidth,
    DvbChannelInfo_getNwId,     
    DvbChannelInfo_setNwId,     
    DvbChannelInfo_getOnId,     
    DvbChannelInfo_setOnId,     
    DvbChannelInfo_getTsId,     
    DvbChannelInfo_setTsId,     
    DvbChannelInfo_getProgId,   
    DvbChannelInfo_setProgId,   
    DvbChannelInfo_getSymRate,  
    DvbChannelInfo_setSymRate,  
    DvbChannelInfo_getMod,  
    DvbChannelInfo_setMod,  
    DvbChannelInfo_init,         
}DVB_CHANNEL_INFO_DEF;

extern JNI_CLASS_UTIL     DvbChannelInfo;

typedef enum {
    getChannelAudioLanguage = 0,
	getAudioMts,
	getAudioLangIndex
}EXTRA_CHANNEL_INFO_DEF;

extern JNI_CLASS_UTIL extra_channel_info_def;


typedef enum {
    channel_info_getSvlId = 0,
    channel_info_getSvlRecId,
    channel_info_getChannelId
}CHANNEL_INFO_DEF;


extern JNI_CLASS_UTIL brdcst_cb_def;
    
typedef enum {
    broadcast_service_nfySvctxMsgCB = 0,
}BRDCST_SVC_CB_DEF;

extern JNI_CLASS_UTIL     channel_info_def;

typedef enum {
	HostControlTune_setTunedChannel=0,
    HostControlTune_getNetworkId ,
    HostControlTune_getOrigNetworkId,
    HostControlTune_getTSId,  
    HostControlTune_getSvcId
}HOST_CONTROL_TUNE_DEF;


extern JNI_CLASS_UTIL HostControlTune;

typedef enum {
    getIntValue = 0,
    setIntValue,
    getIntArrayValue,
    setIntArrayValue,
    getByteArrayValue,
    setByteArrayValue,
    isBoolVal,
    setBoolValue,
    setMinValue,
    setMaxValue,
    getGpioID,
    setGpioID,
    getGpioMask,
    setGpioMask,
    getGpioValue,
    setGpioValue
}CFG_VAL_DEF;

extern JNI_CLASS_UTIL     cfg_val_def;


typedef enum {
    getVideoWidth = 0,
    setVideoWidth,
    getVideoHeight,
    setVideoHeight,
    getVideoFrameRate,
    setVideoFrameRate,
    getProgressive,
    setProgressive,
    getVideoFormat,
    setVideoFormat
}VIDEO_RESOLUTION_DEF;

extern JNI_CLASS_UTIL     video_resolution_def;

typedef enum {
    getAlternativeAudio = 0,
    setAlternativeAudio,
    getMts,
    setMts
}AUDIO_INFO_DEF;

extern JNI_CLASS_UTIL     audio_info_def;

typedef enum {
    getSignalLevel = 0,
    setSignalLevel,
    getBer,
    setBer
}SIGNALLEVEL_INFO_DEF;

extern JNI_CLASS_UTIL     signalLevel_info_def;

typedef enum {
    getTotalNumber = 0,
    setTotalNumber,
    getAudioLanguage,
    setAudioLanguage,
    getCurrentLanguage,
    setCurrentLanguage,
    getDigitalMts,
    setDigitalMts,
    getCurrentAudioLangIndex,
    setCurrentAudioLangIndex
}AUDIO_LANGUAGE_INFO_DEF;

extern JNI_CLASS_UTIL     audio_language_info_def;

typedef enum { 
    getSubtitleLang,
    setSubtitleLang,
    getCurrentSubtitleLang,
    setCurrentSubtitleLang
}SUBTITLE_INFO_DEF;

extern JNI_CLASS_UTIL     subtitle_info_def;

typedef enum { 
    getRadioNumber,
    setRadioNumber,
    getTvNumber,
    setTvNumber,
    getAppNumber,
    setAppNumber
}SCAN_PROGRAM_TYPE_DEF;

extern JNI_CLASS_UTIL     scan_program_type_def;

typedef enum { 
    getLowerTunerFreqBound,
    setLowerTunerFreqBound,
    getUpperTunerFreqBound,
    setUpperTunerFreqBound
}DVBC_FREQ_RANGE_DEF;

extern JNI_CLASS_UTIL     dvbc_freq_range_def;

typedef enum { 
    getMainFrequence,
    setMainFrequence,
    getTsCount,
    setTsCount,
    getNitVersion,
    setNitVersion
}MAIN_FREQUENCE_DEF;

extern JNI_CLASS_UTIL     main_frequence_def;


typedef enum
{
    TVCallBack_notifyScanProgress,  //Signature: (II)I
    TVCallBack_notifyScanFrequence, //Signature: (I)I
    TVCallBack_notifyScanCompleted, //Signature: ()I
    TVCallBack_notifyScanCanceled,  //Signature: ()I
    TVCallBack_notifyScanError,     //Signature: (I)I
    TVCallBack_notifyScanUserOperation,     //Signature: (III)I
    TVCallBack_nfySvctxMsgCB,       //Signature: (Ljava/lang/String;I)V
    TVCallBack_onUARTSerialListener,//Signature: (III[B)V
    TVCallBack_onOperationDone,     //(IZ)V
    TVCallBack_onSourceDetected,    //(I)V
    TVCallBack_onOutputSignalStatus,  //(II)V
    TVCallBack_notifyDT,
    TVCallBack_camStatusUpdated,
    TVCallBack_camMMIMenuReceived,
    TVCallBack_camMMIEnqReceived,
    TVCallBack_camMMIClosed,
    TVCallBack_camHostControlTune,
    TVCallBack_camHostControlReplace,
    TVCallBack_camHostControlClearReplace,
    TVCallBack_camSystemIDStatusUpdated,
    TVCallBack_camSystemIDInfoUpdated,
    TVCallBack_eventServiceNotifyUpdate,
    TVCallBack_channelServiceNotifyUpdate,
    TVCallBack_configServiceNotifyDbgLevel,
    TVCallBack_compServiceNotifyInfo //
}TVCallBack_DEF;

extern JNI_CLASS_UTIL     TVCallBack_def;


typedef enum {
	getYear = 0,
	setYear,
	getMonth,
	setMonth,
	getDay,
	setDay,
	getDow,
	setDow,
	getHour,
	setHour,
	getMin,
	setMin,
	getSec,
	setSec,
	getGmt,
	setGmt,
	getDst,
	setDst
}DT_DTG_DEF;
extern JNI_CLASS_UTIL		dt_dtg_def;

extern JNI_CLASS_UTIL  EventActiveWindow_def;
typedef enum{
    EventActiveWindow_getChannels = 0,
    EventActiveWindow_getStartTime,
    EventActiveWindow_getDuration
}EVENT_ACTIVE_WINDOW_DEF;


extern JNI_CLASS_UTIL  EventCommand_def;
typedef enum {
    EventCommand_isActualOnly = 0,          
    EventCommand_getMaxDay,                 
    EventCommand_getPrefLanuage,            
    EventCommand_getActiveWindow,           
    EventCommand_getEventMinSeconds,        
    EventCommand_isFakeEventInsertionEnable,
    EventCommand_getFakeEventMinSecond,     
    EventCommand_isTimeConfictAllow,        
    EventCommand_isPartialOverapAllow,      
    EventCommand_getEventDetailSeparator,   
    EventCommand_getCurrentChannelInfo,     
    EventCommand_isDoRestart,               
    EventCommand_isDoClean,                 
    EventCommand_isDoEnable,                
    EventCommand_getTunerName,              
    EventCommand_getCommandMask             
}EVENT_COMMAND_DEF;


extern JNI_CLASS_UTIL  EventComponent_def;
typedef enum{
    EventComponent_getStreamContent,
    EventComponent_setStreamContent,
    EventComponent_getComponentType,
    EventComponent_setComponentType,
    EventComponent_getComponentTag,
    EventComponent_setComponentTag,
    EventComponent_init,
    EventComponent_init3
}EVENT_COMPONENT_DEF;


extern JNI_CLASS_UTIL  EventInfo_def;
typedef enum
{
    EventInfo_init                             , 
    EventInfo_getSvlId                         , 
    EventInfo_setSvlId                         , 
    EventInfo_getChannelId                     , 
    EventInfo_setChannelId                     , 
    EventInfo_getEventId                       , 
    EventInfo_setEventId                       , 
    EventInfo_getStartTime                     , 
    EventInfo_setStartTime                     , 
    EventInfo_getDuration                      , 
    EventInfo_setDuration                      , 
    EventInfo_isCaption                        , 
    EventInfo_setCaption                       , 
    EventInfo_isFreeCaMode                     , 
    EventInfo_setFreeCaMode                    , 
    EventInfo_getEventTitle                    , 
    EventInfo_setEventTitle                    , 
    EventInfo_getEventDetail                   , 
    EventInfo_setEventDetail                   , 
    EventInfo_getGuidanceMode                  , 
    EventInfo_setGuidanceMode                  , 
    EventInfo_getGuidanceText                  , 
    EventInfo_setGuidanceText                  , 
    EventInfo_getCaSystemId                    , 
    EventInfo_setCaSystemId                    , 
    EventInfo_getEventCategoryNum              , 
    EventInfo_setEventCategoryNum              , 
    EventInfo_getEventCategory                 , 
    EventInfo_setEventCategory                 , 
    EventInfo_getEventComponents               , 
    EventInfo_setEventComponents               , 
    EventInfo_getEventLinkage                  , 
    EventInfo_setEventLinkage                   
}EVENT_INFO_DEF;

extern JNI_CLASS_UTIL  EventLinkage_def;
typedef enum{
    EventLinkage_init,
    EventLinkage_init3,
    EventLinkage_getOnId,
    EventLinkage_setOnId,
    EventLinkage_getTsId,
    EventLinkage_setTsId,
    EventLinkage_getSvcId,
    EventLinkage_setSvcId
}EVENTLINKAGE_DEF;

typedef enum { 
    getRFChannel = 0,
    setRFChannel,
    getRFScanIndex,
    setRFScanIndex,
    getRFScanFrequency,
    setRFScanFrequency
}DTMB_SCAN_RF_DEF;

extern JNI_CLASS_UTIL     dtmb_freq_range_def;
extern JNI_CLASS_UTIL     dtmb_scanRF_def;


extern void init_jni_single_def(JNIEnv *env,JNI_CLASS_UTIL*     jni_class_util);
extern void init_jni_def(JNIEnv *env);
extern void jstring2buffer(JNIEnv *env,jobject jstr,char* ui1_buffer,unsigned int z_len );

extern void jni_brdcst_svc_init(JNIEnv *env);
extern void x_channel_service_init(void);
extern void x_event_service_init(void);
extern void x_scan_service_init_jni(JNIEnv *env);
extern void x_input_service_init_jni(JNIEnv *env);
extern void x_scan_service_dvbc_init_jni(JNIEnv *env);
extern void x_scan_service_dtmb_init_jni(JNIEnv *env);
extern void x_ci_service_init_jni(JNIEnv *env);
extern void jni_comp_init(JNIEnv *env);

#endif
