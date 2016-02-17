package com.tcl.tvmanager;

//import java.sql.Date;
import java.util.List;

import android.content.Context;
//import android.os.Handler;
//import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.os.SystemProperties;
import com.mediatek.tv.common.BaseConfigType;
import com.mediatek.tv.common.ConfigType;
//import com.mediatek.tv.service.ChannelService;
//import com.mediatek.tvcommon.ITVCommon.ChannelFilter;
//import com.mediatek.tvcommon.ITVCommon.ChannelsChangedListener;
//import com.mediatek.tvcommon.ITVCommon.TVSelectorListener;
//import com.tcl.fac.data.Core;
//import com.tcl.fac.data.EnTCLInputSource;
//import com.tcl.fac.function.CommandExecute;
//import com.tcl.tv.util.Util;
//import com.tcl.tvmanager.TTvUtils;
//import com.tcl.tvmanager.vo.EnTCLCallBackSetSourceMsg;
//import com.tcl.tvmanager.vo.EnTCLWindow;
import com.tcl.tvmanager.vo.EnTCLInputSource;
import java.util.ArrayList;
import com.mediatekk.tvcommon.*;

/**
 * TTvCommonManager is simple wrapper of {@link ITVCommon}. APPs can access this
 * methods to operate TV system.
 */


public class TTvCommonManager {
	private static Context mContext;
	private static TTvCommonManager tvMgr;
	public  static EnTCLInputSource m_lastSource ;
	public  static EnTCLInputSource m_tempNowSource ;
	public  static boolean m_ifStorage;
	public  static Context mContent;
   // private static TVInfoToast  tvInfoToast=null ;

    private boolean isNullSrc = false;

    
	private TTvCommonManager(Context context) {
		//tvInfoToast=new TVInfoToast(mContent);
		mContext = context;
	}

	/**
	 * @return the singleton instance of {@link TTvCommonManager}.
	 */
	public static TTvCommonManager getInstance(Context context) {
		if (tvMgr == null) {
			tvMgr = new TTvCommonManager(context);
		//	m_lastSource = getCurrentInputSource();
			m_ifStorage  = false;
		}
		
		m_lastSource = tvMgr.getCurrentInputSource();//huangjian addtest
		mContext = context;
		//mCfg = TVConfigurer.getInstance(mContext);
		return tvMgr;
	}
	

