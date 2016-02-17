/**
 * 
 */
package com.mediatek.tvcm;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A base class of timer implementation.
 * @author mtk40063
 *
 */
public abstract class TVTimer {
	TVTimerManager tm = null;
	//private long time;
	private long delay;
	Date date;
	boolean started = false;
	final String name;
	int type;

	public static final int TYPE_FIXED = 0;
	public static final int TYPE_DELAY = 1;

	TVTimer(String name, TVTimerManager tm) {
		this.name = name;
		this.tm = tm;
	}
	Timer sleepTimer = null;

	public  String getName() {
		return name;
	}

	public synchronized int getType() {
		return type;
	}

	public synchronized void setTimer(Date date) {
		type = TYPE_FIXED;
		this.date = (Date) date.clone();
	}

//	public synchronized void setTimer(String dateString) {
//		type = TYPE_FIXED;
//		DateFormat df = DateFormat.getDateInstance();
//		try {
//			date = df.parse(dateString);
//			System.out.println("The date is  "  + date);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	public synchronized void setTimer(long delay) {
		// time = delay + System.currentTimeMillis();
		type = TYPE_DELAY;		
		this.delay = delay;
		if(started) {
			date = new Date(System.currentTimeMillis() + delay);
		}
	}

	public synchronized long getTimeLeft() {
		if (started && null != date) {
			return date.getTime() - System.currentTimeMillis();
		}
		return 0;
	}

	public synchronized boolean isStarted() {
		return started;
	}
	

	
//	void doCancel() {
//		if (started) {
//			started = false;
//			tm.removeStartedTimers(this);
//			sleepTimer.cancel();
//			sleepTimer = null;				
//		}
//	}

	abstract void work();

////	void doStart() {
////		// Now, after started, delay and time will be
////		if (type == TYPE_FIXED) {
////			delay = time - System.currentTimeMillis();
////		} else if (type == TYPE_DELAY) {
////			time = delay + System.currentTimeMillis();
////		} else {
////			return;
////		}
////		started = true;
////		sleepTimer = new Timer();
////		tm.addStartedTimers(this);
////		if (delay <= 0) {
////			// Do it now!
////			work();
////			started = false;
////			tm.removeStartedTimers(this);
////		} else {
////			sleepTimer.schedule(new TimerTask() {
////				public void run() {
////					work();
////					started = false;
////					tm.removeStartedTimers(TVTimer.this);
////				}
////			}, delay);
////		}
//	}

	/**
	 * Cancel the timer.
	 */
	public synchronized void cancel() {
		started = false;
		tm.removeStartedTimers(this);
//		tm.getHandler().post(new Runnable() {
//			public void run() {
//				doCancel();
//			}
//		});
	}

	/**
	 * If timer has already started, this will reset the timer
	 */
	public synchronized void start() {
		if(type == TYPE_DELAY) {
			date = new Date(System.currentTimeMillis() + delay);
		}
		if(started) {
			return;
		}
		started = true;
		tm.addStartedTimers(this);		
//		tm.getHandler().post(new Runnable() {
//			public void run() {
//				doCancel();
//				doStart();
//			}
//		});
	}

}