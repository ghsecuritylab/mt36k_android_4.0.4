package com.mediatek.tv.service;

import android.app.Service;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.mediatek.tv.common.ConfigValue;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.service.ITVRemoteService;
import com.mediatek.tv.service.TVNative;
import com.mediatek.tv.common.TVCommon;

public class TVRemoteService extends Service {
    public static final String SERVICE_NAME = "TVRemoteService";
    private static final String TAG = "[J]TVRemoteService";
    private Context mContext;
    private Thread initSelfThread = null;
    private boolean mInitFinished = false;

	private final BroadcastReceiver mReceiver = new ConfigServiceBroadcastReceiver();

    // static {
    // Logger.i(TAG, "Load libcom_mediatek_tv_jni.so start !");
    // System.loadLibrary("com_mediatek_tv_jni");
    // Logger.i(TAG, "Load libcom_mediatek_tv_jni.so end !");
    // }

    @Override
    public void onCreate() {
        Logger.i(TAG, "onCreate");
        // Toast.makeText(this, "TVRemoteService createed" +
        // TVRemoteService.this, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        Logger.i(TAG, "onDestroy");
        // Toast.makeText(this, "TVRemoteService Destroy" +
        // TVRemoteService.this, Toast.LENGTH_SHORT).show();
        try {
            binder.unregisterAll();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public TVRemoteService() {
        super();
    }

    public TVRemoteService(Context context) {
        mContext = context;
        initSelfThread = new Thread() {
            public void run() {
        		//Enable log in this stage
        		TVCommon.debugLevel = 7;
        		
        		Logger.i(TAG, "TVRemoteService initSelfThread started...");
        		
        		//Init JNI
        		System.loadLibrary("com_mediatek_tv_jni");

        		//Set context to TVCallback and TVRemoteServiceHandler
        		TVCallBack.setContext(mContext);
        		TVRemoteServiceHandler.setContext(mContext);
        		
        		Logger.i(TAG, "TVRemoteService initSelfThread finished...");
        		
        		//Disable log again
        		TVCommon.debugLevel = 0;
        		
        		mInitFinished = true;
                
        		//keep alive due to RPC limitation
        		while (mInitFinished == true) {
                try {
                	    Thread.sleep(6000000);
                    } catch (Throwable e) {
                    	Logger.e(TAG, "thread sleep error");
                    }
        		}
            }
        };
        initSelfThread.start();
    }

    public void systemReady(){
		//Enable log in this stage
		TVCommon.debugLevel = 7;
		while (mInitFinished == false) {
			try {
				Logger.d(TAG, "Waiting for initSelfThread to be finished");
                Thread.sleep(10);
            } catch (Throwable e) {
            	Logger.e(TAG, "thread sleep error");
            }
		}
		//Disable log again
		TVCommon.debugLevel = 0;

        IntentFilter intentFilter1 = new IntentFilter(AudioManager.VOLUME_CHANGED_ACTION);
        mContext.registerReceiver(mReceiver, intentFilter1);
        IntentFilter intentFilter2 = new IntentFilter(AudioManager.MUTE_CHANGED_ACTION);
        mContext.registerReceiver(mReceiver, intentFilter2);
    }
	
    private final ITVRemoteService.Stub binder = new TVRemoteServiceHandler(new TVCallBack());

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onbind " + intent);
        return binder;
    }

    public Context getContext() {
        return mContext;
    }

    public ITVRemoteService.Stub getBinder() {
        return binder;
    }


	private class ConfigServiceBroadcastReceiver extends BroadcastReceiver {
    
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            
            if(action.equals(AudioManager.VOLUME_CHANGED_ACTION) && mContext != null){
                    int streamType = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_TYPE,0);
                    int val =  intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_VALUE,20);
                    Logger.d(TAG,"volume val = " + val);
                    Logger.d(TAG,"streamType = " + streamType);

                    if(streamType == AudioManager.STREAM_MUSIC  || streamType == AudioManager.STREAM_RING) {
                        ConfigValue cfgVal = new ConfigValue();
                        cfgVal.setIntValue(val);
                   
                        try {
							TVNative.setCfg_native(0xFF, ConfigType.CFG_VOLUME, cfgVal);
							TVNative.updateCfg_native(ConfigType.CFG_VOLUME);
							/*
				            final AudioManager mAudManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
				            
				            mAudManager.setStreamVolume(AudioManager.STREAM_MUSIC,cfgVal.getIntValue(),0);*/
				            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }				 
            }
            if(action.equals(AudioManager.MUTE_CHANGED_ACTION) && mContext != null){
                int streamType = intent.getIntExtra(AudioManager.EXTRA_MUTE_STREAM_TYPE,0);
                boolean val =  intent.getBooleanExtra(AudioManager.EXTRA_MUTE_STREAM_VALUE,false);
                Logger.d(TAG,"mute val = " + val);
                Logger.d(TAG,"streamType = " + streamType);

                if(streamType == AudioManager.STREAM_MUSIC  || streamType == AudioManager.STREAM_RING) {
                    try {
                    	TVNative.setMute_native(val);		            
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }				 
        }
        }
    }

}
