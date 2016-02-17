package com.mediatek.dm;

import android.os.Parcel;
import android.os.Parcelable;

public final class MountPoint implements Parcelable{
    
    public enum FS_TYPE
    {
        FS_TYPE_INVAL,
        FS_TYPE_FAT,
        FS_TYPE_NTFS,
        FS_TYPE_EXT2,
        FS_TYPE_EXT3,
        FS_TYPE_EXT4,
        FS_TYPE_ISO9660,
        FS_TYPE_EXFAT
    };
    
    public String mMountPoint;
    public String mDeviceName;
    public String mVolumeLabel;
    public String mVolumeType;
    public long mTotalSize;
    public long mFreeSize;
    public int mMajor;
    public int mMinor;
    public int mStatus;
    public FS_TYPE mFsType;
    public static final Parcelable.Creator<MountPoint> CREATOR = new
    Parcelable.Creator<MountPoint>() {
        	public MountPoint createFromParcel(Parcel in){
        	     return  new MountPoint(in);
        	}
        	public MountPoint[] newArray(int size){
        	     return new MountPoint[size];
        	}
    };

    public MountPoint(long totalSize, long freeSize, int major, int minor,
            int status, String mountPoint, String deviceName, String volumeLabel, int fsType) {
        mMountPoint = mountPoint;
        mDeviceName = deviceName;
        mVolumeLabel = volumeLabel;
        mVolumeType = "USB";
        mTotalSize = totalSize;
        mFreeSize = freeSize;
        mMajor = major;
        mMinor = minor;
        mStatus = status;
        switch(fsType) {
            case 1:
                mFsType = FS_TYPE.FS_TYPE_FAT;
                break;
            case 2:
                mFsType = FS_TYPE.FS_TYPE_NTFS;
                break;
            case 3:
                mFsType = FS_TYPE.FS_TYPE_EXT2;
                break;
            case 4:
                mFsType = FS_TYPE.FS_TYPE_EXT3;
                break;
            case 5:
                mFsType = FS_TYPE.FS_TYPE_EXT4;
                break;
            case 6:
                mFsType = FS_TYPE.FS_TYPE_ISO9660;
                break;
            case 7:
                mFsType = FS_TYPE.FS_TYPE_EXFAT;
                break;
            default:
                mFsType = FS_TYPE.FS_TYPE_INVAL; 
        }
    }
    public MountPoint(long totalSize, long freeSize, int major, int minor,
            int status, String mountPoint, String deviceName, byte[] volumedata, int fsType) {
        mMountPoint = mountPoint;
        mDeviceName = deviceName;
        mVolumeType = "USB";
        mTotalSize = totalSize;
        mFreeSize = freeSize;
        mMajor = major;
        mMinor = minor;
        mStatus = status;
        if(fsType == 1)
        {
            try{
                String value = new String(volumedata, "cp936");          
                byte[] byteArray = value.getBytes("utf8");
                mVolumeLabel = new String(byteArray);
            }catch(java.io.UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }
        else
        {
            mVolumeLabel = new String((byte[])volumedata);
        }
        switch(fsType) {
            case 1:
                mFsType = FS_TYPE.FS_TYPE_FAT;
                break;
            case 2:
                mFsType = FS_TYPE.FS_TYPE_NTFS;
                break;
            case 3:
                mFsType = FS_TYPE.FS_TYPE_EXT2;
                break;
            case 4:
                mFsType = FS_TYPE.FS_TYPE_EXT3;
                break;
            case 5:
                mFsType = FS_TYPE.FS_TYPE_EXT4;
                break;
            case 6:
                mFsType = FS_TYPE.FS_TYPE_ISO9660;
                break;
            case 7:
                mFsType = FS_TYPE.FS_TYPE_EXFAT;
                break;
            default:
                mFsType = FS_TYPE.FS_TYPE_INVAL; 
        }
    }

    /**
     * used for samba & dlna device
     */
    public MountPoint(String volumeType,String mountPoint,String volumeLabel) {
        mMountPoint = mountPoint;
        mDeviceName = null;
        mVolumeLabel = volumeLabel;
        mVolumeType = volumeType;
        mTotalSize = 0;
        mFreeSize = 0;
        mMajor = 0;
        mMinor = 0;
        mStatus = 0;
        mFsType = FS_TYPE.FS_TYPE_INVAL;
    }
    
    private MountPoint(Parcel in){
    	readFromParcel(in);
    }
    public void writeToParcel(Parcel out, int flags){
    	out.writeString(mMountPoint);
        out.writeString(mDeviceName);
        out.writeString(mVolumeLabel);
        out.writeString(mVolumeType);
        out.writeLong(mTotalSize);
        out.writeLong(mFreeSize);
        out.writeInt(mMajor);
        out.writeInt(mMinor);
        out.writeInt(mStatus);
        out.writeInt(mFsType.ordinal());
    }
    public void readFromParcel(Parcel in){
    	mMountPoint = in.readString();
        mDeviceName = in.readString();
        mVolumeLabel = in.readString();
        mVolumeType = in.readString();
        mTotalSize = in.readLong();
        mFreeSize = in.readLong();
        mMajor = in.readInt();
        mMinor = in.readInt();
        mStatus = in.readInt();
        mFsType = FS_TYPE.values()[in.readInt()];
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
