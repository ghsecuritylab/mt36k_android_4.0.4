package com.mediatek.ui.menu.util;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.mediatek.netcm.ethernet.EthernetImplement;
import com.mediatek.netcm.wifi.WifiAccessPoint;
import com.mediatek.netcm.wifi.WifiAvailableAp;
import com.mediatek.netcm.wifi.WifiConst;
import com.mediatek.netcm.wifi.WifiDevListener;
//import com.mediatek.netcm.wifi.WifiDevManager;
import com.mediatek.netcm.wifi.WifiDongleControl;
import com.mediatek.netcm.wifi.WifiManualSetting;
import com.mediatek.netcm.wifi.WifiStatus;
import com.mediatek.netcm.wifi.WifiWpsConnect;
import com.mediatek.ui.menu.MenuMain;
import com.mediatek.ui.util.MtkLog;

/**
 * @author hs_lechen net connect interface
 */
public class NetWork {

	/** net connect mode ethernet **/
	public static final String MODE_ETHERNET = "ETHERNET";

	/** net connect mode wifi **/
	public static final String MODE_WIRELESS = "WIRELESS";

	/** address mode dhcp **/
	public static final String TYPE_AUTO = "AUTO";

	/** address mode manual **/
	public static final String TYPE_MANUAL = "MANUAL";

	/** NetWork Singleton **/
	private static NetWork netWork;

	/** context **/
	private Context context;

	/** sharedPreference **/
	private SaveValue save;

	/** whether the net is open **/
	public Boolean internetConnect;

	/** Ethernet interface **/
	private EthernetImplement ethernet;

	private WifiDongleControl wifiDongleControl;
	private WifiStatus wifiStatus;
	private WifiManualSetting wifiManualSetting;
	private WifiAvailableAp wifiAvailableAp;
	private WifiWpsConnect wifiWpsConnect;

	//private WifiDevManager wifiDevManager;

//	IntentFilter closeEthernetFilter;
//	private boolean isPreventCloseEthernet = true;

//	private DhcpReceiver closeEthernetReceiver = new DhcpReceiver();
	private EthernetOnOffReceiver ethernetOnOffReceiver = new EthernetOnOffReceiver();
	private WifiOnOffReceiver wifiOnOffReceiver = new WifiOnOffReceiver();

	private boolean etherRegistered = false;
	private boolean wifiRegistered = false;

//	public class DhcpReceiver extends BroadcastReceiver {
//
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//
//			Log.v(TAG, "catch broadcast in DhcpReceiver");
//			if (action.equals(EthernetImplement.ETHERNET_NOTIFY)) {
//				int event = intent.getIntExtra(EthernetImplement.DHCP_STATE,
//						EthernetImplement.E_DHCP_ORIGINAL);
//				switch (event) {
//				case EthernetImplement.E_DHCP_SUCCESS:
//					openInternetConnectSafety(0);
//					context.unregisterReceiver(this);
//					break;
//
//				case EthernetImplement.E_DHCP_FAILED:
//					break;
//				}
//			}
//		}
//	}

	public class EthernetOnOffReceiver extends BroadcastReceiver {

		private int state;

		public void setState(int state) {
			this.state = state;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			MtkLog.v(TAG, "catch broadcast in EthernetOnOffReceiver");
			MtkLog.d(TAG, "***********************" + action
					+ "**********************************");
			if (action != null && action.equals(EthernetImplement.ETHERNET_STOP_INTERFACE_NOTIFY)
					&& state == 0) {
				((MenuMain) context).setNetBack(false);
				MtkLog.v(TAG, "*************ethernet closed*****************");
				if (etherRegistered) {
					MtkLog.v(TAG, "unregister ethernet off");
					context.unregisterReceiver(this);
					etherRegistered = false;
				}
			} else if (action
					.equals(EthernetImplement.ETHERNET_START_INTERFACE_NOTIFY)
					&& state == 1) {
				((MenuMain) context).setNetBack(false);
				MtkLog.v(TAG, "*************ethernet open*****************");
				if (etherRegistered) {
					MtkLog.v(TAG, "unregister ethernet on");
					context.unregisterReceiver(this);
					etherRegistered = false;
				}
			}
		}
	}

