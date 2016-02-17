package com.mediatek.ui.mmp.multimedia;

import java.util.Vector;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mediatek.gamekit.MtkGameKitView;
import com.mediatek.gamekit.MtkGamekitConst;
import com.mediatek.gamekit.GameKitEngine.GameKitListener;
import com.mediatek.media.MtkMediaPlayer;
import com.mediatek.media.NotSupportException;
import com.mediatek.media.MtkMediaPlayer.OnCompletionListener;
import com.mediatek.media.MtkMediaPlayer.OnErrorListener;
import com.mediatek.media.MtkMediaPlayer.OnPreparedListener;
import com.mediatek.media.MtkMediaPlayer.OnRePlayListener;
import com.mediatek.media.MtkMediaPlayer.OnSpeedUpdateListener;
import com.mediatek.media.MtkMediaPlayer.OnTotalTimeUpdateListener;
import com.mediatek.mmpcm.MmpTool;
import com.mediatek.mmpcm.audioimpl.AudioConst;
import com.mediatek.mmpcm.audioimpl.LyricTimeContentInfo;
import com.mediatek.mmpcm.fileimpl.FileConst;
import com.mediatek.mmpcm.mmcimpl.Const;
import com.mediatek.ui.R;
import com.mediatek.ui.mmp.commonview.LrcView;
import com.mediatek.ui.mmp.commonview.ScoreView;
import com.mediatek.ui.mmp.commonview.ControlView.ControlPlayState;
import com.mediatek.ui.mmp.model.MultiFilesManager;
import com.mediatek.ui.mmp.util.AsyncLoader;
import com.mediatek.ui.mmp.util.LogicManager;
import com.mediatek.ui.mmp.util.MultiMediaConstant;
import com.mediatek.ui.mmp.util.AsyncLoader.LoadWork;
import com.mediatek.ui.util.KeyMap;
import com.mediatek.ui.util.MtkLog;

public class MusicPlayActivity extends MediaPlayActivity {

	private static final String TAG = "MusicPlayActivity";

	private static final int PROGRESS_CHANGED = 0;

	private static final int PROGRESS_START = 1;
	// Spectrum
	private static final int PROGRESS_SCOREVIEW = 2;

	private static final int AUDIO_CHANGED = 3;

	private static final int TIME_UPDATE = 4;

	private static final int NOSUPPORT_PLAYNEXT = 5;
	private static final int SPEED_UPDATE = 6;
	private static final int FINISH_AUDIO = 7;
	private static final int CLEAR_LRC = 8;
	private static final int PLAY_END = 9;
	private static final int DISMISS_NOT_SUPPORT = 10;
	private static final int RUN_LUA_CHUNK = 11;
	private static final int RUN_LUA_ANIMA = 12;
	private static final int DELAY_RUN_MILLIS = 2000;
	private static final int DELAYMILLIS = 400;

	private static final long SEEK_DURATION = 3000;

	// //add by xudong chen 20111204 fix DTV00379662
	public static final long SINGLINE = 1;
	public static final long MULTILINE = 8;
	public static final long OFFLINE = 0;
	// end
	private LinearLayout vLayout;

	private ImageView vThumbnail;

	private ScoreView mScoreView;

	private LrcView mLrcView;
	
	private MtkGameKitView mSurfaceView;
	private boolean mIsClose3D = false;

	private Vector<LyricTimeContentInfo> lrc_map;

	private boolean playFlag = true;
	private boolean isActivityLiving = true;
	private boolean retrunFromTipDismis = false;
	private boolean isCenterKey2Pause = false;

	private int mAudioSource = 0;

	private int mAudioFileType = 0;
	private int onerrorSendWhat=0;

	private int mTotalTime;

	private Handler myHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			//by lei add for play 3D animal start
			case RUN_LUA_CHUNK:{				
				luaPlayAnim();				
				break;
			}
			
			case RUN_LUA_ANIMA:{				
				luaRotationAnim();
				break;
			}
			//by lei add for play 3D animal end
			case PROGRESS_CHANGED: {
				if (mControlView != null) {
					int progress = (int) mLogicManager.getPlaybackProgress();
					if (progress >= 0) {
						mControlView.setCurrentTime(progress);
						mControlView.setProgress((int) progress);
					}
				}
				sendEmptyMessageDelayed(PROGRESS_CHANGED, DELAYMILLIS);
				break;
			}
			case PROGRESS_START: {
				if (null == lrc_map || (lrc_map.size() == 0)
						|| null == mLrcView) {
					return;
				}
				int line = mLogicManager.getLrcLine(mLogicManager
						.getPlaybackProgress());

				if (line != -1) {
					mLrcView.setlrc(line, false);
				}

				if (line == lrc_map.size() - 1) {
					return;
				}

				sendEmptyMessageDelayed(PROGRESS_START, DELAYMILLIS);
				break;
			}

