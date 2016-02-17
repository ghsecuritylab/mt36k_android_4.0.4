package com.mediatekk.mmpcm.audio;

import com.mediatek.media.NotSupportException;

public interface IPlayback {
	/**
	 * Set data source to mtkpalyer by sepcified path.
	 * 
	 * @param path
	 * @return return true success , return false fail
	 */
	boolean setDataSource(String path);

	/**
	 * Play a audio
	 * 
	 * @throws IllegalStateException
	 */
	void play() throws IllegalStateException;

	/**
	 * Play next audio
	 * 
	 * @throws IllegalStateException
	 */
	void playNext() throws IllegalStateException;

	/**
	 * Play previous audio
	 * 
	 * @throws IllegalStateException
	 */
	void playPrevious() throws IllegalStateException;

	/**
	 * Pause audio
	 * 
	 * @throws IllegalStateException
	 */
	void pause() throws IllegalStateException;

	/**
	 * Stop audio
	 * 
	 * @throws IllegalStateException
	 */
	void stop() throws IllegalStateException;
	/**
	 * Stop a audio to close handle when file not support
	 * 
	 * @throws IllegalStateException
	 */
	//add by xudong for fix cr 384293
	void stopError() throws IllegalStateException ;
	//end
	/**
	 * Release audio
	 * 
	 * @throws IllegalStateException
	 */
	void release() throws IllegalStateException;

	/**
	 * Fast forword for auido
	 * @throws NotSupportException
	 * @throws IllegalStateException
	 */
	void fastForward() throws NotSupportException, IllegalStateException;

	/**
	 * Fast fewind for auido.
	 * @throws NotSupportException
	 * @throws IllegalStateException
	 */
	void fastRewind() throws NotSupportException, IllegalStateException;

	/**
	 * Slow forward for auido.
	 * @throws NotSupportException
	 * @throws IllegalStateException
	 */
	void slowForward() throws NotSupportException, IllegalStateException;

	/**
	 * Slow Rewind for audio
	 * @throws NotSupportException
	 * @throws IllegalStateException
	 */
	void slowRewind() throws NotSupportException, IllegalStateException;

	int getSpeed();

	void setSpeed(int speed);

	boolean isPlaying();

	int getPlayStatus();

	void setPlayMode(int playMode);

	long getPlaybackProgress();

	/**
	 * get total playback time
	 * 
	 * @return
	 */
	long getTotalPlaybackTime();

	/**
	 * Seek to certain time
	 * 
	 * @param time
	 * @return return true if success, return false if fail
	 */
	boolean seekToCertainTime(long time);
/**
 * Get sample rate
 * @return return  sample rate
 */
	int getSampleRate();

	int getSampleRate(String path);
/**
 * Get bit rate 
 * @return
 */
	int getBitRate();

	int getBitRate(String path);
/**
 *  Get audio codec 
 * @return return  audio codec 
 */
	String getAudioCodec();

	String getAudioCodec(String path);
	/**
	 *  Get channel number
	 * @return return channel number
	 */
	String getChannelNumber();

	String getChannelNumber(String path);

	/**
	 * Set file size by the parameters
	 * 
	 * @param size
	 */
	public void setFileSize(long size);

	/**
	 * Get file size
	 * 
	 * @return file size
	 */
	public long getFileSize();

	/**
	 * Get music title
	 * 
	 * @return music title
	 */
	public String getMusicTitle();

	/**
	 * Get music artist
	 * 
	 * @return music artist
	 */
	public String getMusicArtist();

	/**
	 * Get music album
	 * 
	 * @return music album
	 */
	public String getMusicAlbum();

	/**
	 * Get music genre
	 * 
	 * @return music genre
	 */
	public String getMusicGenre();

	/**
	 * Get music year
	 * 
	 * @return music year
	 */
	public String getMusicYear();

	void registerAudioCompletionListener(Object completionListener);

	void unregisterAudioCompletionListener();

	void registerAudioPreparedListener(Object newPreparedListener);

	void unregisterAudioPreparedListener();

	void registerAudioErrorListener(Object newErrorListener);

	void unregisterAudioErrorListener();

	/**
	 * register audio durationUpdate listener
	 * 
	 * @param updateListener
	 */
	public void registerAudioDurationUpdateListener(Object updateListener);

	public void unregisterAudioDurationUpdateListener();
	/**
	 * Register audio speedUpdate listener
	 * 
	 * @param speedUpdateListener
	 */
	public void registerAuiodSpeedUpdateListener(Object speedUpdateListener);
	
	public void unregisterAudioSpeedUpdateListener();
	
	public void registerAduioReplayListener(Object replayListener);
	
	public void unregisterAudioReplayListener();

	public void registerAudioEofListener(Object eofListener);

	public void unregisterAudioEofListener();
	
	//add for music dlna seek action check
	public boolean canSeekCm();
}
