/*
 * Copyright (C) 2006 The Android Open Source Project
 * 
 * ©2010-2013 TCL CORPORATION All Rights Reserved.
 */

package com.tcl.net.samba;

import java.util.List;
import java.util.Enumeration;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

import jcifs.netbios.NbtAddress;

import android.util.Log;
import android.content.Context;
import android.os.Handler;

/**
 * SmbClientService
 *
 * @see SmbDevice
 * @see SmbShareFolder
 */
public class SmbClient {

    private static final String TAG = "SmbClient";

    public static final String SMB_LOCAL_PATH = "/mnt/smb";
    private Context mCtx;
    private static List<SmbDevice> mDeviceList = new ArrayList<SmbDevice>();
    private static String mLocalIP;
    // sync thread monitor
    private static volatile boolean bSyncSmbListThreadRunning = false;
    private static volatile boolean bSyncSmbListThreadStop = false;
    private OnRecvMsgListener mOnRecvMsgListener;

    public SmbClient(Context ctx) {
        mCtx = ctx;
        mLocalIP = getLocalIpAddress();
    }

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    Log.d(TAG, "getLocalIpAddress : ip : "
                            + inetAddress.getHostAddress().toString());

                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = inetAddress.getHostAddress().toString();
                        String[] ips = ip.split("\\.");
                        if (ips.length == 4)
                            return ip;
                    }
                }
            }

        } catch (SocketException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private boolean isNetworkChanged() {

        String sLocalIP = getLocalIpAddress();

        if (!mLocalIP.equals(sLocalIP)) {
            mLocalIP = sLocalIP;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return SmbDevice object by ip address. Return null when ip address is
     * "not active". Don't need to check there's password or not.
     */
    private SmbDevice getSmbDeviceByIp(String ip) {

        Log.d(TAG, "getSmbServerByIpNetBios : " + ip);

        SmbDevice sdv = new SmbDevice();

        try {
            // Determines if this address is reachable
            if (!InetAddress.getByName(ip).isReachable(1000)) {
                Log.d(TAG, String.format(
                        "getSmbServerByIpNetBios : Server with ip %s is not reachable.", ip));
                return null;
            }

            NbtAddress nbt = NbtAddress.getByName(ip);

            if (nbt != null) {
                if (!nbt.isActive()) {
                    Log.d(TAG,
                            String.format(
                                    "getSmbServerByIpNetBios : Server with ip %s may be not suport SMB services.",
                                    ip));
                    return null;
                }

                NbtAddress[] all = NbtAddress.getAllByAddress(nbt);
                for (int i = 0; i < all.length; i++) {
                    NbtAddress n = all[i];
                    if (!n.isGroupAddress() && n.getNameType() == 0) {
                        System.out.print(n.getHostName());
                        sdv.mName = n.getHostName();
                        sdv.mIp = ip;
                        sdv.mHasPassWord = sdv.hasPassword();

                        Log.d(TAG, String.format("Find a SMB server *%s* with ip: %s", sdv.mName,
                                sdv.mIp));

                        return sdv;
                    }
                }
            }

        } catch (UnknownHostException e) {
            // wrong user name or password
            e.printStackTrace();
            Log.d(TAG, "getSmbServerByIpNetBios : UnknownHostException");
        } catch (IOException e) {
            // Unable to connect the host
            e.printStackTrace();
            Log.d(TAG, "getSmbServerByIpNetBios : IOException");
        }
        return null;
    }

    private int isInList(List<SmbDevice> list, String ip) {
        for (int i = 0; i < list.size(); i++) {
            if (ip.equals(list.get(i).mIp))
                return i;
        }
        return -1;
    }

    private void syncSmbDeviceList() {

        Log.d(TAG, "syncSmbDeviceList start");

        if (mLocalIP == null) {
            Log.d(TAG, "syncSmbDeviceList : can't get valid ip address !");
            return;
        }

        String[] ips = mLocalIP.split("\\.");

        SmbDevice tmpDevice;
        String ip;
        for (int currentI = 2; currentI < 255; currentI++) {
            if (bSyncSmbListThreadStop == true) {
                Log.d(TAG, "syncSmbDeviceList : stop the thread !");
                bSyncSmbListThreadStop = false;

                if (mOnRecvMsgListener != null) {
                    // Update to the latest interface
                    mOnRecvMsgListener
                            .onRecvMsgListener(OnRecvMsgListener.MSG_UPDATE_DEVLIST_CANCEL);
                }
                return;
            }

            if (ips.length > 2) {
                ip = ips[0] + "." + ips[1] + "." + ips[2] + "." + currentI;
            } else {
                Log.d(TAG, "syncSmbDeviceList : ip error --> " + mLocalIP);
                return;
            }

            if (mLocalIP.equals(ip))
                continue;

            int i = isInList(mDeviceList, ip);
            tmpDevice = getSmbDeviceByIp(ip);

            if (tmpDevice == null) {
                if (i != -1) {
                    // Except for ConcurrentModificationException, make sure the
                    // application will get a correct list. 
                    synchronized (mDeviceList) {
                        mDeviceList.remove(i);
                    }
                }
            } else {
                if (i != -1) // already in list
                    continue;

                Log.d(TAG, "syncSmbDeviceList : add ip:" + ip + " to list!");
                // The same reason with above.
                synchronized (mDeviceList) {
                    mDeviceList.add(tmpDevice);
                }
                if (mOnRecvMsgListener != null) {
                    // Update to the latest interface.
                    mOnRecvMsgListener.onRecvMsgListener(OnRecvMsgListener.MSG_UPDATE_DEVLIST_ADD);
                }
            }
        }

        Log.d(TAG, "syncSmbDeviceList : sync SMB device list done. count: " + mDeviceList.size());

        if (mOnRecvMsgListener != null) {
            // Update to the latest interface.
            mOnRecvMsgListener.onRecvMsgListener(OnRecvMsgListener.MSG_UPDATE_DEVLIST_DONE);
        }
    }

    /**     
     * Get the list of all host that with share folders
     * @author TCL TVOS Team
     * @param null
     * @return host list.
     */
    public List<SmbDevice> getSmbDeviceList() {

        if (SmbcUtils.getNetworkStatus(mCtx) == false) {
            Log.d(TAG, "getSmbDeviceList : network state: invalid !");
            mDeviceList.clear();
            return mDeviceList;
        }
        // Log.d(TAG, "getSmbDeviceList : mDeviceList = "+mDeviceList);
        return mDeviceList;
    }

    /**     
     * Set the callback method of smbclient
     * @author TCL TVOS Team
     * @param l the implement of OnRecvMsgListener
     */
    public void setOnRecvMsgListener(OnRecvMsgListener l) {
        mOnRecvMsgListener = l;
    }

    /**     
     * Get  all share folder list of all host .
     * @author TCL TVOS Team
     * @param null
     * @return share folder list.
     */
    public List<SmbShareFolder> getSharefolderList() {

        List<SmbShareFolder> folderList = new ArrayList<SmbShareFolder>();
        List<SmbShareFolder> tempList;

        for (SmbDevice sd : mDeviceList) {
            tempList = sd.getShareFolderList();
            try {
                folderList.addAll(tempList);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
        return folderList;
    }

    /**     
     * Update the list of all host that with share folders .
     * @author TCL TVOS Team
     * @param null
     */
    public void updateSmbDeviceList() {
        if (bSyncSmbListThreadRunning == true)
            return;

        // mDeviceList.clear();
        syncSmbListThreadStart();
        return;
    }

    /**     
     * Update the list of all host that with share folders .
     * @author TCL TVOS Team
     * @param null
     * @return true:updating now/false
     */
    public boolean isUpdating() {

        return bSyncSmbListThreadRunning;
    }

    /**     
     * Stop update the list of all host that with share folders .
     * @author TCL TVOS Team
     * @param null
     * @return true:stop update successed/false
     */
    public boolean stopUpdate() {
        if (bSyncSmbListThreadRunning == true) {
            bSyncSmbListThreadStop = true;
            return true;
        }
        return false;
    }

    private void syncSmbListThreadStart() {
        new SyncSmbListThread().start();
    }

    /** {@hide} */
    class SyncSmbListThread extends Thread {

        public void run() {
            bSyncSmbListThreadRunning = true;
            syncSmbDeviceList();
            bSyncSmbListThreadRunning = false;
        }

    }
}


