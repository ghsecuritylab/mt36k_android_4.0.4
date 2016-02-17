/*
 * Copyright (C) 2010 MediaTek USA, Inc
 *
 */

package com.mediatek.tv.service;

import java.util.List;

import com.mediatek.tv.common.ConfigValue;
import com.mediatek.tv.model.AudioInfo;
import com.mediatek.tv.model.AudioLanguageInfo;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.DvbcProgramType;
import com.mediatek.tv.model.HostControlTune;
import com.mediatek.tv.model.DtDTG;
import com.mediatek.tv.model.EventCommand;
import com.mediatek.tv.model.EventInfo;
import com.mediatek.tv.model.InputRecord;
import com.mediatek.tv.model.InputRegion;
import com.mediatek.tv.model.ScanExchangeFrenquenceRange;
import com.mediatek.tv.model.ScanParams;
import com.mediatek.tv.model.SubtitleInfo;
import com.mediatek.tv.model.VideoResolution;
import com.mediatek.tv.model.SignalLevelInfo;
import com.mediatek.tv.model.ExtraChannelInfo;
import com.mediatek.tv.model.DvbcFreqRange;
import com.mediatek.tv.model.ScanParaDtmb;
import com.mediatek.tv.model.DtmbFreqRange;
import com.mediatek.tv.model.DtmbScanRF;
import com.mediatek.tv.model.MainFrequence;


public class TVNative {
    private static final String TAG = "TVNative";
    static {
        Logger.i(TAG, "Load libcom_mediatek_tv_jni.so start !");
        System.loadLibrary("com_mediatek_tv_jni");
        Logger.i(TAG, "Load libcom_mediatek_tv_jni.so OK !");
    }
    /**
     * Do the open operation of UART Serial Port (It is only used in factory currently)
     * <pre>
     * 
     * @param [in]uartSerialID
     *            indicate the uart serial porting id.
     * @param [in]uartSerialSetting
     *            the integer array contains the serial port data
     *            the length of this array is 4
     *            the data of index 0 is speed setting
     *            the data of index 1 is data length setting
     *            the data of index 2 is parity setting
     *            the data of index 3 is stop bit setting
     * @param [out]handle
     *            the return handle to identify the opened UART Serial Port.
     * @return 0-OK other-fail
     * 
     */
    protected static native int openUARTSerial_native(int uartSerialID, int[] uartSerialSetting, int[] handle);
    
    /**
     * Do the close operation of UART Serial Port (It is only used in factory currently)
     * <pre>
     *      
     * @param [in]handle
     *            the handle to identify the opened UART Serial Port
     * @return 0-OK other-fail
     * 
     */
    protected static native int closeUARTSerial_native(int handle);
    
    /**
     * Get the UART Serial Setting attribute (It is only used in factory currently)
     * <pre>
     *      
     * @param [in]handle
     *            the handle to identify the opened UART Serial Port
     * @param [out]uartSerialSetting
     *            the integer array contains the serial port data
     *            the length of this array is 4
     *            the data of index 0 is speed setting
     *            the data of index 1 is data length setting
     *            the data of index 2 is parity setting
     *            the data of index 3 is stop bit setting
     * @return 0-OK other-fail
     * 
     */
    public static native int getUARTSerialSetting_native(int handle, int[] uartSerialSetting);
    
    /**
     * Get the UART Serial opration mode (It is only used in factory currently)
     * <pre>
     *      
     * @param [in]handle
     *            the handle to identify the opened UART Serial Port
     * @param [out]operationMode
     *            the operation mode of UART Serial
     * @return 0-OK other-fail
     * 
     */
    public static native int getUARTSerialOperationMode_native(int handle, int[] operationMode);
    
    /**
     * Set the UART Serial Setting attribute (It is only used in factory currently)
     * <pre>
     *      
     * @param [in]handle
     *            the handle to identify the opened UART Serial Port
     * @param [in]uartSerialSetting
     *            the integer array contains the serial port data
     *            the length of this array is 4
     *            the data of index 0 is speed setting
     *            the data of index 1 is data length setting
     *            the data of index 2 is parity setting
     *            the data of index 3 is stop bit setting
     * @return 0-OK other-fail
     * 
     */
    public static native int setUARTSerialSetting_native(int handle, int[] uartSerialSetting);
    
    /**
     * Set the UART Serial operation mode (It is only used in factory currently)
     * <pre>
     *      
     * @param [in]handle
     *            the handle to identify the opened UART Serial Port
     * @param [in]operationMode
     *            the operation mode of UART Serial
     * @return 0-OK other-fail
     * 
     */
    public static native int setUARTSerialOperationMode_native(int handle, int operationMode);
    
    /**
     * Set magic string operation of UART Serial Port(It is only used in factory currently)
     * <pre>
     * @param [in]handle
     *            the handle to identify the opened UART Serial Port
     * @param [in]uartSerialMagicSetting
     *            the magic string
     * @return 0-OK other-fail
     */
    public static native int setUARTSerialMagicString_native(int handle, byte[] uartSerialMagicSetting);
    
    /**
     * Output string to UART Serial Port(It is only used in factory currently)
     * <pre>
     * @param [in]handle
     *            the handle to identify the opened UART Serial Port
     * @param [in]uartSerialData
     *            the string to be output
     * @return 0-OK other-fail
     */
    public static native int outputUARTSerial_native(int handle, byte[] uartSerialData);
  /**
      * Deprecated API, related function has be imgraded to inputservice.
      * <pre>
      * @return -1
  */
    protected static native int autoAdjust_native(String autoType);

  /**
     * The API to handle power off music and flush config settings when power off
     * <pre>
     * @return 0-OK other-fail
     */
    protected static native int powerOff_native();

    /**
     * Get the specific config settings info
     * <pre>
     * @param [in]inputSource
     *            input source value to define which input or all input
     * @param [in]configType
     *            the config type string to identify the config settings
     * @param [in]configParamsValue
     *            config parameters value to define array info
     * @param [out]configValue
     *		     the configvalue of this settings to be output.
     * @return 0-OK other-fail
     */
	protected static native int getCfg_native(int inputSource, String configType, ConfigValue configParamsValue,
	        ConfigValue configValue);
    /**
     * Set the specific config settings value
     * <pre>
     * @param [in]inputSource
     *            input source value to define which input or all input
     * @param [in]configType
     *            the config type string to identify the config settings
     * @param [in]configValue
     *		     the configvalue of this settings to be set to configservice.
     * @return 0-OK other-fail
     */
    protected static native int setCfg_native(int inputSource, String configType, ConfigValue configValue);
	
