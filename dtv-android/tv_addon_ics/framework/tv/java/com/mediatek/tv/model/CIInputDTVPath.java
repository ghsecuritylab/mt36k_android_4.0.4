    /*
 * Copyright (C) 2010 MediaTek USA, Inc
 *
 */

package com.mediatek.tv.model;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.service.CIService;
import com.mediatek.tv.service.ITVRemoteService;

import android.os.Parcel;
import android.os.RemoteException;

public class CIInputDTVPath extends CIPath {
    private static final String TAG = "CIInputDTVPath";
           
   public CIInputDTVPath(CIService service)
   {
	   super(service);
   }
   public void switchPath(boolean on_off)
   {
      try {
           ITVRemoteService service = TVManager.getRemoteTvService();
           if (service != null) {
               service.setCIInputDTVPath_proxy(this.ciservice.getSlotID(),on_off);
           }
       } catch (RemoteException e) {
           e.printStackTrace();
       }
   }
public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
}
public void writeToParcel(Parcel arg0, int arg1) {
	// TODO Auto-generated method stub
	
}
 };
