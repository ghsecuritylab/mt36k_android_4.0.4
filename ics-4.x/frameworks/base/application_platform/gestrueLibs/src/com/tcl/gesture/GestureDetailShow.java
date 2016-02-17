package com.tcl.gesture;

import com.tcl.GestureJNIAPI.ControlStatus;
import com.tcl.GestureJNIAPI.FaceDetectStatus;
import com.tcl.GestureJNIAPI.FacePosStatus;
import com.tcl.GestureJNIAPI.GestureJNI;
import com.tcl.GestureJNIAPI.RunTimeError;
import com.tcl.gesture.EyeSightEngController.ActionHandleStrategy;
import com.tcl.gesture.EyeSightEngController.OnControlStatusListener;
import com.tcl.gesture.EyeSightEngController.OnErrorListener;
import com.tcl.gesture.EyeSightEngController.OnFacePositionListener;
import com.tcl.gesture.EyeSightEngController.OnFaceStatusListener;

import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.R;

public class GestureDetailShow extends Activity{
	
	public static boolean IS_GESTURE_RECOGNITION_ENABLE;
    private GestureEngineMode mEngineMode;
    private GestureJNI gestureEngine;
    boolean firstIn = true;
    Handler mHandler;
    Animation fadeInAnim;
    
    LinearLayout mLinearLayout;
    
    private TextView mTextViewTitle;    //动态标题
    private TextView mTextViewPresent;  //动作介绍
    private TextView mTextViewPage;      //右下角的页码
    
    
    private ImageView mImageView_face;  //人脸识别的图标
    private ImageView mImageView_controll;    //获得控制权的图标
    private ImageView mImageView_ok;      //获得控制权后显示的Ok图标
    private ImageView mImageView_action;  //显示动作的图标
    
    AnimationDrawable rocketAnimation;//动作动画
    
    Context mContext;
    WindowManager windowManager;

    boolean getControllFlag;
    boolean leftFlag;
    boolean rightFlag;
    boolean coverFlag;
    boolean waveFlag;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//系统壁纸
        final WallpaperManager mWPManager = WallpaperManager.getInstance(this);
        if(mWPManager != null){
	        final Drawable bgDrawable = mWPManager.getDrawable();
	        if(bgDrawable != null){
	        	this.getWindow().setBackgroundDrawable(bgDrawable);
	        }
        }
                    
		setContentView(R.layout.gesture_follow_me);
		
		mLinearLayout = (LinearLayout)findViewById(R.id.fillcontent);
		
	    mTextViewTitle = (TextView)findViewById(R.id.showtitle);    //动态标题
	    mTextViewPresent = (TextView)findViewById(R.id.show_present);  //动作介绍
	    mTextViewPage = (TextView)findViewById(R.id.show_page);      //右下角的页码
	    
	    
	    mImageView_face = (ImageView)findViewById(R.id.show_face);  //人脸识别的图标
	    mImageView_controll = (ImageView)findViewById(R.id.show_controll);    //获得控制权的图标
	    mImageView_ok = (ImageView)findViewById(R.id.show_ok);     //获得控制权后显示的Ok图标
	    mImageView_action = (ImageView)findViewById(R.id.show_gesture_action);   //显示动作的图标
		
	    //开始不可见
	    mImageView_ok.setVisibility(View.INVISIBLE);
		mContext = this;
		
		fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_ges);
		
