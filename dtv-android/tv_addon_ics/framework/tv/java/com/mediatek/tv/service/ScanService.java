package com.mediatek.tv.service;

import android.os.RemoteException;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.ScanExchange;
import com.mediatek.tv.model.ScanExchangeFrenquenceRange;
import com.mediatek.tv.model.ScanListener;
import com.mediatek.tv.model.ScanParaPalSecam;
import com.mediatek.tv.model.ScanParaDvbc;
import com.mediatek.tv.model.ScanParams;
import com.mediatek.tv.model.DvbcProgramType;
import com.mediatek.tv.model.DvbcFreqRange;
import com.mediatek.tv.model.ScanParaDtmb;
import com.mediatek.tv.model.DtmbFreqRange;
import com.mediatek.tv.model.DtmbScanRF;
import com.mediatek.tv.model.MainFrequence;

public class ScanService implements IService {
    public static String ScanServiceName = "ScanService";

    public static final int SCAN_RET_OK = 0;
    public static final int SCAN_RET_FAIL = -1;

    public static final String SCAN_MODE_PAL_SECAM_CABLE = "pal_secam-cable";
    public static final String SCAN_MODE_PAL_SECAM_TERRESTRIAL = "pal_secam-terrestrial";
    public static final String SCAN_MODE_DVB_CABLE = "dvb-cable";
    public static final String SCAN_MODE_DTMB_AIR  = "dtmb-air";
    
    // private class ScanServiceStateMechine {
    // public static final int SCAN_STATE_READY = 1;
    // public static final int SCAN_STATE_SCANING = 2;
    // public static final int SCAN_STATE_CANCELING = 3;
    // }
    protected static final String TAG = "[J]ScanService";
    protected static int scanServiceIdCounter = 10;
    protected static ScanService addedScanService;
    
    protected static final int SCAN_MODE_NUMERICAL_UNKNOWN = 0; // numerical
    protected static final int SCAN_MODE_NUMERICAL_PAL_SECAM_CABLE = SCAN_MODE_NUMERICAL_UNKNOWN + 1;
    protected static final int SCAN_MODE_NUMERICAL_PAL_SECAM_TERRESTRIAL = SCAN_MODE_NUMERICAL_PAL_SECAM_CABLE + 1;
    protected static final int SCAN_MODE_NUMERICAL_DVB_CABLE = SCAN_MODE_NUMERICAL_PAL_SECAM_TERRESTRIAL + 1;
    protected static final int SCAN_MODE_NUMERICAL_DTMB_AIR = SCAN_MODE_NUMERICAL_DVB_CABLE + 1;
    
    /* EXCHANG: get/set type ------------- start */
    protected static final int SCAN_EXCHANGE_HEADER_MODE_IDX = 0;
    protected static final int SCAN_EXCHANGE_HEADER_TYPE_IDX = SCAN_EXCHANGE_HEADER_MODE_IDX + 1;
    protected static final int SCAN_EXCHANGE_HEADER_LEN = SCAN_EXCHANGE_HEADER_TYPE_IDX + 1;
    
    protected static final int SCAN_GET_TYPE_UNKNOWN = 0;
    
    protected static final int SCAN_SET_TYPE_UNKNOWN = 300;
    protected static final int SCAN_SET_TYPE_USER_OPERATION_FINISH = SCAN_SET_TYPE_UNKNOWN + 1;
    /* EXCHANG: get/set type ------------- end */
        
    
    protected static void addScanService(ScanService scanService) {
        ScanService.addedScanService = scanService;
    }
    
    protected static int setScanProgress(int progress, int channels) {
        if (null != ScanService.addedScanService.appScanListener) {
            ScanService.addedScanService.appScanListener.setScanProgress(ScanService.addedScanService.scanMode,
                    progress, channels);
        }
        return 0;
    }

    protected static int setScanFrequence(int frequence) {
        if (null != ScanService.addedScanService.appScanListener) {
            ScanService.addedScanService.appScanListener.setScanFrequence(ScanService.addedScanService.scanMode,
                    frequence);
        }
        return 0;
    }

