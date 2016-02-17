package com.mediatek.tv.service;

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

/**
 * This class provides TV Configuration Service
 * <ul>
 * 
 * </ul>
 */
public class ConfigService implements IService {

    private static final String TAG = "[J]CFG_SVC";

	public static String ConfigServiceName = "ConfigServiceName";
	
	private static final int mAllInputSource = 0xFF;
    private static final String mCustomConfigType = "custom_cfg";

	
    @SuppressWarnings("unused")
    private static UARTSerialListener m_UARTSerialListener;
    
    private static class InternalMapElement {
        public Integer m_Handle;
        public UARTSerialListener m_UartSerialListener;
        public InternalMapElement(Integer handle, UARTSerialListener uartSerialListener){
            m_Handle = handle;
            m_UartSerialListener = uartSerialListener;
        }
    }
    private static Map<Integer, InternalMapElement> internalMapTable;
    
    
	
    protected ConfigService() {
        internalMapTable = new HashMap<Integer, InternalMapElement>();
    }

	/* the common get/set/update method to process different type setting */
	/**
	 * do the get operation of tv configuration
     * 
	 * <pre>
     * ------------------------------------------------------------------------------------------------
     *            simple code get the PICTURE MODE of HDMI2, and check it is standard or not 
     * ------------------------------------------------------------------------------------------------
     * int hdmiNum = inputService.getDesignatedTypeInputsNum( InputService.INPUT_TYPE_HDMI );
     * String hdmis[] = inputService.getDesignatedTypeInputsString(InputService.INPUT_TYPE_HDMI);
     * 
     * int hdmiIdx = 2;
     * if (2 > hdmiNum) { 
     *     return; 
     * }
     * else {
     *     ConfigValue configValue = new ConfigValue();
     *     try{
     *          configValue = configService.getCfg(hdmis[1], ConfigType.CFG_PICTURE_MODE);
     *     } catch(TVMException e) {
     *         e.printStackTrace();
     *     }
     *     if (ConfigType.PICTURE_MODE_STANDARD == configValue.getIntValue())
     *     {
     *          System.out.println("Yeah, the picture is STANDARD");
     *     } 
     *     else
     *     {
     *          System.out.println("Ops, the picture is not STANDARD");
     *     }          
     * }
     * </pre>
     * 
	 * @param inputSource
	 *            indicate on which input source user do the operator.
	 * @param configType
	 *            indicate the type of configuration
	 * @return the value of the configuration
	 * 
	 * @see com.mediatek.tv.common.ConfigType 
	 * @see com.mediatek.tv.common.ConfigValue
	 */
    public ConfigValue getCfg(String inputSource, String configType) throws TVMException {
        if (inputSource == null){
			return null;
		}
		ConfigValue cfgValue = new ConfigValue();
		InputService inputService = (InputService)TVManager.getInstance().getService(InputService.InputServiceName);
		int inputSourceID = inputService.getInputIdByString(inputSource);
        int ret = -1;

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.getCfg_proxy(inputSourceID, configType, null, cfgValue);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
	    }