			case PROGRESS_SCOREVIEW: {
				if (!isShowSpectrum()||mLogicManager.isMute()) {
					return;
				}
				if (hasMessages(PROGRESS_SCOREVIEW)) {
					removeMessages(PROGRESS_SCOREVIEW);
				}
				mScoreView.update(mLogicManager.getAudSpectrum());
				mScoreView.invalidate();
				sendEmptyMessageDelayed(PROGRESS_SCOREVIEW, DELAYMILLIS);

				break;
			}
			case AUDIO_CHANGED: {
				setMusicInfo();
				break;
			}
			case TIME_UPDATE: {
				updateTime(mTotalTime);
				break;
			}
			case NOSUPPORT_PLAYNEXT:
				if (isActivityLiving) {
					dismissNotSupprot();
				}
				mLogicManager.playNextAudio();
				setMusicInfo();
				//by lei add for play 3D animal start
				luaStopAnim();
				//by lei add for play 3D animal end
				break;
			case SPEED_UPDATE:
				MtkLog.i(TAG, "  SPEED_UPDATE  speed:" + SPEED_UPDATE);
				// set play icon.
				if (mControlView != null) {
					mLogicManager.setAuidoSpeed(1);
					mControlView.onFast(1, 1, Const.FILTER_AUDIO);
				}
				break;
			// add by keke 1215 fix DTV00380491
			case FINISH_AUDIO: {
				/* fix cr DTV00386326 by lei 1228 */
				mLogicManager.unbindService(MusicPlayActivity.this);
				mLogicManager.finishAudioService();
				MusicPlayActivity.this.finish();
			}
				break;
			case CLEAR_LRC: {
				clearLrc();
			}
				// add by shuming fix CR 00386020
			case DISMISS_NOT_SUPPORT: {
				if (isActivityLiving) {
					dismissNotSupprot();
				}
			}
				// end

