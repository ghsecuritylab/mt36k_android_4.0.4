
移植说明
0. addon_stub_defs
	+com.tcl.deviceinfo.*

1. add class TDeviceInfo 
	dir:tvos_add-on_deviceid/modules/java/com/tcl/deviceinfo/TDeviceInfo.java

2. add libdeviceinfo_jni.so
	dir:tvos_add-on_deviceid/modules/jni/com_tcl_deviceinfo_TDeviceInfo.cpp

3. should copy deviceid_hal.h to hardware/libhardware/include/hardware/
	dir:tvos_add-on_deviceid/hardware/libhardware/include/hardware/deviceid_hal.h
