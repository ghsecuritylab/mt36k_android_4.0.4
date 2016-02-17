/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.net.wifi;

import android.os.Parcelable;
import android.os.Parcel;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkUtils;

import java.net.InetAddress;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.EnumMap;

/**
 * Describes the state of any Wifi connection that is active or
 * is in the process of being set up.
 */
public class WifiInfo implements Parcelable {
    /**
     * This is the map described in the Javadoc comment above. The positions
     * of the elements of the array must correspond to the ordinal values
     * of <code>DetailedState</code>.
     */
    private static final EnumMap<SupplicantState, DetailedState> stateMap =
        new EnumMap<SupplicantState, DetailedState>(SupplicantState.class);

    static {
        stateMap.put(SupplicantState.DISCONNECTED, DetailedState.DISCONNECTED);
        stateMap.put(SupplicantState.INTERFACE_DISABLED, DetailedState.DISCONNECTED);
        stateMap.put(SupplicantState.INACTIVE, DetailedState.IDLE);
        stateMap.put(SupplicantState.SCANNING, DetailedState.SCANNING);
        stateMap.put(SupplicantState.AUTHENTICATING, DetailedState.CONNECTING);
        stateMap.put(SupplicantState.ASSOCIATING, DetailedState.CONNECTING);
        stateMap.put(SupplicantState.ASSOCIATED, DetailedState.CONNECTING);
        stateMap.put(SupplicantState.FOUR_WAY_HANDSHAKE, DetailedState.AUTHENTICATING);
        stateMap.put(SupplicantState.GROUP_HANDSHAKE, DetailedState.AUTHENTICATING);
        stateMap.put(SupplicantState.COMPLETED, DetailedState.OBTAINING_IPADDR);
        stateMap.put(SupplicantState.DORMANT, DetailedState.DISCONNECTED);
        stateMap.put(SupplicantState.UNINITIALIZED, DetailedState.IDLE);
        stateMap.put(SupplicantState.INVALID, DetailedState.FAILED);
    }

   	// frequency Table
	public static  int[] FREQUENCY[] = {
		//LOWER  CENTER UPPER  (MHZ)
		{0, 	0, 		0}, // CHANNAL NUMBER
		{2401,	2412,	2423}, //1
		{2404,	2417,	2428}, //2
		{2411,	2422,	2433},//3
		{2416,	2427,	2438},//4
		{2421,	2432,	2443},//5
		{2426,	2437,	2448},//6
		{2431,	2442,	2453},//7
		{2436,	2447,	2458},//8
		{2441,	2452,	2463},//9
		{2451,	2457,	2468},//10
		{2451,	2462,	2473},//11
		{2456,	2467,	2478},//12
		{2461,	2472,	2483},//13
		{2473,	2484,	2495},//14		
	};
    private SupplicantState mSupplicantState;
    private String mBSSID;
    private String mSSID;
    private int mNetworkId;
    private boolean mHiddenSSID;
    /** Received Signal Strength Indicator */
    private int mRssi;

    /** Link speed in Mbps */
    public static final String LINK_SPEED_UNITS = "Mbps";
    private int mLinkSpeed;

    private InetAddress mIpAddress;

    private String mMacAddress;
    private boolean mExplicitConnect;

	 //public String  mPairwiseCipher;        
//	 public String mGroupCipher;       
//	 public String mKeyMgmt;        
//	 public String mWpaState; 
	 public int mChannelNumber;        
	 public String mFreq;
	 public String mProtocol_caps;
    WifiInfo() {
        mSSID = null;
        mBSSID = null;
        mNetworkId = -1;
        mSupplicantState = SupplicantState.UNINITIALIZED;
        mRssi = -9999;
        mLinkSpeed = -1;
        mHiddenSSID = false;
        mExplicitConnect = false;
//		mPairwiseCipher = null;        
//		mGroupCipher = null;       
//		mKeyMgmt = null;        
//		mWpaState = null;
		mChannelNumber = 0;        
		mFreq = null;
		mProtocol_caps=null;
    }

    /**
     * Copy constructor
     * @hide
     */
    public WifiInfo(WifiInfo source) {
        if (source != null) {
            mSupplicantState = source.mSupplicantState;
            mBSSID = source.mBSSID;
            mSSID = source.mSSID;
            mNetworkId = source.mNetworkId;
            mHiddenSSID = source.mHiddenSSID;
            mRssi = source.mRssi;
            mLinkSpeed = source.mLinkSpeed;
            mIpAddress = source.mIpAddress;
            mMacAddress = source.mMacAddress;
            mExplicitConnect = source.mExplicitConnect;
//			mPairwiseCipher = source.mPairwiseCipher;        
//		    mGroupCipher = source.mGroupCipher;       
//			mKeyMgmt = source.mKeyMgmt;        
//			mWpaState = source.mWpaState;
			mChannelNumber = source.mChannelNumber;        
			mFreq = source.mFreq;
			mProtocol_caps=source.mProtocol_caps;
        }
    }

