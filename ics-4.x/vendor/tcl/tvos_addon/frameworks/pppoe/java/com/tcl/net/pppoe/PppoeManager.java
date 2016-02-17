/*
 * Copyright (C) 2010 The Android-X86 Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Yi Sun <beyounn@gmail.com>
 */
package com.tcl.net.pppoe;

import java.util.List;

import android.annotation.SdkConstant;
import android.annotation.SdkConstant.SdkConstantType;
import android.net.wifi.IWifiManager;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Slog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.mediatekk.netcm.util.NetLog;
//import com.mediatek.pppoe.*;

public class PppoeManager {

    public static final String TAG = "PppoeManager";
    public static final int POE_DEVICE_SCAN_RESULT_READY = 0;
    public static final String POE_STATE_CHANGED_ACTION =
            "android.net.pppoe.POE_STATE_CHANGED";
    public static final String NETWORK_STATE_CHANGED_ACTION =
            "android.net.pppoe.STATE_CHANGE";


    public static final String EXTRA_NETWORK_INFO = "networkInfo";
    public static final String EXTRA_POE_STATE = "pppoe_state";
    public static final String EXTRA_PREVIOUS_POE_STATE = "previous_pppoe_state";

    public static final int POE_STATE_UNKNOWN = 0;
    public static final int POE_STATE_DISABLED = 1;
    public static final int POE_STATE_ENABLED = 2;

    //add pengpai 
    public static final String PPPOE_STATE_CONNECT = "connect";
    public static final String PPPOE_STATE_DISCONNECT = "disconnect";
    public static final String PPPOE_STATE_CONNECTING = "connecting";
    public static final String PPPOE_STATE_FAILED = "failed";
    
    public static final int STATE_DISCONNECT = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECT = 2;
  
    private static PppoeManager mPppoeManager = null;
    private String username;
    private String passwd;

    /**
    * Create a new PppoeManager instance. 
    * Applications will use for pppoe operation.
    * 
    */
    public static PppoeManager getInstance() {
        mPppoeManager = new PppoeManager();
	return mPppoeManager;
    }

    IPppoeManager mService;

    public PppoeManager() {
        Slog.i(TAG, "pppoe Manager");

    }

    /*public void UpdatePoeDevInfo(PppoeDevInfo info) {
        try {
        
	    Slog.i(TAG, "UpdatePoeDevInfo =" + info);	
            mService.UpdatePoeDevInfo(info);
        } catch (RemoteException e) {
            Slog.i(TAG, "==2011==Can not update pppoe device info");
        }
    }*/
////////add by 2011-11-18    
    public void setPppoeEnabled(boolean enable) {
        try {
            mService.setpppoeState(enable ? POE_STATE_ENABLED:POE_STATE_DISABLED);
        } catch (RemoteException e) {
            Slog.i(TAG,"Can not set new state");
        }
    }

	/**
	 * Get the current pppoe link status.
	 * 
	 * @return the status of pppoe.
	 * 
	 */
    public String getPppoeStatus() {//pengpai add 
        try {
            int _sta = mService.getpppoeState();
            Slog.i(TAG,"getpppoeState:"+_sta);
            String _re_sta = PPPOE_STATE_DISCONNECT;
	    if(_sta == STATE_CONNECT){
	        _re_sta = PPPOE_STATE_CONNECT;
	    }else if(_sta == STATE_DISCONNECT){
		_re_sta = PPPOE_STATE_DISCONNECT;
	    }else if(_sta == STATE_CONNECTING){
		_re_sta = PPPOE_STATE_CONNECTING;
	    }else {
                _re_sta = PPPOE_STATE_FAILED;
            }
            Slog.i(TAG,"getpppoeState string:"+_re_sta);
            return _re_sta;
        } catch (RemoteException e) {
            return PPPOE_STATE_FAILED;
        }
    }

