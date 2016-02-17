package com.mediatekk.tvcommon;

import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.AnalogChannelInfo;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.DvbChannelInfo;
import com.mediatek.tv.service.ChannelService;
import com.mediatek.tvcommon.ITVSelectorListener;

import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;

public abstract class TVCommonNative extends Binder implements ITVCommon {
	/**
	 * TVCommon log tag
	 */
	private static final String TVCOMMON = "TVCommon";
	private final Object mActionDoneSync = new Object();
	private boolean mActionDone = false;
	private static Context mContext;
	private static String logTag = "TV_Common";
	// notify TVCOMMON service scan operation has completed
	static final String SCAN_COMPLETE_ACTION = "com.mediatek.tvcommon.scancomplete";

	static Context getContext() {
		return mContext;
	}

	static ITVCommon asInterface(IBinder obj) {
		if (obj == null) {
			return null;
		}

		ITVCommon in = (ITVCommon) obj.queryLocalInterface(descriptor);
		if (in != null) {
			//
			if (Process.myUid() == Process.SYSTEM_UID) {
				logTag = "TV_Server";
			} else {
				logTag = "TV_Local";
			}
			return in;
		}
		logTag = "TV_Client";
		return new TVCommonProxy(obj, mContext);
	}

	void actionDone() {
		synchronized (mActionDoneSync) {
			mActionDone = true;
			mActionDoneSync.notifyAll();
		}
	}

