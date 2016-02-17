/*
 * Copyright (C) 2010 MediaTek USA, Inc
 *
 */

package com.mediatek.tv.service;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.RemoteException;
import android.graphics.Rect;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.AnalogChannelInfo;
import com.mediatek.tv.model.CIPath;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.AudioInfo;
import com.mediatek.tv.model.AudioLanguageInfo;
import com.mediatek.tv.model.SubtitleInfo;
import com.mediatek.tv.model.DtDTG;
import com.mediatek.tv.model.DtListener;
import com.mediatek.tv.model.VideoResolution;
import com.mediatek.tv.model.SignalLevelInfo;
import com.mediatek.tv.model.ExtraChannelInfo;


public class BroadcastService implements IService {
	private static final String TAG = "[BRDCST_SVC] ";

	/**
	 * notify of svctx.
	 * 
	 * @see #ACTION_SVCTX_NFY
	 */
	public static final String ACTION_SVCTX_NFY = "android.tv.SVCTX_NFY";

	/**
	 * audio
	 */
	public static final String SVCTX_FOR_CI_STREAM_TYPE_AUDIO = "audio";

	/**
	 * video
	 */
	public static final String SVCTX_FOR_CI_STREAM_TYPE_VIDEO = "video";

	/**
	 * notify code.
	 */
	public static final String SVCTX_NFY_CODE = "notifyCode";

	/**
	 * siganl is lost. such as unplug signal cable.
	 */
	public static final String SVCTX_NFY_NO_SIGNAL = "noSignal";

	/**
	 * siganl is locked.
	 */
	public static final String SVCTX_NFY_WITH_SIGNAL = "withSignal";

	/**
	 * service is unblocked.
	 */
	public static final String SVCTX_NFY_SERVICE_UNBLOCKED = "serviceUnblocked";

	/**
	 * service is unblocked.
	 */
	public static final String SVCTX_NFY_SERVICE_BLOCKED = "serviceBlocked";

	/**
	 * notify data - block type .
	 */
	public static final String SVCTX_NFY_DATA_BLOCK_TYPE = "blockType";

	/**
	 * channel block.
	 */
	public static final String SVCTX_NFY_DATA_CHANNEL_BLOCK = "channelBlock";

	/**
	 * input block.
	 */
	public static final String SVCTX_NFY_DATA_INPUT_BLOCK = "inputBlock";

	/**
	 * service change is completed.
	 */
	public static final String SVCTX_NFY_SERVICE_CHANGED = "serviceChanged";

	/**
	 * stream is opened. maybe audio or video.
	 */
	public static final String SVCTX_NFY_STREAM_OPENED = "streamOpened";

	/**
	 * stream is stopped. maybe audio or video.
	 */
	public static final String SVCTX_NFY_STREAM_STOPPED = "streamStopped";

	/**
	 * stream is started. maybe audio or video.
	 */
	public static final String SVCTX_NFY_STREAM_STARTED = "streamStarted";

	/**
	 * there is not stream specified by user.
	 */
	public static final String SVCTX_NFY_NO_STREAM = "streamEmpty";

	/**
	 * resolution of vide is updated.
	 */
	public static final String SVCTX_NFY_VIDEO_UPDATED = "videoUpdate";

	/**
	 * audio format is updated. MTS.
	 */
	public static final String SVCTX_NFY_AUDIO_UPDATED = "audioUpdate";

	/**
	 * service has no audio and video.
	 */
	public static final String SVCTX_NFY_NO_AUDIO_VIDEO = "noAudioVideo";

	/**
	 * audio only service. there is not video
	 */
	public static final String SVCTX_NFY_AUDIO_ONLY = "audioOnly";

	/**
	 * video only service. there is not audio
	 */
	public static final String SVCTX_NFY_VIDEO_ONLY = "videoOnly";

	/**
	 * audio and video is normal.
	 */
	public static final String SVCTX_NFY_AUDIO_VIDEO_NORMAL = "avNormal";

	/**
	 * service is stooped. such as when do channel scan.
	 */
	public static final String SVCTX_NFY_SVC_STOPPED = "serviceStopped";

	/**
	 * service is scrambled.
	 */
	public static final String SVCTX_NFY_SVC_SCRAMBLED = "serviceScrambled";
	public static final String SVCTX_NFY_AUDIO_AND_VIDEO_SCRAMBLED   = "videoAndAudioScrambled";
	public static final String SVCTX_NFY_AUDIO_CLEAR_VIDEO_SCRAMBLED = "audioScrambled";
	public static final String SVCTX_NFY_AUDIO_NO_VIDEO_SCRAMBLED    = "audioScrambledAndNoVideo";
	public static final String SVCTX_NFY_VIDEO_CLEAR_AUDIO_SCRAMBLED = "videoScrambled";
	public static final String SVCTX_NFY_VIDEO_NO_AUDIO_SCRAMBLED    = "videoScrambledAndNoAudio";
	
	/**
	 * notify data - av abnormal(scramble svc_stop video_only audio_only) .
	 */
	public static final String SVCTX_NFY_DATA_AUDIO_VIDEO_ABNORMAL = "avAbnormal";

	/**
	 * mute or unmute audio .
	 */
	@Deprecated
	public static final String SVCTX_SET_MUTE = "setMute";

	/**
	 * get audio mute status.
	 */
	@Deprecated
	public static final String SVCTX_GET_MUTE = "getMute";

	/**
	 * get display aspect ratio.
	 */
	@Deprecated
	public static final String SVCTX_GET_DISP_ASP_RATIO = "getDispAspRatio";

	/**
	 * set display aspect ratio.
	 */
	@Deprecated
	public static final String SVCTX_SET_DISP_ASP_RATIO = "setDispAspRatio";

	/**
	 * Audio decode type
	 */
	public static final int SVCTX_AUDIO_DECODE_TYPE_UNKNOWN = 0;
	public static final int SVCTX_AUDIO_DECODE_TYPE_AC3 = 1;    //Dolby
	public static final int SVCTX_AUDIO_DECODE_TYPE_EAC3 = 2;   //DolbyPlus
	
	/**
	 * Display aspect ratio type. 4:3, 16:9
	 */

	public static final int SVCTX_DISP_ASP_RATIO_UNKNOWN = 0;
	public static final int SVCTX_DISP_ASP_RATIO_4_3 = 1;
	public static final int SVCTX_DISP_ASP_RATIO_16_9 = 2;
	public static final int SVCTX_DISP_ASP_RATIO_2_21_1 = 3;

	/**
	 * block type .
	 */
    public static final int SVCTX_NFY_DATA_BLOCK_CH = 1;
    public static final int SVCTX_NFY_DATA_BLOCK_INP = 2;

	/* MTS type */
    