    /**
     * Update the specific config settings value, which makes the config value effect.
     * <pre>
     * @param [in]configType
     *            the config type string to identify the config settings
     * @return 0-OK other-fail
     */
    protected static native int updateCfg_native(String configType);
	
    /**
     * Reset the specified config group value
     * <pre>
     * @param [in]resetType
     *            resetType to identify which group type to be reset
     * @return 0-OK other-fail
     */
    protected static native int resetCfgGroup_native(String resetType);
	
  /**
     * Get the specific config settings Min and Max values
     * <pre>
     * @param [in]configType
     *            the config type string to identify the config settings
     * @param [out]configValue
     *		     the configvalue of this settings to be output
     * @return 0-OK other-fail
     */
    protected static native int getCfgMinMax_native(String configType, ConfigValue configValue);
  /**
     * Please do NOT use this function ,it is not implement yet.  
     */
    protected static native int readGPIO_native(ConfigValue configValue);
  /**
     * Please do NOT use this function ,it is not implement yet.  
    */
    protected static native int writeGPIO_native(ConfigValue configValue);

    /**
      * Select the channel which is specified by user.
      * @param  [in]chInfo
      *              Channel information will be selected.      
      * @param  [in]b_focus
      *              Focus on which window. main or sub.
      * @return 0-OK other-fail
      */
    protected static native int channelSelect_native(ChannelInfo chInfo, boolean b_focus);
    /**
      * Select the channel which is specified by user.(extra function).
      * @param  [in]chInfo
      *              Channel information will be selected.      
      * @param  [in]exChInfo
      *              Channel extra information will be selected.      
      * @param  [in]b_focus
      *              Focus on which window. main or sub.
      * @return 0-OK other-fail
      */
    protected static native int channelSelectEx_native(ChannelInfo chInfo,ExtraChannelInfo exChInfo,boolean b_focus, int audioLangIndex, int audioMts);
    /**
      * Synchronize stop current service.
      * @param  NULL
      * @return 0-OK other-fail
      */
    protected static native int syncStopService_native();
    /**
      * Start the video.
      * @param  [in]focusID
      *              0 is main, 1 is sub   
      * @return 0-OK other-fail
      */
    protected static native int startVideoStream_native(int focusID);
    /**
      * Synchronize stop the subtitle.
      * @param  NULL   
      * @return 0-OK other-fail
      */
    protected static native int syncStopSubtitleStream_native();
    /**
      * Freeze or unfreeze video.
      * @param  [in]chInfo
      *              current channel information          
      * @param  [in]freq
      *              fine tune frequence         
      * @param  [in]b_tuning
      *              tune or not tune     
      * @return 0-OK other-fail
      */
    protected static native int fineTune_native(ChannelInfo chInfo, int freq, boolean b_tuning);
    /**
      * Freeze or unfreeze video.
      * @param  [in]focusID
      *              0 is main, 1 is sub          
      * @param  [in]b_freeze
      *              freeze or unfreeze tv video       
      * @return 0-OK other-fail
      */
    protected static native int freeze_native(int focusID, boolean b_freeze);

    /**
      * Set video blue mute.
      * @param  NULL
      * @return 0-OK other-fail
      */
    protected static native int setVideoMute_native();

    /**
      * Get video resolution.
      * @param  [in]focusID
      *              0 is main, 1 is sub          
      * @param  [in/out]videoRes
      *              VideoResolution class       
      * @return 0-OK other-fail
      */
    protected static native int getVideoResolution_native(int focusID, VideoResolution videoRes);

    /**
      * Get analog audio information.
      * @param  [in]focusID
      *              0 is main, 1 is sub          
      * @param  [in/out]audioInfo
      *              AudioInfo class       
      * @return 0-OK other-fail
      */
    protected static native int getAudioInfo_native(int focusID, AudioInfo audioInfo);
    /**
      * Get dtv signal level information.
      * @param  [in]focusID
      *              0 is main, 1 is sub          
      * @param  [in/out]signalLevelInfo
      *              SignalLevelInfo class       
      * @return 0-OK other-fail
      */
    protected static native int getSignalLevelInfo_native(int focusID, SignalLevelInfo signalLevelInfo);

    /**
      * Get dtv audio language information.
      *      
      * @param  [in/out]audioInfo
      *              AudioLanguageInfo class       
      * @return 0-OK other-fail
      */
    protected static native int getDtvAudioInfo_native(AudioLanguageInfo audioInfo);

    /**
      * Set dtv audio language by audio language.
      *      
      * @param  [in]audioLang
      *              audio language string        
      * @return 0-OK other-fail
      */
    protected static native int setDtvAudioLang_native(String audioLang);
    /**
      * Set dtv audio language by audio index.
      * 
      * @param  [in]focusID
      *              0 is main, 1 is sub          
      * @param  [in]audioIndex
      *              audio index        
      * @return 0-OK other-fail
      */
    protected static native int setDtvAudioLangByIndex_native(int focusID,int audioIndex);
    /**
      * Get dtv subtitle information.
      * 
      * @param  [in/out]subtitleInfo
      *              SubtitleInfo class      
      * @return 0-OK other-fail
      */
    protected static native int getSubtitleInfo_native(SubtitleInfo subtitleInfo);
    /**
      * Set dtv subtitle by subtitle  string.
      * 
      * @param  [in]subtitleLang
      *              subtitle language string      
      * @return 0-OK other-fail
      */
    protected static native int setSubtitleLang_native(String subtitleLang);
    /**
      * Get mpeg stream by stream type  string and pid.
      * 
      * @param  [in]streamType
      *              video or audio string      
      * @param  [in]pid
      *              video pid or audio pid
      * @return 0-OK other-fail
      */
    protected static native int getStreamMpegPid_native(String streamType);
    /**
      * Select mpeg stream by stream type  string and pid.
      * 
      * @param  [in]streamType
      *              video or audio string      
      * @param  [in]pid
      *              video pid or audio pid
      * @return 0-OK other-fail
      */
    protected static native int selectMpegStreamByPid_native(String streamType, int pid);
    /**
      * Is can capture logo or not.
      * 
      * @param  NULL
      * @return  TRUE- can capture logo.  FALSE- can not capture logo
      */
    protected static native boolean isCaptureLogo_native();

