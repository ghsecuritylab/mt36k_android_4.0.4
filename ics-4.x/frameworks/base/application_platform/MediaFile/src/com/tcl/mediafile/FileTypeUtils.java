package com.tcl.mediafile;

import java.io.File;

public class FileTypeUtils {
	
	private static final String TAG = "SmbVideoUtils";
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int MEDIA_TYPE_AUDIO = 3;
	public static final int MEDIA_TYPE_OTHER = 4;
	
    /*
     * 判断该文件是否是系统文件；
     * */
    public static boolean isSystemFile(String filename) {
    	if (filename.startsWith(".") || filename.endsWith("$/"))
    		return true;
    	File file = new File(filename);
    	if (file.isHidden())
    		return true;
    	return false;
    }
    
    public static boolean isSystemFile(File f) {
    	if (f.isHidden() || f.getName().startsWith(".") || f.getName().endsWith("$/"))
    		return true;
    	else
    		return false;
    }
    
//    public static int mediaType(String filename) {
//    	//filename = filename.toLowerCase();
//    	MimeType.MediaFileType mt = MediaFile.getFileType(filename.toUpperCase());
//    	if (mt == null)
//    		return MEDIA_TYPE_OTHER;
//    	
//		int filetype = MediaFile.getFileType(filename.toUpperCase()).fileType;
//    	if (MediaFile.isVideoFileType(filetype)) {
//    		return MEDIA_TYPE_VIDEO;
//    	}
//    	else if	(MediaFile.isImageFileType(filetype)) {
//    		return MEDIA_TYPE_IMAGE;
//    	}
//    	else if (MediaFile.isAudioFileType(filetype)) {
//    		return MEDIA_TYPE_AUDIO;
//    	}
//    	else 
//    		return MEDIA_TYPE_OTHER;
//    }
}
