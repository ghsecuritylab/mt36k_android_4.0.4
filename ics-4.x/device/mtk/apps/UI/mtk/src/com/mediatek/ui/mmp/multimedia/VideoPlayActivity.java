package com.mediatek.ui.mmp.multimedia;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mediatek.media.NotSupportException;
import com.mediatek.mmpcm.mmcimpl.Const;
import com.mediatek.mmpcm.video.IPlayback;
import com.mediatek.mmpcm.video.IPlayback.OnPBCompleteListener;
import com.mediatek.mmpcm.video.IPlayback.OnPBMsgListener;
import com.mediatek.mmpcm.video.IPlayback.OnPBPlayDoneListener;
import com.mediatek.mmpcm.video.IPlayback.OnPBPreparedListener;
import com.mediatek.mmpcm.videoimpl.VideoConst;
import com.mediatek.ui.R;
import com.mediatek.ui.menu.MenuMain;
import com.mediatek.ui.menu.util.MenuConfigManager;
import com.mediatek.ui.mmp.commonview.ControlView.ControlPlayState;
import com.mediatek.ui.mmp.model.MultiFilesManager;
import com.mediatek.ui.mmp.util.LogicManager;
import com.mediatek.ui.mmp.util.MultiMediaConstant;
import com.mediatek.ui.mmp.util.Util;
import com.mediatek.ui.nav.CaptureLogoActivity;
import com.mediatek.ui.util.DestroyApp;
import com.mediatek.ui.util.KeyMap;
import com.mediatek.ui.util.MtkLog;

public class VideoPlayActivity extends MediaPlayActivity {

	private static final String TAG = "VideoPlayActivity";

	private static final int MESSAGE_PLAY = 0;

	private static final int PROGRESS_CHANGED = 1;

	private static final int HIDE_CONTROLER = 2;

	private static final int MSG_UPDATE_CONTROL = 3;

	private static final int MSG_AUDIO_VIDEO_NOT_SUPPORT = 4;
	// //add for bug DTV00379833
	private static final int MSG_VIDEO_NOT_SUPPORT = 18;
	//private boolean VIDEO_NOT_SUPPORT = false;

	private static final int MSG_DISMISS_NOT_SUPPORT = 5;

	private static final int MSG_PLAY_NEXT = 6;

	private static final int MSG_GET_CUR_POS = 7;
	private static final int MSG_CAN_NOT_PAUSE = 8;
	private static final int MSG_CAN_NOT_ZOOM = 9;

	private static final int MSG_CLEAR_CONTROL = 10;
	private static final int PALY_NEXT_DELAYTIME = 3000;

	private static final int DELAYTIME = 1000;

	private static final int HIDE_DELAYTIME = 10000;

	private LinearLayout vLayout;

	private TimeDialog mTimeDialog;
	private VideoDialog mVideoStopDialog;

	private int mVideoSource = 0;

	private boolean isManualStop = false;
	private boolean videoPlayStatus = false;

	private Resources mResources;
	private boolean StepNotSupportShow = false;
	private boolean isActivityLiving = true;

	// when video file don't has time info,the flag is true
	// fix cr DTV00407908 by xiaoyao
	private boolean progressFlag = false;

	public Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

