package com.mediatek.tv.model;

import java.util.Arrays;

import com.mediatek.tv.common.ChannelCommon;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Channel object include channel id,channel name
 * 
 * @param <T>
 * 
 */

public class ChannelInfo implements Parcelable {
    private static int PRIVATE_DATA_LEN = 20;

    /**
     * The SVL id,identify the database
     */
    protected int svlId;

    /**
     * The SVL Record ID,Identify a record,must be unique
     */
    protected int svlRecId;

    /**
     * The channel id ,reserve for digital used
     */
    protected int channelId;

    /**
	 * The brdcstType , e.g. BRDCST_TYPE_DVB BRDCST_TYPE_DTMB BRDCST_TYPE_ANALOG
	 * BRDCST_TYPE_DVB
	 * 
	 */
	protected int brdcstType;

	/**
     * The channel id ,reserve for digital used
     */
    protected int nwMask;

    protected int optionMask;

    protected int serviceType;

    /**
     * The channel id ,reserve for digital used
     */
    protected int channelNumber;

    /**
     * The service name the length should be less than TVGlobal.MAX_PROG_NAME_LEN
     */
    protected String serviceName;

    protected byte[] privateData = new byte[PRIVATE_DATA_LEN];

    public ChannelInfo() {
        super();
    }

    public ChannelInfo(int svlid, int svlRecId) {
        super();
        this.svlId = svlid;
        this.svlRecId = svlRecId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    /**
     * @return the svlId
     */
    public int getSvlId() {
        return svlId;
    }

    /**
     * @param svlId
     *            the svlId to set
     */
    protected void setSvlId(int svlId) {
        this.svlId = svlId;
    }

    /**
     * @return the svlRecId
     */
    public int getSvlRecId() {
        return svlRecId;
    }

    /**
     * @param svlRecId
     *            the svlRecId to set
     */
    protected void setSvlRecId(int svlRecId) {
        this.svlRecId = svlRecId;
    }

    /**
     * @return the privateData
     */
    public byte[] getPrivateData() {
        return privateData;
    }

    /**
     * @param _privateData
     *            the privateData to set
     */
    public void setPrivateData(byte[] _privateData) {
        if (_privateData == null) {
            System.err.println("setPrivateData fail");
            return;
        }
        if (_privateData.length > PRIVATE_DATA_LEN) {
            System.err.println("setPrivateData fail length grate than 20");
            return;
        }
        System.arraycopy(_privateData, 0, this.privateData, 0, _privateData.length);
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    public int getNwMask() {
        return nwMask;
    }

    public void setNwMask(int nwMask) {
        this.nwMask = nwMask;
    }

    public int getOptionMask() {
        return optionMask;
    }

    public void setOptionMask(int optionMask) {
        this.optionMask = optionMask;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    /* util function */
    public boolean isUserDelete() {
        return ((optionMask & ChannelCommon.SB_VOPT_DELETED_BY_USER) > 0) ? true : false;
    }

    public boolean isVisible() {
        return ((nwMask & ChannelCommon.SB_VNET_VISIBLE) > 0) ? true : false;
    }

    public boolean isEpgVisible() {
        return ((nwMask & ChannelCommon.SB_VNET_EPG) > 0) ? true : false;
    }

    public boolean isRadioService() {
        return ((nwMask & ChannelCommon.SB_VNET_RADIO_SERVICE) > 0) ? true : false;
    }

    public boolean isAnalogService() {
        return ((nwMask & ChannelCommon.SB_VNET_ANALOG_SERVICE) > 0) ? true : false;
    }

    public boolean isTvService() {
        return ((nwMask & ChannelCommon.SB_VNET_TV_SERVICE) > 0) ? true : false;
    }

    public void setChannelNameEdited(boolean flag) {
        if (flag) {
            nwMask |= ChannelCommon.SB_VNET_CH_NAME_EDITED;
            optionMask |= ChannelCommon.SB_VOPT_CH_NAME_EDITED;
        } else {
            nwMask &= ~(ChannelCommon.SB_VNET_CH_NAME_EDITED);
            optionMask &= ~(ChannelCommon.SB_VOPT_CH_NAME_EDITED);
        }
    }

    public void setFrequencyEdited(boolean flag) {
        if (flag) {
            nwMask |= ChannelCommon.SB_VNET_FREQ_EDITED;
            optionMask |= ChannelCommon.SB_VOPT_FREQ_EDITED;
        } else {
            nwMask &= ~(ChannelCommon.SB_VNET_FREQ_EDITED);
            optionMask &= ~(ChannelCommon.SB_VOPT_FREQ_EDITED);
        }
    }

    public void setChannelNumberEdited(boolean flag) {
        if (flag) {
            optionMask |= ChannelCommon.SB_VOPT_CH_NUM_EDITED;
        } else {
            optionMask &= ~(ChannelCommon.SB_VOPT_CH_NUM_EDITED);
        }
    }

    public void setChannelDeleted(boolean flag) {
        if (flag) {
            optionMask |= ChannelCommon.SB_VOPT_DELETED_BY_USER;
        } else {
            optionMask &= ~(ChannelCommon.SB_VOPT_DELETED_BY_USER);
        }
    }

	public int getBrdcstType() {
		return brdcstType;
	}

	public void setBrdcstType(int brdcstType) {
		this.brdcstType = brdcstType;
	}

    public String toString() {
		return "ChannelInfo [svlId=" + svlId + " , svlRecId=" + svlRecId
				+ " , channelId=" + channelId + " , brdcstType=" + brdcstType
				+ " , nwMask=" + nwMask + " , optionMask=" + optionMask
				+ " , serviceType=" + serviceType + " , channelNumber="
				+ channelNumber + " , serviceName=" + serviceName
				+ " , privateData=" + Arrays.toString(privateData) + "]";
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + channelId;
        result = prime * result + svlId;
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChannelInfo other = (ChannelInfo) obj;
        if (channelId != other.channelId)
            return false;
        if (svlId != other.svlId)
            return false;
        if (svlRecId != other.svlRecId)
            return false;
        return true;
    }

    public int describeContents() {
        return 0;
    }

    public static final Creator<ChannelInfo> CREATOR = new Parcelable.Creator<ChannelInfo>() {
        public ChannelInfo createFromParcel(Parcel source) {
            return new ChannelInfo(source);
        }

        public ChannelInfo[] newArray(int size) {
            return new ChannelInfo[size];
        }
    };

    private ChannelInfo(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(svlId);
        out.writeInt(svlRecId);
        out.writeInt(channelId);
		out.writeInt(brdcstType);
        out.writeInt(nwMask);
        out.writeInt(optionMask);
        out.writeInt(serviceType);
        out.writeInt(channelNumber);
        out.writeString(serviceName);
        out.writeByteArray(privateData);
    }

    public void readFromParcel(Parcel in) {
        svlId = in.readInt();
        svlRecId = in.readInt();
        channelId = in.readInt();
		brdcstType = in.readInt();
        nwMask = in.readInt();
        optionMask = in.readInt();
        serviceType = in.readInt();
        channelNumber = in.readInt();
        serviceName = in.readString();
        in.readByteArray(privateData);
    }
}
