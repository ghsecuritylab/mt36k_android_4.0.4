package com.mediatekk.tvcm;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.DtListener;
import com.mediatek.tv.model.DtType;
import com.mediatek.tv.service.BroadcastService;

import android.content.Context;

/**
 * Lock order: TVTimer->TVTimerManager, so do not access TVTimer method from TVTimerManager
 * 's object lock.
 * 
 * @author mtk40063
 *
 */
public class TVTimerManager extends TVComponent {

	public TVTimerManager(Context context) {
		super(context);
	}

	class TimerNfy implements Runnable {
		public void run() {
			while(true) {
				try {
					// There is a simple way that we wait for the GCD of
					// timer value. 
					// A nice way to sleep is to wait for the first item 
					// in the timer sorted list/queue. That requires a priority
					// queue heap.
					// To keep it simple, TV timer just use the 1 second tick.
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					return;
				}
				final Vector<TVTimer> timersSpot;
				final Vector<TVTimerListener> listenerSpot;
				synchronized(TVTimerManager.this) {
					timersSpot = (Vector<TVTimer>) startedTimers.clone();
					listenerSpot = (Vector<TVTimerListener>) listeners.clone();
				}
				
				Enumeration<TVTimer> e = timersSpot.elements();
				while(e.hasMoreElements()) {
					TVTimer t = e.nextElement();					
					if(t.getTimeLeft() <= 0) {
						t.work();
					}
				}
				//Notify listeners
				getHandler().post(new Runnable() {
					public void run() {
						Enumeration<TVTimerListener> e = listenerSpot.elements();
						while(e.hasMoreElements()) {
							TVTimerListener l = e.nextElement();		
							if(l != null) {
								l.onTimeTick((TVTimer[]) timersSpot.toArray(new TVTimer[timersSpot.size()]));
							}
						}
					}});
			}
		}
		
	}
	
	void init() {
		String data = "tuner_cab_dig_0";
		try {
			BroadcastService rawSrv = (BroadcastService) getTVMngr().getService(BroadcastService.BrdcstServiceName);
			int ret = rawSrv.dtSetSyncSrc(DtType.DT_SYNC_WITH_DVB_TDT, DtType.DT_SRC_TYPE_MPEG_2_BRDCST, data);
//			rawSrv.dtRegNfyFct(new DtListener() {
//				public void DtNfyFct(int arg0, int arg1, int arg2) {
//					// TODO Auto-generated method stub
//					
//				}});
			TvInfo(" Sync broadcast time " + ret);
		} catch (TVMException e) {
		    e.printStackTrace();
		}
		
		new Thread(new TimerNfy()).start();
	}

	public static interface TVTimerListener {
		public void onTimeTick(TVTimer[] timers);
	}

	protected Hashtable<String, TVTimer> timers = new Hashtable<String, TVTimer>();
	Vector<TVTimer> startedTimers = new Vector<TVTimer>(4);
	public synchronized PowerOffTimer getPowerOffTimer(String name) {
		// Check name conflict?
		PowerOffTimer timer = (PowerOffTimer) timers.get(name);
		if (timer == null) {
			timer = new PowerOffTimer(name, this);
			timers.put(name, timer);
		}
		return timer;
	}
	
	synchronized void  addStartedTimers(TVTimer timer) {
		startedTimers.add(timer);
	}
	synchronized void  removeStartedTimers(TVTimer timer) {
		startedTimers.remove(timer);
	}
	
	Vector<TVTimerListener> listeners = new Vector<TVTimerListener>();
	public synchronized void addTimeListener(TVTimerListener lis) {
		listeners.add(lis);
	}
	public synchronized void removeTimeListener(TVTimerListener lis) {
		listeners.remove(lis);
	}
	
	
	
	public static class PowerOffTimer extends TVTimer {
		PowerOffTimer(String name, TVTimerManager tm) {
			super(name, tm);
		}
		void work() {
			tm.getContent().sendPowerOff();
			cancel();
		}
	}
	
	
	
	/**
	 * TODO: TV manager should support this from PCL.
	 * 
	 * @param date
	 */
	public void setPowerOn(Date date) {
		//
	}

	protected final static String POWERON_CHANNEL = "poweron_channel";

	public short getPowerOnChannel() {
		TVStorage st = getContent().getStorage();
		String chNum = st.get(POWERON_CHANNEL);
		if(chNum != null) {
			return new Short(chNum).shortValue();
		}else {
			return 0;
			//return st.get(TVChannelManager.)
		}
	}

	public void setPowerOnChannel(Short chNum) {
		TVStorage st = getContent().getStorage();
		st.set(POWERON_CHANNEL, new Short(chNum).toString());
	}
	public void setPowerOnTimer(Date time) {
		TVOptionRange<Integer> pt = 
			(TVOptionRange<Integer>) getContent().getConfigurer().getOption(ConfigType.CFG_POWER_ON_TIMER);
		if(pt != null) {
			pt.set((int) (time.getTime()/1000));
		}
	}
	

	
	public long getBroadcastUTC() {
		BroadcastService rawSrv = (BroadcastService) getTVMngr().getService(BroadcastService.BrdcstServiceName);
		//Sorry for this arugments,
		//TVManager told me that these are 'output'
		// arguments. I don't need to init them.
		int[] args = new int[2];

		try {
			//That is in second...			
			return rawSrv.dtGetBrdcstUtc(args) * 1000;
		} catch (TVMException e) {
			e.printStackTrace();
		}
		return System.currentTimeMillis();
	}
	
	public long getBroadcastTZOffset() {
		BroadcastService rawSrv = (BroadcastService) getTVMngr().getService(BroadcastService.BrdcstServiceName);
		try {
			//That is in second...			
			return rawSrv.dtGetTz() * 1000;
		} catch (TVMException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
