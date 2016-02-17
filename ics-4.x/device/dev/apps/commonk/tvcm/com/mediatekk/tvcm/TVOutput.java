/**
 * 
 */
package com.mediatekk.tvcm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.AudioInfo;
import com.mediatek.tv.model.AudioLanguageInfo;
import com.mediatek.tv.model.InputExchange;
import com.mediatek.tv.model.InputExchangeOutputMute;
import com.mediatek.tv.model.SignalLevelInfo;
import com.mediatek.tv.service.BroadcastService;
import com.mediatek.tv.service.InputService;
import com.mediatek.tv.service.InputService.AutoAdjustType;
import com.mediatek.tv.service.InputService.OutputRegionCapability;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.RectF;

public class TVOutput {
	TVInputManager im;
	TVInput input;
	String name;
	boolean signal;
	private List<AudioUpdated> list = new ArrayList<AudioUpdated>();

	public static final int AUTO_ADJUST = 0;
	public static final int AUTO_PHASE = 1;
	public static final int AUTO_COLOR = 2;

	public interface AudioUpdated {
		public void onUpdate();
	}

	//
	// boolean enable;

	public void registerAudioUpdatedListener(AudioUpdated update) {
		if (update == null) {
			throw new IllegalArgumentException();
		}
		synchronized (this) {
			if (!list.contains(update)) {
				list.add(update);
			}
		}
	}

	public void removeAudioUpdatedListener(AudioUpdated update) {
		list.remove(update);
	}

	public void clearAudioUpdatedListener(AudioUpdated update) {
		list.clear();
	}

	TVOutput(TVInputManager im, String name) {
		this.im = im;
		this.name = name;
		this.input = null;

		this.signal = false;

		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastService.ACTION_SVCTX_NFY);
		im.context.registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String nfyCode = intent
						.getStringExtra(BroadcastService.SVCTX_NFY_CODE);

