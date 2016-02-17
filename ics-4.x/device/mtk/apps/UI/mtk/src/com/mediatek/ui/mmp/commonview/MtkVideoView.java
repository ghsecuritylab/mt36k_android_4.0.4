package com.mediatek.ui.mmp.commonview;

import java.io.IOException;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.util.Log;
import com.mediatek.ui.R;
import android.graphics.Canvas;
import com.mediatek.mmpcm.video.IPlayback;
import com.mediatek.mmpcm.video.IPlayback.OnPBCompleteListener;
import com.mediatek.mmpcm.videoimpl.VideoConst;
import com.mediatek.mmpcm.videoimpl.VideoManager;
import com.mediatek.ui.mmp.model.MultiFilesManager;
import com.mediatek.ui.mmp.util.LogicManager;
import com.mediatek.ui.util.MtkLog;

public class MtkVideoView extends SurfaceView {
	private static final String TAG = "MtkVideoView";
	private VideoManager mVideoManager;
	private IPlayback mPlayback;
	private Context mContext;
	// Added by Dan for fix bug DTV00375890
	private String mPath;
	private boolean mIsStop;
	private boolean mIsInvalidate;

	public MtkVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		// Set Transparent for listmode
		this.setZOrderOnTop(true);
		this.getHolder().setFormat(PixelFormat.PIXEL_VIDEO_HOLE);
		this.setBackgroundResource(R.drawable.translucent_background);
		this.setVisibility(View.VISIBLE);
	}


	@Override
	public void draw(Canvas canvas) {
		if(!mIsInvalidate){
			mIsInvalidate=true;
			try{
				Canvas canvas1 = this.getHolder().lockCanvas(null);
				this.getHolder().unlockCanvasAndPost(canvas1);
			}catch(Exception e){
				e.printStackTrace();
			}
		}	
			super.draw(canvas);
	}
	
	@Override
	public void invalidate() {
		
		super.invalidate();
		mIsInvalidate=false;
	}
	
	public MtkVideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MtkVideoView(Context context) {
		this(context, null, 0);
	}

	public void init() {
		MultiFilesManager filesManager = MultiFilesManager
				.getInstance(mContext);
		int source = filesManager.getCurrentSourceType();
		switch (source) {
		case MultiFilesManager.SOURCE_LOCAL:
			source = VideoConst.PLAYER_MODE_MMP;
			break;
		case MultiFilesManager.SOURCE_SMB:
			source = VideoConst.PLAYER_MODE_SAMBA;
			break;
		case MultiFilesManager.SOURCE_DLNA:
			source = VideoConst.PLAYER_MODE_DLNA;
			break;
		default:
			break;
		}

		mVideoManager = VideoManager.getInstance(mContext, source);
		mPlayback = mVideoManager.getPlayback();
		mPlayback.setPreviewMode(true);

		mPlayback.setOnPBCompleteListener(mCompleteListener);
	}
	
	public int getVideoPlayStatus() {
		if (null == mPlayback) {
			return VideoConst.PLAY_STATUS_END;
		}
		return mPlayback.getPlayStatus();
	}

	private OnPBCompleteListener mCompleteListener = new OnPBCompleteListener() {

		public void onComplete(IPlayback pb) {
			// Modified by Dan for fix bug DTV00375890
			if (mIsStop) {
				return;
			}

			try {
				if (mPlayback.getPlayStatus() == VideoConst.PLAY_STATUS_STOPPED) {
					mPlayback.setDataSource(mPath);
					mPlayback.play();
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	public void setPreviewMode(boolean model) {
		mPlayback.setPreviewMode(model);
	}

	public boolean isVideoPlaybackInit() {
		return mPlayback == null ? false : true;
	}

	public void play(String path) {
		try {
			// Added by Dan
			mPath = path;
			mPlayback.setDataSource(path);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		playVideo();
		// Added by Dan for fix bug DTV00375890
		mIsStop = false;
	}

	private void playVideo() {
		if (mPlayback == null) {
			return;
		}
		try {
			mPlayback.play();
		} catch (IllegalStateException e) {
			MtkLog.e(TAG, e.getMessage());
		}
	}

	public boolean isStop() {

		return mPlayback == null ? true : false;
	}

	public void stop() {
		if (mPlayback != null) {
			try {
				// Added by Dan for fix bug DTV00375890
				mIsStop = true;

				int status = mPlayback.getPlayStatus();
				if (status == VideoConst.PLAY_STATUS_END) {
					return;
				}
				if (status < VideoConst.PLAY_STATUS_STOPPED) {
					mPlayback.stop();
				}
			} catch (IllegalStateException e) {
				MtkLog.w(TAG, e.getMessage());
			}
		}
	}

	public void onRelease() {

		if (mPlayback != null) {
			try {
				stop();
				mVideoManager.onRelease();
				mPlayback = null;
			} catch (IllegalStateException e) {
				MtkLog.w(TAG, e.getMessage());
			}
		}

		/* video had been close and send broadcast tell it. */
		LogicManager.getInstance(mContext).videoZoomReset();

//		LogicManager.getInstance(mContext).sendCloseBroadCast();
	}
}
