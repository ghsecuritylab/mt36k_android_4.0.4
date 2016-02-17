package com.mediatek.tv.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.mediatek.tv.service.BroadcastService;
import com.mediatek.tv.service.ComponentService;

public class TVCallBack {
	private static final String TAG = "[J]TVCallBack";
	private static final RemoteCallbackList<ITVCallBack> clientCallbacks = new RemoteCallbackList<ITVCallBack>();
	private boolean callBackDebug = true;
	private List<String> callBacks = new ArrayList<String>();
	private static Context mContext;

	public TVCallBack() {
		super();
	}

	public static void setContext(Context context){
		TVCallBack.mContext = context;
	}

	public void destroyCallBack() {
		Logger.d(TAG, "destroyCallBack:");
		if (callBackDebug) {
			for (int i = 0; i < callBacks.size(); i++) {
				Logger.d(TAG, callBacks.get(i).toString());
			}
		}
		clientCallbacks.kill();
	}

	public void registerCallback(ITVCallBack cb) {
		Logger.d(TAG, "registerCallback:" + cb);
		if (cb != null) {
			clientCallbacks.register(cb);
			if (callBackDebug) {
				callBacks.add(cb.toString());
			}
		}
	}

	public void unregisterCallback(ITVCallBack cb) {
		Logger.d(TAG, "unregisterCallback:" + cb);
		if (cb != null) {
			clientCallbacks.unregister(cb);
			if (callBackDebug) {
				callBacks.remove(cb.toString());
			}
		}
	}

