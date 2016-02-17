package com.mediatek.tv.model;

import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

public class InputRecord implements Parcelable {
    public int inputType; /* the value is from INPS_TYPE_TV to INPS_TYPE_THDMI */
    public int gourp[]; /* come from t_isl_rec */
    public int gourpSize;
    public int internalIdx; /* come from t_isl_rec, the index among same type */
    public int id; /* come from t_isl_rec, the only id in all of input source */

    public void reset() {
        this.inputType = 0;
        this.gourp = null;
        this.gourpSize = 0;
        this.internalIdx = 0;
        this.id = 0;
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public int[] getGourp() {
        return gourp;
    }

    public void setGourp(int[] gourp) {
        this.gourp = gourp;
    }

    public int getGourpSize() {
        return gourpSize;
    }

    public void setGourpSize(int gourpSize) {
        this.gourpSize = gourpSize;
    }

    public int getInternalIdx() {
        return internalIdx;
    }

    public void setInternalIdx(int internalIdx) {
        this.internalIdx = internalIdx;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public static final Creator<InputRecord> CREATOR = new Parcelable.Creator<InputRecord>() {
        public InputRecord createFromParcel(Parcel source) {
            return new InputRecord(source);
        }

        public InputRecord[] newArray(int size) {
            return new InputRecord[size];
        }
    };

    private InputRecord(Parcel source) {
        readFromParcel(source);
    }

    public InputRecord() {
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(inputType);
        if (gourp != null) {
            out.writeInt(gourp.length);
            out.writeIntArray(gourp);
        } else {
            out.writeInt(0);
        }
        out.writeInt(gourpSize);
        out.writeInt(internalIdx);
        out.writeInt(id);
    }

    public void readFromParcel(Parcel in) {
        inputType = in.readInt();
        int size = in.readInt();
        if (size > 0) {
            gourp = new int[size];
            in.readIntArray(gourp);
        }
        gourpSize = in.readInt();
        internalIdx = in.readInt();
        id = in.readInt();
    }

    public String toString() {
        return "InputRecord [inputType=" + inputType + ", gourp=" + Arrays.toString(gourp) + ", gourpSize=" + gourpSize
                + ", internalIdx=" + internalIdx + ", id=" + id + "]";
    }
}
