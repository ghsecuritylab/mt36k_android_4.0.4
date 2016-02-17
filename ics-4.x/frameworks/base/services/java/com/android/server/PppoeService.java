/*
 * Copyright (C) 2009 The Android-x86 Open Source Project
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

package com.android.server;

import java.net.UnknownHostException;
import android.net.pppoe.PppoeNative;
import android.net.pppoe.IPppoeManager;
import android.net.pppoe.PppoeManager;
import android.net.pppoe.PppoeStateTracker;
import android.net.pppoe.PppoeDevInfo;
import android.provider.Settings;
import android.util.Slog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/*
import android.net.NetworkInfo;
import android.net.NetworkStateTracker;
import android.net.ConnectivityManager;
import android.net.ConnectivityService;
*/
public class PppoeService<syncronized> extends IPppoeManager.Stub{
	private Context mContext;
	private PppoeStateTracker mTracker;
	private String[] DevName;
	private static final String TAG = "PppoeService";
	private int mPppoeState = PppoeManager.POE_STATE_UNKNOWN;
	private PppoeDevInfo mDevInfo = null;


	public PppoeService(Context context, PppoeStateTracker Tracker){
		mTracker = Tracker;
		mContext = context;

		Slog.i(TAG,"==PppoeService== start ");
		//setdefaultpppoeState();//default value setting,set in connectivityservice
		Slog.i(TAG, "Trigger the pppoe monitor:I'm in pppoe service ");
		mTracker.StartPolling();//status report polling from Bottom
	}

	public synchronized void UpdatePoeDevInfo(PppoeDevInfo info) {
		final ContentResolver cr = mContext.getContentResolver();
	/*    Settings.Secure.putString(cr, Settings.Secure.ADSL_IFNAME, info.getIfName());
	    Settings.Secure.putString(cr, Settings.Secure.ADSL_IP, info.getIpAddress());
	    Settings.Secure.putString(cr, Settings.Secure.ADSL_DNS, info.getDnsAddr());
	    Settings.Secure.putString(cr, Settings.Secure.ADSL_ROUTE, info.getRouteAddr());*/
	    //Settings.Secure.putString(cr, Settings.Secure.ETH_MASK,info.getNetMask());
		/*try {//here should dial?
				mTracker.resetInterface();
			} catch (UnknownHostException e) {
				Slog.e(TAG, "Wrong ethernet configuration");
			}*///for compile ,commented temp

	}

	public void setpppoeState(int state) {
		Slog.i(TAG, "==setdefaultpppoeState==");
		if(mPppoeState!=state){
			mPppoeState = state;
		}

		//NetworkStateTracker t = mNetTrackers[ConnectivityManager.TYPE_ADSL];
        //NetworkInfo info = t.getNetworkInfo();
		//info.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED,null,null);
	}

	public int getpppoeState( ) {
		return mPppoeState;
	}
///////////////////////////////////////
	public synchronized PppoeDevInfo getSavedPppoeConfig(){

			Slog.i(TAG, "get ppoe ip information ");
    			PppoeDevInfo info = new PppoeDevInfo();  
			File file = new File("/data/data/addr");
			String[] data=ReadIpSettings(file).split("\\$");
			for(int i=0;i<data.length;i++)
			{
				System.out.println("xxxxxxxxxxxx"+data[i]);
			}
  			info.setIpAddress(data[0]);
			info.setRouteAddr(data[1]);
    		info.setDnsAddr(data[2],data[3]);
   		    return info;
	}
///////////////////////////////////////////////////
    public String ReadIpSettings(File file)
    {
    	System.out.print("..............");
        String data = "";
        try {
  		FileInputStream stream = new FileInputStream(file);
  		StringBuffer sb = new StringBuffer();
  		int c;
  		while ((c = stream.read()) != -1) {
  		sb.append((char) c);
  	   }
  		stream.close();
  		data = sb.toString();
  	  	} catch (FileNotFoundException e) {
  		System.out.println("not found file");
		e.printStackTrace();
  	} catch (IOException e) {
  		e.printStackTrace();
  	}
  	return data;
       }
	

}
