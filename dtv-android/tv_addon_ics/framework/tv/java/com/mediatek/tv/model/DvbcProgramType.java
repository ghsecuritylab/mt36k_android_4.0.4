package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DvbcProgramType implements Parcelable {
	/**
	 * The number of radio program
	 */
	protected int radioNumber;
	/**
	 * The number of digital tv program
	 */
	protected int tvNumber;	
	/**
	 * The number of app program
	 */
	protected int appNumber;
	
	public DvbcProgramType() {
		super();
	}

	/**
	 * @brief Get number of radio
	 * @param None
	 * @return radioNumber
	 */
	public int getRadioNumber() {
		return radioNumber;
	}
	
	/**
	 * @brief Set radioNumber
	 * @param radioNumber
	 * @return None
	 */
	public void setRadioNumber(int radioNumber) {
		this.radioNumber = radioNumber;
	}
	
	
	/**
	 * @brief Get number of radio
	 * @param None
	 * @return radioNumber
	 */
	public int getTvNumber() {
		return tvNumber;
	}
	
	/**
	 * @brief Set radioNumber
	 * @param radioNumber
	 * @return None
	 */
	public void setTvNumber(int tvNumber) {
		this.tvNumber = tvNumber;
	}
	
	/**
	 * @brief Get number of radio
	 * @param None
	 * @return radioNumber
	 */
	public int getAppNumber() {
		return appNumber;
	}
	
	/**
	 * @brief Set radioNumber
	 * @param radioNumber
	 * @return None
	 */
	public void setAppNumber(int appNumber) {
		this.appNumber = appNumber;
	}
	
    public static final Creator<DvbcProgramType> CREATOR = new Parcelable.Creator<DvbcProgramType>() {
        public DvbcProgramType createFromParcel(Parcel source) {
            return new DvbcProgramType(source);
        }

        public DvbcProgramType[] newArray(int size) {
            return new DvbcProgramType[size];
        }
    };

    private DvbcProgramType(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(radioNumber);
        out.writeInt(tvNumber);
        out.writeInt(appNumber);
    }

    public void readFromParcel(Parcel in) {
    	radioNumber = in.readInt();
        tvNumber  = in.readInt();
        appNumber = in.readInt();
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
