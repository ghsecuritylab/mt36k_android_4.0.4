package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.service.ITVRemoteService;

public class ScanParaDvbc extends ScanParams implements Parcelable {
	/*dvbc scan mode*/
	public static final int SB_DVBC_SCAN_TYPE_UNKNOWN     = 0;
	public static final int SB_DVBC_SCAN_TYPE_FULL_MODE   = 1;
    public static final int SB_DVBC_SCAN_TYPE_MANUAL_FREQ = 2;
    public static final int SB_DVBC_SCAN_TYPE_UPDATE      = 3;

    public static final int SB_DVBC_SCAN_FREQ_RANGE_START = 47000000;
    public static final int SB_DVBC_SCAN_FREQ_RANGE_END = 858000000;
    
	/*dvbc scan config*/
    public static final int SB_DVBC_CONFIG_IGNORE_ANALOG_CH_ON_SORTING          = 1 << 0;
    public static final int SB_DVBC_CONFIG_SUPPORT_MHEG5_SERVICES               = 1 << 1;
    public static final int SB_DVBC_CONFIG_START_CH_NUM_FOR_NON_LCN_CH          = 1 << 2;
    public static final int SB_DVBC_CONFIG_ALWAYS_APPLY_LCN                     = 1 << 3;
    public static final int SB_DVBC_CONFIG_UPDATE_TO_TEMP_SVL                   = 1 << 4;
    public static final int SB_DVBC_CONFIG_KEEP_DUPLICATE_CHANNELS              = 1 << 5;
    public static final int SB_DVBC_CONFIG_SDT_NIT_TIMEOUT                      = 1 << 6;
    public static final int SB_DVBC_CONFIG_SUPPORT_MHP_SERVICES                 = 1 << 7;
    public static final int SB_DVBC_CONFIG_RESERVE_CH_NUM_BEFORE_NON_LCN_CH     = 1 << 8;
    public static final int SB_DVBC_CONFIG_NOT_SUPPORT_HDTV                     = 1 << 9;
    public static final int SB_DVBC_CONFIG_SIMPLE_SORT_FOR_NON_LCN_CH           = 1 << 10;
    public static final int SB_DVBC_CONFIG_EX_QUICK_BUILD_SVL_BY_SDT            = 1 << 11;
    public static final int SB_DVBC_CONFIG_PRIOR_RF_SCAN_ENABLE                 = 1 << 12;
    public static final int SB_DVBC_CONFIG_SCAN_WITHOUT_SCAN_MAP                = 1 << 13;
	public static final int SB_DVBC_CONFIG_TV_RADIO_SEPARATE                    = 1 << 14;
    public static final int SB_DVBC_CONFIG_CUST_1                               = 1 << 15;
	public static final int SB_DVBC_CONFIG_QAM_SR_AUTO_DETECT                   = 1 << 16;
    public static final int SB_DVBC_CONFIG_INSTALL_FREE_SERVICES_ONLY           = 1 << 17;
	public static final int SB_DVBC_CONFIG_TRUST_NIT_IN_EX_QUICK_SCAN           = 1 << 18;
    public static final int SB_DVBC_CONFIG_QUICK_SCAN_IGNORE_SVC_OUT_OF_NETWORK = 1 << 19;
	
	/*dvbc scan operator name*/
	public static final int SB_DVBC_OPERATOR_NAME_OTHERS        = 0;
	public static final int SB_DVBC_OPERATOR_NAME_UPC		    = 1;	
	public static final int SB_DVBC_OPERATOR_NAME_COMHEM	    = 2;	  
	public static final int SB_DVBC_OPERATOR_NAME_CANAL_DIGITAL = 3;  
	public static final int SB_DVBC_OPERATOR_NAME_TELE2		    = 4; 
	public static final int SB_DVBC_OPERATOR_NAME_STOFA		    = 5;
	public static final int SB_DVBC_OPERATOR_NAME_YOUSEE		= 6;
	public static final int SB_DVBC_OPERATOR_NAME_ZIGGO		    = 7;
	public static final int SB_DVBC_OPERATOR_NAME_UNITYMEDIA    = 8;
	public static final int SB_DVBC_OPERATOR_NAME_NUMERICABLE   = 9;
	/*operator name by area*/
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_BASE    = 1000;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_GUANGZHOU_SHENGWANG	 = 1001;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_GUANGZHOU_SHIWANG    = 1002;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_HUNAN_GUANGDIAN      = 1003;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_CHANGSHA_GUOAN       = 1004;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_WUHAN_SHENGWANG      = 1005;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_WUHAN_SHIWANG        = 1006;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_XIAN_GUANGDIAN       = 1007;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_BEIJING_GEHUAYOUXIAN = 1008;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_QINGDAO_GUANGDIAN    = 1009;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_HUBEI_HUANGSHI       = 1010;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_DALIAN_GUANGDIAN     = 1011;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_SHAOXING_GUANGDIAN   = 1012;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_NEIMENGGU_YOUXIAN    = 1013;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_EZHOU_YOUXIAN        = 1014;
	public static final int SB_DVBC_OPERATOR_NAME_CHINA_SANMING_YOUXIAN      = 1016;

