package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.service.ITVRemoteService;

public class ScanParaDtmb extends ScanParams implements Parcelable
{
    /*return value*/
    public static final int SCAN_DTMB_PARA_OK    = 0;
    public static final int SCAN_DTMB_PARA_FAIL  = -1;
    /*dtmb scan type*/
    public static final int DTMB_SCAN_TYPE_UNKNOWN 			= 0;
    public static final int DTMB_SCAN_TYPE_FULL_MODE 		= 1;
    public static final int DTMB_SCAN_TYPE_QUICK_MODE 		= 2;
    public static final int DTMB_SCAN_TYPE_ADD_ON_MODE 		= 3;
    public static final int DTMB_SCAN_TYPE_SINGLE_RF_CHANNEL = 4;
    public static final int DTMB_SCAN_TYPE_RANGE_RF_CHANNEL 	= 5;   
    public static final int DTMB_SCAN_TYPE_MANUAL_FREQ 		= 6;


    /*DTMB SCAN CONFIG*/

    /*
        DTMB_CONFIG_IGNORE_ANALOG_CH_ON_SORTING              To ignore the analog channels or not while sorting the DTMB channels.
                                                                                                        Note that to set this flag might cause the channel number collision between
                                                                                                        the digital and analog channels
        DTMB_CONFIG_SUPPORT_MHEG5_SERVICES                         Install the MHEG-5 services as visible ones 
        DTMB_CONFIG_START_CH_NUM_FOR_NON_LCN_CH              To customize the the start of the channel numbers to be assigned to the non-LCN 
                                                                                                        channels while LCN need be applied. The default value is 1000.
        DTMB_CONFIG_ALWAYS_APPLY_LCN                                     To always apply the LCN. By default, the engine would allocate the services to 
                                                                                                        channel numbers starting from 1 if all of the services found at this time of scan
                                                                                                        are not attached to a LCN; and henceforth, all the LCN found is ignored. With 
                                                                                                        this flag set, the engine shall always allocate the non-LCN services to the 
                                                                                                        given (or default) start channel numbers for non-LCN services. For example, at
                                                                                                        a country where there is no LCN broadcasted, all the digital services are allocated
                                                                                                        to 800, 801, 802 ...; there will be no services at 1, 2, 3 ...  
        DTMB_CONFIG_UPDATE_TO_TEMP_SVL                                 To store the service records to the given temporary SVL
        DTMB_CONFIG_KEEP_DUPLICATE_CHANNELS                        Do not remove duplicate channels
        DTMB_CONFIG_SDT_NIT_TIMEOUT                                       To customize the timeout value of loading SDT and NIT
        DTMB_CONFIG_SUPPORT_MHP_SERVICES                             Install the MHP services as visible ones
        DTMB_CONFIG_IGNORE_LCN_OF_UNMATCHED_COUNTRY       To ignore the LCN that is of an original network ID that does not belong to the given country 
        DTMB_CONFIG_IGNORE_LCN_OF_FINLAND_RADIO                 To ignore the LCN of Finland radio services
        DTMB_CONFIG_RESERVE_CH_NUM_BEFORE_NON_LCN_CH     To reserve customizied number of channel numbers before allocating for the non-LCN channels
        DTMB_CONFIG_NOT_SUPPORT_HDTV                                    Do not support HDTV (H.264 & MPEG-2)
        DTMB_CONFIG_SIMPLE_SORT_FOR_NON_LCN_CH                  Sort the non-LCN channels by the simple rule - first by country and then service type;
                                                                                                        Otherwise, sort them by (1) country, (2) service type, (3) LCN, (4) service name, (5) service id 
        DTMB_CONFIG_IGNORE_LCN_OF_SWEDEN_RADIO                 To ignore the LCN of Sweden radio services
        DTMB_CONFIG_4_DIGITS_FOR_NORDIG_COUNTRIES             To extend channel number to 9999 for nordig countries
        DTMB_CONFIG_SORTING_BY_FREQ                                       To sort non-lcn channels by frequency order (low --> high)
        DTMB_CONFIG_NOT_INSTALL_DATA_SERVICE                       To disable data service in scan process 
        DTMB_CONFIG_HIER_MODE_DISABLE                                    To disable hierarchical mode demodulation
        DTMB_CONFIG_DISABLE_FORCE_APPLY_HD_SIMULCAST_LCN     To disable force apply HD simulcast LCN in France 
        DTMB_CONFIG_APPLY_SMALLER_FOR_SINGLE_HD_SIMULCAST_LCN  To apply smaller LCN when only found one HD simulcast channel in France
        DTMB_CONFIG_MIN_CH_NUM_FOR_NON_LCN_COUNTRY         To config minimun channel number for non lcn country
        DTMB_CONFIG_NON_STANDARD_BITSTREAM                         To support scan the non-standard DTMBT bitstream
        DTMB_CONFIG_TV_RADIO_SEPARATE                                   To support TV type and Raido type service in the its channel list in Nordig country, 
                                                                                                        and the SB_DTMB_CONFIG_IGNORE_ANALOG_CH_ON_SORTING must be defined before used this config
        DTMB_CONFIG_MUTIL_LANG_CH_NAME                                 To support multi language channel name for DTMB bit stream 
    */
    public static final int DTMB_CONFIG_IGNORE_ANALOG_CH_ON_SORTING              = (1 << 0);
    public static final int DTMB_CONFIG_SUPPORT_MHEG5_SERVICES                   = (1 << 1); 
    public static final int DTMB_CONFIG_START_CH_NUM_FOR_NON_LCN_CH              = (1 << 2);
    public static final int DTMB_CONFIG_ALWAYS_APPLY_LCN                         = (1 << 3);
    public static final int DTMB_CONFIG_UPDATE_TO_TEMP_SVL                       = (1 << 4);
    public static final int DTMB_CONFIG_KEEP_DUPLICATE_CHANNELS                  = (1 << 5);
    public static final int DTMB_CONFIG_SDT_NIT_TIMEOUT                          = (1 << 6);
    public static final int DTMB_CONFIG_SUPPORT_MHP_SERVICES                     = (1 << 7);
    public static final int DTMB_CONFIG_IGNORE_LCN_OF_UNMATCHED_COUNTRY          = (1 << 8);
    public static final int DTMB_CONFIG_IGNORE_LCN_OF_FINLAND_RADIO              = (1 << 9);
    public static final int DTMB_CONFIG_RESERVE_CH_NUM_BEFORE_NON_LCN_CH         = (1 << 10);
    public static final int DTMB_CONFIG_NOT_SUPPORT_HDTV                         = (1 << 11);
    public static final int DTMB_CONFIG_SIMPLE_SORT_FOR_NON_LCN_CH               = (1 << 12);
    public static final int DTMB_CONFIG_IGNORE_LCN_OF_SWEDEN_RADIO               = (1 << 13);
    public static final int DTMB_CONFIG_4_DIGITS_FOR_NORDIG_COUNTRIES            = (1 << 14);
    public static final int DTMB_CONFIG_SORTING_BY_FREQ                          = (1 << 15);
    public static final int DTMB_CONFIG_NOT_INSTALL_DATA_SERVICE                 = (1 << 16);
    public static final int DTMB_CONFIG_HIER_MODE_DISABLE                        = (1 << 17);
    public static final int DTMB_CONFIG_DISABLE_FORCE_APPLY_HD_SIMULCAST_LCN     = (1 << 18);
    public static final int DTMB_CONFIG_APPLY_SMALLER_FOR_SINGLE_HD_SIMULCAST_LCN= (1 << 19);
    public static final int DTMB_CONFIG_MIN_CH_NUM_FOR_NON_LCN_COUNTRY           = (1 << 20);
    public static final int DTMB_CONFIG_NON_STANDARD_BITSTREAM                   = (1 << 21);
    public static final int DTMB_CONFIG_TV_RADIO_SEPARATE                        = (1 << 22);
    public static final int DTMB_CONFIG_MUTIL_LANG_CH_NAME                       = (1 << 23);


