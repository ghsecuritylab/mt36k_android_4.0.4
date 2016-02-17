
package com.tcl.os.system;

import android.content.Context;
import android.view.IWindowManager;
import android.os.ServiceManager;
import android.os.RemoteException;


public class WindowManager {
    static final String TAG = "tcl_WindowManager";
    static final boolean DEBUG = true;


    public WindowManager(Context context) {
        return;
    }

    public void activeHomeKey(boolean bEnableHomeKey) {

   	IWindowManager wm = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));

		try{
			wm.setEnableHomeKey(bEnableHomeKey);
			}
		catch (RemoteException e) {
			e.printStackTrace();
			} 
			
	
    }

}
