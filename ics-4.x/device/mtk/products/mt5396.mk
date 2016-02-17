#
# Copyright (C) 2011 The Android Open-Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# This file is the build configuration for a full Android
# build for mt5396 hardware. This cleanly combines a set of
# device-specific aspects (drivers) with a device-agnostic
# product configuration (apps). 
#

TARGET_PROVIDES_INIT_RC := true
PREBUIT_PATH := device/mtk/mt5396/prebuilt
DTV_OUT_PATH := device/mtk/mt5396/DTV_OUT


ifeq "ROM2EMMC" "$(BOOT_TYPE)"
ifeq "1080p" "$(OSD_RESOLUTION)"
PRODUCT_COPY_FILES := device/mtk/mt5396/configs/init_for_emmc_boot_osd1080.rc:root/init.rc
else
PRODUCT_COPY_FILES := device/mtk/mt5396/configs/init_for_emmc_boot.rc:root/init.rc
endif
PRODUCT_COPY_FILES += device/mtk/mt5396/configs/exmount_emmc.cfg:root/exmount.cfg
else
ifneq ($(NAND_BOOT),false)
ifeq "1080p" "$(OSD_RESOLUTION)"
PRODUCT_COPY_FILES := device/mtk/mt5396/configs/init_for_nand_boot_osd1080.rc:root/init.rc
else
PRODUCT_COPY_FILES := device/mtk/mt5396/configs/init_for_nand_boot.rc:root/init.rc
endif
PRODUCT_COPY_FILES += device/mtk/mt5396/configs/exmount.cfg:root/exmount.cfg
else
PRODUCT_COPY_FILES := device/mtk/mt5396/configs/init_for_usb_disk.rc:root/init.rc
PRODUCT_COPY_FILES += device/mtk/mt5396/configs/exmount.cfg:root/exmount.cfg
endif
endif

#use customized target ueventd.rc to replace original one
PRODUCT_COPY_FILES += device/mtk/mt5396/configs/ueventd.rc:root/ueventd.rc

#Target init scripts 
PRODUCT_COPY_FILES += \
    device/mtk/mt5396/configs/init.mt5396.rc:root/init.mt5396.rc \
    device/mtk/mt5396/configs/boot.conf:system/etc/boot.conf \
    device/mtk/mt5396/configs/script/mount_ubifs.sh:system/etc/script/mount_ubifs.sh \
    device/mtk/mt5396/configs/script/adb_net.sh:system/etc/script/adb_net.sh \
    device/mtk/mt5396/configs/script/insmod.sh:system/etc/script/insmod.sh \
    device/mtk/mt5396/configs/script/set_eth0_mac.sh:system/etc/script/set_eth0_mac.sh \
    device/mtk/mt5396/configs/script/disable_mediascanner.sh:system/etc/script/disable_mediascanner.sh \
    device/mtk/mt5396/configs/script/mknode.sh:system/etc/script/mknode.sh \
    device/mtk/mt5396/configs/script/install_diandu.sh:system/etc/script/install_diandu.sh \
    device/mtk/mt5396/configs/service/post-init.sh:system/etc/service/post-init.sh \
    device/mtk/mt5396/configs/service/dtv_svc.sh:system/etc/service/dtv_svc.sh \
    device/mtk/mt5396/configs/service/pre-init.sh:system/etc/service/pre-init.sh \
    device/mtk/mt5396/configs/service/system_repair.sh:system/etc/service/system_repair.sh \
    device/mtk/mt5396/configs/mtk_bugreport.sh:system/bin/mtk_bugreport.sh \
    device/mtk/mt5396/configs/script/unmount.sh:root/unmount.sh \
	device/mtk/mt5396/configs/script/fbm_mode_2_android.sh:system/bin/fbm_mode_2_android.sh \
	device/mtk/mt5396/configs/script/fbm_mode_1_tvmm.sh:system/bin/fbm_mode_1_tvmm.sh \
	device/mtk/mt5396/configs/appconfig.xml:system/etc/appinfo/appconfig.xml \

# quicklaunch
ifeq ($(BLCR),true)
PRODUCT_COPY_FILES += \
	device/mtk/mt5396/configs/zygote-blcr.rc:root/zygote.rc \
	device/mtk/mt5396/configs/quicklaunch.sh:system/bin/quicklaunch.sh \
	device/mtk/mt5396/DTV_OUT/blcr.ko:root/blcr.ko