    private String  scanCountryCode;
    private int scanType;
    private int scanConfig;
    private int scanStartIndex;
    private int scanEndIndex;
    private int scanFrequency;
    private int scanSvlId;

    public String getScanCountryCode()
    {
        return scanCountryCode;
    }

    public int getScanType()
    {
        return scanType;
    }

    public int getScanConfig()
    {
        return scanConfig;
    }

    public int getScanStartIndex()
    {
        return scanStartIndex;
    }

    public int getScanEndIndex()
    {
        return scanEndIndex;
    }

    public int getScanFrequency()
    {
        return scanFrequency;
    }

    public int getScanSvlId()
    {
        return scanSvlId;
    }

    public ScanParaDtmb(String scanCountryCode, int scanType, int scanConfig, int scanStartIndex, int scanEndIndex, int scanFrequency)
    {
        this.scanCountryCode    = scanCountryCode;
        this.scanType           = scanType;
        this.scanConfig         = scanConfig;
        this.scanStartIndex     = scanStartIndex;
        this.scanEndIndex       = scanEndIndex;
        this.scanFrequency      = scanFrequency;
        this.scanSvlId          = 0; //ChannelCommon.getSvlIdByName(ChannelCommon.DB_DTMB);
    }

	public ScanParaDtmb clone()
    {
    	ScanParaDtmb clonePara 	= new ScanParaDtmb("CHN", 0, 0, 0, 0, 0);
        clonePara.scanCountryCode  = this.scanCountryCode;
        clonePara.scanType         = this.scanType;
        clonePara.scanConfig       = this.scanConfig;
        clonePara.scanStartIndex   = this.scanStartIndex;
        clonePara.scanEndIndex     = this.scanEndIndex;
        clonePara.scanFrequency    = this.scanFrequency;
        clonePara.scanSvlId        = this.scanSvlId;
        return clonePara;
    }
	
