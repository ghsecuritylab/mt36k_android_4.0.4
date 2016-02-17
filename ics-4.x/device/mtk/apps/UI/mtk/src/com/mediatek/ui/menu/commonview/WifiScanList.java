package com.mediatek.ui.menu.commonview;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.mediatek.netcm.wifi.WifiAccessPoint;
import com.mediatek.netcm.wifi.WifiConst;
import com.mediatek.netcm.wifi.WifiUtil;
import com.mediatek.ui.R;
import com.mediatek.ui.menu.MenuMain;
import com.mediatek.ui.menu.adapter.WifiAdapter;
import com.mediatek.ui.menu.util.MenuConfigManager;
import com.mediatek.ui.util.MtkLog;

public class WifiScanList extends WifiContentView {

	boolean isShut = false;
	String TAG = "WifiScanList";
	WifiScanListView wifiScanListView;
	List<WifiAccessPoint> mListAccessPoint;
	List<WifiAccessPoint> mGroup;
	WifiAdapter wifiAdapter;
	MyUpdata myUpData;
	final int PERPAGE = 8;
	private int scanMode = 0;

	public WifiScanList(Context context, RelativeLayout parent,
			List<WifiAccessPoint> mListAccessPoint, int mode) {
		super(context, parent);
		// TODO Auto-generated constructor stub
		this.scanMode = mode;
		if (scanMode == MenuConfigManager.WIFI_SCAN_NORMAL) {
			this.addView(inflate(mContext, R.layout.wifi_scan_ap_list, null),
					params);
		} else {
			this.addView(
					inflate(mContext, R.layout.wifi_scan_wps_ap_list, null),
					params);
		}
		((MenuMain) mContext).setCurWifiView(this);
		wifiScanListView = (WifiScanListView) this
				.findViewById(R.id.wifi_scan_listview);
		wifiScanListView.setOnTouchListener(onTouchListener);
		wifiScanListView.setDivider(null);
		wifiScanListView.setDividerHeight(20);
		this.mListAccessPoint = mListAccessPoint;
		myUpData = new MyUpdata();

		wifiAdapter = new WifiAdapter(mContext, scanMode, this);
		setAdapter(mListAccessPoint, 1);
		wifiScanListView.requestFocus();
		wifiScanListView.setSelection(0);
		wifiScanListView.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
		wifiScanListView.setNextFocusUpId(R.id.wifi_scan_listview);
		// wifiScanListView.setOnItemClickListener(enterItemListener);
		wifiScanListView.setOnItemClickListener(enterItemListener);
		updateBottomButtons();
	}

	public int getMode() {
		return scanMode;
	}

