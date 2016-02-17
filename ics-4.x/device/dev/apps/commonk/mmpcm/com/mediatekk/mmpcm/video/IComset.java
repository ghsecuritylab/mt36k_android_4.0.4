package com.mediatekk.mmpcm.video;

import android.media.AudioManager;

public interface IComset {	
    /**
     * get current zoom type
     * 
     * @param void
     *           
     * @param void
     *                      
     * @return int
     */
	public int getCurZoomType();
    /**
     * get all zoom type
     * 
     * @param void
     *           
     * @param void
     *                      
     * @return int[]
     */
	public int[] getZoomTypes();
    /**
     * change current zoom type
     * 
     * @param type
     *           the type want to set
     * @param void
     *                      
     * @return void
     */
	public void setZoomType(int type);	   
	
	/**
     * imple vedio zoom
     * 
     * @param type
     *           the type want to zoom
     *                      
     * @return int
     */
	public int videoZoom( int videoZoomType );
	
	/**
     * imple get Max Zoom
     * 
     * 
     *                      
     * @return int
     */
	public int getMaxZoom();
	
	
	
}