    public static final int SVCTX_AUD_MTS_UNKNOWN = 0;
    public static final int SVCTX_AUD_MTS_MONO = 1;
    public static final int SVCTX_AUD_MTS_STEREO = 2;
    public static final int SVCTX_AUD_MTS_SUB_LANG = 3;
    public static final int SVCTX_AUD_MTS_DUAL1 = 4;
    public static final int SVCTX_AUD_MTS_DUAL2 = 5;
    public static final int SVCTX_AUD_MTS_NICAM_MONO = 6;
    public static final int SVCTX_AUD_MTS_NICAM_STEREO = 7;
    public static final int SVCTX_AUD_MTS_NICAM_DUAL1 = 8;
    public static final int SVCTX_AUD_MTS_NICAM_DUAL2 = 9;
    public static final int SVCTX_AUD_MTS_FM_MONO = 10;
    public static final int SVCTX_AUD_MTS_FM_STEREO = 11;

	/*audio alternative value*/
	public static final int SVCTX_AUD_ALTERNATIVE_CHANNELS_UNKNOWN                = 0;
	public static final int SVCTX_AUD_ALTERNATIVE_CHANNELS_MONO                   = 1;   /* 1/0 */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_MONO_SUB              = 2;   /* 1+sub-language */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_DUAL_MONO             = 3;    /* 1+1 */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_STEREO                = 4;   /* 2/0 */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_STEREO_SUB            = 5;  /* 2+sub-language */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_STEREO_DOLBY_SURROUND = 6;  /* 2/0, dolby surround */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_SURROUND_2CH          = 7;  /* 2/1 */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_SURROUND              = 8;   /* 3/1 */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_3_0                   = 9;   /* 3/0 */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_4_0                   = 10;   /* 2/2 */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_5_1                   = 11;   /* 3/2.L */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_7_1                   = 12;   /* 5/2.L*/
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_FM_MONO_NICAM_MONO   = 13;
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_FM_MONO_NICAM_STEREO = 14;
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_FM_MONO_NICAM_DUAL   = 15;
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_5_0                  = 16;  /* 3/2 */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_JOINT_STEREO         = 17;
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_FMRDO_MONO           = 18;
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_FMRDO_STEREO         = 19;
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_MONO_LFE             = 20;              /* 1/0.L */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_DUAL_MONO_LFE        = 21;          /* 1+1.L */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_STEREO_LFE           = 22;             /* 2/0.L */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_SURROUND_2CH_LFE     = 23;      /* 2/1.L */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_SURROUND_LFE         = 24;          /* 3/1.L */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_3_1                  = 25;    /* 3/0.L */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_4_1                  = 26;  /* 2/2.L */
	public static final int SVCTX_AUD_ALTERNATIVE__CHANNELS_OTHERS               = 27;  /* please reserve this entry as the last one. */

	/**
	 * set display aspect ratio.
	 */
	@Deprecated
	public static final String SVCTX_SET_VIDEO_BLUE_MUTE = "setVideoBlueMute";

	public static final int UNKNOWN_SOURCE = 0;
	public static final int ATV_SOURCE   = 1;
	public static final int DTV_SOURCE   = 2;
	
	private static int preChSource = UNKNOWN_SOURCE;
	
	/**
	 * Focus window.
	 */
	public static final String SVCTX_FOCUS_WIN_MAIN = "main";
	public static final String SVCTX_FOCUS_WIN_SUB = "sub";

	private static int SVCTX_FOCUS_WIN_ID_MAIN = 0;
	private static int SVCTX_FOCUS_WIN_ID_SUB = 1;
	
	private static int SVCTX_STREAM_TYPE_AUDIO = 0;
	
	private int focusWinID = 0;
	private int tvMode = 0;
	private InputService inputService = null;

	public static String BrdcstServiceName = "BrdcstServiceName";

	protected BroadcastService() {
		dtInternalMapTable = new HashMap<Long, DtInternalMapElement>();
	}
	
	private static class DtInternalMapElement {
        //public Integer m_Handle;
        public DtListener m_DTListener;
        public DtInternalMapElement(DtListener dtListener){
           // m_Handle = handle;
            m_DTListener = dtListener;
        }
    }
	private static Map<Long, DtInternalMapElement> dtInternalMapTable;
	

	private int convertFocusID(String strFocusID) throws TVMException {
		if (strFocusID.equals(SVCTX_FOCUS_WIN_MAIN)) {
			return SVCTX_FOCUS_WIN_ID_MAIN;
		} else if (strFocusID.equals(SVCTX_FOCUS_WIN_SUB)) {
			return SVCTX_FOCUS_WIN_ID_SUB;
		} else {
			throw new TVMException(-1, "Focus window string is invalid");
		}
	}
	
	private String convertFocusID(int FocusID) throws TVMException {
        if (FocusID == SVCTX_FOCUS_WIN_ID_MAIN) {
            return SVCTX_FOCUS_WIN_MAIN;
        } else if (FocusID == SVCTX_FOCUS_WIN_ID_SUB) {
            return SVCTX_FOCUS_WIN_SUB;
        } else {
            throw new TVMException(-1, "Focus window string is invalid");
        }
    }
	
	private int checkReturn(int ret, String strLog) throws TVMException{
		if (ret < 0) {
			Logger.e(TAG, strLog + " failed.");
			throw new TVMException(ret, strLog + " failed.");
		} else {
			Logger.i(TAG, strLog + " success!");
		}
		
		return ret;
	}
    
    private InputService getInputService() {
        if (this.inputService == null){
            TVManager tvManager = TVManager.getInstance(null);
            this.inputService = (InputService) tvManager.getService(InputService.InputServiceName);
        }
        return this.inputService;
    }
    
    private boolean isTVSource(int focusID)throws TVMException {
        InputService inputSvc = this.getInputService();
        String[] tvInputSource = inputSvc.getDesignatedTypeInputsString(InputService.INPUT_TYPE_TV);
        String strInput = inputSvc.getLastInputOfOutput(convertFocusID(focusID));
        
        for (int i = 0; i < tvInputSource.length; i++){
            if (null != strInput) {
                if (tvInputSource[i].equals(strInput)) {
                    return true;                    
                }
            }            
        }
        return false;
    }
    
    public int updateTVWindowRegion(int focusID) throws TVMException{
        int ret = -1;
        InputService inputService = this.getInputService();
        Rect rect = inputService.getScreenOutputRect(convertFocusID(focusID));
        
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {                
                ret = service.updateTVWindowRegion_proxy(focusID, rect.left, rect.top, rect.right-rect.left, rect.bottom-rect.top );
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }    
        
        return checkReturn(ret, "updateTVWindowRegion");
    }	

    private CIService ciService;
    
