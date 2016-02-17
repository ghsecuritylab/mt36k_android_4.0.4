package com.tcl.gesture;


import com.tcl.GestureJNIAPI.ActionType;
import com.tcl.GestureJNIAPI.ControlStatus;
import com.tcl.GestureJNIAPI.FaceDetectStatus;
import com.tcl.GestureJNIAPI.FacePosStatus;
import com.tcl.GestureJNIAPI.GestureJNI;
import com.tcl.gesture.EyeSightEngController.ActionChangeListener;
import com.tcl.gesture.EyeSightEngController.ActionHandleStrategy;
import com.tcl.gesture.EyeSightEngController.OnControlStatusListener;
import com.tcl.gesture.EyeSightEngController.OnErrorListener;
import com.tcl.gesture.EyeSightEngController.OnFacePositionListener;
import com.tcl.gesture.EyeSightEngController.OnFaceStatusListener;
import com.tcl.gesture.ToastStyleDialog.startGestureCallback;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;

import java.util.ArrayList;



/**
 *  Help activity handle the focus change.
 *  Use this class you must specify the ActionHandleStrategy to tell me how to control focus.
 *  Note that the mode added in here has order. For example, the lower order will go into higher when Cover event occurs.
 */
public class GestureEngineMode {
    private static final int NOTIFICATION_DISMISS_INTERVAL = 3000;
    private static final int GESTURE_ICON_INTERVAL = 1000;
    
    private int currentMode;
    private boolean isStart;
    private ArrayList<EyeSightEngController> mContorollers;
    private Handler mHandler;
    private Context mContext;
    private OnErrorListener mErrorListener;
    private GesturePopUp gesturePopUp;
    private FaceDetectStatus mFaceState = FaceDetectStatus.FACE_FINDED;
    private ControlStatus mActionState = ControlStatus.ACTION_ON_CONTROL;
    private FacePosStatus mFacePosStatus;
    
    private static Dialog gestureDialog;
    
    private boolean mGestureManualFlag;
    private boolean mGestureNotificationShowFlag;
    private boolean mManualFirstShowFlag = true;
    private Runnable keepNormalRunnable = new Runnable(){

        
        public void run() {
            gesturePopUp.setToNormal();
        }
        
    };
    
    private ActionChangeListener manageListener = new ActionChangeListener(){

        
        public void onCover(int currentIndex, boolean handled) {
            if(handled) {
                gotoNextMode();
            }
	           mHandler.post(new Runnable() {	                
	                
	                public void run() {
	                	gesturePopUp.setToCover();
	                }
	            });
//            gesturePopUp.setToCover();
            keepNormal();
        }

        
        public void onWave(int currentIndex, boolean handled) {
            if(handled) {
                gotoPrevMode();
            }
	           mHandler.post(new Runnable() {	                
	                
	                public void run() {
	                	gesturePopUp.setToWave();
	                }
	            });           
//            gesturePopUp.setToWave();
            keepNormal();
        }

        
        public void onToLeft(int prevIndex, int currentIndex, boolean handled) {
	           mHandler.post(new Runnable() {	                
	                
	                public void run() {
	                	gesturePopUp.setToLeft();
	                }
	            });
//            gesturePopUp.setToLeft();
            keepNormal();
        }

        
        public void onToRight(int prevIndex, int currentIndex, boolean handled) {
	           mHandler.post(new Runnable() {	                
	                
	                public void run() {
	                	gesturePopUp.setToRight();
	                }
	            });
//            gesturePopUp.setToRight();
            keepNormal();
        }
        
    };
    
    private void keepNormal() {
        mHandler.removeCallbacks(keepNormalRunnable);
        mHandler.postDelayed(keepNormalRunnable, GESTURE_ICON_INTERVAL);
    }
    
    public GestureEngineMode(Context context, Handler handler) {
        mContorollers = new ArrayList<EyeSightEngController>();
        mContext = context;
        mHandler = handler;
        gesturePopUp = new GesturePopUp(context);
    }
    
