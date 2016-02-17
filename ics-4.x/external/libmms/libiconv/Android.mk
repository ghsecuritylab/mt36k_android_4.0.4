
LOCAL_PATH:= $(call my-dir)
#########################
# Build the libiconv library

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += \
			$(LOCAL_PATH)/include/ \
			$(LOCAL_PATH)/lib/ \
			$(LOCAL_PATH)/libcharset/lib \
			$(LOCAL_PATH)/libcharset/include 	
					
LOCAL_SRC_FILES := \
lib/iconv.c                         \
libcharset/lib/localcharset.c       \
lib/relocatable.c        


	
LOCAL_SHARED_LIBRARIES += libc
#LOCAL_LDLIBS += -ldl
#LOCAL_PRELINK_MODULE:=false

LOCAL_CFLAGS += -fno-exceptions -Wno-multichar -fPIC  -DANDROID -fmessage-length=0 -fno-strict-aliasing -Wno-unused -Winit-self -Wpointer-arith -Wpointer-arith -Wwrite-strings -Wunused -Winline -Wnested-externs -Wmissing-declarations -Wmissing-prototypes -Wno-long-long -Wfloat-equal -Wno-multichar -Wsign-compare -Wno-format-nonliteral -Wendif-labels -Wstrict-prototypes -Wdeclaration-after-statement -Wno-system-headers -nostdlib -fvisibility=hidden  -DHAVE_CONFIG_H  -DLIBDIR=\"$(libdir)\"  
								

LOCAL_PRELINK_MODULE := false

LOCAL_MODULE:= libiconv


#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)

