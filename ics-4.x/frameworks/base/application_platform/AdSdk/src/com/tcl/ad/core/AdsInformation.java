/**
 * 
 */
package com.tcl.ad.core;

import com.tcl.ad.AdType;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.10.13
 * 
 * @JDK version: 1.5
 * @brief: Store ads information.   
 * @version: v1.0
 *
 */
public class AdsInformation {
	private String  mAdsFileName;
	private String  mCacheFileName;
	private String  mAdsPath;
	private String  mAdsLink;
	private int     mAdsClickType;
	private int     mAdsShowTime;
	private int 	mAdsShowInterval;
	private String  mAdsText;
	private int     mAdsLength;
	private String  mCacheFileSavedPath;
	private String	mAdsClass;
	private AdType  mAdType;
	private String  mWorldAccessibleFileSavedPath;
	private Boolean mErrorType;
	private String  mErrorNote;
	private String  mServerTime;
	private String  mChkid;
	private String  mLdpType;
	private String  mLdp;
	private Boolean mHasLoaded;
	
	private String	mSpotSpid;
	private String	mSpotPid;
	private	String	mSpotCaid;
	private	String	mSpotPvm;
	private	String	mSpotPvtpm;
	private	String	mContentsClickm;
	private	String	mContentsClickptm;
	private String  mPageId;
	
	public String getPageId() {
		return mPageId;
	}

	public void setPageId(String id) {
		mPageId = id;
	}
	
	public String getSpotSpid() {
		return mSpotSpid;
	}

	public void setSpotSpid(String spotSpid) {
		mSpotSpid = spotSpid;
	}

	public String getSpotPid() {
		return mSpotPid;
	}

	public void setSpotPid(String spotPid) {
		mSpotPid = spotPid;
	}

	public String getSpotCaid() {
		return mSpotCaid;
	}

	public void setSpotCaid(String spotCaid) {
		mSpotCaid = spotCaid;
	}

	public String getSpotPvm() {
		return mSpotPvm;
	}

	public void setSpotPvm(String spotPvm) {
		mSpotPvm = spotPvm;
	}

	public String getSpotPvtpm() {
		return mSpotPvtpm;
	}

	public void setSpotPvtpm(String spotPvtpm) {
		mSpotPvtpm = spotPvtpm;
	}

	public String getContentsClickm() {
		return mContentsClickm;
	}

	public void setContentsClickm(String contentsClickm) {
		mContentsClickm = contentsClickm;
	}

	public String getContentsClickptm() {
		return mContentsClickptm;
	}

	public void setmContentsClickptm(String contentsClickptm) {
		mContentsClickptm = contentsClickptm;
	}

	public AdsInformation() {
		initInstance();
	}
	
	public String getChkid() {
		return mChkid;
	}

	public void setChkid(String chkid) {
		mChkid = chkid;
	}

	public int getAdsLength() {
		return mAdsLength;
	}

	public void setAdsLength(int adsLength) {
		mAdsLength = adsLength;
	}
	
	public String getServerTime() {
		return mServerTime;
	}

	public void setmServerTime(String serverTime) {
		mServerTime = serverTime;
	}

	public String getErrorNote() {
		return mErrorNote;
	}

	public void setErrorNote(String errorNote) {
		mErrorNote = errorNote;
	}

	public Boolean getErrorType() {
		return mErrorType;
	}

	public void setErrorType(Boolean errorType) {
		mErrorType = errorType;
	}

	public String getCacheFileSavedPath() {
		return mCacheFileSavedPath;
	}
	
	public void setCacheFileSavedPath(String cacheFileSavedPath) {
		mCacheFileSavedPath = cacheFileSavedPath;
	}
	
	public String getWorldAccessibleFileSavedPath() {
		return mWorldAccessibleFileSavedPath;
	}
	
	public void setWorldAccessibleFileSavedPath(String worldAccessibleFileSavedPath) {
		mWorldAccessibleFileSavedPath = worldAccessibleFileSavedPath;
	}
	
	public String getAdsText() {
		return mAdsText;
	}

	public void setAdsText(String adsText) {
		mAdsText = adsText;
	}

	public int getAdsShowTime() {
		return mAdsShowTime;
	}

	public void setAdsShowTime(int adsShowTime) {
		mAdsShowTime = adsShowTime;
	}
	
	public int getAdsShowInterval() {
		return mAdsShowInterval;
	}

	public void setAdsShowInterval(int t) {
		mAdsShowInterval = t;
	}
	
