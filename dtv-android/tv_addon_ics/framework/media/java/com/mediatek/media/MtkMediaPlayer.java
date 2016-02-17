package com.mediatek.media;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
/**
 * This class provide MediaPlayer capabilities.
 * <ul>
 *
 * </ul>
 */
public class MtkMediaPlayer {

	private static final String TAG = "MtkMediaPlayer";

	private int mNativeContext; // accessed by native methods

    //Profile string for opening a playback control
    public static final String PROFILE_MAIN_SVCTX             = "main_svctx";
    public static final String PROFILE_SUB_SVCTX              = "sub_svctx";
    public static final String PROFILE_AUTO_DETECT_NEXT_VID   = "AUTO_DETECT_NEXT_VID";
    public static final String PROFILE_USB                    = "USB";
    public static final String PROFILE_SUB_USB                = "SUB_USB";

   //URLType
    public static final int URL_TYPE_UNKNOWN              = 0;//match to IMTK_PB_CTRL_URL_TYPE_PROXY in CMPB
    public static final int URL_TYPE_AGENT                = 1;//match to IMTK_PB_CTRL_URL_TYPE_AGENT in CMPB
    public static final int URL_TYPE_COOKIE               = 2;//match to IMTK_PB_CTRL_URL_TYPE_COOKIE in CMPB
    public static final int URL_TYPE_EXT_SBTL_FULL_PATH   = 3;//match to IMTK_PB_CTRL_URL_TYPE_EXT_SBTL_FULL_PATH in CMPB
    public static final int URL_TYPE_LYRIC_FULL_PATH      = 4;//match to IMTK_PB_CTRL_URL_TYPE_LYRIC_FULL_PATH in CMPB

   //prepareType
    public static final int PREPARE_TYPE_SYNC             = 1;
    public static final int PREPARE_TYPE_ASYNC            = 2;

   //mediaPlayerMode
    public static final int MEDIAPLAYER_MODE_URI          = 1;// match to IMTK_PB_CTRL_BUFFERING_MODEL_URI in CMPB
    public static final int MEDIAPLAYER_MODE_PULL         = 2;// match to IMTK_PB_CTRL_BUFFERING_MODEL_PULL in CMPB

   //MtkMediaPlayer State
    private static final int STATE_ERROR          = -1;
    private static final int STATE_IDLE           = 0;
    private static final int STATE_INITIALLIZED   = 1;
    private static final int STATE_PREPARING      = 2;
    private static final int STATE_PREPARED       = 3;
    private static final int STATE_STARTED        = 4;
    private static final int STATE_PAUSED         = 5;
    private static final int STATE_COMPLETED      = 6;
    private static final int STATE_STOPPED        = 7;
    private static final int STATE_END            = 8;

    static {
        System.loadLibrary("mtkmediaplayer"); // load library
        nativeCMPBInit();
    }

    private int mState = STATE_IDLE;
	private int mBackupState = STATE_IDLE;
    private boolean mIsPlaying = false;
	private int mPlayerHandle;

	// input stream from DataSource
	private DataSource mDataSource;
    private InputStream mInputStream;
    private static final int STREAM_BUF_SIZE = 256 * 1024;
    private byte[] mStreamReadBuffer;
    private byte[] mTsInfoBuffer;
    private int mStreamReadBufferSize;
	private long mStreamCurPos;
    private long mStreamSize = 0;
	private boolean mIsInputStreamValid = true;

	// meta data
    private int mMediaType = DataSourceMetadata.MEDIA_TYPE_UNKNOWN;
    private DataSourceMetadata mDataSourceMetadata;

	private boolean mIsAudioPlayer = false;

    private boolean mSeekEnable = true;
    private int mMillisecond = 0;
    private long mByteSeekPos = 0;
    private long mByteSeekPts = 0;
    private boolean byteSeekPtsEnable = false;

	// player configuration
	private String mPlayerProfile   = MtkMediaPlayer.PROFILE_MAIN_SVCTX;
	private int mPrepareType        = MtkMediaPlayer.PREPARE_TYPE_SYNC;
	private int mConfigUrlType      = MtkMediaPlayer.URL_TYPE_UNKNOWN;
    private String mConfigUrl       = null; //user agent, proxy, subtile, etc.
    private String mDataSourceUrl   = "";
    private int mMediaPlayerMode    = MtkMediaPlayer.MEDIAPLAYER_MODE_PULL;
    private boolean mIsLocalUrlMode = false;

	private static int m_i4Count_NativeOpen_Called;
	private static int m_i4Count_NativeClose_Called;

    /**
     * Default constructor.
     */
    public MtkMediaPlayer() {
    	Log.i(TAG, "MtkMediaPlayer(): Enter");

		m_i4Count_NativeOpen_Called = 0;
		m_i4Count_NativeClose_Called = 0;

        mState = STATE_IDLE;
        mStreamReadBuffer = new byte[STREAM_BUF_SIZE];
        mStreamReadBufferSize = STREAM_BUF_SIZE;

        Log.i(TAG, "MtkMediaPlayer(): mState = " + mState);

		Log.i(TAG, "MtkMediaPlayer(): Leave");
    }

    /**
     * Constructs an instance with the specified source.
     *
     * @param source
     *        the dataSource for you want to play
     * @see DataSource
     */
    public MtkMediaPlayer(DataSource source) {
    	Log.i(TAG, "MtkMediaPlayer(DataSource source): Enter");

		m_i4Count_NativeOpen_Called = 0;
		m_i4Count_NativeClose_Called = 0;

        this.mDataSource = source;
        this.mMediaPlayerMode = MEDIAPLAYER_MODE_PULL;
		mIsLocalUrlMode = false;
        mState = STATE_INITIALLIZED;
        mStreamReadBuffer = new byte[STREAM_BUF_SIZE];
        mStreamReadBufferSize = STREAM_BUF_SIZE;

        Log.i(TAG, "MtkMediaPlayer(DataSource source): mState = " + mState);
		Log.i(TAG, "MtkMediaPlayer(DataSource source): Leave");
    }

	private synchronized void setInputStreamValid()
	{
		Log.i(TAG, "setInputStreamValid(): occur");
		mIsInputStreamValid = true;
	}

	private synchronized void setInputStreamInValid()
	{
		Log.i(TAG, "setInputStreamInValid(): occur");
		mIsInputStreamValid = false;
	}

	private synchronized boolean IsInputStreamValid()
	{
		return mIsInputStreamValid;
	}

	private void closeInputStream()
	{
        Log.i(TAG, "closeInputStream(): Enter");

        if (this.mMediaPlayerMode == MEDIAPLAYER_MODE_PULL) {
            Log.i(TAG, "closeInputStream(): call mInputStream.close()");
            try {
				if(null!=this.mInputStream)
					{
                this.mInputStream.close();
            }
            }
            catch (Exception e) {
                Log.e(TAG, "closeInputStream(): call mInputStream.close(), error.");
            }
        }

        Log.i(TAG, "closeInputStream(): Leave");
    }

    /**
     * Sets the data source to use.
     *
     * @param source
     *        the dataSource for you want to play
     * @see DataSource
     */
    public void setDataSource(DataSource source) throws IllegalStateException {
    	Log.i(TAG, "setDataSource(DataSource source): Enter");
		Log.i(TAG, "setDataSource(DataSource source): begin mState = " + mState);

        if(mState == STATE_IDLE){
        	this.mDataSource = source;
        	this.mMediaPlayerMode = MEDIAPLAYER_MODE_PULL;
            mState = STATE_INITIALLIZED;
			mIsLocalUrlMode = false;
        }
        else {
			Log.e(TAG, "setDataSource(DataSource source): state error, current mState=" + mState + ", throw Exception");
            throw new IllegalStateException("Invalid State!");
        }

        Log.i(TAG, "setDataSource(DataSource source): end mState = " + mState);
		Log.i(TAG, "setDataSource(DataSource source): Leave");
    }

