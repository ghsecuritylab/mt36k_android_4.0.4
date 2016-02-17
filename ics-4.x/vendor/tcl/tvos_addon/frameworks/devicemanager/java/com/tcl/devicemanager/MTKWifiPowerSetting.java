package com.tcl.devicemanager;

import java.io.File;

import android.util.Log;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.service.ConfigService;
import com.mediatek.tv.common.ConfigValue;
//import com.mediatek.tv.common.ConfigType;
//import com.mediatek.tv.common.BaseConfigType;

import com.tcl.devicemanager.ConfigType;
import com.tcl.devicemanager.BaseConfigType;

import com.mediatek.tv.common.TVMException;
//import com.tcl.mstar.settings.pppoe.RootSeeker;

import android.net.wifi.WifiNative;

public class MTKWifiPowerSetting {
    private final static String TAG = "MTKWifiPowerSetting";
	public static TVManager tvMng = TVManager.getInstance(null);
	public static ConfigService configService = (ConfigService) tvMng

	.getService(ConfigService.ConfigServiceName);

	public static ConfigValue value = new ConfigValue();
	
	
	public static boolean curwlanisexternal = false;
	
	public static final int MSG_WIFI_CONNECT_TIMEOUT = 0;
	public static final int MSG_EXWIFI_NOT_CONNECT = 1;
	public static final int MSG_EXWIFI_PULL = 2;
	public static final int MSG_EXWIFI_RECONNECT = 3;
	public static final int MSG_WIFI_NOT_CONNECT = 4;
	public static final int MSG_WIFI_IN_AIRPLANE_MODE = 5;
	public static final int MSG_ENABLE_CHECHBOX = 6;
	public static final int MSG_SET_SUMMARY = 7;
	public static final int MSG_SET_SUMMARY_LOAD = 8;

	// 设置开：
 	public static void setWifiPowerOn() {
 		Log.d("MTKWifiPowerSetting", "=======================>setWifiPowerOn()");
		try {
			value.setIntValue(0);
	
			configService.setCfg(ConfigType.CFG_WIFI_SUPPLY, value);
	
			drvset(ConfigType.CFG_WIFI_SUPPLY_FUNC, 0);
		} catch (TVMException e) {
			Log.i("TVMException", e.toString());
		}
	}

	// 设置关；
	public static void setWifiPowerOff() {
		Log.d("MTKWifiPowerSetting", "=======================>setWifiPowerOff()");
		try {
		value.setIntValue(1);

		configService.setCfg(ConfigType.CFG_WIFI_SUPPLY, value);

		drvset(ConfigType.CFG_WIFI_SUPPLY_FUNC, 1);
		} catch (TVMException e) {
			Log.i("TVMException", e.toString());
		}
	}

	private static boolean drvset(String dInterfaceType, int val) {

		//TVManager tvManager = TVManager.getInstance(null);

		//ConfigService configService = (ConfigService) tvManager
		//		.getService(ConfigService.ConfigServiceName);

		ConfigValue configValue = new ConfigValue();

		int[] value = new int[3];

		configValue.setIntArrayValue(value);

		value[0] = BaseConfigType.D_INTERFACE_OP_SET_NORMAL;

		value[1] = val;

		value[2] = 1;

		try {

			configService.setCfg(dInterfaceType, configValue);

			System.out.println("set value#####" + value[1]);

			return true;

		} catch (TVMException e) {

			e.printStackTrace();

		}

		return false;

	}

	/** 获取开关值：0为开；1为关；*/
	public static int getWifiPowerState() {
		try {
			value = configService.getCfg(ConfigType.CFG_WIFI_SUPPLY);

			return value.getIntValue();
		} catch (TVMException e) {
			Log.i("TVMException", e.toString());
		}
		return -1;
	}
	
	public static boolean isWlan0Connected() {
    	
    	File rootPath = new File("/sys/class/net/wlan0");
		if (rootPath.exists()) {
			return true;
		}
		
		return false;
    }
	
	public static boolean isWlan1Connected() {
    	
    	File rootPath = new File("/sys/class/net/wlan1");
		if (rootPath.exists()) {
			return true;
		}
		
		return false;
    }
	
	public static boolean isWlan2Connected() {
    	
    	File rootPath = new File("/sys/class/net/wlan2");
		if (rootPath.exists()) {
			return true;
		}
		
		return false;
    }
	
	
	public static int getWlanNum() {
		int num = 1;
			num = WifiNative.getDeviceNum();
			Log.d(TAG, "getWlanNum():" + num);
		return num;
		
	}
	
	public static int checkWifiState() {
		int wifinum = getWlanNum();
		int wifipowerstate = getWifiPowerState();
		
		Log.d(TAG, "checkWifiState()===========>getWlanNum():" + wifinum);
		Log.d(TAG, "checkWifiState()===========>getWifiPowerState():" + wifipowerstate);
		if (wifipowerstate == 1) {
			if (wifinum == 0 && curwlanisexternal) {
				return MSG_EXWIFI_NOT_CONNECT;
			} else if (wifinum == 0 && !curwlanisexternal) {
				setWifiPowerOn();
			} else if (wifinum >= 1 && !curwlanisexternal) {
				return MSG_EXWIFI_PULL;
			} 
		} else {
			if (wifinum == 0 && curwlanisexternal) {
				setWifiPowerOff();
				return MSG_EXWIFI_NOT_CONNECT;
			} else if (wifinum == 0 && !curwlanisexternal) {
				setWifiPowerOn();
			} else if (wifinum == 1 && curwlanisexternal) {
				setWifiPowerOff();
				return MSG_EXWIFI_NOT_CONNECT;
			} else if (wifinum > 1 && curwlanisexternal) {
				setWifiPowerOff();
			} else if (wifinum > 1 && !curwlanisexternal) {
				return MSG_EXWIFI_PULL;
			}
		}
		
		return -1;
	}

	/*
	public static void rmWifiSupplican() {
		Log.d("WifiSettings","rmWifiSupplicant!");
		File wpa_supplican = new File("/data/misc/wifi/wpa_supplicant");
		try {
			if (wpa_supplican.exists()) {
				String cmd = String
						.format("rm -r /data/misc/wifi/wpa_supplicant");
				Log.d("WifiSettings","remove /data/misc/wifi/wpa_supplicant!");
				RootSeeker.exec(cmd);
			}
		} catch (SecurityException e) {
			Log.i("WifiSettings............",
					"show SecurityException" + e.toString());
		}
	}
	*/
}
