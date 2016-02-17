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

import android.os.Parcel;
import android.os.Parcelable;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.Inet6Address;

import java.util.Collection;


import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkStateTracker;
import android.net.NetworkUtils;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.SystemProperties;
import android.util.*;

import android.provider.Settings;
import android.content.ContentResolver;
import android.content.Context;

import android.text.format.Formatter;
import android.os.Bundle;

import android.os.Parcelable;
import android.text.TextUtils;
import android.net.ProxyProperties;

import android.net.LinkAddress;
import android.net.RouteInfo;
import android.net.LinkCapabilities;
import android.net.NetworkInfo;
import android.net.LinkProperties;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Track the state of adsl connectivity. All event handling is done here,
 * and all changes in connectivity state are initiated here.
 *
 * @hide
 */
public class PppoeStateTracker extends Handler implements  NetworkStateTracker {
	private static final String TAG="PppoeStateTracker";
	
	public static final int EVENT_PPPOE_CONNECTING	= 0;
	public static final int EVENT_PPPOE_CONNECTED	= 1;
	public static final int EVENT_PPPOE_DISCONNECTED = 2;
	public static final int EVENT_PPPOE_AUTH_FAIL = 3;

    private AtomicBoolean mTeardownRequested = new AtomicBoolean(false);
    private AtomicBoolean mPrivateDnsRouteSet = new AtomicBoolean(false);
    private AtomicBoolean mDefaultRouteSet = new AtomicBoolean(false);
	
	private PppoeManager mEM;
	private boolean mServiceStarted;

	private boolean mStackConnected;
	private boolean mHWConnected;
	private boolean mInterfaceStopped;
	private String mInterfaceName ;
	private PppoeMonitor mMonitor;
	private String[] sDnsPropNames;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private Handler mTrackerTarget;
	private int mDefaultGatewayAddr;
  //  private boolean mDefaultRouteSet;

    /* For sending events to connectivity service handler */
    private Handler mCsHandler;
	private Context mContext;	
	
	
	private NetworkInfo mNetworkInfo;	
	private LinkProperties mLinkProperties;
    private LinkCapabilities mLinkCapabilities;


	public PppoeStateTracker(Context context, Handler target) {

		mNetworkInfo = new NetworkInfo(ConnectivityManager.TYPE_ADSL, 0, "ADSL", "");
		Slog.i(TAG,"--adsl StateTracker--");
	
		mCsHandler = target;
		mContext = context;

		if (PppoeNative.initpppoeNative() != 1 ) {
			Slog.e(TAG,"Can not init  device layers===pppoe");
			return;
		}
		mServiceStarted = true;
		
		sDnsPropNames = new String[] {
			"net.ppp.dns1",
			"net.ppp.dns2"
		};

		
		mMonitor = new PppoeMonitor(this);
		
        mLinkProperties = new LinkProperties();
//        mLinkCapabilities = new LinkCapabilities();

	}
/*	
	@Override
	public String[] getNameServers() {
		return getNameServerList(sDnsPropNames);
	}
*/
	
	@Override
	public String getTcpBufferSizesPropName() {
		// TODO Auto-generated method stub
		Slog.i(TAG,"--adsl getTcpBufferSizesPropName--");
		return "net.tcp.buffersize.wifi";
	}

	@Override
	public void setUserDataEnable(boolean enabled)
	{Slog.i(TAG,"--adsl setUserDataEnable--");}
	
	@Override
    public void setPolicyDataEnable(boolean enabled)
	{Slog.i(TAG,"--adsl setPolicyDataEnable--");}

    public void setDependencyMet(boolean met) {
        // not supported on this network
        Slog.i(TAG,"--adsl setDependencyMet--");
    }

    public void setDataEnable(boolean enabled) {
    	Slog.i(TAG,"--adsl setDataEnable--");
	}
	
    public void setTeardownRequested(boolean isRequested) {
    	Slog.i(TAG,"--adsl setTeardownRequested--");
        mTeardownRequested.set(isRequested);
    }

