package com.mediatek.tvcommon;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.model.DtmbScanRF;
import com.mediatek.tv.service.BroadcastService;
import com.mediatek.tv.service.ChannelService;
import com.mediatek.tv.service.ScanService;
import android.content.Context;
import android.os.RemoteException;

public class TVScanner {
	final public static int STATE_COMPLETED = 0;
	final public static int STATE_SCANNING = 1;

	private static TVScanner scanner;
	private ScanTask task;
	private Hashtable<String, TVOptionRange<Integer>> optionTable = new Hashtable<String, TVOptionRange<Integer>>();

	public static final String SCAN_OPTION_TV_SYSTEM = "TvAudioSystem";
	public static final String SCAN_OPTION_COLOR_SYSTEM = "ColorSystem";
	public static final String SCAN_OPTION_OPERATOR_NAME = "OperatorName";
	public static final String SCAN_OPTION_SCAN_EMOD = "ScanEMod";
	public static final String SCAN_OPTION_SYM_RATE = "SymRate";
	public static final String SCAN_OPTION_NETWOK_ID = "NetworkID";

	protected final Context mContext;
	protected ScanService scanService;
	protected ChannelService channelService;
	protected BroadcastService brdSrv;
	protected TVConfigurer cfg;

	private DtmbScanRF curDtmbScanRF = null;

	public DtmbScanRF getCurDtmbScanRF() {
		return curDtmbScanRF;
	}

	private TVScanner(Context context) {
		mContext = context;
		TVManager tvManager = TVManager.getInstance(mContext);
		scanService = (ScanService) tvManager
				.getService(ScanService.ScanServiceName);
		channelService = (ChannelService) tvManager
				.getService(ChannelService.ChannelServiceName);
		brdSrv = (BroadcastService) tvManager
				.getService(BroadcastService.BrdcstServiceName);
		cfg = TVConfigurer.getInstance(mContext);
		init();
	}

	private void init() {
		optionTable.put(SCAN_OPTION_TV_SYSTEM, new TvAudioSystemOption());
		optionTable.put(SCAN_OPTION_COLOR_SYSTEM, new ColorSystemOption());
		optionTable.put(SCAN_OPTION_OPERATOR_NAME, new OperatorNameOption());
		optionTable.put(SCAN_OPTION_SCAN_EMOD, new ScanEModOption());
		optionTable.put(SCAN_OPTION_SYM_RATE, new SymRateOption());
		optionTable.put(SCAN_OPTION_NETWOK_ID, new NetworkIDOption());
	}

	public final TVOption<?> getOption(String name) {
		return optionTable.get(name);
	}

	public interface ScannerListener {
		public static final int PROGRESS_MAX = 100;
		public static final int PROGRESS_MIN = 0;

		public static final int COMPLETE_OK = 0;
		public static final int COMPLETE_CANCEL = 1;
		public static final int COMPLETE_ERROR = 2;

		public void onProgress(int progress, int channels);

		public void onFrequence(int freq);

		public void onCompleted(int completeValue);
	}

	public static TVScanner getInstance(Context context) {
		if (scanner == null) {
			scanner = new TVScanner(context);
		}
		return scanner;
	}

	/**
	 * Scan all analog TV channels.
	 * 
	 * @param listener
	 *            Listener which display scan process.
	 */
	public synchronized void atvScan(ScannerListener listener) {
		TVCommonNative.TVLog("ATV Full Scan");
		task = new PalScanner(this);
		task.scan(listener);
	}

	/**
	 * Scan analog TV channels range from frequency {@code freqStart} to
	 * {@code freqEnd}.
	 * 
	 * @param freqStart
	 *            The start frequency.
	 * @param freqEnd
	 *            The end frequency.
	 * @param listener
	 *            Listener which display scan process.
	 */
	public synchronized void atvRangeScan(int freqStart, int freqEnd,
			ScannerListener listener) {
		TVCommonNative.TVLog("ATV Range Scan");
		if (freqStart < 0) {
			throw new IllegalArgumentException();
		}
		PalScanner scanner = new PalScanner(this);
		task = scanner;
		scanner.setFreqRange(freqStart, freqEnd);
		scanner.scan(listener);
	}

