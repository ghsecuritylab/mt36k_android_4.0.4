package com.mediatek.mmpcm.audioimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.SmbException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.mediatek.media.MetaDataInfo;
import com.mediatek.media.MtkMediaPlayer;
import com.mediatek.media.MtkMediaPlayer.DataSource;
import com.mediatek.mmpcm.Info;
import com.mediatek.mmpcm.MetaData;
import com.mediatek.mmpcm.MmpTool;
import com.mediatek.mmpcm.audio.IAudioInfo;
import com.mediatek.mmpcm.fileimpl.FileConst;
import com.mediatek.mmpcm.fileimpl.MtkFile;
import com.mediatek.mmpcm.mmcimpl.Const;
import com.mediatek.mmpcm.mmcimpl.PlayList;
import com.mediatek.netcm.dlna.DLNADataSource;
import com.mediatek.netcm.dlna.DLNAManager;
import com.mediatek.netcm.samba.SambaManager;


public class AudioInfo extends Info implements IAudioInfo, DataSource {
	private static final Uri mArtworkUri = Uri
			.parse("content://media/external/audio/albumart");

	private static final String TAG = "AudioInfo";

	private ContentResolver mResolver;
	private Bitmap mBitmap;
	private MtkMediaPlayer mtkPlayer;
	private String dataSource;
	private int mSrcType;
	private static AudioInfo aInfo = null;
	private boolean metaLoadStart = false;

	private String[] mAlbumId = new String[] { MediaStore.Audio.Media.ALBUM_ID };
	private String[] mAudioId = new String[] { MediaStore.Audio.Media._ID };

	private InputStream mInputStream;

	private AudioInfo(Context context) {
		mResolver = context.getContentResolver();
		mtkPlayer = new MtkMediaPlayer();
		mBitmap = null;
	}

	private AudioInfo() {
		mtkPlayer = new MtkMediaPlayer();
		mBitmap = null;
	}
	
	public MtkMediaPlayer getPlayer()
	{
		return mtkPlayer;
	}

	public static AudioInfo getInstance() {
		if (aInfo == null) {
			synchronized (AudioInfo.class) {
				if (aInfo == null) {
					aInfo = new AudioInfo();
				}
			}
		}
		return aInfo;
	}

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
	 * According to the parameters for the meta data info
	 * 
	 * @param path 
	 * @param srcType, specified source typle
	 * @return return meta data info
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
		
				//set file size by lei add.
				setFileSize(getFileSize(dataSource));
				