    public boolean isTeardownRequested() {
    	Slog.i(TAG,"--adsl isTeardownRequested--");
        return mTeardownRequested.get();//false;//
    }
    /**
     * Check if private DNS route is set for the network
     */
    public boolean isPrivateDnsRouteSet() {
    	Slog.i(TAG,"--adsl isPrivateDnsRouteSet--");
        return mPrivateDnsRouteSet.get();//true;//
    }

    /**
     * Set a flag indicating private DNS route is set
     */
    public void privateDnsRouteSet(boolean enabled) {
    	Slog.i(TAG,"--adsl privateDnsRouteSet--");
      mPrivateDnsRouteSet.set(enabled);
    }

    /**
     * Fetch NetworkInfo for the network
     */
    public NetworkInfo getNetworkInfo() {
    	//Slog.i(TAG,"--adsl getNetworkInfo--");
        return new NetworkInfo(mNetworkInfo);
    }

    /**
     * Fetch LinkProperties for the network
     */
    public LinkProperties getLinkProperties() {
    	//Slog.i(TAG,"--adsl getLinkProperties--");
        return new LinkProperties(mLinkProperties);
//		return mLinkProperties;
//		LinkProperties aaa = new LinkProperties(mLinkProperties);
//		return aaa;
    }

    /**
     * A capability is an Integer/String pair, the capabilities
     * are defined in the class LinkSocket#Key.
     *
     * @return a copy of this connections capabilities, may be empty but never null.
     */
    public LinkCapabilities getLinkCapabilities() {
    	Slog.i(TAG,"--adsl getLinkCapabilities");
//        return new LinkCapabilities(mLinkCapabilities);
//		return mLinkCapabilities;
		LinkCapabilities aaa = new LinkCapabilities(mLinkCapabilities);
		return aaa;
    }

    /**
     * Check if default route is set
     */
    public boolean isDefaultRouteSet() {
    	Slog.i(TAG,"--adsl isDefaultRouteSet--");
        return mDefaultRouteSet.get();//true;//
    }

    /**
     * Set a flag indicating default route is set for the network
     */
    public void defaultRouteSet(boolean enabled) {
    	Slog.i(TAG,"--adsl defaultRouteSet--");
      mDefaultRouteSet.set(enabled);
    }


	public void StartPolling() {
		Slog.i(TAG, "--polling........");
		mMonitor.startMonitoring();
	}
	@Override
	public boolean isAvailable() {
		// Only say available if we have interfaces and user did not disable us.
		return (mInterfaceName !=null)&&(mEM.getpppoeState() != PppoeManager.POE_STATE_DISABLED);
	}

	@Override
	public boolean reconnect() {
		//
		Slog.i(TAG,"--adsl reconnect--");
		return false;

	}

// this function is needed for grammer
	@Override
	public boolean setRadio(boolean turnOn) {
		Slog.i(TAG,"--adsl setRadio--");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
//	public void startMonitoring() {
    public void startMonitoring(Context context, Handler target) {

		Slog.i(TAG,"start to monitor the pppoe devices,just a blank function now");
		if (mServiceStarted)	{
			mContext = context;
	        mCsHandler = target;
		
		 mEM = (PppoeManager)context.getSystemService(Context.ADSL_SERVICE);
		 if(mEM == null)
		 {
		 	Slog.e(TAG,"get adsl service null");
		 	return;
		 }
		int state = mEM.getpppoeState();
		if (state != mEM.POE_STATE_DISABLED) {
			if (state == mEM.POE_STATE_UNKNOWN){
					// maybe this is the first time we run, so set it to enabled
					mEM.setPppoeEnabled(true);
				} 
			}
		}
	}
/*
	@Override
	public int startUsingNetworkFeature(String feature, int callingPid,int callingUid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int stopUsingNetworkFeature(String feature, int callingPid,
			int callingUid) {
		// TODO Auto-generated method stub
		return 0;
	}
*/
	@Override
	public boolean teardown() {
		return false;//(mEM != null) ? stopInterface(false) : false;
	}


//this 2 functions checked already
	private void postNotification(int event) {
		String ns = Context.NOTIFICATION_SERVICE;
		
		Message msg = mCsHandler.obtainMessage(EVENT_STATE_CHANGED, new NetworkInfo(mNetworkInfo));
   	msg.sendToTarget();
		
		mNotificationManager = (NotificationManager)mContext.getSystemService(ns);
		final Intent intent = new Intent(PppoeManager.POE_STATE_CHANGED_ACTION);
		intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT);
		intent.putExtra(PppoeManager.EXTRA_POE_STATE, event);
		mContext.sendStickyBroadcast(intent);
	}

