package com.mediatek.gamekit;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class GLES2TextureInfo {
	
	private int GLId;
	private int GLWidth;
	private int GLHegiht;
	private int GLColorMode;
	private Bitmap mBitmap;
	private String mPath;
	private int mPathIndex;
	
	public GLES2TextureInfo(int id, int w, int h, int mode){
		GLId = id;
		GLWidth = w;
		GLHegiht = h;
		GLColorMode = mode;
	}
	
	private int getGLId(){
		return GLId;
	}
	
	public int getWidth(){
		return GLWidth;
	}
	
	public int getColorMode(){
		return GLColorMode;
	}
	
	public int getHeight(){
		return GLHegiht;
	}
	
	public Bitmap getBitmap(){
		return mBitmap;
	}
	
	public void setPath(String path){
		mPath = path;
	}
	
	public String getPath(){
		return mPath;
	}
	
	public void setPathIndex(int index){
		mPathIndex = index;
	}
	
	public int getPathIndex(){
		return mPathIndex;
	}
	
	public void drawBitmap(Bitmap bitmap){
		mBitmap = bitmap;
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getGLId());
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		//GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
	}
}
