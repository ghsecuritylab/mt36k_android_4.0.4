package com.mediatek.ui.nav.commonview;

import com.mediatek.ui.R;
import com.mediatek.ui.util.MtkLog;
import java.lang.reflect.Field;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class TvView extends ImageView {
	static final String TAG = "TvDialogView";

	private int mScreenWidth;
	private int mScreenHeight;
	private LayoutParams wmParams;
	private WindowManager windowManager;

	public TvView(Context context) {
		super(context);
		windowManager = (WindowManager) context.getApplicationContext()
				.getSystemService(context.WINDOW_SERVICE);
		mScreenWidth = windowManager.getDefaultDisplay().getRawWidth();
		mScreenHeight = windowManager.getDefaultDisplay().getRawHeight();
		wmParams = new LayoutParams();
		wmParams.type = LayoutParams.TYPE_PHONE;
		wmParams.flags |= LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.format = android.graphics.PixelFormat.PIXEL_VIDEO_HOLE;
		windowManager.addView(this, wmParams);
		setBackgroundResource(R.drawable.translucent_background);
	}

	public void setTvViewPosition(float x, float y, float width, float height) {
		wmParams.width = (int) (width * mScreenWidth - x * mScreenWidth);
		wmParams.height = (int) (height * mScreenHeight - y * mScreenHeight);
		wmParams.x = (int) (x * mScreenWidth);
		//if (mScreenWidth == 1280 && mScreenHeight == 720) {
			wmParams.y = (int) (y * mScreenHeight);
		//} else {
		//	wmParams.y = (int) (y * mScreenHeight) - getSystemBarheight();
		//}
		MtkLog.i("OSD", "~~~~~~~~mScrrenWidth:" + mScreenWidth
				+ "~~mScreenHeight:" + mScreenHeight);
		MtkLog.i("OSD", "~~wmParams.width: " + wmParams.width
				+ "~~wmParams.height: " + wmParams.height + "~~wmParams.x: "
				+ wmParams.x + "~~wmParams.y:" + wmParams.y + "~~");
		windowManager.updateViewLayout(this, wmParams);
	}

	public void show(Context context) {
		setVisibility(View.VISIBLE);
	}

	public void hide() {
		setVisibility(View.INVISIBLE);
	}

	public int getSystemBarheight() {
		Class<?> c = null;

		Object obj = null;

		Field field = null;

		int x = 0, sbar = 0;

		try {

			c = Class.forName("com.android.internal.R$dimen");

			obj = c.newInstance();

			field = c.getField("status_bar_height");

			x = Integer.parseInt(field.get(obj).toString());

			sbar = getResources().getDimensionPixelSize(x);
			return sbar;

		} catch (Exception e1) {

			MtkLog.e("tag", "get status bar height fail");

			e1.printStackTrace();
			return 0;

		}

	}

}
