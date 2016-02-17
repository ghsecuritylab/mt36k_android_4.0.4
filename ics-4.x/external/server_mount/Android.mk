# Copyright 2010 The Android Open Source Project


LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)


LOCAL_SRC_FILES = socket_server.c
LOCAL_STATIC_LIBRARIS := libc


LOCAL_MODULE:= sserver
#LOCAL_MODULE_PATH := $(TARGET_ROOT_OUT_SBIN)
LOCAL_MODULE_PATH := $(TARGET_OUT)/bin

include $(BUILD_EXECUTABLE)




		
