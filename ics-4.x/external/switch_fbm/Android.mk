LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := switch_fbm.c

# include mtimage header files
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../vm_linux/chiling/app_if/mtal/mtal_inc
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../vm_linux/chiling/app_if/mtal/include

LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../chiling/app_if/mtal/mtal_inc \
                    $(LOCAL_PATH)/../../../../chiling/app_if/mtal/include

LOCAL_SHARED_LIBRARIES := \
    libmtal_dynamic

LOCAL_MODULE := switch_fbm

include $(BUILD_EXECUTABLE)

