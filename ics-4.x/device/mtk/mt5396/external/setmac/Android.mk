#
# Build setmac command
#
LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

# LOCAL_CFLAGS := 

# LOCAL_C_INCLUDES:=

LOCAL_SRC_FILES := setmac.c

LOCAL_MODULE_TAGS := optional
LOCAL_MODULE := setmac

include $(BUILD_EXECUTABLE)

