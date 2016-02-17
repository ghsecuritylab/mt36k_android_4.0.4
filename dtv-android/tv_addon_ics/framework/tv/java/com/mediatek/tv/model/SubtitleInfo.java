package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SubtitleInfo implements Parcelable {

	/**
	 * The language of subtitle
	 */
	protected String subtitleLang;
	
	/**
	 * The language of current using subtitle
	 */
	protected String currentSubtitleLang;
	
	public SubtitleInfo() {
		super();
	}

	/**
	 * @brief Get language of Subtitle
	 * @param None
	 * @return Subtitle language
	 */
	public String getSubtitleLang() {
		return this.subtitleLang;
	}

	/**
	 * @brief Set language of Subtitle
	 * @param None
	 * @return None
	 */
	public void setSubtitleLang(String subtitleLang) {
		this.subtitleLang = subtitleLang;
	}

	
	/**
	 * @brief Get current using Subtitle Lang
	 * @param None
	 * @return Subtitle language
	 */
	public String getCurrentSubtitleLang() {
		return this.currentSubtitleLang;
	}

	/**
	 * @brief Set current using Subtitle Lang
	 * @param None
	 * @return None
	 */
	public void setCurrentSubtitleLang(String currentSubtitleLang) {
		this.currentSubtitleLang = currentSubtitleLang;
	}



    public static final Creator<SubtitleInfo> CREATOR = new Parcelable.Creator<SubtitleInfo>() {
        public SubtitleInfo createFromParcel(Parcel source) {
            return new SubtitleInfo(source);
        }

        public SubtitleInfo[] newArray(int size) {
            return new SubtitleInfo[size];
        }
    };

    private SubtitleInfo(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(subtitleLang);
        out.writeString(currentSubtitleLang);
    }

    public void readFromParcel(Parcel in) {
        subtitleLang = in.readString();
        currentSubtitleLang = in.readString();
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
