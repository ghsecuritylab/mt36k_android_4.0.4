package com.mediatek.ui.mmp.util;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mediatek.common.PhotoPlayer.MtkPhotoHandler;
import com.mediatek.media.MtkMediaPlayer.OnCompletionListener;
import com.mediatek.media.MtkMediaPlayer.OnErrorListener;
import com.mediatek.media.MtkMediaPlayer.OnPreparedListener;
import com.mediatek.media.MtkMediaPlayer.OnRePlayListener;
import com.mediatek.media.MtkMediaPlayer.OnSpeedUpdateListener;
import com.mediatek.media.MtkMediaPlayer.OnTotalTimeUpdateListener;
import com.mediatek.media.NotSupportException;
import com.mediatek.mmpcm.CommonSet;
import com.mediatek.mmpcm.audioimpl.AudioConst;
import com.mediatek.mmpcm.audioimpl.CorverPic;
import com.mediatek.mmpcm.audioimpl.Lyric;
import com.mediatek.mmpcm.audioimpl.LyricTimeContentInfo;
import com.mediatek.mmpcm.audioimpl.PlaybackService;
import com.mediatek.mmpcm.audioimpl.PlaybackService.LocalBinder;
import com.mediatek.mmpcm.mmcimpl.Const;
import com.mediatek.mmpcm.mmcimpl.PlayList;
import com.mediatek.mmpcm.photoimpl.Capture;
import com.mediatek.mmpcm.photoimpl.ConstPhoto;
import com.mediatek.mmpcm.photoimpl.EffectView;
import com.mediatek.mmpcm.photoimpl.Imageshowimpl;
import com.mediatek.mmpcm.photoimpl.Imageshowimpl.OnPhotoCompletedListener;
import com.mediatek.mmpcm.photoimpl.Imageshowimpl.OnPhotoDecodeListener;
import com.mediatek.mmpcm.photoimpl.PhotoUtil;
import com.mediatek.mmpcm.text.ITextEventListener;
import com.mediatek.mmpcm.text.ITextReader;
import com.mediatek.mmpcm.textimpl.TextReader;
import com.mediatek.mmpcm.threedimen.photo.IThrdPhotoEventListener;
import com.mediatek.mmpcm.threedimen.photoimpl.MPlayback;
import com.mediatek.mmpcm.threedimen.photoimpl.PhotoManager;
import com.mediatek.mmpcm.video.IPlayback;
import com.mediatek.mmpcm.video.IPlayback.OnPBCompleteListener;
import com.mediatek.mmpcm.video.IPlayback.OnPBMsgListener;
import com.mediatek.mmpcm.video.IPlayback.OnPBPlayDoneListener;
import com.mediatek.mmpcm.video.IPlayback.OnPBPreparedListener;
import com.mediatek.mmpcm.videoimpl.Comset;
import com.mediatek.mmpcm.videoimpl.VideoConst;
import com.mediatek.mmpcm.videoimpl.VideoManager;
import com.mediatek.tvcm.TVContent;
import com.mediatek.tvcommon.TVConfigurer;
import com.mediatek.ui.menu.util.MenuConfigManager;
import com.mediatek.ui.mmp.util.AsyncLoader.LoadWork;
import com.mediatek.ui.util.MtkLog;

public class LogicManager {

	private static final String TAG = "LogicManager";

	private static LogicManager mLogicManager = null;

	private VideoManager mVideoManager;

	private PlayList mPlayList;

	private IPlayback mVideoPlayback;

	private PlaybackService mAudioPlayback = null;

	private Intent serviceIntent;

	private ServiceConnection serviceConnection;

	private Lyric mLyric;

	private OnPreparedListener mPreparedListener;

	private OnCompletionListener mCompletionListener;

	private OnTotalTimeUpdateListener mTotalTimeUpdateListener;
	private OnSpeedUpdateListener mSpeedUpdateListener; // add by lei
	private OnRePlayListener mReplayListener;

	private OnErrorListener mErrorListener; // fix bug by hs_haizhudeng

	private Imageshowimpl mImageManager;

	private EffectView mEffectView;

	private PhotoManager mPhotoManager; // add by lei

	private MPlayback mPhotoPlayback; // add by lei

	private ITextReader tvManager;

	private CommonSet mmpset;

	private Context mContext;

	private Comset mVideoComSet;

	private EffectView mImageEffectView;

	private float mTextFontSize = MultiMediaConstant.SMALLSIZE;

	private String mTextFontStyle = "regular";

	private String mTextFontColor = "White";

	private ITextEventListener mTextEventListener;

	static public final int MMP_EQ_ELEM_NUM = 10;

	static public final int MMP_EQ_MAX = 0x3FFFFF;

	static public final int MMP_EQ_MIN = 0x000FFF;

	private TVConfigurer mtvcfg;

	private Capture mCapturer;

	private AsyncLoader<Integer> mPlayLoader;

	private int mPlayFlag = 0;
	
	private boolean is3DPhotoMpo=false;
	
	private String thrdMode;
	
	private int thrdModeValue =1;

	private class PlayWork implements LoadWork<Integer> {

		private MPlayback mPlayBack;
		private String mPath;

		public PlayWork(MPlayback playBack, String path) {
			mPlayBack = playBack;
			mPath = path;

		}

		public Integer load() {
			if (mPlayBack == null) {
				mPlayFlag = -1;
			} else {
				MtkPhotoHandler mtkPhotoHandler = mPlayBack.decode3DPhoto(mPath);
				setPicSetting();
				mPlayBack.Display(mtkPhotoHandler);
				mPlayFlag = 1;
			}
			return mPlayFlag;
		}

		public void loaded(Integer result) {
			// TODO Auto-generated method stub

		}

	}

	private LogicManager(Context context) {
		//mPlayLoader = new AsyncLoader<Integer>(1);
		mPlayLoader = AsyncLoader.getInstance(1);
		mPlayList = PlayList.getPlayList();
		mContext = context;
		mtvcfg = TVContent.getInstance(context)
				.getConfigurer();

		mmpset = CommonSet.getInstance(mContext);
		// add by shuming for Fix CR DTV00401969

		// MtkLog.i("", "--------EffectView------");
		mEffectView = new EffectView(context);
	}

	public static LogicManager getInstance(Context context) {

		if (null == mLogicManager) {
			mLogicManager = new LogicManager(context);
		}
		return mLogicManager;
	}

	public void clearAudio() {
		mAudioPlayback = null;
	}

