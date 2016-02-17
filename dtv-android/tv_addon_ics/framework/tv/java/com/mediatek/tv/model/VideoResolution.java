package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoResolution implements Parcelable {
	/**
	 * The width of video
	 */
	protected int mWidth;

	/**
	 * The height of video
	 */
	protected int mHeight;

	/**
	 * The frame rate of video
	 */
	protected int mFrameRate;
	
	/**
	 * Is progressive
	 */
	protected boolean mIsProgressive;	

	/**
	 * Is progressive
	 */
	protected String videoFormat;

	public VideoResolution() {
		super();
	}

	/**
	 * @brief Get width of video
	 * @param None
	 * @return video width
	 */
	public int getVideoWidth() {
		return mWidth;
	}

	/**
	 * @brief Set width of video
	 * @param width
	 *            width of video
	 * @return
	 */
	public void setVideoWidth(int width) {
		this.mWidth = width;
	}
	
	/**
	 * @brief Get height of video
	 * @param None
	 * @return video height
	 */
	public int getVideoHeight() {
		return mHeight;
	}

	/**
	 * @brief Set height of video
	 * @param height
	 *            height of video
	 * @return
	 */
	public void setVideoHeight(int height) {
		this.mHeight = height;
	}	
	
	/**
	 * @brief Get frame rate of video
	 * @param None
	 * @return video frame rate
	 */
	public int getVideoFrameRate() {
		return mFrameRate;
	}

	/**
	 * @brief Set frame rate of video
	 * @param height
	 *            frame rate of video
	 * @return
	 */
	public void setVideoFrameRate(int framerate) {
		this.mFrameRate = framerate;
	}		
	
	/**
	 * @brief Get the progressive
	 * @param None
	 * @return progressive or interleaved
	 */
	public boolean getProgressive() {
		return mIsProgressive;
	}

	/**
	 * @brief Set the progressive
	 * @param isProgressive
	 *            indicator the progressive or interleaved
	 * @return
	 */
	public void setProgressive(boolean isProgressive) {
		this.mIsProgressive = isProgressive;
	}		
		
	/**
	 * @brief Get the videoformat
	 * @param None
	 * @return progressive or interleaved
	 */
	public String getVideoFormat() {
		return videoFormat;
	}

	/**
	 * @brief Set the videoformat
	 * @param videoFormat
	 *            videoFormat HD or SD
	 * @return
	 */
	public void setVideoFormat(String videoFormat) {
		this.videoFormat = videoFormat;
	}	
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public static final Creator<VideoResolution> CREATOR = new Parcelable.Creator<VideoResolution>() {
        public VideoResolution createFromParcel(Parcel source) {
            return new VideoResolution(source);
        }

        public VideoResolution[] newArray(int size) {
            return new VideoResolution[size];
        }
    };

    private VideoResolution(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mWidth);
        out.writeInt(mHeight);
        out.writeInt(mFrameRate);
        out.writeInt(mIsProgressive ? 1 : 0);
        out.writeString(videoFormat);
    }

    public void readFromParcel(Parcel in) {
		videoFormat = in.readString();
        mWidth = in.readInt();
        mHeight = in.readInt();
        mFrameRate = in.readInt();
        mIsProgressive = ((in.readInt() == 1) ? true : false);
    }
}
