package com.mediatek.tv.model;

import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

import com.mediatek.tv.common.ChannelCommon;

/**
 * Analog Channel object include tv system ,audio system ...
 * 
 */

public class AnalogChannelInfo extends ChannelInfo implements Parcelable {
    /**
     * Bit mask for TV system (B/G, D/K, I, M, N, L/L', etc)
     */
    private int tvSys;

    /**
     * Bit mask for Audio system (AM, FM, NICAM, BTSC)
     */
    private int audioSys;

    /**
     * For TV color systems (NTSC, PAL, SECAM,)
     */
    private int colorSys;

    /**
     * For frequency 522,250,000Hz
     * 
     */
    private int frequency;

    private boolean noAutoFineTune;

    /**
     * For Current brdcst Medium e.g. ANA_TERRESTRIAL ANA_CABLE ANA_SATELLITE
     */
    private byte brdcstMedium;

    public AnalogChannelInfo() {
        super();
        this.svlId = ChannelCommon.getSvlIdByName(ChannelCommon.DB_ANALOG);
    }

    public AnalogChannelInfo(String dbName) {
        super();
        this.svlId = ChannelCommon.getSvlIdByName(dbName);
    }
    

    /**
     * @param svlid
     * @param svlRecId
     * 
     */
    public AnalogChannelInfo(int svlid, int svlRecId) {
        super(svlid, svlRecId);
    }
    

    /**
     * @return the tvSys
     */
    public int getTvSys() {
        return tvSys;
    }

    /**
     * @param tvSys
     *            the tvSys to set
     */
    public void setTvSys(int tvSys) {
        this.tvSys = tvSys;
    }

    /**
     * @return the audioSys
     */
    public int getAudioSys() {
        return audioSys;
    }

    /**
     * @param audioSys
     *            the audioSys to set
     */
    public void setAudioSys(int audioSys) {
        this.audioSys = audioSys;
    }

    /**
     * @return the colorSys
     */
    public int getColorSys() {
        return colorSys;
    }

    /**
     * @param colorSys
     *            the colorSys to set
     */
    public void setColorSys(int colorSys) {
        this.colorSys = colorSys;
    }

    /**
     * @return the frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * @param frequency
     *            the frequency to set
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * @return the brdcstMedium
     */
    public byte getBrdcstMedium() {
        return brdcstMedium;
    }

    /**
     * @param brdcstMedium
     *            the brdcstMedium to set
     */
    public void setBrdcstMedium(byte brdcstMedium) {
        this.brdcstMedium = brdcstMedium;
    }

    /**
     * @return the noAutoFineTune
     */
    public boolean isNoAutoFineTune() {
        return noAutoFineTune;
    }

    /**
     * @param noAutoFineTune
     *            the noAutoFineTune to set
     */
    public void setNoAutoFineTune(boolean noAutoFineTune) {
        this.noAutoFineTune = noAutoFineTune;
    }

    

    public String toString() {
	    return "AnalogChannelInfo [svlId=" + svlId + " , svlRecId=" + svlRecId + " , channelId=" + channelId
	            + " , nwMask=" + nwMask + " , optionMask=" + optionMask + " , serviceType=" + serviceType
	            + " , channelNumber=" + channelNumber + " , serviceName=" + serviceName + " , privateData="
	            + Arrays.toString(privateData) + " , tvSys=" + tvSys + " , audioSys=" + audioSys + " , colorSys="
	            + colorSys + " , frequency=" + frequency + " , noAutoFineTune=" + noAutoFineTune + " , brdcstMedium="
	            + brdcstMedium + "]";
    }



	public static final Creator<AnalogChannelInfo> CREATOR = new Parcelable.Creator<AnalogChannelInfo>() {
        public AnalogChannelInfo createFromParcel(Parcel source) {
            return new AnalogChannelInfo(source);
        }

        public AnalogChannelInfo[] newArray(int size) {
            return new AnalogChannelInfo[size];
        }
    };

    private AnalogChannelInfo(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(audioSys);
        out.writeByte(brdcstMedium);
        out.writeInt(colorSys);
        out.writeInt(frequency);
        out.writeInt(noAutoFineTune ? 1 : 0);
        out.writeInt(tvSys);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        audioSys = in.readInt();
        brdcstMedium = in.readByte();
        colorSys = in.readInt();
        frequency = in.readInt();
        noAutoFineTune = (in.readInt() == 1 ? true : false);
        tvSys = in.readInt();
    }
}
