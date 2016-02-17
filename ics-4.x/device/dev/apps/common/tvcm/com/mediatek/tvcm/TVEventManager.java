package com.mediatek.tvcm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.EventInfo;
import com.mediatek.tv.model.EventUpdateReason;
import com.mediatek.tv.service.EventService;
import com.mediatek.tv.service.IEventNotify;
import android.content.Context;
import android.database.Cursor;

/**
 * This Class mainly manage Electronic program guides (EPG) which provide users
 * of television, radio, and other media applications with continuously updated
 * menus displaying broadcast programming or scheduling information for current
 * and upcoming programming.
 */
public class TVEventManager extends TVComponent {
	protected EventService rawSrv;
	static boolean dummyMode = false;
	private Thread tvEventScheduleThread = null;
	private boolean isStop = false;
	private TVStorage st = getContent().getStorage();
	private TVTimerManager tm = getContent().getTimerManager();

	class IEventNotifyDelegate implements IEventNotify {

		public void notifyUpdate(EventUpdateReason reason, final int svlid,
				final int channelId) {
			switch (reason) {
			case EVENT_REASON_PF_UPDATE:
				// TODO: Check id with current channel.
				notifyPFChange();
				break;
			case EVENT_REASON_SCHEDULE_UPDATE:
				getHandler().post(new Runnable() {

					public void run() {
						// TODO Auto-generated method stub
				            schedMonitor.onSchedulChange(svlid, channelId);
					}
				});
				break;
			default:
				// How to reach?
			}

		}
	}

	public TVEventManager(Context context) {

		super(context);
		if (!dummyMode) {
			rawSrv = (EventService) getTVMngr().getService(
					EventService.EventServiceName);
			rawSrv.addListener(new IEventNotifyDelegate());
		}
	}

