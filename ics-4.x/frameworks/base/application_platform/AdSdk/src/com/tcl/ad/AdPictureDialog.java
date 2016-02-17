package com.tcl.ad;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;


public class AdPictureDialog extends Dialog {
	
	private Context mContext;
	private ImageView mImageView;
	private FrameLayout mFrameLayout;
	private Bitmap mBitmap;

	public AdPictureDialog(Context context, int sw, int sh, Bitmap b) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = context;

		mBitmap = b;
		int w = mBitmap.getWidth();
		int h = mBitmap.getHeight();
		
		float scale = 1.0f;
		if(w > sw || h > sh) {
			int r = Float.compare(((float)sw)/w, ((float)sh)/h);
			
			if(r >= 0)
				scale = ((float)sw)/w;
			
			if(r < 0)
				scale = ((float)sh)/h;
		}
	
		mFrameLayout = new FrameLayout(mContext);
		
		setContentView(mFrameLayout);
		FrameLayout.LayoutParams l = new FrameLayout.LayoutParams((int)(w*scale), (int)(h*scale));
		mFrameLayout.setLayoutParams(l);
		
		
		FrameLayout.LayoutParams btnLytp = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);

		btnLytp.gravity = Gravity.CENTER;
		mImageView = new ImageView(mContext);
		mImageView.setScaleType(ScaleType.FIT_CENTER);
		mImageView.setImageBitmap(mBitmap);
		mFrameLayout.addView(mImageView, btnLytp);		
	}
}