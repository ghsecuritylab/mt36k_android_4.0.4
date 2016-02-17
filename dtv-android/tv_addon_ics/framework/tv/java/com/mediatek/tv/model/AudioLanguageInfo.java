package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AudioLanguageInfo implements Parcelable {
	/**
	 * total audio number
	 */
	protected int totalNumber;

	/**
	 * All of the audio language in the stream
	 */
	protected String audioLanguage;

	/**
	 * currentLanguage which was using
	 */
	protected String currentLanguage;

	/**
	 * current Language index
	 */
	protected int currentAudioLangIndex;
	
	/**
	 * The dtv audio mts
	 */
	protected int digitalMts;
	
	/**
	 * The dtv audio decode type
	 */
	protected int audioDecodeType;
	
	public AudioLanguageInfo() {
		super();
	}

	/**
	 * @brief Get totalNumber
	 * @param None
	 * @return totalNumber
	 */
	public int getTotalNumber() {
		return totalNumber;
	}
	
	/**
	 * @brief Set alternative of audio
	 * @param None
	 * @return None
	 */
	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}
	/**
	 * @brief Get eChannel
	 * @param None
	 * @return eChannel
	 */
	public int getCurrentAudioLangIndex() {
		return currentAudioLangIndex;
	}
	
	/**
	 * @brief Set eChannel
	 * @param None
	 * @return None
	 */
	public void setCurrentAudioLangIndex(int currentAudioLangIndex) {
		this.currentAudioLangIndex = currentAudioLangIndex;
	}
	/**
	 * @brief Get eChannel
	 * @param None
	 * @return eChannel
	 */
	public int getDigitalMts() {
		return digitalMts;
	}
	
	/**
	 * @brief Set eChannel
	 * @param None
	 * @return None
	 */
	public void setDigitalMts(int digitalMts) {
		this.digitalMts = digitalMts;
	}
	
	/**
	 * @brief Get audio decode type
	 * @param None
	 * @return audio decode type
	 */
	public int getAudioDecodeType() {
		return audioDecodeType;
	}
	
	/**
	 * @brief Set audio decode type
	 * @param None
	 * @return None
	 */
	public void setAudioDecodeType(int audioDecodeType) {
		this.audioDecodeType = audioDecodeType;
	}
	
	/**
	 * @brief Get all of the audio language
	 * @param None
	 * @return audio language
	 */
	public String getAudioLanguage() {
		return audioLanguage;
	}
	/**
	 * @brief Set using audio language
	 * @param None
	 * @return None
	 */
	public void setCurrentLanguage(String currentLanguage){
		this.currentLanguage = currentLanguage;
	}
	/**
	 * @brief Get using audio language
	 * @param None
	 * @return currentLanguage
	 */
	public String getCurrentLanguage(){
		return this.currentLanguage;
	}
	/**
	 * @brief Set language of audio
	 * @param None
	 * @return None
	 */
	public void setAudioLanguage(String audioLanguage) {
		this.audioLanguage = audioLanguage;
	}

    public static final Creator<AudioLanguageInfo> CREATOR = new Parcelable.Creator<AudioLanguageInfo>() {
        public AudioLanguageInfo createFromParcel(Parcel source) {
            return new AudioLanguageInfo(source);
        }

        public AudioLanguageInfo[] newArray(int size) {
            return new AudioLanguageInfo[size];
        }
    };

    private AudioLanguageInfo(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(audioLanguage);
        out.writeString(currentLanguage);
        out.writeInt(totalNumber);
        out.writeInt(digitalMts);
        out.writeInt(currentAudioLangIndex);
        out.writeInt(audioDecodeType);
    }

    public void readFromParcel(Parcel in) {
    	audioLanguage        = in.readString();
    	currentLanguage        = in.readString();
        totalNumber      = in.readInt();
        digitalMts       = in.readInt();
        currentAudioLangIndex       = in.readInt();
        audioDecodeType  = in.readInt();
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
