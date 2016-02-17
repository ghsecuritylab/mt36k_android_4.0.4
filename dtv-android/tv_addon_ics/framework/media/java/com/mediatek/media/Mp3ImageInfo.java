package com.mediatek.media;

public class Mp3ImageInfo {
    
    public static final int UNKNOWN        = 0;
    public static final int PNG            = 1;
    public static final int JPG            = 2;

    private int img_type;
    private int width;
    private int height;
    private int length;
    private byte imageData[];
    
    
    public Mp3ImageInfo(int img_type, int width, int height, int length,
            byte[] imageData) {
        super();
        this.img_type = img_type;
        this.width = width;
        this.height = height;
        this.length = length;
        this.imageData = imageData;
    }
    public int getImg_type() {
        return img_type;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getLength() {
        return length;
    }
    public byte[] getImageData() {
        return imageData;
    }

}
