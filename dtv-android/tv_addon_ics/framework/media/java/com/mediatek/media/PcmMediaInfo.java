package com.mediatek.media;

import com.mediatek.media.MtkMediaPlayer.DataSourceMetadata;

public class PcmMediaInfo implements DataSourceMetadata{

    public static final int AUD_ENC_UNKNOWN    = 0; 
    public static final int AUD_ENC_MPEG       = 1; 
    public static final int AUD_ENC_MP3        = 2; 
    public static final int AUD_ENC_AAC        = 3; 
    public static final int AUD_ENC_DD         = 4; 
    public static final int AUD_ENC_TRUEHD     = 5; 
    public static final int AUD_ENC_PCM        = 6; 
    public static final int AUD_ENC_DTS        = 7; 
    public static final int AUD_ENC_DTS_HD_HR  = 8; 
    public static final int AUD_ENC_DTS_HD_MA  = 9; 
    public static final int AUD_ENC_WMA        = 10;
    public static final int AUD_ENC_COOK       = 11;
    public static final int AUD_ENC_VORBIS     = 12;
    public static final int AUD_ENC_FLAC       = 13;
    public static final int AUD_ENC_MONKEY     = 14;

     
     
    public class AudioPcmInfo {
        
         //PCM type
         public static final int AUD_PCM_TYPE_NORMAL    = 1;
         public static final int AUD_PCM_TYPE_MS_ADPCM  = 2;
         public static final int AUD_PCM_TYPE_IMA_ADPCM = 3;
  
         //Audio channel number
         public static final int AUD_CH_MONO           = 1;  
         public static final int AUD_CH_STEREO         = 2;  
         public static final int AUD_CH_SURROUND_2CH   = 3;  
         public static final int AUD_CH_SURROUND       = 4;  
         public static final int AUD_CH_3_0            = 5;  
         public static final int AUD_CH_4_0            = 6;  
         public static final int AUD_CH_5_0            = 7;  
         public static final int AUD_CH_5_1            = 8;  
         public static final int AUD_CH_7_1            = 9;  

         //Sampling Frequence
         public static final int AUD_SAMPLE_RATE_8K    = 1; 
         public static final int AUD_SAMPLE_RATE_16K   = 2; 
         public static final int AUD_SAMPLE_RATE_32K   = 3; 
         public static final int AUD_SAMPLE_RATE_11K   = 4; 
         public static final int AUD_SAMPLE_RATE_22K   = 5; 
         public static final int AUD_SAMPLE_RATE_44K   = 6; 
         public static final int AUD_SAMPLE_RATE_12K   = 7; 
         public static final int AUD_SAMPLE_RATE_24K   = 8; 
         public static final int AUD_SAMPLE_RATE_48K   = 9; 
         public static final int AUD_SAMPLE_RATE_96K   = 10;
         public static final int AUD_SAMPLE_RATE_192K  = 11;

         //Bits per Sample
         public static final int AUD_PCM_BIT_DEPTH_8   = 1;
         public static final int AUD_PCM_BIT_DEPTH_16  = 2;
         public static final int AUD_PCM_BIT_DEPTH_20  = 3;
         public static final int AUD_PCM_BIT_DEPTH_24  = 4;
         
         private int     pcm_type;      //PCM type
         private int     channelNumber; //Channels
         private int     sampleRate;    //Sampling Frequence
         private short   blockAlign;    //BLock Alignment
         private int     bitsPerSample; //Bits per Sample
         private int     bigEndian;     //Big Endian PCM
        
         /**
          * Constructs an instance with the AudioPcmInfo.
          * 
          * @param pcm_type
          *        PCM type
          * @param channelNumber
          *        Channels
          * @param sampleRate
          *        Sampling Frequence 
          * @param blockAlign
          *        BLock Alignment
          * @param bitsPerSample
          *        Bits per Sample
          * @param bigEndian
          *        Big Endian PCM                         
          */
        public AudioPcmInfo(int pcm_type, int channelNumber, int sampleRate,
                short blockAlign, int bitsPerSample, int bigEndian) {
            super();
            this.pcm_type = pcm_type;
            this.channelNumber = channelNumber;
            this.sampleRate = sampleRate;
            this.blockAlign = blockAlign;
            this.bitsPerSample = bitsPerSample;
            this.bigEndian = bigEndian;
        }

        }
     
     private  int           mediaType = DataSourceMetadata.MEDIA_TYPE_AUDIO_ES; //Media type
     private  int           totalDuration;  //total time(millisecond)            
     private  long          size;           //total size
     private  int           fgSynchronized; //Set this playback instance to be synchronized.
     private  int           audioCodec = AUD_ENC_PCM; //Audio codec
     private  AudioPcmInfo  audioPcmInfo;             //audio codec info
     
     /**
      * Constructs an instance with the PcmMediaInfo.
      * 
      * @param totalDuration
      *        total time(millisecond)   
      * @param size
      *        total size
      * @param fgSynchronized
      *        Set this playback instance to be synchronized                 
      */
     public PcmMediaInfo(int totalDuration, long size, int fgSynchronized) {
        super();
        this.totalDuration = totalDuration;
        this.size = size;
        this.fgSynchronized = fgSynchronized;
    }

     public void setAudioPcmInfo(AudioPcmInfo audioPcmInfo) {
        this.audioPcmInfo = audioPcmInfo;
    }


    public int getMediaType() {     
            return mediaType;
    } 
}
