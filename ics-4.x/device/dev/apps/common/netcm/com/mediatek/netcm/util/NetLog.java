package com.mediatek.netcm.util;

import android.util.Log;

/**
 * This class use to control log ouput for network CM.
 * 
 */
public class NetLog {
	private static boolean localLOGV = true;
	
	public static void v(String tag, String msg){
		if (localLOGV){
			Log.v(tag, msg);
		}
	}
	
	public static void d(String tag, String msg){
		if (localLOGV){
			Log.d(tag, msg);
		}
	}
	
	public static void i(String tag, String msg){
		if (localLOGV){
			Log.i(tag, msg);
		}
	}
	
	public static void w(String tag, String msg){
		if (localLOGV){
			Log.w(tag, msg);
		}
	}
	
	public static void e(String tag, String msg){
		if (localLOGV){
			Log.e(tag, msg);
		}
	}
}
