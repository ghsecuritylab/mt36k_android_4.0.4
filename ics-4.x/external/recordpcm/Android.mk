LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_C_INCLUDES := ../../chiling/app_if/mtal/mtal_inc ../../chiling/app_if/mtal/include	
LOCAL_SRC_FILES:= recordpcm.c
LOCAL_MODULE := recordpcm
LOCAL_SHARED_LIBRARIES := libmtal_dynamic
LOCAL_MODULE_TAGS := optional

include $(BUILD_EXECUTABLE)
