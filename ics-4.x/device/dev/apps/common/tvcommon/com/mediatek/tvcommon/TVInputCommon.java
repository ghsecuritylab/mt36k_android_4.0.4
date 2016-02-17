package com.mediatek.tvcommon;

import java.util.Enumeration;
import java.util.Vector;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.service.InputService;
import com.mediatek.tv.service.InputService.InputServiceListener;
import com.mediatek.tv.service.InputService.InputServiceListener.InputSignalStatus;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class TVInputCommon {

	private TVManager tvMngr = null;
	private InputService inpSrv = null;
	private Context mContext = null;
	private Handler mHandler = null;
	private TVOutputCommon mTVOutputCommon = null;
	
	private TVInputCommon(Context context) {
		mContext = context.getApplicationContext();
		tvMngr = TVManager.getInstance(mContext);
		inpSrv = (InputService) tvMngr
				.getService(InputService.InputServiceName);
		mHandler = new Handler(mContext.getMainLooper(),
				new Handler.Callback() {
					@Override
					public boolean handleMessage(Message msg) {
						// TODO Auto-generated method stub
						TVInputCommon.this.handleMessage(msg);
						return true;
					}
				});
		
		mTVOutputCommon = TVOutputCommon.getInstance(mContext);
		
		if (inpSrv != null) {
			inpSrv.setInputListener(new InputListenerDelegate());
		} else {
			TVCommonNative.TVLog("TVInputCommon: get InputService failed!!!");
		}
		
	}

	protected void handleMessage(Message msg) {
		// TODO Auto-generated method stub

	}

	private static TVInputCommon tvInputCommon = null;

	public static TVInputCommon getInstance(Context context) {
		if (tvInputCommon == null) {
			tvInputCommon = new TVInputCommon(context);
		}

		return tvInputCommon;
	}

	protected Vector<TVInputSourceListener> listeners = new Vector<TVInputSourceListener>();
	protected Vector<InputSourceEventListener> eventListeners = new Vector<InputSourceEventListener>();

	public static interface TVInputSourceListener {

		public void onOutputSignalChange(String output, boolean hasSignal);

		public void onInputSignalChange(String input, boolean hasSignal);

		public void onInputGotSignal(String input);
	}

	public static interface InputSourceEventListener {
		final static int STATE_UNKNOW = 0;
		final static int InputEventUnknown = 1;
		final static int InputEventNoSignal = 2;
		final static int InputEventWithSignal = 3;
		final static int InputEventVideoUpdate = 4;

		public void onOutputEventChange(String output, int eventType);
	}

	public synchronized void registerSourceListener(TVInputSourceListener listener) {
		listeners.add(listener);
	}

	public synchronized void removeSourceListener(TVInputSourceListener listener) {
		listeners.remove(listener);
	}

	public synchronized void registerSourceEventListener(
			InputSourceEventListener listener) {
		eventListeners.add(listener);
	}

	public synchronized void removeSourceEventListener(
			InputSourceEventListener listener) {
		eventListeners.remove(listener);
	}

	protected void notifyInputSignal(final String inputName,
			final boolean signal) {
		mHandler.post(new Runnable() {
			public void run() {
				Enumeration<TVInputSourceListener> e = listeners.elements();
				TVInputSourceListener item;
				while (e.hasMoreElements()) {
					item = e.nextElement();
					item.onInputSignalChange(inputName, signal);
				}
			}
		});
	}

	protected void notifyOutputSignalChange(final String outputName,
			final boolean hasSignal) {
		mHandler.post(new Runnable() {
			public void run() {
				mTVOutputCommon.signal = hasSignal;
				Enumeration<TVInputSourceListener> e = listeners.elements();
				TVInputSourceListener item;
				while (e.hasMoreElements()) {
					item = e.nextElement();
					item.onOutputSignalChange(outputName, hasSignal);
				}
			}
		});
	}

	protected void notifyOutputEventChange(final String outputName,
			final int eventType) {
		mHandler.post(new Runnable() {
			public void run() {
				Enumeration<InputSourceEventListener> e = eventListeners
						.elements();
				InputSourceEventListener item;
				while (e.hasMoreElements()) {
					item = e.nextElement();
					item.onOutputEventChange(outputName, eventType);
				}
			}
		});
	}

	class InputListenerDelegate implements InputServiceListener {

		public void notifyInputGotSignal(String arg0) {
			// notifyInputSignal(arg0, true);
		}

		public void notifyOutputOperatorDone(String arg0) {

		}

		public void notifyOutputSignalSatus(String arg0,
				InputListenerSignalSatus arg1) {
		}

		public void notifyInputSignalStatus(String arg0, InputSignalStatus arg1) {
			// TODO Auto-generated method stub
			if (arg1 == InputSignalStatus.SignalStatusLocked) {
				notifyInputSignal(arg0, true);
			} else {
				notifyInputSignal(arg0, false);
			}

		}

		public void notifyOutputSignalStatus(String arg0, InputSignalStatus arg1) {
			// TODO Auto-generated method stub
			if (arg1 == InputSignalStatus.SignalStatusLocked) {
				notifyOutputSignalChange(arg0, true);
			} else {
				notifyOutputSignalChange(arg0, false);
			}

		}

		public void notifyOutputEvent(String arg0, InputListenerEventNotify arg1) {
			// TODO Auto-generated method stub
			if (arg1 == InputListenerEventNotify.InputEventUnknown) {
				notifyOutputEventChange(arg0,
						InputSourceEventListener.InputEventUnknown);
			} else if (arg1 == InputListenerEventNotify.InputEventNoSignal) {
				notifyOutputEventChange(arg0,
						InputSourceEventListener.InputEventNoSignal);
			} else if (arg1 == InputListenerEventNotify.InputEventWithSignal) {
				notifyOutputEventChange(arg0,
						InputSourceEventListener.InputEventWithSignal);
			} else if (arg1 == InputListenerEventNotify.InputEventVideoUpdate) {
				notifyOutputEventChange(arg0,
						InputSourceEventListener.InputEventVideoUpdate);
			}
		}

	}

	public void stopDesignateOutput(String outPutName, boolean needSync) {
		try {
			if (inpSrv != null) {
				inpSrv.stopDesignateOutput(outPutName, needSync);
			}
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static final int OUTPUT_MODE_PIP = 1;
	public static final int OUTPUT_MODE_POP = 2;
	public static final int OUTPUT_MODE_NORMAL = 0;
	
	public int getOuputMode() {
		InputService.InputState rawState = inpSrv.getCurrentState();
		switch (rawState) {
		case INPUT_STATE_PIP:
			return OUTPUT_MODE_PIP;
		case INPUT_STATE_POP:
			return OUTPUT_MODE_POP;
		case INPUT_STATE_NORMAL:
			return OUTPUT_MODE_NORMAL;
		default:
			return OUTPUT_MODE_NORMAL;
		}
	}
	

}
