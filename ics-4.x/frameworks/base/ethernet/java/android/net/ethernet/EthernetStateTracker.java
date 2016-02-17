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

package android.net.ethernet;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import android.os.Bundle;
import android.R;
import android.app.Notification;
import android.app.ActivityManagerNative;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.net.LinkCapabilities;
import android.net.LinkProperties;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.DhcpInfoInternal;
import android.net.NetworkStateTracker;
import android.net.NetworkUtils;
import android.net.LinkCapabilities;
import android.net.NetworkInfo;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkStateTracker;
import android.net.NetworkUtils;
import android.net.ProxyProperties;
import android.net.LinkAddress;
import android.net.RouteInfo;
import android.text.TextUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.SystemProperties;
import android.util.*;

import java.util.concurrent.atomic.AtomicBoolean;

import android.net.DhcpInfo;
import android.provider.Settings;

/**
 * Track the state of Ethernet connectivity. All event handling is done here,
 * and all changes in connectivity state are initiated here.
 *
 * @hide
 */

public class EthernetStateTracker extends Handler implements NetworkStateTracker {
    private static final String TAG                                 = "EthernetStateTracker";
    private static final String DEFAULT_DNS_PROPERTY="net.dns1";
    private static final String DEFAULT_DNS_SERVER="8.8.8.8";
    private static final String DEFAULT_PROXY_HOST_PROPERTY="http.proxyHost";
    private static final String DEFAULT_PROXY_PORT_PROPERTY="http.proxyPort";
    private static final String VERSION="v1.0.1";

    public static final int EVENT_DHCP_START                        = 0;
    public static final int EVENT_INTERFACE_CONFIGURATION_SUCCEEDED = 1;
    public static final int EVENT_INTERFACE_CONFIGURATION_FAILED    = 2;
    public static final int EVENT_HW_CONNECTED                      = 3;
    public static final int EVENT_HW_DISCONNECTED                   = 4;
    public static final int EVENT_HW_PHYCONNECTED                   = 5;
    private static final int NOTIFY_ID                              = 6;
    private static final boolean localLOGV = true;

    private AtomicBoolean mTeardownRequested = new AtomicBoolean(false);
    private AtomicBoolean mPrivateDnsRouteSet = new AtomicBoolean(false);
    private AtomicBoolean mDefaultRouteSet = new AtomicBoolean(false);

    private EthernetManager mEM;
    private boolean mServiceStarted;
    private NetworkInfo mNetworkInfo;
    private LinkProperties mLinkProperties;

    private boolean mStackConnected;
    private boolean mHWConnected;
    private boolean mInterfaceStopped;
//    private boolean mIsStaticIPMode;
    private DhcpHandler mDhcpTarget;
    private String mInterfaceName ;
    private DhcpInfoInternal mDhcpInfo;
    private EthernetMonitor mMonitor;
    private String[] sDnsPropNames;
    private boolean mStartingDhcp;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private Handler mTrackerTarget;

    private BroadcastReceiver mEthernetStateReceiver;

    /* For sending events to connectivity service handler */
    private Handler mCsHandler;
	private Context mContext;

    public EthernetStateTracker(Context context, Handler target) {
        mNetworkInfo = new NetworkInfo(ConnectivityManager.TYPE_ETHERNET, 0, "ETH", "");
        if (localLOGV) Slog.v(TAG, "Starts...");

        if (EthernetNative.initEthernetNative() != 0) {
            Slog.e(TAG,"Can not init ethernet device layers");
            return;
        }

        if (localLOGV) Slog.v(TAG,"Successed");
        mServiceStarted = true;
        HandlerThread dhcpThread = new HandlerThread("DHCP Handler Thread");
        dhcpThread.start();
        mDhcpTarget = new DhcpHandler(dhcpThread.getLooper(), this);
        mMonitor = new EthernetMonitor(this);
        mDhcpInfo = new DhcpInfoInternal();
        mLinkProperties = new LinkProperties();
    }

