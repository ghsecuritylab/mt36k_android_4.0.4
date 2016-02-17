/*
 * Copyright (C) 2006 The Android Open Source Project
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

package com.android.server;

import com.android.internal.app.IBatteryStats;
import com.android.server.am.BatteryStatsService;

import android.app.ActivityManagerNative;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.FileUtils;
import android.os.IBinder;
import android.os.DropBoxManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UEventObserver;
import android.provider.Settings;
import android.util.EventLog;
import android.util.Slog;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

// add by zhanghangzhi 
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.os.RemoteException;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.common.ConfigValue;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.UARTSerialListener;

import android.os.SystemProperties;
import android.util.Slog;

import com.mediatek.tv.service.ConfigService;
import com.mediatek.tv.TVManager;

import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.io.FileWriter;
import java.io.FileReader;

// end


/**
 * <p>BatteryService monitors the charging status, and charge level of the device
 * battery.  When these values change this service broadcasts the new values
 * to all {@link android.content.BroadcastReceiver IntentReceivers} that are
 * watching the {@link android.content.Intent#ACTION_BATTERY_CHANGED
 * BATTERY_CHANGED} action.</p>
 * <p>The new values are stored in the Intent data and can be retrieved by
 * calling {@link android.content.Intent#getExtra Intent.getExtra} with the
 * following keys:</p>
 * <p>&quot;scale&quot; - int, the maximum value for the charge level</p>
 * <p>&quot;level&quot; - int, charge level, from 0 through &quot;scale&quot; inclusive</p>
 * <p>&quot;status&quot; - String, the current charging status.<br />
 * <p>&quot;health&quot; - String, the current battery health.<br />
 * <p>&quot;present&quot; - boolean, true if the battery is present<br />
 * <p>&quot;icon-small&quot; - int, suggested small icon to use for this state</p>
 * <p>&quot;plugged&quot; - int, 0 if the device is not plugged in; 1 if plugged
 * into an AC power adapter; 2 if plugged in via USB.</p>
 * <p>&quot;voltage&quot; - int, current battery voltage in millivolts</p>
 * <p>&quot;temperature&quot; - int, current battery temperature in tenths of
 * a degree Centigrade</p>
 * <p>&quot;technology&quot; - String, the type of battery installed, e.g. "Li-ion"</p>
 */
class BatteryService extends Binder {
    private static final String TAG = BatteryService.class.getSimpleName();

    private static final boolean LOCAL_LOGV = false;

    static final int BATTERY_SCALE = 100;    // battery capacity is a percentage

    // Used locally for determining when to make a last ditch effort to log
    // discharge stats before the device dies.
    private int mCriticalBatteryLevel;

    private static final int DUMP_MAX_LENGTH = 24 * 1024;
    private static final String[] DUMPSYS_ARGS = new String[] { "--checkin", "-u" };
    private static final String BATTERY_STATS_SERVICE_NAME = "batteryinfo";

    private static final String DUMPSYS_DATA_PATH = "/data/system/";

    // This should probably be exposed in the API, though it's not critical
    private static final int BATTERY_PLUGGED_NONE = 0;

    private final Context mContext;
    private final IBatteryStats mBatteryStats;

    private boolean mAcOnline;
    private boolean mUsbOnline;
    private int mBatteryStatus;
    private int mBatteryHealth;
    private boolean mBatteryPresent;
    private int mBatteryLevel;
    private int mBatteryVoltage;
    private int mBatteryTemperature;
    private String mBatteryTechnology;
    private boolean mBatteryLevelCritical;
    private int mInvalidCharger;

    private int mLastBatteryStatus;
    private int mLastBatteryHealth;
    private boolean mLastBatteryPresent;
    private int mLastBatteryLevel;
    private int mLastBatteryVoltage;
    private int mLastBatteryTemperature;
    private boolean mLastBatteryLevelCritical;
    private int mLastInvalidCharger;

    private int mLowBatteryWarningLevel;
    private int mLowBatteryCloseWarningLevel;

    private int mPlugType;
    private int mLastPlugType = -1; // Extra state so we can detect first run

    private long mDischargeStartTime;
    private int mDischargeStartLevel;

    private Led mLed;

    private boolean mSentLowBatteryBroadcast = false;

    // add for device SN
    private String mDeviceSN = "";


    // add by zhanghangzhi 
    private TVManager tvManager;
    private ConfigService configService;
    private static final String TMP_DEVICEID = "05337c619f5391716f3056a5a362662663761f46";
	
    public final static int DEVICEID_LENGTH = 40;
    public static String deviceID = "";
    public static String deviceIDCRC = "";
    public static int deviceIDCRC_Value = 0;
    public static String clientType = ""; //caosm for clienttype
	  public static Boolean is3DSupport = true;		//liutl add for 3DSupport flag
	  public static Boolean is5in1PB = true;			//liutl add for PanelButton type flag
    public static long CRC_TABLE[] = {                
                       0x0000,0x1021,0x2042,0x3063,0x4084,0x50A5,0x60C6,0x70E7,
                       0x8108,0x9129,0xA14A,0xB16B,0xC18C,0xD1AD,0xE1CE,0xF1EF
                       };
	         
    //crc_value=(int)CRCCalculate(buffer,buffer[1]-2);//crc  buffer[1]-2  CRC 
    /*
        if((((crc_value&0xff00)>>8)!=buffer[buffer[1]-2])
            ||((crc_value&0x00ff)!=buffer[buffer[1]-1]))
            deviced +crc=42
    */
 
    /*----------------------------------------------------------------------------
     * Name: CRCCalculate 
     * Description: caculate CRC16 for select databuffer
     * Inputs:  pBuffer        information buffer pointer
     *		  ucLength	calculate length
     * Returns: -CRC16 result
     ----------------------------------------------------------------------------*/

     /*
      * CRC: 0xb1f3  --- 45555
      * high := (short) ((crcData & 0xff00)>>8) 
      * low  := (short) (crcData & 0x00ff)
      *  show eeprom value
      *  hexdump /dev/eeprom_3 -C
         00000c00  ff ff ff ff ff ff ff ff  ff ff ff ff ff ff 64 62  |..............db|
         00000c10  35 36 32 65 63 61 66 62  63 64 31 34 65 62 31 33  |562ecafbcd14eb13|
         00000c20  65 38 31 30 39 34 36 35  30 65 35 65 32 39 31 61  |e81094650e5e291a|
         00000c30  65 33 63 33 34 38 b1 f3  00 00 00 00 00 00 00 00  |e3c348..........|

         deviceid:  db562ecafbcd14eb13e81094650e5e291ae3c348 
         CRC_value: b1 f3 
      */
	  
