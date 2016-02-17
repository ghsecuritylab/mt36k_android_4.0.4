package com.mediatek.tv.model;

import java.util.Arrays;

import com.mediatek.tv.common.ChannelCommon;

import android.os.Parcel;
import android.os.Parcelable;

public class DvbChannelInfo extends ChannelInfo implements Parcelable {
    private int brdcstMedium;
    private int frequency;
    private String shortName = "";
    private int bandWidth;
    private int nwId;
    private int onId;
    private int tsId;
    private int progId;
    private int symRate;
    private int mod;

    public DvbChannelInfo() {
        super();
    }

    public DvbChannelInfo(String dbName) {
        super();
        this.svlId = ChannelCommon.getSvlIdByName(dbName);
    }

    public DvbChannelInfo(int svlid, int svlRecId) {
        super(svlid, svlRecId);
    }

    public int getBrdcstMedium() {
        return brdcstMedium;
    }

    public void setBrdcstMedium(int brdcstMedium) {
        this.brdcstMedium = brdcstMedium;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getBandWidth() {
        return bandWidth;
    }

    public void setBandWidth(int bandWidth) {
        this.bandWidth = bandWidth;
    }

    public int getNwId() {
        return nwId;
    }

    public void setNwId(int nwId) {
        this.nwId = nwId;
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

    public int getProgId() {
        return progId;
    }

    public void setProgId(int progId) {
        this.progId = progId;
    }

    public int getSymRate() {
        return symRate;
    }

    public void setSymRate(int symRate) {
        this.symRate = symRate;
    }

    public int getMod() {
        return mod;
    }

    public void setMod(int mod) {
        this.mod = mod;
    }

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DvbChannelInfo [brdcstMedium=");
		builder.append(brdcstMedium);
		builder.append(", brdcstType=");
		builder.append(brdcstType);
		builder.append(", frequency=");
		builder.append(frequency);
		builder.append(", shortName=");
		builder.append(shortName);
		builder.append(", bandWidth=");
		builder.append(bandWidth);
		builder.append(", nwId=");
		builder.append(nwId);
		builder.append(", onId=");
		builder.append(onId);
		builder.append(", tsId=");
		builder.append(tsId);
		builder.append(", progId=");
		builder.append(progId);
		builder.append(", symRate=");
		builder.append(symRate);
		builder.append(", mod=");
		builder.append(mod);
		builder.append(", svlId=");
		builder.append(svlId);
		builder.append(", svlRecId=");
		builder.append(svlRecId);
		builder.append(", channelId=");
		builder.append(channelId);
		builder.append(", nwMask=");
		builder.append(nwMask);
		builder.append(", optionMask=");
		builder.append(optionMask);
		builder.append(", serviceType=");
		builder.append(serviceType);
		builder.append(", channelNumber=");
		builder.append(channelNumber);
		builder.append(", serviceName=");
		builder.append(serviceName);
		builder.append(", privateData=");
		builder.append(Arrays.toString(privateData));
		builder.append("]");
		return builder.toString();
	}

    public static final Creator<DvbChannelInfo> CREATOR = new Parcelable.Creator<DvbChannelInfo>() {
        public DvbChannelInfo createFromParcel(Parcel source) {
            return new DvbChannelInfo(source);
        }

        public DvbChannelInfo[] newArray(int size) {
            return new DvbChannelInfo[size];
        }
    };

    private DvbChannelInfo(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(brdcstMedium);
        out.writeInt(frequency);
        out.writeString(shortName);
        out.writeInt(bandWidth);
        out.writeInt(nwId);
        out.writeInt(onId);
        out.writeInt(tsId);
        out.writeInt(progId);
        out.writeInt(symRate);
        out.writeInt(mod);

    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        brdcstMedium = in.readInt();
        frequency = in.readInt();
        shortName = in.readString();
        bandWidth = in.readInt();
        nwId = in.readInt();
        onId = in.readInt();
        tsId = in.readInt();
        progId = in.readInt();
        symRate = in.readInt();
        mod = in.readInt();
    }
}
