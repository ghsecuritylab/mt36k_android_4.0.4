package com.mediatek.mmpcm;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.SmbException;

import com.mediatek.mmpcm.fileimpl.FileConst;
import com.mediatek.mmpcm.fileimpl.MtkFile;
import com.mediatek.netcm.dlna.DLNADataSource;
import com.mediatek.netcm.dlna.DLNAManager;
import com.mediatek.netcm.samba.SambaManager;

import android.graphics.Bitmap;

public class Info {
	
	public Bitmap getThumbnail(){
		return null;
	}
	
	public MetaData getMetaData(){
		return null;
	}
	
	/**
	 * set file size to mtk player.
	 * @param size
	 */
    public void setFileSize(long size){
    	return;
    }
    
	/**
	 * get file size
	 * 
	 */
	public long getFileSize(String mcurPath){	
		long fileSize = 0;
		MmpTool.LOG_INFO("getFileSize = $$$$$$$$$$$$$$" + mcurPath);
		if (mcurPath == null){
			return fileSize;
		}
		switch (getSrcType()) {
		case FileConst.SRC_DLNA: {
			DLNADataSource dlnaSource = DLNAManager.getInstance()
					.getDLNADataSource(mcurPath);
			if (dlnaSource != null) {
				fileSize = dlnaSource.getContent().getSize();	
				MmpTool.LOG_INFO("getFileSize dlna $$$$$$$$$$$$$$" 
						+ fileSize);
			}
		}
			break;
		case FileConst.SRC_SMB: {
			SambaManager sambaManager = SambaManager.getInstance();
			try {
				fileSize = sambaManager.size(mcurPath);
				MmpTool.LOG_INFO("getFileSize samba $$$$$$$$$$$$$$" 
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
			MtkFile mFile = null;
			//if (mcurPath != null) {
				
				mFile = new MtkFile(mcurPath);
			//}

			if (mFile == null) {
				fileSize = 0;
				break;
			}
			fileSize = mFile.getFileSize();
			MmpTool.LOG_INFO("getFileSize local $$$$$$$$$$$$$$" 
					+ fileSize);
		}
			break;
		default:
			break;
		}
		
		return fileSize;
	}

	public int getSrcType() {		
		return 0;
	}
}
