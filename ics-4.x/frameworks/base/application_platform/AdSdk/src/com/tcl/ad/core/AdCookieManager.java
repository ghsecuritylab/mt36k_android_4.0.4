package com.tcl.ad.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class AdCookieManager {
	
	private Context mContext;
	private SharedPreferences mCookies;
	
	private static AdCookieManager mInstance;
	private static final String TAG = "AdCookieManager";
	private static final String PREFS_ID = "AdCookieManagerPreferences";
	
	private AdCookieManager(Context c) {
		mContext = c;
	}
	
	public static AdCookieManager getInstance(Context c) {
		if(mInstance == null)
			mInstance = new AdCookieManager(c);
		else {
			mInstance.mContext = c;
		}
		
		return mInstance;
	}
	
	public synchronized String getCookies() {
		mCookies = mContext.getSharedPreferences(PREFS_ID, 0);
		
		String cf = mCookies.getString("cf", "");
		String sdf = mCookies.getString("sdf", "");
		String puv = mCookies.getString("puv", "");
		String suv = mCookies.getString("suv", "");
		String cuv = mCookies.getString("cuv", "");
		String cduv = mCookies.getString("cduv", "");
		String mzid = mCookies.getString("mzid", "");
		String cookie = cf+sdf+puv+suv+cuv+cduv+mzid;
		
		Log.v(TAG, "getCookies " + cookie);
		
		return cookie;
	}
	
	public synchronized void setCookies(String cookie) {
		
		Log.v(TAG, "setCookies " + cookie);
		
		mCookies = mContext.getSharedPreferences(PREFS_ID, 0);
		Editor editor = mCookies.edit();
	
		String v = sub(cookie);
		
		if(v.startsWith("cf")) {
			editor.putString("cf", v);  
			
			Log.v(TAG, "commit cf " + v);

		} else if(v.startsWith("sdf")) {
			editor.putString("sdf", v);  
			
			Log.v(TAG, "commit sdf " + v);
		} else if(v.startsWith("puv")) {
			editor.putString("puv", v);  
			Log.v(TAG, "commit puv " + v);
		} else if(v.startsWith("suv")) {
			editor.putString("suv", v);  
			Log.v(TAG, "commit suv " + v);
		} else if(v.startsWith("cuv")) {
			editor.putString("cuv", v);  
			Log.v(TAG, "commit cuv " + v);
		} else if(v.startsWith("cduv")) {
			editor.putString("cduv", v);  
			Log.v(TAG, "commit cduv " + v);
		} else if(v.startsWith("mzid")) {
			editor.putString("mzid", v);  
			Log.v(TAG, "commit mzid " + v);
		}
		else
			Log.v(TAG, " error cookie~~~~~~~~~~~~~~~~~~~~");
		
		editor.commit();
	}
	
	private static String sub(String s) {
		for(int i = 0; i < s.length(); i++) {
			if(s.charAt(i) == ';')
				return s.substring(0, i)+";";
		}
		
		return s;
	}
} 
