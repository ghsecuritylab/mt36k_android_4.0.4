/**
 * 
 */
package com.tcl.ad.core;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.widget.TextView;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.10.21
 * 
 * @JDK version: 1.5
 * @brief: Show text ads.   
 * @version: v1.0
 *
 */
public class AdTextView extends TextView {

	//private AdManager mAdManager;
	private Paint mFocusablePaint;
	
	public AdTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		//mAdManager = adManager;
		mFocusablePaint = new Paint();
		mFocusablePaint.setColor(0xFFFF9200);
		mFocusablePaint.setAlpha(100);
		setTextSize(30);
		setBackgroundColor(Color.TRANSPARENT);
		//setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
		setFocusable(true);
		//AdsLog.infoLog("AdTextView");
	}
	
//	@Override
//	protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		// TODO Auto-generated method stub
//		AdsLog.infoLog("onMeasure");
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		// TODO Auto-generated method stub
//		AdsLog.debugLog("touch in textView");
//		if (mAdManager.getAdsInformation().getHasLoaded() &&
//				mAdManager.getAdsInformation().getAdsClickType() != AdClickType.NO_ACTION.ordinal()
//				&& ev.getAction() == MotionEvent.ACTION_DOWN) {
//			AdActivity.launchAdActivity(mAdManager);
//		}
//		return super.onTouchEvent(ev);
//	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		canvas.drawLine(0, 0, this.getWidth() - 1, 0, paint);
		canvas.drawLine(0, 0, 0, this.getHeight() - 1, paint);
		canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight() - 1, paint);
		canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, paint);
		
		if (hasFocus()) {
			Paint backgroudPaint = mFocusablePaint;
			canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), backgroudPaint);
		}
	}

	public synchronized void showLinkedText(String adText) {
//		String underlineText = new StringBuilder().append("<u>").append(adText).append("</u>").toString();
//		setText(Html.fromHtml(underlineText));
		
		setText(adText);
		//AdsLog.infoLog("showLinkedText");
	}
//	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		AdsLog.debugLog("event = " + event);
//		if(mAdManager.getAdsInformation().getHasLoaded() &&
//				mAdManager.getAdsInformation().getAdsClickType() != AdClickType.NO_ACTION.ordinal() &&
//				(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction()== KeyEvent.ACTION_DOWN)) {
//			AdActivity.launchAdActivity(mAdManager);
//		}
//		return super.onKeyDown(keyCode, event);
//	}
}