    /**
      * Set audio mute.
      * 
      * @param  [in]b_mute
      *              mute audio or not mute audio
      * @return 0-OK other-fail
      */
    protected static native int setMute_native(boolean b_mute);
    /**
      * Get audio mute status.
      * 
      * @param  NULL
      * @return  TRUE-audio is mute FALSE-audio is not mute 
      */
    protected static native boolean getMute_native();
    protected static native int getDtvAudioDecodeType_native();
    /**
      * Set video blue mute.
      * 
      * @param  [in]focusID
      *              0 is main, 1 is sub      
      * @param  [in]bBlueMute
      *              blue mute or not blue mute
      * @param  [in]bBlock
      *              the channel is block or not
      * @return 0-OK other-fail
      */
    protected static native int setVideoBlueMute_native(int focusID,boolean bBlueMute,boolean bBlock);
    /**
      * Get the tv video freeze status.
      * 
      * @param  [in]focusID
      *              0 is main, 1 is sub      
      * @return TRUE-tv video is freeze FALSE-tv video is not freeze
      */
    protected static native boolean isFreeze_native(int focusID);
    /**
      * Can freeze tv video or can not freeze video.
      * 
      * @param  [in]focusID
      *              0 is main, 1 is sub      
      * @return TRUE-can freeze tv video FALSE-can not freeze tv video
      */
    protected static native boolean enableFreeze_native(int focusID);
    /**
      * Set the display aspect ratio.
      * 
      * @param  [in]dispAspRatio
      *              display aspect ratio      
      * @return 0-OK other-fail
      */
    protected static native int setDisplayAspectRatio_native(int dispAspRatio);
    /**
      * Get the display aspect ratio.
      * 
      * @param  NULL
      * @return display aspect ratio
      */
    protected static native int getDisplayAspectRatio_native();
    /**
      * Stop the video or audio stream.
      * 
      * @param  [in]focusID
      *              0 is main, 1 is sub      
      * @param  [in]streamType
      *              video or audio
      * @return 0-OK other-fail
      */
    protected static native int stopStream_native(int focusID, int streamType);
    /**
      * Start the audio stream.
      * 
      * @param  [in]focusID
      *              0 is main, 1 is sub
      * @return 0-OK other-fail
      */
    protected static native int startAudioStream_native(int focusID);
    /**
      * Sync stop the video stream.
      * 
      * @param  [in]focusID
      *              0 is main, 1 is sub
      * @return 0-OK other-fail
      */
    protected static native int syncStopVideoStream_native(int focusID);
    /**
      * Show snow when tv is no signal
      * 
      * @param  [in]focusID
      *              0 is main, 1 is sub
      * @param  [in]bSnow
      *              show snow or cancel show snow
      * @return 0-OK other-fail
      */
    protected static native int showSnowAsNoSignal_native(int focusID,boolean bSnow);
   /**
     * Update tv windows display region
     * 
     * @param  [in]focusID
     *              0 is main, 1 is sub
     * @param  [in]winX
     *              window X coordinate
     * @param  [in]winY
     *              window Y coordinate
     * @param  [in]winWidth
     *              display window width
     * @param  [in]winHeight
     *              display window height
     * @return 0-OK other-fail
     */
    protected static native int updateTVWindowRegion_native(int focusID, int winX, int winY, int winWidth, int winHeight);
   /**
     * Update tv focus window to main or sub
     * 
     * @param  [in]focusID
     *              0 is main, 1 is sub
     * @return 0-OK other-fail
     */
    protected static native int updateFocusWindow_native(int focusID);
   /**
     * Update tv mode
     * 
     * @param  [in]tvMode
     *              0 is main, 1 is sub
     * @return 0-OK other-fail
     */
    protected static native int updateTVMode_native(int tvMode);
	protected static native int setMTS_native(int focusID, int audMTSType);
	// setValue);

	/**
     *  This function set the configuration for DT library behavior.
     *
     *  @param [in] ui4_flag   - Masked flag. such as DT_USE_DST_AT_SPECIFIED_TIME
     *  @see com.mediatek.tv.model.DtType
     *
     *  @return  Return the error code.
     */
    protected static native int dtSetConfig_native(int configFlag);
	
	/**
     *  Set/Unset the current Daylight-Saving-Time.
     *
     * @param [in]  b_dls - Flag to enable/disable DST. 
     *                     TRUE  - Enable Daylight-Saving-Time.
     *                     FALSE -Disable Daylight-Saving-Time.
     *             
     * @return  Return the error code.
     */
    protected static native int dtSetDst_native(boolean bEnable);

	/**
     *   Set the timezone offset to be used in the system time
     *         library for computing the local time from GMT and  vice versa.
     *         The timezone offset is specified as the number of seconds
     *         (+/-) from the Greenwich timezone.
     *
     *          For example:
     *
     *           For the Pacific Time zone (Westcoast U.S),
     *           the timezone offset is:
     *
     *            t_tz_offset = (-8 hour * 3600 sec/hour )
     *                        = -28800 seconds.
     *
     *  @param [in]    t_tz_offset   -  timezone offset in number of seconds.
     *
     *  @return  Return the error code.
     */
    protected static native int dtSetTz_native(long tzOffset);

	/**
     *   Set the current system clock time in UTC time frame.
     *
     *  @param [in]  t_sec          - The number of seconds since the default epoch, 00:00:00Z,
     *                                            Jan 1, 1970.
     *  @param [in]  ui2_milli_sec  -Optional parameter to set the number of milliseconds
     *                                            (0 to 999).
     *
     *  @return  Return the error code.
     */
    protected static native int dtSetUtc_native(long sec, int milliSec);

	/**
     *   Set/Unset if Daylight-Saving-Time should be apply to  this locality. 
     *              In some states (Arizona, Hawaii, and  Indiana) the Day-Light-Saving is 
     *              not used.  Thus for  these locality, the DST control should be set to
     *              FALSE, so that any daylight-saving parameters received  from the
     *              broadcast stream will be ignored.
     *
     *  @param [in]  b_dls    - Flag to enable/disable DST control.
     *                     TRUE     -Enable Daylight-Saving-Time control.
     *                     FALSE    -Disable Daylight-Saving-Time control.
     *
     *	@return  Return the error code.
     */
    protected static native int dtSetDstCtrl_native(boolean bEnable);

	/**
     *  set day_saving_time change value
     * @param changeTime
     * @return Return the error code
     * @throws TVMException
     */
    protected static native int dtSetDsChange_native(long changeTime);

	/**
     *  set day_saving_time offset value
     * @param OffsetTime
     * @return Return the error code
     * @throws TVMException
     */
    protected static native int dtSetDsOffset_native(long OffsetTime);

