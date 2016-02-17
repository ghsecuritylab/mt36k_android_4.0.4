/**
 * 
 */
package com.tcl.ad;



import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import com.tcl.ad.AdRequest.ErrorCode;
import com.tcl.ad.core.AdImageView;
import com.tcl.ad.core.AdManager;
import com.tcl.ad.core.AdUtil;
import com.tcl.ad.core.AdsInformation;
import com.tcl.ad.core.AdsLog;
import com.tcl.ad.core.AdsUploadClickLog;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.10.12
 * 
 * @JDK version: 1.5
 * @brief: The View that displays the ads.   
 * @version: v1.0
 *
 */
public class AdView extends RelativeLayout implements Ad {
	
	private Activity mContext;
	private AdManager mAdManager;
	private AdsInformation mAdsInformation;
	private String mPageID;
	private AdObject mAdObject;
	private String mAdUnitId;
	private View mView;
	private int mWidth;
	private int mHeight;
	
	private AdListener mAppAdListener;
	
	private LinearLayout parentView;
	
	private int mDefaultPicId;
	
	private static final String TAG = "AdView";
	
	public AdView(Activity activity, AdObject adObject, String adUnitId, String pageId, int w, int h) {
		super(activity.getApplication());
		
		mContext = activity;
		mPageID = pageId;
		mAdUnitId = adUnitId;
		mAdObject = adObject;
		mWidth = w;
		mHeight = h;
		mAdManager = new AdManager(activity, this, adObject, adUnitId, pageId, mWidth, mHeight);
	}
	
	
	/**
	 * 构造函数
	 * 
	 * @author 田亚鹏
	 *
	 * @param Activity 广告位所在activity AdObject 广告位对象 adUnitId 应用程序类型 pageId 页面标识
	 * 
	 * @return void
	 */
	public AdView(Activity activity, AdObject adObject, String adUnitId, String pageId) {
		super(activity.getApplication());
		
		mContext = activity;
		mPageID = pageId;
		mAdUnitId = adUnitId;
		mAdObject = adObject;
		mWidth = adObject.getWidth();
		mHeight = adObject.getHeight();
		mAdManager = new AdManager(activity, this, adObject, adUnitId, pageId, mWidth, mHeight);
		//createAdView(activity, adObject, adUnitId, pageId);
	}
		
	public void setDefaultPicId(int i) {
		mDefaultPicId = i;
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.v("AdView", "onClick ~~~~~~~~~~~~~~~~~~~");
			
//			String urls = "http://www.9pl.com/uploadfile/wallpaper/67/d7z425ffkllv731u.jpg";
//			(new PictureDecoder()).execute(new String[]{urls});
			
			String ldptype = mAdsInformation.getLdpType();
			
			if(mAdsInformation == null || mAdsInformation.getLdp() == null || mAdsInformation.getLdp().equals("") || ldptype == null || ldptype.equals("") )
				return;
			
			JSONObject jsonObj = null;	
			
			try {
				jsonObj = new JSONObject(mAdsInformation.getLdp());
			} catch (JSONException e1) {
				e1.printStackTrace();
				
				return;
			} 
			
			if(ldptype.equals("P")) {	//打开网址
				
				String url = null;
				try {
					url = jsonObj.getString("URL");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(url != null && url.startsWith("http://")) {
					Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url.trim()));			 
					mContext.startActivity(viewIntent);
				}
			}
			
			if(ldptype.equals("B")) {	//打开图片			
				String pic_url = null ;
				String viewType = null;
				try {
					pic_url = jsonObj.getString("URL");
					viewType = jsonObj.getString("ViewType");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();				
					return;
				}
				
				if(viewType.equals("0")) {	
					try {
						AdWaitDialog mWaitDialog = new AdWaitDialog(mContext);
						isShowPictureDialig = true;
						mWaitDialog.setOnDismissListener(mDismissListener);
						mWaitDialog.show();
						(new PictureDecoder(mWaitDialog)).execute(new String[]{pic_url.trim()});
					}
		            catch(Exception e) {
		            	return;
		            }				
				}
				
				if(viewType.equals("1")) {			
					String[] params = {pic_url.trim(), mAdsInformation.getAdsFileName()};
					(new PictureDownloader()).execute(params);
				}

			}
			
