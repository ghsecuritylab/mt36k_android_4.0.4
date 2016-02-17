package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ExtraChannelInfo implements Parcelable {
	/**
	 * audio language which want to cache.
	 */
	protected String channelAudioLanguage;	
	
	protected int audioMts       = -1;

	protected int audioLangIndex = -1;

	public ExtraChannelInfo() {
		super();
	}

	/**
	 * @brief Get audio language
	 * @param None
	 * @return audio language
	 */
	public String getChannelAudioLanguage() {
		return this.channelAudioLanguage;
	}
	
	/**
	 * @brief Set audio language
	 * @param audio language
	 * @return None
	 */
	public void setChannelAudioLanguage(String channelAudioLanguage) {
		this.channelAudioLanguage = channelAudioLanguage;
	}
	
	/**
	 * @brief Get audio mts which save in config
	 * @param None
	 * @return audio mts
	 */
	public int getAudioMts() {
		return this.audioMts;
	}
	
	/**
	 * @brief Set audio mts
	 * @param audio mts
	 * @return None
	 */
	public void setAudioMts(int audioMts) {
		this.audioMts = audioMts;
	}

		/**
	 * @brief Get audio lang index
	 * @param None
	 * @return audioLangIndex
	 */
	public int getAudioLangIndex() {
		return this.audioLangIndex;
	}
	
	/**
	 * @brief Set audio Lang index
	 * @param audioLangIndex
	 * @return None
	 */
	public void setAudioLangIndex(int audioLangIndex) {
		this.audioLangIndex = audioLangIndex;
	}
	
    public static final Creator<ExtraChannelInfo> CREATOR = new Parcelable.Creator<ExtraChannelInfo>() {
        public ExtraChannelInfo createFromParcel(Parcel source) {
            return new ExtraChannelInfo(source);
        }

        public ExtraChannelInfo[] newArray(int size) {
            return new ExtraChannelInfo[size];
        }
    };

    private ExtraChannelInfo(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(channelAudioLanguage);
		out.writeInt(audioMts);
		out.writeInt(audioLangIndex);
    }

    public void readFromParcel(Parcel in) {
    	channelAudioLanguage = in.readString();
		audioMts = in.readInt();
		audioLangIndex = in.readInt();
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
