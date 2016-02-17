package com.mediatekk.tvcm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.common.TVCommon;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.AnalogChannelInfo;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.DvbChannelInfo;
import com.mediatek.tv.service.ChannelService;
import com.mediatek.tv.service.IChannelNotify;
import com.mediatekk.tvcm.TVChannelList.ChannelFilter;

import android.content.Context;

/**
 * TVChannelManager manages lower Channel DB(SVL) and provides several channel
 * lists. Channel list does not directly mapped to Physical channel DB. They are
 * filtered by filter and notified when channel DB editing. This is highly
 * customized for different customer requirements. All channels are collected
 * from lower DB into channel manager.
 * 
 * This also provides some functions to operate a default list. Note, these
 * functions are not for DB operation but only on a list. This makes old version
 * code work well and some customer implementation easily operate channel list.
 * If a customer implementation needs a whole DB view, a list with NULL filter
 * can be created, like TK.
 * 
 * TVChannelList implementations should filter channel when a channel
 * notification occurs. That means onChannelEdit, onChannelDeleted, onChannelAdd
 * and onClear should check the channel first. This is because a channel edit
 * may make a filter function fail.
 * 
 * Lock/unlock and modification submit are done by preEdit/endEdit. Channel List
 * should enter this section to protect a batch modification.
 * 
 * There is a base channel list implementation named TVChannelListCommon.
 * 
 * 
 * TODO: 1,Indexer: Should the indexer be implemented in list or the DB.
 * 2,Association linked change notify is currently supported. But this may take
 * performance impact if there is much overlapping of lists. Should we ignore
 * this feature or just told user reload the channel list... 3, Listeners should
 * be merged ....
 * 
 * @author mtk40063
 * 
 */
public class TVChannelManager extends TVComponent {
	protected ChannelService rawChSrv;
	protected ReentrantReadWriteLock lock;
	protected ArrayList<TVChannel> channelSet = new ArrayList<TVChannel>();

	private List<ChannelInfo> palChInfo = new ArrayList<ChannelInfo>();
	private List<ChannelInfo> dvbChInfo = new ArrayList<ChannelInfo>();

	TVChannelManager(Context context) {
		super(context);
		lock = new ReentrantReadWriteLock();
		if (!TVContent.dummyMode) {
			rawChSrv = (ChannelService) getTVMngr().getService(
					ChannelService.ChannelServiceName);
			TvLog(rawChSrv.toString());
			update();
			// rawChSrv.addListener(new IChannelNotify() {
			// public void notifyChannelUpdated(int arg0, int arg1, int arg2) {
			// TvInfo("Recevied lower modification");
			// update();
			// flush();
			// }
			// });
		}

	}

	public final static String LIST_PAL_NAME = "CHLST[PAL]";
	public final static String LIST_DVB_NAME = "CHLST[DVB]";

	/**
	 * TVChannelManager base init does not create list.
	 */
	protected void init() {
		// TODO: What DBs are loading should be determined by Scanner?
		createCommonList(LIST_PAL_NAME, new TVChannelList.ChannelFilter() {
			public boolean filter(TVChannel channel) {
				if (channel.getStandard().equals(TVChannel.STANDARD_PAL)) {
					return true;
				}
				return false;
			}
		});
		createCommonList(LIST_DVB_NAME, new TVChannelList.ChannelFilter() {
			public boolean filter(TVChannel channel) {
				if (channel.getStandard().equals(TVChannel.STANDARD_DVB)) {
					return true;
				}
				return false;
			}
		});

	}

	/**
	 * src will not receive notify of onChannelAdd
	 * 
	 * @param channel
	 * @param src
	 */
	protected void addChannel(TVChannel channel, TVChannelList src) {
		TvInfo("Add new Channel to CM " + channel);
		if (channel.getRawInfo().isUserDelete() == false) {
		lock.writeLock().lock();
		channelSet.add(channel);
		for (TVChannelList cl : listTable.values()) {
			TvInfo("Add new Channel to List " + channel);
			if (cl != src) {
				cl.onChannelAdd(channel);
			}
		}
		lock.writeLock().unlock();
	}
	}

