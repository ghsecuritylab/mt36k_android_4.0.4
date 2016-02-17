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
#include <memory.h>
#include "tv_jni_util.h"

EXTERN_C_START

#define LOG_TAG "tv_jni_util"
#define TRUE     ((unsigned char)0x1)
#define FALSE    ((unsigned char)0x0)


JNI_CLASS_UTIL     List=
{
    "java/util/List",3,
    {
        {"size",        "()I",                      FALSE,  NULL},
        {"get",         "(I)Ljava/lang/Object;",    FALSE,  NULL},
        {"add",         "(ILjava/lang/Object;)V",   FALSE,  NULL},
        
    }
};


JNI_CLASS_UTIL     AnalogChannelInfo=
{
    "com/mediatek/tv/model/AnalogChannelInfo",33,
    {
        {"getTvSys",         "()I",                  FALSE, NULL},
        {"setTvSys",         "(I)V",                 FALSE, NULL},
        {"getAudioSys",      "()I",                  FALSE, NULL},
        {"setAudioSys",      "(I)V",                 FALSE, NULL},
        {"getColorSys",      "()I",                  FALSE, NULL},
        {"setColorSys",      "(I)V",                 FALSE, NULL},
        {"getFrequency",     "()I",                  FALSE, NULL},
        {"setFrequency",     "(I)V",                 FALSE, NULL},
        {"getServiceName",   "()Ljava/lang/String;", FALSE, NULL},
        {"setServiceName",   "(Ljava/lang/String;)V",FALSE, NULL},
        {"getSvlId",         "()I",                  FALSE, NULL},
        {"setSvlId",         "(I)V",                 FALSE, NULL},
        {"getSvlRecId",      "()I",                  FALSE, NULL},
        {"setSvlRecId",      "(I)V",                 FALSE, NULL},
        {"getChannelId",     "()I",                  FALSE, NULL},
        {"setChannelId",     "(I)V",                 FALSE, NULL},
        {"getBrdcstType",    "()I",                  FALSE, NULL},
        {"setBrdcstType",    "(I)V",                 FALSE, NULL},
        {"getNwMask",        "()I",                  FALSE, NULL},
        {"setNwMask",        "(I)V",                 FALSE, NULL},
        {"getOptionMask",    "()I",                  FALSE, NULL},
        {"setOptionMask",    "(I)V",                 FALSE, NULL},
        {"getServiceType",   "()I",                  FALSE, NULL},
        {"setServiceType",   "(I)V",                 FALSE, NULL},
        {"getChannelNumber", "()I",                  FALSE, NULL},
        {"setChannelNumber", "(I)V",                 FALSE, NULL},
        {"getBrdcstMedium",  "()B",                  FALSE, NULL},
        {"setBrdcstMedium",  "(B)V",                 FALSE, NULL},
        {"getPrivateData",   "()[B",                 FALSE, NULL},
        {"setPrivateData",   "([B)V",                FALSE, NULL},
        {"isNoAutoFineTune", "()Z",                  FALSE, NULL},
        {"setNoAutoFineTune","(Z)V",                 FALSE, NULL},
        {"<init>",           "(II)V",                FALSE, NULL},
    }
};