	public class WifiOnOffReceiver extends BroadcastReceiver {

		private int state;

		public void setState(int state) {
			this.state = state;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			MtkLog.v(TAG, "catch broadcast in WifiOnOffReceiver");
			MtkLog.d(TAG, "***********************" + action
					+ "**********************************");
			MtkLog.d(TAG, "***********************"
					+ wifiDongleControl.getWifiState()
					+ "**********************************");
			if (action != null && action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				int wifiState = wifiDongleControl.getWifiState();
				if (state == 0
						&& ((wifiState == WifiConst.W_STATE_DISABLED || wifiState == WifiConst.W_STATE_UNKNOWN))) {
					MtkLog.d(TAG, "**************wifi disabled****************");
					((MenuMain) context).setWifiBack(false);
					if (wifiRegistered) {
						MtkLog.v(TAG, "unregister wifi off");
						context.unregisterReceiver(this);
						wifiRegistered = false;
					}
				} else if (state == 1
						&& (wifiState == WifiConst.W_STATE_ENABLED || wifiState == WifiConst.W_STATE_UNKNOWN)) {
					MtkLog.d(TAG, "**************wifi enabled****************");
					((MenuMain) context).setWifiBack(false);
					if (wifiRegistered) {
						MtkLog.v(TAG, "unregister wifi on");
						context.unregisterReceiver(this);
						wifiRegistered = false;
					}
				}
			}
		}
	}

	private static final String TAG = "NetWork";

	private long start, end;

	private NetWork(Context mcontext) {		
		this.context=mcontext;
	}

	/**
	 * name:initial net setting author:hs_lechen
	 */
	private void initSetting() {
		save = SaveValue.getInstance(context);
		internetConnect = save.readBooleanValue("internetConnect");
		ethernet = EthernetImplement.getInstance(context);
		// WifiConst.DummyMode=false;
		wifiDongleControl = WifiDongleControl.getInstance(context);
		wifiStatus = WifiStatus.getInstance(context);
		wifiManualSetting = WifiManualSetting.getInstance(context);
		wifiAvailableAp = WifiAvailableAp.getInstance(context);
		wifiWpsConnect = WifiWpsConnect.getInstance(context);

	//	wifiDevManager = WifiDevManager.getInstance();
		

//		closeEthernetFilter = new IntentFilter();
//		closeEthernetFilter.addAction(EthernetImplement.ETHERNET_NOTIFY);

		// DhcpReceiver dr=new DhcpReceiver();
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(EthernetImplement.ETHERNET_NOTIFY);
		// context.registerReceiver(dr, filter);

		
		MtkLog.i(TAG, "initial complete!!!");
	}

	public boolean isWifiDongleExist() {
		return wifiDongleControl.isWifiDongleExist();
	}

	public void setWifiDongleListener(WifiDevListener wifiDevListener) {
	//	wifiDevManager.setOnDevEventListener(wifiDevListener);
	}

	public void removeWifiDongleListener() {
	//	wifiDevManager.setOffDevEventListener();
	}

	public boolean openWifi() {
		return wifiDongleControl.openWifi();
	}

	public boolean closeWifi() {
		return wifiDongleControl.closeWifi();
	}

	public void disconnectWifi() {		
		wifiAvailableAp.disConnect();
		wifiAvailableAp.removeAllNetwork();
	}

	public void disconnectWpsWifi() {		
		wifiWpsConnect.wpsCancel();
		wifiWpsConnect.removeAllNetwork();
	}

	public int getWifiState() {
		return wifiDongleControl.getWifiState();
	}

	public boolean isWifiAPConnected() {
		return wifiStatus.isWifiConnected();
	}

