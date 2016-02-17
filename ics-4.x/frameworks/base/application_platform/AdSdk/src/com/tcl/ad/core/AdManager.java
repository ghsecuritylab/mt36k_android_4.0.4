/**
 * 
 */
package com.tcl.ad.core;

import java.lang.ref.WeakReference;

import com.tcl.ad.*;

import android.R.integer;
import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.11.12
 * 
 * @JDK version: 1.5
 * @brief: This class implements all core Ads processes.   
 * @version: v1.0
 *
 */
public class AdManager {

	private static final String TAG = "AdManager";
	
	//private AdWebView mAdWebView;
	private View mAdInnerView;
	private MediaPlayer mMediaPlayer;
	private AdObject mAdObject;
	private WeakReference<Activity> mActivityRef;
	private boolean mIsRefreshing;
	private boolean mIsReady;
	private AdRequest mAdRequest;
	//private Ad mAd;
	private AdListener mAdListener;
	private AdServerConnectionTask mAdTask;
	private String mAdUnitId;
	//private AdsShowTimer mTimer;
	//private AdsRefreshTimer mAdsRefreshTimer;
	private long mDelayTime;
	private AdsInformation mAdsInformation;
	private AdsInformation mAdsBackupInfromation;
	private HttpDownloader mHttpDownloader;
	private String mPageID;
	private int width;
	private int height;
	
	public enum AdClickType
	{
		NO_ACTION,
		ENLARGE_IMGAE,
		LAUNCH_APPLICATION,
		LAUNCH_BROWSER,
		INSTALL_APPLICATION,
		PLAY_VIDEO,
		PLAY_AUDIO
	}
	
	public AdManager(Activity activity, Ad ad, AdObject adObject, String adUnitId, String i, int w, int h) {
		mPageID = i;
		mAdObject = adObject;
		mActivityRef = new WeakReference<Activity>(activity);
		mIsRefreshing = false;
		mIsReady = false;
		//mAd = ad;
		mAdListener = null;
		mAdTask = null;
		mAdUnitId = adUnitId;
		//mTimer = new AdsShowTimer(this);
		//mAdsRefreshTimer = new AdsRefreshTimer(this);
		mDelayTime = 60 * 1000l;
		mAdsInformation = null;
		mAdsBackupInfromation = null;
		mMediaPlayer = null;
		mHttpDownloader = null;
		//Enable cookie sync
		//CookieSyncManager.createInstance(activity);
		width = w;
		height = h;
		//createInnerView();
	}
	
	public String getPageID() {
		return mPageID;
	}
	
	public AdsInformation getAdsInformation() {
		return mAdsInformation;
	}
	
	public final synchronized void setAdsInformation(AdsInformation adsInformation) {
		if (mAdsInformation != null) {
			mAdsBackupInfromation = mAdsInformation;
		}
		mAdsInformation = adsInformation;
	}
	
	private final synchronized void restoreAdsInformation() {
		if (mAdsBackupInfromation != null) {
			mAdsInformation = mAdsBackupInfromation;
			mAdsBackupInfromation = null;
		}
	}

	private final synchronized void createInnerView() {
		
		if(mIsRefreshing && mAdInnerView != null)
			return;
		
		Log.v(TAG, "createInnerView ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ " + mAdsInformation.getAdType().name());
		switch (mAdsInformation.getAdType()) {
		case AD_TYPE_TEXT:
			mAdInnerView = new AdTextView(getActivity());
			break;
		default:
			mAdInnerView = new AdImageView(getActivity(), width, height, this);
		}
		
		mAdInnerView.setVisibility(View.GONE);
	}
	
	public final synchronized View getInnerView() {
		return mAdInnerView;
	}
	
	public final synchronized boolean isReady() {
		return mIsReady;
	}
	
	public final synchronized Activity getActivity() {
		return mActivityRef.get();
	}
	
	public final synchronized void setAdListener(AdListener adListener) {
		mAdListener = adListener;
	}
	
//	public final synchronized void stopAdTask() {
//		disableRefreshAds();
//		if (mAdTask != null) {
//			mAdTask.cancel(true);
//			mAdTask.stopUncompeletedTask();
//			mAdTask = null;
//		}
//	}
	
	public final synchronized void stopHttpDownloader() {
		if (mHttpDownloader != null) {
			mHttpDownloader.cancel(true);
			mHttpDownloader = null;
		}
	}
	
	public final synchronized void destroyAdManager() {
		//mHandler.removeCallbacks(mTimer);
		mHandler.removeMessages(REFREAH_AD);
		stopHttpDownloader();

		cleanupAds();
	}
	
