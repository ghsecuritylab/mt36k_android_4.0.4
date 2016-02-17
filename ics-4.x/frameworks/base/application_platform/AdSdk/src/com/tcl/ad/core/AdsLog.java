package com.tcl.ad.core;

import android.util.Log;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.10.13
 * 
 * @JDK version: 1.5
 * @brief: Write log to console with Ads tag.   
 * @version: v1.0
 *
 */
public final class AdsLog {

	private static final String TAG = "TCL_AD_SDK";
	private AdsLog(){};
	public static void debugLog(String msg)
	{
		Log.d(TAG, msg);
	}
	
	public static void errorLog(String msg)
	{
		Log.e(TAG, msg);
	}
	
	public static void infoLog(String msg)
	{
		Log.d(TAG, msg);
	}
}