	/**
	 * @see {@link ITVCommon#changeInputSource(String, String)}
	 */
	public static void changeInputSource(String outputName, String inputName) {
		try {
			if(inputName=="storage") 
			{
				m_ifStorage = true;
				return;
			}else{
			  Log.i("zouhc","-------changeInputSource:"+inputName);
			  m_ifStorage = false;
			}
			TVCommonNative.getDefault(mContext).changeInputSource(outputName,	inputName);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}


	/**  CYD
	 * @see {@link ITVCommon#getCurrentInputSource()}
	 */
	public EnTCLInputSource getCurrentInputSource() {
		String temp_str="error";
    /*
		if(m_ifStorage){
       Log.i("zouhc","--------getCurrentInputSource: "+"storage");
       return EnTCLInputSource.EN_TCL_STORAGE;
    }*/
    TVConfigurer mCfg = TVConfigurer.getInstance(mContext);

    if(mCfg.getGroup() == TVConfigurer.CFG_GRP_LEAVE){
         Log.i("zouhc","--------getCurrentInputSource: "+mCfg.getGroup());
       return EnTCLInputSource.EN_TCL_STORAGE;
    }
		try {
			temp_str = TVCommonNative.getDefault(mContext).getCurrentInputSource();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		Log.i("huangjian"," ~~~~~~~ 888 test  getCurrentInputSource ++++++ temp_str:" + temp_str);
		
		if(temp_str.charAt(0)== 'd' || temp_str.charAt(0)== 'D') {
			return EnTCLInputSource.EN_TCL_DTV;
		}
		else if(temp_str.charAt(0)== 'a' || temp_str.charAt(0)== 'A') {
			if(temp_str.equals("atv") || temp_str.equals("ATV")) {
				return EnTCLInputSource.EN_TCL_ATV;
			}
			else if(temp_str.equals("av0") || temp_str.equals("AV0")) {
				return EnTCLInputSource.EN_TCL_AV1;
			}
			else
				return EnTCLInputSource.EN_TCL_AV2;
		}
		//vga0
		else if(temp_str.charAt(0)== 'v' || temp_str.charAt(0)== 'V') {
				return EnTCLInputSource.EN_TCL_VGA;
		}//storage
		else if(temp_str.charAt(0)== 's' || temp_str.charAt(0)== 'S') {
			return EnTCLInputSource.EN_TCL_STORAGE;
		}
		else if(temp_str.charAt(0) == 'h' || temp_str.charAt(0) == 'H')
		{
			if(temp_str.equals("HDMI0") || temp_str.equals("hdmi0")) {
				return EnTCLInputSource.EN_TCL_HDMI2;
			}
			else{
				return EnTCLInputSource.EN_TCL_HDMI1;
      }
		}
        Log.i("zouhc","------getCurrentInputSource: "+temp_str);
       return EnTCLInputSource.EN_TCL_YPBPR;
	}

	
	public static void setInputSource(String str) {

		//sourceDisplay(str);
		if (str.equals("hdmi0"))
			str = "hdmi1";
		else if (str.equals("hdmi1"))
			str = "hdmi0";	
		changeInputSource("main", str);
	}




	public static void setInputSource(EnTCLInputSource source) {
		String src_str=null;
		m_lastSource = source;
		switch(source) 
		{
			case EN_TCL_ATV :
				src_str="atv";
				break;
			case EN_TCL_DTV :
				src_str="dtv";
				break;
			case EN_TCL_AV1 :
				src_str="av0";
				break;
			case EN_TCL_AV2 :
				src_str="av1";
				break;
			case EN_TCL_HDMI1:
				src_str="hdmi0";
				break;
			case EN_TCL_HDMI2:
				src_str="hdmi1";
				break;
			case EN_TCL_YPBPR:
				src_str="component0";
				break;
			case EN_TCL_VGA:
				src_str="vga0";
				break;
			case EN_TCL_STORAGE:
				leaveTV(mContext);
				src_str="storage";
				Log.i("huangjian +++++ 999999 setInputSource cyd ","src_str"+src_str);
				setInputSource(src_str);
				return;
			default:
				{
					src_str="atv";
					m_lastSource = EnTCLInputSource.EN_TCL_ATV;
					Log.i("cyd getCurrentInputSource","m_lastSource=" +m_lastSource);
					Log.i("cyd setInputSource","++src_str=" +src_str);
					setInputSource(src_str);
					break;
				}
		}
		Log.i("cyd setInputSource","src_str=" +src_str);
		enterTV(mContext);
		setInputSource(src_str);
		//if(m_tempNowSource == getCurrentInputSource()) 
	}
    /*modify by zouhc@tcl*/
  public EnTCLInputSource getLastestSavedSource() {

    String temp_str="error";
    try {
			temp_str = TVCommonNative.getDefault(mContext).getCurrentInputSource();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		    Log.i("zouhc","------getLastestSavedSource: "+temp_str);
		//Log.i("zouhc","getLastestSavedSource ++++++ temp_str:" + temp_str);
		
		if(temp_str.charAt(0)== 'd' || temp_str.charAt(0)== 'D') {
			return EnTCLInputSource.EN_TCL_DTV;
		}
		else if(temp_str.charAt(0)== 'a' || temp_str.charAt(0)== 'A') {
			if(temp_str.equals("atv") || temp_str.equals("ATV")) {
				return EnTCLInputSource.EN_TCL_ATV;
			}
			else if(temp_str.equals("av0") || temp_str.equals("AV0")) {
				return EnTCLInputSource.EN_TCL_AV1;
			}
			else
				return EnTCLInputSource.EN_TCL_AV2;
		}
		//vga0
		else if(temp_str.charAt(0)== 'v' || temp_str.charAt(0)== 'V') {
				return EnTCLInputSource.EN_TCL_VGA;
		}//storage
		else if(temp_str.charAt(0) == 'h' || temp_str.charAt(0) == 'H')
		{
			if(temp_str.equals("HDMI0") || temp_str.equals("hdmi0")) {
				return EnTCLInputSource.EN_TCL_HDMI2;
			}
			else{
				return EnTCLInputSource.EN_TCL_HDMI1;
      }
		}

    return EnTCLInputSource.EN_TCL_YPBPR;
    //   return getCurrentInputSource();
    
    //return m_lastSource;
    
  }

  public void setInputSource(EnTCLInputSource source,boolean asLastestSavedSource) {
    if(asLastestSavedSource == true)
    {
      m_tempNowSource = source;
      m_lastSource = getCurrentInputSource();
    }
    setInputSource(source);
  }
  
  	/**
	 * @see {@link ITVCommon#getChannels()}
	 */
	public List<TVChannel> getChannels() {
		try {
			return TVCommonNative.getDefault(mContext).getChannels();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
		/**
	 * @see {@link ITVCommon#select(int)}
	 */
	public boolean select(int num) {
		try {
			return TVCommonNative.getDefault(mContext).select(num);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}
  /**
   * @see {@link ITVCommon#schedulePowerOff(delay)} add by zouhc@tcl.com
   */
  public void powerOff(long delay) {
    try {
      TVCommonNative.getDefault(mContext).schedulePowerOff(delay);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }
	
	/**
	 * @see {@link ITVCommon#getTunnerMode()}
	 */
	public int getTunnerModeForDig() {
		try {
			return TVCommonNative.getDefault(mContext).getTunnerMode();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return 0;
	}	
	
	public int getCurrentChannelNum() {
		try {
			//return TVCommonNative.getDefault(mContext).getCurrentChannelNum();
			//if (isSourceDTVForMatrix()) {
				//return TCLMatrixChannel.getInstance(mContext).getPlayingChNumId();
			//} else {
				return TVCommonNative.getDefault(mContext).getCurrentChannelNum();
			//}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return -1;
	}	
	
  public boolean isSourceDTVForMatrix() {
		TVConfigurer cfger = TVConfigurer.getInstance(mContext);
		ITVCommon itv = TVCommonNative.getDefault(mContext);
		String currentInput = null;
		
		try {
//			Log.i(TAG, "isNullSrc = "+isNullSrc);
			if(itv == null || cfger == null || isNullSrc)
				return false;
			currentInput = itv.getCurrentInputSource();
//			Log.i(TAG,"isSourceDTVForMatrix currentInput:"+currentInput);
			if (currentInput != null && currentInput.equals("dtv")) {
				int tunnerMode = getTunnerModeForDig();
				
				if(SystemProperties.get("ro.IPTV_DEV_ID").equals("TCL-CN-MT55CD-F3700A")
						||SystemProperties.get("ro.IPTV_DEV_ID").equals("TCL-CN-MT55CD-F3700A-G")
						||SystemProperties.get("ro.IPTV_DEV_ID").equals("TCL-CN-MT55CD-F2890A")){
					Log.i("huangjian","4444 &&&&&&&  clienttype:"+SystemProperties.get("ro.IPTV_DEV_ID"));
					return false;
				}
					
				if (tunnerMode == ConfigType.BS_SRC_CABLE){
					SystemProperties.set("matrix.source_dvbc", "true");
					return true;
				}
			}
		
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
		/**
	 * @see {@link ITVCommon#getCurrentChannel()}
	 */
	public TVChannel getCurrentChannel() {
		try {
			//if (isSourceDTVForMatrix()) {
				//return TCLMatrixChannel.getInstance(mContext).getTVChannelByChannelNum(getCurrentChannelNum());
			//} else {
				return TVCommonNative.getDefault(mContext).getCurrentChannel();
			//}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
	
  public static void leaveTV(Context context){
         try {
             TVMethods tvContent = TVMethods.getInstance(context);
             TVInputCommon mTvInputManager = TVInputCommon.getInstance(context);
             TVOutputCommon output = TVOutputCommon.getInstance(context);
             mTvInputManager.stopDesignateOutput(output.getDefaultOutput(), true);
             tvContent.leaveTV();
         } catch (Exception e) {
             e.printStackTrace();
         }
  }
  public static void enterTV(Context context){
         Log.i("cyd++", "enterTV====================");
         try {
        TVOutputCommon output = TVOutputCommon.getInstance(context);
        TVInputCommon mTvInputManager = TVInputCommon.getInstance(context);
        String str = output.enterOutputMode(mTvInputManager.OUTPUT_MODE_NORMAL);
        TVMethods tvMethods = TVMethods.getInstance(context);
        tvMethods.enterTV();
        if(output.getInstanceVideoResolution("main").getVideoFramerate() <= 0) {
          Log.i("enterTV", "connect====================");
          output.connect(output.getDefaultOutput(),output.getInput(str));
        }
        
        if(!is3DMode(context)){
          TVConfigurer mCfg = TVConfigurer.getInstance(context);
            @SuppressWarnings("unchecked")
            TVOption<Integer> mTVOption = (TVOption<Integer>) mCfg
              .getOption(BaseConfigType.CFG_PICTURE_MODE);
            mTVOption.set(mTVOption.get()); 
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
  }
  public static boolean is3DMode(Context context) {
    boolean bRet = false;
    boolean[] subTypeStates = new boolean[11];

  //  TVContent mTvContent = TVContent.getInstance(context, true);
    //TVConfigurer mCfg = mTvContent.getConfigurer();
    TVConfigurer mCfg = TVConfigurer.getInstance(context);
    TVOption<Integer> mTVOption3DMode = (TVOption<Integer>) mCfg
        .getOption(BaseConfigType.CFG_3D_MODE);

    int Mode = mTVOption3DMode.get();

    try {
      subTypeStates = mCfg.get3DModeState();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    Log.i("====3D mode====", "get3DMode return " + Mode);
    switch (Mode) {
    case 0:
      bRet = false;
      break;
    case 1:
      if (subTypeStates != null) {
        if (subTypeStates[ConfigType.CFG_3D_MODE_AUTO])
          bRet = true;
        else
          bRet = false;
        Log.i("Util", "====3D automode===="
            + subTypeStates[ConfigType.CFG_3D_MODE_AUTO]);
      }
      break;
    default:
      bRet = true;
      break;
    }

    return bRet;
  }

}