	public final synchronized void stopMediaPlayer() {
		AdsLog.debugLog("stopMediaPlayer = " + mMediaPlayer);
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
	
	private final synchronized void cleanupAds() {
		if (mAdsInformation != null) {
			AdUtil.removeImageCacheDirectory(mAdsInformation.getCacheFileSavedPath());
			AdUtil.removeWorldReadableFile(mAdsInformation.getWorldAccessibleFileSavedPath() + "/" + mAdsInformation.getCacheFileName());
			mAdsInformation.clearAllInformation();
			mAdsInformation = null;
		}
		cleanupBackupAdsInformation();
	}
	
	private final synchronized void cleanupBackupAdsInformation() {
		if (mAdsBackupInfromation != null) {
			AdUtil.removeWorldReadableFile(mAdsBackupInfromation.getWorldAccessibleFileSavedPath() + "/" + mAdsBackupInfromation.getCacheFileName());
			mAdsBackupInfromation = null;
		}
	}
	
	public final synchronized void dismissAdView() {
		//stopAdTask();
		if (mAdInnerView != null) {
			mAdInnerView.setVisibility(View.GONE);
			mAdInnerView = null;
		}
	}
	
	private void refresh() {
		if(mIsRefreshing) {
			mAdTask = new AdServerConnectionTask(this);
			mAdTask.execute(new AdRequest[] { mAdRequest });
			mIsReady = false;
		}
	}
	
	private static final int REFREAH_AD = 123;
	
	private Handler mHandler = new Handler() {
    	public void handleMessage(Message msg) {
			switch(msg.what) {
			
			case REFREAH_AD:
				refresh();
				//sendEmptyMessageDelayed(REFREAH_AD, mAdsInformation.getAdsShowInterval() * 1000);
				break;
			}
		}
    };
	
	
	public final synchronized void sendRequest(AdRequest adRequest) {
		mAdRequest = adRequest;
		mAdTask = new AdServerConnectionTask(this);
		mAdTask.execute(new AdRequest[] { adRequest });
		mIsReady = false;
	}

	
	final synchronized void notifyAdReceived() {
		createInnerView();
		mIsReady = true;

		cleanupBackupAdsInformation();
		showAds();
		downloadClickingResources();
	}
	
	public final synchronized void notifyAdFailedToReceiveAd(AdRequest.ErrorCode errorCode) {
		AdsLog.infoLog("onFailedToReceiveAd");
		// Release mAdTask? for uncomplete downloading
		restoreAdsInformation();
		mAdTask = null;
		if (mAdListener != null)
			mAdListener.onFailedToReceiveAd(errorCode);
	}
	
	private final synchronized void showAds() {
		Log.v(TAG, "showAds ~~~~~~~~~~~~~~~~~~~~~~~~");
		
		if(mAdsInformation == null)
			return;
		
		String ldptype = mAdsInformation.getLdpType();
		String ldp = mAdsInformation.getLdp();
		
		Log.v(TAG, "ldptype " + ldptype);
		Log.v(TAG, "ldp " + ldp);
		
		//Enlarge image
		if (mAdInnerView instanceof AdImageView) {
			
			if(mAdsInformation.getCacheFileSavedPath() == null || mAdsInformation.getAdsFileName() == null)
				return;
			
			String filePath = new StringBuilder().append(mAdsInformation.getCacheFileSavedPath()).append("/").append(mAdsInformation.getAdsFileName()).toString();
			
			Log.v(TAG, filePath);
			mAdInnerView.setVisibility(View.VISIBLE);
			AdImageView adImageView = (AdImageView)mAdInnerView;
			// Try to load AdsURL
			adImageView.showImage(filePath);
			uploadPVLog();
		} else if (mAdInnerView instanceof AdTextView) {
			
			if(mAdsInformation.getAdsText() == null || mAdsInformation.getAdsText().length() < 1)
				return;
			AdTextView adTextView = (AdTextView) mAdInnerView;
			adTextView.setVisibility(View.VISIBLE);
			adTextView.setGravity(Gravity.CENTER);
			adTextView.setText(mAdsInformation.getAdsText());

			uploadPVLog();
		} else {
			Log.v(TAG, "error adview type ~~~~~~~~~~~~~~~~~~~");
			return;
		}
		
		if (mAdListener != null && !mIsRefreshing) {
			//Log.v(TAG, "mAdListener called ~~~~~~~~~~~~~~~~~~`" );
			mAdListener.onReceiveAd();
		}
		
		if(mAdsInformation.getAdsShowInterval() > 10) {
			mIsRefreshing = true;
			mHandler.sendEmptyMessageDelayed(REFREAH_AD, mAdsInformation.getAdsShowInterval() * 1000);
			mAdListener.onRefreshAd();
		}
		else{
			mIsRefreshing = false;
		}
	}
	
	private final synchronized void downloadClickingResources() {
		
		if(mAdsInformation == null || mAdsInformation.getLdpType() == null || mAdsInformation.getLdpType().length() < 1)
			return;
		
		if (mAdsInformation.getLdpType().equals("I")) {
			stopHttpDownloader();
			mHttpDownloader = new HttpDownloader(this, false);
			mHttpDownloader.execute(new String[] {""});
		} 
		else {
			mAdsInformation.setHasLoaded(true);
		}
	}
	
	private final synchronized void uploadPVLog() {	
		(new AdsUploadPVLog(mActivityRef.get().getApplicationContext(), mAdsInformation.getSpotPvm(), mAdsInformation.getPageId(), true)).start();
		(new AdsUploadPVLog(mActivityRef.get().getApplicationContext(), mAdsInformation.getSpotPvtpm(), mAdsInformation.getPageId(), false)).start();
	}
	
	final String adUnitId() {
		return mAdUnitId;
	}
	
	final AdObject adObject() {
		return mAdObject;
	}
}