	public boolean connectWifi(String ssid, int auth, String password) {
		switch (auth) {
		case MenuConfigManager.W_CONFIRM_UNKNOWN:
			return (wifiManualSetting.manualEnableNetworkLink(ssid,
					WifiConst.W_CONFIRM_WPA_PSK, WifiConst.W_ENCRYPT_TKIP,
					password)
					|| wifiManualSetting.manualEnableNetworkLink(ssid,
							WifiConst.W_CONFIRM_WPA_PSK,
							WifiConst.W_ENCRYPT_AES, password)
					|| wifiManualSetting.manualEnableNetworkLink(ssid,
							WifiConst.W_CONFIRM_WPA2_PSK,
							WifiConst.W_ENCRYPT_TKIP, password)
					|| wifiManualSetting.manualEnableNetworkLink(ssid,
							WifiConst.W_CONFIRM_WPA2_PSK,
							WifiConst.W_ENCRYPT_AES, password) || wifiManualSetting
					.manualEnableNetworkLink(ssid, WifiConst.W_CONFIRM_WEP,
							WifiConst.W_ENCRYPT_WEP, password));

		case MenuConfigManager.W_CONFIRM_NONE:
			return wifiManualSetting.manualEnableNetworkLink(ssid,
					WifiConst.W_CONFIRM_OPEN, WifiConst.W_ENCRYPT_NONE);
		case MenuConfigManager.W_CONFIRM_WEP:
			MtkLog.v(TAG, "now the mode is WEP************************");
			MtkLog.v(TAG, "now the ssid************************" + ssid);
			MtkLog.v(TAG, "now the password************************" + password);
			return wifiManualSetting.manualEnableNetworkLink(ssid,
					WifiConst.W_CONFIRM_WEP, WifiConst.W_ENCRYPT_WEP, password);
		case MenuConfigManager.W_CONFIRM_WPA_PSK_TKIP:
			MtkLog.v(TAG, "now the mode is WPA_PSK_TKIP************************");
			MtkLog.v(TAG, "now the ssid************************" + ssid);
			MtkLog.v(TAG, "now the password************************" + password);
			return wifiManualSetting.manualEnableNetworkLink(ssid,
					WifiConst.W_CONFIRM_WPA_PSK, WifiConst.W_ENCRYPT_TKIP,
					password);
		case MenuConfigManager.W_CONFIRM_WPA_PSK_AES:
			MtkLog.v(TAG, "now the mode is WPA_PSK_AES************************");
			MtkLog.v(TAG, "now the ssid************************" + ssid);
			MtkLog.v(TAG, "now the password************************" + password);
			return wifiManualSetting.manualEnableNetworkLink(ssid,
					WifiConst.W_CONFIRM_WPA_PSK, WifiConst.W_ENCRYPT_AES,
					password);
		case MenuConfigManager.W_CONFIRM_WPA2_PSK_TKIP:
			MtkLog.v(TAG, "now the mode is WPA2_PSK_TKIP************************");
			MtkLog.v(TAG, "now the ssid************************" + ssid);
			MtkLog.v(TAG, "now the password************************" + password);
			return wifiManualSetting.manualEnableNetworkLink(ssid,
					WifiConst.W_CONFIRM_WPA2_PSK, WifiConst.W_ENCRYPT_TKIP,
					password);
		case MenuConfigManager.W_CONFIRM_WPA2_PSK_AES:
			MtkLog.v(TAG, "now the mode is WPA2_PSK_AES************************");
			MtkLog.v(TAG, "now the ssid************************" + ssid);
			MtkLog.v(TAG, "now the password************************" + password);
			return wifiManualSetting.manualEnableNetworkLink(ssid,
					WifiConst.W_CONFIRM_WPA2_PSK, WifiConst.W_ENCRYPT_AES,
					password);
		case MenuConfigManager.W_CONFIRM_AUTO:
			MtkLog.v(TAG, "now the mode is auto************************");
			MtkLog.v(TAG, "now the ssid************************" + ssid);
			MtkLog.v(TAG, "now the password************************" + password);
			return wifiManualSetting.manualEnableNetworkLink(ssid,
					WifiConst.W_CONFIRM_PSK_AUTO, WifiConst.W_ENCRYPT_TKIP_AES,
					password);
		default:
			return false;
		}
	}

	public boolean connectWifi(WifiAccessPoint wifiAccessPoint) {
		return wifiAvailableAp.enableNetworkLink(wifiAccessPoint.mResult);
	}

	public boolean connectWifi(WifiAccessPoint wifiAccessPoint, String pass) {
		return wifiAvailableAp.enableNetworkLink(wifiAccessPoint.mResult, pass);
	}

