package com.tcl.EduOnlineService;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailProduct implements Parcelable {
	private int ErrorStatus;
	
	public String title="";//课程名称
	public String programid = "";//?
	public String introduction = "";//剧情简介
	public String smallPosterUrl = "";//小图url地址
	public String bigPosterUrl = "";//大图url地址
	
	//影片信息
	public String classinfo = "";
	public String lecturer = "";
	public String language = "";
	public String score = "";
	public String pt = "";
	public String cn = "";
	public String tlength = "";
	public String source = "";
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
		this.programid = in.readString();
		this.introduction = in.readString();
		this.smallPosterUrl = in.readString();
		this.bigPosterUrl = in.readString();
		
		this.classinfo = in.readString();
		this.lecturer = in.readString();
		this.language = in.readString();
		this.score = in.readString();
		this.pt = in.readString();
		this.cn = in.readString();
		this.tlength = in.readString();
		this.source = in.readString();
		
		in.readTypedList(PlayUrlList, DetailPlayUrlParcel.CREATOR);
	}
	
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(ErrorStatus);
		
		dest.writeString(title);
		dest.writeString(programid);
		dest.writeString(introduction);
		dest.writeString(smallPosterUrl);
		dest.writeString(bigPosterUrl);
		
		dest.writeString(classinfo);
		dest.writeString(lecturer);
		dest.writeString(language);
		dest.writeString(score);
		dest.writeString(pt);
		dest.writeString(cn);
		dest.writeString(tlength);
		dest.writeString(source);
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
	
	public void setProgramid(String Programid)
	{
		programid = Programid;
	}
   
	public String getProgramid()
	{
		return programid;
	}
	
	public void setIntroduction(String Introduction)
	{
		introduction = Introduction;
	}
   
	public String getIntroduction()
	{
		return introduction;
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
	
	public void setClassInfo(String ClassInfo)
	{
		classinfo = ClassInfo;
	}  
	public String getClassInfo()
	{
		return classinfo;
	}
	
	public void setLecturer(String Lecturer)
	{
		lecturer = Lecturer;
	}  
	public String getLecturer()
	{
		return lecturer;
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
	
	public void setTlength(String Tlength)
	{
		tlength = Tlength;
	}  
	public String getTlength()
	{
		return tlength;
	}
	
	public void setSource(String Source)
	{
		source = Source;
	}  
	public String getSource()
	{
		return source;
	}
	
	public void setScore(String Score)
	{
		classinfo = Score;
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
