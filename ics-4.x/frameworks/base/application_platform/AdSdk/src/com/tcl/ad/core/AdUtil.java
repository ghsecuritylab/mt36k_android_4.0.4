/**
 * 
 */
package com.tcl.ad.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import org.xml.sax.SAXException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.10.21
 * 
 * @JDK version: 1.5
 * @brief: Provide some fundamentals function.   
 * @version: v1.0
 *
 */
public final class AdUtil {

	private static String	mUserAgentString = null;

	private static String APP_ID;
	
	//public static final String SERVER_URL = "http://adstest.huantest.com/a.gif";
	
	public static final String SERVER_URL = "http://ads.huan.tv:80/a.gif";
	
	public static final boolean isJJDS = false;
	
	private AdUtil() {};
	
	public static void startPicViewer(String pathName, Context c) {
		try {
			Intent i = new Intent();			
			i.setAction("android.intent.action.VIEWLIST");
			Bundle bundle = new Bundle();
			ArrayList<String> list = new ArrayList<String>();
			list.add(pathName);
			bundle.putStringArrayList("selectImages", list);
			bundle.putInt("selectIndex", 0);
			i.putExtras(bundle);
			c.startActivity(i);
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static void startAppStore(String id, Context c) {
		try {
			Intent i = new Intent();
			i.putExtra("appid", id);
			
		    i.setComponent(new ComponentName("com.tcl.appmarket2","com.tcl.appmarket2.ActivityDetail"));   
		    i.setAction(Intent.ACTION_VIEW);   
		    c.startActivity(i); 
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static void startJJDSAppStore(String id, String url, Context c) {
		try {
			Intent i = new Intent();
			i.putExtra("appid", id);
			i.putExtra("iconurl", url);
			
		    i.setComponent(new ComponentName("com.tcl.appmarket","com.tcl.appmarket.ui.AppDetailActivity"));   
		    i.setAction(Intent.ACTION_VIEW);   
		    c.startActivity(i); 
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static boolean downLoadPicture(String picUrl, String name, Context c) {
		
		File file = new File(name);		
		if (file.exists() && file.length() > 0)
			return true;
		
		try {
			URL url = new URL(picUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setReadTimeout(5000);
			DataInputStream in = new DataInputStream(connection.getInputStream());
			DataOutputStream out = new DataOutputStream(c.openFileOutput(file.getName(), Context.MODE_WORLD_READABLE));
			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}
			out.close();
			in.close();
			connection.disconnect() ;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	public final static BitmapDrawable getNewImage(String pathName, int targetImageWidth, int targetImageHeight) {
		AdsLog.debugLog("sundy-->getNewImage" + "targetImageWidth: " + targetImageWidth + "targetImageHeight: " + targetImageHeight);
		
		
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(pathName, options);

			int scale = 1;
			while ((options.outWidth / scale > targetImageWidth * 2 ) || (options.outHeight / scale > targetImageHeight * 2))
				scale *= 2;
			//Release BitmapFactory.Options
			options = null;
			
			options = new BitmapFactory.Options();
			options.inSampleSize = scale;
			
			Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
			//Release BitmapFactory.Options
			options = null;
			if (bitmap == null)
			{
				AdsLog.errorLog("Bitmap decode failed");
				return null;
			}
			
			int originalImageWidth = bitmap.getWidth();
			int originalImageHeight = bitmap.getHeight();
			
			float scaleWidth = ((float) targetImageWidth) / originalImageWidth;
			float scaleHeight = ((float) targetImageHeight) / originalImageHeight;
			
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);

			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, originalImageWidth,
					originalImageHeight, matrix, true);
			if (bitmap != resizedBitmap) bitmap.recycle();
			return new BitmapDrawable(resizedBitmap);
		} catch (OutOfMemoryError e) {
			Log.v("AdUtil", "OutOfMemoryError ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		}
		
		return null;
	}

	
	public final static String getApplicationCacheDirectory(Context context, Boolean canCreate) {
		String applicationDataPath = Environment.getDataDirectory().getPath();
		String cacheDirectory = new StringBuilder().append(applicationDataPath).append("/data/").append(context.getPackageName()).append("/cache").toString();
		File cacheDirectoryFile = new File(cacheDirectory);
		if (!cacheDirectoryFile.exists() && canCreate) {
			try {		
				return cacheDirectoryFile.mkdirs() ? cacheDirectory : null;
			}
			catch (SecurityException e) {
				AdsLog.errorLog("create cache directory failed");
				return null;
			}
		}
		return cacheDirectory;
	}

	
	public final static String lastPathComponent(String string) {
		
		if(string == null)
			return null;
		
		String[] item = string.split("/");
		return item[item.length - 1];
	}
	
	public final static Boolean isValidURL(String urlPath) {
		try {
			new URL(urlPath);
			return true;
		}
		catch (MalformedURLException e) {
			AdsLog.errorLog("invalid url");
		}
		return false;
	}
	
	public final static void removeImageCacheDirectory(String directoryPath) {
		
		Log.v("AdUtil", "removeImageCacheDirectory " + directoryPath);
		if (directoryPath == null)
			return;
		try {
			File cacheDiretory = new File(directoryPath);
			
			if(!cacheDiretory.exists())
				return;
			
			for (File child : cacheDiretory.listFiles())
				child.delete();
			cacheDiretory.delete();
		} catch (SecurityException e) {
			AdsLog.errorLog("removeImageCacheDirectory = " + e.getMessage());
		}
	}
	
	public final static void removeWorldReadableFile(String filePath) {
		try {
			new File(filePath).delete();
		} catch (SecurityException e) {
			AdsLog.errorLog("removeCacheFile = " + e.getMessage());
		}
	}
	
	public final static AdsInformation parseXML(String xml) {
		AdsXMLHandler handler = new AdsXMLHandler();
		try {
			Xml.parse(xml, handler);
		} catch (SAXException e) {
			AdsLog.errorLog("parseXML = " + e.getMessage());
		}
		return handler.getAdsInformation();
	}
	

	
	public final static Boolean equalPackages(String installedPackageName, String packageName) {
		return installedPackageName.equalsIgnoreCase("package:" + packageName);
	}
	
	public final static String getAppID() {
		return APP_ID;
	}
	
	public final static void setAppID(String id) {
		APP_ID = id;
	}
	
	public final static String getUserAgentString(Context context) {
		if (mUserAgentString == null) {			
			String productName = System.getProperty("os.name", "Linux");
			String nickName = (new StringBuilder()).append("Android ").append(Build.VERSION.RELEASE).toString();

			String languageCode = Locale.getDefault().getLanguage().toLowerCase();
			if (languageCode == null || languageCode.length() == 0)
				languageCode = "zh";

			String countryCode = Locale.getDefault().getCountry().toLowerCase();
			if (countryCode == null || countryCode.length() == 0)
				countryCode = "CN";

			String productBuild = (new StringBuilder()).append(Build.MODEL).append(" Build/").append(Build.ID).toString();
			
			String userAgentString = (new StringBuilder()).append("Mozilla/5.0 (")
			.append(productName).append("; U; ").append(nickName).append("; ")
			.append(productBuild).append("; ")
			.append(languageCode).append("-").append(countryCode)
			.append(") WebKit/525.1+ (KHTML,likeGecko, Safari/525.1+)")
			.toString();
			
			mUserAgentString = (new StringBuilder()).append(userAgentString)
			.append("#2.0#TCL/SIS9565/V8-0SS6101-LF1V043/Mozilla5.0/1280*720(104136869,405c9b248e79d2968fb0cebd6adfab7142380ffd;2712428,df230e1e416b00e052de980489bd7aa9effab01a)")
			.toString(); 
		}
		return mUserAgentString;
	}
	
	//为什么用ContextWrapper类型
	public final static String getAdsServerPath() {
		
		return SERVER_URL;
	}
}
