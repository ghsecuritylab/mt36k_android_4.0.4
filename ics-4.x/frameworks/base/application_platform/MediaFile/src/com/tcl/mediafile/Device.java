package com.tcl.mediafile;

import android.os.Parcelable;

/**
 *设备基类
 * */
public abstract class Device implements Parcelable {
	
	public static final String EXTRA_KEY = "com.tcl.device";
	public static final String TYPE_SDCARD = "sdcard";
	public static final String TYPE_USB = "ums";
	public static final String TYPE_SMB = "smb";

	
	
	/**
	 * device type: usb (usb storage), sdcard, smb etc.
	 * */
	abstract public String type();
	
	/**
	 * 获取设备路径
	 * 
	 * @return 返回设备路径
	 * */
	abstract public String path();
	
	/**
	 * 获取设备名称
	 * 
	 * @return 设备名
	 * */
	abstract public String name();
	
	/**
	 * 获取该设备的跟文件系统。反之，通过MediaFile也可以获得Device对象。
	 * 
	 * @return MediaFile
	 * */
	abstract public MediaFile getRootFile();
	
//	/**
//	 * 获取设备图标。
//	 * 
//	 * @return 设备图标
//	 * */
//	public Bitmap getDeviceIcon(Context ctx) {
//		return null;
//	}
	
	abstract public boolean isMounted();
	abstract public String getState();
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("device: {");
		sb.append("type: ").append(type());
		sb.append(", name: ").append(name());
		sb.append(", path: ").append(path());
		sb.append("}");
		return sb.toString();
	}
}