JNI_CLASS_UTIL     DvbChannelInfo=
{
    "com/mediatek/tv/model/DvbChannelInfo",41,
    {
        {"getSvlId",         "()I",                  FALSE, NULL},
        {"setSvlId",         "(I)V",                 FALSE, NULL},
        {"getSvlRecId",      "()I",                  FALSE, NULL},
        {"setSvlRecId",      "(I)V",                 FALSE, NULL},
        {"getChannelId",     "()I",                  FALSE, NULL},
        {"setChannelId",     "(I)V",                 FALSE, NULL},
        {"getBrdcstType",    "()I",                  FALSE, NULL},
        {"setBrdcstType",    "(I)V",                 FALSE, NULL},
        {"getNwMask",        "()I",                  FALSE, NULL},
        {"setNwMask",        "(I)V",                 FALSE, NULL},
        {"getOptionMask",    "()I",                  FALSE, NULL},
        {"setOptionMask",    "(I)V",                 FALSE, NULL},
        {"getServiceType",   "()I",                  FALSE, NULL},
        {"setServiceType",   "(I)V",                 FALSE, NULL},
        {"getChannelNumber", "()I",                  FALSE, NULL},
        {"setChannelNumber", "(I)V",                 FALSE, NULL},
        {"getServiceName",   "()Ljava/lang/String;", FALSE, NULL},
        {"setServiceName",   "(Ljava/lang/String;)V",FALSE, NULL},
        {"getPrivateData",   "()[B",                 FALSE, NULL},
        {"setPrivateData",   "([B)V",                FALSE, NULL},
        {"getShortName",     "()Ljava/lang/String;", FALSE, NULL},
        {"setShortName",     "(Ljava/lang/String;)V",FALSE, NULL},
        {"getBrdcstMedium",  "()I",                  FALSE, NULL},
        {"setBrdcstMedium",  "(I)V",                 FALSE, NULL},
        {"getFrequency",     "()I",                  FALSE, NULL},
        {"setFrequency",     "(I)V",                 FALSE, NULL},
        {"getBandWidth",     "()I",                  FALSE, NULL},
        {"setBandWidth",     "(I)V",                 FALSE, NULL},
        {"getNwId",          "()I",                  FALSE, NULL},
        {"setNwId",          "(I)V",                 FALSE, NULL},
        {"getOnId",          "()I",                  FALSE, NULL},
        {"setOnId",          "(I)V",                 FALSE, NULL},
        {"getTsId",          "()I",                  FALSE, NULL},
        {"setTsId",          "(I)V",                 FALSE, NULL},
        {"getProgId",        "()I",                  FALSE, NULL},
        {"setProgId",        "(I)V",                 FALSE, NULL},
        {"getSymRate",       "()I",                  FALSE, NULL},
        {"setSymRate",       "(I)V",                 FALSE, NULL},
        {"getMod",           "()I",                  FALSE, NULL},
        {"setMod",           "(I)V",                 FALSE, NULL},
        {"<init>",           "(II)V",                FALSE, NULL},
    }
};



JNI_CLASS_UTIL     channel_info_def=
{
    "com/mediatek/tv/model/ChannelInfo",3,
    {
        {"getSvlId",        "()I",                  FALSE,  NULL},
        {"getSvlRecId",     "()I",                  FALSE,  NULL},
        {"getChannelId",    "()I",                  FALSE,  NULL},
    }
};

JNI_CLASS_UTIL     extra_channel_info_def=
{
    "com/mediatek/tv/model/ExtraChannelInfo",3,
    {
        {"getChannelAudioLanguage",    "()Ljava/lang/String;",  FALSE,    NULL},
		{"getAudioMts",				   "()I", 				    FALSE,    NULL},
		{"getAudioLangIndex", 		   "()I",				FALSE,	  NULL},
    }
};


JNI_CLASS_UTIL     HostControlTune=
{
    "com/mediatek/tv/model/HostControlTune",5,
    {
        
        {"setTunedChannel",   "(Lcom/mediatek/tv/model/ChannelInfo;)V",   FALSE,  NULL},
        {"getNetworkId",            "()I",                  FALSE,  NULL},
        {"getOrigNetworkId",      "()I",                  FALSE,  NULL},
        {"getTSId",                   "()I",                  FALSE,  NULL},
        {"getSvcId",                 "()I",                  FALSE,  NULL},
    }
};



JNI_CLASS_UTIL     cfg_val_def=
{
    "com/mediatek/tv/common/ConfigValue",16,
    {
        {"getIntValue",        "()I",                  FALSE, NULL},            
        {"setIntValue",        "(I)V",                 FALSE, NULL},
        {"getIntArrayValue",   "()[I",                 FALSE, NULL},
        {"setIntArrayValue",   "([I)V",                FALSE, NULL},
        {"getByteArrayValue",  "()[B",                 FALSE, NULL},
        {"setByteArrayValue",  "([B)V",                FALSE, NULL},
        {"isBoolVal",          "()Z",                  FALSE, NULL},
        {"setBoolValue",       "(Z)V",                 FALSE, NULL},
        {"setMinValue",        "(I)V",                 FALSE, NULL},
        {"setMaxValue",        "(I)V",                 FALSE, NULL},
        {"getGpioID",          "()I",                  FALSE, NULL},
        {"setGpioID",          "(I)V",                 FALSE, NULL},
        {"getGpioMask",        "()I",                  FALSE, NULL},
        {"setGpioMask",        "(I)V",                 FALSE, NULL},
        {"getGpioValue",       "()I",                  FALSE, NULL},
        {"setGpioValue",       "(I)V",                 FALSE, NULL},
    }
};


