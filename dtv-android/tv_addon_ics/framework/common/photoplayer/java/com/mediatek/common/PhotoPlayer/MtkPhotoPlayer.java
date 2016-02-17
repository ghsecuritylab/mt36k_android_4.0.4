package com.mediatek.common.PhotoPlayer;

import java.io.IOException;
import com.mediatek.common.PhotoPlayer.*;
import android.util.Log;
import android.graphics.Rect;



@SuppressWarnings("unused")
public class MtkPhotoPlayer {

	final static String TAG = "MTKPhotoPlayer";
	private int vdpformat;
	private int ScreenWidth;
	private int ScreenHeight;
	static {
		System.loadLibrary("MtkPhotoPlayer");
	}
	public MtkPhotoPlayer(){
		this.vdpformat = MtkPhotoHandler.MTKPHOTOHANDER_IMAGE_COLOR_FORMAT_Y_CBCR422;
		this.ScreenWidth  = this.nativeGetScreenWidth();
		this.ScreenHeight = this.nativeGetScreenHeight();
	}
	
	public MtkPhotoPlayer(int fmt){
/*		this.nativeVideoConnect(true, fmt);*/
		this.vdpformat = fmt;
		this.ScreenWidth  = this.nativeGetScreenWidth();
		this.ScreenHeight = this.nativeGetScreenHeight();
	}
	
	public int ConnetVDP(int fmt)throws NotSupportException {
		this.vdpformat = fmt;
		return this.nativeVideoConnect(true, fmt);
	}
	
	public int ConnetVDP()throws NotSupportException {
		return this.nativeVideoConnect(true, this.vdpformat);
	}
	
	public int DisConnetVDP()throws NotSupportException {
		return this.nativeVideoConnect(false, this.vdpformat);
	}
	
	public int Play(String filename)throws NotSupportException
	{
		if (filename == null)
		{
			return 0;
		}
		
		try {
			MtkPhotoHandler handle = new MtkPhotoHandler(filename);
			int PicWidth  = handle.GetPhotoWidth();
			int PicHeight = handle.GetPhotoHeight();
			int DisplayWidth = 0, DisplayHeight = 0; 
			/* int Rotation = handle.GetPhotoRationInfo();

			
			if (Rotation  == MtkPhotoHandler.MTKPHOTOHANDER_IMAGE_ROTATE_90 ||
					Rotation  == MtkPhotoHandler.MTKPHOTOHANDER_IMAGE_ROTATE_270 ||
					Rotation  == MtkPhotoHandler.MTKPHOTOHANDER_IMAGE_ROTATE_90_FLIP ||
					Rotation  == MtkPhotoHandler.MTKPHOTOHANDER_IMAGE_ROTATE_270_FLIP)
			{
				if (PicHeight  >  this.ScreenWidth || PicWidth > this.ScreenHeight){
					if (PicHeight * this.ScreenHeight > PicWidth * this.ScreenWidth){
						DisplayWidth = this.ScreenWidth;
						DisplayHeight = PicWidth * this.ScreenWidth / PicHeight;
					}else{
						DisplayHeight = this.ScreenHeight;
						DisplayWidth = PicHeight * this.ScreenHeight / PicWidth;
					}
				} else {
					DisplayWidth = PicHeight;
					DisplayHeight = PicWidth;
				}
			}else{ */
			
			if (PicWidth  >  this.ScreenWidth || PicHeight > this.ScreenHeight){
				if (PicWidth * this.ScreenHeight > PicHeight * this.ScreenWidth){
					DisplayWidth = this.ScreenWidth;
					DisplayHeight = PicHeight * this.ScreenWidth / PicWidth;
				}else{
					DisplayHeight = this.ScreenHeight;
					DisplayWidth = PicWidth * this.ScreenHeight / PicHeight;
				}
			} else {
				DisplayWidth = PicWidth;
				DisplayHeight = PicHeight;
			}
			
			//}
			
			//short Ratio   = (short) ((PicWidth * 1000) / PicHeight);

			handle.Decode(DisplayWidth, DisplayHeight);
			if (handle.GetHandleStatus() == MtkPhotoHandler.MTKPHOTOHANDER_STATUS_DECODED)
			{
				Rect rect = new Rect(((this.ScreenWidth - DisplayWidth) / 2), 
									 ((this.ScreenHeight - DisplayHeight) / 2),
									 DisplayWidth + ((this.ScreenWidth - DisplayWidth) / 2),
									 DisplayHeight + ((this.ScreenHeight - DisplayHeight) / 2));
				handle.Play(rect);
			}else{
//				this.DisConnetVDP();
				handle.Close();
				throw (new NotSupportException("Decode error!"));
			}
			handle.Close();
		}catch (NotSupportException e)
		{
//			this.DisConnetVDP();	
			throw(e);
		}
		return 0;
	}
	public MtkPhotoHandler Decode(String filename)throws NotSupportException
	{
		if (filename == null)
		{
			return null;
		}
		
		try {
			MtkPhotoHandler handle = new MtkPhotoHandler(filename);
			int PicWidth  = handle.GetPhotoWidth();
			int PicHeight = handle.GetPhotoHeight();
			int DisplayWidth, DisplayHeight; 

			this.ScreenWidth  = this.nativeGetScreenWidth();
			this.ScreenHeight = this.nativeGetScreenHeight();
			
			if (PicWidth  >  this.ScreenWidth || PicHeight > this.ScreenHeight){
				if (PicWidth * this.ScreenHeight > PicHeight * this.ScreenWidth){
					DisplayWidth = this.ScreenWidth;
					DisplayHeight = PicHeight * this.ScreenWidth / PicWidth;
				}else{
					DisplayHeight = this.ScreenHeight;
					DisplayWidth = PicWidth * this.ScreenHeight / PicHeight;
				}
			} else {
				DisplayWidth = PicWidth;
				DisplayHeight = PicHeight;
			}

			handle.Decode(DisplayWidth, DisplayHeight);
			if (handle.GetHandleStatus() == MtkPhotoHandler.MTKPHOTOHANDER_STATUS_DECODED)
			{
				return handle;
				//do nothing
			}else{
				handle.Close();
				throw (new NotSupportException("Decode error!"));
			}
		}catch (NotSupportException e){
			throw(e);
		}
		//return null;
	}