	/**
     *  This API sets the synchronization source for the current  time.
     *
     *  @param [in]  t_sync_src_type - Specifies the types of synchronization source. such as DT_SYNC_WITH_DVB_TDT.
     *  @param [in]  t_src_type_desc - Specifies the source characteristic. such as DT_SRC_TYPE_MPEG_2_BRDCST.
     *  @param [in]  pv_src_info        - Pointer to a structure containing information
     *                                                 about the synchronization source.
     *
     *  @return Return the error code.
     *
     *  @see com.mediatek.com.model.DtType
     */
    protected static native int dtSetSyncSrc_native(int eSyncSrcType, int eSrcDescType, String data);

	/**
     *   Set/Unset if Timezone offset information received from  broadcast
     *                stream table should be apply to this locality.  By default, this setting
     *                is TRUE.  However, if the user want to explicitly specify the timezone 
     *                offset, then  it should call this API with FALSE value. This will ignore 
     *                any Timezone parameters received from the broadcast stream.
     *
     *  @param [in]  b_tz  - Flag to enable/disable timezone parameter from broadcast
     *                                stream.
     *
     *
     *  @return   Return the error code.
     */
    protected static native int dtSetTzCtrl_native(boolean bEnable);

	/**
     *  Set the system country code. If the received system time table contains
     *               a matching country code, it will be   used to adjust the time zone.
     *
     *  @param [in]  t_count_code  - Contains the system country code.
     *  @param [in]  ui2_region_id  - Contains a system region id.
     *
     *  @return  Return the error code. 
     */
    protected static native int dtSetSysCountCode_native(byte[] countCode, int regionId);

	/**
     *   Get the current Daylight-Saving-Time.
     *
     *  @param [in] VOID
     *
     *  @return  Return the Dalylight-Saving-Time on/off.
     */
    protected static native boolean dtGetDst_native();

	/**
     *  Get the current system clock time as the number of econds since 
     *              the GPS epoch, e.g, January 6, 1980,  and also the number of leap
     *              seconds since the GPS   epoch.
     *
     *              Note: As of 2005, the GPS time is ahead of UTC by  13 seconds.
     *
     *  @param [out]  pi4_gps_leap_sec - Number of GPS_UTC leap seconds (UTC) since
     *                                                    GPS epoch.
     *  @param [out] pui2_milli_sec	    - Optional parameter to store the number of
     *                                                     milliseconds (0 to 999). If this parameter is  NULL,
     *                                                     milliseconds is ignored.
     *
     *  @return Return the number of seconds from the GPS epoch (00:00:00Z, Jan 06, 1980).l     
     */
    protected static native long dtGetGps_native(int[] data);

	/**
     *!   Get the time zone offset used in the system time library
     *              for computing the local time from GMT and vice versa.
     *              Return timezone offset value as number of seconds.
     *
     *              For example, for the Pacific Time zone, the returned value
     *              is
     *
     *                 (-8 Hour ) * 3600 sec/hour = -28800 sec
     *
     *  @param [in]  VOID
     *
     *  @return  Return the time zone value as the number of seconds.
     */
    protected static native long dtGetTz_native();

	/**
     *!  Get the current system clock time in UTC time frame,   the result is 
     *             returned as the number of seconds from  a default epoch of
     *             00:00:00Z, Jan 1, 1970.
     *
     *  @param [out]  pui2_milli_sec -  Optional parameter to store the number of
     *                                                 milliseconds (0 to 999). If the parameter is NULL,  
     *                                                 milliseconds is ignored.
     *  @param [out]   pt_dt_cond     - The condition of the system clock: DT_NOT_RUNNING,
     *                                                 DT_FREE_RUNNING, DT_SYNC_RUNNING.  If NULL, 
     *                                                 then  condition code is  ignored.
     *
     *  @return  Return the number of seconds since default epoch for the current  UTC time.
     */
    protected static native long dtGetUtc_native(int[] data);

	/**
     *!   Get the current broadcast time in UTC time frame,  the result is  returned
     *               as the number of seconds from  a default epoch of 00:00:00Z, Jan 1, 1970.
     *
     *  @param [out]  pui2_milli_sec  - Optional parameter to store the number of
     *                                                 milliseconds (0 to 999). If the parameter is NULL,
     *                                                 milliseconds is ignored.
     *
     *  @param [out]  pt_dt_cond	-The condition of the system clock: DT_NOT_RUNNING,
     *                                                 DT_FREE_RUNNING, DT_SYNC_RUNNING.  If NULL, then
     *                                                 condition code is ignored.
     *
     *  @return   Return the number of seconds since default epoch for the current.
     */
    protected static native long dtGetBrdcstUtc_native(int[] data);

	/**
     *! Get a country code as signaled in the system time /date table.
     *
     *  @param [in]    ui2_idx   - Contains an index.
     *
     *  @param [out]  pt_count_code  - Contains the country code.
     *  @param [out]  pui2_region_id  - Contains the region id.
     *  @param [out]  pt_tz_offset      - The time offset for the country code and region.
     *
     *  @return  Return the error code.
     */
    protected static native int dtGetCountCode_native(int index, byte[] countCode, long[] data);

	/**
     *! Get the flag indicating if the Daylight-Saving-Time should  be applied
     *              for this locality.
     *
     *  @param [in] VOID
     *
     *  @return  Daylight-Saving-Time control.
     */
    protected static native boolean dtGetDstCtrl_native();

	/**
     *  Get the day_saving_time change value
     * 
     * @param [in] VOID
     * 
     * @return ds_change value
     * 
     * @throws TVMException
     */
    protected static native long dtGetDsChange_native();

	/**
     *  Get the day_saving_time offset value
     * 
     * @param [in] VOID
     * 
     * @return ds_offset value
     * 
     * @throws TVMException
     */
    protected static native long dtGetDsOffset_native();

	/**
     *!  Get the flag indicating if the time zone parameter received from the 
     *              broadcast table should  be applied for this locality.
     *
     *  @param [in] VOID.
     *
     *  @return Return if use time zone information from broadcast table for this location or not.
     */
    protected static native boolean dtGetTzCtrl_native();

	/**
     *!  Get the number of country code's signaled in the system  time / date table.
     *
     *  @param [in] VOID
     *
     *  @return   Return the number of individual country code entries.
     */
    protected static native int dtGetNumCountCode_native();

	/**
     *!   Get the system country code and region id.
     *
     *  @param [out]   pt_count_code  - Contains the system country code.
     *  @param [out]   pui2_region_id  - Contains the system region id.
     *  
     *  @return  Return the error code.
     */
    protected static native int dtGetSysCountCode_native(byte[] countCode, int[] data);

