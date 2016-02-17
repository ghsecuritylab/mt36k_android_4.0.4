package com.tcl.ad;

import android.R.integer;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ImageView.ScaleType;


public class AdPictureDialog1 extends Dialog {
	
	private Context mContext;
	private ImageView mImageView;
	private FrameLayout mFrameLayout;

	public AdPictureDialog1(Context context, int sw, int sh, String path) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = context;
		
//		LayoutParams p = getWindow().getAttributes();  
//		p.height = h; 	
//		p.width = w;
//		getWindow().setAttributes(p);
		Bitmap bm = BitmapFactory.decodeFile(path);
		int w = bm.getWidth();
		int h = bm.getHeight();
		
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
		mImageView.setImageBitmap(bm);
		mFrameLayout.addView(mImageView, btnLytp);
		
	}
}