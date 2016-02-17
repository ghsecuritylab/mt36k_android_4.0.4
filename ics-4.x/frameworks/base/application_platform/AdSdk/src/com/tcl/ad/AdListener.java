package com.tcl.ad;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.10.12
 * 
 * @JDK version: 1.5
 * @brief: A simple interface for receiving notifications 
 * on the status of the ads.  
 * @version: v1.0
 *
 */
public interface AdListener {

	/**
	 * 获取成功后的回调方法
	 * 
	 * @author 田亚鹏
	 *
	 * @param void
	 * 
	 * @return void
	 */
	public void onReceiveAd();
	
	/**
	 * 刷新成功后的回调方法
	 * 
	 * @author 田亚鹏
	 *
	 * @param void
	 * 
	 * @return void
	 */
	public void onRefreshAd();
	
	/**
	 * 获取失败后的回调方法
	 * 
	 * @author 田亚鹏
	 *
	 * @param AdRequest.ErrorCode 错误码
	 * 
	 * @return void
	 */
	public void onFailedToReceiveAd(AdRequest.ErrorCode errorcode);
//
//	public void onPresentScreen(Ad ad);
//
//	public void onDismissScreen(Ad ad);
//
//	public void onLeaveApplication(Ad ad);
}
