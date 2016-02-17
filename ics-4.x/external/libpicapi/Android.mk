#
# Build setmac command
#
LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := libpicapi

LOCAL_SRC_FILES := pictureapi.cpp
LOCAL_SHARED_LIBRARIES :=  liblog libmtal_dynamic libjpeg
LOCAL_CFLAGS := -I$(L_CFLAGS) -DANDROID_TOOLCHAIN
#LOCAL_LDLIBS += -lmtal_static
LOCAL_LDLIBS += -lmtal_dynamic
LOCAL_LDLIBS += -ljpeg
#LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../project_x/middleware/res_mngr/drv
#LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../project_x/middleware/inc
#LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../chiling/app_if/mtal/obj/src
#LOCAL_C_INCLUDES += $(LOCAL_PATH)/./include/

                   
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../chiling/app_if/mtal/mtal_inc
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../chiling/app_if/mtal/include
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../chiling/driver/linux/include
LOCAL_C_INCLUDES += $(DRV_INC_DIR)
LOCAL_C_INCLUDES += $(COMMON_INC_DIR)

#LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../project_x/target/$(TARGET_IC)/$(subst mt,,$(TARGET_IC))_driver/inc \
#          $(LOCAL_PATH)/../../../../project_x/target/$(TARGET_IC)/$(subst mt,,$(TARGET_IC))_driver/drv_inc \
#          $(LOCAL_PATH)/../../../../project_x/target/$(TARGET_IC)/$(subst mt,,$(TARGET_IC))_driver/nptv8098/inc/sys \
#          $(LOCAL_PATH)/../../../../project_x/target/$(TARGET_IC)/$(subst mt,,$(TARGET_IC))_driver/nptv8098/inc/ \
#          $(LOCAL_PATH)/../../../../project_x/target/$(TARGET_IC)/$(subst mt,,$(TARGET_IC))_driver/nptv8098/inc/hw \
#          $(LOCAL_PATH)/../../../../project_x/target/$(TARGET_IC)/$(subst mt,,$(TARGET_IC))_driver/nptv8098/inc/drv \
#          $(LOCAL_PATH)/../../../../project_x/target/$(TARGET_IC)/$(subst mt,,$(TARGET_IC))_driver/private_inc

#LOCAL_CFLAGS += $(LOCAL_PATH)/../../../../../vm_linux/android/ics-4.x/device/mtk/mt5396/prebuilt/lib

LOCAL_C_INCLUDES +=	$(JNI_H_INCLUDE)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../android/ics-4.x/frameworks/base/opengl/include/GLES

LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../../android/ics-4.x/external/jpeg

LOCAL_MODULE_TAGS := eng

ifeq "$(ENABLE_SCALER_BUFFER_FOR_4K2K_PHOTO)" "true"
LOCAL_CFLAGS += -DALL_PIC_VIDEOPATH
endif

LOCAL_PRELINK_MODULE := false

#include $(BUILD_EXECUTABLE)
include $(BUILD_SHARED_LIBRARY)
