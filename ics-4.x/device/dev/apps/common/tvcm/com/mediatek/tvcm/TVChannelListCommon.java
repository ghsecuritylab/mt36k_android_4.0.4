package com.mediatek.tvcm;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.content.Context;

import com.mediatek.tv.service.ChannelService; //import com.mediatek.tv.service.ScanService;

/**
 * User can get channel list with filter. When the list changes, list listeners
 * will be called. User can get next/prev channel to specified channel.
 * 
 * @author mtk40063
 * 
 */
public class TVChannelListCommon extends TVComponent implements TVChannelList {

	private Vector<ListChangedListener> listeners = new Vector<ListChangedListener>();
	//protected ReentrantReadWriteLock lock;
	protected ChannelService rawSrv = null;
	final ChannelFilter filter;
	private TVChannelManager cm;

	TVChannelListCommon(ChannelFilter filter, Context context) {
		super(context);
		if (filter != null) {
			this.filter = filter;
		} else {
			this.filter = emptyFilter;
		}

		cm = getContent().getChannelManager();
		//lock = new ReentrantReadWriteLock();
		// TMP, this must be used as an argument

	}

	void init() {

	}

	// 
	// protected TreeMap<Short, TVChannel> numberMap = new TreeMap<Short,
	// TVChannel>();
	protected LinkedList<TVChannel> list = new LinkedList<TVChannel>();

	protected void onStart() {

	}

	public List<TVChannel> getChannels(ChannelFilter filter) {
		List<TVChannel> res = new ArrayList<TVChannel>();
		if (filter == null) {
			filter = emptyFilter;
		}
		cm.preRead();
		Iterator<TVChannel> itr = list.iterator();
		while (itr.hasNext()) {
			// TVChannel channel = channels.get(i);
			TVChannel channel = itr.next();
			if (filter.filter(channel) && channel.getRawInfo().isUserDelete() == false) {
				res.add(channel);
			}
		}
		cm.endRead();
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediatek.tvcm.ChannelList#getChannels(int, int,
	 * com.mediatek.tvcm.TVChannelList.ChannelFilter)
	 */
	public List<TVChannel> getChannels(int startIdx, int count,
			ChannelFilter filter) {
		List<TVChannel> res = getChannels(filter);
		if (startIdx < 0) {
			startIdx = 0;
		} else if (startIdx >= res.size()) {
			// Empty
			return new ArrayList<TVChannel>();
		}
		if (/* startIdx + */count < 0 /* startIdx */) {
			return new ArrayList<TVChannel>();
		} else if (startIdx + count > res.size()) {
			count = res.size() - startIdx;
		}
		return res.subList(startIdx, startIdx + count);
	}

	/**
	 * @deprecated
	 */
	public int getChannelLength() {
		return list.size();
	}

	public TVChannel findChannelByNumber(short num) {
		TVChannel ch = null;
		// STUPID:
		// TODO: Use indexer in CM.
		cm.preRead();
		for (TVChannel each : list) {
			if (each.getChannelNum() == num) {
				ch = each;
				break;
			}
		}
		cm.endRead();
		return ch;
	}

	protected TVChannel next(TVChannel ch, boolean round) {
		TVChannel channel;
		int idx = list.indexOf(ch);
		int next;
		if (idx == -1) {
			return null;
		}
		next = idx + 1;
		if (next == list.size()) {
			next = 0;
			if (next == idx) {
				return null;
			}
		}
		return list.get(next);
	}
	public TVChannel nextChannel(TVChannel ch, boolean round,
			ChannelFilter filter) {

		if (ch == null || !ch.isValid()) {
			return ch;
		}

		if (filter == null) {
			filter = emptyFilter;
		}
		TVChannel channel;
		cm.preRead();
		for (channel = next(ch, round); channel != null && !channel.equals(ch); channel = next(channel,
				round)) {
			if (filter.filter(channel)) {
				cm.endRead();
				return channel;
			}
		}
		cm.endRead();
		return ch;
	}

	ChannelFilter emptyFilter = new ChannelFilter() {
		public boolean filter(TVChannel channel) {
			return true;
		}
	};

	protected TVChannel prev(TVChannel ch, boolean round) {

		int idx = list.indexOf(ch);
		int prev;
		if (idx == -1) {
			return null;
		}
		prev = idx - 1;
		if (prev == -1) {
			prev = list.size() - 1;
			if (prev == idx) {
				return null;
			}
		}
		return list.get(prev);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mediatek.tvcm.ChannelList#prevChannel(com.mediatek.tvcm.TVChannel,
	 * boolean, com.mediatek.tvcm.TVChannelList.ChannelFilter)
	 */
	public TVChannel prevChannel(TVChannel ch, boolean round,
			ChannelFilter filter) {
		if (ch == null || !ch.isValid()) {
			return ch;
		}
		if (filter == null) {
			filter = emptyFilter;
		}
		TVChannel channel;
		cm.preRead();
		for (channel = prev(ch, round); channel != null && !channel.equals(ch); channel = prev(channel,
				round)) {
			if (filter.filter(channel)) {
				cm.endRead();
				return channel;
			}
		}
		cm.endRead();
		return ch;
	}
	
	public TVChannel firstChannel(boolean round,
			ChannelFilter filter) {
		if(list.isEmpty()) {
			return null;
		}
		if (filter == null) {
			filter = emptyFilter;
		}
		TVChannel channel;
		
		cm.preRead();
		for (channel = list.getFirst(); channel != null; channel = next(channel,
				round)) {
			if (filter.filter(channel)) {
				cm.endRead();
				return channel;
			}
		}
		cm.endRead();
		return null;
	}