    /**
     * Sets the data source to use.
     *
     * @param path
     *        the path of the file
     *
     */
    public void setDataSource(String path) throws IllegalStateException {
    	Log.i(TAG, "setDataSource(String path): Enter");
		Log.i(TAG, "setDataSource(String path): begin mState = " + mState);

        if(mState == STATE_IDLE){
        	if(path == null || path.equals("")){
				Log.e(TAG, "setDataSource(String path): path error, throw Exception");
        		throw new IllegalStateException("path is NULL!");
        	}
            else if(path.toLowerCase().startsWith("/")){
            	this.mMediaPlayerMode = MEDIAPLAYER_MODE_PULL;
				mIsLocalUrlMode = true;
				Log.i(TAG, "setDataSource(String path): local url and the path is : " + path);
            }
            else{
            this.mMediaPlayerMode = MEDIAPLAYER_MODE_URI;
				mIsLocalUrlMode = false;
				Log.i(TAG, "setDataSource(String path): network url and the path is : " + path);
            }
        	this.mDataSourceUrl = path;
            mState = STATE_INITIALLIZED;
            Log.i(TAG, "setDataSource(String path): mDataSourceUrl = " + this.mDataSourceUrl + " mMediaPlayerMode = " + this.mMediaPlayerMode);
        }
        else {
			Log.e(TAG, "setDataSource(String path): state error, current mState=" + mState + ", throw Exception");
            throw new IllegalStateException("Invalid State!");
        }

        Log.i(TAG, "setDataSource(String path): end mState = " + mState);
		Log.i(TAG, "setDataSource(String path): Enter");
    }

    /**
     * Sets the data source metadata to use.
     *
     * @param dataSourceMetadata
     *
     * @see DataSourceMetadata
     */
    public void setDataSourceMetadata(DataSourceMetadata dataSourceMetadata) throws IllegalStateException {
        Log.i(TAG, "setDataSourceMetadata(DataSourceMetadata): Enter");

        this.mDataSourceMetadata = dataSourceMetadata;

        Log.i(TAG, "setDataSourceMetadata(DataSourceMetadata): Leave");
    }

    /**
     * Sets the data source whether or not can be seek.
     *
     * @param seekEnable
     *        true to enable seek, false to disable seek
     *
     */
    public void setDataSourceSeekEnable(boolean seekEnable){
        Log.i(TAG, "setDataSourceSeekEnable(): Enter");

		Log.i(TAG, "setDataSourceSeekEnable(): seekEnable=" + seekEnable);

        this.mSeekEnable = seekEnable;

        Log.i(TAG, "setDataSourceSeekEnable(): Leave");
    }

	public void configAudioPlayer()	{
		Log.i(TAG, "configAudioPlayer(): Enter");

        this.mIsAudioPlayer = true;

        Log.i(TAG, "configAudioPlayer(): Leave");
	}

    private void handleMessage(int type, int what, int extra) {

        if (type == EVENT_ERROR) {
			Log.e(TAG, "handleMessage(): receive EVENT_ERROR");

            if(what != IMTK_PB_CTRL_ERROR_VIDEO_UNPLAYABLE &&
               what != IMTK_PB_CTRL_ERROR_AUDIO_UNPLAYABLE)
            {
               mBackupState = mState;
               mState = STATE_ERROR;
			   Log.i(TAG, "handleMessage(): case EVENT_ERROR, mState = STATE_ERROR");
            }

            if (mErrorListener != null) {
                mErrorListener.onError(this, what, extra);
            }

        } else if (type == EVENT_UPDATE) {
            if (mUpdateListener != null) {
                mUpdateListener.onBufferingUpdate(this, extra);
            }
        } else if (type == EVENT_COMPLETION) {
        	Log.i(TAG, "handleMessage(): receive EVENT_COMPLETION");

			mState = STATE_COMPLETED;
			if (mCompletionListener != null) {
				mCompletionListener.onCompletion(this, what, extra);
            }

        }  else if (type == EVENT_SEEKCOMPLETE) {
        	Log.i(TAG, "handleMessage): receive EVENT_SEEKCOMPLETE");

            if (mSeekListener != null) {
                mSeekListener.onSeekComplete(this);
            }
        } else if (type == EVENT_PLAYDONE) {
		   	Log.i(TAG, "handleMessage(): receive EVENT_PLAYDONE");

            if (mPlayDoneListener != null) {
                if(byteSeekPtsEnable){
                    setByteSeekPts(this.mByteSeekPos, this.mByteSeekPts);
                    Log.i(TAG, "handleMessage(): case EVENT_PLAYDONE, mByteSeekPos=" + this.mByteSeekPos + ", mByteSeekPts=" + this.mByteSeekPts);
                }
                mPlayDoneListener.onPlayDone(this);
            }
        } else if (type == EVENT_CUR_TIME_UPDATE) {
			if (mCurTimeUpdateListener != null) {
				Log.i(TAG, "handleMessage(): receive EVENT_CUR_TIME_UPDATE");
				mCurTimeUpdateListener.onCurTimeUpdate(this, what, extra);
        }
        } else if (type == EVENT_TOTAL_TIME_UPDATE) {
			Log.i(TAG, "handleMessage(): receive EVENT_TOTAL_TIME_UPDATE");

			if (mTotalTimeUpdateListener != null) {
				mTotalTimeUpdateListener.onTotalTimeUpdate(this, what, extra);
               }
        } else if (type == EVENT_STEP_DONE) {
			Log.i(TAG, "handleMessage(): receive EVENT_STEP_DONE");

			if (mStepDoneListener != null) {
				mStepDoneListener.onStepDone(this, what, extra);
        }
        } else if (type == EVENT_EOF) {
			Log.i(TAG, "handleMessage(): receive EVENT_EOF");

			if (mEofListener != null) {
				mEofListener.onEof(this, what, extra);
               }
        } else if (type == EVENT_POSITION_UPDATE) {
			if (mPositionUpdateListener != null) {
				mPositionUpdateListener.onPositionUpdate(this, what, extra);
				Log.i(TAG, "handleMessage(): receive EVENT_POSITION_UPDATE");
        }
        } else if (type == EVENT_SPEED_UPDATE) {
			Log.i(TAG, "handleMessage(): receive EVENT_SPEED_UPDATE: what=" + what);

			if (mSpeedUpdateListener != null) {
				mSpeedUpdateListener.onSpeedUpdate(this, what, extra);
               }
         } else if (type == EVENT_PREPARED) {
			Log.i(TAG, "handleMessage(): receive EVENT_PREPARED");

			mState = STATE_PREPARED;
			if(mPreparedListener != null) {
				mPreparedListener.onPrepared(this);
        }
        } else if (type == EVENT_REPLAY) {
			Log.i(TAG, "handleMessage(): receive EVENT_REPLAY: what=" + what);

			if (mReplayListener != null) {
				mReplayListener.onRePlay(this, what, extra);
               }
	   	} else if (type == EVENT_AUDIO_ONLY_SERVICE)
   		{
   			Log.i(TAG, "handleMessage(): receive EVENT_AUDIO_ONLY_SERVICE: what=" + what);
   			if (mAudioOnlyListener != null)
			{
				mAudioOnlyListener.onAudioOnly(this, what, extra);
               }
	   	}

    }

	 /**
	 *
	 * @return:
	 * >0 : the number of bytes actually read
	 * -2   : exception happened
	 */
    private synchronized int read(int size) {

        int result = 0;
		boolean isStreamValid = true;

        if (mPlayerHandle == 0) {
			Log.e(TAG, "read(): mPlayerHandle = 0, return");
            return 0;
        }
		isStreamValid = IsInputStreamValid();
		if (!isStreamValid)
		{
			Log.i(TAG, "read(): input stream is not valid now, so return directly 1");
			return 0;
		}
        if (size > mStreamReadBufferSize) {
            mStreamReadBufferSize = size;
            mStreamReadBuffer = new byte[mStreamReadBufferSize];
        }
        while (true) {
            try {
				isStreamValid = IsInputStreamValid();
				if (!isStreamValid)
			{
					Log.i(TAG, "read(): input stream is not valid now, so return directly 2");
				return 0;
			}
                int ret = mInputStream.read(mStreamReadBuffer, result, size - result);
                if (ret > 0) {
                    mStreamCurPos += ret;
                    result += ret;
                } else {
					Log.i(TAG, "read(" + size + "): ret<=0, result=" + result);
                    return result;
                }

                if (result == size) {
                    break;
                }

            } catch (Exception e) {
            	result = -2;
				Log.e(TAG, "read(" + size + "): Exception Happen result = " + result);
                return result;
            }
        }
        return result;
    }

