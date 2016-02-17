package com.mediatek.ui.menu.commonview;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mediatek.netcm.ethernet.EthernetImplement;
import com.mediatek.netcm.wifi.WifiAccessPoint;
import com.mediatek.netcm.wifi.WifiConst;
import com.mediatek.ui.R;
import com.mediatek.ui.menu.MenuMain;
import com.mediatek.ui.menu.util.MenuConfigManager;
import com.mediatek.ui.menu.util.NetWork;
import com.mediatek.ui.setup.Loading;

public class WifiConnectView extends WifiContentView {

	String ssid;
	String pass;
	Loading loading;
	TextView connectTitle;
	int auth;
	NetWork netWork;
	String TAG = "WifiConnectView";
	WifiAccessPoint mWifiAccessPoint = null;
	List<WifiAccessPoint> mListAccessPoint;
	NetworkReceiver mConnectReceiver = null;
	ScanReadyReceiver mScanReceiver = null;
	WPSReceiver mWpsReceiver = null;
	boolean hasAPPass = false;
	boolean isTimeCancel = false;
	boolean isWps = false;

	boolean isShut = false;
	boolean isFail = false;
	boolean isPrev = false;
	final int SCAN_SUCCESS = 0;
	final int SCAN_FAIL = 1;
	final int CONNECT_FAIL = 2;
	final int CONNECT_FAIL_NOMATCH = 3;
	final int CONNECT_FAIL_PASSERR = 4;
	final int CONNECT_FAIL_TIMEOUT = 5;
	final int CONNECT_SUCCESS = 6;