    /**
     * Use it to enable a manual display when the first wave event occurs in this mode.
     * @param gestureEngine The gesture engine to catch the gesture event.
     */
    public void enableGestureManual(GestureJNI gestureEngine){
        if(isStarted()) {
            throw new RuntimeException("enable Gesture notification before start.");
        }
       
        //lizhenzhen
        enableNotificationShowControl(gestureEngine);
        enableGestureNotificationControl(mContext, mHandler, gestureEngine);
    }

    //lizhenzhen

    private Runnable enableGestureRunnable = new Runnable(){

        
        public void run() {
            gesturePopUp.setToManual();
        }
        
    };

    public boolean getNotificationFlag(){
    	return mGestureManualFlag;
    }

    private OnControlStatusListener mControlStatusListener = new OnControlStatusListener(){
    	
    	public void onControlStatus(ControlStatus actionState) {
    		// TODO Auto-generated method stub
    		//无脸部识别时，也无控制权
//    		if(mFaceState == FaceDetectStatus.FACE_MISSED){
    		if(mFacePosStatus == FacePosStatus.FACE_POS_DEVIATED){
        		
    			actionState = ControlStatus.ACTION_LOSE_CONTROL;
//    			return;
    		}
    		Log.v("===LZZ===","onControlStatus actionState = "+ actionState);
    		if(mActionState == actionState){
    			return;
    		}
    		
    		
    		
        	if(actionState == ControlStatus.ACTION_LOSE_CONTROL){
        		//灰显
        		keepControl(false);
        	}else if(actionState == ControlStatus.ACTION_ON_CONTROL){
        		keepControl(true);
        	}
    		mActionState = actionState;
    	}
    };
    
    private OnFaceStatusListener mFaceStatusListener = new OnFaceStatusListener(){
    	
    	public void onFaceStatus(FaceDetectStatus faceState) {
    		// TODO Auto-generated method stub
    		Log.v("===LiZZ===","onFaceStatus faceState = "+ faceState);
    		if(mFaceState == faceState){
    			return;
    		}
    		
    		mFaceState = faceState;
    		
    		if(faceState == FaceDetectStatus.FACE_FINDED){
//    			SetFaceIcon(true);
    		}else if(faceState == FaceDetectStatus.FACE_MISSED){
    			if(mFacePosStatus == FacePosStatus.FACE_POS_DEVIATED){
    				return;
    			}
    			SetFaceIcon(false);
    			keepControl(false);
    		}
    	}
    };
    
    private OnFacePositionListener mFacePositionListener = new OnFacePositionListener(){
    	public void onFacePosition(FacePosStatus facePosState) {
    		Log.v("===LiZZ===","onFacePosition faceState = "+ facePosState);
    		if(mFacePosStatus == facePosState){
    			return;
    		}
    		mFacePosStatus = facePosState;
    		
    		if(facePosState == FacePosStatus.FACE_POS_OK){
    			SetFaceIcon(true);
    	        if(mGestureManualFlag && mManualFirstShowFlag){
    	        	mHandler.post(new Runnable(){
    	        		
    	        		public void run() {
    	        			// TODO Auto-generated method stub
    	        			gesturePopUp.notificationShow();
    	        		}
    	        	});
    	        	
    	        	mManualFirstShowFlag = false;
    	        }
    		}else if(facePosState == FacePosStatus.FACE_POS_DEVIATED){
    			if(mFaceState == FaceDetectStatus.FACE_MISSED){
    		         return;
    			}
    			SetFaceIcon(false);
    			keepControl(false);
    		}
    	}
    };
    
