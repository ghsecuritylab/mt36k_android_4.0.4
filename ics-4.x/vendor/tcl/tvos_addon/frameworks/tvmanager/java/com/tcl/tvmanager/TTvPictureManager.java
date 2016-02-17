package com.tcl.tvmanager;
//package com.tcl.fac.function;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.math.*; 

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.tclwidget.TCLToast;
import android.util.Log;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.Display;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.BaseConfigType;
//import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.common.ConfigValue;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.service.ConfigService;
import com.mediatekk.tvcommon.TVConfigurer;
import com.mediatek.tv.service.InputService;
import com.mediatekk.tvcommon.TVOption;
import com.mediatekk.tvcommon.TVOptionRange;
//import com.mediatek.tvcommon.TvChDBHelper;
//import com.mediatek.tvcommon.TCLMatrixDtvService;
import com.tcl.devicemanager.ConfigType;

import com.tcl.fac.data.addon.Core;
//import com.tcl.fac.function.*;
import com.tcl.tvmanager.vo.*;


public class TTvPictureManager {

	private static final String TAG = " TTvPictureManager";	
	private static TTvPictureManager m_TvPicture;
	//	private static ConfigValue value = new ConfigValue();
	//	public static ConfigService configService;
	//	private static TVOption<Integer> mTVOptionBackLight = null;
	private InputService inpSrv = null;
	public static TVConfigurer mCfg = null;
	TVManager tvMngr;

	private static  ConfigService m_configService = null;
	private static ConfigValue m_value = null;
	public static Context mContext = null;

	TTvPictureManager(Context context) {
		mCfg = TVConfigurer.getInstance(context);
		mContext = context;
		tvMngr = TVManager.getInstance(null);
		inpSrv = (InputService) tvMngr
			.getService(InputService.InputServiceName);
		m_configService = (ConfigService) tvMngr
			.getService(ConfigService.ConfigServiceName);
		m_value = new ConfigValue();
	}
	public static void configServiceSetValue(String  ConfigTypeString,int status) {
		TVManager tvManager = TVManager.getInstance(null);
		ConfigService configService = (ConfigService) tvManager
			.getService(ConfigService.ConfigServiceName);
		ConfigValue value = new ConfigValue();
		value.setIntValue(status);
		try {
			configService.setCfg(ConfigTypeString, value);
		} catch (TVMException e) {
			e.printStackTrace();
		}
	}

