package com.mediatek.tv.service;

import java.util.List;


import android.content.Context;
import android.media.AudioManager;
import android.os.RemoteException;
import android.util.Log;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ConfigValue;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.model.AudioInfo;
import com.mediatek.tv.model.AudioLanguageInfo;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.AnalogChannelInfo;
import com.mediatek.tv.model.ChannelModel;
import com.mediatek.tv.model.DtDTG;
import com.mediatek.tv.model.DvbChannelInfo;
import com.mediatek.tv.model.DvbcProgramType;
import com.mediatek.tv.model.EventCommand;
import com.mediatek.tv.model.HostControlTune;
import com.mediatek.tv.model.InputRecord;
import com.mediatek.tv.model.InputRegion;
import com.mediatek.tv.model.ScanExchangeFrenquenceRange;
import com.mediatek.tv.model.ScanParaDvbc;
import com.mediatek.tv.model.ScanParaPalSecam;
import com.mediatek.tv.model.SignalLevelInfo;
import com.mediatek.tv.model.SubtitleInfo;
import com.mediatek.tv.model.VideoResolution;
import com.mediatek.tv.model.ExtraChannelInfo;
import com.mediatek.tv.model.DvbcFreqRange;
import com.mediatek.tv.model.ScanParaDtmb;
import com.mediatek.tv.model.DtmbFreqRange;
import com.mediatek.tv.model.DtmbScanRF;
import com.mediatek.tv.model.MainFrequence;
import com.mediatek.tv.common.TVCommon;
import android.content.Intent;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class TVRemoteServiceHandler extends ITVRemoteService.Stub {
	private static final String TAG = "[J]TVRemoteServiceHandler";

	private TVCallBack tvCallback;

	private static Context mContext;
	
	public TVRemoteServiceHandler(TVCallBack tvCallback) {
		super();
		this.tvCallback = tvCallback;
	}
	
	public static void setContext(Context context){
		TVRemoteServiceHandler.mContext = context;
	}
	
	public void registerCallback(ITVCallBack cb) throws RemoteException {
		Logger.d(TAG, "Enter  registerCallback ");
		Logger.d(TAG, "cb = " + cb);
		if (tvCallback != null) {
			tvCallback.registerCallback(cb);
		}
		Logger.d(TAG, "Leave  registerCallback ");
	}

	public void unregisterCallback(ITVCallBack cb) throws RemoteException {
		Logger.d(TAG, "Enter  unregisterCallback ");
		Logger.d(TAG, "cb = " + cb);
		if (tvCallback != null) {
			tvCallback.unregisterCallback(cb);
		}
		Logger.d(TAG, "Leave  unregisterCallback ");
	}

	public void unregisterAll() throws RemoteException {
		Logger.d(TAG, "Enter  unregisterAll ");
		if (tvCallback != null) {
			tvCallback.destroyCallBack();
		}
		Logger.d(TAG, "Leave  unregisterAll ");
	}

	public int autoAdjust_proxy(String autoType) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  autoAdjust_proxy ");
		Logger.d(TAG, "autoType = " + autoType);
		// ret = TVNative.autoAdjust(inputSource, configType,
		// configValue);//TODO
		Logger.d(TAG, "Leave  autoAdjust_proxy ");
		return ret;
	}

	public int powerOff_proxy() throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  powerOff_proxy ");
		ret = TVNative.powerOff_native();
		Logger.d(TAG, "Leave  powerOff_proxy ret = " + ret);
		return ret;
	}

	public int getCfg_proxy(int inputSource, String configType, ConfigValue configParamsValue, ConfigValue configValue)
	        throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  getCfg_proxy ");
		Logger.d(TAG, "inputSource = " + inputSource);
		Logger.d(TAG, "configType  = " + configType);
		
		if (null != configParamsValue) {
			Logger.d(TAG, configParamsValue.toString());
		}
		if (null != configValue) {
			Logger.d(TAG, configValue.toString());
		}
		
		if(configType.equals(ConfigType.CFG_VOLUME) && mContext != null && configValue != null){

			final AudioManager mAudManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
			
			int val = mAudManager.getStreamVolume(AudioManager.STREAM_MUSIC);

			Logger.d(TAG,"get volume val = " + val);

			configValue.setIntValue(val);
			
			return 0;
			
	    }

		ret = TVNative.getCfg_native(inputSource, configType, configParamsValue, configValue);
		Logger.d(TAG, "Leave  getCfg_proxy ret = " + ret);
		return ret;
	}

	public int setCfg_proxy(int inputSource, String configType, ConfigValue configValue) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  setCfg_proxy ");
		Logger.d(TAG, "inputSource = " + inputSource);
		Logger.d(TAG, "configType  = " + configType);
		if (null != configValue) {
			Logger.d(TAG, configValue.toString());
		}

		if(configType.equals(ConfigType.CFG_VOLUME) && mContext != null && configValue != null){
			
    	    Logger.d(TAG, "Set volume to " + configValue.toString());
			
            final AudioManager mAudManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
            
            mAudManager.setStreamVolume(AudioManager.STREAM_MUSIC,configValue.getIntValue(),0);
            
            return 0;
	    }
		
		ret = TVNative.setCfg_native(inputSource, configType, configValue);
		Logger.d(TAG, "Leave  setCfg_proxy ret = " + ret);
		return ret;
	}


	public int updateCfg_proxy(String configType) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  updateCfg_proxy ");
		Logger.d(TAG, "configType = " + configType);
		ret = TVNative.updateCfg_native(configType);
		Logger.d(TAG, "Leave  updateCfg_proxy ret = " + ret);
		return ret;
	}

	public int resetCfgGroup_proxy(String resetType) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  resetCfgGroup_proxy ");
		Logger.d(TAG, "resetType = " + resetType);
		ret = TVNative.resetCfgGroup_native(resetType);
		Logger.d(TAG, "Leave  resetCfgGroup_proxy ret = " + ret);
		return ret;
	}

	public int readGPIO_proxy(ConfigValue configValue) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  readGPIO_proxy ");
		Logger.d(TAG, "configValue = " + configValue);
		ret = TVNative.readGPIO_native(configValue);
		Logger.d(TAG, "Leave  readGPIO_proxy ret = " + ret);
		return ret;
	}

	public int writeGPIO_proxy(ConfigValue configValue) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  writeGPIO_proxy ");
		Logger.d(TAG, "configValue = " + configValue);
		ret = TVNative.writeGPIO_native(configValue);
		Logger.d(TAG, "Leave  writeGPIO_proxy ret = " + ret);
		return ret;
	}

	public int channelSelect_proxy(boolean b_focus, ChannelInfo chInfo) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  channelSelect_proxy ");
		Logger.d(TAG, "chInfo = " + chInfo);
		ret = TVNative.channelSelect_native(chInfo, b_focus);
		Logger.d(TAG, "Leave  channelSelect_proxy ");
		return ret;
	}
	
	public int channelSelectEx_proxy(boolean b_focus, int audioLangIndex, int audioMts,ChannelInfo chInfo, ExtraChannelInfo exChInfo) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  channelSelect_ex_proxy ");
		Logger.d(TAG, "chInfo = " + chInfo);
		Logger.d(TAG, "ExChInfo = " + exChInfo);
		ret = TVNative.channelSelectEx_native(chInfo,exChInfo, b_focus,audioLangIndex,audioMts);
		Logger.d(TAG, "Leave  channelSelect_ex_proxy ");
		return ret;
	}
	
	public int syncStopService_proxy() throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  syncStopService_proxy ");
		ret = TVNative.syncStopService_native();
		Logger.d(TAG, "Leave  syncStopService_proxy " + ret);
		return ret;
	}

	public int startVideoStream_proxy(int focusID) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  startVideoStream_proxy ");
		ret = TVNative.startVideoStream_native(focusID);
		Logger.d(TAG, "Leave  startVideoStream_proxy " + ret);
		return ret;
	}
	
	public int syncStopSubtitleStream_proxy() throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  syncStopSubtitleStream_proxy ");
		ret = TVNative.syncStopSubtitleStream_native();
		Logger.d(TAG, "Leave  syncStopSubtitleStream_proxy " + ret);
		return ret;
	}
	
	public int fineTune_proxy(AnalogChannelInfo chInfo, int freq, boolean b_tuning) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  fineTune_proxy ");
		Logger.d(TAG, "chInfo = " + chInfo);
		Logger.d(TAG, "freq = " + freq);
		Logger.d(TAG, "b_tuning = " + b_tuning);
		ret = TVNative.fineTune_native(chInfo, freq, b_tuning);
		Logger.d(TAG, "Leave  fineTune_proxy ");
		return ret;
	}

	public int freeze_proxy(int focusID, boolean b_freeze) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  freeze_proxy ");
		Logger.d(TAG, "focusID = " + focusID);
		Logger.d(TAG, "b_freeze = " + b_freeze);
		ret = TVNative.freeze_native(focusID, b_freeze);
		Logger.d(TAG, "Leave  freeze_proxy ");
		return ret;
	}

	public int setVideoMute_proxy() throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  setVideoMute_proxy ");
		ret = TVNative.setVideoMute_native();
		Logger.d(TAG, "Leave  setVideoMute_proxy ");
		return ret;
	}

	public int getVideoResolution_proxy(int focusID, VideoResolution videoRes) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getVideoResolution_proxy ");
		Logger.d(TAG, "focusID = " + focusID);
		Logger.d(TAG, "videoRes = " + videoRes);
		ret = TVNative.getVideoResolution_native(focusID, videoRes);
		Logger.d(TAG, "Leave  getVideoResolution_proxy ");
		return ret;
	}

	public int getAudioInfo_proxy(int focusID, AudioInfo audioInfo) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getAudioInfo_proxy ");
		Logger.d(TAG, "focusID = " + focusID);
		Logger.d(TAG, "audioInfo = " + audioInfo);
		ret = TVNative.getAudioInfo_native(focusID, audioInfo);
		Logger.d(TAG, "Leave  getAudioInfo_proxy ");
		return ret;
	}
	
	public int getsignalLevelInfo_proxy(int focusID,SignalLevelInfo signalLevelInfo) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getsignalLevelInfo_proxy ");
		Logger.d(TAG, "focusID = " + focusID);
		Logger.d(TAG, "signalLevelInfo = " + signalLevelInfo);
		ret = TVNative.getSignalLevelInfo_native(focusID, signalLevelInfo);
		Logger.d(TAG, "Leave  getsignalLevelInfo_proxy ");
		return ret;
	}
	public int getDtvAudioLangInfo_proxy(AudioLanguageInfo audioLangInfo) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getDtvAudioLangInfo_proxy ");
		Logger.d(TAG, "audioLangInfo = " + audioLangInfo);
		ret = TVNative.getDtvAudioInfo_native(audioLangInfo);
		Logger.d(TAG, "Leave  getDtvAudioLangInfo_proxy ");
		return ret;
	}
	
	public int setDtvAudioLang_proxy(String audioLang) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  setDtvAudioLang_proxy ");
		Logger.d(TAG, "audioLang = " + audioLang);
		ret = TVNative.setDtvAudioLang_native(audioLang);
		Logger.d(TAG, "Leave  setDtvAudioLang_proxy ");
		return ret;
	}
	
	
	public int setDtvAudioLangByIndex_proxy(int focusID,int audioIndex) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  setDtvAudioLangByIndex_proxy ");
		Logger.d(TAG, "focusID = " + focusID);
		Logger.d(TAG, "audioIndex = " + audioIndex);
		ret = TVNative.setDtvAudioLangByIndex_native(focusID,audioIndex);
		Logger.d(TAG, "Leave  setDtvAudioLangByIndex_proxy ");
		return ret;
	}
	
	public int getSubtitleInfo_proxy(SubtitleInfo subtitleInfo) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getAudioInfo_proxy ");
		Logger.d(TAG, "subtitleInfo = " + subtitleInfo);
		ret = TVNative.getSubtitleInfo_native(subtitleInfo);
		Logger.d(TAG, "Leave  getAudioInfo_proxy ");
		return ret;
	}
	
	public int setSubtitleLang_proxy(String subtitleLang) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  setSubtitleLang_proxy ");
		Logger.d(TAG, "subtitleLang = " + subtitleLang);
		ret = TVNative.setSubtitleLang_native(subtitleLang);
		Logger.d(TAG, "Leave  setSubtitleLang_proxy ");
		return ret;
	}
	
	public int getStreamMpegPid_proxy(String streamType) throws RemoteException {
		int pid = 0;
		Logger.d(TAG, "Enter  getStreamMepgPid_proxy ");
		Logger.d(TAG, "streamType = " + streamType);
		pid = TVNative.getStreamMpegPid_native(streamType);
		Logger.d(TAG, "Leave  getStreamMepgPid_proxy ");
		return pid;
	}

	public int selectMpegStreamByPid_proxy(String streamType, int pid) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  selectMpegStreamByPid_proxy ");
		Logger.d(TAG, "streamType = " + streamType);
		Logger.d(TAG, "pid = " + pid);
		ret = TVNative.selectMpegStreamByPid_native(streamType, pid);
		Logger.d(TAG, "Leave  selectMpegStreamByPid_proxy ");
		return ret;
	}

	public boolean isCaptureLogo_proxy() throws RemoteException {
		boolean ret = false;
		Logger.d(TAG, "Enter  isCaptureLogo_proxy ");
		ret = TVNative.isCaptureLogo_native();
		Logger.d(TAG, "Leave  isCaptureLogo_proxy ");
		return ret;
	}
	
	public int setMute_proxy(boolean b_mute) throws RemoteException {
		int ret = 0;
		Logger.d(TAG, "Enter  setMute_proxy ");
		Logger.d(TAG, "b_mute = " + b_mute);
		//ret = TVNative.setMute_native(b_mute);
		if(mContext != null) {
            final AudioManager mAudManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
            mAudManager.setStreamMute(AudioManager.STREAM_MUSIC, b_mute);
	    }
		Logger.d(TAG, "Leave  setMute_proxy ");
		return ret;
	}

	public boolean getMute_proxy() throws RemoteException {
		boolean ret = false;
		Logger.d(TAG, "Enter  getMute_proxy ");
		ret = TVNative.getMute_native();
		Logger.d(TAG, "Leave  getMute_proxy ");
		return ret;
	}

	public int getDtvAudioDecodeType_proxy() throws RemoteException {
		int ret = 0;
		Logger.d(TAG, "Enter  getDtvAudioDecodeType_proxy ");
		ret = TVNative.getDtvAudioDecodeType_native();
		Logger.d(TAG, "Leave  getDtvAudioDecodeType_proxy ");
		return ret;
	}

	public int setVideoBlueMute_proxy(int focusID) throws RemoteException {
		int ret = 0;
		Logger.d(TAG, "Enter  setVideoBlueMute_proxy ");
		ret = TVNative.setVideoBlueMute_native(focusID,true,false);
		Logger.d(TAG, "Leave  setVideoBlueMute_proxy ");
		return ret;
	}
	
	public int setVideoBlueMuteEx_proxy(int focusID,boolean bBlueMute,boolean bBlock) throws RemoteException {
		int ret = 0;
		Logger.d(TAG, "Enter  setVideoBlueMute_proxy ");
		ret = TVNative.setVideoBlueMute_native(focusID,bBlueMute,bBlock);
		Logger.d(TAG, "Leave  setVideoBlueMuteEx_proxy ");
		return ret;
	}
	
	public boolean isFreeze_proxy(int focusID) throws RemoteException {
		boolean ret = false;
		Logger.d(TAG, "Enter  isFreeze_proxy ");
		ret = TVNative.isFreeze_native(focusID);
		Logger.d(TAG, "Leave  isFreeze_proxy ");
		return ret;
	}
	
	public boolean enableFreeze_proxy(int focusID) throws RemoteException {
		boolean ret = false;
		Logger.d(TAG, "Enter  enableFreeze_proxy ");
		ret = TVNative.enableFreeze_native(focusID);
		Logger.d(TAG, "Leave  enableFreeze_proxy ");
		return ret;
	}
	
	public int setDisplayAspectRatio_proxy(int dispAspRatio) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getDisplayAspectRatio_proxy() throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getDisplayAspectRatio_proxy ");
		ret = TVNative.getDisplayAspectRatio_native();
		Logger.d(TAG, "Leave  getDisplayAspectRatio_proxy ");
		return ret;
	}

	public int updateTVWindowRegion_proxy(int focusID, int winX, int winY, int winWidth, int winHeight)
	        throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  updateTVWindowRegion_proxy ");
		ret = TVNative.updateTVWindowRegion_native(focusID, winX, winY, winWidth, winHeight);
		Logger.d(TAG, "Leave  updateTVWindowRegion_proxy ");
		return ret;
	}

	public int stopStream_proxy(int focusID, int streamType) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  stopStream_proxy ");
		ret = TVNative.stopStream_native(focusID, streamType);
		Logger.d(TAG, "Leave  stopStream_proxy ");
		return ret;
	}

	public int startAudioStream_proxy(int focusID) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  startAudioStream_proxy ");
		ret = TVNative.startAudioStream_native(focusID);
		Logger.d(TAG, "Leave  startAudioStream_proxy ");
		return ret;
	}

	public int syncStopVideoStream_proxy(int focusID) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  syncStopVideoStream_proxy ");
		ret = TVNative.syncStopVideoStream_native(focusID);
		Logger.d(TAG, "Leave  syncStopVideoStream_proxy ");
		return ret;
	}
	
	public int showSnowAsNoSignal_proxy(int focusID,boolean bSnow) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  showSnowAsNoSignal_proxy ");
		ret = TVNative.showSnowAsNoSignal_native(focusID,bSnow);
		Logger.d(TAG, "Leave  showSnowAsNoSignal_proxy ");
		return ret;
	}
	
	public int updateFocusWindow_proxy(int focusID) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  updateFocusWindow_proxy ");
		ret = TVNative.updateFocusWindow_native(focusID);
		Logger.d(TAG, "Leave  updateFocusWindow_proxy ");
		return ret;
	}
	
	public int updateTVMode_proxy(int tvMode) throws RemoteException {
	    int ret = -1;
        Logger.d(TAG, "Enter  updateTVMode_proxy ");
        ret = TVNative.updateTVMode_native(tvMode);
        Logger.d(TAG, "Leave  updateTVMode_proxy ");
        return ret;
	}
	
	public int setMTS_proxy(int focusID, int audMTSType) throws RemoteException {
	    int ret = -1;
        Logger.d(TAG, "Enter  setMTS_proxy ");
        ret = TVNative.setMTS_native(focusID, audMTSType);
        Logger.d(TAG, "Leave  setMTS_proxy ");
        return ret;
	}

	public int setChannelList_proxy(int channelOperator, int svlid, ChannelModel channelModel) throws RemoteException {
		int ret = -1;
		if (channelModel == null) {
			return ret;
		}
		Logger.d(TAG, "Enter  setChannelList_proxy ");
		Logger.d(TAG, "channelOperator = " + channelOperator);
		Logger.d(TAG, "svlid = " + svlid);
		Logger.d(TAG, "channelModel = " + channelModel);
		List<ChannelInfo> list = channelModel.getChannelList();
		if (list == null) {
			Logger.d(TAG, "the list is null");
			return -1;
		}
		for (int i = 0; i < list.size(); i++) {
			Logger.d(TAG, list.get(i).toString());
		}
		ret = TVNative.setChannelList_native(channelOperator, svlid, list);
		Logger.d(TAG, "Leave  setChannelList_proxy ");
		return ret;
	}

	public int getChannelList_proxy(int svlId, ChannelModel channelModel) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getChannelList_proxy ");
		Logger.d(TAG, "svlId = " + svlId);
		if (channelModel == null) {
			Logger.d(TAG, "channelModel is null ");
			return -1;
		}
		ret = TVNative.getChannelList_native(svlId, channelModel.getChannelList());
		Logger.d(TAG, "Leave  getChannelList_proxy ");
		return ret;
	}

	public int fsSyncChannelList_proxy(int svlId) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  fsSyncChannelList_proxy ");
		Logger.d(TAG, "svlId = " + svlId);
		ret = TVNative.fsSyncChannelList_native(svlId);
		Logger.d(TAG, "Leave  fsSyncChannelList_proxy ");
		return ret;
	}

	public int fsStoreChannelList_proxy(int svlId) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  fsStoreChannelList_proxy ");
		Logger.d(TAG, "svlId = " + svlId);
		ret = TVNative.fsStoreChannelList_native(svlId);
		Logger.d(TAG, "Leave  fsStoreChannelList_proxy ");
		return ret;
	}

	public int digitalDBClean_proxy(int svlId) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  digitalDBClean_native_proxy ");
		Logger.d(TAG, "svlId = " + svlId);
		ret = TVNative.digitalDBClean_native(svlId);
		Logger.d(TAG, "Leave  digitalDBClean_native_proxy ");
		return ret;
	}

	public int startScan_pal_secam_proxy(String scanMode, ScanParaPalSecam p) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  startScan_proxy ");
		Logger.d(TAG, "scanMode = " + scanMode);
		Logger.d(TAG, "p = " + p);
		ret = TVNative.startScan_native(scanMode, p, null);
		Logger.d(TAG, "Leave  startScan_proxy ");
		return ret;
	}

	public int cancelScan_proxy(String scanMode) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  cancelScan_proxy ");
		Logger.d(TAG, "scanMode = " + scanMode);
		ret = TVNative.cancelScan_native(scanMode);
		Logger.d(TAG, "Leave  cancelScan_proxy ");
		return ret;
	}

	public int getScanData_proxy(String scanMode, int type, ScanExchangeFrenquenceRange scanExchangeData)
	        throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getScanData_proxy ");
		Logger.d(TAG, "scanMode = " + scanMode);
		Logger.d(TAG, "type = " + type);
		Logger.d(TAG, "scanExchangeData = " + scanExchangeData);
		ret = TVNative.getScanData_native(scanMode, type, scanExchangeData);
		Logger.d(TAG, "Leave  getScanData_proxy ");
		return ret;
	}
	
	public int scanExchangeData_proxy(int[] scanData) throws RemoteException {
        int ret = -1;
        Logger.d(TAG, "Enter  scanExchangeData_proxy ");
        Logger.d(TAG, "scanData = " + scanData);
        ret = TVNative.scanExchangeData(scanData);
        Logger.d(TAG, "Leave  scanExchangeData_proxy ");
        return ret;
    }

	/* For dvbc scan */
	public int startScan_dvbc_proxy(String scanMode, ScanParaDvbc p) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  startScan_dvbc_proxy startScan_dvbc_proxy");
		Logger.d(TAG, "scanMode = " + scanMode);
		Logger.d(TAG, "p = " + p);
		ret = TVNative.startScanDvbc_native(scanMode, p);
		Logger.d(TAG, "Leave  startScan_dvbc_proxy ");
		return ret;
	}

	public int cancelScan_dvbc_proxy(String scanMode) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  cancelScanDVBC_proxy ");
		Logger.d(TAG, "scanMode = " + scanMode);
		ret = TVNative.cancelScanDvbc_native(scanMode);
		Logger.d(TAG, "Leave  cancelScanDVBC_proxy ");
		return ret;
	}

	public int getDefaultSymRate_proxy(String countryCode) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getDefaultSymRate_proxy ");
		ret = TVNative.getDefaultSymRate_native(countryCode);
		Logger.d(TAG, "Leave  getDefaultSymRate_proxy ");
		return ret;
	}

	public int getDefaultFrequency_proxy(String countryCode) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getDefaultFrequency_proxy ");
		ret = TVNative.getDefaultFrequence_native(countryCode);
		Logger.d(TAG, "Leave  getDefaultFrequency_proxy ");
		return ret;
	}

	public int getDefaultEMod_proxy(String countryCode) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getDefaultEMod_proxy ");
		ret = TVNative.getDefaultEMod_native(countryCode);
		Logger.d(TAG, "Leave  getDefaultEMod_proxy ");
		return ret;
	}

	public int getDefaultNwID_proxy(String countryCode) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getDefaultNwID_proxy ");
		ret = TVNative.getDefaultNwID_native(countryCode);
		Logger.d(TAG, "Leave  getDefaultNwID_proxy ");
		return ret;
	}

    /*For dtmb*/
    public int startScan_dtmb_proxy(String scanMode, ScanParaDtmb p) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  startScan_dtmb_proxy");
		Logger.d(TAG, "scanMode = " + scanMode);
		Logger.d(TAG, "p = " + p);
		ret = TVNative.startScanDtmb_native(scanMode, p);
		Logger.d(TAG, "Leave  startScan_dtmb_proxy ");
		return ret;
	}

	public int cancelScan_dtmb_proxy(String scanMode) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  cancelScanDtmb_proxy ");
		Logger.d(TAG, "scanMode = " + scanMode);
		ret = TVNative.cancelScanDtmb_native(scanMode);
		Logger.d(TAG, "Leave  cancelScanDtmb_proxy ");
		return ret;
	}

    
    public int getDtmbFreqRange_proxy(String scanMode,DtmbFreqRange dtmbFreqRange) throws RemoteException {
            int ret = -1;
            Logger.d(TAG, "Enter  getDtmbFreqBound_proxy ");
            Logger.d(TAG, "scanMode = " + scanMode);
            ret = TVNative.getDtmbFreqRange_native(scanMode,dtmbFreqRange);
            Logger.d(TAG, "Leave  getDtmbFreqBound_proxy ");
            return ret;
        }

    public int getFirstDtmbScanRF_proxy(String scanMode, DtmbScanRF firstRF) throws RemoteException {
            int ret = -1;
            Logger.d(TAG, "Enter  getFirstDtmbScanRF_proxy ");
            Logger.d(TAG, "scanMode = " + scanMode);
            ret = TVNative.getFirstDtmbScanRF_native(scanMode, firstRF);
            Logger.d(TAG, "Leave  getFirstDtmbScanRF_proxy ");
            return ret;
        }
    
    public int getLastDtmbScanRF_proxy(String scanMode, DtmbScanRF lastRF) throws RemoteException {
            int ret = -1;
            Logger.d(TAG, "Enter  getLastDtmbScanRF_proxy ");
            Logger.d(TAG, "scanMode = " + scanMode);
            ret = TVNative.getLastDtmbScanRF_native(scanMode, lastRF);
            Logger.d(TAG, "Leave  getLastDtmbScanRF_proxy ");
            return ret;
        }

    public int getNextDtmbScanRF_proxy(String scanMode, DtmbScanRF currRF, DtmbScanRF nextRF) throws RemoteException {
            int ret = -1;
            Logger.d(TAG, "Enter  getNextDtmbScanRF_proxy ");
            Logger.d(TAG, "scanMode = " + scanMode);
            ret = TVNative.getNextDtmbScanRF_native(scanMode, currRF, nextRF);
            Logger.d(TAG, "Leave  getNextDtmbScanRF_proxy ");
            return ret;
        }

    public int getPrevDtmbScanRF_proxy(String scanMode, DtmbScanRF currRF, DtmbScanRF prevRF) throws RemoteException {
            int ret = -1;
            Logger.d(TAG, "Enter  getPrevDtmbScanRF_proxy ");
            Logger.d(TAG, "scanMode = " + scanMode);
            ret = TVNative.getPrevDtmbScanRF_native(scanMode, currRF, prevRF);
            Logger.d(TAG, "Leave  getPrevDtmbScanRF_proxy ");
            return ret;
        }

    public int getCurrentDtmbScanRF_proxy(String scanMode, int channelId, DtmbScanRF currRF) throws RemoteException {
            int ret = -1;
            Logger.d(TAG, "Enter  getCurrentDtmbScanRF_proxy ");
            Logger.d(TAG, "scanMode = " + scanMode);
            ret = TVNative.getCurrentDtmbScanRF_native(scanMode, channelId, currRF);
            Logger.d(TAG, "Leave  getCurrentDtmbScanRF_proxy ");
            return ret;
        }

	public int dtSetConfig_proxy(int configFlag) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  dtSetConfig_proxy ");
		ret = TVNative.dtSetConfig_native(configFlag);
		Logger.d(TAG, "Leave  dtSetConfig_proxy ");
		return ret;
	}

	public int dtSetDst_proxy(boolean bEnable) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  dtSetDst_proxy ");
		ret = TVNative.dtSetDst_native(bEnable);
		Logger.d(TAG, "Leave  dtSetDst_proxy ");
		return ret;
	}
	public int dtSetTz_proxy(long tzOffset) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  dtSetTz_proxy ");
		ret = TVNative.dtSetTz_native(tzOffset);
		Logger.d(TAG, "Leave  dtSetTz_proxy ");
		return ret;
	}
	public int dtSetUtc_proxy(long sec, int milliSec) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  dtSetUtc_proxy ");
		ret = TVNative.dtSetUtc_native(sec, milliSec);
		Logger.d(TAG, "Leave  dtSetUtc_proxy ");
		return ret;
	}
	public int dtSetDstCtrl_proxy(boolean bEnable) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  dtSetDstCtrl_proxy ");
		ret = TVNative.dtSetDstCtrl_native(bEnable);
		Logger.d(TAG, "Leave  dtSetDstCtrl_proxy ");
		return ret;
	}
	public int dtSetDsChange_proxy(long changeTime) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  dtSetDsChange_proxy ");
		ret = TVNative.dtSetDsChange_native(changeTime);
		Logger.d(TAG, "Leave  dtSetDsChange_proxy ");
		return ret;
	}
	public int dtSetDsOffset_proxy(long OffsetTime) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  dtSetDsOffset_proxy ");
		ret = TVNative.dtSetDsOffset_native(OffsetTime);
		Logger.d(TAG, "Leave  dtSetDsOffset_proxy ");
		return ret;
	}
	public int dtSetSyncSrc_proxy(int eSyncSrcType, int eSrcDescType, String data) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  dtSetSyncSrc_proxy ");
		ret = TVNative.dtSetSyncSrc_native(eSyncSrcType, eSrcDescType, data);
		Logger.d(TAG, "Leave  dtSetSyncSrc_proxy ");
		return ret;
	}
	public int dtSetTzCtrl_proxy(boolean bEnable) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  dtSetTzCtrl_proxy ");
		ret = TVNative.dtSetTzCtrl_native(bEnable);
		Logger.d(TAG, "Leave  dtSetTzCtrl_proxy ");
		return ret;
	}
	public int dtSetSysCountCode_proxy(byte[] countCode, int regionId) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  dtSetSysCountCode_proxy ");
		ret = TVNative.dtSetSysCountCode_native(countCode, regionId);
		Logger.d(TAG, "Leave  dtSetSysCountCode_proxy ");
		return ret;
	}
	public boolean dtGetDst_proxy() throws RemoteException {
		Logger.d(TAG, "Enter  dtGetDst_proxy ");
		return TVNative.dtGetDst_native();
	}
	public long dtGetGps_proxy(int[] data) throws RemoteException {
		Logger.d(TAG, "Enter  dtGetGps_proxy ");
		return TVNative.dtGetGps_native(data);
	}
	public long dtGetTz_proxy() {
		Logger.d(TAG, "Enter  dtGetTz_proxy ");
		return TVNative.dtGetTz_native();
	}
	public long dtGetUtc_proxy(int[] data) throws RemoteException {
		Logger.d(TAG, "Enter  dtGetUtc_proxy ");
		return TVNative.dtGetUtc_native(data);
	}
	public long dtGetBrdcstUtc_proxy(int[] data) throws RemoteException {
		Logger.d(TAG, "Enter  dtGetBrdcstUtc_proxy ");
		return TVNative.dtGetBrdcstUtc_native(data);
	}
	public int dtGetCountCode_proxy(int index, byte[] countCode, long[] data) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  dtGetCountCode_proxy ");
		ret = TVNative.dtGetCountCode_native(index, countCode, data);
		Logger.d(TAG, "Leave  dtGetCountCode_proxy ");
		return ret;
	}
	public boolean dtGetDstCtrl_proxy() throws RemoteException {
		Logger.d(TAG, "Enter  dtGetDstCtrl_proxy ");
		return TVNative.dtGetDstCtrl_native();
	}
	public long dtGetDsChange_proxy() throws RemoteException {
		Logger.d(TAG, "Enter  dtGetDsChange_proxy ");
		return TVNative.dtGetDsChange_native();
	}
	public long dtGetDsOffset_proxy() throws RemoteException {
		Logger.d(TAG, "Enter  dtGetDsOffset_proxy ");
		return TVNative.dtGetDsOffset_native();
	}
	public boolean dtGetTzCtrl_proxy() throws RemoteException {
		Logger.d(TAG, "Enter  dtGetTzCtrl_proxy ");
		return TVNative.dtGetTzCtrl_native();
	}
	public int dtGetNumCountCode_proxy() throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  dtGetNumCountCode_proxy ");
		ret = TVNative.dtGetNumCountCode_native();
		Logger.d(TAG, "Leave  dtGetNumCountCode_proxy ");
		return ret;
	}
	public int dtGetSysCountCode_proxy(byte[] countCode, int[] data) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  dtGetSysCountCode_proxy ");
		ret = TVNative.dtGetSysCountCode_native(countCode, data);
		Logger.d(TAG, "Leave  dtGetSysCountCode_proxy ");
		return ret;
	}
	public byte dtGetLastSyncTblId_proxy() throws RemoteException {
		Logger.d(TAG, "Enter  dtGetLastSyncTblId_proxy ");
		return TVNative.dtGetLastSyncTblId_native();
	}
	public int dtCheckInputTime_proxy(boolean bEnable) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  dtCheckInputTime_proxy ");
		ret = TVNative.dtCheckInputTime_native(bEnable);
		Logger.d(TAG, "Leave  dtCheckInputTime_proxy ");
		return ret;
	}
	public int dtConfigCheckInputTime_proxy(int eSetType, int setValue) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  dtConfigCheckInputTime_proxy ");
		ret = TVNative.dtConfigCheckInputTime_native(eSetType, setValue);
		Logger.d(TAG, "Leave  dtConfigCheckInputTime_proxy ");
		return ret;
	}

	public int dt_utc_sec_to_dtg_proxy(long utcTime, DtDTG dtgTime)
	throws RemoteException
	{
		int ret;

		ret = TVNative.DtUtcSecToDtg_native(utcTime, dtgTime);

		return ret;
	}

	public int dt_utc_sec_to_loc_dtg_proxy(long utcTime, DtDTG dtgTime)
	throws RemoteException
	{
		int ret;

		ret = TVNative.DtUtcSecToLocDtg_native(utcTime, dtgTime);
		return ret;
	}

	public int dt_conv_utc_local_proxy(DtDTG dtgTimeIn, DtDTG dtgTimeOut) 
	throws RemoteException
	{
		int ret;

		ret = TVNative.DtConvUtcLocal_native(dtgTimeIn, dtgTimeOut);
		return ret;
	}

	public long dt_dtg_to_sec_proxy(DtDTG dtgTime)
	throws RemoteException
	{
		return TVNative.DtDtgToSec_native(dtgTime);
	}

	public long dt_gps_sec_to_utc_sec_proxy(long gpsSec)
	throws RemoteException
	{
		return TVNative.DtGpsSecToUtcSec_native(gpsSec);
	}

	public int dt_bcd_to_sec_proxy(String bcdTime)
	throws RemoteException
	{
		return TVNative.DtBcdToSec_native(bcdTime);
	}

	public int dt_mjd_bcd_to_dtg_proxy(String bcdTime, DtDTG dtgTime)
	throws RemoteException
	{
		int ret;

		ret = TVNative.DtMjdBcdToDtg_native(bcdTime, dtgTime);
		return ret;
	}

	public int dt_mjd_to_dtg_proxy(long mjdTime, DtDTG dtgTime)
	throws RemoteException
	{
		int ret;
		ret = TVNative.DtMjdToDtg_native(mjdTime, dtgTime);
		return ret;
	}

	public long dt_dtg_to_mjd_proxy(DtDTG dtgTime)
	throws RemoteException
	{
		return TVNative.DtDtgToMjd_native(dtgTime);
	}

	public int dt_dtg_to_mjd_bcd_proxy(DtDTG dtgTime, int[] mjdInfo)
	throws RemoteException
	{
		int ret;
		ret = TVNative.DtDtgToMjdBcd_native(dtgTime, mjdInfo);
		return ret;
	}

	public long dt_diff_proxy(DtDTG dtgTimeFrom, DtDTG dtgTimeTo)
	throws RemoteException
	{
		return TVNative.DtDiff_native(dtgTimeFrom, dtgTimeTo);
	}

	public int dt_add_proxy(DtDTG dtgTimeOld, long addSec, DtDTG dtgTimeNew)
	throws RemoteException
	{
		int ret;
		ret = TVNative.DtAdd_native(dtgTimeOld, addSec, dtgTimeNew);
		return ret;
	}

	public boolean dt_is_leap_year_proxy(long year)
	throws RemoteException
	{
		return TVNative.DtIsLeapYear_native(year);
	}

	public int dt_reg_nfy_fct_proxy(long[] t_handle)
	throws RemoteException
	{
		int ret;

		ret = TVNative.DtRegNfyFct_native(t_handle);

		return ret;
	}

	public int inputServiceBind_proxy(int output, int inputId) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  bind_proxy ");
		Logger.d(TAG, "output = " + output);
		Logger.d(TAG, "inputId = " + inputId);
		ret = TVNative.inputServiceBind_native(output, inputId);
		Logger.d(TAG, "Leave  bind_proxy ");
		return ret;
	}

	public int inputServiceSwap_proxy(int output1, int output2) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  swap_proxy ");
		Logger.d(TAG, "output1 = " + output1);
		Logger.d(TAG, "output2 = " + output2);
		ret = TVNative.inputServiceSwap_native(output1, output2);
		Logger.d(TAG, "Leave  swap_proxy ");
		return ret;
	}

	public int setScreenOutputRect_proxy(int output, int left, int right, int top, int bottom) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  setScreenOutputRect_proxy ");
		Logger.d(TAG, "output = " + output);
		Logger.d(TAG, "left = " + left);
		Logger.d(TAG, "right = " + right);
		Logger.d(TAG, "top = " + top);
		Logger.d(TAG, "bottom = " + bottom);
		ret = TVNative.setScreenOutputRect_native(output, left, right, top, bottom);
		Logger.d(TAG, "Leave  setScreenOutputRect_proxy ");
		return ret;
	}

	public int inputServiceGetRecord_proxy(int index, InputRecord inputRecord) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  inputServiceGetRecord_proxy ");
		Logger.d(TAG, "index = " + index);
		Logger.d(TAG, "inputRecord = " + inputRecord);
		ret = TVNative.inputServiceGetRecord_native(index, inputRecord);
		Logger.d(TAG, "Leave  inputServiceGetRecord_proxy ");
		return ret;
	}

	public int inputServiceSetOutputMute(int output, boolean mute) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  inputServiceSetOutputMute ");
		Logger.d(TAG, "index = " + output);
		Logger.d(TAG, "inputRecord = " + mute);
		ret = TVNative.inputServiceSetOutputMute(output, mute);
		Logger.d(TAG, "Leave  inputServiceSetOutputMute ");
		return ret;
	}

	public int setScreenOutputVideoRect_proxy(int output, int left, int right, int top, int bottom)
	        throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  setScreenOutputVideoRect_proxy ");
		Logger.d(TAG, "output = " + output);
		Logger.d(TAG, "left = " + left);
		Logger.d(TAG, "right = " + right);
		Logger.d(TAG, "top = " + top);
		Logger.d(TAG, "bottom = " + bottom);
		ret = TVNative.setScreenOutputVideoRect_native(output, left, right, top, bottom);
		Logger.d(TAG, "Leave  setScreenOutputVideoRect_proxy ");
		return ret;
	}

	public int getScreenOutputRect_proxy(int output, InputRegion inputRegion) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getScreenOutputRect_proxy ");
		Logger.d(TAG, "output = " + output);
		Logger.d(TAG, "inputRegion = " + inputRegion);
		ret = TVNative.getScreenOutputRect_native(output, inputRegion);
		Logger.d(TAG, "Leave  setScreenOutputVideoRect_proxy ");
		return ret;
	}

	public int getScreenOutputVideoRect_proxy(int output, InputRegion inputRegion) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  getScreenOutputVideoRect_proxy ");
		Logger.d(TAG, "output = " + output);
		Logger.d(TAG, "inputRegion = " + inputRegion);
		ret = TVNative.getScreenOutputVideoRect_native(output, inputRegion);
		Logger.d(TAG, "Leave  getScreenOutputVideoRect_proxy ");
		return ret;
	}

	public int inputSourceExchangeData_proxy(int[] inputSourceData) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  inputSourceExchangeData_proxy ");
		Logger.d(TAG, "inputSourceData = " + inputSourceData);
		ret = TVNative.inputSourceExchangeData(inputSourceData);
		Logger.d(TAG, "Leave  inputSourceExchangeData_proxy ");
		return ret;
	}

	public int openUARTSerial_proxy(int uartSerialID, int[] uartSerialSetting, int[] handle) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  openUARTSerial_proxy ");
		Logger.d(TAG, "uartSerialID = " + uartSerialID);
		Logger.d(TAG, "uartSerialSetting = " + uartSerialSetting);
		Logger.d(TAG, "handle = " + handle);
		ret = TVNative.openUARTSerial_native(uartSerialID, uartSerialSetting, handle);
		Logger.d(TAG, "Leave  openUARTSerial_proxy ret = " + ret);
		return ret;
	}

	public int closeUARTSerial_proxy(int handle) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  closeUARTSerial_proxy ");
		Logger.d(TAG, "handle = " + handle);
		ret = TVNative.closeUARTSerial_native(handle);
		Logger.d(TAG, "Leave  closeUARTSerial_proxy ret = " + ret);
		return ret;
	}

	public int getUARTSerialSetting_proxy(int handle, int[] uartSerialSetting) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  getUARTSerialSetting_proxy ");
		Logger.d(TAG, "handle = " + handle);
		Logger.d(TAG, "uartSerialSetting = " + uartSerialSetting);
		ret = TVNative.getUARTSerialSetting_native(handle, uartSerialSetting);
		Logger.d(TAG, "Leave  getUARTSerialSetting_proxy ret = " + ret);
		return ret;
	}

	public int getUARTSerialOperationMode_proxy(int handle, int[] operationMode) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  getUARTSerialOperationMode_proxy ");
		Logger.d(TAG, "handle = " + handle);
		Logger.d(TAG, "operationMode = " + operationMode);
		ret = TVNative.getUARTSerialOperationMode_native(handle, operationMode);
		Logger.d(TAG, "Leave  getUARTSerialOperationMode_proxy ret = " + ret);
		return ret;
	}

	public int setUARTSerialSetting_proxy(int handle, int[] uartSerialSetting) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  setUARTSerialSetting_proxy ");
		Logger.d(TAG, "handle = " + handle);
		Logger.d(TAG, "uartSerialSetting = " + uartSerialSetting);
		ret = TVNative.setUARTSerialSetting_native(handle, uartSerialSetting);
		Logger.d(TAG, "Leave  setUARTSerialSetting_proxy ");
		return ret;
	}

	public int setUARTSerialOperationMode_proxy(int handle, int operationMode) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  setUARTSerialOperationMode_proxy ");
		Logger.d(TAG, "handle = " + handle);
		Logger.d(TAG, "operationMode = " + operationMode);
		ret = TVNative.setUARTSerialOperationMode_native(handle, operationMode);
		Logger.d(TAG, "Leave  setUARTSerialOperationMode_proxy ret = " + ret);
		return ret;
	}

	public int setUARTSerialMagicString_proxy(int handle, byte[] uartSerialMagicSetting) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  setUARTSerialMagicString_proxy ");
		Logger.d(TAG, "handle = " + handle);
		Logger.d(TAG, "uartSerialMagicSetting = " + uartSerialMagicSetting);
		ret = TVNative.setUARTSerialMagicString_native(handle, uartSerialMagicSetting);
		Logger.d(TAG, "Leave  setUARTSerialMagicString_proxy ret = " + ret);
		return ret;
	}

	public int outputUARTSerial_proxy(int handle, byte[] uartSerialData) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  outputUARTSerial_proxy ");
		Logger.d(TAG, "handle = " + handle);
		Logger.d(TAG, "uartSerialData = " + uartSerialData);
		ret = TVNative.outputUARTSerial_native(handle, uartSerialData);
		Logger.d(TAG, "Leave  outputUARTSerial_proxy ret = " + ret);
		return ret;
	}

	public int getCfgMinMax_proxy(String configType, ConfigValue configValue) throws RemoteException {
		int ret;
		Logger.d(TAG, "Enter  getCfgMinMax_proxy ");
		Logger.d(TAG, "configType = " + configType);
		Logger.d(TAG, "configValue = " + configValue);
		ret = TVNative.getCfgMinMax_native(configType, configValue);
		Logger.d(TAG, "Leave  getCfgMinMax_proxy ret = " + ret);
		return ret;
	}

	public boolean setOSDColorKey_proxy(boolean enable, int colorkey) throws RemoteException {
		boolean ret = false;
		Logger.d(TAG, "Enter  setOSDColorKey_proxy ");
		Logger.d(TAG, "enable = " + enable);
		Logger.d(TAG, "colorkey = " + colorkey);
		ret = TVNative.setOSDColorKey_native(enable, colorkey);
		Logger.d(TAG, "Leave  setOSDColorKey_proxy ");
		return ret;
	}

	public boolean setOSDOpacity_proxy(int opacity) throws RemoteException {
		boolean ret = false;
		Logger.d(TAG, "Enter  setOSDOpacity_proxy ");
		Logger.d(TAG, "opacity = " + opacity);
		ret = TVNative.setOSDOpacity_native(opacity);
		Logger.d(TAG, "Leave  setOSDOpacity_proxy ");
		return ret;
	}

	public int eventServiceSetCommand_proxy(EventCommand eventCommand) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  eventServiceSetCommand_proxy ");
		Logger.d(TAG, "eventCommand = " + eventCommand);
		ret = TVNative.eventSetCommand(eventCommand);
		Logger.d(TAG, "Leave  eventServiceSetCommand_proxy ");
		return ret;
	}

	public int eventServiceGetPFEvents_proxy(DvbChannelInfo channelInfo, List events) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  eventServiceGetPFEvents_proxy ");
		Logger.d(TAG, "channelInfo = " + channelInfo);
		Logger.d(TAG, "events = " + events);
		ret = TVNative.getPFEvents(channelInfo, events);
		Logger.d(TAG, "Leave  eventServiceGetPFEvents_proxy ");
		return ret;
	}

	public int eventServiceGetScheduleEvents_proxy(DvbChannelInfo channel, long startTime, long endTime, List events)
	        throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  eventServiceGetScheduleEvents_proxy ");
		Logger.d(TAG, "channels = " + channel);
		Logger.d(TAG, "startTime = " + startTime);
		Logger.d(TAG, "endTime = " + endTime);
		Logger.d(TAG, "events = " + events);
		ret = TVNative.getScheduleEvents(channel, startTime, endTime, events);
		Logger.d(TAG, "Leave  eventServiceGetScheduleEvents_proxy ");
		return ret;
	}

	public int getSlotNum_proxy() throws RemoteException {
		int ret = 0;
		Logger.d(TAG, "Enter  getSlotNum_proxy ");
		ret = TVNative.getSlotNum_native();
		Logger.d(TAG, "Leave  getSlotNum_proxy ");
		return ret;
	}

	public boolean isSlotActive_proxy(int slotId) throws RemoteException {
		boolean ret = false;
		Logger.d(TAG, "Enter  isSlotActive_proxy ");
		Logger.d(TAG, "slotId = " + slotId);
		ret = TVNative.isSlotActive_native(slotId);
		Logger.d(TAG, "Leave  isSlotActive_proxy ");
		return ret;
	}

	public int enterMMI_proxy(int slotId) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  enterMMI_proxy ");
		Logger.d(TAG, "slotId = " + slotId);
		ret = TVNative.enterMMI_native(slotId);
		Logger.d(TAG, "Leave  enterMMI_proxy ");
		return ret;
	}

	public String getCamName_proxy(int slotId) throws RemoteException {
		String camName = "";
		Logger.d(TAG, "Enter  getCamName_proxy ");
		Logger.d(TAG, "slotId = " + slotId);
		camName = TVNative.getCamName_native(slotId);
		Logger.d(TAG, "Leave  getCamName_proxy ");
		return camName;
	}

	public int[] getCamSystemIDInfo_proxy(int slotId) throws RemoteException {
		int[] arr_id;
		Logger.d(TAG, "Enter  getCamSystemIDInfo_proxy ");
		Logger.d(TAG, "slotId = " + slotId);
		arr_id = TVNative.getCamSystemIDInfo_native(slotId);
		Logger.d(TAG, "Leave  getCamSystemIDInfo_proxy ");
		return arr_id;
	}

	public int closeMMI_proxy(int slotId) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  closeMMI_proxy ");
		Logger.d(TAG, "slotId = " + slotId);
		ret = TVNative.closeMMI_native(slotId);
		Logger.d(TAG, "Leave  closeMMI_proxy ");
		return ret;
	}

	public int setMMIClosed_proxy(int slotId) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  setMMIClosed_proxy ");
		Logger.d(TAG, "slotId = " + slotId);
		ret = TVNative.setMMIClosed_native(slotId);
		Logger.d(TAG, "Leave  setMMIClosed_proxy ");
		return ret;
	}

	public int answerMMIMenu_proxy(int slotId, int menuId, char answerItem) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  answerMMIMenu_proxy ");
		Logger.d(TAG, "slotId = " + slotId);
		ret = TVNative.answerMMIMenu_native(slotId, menuId, answerItem);
		Logger.d(TAG, "Leave  answerMMIMenu_proxy ");
		return ret;
	}

	public int answerMMIEnq_proxy(int slotId, int enqId, boolean answer, String answerData) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  answerMMIEnq_proxy ");
		Logger.d(TAG, "slotId = " + slotId);
		ret = TVNative.answerMMIEnq_native(slotId, enqId, answer, answerData);
		Logger.d(TAG, "Leave  answerMMIEnq_proxy ");
		return ret;
	}

	public int askRelease_proxy(int slotId) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  askRelease_proxy ");
		Logger.d(TAG, "slotId = " + slotId);
		ret = TVNative.askRelease_native(slotId);
		Logger.d(TAG, "Leave  askRelease_proxy ");
		return ret;
	}

	public int setCITsPath_proxy(int slotId, boolean b_switch) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  setCITsPath_proxy ");
		Logger.d(TAG, "slotId = " + slotId);
		ret = TVNative.setCITsPath_native(slotId, b_switch);
		Logger.d(TAG, "Leave  setCITsPath_proxy ");
		return ret;
	}

	public int setCIInputDTVPath_proxy(int slotId, boolean b_switch) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter  setCIInputDTVPath_proxy ");
		Logger.d(TAG, "slotId = " + slotId);
		ret = TVNative.setCIInputDTVPath_native(slotId, b_switch);
		Logger.d(TAG, "Leave  setCIInputDTVPath_proxy ");
		return ret;
	}

	public int getTunedChannel_proxy(int svlId, HostControlTune tune) throws RemoteException {
  	    int ret = -1;
		Logger.d(TAG, "Enter  getTunedChannel_native ");
		Logger.d(TAG, "svlId = " + svlId);
		ret = TVNative.getTunedChannel_native(svlId,tune);
		Logger.d(TAG, "Leave  getTunedChannel_native ");
		return ret;
	}

	public int getDvbcScanTypeNum_proxy(String scanMode,DvbcProgramType dvbcScanData) throws RemoteException {
  	    int ret = -1;
		Logger.d(TAG, "Enter  get_dvbc_scanTypeNum_proxy ");
		Logger.d(TAG, "scanMode = " + scanMode);
		ret = TVNative.getDvbcScanTypeNum_native(scanMode,dvbcScanData);
		Logger.d(TAG, "Leave  get_dvbc_scanTypeNum_proxy ");
		return ret;
	}
	
	public int getDvbcFreqRange_proxy(String scanMode,DvbcFreqRange dvbcFreqRange) throws RemoteException {
  	    int ret = -1;
		Logger.d(TAG, "Enter  getDvbcFreqBound_proxy ");
		Logger.d(TAG, "scanMode = " + scanMode);
		ret = TVNative.getDvbcFreqRange_native(scanMode,dvbcFreqRange);
		Logger.d(TAG, "Leave  getDvbcFreqBound_proxy ");
		return ret;
	}

	public int activateComponent_proxy(String comp) throws RemoteException{
		int ret = -1;
		Logger.d(TAG, "Enter  activateComponent_proxy ");
		Logger.d(TAG, "Component Name = " + comp);
		ret = TVNative.activateComponent_native(comp);
		Logger.d(TAG, "Leave  activateComponent_proxy ");
		return ret;
	}
	public int inactivateComponent_proxy(String comp)throws RemoteException{
		int ret = -1;
		Logger.d(TAG, "Enter  inactivateComponent_proxy ");
		Logger.d(TAG, "Component Name = " + comp);
		ret = TVNative.inactivateComponent_native(comp);
		Logger.d(TAG, "Leave  inactivateComponent_proxy ret = " + ret);
		return ret;
	}
	
	public int updateSysStatus_proxy(String statusDesc)
	{
		int ret = -1;
		Logger.d(TAG, "Enter  updateSysStatus_proxy ");
		Logger.d(TAG, " Statusdesc = " + statusDesc);
	 	ret = TVNative.updateSysStatus_native(statusDesc);
		Logger.d(TAG, "Leave  updateSysStatus_proxy ret = " + ret);
		return ret;
	}
	public boolean isTTXAvail_proxy()
	{
		boolean ret = false;
		Logger.d(TAG, "Enter  isTTXAvail_proxy ");		
	 	ret = TVNative.IsTTXAvail_native();
		Logger.d(TAG, "Leave  isTTXAvail_proxy ret = " + ret);
		return ret;
	}

	public int sendkeyEventtoComp_proxy(int keycode, int ui4_keyevent)
	{
		int ret = -1;
		Logger.d(TAG, "Enter sendkeyEventtoComp_proxy ");
		Logger.d(TAG,  " keycode = " + keycode + " keyevent = " + ui4_keyevent);
	 	ret = TVNative.sendkeyEventtoComp_native(keycode, ui4_keyevent);
		Logger.d(TAG, "Leave  sendkeyEventtoComp_proxy ret = " + ret);
		return ret;
	}

	public int lockDigitalTuner_proxy(int frequency) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter lockDigitalTuner ");
		Logger.d(TAG,  " frequency = " + frequency);
	 	ret = TVNative.lockDigitalTuner_native(frequency);
		Logger.d(TAG, "Leave  lockDigitalTuner_proxy ret = " + ret);
		return ret;
	}
	
	public int unlockDigitalTuner_proxy(int magicID) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter unlockDigitalTuner_proxy ");
		Logger.d(TAG,  " magicID = " + magicID);
	 	ret = TVNative.unlockDigitalTuner_native(magicID);
		Logger.d(TAG, "Leave  unlockDigitalTuner_proxy ret = " + ret);
		return ret;
	}

	public int getCurrentDTVAudioCodec_proxy() throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter getCurrentDTVAudioCodec_proxy ");
	 	ret = TVNative.getCurrentDTVAudioCodec_native();
		Logger.d(TAG, "Leave getCurrentDTVAudioCodec_proxy ret = " + ret);
		return ret;
	}

	public int getCurrentDTVVideoCodec_proxy() throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "Enter getCurrentDTVVideoCodec_proxy ");
	 	ret = TVNative.getCurrentDTVVideoCodec_native();
		Logger.d(TAG, "Leave getCurrentDTVVideoCodec_proxy ret = " + ret);
		return ret;
	}
    public void setMuteState_proxy(boolean b_mute)  throws RemoteException {
		Log.i(TAG, "Enter setMuteState_proxy");
		try {
			if (mContext != null) {
				Log.i(TAG, "setMuteState VALUE:" + b_mute);
				TVManager tvMng = TVManager.getInstance(mContext);
				ConfigService rawSrv = (ConfigService) tvMng
						.getService(ConfigService.ConfigServiceName);
				ConfigValue rawVal = new ConfigValue();
				rawVal.setIntValue(b_mute ? 1 : 0);
				rawSrv.setCfg(ConfigType.CFG_MUTE_VOLUME_FUNC, rawVal);
				rawSrv.updateCfg(ConfigType.CFG_MUTE_VOLUME_FUNC);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public boolean getMuteState_proxy()  throws RemoteException {
		Log.i(TAG, "Enter getMuteState_proxy");
		boolean nRet = false;
		try {
			if (mContext != null) {
				TVManager tvMng = TVManager.getInstance(mContext);
				ConfigService rawSrv = (ConfigService) tvMng
						.getService(ConfigService.ConfigServiceName);
				ConfigValue rawVal = rawSrv.getCfg(ConfigType.CFG_MUTE_VOLUME_FUNC);
				nRet = (rawVal.getIntValue() == 1) ? true : false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return nRet;
	}
			
	public int getDvbcMainFrequence_proxy(String scanMode,MainFrequence mainFrequence) throws RemoteException {
  	    int ret = -1;
		Logger.d(TAG, "Enter  getDvbcMainFrequence_proxy ");
		Logger.d(TAG, "scanMode = " + scanMode);
		ret = TVNative.getDvbcMainFrequence_native(scanMode,mainFrequence);
		Logger.d(TAG, "Leave  getDvbcMainFrequence_proxy ");
		return ret;
	}
	
}