    private long skip(long n) {

        long ret = 0;
        if (n == 0) {
            return 0;
        }

		boolean isStreamValid = true;

        if (n <= mStreamReadBufferSize) {
            ret = this.read((int) n);

        } else {
            long r = n;
            while (true) {
				isStreamValid = IsInputStreamValid();
				if (!isStreamValid)
				{
					Log.i(TAG, "skip(): input stream is not valid now, so return directly 1");
					return 0;
				}
                ret = this.read((int) mStreamReadBufferSize);
                if (ret > 0) {
                    r -= ret;
                } else {
                    break;
                }

                if (r <= mStreamReadBufferSize) {
					isStreamValid = IsInputStreamValid();
					if (!isStreamValid)
					{
						Log.i(TAG, "skip(): input stream is not valid now, so return directly 2");
						return 0;
					}
                    ret = this.read((int) r);
                    if (ret > 0) {
                        r -= ret;
                    }
                    break;
                }
            }
        }

        return ret;
    }

    private synchronized long seek(int whence, long offset) {
		boolean isStreamValid = true;
		isStreamValid = IsInputStreamValid();
		if (!isStreamValid)
		{
			Log.i(TAG, "seek(): input stream is not valid now, so return directly 1");
			return 0;
		}

        if (whence == SEEK_CUR) {
            if (offset == 0) {
                return mStreamCurPos;
            }
            if (offset < 0) {
                try {
                    mStreamCurPos += offset;
                    if (mStreamCurPos < 0) {
                        mStreamCurPos = 0;
                    }
                    this.mInputStream.close();
                    this.mInputStream = mDataSource.newInputStream();
                    try{
                        mStreamCurPos = mInputStream.skip(mStreamCurPos);
                    }
                    catch(Exception ex){
                        skip(mStreamCurPos);
                    }
                    return mStreamCurPos;
                } catch (IOException e) {
                	Log.e(TAG, "seek(): exception happen, 1");
                    return mStreamCurPos;
                }
            } else {
                try{

                    mStreamCurPos += mInputStream.skip(offset);
                }
                catch(Exception ex){
                    mStreamCurPos += skip(offset);
                }
                return mStreamCurPos;
            }
        } else if (whence == SEEK_BEGIN) {
            if (mStreamCurPos == offset) {
                return mStreamCurPos;
            }
            if((offset > mStreamCurPos) && ((offset - mStreamCurPos) < 1*1024*1024) ) {
                try{
                    mStreamCurPos += mInputStream.skip((offset - mStreamCurPos));
                }
                catch(Exception ex){
                    mStreamCurPos += skip(offset);
                }
                return mStreamCurPos;
            }
            try {
                mStreamCurPos = 0;
                this.mInputStream.close();
                this.mInputStream = mDataSource.newInputStream();
                try{
                    mStreamCurPos += mInputStream.skip(offset);
                }
                catch(Exception ex){
                    mStreamCurPos += skip(offset);
                }
                return mStreamCurPos;
            } catch (Exception e) {
            	Log.e(TAG, "seek(): exception happen, 2");
                return mStreamCurPos;
            }
        } else {
            if((mStreamSize > 0) && (offset < 0)) {
                    long pos = mStreamSize + offset;
                    if((pos >= mStreamCurPos)) {
                        if (((pos - mStreamCurPos) < 1*1024*1024)) {
                            try {
                                mStreamCurPos += mInputStream.skip((pos - mStreamCurPos));
                            } catch (Exception ex) {
                                mStreamCurPos += skip((pos - mStreamCurPos));
                            }
                            return mStreamCurPos;
                        }
                    }

                    try {
                        this.mInputStream.close();
                    } catch (IOException e) {
                    	Log.e(TAG, "seek(): exception happen, 3");
                    	return mStreamCurPos;
                    }
                    this.mInputStream = mDataSource.newInputStream();
                    mStreamCurPos = 0;
                    if (pos > 0) {
                        try {
                            mStreamCurPos = mInputStream.skip(pos);
                        } catch (Exception ex) {
                            mStreamCurPos = skip(pos);
                        }
                    }
                    return mStreamCurPos;
                }

            return mStreamCurPos;
        }
    }

    private int readTsInfo(int size){

        if(mPlayerHandle == 0)
        {
	       Log.e(TAG, "readTsInfo(): mPlayerHandle = 0, return");
           return 0;
        }
        mTsInfoBuffer = new byte[size];
        try
        {
            int ret = mInputStream.read(mTsInfoBuffer, 0, size);
            return ret;
        }
        catch(Exception e)
        {
            return 0;
        }
    }

    /**
     * Gets the current playback position.
     *
     * @return the current position in milliseconds
     *
     */
    public int getCurrentPosition() {

		if(mPlayerHandle == 0)
		{
			Log.e(TAG, "getCurrentPosition(): mPlayerHandle = 0, return");
			return 0;
		}
        return (int)this.nativeGetCurrentPosition(mPlayerHandle, PositionType.MILLISECOND);
    }

    /**
     * Gets the current playback position.
     *
     * @param  positionType
     *
     * @see PositionType
     *
     * @return the current position in milliseconds/position
     *
     */
    public long getCurrentPosition(int positionType) {

		if(mPlayerHandle == 0)
		{
			Log.e(TAG, "getCurrentPosition(positionType): mPlayerHandle = 0, return");
			return 0;
		}
        return this.nativeGetCurrentPosition(mPlayerHandle, positionType);
    }

    /**
     * Gets the duration of the file.
     *
     * @return the duration in milliseconds
     *
     */
    public int getDuration() {

	    if(mPlayerHandle == 0)
		{
			Log.e(TAG, "getDuration(): mPlayerHandle = 0, return");
			return 0;
		}
        return this.nativeGetDuration(mPlayerHandle);
    }

    /**
     * Checks whether the MtkMediaPlayer is playing
     *
     * @return true if currently playing, false otherwise
     *
     */
    public boolean isPlaying() {
        return mIsPlaying;
    }

    /**
     *  Pauses playback.
     *
     */
    public void pause() throws IllegalStateException {
    	Log.i(TAG, "pause(): Enter");

        if(mState == STATE_STARTED || mState == STATE_PAUSED){
            mIsPlaying = false;
            this.nativePause(mPlayerHandle);
            mState = STATE_PAUSED;
        }
        else{
			Log.e(TAG, "pause(): state error, current mState=" + mState + ", throw Exception");
            throw new IllegalStateException("Invalid State!");
        }

        Log.i(TAG, "pause(): mState = " + mState);
		Log.i(TAG, "pause(): Leave");
    }

    /**
     *  Prepares the player for playback, synchronously.
     *
     */
    public void prepare() throws IOException, NotSupportException, IllegalStateException {
    	Log.i(TAG, "prepare(): Enter");

		configPlayer(MtkMediaPlayer.PROFILE_MAIN_SVCTX, MtkMediaPlayer.PREPARE_TYPE_SYNC);
		m_Prepare();

		Log.i(TAG, "prepare(): Leave");
    }

    /**
     * Prepares the player for playback, synchronously.
     *
     * @param profile
     *        Profile string for opening a playback control
     *
     */
    public void prepare(String profile) throws IOException, NotSupportException, IllegalStateException {
    	Log.i(TAG, "prepare(String profile): Enter");

		configPlayer(profile, MtkMediaPlayer.PREPARE_TYPE_SYNC);
		m_Prepare();

		Log.i(TAG, "prepare(String profile): Leave");
    }

    /**
     * Prepares the player for playback, asynchronously.
     *
     */
    public void prepareAsync() throws IOException, NotSupportException, IllegalStateException {
    	Log.i(TAG, "prepareAsync(): Enter");

		configPlayer(MtkMediaPlayer.PROFILE_MAIN_SVCTX, MtkMediaPlayer.PREPARE_TYPE_ASYNC);
		m_Prepare();

		Log.i(TAG, "prepareAsync(): Leave");
    }

    /**
     * Prepares the player for playback, asynchronously.
     *
     * @param profile
     *        Profile string for opening a playback control
     *
     */
    public void prepareAsync(String profile) throws IOException, NotSupportException, IllegalStateException {
        Log.i(TAG, "prepareAsync(String profile): Enter");

		configPlayer(profile, MtkMediaPlayer.PREPARE_TYPE_ASYNC);
		m_Prepare();

		Log.i(TAG, "prepareAsync(String profile): Leave");
    }

    /**
     * Releases resources associated with this MtkMediaPlayer object.
     *
     */
    public void release() {
		Log.i(TAG, "release(): Enter");
		Log.i(TAG, "release(): begin mState = " + mState);

        if (mPlayerHandle == 0) {
			Log.e(TAG, "release(): mPlayerHandle = 0, return");
            return;
        }
        this.nativeRelease(mPlayerHandle);
        mState = STATE_END;

		Log.i(TAG, "release(): end State = " + mState);
		Log.i(TAG, "release(): Leave");
    }

