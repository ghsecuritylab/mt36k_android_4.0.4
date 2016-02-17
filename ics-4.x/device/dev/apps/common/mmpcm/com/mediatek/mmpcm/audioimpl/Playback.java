package com.mediatek.mmpcm.audioimpl;

import java.io.File;
import java.io.IOException;
import android.media.MediaPlayer;

import com.mediatek.media.NotSupportException;
import com.mediatek.mmpcm.MmpTool;
import com.mediatek.mmpcm.audio.IPlayback;
import com.mediatek.mmpcm.mmcimpl.Const;
import com.mediatek.mmpcm.mmcimpl.PlayList;

public class Playback implements IPlayback {

    private MediaPlayer mPlayer;
    private String dataSource;
    private int mPlayStatus = AudioConst.PLAY_STATUS_UNKNOW;
    private int mPlayMode = AudioConst.PLAYER_MODE_LOCAL;

    Playback() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(completionListener);
        mPlayer.setOnErrorListener(errorListener);
        mPlayer.setOnPreparedListener(preparedListener);
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

    public void fastForward() {
        // TODO Auto-generated method stub
        throw new NotSupportException();
    }

    public void fastRewind() {
        // TODO Auto-generated method stub
        throw new NotSupportException();
    }

    public void slowForward() {
        // TODO Auto-generated method stub
        throw new NotSupportException();
    }

    public void slowRewind() {
        // TODO Auto-generated method stub
        throw new NotSupportException();
    }

    public int getSpeed() {
        return -1;
    }

