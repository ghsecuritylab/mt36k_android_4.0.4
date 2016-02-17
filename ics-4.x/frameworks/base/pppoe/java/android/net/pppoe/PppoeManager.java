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
package android.net.pppoe;

import java.util.List;

import android.annotation.SdkConstant;
import android.annotation.SdkConstant.SdkConstantType;
import android.net.wifi.IWifiManager;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Slog;

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

    IPppoeManager mService;
    Handler mHandler;

    public PppoeManager(IPppoeManager service, Handler handler) {
        Slog.i(TAG, "Init pppoe Manager");
        mService = service;
        mHandler = handler;
    }

    public void UpdatePoeDevInfo(PppoeDevInfo info) {
        try {
        
			Slog.i(TAG, "UpdatePoeDevInfo =" + info);	
            mService.UpdatePoeDevInfo(info);
        } catch (RemoteException e) {
            Slog.i(TAG, "==2011==Can not update pppoe device info");
        }
    }
////////add by 2011-11-18    
    public void setPppoeEnabled(boolean enable) {
        try {
            mService.setpppoeState(enable ? POE_STATE_ENABLED:POE_STATE_DISABLED);
        } catch (RemoteException e) {
            Slog.i(TAG,"Can not set new state");
        }
    }

    public int getpppoeState( ) {//enabled or disabled
        try {
            return mService.getpppoeState();
        } catch (RemoteException e) {
            return 0;
        }
    }
/////////////////////////////////////////////////////////////
    public  PppoeDevInfo getSavedPppoeConfig() {
	Slog.i(TAG, "--------entry getSavedPppoeConfig-----");
        try {
	Slog.i(TAG, "--------entry try-----");
            return mService.getSavedPppoeConfig();
        } catch (RemoteException e) {
            Slog.i(TAG, "Can not get ppoe config");
	        }
	return null;
	
     
    }
/////////////////////////////////////////////////////////

}
