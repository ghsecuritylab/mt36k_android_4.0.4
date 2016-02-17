package com.mediatek.media;

public class MediaInfo {    
    public static final int UNKNOWN      = 0;
    public static final int AVI          = 1;  
    public static final int MPEG2_PS     = 2;
    public static final int MPEG2_TS     = 3;
    public static final int ASF          = 4;    
    public static final int MKV          = 5;    
    public static final int OGG          = 6;    
    public static final int FLAC         = 7;   
    public static final int APE          = 8;    
    public static final int VIDEO_ES     = 9;
    public static final int AUDIO_ES     = 10;
    public static final int MP4          = 11;    
    public static final int WAV          = 12;    
    public static final int RM           = 13;     
    public static final int MTK_P0       = 14; 
        
    private int                        mediaType;
    private int                        duration;               ///< total time(millisecond)
    private long                       size;                   ///< total size of this file, in byte
    private int                        averageBitrate;         ///< average bitrate of this file, in bits per second
    private short                      videoTrackNumber;       ///< number of video tracks
    private short                      audioTrackNumber;       ///< number of audio tracks
    private short                      subtitleTrackNumber;    ///< number of subtitle tracks
    private int                        mTsProgramNum;
    
    public MediaInfo(int mediaType, int duration, long size,
                     int averageBitrate, short videoTrackNumber, 
                     short audioTrackNumber, short subtitleTrackNumber,
                     int tsProgramNum) {
        super();
        this.mediaType = mediaType;
        this.duration = duration;
        this.size = size;
        this.averageBitrate = averageBitrate;
        this.videoTrackNumber = videoTrackNumber;
        this.audioTrackNumber = audioTrackNumber;
        this.subtitleTrackNumber = subtitleTrackNumber;
		this.mTsProgramNum = tsProgramNum;
    }

    public int getMediaType() {
        return mediaType;
    }

    public int getDuration() {
        return duration;
    }

    public long getSize() {
        return size;
    }

    public int getAverageBitrate() {
        return averageBitrate;
    }

    public short getVideoTrackNumber() {
		return videoTrackNumber;
	}

	public short getAudioTrackNumber() {
        return audioTrackNumber;
    }

    public short getSubtitleTrackNumber() {
        return subtitleTrackNumber;
    } 
    
	public int getTsProgramNum() {
		return mTsProgramNum;
	}

}
