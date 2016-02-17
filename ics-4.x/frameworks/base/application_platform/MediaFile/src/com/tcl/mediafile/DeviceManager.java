package com.tcl.mediafile;

import java.util.List;

import android.content.Context;

public abstract class DeviceManager {
	abstract public List<Device> getDevices(Context ctx);
}
