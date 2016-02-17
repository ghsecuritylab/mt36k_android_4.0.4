#!/sbin/sh
#
# Low priority service
# Only run after UI shown

source /system/etc/boot.conf

#until [ x`getprop dev.bootcomplete` == x"1" ] ; do
#    sleep 1
#done
#echo ""
#echo "==> Boot Complete"
#cat /proc/uptime
#echo ""

#until [ x`getprop init.svc.bootanim` == x"stopped" ] ; do
#    sleep 1
#done
#echo ""
#echo "==> Boot Animation Stopped"
#cat /proc/uptime
#echo ""

until pgrep android.process.media > /dev/null ; do
   sleep 1
done
echo "==> android.process.media start"
cat /proc/uptime
echo ""
 
#sleep 2

# enable drmserver
start drm

# disable mediascanner
# /sbin/sh /system/etc/script/disable_mediascanner.sh &
    
# PPPoE
/system/bin/pppoe-dial &

# sserver
/system/bin/sserver &
    
# flash init
/sbin/sh /system/etc/script/install_flash.sh &

# usb driver. make sure it's before other modules which use usb.
insmod /system/lib/modules/musb_hdrc.ko

#install diandu
/sbin/sh /system/etc/script/install_diandu.sh &
# Enable ADB
if $ENABLE_ADB_NET ; then
    /sbin/sh /etc/script/adb_net.sh
else
    /sbin/sh /etc/script/adb_usb.sh
fi

# add by zhanghangzhi for system_reapair.sh
if [ ! -e /data/system_repair.sh ]; then
    cp -a system/etc/service/system_repair.sh /data/
    chmod 777 /data/system_repair.sh
fi
# end

# Enable Crash handler
if $ENABLE_ANDROID_DEV; then
   setprop mtk.failed_reboot 0
#   logcat -v time -f /data/log.txt -r 8192 -n 1 &
else
   setprop mtk.failed_reboot 1
fi

if [ -d /data/anr/traces.txt ] ; then
   rmdir /data/anr/traces.txt
   touch /data/anr/traces.txt
fi

# Mount /cache
/sbin/sh /etc/script/mount_ubifs.sh

#detect RiptideGP's lib
if [ ! -e /data/data/com.vectorunit.bluetcl/lib/libBlue.so ] ; then
cp -a /system/lib/libBlue.so /data/data/com.vectorunit.bluetcl/lib/libBlue.so
cp -a /system/lib/libfmodevent.so /data/data/com.vectorunit.bluetcl/lib/libfmodevent.so
cp -a /system/lib/libfmodex.so /data/data/com.vectorunit.bluetcl/lib/libfmodex.so
echo "copy file libBlue.so, libfmodevent.so, libfmodex.so...."
fi

