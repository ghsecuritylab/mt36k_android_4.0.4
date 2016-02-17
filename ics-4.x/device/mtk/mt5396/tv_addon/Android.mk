TV_ADDON_TOP := $(word 1, $(subst /vm_linux/android/,/vm_linux/android /, $(shell pwd)))
TV_ADDON_TOP := $(TV_ADDON_TOP)/dtv-android/tv_addon_ics
VM_LINUX_ROOT := $(TV_ADDON_TOP)/../../../vm_linux
TV_ADDON_TOP_EXIST := $(shell test -d $(TV_ADDON_TOP) && echo "true" || echo "false")

ifneq "$(RLS_CUSTOM_BUILD)" "true"
    include $(TV_ADDON_TOP)/Android.mk
else
#    $(error $(TV_ADDON_TOP) not exist)
    # TODO: use pre-buile

#    LOCAL_PATH := $(call my-dir)
    include $(TV_ADDON_TOP)/Android.mk
   
    PRODUCT_COPY_FILES += \
	$(ICS_DIR)/device/mtk/mt5396/prebuilt/hlstest.bin:system/bin/hlstest.bin \
	$(ICS_DIR)/device/mtk/mt5396/prebuilt/audio.primary.goldfish.so:system/lib/hw/audio.primary.goldfish.so \
	$(ICS_DIR)/device/mtk/mt5396/prebuilt/libliveMedia.so:system/lib/libliveMedia.so \
	$(ICS_DIR)/device/mtk/mt5396/prebuilt/com.mediatek.bluetooth.adapter.xml:system/etc/permissions/com.mediatek.bluetooth.adapter.xml \
	$(ICS_DIR)/device/mtk/mt5396/prebuilt/libblueadapter.so:system/lib/libblueadapter.so \
	$(ICS_DIR)/device/mtk/mt5396/prebuilt/libsrec_jni.so:system/lib/libsrec_jni.so


	PRODUCT_COPY_FILES += \
	$(ICS_DIR)/device/mtk/mt5396/prebuilt/com.mediatek.bluetooth.adapter.jar:system/framework/com.mediatek.bluetooth.adapter.jar \
	$(ICS_DIR)/device/mtk/mt5396/prebuilt/rtsptest.bin:system/bin/rtsptest.bin
	
endif

