    /*
 * Copyright (C) 2010 MediaTek USA, Inc
 *
 */

package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;


public class HostControlReplace extends HostControlResource {
    private static final String TAG = "HostControlReplace";
                
    public HostControlReplace(byte refId,int replaceId,int replacementId)
    {
        this.refId = refId;
        replaced_pid = replaceId;
        replacement_pid = replacementId;
    }    
	
	/**
	   * Query replacement ref value which is used to match a clear replace object
	   */
    public int getRefId()
    {
    	return refId;
    }  
	
	/**
	   * Query replaced pid which mabe video,audio,teletext or subtitles be replaced by the component
	   * being transmitted in the replacement pid.The replacement occurs immediately.
	   * 
	   *			
	   * @return replaced pid
	   */
    public int getReplaceedPID()
    {
    	return this.replaced_pid;
    }
	
	/**
	   * Query replacement pid which mabe video,audio,teletext or subtitles this pid will replace with the pid returned from
	   * getReplaceedPID()
	   *			
	   * @return replacement  pid
	   */
    public int getReplacementPID()
    {
    	return this.replacement_pid;
    }
	
	/**
	   * restore to previous played pid
	   * 
	   */
    public int restoreHost()
    {
    	return 0;
    }
    private  byte    refId;
    private  int     replaced_pid;
    private  int     replacement_pid;

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	 public static final Creator<HostControlReplace> CREATOR = new Parcelable.Creator<HostControlReplace>() {
	        public HostControlReplace createFromParcel(Parcel source) {
	            return new HostControlReplace(source);
	        }

	        public HostControlReplace[] newArray(int size) {
	            return new HostControlReplace[size];
	        }
	    };

	    private HostControlReplace(Parcel source) {
	        readFromParcel(source);
	    }

	    public void writeToParcel(Parcel out, int flags) {
	        out.writeByte(refId);
            out.writeInt(replaced_pid);
	        out.writeInt(replacement_pid);
	    }

	    public void readFromParcel(Parcel in) {
	    	refId =   in.readByte();
	    	replaced_pid = in.readInt();
	    	replacement_pid = in.readInt();
	    }

	    public String toString() {
	        StringBuilder builder = new StringBuilder();
	        builder.append("HostControlReplace [reference id=");
	        builder.append(refId);
	        builder.append(", replaced pid=");
	        builder.append(replaced_pid);
	        builder.append(", replacement pid=");
	        builder.append(replacement_pid);
	        builder.append("]");
	        return builder.toString();
	    }	
 
};

