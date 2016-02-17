package android.net.wifi;

import android.os.Parcelable;
import android.os.Parcel;

/**
 * The current wifi link's BSS info.
 * Current BSS info include BSSID, SSID, network ID, Pairwise 
 * cipher, Group cipher, Key mgmt, the state of WPA, RSSI and
 * Channel number.
 * 
 * @author mtk40456
 *
 */
public class CurrentBssInfo implements Parcelable {
    /** The current linked network name. */
    public String mSSID;
    
    /** The current linked network address of the access point. */
    public String mBSSID;
    
    /**
     * The current linked network's ID.
     */
    public int mNetworkId;
    
    /**
     * The current linked network's pairwise cipher. 
     */
    public String  mPairwiseCipher;
    
    /**
     * The current linked network's group cipher.
     */
    public String mGroupCipher;
    
    /**
     * The current linked network's key management schemes.
     */
    public String mKeyMgmt;
    
    /**
     * The current linked network's state.
     */
    public String mWpaState;
    
    /**
     * The current linked network's signal strength.
     */
    public int mRssi;
    
    /**
     * The current linked network's channel number.
     */
    public int mChannelNumber;
    
    /**
     * The current linked network's freq.
     */
    public String mFreq;
    
    
    CurrentBssInfo() {
        this.mSSID = null;
        this.mBSSID = null;
        this.mNetworkId = -1;
        this.mPairwiseCipher = null;
        this.mGroupCipher = null;
        this.mKeyMgmt = null;
        this.mWpaState = null;
        this.mRssi = 0;
        this.mChannelNumber = 0;
        this.mFreq = null;
    }

    public CurrentBssInfo(String SSID, String BSSID, int networkId, String PairwiseCipher, 
    		String GroupCipher, String KeyMgmt, String WpaState, int rssi, int ChannelNumber,
    		String frequency) {
        this.mSSID = SSID;
        this.mBSSID = BSSID;
        this.mNetworkId = networkId;
        this.mPairwiseCipher = PairwiseCipher;
        this.mGroupCipher = GroupCipher;
        this.mKeyMgmt = KeyMgmt;
        this.mWpaState = WpaState;
        this.mRssi = rssi;
        this.mChannelNumber = ChannelNumber;
        this.mFreq = frequency;
    }

    
    void setSSID(String SSID) {
        mSSID = SSID;
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
    
    void setNetworkId(int networkId) {
    	mNetworkId = networkId;
    }

    /**
     * The ID number that the supplicant uses to identify this
     * network configuration entry. This must be passed as an argument
     * to most calls into the supplicant.
     * @return the network ID that thesupplicant uses to identify currnet network entry.
     */
    public int getNetworkId() {
        return mNetworkId;
    }
    
    void setPairwiseCipher(String PairwiseCipher) {
    	mPairwiseCipher = PairwiseCipher;
    }

    /**
     * The currnet linked network's pairwise ciphers.
     * @return the network's pairwise ciphers.
     */
    public String getPairwiseCipher() {
        return mPairwiseCipher;
    }
    
    void setGroupCipher(String GroupCipher) {
    	mGroupCipher = GroupCipher;
    }

    /**
     * The currnet linked network's group ciphers.
     * @return the network's group ciphers.
     */
    public String getGroupCipher() {
        return mGroupCipher;
    }
    
    
    void setKeyMgmt(String KeyMgmt) {
    	mKeyMgmt = KeyMgmt;
    }

    /**
     * The currnet linked network's key management.
     * @return the network's key management.
     */
    public String getKeyMgmt() {
        return mKeyMgmt;
    }
    
    
    void setWpaState(String WpaState) {
    	mWpaState = WpaState;
    }

    /**
     * The currnet linked network's state.
     * @return the network's state.
     */
    public String getWpaState() {
        return mWpaState;
    }
    
    void setRssi(int rssi) {
        mRssi = rssi;
    }
    
    /**
     * Returns the received signal strength indicator of the current 802.11
     * network.
     * <p><strong>This is not normalized, but should be!</strong></p>
     * @return the RSSI
     */
    public int getRssi() {
        return mRssi;
    }


    void setChannelNumber(int ChannelNumber) {
    	mChannelNumber = ChannelNumber;
    }

    /**
     * The currnet linked network's channel number.
     * @return the network's channel number.
     */
    public int getChannelNumber() {
        return mChannelNumber;
    }
    
    void setFrequency(String frequency) {
    	mFreq = frequency;
    }

    /**
     * The currnet linked network's frequency.
     * @return the network's frequency.
     */
    public String getFrequency() {
        return mFreq;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String none = "<none>";

        sb.append("SSID: ").
            append(mSSID == null ? none : mSSID).
            append(", BSSID: ").
            append(mBSSID == null ? none : mBSSID).
            append(", NetworkId: ").
            append(mNetworkId).
            append(", PairwiseCipher: ").
            append(mPairwiseCipher == null ? none : mPairwiseCipher).
            append(", GroupCipher: ").
            append(mGroupCipher == null ? none : mGroupCipher).
            append(", KeyMgmt: ").
            append(mKeyMgmt == null ? none : mKeyMgmt).
            append(", WpaState: ").
            append(mWpaState == null ? none : mWpaState).
            append(", Rssi: ").
            append(mRssi).
            append(", ChannelNumber: ").
            append(mChannelNumber).
            append(", frequency: ").
            append(mFreq);

        return sb.toString();
    }

    /** Implement the Parcelable interface {@hide} */
    public int describeContents() {
        return 0;
    }

    /** Implement the Parcelable interface {@hide} */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSSID);
        dest.writeString(mBSSID);
        dest.writeInt(mNetworkId);
        dest.writeString(mPairwiseCipher);
        dest.writeString(mGroupCipher);
        dest.writeString(mKeyMgmt);
        dest.writeString(mWpaState);
        dest.writeInt(mRssi);
        dest.writeInt(mChannelNumber);
        dest.writeString(mFreq);
    }

    /** Implement the Parcelable interface {@hide} */
    public static final Creator<CurrentBssInfo> CREATOR =
        new Creator<CurrentBssInfo>() {
            public CurrentBssInfo createFromParcel(Parcel in) {
                return new CurrentBssInfo(
                    in.readString(),
                    in.readString(),
                    in.readInt(),
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readInt(),
                    in.readInt(),
                    in.readString()
                );
            }

            public CurrentBssInfo[] newArray(int size) {
                return new CurrentBssInfo[size];
            }
        };

	
	
}
