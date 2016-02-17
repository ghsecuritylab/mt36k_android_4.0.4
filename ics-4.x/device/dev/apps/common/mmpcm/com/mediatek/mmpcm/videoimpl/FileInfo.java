package com.mediatek.mmpcm.videoimpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.SmbException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.mediatek.media.MetaDataInfo;
import com.mediatek.media.MtkMediaPlayer;
import com.mediatek.media.NotSupportException;
import com.mediatek.media.MtkMediaPlayer.DataSource;
import com.mediatek.mmpcm.Info;
import com.mediatek.mmpcm.MetaData;
import com.mediatek.mmpcm.MmpTool;
import com.mediatek.mmpcm.fileimpl.FileConst;
import com.mediatek.mmpcm.fileimpl.VideoFile;
import com.mediatek.mmpcm.video.IFileInfo;
import com.mediatek.netcm.dlna.DLNADataSource;
import com.mediatek.netcm.dlna.DLNAManager;
import com.mediatek.netcm.samba.SambaManager;


public class FileInfo extends Info implements IFileInfo, DataSource {

    private ContentResolver mResolver;
	private static SharedPreferences videoShare;
	private final String SHARED_NAME = "videoprogress";
	private MtkMediaPlayer mtkPlayer;
	private String dataSource;
	private int mSrcType;
	private static FileInfo vInfo = null;
	private boolean metaLoadStart = false;

	private String[] mVideoID = new String[] { MediaStore.Video.Media._ID };
	private String[] mThumbVideoID = new String[] { MediaStore.Video.Thumbnails.VIDEO_ID };
	private InputStream mInputStream = null;

	private FileInfo(Context context) {
		mResolver = context.getContentResolver();
		videoShare = context.getSharedPreferences(SHARED_NAME,
				Context.MODE_PRIVATE);
		mtkPlayer = new MtkMediaPlayer();
	}

	private FileInfo() {
		mtkPlayer = new MtkMediaPlayer();
	}

	/**
	 * get static FileInfo instance
	 * @return
	 */
	public static FileInfo getInstance() {
		if (vInfo == null) {
			synchronized (FileInfo.class) {
				if (vInfo == null) {
					vInfo = new FileInfo();
				}
			}
		}
		return vInfo;
	}

