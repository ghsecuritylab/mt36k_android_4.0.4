package com.mediatekk.mmpcm.video;

public interface ICapture {
    /**
     * Capture the current video frame
     * 
     * @param filepath
     *            file path
     * @return null
     */    
	public void captureVideo(String filepath);
    /**
     * save the pic to user's path after capture success
     * 
     * @param filepath
     *            file path
     * @param picturename
     *            save name           
     * @return null
     */ 	
	public void saveToPath(String path, String picturename);
    /**
     * abort
     * 
     * @param null
     *            
     * @param null
     *                       
     * @return null
     */ 
	public void abortCapture();
    /**
     * set the capture to power on logo
     * 
     * @param picturename
     *            saved picture named by saveToPath()
     * @param null
     *                       
     * @return null
     */ 
	public void setPowerOnLogo(String picturename);
    /**
     * set background
     * 
     * @param picturename
     *            saved picture named by saveToPath()
     * @param null
     *                       
     * @return null
     */	
	public void setBackground(String picturename);
}