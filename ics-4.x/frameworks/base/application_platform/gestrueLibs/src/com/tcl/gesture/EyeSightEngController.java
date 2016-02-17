package com.tcl.gesture;


import com.tcl.GestureJNIAPI.ActionType;
import com.tcl.GestureJNIAPI.ControlStatus;
import com.tcl.GestureJNIAPI.FaceDetectStatus;
import com.tcl.GestureJNIAPI.FacePosStatus;
import com.tcl.GestureJNIAPI.GestureCallback;
import com.tcl.GestureJNIAPI.GestureJNI;
import com.tcl.GestureJNIAPI.RunTimeError;

import android.os.Handler;
import android.util.Log;


public class EyeSightEngController implements GestureCallback{
    private static final String TAG = "EyeSightEngController";
    
    private boolean isStart;
    private GestureJNI mGestureEngine;
    private ActionChangeListener mChangeListener;
    private OnErrorListener mErrorListener;
    private ActionHandleStrategy mHandleStrategy;
    private Handler mHandler;
    
    private OnControlStatusListener mControlStatusListener;
    private OnFaceStatusListener mFaceStatusListener;
    private OnFacePositionListener mFacePositionListener;
    
    public EyeSightEngController(GestureJNI eyeSightEngine, ActionHandleStrategy handleStrategy) {
        mGestureEngine = eyeSightEngine;
        mHandleStrategy = handleStrategy;
    }
    
    public void setHandler(Handler handler) {
        mHandler = handler;
    }
    
    public void setOnErrorListener(OnErrorListener errorListener) {
        mErrorListener = errorListener;
    }
    
    public void startControl() {
        if(mGestureEngine != null) {
            mGestureEngine.RegisterCallback(this);
            mHandleStrategy.onGetControl();
            isStart = true;
        } //next is test
        else{
        	mHandleStrategy.onGetControl();
        	isStart = true;
        }
    }
    
    public void stopControl() {
    	if(mGestureEngine != null) {
    	    mGestureEngine.RegisterCallback(null);
    	}
        mHandleStrategy.onLoseControl();
        isStart = false;
    }
    
    public boolean isStarted(){
        return isStart;
    }
    
    public void setOnActionChangeListener(ActionChangeListener actionChangeListener){
        mChangeListener = actionChangeListener;
    }


    public void HandleActionOutput(ActionType actionType, ControlStatus actionState) {
    	Log.v("===LiZZ===","HandleActionOutput");
        if(actionType != ActionType.ACTION_NOTHING && isStart)
        {
            switch(actionType) 
            {
                case ACTION_LEFT:
                    runOnUiThread(new Runnable() {

                        public void run() {
                            int prevIndex1 = mHandleStrategy.getCurrentPosition();
                            boolean handled = mHandleStrategy.handleLeft();
                            if(mChangeListener != null) {
                                mChangeListener.onToLeft(prevIndex1, mHandleStrategy.getCurrentPosition(), handled);
                            }
                        }
                        
                    });
                    break; 
                case ACTION_RIGHT:
                    runOnUiThread(new Runnable() {

                        public void run() {
                            int prevIndex2 = mHandleStrategy.getCurrentPosition();
                            boolean handled = mHandleStrategy.handleRight();
                            if(mChangeListener != null) {
                                mChangeListener.onToRight(prevIndex2, mHandleStrategy.getCurrentPosition(), handled);
                            }
                        }
                    });
                    break;
                case ACTION_COVER:
                    runOnUiThread(new Runnable() {

                        public void run() {
                            boolean handled = mHandleStrategy.handleCover(mHandleStrategy.getCurrentPosition());
                            if(mChangeListener != null) {
                                mChangeListener.onCover(mHandleStrategy.getCurrentPosition(), handled);
                            }
                        }
                        
                    });
                    break;
                case ACTION_WAVE: 
                    runOnUiThread(new Runnable() {

                        public void run() {
                            boolean handled = mHandleStrategy.handleWave();
                            if(mChangeListener != null) {
                                mChangeListener.onWave(mHandleStrategy.getCurrentPosition(), handled);
                            }
                        }
                        
                    });
                    break;
            }
        }
        //lizhenzhen
        if(actionType == ActionType.ACTION_NOTHING && isStart){
        	Log.v("LiZZ", "actionType = "+actionType);
        	if(mControlStatusListener != null){
        		mControlStatusListener.onControlStatus(actionState);
        	}
        }
    }

    private void runOnUiThread(Runnable runnable) {
        if(mHandler != null){
            mHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    public interface ActionHandleStrategy {
        boolean handleRight();
        
        boolean handleLeft();
        
        boolean handleWave();
        
        boolean handleCover(int position);
        
        int getCurrentPosition();
        
        void onGetControl();
        
        void onLoseControl();
    }
    
    
    public interface ActionChangeListener{
        
        /**
         * @param prevIndex The index before strategy handle the Left event.
         * @param currentIndex The index after strategy handle the Left event.
         * @param handled If the strategy really handled the Left event. 
         */
        void onToLeft(int prevIndex, int currentIndex, boolean handled);
        /**
         * @param prevIndex The index before strategy handle the Right event.
         * @param currentIndex The index after strategy handle the Right event.
         * @param handled If the strategy really handled the Right event. 
         */
        void onToRight(int prevIndex, int currentIndex, boolean handled);
        /**
         * @param currentIndex The index after strategy handle the Cover event.
         * @param handled If the strategy really handled the Cover event. 
         */
        void onCover(int currentIndex, boolean handled);
        /**
         * @param currentIndex The index after strategy handle the Wave event.
         * @param handled If the strategy really handled the Wave event. 
         */
        void onWave(int currentIndex, boolean handled);
        
    }
    
    public interface OnErrorListener {
        void onError(RunTimeError errorCode);
    }

    public void HandleRunTimeError(RunTimeError errorCode) {
    	Log.v("手势识别", "onError11");
        if(mErrorListener != null) {
        	Log.v("手势识别", "mErrorListener");
            mErrorListener.onError(errorCode);
        }
    }
	public void HandleFaceOutput(ActionType actionType, FaceDetectStatus faceState) {
		// TODO Auto-generated method stub
    	Log.v("===LiZZ===","HandleFaceOutput");
        if(actionType == ActionType.ACTION_NOTHING && isStart){
        	Log.v("LiZZ", "face actionType = "+actionType);
        	if(mFaceStatusListener != null){
        		mFaceStatusListener.onFaceStatus(faceState);
        	}
        }
	}
    //lizhenzhen
    public void setControlStatusListener(OnControlStatusListener ControlStatusListener) {
        mControlStatusListener= ControlStatusListener;
    }
    public interface OnControlStatusListener {
        void onControlStatus(ControlStatus actionState);
    }

    //for face state
    public void setFaceStatusListener(OnFaceStatusListener FaceStatusListener) {
    	mFaceStatusListener= FaceStatusListener;
    }
    public interface OnFaceStatusListener {
        void onFaceStatus(FaceDetectStatus faceState);
    }
    public void HandleFacePosition(FacePosStatus facePosState) {
    	// TODO Auto-generated method stub
    	Log.v("===LiZZ===","HandleFacePosition");
        if(mFacePositionListener != null){
        	Log.v("===LiZZ===","HandleFacePosition faceState = "+ facePosState);
        	mFacePositionListener.onFacePosition(facePosState);
        }
    }
    
    public void setFacePositionListener(OnFacePositionListener FacePositionListener) {
    	mFacePositionListener= FacePositionListener;
    }
    public interface OnFacePositionListener {
        void onFacePosition(FacePosStatus facePosState);
    }
}
