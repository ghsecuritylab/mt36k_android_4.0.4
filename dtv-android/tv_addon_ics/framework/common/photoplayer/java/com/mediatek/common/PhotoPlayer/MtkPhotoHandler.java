package com.mediatek.common.PhotoPlayer;

import java.io.IOException;
import com.mediatek.common.PhotoPlayer.*;
import android.util.Log;
import android.graphics.Rect;



@SuppressWarnings("unused")
public class MtkPhotoHandler{

	final static String TAG = "MtkPhotoHandler";
	
	final static int MTKPHOTOHANDER_EVENT_IMAGE_NEEDDATA    = 0; /* Not used in right now!*/
	final static int MTKPHOTOHANDER_EVENT_IMAGE_ERROR       = 1;
	final static int MTKPHOTOHANDER_EVENT_IMAGE_FINISH      = 2;
	final static int MTKPHOTOHANDER_EVENT_IMAGE_DECODEING   = 3;
	final static int MTKPHOTOHANDER_EVENT_IMAGE_MAX         = 4;
	
	final static int MTKPHOTOHANDER_IMAGE_COLOR_FORMAT_AYCBCR8888 = 0;         ///< AYCbCr display mode, 32 bit per pixel, for OSD
	final static int MTKPHOTOHANDER_IMAGE_COLOR_FORMAT_Y_CBCR422  = 1;          ///< Y/CbCr separate 422 display mode, 16 bit per pixel, for video plane
	final static int MTKPHOTOHANDER_IMAGE_COLOR_FORMAT_ARGB8888   = 2;
	final static int MTKPHOTOHANDER_IMAGE_COLOR_FORMAT_ARGB1555   = 3;
	final static int MTKPHOTOHANDER_IMAGE_COLOR_FORMAT_ARGB565    = 4;    
	final static int MTKPHOTOHANDER_IMAGE_COLOR_FORMAT_ARGB4444   = 5;        
	final static int MTKPHOTOHANDER_IMAGE_COLOR_FORMAT_RGBA8888   = 6;            
	final static int MTKPHOTOHANDER_IMAGE_COLOR_FORMAT_RGB888     = 7;        
	final static int MTKPHOTOHANDER_IMAGE_COLOR_FORMAT_RGB565     = 8;
	final static int MTKPHOTOHANDER_IMAGE_COLOR_FORMAT_MAX        = 9;
	
	final static int MTKPHOTOHANDER_STATUS_OPENED    = 0;
	final static int MTKPHOTOHANDER_STATUS_DECODED   = 1;
	final static int MTKPHOTOHANDER_STATUS_DECODEING = 2;
	final static int MTKPHOTOHANDER_STATUS_ERROR     = 3;
	
	final static int MTKPHOTOHANDER_IMAGE_ROTATE_0        = 0;                     ///<no rotation
	final static int MTKPHOTOHANDER_IMAGE_ROTATE_90       = 1;                    ///<clockwise 90 degrees
	final static int MTKPHOTOHANDER_IMAGE_ROTATE_180      = 2;                   ///<clockwise 180 degrees
	final static int MTKPHOTOHANDER_IMAGE_ROTATE_270      = 3;              ///<clockwise 270 degrees
	final static int MTKPHOTOHANDER_IMAGE_ROTATE_0_FLIP   = 4;                ///<no rotation with flip
	final static int MTKPHOTOHANDER_IMAGE_ROTATE_90_FLIP  = 5;               ///<clockwise 90 degrees with flip
	final static int MTKPHOTOHANDER_IMAGE_ROTATE_180_FLIP = 6;              ///<clockwise 180 degrees with flip
	final static int MTKPHOTOHANDER_IMAGE_ROTATE_270_FLIP = 7;              ///<clockwise 270 degrees with flip
	final static int MTKPHOTOHANDER_IMAGE_ROTATE_MAX      = 8;
	/*
	 * for MPO, JPEG, JPS format, we use image decode 0 
	 * for PNS format, we use image decode 1
	 * */
	final static int IMAGE_DECODE_0                          = 0;
	final static int IMAGE_DECODE_1                          = 1;

	/*
	static {
		System.loadLibrary("MtkPhotoPlayer");
	}
	*/
	
	public MtkPhotoHandler(){
		this.event = MTKPHOTOHANDER_EVENT_IMAGE_MAX;
		this.handle = 0;
	}
	
	public MtkPhotoHandler(String filename){
		if(filename.endsWith("pns") || filename.endsWith("PNS"))
    	{
    		this.imagedecodeid = IMAGE_DECODE_1;
    	}
    	else
    	{
    		this.imagedecodeid = IMAGE_DECODE_0;
    	}
		this.handle = this.nativePhotoOpen(filename,this.imagedecodeid);
    	this.width = this.nativePhotoGetWidth(this.handle);
    	this.height = this.nativePhotoGetHeight(this.handle);
    	this.rotate = this.nativePhotoGetRotation(this.handle);
		this.event = MTKPHOTOHANDER_EVENT_IMAGE_MAX;
	}
	