    /* private static Thread testThread = null;			test Analog SetUserOperation(part1/3). 
    													Because AP don't do this operation, we setup a testThread to help. 
														Notice. SetUserOperation should be open in PalScanParaPalSecam() for this test.(part2/3) */
														
    protected static int setScanCompleted() {
        if (null != ScanService.addedScanService.appScanListener) {
            ScanService.addedScanService.appScanListener.setScanCompleted(ScanService.addedScanService.scanMode);
        }
        return 0;
    }

    protected static int setScanCanceled() {
        if (null != ScanService.addedScanService.appScanListener) {
            ScanService.addedScanService.appScanListener.setScanCanceled(ScanService.addedScanService.scanMode);
        }
        return 0;
    }

    protected static int setScanError(int error) {
        if (null != ScanService.addedScanService.appScanListener) {
            ScanService.addedScanService.appScanListener.setScanError(ScanService.addedScanService.scanMode, error);
        }
        return 0;
    }
    
    protected static int onScanUserOperation(int currentFreq, int foundChNum, int finishData) {
        Logger.i(TAG, " onScanUserOperation: " + currentFreq + " : " + foundChNum + " : " + finishData);
        if (null != ScanService.addedScanService.atvScanUserOperationListener) {
            Logger.i(TAG, " onScanUserOperation: " + currentFreq + " : " + foundChNum + " : " + finishData);
            ScanService.addedScanService.atvScanUserOperationListener.onScanUserOperation(currentFreq, foundChNum, finishData);
        }

		/* for test setUserOperation of different handle: pal_secam_cable and pal_secam_terrestrial		part3/3 */
		/* final int Data = finishData;
		testThread = new Thread() {
			public void run() {
				TVManager tvManager = TVManager.getInstance(null);
				if ( null != tvManager){
					ScanService scanService = (ScanService) tvManager.getService(ScanService.ScanServiceName);
					try {
						System.out.println("TEST!!!! onScanUserOperation.  setAnalogScanUserOperationFinish  start");
						scanService.setAnalogScanUserOperationFinish(Data);
						System.out.println("TEST!!!! onScanUserOperation.  setAnalogScanUserOperationFinish  end");
					} catch (TVMException e) {
						e.printStackTrace();
					}
				}
			}
		};
		testThread.start();*/
		
        return 0;
    }

    protected void cleanAll() {
        this.appScanListener = null;
    }

    protected int scanServiceId;
    protected String serviceName;
    protected ScanListener appScanListener;
    protected ScanUserOperationListener atvScanUserOperationListener;
    protected String scanMode;

    protected ScanService() {
        scanServiceIdCounter = scanServiceIdCounter % 0xFFFFFF;
        if (0 == scanServiceIdCounter) {
            scanServiceIdCounter = 0;
        }
        this.scanServiceId = scanServiceIdCounter++;
        this.atvScanUserOperationListener = null;
        
        ScanService.addScanService(this);
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
        cleanAll();
    }

    public String getServiceName() {
        return serviceName;
    }
    
    /**
     * user operation listener. 
     * In some condition, the user of TVM / ATV scan need pause the progress after scan 
     * service find a analog channel and the video has been displayed. This interface is 
     * used to notify scan service user the found channel condition. 
     * 
     * @param currentFreq
     *            current frequency
     ** @param foundChNum
     *            total number of found channels
     ** @param finishData
     *            validation data between user and scan service. At first time, scan service
     *            will send it to user when she notify paused message. Then, user should return
     *            back the validation data for resume scan
     * @return void
     * @see setAnalogScanUserOperationFinish
     * @see setAnalogScanUserOperationListener
     */
    public interface ScanUserOperationListener {
        public void onScanUserOperation(int currentFreq, int foundChNum, int finishData);
    }
    
