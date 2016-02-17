#!/sbin/sh

insmod /system/lib/modules/android.ko

setprop service.adb.tcp.port -1
setprop persist.sys.usb.config adb
stop adbd
start adbd
