LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
include $(BUILD_MULTI_PREBUILT)

# ==============================================================================
include $(LOCAL_PATH)/com.tcl.tvos.addon.mk

# ==============================================================================
include $(call all-makefiles-under,$(LOCAL_PATH))
