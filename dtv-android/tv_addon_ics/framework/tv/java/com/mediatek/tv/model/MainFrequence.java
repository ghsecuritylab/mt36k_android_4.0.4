package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MainFrequence implements Parcelable {
	
	/**
	 * main Frequence
	 */
	protected int mainFrequence;
	
	/**
	 * stream count
	 */
	protected int tsCount;
	
	/**
	 * nit table version
	 */
	protected int nitVersion;
	
	public MainFrequence() {
		super();
	}

	/**
	 * @brief Get main Frequence
	 * @param None
	 * @return main Frequence
	 */
	public int getMainFrequence() {
		return this.mainFrequence;
	}
	
	/**
	 * @brief Set main Frequence
	 * @param None
	 * @return None
	 */
	public void setMainFrequence(int mainFrequence) {
		this.mainFrequence = mainFrequence;
	}
	
	/**
	 * @brief Get tsCount(stream count)
	 * @param None
	 * @return tsCount
	 */
	public int getTsCount() {
		return this.tsCount;
	}
	
	/**
	 * @brief Set ts Count
	 * @param None
	 * @return None
	 */
	public void setTsCount(int tsCount) {
		this.tsCount = tsCount;
	}
	
	/**
	 * @brief Get nit Version
	 * @param None
	 * @return nit Version
	 */
	public int getNitVersion() {
		return this.nitVersion;
	}
	
	/**
	 * @brief Set nit Version
	 * @param None
	 * @return None
	 */
	public void setNitVersion(int nitVersion) {
		this.nitVersion = nitVersion;
	}
	
    public static final Creator<MainFrequence> CREATOR = new Parcelable.Creator<MainFrequence>() {
        public MainFrequence createFromParcel(Parcel source) {
            return new MainFrequence(source);
        }

        public MainFrequence[] newArray(int size) {
            return new MainFrequence[size];
        }
    };

    private MainFrequence(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mainFrequence);
        out.writeInt(tsCount);
        out.writeInt(nitVersion);
    }

    public void readFromParcel(Parcel in) {
    	mainFrequence = in.readInt();
    	tsCount       = in.readInt();
    	nitVersion    = in.readInt();
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