	/**
     *!  Get the last effective table ID used for time sync'ing.
     *         For ATSC, result may be STT (in/out-band); for DVB, 
     *         result may be TDT or TOT; for analog, result may be VBI
     *         teletext. If DT never sync'ed, return zero.
     *   
     *  @param [in] VOID 
     *         
     *  @return  Table ID for the last effective time sync.
     *  @retval  DT_STT_IN_BAND_TBL_ID        -   STT (in-band)
     *  @retval  DT_STT_OUT_OF_BAND_TBL_ID    -   STT (out-band)
     *  @retval  DT_DVB_TDT_TBL_ID            -   TDT
     *  @retval  DT_DVB_TOT_TBL_ID            -   TOT
     *  @retval  DT_VBI_TELETEXT_TBL_ID       -   VBI Teletext
     */
    protected static native byte dtGetLastSyncTblId_native();

	/**
     *!    Enable or dis-able the check input function.
     *
     *  @param [in]  b_enable -  Flag to enable/disable checking of input time.
     *                                       TRUE  : Enable checking of input time.
     *                                       FALSE: Dis-able checking of input time. (Every time
     *                                                  value received are accepted.)
     *
     *  @return Return the error code.
     */
    protected static native int dtCheckInputTime_native(boolean bEnable);

	/**
     *!    Configure the check time engine. 
     *
     *  @param [in] e_set_parm - Specifies the parameter to set in the next argument: *
     *                                          DT_USE_DEFAULT_CONFIG:  Revert to default parameters.
     *                                         DT_SET_CONSECUTIVE_VAL_TO_CHK: Specifies number of consecutive good 
     *                                         								input value to be processed before 
     *                                         								accepting the next good input time value.
     *                                          DT_SET_TIME_WINDOW_ADJUSTMENT: Specifies the delta value (tolerance) 
     *                                          							between the time difference between the 
     *                                          							current and previous one received for the 
     *                                          							input time, and the  time difference between 
     *                                          							the current and last reading of system time values.
     *
     *  @param [in] pv_value   - Pointer to the variable containg value to set.
     *                                       For DT_SET_CONSECUTIVE_VAL_TO_CHK and DT_SET_TIME_WINDOW_ADJUSTMENT, 
     *                                       this pointer  refer to a 'INT32' variable containing the set values. 
     *                                       For DT_USE_DEFAULT_CONFIG, this pointer is ignored.
     *
     *  @return   Return the error code.
     *
     *  @see com.mediatek.tv.model.DtType
     */
    protected static native int dtConfigCheckInputTime_native(int eSetType, int setValue);

	/**
	 *   Conversion from the number of UTC elapse seconds since
	 *          default epoch to UTC YYYYMMDDHHMMSS.
	 *          
	 * @param[in] utcTime
	 *            input, number of elapse seconds from default epoch in UTC
	 *            time frame.
	 *            
	 * @param[out] dtgTime
	 *            output, Structure containing Day-Time-Group values in UTC
	 *            time scale.
	 * 
	 * @return   Return the error code.
	 */
	protected static native int DtUtcSecToDtg_native(long utcTime, DtDTG dtgTime);

	/**
	 *   Conversion from the number of UTC elapse seconds since
	 *          default epoch to UTC YYYYMMDDHHMMSS in local time.
	 *          
	 * @param[in] utcTime
	 *            input, number of elapse seconds from default epoch in UTC
	 *            time frame.
	 *            
	 * @param[out] dtgTime
	 *            output, Structure containing Day-Time-Group values in local
	 *            time scale.
	 *            
	 * @return   Return the error code.
	 */
	protected static native int DtUtcSecToLocDtg_native(long utcTime, DtDTG dtgTime);

	/**
	 *   Convert between UTC day-time-group to Local
	 *             day-time-group (take into account of day-light-saving
	 *              time and timezone).
	 *
	 *              If the input dtg is local, then convert the local dtg
	 *              value to UTC dtg value; else if the input dtg is UTC
	 *              dtg, then convert the UTC dtg value to Local dtg value.
	 *              
	 * @param[in] dtgTimeIn
	 *            input, UTC or Local DTG calendar value.
	 *            
	 * @param[out] dtgTimeOut
	 *            output, UTC or Local DTG calendar value.
	 * 
	 * @return   Return the error code.
	 */
	protected static native int DtConvUtcLocal_native(DtDTG dtgTimeIn, DtDTG dtgTimeOut);

	/**
	 *    Compute the number of seconds for a specified DTG
	 *              from the default epoch.  Note: no adjustment for TZ
	 *              or DST is made in this API.
	 *
	 *              Therefore, If the input DTG is local,
	 *              then the difference is computed between local time
	 *              and 00:00:00 local Jan 1, 1970.  If the input DTG
	 *              is UTC, then the difference is computed between UTC time
	 *              and 00:00:00 UTC  Jan 1, 1970.
	 *              
	 * @param[in] dtgTime
	 *            input, Input day-time-group value (local or UTC)
	 *            
	 * @return number of seconds
	 * 
	 */
	protected static native long DtDtgToSec_native(DtDTG dtgTime);

	/**
	 *    Convert GPS seconds to UTC seconds
	 * 
	 * @param gpsSec
	 *            input, Number of seconds since GPS epoch (0000Z, 06 Jan 1980)
	 *            
	 * @return number of seconds since UTC epoch
	 * 
	 */
	protected static native long DtGpsSecToUtcSec_native(long gpsSec);

	/**
	 *    This function converts the  24 bits of BCD (binary code decimal)
	 *               for hour, minutes, and seconds into number of seconds.
	 *               
	 * @param bcdTime
	 *            input, A 24 bits BCD (binary code decimal) string for HHMMSS
	 *            
	 * @return Seconds from the BCD HHMMSS
	 */
	protected static native int DtBcdToSec_native(String bcdTime);

	/**
	 *    This function converts the 40 bits string that consists of 16 LSBs MJD
	 *               (Modified Julian Date) + 24 bits of BCD (binary code decimal) for hour,
	 *               minutes, and seconds into a DTG_T structure.
	 *
	 *               The 40 bits MJD+BCD data are received from DVB TDT and TOT table.
	 *               
	 * @param[in] bcdTime
	 *            input, A 40 bits code string consisting of 16 LSB's MJD value and + 24 bits of
	 *               BCD (binary code decimal)
	 *               
	 * @param[out] dtgTime
	 * 			output, a DTG_T structure.
	 * 
	 * @return   Return the error code.
	 */
	protected static native int DtMjdBcdToDtg_native(String bcdTime, DtDTG dtgTime);

