package com.mediatek.ui.menu.commonview;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mediatek.netcm.wifi.WifiAccessPoint;
import com.mediatek.netcm.wifi.WifiConst;
import com.mediatek.ui.R;
import com.mediatek.ui.menu.MenuMain;
import com.mediatek.ui.menu.util.MenuConfigManager;
import com.mediatek.ui.menu.util.NetWork;
import com.mediatek.ui.util.MtkLog;

public class WifiCommonTextView extends WifiContentView {

	private final int reDisconnectDelay=2500;
	TextView contentView;
	boolean isShut = false;
	String TAG = "WifiCommonTextView";
	List<WifiAccessPoint> mListAccessPoint;
	int place;
	int auth;
	String ssid;
	WifiAccessPoint mWifiAccessPoint = null;
	ScanReadyReceiver mScanReceiver = null;
	NetWork netWork;

	boolean isExiting = false;

	/**
	 * get all wifi access point from broadcast.
	 * 
	 * @author mtk40513
	 * 
	 */
	private class ScanReadyReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			MtkLog.d(TAG, "ScanReadyReceiver: ");
			if (intent.getAction() != null &&intent.getAction().equals(WifiConst.SCAN_READY)) {
				mListAccessPoint = netWork.getScanAccessPoints();
				((MenuMain) mContext).setWifiScanList(mListAccessPoint);
				unregisterScanReceiver();
				WifiScanList view = new WifiScanList(mContext, parent,
						mListAccessPoint, 0);
				parent.removeAllViews();
				parent.addView(view);
			}
		}
	}

	public WifiCommonTextView(Context context, RelativeLayout parent,
			int state, int auth, String ssid) {
		super(context, parent);
		// TODO Auto-generated constructor stub

		this
				.addView(inflate(mContext, R.layout.wifi_common_text, null),
						params);
		((MenuMain) mContext).setCurWifiView(this);
		setState(state);
		contentView = (TextView) this.findViewById(R.id.wifi_common_text);
		curView = contentView;
		this.auth = auth;
		this.ssid = ssid;
		netWork = NetWork.getInstance(mContext);
		updateBottomButtons();
	}

	public WifiCommonTextView(Context context, RelativeLayout parent,
			int state, WifiAccessPoint mWifiAccessPoint) {
		super(context, parent);
		// TODO Auto-generated constructor stub

		this
				.addView(inflate(mContext, R.layout.wifi_common_text, null),
						params);
		((MenuMain) mContext).setCurWifiView(this);
		setState(state);
		contentView = (TextView) this.findViewById(R.id.wifi_common_text);
		curView = contentView;
		this.mWifiAccessPoint = mWifiAccessPoint;
		netWork = NetWork.getInstance(mContext);
		updateBottomButtons();
	}

	public void setText(CharSequence text) {
		contentView.setText(text);
	}

	public void setText(int text) {
		contentView.setText(text);
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

	@Override
	protected void findNextView() {
		// TODO Auto-generated method stub
		int state = this.getState();
		switch (state) {
		case MenuConfigManager.WIFI_COMMON_BIND:
			// enter the main menu
			MtkLog.v(TAG, "****************disconnect******************");
			netWork.disconnectWifi();			
			new Timer().schedule(new TimerTask(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					MtkLog.v(TAG, "****************redisconnect******************");
					netWork.disconnectWifi();
				}				
			}, reDisconnectDelay);
			WifiMainMenu wmm = new WifiMainMenu(mContext, parent);
			setNextView(wmm);
			break;
		case MenuConfigManager.WIFI_COMMON_PBC_HINT:
			// enter PBC connection
			WifiConnectView wcView = new WifiConnectView(mContext, parent,
					MenuConfigManager.WIFI_CONNECT_PBC, -1, "", "");
			wcView.startBackground();
			setNextView(wcView);
			break;
		}
	}

	@Override
	protected void findPrevView() {
		// TODO Auto-generated method stub
		int state = this.getState();
		switch (state) {
		case MenuConfigManager.WIFI_COMMON_SCAN_FAIL:
			getAPList();
			break;
		case MenuConfigManager.WIFI_COMMON_SCAN_INVALID:
			WifiInputView view1 = new WifiInputView(mContext, parent,
					MenuConfigManager.WIFI_INPUT_SCAN_PASS, mWifiAccessPoint);
			setPrevView(view1);
			break;
		case MenuConfigManager.WIFI_COMMON_MANUAL_INVASSID:
			WifiInputView view2 = new WifiInputView(mContext, parent,
					MenuConfigManager.WIFI_INPUT_MANUAL_SSID, auth, ssid);
			setPrevView(view2);
			break;
		case MenuConfigManager.WIFI_COMMON_MANUAL_INVAPASS:
			WifiInputView view3 = new WifiInputView(mContext, parent,
					MenuConfigManager.WIFI_INPUT_MANUAL_PASS, auth, ssid);
			setPrevView(view3);
			break;
		case MenuConfigManager.WIFI_COMMON_MANUAL_FAIL:
		case MenuConfigManager.WIFI_COMMON_PBC_HINT:
		case MenuConfigManager.WIFI_COMMON_PBC_FAIL:
		case MenuConfigManager.WIFI_COMMON_PIN_FAIL:
		case MenuConfigManager.WIFI_COMMON_NO_AP:
		case MenuConfigManager.WIFI_COMMON_NO_WPS_AP:
		case MenuConfigManager.WIFI_COMMON_SCAN_TIMEOUT:
			WifiMainMenu view4 = new WifiMainMenu(mContext, parent);
			setPrevView(view4);
			break;
		}
	}

	@Override
	protected void findBackView() {
		// TODO Auto-generated method stub
		int state = this.getState();
		isShut = true;
	}

	@Override
	protected void findEnterView() {
		// TODO Auto-generated method stub
		int state = this.getState();
		switch (state) {
		case MenuConfigManager.WIFI_COMMON_BIND:
			MtkLog.v(TAG, "****************disconnect******************");
			netWork.disconnectWifi();			
			new Timer().schedule(new TimerTask(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					MtkLog.v(TAG, "****************redisconnect******************");
					netWork.disconnectWifi();
				}				
			}, reDisconnectDelay);
			WifiMainMenu wmm = new WifiMainMenu(mContext, parent);
			setEnterView(wmm);
			break;
		case MenuConfigManager.WIFI_COMMON_PBC_HINT:
			break;
		}
	}

	private void getAPList() {
		isExiting = true;
		mListAccessPoint = new ArrayList<WifiAccessPoint>();
		registerScanReadyRecveiver();
		netWork = NetWork.getInstance(mContext);
		netWork.scanAvailableAP(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (isExiting) {
			return true;
		}

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
				findEnterView();
				if ((view = getEnterView()) != null) {
					if (isShut) {
						((MenuMain) mContext).recoverMenu();
					}
					parent.removeAllViews();
					parent.addView(view);
					return true;
				}
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
		boolean[] data;
		int state = this.getState();
		switch (state) {
		case MenuConfigManager.WIFI_COMMON_NODONGLE:
		case MenuConfigManager.WIFI_COMMON_SCAN_SUCCESS:
		case MenuConfigManager.WIFI_COMMON_MANUAL_SUCCESS:
		case MenuConfigManager.WIFI_COMMON_PBC_SUCCESS:
		case MenuConfigManager.WIFI_COMMON_PIN_SUCCESS:
			data = new boolean[] { false, false, false, false, true };
			((MenuMain) mContext).setWifiButtonText(4, R.string.menu_wifi_exit);
			break;
		case MenuConfigManager.WIFI_COMMON_SCAN_FAIL:
		case MenuConfigManager.WIFI_COMMON_SCAN_INVALID:
		case MenuConfigManager.WIFI_COMMON_MANUAL_INVAPASS:
		case MenuConfigManager.WIFI_COMMON_MANUAL_INVASSID:
		case MenuConfigManager.WIFI_COMMON_MANUAL_FAIL:
		case MenuConfigManager.WIFI_COMMON_PBC_FAIL:
		case MenuConfigManager.WIFI_COMMON_PIN_FAIL:
		case MenuConfigManager.WIFI_COMMON_NO_WPS_AP:
		case MenuConfigManager.WIFI_COMMON_NO_AP:
			data = new boolean[] { true, false, false, false, true };
			((MenuMain) mContext).setWifiButtonText(4,
					R.string.menu_wifi_cancel);
			break;
		case MenuConfigManager.WIFI_COMMON_BIND:
			data = new boolean[] { false, false, false, true, true };
			((MenuMain) mContext).setWifiButtonText(4,
					R.string.menu_wifi_cancel);
			break;
		case MenuConfigManager.WIFI_COMMON_PBC_HINT:
			data = new boolean[] { true, false, false, true, true };
			((MenuMain) mContext).setWifiButtonText(4,
					R.string.menu_wifi_cancel);
			break;
		default:
			data = new boolean[] { false, false, false, false, false };
			break;
		}
		((MenuMain) mContext).displayWifiButtons(data);
	}
}