    /**
     * set user operation listener to scan service. 
     * In some condition, the user of TVM / ATV scan need pause the progress after scan 
     * service find a analog channel and the video has been displayed. This API is used to
     * set user operation listener to scan service. 
     * 
     * Scan service will notify user by the set listener after she has paused the scan 
     * progress
     * 
     * After user has finish private action, user have to call setAnalogScanUserOperationFinish 
     * to ask scan service resume scan.
     * 
     * @param scanUserOperationListener
     *            the actual listener
     * @return int
     * @see setAnalogScanUserOperationFinish
     * @see ScanUserOperationListener
     */    
    public int setAnalogScanUserOperationListener( ScanUserOperationListener scanUserOperationListener ){
        Logger.i(TAG, " setAnalogScanUserOperationListener");
        this.atvScanUserOperationListener = scanUserOperationListener;
        return ScanService.SCAN_RET_OK;
    }

    /**
     * get dvbc scan type (radio program number  digital tv number  app program number)
     * 
     * @param scanMode
     *            the scan mode name (SCAN_MODE_PAL_SECAM_CABLE,
     *            SCAN_MODE_PAL_SECAM_TERRESTRIAL, etc.)
     * @return DvbcScanDataType
     */
    public DvbcProgramType getDvbcProgramTypeNumber(String scanMode) {
        int ret = -1;
        DvbcProgramType programType = new DvbcProgramType();
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
            	if(scanMode.equals(SCAN_MODE_DVB_CABLE))
            	{
            		ret = service.getDvbcScanTypeNum_proxy(scanMode, programType);
            		System.out.println("get DvbcScanType Number  " + ret );
            	}
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return programType;
    }
    