	public void saveWifiConfigure() {
		wifiManualSetting.saveConfiguaration();
	}

	public boolean scanAvailableAP(boolean isWps) {
		if (openWifi()) {
			if (!isWps) {
				wifiAvailableAp.registerScanReceiver();
				return wifiAvailableAp.startScan();
			}else{
				wifiWpsConnect.registerWpsScanReceiver();
				return wifiWpsConnect.startScan();
			}
		} else {
			return false;
		}
	}

	public void unregisterScanReceiver() {
		wifiAvailableAp.unregisterScanReceiver();
	}

	public List<WifiAccessPoint> getScanAccessPoints() {		
		return wifiAvailableAp.sortBySignal(wifiAvailableAp
				.getScanAccessPoints());
	}

	public List<WifiAccessPoint> getWpsScanAccessPoints() {
		return wifiAvailableAp.sortBySignal(wifiWpsConnect
				.getWpsScanResultList());
	}

	public String getPinCode() {
		return wifiWpsConnect.getPinCode();
	}

	public boolean connectPBC() {
		return wifiWpsConnect.enableWpsPbcLink();
	}

	public boolean connectPIN(String bSsid, String pinCode) {
		return wifiWpsConnect.enableWpsPinLink(bSsid, pinCode);
	}	

	/**
	 * name:get singleton of this object
	 * 
	 * @param context
	 * @return
	 * @author hs_lechen
	 */
	public static NetWork getInstance(Context mcontext) {
		MtkLog.v("UIUIUIUIUI", "get NetWork instance");
		if (netWork == null) {
			MtkLog.v("UIUIUIUIUI", "new instance");
			netWork = new NetWork(mcontext);
			netWork.initSetting();
		} else {
			MtkLog.v("UIUIUIUIUI", "old instance");
		}
		return netWork;
	}

	public void setEthernetDummy(boolean isDummy) {
		EthernetImplement.dummyMode = isDummy;
	}

	/**
	 * get connection mode
	 * 
	 * @return auto or manual
	 */
	public String getConnectMode() {
		if (EthernetImplement.dummyMode) {
			return "dhcp";
		}
		return ethernet.getConnectMode();
	}

	public String getIPAddress() {
		if (EthernetImplement.dummyMode) {
			return "172.26.149.55";
		}
		return ethernet.getIpAddress();
	}

	public String getMaskAddress() {
		if (EthernetImplement.dummyMode) {
			return "255.255.255.0";
		}
		return ethernet.getNetMask();
	}

	public String getRouteAddress() {
		if (EthernetImplement.dummyMode) {
			return "172.26.149.254";
		}
		return ethernet.getRouteAddr();
	}

	public String getDnsAddress() {
		if (EthernetImplement.dummyMode) {
			return "172.26.129.15";
		}
		return ethernet.getDnsAddr();
	}

	public String getDns2Address() {
		if (EthernetImplement.dummyMode) {
			return "172.26.129.15";
		}
		return ethernet.getDns2Addr();
	}

	public String getMacAddress() {
		if (EthernetImplement.dummyMode) {
			return "00:00:00:00:00:00";
		}
		return ethernet.getMacAddr();
	}

	public String getWifiIPAddress() {
		return wifiStatus.getIpAddr();
	}

	public String getWifiMaskAddress() {
		return wifiStatus.getNetMask();
	}

	public String getWifiRouteAddress() {
		return wifiStatus.getRouteAddr();
	}

	public String getWifiDnsAddress() {
		return wifiStatus.getDnsAddr();
	}
	
	public String getWifiDns2Address() {
		return wifiStatus.getDns2Addr();
	}

	public String getWifiMacAddress() {
		return wifiStatus.getMacAddr();
	}

//	public void StopDhcpRegister() {
//		if (EthernetImplement.dummyMode) {
//			return;
//		}
//		ethernet.onStopRegister();
//	}

	public void closeEthernet() {
		ethernet.closeEthernet();
	}

	public void openEthernet() {
		ethernet.openEthernet();
	}

