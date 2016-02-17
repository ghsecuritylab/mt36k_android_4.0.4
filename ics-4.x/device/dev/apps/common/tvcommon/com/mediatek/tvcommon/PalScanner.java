package com.mediatek.tvcommon;

import java.util.ArrayList;
import java.util.List;

import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.common.TVCommon;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.AnalogChannelInfo;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.ScanParaPalSecam;
import com.mediatek.tv.service.ChannelService;
import com.mediatek.tv.service.ScanService;
import com.mediatek.tvcommon.TVScanner.ScannerListener;

class PalScanner extends ScanTask {
	private boolean isUpdate = false;
	private int freqFrom = -1;
	private int freqTo;
	private TVScanner mScanner;

	PalScanner(TVScanner tvScanner) {
		mScanner = tvScanner;
		mContext = mScanner.mContext;
		if(null != mScanner.scanService){
		scanService = mScanner.scanService;
		}
		channelService = mScanner.channelService;
		if(null != mScanner.brdSrv){
		brdSrv = mScanner.brdSrv;
		}
		cfg = mScanner.cfg;
	}

	void setFreqRange(int freqFrom, int freqTo) {
		this.freqFrom = freqFrom;
		this.freqTo = freqTo;
		this.isUpdate = true;
	}

	void setUpdate(boolean update) {
		if (freqFrom != -1) {
			return;
		}
		isUpdate = update;
	}

	String getRawScanMode() {
		/*
		@SuppressWarnings("unchecked")
		TVOption<Integer> option = (TVOption<Integer>) cfg
				.getOption(IntegerOption.CFG_BS_SRC);
				if(option !=null){
						return option.get() == ConfigType.BS_SRC_AIR ? ScanService.SCAN_MODE_PAL_SECAM_TERRESTRIAL
				  : ScanService.SCAN_MODE_PAL_SECAM_CABLE;
				}*/
				return ScanService.SCAN_MODE_PAL_SECAM_CABLE;
	}

	void preSetAnalogChannels(List<PalPreSetChannel> channels) {
		/*byte brdcstMedium = getRawScanMode() == ScanService.SCAN_MODE_PAL_SECAM_TERRESTRIAL ? TVCommon.BRDCST_MEDIUM_ANA_TERRESTRIAL : TVCommon.BRDCST_MEDIUM_ANA_CABLE;*/
		byte brdcstMedium = TVCommon.BRDCST_MEDIUM_ANA_CABLE;
		List<ChannelInfo> raws = new ArrayList<ChannelInfo>();
		for (PalPreSetChannel ch : channels) {
			AnalogChannelInfo chInfo = new AnalogChannelInfo(ChannelCommon.DB_CABEL);
			chInfo.setBrdcstMedium(brdcstMedium);
			chInfo.setChannelNumber(ch.getChannelNum());
			chInfo.setServiceName(ch.getChannelName());
			chInfo.setFrequency(ch.getFrequency());
			chInfo.setColorSys(ch.getColorSys());
			chInfo.setTvSys(ch.getTvSys());
			chInfo.setAudioSys(ch.getAudioSys());
			raws.add(chInfo);
		}

		prepareScan();
		try {
			channelService.analogDBClean(ChannelCommon.DB_CABEL);
			channelService.analogDBClean(ChannelCommon.DB_AIR);
			channelService.setChannelList(ChannelService.ChannelOperator.APPEND, ChannelCommon.DB_CABEL, raws);
		} catch (TVMException e) {
			e.printStackTrace();
		}
		type = ScanTask.TYPE_ATV;
		preSetChannels(raws);
	}

	@Override
	public boolean scan(ScannerListener listener) {
		prepareScan();
		type = ScanTask.TYPE_ATV;
		TvAudioSystemOption tvSystemOption = (TvAudioSystemOption) mScanner
				.getOption(TVScanner.SCAN_OPTION_TV_SYSTEM);
		ColorSystemOption colorSystemOption = (ColorSystemOption) mScanner
				.getOption(TVScanner.SCAN_OPTION_COLOR_SYSTEM);
		ScanParaPalSecam para = new ScanParaPalSecam();
		if (freqFrom > 0) {
			isUpdate = true;
			para.changeScanType(
					ScanParaPalSecam.SB_PAL_SECAM_SCAN_TYPE_RANGE_MODE,
					freqFrom, freqTo);
			para.searchAllChannelInRange(1);
		} else if (isUpdate) {
			para.changeScanType(
					ScanParaPalSecam.SB_PAL_SECAM_SCAN_TYPE_UPDATE_MODE, 0, 0);
		} else {
			para.changeScanType(
					ScanParaPalSecam.SB_PAL_SECAM_SCAN_TYPE_FULL_MODE, 0, 0);
		}

		if (colorSystemOption !=null && colorSystemOption.getRawEnable() == 0) {
			para.needActualColorSystem(true);
		}
		if(colorSystemOption !=null)
		para.designateColorSystem(colorSystemOption.getRawEnable(),
				colorSystemOption.getRawColorSystem());
		if(tvSystemOption !=null){
			para.designateTvSystemAudioSystem(tvSystemOption.getRawEnable(),
					tvSystemOption.getRawTvSystem(),
					tvSystemOption.getRawAudSystem());
		}
		if (!isUpdate) {
			ArrayList<ChannelInfo> dels = new ArrayList<ChannelInfo>();
			try {
				for (ChannelInfo info : channelService
						.getChannelList(getNativeDBName())) {
					if (info instanceof AnalogChannelInfo) {
						dels.add(info);
					}
				}

				channelService.setChannelList(
						ChannelService.ChannelOperator.DELETE,
						getNativeDBName(), dels);
			} catch (TVMException e) {
				e.printStackTrace();
			}
		}

		try {
			/* confirm air svl is null */
			channelService.analogDBClean(ChannelCommon.DB_AIR);
		} catch (TVMException e) {
			e.printStackTrace();
		}

		para.designateName(1, getNativeDBName());

		int ret = scanService.startScan(getRawScanMode(), para,
				new ScanListenerDelegater(listener));

		return ret == 0;
	}

}
