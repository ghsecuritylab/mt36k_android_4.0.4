package com.tcl.ad.core;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
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
 * @brief: Upload PV log.   
 * @version: v1.0
 *
 */
public final class AdsUploadPVLog extends Thread {
	
	private static String TAG = "AdsUploadPVLog";
	
	private String 	mRequestString = null;
	private Context	mContext = null;
	private boolean mSendCookie = false;
	private String mPageID;
	
	public AdsUploadPVLog(Context c, String url, String id, boolean s) {
		mContext = c;
		mRequestString = url;
		mSendCookie = s;
		mPageID = id;
	}
	
	
	public void run() {	

//		String d = "";
//		
//		if(mSendCookie)
//			d = "^$mz^d=15";
		
		if(mRequestString == null || mRequestString.length() < 1)
			return;
		
		mRequestString = mRequestString.trim() + "^$mz^d=15" + "^r=" + mPageID;
		Log.v(TAG, mSendCookie + " run ~~~~~~~~~~~~~~~~~~~~~~~~~ " + mRequestString);

		
//		CookieManager cookieManager = CookieManager.getInstance();
//		cookieManager.setAcceptCookie(true);
		
		String urlString = mRequestString.replaceAll("\\^", "%5E");
		
		//Log.v(TAG, " run ~~~~~~~~~~~~~~~~~~~~~~~~~ " + urlString);
		
		
	
		//SharedPreferences cookieInfo = mContext.getSharedPreferences("CookieManager", 0);

		try {
			HttpGet httpGet = new HttpGet(urlString);

			HttpResponse httpResponse = null;

//			String cf = cookieInfo.getString("cf", "");
//			String sdf = cookieInfo.getString("sdf", "");
//			String puv = cookieInfo.getString("puv", "");
//			String suv = cookieInfo.getString("suv", "");
//			String cuv = cookieInfo.getString("cuv", "");
//			String cduv = cookieInfo.getString("cduv", "");
//			String mzid = cookieInfo.getString("mzid", "");
//			
//			String adsCookie = cf+sdf+puv+suv+cuv+cduv+mzid;
			
			AdCookieManager cookieManager = AdCookieManager.getInstance(mContext);
			String adsCookie = cookieManager.getCookies();
			
			//Log.v(TAG, "adsCookie " + adsCookie);
			if(adsCookie != null) {
				httpGet.setHeader("Cookie", adsCookie);  
				
			}
			
			httpGet.setHeader("User-Agent", AdUtil.getUserAgentString(mContext));  
			
			//Header hd = httpGet.getFirstHeader("Cookie");
			
			//Log.v(TAG, "httpGet get header Cookie " + hd);
			
			httpResponse = new DefaultHttpClient().execute(httpGet);
			
			if (httpResponse.getStatusLine().getStatusCode() == 200 && mSendCookie) {			
				
				Header[] headers = httpResponse.getHeaders("Set-Cookie");
				
				//String cString = "";
				
				//Editor editor = cookieInfo.edit();
				
				for(Header h : headers) {
					//String n = h.getName();
					String value = h.getValue();

					
					//String v = sub(value);
					
					cookieManager.setCookies(value);
				
					
//					if(v.startsWith("cf")) {
//						editor.putString("cf", v);  
//						
//						Log.v(TAG, "commit cf " + v);
//		
//					} else if(v.startsWith("sdf")) {
//						editor.putString("sdf", v);  
//						
//						Log.v(TAG, "commit sdf " + v);
//					} else if(v.startsWith("puv")) {
//						editor.putString("puv", v);  
//						Log.v(TAG, "commit puv " + v);
//					} else if(v.startsWith("suv")) {
//						editor.putString("suv", v);  
//						Log.v(TAG, "commit suv " + v);
//					} else if(v.startsWith("cuv")) {
//						editor.putString("cuv", v);  
//						Log.v(TAG, "commit cuv " + v);
//					} else if(v.startsWith("cduv")) {
//						editor.putString("cduv", v);  
//						Log.v(TAG, "commit cduv " + v);
//					} else if(v.startsWith("mzid")) {
//						editor.putString("mzid", v);  
//						Log.v(TAG, "commit mzid " + v);
//					}
//					else
//						;
//					
//					//Log.v(TAG, "Value " + v);
//					editor.commit();
//					//cString = cString + v + ";";
				}
								  
			}
			
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
}
