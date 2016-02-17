package com.mediatek.mmpcm.video;

public interface IVidMediaInfo {
    /**
     * get video container type of the played file
     * 
     * @return video type,like VideoConst.VIDEO_TYPE_AVI
     */
    public int getMediaType();
    
    /**
     * get audio track number of the played file
     * 
     * @return
     */
    public short getAudioTrackNumber();
    
    /**
     * get subtitle number of the played file
     * 
     * @return
     */
    public short getSubtitleTrackNumber();
    
    /**
     * get video format of the played file
     * 
     * @return video format,like VideoConst.VIDEO_CODEC_MPEG4
     */
    public int getVideoCodec();
    
    /**
     * get the width of the played file
     * 
     * @return
     */
    public int getWidth();
    
    /**
     * get the height of the played file
     * 
     * @return
     */
    public int getHeigth();
    
    /**
     * get audio format of the played file
     * 
     * @return audio format,like VideoConst.AUDIO_CODEC_MP3
     */
    public int getAudioCodec();
    
    /**
     * get audio channel number of the played file
     * 
     * @return
     */
    public int getChannelNumber();
    
    
    /* UI Layer don't call follow API */
    public void setMediaInfo(int mediaType, int duration, long size,
            int averageBitrate, short audioTrackNumber,
            short subtitleTrackNumber);
    public void setVideoTrackInfo(int videoCodec, int width, int heigth,
            int instantBitrate);
    public void setAudioTrackInfo(int audioCodec, int channelNumber,
            int sampleRate, int bitrate);
}
