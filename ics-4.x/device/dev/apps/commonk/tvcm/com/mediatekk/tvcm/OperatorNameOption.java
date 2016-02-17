/**
 * 
 */
package com.mediatekk.tvcm;

import com.mediatek.tv.model.ScanParaDvbc;

public class OperatorNameOption extends TVOptionRange<Integer> {
	public static final int OPERATOR_NAME_OTHERS = 0;
	public static final int OPERATOR_NAME_UPC = 1;
	public static final int OPERATOR_NAME_COMHEM = 2;
	public static final int OPERATOR_NAME_CANAL_DIGITAL = 3;
	public static final int OPERATOR_NAME_TELE2 = 4;
	public static final int OPERATOR_NAME_STOFA = 5;
	public static final int OPERATOR_NAME_YOUSEE = 6;
	public static final int OPERATOR_NAME_ZIGGO = 7;
	public static final int OPERATOR_NAME_UNITYMEDIA = 8;
	public static final int OPERATOR_NAME_NUMERICABLE = 9;
	public static final int OPERATOR_NAME_END = 9;

	static int operator_name_tbl[] = {
			ScanParaDvbc.SB_DVBC_OPERATOR_NAME_OTHERS,
			ScanParaDvbc.SB_DVBC_OPERATOR_NAME_UPC,
			ScanParaDvbc.SB_DVBC_OPERATOR_NAME_COMHEM,
			ScanParaDvbc.SB_DVBC_OPERATOR_NAME_CANAL_DIGITAL,
			ScanParaDvbc.SB_DVBC_OPERATOR_NAME_TELE2,
			ScanParaDvbc.SB_DVBC_OPERATOR_NAME_STOFA,
			ScanParaDvbc.SB_DVBC_OPERATOR_NAME_YOUSEE,
			ScanParaDvbc.SB_DVBC_OPERATOR_NAME_ZIGGO,
			ScanParaDvbc.SB_DVBC_OPERATOR_NAME_UNITYMEDIA,
			ScanParaDvbc.SB_DVBC_OPERATOR_NAME_NUMERICABLE };

	@Override
	public Integer getMax() {
		return OPERATOR_NAME_END;
	}

	@Override
	public Integer getMin() {
		return OPERATOR_NAME_OTHERS;
	}

	private int val = OPERATOR_NAME_OTHERS;

	@Override
	public Integer get() {
		return val;
	}

	@Override
	public boolean set(Integer val) {
		this.val = val.intValue();
		return true;
	}

	int getRawOperatorName() {
		return operator_name_tbl[val];
	}

	int getRawEnable() {
		// This is used for arg0, :)
		if (val == OPERATOR_NAME_OTHERS)
			return 0;
		else
			return 1;
	}
}