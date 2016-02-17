package com.mediatek.common.capture;

import java.io.IOException;
import com.mediatek.common.capture.*;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;



@SuppressWarnings("unused")
public class MtkCaptureLogo {
	
	private int handle;
//	private MtkCaptureCapability capability;
	private int event;
	static {
		System.loadLibrary("mtkcapture");
	}
	
	/* Type Define For Capture Source *******************************/
	public static final int CAPTURE_SRC_TYPE_DEFAULT = 0;
	public static final int CAPTURE_SRC_TYPE_TV_VIDEO = 1;
	public static final int CAPTURE_SRC_TYPE_MM_VIDEO = 2;
	public static final int CAPTURE_SRC_TYPE_MM_IMAGE = 3;
	public static final int CAPTURE_SRC_TYPE_MM_IMAGE_ANDROID = 4;
	
	/* Event Define For Capture Operation *******************************/
	
	public static final int	CAP_EVENT_TYPE_NONE = 0;
	public static final int	CAP_EVENT_TYPE_OPEN_DONE = 1;
	public static final int	CAP_EVENT_TYPE_OPEN_ERROR = 2;
	public static final int	CAP_EVENT_TYPE_CAP_DONE = 3;
	public static final int	CAP_EVENT_TYPE_CAP_ERR = 4;
	public static final int	CAP_EVENT_TYPE_SAVE_DONE = 5;
	public static final int	CAP_EVENT_TYPE_SAVE_ERROR = 6;
	public static final int	CAP_EVENT_TYPE_DO_CAPTURING = 7;
	public static final int	CAP_EVENT_TYPE_DO_SAVING = 8;
	
	
	/* Event Define For Capture Format *******************************/
	public static final int	CAP_FMT_TYPE_DEFAULT = 0;
	public static final int	CAP_FMT_TYPE_MPEG = 1;
	public static final int	CAP_FMT_TYPE_JPEG = 2;
	public static final int	CAP_FMT_TYPE_RAW =3;

	/* Event Define For Capture Output Resolution *******************************/
	public static final int CAP_OUT_RES_TYPE_SD = 0;
	public static final int CAP_OUT_RES_TYPE_HD = 1;
	public static final int CAP_OUT_RES_TYPE_USER = 2;
	
	/* Event Define For Capture Output Resolution *******************************/
	public static final int CAP_DEVICE_TYPE_DEFAULT = 0;
	public static final int CAP_DEVICE_TYPE_EXTERNAL = 1;
	public static final int CAP_DEVICE_TYPE_INTERNAL = 2;
	/**
     *   
     * 
     */
    public interface OnEventListener {
        abstract boolean onEvent(MtkCaptureLogo cap, int event);
    }
    
    public void setOnEventListener(OnEventListener listener) {
        this.EventListener = listener;
    }
    /**
     *   not used right now.
     * 
     */
    public interface OnCustomEventListener {
        abstract void onCustomEvent(MtkCaptureLogo cap, int source, int event);
    }
    
    public void setOnCustomEventListener(OnCustomEventListener listener) {
        this.CustomEventListener = listener;
    }
    
	public MtkCaptureLogo()
	{
		this.nativeInit();
	}
  
	public MtkCaptureLogo(int e_source) throws NotSupportException
	{
		this.nativeInit();
//		capability = this.nativeCaptureQueryCapability();
		handle = this.nativeCaptureOpen(e_source);
		event = CAP_EVENT_TYPE_NONE;
	}
	
	public int Capture(MtkCaptureInfo info) throws NotSupportException
	{
		return this.nativeCapture(handle, info);
	}

	public int Close() throws NotSupportException
	{
		return this.nativeCaptureClose(handle);
	}
	
	public int Save(MtkCaptureLogoSaveInfo info) throws NotSupportException
	{
		return this.nativeCaptureSave(handle, info);
	}
	public int Save(String path) throws NotSupportException
	{
		return this.nativeCaptureSaveExternal(handle, path);
	}

	public int Stop() throws NotSupportException
	{
		if(event == CAP_EVENT_TYPE_DO_SAVING || 
		   event == CAP_EVENT_TYPE_SAVE_DONE)
		{
			return this.nativeCaptureSyncStopSave(handle);	
		}
		else
		{
			return this.nativeCaptureSyncStopCapture(handle);
		}
	}
	
	public int SaveAsBootLogo(MtkCaptureLogoSaveInfo info) throws NotSupportException
	{
		return this.nativeCaptureSave(handle, info);
	}

	public MtkCaptureCapability QueryCapability() throws NotSupportException
	{
		return this.nativeCaptureQueryCapability();
	}
	
	public int GetData(byte[] b, int length) throws NotSupportException
	{
		return this.nativeCaptureGetData(handle, b, length);
	}
	
	public int SelectAsBootLogo(int device, int index) throws NotSupportException
	{
		return this.nativeCaptureSelectAsBootLogo(device, index);
	}
	
	private void CaptureEvent(int eEvent) {
			
		this.event = eEvent;
        Log.v("CaptureEvent", "CaptureEvent##type ==" + eEvent);
        this.EventListener.onEvent(this, eEvent);
    }
  
  private void CustomCaptureEvent(int type, int eEvent) {
			
		this.event = type;
        Log.v("CaptureEvent", "CaptureEvent##type ==" + type + " CAPTURE_EVENT_TYPE_T == " + eEvent);
        this.CustomEventListener.onCustomEvent(this, type,eEvent);
    }
	
	private native void nativeInit();
	private native int nativeCaptureOpen(int e_source);
	private native int nativeCapture(int handle, MtkCaptureInfo info);
	private native int nativeCaptureGetData(int handle, byte[] data, int length);
	private native int nativeCaptureClose(int handle);

    /** just for save internal device **/
	private native int nativeCaptureSave(int handle, MtkCaptureLogoSaveInfo info);
	private native int nativeCaptureSyncStopCapture(int handle);
	private native int nativeCaptureSyncStopSave(int handle);
	private native MtkCaptureCapability nativeCaptureQueryCapability();
	
	/*default: device = CAP_DEVICE_TYPE_DEFAULT*/
	/*0,1 ...: device = CAP_DEVICE_TYPE_INTERNAL, index = 0, 1......*/
	/*none: device = CAP_DEVICE_TYPE_INTERNAL, index = 0xFF*/
	private native int nativeCaptureSelectAsBootLogo(int device, int index);

    /**path for external device such as : /mnt/usbdisk/* /mnt/sdcard/*  **/
	private native int nativeCaptureSaveExternal(int handle, String path);
	
	private OnEventListener EventListener;
	private OnCustomEventListener CustomEventListener;
}

