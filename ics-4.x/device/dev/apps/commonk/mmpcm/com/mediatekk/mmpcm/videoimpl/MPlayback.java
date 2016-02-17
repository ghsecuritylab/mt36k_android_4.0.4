package com.mediatekk.mmpcm.videoimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbException;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;


import com.mediatek.media.AudioTrackInfo;
import com.mediatek.media.MediaInfo;
import com.mediatek.media.MetaDataInfo;
import com.mediatek.media.MtkMediaPlayer;
import com.mediatek.media.MtkMediaPlayer.OnAudioOnlyListener;
import com.mediatek.media.SubtitleAttr;
import com.mediatek.media.VideoInfo;
import com.mediatek.media.MtkMediaPlayer.DataSource;
import com.mediatek.media.MtkMediaPlayer.OnBufferingUpdateListener;
import com.mediatek.media.MtkMediaPlayer.OnCompletionListener;
import com.mediatek.media.MtkMediaPlayer.OnEofListener;
import com.mediatek.media.MtkMediaPlayer.OnErrorListener;
import com.mediatek.media.MtkMediaPlayer.OnPlayDoneListener;
import com.mediatek.media.MtkMediaPlayer.OnPositionUpdateListener;
import com.mediatek.media.MtkMediaPlayer.OnPreparedListener;
import com.mediatek.media.MtkMediaPlayer.OnRePlayListener;
import com.mediatek.media.MtkMediaPlayer.OnSeekCompleteListener;
import com.mediatek.media.MtkMediaPlayer.OnSpeedUpdateListener;
import com.mediatek.media.MtkMediaPlayer.OnStepDoneListener;
import com.mediatek.media.MtkMediaPlayer.OnTotalTimeUpdateListener;
import com.mediatek.media.SubtitleAttr.SubtitleFontInfo;
import com.mediatek.media.MtkMediaPlayer.PlayerSpeed;
import com.mediatek.media.TsInfo.PmtInfo;
import com.mediatek.media.TsInfo.StreamInfo;
import com.mediatek.media.NotSupportException;
import com.mediatek.media.TsInfo;
import com.mediatek.media.VideoTrackInfo;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.service.InputService;
import com.mediatekk.mmpcm.MmpTool;
import com.mediatekk.mmpcm.fileimpl.VideoFile;
import com.mediatekk.mmpcm.mmcimpl.Const;
import com.mediatekk.mmpcm.mmcimpl.PlayList;
import com.mediatekk.mmpcm.video.IPlayback;
import com.mediatekk.netcm.dlna.DLNADataSource;
import com.mediatekk.netcm.dlna.DLNAManager;
import com.mediatekk.netcm.samba.SambaDataSource;
import com.mediatekk.netcm.samba.SambaManager;

public class MPlayback implements IPlayback, DataSource {
	private static MtkMediaPlayer mmediaplayer = null;
	private static MPlayback pb = null;

	private static int speedStep;
	// add by shuming for fix bug DTV00385698
	private boolean mFeaturenotsurport = true;
	// end

	private String mcurPath = null;
	private int mPlayStatus = VideoConst.PLAY_STATUS_UNKNOW;
	private static PlayList videopl = null;
	private static boolean isEnd = false;
	private static int mplayerMode = VideoConst.PLAYER_MODE_MMP;
	// private static Rect t_rect = new Rect(0, 0, 1000, 1000);
	private InputService inputManager;
	private boolean previewMode = false;
	private short tsProgramIndex = 0;
	private InputStream mInputStream;
	MtkMediaPlayer.PlayerSpeed eSpeed = PlayerSpeed.SPEED_1X;

	/**
	 * set playback message listener
	 */
	public void setOnPBMsgListener(OnPBMsgListener listener) {
		mOnPBMsgListener = listener;
	}

	private OnPBMsgListener mOnPBMsgListener;

	/**
	 * set playback complete listener
	 */
	public void setOnPBCompleteListener(OnPBCompleteListener listener) {
		mOnPBCompleteListener = listener;
	}

	private OnPBCompleteListener mOnPBCompleteListener;

	public void setOnPBPreparedListener(OnPBPreparedListener listener) {
		mOnPBPreparedListener = listener;
	}

	private OnPBPreparedListener mOnPBPreparedListener;

	public void setOnPBBufferUpdateListener(OnPBBufferUpdateListener listener) {
		mOnPBBufferUpdateListener = listener;
	}

	private OnPBBufferUpdateListener mOnPBBufferUpdateListener;

	public void setOnPBPlayDoneListener(OnPBPlayDoneListener listener) {
		mOnPBPlayDoneListener = listener;
	}

	private OnPBPlayDoneListener mOnPBPlayDoneListener;

	public void setOnPBDurarionUpdateListener(
			OnPBDurarionUpdateListener listener) {
		mOnPBDurarionUpdateListener = listener;
	}

	private OnPBDurarionUpdateListener mOnPBDurarionUpdateListener;

	public void setOnPBEofListener(OnPBEofListener listener) {
		mOnPBEofListener = listener;
	}

	private OnPBEofListener mOnPBEofListener;

	/**
	 * gain new inputStream
	 */
	public InputStream newInputStream() {
		MmpTool.LOG_INFO(mcurPath);
		closeStream();
		if (isEnd) {
			return null;
		}
		if (mcurPath == null) {
			sendMsg(VideoConst.MSG_SOURCE_NOT_PREPARED);
			return null;
		} else {
			if (mplayerMode == VideoConst.PLAYER_MODE_MMP) {
				try {
					mInputStream = new FileInputStream(mcurPath);
				} catch (FileNotFoundException e) {
					sendMsg(VideoConst.MSG_INPUT_STREAM_FAIL);
					e.printStackTrace();
				}

				if (mInputStream == null) {
					sendMsg(VideoConst.MSG_INPUT_STREAM_FAIL);
				}
			} else if (mplayerMode == VideoConst.PLAYER_MODE_SAMBA) {
				try {
					mInputStream = SambaManager.getInstance()
							.getSambaDataSource(mcurPath).newInputStream();
				} catch (SmbException e) {
					sendMsg(VideoConst.MSG_INPUT_STREAM_FAIL);
					e.printStackTrace();
				} catch (MalformedURLException e) {
					sendMsg(VideoConst.MSG_INPUT_STREAM_FAIL);
					e.printStackTrace();
				} catch (UnknownHostException e) {
					sendMsg(VideoConst.MSG_INPUT_STREAM_FAIL);
					e.printStackTrace();
				}

				if (mInputStream == null) {
					sendMsg(VideoConst.MSG_INPUT_STREAM_FAIL);
				}
			} else if (mplayerMode == VideoConst.PLAYER_MODE_DLNA) {
				DLNADataSource source = DLNAManager.getInstance()
						.getDLNADataSource(mcurPath);
				if (source == null) {
					mInputStream = null;

				} else {
					mInputStream = source.newInputStream();
				}

				if (mInputStream == null) {
					sendMsg(VideoConst.MSG_INPUT_STREAM_FAIL);
				}
			}

			return mInputStream;
		}
	}

	// Mplayback
	public MPlayback(int playerMode) {
		if (playerMode != VideoConst.PLAYER_MODE_HTTP) {
			videopl = PlayList.getPlayList();
		}

		mplayerMode = playerMode;

		mmediaplayer = new MtkMediaPlayer();

		mmediaplayer.setOnErrorListener(errListener);
		mmediaplayer.setOnAudioOnlyListener(audioOnlyListener);
		mmediaplayer.setOnPreparedListener(preListener);
		mmediaplayer.setOnBufferingUpdateListener(bufferListener);
		mmediaplayer.setOnSeekCompleteListener(seekListener);
		mmediaplayer.setOnCompletionListener(completeListener);
		mmediaplayer.setOnPlayDoneListener(playDoneListener);
		mmediaplayer.setOnSpeedUpdateListener(speedUpdateListener);
		mmediaplayer.setOnEofListener(eofListener);
		mmediaplayer.setOnTotalTimeUpdateListener(durationListener);
		mmediaplayer.setOnPositionUpdateListener(posUpdateListener);
		mmediaplayer.setOnStepDoneListener(stepDoneListener);
		mmediaplayer.setOnRePlayListener(replayListener);

		mPlayStatus = VideoConst.PLAY_STATUS_UNKNOW;

		TVManager tvManager = TVManager.getInstance(null);
		inputManager = (InputService) tvManager
				.getService(InputService.InputServiceName);

		pb = this;
	}

	private void cleanListener() {
		if (mmediaplayer != null) {
			mmediaplayer.setOnErrorListener(null);
			mmediaplayer.setOnAudioOnlyListener(null);
			mmediaplayer.setOnPreparedListener(null);
			mmediaplayer.setOnBufferingUpdateListener(null);
			mmediaplayer.setOnSeekCompleteListener(null);
			mmediaplayer.setOnCompletionListener(null);
			mmediaplayer.setOnPlayDoneListener(null);
			mmediaplayer.setOnEofListener(null);
			mmediaplayer.setOnTotalTimeUpdateListener(null);
			mmediaplayer.setOnPositionUpdateListener(null);
		}
	}

