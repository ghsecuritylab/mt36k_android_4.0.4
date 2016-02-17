LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := tcl_sim_kb.ko
LOCAL_MODULE_TAGS := optional
#LOCAL_MODULE_TAGS := user development
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT)/lib/modules
LOCAL_SRC_FILES := $(LOCAL_MODULE)
include $(BUILD_PREBUILT)
###########################################
include $(CLEAR_VARS)
LOCAL_MODULE := libinputproc.so
LOCAL_MODULE_TAGS := optional
#LOCAL_MODULE_TAGS := user development
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT)/lib
LOCAL_SRC_FILES := $(LOCAL_MODULE)
include $(BUILD_PREBUILT)
#########################################
include $(CLEAR_VARS)
LOCAL_MODULE := sinput
LOCAL_MODULE_TAGS := optional
#LOCAL_MODULE_TAGS := user development
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT)/bin
LOCAL_SRC_FILES := $(LOCAL_MODULE)
include $(BUILD_PREBUILT)
#########################################
include $(CLEAR_VARS)
LOCAL_MODULE := libinputproc_jni.so
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT)/lib
LOCAL_SRC_FILES := $(LOCAL_MODULE)
include $(BUILD_PREBUILT)
###########################################
include $(CLEAR_VARS)
LOCAL_MODULE := libsim-mouse.so
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT)/lib
LOCAL_SRC_FILES := $(LOCAL_MODULE)
include $(BUILD_PREBUILT)
###########################################
include $(CLEAR_VARS)
LOCAL_MODULE := Vendor_0019_Product_0001.kl
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT)/usr/keylayout
LOCAL_SRC_FILES := $(LOCAL_MODULE)
include $(BUILD_PREBUILT)
