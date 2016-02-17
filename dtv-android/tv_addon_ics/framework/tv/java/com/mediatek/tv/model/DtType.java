package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DtType implements Parcelable {
	/**
	 * The configuration for DT library
	 */
	public static final int      DT_USE_DST_AT_SPECIFIED_TIME              = 2;    /**<UTC to local: use special daylight saving        */
	public static final int      DT_USE_DST_AT_CURRENT_TIME                = 4;   /**<UTC to local: use current time         			*/
	public static final int      DT_DVB_MATCH_CNTRY_AND_REG_CODE           = 8;    /**<DVB match type:use country code and region code  */
	public static final int      DT_DVB_MATCH_CNTRY_CODE                   = 16;   /**<DVB match type:use country code					*/
	public static final int      DT_DVB_MATCH_ANY_CNTRY_OR_REG_CODE        = 32;   /**<DVB match type:use any country or region code    */
	public static final int      DT_ANALOG_USE_TELETEXT_LOCAL_TIME_OFFSET  = 64;   /**<Analog: use teletext local time offset        	*/
	public static final int      DT_ANALOG_UNSET_CONFIG_SETTING            = 128;  /**<Analog: unset config 							*/
	public static final int      DT_DVB_LTO_CHECK_CHG_TIME                 = 256;  /**<DT_DVB_LTO_CHECK_CHG_TIME                 		*/
	public static final int      DT_DVB_LTO_RAW_TZ_WHEN_DST_CTRL_OFF       = 512;  /**<*DT_DVB_LTO_RAW_TZ_WHEN_DST_CTRL_OFF				*/
	
	/**
	 * Enum for specifying the source to synchronize the current time
	 */
	public static final int DT_SYNC_SRC_NONE 			= 0;
	public static final int DT_SYNC_WITH_DVB_TDT 		= 1;
	public static final int DT_SYNC_WITH_DVB_TOT		= 2;
	public static final int DT_SYNC_WITH_ATSC_STT		= 3;
	public static final int DT_SYNC_WITH_SCTE_STT		= 4;          /* For Cable out-of-band source. */
	public static final int DT_SYNC_WITH_DVB_TDT_OR_TOT = 5;
	public static final int DT_SYNC_WITH_DVB_TOT_OR_TDT = 6;
	public static final int DT_SYNC_SRC_ANALOG_NONE		= 7;
	public static final int DT_SYNC_WITH_ANALOG_TELETEXT= 8;
	
	/**
	 * ENUM type describing the characteristics of the synchronization source.  
	 */
	public static final int DT_SRC_TYPE_FORMAT_UNKNOWN 	= 0;
	public static final int DT_SRC_TYPE_MPEG_2_BRDCST	= 1;
	public static final int DT_SRC_TYPE_VBI_ANA_TER		= 2;
	public static final int DT_SRC_TYPE_VBI_ANA_CAB		= 3;
	public static final int DT_SRC_TYPE_VBI_ANA_SAT		= 4;
	public static final int DT_SRC_TYPE_CONN_HANDLE		= 5;
	
	/**
	 * DT_CHECK_TIME_CONFIG
	 */
	public static final int  DT_USE_DEFAULT_CONFIG 					= 0;
	public static final int  DT_SET_CONSECUTIVE_VAL_TO_CHK 			= 1;
	public static final int  DT_SET_TIME_WINDOW_ADJUSTMENT			= 2;
	public static final int  DT_REJECT_BRDCST_TIME_BEFORE_THIS_DATE	= 3;


	/**
	 *  system clock condition.  notify condition
	 */
	public static final int	DT_NOT_RUNNING           = 1;    /**<Condition:Not running        		*/
	public static final int	DT_FREE_RUNNING          = 2;    /**<Condition:Free running        		*/
	public static final int	DT_SYNC_RUNNING          = 3;    /**<Condition:sync running        		*/
	public static final int	DT_SYNC_DISCONT          = 4;    /**<Condition:sync discontinue        	*/
	public static final int	DT_LTO_UPDATED           = 5;    /**<Condition:Local timezone updated   */
	public static final int	DT_NO_SYNC_SRC           = 6;    /**<Condition:No sync source        	*/
	public static final int	DT_IS_RUNNING            = 7;    /**<Condition:Is running        		*/
	public static final int	DT_DAY_LGT_SAV_CHANGED   = 8;    /**<Condition:Daylight saving changed  */
	public static final int	DT_TZ_OFFSET_CHANGED     = 9;    /**<Condition:Timezone offset changed  */

	public DtType() {
		super();
	}

	//@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

}