JNI_CLASS_UTIL     video_resolution_def=
{
    "com/mediatek/tv/model/VideoResolution",10,
    {
        {"getVideoWidth",     "()I",                  FALSE,    NULL},
        {"setVideoWidth",     "(I)V",                 FALSE,    NULL},
        {"getVideoHeight",    "()I",                  FALSE,    NULL},
        {"setVideoHeight",    "(I)V",                 FALSE,    NULL},
        {"getVideoFrameRate", "()I",                  FALSE,    NULL},
        {"setVideoFrameRate", "(I)V",                 FALSE,    NULL},
        {"getProgressive",    "()Z",                  FALSE,    NULL},
        {"setProgressive",    "(Z)V",                 FALSE,    NULL},
        {"getVideoFormat",    "()Ljava/lang/String;", FALSE,    NULL},
        {"setVideoFormat",    "(Ljava/lang/String;)V",FALSE,    NULL},
    }
};

JNI_CLASS_UTIL     audio_info_def=
{
    "com/mediatek/tv/model/AudioInfo",4,
    {
        {"getAlternativeAudio", "()I",                  FALSE,    NULL},
        {"setAlternativeAudio", "(I)V",                 FALSE,    NULL},
		{"getMts", "()I",								FALSE,	  NULL},
		{"setMts", "(I)V",								FALSE,	  NULL},

    }
};


JNI_CLASS_UTIL     signalLevel_info_def=
{
    "com/mediatek/tv/model/SignalLevelInfo",4,
    {
        {"getSignalLevel", "()I",                  FALSE,    NULL},
        {"setSignalLevel", "(I)V",                 FALSE,    NULL},
        {"getBer",         "()I",                  FALSE,    NULL},
        {"setBer",         "(I)V",                 FALSE,    NULL},
    }
};

JNI_CLASS_UTIL     audio_language_info_def=
{
    "com/mediatek/tv/model/AudioLanguageInfo",10,
    {
        {"getTotalNumber",      "()I",                  FALSE,    NULL},
        {"setTotalNumber",      "(I)V",                 FALSE,    NULL},
        {"getAudioLanguage",    "()Ljava/lang/String;",  FALSE,    NULL},
        {"setAudioLanguage",    "(Ljava/lang/String;)V", FALSE,    NULL},
        {"getCurrentLanguage",    "()Ljava/lang/String;",  FALSE,    NULL},
        {"setCurrentLanguage",    "(Ljava/lang/String;)V", FALSE,    NULL},
        {"getDigitalMts",         "()I",                  FALSE,    NULL},
        {"setDigitalMts",         "(I)V",                 FALSE,    NULL},
        {"getCurrentAudioLangIndex",         "()I",                  FALSE,    NULL},
        {"setCurrentAudioLangIndex",         "(I)V",                 FALSE,    NULL},
    }
};

JNI_CLASS_UTIL     subtitle_info_def=
{
    "com/mediatek/tv/model/SubtitleInfo",4,
    {
        {"getSubtitleLang", "()Ljava/lang/String;",  FALSE,    NULL},
        {"setSubtitleLang", "(Ljava/lang/String;)V", FALSE,    NULL},
        {"getCurrentSubtitleLang", "()Ljava/lang/String;",  FALSE,    NULL},
        {"setCurrentSubtitleLang", "(Ljava/lang/String;)V", FALSE,    NULL},
    }
};

