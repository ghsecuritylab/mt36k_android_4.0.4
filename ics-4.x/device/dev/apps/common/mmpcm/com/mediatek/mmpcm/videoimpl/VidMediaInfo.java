package com.mediatek.mmpcm.videoimpl;

import com.mediatek.mmpcm.video.IVidMediaInfo;

public class VidMediaInfo implements IVidMediaInfo {

    private int mediaType;
    private int duration;
    private long size;
    private int averageBitrate;
    private short audioTrackNumber;
    private short subtitleTrackNumber;

    private int audioCodec;
    private int channelNumber;
    private int sampleRate;
    private int bitrate;

    private int videoCodec;
    private int width;
    private int heigth;
    private int instantBitrate;

    public void setMediaInfo(int mediaType, int duration, long size,
            int averageBitrate, short audioTrackNumber,
            short subtitleTrackNumber) {
        this.mediaType = mediaType;
        this.duration = duration;
        this.size = size;
        this.averageBitrate = averageBitrate;
        this.audioTrackNumber = audioTrackNumber;
        this.subtitleTrackNumber = subtitleTrackNumber;
    }

    public void setVideoTrackInfo(int videoCodec, int width, int heigth,
            int instantBitrate) {
        this.videoCodec = videoCodec;
        this.width = width;
        this.heigth = heigth;
        this.instantBitrate = instantBitrate;
    }

    public void setAudioTrackInfo(int audioCodec, int channelNumber,
            int sampleRate, int bitrate) {
        this.audioCodec = audioCodec;
        this.channelNumber = channelNumber;
        this.sampleRate = sampleRate;
        this.bitrate = bitrate;
    }

    public int getMediaType() {
        return mediaType;
    }

    private int getDuration() {
        return duration;
    }

    private long getSize() {
        return size;
    }

    private int getAverageBitrate() {
        return averageBitrate;
    }

    public short getAudioTrackNumber() {
        return audioTrackNumber;
    }

    public short getSubtitleTrackNumber() {
        return subtitleTrackNumber;
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

    private int getInstantBitrate() {
        return instantBitrate;
    } 
    
    public int getAudioCodec() {
        return audioCodec;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    private int getSampleRate() {
        return sampleRate;
    }

    private int getBitrate() {
        return bitrate;
    }
   
}
