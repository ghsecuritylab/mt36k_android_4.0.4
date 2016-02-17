package com.mediatek.tv.service;


import java.util.LinkedList;
import java.util.ListIterator;
import java.lang.IllegalArgumentException;

import android.os.RemoteException;
import com.mediatek.tv.TVManager;
import com.mediatek.tv.model.MMIMenu;
import com.mediatek.tv.model.MMIEnq;
import com.mediatek.tv.model.HostControlTune;
import com.mediatek.tv.model.HostControlReplace;
import com.mediatek.tv.model.CIPath;
import com.mediatek.tv.model.CITSPath;
import com.mediatek.tv.model.CIInputDTVPath;
import com.mediatek.tv.model.CIListener;

public class CIService implements IService {
    private static final String TAG = "[CI_SVC] ";
    public static String CIServiceName = "CIServiceName";
    protected LinkedList<CIListener> ciListenerList = new LinkedList<CIListener>();
    private static CIService service;
    int slot_id;

    private CITSPath ts_path;
    private CIInputDTVPath inputsource_path;
    private MMIMenu menu;
    private MMIEnq enq;
    private HostControlTune tune;
    private HostControlReplace replace;
    private int[] systemIdInfo;
    private byte system_id_status;
    
    protected CIService(int slotid) {
          this.slot_id = slotid;  
          this.system_id_status = CIListener.CI_CAM_SYSTEM_ID_STATUS_INVALID;
          inputsource_path = new CIInputDTVPath(this);
          ts_path = new CITSPath(this);          
  
    }
    protected CIService() {
        this.slot_id = 0;  
        this.system_id_status = CIListener.CI_CAM_SYSTEM_ID_STATUS_INVALID;
        inputsource_path = new CIInputDTVPath(this);
        ts_path = new CITSPath(this);          

  }
  /**
	 * Query system supported PCMCIA slot number,now only supported one slot 
	 * 
	 * @param Void
	 *            
	 * @return supported PCMCIA slot number, should be 1
	 */
    public static int getSlotNum()
    {
       int slotNum = 0;
       try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
               slotNum = service.getSlotNum_proxy();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return slotNum;
    }
  
  /**
	 * Query CIService instance, for one slot id only one instance returned,now only supported one slot 
	 * 
	 * @param slotid: need input 0
	 *            
	 * @return CISerivce instance
	 */
    public static CIService getInstance(int slotid)
    {
        if(slotid == 0)
        {
           if(service == null)
            {
              service = new CIService(0);
            }
        }
        else
        {
          throw new IllegalArgumentException("slot id");
        }
        return service;
    }
  
  /**
	 * Query slot id related to this CIService object
	 * 
	 * @param Void
	 *            
	 * @return slot id, now will return 0
	 */
    public int getSlotID()
    {
    	return this.slot_id;
    }
  
  /**
	 * Add CI Listener to get CAM's notification
	 * 
	 * @param ciListener: one object that implement CIListener interface class
	 *            
	 * @see CIListener
	 */
    public void addCIListener(CIListener ciListener)
    {
        ciListenerList.add(ciListener);
    }
    /**
	 * Remove CI  Listener 
	 * 
	 * @param ciListener
	 *            
	 * @see CIListener
	 */
    public void removeCIListener(CIListener ciListener)
    {
        ciListenerList.remove(ciListener);
    }
	
