package com.tcl.ad.core;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.tcl.ad.AdType;
import com.tcl.ad.core.AdManager.AdClickType;


/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.10.21
 * 
 * @JDK version: 1.5
 * @brief: Parser xml document.   
 * @version: v1.0
 *
 */
public class AdsXMLHandler extends DefaultHandler {

	private AdsInformation mAdsInformation;
	private String mCurrentTag;
	
	/**
	 * 
	 */
	public AdsXMLHandler() {
		// TODO Auto-generated constructor stub
		mCurrentTag = null;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		AdsLog.infoLog("AdsXMLHandler startDocument");
		mAdsInformation = new AdsInformation();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		AdsLog.infoLog("AdsXMLHandler startElement " + localName);
		mCurrentTag = localName;
		
		if (localName.equalsIgnoreCase("errors"))
			readErrorsElement(attributes);
		
		if (localName.equalsIgnoreCase("error"))
			readErrorElement(attributes);
		
		if (localName.equalsIgnoreCase("ad"))
			readAdElement(attributes);
		
//		if (localName.equalsIgnoreCase("cornerMark"))
//			readCornerMarkElement(attributes);
		
		if (localName.equalsIgnoreCase("spot"))
			readSpotElement(attributes);
//		
//		if (localName.equalsIgnoreCase("contents"))
//			readContentsElement(attributes);
		
		if (localName.equalsIgnoreCase("content"))
			readContentElement(attributes);
		
//		if (localName.equalsIgnoreCase("click"))
//			readClickElement(attributes);
//		
//		if (localName.equalsIgnoreCase("audio"))
//			readAudioElement(attributes);
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
		AdsLog.infoLog("AdsXMLHandler endDocument");
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		AdsLog.infoLog("AdsXMLHandler endElement " + localName);
		mCurrentTag = null;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		AdsLog.infoLog("AdsXMLHandler characters");
		if (mCurrentTag != null) {
			String data = new String(ch, start, length);
			AdsLog.debugLog("data = " + data);
		}
	}
	
	public AdsInformation getAdsInformation() {
		return mAdsInformation;
	}
	
	private void readErrorsElement(Attributes attributes) {
		if (attributes == null)
			return;
		
		String value = attributes.getValue("type");
		mAdsInformation.setErrorType(value.equalsIgnoreCase("false")? false : true);
	}
	
	private void readErrorElement(Attributes attributes) {
		if (attributes == null)
			return;
		
		String value = attributes.getValue("note");
		mAdsInformation.setErrorNote(value);
		
		value = attributes.getValue("servertime");
		mAdsInformation.setmServerTime(value);
	}
	
	private void readAdElement(Attributes attributes) {
		if (attributes == null)
			return;
		
		//Remove the below type later
		/*
		 * 接口文档：<ad width=”” height=”” class=”” showTime=”-1”> 没发现“Chkid”？
		 */
		String value = attributes.getValue("Chkid");
		mAdsInformation.setChkid(value);
		
		value = attributes.getValue("width");
		
		value = attributes.getValue("height");
		
		value = attributes.getValue("class");
		mAdsInformation.setAdsClass(value);
		
		value = attributes.getValue("showTime");
		mAdsInformation.setAdsShowTime(toInteger(value));
		
		//value = attributes.getValue("location");
		
		value = attributes.getValue("interval");
		mAdsInformation.setAdsShowInterval(toInteger(value));
	}
//	
//	private void readCornerMarkElement(Attributes attributes) {
//		if (attributes == null)
//			return;
//		
//		String value = attributes.getValue("location");
//		
//		value = attributes.getValue("interval");
//		
//	}
	
	private void readSpotElement(Attributes attributes) {
		if (attributes == null)
			return;
//		
//		String value = attributes.getValue("spid");
//		mAdsInformation.setSpotSpid(value);
//		
//		value = attributes.getValue("pid");
//		mAdsInformation.setSpotPid(value);
//		
//		value = attributes.getValue("caid");
//		mAdsInformation.setSpotCaid(value);
		
		String value = attributes.getValue("pvm");
		mAdsInformation.setSpotPvm(value);
		
		value = attributes.getValue("pvtpm");
		mAdsInformation.setSpotPvtpm(value);
	}
	
//	private void readContentsElement(Attributes attributes) {
//		if (attributes == null)
//			return;
//		
//		String value = attributes.getValue("clickm");
//		mAdsInformation.setContentsClickm(value);
//		
//		value = attributes.getValue("clicktpm");
//		mAdsInformation.setmContentsClickptm(value);
//	}
	
	private void readContentElement(Attributes attributes) {
		if (attributes == null)
			return;
		
		AdsLog.infoLog("readContentElement ~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		String value = attributes.getValue("length");
		mAdsInformation.setAdsLength(toInteger(value));
		
		String adsResourceType = attributes.getValue("type");
		mAdsInformation.setAdType(adsResourceType);
		
		value = attributes.getValue("src");
		//"T" 是否已经和客户确定？ 接口文档是这样描述的：物料url；若广告类型为文字，则该属性保存文字内容
		if (mAdsInformation.getAdType() == AdType.AD_TYPE_TEXT) {
			mAdsInformation.setAdsText(value);
		} else {
			mAdsInformation.setAdsPath(value);
		}
		//解析出来的"position"用了吗？ 接口文档是这样描述的： 当对联广告时出现，用来标记物料位于对联广告位的左侧（L）还是右侧（R）
		//value = attributes.getValue("position");
		
		value = attributes.getValue("ldp");
		mAdsInformation.setLdp(value);
		
		value = attributes.getValue("ldpType");
		mAdsInformation.setLdpType(value);
		
		value = attributes.getValue("clickm");
		mAdsInformation.setContentsClickm(value);
		
	}
	
//	private void readClickElement(Attributes attributes) {
//		if (attributes == null)
//			return;
//		
//		String value = attributes.getValue("ldpType");
//		mAdsInformation.setAdsClickType(toClickType(value));
//		
//		value = attributes.getValue("ldp");
//		mAdsInformation.setAdsLink(value);
//	}
	
//	private void readAudioElement(Attributes attributes) {
//		if (attributes == null)
//			return;
//		
//		String value = attributes.getValue("audioUrl");
//
//	}
	
	private int toInteger(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			AdsLog.errorLog("toInteger error");
		}
		return 0;
	}
	
}
