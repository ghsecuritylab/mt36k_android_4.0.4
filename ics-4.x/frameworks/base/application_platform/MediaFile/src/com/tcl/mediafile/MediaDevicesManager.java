package com.tcl.mediafile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.android.internal.R;

public class MediaDevicesManager {
	
	private List<MediaDevice> mUMDList = new ArrayList<MediaDevice>();
	private static final String TAG = "LocalMovie - MediaDevicesManager";
	private static MediaDevicesManager mInstance = null;
	public static String SDCARD_NAME = "SD Card";
	
	/*static {
		//汉字“卡”的utf8编码
		byte[] kas = {(byte)0xe5, (byte)0x8d, (byte)0xa1};
		sSdcardStr = "SD" + new String(kas);
	}*/
	
	private MediaDevicesManager() {
		freshDeviceList();
	}
	
	public void freshDeviceList() {
		mUMDList.clear();

		
		/*String test = "卡";
		byte[] tbyte = test.getBytes();
		for (byte b: tbyte) {
			Log.d(TAG, String.format("%02x", b));
		}*/
		
		mUMDList.add(new MediaDevice(SDCARD_NAME, "/mnt/sdcard", MediaDevice.TYPE_SDCARD));
		mUMDList.addAll(getExtraUsbStorage());

		List<MediaDevice> mUMDListNow = new ArrayList<MediaDevice>();
		//删除没有挂载的设备。
		for (int i = 0; i < mUMDList.size(); i++) {
			Log.d(TAG, "delete  --> " + mUMDList.get(i).toString());
			if (mUMDList.get(i).isMounted()) {
				MediaDevice deviceNow = mUMDList.get(i);
				if(deviceNow.mPartitionList.size() == 1){
					deviceNow.mPath = deviceNow.mPartitionList.get(0);
				}
				mUMDListNow.add(deviceNow);
			}
		}
		mUMDList.clear();
		mUMDList = mUMDListNow;
		sort(mUMDList);
		Log.d(TAG , toString());
	}
	
	public static MediaDevicesManager obtinInstance(Context ctx) {
		if (mInstance == null) {
			mInstance = new MediaDevicesManager();
			SDCARD_NAME = ctx.getResources().getString(R.string.mediafile_sdcard);
		}
		return mInstance;
	}
	
	public int devicesCount() {
		return mUMDList.size();
	}
	
	public MediaDevice getDevice(int id) {
		if (id >= devicesCount())
			return null;
		return mUMDList.get(id);
	}
	
