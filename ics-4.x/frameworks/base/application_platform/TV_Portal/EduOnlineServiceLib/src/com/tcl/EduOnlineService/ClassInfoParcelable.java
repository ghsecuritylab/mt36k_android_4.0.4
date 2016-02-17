package com.tcl.EduOnlineService;

import android.os.Parcel;
import android.os.Parcelable;

public class ClassInfoParcelable implements Parcelable {
	
	private String id;
	private String title;
	private String type;
	private String subclassnum;
	
	public static final Parcelable.Creator<ClassInfoParcelable> CREATOR = new Parcelable.Creator<ClassInfoParcelable>()
	{
		public ClassInfoParcelable createFromParcel(Parcel in)
		{
			return new ClassInfoParcelable(in);
		}

		public ClassInfoParcelable[] newArray(int size)
		{
			return new ClassInfoParcelable[size]; 
		}
	};
	
	public ClassInfoParcelable()
    {
    	
    }
    
	private ClassInfoParcelable(Parcel in)
	{
		readFromParcel(in);
	}
	
	public int describeContents()
	{
		return 0;
	}

	public void readFromParcel(Parcel in)
	{
		this.id = in.readString();
		this.title = in.readString();
		this.type = in.readString();
		this.subclassnum = in.readString();
	}

	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(type);
		dest.writeString(subclassnum);
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
	public String getSubClassNum()
	{
		return subclassnum;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public void setSubClassNum(String SubClassNum)
	{
		this.subclassnum = SubClassNum;
	}

}
