package com.mediatek.mmpcm.audioimpl;

import com.mediatek.media.MtkMediaPlayer;
import com.mediatek.media.NotSupportException;
import com.mediatek.mmpcm.MmpTool;
import com.mediatek.mmpcm.audio.IPlayback;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class PlaybackService extends Service {

    private final LocalBinder mBinder = new LocalBinder();
    private IPlayback mPlayback = new MtkPlayback();
    private AudioBroadcastReceiver audioBroadcastReceiver = new AudioBroadcastReceiver();

    public class LocalBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    };
   
    public class AudioBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("com.mediatek.closeAudio".equals(action) == true) {
                MmpTool.LOG_DBG("onReceive");
                stopSelf();
            }
        }

    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void onCreate() {

        MmpTool.LOG_DBG("onCreate");
        super.onCreate();

        IntentFilter filter = new IntentFilter("com.mediatek.closeAudio");
        registerReceiver(audioBroadcastReceiver, filter);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        MmpTool.LOG_DBG("onStartCommand");

        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    public void onDestroy() {

        MmpTool.LOG_DBG("onDestroy");
        super.onDestroy();
        mPlayback.release();
        mPlayback = null;
        unregisterReceiver(audioBroadcastReceiver);
    }

    public boolean onUnbind(Intent intent) {

        MmpTool.LOG_DBG("onUnbind");
        return super.onUnbind(intent);
    }

    public boolean isPlaying() {
        return mPlayback.isPlaying();
    }

    public int getSpeed() {
        return mPlayback.getSpeed();
    }
    /**
     * set normal play, ff, fr and so on.
     */
    public void setSpeed(int speed){
    	mPlayback.setSpeed(speed);
    }
    public int getPlayStatus() {
        return mPlayback.getPlayStatus();
    }

    public void setPlayMode(int playMode) {
        mPlayback.setPlayMode(playMode);
    }

    public void changePlayback() {
        if (mPlayback != null) {
            mPlayback.release();

            if (mPlayback instanceof MtkPlayback) {
                mPlayback = new Playback();
            } else {
                mPlayback = new MtkPlayback();
            }
        }

    }

    public boolean setDataSource(String path) {
        return mPlayback.setDataSource(path);
    }

    public void play() throws IllegalStateException {
        mPlayback.play();
    }
    public boolean canSeek(){
    	return mPlayback.canSeekCm();
    }

    public void pause() throws IllegalStateException {
        mPlayback.pause();
    }

    public void stop() throws IllegalStateException {
        mPlayback.stop();
    }
  //add by xudong for fix cr 384293
    public void stopError() throws IllegalStateException {
        mPlayback.stopError();
    }
//end
    public void playNext() throws IllegalStateException {
        mPlayback.playNext();
    }

    public void playPrevious() throws IllegalStateException {
        mPlayback.playPrevious();
    }

    public boolean seekToCertainTime(long time) throws NotSupportException {
        return mPlayback.seekToCertainTime(time);
    }

    public long getPlaybackProgress() {

        return mPlayback.getPlaybackProgress();
    }

    public long getTotalPlaybackTime() {

        return mPlayback.getTotalPlaybackTime();
    }

    public void fastForward() throws NotSupportException, IllegalStateException {

        // TODO to be implemented
        mPlayback.fastForward();
    }

    public void fastRewind() throws NotSupportException, IllegalStateException {

        // TODO to be implemented
        mPlayback.fastRewind();
    }

    public void slowForward() throws NotSupportException, IllegalStateException {

        // TODO to be implemented
        mPlayback.slowForward();
    }

    public void slowRewind() throws NotSupportException, IllegalStateException {

        // TODO to be implemented
        mPlayback.slowRewind();
    }

    public int getBitRate() {
        return mPlayback.getBitRate();
    }

    public int getBitRate(String path) {
        return mPlayback.getBitRate(path);
    }

    public int getSampleRate() {
        return mPlayback.getSampleRate();
    }

    public int getSampleRate(String path) {
        return mPlayback.getSampleRate(path);
    }

    public String getAudioCodec() {
        return mPlayback.getAudioCodec();
    }

    public String getAudioCodec(String path) {
        return mPlayback.getAudioCodec(path);
    }

    public String getChannelNumber() {
        return mPlayback.getChannelNumber();
    }

    public String getChannelNumber(String path) {
        return mPlayback.getChannelNumber(path);
    }

    public String getTitle() {
        return mPlayback.getMusicTitle();
    }

    public String getArtist() {
        return mPlayback.getMusicArtist();
    }

    public String getAlbum() {
        return mPlayback.getMusicAlbum();
    }

    public String getGenre() {
        return mPlayback.getMusicGenre();
    }

    public String getYear() {
        return mPlayback.getMusicYear();
    }
/**
 *  Register AudioCompletion Listener
 * @param completionListener
 */
    public void registerAudioCompletionListener(Object completionListener) {
        if (completionListener instanceof MtkMediaPlayer.OnCompletionListener) {
            mPlayback
                    .registerAudioCompletionListener((MtkMediaPlayer.OnCompletionListener) completionListener);
        } else {
            mPlayback
                    .registerAudioCompletionListener((MediaPlayer.OnCompletionListener) completionListener);
        }
    }
/**
 * Unregister AudioCompletion Listener
 */
    public void unregisterAudioCompletionListener() {
        mPlayback.unregisterAudioCompletionListener();
    }
/**
 * Register AudioPrepared Listener
 * @param preparedListener
 */
    public void registerAudioPreparedListener(Object preparedListener) {
        if (preparedListener instanceof MtkMediaPlayer.OnPreparedListener) {
            mPlayback
                    .registerAudioPreparedListener((MtkMediaPlayer.OnPreparedListener) preparedListener);
        } else {
            mPlayback
                    .registerAudioPreparedListener((MediaPlayer.OnPreparedListener) preparedListener);
        }
    }

    public void unregisterAudioPreparedListener() {
        mPlayback.unregisterAudioPreparedListener();
    }

    //updata by hs_haizhudeng
    /**
     * register AudioError Listener
     */
    public void registerAudioErrorListener(
    		Object errorListener) {
        if (errorListener instanceof MtkMediaPlayer.OnErrorListener) {
            mPlayback
                    .registerAudioErrorListener((MtkMediaPlayer.OnErrorListener) errorListener);
        } else {
            mPlayback
                    .registerAudioErrorListener((MediaPlayer.OnErrorListener) errorListener);
        }
    }

    public void unregisterAudioErrorListener() {
        mPlayback.unregisterAudioErrorListener();
    }
    
    public void registerAudioDurationUpdateListener(Object durationListener){
        mPlayback.registerAudioDurationUpdateListener((MtkMediaPlayer.OnTotalTimeUpdateListener) durationListener);
    }   
    
    public void unregisterAudioDurationUpdateListener(){
        mPlayback.unregisterAudioDurationUpdateListener();
    }
    
    public void registerAuidoSpeedUpdateListener(Object speedUpdateListener){
    	mPlayback.registerAuiodSpeedUpdateListener(speedUpdateListener);
    }
    
    public void unregisterAudioSpeedUpdateListener(){
    	mPlayback.unregisterAudioSpeedUpdateListener();
    }
    
    public void registerAudioReplayListener(Object replayListener){
    	mPlayback.registerAduioReplayListener(replayListener);
    }
    
    public void unregisterAudioReplayListener(){
    	mPlayback.unregisterAudioReplayListener();
    }
    public void registerAudioEofListener(Object eofListener){
        mPlayback.registerAudioEofListener((MtkMediaPlayer.OnEofListener) eofListener);
    }
    
    public void unregisterAudioEofListener(){
        mPlayback.unregisterAudioEofListener();
    }
}
