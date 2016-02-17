package com.mediatekk.mmpcm.threedimen.photoimpl;

import com.mediatekk.mmpcm.Info;
import com.mediatekk.mmpcm.MetaData;

public class FileInfo extends Info{
	static private FileInfo mInfo;
	public static FileInfo getInstance() {
		if (mInfo == null) {
			synchronized (FileInfo.class) {
				if (mInfo == null) {
					mInfo = new FileInfo();
				}
			}
		}
		return mInfo;
	}
	
	public MetaData getMetaDataInfo(){
		return null;
	}
	
}
