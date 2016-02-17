package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DtmbFreqRange implements Parcelable {
	/**
	 * The lower frequence of tuner
	 */
	protected int lowerTunerFreqBound;
	/**
	 * The upper frequence of tuner
	 */
	protected int upperTunerFreqBound;	


	public DtmbFreqRange() {
		super();
	}

	/**
	 * @brief Get lower tuner freq
	 * @param None
	 * @return lowerTunerFreqBound
	 */
	public int getLowerTunerFreqBound() {
		return lowerTunerFreqBound;
	}
	
	/**
	 * @brief Set lower tuner freq bound
	 * @param lowerTunerFreqBound
	 * @return None
	 */
	public void setLowerTunerFreqBound(int lowerTunerFreqBound) {
		this.lowerTunerFreqBound = lowerTunerFreqBound;
	}
	
	
	/**
	 * @brief Get upper tuner freq bound
	 * @param None
	 * @return upperTunerFreqBound
	 */
	public int getUpperTunerFreqBound() {
		return upperTunerFreqBound;
	}
	
	/**
	 * @brief Set upper tuner freq bound
	 * @param upperTunerFreqBound
	 * @return None
	 */
	public void setUpperTunerFreqBound(int upperTunerFreqBound) {
		this.upperTunerFreqBound = upperTunerFreqBound;
	}
	

    public static final Creator<DtmbFreqRange> CREATOR = new Parcelable.Creator<DtmbFreqRange>() {
        public DtmbFreqRange createFromParcel(Parcel source) {
            return new DtmbFreqRange(source);
        }

        public DtmbFreqRange[] newArray(int size) {
            return new DtmbFreqRange[size];
        }
    };

    private DtmbFreqRange(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(lowerTunerFreqBound);
        out.writeInt(upperTunerFreqBound);
    }

    public void readFromParcel(Parcel in) {
    	lowerTunerFreqBound = in.readInt();
    	upperTunerFreqBound = in.readInt();
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}

