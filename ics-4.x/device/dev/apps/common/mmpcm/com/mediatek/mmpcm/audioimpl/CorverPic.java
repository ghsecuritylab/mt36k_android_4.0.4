package com.mediatek.mmpcm.audioimpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.SmbException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.mediatek.media.Mp3ImageInfo;
import com.mediatek.media.MtkMediaPlayer;
import com.mediatek.media.MtkMediaPlayer.DataSource;
import com.mediatek.mmpcm.Info;
import com.mediatek.mmpcm.MmpTool;
import com.mediatek.mmpcm.audio.ICorverPic;
import com.mediatek.mmpcm.fileimpl.FileConst;
import com.mediatek.mmpcm.fileimpl.MtkFile;
import com.mediatek.netcm.dlna.DLNADataSource;
import com.mediatek.netcm.dlna.DLNAManager;
import com.mediatek.netcm.samba.SambaManager;

public class CorverPic extends Info implements ICorverPic , DataSource {
    private MtkMediaPlayer mtkPlayer;
    private String dataSource;
    private int mSrcType;
    private static CorverPic aCorver = null;
    private boolean thumbLoadStart = false;
    
	private InputStream mInputStream;

	private CorverPic() {
		mtkPlayer = AudioInfo.getInstance().getPlayer();
	}

	public static CorverPic getInstance() {
		if (aCorver == null) {
			synchronized (CorverPic.class) {
				if (aCorver == null) {
					aCorver = new CorverPic();
				}
			}
		}
		return aCorver;
	}
     /**
      * New input stream
      */
	public InputStream newInputStream() {
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
			if(DLNAManager.getInstance().getDLNADataSource(
					dataSource) != null) {
			mInputStream = DLNAManager.getInstance().getDLNADataSource(
					dataSource).newContentInputStream();
			}
		} else {// HTTP
			return null;
		}

		return mInputStream;
	}
	/**
	 * Get audio corver picture by specified source type,path,
	 * @param srcType
	 * @param filepath
	 * @param width
	 * @param height
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Bitmap getAudioCorverPic(int srcType, String filepath, int width,
			int height) throws IllegalArgumentException {

		MmpTool.LOG_DBG("path = " + filepath);

		if (filepath == null) {
			throw new IllegalArgumentException("empty path!");
		}
		if (!(filepath.endsWith("mp3") || filepath.endsWith("MP3"))) {
			MmpTool.LOG_DBG("Not MP3 format audio, return null corver pic");
			return null;
		}

		if (mtkPlayer != null) {

			dataSource = filepath;

			mSrcType = srcType;

			try {
				mtkPlayer.reset();

				mtkPlayer.setDataSource(this);
				//set file size by lei add.
				setFileSize(getFileSize(dataSource));
				
				mtkPlayer.getMetaDataPrepare();

				thumbLoadStart = true;
			} catch (Exception e) {
				MmpTool.LOG_INFO( " getAudioCorverPic() :" + e.toString());
				try {
					mtkPlayer.getMetaDataStop();
				} catch (IllegalStateException e1) {
					e1.printStackTrace();
				}
				closeStream();
				return null;
			}

		Mp3ImageInfo mp3Info = mtkPlayer.getMp3ImageInfo();

		if (mp3Info == null || mp3Info.getLength() == 0) {
			try {
				mtkPlayer.getMetaDataStop();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			}
			closeStream();
			return null;
		} else {
			byte[] thBuffer = mp3Info.getImageData();

			MmpTool.LOG_DBG( "mp3Info.getWidth() = " + mp3Info.getWidth());
			MmpTool.LOG_DBG( "getHeight() = " + mp3Info.getHeight());
			MmpTool.LOG_DBG("getImg_type() = " + mp3Info.getImg_type());

			Bitmap bitMap = BitmapFactory.decodeByteArray(thBuffer, 0,
					thBuffer.length);
			try {
				mtkPlayer.getMetaDataStop();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			}
			closeStream();
			if (bitMap == null) {

				return null;
			}
			int bitWidth = bitMap.getWidth();
			int bitHeight = bitMap.getHeight();
			float scaleWidth = width / (float) bitWidth;
			float scaleHeight = height / (float) bitHeight;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			bitMap = Bitmap.createBitmap(bitMap, 0, 0, bitWidth, bitHeight,
					matrix, true);

			return bitMap;
		}
	}
		return null;
	}
	
	/**
	 * set file size to mtkPlayer.
	 * @param size
	 */
    public void setFileSize(long size){   	
    	if (mtkPlayer != null) {
    		mtkPlayer.setMediaSize(size);
    	}
    }
    
    
	/**
	 * @deprecated
	 * Get file size
	 * @return
	 * @SuppressWarnings("unused")
	 */
	private long getFileSize(){	
		long fileSize = 0;
		String mcurPath = dataSource;
		switch (mSrcType) {
		case FileConst.SRC_DLNA: {
			DLNADataSource dlnaSource = DLNAManager.getInstance()
					.getDLNADataSource(mcurPath);
			if (dlnaSource != null) {
				fileSize = dlnaSource.getContent().getSize();	
				MmpTool.LOG_INFO("getAudioFileSize dlna $$$$$$$$$$$$$$" 
						+ fileSize);
			}
		}
			break;
		case FileConst.SRC_SMB: {
			SambaManager sambaManager = SambaManager.getInstance();
			try {
				fileSize = sambaManager.size(mcurPath);
				MmpTool.LOG_INFO("getAudioFileSize samba $$$$$$$$$$$$$$" 
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
			MtkFile mFile = null;
			if (mcurPath != null) {
				mFile = new MtkFile(mcurPath);
			}
			MmpTool.LOG_INFO("getAudioFileSize = $$$$$$$$$$$$$$" + mcurPath);

			if (mFile == null) {
				fileSize = 0;
				break;
			}
			fileSize = mFile.getFileSize();
			MmpTool.LOG_INFO("getAudioFileSize local $$$$$$$$$$$$$$" 
					+ fileSize);
		}
			break;
			
		default:
			break;
		}
		
		return fileSize;
	}
	/**
     * Close Stream and release resource.
     */
	public void closeStream() {
		try {
			mtkPlayer.getMetaDataStop();
			thumbLoadStart = false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		if (null != mInputStream) {
			MmpTool.LOG_INFO("  input stream is not null");
			try {
				mInputStream.close();
			} catch (IOException e) {
				MmpTool.LOG_INFO(" close input sream " + e.toString());
				
			}

		}
	}
	/**
     * Stop get meta data thumbnail
     */
	public void stopThumbnail() {
		if (mtkPlayer != null && thumbLoadStart == true) {
			try {
				mtkPlayer.getMetaDataStop();
				thumbLoadStart = false;
			} catch (IllegalStateException e) {
				e.printStackTrace();
				thumbLoadStart = false;
				return;
			}
		}
	}
}
