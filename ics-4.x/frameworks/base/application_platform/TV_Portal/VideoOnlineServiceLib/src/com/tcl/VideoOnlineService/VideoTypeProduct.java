package com.tcl.VideoOnlineService;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoTypeProduct implements Parcelable {
	private int ErrorStatus;
	private String classnum;
	private List<VideoTypeParcel>VideoTypeList = null;
	
	public static final Parcelable.Creator<VideoTypeProduct> CREATOR = new Parcelable.Creator<VideoTypeProduct>()
	{
		public VideoTypeProduct createFromParcel(Parcel in)
		{
			return new VideoTypeProduct(in);
		}

		public VideoTypeProduct[] newArray(int size)
		{
			return new VideoTypeProduct[size]; 
		}
	};
	
	public VideoTypeProduct()
    {
		VideoTypeList = new ArrayList<VideoTypeParcel>();
    }
	
	private VideoTypeProduct(Parcel in)
	{
		this();
		readFromParcel(in);
	}
	
	public int describeContents()
	{
		return 0;
	}
	
	public void readFromParcel(Parcel in)
	{
		this.ErrorStatus = in.readInt();
		this.classnum = in.readString();
		in.readTypedList(VideoTypeList, VideoTypeParcel.CREATOR);
	}
	
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(ErrorStatus);
		dest.writeString(classnum);
		dest.writeTypedList(VideoTypeList);
	}
	
	public List<VideoTypeParcel> getVideoTypeList()
	{
		return VideoTypeList;
	}
	
	public void setVideoTypeList(List<VideoTypeParcel> list)
	{
		this.VideoTypeList = list;
	}

	
	
	public int getErrorStatus()
	{
		return ErrorStatus;
	}
	public void setErrorStatus(int errorStatus)
	{
		this.ErrorStatus = errorStatus;
	}
	
	public String getClassNum()
	{
		return classnum;
	}
	public void setClassNum(String ClassNum)
	{
		this.classnum = ClassNum;
	}
	
}
