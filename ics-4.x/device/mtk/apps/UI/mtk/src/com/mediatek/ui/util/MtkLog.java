package com.mediatek.ui.util;

import android.util.Log;

/**
 * 
 * @author mtk40530
 *
 */
public final class MtkLog {
	private static boolean logOnFlag = true;
	
	private MtkLog(){}
	
	public static void v(String tag, String msg){
		if (logOnFlag){
			Log.v(tag, msg);
		}
	}
	
	public static void d(String tag, String msg){
		if (logOnFlag){
			Log.d(tag, msg);
		}
	}
	
	public static void i(String tag, String msg){
		if (logOnFlag){
			Log.i(tag, msg);
		}
	}
	
	public static void w(String tag, String msg){
		if (logOnFlag){
			Log.w(tag, msg);
		}
	}
	
	public static void e(String tag, String msg){
		if (logOnFlag){
			Log.e(tag, msg);
		}
	}
}