    public void setSpeed(int speed){
    	
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
            mPlayer.pause();
            mPlayStatus = AudioConst.PLAY_STATUS_PAUSED;
        } else if (mPlayStatus == AudioConst.PLAY_STATUS_PAUSED) {
            mPlayer.start();
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

        try {
            mPlayer.reset();

            mPlayer.setDataSource(path);

            mPlayer.prepareAsync();

            mPlayStatus = AudioConst.PLAY_STATUS_PREPARING;
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            // TODO: notify the user why the file couldn't be opened
            e.printStackTrace();
            return false;
        }

        return true;

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
            mPlayer.start();
            mPlayStatus = AudioConst.PLAY_STATUS_STARTED;
        } else if (mPlayStatus == AudioConst.PLAY_STATUS_STARTED
                || mPlayStatus == AudioConst.PLAY_STATUS_PREPARING
                || mPlayStatus == AudioConst.PLAY_STATUS_PREPARED) {
            MmpTool.LOG_DBG("Has played or prepared!");
            return;
        } else if (mPlayStatus == AudioConst.PLAY_STATUS_STOPPED) {
            boolean ret = setDataSource(dataSource);
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
                    newCompletionListener.onCompletion(mPlayer);
                }
            }

            return false;
        }

        if (mPlayStatus >= AudioConst.PLAY_STATUS_STARTED
                && mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
            mPlayer.stop();
            mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
        }

        return setDataSource(path);
    }
    /**
	 * Play next a audio
	 * 
	 * @throws IllegalStateException
	 */
    public void playNext() throws IllegalStateException {

        if (mPlayMode != AudioConst.PLAYER_MODE_LOCAL) {
            MmpTool.LOG_ERROR("This player mode can't do next!");
            throw new IllegalStateException("Can't do Next!!!");
        } else {
            playNext(false);
        }

    }
    /**
	 * Play previous a audio
	 * 
	 * @throws IllegalStateException
	 */
    public void playPrevious() throws IllegalStateException {

        if (mPlayMode != AudioConst.PLAYER_MODE_LOCAL) {
            MmpTool.LOG_ERROR("This player mode can't do next!");
            throw new IllegalStateException("Can't do Next!!!");
        }

        String path = PlayList.getPlayList().getNext(Const.FILTER_AUDIO,
                Const.MANUALPRE);
        if (path == null) {
            if (mPlayStatus != AudioConst.PLAY_STATUS_COMPLETED) {
                stop();
                if (newCompletionListener != null) {
                    newCompletionListener.onCompletion(mPlayer);
                }
            }
            return;
        }

        if (mPlayStatus >= AudioConst.PLAY_STATUS_STARTED
                && mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
            mPlayer.stop();
            mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
        }

        setDataSource(path);

    }

	/**
	 * Stop audio
	 * 
	 * @throws IllegalStateException
	 */
    public void stop() throws IllegalStateException {

        if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
                && mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
            mPlayer.stop();
            mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
        }

    }
    /**
	 * Release audio
	 * 
	 * @throws IllegalStateException
	 */
    public void release() throws IllegalStateException {

        stop();
        mPlayer.release();
        mPlayStatus = AudioConst.PLAY_STATUS_END;
        mPlayer = null;
    }

    public long getPlaybackProgress() {

        if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
                && mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
            return mPlayer.getCurrentPosition();
        } else {
            return 0;
        }

    }

    public long getTotalPlaybackTime() {

        if (mPlayStatus >= AudioConst.PLAY_STATUS_PREPARED
                && mPlayStatus <= AudioConst.PLAY_STATUS_SR) {
            return mPlayer.getDuration();
        } else {
            return 0;
        }

    }
    /**
     * Seek to  certain time
     * @param time
     * @return return true if success, return false if fail
     */
    public boolean seekToCertainTime(long time) {

        if (mPlayStatus == AudioConst.PLAY_STATUS_STARTED
                || mPlayStatus == AudioConst.PLAY_STATUS_PAUSED) {
            if (time < 0) {
                time = 0;
            }
            if (time > mPlayer.getDuration()) {
                time = mPlayer.getDuration();
            }

            try {
                mPlayer.seekTo((int) time);
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

    public int getBitRate() {
        // TODO Auto-generated method stub
        return -1;
    }

    public int getBitRate(String path) {
        // TODO Auto-generated method stub
        if (path == null) {
            return -1;
        }

        return -1;
    }

    public int getSampleRate() {
        // TODO Auto-generated method stub
        return -1;
    }

    public int getSampleRate(String path) {
        // TODO Auto-generated method stub
        if (path == null) {
            return -1;
        }

        return -1;
    }

    public String getAudioCodec() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getAudioCodec(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getChannelNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getChannelNumber(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getMusicTitle() {
        return null;
    }

    public String getMusicArtist() {
        return null;
    }

    public String getMusicAlbum() {
        return null;
    }

    public String getMusicGenre() {
        return null;
    }

    public String getMusicYear() {
        return null;
    }

    public int getPlayStatus() {
        return mPlayStatus;
    }

    public void setFileSize(long size){
    	
    }
    
    public long getFileSize(){
    	return 0;
    }
    
    public void registerAudioCompletionListener(Object completionListener) {
        newCompletionListener = (MediaPlayer.OnCompletionListener) completionListener;
    }

    public void unregisterAudioCompletionListener() {
        newCompletionListener = null;
    }

    public void registerAudioPreparedListener(Object preparedListener) {
        newPreparedListener = (MediaPlayer.OnPreparedListener) preparedListener;
    }

    public void unregisterAudioPreparedListener() {
        newPreparedListener = null;
    }

    public void registerAudioErrorListener(Object errorListener) {
        newErrorListener = (MediaPlayer.OnErrorListener) errorListener;
    }

    public void unregisterAudioErrorListener() {
        newErrorListener = null;
    }

    public void registerAudioDurationUpdateListener(Object updateListener) {

    }

    public void unregisterAudioDurationUpdateListener() {

    }
    
    public void registerAudioEofListener(Object eofListener) {
        
    }

    public void unregisterAudioEofListener() {
        
    }
    
    public void registerAuiodSpeedUpdateListener(Object speedUpdateListener){
    	
    }
    private MediaPlayer.OnCompletionListener newCompletionListener = null;
    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

        public void onCompletion(MediaPlayer mp) {
            mPlayStatus = AudioConst.PLAY_STATUS_COMPLETED;
            boolean state = playNext(true);
            if (newCompletionListener != null && state == false) {
                newCompletionListener.onCompletion(mp);
            }
        }
    };

    private MediaPlayer.OnPreparedListener newPreparedListener = null;
    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            mPlayStatus = AudioConst.PLAY_STATUS_PREPARED;

            try {
                mp.start();
                mPlayStatus = AudioConst.PLAY_STATUS_STARTED;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return;
            }

            if (newPreparedListener != null) {
                newPreparedListener.onPrepared(mp);
            }
        }
    };

    private MediaPlayer.OnErrorListener newErrorListener = null;
    private MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {

        public boolean onError(MediaPlayer mp, int what, int extra) {
            try {
                stop();
                mPlayStatus = AudioConst.PLAY_STATUS_STOPPED;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return false;
            }

            if (newErrorListener != null) {
                return newErrorListener.onError(mp, what, extra);
            }

            return true;
        }
    };

	public void stopError() throws IllegalStateException {
		// TODO Auto-generated method stub
		
	}
	
	public void registerAduioReplayListener(Object replayListener) {
		// TODO Auto-generated method stub
		
	}

	public void unregisterAudioReplayListener() {
		// TODO Auto-generated method stub
		
	}

	public void unregisterAudioSpeedUpdateListener() {
		// TODO Auto-generated method stub
		
	}

	public boolean canSeekCm() {
		// TODO Auto-generated method stub
		return false;
	}
}