	/*dvbc nit search mode*/
    public static final int SB_DVBC_NIT_SEARCH_MODE_OFF      = 0;
    public static final int SB_DVBC_NIT_SEARCH_MODE_QUICK    = 1;
    public static final int SB_DVBC_NIT_SEARCH_MODE_EX_QUICK = 2;
    public static final int SB_DVBC_NIT_SEARCH_MODE_NUM      = 3; 

	/*dvbc scan valid mask*/
	public static final int SB_DVBC_SCAN_INFO_NW_ID_VALID      = 0x1;
	public static final int SB_DVBC_SCAN_INFO_BW_VALID         = 0x2;
	public static final int SB_DVBC_SCAN_INFO_MOD_VALID        = 0x4;
	public static final int SB_DVBC_SCAN_INFO_SYM_VALID        = 0x8;
	public static final int SB_DVBC_SCAN_INFO_START_FREQ_VALID = 0x10;
	public static final int SB_DVBC_SCAN_INFO_END_FREQ_VALID   = 0x20;

	/*dvbc scan eMod*/
	public static final int MOD_UNKNOWN = 0;
	public static final int MOD_QAM_16  = 1; 
	public static final int MOD_QAM_32  = 2;
    public static final int MOD_QAM_64  = 3;
    public static final int MOD_QAM_128 = 4;
    public static final int MOD_QAM_256 = 5;

	/*dvbc scan BW*/
    public static final int BW_UNKNOWN = 0;
    public static final int BW_6_MHz   = 1;
    public static final int BW_7_MHz   = 2;
    public static final int BW_8_MHz   = 3;
    
    public static final int SCAN_DVBC_PARA_OK   = 0;
    public static final int SCAN_DVBC_PARA_FAIL = -1;

    protected String countryCode;
	protected int operatorName;
	protected int eBw;	
	protected int searchMode;
	protected int scanType;
	protected int validMask;
	protected int eMod;
	protected int symRate;
	protected int startFreq;
	protected int endFreq;
	protected int netWorkId;
    protected int scanCfg;
    protected int svlId;


    protected int getScanCfg() {
        return scanCfg;
    }

    protected int getSymRate() {
        return symRate;
    }

    protected int getSvlId() {
        return svlId;
    }

	protected String getCountryCode() {
        return countryCode;
    }
	
	protected int getOperatorName() {
        return operatorName;
    }
	
	protected int getEBw() {
        return eBw;
    }

	protected int getSearchMode() {
        return searchMode;
    }
	
	protected int getScanType() {
        return scanType;
    }
		
	protected int getValidMask() {
        return validMask;
    }	
	
	protected int getEMod() {
        return eMod;
    }

	protected int getNetWorkID() {
        return netWorkId;
    }

	protected int getStartFreq() {
        return startFreq;
    }

	protected int getEndFreq() {
        return endFreq;
    }
	
	public static int getDefaultSymRate(String countryCode) throws RemoteException {
		int symRate = 0;
        ITVRemoteService service = TVManager.getRemoteTvService();
		if (null != service) {
			symRate = service.getDefaultSymRate_proxy(countryCode);
		}
        return symRate;
    }

	public static int getDefaultFreq(String countryCode) throws RemoteException {
		int freq = 0;
        ITVRemoteService service = TVManager.getRemoteTvService();
		if (null != service) {
        	freq = service.getDefaultFrequency_proxy(countryCode);
		}
        return freq;
    }

	public static int getDefaultEMod(String countryCode) throws RemoteException {
		int eMod = 0;
        ITVRemoteService service = TVManager.getRemoteTvService();
		if (null != service) {
        	eMod = service.getDefaultEMod_proxy(countryCode);
		}
        return eMod;
    }

	public static int getDefaultNetWorkId(String countryCode) throws RemoteException {
		int netWorkId = 0;
        ITVRemoteService service = TVManager.getRemoteTvService();
		if (null != service) {
        	netWorkId = service.getDefaultNwID_proxy(countryCode);
		}
        return netWorkId;
    }
	
    /* change "private" to "protected" for ScanParams class property */
    public ScanParaDvbc(String countryCode) {
    	this.countryCode = countryCode;
        super.mScanType  = SB_DVBC_SCAN_TYPE_FULL_MODE;      
        this.operatorName= SB_DVBC_OPERATOR_NAME_OTHERS; 
        this.endFreq     = 0;
        this.netWorkId   = 0;
        this.startFreq   = 0;
        this.searchMode  = SB_DVBC_NIT_SEARCH_MODE_OFF;
        this.symRate     = 0;
        this.validMask   = 0;
        this.eMod        = MOD_UNKNOWN;/*the mod is ignore.*/ 
        this.eBw         = BW_UNKNOWN;/*the bandwidth is ignore.*/ 
        this.scanCfg     = 0;
        this.svlId       = ChannelCommon.getSvlIdByName(ChannelCommon.DB_ANALOG);
    }
    
