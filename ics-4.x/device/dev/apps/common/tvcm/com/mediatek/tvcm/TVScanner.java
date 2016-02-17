package com.mediatek.tvcm;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.service.BroadcastService;
import com.mediatek.tv.service.ScanService;

/**
 * Scanner.<br>
 * 
 * @author mtk40063
 * 
 */
public class TVScanner extends TVComponent {

	/**
	 * Scanner status listener.
	 * 
	 * @author mtk40063
	 * 
	 */
	public interface ScannerListener {
		public static final int PROGRESS_MAX = 100;
		public static final int PROGRESS_MIN = 0;

		public static final int COMPLETE_OK = 0;
		public static final int COMPLETE_CANCEL = 1;
		public static final int COMPLETE_ERROR = 2;

		/**
		 * Scan progress notify
		 * 
		 * @param progress
		 *            0-100
		 * @param channels
		 *            number of channels
		 */
		public void onProgress(int progress, int channels);

		/**
		 * Frequence notify
		 * 
		 * @param freq
		 */
		public void onFrequence(int freq);

		/**
		 * Completed notify
		 * 
		 * @param completeValue
		 *            COMPLETE_OK COMPLETE_CANCEL COMPLETE_ERROR
		 */
		public void onCompleted(int completeValue);

	}

	public interface WaitScannerListener {
		void onWaitProgress(int currentFreq, int foundChNum, int finishData);
	}

	public void resumeWaitScan(int finishData) {
		scanService = (ScanService) this.getTVMngr().getService(
				ScanService.ScanServiceName);
		try {
			scanService.setAnalogScanUserOperationFinish(finishData);
		} catch (TVMException e) {
			e.printStackTrace();
		}
	}

	public synchronized void saveScanResult(boolean isSave) {
		ScanTask st = getCurrTask();
		if (st != null) {
			st.isSave = isSave;
		}
	}

	public final static int STATE_COMPLETE = 0;
	public final static int STATE_SCANNING = 1;

	// private ScannerListener listener;
	private TVManager tvMngr = null;
	// private ScanService scanService = null;

	private int state;
	private boolean autoClean = true;
	public static final String SCAN_OPTION_SCAN_MODE = "ScanMode";
	public static final String SCAN_OPTION_TV_SYSTEM = "TvAudioSystem";
	public static final String SCAN_OPTION_COLOR_SYSTEM = "ColorSystem";
	// for scan dvbc
	public static final String SCAN_OPTION_OPERATOR_NAME = "OperatorName";
	// public static final String SCAN_OPTION_SEARCH_MODE = "SearchMode";
	// public static final String SCAN_OPTION_SCAN_CONFIG = "ScanConfig";
	public static final String SCAN_OPTION_SCAN_EMOD = "ScanEMod";
	public static final String SCAN_OPTION_SYM_RATE = "SymRate";
	public static final String SCAN_OPTION_NETWOK_ID = "NetworkID";

	/**
	 * @deprecated
	 */
	public static final String SCAN_STANDARD_PAL = "pal";
	/**
	 * @deprecated
	 */
	public static final String SCAN_STANDARD_DVBC = "dvbc";

	/**
	 * This can be customized to different policy. This DOES NOT mean what
	 * standard, just a task name.
	 */
	public static final String SCAN_TYPE_PAL = "pal";
	public static final String SCAN_TYPE_PALT = "palt";
	public static final String SCAN_TYPE_PALC = "palc";
	public static final String SCAN_TYPE_PAL_WAIT = "pal_wait";

	public static final String SCAN_TYPE_DVBC = "dvbc";

	TVScanner(Context context) {
		super(context);
		state = STATE_COMPLETE;

		// addOption(SCAN_OPTION_SCAN_MODE, new ScanModeOption());
		addOption(SCAN_OPTION_TV_SYSTEM, new TvAudioSystemOption());
		addOption(SCAN_OPTION_COLOR_SYSTEM, new ColorSystemOption());
		addOption(SCAN_OPTION_OPERATOR_NAME, new OperatorNameOption());
		// addOption(SCAN_OPTION_SEARCH_MODE, new SearchModeOption());
		// addOption(SCAN_OPTION_SCAN_CONFIG, new ScanConfigOption());
		addOption(SCAN_OPTION_SCAN_EMOD, new ScanEModOption());
		addOption(SCAN_OPTION_SYM_RATE, new SymRateOption());
		addOption(SCAN_OPTION_NETWOK_ID, new NetworkIDOption());

	}

