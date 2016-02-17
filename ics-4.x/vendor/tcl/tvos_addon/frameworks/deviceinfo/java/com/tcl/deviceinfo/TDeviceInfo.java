package com.tcl.deviceinfo;

import java.lang.Object;
import java.lang.Exception;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import android.os.SystemProperties;

/*
import com.tcl.tvmanager.TAppManager;
import com.tcl.tvmanager.TTvChannelManager;
import com.tcl.tvmanager.TTvPictureManager;
import com.tcl.tvmanager.TTvCommonManager;
import com.tcl.tvmanager.TTvSoundManager;
import com.tcl.tvmanager.vo.EnTCLInputSource;
import com.tcl.tvmanager.vo.EnTCLPictureMode;
import com.tcl.tvmanager.vo.EnTCLColorTemperature;
import com.tcl.tvmanager.vo.EnTCLPictureView;
import com.tcl.tvmanager.vo.EnTCLSoundScene;
*/

import com.mediatek.tv.service.ConfigService;
import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ConfigValue;
import android.util.Slog;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.common.TVMException;
import java.io.UnsupportedEncodingException;


import java.util.ArrayList;
import java.util.List;
import android.util.Log;

/**
 *  DeviceInfo组件用于标准化管理平台相关的数据。组件负责做五件事： 
 *	<br>&Oslash;&nbsp; 负责创建/data/devinfo.txt文件，包含：DeviceID、软件版本号、ClientType、DeviceID的CRC校验码
 *	<br>&Oslash;&nbsp; 实现DeviceID的双备份，开机对DeviceID进行校验
 *	<br>&Oslash;&nbsp; 为用户提供平台相关的数据并通过ProjectID来区分这些数据，实现一套软件兼容多个ProjectID的目的
 *	<br>&Oslash;&nbsp; 平台数据进行双备份：为了确保平台数据的安全性，对数据进行双备份。系统启动时对数据进行校验，如果数据校验失败，则从备份中恢复
 *	<br>&Oslash;&nbsp; 为应用提供工厂相关的接口。应用要想获取工厂数据，不能直接调用工厂菜单接口，需通过DeviceInfo组件的接口来获取，以确保数据的安全性
 */
public class TDeviceInfo {
	private static final String TAG = "TCL_DeviceInfo";
	private static final boolean LOGI = true;
	private static final String DEVINFO_FILENAME = "/data/devinfo.txt";
	private static final String CITY_FILENAME = "/data/cityname.txt";
	private static TDeviceInfo mDeviceInfo;

	private static ConfigService configService;

	/* modify by gaodw.
	static {
		try {
			System.loadLibrary("deviceinfo_jni");
		} catch (Exception e) {
			if(LOGI) Log.i(TAG, "Count not load deviceinfo_jni library");
		}
		try {
			System.loadLibrary("com_tcl_tv_jni");
		} catch (Exception e) {
			if(LOGI) Log.i(TAG, "Count not load com_tcl_tv_jni library");
		}
	
	}
	*/


    private TDeviceInfo(){
	TVManager  tvManager = TVManager.getInstance(null);;
	configService=(ConfigService)tvManager.getService(ConfigService.ConfigServiceName);
    }


    /**
     * 获取DeviceInfo的实例对象
     * @param 无
     * @return 返回一个DeviceInfo的对象
     */
	public static TDeviceInfo getInstance() {
		if(mDeviceInfo == null) {
			mDeviceInfo = new TDeviceInfo();
		}
		return mDeviceInfo;
	}

