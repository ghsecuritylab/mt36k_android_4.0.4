LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= src/if.c\
	src/pppoe.c\
	src/discovery.c\
	src/common.c\
	src/ppp.c\
	src/debug.c\


LOCAL_C_INCLUDES += $(LOCAL_PATH)/src

LOCAL_SHARED_LIBRARIES := 

VERSION:=3.10

LOCAL_CFLAGS := -g -Wall -O0 -mapcs-frame -DANDROID -pipe -fPIC -fsigned-char -D_CPU_LITTLE_ENDIAN_ -D__INTEGRITY -DVERSION="$(VERSION)"


LOCAL_MODULE:=pppoe

LOCAL_MODULE_TAGS:=eng


include $(BUILD_EXECUTABLE)
