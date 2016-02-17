package com.mediatek.ui.menu.util;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.RelativeLayout;

import com.mediatek.ui.R;
import com.mediatek.ui.menu.commonview.WifiCommonTextView;
import com.mediatek.ui.util.MtkLog;

public class WifiDongleWatcher {

	private DongleReceiver dongleReceiver;
	private Context mContext;
	String TAG = "WifiDongleListener";
	private List<WifiDongleChangedListener> wifiDongleChangedListenerList;
	private NetWork netWork;
	RelativeLayout parent;

	private WifiDongleChangedListener wifiDongleChangedListener = new WifiDongleChangedListener() {

		public void onPlugOut() {
			// TODO Auto-generated method stub
			WifiCommonTextView wcTextView = new WifiCommonTextView(mContext,
					parent, MenuConfigManager.WIFI_COMMON_NODONGLE, null);
			wcTextView.setText(R.string.menu_wifi_nodongle);
			parent.removeAllViews();
			parent.addView(wcTextView);
			netWork.closeWifi();
		}

		public void onPlugIn() {
			// TODO Auto-generated method stub
			SaveValue saveV = SaveValue.getInstance(mContext);
			if (saveV.readValue(MenuConfigManager.NETWORK_CONNECTION) == 0
					&& saveV.readValue(MenuConfigManager.NETWORK_INTERFACE) == 1) {
				netWork.openWifi();
			}
		}

	};

	public WifiDongleWatcher(Context mContext, RelativeLayout parent) {
		this.mContext = mContext;
		this.parent = parent;
		dongleReceiver = new DongleReceiver();
		netWork = NetWork.getInstance(mContext);
		wifiDongleChangedListenerList = new ArrayList<WifiDongleChangedListener>();
		wifiDongleChangedListenerList.add(wifiDongleChangedListener);
	}

	public void setReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mContext.registerReceiver(dongleReceiver, filter);
	}

	public void removeReceiver() {
		mContext.unregisterReceiver(dongleReceiver);
	}

	public void addDongleChangeListener(
			WifiDongleChangedListener wifiDongleChangedListener) {
		this.wifiDongleChangedListenerList.add(wifiDongleChangedListener);
	}

	/**
	 * receive message from net connect
	 * 
	 * @author mtk40513
	 * 
	 */
	private class DongleReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			Log
					.d(TAG,
							"***********************Receive**********************************");
			String action = intent.getAction();
			MtkLog.d(TAG, "***********************" + action
					+ "**********************************");
			if (action != null && action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
				MtkLog.d(TAG, "W_SUPPLICANT_CONNECTED success *****1.1******");
				DetailedState state = WifiInfo
						.getDetailedStateOf((SupplicantState) intent
								.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
				MtkLog.v(TAG, "state = " + state);
				if (state == DetailedState.DISCONNECTED
						&& !netWork.isWifiDongleExist()) {
					for (int i = 0; i < wifiDongleChangedListenerList.size(); i++) {
						wifiDongleChangedListenerList.get(i).onPlugOut();
					}
				}
				// SupplicantState
				// state=intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
			} else if (action
					.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
				MtkLog.d(TAG, "W_SUPPLICANT_CONNECTED failed *****1.2******");

			} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				DetailedState state = ((NetworkInfo) intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO))
						.getDetailedState();
				MtkLog.d(TAG, "W_SUPPLICANT_CONNECTED success *****1.3******");
				MtkLog.v(TAG, "state = " + state);

				if (state == DetailedState.DISCONNECTED
						&& !netWork.isWifiDongleExist()) {
					MtkLog.d(TAG, "W_NETWORK_CONNECTED failed *****1.32******");
					for (int i = 0; i < wifiDongleChangedListenerList.size(); i++)
						wifiDongleChangedListenerList.get(i).onPlugOut();
				}
			}
			// else if(action.equals())
		}
	}
}
