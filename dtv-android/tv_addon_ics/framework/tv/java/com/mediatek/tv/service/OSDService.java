package com.mediatek.tv.service;

import android.os.RemoteException;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.TVMException;

/**
 * This class provides OSD manager service
 * <ul>
 * <li>Set/enable OSD colorkey.</li>
 * <li>Set OSD fading.</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class OSDService implements IService {
    private static final String TAG = "[J]OSDService";

    public static String OSDServiceName = "OSDService";

    protected OSDService() {
    }

    public boolean setColorKey(boolean enabled, int color) {
        boolean ret = false;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setOSDColorKey_proxy(enabled, color);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean setOpacity(int opacity) {
        boolean ret = false;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setOSDOpacity_proxy(opacity);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