		Logger.i(TAG, "getCfg " +  configType + " w " + inputSource + " return " + ret);
		return cfgValue;
	}

	/**
     * do the get operation of tv configuration for current input source. It
     * also get the configuration which doesn't care the input soruce.
	 *  
	 * @param configType
	 *            indicate the type of configuration
	 * @return the value of the configuration
     * 
	 * @see com.mediatek.tv.common.ConfigType 
     * @see com.mediatek.tv.common.ConfigValue
	 */
    public ConfigValue getCfg(String configType) throws TVMException {
		ConfigValue cfgValue = new ConfigValue();		
        int ret = -1;

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.getCfg_proxy(mAllInputSource, configType, null, cfgValue);
                if((configType.equals(ConfigType.VINFO_MODEL_NAME)) ||(configType.equals(ConfigType.VINFO_SERIAL_NUM))
                		||(configType.equals(ConfigType.VINFO_VERSION)))
                {
                	byte[] bArray = cfgValue.getByteArrayValue();
					if(bArray != null)
					{
                		System.out.println("SerialNum size is  " + bArray.length);
					
		            	System.out.println("");
		            	for (int i = 0; i < bArray.length; ++i)
		            	{
		            		System.out.println(bArray[i]);
		            	}
		            	System.out.println("");
		                String str = new String(cfgValue.getByteArrayValue());	                    
                        cfgValue.setStringValue(str);
		            }
					else
					{
					    cfgValue.setStringValue(null);
					}
	
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

		Logger.i(TAG, "getCfg configType " + configType + " wo inputsource return " + ret);
		return cfgValue;
	}


	/* the common get/set/update method to process different type setting */
	/**
	 * do the get operation of tv configuration
     * 
	 * <pre>
     * ------------------------------------------------------------------------------------------------
     *            simple code get the PICTURE MODE of HDMI2, and check it is standard or not 
     * ------------------------------------------------------------------------------------------------
     * int hdmiNum = inputService.getDesignatedTypeInputsNum( InputService.INPUT_TYPE_HDMI );
     * String hdmis[] = inputService.getDesignatedTypeInputsString(InputService.INPUT_TYPE_HDMI);
     * 
     * int hdmiIdx = 2;
     * if (2 > hdmiNum) { 
     *     return; 
     * }
     * else {
     *     ConfigValue configValue = new ConfigValue();
     *     try{
     *          configValue = configService.getCfg(hdmis[1], ConfigType.CFG_PICTURE_MODE);
     *     } catch(TVMException e) {
     *         e.printStackTrace();
     *     }
     *     if (ConfigType.PICTURE_MODE_STANDARD == configValue.getIntValue())
     *     {
     *          System.out.println("Yeah, the picture is STANDARD");
     *     } 
     *     else
     *     {
     *          System.out.println("Ops, the picture is not STANDARD");
     *     }          
     * }
     * </pre>
     * 
	 * @param inputSource
	 *            indicate on which input source user do the operator.
	 * @param configType
	 *            indicate the type of configuration
	 * @return the value of the configuration
	 * 
	 * @see com.mediatek.tv.common.ConfigType 
	 * @see com.mediatek.tv.common.ConfigValue
	 */
    public ConfigValue getCustomCfg(int offset, int size) throws TVMException {
		ConfigValue cfgValue = new ConfigValue();
		int [] asize = new int[2];
		asize[0] = offset;
		asize[1] = size;
		cfgValue.setIntArrayValue(asize);
		
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.getCfg_proxy(mAllInputSource, mCustomConfigType, null, cfgValue);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

		Logger.i(TAG, "getCustomCfg offset = " + offset + " size = " + size + " return " + ret);
		return cfgValue;
	}  
	
	/**
	 * do the set operation of tv configuration
	 *
	 * <pre>
     * ------------------------------------------------------------------------------------------------
     *                           simple code set the PICTURE MODE of HDMI2 
     * ------------------------------------------------------------------------------------------------
     * int hdmiNum = inputService.getDesignatedTypeInputsNum( InputService.INPUT_TYPE_HDMI );
     * String hdmis[] = inputService.getDesignatedTypeInputsString(InputService.INPUT_TYPE_HDMI);
     * 
     * int hdmiIdx = 2;
     * if (2 > hdmiNum) { 
     *     return; 
     * }
     * else {
     *     ConfigValue configValue = new ConfigValue();
     *     try{
     *          configValue.setIntValue(ConfigType.PICTURE_MODE_STANDARD);
     *          configService.setCfg(hdmis[1], ConfigType.CFG_PICTURE_MODE, configValue);
     *     } catch(TVMException e) {
     *         e.printStackTrace();
     *     }          
     * }
     * </pre>
     *
	 * @param inputSource
	 *            indicate on which input source user do the operator.
	 *            
	 * @param configType
	 *            indicate the type of configuration
	 *            
	 * @param configValue
	 *            contains the value to be set.
	 *            
	 * @return 
	 * 
	 * @see com.mediatek.tv.common.ConfigType 
     * @see com.mediatek.tv.common.ConfigValue
	 */
    public void setCfg(String inputSource, String configType, ConfigValue configValue) throws TVMException {
        if (inputSource == null){
			return;
		}
		InputService inputService = (InputService)TVManager.getInstance().getService(InputService.InputServiceName);
        
		int inputSourceID = inputService.getInputIdByString(inputSource);
        int ret = -1;

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setCfg_proxy(inputSourceID, configType, configValue);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		Logger.i(TAG, "setCfg " + configType + " w " + inputSource + " return " + ret);
	}

	/**
     * do the set operation of tv configuration for current input source. It
     * also set the configuration which doesn't care the input soruce.
     *  
     *  @param configType
	 *            indicate the type of configuration
	 *            
	 * @param configValue
	 *            contains the value to be set.
	 *            
	 * @return 
	 * 
	 * @see com.mediatek.com.common.ConfigType
	 * @see com.mediatek.com.common.ConfigValue
	 */
    public void setCfg(String configType, ConfigValue configValue) throws TVMException {
        int ret = -1;

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setCfg_proxy(mAllInputSource, configType, configValue);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		Logger.i(TAG, "setCfg " + configType + " wo inputsource return " + ret);
	}

	/**
		 * do the set operation of tv configuration
		 *
		 * <pre>
		 * ------------------------------------------------------------------------------------------------
		 *							 simple code set the PICTURE MODE of HDMI2 
		 * ------------------------------------------------------------------------------------------------
		 * int hdmiNum = inputService.getDesignatedTypeInputsNum( InputService.INPUT_TYPE_HDMI );
		 * String hdmis[] = inputService.getDesignatedTypeInputsString(InputService.INPUT_TYPE_HDMI);
		 * 
		 * int hdmiIdx = 2;
		 * if (2 > hdmiNum) { 
		 *	   return; 
		 * }
		 * else {
		 *	   ConfigValue configValue = new ConfigValue();
		 *	   try{
		 *			configValue.setIntValue(ConfigType.PICTURE_MODE_STANDARD);
		 *			configService.setCfg(hdmis[1], ConfigType.CFG_PICTURE_MODE, configValue);
		 *	   } catch(TVMException e) {
		 *		   e.printStackTrace();
		 *	   }		  
		 * }
		 * </pre>
		 *
		 * @param inputSource
		 *			  indicate on which input source user do the operator.
		 *			  
		 * @param configType
		 *			  indicate the type of configuration
		 *			  
		 * @param configValue
		 *			  contains the value to be set.
		 *			  
		 * @return 
		 * 
		 * @see com.mediatek.tv.common.ConfigType 
		 * @see com.mediatek.tv.common.ConfigValue
		 */
		public void setCustomCfg(int offset, byte [] data) throws TVMException {
			ConfigValue cfgValue = new ConfigValue();

			cfgValue.setByteArrayValue(data);
			cfgValue.setIntValue(offset);
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setCfg_proxy(mAllInputSource, mCustomConfigType, cfgValue);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
			Logger.i(TAG, "setCustomCfg offset = " + offset + " size = " + data.length + " return " + ret);
		}

	/**
	 * do the update operation of tv configuration
	 * 
	 * <pre>
     * ------------------------------------------------------------------------------------------------
     *                     simple code to update the PICTURE MODE of current source 
     * ------------------------------------------------------------------------------------------------
     * try{
     *      configService.updateCfg(ConfigType.CFG_PICTURE_MODE);
     * } catch(TVMException e) {
     *      e.printStackTrace();
     * }
     * </pre>
     *            
     * @param configType
     *            indicate the type of configuration
     *            
     * @return 
     * 
     * @see com.mediatek.tv.common.ConfigType 
     * @see com.mediatek.tv.common.ConfigValue
     */
	public void updateCfg(String configType) throws TVMException {
        int ret = -1;

		 if(configType.equals(ConfigType.CFG_VOLUME)){
             return;	
         }
		
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.updateCfg_proxy(configType);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

		Logger.i(TAG, "updateCfg " + configType + " return " + ret);
        return; /* update_proxy(tvc_type); */
	}

	/**
	 * do the reset operation of tv configuration
	 * 
     * <pre>
     * ------------------------------------------------------------------------------------------------
     *                     simple code to reset the configuration to user 
     * ------------------------------------------------------------------------------------------------
     * try{
     *      configService.resetCfgGroup(ConfigType.RESET_USER);
     * } catch(TVMException e) {
     *      e.printStackTrace();
     * }
     * </pre>
     * 
	 * @param reset_type
	 *            the type of the reset operation
	 *            
	 * @return 
	 * @see com.mediatek.tv.common.ConfigType 
	 */
	public void resetCfgGroup(String resetType) throws TVMException {
        @SuppressWarnings("unused")
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.resetCfgGroup_proxy(resetType);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		Logger.i(TAG, "resetCfgGroup " + " return " + ret);
	}

    /**
     * do the get min max value operation of tv configuration
     * 
     * <pre>
     * ------------------------------------------------------------------------------------------------
     *                     simple code to get the min/max value of configuration 
     * ------------------------------------------------------------------------------------------------
     * try{
     *      configService.getCfgMinMax(ConfigType.CFG_PICTURE_MODE);
     * } catch(TVMException e) {
     *      e.printStackTrace();
     * }
     * </pre>
     * 
     * @param configType
     *            the type of the configType(note only part type could do this
     *            operation)
     *            
     * @param configValue
     *            contains the gotten min/max value .
     *            
     * @return 
     * @see com.mediatek.tv.common.ConfigType
     * @see com.mediatek.tv.common.ConfigValue  
     */
	public void getCfgMinMax(String configType, ConfigValue configValue) throws TVMException {
        @SuppressWarnings("unused")
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.getCfgMinMax_proxy(configType, configValue);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		Logger.i(TAG, "getCfgMinMax " + " return " + ret);
		return; 
	}
	
    /**
     * process the power off flow of TV
     * 
     * <pre>
     * ------------------------------------------------------------------------------------------------
     *                     simple code to call this interface 
     * ------------------------------------------------------------------------------------------------
     * try{
     *      configService.powerOff();
     * } catch(TVMException e) {
     *      e.printStackTrace();
     * }
     * </pre>
     *          
     */
    public void powerOff() throws TVMException {
        @SuppressWarnings("unused")
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.powerOff_proxy();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		Logger.i(TAG, "powerOff " + " return " + ret);
        return; 
    }
    
	/* the special method to do special operation */
	/**
	 * do the read gpio operation
	 * 
     * <pre>
     * ------------------------------------------------------------------------------------------------
     *                     simple code to read GPIO value 
     * ------------------------------------------------------------------------------------------------
     * ConfigValue configValue = new ConfigValue();
     * configValue.setGpioID(0x1);
     * configValue.setGpioMask(0x8F);
     * try{
     *      configService.readGPIO(configValue);
     * } catch(TVMException e) {
     *      e.printStackTrace();
     * }
     * </pre>
     *  
	 * @param gpioValue
     *            the input/output value of parameter (input gpio id and mask,
     *            output gpio value)
	 * @return 
	 * 
	 * @see com.mediatek.tv.common.ConfigValue 
	 */
	public void readGPIO(ConfigValue gpioValue) throws TVMException {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.readGPIO_proxy(gpioValue);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		Logger.i(TAG, "readGPIO return " + ret);
		return; /* readGPIO(output_value); */
	}

	/**
	 * do the write gpio operation
	 * 
     * <pre>
     * ------------------------------------------------------------------------------------------------
     *                     simple code to write GPIO value 
     * ------------------------------------------------------------------------------------------------
     * ConfigValue configValue = new ConfigValue();
     * configValue.setGpioID(0x1);
     * configValue.setGpioMask(0x8F);
     * configValue.setGpioValue(0x10);
     * try{
     *      configService.writeGPIO(configValue);
     * } catch(TVMException e) {
     *      e.printStackTrace();
     * }
     * </pre>
     *  
     * @param gpioValue
     *            the input gpio attribute.
     * @return 
     * 
     * @see com.mediatek.tv.common.ConfigValue
     */
	public void writeGPIO(ConfigValue gpioValue) throws TVMException {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.writeGPIO_proxy(gpioValue);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		Logger.i(TAG, "writeGPIO return " + ret);
        return; /* writeGPIO_proxy(input_value); */
	}

	/**
	*@deprecated
 	*change autoAdjust to input source
	 * do the auto adjust of vga
	 * 
     * <pre>
     * ------------------------------------------------------------------------------------------------
     *                     simple code to write GPIO value 
     * ------------------------------------------------------------------------------------------------
     * try{
     *      configService.autoVgaAdjust();
     * } catch(TVMException e) {
     *      e.printStackTrace();
     * }
     * </pre>
     *  
	 * @return 
	 */
	public void autoAdjust(String autoType) throws TVMException {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.autoAdjust_proxy(autoType);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
	    Logger.i(TAG, "autoAdjust return " + ret);
		return;
	}

	protected static void onUARTSerialListener(int uartSerialID, int ioNotifyCond, int eventCode, byte[] data){
        Logger.i(TAG, "receive the data notify" + uartSerialID + " " + ioNotifyCond + " " + eventCode);
        if (!internalMapTable.containsKey(new Integer(uartSerialID)))
        {
            Logger.i(TAG, "the " + uartSerialID + " serial has been opend");
            return;
        }
        InternalMapElement internalMapElement = internalMapTable.get(new Integer(uartSerialID));
        if (null != internalMapElement.m_UartSerialListener){           
            internalMapElement.m_UartSerialListener.onUARTSerialNfy(ioNotifyCond, eventCode, data);
        }       
    }
    
    /**
     * do the open operation of UART Serial Port
     * <pre>
     * 
     * @param uartSerialID
     *            indicate the uart serial porting id.
     * @param uartSerialSetting
     *            the integer array contains the serial port data
     *            the length of this array is 4
     *            the data of index 0 is speed setting
     *            the data of index 1 is data length setting
     *            the data of index 2 is parity setting
     *            the data of index 3 is stop bit setting
     * @param uartSerialListener
     *            the listener to receive the uart serial port data
     * @return void
     * 
     */
    public void openUARTSerial(int uartSerialID, int[] uartSerialSetting, UARTSerialListener uartSerialListener)throws TVMException{
        int ret = 0;
        int[] handle = new int[1];
        
        if (internalMapTable.containsKey(new Integer(uartSerialID)))
        {
            Logger.i(TAG, "the " + uartSerialID + " serial has been opend");
            return;
        }       
        Logger.i(TAG, "enter openUARTSerial");
        
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.openUARTSerial_proxy(uartSerialID, uartSerialSetting, handle);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        Logger.i(TAG, "openUARTSerial_native return " + ret);
        
        if (0 != ret){
            throw new TVMException("Open UARTSerial return " + ret);
        }
        Logger.i(TAG, "openUART get the handle 0x" + Integer.toHexString(handle[0]));
        internalMapTable.put(new Integer(uartSerialID), new InternalMapElement(new Integer(handle[0]), uartSerialListener));
    }
    
    /**
     * do the close operation of UART Serial Port
     * <pre>
     * 
     * @param uartSerialID
     *            indicate the uart serial porting id
     * @return void 
     */
    public void closeUARTSerial(int uartSerialID)throws TVMException{
        int ret = 0;
        if (!internalMapTable.containsKey(new Integer(uartSerialID)))
        {
            Logger.i(TAG, "the " + uartSerialID + " serial has not been found");
            throw new TVMException("the " + uartSerialID + " serial has not been found");
        }    
        InternalMapElement internalMapElement = internalMapTable.get(new Integer(uartSerialID));
        Logger.i(TAG, "closeUARTSerial the handle 0x" + Integer.toHexString(internalMapElement.m_Handle));
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.closeUARTSerial_proxy(internalMapElement.m_Handle);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        if (0 != ret){
            throw new TVMException("Close UARTSerial return " + ret);
        }
        internalMapTable.remove(new Integer(uartSerialID));
    }
    
    /**
     * do the get setting operation of UART Serial Port
     * <pre>
     * 
     * @param uartSerialID
     *            indicate the uart serial porting id.
     * return uartSerialSetting
     *            the integer array contains the serial port data
     *            the length of this array is 4
     *            the data of index 0 is speed setting
     *            the data of index 1 is data length setting
     *            the data of index 2 is parity setting
     *            the data of index 3 is stop bit setting
     */
    public int[] getUARTSerialSetting(int uartSerialID)throws TVMException{
        int ret = 0;
        if (!internalMapTable.containsKey(new Integer(uartSerialID)))
        {
            Logger.i(TAG, "the " + uartSerialID + " serial has not been found");
            throw new TVMException("the " + uartSerialID + " serial has not been found");
        }
        int[] uartSerialSetting = new int[4];
        InternalMapElement internalMapElement = internalMapTable.get(new Integer(uartSerialID));
        Logger.i(TAG, "getUARTSerialSetting the handle 0x" + Integer.toHexString(internalMapElement.m_Handle));
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.getUARTSerialSetting_proxy(internalMapElement.m_Handle, uartSerialSetting);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        if (0 != ret){
            throw new TVMException("Get UARTSerial settring return " + ret);
        }
        
        return uartSerialSetting;
    }
    
    /**
     * do the get operation mode operation of UART Serial Port
     * <pre>
     * @param uartSerialID
     *            indicate the uart serial porting id.
     * return operation mode
     */
    public int getUARTSerialOperationMode(int uartSerialID)throws TVMException{
        int ret = 0;
        if (!internalMapTable.containsKey(new Integer(uartSerialID)))
        {
            Logger.i(TAG, "the " + uartSerialID + " serial has not been found");
            throw new TVMException("the " + uartSerialID + " serial has not been found");
        }
        int[] operationMode = new int[1];
        InternalMapElement internalMapElement = internalMapTable.get(new Integer(uartSerialID));
        Logger.i(TAG, "getUARTSerialOperationMode the handle 0x" + Integer.toHexString(internalMapElement.m_Handle));
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.getUARTSerialOperationMode_proxy(internalMapElement.m_Handle, operationMode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        if (0 != ret){
            throw new TVMException("Get UARTSerial OperationMode return " + ret);
        }
        return operationMode[0];
    }
    
    /**
     * do the set setting operation of UART Serial Port
     * <pre>
     * @param uartSerialID
     *            indicate the uart serial porting id.
     * @param uartSerialSetting
     *            the integer array contains the serial port data
     *            the length of this array is 4
     *            the data of index 0 is speed setting
     *            the data of index 1 is data length setting
     *            the data of index 2 is parity setting
     *            the data of index 3 is stop bit setting
     */
    public void setUARTSerialSetting(int uartSerialID, int[] uartSerialSetting)throws TVMException{
        int ret = 0;
        if (!internalMapTable.containsKey(new Integer(uartSerialID)))
        {
            Logger.i(TAG, "the " + uartSerialID + " serial has not been found");
            throw new TVMException("the " + uartSerialID + " serial has not been found");
        }
        InternalMapElement internalMapElement = internalMapTable.get(new Integer(uartSerialID));
        Logger.i(TAG, "setUARTSerialSetting the handle 0x" + Integer.toHexString(internalMapElement.m_Handle));
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setUARTSerialSetting_proxy(internalMapElement.m_Handle, uartSerialSetting);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (0 != ret){
            throw new TVMException("Set UARTSerial Setting return " + ret);
        }
        
        return;
    }
    
    /**
     * do the set operation mode operation of UART Serial Port
     * <pre>
     * @param uartSerialID
     *            indicate the uart serial porting id.
     * @param operationMode
     */
    public void setUARTSerialOperationMode(int uartSerialID, int operationMode)throws TVMException{
        int ret = 0;
        if (!internalMapTable.containsKey(new Integer(uartSerialID)))
        {
            Logger.i(TAG, "the " + uartSerialID + " serial has not been found");
            throw new TVMException("the " + uartSerialID + " serial has not been found");
        }
        InternalMapElement internalMapElement = internalMapTable.get(new Integer(uartSerialID));
        Logger.i(TAG, "setUARTSerialOperationMode the handle 0x" + Integer.toHexString(internalMapElement.m_Handle));
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setUARTSerialOperationMode_proxy(internalMapElement.m_Handle, operationMode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (0 != ret){
            throw new TVMException("Set UARTSerial OperationMode return " + ret);
        }
    }
    
    /**
     * do the set magic string operation of UART Serial Port
     * <pre>
     * @param uartSerialID
     *            indicate the uart serial porting id.
     * @param uartSerialMagicSetting
     *            the string length should be 1 currently.
     */
    public void setUARTSerialMagicString(int uartSerialID, byte[] uartSerialMagicSetting)throws TVMException{
        int ret = 0;
        if (!internalMapTable.containsKey(new Integer(uartSerialID)))
        {
            Logger.i(TAG, "the " + uartSerialID + " serial has not been found");
            throw new TVMException("the " + uartSerialID + " serial has not been found");
        }
        InternalMapElement internalMapElement = internalMapTable.get(new Integer(uartSerialID));
        Logger.i(TAG, "setUARTSerialMagicString the handle 0x" + Integer.toHexString(internalMapElement.m_Handle));
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setUARTSerialMagicString_proxy(internalMapElement.m_Handle, uartSerialMagicSetting);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (0 != ret){
            throw new TVMException("Set UARTSerial MagicString return " + ret);
        }
    }
    
    /**
     * do the output operation of UART Serial Port
     * <pre>
     * @param uartSerialID
     *            indicate the uart serial porting id.
     * @param uartSerialData
     *            the string to be output.
     */
    public void outputUARTSerial(int uartSerialID, byte[] uartSerialData)throws TVMException{
        int ret = 0;
        if (!internalMapTable.containsKey(new Integer(uartSerialID)))
        {
            Logger.i(TAG, "the " + uartSerialID + " serial has not been found");
            throw new TVMException("the " + uartSerialID + " serial has not been found");
        }
        InternalMapElement internalMapElement = internalMapTable.get(new Integer(uartSerialID));
        Logger.i(TAG, "outputUARTSerial the handle 0x" + Integer.toHexString(internalMapElement.m_Handle));
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.outputUARTSerial_proxy(internalMapElement.m_Handle, uartSerialData);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (0 != ret){
            throw new TVMException("Output UARTSerial Setting return " + ret);
        }
    }
    
    

    /**
     * do the get operation of tv configuration for current input source. It
     * also get the configuration which doesn't care the input soruce.
     *  
     * @param configType
     *            indicate the type of configuration
     * @return the value of the configuration
     * 
     * @see com.mediatek.tv.common.ConfigType 
     * @see com.mediatek.tv.common.ConfigValue
     */
    public ConfigValue getDrvCfg(String dInterfaceType, ConfigValue drvCfgParams ) throws TVMException {
        int ret = -1;
        ConfigValue gottenValue = new ConfigValue();
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.getCfg_proxy( mAllInputSource, dInterfaceType, drvCfgParams, gottenValue);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Logger.i(TAG, "getDrvCfg dInterfaceType " + dInterfaceType + " return " + ret);
        return gottenValue;
    }

    /**
     * do the get operation of tv configuration for current input source. It
     * also get the configuration which doesn't care the input soruce.
     *  
     * @param configType
     *            indicate the type of configuration
     * @return the value of the configuration
     * 
     * @see com.mediatek.tv.common.ConfigType 
     * @see com.mediatek.tv.common.ConfigValue
     */
    public void setDrvCfg(String dInterfaceType, ConfigValue drvCfgParams ) throws TVMException {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.setCfg_proxy(mAllInputSource, dInterfaceType, drvCfgParams);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Logger.i(TAG, "setDrvCfg dInterfaceType " + dInterfaceType + " return " + ret);
        return;
    }    
}