	private OnItemClickListener enterItemListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			MtkLog.v(TAG, "hahaha");
			KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_DPAD_CENTER);
			WifiScanList.this.onKeyDown(KeyEvent.KEYCODE_DPAD_CENTER, keyEvent);
		}
	};

	public WifiScanList(Context context, RelativeLayout parent, int mode) {
		super(context, parent);
		// TODO Auto-generated constructor stub

		this.addView(inflate(mContext, R.layout.wifi_scan_ap_list, null),
				params);
		((MenuMain) mContext).setCurWifiView(this);
		wifiScanListView = (WifiScanListView) this
				.findViewById(R.id.wifi_scan_listview);
		wifiScanListView.setDivider(null);
		wifiScanListView.setDividerHeight(20);
		this.mListAccessPoint = ((MenuMain) mContext).getWifiScanList();
		myUpData = new MyUpdata();
		this.scanMode = mode;
		wifiAdapter = new WifiAdapter(mContext, scanMode, this);
		setAdapter(mListAccessPoint, 1);
		wifiScanListView.requestFocus();
		wifiScanListView.setSelection(0);
		wifiScanListView.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
		wifiScanListView.setNextFocusUpId(R.id.wifi_scan_listview);
		wifiScanListView.setOnItemClickListener(enterItemListener);
		updateBottomButtons();
	}

	public void setFocus(int index) {
		wifiScanListView.getChildAt(index).requestFocusFromTouch();
		wifiScanListView.setSelection(index);
	}

	private void setAdapter(List<WifiAccessPoint> adpter, int pageNum) {
		wifiScanListView.initData(adpter, PERPAGE, myUpData, pageNum);
		mGroup = (List<WifiAccessPoint>) wifiScanListView.getCurrentList();
		wifiAdapter.setmGroup(mGroup);
		wifiScanListView.setAdapter(wifiAdapter);
	}

	class MyUpdata implements WifiScanListView.UpDateListView {
		@SuppressWarnings("unchecked")
		public void updata() {
			mGroup = (List<WifiAccessPoint>) wifiScanListView.getCurrentList();
			MtkLog.v(TAG, "currentnum " + mGroup.size());
			wifiAdapter.setmGroup(mGroup);
			wifiScanListView.setAdapter(wifiAdapter);
		}
	}

	@Override
	protected void findBackView() {
		// TODO Auto-generated method stub
		isShut = true;
	}

	@Override
	protected void findEnterView() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void findNextView() {
		// TODO Auto-generated method stub
		int page = wifiScanListView.getCurrentPageNum();
		int pageIndex = wifiScanListView.getSelectedItemPosition();
		int index = (page - 1) * PERPAGE + pageIndex;
		WifiAccessPoint curAP = mListAccessPoint.get(index);
		int mode = getMode();
		WifiContentView view = null;
		if (mode == MenuConfigManager.WIFI_SCAN_NORMAL) {
			int curProtocal = curAP.getProtocol();
			int curEncrypt = WifiUtil.getEncrypt(curAP.mResult);
			String ssid = curAP.getSsid();

			MtkLog.v(TAG, "ssid*********************************" + ssid);
			MtkLog.v(TAG, "protocal*****************************" + curProtocal);
			MtkLog.v(TAG, "security*****************************" + curEncrypt);
			MtkLog.v(TAG, "encrypt******************************"
					+ curAP.getEncrypt());

			if (curEncrypt == WifiConst.W_SECURITY_NONE) {
				WifiConnectView wcView = new WifiConnectView(mContext, parent,
						MenuConfigManager.WIFI_CONNECT_SCAN, curAP, "", false);
				wcView.startBackground();
				view = wcView;
			} else {
				WifiInputView wiView = new WifiInputView(mContext, parent,
						MenuConfigManager.WIFI_INPUT_SCAN_PASS, curAP);
				view = wiView;
			}
		} else {
			WifiConnectView wcwView = new WifiConnectView(mContext, parent,
					MenuConfigManager.WIFI_CONNECT_PIN_AP, curAP, "", false);
			wcwView.startBackground();
			view = wcwView;
		}

		// switch (curSecurity) {
		// case WifiConst.W_CONFIRM_WPA_PSK:
		// switch (curEncrypt) {
		// case WifiConst.W_ENCRYPT_TKIP:
		// view = new WifiInputView(mContext, parent,
		// MenuConfigManager.WIFI_INPUT_SCAN_PASS,
		// MenuConfigManager.W_CONFIRM_WPA_PSK_TKIP, ssid);
		// break;
		// case WifiConst.W_ENCRYPT_AES:
		// view = new WifiInputView(mContext, parent,
		// MenuConfigManager.WIFI_INPUT_SCAN_PASS,
		// MenuConfigManager.W_CONFIRM_WPA_PSK_AES, ssid);
		// break;
		// }
		// break;
		// case WifiConst.W_CONFIRM_WPA2_PSK:
		// switch (curEncrypt) {
		// case WifiConst.W_ENCRYPT_TKIP:
		// view = new WifiInputView(mContext, parent,
		// MenuConfigManager.WIFI_INPUT_SCAN_PASS,
		// MenuConfigManager.W_CONFIRM_WPA2_PSK_TKIP, ssid);
		// break;
		// case WifiConst.W_ENCRYPT_AES:
		// view = new WifiInputView(mContext, parent,
		// MenuConfigManager.WIFI_INPUT_SCAN_PASS,
		// MenuConfigManager.W_CONFIRM_WPA2_PSK_AES, ssid);
		// break;
		// }
		// break;
		// case WifiConst.W_CONFIRM_WPA:
		// case WifiConst.W_CONFIRM_WPA2:

		// break;
		// case WifiConst.W_CONFIRM_UNKNOWN:
		// view = new WifiInputView(mContext, parent,
		// MenuConfigManager.WIFI_INPUT_MANUAL_PASS,
		// MenuConfigManager.W_CONFIRM_UNKNOWN, ssid);
		// break;
		// }

		if (view != null) {
			setNextView(view);
		}
	}

	@Override
	protected void findPrevView() {
		// TODO Auto-generated method stub
		WifiMainMenu view = new WifiMainMenu(mContext, parent);
		setPrevView(view);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		MtkLog.v(TAG, "key down");
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			WifiContentView view;
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
				if (wifiScanListView.getSelectedItem() == null) {
					return true;
				}
				findNextView();
				MtkLog.v(TAG, "key right");
				if ((view = getNextView()) != null) {
					parent.removeAllViews();
					parent.addView(view);
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				MtkLog.v(TAG, "key center");
				if (wifiScanListView.getSelectedItem() == null) {
					return true;
				}
				findNextView();
				if ((view = getNextView()) != null) {
					parent.removeAllViews();
					parent.addView(view);
					return true;
				}
				break;
			// case KeyEvent.KEYCODE_DPAD_UP:
			// case KeyEvent.KEYCODE_DPAD_DOWN:
			// return wifiScanListView.onKeyDown(keyCode, event);
			default:
				return false;
			}
		}
		return true;
	}

	@Override
	protected void updateBottomButtons() {
		// TODO Auto-generated method stub
		boolean[] data = new boolean[] { true, false, true, true, true };
		((MenuMain) mContext).setWifiButtonText(4, R.string.menu_wifi_cancel);
		((MenuMain) mContext).displayWifiButtons(data);
	}

}
