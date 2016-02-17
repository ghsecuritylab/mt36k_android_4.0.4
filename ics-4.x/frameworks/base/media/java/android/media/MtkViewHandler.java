package android.media;

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

//import android.media.AudioManager;

public class MtkViewHandler extends Handler {

    private final static String TAG = "MtkViewHandler";
    private View muteView;

    private final int MUTE_STATUS = 1;
    private final int UNMUTE_STATUS  = 2;

    private Context mContext = null;
    private static HandlerThread mHandlerThread;
    private static MtkViewHandler mMtkViewHandler = null;

    boolean isMute=false;


    private MtkViewHandler(Context context) {

        super(mHandlerThread.getLooper());
        this.mContext = context;
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
                case MUTE_STATUS: 
                    Log.d(TAG, "[ESTALENT] MtkViewHandler handlerMessage VOLUME_MUTE");
                  
		if(!isMute){
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

			isMute=true;

                    } catch(WindowManager.BadTokenException bte) {
                       //Log.d(TAG, "bte:\n");
                        bte.printStackTrace();
                    }
		}
                    break;
                
                case UNMUTE_STATUS:

                    try {
                        Log.d(TAG, "[ESTALENT] MtkViewHandler handlerMessage VOLUME_UNMUTE");
                        
			if(isMute)
			wm.removeView(muteView);
                    } catch (Exception e) {
                        //Log.d(TAG, "REMOVE MUTE FAILED.");
                        e.printStackTrace();
                    }
		
			isMute = false;

                    break;

/*                case VOLUME_MUTE_INIT: {
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
 */
                default:
                    Log.d(TAG, "[ESTALENT] Unknown command!");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean showMute(boolean stat) throws Exception {
       
        Log.d(TAG, "[ESTALENT] stat: " + stat);

        if (stat) {
            Log.i(TAG, "[ESTALENT] Mute");
            Message msg = mMtkViewHandler.obtainMessage();
            msg.what = MUTE_STATUS;
            msg.sendToTarget();            
        } else {
            Log.d(TAG, "[ESTALENT] UnMute");
            Message msg = mMtkViewHandler.obtainMessage();
            msg.what = UNMUTE_STATUS;
            msg.sendToTarget();
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