	/**
	 * To select the channel which is specified by user.
	 * @param chInfo
	 *            Channel information will be selected.
	 */
	public int channelSelect(ChannelInfo chInfo) throws TVMException {
        int ret = -1;
        boolean b_focus = true;
        
		Logger.i(TAG, "select channel begin");
        int win_id;
        
        int  ChSource  = UNKNOWN_SOURCE;
		// to check scan service
		// to check input service
		
		b_focus = isTVSource(this.focusWinID);
		win_id = b_focus? this.focusWinID: this.focusWinID == SVCTX_FOCUS_WIN_ID_MAIN?SVCTX_FOCUS_WIN_ID_SUB:SVCTX_FOCUS_WIN_ID_MAIN;
		
		// select channel
		Logger.i(TAG, "Ready to select channel! b_focus = " + b_focus + ", win_id = " + win_id);

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
			TVManager tvManager = TVManager.getInstance(null);
			this.ciService = (CIService) tvManager.getService(CIService.CIServiceName);
            if (service != null) {
                if(chInfo instanceof AnalogChannelInfo)
                {
                    CIPath ciPath = this.ciService.getCITSPath();
                    ChSource      = ATV_SOURCE;

                    if( ChSource != preChSource )
                    {
                    	ciPath.switchPath(false);   /* Only when DTV, it is true */
                    }

                    preChSource = ATV_SOURCE;
                }
                else
                {
                    CIPath ciPath = this.ciService.getCITSPath();
                    ChSource      = DTV_SOURCE;

                    if( ChSource != preChSource )
                    {
                    	ciPath.switchPath(true);   /* Only when DTV, it is true */
                    }

                    preChSource = DTV_SOURCE;
                }
                ret = service.channelSelect_proxy(b_focus, chInfo);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		return checkReturn(ret, "select channel");
	}
	
	
	/**
	 * @brief To select the channel which is specified by user.(overload)
	 * @param ChannelInfo chInfo  ExtraChannelInfo extraChInfo(like audioLanguage)
	 *            Channel information will be selected.
	 */
	public int channelSelect(ChannelInfo chInfo,ExtraChannelInfo exChInfo) throws TVMException {
        int ret = -1;
        boolean b_focus = true;
        int  ChSource  = UNKNOWN_SOURCE;
		Logger.i(TAG, "select channel begin");
        int win_id;
        int audioLangIndex = 0;
        int audioMts       = 0;
		// to check scan service
		// to check input service
		
		b_focus = isTVSource(this.focusWinID);
		win_id = b_focus? this.focusWinID: this.focusWinID == SVCTX_FOCUS_WIN_ID_MAIN?SVCTX_FOCUS_WIN_ID_SUB:SVCTX_FOCUS_WIN_ID_MAIN;
		
		// select channel
		Logger.i(TAG, "Ready to select channel! b_focus = " + b_focus + ", win_id = " + win_id);

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
			TVManager tvManager = TVManager.getInstance(null);
			this.ciService = (CIService) tvManager.getService(CIService.CIServiceName);
            if (service != null) {
                if(chInfo instanceof AnalogChannelInfo)
                {
                    CIPath ciPath = this.ciService.getCITSPath();
                    
                    ciPath.switchPath(false);   
                }
                else
                {
                    CIPath ciPath = this.ciService.getCITSPath();
                    ChSource      = DTV_SOURCE;
                    System.out.println("preChSource is "+ preChSource + " ChSource is "+ ChSource);
                    if(ChSource  != preChSource)
                    {
                    	ciPath.switchPath(true);
                    	preChSource = DTV_SOURCE;
                    	System.out.println("ciPath.switchPath(true)!!!");
                    }
                    else
                    {
                    	preChSource = DTV_SOURCE;
                    	System.out.println("do not switchPath!!");
                    	/*do nothing*/
                    }
                }
				if(exChInfo != null)
				{
					audioLangIndex = exChInfo.getAudioLangIndex();
					System.out.println("audioLangIndex is "+audioLangIndex);
				
					audioMts = exChInfo.getAudioMts();
					System.out.println("audioMts is "+audioMts);
				}
                ret = service.channelSelectEx_proxy(b_focus, audioLangIndex,audioMts,chInfo,exChInfo);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		return checkReturn(ret, "select channel");
	}

	/**
	 *  Synchronize stop current service.
	 * @param 
	 */
	public int syncStopService() throws TVMException {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.syncStopService_proxy();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		return checkReturn(ret, "sync stop service");		
	}

	/**
	 *  To set some attribute about A/V.
	 * @param setType
	 *            set type. (Not define now)
	 * @param objSetValue
	 *            information for set operator.
	 */
	@Deprecated
    public int serviceSet(String setType, Object objSetValue) throws TVMException {
        int ret = -1;

		return ret;
	}

	/**
	 *  To get some attribute about A/V.
	 * @param getType
	 *            get type. (Not define now)
	 * @param objGetValue
	 *            information for set operator.
	 */
	@Deprecated
	public Object serviceGet(String getType) throws TVMException {
		Object objGetValue = new Object();
        //TODO
		return objGetValue;
	}
	/**
	 *  is capture logo.
	 * @param NULL
	 */
	public boolean isCaptureLogo() throws TVMException {
		boolean ret  = false;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret  = service.isCaptureLogo_proxy();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		return ret;
	}
	/**
	 *  Set audio mute.
	 * @param b_mute
	 *            indicator mute or unmute.
	 */
	public int setMute(boolean b_mute) throws TVMException {
        int ret  = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret  = service.setMute_proxy(b_mute);
                
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
				
		return checkReturn(ret, "set mute");
	}

	/**
	 *  Get audio mute.
	 * @param
	 * @return true-mute false-unmute
	 */
	public boolean getMute() throws TVMException {
        boolean ret = false;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.getMute_proxy();
                return ret;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return ret;
	}
	/**
	 *  Set video as blue mute.
	 * @param NULL
	 *           
	 */
	public int setVideoBlueMute() throws TVMException {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setVideoBlueMute_proxy(this.focusWinID);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		return checkReturn(ret, "set VideoBlueMute");
	}
	
	/**
	 *  Set video as blue mute.
	 * @param bBlueMute
	 * if the bBlueMute is true, force set the screen as blue mute; else cancel the blue mute.
	 *           
	 */
	public int setVideoBlueMute(boolean bBlueMute) throws TVMException {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setVideoBlueMuteEx_proxy(this.focusWinID,bBlueMute,false);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		return checkReturn(ret, "set VideoBlueMute");
	}

	/**
	 *  Set video as blue mute.
	 * @param bBlueMute
	 * if the bBlueMute is true, force set the screen as blue mute; else cancel the blue mute.
	 * @param bBlock
	 * if the channel is block, bBlock is true; else bBlock is false.
	 *           
	 */
	public int setVideoBlueMute(boolean bBlueMute,boolean bBlock) throws TVMException {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setVideoBlueMuteEx_proxy(this.focusWinID,bBlueMute,bBlock);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		return checkReturn(ret, "set VideoBlueMute");
	}
	/**
	 *  is freeze or not.
	 * @param
	 * @return true-freeze false-unfreeze
	 */
	public boolean isFreeze(String strFocusID) throws TVMException {
        boolean ret = false;
        int focusID = convertFocusID(strFocusID);
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.isFreeze_proxy(focusID);
                return ret;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return ret;
	}
	
	/**
	 *  is freeze or not.
	 * @param
	 * @return true-freeze false-unfreeze
	 */
	public boolean enableFreeze(String strFocusID) throws TVMException {
        boolean ret = false;
        int focusID = convertFocusID(strFocusID);
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.enableFreeze_proxy(focusID);
                return ret;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return ret;
	}
	
	/**
	 *  Set aspect ratio of display.
	 * @param dispAspRatio
	 *            aspect ratio type.
	 */
	public int setDisplayAspectRatio(int dispAspRatio) throws TVMException {
        int ret = -1;

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setDisplayAspectRatio_proxy(dispAspRatio);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
				
		return checkReturn(ret, "set aspect ratio of display");
	}

	/**
	 *  Get aspect ratio of display.
	 * @param 
	 * @return aspect ratio type.
	 */
	public int getDisplayAspectRatio() throws TVMException {
        int ratio = 0;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ratio = service.getDisplayAspectRatio_proxy();
                return ratio;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ratio;
	}

	/**
	 *  Start fine tune for analog TV.
	 * @param chInfo
	 *            current channel information.
	 */
	public int fineTune(ChannelInfo chInfo) throws TVMException {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                if (chInfo instanceof AnalogChannelInfo) {     
                    Logger.i(TAG, " do fine tune!!!");
                    ret = service.fineTune_proxy((AnalogChannelInfo)chInfo, ((AnalogChannelInfo) chInfo).getFrequency(), true);
                } else {
                    Logger.e(TAG, "chInfo is not AnalogChannelInfo!!!!!");
                }
                
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		return checkReturn(ret, "fine tune");
	}

	/**
	 *  Exit fine tune. If user want to save the result of fine tune,
	 *        please assign the modified value of frequency to "freq", else
	 *        assign original frequency.
	 * @param chInfo
	 *            current channel information.
	 */
	public int exitFineTune(ChannelInfo chInfo) throws TVMException {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                if (chInfo instanceof AnalogChannelInfo) {
                    ret = service.fineTune_proxy((AnalogChannelInfo)chInfo, ((AnalogChannelInfo) chInfo).getFrequency(), false);
                } else {
                    Logger.e(TAG, "chInfo is not AnalogChannelInfo!!!!!");
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		return checkReturn(ret, "exit fine tune "); 
	}

	/**
	 *  Freeze or Unfreeze video.
	 * @param focusID
	 *            Focus on which window. main or sub.
	 * @param b_freeze
	 *            If set true, do freeze, else do unfreeze.
	 */
    public int setFreeze(String strFocusID, boolean b_freeze) throws TVMException {
		int focusID = convertFocusID(strFocusID);
        int ret = -1;//

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.freeze_proxy(focusID, b_freeze);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

		return checkReturn(ret, "set freeze"); 
	}

	/**
	 *  set blue mute.
	 * @param None
	 *            .
	 */
	public int setVideoMute() throws TVMException {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setVideoMute_proxy();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		return checkReturn(ret, "set video mute "); 
	}

	/**
	 *  get video resolution blue mute.
	 * @param strFocusID
	 *            focus windows. main or sub. .
	 */
    public VideoResolution getVideoResolution(String strFocusID) throws TVMException {
		VideoResolution videoRes = new VideoResolution();
		int focusID = convertFocusID(strFocusID);
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.getVideoResolution_proxy(focusID, videoRes);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		checkReturn(ret, "get video resolution tune"); 
		
		return videoRes;
	}

	/**
	 *  get audio info.
	 * @param strFocusID
	 *            focus windows. main or sub. .
	 */
    public AudioInfo getAudioInfo(String strFocusID) throws TVMException {
		AudioInfo audioInfo = new AudioInfo();
		int focusID = convertFocusID(strFocusID);
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.getAudioInfo_proxy(focusID, audioInfo);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		checkReturn(ret, "get audio info"); 
		
		return audioInfo;
	}

    /**
     *  set audio info.
     * @param strFocusID
     *            focus windows. main or sub. .
     * @param audMTSType    
     *            MTS Audio type.        
     */
    public int setAudioInfo(String strFocusID, int audMTSType) throws TVMException {
        int ret = -1;
        int focusID = convertFocusID(strFocusID);
        
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {                
                ret = service.setMTS_proxy(focusID, audMTSType);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }        
        return checkReturn(ret, "setAudioInfo");       
    }       

	/**
	 *  get signal Level info.
	 * @param strFocusID
	 *            focus windows. main or sub. .
	 */
    public SignalLevelInfo getSignalLevelInfo(String strFocusID) throws TVMException {
		int focusID = convertFocusID(strFocusID);
		int ret = -1;
		SignalLevelInfo signalvl = new SignalLevelInfo();
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
            	ret = service.getsignalLevelInfo_proxy(focusID,signalvl);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
		checkReturn(ret, "get signal level"); 
		
		return signalvl;
	}
    
	/**
	 *  get subtitle info.
	 * @param NULL
	 *           
	 */
    public SubtitleInfo getSubtitleInfo() throws TVMException {
		SubtitleInfo subtitleInfo = new SubtitleInfo();
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.getSubtitleInfo_proxy(subtitleInfo);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		checkReturn(ret, "get Subtitle info"); 
		
		return subtitleInfo;
	}
    
	/**
	 *  set digital subtitle Lang
	 * @param NULL 
	 * return       
	 */
    public int setSubtitleLang(String subtitleLang) throws TVMException {
        int ret = -1;
        
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setSubtitleLang_proxy(subtitleLang);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		checkReturn(ret, "setSubtitleLang !!"); 
		
		return ret;
    }
    
	/**
	 *  Synchronize stop subtitle stream.
	 * @param 
	 */
	public int syncStopSubtitleStream() throws TVMException {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.syncStopSubtitleStream_proxy();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		return checkReturn(ret, "sync stop service");		
	}
	
	/**
	 *  get mpeg stream pid.
	 * @param streamType "audioType" "videoType"
	 * return stream pid      
	 */
	public int getStreamMpegPid(String streamType) throws TVMException {
		int pid = 0;
		
    	try {
        	ITVRemoteService service = TVManager.getRemoteTvService();
        	if (service != null) {
            	pid = service.getStreamMpegPid_proxy(streamType);
        		}
    		} catch (RemoteException e) {
        		e.printStackTrace();
    		}
			
			return pid;
	}

	/**
	 *  select stream by the stream pid
	 * @param streamType "audioType" "videoType" 
	 * @param stream pid
	 * return ret      
	 */
    public int selectMpegStreamByPid(String streamType, int pid) throws TVMException {
	int ret = -1;
	
	try {
    	ITVRemoteService service = TVManager.getRemoteTvService();
    	if (service != null) {
        	ret = service.selectMpegStreamByPid_proxy(streamType, pid);
    		}
		} catch (RemoteException e) {
    		e.printStackTrace();
		}
		
		checkReturn(ret, "selectMpegStreamByPid"); 
		
		return ret;
	}
    
  
	/**
	 *  get digital audio language info (audio number && audio language in the stream)
	 * @param NULL 
	 * return AudioLanguageInfo      
	 */
    public AudioLanguageInfo getDtvAudioLangInfo() throws TVMException {
    	AudioLanguageInfo audioLangInfo = new AudioLanguageInfo();
        int ret = -1;
        int audioType = SVCTX_AUDIO_DECODE_TYPE_UNKNOWN;

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.getDtvAudioLangInfo_proxy(audioLangInfo);                
                audioType = service.getDtvAudioDecodeType_proxy();
				audioLangInfo.setAudioDecodeType(audioType);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		checkReturn(ret, "getDtvAudioLangInfo!!"); 
		
		return audioLangInfo;
    }
    
	/**
	 *  set digital audio language info (audio number && audio language in the stream)
	 * @param NULL 
	 * return AudioLanguageInfo      
	 */
    public int setDtvAudioLang(String audioLang) throws TVMException {
        int ret = -1;
        
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setDtvAudioLang_proxy(audioLang);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		checkReturn(ret, "setDtvAudioLang !!"); 
		
		return ret;
    }
    
	/**
	 *  set digital audio language info (audio number && audio language in the stream)
	 * @param NULL 
	 * return AudioLanguageInfo      
	 */
    public int setDtvAudioLangByIndex(int audioIndex) throws TVMException {
        int ret = -1;
        
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setDtvAudioLangByIndex_proxy(this.focusWinID,audioIndex);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		checkReturn(ret, "setDtvAudioLangByIndex !!"); 
		
		return ret;
    }
    
    /**
	 *  stop the audio  in asynchronous way.
	 * @param NULL 
	 * return ret(0 success.  else  fail)      
	 */
    public int stopAudioStream() throws TVMException {      
        int ret = -1;
        
        /* Check whether TV is on focus */
        if (isTVSource(this.focusWinID) == false){
            checkReturn(-1, "Focus window is not TV. Operation is NOT ALLOWED!!");
            return -1;
        }else {
            try {
                ITVRemoteService service = TVManager.getRemoteTvService();
                if (service != null) {                
                    ret = service.stopStream_proxy(this.focusWinID, SVCTX_STREAM_TYPE_AUDIO);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }    
        }       
        
        return checkReturn(ret, "stopAudioStream");
    }
    
	/**
	 *  select the audio stream
	 * @param NULL 
	 * return ret(0 success.  else  fail)      
	 */
    public int startAudioStream() throws TVMException {
        int ret = -1;
        
        /* Check whether TV is on focus */
        if (isTVSource(this.focusWinID) == false){
            checkReturn(-1, "Focus window is not TV. Operation is NOT ALLOWED!!");
            return -1;
        }else {
            try {
                ITVRemoteService service = TVManager.getRemoteTvService();
                if (service != null) {                
                    ret = service.startAudioStream_proxy(this.focusWinID);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }    
        }       
        
        return checkReturn(ret, "startAudioStream");
    }   
    
    /**
	 *  stop the video in synchronous way.
	 * @param NULL 
	 * return ret(0 success.  else  fail)      
	 */
    public int syncStopVideoStream() throws TVMException {      
        int ret = -1;
        
        /* Check whether TV is on focus */
        if (isTVSource(this.focusWinID) == false){
            checkReturn(-1, "Focus window is not TV. Operation is NOT ALLOWED!!");
            return -1;
        }else {
            try {
                ITVRemoteService service = TVManager.getRemoteTvService();
                if (service != null) {                
                    ret = service.syncStopVideoStream_proxy(this.focusWinID);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }    
        }       
        
        return checkReturn(ret, "syncStopVideoStream");
    }
    
    /**
	 *  start the video.
	 * @param NULL 
	 * return ret(0 success.  else  fail)      
	 */
    public int startVideoStream() throws TVMException {      
        int ret = -1;
        
        /* Check whether TV is on focus */
        if (isTVSource(this.focusWinID) == false){
            checkReturn(-1, "Focus window is not TV. Operation is NOT ALLOWED!!");
            return -1;
        }else {
            try {
                ITVRemoteService service = TVManager.getRemoteTvService();
                if (service != null) {                
                    ret = service.startVideoStream_proxy(this.focusWinID);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }    
        }       
        
        return checkReturn(ret, "syncStopVideoStream");
    }
    
    /**
	 *  show Snow when the signal status is no signal
	 * @param NULL 
	 * return ret(0 success.  else  fail)      
	 */
    public int showSnowAsNoSignal() throws TVMException {      
        int ret = -1;
        try 
        {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) 
            {                
                ret = service.showSnowAsNoSignal_proxy(this.focusWinID,true);
            }
        } catch (RemoteException e) 
        {
            e.printStackTrace();
        }        
 
        return checkReturn(ret, "showSnowAsNoSignal");
    }
    

    /**
	 *  show Snow when the signal status is no signal
	 * @param bSnow 
	 * if the bSnow is true,show snow. else cancel show snow. 
	 * return ret(0 success.  else  fail)      
	 */
    public int showSnowAsNoSignal(boolean bSnow) throws TVMException {      
        int ret = -1;
        try 
        {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) 
            {                
                ret = service.showSnowAsNoSignal_proxy(this.focusWinID, bSnow);
            }
        } catch (RemoteException e) 
        {
            e.printStackTrace();
        }        
 
        return checkReturn(ret, "showSnowAsNoSignal");
    }

	/**
	 *  update the focus window(main to sub or sub to main)
	 * @param FocusID 
	 * return ret(0 success.  else  fail)      
	 */
    public int updateFocusWindow(String strFocusID) throws TVMException {
        int ret = -1;
        this.focusWinID = this.convertFocusID(strFocusID);
        
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {                
                ret = service.updateFocusWindow_proxy(this.focusWinID);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }        
        checkReturn(ret, "updateFocusWindow"); 
        
        return checkReturn(ret, "updateFocusWindow");
    }       
    
    /**
	 *  update the tv mode(pip-pop to normal or normal to pip-pop)
	 * @param tvState 
	 * return ret(0 success.  else  fail)      
	 */
    public int updateTVMode(InputService.InputState tvState) throws TVMException {
        int ret = -1;

        if (tvState == InputService.InputState.INPUT_STATE_NORMAL) {
            this.tvMode = 0;
        } else if (tvState == InputService.InputState.INPUT_STATE_PIP) {
            this.tvMode = 1;
        } else if (tvState == InputService.InputState.INPUT_STATE_POP) {
            this.tvMode = 2;
        } else {
            checkReturn(ret, "tvState Error");
            return ret;
        }
        
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.updateTVMode_proxy(this.tvMode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return checkReturn(ret, "updateTVMode");
    }    
	
    /**
     *  This function set the configuration for DT library behavior.
     *
     *  @param [in] ui4_flag   - Masked flag. such as DT_USE_DST_AT_SPECIFIED_TIME
     *  @see com.mediatek.tv.model.DtType
     *
     *  @return  Return the error code.
     */
    public int dtSetConfig(int configFlag) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtSetConfig_proxy(configFlag);
				checkReturn(ret, "dtSetConfig");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dtSetConfig");
	}
   
    /**
     *  Set/Unset the current Daylight-Saving-Time.
     *
     * @param [in]  b_dls - Flag to enable/disable DST. 
     *                     TRUE  - Enable Daylight-Saving-Time.
     *                     FALSE -Disable Daylight-Saving-Time.
     *             
     * @return  Return the error code.
     */
    public int dtSetDst(boolean bEnable) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtSetDst_proxy(bEnable);
				checkReturn(ret, "dtSetDst");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dtSetDst");
	}
    
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
    public int dtSetTz(long tzOffset) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtSetTz_proxy(tzOffset);
				checkReturn(ret, "dtSetTz");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dtSetTz");
	}
    
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
    public int dtSetUtc(long sec, int milliSec) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtSetUtc_proxy(sec, milliSec);
				checkReturn(ret, "dtSetUtc");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dtSetUtc");
	}
    
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
    public int dtSetDstCtrl(boolean bEnable) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtSetDstCtrl_proxy(bEnable);
				checkReturn(ret, "dtSetDstCtrl");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dtSetDstCtrl");
	}
    
    /**
     *  set day_saving_time change value
     * @param changeTime
     * @return Return the error code
     * @throws TVMException
     */
    public int dtSetDsChange(long changeTime) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtSetDsChange_proxy(changeTime);
				checkReturn(ret, "dtSetDsChange");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dtSetDsChange");
	}
    
