package com.mediatek.media;

public class ThumbNailInfo {
    
	public static final int AYUV_CLUT2     = 0; 
	public static final int AYUV_CLUT4     = 1; 
	public static final int AYUV_CLUT8     = 2; 
	public static final int UYVY_16        = 3; 
	public static final int YUYV_16        = 4; 
	public static final int AYUV_D8888     = 5; 
    public static final int ARGB_CLUT2     = 6; 
    public static final int ARGB_CLUT4     = 7; 
    public static final int ARGB_CLUT8     = 8; 
    public static final int RGB_D565       = 9; 
    public static final int ARGB_D1555     = 10;
    public static final int ARGB_D4444     = 11;
    public static final int ARGB_D8888     = 12;
    public static final int YUV_420_BLK    = 13;
    public static final int YUV_420_RS     = 14;
    public static final int YUV_422_BLK    = 15;
    public static final int YUV_422_RS     = 16;
    public static final int YUV_444_BLK    = 17;
    public static final int YUV_444_RS     = 18;

    private int canvasColormode;
    private int thumbnailWidth;
    private int thumbnailHeight;
    
    public ThumbNailInfo(int canvasColormode, int thumbnailWidth, int thumbnailHeight) {
        super();
        this.canvasColormode = canvasColormode;
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;
    }

    public int getCanvasColormode() {
        return canvasColormode;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    } 

}