	final int CONNECT_MODE = 0;
	final int SCAN_MODE = 1;
	final int WPS_MODE = 2;
	final int WPS_TIMEOUT_TIME = 150000;
	final int SCAN_TIMEOUT_TIME=20000;
	final int TIMEOUT_TIME=60000;

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			loading.stopDraw();
			if (isPrev) {
				return;
			}
			WifiContentView view;
			int state = getState();
			switch (msg.what) {
			case SCAN_SUCCESS:
				if (mListAccessPoint == null || mListAccessPoint.size() == 0) {
					int status = MenuConfigManager.WIFI_COMMON_NO_AP;
					if (state == MenuConfigManager.WIFI_CONNECT_WPS_SCANING) {
						status = MenuConfigManager.WIFI_COMMON_NO_WPS_AP;
					}
					WifiCommonTextView wcTextView = new WifiCommonTextView(
							mContext, parent, status, -1, "");
					wcTextView.setText(R.string.menu_wifi_no_ap);
					isPrev = false;
					isFail = false;
					isTimeCancel = true;
					parent.removeAllViews();
					parent.addView(wcTextView);
					return;
				} else {
					switch (state) {
					case MenuConfigManager.WIFI_CONNECT_SCANING:
						view = new WifiScanList(mContext, parent,
								mListAccessPoint,
								MenuConfigManager.WIFI_SCAN_NORMAL);
						break;
					case MenuConfigManager.WIFI_CONNECT_WPS_SCANING:
						view = new WifiScanList(mContext, parent,
								mListAccessPoint,
								MenuConfigManager.WIFI_SCAN_WPS);
						break;
					default:
						view = new WifiScanList(mContext, parent,
								mListAccessPoint,
								MenuConfigManager.WIFI_SCAN_NORMAL);
						break;
					}
				}
				break;
			case SCAN_FAIL:
				view = new WifiMainMenu(mContext, parent);
				break;
			case CONNECT_FAIL:
				WifiCommonTextView temp5;
				switch (state) {				
				case MenuConfigManager.WIFI_CONNECT_SCAN:
					temp5 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_SCAN_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_MANUAL:
					temp5 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_MANUAL_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PIN_AUTO:
					temp5 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PIN_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PIN_AP:
					temp5 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PIN_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PBC:
					temp5 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PBC_FAIL, -1, "");
					break;
				default:
					temp5 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_SCAN_FAIL, -1, "");
					break;
				}
				temp5.setText(R.string.menu_wifi_connect_fail);
				view = temp5;
				break;
			case CONNECT_FAIL_NOMATCH:
				WifiCommonTextView temp4;
				switch (state) {
				case MenuConfigManager.WIFI_CONNECT_SCAN:
					temp4 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_SCAN_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_MANUAL:
					temp4 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_MANUAL_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PIN_AUTO:
					temp4 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PIN_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PIN_AP:
					temp4 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PIN_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PBC:
					temp4 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PBC_FAIL, -1, "");
					break;
				default:
					temp4 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_SCAN_FAIL, -1, "");
					break;
				}
				temp4.setText(R.string.menu_wifi_no_match);
				view = temp4;
				break;
			case CONNECT_FAIL_PASSERR:
				WifiCommonTextView temp3;
				switch (state) {
				case MenuConfigManager.WIFI_CONNECT_SCAN:
					temp3 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_SCAN_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_MANUAL:
					temp3 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_MANUAL_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PIN_AUTO:
					temp3 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PIN_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PIN_AP:
					temp3 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PIN_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PBC:
					temp3 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PBC_FAIL, -1, "");
					break;
				default:
					temp3 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_SCAN_FAIL, -1, "");
					break;
				}
				String s1 = mContext.getResources().getString(
						R.string.menu_wifi_pass_err);
				String s2 = getPass();
				temp3.setText(s1 + s2);
				view = temp3;
				break;
			case CONNECT_FAIL_TIMEOUT:
				WifiCommonTextView temp2;
				switch (state) {
				case MenuConfigManager.WIFI_CONNECT_SCAN:
					temp2 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_SCAN_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_MANUAL:
					temp2 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_MANUAL_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PIN_AUTO:
					temp2 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PIN_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PIN_AP:
					temp2 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PIN_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PBC:
					temp2 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PBC_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_SCANING:
					temp2 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_SCAN_TIMEOUT, -1, "");
					break;
				default:
					temp2 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_SCAN_FAIL, -1, "");
					break;
				}
				temp2.setText(R.string.menu_wifi_connect_fail);
				view = temp2;
				break;
			case CONNECT_SUCCESS:
				WifiCommonTextView temp1;
				switch (state) {
				case MenuConfigManager.WIFI_CONNECT_SCAN:
					temp1 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_SCAN_SUCCESS, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_MANUAL:
					temp1 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_MANUAL_SUCCESS, -1,
							"");
					break;
				case MenuConfigManager.WIFI_CONNECT_PIN_AUTO:
					temp1 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PIN_SUCCESS, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PIN_AP:
					temp1 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PIN_FAIL, -1, "");
					break;
				case MenuConfigManager.WIFI_CONNECT_PBC:
					temp1 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_PBC_SUCCESS, -1, "");
					break;
				default:
					temp1 = new WifiCommonTextView(mContext, parent,
							MenuConfigManager.WIFI_COMMON_SCAN_SUCCESS, -1, "");
					break;
				}
				temp1.setText(R.string.menu_wifi_connect_success);
				view = temp1;
				break;
			default:
				view = new WifiMainMenu(mContext, parent);
				break;
			}
			isPrev=false;
			isFail=false;
			isTimeCancel = true;
			parent.removeAllViews();
			parent.addView(view);
		}
	};

	class TimeoutTimerTask extends TimerTask {

		private int mode;

		public TimeoutTimerTask(int mode) {
			this.mode = mode;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			WifiCommonTextView wcTextView;
			if (mode == SCAN_MODE) {
				unregisterScanReceiver();
			} else if (mode == CONNECT_MODE) {
				unregisterNetworkChangedReceiver();
			} else if (mode == WPS_MODE) {
				unregisterWPSChangedReceiver();
			}
			if (isFail) {
				mHandler.sendEmptyMessage(CONNECT_FAIL);
			} else if (!isTimeCancel) {
				mHandler.sendEmptyMessage(CONNECT_FAIL_TIMEOUT);
			}
		}
	}

	public void registerNetChangedRecveiver() {
		final Intent intent1 = new Intent(
				WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		final Intent intent3=new Intent(
				WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mContext.removeStickyBroadcast(intent1);
		mContext.removeStickyBroadcast(intent3);

		final Intent intent2 = new Intent(
				WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		mContext.removeStickyBroadcast(intent2);

		mConnectReceiver = new NetworkReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mContext.registerReceiver(mConnectReceiver, filter);
		Log.v(TAG, "***************registered*****************");

	}

	public void unregisterNetworkChangedReceiver() {
		if (mConnectReceiver != null) {
			mContext.unregisterReceiver(mConnectReceiver);
			mConnectReceiver = null;
		}
		return;
	}

	public void registerWPSChangedReceiver() {
		/*final Intent intent1=new Intent(
				WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mContext.removeStickyBroadcast(intent1);
		
		final Intent intent2=new Intent(
				WifiManager.WPS_CONNECTION_CHANGED_ACTION);
		mContext.removeStickyBroadcast(intent2);
		
		mWpsReceiver = new WPSReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.WPS_CONNECTION_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mContext.registerReceiver(mWpsReceiver, filter);
		Log
				.v(TAG,
						"@@@@@@@@@@@@@@@@@register wps receiver@@@@@@@@@@@@@@@@@@@@@@@@@@@");*/
	}

	public void unregisterWPSChangedReceiver() {
		if (mWpsReceiver != null) {

			mContext.unregisterReceiver(mWpsReceiver);
			Log
					.v(TAG,
							"@@@@@@@@@@@@@@@@@unregister wps receiver@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			mWpsReceiver = null;
		}
		return;
	}

	public void registerScanReadyRecveiver() {
		mScanReceiver = new ScanReadyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiConst.SCAN_READY);
		mContext.registerReceiver(mScanReceiver, filter);
	}

	public void unregisterScanReceiver() {
		if (mScanReceiver != null) {
			mContext.unregisterReceiver(mScanReceiver);
			mScanReceiver = null;
		}
		return;
	}

	/**
	 * receive message from net connect
	 * 
	 * @author mtk40513
	 * 
	 */
	private class NetworkReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "******************Receive**************************");
			String action = intent.getAction();
			Log.d(TAG, "***********************" + action
					+ "**********************************");
			if (action != null) {
			if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
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
					Log.d(TAG, "W_NETWORK_CONNECTED success ***1.31*****");
					unregisterNetworkChangedReceiver();
					isTimeCancel = true;
					mHandler.sendEmptyMessage(CONNECT_SUCCESS);
					netWork.saveWifiConfigure();
					isFail=false;
				} else {
					Log.d(TAG, "W_NETWORK_CONNECTED fail *****1.32******");
					isFail = true;
				}
			}
		}
	}
	}

	private class WPSReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			/*Log.d(TAG, "******************Receive WPS**********************");
			String action = intent.getAction();
			Log.d(TAG, "***********************" + action
					+ "**********************************");
			if (action.equals(WifiManager.WPS_CONNECTION_CHANGED_ACTION)) {
				Log.d(TAG, "wps connection changed *****1.1******");
				int event = intent.getIntExtra(WifiManager.WPS_CONNECT_STATE,
						WifiManager.WPS_STATE_UNKNOWN);
				switch (event) {
				case WifiManager.WPS_STATE_SUCCEEDED:					
					break;
				case WifiManager.WPS_STATE_FAILED:
					Log.d(TAG, "wps connection changed *****1.12******");
					isTimeCancel = true;
					isFail = true;					
					unregisterWPSChangedReceiver();
					mHandler.sendEmptyMessage(CONNECT_FAIL);
					break;
				case WifiManager.WPS_STATE_TIMEOUT:
					Log.d(TAG, "wps connection changed *****1.13******");
					isTimeCancel = true;					
					unregisterWPSChangedReceiver();
					mHandler.sendEmptyMessage(CONNECT_FAIL_TIMEOUT);
					break;
				}
			} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				DetailedState state = ((NetworkInfo) intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO))
						.getDetailedState();
				Log.d(TAG, "W_SUPPLICANT_CONNECTED success *****2.1******");
				Log.v(TAG, "state = " + state);

				if (state == DetailedState.CONNECTED) {
					Log.d(TAG, "W_NETWORK_CONNECTED success ***2.11***");
					unregisterWPSChangedReceiver();
					isTimeCancel = true;
					netWork.saveWifiConfigure();
					mHandler.sendEmptyMessage(CONNECT_SUCCESS);
				} else {
					Log.d(TAG, "W_NETWORK_CONNECTED fail *****2.12******");
					isFail = true;
				}
			}*/
		}
	}

	/**
	 * get all wifi access point from broadcast.
	 * 
	 * @author mtk40513
	 * 
	 */
	private class ScanReadyReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "ScanReadyReceiver: ");
			if ( intent.getAction() != null
					&& (WifiConst.SCAN_READY).equals(intent.getAction())) {
				int state = getState();
				if (state == MenuConfigManager.WIFI_CONNECT_SCANING) {
					mListAccessPoint = netWork.getScanAccessPoints();
				} else if (state == MenuConfigManager.WIFI_CONNECT_WPS_SCANING) {
					mListAccessPoint = netWork.getWpsScanAccessPoints();
				}
				((MenuMain) mContext).setWifiScanList(mListAccessPoint);
				unregisterScanReceiver();
				netWork.unregisterScanReceiver();
				isTimeCancel = true;
				mHandler.sendEmptyMessage(SCAN_SUCCESS);
			}
		}
	}

	private String getPass() {
		switch (auth) {
		case MenuConfigManager.W_CONFIRM_WEP:
			return "WEP";
		case MenuConfigManager.W_CONFIRM_WPA_PSK_TKIP:
		case MenuConfigManager.W_CONFIRM_WPA_PSK_AES:
		case MenuConfigManager.W_CONFIRM_WPA2_PSK_TKIP:
		case MenuConfigManager.W_CONFIRM_WPA2_PSK_AES:
		case MenuConfigManager.W_CONFIRM_UNKNOWN:
			return "WPA";
		}
		return "WPA";
	}

	public WifiConnectView(Context context, RelativeLayout parent, int state,
			int auth, String ssid, String pass) {
		super(context, parent);
		// TODO Auto-generated constructor stub

		this.addView(inflate(mContext, R.layout.wifi_load_view, null), params);
		((MenuMain) mContext).setCurWifiView(this);
		setState(state);
		connectTitle = (TextView) this.findViewById(R.id.wifi_connect_text);
		loading = (Loading) this.findViewById(R.id.wifi_connect_loading);
		initView();
		this.ssid = ssid;
		this.pass = pass;
		this.auth = auth;		
		updateBottomButtons();
	}

	public WifiConnectView(Context context, RelativeLayout parent, int state,
			WifiAccessPoint wifiAccessPoint, String pass, boolean hasAPPass) {
		super(context, parent);
		// TODO Auto-generated constructor stub

		this.addView(inflate(mContext, R.layout.wifi_load_view, null), params);
		((MenuMain) mContext).setCurWifiView(this);
		setState(state);
		connectTitle = (TextView) this.findViewById(R.id.wifi_connect_text);
		loading = (Loading) this.findViewById(R.id.wifi_connect_loading);
		initView();
		this.mWifiAccessPoint = wifiAccessPoint;
		this.pass = pass;
		this.hasAPPass = hasAPPass;
		updateBottomButtons();
	}

	private void initView() {
		int state = getState();
		switch (state) {
		case MenuConfigManager.WIFI_CONNECT_SCANING:
			setTitle(R.string.menu_wifi_scaning);
			break;
		case MenuConfigManager.WIFI_CONNECT_SCAN:
		case MenuConfigManager.WIFI_CONNECT_MANUAL:
		case MenuConfigManager.WIFI_CONNECT_PIN_AUTO:
		case MenuConfigManager.WIFI_CONNECT_PIN_AP:
		case MenuConfigManager.WIFI_CONNECT_PBC:
			setTitle(R.string.menu_wifi_connecting);
			break;
		}
	}

	public void startBackground() {
		int state = getState();
		loading.drawLoading();
		switch (state) {
		case MenuConfigManager.WIFI_CONNECT_SCANING:
		case MenuConfigManager.WIFI_CONNECT_WPS_SCANING:
			scanThread.start();
			break;
		case MenuConfigManager.WIFI_CONNECT_SCAN:
		case MenuConfigManager.WIFI_CONNECT_MANUAL:
		case MenuConfigManager.WIFI_CONNECT_PIN_AUTO:
		case MenuConfigManager.WIFI_CONNECT_PIN_AP:
		case MenuConfigManager.WIFI_CONNECT_PBC:
			connectThread.start();
			break;
		}
	}

	private Thread scanThread = new Thread(new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			int a = 0;
			for (int i = 0; i < 100000000; i++) {
				a++;
			}
			Timer timer = new Timer();
			TimeoutTimerTask timerTask = new TimeoutTimerTask(SCAN_MODE);
			timer.schedule(timerTask, SCAN_TIMEOUT_TIME);
			getAPList();
		}
	});

	private Thread connectThread = new Thread(new Runnable() {

		public void run() {
			// TODO Auto-generated method stub

			int a = 0;
			for (int i = 0; i < 100000000; i++) {
				a++;
			}
			connectWifi();
		}
	});

	private void getAPList() {
		int state = getState();
		mListAccessPoint = new ArrayList<WifiAccessPoint>();
		registerScanReadyRecveiver();
		netWork = NetWork.getInstance(mContext);
		
		if(state == MenuConfigManager.WIFI_CONNECT_WPS_SCANING) {
			netWork.scanAvailableAP(true);
		} else {
			netWork.scanAvailableAP(false);
		}
		
		// For dummy
		// for (int i = 0; i < 15; i++) {
		// WifiAccessPoint a = new WifiAccessPoint();
		// mListAccessPoint.add(a);
		// }

	}

	private boolean connectWifi() {
		netWork = NetWork.getInstance(mContext);
		int state = getState();
		switch (state) {
		case MenuConfigManager.WIFI_CONNECT_SCAN:
			Timer timer = new Timer();
			TimeoutTimerTask timerTask = new TimeoutTimerTask(CONNECT_MODE);
			timer.schedule(timerTask, TIMEOUT_TIME);
			registerNetChangedRecveiver();
			if (!hasAPPass) {
				Log.v(TAG, "*******connect none password AP***************");
//				Log.v(TAG, "******************" + mWifiAccessPoint.getSsid()
//						+ "*****************************");
				return netWork.connectWifi(mWifiAccessPoint);
			} else {
//				Log.v(TAG, "ssid*************************************"
//						+ mWifiAccessPoint.getSsid());
//				Log.v(TAG, "pass*************************************"
//						+ pass);
				return netWork.connectWifi(mWifiAccessPoint, pass);
			}
		case MenuConfigManager.WIFI_CONNECT_MANUAL:
			Timer timer1 = new Timer();
			TimeoutTimerTask timerTask1 = new TimeoutTimerTask(CONNECT_MODE);
			timer1.schedule(timerTask1, TIMEOUT_TIME);
			registerNetChangedRecveiver();
			Log.v(TAG, "now the ssid************************"+ssid);
			Log.v(TAG, "now the authmode************************"+auth);
			Log.v(TAG, "now the password************************"+pass);
			netWork.disconnectWifi();
			return netWork.connectWifi(ssid, auth, pass);
		case MenuConfigManager.WIFI_CONNECT_PBC:
			Timer timer2 = new Timer();
			TimeoutTimerTask timerTask2 = new TimeoutTimerTask(WPS_MODE);
			timer2.schedule(timerTask2, WPS_TIMEOUT_TIME);
			registerWPSChangedReceiver();
			return netWork.connectPBC();
		case MenuConfigManager.WIFI_CONNECT_PIN_AP:
			Timer timer3 = new Timer();
			TimeoutTimerTask timerTask3 = new TimeoutTimerTask(WPS_MODE);
			timer3.schedule(timerTask3, WPS_TIMEOUT_TIME);
			registerWPSChangedReceiver();
			String bSsid = mWifiAccessPoint.mResult.BSSID;
			String pinCode = ((MenuMain) mContext).getPinCode();
			return netWork.connectPIN(bSsid, pinCode);
		case MenuConfigManager.WIFI_CONNECT_PIN_AUTO:
			Timer timer4 = new Timer();
			TimeoutTimerTask timerTask4 = new TimeoutTimerTask(WPS_MODE);
			timer4.schedule(timerTask4, WPS_TIMEOUT_TIME);
			registerWPSChangedReceiver();
			String pinCode1 = ((MenuMain) mContext).getPinCode();
			return netWork.connectPIN("any", pinCode1);
		}
		return false;
	}

	public void setTitle(String text) {
		connectTitle.setText(text);
	}

	public void setTitle(int text) {
		connectTitle.setText(text);
	}

	public String getTitle() {
		return connectTitle.getText().toString();
	}

	@Override
	protected void findBackView() {
		// TODO Auto-generated method stub
		netWork = NetWork.getInstance(mContext);
		isPrev = true;
		isShut = true;
		int state = getState();
		switch (state) {
		case MenuConfigManager.WIFI_CONNECT_SCAN:
		case MenuConfigManager.WIFI_CONNECT_MANUAL:
			netWork.disconnectWifi();
			new Timer().schedule(new TimerTask(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.v(TAG, "****************redisconnect******************");
					netWork.disconnectWifi();
				}				
			}, 2500);
			break;
		case MenuConfigManager.WIFI_CONNECT_PIN_AUTO:
		case MenuConfigManager.WIFI_CONNECT_PIN_AP:
		case MenuConfigManager.WIFI_CONNECT_PBC:
			netWork.disconnectWpsWifi();			
			break;
		}
	}

	@Override
	protected void findEnterView() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void findNextView() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void findPrevView() {
		// TODO Auto-generated method stub
		netWork = NetWork.getInstance(mContext);
		int state = getState();
		switch (state) {
		case MenuConfigManager.WIFI_CONNECT_SCAN:
			netWork.disconnectWifi();
			reDisconnect();
			WifiScanList viewScan = new WifiScanList(mContext, parent,
					MenuConfigManager.WIFI_SCAN_NORMAL);
			setPrevView(viewScan);
			isPrev = true;
			break;
		case MenuConfigManager.WIFI_CONNECT_SCANING:
		case MenuConfigManager.WIFI_CONNECT_MANUAL:
			netWork.disconnectWifi();
			reDisconnect();
			WifiMainMenu viewMain = new WifiMainMenu(mContext, parent);
			setPrevView(viewMain);
			isPrev = true;
			break;
		case MenuConfigManager.WIFI_CONNECT_PIN_AUTO:
		case MenuConfigManager.WIFI_CONNECT_PIN_AP:
		case MenuConfigManager.WIFI_CONNECT_PBC:
			netWork.disconnectWpsWifi();
			WifiMainMenu viewMain1 = new WifiMainMenu(mContext, parent);
			setPrevView(viewMain1);
			isPrev = true;
			break;
		}
	}
	
	private void reDisconnect(){
		new Timer().schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.v(TAG, "****************redisconnect******************");
				netWork.disconnectWifi();
			}				
		}, 2500);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			WifiContentView view;
			int state = this.getState();
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				findBackView();
				if (isShut) {
					((MenuMain) mContext).recoverMenu();
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				findPrevView();
				if ((view = getPrevView()) != null) {
					parent.removeAllViews();
					parent.addView(view);
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				findNextView();
				if ((view = getNextView()) != null) {
					parent.removeAllViews();
					parent.addView(view);
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				break;
			default:
				return false;
			}
		}
		return true;
	}

	@Override
	protected void updateBottomButtons() {
		// TODO Auto-generated method stub
		boolean[] data = new boolean[] { true, false, false, false, true };
		((MenuMain) mContext).setWifiButtonText(4, R.string.menu_wifi_cancel);
		((MenuMain) mContext).displayWifiButtons(data);
	}
	
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if(visibility != View.VISIBLE) {
			isTimeCancel = true;
		}
	}
	
}
