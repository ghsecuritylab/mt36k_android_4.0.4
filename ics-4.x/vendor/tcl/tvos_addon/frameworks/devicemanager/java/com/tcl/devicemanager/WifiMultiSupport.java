/*
 *   Copyright (C) 2006 The Android Open Source Project
 *    
 *   ©2010-2013 TCL CORPORATION All Rights Reserved.
 */

package com.tcl.devicemanager;

import android.net.wifi.WifiManager;
import android.net.wifi.IWifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.WifiNative;
import android.text.TextUtils;
//import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
//import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.util.Log;
import android.util.Slog;

import java.io.InputStream;
import java.lang.Process;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;


public class WifiMultiSupport {

	private static final String TAG = "WifiMultiSupport";


/*
	static{
		System.loadLibrary("wifimultsupport_jni");
	}

	private native static int getDeviceNum();

	private native static int getWifiSwitch();

	private native static int setWifiSwitch(boolean state);
*/

	private WifiManager mWifiManager;

	public WifiMultiSupport(Context context){
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	/**
	 * Get Wi-Fi dongle number
	 * @author TCL TVOS Team
	 * @return Wi-Fi dongle number
	 */
	public static int WifiDeviceNum() {
		return getDeviceNum();
	}
	
	/**
	 * Get the inner Wi-Fi GPIO status (Power ON or OFF)
	 * @author TCL TVOS Team
	 * @return inner Wi-Fi power status
	 */
	public static int readWifiPowerStatus() {
		return getWifiSwitch_1();
	}

	/**
	 * Set the inner Wi-Fi GPIO state (Power ON or OFF) 
	 * @author TCL TVOS Team
	 * @param true/false
	 * @return >= 0 On Success, < 0 On error
	 */
	public static int setWifiPowerStatus(boolean state) {
		if(getDeviceNum() < 1) {
			Slog.i(TAG, " **** no wifi device, so disable **** ");
			//mWifiManager.setWifiEnabled(false);
		}
		if(state == true) {
			return setWifiSwitch_1(true);
		} else {
			//mWifiManager.setWifiEnabled(false);
			return setWifiSwitch_1(false);
		}
	}

	public static int readProjectWifiStatus() { // lvh@tcl
		return 1;
	}
	
	//add by gaodw. adapter mTK interface.
	private static int setWifiSwitch_1(boolean state){
		if(state){
			MTKWifiPowerSetting.setWifiPowerOn();
		}else{
            MTKWifiPowerSetting.setWifiPowerOff();
		}
		return 0;
	}
	
	private static int getWifiSwitch_1(){
		if(MTKWifiPowerSetting.getWifiPowerState() == 0){
			return 1;
		}else{
			return 0;
		}

		//return MTKWifiPowerSetting.getWifiPowerState() ;
	}
	//end


       private static int getDeviceNum(){
	return  MTKWifiPowerSetting.getWlanNum();
       }
	
}