	/**
	 * name:open ethernet connect author:hs_lechen
	 */
	public Boolean openInternetConnect() {
		// Message msg = new Message();
		// msg.what = 111;
		// Handler mh = ((MenuMain)context).getMHandler();
		// mh.sendMessage(msg);
		if (EthernetImplement.dummyMode) {
			return true;
		}
		MtkLog.i(TAG, "open the ethernet connection***************");
		if (ethernet.openEthernet()) {
			internetConnect = true;
			save.saveBooleanValue("internetConnect", true);
			return true;
		} else {
			MtkLog.i(TAG, "fail to open the ethernet connection**********");
			internetConnect = false;
			save.saveBooleanValue("internetConnect", false);
			return false;
		}
	}

	// add by jun gu 2011 8 1
	public void openInternetConnectSafety() {
		if (EthernetImplement.dummyMode) {
			return;
		}
		((MenuMain) context).setNetBack(true);
		MtkLog.v("NetWork", "current setNet is " + internetConnect);
		if (!internetConnect) {
			MtkLog.i(TAG, "start to open ethernet!!!");
			new OpenEthernetThread(true).start();
		} else {
			MtkLog.i(TAG, "start to close ethernet!!!");
			new OpenEthernetThread(false).start();
		}
	}

	// add by jun gu 2011 8 1
	public void openInternetConnectSafety(int value) {
		if (EthernetImplement.dummyMode) {
			return;
		}
		Log
				.v(TAG, "current setNetback is "
						+ ((MenuMain) context).getNetBack());
		start = System.currentTimeMillis();
		if (value == 1) {
			MtkLog.i(TAG, "start to open ethernet!!!");
			((MenuMain) context).setNetBack(true);
			new OpenEthernetThread(true).start();
			IntentFilter filter = new IntentFilter();
			filter.addAction(EthernetImplement.ETHERNET_START_INTERFACE_NOTIFY);
			ethernetOnOffReceiver.setState(1);
			if (!etherRegistered) {
				MtkLog.v(TAG, "register ethernet on");
				context.registerReceiver(ethernetOnOffReceiver, filter);
				etherRegistered = true;
			}
			// new WatchEthernetThread(true).start();
		} else if (value == 0) {
			MtkLog.i(TAG, "start to close ethernet!!!");
			((MenuMain) context).setNetBack(true);
			new OpenEthernetThread(false).start();
			IntentFilter filter = new IntentFilter();
			filter.addAction(EthernetImplement.ETHERNET_STOP_INTERFACE_NOTIFY);
			ethernetOnOffReceiver.setState(0);
			if (!etherRegistered) {
				MtkLog.v(TAG, "register ethernet off");
				context.registerReceiver(ethernetOnOffReceiver, filter);
				etherRegistered = true;
			}
			// new WatchEthernetThread(false).start();
		} else if (value == 2) {
			MtkLog.i(TAG, "start to close wifi!!!");
			((MenuMain) context).setWifiBack(true);
			new OpenWifiThread(false).start();
			IntentFilter filter = new IntentFilter();
			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			wifiOnOffReceiver.setState(0);
			if (!wifiRegistered) {
				MtkLog.v(TAG, "register wifi off");
				context.registerReceiver(wifiOnOffReceiver, filter);
				wifiRegistered = true;
			}
			// new WatchWifiThread(false).start();

		} else if (value == 3) {
			MtkLog.i(TAG, "start to open wifi!!!");
			((MenuMain) context).setWifiBack(true);
			new OpenWifiThread(true).start();
			IntentFilter filter = new IntentFilter();
			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			wifiOnOffReceiver.setState(1);
			if (!wifiRegistered) {
				MtkLog.v(TAG, "register wifi on");
				context.registerReceiver(wifiOnOffReceiver, filter);
				wifiRegistered = true;
			}
			// new WatchWifiThread(true).start();
		} else {
			MtkLog.i(TAG, "value error!!!");
		}
	}

	public void startDhcpRegister() {
		if (EthernetImplement.dummyMode) {
			return;
		}
		ethernet.onStartRegister();
	}
	

	public void setConnectDhcp() {
		if (EthernetImplement.dummyMode) {
			return;
		}
		ethernet.setConnectDhcp();
	}