	private void resetListener() {
		if (mmediaplayer != null) {
			mmediaplayer.setOnErrorListener(errListener);
			mmediaplayer.setOnAudioOnlyListener(audioOnlyListener);
			mmediaplayer.setOnPreparedListener(preListener);
			mmediaplayer.setOnBufferingUpdateListener(bufferListener);
			mmediaplayer.setOnSeekCompleteListener(seekListener);
			mmediaplayer.setOnCompletionListener(completeListener);
			mmediaplayer.setOnPlayDoneListener(playDoneListener);
			mmediaplayer.setOnEofListener(eofListener);
			mmediaplayer.setOnTotalTimeUpdateListener(durationListener);
			mmediaplayer.setOnPositionUpdateListener(posUpdateListener);
		}
	}

	/**
	 * set preview mode
	 */
	public void setPreviewMode(boolean preview) {
		previewMode = preview;
	}

	/**
	 * get current file name
	 */
	public String getCurFileName() {
		return mcurPath;
	}

	/**
	 * set player mode
	 * 
	 * @param mode
	 */
	public void setPlayerMode(int mode) {
		mplayerMode = mode;
	}

	/**
	 * gain player mode
	 */
	public int getPlayerMode() {
		return mplayerMode;
	}

	private void sendMsg(int msg) {
		Message m = new Message();
		m.what = msg;
		videoMsgHandler.sendMessage(m);
	}

	/**
	 * set date source
	 * 
	 * @param path
	 */
	public void setDataSource(String path) throws IllegalStateException {

		if (mPlayStatus == VideoConst.PLAY_STATUS_STOPPED) {
			mPlayStatus = VideoConst.PLAY_STATUS_END;
		}
		if (mPlayStatus != VideoConst.PLAY_STATUS_END) {

			MmpTool.LOG_ERROR("Video is playing,can't set setDataSource");

			sendMsg(VideoConst.MSG_IS_PLAYING);
		}

		mmediaplayer.reset();
		resetListener();

		isEnd = false;

		mcurPath = path;

		try {

			mmediaplayer.setDataSource(this);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			sendMsg(VideoConst.MSG_INVALID_STATE);
			throw new IllegalStateException(e);
		}
		mPlayStatus = VideoConst.PLAY_STATUS_INITED;

		if (mplayerMode == VideoConst.PLAYER_MODE_DLNA) {

			DLNADataSource dlnaSource = DLNAManager.getInstance()
					.getDLNADataSource(mcurPath);

			if (dlnaSource != null) {
				mmediaplayer.setDataSourceSeekEnable(dlnaSource.getContent()
						.canSeek());
			}

		}

		MmpTool.LOG_DBG("setDataSource finish!");
		setFileSize(getVideoFileSize());
	}

	private VideoFile mVideoFile;

