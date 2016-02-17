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


public class MMIEnq extends MMI{
    private static final String TAG = "MMIEnq";           

    public MMIEnq(int enq_id,byte anwDataLen,byte bBlindAnswe,String text)
    {
       super(enq_id);
       this.ans_txt_len = anwDataLen;
       this.b_blind_ans = bBlindAnswe;
       this.w2s_text = text;
    }
    public MMIEnq()
    {
    }
	
    /**
	 * Send Close CAM's MMI enq command to CAM,after this api is called  CIService:getMMIEng() will return NULL object
	 *In this case MMI enq is closed by host rather than by CAM            
	 *            
	 * @see CIService:getMMIEnq
	 */
    public void close()
    {
    	super.close();
    	this.ciservice.setMMIEnq(null);
    	
    }
	
    /**
	 * Notify cam that app has finished to clean up mmi enq,after this api is called  CIService:getMMIEng() will return NULL object
	 * App will do some clean up actions after received CIListener:camMMIClosed()           
	 * @see CIService:getMMIEng
	 * @see CIListener:camMMIClosed
	 */
    public void closeDone()
    {
    	super.close();
    	this.ciservice.setMMIEnq(null);
    	
    }
	
    /**
	 * Send enq answer text to CAM         
	 *            
	 * @param answer: 
	 *answer = 01 means that answer_data contains user's answer text 
	 *answer_item = 00 means user wishes to abort the dialogue
	 * @param answer_data: contains user's answer text which length maybe is 0: 
	 */
    public void answer(boolean answer, String answer_data)
    {
         try {
             ITVRemoteService service = TVManager.getRemoteTvService();
             if (service != null) {
                 service.answerMMIEnq_proxy(this.ciservice.getSlotID(),this.mmi_id,answer,answer_data);
             }
         } catch (RemoteException e) {
             e.printStackTrace();
         }
    }
	
    /**
	 * Query title string of the mmi enq sent by cam       
	 */
     public String getText()
     {
         return w2s_text;
     } 
	
    /**
	 * if the api return 1 will means that the user input has not to be displayed when entered.      
	 *  The application has the choice of the replacement character used(star,...).           
	 *            
	 */
     public boolean isBlindAns()
     {
         return b_blind_ans>0;
     } 
	
    /**
	 * Query CAM's expected length for the answer.return FF means CAM unknown the length.      
	 */
     public byte getAnsTextLen()
     {
         return ans_txt_len;
     } 
    private  byte       ans_txt_len;
    private  byte       b_blind_ans;
    private  String     w2s_text;

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	  public static final Creator<MMIEnq> CREATOR = new Parcelable.Creator<MMIEnq>() {
	        public MMIEnq createFromParcel(Parcel source) {
	            return new MMIEnq(source);
	        }

	        public MMIEnq[] newArray(int size) {
	            return new MMIEnq[size];
	        }
	    };

	    private MMIEnq(Parcel source) {
	        readFromParcel(source);
	    }

	    public void writeToParcel(Parcel out, int flags) {
	    	out.writeInt(mmi_id);
	    	out.writeString(w2s_text);
	        out.writeByte(b_blind_ans);
	        out.writeByte(ans_txt_len);
	    }

	    public void readFromParcel(Parcel in) {
	    	mmi_id = in.readInt();
	    	w2s_text = in.readString();
	     	b_blind_ans = in.readByte();
	    	ans_txt_len = in.readByte();
	    }

	    public String toString() {
	        StringBuilder builder = new StringBuilder();
	        builder.append("MMIEnq [enq id=");
	        builder.append(mmi_id);
	        builder.append(", text=");
	        builder.append(w2s_text);
	        builder.append(", blind answer=");
	        builder.append(b_blind_ans);
	        builder.append(", answer text length=");
	        builder.append(ans_txt_len);
	        builder.append("]");
	        return builder.toString();
	    }	
};
