package com.mediatek.tvcommon;

import java.util.ArrayList;
import java.util.List;

import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.common.TVCommon;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.DvbChannelInfo;
import com.mediatek.tv.model.ScanParaDtmb;
import com.mediatek.tv.model.ScanParaDvbc;
import com.mediatek.tv.model.ScanParams;
import com.mediatek.tv.service.ChannelService;
import com.mediatek.tv.service.ScanService;
import com.mediatek.tvcommon.TVScanner.ScannerListener;

class DTVScanner extends ScanTask {
	private int freq = -1;
	private int index;
	private TVScanner mScanner;

	DTVScanner(TVScanner tvScanner) {
		mScanner = tvScanner;
		mContext = mScanner.mContext;
		scanService = mScanner.scanService;
		channelService = mScanner.channelService;
		brdSrv = mScanner.brdSrv;
		cfg = mScanner.cfg;
	}

	void setFreq(int freq) {
		this.freq = freq;
	}

	void setIndex(int index) {
		this.index = index;
	}

	@Override
	public boolean scan(ScannerListener listener) {
		try {
			brdSrv.syncStopService();
		} catch (TVMException e1) {
			e1.printStackTrace();
		}

		state = TVScanner.STATE_SCANNING;
		type = ScanTask.TYPE_DTV;
		ScanParams para = null;
		@SuppressWarnings("unchecked")
		TVOption<Integer> option = (TVOption<Integer>) cfg
				.getOption(IntegerOption.CFG_BS_SRC);
		if (option != null) {
		int mode = option.get();
		switch (mode) {
		// dtmb_air
		case ConfigType.BS_SRC_AIR:
			if (freq > 0) {
				para = new ScanParaDtmb("CHN",
						ScanParaDtmb.DTMB_SCAN_TYPE_SINGLE_RF_CHANNEL, 0,
						index, index, freq);
			} else {
				para = new ScanParaDtmb("CHN",
						ScanParaDtmb.DTMB_SCAN_TYPE_FULL_MODE, 0, 0, 0, 0);
			}

			break;

		case ConfigType.BS_SRC_CABLE:
			OperatorNameOption operatorOpt = (OperatorNameOption) mScanner
					.getOption(TVScanner.SCAN_OPTION_OPERATOR_NAME);
			TVCommonNative.TVLog("freq: " + freq + " index: " + index + " operatorName: " + operatorOpt.getRawOperatorName());
			if (freq > 0 && index < 0) {
				para = new ScanParaDvbc("CHN", operatorOpt.getRawOperatorName(),
						ScanParaDvbc.SB_DVBC_NIT_SEARCH_MODE_QUICK,
						ScanParaDvbc.SB_DVBC_SCAN_TYPE_FULL_MODE, 0, freq,
						ScanParaDvbc.SB_DVBC_SCAN_FREQ_RANGE_END);
			} else if (freq > 0) {
				ScanEModOption eModOpt = (ScanEModOption) mScanner
						.getOption(TVScanner.SCAN_OPTION_SCAN_EMOD);
				SymRateOption symRateOpt = (SymRateOption) mScanner
						.getOption(TVScanner.SCAN_OPTION_SYM_RATE);
				NetworkIDOption networkIDOpt = (NetworkIDOption) mScanner
						.getOption(TVScanner.SCAN_OPTION_NETWOK_ID);
				int validMask = 0;
					if (eModOpt != null && networkIDOpt != null
							&& symRateOpt != null && operatorOpt != null) {
				validMask |= ScanParaDvbc.SB_DVBC_SCAN_INFO_START_FREQ_VALID
						| ScanParaDvbc.SB_DVBC_SCAN_INFO_END_FREQ_VALID
								| eModOpt.getMask()
								| symRateOpt.getMask()
								| networkIDOpt.getMask();

						para = new ScanParaDvbc("CHN", operatorOpt.getRawOperatorName(),
								ScanParaDvbc.SB_DVBC_NIT_SEARCH_MODE_OFF,
								ScanParaDvbc.SB_DVBC_SCAN_TYPE_MANUAL_FREQ, 0,
								validMask, eModOpt.get(), symRateOpt.get(),
								freq, freq, networkIDOpt.get());
					}

			} else {
				para = new ScanParaDvbc("CHN",  
										operatorOpt.getRawOperatorName(),
										ScanParaDvbc.SB_DVBC_NIT_SEARCH_MODE_OFF,
										ScanParaDvbc.SB_DVBC_SCAN_TYPE_FULL_MODE, 
										0, 
										ScanParaDvbc.SB_DVBC_SCAN_FREQ_RANGE_START,
										ScanParaDvbc.SB_DVBC_SCAN_FREQ_RANGE_END);
			}
			break;

		default:
			break;
		}
		}

		if (freq > 0) {
			try {
				List<ChannelInfo> dels = new ArrayList<ChannelInfo>();
			   List<ChannelInfo>  mChannelInfoList =channelService.getChannelList(getNativeDBName());
			   if(mChannelInfoList !=null &&mChannelInfoList.size()>0){
				   for (ChannelInfo info : mChannelInfoList) {
					if (info instanceof DvbChannelInfo
							&& ((DvbChannelInfo) info).getFrequency() == freq) {
						dels.add(info);
					}
				}
				channelService.setChannelList(
						ChannelService.ChannelOperator.DELETE,
						getNativeDBName(), dels);
			   }
			} catch (TVMException e) {
				e.printStackTrace();
			}
		} else {
			try {
				channelService.digitalDBClean(getNativeDBName());
			} catch (TVMException e) {
				e.printStackTrace();
			}
		}
		int ret = scanService.startScan(getRawScanMode(), para,
				new ScanListenerDelegater(listener));
		return ret == 0;
	}

