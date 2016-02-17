package com.mediatek.ui.mmp.commonview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mediatek.media.MtkMediaPlayer;
import com.mediatek.media.MtkMediaPlayer.OnCompletionListener;
import com.mediatek.media.MtkMediaPlayer.OnErrorListener;
import com.mediatek.media.MtkMediaPlayer.OnPreparedListener;
import com.mediatek.media.MtkMediaPlayer.OnRePlayListener;
import com.mediatek.media.MtkMediaPlayer.OnSpeedUpdateListener;
import com.mediatek.mmpcm.audioimpl.AudioConst;
import com.mediatek.mmpcm.audioimpl.PlaybackService;
import com.mediatek.mmpcm.mmcimpl.Const;
import com.mediatek.ui.R;
import com.mediatek.ui.mmp.util.GetDataImp;
import com.mediatek.ui.mmp.util.LogicManager;
import com.mediatek.ui.util.MtkLog;

public class MusicPlayInfoView extends LinearLayout {

	private static final String TAG = "MusicPlayInfoView";

	private static final int PROGRESS_CHANGED = 0;

	private static final int PROGRESS_SCOREVIEW = 1;

	private static final int AUDIO_CHANGED = 2;

	private static final int SPEED_UPDATE = 3;
	
	private static final int NOSUPPORT_PLAYNEXT = 4;

	private static final int DELAY_TIME = 1000;

	private static final int DELAY_SCO = 400;

	private TextView vMusicName;

	private ProgressBar vProgressBar;

	private TextView vStartTime;

	private TextView vEndtime;

	private ImageView vPlay;

	private ImageView vRepeat;

	private ImageView vShuffle;

	private ImageView vUnkowm;

	private ImageView vVolume;

	private ProgressBar vVolumeBar;

	private ScoreView mScoreView;

	private View mContentView;

	private LinearLayout mVolumeProgressBg;

	private LogicManager mLogicManager;

	private int maxVolume = 0;

	private int currentVolume = 0;

	private int mType = 1;

	private View vMusicView;

	private boolean isMute = false;

	private OnCompletionListener mCompletionListener;

	public MusicPlayInfoView(Context context, View contentView, int type,
			OnCompletionListener listener) {
		super(context);
		mContentView = contentView;
		MtkLog.d(TAG, "MusicPlayInfoView");
		mType = type;
		if (mType == 0) {
			findScoreView();
		}
		mCompletionListener = listener;
		findView();
	}

	// public MusicPlayInfoView(Context context, AttributeSet attrs) {
	// super(context, attrs);
	// if (mType != 0) {
	// mContentView = LayoutInflater.from(context).inflate(
	// R.layout.mmp_musicbackcom, null);
	// mContentView.setLayoutParams(new LayoutParams(
	// LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	// this.addView(mContentView);
	// findView();
	// }
	// }
	//
	// public MusicPlayInfoView(Context context) {
	//
	// this(context, null);
	// }

	private PlaybackService mAudioPlayback = null;
	
	//private boolean mBInited = false;
	public void init(Context context) {
      /*if (mBInited){
    	  initView();
        	return;
        }
        mBInited = true;*/
        registerListener(context);
        initView();
		
	}
	
	private void registerListener(Context context){
		mLogicManager = LogicManager.getInstance(context);
		mLogicManager.setPreparedListener(mPreparedListener);
		if(null != mLogicManager.getAudioPlaybackService()){
			mLogicManager.getAudioPlaybackService().registerAudioPreparedListener(
							mPreparedListener);
		}
		
		mLogicManager.setSpeedUpdateListener(mSpeedUpdateListener);
		mLogicManager.getAudioPlaybackService()
				.registerAuidoSpeedUpdateListener(mSpeedUpdateListener);
		
		mLogicManager.setReplayListener(mReplayListener);
		mLogicManager.getAudioPlaybackService().registerAudioReplayListener(mReplayListener);
		
		//add by xudong 
		mLogicManager.setErrorListener(mErrorListener);
		mLogicManager.getAudioPlaybackService()
				.registerAudioErrorListener(mErrorListener);
		
		
		mLogicManager.setCompletionListener(mCompletionListener);
		mAudioPlayback = mLogicManager.getAudioPlaybackService();
		mAudioPlayback.registerAudioCompletionListener(mCompletionListener);
	}
	
