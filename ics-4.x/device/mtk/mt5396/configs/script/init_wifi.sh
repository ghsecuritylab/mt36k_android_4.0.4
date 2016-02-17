#!/sbin/sh

if [ ! -s /data/misc/wifi/cfg80211.ko ] ; then
    cp -a /system/lib/modules/cfg80211.ko /data/misc/wifi/
fi

if [ ! -s /data/misc/wifi/mac80211.ko ] ; then
    cp -a /system/lib/modules/mac80211.ko /data/misc/wifi/
fi

if [ ! -s /data/misc/wifi/rfkill.ko ] ; then
    cp -a /system/lib/modules/rfkill.ko /data/misc/wifi/
fi

if [ ! -s /data/misc/wifi/rt2x00usb.ko ] ; then
    cp -a /system/lib/modules/rt2x00usb.ko /data/misc/wifi/
fi

if [ ! -s /data/misc/wifi/rt2800usb.ko ] ; then
    cp -a /system/lib/modules/rt2800usb.ko /data/misc/wifi/
fi

if [ ! -s /data/misc/wifi/rt2800lib.ko ] ; then
    cp -a /system/lib/modules/rt2800lib.ko /data/misc/wifi/
fi

if [ ! -s /data/misc/wifi/rfkill.ko ] ; then
    cp -a /system/lib/modules/rfkill.ko /data/misc/wifi/
fi

if [ ! -s /data/misc/wifi/8192cu.ko ] ; then
    cp -a /system/lib/modules/8192cu.ko /data/misc/wifi/
fi

if [ ! -s /data/misc/wifi/wpa_supplicant.conf ] ; then
    cp -a /system/etc/wifi/wpa_supplicant.conf /data/misc/wifi/
    chmod 777 /data/misc/wifi/wpa_supplicant.conf
fi

if [ ! -s /data/misc/wifi/wpa.conf ] ; then
    cp -a /system/etc/wifi/wpa_supplicant.conf /data/misc/wifi/wpa.conf
    chmod 777 /data/misc/wifi/wpa.conf
fi

if [ ! -s /data/misc/wifi/p2p.conf ] ; then
    cp -a /system/etc/wifi/p2p_supplicant.conf /data/misc/wifi/p2p.conf
    chmod 777 /data/misc/wifi/p2p.conf
fi

if [ ! -s /data/misc/wifi/rtutil5572sta.ko ] ; then
    cp -a /system/lib/modules/rtutil5572sta.ko /data/misc/wifi/rtutil5572sta.ko
fi

if [ ! -s /data/misc/wifi/rt5572sta.ko ] ; then
    cp -a /system/lib/modules/rt5572sta.ko /data/misc/wifi/rt5572sta.ko
fi

if [ ! -s /data/misc/wifi/rtnet5572sta.ko ] ; then
    cp -a /system/lib/modules/rtnet5572sta.ko /data/misc/wifi/rtnet5572sta.ko
fi

if [ ! -s /data/misc/wifi/rtutil5572ap.ko ] ; then
    cp -a /system/lib/modules/rtutil5572ap.ko /data/misc/wifi/rtutil5572ap.ko
fi

if [ ! -s /data/misc/wifi/rt5572ap.ko ] ; then
    cp -a /system/lib/modules/rt5572ap.ko /data/misc/wifi/rt5572ap.ko
fi

if [ ! -s /data/misc/wifi/rtnet5572ap.ko ] ; then
    cp -a /system/lib/modules/rtnet5572ap.ko /data/misc/wifi/rtnet5572ap.ko
fi
if [ ! -s /data/misc/wifi/adf.ko ] ; then
    cp -a /system/lib/modules/adf.ko /data/misc/wifi/adf.ko
fi

if [ ! -s /data/misc/wifi/asf.ko ] ; then
    cp -a /system/lib/modules/asf.ko /data/misc/wifi/asf.ko
fi

if [ ! -s /data/misc/wifi/ath_hif_usb.ko ] ; then
    cp -a /system/lib/modules/ath_hif_usb.ko /data/misc/wifi/ath_hif_usb.ko
fi

if [ ! -s /data/misc/wifi/ath_htc_hst.ko ] ; then
    cp -a /system/lib/modules/ath_htc_hst.ko /data/misc/wifi/ath_htc_hst.ko
fi

if [ ! -s /data/misc/wifi/ath_hal.ko ] ; then
    cp -a /system/lib/modules/ath_hal.ko /data/misc/wifi/ath_hal.ko
fi

if [ ! -s /data/misc/wifi/ath_dfs.ko ] ; then
    cp -a /system/lib/modules/ath_dfs.ko /data/misc/wifi/ath_dfs.ko
fi

if [ ! -s /data/misc/wifi/ath_rate_atheros.ko ] ; then
    cp -a /system/lib/modules/ath_rate_atheros.ko /data/misc/wifi/ath_rate_atheros.ko
fi

if [ ! -s /data/misc/wifi/ath_dev.ko ] ; then
    cp -a /system/lib/modules/ath_dev.ko /data/misc/wifi/ath_dev.ko
fi

if [ ! -s /data/misc/wifi/ath_pktlog.ko ] ; then
    cp -a /system/lib/modules/ath_pktlog.ko /data/misc/wifi/ath_pktlog.ko
fi

if [ ! -s /data/misc/wifi/umac.ko ] ; then
    cp -a /system/lib/modules/umac.ko /data/misc/wifi/umac.ko
fi

if [ ! -s /data/misc/wifi/ath_usbdrv.ko ] ; then
    cp -a /system/lib/modules/ath_usbdrv.ko /data/misc/wifi/ath_usbdrv.ko
fi
if [ ! -s /data/misc/wifi/8712u.ko ] ; then
    cp -a /system/lib/modules/8712u.ko /data/misc/wifi/8712u.ko
fi
#insmod /data/misc/wifi/rfkill.ko
#insmod /data/misc/wifi/cfg80211.ko
#insmod /data/misc/wifi/mac80211.ko
#insmod /data/misc/wifi/rt2x00lib.ko
#insmod /data/misc/wifi/rt2x00usb.ko
#insmod /data/misc/wifi/rt2800lib.ko
#insmod /data/misc/wifi/rt2800usb.ko 
#insmod /data/misc/wifi/8192cu.ko
#add by gaoyunpei for rt5372
#insmod /data/misc/wifi/rfkill.ko
#insmod /data/misc/wifi/cfg80211.ko
#insmod /data/misc/wifi/8192cu.ko
#add by gaoyunpei end
mkdir -p /lib/modules/3.0.13
