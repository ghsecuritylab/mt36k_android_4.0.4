package com.mediatek.dm;

import java.util.ArrayList;
import java.util.Iterator;
import android.util.Log;
import android.net.wifi.WifiNative;

import com.mediatek.dm.Device;
import com.mediatek.dm.MountPoint;
import com.mediatek.dm.DeviceManagerEvent;
//import com.mediatek.dm.DMCallBack;
import com.mediatek.dm.DMNativeDaemonConnector;


public class DMServer implements IDMNativeDaemonConnectorCallbacks
{
    private static String TAG = "DMServer";
    private DMNativeDaemonConnector mDMConnector;
    private ArrayList<MountPoint> mMountPoints;
    private ArrayList<Device> mDevices;
    private DMRemoteServiceHandler handler;
    private static final boolean mDebug = false;

    public DMServer(DMRemoteServiceHandler handler)
    {
        mMountPoints = new ArrayList<MountPoint>();
        mDevices = new ArrayList<Device>();
        this.handler = handler;
        
        mDMConnector = new DMNativeDaemonConnector(this, "DMvold", 10, "DMConnector");
        Thread thread = new Thread(mDMConnector, DMNativeDaemonConnector.class.getName());
        thread.start();
        nativeStart();        
    }
    
    public void releaseInstance()
    {
    	nativeEnd();          
    }

    public void onDaemonConnected()
    {
        //Todo nothing.
    }
    public boolean onEvent(int code, byte[] raw, String[] cooked)
    {
    	Log.v(TAG, "ONEVENT\n");
        switch(code)
        {
        case DeviceManagerEvent.mounted:
           try{ 
                if(Integer.parseInt(cooked[10]) == 1) {
                    int start = cooked[0].length() + cooked[1].length() + cooked[2].length() + cooked[3].length()
                               + cooked[4].length() + cooked[5].length() + cooked[6].length() + cooked[7].length()
                               + cooked[8].length() + 18;
                    raw[start + cooked[9].length()] = '\0';
                    String value = new String(raw, start, cooked[9].length() + 1, "cp936");                   
                    byte[] byteArray = value.getBytes("utf8");                    
                    String vLabel = new String(byteArray);
                    
                    mMountPoints.add(new MountPoint(Integer.parseInt(cooked[2]), Integer.parseInt(cooked[3]),
                                                     Integer.parseInt(cooked[4]), Integer.parseInt(cooked[5]),
                                                     Integer.parseInt(cooked[6]), cooked[7], cooked[8], vLabel,
                                                     Integer.parseInt(cooked[10])));
                    if(mDebug)printMountPoints();
                    eventNotification(new DeviceManagerEvent(code, cooked[7]));
                }else {
                    mMountPoints.add(new MountPoint(Integer.parseInt(cooked[2]), Integer.parseInt(cooked[3]),
                                                     Integer.parseInt(cooked[4]), Integer.parseInt(cooked[5]),
                                                     Integer.parseInt(cooked[6]), cooked[7], cooked[8], cooked[9],
                                                     Integer.parseInt(cooked[10])));
                    if(mDebug)printMountPoints();
                    eventNotification(new DeviceManagerEvent(code, cooked[7]));
                }
            }
            catch(Exception e) {
                Log.v(TAG, "parameter is invalid\n");
            }
            break;
        case DeviceManagerEvent.umounted:
            try{
                doUmountedEvent(cooked[2]);
                eventNotification(new DeviceManagerEvent(code, cooked[2]));
            }catch(Exception e) {
                Log.v(TAG, "parameter is invalid\n");
            }
            break;
        case DeviceManagerEvent.connected:
            try{
                mDevices.add(new Device(Integer.parseInt(cooked[2]), Integer.parseInt(cooked[3]),
                                         Integer.parseInt(cooked[3]), cooked[5]));
                if(mDebug)printDevices();
                eventNotification(new DeviceManagerEvent(code, ""));
            }catch(Exception e) {
                Log.v(TAG, "parameter is invalid\n");
            }
            break;
        case DeviceManagerEvent.disconnected:
            try{
                doRemoveEvent(cooked[2]);
                eventNotification(new DeviceManagerEvent(code, ""));
            }
            catch(Exception e) {
                Log.v(TAG, "parameter is invalid\n");
            }
            break;
        case DeviceManagerEvent.productconnected:
            try{
                Log.i(TAG, String.format("Product connected: %s\n", cooked[2]));
                 WifiNative.setDongleInside(cooked[2]);
                 Log.i(TAG, String.format("setDongleInside have finished"));
                //No need to broadcast
                eventNotification(new DeviceManagerEvent(code, cooked[2])); 
               
            }
            catch(Exception e) {
               	Log.v(TAG, "parameter is invalid\n");                
            }
            break;
        case DeviceManagerEvent.productdisconnected:
            try{
                Log.i(TAG, String.format("Product disconnected: %s\n", cooked[2]));
                //No need to broadcast
                  WifiNative.setDongleRemove(cooked[2]);
                  Log.i(TAG, String.format("setDongleRemove have finished"));
                eventNotification(new DeviceManagerEvent(code, cooked[2]));
              
            }
            catch(Exception e) {
                Log.v(TAG, "parameter is invalid\n");
            }
            break;
        case DeviceManagerEvent.wificonnected:
            try{
                Log.i(TAG, String.format("Wifi connected, Interface: %s\n", cooked[2]));
                eventNotification(new DeviceManagerEvent(code, cooked[2]));                    
            }
            catch(Exception e) {
                Log.v(TAG, "parameter is invalid\n");
            }
            break;
        case DeviceManagerEvent.wifidisconnected:
            try{
                Log.i(TAG, String.format("Wifi disconnected, Interface: %s\n", cooked[2]));
                eventNotification(new DeviceManagerEvent(code, cooked[2]));                   
            }
            catch(Exception e) {
                Log.v(TAG, "parameter is invalid\n");
            }
            break;
        case DeviceManagerEvent.isomountfailed:
	         try{
                Log.i(TAG, String.format("ISO mount failed, mntpath: %s\n", cooked[2]));
                eventNotification(new DeviceManagerEvent(code, cooked[2]));
	         }
             catch(Exception e) {
                Log.v(TAG, "parameter is invalid\n");	            
	         }
             break;
        case DeviceManagerEvent.unsupported:
             try{
                  eventNotification(new DeviceManagerEvent(code, ""));
              }
              catch(Exception e) {
                  Log.v(TAG, "parameter is invalid\n");
              }
             break;
        default:
             Log.v(TAG, "event type is not support\n");
             break;
        }
        return true;
    }
    
