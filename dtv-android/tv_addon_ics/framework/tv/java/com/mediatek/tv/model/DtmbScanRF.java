package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DtmbScanRF implements Parcelable {   
    /**
         * The RF channel
         */
    private String rfChannel;
    
	/**
	 * The scan index of the RF
	 */
	private int rfScanIndex;

    /**
	 * The frequency of the RF
	 */
	private int rfScanFrequency;
	

	public DtmbScanRF() {
		super();
	}

	/**
	 * @brief Get RF channel
	 * @param None
	 * @return rfChannel
	 */
	public String getRFChannel() {
		return rfChannel;
	}
	
	/**
	 * @brief Set RF channel
	 * @param rfChannel
	 * @return None
	 */
	public void setRFChannel(String rfChannel) {
		this.rfChannel = rfChannel;
	}
	
	
	/**
	 * @brief Get RF scan index
	 * @param None
	 * @return rfScanIndex
	 */
	public int getRFScanIndex() {
		return rfScanIndex;
	}
	
	/**
	 * @brief Set RF scan index
	 * @param rfScanIndex
	 * @return None
	 */
	public void setRFScanIndex(int rfScanIndex) {
		this.rfScanIndex = rfScanIndex;
	}

    /**
	 * @brief Get RF scan frequency
	 * @param None
	 * @return rfScanFrequency
	 */
	public int getRFScanFrequency() {
		return rfScanFrequency;
	}
	
	/**
	 * @brief Set RF scan frequency
	 * @param rfScanFrequency
	 * @return None
	 */
	public void setRFScanFrequency(int rfScanFrequency) {
		this.rfScanFrequency = rfScanFrequency;
	}
	

    public static final Creator<DtmbScanRF> CREATOR = new Parcelable.Creator<DtmbScanRF>() {
        public DtmbScanRF createFromParcel(Parcel source) {
            return new DtmbScanRF(source);
        }

        public DtmbScanRF[] newArray(int size) {
            return new DtmbScanRF[size];
        }
    };

    private DtmbScanRF(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(rfChannel);
        out.writeInt(rfScanIndex);
        out.writeInt(rfScanFrequency);
    }

    public void readFromParcel(Parcel in) {
    	rfChannel       = in.readString();
    	rfScanIndex     = in.readInt();
        rfScanFrequency = in.readInt();
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}


