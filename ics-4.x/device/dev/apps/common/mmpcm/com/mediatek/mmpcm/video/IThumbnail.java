package com.mediatek.mmpcm.video;

import android.graphics.Bitmap;

public interface IThumbnail {
	/**
	 * Get video thumbnail bitmap 
	 * @param srcType
	 * @param filepath
	 * @param width
	 * @param height
	 * @return
	 * @throws IllegalArgumentException
	 */
    public Bitmap getVideoThumbnail(int srcType, String filepath, int width,
            int height) throws IllegalArgumentException;
    
    /**
     * Stop gain thumbnail 
     */
    public void stopThumbnail();
}
