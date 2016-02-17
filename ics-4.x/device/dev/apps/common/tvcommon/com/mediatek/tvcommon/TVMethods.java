package com.mediatek.tvcommon;

import java.io.IOException;

import android.content.Context;
import android.os.RemoteException;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.service.BroadcastService;
import com.mediatek.tv.service.InputService;
import com.mediatek.tv.service.OSDService;
import com.mediatek.tv.service.TVRemoteService;

public class TVMethods {
	static private TVMethods instance;
	TVManager tvMngr;
	static Context mContext;

	TVMethods(Context context) {
		mContext = context.getApplicationContext();
		tvMngr = TVManager.getInstance(mContext);
	}

	public static TVMethods getInstance(Context context) {
		if (instance == null) {
			instance = new TVMethods(context);
		}

		return instance;
	}

	public boolean isMute() {
		boolean mute = false;
		BroadcastService srv = (BroadcastService) tvMngr
				.getService(BroadcastService.BrdcstServiceName);
		try {
			if(srv != null)
				mute = srv.getMute();
		} catch (TVMException e) {
			e.printStackTrace();
		}
		return mute;
	}

	public void setAudioMute(boolean mute) {
		BroadcastService srv = (BroadcastService) tvMngr
				.getService(BroadcastService.BrdcstServiceName);
		try {
			if (srv != null)
			srv.setMute(mute);
		} catch (TVMException e) {
			e.printStackTrace();
		}
	}

	public boolean setColorKey(boolean b_enabled, int color) {
		OSDService srv = (OSDService) tvMngr
				.getService(OSDService.OSDServiceName);
		if (srv != null)
			return srv.setColorKey(b_enabled, color);
		else
			return false;
	}

	public boolean setOpacity(int opacity) {
		OSDService srv = (OSDService) tvMngr
				.getService(OSDService.OSDServiceName);
		if (srv != null)
			return srv.setOpacity(opacity);
		else
			return false;
	}

	public void manualStop() {
		BroadcastService brdSrv = (BroadcastService) tvMngr
				.getService(BroadcastService.BrdcstServiceName);
		try {
			if(brdSrv != null)
				brdSrv.syncStopService();
		} catch (TVMException e) {
			e.printStackTrace();
		}
	}

	/**
	 * stop video for TV sources.
	 */
	public void tvVideoStop() {
		BroadcastService brdSrv = (BroadcastService) tvMngr
				.getService(BroadcastService.BrdcstServiceName);
		try {
			if(brdSrv != null)
				brdSrv.syncStopVideoStream();
		} catch (TVMException e) {
			e.printStackTrace();
		}
	}

	public static final String inputTypes[] = { InputService.INPUT_TYPE_TV,
			InputService.INPUT_TYPE_AV, InputService.INPUT_TYPE_SVIDEO,
			InputService.INPUT_TYPE_COMPONENT,
			InputService.INPUT_TYPE_COMPOSITE, InputService.INPUT_TYPE_HDMI,
			InputService.INPUT_TYPE_VGA };

	protected static final int cfgGrpForType[] = { TVConfigurer.CFG_GRP_ATV,
			TVConfigurer.CFG_GRP_AV, TVConfigurer.CFG_GRP_AV,
			TVConfigurer.CFG_GRP_COMPONENT, TVConfigurer.CFG_GRP_AV,
			TVConfigurer.CFG_GRP_HDMI, TVConfigurer.CFG_GRP_VGA };

