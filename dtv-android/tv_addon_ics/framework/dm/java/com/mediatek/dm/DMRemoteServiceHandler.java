package com.mediatek.dm;

import java.util.ArrayList;
import com.mediatek.dm.DMServer;
import com.mediatek.dm.Device;
import com.mediatek.dm.MountPoint;
import com.mediatek.dm.IDMCallback;
import com.mediatek.dm.DMCallBack;

import android.util.Log;

import android.content.Context;
import android.os.RemoteException;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class DMRemoteServiceHandler extends IDMRemoteService.Stub {
	public static final String SERVICE_NAME = "DMRemoteService";
	private static final String TAG = "[J]DMRemoteServiceHandler";
    private DMServer dm;
    private Context context; 
    
    private DMCallBack dmCallback ;

	public DMRemoteServiceHandler(Context context) {
		super();
		this.context = context;
		dmCallback = new DMCallBack();
		dm = new DMServer(this);
		//this.dmCallback = dmCallback;		
	}

	/**
     * Registers an IDMRemoteServiceListener for receiving async
     * notifications.
     */
	public void registerDMCallback(IDMCallback cb) throws RemoteException {
		Log.d(TAG, "Enter  registerCallback ");
		Log.d(TAG, "cb = " + cb);
		//if (dmCallback != null) {
			dmCallback.registerCallback(cb);
		//}
		Log.d(TAG, "Leave  registerCallback ");
	}

	
     /**
     * Unregisters an IMountServiceListener
     */
	public void unregisterDMCallback(IDMCallback cb)
			throws RemoteException {
		Log.d(TAG, "Enter  registerCallback ");
		Log.d(TAG, "cb = " + cb);
		//if (dmCallback != null) {
			dmCallback.unregisterCallback(cb);
		//}
		Log.d(TAG, "Leave  registerCallback ");
	}
	/**
	*Get device count
	*/
    public int getDeviceCount()throws RemoteException
    {
    	return dm.getDeviceCount();
    }

	/**
	*Get ArrayList of mount point specified by dev
	*/
    public ArrayList<MountPoint> getDeviceContent(Device dev)throws RemoteException
    {
    	return dm.getDeviceContent(dev);
    }

	/**
	*Get ArrayList for device
	*/
    public ArrayList<Device> getDeviceList()throws RemoteException
    {
    	return dm.getDeviceList();
    }
	
	/**
	*Get mount point count
	*/
    public int getMountPointCount()throws RemoteException
    {
    	return dm.getMountPointCount();
    }
	/**
	*Get Arraylist of mount point
	*/
    public ArrayList<MountPoint> getMountPointList()throws RemoteException
    {
    	return dm.getMountPointList();
    }
   
	/**
	*Get MntPoint Instance specified by path
	*/
    public MountPoint getMountPoint(String path)throws RemoteException
    {
    	return dm.getMountPoint(path);
    }

	/**
	*Get the Device Instance which the mntpoint belong to
	*/
    public Device getParentDevice(MountPoint mntpoint)throws RemoteException
    {
    	return dm.getParentDevice(mntpoint);
    }

	/**
	*unmount device by name, it will umount all partition which is belonged to 
    *the device
    */
	public void umountDevice(String devName)throws RemoteException
	{
		dm.umountDevice(devName);
		return;
	}

	/**
	*mount ISO file
	*/
	public void mountISO(String isoFilePath)throws RemoteException
	{
		dm.mountISO(isoFilePath);
		return;
	}
	
	/*mount iso file at current directory with a specified a label*/
	public void mountISOex(String isoFilePath, String isoLabel)throws RemoteException {
        dm.mountISOex(isoFilePath, isoLabel);
        return;
    }
	/**
	*umount ISO file
	*/
	public void umountISO(String isoMountPath)throws RemoteException
	{
		dm.umountISO(isoMountPath);
		return;
	}
	
	/**
	*Judge the mountpath is or not the isomountpath
	*/
    public boolean isVirtualDevice(String isoMountPath)throws RemoteException
    {
    	return dm.isVirtualDevice(isoMountPath);
    }
    
    public void notifyListener (DeviceManagerEvent event) {
    	Log.i(TAG, "Notify step 2.\n");
    	dmCallback.notifyDeviceEvent(event);    	
    }


}
