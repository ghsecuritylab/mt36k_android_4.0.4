package com.mediatekk.mmpcm.videoimpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import jcifs.smb.SmbException;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.mediatek.media.MtkMediaPlayer;
import com.mediatek.media.NotSupportException;
import com.mediatek.media.ThumbNailInfo;
import com.mediatek.media.MtkMediaPlayer.DataSource;
import com.mediatekk.mmpcm.Info;
import com.mediatekk.mmpcm.MmpTool;
import com.mediatekk.mmpcm.fileimpl.FileConst;
import com.mediatekk.mmpcm.fileimpl.VideoFile;
import com.mediatekk.mmpcm.video.IThumbnail;
import com.mediatekk.netcm.dlna.DLNADataSource;
import com.mediatekk.netcm.dlna.DLNAManager;
import com.mediatekk.netcm.samba.SambaManager;

public class Thumbnail extends Info implements IThumbnail, DataSource {

	private MtkMediaPlayer mtkPlayer;
	private String dataSource;
	private int mSrcType;
	private boolean thumbLoadStart = false;
	private static Thumbnail vThumb = null;
	private InputStream mInputStream;
	private  Integer  mState = VideoConst.THUMANIL_STATUS_STOPED; 
	private Thumbnail() {
		mtkPlayer = new MtkMediaPlayer();
	}

	public static Thumbnail getInstance() {
		if (vThumb == null) {
			synchronized (Thumbnail.class) {
				if (vThumb == null) {
					vThumb = new Thumbnail();
				}
			}
		}
		return vThumb;
	}

	/**
	 * get new input stream
	 */
	public InputStream newInputStream() {
		closeStream();
		if (mSrcType == FileConst.SRC_USB) {
			try {
				mInputStream = new FileInputStream(dataSource);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		} else if (mSrcType == FileConst.SRC_SMB) {
			try {
				mInputStream = SambaManager.getInstance().getSambaDataSource(
						dataSource).newInputStream();
			} catch (SmbException e) {
				e.printStackTrace();
				return null;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return null;
			}
		} else if (mSrcType == FileConst.SRC_DLNA) { // DLNA
			if(null != DLNAManager.getInstance().getDLNADataSource(
					dataSource)){
				mInputStream = DLNAManager.getInstance().getDLNADataSource(
					dataSource).newContentInputStream();
			}
			
		} else {// HTTP
			return null;
		}

		return mInputStream;
	}
	
	/**
	 * get video thumbnail bitmap
	 */
	public Bitmap getVideoThumbnail(int srcType, String filepath, int width,
			int height) throws IllegalArgumentException {

		MmpTool.LOG_DBG("filepath = " + filepath);

		if (filepath == null) {
			throw new IllegalArgumentException("empty filepath!");
		}

		if (mtkPlayer != null) {

			dataSource = filepath;
			mSrcType = srcType;
			try {
				mState = VideoConst.THUMANIL_STATUS_INIT;
				mtkPlayer.reset();
				mtkPlayer.setDataSource(this);
				
				// DLNA canSeek
				if (mSrcType == FileConst.SRC_DLNA) {
					DLNADataSource dlnaSource = DLNAManager.getInstance()
							.getDLNADataSource(filepath);
					if (dlnaSource != null) {
						Log.e("xy",
								"dlnaSource.getContent().canSeek()============================"
										+ dlnaSource.getContent().canSeek());
						mtkPlayer.setDataSourceSeekEnable(dlnaSource
								.getContent().canSeek());
					}
				}
				
				//set file size by lei add.
				//setFileSize(getVideoFileSize(filepath));
				setFileSize(getFileSize(filepath));
				//mtkPlayer.prepare(); /*get thumbnal by specified sub. lei modife*/
				synchronized (mState) {
					if (mState == VideoConst.THUMANIL_STATUS_INIT) {
						mtkPlayer.prepare(MtkMediaPlayer.PROFILE_USB);
						mState = VideoConst.THUMANIL_STATUS_PREPARED;
					}
				}
				thumbLoadStart = true;
			} catch (Exception e) {
				MmpTool.LOG_INFO(" getVideoThumbnail() 109 line :"+e.toString());
				closeStream();
				return null;
			}
		}

		ThumbNailInfo thInfo = new ThumbNailInfo(ThumbNailInfo.RGB_D565,
				VideoConst.THUMBNAIL_WIDTH, VideoConst.THUMBNAIL_HEIGTH);

		byte[] thBuffer = mtkPlayer.getThumbNailInfo(thInfo);

		if (thBuffer == null) {
			stopMetaData();
			closeStream();
			return null;
		} else {
			Bitmap bitmap = Bitmap.createBitmap(VideoConst.THUMBNAIL_WIDTH,
					VideoConst.THUMBNAIL_HEIGTH, Bitmap.Config.RGB_565);

			ByteBuffer buffer = ByteBuffer.wrap(thBuffer);
			bitmap.copyPixelsFromBuffer(buffer);

			stopMetaData();
			closeStream();

			int bitWidth = bitmap.getWidth();
			int bitHeight = bitmap.getHeight() - 2;
			float scaleWidth = width / (float) bitWidth;
			float scaleHeight = height / (float) bitHeight;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitWidth, bitHeight,
					matrix, true);

			return bitmap;
		}
	}
	/**
	 * set file size to mtk player.
	 * @param size
	 */
    public void setFileSize(long size){   	
    	if (mtkPlayer != null) {
    		mtkPlayer.setMediaSize(size);
    	}
    }
    