	public void initVideo(Context context, int videoSource) {

		mPlayList = PlayList.getPlayList();
		// mPlayList.setRepeatMode(Const.FILTER_VIDEO, Const.REPEAT_NONE);
		mVideoManager = VideoManager.getInstance(context, videoSource);
		mVideoPlayback = mVideoManager.getPlayback();
		mVideoPlayback.setPlayerMode(videoSource);
		mVideoPlayback.setPreviewMode(false);

		if (null == mVideoComSet) {
			mVideoComSet = new Comset();
		} else {
			mVideoComSet.videoZoom(0);
		}
		new Thread(new Runnable() {
			public void run() {
				try {
					mVideoPlayback.setDataSource(mPlayList
							.getCurrentPath(Const.FILTER_VIDEO));
				} catch (IllegalArgumentException e) {
					MtkLog.e(TAG, e.getMessage());
				} catch (IllegalStateException e) {
					mVideoPlayback.reset();
					MtkLog.e(TAG, e.getMessage());
				} catch (IOException e) {
					MtkLog.e(TAG, e.getMessage());
				}
			}
		}).start();

	}

	/*--------------------------------------- Video --------------------------------*/
	public IPlayback getVideoPlayBack() {

		return mVideoPlayback;
	}

	public void freeVideoResource() {
		mmpset.mmpFreeVideoResource();
	}

	public void restoreVideoResource() {
		mmpset.mmpRestoreVideoResource();
	}

	public String getCurDevName(String currentPath) {
		if (null == currentPath) {
			return null;
		}
		String[] paths = currentPath.split("/");
		for (String path : paths) {
			MtkLog.i(TAG, " path  :" + path);
		}
		String devName = null;
		if (paths.length > 2) {
			if ("usbdisk".equals(paths[2])) {
				devName = "sda1";
			} else if ("usb".equals(paths[2])) {
				devName = paths[3];
			}
		}
		return devName;
	}

	public void setCapturer(View view) {
		if (null == mCapturer) {
			mCapturer = new Capture();
		}
		mCapturer.captureImage(view);
	}

	public int getNativeBitmap() {
		return mCapturer.getNativeBitmap();
	}

	public int getWidth() {
		return mCapturer.getWidth();
	}

	public int getHeight() {
		return mCapturer.getHeight();
	}

	public int getPitch() {
		return mCapturer.getPitch();
	}

	public int getMode() {
		return mCapturer.getColorMode();
	}

	public void setSubtitleTrack(short index) {
		if (null == mVideoPlayback) {
			return;
		}
		MtkLog.i(TAG, "------setSubtitleTrack  index:" + index);
		mVideoPlayback.setSubtitleTrack(index);
	}

	public void setSubOnOff(boolean flag) {
		if (null == mVideoPlayback) {
			return;
		}
		mVideoPlayback.setSubOnOff(flag);
	}

	public short getSubtitleTrackNumber() {
		if (null == mVideoPlayback) {
			return 0;
		}
		return mVideoPlayback.getMediaInfo().getSubtitleTrackNumber();
	}

	public int getAudioTranckNumber() {
		if (null == mVideoPlayback) {
			return 0;
		}
		return mVideoPlayback.getMediaInfo().getAudioTrackNumber();

	}

	public void setAudioTranckNumber(short mtsIdx) {
		if (null == mVideoPlayback) {
			return;
		}
		mVideoPlayback.selectMts(mtsIdx);

	}

	public int[] getAvailableScreenMode() {
		return mmpset.getAvailableScreenMode();
	}

	public void videoZoom(int zoomType) {
		mVideoComSet.videoZoom(zoomType);
	}

	/**
	 * set picture zoom type 1X, 2X 4X.
	 * 
	 * @param zoomType
	 */
	public void setPicZoom(int zoomType) {
		if (mImageEffectView != null) {
			mImageEffectView.setMultiple(zoomType);
		}
	}

	/**
	 * get current setting zoom value
	 * 
	 * @return
	 */
	public int getPicCurZoom() {
		if (mImageEffectView == null) {
			return 1;
		}
		return mImageEffectView.getMultiple();
	}

	public int getCurZomm() {
		if (null == mVideoComSet) {
			return 1;
		}
		return mVideoComSet.getCurZoomType();
	}

	public int getMaxZoom() {
		return mVideoComSet.getMaxZoom();
	}

	public void videoZoomReset() {
		if (mVideoComSet == null) {
			mVideoComSet = new Comset();
		}
		mVideoComSet.videoZoomReset();
	}

	public String getVideoPageSize() {
		return (mPlayList.getCurrentIndex(Const.FILTER_VIDEO) + 1) + "/"
				+ mPlayList.getFileNum(Const.FILTER_VIDEO);
	}

	public void playVideo() {
		if (null == mVideoPlayback) {
			return;
		}
		try {
			mVideoPlayback.play();
		} catch (IllegalStateException e) {
			MtkLog.e(TAG, e.getMessage());
			throw new IllegalStateException(e);
		}

	}

	public void pauseVideo() {
		if (null == mVideoPlayback) {
			return;
		}
		try {
			mVideoPlayback.pause();
		} catch (IllegalStateException e) {
			MtkLog.e(TAG, e.getMessage());
			if (VideoConst.MSG_ERR_CANNOTPAUSE
					.equals(e.getMessage().toString())) {
				throw new IllegalStateException(VideoConst.MSG_ERR_CANNOTPAUSE);
			}else if (VideoConst.MSG_ERR_PAUSEEXCEPTION.equals(e.getMessage()
					.toString())) {
				throw new IllegalStateException(
						VideoConst.MSG_ERR_PAUSEEXCEPTION);
			}else{
				throw new IllegalStateException( e.getMessage());
			}

		}

	}

	public void stepVideo() {
		if (null == mVideoPlayback) {
			return;
		}
		try {
			mVideoPlayback.step();
		} catch (IllegalStateException e) {
			MtkLog.e(TAG, e.getMessage());
		}
	}

	public void stopVideo() {
		if (null == mVideoPlayback) {
			return;
		}
		try {
			mVideoPlayback.stop();
		} catch (IllegalStateException e) {
			MtkLog.e(TAG, "stop  " + e.getMessage());
		}
	}

