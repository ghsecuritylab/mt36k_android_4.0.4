LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := cli_shell
LOCAL_MODULE_TAGS := optional
LOCAL_CFLAGS = $(L_CFLAGS) 
LOCAL_SRC_FILES := cli_main.c
include $(BUILD_EXECUTABLE)

