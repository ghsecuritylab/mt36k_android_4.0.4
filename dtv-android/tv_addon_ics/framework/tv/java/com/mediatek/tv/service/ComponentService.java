package com.mediatek.tv.service;
import java.util.ArrayList;

import android.os.RemoteException;
import java.io.IOException; 

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.CompListener;
import com.mediatek.tv.model.KeyMapReader;

public class ComponentService implements IService  {
  
	public static String CompServiceName = "CompService";

    public static final int COMP_RET_OK   = 0;
    public static final int COMP_RET_FAIL = -1;

    public static final String COMP_TTX_AVAIL               = "Comp_TTX_Avail";
    public static final String COMP_TTX_NOT_AVAIL           = "Comp_TTX_NotAvail";
    public static final String COMP_TTX_SHOW_NO_TELETEXT    = "Show_No_Teletext";
    public static final String COMP_TTX_ACTIVATE            = "Comp_TTX_Activate";
    public static final String COMP_TTX_COMP_INACTIVATE     = "Comp_TTX_InActivate";

    /* for updated sys status */	
    /*when change to TV source or SVL updated, there is not any SVL*/
    public static final String COMP_TTX_SYS_STATUS_EMPTY_SVL = "Set_Status_Empty_SVL";

    /* Before Start channel Scan */
    public static final String COMP_TTX_SYS_CHANNEL_SCANNING = "Set_Status_Channel_Scaning";
    /* channel Scan  finished */
    public static final String COMP_TTX_SYS_SCAN_FINISHED    = "Set_Status_Scan_Finished";
    /* Before channel Change */
    public static final String COMP_TTX_SYS_BEFORE_SVC_CHG   = "Set_Status_Before_SVC_CHG";
    /* After channel Change */
    public static final String COMP_TTX_SYS_AFTER_SVC_CHG    = "Set_Status_After_SVC_CHG";
    /* After set the mute on*/
    public static final String COMP_TTX_SYS_MUTE_ON          = "Set_Status_Mute_On";
    /* After set the mute off*/
    public static final String COMP_TTX_SYS_Mute_OFF         = "Set_Status_Mute_Off";
    /* After update screen mode */
    public static final String COMP_TTX_SYS_ASPECT_RATIO_CHG = "Set_Status_Aspect_Ratio_Change";
    /* After  set zoom ratio*/
    public static final String COMP_TTX_SYS_ZOOM_MODE_CHG    = "Set_Status_Zoom_Mode_Change";
    /* After set Freeze */ 
    public static final String COMP_TTX_SYS_AFTER_FREEZE     = "Set_Status_After_Freeze";
    /* After set unfreeze */
    public static final String COMP_TTX_SYS_AFTER_UNFREEZE   = "Set_Status_After_UnFreeze";
    
    public static final String COMP_TTX_NAME = "ttx_comp";
    
    public static final int  KEY_EVENT_DOWN   = 0;
    public static final int  KEY_EVENT_UP     = 1;
    public static final int  KEY_EVENT_REPEAT = 2;
	
	private static ArrayList<CompListener> listens = new ArrayList<CompListener>();	
	private static KeyMapReader            KeyCodeMap = null;
	
	private static final String TAG = "[COMP_SVC] ";
	private int checkReturn(int ret, String strLog) throws TVMException{
		if (ret < 0) {
			Logger.e(TAG, strLog + " failed.");
			throw new TVMException(ret, strLog + " failed.");
		} else {
			Logger.i(TAG, strLog + " success!");
		}
		
		return ret;
	}

	protected static ComponentService addedCompService;	


	protected ComponentService() {
		    Logger.i(TAG, "Enter ComponentService");
	        ComponentService.addCompService(this);
	        if(null == KeyCodeMap)
	        {
	        	try{
	        	    KeyCodeMap = new KeyMapReader("system/usr/keylayout/ttxkeymap.ini");
	        	}
	        	catch (IOException e) {
	                   e.printStackTrace();
	            }
	        	
	        }
	}
	
