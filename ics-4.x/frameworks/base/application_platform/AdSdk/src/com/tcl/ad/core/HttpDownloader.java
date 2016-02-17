/**
 * 
 */
package com.tcl.ad.core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.10.21
 * 
 * @JDK version: 1.5
 * @brief: Download image/apk via Http protocal.   
 * @version: v1.0
 *
 */
public class HttpDownloader extends AsyncTask<String, Integer, Boolean> {

	private AdManager mAdManager;
	private Boolean   mDownloadAdsResources;

	public HttpDownloader(AdManager adManager, Boolean downloadAdsResources) {
		mAdManager = adManager;
		mDownloadAdsResources = downloadAdsResources;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		Boolean isSuccessful = false;
		Context context = mAdManager.getActivity();
		AdsInformation adsInformation = mAdManager.getAdsInformation();
		if (context == null || adsInformation == null)
		{
			AdsLog.errorLog("sundy-->context-->" + context.toString());// + "-->adsInformation" + adsInformation.toString());
			AdsLog.errorLog("doInBackgroud activity or adInformation is null.");
			return false;
		}
		
		try {
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
			
			URL webUrl = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) webUrl.openConnection();
			try {
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoOutput(true);

				FileOutputStream fileOutput = null;
				if (mDownloadAdsResources) {
					String fileOutputPath = new StringBuilder().append(adsInformation.getCacheFileSavedPath()).append("/").append(fileName).toString();
					//Remove the file if it does exist.
					//context.deleteFile(fileOutputPath);
					// this will be used to write the downloaded data into the
					// file we created
					fileOutput = new FileOutputStream(fileOutputPath);
					
				} else {
					fileOutput = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
				}

				try {
					InputStream inStream = urlConnection.getInputStream();

					int totalSize = urlConnection.getContentLength();
					int downloadedSize = 0;

					byte[] buffer = new byte[1024];
					int bufferLength = 0;

					while ((bufferLength = inStream.read(buffer)) > 0 && !isCancelled()) {
						fileOutput.write(buffer, 0, bufferLength);
						downloadedSize += bufferLength;
						publishProgress((int)((downloadedSize/(float)totalSize) * 100));
					}
					isSuccessful = (downloadedSize == totalSize);
					inStream.close();
				} finally {
					fileOutput.close();
				}
			} catch (FileNotFoundException e) {
				AdsLog.errorLog("FileOutputStream = " + e.getMessage());
			} catch (SecurityException e) {
				AdsLog.errorLog("SecurityException = " + e.getMessage());
			} finally {
				urlConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			AdsLog.errorLog("MalformedURLException = " + e.getMessage());
		} catch (IOException e) {
			AdsLog.errorLog("IOException = " + e.getMessage());
		} catch (Exception e) {
			AdsLog.errorLog("URLConnection Exception " + e.getMessage());
		}
		return isSuccessful;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		
		Log.v("HttpDownloader", "onPostExecute ~~~~~~~~~~~~~~~~~~~~~~~~");
		if (mAdManager != null && result) {
			if (mDownloadAdsResources) {
				mAdManager.notifyAdReceived();
			} 
			else {
				//download banner resources
				AdsInformation adsInformation = mAdManager.getAdsInformation();
				if (adsInformation != null) {
					adsInformation.setHasLoaded(true);
				}
			}
		} else {
			AdsLog.errorLog("Httpdownder failed");
		}
		//Release AdManger
		mAdManager = null;
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		AdsLog.errorLog("Cancel AsyncTask in httpdownloader");
		//Release AdManger
		mAdManager = null;
	}

//	@Override
//	protected void onProgressUpdate(Integer... values) {
//		// TODO Auto-generated method stub
//		AdsLog.infoLog("onProgressUpdate = " + values[0]);
//	}
}
