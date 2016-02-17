package com.mediatek.ui.mmp.model;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.sql.Time;

import jcifs.smb.SmbException;
import android.graphics.Bitmap;

import com.mediatek.mmpcm.MetaData;
import com.mediatek.mmpcm.audioimpl.AudioInfo;
import com.mediatek.mmpcm.audioimpl.CorverPic;
import com.mediatek.mmpcm.fileimpl.FileConst;
import com.mediatek.mmpcm.videoimpl.FileInfo;
import com.mediatek.mmpcm.videoimpl.Thumbnail;
import com.mediatek.netcm.samba.SambaManager;

public class SmbFileAdapter extends FileAdapter {
	private String mPath;
	private static SambaManager sManager = SambaManager.getInstance();
	private AudioInfo mAudioInfo;
	private FileInfo mFileInfo;

	public SmbFileAdapter(String path) {
		mPath = path;
	}

	public SmbFileAdapter(String path, AudioInfo audioInfo, FileInfo fileInfo) {
		this(path);
		mAudioInfo = audioInfo;
		mFileInfo = fileInfo;
	}

	public long getFileSize(){
		long lSize;
		if (this.isFile()) {
			lSize = this.length();
		} else {
			// TODO Get Directory Size
			lSize = 0;
		}
		return lSize;
	}
	
	@Override
	public String getSize() {

		return getSize(getFileSize());
	}
	
	@Override
	public String getTextSize() {
		return getTextSize(getFileSize());
	}
	
	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public String getAbsolutePath() {
		return mPath;
	}

	@Override
	public String getPath() {
		return mPath;
	}

	@Override
	public String getName() {
		return sManager.getFileName(mPath);
	}

	@Override
	public boolean isDirectory() {
		try {
			return sManager.isDir(mPath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SmbException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean isFile() {
		try {
			return sManager.isFile(mPath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SmbException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public String getLastModified() {
		return "-";
	}

	@Override
	public long lastModified() {
		return 0;
	}

	@Override
	public long length() {
		try {
			return sManager.size(mPath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SmbException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return 0;
	}

	@Override
	protected InputStream getInputStream() {
		try {
			return sManager.getFileStream(mPath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SmbException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Bitmap getThumbnail(int width, int height) {
//		if (!isPhotoFile()) {
//			return null;
//		}
//
//		Bitmap bitmap = decodeBitmap(getInputStream(), width, height);
//
//		return bitmap;
		
		Bitmap bitmap = null;
		if (isVideoFile()){
			Thumbnail vThumb = Thumbnail.getInstance();
			bitmap = vThumb.getVideoThumbnail(FileConst.SRC_SMB, 
					this.getPath(), width, height);
			
		} else if (isPhotoFile()){
			bitmap = decodeBitmap(getInputStream(), width, height);
			
		} else if (isAudioFile()){
			CorverPic aCorver =  CorverPic.getInstance();
			bitmap = aCorver.getAudioCorverPic(FileConst.SRC_SMB, 
					this.getPath(), width, height);          
		}
		
		return bitmap;
	}
	
	public void stopThumbnail(){
		if (isVideoFile()){
			Thumbnail vThumb = Thumbnail.getInstance();
			vThumb.stopThumbnail();
			
		} else if (isPhotoFile()){
			stopDecode();
			
		} else if (isAudioFile()){
			CorverPic aCorver =  CorverPic.getInstance();
			aCorver.stopThumbnail();          
		}
		
	}

	@Override
	public String getInfo() {
		String info;

		if (isPhotoFile()) {
			info = assemblyInfos(getName(), getResolution(), getSize());
		} else if (isAudioFile()) {
			MetaData data = mAudioInfo
					.getMetaDataInfo(mPath, FileConst.SRC_SMB);
			if (null == data) {
				return "";
			}
			String title = data.getTitle();
			if (title == null || title.length()<=0) {
				title = getName();
			}
			info = assemblyInfos(title, data.getAlbum(), data.getGenre(),
					data.getYear(), getSize());
		} else if (isVideoFile()) {
			MetaData data = mFileInfo.getMetaDataInfo(mPath, FileConst.SRC_SMB);
			if (null == data) {
				return "";
			}
			String title = data.getTitle();
			if (title == null || title.length()<=0) {
				title = getName();
			}
			info = assemblyInfos(title, data.getYear(),
					setTime(data.getDuration()), getSize());
			mFileInfo.stopMetaData();
		} else {
			info = assemblyInfos(getName(), getLastModified(), getTextSize());
		}

		return info;
	}

	@Override
	public String getResolution() {
		String resolution = getResolution(getInputStream());

		return resolution;
	}

	@Override
	public String getSuffix() {
		return "";
	}
}
