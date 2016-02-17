#
# Build setmac command
#
LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libvsstex

LOCAL_SRC_FILES := vsstex.cpp

LOCAL_SHARED_LIBRARIES := libEGL libGLESv1_CM liblog libmtal_dynamic
LOCAL_CFLAGS := -I$(L_CFLAGS) -DANDROID_TOOLCHAIN
LOCAL_LDLIBS += -lmtal_dynamic
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../chiling/app_if/mtal/mtal_inc
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../chiling/app_if/mtal/include
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../chiling/app_if/mtal/obj/src
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../project_x/middleware/res_mngr/drv
LOCAL_C_INCLUDES += $(LOCAL_PATH)/./include/

#LOCAL_CFLAGS += $(LOCAL_PATH)/../../../../../vm_linux/android/ics-4.x/device/mtk/mt5396/prebuilt/lib

LOCAL_C_INCLUDES +=	$(JNI_H_INCLUDE)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../android/ics-4.x/frameworks/base/opengl/include/GLES

LOCAL_MODULE_TAGS := eng


LOCAL_PRELINK_MODULE := false

#include $(BUILD_EXECUTABLE)
include $(BUILD_SHARED_LIBRARY)
