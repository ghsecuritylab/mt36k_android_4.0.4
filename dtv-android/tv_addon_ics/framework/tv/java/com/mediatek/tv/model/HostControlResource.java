    /*
 * Copyright (C) 2010 MediaTek USA, Inc
 *
 */

package com.mediatek.tv.model;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.service.CIService;
import com.mediatek.tv.service.ITVRemoteService;

import android.os.Parcelable;
import android.os.RemoteException;


public abstract class HostControlResource implements Parcelable{
    private static final String TAG = "HostControl";
    protected CIService ciservice;
    public void setCIService(CIService service)
     {
         this.ciservice = service;
     }

    protected void askRelease()
    {
    	this.ciservice.setHostControlReplace(null);
       try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                service.askRelease_proxy(this.ciservice.getSlotID());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public abstract int restoreHost();
};