    private void enableGestureNotificationControl(Context context, Handler handler,
            GestureJNI eyeSightEngine) {
        ActionHandleStrategy notificationStrategy = new GestureNotificationStrategy(context, handler);
        EyeSightEngController notificationController = new EyeSightEngController(eyeSightEngine, notificationStrategy);
        notificationController.setOnErrorListener(mErrorListener);
        
        //lizhenzhen
        mGestureManualFlag = true;
        notificationController.setControlStatusListener(mControlStatusListener);
        //检测当没有脸部识别时，要切换无脸部识别的图标
        notificationController.setFaceStatusListener(mFaceStatusListener);
        
        notificationController.setFacePositionListener(mFacePositionListener);
        
        mContorollers.add(1, notificationController);

        gesturePopUp.setToManual();

    }
    private void enableNotificationShowControl(GestureJNI eyeSightEngine) {
        ActionHandleStrategy notificationStrategy = new GestureNotificationShowStrategy();
        EyeSightEngController notificationController = new EyeSightEngController(eyeSightEngine, notificationStrategy);
        notificationController.setOnErrorListener(mErrorListener);
        
        mContorollers.add(0, notificationController);

    }
    private void removeGestureNotificationControl(){
    	if(!mGestureManualFlag){
    		return;
    	}
    	mGestureManualFlag = false; //lzz added
        removeMode(0);
    }
    
    
    /**
     * Add ActionHandleStrategy to this mode, note that the strategy added here has its order in the mode.
     * @param gestureEngine The gesture engine, used to catch the gesture event.
     * @param actionStrategy The specific ActionHandleStrategy which will be added in this mode.
     */
    public void addMode(GestureJNI gestureEngine, ActionHandleStrategy actionStrategy) {
        EyeSightEngController controller = new EyeSightEngController(gestureEngine, actionStrategy);
        controller.setOnActionChangeListener(manageListener);
        controller.setHandler(mHandler);
        Log.v("手势识别", "addMode");
        controller.setOnErrorListener(mErrorListener);
        //lizhenzhen
        controller.setControlStatusListener(mControlStatusListener);
        controller.setFaceStatusListener(mFaceStatusListener);
        controller.setFacePositionListener(mFacePositionListener);

        
        mContorollers.add(controller);
    };
    private void keepControl(boolean flag) {
    	if(flag){
    	    mHandler.removeCallbacks(keepNormalRunnable);
 	        mHandler.post(keepNormalRunnable);
    	}else{
//    		if(mActionState == ControlStatus.ACTION_LOSE_CONTROL){
//    			return;
//    		}
    		mHandler.removeCallbacks(keepNormalRunnable);
	        mHandler.post(keepNoControlRunnable);
    	}
    }
    private Runnable keepNoControlRunnable = new Runnable(){

        
        public void run() {
            gesturePopUp.setToNoControl();
        }
        
    };

    private void SetFaceIcon(boolean flag) {
    	if(flag){
 	        mHandler.post(keepFaceRunnable);
    	}else{
	        mHandler.post(keepNoFaceRunnable);
    	}
    }
    private Runnable keepNoFaceRunnable = new Runnable(){

        
        public void run() {
            gesturePopUp.setToNoFace();
        }
        
    };
    private Runnable keepFaceRunnable = new Runnable(){

        
        public void run() {
            gesturePopUp.setToFace();
        }
        
    };
    /**Set listener before addMode(GestureJNI gestureEngine, ActionHandleStrategy actionStrategy).
     * @param errorListener The listener will be called when the gesture engine get error.
     */
    public void setOnErrorListener(OnErrorListener errorListener) {
        mErrorListener = errorListener;
        for(EyeSightEngController controller : mContorollers) {
            controller.setOnErrorListener(mErrorListener);
        }
    }
    
    public void showGesturePopUp(){
        gesturePopUp.show();       
    }

    public void dismissGesturePopUp(){
        gesturePopUp.dismiss();
    }

    /**
     * Start handling the event dispatched by gesture engine. 
     * It means the current strategy will obtain control.
     */
    public void startControl() {
    	if(!GetNotificationDialogFlag() || !NotificationDialogisShowing()){
    		showGesturePopUp();
    	}
        if(mContorollers.size() > 0) {
            EyeSightEngController controller = mContorollers.get(currentMode);
            controller.startControl();
            isStart = true;
        }
    }
    
    public void stopControl() {
        if(!isStarted()) {
            return;
        }
        mContorollers.get(currentMode).stopControl();
        isStart = false;
    }
    
    public boolean isStarted(){
        return isStart;
    }
    