				if (nfyCode.equals(BroadcastService.SVCTX_NFY_AUDIO_UPDATED)) {
					for (AudioUpdated update : list) {
						update.onUpdate();
					}
				}
			}
		}, filter);
	}

	public TVInput getInput() {
		return input;
	}

	public String getName() {
		return name;
	}

	public String getHWName() {
		return name;
	}

	public boolean hasSignal() {
		return signal;
	}

	public boolean isEnabled() {
		if (im.getOuputMode() == TVInputManager.OUTPUT_MODE_NORMAL
				&& name.equals(InputService.INPUT_OUTPUT_SUB)) {
			return false;
		}
		return true;
		// return enable;
	}

	public boolean canConnect(TVInput input) {
		return true;

		// if (input == null) {
		// return true;
		// }
		// TVOutput[] outputs = im.getOutputs();
		// for (int i = 0; i < outputs.length; i++) {
		// if (outputs[i] != this && outputs[i].isEnabled()) {
		// TVInput otherInput = outputs[i].getInput();
		// if (otherInput != null) {
		// if (input.conflictWith(otherInput)) {
		// return false;
		// }
		// }
		// }
		// }
		// return true;
	}

	public TVInput[] getConnectableInputs() {
		TVInput[] inputs = im.getInputs();
		Vector<TVInput> res = new Vector<TVInput>(4);
		for (int i = 0; i < inputs.length; i++) {
			if (canConnect(inputs[i])) {
				res.add(inputs[i]);
			}
		}
		return res.toArray(new TVInput[res.size()]);
	}

	public void loadConnect() {
		TVStorage st = im.getContent().getStorage();
		String inputName = st.get(FIELD_START_INP_PRFX + name);
		if (inputName != null) {
			TVInput inp = im.getInput(inputName);
			if (inp != null) {
				connect(inp);
				return;
			}
		}
		// First time of support..
		if (name.equals(TVInputManager.OUTPUT_NAME_MAIN)) {
			connect(im.getInput(im.getContent().getCustomer().getInitInput()));
		} else {
			connect(im.getInput("hdmi0"));
		}

	}

	public void connect(TVInput input) {

		if (!canConnect(input)) {
			TVComponent.TvLog("Cannot connect output " + name + "<->" + input);
			return;
		}

		TVStorage st = im.getContent().getStorage();
		if (this.input != null) {
			this.input.getInpCtx().onDisconnected(this.input, this);
		}
		this.input = input;

		if (input != null) {
			input.getInpCtx().onConnected(input, this);
			st.set(FIELD_START_INP_PRFX + name, input.getName());
			signal = false;
			im.notifyInputSelect(name, input.getName());
		} else {
			st.set(FIELD_START_INP_PRFX + name, "");
			im.notifyInputSelect(name, "");
		}
		st.flush();
		im.getContent().sendUpdate();
		TVComponent.TvLog("Connect output " + name + "<->" + input);
	}

	final static String FIELD_START_INP_PRFX = "start_input_";

	String getSavedInputName() {
		TVStorage st = im.getContent().getStorage();
		return st.get(FIELD_START_INP_PRFX + name);
	}

	//
	// public void connect(TVInput input) {
	// setInputInternel(input);
	// }

	void swap(TVOutput output) {
		if (output == null) {

			return;
		}
		// Need call TVManager's swap to do the real 'connection'.
		// Normally we don't do bind for input/output change. However
		// , we cannot stop SWAP doing that. Finally the correct playing
		// or stoping will be done in updatePipeLine in connect.
		// If there is any issue for low level pipeline problem, we can
		// add functions like setInputInternal which does not updatePipeLine
		// but call it at last.
		if (im.inpSrv.swap(name, output.getName()) == InputService.INPUT_RET_OK) {

			TVInput inputOther = output.getInput();
			connect(inputOther);
			output.connect(input);

		}

	}

	public boolean aspectRatioCanSet(){
		return im.inpSrv.getAspectRatioEnable(name);
	}
	
	
	private boolean rectCanSet(Rect r, OutputRegionCapability cap) {
		if (r.left >= cap.x_min && r.left <= cap.x_max && r.top >= cap.y_min
				&& r.top <= cap.y_max && r.width() >= cap.width_min
				&& r.width() <= cap.width_max && r.height() >= cap.height_min
				&& r.height() <= cap.height_max) {
			return true;
		} else {
			return false;
		}
	}

	public boolean setScreenRectangle(RectF rect) {
		Rect r = new Rect((int) (rect.left * 10000.0f),
				(int) (rect.top * 10000.0f), (int) (rect.right * 10000.0f),
				(int) (rect.bottom * 10000.0f));
		OutputRegionCapability cap = im.inpSrv.getScreenOutputCapability(name);
		if (true) {
		im.inpSrv.setScreenOutputRect(name, r);
			return true;
		} else {
			return false;
		}
	}

	public RectF getScreenRectangle() {
		Rect rect = im.inpSrv.getScreenOutputRect(name);
		return new RectF((float) rect.left / 10000.0f,
				(float) rect.top / 10000.0f, (float) rect.right / 10000.0f,
				(float) rect.bottom / 10000.0f);
	}

	public boolean setSrcRectangle(RectF rect) {
		Rect r = new Rect((int) (rect.left * 10000.0f),
				(int) (rect.top * 10000.0f), (int) (rect.right * 10000.0f),
				(int) (rect.bottom * 10000.0f));
		OutputRegionCapability cap = im.inpSrv
				.getScreenOutputVideoCapability(name);
		if (true) {
		im.inpSrv.setScreenOutputVideoRect(name, r);
			return true;
		} else {
			return false;
		}
	}

	public RectF getSrcRectangle() {
		Rect rect = im.inpSrv.getScreenOutputVideoRect(name);
		return new RectF((float) rect.left / 10000.0f,
				(float) rect.top / 10000.0f, (float) rect.right / 10000.0f,
				(float) rect.bottom / 10000.0f);
	}

	
	public boolean isFreeze(){
		boolean freeze = false;
		
		if (!im.dummyMode) {
			BroadcastService brdSrv = null;
			brdSrv = (BroadcastService) im.getTVMngr().getService(
					BroadcastService.BrdcstServiceName);
			
			if (brdSrv != null) {
				try {
					freeze = brdSrv.isFreeze(name);
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return freeze;
	}
	
	public void setFreeze(boolean isFreeze) {
		if (!im.dummyMode) {
			BroadcastService brdSrv = null;
			brdSrv = (BroadcastService) im.getTVMngr().getService(
					BroadcastService.BrdcstServiceName);
			if (brdSrv != null) {
				try {
					brdSrv.setFreeze(name, isFreeze);
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public boolean isEnableFreeze(){
		boolean enableFreeze = false;
		if (!im.dummyMode) {
			BroadcastService brdSrv = null;
			brdSrv = (BroadcastService) im.getTVMngr().getService(
					BroadcastService.BrdcstServiceName);
			if (brdSrv != null) {
				try {
					enableFreeze = brdSrv.enableFreeze(name);
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return enableFreeze;
	}

	boolean setDefault() {
		try {
			if (!im.dummyMode) {
				TVComponent.TvLog("Try to change focus to " + name);
				im.inpSrv.focusChangeTo(name);
			}
			return true;
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return true;

		}
	}

	public static interface AdjustListener {
		public static final int EVENT_FINISHED = 0;
		public static final int EVENT_NOT_SUPPORT = 1;

		// ...
		public void onEvent(int event);
	}

	/**
	 * @deprecated
	 */
	public void adjust(final AdjustListener listener) {
		new Thread(new Runnable() {
			public void run() {
				try {
					im.inpSrv.setAutoAdjust(name,
							InputService.AutoAdjustType.AUTO_TYPE_VGA_ADJUST);
					// Work around:
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// if (listener != null) {
				im.getHandler().post(new Runnable() {
					public void run() {
						if (listener != null) {
							listener.onEvent(AdjustListener.EVENT_FINISHED);
						}
					}
				});
				// }
			}
		}).start();
	}

	public void adjust(final AdjustListener listener, final int item) {
		new Thread(new Runnable() {
			public void run() {
				try {
					AutoAdjustType mAutoAdjustType = AutoAdjustType.AUTO_TYPE_COLOR;
					switch (item) {
					case AUTO_ADJUST:
						mAutoAdjustType = AutoAdjustType.AUTO_TYPE_VGA_ADJUST;
						break;
					case AUTO_PHASE:
						mAutoAdjustType = AutoAdjustType.AUTO_TYPE_PHASE;
						break;
					case AUTO_COLOR:
						mAutoAdjustType = AutoAdjustType.AUTO_TYPE_COLOR;
						break;
					default:
						mAutoAdjustType = AutoAdjustType.AUTO_TYPE_COLOR;
						break;
					}
					im.inpSrv.setAutoAdjust(name, mAutoAdjustType);
					// Work around:
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// if (listener != null) {
				im.getHandler().post(new Runnable() {
					public void run() {
						if (listener != null) {
							listener.onEvent(AdjustListener.EVENT_FINISHED);
						}
					}
				});
				// }
			}
		}).start();
	}

	public static class OutputColorSystemOption extends ColorSystemOption {
		TVOutput output;

		public OutputColorSystemOption(TVOutput output) {
			super();
			this.output = output;
		}

		public Integer get() {
			int cs;
			try {
				cs = output.im.inpSrv.getColorSystem(output.getName());
				for (int i = 0; i < color_sys_tbl.length; i++) {
					if (color_sys_tbl[i] == cs) {
						return i;
					}
				}
				TVComponent.TvLog("Color System failed" + cs);
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return this.COLOR_SYS_AUTO;
		}

		@Override
		public boolean set(Integer val) {
			// Throw exception here?
			return true;
		}
	}

	OutputColorSystemOption colorSytem = new OutputColorSystemOption(this);

	public OutputColorSystemOption getColorSystem() {
		return colorSytem;
	}
	
	private VideoResolution inst = null;
	public VideoResolution getInstanceVideoResolution() {
		if (inst == null) {
			inst = new VideoResolution(this);
		}
		return inst;
	}

	public static class VideoResolution {
		public static enum TimingType {
			TIMING_TYPE_UNKNOWN, TIMING_TYPE_VIDEO, TIMING_TYPE_GRAPHIC, TIMING_TYPE_NOT_SUPPORT
		}

		TVOutput output;

		VideoResolution(TVOutput output) {
			this.output = output;
		}

		public boolean isVideoProgressive() {
			try {
				InputService.VideoResolution res = output.im.inpSrv
						.getVideoResolution(output.name);
				return res.isProgressive;
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		public Rect getVideoResolution() {
			try {
				InputService.VideoResolution res = output.im.inpSrv
						.getVideoResolution(output.name);
				return new Rect(0, 0, res.width, res.height);
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		public String getVideoFormat() {
			try {
				InputService.VideoResolution res = output.im.inpSrv
						.getVideoResolution(output.name);
				return res.sdHdString;
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		public int getVideoFramerate() {
			try {
				InputService.VideoResolution res = output.im.inpSrv
						.getVideoResolution(output.name);
				return res.frameRate;
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}

		public TimingType getTimingType() {
			try {
				InputService.VideoResolution res = output.im.inpSrv
						.getVideoResolution(output.name);
				return TimingType.values()[res.timingType.ordinal()];
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return TimingType.values()[0];
		}
	}

	/**
	 * @deprecated
	 */
	public boolean isVideoProgressive() {
		try {
			InputService.VideoResolution res = im.inpSrv
					.getVideoResolution(name);
			return res.isProgressive;
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @deprecated
	 */
	public Rect getVideoResolution() {
		try {
			InputService.VideoResolution res = im.inpSrv
					.getVideoResolution(name);
			return new Rect(0, 0, res.width, res.height);
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// public String getVideoFormat(){
	// try {
	// BroadcastService srv = (BroadcastService) im.getTVMngr().getService(
	// BroadcastService.BrdcstServiceName);
	// return srv.getVideoResolution(name).getVideoFormat();
	// } catch (TVMException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return null;
	// }
	/**
	 * @deprecated
	 */
	public String getVideoFormat() {
		try {
			InputService.VideoResolution res = im.inpSrv
					.getVideoResolution(name);
			return res.sdHdString;
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @deprecated
	 */
	public int getVideoFramerate() {// hzy fix CR:363961
		try {
			InputService.VideoResolution res = im.inpSrv
					.getVideoResolution(name);
			return res.frameRate;
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * @deprecated
	 * @return
	 */
	public String[] getAudioLangArray() {
		List<String> langs = new ArrayList<String>();
		BroadcastService srv = (BroadcastService) im.getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		String str = null;
		try {
			str = srv.getDtvAudioLangInfo().getAudioLanguage();
		} catch (TVMException e) {
			e.printStackTrace();
		}
		if (str == null || str.length() == 0 || str.length() % 3 != 0) {
			return null;
		}
		for (int i = 0; i <= str.length() - 3; i += 3) {
			langs.add(str.substring(i, i + 3));
		}
		return langs.toArray(new String[0]);
	}

	/**
	 * @deprecated
	 */
	public String getAudLang() {
		BroadcastService srv = (BroadcastService) im.getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		AudioLanguageInfo ai;
		try {
			ai = srv.getDtvAudioLangInfo();

			return ai.getAudioLanguage();
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @deprecated
	 */
	public String getNextAudLang() {

		String defAudLang = getDefaultAudLang();
		String allAudLang = getAudLang();

		if (allAudLang == null || allAudLang.equals("") || defAudLang == null
				|| defAudLang.equals("")) {
			if (allAudLang == null || allAudLang.equals("")) {
				return null;
			} else {
				return allAudLang.substring(0, 3);
			}
		} else {

			int index = allAudLang.indexOf(defAudLang) + 3;

			if (index >= allAudLang.length()) {
				index = 0;
			}

			return allAudLang.substring(index, index + 3);

		}
	}

	/**
	 * @deprecated
	 * @param lang
	 */
	public void setAudLang(String lang) {
		BroadcastService srv = (BroadcastService) im.getTVMngr().getService(
				BroadcastService.BrdcstServiceName);

		try {
			srv.setDtvAudioLang(lang);
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @deprecated
	 * @return
	 */
	public String getDefaultAudLang() {
		BroadcastService srv = (BroadcastService) im.getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		AudioLanguageInfo ai;
		try {
			ai = srv.getDtvAudioLangInfo();

			return ai.getCurrentLanguage();
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get the index of current audio language.
	 * 
	 * @deprecated
	 * 
	 * @return int: Index of current audio language
	 */
	public int getCurAudLangIndex() {
		BroadcastService srv = (BroadcastService) im.getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		AudioLanguageInfo ai;
		try {
			ai = srv.getDtvAudioLangInfo();
			return ai.getCurrentAudioLangIndex();
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Get the index and value of current audio language.
	 * 
	 * @return Hashtable<Integer, String>: A hashtalbe which stores the current
	 *         audio language(value of hashtable) and its index(key of
	 *         hashtable).
	 */
	public Hashtable<Integer, String> getCurAudLang() {
		Hashtable<Integer, String> table = new Hashtable<Integer, String>();
		BroadcastService srv = (BroadcastService) im.getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		AudioLanguageInfo ai;
		try {
			ai = srv.getDtvAudioLangInfo();
			table.put(new Integer(ai.getCurrentAudioLangIndex()), ai
					.getCurrentLanguage());
			return table;
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get all audio language.
	 * 
	 * @return Hashtable<Integer, String>: A hashtalbe which stores the audio
	 *         language(value of hashtable) and its index(key of hashtable). The
	 *         index start from 1.
	 */
	public Hashtable<Integer, String> getAudLangTable() {
		Hashtable<Integer, String> table = new Hashtable<Integer, String>();
		BroadcastService srv = (BroadcastService) im.getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		String str = null;
		try {
			str = srv.getDtvAudioLangInfo().getAudioLanguage();
		} catch (TVMException e) {
			e.printStackTrace();
		}
		if (str == null || str.length() == 0 || str.length() % 3 != 0) {
			return null;
		}
		for (int i = 0; i <= str.length() - 3; i += 3) {
			table.put(new Integer(i / 3 + 1), str.substring(i, i + 3));
		}
		return table;
	}

	/**
	 * 
	 * @param audIndex
	 */
	public void setAudLangByIndex(int audIndex) {
		BroadcastService srv = (BroadcastService) im.getTVMngr().getService(
				BroadcastService.BrdcstServiceName);

		try {
			srv.setDtvAudioLangByIndex(audIndex);
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @deprecated
	 */
	public int getTotalAudLangNum() {
		BroadcastService srv = (BroadcastService) im.getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		AudioLanguageInfo ai;
		try {
			ai = srv.getDtvAudioLangInfo();
			return ai.getTotalNumber();
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static class MTSOption extends TVOptionRange<Integer> {
		public static final int MTS_UNKNOWN = 0;
		public static final int MTS_MONO = 1;
		public static final int MTS_STEREO = 2;
		public static final int MTS_SUB_LANG = 3;
		public static final int MTS_DUAL1 = 4;
		public static final int MTS_DUAL2 = 5;
		public static final int MTS_NICAM_MONO = 6;
		public static final int MTS_NICAM_STEREO = 7;
		public static final int MTS_NICAM_DUAL1 = 8;
		public static final int MTS_NICAM_DUAL2 = 9;
		public static final int MTS_FM_MONO = 10;
		public static final int MTS_FM_STEREO = 11;
		public static final int MTS_END = 11;

		final private TVOutput output;
		BroadcastService srv;

		MTSOption(TVOutput output) {
			this.output = output;
			// srv = (BroadcastService)
			// output.im.getTVMngr().getService(BroadcastService.BrdcstServiceName);
		}

		private List<Integer> arrayCanSet;

		private void updateCanset(int af) {
			arrayCanSet = new ArrayList<Integer>();
			switch (af) {
			case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_FM_MONO_NICAM_MONO:
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_MONO);
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_NICAM_MONO);
				break;
			case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_FM_MONO_NICAM_STEREO:
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_MONO);
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_NICAM_STEREO);
				break;
			case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_FM_MONO_NICAM_DUAL:
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_MONO);
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_NICAM_DUAL1);
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_NICAM_DUAL2);
				break;
			case BroadcastService.SVCTX_AUD_ALTERNATIVE_CHANNELS_MONO:
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_MONO);
				break;
			case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_DUAL_MONO:
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_DUAL1);
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_DUAL2);
				break;
			case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_MONO_SUB:
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_MONO);
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_SUB_LANG);
				break;
			case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_STEREO:
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_MONO);
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_STEREO);
				break;
			case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_STEREO_SUB:
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_MONO);
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_STEREO);
				arrayCanSet.add(BroadcastService.SVCTX_AUD_MTS_SUB_LANG);
				break;
			default:
				break;
			}
		}

		public boolean canSet(int val) {

			// // TODO Auto-generated method stub
			try {
				srv = (BroadcastService) output.im.getTVMngr().getService(
						BroadcastService.BrdcstServiceName);
				AudioInfo ai = srv.getAudioInfo(output.getName());
				int af = ai.getAlternativeAudio();
				updateCanset(af);
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(arrayCanSet==null || arrayCanSet.size()<=0){
				return false;
			}else{
				return arrayCanSet.contains(val);
			}
		}

		public Integer getMax() {
			return MTS_END;

		}

		@Override
		public Integer getMin() {
			return MTS_UNKNOWN;
		}

		@Override
		public Integer get() {
			// TODO Auto-generated method stub
			srv = (BroadcastService) output.im.getTVMngr().getService(
					BroadcastService.BrdcstServiceName);
			try {
				AudioInfo ai = srv.getAudioInfo(output.getName());			
				if (canSet(ai.getMts())) {				
					return ai.getMts();
				} else {			
					return MTS_MONO;
				}
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return MTS_MONO;
		}
		
		public int getMtsIndex(int curMts){
			srv = (BroadcastService) output.im.getTVMngr().getService(
					BroadcastService.BrdcstServiceName);
			try {
				AudioInfo ai = srv.getAudioInfo(output.getName());
				updateCanset(ai.getAlternativeAudio());
				if(arrayCanSet==null || arrayCanSet.size()<=0){
					return -1;
				}else{
					return arrayCanSet.indexOf(curMts);
				}
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return -1;
		}		

		@Override
		public boolean set(Integer val) {
			// TODO Auto-generated method stub
			try {
				srv = (BroadcastService) output.im.getTVMngr().getService(
						BroadcastService.BrdcstServiceName);
				srv.setAudioInfo(output.getName(), val);
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		public int getTotalNum() {
			try {
				srv = (BroadcastService) output.im.getTVMngr().getService(
						BroadcastService.BrdcstServiceName);
				AudioInfo ai = srv.getAudioInfo(output.getName());
				int af = ai.getAlternativeAudio();
				updateCanset(af);			
				switch (af) {
				case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_FM_MONO_NICAM_MONO:
					return 2;
				case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_FM_MONO_NICAM_STEREO:
					return 2;
				case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_FM_MONO_NICAM_DUAL:
					return 3;
				case BroadcastService.SVCTX_AUD_ALTERNATIVE_CHANNELS_MONO:
					return 1;
				case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_DUAL_MONO:
					return 2;
				case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_MONO_SUB:
					return 2;
				case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_STEREO:
					return 2;
				case BroadcastService.SVCTX_AUD_ALTERNATIVE__CHANNELS_STEREO_SUB:
					return 3;
				default:
					return 0;
				}

			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}

	}

	MTSOption mtsOption = new MTSOption(this);

	public MTSOption getMTSOption() {
		return mtsOption;
	}

	public void stop() {
		try {
			im.inpSrv.stopDesignateOutput(name, true);
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void toTop() {
		/**
		 * Currently, not , support !!!!
		 */
		// PlaneName[] pn;
		// try {
		// pn = im.inpSrv.getPlaneArray();
		//	        
		// if(name.equals(TVInputManager.OUTPUT_NAME_MAIN)) {
		// pn[0] = PlaneName.GLPMX_MAIN;
		// pn[1] = PlaneName.GLPMX_OSD1;
		// pn[2] = PlaneName.GLPMX_OSD2;
		// pn[3] = PlaneName.GLPMX_PIP;
		// } else {
		// pn[0] = PlaneName.GLPMX_PIP;
		// pn[1] = PlaneName.GLPMX_OSD1;
		// pn[2] = PlaneName.GLPMX_OSD2;
		// pn[3] = PlaneName.GLPMX_MAIN;
		// }
		//			
		// im.inpSrv.setPlaneArray(pn);
		// } catch (TVMException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/**
	 * @brief Get signal Level
	 * @return Signal Level(rang: 0-100)
	 */
	public int getSignalLevel() {
		BroadcastService brdSrv = null;
		brdSrv = (BroadcastService) im.getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		if (brdSrv != null) {
			try {
				SignalLevelInfo sigInfo = brdSrv.getSignalLevelInfo(this.name);
				return sigInfo.getSignalLevel();
			} catch (TVMException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	/**
	 * @brief Get signal ber
	 * @return Signal ber
	 */
	public int getSignalBer() {
		BroadcastService brdSrv = null;
		brdSrv = (BroadcastService) im.getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		if (brdSrv != null) {
			try {
				SignalLevelInfo sigInfo = brdSrv.getSignalLevelInfo(this.name);
				return sigInfo.getBer();
			} catch (TVMException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

    /**
     * Stop video only for TV or other input sources.
     * 
     * @param mute
     *            whether stop video or not, mute video if it is true.
     * @return void 
     * 	
     */
	public void videoStop(boolean mute) {
		InputExchangeOutputMute inputExchange = new InputExchangeOutputMute();
		inputExchange.setDoMute(mute);
		im.inpSrv.setOutputProperty(this.name,
				InputExchange.INPUT_SET_TYPE_OUTPUT_MUTE, inputExchange);
	}
}
