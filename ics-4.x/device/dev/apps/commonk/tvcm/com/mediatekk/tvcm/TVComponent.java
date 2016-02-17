package com.mediatekk.tvcm;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import com.mediatek.tv.TVManager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * TV component base class.
 * This class provides common implementation for Android interface and TVManager.
 * @author mtk40063
 *
 */
public abstract class TVComponent{

	public final static boolean dummyMode = false;

	private Hashtable<String, TVOption<?>> optionTable = new Hashtable<String, TVOption<?>>();
	private Handler handler;
	protected Context context;

	public TVComponent(Context context) {
		this.context = context.getApplicationContext();
		handler = new Handler(this.context.getMainLooper(), new Handler.Callback() {
			public boolean handleMessage(Message msg) {
				TVComponent.this.handleMessage(msg.what, msg.obj);
				return true;
			}
		});		
	}
	
	protected void handleMessage(int msgCode, Object obj) {
		
	}
	
	public void sendMessageOnce(int msgCode, Object obj) {
		if(handler.hasMessages(msgCode)) {
			return;
		}
		handler.sendMessage(handler.obtainMessage(msgCode, obj));
	}
 	
	public void sendMessageOnceDelayed(int msgCode, Object obj, long delayMillis) {
		if (handler.hasMessages(msgCode)) {
			return;
		}
		handler.sendMessageDelayed(handler.obtainMessage(msgCode, obj),
				delayMillis);
	}

	public void sendMessage(int msgCode, Object obj) {
		handler.sendMessage(handler.obtainMessage(msgCode, obj));
	}
 	
	public void sendMessageDelayed(int msgCode, Object obj, long delayMillis) {
		handler.sendMessageDelayed(handler.obtainMessage(msgCode, obj),
				delayMillis);
	}

	protected Handler getHandler() {
		return handler;
	}

	protected TVContent getContent() {
		return TVContent.getInstance(context);
	}
	
	protected TVManager getTVMngr() {
		if (dummyMode) {
			return null;
		} else {
			return TVManager.getInstance(context);
		}
	}

	/**
	 * Get TVOption for specified name
	 * @param name
	 * @return
	 */
	public final synchronized TVOption<?> getOption(String name) {
		return optionTable.get(name);		
	}

	/**
	 * Add new option to option table. 
	 * If the name has already added, this will fail
	 * @param name
	 * @param option
	 */
	public final synchronized void addOption(String name, TVOption<?> option) {
		if (optionTable.containsKey(name)) {
			// Throw exception here
			return;
		}
		optionTable.put(name, option);
	}
	
	/**
	 * Replace option to a new option for specified name. 
	 * Old option will be returned.
	 * 
	 * @param name
	 * @param option
	 * @return
	 */
	public final synchronized TVOption<?> replaceOption(String name, TVOption<?> option) {
		TVOption<?> old = optionTable.get(name); 
		optionTable.put(name, option);			
		return old;
	}

	/**
	 * Get all options iterator.
	 * @return
	 */
	public synchronized Iterator<String> getAllOptions() {
		String log = new String();
		Set<String> names = optionTable.keySet();
		Iterator<String> itr = names.iterator();
		return itr;
	}
	
	private static final boolean isLog = true;

	/**
	 * SHow log
	 * @param s
	 */
	public static void TvLog(String s) {
		if (!isLog) {
			return;
		}
		StackTraceElement[] el = new Exception().getStackTrace();
		android.util.Log.i("TVLog",
				"Class:" + el[1].getClassName() + 
				"." + el[1].getMethodName() +
				" (Line:" + new Integer(el[1].getLineNumber()).toString() + ") :<" + s);
		
	}
	
	/**
	 * SHow log
	 * @param s
	 */
	public static void TvInfo(String s) {		
		if (!isLog) {
			return;
		}
		android.util.Log.i("TVInfo", s);
		
	}
	
	/**
	 * Dump all options 
	 */
	public final void dumpOptions() {		
		String log = new String();
		Set<String> names = optionTable.keySet();
		Iterator<String> itr = names.iterator();
		log = "OPTION TABLE of Class:"+ this.getClass().getName() + ", Object:" + this + " {\n";
		while(itr.hasNext()) {
			String name = (String) itr.next();
			TVOption<?> opt = getOption(name);
			log += "\t Option:<" + name + ">: ";
			log += opt;
			log += "\n";
		}
		log += "}";
		TvInfo(log);
	}

	protected void onSaveInstanceState(Bundle outState) {
	}

	protected void onCreate(Bundle savedInstanceState) {
	
	}
	
	protected void onStart() {

	}

	protected void onStop() {

	}

	// Need more? Would TVContent be killed?
}