	public void removeStickyBroadcast() {
		if (EthernetImplement.dummyMode) {
			return;
		}
		ethernet.onRemoveStickyBroadcast();
	}

	public void manualIPSetting(String ip, String mask, String route,
			String dns, String dns2) {
		if (EthernetImplement.dummyMode) {
			return;
		}
		ethernet.manualSetting(ip, mask, route, dns, dns2);
	}

	/**
	 * 
	 * @author MTK40602 create new thread to open/close Ethernet
	 */
	private class OpenEthernetThread extends Thread {
		
		private boolean isOpen;

		public OpenEthernetThread(boolean isOpen) {
			this.isOpen = isOpen;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (this) {
				synchronized (context) {
					if (isOpen) {
						netWork.openInternetConnect();
					} else {
						netWork.closeInternetConnect();
					}
				}
			}
			end = System.currentTimeMillis();
			MtkLog.v(TAG, "cost time " + (end - start) / 1000);
		}
	}

	private class WatchEthernetThread extends Thread {

		private boolean isOpen;
		private int stopState;
		private boolean isStop = false;
		private boolean isCancel = true;

		public WatchEthernetThread(boolean isOpen) {
			this.isOpen = isOpen;
			if (isOpen) {
				stopState = EthernetImplement.E_STATUS_ENABLED;
			} else {
				stopState = EthernetImplement.E_STATUS_DISABLED;
			}
		}

		@Override
		public void run() {

			MtkLog.v(TAG, "***********ether watcher running************");
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (isCancel) {
						isStop = true;
					}
				}
			}, 3000);

			while (stopState != ethernet.getState() && !isStop) {
			}
			if (stopState == ethernet.getState()) {
				MtkLog.v(TAG, "********eth state reach*************");
			} else {
				MtkLog.v(TAG, "********eth state not reach*************");
			}
			((MenuMain) context).setNetBack(false);
			isCancel = true;
		}
	}

	/**
	 * 
	 * @author MTK40602 create new thread to open/close Wifi
	 */
	private class OpenWifiThread extends Thread {
		
		private boolean isOpen;

		public OpenWifiThread(boolean isOpen) {
			this.isOpen = isOpen;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (this) {
				synchronized (context) {
					if (isOpen) {
						netWork.openWifi();
					} else {
						netWork.closeWifi();
//						if (!isPreventCloseEthernet) {
//							context.registerReceiver(closeEthernetReceiver,
//									closeEthernetFilter);
//						}
					}
				}
			}
			end = System.currentTimeMillis();
			MtkLog.v(TAG, "cost time " + (end - start) / 1000);
		}
	}

	private class WatchWifiThread extends Thread {

		private boolean isOpen;
		private int stopState;
		private boolean isStop = false;
		private boolean isCancel = true;

		public WatchWifiThread(boolean isOpen) {
			this.isOpen = isOpen;
			if (isOpen) {
				stopState = WifiConst.W_STATE_ENABLED;
			} else {
				stopState = WifiConst.W_STATE_DISABLED;
			}
		}

		@Override
		public void run() {
			MtkLog.v(TAG, "***********wifi watcher running************");
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (isCancel) {
						isStop = true;
					}
				}
			}, 3000);

			while (stopState != netWork.getWifiState() && !isStop) {
			}
			if (stopState == netWork.getWifiState()) {
				MtkLog.v(TAG, "********wifi state reach*************");
			} else {
				MtkLog.v(TAG, "********wifi state not reach*************");
			}
			((MenuMain) context).setWifiBack(false);
		}
	}