    void setSSID(String SSID) {
        mSSID = SSID;
        // network is considered not hidden by default
        mHiddenSSID = false;
    }

    /**
     * Returns the service set identifier (SSID) of the current 802.11 network.
     * If the SSID is an ASCII string, it will be returned surrounded by double
     * quotation marks.Otherwise, it is returned as a string of hex digits. The
     * SSID may be {@code null} if there is no network currently connected.
     * @return the SSID
     */
    public String getSSID() {
        return mSSID;
    }

    void setBSSID(String BSSID) {
        mBSSID = BSSID;
    }

    /**
     * Return the basic service set identifier (BSSID) of the current access point.
     * The BSSID may be {@code null} if there is no network currently connected.
     * @return the BSSID, in the form of a six-byte MAC address: {@code XX:XX:XX:XX:XX:XX}
     */
    public String getBSSID() {
        return mBSSID;
    }

    /**
     * Returns the received signal strength indicator of the current 802.11
     * network.
     * <p><strong>This is not normalized, but should be!</strong></p>
     * @return the RSSI, in the range ??? to ???
     */
    public int getRssi() {
        return mRssi;
    }

    void setRssi(int rssi) {
        mRssi = rssi;
    }

    /**
     * Returns the current link speed in {@link #LINK_SPEED_UNITS}.
     * @return the link speed.
     * @see #LINK_SPEED_UNITS
     */
    public int getLinkSpeed() {
        return mLinkSpeed;
    }

    void setLinkSpeed(int linkSpeed) {
        this.mLinkSpeed = linkSpeed;
    }

    /**
     * Record the MAC address of the WLAN interface
     * @param macAddress the MAC address in {@code XX:XX:XX:XX:XX:XX} form
     */
    void setMacAddress(String macAddress) {
        this.mMacAddress = macAddress;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    void setNetworkId(int id) {
        mNetworkId = id;
    }


    /**
     * @hide
     */
    public boolean isExplicitConnect() {
        return mExplicitConnect;
    }

    /**
     * @hide
     */
    public void setExplicitConnect(boolean explicitConnect) {
        this.mExplicitConnect = explicitConnect;
    }


    /**
     * Each configured network has a unique small integer ID, used to identify
     * the network when performing operations on the supplicant. This method
     * returns the ID for the currently connected network.
     * @return the network ID, or -1 if there is no currently connected network
     */
    public int getNetworkId() {
        return mNetworkId;
    }

    /**
     * Return the detailed state of the supplicant's negotiation with an
     * access point, in the form of a {@link SupplicantState SupplicantState} object.
     * @return the current {@link SupplicantState SupplicantState}
     */
    public SupplicantState getSupplicantState() {
        return mSupplicantState;
    }

    void setSupplicantState(SupplicantState state) {
        mSupplicantState = state;
    }

    void setInetAddress(InetAddress address) {
        mIpAddress = address;
    }

    public int getIpAddress() {
        if (mIpAddress == null || mIpAddress instanceof Inet6Address) return 0;
        return NetworkUtils.inetAddressToInt(mIpAddress);
    }

    /**
     * @return {@code true} if this network does not broadcast its SSID, so an
     * SSID-specific probe request must be used for scans.
     */
    public boolean getHiddenSSID() {
        return mHiddenSSID;
    }

    /** {@hide} */
    public void setHiddenSSID(boolean hiddenSSID) {
        mHiddenSSID = hiddenSSID;
    }

   /**
     * Map a supplicant state into a fine-grained network connectivity state.
     * @param suppState the supplicant state
     * @return the corresponding {@link DetailedState}
     */
    public static DetailedState getDetailedStateOf(SupplicantState suppState) {
        return stateMap.get(suppState);
    }

    /**
     * Set the <code>SupplicantState</code> from the string name
     * of the state.
     * @param stateName the name of the state, as a <code>String</code> returned
     * in an event sent by {@code wpa_supplicant}.
     */
    void setSupplicantState(String stateName) {
        mSupplicantState = valueOf(stateName);
    }

    static SupplicantState valueOf(String stateName) {
        if ("4WAY_HANDSHAKE".equalsIgnoreCase(stateName))
            return SupplicantState.FOUR_WAY_HANDSHAKE;
        else {
            try {
                return SupplicantState.valueOf(stateName.toUpperCase());
            } catch (IllegalArgumentException e) {
                return SupplicantState.INVALID;
            }
        }
    }
/*	 public String getPairwiseCipher() 
	 {        
	        return mPairwiseCipher;    
	 }        
	 void setGroupCipher(String GroupCipher) {    	
	 	mGroupCipher = GroupCipher;    
		}   
	 public String getGroupCipher() {        
	 return mGroupCipher;    
	 }            
	 void setKeyMgmt(String KeyMgmt) {    	
	 	mKeyMgmt = KeyMgmt;    
		}    
	 public String getKeyMgmt() {        
	 return mKeyMgmt;    
	 }            
	 void setWpaState(String WpaState) {    	
	 	mWpaState = WpaState;    
		}    
	 public String getWpaState() {        
	 return mWpaState;    
	 }*/


     public void setChannelNumber(int ChannelNumber) {    
	 	mChannelNumber = ChannelNumber;    
	 }   
	 public int getChannelNumber(String frequency) {        

         if(null == frequency)
		 	return -1;
		for(int i=0;i<15;i++)
			for(int j=0;j<3;j++)
				{
				   if(Integer.parseInt(frequency) == (FREQUENCY[i][j]))
				   	return i;
				}
			return -1;    
	 }  

		 void setFrequency(String frequency) {    	
		 	mFreq = frequency;    
			}
		public String getFrequency() {        
	        return mFreq;
		}
	 public void setProtocol_caps(String Protocol_caps) {    
	 	mProtocol_caps = Protocol_caps;    
	 }   
	 public String getProtocol_caps() {        
		return mProtocol_caps;    
	 } 
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String none = "<none>";

        sb.append("SSID: ").append(mSSID == null ? none : mSSID).
            append(", BSSID: ").append(mBSSID == null ? none : mBSSID).
            append(", MAC: ").append(mMacAddress == null ? none : mMacAddress).
            append(", Supplicant state: ").
            append(mSupplicantState == null ? none : mSupplicantState).
            append(", RSSI: ").append(mRssi).
            append(", Link speed: ").append(mLinkSpeed).
            append(", Net ID: ").append(mNetworkId).
            append(", Explicit connect: ").append(mExplicitConnect).
//		    append(", PairwiseCipher: ").append(mPairwiseCipher == null ? none : mPairwiseCipher).           
//			append(", GroupCipher: ").append(mGroupCipher == null ? none : mGroupCipher).            
//			append(", KeyMgmt: ").append(mKeyMgmt == null ? none : mKeyMgmt).            
//			append(", WpaState: ").append(mWpaState == null ? none : mWpaState).
			append(", ChannelNumber: ").append(mChannelNumber).            
			append(", frequency: ").append(mFreq).
			append(", Protocol_caps: ").append(mProtocol_caps);

        return sb.toString();
    }