	public int Display(MtkPhotoHandler mtk_handle)throws NotSupportException
	{
		if (mtk_handle == null)
		{
			return 0;
		}
		
		try {
			int PicWidth  = mtk_handle.GetPhotoWidth();
			int PicHeight = mtk_handle.GetPhotoHeight();
			int DisplayWidth, DisplayHeight; 

			this.ScreenWidth  = this.nativeGetScreenWidth();
			this.ScreenHeight = this.nativeGetScreenHeight();
			
			if (PicWidth  >  this.ScreenWidth || PicHeight > this.ScreenHeight){
				if (PicWidth * this.ScreenHeight > PicHeight * this.ScreenWidth){
					DisplayWidth = this.ScreenWidth;
					DisplayHeight = PicHeight * this.ScreenWidth / PicWidth;
				}else{
					DisplayHeight = this.ScreenHeight;
					DisplayWidth = PicWidth * this.ScreenHeight / PicHeight;
				}
			} else {
				DisplayWidth = PicWidth;
				DisplayHeight = PicHeight;
			}
			
			if (mtk_handle.GetHandleStatus() == MtkPhotoHandler.MTKPHOTOHANDER_STATUS_DECODED)
			{
				Rect rect = new Rect(((this.ScreenWidth - DisplayWidth) / 2), 
									 ((this.ScreenHeight - DisplayHeight) / 2),
									 DisplayWidth + ((this.ScreenWidth - DisplayWidth) / 2),
									 DisplayHeight + ((this.ScreenHeight - DisplayHeight) / 2));
				mtk_handle.Play(rect);
			}else{
				mtk_handle.Close();
				throw (new NotSupportException("Decode error!"));
			}
			mtk_handle.Close();
		}catch (NotSupportException e)
		{	
			throw(e);
		}
		return 0;
	}
	
    private native int nativeVideoConnect(boolean flag, int fmt);
    private native int nativeGetScreenWidth();
    private native int nativeGetScreenHeight();
    
}