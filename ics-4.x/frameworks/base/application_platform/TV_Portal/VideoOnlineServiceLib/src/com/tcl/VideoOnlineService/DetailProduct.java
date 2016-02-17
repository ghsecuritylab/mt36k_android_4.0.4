package com.tcl.VideoOnlineService;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailProduct implements Parcelable {
	private int ErrorStatus;
	
	public String id = "";//?
	public String title="";//课程名称
	
	public String desc = "";//剧情简介
	public String smallPosterUrl = "";//小图url地址
	public String bigPosterUrl = "";//大图url地址
	
	//影片信息
	public String director = "";
	public String actors = "";
	public String area = "";
	public String language = "";
	public String score = "";
	public String pt = "";
	public String cn = "";	

	public List<DetailPlayUrlParcel>PlayUrlList = null;//下载信息：影片大小、格式等信息
	
	public static final Parcelable.Creator<DetailProduct> CREATOR = new Parcelable.Creator<DetailProduct>()
	{
		public DetailProduct createFromParcel(Parcel in)
		{
			return new DetailProduct(in);
		}

		public DetailProduct[] newArray(int size)
		{
			return new DetailProduct[size]; 
		}
	};
	
	public DetailProduct()
    {
		PlayUrlList = new ArrayList<DetailPlayUrlParcel>();
    }
	
	private DetailProduct(Parcel in)
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
		
		this.title = in.readString();
		this.id = in.readString();
		this.desc = in.readString();
		this.smallPosterUrl = in.readString();
		this.bigPosterUrl = in.readString();
		
		this.director = in.readString();
		this.actors = in.readString();
		this.area = in.readString();
		this.language = in.readString();
		this.score = in.readString();
		this.pt = in.readString();
		this.cn = in.readString();		
		
		in.readTypedList(PlayUrlList, DetailPlayUrlParcel.CREATOR);
	}
	
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(ErrorStatus);
		
		dest.writeString(title);
		dest.writeString(id);
		dest.writeString(desc);
		dest.writeString(smallPosterUrl);
		dest.writeString(bigPosterUrl);
		
		dest.writeString(director);
		dest.writeString(actors);
		dest.writeString(area);
		dest.writeString(language);
		dest.writeString(score);
		dest.writeString(pt);
		dest.writeString(cn);		
		
		dest.writeTypedList(PlayUrlList);
	}
	
	
	public void setTitle(String Title)
	{
		title = Title;
	}
   
	public String getTitle()
	{
		return title;
	}
	
	public void setId(String Id)
	{
		id = Id;
	}
   
	public String getId()
	{
		return id;
	}
	
	public void setDesc(String Desc)
	{
		desc = Desc;
	}
   
	public String getDesc()
	{
		return desc;
	}
   
	public void setsmallPosterUrl(String smallUrl)
	{
	   smallPosterUrl = smallUrl;
	}
  
	public String getsmallPosterUrl()
	{
		return smallPosterUrl;
	}
	
	public void setBigPosterUrl(String BigUrl)
	{
	   bigPosterUrl = BigUrl;
	}
  
	public String getBigPosterUrl()
	{
		return bigPosterUrl;
	}
	
	public void setDirector(String Director)
	{
		director = Director;
	}  
	public String getDirector()
	{
		return director;
	}
	
	public void setActors(String Actors)
	{
		actors = Actors;
	}  
	public String getActors()
	{
		return actors;
	}
	
	public void setArea(String Area)
	{
		area = Area;
	}  
	public String getArea()
	{
		return area;
	}
	
	public void setLanguage(String Language)
	{
		language = Language;
	}  
	public String getLanguage()
	{
		return language;
	}
	
	public void setPt(String Pt)
	{
		pt = Pt;
	}  
	public String getPt()
	{
		return pt;
	}
	
	public void setCn(String Cn)
	{
		cn = Cn;
	}  
	public String getCn()
	{
		return cn;
	}
	
	
	public void setScore(String Score)
	{
		score = Score;
	}  
	public String getScore()
	{
		return score;
	}
	
	public List<DetailPlayUrlParcel> getPlayUrlList()
	{
		return PlayUrlList;
	}	
	public void setPlayUrlList(List<DetailPlayUrlParcel> list)
	{
		this.PlayUrlList = list;
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
