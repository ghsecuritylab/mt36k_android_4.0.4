package com.mediatek.tvcm;

import android.os.RemoteException;

import com.mediatek.gtv.ITVCommonScanListener;

import com.mediatek.tv.common.TVCommon;
import com.mediatek.tvcm.TVScanner.ScannerListener;
import com.mediatek.gtv.ITVCommon;
import com.mediatek.gtv.ITVOption;

/**
 * 
 * This will provide necessary functions for third-part applications
 * 
 * @author mtk40063
 * 
 */

public class TVCommonService extends ITVCommon.Stub {

	TVContent cnt;

	TVCommonService(TVContent cnt) {
		this.cnt = cnt;
	}

	public void scan() throws RemoteException {
		cnt.getScanner().scan(null);

	}

	public void select(int num) throws RemoteException {
		cnt.getChannelSelector().select((short) num);
	}

	public static class TVCommonRangeOptionDelegate extends ITVOption.Stub {
		TVOption<?> opt;

		TVCommonRangeOptionDelegate(TVOption<?> opt) {
			this.opt = opt;
		}

		public int getInt() throws RemoteException {
			// TODO Auto-generated method stub
			return ((TVOption<Integer>) opt).get();
			// return null;
		}

		public boolean setInt(int val) throws RemoteException {
			// TODO Auto-generated method stub
			return ((TVOption<Integer>) opt).set(val);
		}

		public int getIntMax() throws RemoteException {
			return ((TVOptionRange<Integer>) opt).getMax();
		}

		public int getIntMin() throws RemoteException {
			return ((TVOptionRange<Integer>) opt).getMin();
		}

	}

	public ITVOption getOption(String name) throws RemoteException {
		// TODO Auto-generated method stub
		return new TVCommonRangeOptionDelegate(cnt.getConfigurer().getOption(
				name));
	}

	class TVCommonScanListenerDelegate implements ScannerListener {
		ITVCommonScanListener ipcListener;

		TVCommonScanListenerDelegate(ITVCommonScanListener ipcListener) {
			this.ipcListener = ipcListener;
		}

		public void onCompleted(int completeValue) {
			// TODO Auto-generated method stub
			try {
				ipcListener.onScanComplete();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onFrequence(int freq) {
			// TODO Auto-generated method stub

		}

		public void onProgress(int progress, int channels) {
			// TODO Auto-generated method stub

		}

	}

	public void scan(ITVCommonScanListener listener) throws RemoteException {
		cnt.getScanner().scan(new TVCommonScanListenerDelegate(listener));

	}

	public void popStop() throws RemoteException {
		cnt.popStop();

	}

	public void pushStop() throws RemoteException {
		cnt.pushStop();
	}

	public void changeInputSource(String outputName, String inputName)
			throws RemoteException {
		cnt.getInputManager().changeInputSource(outputName, inputName);
	}

	public void channelDown() throws RemoteException {
		cnt.getChannelSelector().channelDown();
	}

	public void channelUp() throws RemoteException {
		cnt.getChannelSelector().channelUp();
	}

	public void enterTV() throws RemoteException {
		cnt.enterTV();
	}

	public String getCurrInputSource(String outputName) throws RemoteException {
		return cnt.getInputManager().getCurrInputSource(outputName);
	}

	public String[] getInputSourceArray() throws RemoteException {
		return cnt.getInputManager().getInputSourceArray();
	}

	public String getTypeFromInputSource(String inputSourceName)
			throws RemoteException {
		return cnt.getInputManager().getTypeFromInputSource(inputSourceName);
	}

	public boolean hasScramble() throws RemoteException {
		return cnt.getChannelSelector().hasScramble();
	}

	public boolean hasSignal() throws RemoteException {
		return cnt.getChannelSelector().hasSignal();
	}

	public void leaveTV() throws RemoteException {
		cnt.leaveTV();
	}

	public void sendPowerOff() throws RemoteException {
		cnt.sendPowerOff();
	}

	public boolean setColorKey(boolean bEnabled, int color)
			throws RemoteException {
		return cnt.setColorKey(bEnabled, color);
	}

	public boolean setOpacity(int opacity) throws RemoteException {
		return cnt.setOpacity(opacity);
	}

	public int getChannelsLength() throws RemoteException {
		return cnt.getChannelManager().getChannels().size();
	}

	public String getCurrentChannelName() throws RemoteException {
		return cnt.getChannelSelector().getCurrentChannel().getChannelName();
	}

	public int getCurrentChannelNum() throws RemoteException {
		// TODO Auto-generated method stub
		return cnt.getChannelSelector().getCurrentChannel().getChannelNum();
	}

}