	void init() {
	}

	
	public void setCommand(TVEventCommand eventCommand){
		if(!dummyMode&&rawSrv!=null){
			try {
				rawSrv.eventSetCommand(eventCommand);
	        } catch (TVMException e) {
	            e.printStackTrace();
	        }
		}

	}
	/**
	 * @return the current and the next TV event of current TV channel.
	 */
	public TVEvent[] getPFEvent() {
		TVEvent[] events = new TVEvent[] { null, null };
		if (!dummyMode) {
			try {
				TVChannel curCh = getContent().getChannelSelector()
						.getCurrentChannel();
				if (curCh != null) {
					rawSrv.setCurrentChannel(curCh.getRawInfo());
					ArrayList<EventInfo> rawEvents = rawSrv.getPFEvents(curCh
							.getRawInfo());

					if (rawEvents.size() > 0) {
						if (rawEvents.get(0) != null) {
							events[0] = new TVEvent(rawEvents.get(0), curCh);
						}
					}
					if (rawEvents.size() > 1) {
						if (rawEvents.get(1) != null) {
							events[1] = new TVEvent(rawEvents.get(1), curCh);
						}
					}
				}
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return events;
		} else {
			TVChannel curCh = getContent().getChannelSelector()
					.getCurrentChannel();
			return new TVEvent[] {
					new TVEvent.DummyEvent(curCh, System.currentTimeMillis()
							% (1200 * 1000), 1000 * 1200),
					new TVEvent.DummyEvent(curCh, 1000 * 600
							+ System.currentTimeMillis() % (1200 * 1000),
							1000 * 1200) };
		}

	}

	/**
	 * Interface definition for a callback to invoke when either current or the
	 * next TV event changed.
	 */
	public static interface PFEventListener {
		/**
		 * Called when either the current or the next TV event changed.
		 */
		public void onChange();
	}

	/**
	 * Interface definition for a callback to invoke when read TV events from
	 * monitor.
	 */
	public static interface EventListener {
		public void onChange(TVChannel ch);
	}

	TVSchedEventMonitor schedMonitor = new TVSchedEventMonitor(this);

	// TODO:Check whether we need multiple monitors.
	// Currently EM only support single monitor because
	// lower TV Manager just support monitoring one 'window',
	// however, it is possible to support multiple monitors
	// by merging the request and managing listener dispatching..
	// public TVSchedEventMonitor getSchedMonitor(String name) {
	// }
	/**
	 * @return TVSchedEventMonitor
	 */
	public TVSchedEventMonitor getSchedMonitor() {
		return schedMonitor;
	}

	private Vector<PFEventListener> pfListeners = new Vector<PFEventListener>(3);

	/**
	 * register PFEventListener
	 */
	public synchronized void registerPFEventListener(PFEventListener listener) {
		pfListeners.add(listener);
	}

	/**
	 * unregister PFEventListener
	 */
	public synchronized void unregisterPFEventListener(PFEventListener listener) {
		pfListeners.remove(listener);
	}

	protected void notifyPFChange() {
		final Vector<PFEventListener> listeners = (Vector<PFEventListener>) pfListeners
				.clone();
		getHandler().post(new Runnable() {
			public void run() {
				for (PFEventListener listener : listeners) {
					listener.onChange();
				}
			}
		});
	}

	class ScheduleTVEventTimer implements Runnable {

		public void run() {
			while (!isStop) {
				try {
					Thread.sleep(1000);
					synchronized (TVEventManager.this) {
						Set<Map.Entry<TVChannel, TreeSet<TVEvent>>> set = scheduleMap
								.entrySet();
						for (Map.Entry<TVChannel, TreeSet<TVEvent>> entry : set) {
							List<TVEvent> delList = new ArrayList<TVEvent>();
							for (TVEvent event : entry.getValue()) {
								if (tm.getBroadcastUTC() >= event
										.getStartTime()
										+ event.getOffset()) {
									event.setOffset(0L);
									event.setSchedule(false);
									// entry.getValue().remove(event);
									delList.add(event);
									// remove schedule event from database
									st.deleteTVEventRecord(event.getChannel()
											.getRawInfo().getSvlRecId(), event
											.getStartTime());
									notifyTVEventTimeUpListener(entry.getKey(),
											event);
								}
							}
							entry.getValue().removeAll(delList);
						}
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Interface definition for a callback to invoke when the scheduled event
	 * time up.
	 */
	public interface TVEventTimeUp {
		/**
		 * Called when time up
		 * 
		 * @param event
		 *            the time up schedule TV event
		 * @param ch
		 *            the channel which event belong to
		 */
		public void onTimeUp(TVChannel ch, TVEvent event);
	}

	List<TVEventTimeUp> timers = new ArrayList<TVEventTimeUp>();

	/**
	 * register TVEventTimeUp listener
	 */
	public void registerTVEventTimeUpListener(TVEventTimeUp timer) {
		if (timer == null) {
			throw new IllegalArgumentException("timer is null");
		}

		synchronized (this) {
			if (!timers.contains(timer)) {
				timers.add(timer);
			}
		}
	}

	/**
	 * remove TVEventTimeUp listener
	 */
	public void removeTVEventTimeUpListener(TVEventTimeUp timer) {
		timers.remove(timer);
	}

	/**
	 * clear all TVEventTimeUp listeners
	 */
	public void clearTVEventTimeUpListener() {
		timers.clear();
	}

	protected void notifyTVEventTimeUpListener(final TVChannel ch,
			final TVEvent event) {
		getHandler().post(new Runnable() {
			public void run() {
				for (TVEventTimeUp timer : timers) {
					timer.onTimeUp(ch, event);
				}
			}
		});
	}

	private Map<TVChannel, TreeSet<TVEvent>> scheduleMap = new LinkedHashMap<TVChannel, TreeSet<TVEvent>>();

	/**
	 * @return TV channels which contain scheduled TV events from database.
	 * @param chs
	 */
	public TVChannel[] getScheduleTVChannelsFromDB(List<TVChannel> chs) {
		List<TVChannel> schedules = new ArrayList<TVChannel>();
		Integer[] ids = st.queryDistinctTVChannelsID();
		if (ids == null) {
			return null;
		}
		for (TVChannel ch : chs) {
			for (Integer id : ids) {
				if (id == ch.getRawInfo().getSvlRecId()) {
					schedules.add(ch);
					break;
				}
			}
		}
		if (schedules.size() == 0) {
			return null;
		} else {
			return schedules.toArray(new TVChannel[0]);
		}
	}

	/**
	 * @return all scheduled TV events
	 * 
	 */
	public TVEvent[] getScheduleTVEvents() {
		Set<TVEvent> events = new TreeSet<TVEvent>();
		for (TreeSet<TVEvent> set : scheduleMap.values()) {
			events.addAll(set);
		}
		return events.toArray(new TVEvent[0]);
	}

	/**
	 * @return the TV events of specified TV channel
	 * @param ch
	 *            specify the channel to search scheduled TV events
	 */
	public TVEvent[] getScheduleTVEvents(TVChannel ch) {
		return scheduleMap.get(ch).toArray(new TVEvent[0]);
	}

	/**
	 * schedule the specified TV event
	 * 
	 * @param event
	 *            the TV event to schedule
	 * @param offset
	 *            if offset is positive, then delay the notification, or vice
	 *            versa
	 */
	public synchronized void scheduleTVEvent(TVEvent event, long offset) {
		if (tm.getBroadcastUTC() >= event.getStartTime()) {
			return;
		}
		event.setOffset(offset);
		event.setSchedule(true);
		loadScheduleMap(event);
		// add new schedule event to database
		st.insertTVEventRecord(event.getChannel().getRawInfo().getSvlRecId(),
				event.getStartTime(), offset);
		startTVEventSchedule();
	}

	/**
	 * cancel the scheduled TV event
	 */
	public synchronized void unscheduleTVEvent(TVEvent event) {
		if (scheduleMap.containsKey(event.channel)) {
			TreeSet<TVEvent> set = scheduleMap.get(event.channel);
			if (set.contains(event)) {
				set.remove(event);
				event.setOffset(0L);
				event.setSchedule(false);
				// remove schedule event from database
				st.deleteTVEventRecord(
						event.channel.getRawInfo().getSvlRecId(), event
								.getStartTime());
				if (scheduleMap.get(event.channel).size() == 0) {
					scheduleMap.remove(event.channel);
				}
			}
		}
		stopTVEventSchedule();
	}

	private void startTVEventSchedule() {
		if (tvEventScheduleThread == null && scheduleMap.size() > 0) {
			isStop = false;
			tvEventScheduleThread = new Thread(new ScheduleTVEventTimer());
			tvEventScheduleThread.start();
		}
	}

	private void stopTVEventSchedule() {
		if (scheduleMap.size() == 0) {
			isStop = true;
			tvEventScheduleThread = null;
		}
	}

	private void loadScheduleMap(TVEvent event) {
		if (event != null) {
			if (!scheduleMap.containsKey(event.channel)) {
				TreeSet<TVEvent> eventSet = new TreeSet<TVEvent>();
				eventSet.add(event);
				scheduleMap.put(event.channel, eventSet);
			} else {
				scheduleMap.get(event.channel).add(event);
			}
		}
	}

	/**
	 * After successfully read TV events( call method syncRead or asyncRead in
	 * class TVSchedEventMonitor), it must call this method to load the
	 * scheduled events.
	 */
	public void loadScheduleTVEvents() {
		Cursor cursor = st.queryTVEventRecords();
		if (cursor == null) {
			return;
		}

		try {
			int chIndex = cursor.getColumnIndex(TVStorage.CHANNEL_ID);
			int startIndex = cursor.getColumnIndex(TVStorage.START_TIME);
			int offsetIndex = cursor.getColumnIndex(TVStorage.OFFSET_TIME);
			TVChannel ch;
			TVEvent[] events;
			TVEvent event;
			cursor.moveToFirst();
			if (cursor.getPosition() < cursor.getCount()) {
				do {
					ch = findChannelbysvlRecId(cursor.getInt(chIndex));
					if (ch != null) {
						events = schedMonitor.getEvents(ch);
						event = findTVEventbyStartTime(cursor
								.getLong(startIndex), events);
						if (event != null) {
							event.setOffset(cursor.getLong(offsetIndex));
							event.setSchedule(true);
							loadScheduleMap(event);
						}
					}
				} while (cursor.moveToNext());

				startTVEventSchedule();
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	// Fri Nov 18 13:43:04 GMT+08:00 2011
	private static final long SYSTEM_TIME_REFEREE = 1321594984000L;

	/**
	 * After successfully read TV events( call method syncRead or asyncRead in
	 * class TVSchedEventMonitor), it strongly recommended to call this method
	 * to delete the scheduled TV events which are out of data.
	 * 
	 * @param curTime
	 *            the current time in milliseconds since January 1, 1970
	 *            00:00:00 UTC.
	 */
	public void removeTimeupScheduleTVEvents(long curTime) {
		// The system time is wrong when current time is less than
		// SYSTEM_TIME_REFEREE. This will occur when system can not get the
		// correct time from DTV TS.
		if (curTime < SYSTEM_TIME_REFEREE) {
			return;
		}
		st.deleteTimeupTVEventRecords(curTime);
	}

	private TVChannel findChannelbysvlRecId(int id) {
		if (schedMonitor.channels != null) {
			for (TVChannel ch : schedMonitor.channels) {
				if (id == ch.getRawInfo().getSvlRecId()) {
					return ch;
				}
			}
		}
		return null;
	}

	private TVEvent findTVEventbyStartTime(long start, TVEvent[] events) {
		if (events != null) {
			for (TVEvent event : events) {
				if (event.getStartTime() == start) {
					return event;
				}
			}
		}
		return null;
	}

	/**
	 * delete all scheduled TV events
	 */
	public void clearAllScheduleTVEvent() {
		st.deleteAllTVEventRecords();
		scheduleMap.clear();
		stopTVEventSchedule();
	}
}
