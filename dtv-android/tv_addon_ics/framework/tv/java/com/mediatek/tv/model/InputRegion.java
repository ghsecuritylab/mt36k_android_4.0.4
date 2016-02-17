package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class InputRegion implements Parcelable {
    private int left;
    private int right;
    private int top;
    private int bottom;
    
    public InputRegion(){
        this.bottom = this.left = this.right = this.top = 0;
    }
    
    public int getLeft() {
        return left;
    }
    public int getRight() {
        return right;
    }
    public int getTop() {
        return top;
    }
    public int getBottom() {
        return bottom;
    }
    public void setLeft(int left) {
        this.left = left;
    }
    public void setRight(int right) {
        this.right = right;
    }
    public void setTop(int top) {
        this.top = top;
    }
    public void setBottom(int bottom) {
        this.bottom = bottom;
    }
    
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public static final Creator<InputRegion> CREATOR = new Parcelable.Creator<InputRegion>() {
        public InputRegion createFromParcel(Parcel source) {
            return new InputRegion(source);
        }

        public InputRegion[] newArray(int size) {
            return new InputRegion[size];
        }
    };

    private InputRegion(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(left);
        out.writeInt(right);
        out.writeInt(top);
        out.writeInt(bottom);
    }

    public void readFromParcel(Parcel in) {
        left = in.readInt();
        right = in.readInt();
        top = in.readInt();
        bottom = in.readInt();
    }
}
