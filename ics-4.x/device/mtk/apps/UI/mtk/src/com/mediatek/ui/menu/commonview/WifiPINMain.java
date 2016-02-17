package com.mediatek.ui.menu.commonview;

import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mediatek.ui.R;
import com.mediatek.ui.menu.MenuMain;
import com.mediatek.ui.menu.util.MenuConfigManager;
import com.mediatek.ui.menu.util.NetWork;

public class WifiPINMain extends WifiContentView {

	Button refreshPIN;
	TextView pinCode;
	boolean isShut = false;
	String TAG = "WifiPINMain";
	NetWork netWork;

	public WifiPINMain(Context context, RelativeLayout parent) {
		super(context, parent);
		// TODO Auto-generated constructor stub

		this.addView(inflate(mContext, R.layout.wifi_pin_main, null), params);
		((MenuMain) mContext).setCurWifiView(this);
		refreshPIN = (Button) this.findViewById(R.id.wifi_pin_refresh_button);
		pinCode = (TextView) this.findViewById(R.id.wifi_pin_pinvalue);
		refreshPIN.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				setPinCode();
			}
		});
		refreshPIN.setOnTouchListener(onTouchListener);
		updateBottomButtons();
		refreshPIN.requestFocus();
		netWork = NetWork.getInstance(mContext);
		setPinCode();
	}

	public String getPinCode() {
		String source = pinCode.getText().toString();
		return source.substring(1, source.length() - 1);
	}
	
	public void setPinCode(){
		String newPin = refreshPinCode();
		if (newPin == null) {
			pinCode.setText("[00000000]");
		} else {
			pinCode.setText("[" + newPin + "]");
		}
	}

	public void setPinCode(String text) {
		pinCode.setText("[" + text + "]");
	}

	public String refreshPinCode() {
		return netWork.getPinCode();
	}

	@Override
	protected void findBackView() {
		// TODO Auto-generated method stub
		isShut = true;
	}

	@Override
	protected void findEnterView() {
		// TODO Auto-generated method stub
		setPinCode();
	}

	@Override
	protected void findNextView() {
		// TODO Auto-generated method stub
		// enter connection interface
		final TurnkeyCommDialog confirmDialog = new TurnkeyCommDialog(mContext,
				3);
		confirmDialog.setMessage(mContext
				.getString(R.string.menu_wifi_need_special));
		confirmDialog.setButtonYesName(mContext
				.getString(R.string.menu_setup_button_yes));
		confirmDialog.setButtonNoName(mContext
				.getString(R.string.menu_setup_button_no));
		confirmDialog.show();
		confirmDialog.getButtonNo().requestFocus();
		((MenuMain) mContext).hideWifiDialog();
		confirmDialog.setPositon(-20, 70);

		confirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				int action = event.getAction();
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& action == KeyEvent.ACTION_DOWN) {

					confirmDialog.dismiss();
					((MenuMain) mContext).showWifiDialog();
					refreshPIN.requestFocus();
					return true;
				}
				return false;
			}
		});

		OnKeyListener yesListener = new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_ENTER
							|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
						((MenuMain) mContext).setPinCode(getPinCode());
						confirmDialog.dismiss();
						((MenuMain) mContext).showWifiDialog();
						WifiConnectView wcView = new WifiConnectView(mContext,
								parent,
								MenuConfigManager.WIFI_CONNECT_WPS_SCANING, -1,
								"", "");
						wcView.startBackground();
						parent.removeAllViews();
						parent.addView(wcView);
						return true;
					}
				}
				return false;
			}
		};

		OnKeyListener noListener = new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_ENTER
							|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
						((MenuMain) mContext).setPinCode(getPinCode());
						confirmDialog.dismiss();
						((MenuMain) mContext).showWifiDialog();
						WifiConnectView wcView = new WifiConnectView(mContext,
								parent,
								MenuConfigManager.WIFI_CONNECT_PIN_AUTO, -1,
								"", "");
						wcView.startBackground();
						parent.removeAllViews();
						parent.addView(wcView);
						return true;
					}
				}
				return false;
			}
		};
		confirmDialog.getButtonNo().setOnKeyListener(noListener);
		confirmDialog.getButtonYes().setOnKeyListener(yesListener);
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
				findEnterView();
				if ((view = getEnterView()) != null) {
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
		boolean[] data = new boolean[] { true, false, false, true, true };
		((MenuMain) mContext).setWifiButtonText(4, R.string.menu_wifi_cancel);
		((MenuMain) mContext).displayWifiButtons(data);
	}

}
