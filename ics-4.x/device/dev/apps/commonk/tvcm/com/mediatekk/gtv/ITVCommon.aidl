package com.mediatekk.gtv;
import com.mediatekk.gtv.ITVCommonScanListener;
import com.mediatekk.gtv.ITVOption;

 
interface ITVCommon {
	void scan(ITVCommonScanListener listener);
	void pushStop();
	void popStop();
	void enterTV();
	void leaveTV();
	void sendPowerOff();
	boolean setOpacity(int opacity);
	boolean setColorKey(boolean b_enabled, int color);
	
	//declare TVChannelSelector methods
	void channelDown();
	void channelUp();
	boolean hasSignal();
	boolean hasScramble();
	void select(int num);
	
	//declare TVInputManager methods
	void changeInputSource(String outputName, String inputName);
	String[] getInputSourceArray();
	String getCurrInputSource(String outputName);
	String getTypeFromInputSource(String inputSourceName);
	
    int getChannelsLength();
    int getCurrentChannelNum();
    String getCurrentChannelName();
    	
	ITVOption getOption(String name);
}
