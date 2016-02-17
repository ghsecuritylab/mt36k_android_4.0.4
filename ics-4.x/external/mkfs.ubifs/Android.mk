LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= mkfs_ubifs.c\

LOCAL_FORCE_STATIC_EXECUTABLE := true
LOCAL_MODULE_TAGS := eng
LOCAL_STATIC_LIBRARIES := libcutils libc
LOCAL_MODULE := mkfs.ubifs

include $(BUILD_EXECUTABLE)

