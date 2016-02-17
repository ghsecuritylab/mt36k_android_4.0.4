package com.mediatek.mmpcm.videoimpl;

import java.io.File;
import java.io.IOException;

import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mediatek.media.VideoInfo;
import com.mediatek.media.VideoTrackInfo;
import com.mediatek.mmpcm.MmpTool;
import com.mediatek.mmpcm.mmcimpl.Const;
import com.mediatek.mmpcm.mmcimpl.PlayList;
import com.mediatek.mmpcm.video.IPlayback;
import com.mediatek.mmpcm.video.IPlayback.OnPBBufferUpdateListener;
import com.mediatek.mmpcm.video.IPlayback.OnPBDurarionUpdateListener;
import com.mediatek.mmpcm.video.IPlayback.OnPBEofListener;
import com.mediatek.mmpcm.video.IPlayback.OnPBPlayDoneListener;
import com.mediatek.tv.TVManager;
import com.mediatek.tv.service.InputService;

public class Playback implements IPlayback, SurfaceHolder.Callback {

    private static MediaPlayer mmediaplayer = null;
    private static Playback pb = null;
    private static VideoManager vidman = null;

    private SurfaceHolder msurfaceholder;
    private String mcurPath = null;
    private int mPlayStatus = VideoConst.PLAY_STATUS_UNKNOW;
    private static PlayList videopl = null;
    private static boolean isEnd = false;
    private static int mplayerMode = VideoConst.PLAYER_MODE_MMP;
    private boolean previewMode = false;

    public void setOnPBMsgListener(OnPBMsgListener listener) {
        mOnPBMsgListener = listener;
    }

    private OnPBMsgListener mOnPBMsgListener;

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

    public void setOnPBDurarionUpdateListener(OnPBDurarionUpdateListener listener) {
        mOnPBDurarionUpdateListener = listener;
    }

    private OnPBDurarionUpdateListener mOnPBDurarionUpdateListener;
    
    public void setOnPBEofListener(OnPBEofListener listener) {
        mOnPBEofListener = listener;
    }

    private OnPBEofListener mOnPBEofListener;
    
