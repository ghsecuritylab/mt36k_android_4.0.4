package com.mediatek.media;

public class AudioTrackInfo {
    /** Audio Codec */    
    public static final int UNKNOWN    = 0 ;
    public static final int MPEG       = 1 ;
    public static final int MP3        = 2 ;
    public static final int AAC        = 3 ;
    public static final int DD         = 4 ;
    public static final int TRUEHD     = 5 ;
    public static final int PCM        = 6 ;
    public static final int DTS        = 7 ;
    public static final int DTS_HD_HR  = 8 ;
    public static final int DTS_HD_MA  = 9 ;
    public static final int WMA        = 10;
    public static final int COOK       = 11;
    public static final int VORBIS     = 12;
    public static final int FLAC       = 13;
    public static final int MONKEY     = 14;
    
    
    /* channel number */
    public static final int CH_MONO            = 1 ;
    public static final int CH_STEREO         = 2 ;
    public static final int CH_SURROUND_TWO_CH   = 3 ;
    public static final int CH_SURROUND     = 4 ;
    public static final int CH_THREE_ZERO           = 5 ;
    public static final int CH_FOUR_ZERO            = 6 ;
    public static final int CH_FIVE_ZERO            = 7 ;
    public static final int CH_FIVE_ONE           = 8 ;
    public static final int CH_SEVEN_ONE           = 9 ;
    
    
    /* sample Rate */
    public static final int RATE_8K         = 1 ; 
    public static final int RATE_16K        = 2 ; 
    public static final int RATE_32K        = 3 ; 
    public static final int RATE_11K        = 4 ; 
    public static final int RATE_22K        = 5 ; 
    public static final int RATE_44K        = 6 ; 
    public static final int RATE_12K        = 7 ; 
    public static final int RATE_24K        = 8 ; 
    public static final int RATE_48K        = 9 ; 
    public static final int RATE_96K        = 10; 
    public static final int RATE_192K       = 11; 
 
    private int audioCodec;
    private int channelNumber;
    private int sampleRate;
    private int bitrate;
    
    public AudioTrackInfo(int audioCodec, int channelNumber, int sampleRate, int bitrate) {
        super();
        this.audioCodec = audioCodec;
        this.channelNumber = channelNumber;
        this.sampleRate = sampleRate;
        this.bitrate = bitrate;
    }

    public int getAudioCodec() {
        return audioCodec;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getBitrate() {
        return bitrate;
    }
    

}
