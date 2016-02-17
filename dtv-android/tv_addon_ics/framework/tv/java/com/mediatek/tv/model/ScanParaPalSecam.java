package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.service.BroadcastService;

public class ScanParaPalSecam extends ScanParams implements Parcelable {
    protected static final int SB_PAL_SECAM_CONFIG_PLAY_VIDEO_ONLY = 1 << 0;
    protected static final int SB_PAL_SECAM_CONFIG_RANGE_SCAN_NO_WRAP_AROUND = 1 << 1;
    protected static final int SB_PAL_SECAM_CONFIG_START_CH_NUM = 1 << 2;
    protected static final int SB_PAL_SECAM_CONFIG_SCAN_REPLACE_EXISTING_CHANNEL = 1 << 3;
    protected static final int SB_PAL_SECAM_CONFIG_SCAN_WITH_DESIGNATED_TV_SYS = 1 << 4;
    protected static final int SB_PAL_SECAM_CONFIG_SCAN_WITH_TV_BYPASS = 1 << 5;
    protected static final int SB_PAL_SECAM_CONFIG_SCAN_WITH_MONITOR_BYPASS = 1 << 6;
    protected static final int SB_PAL_SECAM_CONFIG_SCAN_WITHOUT_DSP_DETECT_TV_SYS = 1 << 7;
    protected static final int SB_PAL_SECAM_CONFIG_SCAN_ALL_CH_IN_RANGE_MODE = 1 << 8;
    protected static final int SB_PAL_SECAM_CONFIG_SCAN_WITH_DESIGNATED_COLOR_SYS = 1 << 9;
    protected static final int SB_PAL_SECAM_CONFIG_CUSTOM_SVL_CONFIG = 1 << 10;
    protected static final int SB_PAL_SECAM_CONFIG_UPDATE_TO_TEMP_SVL = 1 << 11;
    protected static final int SB_PAL_SECAM_CONFIG_USER_OPERATION = 1 << 12;
    protected static final int SB_PAL_SECAM_CONFIG_ACTUAL_COLOR_SYS = 1 << 13;

    public static final int SB_PAL_SECAM_SCAN_TYPE_UNKNOWN = 0;
    public static final int SB_PAL_SECAM_SCAN_TYPE_FULL_MODE = SCAN_TYPE_FULL;
    public static final int SB_PAL_SECAM_SCAN_TYPE_RANGE_MODE = SCAN_TYPE_FREQUENCY;
    public static final int SB_PAL_SECAM_SCAN_TYPE_UPDATE_MODE = SCAN_TYPE_CHANNEL;

    public static final int SCAN_PAL_SECAM_PARA_OK = 0;
    public static final int SCAN_PAL_SECAM_PARA_FAIL = -1;

    protected int designatedCheckTvSystem; /* TV system in ChannelInfo */
    protected int designatedCheckAudioSystem; /* add audio system in ChannelInfo */
    protected int designatedCheckColorSystem; /* color system in ChannelInfo */
    protected int isNegativeDirection; /* scan direct */
    protected int scanCfg; /* SB_PAL_SECAM_CONFIG */
    protected BroadcastService tvInput;
    protected BroadcastService tvBypass;
    protected BroadcastService monitorBypass;
    protected int customCfgTvSystem; /* TV system in ChannelInfo */
    protected int customCfgAudioSystem; /* add audio system in ChannelInfo */
    protected int svlId; /* Service List ID */
    protected int firstChannelNumber; /* the first channel number */

    protected int getDesignatedCheckTvSystem() {
        return designatedCheckTvSystem;
    }

    protected int getDesignatedCheckAudioSystem() {
        return designatedCheckAudioSystem;
    }

    protected int getDesignatedCheckColorSystem() {
        return designatedCheckColorSystem;
    }

    protected int getIsNegativeDirection() {
        return isNegativeDirection;
    }

    protected int getScanCfg() {
        return scanCfg;
    }

    protected BroadcastService getTvInput() {
        return tvInput;
    }

    protected BroadcastService getTvBypass() {
        return tvBypass;
    }

    protected BroadcastService getMonitorBypass() {
        return monitorBypass;
    }

