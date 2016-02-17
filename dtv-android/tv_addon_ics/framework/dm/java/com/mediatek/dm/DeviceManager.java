package com.mediatek.dm;

import android.os.IBinder;
import android.os.RemoteException;

import com.mediatek.dm.Device;
import com.mediatek.dm.IDMCallback;
import com.mediatek.dm.DeviceManagerEvent;
import com.mediatek.dm.MountPoint;
import com.mediatek.dm.IDMRemoteService;
import com.mediatek.dm.DMRemoteServiceHandler;
import com.mediatek.dm.IDMCallback;
import com.mediatek.dm.DeviceManagerListener;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

/**
 * This class provides the primary API for managing all aspects of DM services.
 */
public class DeviceManager {
	private static final String TAG = "DeviceManager";
	private ArrayList<DeviceManagerListener> mListeners;
	private static DeviceManager dm;

	private static IDMRemoteService remoteService = null;
	private DMClientHandler handler;

	public static DeviceManager getInstance() {

		synchronized (DeviceManager.class) {
			if (dm == null) {
				Log.d(TAG, "Create DeviceManager");
				dm = new DeviceManager();
			}

		}
		Log.d(TAG, "got an DeviceManager instance " + dm);

		return dm;
	}

	private DeviceManager() {

        mListeners = new ArrayList<DeviceManagerListener>();
        IBinder b  = (IBinder)android.os.ServiceManager.getService(DMRemoteServiceHandler.SERVICE_NAME);
        remoteService = IDMRemoteService.Stub.asInterface(b);
        handler = new DMClientHandler(this);
		try {
			remoteService.registerDMCallback(IDMCallback.Stub.asInterface(handler));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void finalize() {
		try {
			remoteService.unregisterDMCallback(IDMCallback.Stub.asInterface(handler));
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    try {
			super.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
	}

	public static void releaseInstance() {
		//remoteService.releaseInstance();
	}

	public void addListener(DeviceManagerListener listener) {
		mListeners.add(listener);
	}

	/**
	 * Unregisters an IMountServiceListener
	 */
	public void removeListener(DeviceManagerListener listener) {
		mListeners.remove(listener);
	}

	protected void onDeviceManagerListener(DeviceManagerEvent event) {
		Log.i(TAG, "Notify step 5[end].\n");
		Iterator<DeviceManagerListener> it = mListeners.iterator();
		while (it.hasNext()) {
			DeviceManagerListener listener = it.next();
			Log.i(TAG, "Notify begin to end.\n");
			listener.onEvent(event);
		}
	}

	/**
	 * Get device count
	 */
	public int getDeviceCount() {
		int ret = 0;
		try {
			ret = remoteService.getDeviceCount();
		} catch (RemoteException ex) {
		}
		return ret;
	}

	/**
	 * Get ArrayList of mount point specified by dev
	 */
	public ArrayList<MountPoint> getDeviceContent(Device dev) {
		try {
			return (ArrayList<MountPoint>) remoteService.getDeviceContent(dev);
		} catch (RemoteException ex) {
		}
		return null;
	}

	/**
	 * Get ArrayList for device
	 */
	public ArrayList<Device> getDeviceList() {
		try {
			return (ArrayList<Device>) remoteService.getDeviceList();
		} catch (RemoteException ex) {
		}
		return null;
	}

	/**
	 * Get mount point count
	 */
	public int getMountPointCount() {
		int ret = 0;
		try {
			ret = remoteService.getMountPointCount();
		} catch (RemoteException ex) {
		}
		return ret;
	}

	/**
	 * Get Arraylist of mount point
	 */
	public ArrayList<MountPoint> getMountPointList() {
		try {
			return (ArrayList<MountPoint>) remoteService.getMountPointList();
		} catch (RemoteException ex) {
		}
		return null;
	}

	/**
	 * Get MntPoint Instance specified by path
	 */
	public MountPoint getMountPoint(String path) {
		try {
			return remoteService.getMountPoint(path);
		} catch (RemoteException ex) {
		}
		return null;
	}

	/**
	 * Get the Device Instance which the mntpoint belong to
	 */
	public Device getParentDevice(MountPoint mntpoint) {
		try {
			return remoteService.getParentDevice(mntpoint);
		} catch (RemoteException ex) {
		}
		return null;
	}

	/**
	 * unmount device by name, it will umount all partition which is belonged to
	 * the device
	 */
	public void umountDevice(String devName) {
		try {
			remoteService.umountDevice(devName);
		} catch (RemoteException ex) {
		}
		return;
	}

	/**
	 * mount ISO file
	 */
	public void mountISO(String isoFilePath) {

		try {
			remoteService.mountISO(isoFilePath);
		} catch (RemoteException ex) {
		}
		return;
	}

	/**
	 * mount iso file at current directory with a specified a label
	 */
    public void mountISOex(String isoFilePath, String isoLabel)
    {
    	try {
			remoteService.mountISOex(isoFilePath, isoLabel);
		} catch (RemoteException e) {
		}
    }
	/**
	 * umount ISO file
	 */
	public void umountISO(String isoMountPath) {
		try {
			remoteService.umountISO(isoMountPath);
		} catch (RemoteException ex) {
		}
		return;
	}

	/**
	 * Judge the mountpath is or not the isomountpath
	 */
	public boolean isVirtualDevice(String isoMountPath) {
		try {
			return remoteService.isVirtualDevice(isoMountPath);
		} catch (RemoteException ex) {

		}
		return false;
	}

	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return null;
	}


	public void notifyDeviceEvent(DeviceManagerEvent event)
			throws RemoteException {
		Log.i(TAG, "Notify step 4.\n");
		// TODO Auto-generated method stub
		this.onDeviceManagerListener(event);
	}

}