	private void initView(){
		maxVolume = mLogicManager.getMaxVolume();
		currentVolume = mLogicManager.getVolume();
		vVolumeBar.setMax(maxVolume);
		int playStatus = mAudioPlayback.getPlayStatus();
		if (playStatus == AudioConst.PLAY_STATUS_PAUSED) {
			vPlay.setImageResource(R.drawable.mmp_thumbnail_player_icon_pause);
		}else if(playStatus == AudioConst.PLAY_STATUS_FF){
			vPlay.setImageResource(R.drawable.common_key_ff);
		}else if(playStatus == AudioConst.PLAY_STATUS_FR){
			vPlay.setImageResource(R.drawable.common_key_fr);
		}else if(playStatus == AudioConst.PLAY_STATUS_STOPPED){
			vPlay.setImageResource(R.drawable.mmp_thumbnail_player_icon_stop);
		}else{
			vPlay.setImageResource(R.drawable.mmp_thumbnail_player_icon_play);
		}
		/*if (playStatus == AudioConst.PLAY_STATUS_FF 
				|| playStatus == AudioConst.PLAY_STATUS_FR
				|| playStatus == AudioConst.PLAY_STATUS_SF
				|| playStatus == AudioConst.PLAY_STATUS_SR){
			mAudioPlayback.play();
		}*/
		onMute();

		int repeatModel = mLogicManager.getRepeatModel(Const.FILTER_AUDIO);
		setRepeate(repeatModel);

		boolean isShuffle = mLogicManager.getShuffleMode(Const.FILTER_AUDIO);
		if (isShuffle) {
			vShuffle.setVisibility(View.VISIBLE);
		} else {
			vShuffle.setVisibility(View.INVISIBLE);
		}

		sendMessage();
	}

	private void setRepeate(int repeatModel) {
		switch (repeatModel) {
		case Const.REPEAT_NONE: {
			vRepeat.setVisibility(View.INVISIBLE);
			break;
		}
		case Const.REPEAT_ONE: {
			vRepeat.setVisibility(View.VISIBLE);
			vRepeat
					.setImageResource(R.drawable.mmp_thumbnail_player_icon_repeatone);
			break;
		}
		case Const.REPEAT_ALL: {
			vRepeat.setVisibility(View.VISIBLE);
			vRepeat
					.setImageResource(R.drawable.mmp_thumbnail_player_icon_repeall);
			break;
		}
		case Const.REPEAT_MARKED: {
			vShuffle.setVisibility(View.INVISIBLE);
			break;
		}
		default:
			break;
		}
	}

	public void onRepeatClick() {
		int repeatModel = mLogicManager.getRepeatModel(Const.FILTER_AUDIO);
		if (repeatModel < Const.REPEAT_NONE) {
			repeatModel += 1;
		} else {
			repeatModel = Const.REPEAT_ALL;
		}

		setRepeate(repeatModel);
		mLogicManager.setRepeatMode(Const.FILTER_AUDIO, repeatModel);

	}

	public void onStop() {
		//

		removeMessage();
		mLogicManager.stopAudio();
		vPlay.setImageResource(R.drawable.mmp_thumbnail_player_icon_stop);
		mLogicManager.finishAudioService();

	}

	public void onPauseOrPlay() {

		if (mLogicManager.isAudioPause() || mLogicManager.isAudioFast()) {
			mHandler.sendEmptyMessage(PROGRESS_CHANGED);
			mHandler.sendEmptyMessage(PROGRESS_SCOREVIEW);
			vPlay.setImageResource(R.drawable.mmp_thumbnail_player_icon_play);
			mLogicManager.playAudio();
		} else if (mLogicManager.isAudioPlaying()) {
			removeMessage();
			mLogicManager.pauseAudio();
			vPlay.setImageResource(R.drawable.mmp_thumbnail_player_icon_pause);
		}

	}

	public void onFast() {
		int speed = mLogicManager.getAudioSpeed();
		if (speed == 0) {
			return;
		} else if (speed == 1) {
			vPlay.setImageResource(R.drawable.mmp_thumbnail_player_icon_play);
		} else {
			int status = mLogicManager.getAudioStatus();
			if (status == AudioConst.PLAY_STATUS_FR) {
				vPlay.setImageResource(R.drawable.common_key_fr);
				sendMessageMini();
			} else if (status == AudioConst.PLAY_STATUS_FF) {
				vPlay.setImageResource(R.drawable.common_key_ff);				
				sendMessageMini();
			}
		}
	}

