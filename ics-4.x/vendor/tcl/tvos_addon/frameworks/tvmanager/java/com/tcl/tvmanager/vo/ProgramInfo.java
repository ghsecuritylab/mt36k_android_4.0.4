package com.tcl.tvmanager.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class ProgramInfo implements Parcelable {
    // / program information of program number
    public int number;  //频道号  
    // / Program skip
    public boolean isSkip;  
    // / Program delete
    public boolean isDelete;
    // / Program Hide
    public boolean isHide;  //目前setProgramAttribute只实现对该属性的更改
    // / Program service type
    public EnTCLMemberServiceType serviceType;
    
    /*===================zhang ming add=================================*/
    public int index;  //数组下标
    
    public String programName;
    public int freq;
    public int antennaType;
    
    public boolean isFavorite;
    public boolean isLock;
    public boolean isScramble;
    public int serviceID;
    public int transportStreamID;
    

    public ProgramInfo() {
        number = 0;
        isSkip = false;
        isDelete = false;
        isHide = false;
        serviceType = EnTCLMemberServiceType.EN_TCL_ATV;
        index = 0;
        programName = " ";
        freq = 0;
        antennaType = 0;
        isFavorite = false;
        isLock = false;
        isScramble = false;
        serviceID = 0 ;
        transportStreamID = 0;
    }

    public static final Parcelable.Creator<ProgramInfo> CREATOR = new Parcelable.Creator<ProgramInfo>() {

        @Override
        public ProgramInfo createFromParcel(Parcel in) {
            return new ProgramInfo(in);
        }

        @Override
        public ProgramInfo[] newArray(int size) {
            return new ProgramInfo[size];
        }
    };

    private ProgramInfo(Parcel in) {
        super();
        number = in.readInt();
        isSkip = in.readInt() == 1;
        isDelete = in.readInt() == 1;
        isHide = in.readInt() == 1;
        serviceType = EnTCLMemberServiceType.values()[in.readInt()];
        index = in.readInt();
        programName = in.readString();
        freq = in.readInt();
        antennaType = in.readInt();
        isFavorite = in.readInt() == 1;
        isLock = in.readInt() == 1;
        isScramble = in.readInt() == 1;
        serviceID = in.readInt();
        transportStreamID = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {
        out.writeInt(number);
        out.writeInt(isSkip ? 1 : 0);
        out.writeInt(isDelete ? 1 : 0);
        out.writeInt(isHide ? 1 : 0);
        out.writeInt(serviceType.ordinal());
        out.writeInt(index);
        out.writeString(programName);
        out.writeInt(freq);
        out.writeInt(antennaType);
        out.writeInt(isFavorite ? 1:0);
        out.writeInt(isLock ? 1:0);
        out.writeInt(isScramble ? 1:0);
        out.writeInt(serviceID);
        out.writeInt(transportStreamID);
    }

    @Override
    public String toString() {
        return "number=" + number + ", freq=" + freq + ", isSkip=" + isSkip;
    }
}
