    /*
 * Copyright (C) 2010 MediaTek USA, Inc
 *
 */

package com.mediatek.tv.model;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.service.CIService;
import com.mediatek.tv.service.ITVRemoteService;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

public class HostControlTune extends HostControlResource {
    private static final String TAG = "HostControlTune";
           
   public HostControlTune(int networkId,int origNetworkId,int tsId,int svcId)
   {
	   network_id = networkId;
	   orig_network_id = origNetworkId;
	   ts_id = tsId;
	   service_id = svcId;
	   tunedChannel = null;
   }
   
   /**
	  * Query the ChannelInfo object indicates the channel which application need tuned to
	  * 
	  * @param curChannel: current played channel object
	  * 		   
	  * @return ChannelInfo object
	  */
   public  ChannelInfo getTunedChannel(ChannelInfo curChannel)
   {
	  System.out.println("[ci]invoke the tuneHost function" ); 	  
	  int ret = 0;
	  if(curChannel == null)
	  {
         return null;
	  }
      try {
         ITVRemoteService service = TVManager.getRemoteTvService();
         if (service != null) {
        	 ret = service.getTunedChannel_proxy(curChannel.svlId,this);
         }
     } catch (RemoteException e) {
         e.printStackTrace();
     }
   	  return this.tunedChannel;
   }
   /**
	  * restore to previous played channel
	  * 
	  */
   public int restoreHost()
   {
   	  return 0;
   }
    public void setTunedChannel(ChannelInfo tunedChannel)
    {
        System.out.println("[ci]setTunedChannel " + tunedChannel.toString());
        CIService.getInstance(0).getHostControlTune().tunedChannel = tunedChannel;
    	this.tunedChannel = tunedChannel;
    }
    public int getNetworkId()
    {   
		System.out.println("[ci]invoke the getNetworkId function" );	
        return network_id;
    }
    public int getOrigNetworkId()
    {
		System.out.println("[ci]invoke the getOrigNetworkId function" );	
        return orig_network_id;
    }
    public int getTSId()
    {
		System.out.println("[ci]invoke the getTSId function" );	
        return ts_id;
    }
    public int getSvcId()
    {
		System.out.println("[ci]invoke the getSvcId function" );	
        return service_id;
    }
    private  int   network_id;
    private  int   orig_network_id;
    private  int   ts_id;
    private  int   service_id;
    private  ChannelInfo tunedChannel;
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	  public static final Creator<HostControlTune> CREATOR = new Parcelable.Creator<HostControlTune>() {
	        public HostControlTune createFromParcel(Parcel source) {
	            return new HostControlTune(source);
	        }

	        public HostControlTune[] newArray(int size) {
	            return new HostControlTune[size];
	        }
	    };

	    private HostControlTune(Parcel source) {
	        readFromParcel(source);
	    }

	    public void writeToParcel(Parcel out, int flags) {
	        out.writeInt(network_id);
	        out.writeInt(orig_network_id);
	        out.writeInt(ts_id);
	        out.writeInt(service_id);
	    }

	    public void readFromParcel(Parcel in) {
	    	network_id = in.readInt();
	    	orig_network_id = in.readInt();
	    	ts_id = in.readInt();
	    	service_id = in.readInt();
	    }

	    public String toString() {
	        StringBuilder builder = new StringBuilder();
	        builder.append("HostControlTune [network id=");
	        builder.append(network_id);
	        builder.append(", orignal network id=");
	        builder.append(orig_network_id);
	        builder.append(", ts id=");
	        builder.append(ts_id);
	        builder.append(", service id=");
	        builder.append(service_id);	 
	        builder.append("]");
	        return builder.toString();
	    }
 };