    public ScanParaDvbc(String countryCode, int operatorName, int searchMode, int scanType, int scanCfg, int startFreq, int endFreq) {
    	this.countryCode = countryCode;
        super.mScanType  = scanType;
        this.operatorName= operatorName; 
        this.endFreq     = endFreq;
        this.netWorkId   = 0;
        this.startFreq   = startFreq;
        this.searchMode  = searchMode;
        this.symRate     = 0;
        this.validMask   = SB_DVBC_SCAN_INFO_START_FREQ_VALID|SB_DVBC_SCAN_INFO_END_FREQ_VALID;
        this.eMod        = MOD_UNKNOWN;/*the mod is ignore.*/ 
        this.eBw         = BW_UNKNOWN;/*the bandwidth is ignore.*/ 
        this.scanCfg     = scanCfg;
        this.svlId       = ChannelCommon.getSvlIdByName(ChannelCommon.DB_ANALOG);
    }
    
    public ScanParaDvbc(String countryCode, int operatorName, int searchMode, int scanType, int scanCfg,
    		            int validMask, int eMod, int symRate, int startFreq, int endFreq, int netWorkID) {
    	this.countryCode = countryCode;
        super.mScanType  = scanType;
        this.operatorName= operatorName; 
        this.endFreq     = endFreq;
        this.netWorkId   = netWorkID;
        this.startFreq   = startFreq;
        this.searchMode  = searchMode;
        this.symRate     = symRate;
        this.validMask   = validMask;
        this.eMod        = eMod; 
        this.eBw         = BW_UNKNOWN;/*the bandwidth is ignore.*/ 
        this.scanCfg     = scanCfg;
        this.svlId       = ChannelCommon.getSvlIdByName(ChannelCommon.DB_ANALOG);
    }

    public ScanParaDvbc clone() {
		ScanParaDvbc clonePara = new ScanParaDvbc("CHN");
        clonePara.countryCode  = this.countryCode;
        clonePara.eBw          = this.eBw;
        clonePara.eMod         = this.eMod;
        clonePara.endFreq      = this.endFreq;
        clonePara.scanCfg      = this.scanCfg;
        clonePara.netWorkId    = this.netWorkId;
        clonePara.operatorName = this.operatorName;
        clonePara.scanType     = this.scanType;
        clonePara.searchMode   = this.searchMode;
        clonePara.startFreq    = this.startFreq;
        clonePara.svlId        = this.svlId;
        clonePara.symRate      = this.symRate;
        clonePara.validMask    = this.validMask;
        return clonePara;
    }
	
    public int clone(ScanParaDvbc originalPara, ScanParaDvbc clonePara) {
        clonePara.countryCode  = originalPara.countryCode;
        clonePara.eBw          = originalPara.eBw;
        clonePara.eMod         = originalPara.eMod;
        clonePara.endFreq      = originalPara.endFreq;
        clonePara.scanCfg      = originalPara.scanCfg;
        clonePara.netWorkId    = originalPara.netWorkId;
        clonePara.operatorName = originalPara.operatorName;
        clonePara.scanType     = originalPara.scanType;
        clonePara.searchMode   = originalPara.searchMode;
        clonePara.startFreq    = originalPara.startFreq;
        clonePara.svlId        = originalPara.svlId;
        clonePara.symRate      = originalPara.symRate;
        clonePara.validMask    = originalPara.validMask;
        return SCAN_DVBC_PARA_OK;
    }

    public String toString() {
        return "ScanParaDvbc [countryCode=" + countryCode + ", eBw=" + eBw + ", eMod=" + eMod
                + ", endFreq=" + endFreq + ", scanCfg=" + scanCfg + ", netWorkId=" + netWorkId
                + ", operatorName=" + operatorName + ", scanType=" + scanType + ", searchMode="
                + searchMode + ", startFreq=" + startFreq + ", svlId=" + svlId
                + ", symRate=" + symRate + ", validMask=" + validMask + "]";
    }

    public static final Creator<ScanParaDvbc> CREATOR = new Parcelable.Creator<ScanParaDvbc>() {
        public ScanParaDvbc createFromParcel(Parcel source) {
            return new ScanParaDvbc(source);
        }

        public ScanParaDvbc[] newArray(int size) {
            return new ScanParaDvbc[size];
        }
    };

    private ScanParaDvbc(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(countryCode);
        out.writeInt(eBw);
        out.writeInt(eMod);
        out.writeInt(endFreq);
        out.writeInt(scanCfg);
        out.writeInt(netWorkId);
        out.writeInt(operatorName);
        out.writeInt(svlId);
        out.writeInt(scanType);
        out.writeInt(searchMode);
        out.writeInt(startFreq);
        out.writeInt(symRate);
        out.writeInt(validMask);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        countryCode = in.readString();
        eBw         = in.readInt();
        eMod        = in.readInt();
        endFreq     = in.readInt();
        scanCfg     = in.readInt();
        netWorkId   = in.readInt();
        operatorName = in.readInt();
        svlId        = in.readInt();
        scanType     = in.readInt();
        searchMode   = in.readInt();
        startFreq    = in.readInt();
        symRate      = in.readInt();
        validMask    = in.readInt();
    }
}
