package com.mediatek.tv.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ChannelModel implements Parcelable {
    private List<ChannelInfo> channelList = new ArrayList<ChannelInfo>();

    public ChannelModel() {
        super();
    }

    public ChannelModel(List<ChannelInfo> channelList) {
        this.channelList = channelList;
    }

    public List<ChannelInfo> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<ChannelInfo> channelList) {
        this.channelList = channelList;
    }

    public int describeContents() {
        return 0;
    }

    public static final Creator<ChannelModel> CREATOR = new Parcelable.Creator<ChannelModel>() {
        public ChannelModel createFromParcel(Parcel source) {
            return new ChannelModel(source);
        }

        public ChannelModel[] newArray(int size) {
            return new ChannelModel[size];
        }
    };

    private ChannelModel(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        if (channelList != null) {
            out.writeInt(channelList.size());
            for (int i = 0; i < channelList.size(); i++) {
                out.writeParcelable(channelList.get(i), flags);
            }
        }
    }

    public void readFromParcel(Parcel in) {
        int len = in.readInt();
        for (int i = 0; i < len; i++) {
            channelList.add((ChannelInfo) in.readParcelable(null));
        }
    }
}
