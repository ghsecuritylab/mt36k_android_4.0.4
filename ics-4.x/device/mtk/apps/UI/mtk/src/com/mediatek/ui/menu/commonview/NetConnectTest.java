package com.mediatek.ui.menu.commonview;

import com.mediatek.ui.R;

import android.content.Context;
import android.view.View;
/**
 * @usePart setup_network_configuration_connection test
 * @author hs_haosun
 *
 */
public class NetConnectTest implements Runnable{

	private Context mContext;
	private TurnkeyCommDialog dialog;
	private String type;
	public NetConnectTest(Context mContext) {
		this.mContext = mContext;
	}
	public NetConnectTest(Context mContext,TurnkeyCommDialog dialog,String type) {
		this.mContext = mContext;
		this.dialog = dialog;
		this.type = type;
	}
	public void run() {
		// TODO do after start connection
		if ("success".equals(type)){
			dialog.getTextView().setText(mContext.getString(R.string.menu_setup_connetion_info2));
			dialog.getWaitView().setVisibility(View.GONE);
			dialog.getLoading().stopDraw();
			dialog.getLoading().setVisibility(View.GONE);
		}else {
			dialog.getTextView().setText(mContext.getString(R.string.menu_setup_connetion_info3));
			dialog.getWaitView().setVisibility(View.GONE);
			dialog.getLoading().stopDraw();
			dialog.getLoading().setVisibility(View.GONE);
		}
		
		
	}

}
