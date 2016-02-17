package com.tcl.mediafile;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

public class Sdcard extends Device {

	@Override
	public String type() {
		// TODO Auto-generated method stub
		return Device.TYPE_SDCARD;
	}

	@Override
	public String path() {
		// TODO Auto-generated method stub
		return Environment.getExternalStorageDirectory().getPath();
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return MstarDeviceManager.getLabel(this);
	}

	@Override
	public MediaFile getRootFile() {
		MediaFile f = new LocalFile(Environment.getExternalStorageDirectory());
		f.setDevice(this);
		return f;
	}

	public boolean isMounted() {
		// TODO Auto-generated method stub
		String stat = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(stat) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(stat)) {
			return true;
		}
		else
			return false;
	}

	@Override
	public String getState() {
		return Environment.getExternalStorageState();
	}
	
	public static boolean mounted() {
		String stat = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(stat) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(stat)) {
			return true;
		}
		else
			return false;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
	}
	
	public static final Parcelable.Creator<Sdcard> CREATOR = new Parcelable.Creator<Sdcard>() {

		@Override
		public Sdcard createFromParcel(Parcel source) {
			return new Sdcard();
		}

		@Override
		public Sdcard[] newArray(int size) {
			return new Sdcard[size];
		}
	};
}
