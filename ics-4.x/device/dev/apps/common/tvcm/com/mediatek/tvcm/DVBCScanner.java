package com.mediatek.tvcm;

import java.util.ArrayList;
import java.util.List;

import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.AnalogChannelInfo;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.DvbChannelInfo;
import com.mediatek.tv.model.ScanParaDvbc;
import com.mediatek.tv.service.ScanService;
import com.mediatek.tv.service.ChannelService.ChannelOperator;

public class DVBCScanner extends ScanTask {
	ScanService scanService = null;
	int validMask;
	
	DVBCScanner(TVScanner scanner, TVScanner.ScannerListener listener) {
		super(scanner, TVScanner.SCAN_TYPE_DVBC, listener);
		// TODO Auto-generated constructor stub
	}


	public boolean scan() {
		scanService = (ScanService) scanner.getTVMngr().getService(
				ScanService.ScanServiceName);
		if (scanService != null) {
			ScanParaDvbc para = null;
			OperatorNameOption operatorOpt = (OperatorNameOption) scanner
					.getOption(TVScanner.SCAN_OPTION_OPERATOR_NAME);
			if (freqFrom > 0 && freqTo < 0) {
				para = new ScanParaDvbc("CHN", operatorOpt.get(),
						ScanParaDvbc.SB_DVBC_NIT_SEARCH_MODE_QUICK,
						ScanParaDvbc.SB_DVBC_SCAN_TYPE_FULL_MODE, 0, freqFrom,
						ScanParaDvbc.SB_DVBC_SCAN_FREQ_RANGE_END);
			} else if (freqFrom > 0) {
				List<ChannelInfo> rawList;
				try {
					rawList = cm.rawChSrv
							.getChannelList(ChannelCommon.DB_ANALOG);
					for (ChannelInfo each : rawList) {
						if (each instanceof DvbChannelInfo) {
							if (((DvbChannelInfo) each).getFrequency() == freqFrom) {
								each.setChannelDeleted(false);
								List<ChannelInfo> list = new ArrayList<ChannelInfo>();
								list.add(each);
								cm.rawChSrv.setChannelList(
										ChannelOperator.UPDATE,
										ChannelCommon.DB_ANALOG, list);
							}
						}
					}
					cm.flush();
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ScanEModOption eModOpt = (ScanEModOption) scanner
						.getOption(TVScanner.SCAN_OPTION_SCAN_EMOD);
				SymRateOption symRateOpt = (SymRateOption) scanner
						.getOption(TVScanner.SCAN_OPTION_SYM_RATE);
				NetworkIDOption networkIDOpt = (NetworkIDOption) scanner
						.getOption(TVScanner.SCAN_OPTION_NETWOK_ID);
				validMask |= ScanParaDvbc.SB_DVBC_SCAN_INFO_START_FREQ_VALID
						| ScanParaDvbc.SB_DVBC_SCAN_INFO_END_FREQ_VALID
						| eModOpt.getMask() | symRateOpt.getMask()
						| networkIDOpt.getMask();

				para = new ScanParaDvbc("CHN", operatorOpt.get(),
						ScanParaDvbc.SB_DVBC_NIT_SEARCH_MODE_OFF,
						ScanParaDvbc.SB_DVBC_SCAN_TYPE_MANUAL_FREQ, 0, validMask,
						eModOpt.get(), symRateOpt.get(), freqFrom, freqTo,
						networkIDOpt.get());
			} else {
				para = new ScanParaDvbc("CHN");
			}

			if (!isUpdate()) {
				TVChannelManager cm = scanner.getContent().getChannelManager();

				try {
					cm.rawChSrv.digitalDBClean(ChannelCommon.DB_ANALOG);
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			int ret = scanService.startScan(ScanService.SCAN_MODE_DVB_CABLE,
					para, new scanListenerDelegater(listener));
			TVComponent.TvLog(" scan service scan ret "
					+ new Integer(ret).toString());
			if (ret == 0) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public void cancel() {
				
		scanService.cancelScan(ScanService.SCAN_MODE_DVB_CABLE);		
	}
}
