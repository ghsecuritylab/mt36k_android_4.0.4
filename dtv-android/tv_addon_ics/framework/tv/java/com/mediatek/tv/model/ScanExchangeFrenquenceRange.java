package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ScanExchangeFrenquenceRange implements Parcelable {
    public int tunerLowerBound;
    public int tunerUpperBound;

    protected int setTunerLowerBound(int tunerLowerBound) {
        this.tunerLowerBound = tunerLowerBound;
        return 0;
    }

    protected int setTunerUpperBound(int tunerUpperBound) {
        this.tunerUpperBound = tunerUpperBound;
        return 0;
    }

    public ScanExchangeFrenquenceRange() {
        tunerLowerBound = 0;
        tunerUpperBound = 0;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public static final Creator<ScanExchangeFrenquenceRange> CREATOR = new Parcelable.Creator<ScanExchangeFrenquenceRange>() {
        public ScanExchangeFrenquenceRange createFromParcel(Parcel source) {
            return new ScanExchangeFrenquenceRange(source);
        }

        public ScanExchangeFrenquenceRange[] newArray(int size) {
            return new ScanExchangeFrenquenceRange[size];
        }
    };

    private ScanExchangeFrenquenceRange(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(tunerLowerBound);
        out.writeInt(tunerUpperBound);
    }

    public void readFromParcel(Parcel in) {
        tunerLowerBound = in.readInt();
        tunerUpperBound = in.readInt();
    }
}
