package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SignalLevelInfo implements Parcelable {
	
	/**
	 * signalLevel
	 */
	protected int signalLevel;
	
	/**
	 * ber
	 */
	protected int ber;
	
	public SignalLevelInfo() {
		super();
	}

	/**
	 * @brief Get signal Level
	 * @param None
	 * @return signal Level
	 */
	public int getSignalLevel() {
		return this.signalLevel;
	}
	
	/**
	 * @brief Set signal Level
	 * @param None
	 * @return None
	 */
	public void setSignalLevel(int signalLevel) {
		this.signalLevel = signalLevel;
	}
	
	/**
	 * @brief Get ber
	 * @param None
	 * @return ber
	 */
	public int getBer() {
		return this.ber;
	}
	
	/**
	 * @brief Set ber
	 * @param None
	 * @return None
	 */
	public void setBer(int ber) {
		this.ber = ber;
	}
	
    public static final Creator<SignalLevelInfo> CREATOR = new Parcelable.Creator<SignalLevelInfo>() {
        public SignalLevelInfo createFromParcel(Parcel source) {
            return new SignalLevelInfo(source);
        }

        public SignalLevelInfo[] newArray(int size) {
            return new SignalLevelInfo[size];
        }
    };

    private SignalLevelInfo(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(signalLevel);
        out.writeInt(ber);
    }

    public void readFromParcel(Parcel in) {
    	signalLevel = in.readInt();
    	ber         = in.readInt();
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