	protected static int notifyScanProgress(int progress, int channels) {
		Logger.i(TAG, "notifyScanProgress progress=" + progress + " channels=" + channels);

		int ret = 0;
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).notifyScanProgress(progress, channels);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}

	protected static int notifyScanFrequence(int frequence) {
		Logger.i(TAG, "notifyScanFrequence frequence=" + frequence);

		int ret = 0;
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).notifyScanFrequence(frequence);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}

	protected static int notifyScanCompleted() {
		Logger.i(TAG, "notifyScanCompleted");

		int ret = 0;
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).notifyScanCompleted();
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}

	protected static int notifyScanCanceled() {
		Logger.i(TAG, "notifyScanCanceled");
		int ret = 0;
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).notifyScanCanceled();
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}

	protected static int notifyScanError(int error) {
		Logger.i(TAG, "notifyScanError error=" + error);
		int ret = 0;
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).notifyScanError(error);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}
	
	protected static int notifyScanUserOperation(int currentFreq, int foundChNum, int finishData){
        Logger.i(TAG, "notifyScanUserOperation: " + currentFreq + "," + foundChNum + "," + finishData);
        int ret = 0;
        synchronized (TVCallBack.class) {
            final int N = clientCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    ret = clientCallbacks.getBroadcastItem(i).notifyScanUserOperation(currentFreq, foundChNum, finishData);
                } catch (RemoteException e) {
                }
            }
            clientCallbacks.finishBroadcast();
        }
        return ret;
    }

	                       
	protected static void nfySvctxMsgCB(String nfyMsgCode, int nfyData)  {
		try {
			Intent intent = new Intent(BroadcastService.ACTION_SVCTX_NFY);
			intent.putExtra(BroadcastService.SVCTX_NFY_CODE, nfyMsgCode);

			Logger.i(TAG, "receive msg : " + nfyMsgCode);

			if (nfyMsgCode.equals(BroadcastService.SVCTX_NFY_AUDIO_AND_VIDEO_SCRAMBLED)){
				intent.putExtra(BroadcastService.SVCTX_NFY_DATA_AUDIO_VIDEO_ABNORMAL, BroadcastService.SVCTX_NFY_AUDIO_AND_VIDEO_SCRAMBLED);			
			}
			if (nfyMsgCode.equals(BroadcastService.SVCTX_NFY_AUDIO_CLEAR_VIDEO_SCRAMBLED)){
				intent.putExtra(BroadcastService.SVCTX_NFY_DATA_AUDIO_VIDEO_ABNORMAL, BroadcastService.SVCTX_NFY_AUDIO_CLEAR_VIDEO_SCRAMBLED);			
			}
			if (nfyMsgCode.equals(BroadcastService.SVCTX_NFY_AUDIO_NO_VIDEO_SCRAMBLED)){
				intent.putExtra(BroadcastService.SVCTX_NFY_DATA_AUDIO_VIDEO_ABNORMAL, BroadcastService.SVCTX_NFY_AUDIO_NO_VIDEO_SCRAMBLED);			
			}
			if (nfyMsgCode.equals(BroadcastService.SVCTX_NFY_VIDEO_CLEAR_AUDIO_SCRAMBLED)){
				intent.putExtra(BroadcastService.SVCTX_NFY_DATA_AUDIO_VIDEO_ABNORMAL, BroadcastService.SVCTX_NFY_VIDEO_CLEAR_AUDIO_SCRAMBLED);			
			}
			if (nfyMsgCode.equals(BroadcastService.SVCTX_NFY_VIDEO_NO_AUDIO_SCRAMBLED)){
				intent.putExtra(BroadcastService.SVCTX_NFY_DATA_AUDIO_VIDEO_ABNORMAL, BroadcastService.SVCTX_NFY_VIDEO_NO_AUDIO_SCRAMBLED);			
			}
			
			if (nfyMsgCode.equals(BroadcastService.SVCTX_NFY_STREAM_OPENED)){
				intent.putExtra(BroadcastService.SVCTX_NFY_STREAM_OPENED, BroadcastService.SVCTX_NFY_STREAM_OPENED); 		
			}
			
			if (nfyMsgCode.equals(BroadcastService.SVCTX_NFY_STREAM_STARTED)){
				intent.putExtra(BroadcastService.SVCTX_NFY_STREAM_STARTED, BroadcastService.SVCTX_NFY_STREAM_STARTED); 		
			}
			
			if (nfyMsgCode.equals(BroadcastService.SVCTX_NFY_VIDEO_UPDATED)){
				intent.putExtra(BroadcastService.SVCTX_NFY_AUDIO_VIDEO_NORMAL, BroadcastService.SVCTX_NFY_VIDEO_UPDATED); 		
			}
			if (nfyMsgCode.equals(BroadcastService.SVCTX_NFY_AUDIO_UPDATED)){
				intent.putExtra(BroadcastService.SVCTX_NFY_AUDIO_VIDEO_NORMAL, BroadcastService.SVCTX_NFY_AUDIO_UPDATED); 		
			}

			if (nfyMsgCode.equals(BroadcastService.SVCTX_NFY_AUDIO_ONLY)){
				intent.putExtra(BroadcastService.SVCTX_NFY_DATA_AUDIO_VIDEO_ABNORMAL, BroadcastService.SVCTX_NFY_AUDIO_ONLY);			
			}
			if (nfyMsgCode.equals(BroadcastService.SVCTX_NFY_VIDEO_ONLY)){
				intent.putExtra(BroadcastService.SVCTX_NFY_DATA_AUDIO_VIDEO_ABNORMAL, BroadcastService.SVCTX_NFY_VIDEO_ONLY);			
			}
			if (nfyMsgCode.equals(BroadcastService.SVCTX_NFY_NO_AUDIO_VIDEO)){
				intent.putExtra(BroadcastService.SVCTX_NFY_DATA_AUDIO_VIDEO_ABNORMAL, BroadcastService.SVCTX_NFY_NO_AUDIO_VIDEO);			
			}
			if (mContext != null)
	        mContext.sendBroadcast(intent);
		}catch (Exception e)	{
		
		}
			
	}

	protected static void onUARTSerialListener(int uartSerialID, int ioNotifyCond, int eventCode, byte[] data) {
		Logger.d(TAG, "uartSerialID=" + uartSerialID);
		Logger.d(TAG, "ioNotifyCond=" + ioNotifyCond);
		Logger.d(TAG, "eventCode=" + eventCode);
		Logger.d(TAG, "data=" + data);
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					clientCallbacks.getBroadcastItem(i).onUARTSerialListener(uartSerialID, ioNotifyCond, eventCode,
					        data);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
	}

	protected static void onOperationDone(int output, boolean isSignalLoss) {
		Logger.d(TAG, "output=" + output);
		Logger.d(TAG, "isSignalLoss=" + isSignalLoss);
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					clientCallbacks.getBroadcastItem(i).onOperationDone(output, isSignalLoss);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
	}

	protected static void onSourceDetected(int inputId, int signalStatus) {
		Logger.d(TAG, "inputId=" + inputId);
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					clientCallbacks.getBroadcastItem(i).onSourceDetected(inputId, signalStatus);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
	}

	protected static void onOutputSignalStatus(int output, int signalStatus) {
		Logger.d(TAG, "output=" + output);
		Logger.d(TAG, "signalStatus=" + signalStatus);
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					clientCallbacks.getBroadcastItem(i).onOutputSignalStatus(output, signalStatus);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
	}

    
	protected static void notifyDT(int h_handle, int cond, int delta_time) {

		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					clientCallbacks.getBroadcastItem(i).notifyDT(h_handle, cond, delta_time);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
	}

	protected static int camStatusUpdated(int slotId, byte cam_status) {
		int ret = 0;
		Logger.i(TAG, "camStatusUpdated");
		Logger.d(TAG, "slot id=" + slotId);
		Logger.d(TAG, "cam statue=" + cam_status);
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).camStatusUpdated(slotId, cam_status);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}

	protected static int camMMIMenuReceived(int slotId, int menuId, byte choiceNum, java.lang.String title,
	        java.lang.String subTitle, java.lang.String bottom, java.lang.String[] itemlist) {
		int ret = 0;
		Logger.i(TAG, "camMMIMenuReceived");
		Logger.d(TAG, "slot id=" + slotId);
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).camMMIMenuReceived(slotId, menuId, choiceNum, title,
					        subTitle, bottom, itemlist);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}

	protected static int camMMIEnqReceived(int slotId, com.mediatek.tv.model.MMIEnq enq) {
		int ret = 0;
		Logger.i(TAG, "camMMIEnqReceived");
		Logger.d(TAG, "slot id=" + slotId);
		Logger.d(TAG, "MMI enq=" + enq);
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).camMMIEnqReceived(slotId, enq);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}

	protected static int camMMIClosed(int slotId, byte mmi_close_delay) {
		int ret = 0;
		Logger.i(TAG, "camMMIClosed");
		Logger.d(TAG, "slot id=" + slotId);
		Logger.d(TAG, "mmi close delay=" + mmi_close_delay);
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).camMMIClosed(slotId, mmi_close_delay);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}

	protected static int camHostControlTune(int slotId, com.mediatek.tv.model.HostControlTune tune_request) {
		int ret = 0;
		Logger.i(TAG, "camHostControlTune");
		Logger.d(TAG, "slot id=" + slotId);
		Logger.d(TAG, "tune request=" + tune_request);
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).camHostControlTune(slotId, tune_request);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}

	protected static int camHostControlReplace(int slotId, com.mediatek.tv.model.HostControlReplace replace_request) {
		int ret = 0;
		Logger.i(TAG, "camHostControlReplace");
		Logger.d(TAG, "slot id=" + slotId);
		Logger.d(TAG, "replace request=" + replace_request);
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).camHostControlReplace(slotId, replace_request);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}

	protected static int camHostControlClearReplace(int slotId, byte refId) {
		int ret = 0;
		Logger.i(TAG, "camHostControlClearReplace");
		Logger.d(TAG, "slot id=" + slotId);
		Logger.d(TAG, "replace request=" + refId);
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).camHostControlClearReplace(slotId, refId);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}

	protected static int camSystemIDStatusUpdated(int slotId, byte sysIdStatus) {
		int ret = 0;
		Logger.i(TAG, "camSystemIDStatusUpdated");
		Logger.d(TAG, "slot id=" + slotId);
		Logger.d(TAG, "systemIdStatus =" + sysIdStatus);
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).camSystemIDStatusUpdated(slotId, sysIdStatus);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}

	protected static int camSystemIDInfoUpdated(int slotId, int[] arrInfo) {
		int ret = 0;
		Logger.i(TAG, "camSystemIDInfoUpdated");
		Logger.d(TAG, "slot id=" + slotId);
		Logger.d(TAG, "systemIdInfo =" + arrInfo);
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					ret = clientCallbacks.getBroadcastItem(i).camSystemIDInfoUpdated(slotId, arrInfo);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
		return ret;
	}

	protected static void eventServiceNotifyUpdate(int reason, int svlId, int channelId) throws RemoteException {
		Logger.i(TAG, "eventServiceNotifyUpdate");
		Logger.d(TAG, "reason" + reason);
		Logger.d(TAG, "svlId =" + svlId);
		Logger.d(TAG, "channelId =" + channelId);

		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					clientCallbacks.getBroadcastItem(i).eventServiceNotifyUpdate(reason, svlId, channelId);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
	}
	
	protected static void channelServiceNotifyUpdate(int condition, int reason, int data) throws RemoteException {
		Logger.i(TAG, "channelServiceNotifyUpdate");
		Logger.d(TAG, "condition" + condition);
		Logger.d(TAG, "reason =" + reason);
		Logger.d(TAG, "data =" + data);

		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					clientCallbacks.getBroadcastItem(i).notifyChannelUpdated(condition, reason, data);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
	}
	
	protected static void configServiceNotifyDbgLevel(int dbgLevel) throws RemoteException {
		Logger.i(TAG, "configServiceNotifyDbgLevel");
		Logger.d(TAG, "dbgLevel" + dbgLevel);

		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					clientCallbacks.getBroadcastItem(i).notifyDbgLevel(dbgLevel);
				} catch (RemoteException e) {
				}
			}
			clientCallbacks.finishBroadcast();
		}
	}
	protected static void compServiceNotifyInfo(String ntfyInfo) throws RemoteException {
		Logger.i(TAG, "compServiceNotifyInfo Info" + ntfyInfo);
	
		synchronized (TVCallBack.class) {
			final int N = clientCallbacks.beginBroadcast();
			for (int i = 0; i < N; i++) {
				try {
					clientCallbacks.getBroadcastItem(i).notifyCompInfo(ntfyInfo);
				} catch (RemoteException e) {
				}			}
			clientCallbacks.finishBroadcast();
		}

	}
}
