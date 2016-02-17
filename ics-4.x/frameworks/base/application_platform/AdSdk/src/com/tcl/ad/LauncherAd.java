package com.tcl.ad;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.PublicKey;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.tcl.ad.AdView.PictureDownloader;
import com.tcl.ad.core.AdCookieManager;
import com.tcl.ad.core.AdManager;
import com.tcl.ad.core.AdUtil;
import com.tcl.ad.core.AdsInformation;
import com.tcl.ad.core.AdsLog;
import com.tcl.ad.core.AdsUploadClickLog;
import com.tcl.ad.core.AdsUploadPVLog;
import com.tcl.ad.core.HttpDownloader;

import android.R.integer;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

public class LauncherAd {
	private Context mContext;
	private AdsInformation mAdsInfo;
	private String mPageID;
	private String mAppType;
	private AdObject mAdObject;
	private AdConnectionTask mAdTask;
	private String mAdPicturePath;
	private AdLoadListener mAdLoadListener;
	private Bitmap mAdBitmap;
	private int mScreenWidth;
	private int mScreenHeight;
	
	private static final String TAG = "LauncherAd";
	
	public LauncherAd(Context context, AdObject adObject, String appType, String pageId, int screenW, int screenH) {	
		mContext = context;
		mPageID = pageId;
		mAppType = appType;
		mAdObject = adObject;
		mScreenWidth = screenW;
		mScreenHeight = screenH;
	}
	
	public void setAdBitmap(Bitmap b) {
		mAdBitmap = b;
	}
	
	public Bitmap getAdBitmap() {
		return mAdBitmap;
	}
	
	public void loadAd() {
		mAdTask = new AdConnectionTask();
		mAdTask.execute();
	}
	
	public void setListener(AdLoadListener l) {
		mAdLoadListener = l;
	}
	
