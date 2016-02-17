    /*
 * Copyright (C) 2010 MediaTek USA, Inc
 *
 */

package com.mediatek.tv.model;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.service.ITVRemoteService;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

public class MMIMenu extends MMI{        
    private static final String TAG = "MMIMenu";

    public MMIMenu(int menuId,byte choiceNum,String title,String subTitle,String bottom,String[] itemlist)
    {
      super(menuId);
      this.choice_nb = choiceNum;
      this.title = title;
      this.subtitle = subTitle;
      this.bottom = bottom;
      this.itemList = itemlist;
    }
    public MMIMenu()
    {
    }
	
    /**
	 * Send Close CAM's MMI menu command to CAM,after this api is called  CIService:getMMIMenu() will return NULL object
	 *In this case MMI menu is closed by host rather than by CAM            
	 *            
	 * @see CIService:getMMIMenu
	 */
    public void close()
    {
    	super.close();
    	this.ciservice.setMMIMenu(null);
    	
    }
    /**
	 * Notify cam that app has finished to clean up mmi menu,after this api is called  CIService:getMMIMenu() will return NULL object
	 * App will do some clean up actions after received CIListener:camMMIClosed()           
	 * @see CIService:getMMIMenu
	 * @see CIListener:camMMIClosed
	 */
    public void closeDone()
    {
    	super.close();
    	this.ciservice.setMMIMenu(null);
    	
    }
	
    /**
	 * Send menu item selection choice to CAM         
	 *            
	 * @param answer_item: the number of the choice selected by the user.
	 *answer_item = 01 corresponds to the first choice that had  been presented by app in MMIMenu object
	 *answer_item = 00 indicates that the user has cancelled the preceding MMIMenu object without making a choice
	 * @see CIService:getMMIMenu
	 */
    public void answer( char answer_item)
    {
       try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                service.answerMMIMenu_proxy(this.ciservice.getSlotID(),this.mmi_id,answer_item);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
	 * Query title string of the mmi menu sent by cam       
	 */
    public String getTitle()
    {
        return title;
    }
    /**
	 * Query sub title string of the mmi menu sent by cam       
	 */
    public String getSubtitle()
    {
        return subtitle;
    }  
    /**
	 * Query bottom string of the mmi menu sent by cam       
	 */
    public String getBottom()
    {
        return bottom;
    } 
    /**
	 * Query time number of the mmi menu sent by cam       
	 */
    public byte getItemNum()
    {
        return choice_nb;
    } 
    /**
	 * Query item string list of the mmi menu sent by cam       
	 */
    public String[] getItemList()
    {
        return itemList;
    } 
     private  String     title;
     private  String     subtitle;
     private  String     bottom;
     private  byte       choice_nb;     
     private  String[]   itemList;

	 
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	  public static final Creator<MMIMenu> CREATOR = new Parcelable.Creator<MMIMenu>() {
	        public MMIMenu createFromParcel(Parcel source) {
	            return new MMIMenu(source);
	        }

	        public MMIMenu[] newArray(int size) {
	            return new MMIMenu[size];
	        }
	    };

	    private MMIMenu(Parcel source) {
	        readFromParcel(source);
	    }

	    public void writeToParcel(Parcel out, int flags) {
	        out.writeInt(mmi_id);
	        out.writeByte(choice_nb);
	    	out.writeString(title);
	        out.writeString(subtitle);
	        out.writeString(bottom);
	        out.writeStringArray(itemList);
	    }

	    public void readFromParcel(Parcel in) {
	    	mmi_id = in.readInt();
	    	choice_nb = in.readByte();
	    	title = in.readString();
	    	subtitle = in.readString();
	    	bottom =  in.readString();
	    	in.readStringArray(itemList);
	    }

	    public String toString() {
	        StringBuilder builder = new StringBuilder();
	        builder.append("MMIMenu [menu id=");
	        builder.append(this.mmi_id);
	        builder.append("\n");
	        builder.append("title=");
	        builder.append(title);
	        builder.append("\n");
            builder.append("subtitle=");
	        builder.append(subtitle);
	        builder.append("\n");
	        builder.append("bottom=");
	        builder.append(bottom);
	        builder.append("\n");
	        builder.append("choice_nb=");
	        builder.append(choice_nb);
	        builder.append("\n");
            int i=0;
            for(;i<choice_nb;i++)
            {
	        builder.append(", itemList=");
	        builder.append(itemList[i]);
	        builder.append("\n");
           }
	        builder.append("\n");
	        builder.append("]");
	        return builder.toString();
	    }	
};
