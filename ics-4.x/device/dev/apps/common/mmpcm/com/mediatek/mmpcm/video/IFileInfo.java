package com.mediatek.mmpcm.video;

import com.mediatek.mmpcm.MetaData;

import android.graphics.Bitmap;
import android.provider.MediaStore;

public interface IFileInfo {

    public MetaData getMetaDataInfo(String path,int srcType);
    /**
     * get thumbnail bmp of played file
     * 
     * @param filepath 
     *           file path
     * @param kind
     *           like MediaStore.Video.Thumbnails.MINI_KIND           
     * @return bmp
     */ 
	public Bitmap getThumbnail(String filepath, int width, int height);	
	public void stopMetaData();
	public void saveProgress(String path,int progress);
	public int getSavedProgress(String path);
}