	public static boolean drvset(String dInterfaceType, int val) {
		TVManager tvManager = TVManager.getInstance(null);
		ConfigService configService = (ConfigService) tvManager
			.getService(ConfigService.ConfigServiceName);

		ConfigValue configValue = new ConfigValue();
		int[] value = new int[3];
		configValue.setIntArrayValue(value);
		value[0] = ConfigType.D_INTERFACE_OP_SET_NORMAL;
		value[1] = val;
		value[2] = 1;

		try {
			configService.setCfg(dInterfaceType, configValue);
			System.out.println("set value#####" + value[1]);
			return true;
		} catch (TVMException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void setDBCEn(int value) {
		configServiceSetValue(ConfigType.CFG_FAC_DBC_EN, value);
		drvset(ConfigType.CFG_FAC_DBC_EN_FUNC, value);
	}
	public boolean sendMsgToDBC(EnTCLMsgToDBC i) {
		try{
		onNLSwitchOff();
		refreshUIBkLight();
                }catch(Exception e){
			e.printStackTrace();
		}

		if(i == EnTCLMsgToDBC.EN_TCL_DBC_TV) {
			setDBCEn(EnTCLMsgToDBC.EN_TCL_DBC_TV.ordinal());
		}else if(i == EnTCLMsgToDBC.EN_TCL_DBC_GRAPHIC) {
			setDBCEn(EnTCLMsgToDBC.EN_TCL_DBC_GRAPHIC.ordinal());
		}
		return true;
	}
	public static TTvPictureManager getInstance(Context context){
		if(m_TvPicture == null)
			m_TvPicture = new TTvPictureManager(context);
		return m_TvPicture;
	}

	public static  int getGraphicBacklight()  {
		//return getStdBacklight();
		//return mTVOptionBackLight.get();
		int picBkLight=0;
		ConfigValue value = new ConfigValue();
		try {
			value = m_configService.getCfg(ConfigType.CFG_VID_UIBACKLIGHT);//jane
			picBkLight=value.getIntValue();

		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return picBkLight;
	}
	//背光
	public static int getStdBacklight() {
		return configServiceGetValue(ConfigType.CFG_FAC_STD_BACKLIGHT);
	}

	public static int  configServiceGetValue(String  ConfigTypeString) {
		TVManager tvManager = TVManager.getInstance(null);
		ConfigService configService = (ConfigService) tvManager
			.getService(ConfigService.ConfigServiceName);
		ConfigValue value = null;
		try {
			value = configService.getCfg(ConfigTypeString);
		} catch (TVMException e) {
			e.printStackTrace();
		}
		return value.getIntValue();
	}

	public static  void setGraphicBacklight(short picValue) {
		Log.i("cyd", "+++++++setGraphicBacklight++++ enter");
		try {
			m_value.setIntValue((int) picValue);
			m_configService.setCfg(ConfigType.CFG_VID_UIBACKLIGHT, m_value);//jane
			m_configService.updateCfg(ConfigType.CFG_VID_UIBACKLIGHT);
		} catch (TVMException e) {
			Log.i("cyd", "+++++++setGraphicBacklight++++ error");
		}
	}

	public static void setStdBacklight(int value) {
		configServiceSetValue(ConfigType.CFG_FAC_STD_BACKLIGHT, value);
		drvset(ConfigType.CFG_FAC_STD_BACKLIGHT_FUNC, value);
	}

	public static void setPictureMode(int mode) {
		@SuppressWarnings("unchecked")
			TVOption<Integer> mTVOptionPictureMode = (TVOption<Integer>) mCfg
			.getOption(ConfigType.CFG_PICTURE_MODE);
		switch (mode) {
			case Core.PM_NORMAL:
				mTVOptionPictureMode.set(BaseConfigType.PICTURE_MODE_SPORT);
				break;
			case Core.PM_MILD:
				mTVOptionPictureMode.set(BaseConfigType.PICTURE_MODE_CINEMA);
				break;
				//		case Core.PM_DYNAMIC:
				//			mTVOptionPictureMode.set(BaseConfigType.PICTURE_MODE_DYNAMIC);
				//			break;
			case Core.PM_USER:
				mTVOptionPictureMode.set(BaseConfigType.PICTURE_MODE_USER);
				break;
			case Core.PM_VIVID:
				mTVOptionPictureMode.set(BaseConfigType.PICTURE_MODE_VIVID);
				break;
		}
	}

	public /*static*/ int getPictureMode(){
		@SuppressWarnings("unchecked")
			TVOption<Integer> mTVOptionPictureMode = (TVOption<Integer>) mCfg
			.getOption(ConfigType.CFG_PICTURE_MODE);
		int mode = mTVOptionPictureMode.get();

		switch(mode){
			case BaseConfigType.PICTURE_MODE_SPORT: 
				mode = Core.PM_NORMAL;
				break;
			case BaseConfigType.PICTURE_MODE_CINEMA:
				mode = Core.PM_MILD;
				break;
			case BaseConfigType.PICTURE_MODE_USER:
				mode = Core.PM_USER;
				break;
			case BaseConfigType.PICTURE_MODE_VIVID:
				mode = Core.PM_VIVID;
				break;
			default:
				mode = Core.PM_NORMAL;
				break;
		}
		return mode;
	}

	public void setBacklight(int value) {
		setStdBacklight(value);
	}

	public int getBacklight(){
		return getStdBacklight();
	}

	public void setScreenPosition(String output, float x, float y, float w, float h) {
		//Log.i("TVOutputCommon","output = "+output+",x="+x+",y="+y+",w="+w+",h="+h);
		TTvCommonManager tvcommon = TTvCommonManager.getInstance(null);

		BigDecimal bX = new BigDecimal(x);
		x = bX.setScale(2, BigDecimal.ROUND_DOWN).floatValue(); 
		BigDecimal bY = new BigDecimal(y);
		y = bY.setScale(2, BigDecimal.ROUND_DOWN).floatValue(); 
		BigDecimal bW = new BigDecimal(w);
		w = bW.setScale(2, BigDecimal.ROUND_UP).floatValue(); 
		BigDecimal bH = new BigDecimal(h);
		h = bH.setScale(2, BigDecimal.ROUND_UP).floatValue(); 

		//		Log.i("TVOutputCommon","setScreenPosition!!!src="+getInput("main")+", is source dtv "+tvcommon.isSourceDTVForMatrix());
		Rect r = new Rect((int) (x * 10000.0f), (int) (y * 10000.0f),
				(int) (w * 10000.0f), (int) (h * 10000.0f));
		inpSrv.setScreenOutputRect(output, r);
		Log.i(TAG, "--- enter inpSrv.setScreenOutputRect(output, r);");
	}

	public void scaleVideoWindow(EnTCLWindow i, VideoWindowRect windowRect){

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowMgr = (WindowManager)mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		windowMgr.getDefaultDisplay().getMetrics(dm);
		float W = dm.widthPixels;
		float H = dm.heightPixels;        

		Log.i(TAG, "Width = " + W);
		Log.i(TAG, "Height = " + H);

		Log.i(TAG, "--- scaleVideoWindow " + windowRect.x + "," + windowRect.y + "," + windowRect.width + "," + windowRect.height);
		float x = windowRect.x/W;
		float y = windowRect.y/H;
		float w = (windowRect.x + windowRect.width)/W;
		float h = (windowRect.y+windowRect.height)/H;
		//setScreenPosition("main", windowRect.x, windowRect.y, windowRect.width, windowRect.height);
		setScreenPosition("main", x, y, w, h);
	}

	public PanelProperty getPanelWidthHeight(){
		PanelProperty panelProperty_tcl = new PanelProperty();

		panelProperty_tcl.width  = 1920;
		panelProperty_tcl.height = 1080;

		return panelProperty_tcl;
	}
	public void onNLSwitchOff() throws RemoteException {

		// TODO Auto-generated method stub
		Log.e(TAG, "Off nature light");
		try {
			final int NL_SWITCH_OFF = 10;
			TVManager tvManager = TVManager.getInstance(null);
			ConfigService configService = (ConfigService)tvManager.getService(ConfigService.ConfigServiceName);
			ConfigValue configValue = new ConfigValue();
			int[] value = new int[3];
			configValue.setIntArrayValue(value);
			value[0] = BaseConfigType.D_INTERFACE_OP_SET_NORMAL;
			value[1] = NL_SWITCH_OFF;
			value[2] = 1;
			configService.setCfg(ConfigType.CFG_NATURELIGHT_FUNC,configValue);
		} catch (Exception e) {
			Log.e(TAG, "Off nature light exception:" + e.toString());
		}
	}



	public void refreshUIBkLight() throws RemoteException {
		// TODO Auto-generated method stub
		try {
			TVManager tvMng = TVManager.getInstance(null);
			ConfigService configService = (ConfigService)tvMng.getService(ConfigService.ConfigServiceName);
			ConfigValue value = new ConfigValue();
			value = configService.getCfg(ConfigType.CFG_VID_UIBACKLIGHT);
			configService.setCfg(ConfigType.CFG_VID_UIBACKLIGHT, value);
		} catch (Exception e) {

		}
	}

}
