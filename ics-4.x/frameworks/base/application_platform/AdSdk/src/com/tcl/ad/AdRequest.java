/**
 * 
 */
package com.tcl.ad;

import java.util.ArrayList;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.10.12
 * 
 * @JDK version: 1.5
 * @brief: An AdRequest object contains information about 
 * the ad to fetch.   
 * @version: v1.0
 *
 */
public class AdRequest {

	private ArrayList<String> mAdsRequestIdList = null;
	public enum ErrorCode
	{
		INVALID_REQUEST,
		NO_FILL,
		NETWORK_ERROR,
		INTERNAL_ERROR
	}
	
	public AdRequest() {
		mAdsRequestIdList = new ArrayList<String>();
	}
	
	public AdRequest addAdsId(String adsId) {
		if (adsId != null && !mAdsRequestIdList.contains(adsId))
			mAdsRequestIdList.add(adsId);
		return this;
	}
	
	public ArrayList<String> getAllAdsId() {
		return mAdsRequestIdList;
	}
}
