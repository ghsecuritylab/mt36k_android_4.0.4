package com.mediatek.common.capture;


/* Capture information definition *******************************/
public class MtkCaptureLogoSaveInfo
{
	int     e_device_type;
	public final int    	  ui4_logo_id;
	public final byte       ps_path[];
	
	public MtkCaptureLogoSaveInfo(int device_type, int logo_id, byte ps_path[])
	{
		this.e_device_type = device_type;
		this.ui4_logo_id   = logo_id;
		this.ps_path = ps_path;
	}
}