
LOCAL_PATH:= $(call my-dir)

#########################
# Build the libmms library

include $(CLEAR_VARS)

LOCAL_C_INCLUDES +=   $(LOCAL_PATH)/../glib/glib   \
											$(LOCAL_PATH)/../glib        \
											$(LOCAL_PATH)/../glib/android       \
	                    $(LOCAL_PATH)/libiconv/include 			\
	                    $(LOCAL_PATH)/src/								\
	                    $(LOCAL_PATH)/include/ 						\
											$(LOCAL_PATH)/lib/ 								\
											$(LOCAL_PATH)/libcharset/lib 			\
											$(LOCAL_PATH)/libiconv/libcharset/include 													
	                   
LOCAL_SRC_FILES := \
src/uri.c          \
src/mms.c          \
src/mmsh.c         \
src/mmsx.c         \
libiconv/lib/iconv.c                         \
libiconv/libcharset/lib/localcharset.c       \
libiconv/lib/relocatable.c          				 \


	
LOCAL_SHARED_LIBRARIES += libglib 
LOCAL_SYSTEM_SHARED_LIBRARIES := libc
#LOCAL_LDLIBS += -ldl 

LOCAL_CFLAGS += -DLIBDIR=\"$(libdir)\" -DHAVE_SYS_SOCKET_H -DHAVE_NETINET_IN_H -DHAVE_NETDB_H -fno-exceptions -Wno-multichar -fPIC  -DANDROID -fmessage-length=0 -fno-strict-aliasing -Wno-unused -Winit-self -Wpointer-arith -Wpointer-arith -Wwrite-strings -Wunused -Winline -Wnested-externs -Wmissing-declarations -Wmissing-prototypes -Wno-long-long -Wfloat-equal -Wno-multichar -Wsign-compare -Wno-format-nonliteral -Wendif-labels -Wstrict-prototypes -Wdeclaration-after-statement -Wno-system-headers -nostdlib

LOCAL_PRELINK_MODULE := false

LOCAL_MODULE:= libmms

LOCAL_MODULE_TAGS := eng

include $(BUILD_SHARED_LIBRARY)




