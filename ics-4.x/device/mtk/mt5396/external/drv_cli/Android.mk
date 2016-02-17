#
# Build drv_cli commands
#
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_CFLAGS += -O2 -Wall
LOCAL_LDLIBS += 

LOCAL_SRC_FILES:= \
	cli.c

LOCAL_MODULE := cli
LOCAL_MODULE_TAGS := optional

include $(BUILD_EXECUTABLE)