	private void setpppoeState(boolean state, int event) {
	
		Slog.d(TAG, "--mNetworkInfo.isConnected():" + mNetworkInfo.isConnected());
		
			if (state) {
				mNetworkInfo.setDetailedState(DetailedState.CONNECTED, null, null);
				Slog.i(TAG,"--set CONNECTED--");
				mCsHandler.sendEmptyMessage(EVENT_ADSL_CONNECTED);//this msg used for  broadcast up for tianyapeng--0706  
			} else {
				mNetworkInfo.setDetailedState(DetailedState.DISCONNECTED, null, null);
				Slog.i(TAG,"--set DISCONNECTED--");
			}
			mNetworkInfo.setIsAvailable(state);
			postNotification(event);
	}
	private void FailStateReport(boolean state, int event) {
	
		Slog.d(TAG, "--StateReport:--" + mNetworkInfo.isConnected());
		
		mCsHandler.sendEmptyMessage(EVENT_ADSL_AUTHFAIL);//this msg used for  huopinghua--0706
		
		Slog.i(TAG,"--EVENT_ADSL_AUTHFAIL--");
	}
	//@Override
	
	public void addDefaultRoute() {
       		 if ((mInterfaceName != null) && (mDefaultGatewayAddr != 0) &&
               		 mDefaultRouteSet.get() == false) {
            	 Slog.i(TAG,"+++++in addDefaultRoute+++++++++");   

               	 Log.d(TAG, "addDefaultRoute for ppp0 GatewayAddr=" + mDefaultGatewayAddr);
            
           	 NetworkUtils.setDefaultRoute(mInterfaceName, mDefaultGatewayAddr);
            	mDefaultRouteSet.set(true);
       		 }
    	}

	//@Override
   	 public void removeDefaultRoute() {
       		 if (mInterfaceName != null && mDefaultRouteSet.get() == true) {
 
                Log.d(TAG, "removeDefaultRoute for " + mNetworkInfo.getTypeName() + " (" +
                        mInterfaceName + ")");

            	NetworkUtils.removeDefaultRoute(mInterfaceName);
            	mDefaultRouteSet.set(false);
       		 }
    	}

