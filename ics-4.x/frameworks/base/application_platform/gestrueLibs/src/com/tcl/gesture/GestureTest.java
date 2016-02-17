package com.tcl.gesture;

import com.tcl.GestureJNIAPI.GestureJNI;
import com.tcl.gesture.EyeSightEngController.ActionHandleStrategy;


import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.internal.R;

//Two layer in this case. 1st is made up by some buttons, 2nd is a list.
//This demo will show you how to use the gesture lib.
public class GestureTest extends Activity{
    ListView listView;
    View[] views;
    ViewGroup buttonContainer;
    Handler mHandler;
    
    public static final boolean IS_GESTURE_RECOGNITION_ENABLE = true;
    private GestureEngineMode mEngineMode;
    private GestureJNI gestureEngine;
    boolean firstIn = true;
    
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_test_layout);
        listView = (ListView) findViewById(R.id.gesture_test_list);
        String[] strs = {"aaaaaaaaaaaaa","bbbbbbbbbbbbb","cccccccccccccc","dddddddddddd","eeeeeeeeeeeee"};
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, strs));
        views = new View[3];
        views[0] = findViewById(R.id.gesture_test_btn_enter);
        views[1] = findViewById(R.id.gesture_test_btn_normal);
        views[2] = findViewById(R.id.gesture_test_btn_quit);
        buttonContainer = (ViewGroup) findViewById(R.id.gesture_test_btn_container);
        mHandler = new Handler();
        if(IS_GESTURE_RECOGNITION_ENABLE) {

            mEngineMode = new GestureEngineMode(this, mHandler);
//            eyeSightEngine = EyeSightAPI.getInstance();
//            eyeSightEngine.InitEyeSightEngine(this.getApplicationContext());
            gestureEngine = GestureJNI.getInstance();
            gestureEngine.InitGetProcessAction();
            gestureEngine.StartGetProcessAction();
            MyButtonStrategy actionStrategy1 = new MyButtonStrategy(views);
            MyListStrategy actionStrategy2 = new MyListStrategy(listView);
            mEngineMode.addMode(gestureEngine, actionStrategy1);
            mEngineMode.addMode(gestureEngine, actionStrategy2);
           //eyeSightEngine.StartEyeSightEngine(CameraType.BACK_CAMERA);
       
        }
    }
    
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT);
    }
    
    protected void onResume(){
        super.onResume();
        if(IS_GESTURE_RECOGNITION_ENABLE) {
//            eyeSightEngine.StartEyeSightEngine(CameraType.BACK_CAMERA);
            if(firstIn){
                firstIn = false;
                mEngineMode.enableGestureManual(gestureEngine);
            }
            mEngineMode.startControl();
        }
//        testOurStrategy();
    }

    private void testOurStrategy() {
        mHandler.postDelayed(new Runnable(){

            
            public void run() {
                Log.v("start test", "wave");
                mEngineMode.testWave();
            }}, 5000);
        mHandler.postDelayed(new Runnable(){

            
            public void run() {
                Log.v("start test", "cover");
                mEngineMode.testCover();
            }}, 13000);
        mHandler.postDelayed(new Runnable(){

            
            public void run() {
                Log.v("start test", "right");
                mEngineMode.testRight();
            }}, 15000);
        mHandler.postDelayed(new Runnable(){

            
            public void run() {
                Log.v("start test", "right");
                mEngineMode.testRight();
            }}, 16000);
        mHandler.postDelayed(new Runnable(){

            
            public void run() {
                Log.v("start test", "left");
                mEngineMode.testLeft();
            }}, 17000);
        mHandler.postDelayed(new Runnable(){

            
            public void run() {
                Log.v("start test", "Wave");
                mEngineMode.testWave();
            }}, 18000);
        mHandler.postDelayed(new Runnable(){

            
            public void run() {
                Log.v("start test", "Wave");
                mEngineMode.testWave();
            }}, 19000);
    }
    
    protected void onStop(){
        super.onStop();
        if(IS_GESTURE_RECOGNITION_ENABLE) {
            mEngineMode.stopControl();
        }
    }
    
    protected void onDestroy(){
        super.onDestroy();
        if(mEngineMode != null){
            mEngineMode.close();
        }
//        eyeSightEngine.StopEyeSightEngine(); 
//        eyeSightEngine.DestroyEngine();
        gestureEngine.StopGetProcessAction();
        
    }
    
    public class MyButtonStrategy extends ViewsActionHandleStrategy{
        View[] mViews;

        public MyButtonStrategy(View[] views) {
            super(views);
            mViews = views;
        }

        
        public boolean handleCover(int position) {
            switch(position){
                case 0:
                    //OK, in this case we should go to next layer.
                    return true;
                case 1:
                    //ignore it.
                    return false;
                case 2:
//                    handleWave();
                    return false;
            }
            return false;
        }

        
        public void onGetControl() {
            //Help user easy to find the focus area.
            Log.v("strategy1","getControl");
            buttonContainer.setBackgroundColor(Color.YELLOW);
            mViews[0].requestFocus();
        }

        
        public void onLoseControl() {
            //lose the focus of this layer.
            Log.v("strategy1","loseControl");
            buttonContainer.setBackgroundColor(0x000000);
        }
        
        
        public boolean handleWave() {
            //we are in the top layer, so we should finish app.
            Log.v("strategy1","wave");
            finish();
            return false;
        }
    }
    
    public class MyListStrategy implements ActionHandleStrategy{
        ListView mList;
        int currentSelected = 0;
        
        public MyListStrategy(ListView list){
            mList = list;
        }
        
        
        public int getCurrentPosition() {
            // TODO Auto-generated method stub
            return mList.getSelectedItemPosition();
        }

        
        public boolean handleCover(int position) {
            // TODO Auto-generated method stub
            showToast("select: " + position);
            return false;
        }

        
        public boolean handleLeft() {
            currentSelected--;
            if(currentSelected < 0){
                currentSelected = 0;
            }
            mList.setSelection(currentSelected);
            return true;
        }

        
        public boolean handleRight() {
            currentSelected++;
            if(currentSelected > mList.getCount() - 1){
                currentSelected = mList.getCount() - 1;
            }
            mList.setSelection(currentSelected);
            return true;
        }

        
        public boolean handleWave() {
          //return true, so we can get back to previous layer.
            return true;
        }

        
        public void onGetControl() {
            mList.requestFocus();
        }

        
        public void onLoseControl() {
            
        }
        
    }
    
    
}