    /**
	 * Enter CAM's MMI menu ,this api only can be called after received 
	 * CIListener:camStatusUpdated(CI_CAM_STATUS_NAME)
	 *            
	 * @see CIListener:camStatusUpdated
	 */
    public void enterMMI()
    {
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                service.enterMMI_proxy(this.slot_id);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
	
    public CIPath getCIInputDTVPath()
    {
        return inputsource_path;
    }
    public CIPath getCITSPath()
    {
          return ts_path;
    }
	
    /**
	 * Query CAM's MMIMenu ,this api only return valid object after received 
	 * CIListener:camMMIMenuReceived()
	 *            
	 * @see CIListener:camMMIMenuReceived
	 * @see MMIMenu
	 */
    public MMIMenu getMMIMenu()
    {
        return this.menu;
    }
    
    /**
	 * Query CAM's MMIEnq ,this api only return valid object after received 
	 * CIListener:camMMIEnqReceived()
	 *            
	 * @see CIListener:camMMIEnqReceived
	 * @see MMIEnq
	 */
      public MMIEnq getMMIEnq()
    {
        return this.enq;
    }
    
    /**
	 * Query CAM's MMIEnq ,this api only return valid object after received 
	 * CIListener:camHostControlTune()
	 *            
	 * @see CIListener:camHostControlTune
	 * @see HostControlTune
	 */  
    public HostControlTune getHostControlTune()
    {
        return this.tune;
    }
    
    /**
	 * Query CAM's MMIEnq ,this api only return valid object after received 
	 * CIListener:camHostControlReplace()
	 *            
	 * @see CIListener:camHostControlReplace
	 * @see HostControlReplace
	 */
    public HostControlReplace getHostControlReplace()
    {
        return this.replace;
    }
	
    /**
	 * Query CAM if is active ,this api only return TRUE after received 
	 * CIListener:camStatusUpdated(CI_CAM_STATUS_NAME)
	 *            
	 * @see CIListener:camStatusUpdated
	 */
    public boolean isSlotActive()
    {
         boolean active = false;
         try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                active = service.isSlotActive_proxy(this.slot_id);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return active;
    }
	
    /**
	 * Query CAM's name ,this api only return valid String object after received 
	 * CIListener:camStatusUpdated(CI_CAM_STATUS_NAME)
	 *            
	 * @see CIListener:camStatusUpdated
	 */
    public String getCamName()
    {
    	String camName = "";
         try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
            	camName = service.getCamName_proxy(this.slot_id);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return camName;
    }
	
    /**
	 * Query CAM's system id status, the return value is same as argument in call:
	 * CIListener:camSystemIDStatusUpdated()
	 *            
	 * @see CIListener:camSystemIDStatusUpdated
	 */
    public byte getCamSystemIDStatus()
    {
        return this.system_id_status;
    }
	
    /**
	 * Query CAM's ca id array ,this api only return valid object after after received 
	 * CIListener:camSystemIDStatusUpdated(CI_CAM_SYSTEM_ID_STATUS_READY)
	 *            
	 * @see CIListener:camSystemIDStatusUpdated
	 */
    public int[] getCamSystemIDInfo()
    {
    /*	int[] arr_id = {0};
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
            	arr_id = service.getCamSystemIDInfo_proxy(this.slot_id);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/
        return systemIdInfo;
    }
     public void setMMIMenu(MMIMenu menu)
    {
     this.menu = menu;
     if(this.menu != null)
     {
    	 this.menu.setCIService(this);
     }

    }
    public void setMMIEnq(MMIEnq enq)
    {
        this.enq = enq;
        if(this.enq != null)
        {
        	this.enq.setCIService(this);
        }
   }
    public void setHostControlTune(HostControlTune tune)
      {
        if(tune != null)
        {
          tune.setCIService(this);
        }
          this.tune = tune;
      }
    public void setHostControlReplace(HostControlReplace replace)
    {
        if(replace != null)
        {
          enq.setCIService(this);
        }
        this.replace = replace;
    }
    
   
    /******************************************************/
    /*  callback apis*/
    /******************************************************/
    protected int camStatusUpdated(byte cam_status) {
    	  if(cam_status == CIListener.CI_CAM_STATUS_REMOVE)
    	  {
    		  this.enq = null;
    		  this.menu = null;
    		  this.tune = null;
    		  this.replace = null;
    		  this.system_id_status = CIListener.CI_CAM_SYSTEM_ID_STATUS_INVALID;
    	  }
    	  else if(cam_status == CIListener.CI_CAM_STATUS_INSERT ||
    			  cam_status == CIListener.CI_CAM_STATUS_NAME)
    	  {
    		  this.system_id_status = CIListener.CI_CAM_SYSTEM_ID_STATUS_WAIT;    		    
    	  }
          ListIterator<CIListener> itr = ciListenerList.listIterator();
          while(itr.hasNext())
           {
              CIListener lis = (CIListener)itr.next();
              lis.camStatusUpdated(cam_status);
           }
          if(cam_status == CIListener.CI_CAM_STATUS_NAME)
    		{
         		System.out.println("slot active " + isSlotActive());	
     		}

           return 0;
       }
    protected int camMMIMenuReceived(int menuId,byte choiceNum,String title,String subTitle,String bottom,String[] itemlist) {
   	  	  System.out.println("camMMIMenuReceived 0");		
   	       this.menu = new MMIMenu(menuId,choiceNum,title,subTitle,bottom,itemlist);
   	       this.menu.setCIService(this);
   	  	  System.out.println("camMMIMenuReceived:"+this.menu.toString());		

           ListIterator<CIListener> itr = ciListenerList.listIterator();
            while(itr.hasNext())
             {
                CIListener lis = (CIListener)itr.next();
                lis.camMMIMenuReceived();
             }           
             return 0;
       }
    protected int camMMIEnqReceived(MMIEnq MMI_enq) {
 	  	  System.out.println("camMMIEnqReceived 0"+MMI_enq.toString());		
           this.setMMIEnq(MMI_enq);
           ListIterator<CIListener> itr = ciListenerList.listIterator();
            while(itr.hasNext())
             {
                CIListener lis = (CIListener)itr.next();
                lis.camMMIEnqReceived();
             }            
             return 0;
       }
    protected int camMMIClosed( byte mmi_close_delay) {
          ListIterator<CIListener> itr = ciListenerList.listIterator();
          while(itr.hasNext())
          {
              CIListener lis = (CIListener)itr.next();
              lis.camMMIClosed(mmi_close_delay);
          }              
          return 0;
       }
    protected int camHostControlTune(HostControlTune tune_request) {
	  	  System.out.println("camHostControlTune:"+tune_request.toString());		
        this.setHostControlTune(tune_request);
          ListIterator<CIListener> itr = ciListenerList.listIterator();
          while(itr.hasNext())
          {
              CIListener lis = (CIListener)itr.next();
              lis.camHostControlTune();
          }              
           return 0;
       }
    protected int camHostControlReplace(HostControlReplace replace_request) {
	  	  System.out.println("HostControlReplace 0"+replace_request.toString());		
         this.setHostControlReplace(replace_request);
           ListIterator<CIListener> itr = ciListenerList.listIterator();
           while(itr.hasNext())
           {
               CIListener lis = (CIListener)itr.next();
               lis.camHostControlReplace();
           }              
           return 0;
       }
    protected int camHostControlClearReplace(byte refId) {
	  	  System.out.println("camHostControlClearReplace 0");		
          if(this.replace.getRefId() != refId)
            {
                return -1;
            }
            ListIterator<CIListener> itr = ciListenerList.listIterator();
           while(itr.hasNext())
           {
               CIListener lis = (CIListener)itr.next();
               lis.camHostControlClearReplace();
           }         
           return 0;
       }
    protected int camSystemIDStatusUpdated(byte sys_id_status) {
           this.system_id_status = sys_id_status;
           ListIterator<CIListener> itr = ciListenerList.listIterator();
           while(itr.hasNext())
           {
               CIListener lis = (CIListener)itr.next();
               lis.camSystemIDStatusUpdated(sys_id_status);
           }         
          return 0;
       }
    protected int camSystemIDInfoUpdated(int[] arrInfo)
    {
    	this.systemIdInfo = arrInfo;
	    int i =0;
		System.out.println("system id num " + systemIdInfo.length);		

        for(;i<systemIdInfo.length;i++)
        {
        	System.out.println("system id  " + systemIdInfo[i]);		
     }        
    	return 0;
    }


}



