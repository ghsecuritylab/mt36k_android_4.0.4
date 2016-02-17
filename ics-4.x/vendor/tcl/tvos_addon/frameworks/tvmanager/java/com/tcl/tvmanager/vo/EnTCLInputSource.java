package com.tcl.tvmanager.vo;
import android.os.Parcel;
import android.os.Parcelable;

public enum EnTCLInputSource implements Parcelable{
    EN_TCL_NONE,
    EN_TCL_ATV,
    EN_TCL_DTV,
    EN_TCL_AV1,
    EN_TCL_AV2,
    EN_TCL_YPBPR,
    EN_TCL_YPBPR2,
    EN_TCL_YPBPR3,
    EN_TCL_HDMI1,
    EN_TCL_HDMI2,
    EN_TCL_HDMI3,
    EN_TCL_HDMI4,
    EN_TCL_VGA,
    EN_TCL_DV,
    EN_TCL_DV2,
    EN_TCL_DV3,
    EN_TCL_DV4,
    EN_TCL_STORAGE,
    EN_TCL_STORAGE2,
    EN_TCL_KTV,
    EN_TCL_OSD,
    EN_TCL_MAX;
    public static final Parcelable.Creator<EnTCLInputSource> CREATOR
            = new Parcelable.Creator<EnTCLInputSource>() {

                @Override
                public EnTCLInputSource createFromParcel(Parcel in) {
                    return EnTCLInputSource.values()[in.readInt()];
                }

                @Override
                public EnTCLInputSource[] newArray(int size) {
                    return new EnTCLInputSource[size];
                }
            };
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {
        out.writeInt(ordinal());
    }
}
