package com.mediatek.dm;

import java.util.ArrayList;

import android.util.Log;

import android.os.RemoteException;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class DMClientHandler extends IDMCallback.Stub {
	private static final String TAG = "[J]DMClientHandler";
	private DeviceManager manager;

	public DMClientHandler(DeviceManager manager) {
		super();
		this.manager = manager;
	}

	public void notifyDeviceEvent(DeviceManagerEvent event) throws RemoteException {
		// TODO Auto-generated method stub
		Log.i(TAG, "notifyDeviceEvent[2]");
		// 
		manager.notifyDeviceEvent(event);
	}

	
}