    /**
     * 获取设备的硬件版本 
     * @param 无
     * @return 返回硬件版本号
     */
	public String getHardwareVersion() {
		String mRet = "";
		try {
			mRet = SystemProperties.get("ro.hardware.version_id");
			if(LOGI) Log.i(TAG, "getHardwareVersion: " +  mRet );
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}

    /**
     * 获取系统的软件版本 
     * @param 无
     * @return 返回系统软件版本号
     */
	public String getSoftwareVersion() {
		String mRet = "";
		try {
			mRet = SystemProperties.get("ro.software.version_id");
			if(LOGI) Log.i(TAG, "getSoftwareVersion: " +  mRet );
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}

    /**
     * 获取ClientType，建议用getClientType()方法代替 
     * @param  无
     * @return 返回当前平台ClientType
     */
	 @Deprecated
	public String getDeviceModel() {
		//modify by gaow
		return getClientType(getProjectID());
        /*
		String mRet = "";
		try {
			mRet = SystemProperties.get("ro.IPTV_DEV_ID");
			if(LOGI) Log.i(TAG, "getDeviceModel: " +  mRet );
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
        */
	}

    /**
     * 获取DeviceID 
     * @param  无
     * @return 返回当前平台DeviceID
     */
	public String getDeviceID() {
        String deviceID = "";
        try {
            FileReader read = new FileReader(new File("/data/devinfo.txt"));
            BufferedReader br = new BufferedReader(read);
            while ((deviceID = br.readLine()) != null) {
                if (deviceID.contains("devid")) {
                    deviceID = deviceID.substring(deviceID.indexOf("=") + 1);
                    //Log.i("我从文件中读到的device id", deviceID+"");
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            return "";
        }

        deviceID = (deviceID==null)?"":deviceID;
        return deviceID;
	}

    /**
     * 用于恢复出厂设置清理平台数据，恢复到出厂状态 
	 * 该方法根据不同平台需要单独实现
     * @param  无
     * @return 无
     */
	 
	 /*
	public void cleanE2promInfo() {
		try {
			native_doCleanChannelList();
			native_doResetUserSettings();
		} catch (Exception e){
			e.printStackTrace();
		}
	}*/

    /**
     * 获取ProjectID 
     * @param  无
     * @return 返回当前平台的ProjectID，默认值为：0
     */
	public int getProjectID() {
		int mRet1 = 0;
		/*
		try {
			mRet = native_doGetAttribute(FACTORY_ATTR_PROJ_ID);
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
		*/
		
		//modify by gaow
		String mRet = "";
		try {
			//mRet = SystemProperties.get("ro.IPTV_DEV_PRJID");
			mRet = SystemProperties.get("persist.sys.PRJID");
			if(LOGI) Log.i(TAG, "getProjectID 0: " +  mRet );  
			mRet1 = Integer.parseInt( mRet.trim() );
			if(LOGI) Log.i(TAG, "getProjectID 1: " +  mRet1 );  
		} catch (Exception e){
			Log.e("TDeviceInfo: ",  "SystemProperties.get(persist.sys.PRJID) err !!! ");
			e.printStackTrace();
		}
		
		return mRet1;
	}

    /**
     * 获取ClientType 
     * @param int prjid，当前平台的ProjectID 
     * @return 返回当前平台ClientType
     */
	public String getClientType(int prjid) {
	
	/*
		String mRet = "";
		try {
			mRet = native_devinfo_getClientType(prjid);
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	*/
		
		//modify by gaodw
		String mRet = "";
		try {
			mRet = SystemProperties.get("ro.IPTV_DEV_ID");
		} catch (Exception e){
			e.printStackTrace();
		}
		
		if(mRet!=null && mRet.length()>0){
     		return mRet;
		}
		
		// try get clientype from /data/devinfo.txt
        String devmodel = "";
        try {
            FileReader read = new FileReader(new File("/data/devinfo.txt"));
            BufferedReader br = new BufferedReader(read);
            while ((devmodel = br.readLine()) != null) {
                if (devmodel.contains("devmodel")) {
                    // Log.i("我从文件中读到的device=类型号,全行", devmodel+"");
                    devmodel = devmodel.substring(devmodel.indexOf("=") + 1);
                    // Log.i("我从文件中读到的device类型号", devmodel+"");
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
        if ("000".equals(devmodel)) {
            devmodel = "";
        }		
		
		return devmodel;
	}

        /*
         * Model name be generated from project name.
         * ex. L43E5390A-3D(MT55) => L43E5390A-3D
         */
	public String getModelName(int prjid) {
            String name = "";
            try {
                String pname = SystemProperties.get("persist.sys.getprojectname");
                if (pname != null && !pname.equals("")) {
                    if (pname.indexOf('(') > -1) {
                        name = pname.split("[(]MT55")[0];
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(LOGI) Log.i(TAG, "Model Name : " + name);

            return name;
	}
	
	public int get3DStatus(int prjid) {
            int mRet = 0;
            String strRet = "";
            try {
                strRet = SystemProperties.get("persist.sys.three_D_status");
                mRet = Integer.parseInt( strRet.trim() );
            } catch (Exception e){
                e.printStackTrace();
            }
            return mRet;
	}
	
    public String getDeviceUUID() {
	/*
        String uuid = "";
        try {
            uuid = SystemProperties.get("persist.sys.UUID");
        } catch (Exception e) {
            e.printStackTrace();
        } 
	*/       

	 	    Slog.d(TAG, "Entry _readDeviceUUID() ..... \n ");
	 	    String  read_uuid="";
	 	    byte[] mbyteData=null;
		
	 	    mbyteData=configValueRead(ConfigType.CFG_STORE_UUID,40);
		    try{
			      if(mbyteData!=null){
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
		    for(int i=0;i<(value.getIntArrayValue().length);i++){
			      Slog.d(TAG, "configValueRead--->read eeprom data---is---\n" + data[i]);
		    }
		    */
		
		    int data1[]	= new int[length/2];
		    for(int i=0;i<(length/2);i++){
			      data1[i]=data[i];
			      // Slog.d(TAG, "configValueRead--->read eeprom data---is---\n" + data[i]);
			      // Slog.d(TAG, "configValueRead--->read eeprom data1---is---\n" + data1[i]);
		    }
		    return dataIntToShort(data1,length);
	  }
		
	  private byte[] dataIntToShort(int data[],int length){
		    Slog.d(TAG,"Entry dataIntToShort ......\n ");
		    byte[] byteData =new byte[length]; 
		    int intLength=length/2;
		    for(int i=0;i<intLength;i++){
			      byteData[i*2+1]=(byte) ((data[i]>>8)&0xff);
			      byteData[i*2]=(byte)(data[i]&0xff);
		    }
		    /*
		    for(int i=0;i<length;i++){
			      Slog.d(TAG, "dataIntToShort ---> data is " + byteData[i]);
		    }
		    */
		    try{
			      String dev_idStr="";
			      dev_idStr = new String(byteData,"UTF-8");
			      //dev_idStr = new String(byteData,"GBK");
			      Slog.d(TAG, "dataIntToShort---> devID data:  " + dev_idStr);
		    }catch(UnsupportedEncodingException e){
			      e.printStackTrace();
		    }
		    return byteData;	
	  }


    public boolean setDeviceUUID(String uuid) {
	_writeDeviceUUID(uuid);
        return true;
    }

	  private void _writeDeviceUUID(String deviceUUIDStr){
		    if(deviceUUIDStr!=null)
			      Slog.d(TAG, "Entry _writeDeviceUUID()---> deviceUUIDStr: \n"+deviceUUIDStr);
		    else{
			      Slog.e(TAG, "Entry _writeDeviceUUID()---> deviceUUIDStr: deviceUUIDStr=null");
			      return;
		    }		
		    short[]shortData=null;
		    byte[] mbytedata=deviceUUIDStr.getBytes();
		    int intArrayLength=mbytedata.length;
		    shortData =new short[intArrayLength];
		    for(int i=0;i<intArrayLength;i++){
			      shortData[i]= (short)( (mbytedata[i]) & (0x00ff) );
			  //Slog.d(TAG, "_writeDeviceUUID---> shortData  is :  " + shortData[i]);
		    }
		    int[]intData=dataChangeForEEP(shortData);
		    configValueSet(ConfigType.CFG_STORE_UUID,intData);
	  }


	  private  int[] dataChangeForEEP(short data[]){
		    Slog.d(TAG, "Entry dataChangeForEEP() ");	
		
		    int[] intData = null ;
		    int len=data.length;
		    int intArrayLength=len/2;
		    if(len%2==0){
			      intData =new int[intArrayLength];
			      for(int i=0;i<intArrayLength;i++){
				        intData[i]=((data[i*2+1]<<8)&0xff00)+data[i*2];
				        Slog.d(TAG, "dataChangeForEEP---> data is A :  " + intData[i]);
			      }
		    }
		    else{
			      intData =new int[intArrayLength+1];
			      for(int i=0;i<intArrayLength;i++){
				        intData[i]=(data[i*2+1]<<8)&0xff00+data[i*2];
				        //Slog.d(TAG, "dataChangeForEEP---> data is B:  " + intData[i]);
			      }
			      intData[intArrayLength-1]=data[len-1];
		    }
		    return intData;
	  }
	  /*
	   * Function: configValueSet(String  configType,int[] data)
	   */
	  private void configValueSet(String  configType,int[] data){	
		    ConfigValue value = new ConfigValue();
		    value.setIntArrayValue(data);
		    try {
			      configService.setCfg(configType,value);	
		    } catch (TVMException e) {
			      e.printStackTrace();
		    }
	  }

    /**
     * 获取平台的机型名称 
     * @param int prjid，当前平台的ProjectID
     * @return 返回平台的机型名称
     */
	/* 
	public String getModelName(int prjid) {
		String mRet = "";
		try {
			mRet = native_devinfo_getModelName(prjid);
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}*/

    /**
     * 获取屏的名称 
     * @param int prjid，当前平台的ProjectID
     * @return 返回电视使用的屏的名称
     */
	 /*
	public String getPanelName(int prjid) {
		String mRet = "";
		try {
			mRet = native_devinfo_getPanelName(prjid);
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}*/

    /**
     * 获取屏的类型
     * @param int prjid，当前平台的ProjectID
     * @return 返回屏的类型，4K2K返回：1， 2K1K返回：0
     */
	 /*
	public int getPanelType(int prjid) {
		int mRet = -1;
		try {
			mRet = native_devinfo_getPanelType(prjid);
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}*/

    /**
     * 获取电源的名称
     * @param int prjid，当前平台的ProjectID
     * @return 返回电源的名称
     */
	 /*
	public String getPSUName(int prjid) {
		String mRet = "";
		try {
			mRet = native_devinfo_getPSUName(prjid);
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}*/

    /**
     * 获取遥控器的名称
     * @param int prjid，当前平台的ProjectID
     * @return 返回遥控器的名称
     */
	 /*
	public String getRCUName(int prjid) {
		String mRet = "";
		try {
			mRet = native_devinfo_getRCUName(prjid);
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}*/

    /**
     * 判断当前平台支不支持3D功能
     * @param int prjid，当前平台的ProjectID
     * @return 支持返回：1， 否则返回：0
     */
	/* 
	public int get3DStatus(int prjid) {
		int mRet = -1;
		try {
			mRet = native_devinfo_get3DStatus(prjid);
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}
*/
    /**
     * 获取所使用的3D眼镜类型
     * @param int prjid，当前平台的ProjectID
     * @return 快门眼镜返回：1， 偏光眼镜返回：0
     */
	 /*
	public int get3DGlassesType(int prjid) {
		int mRet = -1;
		try {
			mRet = native_devinfo_get3DGlassesType(prjid);
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}
	*/

    /**
     * 判断支不支持Wifi功能
     * @param int prjid，当前平台的ProjectID
     * @return 支持返回：1，否则返回：0
     */
	 ///*
	public int getWifiStatus(int prjid) {
		int mRet = -1;
		try {
		//	mRet = native_devinfo_getWifiStatus(prjid);
			mRet = 1;

		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}//*/

    /**
     * 获取Wifi类型
     * @param int prjid，当前平台的ProjectID
     * @return 内置Wifi：1，外置Wifi返回：0
     */
	 ///*
	public int getWifiType(int prjid) {
		int mRet = -1;
		try {
			//mRet = native_devinfo_getWifiType(prjid);
			mRet =1;
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}
	//*/

    /**
     * 判断当前平台是否支持运动补偿功能
     * @param int prjid，当前平台的ProjectID
     * @return 支持返回：1，不支持返回：0
     */
	 /*
	public int getMemc(int prjid) {
		int mRet = -1;
		try {
			mRet = native_devinfo_getMemc(prjid);
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}*/

    /**
     * 判断工厂D模式是否打开
     * @param 无
     * @return 打开返回：<code>true</code>；否则返回：<code>false</code>
     */
	 /*
	public boolean getFactoryDModeStatus() {
		boolean mRet = false;
		try {
			mRet = (native_doGetAttribute(FACTORY_ATTR_D_MODE) == 0x01)?true:false;
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}*/

    /**
     * 判断工厂P模式是否打开
     * @param 无
     * @return 打开返回：<code>true</code>；否则返回：<code>false</code>
     */
	 /*
	public boolean getFactoryPModeStatus() {
		boolean mRet = false;
		try {
			mRet = (native_doGetAttribute(FACTORY_ATTR_P_MODE) == 0x01)?true:false;
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}
	*/

    /**
     * 判断工厂W模式是否打开
     * @param 无
     * @return 打开返回：<code>true</code>；否则返回：<code>false</code>
     */
	 /*
	public boolean getFactoryWModeStatus() {
		boolean mRet = false;
		try {
			mRet = (native_doGetAttribute(FACTORY_ATTR_WARM_UP_MODE) == 0x01)?true:false;
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}
	*/

    /**
     * 获取产品注册码
     * @param 无
     * @return 返回产品注册码
     */
	 ///*
	public String getRegistrationCode() {
		String mRet = "";
		try {
			//add by goadw.
			mRet = SystemProperties.get("persist.sys.RegistrationCode");			
			//end
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(LOGI) Log.i(TAG, "getRegistrationCode:" + mRet);
		return mRet;
	}

    /*
     * Get device SN from eeprom
     * SN : 22chars, addr in eeprom is oxcd6.
     * Set SN property in BatteryService when system start up.
     * @douzy
     */
    public String getSN() {
        String sn = "";
        try {
            sn = SystemProperties.get("persist.sys.SN");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sn;
    }

    /*
     * Get device SN from eeprom
     * SN : 22chars, addr in eeprom is oxcd6.
     * The same to getSN() for UserCenter3.0.
     */
    public String getSn() {
        return getSN();
    }


//	*/

	/**
     * 获取TV厂商名称
     * @param 无
     * @return 返回TV厂商名称，默认值为：TCL Multimedia<br>
     * TV厂商名称以“TCL+加公司名称”进行命名
     */
	 /*
	public String getTVCompany() {
		String mRet = "TCL Multimedia";
		try {
			mRet = SystemProperties.get("ro.build.company");
		} catch (Exception e){
			e.printStackTrace();
		}
		return mRet;
	}
	*/

    /**
     * 获取ProjectID数目列表
	 * <br>通过ProjectID数目列表可以知道有都少个ProjectID以及ProjiectID值是多少
     * @param 无
     * @return 返回：List<Integer>类型的ProjectID数目列表
     */
	 /*
	public List<Integer> getProjectList() {
		List<Integer> mRet = new ArrayList<Integer>();
		int mTemp[] = native_devinfo_getProjectList();
		for(int i=0;i<mTemp.length;i++){
			mRet.add(mTemp[i]);
		} 
		return mRet;
	}
	*/

    /**
     * 设置ProjectID
     * @param 需要设置的ProjectID值
     * @return 无
     */
	 /*
	public void setProjectID(int projectID) {
		try {
			native_doSetAttribute(FACTORY_ATTR_PROJ_ID, projectID);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	*/

    /**
     * 获取MAC地址
     * @param 无
     * @return 返回MAC地址
     */
	
	public String getMACAddress() {
		/*String mRet = "";
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(LOGI) Log.i(TAG, "getRegistrationCode:" + mRet);
		return mRet;*/
  /*

    short[] data = new short[6];

    try

    {

      data = readMACFromSpiFlash();

    }

    catch (TvCommonException e)

    {

      // TODO Auto-generated catch block

      e.printStackTrace();

    }

    return shortArraytoHexString(data);

    */

    

    // modify by liuwei
        String mac = null;
        try
        {
          android.net.ethernet.EthernetDevInfo eth = new android.net.ethernet.EthernetDevInfo();
          mac = eth.getMacAddress();
          Log.d("lw", "--------------- mac=" + mac);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        return (mac==null)?"":mac;
  }

	
	/**
     * 获取城市名称
     * @param 无
     * @return 返回城市名称
     */
	 /*
	public String getCityName() {
		File type = new File(TDeviceInfo.CITY_FILENAME);
		String mCityName = "";
		FileReader read;
		if(type.exists()&&type.canRead()){
			try {
				read = new FileReader(type);
				BufferedReader br = new BufferedReader(read);
				if (br.ready()) {
					mCityName = br.readLine();
					br.close();
					return mCityName;
				} else {
					if(LOGI) Log.i(TAG,"can't read /data/cityname.txt");
					return mCityName;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return mCityName;
			}
		}else{
			return mCityName;
		}
	}
	*/
	
	/**
     * 设置城市名称
     * @param 城市名称
     * @return 无
     */
	 /*
	public void setCityName(String mCityName) {
		FileWriter fw=null;
		try {
			fw = new FileWriter(TDeviceInfo.CITY_FILENAME);
			fw.write(mCityName);
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try{
				if(fw!=null){
					fw.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}  
	}

	private TDeviceInfo() {

	}
	
	private void createDeviceInfoFile(){
		int prjid = getProjectID();
		FileWriter fw=null;
		try {
			fw = new FileWriter(TDeviceInfo.DEVINFO_FILENAME);
			fw.write("devid=" + getDeviceID() + "\n");
			fw.write("sv=" + getSoftwareVersion() + "\n"); 
			fw.write("hv=" + getHardwareVersion() + "\n"); 
			fw.write("devmodel=" + getClientType(prjid) + "\n"); 
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try{
				if(fw!=null){
					fw.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}   
	}
	*/

	/*
	 *init generate devinfo.txt 
	 *@hide
	 */
	 
	 /*
	public void init(){
		try {
			//check device info database
			native_devinfo_checkDatabase();
			//create file "/data/devinfo.txt"
			createDeviceInfoFile();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	*/
	
	/*
	 * deviceinfo_jni natvie functions 
	 */
	 /*
	private static native void native_devinfo_checkDatabase();
	private static native String native_devinfo_getClientType(int projectID);
	private static native String native_devinfo_getModelName(int projectID);
	private static native String native_devinfo_getPanelName(int projectID);
	private static native int native_devinfo_getPanelType(int projectID);
	private static native String native_devinfo_getPSUName(int projectID);
	private static native String native_devinfo_getRCUName(int projectID);
	private static native int native_devinfo_get3DStatus(int projectID);
	private static native int native_devinfo_get3DGlassesType(int projectID);
	private static native int native_devinfo_getWifiStatus(int projectID);
	private static native int native_devinfo_getWifiType(int projectID);
	private static native int native_devinfo_getMemc(int projectID);
	private static native int[] native_devinfo_getProjectList();
*/
	/*
	 * com_tcl_tv_jni natvie functions 
	 */
	 
	 /*
	private static final int FACTORY_ATTR_PROJ_ID					= 0;
	private static final int FACTORY_ATTR_RED_GAIN					= 1;
	private static final int FACTORY_ATTR_GREEN_GAIN				= 2;
	private static final int FACTORY_ATTR_BLUE_GAIN					= 3; 
	private static final int FACTORY_ATTR_RED_OFFSET				= 4;
	private static final int FACTORY_ATTR_GREEN_OFFSET				= 5;
	private static final int FACTORY_ATTR_BLUE_OFFSET				= 6;
	private static final int FACTORY_ATTR_POWER_MODE				= 7;
	private static final int FACTORY_ATTR_HORIZON_POSITION			= 8;
	private static final int FACTORY_ATTR_VERTICAL_POSITION			= 9;
	private static final int FACTORY_ATTR_HORIZON_ZOOM_SIZE			= 10;
	private static final int FACTORY_ATTR_VERTICAL_ZOOM_SIZE		= 11;
	private static final int FACTORY_ATTR_MEMORY_SSC				= 12;
	private static final int FACTORY_ATTR_MEMORY_SPAN				= 13;
	private static final int FACTORY_ATTR_MEMORY_STEPS				= 14;
	private static final int FACTORY_ATTR_LVDS_SSC					= 15;
	private static final int FACTORY_ATTR_LVDS_SPAN					= 16;
	private static final int FACTORY_ATTR_LVDS_STEPS				= 17;
	private static final int FACTORY_ATTR_SI_TUNER_STATUS			= 18;
	private static final int FACTORY_ATTR_MAX_VOLUME				= 19;
	private static final int FACTORY_ATTR_PANEL_ROTATE_STATUS		= 20;
	private static final int FACTORY_ATTR_NATURE_LIGHT_BP			= 21;
	private static final int FACTORY_ATTR_NATURE_LIGHT_APL2			= 22;
	private static final int FACTORY_ATTR_NATURE_LIGHT_MODE			= 23;
	private static final int FACTORY_ATTR_NATURE_LIGHT_PRINT_ENABLE	= 24;
	private static final int FACTORY_ATTR_NATURE_LIGHT_VERBOSE_LEVEL= 25;
	private static final int FACTORY_ATTR_NATURE_LIGHT_SB			= 26;
	private static final int FACTORY_ATTR_NATURE_LIGHT_BKE			= 27;
	private static final int FACTORY_ATTR_LOGO_INDEX				= 28;
	private static final int FACTORY_ATTR_SIDE_PANEL_BUTTON			= 29;
	private static final int FACTORY_ATTR_REMOTE_CONTROL_BUTTON		= 30;
	private static final int FACTORY_ATTR_PANEL_KEY_LOCK_STATUS		= 31;
	private static final int FACTORY_ATTR_NATURE_LIGHT_DIMAP		= 32;
	private static final int FACTORY_ATTR_NATURE_LIGHT_ADIM_ENABLE	= 33;
	private static final int FACTORY_ATTR_NATURE_CHINA_DESCRAMBLER_BOX1 = 34;
	private static final int FACTORY_ATTR_NATURE_CHINA_DESCRAMBLER_BOX2 = 35;
	private static final int FACTORY_ATTR_NATURE_CHINA_DESCRAMBLER_BOX2_DELAY = 36;
	private static final int FACTORY_ATTR_NATURE_VBY1_DRIVING_CURRENT = 37;
	private static final int FACTORY_ATTR_AUTO_ADC_COLOR_R_GAIN  	 = 38;
	private static final int FACTORY_ATTR_AUTO_ADC_COLOR_G_GAIN		 = 39;
	private static final int FACTORY_ATTR_AUTO_ADC_COLOR_B_GAIN		 = 40;
	private static final int FACTORY_ATTR_AUTO_ADC_COLOR_R_OFFSET  	 = 41;
	private static final int FACTORY_ATTR_AUTO_ADC_COLOR_G_OFFSET	 = 42;
	private static final int FACTORY_ATTR_AUTO_ADC_COLOR_B_OFFSET	 = 43;
	private static final int FACTORY_ATTR_D_MODE                     = 44;
	private static final int FACTORY_ATTR_P_MODE                     = 45;
	private static final int FACTORY_ATTR_WARM_UP_MODE               = 46;
	private static final int FACTORY_ATTR_HOTEL_MODE                 = 47;
	private static final int FACTORY_ATTR_HOTEL_POWER_LOGO           = 48;
	private static final int FACTORY_ATTR_HOTEL_AUTO_SET             = 49;
	private static final int FACTORY_ATTR_HOTEL_CH_LOCK              = 50;
	private static final int FACTORY_ATTR_HOTEL_KEY_LOCK             = 51;
	private static final int FACTORY_ATTR_HOTEL_POWER_VOLUME         = 52;
	private static final int FACTORY_ATTR_HOTEL_SOURCE               = 53;
	private static final int FACTORY_ATTR_HOTEL_POWER_ATV_CH         = 54;
	private static final int FACTORY_ATTR_HOTEL_MAX_VOLUME           = 55;
	private static final int FACTORY_ATTR_HOTEL_PICTURE_MODE         = 56;
	private static final int FACTORY_ATTR_HOTEL_SOUND_MODE           = 57;
	private static final int FACTORY_ATTR_PSHOTKEY                   = 58;
	private static final int FACTORY_ATTR_AUTO_ADC_STATUS            = 59;
	*/
	
	/*
	private static native boolean native_Init0();
	private static native boolean native_finalize0();
	private static native int native_doGetAttribute(int attr);
	private static native boolean native_doSetAttribute(int attr, int value);
	private static native boolean native_doResetUserSettings();
	private static native boolean native_doCloneUSB2TV(int option);
	private static native boolean native_doCloneTV2USB(int option);
	private static native boolean native_doSetPatternRGB(int red, int green, int blue);
	private static native boolean native_doCleanChannelList();
	private static native int native_doPalPreSetChannel(int chNum, String chName, int freq, int color,int tvAudio);
	private static native void native_doOpenSerialPort();
	private static native void native_doCloseSerialPort();
	private static native void native_doOutSerialData(byte[] serialData);
	private static native int native_doGetCurve(int type, int index);
	private static native void native_doSetCurve(int type, int index, int value);
	private static native void native_doShellExecute(String cmd);
	private static native int[] native_doGetPanelPixel(int xpoint, int ypoint, int num);
	private static native String native_doGetBootCodeVersion();
	private static native boolean native_doSetAutoADCColor();
	
	*/
	
}
