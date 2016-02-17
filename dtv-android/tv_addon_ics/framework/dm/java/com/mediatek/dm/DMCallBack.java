package com.mediatek.dm;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import com.mediatek.dm.IDMCallback;

import android.os.RemoteCallbackList;
import android.os.RemoteException;


public class DMCallBack {
	private static final String TAG = "[J]DMCallBack";
	private static final RemoteCallbackList<IDMCallback> clientCallbacks = new RemoteCallbackList<IDMCallback>();
	private boolean callBackDebug = true;
	private List<String> callBacks = new ArrayList<String>();

	public DMCallBack() {
		super();
	}

	public void destroyCallBack() {
		Log.d(TAG, "destroyCallBack:");
		if (callBackDebug) {
			for (int i = 0; i < callBacks.size(); i++) {
				Log.d(TAG, callBacks.get(i).toString());
			}
		}
		clientCallbacks.kill();
	}

	public void registerCallback(IDMCallback cb) {
		Log.d(TAG, "registerCallback:" + cb);
		if (cb != null) {
			clientCallbacks.register(cb);
			if (callBackDebug) {
				callBacks.add(cb.toString());
			}
		}
	}

	public void unregisterCallback(IDMCallback cb) {
		Log.d(TAG, "unregisterCallback:" + cb);
		if (cb != null) {
			clientCallbacks.unregister(cb);
			if (callBackDebug) {
				callBacks.remove(cb.toString());
			}
		}
	}

	protected void notifyDeviceEvent(DeviceManagerEvent event) {
		Log.i(TAG, "Notify step 3. notifyDeviceEvent=" + event);

		synchronized (DMCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					clientCallbacks.getBroadcastItem(i).notifyDeviceEvent(event);
				} catch (Exception e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return;
	}

}
