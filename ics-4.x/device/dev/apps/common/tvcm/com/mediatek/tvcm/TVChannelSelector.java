package com.mediatek.tvcm;

import java.util.Enumeration;
import java.util.Vector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import android.widget.Toast;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.AnalogChannelInfo;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.DvbChannelInfo;
import com.mediatek.tv.service.BroadcastService;

/**
 * Channel selector.<br>
 * Change current channel.
 * 
 * NOTE, some of the notifications are from TV Manager such as signal and
 * service is changed. But there are several notifications are from TV Common
 * logic itself such as block.
 * 
 * @author mtk40063
 * 
 */
public class TVChannelSelector extends TVComponent {

	public interface SelectorListener {
		final static String STATE_AUDIO_AND_VIDEO_SCRAMBLED = "videoAndAudioScrambled";
		final static String STATE_AUDIO_CLEAR_VIDEO_SCRAMBLED = "audioScrambled";
		final static String STATE_AUDIO_NO_VIDEO_SCRAMBLED = "audioScrambledAndNoVideo";
		final static String STATE_VIDEO_CLEAR_AUDIO_SCRAMBLED = "videoScrambled";
		final static String STATE_VIDEO_NO_AUDIO_SCRAMBLED = "videoScrambledAndNoAudio";
		final static String STATE_UNSCRAMBLED = "unscrambled";

		public void onChannelSelect(TVChannel ch);

		public void onSignalChange(boolean hasSignal);

		public void onBlock(TVChannel ch);

		public void onScramble(boolean hasScramble, String state);
	}

	private TVChannel currentCh;

	void setCurrentCh(TVChannel ch) {
		currentCh = ch;
	}

	private static final String PRE_ANALOG_CHNUM = "pre_analog_chnum";
	private static final String PRE_DIGITAL_CHNUM = "pre_digital_chnum";
	private SharedPreferences sp = null;
	private Editor editor = null;

	// private TVChannel tempCh = null;
	private TVManager tvMngr = null;
	private BroadcastService brdcstService = null;

	private Vector<SelectorListener> listeners = new Vector<SelectorListener>();

	class DelegaterChannelChange implements Runnable {
		public DelegaterChannelChange() {

		}

		public void run() {
			Enumeration<SelectorListener> e = listeners.elements();
			SelectorListener item;
			while (e.hasMoreElements()) {
				item = e.nextElement();
				item.onChannelSelect(getCurrentChannel());
			}
		}
	}

	class DelegaterSignalChange implements Runnable {
		private boolean hasSignal = false;

		public DelegaterSignalChange(boolean hasSignal) {
			this.hasSignal = hasSignal;
		}

		public void run() {
			Enumeration<SelectorListener> e = listeners.elements();
			SelectorListener item;
			while (e.hasMoreElements()) {
				item = e.nextElement();
				item.onSignalChange(hasSignal);
			}
		}
	}

	class DelegaterBlocked implements Runnable {
		private TVChannel ch;

		public DelegaterBlocked(TVChannel ch) {
			this.ch = ch;
		}

		public void run() {
			Enumeration<SelectorListener> e = listeners.elements();
			SelectorListener item;
			while (e.hasMoreElements()) {
				item = e.nextElement();
				item.onBlock(ch);
			}
		}
	}

	class DegegaterScrambled implements Runnable {
		private boolean hasScramble;
		private String state;

		public DegegaterScrambled(boolean hasScramble, String state) {
			this.hasScramble = hasScramble;
			this.state = state;
		}

		public void run() {
			Enumeration<SelectorListener> e = listeners.elements();
			SelectorListener item;
			while (e.hasMoreElements()) {
				item = e.nextElement();
				item.onScramble(hasScramble, state);
			}
		}
	}

	public TVChannel getCurrentChannel() {
		return currentCh;
	}

	class ScrambleState {
		String state;

		public ScrambleState() {
			this.state = SelectorListener.STATE_UNSCRAMBLED;
		}

		public ScrambleState(String state) {
			this.state = state;
		}

		public void setState(String state) {
			this.state = state;
			scrambleState = state;
			if (this.state.equals(SelectorListener.STATE_UNSCRAMBLED)) {
				setHasScramble(false);
				getHandler().post(new DegegaterScrambled(false, this.state));
			} else {
				setHasScramble(true);
				getHandler().post(new DegegaterScrambled(true, this.state));
			}
		}

