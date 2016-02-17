package com.mediatek.ui.menu.commonview;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mediatek.ui.R;
import com.mediatek.ui.menu.MenuMain;
import com.mediatek.ui.menu.util.MenuConfigManager;

public class WifiMainMenu extends WifiContentView {

	boolean isShut = false;
	String TAG = "WifiMainMenu";

	Button btnScan;
	Button btnManual;
	Button btnAuto;

	public WifiMainMenu(Context context, RelativeLayout parent) {
		super(context, parent);
		// TODO Auto-generated constructor stub
		// this.removeAllViews();
		// parent.removeAllViews();

		this.addView(inflate(mContext, R.layout.wifi_main_menu, null), params);
		((MenuMain) mContext).setCurWifiView(this);
		btnScan = (Button) this.findViewById(R.id.wifi_main_btn_scan);
		btnManual = (Button) this.findViewById(R.id.wifi_main_btn_manual);
		btnAuto = (Button) this.findViewById(R.id.wifi_main_btn_auto);
        btnAuto.setVisibility(View.INVISIBLE);
		btnScan.setOnClickListener(enterListener);
		btnManual.setOnClickListener(enterListener);
		btnAuto.setOnClickListener(enterListener);
		btnAuto.setOnTouchListener(onTouchListener);
		btnScan.setOnTouchListener(onTouchListener);
		btnManual.setOnTouchListener(onTouchListener);
		btnScan.requestFocus();
		btnGroup = new ArrayList<Button>();
		btnIndex = 0;
		btnGroup.add(btnScan);
		btnGroup.add(btnManual);
		btnGroup.add(btnAuto);
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
		if (btnScan.isFocused()) {
			WifiConnectView wcView = new WifiConnectView(mContext, parent,
					MenuConfigManager.WIFI_CONNECT_SCANING, -1, "", "");
			wcView.startBackground();
			setNextView(wcView);
		} else if (btnManual.isFocused()) {
			Log.v(TAG, "goto manual");
			WifiInputView wiView = new WifiInputView(mContext, parent,
					MenuConfigManager.WIFI_INPUT_MANUAL_SSID, -1, "");
			setNextView(wiView);
			// jump to the input interface of ssid
		} else if (btnAuto.isFocused()) {
			// jump to the choice interface of pin/pbc
			WifiWpsMain wwMain = new WifiWpsMain(mContext, parent);
			setNextView(wwMain);
		}
	}

	@Override
	protected void findPrevView() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateBottomButtons() {
		// TODO Auto-generated method stub
		boolean[] data = new boolean[] { false, false, true, true, true };
		((MenuMain) mContext).displayWifiButtons(data);
		((MenuMain) mContext).setWifiButtonText(4, R.string.menu_wifi_cancel);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.v(TAG, "key down");
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			WifiContentView view;
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.v(TAG, "back and recover");
				findBackView();
				if (isShut) {
					((MenuMain) mContext).recoverMenu();
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				Log.v(TAG, "find next view");
				findNextView();
				if ((view = getNextView()) != null) {
					parent.removeAllViews();
					parent.addView(view);
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				Log.v(TAG, "find next view");
				findNextView();
				if ((view = getNextView()) != null) {
					parent.removeAllViews();
					parent.addView(view);
					return true;
				}
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
}
