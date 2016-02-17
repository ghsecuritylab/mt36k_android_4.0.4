package com.opengl.jni;

import android.util.Log;
import android.os.SystemProperties;

public class CaptureFrameBuffer {

	private static final String TAG = "CaptureFrameBuffer-fw";
	public static long timingChangeTimes = 0;
	
    static {
        System.loadLibrary("captureod");
    }
    public static native int   nativeInitCaptureOd();
    public static native void  nativeUinitCaptureOd();
    public static native int   nativeGetWidth();
    public static native int   nativeGetHeight();
    public static native void nativeSetMaxWidthAndHeigh(int w, int h);
	public static native int nativeCaptureScreen2File(int x, int y, int w, int h);

    //public static native int   nativeGetFrameBuffer(byte [] data, int ctx, int x, int y, int w, int h);
    public static native void nativeSetQuality(int quality);

/*

	public static int   nativeChangeTiming(){
		timingChangeTimes++;
		return 0;
	}

	public static int   nativeGetFullFrameBuffer(byte [] data){
		int ret = 0;
		int ctx = (timingChangeTimes!=0)?(1<<8):0;
		Log.d(TAG, "timingChangeTimes = " + timingChangeTimes);
		ret = nativeGetFrameBuffer(data, ctx, 0, 0, 0, 0);
		if ((ctx>>8) != 0)
			timingChangeTimes = 0;
		return ret;
		
	}
    public static int   nativeGetRectFrameBuffer(byte [] data, int x, int y, int w, int h){
		int ret = 0;
		int ctx = (timingChangeTimes!=0)?(1<<8):0;
		Log.d(TAG, "timingChangeTimes = " + timingChangeTimes);
		ret = nativeGetFrameBuffer(data, ctx, x, y, w, h);
		if ((ctx>>8) != 0)
			timingChangeTimes = 0;
		return ret;
    }
	public static int   nativeGetFullFrameBufferRGB24(byte [] data) {
		int ret = 0;
		int ctx = (timingChangeTimes!=0)?(1<<8):0;
		ctx |= 1;
		Log.d(TAG, "timingChangeTimes = " + timingChangeTimes);
		ret = nativeGetFrameBuffer(data, ctx, 0, 0, 0, 0);
		if ((ctx>>8) != 0)
			timingChangeTimes = 0;
		return ret;
	}
    public static int   nativeGetRectFrameBufferRGB24(byte [] data, int x, int y, int w, int h){
		int ret = 0;
		int ctx = (timingChangeTimes!=0)?(1<<8):0;
		ctx |= 1;
		Log.d(TAG, "timingChangeTimes = " + timingChangeTimes);
		ret = nativeGetFrameBuffer(data, ctx, x, y, w, h);
		if ((ctx>>8) != 0)
			timingChangeTimes = 0;
		return ret;
    }
    */
}
