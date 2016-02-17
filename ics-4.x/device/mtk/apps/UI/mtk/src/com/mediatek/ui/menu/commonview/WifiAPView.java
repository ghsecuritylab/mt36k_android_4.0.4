package com.mediatek.ui.menu.commonview;

import android.content.Context;
import android.widget.TextView;

import com.mediatek.netcm.wifi.WifiAccessPoint;
import com.mediatek.netcm.wifi.WifiConst;
import com.mediatek.ui.R;

public class WifiAPView extends ListViewItemView {

	private Context mContext;
	private static final String TAG = "WifiAPView";
	TextView mNameView;
	TextView mSecurityView;
	TextView mSignalView;
	private WifiAccessPoint mAP;

	public WifiAPView(Context context) {
		super(context);
		this.mContext = context;
		init();
		// TODO Auto-generated constructor stub
	}

	private void init() {
		this.setFocusable(false);
		this.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
		this.addView(inflate(mContext, R.layout.wifi_ap_view, null));
		mNameView = (TextView) findViewById(R.id.wifi_ap_name);
		mSecurityView = (TextView) findViewById(R.id.wifi_ap_security);
		mSignalView = (TextView) findViewById(R.id.wifi_ap_signal);
	}

	public void setAPName(String text) {
		mNameView.setText(text);
	}

	public void setAPName(int text) {
		mNameView.setText(text);
	}

	public void setAPSecurity(String text) {
		mSecurityView.setText(text);
	}

	public void setAPSecurity(int text) {
		mSecurityView.setText(text);
	}

	public void setAPSignal(String text) {
		mSignalView.setText(text);
	}

	public void setAPSignal(int text) {
		mSignalView.setText(text);
	}

	public TextView getAPNameView() {
		return mNameView;
	}

	public TextView getAPSecurityView() {
		return mSecurityView;
	}

	public TextView getAPSignalView() {
		return mSignalView;
	}

	public void setAdapter(WifiAccessPoint mAP) {
		this.mAP = mAP;
		setAPName(mAP.getSsid());
		int protocal = mAP.getProtocol();
		int security = mAP.getEncrypt();
		getProtocal(protocal, security);
		setAPSignal(mAP.getSignalStrength() + "");
	}

	public void getProtocal(int source, int security) {
		switch (source) {
		case WifiConst.W_CONFIRM_WPA_PSK:
			setAPSecurity(R.string.menu_wifi_ap_security_wpa);
			break;
		case WifiConst.W_CONFIRM_WEP:		
			// setAPSecurity(R.string.menu_wifi_ap_security_none);
			setAPSecurity(R.string.menu_wifi_ap_security_wep);
			break;
		case WifiConst.W_CONFIRM_WPA2_PSK:
			setAPSecurity(R.string.menu_wifi_ap_security_wpa2);
			break;
		case WifiConst.W_CONFIRM_PSK_AUTO:
			setAPSecurity(R.string.menu_wifi_ap_security_auto);
			break;
		case WifiConst.W_CONFIRM_EAP_AUTO:
		case WifiConst.W_CONFIRM_WPA_EAP:
		case WifiConst.W_CONFIRM_WPA2_EAP:
			setAPSecurity(R.string.menu_wifi_ap_security_notsupport);
			break;
		case WifiConst.W_CONFIRM_UNKNOWN:
			if (security == WifiConst.W_ENCRYPT_NONE) {
				setAPSecurity(R.string.menu_wifi_ap_security_none);
			} else {
				setAPSecurity(R.string.menu_wifi_ap_security_notsupport);
			}
			break;
		}
	}

}
