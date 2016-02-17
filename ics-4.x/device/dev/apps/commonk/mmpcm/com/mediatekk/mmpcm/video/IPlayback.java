package com.mediatekk.mmpcm.video;

import java.io.IOException;

import android.graphics.Rect;

import com.mediatek.media.NotSupportException;
import com.mediatek.media.VideoInfo;
import com.mediatek.media.VideoTrackInfo;
import com.mediatekk.mmpcm.videoimpl.Subtitle;
import com.mediatekk.mmpcm.videoimpl.VidMediaInfo;

public interface IPlayback {
    /**
     * Interface definition for a callback to be invoked when playback of a
     * media source has completed
     * 
     * @author MTK94044
     * 
     */
    public interface OnPBCompleteListener {
        /**
         * Called when the end of a media source is reached during playback
         * 
         * @param pb
         *            the playback that reached the end of the file
         */
        void onComplete(IPlayback pb);
    }

    /**
     * Register a callback to be invoked when the end of a media source has been
     * reached during playback.
     * 
     * @param listener
     *            the callback that will be run
     */
    public void setOnPBCompleteListener(OnPBCompleteListener listener);

    /**
     * Interface definition for a callback to be invoked when the media source
     * is ready for playback.
     */
    public interface OnPBPreparedListener {
        /**
         * Called when the media file is ready for playback.
         * 
         * @param mp
         *            the player that is ready for playback
         */
        void onPrepared(IPlayback pb);
    }

    /**
     * Register a callback to be invoked when the media source is ready for
     * playback.
     * 
     * @param listener
     *            the callback that will be run
     */
    public void setOnPBPreparedListener(OnPBPreparedListener listener);

    /**
     * Interface definition for a callback to be invoked when the error
     * happened.
     */
    public interface OnPBMsgListener {
        /**
         * Called when the error happened.
         * 
         * @param mp
         *            the player
         */
        void onMsg(int msg);
    }

    /**
     * Register a callback to be invoked when the error happened
     * 
     * @param listener
     *            the callback that will be run
     */
    public void setOnPBMsgListener(OnPBMsgListener listener);

    /**
     * Interface definition for a callback to be invoked when buffer update.
     */
    public interface OnPBBufferUpdateListener {
        /**
         * Called when buffer update.
         * 
         * @param mp
         *            the player
         */
        void onBufferUpdate(IPlayback pb);
    }

    /**
     * Register a callback to be invoked when the buffer update
     * 
     * @param listener
     *            the callback that will be run
     */
    public void setOnPBBufferUpdateListener(OnPBBufferUpdateListener listener);

    /**
     * Interface definition for a callback to be invoked when the media source
     * is ready for playback.
     */
    public interface OnPBPlayDoneListener {
        /**
         * Called when the media file is ready for playback.
         * 
         * @param mp
         *            the player that is ready for playback
         */
        void onPlayDone(IPlayback pb);
    }

    /**
     * Register a callback to be invoked when the media source is ready for
     * playback.
     * 
     * @param listener
     *            the callback that will be run
     */
    public void setOnPBPlayDoneListener(OnPBPlayDoneListener listener);

    /**
     * Time update listener
     * @author hs_xiaojieguo
     *
     */
    public interface OnPBDurarionUpdateListener {
        void onUpdate(IPlayback pb, int duration);
    }

    public void setOnPBDurarionUpdateListener(
            OnPBDurarionUpdateListener listener);

    public interface OnPBEofListener {
        void onEof(IPlayback pb, int enevt);
    }

    public void setOnPBEofListener(OnPBEofListener listener);

    public int getPlayerMode();

    /**
     * set player mode
     * 
     * @param mode
     *            mmp mode or net mode
     * @param void
     * 
     * @return void
     */
    public void setPlayerMode(int mode);

    /**
     * set data source
     * 
     * @param path
     *            path
     * @param void
     * 
     * @return void
     */
    public void setDataSource(String path) throws IOException,
            IllegalArgumentException, IllegalStateException;

    /**
     * play some video file
     * 
     * @param void void
     * @param void
     * 
     * @return void
     */
    public void play() throws IllegalStateException;

    /**
     * paused the played file
     * 
     * @param void void
     * @param void
     * 
     * @return void
     */
    public void pause() throws IllegalStateException;

    /**
     * stop the played file
     * 
     * @param void void
     * @param void
     * 
     * @return void
     */
    public void stop() throws IllegalStateException;

