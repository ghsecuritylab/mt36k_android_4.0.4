#
# Copyright (c) 2010-2012 TCL Corp.
# All rights reserved.
#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := com.tcl.adsystem
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := 

#LOCAL_JAVA_LIBRARIES := services

LOCAL_STATIC_JAVA_LIBRARIES := \
	pre_com.tcl.adsystem

include $(BUILD_JAVA_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := pre_com.tcl.adsystem
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_MODULE_PATH := $(TARGET_OUT_JAVA_LIBRARIES)
LOCAL_SRC_FILES := pre_com.tcl.adsystem.jar
include $(BUILD_PREBUILT)

