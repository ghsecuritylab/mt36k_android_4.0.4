package com.tcl.VideoOnlineService;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoTypeParcel implements Parcelable {
	public String id = "";//影片分类id
	public String title = "";//分类名称
	public String type = "";//type=1 表示没有子分类，type=0 表示有子分类
	public String contentnum = "";//type=0 时表示表示子分类数量，type=1 时表示影片数量
	
	public List<VideoSubTypeParcel> SubTypeList = null;
	
	public static final Parcelable.Creator<VideoTypeParcel> CREATOR = new Parcelable.Creator<VideoTypeParcel>()
	{
		public VideoTypeParcel createFromParcel(Parcel in)
		{
			return new VideoTypeParcel(in);
		}

		public VideoTypeParcel[] newArray(int size)
		{
			return new VideoTypeParcel[size]; 
		}
	};
	
	public VideoTypeParcel()
    {
		SubTypeList = new ArrayList<VideoSubTypeParcel>();
    }
	
	private VideoTypeParcel(Parcel in)
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
		this.title = in.readString();
		this.id = in.readString();
		this.type = in.readString();
		this.contentnum = in.readString();		
		
		in.readTypedList(SubTypeList, VideoSubTypeParcel.CREATOR);
	}
	
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(title);
		dest.writeString(id);
		dest.writeString(type);
		dest.writeString(contentnum);		
		
		dest.writeTypedList(SubTypeList);
	}
	
	public String getId()
	{
		return id;
	}
	public String getTitle()
	{
		return title;
	}
	public String getType()
	{
		return type;
	}
	public int getContentNum()
	{
		return Integer.parseInt(contentnum);
	}
	
	public void setID(String ID)
	{
		id = ID;
	}	
	public void setTitle(String Title)
	{
		title = Title;
	}	
	public void setType(String Type)
	{
		type = Type;
	}	
	public void setContentNum(String num)
	{
		contentnum = num;
	}
	
	public List<VideoSubTypeParcel> getSubTypeList()
	{
		return SubTypeList;
	}	
	public void setSubTypeList(List<VideoSubTypeParcel> list)
	{
		this.SubTypeList = list;
	}

}
