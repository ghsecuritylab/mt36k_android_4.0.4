package com.mediatek.gtv;

import android.os.IBinder;
import android.os.RemoteException;

public class TVCommon {
	ITVCommon service;

	public TVCommon(IBinder service) {
		this.service = ITVCommon.Stub.asInterface(service);

	}

	public void select(int num) {
		try {
			service.select(num);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void popStop() {
		try {
			service.popStop();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void pushStop() {
		try {
			service.pushStop();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void changeInputSource(String outputName, String inputName) {
		try {
			service.changeInputSource(outputName, inputName);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void channelDown() {
		try {
			service.channelDown();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void channelUp() {
		try {
			service.channelUp();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void enterTV() {
		try {
			service.enterTV();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void leaveTV() {
		try {
			service.leaveTV();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getCurrInputSource(String outputName) {
		try {
			return service.getCurrInputSource(outputName);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String[] getInputSourceArray() {
		try {
			return service.getInputSourceArray();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getTypeFromInputSource(String inputSourceName) {
		try {
			return service.getTypeFromInputSource(inputSourceName);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean hasScramble() {
		try {
			return service.hasScramble();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean hasSignal() {
		try {
			return service.hasSignal();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void sendPowerOff() {
		try {
			service.sendPowerOff();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean setColorKey(boolean bEnabled, int color) {
		try {
			return service.setColorKey(bEnabled, color);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean setOpacity(int opacity) {
		try {
			return service.setOpacity(opacity);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public int getChannelsLength() {
		try {
			return service.getChannelsLength();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public String getCurrentChannelName() {
		try {
			return service.getCurrentChannelName();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public int getCurrentChannelNum() {
		try {
			return service.getCurrentChannelNum();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public static interface TVScanListener {
		public void onScanComplete();
	}

	public static class TVScanListenerDelegater extends
			ITVCommonScanListener.Stub {
		private TVScanListener clientListener;

		TVScanListenerDelegater(TVScanListener clientListener) {
			this.clientListener = clientListener;
		}

		public void onScanComplete() throws RemoteException {
			clientListener.onScanComplete();
		}

	}

	public void scan(TVScanListener listener) {
		try {
			service.scan(new TVScanListenerDelegater(listener));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class CommonIntegerRangeOption {
		ITVOption opt;

		CommonIntegerRangeOption(ITVOption opt) {
			this.opt = opt;
		}

		public int getMax() {
			try {
				return opt.getIntMax();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}

		public int getMin() {
			try {
				return opt.getIntMin();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}

		public int get() {
			try {
				return opt.getInt();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}

		public boolean set(Integer val) {
			try {
				return opt.setInt(val);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

	}

	public enum ConfigType {
		// TODO Add Config Option to here
		CFG_BRIGHTNESS("grp_video_brightness"), CFG_VOLUME("grp_audio_volume");
		private final String name;

		ConfigType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			if (name == null) {
				return super.toString();
			}
			return name;
		}
	}

	public CommonIntegerRangeOption getIntRangeOption(String name) {
		try {
			return new CommonIntegerRangeOption(service.getOption(name));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public CommonIntegerRangeOption getIntRangeOption(ConfigType name) {
		try {
			return new CommonIntegerRangeOption(service.getOption("" + name));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