    /**
     * Resets the MtkMediaPlayer to its uninitialized state. After calling this method, you will have to initialize it again by setting the data source and calling prepare().
     *
     */
    public void reset() {
    	Log.i(TAG, "reset(): Enter");
		Log.i(TAG, "reset(): begin mState = " + mState);

        mStreamCurPos = 0;
        mDataSource = null;
        mInputStream = null;
        mStreamSize = 0;
        mIsPlaying = false;
        mMillisecond = 0;
        mByteSeekPos = 0;
        mByteSeekPts = 0;
        byteSeekPtsEnable = false;
        mMediaType = DataSourceMetadata.MEDIA_TYPE_UNKNOWN;
        mDataSourceMetadata = null;
        mSeekEnable = true;
        mConfigUrlType = URL_TYPE_UNKNOWN;
        mConfigUrl = null;
        mDataSourceUrl= "";
        mMediaPlayerMode = MEDIAPLAYER_MODE_PULL;
        mPlayerHandle = 0;
        mState = STATE_IDLE;
        this.nativeReset();
        mIsLocalUrlMode = false;

		mIsAudioPlayer = false;

		Log.i(TAG, "reset(): end mState = " + mState);
		Log.i(TAG, "reset(): Leave");
    }

    /**
     * Seeks to specified time position.
     *
     * @param ms
     *        The offset in milliseconds from the start to seek
     *
     */
    public void seekTo(int ms) throws NotSupportException, IllegalStateException {
    	Log.i(TAG, "seekTo(): Enter");
		Log.i(TAG, "seekTo(): begin mState = " + mState + ", ms = "+ ms);

        if(mState == STATE_PREPARED){
            this.mMillisecond = ms;
            Log.i(TAG, "seekTo(): mMillisecond = " + this.mMillisecond);
        }
        else if(mState == STATE_STARTED || mState == STATE_COMPLETED || mState == STATE_PAUSED){
            this.nativeSeekTo(mPlayerHandle, ms);
        }
        else {
			Log.e(TAG, "seekTo(): state error, current mState=" + mState + ", throw Exception");
            throw new IllegalStateException("Invalid State!");
        }

		Log.i(TAG, "seekTo(): end mState = " + mState);
		Log.i(TAG, "seekTo(): Leave");
    }
    /**
     * Sets display area of playback.
     *
     * due to the before error code, this API seems abnormal.
     *
     * @param rect: the rectangle for display
     * but be careful:
     * Rect.x: the left coordinate of rect
     * Rect.y: the top coordinate of rect
     * Rect.right: the width of rect
     * Rect.bottom: the height of rect
     *
     * before using this API, you have to do coordinate convertion except full screen {0, 0, 1000, 1000}:
     * x = (left*1000)/width;
     * y = (top*1000)/height;
     * w = (w *1000)/width;
     * h = (h*1000)/height;
     *
     * you'd better use the API setDisplay(int x, int y, int w, int h),
     * and this API setDisplay(Rect rect) will be removed in future.
     */
    /* 
    public void setDisplay(Rect rect) {
        this.nativeSetDisplay(mPlayerHandle, rect);
    }
*/
    /**
     * Sets display area of playback.
     *
     * x: the left coordinate of display area
     * y: the top coordinate of display area
     * w: the width of display area
     * h: the height of display area
     *
     * before using this API, you have to do coordinate convertion except full screen {0, 0, 1000, 1000}:
     * x = (left*1000)/width;
     * y = (top*1000)/height;
     * w = (w *1000)/width;
     * h = (h*1000)/height;
     *
     */
	public void setDisplay(int x, int y, int w, int h)
	{
		Log.i(TAG, "setDisplay(): Enter");
		Log.i(TAG, "setDisplay(): begin mState = " + mState);
		Log.i(TAG, "setDisplay(): x = " + x + ", y = " + y + ", w = " + w + ", h = " + h);

		if(mPlayerHandle == 0)
		{
			Log.e(TAG, "setDisplay(): mPlayerHandle = 0, return");
			return ;
		}

		this.nativeSetDisplayRect(mPlayerHandle, x, y, w, h);

		Log.i(TAG, "setDisplay(): end mState = " + mState);
		Log.i(TAG, "setDisplay(): Leave");
    }

    /**
     * Register a callback to be invoked when an error has happened during an operation.
     *
     * @param listener
     *        the callback that will be run
     *
     */
    public void setOnErrorListener(OnErrorListener listener) {
        this.mErrorListener = listener;
    }

