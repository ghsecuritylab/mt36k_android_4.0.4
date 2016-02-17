package com.tcl.gesture;	

import com.tcl.GestureJNIAPI.GestureJNI;
import com.tcl.GestureJNIAPI.RunTimeError;
import com.tcl.gesture.EyeSightEngController.OnErrorListener;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.android.internal.R;

public class ToastStyleDialog extends Dialog
{
	
	private Context mContext;

	private View.OnClickListener mClickListener;
	private View.OnClickListener mShowClickListener;
	
    private GestureEngineMode mEngineMode;
    private GestureJNI gestureEngine;
    Handler mHandler;
    
	public ToastStyleDialog(Context context,  View.OnClickListener clickListener ,View.OnClickListener showClickListener)
	{
		super(context,R.style.ToastSytleDialog);
		mContext = context;
		mClickListener = clickListener;
		mShowClickListener = showClickListener;
	}
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.gesture_custom_dialog);
		setContentView(R.layout.gesture_tip_dialog);
		getWindow().setGravity(Gravity.CENTER);
		
		Button tasteButton = (Button)findViewById(R.id.taste);
		Button showButton = (Button)findViewById(R.id.show);
		
		tasteButton.setOnClickListener(mClickListener);
		showButton.setOnClickListener(mShowClickListener);
		
       	mHandler = new Handler();      	
        mEngineMode = new GestureEngineMode(mContext, mHandler);

        gestureEngine = GestureJNI.getInstance();
        gestureEngine.InitGetProcessAction();
        gestureEngine.StartGetProcessAction();
        mEngineMode.setOnErrorListener(new OnErrorListener(){
            
           	public void onError(RunTimeError errorCode) {
           	// TODO Auto-generated method stub
            	Log.v("GestureDetailShow", "mEngineMode.setOnErrorListener" + errorCode);
           		if(errorCode != RunTimeError.STATE_OK){
         			
           	    	if(mEngineMode != null){
           	    		mEngineMode.close();
           	    	}
           	    	gestureEngine.StopGetProcessAction();
           		}
           	}
        });               
	}
	
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
			mStartGestureCallback.onStartGesture();
		}
		return super.dispatchKeyEvent(event);
	}
	private startGestureCallback mStartGestureCallback = null;
	public void setGestureCallback(startGestureCallback cc) {
		mStartGestureCallback = cc;
	}
	public interface startGestureCallback {
		abstract void onStartGesture();
	}
}