		public String getState() {
			return this.state;
		}

		public synchronized void updateAudio() {
			if (state.equals(SelectorListener.STATE_AUDIO_AND_VIDEO_SCRAMBLED)) {
				setState(SelectorListener.STATE_VIDEO_CLEAR_AUDIO_SCRAMBLED);
			} else if (state
					.equals(SelectorListener.STATE_AUDIO_CLEAR_VIDEO_SCRAMBLED)
					|| state
							.equals(SelectorListener.STATE_AUDIO_NO_VIDEO_SCRAMBLED)) {
				setState(SelectorListener.STATE_UNSCRAMBLED);
			}
		}

		public synchronized void updateVideo() {
			if (state.equals(SelectorListener.STATE_AUDIO_AND_VIDEO_SCRAMBLED)) {
				setState(SelectorListener.STATE_AUDIO_CLEAR_VIDEO_SCRAMBLED);
			} else if (state
					.equals(SelectorListener.STATE_VIDEO_CLEAR_AUDIO_SCRAMBLED)
					|| state
							.equals(SelectorListener.STATE_VIDEO_NO_AUDIO_SCRAMBLED)) {
				setState(SelectorListener.STATE_UNSCRAMBLED);
			}
		}
	}

	private boolean selecting = false;

	/**
	 * check selecting channel state: selecting or completed
	 * 
	 * @return selecting channel state
	 */
	public boolean isSelecting() {
		return selecting;
	}

	TVChannelSelector(Context context) {
		super(context);

		if (!TVContent.dummyMode) {
			tvMngr = getTVMngr();
			brdcstService = (BroadcastService) tvMngr
					.getService(BroadcastService.BrdcstServiceName);
			IntentFilter filter = new IntentFilter();
			filter.addAction(BroadcastService.ACTION_SVCTX_NFY);
			context.registerReceiver(new BroadcastReceiver() {
				private ScrambleState state = new ScrambleState();

				public void onReceive(Context context, Intent intent) {
					String nfyCode = intent
							.getStringExtra(BroadcastService.SVCTX_NFY_CODE);

					if (nfyCode
							.equals(BroadcastService.SVCTX_NFY_SERVICE_CHANGED)) {
						selecting = false;
					}
					/*
					 * if (nfyCode
					 * .equals(BroadcastService.SVCTX_NFY_SERVICE_CHANGED)) { //
					 * We don't need post it because this is in context' //
					 * thread new DelegaterChannelChange().run(); } else
					 */if (nfyCode
							.equals(BroadcastService.SVCTX_NFY_WITH_SIGNAL)) {
						setHasSignal(true);
						// We don't need post it because this is in context'
						// thread
						new DelegaterSignalChange(true).run();
					} else if (nfyCode
							.equals(BroadcastService.SVCTX_NFY_NO_SIGNAL)) {
						setHasSignal(false);
						// We don't need post it because this is in context'
						// thread
						new DelegaterSignalChange(false).run();
					} else if (nfyCode
							.equals(BroadcastService.SVCTX_NFY_AUDIO_AND_VIDEO_SCRAMBLED)
							|| nfyCode
									.equals(BroadcastService.SVCTX_NFY_AUDIO_CLEAR_VIDEO_SCRAMBLED)
							|| nfyCode
									.equals(BroadcastService.SVCTX_NFY_AUDIO_NO_VIDEO_SCRAMBLED)
							|| nfyCode
									.equals(BroadcastService.SVCTX_NFY_VIDEO_CLEAR_AUDIO_SCRAMBLED)
							|| nfyCode
									.equals(BroadcastService.SVCTX_NFY_VIDEO_NO_AUDIO_SCRAMBLED)) {
						state.setState(nfyCode);
					} else if (nfyCode
							.equals(BroadcastService.SVCTX_NFY_AUDIO_UPDATED)) {
						state.updateAudio();
					} else if (nfyCode
							.equals(BroadcastService.SVCTX_NFY_VIDEO_UPDATED)) {
						state.updateVideo();
					} else if (nfyCode
							.equals(BroadcastService.SVCTX_NFY_NO_AUDIO_VIDEO)
							|| nfyCode
									.equals(BroadcastService.SVCTX_NFY_AUDIO_ONLY)
							|| nfyCode
									.equals(BroadcastService.SVCTX_NFY_VIDEO_ONLY)) {
						state.setState(SelectorListener.STATE_UNSCRAMBLED);
					}

				}

			}, filter);
		}
	}

