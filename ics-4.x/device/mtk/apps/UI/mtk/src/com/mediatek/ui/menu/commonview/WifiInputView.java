package com.mediatek.ui.menu.commonview;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mediatek.netcm.wifi.WifiAccessPoint;
import com.mediatek.netcm.wifi.WifiConst;
import com.mediatek.netcm.wifi.WifiUtil;
import com.mediatek.ui.R;
import com.mediatek.ui.menu.MenuMain;
import com.mediatek.ui.menu.util.MenuConfigManager;
import com.mediatek.ui.menu.util.NetWork;
import com.mediatek.ui.util.KeyMap;
import com.mediatek.ui.util.MtkLog;

public class WifiInputView extends WifiContentView {

	TextView mInputTitle;
	TextView mInputContent;
	boolean isShut = false;
	int authMode;
	String ssid;
	String TAG = "WifiInputView";
	List<WifiAccessPoint> mListAccessPoint;
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
			if (intent.getAction()!= null && intent.getAction().equals(WifiConst.SCAN_READY)) {
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

	public WifiInputView(Context context, RelativeLayout parent, int state,
			int authMode, String ssid) {
		super(context, parent);
		// TODO Auto-generated constructor stub
		this.addView(inflate(mContext, R.layout.wifi_input_view, null), params);
		((MenuMain) mContext).setCurWifiView(this);
		setState(state);
		this.authMode = authMode;
		this.ssid = ssid;
		netWork = NetWork.getInstance(mContext);
		initView();
		updateBottomButtons();
	}

	public WifiInputView(Context context, RelativeLayout parent, int state,
			WifiAccessPoint mWifiAccessPoint) {
		super(context, parent);
		// TODO Auto-generated constructor stub

		this.addView(inflate(mContext, R.layout.wifi_input_view, null), params);
		((MenuMain) mContext).setCurWifiView(this);
		setState(state);
		this.mWifiAccessPoint = mWifiAccessPoint;
		initView();
		updateBottomButtons();
	}

	private void initView() {
		int state = getState();
		mInputTitle = (TextView) this.findViewById(R.id.wifi_input_text);
		mInputContent = (TextView) this.findViewById(R.id.wifi_input_edit);
		mInputContent.setOnTouchListener(onTouchListener);

		if (mWifiAccessPoint != null) {
			int encrypt = WifiUtil.getSecurity(mWifiAccessPoint.mResult);
			switch (encrypt) {
			case WifiConst.W_SECURITY_WEP:
				setInputName(R.string.menu_wifi_input_key_wep);
				break;
			case WifiConst.W_SECURITY_PSK:
			case WifiConst.W_SECURITY_EAP:
				setInputName(R.string.menu_wifi_input_key_wpa);
				break;
			}
		} else {
			switch (state) {
			case MenuConfigManager.WIFI_INPUT_MANUAL_SSID:
				setInputName(R.string.menu_wifi_input_ssid);
				break;
			case MenuConfigManager.WIFI_INPUT_MANUAL_PASS:
			case MenuConfigManager.WIFI_INPUT_SCAN_PASS:
				switch (authMode) {
				case MenuConfigManager.W_CONFIRM_UNKNOWN:
					setInputName(R.string.menu_wifi_input_key_nosupport);
					break;
				case MenuConfigManager.W_CONFIRM_WEP:
					setInputName(R.string.menu_wifi_input_key_wep);
					break;
				case MenuConfigManager.W_CONFIRM_WPA_PSK_TKIP:
				case MenuConfigManager.W_CONFIRM_WPA_PSK_AES:
				case MenuConfigManager.W_CONFIRM_WPA2_PSK_TKIP:
				case MenuConfigManager.W_CONFIRM_WPA2_PSK_AES:
				case MenuConfigManager.W_CONFIRM_AUTO:
					setInputName(R.string.menu_wifi_input_key_wpa);
					break;
				}
				break;
			}
		}
		mInputContent.requestFocus();
		setInputValue(null);
	}

	public void setInputName(CharSequence text) {
		mInputTitle.setText(text);
	}

	public void setInputName(int text) {
		mInputTitle.setText(text);
	}

	public CharSequence getInputName() {
		return mInputTitle.getText();
	}

	public void setInputValue(CharSequence text) {
		mInputContent.setText(text);
	}

	public void setInputValue(int text) {
		mInputContent.setText(text);
	}

	public CharSequence getInputValue() {
		return mInputContent.getText();
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
	protected void findBackView() {
		// TODO Auto-generated method stub
		isShut = true;
	}

	@Override
	protected void findEnterView() {
		// TODO Auto-generated method stub
		((MenuMain) mContext).hideWifiDialog();
		final KeyboardDialog tcg = new KeyboardDialog(mContext);
		tcg.show();
		tcg.setSize(0.5f, 0.33f);
		tcg.setPositon(70, 0);
		tcg.getvInput().setText(getInputValue() + "_");
		tcg.getvInput().setSelection(tcg.getvInput().getText().length() - 1);
		tcg.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				int action = event.getAction();
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& action == KeyEvent.ACTION_DOWN) {
					tcg.dismiss();
					((MenuMain) mContext).showWifiDialog();
					mInputContent.requestFocus();
					return true;
				}
				return false;
			}
		});
		OnKeyListener listener = new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyMap.KEYCODE_DPAD_CENTER
							|| keyCode == KeyEvent.KEYCODE_ENTER) {
						String name = "";
						if (v.getId() == tcg.getButton_ok().getId()) {
							// tm.clear();
							name = tcg.getInput();
							tcg.dismiss();
						} else if (v.getId() == tcg.getButton_cancel().getId()) {
							tcg.dismiss();
						}
						((MenuMain) mContext).showWifiDialog();
						setInputValue(name);
						mInputContent.requestFocus();
						return true;
					}
				}
				return false;
			}

		};
		tcg.getButton_ok().setOnKeyListener(listener);
		tcg.getButton_cancel().setOnKeyListener(listener);
	}

	@Override
	protected void findNextView() {
		// TODO Auto-generated method stub
		int state = getState();
		switch (state) {
		case MenuConfigManager.WIFI_INPUT_MANUAL_SSID:
			if (checkSsidFormat(getInputValue().toString())) {
				// enter authentication mode choice
				MtkLog.v(TAG, "invalid auth mode");
				WifiAuthModeView wamView = new WifiAuthModeView(mContext,
						parent, getInputValue().toString());
				setNextView(wamView);
			} else {
				WifiCommonTextView view = new WifiCommonTextView(mContext,
						parent, MenuConfigManager.WIFI_COMMON_MANUAL_INVASSID,
						authMode, getInputValue().toString());
				view.setText(R.string.menu_wifi_input_ssid_error);
				setNextView(view);
			}
			break;
		case MenuConfigManager.WIFI_INPUT_SCAN_PASS:
			int encrypt = WifiUtil.getSecurity(mWifiAccessPoint.mResult);
			boolean bPassFormat = false;
			switch (encrypt) {
			case WifiConst.W_SECURITY_WEP:
				bPassFormat = checkPassFormat(getInputValue().toString(),
						MenuConfigManager.W_CONFIRM_WEP);
				break;
			case WifiConst.W_SECURITY_PSK:
			case WifiConst.W_SECURITY_EAP:
				bPassFormat = checkPassFormat(getInputValue().toString(),
						MenuConfigManager.W_CONFIRM_WPA_PSK_TKIP);
				break;
			}
			if (bPassFormat) {

				// enter connection interface
				String pass = getInputValue().toString();

				if (encrypt == WifiConst.W_SECURITY_WEP) {
					if (pass.length() == 5 || pass.length() == 13) {
						pass = getHexPass(pass);
					}
				}
				WifiConnectView view = new WifiConnectView(mContext, parent,
						MenuConfigManager.WIFI_CONNECT_SCAN, mWifiAccessPoint,
						pass, true);
				view.startBackground();
				setNextView(view);
			} else {
				WifiCommonTextView view = new WifiCommonTextView(mContext,
						parent, MenuConfigManager.WIFI_COMMON_SCAN_INVALID,
						mWifiAccessPoint);
				switch (encrypt) {
				case WifiConst.W_ENCRYPT_WEP:
					view.setText(R.string.menu_wifi_invalid_wep_key);
					break;
				case WifiConst.W_ENCRYPT_TKIP:
				case WifiConst.W_ENCRYPT_AES:
					view.setText(R.string.menu_wifi_invalid_wpa_key);
					break;
				}
				setNextView(view);
			}
			break;
		case MenuConfigManager.WIFI_INPUT_MANUAL_PASS:
			if (checkPassFormat(getInputValue().toString(), authMode)) {
				// enter connection interface

				String pass = getInputValue().toString();

				if (authMode == MenuConfigManager.W_CONFIRM_WEP) {
					if (pass.length() == 5 || pass.length() == 13) {
						pass = getHexPass(pass);
					}
				}

				WifiConnectView view = new WifiConnectView(mContext, parent,
						MenuConfigManager.WIFI_INPUT_MANUAL_PASS, authMode,
						ssid, pass);
				view.startBackground();
				setNextView(view);
			} else {
				MtkLog.v(TAG, "into valid");
				WifiCommonTextView view = new WifiCommonTextView(mContext,
						parent, MenuConfigManager.WIFI_COMMON_MANUAL_INVAPASS,
						authMode, ssid);
				switch (authMode) {
				case MenuConfigManager.W_CONFIRM_WEP:
					view.setText(R.string.menu_wifi_invalid_wep_key);
					break;
				case MenuConfigManager.W_CONFIRM_UNKNOWN:
				case MenuConfigManager.W_CONFIRM_WPA_PSK_TKIP:
				case MenuConfigManager.W_CONFIRM_WPA_PSK_AES:
				case MenuConfigManager.W_CONFIRM_WPA2_PSK_TKIP:
				case MenuConfigManager.W_CONFIRM_WPA2_PSK_AES:
				case MenuConfigManager.W_CONFIRM_AUTO:
					view.setText(R.string.menu_wifi_invalid_wpa_key);
					break;
				}
				setNextView(view);
			}
			break;
		}
	}

	private String getHexPass(String source) {
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			int j = c;
			String temp = Integer.toHexString(j);
			sb.append(temp);
		}
		return sb.toString();
	}

	@Override
	protected void findPrevView() {
		// TODO Auto-generated method stub
		int state = getState();
		switch (state) {
		case MenuConfigManager.WIFI_INPUT_SCAN_PASS:
			getAPList();
			break;
		case MenuConfigManager.WIFI_INPUT_MANUAL_PASS:
		case MenuConfigManager.WIFI_INPUT_MANUAL_SSID:
			setPrevView(new WifiMainMenu(mContext, parent));
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

	private boolean checkSsidFormat(String ssid) {
		if (ssid.length() <= 0 || ssid.length() > 32) {
			return false;
		}
		return true;
	}

	private boolean checkPassFormat(String ssid, int auth) {
		switch (auth) {
		case MenuConfigManager.W_CONFIRM_UNKNOWN:
		case MenuConfigManager.W_CONFIRM_WPA_PSK_TKIP:
		case MenuConfigManager.W_CONFIRM_WPA_PSK_AES:
		case MenuConfigManager.W_CONFIRM_WPA2_PSK_TKIP:
		case MenuConfigManager.W_CONFIRM_WPA2_PSK_AES:
		case MenuConfigManager.W_CONFIRM_AUTO:
			if (ssid.length() == 64 && ssid.matches("[0-9A-Fa-f]{64}")) {
				return true;
			} else if (ssid.length() >= 8 && ssid.length() <= 63) {
				return true;
			} else {
				return false;
			}
		case MenuConfigManager.W_CONFIRM_NONE:
			break;
		case MenuConfigManager.W_CONFIRM_WEP:
			Pattern pattern1 = Pattern
					.compile("[0-9a-fA-F]{10}|[0-9a-fA-F]{26}");
			Pattern pattern2 = Pattern.compile("\\w{5}");
			Pattern pattern3 = Pattern.compile("\\w{13}");
			if (!pattern1.matcher(ssid).matches()
					&& !pattern2.matcher(ssid).matches()
					&& !pattern3.matcher(ssid).matches()) {
				return false;
			}
			break;
		}
		return true;
	}

	@Override
	protected void updateBottomButtons() {
		// TODO Auto-generated method stub
		int state = getState();
		boolean[] data = new boolean[] { true, true, false, true, true };

		switch (state) {
		case MenuConfigManager.WIFI_INPUT_MANUAL_PASS:
		case MenuConfigManager.WIFI_INPUT_SCAN_PASS:
			((MenuMain) mContext)
					.setWifiButtonText(1, R.string.menu_wifi_input);
			break;
		case MenuConfigManager.WIFI_INPUT_MANUAL_SSID:
			((MenuMain) mContext).setWifiButtonText(1, R.string.menu_wifi_set);
			break;
		default:
			data = new boolean[] { false, false, false, false, false };
			break;
		}
		((MenuMain) mContext).setWifiButtonText(4, R.string.menu_wifi_cancel);
		((MenuMain) mContext).displayWifiButtons(data);
	}

}