			if(ldptype.equals("D")) {	//应用商店
				try {
					String appid = jsonObj.getString("APPID");
					String urlIcon = jsonObj.getString("Icon");
					
					if(AdUtil.isJJDS)
						AdUtil.startJJDSAppStore(appid, urlIcon, mContext);
					else
						AdUtil.startAppStore(appid, mContext);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(ldptype.equals("S")) {	
				try {
					String packagename = jsonObj.getString("Package");
					String activity = jsonObj.getString("Activity");
					String appid = jsonObj.getString("APPID");
					String urlIcon = jsonObj.getString("Icon");
					
					Log.v(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					Log.v(TAG, "packagename " + packagename);
					Log.v(TAG, "activity " + activity);
					Log.v(TAG, "appid " + appid);
					Log.v(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	
					PackageInfo packageInfo;
					try{
				        packageInfo = mContext.getPackageManager().getPackageInfo(packagename, 0);				 
					}catch(NameNotFoundException e) {
				        packageInfo = null;
				        e.printStackTrace();
					}
					
					if(packageInfo == null){						
						
						if(AdUtil.isJJDS)
							AdUtil.startJJDSAppStore(appid, urlIcon, mContext);
						else
							AdUtil.startAppStore(appid, mContext);
					}
					else{
						
						//activity = activity;
						Log.v(TAG, "run app  ~~~~~~~~~~~~~~ " + activity);
						Intent i = new Intent();
					    i.setComponent(new ComponentName(packagename, activity));   
					    i.setAction(Intent.ACTION_MAIN);   
					    mContext.startActivity(i);  
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			
			if(ldptype.equals("M")) {	//播放视频
				
				String url = null;
				try {
					url = jsonObj.getString("URL");
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri uri = Uri.parse(url);
					intent.setDataAndType(uri, "video/*");
					mContext.startActivity(intent);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch(Exception e) {
					e.printStackTrace();
				}				
			}
					
			new AdsUploadClickLog.Builder(mContext, mAdsInformation.getContentsClickm(), mPageID).build();
		}	
	};
	
	OnDismissListener mDismissListener = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {
			isShowPictureDialig = false;
		}
		
	};
	
	private boolean isShowPictureDialig = true;
	
	class PictureDecoder extends AsyncTask<String, Void, Void> {
		private boolean isDone;
		private String filepath;
		private Bitmap bitmap;
		
		private AdWaitDialog mAdWaitDialog;
		
		public PictureDecoder(AdWaitDialog d) {
			mAdWaitDialog = d;
		}

		@Override
		protected Void doInBackground(String... params) {
			
			filepath = mContext.getFilesDir().getAbsolutePath() + "/click_open.jpg";
			
			File file = new File(filepath);	
			if(file.exists())
				file.delete();
			
			Log.v("AdView", filepath + " PictureDecoder ~~~~~~~ " + params[0]);
			isDone = AdUtil.downLoadPicture(params[0], filepath, mContext);
			
			if(isDone && isShowPictureDialig) {
				bitmap = BitmapFactory.decodeFile(filepath);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void mVar) {
			if(isDone && bitmap != null && isShowPictureDialig) {
		        DisplayMetrics dm = new DisplayMetrics();
		        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
		        int width = dm.widthPixels;
		        int height = dm.heightPixels;
				AdPictureDialog aPicture = new AdPictureDialog(mContext, width, height, bitmap); 
				aPicture.show();
			}
			
			if(mAdWaitDialog != null && mAdWaitDialog.isShowing())
				mAdWaitDialog.dismiss();
		}
	}
	
	class PictureDownloader extends AsyncTask<String, Void, Void> {
		private boolean isDone;
		private String filepath;

		@Override
		protected Void doInBackground(String... params) {
			
			filepath = params[1];
			Log.v("AdView", "PictureDownloader run  ~~~~~~~~~~~~~~~~~~");
			isDone = AdUtil.downLoadPicture(params[0], params[1], mContext);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void mVar) {
			if(isDone) {
				filepath = mContext.getFilesDir().getAbsolutePath() + "/" + filepath;
				Log.v(TAG, "PictureDownloader filepath " + filepath);
				AdUtil.startPicViewer(filepath, mContext);
			}
		}
	}
	

	private void createAdView(Activity activity, AdObject adObject, String adUnitId, String pageid) {
		
		mView = mAdManager.getInnerView();
		
		setGravity(Gravity.CENTER);
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		
		int width = Math.round(TypedValue.applyDimension(
				TypedValue.TYPE_REFERENCE, mWidth, activity
						.getResources().getDisplayMetrics()));
		
		int height = Math.round(TypedValue.applyDimension(
				TypedValue.TYPE_REFERENCE, mHeight, activity
						.getResources().getDisplayMetrics()));
		
		addView(mView, width, height);
		
		setFocusable(true);
    	setClickable(true);

    	setOnClickListener(mClickListener);
    	Log.v("AdView", "createAdView setFocusable ~~~~~~~~~~~~~~~~~~");
	}
	
	private void showDefaultAd() {
		setGravity(Gravity.CENTER);
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		
		int width = Math.round(TypedValue.applyDimension(
				TypedValue.TYPE_REFERENCE, mWidth, mContext
						.getResources().getDisplayMetrics()));
		
		int height = Math.round(TypedValue.applyDimension(
				TypedValue.TYPE_REFERENCE, mHeight, mContext
						.getResources().getDisplayMetrics()));
		
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mDefaultPicId);
		ImageView miView = new ImageView(mContext);
		miView.setImageBitmap(bitmap);
		
		addView(miView, width, height);
		
		parentView.addView(AdView.this);
		
		Log.v("AdView", "showDefaultAd ~~~~~~~~~~~~~~~~~~  " + mDefaultPicId + "  " + width);
	}
	
	public View getInnerView() {
		return mView;
	}
	
	
	/**
	 * 销毁AdView
	 * 
	 * @author 田亚鹏
	 *
	 * @param void
	 * 
	 * @return void
	 */
	
	public void destroy() {
		if (mAdManager != null) {
			mAdManager.destroyAdManager();
			mAdManager = null;
		}
	}
	
	
	/**
	 * 判断广告资源是否已经就绪
	 * 
	 * @author 田亚鹏
	 *
	 * @param void
	 * 
	 * @return boolean
	 */
	
	public boolean isReady() {
		if (mAdManager == null)
			return false;
		else
			return mAdManager.isReady();
	}

	
	/**
	 * 加载广告
	 * 
	 * @author 田亚鹏
	 *
	 * @param AdRequest 加载请求 LinearLayout 父layout
	 * 
	 * @return void
	 */
	public void loadAd(AdRequest adRequest, LinearLayout parenet) {
		// TODO Auto-generated method stub
		if (mAdManager == null || mAdManager.getActivity() == null) {
    		AdsLog.errorLog("Activity was null.");
    		return;
    	}
		parentView = parenet;
		mAdManager.setAdListener(mAdListener);
		mAdManager.sendRequest(adRequest);
	}

	
	/**
	 * 设置回调接口
	 * 
	 * @author 田亚鹏
	 *
	 * @param AdListener 回调接口
	 * 
	 * @return void
	 */
	public void setAdListener(AdListener adListener) {
		mAppAdListener = adListener;
	}

	private AdListener mAdListener = new AdListener() {

		@Override
		public void onFailedToReceiveAd(ErrorCode errorcode) {
			Log.v("AdView", "onFailedToReceiveAd  ~~~~~~~~~~~~~~~~~~");
			
			if(mAppAdListener != null) {
				mAppAdListener.onFailedToReceiveAd(errorcode);
			}
			
			showDefaultAd();
		}

		@Override
		public void onReceiveAd() {
			
			Log.v("AdView", "onReceiveAd  ~~~~~~~~~~~~~~~~~~");
			if(mAdManager != null) {
				mAdsInformation = mAdManager.getAdsInformation();
				createAdView(mContext, mAdObject, mAdUnitId, mPageID);
				if(mAppAdListener != null)
					mAppAdListener.onReceiveAd();
				parentView.addView(AdView.this);
			}
			else {
				Log.v("AdView", "onReceiveAd mAdManager == null ~~~~~~~~~~~~~~~~~~");
				if(mAppAdListener != null) {
					mAppAdListener.onFailedToReceiveAd(ErrorCode.INTERNAL_ERROR);
					showDefaultAd();
				}
			}
		}

		@Override
		public void onRefreshAd() {
			Log.v("AdView", "onReFreshAd  ~~~~~~~~~~~~~~~~~~");
			
			if(mAdManager != null) {
				mAdsInformation = mAdManager.getAdsInformation();
				if(mAppAdListener != null)
					mAppAdListener.onRefreshAd();
			}
		}
	};
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		AdsLog.debugLog("onDetachedFromWindow");
		if (mAdManager != null)
		{
			mAdManager.destroyAdManager();
			mAdManager = null;
		}
		super.onDetachedFromWindow();
	}
}
