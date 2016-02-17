
package com.android.internal.policy.impl;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.media.AudioManager;

public class MtkViewHandler extends Handler {

    private final static String TAG = "MtkViewHandler";
    private View muteView;

    private final int VOLUME_MUTE = 1;
    private final int VOLUME_UP   = 2;
    private final int VOLUME_DOWN = 3;
    private final int VOLUME_MUTE_INIT = 4;

    private boolean muteFlag = false;
    private Context mContext = null;
    private static HandlerThread mHandlerThread;
    private static MtkViewHandler mMtkViewHandler = null;

    private int mcur = 0;

    private final int INIT_KEYCODE = 1208;

    private MtkViewHandler(Context context) {

        super(mHandlerThread.getLooper());
        this.mContext = context;
    }

    private int getCurVol() {
         AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
         return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    } 

    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        try {
            WindowManager wm = (WindowManager) mContext
                                 .getSystemService(Context.WINDOW_SERVICE);
            if (wm == null) {
                Log.d(TAG, "[ESTALENT] wm is null");
                return ;
            }

            switch (msg.what) {
                case VOLUME_UP: 
                    Log.d(TAG, "[ESTALENT] MtkViewHandler handlerMessage VOLUME_UP");

                    int cur = getCurVol();
                    if (cur >= 100) {
                        Log.d(TAG, "[ESTALENT] volume >= 100");
                    } else if (muteFlag) {
                        muteFlag = false;
                        try {
                            wm.removeView(muteView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                
                case VOLUME_DOWN:
                    Log.d(TAG, "[ESTALENT] MtkViewHandler handlerMessage VOLUME_DOWN");
                    
                    Log.d(TAG, "[ESTALENT] cur: " + mcur);
                    if (mcur <= 1) {
                        Log.d(TAG, "[ESTALENT] volume <= 0");
                        if (!muteFlag) {
                            muteFlag = true;
                            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
                            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            params.format = PixelFormat.RGBA_8888;
                            params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                            LayoutInflater inflater = (LayoutInflater) mContext
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            muteView = inflater.inflate(com.android.internal.R.layout.tcl_mute, null);
                            try {
                                wm.addView(muteView, params);
                            } catch(WindowManager.BadTokenException bte) {
                                //Log.d(TAG, "bte:\n");
                                bte.printStackTrace();
                            }
                        } 
                    }                  
                    break;
                
                case VOLUME_MUTE: {
                    Log.d(TAG, "[ESTALENT] MtkViewHandler handlerMessage VOLUME_MUTE");

                    if (!muteFlag) {    
                        muteFlag = true;
                        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
                        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        params.format = PixelFormat.RGBA_8888;
                        params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                        LayoutInflater inflater = (LayoutInflater) mContext
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        muteView = inflater.inflate(com.android.internal.R.layout.tcl_mute, null);
                        try {
                            wm.addView(muteView, params);
                        } catch(WindowManager.BadTokenException bte) {
                            //Log.d(TAG, "bte:\n");
                            bte.printStackTrace();
                        }

                    } else {
                        muteFlag = false;

                        try {
                            Log.d(TAG, "[ESTALENT] removeView");
                            wm.removeView(muteView);
                        } catch (Exception e) {
                            //Log.d(TAG, "REMOVE MUTE FAILED.");
                            e.printStackTrace();
                        }
                    }
                    break;
                }

                case VOLUME_MUTE_INIT: {
                    Log.d(TAG, "[ESTALENT] MtkViewHandler handlerMessage VOLUME_MUTE_INIT");

                    muteFlag = true;
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
                    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    params.format = PixelFormat.RGBA_8888;
                    params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                    LayoutInflater inflater = (LayoutInflater) mContext
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    muteView = inflater.inflate(com.android.internal.R.layout.tcl_mute, null);
                    try {
                        wm.addView(muteView, params);
                    } catch(WindowManager.BadTokenException bte) {
                            //Log.d(TAG, "bte:\n");
                            bte.printStackTrace();
                    }

                }
                break;
 
                default:
                    Log.d(TAG, "[ESTALENT] Unknown command!");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean onPreKeyDown(KeyEvent event) throws Exception {
       
        final int keyCode = event.getKeyCode(); 
        Log.d(TAG, "[ESTALENT] onPreKeyDown keyCode: " + keyCode);

        if (keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            //Log.i(TAG, "[ESTALENT] volume mute");
            Message msg = mMtkViewHandler.obtainMessage();
            msg.what = VOLUME_MUTE;
            msg.sendToTarget();            
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                   keyCode == KeyEvent.KEYCODE_TCL_FASTMOVE_RIGHT_1 ||
                   keyCode == KeyEvent.KEYCODE_TCL_FASTMOVE_RIGHT_2 ||
                   keyCode == KeyEvent.KEYCODE_TCL_FASTMOVE_RIGHT_3 ||
                   keyCode == KeyEvent.KEYCODE_TCL_FASTMOVE_RIGHT_4 ||
                   keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
    	    //Log.i(TAG, "[ESTALENT] volume up");
            Message msg = mMtkViewHandler.obtainMessage();
            msg.what = VOLUME_UP;
            msg.sendToTarget();
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                   keyCode == KeyEvent.KEYCODE_TCL_FASTMOVE_LEFT_1  ||
                   keyCode == KeyEvent.KEYCODE_TCL_FASTMOVE_LEFT_2  ||
                   keyCode == KeyEvent.KEYCODE_TCL_FASTMOVE_LEFT_3  ||
                   keyCode == KeyEvent.KEYCODE_TCL_FASTMOVE_LEFT_4  ||
                   keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
    	    //Log.i(TAG, "[ESTALENT] volume up");
             
            mcur = getCurVol();
            Message msg = mMtkViewHandler.obtainMessage();
            msg.what = VOLUME_DOWN;
            msg.sendToTarget();
        } else if (keyCode == INIT_KEYCODE) {
            Message msg = mMtkViewHandler.obtainMessage();
            msg.what = VOLUME_MUTE_INIT;
            msg.sendToTarget();
        } else {
            Log.d(TAG, "[ESTALENT] Invalid KeyCode");
        }

        return true;
    }

    public static MtkViewHandler getInstance(Context context) {
        //Log.i(TAG, "[ESTALENT] MtkViewHandler genInstance");
            
        if (mMtkViewHandler == null) {        
            Log.d(TAG, "mMtkViewHandler is null, new instance.");  
            mHandlerThread = new HandlerThread("mtk_view_handler");
            mHandlerThread.start();
            mMtkViewHandler = new MtkViewHandler(context);
        }
        return mMtkViewHandler;
    } 

    private MtkViewHandler() {}

}


