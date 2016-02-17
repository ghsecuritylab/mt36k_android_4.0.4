package com.mediatek.tv.service;


import android.os.RemoteException;

import com.mediatek.tv.common.TVCommon;
import com.mediatek.tv.model.EventUpdateReason;
import com.mediatek.tv.model.HostControlReplace;
import com.mediatek.tv.model.HostControlTune;
import com.mediatek.tv.model.MMIEnq;
public class TVClientHandler extends ITVCallBack.Stub {
	private static final String TAG = "[J]TVClientHandler";

	public int notifyScanProgress(int progress, int channels) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "notifyScanProgress progress=" + progress + " channels=" + channels);
		ret = ScanService.setScanProgress(progress, channels);
		return ret;
	}

	public int notifyScanFrequence(int frequence) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "notifyScanFrequence frequence=" + frequence);
		ret = ScanService.setScanFrequence(frequence);
		return ret;
	}

	public int notifyScanCompleted() throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "notifyScanCompleted");
		ret = ScanService.setScanCompleted();
		return ret;
	}

	public int notifyScanCanceled() throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "notifyScanCanceled");
		ret = ScanService.setScanCanceled();
		return ret;
	}

	public int notifyScanError(int error) throws RemoteException {
		int ret = -1;
		Logger.d(TAG, "notifyScanError");
		ret = ScanService.setScanError(error);
		return ret;
	}
	
	public int notifyScanUserOperation(int currentFreq, int foundChNum, int finishData) throws RemoteException {
        int ret = -1;
        Logger.d(TAG, "notifyScanUserOperation");
        ret = ScanService.onScanUserOperation(currentFreq, foundChNum, finishData);
        return ret;
    }


	public void onUARTSerialListener(int uartSerialID, int ioNotifyCond, int eventCode, byte[] data)
	        throws RemoteException {
		Logger.d(TAG, "uartSerialID=" + uartSerialID);
		Logger.d(TAG, "ioNotifyCond=" + ioNotifyCond);
		Logger.d(TAG, "eventCode=" + eventCode);
		Logger.d(TAG, "data=" + data);

		ConfigService.onUARTSerialListener(uartSerialID, ioNotifyCond, eventCode, data);
	}

	public void notifyDT(int h_handle, int cond, int delta_time) throws RemoteException {

		BroadcastService.dtLisenter(h_handle, cond, delta_time);
	}

	public void onOperationDone(int output, boolean isSignalLoss) throws RemoteException {
		Logger.d(TAG, "output=" + output);
		Logger.d(TAG, "isSignalLoss=" + isSignalLoss);
		InputService.onOperationDone(output, isSignalLoss);
	}

	public void onSourceDetected(int inputId, int signalStatus) throws RemoteException {
		Logger.d(TAG, "inputId=" + inputId);
		InputService.onSourceDetected(inputId, signalStatus);
	}

	public void onOutputSignalStatus(int output, int signalStatus) throws RemoteException {
		Logger.d(TAG, "output=" + output);
		Logger.d(TAG, "signalStatus=" + signalStatus);
		InputService.onOutputSignalStatus(output, signalStatus);
	}

	public int camStatusUpdated(int slotId, byte cam_status) throws RemoteException {
		Logger.d(TAG, "slotId=" + slotId);
		Logger.d(TAG, "cam_status=" + cam_status);
		return CIService.getInstance(slotId).camStatusUpdated(cam_status);
	}

	public int camMMIEnqReceived(int slotId, MMIEnq enq) throws RemoteException {
		Logger.d(TAG, "slotId=" + slotId);
		return CIService.getInstance(slotId).camMMIEnqReceived(enq);
	}

	public int camMMIClosed(int slotId, byte mmi_close_delay) throws RemoteException {
		Logger.d(TAG, "slotId=" + slotId);
		Logger.d(TAG, "mmi_close_delay=" + mmi_close_delay);
		return CIService.getInstance(slotId).camMMIClosed(mmi_close_delay);
	}

	public int camHostControlTune(int slotId, HostControlTune tune_request) throws RemoteException {
		Logger.d(TAG, "slotId=" + slotId);
		return CIService.getInstance(slotId).camHostControlTune(tune_request);
	}

	public int camHostControlReplace(int slotId, HostControlReplace replace_request) throws RemoteException {
		Logger.d(TAG, "slotId=" + slotId);
		return CIService.getInstance(slotId).camHostControlReplace(replace_request);
	}

	public int camHostControlClearReplace(int slotId, byte refId) throws RemoteException {
		Logger.d(TAG, "slotId=" + slotId);
		Logger.d(TAG, "refId=" + refId);
		return CIService.getInstance(slotId).camHostControlClearReplace(refId);
	}

	public int camSystemIDStatusUpdated(int slotId, byte sysIdStatus) throws RemoteException {
		Logger.d(TAG, "slotId=" + slotId);
		Logger.d(TAG, "sysIdStatus=" + sysIdStatus);
		return CIService.getInstance(slotId).camSystemIDStatusUpdated(sysIdStatus);
	}

	public int camSystemIDInfoUpdated(int slotId, int[] systemIDInfo) throws RemoteException {
		Logger.d(TAG, "slotId=" + slotId);
		Logger.d(TAG, "systemIDInfo=" + systemIDInfo);
		return CIService.getInstance(slotId).camSystemIDInfoUpdated(systemIDInfo);
	}

	public int camMMIMenuReceived(int slotId, int menuId, byte choiceNum, String title, String subTitle, String bottom,
	        String[] itemlist) throws RemoteException {
		Logger.d(TAG, "camMMIMenuReceived,tv TVClientHandler class slotId=" + slotId);
		return CIService.getInstance(slotId).camMMIMenuReceived(menuId, choiceNum, title, subTitle, bottom, itemlist);

	}

	public void eventServiceNotifyUpdate(int reason, int svlId, int channelId) throws RemoteException {
		Logger.d(TAG, "reason=" + reason);
		Logger.d(TAG, "svlId=" + svlId);
		Logger.d(TAG, "channelId=" + channelId);
		EventUpdateReason eventUpdateReason = null;
		if (reason == 0) {
			eventUpdateReason = EventUpdateReason.EVENT_REASON_PF_UPDATE;
		} else if (reason == 1) {
			eventUpdateReason = EventUpdateReason.EVENT_REASON_SCHEDULE_UPDATE;
		}
		EventService.notifyUpdate(eventUpdateReason, svlId, channelId);
	}

	public void notifyChannelUpdated(int condition, int reason, int data) throws RemoteException {
		Logger.d(TAG, "condition=" + condition);
		Logger.d(TAG, "reason=" + reason);
		Logger.d(TAG, "data=" + data);
		ChannelService.notifyChannelUpdated(condition, reason, data);
	}

	public void notifyDbgLevel(int debugLevel) throws RemoteException {
		Logger.d(TAG, "debugLevel=" + debugLevel);
		TVCommon.debugLevel = debugLevel;
	}
   
	public void notifyCompInfo(String NotifyInfo)throws RemoteException {
		Logger.d(TAG, "notifyCompInfo Notify Info =" + NotifyInfo);
		ComponentService.notifyCompInfo(NotifyInfo);
	}
}