	void prepare() {
		synchronized (mActionDoneSync) {
			while (!mActionDone) {
				try {
					mActionDoneSync.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	static String getNativeDBName() {
		TVConfigurer cfg = TVConfigurer.getInstance(TVCommonNative.getContext());
		if (null == cfg) {
			TVLog("null == cfg, set default Cable DB");
			return ChannelCommon.DB_CABEL;
		} 

		/* Notice: grp is 255 when input not ready in tv start */
		int grp = cfg.getGroup();
		TVLog("grp: " + grp);
		if (ConfigType.CONFIG_VALUE_GROUP_DTV != grp) {
			return ChannelCommon.DB_CABEL;
		}
		
		TVOption<Integer> option = (TVOption<Integer>) cfg.getOption(IntegerOption.CFG_BS_SRC);
		if (option != null) {
			TVLog("BS_SRC: " + option.get());
			return option.get() == ConfigType.BS_SRC_AIR ? ChannelCommon.DB_AIR
					: ChannelCommon.DB_CABEL;
		}
		return ChannelCommon.DB_CABEL;
	}

	/* Actually, it is the original "getNativeDBName". Now add this for digital especially in tv start */
	static String getDigitalNativeDBName() {
		TVConfigurer cfg = TVConfigurer.getInstance(TVCommonNative.getContext());
		if (null == cfg) {
			TVLog("null == cfg, set default Cable DB");
			return ChannelCommon.DB_CABEL;
		} 

		TVOption<Integer> option = (TVOption<Integer>) cfg.getOption(IntegerOption.CFG_BS_SRC);
		if (option != null) {
			TVLog("BS_SRC: " + option.get());
			return option.get() == ConfigType.BS_SRC_AIR ? ChannelCommon.DB_AIR
					: ChannelCommon.DB_CABEL;
		}
		return ChannelCommon.DB_CABEL;
	}


	TVCommonNative() {
		attachInterface(this, descriptor);
	}

	private static final boolean isLog = true;

	/**
	 * Send a {@link #TVCOMMON} log message.
	 * 
	 * @param str
	 *            The message you would like logged.
	 */
	static public void TVLog(String str) {
		if (isLog) {
			StackTraceElement[] el = new Exception().getStackTrace();
			android.util.Log.i(
					logTag,
					"Class:" + el[1].getClassName() + "."
							+ el[1].getMethodName() + " [Line:"
							+ new Integer(el[1].getLineNumber()).toString()
							+ "] :---> " + str);
		}
	}
	/**
	 * Retrieve the {@link #ITVCommon} interface.
	 * 
	 * @param context
	 *            Application Context
	 */
	static public ITVCommon getDefault(Context context) {
		if (mContext == null && context !=null) {
			mContext = context.getApplicationContext();
		}

		if (gDefault != null) {
			return gDefault;
		}
		IBinder b = getService(TVCOMMON);
		if (b == null) {
			b = TVCommonService.getInstance();
			TVLog(TVCOMMON
					+ "service is not exit in ServiceManager, try to add it to ServiceManager");
			addService(TVCOMMON, b);
		}

		try {
			// Flags ???
			b.linkToDeath(new DeathRecipient() {
				// @Override
				public void binderDied() {
					TVLog(TVCOMMON + " is dead Oooooooooops... :-(");
					gDefault = null;
				}
			}, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		gDefault = asInterface(b);
		return gDefault;
	}

	static IBinder getService(String svr) {

		Class<?> svrMgr;

		Class<?> para = String.class;
		Method getSvr = null;
		try {
			svrMgr = Class.forName("android.os.ServiceManager");
			try {
				getSvr = svrMgr.getDeclaredMethod("getService", para);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		IBinder b = null;
		if (getSvr != null) {
			try {
				b = (IBinder) getSvr.invoke(null, svr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return b;
	}

	static void addService(String name, IBinder service) {
		Class<?> svrMgr;
		Method addSvr = null;
		try {
			svrMgr = Class.forName("android.os.ServiceManager");
			for (Method mtd : svrMgr.getDeclaredMethods()) {
				if (mtd.getName().equals("addService")) {
					addSvr = mtd;
					break;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Object[] args = new Object[] { name, service };
		if (addSvr != null) {
			try {
				addSvr.invoke(null, args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public IBinder asBinder() {
		return this;
	}

	@Override
	protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
			throws RemoteException {
		switch (code) {
		case SCAN_TRANSACTION: {
			return true;
		}
		case SELECT_INT_CHANNEL_TRANSCATION: {
			data.enforceInterface(descriptor);
			int num = data.readInt();
			boolean res = this.select(num);
			reply.writeNoException();
			reply.writeInt(res ? 1 : 0);
			return true;
		}
		case SELECT_TV_CHANNEL_TRANSACTION: {

			return true;
		}

		case GET_CURRENT_INT_CHANNEL_TRANSACTION: {
			data.enforceInterface(descriptor);
			int num = this.getCurrentChannelNum();
			reply.writeNoException();
			reply.writeInt(num);
			return true;
		}

		case CHANNEL_UP_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.channelUp();
			reply.writeNoException();
			return true;
		}

		case CHANNEL_DOWN_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.channelDown();
			reply.writeNoException();
			return true;
		}

		case SELECT_UP_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.selectUp();
			reply.writeNoException();
			return true;
		}

		case SELECT_DOWN_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.selectDown();
			reply.writeNoException();
			return true;
		}

		case FAVORITE_CHANNEL_UP_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.favoriteChannelUp();
			reply.writeNoException();
			return true;
		}

		case FAVORITE_CHANNEL_DOWN_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.favoriteChannelDown();
			reply.writeNoException();
			return true;
		}

		case DELETE_CHANNEL_TRANSACTION: {
			data.enforceInterface(descriptor);
			int num = data.readInt();
			TVChannel channel = ((TVCommonService) this)
					.findChannelByNumber(num);
			if (channel != null) {
				this.deleteChannel(channel);
			}
			reply.writeNoException();
			return true;
		}

		case CHANGE_INPUR_SOURCE_TRANSACTION: {
			data.enforceInterface(descriptor);
			String outputName = data.readString();
			String inputName = data.readString();
			this.changeInputSource(outputName, inputName);
			reply.writeNoException();
			return true;
		}

		case GET_INPUT_SOURCE_TRANSACTION: {
			data.enforceInterface(descriptor);
			String outputName = data.readString();
			String inputName = this.getInputSource(outputName);
			reply.writeNoException();
			reply.writeString(inputName);
			return true;
		}

		case BLOCK_INPUT_SOURCE_TRANSACTION: {
			data.enforceInterface(descriptor);
			String inputName = data.readString();
			boolean block = data.readInt() == 1 ? true : false;
			this.blockInputSource(inputName, block);
			reply.writeNoException();
			return true;
		}

		case TEMP_UNBLOCK_INPUT_SROUCE_TRANSACTION: {
			data.enforceInterface(descriptor);
			String inputName = data.readString();
			this.tempUnblockInputSource(inputName);
			reply.writeNoException();
			return true;
		}

		case IS_INPUT_SOURCE_BLOCKED_TRANSACTION: {
			data.enforceInterface(descriptor);
			String inputName = data.readString();
			boolean res = this.isInputSourceBlocked(inputName);
			reply.writeNoException();
			reply.writeInt(res ? 1 : 0);
			return true;
		}

		case IS_INPUT_SOURCE_BLOCKED_PHYSICAL_TRANSACTION: {
			data.enforceInterface(descriptor);
			String inputName = data.readString();
			boolean res = this.isInputSourcePhysicalBlocked(inputName);
			reply.writeNoException();
			reply.writeInt(res ? 1 : 0);
			return true;
		}

		case GET_INPUT_SOURCE_ARRAY_TRANSACTION: {
			data.enforceInterface(descriptor);
			String[] inputs = this.getInputSourceArray();
			reply.writeNoException();
			reply.writeStringArray(inputs);
			return true;
		}

		case SELECT_PRE_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.selectPrev();
			reply.writeNoException();
			return true;
		}

		case GET_CHANNELS_TRANSACTION: {
			return true;
		}

		case GET_CURRENT_INPUT_SOURCE_TRANSACTION: {
			data.enforceInterface(descriptor);
			String inputName = this.getCurrentInputSource();
			reply.writeNoException();
			reply.writeString(inputName);
			return true;
		}

		case GET_INPUT_SOURCE_TYPE_TRANSACTION: {
			data.enforceInterface(descriptor);
			String inputName = data.readString();
			String type = this.getInputSourceType(inputName);
			reply.writeNoException();
			reply.writeString(type);
			return true;
		}

		case UPDATE_CURRENT_OUTPUT_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.updateCurrentOutput();
			reply.writeNoException();
			return true;
		}

		case SWAP_CHANNEL_TRANSACTION: {
			data.enforceInterface(descriptor);
			int ch1 = data.readInt();
			int ch2 = data.readInt();
			((TVCommonService) this).swapChannel(ch1, ch2);
			reply.writeNoException();
			return true;
		}

		case INSERT_CHANNEL_TRANSACTION: {
			data.enforceInterface(descriptor);
			int dst = data.readInt();
			int src = data.readInt();
			((TVCommonService) this).insertChannel(dst, src);
			reply.writeNoException();
		}

		case SCHEDULE_POWEROFF_LONG_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.schedulePowerOff(data.readLong());
			reply.writeNoException();
			return true;
		}

		case CANCEL_SCHEDULED_POWEROFF_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.cancelScheduledPowerOff();
			reply.writeNoException();
			return true;
		}

		case GET_REMAINING_POWEROFF_TIME_TRANSACTION: {
			data.enforceInterface(descriptor);
			long time = this.getRemainingPowerOffTime();
			reply.writeNoException();
			reply.writeLong(time);
			return true;
		}

		case ADD_ITVSELECTOR_LISTENER_TRANSACTION: {
			data.enforceInterface(descriptor);
			ITVSelectorListener listener;
			listener = ITVSelectorListener.Stub.asInterface(data
					.readStrongBinder());

			((TVCommonService) this)
					.addSelectorListener(new ITVSelectorListenerDelegater(
							listener));
			reply.writeNoException();
			return true;
		}

		case ADD_IUPDATE_LISTENER_TRANSACTION: {
			data.enforceInterface(descriptor);
			IUpdateListener listener;
			listener = IUpdateListener.Stub
					.asInterface(data.readStrongBinder());
			((TVCommonService) this).addIUpdateListener(listener);
			reply.writeNoException();
			return true;
		}

		case SET_TUNNER_MODE_TRANSACTION: {
			data.enforceInterface(descriptor);
			int mode = data.readInt();
			this.setTunnerMode(mode);
			reply.writeNoException();
			return true;
		}

		case CLEAR_CHANNELS_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.clearChannels();
			reply.writeNoException();
			return true;
		}

		case CLEAR_ALL_AIR_CHANNELS_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.clearAllAirChannels();
			reply.writeNoException();
			return true;
		}
		
		case CLEAR_ALL_CABLE_CHANNELS_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.clearAllCableChannels();
			reply.writeNoException();
			return true;
		}
		
		case CLEAR_ALL_CHANNELS_TRANSACTION: {
			data.enforceInterface(descriptor);
			this.clearAllChannels();
			reply.writeNoException();
			return true;
		}

		case RESET_CHANNEL_ATTRIBUTE_TRANSACTION: {
			data.enforceInterface(descriptor);
			int reason = data.readInt();
			int num = data.readInt();
			TVChannel ch = this.findChannelByNumber(num);
			if (ch != null) {
				switch (reason) {
				case UpdateReason.RESET_CH_NUM: {
					int newNum = data.readInt();
					this.resetChannelAttribute(ch, "", reason, newNum);
					break;
				}
				case UpdateReason.RESET_CH_NAME: {
					String name = data.readString();
					this.resetChannelAttribute(ch, name, reason);
					break;
				}

				case UpdateReason.RESET_CH_MASK: {
					int mask = data.readInt();
					this.resetChannelAttribute(ch, "", reason, mask);
					break;
				}

				case UpdateReason.RESET_CH_FREQ: {
					int freq = data.readInt();
					this.resetChannelAttribute(ch, "", reason, freq);
					break;
				}

				default:
					break;
				}
			}
			return true;
		}

		default:
			break;
		}
		return super.onTransact(code, data, reply, flags);
	}

	private class ITVSelectorListenerDelegater implements TVSelectorListener {
		private ITVSelectorListener listener;

		public ITVSelectorListenerDelegater(ITVSelectorListener listener) {
			this.listener = listener;
		}

		@Override
		public void onChannelSelected(TVChannel ch) {
			try {
				listener.onChannelSelected(ch.getChannelNum());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onChannelBlocked(TVChannel ch) {
			try {
				listener.onChannelBlocked(ch.getChannelNum());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onInputSelected(String outputName, String inputName) {
			try {
				listener.onInputSelected(outputName, inputName);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onInputBlocked(String inputName) {
			try {
				listener.onInputBlocked(inputName);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}
	}

	private static ITVCommon gDefault;
}

class TVCommonProxy implements ITVCommon {

	public IBinder asBinder() {
		return mRemote;
	}

	private IBinder mRemote;
	private volatile HashMap<String, List<TVChannel>> channelsMap = new HashMap<String, List<TVChannel>>();
	final private TVManager tvMgr;
	final private ChannelService rawChSrv;
	final private Context mContext;
	private Handler handler;

	private static final String ANALOG_TV = "atv";
	private static final String DIGITAL_TV = "dtv";

	public TVCommonProxy(IBinder remote, Context context) {
		mRemote = remote;
		mContext = context;
		handler = new Handler(Looper.getMainLooper());
		tvMgr = TVManager.getInstance(context);
		rawChSrv = (ChannelService) tvMgr
				.getService(ChannelService.ChannelServiceName);
		update();

		try {
			monitorUpdate();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	synchronized void asynUpdate() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				update();
			}
		}).start();
	}

	private Comparator<TVChannel> mComparator = new Comparator<TVChannel>() {

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

	synchronized void update() {
		long startTime = SystemClock.elapsedRealtime();
		List<ChannelInfo> rawList = null;
		try {
			// Raw channel Information store either in DB_ANALOG or DB_AIR.
			if (rawChSrv != null)
				rawList = rawChSrv.getChannelList(TVCommonNative.getNativeDBName());
		} catch (TVMException e) {
			e.printStackTrace();
		}
		List<TVChannel> analogs = new ArrayList<TVChannel>();

		
		List<TVChannel> digitals = new ArrayList<TVChannel>();

		TVChannel ch = null;
		if (rawList != null && rawList.size() > 0) {
			for (ChannelInfo info : rawList) {
				ch = new TVChannel(info);
				if (info instanceof AnalogChannelInfo) {
					analogs.add(ch);
				} else if (info instanceof DvbChannelInfo) {
					digitals.add(ch);
				}
			}
		}
		Collections.sort(analogs, mComparator);
		Collections.sort(digitals, mComparator);
		TVCommonNative.TVLog("ATV got " + analogs.size() + " channels");
		TVCommonNative.TVLog("DTV got " + digitals.size() + " channels");
		synchronized (channelsMap) {
			channelsMap.put(ANALOG_TV, analogs);
			channelsMap.put(DIGITAL_TV, digitals);
		}

		TVCommonNative.TVLog("Update operation spend "
				+ (SystemClock.elapsedRealtime() - startTime) + " miliseconds");
	}

	@Override
	public void channelDown() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			mRemote.transact(CHANNEL_DOWN_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public void channelUp() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			mRemote.transact(CHANNEL_UP_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public TVChannel getCurrentChannel() throws RemoteException {
		List<TVChannel> channels = getChannels();
		if (channels != null) {
			int num = getCurrentChannelNum();
			for (TVChannel ch : channels) {
				if (ch.getChannelNum() == num) {
					return ch;
				}
			}
		}
		return null;
	}

	@Override
	public int getCurrentChannelNum() throws RemoteException {

		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		int ret;
		try {
			data.writeInterfaceToken(descriptor);
			mRemote.transact(GET_CURRENT_INT_CHANNEL_TRANSACTION, data, reply,
					0);
			reply.readException();
			ret = reply.readInt();
		} finally {
			data.recycle();
			reply.recycle();
		}
		return ret;
	}

	@Override
	public boolean select(int num) throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		boolean ret;
		try {
			data.writeInterfaceToken(descriptor);
			data.writeInt(num);
			mRemote.transact(SELECT_INT_CHANNEL_TRANSCATION, data, reply, 0);
			reply.readException();
			ret = reply.readInt() != 0;
		} finally {
			data.recycle();
			reply.recycle();
		}
		return ret;
	}

	@Override
	public boolean select(TVChannel ch) throws RemoteException {
		return select(ch.getChannelNum());
	}

	@Override
	public void changeInputSource(String outputName, String inputName)
			throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			data.writeString(outputName);
			data.writeString(inputName);
			mRemote.transact(CHANGE_INPUR_SOURCE_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public String getInputSource(String outputName) throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		String inputName;
		try {
			data.writeInterfaceToken(descriptor);
			data.writeString(outputName);
			mRemote.transact(GET_INPUT_SOURCE_TRANSACTION, data, reply, 0);
			reply.readException();
			inputName = reply.readString();
		} finally {
			data.recycle();
			reply.recycle();
		}
		return inputName;
	}

	@Override
	public void blockInputSource(String inputName, boolean block)
			throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			data.writeString(inputName);
			data.writeInt(block ? 1 : 0);
			mRemote.transact(BLOCK_INPUT_SOURCE_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public void tempUnblockInputSource(String inputName) throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			data.writeString(inputName);
			mRemote.transact(TEMP_UNBLOCK_INPUT_SROUCE_TRANSACTION, data,
					reply, 0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public boolean isInputSourceBlocked(String inputName)
			throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		boolean ret;
		try {
			data.writeInterfaceToken(descriptor);
			data.writeString(inputName);
			mRemote.transact(IS_INPUT_SOURCE_BLOCKED_TRANSACTION, data, reply,
					0);
			reply.readException();
			ret = reply.readInt() != 0;
		} finally {
			data.recycle();
			reply.recycle();
		}
		return ret;
	}

	@Override
	public boolean isInputSourcePhysicalBlocked(String inputName)
			throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		boolean ret;
		try {
			data.writeInterfaceToken(descriptor);
			data.writeString(inputName);
			mRemote.transact(IS_INPUT_SOURCE_BLOCKED_PHYSICAL_TRANSACTION,
					data, reply, 0);
			reply.readException();
			ret = reply.readInt() != 0;
		} finally {
			data.recycle();
			reply.recycle();
		}
		return ret;
	}

	@Override
	public String[] getInputSourceArray() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		data.writeInterfaceToken(descriptor);
		mRemote.transact(GET_INPUT_SOURCE_ARRAY_TRANSACTION, data, reply, 0);
		reply.readException();
		int length = reply.readInt();
		String[] inputs = new String[length];
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = reply.readString();
		}
		data.recycle();
		reply.recycle();
		return inputs;
	}

	@Override
	public void selectPrev() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		data.writeInterfaceToken(descriptor);
		mRemote.transact(SELECT_PRE_TRANSACTION, data, reply, 0);
		reply.readException();
		data.recycle();
		reply.recycle();
	}

	@Override
	public List<TVChannel> getChannels() throws RemoteException {
		return channelsMap.get(getCurrentInputSource());
	}

	@Override
	public String getCurrentInputSource() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		data.writeInterfaceToken(descriptor);
		mRemote.transact(GET_CURRENT_INPUT_SOURCE_TRANSACTION, data, reply, 0);
		reply.readException();
		String inputName = reply.readString();
		data.recycle();
		reply.recycle();
		return inputName;
	}

	@Override
	public List<TVChannel> getChannels(ChannelFilter filter)
			throws RemoteException {
		if (getChannels() == null) {
			return null;
		}
		List<TVChannel> channels = new ArrayList<TVChannel>();
		Iterator<TVChannel> iterator = getChannels().iterator();
		while (iterator.hasNext()) {
			TVChannel ch = (TVChannel) iterator.next();
			if (filter.filter(ch)) {
				channels.add(ch);
			}
		}
		return channels;
	}

	@Override
	public List<TVChannel> getChannels(int start, int count,
			ChannelFilter filter) throws RemoteException {
		List<TVChannel> list = getChannels(filter);
		if (list != null) {
			int end = start + count;
			if (start >= 0 && end <= list.size()) {
				if (start <= end) {
					return list.subList(start, end);
				}
				throw new IllegalArgumentException();
			}
			throw new IndexOutOfBoundsException();
		}
		return null;
	}

	@Override
	public synchronized void deleteChannel(TVChannel ch) throws RemoteException {
		List<TVChannel> channels = channelsMap.get(getCurrentInputSource());
		if (channels != null) {
			Parcel data = Parcel.obtain();
			Parcel reply = Parcel.obtain();
			data.writeInterfaceToken(descriptor);
			data.writeInt(ch.getChannelNum());
			mRemote.transact(DELETE_CHANNEL_TRANSACTION, data, reply, 0);
			reply.readException();
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public String getInputSourceType(String inputName) throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		data.writeInterfaceToken(descriptor);
		data.writeString(inputName);
		mRemote.transact(GET_INPUT_SOURCE_TYPE_TRANSACTION, data, reply, 0);
		reply.readException();
		String type = reply.readString();
		data.recycle();
		reply.recycle();
		return type;
	}

	@Override
	public synchronized void updateCurrentOutput() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		data.writeInterfaceToken(descriptor);
		mRemote.transact(UPDATE_CURRENT_OUTPUT_TRANSACTION, data, reply, 0);
		reply.readException();
		data.recycle();
		reply.recycle();
	}

	@Override
	public synchronized void swapChannel(TVChannel ch1, TVChannel ch2)
			throws RemoteException {
		List<TVChannel> channels = channelsMap.get(getCurrentInputSource());
		if (channels != null) {
			Parcel data = Parcel.obtain();
			Parcel reply = Parcel.obtain();
			data.writeInterfaceToken(descriptor);
			data.writeInt(ch1.getChannelNum());
			data.writeInt(ch2.getChannelNum());
			mRemote.transact(SWAP_CHANNEL_TRANSACTION, data, reply, 0);
			reply.readException();
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public void schedulePowerOff(long delay) throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		data.writeInterfaceToken(descriptor);
		data.writeLong(delay);
		mRemote.transact(SCHEDULE_POWEROFF_LONG_TRANSACTION, data, reply, 0);
		reply.readException();
		data.recycle();
		reply.recycle();
	}

	@Override
	public void schedulePowerOff(Date date) throws RemoteException {
		schedulePowerOff(date.getTime() - System.currentTimeMillis());
	}

	@Override
	public void cancelScheduledPowerOff() throws RemoteException {

		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		data.writeInterfaceToken(descriptor);
		mRemote.transact(CANCEL_SCHEDULED_POWEROFF_TRANSACTION, data, reply, 0);
		reply.readException();
		data.recycle();
		reply.recycle();
	}

	@Override
	public long getRemainingPowerOffTime() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		data.writeInterfaceToken(descriptor);
		mRemote.transact(CANCEL_SCHEDULED_POWEROFF_TRANSACTION, data, reply, 0);
		reply.readException();
		long time = reply.readLong();
		data.recycle();
		reply.recycle();
		return time;
	}

	@Override
	public synchronized void insertChannel(TVChannel dst, TVChannel src)
			throws RemoteException {
		List<TVChannel> channels = channelsMap.get(getCurrentInputSource());
		if (channels != null) {
			Parcel data = Parcel.obtain();
			Parcel reply = Parcel.obtain();
			data.writeInterfaceToken(descriptor);
			data.writeInt(dst.getChannelNum());
			data.writeInt(src.getChannelNum());
			mRemote.transact(INSERT_CHANNEL_TRANSACTION, data, reply, 0);
			reply.readException();
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public TVChannel findChannelByNumber(int channelNum) throws RemoteException {
		List<TVChannel> channels = channelsMap.get(getCurrentInputSource());
		return findChannelByNumber(channels, channelNum);
	}

	// Find channel in specified list by channel number.
	private TVChannel findChannelByNumber(List<TVChannel> channels,
			int channelNum) {
		if (channels != null && channels.size() > 0) {
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
	private ITVSelectorListener.Stub lStub = null;

	@Override
	public void addSelectorListener(TVSelectorListener listener)
			throws RemoteException {
		if (lStub == null) {
			lStub = new ITVSelectorListener.Stub() {

				@Override
				public void onChannelSelected(int channelNum)
						throws RemoteException {
					final TVChannel ch = findChannelByNumber(channelNum);
	                                if(ch==null){
						return;
					}else{

					handler.post(new Runnable() {

						@Override
						public void run() {
							Enumeration<TVSelectorListener> e = selectorListeners
									.elements();
							TVSelectorListener item;
							while (e.hasMoreElements()) {
								item = e.nextElement();
								TVCommonNative.TVLog("Channel:"
										+ ch.getChannelNum() + " was selected.");
								item.onChannelSelected(ch);
							}
						}
					});
                                        }
				}

				@Override
				public void onChannelBlocked(int channelNum)
						throws RemoteException {
					final TVChannel ch = findChannelByNumber(channelNum);
	                                if(ch==null){
						return;
					}else{

					handler.post(new Runnable() {

						@Override
						public void run() {
							Enumeration<TVSelectorListener> e = selectorListeners
									.elements();
							TVSelectorListener item;
							while (e.hasMoreElements()) {
								item = e.nextElement();
								TVCommonNative.TVLog("Channel:"
										+ ch.getChannelNum() + " was blocked.");
								item.onChannelBlocked(ch);
							}
						}
					});
                                        }
				}

				@Override
				public void onInputSelected(final String outputName,
						final String inputName) throws RemoteException {
					handler.post(new Runnable() {

						@Override
						public void run() {
							Enumeration<TVSelectorListener> e = selectorListeners
									.elements();
							TVSelectorListener item;
							while (e.hasMoreElements()) {
								item = e.nextElement();
								TVCommonNative.TVLog("Input Source "
										+ inputName + " was blocked.");
								item.onInputSelected(outputName, inputName);
							}
						}
					});
				}

				@Override
				public void onInputBlocked(final String inputName)
						throws RemoteException {
					handler.post(new Runnable() {

						@Override
						public void run() {
							Enumeration<TVSelectorListener> e = selectorListeners
									.elements();
							TVSelectorListener item;
							while (e.hasMoreElements()) {
								item = e.nextElement();
								TVCommonNative.TVLog("Input Source "
										+ inputName + " was blocked.");
								item.onInputBlocked(inputName);
							}
						}
					});

				}
			};
			addITVSelectorListenerStub(lStub);
		}
		synchronized (selectorListeners) {
			selectorListeners.add(listener);
		}
	}

	private void addITVSelectorListenerStub(ITVSelectorListener.Stub listener)
			throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			data.writeStrongBinder((((listener != null)) ? (listener.asBinder())
					: (null)));
			mRemote.transact(ADD_ITVSELECTOR_LISTENER_TRANSACTION, data, reply,
					0);
			reply.readException();
		} finally {
			reply.recycle();
			data.recycle();
		}
	}

	@Override
	public void removeSelectorListener(TVSelectorListener listener) {
		synchronized (selectorListeners) {
			selectorListeners.remove(listener);
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
			Enumeration<ChannelsChangedListener> e = channelListListeners.elements();
			ChannelsChangedListener item;
			int i = 0;
			while (e.hasMoreElements()) {
				i++;
				item = e.nextElement();
				item.onChanged();
				TVCommonNative.TVLog("DelegaterChannelsChanged: " + i + " times");
			}
		}
	}

	private void monitorUpdate() throws RemoteException {
		IUpdateListener.Stub lStub = new IUpdateListener.Stub() {

			@Override
			public void onUpdate(int reason, String str, int... para)
					throws RemoteException {
				TVCommonNative.TVLog("reason: " + reason + ", str: " + str);
				switch (reason) {
				case UpdateReason.RESET_CH_NUM: {
					int inputID = para[0];
					List<TVChannel> channels = channelsMap
							.get(inputID == 0 ? ANALOG_TV : DIGITAL_TV);
					int resetNum = para[1];
					TVChannel ch = findChannelByNumber(channels, resetNum);
					if (ch != null) {
						int newNum = para[2];
						ch.rawInfo.setChannelNumber(newNum);
						synchronized (channels) {
							Collections.sort(channels, mComparator);
						}
					}
					break;
				}

				case UpdateReason.RESET_CH_NAME: {
					int inputID = para[0];
					List<TVChannel> channels = channelsMap
							.get(inputID == 0 ? ANALOG_TV : DIGITAL_TV);
					int num = para[1];
					TVChannel ch = findChannelByNumber(channels, num);
					if (ch != null) {
						ch.rawInfo.setServiceName(str);
					}
					break;

				}

				case UpdateReason.RESET_CH_MASK: {
					int inputID = para[0];
					List<TVChannel> channels = channelsMap
							.get(inputID == 0 ? ANALOG_TV : DIGITAL_TV);
					int num = para[1];
					int mask = para[2];
					TVChannel ch = findChannelByNumber(channels, num);
					if (ch != null) {
						ch.setMask(mask);
					}
					break;
				}

				case UpdateReason.RESET_CH_FREQ: {
					int inputID = para[0];
					List<TVChannel> channels = channelsMap
							.get(inputID == 0 ? ANALOG_TV : DIGITAL_TV);
					int num = para[1];
					int freq = para[2];
					TVChannel ch = findChannelByNumber(channels, num);
					if (ch != null) {
						if (ch.rawInfo instanceof AnalogChannelInfo) {
							((AnalogChannelInfo) ch.rawInfo).setFrequency(freq);
						} else if (ch.rawInfo instanceof DvbChannelInfo) {
							((DvbChannelInfo) ch.rawInfo).setFrequency(freq);
						}
					}
					break;
				}

				case UpdateReason.DELETE_CH: {
					int inputID = para[0];
					List<TVChannel> channels = channelsMap
							.get(inputID == 0 ? ANALOG_TV : DIGITAL_TV);
					int delNum = para[1];
					synchronized (channels) {
						channels.remove(findChannelByNumber(channels, delNum));
					}
					break;
				}

				case UpdateReason.SWAP_CH: {
					int inputID = para[0];
					List<TVChannel> channels = channelsMap
							.get(inputID == 0 ? ANALOG_TV : DIGITAL_TV);
					int lNum = para[1];
					int rNum = para[2];
					TVChannel lhs = findChannelByNumber(channels, lNum);
					TVChannel rhs = findChannelByNumber(channels, rNum);
					if (lhs == null || rhs == null) {
						return;
					}
					lhs.rawInfo.setChannelNumber(rNum);
					rhs.rawInfo.setChannelNumber(lNum);
					synchronized (channels) {
						Collections.sort(channels, mComparator);
					}
					break;
				}

				case UpdateReason.INSERT_CH: {
					int inputID = para[0];
					List<TVChannel> channels = channelsMap
							.get(inputID == 0 ? ANALOG_TV : DIGITAL_TV);
					int dstNum = para[1];
					int srcNum = para[2];
					TVChannel dst = findChannelByNumber(channels, dstNum);
					TVChannel src = findChannelByNumber(channels, srcNum);
					if (dst == null || src == null) {
						return;
					}
					int dstIndex = channels.indexOf(dst);
					int srcIndex = channels.indexOf(src);
					List<TVChannel> subList;
					int offset = 0;
					if (dstIndex > srcIndex) {
						subList = channels.subList(srcIndex, dstIndex);
						offset = 1;
					} else {
						subList = channels.subList(dstIndex + 1, srcIndex + 1);
						offset = -1;
					}

					dst.rawInfo.setChannelNumber(srcNum);
					for (TVChannel ch : subList) {
						ch.rawInfo.setChannelNumber(ch.rawInfo
								.getChannelNumber() + offset);
					}
					synchronized (channels) {
						Collections.sort(channels, mComparator);
					}
					break;
				}

				case UpdateReason.SET_TUNNER:
				case UpdateReason.SCAN_COMPLETE:
				case UpdateReason.ICH_NOTIFY:
				case UpdateReason.CLEAR_CHANNELS:
					TVCommonNative.TVLog("Update all channels.");
					update();
					break;

				default:
					break;
				}
				TVCommonNative.TVLog("DelegaterChannelsChanged !!! ");
				handler.post(new DelegaterChannelsChanged());
			}
		};

		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			data.writeStrongBinder((((lStub != null)) ? (lStub.asBinder())
					: (null)));
			TVCommonNative
					.TVLog("Register listener to monitor the update of channel list.");
			mRemote.transact(ADD_IUPDATE_LISTENER_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			reply.recycle();
			data.recycle();
		}
	}

	@Override
	public void setTunnerMode(int mode) throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			data.writeInt(mode);
			mRemote.transact(SET_TUNNER_MODE_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			reply.recycle();
			data.recycle();
		}
	}

	@Override
	public int getTunnerMode() {
		TVConfigurer cfg = TVConfigurer.getInstance(TVCommonNative.getContext());
		if (null == cfg) {
			TVCommonNative.TVLog("null == cfg, set default Cable DB");
			return ConfigType.BS_SRC_CABLE;
		} 

		/* Notice: grp is 255 when input not ready in tv start */
		int grp = cfg.getGroup();
		TVCommonNative.TVLog("grp: " + grp);
		if (ConfigType.CONFIG_VALUE_GROUP_DTV != grp) {
			return ConfigType.BS_SRC_CABLE;
		}
	
		@SuppressWarnings("unchecked")
		TVOption<Integer> opt = (TVOption<Integer>) TVConfigurer.getInstance(
				mContext).getOption(IntegerOption.CFG_BS_SRC);
		if(opt !=null){
			return opt.get();
		}
		return ConfigType.BS_SRC_CABLE;
	}

	@Override
	public void favoriteChannelDown() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			mRemote.transact(FAVORITE_CHANNEL_DOWN_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public void favoriteChannelUp() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			mRemote.transact(FAVORITE_CHANNEL_UP_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public void clearChannels() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			mRemote.transact(CLEAR_CHANNELS_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public void flush() throws RemoteException {
		try {
			TVCommonNative.TVLog("flush 2 DBs");
			rawChSrv.fsStoreChannelList(ChannelCommon.DB_AIR);
			rawChSrv.fsStoreChannelList(ChannelCommon.DB_CABEL);
		} catch (TVMException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clearAllChannels() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			mRemote.transact(CLEAR_ALL_CHANNELS_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}
	
	@Override
	public void clearAllAirChannels() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			mRemote.transact(CLEAR_ALL_AIR_CHANNELS_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}
	
	@Override
	public void clearAllCableChannels() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			mRemote.transact(CLEAR_ALL_CABLE_CHANNELS_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public synchronized void resetChannelAttribute(TVChannel ch, String str,
			int... para) throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		int reason = para[0];
		data.writeInterfaceToken(descriptor);
		data.writeInt(reason);
		data.writeInt(ch.getChannelNum());
		switch (reason) {
		case UpdateReason.RESET_CH_NUM: {
			int newNum = para[1];
			data.writeInt(newNum);
			break;
		}

		case UpdateReason.RESET_CH_NAME: {
			String name = str;
			data.writeString(name);
			break;
		}

		case UpdateReason.RESET_CH_MASK: {
			int mask = para[1];
			data.writeInt(mask);
			break;
		}

		case UpdateReason.RESET_CH_FREQ: {
			int freq = para[1];
			data.writeInt(freq);
			break;
		}

		default:
			break;
		}

		try {
			mRemote.transact(RESET_CHANNEL_ATTRIBUTE_TRANSACTION, data, reply,
					0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public void selectUp() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			mRemote.transact(SELECT_UP_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public void selectDown() throws RemoteException {
		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();
		try {
			data.writeInterfaceToken(descriptor);
			mRemote.transact(SELECT_DOWN_TRANSACTION, data, reply, 0);
			reply.readException();
		} finally {
			data.recycle();
			reply.recycle();
		}
	}

	@Override
	public List<TVChannel> getChannels(String inputName) throws RemoteException {
		return channelsMap.get(inputName);
	}
}
