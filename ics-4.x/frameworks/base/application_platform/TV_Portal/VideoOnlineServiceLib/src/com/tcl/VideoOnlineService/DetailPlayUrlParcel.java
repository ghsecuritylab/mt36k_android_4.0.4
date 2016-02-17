package com.tcl.VideoOnlineService;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailPlayUrlParcel implements Parcelable {	
	public String urlid = "";//影片内容 id
	public String ci = "";//集数(第几集)
	public String size = "";//本集大小(单位为 MB)
	public String length = "";//本集时间长(单位为分钟)
	public String format = "";//文件格式
	public String rate = "";//码率(单位 Kbps)
	public String vf = "";//是否是 Vip 电影
	public String url = "";//播放地址,文件真实地址
	public String stype = "";//数据传输类型,0 为点播,1 为直播

	
	public static final Parcelable.Creator<DetailPlayUrlParcel> CREATOR = new Parcelable.Creator<DetailPlayUrlParcel>()
	{
		public DetailPlayUrlParcel createFromParcel(Parcel in)
		{
			return new DetailPlayUrlParcel(in);
		}

		public DetailPlayUrlParcel[] newArray(int size)
		{
			return new DetailPlayUrlParcel[size]; 
		}
	};
	
	public DetailPlayUrlParcel()
    {
    	
    }
    
	private DetailPlayUrlParcel(Parcel in)
	{
		readFromParcel(in);
	}
	
	public int describeContents()
	{
		return 0;
	}

	public void readFromParcel(Parcel in)
	{
		this.urlid = in.readString();
		this.ci = in.readString();
		this.size= in.readString();
		this.length = in.readString();
		this.format= in.readString();
		this.rate = in.readString();
		this.vf = in.readString();
		this.url = in.readString();
		this.stype = in.readString();
		
	}

	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(urlid);
		dest.writeString(ci);
		dest.writeString(size);
		dest.writeString(length);
		dest.writeString(format);
		dest.writeString(rate);
		dest.writeString(vf);
		dest.writeString(url);
		dest.writeString(stype);

	}
	
	public void setUrlId(String Id)
	{
		urlid = Id;
	}  
	public String getUrlId()
	{
		return urlid;
	}
	
	public void setCi(String Ci)
	{
		ci = Ci;
	}  
	public String getCi()
	{
		return ci;
	}
	
	public void setSize(String Size)
	{
		size = Size;
	}  
	public String getSize()
	{
		return size;
	}
	
	public void setLength(String Length)
	{
		length = Length;
	}  
	public String getLength()
	{
		return length;
	}
	
	public void setFormat(String Format)
	{
		format = Format;
	}  
	public String getFormat()
	{
		return format;
	}
	
	public void setRate(String Rate)
	{
		rate = Rate;
	}  
	public String getRate()
	{
		return rate;
	}
	
	public void setVf(String Vf)
	{
		vf = Vf;
	}  
	public String getVf()
	{
		return vf;
	}
	
	public void setUrl(String Url)
	{
		url = Url;
	}  
	public String getUrl()
	{
		return url;
	}
	
	
	public void setStype(String Stype)
	{
		stype = Stype;
	}  
	public String getStype()
	{
		return stype;
	}
	
	
	

}