	public List<MediaDevice> getDeviceList() {
		freshDeviceList();
		return mUMDList;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("MediaDevicesManager: \n");
		for (MediaDevice md : mUMDList) {
			sb.append("                ");
			sb.append(md.toString());
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public List<MediaDevice> getExtraUsbStorage_1() {
		 List<MediaDevice> list = new ArrayList<MediaDevice>();
		 
		 InputStream is;
	        StringBuffer buffer = new StringBuffer();
		    String line;        								// 用来保存每行读取的内容
			try {
				is = new FileInputStream("/proc/mounts");
			    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			    line = reader.readLine();       				// 读取第一行
			    String[] a;
			    String devicepath = null;
			    while (line != null) {          				// 如果 line 为空说明读完了
			        //System.out.println(line);
			    	Log.d(TAG, line);
				    a = line.split(" ");
				    devicepath = null;
				    if (a.length > 2)
				    	devicepath= a[1];
				    
			        if (devicepath.startsWith("/mnt/sd") && !devicepath.startsWith("/mnt/sdcard")) {
			        	String[] paths = devicepath.split("/");
			        	String name, path;
			        	
		        		name = paths[2];
			        	name = friendlyName(name);
		        		path = "/mnt/"+ paths[2] + "/";
		        		Log.d(TAG, String.format("device: %s , %s, %s  has mounted.", devicepath, name, path));
		        	
			        	MediaDevice md = listHas(list, path);
			        	if (md == null) {
			        		md = new MediaDevice(name, path, MediaDevice.TYPE_USB);
			        		md.addmPartition(devicepath);
				        	list.add(md);
			        	}
			        	else {
			        		md.addmPartition(devicepath);
			        	}
			        	Log.d(TAG, md.toString());
			        }
				    line = reader.readLine();  			 		// 读取下一行
			    }
		        is.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	//Log.d(TAG, String.format("device: %s is not mounted.", path));
		 return list;
	}
	
	public List<MediaDevice> getExtraUsbStorage_2() {
		 List<MediaDevice> list = new ArrayList<MediaDevice>();
		 
		 InputStream is;
	        StringBuffer buffer = new StringBuffer();
		    String line;        								// 用来保存每行读取的内容
			try {
				is = new FileInputStream("/proc/mounts");
			    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			    line = reader.readLine();       				// 读取第一行
			    String[] a;
			    String devicepath = null;
			    while (line != null) {          				// 如果 line 为空说明读完了
			        //System.out.println(line);
			    	Log.d(TAG, line);
				    a = line.split(" ");
				    devicepath = null;
				    if (a.length > 2)
				    	devicepath= a[1];
				    
			        if (devicepath.startsWith("/mnt/usb/")) {
			        	String[] paths = devicepath.split("/");
			        	String name, path;
			        	
		        		name = paths[3];
//			        	name = friendlyName(name);
		        		Log.d(TAG, String.format("device: %s , %s, %s  has mounted.", devicepath, name, devicepath));
		        		MediaDevice md = new MediaDevice(name, devicepath, MediaDevice.TYPE_USB);
			        	list.add(md);

//			        	MediaDevice md = listHas(list, path);
//			        	if (md == null) {
//			        		md = new MediaDevice(name, path, MediaDevice.TYPE_USB);
//			        		md.addmPartition(devicepath);
//				        	list.add(md);
//			        	}
//			        	else {
//			        		md.addmPartition(devicepath);
//			        	}
			        	Log.d(TAG, md.toString());
			        }
				    line = reader.readLine();  			 		// 读取下一行
			    }
		        is.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	//Log.d(TAG, String.format("device: %s is not mounted.", path));
		 return list;
	}
	
	public List<MediaDevice> getExtraUsbStorage() {
		return getExtraUsbStorage_2();
	}

	
	public List<MediaDevice> getExtraUsbStorage_0() {
		 List<MediaDevice> list = new ArrayList<MediaDevice>();
		 
		 InputStream is;
	        StringBuffer buffer = new StringBuffer();
		    String line;        								// 用来保存每行读取的内容
			try {
				is = new FileInputStream("/proc/mounts");
			    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			    line = reader.readLine();       				// 读取第一行
			    String[] a;
			    String devicepath = null;
			    while (line != null) {          				// 如果 line 为空说明读完了
			        System.out.println(line);
				    a = line.split(" ");
				    devicepath = null;
				    if (a.length > 2)
				    	devicepath= a[1];
				    
			        if (devicepath.startsWith("/mnt/sd") && !devicepath.startsWith("/mnt/sdcard")) {
			        	String name = devicepath.substring(5, 8);
			        	name = friendlyName(name);
			        	String path = devicepath.substring(0, 9);
			        	Log.d(TAG, String.format("device: %s , %s, %s  has mounted.", devicepath, name, path));
		        	
			        	//FIXME desert!!!
			        	MediaDevice md = listHas(list, path);
			        	if (md == null) {
				        	list.add(new MediaDevice(name, path, MediaDevice.TYPE_USB));
			        	}
			        	else {
			        		md.addmPartition(path);
			        	}
			        }
				    line = reader.readLine();  			 		// 读取下一行
			    }
		        is.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	//Log.d(TAG, String.format("device: %s is not mounted.", path));
		 return list;
	}
	
	private MediaDevice listHas(List<MediaDevice> list, String path) {
		for (MediaDevice md : list) {
			Log.d(TAG, md.path() + "  <>  " + path);
    		if (md.path().equals(path))
    			return md;
    	}
		return null;
	}

	private String friendlyName(String src) {
		if (src.equals(SDCARD_NAME)) {
			return SDCARD_NAME;
		}
		else if (src.startsWith("sd") && src.length() == 3) {
			char s = src.toCharArray()[2];
			return "USB" + (s - 'a' + 1);
		}
		else
			return src;
	}
	
	public static void sort(List<MediaDevice> list) {
		DeviceComparator dc = new DeviceComparator();
		Collections.sort(list, dc);
	}
	
	public static class DeviceComparator implements Comparator {
		public int compare(Object object1, Object object2) {
			MediaDevice md1 = (MediaDevice) object1;
			MediaDevice md2 = (MediaDevice) object2;
			Log.d(TAG, String.format("compare mediadevice: %s(%d), %s(%d)", 
					md1.name(), md1.compareCode(), md2.name(), md2.compareCode()));
			return md1.compareCode() - md2.compareCode();
		}
	}
}
