package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AudioInfo implements Parcelable {
	/**
	 * The alternative audio like stereo
	 */
	protected int alternativeAudio;

	/**
	 * The mts value of analog
	 */
	protected int mts;

	public AudioInfo() {
		super();
	}

	/**
	 * @brief Get alternative of audio
	 * @param None
	 * @return audio alternative
	 */
	public int getAlternativeAudio() {
		return alternativeAudio;
	}
	
	/**
	 * @brief Set alternative of audio
	 * @param None
	 * @return None
	 */
	public void setAlternativeAudio(int alternativeAudio) {
		this.alternativeAudio = alternativeAudio;
	}
	
	/**
	 * @brief Get mts value
	 * @param None
	 * @return audio alternative
	 */
	public int getMts() {
		return this.mts;
	}
	
	/**
	 * @brief Set mts value
	 * @param None
	 * @return None
	 */
	public void setMts(int mts) {
		this.mts = mts;
	}
	
    public static final Creator<AudioInfo> CREATOR = new Parcelable.Creator<AudioInfo>() {
        public AudioInfo createFromParcel(Parcel source) {
            return new AudioInfo(source);
        }

        public AudioInfo[] newArray(int size) {
            return new AudioInfo[size];
        }
    };

    private AudioInfo(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(alternativeAudio);
        out.writeInt(mts);
    }

    public void readFromParcel(Parcel in) {
        alternativeAudio = in.readInt();
                     mts = in.readInt();
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
