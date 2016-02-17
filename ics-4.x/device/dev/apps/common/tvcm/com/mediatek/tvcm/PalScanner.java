package com.mediatek.tvcm;

import java.util.ArrayList;
import java.util.List;

import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.AnalogChannelInfo;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.ScanParaPalSecam;
import com.mediatek.tv.service.ChannelService;
import com.mediatek.tv.service.ScanService;
import com.mediatek.tvcm.TVScanner.ScannerListener;
import com.mediatek.tvcm.TVScanner.WaitScannerListener;

public class PalScanner extends ScanTask {
	ScanService scanService = null;

	PalScanner(TVScanner scanner, String name, ScannerListener listener) {
		super(scanner, name, listener);
		// TODO Auto-generated constructor stub
	}

	PalScanner(TVScanner scanner, String name, ScannerListener listener,
			WaitScannerListener waitListener) {
		super(scanner, name, listener, waitListener);
	}

	protected String getRawScanMode() {
		if (getType().equals(TVScanner.SCAN_TYPE_PALT)) {
			return ScanService.SCAN_MODE_PAL_SECAM_TERRESTRIAL;
		} else {
			return ScanService.SCAN_MODE_PAL_SECAM_CABLE;
		}

	}

	@Override
	public void cancel() {
		scanService.cancelScan(getRawScanMode());

	}

	@Override
	public boolean scan() {
		TvAudioSystemOption tvSystemOption = (TvAudioSystemOption) scanner
				.getOption(TVScanner.SCAN_OPTION_TV_SYSTEM);
		ColorSystemOption colorSystemOption = (ColorSystemOption) scanner
				.getOption(TVScanner.SCAN_OPTION_COLOR_SYSTEM);
		scanService = (ScanService) scanner.getTVMngr().getService(
				ScanService.ScanServiceName);
		if (scanService != null) {
			ScanParaPalSecam para = new ScanParaPalSecam();
			if (isWait) {
				para.doUserOperationAfterChannelFound(true);
			}
			// Tricky...
			if (freqFrom > 0) {
				isUpdate = true;
				para.changeScanType(
						ScanParaPalSecam.SB_PAL_SECAM_SCAN_TYPE_RANGE_MODE,
						freqFrom, freqTo);
				para.searchAllChannelInRange(1);
			} else if (isUpdate) {
				para.changeScanType(
						ScanParaPalSecam.SB_PAL_SECAM_SCAN_TYPE_UPDATE_MODE, 0,
						0);
			} else {
				para
						.changeScanType(
								ScanParaPalSecam.SB_PAL_SECAM_SCAN_TYPE_FULL_MODE,
								0, 0);
			}

			if (colorSystemOption.getRawEnable() == 0) {
				para.needActualColorSystem(true);
			}
			para.designateColorSystem(colorSystemOption.getRawEnable(),
					colorSystemOption.getRawColorSystem());
			para.designateTvSystemAudioSystem(tvSystemOption.getRawEnable(),
					tvSystemOption.getRawTvSystem(), tvSystemOption
							.getRawAudSystem());

			if (!isUpdate()) {
				TVChannelManager cm = scanner.getContent().getChannelManager();

				ArrayList<ChannelInfo> del = new ArrayList<ChannelInfo>();
				List<ChannelInfo> all;
				try {
					all = cm.rawChSrv.getChannelList(ChannelCommon.DB_ANALOG);
					for (ChannelInfo rawInfo : all) {
						if (rawInfo instanceof AnalogChannelInfo) {
							del.add(rawInfo);
						}
					}
					cm.rawChSrv.setChannelList(
							ChannelService.ChannelOperator.DELETE,
							ChannelCommon.DB_ANALOG, del);
				
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			para.designateName(1, ChannelCommon.DB_ANALOG);
			if (isWait) {
				scanService
						.setAnalogScanUserOperationListener(new ScanUserOperationListenerDelegater(
								waitListener));
			}

			int ret = scanService.startScan(getRawScanMode(), para,
					new scanListenerDelegater(listener));
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

}