JNI_CLASS_UTIL     scan_program_type_def=
{
    "com/mediatek/tv/model/DvbcProgramType",6,
    {
        {"getRadioNumber", "()I",                  FALSE,    NULL},
        {"setRadioNumber", "(I)V",                 FALSE,    NULL},
		{"getTvNumber", "()I",					   FALSE,	 NULL},
		{"setTvNumber", "(I)V", 				   FALSE,	 NULL},
		{"getAppNumber", "()I",					   FALSE,	 NULL},
		{"setAppNumber", "(I)V", 				   FALSE,	 NULL},

    }
};

JNI_CLASS_UTIL     dvbc_freq_range_def=
{
    "com/mediatek/tv/model/DvbcFreqRange",4,
    {
        {"getLowerTunerFreqBound", "()I",          FALSE,    NULL},
        {"setLowerTunerFreqBound", "(I)V",         FALSE,    NULL},
		{"getUpperTunerFreqBound", "()I",		   FALSE,	 NULL},
		{"setUpperTunerFreqBound", "(I)V", 		   FALSE,	 NULL},
    }
};

JNI_CLASS_UTIL     main_frequence_def=
{
    "com/mediatek/tv/model/MainFrequence",6,
    {
        {"getMainFrequence", "()I",                  FALSE,    NULL},
        {"setMainFrequence", "(I)V",                 FALSE,    NULL},
		{"getTsCount", "()I",					     FALSE,	 NULL},
		{"setTsCount", "(I)V", 				         FALSE,	 NULL},
		{"getNitVersion", "()I",					 FALSE,	 NULL},
		{"setNitVersion", "(I)V", 				     FALSE,	 NULL},

    }
};


JNI_CLASS_UTIL     TVCallBack_def = 
{
    "com/mediatek/tv/service/TVCallBack",25,
    {
        {"notifyScanProgress",     "(II)I",                 TRUE,     NULL},
        {"notifyScanFrequence",    "(I)I",                  TRUE,     NULL},
        {"notifyScanCompleted",    "()I",                   TRUE,     NULL},
        {"notifyScanCanceled",     "()I",                   TRUE,     NULL},
        {"notifyScanError",        "(I)I",                  TRUE,     NULL},
        {"notifyScanUserOperation","(III)I",                TRUE,     NULL},
        {"nfySvctxMsgCB",          "(Ljava/lang/String;I)V",TRUE,     NULL},
        {"onUARTSerialListener",   "(III[B)V",              TRUE,     NULL},
        {"onOperationDone",        "(IZ)V",                 TRUE,     NULL},
        {"onSourceDetected",       "(II)V",                 TRUE,     NULL},
        {"onOutputSignalStatus",   "(II)V",                 TRUE,     NULL},
        {"notifyDT",               "(III)V",                TRUE,      NULL},
        {"camStatusUpdated",       "(IB)I",                 TRUE,     NULL},
        {"camMMIMenuReceived",     "(IIBLjava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I", TRUE,     NULL},
        {"camMMIEnqReceived",      "(ILcom/mediatek/tv/model/MMIEnq;)I", TRUE,      NULL},
        {"camMMIClosed",           "(IB)I",                 TRUE,      NULL},
        {"camHostControlTune",       "(ILcom/mediatek/tv/model/HostControlTune;)I", TRUE,      NULL},
        {"camHostControlReplace",    "(ILcom/mediatek/tv/model/HostControlReplace;)I",TRUE,       NULL},
        {"camHostControlClearReplace","(IB)I",                 TRUE,       NULL},
        {"camSystemIDStatusUpdated",  "(IB)I",                 TRUE,       NULL},
        {"camSystemIDInfoUpdated",  "(I[I)I",                 TRUE,       NULL},
        {"eventServiceNotifyUpdate",    "(III)V",                TRUE,     NULL},
        {"channelServiceNotifyUpdate",  "(III)V",                TRUE,     NULL},
        {"configServiceNotifyDbgLevel",  "(I)V",                TRUE,     NULL},
        {"compServiceNotifyInfo",       "(Ljava/lang/String;)V",  TRUE,        NULL},
    }     
};