	private boolean hasSignal;
	private boolean hasScramble;
	private String scrambleState = SelectorListener.STATE_UNSCRAMBLED;

	protected void init() {
		TVStorage st = getContent().getStorage();
		sp = st.getSharedPreferences();
		editor = st.getEditor();
	}

	protected synchronized void setHasSignal(boolean hasSignal) {
		this.hasSignal = hasSignal;
	}

	protected synchronized void setHasScramble(boolean hasScramble) {
		this.hasScramble = hasScramble;
	}

	/**
	 * Check whether has scrambled
	 * 
	 * @return
	 */
	public synchronized boolean hasScramble() {
		return hasScramble;
	}

	/**
	 * Check whether has signal
	 * 
	 * @return
	 */
	public synchronized boolean hasSignal() {
		return hasSignal;
	}

	public String getScrambleState() {
		return scrambleState;
	}

	protected void notifyChannelBlocked(TVChannel ch) {
		getHandler().post(new DelegaterBlocked(ch));
	}

	/**
	 * Select channel
	 * 
	 * @param ch
	 */
	public void select(TVChannel ch) {
		int num = 0;
		if (ch == currentCh){
			return;
		}
			synchronized (this) {
			if (currentCh != null) {
				num = currentCh.getChannelNum();
				if (ch.getRawInfo() instanceof AnalogChannelInfo) {
				editor.putInt(PRE_ANALOG_CHNUM, num);
				editor.commit();
				} else if (ch.getRawInfo() instanceof DvbChannelInfo) {
				editor.putInt(PRE_DIGITAL_CHNUM, num);
				editor.commit();
			}
			}
				currentCh = ch;
			selecting = true;
			}
			getContent().sendUpdate();
			getHandler().post(new DelegaterChannelChange());
	}