	/**
	 *     This function converts the MJD value to a calendar date.
	 * 
	 * @param[in] mjdTime
	 *            input, Modified-Julia-Date value (for example: 45000)
	 *            
	 * @param[out] dtgTime
	 * 			output, a DTG_T structure.
	 * 
	 * @return   Return the error code.
	 */
	protected static native int DtMjdToDtg_native(long mjdTime, DtDTG dtgTime);

	/**
	 *    This function converts a calendar value to the MJD date.
	 * 
	 * @param dtgTime
	 *            input, a DTG_T structure. only year/month/day count.
	 *            
	 * @return Modified-Julia-Date value.
	 * 
	 */
	protected static native long DtDtgToMjd_native(DtDTG dtgTime);

	/**
	 *     This function converts a calendar value to the MJD date,
	 *               BCD hour/minute and second.
	 *               Note that the hour/minute 16-bit BCD code endian is 
	 *               platform-specific. It may need endian conversion to
	 *               reconstruct the 40-bit date time data.
	 *               
	 * @param[in] dtgTime
	 *            input, a DTG_T structure. only year/month/day count.
	 *            
	 * @param[out] mjdInfo
	 * 			output, mjd + hr_min + sec.
	 * 
	 * @return   Return the error code.
	 */
	protected static native int DtDtgToMjdBcd_native(DtDTG dtgTime, int[] mjdInfo);

	/**
	 *     Compute the difference in seconds between two DTG times
	 *              delta seconds = t_dtg_to - t_dtg_from.
	 *              
	 * @param dtgTimeFrom
	 *            input, a DTG_T structure. 
	 *            
	 * @param dtgTimeTo
	 * 			input, a DTG_T structure.
	 * 
	 * @return  Number of seconds between two DTG values.
	 */
	protected static native long DtDiff_native(DtDTG dtgTimeFrom, DtDTG dtgTimeTo);
	
	/**
	 *     Count the DTG time by adding one second time to be another DTG time.
	 * 
	 * @param[in] dtgTimeOld
	 *            input, a DTG_T structure. 
	 *            
	 * @param[in] addSec
	 * 			 input, additional time.
	 * 
	 * @param[out] dtgTimeNew
	 * 			output,  a DTG_T structure.
	 * 
	 * @return   Return the error code.
	 */
	protected static native int DtAdd_native(DtDTG dtgTimeOld, long addSec, DtDTG dtgTimeNew);

	/**
	 *     Given a year, determine if it is a leap year.
	 * 
	 * @param year
	 *            input, Specify the year to be evaluated 
	 *            
	 * @return  TRUE:leap-year FALSE:not leap year
	 */
	protected static native boolean DtIsLeapYear_native(long year);

	/**
	 *     This API registers a notification function that will be called by the Date
	 *              Time library when the system clock is synchronized with the current time 
	 *              value from the STT,  TDT, or TOT, or when the system clock switches to
	 *              a free running state.
	 *              The notification function will be called in the section manager callback context.
	 *              
	 * @param[in] dt_listener
	 *            input, Notification function provided by the caller. 
	 *            
	 * @param[out] h_handle 
	 * 			output, Handle reference to the client's notification function.
	 * 
	 * @return   Return the error code.
	 */
	protected static native int DtRegNfyFct_native(long[] t_handle);
	
    //protected static native int serviceSet_native(String setType, Object setValue);

	// protected static native int serviceGet_native(String getType, Object
	// getValue);

    /**
     * Set the channelList
     * 
     * @param channelOperator
     *            [in] channel operator
     * @param svlid
     *            [in] service list id
     * @param list
     *            [in out] contain the channels
     * @return 0-OK other-fail
     * @see ChannelOperator#APPEND
     * @see ChannelOperator#UPDATE
     * @see ChannelOperator#DELETE
     */
	protected static native int setChannelList_native(int channelOperator, int svlid, /* in */List<ChannelInfo> list);

    /**
     * Synchronize channel list from flash to memory
     * 
     * @param svlId
     *            [in]service list id
     * @return 0-OK other-fail
     */
    protected static native int fsSyncChannelList_native(int svlId);
    /**
     * Clean all digital channel from memory
     * 
     * @param svlId
     *            [in]service list id
     * @return 0-OK other-fail
     */
    protected static native int digitalDBClean_native(int svlId);

    /**
     * Store channel list to flash disk
     * 
     * @param svlId
     *            [in]service list id
     * @return 0-OK other-fail
     */
    protected static native int fsStoreChannelList_native(int svlId);

    /**
     * Get the channelList
     * 
     * @param svlId
     *            [in] service list id
     * @param channelList
     *            [in out] the channelList user allocated
     * @return 0-OK other-fail
     */
	protected static native int getChannelList_native(int svlId, /* in out */
	List<ChannelInfo> channelList);

    /* scan service native function start */
    
    /**
     * Analog scan native module initialization function. Before user call any other analog scan native
     * function, this api have to be invoked.
     * 
     * @return int
     */
    protected static native int scanServiceInit_native();

    
    /**
     * Begin channel scan. Application should not call this API again when scan
     * service is scanning channels
     * 
     * @param scanMode
     *            the scan mode name (SCAN_MODE_PAL_SECAM_CABLE,
     *            SCAN_MODE_PAL_SECAM_TERRESTRIAL, etc.)
     * @param scanParams
     *            scan configuration (scan type, etc.)
     * @param scanService           
     *            the caller
     * @return int
     */
    protected static native int startScan_native(String scanMode, ScanParams p, ScanService scanService);

    /**
     * Cancel channel scan. Application can only call this API when scan service
     * is scanning channels
     * 
     * @param scanMode
     *            the scan mode name (SCAN_MODE_PAL_SECAM_CABLE,
     *            SCAN_MODE_PAL_SECAM_TERRESTRIAL, etc.)
     * @return int
     */
    protected static native int cancelScan_native(String scanMode);

    /**
     * Get data of special scan mode. 
     * 
     * @param scanMode
     *            the scan mode name (SCAN_MODE_PAL_SECAM_CABLE,
     *            SCAN_MODE_PAL_SECAM_TERRESTRIAL, etc.)
     * @param type
     *            the operator type. ScanExchange static elements contain all of
     *            the type
     * @param scanExchangeData       
     *            the temporary storage. It is just a super class. Different
     *            type Different detail scanExchangeData
     * @return int
     */
    protected static native int getScanData_native(String scanMode, int type,
            ScanExchangeFrenquenceRange scanExchangeData);

    /**
     * API to exchange bytes between analog scan java and native module. 
     * 
     * @param scanData
     *            the bytes, detail format is defined in java and native module. 
     * @return int
     */
    protected static native int scanExchangeData( int[] scanData );

