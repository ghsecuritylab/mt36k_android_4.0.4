/**
 * 
 */
package com.mediatekk.tvcm;

import com.mediatek.tv.service.ScanService;

/**
 * @deprecated
 * @author mtk40063
 *
 */
public class ScanModeOption extends TVOptionRange<Integer> {
	public static final int SCAN_CABLE = 0;
	public static final int SCAN_TERRESTRIAL = 1;
	public static final int SCAN_DVB_CABLE = 2;
	public static final int SCAN_END = SCAN_DVB_CABLE;

	
	private static String scan_mode[] = {
			ScanService.SCAN_MODE_PAL_SECAM_CABLE,
			ScanService.SCAN_MODE_PAL_SECAM_TERRESTRIAL,
			ScanService.SCAN_MODE_DVB_CABLE
	};

	@Override
	public Integer getMax() {		
		return SCAN_END;
	}

	@Override
	public Integer getMin() {
		return SCAN_CABLE;
	}

	private int val = SCAN_TERRESTRIAL;

	@Override
	public Integer get() {
		return val;
	}

	@Override
	public boolean set(Integer val) {
		this.val = val.intValue();
		return true;
	}
	
	static String toRawScanMode(int scanMode) {
		return scan_mode[scanMode];
	}


	String getRawScanMode() {
		return scan_mode[val];
	}

}