    /**
     * Stop etherent interface
     * @param suspend {@code false} disable the interface {@code true} only reset the connection without disable the interface
     * @return true
     */
    public boolean stopInterface(boolean suspend) {
        if (mEM != null) {
            EthernetDevInfo info = mEM.getSavedConfig();
            if (info != null && mEM.isConfigured()) {
                synchronized (mDhcpTarget) {
                    mInterfaceStopped = true;
                    if (localLOGV) Slog.i(TAG, "stop dhcp and interface");
                    mDhcpTarget.removeMessages(EVENT_DHCP_START);
                    String ifname = info.getIfName();

                    if (!NetworkUtils.stopDhcp(ifname)) {
                        if (localLOGV) Slog.w(TAG, "Could not stop DHCP");
                    }
                    mLinkProperties.clear();
                    NetworkUtils.resetConnections(ifname, NetworkUtils.RESET_ALL_ADDRESSES);
                    if (!suspend) {
                        NetworkUtils.disableInterface(ifname);
					//	Begin added by TCL Xi'an NT liukun:liukun@tcl.com -2012.4.16
                    }else{
                    	boolean ret;
											ret = NetworkUtils.clearAddresses(ifname);
											if (localLOGV) Slog.w(TAG, "stopInterface --> clearAddresses ret = "+ret);
										}
					//	End added by TCL Xi'an NT liukun:liukun@tcl.com -2012.4.16
                    mLinkProperties.clear();
            		}
        		}
        
		        if (ActivityManagerNative.isSystemReady()) {
		        	Intent intent =  new Intent(EthernetManager.ETHERNET_STOP_INTERFACE_ACTION);
		        	intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT);
		        	mContext.sendBroadcast(intent);
		        }
        }
        return true;
    }
    