   /**
     * Begin dvbc channel scan. Application should not call this API again when scan
     * service is scanning channels
     * 
     * @param  [in]scanMode
     *              the scan mode name (SCAN_MODE_DVB_CABLE)
     * @param  [in]scanParams
     *              scan configuration (scan type,network id, etc.)
     * @return 0-OK other-fail
     */
    protected static native int startScanDvbc_native( String scanMode, ScanParams p );
   /**
     * Cancel dvbc channel scan. Application can only call this API when scan service
     * is scanning channels
     * 
     * @param  [in]scanMode
     *              the scan mode name (SCAN_MODE_DVB_CABLE)
     * @return 0-OK other-fail
     */
    protected static native int cancelScanDvbc_native(String scanMode);
   /**
     * get dvbc program type number. (include: radio program number,tv program number,
     * application program number)
     * 
     * @param  [in]scanMode
     *              the scan mode name (SCAN_MODE_DVB_CABLE)     
     * @param  [out]dvbcScanData
     *              dvbc program type class
     * @return 0-OK other-fail
     */
    protected static native int getDvbcScanTypeNum_native(String scanMode, DvbcProgramType dvbcScanData);
   /**
     * get dvbc scan frequence range
     * 
     * @param  [in]scanMode
     *              the scan mode name (SCAN_MODE_DVB_CABLE)     
     * @param  [out]dvbcFreqRange
     *              dvbc frequence range class
     * @return 0-OK other-fail
     */
    protected static native int getDvbcFreqRange_native(String scanMode, DvbcFreqRange dvbcFreqRange);

    protected static native int getDvbcMainFrequence_native(String scanMode, MainFrequence mainFrequence);

   /**
     * get dvbc default symrate
     * 
     * @param  [in]countryCode
     *              the user select country code.    
     * @return symrate
     */
    protected static native int getDefaultSymRate_native(String countryCode);
   /**
     * get dvbc default frequence
     * 
     * @param  [in]countryCode
     *              the user select country code.    
     * @return frequence
     */
    protected static native int getDefaultFrequence_native(String countryCode);
   /**
     * get dvbc default emod
     * 
     * @param  [in]countryCode
     *              the user select country code.    
     * @return emod
     */
    protected static native int getDefaultEMod_native(String countryCode);
   /**
     * get dvbc default networkid
     * 
     * @param  [in]countryCode
     *              the user select country code.    
     * @return networkid
     */
    protected static native int getDefaultNwID_native(String countryCode);
    protected static native int startScanDtmb_native(String scanMode, ScanParams p);
    protected static native int cancelScanDtmb_native(String scanMode);
    protected static native int getDtmbFreqRange_native(String scanMode, DtmbFreqRange dtmbFreqRange);
    protected static native int getFirstDtmbScanRF_native(String scanMode, DtmbScanRF firstRF);
    protected static native int getLastDtmbScanRF_native(String scanMode, DtmbScanRF lastRF);
    protected static native int getNextDtmbScanRF_native(String scanMode, DtmbScanRF currRF, DtmbScanRF nextRF);
    protected static native int getPrevDtmbScanRF_native(String scanMode, DtmbScanRF currRF, DtmbScanRF nextRF);
    protected static native int getCurrentDtmbScanRF_native(String scanMode, int channelId, DtmbScanRF currRF);

    /* scan service native function end */

	/* input service native function start */
	/**
	* Get input source record.
	* 
	* @param index
	*            [in] the index of input source.
	* @param inputRecord
	*            [out] the input source record.    
	* @ see InputRecord
	* @return 0-OK other-fail
	*/
    protected static native int inputServiceGetRecord_native(int index, InputRecord inputRecord);

	/**
	* Bind the designated input source on designated output window.
	* 
	* @param output
	* 		   [in] the output window(0: main; 1: sub).
	* @param inputId
	* 		   [in] the designated input source id.	 
	* @return 0-OK other-fail
	*/
    protected static native int inputServiceBind_native(int output, int inputId);

	/**
	* Swap designated two output windows.
	* 
	* @param output1
	* 		   [in] the output window(0: main; 1: sub).
	* @param output2
	* 		   [in] the output window(0: main; 1: sub). 
	* @return 0-OK other-fail
	*/
    protected static native int inputServiceSwap_native(int output1, int output2);

	/**
	* Set output window's display region.
	* 
	* @param output
	* 		   [in] the output window(0: main; 1: sub).
	* @param left, right, top, bottom
	* 		   [in] the region of output window.
	* @return 0-OK other-fail
	*/
    protected static native int setScreenOutputRect_native(int output, int left, int right, int top, int bottom);

	/**
	* Set video region.
	* 
	* @param output
	* 		   [in] the output window(0: main; 1: sub).
	* @param left, right, top, bottom
	* 		   [in] the region of video.
	* @return 0-OK other-fail
	*/
	protected static native int setScreenOutputVideoRect_native(int output, int left, int right, int top, int bottom);

	/**
	* Get output window's display region.
	* 
	* @param output
	* 		   [in] the output window(0: main; 1: sub).
	* @param inputRegion
	* 		   [out] the region of output window.
	* @see InputRegion	  
	* @return 0-OK other-fail
	*/
	protected static native int getScreenOutputRect_native(int output, InputRegion inputRegion);

	/**
	* Get video region.
	* 
	* @param output
	* 		   [in] the output window(0: main; 1: sub).
	* @param inputRegion
	* 		   [out] the region of video.
	* @see InputRegion	  
	* @return 0-OK other-fail
	*/
	protected static native int getScreenOutputVideoRect_native(int output, InputRegion inputRegion);

	/**
	* Exchange data between input service and input client.
	* 
	* @param inputSourceData
	* 		   [in] exchange data between input service and input client.
	* @return 0-OK other-fail
	*/	
    protected static native int inputSourceExchangeData( int[] inputSourceData );

	/**
	* Set designated output window's video to mute/unmte.
	* 
	* @param output
	* 		   [in] the output window(0: main; 1: sub).
	* @param mute
	* 		   [in] video mute or unmute( true: mute; false: unmute).
	* @return 0-OK other-fail
	*/	
    protected static native int inputServiceSetOutputMute(int output,   boolean mute);

    /* input service native function end */

    /* OSD service native function start */

	  /**
     * Set OSD colorkey for fliter.
     * 
     * @param enable
     *            [in] enable or disable colorkey fliter.
     * @param colorkey
     *            [in] colorkey for fliter.
     * @return true-OK other-fail
     */
    protected static native boolean setOSDColorKey_native(boolean enable, int colorkey);
	
	 /**
	* Set OSD Opacity.
	* 
	* @param opacity
	*			 [in]  opacity of osd. 0 is transprent, 255 is solid.
	* @return true-OK other-fail
	*/

