package com.mediatekk.mmpcm.photo;

import com.mediatekk.mmpcm.photoimpl.PhotoUtil;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface IImageshow {

	// Iphotoinfo
	public int getOrientation();

	public int getPheight();

	public int getPwidth();

	public String getName();

	public String getSize();

	public String getMake();

	public String getModel();

	public String getFlash();

	public String getWhiteBalance();

//	public String setPhotoFrameImage();

	// Iplay
	public PhotoUtil curShow();

	public PhotoUtil getNext();

	public PhotoUtil getPre();
	
	public PhotoUtil autoPlayNext();

	public void setPause(boolean blean);

	public void setPlay(boolean blean);

	public int getStatusPlay();

	public void setDuration(int interval);
	
	public int getDuration();


	// Izoom
	public int getZoomOutSize();

	public float getZoomInSize();

	public Bitmap rightRotate(Bitmap bitmap);
	
	public Bitmap leftRotate(Bitmap bitmap);

	public void Zoom(ImageView image, int inOrOut, Bitmap bitmap, float size);

	// Imove
	public Bitmap moveImage();

	
}