JNI_CLASS_UTIL     dt_dtg_def=
{
    "com/mediatek/tv/model/DtDTG",18,
    {
        {"getYear",             "()I",                 FALSE,        NULL},
        {"setYear",             "(I)V",                FALSE,        NULL},
        {"getMonth",            "()I",                 FALSE,        NULL},
        {"setMonth",            "(I)V",                FALSE,        NULL},
        {"getDay",                 "()I",                 FALSE,        NULL},
        {"setDay",                 "(I)V",                FALSE,        NULL},
        {"getDow",                "()I",                 FALSE,        NULL},
        {"setDow",                 "(I)V",                FALSE,        NULL},
        {"getHour",             "()I",                 FALSE,        NULL},
        {"setHour",             "(I)V",                FALSE,        NULL},
        {"getMin",                 "()I",                 FALSE,        NULL},
        {"setMin",                "(I)V",                FALSE,        NULL},
        {"getSec",                "()I",                 FALSE,        NULL},
        {"setSec",                 "(I)V",                FALSE,        NULL},
        {"getGmt",                 "()Z",                 FALSE,        NULL},
        {"setGmt",                "(Z)V",                FALSE,        NULL},
        {"getDst",                 "()Z",                 FALSE,        NULL},
        {"setDst",                 "(Z)V",                FALSE,        NULL},
    }
};

JNI_CLASS_UTIL  EventCommand_def = 
{
    "com/mediatek/tv/model/EventCommand",16,
    {
        {"isActualOnly"                  ,"()Z"                                         ,FALSE,   NULL},
        {"getMaxDay"                     ,"()I"                                         ,FALSE,   NULL},
        {"getPrefLanuage"                ,"()[Ljava/lang/String;"                       ,FALSE,   NULL},
        {"getActiveWindow"               ,"()Lcom/mediatek/tv/model/EventActiveWindow;" ,FALSE,   NULL},
        {"getEventMinSeconds"            ,"()I"                                         ,FALSE,   NULL},
        {"isFakeEventInsertionEnable"    ,"()Z"                                         ,FALSE,   NULL},
        {"getFakeEventMinSecond"         ,"()I"                                         ,FALSE,   NULL},
        {"isTimeConfictAllow"            ,"()Z"                                         ,FALSE,   NULL},
        {"isPartialOverapAllow"          ,"()Z"                                         ,FALSE,   NULL},
        {"getEventDetailSeparator"       ,"()Ljava/lang/String;"                        ,FALSE,   NULL},
        {"getCurrentChannelInfo"         ,"()Lcom/mediatek/tv/model/ChannelInfo;"       ,FALSE,   NULL},
        {"isDoRestart"                   ,"()Z"                                         ,FALSE,   NULL},
        {"isDoClean"                     ,"()Z"                                         ,FALSE,   NULL},
        {"isDoEnable"                    ,"()Z"                                         ,FALSE,   NULL},
        {"getTunerName"                  ,"()Ljava/lang/String;"                        ,FALSE,   NULL},
        {"getCommandMask"                ,"()I"                                         ,FALSE,   NULL},
    }
};


JNI_CLASS_UTIL  EventActiveWindow_def = 
{
    "com/mediatek/tv/model/EventActiveWindow",3,
    {
        {"getChannels",         "()[Lcom/mediatek/tv/model/ChannelInfo;"                ,FALSE   ,NULL},  
        {"getStartTime",        "()J"                                                   ,FALSE   ,NULL},     
        {"getDuration",         "()J"                                                   ,FALSE   ,NULL},     
    }
};

JNI_CLASS_UTIL  EventComponent_def = 
{
    "com/mediatek/tv/model/EventComponent",8,
    {
        {"getStreamContent"       , "()S"      ,   FALSE,   NULL}, 
        {"setStreamContent"       , "(S)V"     ,   FALSE,   NULL}, 
        {"getComponentType"       , "()S"      ,   FALSE,   NULL}, 
        {"setComponentType"       , "(S)V"     ,   FALSE,   NULL}, 
        {"getComponentTag"        , "()S"      ,   FALSE,   NULL}, 
        {"setComponentTag"        , "(S)V"     ,   FALSE,   NULL}, 
        {"<init>"                 , "()V"      ,   FALSE,   NULL}, 
        {"<init>"                 , "(SSS)V"   ,   FALSE,   NULL}, 
    }
};


