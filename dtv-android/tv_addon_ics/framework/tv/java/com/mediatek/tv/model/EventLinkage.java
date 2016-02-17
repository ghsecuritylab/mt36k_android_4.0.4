package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class EventLinkage implements Parcelable {
    private int onId;
    private int tsId;
    private int svcId;

    public EventLinkage() {
        super();
    }

    public EventLinkage(int onId, int tsId, int svcId) {
        super();
        this.onId = onId;
        this.tsId = tsId;
        this.svcId = svcId;
    }

    public int getOnId() {
        return onId;
    }

    public void setOnId(int onId) {
        this.onId = onId;
    }

    public int getTsId() {
        return tsId;
    }

    public void setTsId(int tsId) {
        this.tsId = tsId;
    }

    public int getSvcId() {
        return svcId;
    }

    public void setSvcId(int svcId) {
        this.svcId = svcId;
    }

    public int describeContents() {
        return 0;
    }

    public static final Creator<EventLinkage> CREATOR = new Parcelable.Creator<EventLinkage>() {
        public EventLinkage createFromParcel(Parcel source) {
            return new EventLinkage(source);
        }

        public EventLinkage[] newArray(int size) {
            return new EventLinkage[size];
        }
    };

    private EventLinkage(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(onId);
        out.writeInt(tsId);
        out.writeInt(svcId);
    }

    public void readFromParcel(Parcel in) {
        onId = in.readInt();
        tsId = in.readInt();
        svcId = in.readInt();
    }
}