	public void setMute() {
		mLogicManager.setMute();
		onMute();
	}

	public void onMute() {

		if (mLogicManager.isMute()) {
			vVolume.setImageResource(R.drawable.mmp_toolbar_icon_mute);
			if (null != mVolumeProgressBg) {
				mVolumeProgressBg.setVisibility(View.INVISIBLE);
			}
			hidScore(true);
		} else {
			hidScore(false);
			vVolume.setImageResource(R.drawable.mmp_toolbar_icon_volume);
			if (null != mVolumeProgressBg) {
				mVolumeProgressBg.setVisibility(View.VISIBLE);
			}

		}

	}

	private void hidScore(boolean ishide) {
		if (null == mScoreView) {
			return;
		}
		if (ishide) {
			mScoreView.setVisibility(View.INVISIBLE);
			mHandler.removeMessages(PROGRESS_SCOREVIEW);
		} else {
			mScoreView.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessageDelayed(PROGRESS_SCOREVIEW, DELAY_SCO);
		}
	}

	public void setVolumeUp() {
		if (mLogicManager.isMute()) {
			setMute();
			return;
		}
		currentVolume = currentVolume + 1;
		if (currentVolume > maxVolume) {
			currentVolume = maxVolume;
		}
		mLogicManager.setVolume(currentVolume);
		vVolumeBar.setProgress(currentVolume);

	}

	public void setVolumeDown() {
		if (mLogicManager.isMute()) {
			setMute();
			return;
		}
		currentVolume = currentVolume - 1;
		if (currentVolume < 0) {
			currentVolume = 0;
		}
		mLogicManager.setVolume(currentVolume);
		vVolumeBar.setProgress(currentVolume);

	}

	private void reSet() {
		vPlay.setImageResource(R.drawable.mmp_thumbnail_player_icon_play);
	}

	private OnPreparedListener mPreparedListener = new OnPreparedListener() {

		public void onPrepared(MtkMediaPlayer mp) {
			reSet();
			if (mLogicManager.isAudioPlaying()) {
				// TODO Check
				removeMessage();

				mHandler.sendEmptyMessage(AUDIO_CHANGED);
				mHandler.sendEmptyMessage(PROGRESS_CHANGED);
				mHandler.sendEmptyMessage(PROGRESS_SCOREVIEW);
			}

		}
	};

	private OnSpeedUpdateListener mSpeedUpdateListener = new OnSpeedUpdateListener() {

		public void onSpeedUpdate(MtkMediaPlayer arg0, int speed, int arg2) {/*
			MtkLog.i(TAG, "  mSpeedUpdateListener backplay before 1 speed:" + speed);

			MtkLog.i(TAG, "  mSpeedUpdateListener backplay before 2 speed:"
					+ mLogicManager.getPlaybackProgress());
			long progress = mLogicManager.getPlaybackProgress();
			if (speed == 100000 && progress >= 0 && progress <= 10) {
				mHandler.sendEmptyMessage(SPEED_UPDATE);
			}
		*/}
	};

	private OnErrorListener mErrorListener = new OnErrorListener() {

		private Runnable mErrorRunnable;

		// @Override if add "override" P4 will build failure
		public boolean onError(MtkMediaPlayer arg0, int arg1, int arg2) {
			MtkLog.i(TAG, "OnErrorListener  targ1:" + arg1 + "  arg2" + arg2
					+ " " + System.currentTimeMillis());
			if (mErrorRunnable != null) {
				mHandler.removeCallbacks(mErrorRunnable);
			}
			//add by xudong for fix cr 384293
			mLogicManager.stopAudioError();
			//end
			mErrorRunnable = new Runnable() {
				// @Override
				public void run() {	
					MtkLog.i(TAG, "OnErrorListener  run was execued" );
					mHandler.sendEmptyMessageDelayed(NOSUPPORT_PLAYNEXT, 3000);
					initControl();
					mErrorRunnable = null;

				}
			};			
			mHandler.postDelayed(mErrorRunnable, 100);

			return false;
		}

	};

