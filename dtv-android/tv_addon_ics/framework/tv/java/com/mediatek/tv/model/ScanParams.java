/*
 * Copyright (C) 2010 MediaTek USA, Inc
 *
 */

package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class contains scan parameters.
 */
@SuppressWarnings("unused")
public class ScanParams implements Parcelable {
    private static final String TAG = "ScanParams";

    /**
     * Full Scan.
     */
    public static final int SCAN_TYPE_FULL = 1;
    /**
     * Partial scan based on channel number.
     */
    public static final int SCAN_TYPE_CHANNEL = 2;
    /**
     * Partial scan based on frequency.
     */
    public static final int SCAN_TYPE_FREQUENCY = 3;

    protected int mScanType;
    protected int mScanChannelFrom;
    protected int mScanChannelTo;
    protected int mScanFreqFrom;
    protected int mScanFreqTo;

    /**
     * Create a new ScanParams instance and set scan type SCAN_TYPE_FULL
     */
    public ScanParams() {
        mScanType = SCAN_TYPE_FULL;
        mScanChannelFrom = 0;
        mScanChannelTo = 0;
        mScanFreqFrom = 0;
        mScanFreqTo = 0;
    }

    /**
     * Create a new ScanParams instance
     * 
     * @param scanType
     *            one of the supported scan types
     * @param scanFrom
     *            the lower bound of scan range
     * @param scanTo
     *            the upper bound of scan range
     */
    public ScanParams(int scanType, int scanFrom, int scanTo) {
        if (scanType == SCAN_TYPE_CHANNEL) {
            mScanType = scanType;
            mScanChannelFrom = scanFrom;
            mScanChannelTo = scanTo;
            mScanFreqFrom = 0;
            mScanFreqTo = 0;
        } else if (scanType == SCAN_TYPE_FREQUENCY) {
            mScanType = scanType;
            mScanFreqFrom = scanFrom;
            mScanFreqTo = scanTo;
            mScanChannelFrom = 0;
            mScanChannelTo = 0;
        } else {
            mScanType = SCAN_TYPE_FULL;
            mScanChannelFrom = 0;
            mScanChannelTo = 0;
            mScanFreqFrom = 0;
            mScanFreqTo = 0;
        }
    }

    protected int getmScanType() {
        return mScanType;
    }

    protected int getmScanChannelFrom() {
        return mScanChannelFrom;
    }

    protected int getmScanChannelTo() {
        return mScanChannelTo;
    }

    protected int getmScanFreqFrom() {
        return mScanFreqFrom;
    }

    protected int getmScanFreqTo() {
        return mScanFreqTo;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public static final Creator<ScanParams> CREATOR = new Parcelable.Creator<ScanParams>() {
        public ScanParams createFromParcel(Parcel source) {
            return new ScanParams(source);
        }

        public ScanParams[] newArray(int size) {
            return new ScanParams[size];
        }
    };

    private ScanParams(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mScanType);
        out.writeInt(mScanChannelFrom);
        out.writeInt(mScanChannelTo);
        out.writeInt(mScanFreqFrom);
        out.writeInt(mScanFreqTo);

    }

    public void readFromParcel(Parcel in) {
        mScanType = in.readInt();
        mScanChannelFrom = in.readInt();
        mScanChannelTo = in.readInt();
        mScanFreqFrom = in.readInt();
        mScanFreqTo = in.readInt();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ScanParams [mScanType=");
        builder.append(mScanType);
        builder.append(", mScanChannelFrom=");
        builder.append(mScanChannelFrom);
        builder.append(", mScanChannelTo=");
        builder.append(mScanChannelTo);
        builder.append(", mScanFreqFrom=");
        builder.append(mScanFreqFrom);
        builder.append(", mScanFreqTo=");
        builder.append(mScanFreqTo);
        builder.append("]");
        return builder.toString();
    }

}
