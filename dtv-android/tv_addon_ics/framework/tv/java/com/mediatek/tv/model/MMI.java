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

public abstract class MMI implements Parcelable{
    private static final String TAG = "MMI";    
    protected int                 mmi_id;
    protected CIService ciservice;
    public MMI(int mmi_id)
    {
        this.mmi_id = mmi_id;
    }
    public MMI()
    {
    	
    }
    public void setCIService(CIService service)
     {
         this.ciservice = service;
     }
	
    public void close()
    {
     try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                service.closeMMI_proxy(this.ciservice.getSlotID());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void closeDone()
    {
      try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                service.setMMIClosed_proxy(this.ciservice.getSlotID());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