//    private int convertInetAddrToInt(String addr) {
//        if (addr != null) {
//            try {
//                InetAddress inetAddress = NetworkUtils.numericToInetAddress(addr);
//                if (inetAddress instanceof Inet4Address) {
//                    return NetworkUtils.inetAddressToInt(inetAddress);
//                }
//            } catch (IllegalArgumentException e) {}
//        }
//        return 0;
//    }
    

    private boolean configureInterface(EthernetDevInfo info) throws UnknownHostException {
        mStackConnected = false;
        mHWConnected = false;
        mInterfaceStopped = false;

        if (info.getConnectMode().equals(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP)) {
            if (localLOGV) Slog.i(TAG, "trigger dhcp for device sss " + info.getIfName());
            sDnsPropNames = new String[] {
                    "dhcp." + mInterfaceName + ".dns1",
                    "dhcp." + mInterfaceName + ".dns2"
                 };
            
            mStartingDhcp = true;
//            mIsStaticIPMode = false;
            mDhcpTarget.sendEmptyMessage(EVENT_DHCP_START);
        } else {
            DhcpInfo tTmpDhcpInfo = new DhcpInfo();
            int event;
            
            sDnsPropNames = new String[] {
                    "net." + mInterfaceName + ".dns1",
                    "net." + mInterfaceName + ".dns2"
                 };
            
//            mIsStaticIPMode = true;
            
            tTmpDhcpInfo.ipAddress = lookupHost(info.getIpAddress());
            tTmpDhcpInfo.gateway = lookupHost(info.getRouteAddr());
            tTmpDhcpInfo.netmask = lookupHost(info.getNetMask());
            tTmpDhcpInfo.dns1 = lookupHost(info.getDnsAddr());
            tTmpDhcpInfo.dns2 = lookupHost(info.getDns2Addr());
 
            if (localLOGV) Slog.i(TAG, "set ip manually " + info.getIpAddress());
            NetworkUtils.removeDefaultRoute(info.getIfName());
            if (NetworkUtils.configureInterface(info.getIfName(), tTmpDhcpInfo)) {
                event = EVENT_INTERFACE_CONFIGURATION_SUCCEEDED;
                SystemProperties.set("net.dns1", info.getDnsAddr());
				SystemProperties.set("net." + info.getIfName() + ".dns1", info.getDnsAddr());
				SystemProperties.set("net." + info.getIfName() + ".dns2", "0.0.0.0");
                mLinkProperties.clear();
                mLinkProperties.setInterfaceName(mInterfaceName);						

                InetAddress destAddr = NetworkUtils.numericToInetAddress(info.getIpAddress());
                InetAddress maskAddr = NetworkUtils.numericToInetAddress(info.getNetMask());
                int prefixLength = NetworkUtils.netmaskIntToPrefixLength(NetworkUtils.inetAddressToInt(maskAddr));
                LinkAddress linkAddress = new LinkAddress(destAddr, prefixLength);
                mLinkProperties.addLinkAddress(linkAddress);

                if (TextUtils.isEmpty(info.getDnsAddr()) == false) {
                    InetAddress dns1Addr = NetworkUtils.numericToInetAddress(info.getDnsAddr());
                    mLinkProperties.addDns(dns1Addr);
                } 

                if (TextUtils.isEmpty(info.getDns2Addr()) == false) {
                    InetAddress dns2Addr = NetworkUtils.numericToInetAddress(info.getDns2Addr());
                    mLinkProperties.addDns(dns2Addr);
                }

                InetAddress gatewayAddr = NetworkUtils.numericToInetAddress(info.getRouteAddr());
                RouteInfo route = new RouteInfo(null, gatewayAddr);
                mLinkProperties.addRoute(route);
                if (localLOGV) Slog.v(TAG, "Static IP configuration succeeded, update LinkProperties");
            } else {
                event = EVENT_INTERFACE_CONFIGURATION_FAILED;
                if (localLOGV) Slog.w(TAG, "Static IP configuration failed");
            }
            this.sendEmptyMessage(event);
        }
        return true;
    }



    /**
     * init ethernet interface
     * @return true
     * @throws UnknownHostException
     */
    public boolean initInterface() throws UnknownHostException{
        /*
         * This will guide us to enabled the enabled device
         */
        if (mEM != null) {
            EthernetDevInfo info = mEM.getSavedConfig();
            if (info != null && mEM.isConfigured()) {
                synchronized (this) {
                    mInterfaceName = info.getIfName();
                    if (localLOGV) Slog.i(TAG, "reset device " + mInterfaceName);
                    NetworkUtils.resetConnections(mInterfaceName, NetworkUtils.RESET_ALL_ADDRESSES);
                    NetworkUtils.enableInterface(mInterfaceName);

                   if (localLOGV) Slog.w(TAG, "reset Connection  185 . ");
                     // Stop DHCP
                    if (mDhcpTarget != null) {
                        mDhcpTarget.removeMessages(EVENT_DHCP_START);
                    }

	                if (localLOGV) Slog.w(TAG, "reset remove Message  191.");
                    if (ActivityManagerNative.isSystemReady()) {
                    	if (localLOGV) Slog.i(TAG, " Activity Manager native is System Ready  193.");
                    	Intent intent =  new Intent(EthernetManager.ETHERNET_START_INTERFACE_ACTION);
                    	intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT);
                    	mContext.sendBroadcast(intent);
                    }
                    
                    if (localLOGV)  Slog.i(TAG, "after send broaad cast 199. ");
                    configureInterface(info);
                }
            }
        }
        
        return true;
    }

    /**
     * reset ethernet interface
     * @return true
     * @throws UnknownHostException
     */
    public boolean resetInterface()  throws UnknownHostException{
        /*
         * This will guide us to enabled the enabled device
         */
        if (mEM != null) {
            EthernetDevInfo info = mEM.getSavedConfig();
            if (info != null && mEM.isConfigured()) {
                synchronized (this) {
                    mInterfaceName = info.getIfName();
                    if (localLOGV) Slog.i(TAG, "reset device " + mInterfaceName);
                    NetworkUtils.resetConnections(mInterfaceName, NetworkUtils.RESET_ALL_ADDRESSES);
                    NetworkUtils.enableInterface(mInterfaceName);
                     // Stop DHCP
                    if (mDhcpTarget != null) {
                        mDhcpTarget.removeMessages(EVENT_DHCP_START);
                    }
                    if (!NetworkUtils.stopDhcp(mInterfaceName)) {
                        if (localLOGV) Slog.w(TAG, "Could not stop DHCP");
                    }
                    mLinkProperties.clear();
                    configureInterface(info);
                }
            }
        }
        return true;
    }