	/**
	 * These APIs help AP handle some dirty things when AP leaves or enters
	 * active mode...
	 */
	public void enterTV() {
		TVConfigurer cfger = TVConfigurer.getInstance(mContext);
		ITVCommon itv = TVCommonNative.getDefault(mContext);
		String currentInput = null;
		String cInputType = null;
		int cfgIndex = 0;

		try {
			if(itv == null || cfger == null)
				return;
			currentInput = itv.getCurrentInputSource();
			if (currentInput != null) {
				cInputType = itv.getInputSourceType(currentInput);
				for (int i = 0; i < inputTypes.length; i++) {
					if (cInputType.equals(inputTypes[i])) {
						if (cInputType.equals(InputService.INPUT_TYPE_TV)) {
							cfgIndex = currentInput.equals("atv") ? TVConfigurer.CFG_GRP_ATV
									: TVConfigurer.CFG_GRP_DTV;
						} else {
							cfgIndex = cfgGrpForType[i];
						}
						cfger.setGroup(cfgIndex);
                        TVCommonNative.TVLog("Set Configure Group to "
                                + TVConfigurer.CFG_GRP_NAMES[cfgIndex]);
						break;
					}
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void leaveTV() {
		TVConfigurer cfger = TVConfigurer.getInstance(mContext);
		if(cfger != null)
			cfger.setGroup(TVConfigurer.CFG_GRP_LEAVE);
        TVCommonNative.TVLog("Set Configure Group to mmp");
	}

	/**
	 * In order to improve the performance of file system, it do not add 'sync'
	 * option when mount UBIFS file system. Now UBIFS work on write-back, which
	 * means that file changes do not go to the flash media straight away, but
	 * they are cached and go to the flash later, when it is absolutely
	 * necessary. Invoke this method when you need to flash data to media
	 * immediately.
	 */
	public void flushMedia() {
		try {
			Runtime.getRuntime().exec("sync");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set video as blue mute
	 * 
	 * @param isMute
	 *            , set blue mute if it is true
	 */
	public void setVideoBlueMute() {
		BroadcastService brdSrv = (BroadcastService) tvMngr
				.getService(BroadcastService.BrdcstServiceName);
		ITVCommon itv = TVCommonNative.getDefault(mContext);
		TVConfigurer cf = TVConfigurer.getInstance(mContext);
		@SuppressWarnings("unchecked")
		TVOptionRange<Integer> blueOpt = (TVOptionRange<Integer>) cf
				.getOption(ConfigType.CFG_BLUE_SCREEN);

		try {
			if(itv != null){
			TVChannel ch;
			ch = itv.getCurrentChannel();
			if (ch != null && null != brdSrv){
				if(blueOpt.get() == ConfigType.COMMON_ON){
					brdSrv.setVideoBlueMute(true, ch.isBlocked());
				}else {
					brdSrv.setVideoBlueMute(false, ch.isBlocked());
				}
			}
		 }
		} catch (TVMException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public void setVideoBlueMute(boolean isMute) {
		BroadcastService brdSrv = (BroadcastService) tvMngr
				.getService(BroadcastService.BrdcstServiceName);
		try {
			if(brdSrv != null)
				brdSrv.setVideoBlueMute(isMute);
		} catch (TVMException e) {
			e.printStackTrace();
		}
	}

	public long getBroadcastUTC() {
		BroadcastService rawSrv = (BroadcastService) TVManager.getInstance(
				mContext).getService(BroadcastService.BrdcstServiceName);
		// Sorry for this arugments,
		// TVManager told me that these are 'output'
		// arguments. I don't need to init them.
		int[] args = new int[2];
		try {
			// That is in second...
			if(rawSrv != null)
				return rawSrv.dtGetBrdcstUtc(args) * 1000;
		} catch (TVMException e) {
			e.printStackTrace();
		}
		return System.currentTimeMillis();
	}

	public long getBroadcastTZOffset() {
		BroadcastService rawSrv = (BroadcastService) TVManager.getInstance(
				mContext).getService(BroadcastService.BrdcstServiceName);
		try {
			// That is in second...
			if(rawSrv != null)
				return rawSrv.dtGetTz() * 1000;
		} catch (TVMException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean isFreeze() {
		boolean freeze = false;

		BroadcastService brdSrv = null;
		brdSrv = (BroadcastService) tvMngr
				.getService(BroadcastService.BrdcstServiceName);

		if (brdSrv != null) {
			try {
				/*
				 *ITVCommon not give the interface to get current output,
				 *so, I just use "main" as default output.
 				*/
				freeze = brdSrv.isFreeze("main");
			} catch (TVMException e) {
				e.printStackTrace();
			}
		}

		return freeze;
	}

	public void setFreeze(boolean isFreeze) {
		BroadcastService brdSrv = null;
		brdSrv = (BroadcastService) tvMngr
				.getService(BroadcastService.BrdcstServiceName);
		if (brdSrv != null) {
			try {
				brdSrv.setFreeze("main", isFreeze);
			} catch (TVMException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setFreeze(String outputName, boolean isFreeze) {
		BroadcastService brdSrv = null;
		brdSrv = (BroadcastService) tvMngr
				.getService(BroadcastService.BrdcstServiceName);
		if (brdSrv != null) {
			try {
				brdSrv.setFreeze(outputName, isFreeze);
			} catch (TVMException e) {
				e.printStackTrace();
			}
		}
	}
}