	/**
	 * gain new input Stream
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
			
			if(null != DLNAManager.getInstance().getDLNADataSource(dataSource)){
			    mInputStream = DLNAManager.getInstance().getDLNADataSource(
					  dataSource).newContentInputStream();	
			}
			
		} else {// HTTP
			return null;
		}

		return mInputStream;
	}

	/**
	 * Get MetaDate object assign
	 * @param path,srcType
	 */
	public MetaData getMetaDataInfo(String path, int srcType)
			throws IllegalArgumentException {
		MmpTool.LOG_DBG("path = " + path);

		MetaData mMetaInfo = new MetaData();

		if (path == null) {
			throw new IllegalArgumentException("empty path!");
		}

		if (mtkPlayer != null) {

			dataSource = path;

			mSrcType = srcType;

			try {
				mtkPlayer.reset();

				mtkPlayer.setDataSource(this);
				
				// DLNA canSeek
				if (mSrcType == FileConst.SRC_DLNA) {
					DLNADataSource dlnaSource = DLNAManager.getInstance()
							.getDLNADataSource(path);
					if (dlnaSource != null) {
						Log.i("xy",
								"dlnaSource.getContent().canSeek()-----------------------------------------"
										+ dlnaSource.getContent().canSeek());
						mtkPlayer.setDataSourceSeekEnable(dlnaSource
								.getContent().canSeek());
					}
				}
				
				//set file size by lei add.
				//setFileSize(getVideoFileSize(path));
				setFileSize(getFileSize(path));
				
				mtkPlayer.getMetaDataPrepare();

			} catch (Exception e) {
				MmpTool.LOG_INFO( " getMetaDataInfo()  :"+e.toString());
				mMetaInfo.setMetaData(null, null, null, null, null, null, null,
						0, 0);
				try {
					mtkPlayer.getMetaDataStop();
					metaLoadStart = false;
				} catch (IllegalStateException e1) {
					e1.printStackTrace();
				}
				closeStream();
				return mMetaInfo;
			}

			metaLoadStart = true;

			MetaDataInfo metaDataInfo = mtkPlayer
					.getMetaDataInfoInstance(mtkPlayer);

//			String mtitle = "";
			StringBuilder mtitle = new StringBuilder("");
			short mtitleASCII[] = metaDataInfo.getTitle();
			for (int i = 0; i < mtitleASCII.length; i++) {
				if (mtitleASCII[i] == 0) {
					break;
				}
				mtitle.append((char)mtitleASCII[i]);
//				mtitle = mtitle + (char) mtitleASCII[i];
			}

//			String mdirector = "";
			StringBuilder mdirector = new StringBuilder("");
			short mdirectorASCII[] = metaDataInfo.getDirector();
			for (int i = 0; i < mdirectorASCII.length; i++) {
				if (mdirectorASCII[i] == 0) {
					break;
				}
//				mdirector = mdirector + (char) mdirectorASCII[i];
				mdirector.append((char) mdirectorASCII[i]);
			}

//			String mcopyright = "";
			StringBuilder mcopyright = new StringBuilder("");
			short mcopyrightASCII[] = metaDataInfo.getCopyright();
			for (int i = 0; i < mcopyrightASCII.length; i++) {
				if (mcopyrightASCII[i] == 0) {
					break;
				}
//				mcopyright = mcopyright + (char) mcopyrightASCII[i];
				mcopyright.append((char) mcopyrightASCII[i]);
			}

//			String myear = "";
			StringBuilder myear = new StringBuilder("");
			short myearASCII[] = metaDataInfo.getYear();
			for (int i = 0; i < myearASCII.length; i++) {
				if (myearASCII[i] == 0) {
					break;
				}
//				myear = myear + (char) myearASCII[i];
				myear.append((char) myearASCII[i]);
			}

//			String mgenre = "";
			StringBuilder mgenre = new StringBuilder("");
			short mgenreASCII[] = metaDataInfo.getGenre();
			for (int i = 0; i < mgenreASCII.length; i++) {
				if (mgenreASCII[i] == 0) {
					break;
				}
//				mgenre = mgenre + (char) mgenreASCII[i];
				mgenre.append((char) mgenreASCII[i]);
			}

//			String martist = "";
			StringBuilder martist = new StringBuilder("");
			short martistASCII[] = metaDataInfo.getArtist();
			for (int i = 0; i < martistASCII.length; i++) {
				if (martistASCII[i] == 0) {
					break;
				}
//				martist = martist + (char) martistASCII[i];
				martist.append((char) martistASCII[i]);
			}

//			String malbum = "";
			StringBuilder malbum = new StringBuilder("");
			short malbumASCII[] = metaDataInfo.getAlbum();
			for (int i = 0; i < malbumASCII.length; i++) {
				if (malbumASCII[i] == 0) {
					break;
				}
//				malbum = malbum + (char) malbumASCII[i];
				malbum.append((char) malbumASCII[i]);
			}

			int mduration = metaDataInfo.getDuration();
			int mbitrate = metaDataInfo.getBitrate();

			mMetaInfo.setMetaData(mtitle.toString(), mdirector.toString(), mcopyright.toString(), myear.toString(), mgenre.toString(),
					martist.toString(), malbum.toString(), mduration, mbitrate);

			try {
				mtkPlayer.getMetaDataStop();
				metaLoadStart = false;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			closeStream();
		}

		return mMetaInfo;
	}
	
	/**
	 * set file size to mtk player.
	 * @param size
	 */
    public void setFileSize(long size){
    	MmpTool.LOG_INFO("File info = $$$$$$$$$$$$$$" + size);
    	if (mtkPlayer != null) {
    		mtkPlayer.setMediaSize(size);
    	}
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

	public int getSrcType(){
		return mSrcType;
	}
	
	private void closeStream() {
		if (null != mInputStream) {
			MmpTool.LOG_INFO( "  input stream is not null");
			try {
				mInputStream.close();
			} catch (IOException e) {
				MmpTool.LOG_INFO(" close input sream " + e.toString());
			} finally {
				mInputStream = null;
			}
		}
	}

	/**
	 * stop meta data
	 */
	public void stopMetaData() {
		if (mtkPlayer != null && metaLoadStart == true) {
			try {
				mtkPlayer.getMetaDataStop();
				metaLoadStart = false;
			} catch (IllegalStateException e) {
				e.printStackTrace();
				metaLoadStart = false;
				return;
			}
		}
	}

	/**
	 * save progress
	 * @param path,progress
	 */
	public void saveProgress(String path, int progress) {
		SharedPreferences.Editor editor = videoShare.edit();

		editor.putInt(path, progress);
		editor.commit();
	}

	/**
	 * get saved progress
	 * @param path
	 */
	public int getSavedProgress(String path) {

		if (videoShare.contains(path)) {
			return videoShare.getInt(path, 0);
		}

		return 0;
	}

	private Cursor getCursor(String path, String[] projection) {
		if (path == null) {
			return null;
		}

		Uri contentUri = MediaStore.Video.Media.getContentUri("external");
		String selection = MediaStore.Video.Media.DATA + "=?";
		String[] selectionArgs = new String[] { path };

		return mResolver.query(contentUri, projection, selection,
				selectionArgs, null);
	}

	/**
	 * @deprecated
	 */
	private Bitmap getThumnail(long thumbId, int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;

		return ThumbnailUtils.extractThumbnail(MediaStore.Video.Thumbnails
				.getThumbnail(mResolver, thumbId,
						MediaStore.Video.Thumbnails.MINI_KIND, options), width,
				height);
	}

	/**
	 * @deprecated
	 */
	public Bitmap getThumbnail(String filepath, int width, int height) {
		Cursor mCursor = getCursor(filepath, mVideoID);
		if (mCursor == null || mCursor.getCount() == 0) {
			return null;
		}

		mCursor.moveToFirst();
		long thumbId = mCursor.getLong(mCursor
				.getColumnIndexOrThrow(MediaStore.Video.Media._ID));

		return getThumnail(thumbId, width, height);
	}
}