				mtkPlayer.getMetaDataPrepare();

			} catch (Exception e) {
				MmpTool.LOG_INFO( " getMetaDataInfo()  :" + e.toString());
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

			String mtitle = "";
			StringBuilder temp = new StringBuilder();
			short mtitleASCII[] = metaDataInfo.getTitle();
			for (int i = 0; i < mtitleASCII.length; i++) {
				if (mtitleASCII[i] == 0) {
					break;
				}
				temp.append((char) mtitleASCII[i]);
			}
			mtitle = temp.toString();
			
			String mdirector = "";
			temp.delete(0, temp.length());
			short mdirectorASCII[] = metaDataInfo.getDirector();
			for (int i = 0; i < mdirectorASCII.length; i++) {
				if (mdirectorASCII[i] == 0) {
					break;
				}
				temp.append((char) mdirectorASCII[i]);
			}
			mdirector = temp.toString();

			String mcopyright = "";
			temp.delete(0, temp.length());
			short mcopyrightASCII[] = metaDataInfo.getCopyright();
			for (int i = 0; i < mcopyrightASCII.length; i++) {
				if (mcopyrightASCII[i] == 0) {
					break;
				}
				temp.append((char) mcopyrightASCII[i]);
			}
			mcopyright = temp.toString();

			String myear = "";
			temp.delete(0, temp.length());
			short myearASCII[] = metaDataInfo.getYear();
			for (int i = 0; i < myearASCII.length; i++) {
				if (myearASCII[i] == 0) {
					break;
				}
				temp.append((char) myearASCII[i]);
			}
			myear = temp.toString();

			String mgenre = "";
			temp.delete(0, temp.length());
			short mgenreASCII[] = metaDataInfo.getGenre();
			for (int i = 0; i < mgenreASCII.length; i++) {
				if (mgenreASCII[i] == 0) {
					break;
				}
				temp.append((char) mgenreASCII[i]);
			}
			mgenre = temp.toString();

			String martist = "";
			temp.delete(0, temp.length());
			short martistASCII[] = metaDataInfo.getArtist();
			for (int i = 0; i < martistASCII.length; i++) {
				if (martistASCII[i] == 0) {
					break;
				}
				temp.append((char) martistASCII[i]);
			}
			martist = temp.toString();

			String malbum = "";
			temp.delete(0, temp.length());
			short malbumASCII[] = metaDataInfo.getAlbum();
			for (int i = 0; i < malbumASCII.length; i++) {
				if (malbumASCII[i] == 0) {
					break;
				}
				temp.append((char) malbumASCII[i]);
			}
			malbum = temp.toString();

			int mduration = metaDataInfo.getDuration();
			int mbitrate = metaDataInfo.getBitrate();

			mMetaInfo.setMetaData(mtitle, mdirector, mcopyright, myear, mgenre,
					martist, malbum, mduration, mbitrate);

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
	 * set file size to mtkPlayer.
	 * @param size
	 */
	public  void setFileSize(long size){   	
    	if (mtkPlayer != null) {
    		mtkPlayer.setMediaSize(size);
    	}
    }
    
    
	@Override
	public int getSrcType() {
		
		return mSrcType;
	}

	/**
	 * @deprecated
	 * Get file size
	 * @return
	 * @SuppressWarnings("unused")
	 */
	public long getFileSize(){	
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
	
	private void closeStream() {
		if (null != mInputStream) {
			MmpTool.LOG_INFO("  input stream is not null");
			try {
				mInputStream.close();
			} catch (IOException e) {
				MmpTool.LOG_INFO( " close input sream " + e.toString());
			} finally {
				mInputStream = null;
			}
		}
	}

	/**
	 * Stop get meta data
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
	 * @deprecated
	 */
	public void setAlbumArtwork(Bitmap artwork) {
		mBitmap = artwork;
	}

	/**
	 * @deprecated
	 */
	public void setAlbumArtwork(String path) {
		mBitmap = BitmapFactory.decodeFile(path);
	}

	/**
	 * According to the parameters for the get albumArt work
	 * 
	 * @return albumArt work
	 * @deprecated
	 */
	public Bitmap getAlbumArtwork() {

		File file = PlayList.getPlayList().getCurrentFile(Const.FILTER_AUDIO);
		if (file == null) {
			return null;
		}

		return getAlbumArtwork(file.getPath());
	}


	/**
	 * According to the parameters for the get albumArt work
	 * 
	 * @param path
	 * @return return  albumArt work
	 * @deprecated
	 */
	public Bitmap getAlbumArtwork(String path) {

		Cursor mCursor = getCursor(path, mAlbumId);
		if (mCursor == null || mCursor.getCount() == 0) {
			return null;
		}

		mCursor.moveToFirst();
		long albumId = mCursor.getLong(0);

		return getArtwork(albumId);
	}

	private Cursor getCursor(String path, String[] projection) {
		if (path == null) {
			return null;
		}

		Uri contentUri = MediaStore.Audio.Media.getContentUriForPath(path);
		String selection = MediaStore.Audio.Media.DATA + "=?";
		String[] selectionArgs = new String[] { path };

		return mResolver.query(contentUri, projection, selection,
				selectionArgs, null);
	}

	/**
	 * @deprecated
	 */
	private Bitmap getArtwork(long albumId) {
		Uri uri = ContentUris.withAppendedId(mArtworkUri, albumId);
		if (uri != null) {
			InputStream in = null;

			try {
				in = mResolver.openInputStream(uri);
			} catch (FileNotFoundException e) {

                // TODO The album artwork file doesn't exist
                // should be replaced by default bitmap

                return mBitmap;
            }

			return BitmapFactory.decodeStream(in, null, null);
		}

		return mBitmap;

	}

	/**
	 * @deprecated
	 * @SuppressWarnings("unused")
	 */
	private Bitmap getArtworkFromFile(long albumId) {
		return null;
	}

	/**
	 * @deprecated
	 * @SuppressWarnings("unused")
	 */
	private Bitmap getDefaultArtwork() {
		return null;
	}

}
