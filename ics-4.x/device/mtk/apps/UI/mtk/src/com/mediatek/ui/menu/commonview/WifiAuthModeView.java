package com.mediatek.ui.menu.commonview;

import java.util.ArrayList;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mediatek.ui.R;
import com.mediatek.ui.menu.MenuMain;
import com.mediatek.ui.menu.util.MenuConfigManager;

public class WifiAuthModeView extends WifiContentView {

	String ssid;
	boolean isShut = false;
	LinearLayout btnLayout;
	String TAG = "WifiAuthModeView";

	public WifiAuthModeView(Context context, RelativeLayout parent, String ssid) {
		super(context, parent);

		this.addView(inflate(mContext, R.layout.wifi_auth_mode, null), params);
		((MenuMain) mContext).setCurWifiView(this);
		this.ssid = ssid;
		btnLayout = (LinearLayout) this
				.findViewById(R.id.wifi_auth_mode_btn_layout);
		updateBottomButtons();
		btnLayout.getChildAt(1).requestFocus();
		btnGroup = new ArrayList<Button>();
		btnIndex = 0;
		for (int i = 1; i < btnLayout.getChildCount(); i++) {
			btnLayout.getChildAt(i).setOnClickListener(enterListener);
			btnLayout.getChildAt(i).setOnTouchListener(onTouchListener);
			btnGroup.add((Button) btnLayout.getChildAt(i));
		}
		// TODO Auto-generated constructor stub
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
		int index = 1;
		for (int i = 1; i < btnLayout.getChildCount(); i++) {
			if (btnLayout.getChildAt(i).isFocused()) {
				index = i;
				break;
			}
		}
		if (index == 1) {
			// enter the connection interface
			WifiConnectView view = new WifiConnectView(mContext, parent,
					MenuConfigManager.WIFI_CONNECT_MANUAL, index, ssid, "");
			view.startBackground();
			setNextView(view);
		} else {
			WifiInputView view = new WifiInputView(mContext, parent,
					MenuConfigManager.WIFI_INPUT_MANUAL_PASS, index, ssid);
			setNextView(view);
		}
	}

	@Override
	protected void findPrevView() {
		// TODO Auto-generated method stub
		WifiInputView view = new WifiInputView(mContext, parent,
				MenuConfigManager.WIFI_INPUT_MANUAL_SSID, -1, "");
		setPrevView(view);
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
				findNextView();
				if ((view = getNextView()) != null) {
					if (isShut) {
						((MenuMain) mContext).recoverMenu();
					}
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
