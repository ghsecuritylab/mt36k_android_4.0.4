package com.mediatek.tv.service;

import com.mediatek.tv.common.ConfigValue;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.AudioInfo;
import com.mediatek.tv.model.AudioLanguageInfo;
import com.mediatek.tv.model.SubtitleInfo;
import com.mediatek.tv.model.ChannelModel;
import com.mediatek.tv.model.EventInfo;
import com.mediatek.tv.model.EventCommand;
import com.mediatek.tv.model.AnalogChannelInfo;
import com.mediatek.tv.model.DvbChannelInfo;
import com.mediatek.tv.model.VideoResolution;
import com.mediatek.tv.model.ScanParams;
import com.mediatek.tv.model.ScanParaPalSecam;
import com.mediatek.tv.model.ScanParaDvbc;
import com.mediatek.tv.model.ScanExchangeFrenquenceRange;
import com.mediatek.tv.model.InputRecord;
import com.mediatek.tv.model.InputRegion;
import com.mediatek.tv.model.DtType;
import com.mediatek.tv.model.DtDTG;
import com.mediatek.tv.service.ITVCallBack;
import com.mediatek.tv.model.HostControlTune;
import com.mediatek.tv.model.DvbcProgramType;
import com.mediatek.tv.model.SignalLevelInfo;
import com.mediatek.tv.model.ExtraChannelInfo;
import com.mediatek.tv.model.DvbcFreqRange;
import com.mediatek.tv.model.ScanParaDtmb;
import com.mediatek.tv.model.DtmbFreqRange;
import com.mediatek.tv.model.DtmbScanRF;
import com.mediatek.tv.model.MainFrequence;

interface ITVRemoteService {
	void registerCallback(ITVCallBack cb);
    void unregisterCallback(ITVCallBack cb);
    void unregisterAll();
    
	/*Factory*/    
    int openUARTSerial_proxy(int uartSerialID, in int[] uartSerialSetting, inout int[] handle);
    int closeUARTSerial_proxy(int handle);
    int getUARTSerialSetting_proxy(int handle, inout int[] uartSerialSetting);
    int getUARTSerialOperationMode_proxy(int handle, inout int[] operationMode);
    int setUARTSerialSetting_proxy(int handle, inout int[] uartSerialSetting);
    int setUARTSerialOperationMode_proxy(int handle, int operationMode);
    int setUARTSerialMagicString_proxy(int handle,inout byte[] uartSerialMagicSetting);
    int outputUARTSerial_proxy(int handle, inout byte[] uartSerialData);
    /*Factory*/
    
    int autoAdjust_proxy   (String autoType);
    int powerOff_proxy     ();
    int getCfg_proxy       (int inputSource, String configType,inout ConfigValue configParamsValue, inout ConfigValue configValue );
  	int setCfg_proxy       (int inputSource, String configType,in ConfigValue configValue);
    int updateCfg_proxy    (String configType);
    int resetCfgGroup_proxy(String resetType);
    int readGPIO_proxy     (inout ConfigValue configValue);
    int writeGPIO_proxy    (in ConfigValue configValue);
    int getCfgMinMax_proxy (String configType,inout ConfigValue configValue);
    
