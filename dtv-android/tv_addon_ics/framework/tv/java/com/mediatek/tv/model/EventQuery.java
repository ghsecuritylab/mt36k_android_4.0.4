package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class EventQuery implements Parcelable {
    private ChannelInfo channelInfo = new ChannelInfo();
    private long startTime;
    private long endTime;

    public EventQuery() {
        super();
    }

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int describeContents() {
        return 0;
    }

    public static final Creator<EventQuery> CREATOR = new Parcelable.Creator<EventQuery>() {
        public EventQuery createFromParcel(Parcel source) {
            return new EventQuery(source);
        }

        public EventQuery[] newArray(int size) {
            return new EventQuery[size];
        }
    };

    private EventQuery(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(channelInfo, flags);
        out.writeLong(startTime);
        out.writeLong(endTime);
    }

    public void readFromParcel(Parcel in) {
        channelInfo = in.readParcelable(null);
        startTime = in.readLong();
        endTime = in.readLong();
    }
}