	public void finishVideo() {
		if (null == mVideoPlayback) {
			MtkLog.e(TAG, "finishVideo  is null");
			return;
		}
		try {
			int status = mVideoPlayback.getPlayStatus();
			if (status == VideoConst.PLAY_STATUS_END) {
				return;
			}
			if (status < VideoConst.PLAY_STATUS_STOPPED) {
				mVideoPlayback.stop();
				MtkLog.i(TAG, " stop video");
			}
		} catch (IllegalStateException e) {
			MtkLog.e(TAG, "stop  " + e.getMessage());
		}
		try {
			mVideoPlayback.onRelease();
			mVideoPlayback = null;
			MtkLog.i(TAG, "OnRelease VideoPlayback ");
			/* Had closed video play and send broadcast tell it */
		} catch (Exception e) {
			mVideoPlayback = null;
			MtkLog.e(TAG, "onRelease  " + e.toString());
		}

	}

	public void sendCloseBroadCast() {

		Intent intent = new Intent(MultiMediaConstant.STOPMUSIC);
		mContext.sendBroadcast(intent);
		MtkLog.e(TAG, "Video Play Activity sendCloseVideoBroadCast ! ");
		clearAudio();
	}

	/**
	 * Play prev video.
	 * 
	 * @return -1, play failed, 0, successful.
	 */
	public int playPrevVideo() {
		if (null == mVideoPlayback) {
			return -1;
		}
		try {
			mVideoPlayback.manualPrev();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	/**
	 * Replay video.
	 * 
	 * @return -1, replay failed, 0, successful.
	 */
	public int replayVideo() {
		if (null == mVideoPlayback) {
			return -1;
		}
		try {
			mVideoPlayback.replay();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	/**
	 * Play next video.
	 * 
	 * @return -1, play failed, 0, successful.
	 */
	public int playNextVideo() {
		if (null == mVideoPlayback) {
			return -1;
		}
		try {
			mVideoPlayback.manualNext();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return 0;
	}

	// add by shuming fix CR00385698
	/**
	 * 
	 * @param featurenotsurport
	 */
	public void setVideoFeaturenotsupport(boolean featurenotsurport) {
		if (null == mVideoPlayback) {
			return;
		}
		mVideoPlayback.setFeaturenotsurport(featurenotsurport);
	}

	/**
	 * 
	 * @return isVideoFeaturenotsurport
	 */
	public boolean isVideoFeaturenotsurport() {
		if (null == mVideoPlayback) {
			return false;
		}
		return mVideoPlayback.isFeaturenotsurport();
	}

	// end
	public void onDevUnMount() {
		finishAudioService();
		finishVideo();
	}

	public void slowForwardVideo() throws NotSupportException {
		if (null == mVideoPlayback) {
			return;
		}
		try {
			mVideoPlayback.slowForward();
		} catch (IllegalStateException e) {
			MtkLog.i(TAG, e.getMessage());
		}

	}

	public void fastForwardVideo() throws NotSupportException {
		if (null == mVideoPlayback) {
			return;
		}
		try {
			mVideoPlayback.fastForward();
		} catch (IllegalStateException e) {
			MtkLog.i(TAG, e.getMessage());
		}

	}

	public void fastRewindVideo() throws NotSupportException {
		if (null == mVideoPlayback) {
			return;
		}
		try {
			mVideoPlayback.fastRewind();
		} catch (IllegalStateException e) {
			MtkLog.i(TAG, e.getMessage());
		}

	}
	
	public boolean canDoSeek() {
		if (null == mVideoPlayback) {
			return false;
		}
		return mVideoPlayback.canDoSeek();
	}

	public int getVideoSpeed() {
		if (null == mVideoPlayback) {
			return 1;
		}
		return mVideoPlayback.getSpeed();
	}

	public void seek(int positon) throws NotSupportException{
		if (null == mVideoPlayback) {
			return;
		}
		try {
			mVideoPlayback.seek(positon);
		} catch (IllegalStateException e) {
			MtkLog.e(TAG, e.getMessage());
		}
	}

	public boolean isVideoFast() {
		if (null == mVideoPlayback) {
			return false;
		}
		return (mVideoPlayback.getPlayStatus() == VideoConst.PLAY_STATUS_FR)
				|| (mVideoPlayback.getPlayStatus() == VideoConst.PLAY_STATUS_FF);
	}

	public int getVideoDuration() {
		return mVideoPlayback.getDuration();
	}

	/**
	 * Get video width;
	 * 
	 * @return
	 */
	public int getVideoWidth() {
		int width = 0;
		if (mVideoPlayback != null) {
			// add by xiaojie fix cr DTV00384016
			if (mVideoPlayback.getVideoInfo() != null) {
				width = mVideoPlayback.getVideoInfo().getWidth();
			}
			// end
		}

		return width;
	}

	/**
	 * Get video height.
	 * 
	 * @return
	 */
	public int getVideoHeight() {
		int heghit = 0;
		if (mVideoPlayback != null) {
			// add by xiaojie fix cr DTV00384016
			if (mVideoPlayback.getVideoInfo() != null) {
				heghit = mVideoPlayback.getVideoInfo().getHeight();
			}
			// end
		}

		return heghit;
	}

	/**
	 * get video file current play postion.
	 * 
	 * @return
	 */
	public long getVideoCurFilePosition() {
		if (null == mVideoPlayback) {
			return 0;
		}
		return mVideoPlayback.getCurFilePosition();
	}

	public String getVideoTitle() {
		if (null == mVideoPlayback) {
			return "";
		}
		return mVideoPlayback.getVideoTitle();
	}

	public String getVideoCopyright() {
		if (null == mVideoPlayback) {
			return "";
		}
		return mVideoPlayback.getVideoCopyright();
	}

	public String getVideoYear() {
		if (null == mVideoPlayback) {
			return "";
		}
		return mVideoPlayback.getVideoYear();
	}

	public String getVideoGenre() {
		if (null == mVideoPlayback) {
			return "";
		}
		return mVideoPlayback.getVideoGenre();
	}

	public String getVideoDirector() {
		return mVideoPlayback.getVideoDirector();
	}

	public int getVideoProgress() {
		if (null == mVideoPlayback) {
			return 0;
		}
		return mVideoPlayback.getProgress();
	}

	/*------------------- mmpset -----------------------*/
	public int getVolume() {
		if (null == mmpset) {
			return 0;
		}
		return mmpset.getVolume();
	}

	public void setVolume(int volume) {
		if (null == mmpset) {
			return;
		}
		mmpset.setVolume(volume);
	}
	
	public void setVolumeUp(){
		if (mLogicManager.isMute()) {
			setMute();
			return;
		}
		int maxVolume = mLogicManager.getMaxVolume();
		int currentVolume = mLogicManager.getVolume();
		currentVolume = currentVolume + 1;
		if (currentVolume > maxVolume) {
			currentVolume = maxVolume;
		}
		mLogicManager.setVolume(currentVolume);
	}
	
	public void setVolumeDown(){
		if (mLogicManager.isMute()) {
			setMute();
			return;
		}
		int currentVolume = mLogicManager.getVolume();
		currentVolume = currentVolume - 1;
		if (currentVolume < 0) {
			currentVolume = 0;
		}
		mLogicManager.setVolume(currentVolume);
	}

	public void setAudioOnly(boolean switchFlag) {
		if (null == mmpset) {
			return;
		}

		mmpset.setAudOnly(switchFlag);
	}

	public boolean isAudioOnly() {
		if (null == mmpset) {
			return false;
		}
		return mmpset.getAudOnly();
	}

	public int[] getAudSpectrum() {
		int[] valueArray = new int[15];
		int[] array = mtvcfg.getSpectrum();
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				valueArray[i] = (MMP_EQ_ELEM_NUM * (array[i] - MMP_EQ_MIN) / (MMP_EQ_MAX - MMP_EQ_MIN));
			}
		}
		return valueArray;
	}

	public int getMaxVolume() {
		return mmpset.getMaxVolume();
	}

	/**
	 * set picture mode
	 * 
	 * @param type
	 */
	public void setPictureMode(int type) {
		mmpset.setPictureMode(type);
	}

	/**
	 * set screen mode
	 * 
	 * @param type
	 */
	public void setScreenMode(int type) {
		mmpset.setScreenMode(type);
	}

	public int getCurPictureMode() {
		return mmpset.getCurPictureMode();
	}

	public int getCurScreenMode() {
		if (null == mVideoPlayback) {
			return 0;
		}
		return mmpset.getCurScreenMode();
	}

	public void setMute() {
		if (null == mmpset) {
			return;
		}
		mmpset.setMute();
	}

	public boolean isMute() {
		if (null == mmpset) {
			return false;
		}
		return mmpset.isMute();
	}

	public int getVideoPlayStatus() {
		if (null == mVideoPlayback) {
			return VideoConst.PLAY_STATUS_END;
		}
		return mVideoPlayback.getPlayStatus();
	}

	// public String getVideoCurFileName() {
	// return mVideoPlayback.getCurFileName();
	// }

	public String getFileDuration() {
		if (null == mVideoPlayback) {
			return "";
		}
		return mVideoPlayback.getVideoYear();
	}

	public String getFileName() {
		if (null == mVideoPlayback) {
			return "";
		}
		String filename = mVideoPlayback.getCurFileName();
		try {
			return filename.substring(filename.lastIndexOf("/") + 1);

		} catch (Exception e) {
			MtkLog.d(TAG, e.toString());
			return null;
		}
	}

	/**
	 * get file size
	 * 
	 * @return
	 */
	public long getVideoFileSize() {
		if (null == mVideoPlayback) {
			return 0;
		}
		return mVideoPlayback.getVideoFileSize();
	}

	/**
	 * set file size to video playback.
	 * 
	 * @param fileSize
	 */
	public void setVideoFileSize(long fileSize) {
		if (null == mVideoPlayback) {
			return;
		}

		mVideoPlayback.setFileSize(fileSize);
	}

	public void setPreparedListener(OnPBPreparedListener listener) {
		if (null == mVideoPlayback) {
			return;
		}
		mVideoPlayback.setOnPBPreparedListener(listener);
	}

	public void setOnPBMsgListener(OnPBMsgListener listener) {
		if (null == mVideoPlayback) {
			return;
		}
		mVideoPlayback.setOnPBMsgListener(listener);

	}

	public void setOnPBPlayDoneListener(OnPBPlayDoneListener listener) {
		if (null == mVideoPlayback) {
			return;
		}
		mVideoPlayback.setOnPBPlayDoneListener(listener);

	}

	public void setCompleteListener(OnPBCompleteListener listener) {
		mVideoPlayback.setOnPBCompleteListener(listener);
	}

	public boolean videoIsPlaying() {
		switch (getVideoPlayStatus()) {
		case VideoConst.PLAY_STATUS_UNKNOW:

			break;
		case VideoConst.PLAY_STATUS_STARTED:
			return true;
		case VideoConst.PLAY_STATUS_PAUSED:

		case VideoConst.PLAY_STATUS_STOPPED:

			break;
		case VideoConst.PLAY_STATUS_PREPARED:
			break;
		default:
			break;
		}
		return false;
	}

	/*-------------------aduido ------------------*/
	private int mAudioSource;

	// change by browse fix CR DTV00384318
	/**
	 * New Service when service not exist.
	 * */
	private void initService(Context context) {
		serviceIntent = new Intent(context,
				com.mediatek.mmpcm.audioimpl.PlaybackService.class);
		serviceConnection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder service) {
				LocalBinder binder = (LocalBinder) service;
				mAudioPlayback = binder.getService();
				startPlayAudio(mAudioSource);
			}

			public void onServiceDisconnected(ComponentName name) {
				MtkLog.d(TAG, "onServiceDisconnected");
			}

		};
		startService(context);
		bindService(context);
	}

	public void initAudio(Context context, final int audioSource) {
		mPlayList = PlayList.getPlayList();
		mmpset = CommonSet.getInstance(context);
		mAudioSource = audioSource;
		if (mAudioPlayback == null) {
			initService(context);
		} else {
			if (mAudioPlayback.getPlayStatus() < AudioConst.PLAY_STATUS_STOPPED) {
				stopAudio();
			}
			bindService(context);
		}
	}

	// end
	private void startPlayAudio(int audioSource) {
		mAudioPlayback.registerAudioPreparedListener(mPreparedListener);
		mAudioPlayback.registerAudioCompletionListener(mCompletionListener);
		mAudioPlayback.setPlayMode(audioSource);
		mAudioPlayback
				.registerAudioDurationUpdateListener(mTotalTimeUpdateListener);
		mAudioPlayback.registerAuidoSpeedUpdateListener(mSpeedUpdateListener);
		mAudioPlayback.registerAudioErrorListener(mErrorListener);
		mAudioPlayback.registerAudioReplayListener(mReplayListener);
		setDataSource(mPlayList.getCurrentPath(Const.FILTER_AUDIO));
	}

	public void stopDecode() {
		if (null != mImageManager) {
			new Thread(new Runnable() {
				public void run() {
					mImageManager.stopDecode();
				}
			}).start();
		}
	}

	public void setDataSource(String path) {
		new MyThread(path).start();
	}

	private class MyThread extends Thread {
		String path;

		MyThread(String path) {
			this.path = path;
		}

		public void run() {
			MtkLog.i(TAG, "---- path :" + path);
			mAudioPlayback.setDataSource(path);

		}
	}

	public void startService(Context context) {
		context.startService(serviceIntent);
	}

	public void stopService(Context context) {
		context.stopService(serviceIntent);
	}

	public void bindService(Context context) {
		try {
			context.bindService(serviceIntent, serviceConnection,
					Context.BIND_AUTO_CREATE);
		} catch (Exception e) {
			MtkLog.e(TAG, e.toString());
		}

	}

	public void unbindService(Context context) {
		try {
			context.unbindService(serviceConnection);
		} catch (Exception e) {
			MtkLog.e(TAG, e.toString());
		}

	}

	public PlaybackService getAudioPlaybackService() {
		return mAudioPlayback;
	}

	public void playAudio() {
		if(null==mAudioPlayback)
		{
			return;
		}
		try {
			mAudioPlayback.play();
		} catch (IllegalStateException e) {
			MtkLog.e(TAG, e.getMessage());
		}

	}

	public void pauseAudio() {
		try {
			mAudioPlayback.pause();
		} catch (IllegalStateException e) {
			MtkLog.e(TAG, e.getMessage());
			if (AudioConst.MSG_ERR_CANNOTPAUSE
					.equals(e.getMessage().toString())) {
				throw new IllegalStateException(AudioConst.MSG_ERR_CANNOTPAUSE);
			}else if (AudioConst.MSG_ERR_PAUSEEXCEPTION.equals(e.getMessage()
					.toString())) {
				throw new IllegalStateException(
						AudioConst.MSG_ERR_PAUSEEXCEPTION);
			}else{
				throw new IllegalStateException(e.getMessage());
			}
			

		}

	}

	public void stopAudio() {
		MtkLog.d(TAG, "stopAudio");
		if (mAudioPlayback != null) {
			try {
				mAudioPlayback.stop();
			} catch (IllegalStateException e) {
				MtkLog.e(TAG, e.getMessage());
			}

		}

	}

	// add by xudong for fix cr 384293
	public void stopAudioError() {
		MtkLog.d(TAG, "stopAudio");
		if (mAudioPlayback != null) {
			try {
				mAudioPlayback.stopError();
			} catch (IllegalStateException e) {
				MtkLog.e(TAG, e.getMessage());
			}
		}
	}

	// end
	public void finishAudioService() {
		if (null != mAudioPlayback) {
			try {
				mAudioPlayback.stop();
			} catch (IllegalStateException e) {
				MtkLog.e(TAG, e.getMessage());
			}
		}

	}

	public void playNextAudio() {
		if (null != mAudioPlayback) {
			try {
				mAudioPlayback.playNext();
			} catch (IllegalStateException e) {
				MtkLog.i(TAG, e.getMessage());
			}

		}
	}

	public void playPrevAudio() {
		if (null != mAudioPlayback) {
			try {
				mAudioPlayback.playPrevious();
			} catch (IllegalStateException e) {
				MtkLog.i(TAG, e.getMessage());
			}

		}
	}

	public boolean isAudioFast() {
		if(null==mAudioPlayback)
		{
			return false;
		}
		return (mAudioPlayback.getPlayStatus() == AudioConst.PLAY_STATUS_FR)
				|| (mAudioPlayback.getPlayStatus() == AudioConst.PLAY_STATUS_FF)
				|| (mAudioPlayback.getPlayStatus() == AudioConst.PLAY_STATUS_SF)
				|| (mAudioPlayback.getPlayStatus() == AudioConst.PLAY_STATUS_SR);
	}

	public boolean isAudioPlaying() {

		if (null != mAudioPlayback) {
			return mAudioPlayback.isPlaying();
		}
		return false;
	}

	public boolean isAudioPause() {

		if (null == mAudioPlayback) {
			return false;
		}

		return mAudioPlayback.getPlayStatus() == AudioConst.PLAY_STATUS_PAUSED;
	}

	public boolean isAudioStarted() {
		if (null == mAudioPlayback) {
			return false;
		}
		int status = mAudioPlayback.getPlayStatus();
		return ((status >= AudioConst.PLAY_STATUS_STARTED) && (status < AudioConst.PLAY_STATUS_STOPPED));
	}

	public boolean isAudioStoped() {
		if (null == mAudioPlayback) {
			return true;
		}
		return mAudioPlayback.getPlayStatus() == AudioConst.PLAY_STATUS_STOPPED;
	}

	public void seekToCertainTime(long time) throws NotSupportException{
		if (null != mAudioPlayback) {
				mAudioPlayback.seekToCertainTime(time);
		}
	}
	public boolean canSeek(){
		return mAudioPlayback.canSeek();
	}
	public void fastForwardAudio() throws NotSupportException {
		if (mAudioPlayback == null) {
			return;
		}

		try {
			mAudioPlayback.fastForward();

		} catch (IllegalStateException e) {
			MtkLog.e(TAG, e.getMessage());

		}

	}

	public int getAudioSpeed() {
		if (null == mAudioPlayback) {
			return 0;
		}
		return mAudioPlayback.getSpeed();
	}

	public void setAuidoSpeed(int speed) {
		if (null != mAudioPlayback) {
			mAudioPlayback.setSpeed(speed);
		}
	}

	public int getAudioStatus() {
		if (null == mAudioPlayback) {
			return -1;
		}
		return mAudioPlayback.getPlayStatus();
	}

	public void fastRewindAudio() throws NotSupportException {
		if (mAudioPlayback == null) {
			return;
		}
		try {
			mAudioPlayback.fastRewind();
		} catch (IllegalStateException e) {
			MtkLog.e(TAG, e.getMessage());
		}

	}

	public long getPlaybackProgress() {
		if (mAudioPlayback != null) {
			return mAudioPlayback.getPlaybackProgress();
		} else {
			return 0;
		}

	}

	public long getTotalPlaybackTime() {
		if (null == mAudioPlayback) {
			return 0;
		}
		return mAudioPlayback.getTotalPlaybackTime();
	}

	public Bitmap getAlbumArtwork(int srcType, String path, int width,
			int height) {
		return CorverPic.getInstance().getAudioCorverPic(srcType, path, width,
				height);
	}

	public String getMusicAlbum() {
		if(null==mAudioPlayback)
		{
			return "";
		}
		return mAudioPlayback.getAlbum();
	}

	public String getMusicArtist() {
		if(null==mAudioPlayback)
		{
			return "";
		}
		return mAudioPlayback.getArtist();
	}

	public String getMusicGenre() {
		return mAudioPlayback.getGenre();
	}

	public String getMusicTitle() {
		if (null == mAudioPlayback) {
			return "";
		}
		return mAudioPlayback.getTitle();
	}

	public String getMusicYear() {
		return mAudioPlayback.getYear();
	}

	public Vector<LyricTimeContentInfo> getLrcInfo() {

		// TODO change
		Vector<LyricTimeContentInfo> lrcInfo = new Vector<LyricTimeContentInfo>();
		String mp3Path = mPlayList.getCurrentPath(Const.FILTER_AUDIO);
		try {
			int index = mp3Path.lastIndexOf(".");
			if (index == -1) {
				return lrcInfo;
			}
			String lrcPath = mp3Path.substring(0, index) + ".lrc";
			MtkLog.i(TAG, "  lrcPath =" + lrcPath + "  mp3Path=" + mp3Path);
			File lrcFile = new File(lrcPath);
			if (lrcFile.exists()) {
				mLyric = new Lyric(lrcPath);
				lrcInfo = mLyric.getLyricTimeContentInfo();
			}

		} catch (Exception e) {
			MtkLog.i(TAG, e.getMessage());
			return null;
		}
		return lrcInfo;
	}

	public int getLrcLine(long time) {
		if (mLyric != null) {
			return mLyric.getLine(time);
		} else {
			return 0;
		}
	}

	public String getCurrentPath(int type) {
		return mPlayList.getCurrentPath(type);
	}

	// public String getAudioFilenmae() {
	//
	// return mPlayList.getCurrentFileName(Const.FILTER_AUDIO);
	// }

	public String getAudioPageSize() {
		return (mPlayList.getCurrentIndex(Const.FILTER_AUDIO) + 1) + "/"
				+ mPlayList.getFileNum(Const.FILTER_AUDIO);
	}

	public void setPreparedListener(OnPreparedListener listener) {

		mPreparedListener = listener;

	}

	// fix bug by hs_hzd
	public void setErrorListener(OnErrorListener listener) {

		mErrorListener = listener;

	}

	public void removeErrorListener() {
		if (mAudioPlayback != null) {
			mAudioPlayback.unregisterAudioErrorListener();
			mErrorListener = null;
		}
	}

	public void setCompletionListener(OnCompletionListener listener) {

		mCompletionListener = listener;

	}

	public void setTotalTimeUpdateListener(OnTotalTimeUpdateListener listener) {

		mTotalTimeUpdateListener = listener;
	}

	public void setSpeedUpdateListener(OnSpeedUpdateListener listener) {
		mSpeedUpdateListener = listener;
	}

	public void setReplayListener(OnRePlayListener listener) {
		mReplayListener = listener;
	}

	public int getRepeatModel(int type) {
		if (null == mPlayList) {
			return 0;
		}
		return mPlayList.getRepeatMode(type);
	}

	public boolean getShuffleMode(int fileType) {
		return mPlayList.getShuffleMode(fileType);
	}

	public void setShuffle(int type, boolean model) {
		mPlayList.setShuffleMode(type, model);
	}

	public void initPhoto(Display display, EffectView view) {
		mPlayList = PlayList.getPlayList();
		mImageEffectView = view;
		if (null == mImageManager) {
			mImageManager = new Imageshowimpl(display);
		}
	}

	/*public void initThrdPhoto(Display display, Context context) {
		//add by shuming for fix CR: DTV00410744
		mPlayList = PlayList.getPlayList();
		mPhotoManager = PhotoManager.getInstance(context);
		mPhotoPlayback = mPhotoManager.getPlayback();

	}*/

	public void stopPlayWork() {

		if (null != mPlayLoader) {
			mPlayLoader.clearQueue();
			mPlayLoader = null;
		}
	}

	public void playThrdPhoto() {
		try {
			String path = mPlayList.getCurrentPath(Const.FILTER_IMAGE);
			//setPicSetting();
			if (mPlayLoader != null) {
				mPlayLoader.clearQueue();
				mPlayLoader.addWork(new PlayWork(mPhotoPlayback, path));
			} else {

			}

		} catch (NotSupportException e) {
			e.printStackTrace();
		}
	}

	public void playThrdCurPhoto() {
		try {
			String path = mPlayList.getNext(Const.FILTER_IMAGE, Const.AUTOPLAY);
			//setPicSetting();
			if (mPlayLoader != null) {
				mPlayLoader.clearQueue();
				mPlayLoader.addWork(new PlayWork(mPhotoPlayback, path));
			}

		} catch (NotSupportException e) {
			e.printStackTrace();
		}
	}

	public void playThrdNextPhoto() {
		try {
			String path = mPlayList.getNext(Const.FILTER_IMAGE,
					Const.MANUALNEXT);
			//setPicSetting();
			if (mPlayLoader != null) {
				mPlayLoader.clearQueue();
				mPlayLoader.addWork(new PlayWork(mPhotoPlayback, path));
			}

		} catch (NotSupportException e) {
			e.printStackTrace();
		}
	}

	public void playThrdPrePhoto() {
		try {
			String path = mPlayList
					.getNext(Const.FILTER_IMAGE, Const.MANUALPRE);
			MtkLog.i(TAG, "  thrdPrePhoto path = " + path);
			//setPicSetting();
			if (mPlayLoader != null) {
				mPlayLoader.clearQueue();
				mPlayLoader.addWork(new PlayWork(mPhotoPlayback, path));
			}
		} catch (NotSupportException e) {
			e.printStackTrace();
		}
	}

	/**
	 * press chup get play path
	 * 
	 * @return
	 */
	public String playThrdPhotoNextPath() {
		String path = null;
		try {
			path = mPlayList.getNext(Const.FILTER_IMAGE, Const.MANUALNEXT);
			if (mPlayLoader != null) {
				mPlayLoader.clearQueue();
				mPlayLoader.addWork(new PlayWork(mPhotoPlayback, path));
			}
		} catch (NotSupportException e) {
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * press chwn get play path
	 * 
	 * @return
	 */
	public String playThrdPhotoPrePath() {
		return mPlayList.getNext(Const.FILTER_IMAGE, Const.MANUALPRE);
	}

	/*
	 * public void repeatThrdPhoto(){ mPlayList = PlayList.getPlayList(); String
	 * path =mPlayList.getCurrentPath(Const.FILTER_IMAGE);
	 * //mPhotoPlayback.play(path); }
	 */
	public void closeThrdPhoto() {
		try {
			if (mPhotoPlayback != null) {
				new Thread(new Runnable() {

					public void run() {

						mPhotoPlayback.close();
					}

				}).start();

			}
		} catch (NotSupportException e) {

		}
	}

	public void setPhotoCompleteListener(
			OnPhotoCompletedListener completeListener) {
		mImageManager.setCompleteListener(completeListener);
	}

	public void setPhotoDecodeListener(OnPhotoDecodeListener decodeListener) {
		mImageManager.setDecodeListener(decodeListener);
	}

	public PhotoUtil transfBitmap(String path) {

		if (null == path || "".equals(path)) {
			return null;
		}
		try {
			return mImageManager.transfBitmap(path);

		} catch (OutOfMemoryError error) {
			MtkLog.i(TAG, " transfBitmap  " + error.getMessage());
			return null;
		}
	}

	/**
	 * get current picture
	 * 
	 * @return current picture of Bitmap
	 * @throws IllegalTypeException
	 */
	public PhotoUtil getCurImageBitmap() {

		return mImageManager.curShow();
	}

	public void setImageSource(int source) {
		mImageManager.setLocOrNet(source);
	}

	/**
	 * get next picture
	 * 
	 * @return picture of Bitmap
	 * @throws IllegalTypeException
	 */
	public PhotoUtil getNextImageBitmap() {
		return mImageManager.getNext();
	}

	/**
	 * get per picture
	 * 
	 * @return picture of Bitmap
	 * @throws IllegalTypeException
	 */
	public PhotoUtil getPreImageBitmap() {
		return mImageManager.getPre();
	}

	// public Bitmap capturePic(View view) {
	// return mImageManager.capturePic(view);
	// }

	public PhotoUtil getNextImage() {
		return mImageManager.autoPlayNext();
	}

	public int getImageEffect() {
		if (null == mImageEffectView) {
			return ConstPhoto.DEFAULT;
		}
		return mImageEffectView.getEffectValue();
	}

	public Bitmap setLeftRotate(Bitmap bitmap) {
		return mImageManager.leftRotate(bitmap);
	}

	public Bitmap setRightRotate(Bitmap bitmap) {
		return mImageManager.rightRotate(bitmap);
	}

	public void zoomImage(ImageView view, int inOrOut, Bitmap bitmap, int size) {
		mImageManager.Zoom(view, inOrOut, bitmap, size);
	}

	// add by xiaojie fix cr DTV00389237
	public int getCurrentZoomSize() {
		return mImageManager.getZoomOutSize();
	}

	public int getCurrentImageIndex() {

		return mPlayList.getCurrentIndex(Const.FILTER_IMAGE) + 1;
	}

	public int getImageNumber() {

		return mPlayList.getFileNum(Const.FILTER_IMAGE);
	}

	public String getImagePageSize() {
		return (mPlayList.getCurrentIndex(Const.FILTER_IMAGE) + 1) + "/"
				+ mPlayList.getFileNum(Const.FILTER_IMAGE);
	}

	public String getThrdPhotoPageSize() {
		return (mPlayList.getCurrentIndex(Const.FILTER_IMAGE) + 1) + "/"
				+ mPlayList.getFileNum(Const.FILTER_IMAGE);
	}

	// Public method
	public int getMode(int type) {
		return mPlayList.getRepeatMode(type);
	}

	public void setRepeatMode(int type, int mode) {
		if (null != mPlayList) {
			mPlayList.setRepeatMode(type, mode);
		}
	}

	public int getPhotoOrientation() {
		if (mImageManager == null) {
			mImageManager = new Imageshowimpl();
		}
		return mImageManager.getOrientation();
	}

	public String getPhotoName() {
		if (mImageManager == null) {
			mImageManager = new Imageshowimpl();
		}
		return mImageManager.getName();
	}

	public String getWhiteBalance() {
		if (mImageManager == null) {
			mImageManager = new Imageshowimpl();
		}
		return mImageManager.getWhiteBalance();
	}

	public String getAlbum() {
		if (mImageManager == null) {
			mImageManager = new Imageshowimpl();
		}
		return mImageManager.getAlbum();
	}

	public String getMake() {
		if (mImageManager == null) {
			mImageManager = new Imageshowimpl();
		}
		return mImageManager.getMake();
	}

	public String getModifyDate() {
		if (mImageManager == null) {
			mImageManager = new Imageshowimpl();
		}
		return mImageManager.getModifyDate();
	}

	public int getPhotoDur() {
		if (mImageManager == null) {
			mImageManager = new Imageshowimpl();
		}
		return mImageManager.getDuration();
	}

	public String getPhotoModel() {
		if (mImageManager == null) {
			mImageManager = new Imageshowimpl();
		}
		return mImageManager.getModel();
	}

	public String getFlash() {
		if (mImageManager == null) {
			mImageManager = new Imageshowimpl();
		}
		return mImageManager.getFlash();
	}

	/*
	 * public String getResolution() { if(mImageManager == null){ mImageManager
	 * = new Imageshowimpl(); } return mImageManager.getPwidth() + " x " +
	 * mImageManager.getPheight(); }
	 */
	/* add by lei 2011-12-26, fix 3d photo get resolution issue */
	public String getResolution() {
		if (mImageManager == null) {
			mImageManager = new Imageshowimpl();
		}
		return mImageManager.getResolution();
	}

	public String getPhotoSize() {
		if (mImageManager == null) {
			mImageManager = new Imageshowimpl();
		}
		return mImageManager.getSize();
	}

	public String getFocalLength() {
		if (mImageManager == null) {
			mImageManager = new Imageshowimpl();
		}
		return mImageManager.getFocalLength();
	}

	public void initText(int textSource, TextView tv, ScrollView sv,
			int screenHeight) {
		mPlayList = PlayList.getPlayList();
		// mPlayList.setRepeatMode(Const.FILTER_TEXT, Const.REPEAT_ALL);

		tvManager = TextReader.getTextReader();
		tvManager.setPlayMode(textSource);
		tvManager.setTv(tv);
		tvManager.setScrollView(sv);
		tvManager.setErrorListener(mTextEventListener);
		tvManager.setScreenHeight(screenHeight);
		tvManager.playFirst();

		tvManager.setFontSize(mTextFontSize);
		tvManager.setFontStyle(mTextFontStyle, mTextFontStyle);
		tvManager.setFontColor(mTextFontColor);
	}

	public void setTextListener(ITextEventListener tEventListener) {
		mTextEventListener = tEventListener;
	}

	public void scrollLnUp() {
		tvManager.scrollLnUp();
	}

	public void scrollLnDown() {
		tvManager.scrollLnDown();
	}

	public void pageUp() {
		tvManager.pageUp();
	}

	public void pageDown() {
		tvManager.pageDown();
	}

	public void skipToPage(int pageNum) {
		tvManager.skipToPage(pageNum);
	}

	public void playTextNext() {
		tvManager.playNext();
	}

	public void playTextPrev() {
		tvManager.playPrev();
	}

	public int getTextTotalPage() {
		return tvManager.getTotalPage();
	}

	public String getPageNum() {
		// Modified by Dan for fix bug DTV00375633
		int currentPos = tvManager.getCurPagenum();
		int count = tvManager.getTotalPage();

		String result = "";
		if (currentPos > 0 && count > 0) {
			result = currentPos + "/" + count;
		}

		return result;
	}

	public int getTextCurrentPage() {
		return tvManager.getCurPagenum();
	}

	public String getCurrentFileName(int fileType) {

		return mPlayList.getCurrentFileName(fileType);
	}

	public String getPreviewBuf(String path) {
		tvManager = TextReader.getTextReader();
		return tvManager.getPreviewBuf(path);
	}

	public String getTextPageSize() {
		// Modified by Dan for fix bug DTV00375633
		int currentPos = mPlayList.getCurrentIndex(Const.FILTER_TEXT) + 1;
		int count = mPlayList.getFileNum(Const.FILTER_TEXT);

		String result = "";
		if (currentPos > 0 && count > 0) {
			result = currentPos + "/" + count;
		}

		return result;
	}

	public void setFontColor(String color) {
		mTextFontColor = color;
		tvManager.setFontColor(color);
	}

	public int getFontColor() {
		if (mTextFontColor.equalsIgnoreCase("white")) {
			return 0;
		} else if (mTextFontColor.equalsIgnoreCase("blue")) {
			return 1;
		} else if (mTextFontColor.equalsIgnoreCase("red")) {
			return 2;
		} else if (mTextFontColor.equalsIgnoreCase("green")) {
			return 3;
		} else if (mTextFontColor.equalsIgnoreCase("black")) {
			return 4;
		}
		return 0;
	}

	public void setFontSize(float size) {
		mTextFontSize = size;
		tvManager.setFontSize(size);
	}

	public int getFontSize() {
		if (mTextFontSize == MultiMediaConstant.SMALLSIZE) {
			return 0;
		} else if (mTextFontSize == MultiMediaConstant.MEDSIZE) {
			return 1;
		} else {
			return 2;
		}
	}

	public void setFontStyle(String defaultValue, String content) {
		mTextFontStyle = content;
		tvManager.setFontStyle(defaultValue, content);
	}

	public int getFontStyle() {
		if (mTextFontStyle.equalsIgnoreCase("regular")) {
			return 0;
		} else if (mTextFontStyle.equalsIgnoreCase("italic")) {
			return 1;
		} else if (mTextFontStyle.equalsIgnoreCase("bold")) {
			return 2;
		} else if (mTextFontStyle.equalsIgnoreCase("bold_italic")) {
			return 3;
		} else {
			return 0;
		}
	}

	public String getTextAlbum() {
		String album = mPlayList.getCurrentFileName(Const.FILTER_TEXT);
		int start = 0;
		if (album != null) {
			start = album.indexOf(".");
			if (start + 1 < album.length()) {
				album = album.substring(start + 1);
			} else {
				album = "";
			}
		} else {
			album = "";
		}
		return album + " ...";
	}

	public String getTextSize() {

		// TODO change

		String path = mPlayList.getCurrentPath(Const.FILTER_TEXT);
		if (null == path || "".equals(path)) {
			return "";
		}

		File file = new File(path);
		if (null == file) {
			return "";
		}

		long length = file.length();
		return length + " Byte";
		// if (length / 1024 / 1024 != 0) {
		// return length / 1024 / 1024 + "MB";
		// } else if (length / 1024 != 0) {
		// return length / 1024 + "KB";
		// } else
		// return length + " B";
	}

	public String getNextName(int type) {
		return mPlayList.getNextFileName(type);
	}

	public String getCurrentPhotoPath() {
		String path = mPlayList.getCurrentFileName(Const.FILTER_IMAGE);
		if ("" == path || null == path) {
			return "";
		} else {
			return path;
		}
	}

	public void setThrdPhotoCompelet(IThrdPhotoEventListener thrdPhotoListener) {
		PhotoManager mPhotoManager = PhotoManager.getInstance();
		if (mPhotoPlayback == null) {
			mPhotoPlayback = mPhotoManager.getPlayback();
		}
		mPhotoPlayback.setEventListener(thrdPhotoListener);
	}

	// Added by Dan for fix bug DTV00389362
	private int mLrcOffsetMode = 0;

	public void setLrcOffsetMode(int lrcOffsetMode) {
		mLrcOffsetMode = lrcOffsetMode;
	}

	public int getLrcOffsetMode() {
		return mLrcOffsetMode;
	}

	public int getPlayStatus() {
		if (mAudioPlayback != null) {
			return mAudioPlayback.getPlayStatus();
		}
		return 0;
	}

	// add by shuming for Fix CR DTV00401969
	public int getRotateDigree() {
		return mEffectView.getRotateGigree();
	}

	public void setRotateDigree(int digree) {
		mEffectView.setRotateGigree(digree);
	}
	
	public void setPicSetting() {
		thrdMode = MenuConfigManager.VIDEO_3D_MODE;
		String fileName = getCurrentPhotoPath();
		if (fileName != null && 
				fileName.toLowerCase().endsWith(".mpo")){
			is3DPhotoMpo = true;
		} else {
			is3DPhotoMpo = false;
		}
		MenuConfigManager mConfigManager = MenuConfigManager.getInstance(mContext);
		if(is3DPhotoMpo){
			mConfigManager.setValue(thrdMode, 
					thrdModeValue);
		} else {
			mConfigManager.setValue(MenuConfigManager.VIDEO_3D_MODE, 
					0);
		}
	}
}
