package com.mediatek.tv.service;

import com.mediatek.tv.model.HostControlReplace;
import com.mediatek.tv.model.HostControlTune;
import com.mediatek.tv.model.MMIEnq;
import com.mediatek.tv.model.MMIMenu;
interface ITVCallBack {
	/*Channel serivce callback start*/
	void notifyChannelUpdated(int condition,int reason,int data);
	/*Channel serivce callback start*/


	/*Scan callback start*/
    int notifyScanProgress(int progress, int channels);
    int notifyScanFrequence(int frequence);
    int notifyScanCompleted();
    int notifyScanCanceled() ;
    int notifyScanError(int error);
    int notifyScanUserOperation(int currentFreq, int foundChNum, int finishData);
    /*Scan callback end*/
    
    /*Brdcst service start*/
    /*void nfySvctxMsgCB(String key,int data);*/
    /*Brdcst service end*/
    
    /*configure service start*/
    void onUARTSerialListener(int uartSerialID, int ioNotifyCond, int eventCode,in byte[] data);
    /*configure service end*/
    
    /*input service start*/
    void onOperationDone(int output, boolean isSignalLoss);
    void onSourceDetected(int inputId, int signalStatus);
    void onOutputSignalStatus(int output, int signalStatus);
    /*input service end*/
    
    /*ci service start*/
    int camStatusUpdated(int slotId,byte cam_status);
    int camMMIMenuReceived(int slotId,int menuId,byte choiceNum,in java.lang.String title,in java.lang.String subTitle,in java.lang.String bottom,in java.lang.String[] itemlist);
    int camMMIEnqReceived(int slotId,in MMIEnq enq);
    int camMMIClosed(int slotId,byte mmi_close_delay);
    int camHostControlTune(int slotId,in HostControlTune tune_request);
    int camHostControlReplace(int slotId,in HostControlReplace replace_request);
    int camHostControlClearReplace(int slotId,byte refId);
    int camSystemIDStatusUpdated(int slotId,byte sysIdStatus);
    int camSystemIDInfoUpdated(int slotId, in int[] arrInfo);
     /*ci service end*/
     
    /*Event service notify start*/ 
    void eventServiceNotifyUpdate(int reason,int svlid,int channelId);
    /*Event service notify end*/
 
    void notifyDT(int h_handle, int cond, int delta_time);
    
    
    void notifyDbgLevel(int debugLevel);
    
    /* for comp service start */
    void notifyCompInfo(String NotifyInfo);
}