    /*public  PppoeDevInfo getSavedPppoeConfig() {
	Slog.i(TAG, "--------entry getSavedPppoeConfig-----");
        try {
	Slog.i(TAG, "--------entry try-----");
            return mService.getSavedPppoeConfig();
        } catch (RemoteException e) {
            Slog.i(TAG, "Can not get ppoe config");
	        }
	return null;
	
     
    }*/

/////////////////////////////////////////////////////////pengpai add 
	/**
	 * Create a dial up link by pppoe.
	 * 
	 * @param username   user name.
	 * @param passwd     the password use to pppoe link.
	 * 
	 */
	public void createDialUpLink(String username, String passwd) {
		this.username = username;
		this.passwd = passwd;
		//pppoeins.dialUp(username, passwd);
	}

	/**
	 * Break a dial up link.
	 * 
	 */
	public void breakDialUpLink() {
		//pppoeins.hangUp();
	}

	/**
	 * Get the user name which use to create pppoe link.
	 * 
	 * @return the user name.
	 */
	public String getUserName() {
		return username;
	}

	/**
	 * Get the password which use to create pppoe link.
	 * 
	 * @return the password.
	 * 
	 */
	public String getPassword() {
		return passwd;
	}
	
	/**
	 * Get the current pppoe link's IP address.
	 * 
	 * @return the IP address.
	 * 
	 */
	public String getIP() {
		String ipPrefix = "inet addr:";
//		String[] command = {"ifconfig", "ppp0"};
		String ip = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("ifconfig ppp0");
//			process = Runtime.getRuntime().exec(command);
//			Log.d("testPppoe", "ip, ifconfig ppp0");
			bufferedReader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
//				Log.d("testPppoe", "line: "+line);
//				line = line.trim();
				index = line.toLowerCase().indexOf(ipPrefix);
				if (index >= 0) {
					ip = line.substring(index+ipPrefix.length()).trim();
					index = ip.indexOf(" ");
					ip = ip.substring(0, index);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}
		return ip;
	}
	
	/**
	 * Get the current pppoe link's net mask.
	 * 
	 * @return the net mask.
	 * 
	 */
	public String getMask() {
		String maskPrefix = "mask:";
		String mask = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("ifconfig ppp0");
//			Log.d("testPppoe", "mask, ifconfig ppp0");
			bufferedReader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
//				Log.d("testPppoe", "line: "+line);
				index = line.toLowerCase().indexOf(maskPrefix);
				if (index >= 0) {
					mask = line.substring(index + maskPrefix.length()).trim();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}
		return mask;
	}
	
	/**
	 * Get the current pppoe link's gate way.
	 * 
	 * @return the gate way.
	 * 
	 */
	public String getGateway() {
		String gwPrefix = "default";
		String gateway = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("route");
			bufferedReader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
//				Log.d("testPppoe", "line: "+line);
//				line = line.trim();
				index = line.toLowerCase().indexOf(gwPrefix);
				if (index >= 0) {
					gateway = line.substring(index + gwPrefix.length()).trim();
					index = gateway.indexOf(" ");
					gateway = gateway.substring(0, index);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}
		return gateway;
	}
	
	/**
	 * Get the current pppoe link's MAC address.
	 * 
	 * @return the MAC address.
	 * 
	 */
	public String getMac() {
		String macPrefix = "hwaddr";
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("ifconfig eth0");
//			Log.d("testPppoe", "mac, ifconfig eth0");
			bufferedReader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));
//			Log.d("testPppoe", "buffer: "+bufferedReader.toString());
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				NetLog.d("testPppoe", "line: "+line);
				index = line.toLowerCase().indexOf(macPrefix);
				if (index >= 0) {
					mac = line.substring(index + macPrefix.length()).trim();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}
		return mac;
	}
	
	/**
	 * Get the current pppoe link's DNS address.
	 * 
	 * @return the DNS address.
	 * 
	 */
	public String getDNS() {
		String dnsPrefix = "[net.dns1]: [";
		String dns = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
//			process = Runtime.getRuntime().exec("getprop | grep net.dns1");
			process = Runtime.getRuntime().exec("getprop");
			bufferedReader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				NetLog.d("testPppoe", "line: "+line);
				index = line.toLowerCase().indexOf(dnsPrefix);
				if (index >= 0) {
					dns = line.substring(index + dnsPrefix.length()).trim();
					index = dns.indexOf("]");
					dns = dns.substring(0, index);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}
		return dns;
	}
        //add end

}