JNI_CLASS_UTIL  EventInfo_def = 
{
    "com/mediatek/tv/model/EventInfo",33,
    {
        {"<init>"                           , "()V"                                             , FALSE,   NULL},
        {"getSvlId"                         , "()I"                                             , FALSE,   NULL},
        {"setSvlId"                         , "(I)V"                                            , FALSE,   NULL},
        {"getChannelId"                     , "()I"                                             , FALSE,   NULL},
        {"setChannelId"                     , "(I)V"                                            , FALSE,   NULL},
        {"getEventId"                       , "()I"                                             , FALSE,   NULL},
        {"setEventId"                       , "(I)V"                                            , FALSE,   NULL},
        {"getStartTime"                     , "()J"                                             , FALSE,   NULL},
        {"setStartTime"                     , "(J)V"                                            , FALSE,   NULL},
        {"getDuration"                      , "()J"                                             , FALSE,   NULL},
        {"setDuration"                      , "(J)V"                                            , FALSE,   NULL},
        {"isCaption"                        , "()Z"                                             , FALSE,   NULL},
        {"setCaption"                       , "(Z)V"                                            , FALSE,   NULL},
        {"isFreeCaMode"                     , "()Z"                                             , FALSE,   NULL},
        {"setFreeCaMode"                    , "(Z)V"                                            , FALSE,   NULL},
        {"getEventTitle"                    , "()Ljava/lang/String;"                            , FALSE,   NULL},
        {"setEventTitle"                    , "(Ljava/lang/String;)V"                           , FALSE,   NULL},
        {"getEventDetail"                   , "()Ljava/lang/String;"                            , FALSE,   NULL},
        {"setEventDetail"                   , "(Ljava/lang/String;)V"                           , FALSE,   NULL},
        {"getGuidanceMode"                  , "()I"                                             , FALSE,   NULL},
        {"setGuidanceMode"                  , "(I)V"                                            , FALSE,   NULL},
        {"getGuidanceText"                  , "()Ljava/lang/String;"                            , FALSE,   NULL},
        {"setGuidanceText"                  , "(Ljava/lang/String;)V"                           , FALSE,   NULL},
        {"getCaSystemId"                    , "()[I"                                            , FALSE,   NULL},
        {"setCaSystemId"                    , "([I)V"                                           , FALSE,   NULL},
        {"getEventCategoryNum"              , "()I"                                             , FALSE,   NULL},
        {"setEventCategoryNum"              , "(I)V"                                            , FALSE,   NULL},
        {"getEventCategory"                 , "()[I"                                            , FALSE,   NULL},
        {"setEventCategory"                 , "([I)V"                                           , FALSE,   NULL},
        {"getEventComponents"               , "()[Lcom/mediatek/tv/model/EventComponent;"       , FALSE,   NULL},
        {"setEventComponents"               , "([Lcom/mediatek/tv/model/EventComponent;)V"      , FALSE,   NULL},
        {"getEventLinkage"                  , "()[Lcom/mediatek/tv/model/EventLinkage;"         , FALSE,   NULL},
        {"setEventLinkage"                  , "([Lcom/mediatek/tv/model/EventLinkage;)V"        , FALSE,   NULL},
    }
};

JNI_CLASS_UTIL  EventLinkage_def = 
{
    "com/mediatek/tv/model/EventLinkage",8,
    {
        {"<init>"                 , "()V"      ,   FALSE,   NULL}, 
        {"<init>"                 , "(III)V"   ,   FALSE,   NULL}, 
        {"getOnId"                , "()I"      ,   FALSE,   NULL}, 
        {"setOnId"                , "(I)V"     ,   FALSE,   NULL}, 
        {"getTsId"                , "()I"      ,   FALSE,   NULL}, 
        {"setTsId"                , "(I)V"     ,   FALSE,   NULL}, 
        {"getSvcId"               , "()I"      ,   FALSE,   NULL}, 
        {"setSvcId"               , "(I)V"     ,   FALSE,   NULL}, 
    }
};