	protected void delChannel(TVChannel channel, TVChannelList src) {
		lock.writeLock().lock();
		if (channelSet.contains(channel)) {
			TvInfo("Del Channel " + channel);
			channelSet.remove(channel);
			ArrayList<ChannelInfo> rawList = new ArrayList<ChannelInfo>();
			rawList.add(channel.getRawInfo());
			try {
				(channel.getRawInfo()).setChannelDeleted(true);
				rawChSrv.setChannelList(ChannelService.ChannelOperator.DELETE,
						ChannelCommon.DB_ANALOG, rawList);
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (TVChannelList cl : listTable.values()) {
				if (cl != src) {
					cl.onChannelDeleted(channel);
				}
			}
		}
		lock.writeLock().unlock();
	}

	// static interface RawInfoVistor {
	// void visit(ChannelInfo item);
	// }

	// void load(String dbName) {
	// update();
	// }

	void cleanDB() {
		if (!dummyMode) {
			if (rawChSrv != null) {
				ChannelInfo info;
				List<ChannelInfo> rawList;
				try {
					// TODO: We need to visit several db names...
					rawList = rawChSrv.getChannelList(ChannelCommon.DB_ANALOG);
					rawChSrv.setChannelList(
							ChannelService.ChannelOperator.DELETE,
							ChannelCommon.DB_ANALOG, rawList);
					TvLog("Delete all DB");
					rawChSrv.digitalDBClean(ChannelCommon.DB_ANALOG);
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {

		}
	}

	/**
	 * We may move this into a thread..
	 */
	public void update() {
		if (rawChSrv != null) {
			List<ChannelInfo> rawList;
			try {
				lock.writeLock().lock();
				for (TVChannelList cl : listTable.values()) {
					cl.onClear();
				}
				for (TVChannel ch : channelSet) {
					ch.invalid();
				}
				channelSet.clear();

				rawList = rawChSrv.getChannelList(ChannelCommon.DB_ANALOG);
				for (ChannelInfo rawInfo : rawList) {
					TVChannel ch = new TVChannel(this, rawInfo);
					addChannel(ch, null);
				}
				lock.writeLock().unlock();

			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getContent().sendUpdate();
		}
	}

	Hashtable<String, TVChannelList> listTable = new Hashtable<String, TVChannelList>();

	/**
	 * Create different list.
	 * 
	 * @param name
	 * @param filter
	 * @return
	 */
	public TVChannelList createCommonList(String name,
			TVChannelList.ChannelFilter filter) {
		TVChannelListCommon list = new TVChannelListCommon(filter, context);
		addList(name, list, filter);
		return list;
	}

	protected void addList(String name, TVChannelList list,
			TVChannelList.ChannelFilter filter) {
		for (TVChannel ch : channelSet) {
			if (filter != null) {
				if (filter.filter(ch)) {
					list.onChannelAdd(ch);
				}
			} else {
				list.onChannelAdd(ch);
			}
		}
		listTable.put(name, list);
	}

	public TVChannelList getList(String name) {
		if (listTable.containsKey(name)) {
			return listTable.get(name);
		}
		return null;
	}

	TVChannelList defaultList = null;

	public synchronized TVChannelList getCurrList() {
		if (defaultList == null) {
			defaultList = createCommonList("default", null);
		}
		return defaultList;
	}

	public synchronized void setCurrList(String name) {
		TVChannelList list = getList(name);
		if (list != null) {
			defaultList = list;
			getHandler().post(new Runnable() {
				public void run() {
					for (TVChannelList eachList : listTable.values()) {
						eachList.notifyListeners();
					}

				}
			});
		}
	}

	/**
	 * Get some channels after start index, with a filter.
	 * 
	 * @param startIdx
	 * @param count
	 * @param filter
	 * @return
	 */
	public List<TVChannel> getChannels(int startIdx, int count,
			TVChannelList.ChannelFilter filter) {
		return getCurrList().getChannels(startIdx, count, filter);
	}

	public int getChannelLength() {
		return getCurrList().getChannelLength();
	}

	/**
	 * Get channels after start index, with a filter.
	 * 
	 * @param startIdx
	 * @param filter
	 * @return
	 */
	public List<TVChannel> getChannels(int startIdx,
			TVChannelList.ChannelFilter filter) {
		return getCurrList().getChannels(startIdx,
				getCurrList().getChannelLength(), filter);
	}

	/**
	 * Get channel list in a specified order
	 * 
	 * @param startIdx
	 * @param count
	 * @param filter
	 * @param s
	 * @return
	 */
	public List<TVChannel> getChannels(int startIdx, int count,
			TVChannelList.ChannelFilter filter, Comparator<TVChannel> s) {
		List<TVChannel> list = getCurrList().getChannels(startIdx, count,
				filter);
		Collections.sort(list, s);
		return list;
	}

	/**
	 * Get all channels
	 * 
	 * @return
	 */
	public List<TVChannel> getChannels() {
		return getCurrList().getChannels(null);
	}

	/**
	 * Get a channel list.
	 * 
	 * @param startIdx
	 *            Start index
	 * @param count
	 *            : Length of list
	 * @return
	 */
	public List<TVChannel> getChannels(int startIdx, int count) {
		return getCurrList().getChannels(startIdx, count, null);
	}

	/**
	 * Get a channel list contains all favorite channels.
	 * 
	 * @return
	 */
	public List<TVChannel> getFavoriteChannels() {
		return getChannels(0, ChannelFilter.TVFavoriteFilter);
	}

	/**
	 * Get a channel list contains favorite channels.
	 * 
	 * @param startIdx
	 *            Start index of 'favorite list'.
	 * @param count
	 *            Length of list
	 * @return
	 */
	public List<TVChannel> getFavoriteChannels(int startIdx, int count) {
		return getChannels(startIdx, count, ChannelFilter.TVFavoriteFilter);
	}

	/**
	 * Find channel with channel number
	 * 
	 * @param num
	 * @return
	 */
	public TVChannel findChannelByNumber(short num) {
		return getCurrList().findChannelByNumber(num);
	}

	/**
	 * Get next channel. If it cannot find , arg channel self is returned
	 * 
	 * @param ch
	 * @param round
	 *            : Turn back to head?
	 * @param filter
	 *            : Can be null.
	 * @return return channel self if not found, or the channel after the ch and
	 *         in filter.
	 */
	public TVChannel nextChannel(TVChannel ch, boolean round,
			TVChannelList.ChannelFilter filter) {
		return getCurrList().nextChannel(ch, round, filter);
	}

	/**
	 * Get prev channel. If it cannot find , arg channel self is returned
	 * 
	 * @param ch
	 * @param round
	 *            : Turn back to tail?
	 * @param filter
	 *            : Can be null.
	 * @return return channel self if not found, or the channel before the ch
	 *         and in filter.
	 */
	public TVChannel prevChannel(TVChannel ch, boolean round,
			TVChannelList.ChannelFilter filter) {
		return getCurrList().prevChannel(ch, round, filter);
	}

	public TVChannel firstChannel(boolean round, ChannelFilter filter) {
		return getCurrList().firstChannel(round, filter);

	}

	public TVChannel lastChannel(boolean round, ChannelFilter filter) {
		// TODO Auto-generated method stub
		return getCurrList().lastChannel(round, filter);
	}

	/**
	 * Clear the whole list, including raw data.
	 */
	public void clear() {
		lock.writeLock().lock();
		for (TVChannelList cl : listTable.values()) {
			cl.onClear();
		}
		for (TVChannel ch : channelSet) {
			ch.invalid();
		}
		channelSet.clear();

		cleanDB();

		lock.writeLock().unlock();

		getContent().sendUpdate();
	}

	/**
	 * Clear the whole list, including raw data.
	 */
	// public void clear(TVChannelList.ChannelFilter filter) {
	// ArrayList<TVChannel> delChannels = new ArrayList<TVChannel>();
	// for (TVChannel each : channelSet) {
	// if (filter.filter(each)) {
	// delChannels.add(each);
	// }
	// }
	// for (TVChannel each : delChannels) {
	// delChannel(each, null);
	// }
	// getHandler().post(new Runnable() {
	// public void run() {
	// getContent().updatePipeLine(true);
	// }
	// });
	// }

	public void insertChannelAfter(TVChannel chSrc, TVChannel chDst) {
		getCurrList().insertChannelAfter(chSrc, chDst);
	}

	/**
	 * Insert the channel before the channelAfter.
	 * 
	 * @param channelSrc
	 * @param channelDest
	 */
	public void insertChannel(TVChannel chSrc, TVChannel chDst) {
		getCurrList().insertChannel(chSrc, chDst);
	}

	/**
	 * Swap two channels.
	 * 
	 * @param ch1
	 * @param ch2
	 */
	public void swapChannel(TVChannel ch1, TVChannel ch2) {
		getCurrList().swapChannel(ch1, ch2);
	}

	/**
	 * Delete channel
	 * 
	 * @param ch
	 */
	public void deleteChannel(TVChannel ch) {
		getCurrList().deleteChannel(ch);
	}

	private int editCount = 0;

	void preEdit() {
		lock.writeLock().lock();
		editCount++;
		TvLog("Enter CM Write Lock, with count " + editCount);

	}

	Vector<TVChannel> updateRequest = new Vector<TVChannel>();

	void endEdit(TVChannelList list) {
		editCount--;
		if (editCount == 0) {
			for (TVChannel each : updateRequest) {
				for (TVChannelList cl : listTable.values()) {
					if (cl != list) {
						cl.onChannelEdit(each);
					}
				}
			}
			updateRequest.clear();
		}
		TvLog("Leave CM Write Lock, with count " + editCount + " from src "
				+ list);
		lock.writeLock().unlock();
	}

	void endEdit(TVChannel ch) {
		updateRequest.add(ch);
		editCount--;
		if (editCount == 0) {
			for (TVChannel each : updateRequest) {
				for (TVChannelList cl : listTable.values()) {
					cl.onChannelEdit(each);
				}
			}
			updateRequest.clear();
		}
		TvLog("Leave CM Write Lock, with count " + editCount + " from ch " + ch);
		lock.writeLock().unlock();
	}

	void preRead() {
		TvLog("Enter CM Read Lock");
		lock.readLock().lock();
	}

	void endRead() {
		TvLog("Leave CM Read Lock");
		lock.readLock().unlock();
	}

	/**
	 * Register channel list listener.
	 * 
	 * @param listener
	 */
	public void registerListListener(TVChannelList.ListChangedListener listener) {
		for (TVChannelList list : listTable.values()) {
			list.registerListListener(listener);
		}
	}

	/**
	 * Remove channel list listener.
	 * 
	 * @param listener
	 */
	public void removeListListener(TVChannelList.ListChangedListener listener) {
		for (TVChannelList list : listTable.values()) {
			list.removeListListener(listener);
		}
	}

	/**
	 * Flush the operation to persistent storage.
	 */
	public void flush() {
		lock.writeLock().lock();
		try {
			rawChSrv.fsStoreChannelList(ChannelCommon.DB_ANALOG);
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lock.writeLock().unlock();
	}

	static short dummyNum = 0;

	public void dummyAdd(String name) {
		TVChannel item;
		item = new TVChannel(this, null);

		item.setChannelName(name);
		item.setChannelNum(dummyNum++);
		channelSet.add(item);

		for (TVChannelList cl : listTable.values()) {
			TvInfo("Add new Channel to List " + item);
			cl.onChannelAdd(item);
		}
	}

	public void addPalPreSetChannel(PalPreSetChannel ch) {
		if (ch != null) {
			AnalogChannelInfo chInfo = new AnalogChannelInfo(
					ChannelCommon.DB_ANALOG);
			if (chInfo != null) {
				chInfo.setBrdcstMedium(TVCommon.BRDCST_MEDIUM_ANA_CABLE);
				chInfo.setChannelNumber(ch.getChannelNum());
				chInfo.setServiceName(ch.getChannelName());
				chInfo.setFrequency(ch.getFrequency());
				chInfo.setColorSys(ch.getColorSys());
				chInfo.setTvSys(ch.getTvSys());
				chInfo.setAudioSys(ch.getAudioSys());
				palChInfo.add(chInfo);
			}
		}
	}

	public void addDvbPreSetChannel(DvbPreSetChannel ch) {
		if (ch != null) {
			DvbChannelInfo chInfo = new DvbChannelInfo(ChannelCommon.DB_ANALOG);
			if (chInfo != null) {
				chInfo.setBrdcstMedium(TVCommon.BRDCST_MEDIUM_DIG_CABLE);
				chInfo.setChannelNumber(ch.getChannelNum());
				chInfo.setServiceName(ch.getChannelName());
				chInfo.setFrequency(ch.getFrequency());
				chInfo.setBandWidth(ch.getBindWidth());
				chInfo.setNwId(ch.getNetworkId());
				chInfo.setOnId(ch.getOnId());
				chInfo.setTsId(ch.getTsId());
				chInfo.setProgId(ch.getProgId());
				chInfo.setSymRate(ch.getSymRate());
				chInfo.setMod(ch.getMod());
				dvbChInfo.add(chInfo);
			}
		}
	}

	public void setPalPreSetChannel() {
		if (palChInfo != null) {
			try {
				ChannelService cs = (ChannelService) this.getTVMngr()
						.getService(ChannelService.ChannelServiceName);
				cs.setChannelList(ChannelService.ChannelOperator.APPEND,
						ChannelCommon.DB_ANALOG, palChInfo);
				update();
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setDvbPreSetChannel() {
		if (dvbChInfo != null) {
			try {
				ChannelService cs = (ChannelService) this.getTVMngr()
						.getService(ChannelService.ChannelServiceName);
				cs.setChannelList(ChannelService.ChannelOperator.APPEND,
						ChannelCommon.DB_ANALOG, dvbChInfo);
				update();
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void clearPalPreSetChannel() {
		palChInfo.clear();
	}

	public void clearDvbPreSetChannel() {
		dvbChInfo.clear();
	}
}