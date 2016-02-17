package com.mediatek.tv.service;


import com.mediatek.tv.model.CIListener;

public class CIServiceTest implements CIListener{
    public static void main(String[] args) {
    	CIServiceTest test = new CIServiceTest();
    	CIService.getInstance(0).addCIListener(test);
    }   
    public CIServiceTest()
    {
    }
    
	public int camStatusUpdated(byte cam_status) {
		
		System.out.println("java camStatusUpdated() " + cam_status);
		return 0;
	}

	public int camMMIMenuReceived() {
		System.out.println("java camMMIMenuReceived() " + CIService.getInstance(0).getMMIMenu().toString());
		return 0;
	}

	public int camMMIEnqReceived() {
		System.out.println("java camMMIEnqReceived() " + CIService.getInstance(0).getMMIEnq().toString());
		return 0;
	}
	public int camMMIClosed(byte mmi_close_delay) {
		System.out.println("java camMMIClosed() " + mmi_close_delay);
		return 0;
	}

	public int camHostControlTune() {
		System.out.println("java camHostControlTune() " + CIService.getInstance(0).getHostControlTune().toString());
		return 0;
	}

	public int camHostControlReplace() {
		System.out.println("java camHostControlReplace() " + CIService.getInstance(0).getHostControlReplace().toString());
		return 0;
	}

	public int camHostControlClearReplace() {
		System.out.println("java camHostControlClearReplace() ");
		return 0;
	}

	public int camSystemIDStatusUpdated(byte sys_id_status) {
		System.out.println("java camSystemIDStatusUpdated() " + sys_id_status);
		return 0;
	}
}