    /**
     *  set day_saving_time offset value
     * @param OffsetTime
     * @return Return the error code
     * @throws TVMException
     */
    public int dtSetDsOffset(long OffsetTime) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtSetDsOffset_proxy(OffsetTime);
				checkReturn(ret, "dtSetDsOffset");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dtSetDsOffset");
	}
    
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
    public int dtSetSyncSrc(int eSyncSrcType, int eSrcDescType,String data) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtSetSyncSrc_proxy(eSyncSrcType, eSrcDescType, data);
				checkReturn(ret, "dtSetSyncSrc");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dtSetSyncSrc");
	}
    
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
    public int dtSetTzCtrl(boolean bEnable) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtSetTzCtrl_proxy(bEnable);
				checkReturn(ret, "dtSetTzCtrl");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dtSetTzCtrl");
	}
    
    /**
     *  Set the system country code. If the received system time table contains
     *               a matching country code, it will be   used to adjust the time zone.
     *
     *  @param [in]  t_count_code  - Contains the system country code.
     *  @param [in]  ui2_region_id  - Contains a system region id.
     *
     *  @return  Return the error code. 
     */
    public int dtSetSysCountCode(byte[] countCode, int regionId) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtSetSysCountCode_proxy(countCode, regionId);
				checkReturn(ret, "dtSetSysCountCode");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dtSetSysCountCode");
	}
    
    /**
     *   Get the current Daylight-Saving-Time.
     *
     *  @param [in] VOID
     *
     *  @return  Return the Dalylight-Saving-Time on/off.
     */
    public boolean dtGetDst() throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dtGetDst_proxy();
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return false;
	}
    
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
    public long dtGetGps(int[] data) throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dtGetGps_proxy(data);
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return 0;
	}
    
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
    public long dtGetTz() throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dtGetTz_proxy();
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return 0;
	}
    
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
    public long dtGetUtc(int[] data) throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dtGetUtc_proxy(data);
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return 0;
	}
    
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
    public long dtGetBrdcstUtc(int[] data) throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dtGetBrdcstUtc_proxy(data);
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return 0;
	}
    
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
    public int dtGetCountCode(int index, byte[] countCode, long[] data) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtGetCountCode_proxy(index, countCode, data);
				checkReturn(ret, "dtGetCountCode");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dtGetCountCode");
	}
    
    /**
     *! Get the flag indicating if the Daylight-Saving-Time should  be applied
     *              for this locality.
     *
     *  @param [in] VOID
     *
     *  @return  Daylight-Saving-Time control.
     */
    public boolean dtGetDstCtrl() throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dtGetDstCtrl_proxy();
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return false;
	}
    
    /**
     *  Get the day_saving_time change value
     * 
     * @param [in] VOID
     * 
     * @return ds_change value
     * 
     * @throws TVMException
     */
    public long dtGetDsChange() throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dtGetDsChange_proxy();
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return 0;
	}
    
    /**
     *  Get the day_saving_time offset value
     * 
     * @param [in] VOID
     * 
     * @return ds_offset value
     * 
     * @throws TVMException
     */
    public long dtGetDsOffset() throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dtGetDsOffset_proxy();
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return 0;
	}
    
    /**
     *!  Get the flag indicating if the time zone parameter received from the 
     *              broadcast table should  be applied for this locality.
     *
     *  @param [in] VOID.
     *
     *  @return Return if use time zone information from broadcast table for this location or not.
     */
    public boolean dtGetTzCtrl() throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dtGetTzCtrl_proxy();
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return false;
	}
    
    /**
     *!  Get the number of country code's signaled in the system  time / date table.
     *
     *  @param [in] VOID
     *
     *  @return   Return the number of individual country code entries.
     */
    public int dtGetNumCountCode() throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dtGetNumCountCode_proxy();
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return 0;
	}
    
    /**
     *!   Get the system country code and region id.
     *
     *  @param [out]   pt_count_code  - Contains the system country code.
     *  @param [out]   pui2_region_id  - Contains the system region id.
     *  
     *  @return  Return the error code.
     */
    public int dtGetSysCountCode(byte[] countCode, int[] data) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtGetSysCountCode_proxy(countCode, data);
				checkReturn(ret, "dtGetSysCountCode");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dtGetSysCountCode");
	}
    
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
    public byte dtGetLastSyncTblId() throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dtGetLastSyncTblId_proxy();
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return 0;
	}
    
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
    public int dtCheckInputTime(boolean bEnable) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtCheckInputTime_proxy(bEnable);
				checkReturn(ret, "dt_utc_sec_to_dtg");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dt_utc_sec_to_dtg");
	}
    
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
    public int dtConfigCheckInputTime(int eSetType, int setValue) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dtConfigCheckInputTime_proxy(eSetType, setValue);
				checkReturn(ret, "dtConfigCheckInputTime");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dtConfigCheckInputTime");
	}

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
	public int dtUtcSecToDtg(long utcTime, DtDTG dtgTime) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dt_utc_sec_to_dtg_proxy(utcTime, dtgTime);
				checkReturn(ret, "dt_utc_sec_to_dtg");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dt_utc_sec_to_dtg");
	}

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
	public int dtUtcSecToLocDtg(long utcTime, DtDTG dtgTime) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dt_utc_sec_to_loc_dtg_proxy(utcTime, dtgTime);
				checkReturn(ret, "dt_utc_sec_to_loc_dtg");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dt_utc_sec_to_loc_dtg");
	}
	
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
	public int dtConvUtcLocal(DtDTG dtgTimeIn, DtDTG dtgTimeOut) throws TVMException {
		int ret = -1;
		
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dt_conv_utc_local_proxy(dtgTimeIn, dtgTimeOut);
				checkReturn(ret, "dt_utc_sec_to_loc_dtg");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dt_utc_sec_to_loc_dtg");
	}
	
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
	public long dtDtgToSec(DtDTG dtgTime) throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dt_dtg_to_sec_proxy(dtgTime);
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 *    Convert GPS seconds to UTC seconds
	 * 
	 * @param gpsSec
	 *            input, Number of seconds since GPS epoch (0000Z, 06 Jan 1980)
	 *            
	 * @return number of seconds since UTC epoch
	 * 
	 */
	public long dtGpsSecToUtcSec(long gpsSec) throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dt_gps_sec_to_utc_sec_proxy(gpsSec);
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 *    This function converts the  24 bits of BCD (binary code decimal)
	 *               for hour, minutes, and seconds into number of seconds.
	 *               
	 * @param bcdTime
	 *            input, A 24 bits BCD (binary code decimal) string for HHMMSS
	 *            
	 * @return Seconds from the BCD HHMMSS
	 */
	public int dtBcdToSec(String bcdTime) throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dt_bcd_to_sec_proxy(bcdTime);
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return -1;
	}
	
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
	public int dtMjdBcdToDtg(String bcdTime, DtDTG dtgTime) throws TVMException {
		int ret = -1;
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dt_mjd_bcd_to_dtg_proxy(bcdTime, dtgTime);
				checkReturn(ret, "dt_mjd_bcd_to_dtg");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dt_mjd_bcd_to_dtg");
	}
	
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
	public int dtMjdToDtg(long mjdTime, DtDTG dtgTime) throws TVMException {
		int ret = -1;
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dt_mjd_to_dtg_proxy(mjdTime, dtgTime);
				checkReturn(ret, "dt_mjd_to_dtg");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dt_mjd_to_dtg");
	}
	
	/**
	 *    This function converts a calendar value to the MJD date.
	 * 
	 * @param dtgTime
	 *            input, a DTG_T structure. only year/month/day count.
	 *            
	 * @return Modified-Julia-Date value.
	 * 
	 */
	public long dtDtgToMjd(DtDTG dtgTime) throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dt_dtg_to_mjd_proxy(dtgTime);
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return -1;
	}
	
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
	public int dtDtgToMjdBcd(DtDTG dtgTime, int[] mjdInfo) throws TVMException {
		int ret = -1;
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret =  service.dt_dtg_to_mjd_bcd_proxy(dtgTime, mjdInfo);
				checkReturn(ret, "dt_dtg_to_mjd_bcd");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dt_dtg_to_mjd_bcd");
	}
	
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
	public long dtDiff(DtDTG dtgTimeFrom, DtDTG dtgTimeTo) throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dt_diff_proxy(dtgTimeFrom, dtgTimeTo);
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return -1;
	}
	
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
	public int dtAdd(DtDTG dtgTimeOld, long addSec, DtDTG dtgTimeNew) throws TVMException {
		int ret = -1;
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dt_add_proxy(dtgTimeOld, addSec, dtgTimeNew);
				checkReturn(ret, "dt_add");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return checkReturn(ret, "dt_add");
	}
	
	/**
	 *     Given a year, determine if it is a leap year.
	 * 
	 * @param year
	 *            input, Specify the year to be evaluated 
	 *            
	 * @return  TRUE:leap-year FALSE:not leap year
	 */
	public boolean dtIsLeapYear(long year) throws TVMException {
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				return service.dt_is_leap_year_proxy(year);
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		return false;
	}
	
	protected static void dtLisenter(int h_handle, int cond, int delta_time){
		DtInternalMapElement internalMapElement = dtInternalMapTable.get(new Long(h_handle));
		if (null != internalMapElement){
	        if (null != internalMapElement.m_DTListener){           
	            internalMapElement.m_DTListener.DtNfyFct(h_handle, cond, delta_time);
	        } 
		}
		else{
			System.out.println("[xiuqin]dtLisenter internalMapElement is null.");
		}
	}
	
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
	public int dtRegNfyFct(DtListener dt_listener, long[] handle) 
	throws TVMException {
		int ret = -1;
		long h_handle = 0xff;
		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null){
				ret = service.dt_reg_nfy_fct_proxy(handle);
				h_handle = handle[0];
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
		
		System.out.println("ok.");
		dtInternalMapTable.put(new Long(h_handle), new DtInternalMapElement(dt_listener));
		
		return ret;
	}

	/**
	 *     This API will lock the digital tuner at desired frequency. It is introduced for
	 *     CI Regional Frequency Control. After tuner is locked, CAM checks the information
	 *     from TS automatically.
	 *     Please note that:
	 *     (1) This API should only be used under DTV source, tuner mode should be Cable;
	 *     (2) This API is a synchronous call, caller would be blocked till the function returns;
	 *     (3) This API will first try to stop the current playing service before locking tuner;
	 *     (4) Once tuner is locked, DO NOT do channel select/scan before unlocking tuner, otherwise
	 *         the behavior is undefined.
	 *              
	 * @param[in] frequency
	 *            input, The frequency to be locked. 
	 * 
	 * 
	 * @return   >0 MagicID, use this ID to unlock tuner; 
	 *           <0 error occurs
	 *           -1: Invalid frequency
	 *           -2: Current source is not DTV with cable
	 *           -3: Tuner is already locked in another frequency
	 *           -4: Tuner cannot be connected
	 *           -100: TVRemoteService is not found
	 *           -101: TVRemoteService exception occurred 
	 *           
	 */
	public int lockDigitalTuner(int frequency) throws TVMException {
		int ret;
		Logger.i(TAG, "lockDigitalTuner at " + frequency + "HZ!");

		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null) {
				ret = service.lockDigitalTuner_proxy(frequency);
				if (ret <= 0) {
					Logger.e(TAG, "lockDigitalTune failed as Internal error: " + ret);
				}
				else {
					Logger.i(TAG, "lockDigitalTuner OK, MagicID is " + ret);
				}
			}
			else {
				Logger.e(TAG, "lockDigitalTuner failed as TVRemoteService is not found.");
				ret = -100;
			}
		} catch(RemoteException e) {
			ret = -101;
			Logger.e(TAG, "lockDigitalTuner failed as TVRemoteService exception occurred.");
			e.printStackTrace();
		}

		return ret;
	}
	
	/**
	 *     This API will unlock the previously locked digital tuner.
	 *              
	 * @param[in] MagicID
	 *            input, MagicID, returned from lockDigitalTuner. 
	 * 
	 * 
	 * @return   0 OK;
	 *           <0 error occurs
	 *           -1: Invalid magic ID
	 *           -2: Tuner cannot be disconnected
	 *           -100: TVRemoteService is not found
	 *           -101: TVRemoteService exception occurred 
	 */
	public int unlockDigitalTuner(int magicID) throws TVMException {
		int ret;
		Logger.i(TAG, "unlockDigitalTuner with MagicID:" + magicID);

		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null) {
				ret = service.unlockDigitalTuner_proxy(magicID);
				if (ret < 0) {
					Logger.e(TAG, "lockDigitalTune failed as Internal error:" + ret);
				}
				else {
					Logger.i(TAG, "unlockDigitalTuner OK.");
				}
			}
			else {
				Logger.e(TAG, "unlockDigitalTuner failed as TVRemoteService is not found.");
				ret = -100;
			}
		} catch(RemoteException e) {
			ret = -101;
			Logger.e(TAG, "lockDigitalTuner failed as TVRemoteService exception occurred.");
			e.printStackTrace();
		}

		return ret;
	}
	
	public class AudioEncodingType {
		/* Same as the AUC_ENC_XXX defined in u_scdb.h */
		public static final int AUD_ENC_UNKNOWN       = 0;    /**< Unknown audio encoding */
		public static final int AUD_ENC_AC3           = 1;    /**< Dolby AC-3 audio coding */
		public static final int AUD_ENC_MPEG_1        = 2;    /**< MPEG-1 (ISO/IEC 11172-3) encoded audio */
		public static final int AUD_ENC_MPEG_2        = 3;    /**< MPEG-2 (ISO/IEC 13818-3) encoded audio */
		public static final int AUD_ENC_PCM           = 4;    /**< PCM audio */
		public static final int AUD_ENC_TV_SYS        = 5;    /**< TV system specific audio type */
		public static final int AUD_ENC_DTS           = 6;    /**< DTS audio coding */
		public static final int AUD_ENC_AAC           = 7;    /**< AAC encoded audio */
		public static final int AUD_ENC_EU_CANAL_PLUS = 8;    /**< Canal+ SCART-out audio */
		public static final int AUD_ENC_WMA_V1        = 9;    /**< WMA V1 encoded audio */
		public static final int AUD_ENC_WMA_V2        = 10;   /**< WMA V2 encoded audio */
		public static final int AUD_ENC_WMA_V3        = 11;   /**< WMA V3 encoded audio */
		public static final int AUD_ENC_E_AC3         = 12;   /**< Enhanced AC-3 encoded audio */
		public static final int AUD_ENC_LPCM          = 13;   /**< Linear PCM audio */
		public static final int AUD_ENC_FM_RADIO      = 14;   /**< FM radio audio */
		public static final int AUD_ENC_COOK          = 15;   /**< Realmedia Cook encoded audio */
		public static final int AUD_ENC_DRA           = 16;   /**< DRA (Dynamic Resolution Adaption) encoded audio */
		public static final int AUD_ENC_VORBIS        = 17;   /**<MKV encoded audio */
		public static final int AUD_ENC_WMA_PRO       = 18;   /**< WMA PRO encoded audio */
		public static final int AUD_ENC_WMA_LOSSLESS  = 19;   /**< WMA LOSSLESS encoded audio */
		public static final int AUD_ENC_AWB           = 20;   /**< AMB encoded audio */
		public static final int AUD_ENC_AMR           = 21;   /**< AMR encoded audio */
	}
	
	public class VideoEncodingType {
		/* Same as the VID_ENC_XXX defined in u_scdb.h */
		public static final int VID_ENC_UNKNOWN       = 0;    /**< Unknown video encoding */
		public static final int VID_ENC_MPEG_1        = 1;    /**< MPEG-1 (ISO/IEC 11172-2) encoded video */
		public static final int VID_ENC_MPEG_2        = 2;    /**< MPEG-2 (ISO/IEC 13818-2) encoded video */
		public static final int VID_ENC_MPEG_4        = 3;    /**< MPEG-4 (ISO/IEC 14496-2) encoded video */
		public static final int VID_ENC_DIVX_311      = 4;    /**< DivX 3.11 encoding */
		public static final int VID_ENC_DIVX_4        = 5;    /**< DivX 4 encoding */
		public static final int VID_ENC_DIVX_5        = 6;    /**< DivX 5 encoding */
		public static final int VID_ENC_XVID          = 7;    /**< Xvid encoding */
		public static final int VID_ENC_WMV1          = 8;    /**< WMV1 (Windows Media Video v7) encoding */
		public static final int VID_ENC_WMV2          = 9;    /**< WMV2 (Windows Media Video v8) encoding */
		public static final int VID_ENC_WMV3          = 10;   /**< WMV3 (Windows Media Video v9) encoding */
		public static final int VID_ENC_WVC1          = 11;   /**< WVC1 (Windows Media Video v9 Advanced Profile) encoding */
		public static final int VID_ENC_H264          = 12;   /**< H.264 (ISO/IEC 14496-10) encoded video */
		public static final int VID_ENC_H263          = 13;   /**< H.263 encoded video */
		public static final int VID_ENC_MJPEG         = 14;   /**< Motion JPEG encoded video */
		public static final int VID_ENC_RV8           = 15;   /**< RealVideo 8 encoding */
		public static final int VID_ENC_RV9           = 16;   /**< RealVideo 9 encoding */
		public static final int VID_ENC_SORENSON      = 17;   /**< Sorenson codec */
		public static final int VID_ENC_AVS           = 18;   /**< AVS (Audio Video Standard) encoded video */
		public static final int VID_ENC_NV12          = 19;   /**< RAW encoding */
		public static final int VID_ENC_VP8           = 20;   /**< VP8 encoded video for MKV */
		public static final int VID_ENC_VP6           = 21;   /**< VP6 encoded video for FLV */
	}
	
	/**
	 *     This API gets the audio encoding type of the current playing dtv program.
	 *              
	 * @param
	 * 
	 * @return   >=0 One of the audio encoding type defined in AudioEncodingType ;
	 *           <0 error occurs
	 *           -1: current source is not DTV
	 *           -2: No audio is playing
	 *           -100: TVRemoteService is not found
	 *           -101: TVRemoteService exception occurred 
	 */
	public int getCurrentDTVAudioCodec() throws TVMException {
		int ret;
		Logger.i(TAG, "getCurrentDTVAudioCodec enter");

		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null) {
				ret = service.getCurrentDTVAudioCodec_proxy();
				if (ret < 0) {
					Logger.e(TAG, "getCurrentDTVAudioCodec failed as Internal error:" + ret);
				}
				else {
					Logger.i(TAG, "getCurrentDTVAudioCodec OK.");
				}
			}
			else {
				Logger.e(TAG, "getCurrentDTVAudioCodec failed as TVRemoteService is not found.");
				ret = -100;
			}
		} catch(RemoteException e) {
			ret = -101;
			Logger.e(TAG, "getCurrentDTVAudioCodec failed as TVRemoteService exception occurred.");
			e.printStackTrace();
		}

		return ret;
	}
	
	/**
	 *     This API gets the audio encoding type of the current playing dtv program.
	 *              
	 * @param
	 * 
	 * @return   >=0 One of the video encoding type defined in VideoEncodingType ;
	 *           <0 error occurs
	 *           -1: current source is not DTV
	 *           -2: No video is playing
	 *           -100: TVRemoteService is not found
	 *           -101: TVRemoteService exception occurred 
	 */
	public int getCurrentDTVVideoCodec() throws TVMException {
		int ret;
		Logger.i(TAG, "getCurrentDTVVideoCodec enter");

		try {
			ITVRemoteService service = TVManager.getRemoteTvService();
			if (service != null) {
				ret = service.getCurrentDTVVideoCodec_proxy();
				if (ret < 0) {
					Logger.e(TAG, "getCurrentDTVVideoCodec failed as Internal error:" + ret);
				}
				else {
					Logger.i(TAG, "getCurrentDTVVideoCodec OK.");
				}
			}
			else {
				Logger.e(TAG, "getCurrentDTVVideoCodec failed as TVRemoteService is not found.");
				ret = -100;
			}
		} catch(RemoteException e) {
			ret = -101;
			Logger.e(TAG, "getCurrentDTVVideoCodec failed as TVRemoteService exception occurred.");
			e.printStackTrace();
		}

		return ret;
	}
	
}
