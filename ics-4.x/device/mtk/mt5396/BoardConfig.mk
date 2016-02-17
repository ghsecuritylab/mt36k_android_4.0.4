# Copyright (C) 2011 The Android Open Source Project
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
# Product-specific compile-time definitions.
#

USE_CAMERA_STUB := true
BOARD_USES_GENERIC_AUDIO := false

TARGET_CPU_ABI := armeabi-v7a
TARGET_CPU_ABI2 := armeabi

TARGET_NO_BOOTLOADER := true
TARGET_NO_KERNEL := false
TARGET_NO_RECOVERY := false

HAVE_HTC_AUDIO_DRIVER := false
BOARD_USES_ALSA_AUDIO := true
BUILD_WITH_ALSA_UTILS := true
BOARD_USES_MT53XX_AUDIO := true

BOARD_HAVE_BLUETOOTH := false

ifeq "$(SYS_IMG_FS)" "ext4"
TARGET_USERIMAGES_USE_EXT4 := true
BOARD_FLASH_BLOCK_SIZE := 512
TARGET_USERIMAGES_SPARSE_EXT_DISABLED := true
#boot.image, 16M
BOARD_BOOTIMAGE_MAX_SIZE := 8388608

#system.image, 200M
BOARD_SYSTEMIMAGE_PARTITION_SIZE := 209715200

#userdata.image 190M
BOARD_USERDATAIMAGE_PARTITION_SIZE := 195035136

#cache.image 77M
BOARD_CACHEIMAGE_PARTITION_SIZE := 80740352
else
TARGET_USERIMAGES_USE_UBIFS := true

# NAND Flash Page/Block information for otapackage
BOARD_NAND_PAGE_SIZE := 2048
BOARD_FLASH_BLOCK_SIZE := $(BOARD_NAND_PAGE_SIZE) * 64
endif

# Set /system/bin/sh to mksh, not ash, to test the transition.
TARGET_SHELL := mksh

BOARD_EGL_CFG := device/mtk/${IC_SETTING}/egl.cfg
USE_OPENGL_RENDERER := true

ifeq "${IC_SETTING}" "mt5395"
    TARGET_BOARD_PLATFORM := mt5395
    TARGET_ARCH_VARIANT := armv6-vfp
endif

ifeq "${IC_SETTING}" "mt5396"
    TARGET_BOARD_PLATFORM := mt5396
    TARGET_ARCH_VARIANT := armv7-a
    ARCH_ARM_HAVE_TLS_REGISTER := true
endif

# For WiFi Setting
BOARD_WIFI_VENDOR := realtek
ifeq ($(BOARD_WIFI_VENDOR), ralink)
# For WPA_SUPPLICANT that supports android driver_cmd
BOARD_WPA_SUPPLICANT_DRIVER ?= NL80211
BOARD_WPA_SUPPLICANT_PRIVATE_LIB ?= private_lib_driver_cmd
WPA_SUPPLICANT_VERSION ?= VER_0_8_X
else ifeq ($(BOARD_WIFI_VENDOR), realtek)
    WPA_SUPPLICANT_VERSION := VER_0_8_X
    #BOARD_WPA_SUPPLICANT_DRIVER := WEXT
    BOARD_WPA_SUPPLICANT_DRIVER := NL80211
    BOARD_WPA_SUPPLICANT_PRIVATE_LIB := lib_driver_cmd_rtl
    BOARD_HOSTAPD_DRIVER        := NL80211
    BOARD_HOSTAPD_PRIVATE_LIB   := lib_driver_cmd_rtl

    BOARD_WLAN_DEVICE := rtl8192cu
    #BOARD_WLAN_DEVICE := rtl8192du
    #BOARD_WLAN_DEVICE := rtl8192ce
    #BOARD_WLAN_DEVICE := rtl8192de
    #BOARD_WLAN_DEVICE := rtl8723as
    #BOARD_WLAN_DEVICE := rtl8723au
    #BOARD_WLAN_DEVICE := rtl8188es

    WIFI_DRIVER_MODULE_NAME   := 8192cu
    #WIFI_DRIVER_MODULE_PATH   := "/system/lib/modules/8192cu.ko"

    WIFI_DRIVER_MODULE_ARG    := ""
    WIFI_FIRMWARE_LOADER      := ""
    WIFI_DRIVER_FW_PATH_STA   := ""
    WIFI_DRIVER_FW_PATH_AP    := ""
    WIFI_DRIVER_FW_PATH_P2P   := ""
    WIFI_DRIVER_FW_PATH_PARAM := ""
endif
TARGET_RELEASETOOLS_EXTENSIONS := device/mtk/$(IC_SETTING)