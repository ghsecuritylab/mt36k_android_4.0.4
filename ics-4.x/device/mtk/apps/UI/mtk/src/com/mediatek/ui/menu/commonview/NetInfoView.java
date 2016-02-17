package com.mediatek.ui.menu.commonview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.ethernet.EthernetManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mediatek.netcm.ethernet.EthernetImplement;
import com.mediatek.ui.R;
import com.mediatek.ui.menu.util.MenuConfigManager;
import com.mediatek.ui.menu.util.NetWork;
import com.mediatek.ui.menu.util.SaveValue;

/**
 * A view to show Ethernet or wireless network information, include interface
 * type, address type, IP address and so on
 * 
 * @author MTK40462
 * 
 */
public class NetInfoView extends LinearLayout {

	private TextView interfaceName, ipTypeName, ipAddressName, subNetMask,
			defaultGate, firstDns, standbyDns, ethernetMac;
	private Context context;
	private LayoutInflater mInflater;
	NetWork netWork;
	String TAG = "NetInfoView";
	DhcpReceiver mReceiver;
	WifiReceiver mWifiReceiver;
	private boolean isRegisterEth = false;		

	private String interfaceType, addrType, ipAddr, subnetMask, defGateway,
			firDNS, secDNS, ethrMac;

	public class DhcpReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action != null && action.equals(EthernetImplement.ETHERNET_NOTIFY)) {
				int event = intent.getIntExtra(EthernetImplement.DHCP_STATE,
						EthernetImplement.E_DHCP_ORIGINAL);
				switch (event) {
				case EthernetImplement.E_DHCP_SUCCESS:
					setValue();
					break;

				case EthernetImplement.E_HW_CONNECTED:
					setValue();
					break;

				case EthernetImplement.E_DHCP_FAILED:
					setNullValue();
					break;

				case EthernetImplement.E_HW_DISCONNECTED:
//					if (netWork.getConnectMode() != EthernetDevInfo.ETHERNET_CONN_MODE_DHCP) {
//						setNullValue();
//					}
					setValue();
					break;
				}
			}
			// netWork.StopDhcpRegister();
		}
	}

	/**
	 * receive message from net connect
	 * 
	 * @author mtk40513
	 * 
	 */
	private class WifiReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			Log
					.d(TAG,
							"***********************Receive**********************************");
			String action = intent.getAction();
			Log.d(TAG, "***********************" + action
					+ "**********************************");
			if (action != null && action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
				Log.d(TAG, "W_SUPPLICANT_CONNECTED success *****1.1******");
				DetailedState state = WifiInfo
						.getDetailedStateOf((SupplicantState) intent
								.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
				Log.v(TAG, "state = " + state);
				// SupplicantState
				// state=intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
			} else if (action
					.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
				Log.d(TAG, "W_SUPPLICANT_CONNECTED success *****1.2******");

			} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				DetailedState state = ((NetworkInfo) intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO))
						.getDetailedState();
				Log.d(TAG, "W_SUPPLICANT_CONNECTED success *****1.3******");
				Log.v(TAG, "state = " + state);

				if (state == DetailedState.CONNECTED) {
					Log.d(TAG, "W_NETWORK_CONNECTED success *****1.31******");
					setValue();
				} else {
					Log.d(TAG, "W_NETWORK_CONNECTED fail *****1.32******");
					setNullValue();
				}
			}
		}
	}

	public NetInfoView(Context context) {
		super(context);
		this.context = context;
	}

	public NetInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		mInflater = LayoutInflater.from(context);
		mInflater.inflate(R.layout.menu_net_info, this);
		init();
	}

	/**
	 * init
	 */
	public void init() {
		interfaceName = (TextView) findViewById(R.id.common_netinfo_interface_r);
		ipTypeName = (TextView) findViewById(R.id.common_netinfo_addtype_r);
		ipAddressName = (TextView) findViewById(R.id.common_netinfo_ipadd_r);
		subNetMask = (TextView) findViewById(R.id.common_netinfo_subcode_r);
		defaultGate = (TextView) findViewById(R.id.common_netinfo_defaultsub_r);
		firstDns = (TextView) findViewById(R.id.common_netinfo_firstdns_r);
		standbyDns = (TextView) findViewById(R.id.common_netinfo_standbydns_r);
		ethernetMac = (TextView) findViewById(R.id.common_netinfo_ethmac_r);

		netWork = NetWork.getInstance(context);
		// setValue();

		final IntentFilter filter = new IntentFilter();
		filter.addAction(EthernetImplement.ETHERNET_NOTIFY);

		mReceiver = new DhcpReceiver();
		context.registerReceiver(mReceiver, filter);
		isRegisterEth = true;

		mWifiReceiver = new WifiReceiver();
		IntentFilter filter1 = new IntentFilter();
		filter1.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter1.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		filter1.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		context.registerReceiver(mWifiReceiver, filter1);
	}

	public void unregisterNetReceiever() {

		Log.v(TAG, "unregister sub register************************");
		context.unregisterReceiver(mReceiver);
		context.unregisterReceiver(mWifiReceiver);
	}

	/**
	 * set and show network information
	 */
	public void setValue() {
		SaveValue sv = SaveValue.getInstance(context);

		if (sv.readValue(MenuConfigManager.NETWORK_INTERFACE) == 0) {
			setEthernetInfo();
		} else {
			setWirelessInfo();
		}
		showNetInfo(interfaceType, addrType, ipAddr, subnetMask, defGateway,
				firDNS, secDNS, ethrMac);
	}

	public void setNullValue() {
		SaveValue sv = SaveValue.getInstance(context);
		int mode = sv.readValue(MenuConfigManager.NETWORK_INTERFACE);
		int isConnect=sv.readValue(MenuConfigManager.NETWORK_CONNECTION);

		if (mode == 0) {
			interfaceType = context.getResources().getString(
					R.string.menu_address_ethernet);
		} else {
			interfaceType = context.getResources().getString(
					R.string.menu_address_wireless);
		}

		if (mode == 0 && isConnect==1) {
			addrType = netWork.getConnectMode();
		} else {
			addrType = "Auto";
		}
		ipAddr = "";
		subnetMask = "";
		defGateway = "";
		firDNS = "";

		// not implement yet
		secDNS = "";
		ethrMac = netWork.getMacAddress();
		showNetInfo(interfaceType, addrType, ipAddr, subnetMask, defGateway,
				firDNS, secDNS, ethrMac);
	}

	/**
	 * set Ethernet information, standby DNS is not implemented yet
	 */
	public void setEthernetInfo() {
		interfaceType = context.getResources().getString(
				R.string.menu_address_ethernet);
		addrType = netWork.getConnectMode();
		ipAddr = netWork.getIPAddress();
		subnetMask = netWork.getMaskAddress();
		defGateway = netWork.getRouteAddress();
		firDNS = netWork.getDnsAddress();		
		secDNS = netWork.getDns2Address();
		ethrMac = netWork.getMacAddress();
	}

	/**
	 * set wireless network information, it is not implemented yet, the wireless
	 * network data is dummy
	 */
	public void setWirelessInfo() {
		interfaceType = context.getResources().getString(
				R.string.menu_address_wireless);

		// will be replaced by wireless implement later
		addrType = context.getResources().getString(R.string.menu_address_auto);
		ipAddr = netWork.getWifiIPAddress();
		subnetMask = netWork.getWifiMaskAddress();
		defGateway = netWork.getWifiRouteAddress();
		firDNS = netWork.getWifiDnsAddress();
		secDNS = netWork.getWifiDns2Address();

		// will be replaced by wireless implement later
		ethrMac = netWork.getWifiMacAddress();
	}

	/**
	 * show network information
	 * 
	 * @param interfaceType
	 *            the network interface type,Ethernet or wireless
	 * @param addrType
	 *            the network address type, auto or manual
	 * @param ipAddr
	 *            the ip address
	 * @param subnetMask
	 *            the subnet mask
	 * @param defGateWay
	 *            the default Gateway
	 * @param priDNS
	 *            the primary DNS
	 * @param secondDNS
	 *            the standby DNS
	 * @param etherMac
	 *            the MAC address
	 */
	public void showNetInfo(String interfaceType, String addrType,
			String ipAddr, String subnetMask, String defGateWay, String priDNS,
			String secondDNS, String etherMac) {
		interfaceName.setText(interfaceType);
		ipTypeName.setText(addrType);
		ipAddressName.setText(filterInfo(ipAddr));
		subNetMask.setText(filterInfo(subnetMask));
		defaultGate.setText(filterInfo(defGateWay));
		firstDns.setText(filterInfo(priDNS));		
		standbyDns.setText(filterInfo(secondDNS));
		ethernetMac.setText(etherMac);
	}

	private String filterInfo(String source) {
		if (source == null || source.equalsIgnoreCase("")) {
			return context.getString(R.string.menu_setup_ip_null_warning);
		} else {
			String[] ips = source.split("\\.");
			if (ips == null || ips.length != 4) {
				return context.getString(R.string.menu_setup_ip_null_warning);
			} else {
				for (int i = 0; i < 4; i++) {
					try {
						ips[i] = Integer.parseInt(ips[i]) + "";
					} catch (Exception e) {
						return context
								.getString(R.string.menu_setup_ip_null_warning);
					}
					source = ips[0] + "." + ips[1] + "." + ips[2] + "."
							+ ips[3];
				}
			}
			return source;
		}
	}
}