	void init() {
		// if (dummyMode) {
		// registerConcretScanner(SCAN_STANDARD_PAL, new DummyScanner(this,
		// "PAL"));
		// registerConcretScanner(SCAN_STANDARD_DVBC, new DummyScanner(this,
		// "DVBC"));
		// } else {
		// registerConcretScanner(SCAN_STANDARD_PAL, new PalScanner(this));
		// // registerConcretScanner(SCAN_STANDARD_DVBC, new
		// // DVBCScanner(this));
		// }
	}

	public synchronized boolean rangeScan(int freqFrom, int freqTo,
			ScannerListener listener) {
		return rangeScan(SCAN_STANDARD_PAL, freqFrom, freqTo, listener);
	}

	public synchronized boolean rangeScan(int freqFrom, int freqTo,
			ScannerListener listener, WaitScannerListener listener2) {
		ScanTask st = new PalScanner(this, SCAN_TYPE_PAL_WAIT, listener,
				listener2);
		st.setFreqRange(freqFrom, freqTo);
		addScanTask(st);
		return true;
	}

	/**
	 * Full scan, options is stored in that from Scanner.getOpton. Defualtly use
	 * Analog scan
	 * 
	 * @param listener
	 * @return
	 */
	public synchronized boolean scan(ScannerListener listener) {
		return scan(SCAN_TYPE_PAL, listener);
	}

	public synchronized boolean updateScan(ScannerListener listener) {
		return updateScan(SCAN_TYPE_PAL, listener);
	}

	public synchronized boolean waitScan(ScannerListener listener,
			WaitScannerListener waitListener) {
		ScanTask task = new PalScanner(this, SCAN_TYPE_PAL_WAIT, listener,
				waitListener);
		addScanTask(task);
		return true;
	}

	public boolean scan(String standard, ScannerListener listener) {
		return scanMultiple(new String[] { standard }, listener);
	}

	/**
	 * Inhereit this to support different scan task.
	 * 
	 * @param standard
	 * @param listener
	 * @return
	 */
	public ScanTask createTask(String standard, ScannerListener listener) {
		// TODO: Stupid:
		if (dummyMode) {
			return new DummyScanner(this, standard, listener);
		} else {
			if (standard.equals(SCAN_TYPE_DVBC)) {
				return new DVBCScanner(this, listener);
			} else if (standard.equals(SCAN_TYPE_PALT)) {
				return new PalScanner(this, SCAN_TYPE_PALT, listener);
			} else if (standard.equals(SCAN_TYPE_PALC)) {
				return new PalScanner(this, SCAN_TYPE_PALC, listener);
			} else if (standard.equals(SCAN_TYPE_PAL)) {
				return new PalScanner(this, SCAN_TYPE_PALT, listener);
			}
			return null;
		}

	}

	public boolean scanMultiple(String types[], ScannerListener[] listeners) {

		if (types.length != listeners.length) {
			throw new IllegalArgumentException();
		}

		// if (autoClean) {
		// TVChannelManager cm = getContent().getChannelManager();
		// cm.clear();
		// }
		synchronized (this) {
			for (int i = 0; i < types.length; i++) {
				ScanTask st = createTask(types[i], listeners[i]);
				addScanTask(st);
			}
		}
		return true;
	}

	public boolean scanMultiple(String types[], ScannerListener listener) {
		// if (autoClean) {
		// TVChannelManager cm = getContent().getChannelManager();
		// cm.clear();
		// }
		synchronized (this) {
			for (String type : types) {
				ScanTask st = createTask(type, listener);
				addScanTask(st);
			}
		}
		return true;
	}

	public boolean scanMultiple(ScanTask[] tasks) {
		// if (autoClean) {
		// TVChannelManager cm = getContent().getChannelManager();
		// cm.clear();
		// }
		synchronized (this) {
			for (ScanTask task : tasks) {
				addScanTask(task);
			}
		}
		return true;
	}

	/**
	 * Update scan, new channels will be added in list.
	 * 
	 * @param listener
	 * @return
	 */
	public synchronized boolean updateScan(String type, ScannerListener listener) {
		ScanTask st = createTask(type, listener);
		st.setUpdate(true);
		addScanTask(st);
		return true;

	}

	public synchronized boolean rangeScan(String type, int freqFrom,
			int freqTo, ScannerListener listener) {
		ScanTask st = createTask(type, listener);
		st.setFreqRange(freqFrom, freqTo);
		addScanTask(st);
		return true;
	}

	public synchronized boolean dtvRangeScan(int freqFrom, int freqTo,
			ScannerListener listener) {
		ScanTask st = createTask(SCAN_TYPE_DVBC, listener);
		st.dtvSetFreqRange(freqFrom, freqTo);
		addScanTask(st);
		return true;
	}