    /**
     * Register a callback to be invoked when the status of a stream's buffer has changed.
     *
     * @param listener
     *        the callback that will be run
     *
     */
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        this.mUpdateListener = listener;
    }

    /**
     * Register a callback to be invoked when the start of a media source playing.
     *
     * @param listener
     *        the callback that will be run
     *
     */
    public void setOnPlayDoneListener(OnPlayDoneListener listener) {
        this.mPlayDoneListener = listener;
    }

    /**
     * Register a callback to be invoked when the end of a media source has been reached during playback.
     *
     * @param listener
     *        the callback that will be run
     *
     */
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.mCompletionListener = listener;
    }

    /**
     * Register a callback to be invoked when an info/warning is available.
     *
     * @param listener
     *        the callback that will be run
     *
     */
    public void setOnAudioOnlyListener(OnAudioOnlyListener listener) {
        this.mAudioOnlyListener = listener;
    }

    /**
     * Register a callback to be invoked when a seek operation has been completed.
     *
     * @param listener
     *        the callback that will be run
     *
     */
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        this.mSeekListener = listener;
    }

    /**
     * Register a callback to be invoked when the media source is ready for playback.
     *
     * @param listener
     *        the callback that will be run
     *
     */
    public void setOnPreparedListener(OnPreparedListener listener) {
        this.mPreparedListener = listener;
    }

    /**
     * Register a callback to be invoked when the status of current time has changed.
     *
     * @param listener
     *        the callback that will be run
     *
     */
    public void setOnCurTimeUpdateListener(OnCurTimeUpdateListener listener) {
        this.mCurTimeUpdateListener = listener;
    }

    /**
     * Register a callback to be invoked when the status of total time has changed.
     *
     * @param listener
     *        the callback that will be run
     *
     */
    public void setOnTotalTimeUpdateListener(OnTotalTimeUpdateListener listener) {
        this.mTotalTimeUpdateListener = listener;
    }

    /**
     *
     *
     */
    public void setOnStepDoneListener(OnStepDoneListener listener) {
        this.mStepDoneListener = listener;
    }

    /**
     *
     *
     */
    public void setOnEofListener(OnEofListener listener) {
        this.mEofListener = listener;
    }

    /**
     *
     *
     */
    public void setOnPositionUpdateListener(OnPositionUpdateListener listener) {
        this.mPositionUpdateListener = listener;
    }

    /**
     *
     *
     */
    public void setOnSpeedUpdateListener(OnSpeedUpdateListener listener) {
        this.mSpeedUpdateListener = listener;
    }

	/**
     *
     *
     */
    public void setOnRePlayListener(OnRePlayListener listener) {
        this.mReplayListener = listener;
    }

    /**
     * Starts or resumes playback.
     *
     */
    public void start() throws IllegalStateException {
    	Log.i(TAG, "start(): Enter");
		Log.i(TAG, "start(): begin mState = " + mState);

        if(mState == STATE_PREPARED || mState == STATE_PAUSED || mState == STATE_COMPLETED || mState == STATE_STARTED){
            this.nativeStart(mPlayerHandle, mMillisecond);
            mIsPlaying = true;
            mState = STATE_STARTED;
        }
        else {
			Log.e(TAG, "start(): state error, current mState=" + mState + ", throw Exception");
            throw new IllegalStateException("Invalid State!");
        }

		Log.i(TAG, "start(): end mState = " + mState);
		Log.i(TAG, "start(): Leave");
    }

    /**
     * Stops playback after playback has been stopped or paused.
     *
     */
    public void stop() throws IllegalStateException {
    	Log.i(TAG, "stop(): Enter");
		Log.i(TAG, "stop(): begin mState = " + mState);

        if(mState == STATE_ERROR || mState == STATE_PREPARED || mState == STATE_STARTED || mState == STATE_PAUSED ||  mState == STATE_COMPLETED || mState == STATE_STOPPED){

            mIsPlaying = false;
			setInputStreamInValid();
            if(mPlayerHandle!=0)
            {
	        	try
				{
					this.nativeStop(mPlayerHandle);
					this.nativeClose(mPlayerHandle);
					this.closeInputStream();
					m_i4Count_NativeClose_Called ++;
				}catch(Exception e)
				{
	               	this.nativeClose(mPlayerHandle);
					this.closeInputStream();
					m_i4Count_NativeClose_Called ++;
	               	mPlayerHandle = 0;
	                mState = STATE_ERROR;
					Log.e(TAG, "stop(): File stop fail, throw Exception and mState=" + mState);
	                throw new IllegalStateException("File stop fail!");
                }
            }
            mPlayerHandle = 0;
            mMillisecond = 0;
            mState = STATE_STOPPED;
        }
        else{
			Log.e(TAG, "stop(): state error, current mState=" + mState + ", throw Exception");
            throw new IllegalStateException("Invalid State!");
        }

		Log.i(TAG, "stop(): end mState = " + mState);
		Log.i(TAG, "stop(): Leave");
    }

    /**
     *
     *
     */
    public void getMetaDataPrepare() throws IOException, NotSupportException, IllegalStateException {
    	Log.i(TAG, "getMetaDataPrepare(): Enter");

		configPlayer(MtkMediaPlayer.PROFILE_SUB_SVCTX, MtkMediaPlayer.PREPARE_TYPE_SYNC);
		m_Prepare();

		Log.i(TAG, "getMetaDataPrepare(): Leave");
    }

    /**
     *
     *
     */
    public void getMetaDataStop() throws IllegalStateException {
    	Log.i(TAG, "getMetaDataStop(): Enter");
		Log.i(TAG, "getMetaDataStop(): begin mState = " + mState);

        if(mState == STATE_ERROR || mState == STATE_INITIALLIZED || mState == STATE_PREPARING || mState == STATE_PREPARED || mState == STATE_STOPPED){

            if(mPlayerHandle!=0)
            {
            	setInputStreamInValid();

				Log.i(TAG, "getMetaDataStop(): mPlayerHandle!=0, so call nativeClose(mPlayerHandle)");
				this.nativeClose(mPlayerHandle);
				this.closeInputStream();
				m_i4Count_NativeClose_Called ++;
            }
            mPlayerHandle = 0;
            mState = STATE_STOPPED;
        }
        else{
			Log.e(TAG, "getMetaDataStop(): state error, current mState=" + mState + ", throw Exception");
            throw new IllegalStateException("Invalid State!");
        }

		Log.i(TAG, "getMetaDataStop(): end mState = " + mState);
		Log.i(TAG, "getMetaDataStop(): Leave");
    }

    /**
     * Steps some frames.
     *
     * @param amount
     *        The frame number for stepping
     *
     */
    public void step(int amount) throws IllegalStateException {
    	Log.i(TAG, "step(): Enter");
		Log.i(TAG, "step(): mState = " + mState + ", amount = " + amount);

		if(mPlayerHandle == 0)
		{
			Log.e(TAG, "setDisplay(): mPlayerHandle = 0, return");
			return ;
		}

        this.nativeStep(mPlayerHandle, amount);

		Log.i(TAG, "step(): Leave");
    }

    /**
     *
     * the enum of PlayerSpeed.
     *
     */
    public enum PlayerSpeed {
        SPEED_FR_32X, SPEED_FR_16X, SPEED_FR_8X, SPEED_FR_4X, SPEED_FR_3X, SPEED_FR_2X, SPEED_FR_1X, SPEED_FR_1_2X, SPEED_FR_1_3X, SPEED_FR_1_4X, SPEED_FR_1_8X, SPEED_FR_1_16X, SPEED_FR_1_32X, SPEED_ZERO, SPEED_FF_1_32X, SPEED_FF_1_16X, SPEED_FF_1_8X, SPEED_FF_1_4X, SPEED_FF_1_3X, SPEED_FF_1_2X, SPEED_1X, SPEED_FF_2X, SPEED_FF_3X, SPEED_FF_4X, SPEED_FF_8X, SPEED_FF_16X, SPEED_FF_32X,
    }

    /**
     * Set playback speed.
     *
     * @param speed
     *        playback speed
     * @see PlayerSpeed
     *
     */
    public void setSpeed(PlayerSpeed speed) throws NotSupportException {
    	Log.i(TAG, "setSpeed(): Enter");
		Log.i(TAG, "setSpeed(): mState = " + mState + ", speed = " + speed);

		if(mPlayerHandle == 0)
		{
			Log.e(TAG, "setSpeed(): mPlayerHandle = 0, return");
			return ;
		}

        int s = MTKPLYER_CTRL_SPEED_ZERO;
		s = _getSpeed(speed);

        nativeSetSpeed(mPlayerHandle, s);

		Log.i(TAG, "setSpeed(): Leave");
    }

    /**
     * Selects an audio track to be played.
     *
     * @param audioTrack
     *        the audio track number to be played
     *
     */
    public void setAudioTrack(short audioTrack) {
    	Log.i(TAG, "setAudioTrack(): Enter");
		Log.i(TAG, "setAudioTrack(): mState = " + mState + ", audioTrack = " + audioTrack);

		if(mPlayerHandle == 0)
		{
			Log.e(TAG, "setAudioTrack(): mPlayerHandle = 0, return");
			return ;
		}

        nativeSetAudioTrack(mPlayerHandle, audioTrack);

		Log.i(TAG, "setAudioTrack(): Leave");
    }

    /**
     * Selects an subtitle track to display.
     *
     * @param subtitleTrack
     *        the subtitle track number to display
     *
     */
    public void setSubtitleTrack(short subtitleTrack) {
		Log.i(TAG, "setSubtitleTrack(): Enter");
		Log.i(TAG, "setSubtitleTrack(): mState = " + mState + ", subtitleTrack = " + subtitleTrack);

		if(mPlayerHandle == 0)
		{
			Log.e(TAG, "setSubtitleTrack(): mPlayerHandle = 0, return");
			return ;
		}

        nativeSetSubtitleTrack(mPlayerHandle, subtitleTrack);

		Log.i(TAG, "setSubtitleTrack(): Leave");
    }


    /**
     * Sets Subtitle Attribute.
     *
     * @param attribute
     *        the subtitle attribute
     *
     */
    public void setSubtitleAttr(SubtitleAttr attribute) {
    	Log.i(TAG, "setSubtitleAttr(): Enter");
		Log.i(TAG, "setSubtitleTrack(): mState = " + mState);

		if(mPlayerHandle == 0)
		{
			Log.e(TAG, "setSubtitleAttr(): mPlayerHandle = 0, return");
			return ;
		}

        nativeSetSubtitleAttr(mPlayerHandle, attribute);

		Log.i(TAG, "setSubtitleAttr(): Leave");
    }

    /**
     * Selects a program in Transport Stream to be played.
     *
     * @param index
     *        the index of program
     *
     * @return the state of set
     *
     */
    public int setTS(short index) {
    	Log.i(TAG, "setTS(): Enter");
		Log.i(TAG, "setTS(): mState = " + mState + ", index = " + index);

		if(mPlayerHandle == 0)
		{
			Log.e(TAG, "setTS(): mPlayerHandle = 0, return");
			return 0;
		}

		if (mBackupState != STATE_IDLE)
		{
			Log.i(TAG, "setTS(): begin to reset state machine mBackupState = " + mBackupState);
			mState = mBackupState;
			mBackupState = STATE_IDLE;
		}

    	int i4Ret = 0;
		i4Ret = this.nativeSetTS(mPlayerHandle, index);

		Log.i(TAG, "setTS(): Leave");

		return i4Ret;
    }

    /**
     * Sets PCM Codec information.
     *
     * @param pcmMediaInfo
     *        the PCM codec info
     * @see PcmMediaInfo
     *
     */
    public void setPcmMediaInfo(PcmMediaInfo pcmMediaInfo) {
    	Log.i(TAG, "setPcmMediaInfo(): Enter");
		Log.i(TAG, "setPcmMediaInfo(): mState = " + mState);

		if(mPlayerHandle == 0)
		{
			Log.e(TAG, "setPcmMediaInfo(): mPlayerHandle = 0, return");
			return ;
		}

        nativeSetPcmMediaInfo(mPlayerHandle, pcmMediaInfo);

		Log.i(TAG, "setPcmMediaInfo(): Leave");
    }

    /**
     * Sets the size of the file.
     *
     * @param size
     *        the size of the file
     *
     */
    public void setMediaSize(long size) {
    	Log.i(TAG, "setMediaSize(): Enter");
		Log.i(TAG, "setMediaSize(): mState = " + mState + ", size = " + size);

        this.mStreamSize = size;

		Log.i(TAG, "setMediaSize(): Leave");
    }

    /**
     *
     *
     *
     *
     */
    public void setURL(int urlType, String url) {
    	Log.i(TAG, "setURL(): Enter");
		Log.i(TAG, "setURL(): mState = " + mState + ", urlType = " + urlType + ", url = " + url);

        this.mConfigUrlType = urlType;
        this.mConfigUrl = url;

		Log.i(TAG, "setURL(): Leave");
    }

    /**
     *
     *
     *
     *
     */
    public void setByteSeek(long position) {
        Log.i(TAG, "setByteSeek(): Enter");
		Log.i(TAG, "setByteSeek(): mState = " + mState + ", position = " + position);

		if(mPlayerHandle == 0)
		{
			Log.e(TAG, "setByteSeek(): mPlayerHandle = 0, return");
			return ;
		}

        this.nativeSetByteSeek(mPlayerHandle, position);

        Log.i(TAG, "setByteSeek(): Leave");
    }

    /**
     *
     *
     *
     *
     */
    public void setByteSeekPts(long position, long pts) {
    	Log.i(TAG, "setByteSeekPts(): Enter");
		Log.i(TAG, "setByteSeekPts(): mState = " + mState + ", position = " + position + ", pts" + pts);

        if(mState == STATE_PREPARED){
            this.mByteSeekPos = position;
            this.mByteSeekPts = pts;
            this.byteSeekPtsEnable = true;
            Log.e(TAG, "setByteSeekPts(): byteSeekPtsEnable = " + this.byteSeekPtsEnable);
        }
        else{
			if(mPlayerHandle == 0)
			{
				Log.e(TAG, "setByteSeekPts(): mPlayerHandle = 0, return");
				return ;
			}
	        nativeSetByteSeekPts(mPlayerHandle, position, pts);
        }

		Log.i(TAG, "setByteSeekPts(): Leave");
    }

    /**
     *
     *
     *
     *
     */
    public void set3DVideoMode(int videoMode) {
    	Log.i(TAG, "set3DVideoMode(): Enter");
		Log.i(TAG, "set3DVideoMode(): mState = " + mState + ", videoMode = " + videoMode);

		if(mPlayerHandle == 0)
		{
			Log.e(TAG, "set3DVideoMode(): mPlayerHandle = 0, return");
			return ;
		}

        nativeSet3DVideoMode(mPlayerHandle, videoMode);

		Log.i(TAG, "set3DVideoMode(): Leave");
    }


    /**
     * Gets Transport Stream information according to specified length.
     *
     * @param length
     *        the number of bytes to read range
     * @return The Transport Stream information
     */
    public TsInfo getTSInfo(int length)  throws IllegalStateException{
        return nativeGetTSInfo(mPlayerHandle, length);
    }

    /**
     * Gets media information.
     *
     * @return The media information
     *
     */
    public MediaInfo getMediaInfo() {
        return nativeGetMediaInfo(mPlayerHandle);
    }

    public MetaDataInfo getMetaDataInfoInstance(MtkMediaPlayer mediaPlayer) {
        return new MetaDataInfo(mediaPlayer, mPlayerHandle);
    }

    public Object getMetaDataInfo(int handle, int metaType) {
        return nativeGetMetaDataInfo(handle, metaType);
    }

    /**
     * Gets mp3 cover image information.
     *
     * @return The mp3 cover image information
     *
     */
    public Mp3ImageInfo getMp3ImageInfo() {
        return nativeGetMp3ImageInfo(mPlayerHandle);
    }

    /**
     * Gets video track info.
     *
     * @return The video track information
     *
     */
    public VideoTrackInfo getVideoTrackInfo() {
        return nativeGetVideoTrackInfo(mPlayerHandle);
    }

    /**
     * Gets audio track info of the currently playing audio track.
     *
     * @return The audio track information
     *
     */
    public AudioTrackInfo getAudioTrackInfo() {
        return nativeGetAudioTrackInfo(mPlayerHandle);
    }

    /**
     * Gets thumbnail info.
     *
     * @return The thumbnail information
     *
     */
    public byte[] getThumbNailInfo(ThumbNailInfo thumbNailInfo) {
        return (byte[])nativeGetThumbNailInfo(mPlayerHandle, thumbNailInfo);
    }

    /**
     * Gets player status.
     *
     * @return true if currently playing, false otherwise
     *
     */
    public boolean getPlayerStatus() {
        return nativeGetPlayerStatus();
    }

    /**
     * Gets video info.
     *
     * @return The video information
     *
     */
    public VideoInfo getVideoInfo() {
        return nativeGetVideoInfo(mPlayerHandle);
    }

	public boolean canDoTrick(PlayerSpeed speed) {
		Log.i(TAG, "canDoTrick(): mState = " + mState + ", speed = " + speed);

        int s = _getSpeed(speed);

		return nativeCanDoTrick(mPlayerHandle, s);
	}

	public boolean canDoSeek(PlayerSpeed speed) {
		Log.i(TAG, "canDoSeek(): mState = " + mState + ", speed = " + speed);

        int s = _getSpeed(speed);

		return nativeCanDoSeek(mPlayerHandle, s);
	}

	private void configPlayer(String szProfile, int i4PrepareType)
	{
		Log.i(TAG, "setPlayerProfile(): Enter");
		Log.i(TAG, "setPlayerProfile(): szProfile=" + szProfile + " i4PrepareType=" + i4PrepareType);

		this.mPlayerProfile = szProfile;
		this.mPrepareType = i4PrepareType;

		Log.i(TAG, "setPlayerProfile(): Leave");
	}

	/**
     *  Prepares the player for playback, synchronously.
     *
     */
    private void m_Prepare() throws IOException, NotSupportException, IllegalStateException {
    	Log.i(TAG, "m_Prepare(): Enter");
		Log.i(TAG, "m_Prepare(): begin mState = " + mState);

		Log.i(TAG, "m_Prepare():  m_i4Count_NativeOpen_Called=" + m_i4Count_NativeOpen_Called + ", m_i4Count_NativeClose_Called="+ m_i4Count_NativeClose_Called);

        if(mState == STATE_INITIALLIZED || mState == STATE_STOPPED){

        	if((this.mMediaPlayerMode == MEDIAPLAYER_MODE_PULL) && (!mIsLocalUrlMode)){
	            InputStream input = this.mDataSource.newInputStream();
	            if(input == null){
					Log.e(TAG, "m_Prepare(): File Not Found, throw Exception");
	                throw new IOException("File Not Found");
	            }
	            this.mInputStream = input;
				this.setInputStreamValid();
	            if(mStreamSize == 0){
	                mStreamSize = input.available();
	            }
	            Log.i(TAG, "m_Prepare(): mStreamSize = " + mStreamSize);
	            mStreamCurPos = 0;
            }

            if(mDataSourceMetadata != null){
                mMediaType = mDataSourceMetadata.getMediaType();
            }

            Log.i(TAG, "m_Prepare(): mMediaPlayerMode = " + this.mMediaPlayerMode);
            Log.i(TAG, "m_Prepare(): dataSourceMetadata = " + mDataSourceMetadata + "###mediaType = " + mMediaType);

            mPlayerHandle = this.nativeOpen(this.mMediaPlayerMode, this.mPlayerProfile, mMediaType);
            if (mPlayerHandle == 0) {
				Log.e(TAG, "m_Prepare(): NativeOpen Fail, throw Exception");
                throw new IllegalStateException("NativeOpen Fail!");
            }
            this.nativeRegCallback(mPlayerHandle);
            if(mConfigUrlType != URL_TYPE_UNKNOWN && mConfigUrl != null)
            {
                nativeSetURL(mPlayerHandle, mConfigUrlType, mConfigUrl);
            }
            this.nativeSetEngineParam(mPlayerHandle, this.mMediaPlayerMode, this.mPrepareType);
            this.nativePrepare(mPlayerHandle);
            mState = STATE_PREPARED;

			m_i4Count_NativeOpen_Called ++;
        }
        else{
			Log.e(TAG, "m_Prepare(): state error, current State=" + mState + ", throw Exception");
            throw new IllegalStateException("Invalid State!");
        }

		Log.i(TAG, "m_Prepare(): end mState = " + mState);
		Log.i(TAG, "m_Prepare(): Leave");
    }

	private int _getSpeed(PlayerSpeed speed) {
		Log.i(TAG, "getSpeed(): speed = " + speed);

        int s = MTKPLYER_CTRL_SPEED_ZERO;
        switch (speed) {
        case SPEED_FR_32X: {
            s = MTKPLYER_CTRL_SPEED_FR_32X;
            break;
        }
        case SPEED_FR_16X: {
            s = MTKPLYER_CTRL_SPEED_FR_16X;
            break;
        }
        case SPEED_FR_8X: {
            s = MTKPLYER_CTRL_SPEED_FR_8X;
            break;
        }
        case SPEED_FR_4X: {
            s = MTKPLYER_CTRL_SPEED_FR_4X;
            break;
        }
        case SPEED_FR_3X: {
            s = MTKPLYER_CTRL_SPEED_FR_3X;
            break;
        }
        case SPEED_FR_2X: {
            s = MTKPLYER_CTRL_SPEED_FR_2X;
            break;
        }
        case SPEED_FR_1X: {
            s = MTKPLYER_CTRL_SPEED_FR_1X;
            break;
        }
        case SPEED_FR_1_2X: {
            s = MTKPLYER_CTRL_SPEED_FR_1_2X;
            break;
        }
        case SPEED_FR_1_3X: {
            s = MTKPLYER_CTRL_SPEED_FR_1_3X;
            break;
        }
        case SPEED_FR_1_4X: {
            s = MTKPLYER_CTRL_SPEED_FR_1_4X;
            break;
        }
        case SPEED_FR_1_8X: {
            s = MTKPLYER_CTRL_SPEED_FR_1_8X;
            break;
        }
        case SPEED_FR_1_16X: {
            s = MTKPLYER_CTRL_SPEED_FR_1_16X;
            break;
        }
        case SPEED_FR_1_32X: {
            s = MTKPLYER_CTRL_SPEED_FR_1_32X;
            break;
        }
        case SPEED_ZERO: {
            s = MTKPLYER_CTRL_SPEED_ZERO;
            break;
        }
        case SPEED_FF_1_32X: {
            s = MTKPLYER_CTRL_SPEED_FF_1_32X;
            break;
        }
        case SPEED_FF_1_16X: {
            s = MTKPLYER_CTRL_SPEED_FF_1_16X;
            break;
        }
        case SPEED_FF_1_8X: {
            s = MTKPLYER_CTRL_SPEED_FF_1_8X;
            break;
        }
        case SPEED_FF_1_4X: {
            s = MTKPLYER_CTRL_SPEED_FF_1_4X;
            break;
        }
        case SPEED_FF_1_3X: {
            s = MTKPLYER_CTRL_SPEED_FF_1_3X;
            break;
        }
        case SPEED_FF_1_2X: {
            s = MTKPLYER_CTRL_SPEED_FF_1_2X;
            break;
        }
        case SPEED_1X: {
            s = MTKPLYER_CTRL_SPEED_1X;
            break;
        }
        case SPEED_FF_2X: {
            s = MTKPLYER_CTRL_SPEED_FF_2X;
            break;
        }
        case SPEED_FF_3X: {
            s = MTKPLYER_CTRL_SPEED_FF_3X;
            break;
        }
        case SPEED_FF_4X: {
            s = MTKPLYER_CTRL_SPEED_FF_4X;
            break;
        }
        case SPEED_FF_8X: {
            s = MTKPLYER_CTRL_SPEED_FF_8X;
            break;
        }
        case SPEED_FF_16X: {
            s = MTKPLYER_CTRL_SPEED_FF_16X;
            break;
        }
        case SPEED_FF_32X: {
            s = MTKPLYER_CTRL_SPEED_FF_32X;
            break;
        }
        default:
            break;
        }

		return s;
	}

	/**
     * Interface definition of a callback to be invoked when there has been an error during an operation.
     *
     */
    public interface OnErrorListener {
        abstract boolean onError(MtkMediaPlayer mp, int what, int extra);
    }

    /**
     * Interface definition of a callback to be invoked indicating buffering status of a media resource being playing.
     *
     */
    public interface OnBufferingUpdateListener {
        abstract void onBufferingUpdate(MtkMediaPlayer mp, int percent);
    }

    /**
     * Interface definition for a callback to be invoked when the start of a media source playing.
     *
     */
    public interface OnPlayDoneListener {
        /**
         *
         * Called when the start of a media source playing.
         * @param mp
         *           the MtkMediaPlayer that the start of a media source playing
         */
        abstract void onPlayDone(MtkMediaPlayer mp);
    }

    /**
     *  Interface definition for a callback to be invoked when playback of a media source has completed.
     *
     */
    public interface OnCompletionListener {
        abstract boolean onCompletion(MtkMediaPlayer mp, int what, int extra);
    }

    /**
     *
     *
     */
    public interface OnAudioOnlyListener {
        abstract boolean onAudioOnly(MtkMediaPlayer mp, int what, int extra);
    }

    /**
     * Interface definition of a callback to be invoked indicating the completion of a seek operation.
     *
     */
    public interface OnSeekCompleteListener {
        abstract void onSeekComplete(MtkMediaPlayer mp);
    }

    /**
     *
     *
     */
    public interface OnPreparedListener {
        abstract void onPrepared(MtkMediaPlayer mp);
    }

    /**
     *
     *
     */
    public interface OnCurTimeUpdateListener {
        abstract void onCurTimeUpdate(MtkMediaPlayer mp, int what, int extra);
    }

    /**
     *
     *
     */
    public interface OnTotalTimeUpdateListener {
        abstract void onTotalTimeUpdate(MtkMediaPlayer mp, int what, int extra);
    }

    /**
     *
     *
     */
    public interface OnStepDoneListener {
        abstract void onStepDone(MtkMediaPlayer mp, int what, int extra);
    }

    /**
     *
     *
     */
    public interface OnEofListener {
        abstract void onEof(MtkMediaPlayer mp, int what, int extra);
    }

    /**
     *
     *
     */
    public interface OnPositionUpdateListener {
        abstract void onPositionUpdate(MtkMediaPlayer mp, int what, int extra);
    }

    /**
     *
     *
     */
    public interface OnSpeedUpdateListener {
        abstract void onSpeedUpdate(MtkMediaPlayer mp, int what, int extra);
    }

	/**
     *
     *
     */
    public interface OnRePlayListener {
        abstract void onRePlay(MtkMediaPlayer mp, int what, int extra);
    }

	//listener
    private OnErrorListener mErrorListener;
    private OnBufferingUpdateListener mUpdateListener;
    private OnPlayDoneListener mPlayDoneListener;
    private OnCompletionListener mCompletionListener;
    private OnAudioOnlyListener mAudioOnlyListener;
    private OnSeekCompleteListener mSeekListener;
    private OnPreparedListener mPreparedListener;
    private OnCurTimeUpdateListener mCurTimeUpdateListener;
    private OnTotalTimeUpdateListener mTotalTimeUpdateListener;
    private OnStepDoneListener mStepDoneListener;
    private OnEofListener mEofListener;
    private OnPositionUpdateListener mPositionUpdateListener;
    private OnSpeedUpdateListener mSpeedUpdateListener;
	private OnRePlayListener mReplayListener;

    /**
     * Interface definition for DataSource.
     */
    public interface DataSource {
        /**
         * Returns an object of InputStream.
         */
        abstract InputStream newInputStream();
    }

    /**
     * Interface definition for DataSourceMetadata.
     */
    public interface DataSourceMetadata {

        public static final int MEDIA_TYPE_UNKNOWN    = 0;
        public static final int MEDIA_TYPE_AVI        = 1;
        public static final int MEDIA_TYPE_MPEG2_PS   = 2;
        public static final int MEDIA_TYPE_MPEG2_TS   = 3;
        public static final int MEDIA_TYPE_ASF        = 4;
        public static final int MEDIA_TYPE_MKV        = 5;
        public static final int MEDIA_TYPE_OGG        = 6;
        public static final int MEDIA_TYPE_FLAC       = 7;
        public static final int MEDIA_TYPE_APE        = 8;
        public static final int MEDIA_TYPE_VIDEO_ES   = 9;
        public static final int MEDIA_TYPE_AUDIO_ES   = 10;
        public static final int MEDIA_TYPE_MP4        = 11;
        public static final int MEDIA_TYPE_WAV        = 12;
        public static final int MEDIA_TYPE_RM         = 13;
        public static final int MEDIA_TYPE_MTK_P0     = 14;

        abstract int getMediaType();

    }

    /**
     * Interface definition for ThreeDimensionVideoMode.
     */
    public interface ThreeDimensionVideoMode {

        public static final int THREEDIMENSION_VIDEO_TAB_LE_T = 0;
        public static final int THREEDIMENSION_VIDEO_TAB_RE_T = 1;
        public static final int THREEDIMENSION_VIDEO_SBS_LE_L = 2;
        public static final int THREEDIMENSION_VIDEO_SBS_RE_L = 3;
        public static final int THREEDIMENSION_VIDEO_AF_LE_F  = 4;
        public static final int THREEDIMENSION_VIDEO_AF_RE_F  = 5;

    }

    /**
     * Interface definition for Position Type.
     */
    public interface PositionType {

        public static final int MILLISECOND   = 1;
        public static final int POSITION      = 2;

    }

    /* map to CMPB Ctrl value */
    private static final int MTKPLYER_CTRL_SPEED_FR_32X = -3200000;
    private static final int MTKPLYER_CTRL_SPEED_FR_16X = -1600000;
    private static final int MTKPLYER_CTRL_SPEED_FR_8X = -800000;
    private static final int MTKPLYER_CTRL_SPEED_FR_4X = -400000;
    private static final int MTKPLYER_CTRL_SPEED_FR_3X = -300000;
    private static final int MTKPLYER_CTRL_SPEED_FR_2X = -200000;
    private static final int MTKPLYER_CTRL_SPEED_FR_1X = -100000;
    private static final int MTKPLYER_CTRL_SPEED_FR_1_2X = -50000;
    private static final int MTKPLYER_CTRL_SPEED_FR_1_3X = -33333;
    private static final int MTKPLYER_CTRL_SPEED_FR_1_4X = -25000;
    private static final int MTKPLYER_CTRL_SPEED_FR_1_8X = -12500;
    private static final int MTKPLYER_CTRL_SPEED_FR_1_16X = -6250;
    private static final int MTKPLYER_CTRL_SPEED_FR_1_32X = -3125;
    private static final int MTKPLYER_CTRL_SPEED_ZERO = 0;
    private static final int MTKPLYER_CTRL_SPEED_FF_1_32X = 3125;
    private static final int MTKPLYER_CTRL_SPEED_FF_1_16X = 6250;
    private static final int MTKPLYER_CTRL_SPEED_FF_1_8X = 12500;
    private static final int MTKPLYER_CTRL_SPEED_FF_1_4X = 25000;
    private static final int MTKPLYER_CTRL_SPEED_FF_1_3X = 33333;
    private static final int MTKPLYER_CTRL_SPEED_FF_1_2X = 50000;
    private static final int MTKPLYER_CTRL_SPEED_1X = 100000;
    private static final int MTKPLYER_CTRL_SPEED_FF_2X = 200000;
    private static final int MTKPLYER_CTRL_SPEED_FF_3X = 300000;
    private static final int MTKPLYER_CTRL_SPEED_FF_4X = 400000;
    private static final int MTKPLYER_CTRL_SPEED_FF_8X = 800000;
    private static final int MTKPLYER_CTRL_SPEED_FF_16X = 1600000;
    private static final int MTKPLYER_CTRL_SPEED_FF_32X = 3200000;

    private static final int SEEK_BEGIN = 1; // match to IMTK_CTRL_PULL_SEEK_BGN in CMPB
    private static final int SEEK_CUR   = 2; // match to IMTK_CTRL_PULL_SEEK_CUR in CMPB

    private static final int EVENT_ERROR               = 1;// match to IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR in CMPB
    private static final int EVENT_UPDATE              = 2;// match to IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW & IMTK_PB_CTRL_EVENT_GET_BUF_READY in CMPB
    private static final int EVENT_COMPLETION          = 3;// match to IMTK_PB_CTRL_EVENT_EOS in CMPB
    private static final int EVENT_PLAYDONE            = 4;// match to IMTK_PB_CTRL_EVENT_PLAY_DONE in CMPB
    private static final int EVENT_SEEKCOMPLETE        = 5;// match to IMTK_PB_CTRL_EVENT_TIMESEEK_DONE in CMPB
    private static final int EVENT_CUR_TIME_UPDATE     = 6;// match to IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE in CMPB
    private static final int EVENT_TOTAL_TIME_UPDATE   = 7;// match to IMTK_PB_CTRL_EVENT_TOTAL_TIME_UPDATE in CMPB
    private static final int EVENT_STEP_DONE           = 8;// match to IMTK_PB_CTRL_EVENT_STEP_DONE in CMPB
    private static final int EVENT_EOF                 = 9;// match to IMTK_PB_CTRL_EVENT_EOF in CMPB
    private static final int EVENT_POSITION_UPDATE     = 10;// match to IMTK_PB_CTRL_EVENT_POSITION_UPDATE in CMPB
    private static final int EVENT_SPEED_UPDATE        = 11;// match to IMTK_PB_CTRL_EVENT_SPEED_UPDATE in CMPB
    private static final int EVENT_PREPARED            = 12;// match to IMTK_PB_CTRL_EVENT_PREPARE_DONE in CMPB
    private static final int EVENT_REPLAY              = 13;// match to IMTK_PB_CTRL_EVENT_REPLAY in CMPB
    private static final int EVENT_AUDIO_ONLY_SERVICE  = 14;// match to IMTK_PB_CTRL_EVENT_AUDIO_ONLY_SERVICE in CMPB

    private static final int IMTK_PB_CTRL_ERROR_AUDIO_UNPLAYABLE = 1;   ///< audio is unplayable
    private static final int IMTK_PB_CTRL_ERROR_VIDEO_UNPLAYABLE = 2;   ///< video is unplayable

	// native methods is as following:

    private native int nativeOpen(int mediaPlayerMode, String profile, int mediaType);

    private native void nativeClose(int handle);

    private native void nativeRegCallback(int handle);

    private native void nativeSetEngineParam(int handle, int mediaPlayerMode, int prepareType);

    private native int nativeGetDuration(int handle);

    private native long nativeGetCurrentPosition(int handle, int positionType);

    private native void nativeSeekTo(int handle, int ms);

	private native void nativeSetDisplayRect(int handle, int x, int y, int w, int h);

    private native void nativePrepare(int handle);

    private native void nativeStart(int handle, int millisecond);

    private native void nativePause(int handle);

    private native void nativeStop(int handle);

    private native void nativeRelease(int handle);

    private native void nativeReset();

    private native void nativeStep(int handle, int amount);

    private native void nativeSetSpeed(int handle, int speed);

    private native void nativeSetAudioTrack(int handle, short audioTrack);

    private native void nativeSetSubtitleTrack(int handle, short subtitleTrack);

    private native void nativeSetSubtitleAttr(int handle, SubtitleAttr subtitleAttr);

    private native int nativeSetTS(int handle, short index);

    private native void nativeSetURL(int handle, int urlType, String url);

    private native void nativeSetByteSeek(int handle, long position);

    private native void nativeSetByteSeekPts(int handle, long position, long pts);

    private native void nativeSet3DVideoMode(int handle, int videoMode);

    private native TsInfo nativeGetTSInfo(int handle, int length);

    private native MediaInfo nativeGetMediaInfo(int handle);

    private native Object nativeGetMetaDataInfo(int handle, int metaType);

    private native Mp3ImageInfo nativeGetMp3ImageInfo(int handle);

    private native VideoTrackInfo nativeGetVideoTrackInfo(int handle);

    private native AudioTrackInfo nativeGetAudioTrackInfo(int handle);

    private native Object nativeGetThumbNailInfo(int handle, ThumbNailInfo thumbNailInfo);

    private native boolean nativeGetPlayerStatus();

	private native boolean nativeCanDoTrick(int handle, int speed);

	private native boolean nativeCanDoSeek(int handle, int speed);

    private native void nativeSetPcmMediaInfo(int handle, PcmMediaInfo pcmMediaInfo);

    private native VideoInfo nativeGetVideoInfo(int handle);

    private static native void nativeCMPBInit();
}