    private static int lookupHost(String hostname) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            return -1;
        }
        byte[] addrBytes;
        int addr;
        addrBytes = inetAddress.getAddress();
        addr = ((addrBytes[3] & 0xff) << 24)
                | ((addrBytes[2] & 0xff) << 16)
                | ((addrBytes[1] & 0xff) << 8)
                |  (addrBytes[0] & 0xff);
        return addr;
    }


	private boolean configureInterface(PppoeDevInfo info) throws UnknownHostException {
		   
			mInterfaceName = "ppp0";
			String ipaddr = info.getIpAddress();
			String gateway = info.getRouteAddr();
			String dns1 = info.getDns1Addr();
			String dns2 = info.getDns2Addr();
			mDefaultGatewayAddr = lookupHost(gateway);
			mDefaultRouteSet.set(false);
			Slog.i(TAG,"+++getIpAddress++"+info.getIpAddress()+"+++++++++");
			Slog.i(TAG,"++getRouteAddr+++"+info.getRouteAddr()+"+++++++++");
			Slog.i(TAG,"++getNetMask+++"+info.getNetMask()+"+++++++++");
			Slog.i(TAG,"+++getDns1Addr++"+info.getDns1Addr()+"+++++++++");
			Slog.i(TAG,"++getDns2Addr+++"+info.getDns2Addr()+"+++++++++");
			PppoeNative.pppoeconfigure(ipaddr,gateway,dns1,dns2);
			Slog.v(TAG, "PPPoE configuration succeeded");	
			
            DhcpInfo tTmpDhcpInfo = new DhcpInfo();
            tTmpDhcpInfo.ipAddress = lookupHost(info.getIpAddress());
            tTmpDhcpInfo.gateway = lookupHost(info.getRouteAddr());
            tTmpDhcpInfo.netmask = lookupHost(info.getNetMask());
            tTmpDhcpInfo.dns1 = lookupHost(info.getDns1Addr());
            tTmpDhcpInfo.dns2 = lookupHost(info.getDns2Addr());			
			
			//*
			if(NetworkUtils.configureInterface(mInterfaceName, tTmpDhcpInfo))
			{
				Slog.i(TAG,"+++++NetworkUtils.configureInterface ok+++++++++");
			}
			else//fail
			{
				Slog.i(TAG,"+++++NetworkUtils.configureInterface fail+++++++++");
			}//*/
			//addDefaultRoute();
//*   
         mLinkProperties.clear();
            mLinkProperties.setInterfaceName(mInterfaceName);						

            InetAddress destAddr = NetworkUtils.numericToInetAddress(info.getIpAddress());
            InetAddress maskAddr = NetworkUtils.numericToInetAddress("255.255.255.0");//info.getNetMask());
            int prefixLength = NetworkUtils.netmaskIntToPrefixLength(NetworkUtils.inetAddressToInt(maskAddr));
            LinkAddress linkAddress = new LinkAddress(destAddr, prefixLength);
            mLinkProperties.addLinkAddress(linkAddress);

 			mLinkProperties.addDns(NetworkUtils.numericToInetAddress(dns1));
			mLinkProperties.addDns(NetworkUtils.numericToInetAddress(dns2));
            RouteInfo route = new RouteInfo(null, NetworkUtils.numericToInetAddress(gateway));
            mLinkProperties.addRoute(route);

//*/			
			return true;
		}


	public void handleMessage(Message msg) {

		synchronized (this) {
			switch (msg.what) {
			case EVENT_PPPOE_CONNECTED://conn :HQS
				//setpppoeState(true, msg.what);	//zengyongying: move this to the end of this case,
				System.out.println("///////////////////////////////");
			 	Slog.i(TAG, "begin to configure PPOE");
				 mEM = (PppoeManager)mContext.getSystemService(Context.ADSL_SERVICE);
                    		 
					int state = mEM.getpppoeState();
					if (state != mEM.POE_STATE_DISABLED) {
						PppoeDevInfo info = mEM.getSavedPppoeConfig();
						if (info != null) {
							try {
								configureInterface(info);
							} catch (UnknownHostException e) {
							    e.printStackTrace();
							}
						}
					}
				NetworkUtils.removeDefaultRoute("eth0");//20111021	
				
				setpppoeState(true, msg.what);//zengyongying: set state only when all into ready for CS
				Slog.i(TAG, "end to configure PPOE");
				break;
			case EVENT_PPPOE_DISCONNECTED://DIS:HQS
				setpppoeState(false, msg.what);
				break;
			case EVENT_PPPOE_AUTH_FAIL://FAIL:HQS
				FailStateReport(false,EVENT_PPPOE_AUTH_FAIL);
				Slog.i(TAG, "--received EVENT_PPPOE_AUTH_FAIL--");
				break;
			}
		}
	}

	public void notifyPhyConnected(String ifname) {
		Slog.i(TAG, "--report auth fail for " + ifname);
		synchronized(this) {
			this.sendEmptyMessage(EVENT_PPPOE_AUTH_FAIL);
		}

	}

	public void notifyStateChange(String ifname,DetailedState state) 
	{
		Slog.i(TAG, "--report new state " + state.toString() + " on dev " + ifname);
			synchronized(this) {
				this.sendEmptyMessage(state.equals(DetailedState.CONNECTED)
					? EVENT_PPPOE_CONNECTED : EVENT_PPPOE_DISCONNECTED);
			}
	}

}

