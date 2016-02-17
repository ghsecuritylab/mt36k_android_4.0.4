package com.android.internal.policy.impl;

import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.content.Context;
import android.util.Log;
import android.content.Intent;

public class ChangeProjectId {
private static ChangeProjectId mChangeProjectId=null;
private static Context   _mContext=null;
private final int   DELAY_TIME=10000;  
private final String   KEYCODE_MENU="m";  
private final int  MAX_PROJECT_ID=200; 
private boolean threadRunning=false;
private String input_num_str = "";

	ChangeProjectId(Context mContext){
	_mContext = mContext;
}
public boolean getTreadIsRunningStatus(){
	return threadRunning;
}

public  boolean judgeStringEquel062598() {
	if ((input_num_str.equalsIgnoreCase("062598"))
			&& (threadRunning)) {
		return true;
	}
	else return false;
	
}
public  boolean inputKeyCode(int keyCode) {
	boolean isKeyCodeNum = false;

	if (((keyCode >= KeyEvent.KEYCODE_0) & (keyCode <= KeyEvent.KEYCODE_9))
			|| (keyCode == KeyEvent.KEYCODE_MENU)) {
		isKeyCodeNum = true;
		Log.v("ID", "keyCode  " + keyCode+"threadRunning"+threadRunning);
		if (!threadRunning) {
			if ((keyCode == KeyEvent.KEYCODE_0)) {
				input_num_str = "";
				threadRunning = true;
				input_num_str = "" + (keyCode - 7);
				sendMsgDelay(DELAY_TIME);// delay two seconds to
				// post message
			}
		} else { 
			//Log.v("ID","keyCode +threadRuning "+keyCode);
			if (keyCode == KeyEvent.KEYCODE_MENU) {
				input_num_str = input_num_str + (KEYCODE_MENU);
			} else {
				input_num_str = input_num_str + (keyCode - 7);
			}
		}
	}
	return isKeyCodeNum;
}

private void sendMsgDelay( int delayMillis) {
	Message msg = Message.obtain();
	mHandler.sendMessageDelayed(msg, delayMillis);
}
private Handler mHandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
		if (input_num_str != "")
			Log.v("ID", "handleMessage" + input_num_str);
		if (input_num_str.contains("062598"+KEYCODE_MENU)) {
			input_num_str = input_num_str.substring(input_num_str
					.indexOf(""+KEYCODE_MENU) + 1);
			Log.v("ID", "input_projectid" + input_num_str);

			int data = Integer.parseInt(input_num_str, 10);
			if (data < MAX_PROJECT_ID)
			{	
				Log.v("ID","sendBrocast to change Project id--"+data);
				Intent i = new Intent("com.tcl.changeProjectId");
				i.putExtra("ID", data);
				_mContext.sendBroadcast(i);
			}
		}

		input_num_str = "";
		threadRunning = false;
		mHandler.removeMessages(msg.what);
		
	}
};


}
