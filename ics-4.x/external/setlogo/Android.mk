#
# Build setlogo command
#
LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

# LOCAL_CFLAGS := 

#LOCAL_C_INCLUDES:= $(LOCAL_PATH)/include 

LOCAL_SRC_FILES := setlogo.c

LOCAL_MODULE_TAGS := optional
LOCAL_MODULE := setlogo


include $(BUILD_EXECUTABLE)

