LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := eatmem
LOCAL_MODULE_TAGS := optional
LOCAL_CFLAGS = $(L_CFLAGS) 
LOCAL_SRC_FILES := eatmem.c
include $(BUILD_EXECUTABLE)