				break;
			// Added by yongzheng for fix CR DTV00388558 12/1/12
			case PLAY_END:
				if (mControlView != null) {
					mControlView.setProgress(mControlView.getProgressMax());
					if (!isNotSupport) {
						mControlView.setCurrentTime(mControlView.getProgressMax());
					}
					mControlView.setEndtime(mControlView.getProgressMax());
				}			
				break;
			// end
			default:
				break;
			}

		}
	};
	//by lei add for play 3D animal start
	private void luaPlayAnim(){
		if (mIsClose3D) return;
		String chunk = "openAnim();";
		mSurfaceView.runLuaChunkQueueEvent(chunk);
	}

	private void luaStopAnim(){
		if (mIsClose3D) return;
		String chunk = "closeAnim();";
		mSurfaceView.runLuaChunkQueueEvent(chunk);
	}
	
	//by lei add for play 3D animal end
	private void luaRotationAnim(){
		if (mIsClose3D) return;
		String chunk = "rotationAnim();";
		mSurfaceView.runLuaChunkQueueEvent(chunk);
	}
						//by lei add.
	private GameKitListener mGkListener = new GameKitListener(){

		@Override
		public void onMessage(int type, int value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMessage(String from, String to, String subject,
				String body) {
			MtkLog.d(TAG, "subject : " +subject + ":"+ body);
			if (subject != null && subject.equals(MtkGamekitConst.AIMAL_END)){
				if (body != null){
					if (body.equals(MtkGamekitConst.AIMAL_Open)){
						myHandler.removeMessages(RUN_LUA_ANIMA);
						myHandler.sendEmptyMessage(RUN_LUA_ANIMA);
					}else if(body.equals(MtkGamekitConst.AIMAL_Close)){
						//by lei add.
						myHandler.removeMessages(RUN_LUA_CHUNK);
						myHandler.sendEmptyMessage(RUN_LUA_CHUNK);
					}
				}
			}
			
		}};
	
	private void updateTime(int totalTime) {
		if (null != mControlView) {
			mControlView.setEndtime(totalTime);
			mControlView.setProgressMax(totalTime);
		}

		if (null != mInfo && mInfo.isShowing()) {
			mInfo.updateTime(totalTime);
		}
	}

	private ControlPlayState mControlImp = new ControlPlayState() {

		public void play() {
			/* add by lei for fix cr 386020 */
			if (isNotSupport || null == mLogicManager.getAudioPlaybackService()) {
				return;
			}

			/* add by lei for fix cr DTV00381177&DTV00390959 */
			MtkLog.e(TAG, "***********show Spectrum****************"
					+ isShowSpectrum());
			if (isShowSpectrum()) {
				//add by keke 2.1 for DTV00393701
				mScoreView.clearTiles();
				mScoreView.setVisibility(View.VISIBLE);
				myHandler.sendEmptyMessage(PROGRESS_SCOREVIEW);
			}
			myHandler.sendEmptyMessage(PROGRESS_CHANGED);
			myHandler.sendEmptyMessage(PROGRESS_START);
			mLogicManager.playAudio();

		}

		public void pause() {
			/* add by lei for fix cr 386020 */
			if (isNotSupport || null == mLogicManager.getAudioPlaybackService()) {
				return;
			}
			// change by shuming fix CR 00386020
			try {
				mLogicManager.pauseAudio();
			} catch (Exception e) {
				if ((AudioConst.MSG_ERR_CANNOTPAUSE).equals(e.getMessage()
						.toString())
						&& isCenterKey2Pause) {
					isCenterKey2Pause = false;
					displayErrorMessage(AudioConst.MSG_FILE_NOT_SUPPORT, 0);
					if (isActivityLiving) {
						myHandler.sendEmptyMessageDelayed(DISMISS_NOT_SUPPORT,
								3000);
						throw new IllegalStateException(
								AudioConst.MSG_ERR_CANNOTPAUSE);
					}
				}else if (AudioConst.MSG_ERR_PAUSEEXCEPTION.equals(e.getMessage()
						.toString())) {
					throw new IllegalStateException(
							AudioConst.MSG_ERR_PAUSEEXCEPTION);
				}else{
					throw new IllegalStateException(e.getMessage());
				}

				// TODO: handle exception
			}
			// end

			/* add by lei for fix cr DTV00381177&DTV00390959 */
			myHandler.removeMessages(PROGRESS_SCOREVIEW);
			// change by shuming fix CR DTV00
			myHandler.removeMessages(PROGRESS_START);
			myHandler.removeMessages(PROGRESS_CHANGED);
			myHandler.removeMessages(AUDIO_CHANGED);
			myHandler.removeMessages(SPEED_UPDATE);
			// removeMessages();
		}
	};

	private OnErrorListener mErrorListener = new OnErrorListener() {

		private Runnable mErrorRunnable;

		// @Override if add "override" P4 will build failure
		public boolean onError(MtkMediaPlayer arg0, final int what,
				final int extra) {
			MtkLog.i(TAG, "OnErrorListener  targ1:" + what + "  arg2" + extra
					+ " " + System.currentTimeMillis());
			/* add by lei for fix cr 386020 */
			isNotSupport = true;
			//add by xudong for fix cr 384293
			mLogicManager.stopAudioError();
			//end
			onerrorSendWhat=what;
			if (mErrorRunnable != null) {
				myHandler.removeCallbacks(mErrorRunnable);
			}
			mErrorRunnable = new Runnable() {

				// @Override
				public void run() {
					displayErrorMessage(what, extra);
					myHandler.sendEmptyMessageDelayed(NOSUPPORT_PLAYNEXT, 3000);

					if (isNotSupport) {
						mScoreView.setVisibility(View.INVISIBLE);
						myHandler.removeMessages(PROGRESS_SCOREVIEW);
						// removeScore(true);
					}
					setMusicInfo();
					mErrorRunnable = null;

				}
			};
			myHandler.postDelayed(mErrorRunnable, 100);

			return false;
		}
	};

	/* add by lei for fix cr 386270 */
	protected void displayErrorMessage(int what, int extra) {
		if (!isActivityLiving) {
			return;
		}
		switch (what) {
		case AudioConst.MSG_AUDIO_NOT_SUPPORT:
			onNotSuppsort(getResources().getString(
					R.string.mmp_audio_notsupport));
			break;
		case AudioConst.MSG_VIDEO_NOT_SUPPORT:
			onNotSuppsort(getResources().getString(
					R.string.mmp_video_notsupport));
			break;
		case AudioConst.MSG_AV_NOT_SUPPORT:
			onNotSuppsort(getResources().getString(
					R.string.mmp_video_notsupport));
			break;
		case AudioConst.MSG_FILE_NOT_SUPPORT:
			setIsFileNotSupport(true);
			onNotSuppsort(getResources()
					.getString(R.string.mmp_file_notsupport));
			break;
		case AudioConst.MSG_FILE_CORRUPT:
			setIsFileNotSupport(true);
			onNotSuppsort(getResources().getString(R.string.mmp_file_corrupt));
			break;
		default:
			onNotSuppsort(getResources()
					.getString(R.string.mmp_file_notsupport));
			break;
		}

	}

	private OnTotalTimeUpdateListener mTotalTimeUpdateListener = new OnTotalTimeUpdateListener() {

		public void onTotalTimeUpdate(MtkMediaPlayer arg0, int totalTime,
				int arg2) {

			MtkLog.i(TAG,
					"  mTotalTimeUpdateListener  onTotalTimeUpdate  totalTime:"
							+ totalTime);
			mTotalTime = totalTime;
			myHandler.sendEmptyMessage(TIME_UPDATE);
		}
	};

	private OnSpeedUpdateListener mSpeedUpdateListener = new OnSpeedUpdateListener() {

		public void onSpeedUpdate(MtkMediaPlayer arg0, int speed, int arg2) {
		/*move it to replayListener by lei*/
		/*			
			long progress = mLogicManager.getPlaybackProgress();
			if (speed == 100000 && progress >= 0 && progress <= 10) {
				myHandler.sendEmptyMessage(SPEED_UPDATE);
			}

		*/}
	};

	
	private OnRePlayListener mReplayListener = new OnRePlayListener() {
		public void onRePlay(MtkMediaPlayer arg0, int arg1, int arg2) {
			myHandler.sendEmptyMessage(SPEED_UPDATE);
			
		}
	};

	private OnPreparedListener mPreparedListener = new OnPreparedListener() {

		public void onPrepared(MtkMediaPlayer mp) {
			MtkLog.i(TAG, " audio  OnPrepared   -------------- ");
			/* add by lei for fix cr 386020 */
			isNotSupport = false;
			onerrorSendWhat=0;
			setIsFileNotSupport(false);
			if (null != mControlView) {
				mControlView.reSetAudio();
				mControlView.setProgress(0);
				mControlView.showProgress();
			}
			removeMessages();
			myHandler.sendEmptyMessage(AUDIO_CHANGED);
			myHandler.sendEmptyMessageDelayed(PROGRESS_CHANGED, DELAYMILLIS);
			myHandler.sendEmptyMessage(PROGRESS_START);
			removeScore(isHideSperum);
			//by lei add.
			//myHandler.sendEmptyMessageDelayed(RUN_LUA_CHUNK, DELAY_RUN_MILLIS);
		}

	};

	private void setMusicInfo() {
		// Added by Dan for fix bug DTV00384892
		if (menuDialog != null && menuDialog.isShowing()) {
			menuDialog.dismiss();
		}

		lrc_map = mLogicManager.getLrcInfo();
		if (mControlView != null) {
			mControlView.setRepeat(Const.FILTER_AUDIO);
			long times = mLogicManager.getTotalPlaybackTime();
			// Modified by yongzheng for fix CR DTV00388558 12/1/12
			if (isNotSupport
					&& onerrorSendWhat != AudioConst.MSG_AUDIO_NOT_SUPPORT) {
				mControlView.hideProgress();
			} else {
				mControlView.setCurrentTime(0);
				if (onerrorSendWhat != AudioConst.MSG_AUDIO_NOT_SUPPORT) {
					mControlView.setEndtime((int) times);
				}

				if (times == 0) {
					mControlView.setProgress((int) times);
				} else {
					mControlView.setProgressMax((int) times);
				}
			}
			mControlView.setVolumeMax(maxVolume);
			mControlView.setCurrentVolume(currentVolume);
			mControlView.setFileName(mLogicManager
					.getCurrentFileName(Const.FILTER_AUDIO));
			mControlView.setFilePosition(mLogicManager.getAudioPageSize());
		}
		initLrc(mPerLine);

		if (null != mInfo && mInfo.isShowing()) {
			mInfo.setAudioView();
		}

		final String path=mLogicManager.getCurrentPath(Const.FILTER_AUDIO);
		if (null!=path) {
			if((path.endsWith("mp3") || path.endsWith("MP3"))){
				AsyncLoader load =AsyncLoader.getInstance(1); 
				load.addWork(new LoadWork<Bitmap>() {
					
					public Bitmap load() {
						 Bitmap bmp = mLogicManager.getAlbumArtwork(mAudioFileType,
								path, vThumbnail
								.getWidth(), vThumbnail.getHeight());
						return bmp;
					}
					
					public void loaded(final Bitmap result) {
						vThumbnail.post(new Runnable() {
						
						public void run() {
							vThumbnail.setImageBitmap(result);
						}
					});
					}
				});
			}else{
				vThumbnail.setImageBitmap(null);
			}
			
			
//			new Thread(new Runnable() {
//				
//				public void run() {
//					final Bitmap bmp = mLogicManager.getAlbumArtwork(mAudioFileType,
//							path, vThumbnail
//							.getWidth(), vThumbnail.getHeight());
//					vThumbnail.post(new Runnable() {
//						
//						public void run() {
//							vThumbnail.setImageBitmap(bmp);
//						}
//					});
//				}
//			}).start();

		
			
		}
	}

	/* true playing(ff, fr,fb), else stop or pause */
	// private boolean isPlay = true;
	private OnCompletionListener mCompletionListener = new OnCompletionListener() {

		public boolean onCompletion(MtkMediaPlayer mp, int flag, int arg2) {

			MtkLog.i(TAG, "-------------- Completion ----------------- flag="
					+ flag);

			if (flag == 1) {
				myHandler.sendEmptyMessage(FINISH_AUDIO);
				isNotSupport = true;
			} else {
				myHandler.sendEmptyMessage(PLAY_END);
				myHandler.removeMessages(CLEAR_LRC);
				myHandler.sendEmptyMessage(CLEAR_LRC);
				removeMessages();
			}
			luaStopAnim(); //by lei add for play 3D animal
			return false;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mmp_musicplay);
		findView();
		getIntentData();
		initData();
		// add by keke for fix DTV00380638
		mControlView.setRepeatVisibility(Const.FILTER_AUDIO);
		showPopUpWindow(vLayout);
	}

/*	*//**
	 * Set Spreum status, true hide, false display.
	 *//*
	private boolean mIsHideSperum = false;*/

	public void removeScore(boolean ishide) {

		if (ishide) {

			mScoreView.setVisibility(View.INVISIBLE);
			myHandler.removeMessages(PROGRESS_SCOREVIEW);
		} else {
			mScoreView.clearTiles();
			mScoreView.setVisibility(View.VISIBLE);
			myHandler.sendEmptyMessageDelayed(PROGRESS_SCOREVIEW, DELAYMILLIS);
		}

	}
	
	public void removeScorePause(){
		mScoreView.setVisibility(View.INVISIBLE);
	}

	public boolean isShowSpectrum() {
		return !isHideSperum;
		// return mScoreView.isShown();
	}

	public void initLrc(int perline) {
		myHandler.removeMessages(PROGRESS_START);
		mLrcView.setVisibility(View.VISIBLE);
		if (null != lrc_map && lrc_map.size() > 0) {
			MtkLog.d(TAG, "perline:" + perline);
			mLrcView.init(lrc_map, perline);
			myHandler.sendEmptyMessageDelayed(PROGRESS_START, DELAYMILLIS);
		} else {
			mLrcView.noLrc(getString(R.string.mmp_info_nolrc));
		}
	}

	public void setLrcLine(int perline) {
		mLrcView.setVisibility(View.VISIBLE);
		if (null != lrc_map && lrc_map.size() > 0) {
			if (null != mLrcView) {
				mLrcView.setLines(perline);
				long progress = mLogicManager.getPlaybackProgress();
				if (progress >= 0) {
					int currentline = mLogicManager.getLrcLine(progress);
					mLrcView.setlrc(currentline, true);
				}
				myHandler.sendEmptyMessageDelayed(PROGRESS_START, DELAYMILLIS);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void hideLrc() {
		myHandler.removeMessages(PROGRESS_START);
		mLrcView.setVisibility(View.INVISIBLE);
	}

	private void getIntentData() {

		mAudioSource = MultiFilesManager.getInstance(this)
				.getCurrentSourceType();

		switch (mAudioSource) {
		case MultiFilesManager.SOURCE_LOCAL:
			mAudioSource = AudioConst.PLAYER_MODE_LOCAL;
			mAudioFileType = FileConst.SRC_USB;
			break;
		case MultiFilesManager.SOURCE_SMB:
			mAudioSource = AudioConst.PLAYER_MODE_SAMBA;
			mAudioFileType = FileConst.SRC_SMB;
			break;
		case MultiFilesManager.SOURCE_DLNA:
			mAudioSource = AudioConst.PLAYER_MODE_DLNA;
			mAudioFileType = FileConst.SRC_DLNA;
			break;
		default:
			break;
		}
	}

	private void initData() {
		mLogicManager = LogicManager.getInstance(this);
		mLogicManager.setPreparedListener(mPreparedListener);

		mLogicManager.setCompletionListener(mCompletionListener);
		mLogicManager.setTotalTimeUpdateListener(mTotalTimeUpdateListener);
		mLogicManager.setSpeedUpdateListener(mSpeedUpdateListener);
		mLogicManager.setErrorListener(mErrorListener);
		mLogicManager.setReplayListener(mReplayListener);
		mLogicManager.initAudio(this, mAudioSource);

		initVulume(mLogicManager);
		isNotSupport = true;
		isActivityLiving = true;
		// add by xudong fix cr DTV00385993
		retrunFromTipDismis = false;
		isCenterKey2Pause = false;
		// end

	}

	private void findView() {
		vLayout = (LinearLayout) findViewById(R.id.mmp_music_top);
		vThumbnail = (ImageView) findViewById(R.id.mmp_music_img);
		mScoreView = (ScoreView) findViewById(R.id.mmp_music_tv);
		mLrcView = (LrcView) findViewById(R.id.mmp_music_lrc);
		if (!mIsClose3D){
			mSurfaceView = (MtkGameKitView)findViewById(R.id.gl_surface_view);
			MtkLog.d(TAG, "mSurfaceView register listener");
			mSurfaceView.removeListener();
			mSurfaceView.addGamekitListener(mGkListener);
			//by lei add.
			myHandler.sendEmptyMessageDelayed(RUN_LUA_CHUNK, DELAY_RUN_MILLIS);
			MtkLog.d("Ogre", mSurfaceView.getClass().toString());
		}
		getPopView(R.layout.mmp_popupmusic, MultiMediaConstant.AUDIO,
				mControlImp);

		mControlView.setFilePosition(mLogicManager.getAudioPageSize());
	}

	/**
	 * {@inheritDoc} fix bug DTV00365251 by lei add.
	 */
	public boolean dispatchKeyEvent(KeyEvent event) {
		MtkLog.e("leilei", " set auido only---");
		if (mLogicManager.isAudioOnly()) {
			/* by lei add for fix cr DTV00390970 */
			if (event.getAction() == KeyEvent.ACTION_UP) {
				mLogicManager.setAudioOnly(false);
			}
			return true;
		}

		return super.dispatchKeyEvent(event);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyMap.KEYCODE_DPAD_CENTER:
		case KeyMap.KEYCODE_MTKIR_PLAYPAUSE:
			// add by shuming fix CR 00386020
			if (mControlView.isPalying()) {
				isCenterKey2Pause = true;
			} else {
				isCenterKey2Pause = false;
			}
			// end
			setPlayerStop(false);
			if (isNotSupport) {
				return true;
			}
			// Added by yognzheng for fix CR DTV00390968 16/1/12
			if (mTipsDialog != null
					&& mTipsDialog.isShowing()
					&& mTipsDialog.getTitle().equals(
							getResources().getString(
									R.string.mmp_file_notsupport))) {
				mTipsDialog.dismiss();
			}
			break;
		// Added by yognzheng for fix CR DTV00390968 16/1/12
	/*	case KeyMap.KEYCODE_MENU: {
			if (mTipsDialog != null
					&& mTipsDialog.isShowing()
					&& mTipsDialog.getTitle().equals(
							getResources().getString(
									R.string.mmp_file_notsupport))) {
				mTipsDialog.dismiss();
			}
		}
			break;*/
		case KeyMap.KEYCODE_MTKIR_CHDN:
		case KeyMap.KEYCODE_MTKIR_PREVIOUS: {
			if (isValid()) {
				dismissNotSupprot();
				myHandler.removeMessages(NOSUPPORT_PLAYNEXT);
				// add by xiaojie fix cr DTV00379650
				myHandler.removeMessages(CLEAR_LRC);
				myHandler.sendEmptyMessage(CLEAR_LRC);
				// end
				mLogicManager.playPrevAudio();
				myHandler.removeMessages(PROGRESS_START);
				luaStopAnim(); //by lei add for play 3D animal start
			}
			return true;
		}
		case KeyMap.KEYCODE_MTKIR_CHUP:
		case KeyMap.KEYCODE_MTKIR_NEXT: {
			if (isValid()) {
				dismissNotSupprot();
				myHandler.removeMessages(NOSUPPORT_PLAYNEXT);
				// add by xiaojie fix cr DTV00379650
				myHandler.removeMessages(CLEAR_LRC);
				myHandler.sendEmptyMessage(CLEAR_LRC);
				// end
				mLogicManager.playNextAudio();
				myHandler.removeMessages(PROGRESS_START);
				luaStopAnim(); //by lei add for play 3D animal start
			}
			return true;
		}
		case KeyMap.KEYCODE_DPAD_LEFT:
		case KeyMap.KEYCODE_DPAD_RIGHT: {

			if (isNotSupport || mLogicManager.isAudioFast()) {
				return true;
			}
			// add by xiaojie fix cr DTV00381177
			// if (mLogicManager.isAudioPause()) {
			// mScoreView.setVisibility(View.INVISIBLE);
			// }
			// end
			// add by xiaojie fix cr DTV00381234
			String fileNotSupport = this.getResources().getString(
					R.string.mmp_file_notsupport);
			if (mTipsDialog != null && mTipsDialog.isShowing()
					&& mTipsDialog.getTitle().equals(fileNotSupport)) {
				mTipsDialog.dismiss();
				// add by xudong fix cr DTV00385993
				retrunFromTipDismis = true;
				// end
				return true;

			}
			
			// end

			// add by xudong fix cr DTV00385993
			retrunFromTipDismis = false;
			// end
			return seek(keyCode, event);
		}
		case KeyMap.KEYCODE_MTKIR_REWIND: {
			if (mLogicManager.isAudioStoped()) {
				return true;
			}

			try {
				mLogicManager.fastRewindAudio();
				setFast(1);
			} catch (NotSupportException e) {
				featureNotWork(getString(R.string.mmp_featue_notsupport));

			}
			return true;
		}
		case KeyMap.KEYCODE_MTKIR_FASTFORWARD: {
			if (mLogicManager.isAudioStoped()) {
				return true;
			}
			try {
				mLogicManager.fastForwardAudio();
				setFast(0);
			} catch (NotSupportException e) {
				featureNotWork(getString(R.string.mmp_featue_notsupport));

			}

			return true;
		}
		case KeyMap.KEYCODE_MTKIR_STOP: {
			/* add by lei 1228 */
			if (isNotSupport) {
				return true;
			}
			// Added by yognzheng for fix CR DTV00390968 16/1/12
			if (mTipsDialog != null
					&& mTipsDialog.isShowing()
					&& mTipsDialog.getTitle().equals(
							getResources().getString(
									R.string.mmp_file_notsupport))) {
				mTipsDialog.dismiss();
			}
			mLogicManager.stopAudio();
			stop();
			return true;
		}
		case KeyMap.KEYCODE_BACK: {
			removeControlView();
			finish();
			break;
		}
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void stop() {

		removeMessages();
		// Add by yongzheng for fix CR DTV00379673
		setPlayerStop(true);
		if (null != mLrcView && null != lrc_map && lrc_map.size() > 0) {
			mLrcView.noLrc(null);
		}
		// end
		if (null != mControlView) {
			mControlView.setCurrentTime(0);
			mControlView.setProgress(0);
			mControlView.stop();
		}
		if (null != mScoreView) {
			mScoreView.setVisibility(View.INVISIBLE);
		}
		
	}
	public void finish() {
		
		setResult(100, null);
		super.finish();
	}

	private void removeMessages() {
		myHandler.removeMessages(PROGRESS_START);
		myHandler.removeMessages(PROGRESS_CHANGED);
		myHandler.removeMessages(PROGRESS_SCOREVIEW);
		myHandler.removeMessages(AUDIO_CHANGED);
		myHandler.removeMessages(SPEED_UPDATE);
		myHandler.removeMessages(NOSUPPORT_PLAYNEXT);
	}

	private void setFast(int isForward) {

		int speed = mLogicManager.getAudioSpeed();
		if (speed == 0) {
			return;
		}

		if (null == mControlView) {
			return;
		}
		hideFeatureNotWork();

		if (!myHandler.hasMessages(PROGRESS_CHANGED)) {
			myHandler.sendEmptyMessage(PROGRESS_CHANGED);
		}
		if (!myHandler.hasMessages(PROGRESS_SCOREVIEW)) {
			myHandler.sendEmptyMessage(PROGRESS_SCOREVIEW);

		}
		if (!myHandler.hasMessages(PROGRESS_CHANGED)) {
			myHandler.sendEmptyMessage(PROGRESS_CHANGED);
		}
		mControlView.onFast(speed, isForward, Const.FILTER_AUDIO);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (keyCode == KeyMap.KEYCODE_DPAD_LEFT
				|| keyCode == KeyMap.KEYCODE_DPAD_RIGHT) {

			if (mLogicManager.isAudioFast()) {
				return true;
			}
			//add by keke 2.1 for DTV00393701
			if(mLogicManager.getPlayStatus()==AudioConst.PLAY_STATUS_PAUSED){
				removeScorePause();
			}
			// add "&& !retrunFromTipDismis" by xudong fix cr DTV00385993
			if (playFlag && !retrunFromTipDismis) {
				mControlView.setMediaPlayState();
			}
		}

		return super.onKeyUp(keyCode, event);
	}

	private boolean seek(int keyCode, KeyEvent event) {
		if (null == mControlView) {
			return true;
		}
		if(!mLogicManager.canSeek()){
			featureNotWork(getString(R.string.mmp_file_notsupport));
			return true;
		}
		if (mControlView.isPalying()) {
			playFlag = true;
			mControlView.setMediaPlayState();
			if(mControlView.isPalying()){
				playFlag=false;
				return true;
			}
		} else if (event.getRepeatCount() == 0) {
			playFlag = false;
		}

		long progress = mLogicManager.getPlaybackProgress();

		if (progress < 0) {
			return true;
		}

		if (keyCode == KeyMap.KEYCODE_DPAD_LEFT) {
			progress = progress - SEEK_DURATION;
			if (progress < 0) {
				progress = 0;
			}

		} else {
			progress = progress + SEEK_DURATION;

			long totalProgress = mLogicManager.getTotalPlaybackTime();
			if (progress > totalProgress) {
				progress = totalProgress;
			}
		}
		try {
			mLogicManager.seekToCertainTime(progress);
		} catch (Exception e) {
			featureNotWork(getString(R.string.mmp_file_notsupport));
			return true;
		}
		
		mControlView.setCurrentTime(progress);
		mControlView.setProgress((int) progress);
		// Added by yongzheng for fix CR DTV00379673
		if (getPlayerStop()) {
			removeMessages();
			return true;
		}
		// end
		// modified by keke for fix DTV00381199
		if (hasLrc()) {
			myHandler.sendEmptyMessage(PROGRESS_START);
		}
		return true;

	}

	public boolean hasLrc() {
		if (null == lrc_map || (lrc_map.size() == 0) || null == mLrcView) {
			return false;
		}

		return true;
	}
	protected void onResume() {
		super.onResume();
		
		if (!mIsClose3D) mSurfaceView.onResume();

	}
	/**
	 * {@inheritDoc}
	 */
	protected void onPause() {
		super.onPause();
		if (!mIsClose3D) mSurfaceView.onPause();
		removeMessages();

	}

	/**
	 * {@inheritDoc}
	 */
	protected void onDestroy() {
		isActivityLiving = false;
		if (mLogicManager.getAudioPlaybackService() != null) {
			mLogicManager.unbindService(this);
		}

		super.onDestroy();
	}

	public void clearLrc() {
		if (mLrcView != null && mLrcView.getVisibility() == View.VISIBLE
				&& null != lrc_map && lrc_map.size() > 0) {
			mLrcView.noLrc("");
		}
	}

	/**
	 * Audio wheather stop
	 */
	// Added by yongzheng for fix CR DTV00379673 and DTV00388521
	private boolean isMusicStop = false;

	protected boolean getPlayerStop() {
		return isMusicStop;
	}

	// end

	protected void setPlayerStop(boolean isStop) {
		isMusicStop = isStop;
	}

	private boolean isFileNotSupport = false;

	protected boolean isFileNotSupport() {
		return isFileNotSupport;
	}

	protected void setIsFileNotSupport(boolean fileNotSupport) {
		isFileNotSupport = fileNotSupport;
	}
}
