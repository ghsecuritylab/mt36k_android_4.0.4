package com.mediatek.media;

public class VideoTrackInfo {    
    public static final int UNKNOWN        = 0;  
    public static final int MPEG1_2        = 1;      
    public static final int MPEG4          = 2;        
    public static final int H264           = 3;         
    public static final int H263           = 4;         
    public static final int VC1            = 5;          
    public static final int WMV1           = 6;         
    public static final int WMV2           = 7;         
    public static final int WMV3           = 8;         
    public static final int DIVX311        = 9;      
    public static final int RV8            = 10;          
    public static final int RV9_10         = 11;       
    public static final int MJPEG          = 12;        
    public static final int SORENSON_SPARK = 13;
    
    private int     videoCodec;      
    private int     width;           
    private int     heigth;          
    private int     instantBitrate;
     
     
    public VideoTrackInfo(int videoCodec, int width, int heigth, int instantBitrate) {
        super();
        this.videoCodec = videoCodec;
        this.width = width;
        this.heigth = heigth;
        this.instantBitrate = instantBitrate;
    }


    public int getVideoCodec() {
        return videoCodec;
    }


    public int getWidth() {
        return width;
    }


    public int getHeigth() {
        return heigth;
    }


    public int getInstantBitrate() {
        return instantBitrate;
    }  

}