else
PRODUCT_COPY_FILES += \
	device/mtk/mt5396/configs/zygote-normal.rc:root/zygote.rc
endif

ifeq ($(ANDROID_ICS_512MB), true)
PRODUCT_COPY_FILES += \
	device/mtk/mt5396/configs/script/setup_zram.sh:system/etc/script/setup_zram.sh
else
PRODUCT_COPY_FILES += \
	device/mtk/mt5396/configs/script/setup_zram_cust_1.sh:system/etc/script/setup_zram.sh
endif


ifeq ($(USB_ADB),true)
PRODUCT_COPY_FILES += \
    device/mtk/mt5396/configs/script/adb_usb.sh:system/etc/script/adb_usb.sh
# usb modules
PRODUCT_COPY_FILES += \
	device/mtk/mt5396/DTV_OUT/musb_hdrc.ko:system/lib/modules/musb_hdrc.ko
endif


# mount external storage and busybox
PRODUCT_COPY_FILES += \
    device/mtk/mt5396/configs/vold.fstab:system/etc/vold.fstab \
    $(PREBUIT_PATH)/busybox:root/sbin/busybox \
    $(PREBUIT_PATH)/mount.exfat:root/sbin/mount.exfat

# misc scripts
PRODUCT_COPY_FILES += \
    $(PREBUIT_PATH)/get_rootfs.sh:system/bin/get_rootfs.sh \
    $(PREBUIT_PATH)/update_rootfs.sh:system/bin/update_rootfs.sh


# key input
PRODUCT_COPY_FILES += \
    device/mtk/mt5396/configs/qwerty.kl:system/usr/keylayout/qwerty.kl \
    device/mtk/mt5396/configs/mtkinp_events.kl:system/usr/keylayout/mtkinp_events.kl \
    device/mtk/mt5396/configs/ttxkeymap.ini:system/usr/keylayout/ttxkeymap.ini
# dev_svc
PRODUCT_COPY_FILES += \
    $(DTV_OUT_PATH)/system/bin/dtv_svc:system/bin/dtv_svc \
    $(DTV_OUT_PATH)/symbols/system/bin/dtv_svc:symbols/system/bin/dtv_svc

# DTV driver ko
PRODUCT_COPY_FILES += \
    $(DTV_OUT_PATH)/dtv_driver_adpt.ko:root/dtv_driver_adpt.ko \
    $(DTV_OUT_PATH)/snd-mtk.ko:root/snd-mtk.ko \
    $(DTV_OUT_PATH)/fuse.ko:system/lib/modules/fuse.ko \
    $(DTV_OUT_PATH)/ntfs.ko:system/lib/modules/ntfs.ko \
    $(DTV_OUT_PATH)/rt2800lib.ko:system/lib/modules/rt2800lib.ko \
    $(DTV_OUT_PATH)/rt2800usb.ko:system/lib/modules/rt2800usb.ko \
    $(DTV_OUT_PATH)/rt2x00lib.ko:system/lib/modules/rt2x00lib.ko \
    $(DTV_OUT_PATH)/rt2x00usb.ko:system/lib/modules/rt2x00usb.ko \
    $(DTV_OUT_PATH)/cfg80211.ko:system/lib/modules/cfg80211.ko \
    $(DTV_OUT_PATH)/mac80211.ko:system/lib/modules/mac80211.ko \
    $(DTV_OUT_PATH)/rfkill.ko:system/lib/modules/rfkill.ko

# android adb support
ifeq ($(USB_ADB),true)
PRODUCT_COPY_FILES += \
    $(DTV_OUT_PATH)/android.ko:system/lib/modules/android.ko
endif

#v4l library file
PRODUCT_COPY_FILES += \
    $(DTV_OUT_PATH)/v4l2-common.ko:system/lib/modules/v4l2-common.ko \
    $(DTV_OUT_PATH)/v4l2-int-device.ko:system/lib/modules/v4l2-int-device.ko \
    $(DTV_OUT_PATH)/videodev.ko:system/lib/modules/videodev.ko \
    $(DTV_OUT_PATH)/uvcvideo.ko:system/lib/modules/uvcvideo.ko 
#$(DTV_OUT_PATH)/v4l1-compat.ko:system/lib/modules/v4l1-compat.ko

#add drv_cli for debug
PRODUCT_COPY_FILES += \
    device/mtk/mt5396/configs/drv_cli.sh:system/etc/drv_cli.sh

