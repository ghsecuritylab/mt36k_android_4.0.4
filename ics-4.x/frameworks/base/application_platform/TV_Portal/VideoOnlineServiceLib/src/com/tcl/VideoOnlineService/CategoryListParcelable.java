package com.tcl.VideoOnlineService;

import android.os.Parcel;
import android.os.Parcelable;

public class CategoryListParcelable implements Parcelable {
	
	private String id;
	private String title;
	private String smallPosterUrl;
	
	public static final Parcelable.Creator<CategoryListParcelable> CREATOR = new Parcelable.Creator<CategoryListParcelable>()
	{
		public CategoryListParcelable createFromParcel(Parcel in)
		{
			return new CategoryListParcelable(in);
		}

		public CategoryListParcelable[] newArray(int size)
		{
			return new CategoryListParcelable[size]; 
		}
	};
	
	public CategoryListParcelable()
    {
    	
    }
    
	private CategoryListParcelable(Parcel in)
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
		this.id = in.readString();
		this.title = in.readString();
		this.smallPosterUrl = in.readString();
		
	}

	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(smallPosterUrl);
	}
	
	public String getsmallPosterUrl()
	{
		return smallPosterUrl;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getId()
	{
		return id;
	}
	
	
	public void setsmallPosterUrl(String url)
	{
		this.smallPosterUrl = url;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void setId(String Id)
	{
		this.id = Id;
	}

}
