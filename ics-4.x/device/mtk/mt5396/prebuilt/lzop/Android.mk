############################################ 
#copy many so files 
#=========================================== 
#LOCAL_PATH := $(my-dir)
#include $(CLEAR_VARS) 
#
#
#include $(BUILD_PREBUILT) 

#LOCAL_PATH := $(call my-dir)

#include $(CLEAR_VARS)

#LOCAL_MODULE := lzop
 
#LOCAL_SRC_FILES := lzop

#LOCAL_MODULE_PATH := $(HOST_OUT_EXECUTABLES)

#$(warning sszhangdebugdebug=$(LOCAL_MODULE_PATH))
#LOCAL_MODULE_CLASS := EXECUTABLES
#LOCAL_MODULE_TAGS := optional

#include $(BUILD_PREBUILT) 

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := eng
LOCAL_MODULE_PATH := $(HOST_OUT_EXECUTABLES)
$(warning sszhangdebugdebug=$(LOCAL_MODULE_PATH))
LOCAL_PREBUILT_EXECUTABLES := lzop
include $(BUILD_HOST_PREBUILT)
