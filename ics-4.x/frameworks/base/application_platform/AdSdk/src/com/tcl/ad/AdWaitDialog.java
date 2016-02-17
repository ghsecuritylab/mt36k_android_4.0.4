package com.tcl.ad;


import com.tcl.ad.core.AdUtil;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ProgressBar;



public class AdWaitDialog extends Dialog {
	
	private Context mContext;
	private FrameLayout mFrameLayout;
	private ProgressBar mBar;

	public AdWaitDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = context;
	
		mFrameLayout = new FrameLayout(mContext);
		
		setContentView(mFrameLayout);
		FrameLayout.LayoutParams l = new FrameLayout.LayoutParams(240, 160);
		mFrameLayout.setLayoutParams(l);
		
		FrameLayout.LayoutParams btnLytp = null;
		if(AdUtil.isJJDS)
			btnLytp = new FrameLayout.LayoutParams(112, 112);
		else 
			btnLytp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		btnLytp.gravity = Gravity.CENTER;
		mBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleLarge);
		mFrameLayout.addView(mBar, btnLytp);		
	}
}