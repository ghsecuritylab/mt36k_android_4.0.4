package com.tcl.mediafile;

import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.storage.StorageVolume;

public class Ums extends Device {
	StorageVolume volume;
	Context mContext;
	
	public Ums(StorageVolume v) {
		volume = v;
	}
	
	@Override
	public String type() {
		return Device.TYPE_USB;
	}

	@Override
	public String path() {
		return volume.getPath();
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return MstarDeviceManager.getLabel(this);
	}

	@Override
	public MediaFile getRootFile() {
		MediaFile f = new LocalFile(volume.getPath());
		f.setDevice(this);
		return f;
	}

	@Override
	public String getState() {
		// TODO Auto-generated method stub
		return MstarDeviceManager.getUmsState(path());
	}
	
	@Override
	public boolean isMounted() {
		String stat = getState();
		if (Environment.MEDIA_MOUNTED.equals(stat) 
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(stat)) {
			return true;
		}
		return false;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		volume.writeToParcel(dest, flags);
	}
	
	public static final Parcelable.Creator<Ums> CREATOR = new Parcelable.Creator<Ums>() {

		@Override
		public Ums createFromParcel(Parcel source) {
			return new Ums(StorageVolume.CREATOR.createFromParcel(source));
		}

		@Override
		public Ums[] newArray(int size) {
			return new Ums[size];
		}
	};
}