# 3D library file
ifeq "$(SUPPORT_KERNEL_CHB)" "true"
$(warning mali_chb=$(SUPPORT_KERNEL_CHB))
PRODUCT_COPY_FILES += \
    $(PREBUIT_PATH)/dram_chb/mali/mali.ko:root/mali.ko \
    $(PREBUIT_PATH)/dram_chb/mali/ump.ko:root/ump.ko \
    $(PREBUIT_PATH)/dram_chb/mali/lib/libMali.so:system/lib/libMali.so \
    $(PREBUIT_PATH)/dram_chb/mali/lib/libUMP.so:system/lib/libUMP.so \
    $(PREBUIT_PATH)/dram_chb/mali/lib/libEGL_mali.so:system/lib/egl/libEGL_mali.so \
    $(PREBUIT_PATH)/dram_chb/mali/lib/libGLESv2_mali.so:system/lib/egl/libGLESv2_mali.so \
    $(PREBUIT_PATH)/dram_chb/mali/lib/libGLESv1_CM_mali.so:system/lib/egl/libGLESv1_CM_mali.so \
    $(PREBUIT_PATH)/dram_chb/mali/lib/egl.cfg:system/lib/egl/egl.cfg \
    $(PREBUIT_PATH)/dram_chb/mali/lib/libMali.so:obj/lib/libMali.so \
    $(PREBUIT_PATH)/dram_chb/mali/lib/libUMP.so:obj/lib/libUMP.so
else
PRODUCT_COPY_FILES += \
    $(PREBUIT_PATH)/mali/mali.ko:root/mali.ko \
    $(PREBUIT_PATH)/mali/ump.ko:root/ump.ko \
    $(PREBUIT_PATH)/mali/lib/libMali.so:system/lib/libMali.so \
    $(PREBUIT_PATH)/mali/lib/libUMP.so:system/lib/libUMP.so \
    $(PREBUIT_PATH)/mali/lib/libEGL_mali.so:system/lib/egl/libEGL_mali.so \
    $(PREBUIT_PATH)/mali/lib/libGLESv2_mali.so:system/lib/egl/libGLESv2_mali.so \
    $(PREBUIT_PATH)/mali/lib/libGLESv1_CM_mali.so:system/lib/egl/libGLESv1_CM_mali.so \
    $(PREBUIT_PATH)/mali/lib/egl.cfg:system/lib/egl/egl.cfg \
    $(PREBUIT_PATH)/mali/lib/libMali.so:obj/lib/libMali.so \
    $(PREBUIT_PATH)/mali/lib/libUMP.so:obj/lib/libUMP.so
endif

# bootanimation file 
PRODUCT_COPY_FILES += \
	 $(PREBUIT_PATH)/bootanimation.zip:system/media/bootanimation.zip
	 
# sserver file 
PRODUCT_COPY_FILES += \
	 $(PREBUIT_PATH)/sserver:system/bin/sserver \
	 $(PREBUIT_PATH)/MonitorServer:system/bin/MonitorServer

# Wifi driver and configuration file
PRODUCT_COPY_FILES += \
    device/mtk/mt5396/configs/script/init_wifi.sh:system/etc/script/init_wifi.sh \
    $(PREBUIT_PATH)/wifi/wpa_supplicant.conf:system/etc/wifi/wpa_supplicant.conf \
    $(PREBUIT_PATH)/wifi/p2p_supplicant.conf:system/etc/wifi/p2p_supplicant.conf \
    $(PREBUIT_PATH)/wifi/rt2870.bin:system/etc/firmware/rt2870.bin \
    $(PREBUIT_PATH)/wifi/8192cu.ko:system/lib/modules/8192cu.ko \
    $(PREBUIT_PATH)/wifi/8712u.ko:system/lib/modules/8712u.ko \
    $(PREBUIT_PATH)/wifi/wpa_supplicantrt:system/bin/wpa_supplicantrt \
    $(PREBUIT_PATH)/wifi/wpa_clirt:system/bin/wpa_clirt  \
    $(PREBUIT_PATH)/wifi/iwpriv:system/bin/iwpriv  \
    $(PREBUIT_PATH)/wifi/wifi_set.sh:system/bin/wifi_set.sh \
    $(PREBUIT_PATH)/wifi/wpa_supplicantra:system/bin/wpa_supplicantra \
    $(PREBUIT_PATH)/wifi/wpa_clira:system/bin/wpa_clira \
    $(PREBUIT_PATH)/wifi/iw:system/bin/iw
    