     public static long CRCCalculate(byte pBuffer[]) {
          
         Slog.d(TAG, "Entry CRCCalculate --- \n");
	
         long uwCRC = 0; //16bit
         int ucTemp = 0; //8bit
         
         uwCRC = 0xFFFF;
         for (int i=0;i< (DEVICEID_LENGTH);i++) {
             ucTemp = (int)(uwCRC>>0x0C);   
             ucTemp = ucTemp&0xFF;

             uwCRC <<= 4; 
             uwCRC = 0xFFFF&uwCRC;

             int s = ucTemp^(pBuffer[i]>>0x04);
             s = s&0x0f;

             uwCRC ^= CRC_TABLE[s];  
             uwCRC = 0xFFFF&uwCRC;

             ucTemp = (int)(uwCRC>>0x0C);   
             ucTemp = ucTemp&0xFF; 

             uwCRC <<= 4;	
             uwCRC = 0xFFFF&uwCRC;

             int s1 = ucTemp^(pBuffer[i]&0x0F);
             s1 = s1&0x0f;

             uwCRC ^= CRC_TABLE[s1];  
             uwCRC = 0xFFFF&uwCRC;
        }
   
        Slog.d(TAG, "crc data is ---"+uwCRC);
        String tt = Long.toHexString(uwCRC);
        Slog.d(TAG, "crc data is ---"+ tt);
        
        return uwCRC;
    }

    public static int CRCCalculate(String deviceIDStr) {
		   
        long uwCRC = 0;//16bit
        Slog.d(TAG, "Entry CRCCalculate! \n");
		
        if (deviceIDStr == null) {
            Slog.e(TAG,"deviceIDStr=null");
            return 0;
        }

        Slog.d(TAG, "--->deviceIDStr: "+ deviceIDStr);

        byte pBuffer[] = deviceIDStr.getBytes();
        uwCRC = CRCCalculate(pBuffer);	
		
        int retValue = (int)uwCRC;
        Slog.d(TAG, "crc data is ---"+ retValue);
		
        return retValue;
    }

    public static boolean isCRCRight(String deviceIDstr, int crc) {
        boolean retvalue = false;
        String tmpCRCStr = "";

        Slog.d(TAG,"Entry isCRCRight \n");
        Slog.d(TAG,"deviceIDstr: \n" + deviceIDstr);
        Slog.d(TAG,"deviceIDCRCStr: \n" + crc);
		
        if ( (deviceIDstr==null)||(crc==0 ) ) {
            return false;
        }
        
        long calculateCRC=CRCCalculate(deviceIDstr);
        int calCRC=(int)calculateCRC;
		
        if (calCRC==crc) {
            retvalue=true;
        }
      
        return retvalue;
    }
	
