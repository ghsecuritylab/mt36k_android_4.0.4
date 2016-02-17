package com.mediatek.ui.menu.commonview;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mediatek.ui.menu.MenuMain;

public abstract class WifiContentView extends RelativeLayout {

	protected Context mContext;
	protected RelativeLayout parent;
	protected WifiContentView prevView;
	protected WifiContentView nextView;
	protected WifiContentView backView;
	protected WifiContentView enterView;
	private int state;
	protected View curView;
	protected List<Button> btnGroup;
	protected int btnIndex = -1;
	String TAG = "WifiContentView";

	public WifiContentView(Context context, RelativeLayout parent) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.parent = parent;
	}

	public OnClickListener enterListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_DPAD_CENTER);
			WifiContentView.this.onKeyDown(KeyEvent.KEYCODE_DPAD_CENTER,
					keyEvent);
		}
	};

	private int mp = RelativeLayout.LayoutParams.MATCH_PARENT;
	protected RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
			mp, mp);

	// protected OnItemSelectedListener enterItemListener = new
	// OnItemSelectedListener() {
	//
	// public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
	// long arg3) {
	// // TODO Auto-generated method stub
	// KeyEvent keyEvent=new
	// KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_CENTER);
	// WifiContentView.this.onKeyDown(KeyEvent.KEYCODE_DPAD_CENTER,keyEvent);
	// }
	//
	// public void onNothingSelected(AdapterView<?> arg0) {
	// // TODO Auto-generated method stub
	//			
	// }
	// };

	OnTouchListener onTouchListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			v.requestFocusFromTouch();
			return false;
		}
	};

	protected abstract void updateBottomButtons();

	protected void setBottomButtons(boolean[] switcher) {
		((MenuMain) mContext).displayWifiButtons(switcher);
	}

	protected WifiContentView getNextView() {
		return nextView;
	}

	protected WifiContentView getPrevView() {
		return prevView;
	}

	protected WifiContentView getBackView() {
		return backView;
	}

	protected WifiContentView getEnterView() {
		return enterView;
	}

	protected abstract void findNextView();

	protected abstract void findPrevView();

	protected abstract void findBackView();

	protected abstract void findEnterView();

	protected void setPrevView(WifiContentView view) {
		this.prevView = view;
	}

	protected void setNextView(WifiContentView view) {
		this.nextView = view;
	}

	protected void setEnterView(WifiContentView view) {
		this.enterView = view;
	}

	protected void setBackView(WifiContentView view) {
		this.backView = view;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}

	public void getFocus() {
		if (curView != null) {
			curView.requestFocus();
			curView.requestFocusFromTouch();
		} else {		
			this.requestFocus();
			this.requestFocusFromTouch();
		}
	}

}
