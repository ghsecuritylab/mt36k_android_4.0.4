package com.mediatek.pppoe;

import android.content.Context;
import android.net.IConnectivityManager;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.IEthernetManager;
import android.net.wifi.IWifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

import java.util.ArrayList;

public class PppoeService {
    static {
    	System.loadLibrary("pppoedial");
    }
    public static final int DISCONNECT = 0;
    public static final int CONNECTING = 1;
    public static final int CONNECT = 2;

    
    private static PppoeService pppoe = new PppoeService();

    private IEthernetManager ethernetManager;
    private IConnectivityManager connectivityService;
    private IWifiManager wifiManager ;
    private EthernetDevInfo savedInfo;
    private EthernetDevInfo current;
    private EventListener listener;
    private MonitorStatus monitor;
    private PppoeInfo pppoeInfo;



    private static final String DEFAULT_DEV = "eth0";
    
    
    public class PppoeInfo {
        public String ipaddr;
        public String netmask;
        public String server;
        public String dns1;
        public String dns2;

        public PppoeInfo(String ipaddr, String netmask, String server, String dns1, String dns2) {
            this.ipaddr = ipaddr;
            this.netmask = netmask;
            this.server = server;
            this.dns1 = dns1;
            this.dns2 = dns2;
        }
    }

    
    public static PppoeService getInstance() {
    	return pppoe;
    }

