package com.mediatekk.tvcommon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.AnalogChannelInfo;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.DvbChannelInfo;
import com.mediatek.tv.service.BroadcastService;
import com.mediatek.tv.service.ChannelService;
import com.mediatek.tv.service.ComponentService;
import com.mediatek.tv.service.ConfigService;
import com.mediatek.tv.service.IChannelNotify;
import com.mediatek.tv.service.InputService;
import com.mediatek.tv.service.ChannelService.ChannelOperator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.RemoteException;
import android.os.SystemClock;

class TVCommonService extends TVCommonNative implements Handler.Callback,
		MessageQueue.IdleHandler {

	Looper mLooper;
	MessageQueue mQueue;
	Handler handler;

	private volatile HashMap<String, ChannelQueue> channelQueueMap = new HashMap<String, ChannelQueue>();
	private TVManager tvMgr;
	private InputService inputSrv;
	private ChannelService rawChSrv;
	private BroadcastService brdcstSrv;
	private TVStorage storage;
	private TVConfigurer configurer;
	private ComponentService compSrv;

	private static final String ANALOG_TV = "atv";
	private static final String DIGITAL_TV = "dtv";

	private HashMap<String, ChannelQueue> channelSet = new HashMap<String, ChannelQueue>();

	final private List<TVInput> inputs = new ArrayList<TVInput>();
	final private List<TVOutput> outputs = new ArrayList<TVOutput>();

	private static final String START_CHANNEL_PREFIX = "start_channel_";

	Comparator<TVChannel> mComparator = new Comparator<TVChannel>() {

		@Override
		public int compare(TVChannel lhs, TVChannel rhs) {
			if (lhs.getChannelNum() < rhs.getChannelNum()) {
				return -1;
			} else if (lhs.getChannelNum() > rhs.getChannelNum()) {
				return 1;
			} else {
				return 0;
			}
		}
	};

    private static TVCommonService sTVCommonService = null;

    static class TVCommonServiceThread extends Thread {
        TVCommonService mTVCommonService;

        public TVCommonServiceThread() {
            super("TVCommonService");
        }

        public void run() {
            Looper.prepare();
            TVCommonService tvcs = new TVCommonService();
            synchronized (this) {
                mTVCommonService = tvcs;
                notifyAll();
            }
            Looper.loop();
        }
    }

    public static TVCommonService getInstance() {
        if (sTVCommonService != null) {
            return sTVCommonService;
        }
        TVCommonServiceThread tvcst = new TVCommonServiceThread();
        tvcst.start();
        synchronized (tvcst) {
            while (tvcst.mTVCommonService == null) {
                try {
                    tvcst.wait();
                } catch (Throwable e) {
                }
            }
        }
        sTVCommonService = tvcst.mTVCommonService;
        return sTVCommonService;
    }

	private TVCommonService() {

		super();
		tvMgr = TVManager.getInstance(getContext());
		rawChSrv = (ChannelService) tvMgr
				.getService(ChannelService.ChannelServiceName);
		inputSrv = (InputService) tvMgr
				.getService(InputService.InputServiceName);
		brdcstSrv = (BroadcastService) tvMgr
				.getService(BroadcastService.BrdcstServiceName);
		compSrv = (ComponentService) tvMgr
				.getService(ComponentService.CompServiceName);

		mLooper = Looper.getMainLooper();
		mQueue = Looper.myQueue();

		mQueue.addIdleHandler(this);
		handler = new Handler(mLooper, this);
		storage = TVStorage.getInstance(getContext());
		configurer = TVConfigurer.getInstance(getContext());

		init();

		IntentFilter shutdownFilter = new IntentFilter();
		shutdownFilter.addAction(Intent.ACTION_SHUTDOWN);
		shutdownFilter.setPriority(IntentFilter.SYSTEM_LOW_PRIORITY);
		getContext().registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				uninit();
			}
		}, shutdownFilter);

		IntentFilter scanFilter = new IntentFilter(SCAN_COMPLETE_ACTION);
		getContext().registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int type = intent.getExtras().getInt("scan_type");
				TVLog("Scan completed:" + (type == 0 ? ANALOG_TV : DIGITAL_TV));
				// Scan completed, remap ChannelInfo to TVChannel objects.
				update(type);
				notifyUpdate(UpdateReason.SCAN_COMPLETE, "");
			}
		}, scanFilter);
		
		if(rawChSrv != null)
		rawChSrv.addListener(new IChannelNotify() {
			@Override
			public void notifyChannelUpdated(int condition, int reason, int data) {
				// The flag 1 << 1 and 1 << 2 mean add a new channel and delete
				// a channel.
				if (condition == 2 && (reason & (1 << 1 | 1 << 2)) != 0) {
					TVLog("Got notification that channels update.");
					// TODO
					//update(ScanTask.TYPE_DTV);
					//notifyUpdate(UpdateReason.ICH_NOTIFY, "");
				}
			}
		});

	}

	TVOutput mCurrentOutput;

	private void init(){
		// Initialise TV input source and output source.
		if(inputSrv == null)
			return;
		
		for (String type : inputTypes) {
			String[] hwName = inputSrv.getDesignatedTypeInputsString(type);
			if (hwName == null) {
				TVLog("Input source type:" + type
						+ " have no physical input source.");
				continue;
			}
			if (type.equals(INPUT_TYPE_TV)) {
				// Seperate a physical tv input source to atv and dtv
				inputs.add(new TVInput(type, ANALOG_TV, hwName[0]));
				inputs.add(new TVInput(type, DIGITAL_TV, hwName[0]));
				TVLog("Seperate input source:" + hwName[0] + " to atv and dtv");
			} else {
				for (String name : hwName) {
					inputs.add(new TVInput(type, name));
					TVLog("Initilize input source:" + name);
				}
			}
		}

		for (String outputName : inputSrv.getScreenOutputs()) {
			outputs.add(new TVOutput(outputName));
			TVLog("Initilize output:" + outputName);
		}

		mCurrentOutput = getTVOutput("main");
		update();

	}

	private void uninit() {
		TVLog("Shutting down TV .... ");
		ConfigService cfgSrv = (ConfigService) tvMgr
				.getService(ConfigService.ConfigServiceName);
		try {
			// ???
			if(cfgSrv != null)
			cfgSrv.powerOff();
		} catch (TVMException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Each input source map to a {@code TVInput} object.
	 */
	final class TVInput {

		private final int cfgGrpForType[] = { TVConfigurer.CFG_GRP_ATV,
				TVConfigurer.CFG_GRP_AV, TVConfigurer.CFG_GRP_AV,
				TVConfigurer.CFG_GRP_COMPONENT, TVConfigurer.CFG_GRP_AV,
				TVConfigurer.CFG_GRP_HDMI, TVConfigurer.CFG_GRP_VGA };

		private String type;
		private String name;
		private String hwName;
		private int cfgIndex;
		boolean blocked;
		private boolean tempUnblocked = false;

		private static final String INPUT_BLOCK_PREFIX = "inp_block_";

		TVInput(String type, String name, String hwName) {
			this.type = type;
			this.name = name;
			this.hwName = hwName;
			cfgIndex = getCfgGrpIdxForType(type);
			blocked = storage.getBoolean(INPUT_BLOCK_PREFIX + name, false);
		}

		TVInput(String type, String name) {
			this(type, name, name);
		}

		boolean isBlocked() {
			return blocked && !tempUnblocked;
		}

		boolean isPhysicalBlocked() {
			return blocked;
		}

		void tempUnblocked() {
			if (blocked) {
				tempUnblocked = true;
			}
		}

		boolean isTempUnblocked() {
			return tempUnblocked;
		}

		void setBlocked(boolean blocked) {
			this.blocked = blocked;
			storage.setBoolean(INPUT_BLOCK_PREFIX + name, blocked);
		}

		String getType() {
			return type;
		}

		String getName() {
			return name;
		}

		String getHWName() {
			return hwName;
		}

		int getCfgGrpIdxForType(String type) {
			if (type == null) {
				return 0;
			}
			int cfgIndex = 0;
			for (int i = 0; i < inputTypes.length; i++) {
				if (type.equals(inputTypes[i])) {
					if (type.equals(INPUT_TYPE_TV)) {
						cfgIndex = name.equals(ANALOG_TV) ? TVConfigurer.CFG_GRP_ATV
								: TVConfigurer.CFG_GRP_DTV;
					} else {
						cfgIndex = cfgGrpForType[i];
					}
					return cfgIndex;
				}
			}
			return 0;
		}

	}

	/**
	 * Each screen output map to a {@code TVOutput} object.
	 */
	class TVOutput {
		private static final String START_INP_PREFIX = "start_input_";

		String name;
		TVInput input;

		TVOutput(String name) {
			this.name = name;
			String inpName = storage.getString(START_INP_PREFIX + name,
					ANALOG_TV);
			input = TVCommonService.this.getTVInput(inpName);
		}

		void connect(TVInput input) {
			this.input = input;
			configurer.setGroup(input.cfgIndex);
			storage.setString(START_INP_PREFIX + name, input.getName());
		}

		String getName() {
			return name;
		}

		TVInput getInput() {
			return input;
		}
	}

	synchronized void update() {
		long startTime = SystemClock.elapsedRealtime();

		List<ChannelInfo> cableRawList = null;
		try {
			// Raw channel Info stored in DB_CABLE (For analog and digital)
			cableRawList = rawChSrv.getChannelList(ChannelCommon.DB_CABEL);
		} catch (TVMException e) {
			e.printStackTrace();
		}

		List<ChannelInfo> airRawList = null;
		try {
			// Raw channel Info stored in DB_AIR 
			airRawList = rawChSrv.getChannelList(ChannelCommon.DB_AIR);
		} catch (TVMException e) {
			e.printStackTrace();
		}

		List<TVChannel> analogs  = new ArrayList<TVChannel>();
		List<TVChannel> airChs   = new ArrayList<TVChannel>();
		List<TVChannel> cableChs = new ArrayList<TVChannel>();

		TVChannel ch = null;
		if(null != cableRawList) {
			for (ChannelInfo info : cableRawList) {
				ch = new TVChannel(info);
				if (info instanceof AnalogChannelInfo) {
					analogs.add(ch);
				} else if (info instanceof DvbChannelInfo) {
					cableChs.add(ch);
				}
			}
		} else {
			TVLog("null == cableRawList");
		}

		ch = null;
		if(null != airRawList) {
			for (ChannelInfo info : airRawList) {
				ch = new TVChannel(info);
				if (info instanceof DvbChannelInfo) {
					airChs.add(ch);
				}
			}
		} else {
			TVLog("null == airRawList");
		}

		Collections.sort(analogs,  mComparator);
		Collections.sort(cableChs, mComparator);
		Collections.sort(airChs,   mComparator);
		
		TVLog("ATV got " + analogs.size() + " channels");
		TVLog("Cable DTV got " + cableChs.size() + " channels");
		TVLog("Air DTV got " + airChs.size()   + " channels");
		
		synchronized (channelQueueMap) {
			ChannelQueue queue0 = new ChannelQueue(analogs);
			queue0.head();
			channelSet.put(ANALOG_TV + ConfigType.BS_SRC_CABLE, queue0);
			channelQueueMap.put(ANALOG_TV, queue0);

			ChannelQueue queue1 = new ChannelQueue(cableChs);
			queue1.head();
			channelSet.put(DIGITAL_TV + ConfigType.BS_SRC_CABLE, queue1);

			ChannelQueue queue2 = new ChannelQueue(airChs);
			queue2.head();
			channelSet.put(DIGITAL_TV + ConfigType.BS_SRC_AIR, queue2);

			int tunerMode = getTunnerModeForDig();
			TVLog("BS_SRC: " + tunerMode);
			if (ConfigType.BS_SRC_AIR == tunerMode) {
				channelQueueMap.put(DIGITAL_TV, queue2);
			} else {
				channelQueueMap.put(DIGITAL_TV, queue1);
			}
			
		}
		
		// resume TV status
		int anaNum = storage.getInt(START_CHANNEL_PREFIX + ChannelCommon.DB_CABEL + "_" + ANALOG_TV, -1);
		TVLog("get store info: "  + START_CHANNEL_PREFIX + ChannelCommon.DB_CABEL + "_" + ANALOG_TV + ", ch_num: " + anaNum);
		if (anaNum < 0) {
			channelQueueMap.get(ANALOG_TV).head();
		} else {
			channelQueueMap.get(ANALOG_TV).findChannelByNumber(anaNum);
		}

		int digNum = storage.getInt(START_CHANNEL_PREFIX + getDigitalNativeDBName() + "_" + DIGITAL_TV, -1);
		TVLog("get store info: " + START_CHANNEL_PREFIX  + getDigitalNativeDBName() + "_" + DIGITAL_TV + ", ch_num: " + digNum);
		if (digNum < 0) {
			channelQueueMap.get(DIGITAL_TV).head();
		} else {
			channelQueueMap.get(DIGITAL_TV).findChannelByNumber(digNum);
		}

		/* original flow FYI
		for (String key : channelQueueMap.keySet()) {
			int num = storage.getInt(START_CHANNEL_PREFIX + getNativeDBName()
					+ "_" + key, -1);
			TVLog("get store info: " + START_CHANNEL_PREFIX + getNativeDBName() + 
						"_" + key + ", ch_num: " + num);
			if (num < 0) {
				channelQueueMap.get(key).head();
			} else {
				channelQueueMap.get(key).findChannelByNumber(num);
			}
		}
		*/

		sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
		TVLog("Update operation spend " + (SystemClock.elapsedRealtime() - startTime) + " miliseconds");
	}

	void update(int type) {

		List<ChannelInfo> cableRawList = null;
		try {
			// Raw channel Info stored in DB_CABLE (For analog and digital)
			cableRawList = rawChSrv.getChannelList(ChannelCommon.DB_CABEL);
		} catch (TVMException e) {
			e.printStackTrace();
		}

		List<ChannelInfo> airRawList = null;
		try {
			// Raw channel Info stored in DB_AIR 
			airRawList = rawChSrv.getChannelList(ChannelCommon.DB_AIR);
		} catch (TVMException e) {
			e.printStackTrace();
		}

		List<TVChannel> analogs  = new ArrayList<TVChannel>();
		List<TVChannel> airChs   = new ArrayList<TVChannel>();
		List<TVChannel> cableChs = new ArrayList<TVChannel>();

		TVChannel ch = null;
		switch (type) {
		case ScanTask.TYPE_ATV: {
			TVLog("Update ATV");
			if (null != cableRawList) {
				for (ChannelInfo info : cableRawList) {
					if (info instanceof AnalogChannelInfo) {
						ch = new TVChannel(info);
						analogs.add(ch);
					}
				}
			} else {
				TVLog("null == cableRawList");
			}
			break;
		}
		case ScanTask.TYPE_DTV: {
			TVLog("Update DTV");
			if (null != airRawList) {
				for (ChannelInfo info : airRawList) {
					if (info instanceof DvbChannelInfo) {
						ch = new TVChannel(info);
						airChs.add(ch);
					}
				}
			} else {
				TVLog("null == airRawList");
			}

			if (null != cableRawList) { 
				for (ChannelInfo info : cableRawList) {
					if (info instanceof DvbChannelInfo) {
						ch = new TVChannel(info);
						cableChs.add(ch);
					}
				}
			} else {
				TVLog("null == cableRawList");
			}
			break;
		}

		default:
			TVLog("Notice: Update unknown " + type);
			break;
		}

		Collections.sort(analogs,  mComparator);
		Collections.sort(cableChs, mComparator);
		Collections.sort(airChs,   mComparator);
		
		synchronized (channelQueueMap) {
			/* original flow FYI
			ChannelQueue queue = new ChannelQueue(channels);
			queue.head();
			String tv = type == 0 ? ANALOG_TV : DIGITAL_TV;
			channelQueueMap.put(tv, queue);
			channelSet.put(tv + getTunnerMode(), queue);
			*/
			
			if (ScanTask.TYPE_ATV == type) {
				ChannelQueue queue0 = new ChannelQueue(analogs);
				queue0.head();
				channelSet.put(ANALOG_TV + ConfigType.BS_SRC_CABLE, queue0);
				channelQueueMap.put(ANALOG_TV, queue0);
			} 
			else if (ScanTask.TYPE_DTV == type) {
				
				ChannelQueue queue1 = new ChannelQueue(cableChs);
				queue1.head();
				channelSet.put(DIGITAL_TV + ConfigType.BS_SRC_CABLE, queue1);
				
				ChannelQueue queue2 = new ChannelQueue(airChs);
				queue2.head();
				channelSet.put(DIGITAL_TV + ConfigType.BS_SRC_AIR,   queue2);
			
				int tunerMode = getTunnerModeForDig();
				TVLog("BS_SRC: " + tunerMode);
				if (ConfigType.BS_SRC_AIR == tunerMode) {
					channelQueueMap.put(DIGITAL_TV, queue2);
				} else {
					channelQueueMap.put(DIGITAL_TV, queue1);
				}
			}
		}
	}

	private void stopOutput(String outputName, boolean mute) {
		try {
			inputSrv.stopDesignateOutput(outputName, true);
		} catch (TVMException e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		TVOption<Integer> blueOpt = (TVOption<Integer>) configurer
				.getOption(ConfigType.CFG_BLUE_SCREEN);
		if (blueOpt != null && blueOpt.get() == ConfigType.COMMON_ON) {
			try {
				// Ugly, WTF...
				brdcstSrv.setVideoBlueMute(true, mute);
			} catch (TVMException e) {
				e.printStackTrace();
			}
		} else {
			try {
				brdcstSrv.setVideoBlueMute(false, false);
			} catch (TVMException e) {
				e.printStackTrace();
			}
		}
	}

	synchronized void updatePipeLine(TVOutput output) {
		TVInput input = output.getInput();
		if (null != input) {
			/* set group first */
			configurer.setGroup(input.cfgIndex);
			
			TVChannel ch = getCurrentChannel();
			if (ch != null) {
				TVLog("prepare store info: " + START_CHANNEL_PREFIX + getNativeDBName() + 
							"_" + input.getName() + ", ch_num: " + ch.getChannelNum());
				storage.setInt(START_CHANNEL_PREFIX + getNativeDBName() + "_"
						+ input.getName(), ch.getChannelNum());
				handler.post(new DelegaterChannelChange(ch));
			}

			TVLog("Bind output:" + output.getName() + " with input:" + input.getName());
			inputSrv.bind(output.getName(), input.getHWName());
			if (input.isBlocked()) {
				// Once the channel is temporarily unblocked, it will be always
				// unblocked even the current input source is blocked.
				if (input.getType() == INPUT_TYPE_TV && ch != null
						&& ch.isTempUnblocked()) {
				} else {
					TVLog("Input source:" + input.getName() + " is blocked.");
					handler.post(new DelegaterInputBlocked(input.getName()));
					stopOutput(output.getName(), true);// TO-DO True or false???
					return;
				}
			}
			
			if (input.getType() == INPUT_TYPE_TV) {
				if (ch != null) {
					if (ch.isBlocked() == false || input.isTempUnblocked()) {
						// If input source have been temporarily unblocked, all
						// channels in current input source will always be in
						// unblock state until the next boot.
						if (input.isTempUnblocked()) {
							ch.setTempUnblocked(true);
						}

						TVLog("Channel is OK, select this channel.");
						try {
							brdcstSrv.channelSelect(ch.rawInfo);
						} catch (TVMException e) {
							e.printStackTrace();
						}

					} else {
						TVLog("Channel is blocked.");
						handler.post(new DelegaterChannelBlocked(ch));
						stopOutput(output.getName(), true);
					}

				} else {
					TVLog("Channel is null.");
					stopOutput(output.getName(), false);
				}
			}
		} else {
			TVLog("input is null.");
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	private static final int MSG_UPDATE_PIPE = 1;
	private static final int MSG_UPDATE_CHANNEL = 2;

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_UPDATE_PIPE:
			updatePipeLine((TVOutput) msg.obj);
			break;
		case MSG_UPDATE_CHANNEL:
			break;

		default:
			break;
		}
		return false;
	}

	void sendMessageOnce(int what, Object obj) {
		if (!handler.hasMessages(what)) {
			handler.sendMessage(handler.obtainMessage(what, obj));
		}
	}

	@Override
	public boolean queueIdle() {
		// TODO Auto-generated method stub
		return false;
	}

	TVInput getTVInput(String name) {
		for (TVInput input : inputs) {
			if (input.getName().equals(name)) {
				return input;
			}
		}
		return null;
	}

	TVOutput getTVOutput(String name) {
		for (TVOutput output : outputs) {
			if (output.getName().equals(name)) {
				return output;
			}
		}
		return null;
	}

	public void channelDown() {
		channelDown(ChannelFilter.TVNotSkipFilter);
	}

	public void channelUp() {
		channelUp(ChannelFilter.TVNotSkipFilter);
	}

	@Override
	public void selectUp() throws RemoteException {
		channelUp(ChannelFilter.TVNothingFilter);
	}

	@Override
	public void selectDown() throws RemoteException {
		channelDown(ChannelFilter.TVNothingFilter);
	}

	public void favoriteChannelDown() {
		channelDown(ChannelFilter.TVFavoriteFilter);
	}

	public void favoriteChannelUp() {
		channelUp(ChannelFilter.TVFavoriteFilter);
	}

	void channelUp(ChannelFilter filter) {
		ChannelQueue queue = channelQueueMap.get(mCurrentOutput.getInput()
				.getName());
		if (queue != null) {
			TVChannel curChannel = queue.getChannel();
			if (curChannel == null) {
				return;
			}
			TVChannel nextChannel;
			do {
				nextChannel = queue.next();
				if (filter.filter(nextChannel)) {
					break;
				}

			} while (curChannel != nextChannel);
		}
		sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
	}

	void channelDown(ChannelFilter filter) {
		ChannelQueue queue = channelQueueMap.get(mCurrentOutput.getInput()
				.getName());
		if (queue != null) {
			TVChannel curChannel = queue.getChannel();
			if (curChannel == null) {
				return;
			}
			TVChannel preChannel;
			do {
				preChannel = queue.previous();
				if (filter.filter(preChannel)) {
					break;
				}

			} while (curChannel != preChannel);
		}
		sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
	}

	public TVChannel getCurrentChannel() {
		ChannelQueue queue = channelQueueMap.get(mCurrentOutput.getInput().getName());
		if (queue != null) {
			TVChannel ch = queue.getChannel();
			if (null == ch) {
				TVLog("null == ch");
			} else {
				TVLog("find ch");
			}
			return ch;
		} else {
			TVLog("queue == null");
		}
		return null;
	}

	public int getCurrentChannelNum() {
		ChannelQueue queue = channelQueueMap.get(mCurrentOutput.getInput()
				.getName());
		if (queue != null) {
			TVChannel ch = queue.getChannel();
			if (ch != null) {
				return ch.getChannelNum();
			}
		}
		return -1;
	}

	public boolean select(int num) {
		TVChannel ch = null;
		ChannelQueue queue = channelQueueMap.get(mCurrentOutput.getInput()
				.getName());
		if (queue != null) {
			ch = queue.findChannelByNumber(num);
		}
		sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
		return ch != null;
	}

	public boolean select(TVChannel ch) {
		TVChannel channel = null;
		ChannelQueue queue = channelQueueMap.get(mCurrentOutput.getInput()
				.getName());
		if (queue != null) {
			channel = queue.findChannel(ch);
		}
		sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
		return channel != null;
	}

	public void selectPrev() {
		ChannelQueue queue = channelQueueMap.get(mCurrentOutput.getInput()
				.getName());
		if (queue != null) {
			queue.getPreSelected();
			sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
		}
	}

	@Override
	public synchronized void changeInputSource(String outputName,
			String inputName) {
		TVLog("Change input source:" + inputName + " to output:" + outputName);
		TVOutput output = getTVOutput(outputName);
		TVInput input = getTVInput(inputName);
		if (output != null && input != null) {
			mCurrentOutput = output;
			output.connect(input);
			handler.post(new DelegaterInputSelected(outputName, inputName));
			sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
		}

	}

	@Override
	public String getInputSource(String outputName) {
		TVOutput output = getTVOutput(outputName);
		TVInput input = null;
		if(output != null)
			input = output.getInput();
		return input != null ? input.getName() : null;
	}

	public String getInputSourceType(String inputName) {
		TVInput input = getTVInput(inputName);
		return input != null ? input.getType() : null;
	}

	public String getCurrentInputSource() {
		return mCurrentOutput.getInput().getName();
	}

	public void blockInputSource(String inputName, boolean block) {
		TVInput input = getTVInput(inputName);
		if (input != null) {
			input.setBlocked(block);
			sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
		}
	}

	public void tempUnblockInputSource(String inputName) {
		TVInput input = getTVInput(inputName);
		if (input != null) {
			input.tempUnblocked();
			sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
		}
	}

	public boolean isInputSourceBlocked(String inputName) {
		TVInput input = getTVInput(inputName);
		if (input != null) {
			return input.isBlocked();
		}
		return false;
	}

	public boolean isInputSourcePhysicalBlocked(String inputName) {
		TVInput input = getTVInput(inputName);
		if (input != null) {
			return input.isPhysicalBlocked();
		}
		return false;
	}

	public String[] getInputSourceArray() {
		List<String> inps = new ArrayList<String>();
		for (TVInput input : inputs) {
			inps.add(input.getName());
		}
		return inps.toArray(new String[inputs.size()]);
	}

	public List<TVChannel> getChannels(String inputName) {
		TVInput input = getTVInput(inputName);
		if (input != null) {
			ChannelQueue queue = channelQueueMap.get(input.getName());
			if (queue != null) {
				return queue.getChannels();
			}
		}
		return null;
	}
	public List<TVChannel> getChannels() {
		ChannelQueue queue = channelQueueMap.get(mCurrentOutput.getInput()
				.getName());
		if (queue != null) {
			return queue.getChannels();
		}
		return null;
	}

	public List<TVChannel> getChannels(ChannelFilter filter) {
		ChannelQueue queue = channelQueueMap.get(mCurrentOutput.getInput()
				.getName());
		if (queue != null) {
			List<TVChannel> channels = new ArrayList<TVChannel>();
			Iterator<TVChannel> iterator = queue.getChannels().iterator();
			while (iterator.hasNext()) {
				TVChannel ch = (TVChannel) iterator.next();
				if (filter.filter(ch)) {
					channels.add(ch);
				}
			}
			return channels;
		}
		return null;
	}

	public List<TVChannel> getChannels(int start, int count,
			ChannelFilter filter) {
		List<TVChannel> list = getChannels(filter);
		if (list != null) {
			int end = start + count;
			if (end > list.size()) {
				end = list.size();
			}
			if (start >= 0 && start <= end) {
				return list.subList(start, end);
			} else {
				throw new IllegalArgumentException();
			}
		}
		return null;
	}

	@Override
	public synchronized void deleteChannel(TVChannel ch) throws RemoteException {
		String inputName = mCurrentOutput.getInput().getName();
		ChannelQueue queue = channelQueueMap.get(inputName);
		if (queue != null) {
			TVLog("Delete " + inputName + " channel:" + ch.getChannelNum());
			queue.remove(ch);
			permanentDeleteChannel(ch);
			notifyUpdate(UpdateReason.DELETE_CH, "",
					inputName.equals(ANALOG_TV) ? 0 : 1, ch.getChannelNum());
		}
	}

	private void permanentDeleteChannel(TVChannel ch) {
		ArrayList<ChannelInfo> rawList = new ArrayList<ChannelInfo>();
		rawList.add(ch.rawInfo);
		ch.rawInfo.setChannelDeleted(true);

		try {
			rawChSrv.setChannelList(ChannelService.ChannelOperator.DELETE,
					TVCommonNative.getNativeDBName(), rawList);
		} catch (TVMException e) {
			e.printStackTrace();
		}
	}

	public void updateCurrentOutput() {
		sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
	}

	public synchronized void swapChannel(TVChannel ch1, TVChannel ch2) {
		if (ch1 == ch2 || ch1 == null || ch2 == null) {
			return;
		}
		String inputName = mCurrentOutput.getInput().getName();
		ChannelQueue queue = channelQueueMap.get(inputName);
		int lNum = ch1.getChannelNum();
		int rNum = ch2.getChannelNum();

		if (queue != null) {

			ch1.rawInfo.setChannelNumber(rNum);
			ch1.rawInfo.setChannelNumberEdited(true);
			ch2.rawInfo.setChannelNumber(lNum);
			ch2.rawInfo.setChannelNumberEdited(true);

			List<ChannelInfo> list = new ArrayList<ChannelInfo>();
			list.add(ch1.rawInfo);
			list.add(ch2.rawInfo);
			try {
				rawChSrv.setChannelList(ChannelOperator.UPDATE,
						getNativeDBName(), list);
			} catch (TVMException e) {
				e.printStackTrace();
			}

			TVChannel ch = queue.getChannel();
			queue.remove(ch1);
			queue.remove(ch2);
			queue.add(ch1);
			queue.add(ch2);
			queue.findChannel(ch);

			notifyUpdate(UpdateReason.SWAP_CH, "",
					inputName.equals(ANALOG_TV) ? 0 : 1, lNum, rNum);
		}
	}

	synchronized void swapChannel(int ch1, int ch2) {
		ChannelQueue queue = channelQueueMap.get(mCurrentOutput.getInput()
				.getName());
		if (queue != null) {
			swapChannel(findChannelByNumber(ch1), findChannelByNumber(ch2));
		}
	}

	/**
	 * Insert {@code dst} channel to the position of {@code src} channel, If the
	 * channel number of {@code dst} is smaller than src, the {@code dst} will
	 * be insert after {@code src}, or it will be inserted before {@code src}.
	 * 
	 * @param dst
	 * @param src
	 */
	public synchronized void insertChannel(TVChannel dst, TVChannel src) {
		if (dst == src || dst == null || src == null) {
			return;
		}
		String inputName = mCurrentOutput.getInput().getName();
		ChannelQueue queue = channelQueueMap.get(inputName);
		int dstNum = dst.getChannelNum();
		int srcNum = src.getChannelNum();
		int offset = 0;
		if (queue != null) {
			int dstIndex = queue.getChannels().indexOf(dst);
			int srcIndex = queue.getChannels().indexOf(src);
			List<TVChannel> subList;
			if (dstIndex > srcIndex) {
				subList = queue.getChannels().subList(srcIndex, dstIndex);
				offset = 1;
			} else {
				subList = queue.getChannels().subList(dstIndex + 1,
						srcIndex + 1);
				offset = -1;
			}

			dst.rawInfo.setChannelNumber(srcNum);
			dst.rawInfo.setChannelNumberEdited(true);
			List<ChannelInfo> channelInfos = new ArrayList<ChannelInfo>();
			for (TVChannel ch : subList) {
				ch.rawInfo.setChannelNumber(ch.rawInfo.getChannelNumber()
						+ offset);
				ch.rawInfo.setChannelNumberEdited(true);
				channelInfos.add(ch.rawInfo);
			}
			channelInfos.add(dst.rawInfo);
			try {
				rawChSrv.setChannelList(ChannelOperator.UPDATE,
						getNativeDBName(), channelInfos);
			} catch (TVMException e) {
				e.printStackTrace();
			}

			TVChannel ch = queue.getChannel();
			queue.remove(dst);
			queue.add(dst);
			queue.findChannel(ch);
			// queue.getChannels().remove(dstIndex);
			// queue.getChannels().add(srcIndex, dst);

			notifyUpdate(UpdateReason.INSERT_CH, "",
					inputName.equals(ANALOG_TV) ? 0 : 1, dstNum, srcNum);
		}
	}

	synchronized void insertChannel(int dst, int src) {
		ChannelQueue queue = channelQueueMap.get(mCurrentOutput.getInput()
				.getName());
		if (queue != null) {
			insertChannel(findChannelByNumber(dst), findChannelByNumber(src));
		}
	}

	public void sendPowerOff() {
		handler.post(new Runnable() {

			public void run() {
				Intent i = new Intent(
						"android.intent.action.ACTION_REQUEST_SHUTDOWN");
				i.putExtra("android.intent.extra.KEY_CONFIRM", false);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getContext().startActivity(i);

			}
		});
	}

	private long timeToPowerOff = 0L;
	private Timer timer = new Timer("poweroff");;
	private Object lock = new Object();
	private TimerTask timerTask;

	public void schedulePowerOff(long delay) {
		synchronized (lock) {
			timeToPowerOff = System.currentTimeMillis() + delay;
		}
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
		synchronized (lock) {
			timerTask = new TimerTask() {

				@Override
				public void run() {
					sendPowerOff();
				}
			};
		}
		timer.schedule(timerTask, delay);
	}

	@Override
	public void schedulePowerOff(Date date) {
		synchronized (lock) {
			timeToPowerOff = date.getTime();
		}
		schedulePowerOff(timeToPowerOff - System.currentTimeMillis());
	}

	@Override
	public void cancelScheduledPowerOff() {
		if (timerTask != null) {
			synchronized (lock) {
				timerTask.cancel();
				timeToPowerOff = 0;
			}
		}
	}

	@Override
	public long getRemainingPowerOffTime() {
		long remainningTime;
		synchronized (lock) {
			remainningTime = timeToPowerOff - System.currentTimeMillis();
		}
		return remainningTime;
	}

	@Override
	public TVChannel findChannelByNumber(int channelNum) {
		ChannelQueue queue = channelQueueMap.get(mCurrentOutput.getInput()
				.getName());
		if (queue != null) {
			List<TVChannel> channels = queue.getChannels();
			if (channels.size() <= 0) {
				return null;
			}
			int low = 0;
			int high = channels.size() - 1;
			// Binary search
			while (low <= high) {
				int mid = (low + high) / 2;
				TVChannel ch = channels.get(mid);
				int num = ch.getChannelNum();
				if (channelNum == num) {
					return ch;
				} else if (channelNum < num) {
					high = mid - 1;
				} else {
					low = mid + 1;
				}
			}
		}
		return null;
	}

	private Vector<TVSelectorListener> selectorListeners = new Vector<TVSelectorListener>();

	public void addSelectorListener(TVSelectorListener listener) {
		synchronized (selectorListeners) {
			selectorListeners.add(listener);
		}
	}

	public void removeSelectorListener(TVSelectorListener listener) {
		synchronized (selectorListeners) {
			selectorListeners.remove(listener);
		}
	}

	private class DelegaterChannelChange implements Runnable {
		private TVChannel ch;

		public DelegaterChannelChange(TVChannel ch) {
			this.ch = ch;
		}

		public void run() {
			Enumeration<TVSelectorListener> e = selectorListeners.elements();
			TVSelectorListener item;
			while (e.hasMoreElements()) {
				item = e.nextElement();
				item.onChannelSelected(ch);
			}
		}
	}

	private class DelegaterChannelBlocked implements Runnable {
		private TVChannel ch;

		public DelegaterChannelBlocked(TVChannel ch) {
			this.ch = ch;
		}

		public void run() {
			Enumeration<TVSelectorListener> e = selectorListeners.elements();
			TVSelectorListener item;
			while (e.hasMoreElements()) {
				item = e.nextElement();
				item.onChannelBlocked(ch);
			}
		}
	}

	private class DelegaterInputSelected implements Runnable {
		private String outputName;
		private String inputName;

		public DelegaterInputSelected(String outputName, String inputName) {
			this.outputName = outputName;
			this.inputName = inputName;
		}

		@Override
		public void run() {
			Enumeration<TVSelectorListener> e = selectorListeners.elements();
			TVSelectorListener item;
			while (e.hasMoreElements()) {
				item = e.nextElement();
				item.onInputSelected(outputName, inputName);
			}
		}
	}

	private class DelegaterInputBlocked implements Runnable {
		private String inputName;

		public DelegaterInputBlocked(String inputName) {
			this.inputName = inputName;
		}

		@Override
		public void run() {
			Enumeration<TVSelectorListener> e = selectorListeners.elements();
			TVSelectorListener item;
			while (e.hasMoreElements()) {
				item = e.nextElement();
				item.onInputBlocked(inputName);
			}
		}
	}

	private Vector<ChannelsChangedListener> channelListListeners = new Vector<ChannelsChangedListener>();

	@Override
	public void addChannelsChangedListener(ChannelsChangedListener listener) {
		synchronized (channelListListeners) {
			channelListListeners.add(listener);
		}
	}

	@Override
	public void removeChannelsChangedListener(ChannelsChangedListener listener) {
		synchronized (channelListListeners) {
			channelListListeners.remove(listener);
		}
	}

	private class DelegaterChannelsChanged implements Runnable {
		@Override
		public void run() {
			Enumeration<ChannelsChangedListener> e = channelListListeners
					.elements();
			ChannelsChangedListener item;
			while (e.hasMoreElements()) {
				item = e.nextElement();
				item.onChanged();
			}
		}
	}

	List<IUpdateListener> iUpdateListeners = new ArrayList<IUpdateListener>();

	void addIUpdateListener(IUpdateListener listener) {
		iUpdateListeners.add(listener);
	}

	private void notifyUpdate(int reason, String str, int... para) {
		for (IUpdateListener listener : iUpdateListeners) {
			try {
				listener.onUpdate(reason, str, para);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		handler.post(new DelegaterChannelsChanged());
	}

	public void setTunnerMode(int mode) {
		@SuppressWarnings("unchecked")
		TVOption<Integer> option = (TVOption<Integer>) configurer
				.getOption(IntegerOption.CFG_BS_SRC);
		int oldMode = 0;
		if(option == null){
			return;
		}
		oldMode = option.get();
		if (oldMode != mode) {
			option.set(mode);

			try {
				brdcstSrv.syncStopService();
			} catch (TVMException e) {
				e.printStackTrace();
			}

			if (channelSet.containsKey(ANALOG_TV + ConfigType.BS_SRC_CABLE)
					|| channelSet.containsKey(DIGITAL_TV + mode)) {
				synchronized (channelQueueMap) {
					TVCommonNative.TVLog("set queue and update");
					channelQueueMap.put(ANALOG_TV,  channelSet.get(ANALOG_TV + ConfigType.BS_SRC_CABLE));
					channelQueueMap.put(DIGITAL_TV, channelSet.get(DIGITAL_TV + mode));
					sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
				}
			} else {
				TVCommonNative.TVLog("update directly");
				update();
			}
			notifyUpdate(UpdateReason.SET_TUNNER, "");
		}
		return;
	}

	public int getTunnerMode() {
		/* Notice: grp is 255 when input not ready in tv start */
		int grp = configurer.getGroup();
		TVCommonNative.TVLog("grp: " + grp);
		if (ConfigType.CONFIG_VALUE_GROUP_DTV != grp) {
			return ConfigType.BS_SRC_CABLE;
		}
		
		@SuppressWarnings("unchecked")
		TVOption<Integer> option = (TVOption<Integer>) configurer
				.getOption(IntegerOption.CFG_BS_SRC);
		if(option != null)
			return option.get();
		else
			return 0;
	}

	private int getTunnerModeForDig() {
		/* Notice: grp is 255 when input not ready in tv start */
		int grp = configurer.getGroup();
		TVCommonNative.TVLog("grp: " + grp);
		
		@SuppressWarnings("unchecked")
		TVOption<Integer> option = (TVOption<Integer>) configurer
				.getOption(IntegerOption.CFG_BS_SRC);
		if(option != null)
			return option.get();
		else
			return 0;
	}


	public synchronized void clearChannels() {
		String inputName = mCurrentOutput.getInput().getName();
		ChannelQueue queue = channelQueueMap.get(inputName);
		if (queue != null) {
			channelQueueMap.put(inputName, null);
			if (inputName.equals(ANALOG_TV)) {
				try {
					rawChSrv.analogDBClean(ChannelCommon.DB_AIR);
					rawChSrv.analogDBClean(ChannelCommon.DB_CABEL);
				} catch (TVMException e) {
					e.printStackTrace();
				}
			} else if (inputName.equals(DIGITAL_TV)) {
				try {
					rawChSrv.digitalDBClean(getNativeDBName());
				} catch (TVMException e) {
					e.printStackTrace();
				}
			}
		}
		sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
		notifyUpdate(UpdateReason.CLEAR_CHANNELS, "");
	}


	public synchronized void clearAllAirChannels() {
		TVLog("clear air queueMap and set");

		/* clean air part of the queue, leave cable part */
		synchronized (channelQueueMap) {
			channelQueueMap.put(DIGITAL_TV, channelSet.get(DIGITAL_TV + ConfigType.BS_SRC_CABLE));
			channelQueueMap.put(ANALOG_TV,	channelSet.get(ANALOG_TV  + ConfigType.BS_SRC_CABLE));
		}

		/* clean air set */
		synchronized (channelSet) {
			channelSet.put(DIGITAL_TV + ConfigType.BS_SRC_AIR, null);
			channelSet.put(ANALOG_TV  + ConfigType.BS_SRC_AIR, null);
		}

		/* clean air channel info and svl */
		try {
			TVLog("clear air svl");
			rawChSrv.analogDBClean(ChannelCommon.DB_AIR);
			rawChSrv.digitalDBClean(ChannelCommon.DB_AIR);
		} catch (TVMException e) {
			e.printStackTrace();
		}
		
		// check whether raw channel Info stored in DB_AIR is empty or not.
		List<ChannelInfo> rawList = null;
		try {
			rawList = rawChSrv.getChannelList(ChannelCommon.DB_AIR);
		} catch (TVMException e) {
			e.printStackTrace();
		}
		
		if (rawList != null && rawList.size() == 0) {
			try {
				compSrv.updateSysStatus(ComponentService.COMP_TTX_SYS_STATUS_EMPTY_SVL);
			} catch (TVMException e) {
				e.printStackTrace();
			}
		}

		// refresh
		TVLog("Update pipe. curOutput: " + mCurrentOutput.getName());
		sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
		notifyUpdate(UpdateReason.CLEAR_CHANNELS, "");
	}


	public synchronized void clearAllCableChannels() {
		TVLog("Clear cable svl, queueMap and set");

		/* clean cable part of the queue, leave air part */
		synchronized (channelQueueMap) {
			channelQueueMap.put(DIGITAL_TV, channelSet.get(DIGITAL_TV + ConfigType.BS_SRC_AIR));
			channelQueueMap.put(ANALOG_TV,	channelSet.get(ANALOG_TV  + ConfigType.BS_SRC_AIR));
		}

		/* clean air set */
		synchronized (channelSet) {
			channelSet.put(DIGITAL_TV + ConfigType.BS_SRC_CABLE, null);
			channelSet.put(ANALOG_TV  + ConfigType.BS_SRC_CABLE, null);
		}

		/* clean air channel info and svl */
		try {
			rawChSrv.analogDBClean(ChannelCommon.DB_CABEL);
			rawChSrv.digitalDBClean(ChannelCommon.DB_CABEL);
		} catch (TVMException e) {
			e.printStackTrace();
		}
		
		// check whether raw channel Info stored in DB_AIR is empty or not.
		List<ChannelInfo> rawList = null;
		try {
			rawList = rawChSrv.getChannelList(ChannelCommon.DB_CABEL);
		} catch (TVMException e) {
			e.printStackTrace();
		}
		
		if (rawList != null && rawList.size() == 0) {
			try {
				compSrv.updateSysStatus(ComponentService.COMP_TTX_SYS_STATUS_EMPTY_SVL);
			} catch (TVMException e) {
				e.printStackTrace();
			}
		}

		// refresh
		TVLog("Update pipe. curOutput: " + mCurrentOutput.getName());
		sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
		notifyUpdate(UpdateReason.CLEAR_CHANNELS, "");
	}


	public synchronized void clearAllChannels() {
		try {
			rawChSrv.analogDBClean(ChannelCommon.DB_AIR);
			rawChSrv.analogDBClean(ChannelCommon.DB_CABEL);
			rawChSrv.digitalDBClean(ChannelCommon.DB_AIR);
			rawChSrv.digitalDBClean(ChannelCommon.DB_CABEL);
		} catch (TVMException e) {
			e.printStackTrace();
		}

		TVLog("clear channelQueueMap and channelSet");
		channelQueueMap.clear();
		channelSet.clear();
		
		sendMessageOnce(MSG_UPDATE_PIPE, mCurrentOutput);
		notifyUpdate(UpdateReason.CLEAR_CHANNELS, "");
	}

	public synchronized void flush() {
		try {
			TVLog("flush 2 DBs");
			rawChSrv.fsStoreChannelList(ChannelCommon.DB_AIR);
			rawChSrv.fsStoreChannelList(ChannelCommon.DB_CABEL);
		} catch (TVMException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void resetChannelAttribute(TVChannel ch, String str,
			int... para) throws RemoteException {
		int reason = para[0];
		List<ChannelInfo> list = new ArrayList<ChannelInfo>();
		String inputName = mCurrentOutput.getInput().getName();
		ChannelQueue queue = channelQueueMap.get(inputName);
		if (queue == null) {
			return;
		}
		switch (reason) {
		case UpdateReason.RESET_CH_NUM: {
			TVChannel curCH = queue.getChannel();
			if (queue.remove(ch)) {
				int newNum = para[1];
				ch.rawInfo.setChannelNumber(newNum);
				ch.rawInfo.setChannelNumberEdited(true);
				queue.add(ch);
				queue.findChannel(curCH);
				list.add(ch.rawInfo);
				notifyUpdate(reason, "", inputName.equals(ANALOG_TV) ? 0 : 1,
						ch.getChannelNum(), newNum);
			}
			break;
		}

		case UpdateReason.RESET_CH_NAME: {
			ch.rawInfo.setServiceName(str);
			ch.rawInfo.setChannelNameEdited(true);
			list.add(ch.rawInfo);
			notifyUpdate(reason, str, inputName.equals(ANALOG_TV) ? 0 : 1,
					ch.getChannelNum());
			break;
		}

		case UpdateReason.RESET_CH_MASK: {
			int mask = para[1];
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			DataOutputStream st = new DataOutputStream(bs);
			try {
				st.writeInt(mask);
				ch.rawInfo.setPrivateData(bs.toByteArray());
				// The mask maybe come from client.
				ch.setMask(mask);
				list.add(ch.rawInfo);
			} catch (IOException e) {
				e.printStackTrace();
			}

			notifyUpdate(reason, str, inputName.equals(ANALOG_TV) ? 0 : 1,
					ch.getChannelNum(), mask);
			break;
		}

		case UpdateReason.RESET_CH_FREQ: {
			int freq = para[1];
			if (ch.rawInfo instanceof AnalogChannelInfo) {
				((AnalogChannelInfo) ch.rawInfo).setFrequency(freq);
			} else if (ch.rawInfo instanceof DvbChannelInfo) {
				((DvbChannelInfo) ch.rawInfo).setFrequency(freq);
			}
			ch.rawInfo.setFrequencyEdited(true);
			list.add(ch.rawInfo);
			notifyUpdate(reason, str, inputName.equals(ANALOG_TV) ? 0 : 1,
					ch.getChannelNum(), freq);
			break;
		}

		default:
			break;
		}

		try {
			if (list.size() > 0) {
				rawChSrv.setChannelList(ChannelOperator.UPDATE,
						getNativeDBName(), list);
			}
		} catch (TVMException e) {
			e.printStackTrace();
		}
	}
}