    public void removeMode(int index){
        EyeSightEngController controller = mContorollers.remove(index);
        boolean isStarted = false;
        if(controller.isStarted()) {
            isStarted = true;
        }
        controller.stopControl();
        controller.setOnActionChangeListener(null);
        if(currentMode > mContorollers.size() -1) {
            currentMode--;
        }
        if(isStarted){
            mContorollers.get(currentMode).startControl(); 
        }
    }
//加载详情介绍模式
    public void addShowEnableMode(GestureJNI gestureEngine, ActionHandleStrategy actionStrategy
    		                      ,OnControlStatusListener controlStatusListener
    		                      ,OnFacePositionListener facePositionListener
    		                      ,OnFaceStatusListener faceStatusListener) {
        EyeSightEngController controller = new EyeSightEngController(gestureEngine, actionStrategy);
        controller.setHandler(mHandler);
        Log.v("====LiZZ====", "addShowEnableMode");
        controller.setOnErrorListener(mErrorListener);
        //监听到的消息外部注册
        controller.setControlStatusListener(controlStatusListener);
        controller.setFaceStatusListener(faceStatusListener);
        controller.setFacePositionListener(facePositionListener);
        
        mContorollers.add(0,controller);
    };
    //不需要手势图标和脸部图标
    public void startNoPopControl() {
        if(mContorollers.size() > 0) {
            EyeSightEngController controller = mContorollers.get(currentMode);
            controller.startControl();
            isStart = true;
        }
    }
    
    void gotoNextMode() {
        if(currentMode < mContorollers.size() - 1) {
            setCurrentMode(currentMode + 1);
        }
    }
    
    void gotoPrevMode() {
        if(currentMode > 0) {
            setCurrentMode(currentMode - 1);
        }
    }
    
    public void setCurrentMode(int mode){
        if(mode < 0 || mode > mContorollers.size() - 1){
            throw new IndexOutOfBoundsException("request mode is " + mode + ". size is " + mContorollers.size());
        }
        if(isStarted()){
            mContorollers.get(currentMode).stopControl();
        }
        currentMode = mode;
        mContorollers.get(mode).startControl();
    }
    
    public int getCurrentMode(){
        return currentMode;
    }
    
