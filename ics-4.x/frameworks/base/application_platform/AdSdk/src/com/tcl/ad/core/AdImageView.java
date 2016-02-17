/**
 * 
 */
package com.tcl.ad.core;

import java.io.InputStream;

import com.tcl.ad.core.AdManager.AdClickType;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.10.14
 * 
 * @JDK version: 1.5
 * @brief: Show image ads in ImageView.   
 * @version: v1.0
 *
 */
public class AdImageView extends ImageView {

	private AdManager mAdManager;
	private Paint mFocusablePaint;
	private int height;
	private int width;
	
	public AdImageView(Context context, int w, int h, AdManager adManager) {
		super(context);
		mAdManager = adManager;
		mFocusablePaint = new Paint();
		width = w;
		height = h;
		
		setBackgroundColor(Color.TRANSPARENT);
		setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
		mFocusablePaint.setColor(0xFFFF9200);
	}
	
	@Override
	protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		setMeasuredDimension(width, height);
	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		if (mAdManager.getAdsInformation().getHasLoaded() &&
//				mAdManager.getAdsInformation().getAdsClickType() != AdClickType.NO_ACTION.ordinal() &&
//				ev.getAction() == MotionEvent.ACTION_DOWN) {
//			AdActivity.launchAdActivity(mAdManager);
//		}
//		return super.onTouchEvent(ev);
//	}

//	public synchronized void showImage(InputStream in) {
//		BitmapDrawable bitmapDrawable = AdUtil.getNewImage(in, width, height);
//		if (bitmapDrawable != null)
//			setImageDrawable(bitmapDrawable);
//	}
	
	public synchronized void showImage(String pathName) {
		BitmapDrawable bitmapDrawable = AdUtil.getNewImage(pathName, width, height);
		if (bitmapDrawable != null)
			setImageDrawable(bitmapDrawable);
	}
}