#Ralink RT5370 proprietary
PRODUCT_COPY_FILES += \
    $(PREBUIT_PATH)/wifi/rt5572sta.ko:system/lib/modules/rt5572sta.ko \
    $(PREBUIT_PATH)/wifi/rtnet5572sta.ko:system/lib/modules/rtnet5572sta.ko \
    $(PREBUIT_PATH)/wifi/rtutil5572sta.ko:system/lib/modules/rtutil5572sta.ko \
    $(PREBUIT_PATH)/wifi/RT2870STA.dat:system/etc/Wireless/RT2870STA/RT2870STA.dat \
    $(PREBUIT_PATH)/wifi/rt5572ap.ko:system/lib/modules/rt5572ap.ko \
    $(PREBUIT_PATH)/wifi/rtnet5572ap.ko:system/lib/modules/rtnet5572ap.ko \
    $(PREBUIT_PATH)/wifi/rtutil5572ap.ko:system/lib/modules/rtutil5572ap.ko \
    $(PREBUIT_PATH)/wifi/RT2870AP.dat:system/etc/Wireless/RT2870AP/RT2870AP.dat
    
#atheros k2
PRODUCT_COPY_FILES += \
	  $(PREBUIT_PATH)/wifi/adf.ko:system/lib/modules/adf.ko \
    $(PREBUIT_PATH)/wifi/asf.ko:system/lib/modules/asf.ko \
    $(PREBUIT_PATH)/wifi/ath_dev.ko:system/lib/modules/ath_dev.ko \
    $(PREBUIT_PATH)/wifi/ath_dfs.ko:system/lib/modules/ath_dfs.ko \
    $(PREBUIT_PATH)/wifi/ath_hal.ko:system/lib/modules/ath_hal.ko \
    $(PREBUIT_PATH)/wifi/ath_hif_usb.ko:system/lib/modules/ath_hif_usb.ko \
    $(PREBUIT_PATH)/wifi/ath_htc_hst.ko:system/lib/modules/ath_htc_hst.ko \
    $(PREBUIT_PATH)/wifi/ath_pktlog.ko:system/lib/modules/ath_pktlog.ko \
    $(PREBUIT_PATH)/wifi/ath_rate_atheros.ko:system/lib/modules/ath_rate_atheros.ko \
    $(PREBUIT_PATH)/wifi/ath_usbdrv.ko:system/lib/modules/ath_usbdrv.ko \
    $(PREBUIT_PATH)/wifi/iwpriv:system/bin/iwpriv \
    $(PREBUIT_PATH)/wifi/umac.ko:system/lib/modules/umac.ko \
    $(PREBUIT_PATH)/wifi/wlanconfig:system/bin/wlanconfig \
    $(PREBUIT_PATH)/wifi/wpa_supplicantat:system/bin/wpa_supplicantat \
    $(PREBUIT_PATH)/wifi/wpa_cliat:system/bin/wpa_cliat \
    
PRODUCT_PROPERTY_OVERRIDES += \
    wifi.interface=wlan0
    
# Flash ko file
#PRODUCT_COPY_FILES += \
#	device/mtk/mt5396/configs/script/install_flash.sh:system/etc/script/install_flash.sh \
#	device/mtk/mt5396/prebuilt/f11_plugin/oem_install_flash_player.bin:system/apk/oem_install_flash_player.bin \
#	$(PREBUIT_PATH)/f11_plugin/libflashplayer.so:/system/lib/plugins/com.adobe.flashplayer/libflashplayer.so \
#	$(PREBUIT_PATH)/f11_plugin/libstagefright_froyo.so:/system/lib/plugins/com.adobe.flashplayer/libstagefright_froyo.so \
#	$(PREBUIT_PATH)/f11_plugin/libstagefright_honeycomb.so:/system/lib/plugins/com.adobe.flashplayer/libstagefright_honeycomb.so \
#	$(PREBUIT_PATH)/f11_plugin/libysshared.so:/system/lib/plugins/com.adobe.flashplayer/libysshared.so

#3D wallpapers
PRODUCT_COPY_FILES += \
 packages/wallpapers/LivePicker/android.software.live_wallpaper.xml:system/etc/permissions/android.software.live_wallpaper.xml

#lib ext2 add
PRODUCT_COPY_FILES += \
 $(PREBUIT_PATH)/libext2_uuid.so:/system/lib/libext2_uuid.so