	public String getLdp() {
		return mLdp;
	}

	public void setLdp(String l) {
		mLdp = l;
	}
	
	public String getLdpType() {
		return mLdpType;
	}

	public void setLdpType(String l) {
		mLdpType = l;
	}

	public int getAdsClickType() {
		return mAdsClickType;
	}

	public void setAdsClickType(int adsClickType) {
		mAdsClickType = adsClickType;
	}

	public void clearAllInformation() {
		initInstance();
	}
	
	public String getAdsFileName() {
		return mAdsFileName;
	}

	public void setAdsFileName(String AdsFileName) {
		mAdsFileName = AdsFileName;
	}
	
	public String getCacheFileName() {
		return mCacheFileName;
	}

	public void setCacheFileName(String cacheFileName) {
		mCacheFileName = cacheFileName;
	}

	public String getAdsPath() {
		return mAdsPath;
	}

	public void setAdsPath(String adsPath) {
		mAdsPath = adsPath;
	}

	public String getAdsLink() {
		return mAdsLink;
	}

	public void setAdsLink(String adsLink) {
		mAdsLink = adsLink;
	}
	
	public Boolean getHasLoaded() {
		return mHasLoaded;
	}

	public void setHasLoaded(Boolean hasLoaded) {
		mHasLoaded = hasLoaded;
	}
	
	public void setAdsClass(String adsClass) {
		mAdsClass = adsClass;
	}
	
	public String getAdsClass() {
		return mAdsClass;
	}
	
	
	public void setAdType(String t) {
		mAdType = AdType.type(t);
	}
	
	public AdType getAdType() {
		return mAdType;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AdsInformation [mAdsFileName=");
		builder.append(mAdsFileName);
		builder.append(", mCacheFileName=");
		builder.append(mCacheFileName);
		builder.append(", mAdsPath=");
		builder.append(mAdsPath);
		builder.append(", mAdsLink=");
		builder.append(mAdsLink);
		builder.append(", mAdsClickType=");
		builder.append(mAdsClickType);
		builder.append(", mAdsShowTime=");
		builder.append(mAdsShowTime);
		builder.append(", mAdsText=");
		builder.append(mAdsText);
		builder.append(", mAdsLength=");
		builder.append(mAdsLength);
		builder.append(", mCacheFileSavedPath=");
		builder.append(mCacheFileSavedPath);
		builder.append(", mAdsClass=");
		builder.append(mAdsClass);
		builder.append(", mAdsResourceType=");
		builder.append(mAdType.toString());
		builder.append(", mWorldAccessibleFileSavedPath=");
		builder.append(mWorldAccessibleFileSavedPath);
		builder.append(", mErrorType=");
		builder.append(mErrorType);
		builder.append(", mErrorNote=");
		builder.append(mErrorNote);
		builder.append(", mServerTime=");
		builder.append(mServerTime);
		builder.append(", mChkid=");
		builder.append(mChkid);
		builder.append(", mHasLoaded=");
		builder.append(mHasLoaded);
		builder.append(", mSpotSpid=");
		builder.append(mSpotSpid);
		builder.append(", mSpotPid=");
		builder.append(mSpotPid);
		builder.append(", mSpotCaid=");
		builder.append(mSpotCaid);
		builder.append(", mSpotPvm=");
		builder.append(mSpotPvm);
		builder.append(", mSpotPvtpm=");
		builder.append(mSpotPvtpm);
		builder.append(", mContentsClickm=");
		builder.append(mContentsClickm);
		builder.append(", mContentsClickptm=");
		builder.append(mContentsClickptm);
		builder.append("]");
		return builder.toString();
	}

	private void initInstance() {
		mAdsFileName = null;
		mCacheFileName = null;
		mAdsPath = null;
		mAdsLink = null;
		mAdsClickType = 0;
		mAdsShowTime = -1;
		mAdsLength = 0;
		mAdsText = null;
		mCacheFileSavedPath = null;
		mWorldAccessibleFileSavedPath = null;
		mErrorType = true;
		mErrorNote = null;
		mServerTime = null;
		mChkid = null;
		mHasLoaded = false;
		
		mSpotSpid = null;
		mSpotPid = null;
		mSpotCaid = null;
		mSpotPvm = null;
		mSpotPvtpm = null;
		mContentsClickm = null;
		mContentsClickptm = null;
		mAdsClass = null;
		mAdType = null;
	}
}
