package com.tcl.ad.core;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.CookieManager;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.11.22
 * 
 * @JDK version: 1.5
 * @brief: Upload click log.   
 * @version: v1.0
 *
 */
public final class AdsUploadClickLog {
	private String 	mRequestString = null;
	private Context	mContext = null;
	
	class Connection implements Runnable {		
		public void run() {
			// TODO Auto-generated method stub
			try {	
				
				//String urlString = mRequestString.replaceAll("\\^", "%5E");
				

				
				URL url = new URL(mRequestString);
				
				Log.v("AdsUploadClickLog", "run " + mRequestString);
				
//				SharedPreferences cookieInfo = mContext.getSharedPreferences("CookieManager", 0);
//				
//				String cf = cookieInfo.getString("cf", "");
//				String sdf = cookieInfo.getString("sdf", "");
//				String puv = cookieInfo.getString("puv", "");
//				String suv = cookieInfo.getString("suv", "");
//				String cuv = cookieInfo.getString("cuv", "");
//				String cduv = cookieInfo.getString("cduv", "");
//				String mzid = cookieInfo.getString("mzid", "");
//				
//				String adsCookie = cf+sdf+puv+suv+cuv+cduv+mzid;
				
				AdCookieManager cookieManager = AdCookieManager.getInstance(mContext);
				String adsCookie = cookieManager.getCookies();
				
				
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				if (adsCookie != null) {
					urlConnection.setRequestProperty("Cookie", adsCookie);
				}
				urlConnection.addRequestProperty("User-Agent", AdUtil.getUserAgentString(mContext));
				
				try {
					//Send request
					urlConnection.getInputStream();
					
					AdsLog.debugLog("AdsUploadClickLog Send request ok ~~~~~~~~~~~~~~~~~~~");
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					AdsLog.errorLog("IOException " + e.getMessage());
					e.printStackTrace();
				} 
				catch (Exception e){
					AdsLog.errorLog("Exception " + e.getMessage());
				}finally {
					urlConnection.disconnect();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static class Builder {
		private String clickm = null;
		private Context context = null;
		private String pageid;
		
		public Builder(Context context, String clickm, String id) {
			this.clickm = clickm;
			this.context = context;
			pageid = id;
		}
		
		public AdsUploadClickLog build() {
			return new AdsUploadClickLog(this);
		}
	}
	
	private Boolean createRequestString(Builder builder) {
		mContext = builder.context;
		
		if(builder.clickm == null || builder.clickm.trim().length() < 1)
			return false;
		
		mRequestString = builder.clickm.trim() + "^$mz" + "^r=" + builder.pageid;
		return true;
	}
	
	private AdsUploadClickLog(Builder builder) {
		if (createRequestString(builder)) {
			new Thread(new Connection()).start();
		}
	}
}
