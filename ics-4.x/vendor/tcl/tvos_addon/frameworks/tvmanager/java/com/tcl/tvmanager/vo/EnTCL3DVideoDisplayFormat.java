package com.tcl.tvmanager.vo;

import android.os.Parcel;
import android.os.Parcelable;

public enum EnTCL3DVideoDisplayFormat implements Parcelable {
    // / 3D Off mode
    EN_TCL_3D_NONE,
    // / 3D Side By Side mode
    EN_TCL_3D_SIDE_BY_SIDE,
    // / 3D Top Bottom mode
    EN_TCL_3D_TOP_BOTTOM,
    // / 3D Frame Packing mode
    EN_TCL_3D_FRAME_PACKING,
    // / 3D Line Alternative mode
    EN_TCL_3D_LINE_ALTERNATIVE,
    // / 3D 2Dto3D mode
    EN_TCL_3D_2DTO3D,
    // / 3D Auto mode
    EN_TCL_3D_AUTO,
    // / 3D Check Board mode
    EN_TCL_3D_CHECK_BOARD,
    // / 3D Pixel Alternative mode
    EN_TCL_3D_PIXEL_ALTERNATIVE,
    // / 3D Frame Alternative
    EN_TCL_3D_ALTERNATIVE,
    // / total format number
    EN_TCL_3D_MAX;

    public static final Parcelable.Creator<EnTCL3DVideoDisplayFormat> CREATOR = new Parcelable.Creator<EnTCL3DVideoDisplayFormat>() {
        public EnTCL3DVideoDisplayFormat createFromParcel(Parcel in) {
            return EnTCL3DVideoDisplayFormat.values()[in.readInt()];
        }

        public EnTCL3DVideoDisplayFormat[] newArray(int size) {
            return new EnTCL3DVideoDisplayFormat[size];
        }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ordinal());
    }
}