    public boolean isVirtualDevice(String isoMountPath)
    {
        Iterator<MountPoint> it = mMountPoints.iterator();
        while(it.hasNext())
        {
            MountPoint mntPoint = it.next();
            if(mntPoint.mMountPoint.equals(isoMountPath))
            {
                if(mntPoint.mVolumeLabel.equals("ISOVirtualDevice"))
                {
                  Log.v(TAG, "It is a Virtual Device. \n");
                  return true;
                }
                else
                {
                  Log.v(TAG, "It is not a Virtual Device. \n");
                  return false;
                }
            }
        }
        Log.v(TAG, "It is not valid isoMountPath, invalid argment. \n");
        return false;
    }

	/*Get device count*/
	public int getDeviceCount() 
	{
		return mDevices.size();
	}

	/*Get ArrayList of mount point specified by dev*/
	public ArrayList<MountPoint> getDeviceContent(Device dev) 
	{
		ArrayList<MountPoint> retMntList = null;  

        /*add the MntPoint instance whose name is match the device's name to return list */
        Iterator<MountPoint> it = mMountPoints.iterator();
        while(it.hasNext())
        {
            MountPoint mnt = it.next();
            if(dev.mDeviceName.length() <= mnt.mDeviceName.length())
            {
                String name = mnt.mDeviceName.substring(0, dev.mDeviceName.length());
                if(name.equals(dev.mDeviceName))
                {
                    if(retMntList == null)
                    {
                        retMntList = new ArrayList<MountPoint>(); 
                    }

                    retMntList.add(mnt);
                }
            }
        }
		return retMntList;
	}

	/*Get ArrayList for device*/
	public ArrayList<Device> getDeviceList() 
	{
		return mDevices;
	}

	/*Get mount point count*/
	public int getMountPointCount() 
	{
		return mMountPoints.size();
	}

	/*Get Arraylist of mount point*/
	public ArrayList<MountPoint> getMountPointList() 
	{
		return mMountPoints;
	}

	/*Get MntPoint Instance specified by path*/
	public MountPoint getMountPoint(String path) 
	{
        MountPoint retMntPoint = null;

        /*find the matched MntPoint instance and returne*/
		Iterator<MountPoint> it = mMountPoints.iterator();
        while(it.hasNext())
        {
            MountPoint mnt = it.next();
            if(path.equals(mnt.mMountPoint))
            {
                retMntPoint = mnt;
                break;
            }
        }
		return retMntPoint;
	}

	/*Get the Dev Instance which the mntpoint belong to*/
	public Device getParentDevice(MountPoint mntpoint) 
	{
        Device retDev = null;
		Iterator<Device> it = mDevices.iterator();
        while(it.hasNext())
        {
            Device dev = it.next();
            String name = mntpoint.mDeviceName.substring(0, dev.mDeviceName.length());
            if(name.equals(dev.mDeviceName))
            {
                retDev = dev;
                break;
            }
        }
		return retDev;
	}

