package com.mediatek.ui.menu.commonview;

import com.mediatek.netcm.wifi.WifiAccessPoint;
import com.mediatek.ui.R;

import android.content.Context;
import android.widget.TextView;

public class WifiWpsAPView extends ListViewItemView {

	private Context mContext;
	private static final String TAG = "WifiWpsAPView";
	TextView mNameView;
	TextView mBssidView;
	private WifiAccessPoint mAP;
	
	
	public WifiWpsAPView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		init();
	}
	
	private void init() {
		this.setFocusable(false);
		this.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
		this.addView(inflate(mContext, R.layout.wifi_wps_ap_view, null));
		mNameView = (TextView) findViewById(R.id.wifi_wps_ap_name);
		mBssidView = (TextView) findViewById(R.id.wifi_wps_ap_bssid);		
	}

	public void setAPName(String text) {
		mNameView.setText(text);
	}

	public void setAPName(int text) {
		mNameView.setText(text);
	}

	public void setAPBssid(String text) {
		mBssidView.setText(text);
	}

	public void setAPBssid(int text) {
		mBssidView.setText(text);
	}
	
	public TextView getAPNameView() {
		return mNameView;
	}

	public TextView getAPSecurityView() {
		return mBssidView;
	}
	
	public void setAdapter(WifiAccessPoint mAP) {
		this.mAP = mAP;
		setAPName(mAP.getSsid());
		String bSsid=mAP.mResult.BSSID;		
		setAPBssid(bSsid);
	}
}
