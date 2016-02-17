package com.tcl.VideoOnlineService;

import android.os.Parcel;
import android.os.Parcelable;

public class SmallPicProduct implements Parcelable {
	private int ErrorStatus;
	
	private String smallPicUrl;
	
	public static final Parcelable.Creator<SmallPicProduct> CREATOR = new Parcelable.Creator<SmallPicProduct>()
	{
		public SmallPicProduct createFromParcel(Parcel in)
		{
			return new SmallPicProduct(in);
		}

		public SmallPicProduct[] newArray(int size)
		{
			return new SmallPicProduct[size]; 
		}
	};
	
	public SmallPicProduct()
    {

    }
	
	private SmallPicProduct(Parcel in)
	{
		readFromParcel(in);
	}
	
	public int describeContents()
	{
		return 0;
	}
	
	public void readFromParcel(Parcel in)
	{
		this.ErrorStatus = in.readInt();
		
		this.smallPicUrl = in.readString();
	}
	
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(ErrorStatus);
		
		dest.writeString(smallPicUrl);
	}
	
	public void setSmallPicUrl(String SmallPicUrl)
	{
		smallPicUrl = SmallPicUrl;
	}  
	public String getSmallPicUrl()
	{
		return smallPicUrl;
	}
	
	
	
	public int getErrorStatus()
	{
		return ErrorStatus;
	}
	public void setErrorStatus(int errorStatus)
	{
		this.ErrorStatus = errorStatus;
	}
}
