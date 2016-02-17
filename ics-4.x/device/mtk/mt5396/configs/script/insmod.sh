#!/sbin/sh

insmod /snd-mtk.ko

until pgrep android.process.media > /dev/null ; do
    sleep 1
done

insmod /system/lib/modules/v4l2-int-device.ko
insmod /system/lib/modules/videodev.ko
insmod /system/lib/modules/uvcvideo.ko
insmod /system/lib/modules/v4l2-common.ko

insmod /system/lib/modules/fuse.ko
insmod /system/lib/modules/ntfs.ko


/sbin/sh /system/etc/script/init_wifi.sh