    int dtSetConfig_proxy(int configFlag);
    int dtSetDst_proxy(boolean bEnable);
    int dtSetTz_proxy(long tzOffset);
    int dtSetUtc_proxy(long sec, int milliSec);
    int dtSetDstCtrl_proxy(boolean bEnable);
    int dtSetDsChange_proxy(long changeTime);
    int dtSetDsOffset_proxy(long OffsetTime);
    int dtSetSyncSrc_proxy(int eSyncSrcType, int eSrcDescType, in String data);
    int dtSetTzCtrl_proxy(boolean bEnable);
    int dtSetSysCountCode_proxy(in byte[] countCode, int regionId);
    boolean dtGetDst_proxy();
    long dtGetGps_proxy(inout int[] data);
    long dtGetTz_proxy();
    long dtGetUtc_proxy(inout int[] data);
    long dtGetBrdcstUtc_proxy(inout int[] data);
    int dtGetCountCode_proxy(int index, inout byte[] countCode, inout long[] data);
    boolean dtGetDstCtrl_proxy();
    long dtGetDsChange_proxy();
    long dtGetDsOffset_proxy();
    boolean dtGetTzCtrl_proxy();
    int dtGetNumCountCode_proxy();
    int dtGetSysCountCode_proxy(inout byte[] countCode, inout int[] data);
    byte dtGetLastSyncTblId_proxy();
    int dtCheckInputTime_proxy(boolean bEnable);
    int dtConfigCheckInputTime_proxy(int eSetType, int setValue);
    int dt_utc_sec_to_dtg_proxy(long utcTime, inout DtDTG dtgTime);
	int dt_utc_sec_to_loc_dtg_proxy(long utcTime, inout DtDTG dtgTime);
	int dt_conv_utc_local_proxy(in DtDTG dtgTimeIn, inout DtDTG dtgTimeOut);
	long dt_dtg_to_sec_proxy(in DtDTG dtgTime);
	long dt_gps_sec_to_utc_sec_proxy(long gpsSec);
	int dt_bcd_to_sec_proxy(String bcdTime);
	int dt_mjd_bcd_to_dtg_proxy(String bcdTime, inout DtDTG dtgTime);
	int dt_mjd_to_dtg_proxy(long mjdTime, inout DtDTG dtgTime);
	long dt_dtg_to_mjd_proxy(in DtDTG dtgTime);
	int dt_dtg_to_mjd_bcd_proxy(in DtDTG dtgTime, inout int[] mjdInfo);
	long dt_diff_proxy(in DtDTG dtgTimeFrom, in DtDTG dtgTimeTo);
	int dt_add_proxy(in DtDTG dtgTimeOld, long addSec, inout DtDTG dtgTimeNew);
	boolean dt_is_leap_year_proxy(long year);
	int dt_reg_nfy_fct_proxy(inout long[] handle);
    
    int channelSelect_proxy(boolean b_focus, in ChannelInfo chInfo);
    int channelSelectEx_proxy(boolean b_focus, int audioLangIndex, int audioMts, in ChannelInfo chInfo, in ExtraChannelInfo exChInfo);
    int syncStopService_proxy();
    int startVideoStream_proxy(int focusID);
    int syncStopSubtitleStream_proxy();
    int fineTune_proxy(in AnalogChannelInfo chInfo, int freq, boolean b_tuning);//TODO
    int freeze_proxy(int focusID, boolean b_freeze);
    int setVideoMute_proxy();
    int getVideoResolution_proxy(int focusID, inout VideoResolution videoRes);
    int getAudioInfo_proxy(int focusID, out AudioInfo audioInfo);
    int getsignalLevelInfo_proxy(int focusID, out SignalLevelInfo signalLevelInfo);
    int getDtvAudioLangInfo_proxy(out AudioLanguageInfo audioLangInfo);
    int setDtvAudioLang_proxy(String audioLang);
    int setDtvAudioLangByIndex_proxy(int focusID,int audioIndex);
    int getSubtitleInfo_proxy(out SubtitleInfo subtitleInfo);
    int setSubtitleLang_proxy(String audioLang);
    int getStreamMpegPid_proxy(String streamType);
    int selectMpegStreamByPid_proxy(String streamType,int pid);
    boolean isCaptureLogo_proxy();
    int setMute_proxy(boolean b_mute);
    boolean getMute_proxy();
    int getDtvAudioDecodeType_proxy();
    int setVideoBlueMute_proxy(int focusID);
    int setVideoBlueMuteEx_proxy(int focusID,boolean bBlueMute,boolean bBlock);
    boolean isFreeze_proxy(int focusID);
    boolean enableFreeze_proxy(int focusID);
    int setDisplayAspectRatio_proxy(int dispAspRatio);
    int getDisplayAspectRatio_proxy();    
    //int serviceSet_proxy(String setType,Object setValue);
    //int serviceGet_proxy(String getType,Object getValue);

	int updateTVWindowRegion_proxy(int focusID, int winX, int winY, int winWidth, int winHeight);
	int stopStream_proxy(int focusID, int streamType);	
	int startAudioStream_proxy(int focusID);
	int syncStopVideoStream_proxy(int focusID);	
	int showSnowAsNoSignal_proxy(int focusID,boolean bSnow);	
	int updateFocusWindow_proxy(int focusID);   
	int updateTVMode_proxy(int tvMode);
	int setMTS_proxy(int focusID, int audMTSType);
    
	//Channel service start
    int setChannelList_proxy(int channelOperator, int svlid, in ChannelModel channelModel);
    int getChannelList_proxy(int svlId,inout ChannelModel channelModel);
    int fsSyncChannelList_proxy(int svlId);
    int fsStoreChannelList_proxy(int svlId);
    int digitalDBClean_proxy(int svlId);
    //Channel service end
    

