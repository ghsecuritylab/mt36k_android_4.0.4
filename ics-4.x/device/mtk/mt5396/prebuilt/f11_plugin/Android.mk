############################################ 
#copy many so files 
#=========================================== 
#LOCAL_PATH := $(my-dir)
#include $(CLEAR_VARS) 
#
#
#include $(BUILD_PREBUILT) 

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# Module name should match apk name to be installed.

LOCAL_MODULE := oem_install_flash_player_ics

LOCAL_SRC_FILES := $(LOCAL_MODULE).apk

LOCAL_MODULE_TAGS := optional 
LOCAL_MODULE_CLASS := APPS

LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
LOCAL_CERTIFICATE := platform

include $(BUILD_PREBUILT)