	/**
	 * get file size
	 * 
	 * @return
	 */
	public long getVideoFileSize() {
		long fileSize = 0;
		switch (mplayerMode) {
		case VideoConst.PLAYER_MODE_DLNA: {
			DLNADataSource dlnaSource = DLNAManager.getInstance()
					.getDLNADataSource(mcurPath);
			if (dlnaSource != null) {
				fileSize = dlnaSource.getContent().getSize();
				MmpTool.LOG_INFO("getVideoFileSize dlna $$$$$$$$$$$$$$"
						+ fileSize);
			}
		}
			break;
		case VideoConst.PLAYER_MODE_SAMBA: {
			SambaManager sambaManager = SambaManager.getInstance();
			try {
				fileSize = sambaManager.size(mcurPath);
				MmpTool.LOG_INFO("getVideoFileSize samba $$$$$$$$$$$$$$"
						+ fileSize);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (SmbException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
			break;
		case VideoConst.PLAYER_MODE_MMP: {

			// videopl = PlayList.getPlayList();
			// String path = videopl.getCurrentPath(Const.FILTER_VIDEO);
			if (mcurPath != null) {
				mVideoFile = new VideoFile(mcurPath);
			}
			MmpTool.LOG_INFO("getVideoFileSize = $$$$$$$$$$$$$$" + mcurPath);

			if (mVideoFile == null) {
				fileSize = 0;
				break;
			}
			fileSize = mVideoFile.getFileSize();
			MmpTool
					.LOG_INFO("getVideoFileSize local $$$$$$$$$$$$$$"
							+ fileSize);
		}
			break;
		case VideoConst.PLAYER_MODE_HTTP:
			break;
		default:
			break;
		}

		return fileSize;
	}

	/*
	 * .ssif not support fast forward.
	 */
	private boolean isFastForward() {
		boolean bFast = true;
		switch (mplayerMode) {
		case VideoConst.PLAYER_MODE_DLNA:
		case VideoConst.PLAYER_MODE_SAMBA:
		case VideoConst.PLAYER_MODE_MMP: {

			// videopl = PlayList.getPlayList();
			// String path = videopl.getCurrentPath(Const.FILTER_VIDEO);
			if (mcurPath != null) {
				mVideoFile = new VideoFile(mcurPath);
				bFast = !(mVideoFile.isIsoVideoFile());
			}
		}
			break;
		default:
			break;
		}
		return bFast;
	}

	/*
	 * .ssif not support fast forwind.
	 */
	private boolean isFastRewind() {
		boolean bFast = true;
		switch (mplayerMode) {
		case VideoConst.PLAYER_MODE_DLNA:
		case VideoConst.PLAYER_MODE_SAMBA:
		case VideoConst.PLAYER_MODE_MMP: {

			// videopl = PlayList.getPlayList();
			// String path = videopl.getCurrentPath(Const.FILTER_VIDEO);
			if (mcurPath != null) {
				mVideoFile = new VideoFile(mcurPath);
				bFast = !(mVideoFile.isIsoVideoFile());
			}
		}
			break;
		default:
			break;
		}
		return bFast;
	}

	private void setDataSource() throws IllegalStateException {

		if (mPlayStatus == VideoConst.PLAY_STATUS_STOPPED) {
			mPlayStatus = VideoConst.PLAY_STATUS_END;
		}
		if (mPlayStatus != VideoConst.PLAY_STATUS_END) {

			MmpTool.LOG_ERROR("Video is playing,can't set setDataSource");

			sendMsg(VideoConst.MSG_IS_PLAYING);
		}
		tsProgramIndex = 0;
		mmediaplayer.reset();
		resetListener();

		isEnd = false;

		try {
			mmediaplayer.setDataSource(this);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			sendMsg(VideoConst.MSG_INVALID_STATE);
			throw new IllegalStateException(e);
		}
		mPlayStatus = VideoConst.PLAY_STATUS_INITED;

		if (mplayerMode == VideoConst.PLAYER_MODE_DLNA) {

			DLNADataSource dlnaSource = DLNAManager.getInstance()
					.getDLNADataSource(mcurPath);

			if (dlnaSource != null) {
				mmediaplayer.setDataSourceSeekEnable(dlnaSource.getContent()
						.canSeek());
			}

		}
		setFileSize(getVideoFileSize());

		MmpTool.LOG_DBG("setDataSource finish!");
	}

	/**
	 * set file size
	 */
	public void setFileSize(long size) {
		if (mmediaplayer != null) {
			mmediaplayer.setMediaSize(size);
		}
	}

	/**
	 * play
	 */
	public void play() throws IllegalStateException {

		if (mmediaplayer != null) {
			speedStep = 1;
			eSpeed = PlayerSpeed.SPEED_1X;
			MmpTool.LOG_DBG("Play Action!");

			if (mPlayStatus == VideoConst.PLAY_STATUS_STEP) {
				try {
					mmediaplayer.pause();
				} catch (IllegalStateException e) {
					e.printStackTrace();
					sendMsg(VideoConst.MSG_INVALID_STATE);
					throw new IllegalStateException(e);
				}

				try {
					mmediaplayer.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
					sendMsg(VideoConst.MSG_INVALID_STATE);
					throw new IllegalStateException(e);
				}

			} else if (mPlayStatus == VideoConst.PLAY_STATUS_PAUSED
					|| mPlayStatus == VideoConst.PLAY_STATUS_FF
					|| mPlayStatus == VideoConst.PLAY_STATUS_FR
					|| mPlayStatus == VideoConst.PLAY_STATUS_SF) {
				try {

					mmediaplayer.start();

				} catch (IllegalStateException e) {
					e.printStackTrace();
					sendMsg(VideoConst.MSG_INVALID_STATE);
					throw new IllegalStateException(e);
				}

				MmpTool.LOG_DBG("Pause to Play!");
				mPlayStatus = VideoConst.PLAY_STATUS_PLAYED;

			} else if (mPlayStatus == VideoConst.PLAY_STATUS_STARTED
					|| mPlayStatus == VideoConst.PLAY_STATUS_PREPARED
					|| mPlayStatus == VideoConst.PLAY_STATUS_PLAYED) {
				MmpTool.LOG_DBG("Has played or prepared!");
				return;
			} else if (mPlayStatus == VideoConst.PLAY_STATUS_INITED) {
				try {
					String exSubPath = findExternalSubtitle();

					if (exSubPath != null) {
						mmediaplayer.setURL(
								MtkMediaPlayer.URL_TYPE_EXT_SBTL_FULL_PATH,
								exSubPath);
					} else {
						mmediaplayer.setURL(MtkMediaPlayer.URL_TYPE_UNKNOWN,
								null);
					}

					mPlayStatus = VideoConst.PLAY_STATUS_PREPAREING;
					sendMsg(VideoConst.MSG_SOURCE_PREPAREING);
					MmpTool.LOG_DBG("Prepare start!");

					if (mcurPath.toLowerCase().endsWith(".ssif")) {
						mmediaplayer
								.prepareAsync(MtkMediaPlayer.PROFILE_AUTO_DETECT_NEXT_VID);
					} else {
						if (mplayerMode == VideoConst.PLAYER_MODE_MMP) {
							mmediaplayer
									.prepareAsync(MtkMediaPlayer.PROFILE_USB);
						} else {
							mmediaplayer.prepareAsync();
						}
						// mmediaplayer.prepareAsync();
					}

				} catch (IOException e) {
					e.printStackTrace();
					mmediaplayer.getMetaDataStop();
					mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
					sendMsg(VideoConst.MSG_FILE_NOT_SUPPORT);
				} catch (IllegalStateException e) {
					e.printStackTrace();
					mmediaplayer.getMetaDataStop();
					mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
					sendMsg(VideoConst.MSG_INVALID_STATE);
					throw new IllegalStateException(e);
				} catch (NotSupportException e) {
					mmediaplayer.getMetaDataStop();
					mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
					sendMsg(VideoConst.MSG_FILE_CORRUPT);
				}

			} else {
				MmpTool.LOG_ERROR("Please setDataSource firstly!");
				sendMsg(VideoConst.MSG_SOURCE_NOT_PREPARED);
			}
		} else {
			MmpTool.LOG_ERROR("The player is null!");
			sendMsg(VideoConst.MSG_PLAYER_NOT_PREPARED);
		}
	}

	/**
	 * pause
	 */
	public void pause() throws IllegalStateException {
		if (mmediaplayer != null) {
			speedStep = 1;
			eSpeed = PlayerSpeed.SPEED_1X;
			if (mPlayStatus == VideoConst.PLAY_STATUS_PLAYED
					|| mPlayStatus == VideoConst.PLAY_STATUS_STEP
					|| mPlayStatus == VideoConst.PLAY_STATUS_FF
					|| mPlayStatus == VideoConst.PLAY_STATUS_FR
					|| mPlayStatus == VideoConst.PLAY_STATUS_SF) {
				if (mplayerMode == VideoConst.PLAYER_MODE_DLNA) {
					DLNADataSource dlnaDataSource = DLNAManager.getInstance()
							.getDLNADataSource(mcurPath);
					if (dlnaDataSource != null) {
						if (!dlnaDataSource.getContent().canPause()) {
							throw new IllegalStateException(
									VideoConst.MSG_ERR_CANNOTPAUSE);
						}
					}
				}
				try {
					mmediaplayer.pause();
				} catch (IllegalStateException e) {
                    e.printStackTrace();
                    //sendMsg(VideoConst.MSG_INVALID_STATE);
                    /*by lei add*/
                    sendMsg(VideoConst.MSG_NOT_SUPPORT);
                    throw new IllegalStateException(e);
				}
				mPlayStatus = VideoConst.PLAY_STATUS_PAUSED;

				MmpTool.LOG_DBG("pause!");
			} else if (mPlayStatus == VideoConst.PLAY_STATUS_PAUSED) {
				try {
					mmediaplayer.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
					sendMsg(VideoConst.MSG_INVALID_STATE);
					throw new IllegalStateException(e);
				}
				mPlayStatus = VideoConst.PLAY_STATUS_PLAYED;

				MmpTool.LOG_DBG("play!");
			} else {
				sendMsg(VideoConst.MSG_INVALID_STATE);
				throw new IllegalStateException("State Fail!!!");
			}
		}
	}

	/**
	 * stop
	 */
	public void stop() throws IllegalStateException {
		if (mmediaplayer != null) {
			if (mPlayStatus == VideoConst.PLAY_STATUS_INITED) {
				return;
			}

			try {
				isEnd = true;
				cleanListener();
				if (mPlayStatus >= VideoConst.PLAY_STATUS_PREPAREING
						&& mPlayStatus <= VideoConst.PLAY_STATUS_PREPARED) {
					mmediaplayer.getMetaDataStop();
					mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
				} else if (mPlayStatus >= VideoConst.PLAY_STATUS_STARTED
						&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {
					mmediaplayer.stop();
					mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
				sendMsg(VideoConst.MSG_INVALID_STATE);
				throw new IllegalStateException(e);
			} finally {
				closeStream();
			}

			/*mPlayStatus = VideoConst.PLAY_STATUS_END;

			mmediaplayer.reset();*/
			mcurPath = null;

			if (mOnPBCompleteListener != null) {
				mOnPBCompleteListener.onComplete(pb);
			}

			MmpTool.LOG_DBG("stop!");
		}
	}

	/**
	 * release resource
	 */
	public void onRelease() {
		mOnPBPreparedListener = null;
		// change by xudong because videoPlayActivity exit it will call this
		// after set completeListener
		// mOnPBCompleteListener = null;
		mOnPBMsgListener = null;
		mOnPBBufferUpdateListener = null;
		mOnPBPlayDoneListener = null;
		mOnPBDurarionUpdateListener = null;
		mOnPBEofListener = null;
		tsProgramIndex = 0;
		mPlayStatus = VideoConst.PLAY_STATUS_END;
		videoMsgHandler.removeCallbacksAndMessages(null);

		closeStream();
		mmediaplayer.release();
		MmpTool.LOG_DBG("release!");
	}

	private void closeStream() {
		if (null != mInputStream) {
			try {
				mInputStream.close();
				MmpTool.LOG_DBG("video closeStream() success");
			} catch (IOException e) {
				MmpTool.LOG_DBG("video closeStream() fail" + e.toString());
			}
		} else {
			MmpTool.LOG_DBG("video closeStream()  stream is null");
		}
		mInputStream = null;
	}

	/**
	 * reset
	 */
	public void reset() {
		if (mmediaplayer != null) {
			mmediaplayer.reset();
		}
		speedStep = 0;
		tsProgramIndex = 0;
		mcurPath = null;
		isEnd = false;
		mPlayStatus = VideoConst.PLAY_STATUS_UNKNOW;
		MmpTool.LOG_DBG("reset!");
	}

	/**
	 * seek music
	 */
    public void seek(int msec) throws IllegalStateException ,NotSupportException {
        if (mmediaplayer != null
                && (mPlayStatus == VideoConst.PLAY_STATUS_PLAYED
                        || mPlayStatus == VideoConst.PLAY_STATUS_PAUSED
                        || mPlayStatus == VideoConst.PLAY_STATUS_FF
                        || mPlayStatus == VideoConst.PLAY_STATUS_FR 
                        || mPlayStatus == VideoConst.PLAY_STATUS_STEP
                        || mPlayStatus == VideoConst.PLAY_STATUS_SF)) {
            try {
				if (msec < getDuration()) {
					MmpTool.LOG_DBG("start seek!");
					if (mplayerMode == VideoConst.PLAYER_MODE_DLNA) {
						DLNADataSource dlnaDataSource = DLNAManager.getInstance()
								.getDLNADataSource(mcurPath);
						if (dlnaDataSource != null) {
							if (!dlnaDataSource.getContent().canSeek()) {
								throw new NotSupportException(VideoConst.NOT_SUPPORT);
							}
						}
					}
					sendMsg(VideoConst.MSG_SEEKING);
					mmediaplayer.seekTo(msec);
				}
			} catch (NotSupportException e) {
				sendMsg(VideoConst.MSG_SEEK_NOT_SUPPORT);
				e.printStackTrace();
			} catch (IllegalStateException e) {
				sendMsg(VideoConst.MSG_INVALID_STATE);
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
		} else {
			sendMsg(VideoConst.MSG_INVALID_STATE);
		}
	}

	public void stopResume() {

	}

	private void autoNext() {

		if (mmediaplayer != null) {

			if (mplayerMode == VideoConst.PLAYER_MODE_HTTP) {
				MmpTool.LOG_ERROR("This player mode can't do next!");
				throw new IllegalStateException("Can't do Next!!!");
			}

			if (mPlayStatus >= VideoConst.PLAY_STATUS_STARTED
					&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {
				try {
					mmediaplayer.stop();
				} catch (IllegalStateException e) {
					sendMsg(VideoConst.MSG_INVALID_STATE);
					e.printStackTrace();
					throw new IllegalStateException(e);
				}
				mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
			}

			mcurPath = videopl.getNext(Const.FILTER_VIDEO, Const.AUTOPLAY);

			// mmediaplayer.reset();

			mPlayStatus = VideoConst.PLAY_STATUS_END;

			try {
				setDataSource();
			} catch (IllegalStateException e) {
				throw new IllegalStateException(e);
			}

			try {
				play();
			} catch (IllegalStateException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/**
	 * manual next
	 */
	public void manualNext() throws IllegalStateException {

		if (mmediaplayer != null) {
			if (mplayerMode == VideoConst.PLAYER_MODE_HTTP) {
				MmpTool.LOG_ERROR("This player mode can't do next!");
				throw new IllegalStateException("Can't do Next!!!");
			}

			if (mPlayStatus == VideoConst.PLAY_STATUS_INITED) {
				return;
			}

			if (mPlayStatus >= VideoConst.PLAY_STATUS_PREPAREING
					&& mPlayStatus <= VideoConst.PLAY_STATUS_PREPARED) {
				cleanListener();
				try {
					mmediaplayer.getMetaDataStop();
				} catch (IllegalStateException e) {
					sendMsg(VideoConst.MSG_INVALID_STATE);
					e.printStackTrace();
					throw new IllegalStateException(e);
				}
				mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
			} else if (mPlayStatus >= VideoConst.PLAY_STATUS_STARTED
					&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {
				cleanListener();
				try {
					mmediaplayer.stop();
				} catch (IllegalStateException e) {
					sendMsg(VideoConst.MSG_INVALID_STATE);
					e.printStackTrace();
					throw new IllegalStateException(e);
				}
				mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
			}

			if (videopl.getShuffleMode(Const.FILTER_VIDEO) == Const.SHUFFLE_ON
					|| videopl.getRepeatMode(Const.FILTER_VIDEO) == Const.REPEAT_NONE) {
				if (videopl.getCurrentIndex(Const.FILTER_VIDEO) >= (videopl
						.getFileNum(Const.FILTER_VIDEO) - 1)) {
					stop();
					MmpTool.LOG_DBG("End of PlayList!");
					throw new IllegalStateException("End of PlayList!!!");
				}
			}

			mcurPath = videopl.getNext(Const.FILTER_VIDEO, Const.MANUALNEXT);

			MmpTool.LOG_DBG(mcurPath);

			// mmediaplayer.reset();

			mPlayStatus = VideoConst.PLAY_STATUS_END;

			try {
				setDataSource();
			} catch (IllegalStateException e) {
				throw new IllegalStateException(e);
			}

			try {
				play();
			} catch (IllegalStateException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/**
	 * manual preview
	 */
	public void manualPrev() throws IllegalStateException {

		if (mmediaplayer != null) {
			if (mplayerMode == VideoConst.PLAYER_MODE_HTTP) {
				MmpTool.LOG_ERROR("This player mode can't do prev!");
				throw new IllegalStateException("Can't do Prev!!!");
			}

			if (mPlayStatus == VideoConst.PLAY_STATUS_INITED) {
				return;
			}

			if (mPlayStatus >= VideoConst.PLAY_STATUS_PREPAREING
					&& mPlayStatus <= VideoConst.PLAY_STATUS_PREPARED) {
				cleanListener();
				try {
					mmediaplayer.getMetaDataStop();
				} catch (IllegalStateException e) {
					sendMsg(VideoConst.MSG_INVALID_STATE);
					e.printStackTrace();
					throw new IllegalStateException(e);
				}
				mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
			} else if (mPlayStatus >= VideoConst.PLAY_STATUS_STARTED
					&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {
				cleanListener();
				try {
					mmediaplayer.stop();
				} catch (IllegalStateException e) {
					sendMsg(VideoConst.MSG_INVALID_STATE);
					e.printStackTrace();
					throw new IllegalStateException(e);
				}
				mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
			}

			if (videopl.getShuffleMode(Const.FILTER_VIDEO) == Const.SHUFFLE_ON
					|| videopl.getRepeatMode(Const.FILTER_VIDEO) == Const.REPEAT_NONE) {
				if (videopl.getCurrentIndex(Const.FILTER_VIDEO) == 0) {
					stop();
					MmpTool.LOG_DBG("End of PlayList!");
					throw new IllegalStateException("End of PlayList!!!");
				}
			}

			mcurPath = videopl.getNext(Const.FILTER_VIDEO, Const.MANUALPRE);

			if (mcurPath == null) {
				MmpTool.LOG_DBG("End of PlayList!");
				stop();
				throw new IllegalStateException("End of PlayList!!!");
			}

			// mmediaplayer.reset();

			mPlayStatus = VideoConst.PLAY_STATUS_END;

			try {
				setDataSource();
			} catch (IllegalStateException e) {
				throw new IllegalStateException(e);
			}

			try {
				play();
			} catch (IllegalStateException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/**
	 * Replay a video
	 */
	public void replay() throws IllegalStateException {
		if (mmediaplayer != null) {
			if (mplayerMode == VideoConst.PLAYER_MODE_HTTP) {
				MmpTool.LOG_ERROR("This player mode can't do prev!");
				throw new IllegalStateException("Can't do Prev!!!");
			}

			if (mPlayStatus != VideoConst.PLAY_STATUS_STOPPED
					&& mPlayStatus != VideoConst.PLAY_STATUS_END) {
				try {
					mmediaplayer.stop();
				} catch (IllegalStateException e) {
					sendMsg(VideoConst.MSG_INVALID_STATE);
					e.printStackTrace();
					throw new IllegalStateException(e);
				}
				mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
			}

			mcurPath = videopl.getCurrentPath(Const.FILTER_VIDEO);

			if (mcurPath == null) {
				MmpTool.LOG_DBG("End of PlayList!");
				stop();
			}

			// mmediaplayer.reset();

			mPlayStatus = VideoConst.PLAY_STATUS_END;

			try {
				setDataSource();
			} catch (IllegalStateException e) {
				throw new IllegalStateException(e);
			}

			try {
				play();
			} catch (IllegalStateException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/**
	 * Fast forward
	 */
	public void fastForward() throws IllegalStateException, NotSupportException {
//		MtkMediaPlayer.PlayerSpeed eSpeed;

		if (!isFastForward()) {
			throw new NotSupportException(VideoConst.NOT_SUPPORT);
		}
		
		
		if (mplayerMode == VideoConst.PLAYER_MODE_DLNA) {
			DLNADataSource dlnaDataSource = DLNAManager.getInstance()
					.getDLNADataSource(mcurPath);
			if (dlnaDataSource != null) {
				if (!dlnaDataSource.getContent().canSeek()) {
					throw new NotSupportException(VideoConst.NOT_SUPPORT);
				}
			}
		}

		switch (mPlayStatus) {
		case VideoConst.PLAY_STATUS_FF:
			speedStep <<= 1;

			if (speedStep > 32) {
				speedStep = 1;
				eSpeed = PlayerSpeed.SPEED_1X;
				try {
					mmediaplayer.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
					// sendMsg(VideoConst.MSG_INVALID_STATE);
					throw new IllegalStateException(e);
				}
				mPlayStatus = VideoConst.PLAY_STATUS_PLAYED;
			} else {
				switch (speedStep) {
				case 2:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_2X;
					break;

				case 4:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_4X;
					break;

				case 8:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_8X;
					break;

				case 16:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_16X;
					break;

				case 32:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_32X;
					break;

				default:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_2X;
					break;
				}

				try {
					mmediaplayer.setSpeed(eSpeed);
				} catch (NotSupportException e) {
					e.printStackTrace();
					// sendMsg(VideoConst.MSG_NOT_SUPPORT);
					/* don't throw exception,(cr:368100)add by lei start */
					// throw new NotSupportException(e);
					if (speedStep != 1) {
						speedStep = 32;
						fastForward();
					}
					/* end. */
				}

				mPlayStatus = VideoConst.PLAY_STATUS_FF;
			}
			break;

		case VideoConst.PLAY_STATUS_STEP:
			try {
				mmediaplayer.pause();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				// sendMsg(VideoConst.MSG_INVALID_STATE);
				throw new IllegalStateException(e);
			}
		case VideoConst.PLAY_STATUS_PLAYED:
		case VideoConst.PLAY_STATUS_PAUSED:
		case VideoConst.PLAY_STATUS_FR:
		case VideoConst.PLAY_STATUS_SF:
			speedStep = 2;
			eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_2X;

			try {
				mmediaplayer.setSpeed(eSpeed);
			} catch (NotSupportException e) {
				e.printStackTrace();
				// sendMsg(VideoConst.MSG_NOT_SUPPORT);
                speedStep = 1;
                eSpeed = PlayerSpeed.SPEED_1X;
				throw new NotSupportException(e);
			}
			mPlayStatus = VideoConst.PLAY_STATUS_FF;
			break;

		default:
			break;
		}
	}

	/**
	 * fast review play
	 */
	public void fastRewind() throws IllegalStateException, NotSupportException {
//		MtkMediaPlayer.PlayerSpeed eSpeed;

		if (!isFastRewind()) {
			throw new NotSupportException(VideoConst.NOT_SUPPORT);
		}
		if (mplayerMode == VideoConst.PLAYER_MODE_DLNA) {
			DLNADataSource dlnaDataSource = DLNAManager.getInstance()
					.getDLNADataSource(mcurPath);
			if (dlnaDataSource != null) {
				if (!dlnaDataSource.getContent().canSeek()) {
					throw new NotSupportException(VideoConst.NOT_SUPPORT);
				}
			}
		}

		switch (mPlayStatus) {
		case VideoConst.PLAY_STATUS_FR:
			speedStep <<= 1;

			if (speedStep > 32) {
				speedStep = 1;
				eSpeed = PlayerSpeed.SPEED_1X;
				try {
					mmediaplayer.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
					// sendMsg(VideoConst.MSG_INVALID_STATE);
					throw new IllegalStateException(e);
				}

				mPlayStatus = VideoConst.PLAY_STATUS_PLAYED;
			} else {
				switch (speedStep) {
				case 2:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_2X;
					break;

				case 4:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_4X;
					break;

				case 8:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_8X;
					break;

				case 16:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_16X;
					break;

				case 32:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_32X;
					break;

				default:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_2X;
					break;
				}

				try {
					mmediaplayer.setSpeed(eSpeed);
				} catch (NotSupportException e) {
					e.printStackTrace();
					// sendMsg(VideoConst.MSG_NOT_SUPPORT);
					throw new NotSupportException(e);
				}

				mPlayStatus = VideoConst.PLAY_STATUS_FR;
			}
			break;

		case VideoConst.PLAY_STATUS_STEP:
			try {
				mmediaplayer.pause();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				// sendMsg(VideoConst.MSG_INVALID_STATE);
				throw new IllegalStateException(e);
			}
		case VideoConst.PLAY_STATUS_PLAYED:
		case VideoConst.PLAY_STATUS_PAUSED:
		case VideoConst.PLAY_STATUS_FF:
		case VideoConst.PLAY_STATUS_SF:
			speedStep = 2;
			eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_2X;

			try {
				mmediaplayer.setSpeed(eSpeed);
			} catch (NotSupportException e) {
				e.printStackTrace();
				// sendMsg(VideoConst.MSG_NOT_SUPPORT);
                speedStep = 1;
                eSpeed = PlayerSpeed.SPEED_1X;
				throw new NotSupportException(e);
			}
			mPlayStatus = VideoConst.PLAY_STATUS_FR;
			break;

		default:
			break;
		}
	}

	/**
	 * slow forward
	 */
	public void slowForward() throws IllegalStateException, NotSupportException {
		MtkMediaPlayer.PlayerSpeed eSpeed;

		if (!isFastRewind()) {
			throw new NotSupportException(VideoConst.NOT_SUPPORT);
		}

		switch (mPlayStatus) {
		case VideoConst.PLAY_STATUS_SF:
			speedStep <<= 1;

			if (speedStep > 32) {
				speedStep = 1;
				eSpeed = PlayerSpeed.SPEED_1X;
				try {
					mmediaplayer.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
					// sendMsg(VideoConst.MSG_INVALID_STATE);
					throw new IllegalStateException(e);
				}
				mPlayStatus = VideoConst.PLAY_STATUS_PLAYED;
			} else {
				switch (speedStep) {
				case 2:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_1_2X;
					break;

				case 4:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_1_4X;
					break;

				case 8:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_1_8X;
					break;

				case 16:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_1_16X;
					break;

				case 32:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_1_32X;
					break;

				default:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_1_2X;
					break;
				}

				try {
					mmediaplayer.setSpeed(eSpeed);
				} catch (NotSupportException e) {
					e.printStackTrace();
					// sendMsg(VideoConst.MSG_NOT_SUPPORT);
					throw new NotSupportException(e);
				}

				mPlayStatus = VideoConst.PLAY_STATUS_SF;
			}
			break;

		case VideoConst.PLAY_STATUS_STEP:
			try {
				mmediaplayer.pause();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				// sendMsg(VideoConst.MSG_INVALID_STATE);
				throw new IllegalStateException(e);
			}
		case VideoConst.PLAY_STATUS_PLAYED:
		case VideoConst.PLAY_STATUS_PAUSED:
		case VideoConst.PLAY_STATUS_FF:
		case VideoConst.PLAY_STATUS_FR:
			speedStep = 2;
			eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_1_2X;

			try {
				mmediaplayer.setSpeed(eSpeed);
			} catch (NotSupportException e) {
				e.printStackTrace();
				// sendMsg(VideoConst.MSG_NOT_SUPPORT);
				speedStep = 1;
				eSpeed = PlayerSpeed.SPEED_1X;
				throw new NotSupportException(e);
			}

			mPlayStatus = VideoConst.PLAY_STATUS_SF;
			break;

		default:
			break;
		}
	}

	public void step() throws IllegalStateException {
		if (mmediaplayer != null) {
			if (mPlayStatus == VideoConst.PLAY_STATUS_PAUSED
					|| mPlayStatus == VideoConst.PLAY_STATUS_STEP) {
				try {
					mmediaplayer.step(1);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					// sendMsg(VideoConst.MSG_STEP_NOT_SUPPORT);
					// add by shuming for fix bug DTV00384296
					sendMsg(VideoConst.MSG_NOT_SUPPORT);
					e.printStackTrace();
					throw new IllegalStateException(e);
				}

				mPlayStatus = VideoConst.PLAY_STATUS_STEP;
			} 
			// fix cr DTV00406500,by xiaoyao
//			else {
//				sendMsg(VideoConst.MSG_STEP_NOT_SUPPORT);
//			}
		}
	}

	public void abRepeat() {

	}

	public void selectMts(short mtsIdx) {
		if (mmediaplayer != null) {

			/*
			 * MediaInfo mInfo = mmediaplayer.getMediaInfo();
			 * 
			 * if (mtsIdx > mInfo.getAudioTrackNumber()) {
			 * sendMsg(VideoConst.MSG_NOT_SUPPORT); return; }
			 */

			mmediaplayer.setAudioTrack(mtsIdx);
		}
	}

	/*
	 * private short getTsAudioChannel() { short audTrack = 0; TsInfo tsInfo =
	 * new TsInfo();
	 * 
	 * try { tsInfo = mmediaplayer.getTSInfo(VideoConst.TS_READ_BUFFER); } catch
	 * (IllegalStateException e) { e.printStackTrace(); return audTrack; }
	 * 
	 * if (tsInfo.getPmt_num() == 0) { return audTrack; }
	 * 
	 * List<PmtInfo> pmtInfos = new ArrayList<PmtInfo>(tsInfo.getPmt_num());
	 * pmtInfos.addAll(tsInfo.getPmtInfo());
	 * 
	 * if (tsProgramIndex >= tsInfo.getPmt_num()) { return audTrack; }
	 * 
	 * if (pmtInfos.get(tsProgramIndex).getStrm_num() == 0) { return audTrack; }
	 * 
	 * List<StreamInfo> streamInfos = new ArrayList<StreamInfo>(pmtInfos.get(
	 * tsProgramIndex).getStrm_num());
	 * 
	 * streamInfos.addAll(pmtInfos.get(tsProgramIndex).getStreamInfo());
	 * 
	 * for (int i = 0; i < streamInfos.size(); i++) {
	 * 
	 * for (short s : VideoConst.tsAudioStrmType) { if
	 * (streamInfos.get(i).getStrm_type() == s) { audTrack++; } } }
	 * 
	 * return audTrack; }
	 */

	public VidMediaInfo getMediaInfo() {
		VidMediaInfo vidInfo = new VidMediaInfo();

		if (mmediaplayer != null) {
			if (mPlayStatus >= VideoConst.PLAY_STATUS_PLAYED
					&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {

				MediaInfo mInfo = mmediaplayer.getMediaInfo();
				AudioTrackInfo audioInfo = mmediaplayer.getAudioTrackInfo();
				VideoTrackInfo videoInfo = mmediaplayer.getVideoTrackInfo();

				vidInfo.setMediaInfo(mInfo.getMediaType(), mInfo.getDuration(),
						mInfo.getSize(), mInfo.getAverageBitrate(), mInfo
								.getAudioTrackNumber(), mInfo
								.getSubtitleTrackNumber());

				vidInfo.setVideoTrackInfo(videoInfo.getVideoCodec(), videoInfo
						.getWidth(), videoInfo.getHeigth(), videoInfo
						.getInstantBitrate());

				vidInfo.setAudioTrackInfo(audioInfo.getAudioCodec(), audioInfo
						.getChannelNumber(), audioInfo.getSampleRate(),
						audioInfo.getBitrate());
			}
		}

		return vidInfo;
	}

	/**
	 * Get Video Track information, for example, width, height,
	 * 
	 * @deprecated
	 * @return VideoTrackInfo.
	 */
	public VideoTrackInfo getVideoTrackInfo() {
		if (mmediaplayer == null) {
			return null;
		}

		return mmediaplayer.getVideoTrackInfo();

	}

	/**
	 * Get Video information, when ts file, for example, width, height,
	 * 
	 * @return VideoInfo.
	 */
	public VideoInfo getVideoInfo() {
		if (mmediaplayer == null) {
			return null;
		}
		return mmediaplayer.getVideoInfo();
	}

	/**
	 * get duration
	 */
	public int getDuration() {
		if (mmediaplayer != null) {
			if (mPlayStatus >= VideoConst.PLAY_STATUS_PLAYED
					&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {
				return mmediaplayer.getDuration();
			} else {
				return 0;
			}
		}
		return 0;
	}

	/**
	 * get progress
	 */
	public int getProgress() {
		if (mmediaplayer != null) {
			if (mPlayStatus >= VideoConst.PLAY_STATUS_PLAYED
					&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {

                int prog = mmediaplayer.getCurrentPosition();
               
                if (mPlayStatus == VideoConst.PLAY_STATUS_FR) {
                	////add by shuming for fix bug DTV00385698
                	//setFeaturenotsurport(false);
					//end
					/*mark by lei, and onReplayListener will do it.*/					
//                    if (prog >= 0 && prog < 1000) {                    	                   	                   	                   	                      
//                        
//                        mPlayStatus = VideoConst.PLAY_STATUS_PLAYED;
//                        sendMsg(VideoConst.MSG_PLAY_START);
//                    }
                }

				return prog;
			} else {
				return 0;
			}
		}
		return 0;
	}

	/**
	 * get current file position
	 */
	public long getCurFilePosition() {
		if (mmediaplayer != null) {
			if (mPlayStatus >= VideoConst.PLAY_STATUS_PLAYED
					&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {

                long pos = mmediaplayer.getCurrentPosition(
                		MtkMediaPlayer.PositionType.POSITION);

				return pos;
			} else {
				return 0;
			}
		}
		return 0;
	}

	/**
	 * if play end
	 */
	public boolean isEnd() {
		return isEnd;
	}

	/**
	 * get play state
	 */
	public int getPlayStatus() {
		return mPlayStatus;
	}

	/**
	 * get speed
	 */
	public int getSpeed() {
		return speedStep;
	}

	/**
	 * get program count
	 */
	public byte getProgramCount() {
		if (mmediaplayer != null) {
			if (mPlayStatus >= VideoConst.PLAY_STATUS_PLAYED
					&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {

				try {
//					TsInfo tsInfo = mmediaplayer
//							.getTSInfo(VideoConst.TS_READ_BUFFER);
					
					 return (byte) mmediaplayer.getMediaInfo().getTsProgramNum();//tsInfo.getPmt_num();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					return 0;
				}
			}
		}

		return 0;
	}

	/**
	 * set program
	 * 
	 * @param index
	 */
	public void setProgram(short index) {
		if (mmediaplayer != null) {
			if (mPlayStatus >= VideoConst.PLAY_STATUS_PLAYED
					&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {
				mmediaplayer.setTS(index);
				tsProgramIndex = index;
			}
		}
	}

	/**
	 * get video title
	 */
	public String getVideoTitle() {
//		String title = "";
		StringBuilder title = new StringBuilder("");

		if (mPlayStatus >= VideoConst.PLAY_STATUS_PLAYED
				&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {
			MetaDataInfo metaDataInfo = mmediaplayer
					.getMetaDataInfoInstance(mmediaplayer);
			short titleASC2[] = metaDataInfo.getTitle();
			for (int i = 0; i < titleASC2.length; i++) {
				if (titleASC2[i] == 0) {
					break;
				}
//				title = title + (char) titleASC2[i];
				title.append((char) titleASC2[i]);
			}
		}

		MmpTool.LOG_DBG(title.toString());
		return title.toString();
	}

	/**
	 * get video director
	 */
	public String getVideoDirector() {
//		String artist = "";
		StringBuilder artist = new StringBuilder("");

		if (mPlayStatus >= VideoConst.PLAY_STATUS_PLAYED
				&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {
			MetaDataInfo metaDataInfo = mmediaplayer
					.getMetaDataInfoInstance(mmediaplayer);
			short artistASC2[] = metaDataInfo.getDirector();
			for (int i = 0; i < artistASC2.length; i++) {
				if (artistASC2[i] == 0) {
					break;
				}
//				artist = artist + (char) artistASC2[i];
				artist.append((char) artistASC2[i]);
			}
		}

		MmpTool.LOG_DBG(artist.toString());
		return artist.toString();
	}

	/**
	 * get video copy right
	 */
	public String getVideoCopyright() {
//		String album = "";
		StringBuilder album = new StringBuilder("");

		if (mPlayStatus >= VideoConst.PLAY_STATUS_PLAYED
				&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {
			MetaDataInfo metaDataInfo = mmediaplayer
					.getMetaDataInfoInstance(mmediaplayer);
			short albumASC2[] = metaDataInfo.getCopyright();
			for (int i = 0; i < albumASC2.length; i++) {
				if (albumASC2[i] == 0) {
					break;
				}
//				album = album + (char) albumASC2[i];
				album.append((char) albumASC2[i]);
			}
		}

		MmpTool.LOG_DBG(album.toString());
		return album.toString();
	}

	public String getVideoGenre() {
//		String genre = "";
		StringBuilder genre = new StringBuilder("");

		if (mPlayStatus >= VideoConst.PLAY_STATUS_PLAYED
				&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {
			MetaDataInfo metaDataInfo = mmediaplayer
					.getMetaDataInfoInstance(mmediaplayer);
			short genreASC2[] = metaDataInfo.getGenre();
			for (int i = 0; i < genreASC2.length; i++) {
				if (genreASC2[i] == 0) {
					break;
				}
//				genre = genre + (char) genreASC2[i];
				genre.append((char) genreASC2[i]);
			}
		}

		MmpTool.LOG_DBG(genre.toString());
		return genre.toString();
	}

	/**
	 * get video year
	 */
	public String getVideoYear() {
//		String year = "";
		StringBuilder year = new StringBuilder("");

		if (mPlayStatus >= VideoConst.PLAY_STATUS_PLAYED
				&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {
			MetaDataInfo metaDataInfo = mmediaplayer
					.getMetaDataInfoInstance(mmediaplayer);
			short yearASC2[] = metaDataInfo.getYear();
			for (int i = 0; i < yearASC2.length; i++) {
				if (yearASC2[i] == 0) {
					break;
				}
//				year = year + (char) yearASC2[i];
				year.append((char) yearASC2[i]);
			}
		}

		MmpTool.LOG_DBG(year.toString());
		return year.toString();
	}

	/**
	 * Registe Listener
	 */

	private OnErrorListener errListener = new OnErrorListener() {
		public boolean onError(MtkMediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			MmpTool.LOG_ERROR(" extra =  " + extra);
			MmpTool.LOG_ERROR("what =  " + what);
			if (what == 1) {
				sendMsg(VideoConst.MSG_AUDIO_NOT_SUPPORT);
			} else if (what == 2) {
				sendMsg(VideoConst.MSG_VIDEO_NOT_SUPPORT);
			} else if (what == 3) {
				sendMsg(VideoConst.MSG_AV_NOT_SUPPORT);
				// stop();
			} else if (what == 8) {
				sendMsg(VideoConst.MSG_FILE_CORRUPT);
				// stop();
			} else if (what == 4) {
				sendMsg(VideoConst.MSG_FILE_NOT_SUPPORT);
				// stop();
			} else {
				sendMsg(VideoConst.MSG_FILE_NOT_SUPPORT);
			}

			return true;
		}
	};

	private OnAudioOnlyListener audioOnlyListener = new OnAudioOnlyListener() {

		public boolean onAudioOnly(MtkMediaPlayer mp, int what, int extra) {
			MmpTool.LOG_ERROR("what =  " + what);

			sendMsg(VideoConst.MSG_FILE_AUDIOONLY);
			return true;
		}

	};
	private OnCompletionListener completeListener = new OnCompletionListener() {

		public boolean onCompletion(MtkMediaPlayer arg0, int flag, int exta) {
			// TODO Auto-generated method stub
			MmpTool.LOG_DBG("play compeletion!");
			if (mmediaplayer != null) {

				// add by shuming for fix bug DTV00385698
				setFeaturenotsurport(true);
				// end
				if (previewMode) {
					isEnd = true;
					sendMsg(VideoConst.MSG_END_OF_PLAYLIST);
					return false;
				}

				if (videopl.getShuffleMode(Const.FILTER_VIDEO) == Const.SHUFFLE_ON
						|| videopl.getRepeatMode(Const.FILTER_VIDEO) == Const.REPEAT_NONE) {
					if (videopl.getCurrentIndex(Const.FILTER_VIDEO) >= ((videopl
							.getFileNum(Const.FILTER_VIDEO) - 1))) {
						try {
							// stop();
							isEnd = true;
							sendMsg(VideoConst.MSG_END_OF_PLAYLIST);
							throw new Exception("End of PlayList!!!");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				if (!isEnd && !previewMode) {
					sendMsg(VideoConst.MSG_AUTO_NEXT);
				}

				if (mOnPBCompleteListener != null) {

					mOnPBCompleteListener.onComplete(pb);
				}
			}

			return false;
		}

	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VideoConst.MSG_PRAPARE: {
				MmpTool.LOG_DBG("Prepared ok!");
				sendMsg(VideoConst.MSG_PLAY_START);

				mmediaplayer.setDisplay(0,0,1000,1000);

				try {
					MmpTool.LOG_DBG("Start to play!");
					mmediaplayer.start();
					mPlayStatus = VideoConst.PLAY_STATUS_STARTED;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					sendMsg(VideoConst.MSG_PLAY_START_FAIL);
				}

				if (mOnPBPreparedListener != null) {
					mOnPBPreparedListener.onPrepared(pb);
				}
			}
				break;
			default:
				break;
			}
		}
	};

	private OnPreparedListener preListener = new OnPreparedListener() {

		public void onPrepared(MtkMediaPlayer arg0) {

			mHandler.sendEmptyMessage(VideoConst.MSG_PRAPARE);
			mPlayStatus = VideoConst.PLAY_STATUS_PREPARED;
		}
	};

	private OnBufferingUpdateListener bufferListener = new OnBufferingUpdateListener() {

		public void onBufferingUpdate(MtkMediaPlayer arg0, int arg1) {
			// TODO Auto-generated method stub

			if (mOnPBBufferUpdateListener != null) {
				mOnPBBufferUpdateListener.onBufferUpdate(pb);
			}
		}
	};

	private OnSeekCompleteListener seekListener = new OnSeekCompleteListener() {

		public void onSeekComplete(MtkMediaPlayer mp) {

			MmpTool.LOG_DBG("Seek Done!");
			sendMsg(VideoConst.MSG_SEEK_DONE);
		}
	};

	private OnPlayDoneListener playDoneListener = new OnPlayDoneListener() {

		public void onPlayDone(MtkMediaPlayer arg0) {

			MmpTool.LOG_DBG("Play Done!");

			if (previewMode == true) {
				setPreviewRect();
			} else {
				videoZoomReset();
			}

			setSubOnOff(false);
			mPlayStatus = VideoConst.PLAY_STATUS_PLAYED;
			if (mOnPBPlayDoneListener != null) {
				mOnPBPlayDoneListener.onPlayDone(pb);
			}
			

		}
	};
	private OnSpeedUpdateListener speedUpdateListener = new OnSpeedUpdateListener() {

        public void onSpeedUpdate(MtkMediaPlayer arg0, int arg1, int arg2) {
            
        	MmpTool.LOG_INFO("---------OnSpeedUpdateListener-------");
        	/*mark by lei, and onReplayListener will do it.*/
        	//add by lei fix cr DTV00391641
//        	if(getCurFilePosition() == 0 && mmediaplayer.getCurrentPosition() != 0){
//        		return;
//        	}
//        	
//        	if ( getCurFilePosition() < 2.0f)
//        	{
//        		//add by Xiaojie for fix bug about capture DTV00389206              
//        		speedStep = 1;
//        		sendMsg(VideoConst.MSG_RESET_VIDEO_ZOOM_MODE);           		
//        	}
        }
    };

    private OnTotalTimeUpdateListener durationListener = new OnTotalTimeUpdateListener() {

        public void onTotalTimeUpdate(MtkMediaPlayer arg0, int arg1, int arg2) {
            
            if (mOnPBDurarionUpdateListener != null) {
                mOnPBDurarionUpdateListener.onUpdate(pb, arg1);
            }
        }
    };

    private OnEofListener eofListener = new OnEofListener() {

        public void onEof(MtkMediaPlayer arg0, int arg1, int arg2) {
            
            if (mOnPBEofListener != null) {
                mOnPBEofListener.onEof(pb, arg1);
            }
        }
    };
    
    private OnPositionUpdateListener posUpdateListener = new OnPositionUpdateListener(){
	
		public void onPositionUpdate(MtkMediaPlayer arg0, int arg1, int arg2) {
			MmpTool.LOG_INFO("---------OnPositionUpdateListener------- arg1 =" + arg1);
			sendMsg(VideoConst.MSG_POSITION_UPDATE);
			/*mark by lei, and onReplayListener will do it.*/
//            if (mPlayStatus == VideoConst.PLAY_STATUS_FR) {
//                if (arg1 >= 0 && arg1 < 1000) {               	
//                    mPlayStatus = VideoConst.PLAY_STATUS_PLAYED;
//                    sendMsg(VideoConst.MSG_PLAY_START);
//                }
//            }
		}   	
    };
    
    
    private OnStepDoneListener stepDoneListener = new OnStepDoneListener(){

		public void onStepDone(MtkMediaPlayer arg0, int arg1, int arg2) {
			
			MmpTool.LOG_INFO("---------stepDoneListener-------");
			sendMsg(VideoConst.MSG_STEP_DONE);
		}
    	
    };
    
    /**
     * replay listener, JNI will call onReplay funcion when auto replay .
     */
    private OnRePlayListener replayListener = new OnRePlayListener(){

		public void onRePlay(MtkMediaPlayer arg0, int arg1, int arg2) {
			MmpTool.LOG_INFO("---------replayListener-------");
			//add by shuming for fix bug DTV00385698
            setFeaturenotsurport(false);
			//end
            mPlayStatus = VideoConst.PLAY_STATUS_PLAYED;
            sendMsg(VideoConst.MSG_PLAY_START);
            /*get heght and width is 0 when don't delay send msg.*/
            //videoMsgHandler.sendEmptyMessageDelayed(VideoConst.MSG_PLAY_START, 200);
    		speedStep = 1;
    		eSpeed = PlayerSpeed.SPEED_1X;
		} 	
    };
    
    Handler videoMsgHandler = new Handler() {
        public void handleMessage(Message msg) {
			if (msg.what == VideoConst.MSG_AUTO_NEXT) {
				MmpTool.LOG_DBG("auto next");
				autoNext();
			} else if (msg.what == VideoConst.MSG_END_OF_PLAYLIST) {
				MmpTool.LOG_DBG("end of list!");
				stop();
			}
			else if(msg.what == VideoConst.MSG_AV_NOT_SUPPORT){
				if(mcurPath!=null && mcurPath.endsWith(".ts")){

					if(tsProgramIndex<getProgramCount()-1){
						tsProgramIndex+=1;
						setProgram(tsProgramIndex);
						if (mOnPBMsgListener != null) {
							mOnPBMsgListener.onMsg(VideoConst.MSG_RETRY_PROGRAM);
						}
						return ;
					}
					
				}
			}

			if (mOnPBMsgListener != null) {
				mOnPBMsgListener.onMsg(msg.what);
			}
		}
	};

	private int setPreviewRect() {
		Rect outRect = new Rect(VideoConst.PREVIEW_RECT_X,
				VideoConst.PREVIEW_RECT_Y, VideoConst.PREVIEW_RECT_X
						+ VideoConst.PREVIEW_RECT_W, VideoConst.PREVIEW_RECT_Y
						+ VideoConst.PREVIEW_RECT_H);

		if (inputManager == null) {
			MmpTool.LOG_DBG("input manager null!!!");
			return -1;
		}

		return inputManager.setScreenOutputRect(InputService.INPUT_OUTPUT_MAIN,
				outRect);
	}

	private int videoZoomReset() {
		Rect outRect = new Rect(0, 0, VideoConst.VOUT_REGION_MAX_WIDTH,
				VideoConst.VOUT_REGION_MAX_HEIGTH);

		if (inputManager == null) {
			MmpTool.LOG_DBG("input manager null!!!");
			return -1;
		}

		MmpTool.LOG_DBG("zoom reset!!!");
		return inputManager.setScreenOutputRect(InputService.INPUT_OUTPUT_MAIN,
				outRect);
	}

	/**
	 * Subtitle
	 */
	private class subFilter implements FilenameFilter {
		public boolean isSub(String file) {
			for (String s : VideoConst.subSuffix) {
				if (file.toLowerCase().endsWith(s)) {
					return true;
				}
			}
			return false;
		}

		public boolean accept(File dir, String fname) {
			return (isSub(fname));
		}
	}

	/**
	 * find external subtitle
	 * 
	 * @return
	 */
	public String findExternalSubtitle() {

		String subPath = null;
		String vFileName = videopl.getCurrentFileName(Const.FILTER_VIDEO);
		MmpTool.LOG_DBG(vFileName);
		if (vFileName == null) {
			return null;
		}
		int index = vFileName.lastIndexOf('.');
		if (index < 0) {
			return null;
		}
		String vFileNameTmp = vFileName.substring(0, index);
		MmpTool.LOG_DBG(vFileNameTmp);

		if (mplayerMode == VideoConst.PLAYER_MODE_MMP) {

			File retFile = new File(mcurPath);

			File parentFile = retFile.getParentFile();

			MmpTool.LOG_DBG(parentFile.getPath());

			File[] subFiles = parentFile.listFiles(new subFilter());

			if (subFiles == null) {
				return null;
			}

			MmpTool.LOG_DBG("length = " + subFiles.length);

			if (subFiles.length == 0) {
				return null;
			}

			int i = 0;

			for (i = 0; i < subFiles.length; i++) {
				String subName = subFiles[i].getName();
				String vSubNameTemp = subName.substring(0, subName
						.lastIndexOf('.'));
				MmpTool.LOG_DBG(vSubNameTemp);

				if (vSubNameTemp.compareTo(vFileNameTmp) == 0) {
					break;
				}
			}

			if (i >= subFiles.length) {
				return null;
			}

			MmpTool.LOG_DBG("i = " + i);

			subPath = subFiles[i].getPath();
			MmpTool.LOG_DBG(subPath);

		}

		return subPath;
	}

	public void setSubtitleTrack(short index) {
		if (mmediaplayer != null) {
			if (mPlayStatus >= VideoConst.PLAY_STATUS_PLAYED
					&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {
				mmediaplayer.setSubtitleTrack(index);
			}
		}
	}

	/**
	 * set subtitle default type
	 */
	public void setSubDefaultType() {
		SubtitleAttr subtAttr = new SubtitleAttr();
		subtAttr.setDefaultType();

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	/**
	 * set sub dispaly mode
	 */
	public void setSubDisplayMode(int mode) {

		SubtitleAttr subtAttr = new SubtitleAttr();
		SubtitleAttr.SubtitleDisplayMode displayMode = subtAttr.new SubtitleDisplayMode(
				mode, (short) 1);
		subtAttr.setDisplayMode(displayMode);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	/**
	 * set sub high light style
	 */
	public void setSubHighlightStyle(int highlightStyle) {

		SubtitleAttr subtAttr = new SubtitleAttr();

		SubtitleAttr.SubtitleHiltStyle hiltStyle = subtAttr.new SubtitleHiltStyle(
				highlightStyle, (short) 1);
		subtAttr.setHiltStyle(hiltStyle);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	/**
	 * set sub timeoffset
	 */
	public void setSubTimeOffset(int offsetMode, int offsetValue) {
		SubtitleAttr subtAttr = new SubtitleAttr();

		SubtitleAttr.SubtitleTimeOffset timeOffset = subtAttr.new SubtitleTimeOffset(
				offsetMode, offsetValue);
		subtAttr.setTimeOffset(timeOffset);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	/**
	 * set sub font encode
	 */
	public void setSubFontEncode(int encodeMode) {
		SubtitleAttr subtAttr = new SubtitleAttr();

		SubtitleAttr.SubtitleFontEnc fontEnc = subtAttr.new SubtitleFontEnc(
				encodeMode, (short) 1);
		subtAttr.setFontEnc(fontEnc);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	public void setSubOnOff(boolean on) {
		if (on == false) {
			setSubtitleTrack((short) 255);
		} else {
			return;
		}
	}

	public void setSubShowHide(boolean on) {
		SubtitleAttr subtAttr = new SubtitleAttr();

		subtAttr.setShowHide(on);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	public void setSubFontInfo(Subtitle.FontInfo info) {
		SubtitleAttr subtAttr = new SubtitleAttr();

		SubtitleAttr.SubtitleFontInfo fontInfo = subtAttr.new SubtitleFontInfo(
				info.fontSize, info.fontStyle, info.cmapEncoding,
				info.fontName, info.width, info.customSize);
		subtAttr.setFontInfo(fontInfo);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	public void setSubFontSize(int size) {
		SubtitleAttr subtAttr = new SubtitleAttr();

		SubtitleAttr.SubtitleFontInfo fontInfo = subtAttr.new SubtitleFontInfo(
				size, SubtitleFontInfo.STYLE_UNIFORM,
				SubtitleFontInfo.ENCODING_UNICODE, "fnt_Default", (short) 0,
				(byte) 0);

		subtAttr.setFontInfo(fontInfo);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	public void setSubFontCustomSize(byte customSize) {
		SubtitleAttr subtAttr = new SubtitleAttr();

		SubtitleAttr.SubtitleFontInfo fontInfo = subtAttr.new SubtitleFontInfo(
				SubtitleFontInfo.SIZE_CUSTOM, SubtitleFontInfo.STYLE_UNIFORM,
				SubtitleFontInfo.ENCODING_UNICODE, "fnt_Default", (short) 0,
				(byte) customSize);

		subtAttr.setFontInfo(fontInfo);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	public void setSubBgColor(Subtitle.Color color) {
		SubtitleAttr subtAttr = new SubtitleAttr();

		SubtitleAttr.SubtitleColor bgColor = subtAttr.new SubtitleColor(
				color.alpha, color.r, color.g, color.b);
		subtAttr.setBgColor(bgColor);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	/**
	 * set sub text color
	 */
	public void setSubTextColor(Subtitle.Color color) {
		SubtitleAttr subtAttr = new SubtitleAttr();

		SubtitleAttr.SubtitleColor textColor = subtAttr.new SubtitleColor(
				color.alpha, color.r, color.g, color.b);
		subtAttr.setTextColor(textColor);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	public void setSubEdgeColor(Subtitle.Color color) {
		/*
		 * SubtitleAttr subtAttr = new SubtitleAttr();
		 * 
		 * SubtitleAttr.SubtitleColor edgeColor = subtAttr.new SubtitleColor(
		 * color.alpha, color.r, color.g, color.b);
		 * subtAttr.setEdgeColor(edgeColor);
		 * 
		 * if (subtAttr.isValid()) { if (mmediaplayer != null) {
		 * mmediaplayer.setSubtitleAttr(subtAttr); } }
		 */
	}

	public void setSubBoderType(int type) {
		SubtitleAttr subtAttr = new SubtitleAttr();

		subtAttr.setBoderType(type);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}

	}

	/**
	 * set sub boder width
	 */
	public void setSubBoderWidth(int width) {
		SubtitleAttr subtAttr = new SubtitleAttr();

		subtAttr.setBorderWidth(width);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	/**
	 * set sub roll type
	 */
	public void setSubRollType(int type) {
		SubtitleAttr subtAttr = new SubtitleAttr();

		subtAttr.setRollType(type);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	/**
	 * set sub display rect
	 */
	public void setSubDisplayRect(Rect dispRect) {
		SubtitleAttr subtAttr = new SubtitleAttr();

		SubtitleAttr.SubtitleDisplayRect displayRect = subtAttr.new SubtitleDisplayRect(
				dispRect.left, dispRect.top, dispRect.width(), dispRect
						.height());
		subtAttr.setDisplayRect(displayRect);

		if (subtAttr.isValid()) {
			if (mmediaplayer != null) {
				mmediaplayer.setSubtitleAttr(subtAttr);
			}
		}
	}

	public void setSubDisplayOffset(int x, int y) {
		/*
		 * SubtitleAttr subtAttr = new SubtitleAttr();
		 * 
		 * SubtitleAttr.SubtitleDisplayOffset displayOffset = subtAttr.new
		 * SubtitleDisplayOffset(x,y); subtAttr.setDisplayOffset(displayOffset);
		 * 
		 * if (subtAttr.isValid()) { if (mmediaplayer != null) {
		 * mmediaplayer.setSubtitleAttr(subtAttr); } }
		 */
	}

	// add by shuming for fix bug DTV00385698
	/**
	 * setFeaturenotsurport
	 */
	public void setFeaturenotsurport(boolean featurenotsurport) {
		mFeaturenotsurport = featurenotsurport;
	}

	/**
	 * isFeaturenotsurport
	 */
	public boolean isFeaturenotsurport() {
		return mFeaturenotsurport;
	}

	public boolean canDoSeek() {
		if (mmediaplayer != null) {
			if (mPlayStatus >= VideoConst.PLAY_STATUS_PLAYED
					&& mPlayStatus <= VideoConst.PLAY_STATUS_SF) {
				return mmediaplayer.canDoSeek(eSpeed);
			}
		}
		return false;
	}

	// end
}
