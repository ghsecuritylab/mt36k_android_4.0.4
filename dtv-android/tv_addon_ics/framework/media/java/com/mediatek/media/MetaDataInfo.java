package com.mediatek.media;

public class MetaDataInfo {
    
    /* map to CMPB Meta Type */
    public static final int META_TYPE_INVAL        = 0; 
    public static final int META_TYPE_TITLE        = 1; 
    public static final int META_TYPE_DIRECTOR     = 2; 
    public static final int META_TYPE_COPYRIGHT    = 3; 
    public static final int META_TYPE_YEAR         = 4; 
    public static final int META_TYPE_DATE         = 5; 
    public static final int META_TYPE_GENRE        = 6; 
    public static final int META_TYPE_DURATION     = 7; 
    public static final int META_TYPE_SIZE         = 8; 
    public static final int META_TYPE_ARTIST       = 9; 
    public static final int META_TYPE_ALBUM        = 10;
    public static final int META_TYPE_BITRATE      = 11;
    public static final int META_TYPE_PROTECT      = 12;
    public static final int META_TYPE_CREATE_TIME  = 13;
    public static final int META_TYPE_ACCESS_TIME  = 14;
    public static final int META_TYPE_MODIFY_TIME  = 15;
    public static final int META_TYPE_RESOLUTION   = 16;
    public static final int META_TYPE_NEXT_TITLE   = 17;
    public static final int META_TYPE_NEXT_ARTIST  = 18;
    public static final int META_TYPE_FRAME_RATE   = 19;

    private int  handle;
    MtkMediaPlayer mediaPlayer;
    public MetaDataInfo(MtkMediaPlayer mediaPlayer, int handle) {
        super();
        this.handle = handle;
        this.mediaPlayer = mediaPlayer;
    }

    public short[] getTitle() {
        
        Object obj = mediaPlayer.getMetaDataInfo(handle, META_TYPE_TITLE);
        return (short[])obj;
                
    }
    
    public short[] getDirector() {
        
        Object obj = mediaPlayer.getMetaDataInfo(handle, META_TYPE_DIRECTOR);
        return (short[])obj;
        
    }
    
    public short[] getCopyright() {
        
        Object obj = mediaPlayer.getMetaDataInfo(handle, META_TYPE_COPYRIGHT);
        return (short[])obj;
        
    }
    
    public short[] getYear() {
        
        Object obj = mediaPlayer.getMetaDataInfo(handle, META_TYPE_YEAR);
        return (short[])obj;
        
    }
    
    public short[] getGenre() {
        
        Object obj = mediaPlayer.getMetaDataInfo(handle, META_TYPE_GENRE);
        return (short[])obj;
        
    }
    
    public short[] getArtist() {
        
        Object obj = mediaPlayer.getMetaDataInfo(handle, META_TYPE_ARTIST);
        return (short[])obj;
        
    }
    
    public short[] getAlbum() {
        
        Object obj = mediaPlayer.getMetaDataInfo(handle, META_TYPE_ALBUM);
        return (short[])obj;
        
    }
    
    public int getDuration() {
        
        Object obj = mediaPlayer.getMetaDataInfo(handle, META_TYPE_DURATION);
        return Integer.parseInt(obj.toString());
        
    }
    
    public int getBitrate() {
        
        Object obj = mediaPlayer.getMetaDataInfo(handle, META_TYPE_BITRATE);
        return Integer.parseInt(obj.toString());
        
    }
    
    public int[] getFrameRate() {
        
        Object obj = mediaPlayer.getMetaDataInfo(handle, META_TYPE_FRAME_RATE);
        return (int[])obj;
        
    }

    
}