    /** Do follow yourself:
     *  gestureEngine.StopGetProcessAction();
     */
    public void close(){
        stopControl();
        gesturePopUp.dismiss();
        gesturePopUp.notificationDismiss();
        
    }
    
    
    public void getNotificationDialog(Context context){

    	//详细演示按钮
        View.OnClickListener mShowClickListener = new View.OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(mContext, GestureDetailShow.class);
				mContext.startActivity(intent);
			}
		};
        gestureDialog = new ToastStyleDialog(context, new View.OnClickListener(){
        	
        	public void onClick(View v) {
        		// TODO Auto-generated method stub
        		Log.v("===LiZZ===","gestureDialog click");
       		
        		restartProcessAction();
    			
        		stopControl();
        		removeMode(0);//空策略
        		GestureDialogDismiss();
        		SetNotificationDialogFlag(false);
        		
        		startControl();
        	}
        }, mShowClickListener);
        
        //当按下esc键时，启动手势操作
        ((ToastStyleDialog) gestureDialog).setGestureCallback(new startGestureCallback(){
            
            public void onStartGesture() {
            	// TODO Auto-generated method stub
            	restartProcessAction();
        		stopControl();
        		removeMode(0); //空策略
        		SetNotificationDialogFlag(false);
            	mGestureNotificationShowFlag = false;
        		startControl();
            }
        });
        
        NotificationDialogShow();
    }
   //为了让第一次获得控制权时同时上报wave动作
    public void restartProcessAction(){
		GestureJNI gestureEngine = GestureJNI.getInstance();
		
		if(gestureEngine != null){
			gestureEngine.StopGetProcessAction();
			gestureEngine.InitGetProcessAction();
			gestureEngine.StartGetProcessAction();
		}
    }
    
    public void NotificationDialogShow(){
    	if(gestureDialog.isShowing()){
    		gestureDialog.dismiss();
    	}
    	gestureDialog.show();
    	SetNotificationDialogFlag(true);
    }
    public boolean NotificationDialogisShowing(){
    	return gestureDialog.isShowing();
    }
    
    public boolean GetNotificationDialogFlag(){
    	return mGestureNotificationShowFlag;
    }
    public void SetNotificationDialogFlag(boolean flag){
    	mGestureNotificationShowFlag = flag;
    }
    public void testLeft() {
    	mContorollers.get(currentMode).HandleActionOutput(ActionType.ACTION_LEFT, ControlStatus.ACTION_ON_CONTROL);
    }
    
    public void testRight(){
        mContorollers.get(currentMode).HandleActionOutput(ActionType.ACTION_RIGHT, ControlStatus.ACTION_ON_CONTROL);
    }
  
    public void testCover(){
        mContorollers.get(currentMode).HandleActionOutput(ActionType.ACTION_COVER, ControlStatus.ACTION_ON_CONTROL);
    }
   
    public void testWave(){
        mContorollers.get(currentMode).HandleActionOutput(ActionType.ACTION_WAVE, ControlStatus.ACTION_ON_CONTROL);
    }
    public void GestureDialogDismiss(){
    	if(gestureDialog != null && gestureDialog.isShowing()){
    		gestureDialog.dismiss();
    	}
    }
    class GestureNotificationStrategy implements ActionHandleStrategy{
       
        Handler mHandler;
       
        public GestureNotificationStrategy(Context context, Handler handler){
            mHandler = handler;
            View.OnClickListener mShowClickListener = new View.OnClickListener() {
    			
    			
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				Intent intent = new Intent(mContext, GestureDetailShow.class);
    				mContext.startActivity(intent);
    			}
    		};
            gestureDialog = new ToastStyleDialog(context, new View.OnClickListener(){
            	
            	public void onClick(View v) {
            		// TODO Auto-generated method stub
            		Log.v("===LiZZ===","gestureDialog click");
            		stopControl();
            		GestureDialogDismiss();
            		removeMode(0);
            		SetNotificationDialogFlag(false);
//            		mGestureNotificationShowFlag = false;
            		startControl();
            	}
            }, mShowClickListener);
            
            //当按下esc键时，启动手势操作
            ((ToastStyleDialog) gestureDialog).setGestureCallback(new startGestureCallback(){
	            
	            public void onStartGesture() {
	            	// TODO Auto-generated method stub
	            	SetNotificationDialogFlag(false);
//	            	mGestureNotificationShowFlag = false;
	            	stopControl();
	            	removeMode(0);
            		startControl();
	            }
            });
            
            ImageView imageView = new ImageView(context);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(params);
            gestureDialog.setContentView(imageView);
        }

        
        public int getCurrentPosition() {
            return 0;
        }

        
        public boolean handleCover(int position) {
            Log.v("aaaaa","cover");
            return false;
        }

        
        public boolean handleLeft() {
            Log.v("aaaaa","left");
            return false;
        }
        
        
        public boolean handleRight() {
            Log.v("aaaaa","right");
            return false;
        }

        
        public boolean handleWave() {
            Log.v("aaaaa","wave");
            //lzz modified
//            gesturePopUp.dismiss();
            gesturePopUp.notificationDismiss();
            mHandler.post(new Runnable() {
                
                
                public void run() {

//                	close();
//                    gestureDialog.show();
                    removeGestureNotificationControl();
//                    startControl();
                }
            });
//            mHandler.postDelayed(new Runnable(){
//
//                
//                public void run() {
//                    gestureDialog.dismiss();
//                    removeGestureNotificationControl();
//                    gesturePopUp.setToNormal();
//                    startControl();
//                }
//                
//            }, NOTIFICATION_DISMISS_INTERVAL);
            return false;
        }


        
        public void onGetControl() {
            Log.v("aaaaa","onGetControl");
        }

        
        public void onLoseControl() {
            Log.v("aaaaa","onLoseControl");
        }
        
       
    }
}