    protected int getCustomCfgTvSystem() {
        return customCfgTvSystem;
    }

    protected int getCustomCfgAudioSystem() {
        return customCfgAudioSystem;
    }

    protected int getSvlId() {
        return svlId;
    }

    protected int getFirstChannelNumber() {
        return firstChannelNumber;
    }

    /* change "private" to "protected" for ScanParams class property */
    public ScanParaPalSecam() {
        super.mScanType = SB_PAL_SECAM_SCAN_TYPE_FULL_MODE;
        designatedCheckTvSystem = ChannelCommon.TV_SYS_UNKNOWN;
        designatedCheckAudioSystem = ChannelCommon.AUDIO_SYS_UNKNOWN;
        designatedCheckColorSystem = ChannelCommon.COLOR_SYS_UNKNOWN;
        isNegativeDirection = 0;
        scanCfg = SB_PAL_SECAM_CONFIG_PLAY_VIDEO_ONLY;
        tvInput = null;
        tvBypass = null;
        monitorBypass = null;
        customCfgTvSystem = ChannelCommon.TV_SYS_AUTO;
        customCfgAudioSystem = ChannelCommon.AUDIO_SYS_UNKNOWN;
        svlId = ChannelCommon.getSvlIdByName(ChannelCommon.DB_ANALOG);
        firstChannelNumber = 1;
        this.wrapAround(0);
        this.doUserOperationAfterChannelFound(false);
        this.needActualColorSystem(false);
    }

    public ScanParaPalSecam clone() {
		ScanParaPalSecam clonePara = new ScanParaPalSecam();
        clonePara.designatedCheckTvSystem 	 = this.designatedCheckTvSystem;
        clonePara.designatedCheckAudioSystem = this.designatedCheckAudioSystem;
        clonePara.designatedCheckColorSystem = this.designatedCheckColorSystem;
        clonePara.isNegativeDirection 		 = this.isNegativeDirection;
        clonePara.scanCfg 					 = this.scanCfg;
        clonePara.tvInput 					 = this.tvInput;
        clonePara.tvBypass 					 = this.tvBypass;
        clonePara.monitorBypass 			 = this.monitorBypass;
        clonePara.customCfgTvSystem 		 = this.customCfgTvSystem;
        clonePara.customCfgAudioSystem 	 	 = this.customCfgAudioSystem;
        clonePara.svlId 				     = this.svlId;
        clonePara.firstChannelNumber 		 = this.firstChannelNumber;
        return clonePara;
    }

    public int clone(ScanParaPalSecam originalPara, ScanParaPalSecam clonePara) {
        clonePara.designatedCheckTvSystem 	 = originalPara.designatedCheckTvSystem;
        clonePara.designatedCheckAudioSystem = originalPara.designatedCheckAudioSystem;
        clonePara.designatedCheckColorSystem = originalPara.designatedCheckColorSystem;
        clonePara.isNegativeDirection 		 = originalPara.isNegativeDirection;
        clonePara.scanCfg 					 = originalPara.scanCfg;
        clonePara.tvInput 					 = originalPara.tvInput;
        clonePara.tvBypass 					 = originalPara.tvBypass;
        clonePara.monitorBypass 			 = originalPara.monitorBypass;
        clonePara.customCfgTvSystem 		 = originalPara.customCfgTvSystem;
        clonePara.customCfgAudioSystem 		 = originalPara.customCfgAudioSystem;
        clonePara.svlId 					 = originalPara.svlId;
        clonePara.firstChannelNumber 		 = originalPara.firstChannelNumber;
        return SCAN_PAL_SECAM_PARA_OK;
    }

    public int changeScanType(int scanType, int scanFrom, int scanTo) {
        super.mScanType = scanType;
        super.mScanFreqFrom = scanFrom;
        super.mScanFreqTo = scanTo;
        
        if (ScanParaPalSecam.SB_PAL_SECAM_SCAN_TYPE_RANGE_MODE == scanType) {
            if (scanFrom <= scanTo) {
                this.isNegativeDirection = 0;
            } else {
                this.isNegativeDirection = 1;
            }
        } else {
            super.mScanFreqFrom = super.mScanFreqTo = 0;
        }
        return SCAN_PAL_SECAM_PARA_OK;
    }