	private OnRePlayListener mReplayListener = new OnRePlayListener(){

		public void onRePlay(MtkMediaPlayer arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(SPEED_UPDATE);
		}
		
	};
	public String settime(int mills) {
		mills /= 1000;
		int minute = mills / 60;
		int hour = minute / 60;
		int second = mills % 60;
		minute %= 60;
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	private void findView() {
		vMusicName = (TextView) mContentView.findViewById(R.id.mmp_musicname);
		vProgressBar = (ProgressBar) mContentView
				.findViewById(R.id.mmp_musicback_progress);
		vStartTime = (TextView) mContentView
				.findViewById(R.id.mmp_musicback_starttime);
		vEndtime = (TextView) mContentView
				.findViewById(R.id.mmp_musicback_endtime);
		vPlay = (ImageView) mContentView.findViewById(R.id.mmp_musicback_play);
		vRepeat = (ImageView) mContentView
				.findViewById(R.id.mmp_musicback_repeat);
		vShuffle = (ImageView) mContentView
				.findViewById(R.id.mmp_musicback_shuffle);
		vUnkowm = (ImageView) mContentView
				.findViewById(R.id.mmp_musicback_unkown);
		vVolume = (ImageView) mContentView
				.findViewById(R.id.mmp_musicback_volume_img);
		vVolumeBar = (ProgressBar) mContentView
				.findViewById(R.id.mmp_musicback_volume);

		mVolumeProgressBg = (LinearLayout) mContentView
				.findViewById(R.id.mmp_volume_progress_bg);
	}

	private void findScoreView() {
		mScoreView = (ScoreView) mContentView.findViewById(R.id.mmp_musicscore);
	}

	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case PROGRESS_CHANGED: {
				int progress = (int) mLogicManager.getPlaybackProgress();
				if (progress >= 0) {
					if (mLogicManager.getAudioPlaybackService() != null) {
						vStartTime.setText(settime(progress));
						vProgressBar.setProgress(progress);						
					}
				}
				sendEmptyMessageDelayed(PROGRESS_CHANGED, DELAY_TIME);
				break;
			}
			case PROGRESS_SCOREVIEW:
				if (null != mScoreView) {
					if (hasMessages(PROGRESS_SCOREVIEW)) {
						removeMessages(PROGRESS_SCOREVIEW);
					}
					mScoreView.update(mLogicManager.getAudSpectrum());
					mScoreView.invalidate();
					sendEmptyMessageDelayed(PROGRESS_SCOREVIEW, DELAY_SCO);
				}
				break;
			case SPEED_UPDATE:{	
				if (mLogicManager.getAudioPlaybackService() != null){
					mLogicManager.setAuidoSpeed(1);	
					vPlay
							.setImageResource(R.drawable.mmp_thumbnail_player_icon_play);
					//mLogicManager.playAudio();
				}
				break;		
			}			
			case NOSUPPORT_PLAYNEXT: {
				MtkLog.i(TAG, "OnErrorListener  play nexted was execued" );
				mLogicManager.playNextAudio();
				initControl();

				break;
			}

			case AUDIO_CHANGED: {
				initControl();
				break;
			}
			default:
				break;
			}

		}

	};
	
	//update  progress of the mini music Windows
	public void sendMessageMini()
	{
		mHandler.sendEmptyMessage(PROGRESS_CHANGED);
		if (mType == 0) {
			mHandler.sendEmptyMessage(PROGRESS_SCOREVIEW);
		}
	}
	public void sendMessage() {
		initControl();
		// if (mLogicManager.isPlaying()) {
		mHandler.sendEmptyMessage(PROGRESS_CHANGED);
		if (mType == 0) {
			mHandler.sendEmptyMessage(PROGRESS_SCOREVIEW);
		}
		// }
	}

	public void removeMessage() {
		mHandler.removeMessages(PROGRESS_SCOREVIEW);
		mHandler.removeMessages(PROGRESS_CHANGED);
		mHandler.removeMessages(SPEED_UPDATE);
	}

	public void initControl() {
		if(null==mLogicManager){
			mLogicManager = LogicManager.getInstance(mContext);
		}
		vEndtime.setText(settime((int) mLogicManager.getTotalPlaybackTime()));
		vVolumeBar.setMax(maxVolume);
		vVolumeBar.setProgress(currentVolume);
		vProgressBar.setMax((int) mLogicManager.getTotalPlaybackTime());
		vMusicName
				.setText(mLogicManager.getCurrentFileName(Const.FILTER_AUDIO));
	}

}
