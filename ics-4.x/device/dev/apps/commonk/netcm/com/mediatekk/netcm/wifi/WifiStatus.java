package com.mediatekk.netcm.wifi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

import com.mediatekk.netcm.util.NetLog;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.CurrentBssInfo;
import android.text.format.Time;


/**
 * This class use to get wifi Status, provide get related information capabilites.
 * <ul>
 * <li> IP, Net Mask, DNS, MAC.</li>
 * <li> Channel, Frequency, Confirm Type.</li>
 * </ul>
 */
public class WifiStatus {
	private WifiManager mWifiManager;
	private static WifiStatus mWifiStatus = null;
	private Context mContext = null;
	private WifiInfo mWifiInfo = null;
	private CurrentBssInfo mCurrentBssInfo = null;
	
	private static final String STR_INTERFACE_ATH0 = "ath0";
	private static final String STR_INTERFACE_REA0 = "rea0";
	private static final String STR_INTERFACE_WLAN0 = "wlan0";
	
	
	private String TAG = "CM_WifiStatus";
	public static String TYPE = "Base Architecture";
	public static int frequency;
	// /sys/module/ar6000/version 
	private static final String mNdisPathPrefix = "/sys/module";
	private static final String mNdisFile = "version";
	
	private WifiStatus(Context context) {
		mContext = context;
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	}
	
	private WifiStatus(Context context, String text){
		
	}
	
	/**
	 * Create a new WifiStatus instance.
	 * Applications will use for getting current link status.
	 * 
	 */
	public static WifiStatus getInstance(Context context) {
		if(WifiConst.DummyMode){
			if(mWifiStatus == null) {
				mWifiStatus = new WifiStatus(context, null);
			}
			
			return mWifiStatus;
		}
		
		if(mWifiStatus == null) {
			mWifiStatus = new WifiStatus(context);
		}
		
		return mWifiStatus;
	}
	