    private final static String DEVINFO_FILENAME = "/data/devinfo.txt";
    private static boolean getDeviceID() {

        Slog.d(TAG,"Entry getDeviceID() \n");
        File type = new File(DEVINFO_FILENAME);

        if (type.exists()&&type.canRead()) {
            Slog.d(TAG,"/data/devinfo.txt is exits and can read !!! \n");
            
            FileReader read;
            try {
                read = new FileReader(type);
                BufferedReader br = new BufferedReader(read);

                if (br.ready()) {
                    String deviceid = null;
                    String deviceidCRC = null;
                    String temdevid;
                    String temdevidCRC;

                    while ((temdevid = br.readLine()) != null) {
                        temdevid.trim();
	                if (temdevid.startsWith("devid")) {
                            deviceid = temdevid;
                            Slog.d(TAG, "Get deviceid Str =" + deviceid);
                        } else if (temdevid.startsWith("crc")) {
                            deviceidCRC = temdevid;
                            Slog.d(TAG, "Get deviceidCRC Str =" + deviceidCRC);
                        } 
                    }
                    
                    if ( deviceid!=null && deviceid.contains("=") ) {
                        Slog.d(TAG, "Get deviceid Str =" + deviceid);
                        deviceid = deviceid.substring(deviceid.indexOf("=") + 1);
                        Slog.d(TAG, "Get deviceid =" + deviceid);
                        deviceID = deviceid;
                        Slog.d(TAG, "read devinfo.txt Get deviceID =" + deviceID);
                    }

                    if (deviceidCRC!=null && deviceidCRC.contains("=") ){
                        Slog.d(TAG, "Get deviceidCRC Str =" + deviceidCRC);
                        deviceidCRC = deviceidCRC.substring(deviceidCRC.indexOf("=") + 1);
                        Slog.d(TAG, "Get deviceid =" + deviceidCRC);
                        deviceIDCRC = deviceidCRC;
                        Slog.d(TAG, "read devinfo.txt Get deviceIDCRC =" + deviceIDCRC);	

                        deviceIDCRC_Value=Integer.parseInt(deviceIDCRC.trim());
                        Slog.d(TAG, "read devinfo.txt Get deviceIDCRC_Value =" + deviceIDCRC_Value);	
								
                    }
					          
                    br.close();
                    return true;
                } else {
                    Slog.d(TAG, "can't read devinfo.txt deviceid");
                    return false;
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }  
        } else {
            return false;
        }
    }
	
    private void readDeviceID(Context mContext) {
        Slog.d(TAG, "Entry BatteryService readDeviceID...... \n " );
        tvManager = TVManager.getInstance(mContext);
        configService = (ConfigService)tvManager.getService(ConfigService.ConfigServiceName);

        int devidcrc = 0;		
        try {
            //_writeDeviceID( TMP_DEVICEID );//test write deviceID
            String devIDStr=_readDevDeviceId();
            Slog.d(TAG, "read devIDStr : \n " + devIDStr);
				
            devidcrc= readCrcData();
            Slog.d(TAG, "read devIDCRC : \n "+ devidcrc  );
                                  
            /* 
            // add by zhanghangzhi for test start
            if (devIDStr.equals("") && devidcrc==0) {
                Slog.d(TAG, "eeprom is empty."); 
                // write test devid
                String testDevId = "db562ecafbcd14eb13e81094650e5e291ae3c348";
                int testDevIdCrc = 45555;
                _writeDeviceID(testDevId);
                writeCrcData(testDevIdCrc);
                devIDStr = testDevId;
                devidcrc = testDevIdCrc;
            }
            // add by zhanghangzhi for test end
            */

            boolean ret = isCRCRight(devIDStr,devidcrc);
            if (ret) {
                Slog.d(TAG, "isCRCRight=true");
                saveWriteFile(devIDStr,devidcrc);
                return ;
            } else {
                Slog.e(TAG, "Read EEPROM  devid data  err !!! \n " );
                //isexit("devinfo.txt")
                if (getDeviceID()) {
                    boolean retval = isCRCRight(deviceID, deviceIDCRC_Value);
                    //file:devinfo.txt  data is right
                    if (retval) {
                        //write EEPROM deviceID
                        Slog.d(TAG, "isCRCRight=true");
                        //save data:
                        saveWriteFile(deviceID,deviceIDCRC_Value);
							
                        Slog.d(TAG, "Return write EEPROM: devidData: \n" + deviceID );
                        _writeDeviceID(deviceID);
                        Slog.d(TAG,"Return write EEPROM: devidDataCRC Value: \n"+ deviceIDCRC_Value );
                        writeCrcData(deviceIDCRC_Value);				
                        return ;
                    } else {
                        Slog.e(TAG, "Read File:devinfo.txt  devid data ==>Cal CRC err !!! \n " );
                    }
                } else {
                    Slog.e(TAG, "Read File:devinfo.txt  devid data  err !!! \n " );
                }
            }

            saveWriteFile(deviceID, deviceIDCRC_Value);
        } catch (Exception e) {
            e.printStackTrace();
       	}
    }

    //read deviceID opt:
    private String _readDevDeviceId() {	
        Slog.d(TAG, "Entry _readDevDeviceId() ..... \n ");
        String read_deviceID = "";
        byte[] mbyteData = null;
		
        mbyteData = configValueRead(ConfigType.CFG_FAC_DEVICE_ID, 40);
        try {
            if (mbyteData != null) {
                read_deviceID = new String(mbyteData, "UTF-8");
                //read_deviceID = new String(mbyteData,"GBK");
            } else {
                Slog.e(TAG, "read DeviceID data is err!");
            }
            Slog.d(TAG, "read deviceID from eeprom data: " + read_deviceID);
			
            } catch(UnsupportedEncodingException e) {
                e.printStackTrace();
            }	
        return read_deviceID;
    }

          //read uuid opt:
          private String _readDeviceUUID(){
                    Slog.d(TAG, "Entry _readDeviceUUID() ..... \n ");
                    String  read_uuid="";
                    byte[] mbyteData=null;
                
                    mbyteData=configValueRead(ConfigType.CFG_STORE_UUID,40);
                    try{
                        if (mbyteData != null) {
                            if (mbyteData[0] == -1 || mbyteData[1] == -1) {
                                return "";
                            }

                            read_uuid = new String(mbyteData, "UTF-8");
                        } else {
                            Slog.e(TAG, "read uuid data error!");
                            return "";
                        }

                        Slog.d(TAG, "read uuid : => " + read_uuid);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    return read_uuid;
          }

        // write device SN @douzy
        private void _writeDeviceSN(String sn) {
            if (sn == null || sn.equals("")) {
                return;
            }

            Slog.d(TAG, "write device SN : " + sn);
            byte[] bdata = sn.getBytes();
            short[] sdata = new short[bdata.length];

            for (int i=0; i<bdata.length; i++) {
                sdata[i] = (short)((bdata[i]) & 0x00FF);
            }

            int[] idata = dataChangeForEEP(sdata);
            configValueSet(ConfigType.CFG_FAC_SN, idata);
        }

        // read device SN @douzy
        private String _readDeviceSN() {
            Slog.d(TAG, "Ready to read device SN.");
            String _sn = "";
            byte[] bdata = null;

            bdata = configValueRead(ConfigType.CFG_FAC_SN, 22); // 22 chars

            // If never set device SN, return "".
            if (bdata != null) {
                if (bdata[0] == -1 || bdata[1] == -1) {
                    // TODO: need check the length < 22 ?
                    return "";
                }
            }

            try {
                if (bdata != null) {
                    _sn = new String(bdata, "UTF-8");
                } else {
                    Slog.e(TAG, "read SN error, no value.");
                }

                Slog.d(TAG, "Device SN from eeprom is : " + _sn);
            } catch (Exception e) {
                Slog.e(TAG, "error when convert device SN");
                e.printStackTrace();
            }
            Slog.d(TAG, "device SN : " + _sn);

            return _sn;
        }

	  private  byte[] configValueRead(String  configType,int length) {
		    Slog.d(TAG,"Entry configValueRead..........\n ");
		    ConfigValue value = new ConfigValue();
		    try {
			      value= configService.getCfg(configType);
		    } catch (TVMException e) {
			      e.printStackTrace();
		    }	
		    int data[]	= value.getIntArrayValue();
		
        /*
        Slog.d(TAG, "configValueRead--->(value.getIntArrayValue().length):\n" + (value.getIntArrayValue().length));
        for (int i=0;i<(value.getIntArrayValue().length);i++){
            Slog.d(TAG, "configValueRead--->read eeprom data---is---\n" + data[i]);
        }
        */

        int data1[] = new int[length/2];
        for (int i=0;i<(length/2);i++) {
            data1[i] = data[i];
            // Slog.d(TAG, "configValueRead--->read eeprom data---is---\n" + data[i]);
            // Slog.d(TAG, "configValueRead--->read eeprom data1---is---\n" + data1[i]);
        }
        
        return dataIntToShort(data1,length);
    }

    private byte[] dataIntToShort(int data[],int length){
        Slog.d(TAG,"Entry dataIntToShort ......\n ");
        byte[] byteData = new byte[length]; 
        int intLength = length/2;
		   
        for (int i=0;i<intLength;i++) {
            byteData[i*2+1] = (byte) ((data[i]>>8)&0xff);
            byteData[i*2] = (byte)(data[i]&0xff);
        }
         
        /*
        for (int i=0;i<length;i++) {
            Slog.d(TAG, "dataIntToShort ---> data is " + byteData[i]);
        }
        */
        try {
            String dev_idStr = "";
            dev_idStr = new String(byteData,"UTF-8");
            //dev_idStr = new String(byteData,"GBK");
            Slog.d(TAG, "dataIntToShort---> devID data:  " + dev_idStr);
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
		   
        return byteData;	
    }
	
    //------------------RegistrationCode ---------------------
    public  String readRegisterCode() { 
        String dev_idStr = "";                
        byte[] tmpbyte = _readRegiterCode();              
        if (tmpbyte == null || tmpbyte.length < 3)
            return "";     
        Slog.d(TAG, "readRegisterCode:tmpbyte[0]==" + tmpbyte[0]); 
        if((tmpbyte[0] == -1) & tmpbyte[1] == -1)
            return "";      
        try {       
            dev_idStr = new String(tmpbyte, "GBK");    
        } catch (UnsupportedEncodingException e) { 
            e.printStackTrace();     
            return "";   
        }      
	if (dev_idStr.length() >= 12) {      
            return dev_idStr.substring(0,11); 
	}
            return ""; 
   }

    private  byte[] _readRegiterCode() {
        return configValueRead(ConfigType.CFG_FAC_ACTIVE_KEY, 12);  
    }

    /**Manage DeviceID:  for example:
     * devid=05337c619f5391716f3056a5a362662663761f46 
     * sv=V8-0MT3601-LF1V003_000 
     * hv=MT36 
     * devmodel=TCL-CN-MT36-V7300A-3D
     */
    private void saveWriteFile(String str, int crc) {
        FileWriter fw = null;
        Slog.d(TAG, "Entry saveWriteFile .........\n");
        try {
            fw = new FileWriter("/data/devinfo.txt");			
            fw.write("devid=");
            Slog.d(TAG, "saveWriteFile--->write deviceID=: " + str);
            fw.write(str);
            //String devid = "f9a97d1a3ec1943e8d83b2b10c0a53a0de7f9555";
            //fw.write(devid);
            fw.write("\n");
						
            fw.write("sv="); 
            Slog.d(TAG, "saveWriteFile--->write sv=: " + SystemProperties.get("ro.software.version_id"));	
            fw.write(SystemProperties.get("ro.software.version_id")); 
            fw.write("\n");
				
            fw.write("hv="); 
            Slog.d(TAG, "saveWriteFile--->write hv=: " + SystemProperties.get("ro.hardware.version_id"));	
            fw.write(SystemProperties.get("ro.hardware.version_id")); 
            fw.write("\n");
				
            fw.write("devmodel="); 
            Slog.d(TAG, "saveWriteFile--->write devmodel=: " + SystemProperties.get("ro.IPTV_DEV_ID"));	
            fw.write(SystemProperties.get("ro.IPTV_DEV_ID")); 
            //fw.write(clientType); //caosm for clienttype
            fw.write("\n");
			
            fw.write("crc=");
            Slog.d(TAG, "saveWriteFile--->write deviceIDCRC=: " + crc);
            String tmpcrc = Integer.toString(crc);
            Slog.d(TAG,"saveWriteFile--->write real crc=: " + tmpcrc);
            fw.write(tmpcrc);
            fw.write("\n");
			   //liutl add for 3D Status
			   fw.write("3DSupport=");
			   String tmp3D = "";
			   if(is3DSupport)
			   	{
			      Slog.d(TAG,"saveWriteFile--->write 3D Support=:1 ");
			      tmp3D=Integer.toString(1);
			   	}
			   else
			   	{
			      Slog.d(TAG,"saveWriteFile--->write 3D Support=:0");
			      tmp3D=Integer.toString(0);			   	
			   	}
	          	     fw.write(tmp3D);
			      fw.write("\n");

			 //liutl add for DianDu Support
			   fw.write("DianDuSupport=");
			   String tmpDianDu = "";
			   	{
			      Slog.d(TAG,"saveWriteFile--->write DianDu Support=:0");
			      tmpDianDu=Integer.toString(0);			   	
			   	}
	          	     fw.write(tmpDianDu);
			      fw.write("\n");

			   //liutl add for PanelButton type
			   fw.write("PanelButton=");
			   String tmpPB = "";
			   if(is5in1PB)
			   	{
			      Slog.d(TAG,"saveWriteFile--->write PanelButton type=:1 ");
			      tmpPB=Integer.toString(1);
			   	}
			   else
			   	{
			      Slog.d(TAG,"saveWriteFile--->write PanelButton type=:0");
			      tmpPB=Integer.toString(0);			   	
			   	}
	          	     fw.write(tmpPB);
			      fw.write("\n");
			  //gaodwl add for getRegistrationCode.
			   fw.write("RegistrationCode=");
			   String RegistrationCodeStr = "";
			   {
			   	RegistrationCodeStr=readRegisterCode() ;			
				Slog.d(TAG,"saveWriteFile--->write RegistrationCode=: " + RegistrationCodeStr );
			   }
	          	   fw.write(RegistrationCodeStr);
			   fw.write("\n");
				  
			
            fw.flush();
            //fw.close(); 
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                    //Runtime.getRuntime().exec("chmod 777 /data/devinfo.txt "); //set chmod 777
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }   
    }
	
    //write DeviceID opt:
    private void _writeDeviceID(String deviceIDStr) {
        if (deviceIDStr != null) {
            Slog.d(TAG, "Entry _writeDeviceID()---> deviceIDStr: \n"+deviceIDStr);
        } else {
            Slog.e(TAG, "Entry _writeDeviceID()---> deviceIDStr: deviceIDStr=null");
            return;
        }
		
        short[] shortData = null;
        byte[] mbytedata = deviceIDStr.getBytes();
        int intArrayLength = mbytedata.length;
        shortData = new short[intArrayLength];

        for (int i=0;i<intArrayLength;i++) {
            shortData[i] = (short)( (mbytedata[i]) & (0x00ff) );
            //Slog.d(TAG, "_writeDeviceID---> shortData  is :  " + shortData[i]);
        }

        int[] intData = dataChangeForEEP(shortData);
        configValueSet(ConfigType.CFG_FAC_DEVICE_ID,intData);
    }
 
    /*
     * Name: dataChangeForEEP
     * Returns: int[]
     */
    private int[] dataChangeForEEP(short data[]) {
        Slog.d(TAG, "Entry dataChangeForEEP() ");	
		
        int[] intData = null;
        int len=data.length;
        int intArrayLength = len/2;
           
        if (len%2==0) {
            intData = new int[intArrayLength];
            for (int i=0;i<intArrayLength;i++) {
                intData[i] = ((data[i*2+1]<<8)&0xff00)+data[i*2];
                Slog.d(TAG, "dataChangeForEEP---> data is A :  " + intData[i]);
            }
        } else {
            intData = new int[intArrayLength+1];
            for (int i=0;i<intArrayLength;i++) {
                intData[i] = (data[i*2+1]<<8)&0xff00+data[i*2];
                //Slog.d(TAG, "dataChangeForEEP---> data is B:  " + intData[i]);
            }
            
            intData[intArrayLength-1] = data[len-1];
        }
        
        return intData;
    }
	
    /*
     * Function: configValueSet(String  configType,int[] data)
     */
    private void configValueSet(String  configType,int[] data) {	
        ConfigValue value = new ConfigValue();
        value.setIntArrayValue(data);
        try {
            configService.setCfg(configType,value);	
        } catch (TVMException e) {
            e.printStackTrace();
        }
    }

    //read CRC
    public int readCrcData() {
        int highValue = ((getNvmData(0xc36) << 8) & 0xff00);
        int lowValue = (getNvmData(0xc37) & 0xff);
        return (highValue + lowValue);
    }
	  
    // write CRC
    public void writeCrcData(int crcData) {
        int highData = (short) ((crcData & 0xff00) >> 8);
        writeNvmData(0xc36, highData);
        int lowdata = (short) (crcData & 0x00ff);
        writeNvmData(0xc37, lowdata);
    }
	 
    public void writeNvmData(int addr, int data) {
        Slog.d("------------------", "---writeNvmData---" + addr + "  " + data);
        ConfigValue value = new ConfigValue();
        value.setIntValue(addr);
        try {
            configService.setCfg(ConfigType.CFG_FAC_WR_ADDR, value);
        } catch (TVMException e) {
            e.printStackTrace();
        }
     
        ConfigValue valuedata = new ConfigValue();
        valuedata.setIntValue(data);
        try {
            configService.setCfg(ConfigType.CFG_FAC_WR_DATA, valuedata);
        } catch (TVMException e) {
            e.printStackTrace();
        }
    }	
	   
    public short getNvmData(int addr) {
        ConfigValue value = new ConfigValue();
        value.setIntValue(addr);
        try {
            configService.setCfg(ConfigType.CFG_FAC_WR_ADDR, value);// write // addr
        } catch (TVMException e) {
            e.printStackTrace();
        }

        try {
            value = configService.getCfg(ConfigType.CFG_FAC_WR_DATA);// get data
        } catch (TVMException e) {
            e.printStackTrace();
        }
      
        return (short) value.getIntValue();
    }

    private void setFileReadable(String fn) {
        File f = new File(fn);
        
        Slog.d(TAG, "coming setFileReadable, fn: " + fn);
        if (f.exists()) {
            Slog.d(TAG, "" + fn + " is exist.");
            try {
                f.setReadable(true, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // end

	  public  int readProjectIdFromEEPROM(Context mContext){
		Slog.d(TAG, "Entry BatteryService readProjectID...... \n " );
		ConfigValue value = new ConfigValue();
		tvManager = TVManager.getInstance(mContext);
		configService=(ConfigService)tvManager.getService(ConfigService.ConfigServiceName);
		try {
			  value = configService.getCfg(ConfigType.CFG_PROJECT_ID);// get project id
		} catch (TVMException e) {
			  e.printStackTrace();
		}
		Slog.d(TAG, "davild=============......."+ String.valueOf(value.getIntValue()) );
		SystemProperties.set("persist.sys.PRJID", String.valueOf(value.getIntValue()) );
		
		return value.getIntValue();    
	  }
	
public String Client_Type = "TCL-CN-MT36K-E5690A-3DG";
	
public  String getClientType(int prjid){
    return Client_Type;
}

public  Boolean get3DFlag(int prjid){
    return true;
}

public Boolean getPanelButtonType(int prjid)
{
    Slog.d(TAG, "getPBType based on project id......."+ prjid);
	return true;

}
    public BatteryService(Context context, LightsService lights) {
        mContext = context;    
        int prjid = 0;
        
        // add by zhanghangzhi at 2012-11-06 start
        Slog.d(TAG, "Entry BatteryService...... \n");
	prjid = readProjectIdFromEEPROM(mContext);
   	clientType = getClientType(prjid); //caosm
  //liutl add for is 3D Support
	is3DSupport = get3DFlag(prjid);
	//liut add for panel button type
	is5in1PB = getPanelButtonType(prjid);
        readDeviceID(context);
        // add by zhanghangzhi at 2012-11-06 end


      	//add by liuwei03@tcl.com getRegistrationCode.
	String RegistrationCodeStr=readRegisterCode();

        mLed = new Led(context, lights);
        mBatteryStats = BatteryStatsService.getService();

        mCriticalBatteryLevel = mContext.getResources().getInteger(
                com.android.internal.R.integer.config_criticalBatteryWarningLevel);
        mLowBatteryWarningLevel = mContext.getResources().getInteger(
                com.android.internal.R.integer.config_lowBatteryWarningLevel);
        mLowBatteryCloseWarningLevel = mContext.getResources().getInteger(
                com.android.internal.R.integer.config_lowBatteryCloseWarningLevel);

        mPowerSupplyObserver.startObserving("SUBSYSTEM=power_supply");

        // watch for invalid charger messages if the invalid_charger switch exists
        if (new File("/sys/devices/virtual/switch/invalid_charger/state").exists()) {
            mInvalidChargerObserver.startObserving("DEVPATH=/devices/virtual/switch/invalid_charger");
        }

        // set initial status
        update();

		//add by zhaodm
		SystemProperties.set("ro.IPTV_DEV_ID", clientType);
		//add by gaodw.
		//add projectID.
		SystemProperties.set("persist.sys.PRJID", String.valueOf(prjid) );
		//add RegistrationCode.
		SystemProperties.set("persist.sys.RegistrationCode", RegistrationCodeStr);

                String uuid = _readDeviceUUID();
                SystemProperties.set("persist.sys.UUID", uuid);

                // write device SN for test
                // _writeDeviceSN("24001096612107134G0014");

                // @douzy
                // add for device SN
                mDeviceSN = _readDeviceSN();

                //add for device SN
                SystemProperties.set("persist.sys.SN", mDeviceSN);
                //add for 3D status
                SystemProperties.set("persist.sys.three_D_status", is3DSupport ? "1" : "0");
		
		// add by xiangjt at 2013-11-07 start
        Slog.d(TAG, "Set porting properties");
		String propertyValue = SystemProperties.get("ro.software.version_id");
		SystemProperties.set("matrix.software.version", propertyValue);
		
		propertyValue = clientType + "-" + readProjectIdFromEEPROM(mContext); 
		SystemProperties.set("matrix.hardware.version", propertyValue);
		Slog.d(TAG, "matrix.hardware.version--->" + propertyValue);
		
		propertyValue = SystemProperties.get("ro.software.version_id");
		int idx = propertyValue.lastIndexOf("-");
		if (idx == -1) return;
		propertyValue = propertyValue.substring(0,idx);
		SystemProperties.set("matrix.platform.version", propertyValue);
		Slog.d(TAG, "matrix.platform.version--->" + propertyValue);
        // add by xiangjt at 2013-11-07 end
    }

    final boolean isPowered() {
        // assume we are powered if battery state is unknown so the "stay on while plugged in" option will work.
        return (mAcOnline || mUsbOnline || mBatteryStatus == BatteryManager.BATTERY_STATUS_UNKNOWN);
    }

    final boolean isPowered(int plugTypeSet) {
        // assume we are powered if battery state is unknown so
        // the "stay on while plugged in" option will work.
        if (mBatteryStatus == BatteryManager.BATTERY_STATUS_UNKNOWN) {
            return true;
        }
        if (plugTypeSet == 0) {
            return false;
        }
        int plugTypeBit = 0;
        if (mAcOnline) {
            plugTypeBit |= BatteryManager.BATTERY_PLUGGED_AC;
        }
        if (mUsbOnline) {
            plugTypeBit |= BatteryManager.BATTERY_PLUGGED_USB;
        }
        return (plugTypeSet & plugTypeBit) != 0;
    }

    final int getPlugType() {
        return mPlugType;
    }

    private UEventObserver mPowerSupplyObserver = new UEventObserver() {
        @Override
        public void onUEvent(UEventObserver.UEvent event) {
            update();
        }
    };

    private UEventObserver mInvalidChargerObserver = new UEventObserver() {
        @Override
        public void onUEvent(UEventObserver.UEvent event) {
            int invalidCharger = "1".equals(event.get("SWITCH_STATE")) ? 1 : 0;
            if (mInvalidCharger != invalidCharger) {
                mInvalidCharger = invalidCharger;
                update();
            }
        }
    };

    // returns battery level as a percentage
    final int getBatteryLevel() {
        return mBatteryLevel;
    }

    void systemReady() {
        // check our power situation now that it is safe to display the shutdown dialog.
        shutdownIfNoPower();
        shutdownIfOverTemp();
    }

    private final void shutdownIfNoPower() {
        // shut down gracefully if our battery is critically low and we are not powered.
        // wait until the system has booted before attempting to display the shutdown dialog.
        if (mBatteryLevel == 0 && !isPowered() && ActivityManagerNative.isSystemReady()) {
            Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
            intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    private final void shutdownIfOverTemp() {
        // shut down gracefully if temperature is too high (> 68.0C)
        // wait until the system has booted before attempting to display the shutdown dialog.
        if (mBatteryTemperature > 680 && ActivityManagerNative.isSystemReady()) {
            Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
            intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    private native void native_update();

    private synchronized final void update() {
        native_update();
        processValues();
    }

    private void processValues() {
        boolean logOutlier = false;
        long dischargeDuration = 0;

        mBatteryLevelCritical = mBatteryLevel <= mCriticalBatteryLevel;
        if (mAcOnline) {
            mPlugType = BatteryManager.BATTERY_PLUGGED_AC;
        } else if (mUsbOnline) {
            mPlugType = BatteryManager.BATTERY_PLUGGED_USB;
        } else {
            mPlugType = BATTERY_PLUGGED_NONE;
        }
        
        // Let the battery stats keep track of the current level.
        try {
            mBatteryStats.setBatteryState(mBatteryStatus, mBatteryHealth,
                    mPlugType, mBatteryLevel, mBatteryTemperature,
                    mBatteryVoltage);
        } catch (RemoteException e) {
            // Should never happen.
        }
        
        shutdownIfNoPower();
        shutdownIfOverTemp();

        if (mBatteryStatus != mLastBatteryStatus ||
                mBatteryHealth != mLastBatteryHealth ||
                mBatteryPresent != mLastBatteryPresent ||
                mBatteryLevel != mLastBatteryLevel ||
                mPlugType != mLastPlugType ||
                mBatteryVoltage != mLastBatteryVoltage ||
                mBatteryTemperature != mLastBatteryTemperature ||
                mInvalidCharger != mLastInvalidCharger) {

            if (mPlugType != mLastPlugType) {
                if (mLastPlugType == BATTERY_PLUGGED_NONE) {
                    // discharging -> charging

                    // There's no value in this data unless we've discharged at least once and the
                    // battery level has changed; so don't log until it does.
                    if (mDischargeStartTime != 0 && mDischargeStartLevel != mBatteryLevel) {
                        dischargeDuration = SystemClock.elapsedRealtime() - mDischargeStartTime;
                        logOutlier = true;
                        EventLog.writeEvent(EventLogTags.BATTERY_DISCHARGE, dischargeDuration,
                                mDischargeStartLevel, mBatteryLevel);
                        // make sure we see a discharge event before logging again
                        mDischargeStartTime = 0;
                    }
                } else if (mPlugType == BATTERY_PLUGGED_NONE) {
                    // charging -> discharging or we just powered up
                    mDischargeStartTime = SystemClock.elapsedRealtime();
                    mDischargeStartLevel = mBatteryLevel;
                }
            }
            if (mBatteryStatus != mLastBatteryStatus ||
                    mBatteryHealth != mLastBatteryHealth ||
                    mBatteryPresent != mLastBatteryPresent ||
                    mPlugType != mLastPlugType) {
                EventLog.writeEvent(EventLogTags.BATTERY_STATUS,
                        mBatteryStatus, mBatteryHealth, mBatteryPresent ? 1 : 0,
                        mPlugType, mBatteryTechnology);
            }
            if (mBatteryLevel != mLastBatteryLevel ||
                    mBatteryVoltage != mLastBatteryVoltage ||
                    mBatteryTemperature != mLastBatteryTemperature) {
                EventLog.writeEvent(EventLogTags.BATTERY_LEVEL,
                        mBatteryLevel, mBatteryVoltage, mBatteryTemperature);
            }
            if (mBatteryLevelCritical && !mLastBatteryLevelCritical &&
                    mPlugType == BATTERY_PLUGGED_NONE) {
                // We want to make sure we log discharge cycle outliers
                // if the battery is about to die.
                dischargeDuration = SystemClock.elapsedRealtime() - mDischargeStartTime;
                logOutlier = true;
            }

            final boolean plugged = mPlugType != BATTERY_PLUGGED_NONE;
            final boolean oldPlugged = mLastPlugType != BATTERY_PLUGGED_NONE;

            /* The ACTION_BATTERY_LOW broadcast is sent in these situations:
             * - is just un-plugged (previously was plugged) and battery level is
             *   less than or equal to WARNING, or
             * - is not plugged and battery level falls to WARNING boundary
             *   (becomes <= mLowBatteryWarningLevel).
             */
            final boolean sendBatteryLow = !plugged
                    && mBatteryStatus != BatteryManager.BATTERY_STATUS_UNKNOWN
                    && mBatteryLevel <= mLowBatteryWarningLevel
                    && (oldPlugged || mLastBatteryLevel > mLowBatteryWarningLevel);

            sendIntent();

            // Separate broadcast is sent for power connected / not connected
            // since the standard intent will not wake any applications and some
            // applications may want to have smart behavior based on this.
            Intent statusIntent = new Intent();
            statusIntent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT);
            if (mPlugType != 0 && mLastPlugType == 0) {
                statusIntent.setAction(Intent.ACTION_POWER_CONNECTED);
                mContext.sendBroadcast(statusIntent);
            }
            else if (mPlugType == 0 && mLastPlugType != 0) {
                statusIntent.setAction(Intent.ACTION_POWER_DISCONNECTED);
                mContext.sendBroadcast(statusIntent);
            }

            if (sendBatteryLow) {
                mSentLowBatteryBroadcast = true;
                statusIntent.setAction(Intent.ACTION_BATTERY_LOW);
                mContext.sendBroadcast(statusIntent);
            } else if (mSentLowBatteryBroadcast && mLastBatteryLevel >= mLowBatteryCloseWarningLevel) {
                mSentLowBatteryBroadcast = false;
                statusIntent.setAction(Intent.ACTION_BATTERY_OKAY);
                mContext.sendBroadcast(statusIntent);
            }

            // Update the battery LED
            mLed.updateLightsLocked();

            // This needs to be done after sendIntent() so that we get the lastest battery stats.
            if (logOutlier && dischargeDuration != 0) {
                logOutlier(dischargeDuration);
            }

            mLastBatteryStatus = mBatteryStatus;
            mLastBatteryHealth = mBatteryHealth;
            mLastBatteryPresent = mBatteryPresent;
            mLastBatteryLevel = mBatteryLevel;
            mLastPlugType = mPlugType;
            mLastBatteryVoltage = mBatteryVoltage;
            mLastBatteryTemperature = mBatteryTemperature;
            mLastBatteryLevelCritical = mBatteryLevelCritical;
            mLastInvalidCharger = mInvalidCharger;
        }
    }

    private final void sendIntent() {
        //  Pack up the values and broadcast them to everyone
        Intent intent = new Intent(Intent.ACTION_BATTERY_CHANGED);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY
                | Intent.FLAG_RECEIVER_REPLACE_PENDING);

        int icon = getIcon(mBatteryLevel);

        intent.putExtra(BatteryManager.EXTRA_STATUS, mBatteryStatus);
        intent.putExtra(BatteryManager.EXTRA_HEALTH, mBatteryHealth);
        intent.putExtra(BatteryManager.EXTRA_PRESENT, mBatteryPresent);
        intent.putExtra(BatteryManager.EXTRA_LEVEL, mBatteryLevel);
        intent.putExtra(BatteryManager.EXTRA_SCALE, BATTERY_SCALE);
        intent.putExtra(BatteryManager.EXTRA_ICON_SMALL, icon);
        intent.putExtra(BatteryManager.EXTRA_PLUGGED, mPlugType);
        intent.putExtra(BatteryManager.EXTRA_VOLTAGE, mBatteryVoltage);
        intent.putExtra(BatteryManager.EXTRA_TEMPERATURE, mBatteryTemperature);
        intent.putExtra(BatteryManager.EXTRA_TECHNOLOGY, mBatteryTechnology);
        intent.putExtra(BatteryManager.EXTRA_INVALID_CHARGER, mInvalidCharger);

        if (false) {
            Slog.d(TAG, "level:" + mBatteryLevel +
                    " scale:" + BATTERY_SCALE + " status:" + mBatteryStatus +
                    " health:" + mBatteryHealth +  " present:" + mBatteryPresent +
                    " voltage: " + mBatteryVoltage +
                    " temperature: " + mBatteryTemperature +
                    " technology: " + mBatteryTechnology +
                    " AC powered:" + mAcOnline + " USB powered:" + mUsbOnline +
                    " icon:" + icon  + " invalid charger:" + mInvalidCharger);
        }

        ActivityManagerNative.broadcastStickyIntent(intent, null);
    }

    private final void logBatteryStats() {
        IBinder batteryInfoService = ServiceManager.getService(BATTERY_STATS_SERVICE_NAME);
        if (batteryInfoService == null) return;

        DropBoxManager db = (DropBoxManager) mContext.getSystemService(Context.DROPBOX_SERVICE);
        if (db == null || !db.isTagEnabled("BATTERY_DISCHARGE_INFO")) return;

        File dumpFile = null;
        FileOutputStream dumpStream = null;
        try {
            // dump the service to a file
            dumpFile = new File(DUMPSYS_DATA_PATH + BATTERY_STATS_SERVICE_NAME + ".dump");
            dumpStream = new FileOutputStream(dumpFile);
            batteryInfoService.dump(dumpStream.getFD(), DUMPSYS_ARGS);
            FileUtils.sync(dumpStream);

            // add dump file to drop box
            db.addFile("BATTERY_DISCHARGE_INFO", dumpFile, DropBoxManager.IS_TEXT);
        } catch (RemoteException e) {
            Slog.e(TAG, "failed to dump battery service", e);
        } catch (IOException e) {
            Slog.e(TAG, "failed to write dumpsys file", e);
        } finally {
            // make sure we clean up
            if (dumpStream != null) {
                try {
                    dumpStream.close();
                } catch (IOException e) {
                    Slog.e(TAG, "failed to close dumpsys output stream");
                }
            }
            if (dumpFile != null && !dumpFile.delete()) {
                Slog.e(TAG, "failed to delete temporary dumpsys file: "
                        + dumpFile.getAbsolutePath());
            }
        }
    }

    private final void logOutlier(long duration) {
        ContentResolver cr = mContext.getContentResolver();
        String dischargeThresholdString = Settings.Secure.getString(cr,
                Settings.Secure.BATTERY_DISCHARGE_THRESHOLD);
        String durationThresholdString = Settings.Secure.getString(cr,
                Settings.Secure.BATTERY_DISCHARGE_DURATION_THRESHOLD);

        if (dischargeThresholdString != null && durationThresholdString != null) {
            try {
                long durationThreshold = Long.parseLong(durationThresholdString);
                int dischargeThreshold = Integer.parseInt(dischargeThresholdString);
                if (duration <= durationThreshold &&
                        mDischargeStartLevel - mBatteryLevel >= dischargeThreshold) {
                    // If the discharge cycle is bad enough we want to know about it.
                    logBatteryStats();
                }
                if (LOCAL_LOGV) Slog.v(TAG, "duration threshold: " + durationThreshold +
                        " discharge threshold: " + dischargeThreshold);
                if (LOCAL_LOGV) Slog.v(TAG, "duration: " + duration + " discharge: " +
                        (mDischargeStartLevel - mBatteryLevel));
            } catch (NumberFormatException e) {
                Slog.e(TAG, "Invalid DischargeThresholds GService string: " +
                        durationThresholdString + " or " + dischargeThresholdString);
                return;
            }
        }
    }

    private final int getIcon(int level) {
        if (mBatteryStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
            return com.android.internal.R.drawable.stat_sys_battery_charge;
        } else if (mBatteryStatus == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            return com.android.internal.R.drawable.stat_sys_battery;
        } else if (mBatteryStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING
                || mBatteryStatus == BatteryManager.BATTERY_STATUS_FULL) {
            if (isPowered() && mBatteryLevel >= 100) {
                return com.android.internal.R.drawable.stat_sys_battery_charge;
            } else {
                return com.android.internal.R.drawable.stat_sys_battery;
            }
        } else {
            return com.android.internal.R.drawable.stat_sys_battery_unknown;
        }
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (mContext.checkCallingOrSelfPermission(android.Manifest.permission.DUMP)
                != PackageManager.PERMISSION_GRANTED) {

            pw.println("Permission Denial: can't dump Battery service from from pid="
                    + Binder.getCallingPid()
                    + ", uid=" + Binder.getCallingUid());
            return;
        }

        if (args == null || args.length == 0 || "-a".equals(args[0])) {
            synchronized (this) {
                pw.println("Current Battery Service state:");
                pw.println("  AC powered: " + mAcOnline);
                pw.println("  USB powered: " + mUsbOnline);
                pw.println("  status: " + mBatteryStatus);
                pw.println("  health: " + mBatteryHealth);
                pw.println("  present: " + mBatteryPresent);
                pw.println("  level: " + mBatteryLevel);
                pw.println("  scale: " + BATTERY_SCALE);
                pw.println("  voltage:" + mBatteryVoltage);
                pw.println("  temperature: " + mBatteryTemperature);
                pw.println("  technology: " + mBatteryTechnology);
            }
        } else if (false) {
            // DO NOT SUBMIT WITH THIS TURNED ON
            if (args.length == 3 && "set".equals(args[0])) {
                String key = args[1];
                String value = args[2];
                try {
                    boolean update = true;
                    if ("ac".equals(key)) {
                        mAcOnline = Integer.parseInt(value) != 0;
                    } else if ("usb".equals(key)) {
                        mUsbOnline = Integer.parseInt(value) != 0;
                    } else if ("status".equals(key)) {
                        mBatteryStatus = Integer.parseInt(value);
                    } else if ("level".equals(key)) {
                        mBatteryLevel = Integer.parseInt(value);
                    } else if ("invalid".equals(key)) {
                        mInvalidCharger = Integer.parseInt(value);
                    } else {
                        update = false;
                    }
                    if (update) {
                        processValues();
                    }
                } catch (NumberFormatException ex) {
                    pw.println("Bad value: " + value);
                }
            }
        }
    }

    class Led {
        private LightsService mLightsService;
        private LightsService.Light mBatteryLight;

        private int mBatteryLowARGB;
        private int mBatteryMediumARGB;
        private int mBatteryFullARGB;
        private int mBatteryLedOn;
        private int mBatteryLedOff;

        private boolean mBatteryCharging;
        private boolean mBatteryLow;
        private boolean mBatteryFull;

        Led(Context context, LightsService lights) {
            mLightsService = lights;
            mBatteryLight = lights.getLight(LightsService.LIGHT_ID_BATTERY);

            mBatteryLowARGB = mContext.getResources().getInteger(
                    com.android.internal.R.integer.config_notificationsBatteryLowARGB);
            mBatteryMediumARGB = mContext.getResources().getInteger(
                    com.android.internal.R.integer.config_notificationsBatteryMediumARGB);
            mBatteryFullARGB = mContext.getResources().getInteger(
                    com.android.internal.R.integer.config_notificationsBatteryFullARGB);
            mBatteryLedOn = mContext.getResources().getInteger(
                    com.android.internal.R.integer.config_notificationsBatteryLedOn);
            mBatteryLedOff = mContext.getResources().getInteger(
                    com.android.internal.R.integer.config_notificationsBatteryLedOff);
        }

        /**
         * Synchronize on BatteryService.
         */
        void updateLightsLocked() {
            final int level = mBatteryLevel;
            final int status = mBatteryStatus;
            if (level < mLowBatteryWarningLevel) {
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    // Solid red when battery is charging
                    mBatteryLight.setColor(mBatteryLowARGB);
                } else {
                    // Flash red when battery is low and not charging
                    mBatteryLight.setFlashing(mBatteryLowARGB, LightsService.LIGHT_FLASH_TIMED,
                            mBatteryLedOn, mBatteryLedOff);
                }
            } else if (status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL) {
                if (status == BatteryManager.BATTERY_STATUS_FULL || level >= 90) {
                    // Solid green when full or charging and nearly full
                    mBatteryLight.setColor(mBatteryFullARGB);
                } else {
                    // Solid orange when charging and halfway full
                    mBatteryLight.setColor(mBatteryMediumARGB);
                }
            } else {
                // No lights if not charging and not low
                mBatteryLight.turnOff();
            }
        }
    }
}

