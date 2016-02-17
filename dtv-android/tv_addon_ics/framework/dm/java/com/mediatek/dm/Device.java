package com.mediatek.dm;

import android.os.Parcel;
import android.os.Parcelable;

public final class Device implements Parcelable
{
    public String mDeviceName;
    public int mMajor;
    public int mMinor;
    public int mStatus;
    public static final Parcelable.Creator<Device> CREATOR = new
Parcelable.Creator<Device>() {
    	public Device createFromParcel(Parcel in){
    	     return  new Device(in);
    	}
    	public Device[] newArray(int size){
    	     return new Device[size];
    	}
};
public Device(int major, int minor, int status, String deviceName)
{
    mDeviceName = deviceName;
    mMajor = major;
    mMinor = minor;
    mStatus = status;
}
private Device(Parcel in){
	readFromParcel(in);
}
public void writeToParcel(Parcel out, int flags){
    out.writeString(mDeviceName);
    out.writeInt(mMajor);
    out.writeInt(mMinor);
    out.writeInt(mStatus);
}
public void readFromParcel(Parcel in){
	mDeviceName = in.readString();
	mMajor = in.readInt();
	mMinor = in.readInt();
	mStatus =in.readInt();
}

public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
}
}