JNI_CLASS_UTIL     dtmb_freq_range_def=
{
    "com/mediatek/tv/model/DtmbFreqRange",4,
    {
        {"getLowerTunerFreqBound", "()I",          FALSE,    NULL},
        {"setLowerTunerFreqBound", "(I)V",         FALSE,    NULL},
		{"getUpperTunerFreqBound", "()I",		   FALSE,	 NULL},
		{"setUpperTunerFreqBound", "(I)V", 		   FALSE,	 NULL},
    }
};

JNI_CLASS_UTIL     dtmb_scanRF_def=
{
    "com/mediatek/tv/model/DtmbScanRF",6,
    {
        {"getRFChannel",        "()Ljava/lang/String;",     FALSE,   NULL},
        {"setRFChannel",        "(Ljava/lang/String;)V",    FALSE,   NULL},
		{"getRFScanIndex",      "()I",		                FALSE,	 NULL},
		{"setRFScanIndex",      "(I)V", 		            FALSE,	 NULL},
        {"getRFScanFrequency",  "()I",		                FALSE,	 NULL},
		{"setRFScanFrequency",  "(I)V", 		            FALSE,	 NULL},
    }
};




JNI_CLASS_UTIL*    tv_class_def[]=
{
    &List,
    &AnalogChannelInfo,
    &DvbChannelInfo,
    &channel_info_def,
    &extra_channel_info_def,
    &HostControlTune,
    &cfg_val_def,
    &video_resolution_def,
    &audio_info_def,
    &signalLevel_info_def,
    &audio_language_info_def,
    &subtitle_info_def,
    &scan_program_type_def,
    &dvbc_freq_range_def,
    &main_frequence_def,
    &TVCallBack_def,
    &dt_dtg_def,
    &EventCommand_def,
    &EventActiveWindow_def,
    &EventComponent_def,
    &EventInfo_def,
    &EventLinkage_def,
    &dtmb_freq_range_def,
    &dtmb_scanRF_def
};

void init_jni_def(JNIEnv *env)
{
    unsigned int        i = 0;
    int                 j = 0;
    JNI_CLASS_UTIL*     jni_class_util = NULL;

    for(i=0;i<sizeof(tv_class_def)/sizeof(tv_class_def[0]);i++)
    {
        jni_class_util = tv_class_def[i];
        init_jni_single_def(env,jni_class_util);
    }
}

void init_jni_single_def(JNIEnv *env,JNI_CLASS_UTIL*     jni_class_util)
{
    int                 j = 0;

    {
        jclass clazz = JNI_GET_CLASS_BY_NAME(env,jni_class_util->class_name);
        for (j=0; j<jni_class_util->max_method; j++ )
        {
            JNI_METHOD*  jni_method = &(jni_class_util->jmethods[j]);
            if (jni_method->static_method == (unsigned char)0x1)
            {
                jni_method->methodId = JNI_GET_CLASS_STATIC_METHOD( env,clazz, jni_method->method_name, jni_method->method_signature);
            }
            else
            {
                jni_method->methodId = JNI_GET_CLASS_METHOD( env,clazz, jni_method->method_name, jni_method->method_signature);
            }

            if (jni_method->methodId == NULL)
            {
                LOGD("Register Class %s[%d]" LOG_TAIL , jni_class_util->class_name,jni_class_util->max_method);
                LOGD("\t\t[%d]name=%s static=%d signature=%s    %s"LOG_TAIL,j,
                     jni_method->method_name,
                     jni_method->static_method,
                     jni_method->method_signature,
                     (jni_method->methodId == NULL)?"[Fail*****]":"[Success]"
                     );
            }
        }
    }
}


void* jni_malloc(unsigned int size)
{
    return malloc(size);
}

void jni_free(void* pt)
{
    if (pt != NULL)
    {
        free(pt);
    }
}

void jstring2buffer(JNIEnv *env,jobject jstr,char* ui1_buffer,unsigned int z_len )
{
    const char*         s_buffer                      = NULL;
    if (jstr == NULL)
        return;

    s_buffer = JNI_GET_STRING_UTF_CHARS(env,(jstring)jstr);
    if (s_buffer!= NULL){
        strncpy(ui1_buffer,s_buffer,z_len);
    }

    JNI_RELEASE_STRING_UTF_CHARS(env,(jstring)jstr,s_buffer);
}

EXTERN_C_END
