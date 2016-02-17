package com.mediatek.media;

public class VideoInfo {

    private  int   width;
    private  int   height;
    private  byte  ratioWidth;
    private  byte  ratioHeight;
    private  boolean srcAspect;
    
    public VideoInfo(int width, int height, byte ratioWidth, byte ratioHeight,
            boolean srcAspect) {
        super();
        this.width = width;
        this.height = height;
        this.ratioWidth = ratioWidth;
        this.ratioHeight = ratioHeight;
        this.srcAspect = srcAspect;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte getRatioWidth() {
        return ratioWidth;
    }

    public byte getRatioHeight() {
        return ratioHeight;
    }

    public boolean isSrcAspect() {
        return srcAspect;
    }
    
}