    public int makeScanWithVideo(int enable) {
        if (1 == enable) {
            this.scanCfg |= SB_PAL_SECAM_CONFIG_PLAY_VIDEO_ONLY;
        } else {
            this.scanCfg &= ~SB_PAL_SECAM_CONFIG_PLAY_VIDEO_ONLY;
        }
        return SCAN_PAL_SECAM_PARA_OK;
    }

    public int wrapAround(int enable) {
        if (1 == enable) {
            this.scanCfg &= ~SB_PAL_SECAM_CONFIG_RANGE_SCAN_NO_WRAP_AROUND;
        } else {
            this.scanCfg |= SB_PAL_SECAM_CONFIG_RANGE_SCAN_NO_WRAP_AROUND;
        }
        return SCAN_PAL_SECAM_PARA_OK;
    }

    public int setFirstChannelNumber(int enable, int firstChannelNumber) {
        if (1 == enable) {
            this.scanCfg |= SB_PAL_SECAM_CONFIG_START_CH_NUM;
            this.firstChannelNumber = firstChannelNumber;
        } else {
            this.scanCfg &= ~SB_PAL_SECAM_CONFIG_START_CH_NUM;
        }
        return SCAN_PAL_SECAM_PARA_OK;
    }

    public int relpaceExsitingChannel(int enable) {
        if (1 == enable) {
            this.scanCfg |= SB_PAL_SECAM_CONFIG_SCAN_REPLACE_EXISTING_CHANNEL;
        } else {
            this.scanCfg &= ~SB_PAL_SECAM_CONFIG_SCAN_REPLACE_EXISTING_CHANNEL;
        }
        return SCAN_PAL_SECAM_PARA_OK;
    }

    public int designateTvSystemAudioSystem(int enable, int tvSystem, int audioSystem) {
        if (1 == enable) {
            this.scanCfg |= SB_PAL_SECAM_CONFIG_SCAN_WITH_DESIGNATED_TV_SYS;
            this.scanCfg |= SB_PAL_SECAM_CONFIG_SCAN_WITHOUT_DSP_DETECT_TV_SYS;
            
            this.designatedCheckTvSystem = tvSystem;
            this.designatedCheckAudioSystem = audioSystem;
        } else {
            this.scanCfg &= ~SB_PAL_SECAM_CONFIG_SCAN_WITH_DESIGNATED_TV_SYS;
            this.scanCfg &= ~SB_PAL_SECAM_CONFIG_SCAN_WITHOUT_DSP_DETECT_TV_SYS;
        }
        return SCAN_PAL_SECAM_PARA_OK;
    }

    // public int tvBypass(int enable, BroadcastService tvBypass) {
    // return SCAN_PAL_SECAM_PARA_OK;
    // }
    //
    // public int monitorBypass(int enable, BroadcastService monitorBypass) {
    // return SCAN_PAL_SECAM_PARA_OK;
    // }

    public int searchAllChannelInRange(int enable) {
        if (1 == enable) {
            this.scanCfg |= SB_PAL_SECAM_CONFIG_SCAN_ALL_CH_IN_RANGE_MODE;
        } else {
            this.scanCfg &= ~SB_PAL_SECAM_CONFIG_SCAN_ALL_CH_IN_RANGE_MODE;
        }
        return SCAN_PAL_SECAM_PARA_OK;
    }

    public int designateColorSystem(int enable, int colorSystem) {
        if (1 == enable) {
            this.scanCfg |= SB_PAL_SECAM_CONFIG_SCAN_WITH_DESIGNATED_COLOR_SYS;
            this.designatedCheckColorSystem = colorSystem;
        } else {
            this.scanCfg &= ~SB_PAL_SECAM_CONFIG_SCAN_WITH_DESIGNATED_COLOR_SYS;
        }
        return SCAN_PAL_SECAM_PARA_OK;
    }

