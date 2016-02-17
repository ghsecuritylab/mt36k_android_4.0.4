package com.tcl.VideoOnlineService;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ClassInfoProduct implements Parcelable {
	private int ErrorStatus;
	
	private String classnum;
	private List<ClassInfoParcelable>ClassInfoList = null;
	
	public static final Parcelable.Creator<ClassInfoProduct> CREATOR = new Parcelable.Creator<ClassInfoProduct>()
	{
		public ClassInfoProduct createFromParcel(Parcel in)
		{
			return new ClassInfoProduct(in);
		}

		public ClassInfoProduct[] newArray(int size)
		{
			return new ClassInfoProduct[size]; 
		}
	};
	
	public ClassInfoProduct()
    {
		ClassInfoList = new ArrayList<ClassInfoParcelable>();
    }
	
	private ClassInfoProduct(Parcel in)
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
		in.readTypedList(ClassInfoList, ClassInfoParcelable.CREATOR);
	}
	
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(ErrorStatus);
		dest.writeString(classnum);
		dest.writeTypedList(ClassInfoList);
	}
	
	public List<ClassInfoParcelable> getClassInfoList()
	{
		return ClassInfoList;
	}
	
	public void setClassInfoList(List<ClassInfoParcelable> list)
	{
		this.ClassInfoList = list;
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