    /**
     * seek to special postion
     * 
     * @param msec
     *            the time user want to seek to
     * @param void
     * 
     * @return void
     */
    public void seek(int msec) throws IllegalStateException;

    /**
     * next file
     * 
     * @param videoPath
     *            the file path
     * @param void
     * 
     * @return void
     */
    public void manualNext() throws IllegalStateException;

    /**
     * prev file
     * 
     * @param videoPath
     *            the file path
     * @param void
     * 
     * @return void
     */
    public void manualPrev() throws IllegalStateException;

    /**
     * fast forward
     * 
     * @param void void
     * @param void
     * 
     * @return void
     */
    public void fastForward() throws IllegalStateException, NotSupportException;

    /**
     * fast rewind
     * 
     * @param void void
     * @param void
     * 
     * @return void
     */
    public void fastRewind() throws IllegalStateException, NotSupportException;

    /**
     * slow forward
     * 
     * @param void void
     * @param void
     * 
     * @return void
     */
    public void slowForward() throws IllegalStateException, NotSupportException;

    /**
     * get video duration
     * 
     * @param void void
     * @param void
     * 
     * @return total time ,ms
     */
    public int getDuration();

    /**
     * get current time
     * 
     * @param void void
     * @param void
     * 
     * @return current time,ms
     */
    public int getProgress();

    /**
     * get play status
     * 
     * @param void void
     * @param void
     * 
     * @return current status,like PLAY_STATUS_STARTED
     */
    public int getPlayStatus();

    /**
     * get current file name
     * 
     * @param void void
     * @param void
     * 
     * @return string
     */
    public String getCurFileName();

    /**
     * release all resource when exit video play
     * 
     * @param void void
     * @param void
     * 
     * @return void
     */
    public void onRelease();

    /**
     * if need to end video play,return true.
     * 
     * @param void void
     * @param void
     * 
     * @return boolean
     */
    public boolean isEnd();

    /**
     * get FF/FR/SF speed
     * 
     * @param void void
     * @param void
     * 
     * @return int,like 2,4,8,16,32
     */
    public int getSpeed();

    /**
     * step frame play,must pasue firstly
     * 
     * @param void void
     * @param void
     * 
     * @return void
     */
    public void step() throws IllegalStateException;

    /**
     * switch audio audio track
     * 
     * @param short mtsIdx,audio track index
     * @param void
     * 
     * @return void
     */
    public void selectMts(short mtsIdx);

    /**
     * get media info
     * 
     * @param void
     * 
     * @param void
     * 
     * @return VidMediaInfo
     */
    public VidMediaInfo getMediaInfo();

    public void setPreviewMode(boolean preview);

    public void reset();

    public byte getProgramCount();

    public void setProgram(short index);

    public void setSubtitleTrack(short index);

    public void setSubDefaultType();

    public void setSubDisplayMode(int mode);

    public void setSubHighlightStyle(int highlightStyle);

    public void setSubTimeOffset(int offsetMode, int offsetValue);

    public void setSubFontEncode(int encodeMode);

    public void setSubOnOff(boolean on);
    
    public void setSubShowHide(boolean on);

    public void setSubFontInfo(Subtitle.FontInfo info);
    
    public void setSubFontSize(int size);
    
    public void setSubFontCustomSize(byte customSize);

    public void setSubBgColor(Subtitle.Color color);

    public void setSubTextColor(Subtitle.Color color);
    
    public void setSubEdgeColor(Subtitle.Color color);

    public void setSubBoderType(int type);

    public void setSubBoderWidth(int width);

    public void setSubRollType(int type);

    public void setSubDisplayRect(Rect dispRect);
    
    public void setSubDisplayOffset(int x,int y);

    public String getVideoTitle();

    public String getVideoDirector();

    public String getVideoCopyright();

    public String getVideoGenre();

    public String getVideoYear();
    
    public void setFileSize(long size);
    
    public long getCurFilePosition();
    
    public long getVideoFileSize();
    
    public void replay();
    
    public VideoTrackInfo getVideoTrackInfo();
    
    public VideoInfo getVideoInfo();
    
  //add by shuming fix CR00385698
    public  void setFeaturenotsurport(boolean featurenotsurport);
    
    public boolean isFeaturenotsurport();
    
    public boolean canDoSeek();
    //end
}
