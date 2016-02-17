    /*
 * Copyright (C) 2010 MediaTek USA, Inc
 *
 */

package com.mediatek.tv.model;

import com.mediatek.tv.service.CIService;
import android.os.Parcelable;


public abstract class CIPath implements Parcelable{
    private static final String TAG = "CIPath";
    protected CIService ciservice;
    
    protected CIPath(CIService service)
    {
        this.ciservice = service;
     }
    public abstract void switchPath(boolean on_off);
 }