			case MESSAGE_PLAY:
				if (mControlView != null) {
					mControlView.play();
					hideControllerDelay();
				}
				break;
			case PROGRESS_CHANGED: {
				if (progressFlag) {
					break;
				}
				if (mControlView != null) {
					int progress = mLogicManager.getVideoProgress();
					// add by xiaojie fix cr DTV00384273
					String featueNotSupport = getString(R.string.mmp_featue_notsupport);
					if (progress == 0 && mTipsDialog != null
							&& mTipsDialog.isShowing()
							&& mTipsDialog.getTitle().equals(featueNotSupport)) {
						mTipsDialog.dismiss();
					}
					// end
					if (progress >= 0) {
						mControlView.setCurrentTime(progress);
						mControlView.setProgress(progress);
					}
				}
				if (mControlView != null && mLogicManager.getVideoPlayStatus()!=VideoConst.PLAY_STATUS_PAUSED 
						&& mLogicManager.getVideoPlayStatus()>VideoConst.PLAY_STATUS_PREPAREING 
						&& mLogicManager.getVideoPlayStatus()<VideoConst.PLAY_STATUS_STOPPED) {
					sendEmptyMessageDelayed(PROGRESS_CHANGED, DELAYTIME);
				}
				break;
			}
			case HIDE_CONTROLER: {
				//add by keke 2.28
				if (menuDialog != null && menuDialog.isShowing()) {
					if(mHandler.hasMessages(HIDE_CONTROLER)){
						mHandler.removeMessages(HIDE_CONTROLER);
					}
					sendEmptyMessageDelayed(HIDE_CONTROLER, PALY_NEXT_DELAYTIME);
					break;
				}
				hideController();
				break;
			}
			case MSG_UPDATE_CONTROL: {
				startUpdate();
				break;
			}
			case MSG_AUDIO_VIDEO_NOT_SUPPORT: {

				if (null != mTipsDialog && mTipsDialog.isShowing()) {
					mTipsDialog.dismiss();
				}
				mLogicManager.playNextVideo();
				break;
			}
			case MSG_DISMISS_NOT_SUPPORT: {
				if(isActivityLiving){								
					dismissNotSupprot();
					if (menuDialog != null && menuDialog.isShowing()) {
						menuDialog.dismiss();
					}
					}
				break;
			}
			case MSG_PLAY_NEXT: {
				mLogicManager.playNextVideo();
				break;
			}
			case MSG_GET_CUR_POS: {
				// MtkLog.i(TAG, " -----  MSG_GET_CUR_POS -- ------");
				// fix cr DTV00407908 by xiaoyao
				progressFlag = true;
				if (mControlView != null) {
					long pos = mLogicManager.getVideoCurFilePosition();
					if (mLargeFile) {
						pos = pos >> RATE;
					}
					if (pos > 0)
					mControlView.setProgress((int) pos);
				}
				break;
			}
				// //add for bug DTV00379833
			case MSG_VIDEO_NOT_SUPPORT: {

				VIDEO_NOT_SUPPORT = true;
				break;
			}
			case MSG_CAN_NOT_PAUSE:
				mHandler.post(new Runnable() {
					public void run() {
							if (menuDialog != null && menuDialog.isShowing()) {
								menuDialog.dismiss();
							}
							onNotSuppsort(getResources().getString(
									R.string.mmp_featue_notsupport));
					}
				});
				mControlView.reSetVideo();
				mControlImp.play();
				break;
			case MSG_CAN_NOT_ZOOM:
				update();
				break;
			case MSG_CLEAR_CONTROL:
				if (mControlView != null) {
					mControlView.setInforbarNull();
				}
				if (null != mInfo && mInfo.isShowing()) {
					mInfo.setVideoView();
				}
				break;
			default:
				break;
			}

		}

	};

	
	/**
	 * Remove to get progress inforamtion and time information message.
	 */
	protected void removeProgressMessage(){
		mHandler.removeMessages(PROGRESS_CHANGED);
	}
	/**
	 * Add to get progress inforamtion and time information message
	 */
	protected void addProgressMessage(){
		mHandler.sendEmptyMessage(PROGRESS_CHANGED);
	}
	
	private ControlPlayState mControlImp = new ControlPlayState() {

		public void play() {
			mLogicManager.playVideo();
			if (mControlView.isTimeViewVisiable()) {
				mHandler.sendEmptyMessage(PROGRESS_CHANGED);
			}
		}

		public void pause() {
			try {
				if(mLogicManager!=null){
					mLogicManager.pauseVideo();
				}
				if(mControlView!=null){
					mControlView.reSetPause();
				}
			} catch (Exception e) {
				if ((VideoConst.MSG_ERR_CANNOTPAUSE).equals(e.getMessage().toString()) ) {	
					mHandler.sendEmptyMessage(MSG_CAN_NOT_PAUSE);
					throw new IllegalStateException(
							VideoConst.MSG_ERR_CANNOTPAUSE);					
				}else if (VideoConst.MSG_ERR_PAUSEEXCEPTION.equals(e.getMessage().toString())) {
					mHandler.sendEmptyMessage(MSG_CAN_NOT_PAUSE);
					throw new IllegalStateException(
							VideoConst.MSG_ERR_PAUSEEXCEPTION);
				}else{
					mHandler.sendEmptyMessage(MSG_CAN_NOT_PAUSE);
					throw new IllegalStateException(e.getMessage());
				}
				
			}
			//sync 2.2
			/*if (mHandler.hasMessages(PROGRESS_CHANGED)) {
				mHandler.removeMessages(PROGRESS_CHANGED);
			}*/
			mHandler.sendEmptyMessage(PROGRESS_CHANGED);
			//end
		}
	};

	private OnPBMsgListener mMessageListener = new OnPBMsgListener() {

		public void onMsg(int msg) {

			switch (msg) {
			case VideoConst.MSG_FILE_CORRUPT: {
				MtkLog.i(TAG, "------  MSG_FILE_CORRUPT  -------");
				setIsFileNotSupport(true);
				isNotSupport = true;
				onNotSuppsort(mResources.getString(R.string.mmp_file_corrupt));
				mHandler.sendEmptyMessageDelayed(MSG_PLAY_NEXT, DELAYTIME);
				break;
			}
			case VideoConst.MSG_AV_NOT_SUPPORT: {

				MtkLog.i(TAG, "------  AV NOT SUPPORT  -------");
				isNotSupport = true;
				final String title = mResources
						.getString(R.string.mmp_audio_notsupport)
						+ "\n"
						+ mResources.getString(R.string.mmp_video_notsupport);
				// need handler post
				mHandler.post(new Runnable() {
					public void run() {
						if (mControlView != null) {
							mControlView.setZoomEmpty();
						}
						onNotSuppsort(title);
					}
				});

				mHandler.sendEmptyMessageDelayed(MSG_AUDIO_VIDEO_NOT_SUPPORT,
						PALY_NEXT_DELAYTIME);
				break;
			}
			case VideoConst.MSG_PLAY_START: {
				MtkLog.i(TAG, "------  MSG_PLAY_START_0  -------");
				if(isSetPicture){
					MenuMain.getInstance().finish();
					isSetPicture = false;
				}
				//add by keke
				isAudioNotSupport=false;
				// add by shuming for fix bug DTV00379833
				VIDEO_NOT_SUPPORT = false;
				//update();
				mHandler.sendEmptyMessage(MSG_UPDATE_CONTROL);
				break;
			}
			//add by shuming CR DTV00385173
			case VideoConst.MSG_RESET_VIDEO_ZOOM_MODE:{
				mLogicManager.videoZoom( VideoConst.VOUT_ZOOM_TYPE_1X );
				break;
			}
			case VideoConst.MSG_PLAYER_NOT_PREPARED: {
				MtkLog.i(TAG, " -----  MSG_PLAYER_NOT_PREPARED -- ------");
				break;
			}
			case VideoConst.MSG_SET_SOURCE_FAIL: {
				MtkLog.i(TAG, " -----  MSG_SET_SOURCE_FAIL -- ------");
				// onNotSuppsort(PhotoPlayActivity.this.getString(R.string))
				break;
			}
			case VideoConst.MSG_IS_PLAYING: {
				MtkLog.i(TAG, " -----  MSG_IS_PLAYING -- ------");
				break;
			}
			case VideoConst.MSG_SET_DATA_SOURCE_FAIL: {
				MtkLog.i(TAG, " -----  MSG_SET_DATA_SOURCE_FAIL -- ------");
				break;
			}
			case VideoConst.MSG_SEEK_NOT_SUPPORT: {
				MtkLog.i(TAG, " -----  MSG_SEEK_NOT_SUPPORT -- ------");
				// modifed by keke for fix DTV00380161
				featureNotWork(VideoPlayActivity.this
						.getString(R.string.mmp_seek_notsupport));
				break;
			}
			case VideoConst.MSG_PLAY_START_FAIL: {
				MtkLog.i(TAG, " -----  MSG_PLAY_START_FAIL -- ------");
				break;
			}
			case VideoConst.MSG_STEP_NOT_SUPPORT: {
				MtkLog.i(TAG, " -----  MSG_STEP_NOT_SUPPORT -- ------");
				mHandler.post(new Runnable() {
					public void run() {
						// modifed by keke for fix DTV00379169
						featureNotWork(VideoPlayActivity.this
								.getString(R.string.mmp_step_notsupport));
						StepNotSupportShow = true;
					}
				});
				break;
			}
			case VideoConst.MSG_NOT_SUPPORT: {
				MtkLog.i(TAG, " -----  MSG_NOT_SUPPORT -- ------");
				featureNotWork(VideoPlayActivity.this
						.getString(R.string.mmp_featue_notsupport));
				break;
			}
			case VideoConst.MSG_RETRY_PROGRAM:
				if (mTipsDialog != null && mTipsDialog.isShowing()) {
					isAudioNotSupport = false;
					VIDEO_NOT_SUPPORT = false;
					mTipsDialog.dismiss();
				}
				break;
			case VideoConst.MSG_AUDIO_NOT_SUPPORT: {
				MtkLog.i(TAG, " -----  MSG_AUDIO_NOT_SUPPORT -- ------");
				// isNotSupport = true;
				isAudioNotSupport = true;
				if(isActivityLiving){
					
				
				mHandler.post(new Runnable() {
					public void run() {
						if(isAudioNotSupport){
							onNotSuppsort(mResources
									.getString(R.string.mmp_audio_notsupport));
						}
					}
				});
				}
				break;
			}
			case VideoConst.MSG_VIDEO_NOT_SUPPORT: {
				MtkLog.i(TAG, " -----  MSG_VIDEO_NOT_SUPPORT -- ------");
				//isNotSupport = true;
				// //add for bug DTV00379833
				mHandler.sendEmptyMessage(MSG_VIDEO_NOT_SUPPORT);
				if(isActivityLiving){
				mHandler.post(new Runnable() {
					public void run() {
						if(VIDEO_NOT_SUPPORT){
							onNotSuppsort(mResources
									.getString(R.string.mmp_video_notsupport));
						}
					}
				});
				}
				// onNotSuppsort(mResources
				// .getString(R.string.mmp_video_notsupport));
				break;
			}

			case VideoConst.MSG_FILE_NOT_SUPPORT: {
				MtkLog.i(TAG, " -----  MSG_FILE_NOT_SUPPORT -- ------");
				setIsFileNotSupport(true);
				isNotSupport=true;
				//set inforbarNull when press "CH+/-" and file not support
				mControlView.setInforbarNull();
				onNotSuppsort(VideoPlayActivity.this
						.getString(R.string.mmp_file_notsupport));
				mHandler.sendEmptyMessageDelayed(MSG_PLAY_NEXT, DELAYTIME);
				break;
			}
			case VideoConst.MSG_INVALID_STATE: {
				MtkLog.i(TAG, " -----  MSG_INVALID_STATE -- ------");
				break;
			}
			case VideoConst.MSG_INPUT_STREAM_FAIL: {
				MtkLog.i(TAG, " -----  MSG_INPUT_STREAM_FAIL -- ------");
				break;
			}
			case VideoConst.MSG_SOURCE_NOT_PREPARED: {
				MtkLog.i(TAG, " -----  MSG_SOURCE_NOT_PREPARED -- ------");
				break;
			}
			case VideoConst.MSG_POSITION_UPDATE: {
				MtkLog.i(TAG, " -----  MSG_POSITION_UPDATE -- ------");
				if (mHandler.hasMessages(PROGRESS_CHANGED)) {
					mHandler.removeMessages(PROGRESS_CHANGED);
				}
				mHandler.sendEmptyMessage(MSG_GET_CUR_POS);
				break;
			}
			case VideoConst.MSG_STEP_DONE: {
				MtkLog.i(TAG, " -----  MSG_STEP_DONE -- ------");
				if (isControlBarShow) {
					mHandler.sendEmptyMessage(PROGRESS_CHANGED);
				}
				break;
			}
			case VideoConst.MSG_SEEK_DONE: {
				MtkLog.i(TAG, " -----  MSG_SEEK_DONE -- ------");
				if (isControlBarShow) {
					mHandler.sendEmptyMessage(PROGRESS_CHANGED);
				}
				break;
			}
			case VideoConst.MSG_FILE_AUDIOONLY: {
				VIDEO_NOT_SUPPORT = true;
				if (isActivityLiving) {
					mHandler.post(new Runnable() {
						public void run() {
							onNotSuppsort(mResources
									.getString(R.string.mmp_video_notsupport));
						}
					});
				}
				break;
			}	
			default:
				break;
			}

		}
	};

	// private void onVideoNotSupport() {
	// isVideoNotSupport = true;
	// if (isAudioNotSupport && isVideoNotSupport) {
	// mHandler.removeMessages(MSG_AUDIO_VIDEO_NOT_SUPPORT);
	// mHandler.sendEmptyMessageDelayed(MSG_AUDIO_VIDEO_NOT_SUPPORT,
	// PALY_NEXT_DELAYTIME);
	// }
	// String title = mResources.getString(R.string.mmp_video_notsupport);
	// if (null != mTipsDialog && mTipsDialog.isShowing()) {
	// mTipsDialog.dismiss();
	// if (isAudioNotSupport) {
	// title = mResources.getString(R.string.mmp_audio_notsupport)
	// + "\n" + title;
	// }
	// }
	// removeFeatureMessage();
	// onNotSuppsort(title);
	// }
	//
	// private void onAudioNotSupport() {
	// isAudioNotSupport = true;
	// if (isAudioNotSupport && isVideoNotSupport) {
	// mHandler.removeMessages(MSG_AUDIO_VIDEO_NOT_SUPPORT);
	// mHandler.sendEmptyMessageDelayed(MSG_AUDIO_VIDEO_NOT_SUPPORT,
	// PALY_NEXT_DELAYTIME);
	// }
	//
	// String title = mResources.getString(R.string.mmp_audio_notsupport);
	// if (null != mTipsDialog && mTipsDialog.isShowing()) {
	// mTipsDialog.dismiss();
	// if (isVideoNotSupport) {
	// title = title + "\n"
	// + mResources.getString(R.string.mmp_video_notsupport);
	// }
	// }
	// removeFeatureMessage();
	// onNotSuppsort(title);
	// }

	private OnPBPlayDoneListener mPlayDoneListener = new OnPBPlayDoneListener() {

		public void onPlayDone(IPlayback pb) {

			MtkLog.i(TAG, " OnPBPlayDoneListener  execute");
			mHandler.sendEmptyMessage(MSG_CAN_NOT_ZOOM);

		}
	};

	static private int MAX_VALUE = 2147483647;
	static private int RATE = 2;
	static private int BASE = 31;
	private boolean mLargeFile = false;

	private boolean isLargeFile(long size) {
		long multiple = 1;
		RATE = 2;
		if (size > MAX_VALUE) {
			multiple = size >> BASE;
			while (true) {
				switch ((int) multiple) {
				case 1:
				case 2:
				case 3:
					return true;
				default:
					multiple = multiple >> 1;
					RATE += 1;
					break;
				}
			}
		}
		return false;
	}

	private void startUpdate(){
		
		if (menuDialog != null && menuDialog.isShowing()) {
			menuDialog.dismiss();
		}
		
		if (mControlView != null) {
			mControlView.setInforbarNull();
			mControlView.setVolumeMax(maxVolume);
			mControlView.setCurrentVolume(currentVolume);
			mControlView.setFileName(mLogicManager.getFileName());
			mControlView.setFilePosition(mLogicManager.getVideoPageSize());
			mControlView.reSetVideo();

		}
		if (null != mInfo && mInfo.isShowing()) {
			mInfo.setVideoView();
		}
	}

	private void update() {
		
		if (mControlView != null) {
			int i = mLogicManager.getVideoDuration();
			long size = 0;
			mLargeFile = false;
			if (i <= 0) {
				MtkLog.i(TAG, " update  execute************* i = 0");
				size = mLogicManager.getVideoFileSize();
				MtkLog.i(TAG, " update  execute************* i = " + i);
				mControlView.setVisibility(false);
				mLargeFile = isLargeFile(size);

				size = (size > MAX_VALUE) ? size >> RATE : size;

				i = (int) size;
			} else {
				mControlView.setVisibility(true);
			}
			i = (i > 0 ? i : 0);
			mControlView.setProgressMax(i);
			mControlView.setEndtime(i);
			mControlView.initVideoTrackNumber();
		
			mControlView.initSubtitle(mLogicManager.getSubtitleTrackNumber());
			//mLogicManager.videoZoomReset();
			mLogicManager.videoZoom( VideoConst.VOUT_ZOOM_TYPE_1X );
			MtkLog.i(TAG, "width:" + mLogicManager.getVideoWidth() + "heght:"
					+ mLogicManager.getVideoHeight());
			// Modified by Dan and keke for fix bug DTV00380300 and DTV00388193
			 if (VIDEO_NOT_SUPPORT||mLogicManager.getVideoWidth() <= 0
			 || mLogicManager.getVideoHeight() <= 0) {
				 SCREENMODE_NOT_SUPPORT = true;
				 mControlView.setZoomEmpty();
			 } else {
				 SCREENMODE_NOT_SUPPORT = false;
				 mControlView.setZoomSize();
			 }
		}
		if (null != mInfo && mInfo.isShowing()) {
			mInfo.setVideoView();
		}
	}

	private OnPBPreparedListener preparedListener = new OnPBPreparedListener() {

		public void onPrepared(IPlayback arg0) {

			MtkLog.i(TAG, "---------OnPBPreparedListener -----------------");

			isNotSupport = false;
			isAudioNotSupport=false;
			progressFlag = false;
			removeFeatureMessage();
			mHandler.sendEmptyMessage(MSG_DISMISS_NOT_SUPPORT);
			if (isControlBarShow) {
				mHandler.sendEmptyMessage(PROGRESS_CHANGED);
			}
			mHandler.sendEmptyMessage(MSG_CLEAR_CONTROL);
		
			setIsFileNotSupport(false);
			if (null != mTimeDialog && mTimeDialog.isShowing()) {
				mTimeDialog.dismiss();
			}

		}
	};
	private OnPBCompleteListener completeListener = new OnPBCompleteListener() {

		public void onComplete(IPlayback pb) {
			if (pb.isEnd()) {
				if (!isManualStop) {
					VideoPlayActivity.this.finish();
				}
			}else
			{
				//TODO..
				
				removeFeatureMessage();
				dismissNotSupprot();
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mmp_videoplay);
		findView();
		getIntentData();
		init();
		mControlView.setVisibility(false);
		mControlView.setRepeatVisibility(Const.FILTER_VIDEO);
		showPopUpWindow(vLayout);
		mResources = getResources();
		mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY, DELAYTIME);

	}

	protected void onResume() {
		super.onResume();
		if (mControlView != null && !isControlBarShow) {
			showController();
		}
		isSetPicture = false;
		// keke change
		if (isBackFromCapture) {
			if (videoPlayStatus) {
				if(null != mControlView){
					mControlView.play();
				}
				videoPlayStatus = false;
			}
			isBackFromCapture = false;
		}
	}

	protected void onStart() {
		super.onStart();
	}

	private void init() {
		mLogicManager = LogicManager.getInstance(this);
		mLogicManager.initVideo(this, mVideoSource);
		mLogicManager.setPreparedListener(preparedListener);
		mLogicManager.setCompleteListener(completeListener);
		mLogicManager.setOnPBMsgListener(mMessageListener);
		mLogicManager.setOnPBPlayDoneListener(mPlayDoneListener);
		// mLogicManager.setVideoFileSize(mLogicManager.getVideoFileSize());
		//add by shuming for fix bug DTV00385698
		mLogicManager.setVideoFeaturenotsupport(true);
		
		//end
		initVulume(mLogicManager);
	}

	/**
	 * 
	 */
	private void getIntentData() {
		mVideoSource = MultiFilesManager.getInstance(this)
				.getCurrentSourceType();

		switch (mVideoSource) {
		case MultiFilesManager.SOURCE_LOCAL:
			mVideoSource = VideoConst.PLAYER_MODE_MMP;
			break;
		case MultiFilesManager.SOURCE_SMB:
			mVideoSource = VideoConst.PLAYER_MODE_SAMBA;
			break;
		case MultiFilesManager.SOURCE_DLNA:
			mVideoSource = VideoConst.PLAYER_MODE_DLNA;
			break;
		default:
			break;
		}
	}

	private void findView() {
		vLayout = (LinearLayout) findViewById(R.id.mmp_video);
		getPopView(R.layout.mmp_popupvideo, MultiMediaConstant.VIDEO,
				mControlImp);
		mControlView.setFilePosition(mLogicManager.getVideoPageSize());
	}

	// Added by yongzheng 20111212 for fix bug DTV00379472
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keycode = event.getKeyCode();
		switch (keycode) {
		case KeyMap.KEYCODE_MTKIR_NEXT:
		case KeyMap.KEYCODE_MTKIR_PREVIOUS:
			return true;
		default:
			break;
		}

		return super.dispatchKeyEvent(event);
	}

	// end

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		MtkLog.d("keke", TAG + "------onKeyDown" + keyCode);
		if (mTimeDialog != null && mTimeDialog.isShowing()) {
			mTimeDialog.onKeyDown(keyCode, event);
			return true;
		}
		mHandler.removeMessages(HIDE_CONTROLER);
		MtkLog.d("keke", TAG + "------onKeyDown" + keyCode);

		if (!getBlueDialogStats()) {
			switch (keyCode) {
			// Added by Dan 20111118 for fix bug DTV00373225
			case KeyMap.KEYCODE_DPAD_CENTER:
//				if (isNotSupport) {
//					return true;
//				}
//				mControlView.reSetPause();
				if (mLogicManager.getVideoPlayStatus() < VideoConst.PLAY_STATUS_PLAYED) {
					reSetController();
					featureNotWork(getString(R.string.mmp_featue_notsupport));
					return true;
				}

				if (StepNotSupportShow && mTipsDialog != null) {
					mTipsDialog.dismiss();
				}
				String featueNotSupport = this.getResources().getString(
						R.string.mmp_featue_notsupport);
				if (mTipsDialog != null
						&& mTipsDialog.isShowing()
						&& mTipsDialog.getTitle().toString().equals(
								featueNotSupport)) {
					//modified by keke 2.1 for DTV00392725
					//mTipsDialog.dismiss(); 
					hideFeatureNotWork();
				}
				break;

			case KeyMap.KEYCODE_MTKIR_PLAYPAUSE:
				if (!mControlView.isPalying() && isValid()) {
					if (!mHandler.hasMessages(PROGRESS_CHANGED)) {
						mHandler.sendEmptyMessage(PROGRESS_CHANGED);
					}
					reSetController();
					try {
						mLogicManager.stepVideo();
					} catch (Exception e) {
						MtkLog.d(TAG, e.getMessage());
						featureNotWork(getString(R.string.mmp_featue_notsupport));
						return true;
					}
				}

				return true;
			case KeyMap.KEYCODE_MTKIR_CHDN:
				if (isValid()) {
					reSetController();
					mLogicManager.playPrevVideo();
				}
				return true;
			case KeyMap.KEYCODE_MTKIR_CHUP:
				if (isValid()) {
					reSetController();
					mLogicManager.playNextVideo();
				}
				return true;
			case KeyMap.KEYCODE_MTKIR_TIMER: {
				//xudong add "&& mLogicManager.getVideoDuration() > 0" for CR DTV00385306
				if (isValid() && mLogicManager.getVideoDuration() > 0) {
					reSetController();
					
					// fix cr DTV00406630 by xiaoyao
					if (mLogicManager.getVideoWidth() <= 0
							|| mLogicManager.getVideoHeight() <= 0 || !mLogicManager.canDoSeek()) {
						mMessageListener.onMsg(VideoConst.MSG_SEEK_NOT_SUPPORT);
						return true;
					}
						
					if (null == mTimeDialog) {
						mTimeDialog = new TimeDialog(VideoPlayActivity.this);
					}
					mTimeDialog.show();
				}
				return true;
			}
			case KeyMap.KEYCODE_MENU: {
				reSetController();
				//remove by keke 2.28
				//mHandler.removeMessages(HIDE_CONTROLER);
				break;
			}
			case KeyMap.KEYCODE_MTKIR_FASTFORWARD: {
				// Added by Dan 20111118 for fix bug DTV00373225
				if (mLogicManager.getVideoPlayStatus() < VideoConst.PLAY_STATUS_PLAYED) {
					reSetController();
					featureNotWork(getString(R.string.mmp_featue_notsupport));
					return true;
				}

				if (isValid()) {
					reSetController();
					try {
						mLogicManager.fastForwardVideo();
					} catch (NotSupportException e) {
						MtkLog.d(TAG, e.getMessage());
						featureNotWork(getString(R.string.mmp_featue_notsupport));
						return true;
					}
					setFast(0);
				}
				return true;
			}
			case KeyMap.KEYCODE_MTKIR_REWIND: {
				if (isValid()) {
					reSetController();
					try {
						mLogicManager.fastRewindVideo();
					} catch (NotSupportException e) {
						MtkLog.d(TAG, e.getMessage());
						//add by shuming for fix bug DTV00385698
						if (mLogicManager.isVideoFeaturenotsurport()) {							
							featureNotWork(getString(R.string.mmp_featue_notsupport));
						}
						mLogicManager.setVideoFeaturenotsupport(true);
						//end
						return true;
					}
					setFast(1);
				}
				return true;
			}
			case KeyMap.KEYCODE_MTKIR_MTSAUDIO: {
				if (isValid()) {
					reSetController();
					if (null != mControlView) {
						mControlView.changeVideoTrackNumber();
					}
					//Whether to support the state of switch tracks initialize sound
					if (mTipsDialog != null && isAudioNotSupport) {
						dismissNotSupprot();
						isAudioNotSupport = false;
					}
				}
				return true;
			}
			case KeyMap.KEYCODE_MTKIR_REPEAT: {
				reSetController();
				onRepeat();
				return true;
			}
			case KeyMap.KEYCODE_MTKIR_EJECT: {
				if (isValid()) {
					reSetController();
					try {
						mLogicManager.slowForwardVideo();
					} catch (NotSupportException e) {
					/*Fix cr DTV00390785 by lei add*/
						featureNotWork(getString(R.string.mmp_featue_notsupport));
						return true;
					}
					
					setFast(2);
				}
				return true;
			}
			case KeyMap.KEYCODE_MTKIR_STOP: {
				// add by keke 1203 for fix DTV00379270
				if (mLogicManager.getVideoPlayStatus() < VideoConst.PLAY_STATUS_PLAYED) {
					featureNotWork(getString(R.string.mmp_featue_notsupport));
					return true;
				}
				if (StepNotSupportShow && mTipsDialog != null) {
					mTipsDialog.dismiss();
				}
				if ( mInfo != null && mInfo.isShowing()) {
					mInfo.dismiss();
				}
				dismissTimeDialog();
				// updata by keke 1203 for fix DTV00379782
				if (isValid()) {
					reSetController();
					if (isNotSupport || VIDEO_NOT_SUPPORT) {
						if (null != mTipsDialog && mTipsDialog.isShowing()) {
							showFullSotpStatus();
							// fix cr DTV00407010 by xiaoyao
							removeFeatureMessage();
						}
					} else {
						showResumeDialog();
					}
				}
				if (isAudioNotSupport || VIDEO_NOT_SUPPORT) {
					return true;
				}
				return true;
			}
			case KeyMap.KEYCODE_BACK: {
				mLogicManager.finishVideo();
				removeControlView();
				dismissTimeDialog();
				dismissNotSupprot();
				finish();
				break;
			}
			case KeyMap.KEYCODE_MTKIR_MTKIR_CC: {
				if (isValid()) {
					if (null != mControlView) {
						short index = (short) (mControlView.getSubtitleIndex() + 1);
						short number = mLogicManager.getSubtitleTrackNumber();
						if (number <= 0) {
							return true;
						}
						reSetController();
						// off
						if (index >= number) {
							index = -1;
						}
						mControlView.setVideoSubtitle(number, index);
					}
				}
				return true;
			}
			case KeyMap.KEYCODE_MTKIR_ZOOM: {
				// add by shuming for fix bug DTV00379833
				if (mLogicManager.getVideoPlayStatus() < VideoConst.PLAY_STATUS_PLAYED
						|| mLogicManager.getMaxZoom() == VideoConst.VOUT_ZOOM_TYPE_1X) {
					reSetController();
					featureNotWork(getString(R.string.mmp_featue_notsupport));
					return true;
				}
				// end
				//add by keke 1.5 for cr DTV00388193
				if (mLogicManager.getVideoWidth() <= 0
						|| mLogicManager.getVideoHeight() <= 0) {
					reSetController();
					featureNotWork(getString(R.string.mmp_featue_notsupport));
					return true;
				}
				//end
				if (isValid()) {
					if (VIDEO_NOT_SUPPORT == true) {
						// Modified by Dan for fix bug DTV00381031
						break;
					}

					reSetController();
					int zoomType = mLogicManager.getCurZomm();
					if (zoomType >= VideoConst.VOUT_ZOOM_TYPE_1X
							&& zoomType < mLogicManager.getMaxZoom()) {
						zoomType++;
					} else {
						zoomType = VideoConst.VOUT_ZOOM_TYPE_1X;
					}
					mLogicManager.videoZoom(zoomType);
					if (null != mControlView) {
						mControlView.setZoomSize();
					}
				}
				return true;
			}
			case KeyMap.KEYCODE_MTKIR_RECORD: {
				
				if (isNotSupport) {
					// modified by keke 1205 for fix bug DTV00379829
					featureNotWork(getString(R.string.mmp_featue_notsupport));
					break;
				}else if(MenuConfigManager.getInstance(this).getDefault(MenuConfigManager.VIDEO_3D_MODE) != 0){
					featureNotWork(getString(R.string.mmp_featue_notsupport));
				}else{
	
					// Added by Dan 20111118 for fix bug DTV00379266
					/*modified by keke 1.13 for DTV00390942 only played and paused status
					can Screenshots*/
					int palystatus=mLogicManager.getVideoPlayStatus();
					if (palystatus == VideoConst.PLAY_STATUS_PLAYED
							|| palystatus == VideoConst.PLAY_STATUS_PAUSED
							|| palystatus == VideoConst.PLAY_STATUS_STEP) {
					}else{
						reSetController();
						featureNotWork(getString(R.string.mmp_featue_notsupport));
						return true;
					}
	
					// modified by keke 1206 for fix DTV00379829
					if (mLogicManager.getVideoWidth() <= 0
							|| mLogicManager.getVideoHeight() <= 0) {
						featureNotWork(getString(R.string.mmp_featue_notsupport));
						return true;
					}
	
					int speed = mLogicManager.getVideoSpeed();
					if (speed > 1) {
						featureNotWork(getString(R.string.mmp_featue_notsupport));
						return true;
					}
	
					reSetController();
					
					// Deleted by Dan for fix Bug DTV00388170
					// add by xiaojie fix cr DTV00384039
	//				int progessTime = mLogicManager.getVideoProgress();
	//				if (progessTime < 1000){
	//					return true;
	//				}
					//end
					
					/*//add by xiaojie fix cr DTV00381248
					if (mTipsDialog != null && mTipsDialog.isShowing()) {
						mTipsDialog.dismiss();
					}*/
					
					//modified by keke for DTV00384207
	                hideFeatureNotWork();
					// keke change
					// fix cr DTV00407913 by xiaoyao
	
					if (mControlView.isPalying()
							|| palystatus == VideoConst.PLAY_STATUS_PAUSED
							|| palystatus == VideoConst.PLAY_STATUS_STEP) {
						if (mControlView.isPalying()){
								mControlView.onCapture();
						
						videoPlayStatus = true;
						}else {
						videoPlayStatus = false;
					}
					hideController();
					Intent intent = new Intent(this, CaptureLogoActivity.class);
					intent.putExtra(CaptureLogoActivity.FROM_MMP,
							CaptureLogoActivity.MMP_VIDEO);
					startActivity(intent);
					isBackFromCapture = true;
					} 
					
					
					return true;
				}
			}
				// Remove by yongzheng 20111212 for fix bug DTV00379472
				/*
				 * case KeyMap.KEYCODE_MTKIR_NEXT: case
				 * KeyMap.KEYCODE_MTKIR_PREVIOUS: return true;
				 */
			default:
				break;
			}
			return super.onKeyDown(keyCode, event);
		} else {
			switch (keyCode) {
			// Added by Dan 20111118 for fix bug DTV00373225
			case KeyMap.KEYCODE_DPAD_CENTER:
				if (isManualStop) {
					setBlueDialogStats(false);
					reSetController();
					isManualStop = false;
					if (mLogicManager.replayVideo() != 0) {
						finish();
					}

				}
				return true;
			case KeyMap.KEYCODE_MTKIR_CHDN:
				if (isValid()) {
					isManualStop = false;
					reSetController();
					setBlueDialogStats(false);
					if (mLogicManager.playPrevVideo() != 0) {
						finish();
					}

				}
				return true;
			case KeyMap.KEYCODE_MTKIR_CHUP:
				if (isValid()) {
					isManualStop = false;
					reSetController();
					setBlueDialogStats(false);
					if (mLogicManager.playNextVideo() != 0) {
						finish();
					}
				}
				return true;
			case KeyMap.KEYCODE_MTKIR_STOP:
				if (isValid())
					reSetController();
				return true;
			case KeyMap.KEYCODE_MENU: {
				reSetController();
				mHandler.removeMessages(HIDE_CONTROLER);
				break;
			}
			case KeyMap.KEYCODE_BACK: {
				mLogicManager.finishVideo();
				removeControlView();
				dismissTimeDialog();
				dismissNotSupprot();
				finish();
				break;
			}
			case KeyMap.KEYCODE_MTKIR_REPEAT:
				return true;
			/*by lei add*/
			case KeyMap.KEYCODE_MTKIR_PLAYPAUSE:
				return true;
				
			default:
				break;
			}
			return super.onKeyDown(keyCode, event);
		}
	}

	private void dismissTimeDialog() {
		if (null != mTimeDialog && mTimeDialog.isShowing()) {
			mTimeDialog.dismiss();
		}
	}

	private void showFullSotpStatus() {
		mControlView.stop();
		isManualStop = true;
		isNotSupport = false;
		setIsFileNotSupport(false);
		setBlueDialogStats(true);
		mLogicManager.stopVideo();
		mControlView.setInforbarNull();
		dismissNotSupprot();
	}

	private void showResumeDialog() {

		if (mLogicManager.getVideoPlayStatus()!=VideoConst.PLAY_STATUS_PAUSED) {
		   mControlView.pause();
		}
		if(mLogicManager.getVideoPlayStatus()==VideoConst.PLAY_STATUS_PAUSED){
			
				
				mVideoStopDialog = new VideoDialog(this);
				mVideoStopDialog.show();
				WindowManager m = mVideoStopDialog.getWindow().getWindowManager();
				Display display = m.getDefaultDisplay();
			mVideoStopDialog.setDialogParams(display.getRawWidth(), display
					.getRawHeight());
			mVideoStopDialog.setOnDismissListener(mDismissListener);
			// modified by keke for fix DTV00380309
			hideController();
		}

	}

	private OnDismissListener mDismissListener = new OnDismissListener() {

		public void onDismiss(DialogInterface dialog) {
			if (getBlueDialogStats()) {
				mControlView.stop();
				//isManualStop = true;
			    //mLogicManager.stopVideo();
				// Added by keke 1202 for fix bug DTV00379478
				mControlView.setInforbarNull();
				// // Added by keke 1202 for fix bug DTV00379470
				dismissNotSupprot();

			} else {
				mControlView.setMediaPlayState();
				mControlView.play();

			}
			reSetController();
		}
	};

	private void onRepeat() {
		int model = mLogicManager.getRepeatModel(Const.FILTER_VIDEO);
		switch (model) {
		case Const.REPEAT_ALL: {
			mControlView.setRepeatSingle();
			//mControlView.setVideoRepeat(Const.REPEAT_ONE);
			mLogicManager.setRepeatMode(Const.FILTER_VIDEO, Const.REPEAT_ONE);
			break;
		}
		case Const.REPEAT_ONE: {
			mControlView.setRepeatNone();
			//mControlView.setVideoRepeat(Const.REPEAT_NONE);
			mLogicManager.setRepeatMode(Const.FILTER_VIDEO, Const.REPEAT_NONE);
			break;
		}
		case Const.REPEAT_NONE: {
			mControlView.setRepeatAll();
			//mControlView.setVideoRepeat(Const.REPEAT_ALL);
			mLogicManager.setRepeatMode(Const.FILTER_VIDEO, Const.REPEAT_ALL);
			break;
		}
		default:
			break;
		}

	}

	private void setFast(int isForward) {

		if (null == mControlView) {
			return;
		}

        hideFeatureNotWork();
		// TODO
		int speed = mLogicManager.getVideoSpeed();
		mControlView.onFast(speed, isForward, Const.FILTER_VIDEO);

		if (!mHandler.hasMessages(PROGRESS_CHANGED)) {
			//mHandler.sendEmptyMessage(PROGRESS_CHANGED);
			//Ts file update progressBar in call back function 'mMessageListener' modified by keke 
			mHandler.sendEmptyMessageDelayed(PROGRESS_CHANGED, 500);
		}
	}

	public void seek(int positon) throws NotSupportException{

		if (positon < 0) {
			positon = 0;
		} else if (positon > mLogicManager.getVideoDuration()) {
			positon = mLogicManager.getVideoDuration();
		}
		if (positon >= 0) {
			mLogicManager.seek(positon);
		}

	}

	private class VideoDialog extends Dialog {
		private Context mContext;

		public VideoDialog(Context context) {
			super(context, R.style.videodialog);

			this.mContext = context;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.mmp_video_innerdialog);
			// added by keke for fox DTV00383992
			if (null != mTipsDialog && mTipsDialog.isShowing()) {
				hideFeatureNotWork();
			}
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			switch (keyCode) {
			case KeyMap.KEYCODE_MTKIR_STOP:
				// Add by yongzheng for fix CR DTV00383189
				mControlView.setVideoSubtitle((short) 1, (short) -1);

				setBlueDialogStats(true);
				// Added by keke 1202 for fix bug DTV00379470
				dismissNotSupprot();
				if (getBlueDialogStats()) {
				isManualStop = true;
				mLogicManager.stopVideo();
				}
				this.dismiss();
				return false;
			case KeyMap.KEYCODE_DPAD_CENTER:
				setBlueDialogStats(false);
				// change by keke for fox DTV00379253
				mControlView.reSetVideo();
				this.dismiss();
				return false;
			case KeyMap.KEYCODE_MTKIR_MUTE:
			case KeyMap.KEYCODE_VOLUME_UP:
			case KeyMap.KEYCODE_VOLUME_DOWN:
			case KeyMap.KEYCODE_MTKIR_PLAYPAUSE:
			case KeyMap.KEYCODE_MTKIR_NEXT:
			case KeyMap.KEYCODE_MTKIR_PREVIOUS:
				if (null != mContext && mContext instanceof MediaPlayActivity) {
					((MediaPlayActivity) mContext).onKeyDown(keyCode, event);
				}
				return true;

			case KeyMap.KEYCODE_MTKIR_ANGLE: {
				Util.exitMmpActivity(VideoPlayActivity.this);
			}
			default:
				return false;
			}
		}

		public void setDialogParams(int width, int height) {
			Window window = getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.width = width;
			lp.height = height;
			window.setAttributes(lp);
		}
	}

	private class TimeDialog extends Dialog {

		private TextView mHour;

		private TextView mMinute;

		private TextView mSeconds;

		private int focusIndex = 0;

		private int actionTag;

		// Added by Dan for fix bug DTV00375870
		private boolean mFocusChanged;
		private Context mContext;
		public TimeDialog(Context context) {
			super(context, R.style.dialog);
			this.mContext = context;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.mmp_seek_time);

			WindowManager m = getWindow().getWindowManager();
			Display display = m.getDefaultDisplay();
			Window window = getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.x = -(int) (display.getRawWidth() * 0.2);
			lp.y = 0;
			window.setAttributes(lp);

			mHour = ((TextView) findViewById(R.id.time_hour));
			mMinute = ((TextView) findViewById(R.id.time_minute));
			mSeconds = ((TextView) findViewById(R.id.time_seconds));

		}

		@Override
		protected void onStart() {
			focusIndex = 0;
			setFocus();
			int progress = 0;
			if (null != mLogicManager) {
				progress = mLogicManager.getVideoProgress();
			}
			progress = (progress > 0 ? progress : 0);
			progress /= 1000;
			long minute = progress / 60;
			long hour = minute / 60;
			long second = progress % 60;
			minute %= 60;
			mHour.setText(String.format("%02d", hour));
			mMinute.setText(String.format("%02d", minute));
			mSeconds.setText(String.format("%02d", second));
			super.onStart();
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO ..
			switch (keyCode) {
			case KeyMap.KEYCODE_MTKIR_ANGLE:{
				dismissTimeDialog();
				Util.exitMmpActivity(VideoPlayActivity.this);
				return true;
			}
			case KeyMap.KEYCODE_BACK:
				dismissTimeDialog();
				return true;
			case KeyMap.KEYCODE_VOLUME_UP: {
				reSetController();
				if (mLogicManager.isMute()) {
					onMute();
					return true;
				}
				currentVolume = currentVolume + 1;
				if (currentVolume > maxVolume) {
					currentVolume = maxVolume;
				}
				mLogicManager.setVolume(currentVolume);
				mControlView.setCurrentVolume(currentVolume);
				return true;
			}
			case KeyMap.KEYCODE_VOLUME_DOWN: {
				reSetController();
				if (mLogicManager.isMute()) {
					onMute();
					return true;
				}
				currentVolume = currentVolume - 1;
				if (currentVolume < 0) {
					currentVolume = 0;
				}
				mLogicManager.setVolume(currentVolume);
				mControlView.setCurrentVolume(currentVolume);
				return true;
			}
			case KeyMap.KEYCODE_MTKIR_MUTE: {
				reSetController();
				onMute();
				return true;
			}
			case KeyMap.KEYCODE_MTKIR_NEXT:
			case KeyMap.KEYCODE_MTKIR_PREVIOUS:
			case KeyMap.KEYCODE_MTKIR_PLAYPAUSE: {
				return true;
			}
			case KeyMap.KEYCODE_DPAD_CENTER: {
				removeFeatureMessage();
				// dismissNotSupprot();
				hideFeatureNotWork();
				int hour = 0;
				int minute = 0;
				int seconds = 0;
				try {
					hour = Integer.valueOf(mHour.getText().toString());
					minute = Integer.valueOf(mMinute.getText().toString());
					seconds = Integer.valueOf(mSeconds.getText().toString());
				} catch (Exception e) {
					MtkLog.i(TAG, e.getMessage());
				}
				int time = (hour * 3600 + minute * 60 + seconds) * 1000;
				int total = mLogicManager.getVideoDuration();
				if (time >= total || time < 0) {
					// TODO message...
					featureNotWork(getString(R.string.mmp_time_out));
					return true;
				}
				dismiss();
				try {
					seek(time);
				} catch (Exception e) {
					featureNotWork(getString(R.string.mmp_featue_notsupport));
				}
				break;
			}
			case KeyMap.KEYCODE_DPAD_LEFT: {
				if (focusIndex > 0) {
					focusIndex -= 1;
				} else {
					focusIndex = 2;
				}
				setFocus();
				// Added by Dan for fix bug DTV00375870
				mFocusChanged = true;
				actionTag = keyCode;
				break;
			}
			case KeyMap.KEYCODE_DPAD_RIGHT: {
				if (focusIndex >= 2) {
					focusIndex = 0;
				} else {
					focusIndex += 1;
				}
				setFocus();
				// Added by Dan for fix bug DTV00375870
				mFocusChanged = true;
				actionTag = KeyMap.KEYCODE_DPAD_LEFT;
				break;
			}
			case KeyMap.KEYCODE_0:
			case KeyMap.KEYCODE_1:
			case KeyMap.KEYCODE_2:
			case KeyMap.KEYCODE_3:
			case KeyMap.KEYCODE_4:
			case KeyMap.KEYCODE_5:
			case KeyMap.KEYCODE_6:
			case KeyMap.KEYCODE_7:
			case KeyMap.KEYCODE_8:
			case KeyMap.KEYCODE_9: {
				setTime(keyCode - 7);
				actionTag = keyCode;
				break;
			}
			case KeyMap.KEYCODE_DPAD_UP: {
				UpDownTime(1);
				// Added by Dan for fix bug DTV00375870
				mFocusChanged = false;
				break;
			}
			case KeyMap.KEYCODE_DPAD_DOWN: {
				UpDownTime(-1);
				// Added by Dan for fix bug DTV00375870
				mFocusChanged = false;
				break;
			}
			default:
				break;
			}
			return super.onKeyDown(keyCode, event);
		}

		private void UpDownTime(int offset) {
			switch (focusIndex) {
			case 0: {
				int value = Integer.valueOf(mHour.getText().toString())
						+ offset;
				if (value <= 9 && value >= 0) {
					mHour.setText("0" + value);
				} else if (value > 9 && value < 100) {
					mHour.setText("" + value);
				} else if (value >= 100) {
					mHour.setText(R.string.mmp_time_inti);
				} else {
					mHour.setText("99");
				}
				break;
			}
			case 1: {
				int value = Integer.valueOf(mMinute.getText().toString())
						+ offset;
				if (value <= 9 && value >= 0) {
					mMinute.setText("0" + value);
				} else if (value > 59) {
					mMinute.setText(R.string.mmp_time_inti);
				} else if (value < 0) {
					mMinute.setText("59");
				} else {
					mMinute.setText("" + value);
				}

				break;
			}
			case 2: {

				int value = Integer.valueOf(mSeconds.getText().toString())
						+ offset;
				if (value <= 9 && value >= 0) {
					mSeconds.setText("0" + value);
				} else if (value > 59) {
					mSeconds.setText(R.string.mmp_time_inti);
				} else if (value < 0) {
					mSeconds.setText("59");
				} else {
					mSeconds.setText("" + value);
				}
				break;
			}
			default:
				break;
			}
		}

		private void setTime(int value) {

			switch (focusIndex) {
			case 0: {
				setValue(mHour, value);
				break;
			}
			case 1: {
				setValue(mMinute, value);
				break;
			}
			case 2: {
				setValue(mSeconds, value);
				break;
			}
			default:
				break;
			}

		}

		private void setValue(TextView v, int key) {
			// Added by Dan for fix bug DTV00375870
			if (mFocusChanged) {
				v.setText("0" + key);
				mFocusChanged = false;
				return;
			}

			int value = Integer.valueOf(v.getText().toString());
			if (value == 0) {
				v.setText("0" + key);
			} else if (value <= 9) {
				// TODO
				int temp = value * 10 + key;
				if (temp > 59 && focusIndex != 0) {
					v.setText("59");
				} else {
					v.setText(value + "" + key);
				}
			} else if (actionTag == KeyMap.KEYCODE_DPAD_LEFT) {
				v.setText("0" + key);
			} else if (focusIndex == 2) {
				focusIndex = 0;
				setFocus();
				mHour.setText("0" + key);
			} else {
				focusIndex++;
				setFocus();
				if (focusIndex == 1) {
					mMinute.setText("0" + key);
				} else if (focusIndex == 2) {
					mSeconds.setText("0" + key);
				}

			}

		}

		private void setFocus() {

			mHour.setTextColor(Color.WHITE);
			mMinute.setTextColor(Color.WHITE);
			mSeconds.setTextColor(Color.WHITE);
			switch (focusIndex) {
			case 0: {
				mHour.setTextColor(Color.RED);
				break;
			}
			case 1: {
				mMinute.setTextColor(Color.RED);
				break;
			}
			case 2: {
				mSeconds.setTextColor(Color.RED);
				break;
			}
			default:
				break;
			}

		}

	}

	protected void hideControllerDelay() {

		// TODO check in
		mHandler.removeMessages(HIDE_CONTROLER);
		mHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, HIDE_DELAYTIME);
	}

	protected void onPause() {
		//add by yongzheng for fix CR DTV00441350, 
		//Activity has been non-existent attached to this Activity dialog does not disappear abnormal
		if (!isBackFromCapture) {
			dismissNotSupprot();
		}
		if (menuDialog != null) {
			menuDialog.dismiss();
		}
		super.onPause();
	}
	protected void onDestroy() {
		super.onDestroy();
		MtkLog.e(TAG, "Video Play Activity onDestroy! ");
		isActivityLiving = false; 
		mHandler.removeMessages(MESSAGE_PLAY);
		mHandler.removeMessages(PROGRESS_CHANGED);
		mHandler.removeMessages(HIDE_CONTROLER);
		mHandler.removeMessages(MSG_GET_CUR_POS);
		mLogicManager.finishVideo();

	}
	private  boolean isFileNotSupport = false;
	protected boolean isFileNotSupport() {
		return isFileNotSupport;
	}
	protected void setIsFileNotSupport(boolean fileNotSupport) {
		isFileNotSupport = fileNotSupport;
	}
}