	/**
	 * Check if the wifi dongle is connect to access point or not.
	 * 
	 * @return true if wifi connect to access point, otherwise false.
	 */
	public boolean isWifiConnected() {
		mWifiInfo = mWifiManager.getConnectionInfo();
		SupplicantState state = mWifiInfo.getSupplicantState();
		
		if(state == SupplicantState.COMPLETED || state == SupplicantState.ASSOCIATED || 
				state == SupplicantState.ASSOCIATING) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get current connect's channel number.
	 * 
	 * @return the channel number.
	 */
	public int getChannal(){
		if(WifiConst.DummyMode){
			return 0;
		}
		
//		if (localLOGV)  Log.d(TAG, "[WifiStatus][getChannal]: channal -> " + WifiUtil.getChannelNumber(frequency));
//		return WifiUtil.getChannelNumber(frequency);
		mCurrentBssInfo = mWifiManager.getCurrentBssInfo();
		if(mCurrentBssInfo == null) {
			return 0;
		}
		
		return mCurrentBssInfo.getChannelNumber();
	}
	
	/**
	 * Get the wifi status.
	 * {@hide} Do not use this API, use getConnectStatus() instead.
	 * @return true if wifi dongle connect to access point, else return false.
	 */
	public boolean getWifiStatus() {
		//return mWifiManager.isWifiEnabled();
		if(WifiConst.DummyMode){
			return false;
		}
		
		return getIp() == 0 ? false : true;
	}
	
	/**
	 * Get current link's speed.
	 * 
	 * @return the link's speed.
	 */
	public int getLinkSpeed() {
		if(WifiConst.DummyMode){
			return 0;
		}
		
		mWifiInfo = mWifiManager.getConnectionInfo();
	
		return (mWifiInfo == null) ? 0 : mWifiInfo.getLinkSpeed();
	}
	
	/**
	 * Get current link's base architecture.
	 * 
	 * @return the link's base architecture.
	 */
	public String getType() {
		return TYPE;
	}
	
	/**
	 * Gets the type of current link's encryption.
	 * 
	 * @return the type of current link's encryption.
	 */
	public int getEncryptType(){
		if(WifiConst.DummyMode){
			return WifiConst.W_ENCRYPT_NONE;
		}
		
		mCurrentBssInfo = mWifiManager.getCurrentBssInfo();
		if(mCurrentBssInfo == null) {
			return WifiConst.W_ENCRYPT_NONE;
		}
		
		String pairWiseCipher =  mCurrentBssInfo.getPairwiseCipher();
		if(pairWiseCipher == null) {
			return WifiConst.W_ENCRYPT_NONE;
		}
		
		if(pairWiseCipher.equals("TKIP+CCMP")) {
			return WifiConst.W_ENCRYPT_TKIP_AES;
		} else if(pairWiseCipher.equals("WEP")) {
			return WifiConst.W_ENCRYPT_WEP;
		} else if (pairWiseCipher.equals("TKIP")) {
			return WifiConst.W_ENCRYPT_TKIP;
		} else if (pairWiseCipher.equals("CCMP")) {
			return WifiConst.W_ENCRYPT_AES;
		} 
		
		return WifiConst.W_ENCRYPT_NONE;
	}
	
	/**
	 * Gets the current link's ssid.
	 * 
	 * @return the current link's ssid.
	 */
	public String getSsid(){
		if(WifiConst.DummyMode){
			return null;
		}
		
		mCurrentBssInfo = mWifiManager.getCurrentBssInfo();
		return (mCurrentBssInfo == null) ? null : mCurrentBssInfo.getSSID();
	}
	
	
	/**
	 * Gets the version of NDIS.
	 * 
	 * @return the version of NDIS.
	 */
	/*public String getNdisVersion() {
		
		File file = null;
		BufferedReader bufferedReader = null;
		String dongleName = null;
		String result = null;
		FileReader filereader = null;
		
		int wifi0 = mWifiManager.wifi0Check();
		switch (wifi0) {
		case 0:
			dongleName = STR_INTERFACE_ATH0;
			break;
		case 1:
			dongleName = STR_INTERFACE_REA0;
			break;
		case 2:
			dongleName = STR_INTERFACE_WLAN0;
			break;

		default:
			return null;
		}
		
		String dirverName = WifiUtil.getDriverName(dongleName);
		if(dirverName == null) {
			return null;
		}

		
	    String NdisFilePath =  mNdisPathPrefix.concat("/").concat(dirverName).concat("/").concat(mNdisFile);
	    NetLog.d(TAG, "[WifiStatus][getNdisVersion]: NdisFilePath ->  " + NdisFilePath);
	    try {
	    	file = new File(NdisFilePath);
			if(!file.exists()) {
				NetLog.d(TAG, "[WifiStatus][getNdisVersion]: file does not exist! ");
				return null;
			}
			
            filereader= new FileReader(file);
			bufferedReader = new BufferedReader(filereader);
			
			String line = bufferedReader.readLine();
			if(line == null) {
				result =  null;
			}
			
			NetLog.d(TAG, "[WifiStatus][getNdisVersion]: line -- >  " + line);
			result = line;
			
	    } catch(IOException e) {
	    	result = null;
	    } finally {
			bufferedReader.close();
		}
		
		
		return result;
	}
	*/
	/**
	 * Gets the type of current link's confirm method.
	 * 
	 * @return the type of current link's confirm method.
	 */
	public int getConfirmType(){
		if(WifiConst.DummyMode){
			return WifiConst.W_CONFIRM_UNKNOWN;
		}
		
		mCurrentBssInfo = mWifiManager.getCurrentBssInfo();
		if(mCurrentBssInfo == null) {
			return WifiConst.W_CONFIRM_UNKNOWN;
		}
		
		String Keymgmt = mCurrentBssInfo.getKeyMgmt();
		NetLog.d(TAG, "[WifiStatus][getConfirmType]: Keymgmt -- >  " + Keymgmt);
		if(Keymgmt == null || Keymgmt.equals("WPS")) {
			return WifiConst.W_CONFIRM_OPEN;
		} else if(Keymgmt.contains("WEP")) {
			return WifiConst.W_CONFIRM_WEP;
		} else if(Keymgmt.contains("WPA-PSK") && Keymgmt.contains("WPA2-PSK")) {
			return WifiConst.W_CONFIRM_PSK_AUTO;
		} else if (Keymgmt.contains("WPA") && Keymgmt.contains("WPA-PSK")) {
			return WifiConst.W_CONFIRM_WPA_PSK;
		} else if(Keymgmt.contains("WPA2") && Keymgmt.contains("WPA2-PSK")) {
			return WifiConst.W_CONFIRM_WPA2_PSK;
		} else if(Keymgmt.contains("WPA-EAP") && Keymgmt.contains("WPA2-EAP")) {
			return WifiConst.W_CONFIRM_EAP_AUTO;
		} else if(Keymgmt.contains("WPA") && Keymgmt.contains("WPA-EAP")) {
			return WifiConst.W_CONFIRM_WPA_EAP;
		} else if(Keymgmt.contains("WPA2") && Keymgmt.contains("WPA2-EAP")) {
			return WifiConst.W_CONFIRM_WPA2_EAP;
		}

		return WifiConst.W_CONFIRM_UNKNOWN;
	}
	
	/**
	 * Gets the type of current link's channel.
	 * 
	 * @return the type of current link's channel.
	 */
	public String getChannelSetting() {
		if(WifiConst.DummyMode){
			return "ETSI";
		}
		
//		int number = mWifiManager.getNumAllowedChannels();
//
//		/* North America */
//		if (number == 11){
//			return "FCC";
//		}
//		
//		/* Europe */ 
//		else if (number == 13){
//			return "ETSI";
//		}
		
		return "ETSI";
	}
	
	/**
	 * Get the type of current link's frequency.
	 * 
	 * @return the type of current link's frequency.
	 */
	public int getFrequency() {
		mCurrentBssInfo = mWifiManager.getCurrentBssInfo();
		if(mCurrentBssInfo == null) {
			return 0;
		}
		
		int number =  mCurrentBssInfo.getChannelNumber();
		
		
		return WifiUtil.getFrequency(number);
	}
	
	/**
	 * Get the time of the current link.
	 * 
	 * @return the time of the current link.
	 */
/*	public String getWorkTime() {
		if(WifiConst.DummyMode){
			return "00:00:00";
		}
		
		if (!WifiUtil.startTimer){
			return "00:00:00";
		}
		
		NetLog.d(TAG, "[WifiStatus][getWorkTime]: Get work time." );
		
		Time tm = new Time();
		tm.setToNow();
		int hour = (tm.yearDay - WifiUtil.yearDay)* 24;
		hour = hour + tm.hour - WifiUtil.hour;
		hour -= 1;
		int minute = tm.minute - WifiUtil.minute + 60;
		minute -= 1;
		int second = tm.second - WifiUtil.second + 60;

		if (second >= 60){
			second -= 60;
			minute += 1;
		}
		if (minute >= 60){
			minute -= 60;
			hour += 1;
		}
		
		StringBuffer hourBuffer = null;
		StringBuffer minuteBuffer = null;
		StringBuffer secondBuffer = null;
		
			
		if (hour < 10) {
			hourBuffer = new StringBuffer().append("0").append(hour);
		}
		if (minute < 10) {
			minuteBuffer = new StringBuffer().append("0").append(minute);
		}
		if (second < 10) {
			secondBuffer = new StringBuffer().append("0").append(second);
		}

		return new StringBuffer().append(hourBuffer == null ? hour: hourBuffer).
			append(":").append(minuteBuffer == null ? minute : minuteBuffer).append(":").
			append(secondBuffer == null ? second : secondBuffer).toString();
	}
*/
	
	/**
	 * Get the level of current link's signal.
	 * 
	 * @return the level of current link's signal.
	 */
	public int getSignalLevel(){
		if(WifiConst.DummyMode){
			return -1;
		}
		
//		mWifiInfo = mWifiManager.getConnectionInfo();
//		if(mWifiInfo == null) {
//			return -1;
//		}
//		
//		int rssi = mWifiInfo.getRssi();
//		if(rssi == Integer.MAX_VALUE) {
//			return -1;
//		}
//		
//		return WifiManager.calculateSignalLevel(rssi, 4);
		
		mCurrentBssInfo = mWifiManager.getCurrentBssInfo();
		if(mCurrentBssInfo == null) {
			return 0;
		}
		
		int rssi = mCurrentBssInfo.getRssi();
		return rssi;
	}
	
	/**
	 * Get the quality of current link's signal.
	 * 
	 * @return the quality of current link's signal.
	 */
	public int getLinkQuality(){
		if(WifiConst.DummyMode){
			return 0;
		}
		
		mCurrentBssInfo = mWifiManager.getCurrentBssInfo();
		if(mCurrentBssInfo == null) {
			return 0;
		}
		
		int rssi = mCurrentBssInfo.getRssi();
		return rssi;
	}
	
	
	/**
	 * Get the current link's MAC address.
	 * 
	 * @return the current link's MAC address.
	 */
	public String getMacAddr(){
		if(WifiConst.DummyMode){
			return null;
		}
				
		mWifiInfo = mWifiManager.getConnectionInfo();
		if(mWifiInfo == null) {
			return null;
		}
		 
		return mWifiInfo.getMacAddress();
	}
	
	public String getBssid() {
		mCurrentBssInfo = mWifiManager.getCurrentBssInfo();
		if(mCurrentBssInfo == null) {
			return null;
		}
		
		return mCurrentBssInfo.getBSSID();
	}
	
	/**
	 * Get the current link's IP address.
	 * 
	 * @return the current link's IP address.
	 */
	public String getIpAddr(){
		if(WifiConst.DummyMode){
			return null;
		}
		
		
		mWifiInfo = mWifiManager.getConnectionInfo();
		if(mWifiInfo == null) {
			return null;
		}
		
		return WifiUtil.intToString(mWifiInfo.getIpAddress());
	}
	
	private int getIp(){
		if(WifiConst.DummyMode){
			return 0;
		}
		
		mWifiInfo = mWifiManager.getConnectionInfo();
		if(mWifiInfo == null) {
			return 0;
		}

		return mWifiInfo.getIpAddress();
	}
	
	
	/**
	 * Get the wifi status.
	 * @return true if wifi dongle connect to access point, else return false.
	 */
	public boolean getConnectStatus(){
		if(WifiConst.DummyMode){
			return false;
		}
		
		mWifiInfo = mWifiManager.getConnectionInfo();
		if(mWifiInfo == null) {
			return false;
		}
		return mWifiInfo.getIpAddress() == 0 ? false : true;
	}
	
	/**
	 * Get the current link's net mask address.
	 * 
	 * @return the current link's net mask address.
	 */
	public String getNetMask(){
		if(WifiConst.DummyMode){
			return null;
		}
		
		DhcpInfo info = mWifiManager.getDhcpInfo();
		if(info == null) {
			return null;
		}
		
		return WifiUtil.intToString(info.netmask);
	}
	
	/**
	 * Get the current link's DNS address.
	 * 
	 * @return the current link's DNS address.
	 */
	public String getDnsAddr(){
		if(WifiConst.DummyMode){
			return null;
		}

		DhcpInfo info = mWifiManager.getDhcpInfo();
		if(info == null) {
			return null;
		}
		
		return WifiUtil.intToString(info.dns1);
	}
	
	/**
	 * Get the current link's alternate DNS address.
	 * 
	 * @return the current link's alternate DNS address.
	 */
	public String getDns2Addr(){
		if(WifiConst.DummyMode){
			return null;
		}
		
		DhcpInfo info = mWifiManager.getDhcpInfo();
		if(info == null) {
			return null;
		}

		return WifiUtil.intToString(info.dns2);
	}
	/**
	 * Get the current link's get way address.
	 * 
	 * @return the current link's get way address.
	 */
	public  String getRouteAddr(){
		if(WifiConst.DummyMode){
			return null;
		}
		
		DhcpInfo info = mWifiManager.getDhcpInfo();
		if(info == null) {
			return null;
		}
		
		return WifiUtil.intToString(info.gateway);
		
	}
}
