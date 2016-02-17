package com.mediatek.mmpcm.audioimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.SmbException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mediatek.media.AudioTrackInfo;
import com.mediatek.media.MetaDataInfo;
import com.mediatek.media.MtkMediaPlayer;
import com.mediatek.media.NotSupportException;
import com.mediatek.media.PcmMediaInfo;
import com.mediatek.media.MtkMediaPlayer.DataSource;
import com.mediatek.media.MtkMediaPlayer.OnRePlayListener;
import com.mediatek.mmpcm.MmpTool;
import com.mediatek.mmpcm.audio.IPlayback;
import com.mediatek.mmpcm.mmcimpl.Const;
import com.mediatek.mmpcm.mmcimpl.PlayList;
import com.mediatek.mmpcm.audioimpl.AudioConst;
import com.mediatek.mmpcm.fileimpl.MtkFile;
import com.mediatek.netcm.dlna.DLNADataSource;
import com.mediatek.netcm.dlna.DLNAManager;
import com.mediatek.netcm.dlna.FileSuffixConst;
import com.mediatek.netcm.samba.SambaManager;

public class MtkPlayback implements IPlayback, DataSource {

    private MtkMediaPlayer mtkMediaPlayer;
    private String dataSource;
    private int speedStep;
    private int mPlayStatus = AudioConst.PLAY_STATUS_UNKNOW;
    private int mPlayMode = AudioConst.PLAYER_MODE_LOCAL;
    private boolean pcmMode = false;
    private PcmMediaInfo pcmInfo;

	private InputStream mInputStream = null;

