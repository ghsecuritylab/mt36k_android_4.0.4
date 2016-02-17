package com.tcl.gesture;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.android.internal.R;

public class MessageDialog extends Dialog{
	private String mMessage;
	private TextView text;
	private String TAG = "MessageDialog";
	
	public MessageDialog(Context context, String msg) {
		super(context,R.style.ToastSytleDialog);
		mMessage = msg;
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	private void initView() {
		setContentView(R.layout.gesture_message_box);
		text = (TextView) findViewById(R.id.Showtoasttext);
		text.setText(mMessage);
	}
	public void setMessage(String msg) {
		mMessage = msg;
		text.setText(mMessage);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG , "onKeyDown");
		if ( (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK ||
				keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_UP) ) {
			Log.d(TAG , "onKeyDown dismiss");
			dismiss();
			//return true;
		}
		//return true;
		return super.onKeyDown(keyCode, event);
	}
}
