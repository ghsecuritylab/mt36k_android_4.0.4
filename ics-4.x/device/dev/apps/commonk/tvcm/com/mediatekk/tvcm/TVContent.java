package com.mediatekk.tvcm;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.mediatek.gtv.ITVCommon;
import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.CompListener;
import com.mediatek.tv.model.SubtitleInfo;
import com.mediatek.tv.service.BroadcastService;
import com.mediatek.tv.service.ComponentService;
import com.mediatek.tv.service.ConfigService;
import com.mediatek.tv.service.InputService;
import com.mediatek.tv.service.OSDService;
import com.mediatekk.tvcm.TVChannelSelector.DelegaterBlocked;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Main entry for TV common logic. If customer implementation needs extend
 * components of TV Common logic, it should extend this class first. Factory
 * methods should be implemented in subclass of TVContent if new there is new
 * component implementation.
 * 
 * 
 * 
 * @author mtk40063
 * 
 */
public class TVContent extends TVComponent {

	private List<TVComponent> comps = new LinkedList<TVComponent>();

	private static TVContent inst = null;

	private TVCustomer customer;

	static private boolean silent = false;

	TVCustomer getCustomer() {
		return customer;
	}

	protected TVContent(Context context) {
		// Build components and do init.
		super(context);

		// this.context = context.getApplicationContext();
		this.customer = null;

		addOption(OPTION_ASPECT_RATIO, new AspectOption());

	}

