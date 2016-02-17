package com.mediatek.ui.menu.commonview;

import java.util.ArrayList;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mediatek.ui.R;
import com.mediatek.ui.menu.MenuMain;
import com.mediatek.ui.menu.util.MenuConfigManager;

public class WifiWpsMain extends WifiContentView {

	boolean isShut = false;
	Button btnPIN;
	Button btnPBC;
	String TAG = "WifiWpsMain";

	public WifiWpsMain(Context context, RelativeLayout parent) {
		super(context, parent);
		// TODO Auto-generated constructor stub
		this.addView(inflate(mContext, R.layout.wifi_wps_main, null), params);
		((MenuMain) mContext).setCurWifiView(this);
		btnPIN = (Button) this.findViewById(R.id.wifi_wps_main_pin);
		btnPBC = (Button) this.findViewById(R.id.wifi_wps_main_pbc);
		btnPBC.setOnClickListener(enterListener);
		btnPIN.setOnClickListener(enterListener);
		btnPIN.setOnTouchListener(onTouchListener);
		btnPBC.setOnTouchListener(onTouchListener);
		btnPIN.requestFocus();
		btnGroup = new ArrayList<Button>();
		btnIndex = 0;
		btnGroup.add(btnPIN);
		btnGroup.add(btnPBC);
		updateBottomButtons();
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
		if (btnPIN.isFocused()) {
			WifiPINMain wpView = new WifiPINMain(mContext, parent);
			setNextView(wpView);
		} else if (btnPBC.isFocused()) {
			WifiCommonTextView view = new WifiCommonTextView(mContext, parent,
					MenuConfigManager.WIFI_COMMON_PBC_HINT, -1, "");
			view.setText(R.string.menu_wifi_wps_pbc_hint);
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
				findNextView();
				if ((view = getNextView()) != null) {
					parent.removeAllViews();
					parent.addView(view);
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				findNextView();
				if ((view = getNextView()) != null) {
					parent.removeAllViews();
					parent.addView(view);
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				if (btnIndex - 1 >= 0) {
					btnIndex = (btnIndex - 1) % btnGroup.size();
					curView = btnGroup.get(btnIndex);
				}
				return false;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (btnIndex + 1 < btnGroup.size()) {
					btnIndex = (btnIndex + 1) % btnGroup.size();
					curView = btnGroup.get(btnIndex);
				}
				return false;
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