    protected static native boolean setOSDOpacity_native(int opacity);

    /* OSD service native function end */

	/* Event service start */
    /**
     * Configure the event module
     * 
     * @param eventCommand
     *            [in] the event command
     * @return 0-OK other-fail
     * @see EventCommand
     */
	protected static native int eventSetCommand(EventCommand eventCommand);

    /**
     * Get the present and following event by special channel
     * 
     * @param channelInfo
     *            [in] which channel want to get the present and following event
     * @param events
     *            [in out] event list by user allocated
     * @return 0-OK other-fail
     * @see ChannelInfo
     * @see EventInfo
     */
	protected static native int getPFEvents(ChannelInfo channelInfo, List<EventInfo> events);

    /**
     * Get the schedule events by special channel
     * 
     * @param channel
     *            [in] which channel want to get the schedule event
     * @param startTime
     *            [in] the start time of the range,in second unit
     * @param endTime
     *            [in] the end time of the range,in second unit
     * @param events
     *            [in out] event list allocated by user
     * @return 0-OK other-fail
     */
	protected static native int getScheduleEvents(ChannelInfo channel,long startTime,long endTime, List<EventInfo> events);
	/* Event service end */
    /* ci service native function start */
     
     /**
     * Get number of slot support by host
     * 
     * @return number of slot support by host
     */
    protected static native int getSlotNum_native();
     /**
     * Whether slot is active,if CAM send app info(Conditional Access) to host,CAM is opreational. 
     * 
     * @param soltId
     *            [in] which slot want to get its status.
     * @return true : slot is active.
     *         false: slot is no active.
     */
    protected static native boolean isSlotActive_native(int slotId);
     /**
     * This API used to enter application's(EPG or CA...) menu.
     * 
     * @param soltId
     *            [in] Which slot used by CAM.
     * @return CIR_FAILED : slot is not active.
     *         CIR_ALREADY_SET_ENTER_MENU: Already enter menu.
     *				 CIR_OK: enter menu successful.
     */
    protected static native int enterMMI_native(int slotId);
     /**
     * This API used to get CAM's name which is send from CAM.
     * 
     * @param soltId
     *            [in] Which slot used by CAM.
     * @return NULL: get CAM name error.
     *         others: get CAM name success.
     */
    protected static native String getCamName_native(int slotId);
     /**
     * This API used to get CAM's support CA_system_ID.
     * 
     * @param soltId
     *            [in] Which slot used by CAM.
     * @return system id support by CAM.
     */
    protected static native int[] getCamSystemIDInfo_native(int slotId);
     /**
     * This API used to close MMI.
     * 
     * @param soltId
     *            [in] Which slot used by CAM.
     * @return CIR_OK: close mmi success.
     *         others: error happens on close MMI
     */    
    protected static native int closeMMI_native(int slotId);
     /**
     * MMI is requested close by CAM, after close MMI by AP, this API is called to clear MMI info in host.
     * 
     * @param soltId
     *            [in] Which slot used by CAM .
     * @return CIR_OK: close mmi success.
     *         others: error happens on close MMI
     */
    protected static native int setMMIClosed_native(int slotId);
     /**
     * This API is used to answer CAM's Menu request.
     * 
     * @param soltId
     *            [in] Which slot used by CAM.
     * @param menuID
     * 						[in] Indicate which menu object to answer
     * @param answerItem
     * 						[in] The NO of the choice selected by the user
     * @return CIR_OK: success operation.
     *				 CIR_FAILED: error operation, not send answer to CAM.
     */
    protected static native int answerMMIMenu_native(int slotId,int menuId,char answerItem);
     /**
     * This API is used to answer CAM's enqury.
     * 
     * @param soltId
     *            [in] Which slot used by CAM.
     * @param enqId
     * 						[in] indicate answer which Enq object got before
     * @param answer
     * 						[in] true means that the object contains the user input (which may be of zero length).
     *								 false means that the user wishes to abort the dialogue.
     * @param answerData
     * 						[in] answer content which shall be coded using the same character coding scheme as that used its associated Enq object.
     * @return CIR_OK: success operation.
     *				 CIR_FAILED: error operation, not send answer to CAM.
     */
    protected static native int answerMMIEnq_native(int slotId,int enqId,boolean answer,String answerData);
     /**
     * Ask the module to restore replaced PIDs and to close the session with the host control resource
     * 
     * @param soltId
     *            [in] Which slot used by CAM.
     * @return CIR_OK: success operation.
     *				 CIR_NOT_INIT: CI engine has not initialize.
     */    
    protected static native int askRelease_native(int slotId);
     /**
     * This API used to control CI driver turn on/off CI CAM.
     * 
     * @param soltId
     *            [in] Which slot used by CAM.
     * @param b_switch
     *						[in]  TRUE: turn on CI CAM.
     *									FALSE:turn off CI CAM.
     * @return CIR_OK: success operation.
     *				 CIR_NOT_INIT: CI engine has not initialize.
     */    
    protected static native int setCITsPath_native(int slotId,boolean b_switch);
     /**
     * This API used to control input source.From DTV to other source , should stop current service.
     * From other source to DTV, should resume last service.
     * 
     * @param soltId
     *            [in] Which slot used by CAM.
     * @param b_switch
     *						[in]  TRUE: DTV is input source.
     *									FALSE: NON DTV is input source.
     * @return CIR_OK: success operation.
     *				 CIR_NOT_INIT: CI engine has not initialize.
     */ 
    protected static native int setCIInputDTVPath_native(int slotId,boolean b_switch);
     /**
     * This API used to get tune channel info ,set tune channel.
     * 
     * @param soltId
     *            [in] Which slot used by CAM.
     * @param tune
     *						[in]  java class about a set of tune parameter and function.
     *							
     * @return CIR_OK: success operation.
     *				 others: operation error.
     */ 
    protected static native int getTunedChannel_native(int svlId,HostControlTune tune);

    /* ci service native function end */
    
    /* Component native function end*/
	protected static native int activateComponent_native(String  comp_name);
	protected static native int inactivateComponent_native(String comp_name);
	
	protected static native int updateSysStatus_native(String Statustype);
	protected static native boolean IsTTXAvail_native();	
	protected static native int sendkeyEventtoComp_native(int keycode, int keyevent);
	
	protected static native int lockDigitalTuner_native(int frequency);
	protected static native int unlockDigitalTuner_native(int magicID);
	
	protected static native int getCurrentDTVAudioCodec_native();
	protected static native int getCurrentDTVVideoCodec_native();
}