    public int clone(ScanParaDtmb originalPara, ScanParaDtmb clonePara)
    {
        clonePara.scanCountryCode  = originalPara.scanCountryCode;
        clonePara.scanType         = originalPara.scanType;
        clonePara.scanConfig       = originalPara.scanConfig;
        clonePara.scanStartIndex   = originalPara.scanStartIndex;
        clonePara.scanEndIndex     = originalPara.scanEndIndex;
        clonePara.scanFrequency    = originalPara.scanFrequency;
        clonePara.scanSvlId        = originalPara.scanSvlId;
        return SCAN_DTMB_PARA_OK;
    }


    public String toString()
    {
        return "ScanParaDtmb [scanCountryCode=" + scanCountryCode + ", scanType=" + scanType + 
                ", scanConfig=" + scanConfig + ", scanStartIndex=" + scanStartIndex + 
                ", scanEndIndex=" + scanEndIndex + ", scanFrequency=" + scanFrequency +
                ", scanSvlId" + scanSvlId + "]";
    }


    public static final Creator<ScanParaDtmb> CREATOR = new Parcelable.Creator<ScanParaDtmb>()
    {
        public ScanParaDtmb createFromParcel(Parcel source)
        {
            return new ScanParaDtmb(source);
        }

        public ScanParaDtmb[] newArray(int size)
        {
            return new ScanParaDtmb[size];
        }
    };

    private ScanParaDtmb(Parcel source)
    {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags)
    {
        super.writeToParcel(out, flags);
        out.writeString(scanCountryCode);
        out.writeInt(scanType);
        out.writeInt(scanConfig);
        out.writeInt(scanStartIndex);
        out.writeInt(scanEndIndex);
        out.writeInt(scanFrequency);
        out.writeInt(scanSvlId);
    }

    public void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);
        scanCountryCode     = in.readString();
        scanType            = in.readInt();
        scanConfig          = in.readInt();
        scanStartIndex      = in.readInt();
        scanEndIndex        = in.readInt();
        scanFrequency       = in.readInt();
        scanSvlId           = in.readInt();
    }
}