    private PppoeService() {
        current = new EthernetDevInfo();
        IBinder eth = ServiceManager.getService(Context.ETHERNET_SERVICE);
        ethernetManager = IEthernetManager.Stub.asInterface(eth);
        IBinder wifi = ServiceManager.getService(Context.WIFI_SERVICE);
        wifiManager = IWifiManager.Stub.asInterface(eth);
        IBinder connect = ServiceManager.getService(Context.CONNECTIVITY_SERVICE);
        connectivityService = IConnectivityManager.Stub.asInterface(connect);
        savedInfo = new EthernetDevInfo();
        try {
            savedInfo = ethernetManager.getSavedConfig();
        } catch (RemoteException e) {
            savedInfo = new EthernetDevInfo();
            savedInfo.setIfName(DEFAULT_DEV);
            savedInfo.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP);
            e.printStackTrace();
        }
    }

    private void setupDial() throws RemoteException {
        /*EthernetDevInfo info = new EthernetDevInfo();
        info.setIfName(savedInfo.getIfName());
        info.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_MANUAL);
        service.updateDevInfo(info);  */

        current.setIfName(DEFAULT_DEV);
    }

    private void setupDial(String device) throws RemoteException {

        /*current.setIfName(device);

        current.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_MANUAL);

        current.setIpAddress(null);
        current.setRouteAddr(null);
        current.setDnsAddr(null);
        current.setNetMask(null);
        current.setDns2Addr(null);

        ethernetManager.updateDevInfo(current);  */

        current.setIfName(device);
    }

    private void teardownDial() throws RemoteException {

        /*EthernetDevInfo info = new EthernetDevInfo();
        info.setIfName(savedInfo.getIfName());

        info.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP);
        info.setIpAddress(null);
        info.setRouteAddr(null);
        info.setDnsAddr(null);
        info.setNetMask(null);*/

        if (savedInfo != null) {
            ethernetManager.updateDevInfo(savedInfo);
        }
    }

    /**
     * @deprecated see  PppoeService.dialUp(String device, String username, String password)
     * */
    public int dialUp(String username, String password) {
        try {
            setupDial();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        int ret = nativeDialUp(DEFAULT_DEV,username, password);
        monitor = new MonitorStatus(this);
        monitor.execute();
        return ret;
    }

    public int dialUp(String device, String username, String password) {
        try {
            setupDial(device);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        int ret = nativeDialUp(device, username, password);
        monitor = new MonitorStatus(this);
        monitor.execute();
        return ret;
    }

    public ArrayList<String> getDevices() {
        ArrayList<String> list = new ArrayList<String>();
        String s = nativeGetDevices();
        String[] devs = s.split("#");
        for (String dev : devs) {
            if(dev != null && !dev.equals("")){
                list.add(dev);
            }
        }
        return list;
    }

    /*  @deprecated see  PppoeService.hangUp(String device) */
    public int hangUp() {
        if(monitor != null) {
            monitor.cancel(true);
        }
        try {
            teardownDial();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        nativeMonitorStop(DEFAULT_DEV);
        return nativeHangUp();
    }

    public int hangUp(String device) {
        if(monitor != null) {
            monitor.cancel(true);
        }
        try {
            teardownDial();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        nativeMonitorStop(device);
        return nativeHangUp();
    }
    
    public int getStatus() {
    	return nativeGetStatus();
    }
    
    public String getMacAddr() {
    	try {
    		return ethernetManager.getMacAddr();
    	} catch (RemoteException e) {
    		return null;
    	}
    }

    public EventErrorType getErrorCode() {
        String error = nativeGetErrorCode();
        if(error.equals("TIMEOUT")) {
            return EventErrorType.TIMEOUT;
        } else if(error.equals("AUTHENTICATION_ERROR"))  {
            return EventErrorType.AUTHENTICATION_ERROR;
        } else if(error.equals("UNKNOWN"))  {
            return EventErrorType.UNKNOWN;
        }
        return EventErrorType.FAILED;
    }

    public void setEventListener(EventListener listener) {
        this.listener = listener;
    }

    public PppoeInfo getPppoeInfo() {
        return pppoeInfo;
    }

    private boolean update() {
        try {
            NetworkInfo info = connectivityService.getActiveNetworkInfo();
            if(info == null) {

                return true;
            }

            if (info.getTypeName().equals(DEFAULT_DEV)) {
                savedInfo = new EthernetDevInfo();
                try {
                    savedInfo = ethernetManager.getSavedConfig();
                } catch (RemoteException e) {
                    savedInfo = new EthernetDevInfo();
                    savedInfo.setIfName(DEFAULT_DEV);
                    savedInfo.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP);
                    e.printStackTrace();
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    private native int nativeDialUp(String device, String username, String password);
    private native int nativeHangUp();
    private native int nativeGetStatus();
    private native String nativeGetErrorCode();
    private native String nativeGetInfo();
    private native void nativeMonitorStart(String device);
    private native void nativeMonitorStop(String device);
    private native String nativeGetDevices();

    public enum EventType {
        DISCONNECTED ,
        CONNECTING ,
        CONNECTED,
    };

    public enum EventErrorType {
        SUCCESSFUL,
        FAILED,
        TIMEOUT,
        AUTHENTICATION_ERROR,
        COMPATIBILITY_ISSUE,
        SERVER_ERROR,
        UNKNOWN
    };
    
    public interface EventListener {
        public void onEvent(EventType type, int precent, EventErrorType error) ;
    }
	
    
    private class MonitorStatus extends AsyncTask<Void, Void, Void>{
        private static final int RETRY_TIMES = 30;
        private PppoeService service;

        public MonitorStatus(PppoeService service) {
            this.service = service;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < RETRY_TIMES; i++) {
                int status = service.getStatus();
                switch (status) {
                    case DISCONNECT: {
                        if(listener != null) {
                            listener.onEvent(EventType.DISCONNECTED, 0, getErrorCode());
                        }
                        if(current.getIfName()!=null) {
                            hangUp(current.getIfName());
                        }  else {
                            hangUp(DEFAULT_DEV);
                        }

                        return null;
                    }
                    case CONNECTING:  {
                        if(listener != null) {
                            listener.onEvent(EventType.CONNECTING, (i*100)/RETRY_TIMES, EventErrorType.UNKNOWN);
                        }
                        break;
                    }
                    case CONNECT: {
                        String info = service.nativeGetInfo();
                        String[] s = info.split("#");
                        pppoeInfo = new PppoeInfo(s[0], s[1], s[2], s[3], s[4]);
                        if (update()) {
                            current.setIpAddress(s[0]);
                            current.setNetMask(s[1]);
                            current.setRouteAddr(s[2]);
                            current.setDnsAddr(s[3]);
                            current.setDns2Addr(s[4]);
                            
                            try {
                                service.ethernetManager.updateDevInfo(current);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        if(listener != null) {
                            listener.onEvent(EventType.CONNECTED, 100, EventErrorType.SUCCESSFUL);
                        }
                        nativeMonitorStart(current.getIfName());
                        return null;
                    }
                    default:
                        return null;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }

            if(listener != null) {
                listener.onEvent(EventType.DISCONNECTED, 0, EventErrorType.TIMEOUT);
            }

            if(current.getIfName()!=null) {
                hangUp(current.getIfName());
            }  else {
                hangUp(DEFAULT_DEV);
            }

            return null;  
        }
    }
}