    // playback
    public Playback(SurfaceView surfaceview, int playerMode) {
        mmediaplayer = new MediaPlayer();

        msurfaceholder = surfaceview.getHolder();
        msurfaceholder.addCallback(this);
        // msurfaceholder.setFixedSize(surfaceview.getWidth(),
        // surfaceview.getHeight());
        msurfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mmediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mmediaplayer.setDisplay(msurfaceholder);

        mmediaplayer.setOnErrorListener(errListener);
        mmediaplayer.setOnPreparedListener(preListener);
        mmediaplayer.setOnCompletionListener(completeListener);
        mmediaplayer.setOnSeekCompleteListener(seekListener);

        mPlayStatus = VideoConst.PLAY_STATUS_UNKNOW;

        if (playerMode == VideoConst.PLAYER_MODE_MMP) {
            videopl = PlayList.getPlayList();
            mcurPath = videopl.getCurrentPath(Const.FILTER_VIDEO);
        }
        mplayerMode = playerMode;

        pb = this;
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
     */
    public void setPlayerMode(int mode) {
        mplayerMode = mode;
    }

    /**
     * get player mode
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
     * @param path
     */
    public void setDataSource(String path) throws IOException,
            IllegalArgumentException, IllegalStateException {

        if (mPlayStatus != VideoConst.PLAY_STATUS_END) {
            MmpTool.LOG_ERROR("Video is playing,can't set setDataSource");
            sendMsg(VideoConst.MSG_IS_PLAYING);
        }

        isEnd = false;

        try {
            mmediaplayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }

        mPlayStatus = VideoConst.PLAY_STATUS_INITED;

        MmpTool.LOG_DBG("setDataSource finish!");
    }

    /**
     * play
     */
    public void play() throws IllegalStateException {

        if (mmediaplayer != null) {
            if (mPlayStatus == VideoConst.PLAY_STATUS_PAUSED) {
                try {
                    mmediaplayer.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    sendMsg(VideoConst.MSG_INVALID_STATE);
                    throw new IllegalStateException(e);
                }

                mPlayStatus = VideoConst.PLAY_STATUS_STARTED;

                MmpTool.LOG_DBG("Pause to Play!");

            } else if (mPlayStatus == VideoConst.PLAY_STATUS_STARTED
                    || mPlayStatus == VideoConst.PLAY_STATUS_PREPARED) {
                MmpTool.LOG_DBG("Has played or prepared!");
                return;
            } else if (mPlayStatus == VideoConst.PLAY_STATUS_INITED) {
                try {
                    mmediaplayer.prepareAsync();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    sendMsg(VideoConst.MSG_INVALID_STATE);
                    throw new IllegalStateException(e);
                }
                mPlayStatus = VideoConst.PLAY_STATUS_PREPARED;
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
            if (mPlayStatus == VideoConst.PLAY_STATUS_STARTED) {
                try {
                    mmediaplayer.pause();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    sendMsg(VideoConst.MSG_INVALID_STATE);
                    throw new IllegalStateException(e);
                }
                mPlayStatus = VideoConst.PLAY_STATUS_PAUSED;
                MmpTool.LOG_DBG("pause!");
            } else {
                try {
                    mmediaplayer.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    sendMsg(VideoConst.MSG_INVALID_STATE);
                    throw new IllegalStateException(e);
                }
                mPlayStatus = VideoConst.PLAY_STATUS_STARTED;
                MmpTool.LOG_DBG("play!");
            }
        }
    }

    /**
     * stop
     */
    public void stop() throws IllegalStateException {
        if (mmediaplayer != null) {
            try {
                isEnd = true;
                saveProgress(getProgress());
                if (mPlayStatus != VideoConst.PLAY_STATUS_STOPPED) {
                    mmediaplayer.stop();
                    mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
                }

                mPlayStatus = VideoConst.PLAY_STATUS_END;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                sendMsg(VideoConst.MSG_INVALID_STATE);
                throw new IllegalStateException(e);
            }

            if (mOnPBCompleteListener != null) {
                mOnPBCompleteListener.onComplete(pb);
            }

            MmpTool.LOG_DBG("stop!");
        }
    }

    /**
     * release
     */
    public void onRelease() {
        mOnPBPreparedListener = null;
        mOnPBCompleteListener = null;
        mOnPBMsgListener = null;
        mPlayStatus = VideoConst.PLAY_STATUS_END;
        videoMsgHandler.removeCallbacksAndMessages(null);
        if (previewMode == false) {
            mmediaplayer.release();
        }
        MmpTool.LOG_DBG("release!");
    }

    /**
     * reset
     */
    public void reset() {
        if (mmediaplayer != null) {
            mmediaplayer.reset();
        }
        mPlayStatus = VideoConst.PLAY_STATUS_UNKNOW;
        MmpTool.LOG_DBG("reset!");
    }

    /**
     * seek
     */
    public void seek(int msec) throws IllegalStateException {
        if (mmediaplayer != null) {
            try {
                if (msec < getDuration()) {
                    MmpTool.LOG_DBG("start seek!");

                    mmediaplayer.seekTo(msec);
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
                sendMsg(VideoConst.MSG_INVALID_STATE);
                throw new IllegalStateException(e);
            }
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

            if (mPlayStatus != VideoConst.PLAY_STATUS_STOPPED) {
                mmediaplayer.stop();
                mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
            }

            mcurPath = videopl.getNext(Const.FILTER_VIDEO, Const.AUTOPLAY);

            mmediaplayer.reset();
            
            mPlayStatus = VideoConst.PLAY_STATUS_END;

            try {
                setDataSource(mcurPath);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            try {
                play();
            } catch (IllegalStateException e) {
                e.printStackTrace();
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
            saveProgress(getProgress());
            if (mPlayStatus != VideoConst.PLAY_STATUS_STOPPED) {
                try {
                    mmediaplayer.stop();
                } catch (IllegalStateException e) {
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

            mmediaplayer.reset();

            mPlayStatus = VideoConst.PLAY_STATUS_END;

            try {
                setDataSource(mcurPath);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }

            try {
                play();
            } catch (IllegalStateException e) {
                e.printStackTrace();
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
                throw new IllegalStateException("Can't do Prev!!!");
            }

            if (videopl.getShuffleMode(Const.FILTER_VIDEO) == Const.SHUFFLE_ON
                    || videopl.getRepeatMode(Const.FILTER_VIDEO) == Const.REPEAT_NONE) {
                if (videopl.getCurrentIndex(Const.FILTER_VIDEO) == 0) {
                	throw new IllegalStateException("head of PlayList!!!");
                }
            }
            saveProgress(getProgress());
            if (mPlayStatus != VideoConst.PLAY_STATUS_STOPPED) {
                try {
                    mmediaplayer.stop();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    throw new IllegalStateException(e);
                }
                mPlayStatus = VideoConst.PLAY_STATUS_STOPPED;
            }

            mcurPath = videopl.getNext(Const.FILTER_VIDEO, Const.MANUALPRE);

            if (mcurPath == null) {
                MmpTool.LOG_DBG("End of PlayList!");
                stop();
            }

            MmpTool.LOG_DBG(mcurPath);

            mmediaplayer.reset();

            mPlayStatus = VideoConst.PLAY_STATUS_END;

            try {
                setDataSource(mcurPath);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }

            try {
                play();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * fast forward
     */
    public void fastForward() throws IllegalStateException {

    }

    /**
     * fast rewind
     */
    public void fastRewind() throws IllegalStateException {

    }

    /**
     * slow forward
     */
    public void slowForward() throws IllegalStateException {

    }

    /**
     * play next frame
     */
    public void playNextFrame() {

    }

    /**
     * step
     */
    public void step() {

    }

    public void abRepeat() {

    }

    /**
     * select mts 
     * @param mtsIdx
     */
    public void selectMts(short mtsIdx) {

    }

    private int getPictureWidth() {
        if (mmediaplayer != null
                && mPlayStatus != VideoConst.PLAY_STATUS_UNKNOW)
            return mmediaplayer.getVideoWidth();
        return 0;
    }

    private int getPictureHeight() {
        if (mmediaplayer != null
                && mPlayStatus != VideoConst.PLAY_STATUS_UNKNOW)
            return mmediaplayer.getVideoHeight();
        return 0;
    }

    public VidMediaInfo getMediaInfo() {
        VidMediaInfo vidInfo = new VidMediaInfo();

        if (mmediaplayer != null) {
            if (mPlayStatus >= VideoConst.PLAY_STATUS_STARTED
                    && mPlayStatus <= VideoConst.PLAY_STATUS_FR) {

                vidInfo.setVideoTrackInfo(0, getPictureWidth(),
                        getPictureHeight(), 0);
            }
        }

        return vidInfo;
    }

    /**
     * get duration 
     */
    public int getDuration() {
        if (mmediaplayer != null) {
            if (mPlayStatus == VideoConst.PLAY_STATUS_STARTED
                    || mPlayStatus == VideoConst.PLAY_STATUS_PAUSED) {
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
            if (mPlayStatus == VideoConst.PLAY_STATUS_STARTED
                    || mPlayStatus == VideoConst.PLAY_STATUS_PAUSED) {
                return mmediaplayer.getCurrentPosition();
            } else {
                return 0;
            }
        }
        return 0;
    }

    /**
     * if end or not
     */
    public boolean isEnd() {
        return isEnd;
    }

    /**
     * get play status
     */
    public int getPlayStatus() {
        return mPlayStatus;
    }

    /**
     * get speed
     */
    public int getSpeed() {
        return 0;
    }

    private OnErrorListener errListener = new OnErrorListener() {
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // TODO Auto-generated method stub
            MmpTool.LOG_ERROR("error happened!");
            mmediaplayer.reset();
            return true;
        }
    };

    private void saveProgress(int progress) {
        if (previewMode == true) {
            return;
        } else {
            vidman = VideoManager.getInstance();
            String path = videopl.getCurrentPath(Const.FILTER_VIDEO);

            String info = "progress = " + Integer.toString(progress);
            MmpTool.LOG_DBG(info);

            if (progress > 0 && progress < getDuration()) {
                vidman.getFileInfo().saveProgress(path, progress);
            } else {
                vidman.getFileInfo().saveProgress(path, 0);
            }
        }
    }

    private int getSavedProgress() {
        if (previewMode == true) {
            return 0;
        } else {
            vidman = VideoManager.getInstance();

            String path = videopl.getCurrentPath(Const.FILTER_VIDEO);

            return vidman.getFileInfo().getSavedProgress(path);
        }
    }

    /**
     * get video title
     */
    public String getVideoTitle() {
        return null;
    }

    /**
     * get video Director
     */
    public String getVideoDirector() {
        return null;
    }

    /**
     * get video copy right
     */
    public String getVideoCopyright() {
        return null;
    }

    
    public String getVideoGenre() {
        return null;
    }

    /**
     * get video year
     */
    public String getVideoYear() {
        return null;
    }

    /**
     * get program count
     */
    public byte getProgramCount() {
        return 0;
    }

    /**
     * set program
     * @param index
     */
    public void setProgram(short index){
        
    }
    
    /**
     * set sub title track
     */
    public void setSubtitleTrack(short index){
        
    }
    
    private OnCompletionListener completeListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer arg0) {
            // TODO Auto-generated method stub
            MmpTool.LOG_DBG("play compeletion!");

            if (mmediaplayer != null) {

                saveProgress(0);
                if (videopl.getShuffleMode(Const.FILTER_VIDEO) == Const.SHUFFLE_ON
                        || videopl.getRepeatMode(Const.FILTER_VIDEO) == Const.REPEAT_NONE) {
                    if (videopl.getCurrentIndex(Const.FILTER_VIDEO) >= ((videopl
                            .getFileNum(Const.FILTER_VIDEO) - 1))) {
                        try {
                            isEnd = true;
                            mmediaplayer.stop();
                            mPlayStatus = VideoConst.PLAY_STATUS_END;
                            throw new Exception("End of PlayList!!!");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    autoNext();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (mOnPBCompleteListener != null) {
                    mOnPBCompleteListener.onComplete(pb);
                }
            }
        }
    };

    private OnPreparedListener preListener = new OnPreparedListener() {

        public void onPrepared(MediaPlayer mp) {
            // TODO Auto-generated method stub

            try {
                msurfaceholder.setFixedSize(getPictureWidth(),
                        getPictureHeight());

                int progress = 0;
                progress = getSavedProgress();
                if (progress > 0) {
                    mmediaplayer.seekTo(progress);
                    sendMsg(VideoConst.MSG_SEEKING);
                    mPlayStatus = VideoConst.PLAY_STATUS_SEEKING;
                } else {
                    mmediaplayer.start();
                    sendMsg(VideoConst.MSG_PLAY_START);
                    mPlayStatus = VideoConst.PLAY_STATUS_STARTED;
                    if (mOnPBPreparedListener != null) {
                        mOnPBPreparedListener.onPrepared(pb);
                    }
                }
                if (previewMode == true) {
                    setPreviewRect();
                }
            } catch (Exception e) {
                try {
                    throw new Exception(e);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
    };

    private OnSeekCompleteListener seekListener = new OnSeekCompleteListener() {

        public void onSeekComplete(MediaPlayer mp) {
            // TODO Auto-generated method stub
            mmediaplayer.start();
            sendMsg(VideoConst.MSG_PLAY_START);
            mPlayStatus = VideoConst.PLAY_STATUS_STARTED;
        }
    };

    Handler videoMsgHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mOnPBMsgListener != null) {
                mOnPBMsgListener.onMsg(msg.what);
            }
        }
    };

    /**
     * surface changed
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        // TODO Auto-generated method stub
        MmpTool.LOG_DBG("Surface Changed");
    }

    /**
     * surface created
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        MmpTool.LOG_DBG("Surface Created");
    }

    /**
     * surface destroyed
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        MmpTool.LOG_DBG("Surface Destroyed");
    }

    /**
     * get current file position
     */
    public long getCurFilePosition(){
    	return 0;
    }
    
    /**
     * set file size
     */
    public void setFileSize(long size){
    	
    }
    
    /**
     * get video file size
     */
    public long getVideoFileSize(){
    	return 0;
    }
    
    /**
     * set sub default type
     */
    public void setSubDefaultType(){
        
        
    }
    
    /**
     * set sub display mode
     */
    public void setSubDisplayMode(int mode) {

    }

    /**
     * set sub high light style
     */
    public void setSubHighlightStyle(int highlightStyle) {

    }
    
    /**
     * set sub timeoffset
     */
    public void setSubTimeOffset(int offsetMode, int offsetValue) {

    }
    
    /**
     * set sub font encode
     */
    public void setSubFontEncode(int encodeMode) {

    }
    
    /**
     * set sub on or off
     */
    public void setSubOnOff(boolean on) {

    }
    
    public void setSubShowHide(boolean on){
    	
    }
    
    /**
     * set sub font info
     */
    public void setSubFontInfo(Subtitle.FontInfo info) {

    }
    
    /**
     * set sub background color
     */
    public void setSubBgColor(Subtitle.Color color) {

    }
    
    /**
     * set sub text color
     */
    public void setSubTextColor(Subtitle.Color color) {

    }
    
    public void setSubEdgeColor(Subtitle.Color color){
    	
    }
    /**
     * set sub boder type
     */
    public void setSubBoderType(int type) {

    }
    
    /**
     * set sub boder width
     */
    public void setSubBoderWidth(int width) {

    }
    
    /**
     * set sub roll type
     */
    public void setSubRollType(int type) {

    }
    
    /**
     * set sub display rect
     * @param dispRect
     */
    public void setSubDisplayRect(Rect dispRect) {

    }
    
    public void setSubDisplayOffset(int x,int y){
    	
    }
    
    public void setSubFontSize(int size){
    	
    }
    
    public void setSubFontCustomSize(byte customSize){
    	
    }

    private int setPreviewRect() {
        Rect outRect = new Rect(VideoConst.PREVIEW_RECT_X,
                VideoConst.PREVIEW_RECT_Y, 
                VideoConst.PREVIEW_RECT_X + VideoConst.PREVIEW_RECT_W, 
                VideoConst.PREVIEW_RECT_Y + VideoConst.PREVIEW_RECT_H);
        
        TVManager tvManager = TVManager.getInstance(null);
        InputService inputManager = (InputService) tvManager
                .getService(InputService.InputServiceName);
        if(null != inputManager){
        return inputManager.setScreenOutputRect(InputService.INPUT_OUTPUT_MAIN,
                outRect);
        }
        return -1;
    }
    
    /**
     * replay
     */
    public void replay(){
    	
    }
    /**
     * Get Video Track information, for example, width, height,
     * @return VideoTrackInfo.
     * @deprecated
     */ 
    public VideoTrackInfo getVideoTrackInfo(){
    	return null;
    }
    /**
     * Get Video information, for example, width, height,
     * @return VideoInfo.
     */ 
    public VideoInfo getVideoInfo(){
    	return null;
    }
    
    //add by shuming for fix bug DTV00385698
	public  void setFeaturenotsurport(boolean featurenotsurport) {
		
	}

	public boolean isFeaturenotsurport() {
		return false;
	}
	//end

	public boolean canDoSeek() {
		return false;
	}
}