	/**
	 * Search the new analog TV channel which current channel list do not
	 * include.
	 * 
	 * @param listener
	 *            Listener which display scan process.
	 */
	public synchronized void atvUpdateScan(ScannerListener listener) {
		TVCommonNative.TVLog("ATV Update Scan");
		PalScanner scanner = new PalScanner(this);
		task = scanner;
		scanner.setUpdate(true);
		scanner.scan(listener);
	}

	/**
	 * Scan all digital TV channels.
	 * 
	 * @param listener
	 *            Listener which display scan process.
	 */
	public synchronized void dtvScan(ScannerListener listener) {
		TVCommonNative.TVLog("DTV Full Scan");
		task = new DTVScanner(this);
		task.scan(listener);
	}

	/**
	 * Scan DVB-C channel in specified frequency {@code freq}, this method must
	 * be invoked in {@link ITVCommon#TUNNER_CABLE} mode.
	 * 
	 * @param freq
	 *            The specified frequency.
	 */
	public synchronized void dvbcSingleRFScan(int freq, ScannerListener listener)
			throws IllegalAccessException {
		TVCommonNative.TVLog("DVB-C Single RF Scan");
		DTVScanner scanner = new DTVScanner(this);
		task = scanner;
		if (task.getRawScanMode() != ScanService.SCAN_MODE_DVB_CABLE) {
			throw new IllegalAccessException(
					"Must be invoked in dvb-cable mode.");
		}
		scanner.setFreq(freq);
		scanner.scan(listener);
	}
	/**
	 * Scan DVB-C channel from frequency {@code freqFrom} to {@code freqTo}. this method must
	 * be invoked in {@link ITVCommon#TUNNER_CABLE} mode.
	 * 
	 * @param freqFrom
	 *            The start frequency.
	 * @param freqTo
	 *            The end frequency.
	 * @param listener
	 *            Listener which display scan process.
	 */
	
	public synchronized void dvbcRangeScan(int freqFrom, int freqTo,
			ScannerListener listener) {
		DTVScanner scanner = new DTVScanner(this);
		task = scanner;
		scanner.setFreq(freqFrom);
		scanner.setIndex(freqTo);
		scanner.scan(listener);
	}
	
	/**
	 * Scan DVB-C channel with specified {@code scanEMod}, {@code sysRate} and
	 * {@code freq}.
	 * 
	 * @param scanEMod
	 *            The specified scan mode.
	 * @param sysRate
	 *            The specified system rate.
	 * @param freq
	 *            The specified system frequency.
	 * @param listener
	 *            Listener which display scan process.
	 */
	public synchronized void dvbcSingleRFScan(int scanEMod, int sysRate,
			int freq, ScannerListener listener) throws IllegalAccessException {
		TVCommonNative.TVLog("DVB-C Single RF Scan");
		TVOptionRange<Integer> option =  optionTable.get(SCAN_OPTION_SCAN_EMOD);
		if(option !=null){
			ScanEModOption eModOpt = (ScanEModOption) option;
			eModOpt.set(scanEMod);
		}
		option = optionTable.get(TVScanner.SCAN_OPTION_SYM_RATE);
		if(option !=null){
			SymRateOption symRateOpt = (SymRateOption) option;
			symRateOpt.set(sysRate);
		}
		
		DTVScanner scanner = new DTVScanner(this);
		task = scanner;
		if (task.getRawScanMode() != ScanService.SCAN_MODE_DVB_CABLE) {
			throw new IllegalAccessException(
					"Must be invoked in dvb-cable mode.");
		}
		scanner.setFreq(freq);
		scanner.scan(listener);
	}

	private HashMap<String, DtmbScanRF> rfMap = new HashMap<String, DtmbScanRF>();