    /** Implement the Parcelable interface {@hide} */
    public int describeContents() {
        return 0;
    }

    /** Implement the Parcelable interface {@hide} */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mNetworkId);
        dest.writeInt(mRssi);
        dest.writeInt(mLinkSpeed);
        if (mIpAddress != null) {
            dest.writeByte((byte)1);
            dest.writeByteArray(mIpAddress.getAddress());
        } else {
            dest.writeByte((byte)0);
        }
        dest.writeString(getSSID());
        dest.writeString(mBSSID);
        dest.writeString(mMacAddress);
        dest.writeByte(mExplicitConnect ? (byte)1 : (byte)0);
/*		dest.writeString(mPairwiseCipher);        
		dest.writeString(mGroupCipher);        
		dest.writeString(mKeyMgmt);        
		dest.writeString(mWpaState);*/
		dest.writeInt(mChannelNumber);        
		dest.writeString(mFreq);
		dest.writeString(mProtocol_caps);
        mSupplicantState.writeToParcel(dest, flags);
    }

    /** Implement the Parcelable interface {@hide} */
    public static final Creator<WifiInfo> CREATOR =
        new Creator<WifiInfo>() {
            public WifiInfo createFromParcel(Parcel in) {
                WifiInfo info = new WifiInfo();
                info.setNetworkId(in.readInt());
                info.setRssi(in.readInt());
                info.setLinkSpeed(in.readInt());
                if (in.readByte() == 1) {
                    try {
                        info.setInetAddress(InetAddress.getByAddress(in.createByteArray()));
                    } catch (UnknownHostException e) {}
                }
                info.setSSID(in.readString());
                info.mBSSID = in.readString();
                info.mMacAddress = in.readString();
                info.mExplicitConnect = in.readByte() == 1 ? true : false;
/*				info.mPairwiseCipher = in.readString();
				info.mGroupCipher = in.readString();
				info.mKeyMgmt = in.readString();
				info.mWpaState = in.readString();*/
				info.mChannelNumber = in.readInt();
				info.mFreq = in.readString();
				info.mProtocol_caps = in.readString();
                info.mSupplicantState = SupplicantState.CREATOR.createFromParcel(in);
                return info;
            }

            public WifiInfo[] newArray(int size) {
                return new WifiInfo[size];
            }
        };
}