	/*unmount device by name, it will umount all partition which is belonged to the device*/
	public void umountDevice(String devName)
	{
        nativeUmount(devName);
    }

    /*notify all listener with event*/
    public void eventNotification(DeviceManagerEvent event) 
    {
    	Log.i(TAG, "Notify begin!\n");
    	handler.notifyListener(event);
    }

	/*mount ISO file*/
	public void mountISO(String isoFilePath)
	{
	   nativeMountISO(isoFilePath);
	}

	 /*mount iso file at current directory with a specified a label*/
    public void mountISOex(String isoFilePath, String isoLabel)
    {
        nativeMountISOex(isoFilePath, isoLabel);
    }

	/*umount ISO file*/
	public void umountISO(String isoMountPath)
	{
	   nativeUmountISO(isoMountPath);
	}

    /*print all mount point information*/
    private void printMountPoints()
    {
        Iterator<MountPoint> it = mMountPoints.iterator();
        while(it.hasNext())
        {
            MountPoint mnt = it.next();
            Log.i(TAG, String.format("volume label: %s", mnt.mVolumeLabel));
            Log.i(TAG, String.format("dev name: %s", mnt.mDeviceName));
            Log.i(TAG, String.format("mount point: %s", mnt.mMountPoint));
            Log.i(TAG, String.format("total size: %d", mnt.mTotalSize));
            Log.i(TAG, String.format("free size: %d", mnt.mFreeSize));
            Log.i(TAG, String.format("major: %d", mnt.mMajor));
            Log.i(TAG, String.format("minor: %d", mnt.mMinor));
            Log.i(TAG, "mFsType: " + mnt.mFsType.toString());
        }
    }

    /*print all device information*/
    private void printDevices()
    {
        Iterator<Device> it = mDevices.iterator();
        while(it.hasNext())
        {
            Device dev = it.next();
            Log.i(TAG, String.format("dev name: %s", dev.mDeviceName));
            Log.i(TAG, String.format("status: %d", dev.mStatus));
            Log.i(TAG, String.format("major: %d", dev.mMajor));
            Log.i(TAG, String.format("major: %d", dev.mMinor));
        }
    }
    
    private void doUmountedEvent(String mountpoint)
    {
        Iterator<MountPoint> it = mMountPoints.iterator();
        while(it.hasNext())
        {
            MountPoint mntPoint = it.next();
            if(mntPoint.mMountPoint.equals(mountpoint))
            {
                Log.i(TAG, String.format("[DM]: Receive Event. mountpoint: %s", mountpoint));
                mMountPoints.remove(mntPoint);
                break;
            }
        }
        if(mDebug)printMountPoints();
    }
    
    private void doRemoveEvent(String deviceName)
    {
        Iterator<Device> it = mDevices.iterator();
        while(it.hasNext())
        {
            Device dev = it.next();
            if(deviceName.equals(dev.mDeviceName))
            {   
                Log.i(TAG, String.format("Device disconnected, devName: %s", deviceName));
                mDevices.remove(dev);
                break;
            }
        }
    }

    private void nativeUmount(String deviceName)
    {
        String cmd = String.format("DMvolume umount %s", deviceName);
        try {
            mDMConnector.doCommand(cmd);
        } catch (DMNativeDaemonConnectorException e) {
            }
    }
    
    /*start to listen the DMVold*/
    private void nativeStart()
    {
        String cmd = String.format("DMvolume start");
        try {
            mDMConnector.doCommand(cmd);
        } catch (DMNativeDaemonConnectorException e) {
            }
    }
    
     /*stop to listen the DMVold*/
    private void nativeEnd()  
    {
        String cmd = String.format("DMvolume end");
        try {
            mDMConnector.doCommand(cmd);
        } catch (DMNativeDaemonConnectorException e) {
            }
    }
	private void nativeMountISO(String isoFilePath)
	{
        String cmd = String.format("DMvolume mountISO \"%s\"", isoFilePath);
        try {
            mDMConnector.doCommand(cmd);
        } catch (DMNativeDaemonConnectorException e) {
            }
	}
	private void nativeUmountISO(String isoMountPath)
	{
        String cmd = String.format("DMvolume umountISO \"%s\"", isoMountPath);
        try {
            mDMConnector.doCommand(cmd);
        } catch (DMNativeDaemonConnectorException e) {
            }
	}
	private void nativeMountISOex(String isoFilePath, String isoLabel)
	{
        String cmd = String.format("DMvolume mountISOex \"%s\" \"%s\"", isoFilePath, isoLabel);
        try {
            mDMConnector.doCommand(cmd);
        } catch (DMNativeDaemonConnectorException e) {
            }
	}
}