	public AdLoadListener getListener() {
		return mAdLoadListener;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	public AdsInformation getAdsInformation() {
		return mAdsInfo;
	}
	
	public String getAdPicturePath() {
		return mAdPicturePath;
	}
	
	public void setAdPicturePath(String p) {
		mAdPicturePath = p;
	}
	
	public interface AdLoadListener {
		public void loadCompleted(LauncherAd ad);
		public void loadError(LauncherAd ad);
	}
	
	public synchronized void uploadPVLog() {		
		(new AdsUploadPVLog(mContext, mAdsInfo.getSpotPvm(), mAdsInfo.getPageId(), true)).start();
		(new AdsUploadPVLog(mContext, mAdsInfo.getSpotPvtpm(), mAdsInfo.getPageId(), false)).start();
	}
	
	
	public synchronized void uploadClickLog() {
		new AdsUploadClickLog.Builder(mContext, mAdsInfo.getContentsClickm(), mPageID).build();
	}
	
	public void runClick() {
		Log.v("AdView", "onClick ~~~~~~~~~~~~~~~~~~~");

		String ldptype = mAdsInfo.getLdpType();
		
		if(mAdsInfo == null || mAdsInfo.getLdp() == null || mAdsInfo.getLdp().equals("") || ldptype == null || ldptype.equals("") )
			return;
		
		JSONObject jsonObj = null;	
		
		try {
			jsonObj = new JSONObject(mAdsInfo.getLdp());
		} catch (JSONException e1) {
			e1.printStackTrace();
			
			return;
		} 
		
		if(ldptype.equals("P")) {	//打开网址

			try {
				String url = jsonObj.getString("URL");
				if(url != null && url.startsWith("http://")) {
					Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url.trim()));			 
					mContext.startActivity(viewIntent);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
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
			
			if(viewType.equals("1")) {		
				
				PictureOption webViewDiaglog = new PictureOption();
				webViewDiaglog.execute(new String[]{pic_url.trim()});
				
//				URL url;
//				try {
//					url = new URL(pic_url.trim());
//					URLConnection conn = url.openConnection();
//		            conn.setReadTimeout(5000);
//		            conn.connect();
//		            InputStream in = conn.getInputStream();
//		            BufferedInputStream bis = new BufferedInputStream(in);
//					BitmapFactory.Options options = new BitmapFactory.Options(); 
//			        options.inJustDecodeBounds = false; 	
//			        options.inSampleSize = 1; 
//			        BitmapFactory.decodeStream(bis,null,options);
//					
//					AdPictureDialog aPicture = new AdPictureDialog(mContext, mScreenWidth, mScreenHeight, pic_url.trim(), mAdObject.getWidth(), mAdObject.getHeight());
//					aPicture.show();
//				} catch (MalformedURLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					return;
//				} catch (IOException e) {
//					return;
//				}
//				
			}
			
			if(viewType.equals("2")) {			
				String[] params = {pic_url.trim(), mAdsInfo.getAdsFileName()};
				(new PictureDownloader()).execute(params);
			}

		}
		
		if(ldptype.equals("D")) {	//应用商店
			String appid = null;
			try {
				appid = jsonObj.getString("APPID");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			try {
				AdUtil.startAppStore(appid, mContext);
			} catch (Exception e) {
				return;
			}
			
		}
		
		if(ldptype.equals("S")) {	
			try {
				String packagename = jsonObj.getString("Package");
				String activity = jsonObj.getString("Activity");
				String appid = jsonObj.getString("APPID"); 
				
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
					AdUtil.startAppStore(appid, mContext);
				}
				else{
					Log.v(TAG, "run app  ~~~~~~~~~~~~~~ activity " + activity);
					Intent i = new Intent();
				    i.setComponent(new ComponentName(packagename,activity));   
				    i.setAction(Intent.ACTION_VIEW);   
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
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
		}
		
		uploadClickLog();
	}
	
	class PictureOption extends AsyncTask<String, Void, Void> {
		
		private boolean isDone;
		private String filepath;
		private Bitmap mBitmap;

		@Override
		protected Void doInBackground(String... params) {
			
			filepath = mContext.getFilesDir().getAbsolutePath() + "/launcher_click_open.jpg";
			
			File file = new File(filepath);	
			if(file.exists())
				file.delete();
			
			Log.v(TAG, filepath + " PictureOption ~~~~~~~ " + params[0]);
			isDone = AdUtil.downLoadPicture(params[0], filepath, mContext);
			
			if(isDone) {
				mBitmap = BitmapFactory.decodeFile(filepath);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void mVar) {
			if(isDone && mBitmap != null) {
				AdPictureDialog aPicture = new AdPictureDialog(mContext, mScreenWidth, mScreenHeight, mBitmap); 
				aPicture.show();
			}
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
	
	private class AdConnectionTask extends AsyncTask<AdRequest, String, String> {

		private static final String TAG = "AdConnectionTask";
		private ResDownloader mResDownloader;
		
		@Override
		protected String doInBackground(AdRequest... params) {
			
			StringBuilder result = new StringBuilder();
			try {
				final String adServerUrl = AdUtil.SERVER_URL;
	
				String uriPattern = new StringBuilder().append(adServerUrl).append("?ao=").append(mAppType)
				.append("^l=").append(mAdObject.toString()).append("^app="+AdUtil.getAppID()).append("^c1=112^c2=10001").toString();
	
				Log.v(TAG, "url " + uriPattern);
				
				URL url = new URL(uriPattern);

			
				AdCookieManager	mCookieManager = AdCookieManager.getInstance(mContext);
				String adsCookie = mCookieManager.getCookies();
				
				Log.v(TAG, "adsCookie " + adsCookie);
				
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoOutput(true);
				
				urlConnection.setDoInput(true);
				urlConnection.setUseCaches(false);
	
				urlConnection.setConnectTimeout(10*1000);
				urlConnection.setReadTimeout(5*1000);
				urlConnection.setRequestMethod("GET");
				if (adsCookie != null) {
					urlConnection.setRequestProperty("Cookie", adsCookie);
				}
				urlConnection.addRequestProperty("User-Agent", AdUtil.getUserAgentString(mContext));
				AdsLog.infoLog("doInBackground = " + urlConnection);
				try {
					InputStream inStream = new BufferedInputStream(urlConnection.getInputStream());
					BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(inStream));
	
					String response = null;
					while ((response = responseBuffer.readLine()) != null && !isCancelled())
						result.append(response);
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					//Close connection
					urlConnection.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
			return result.toString();
		}
		
		


		@Override
		protected void onPostExecute(String result) {			
			
			if (result == null)
				return;
			
			Log.v("TAG", "*************************************************");
			Log.v("TAG", result);
			Log.v("TAG", "*************************************************");
			
			mAdsInfo = AdUtil.parseXML(result);
			
			if(mAdsInfo == null)
				return;
			
			mAdsInfo.setPageId(mPageID);
			mAdsInfo.setAdsFileName(AdUtil.lastPathComponent(mAdsInfo.getAdsPath()));
			startImageDownloader();
		}
		
		private void startImageDownloader() {
			mResDownloader = new ResDownloader(LauncherAd.this, true);
			mResDownloader.execute(new String[] {""});
		}
	}
}

class ResDownloader extends AsyncTask<String, Integer, Boolean> {

	private LauncherAd mAd;
	private Boolean   mDownloadAdsResources;

	public ResDownloader(LauncherAd adManager, Boolean download) {
		mAd = adManager;
		mDownloadAdsResources = download;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		Boolean isSuccessful = false;
		Context context = mAd.getContext();
		AdsInformation adsInformation = mAd.getAdsInformation();
		if (context == null || adsInformation == null || adsInformation.getAdsPath() == null) {
			AdsLog.errorLog("doInBackgroud activity or adInformation is null.");
			mAd.getListener().loadError(mAd);
			return false;
		}
		
//		try {
		String urlString = null;
		String fileName = null;
		
		if (mDownloadAdsResources) {
			//download banner ads
			urlString = adsInformation.getAdsPath();
			fileName = adsInformation.getAdsFileName();
			String fileSavedPath = AdUtil.getApplicationCacheDirectory(context, true);
			adsInformation.setCacheFileSavedPath(fileSavedPath);
		} else {
			//download clicking resources
			urlString = adsInformation.getAdsLink();
			fileName = AdUtil.lastPathComponent(urlString);
			adsInformation.setCacheFileName(fileName);
			adsInformation.setWorldAccessibleFileSavedPath(context.getFilesDir().getAbsolutePath());
		}
		
		Log.v("ResDownloader", urlString);
		String fileOutputPath = new StringBuilder().append(adsInformation.getCacheFileSavedPath()).append("/").append(fileName).toString();
		Log.v("ResDownloader", fileOutputPath);
		
		mAd.setAdPicturePath(fileOutputPath);
		
		DataInputStream in = null;
		DataOutputStream out = null;
		HttpURLConnection connection = null;
		
		File file = new File(fileOutputPath);		
		if (file.exists() && file.length() > 0) {
			mAd.getListener().loadCompleted(mAd);
			return true;
		}

		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setReadTimeout(3000);
			 in = new DataInputStream(connection.getInputStream());
			 out = new DataOutputStream(new FileOutputStream(file));
			
			Log.v("ResDownloader", "downing.......");
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}
			
			isSuccessful = true;
			
		} catch (IOException e) {
			isSuccessful = false;
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
				in.close();
				connection.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
			
		Log.v("ResDownloader", "down over ......." + isSuccessful);
		
		if(isSuccessful)
			mAd.getListener().loadCompleted(mAd);
		else
			mAd.getListener().loadError(mAd);
		
		return isSuccessful;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		
		Log.v("ResDownloader", "onPostExecute ~~~~~~~~~~~~~~~~~~~~~~~~");
		if (mAd != null && result) {
			if (mDownloadAdsResources) {
				//mAd.notifyAdReceived();
			} else {
				//download banner resources
				AdsInformation adsInformation = mAd.getAdsInformation();
				if (adsInformation != null) {
					adsInformation.setHasLoaded(true);
				}
			}
		} else {
			AdsLog.errorLog("Httpdownder failed");
		}
		//Release AdManger
		mAd = null;
	}

	
}