	public TVChannel lastChannel(boolean round,
			ChannelFilter filter) {
		if(list.isEmpty()) {
			return null;
		}
		if (filter == null) {
			filter = emptyFilter;
		}
		TVChannel channel;
		
		cm.preRead();
		for (channel = list.getLast(); channel != null; channel = prev(channel,
				round)) {
			if (filter.filter(channel)) {
				cm.endRead();
				return channel;
			}
		}
		cm.endRead();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediatek.tvcm.ChannelList#clear()
	 */
	public void onClear() {
		list.clear();
		notifyListeners();
	}

	public void insertChannelAfter(TVChannel chSrc, TVChannel chDst) {

		if (chDst == null || chSrc == null) {
			return;
		}

		chSrc.checkValid();
		chDst.checkValid();

		if (chDst == chSrc) {
			return;
		}

		int idx;
		
		cm.preEdit();
		
		list.remove(chDst);
		idx = list.indexOf(chSrc) + 1;
		if (idx == list.size()) {
			// Should be no problem
		}
		list.add(idx, chDst);
		TVChannel last = chDst;
		for (int i = idx + 1; i < list.size(); i++) {
			TVChannel each = list.get(i);
			if (each.getChannelNum() <= last.getChannelNum()) {
				each.setChannelNum((short) (last.getChannelNum() + 1));
			}
			last = each;
		}

		cm.endEdit(this);

		notifyListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mediatek.tvcm.ChannelList#insertChannel(com.mediatek.tvcm.TVChannel,
	 * com.mediatek.tvcm.TVChannel)
	 */
	public void insertChannel(TVChannel chSrc, TVChannel chDst) {
		if (chDst == null || chSrc == null) {
			return;
		}

		chSrc.checkValid();
		chDst.checkValid();

		if (chDst == chSrc) {
			return;
		}

		int idx;
		cm.preEdit();

		list.remove(chDst);
		idx = list.indexOf(chSrc);
		list.add(idx, chDst);
		TVChannel last = chDst;
		for (int i = idx + 1; i < list.size(); i++) {
			TVChannel each = list.get(i);
			if (each.getChannelNum() <= last.getChannelNum()) {
				each.setChannelNum((short) (last.getChannelNum() + 1));
			}
			last = each;
		}

		cm.endEdit(this);
		notifyListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mediatek.tvcm.ChannelList#swapChannel(com.mediatek.tvcm.TVChannel,
	 * com.mediatek.tvcm.TVChannel)
	 */
	public void swapChannel(TVChannel ch1, TVChannel ch2) {
		// TODO: Write lock

		if (ch1 == ch2) {
			return;
		}
		if (ch1 == null || ch2 == null) {
			return;
		}
		ch1.checkValid();
		ch2.checkValid();

		cm.preEdit();

		short tmp = (short) ch1.getChannelNum();
		ch1.setChannelNum(ch2.getChannelNum());
		ch2.setChannelNum(tmp);

		list.remove(ch1);
		list.remove(ch2);
		onChannelAdd(ch1);
		onChannelAdd(ch2);

		cm.endEdit(this);

		notifyListeners();
	}
	
	public void deleteChannel(TVChannel ch) {
		cm.preEdit();
		if(list.contains(ch)) {
			list.remove(ch);			
			cm.delChannel(ch, this);
			cm.endEdit(this);
			notifyListeners();
		} else {
			cm.endEdit(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mediatek.tvcm.ChannelList#deleteChannel(com.mediatek.tvcm.TVChannel)
	 */
	public void onChannelDeleted(TVChannel ch) {
		ch.checkValid();

		// numberMap.remove(ch.getChannelNum());
		list.remove(ch);
		notifyListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mediatek.tvcm.ChannelList#addChannel(com.mediatek.tvcm.TVChannel)
	 */
	public void onChannelAdd(TVChannel ch) {
		boolean changed = false;
		ch.checkValid();
		if(!filter.filter(ch)) {
			return;
		}
		// numberMap.put(ch.getChannelNum(), ch);
		if (list.indexOf(ch) == -1) {			
			ListIterator<TVChannel> itr = list.listIterator();
			while (itr.hasNext()) {
				TVChannel each = itr.next();
				if (each.getChannelNum() > ch.getChannelNum()) {
					TvLog("Add " + ch + " to common list");
					if (itr.hasPrevious()) {
						itr.previous();
					}
					itr.add(ch);
					notifyListeners();
					return;
				}
			}
			TvLog("Add " + ch + " to common list");
			notifyListeners();
			itr.add(ch);
		}

	}

	public void onChannelEdit(TVChannel ch) {
		// TODO:stupid implementation.
		if (list.indexOf(ch) == -1) {
			// Not in list. add it.
			if (filter.filter(ch)) {
				onChannelAdd(ch);
			}
		} else {
			list.remove(ch);
			if (filter.filter(ch)) {
				onChannelAdd(ch);
			}
		}
		// numberMap.put(ch.getChannelNum(), ch);

	}

	public synchronized void registerListListener(ListChangedListener listener) {
		listeners.add(listener);
	}

	public synchronized void removeListListener(ListChangedListener listener) {
		listeners.remove(listener);

	}

	// TODO: We don't notify listeners when raw intent received. Instead,
	// we need notify user when all the channels are completely built.
	public void notifyListeners() {
		getHandler().post(new Runnable() {
			public void run() {
				ListChangedListener listener;
				Enumeration<ListChangedListener> e;
				synchronized (this) {
					e = (Enumeration<ListChangedListener>) ((Vector<ListChangedListener>) listeners
							.clone()).elements();

				}
				while (e.hasMoreElements()) {
					listener = (ListChangedListener) e.nextElement();
					listener.onChanged();
				}
			}
		});
	}

	
}
