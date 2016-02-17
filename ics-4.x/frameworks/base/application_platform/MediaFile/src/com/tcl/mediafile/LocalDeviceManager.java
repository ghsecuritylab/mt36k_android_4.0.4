package com.tcl.mediafile;

import java.util.List;
import android.content.Context;

public class LocalDeviceManager extends DeviceManager {
	@Override
	public List<Device> getDevices(Context ctx) {
		MstarDeviceManager manager = MstarDeviceManager.obtinInstance(ctx);
		return manager.getDevices(ctx);
	}
}
