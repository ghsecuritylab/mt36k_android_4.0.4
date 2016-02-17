package com.mediatek.dm;

import java.util.List;
//import com.mediatek.dm.DeviceManagerListener;
import com.mediatek.dm.DeviceManagerEvent;
import com.mediatek.dm.MountPoint;
import com.mediatek.dm.Device;
import com.mediatek.dm.IDMCallback;

interface IDMRemoteService
{    
    /**
     * Registers an IDMRemoteServiceListener for receiving async
     * notifications.
     */
    void registerDMCallback(in IDMCallback listener);
    
    
     /**
     * Unregisters an IMountServiceListener
     */
    void unregisterDMCallback(in IDMCallback listener);
    
	/**
	*Get device count
	*/
    int getDeviceCount();

	/**
	*Get ArrayList of mount point specified by dev
	*/
    List<MountPoint> getDeviceContent(in Device dev); 

	/**
	*Get ArrayList for device
	*/
    List<Device> getDeviceList();
	
	/**
	*Get mount point count
	*/
    int getMountPointCount() ;

	/**
	*Get Arraylist of mount point
	*/
    List<MountPoint> getMountPointList();
   
	/**
	*Get MntPoint Instance specified by path
	*/
    MountPoint getMountPoint(String path);

	/**
	*Get the Device Instance which the mntpoint belong to
	*/
    Device getParentDevice(in MountPoint mntpoint);

	/**
	*unmount device by name, it will umount all partition which is belonged to 
    *the device
    */
	void umountDevice(String devName);

	/**
	*mount ISO file
	*/
	void mountISO(String isoFilePath);

    /*mount iso file at current directory with a specified a label*/
    void mountISOex(String isoFilePath, String isoLabel);
    
	/**
	*umount ISO file
	*/
	void umountISO(String isoMountPath);
	
	/**
	*Judge the mountpath is or not the isomountpath
	*/
    boolean isVirtualDevice(String isoMountPath);

}
