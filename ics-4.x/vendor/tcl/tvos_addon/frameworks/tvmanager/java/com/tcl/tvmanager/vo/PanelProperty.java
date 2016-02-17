package com.tcl.tvmanager.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class PanelProperty implements Parcelable {
    public int width;
    public int height;

    public PanelProperty() {
        width = 0;
        height = 0;
    }

    public PanelProperty(Parcel in) {
        width = in.readInt();
        height = in.readInt();
    }

    public static final Parcelable.Creator<PanelProperty> CREATOR = new Parcelable.Creator<PanelProperty>() {
        public PanelProperty createFromParcel(Parcel in) {
            return new PanelProperty(in);
        }

        public PanelProperty[] newArray(int size) {
            return new PanelProperty[size];
        }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeInt(width);
        dest.writeInt(height);
    }
}