	LinkedList<ScanTask> taskQ = new LinkedList<ScanTask>();

	void addScanTask(ScanTask st) {
		TvLog("New Scan Task enter queue " + st.getType());
		taskQ.add(st);
		if (state == STATE_COMPLETE) {
			state = STATE_SCANNING;

			TvLog("Task of type " + st.getType() + " is the first, start scan");

			// We need to sync stop service before scanning, this is required by
			// TVMnanager.
			BroadcastService brdSrv = (BroadcastService) getTVMngr()
					.getService(BroadcastService.BrdcstServiceName);
			try {
				brdSrv.syncStopService();
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Ok, start first scan task.
			st.scan();
		}
	}

	ScanTask getCurrTask() {
		return taskQ.getFirst();
	}

	/**
	 * Cancel scan.
	 * 
	 * @return
	 */
	public synchronized boolean cancelScan() {
		if (state == STATE_SCANNING) {
			TvLog("Canceling scan , current scan task is "
					+ getCurrTask().getType());
			getCurrTask().cancel();
			for (ScanTask task : taskQ) {
				task.canceled = true;
			}
			// taskQ.clear();
		} else {
			return false;
		}
		return true;
	}

	void onCancelScan() {
		synchronized (this) {
			if (state == STATE_SCANNING) {
				TvLog("Task of " + getCurrTask().getType()
						+ " is cancelled.. Clear queue");
				while (!taskQ.isEmpty() && getCurrTask().canceled) {
					final ScannerListener usrListener = getCurrTask()
							.getListener();
					// We need to notify user that all of them are canceled.
		//			getHandler().post(new Runnable() {
		//				public void run() {
		//					usrListener
		//							.onCompleted(ScannerListener.COMPLETE_CANCEL);
		//				}
		//			});
					taskQ.remove();
				}
				// If we have newly added, just queue it..
				// taskQ.clear();
				if (taskQ.isEmpty()) {
					state = STATE_COMPLETE;
				} else {
					getCurrTask().scan();
				}
			}
		}

	}

	void onCompleteScan() {
		synchronized (this) {
			if (state == STATE_SCANNING) {
				TvLog("Task of " + getCurrTask().getType()
						+ " is over, goto next scan task");
				taskQ.remove();
				if (taskQ.isEmpty()) {
					TvLog("All scan completed, scan over");
					state = STATE_COMPLETE;
				} else {
					TvLog("Task over, goto next scan task of "
							+ getCurrTask().getType());
					getCurrTask().scan();
				}
			}
		}
	}

	/*
	 * synchronized void setState(int state) { this.state = state; }
	 */

	/**
	 * Get current state
	 * 
	 * @return
	 */
	public synchronized int getState() {
		return state;
	}

	private ScanService scanService = null;

	public int getDvbcRadioNum() {
		scanService = (ScanService) this.getTVMngr().getService(
				ScanService.ScanServiceName);
		if (scanService != null) {
			return scanService.getDvbcProgramTypeNumber(
					ScanService.SCAN_MODE_DVB_CABLE).getRadioNumber();
		}
		return 0;
	}

	public int getDvbcTvNum() {
		scanService = (ScanService) this.getTVMngr().getService(
				ScanService.ScanServiceName);
		if (scanService != null) {
			return scanService.getDvbcProgramTypeNumber(
					ScanService.SCAN_MODE_DVB_CABLE).getTvNumber();
		}
		return 0;
	}

	public int getDvbcAppNum() {
		scanService = (ScanService) getTVMngr().getService(
				ScanService.ScanServiceName);
		if (scanService != null) {
			return scanService.getDvbcProgramTypeNumber(
					ScanService.SCAN_MODE_DVB_CABLE).getAppNumber();
		}
		return 0;
	}

	public int getDvbcLowerFreq() {
		scanService = (ScanService) getTVMngr().getService(
				ScanService.ScanServiceName);
		if (scanService != null) {
			return scanService
					.getDvbcFreqRange(ScanService.SCAN_MODE_DVB_CABLE)
					.getLowerTunerFreqBound();
		}
		return 0;
	}

	public int getDvbcUpperFreq() {
		scanService = (ScanService) getTVMngr().getService(
				ScanService.ScanServiceName);
		if (scanService != null) {
			return scanService
					.getDvbcFreqRange(ScanService.SCAN_MODE_DVB_CABLE)
					.getUpperTunerFreqBound();
		} 
		return 0;
	}
}