/* HFM
    @Override
    public String[] getNameServers() {
        return getNameServerList(sDnsPropNames);
    }
*/
    @Override
    public String getTcpBufferSizesPropName() {
        return "net.tcp.buffersize.default";
    }

    public void StartPolling() {
        mMonitor.startMonitoring();
    }
    @Override
    public boolean isAvailable() {
        // Only say available if we have interfaces and user did not disable us.
        return ((mEM.getTotalInterface() != 0) && (mEM.getState() != EthernetManager.ETHERNET_STATE_DISABLED));
    }

    @Override
    public boolean reconnect() {
        try {
            synchronized (this) {
                if (mHWConnected && mStackConnected)
                    return true;
            }
            if (mEM.getState() != EthernetManager.ETHERNET_STATE_DISABLED) {
                // maybe this is the first time we run, so set it to enabled
                mEM.setEnabled(true);
                if (!mEM.isConfigured()) {
                    mEM.setDefaultConf();
                }
                return resetInterface();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public boolean setRadio(boolean turnOn) {
        return false;
    }

    @Override
    public void startMonitoring(Context context, Handler target) {
        if (localLOGV) Slog.v(TAG,"start to monitor the ethernet devices");
        if (mServiceStarted) {
            mEM = (EthernetManager)context.getSystemService(Context.ETHERNET_SERVICE);
			mContext = context;
	        mCsHandler = target;

///		    IntentFilter filter = new IntentFilter();
///		    filter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);

///		    mEthernetStateReceiver = new EthernetStateReceiver();
///		    mContext.registerReceiver(mEthernetStateReceiver, filter);
            int state = mEM.getState();
            if (state != mEM.ETHERNET_STATE_DISABLED) {
                if (state == mEM.ETHERNET_STATE_UNKNOWN) {
                    // maybe this is the first time we run, so set it to enabled
                    mEM.setEnabled(mEM.getDeviceNameList() != null);
                } else {
                    try {
                        resetInterface();
                    } catch (UnknownHostException e) {
                        Slog.e(TAG, "Wrong ethernet configuration");
                    }
                }
            }
        }
    }

/* HFM
    @Override
    public int startUsingNetworkFeature(String feature, int callingPid, int callingUid) {
        return 0;
    }

    @Override
    public int stopUsingNetworkFeature(String feature, int callingPid, int callingUid) {
        return 0;
    }
*/
    @Override
    public boolean teardown() {
        return (mEM != null) ? stopInterface(false) : false;
    }

    @Override
    public void setUserDataEnable(boolean enabled) {
        Slog.w(TAG, "ignoring setUserDataEnable(" + enabled + ")");
    }

    @Override
    public void setPolicyDataEnable(boolean enabled) {
        Slog.w(TAG, "ignoring setPolicyDataEnable(" + enabled + ")");
    }

    private void IntentBroadcast(int event) {
        Intent intent = new Intent(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT);
        intent.putExtra(EthernetManager.EXTRA_ETHERNET_STATE, event);
		//	Begin added by TCL Xi'an NT liukun:liukun@tcl.com -2012.4.16
        //mContext.sendStickyBroadcast(intent);
        mContext.sendBroadcast(intent);
		//	End added by TCL Xi'an NT liukun:liukun@tcl.com -2012.4.16
        if (localLOGV) Slog.i(TAG, "IntentBroadcast, event=" + event);
    }


    private void postNotification(int event) {
        Message msg = mCsHandler.obtainMessage(EVENT_STATE_CHANGED, new NetworkInfo(mNetworkInfo));
        msg.sendToTarget();
    	 if (localLOGV) Slog.i(TAG," post notifcation: " + event);
        final Intent intent = new Intent(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT);
        intent.putExtra(EthernetManager.EXTRA_ETHERNET_STATE, event);
        mContext.sendStickyBroadcast(intent);
    }
	//add by HQS for adsl when cable unplug--20110720
	private void ReportEthAbnormal() {
		Slog.d(TAG, "--ReportEthAbnormal--");
		mCsHandler.sendEmptyMessage(EVENT_ETH_ABNORMAL);
	}
    private void setState(boolean state, int event) {
        if (mNetworkInfo.isConnected() != state) {
            if (state) {
                if(getLinkStatus(mInterfaceName)){
                    mNetworkInfo.setDetailedState(DetailedState.CONNECTED, null, null);
                    }
            } else {                
                mNetworkInfo.setDetailedState(DetailedState.DISCONNECTED, null, null);
                stopInterface(true);
            }
            mNetworkInfo.setIsAvailable(state);
            postNotification(event);
        } else if (state) {
        	 postNotification(event);
        }
        
    }
//	Begin added by TCL Xi'an NT liukun:liukun@tcl.com
		private void sendEthBroadcast(String action, boolean state){
			Intent intent = new Intent(action);
			intent.putExtra(EthernetManager.EXTRA_ETHERNET_STATE, state);
	
			if (localLOGV) Slog.i(TAG, "sendEthBroadcast  --> action = "+action
										+" state = "+state);
			
			mContext.sendBroadcast(intent);
	}
//	End added by TCL Xi'an NT liukun:liukun@tcl.com

    public void handleMessage(Message msg) {

        synchronized (this) {
            switch (msg.what) {
            case EVENT_INTERFACE_CONFIGURATION_SUCCEEDED:
                if (localLOGV) Slog.i(TAG, "received configured succeeded, aaa stack=" + mStackConnected + " HW=" + mHWConnected);
                mStackConnected = true;                
//                if (mIsStaticIPMode) {
//                	setState(true, msg.what);
//                	break;
//                } else if (mHWConnected)
                    setState(true, msg.what);
                EthernetDevInfo DevInfo = mEM.getSavedConfig();
                SystemProperties.set(DEFAULT_DNS_PROPERTY, DevInfo.getDnsAddr());
                
                //	Begin added by TCL Xi'an NT liukun:liukun@tcl.com
                sendEthBroadcast(EthernetManager.ETHERNET_INTERFACE_CONF_CHANGED, true);
                //	End added by TCL Xi'an NT liukun:liukun@tcl.com
                break;
            case EVENT_INTERFACE_CONFIGURATION_FAILED:
                mStackConnected = false;
                //start to retry ?
                //	Begin added by TCL Xi'an NT liukun:liukun@tcl.com
                sendEthBroadcast(EthernetManager.ETHERNET_INTERFACE_CONF_CHANGED, false);
                //	End added by TCL Xi'an NT liukun:liukun@tcl.com
                break;
            case EVENT_HW_CONNECTED:
                if (localLOGV) Slog.i(TAG, "received HW connected, stack=" + mStackConnected + " HW=" + mHWConnected);
                mHWConnected = true;
                mStackConnected = true;
                setState(true, msg.what);
                break;
            case EVENT_HW_DISCONNECTED:
				ReportEthAbnormal();//add by HQS for adsl disconnect because eth has been unpluged---20110720
                if (localLOGV) Slog.i(TAG, "received disconnected events, stack=" + mStackConnected + " HW=" + mHWConnected);
                setState(mHWConnected = false, msg.what);
                break;
            case EVENT_HW_PHYCONNECTED:
                if (localLOGV) Slog.i(TAG, "interface up event, kick off connection request");
                if (!mStartingDhcp) {
                    int state = mEM.getState();
                    if (state != mEM.ETHERNET_STATE_DISABLED) {
                        EthernetDevInfo info = mEM.getSavedConfig();
                        if (info != null && mEM.isConfigured()) {
                            try {
                                configureInterface(info);
                            } catch (UnknownHostException e) {
                                 // TODO Auto-generated catch block
                                 //e.printStackTrace();
                                 Slog.e(TAG, "Cannot configure interface");
                            }
                        }
                    }
                }
                break;
            }
        }
    }
    private void updateDhcpDevInfo(DhcpInfoInternal DHCPInfoInt) {
        EthernetDevInfo info = mEM.getSavedConfig();

        Slog.d(TAG, "updateDhcpDevInfo, mServiceStarted = " + mServiceStarted + ". mEM.isConfigured()= " + mEM.isConfigured());
        if ( (info!=null) && mEM.isConfigured() ) {

            DhcpInfo DHCPInfo = DHCPInfoInt.makeDhcpInfo();

            info.setIpAddress(NetworkUtils.intToInetAddress(DHCPInfo.ipAddress).getHostAddress());
            info.setRouteAddr(NetworkUtils.intToInetAddress(DHCPInfo.gateway).getHostAddress());
            info.setDnsAddr(NetworkUtils.intToInetAddress(DHCPInfo.dns1).getHostAddress());
            info.setDns2Addr(NetworkUtils.intToInetAddress(DHCPInfo.dns2).getHostAddress());
            info.setNetMask(NetworkUtils.intToInetAddress(DHCPInfo.netmask).getHostAddress());
						
						
            if (mServiceStarted) {
                final ContentResolver cr = mContext.getContentResolver();
                Settings.Secure.putString(cr, Settings.Secure.ETHERNET_IP, info.getIpAddress());
                Settings.Secure.putString(cr, Settings.Secure.ETHERNET_DNS, info.getDnsAddr());
                Settings.Secure.putString(cr, Settings.Secure.ETHERNET_DNS2, info.getDns2Addr());
                Settings.Secure.putString(cr, Settings.Secure.ETHERNET_ROUTE, info.getRouteAddr());
                Settings.Secure.putString(cr, Settings.Secure.ETHERNET_MASK, info.getNetMask());
            }
        }
        Slog.d(TAG, "updateDhcpDevInfo, IP " + info.getIpAddress() + ", GW " + info.getRouteAddr() + ", DNS " + info.getDnsAddr() + ", MASK " + info.getNetMask());
    }
    private class DhcpHandler extends Handler {
         public DhcpHandler(Looper looper, Handler target) {
             super(looper);
             mTrackerTarget = target;
         }

         public void handleMessage(Message msg) {
             int event;

             switch (msg.what) {
                 case EVENT_DHCP_START:
 //                    synchronized (mDhcpTarget) {//hzy fix CR:366762
                         if (!mInterfaceStopped) {
                         //jipson ++ CR382173
                         if (!getLinkStatus(mInterfaceName))
                         {
                             if (localLOGV) Slog.d(TAG, "I/F is not up yet, not to do dhcp");
                             mStartingDhcp = false;
                             return;
                         }
                         //jipson --                          	
                             if (localLOGV) Slog.d(TAG, "DhcpHandler: DHCP request started");

							 DhcpInfoInternal tmpdhcpInfoInternal = new DhcpInfoInternal();
                             if (NetworkUtils.runDhcp(mInterfaceName, tmpdhcpInfoInternal)) {
                                 event = EVENT_INTERFACE_CONFIGURATION_SUCCEEDED;
                            mHWConnected = true; // If DHCP succeeded, HW must be connected
                                 Slog.d(TAG, "DhcpHandler: DHCP request succeeded: " + tmpdhcpInfoInternal.toString());
                             } else {
                                 event = EVENT_INTERFACE_CONFIGURATION_FAILED;
                                 Slog.e(TAG, "DhcpHandler: DHCP request failed: " + NetworkUtils.getDhcpError());
                             }
							 mDhcpInfo = tmpdhcpInfoInternal;
							 mLinkProperties = mDhcpInfo.makeLinkProperties();
							 mLinkProperties.setInterfaceName(mInterfaceName);

							 updateDhcpDevInfo(tmpdhcpInfoInternal);
                             mTrackerTarget.sendEmptyMessage(event);
                         } else {
                             mInterfaceStopped = false;
                         }
                         mStartingDhcp = false;
 //                    }
                     break;
             }
         }
    }

    public void notifyPhyConnected(String ifname) {
        if (localLOGV) Slog.v(TAG, "report interface is up for " + ifname);
        synchronized(this) {
            this.sendEmptyMessage(EVENT_HW_PHYCONNECTED);
        }
    }

    public void notifyStateChange(String ifname,DetailedState state) {
        if (localLOGV) Slog.i(TAG, "report new state " + state.toString() + " on dev " + ifname);
        if (ifname.equals(mInterfaceName)) {
            if (localLOGV) Slog.v(TAG, "update network state tracker");
            synchronized(this) {
                this.sendEmptyMessage(state.equals(DetailedState.CONNECTED)
                    ? EVENT_HW_CONNECTED : EVENT_HW_DISCONNECTED);
            }
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

    public void setDependencyMet(boolean met) {
        // not supported on this network
    }
/*HFM stubs */
    public void setDataEnable(boolean enabled) {
	}
    public void setTeardownRequested(boolean isRequested) {
        mTeardownRequested.set(isRequested);
    }

    public boolean isTeardownRequested() {
        return mTeardownRequested.get();
    }
    /**
     * Check if private DNS route is set for the network
     */
    public boolean isPrivateDnsRouteSet() {
        return mPrivateDnsRouteSet.get();
    }

    /**
     * Set a flag indicating private DNS route is set
     */
    public void privateDnsRouteSet(boolean enabled) {
        mPrivateDnsRouteSet.set(enabled);
    }

    /**
     * Fetch NetworkInfo for the network
     */
    public NetworkInfo getNetworkInfo() {
        return new NetworkInfo(mNetworkInfo);
    }

    /**
     * Fetch LinkProperties for the network
     */
    public LinkProperties getLinkProperties() {
        return new LinkProperties(mLinkProperties);
    }

    /**
     * A capability is an Integer/String pair, the capabilities
     * are defined in the class LinkSocket#Key.
     *
     * @return a copy of this connections capabilities, may be empty but never null.
     */
    public LinkCapabilities getLinkCapabilities() {
        return new LinkCapabilities();
    }

    /**
     * Check if default route is set
     */
    public boolean isDefaultRouteSet() {
        return mDefaultRouteSet.get();
    }

    /**
     * Set a flag indicating default route is set for the network
     */
    public void defaultRouteSet(boolean enabled) {
        mDefaultRouteSet.set(enabled);
    }

	public DhcpInfo getDhcpInfo() {
        return mDhcpInfo.makeDhcpInfo();
    }
    
    public String getMacAddr() {
    	return EthernetNative.getMacAddressCommand();
    }
    
    public boolean getLinkStatus(String nic) {
    	if (EthernetNative.getLinkStatus() == 0) {
			if (localLOGV) Slog.i(TAG, "getLinkStatus return false");
    		return false;
    	}
    	if (localLOGV) Slog.i(TAG, "getLinkStatus return true");
    	return true;
    }

}