#infopush test config
PRODUCT_COPY_FILES += \
 packages/apps/application_platform/InfoPush/UpdatePlatformType.txt:system/app/UpdatePlatformType.txt
 
# List of apps and optional libraries (Java and native) to put in the add-on system image.
PRODUCT_PACKAGES := \
	com.mediatek.tv.custom \
	com.mediatek.tv \
	com.mediatek.tvcm \
        com.mediatekk.tvcm \
	libcom_mediatek_tv_jni \
	sserver \
	pppoecd \
	adsldisconnect

# name of the add-on
PRODUCT_SDK_ADDON_NAME := mtk_tv_addon

# Copy the manifest and hardware files for the SDK add-on.
PRODUCT_SDK_ADDON_COPY_FILES := \
    device/mtk/mt5396/tv_addon/sdk_addon/manifest.ini:manifest.ini

# Copy the jar files for the optional libraries that are exposed as APIs.
PRODUCT_SDK_ADDON_COPY_MODULES := \
    com.mediatek.tv.custom:libs/com.mediatek.tv.custom.jar \
    com.mediatek.tv:libs/com.mediatek.tv.jar

# Name of the doc to generate and put in the add-on. This must match the name defined
# in the optional library with the tag
#    LOCAL_MODULE:= platform_library
# in the documentation section.
PRODUCT_SDK_ADDON_DOC_MODULES := tv
$(call inherit-product, $(SRC_TARGET_DIR)/product/full.mk)
PRODUCT_PACKAGES += TVService

PRODUCT_PACKAGES += \
        ubinize \
        ubiattach \
        ubidetach

PRODUCT_PACKAGES += \
        audio.primary.mt5396 \
        audio.a2dp.default \
        gralloc.mt5396 \
        camera.mt5396 \
        cli \
        cli_shell \
        libdtv_getline

PRODUCT_PACKAGES += \
        hwcomposer.mt5396 \

PRODUCT_PACKAGES += \
        ttyman

PRODUCT_PACKAGES += \
        libvsstex

PRODUCT_PACKAGES += \
        PinyinIME \
        libjni_pinyinime

PRODUCT_PACKAGES += \
        setmac
        
PRODUCT_PACKAGES += \
        setlogo

PRODUCT_PACKAGES += \
        LiveWallpapers \
        LiveWallpapersPicker

PRODUCT_PACKAGES += \
        mkbootimg \
        mkbootfs \
        split_bootimg

PRODUCT_PACKAGES += \
        switch_fbm

#PRODUCT_PACKAGES += \
#        oem_install_flash_player_ics

PRODUCT_PACKAGES += \
        android.policy_tv

PRODUCT_PACKAGES += \
        lzop
# Discard inherited values and use our own instead.
PRODUCT_MANUFACTURER := MTK
PRODUCT_NAME := generic_mt5396
PRODUCT_DEVICE := mt5396
PRODUCT_MODEL := Generic Android on mt5396
PRODUCT_POLICY := android.policy_tv
TARGET_BOOTLOADER_BOARD_NAME := mt5396
PRODUCT_BRAND := Android

# opengl version info
PRODUCT_PROPERTY_OVERRIDES += ro.opengles.version=131072

ifeq "1080p" "$(OSD_RESOLUTION)"
# lcd density
PRODUCT_PROPERTY_OVERRIDES += ro.sf.lcd_density=240
else
# lcd density
# lcd density default is 160
# PRODUCT_PROPERTY_OVERRIDES += ro.sf.lcd_density=160
endif

# dalvik vm heapsize
PRODUCT_PROPERTY_OVERRIDES += dalvik.vm.heapsize=256m

# Visual StrictMode indicator
PRODUCT_PROPERTY_OVERRIDES += persist.sys.strictmode.visual=0
PRODUCT_PROPERTY_OVERRIDES += persist.sys.strictmode.disable=1

# turn off dalvik vm warning abort in release version
ifeq "$(BUILD_CFG)" "rel"
PRODUCT_PROPERTY_OVERRIDES += dalvik.vm.jniopts=warnonly
endif

# Include System Property config file
$(call inherit-product, device/mtk/mt5396/system.mk)

#include tvos_addon product file
$(call inherit-product-if-exists, vendor/tcl/tvos_addon/products/generic_tvos_addon.mk)

#include com.mediatekk.tvcm
