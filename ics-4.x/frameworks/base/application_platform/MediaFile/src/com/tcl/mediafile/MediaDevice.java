package com.tcl.mediafile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

public class MediaDevice implements Serializable {
	
	private static final long serialVersionUID = -1287483947547814576L;
	private static final String TAG = "UsbMediaDevice";
	
	public static final int TYPE_SDCARD		= 0;
	public static final int TYPE_USB		= 1;
	public static final int TYPE_SMB		= 2;
	public static final int TYPE_TEST		= 3;
	public int mType;
	
	public String mPath;
	//public String mDevicePath;
	public String mName = "USB DEVICES";
	public int mMediaCount;
	public List<String> mPartitionList = new ArrayList<String>();
	
	public MediaDevice(String name, String path, int type) {
		if (!path.endsWith("/"))
			path += "/";
		mPath = path;
		mName = name;
		mType = type;
		Log.d(TAG, "create media device: " + mName);
	}
	
	/**添加分区
	 * @param mPartitionPath 分区的路径*/
	public void addmPartition(String partitionPath) {
		Log.d(TAG, "add partition: " + partitionPath);
		mPartitionList.add(partitionPath);
	}
	
	/**@return 返回分区数量*/
	public int partitionCount() {
		return mPartitionList.size();
	}
	
	/**@return 返回分区列表，返回类型为List<String>, String值为分区路径。*/
	public List<String> partitionList() {
		return mPartitionList;
	}
	
	public String path() {
		return mPath;
	}
	
	public String name() {
		return mName;
	}
	
	private String getState() {
		String path;
//		if (mType == TYPE_SDCARD)
//			return Environment.getExternalStorageState();
//		else if (mType == TYPE_USB) {
//			//必须使用 /mnt/sda格式的路径查询。
//			path = mPath.substring(0, 8);
//			Log.d(TAG, ">>>>  USB: " + path);
//			return Environment.getExternalUsbDiskStorageState(path);
//		}
		return "";
	}
	
	/**用于从/proc/mounts文件中获取设备的挂在状态。*/
	public boolean isMounted() {
        Log.d(TAG, toString());
        
//		String externalSt = getState();
//		Log.d(TAG, String.format("MediaDevice %s state: %s", mPath, externalSt));
//		if (externalSt.equals(Environment.MEDIA_MOUNTED)
//				|| externalSt.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
//			return true;
//		} else
//			return false;
        return true;
	}
	
	/**返回对该对象的一个描述。*/
	public String toString() {
		String partitions = "";
		for (String str: mPartitionList) {
			partitions += str;
			partitions += ", ";
		}
		return String.format("MediaDevice (name = %s, path = %s)(partitions: %s)", mName, mPath, partitions);
	}
	
	public String formatToString() {
		return String.format("%s/%d/%s", mName, mType, mPath);
	}
	
	public static MediaDevice createFromString(String s) {
		String[] list = s.split("/", 3);
		if (list.length != 3) {
			Log.d(TAG, "split error: " + s);
			return null;
		}
		String name = list[0];
		int type = Integer.valueOf(list[1]);
		String path = list[2];
		
		MediaDevice md = new MediaDevice(name, path, type);
		return md;
	}
	
	/**获得一个比较码，主要用于设备列表排序。
	 * SD卡始终排在最前。*/
	public int compareCode() {
		if (mName.startsWith("USB")) {
			return mName.charAt(3) - '0';
		}
		else if (mName.equals(MediaDevicesManager.SDCARD_NAME)) {
			return 0;
		}
		else {
			return Integer.MAX_VALUE;
		}
	}
}
