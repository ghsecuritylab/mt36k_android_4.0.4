package com.tcl.EduOnlineService;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class CategoryListProduct implements Parcelable {
	private int ErrorStatus;
	
	private List<CategoryListParcelable>CategoryList = null;
	
	public static final Parcelable.Creator<CategoryListProduct> CREATOR = new Parcelable.Creator<CategoryListProduct>()
	{
		public CategoryListProduct createFromParcel(Parcel in)
		{
			return new CategoryListProduct(in);
		}

		public CategoryListProduct[] newArray(int size)
		{
			return new CategoryListProduct[size]; 
		}
	};
	
	public CategoryListProduct()
    {
		CategoryList = new ArrayList<CategoryListParcelable>();
    }
	
	private CategoryListProduct(Parcel in)
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
		
		in.readTypedList(CategoryList, CategoryListParcelable.CREATOR);
	}
	
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(ErrorStatus);
		
		dest.writeTypedList(CategoryList);
	}
	
	public List<CategoryListParcelable> getCategoryList()
	{
		return CategoryList;
	}
	
	public void setCategoryList(List<CategoryListParcelable> list)
	{
		this.CategoryList = list;
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