	protected static void addCompService(ComponentService CompService) {
		ComponentService.addedCompService = CompService;
    }
	 public void addListener(CompListener CompNotify) {
	        if (CompNotify == null) {
	            Logger.i(TAG, "Invalid CompNotify");
	            return;
	        }

	        synchronized (ComponentService.this) {
	            if (CompNotify != null) {
	                if (!listens.contains(CompNotify)) {
	                    listens.add(CompNotify);
	                }
	            }
	        }
	    }
	 
	    public void removeListener(CompListener CompNotify) {
	        synchronized (ComponentService.this) {
	            if (listens.contains(CompNotify)) {
	                listens.remove(CompNotify);
	            }
	        }
	    }

    public static  int notifyCompInfo(String NotifyInfo) {   
    	android.util.Log.i(TAG, NotifyInfo);
        for (int i = 0; i < listens.size(); i++) {
        	CompListener InfoListener = listens.get(i);
        	InfoListener.CompNotifyInfo(NotifyInfo);
        }
        return 0;
    }
    
	/* Public method */
	/**
	 * notify of Comp.
	 * 
	 * @see #ACTION_COMP_NFY
	 */
	public static final String ACTION_COMP_NFY = "android.tv.COMP_NFY";
		
	public int activateComponent(String Comp_name) throws TVMException {
        int ret = COMP_RET_OK;
        try {
        	android.util.Log.i(TAG, "ActivateComponent");
            ITVRemoteService service = TVManager.getRemoteTvService();
            Logger.i(TAG, "After getRemoteTvService");
            if (service != null) {
            	 Logger.i(TAG, "before activateComponent_proxy");
                   ret = service.activateComponent_proxy(Comp_name);   
                   Logger.i(TAG, "After activateComponent_proxy");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		return checkReturn(ret, "Exit Activate Component");
	}	
	
	public int inActivateCompoent(String Comp_name)throws TVMException {
		int ret = COMP_RET_OK;
        try {
        	android.util.Log.i(TAG, "InActivateCompoent");
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                   ret = service.inactivateComponent_proxy(Comp_name);               
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		
		return checkReturn(ret,  "Exit In-activate Component");
	}
	
	public int updateSysStatus(String Statustype)throws TVMException {
		int ret = COMP_RET_OK;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                   ret = service.updateSysStatus_proxy(Statustype);               
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }		
		return checkReturn(ret,"Exit updateSysStatus_proxy");
	}
	
	public boolean isTTXAvail()throws TVMException {
		boolean ret = false;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                   ret = service.isTTXAvail_proxy();               
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }		
		return ret;
	}
	
	public  int sendkeyEvent(int i4_keycode, int  keyevent)throws TVMException {
		int ret = COMP_RET_OK;
        
		try {
			android.util.Log.i(TAG, "sendkeyEvent");
            ITVRemoteService service = TVManager.getRemoteTvService();
            Logger.i(TAG, "After getRemoteTvService");
            if (service != null) {
            	   Logger.i(TAG, "Before sendkeyEventtoComp_proxy");
            	   int i4_MTKKeyCode = KeyCodeMap.getMTKKeyCode(i4_keycode);
            	   if( -1 != i4_MTKKeyCode)
            	   {  
            	      android.util.Log.i(TAG, "sendkeyEvent APPKeyCode = " + i4_keycode + " MTKKeyCode = " + i4_MTKKeyCode);
                      ret = service.sendkeyEventtoComp_proxy(i4_MTKKeyCode, keyevent);
            	   }
            	   else
            	   {
            		   android.util.Log.i(TAG, "sendkeyEvent APPKeyCode = " + i4_keycode + " does not send to Comp!!!");
            	   }
                   Logger.i(TAG, "After sendkeyEventtoComp_proxy");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }		
	
        return checkReturn(ret,"Exit sendkeyEventtoComp_proxy");
	}
}