    public int setCustomerCfgTvSystemAudioSystem(int enable, int tvSystem, int audioSystem) {
        if (1 == enable) {
            this.scanCfg |= SB_PAL_SECAM_CONFIG_CUSTOM_SVL_CONFIG;
            this.customCfgTvSystem = tvSystem;
            this.customCfgAudioSystem = audioSystem;
        } else {
            this.scanCfg &= ~SB_PAL_SECAM_CONFIG_CUSTOM_SVL_CONFIG;
        }

        return SCAN_PAL_SECAM_PARA_OK;
    }

    public int designateName(int enable, String name) {
        this.svlId = -1;

        if (1 != enable) {
            name = ChannelCommon.DB_ANALOG;
        }

        if (name.equalsIgnoreCase(ChannelCommon.DB_ANALOG_TEMP)) {
            this.scanCfg |= SB_PAL_SECAM_CONFIG_UPDATE_TO_TEMP_SVL;
        }
        else
        {
            this.scanCfg &= ~SB_PAL_SECAM_CONFIG_UPDATE_TO_TEMP_SVL;
        }

        this.svlId = ChannelCommon.getSvlIdByName(name);

        if (-1 == this.svlId) {
            return SCAN_PAL_SECAM_PARA_FAIL;
        }

        return SCAN_PAL_SECAM_PARA_OK;
    }
    
    public int doUserOperationAfterChannelFound( boolean enable ){
        if (enable){
            this.scanCfg |= SB_PAL_SECAM_CONFIG_USER_OPERATION;
        }
        else{
            this.scanCfg &= ~SB_PAL_SECAM_CONFIG_USER_OPERATION;
        }
        return SCAN_PAL_SECAM_PARA_OK;
    }
    
    public int needActualColorSystem( boolean enable ){
        if (enable){
            this.scanCfg |= SB_PAL_SECAM_CONFIG_ACTUAL_COLOR_SYS;
        }
        else{
            this.scanCfg &= ~SB_PAL_SECAM_CONFIG_ACTUAL_COLOR_SYS;
        }
        return SCAN_PAL_SECAM_PARA_OK;
    }

    public String toString() {
        return "ScanParaPalSecam [designatedCheckTvSystem=" + designatedCheckTvSystem + ", designatedCheckAudioSystem="
                + designatedCheckAudioSystem + ", designatedCheckColorSystem=" + designatedCheckColorSystem
                + ", isNegativeDirection=" + isNegativeDirection + ", scanCfg=" + scanCfg + ", tvInput=" + tvInput
                + ", tvBypass=" + tvBypass + ", monitorBypass=" + monitorBypass + ", customCfgTvSystem="
                + customCfgTvSystem + ", customCfgAudioSystem=" + customCfgAudioSystem + ", svlId=" + svlId
                + ", firstChannelNumber=" + firstChannelNumber + ", mScanType=" + mScanType + ", mScanChannelFrom="
                + mScanChannelFrom + ", mScanChannelTo=" + mScanChannelTo + ", mScanFreqFrom=" + mScanFreqFrom
                + ", mScanFreqTo=" + mScanFreqTo + "]";
    }

    public static final Creator<ScanParaPalSecam> CREATOR = new Parcelable.Creator<ScanParaPalSecam>() {
        public ScanParaPalSecam createFromParcel(Parcel source) {
            return new ScanParaPalSecam(source);
        }

        public ScanParaPalSecam[] newArray(int size) {
            return new ScanParaPalSecam[size];
        }
    };

    private ScanParaPalSecam(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(designatedCheckTvSystem);
        out.writeInt(designatedCheckAudioSystem);
        out.writeInt(designatedCheckColorSystem);
        out.writeInt(isNegativeDirection);
        out.writeInt(scanCfg);
        out.writeInt(customCfgTvSystem);
        out.writeInt(customCfgAudioSystem);
        out.writeInt(svlId);
        out.writeInt(firstChannelNumber);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        designatedCheckTvSystem = in.readInt();
        designatedCheckAudioSystem = in.readInt();
        designatedCheckColorSystem = in.readInt();
        isNegativeDirection = in.readInt();
        scanCfg = in.readInt();
        customCfgTvSystem = in.readInt();
        customCfgAudioSystem = in.readInt();
        svlId = in.readInt();
        firstChannelNumber = in.readInt();
    }
}