    /**
     * get dvbc scan type (radio program number  digital tv number  app program number)
     * 
     * @param scanMode
     *            the scan mode name (SCAN_MODE_PAL_SECAM_CABLE,
     *            SCAN_MODE_PAL_SECAM_TERRESTRIAL, etc.)
     * @return DvbcScanDataType
     */
    public DvbcFreqRange getDvbcFreqRange(String scanMode) {
        int ret = -1;
        DvbcFreqRange freqRange = new DvbcFreqRange();
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
            	if(scanMode.equals(SCAN_MODE_DVB_CABLE))
            	{
            		ret = service.getDvbcFreqRange_proxy(scanMode, freqRange);
            		System.out.println("getDvbcFreqRange   " + ret );
            	}
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return freqRange;
    }

    
    /**
     * get dvbc main frequence (include main frequence & nit version &ts count)
     * 
     * @param scanMode
     *            the scan mode name (SCAN_MODE_PAL_SECAM_CABLE,
     *            SCAN_MODE_PAL_SECAM_TERRESTRIAL, etc.)
     * @return MainFrequence
     */
    public MainFrequence getDvbcMainFrequence(String scanMode) {
        int ret = -1;
        MainFrequence mainFrequence = new MainFrequence();
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
            	if(SCAN_MODE_DVB_CABLE == scanMode )
            	{
            		ret = service.getDvbcMainFrequence_proxy(scanMode, mainFrequence);
            		System.out.println("scanService.getDvbcMainFrequence  ret = " + ret + 
										" mainFreq: " + mainFrequence.getMainFrequence() + 
										" tsCount: "  + mainFrequence.getTsCount() +
										" nitVer: "   + mainFrequence.getNitVersion());
            	}
            } else {
				System.out.println("scanService.getDvbcMainFrequence service == null");
			}
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return mainFrequence;
    }


    /**
     * get dtmb freqency range
     * 
     * @param scanMode
     *            the scan mode name (SCAN_MODE_NUMERICAL_DTMB_AIR)
     * @return DtmbFreqRange
     */
    public DtmbFreqRange getDtmbFreqRange(String scanMode) {
        int ret = -1;
        DtmbFreqRange freqRange = new DtmbFreqRange();
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
            	if(scanMode.equals(SCAN_MODE_DTMB_AIR))
            	{
            		ret = service.getDtmbFreqRange_proxy(scanMode, freqRange);
            		System.out.println("getDtmbFreqRange   " + ret );
            	}
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return freqRange;
    }

    /**
      *get first dtmb scan RF
      *
      *@param scanMode
      *             the scan mode name (SCAN_MODE_NUMERICAL_DTMB_AIR)
      * @return DtmbScanRF
      */
    public DtmbScanRF getFirstDtmbScanRF(String scanMode)
    {
        int ret = -1;
        DtmbScanRF firstRF = new DtmbScanRF();
        try
        {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) 
            {
            	if(scanMode.equals(SCAN_MODE_DTMB_AIR))
            	{
            		ret = service.getFirstDtmbScanRF_proxy(scanMode, firstRF);
            		System.out.println("getFirstDtmbScanRF   " + ret );
            	}
            }
        }
        catch(RemoteException e)
        {
            e.printStackTrace();
        }
        return firstRF;
    }


    /**
      *get last dtmb scan RF
      *
      *@param scanMode
      *             the scan mode name (SCAN_MODE_NUMERICAL_DTMB_AIR)
      * @return DtmbScanRF
      */
    public DtmbScanRF getLastDtmbScanRF(String scanMode)
    {
        int ret = -1;
        DtmbScanRF lastRF = new DtmbScanRF();
        try
        {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) 
            {
            	if(scanMode.equals(SCAN_MODE_DTMB_AIR))
            	{
            		ret = service.getFirstDtmbScanRF_proxy(scanMode, lastRF);
            		System.out.println("getFirstDtmbScanRF   " + ret );
            	}
            }
        }
        catch(RemoteException e)
        {
            e.printStackTrace();
        }
        return lastRF;
    }

    
    /**
       *get next dtmb scan RF
       *
       *@param scanMode
       *             the scan mode name (SCAN_MODE_NUMERICAL_DTMB_AIR)
       *             scanMode
       *             current scan RF
       * @return DtmbScanRF
       */
    public DtmbScanRF getNextDtmbScanRF(String scanMode, DtmbScanRF currRF)
    {
        int ret = -1;
        DtmbScanRF nextRF = new DtmbScanRF();
        try
        {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) 
            {
                if(scanMode.equals(SCAN_MODE_DTMB_AIR))
                {
                    ret = service.getNextDtmbScanRF_proxy(scanMode, currRF, nextRF);
                    System.out.println("getFirstDtmbScanRF   " + ret );
                }
            }
        }
        catch(RemoteException e)
        {
            e.printStackTrace();
        }
        return nextRF;
    }

    /**
       *get prev dtmb scan RF
       *
       *@param scanMode
       *             the scan mode name (SCAN_MODE_NUMERICAL_DTMB_AIR)
       * @return DtmbScanRF
       */
    public DtmbScanRF getPrevDtmbScanRF(String scanMode, DtmbScanRF currRF)
    {
        int ret = -1;
        DtmbScanRF prevRF = new DtmbScanRF();
        try
        {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) 
            {
                if(scanMode.equals(SCAN_MODE_DTMB_AIR))
                {
                    ret = service.getPrevDtmbScanRF_proxy(scanMode, currRF, prevRF);
                    System.out.println("getFirstDtmbScanRF   " + ret );
                }
            }
        }
        catch(RemoteException e)
        {
            e.printStackTrace();
        }
        return prevRF;
    }

    public DtmbScanRF getCurrentDtmbScanRF(String scanMode, int channelId)
    {
        int ret = -1;
        DtmbScanRF currRF = new DtmbScanRF();
        try
        {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) 
            {
                if(scanMode.equals(SCAN_MODE_DTMB_AIR))
                {
                    ret = service.getCurrentDtmbScanRF_proxy(scanMode, channelId, currRF);
                    System.out.println("getFirstDtmbScanRF   " + ret );
                }
            }
        }
        catch(RemoteException e)
        {
            e.printStackTrace();
        }
        return currRF;
    }

    
    /**
     * Begin channel scan. Application should not call this API again when scan
     * service is scanning channels
     * 
     * <pre>
     * ------------------------simple code for startScan------------------------
     * ScanParaPalSecam scanParaPalSecam = new ScanParaPalSecam();           
     * MenuScanListener menuScanListener = new MenuScanListener();           
     *                                                                       
     * scanParaPalSecam.changeScanType(                                      
     *         ScanParaPalSecam.SB_PAL_SECAM_SCAN_TYPE_FULL_MODE, 0, 0);     
     *                                                                       
     * scanParaPalSecam.designateColorSystem(1, ChannelCommon.COLOR_SYS_PAL);
     *                                                                       
     * int retStartScan = scanService.startScan(                             
     *         ScanService.SCAN_MODE_PAL_SECAM_TERRESTRIAL, scanParaPalSecam,
     *         menuScanListener);                                            
     * if (ScanService.SCAN_RET_OK != retStartScan)                          
     * {                                                                     
     * }
     * </pre>
     * 
     * @param scanMode
     *            the scan mode name (SCAN_MODE_PAL_SECAM_CABLE,
     *            SCAN_MODE_PAL_SECAM_TERRESTRIAL, etc.)
     * @param scanParams
     *            scan configuration (scan type, etc.)
     * @param scanListener           
     *            notification function table. It is just an interface.
     *            Application should implement the actual class
     * @return int
     * @see ScanListener
     * @see ScanParams
     * @see com.mediatek.tv.model.ScanParaPalSecam
     */
    @SuppressWarnings("unused")
    public int startScan(String scanMode, ScanParams scanParams, ScanListener scanListener) {
        this.scanMode = scanMode;
        this.appScanListener = scanListener;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (scanParams instanceof ScanParaPalSecam) {//For analog
                if (service != null) {
                    int ret = service.startScan_pal_secam_proxy(scanMode, (ScanParaPalSecam)scanParams);
                }
            }
            if (scanParams instanceof ScanParaDvbc) {//For dvbc
                if (service != null) {
                    int ret = service.startScan_dvbc_proxy(scanMode, (ScanParaDvbc)scanParams);
                    System.out.println("---scan startScan_dvbc_proxy ");
                }
            }
            else if(scanParams instanceof ScanParaDtmb) //for dtmb
            {
                if(service != null)
                {
                    int ret = service.startScan_dtmb_proxy(scanMode, (ScanParaDtmb)scanParams);
                    System.out.println("---dtmb scan call startScan_dtmb_proxy");
                    System.out.println("dtmb scan parameters:" + scanParams);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return scanServiceId;
    }

    /**
     * Cancel channel scan. Application can only call this API when scan service
     * is scanning channels
     * 
     * @param scanMode
     *            the scan mode name (SCAN_MODE_PAL_SECAM_CABLE,
     *            SCAN_MODE_PAL_SECAM_TERRESTRIAL, etc.)
     * @return int
     */
    public int cancelScan(String scanMode) {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
            	if(scanMode.equals(SCAN_MODE_DVB_CABLE))
            	{					
            		ret = service.cancelScan_dvbc_proxy(scanMode);
            	}
                else if(scanMode.equals(SCAN_MODE_DTMB_AIR))
                {
                    ret = service.cancelScan_dtmb_proxy(scanMode);
                }
            	else
            	{
            		ret = service.cancelScan_proxy(scanMode);
            	}
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Get data of special scan mode. 
     * 
     * <pre>
     * ------------------------simple code for getScanData------------------------
     * ScanExchangeFrenquenceRange scanExchangeFrenquenceRange = new ScanExchangeFrenquenceRange();
     * int retgetScanData = scanService.getScanData(
     *      ScanService.SCAN_MODE_PAL_SECAM_TERRESTRIAL,
     *      ScanExchange.SCAN_GET_TYPE_TUNER_FREQUENCE_RANGE,
     *      scanExchangeFrenquenceRange);
     * if (ScanService.SCAN_RET_OK != retStartScan)
     * {
     * }
     * </pre>
     *  
     * @param scanMode
     *            the scan mode name (SCAN_MODE_PAL_SECAM_CABLE,
     *            SCAN_MODE_PAL_SECAM_TERRESTRIAL, etc.)
     * @param type
     *            the operator type. ScanExchange static elements contain all of
     *            the type
     * @param scanExchangeData       
     *            the temporary storage. It is just a super class. Different
     *            type Different detail scanExchangeData
     * @return int
     * 
     * @see ScanExchange
     */
    public int getScanData(String scanMode, int type, ScanExchangeFrenquenceRange scanExchangeData) {
        int ret = -1;
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.getScanData_proxy(scanMode, type, scanExchangeData);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    private int exchangeScanData(int scanMode, int exchangeType, int[] scanData) {
        int ret = ScanService.SCAN_RET_FAIL;
        int totalLen;
        int[] exchangeData;
        
        if (null == scanData){
            scanData = new int[1];
        }
        
        totalLen = ScanService.SCAN_EXCHANGE_HEADER_LEN + scanData.length;
        exchangeData = new int[totalLen];

        exchangeData[ScanService.SCAN_EXCHANGE_HEADER_MODE_IDX] = scanMode;
        exchangeData[ScanService.SCAN_EXCHANGE_HEADER_TYPE_IDX] = exchangeType;        
        for (int i = 0, j = ScanService.SCAN_EXCHANGE_HEADER_LEN; j < totalLen; i++, j++) {
            exchangeData[j] = scanData[i];
        }
        
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.scanExchangeData_proxy(exchangeData);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        for (int i = 0, j = ScanService.SCAN_EXCHANGE_HEADER_LEN; j < totalLen; i++, j++) {
            scanData[i] = exchangeData[j];
        }

        return ret;
    }
    
    /**
     * User operation finish. 
     * In some condition, the user of TVM / ATV scan need pause the progress after scan 
     * service find a analog channel and the video has been displayed. This API is used to
     * resume the scan progress.
     * 
     * User can enable the feature(pause/resume scan progress) in ScanParaPalSecam
     * 
     * After TVM / scan service notify user that she has paused the scan progress and user has
     * finish private action, user have to call this API to ask scan service resume scan.
     * 
     * <pre>
     *-----------------------simple code: how to enable the feature----------------------------
     * ScanParaPalSecam ScanParaPalSecam = new ScanParaPalSecam();
     * ...
     * ScanParaPalSecam.doUserOperationAfterChannelFound(true);
     * ...                                                                         
     * int retStartScan = scanService.startScan(                             
     *         ScanService.SCAN_MODE_PAL_SECAM_TERRESTRIAL, scanParaPalSecam,
     *         menuScanListener);                                            
     * if (ScanService.SCAN_RET_OK != retStartScan)                          
     * {                                                                     
     * }
     *-----------------------simple code: how to resume scan----------------------------
     * scanService.setAnalogScanUserOperationFinish( finishData );
     * </pre>
     * 
     * @param finishData
     *            validation data between user and scan service. At first time, scan service
     *            will send it to user when she notify paused message. Then, user should return
     *            back the validation data for resume scan
     * @return int
     * @see setAnalogScanUserOperationListener
     * @see ScanUserOperationListener
     */
    public int setAnalogScanUserOperationFinish(int finishData) throws TVMException {
        int ret = -1;
        int scanData[] = { finishData };
		int mode = (this.scanMode.equals(ScanService.SCAN_MODE_PAL_SECAM_TERRESTRIAL)  ?  ScanService.SCAN_MODE_NUMERICAL_PAL_SECAM_TERRESTRIAL : 
							(this.scanMode.equals(ScanService.SCAN_MODE_PAL_SECAM_CABLE)  ?  ScanService.SCAN_MODE_NUMERICAL_PAL_SECAM_CABLE : 
								ScanService.SCAN_MODE_NUMERICAL_UNKNOWN));
		 
		/* System.out.println("setAnalogScanUserOperationFinish  scanMode == " + this.scanMode + " mode == " + mode); */
        Logger.i(TAG, " setAnalogScanUserOperationFinish: " + finishData);

		if(ScanService.SCAN_MODE_NUMERICAL_UNKNOWN == mode){
			System.out.println("setAnalogScanUserOperationFinish Error Mode == " + mode);
			return ScanService.SCAN_RET_FAIL;
		}
		
        ret = exchangeScanData(
                mode,
                ScanService.SCAN_SET_TYPE_USER_OPERATION_FINISH,
                scanData);
        if (ret < 0) {
            throw new TVMException("setAnalogScanUserOperationFinish FAIL.");
        }
        
        return ScanService.SCAN_RET_OK;
    }
}
