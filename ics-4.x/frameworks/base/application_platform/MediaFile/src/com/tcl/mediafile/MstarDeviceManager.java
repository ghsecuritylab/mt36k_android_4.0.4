package com.tcl.mediafile;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import com.android.internal.R;

public class MstarDeviceManager extends DeviceManager {
	private static final String TAG = "MstarDeviceManager";
	List<Device> volumList = new ArrayList<Device>();
	private StorageManager mStorageManager;
	private static MstarDeviceManager self = null;
	private static final boolean DEBUG = true;
	private static Context mContext = null;
	
	@Override
	public List<Device> getDevices(Context ctx) {
		volumList.clear();
		
		if (mStorageManager == null) {
			mStorageManager = (StorageManager) ctx.getSystemService(Context.STORAGE_SERVICE);
		}
		StorageVolume[] vlist = mStorageManager.getVolumeList();
		//Log.d(TAG, "ums device count: " + vlist.length);
		if (vlist != null && vlist.length > 0) {
			for (StorageVolume sv: vlist) {
				Device device;
				String sdpath = Environment.getExternalStorageDirectory().getPath();
				if (sv.getPath().equals(sdpath))
					device = new Sdcard();
				else 
					device = new Ums(sv);
				if (device.isMounted())
					volumList.add(device);
			}
		}
		
		if (DEBUG) {
			for (Device device: volumList)
				Log.d(TAG, device.toString());
		}
		
		return volumList;
	}
	
	public static MstarDeviceManager obtinInstance(Context ctx) {
		mContext = ctx;
		if (self == null) {
			self = new MstarDeviceManager();
		}
		return self;
	}
	
	public static String getUmsState(String path) {
		if (mContext == null) {
			throw new IllegalStateException("MstarDeviceManager not init, context is null");
		}
		StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
		return mStorageManager.getVolumeState(path);
	}
	
	public static String getUmsLabel(String path) {
		if (mContext == null) {
			throw new IllegalStateException("MstarDeviceManager not init, context is null");
		}
		StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
		//获取到的label有空格。去掉所有空格；
		String label = null;// = mStorageManager.getVolumeLabel(path);
		if (label != null) {
			Log.d(TAG, "getlabel: " + label + "(" + label.length() + ")");
			label = label.trim();
			
			if (label.equals(""))
				label = null;
		}
		
		return label;
	}
	
	
	public static String getLabel(Device device) {
		//主要用于处理，如果label为null,则返回默认label
		String label = getUmsLabel(device.path());
		if (label == null) {
			if (device.type().equals(Device.TYPE_SDCARD)) {
				return mContext.getString(R.string.mediafile_sdcard);
			}
			else if (device.type().equals(Device.TYPE_USB)) {
				return mContext.getString(R.string.mediafile_disk);
			}
		}
		return label;
	}
}