	public InputStream newInputStream() {
		// TODO Auto-generated method stub
		if (mPlayMode == AudioConst.PLAYER_MODE_LOCAL) {
			
			try {
				closeStream();
				mInputStream = new FileInputStream(dataSource);
				return mInputStream;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else if (mPlayMode == AudioConst.PLAYER_MODE_SAMBA) {
			try {
				return SambaManager.getInstance()
						.getSambaDataSource(dataSource).newInputStream();
			} catch (SmbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else if (mPlayMode == AudioConst.PLAYER_MODE_DLNA) { // DLNA
			DLNADataSource dlnaDataSource = DLNAManager.getInstance()
					.getDLNADataSource(dataSource);
			if (dlnaDataSource != null) {
				return dlnaDataSource.newContentInputStream();
			} else {
				return null;
			}
		} else {// HTTP
			return null;
		}
	}

	MtkPlayback() {
		mtkMediaPlayer = new MtkMediaPlayer();
		mtkMediaPlayer.setOnCompletionListener(completionListener);
		mtkMediaPlayer.setOnPreparedListener(preparedListener);
		mtkMediaPlayer.setOnErrorListener(errorListener);
		mtkMediaPlayer.setOnTotalTimeUpdateListener(totalTimeListener);
		mtkMediaPlayer.setOnSpeedUpdateListener(speedUpdateListener);
		mtkMediaPlayer.setOnEofListener(eofListener);
		mtkMediaPlayer.setOnRePlayListener(replayListener);
	}

	
	private void cleanListener() {
		if (mtkMediaPlayer != null) {
			mtkMediaPlayer.setOnErrorListener(null);
			mtkMediaPlayer.setOnPreparedListener(null);
			mtkMediaPlayer.setOnBufferingUpdateListener(null);
			mtkMediaPlayer.setOnSeekCompleteListener(null);
			mtkMediaPlayer.setOnCompletionListener(null);
			mtkMediaPlayer.setOnPlayDoneListener(null);
			mtkMediaPlayer.setOnEofListener(null);
			mtkMediaPlayer.setOnTotalTimeUpdateListener(null);
			mtkMediaPlayer.setOnPositionUpdateListener(null);
			mtkMediaPlayer.setOnRePlayListener(null);
		}
	}

	private void resetListener() {
		if (mtkMediaPlayer != null) {
			mtkMediaPlayer.setOnCompletionListener(completionListener);
			mtkMediaPlayer.setOnPreparedListener(preparedListener);
			mtkMediaPlayer.setOnErrorListener(errorListener);
			mtkMediaPlayer.setOnTotalTimeUpdateListener(totalTimeListener);
			mtkMediaPlayer.setOnSpeedUpdateListener(speedUpdateListener);
			mtkMediaPlayer.setOnEofListener(eofListener);
			mtkMediaPlayer.setOnRePlayListener(replayListener);
		}
	}
	
	public void setPlayMode(int playMode) {
		mPlayMode = playMode;
	}

	public boolean isPlaying() {

		if (mPlayStatus == AudioConst.PLAY_STATUS_STARTED
				|| mPlayStatus == AudioConst.PLAY_STATUS_FF
				|| mPlayStatus == AudioConst.PLAY_STATUS_FR
				|| mPlayStatus == AudioConst.PLAY_STATUS_SF
				|| mPlayStatus == AudioConst.PLAY_STATUS_SR) {
			return true;
		} else {
			return false;
		}
	}

	public int getSpeed() {
		return speedStep;
	}
	/**
	 * set normal play, ff, fr and so on.
	 * @param speed
	 */
	public void setSpeed(int speed){
		speedStep = speed;
	}
	/**
	 * Set fastForward play, 2x, 4x and so on.
	 * @throws NotSupportException, IllegalStateException
	 */
	public void fastForward() throws NotSupportException, IllegalStateException {
		// TODO Auto-generated method stub
		MtkMediaPlayer.PlayerSpeed eSpeed;
		if (mPlayMode == AudioConst.PLAYER_MODE_DLNA) {
			DLNADataSource dlnaDataSource = DLNAManager.getInstance()
					.getDLNADataSource(dataSource);
			if (dlnaDataSource != null) {
				if (!dlnaDataSource.getContent().canSeek()) {
					throw new NotSupportException(AudioConst.NOT_SUPPORT);
				}
			}
		}
		
		switch (mPlayStatus) {
		case AudioConst.PLAY_STATUS_FF:
			speedStep <<= 1;

			if (speedStep > 32) {
				speedStep = 1;
				mtkMediaPlayer.start();
				mPlayStatus = AudioConst.PLAY_STATUS_STARTED;

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
				
				mtkMediaPlayer.setSpeed(eSpeed);
				mPlayStatus = AudioConst.PLAY_STATUS_FF;
		
			}

			break;

		case AudioConst.PLAY_STATUS_STARTED:
		case AudioConst.PLAY_STATUS_PAUSED:
		case AudioConst.PLAY_STATUS_FR:
		case AudioConst.PLAY_STATUS_SF:
		case AudioConst.PLAY_STATUS_SR:
			eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_2X;
			mtkMediaPlayer.setSpeed(eSpeed);
			speedStep = 2;		
			mPlayStatus = AudioConst.PLAY_STATUS_FF;


			break;

		default:
			break;
		}

	}
	/**
	 * Set fastRewind play, 2x, 4x and so on.
	 * @throws NotSupportException, IllegalStateException
	 */
	public void fastRewind() throws NotSupportException, IllegalStateException {
		// TODO Auto-generated method stub
		MtkMediaPlayer.PlayerSpeed eSpeed;
		if (mPlayMode == AudioConst.PLAYER_MODE_DLNA) {
			DLNADataSource dlnaDataSource = DLNAManager.getInstance()
					.getDLNADataSource(dataSource);
			if (dlnaDataSource != null) {
				if (!dlnaDataSource.getContent().canSeek()) {
					throw new NotSupportException(AudioConst.NOT_SUPPORT);
				}
			}
		}
		switch (mPlayStatus) {
		case AudioConst.PLAY_STATUS_FR:
			speedStep <<= 1;

			if (speedStep > 32) {
				speedStep = 1;
				mPlayStatus = AudioConst.PLAY_STATUS_STARTED;
				mtkMediaPlayer.start();

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

				
				mtkMediaPlayer.setSpeed(eSpeed);
				mPlayStatus = AudioConst.PLAY_STATUS_FR;
			}
			break;

		case AudioConst.PLAY_STATUS_STARTED:
		case AudioConst.PLAY_STATUS_PAUSED:
		case AudioConst.PLAY_STATUS_FF:
		case AudioConst.PLAY_STATUS_SF:
		case AudioConst.PLAY_STATUS_SR:
			
			eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_2X;			
			mtkMediaPlayer.setSpeed(eSpeed);
			speedStep = 2;
			mPlayStatus = AudioConst.PLAY_STATUS_FR;

			break;

		default:
			break;
		}
	}
	/**
	 * Set slowForward play, 1/2x, /14x and so on.
	 * @throws NotSupportException, IllegalStateException
	 */
	public void slowForward() throws NotSupportException, IllegalStateException {
		MtkMediaPlayer.PlayerSpeed eSpeed;

		switch (mPlayStatus) {
		case AudioConst.PLAY_STATUS_SF:
			speedStep <<= 1;

			if (speedStep > 32) {
				speedStep = 1;				
				mtkMediaPlayer.start();
				mPlayStatus = AudioConst.PLAY_STATUS_STARTED;
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

				
				mtkMediaPlayer.setSpeed(eSpeed);
				mPlayStatus = AudioConst.PLAY_STATUS_SF;
			}
			break;

		case AudioConst.PLAY_STATUS_STARTED:
		case AudioConst.PLAY_STATUS_PAUSED:
		case AudioConst.PLAY_STATUS_FF:
		case AudioConst.PLAY_STATUS_FR:
		case AudioConst.PLAY_STATUS_SR:
			
			eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FF_1_2X;	
			mtkMediaPlayer.setSpeed(eSpeed);
			speedStep = 2;
			mPlayStatus = AudioConst.PLAY_STATUS_SF;
			
			break;

		default:
			break;
		}
	}
	/**
	 * Set slowRewind play, 1/2x, /14x and so on.
	 * @throws NotSupportException, IllegalStateException
	 */
	public void slowRewind() throws NotSupportException, IllegalStateException {
		MtkMediaPlayer.PlayerSpeed eSpeed;

		switch (mPlayStatus) {
		case AudioConst.PLAY_STATUS_SR:
			speedStep <<= 1;

			if (speedStep > 32) {
				speedStep = 1;			
				mtkMediaPlayer.start();
				mPlayStatus = AudioConst.PLAY_STATUS_STARTED;
			} else {
				switch (speedStep) {
				case 2:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_1_2X;
					break;

				case 4:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_1_4X;
					break;

				case 8:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_1_8X;
					break;

				case 16:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_1_16X;
					break;

				case 32:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_1_32X;
					break;

				default:
					eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_1_2X;
					break;
				}
				
				mtkMediaPlayer.setSpeed(eSpeed);
				mPlayStatus = AudioConst.PLAY_STATUS_SR;
			}
			break;

		case AudioConst.PLAY_STATUS_STARTED:
		case AudioConst.PLAY_STATUS_PAUSED:
		case AudioConst.PLAY_STATUS_FF:
		case AudioConst.PLAY_STATUS_FR:
		case AudioConst.PLAY_STATUS_SF:
			
			eSpeed = MtkMediaPlayer.PlayerSpeed.SPEED_FR_1_2X;			
			mtkMediaPlayer.setSpeed(eSpeed);
			speedStep = 2;
			mPlayStatus = AudioConst.PLAY_STATUS_SR;
			
			break;

		default:
			break;
		}
	}
	/**
	 * Pause a audio
	 * 
	 * @throws IllegalStateException
	 */
	public void pause() throws IllegalStateException {

		if (mPlayStatus == AudioConst.PLAY_STATUS_STARTED
				|| mPlayStatus == AudioConst.PLAY_STATUS_FF
				|| mPlayStatus == AudioConst.PLAY_STATUS_FR
				|| mPlayStatus == AudioConst.PLAY_STATUS_SF
				|| mPlayStatus == AudioConst.PLAY_STATUS_SR) {

			if (mPlayMode == AudioConst.PLAYER_MODE_DLNA) {
				DLNADataSource dlnaDataSource = DLNAManager.getInstance()
						.getDLNADataSource(dataSource);
				if (dlnaDataSource != null) {
					if (!dlnaDataSource.getContent().canPause()) {

						// add by shuming fix CR 00386020
						throw new IllegalStateException(
								AudioConst.MSG_ERR_CANNOTPAUSE);
						// return;
						// end

					}
				}
			}

			try {
				mtkMediaPlayer.pause();
			} catch (Exception e) {
				// TODO: handle exception
				throw new IllegalStateException(
						AudioConst.MSG_ERR_PAUSEEXCEPTION);
			}
			mPlayStatus = AudioConst.PLAY_STATUS_PAUSED;
		} else if (mPlayStatus == AudioConst.PLAY_STATUS_PAUSED) {
			mtkMediaPlayer.start();
			mPlayStatus = AudioConst.PLAY_STATUS_STARTED;
		}

	}
	/**
	 * Set data source
	 * 
	 * @param path
	 * @return return true success , return false fail
	 */
	public boolean setDataSource(String path) {
		if (path == null) {
			return false;
		}

		dataSource = path;

		boolean ret = getPcmMetaInfo(dataSource);

		if (ret == false) {
			return false;
		}

		return setDataSource();
	}

	private boolean setDataSource() {

		try {
			mtkMediaPlayer.reset();
			resetListener();
			mtkMediaPlayer.configAudioPlayer();
			mtkMediaPlayer.setDataSource(this);
			
			setFileSize(getFileSize());
			
			if (pcmMode == true) {
				mtkMediaPlayer.setDataSourceMetadata(pcmInfo);
			}

			if (mPlayMode == AudioConst.PLAYER_MODE_DLNA) {

				DLNADataSource dlnaSource = DLNAManager.getInstance()
						.getDLNADataSource(dataSource);

				if (dlnaSource != null) {
					mtkMediaPlayer.setDataSourceSeekEnable(dlnaSource
							.getContent().canSeek());
				}

			}

            if(mPlayMode == AudioConst.PLAYER_MODE_LOCAL){
                mtkMediaPlayer.prepareAsync(MtkMediaPlayer.PROFILE_USB);
            } else if(mPlayMode == AudioConst.PLAYER_MODE_DLNA)
            {
            	mtkMediaPlayer.prepareAsync("DLNA_PULL");
            }else {
                mtkMediaPlayer.prepareAsync();
            }           

			mPlayStatus = AudioConst.PLAY_STATUS_PREPARING;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (NotSupportException e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}
	/**
	 * Set file size to mtkPlayer.
	 * @param size
	 */
    public void setFileSize(long size){   	
    	if (mtkMediaPlayer != null) {
    		mtkMediaPlayer.setMediaSize(size);
    	}
    }
    
    
	/**
	 * Get file size
	 * @return
	 */
	public long getFileSize(){	
		long fileSize = 0;
		String mcurPath = dataSource;
		switch (mPlayMode) {
		case AudioConst.PLAYER_MODE_DLNA: {
			DLNADataSource dlnaSource = DLNAManager.getInstance()
					.getDLNADataSource(mcurPath);
			if (dlnaSource != null) {
				fileSize = dlnaSource.getContent().getSize();	
				MmpTool.LOG_INFO("getAudioFileSize dlna $$$$$$$$$$$$$$" 
						+ fileSize);
			}
		}
			break;
		case AudioConst.PLAYER_MODE_SAMBA: {
			SambaManager sambaManager = SambaManager.getInstance();
			try {
				fileSize = sambaManager.size(mcurPath);
				MmpTool.LOG_INFO("getAudioFileSize samba $$$$$$$$$$$$$$" 
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
		case AudioConst.PLAYER_MODE_LOCAL: {
			MtkFile mFile = null;
			if (mcurPath != null) {
				mFile = new MtkFile(mcurPath);
			}
			MmpTool.LOG_INFO("getAudioFileSize = $$$$$$$$$$$$$$" + mcurPath);

			if (mFile == null) {
				fileSize = 0;
				break;
			}
			fileSize = mFile.getFileSize();
			MmpTool
					.LOG_INFO("getAudioFileSize local $$$$$$$$$$$$$$"
							+ fileSize);
		}
			break;
		case AudioConst.PLAYER_MODE_HTTP:
			break;
		default:
			break;
		}
		
		return fileSize;
	}
	/**
	 * Play a audio
	 * 
	 * @throws IllegalStateException
	 */
	public void play() throws IllegalStateException {
		if (mPlayStatus == AudioConst.PLAY_STATUS_PAUSED
				|| mPlayStatus == AudioConst.PLAY_STATUS_FF
				|| mPlayStatus == AudioConst.PLAY_STATUS_FR
				|| mPlayStatus == AudioConst.PLAY_STATUS_SF
				|| mPlayStatus == AudioConst.PLAY_STATUS_SR) {
			mtkMediaPlayer.start();
			mPlayStatus = AudioConst.PLAY_STATUS_STARTED;
		} else if (mPlayStatus == AudioConst.PLAY_STATUS_STARTED
				|| mPlayStatus == AudioConst.PLAY_STATUS_PREPARING
				|| mPlayStatus == AudioConst.PLAY_STATUS_PREPARED) {
			MmpTool.LOG_DBG("Has played or prepared!");
			return;
		} else if (mPlayStatus == AudioConst.PLAY_STATUS_STOPPED) {
			boolean ret = setDataSource();
			if (ret == false) {
				MmpTool.LOG_ERROR("setDataSource error!");
			}
		} else {
			MmpTool.LOG_ERROR("Please setDataSource firstly!");
		}
	}

	private boolean playNext(boolean auto) throws IllegalStateException {

		int flag;

		flag = auto ? Const.AUTOPLAY : Const.MANUALNEXT;
		String path = PlayList.getPlayList().getNext(Const.FILTER_AUDIO, flag);

		if (path == null) {
			if (mPlayStatus != AudioConst.PLAY_STATUS_COMPLETED) {
				stop();
				if (newCompletionListener != null) {
					newCompletionListener.onCompletion(mtkMediaPlayer, 1, 0);
				}
			}

			return false;
		}

		dataSource = path;
		
		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARING && 
				mPlayStatus <= AudioConst.PLAY_STATUS_PREPARED){
			try {
				cleanListener();
				mtkMediaPlayer.getMetaDataStop();
			} catch (IllegalStateException e) {
				sendMsg(AudioConst.MSG_INVALID_STATE);
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
			
			mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
		}else if ((mPlayStatus >= AudioConst.PLAY_STATUS_STARTED 
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR)
				|| mPlayStatus == AudioConst.PLAY_STATUS_COMPLETED) {			
			try {
				cleanListener();
				mtkMediaPlayer.stop();
			} catch (IllegalStateException e) {
				sendMsg(AudioConst.MSG_INVALID_STATE);
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
			mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
		}

		getPcmMetaInfo(dataSource);
	
		setDataSource();
		
		return true;
	}
	/**
	 * Play next audio
	 * 
	 * @throws IllegalStateException
	 */
	public void playNext() throws IllegalStateException {
		if (mPlayMode == AudioConst.PLAYER_MODE_HTTP) {
			MmpTool.LOG_ERROR("This player mode can't do next!");
			throw new IllegalStateException("Can't do Next!!!");
		} else {
			playNext(false);
		}
	}
	/**
	 * Play previous audio
	 * 
	 * @throws IllegalStateException
	 */
	public void playPrevious() throws IllegalStateException {

		if (mPlayMode == AudioConst.PLAYER_MODE_HTTP) {
			MmpTool.LOG_ERROR("This player mode can't do prev!");
			throw new IllegalStateException("Can't do Prev!!!");
		}

		String path = PlayList.getPlayList().getNext(Const.FILTER_AUDIO,
				Const.MANUALPRE);
		if (path == null) {
			if (mPlayStatus != AudioConst.PLAY_STATUS_COMPLETED) {
				stop();
				if (newCompletionListener != null) {
					newCompletionListener.onCompletion(mtkMediaPlayer, 1, 0);
				}
			}

			return;
		}

		dataSource = path;

		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARING && 
				mPlayStatus <= AudioConst.PLAY_STATUS_PREPARED){
			try {
				cleanListener();
				mtkMediaPlayer.getMetaDataStop();
			} catch (IllegalStateException e) {
				sendMsg(AudioConst.MSG_INVALID_STATE);
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
			
			mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
		}else if ((mPlayStatus >= AudioConst.PLAY_STATUS_STARTED 
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR)
				|| mPlayStatus == AudioConst.PLAY_STATUS_COMPLETED) {			
			try {
				cleanListener();
				mtkMediaPlayer.stop();
			} catch (IllegalStateException e) {
				sendMsg(AudioConst.MSG_INVALID_STATE);
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
			mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
		}

		boolean ret = getPcmMetaInfo(dataSource);

		if (ret == false) {
			return;
		}

		setDataSource();
	}
	/**
	 * Stop a audio
	 * 
	 * @throws IllegalStateException
	 */
	public void stop() throws IllegalStateException {

		cleanListener();
		
		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARING
				&& mPlayStatus <= AudioConst.PLAY_STATUS_PREPARED) {
			try {
				mtkMediaPlayer.getMetaDataStop();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				throw new IllegalStateException(e);
			}

			mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
		} else if ((mPlayStatus >= AudioConst.PLAY_STATUS_STARTED 
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR)) {
			try {
				mtkMediaPlayer.stop();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
			mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
		}
	}
	/**
	 * Stop a audio to close handle when file not support
	 * 
	 * @throws IllegalStateException
	 */
	//add by xudong for fix cr 384293
	public void stopError() throws IllegalStateException {

			stop();
			mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
				
	}
	//end
	/**
	 * Release resource and stop audio play.
	 * 
	 * @throws IllegalStateException
	 */
	public void release() throws IllegalStateException {
		stop();
		closeStream();
		mtkMediaPlayer.release();
		mPlayStatus = AudioConst.PLAY_STATUS_END;
		mtkMediaPlayer = null;
	}
	/**
 	 * Close the stream
 	 */
	public void closeStream(){
		if (null != mInputStream) {
			MmpTool.LOG_DBG ("input stream is not null");
			try {
				mInputStream.close();
			} catch (IOException e) {
				MmpTool.LOG_DBG( "closeStream() close input sream " + e.toString());
			} finally {
				mInputStream = null;
			}
		}
	}
	/**
	 * Get playback progress value
	 * @return long, playback progress
	 */
	public long getPlaybackProgress() {
		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR) {

			long curpos = mtkMediaPlayer.getCurrentPosition();
			return curpos;
		} else {
			return 0;
		}
	}

	/**
	 * Get total playback time
	 * 
	 * @return
	 */
	public long getTotalPlaybackTime() {
		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
			return mtkMediaPlayer.getDuration();
		} else {
			return 0;
		}
	}
	//add for music dlna seek action check
	public boolean canSeekCm(){
		if (mPlayMode == AudioConst.PLAYER_MODE_DLNA) {
			DLNADataSource dlnaDataSource = DLNAManager.getInstance()
					.getDLNADataSource(dataSource);
			if (dlnaDataSource != null) {
				return dlnaDataSource.getContent().canSeek();
			}
		}
		return true;
	}
	/**
	 * Seek to certain time
	 * 
	 * @param time
	 * @return return true if success, return false if fail
	 */
	public boolean seekToCertainTime(long time)throws NotSupportException {

		if (mPlayStatus == AudioConst.PLAY_STATUS_STARTED
				|| mPlayStatus == AudioConst.PLAY_STATUS_PAUSED) {
			if (time < 0) {
				time = 0;
			}
			if (time > mtkMediaPlayer.getDuration()) {
				time = mtkMediaPlayer.getDuration();
			}

			if (mPlayMode == AudioConst.PLAYER_MODE_DLNA) {
				DLNADataSource dlnaDataSource = DLNAManager.getInstance()
						.getDLNADataSource(dataSource);
				if (dlnaDataSource != null) {
					if (!dlnaDataSource.getContent().canSeek()) {
						throw new NotSupportException(AudioConst.NOT_SUPPORT);
					}
				}
			}
			try {
				mtkMediaPlayer.seekTo((int) time);
			} catch (IllegalStateException e) {
				e.printStackTrace();
				return false;
			} catch (NotSupportException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		} else {
			return false;
		}
	}
	/**
	 * Get bit rate for a aduio
	 * @return return 
	 */
	public int getBitRate() {
		// TODO Auto-generated method stub
		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
			AudioTrackInfo info = mtkMediaPlayer.getAudioTrackInfo();
			if (info != null) {
				return info.getBitrate();
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	public int getBitRate(String path) {
		// TODO Auto-generated method stub
		if (path == null) {
			return -1;
		}

		return -1;
	}
	/**
	 * Get sample rate for a aduio
	 * @return return  sample rate
	 */
	public int getSampleRate() {
		// TODO Auto-generated method stub
		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
			AudioTrackInfo info = mtkMediaPlayer.getAudioTrackInfo();
			if (info != null) {
				switch (info.getSampleRate()) {
				case 1:
					return 8000;
				case 2:
					return 16000;
				case 3:
					return 32000;
				case 4:
					return 11000;
				case 5:
					return 22000;
				case 6:
					return 44000;
				case 7:
					return 12000;
				case 8:
					return 24000;
				case 9:
					return 48000;
				case 10:
					return 96000;
				case 11:
					return 192000;
				default:
					return -1;
				}
			} else {
				return -1;
			}
		} else {
			return -1;
		}

	}

	public int getSampleRate(String path) {
		// TODO Auto-generated method stub
		if (path == null) {
			return -1;
		}

		return -1;
	}
	/**
	 *  Get audio codec information.
	 * @return sting, audio codec information
	 */
	public String getAudioCodec() {
		// TODO Auto-generated method stub
		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
			AudioTrackInfo info = mtkMediaPlayer.getAudioTrackInfo();
			if (info != null) {
				switch (info.getAudioCodec()) {
				case 0:
					return "UNKNOWN";
				case 1:
					return "MPEG";
				case 2:
					return "MP3";
				case 3:
					return "AAC";
				case 4:
					return "DD";
				case 5:
					return "TRUEHD";
				case 6:
					return "PCM";
				case 7:
					return "DTS";
				case 8:
					return "DTS_HD_HR";
				case 9:
					return "DTS_HD_MA";
				case 10:
					return "WMA";
				case 11:
					return "COOK";
				case 12:
					return "VORBIS";
				case 13:
					return "FLAC";
				case 14:
					return "MONKEY";
				default:
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public String getAudioCodec(String path) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 *  Get channel number for a audio
	 * @return channel number
	 */
	public String getChannelNumber() {
		// TODO Auto-generated method stub
		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
			AudioTrackInfo info = mtkMediaPlayer.getAudioTrackInfo();
			if (info != null) {
				switch (info.getChannelNumber()) {
				case 1:
					return "MONO";
				case 2:
					return "STEREO";
				case 3:
					return "SURROUND_TWO_CH";
				case 4:
					return "SURROUND";
				case 5:
					return "THREE_ZERO";
				case 6:
					return "FOUR_ZERO";
				case 7:
					return "FIVE_ZERO";
				case 8:
					return "FIVE_ONE";
				case 9:
					return "SEVEN_ONE";
				default:
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public String getChannelNumber(String path) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Get file title for audio
	 * 
	 * @return music title
	 */
	public String getMusicTitle() {
		String title = "";
		StringBuilder temp = new StringBuilder();
		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
			MetaDataInfo metaDataInfo = mtkMediaPlayer
					.getMetaDataInfoInstance(mtkMediaPlayer);
			short titleASC2[] = metaDataInfo.getTitle();
			for (int i = 0; i < titleASC2.length; i++) {
				if (titleASC2[i] == 0) {
					break;
				}
				temp.append((char) titleASC2[i]);
			}
			title = temp.toString();
		}

		MmpTool.LOG_DBG(title);
		return title;
	}
	/**
	 * Get artist of a audio.
	 * 
	 * @return music artist
	 */
	public String getMusicArtist() {
		String artist = "";
		StringBuilder temp = new StringBuilder();
		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
			MetaDataInfo metaDataInfo = mtkMediaPlayer
					.getMetaDataInfoInstance(mtkMediaPlayer);
			short artistASC2[] = metaDataInfo.getArtist();
			for (int i = 0; i < artistASC2.length; i++) {
				if (artistASC2[i] == 0) {
					break;
				}
				temp.append((char) artistASC2[i]);
			}
			artist = temp.toString();
		}

		MmpTool.LOG_DBG(artist);
		return artist;
	}
	/**
	 * Get album of a audio.
	 * 
	 * @return music album
	 */
	public String getMusicAlbum() {
		String album = "";
		StringBuilder temp = new StringBuilder();
		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
			MetaDataInfo metaDataInfo = mtkMediaPlayer
					.getMetaDataInfoInstance(mtkMediaPlayer);
			short albumASC2[] = metaDataInfo.getAlbum();
			for (int i = 0; i < albumASC2.length; i++) {
				if (albumASC2[i] == 0) {
					break;
				}
				temp.append((char) albumASC2[i]);
			}
			album = temp.toString();
		}

		MmpTool.LOG_DBG(album);
		return album;
	}
	/**
	 * Get genre of a audio.
	 * 
	 * @return music genre
	 */
	public String getMusicGenre() {
		String genre = "";
		StringBuilder temp = new StringBuilder();
		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
			MetaDataInfo metaDataInfo = mtkMediaPlayer
					.getMetaDataInfoInstance(mtkMediaPlayer);
			short genreASC2[] = metaDataInfo.getGenre();
			for (int i = 0; i < genreASC2.length; i++) {
				if (genreASC2[i] == 0) {
					break;
				}
				temp.append((char) genreASC2[i]);
			}
			genre = temp.toString();
		}

		MmpTool.LOG_DBG(genre);
		return genre;
	}
	/**
	 * Get music year
	 * 
	 * @return music year
	 */
	public String getMusicYear() {
		String year = "";
		StringBuilder temp = new StringBuilder();
		if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
				&& mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
			MetaDataInfo metaDataInfo = mtkMediaPlayer
					.getMetaDataInfoInstance(mtkMediaPlayer);
			short yearASC2[] = metaDataInfo.getYear();
			for (int i = 0; i < yearASC2.length; i++) {
				if (yearASC2[i] == 0) {
					break;
				}
				temp.append((char) yearASC2[i]);
			}
			year = temp.toString(); 
		}

		MmpTool.LOG_DBG(year);
		return year;
	}
	/**
	 * Get current play status.
	 * 
	 * @return current play status.
	 */	
	public int getPlayStatus() {
		return mPlayStatus;
	}
	/**
	 * Register a listener to notify a aduio play complete.
	 * @param completionListener
	 * 
	 */	
	public void registerAudioCompletionListener(Object completionListener) {
		newCompletionListener = (MtkMediaPlayer.OnCompletionListener) completionListener;
	}
	/**
	 * Unregister a listener to notify a aduio play complete.
	 * 
	 * 
	 */	
	public void unregisterAudioCompletionListener() {
		newCompletionListener = null;
	}
	/**
	 * Register a listener to notify a aduio prepare play.
	 * @param completionListener
	 * 
	 */	
	public void registerAudioPreparedListener(Object preparedListener) {
		newPreparedListener = (MtkMediaPlayer.OnPreparedListener) preparedListener;
	}
	/**
	 * Unregister a listener to notify a aduio prepare play.
	 * 
	 * 
	 */	
	public void unregisterAudioPreparedListener() {
		newPreparedListener = null;
	}
	/**
	 * Register a listener to notify playing audio occur error.
	 * @param errorListener
	 * 
	 */	
	public void registerAudioErrorListener(Object errorListener) {
		newErrorListener = (MtkMediaPlayer.OnErrorListener) errorListener;
	}
	/**
	 * Unregister a listener to notify playing audio occur error.
	 * 
	 * 
	 */	
	public void unregisterAudioErrorListener() {
		newErrorListener = null;
	}
	/**
	 * Register a listener to notify a aduio play duration update.
	 * @param updateListener
	 * 
	 */	
	public void registerAudioDurationUpdateListener(Object updateListener) {
		newTotalTimeListener = (MtkMediaPlayer.OnTotalTimeUpdateListener) updateListener;
	}

	/**
	 * Unregister a listener to notify a aduio play duration update.
	 * 
	 * 
	 */	
	public void unregisterAudioDurationUpdateListener() {
		newTotalTimeListener = null;
	}
	
	/**
	 * Register a listener to notify a aduio play speed update.
	 * @param speedUpdateListener
	 * 
	 */	
	public void registerAuiodSpeedUpdateListener(Object speedUpdateListener){
		newSpeedUpdateListener = (MtkMediaPlayer.OnSpeedUpdateListener)speedUpdateListener;
	}
	/**
	 * Unregister a listener to notify a aduio play speed update.
	 * 
	 * 
	 */	
	public void unregisterAudioSpeedUpdateListener(){
		newSpeedUpdateListener = null;
	}
	/**
	 * Register a listener to notify a audio replay.
	 * @param replayListener
	 */
	public void registerAduioReplayListener(Object replayListener){
		newReplayListener = (OnRePlayListener)replayListener;
	}
	/**
	 * Unregister a listener to notify a aduio replay.
	 */
	public void unregisterAudioReplayListener(){
		newReplayListener = null;
	}
	/**
	 * Register a listener to notify a aduio file eof error.
	 * @param eofListener
	 * 
	 */	
	public void registerAudioEofListener(Object eofListener) {
		newEofListener = (MtkMediaPlayer.OnEofListener) eofListener;
	}
	/**
	 * Unregister a listener to notify a aduio file eof error.
	 * 
	 * 
	 */	
	public void unregisterAudioEofListener() {
		newEofListener = null;
	}

/*	private class MyThread extends Thread {

		public void run() {
			try {
				boolean state = playNext(true);
				int flag = 0;

				if (state == false) {

					if (mPlayStatus != AudioConst.PLAY_STATUS_STOPPED) {
						mtkMediaPlayer.stop();
						mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
					}
					flag = 1;
				}

				if (newCompletionListener != null) {
					newCompletionListener.onCompletion(mtkMediaPlayer, flag, 0);
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/

	private MtkMediaPlayer.OnCompletionListener newCompletionListener = null;
	private MtkMediaPlayer.OnCompletionListener completionListener = new MtkMediaPlayer.OnCompletionListener() {
		public boolean onCompletion(MtkMediaPlayer mp, int what, int extra) {

			mPlayStatus = AudioConst.PLAY_STATUS_COMPLETED;
			//new MyThread().start();
			sendMsg(AudioConst.MSG_SOURCE_COMPLETE);
			return true;
		}
	};

	private void sendMsg(int msg){
        Message m = Message.obtain();
        m.what = msg;
        audioMsgHandler.sendMessage(m);
	}
	
	private Handler audioMsgHandler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == AudioConst.MSG_SOURCE_PREPARE) {
				if (pcmMode == true) {
					mtkMediaPlayer.setPcmMediaInfo(pcmInfo);
				}

				try {
					mtkMediaPlayer.start();
					mPlayStatus = AudioConst.PLAY_STATUS_STARTED;

					if (newPreparedListener != null) {
						newPreparedListener.onPrepared(mtkMediaPlayer);
					}
				} catch (IllegalStateException e) {
					e.printStackTrace();
					return;
				}
			}  else if (msg.what == AudioConst.MSG_SOURCE_COMPLETE){

				try {
					boolean state = playNext(true);
					int flag = 0;

					if (state == false) {

						if (mPlayStatus != AudioConst.PLAY_STATUS_STOPPED) {
							mtkMediaPlayer.stop();
							mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
						}
						flag = 1;
					}
					if (newCompletionListener != null) {
						newCompletionListener.onCompletion(mtkMediaPlayer, flag, 0);
					}
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}else {
				try {
					stop();
					mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
				} catch (IllegalStateException e) {
					e.printStackTrace();
					return;
				}
				if (newErrorListener != null) {
					newErrorListener.onError(mtkMediaPlayer, msg.what, 0);
				}

			}
		}

	};

	private MtkMediaPlayer.OnErrorListener newErrorListener = null;
	private MtkMediaPlayer.OnErrorListener errorListener = new MtkMediaPlayer.OnErrorListener() {
		public boolean onError(MtkMediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			MmpTool.LOG_DBG("what = " + what);

			sendMsg(AudioConst.MSG_FILE_NOT_SUPPORT);
			/*switch (what) {
			case 1:
				sendMsg(AudioConst.MSG_AUDIO_NOT_SUPPORT);
				break;
			case 2:
				sendMsg(AudioConst.MSG_AUDIO_NOT_SUPPORT);
				break;
			case 3:
				sendMsg(AudioConst.MSG_AUDIO_NOT_SUPPORT);
				break;
			case 4:
				sendMsg(AudioConst.MSG_FILE_NOT_SUPPORT);
				break;
			case 8:
				sendMsg(AudioConst.MSG_FILE_CORRUPT);
				break;
			default:
				sendMsg(AudioConst.MSG_FILE_NOT_SUPPORT);
				break;
			}
*/
			return true;
		}
	};

	private MtkMediaPlayer.OnPreparedListener newPreparedListener = null;
	private MtkMediaPlayer.OnPreparedListener preparedListener = new MtkMediaPlayer.OnPreparedListener() {
		public void onPrepared(MtkMediaPlayer mp) {
			// TODO Auto-generated method stub
			mPlayStatus = AudioConst.PLAY_STATUS_PREPARED;
			MmpTool.LOG_DBG("onPrepared........... = ");
			sendMsg(AudioConst.MSG_SOURCE_PREPARE);
		}
	};

	private MtkMediaPlayer.OnTotalTimeUpdateListener newTotalTimeListener = null;
	private MtkMediaPlayer.OnTotalTimeUpdateListener totalTimeListener = new MtkMediaPlayer.OnTotalTimeUpdateListener() {

		public void onTotalTimeUpdate(MtkMediaPlayer arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			if (newTotalTimeListener != null) {
				newTotalTimeListener.onTotalTimeUpdate(arg0, arg1, arg2);
			}
		}
	};

	private MtkMediaPlayer.OnSpeedUpdateListener newSpeedUpdateListener;
	private MtkMediaPlayer.OnSpeedUpdateListener speedUpdateListener = new MtkMediaPlayer.OnSpeedUpdateListener(){

		public void onSpeedUpdate(MtkMediaPlayer arg0, int speed, int arg2) {
		/*marked by lei, and to do at OnReplay listener*/
//			if (newSpeedUpdateListener != null) {
//				newSpeedUpdateListener.onSpeedUpdate(arg0, speed, arg2);
//				if (mPlayStatus == AudioConst.PLAY_STATUS_FR) {
//					long progress = getPlaybackProgress();
//					if (speed == 100000 && progress >= 0 && progress <= 10) {
//						mPlayStatus = AudioConst.PLAY_STATUS_STARTED;
//					}
//				}
//			}
			
		}
		
	};
	
	private MtkMediaPlayer.OnEofListener newEofListener;
	private MtkMediaPlayer.OnEofListener eofListener = new MtkMediaPlayer.OnEofListener() {

		public void onEof(MtkMediaPlayer arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			if (newEofListener != null) {
				newEofListener.onEof(arg0, arg1, arg2);
			}
		}
	};

	/*The function was called when replay.*/
	private OnRePlayListener newReplayListener;
	private OnRePlayListener replayListener = new OnRePlayListener() {
		
		public void onRePlay(MtkMediaPlayer arg0, int arg1, int arg2) {
			newReplayListener.onRePlay(arg0, arg1, arg2);
			mPlayStatus = AudioConst.PLAY_STATUS_STARTED;
		}
	};
	/**
	 * Get file extend information.
	 * @param file
	 * @return
	 * @SuppressWarnings("unused")
	 */
	private String getFileExtension(String name) {
		int lastIndexOfDot = name.lastIndexOf('.');
		int fileLength = name.length();

		return name.substring(lastIndexOfDot + 1, fileLength);
	}

	private boolean getPcmMetaInfo(String path) {

		if (mPlayMode != AudioConst.PLAYER_MODE_DLNA) {
			pcmInfo = null;
			if (pcmMode == true) {
				mtkMediaPlayer.setDataSourceMetadata(pcmInfo);
			}
			pcmMode = false;
		} else {
			DLNADataSource dlnaDataSource = DLNAManager.getInstance()
					.getDLNADataSource(path);

			if (dlnaDataSource == null) {
				pcmMode = false;
				return false;
			}

			if (dlnaDataSource.getContent().getMimeType().equals(
					FileSuffixConst.DLNA_MEDIA_MIME_TYPE_AUDIO_L16)) {

				pcmMode = true;

				MmpTool.LOG_DBG("pcm duration = "
						+ dlnaDataSource.getContent().getResDuration());

				String resDur = dlnaDataSource.getContent().getResDuration();
				int duration = 0;
				if (resDur != null){
					String[] time = resDur.split(":");

				MmpTool.LOG_DBG("pcm time = " + time[0]);
				MmpTool.LOG_DBG("pcm time = " + time[1]);
				MmpTool.LOG_DBG("pcm time = " + time[2]);

				int hour = Integer.parseInt(time[0]);
				int m = Integer.parseInt(time[1]);
				float s = Float.parseFloat(time[2]) * 1000;

				int second = Math.round(s);

				MmpTool.LOG_DBG("pcm time = " + hour);
				MmpTool.LOG_DBG("pcm time = " + m);
				MmpTool.LOG_DBG("pcm time = " + second);

					duration = (hour * 60 + m) * 60 * 1000 + second;
				MmpTool.LOG_DBG("pcm duration = " + duration);
				}
				long size = dlnaDataSource.getContent().getSize();
				MmpTool.LOG_DBG("pcm size = " + size);

				int type = PcmMediaInfo.AudioPcmInfo.AUD_PCM_TYPE_NORMAL;
				int channelNum = 0;
				String nac = dlnaDataSource.getContent().getNrAudioChannels();
				if (nac != null){
					channelNum = Integer.valueOf(nac);
				}
				MmpTool.LOG_DBG("pcm channelNum = " + channelNum);
				int sampleRate = 0;
				String sf = dlnaDataSource.getContent().getSampleFrequency();
				if (sf != null){
					sampleRate = Integer.valueOf(sf) / 1000;
					sampleRate = mapSampleRate(sampleRate * 1000);
				}
				MmpTool.LOG_DBG("pcm sampleRate = " + sampleRate);
				short blockAlign = 0;

				int bitsPerSample;
				String bits = dlnaDataSource.getContent().getBitsPerSample();
				if (bits == null) {
					bitsPerSample = 0;
				} else {
					bitsPerSample = Integer.valueOf(bits);
				}
				MmpTool.LOG_DBG("pcm bitsPerSample = " + bitsPerSample);
				int bigEndian = 1;

				pcmInfo = new PcmMediaInfo(duration, size, 0);

				PcmMediaInfo.AudioPcmInfo audInfo = pcmInfo.new AudioPcmInfo(
						type, channelNum, sampleRate, blockAlign,
						bitsPerSample, bigEndian);

				pcmInfo.setAudioPcmInfo(audInfo);
			} else {

				pcmInfo = null;
				if (pcmMode == true) {
					mtkMediaPlayer.setDataSourceMetadata(pcmInfo);
				}
				pcmMode = false;
			}
		}
		MmpTool.LOG_DBG("pcmMode = " + pcmMode);
		return true;
	}

	// map sampleRate
	private int mapSampleRate(int sampleRate) {
		switch (sampleRate) {
		case 8000:
			sampleRate = PcmMediaInfo.AudioPcmInfo.AUD_SAMPLE_RATE_8K;
			break;
		case 16000:
			sampleRate = PcmMediaInfo.AudioPcmInfo.AUD_SAMPLE_RATE_16K;
			break;
		case 32000:
			sampleRate = PcmMediaInfo.AudioPcmInfo.AUD_SAMPLE_RATE_32K;
			break;
		case 11000:
			sampleRate = PcmMediaInfo.AudioPcmInfo.AUD_SAMPLE_RATE_11K;
			break;
		case 22000:
			sampleRate = PcmMediaInfo.AudioPcmInfo.AUD_SAMPLE_RATE_22K;
			break;
		case 44000:
			sampleRate = PcmMediaInfo.AudioPcmInfo.AUD_SAMPLE_RATE_44K;
			break;
		case 12000:
			sampleRate = PcmMediaInfo.AudioPcmInfo.AUD_SAMPLE_RATE_12K;
			break;
		case 24000:
			sampleRate = PcmMediaInfo.AudioPcmInfo.AUD_SAMPLE_RATE_24K;
			break;
		case 48000:
			sampleRate = PcmMediaInfo.AudioPcmInfo.AUD_SAMPLE_RATE_48K;
			break;
		case 96000:
			sampleRate = PcmMediaInfo.AudioPcmInfo.AUD_SAMPLE_RATE_96K;
			break;
		case 192000:
			sampleRate = PcmMediaInfo.AudioPcmInfo.AUD_SAMPLE_RATE_192K;
			break;

		default:
			Log.e("MtkPlayback", "SampleRate is error!!! sampleRate==="
					+ sampleRate);
			sampleRate = PcmMediaInfo.AudioPcmInfo.AUD_SAMPLE_RATE_48K;
			break;
		}
		return sampleRate;
	}
}
