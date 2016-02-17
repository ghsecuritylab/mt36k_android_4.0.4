package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * for the DTG type value operation
 *
 */

public class DtDTG implements Parcelable {
	
	
	int		mYear; 	
	int		mMonth;		/* 1-12, month of the year. */
	int		mDay;		/* 1-31, day of the month. */
	int		mDow;		/* 0-6, Sunday to Saturday. */
	int		mHour;		/* 0-23 */
	int		mMinute;	/* 0-59 */
	int		mSecond;	/* 0-61 */

	boolean	mIsGmt;			/* TRUE:  Universal Time Coordinate (UTC)
						    *FALSE: Local Time
						     */
	boolean mIsDst;		/* TRUE:  DayLight-Saving-Time on
								    	* FALSE: Day-Light_Saving Time off
								    	*/

	public DtDTG() {
		super();
	}
	
	/**
	 * 	structure function
	 */
	public DtDTG(int year, int month, int day, 
				int dow, int hour, int min, 
				int sec, boolean isGmt, boolean isDst) {
        super();
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
        this.mDow = dow;
        this.mHour = hour;
        this.mMinute = min;
        this.mSecond = sec;
        this.mIsGmt = isGmt;
        this.mIsDst = isDst;
    }

	/**
	 * @return year of DTG object
	 */
	public int getYear() {
		return mYear;
	}

	/**
	 * @param year 
	 * 			set year value of DTG object
	 */
	public void setYear(int year) {
		this.mYear = year;
	}
	
	/**
	 * @return month of DTG object
	 */
	public int getMonth() {
		return mMonth;
	}

	/**
	 * @param month
	 * 			set month of DTG object
	 */
	public void setMonth(int month) {
		this.mMonth = month;
	}	
	
	/**
	 * @return day of DTG object
	 */
	public int getDay() {
		return mDay;
	}

	/**
	 * set day of DTG object
	 * @param day
	 */
	public void setDay(int day) {
		this.mDay = day;
	}		

	/**
	 * @return Dow of DTG object
	 */
	public int getDow() {
		return mDow;
	}

	/**
	 * set dow of DTG object
	 * @param dow
	 */
	public void setDow(int dow) {
		this.mDow = dow;
	}		
	
	/**
	 * @return hour of DTG object
	 */
	public int getHour() {
		return mHour;
	}
	
	/**
	 * set hour of DTG object
	 * @param hour
	 */
	public void setHour(int hour) {
		this.mHour = hour;
	}		
	
	/**
	 * @return minute of DTG object
	 */
	public int getMin() {
		return mMinute;
	}
	
	/**
	 * set minute of DTG object
	 * @param minute
	 */
	public void setMin(int minute) {
		this.mMinute = minute;
	}		
	
	/**
	 * @return second of DTG object
	 */
	public int getSec() {
		return mSecond;
	}

	/**
	 * set second of DTG object
	 * @param second
	 */
	public void setSec(int second) {
		this.mSecond = second;
	}		
	
	/**
	 * @return Is it GMT of DTG object 
	 */
	public boolean getGmt() {
		return mIsGmt;
	}

	/**
	 * set gmt arg of DTG object
	 * @param isGmt
	 */
	public void setGmt(boolean isGmt) {
		this.mIsGmt = isGmt;
	}	
	
	/**
	 * @return DayLight-Saving-Time on/off
	 */
	public boolean getDst() {
		return mIsDst;
	}

	/**
	 * set DayLight-Saving-Time on/off
	 * @param isDst
	 */
	public void setDst(boolean isDst) {
		this.mIsDst = isDst;
	}		
		
		
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    
    public String toString() {
	    return "DtDTG [mYear=" + mYear + ", mMonth=" + mMonth + ", mDay=" + mDay + ", mDow=" + mDow + ", mHour="
	            + mHour + ", mMinute=" + mMinute + ", mSecond=" + mSecond + ", mIsGmt=" + mIsGmt + ", mIsDst=" + mIsDst
	            + "]";
    }


	public static final Creator<DtDTG> CREATOR = new Parcelable.Creator<DtDTG>() {
        public DtDTG createFromParcel(Parcel source) {
            return new DtDTG(source);
        }

        public DtDTG[] newArray(int size) {
            return new DtDTG[size];
        }
    };

    private DtDTG(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mYear);
        out.writeInt(mMonth);
        out.writeInt(mDay);
        out.writeInt(mDow);
        out.writeInt(mHour);
        out.writeInt(mMinute);
        out.writeInt(mSecond);
        out.writeInt(mIsGmt ? 1 : 0);
        out.writeInt(mIsDst ? 1 : 0);
    }

    public void readFromParcel(Parcel in) {
    	mYear 		= in.readInt();
    	mMonth 		= in.readInt();
    	mDay 		= in.readInt();
    	mDow 		= in.readInt();
    	mHour 		= in.readInt();
    	mMinute 	= in.readInt();
    	mSecond 	= in.readInt();
        mIsGmt 		= ((in.readInt() == 1) ? true : false);
        mIsDst 		= ((in.readInt() == 1) ? true : false);
    }
}