//        if(IS_GESTURE_RECOGNITION_ENABLE) {
		{
			IS_GESTURE_RECOGNITION_ENABLE = true;
        	mHandler = new Handler();      	
            mEngineMode = new GestureEngineMode(this, mHandler);

            gestureEngine = GestureJNI.getInstance();
            mEngineMode.restartProcessAction();

            mEngineMode.setOnErrorListener(new OnErrorListener(){
                
               	public void onError(RunTimeError errorCode) {
               	// TODO Auto-generated method stub
                	Log.v("GestureDetailShow", "mEngineMode.setOnErrorListener" + errorCode);
               		if(errorCode != RunTimeError.STATE_OK){
             			
               	    	if(mEngineMode != null){
               	    		mEngineMode.close();
               	    	}
               	    	mHandler.post(new Runnable() {							
							
							public void run() {
								// TODO Auto-generated method stub
								mEngineMode.GestureDialogDismiss();								
								showCustomToast(mContext, getString(R.string.ges_check_camera_label),0);

							}
						});
               	    	mHandler.postDelayed(new Runnable() {							
							
							public void run() {
								// TODO Auto-generated method stub
								finish();
							}
						},2000);
               	    	gestureEngine.StopGetProcessAction();             	    	
               	    	IS_GESTURE_RECOGNITION_ENABLE = false;               	    	
               		}
               	}
            });

            //识别脸和获得控制权
            mEngineMode.addShowEnableMode(gestureEngine, new gestureDetailShowEnableStrategy()
                                          , controlStatusListener, facePositionListener, faceStatusListener);
            
            mEngineMode.addMode(gestureEngine, new ViewsActionHandleStrategy());
            mEngineMode.startNoPopControl();
        }
	}
	
	
	private Runnable keepNoFaceRunnable = new Runnable() {
		
		
		public void run() {
			// TODO Auto-generated method stub
			setImageResource(mImageView_face, R.drawable.recognition_face_miss);
		}
	};
	
	private Runnable KeepFaceRunnable = new Runnable() {
		
		
		public void run() {
			// TODO Auto-generated method stub
			setImageResource(mImageView_face, R.drawable.recognition_face);
		}
	};
	
	private Runnable keepNocontrollRunnable = new Runnable() {
		
		
		public void run() {
			// TODO Auto-generated method stub
			setImageResource(mImageView_controll, R.drawable.recognition_nocontroll);
		}
	};
	private Runnable keepControllRunnable = new Runnable() {
		
		
		public void run() {
			// TODO Auto-generated method stub
			setImageResource(mImageView_controll, R.drawable.recognition_normal);
		}
	};
	
	private void setFaceIcon(boolean flag){
		if(flag){
			mHandler.post(KeepFaceRunnable);
		}else{
			mHandler.post(keepNoFaceRunnable);
		}
	}
	
	private void setControll(boolean flag){
		if(flag){
			mHandler.post(keepControllRunnable);
		}else{
			mHandler.post(keepNocontrollRunnable);
		}
	}
	
    OnControlStatusListener controlStatusListener = new OnControlStatusListener(){
    	
    	public void onControlStatus(ControlStatus actionState) {
    		// TODO Auto-generated method stub
        	if(actionState == ControlStatus.ACTION_LOSE_CONTROL){
        		setControll(false);
        	}else if(actionState == ControlStatus.ACTION_ON_CONTROL){
        		setControll(true);
        	}
    	}
    };
    OnFacePositionListener facePositionListener = new OnFacePositionListener(){
    	
    	public void onFacePosition(FacePosStatus facePosState) {
    		// TODO Auto-generated method stub
     		if(facePosState == FacePosStatus.FACE_POS_OK){
     			setFaceIcon(true);
    	    }else if(facePosState == FacePosStatus.FACE_POS_DEVIATED){
    	    	setFaceIcon(false);
    		}
    	
    	}
    };
    OnFaceStatusListener faceStatusListener = new OnFaceStatusListener(){
    	
    	public void onFaceStatus(FaceDetectStatus faceState) {
    		// TODO Auto-generated method stub
    		if(faceState == FaceDetectStatus.FACE_MISSED){
    			setControll(false);
    			setFaceIcon(false);
    		}
    	}
    };
    
    private void setImageResource(ImageView imageView, int resourceId){
//        imageView.startAnimation(fadeInAnim);
        imageView.setImageResource(resourceId);

    }
       
    private void setAniamtionResource(ImageView imageView, int resourceId){
    	
//      rocketAnimation.stop();
      imageView.setImageResource(resourceId);

      rocketAnimation = (AnimationDrawable)mImageView_action.getDrawable();
      rocketAnimation.start();

  }
    private static void showCustomToast(Context context, String mToastString, int mLong) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();

        View layout = inflater.inflate(R.layout.gesture_message_box, null);
        TextView text = (TextView) layout.findViewById(R.id.Showtoasttext);
        text.setText(mToastString);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        if (mLong == 0) {
            toast.setDuration(Toast.LENGTH_SHORT);
        }else {
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.setView(layout);
        toast.show();
    }
    
    protected void onResume(){
        super.onResume();
        if(IS_GESTURE_RECOGNITION_ENABLE && getControllFlag) {
            mEngineMode.startControl();           
        }
    }
	
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.v("+++lizz+++", "DetailShow onPause");
        if(IS_GESTURE_RECOGNITION_ENABLE) {
//        	restartProcessAction();
            mEngineMode.close();
        }
	}

	//提示操作Ok的图标
	private void showImageOK(){
		mHandler.post(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				rocketAnimation.stop();
				setImageResource(mImageView_action, R.drawable.gesture_detailshow_ok);
			}
		});
	}
	private void setTextView(int title, int present, int page){
		mTextViewTitle.setText(title);
		mTextViewPresent.setText(present);
		mTextViewPage.setText(page);
	}
	
	class ViewsActionHandleStrategy implements ActionHandleStrategy{
		Dialog showFinishDialog;
		public ViewsActionHandleStrategy() {
			// TODO Auto-generated constructor stub
			showFinishDialog = new MessageDialog(mContext,getString(R.string.detail_show_finish));
		}
		
		
		public int getCurrentPosition() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		public boolean handleLeft() {
			// TODO Auto-generated method stub
			if(leftFlag){
				showImageOK();
				mHandler.postDelayed(new Runnable() {				
					
					public void run() {
						// TODO Auto-generated method stub
						leftFlag = false;
						rightFlag = true;
						setTextView(R.string.show_right_title,R.string.detail_right_show, R.string.show_right_page);

//						setImageResource(mImageView_action, R.drawable.gesture_detailshow_right);
						setAniamtionResource(mImageView_action, R.anim.gesture_right);
					}
				},1000);
			}
			return false;
		}
		
		public boolean handleCover(int position) {
			// TODO Auto-generated method stub
			if(coverFlag){
				showImageOK();
				mHandler.postDelayed(new Runnable() {				
					
					public void run() {
						// TODO Auto-generated method stub
						coverFlag = false;
						waveFlag = true;
						setTextView(R.string.show_wave_title,R.string.detail_wave_show, R.string.show_wave_page);
//						setImageResource(mImageView_action, R.drawable.gesture_detailshow_wave);
						setAniamtionResource(mImageView_action, R.anim.gesture_wave);
					}
				},1000);	
			}
			return false;
		}
		
		public boolean handleRight() {
			// TODO Auto-generated method stub
			if(rightFlag){
				showImageOK();
				mHandler.postDelayed(new Runnable() {				
					
					public void run() {
						// TODO Auto-generated method stub
						rightFlag = false;
						coverFlag = true;
						setTextView(R.string.show_cover_title,R.string.detail_cover_show, R.string.show_cover_page);
//						setImageResource(mImageView_action, R.drawable.gesture_detailshow_cover);
						setAniamtionResource(mImageView_action, R.anim.gesture_cover);
					}
				},1000);				
			}
			return false;
		}
		
		public boolean handleWave() {
			// TODO Auto-generated method stub
			if(waveFlag){
				showImageOK();

		        mHandler.postDelayed(new Runnable(){
			        
		            
		            public void run() {
		            	if(showFinishDialog.isShowing()){
		            		showFinishDialog.dismiss();
		            	}
		            	mEngineMode.close();
		            	mLinearLayout.setVisibility(View.INVISIBLE);
		            	showFinishDialog.show();
		           }		                        
		        }, 1000);
		        
	            mHandler.postDelayed(new Runnable(){
		        
		            
		            public void run() {
			            if(showFinishDialog.isShowing()){
			    		    showFinishDialog.dismiss();
			    		}
			            mEngineMode.restartProcessAction();
			            mEngineMode.GestureDialogDismiss();
			            finish();
		           }		                        
		        }, 4000);
				waveFlag = false;
				
			}
			return false;
		}
		
		public void onGetControl() {
			// TODO Auto-generated method stub
			
		}
		
		public void onLoseControl() {
			// TODO Auto-generated method stub
			
		}
	}
	//第一个策略
	class gestureDetailShowEnableStrategy extends GestureNotificationShowStrategy{
		
		public boolean handleWave() {
			// TODO Auto-generated method stub
			getControllFlag = true;
			mImageView_ok.setVisibility(View.VISIBLE);
			
			if(mEngineMode != null){
				mEngineMode.stopControl();
				mEngineMode.removeMode(0);
			}
			mHandler.postDelayed(new Runnable() {				
				public void run() {
					// TODO Auto-generated method stub
					if(mEngineMode != null){
						mEngineMode.startControl();
					}
					leftFlag = true;
					mImageView_controll.setVisibility(View.INVISIBLE);
					mImageView_face.setVisibility(View.INVISIBLE);
					mTextViewPresent.setLineSpacing(0.0f,1.5f);
					mTextViewPresent.setPadding(10, 0, 40, 0);
					mTextViewPresent.setTextSize(24.0f);
					setTextView(R.string.show_left_title,R.string.detail_left_show, R.string.show_left_page);
//					setImageResource(mImageView_action, R.drawable.gesture_detailshow_left);
					setAniamtionResource(mImageView_action,R.anim.gesture_left);
					
				}
			},1000);
			
			return super.handleWave();
		}
	}
}
