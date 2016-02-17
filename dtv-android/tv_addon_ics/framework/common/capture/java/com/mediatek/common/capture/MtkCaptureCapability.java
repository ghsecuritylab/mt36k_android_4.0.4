package com.mediatek.common.capture;

public class MtkCaptureCapability
{
	public boolean b_default;
	public boolean b_default_exist;
	public byte    ui1_cur_logo_index;
	public byte    ui1_nums_logo_slots;
	public short   ui2_logo_valid_tag;
	
	public MtkCaptureCapability(boolean b_default, boolean b_default_exist, byte ui1_nums_logo_slots,
							byte ui1_cur_logo_index, short ui2_logo_valid_tag)
	{
		this.b_default = b_default;
		this.b_default_exist = b_default_exist;
		this.ui1_cur_logo_index = ui1_cur_logo_index;
		this.ui1_nums_logo_slots = ui1_nums_logo_slots;
		this.ui2_logo_valid_tag = ui2_logo_valid_tag;
	}
}