	@SuppressWarnings("unchecked")
	static Class<TVCustomer> findCustomerClass() {
		try {
			Class<TVCustomer> customerCls = (Class<TVCustomer>) Class
					.forName("com.mediatek.tvcm.TVCustomerImpl");
			return customerCls;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return TVCustomer.class;
	}

	public static synchronized TVContent getInstance(Context context) {
		if (inst == null) {
			Class<TVCustomer> customerCls = findCustomerClass();
			TvLog("Customer class is " + customerCls);
			try {
				Class<?> types[] = new Class<?>[1];
				types[0] = Context.class;
				Method mtd = customerCls.getDeclaredMethod("createContent",
						types);
				inst = (TVContent) mtd.invoke(null, context);
				TvLog("TV Content is created " + inst);
			} catch (Exception e) {
				e.printStackTrace();
			}
			inst.init();
		}
		return inst;
	}

	public static synchronized TVContent getInstance(Context context,
			boolean isSilent) {
		silent = isSilent;
		return TVContent.getInstance(context);
	}

	// final static String FIELD_START_CHANNEL = "start_channel";
	// final static String FIELD_START_INP = "start_input";

	protected void init() {

		// TVContent should load settings from TVStorage and
		// set to components.
		// NOTE, we don't do this loading in each components, because
		// a strict order must be specified here.
		// TODO: User defined behavior should be also defined here.

		// Find customer class and create customer
		Class<TVCustomer> customerCls = findCustomerClass();
		Class<?> types[] = new Class<?>[1];
		types[0] = Context.class;
		Constructor<TVCustomer> ctor;
		try {
			ctor = customerCls.getDeclaredConstructor(types);
			customer = ctor.newInstance(context);
			customer.init();
			TvLog("TV Customer is created " + customer);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!silent) {
		sendUpdate();
		}

		// 3.Power
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SHUTDOWN);
		filter.setPriority(IntentFilter.SYSTEM_LOW_PRIORITY);
		context.registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				uninit();
			}
		}, filter);
	}

	void uninit() {
		TvLog("Shutting down TV .... ");
		TVScanner sc = getScanner();
		// End scanning
		if (sc.getState() == sc.STATE_SCANNING) {
			TvLog("Canceling scanning, for power off");
			sc.cancelScan();
		}
		// This is required by TV manager to save eeprom or do some
		// magic things....
		ConfigService cfgSrv = (ConfigService) getTVMngr().getService(
				ConfigService.ConfigServiceName);
		try {
			cfgSrv.powerOff();
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TVChannelManager cm = getChannelManager();
		cm.flush();
		TVStorage st = getStorage();
		st.flush();

		// TODO: Call tv manager's shutdown./

		TvLog("END TV");
	}

	private static TVScanner scanner = null;

	/**
	 * @return The scanner component
	 */
	public synchronized TVScanner getScanner() {
		if (scanner == null) {
			scanner = createScanner();
			scanner.init();
		}
		return scanner;
		// return TVScanner.getInstance(context);
	}

	protected TVScanner createScanner() {
		return new TVScanner(context);
	}

	private static TVChannelManager chnMngr = null;

	/**
	 * @return The channel manager component
	 */
	public synchronized TVChannelManager getChannelManager() {
		if (chnMngr == null) {
			chnMngr = createChannelManager();
			chnMngr.init();
		}
		return chnMngr;
	}

	protected TVChannelManager createChannelManager() {
		return new TVChannelManager(context);
	}

	private static TVConfigurer defCfg = null;

	/**
	 * @return The configurer component
	 */
	public synchronized TVConfigurer getConfigurer() {
		if (defCfg == null) {
			defCfg = createConfigurer();
			defCfg.init();
		}
		return defCfg;
	}

	protected TVConfigurer createConfigurer() {
		return new TVConfigurer(context);
	}

	Hashtable<String, TVConfigurer> cfgs = new Hashtable<String, TVConfigurer>();

	public synchronized TVConfigurer getConfigurerForInput(String inputSrc) {
		TVConfigurer cfg = cfgs.get(inputSrc);
		if (cfg == null) {
			cfg = createConfigurerForInput(inputSrc);
			cfgs.put(inputSrc, cfg);
		}
		return cfg;
	}

	// Currently not supported, ..
	protected TVConfigurer createConfigurerForInput(String inputSrc) {
		return new TVConfigurer(context);
	}

	private static TVChannelSelector selector = null;

	/**
	 * @return The channel selector component.
	 */
	public TVChannelSelector getChannelSelector() {
		if (selector == null) {
			selector = createChannelSelector();
			selector.init();
		}
		return selector;
	}

	protected TVChannelSelector createChannelSelector() {
		return new TVChannelSelector(context);
	}

	private static TVInputManager inpMngr = null;

	/**
	 * @return The channel selector component.
	 */
	public TVInputManager getInputManager() {
		if (inpMngr == null) {
			inpMngr = createInputManager();
			inpMngr.init();
		}
		return inpMngr;
	}

	protected TVInputManager createInputManager() {
		return new TVInputManager(context);
	}

	/**
	 * @return The CAM Manage component.
	 * */
	private static TVCAMManager camMngr = null;

	public TVCAMManager getCAMManager() {
		if (camMngr == null) {
			camMngr = createCAMManager();
			camMngr.init();
		}
		return camMngr;
	}

	protected TVCAMManager createCAMManager() {
		return new TVCAMManager(context);
	}

	private static TVStorage storage = null;

	/**
	 * @return The channel selector component.
	 */
	public TVStorage getStorage() {
		if (storage == null) {
			storage = createStorage();
			storage.init();
		}
		return storage;
	}

	protected TVStorage createStorage() {
		return new TVStorage(context);
	}

	private static TVTimerManager timerMngr = null;

	/**
	 * @return The channel selector component.
	 */
	public TVTimerManager getTimerManager() {
		if (timerMngr == null) {
			timerMngr = createTimerManager();
			timerMngr.init();
		}
		return timerMngr;
	}

	protected TVTimerManager createTimerManager() {
		return new TVTimerManager(context);
	}

	private static TVEventManager eventMngr = null;

	private static TVTeleTextManager telTextMngr=null;
	
	public TVTeleTextManager getTeleTextManager(){
		if (telTextMngr == null) {
			telTextMngr = createTeleTextManager();
			telTextMngr.init();
		}
		return telTextMngr;
	}
	/**
	 * @return The channel selector component.
	 */
	public TVEventManager getEventManager() {
		if (eventMngr == null) {
			eventMngr = createEventManager();
			eventMngr.init();
		}
		return eventMngr;
	}
	
	protected TVTeleTextManager createTeleTextManager(){
		return new TVTeleTextManager(context);
	}

	protected TVEventManager createEventManager() {
		return new TVEventManager(context);
	}

	/**
	 * Get current channel.
	 * 
	 * @return
	 */
	public TVChannel getCurrentChannel() {
		return getChannelSelector().getCurrentChannel();
	}

	/**
	 * Got a Binder object for IPC functions.
	 * 
	 * @return
	 */
	public ITVCommon getCommonService() {
		return new TVCommonService(this);

	}

	public void onCreate(Bundle savedInstanceState) {
		Iterator<TVComponent> itr = comps.iterator();
		while (itr.hasNext()) {
			TVComponent comp = itr.next();
			comp.onCreate(savedInstanceState);
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		Iterator<TVComponent> itr = comps.iterator();
		while (itr.hasNext()) {
			TVComponent comp = itr.next();
			comp.onSaveInstanceState(outState);
		}
	}

	public void onStart() {
		Iterator<TVComponent> itr = comps.iterator();
		while (itr.hasNext()) {
			TVComponent comp = itr.next();
			comp.onStart();
		}

	}

	public void onStop() {
		Iterator<TVComponent> itr = comps.iterator();
		while (itr.hasNext()) {
			TVComponent comp = itr.next();
			comp.onStop();
		}
		// We could save things in each component's onStop. However
		// here we can maintain a safe order. Take care of this.

		getChannelManager().flush();

	}

	private boolean audioMute = false; // for dummy only

	public boolean isMute() {
		if (!dummyMode) {
			boolean mute = false;
			BroadcastService srv = (BroadcastService) getTVMngr().getService(
					BroadcastService.BrdcstServiceName);
			try {
				mute = srv.getMute();
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return mute;
		} else {
			return audioMute;
		}

	}

	// true is to mute
	public void setAudioMute(boolean mute) {
		if (!dummyMode) {
			Boolean val = new Boolean(mute);
			BroadcastService srv = (BroadcastService) getTVMngr().getService(
					BroadcastService.BrdcstServiceName);
			try {
				srv.setMute(mute);
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			audioMute = mute;
		} else {
			audioMute = mute;
		}
	}

	public void sendPowerOff() {
		getHandler().post(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						"android.intent.action.ACTION_REQUEST_SHUTDOWN");
				i.putExtra("android.intent.extra.KEY_CONFIRM", false);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);

			}
		});
		// We cannot use this hide action in Eclipse, use this ///

	}

	// set colorkey
	public boolean setColorKey(boolean b_enabled, int color) {
		OSDService srv = (OSDService) getTVMngr().getService(
				OSDService.OSDServiceName);
		return srv.setColorKey(b_enabled, color);
	}

	// set opacity
	public boolean setOpacity(int opacity) {
		OSDService srv = (OSDService) getTVMngr().getService(
				OSDService.OSDServiceName);
		return srv.setOpacity(opacity);
	}

	class AspectOption extends TVOptionRange<Integer> {
		public final int ASP_RATIO_UNKNOWN = 0;
		public final int ASP_RATIO_16_9 = 1;
		public final int ASP_RATIO_4_3 = 2;
		public final int ASP_RATIO_END = ASP_RATIO_4_3;

		public AspectOption() {
			super();
		}

		@Override
		public Integer getMax() {
			// TODO Auto-generated method stub
			return ASP_RATIO_END;
		}

		@Override
		public Integer getMin() {
			return ASP_RATIO_16_9;
		}

		int dummyVal = ASP_RATIO_16_9;

		public Integer get() {
			if (!dummyMode) {
				BroadcastService srv = (BroadcastService) getTVMngr()
						.getService(BroadcastService.BrdcstServiceName);
				int asp;
				try {
					asp = srv.getDisplayAspectRatio();
					switch (asp) {
						case BroadcastService.SVCTX_DISP_ASP_RATIO_16_9 :
							return ASP_RATIO_16_9;
						case BroadcastService.SVCTX_DISP_ASP_RATIO_4_3 :
							return ASP_RATIO_4_3;
						default :
							return ASP_RATIO_UNKNOWN;
					}
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				return dummyVal;
			}
			return ASP_RATIO_UNKNOWN;
		}

		public boolean set(Integer val) {
			if (!dummyMode) {
				BroadcastService srv = (BroadcastService) getTVMngr()
						.getService(BroadcastService.BrdcstServiceName);
				int rawVal;
				switch (val.intValue()) {
					case ASP_RATIO_16_9 :
						rawVal = BroadcastService.SVCTX_DISP_ASP_RATIO_16_9;
					case ASP_RATIO_4_3 :
						rawVal = BroadcastService.SVCTX_DISP_ASP_RATIO_4_3;
					default :
						rawVal = BroadcastService.SVCTX_DISP_ASP_RATIO_UNKNOWN;
				}
				try {
					srv.setDisplayAspectRatio(rawVal);
					return true;
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				dummyVal = val.intValue();
				return true;
			}
			return false;
		}

	};

	public final static String OPTION_ASPECT_RATIO = "AspectRatio";

	int stopCount = 0;

	/**
	 * @deprecated
	 */
	public void pushStop() {
		stopCount++;
		BroadcastService brdSrv = (BroadcastService) getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		try {
			brdSrv.syncStopService();
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * stop TV or other input sources.
	 */
	public void manualStop(){
		BroadcastService brdSrv = (BroadcastService) getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		try {			
			brdSrv.syncStopService();
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * stop video for TV sources.
	 */
	public void tvVideoStop() {
		BroadcastService brdSrv = (BroadcastService) getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		try {
			brdSrv.syncStopVideoStream();
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @deprecated
	 */
	public void popStop() {
		stopCount--;
		if (stopCount == 0) {
			updatePipeLine();
		}
	}

	/**
	 * Unblock the playing
	 */
	public void unblock(String outputName) {
		TVOutput output = getInputManager().getOutput(outputName);
		if (output != null) {
			TVInput input = output.getInput();
			if (input != null) {
				if (input.getType().equals(TVInputManager.INPUT_TYPE_TV)) {
					TVChannel ch = getChannelSelector().getCurrentChannel();
					if (ch != null) {
						TvInfo("to usrUnblock current channel");
						ch.usrUnblock();
					}
				}
				if (input.isBlocked()){
					TvInfo("to usrUnblock source");
					input.usrUnblock();
				}
				sendUpdate(output);
			}
		}
	}

	public void sendUpdate(TVOutput output) {
		sendMessageOnce(MSG_UPDATE_PIPE, output);
	}
	
	public void sendUpdateDelayed(TVOutput output, long delayMillis) {
		sendMessageOnceDelayed(MSG_UPDATE_PIPE, output, delayMillis);
	}

	public void sendUpdate() {
		sendMessageOnce(MSG_UPDATE_PIPE_ALL, null);
	}

	public void sendUpdateDelayed(long delayMillis) {
		sendMessageOnceDelayed(MSG_UPDATE_PIPE_ALL, null, delayMillis);
	}

	static final int MSG_UPDATE_PIPE = 0x1;
	static final int MSG_UPDATE_PIPE_ALL = 0x2;

	// static class UpdatePipeReq {
	// UpdatePipeReq(TVOutput output, boolean checkBlock) {
	// this.output = output;
	// this.checkBlock = checkBlock;
	// }
	// TVOutput output;
	// boolean checkBlock;
	// }

	protected void handleMessage(int msgCode, Object obj) {
		switch (msgCode) {
			case MSG_UPDATE_PIPE :
				TvLog("Update PIPE message " + msgCode);
				if (obj instanceof TVOutput) {
					TVOutput output = (TVOutput) obj;
					updatePipeLine(output);
				}
				break;
			case MSG_UPDATE_PIPE_ALL :
				TvLog("Update PIPE (ALL) message " + msgCode);
				updatePipeLine();				
				break;
			default :
				TvLog("What message , who ??? " + msgCode);
		}
	}

	/**
	 * Update pipeline, connect tv manager components and do necessary check.
	 * 
	 * @param output
	 */
	protected void updatePipeLine(TVOutput output/* , boolean checkBlock */) {
		TVInputManager inp = getInputManager();
		TVChannelSelector sel = getChannelSelector();
		TVConfigurer cf = getConfigurer();
		TVInput input = output.getInput();
		TVScanner scanner = getScanner();
		if (output == null) {
			return;
		}
		if(inp.getOuputMode() == TVInputManager.OUTPUT_MODE_PIP) {
			if(output.getName().equals(TVInputManager.OUTPUT_NAME_MAIN)) {
				return;
			}
		}
		if(inp.getOuputMode() == TVInputManager.OUTPUT_MODE_NORMAL) {
			if(output.getName().equals(TVInputManager.OUTPUT_NAME_SUB)) {
				return;
			}
		}
		
		if (scanner.getState() == TVScanner.STATE_SCANNING) {
			return;
		}
		if (dummyMode) {
			TVChannel curCh = sel.getCurrentChannel();
			if (curCh != null) {
				Toast toast = Toast.makeText(context, "I am playing "
						+ curCh.getChannelName(), 1000);
				toast.show();
			}

			return;
		} else {
			InputService inpSrv = (InputService) getTVMngr().getService(
					InputService.InputServiceName);
			BroadcastService brdcstSrv = (BroadcastService) getTVMngr()
					.getService(BroadcastService.BrdcstServiceName);
			TVOptionRange<Integer> blueOpt = (TVOptionRange<Integer>) cf
					.getOption(ConfigType.CFG_BLUE_SCREEN);
			try {
				if (input != null) {
					inpSrv.bind(output.getHWName(), input.getHWName());
					TvInfo("Input <" + input.getName() + "> and output<"
							+ output.getName() + "> is bound");
					if (input.getType().equals(TVInputManager.INPUT_TYPE_TV)) {
						TvInfo("Input is TV, try to select channel");
						TVChannel curCh = sel.getCurrentChannel();

						if (curCh != null) {
							if (input.isBlocked() && !input.isUsrUnblocked()
									&& !curCh.isUsrUnblocked()) {
								TvInfo("Input is blocked and current channel not usrUnblock, stop");
								inp.notifyInputBlocked(input.getName());
								//brdcstSrv.syncStopService();
                                output.stop();
								//if blue screen on, set blue mute
								if(blueOpt.get() == ConfigType.COMMON_ON){
									brdcstSrv.setVideoBlueMute();
								}
							} else {
								if (curCh.isValid()) {
									TvInfo("Channel is correct");
									if (curCh.isBlocked()
											&& !curCh.isUsrUnblocked()
											&& !input.isUsrUnblocked()) {
										TvInfo("Channel is blocked, stop");
										//brdcstSrv.syncStopService();
                                        output.stop();
										sel.notifyChannelBlocked(curCh);
										//if blue screen on, set blue mute
										if(blueOpt.get() == ConfigType.COMMON_ON){
											brdcstSrv.setVideoBlueMute();
										}
									} else {
										TvInfo("Channel is OK, Play !!!!");

										if (input.isUsrUnblocked()){
											TvInfo("to usrUnblock source and current channel");
											input.usrUnblock();
											curCh.usrUnblock();
										}

										if (curCh.getRawInfo() != null) {
											brdcstSrv.channelSelect(curCh
													.getRawInfo());
										} else {
											//brdcstSrv.syncStopService();
                                            output.stop();
											//if blue screen on, set blue mute
											if(blueOpt.get() == ConfigType.COMMON_ON){
												brdcstSrv.setVideoBlueMute();
											}
										}
									}
								}else{
									TvInfo("Channel is invalid");
									output.stop();
									//if blue screen on, set blue mute
									if(blueOpt.get() == ConfigType.COMMON_ON){
										brdcstSrv.setVideoBlueMute();
									}
								}
							}
						} else {
							TvInfo("Channel is Invalid, stop !!!!");
							//brdcstSrv.syncStopService();
                            output.stop();
							//if blue screen on, set blue mute
							if(blueOpt.get() == ConfigType.COMMON_ON){
								brdcstSrv.setVideoBlueMute();
							}
						}

					} else {// end input.getType().equals(TVInputManager.INPUT_TYPE_TV)

						TVOptionRange<Integer> mtsOpt = (TVOptionRange<Integer>) cf
								.getOption(ConfigType.CFG_AUD_DMIX);
						mtsOpt.set(ConfigType.AUD_DOWNMIX_MODE_DUAL_OFF);

						if (input.isBlocked() && !input.isUsrUnblocked()) {
							TvInfo("Other inputs except TV is blocked, stop");
							inp.notifyInputBlocked(input.getName());
							// brdcstSrv.syncStopService();
							output.stop();
							//if blue screen on, set blue mute
							if(blueOpt.get() == ConfigType.COMMON_ON){
								brdcstSrv.setVideoBlueMute();
							}
						}
					}
				} else {
					/**
					 * NULL means that we dont't do anything on this ,
					 */

				}
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Fix me, should find TV input
	protected void updatePipeLine(/* boolean blockCheck */) {
		TVOutput[] outputs = getInputManager().getOutputs();
		for (int i = 0; i < outputs.length; i++) {
			updatePipeLine(outputs[i]/* , blockCheck */);
		}

	}

	public String getSubtitleLang() {
		BroadcastService brdcstSrv = (BroadcastService) getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		SubtitleInfo si;
		try {
			si = brdcstSrv.getSubtitleInfo();
			return si.getSubtitleLang();
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public String getCurSubtitleLang() {
		BroadcastService brdcstSrv = (BroadcastService) getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		SubtitleInfo si;
		try {
			si = brdcstSrv.getSubtitleInfo();
			return si.getCurrentSubtitleLang();
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public void setSubtitleLang(String lang) {
		BroadcastService brdcstSrv = (BroadcastService) getTVMngr().getService(
				BroadcastService.BrdcstServiceName);		
		try {
			brdcstSrv.setSubtitleLang(lang);			

		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void syncStopSubtitleStream() {
		BroadcastService brdcstSrv = (BroadcastService) getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		try {
			brdcstSrv.syncStopSubtitleStream();

		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @deprecated
	 * @param output
	 * @param x
	 *            0.0f - 1.0f
	 * @param y
	 *            0.0f - 1.0f
	 * @param w
	 *            0.0f - 1.0f
	 * @param h
	 *            0.0f - 1.0f
	 */
	public void setScreenPosition(String output, float x, float y, float w,
			float h) {
		InputService inpSrv = (InputService) getTVMngr().getService(
				InputService.InputServiceName);
		Rect r = new Rect((int) (x * 10000.0f), (int) (y * 10000.0f),
				(int) (w * 10000.0f), (int) (h * 10000.0f));
		inpSrv.setScreenOutputRect(output, r);
	}
	private int lastGrp = TVConfigurer.CFG_GRP_ATV;
	
	/**
	 * These APIs help AP handle some dirty things when AP leaves or enters active mode...
	 */
	public void enterTV() {
		TVConfigurer cfger = getConfigurer();
		TVInputManager im = getInputManager();
		TVOutput output = im.getOutput(TVInputManager.OUTPUT_NAME_MAIN);
		TVInput input  = output.getInput(); 
		if(input != null) {
			cfger.setGroup(input.getInpCtx().getCfgGrpIdx(input));
		}	
	}
	
	/**
	 * 
	 */
	public void leaveTV() {
		TVConfigurer cfger = getConfigurer();
		lastGrp = cfger.getGroup();
		cfger.setGroup(TVConfigurer.CFG_GRP_LEAVE);
		
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
	 * @param isMute, set blue mute if it is true
	 */
	public void setVideoBlueMute(boolean isMute){
		BroadcastService brdSrv = (BroadcastService) getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		try {
			brdSrv.setVideoBlueMute(isMute);
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