	/**
	 * Stop TV channel playing.
	 * 
	 * @deprecated
	 */
	public synchronized void stop() {
		if (!dummyMode) {

			synchronized (this) {
				try {
					// STOP here...
					brdcstService.syncStopService();
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// TODO:
				// Should we make currentCh be null.
				// currentCh = null;
			}
		} else {
			Toast toast = Toast.makeText(context, "I am stoping "
					+ currentCh.getChannelName(), 1000);
			toast.show();
		}
	}

	/**
	 * Select channel by channel number
	 * 
	 * @param num
	 */
	public boolean select(short num) {
		TVChannelManager cm = getContent().getChannelManager();
		TVChannel ch = cm.findChannelByNumber(num);
		if (ch != null) {
			select(ch);
			return true;
		}
		return false;
	}

	// public void onChannelUpdate() {
	// TVChannel curCh;
	// synchronized (this) {
	// curCh = currentCh;
	// }
	// if (curCh == null || !curCh.isValid()) {
	// stop();
	// }
	// }

	/**
	 * Select previous channel
	 * 
	 * @param void
	 * @deprecated
	 */

	public void preSelect() {
		channelUp();
	}

	/**
	 * Select next channel
	 * 
	 * @deprecated
	 * @param void
	 */

	public void nextSelect() {
		channelDown();
	}

	/**
	 * Select back channel
	 * 
	 * @param void
	 * @deprecated
	 */

	public void backSelect() {
		selectPrev();
	}

	/**
	 * Select previous channel
	 * 
	 * @param void
	 */

	public void selectPrev() {
		String inp = getContent().getInputManager().getCurrInputSource();
		TVChannelManager cm = getContent().getChannelManager();
		TVChannel ch = null;
		if (inp.equals("atv")) {
			ch = cm.findChannelByNumber((short) sp.getInt(PRE_ANALOG_CHNUM, 0));
		} else if (inp.equals("dtv")) {
			ch = cm
					.findChannelByNumber((short) sp
							.getInt(PRE_DIGITAL_CHNUM, 0));
		}
		if (ch != null) {
			select(ch);
		}
	}

	/**
	 * Channel up
	 */
	public void channelUp() {
		TVChannel curCh;
		synchronized (this) {
			curCh = currentCh;
		}
		if (curCh == null) {
			return;
		}
		TVChannelManager cm = getContent().getChannelManager();
		TVChannel nextCh = cm.nextChannel(curCh, true,
				TVChannelList.ChannelFilter.TVNotSkipFilter);

		select(nextCh);
	}

	/**
	 * Channel down
	 */
	public void channelDown() {
		TVChannel curCh;
		synchronized (this) {
			curCh = currentCh;
		}
		if (curCh == null) {
			return;
		}
		TVChannelManager cm = getContent().getChannelManager();
		TVChannel nextCh = cm.prevChannel(curCh, true,
				TVChannelList.ChannelFilter.TVNotSkipFilter);

		select(nextCh);
	}

	/**
	 *
	 */
	public void favoriteChannelDown() {
		TVChannel curCh;
		synchronized (this) {
			curCh = currentCh;
		}
		if (curCh == null) {
			return;
		}
		TVChannelManager cm = getContent().getChannelManager();
		TVChannel nextCh = cm.prevChannel(curCh, true,
				new TVChannelList.ChannelFilter() {
					public boolean filter(TVChannel channel) {
						return channel.isFavorite();
					}
				});

		select(nextCh);
	}

	/**
	 * 
	 */
	public void favoriteChannelUp() {
		TVChannel curCh;
		synchronized (this) {
			curCh = currentCh;
		}
		if (curCh == null) {
			return;
		}

		TVChannelManager cm = getContent().getChannelManager();
		TVChannel nextCh = cm.nextChannel(curCh, true,
				new TVChannelList.ChannelFilter() {
					public boolean filter(TVChannel channel) {
						return channel.isFavorite();
					}
				});

		select(nextCh);
	}

	public int finetuneStep = 10000;

	/**
	 * Set finetune step.
	 * 
	 * @param finetuneStep
	 */
	public void setDefaultFinetuneStep(int finetuneStep) {
		this.finetuneStep = finetuneStep;
	}

	/**
	 * Finetune, new frequency will be frequency + step
	 * 
	 * @param step
	 */
	public void finetune(int step) {
		TVChannel curCh;
		int freq;
		synchronized (this) {
			curCh = currentCh;
		}
		if (curCh == null) {
			return;
		}
		freq = curCh.getFreq();
		freq += step;
		try {
			if (brdcstService != null) {
				curCh.setFreq(freq);
				brdcstService.fineTune(curCh.getRawInfo());
			}
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Finetune lower
	 */
	public void finetuneLower() {
		finetune(-finetuneStep);
	}

	/**
	 * Finetune higher
	 */
	public void finetuneHigher() {
		finetune(finetuneStep);
	}

	public void exitFinetune() {
		TVChannel curCh;
		synchronized (this) {
			curCh = currentCh;
		}
		if (curCh == null) {
			return;
		}
		if (curCh != null) {
			try {
				brdcstService.exitFineTune(curCh.getRawInfo());
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set a channel to be start channel
	 * 
	 * @param num
	 */
	public void setStartChannel(short num) {
		TVStorage.Setting setting = getContent().getStorage().getSetting();
		setting.setStartChannel(num);
	}

	/**
	 * Set a channel to be start channel
	 * 
	 * @param num
	 */
	public TVChannel getStartChannel() {
		TVStorage.Setting setting = getContent().getStorage().getSetting();
		return getContent().getChannelManager().findChannelByNumber(
				setting.getStartChannelNumber());
	}

	/**
	 * Unset cahnnel. After this is called, current channel will be start
	 * channel
	 * 
	 * @deprecated
	 */
	public void unsetStartChannel() {
		TVStorage.Setting setting = getContent().getStorage().getSetting();
		setting.unsetStartChannelNumber();
	}

	/**
	 * Current channel is start channel?
	 * 
	 * @deprecated
	 * @return
	 */
	public boolean isCurrentAsStartChannel() {
		TVStorage.Setting setting = getContent().getStorage().getSetting();
		return setting.isCurrentAsStartChannel();
	}

	protected void onStart() {
	}

	protected void onStop() {
	}

	/**
	 * Add listener
	 * 
	 * @param listener
	 */
	public synchronized void addSelectorListener(SelectorListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove listener
	 * 
	 * @param listener
	 */
	public synchronized void removeSelectorListener(SelectorListener listener) {
		listeners.remove(listener);
	}

}
