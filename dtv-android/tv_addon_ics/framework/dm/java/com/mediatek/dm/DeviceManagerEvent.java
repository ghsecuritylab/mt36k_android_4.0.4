package com.mediatek.dm;

import android.os.Parcel;
import android.os.Parcelable;

public final class DeviceManagerEvent implements Parcelable{
    public static final int mounted = 601; 
    public static final int umounted = 602; 
    public static final int unsupported = 800;

    public static final int connected = 701;
    public static final int disconnected = 702;
    public static final int isomountfailed = 650;
    public static final int wificonnected = 751;
    public static final int wifidisconnected = 752;
    public static final int productconnected = 753;
    public static final int productdisconnected = 754;

    private int mType;
    private String mMountPointPath;
    
    public static final Parcelable.Creator<DeviceManagerEvent> CREATOR = new
    Parcelable.Creator<DeviceManagerEvent>() {
        	public DeviceManagerEvent createFromParcel(Parcel in){
        	     return  new DeviceManagerEvent(in);
        	}
        	public DeviceManagerEvent[] newArray(int size){
        	     return new DeviceManagerEvent[size];
        	}
    };	
    public DeviceManagerEvent(int type, String mountPointPath)
    {
        mType = type;
        mMountPointPath = mountPointPath;
    }
    private DeviceManagerEvent(Parcel in){
    	readFromParcel(in);
    }
    public void writeToParcel(Parcel out, int flags){
        out.writeInt(mType);
        out.writeString(mMountPointPath);
    }
    public void readFromParcel(Parcel in){
    	mType = in.readInt();
    	mMountPointPath = in.readString();
    }
    public void setType(int type)
    {
        mType = type;
    }

    public int getType()
    {
        return mType;
    }
    
    public String getMountPointPath()
	{
        if(mType != wificonnected && mType != wifidisconnected)
        {
	         return mMountPointPath;
        }
        else
        {
              return null;
        }
	}

    public String getProductName()
    {
        if(mType == productconnected || mType == productdisconnected)
        {
             return mMountPointPath;
        }
        else
        {
              return null;
        }
    }
    
    public String getWifiInterface()
    {
        if(mType == wificonnected || mType == wifidisconnected)
        {
             return mMountPointPath;
        }
        else
        {
              return null;
        }
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