	@Override
	String getRawScanMode() {
		@SuppressWarnings("unchecked")
		TVOption<Integer> option = (TVOption<Integer>) cfg
				.getOption(IntegerOption.CFG_BS_SRC);
		if(option !=null){
			return option.get() == ConfigType.BS_SRC_AIR ? ScanService.SCAN_MODE_DTMB_AIR
					: ScanService.SCAN_MODE_DVB_CABLE;
		}
		return ScanService.SCAN_MODE_DVB_CABLE;
	}

	void preSetCableChs(List<DvbPreSetChannel> channels) {
		byte brdcstMedium = TVCommon.BRDCST_MEDIUM_DIG_CABLE;
		List<ChannelInfo> raws = new ArrayList<ChannelInfo>();
		for (DvbPreSetChannel ch : channels) {
			DvbChannelInfo chInfo = new DvbChannelInfo(ChannelCommon.DB_CABEL);
			chInfo.setBrdcstMedium(brdcstMedium);
			chInfo.setChannelNumber(ch.getChannelNum());
			chInfo.setServiceName(ch.getChannelName());
			chInfo.setFrequency(ch.getFrequency());
			chInfo.setBandWidth(ch.getBindWidth());
			chInfo.setNwId(ch.getNetworkId());
			chInfo.setOnId(ch.getOnId());
			chInfo.setTsId(ch.getTsId());
			chInfo.setProgId(ch.getProgId());
			chInfo.setSymRate(ch.getSymRate());
			chInfo.setMod(ch.getMod());
			raws.add(chInfo);
		}

		prepareScan();
		try {
			channelService.digitalDBClean(ChannelCommon.DB_CABEL);
			channelService.setChannelList(ChannelService.ChannelOperator.APPEND, ChannelCommon.DB_CABEL, raws);
		} catch (TVMException e) {
			e.printStackTrace();
		}

		type = ScanTask.TYPE_DTV;
		preSetChannels(raws);
	}


	void preSetAirChs(List<DvbPreSetChannel> channels) {
		byte brdcstMedium = TVCommon.BRDCST_MEDIUM_DIG_TERRESTRIAL;
		List<ChannelInfo> raws = new ArrayList<ChannelInfo>();
		for (DvbPreSetChannel ch : channels) {
			DvbChannelInfo chInfo = new DvbChannelInfo(ChannelCommon.DB_AIR);
			chInfo.setBrdcstMedium(brdcstMedium);
			chInfo.setChannelNumber(ch.getChannelNum());
			chInfo.setServiceName(ch.getChannelName());
			chInfo.setFrequency(ch.getFrequency());
			chInfo.setBandWidth(ch.getBindWidth());
			chInfo.setNwId(ch.getNetworkId());
			chInfo.setOnId(ch.getOnId());
			chInfo.setTsId(ch.getTsId());
			chInfo.setProgId(ch.getProgId());
			chInfo.setSymRate(ch.getSymRate());
			chInfo.setMod(ch.getMod());
			raws.add(chInfo);
		}

		prepareScan();
		try {
			channelService.digitalDBClean(ChannelCommon.DB_AIR);
			channelService.setChannelList(ChannelService.ChannelOperator.APPEND, ChannelCommon.DB_AIR, raws);
		} catch (TVMException e) {
			e.printStackTrace();
		}

		type = ScanTask.TYPE_DTV;
		preSetChannels(raws);
	}

}