	/**
	 * Listener for user;
	 */
	private OnEventListener EventListener;
	private int 			event;
	private int             handle;
	private int             width;
	private int             height;
	private int             rotate;
	private int            imagedecodeid;
	
    public interface OnEventListener {
        abstract boolean onEvent(MtkPhotoHandler handle, int event);
    }
    
    /**
     * set handle listener.
     */
    public void setOnEventListener(OnEventListener listener) {
        this.EventListener = listener;
    }
    
    /**
     * Open a MPO file for play.
     */
    public MtkPhotoHandler Open(String filename) throws NotSupportException{
    	if(filename.endsWith("pns") || filename.endsWith("PNS"))
    	{
    		this.imagedecodeid = IMAGE_DECODE_1;
    	}
    	else
    	{
    		this.imagedecodeid = IMAGE_DECODE_0;
    	}
    	this.handle = this.nativePhotoOpen(filename,this.imagedecodeid);
    	this.width = this.nativePhotoGetWidth(this.handle);
    	this.height = this.nativePhotoGetHeight(this.handle);
    	this.rotate = this.nativePhotoGetRotation(this.handle);
    	return this;
    }
    
    /**
     * Close handle.
     */
    public void Close() throws NotSupportException {
    	if (this.handle != 0){
    		this.nativePhotoClose(this.handle);
    	}
    }
    
    /**
     * Decode the specify MPO file.
     * SkBitmap    destination bitmap
     * h           destination height of picture
     * pitch       destination buffer pitch
     
    public void Decode(int SkBitmap)throws NotSupportException {
    	if (this.handle != 0){
    		this.nativePhotoDecode2User(this.handle, SkBitmap);
    	}
    }
    */
    /**
     * Decode the specify MPO file.
     * w           destination width of picture
     * h           destination height of picture
     */
    public void Decode(int w, int h)throws NotSupportException {
    	if (this.handle != 0){
    		this.nativePhotoDecode(this.handle, w, h);
    	}
    }
    
    /**
     * Decode the specify MPO file. width & height is default.
     */
    public void Decode()throws NotSupportException {
    	if (this.handle != 0) {
    		this.nativePhotoDecode(this.handle, 0, 0);
    	}
    }
    
    public void SetRatio(short Ratio)throws NotSupportException {
    	if (this.handle != 0) {
    		this.nativePhotoSetRatio(this.handle, Ratio);
    	}
    }
    
    public int GetPhotoWidth()throws NotSupportException {
    	if (this.handle != 0){
    		return this.width;
    	}
    	return 0;
    }
    
    public int GetPhotoHeight()throws NotSupportException {
    	if (this.handle != 0){
    		return this.height;
    	}
    	return 0;
    }
    
    public int GetPhotoRationInfo()throws NotSupportException {
    	if (this.handle != 0){
    		return this.rotate;
    	}
    	return MTKPHOTOHANDER_IMAGE_ROTATE_0;
    }
    
    private void OnMtkPhotoHandlerEvent(int eEvent) {
		this.event = eEvent;
        Log.v("CaptureEvent", "OnMtkPhotoHandlerEvent## type ==" + eEvent);
		if (this.EventListener != null)
        	this.EventListener.onEvent(this, eEvent);
    }

	public int Play(Rect Region)throws NotSupportException {
    	if (this.GetHandleStatus() == MTKPHOTOHANDER_STATUS_DECODED){
    		return this.nativePlay(this.handle, Region);
    	}
		return 0;
    }
	
    private native int nativePhotoOpen(String filename, int decode);
    private native int nativePhotoClose(int handle);
//    private native int nativePhotoDecode2User(int handle, int SkBitmap);
    private native int nativePhotoDecode(int handle, int w, int h);
    private native int nativePhotoGetWidth(int handle);
    private native int nativePhotoGetHeight(int handle);
    private native int nativePhotoGetRotation(int handle);
    private native int nativePhotoSetRatio(int handle, short Ratio);
    private native int nativePlay(int handle, Rect display_region);
    
	public int GetHandleStatus() {
		// TODO Auto-generated method stub
		if (this.event == MTKPHOTOHANDER_EVENT_IMAGE_FINISH){
    		return MTKPHOTOHANDER_STATUS_DECODED;
    	} else if (this.event == MTKPHOTOHANDER_EVENT_IMAGE_ERROR){
    		return MTKPHOTOHANDER_STATUS_ERROR;
    	}else if (this.event == MTKPHOTOHANDER_EVENT_IMAGE_DECODEING){
    		return MTKPHOTOHANDER_STATUS_DECODEING;
    	}else{
    		return MTKPHOTOHANDER_STATUS_OPENED;
    	}
	}
    
}