    /**
     * get src type
     */
	public int getSrcType() {
		
		return mSrcType;
	}

	/**
	 * get file size
	 * 
	 */
/*	private long getVideoFileSize(String mcurPath){	
		long fileSize = 0;
		MmpTool.LOG_INFO("getVideoFileSize = $$$$$$$$$$$$$$" + mcurPath);
		if (mcurPath == null){
			return fileSize;
		}
		switch (mSrcType) {
		case FileConst.SRC_DLNA: {
			DLNADataSource dlnaSource = DLNAManager.getInstance()
					.getDLNADataSource(mcurPath);
			if (dlnaSource != null) {
				fileSize = dlnaSource.getContent().getSize();	
				MmpTool.LOG_INFO("getVideoFileSize dlna $$$$$$$$$$$$$$" 
						+ fileSize);
			}
		}
			break;
		case FileConst.SRC_SMB: {
			SambaManager sambaManager = SambaManager.getInstance();
			try {
				fileSize = sambaManager.size(mcurPath);
				MmpTool.LOG_INFO("getVideoFileSize samba $$$$$$$$$$$$$$" 
						+ fileSize);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (SmbException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
			break;
		case FileConst.SRC_USB: {

			// videopl = PlayList.getPlayList();
			// String path = videopl.getCurrentPath(Const.FILTER_VIDEO);
			VideoFile mVideoFile = null;
			if (mcurPath != null) {
				
				mVideoFile = new VideoFile(mcurPath);
			}

			if (mVideoFile == null) {
				fileSize = 0;
				break;
			}
			fileSize = mVideoFile.getFileSize();
			MmpTool.LOG_INFO("getVideoFileSize local $$$$$$$$$$$$$$" 
					+ fileSize);
		}
			break;
		default:
			break;
		}
		
		return fileSize;
	}*/
	
	private synchronized void stopMetaData() {
		try {
			if (mState == VideoConst.THUMANIL_STATUS_STOPED) {
				return;
			}
			mtkPlayer.getMetaDataStop();
			thumbLoadStart = false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} finally {
			mState = VideoConst.THUMANIL_STATUS_STOPED;
		}
	}

	private void closeStream() {
		if (null != mInputStream) {
			MmpTool.LOG_INFO("  input stream is not null  path="+dataSource);
			try {
				mInputStream.close();
			} catch (IOException e) {
				MmpTool.LOG_INFO( " close input sream " + e.toString());
			} finally {
				mInputStream = null;
			}
		}else{
		MmpTool.LOG_INFO("  input stream is  null   path="+dataSource);
		}
	}

	/**
	 * stop thumbnail
	 */
	public void stopThumbnail() {
		if (mtkPlayer != null && thumbLoadStart == true) {
			try {
				thumbLoadStart = false;
				stopMetaData();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				thumbLoadStart = false;
				return;
			}
		}
	}
	public void resetThumbnail() {
		synchronized (mState) {
			mState = VideoConst.THUMANIL_STATUS_STOPED;
		}
	}

	public int getThumanilState() {
		synchronized (mState) {
			return mState;
		}
	}
}