	/**
	 * Scan DTMB TV channel in specified RF Channel.
	 * 
	 * @param rfChannel
	 *            The specified channel.
	 * @param listener
	 *            Listener which display scan process.
	 */
	public synchronized void dtmbSingleRFScan(String rfChannel,
			ScannerListener listener) throws IllegalAccessException {
		TVCommonNative.TVLog("DTMB Single RF Scan");
		DtmbScanRF rf = rfMap.get(rfChannel);
		DTVScanner scanner = new DTVScanner(this);
		task = scanner;
		if (task.getRawScanMode() != ScanService.SCAN_MODE_DTMB_AIR) {
			throw new IllegalAccessException(
					"Must be invoked in dtmb-air mode.");
		}
		if(rf !=null){
			scanner.setFreq(rf.getRFScanFrequency());
			scanner.setIndex(rf.getRFScanIndex());
			scanner.scan(listener);
		}
	}

	/**
	 * @return The current RF channel.
	 */
	public String getCurrentDtmbRFChannel() {
		ITVCommon tvCommon = TVCommonNative.getDefault(mContext);
		DtmbScanRF rf = null;
		TVChannel ch = null;
		String rfChannel = null;
		try {
			if(tvCommon !=null)
			ch = tvCommon.getCurrentChannel();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (ch != null) {
			rf = scanService.getCurrentDtmbScanRF(
					ScanService.SCAN_MODE_DTMB_AIR, ch.rawInfo.getChannelId());
			if (rf != null) {
				curDtmbScanRF = rf;
				rfChannel = rf.getRFChannel();
				rfMap.put(rfChannel, rf);
			}
		}

		if (rf == null) {
			rfChannel = getFirstDtmbRFChannel();
		}

		return rfChannel;
	}

	/**
	 * @return the first RF channel.
	 */
	public synchronized String getFirstDtmbRFChannel() {
		DtmbScanRF rf = scanService
				.getFirstDtmbScanRF(ScanService.SCAN_MODE_DTMB_AIR);
		String rfChannel = null;
		if (rf != null) {
			rfChannel = rf.getRFChannel();
			rfMap.put(rfChannel, rf);
			curDtmbScanRF = rf;
		}
		return rfChannel;
	}

	/**
	 * @return the last RF channel.
	 */
	public synchronized String getLastDtmbRFChannel() {
		DtmbScanRF rf = scanService
				.getLastDtmbScanRF(ScanService.SCAN_MODE_DTMB_AIR);
		String rfChannel = null;
		if (rf != null) {
			rfChannel = rf.getRFChannel();
			rfMap.put(rfChannel, rf);
			curDtmbScanRF = rf;
		}
		return rfChannel;
	}

	/**
	 * @return the next RF channel.
	 */
	public synchronized String getNextDtmbRFChannel() {
		DtmbScanRF rf = scanService.getNextDtmbScanRF(
				ScanService.SCAN_MODE_DTMB_AIR, curDtmbScanRF);
		String rfChannel = null;
		if (rf != null) {
			rfChannel = rf.getRFChannel();
			rfMap.put(rfChannel, rf);
			curDtmbScanRF = rf;
		} else {
			rfChannel = getFirstDtmbRFChannel();
		}
		return rfChannel;
	}

	/**
	 * @return the previous RF channel.
	 */
	public synchronized String getPrevDtmbRFChannel() {
		DtmbScanRF rf = scanService.getPrevDtmbScanRF(
				ScanService.SCAN_MODE_DTMB_AIR, curDtmbScanRF);
		String rfChannel = null;
		if (rf != null) {
			rfChannel = rf.getRFChannel();
			rfMap.put(rfChannel, rf);
			curDtmbScanRF = rf;
		} else {
			rfChannel = getFirstDtmbRFChannel();
		}
		return rfChannel;
	}

	public int getDvbcRadioNum() {
		return scanService.getDvbcProgramTypeNumber(
				ScanService.SCAN_MODE_DVB_CABLE).getRadioNumber();
	}

	public int getDvbcTvNum() {
		return scanService.getDvbcProgramTypeNumber(
				ScanService.SCAN_MODE_DVB_CABLE).getTvNumber();
	}

	public int getDvbcAppNum() {
		return scanService.getDvbcProgramTypeNumber(
				ScanService.SCAN_MODE_DVB_CABLE).getAppNumber();
	}

	public int getDvbcLowerFreq() {
		int lowerFreq = 0;
		if(null != scanService){
			lowerFreq = scanService.getDvbcFreqRange(ScanService.SCAN_MODE_DVB_CABLE)
				.getLowerTunerFreqBound();
		}
		return lowerFreq;
	}

	public int getDvbcUpperFreq() {
		int upperFreq = 0;
		if(null != scanService){
			upperFreq = scanService.getDvbcFreqRange(ScanService.SCAN_MODE_DVB_CABLE)
				.getUpperTunerFreqBound();
		}
		return upperFreq;
	}

	
	public int getDvbcMainFrequence(){
		if (null != scanService) {
			int mainFreq = scanService.getDvbcMainFrequence(
								ScanService.SCAN_MODE_DVB_CABLE).getMainFrequence();
			TVCommonNative.TVLog("mainFreq = " + mainFreq);
			return mainFreq;
		} else {
			TVCommonNative.TVLog("scanService == null");
		}
		return 0;
	}
	
	public int getDvbcTsCount(){
		if (null != scanService) {
			int tsCount = scanService.getDvbcMainFrequence(
							ScanService.SCAN_MODE_DVB_CABLE).getTsCount();
			TVCommonNative.TVLog("tsCount = " + tsCount);
			return tsCount;
		} else {
			TVCommonNative.TVLog("scanService == null");
		}
		return 0;
	}
	
	public int getDvbcNitVersion(){
		if (null != scanService) {
			int nitVer = scanService.getDvbcMainFrequence(
							ScanService.SCAN_MODE_DVB_CABLE).getNitVersion();
			TVCommonNative.TVLog("nitVer = " + nitVer);
			return nitVer;
		} else {
			TVCommonNative.TVLog("scanService == null");
		}
		return 0;
	}


	public int getDtmbLowerFreq() {
		return scanService.getDtmbFreqRange(ScanService.SCAN_MODE_DTMB_AIR)
				.getLowerTunerFreqBound();
	}

	public int getDtmbUpperFreq() {
		return scanService.getDtmbFreqRange(ScanService.SCAN_MODE_DTMB_AIR)
				.getUpperTunerFreqBound();
	}

	/**
	 * Preset ATV channels.
	 */
	public synchronized void preSetAnalogChannels(
			List<PalPreSetChannel> channels) {
		TVCommonNative.TVLog("Preset analog channels.");
		PalScanner scanner = new PalScanner(this);
		task = scanner;
		scanner.preSetAnalogChannels(channels);
	}

	/**
	 * Preset cable digital channels.
	 */
	public synchronized void preSetCableDigitalChannels(List<DvbPreSetChannel> channels) {
		DTVScanner scanner = new DTVScanner(this);
		if (null != scanner) {
			TVCommonNative.TVLog("preset cable digital chs");
			scanner.preSetCableChs(channels);
		} else {
			TVCommonNative.TVLog("fail: scanner == null");
		}
	}


	/**
	 * Preset air digital channels.
	 */
	public synchronized void preSetAirDigitalChannels(List<DvbPreSetChannel> channels) {
		DTVScanner scanner = new DTVScanner(this);
		if (null != scanner) {
			TVCommonNative.TVLog("preset cable digital chs");
			scanner.preSetAirChs(channels);
		} else {
			TVCommonNative.TVLog("fail: scanner == null");
		}
	}
	

	/**
	 * Cancel the current scan progress.
	 */
	public synchronized void cancelScan() {
		if (task != null) {
			task.cancel();
		}
	}

	/**
	 * @return the current scan state.
	 */
	public int getScanState() {
		if (task != null) {
			return task.getState();
		}
		return STATE_COMPLETED;
	}
}
