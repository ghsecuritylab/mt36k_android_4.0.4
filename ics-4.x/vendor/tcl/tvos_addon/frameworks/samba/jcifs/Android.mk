#
# Copyright (c) 2010-2012 TCL Corp.
# All rights reserved.
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := libjcifs:jcifs-1.3.17.jar

include $(BUILD_MULTI_PREBUILT)