//	public void initEthernet() {
//		String curSMode = netWork.getConnectMode();
//		int curIMode = 0;
//		if (curSMode.equals("dhcp")) {
//			curIMode = 0;
//		} else if (curSMode.equals("manual")) {
//			curIMode = 1;
//		}
//
//		int oldMode = netWork.loadConnectMode();
//		if (curIMode != oldMode) {
//			netWork.saveConnectMode(curIMode);
//			Log.v(TAG, "*****************nowMode**********************"
//					+ curIMode);
//			if (curIMode == 1) {
//				String[] str = { "ip", "subnet", "gateWay", "dns", "dns2" };
//				for (int i = 0; i < 5; i++) {
//					String address = "";
//					String str1, str2, str3, str4;
//					switch (i) {
//					case 0:
//						address = netWork.getIPAddress();
//						break;
//					case 1:
//						address = netWork.getMaskAddress();
//						break;
//					case 2:
//						address = netWork.getRouteAddress();
//						break;
//					case 3:
//						address = netWork.getDnsAddress();
//						break;
//					case 4:
//						address = netWork.getDns2Address();
//						break;
//					}
//					Log.v(TAG, "*****************address**********************"
//							+ address);
//					str1 = address.substring(0, address.indexOf("."));
//					address = address.substring(address.indexOf(".") + 1);
//					str2 = address.substring(0, address.indexOf("."));
//					address = address.substring(address.indexOf(".") + 1);
//					str3 = address.substring(0, address.indexOf("."));
//					address = address.substring(address.indexOf(".") + 1);
//					str4 = address;
//
//					str1 = "".equals(str1) ? str1 = 0 + "" : str1;
//					str2 = "".equals(str2) ? str2 = 0 + "" : str2;
//					str3 = "".equals(str3) ? str3 = 0 + "" : str3;
//					str4 = "".equals(str4) ? str4 = 0 + "" : str4;
//					save.saveStrValue(str[i] + "1", str1);
//					save.saveStrValue(str[i] + "2", str2);
//					save.saveStrValue(str[i] + "3", str3);
//					save.saveStrValue(str[i] + "4", str4);
//				}
//			}
//		}
//	}

	/**
	 * 
	 */
	public void closeInternetConnect() {
		if (EthernetImplement.dummyMode) {
			return;
		}
		MtkLog.i(TAG, "shut net connection");
		internetConnect = false;
		save.saveBooleanValue("internetConnect", false);
		ethernet.closeEthernet();
	}

	public int getEthernetStatus() {
		return ethernet.getState();
	}
	

	public String getMACAddress() {
		if (EthernetImplement.dummyMode) {
			return "00:00:00:00:00:00";
		}
		return ethernet.getMacAddr();
	}

	public void resetDefault() {
		int connect = save.readValue(MenuConfigManager.NETWORK_CONNECTION);
		int method = save.readValue(MenuConfigManager.NETWORK_INTERFACE);

		if (method == 1) {
			if (connect == 1) {
				openInternetConnectSafety(2);
			}
			openInternetConnectSafety(0);
		} else {
			if (connect == 1) {
				openInternetConnectSafety(0);
			}
		}

		save.saveValue(MenuConfigManager.NETWORK_CONNECTION, 0);
		save.saveValue(MenuConfigManager.NETWORK_INTERFACE, 0);
	}	

	public Boolean checkIP(String ip, String netMask, String routeAddr,
			String dnsAddr, String dnsAddr2) {
		IPData ipdata = new IPData(arrayToStr(ip));
		if (ipdata.checkSubnetMask(arrayToStr(netMask))
				&& ipdata
						.checkRoute(arrayToStr(netMask), arrayToStr(routeAddr))) {
			return true;
		} else {
			return false;
		}
	}

	private StringBuffer arrayToStr(String str) {
		StringBuffer sb = new StringBuffer();
		sb.append(getIP(str).toString());
		return sb;
	}

	public StringBuilder getIP(String source) {
		int i, j;
		if (source == null || source.length() == 0) {
			source = "0.0.0.0";
		}
		StringBuilder sb_total = new StringBuilder();
		String[] sources = source.split("\\.");
		StringBuilder sb_tmp;
		for (i = 0; i < sources.length; i++) {
			sb_tmp = new StringBuilder();
			for (j = 0; j < 3 - sources[i].length(); j++) {
				sb_tmp.append('0');
			}
			for (j = 0; j < sources[i].length(); j++) {
				sb_tmp.append(sources[i].charAt(j));
			}
			sb_tmp.append('.');
			sb_total.append(sb_tmp);
		}
		sb_total.deleteCharAt(sb_total.length() - 1);
		return sb_total;
	}

	// public void setPreventEthernet(boolean isPrevent) {
	// this.isPreventCloseEthernet = isPrevent;
	// }

}