    /* scan service proxy function start */
    int startScan_pal_secam_proxy(String scanMode, in ScanParaPalSecam p /*,ScanService scanService remove it*/);
    int cancelScan_proxy(String scanMode);
    int getScanData_proxy(String scanMode, int type,inout ScanExchangeFrenquenceRange scanExchangeData);
    int scanExchangeData_proxy( inout int[] exchangeData );
    
    int startScan_dvbc_proxy(String scanMode, in ScanParaDvbc p );
    int cancelScan_dvbc_proxy(String scanMode);
    int getDefaultSymRate_proxy(String countryCode);
    int getDefaultFrequency_proxy(String countryCode);
    int getDefaultEMod_proxy(String countryCode);
    int getDefaultNwID_proxy(String countryCode);
    int getDvbcScanTypeNum_proxy(String scanMode, out DvbcProgramType dvbcScanData);
    int getDvbcFreqRange_proxy(String scanMode, out DvbcFreqRange freqRange);
    int getDvbcMainFrequence_proxy(String scanMode, out MainFrequence mainFrequence);
    int startScan_dtmb_proxy(String scanMode, in ScanParaDtmb p );
    int cancelScan_dtmb_proxy(String scanMode);
    int getDtmbFreqRange_proxy(String scanMode, out DtmbFreqRange freqRange);
    int getFirstDtmbScanRF_proxy(String scanMode, out DtmbScanRF firstRF);
    int getLastDtmbScanRF_proxy(String scanMode, out DtmbScanRF lastRF);
    int getNextDtmbScanRF_proxy(String scanMode, in DtmbScanRF currRF, out DtmbScanRF nextRF);
    int getPrevDtmbScanRF_proxy(String scanMode, in DtmbScanRF currRF, out DtmbScanRF prevRF);
    int getCurrentDtmbScanRF_proxy(String scanMode, int channelId, out DtmbScanRF currRF);
    /* scan service proxy function end */

    /* input service proxy function start */
    int inputServiceBind_proxy(int output, int inputId);
    int inputServiceGetRecord_proxy(int index, inout InputRecord inputRecord);
    int inputServiceSetOutputMute(int output, boolean mute);
    int inputServiceSwap_proxy(int output1, int output2);
    int setScreenOutputRect_proxy(int output,int left, int right, int top, int bottom);
    int setScreenOutputVideoRect_proxy(int output,int left, int right, int top, int bottom);
    int getScreenOutputRect_proxy(int output, inout InputRegion inputRegion);
    int getScreenOutputVideoRect_proxy(int output, inout InputRegion inputRegion);
    int inputSourceExchangeData_proxy( inout int[] inputSourceData );
    /* input service proxy function end */
    
    /* OSD service proxy function start */
    boolean setOSDColorKey_proxy(boolean enable, int colorkey);
    boolean setOSDOpacity_proxy(int opacity);
    /* OSD service proxy function end */
    
    /* Event service proxy function start */
    int eventServiceSetCommand_proxy(in EventCommand eventCommand);
	int eventServiceGetPFEvents_proxy      (in DvbChannelInfo channelInfo,out List<EventInfo> events);
	int eventServiceGetScheduleEvents_proxy(in DvbChannelInfo channelInfo,in long startTime,in long endTime,out List<EventInfo> events );
	/* Event service proxy function end */
    int getSlotNum_proxy();
    boolean isSlotActive_proxy(int slotId);
    int enterMMI_proxy(int slotId);
    String getCamName_proxy(int slotId);
    int[] getCamSystemIDInfo_proxy(int slotId);    
    int closeMMI_proxy(int slotId);
    int setMMIClosed_proxy(int slotId);
    int answerMMIMenu_proxy(int slotId,int menuId,char answerItem);
    int answerMMIEnq_proxy(int slotId,int enqId,boolean answer,in String answerData);    
    int askRelease_proxy(int slotId);
    int setCITsPath_proxy(int slotId,boolean b_switch);
    int setCIInputDTVPath_proxy(int slotId,boolean b_switch);
    int getTunedChannel_proxy(int svlId,inout HostControlTune tune);
    
    int activateComponent_proxy(String comp);
    int inactivateComponent_proxy(String comp);
    int updateSysStatus_proxy(String statusDesc);
    boolean IsTTXAvail_proxy();
    int sendkeyEventtoComp_proxy(int ui4_keycode,int keyevent);
    int lockDigitalTuner_proxy(int frequency);
    int unlockDigitalTuner_proxy(int magicID);
    
    int getCurrentDTVAudioCodec_proxy();
    int getCurrentDTVVideoCodec_proxy();
    void setMuteState_proxy(boolean b_mute);
    boolean getMuteState_proxy();
}
