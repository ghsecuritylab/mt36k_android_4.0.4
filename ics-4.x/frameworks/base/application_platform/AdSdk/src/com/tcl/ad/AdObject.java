/**
 * 
 */
package com.tcl.ad;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.10.12
 * 
 * @JDK version: 1.5
 * @brief: An different type of ads supported.   
 * @version: v1.0
 *
 */
public class AdObject {
	
//	SINA_EBOOK_ZUOCEDIBU(315, 160, "TCL-COMMON-HUAN-XLDZS-SYAN"),  //云赏阅读
//	SINA_EBOOK_SOUSUOANNIU(315, 590, "TCL-COMMON-HUAN-XLDZS-SSAN"),
//
//	
//	ONLINE_MUSIC_SHOUYETUIJIAN(759, 549, "TCL-COMMON-HUAN-ZXYY-SYTJ"),
//	ONLINE_MUSIC_BOFANGFANZHUAN(383, 383, "TCL-COMMON-HUAN-ZXYY-BFFZ"),
//	ONLINE_MUSIC_SOUSUOANNIU1(390, 150, "TCL-COMMON-HUAN-ZXYY-SSAN"),
//	ONLINE_MUSIC_SOUSUOANNIU2(390, 150, "TCL-COMMON-HUAN-ZXYY-SSAN2"),
//	ONLINE_MUSIC_SOUSUOANNIU3(390, 150, "TCL-COMMON-HUAN-ZXYY-SSAN3"),
//
//	LAUNCHER_AD_1(512, 128, "TCL-COMMON-HUAN-TCLceshi-DPZY1"),
//	LAUNCHER_AD_2(512, 128, "TCL-COMMON-HUAN-TCLceshi-DPZY2"),
//	
//	CARHOME_AD_720(1280, 720, "TCL-ANDROID-TCL-QCZJZZ-FMGG"),
//	CARHOME_AD_1080(1920, 1080, "TCL-CESHI-FMGG2"),
//	
//	
//	APPSTORE_AD_1(636, 218, "TCL-COMMON-HUAN-APPSTORE-SYGG1"),
//	APPSTORE_AD_2(636, 218, "TCL-COMMON-HUAN-APPSTORE-SYGG2"),
//	APPSTORE_AD_3(636, 218, "TCL-COMMON-HUAN-APPSTORE-SYGG3"),
//	APP_STORE_XZHB(525, 713, "TCL-COMMON-HUAN-APPSTORE-XZHB"),
//	
//	AD_JJDS(257, 100, "TCL-JJDS-TCLDMT-ZJM-ZJMGG"),
//	
//	CLICK_AD_APPSTORE(312, 296, "click-APPSTORE"),
//	CLICK_AD_URL(312, 296, "click-URL"),
//	CLICK_AD_PICTURE(312, 296, "click-picture"),
//	CLICK_AD_PICTURE2(312, 296, "click-picture-2");
//	CLICK_AD_mp4(312, 296, "click-mp4"),
//	CLICK_AD_app(312, 296, "click-app"),
//	CLICK_AD_app2(312, 296, "click-app-2"),
//	CLICK_AD_TEXT(312, 296, "ceshiliantiao-noweb-wzlgg");
	
	
	private int width;
	private int height;
	private String id;
	
	
	/**
	 * 构造方法
	 * 
	 * @author 田亚鹏
	 *
	 * @param width 宽度  height 高度 id 广告位id字符串
	 * 
	 * @return void
	 */
	public AdObject(int width, int height, String id) {
		this.width = width;
		this.height = height;
		this.id = id;
	}
	
	/**
	 * 构造方法
	 * 
	 * @author 田亚鹏
	 *
	 * @param void
	 * 
	 * @return int 获取宽度
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * 构造方法
	 * 
	 * @author 田亚鹏
	 *
	 * @param void
	 * 
	 * @return int 获取高度
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 构造方法
	 * 
	 * @author 田亚鹏
	 *
	 * @param void
	 * 
	 * @return String 获取id字符串
	 */
	public String toString() {
		// TODO Auto-generated method stub
		return id;
	}

}
