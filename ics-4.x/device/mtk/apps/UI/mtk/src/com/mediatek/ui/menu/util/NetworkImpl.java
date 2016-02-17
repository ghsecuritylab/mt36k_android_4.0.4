package com.mediatek.ui.menu.util;

import android.content.Context;

import com.mediatek.netcm.ethernet.EthernetImplement;

/**
 * network related interface, used for UI
 * 
 * @author mtk40462
 * 
 */
public class NetworkImpl {
    private Context mContext;
    private static NetworkImpl networkImpl = null;
    static EthernetImplement ethImp;

    private int netOff = 0;
    private int netOn = 1;

    private NetworkImpl(Context context) {
        mContext = context;
        ethImp = EthernetImplement.getInstance(context);
    }

    public static NetworkImpl getInstance(Context context) {
        if (networkImpl == null) {
            networkImpl = new NetworkImpl(context);
        }

        return networkImpl;
    }

    /**
     * open or close network connection
     * 
     * @param enabled
     *            0 close network connection and 1 open network connection
     */
    public void openEthernet(int enabled) {
        if (enabled == netOff) {
            ethImp.closeEthernet();
        } else if (enabled == netOn) {
            ethImp.openEthernet();
        }
    }

    /**
     * get connection mode
     * 
     * @return auto or manual
     */
    public String getConnectMode() {
        return ethImp.getConnectMode();
    }

    /**
     * get network mask
     * 
     * @return network mask address
     */
    public String getNetMask() {
        return ethImp.getNetMask();
    }

    /**
     * get IP address
     * 
     * @return IP address xxx.xxx.xxx.xxx
     */
    public String getIPAddress() {
        return ethImp.getIpAddress();
    }

    /**
     * get route address
     * 
     * @return route address
     */
    public String getRouteAddr() {
        return ethImp.getRouteAddr();
    }

    /**
     * get primary DNS
     * 
     * @return primary DNS
     */
    public String getDnsAddr() {
        return ethImp.getDnsAddr();
    }

    /**
     * get MAC address
     * 
     * @return MAC address
     */
    public String getMacAddr() {
        return ethImp.getMacAddr();
    }
}
