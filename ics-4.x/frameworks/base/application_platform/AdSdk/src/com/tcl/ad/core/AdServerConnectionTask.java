/**
 * 
 */
package com.tcl.ad.core;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlSerializer;

import com.tcl.ad.AdRequest;
import com.tcl.ad.core.AdManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.webkit.CookieManager;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.10.13
 * 
 * @JDK version: 1.5
 * @brief: Communication with Ads Server.   
 * @version: v1.0
 *
 */
public class AdServerConnectionTask extends AsyncTask<AdRequest, String, String> {

	private static final String TAG = "AdServerConnectionTask";
	
	private AdManager			mAdManager;
	private Boolean   			mCanDownload;
	private AdRequest.ErrorCode mErrorCode;
	private HttpDownloader 		mHttpDownloader;
	private AdCookieManager		mCookieManager;
	
	@Override
	protected String doInBackground(AdRequest... params) {
		// TODO Auto-generated method stub
		Activity activity = mAdManager.getActivity();
		if (activity == null)
		{
			AdsLog.errorLog("doInBackgroud activity is null.");
			mErrorCode = AdRequest.ErrorCode.INTERNAL_ERROR;
			return null;
		}
		//For demo
		//final boolean DEBUG = false;
		//if (DEBUG)
		{
			/*String requestAd = createAdsRequestXML(params[0], activity);
			if (requestAd == null) {
				AdsLog.errorLog("doInBackgroud request ad is null");
				mErrorCode = AdRequest.ErrorCode.INVALID_REQUEST;
				return null;
			}*/
			
			StringBuilder result = new StringBuilder();
			try
			{
				String adServerUrl = AdUtil.getAdsServerPath();//"http://10.120.137.44:7838";
				//"http://10.120.137.44:7838/a.gif?ao=1^l=23^c1=11^c2=112^c3=10001";
				String uriPattern = new StringBuilder().append(adServerUrl).append("?ao=").append(mAdManager.adUnitId())
				.append("^l=").append(mAdManager.adObject().toString()).append("^app="+AdUtil.getAppID()).append("^c1=112^c2=10001").toString();
				
				Log.v(TAG, "ad request uri " + uriPattern);
				
				
				URL url = new URL(uriPattern);

				
				String adsCookie = mCookieManager.getCookies();
				Log.v(TAG, "adsCookie " + adsCookie);
				
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoOutput(true);
				
				urlConnection.setDoInput(true);
				urlConnection.setUseCaches(false);
				//urlConnection.setRequestProperty("Content-Type","text/xml;charset=UTF-8");
				urlConnection.setConnectTimeout(10*1000);
				urlConnection.setReadTimeout(5*1000);
				urlConnection.setRequestMethod("GET");
				if (adsCookie != null) {
					urlConnection.setRequestProperty("Cookie", adsCookie);
				}
				urlConnection.addRequestProperty("User-Agent", AdUtil.getUserAgentString(activity));
				//AdsLog.infoLog("doInBackground = " + urlConnection);
				try {


					//Send data to server
					InputStream inStream = new BufferedInputStream(urlConnection.getInputStream());
					BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(inStream));

					String response = null;
					while ((response = responseBuffer.readLine()) != null && !isCancelled())
						result.append(response);
					
				} catch (IOException e) {
					result = null;
					e.printStackTrace();
				} finally {
					//Close connection
					urlConnection.disconnect();
				}
			}
			catch (Exception e) {
				AdsLog.errorLog("URLConnection Exception " + e.getMessage());
				mErrorCode = AdRequest.ErrorCode.NETWORK_ERROR;
				mAdManager.notifyAdFailedToReceiveAd(mErrorCode);
				result = null;
				return null;
			}
			if(result != null)
				return result.toString();
			else {
				return null;
			}
		}
	}

	
	

	public AdServerConnectionTask(AdManager adManager) {
		// TODO Auto-generated constructor stub
		mAdManager = adManager;
		mErrorCode = null;
		mCanDownload = false;
		mHttpDownloader = null;
		mCookieManager = AdCookieManager.getInstance(mAdManager.getActivity());
		//mCookieManager.setAcceptCookie(true);
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		
		mCanDownload = false;
		
		
		if (mErrorCode != null || result == null) {
			AdsLog.errorLog("onPostExecute failed");
			mAdManager.notifyAdFailedToReceiveAd(mErrorCode);
			//Release AdManager
			mAdManager = null;
			return;
		}
		
		Log.v("AdServerConnectionTask", "*************************************************");
		Log.v("AdServerConnectionTask", result);
		Log.v("AdServerConnectionTask", "*************************************************");
		
		if (fillAdsResponseInformation(result)) {
			if (mCanDownload) {
				launchImageDownloader();
			} else {
				mAdManager.notifyAdReceived();
			}
		}
		else {
			mAdManager.notifyAdFailedToReceiveAd(null);
		}
		//Release AdManager
		mAdManager = null;
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		AdsLog.infoLog("Cancel AsyncTask in AdServerConnectionTask.");
		mErrorCode = AdRequest.ErrorCode.INTERNAL_ERROR;
		stopUncompeletedTask();
		//Release AdManager
		mAdManager = null;
	}
	
	public synchronized void stopUncompeletedTask() {
		//Cancel http downloader task if running
		if (mHttpDownloader != null) {
			mHttpDownloader.cancel(true);
			mHttpDownloader = null;
		}
	}
	
	private Boolean fillAdsResponseInformation(String result) {		
		AdsInformation ads = AdUtil.parseXML(result);
		ads.setPageId(mAdManager.getPageID());
		
		if (ads.getErrorType()) {		
			Log.v(TAG, "fillAdsResponseInformation  error ~~~~~~~~~~~~~~~");
			return false;
		}
		
		switch (ads.getAdType()) {
		
		case AD_TYPE_TEXT:
			Log.v(TAG, ads.getAdsText());
			break;
			
		case AD_TYPE_IMAGE:
			if(ads.getAdsPath() == null || ads.getAdsPath().length() == 0){
				return false;
			}
			Log.v(TAG, "adsPath " + ads.getAdsPath());
			mCanDownload = true;
			ads.setAdsFileName(AdUtil.lastPathComponent(ads.getAdsPath()));
			break;
			
		default:
			/*Show image in banner
			 *1->Enlarge image
			 *2->Launch application
			 *3->Launch Browser 
			 */
//			mCanDownload = true;
//			ads.setAdsFileName(AdUtil.lastPathComponent(adsPath));
		}
		
		mAdManager.setAdsInformation(ads);
		return true;
	}
	
	private void launchImageDownloader() {
		mHttpDownloader = new HttpDownloader(mAdManager, true);
		mHttpDownloader.execute(new String[] {""});
	}
}
