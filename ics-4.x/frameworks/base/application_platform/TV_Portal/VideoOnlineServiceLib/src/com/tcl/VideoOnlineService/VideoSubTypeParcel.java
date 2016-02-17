package com.tcl.VideoOnlineService;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoSubTypeParcel implements Parcelable {
	public String subid = "";
	public String subtitle = "";
	public String sub_contentnum = "";
	
	
	public static final Parcelable.Creator<VideoSubTypeParcel> CREATOR = new Parcelable.Creator<VideoSubTypeParcel>()
	{
		public VideoSubTypeParcel createFromParcel(Parcel in)
		{
			return new VideoSubTypeParcel(in);
		}

		public VideoSubTypeParcel[] newArray(int size)
		{
			return new VideoSubTypeParcel[size]; 
		}
	};
	
	public VideoSubTypeParcel()
    {
    	
    }
    
	private VideoSubTypeParcel(Parcel in)
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
		this.subid = in.readString();
		this.subtitle = in.readString();
		this.sub_contentnum = in.readString();
		
	}

	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(subid);
		dest.writeString(subtitle);
		dest.writeString(sub_contentnum);
	}
	
	public String getSubNum()
	{
		return sub_contentnum;
	}
	
	public String getsubTitle()
	{
		return subtitle;
	}
	
	public String getsubId()
	{
		return subid;
	}
	
	
	public void setSubNum(String SubNum)
	{
		this.sub_contentnum = SubNum;
	}

	public void setsubTitle(String title)
	{
		this.subtitle = title;
	}
	
	public void setsubId(String Id)
	{
		this.subid = Id;
	}

}
