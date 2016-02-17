package com.tcl.gesture;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.internal.R;

public class GesturePopUp {
    WindowManager windowManager;
    ImageView imageView;
    Animation fadeInAnim;
    Context mContext;
    boolean isShown;
    
    ImageView checkFaceView;
    LinearLayout mLinearlayout;
    ImageView imageNotification;
    boolean isNotificationShown;
    
    public GesturePopUp(Context context){
        mContext = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mLinearlayout = (LinearLayout)inflater.inflate(R.layout.gesture_image_view, null);
//        imageView = (ImageView) inflater.inflate(R.layout.gesture_test_image, null);
        fadeInAnim = AnimationUtils.loadAnimation(context, R.anim.fade_in_ges);
        
        imageView = (ImageView)mLinearlayout.findViewById(R.id.gesture_check_image);
        checkFaceView = (ImageView)mLinearlayout.findViewById(R.id.gesture_check_face);
        imageNotification = (ImageView) inflater.inflate(R.layout.gesture_test_image, null);

    }
    
    public void show(){
        if(!isShown) {
            WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            wmParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            wmParams.gravity = Gravity.TOP | Gravity.RIGHT;
            wmParams.format = PixelFormat.TRANSLUCENT;
            wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//            windowManager.addView(imageView, wmParams);
            windowManager.addView(mLinearlayout, wmParams);
            isShown = true;
        }
    }
    
    public void notificationShow(){
    	if(!isNotificationShown){
            WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            wmParams.format = PixelFormat.TRANSLUCENT;
            wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            windowManager.addView(imageNotification, wmParams);
            isNotificationShown = true;
    	}
    }
   
   public void notificationDismiss(){
	   if(isNotificationShown){
		   windowManager.removeView(imageNotification);
		   isNotificationShown = false;
	   }
   }
   public boolean getNotificationFlag(){
	   return isNotificationShown;
   }
    
    public void setToNormal(){
        setImageResource(R.drawable.recognition_normal);
    }
    
    public void setToLeft(){
        setImageResource(R.drawable.recognition_left);
    }

    public void setToRight(){
        setImageResource(R.drawable.recognition_right);
    }
    
    public void setToWave(){
        setImageResource(R.drawable.recognition_wave);
    }
    
    public void setToCover(){
        setImageResource(R.drawable.recognition_knock);
    }
    
    public void setToManual(){
//        setImageResource(R.drawable.gesture_notification);
    	imageNotification.setImageResource(R.drawable.gesture_notification);
    }
    //lizhenzhen
    public void setToNoFace(){
    	checkFaceView.startAnimation(fadeInAnim);
    	checkFaceView.setImageResource(R.drawable.recognition_face_miss);  	
    }
    
    public void setToFace(){
    	checkFaceView.startAnimation(fadeInAnim);
    	checkFaceView.setImageResource(R.drawable.recognition_face); 	
    }
    
    public void setToNoControl(){
        setImageResource(R.drawable.recognition_nocontroll);
    }
    
    private void setImageResource(int resourceId){
//        imageView.startAnimation(fadeInAnim);
        imageView.setImageResource(resourceId);
    }
    
    public void dismiss(){
        if(isShown){
//            windowManager.removeView(imageView);
        	windowManager.removeView(mLinearlayout);
            isShown = false;
        }
    }
    
}
