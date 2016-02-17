
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

RP_VERSION:=3.8
MYOBJS := \
		utils.c \
		../pppd/plugins/pppoe/utils.c \
		main.c \
		auth.c \
		options.c \
		sys-linux.c \
		pppoe.c	\
		../pppd/magic.c \
		../pppd/fsm.c \
		../pppd/lcp.c \
		../pppd/ipcp.c \
		../pppd/upap.c \
		../pppd/demand.c \
		../pppd/plugins/pppoe/pppoehash.c \
		../pppd/plugins/pppoe/pppoe_client.c \
		../pppd/plugins/pppoe/libpppoe.c \
		../pppd/ccp.c \
		../pppd/md5.c \
		../pppd/chap.c \
		../pppd/md4.c \
		../pppd/mppe.c \
		../pppd/chap_ms.c \
		../pppd/sha1dgst.c \
		../pppd/extra_crypto.c

LOCAL_SRC_FILES:= $(MYOBJS)

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/ \
	$(LOCAL_PATH)/../ \
	$(LOCAL_PATH)/../include \
	$(LOCAL_PATH)/../pppd \
	$(LOCAL_PATH)/../pppd/plugins/pppoe \
	$(LOCAL_PATH)/../../openssl/include/ \
	$(LOCAL_PATH)/../../openssl/include/openssl
	
LOCAL_CFLAGS := -DMPPE=1 -DCHAP_SUPPORT=1 -DCHAPMS_SUPPORT=1 -DCHAPMS=1 -DMSLANMAN=1  -DMPPPOE_SUPPORT=1 -DCCP_SUPPORT=1 -DDYNAMIC=1  -D_linux_=1 \
-DPLUGIN=1 -DDEBUG=1 -DHAVE_MMAP=1  -DHAVE_CRYPT_H=1
LOCAL_CFLAGS += -O2 -w -DRP_VERSION="$(RP_VERSION)"

LOCAL_SHARED_LIBRARIES := \
		libcutils libcrypto

LOCAL_MODULE:= pppoecd
LOCAL_MODULE_TAGS := optional

include $(BUILD_EXECUTABLE)
#===================================
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= ../disconn/pidkill.c

LOCAL_SHARED_LIBRARIES := \
				libcutils

LOCAL_MODULE:= adsldisconnect
LOCAL_MODULE_TAGS := optional

include $(BUILD_EXECUTABLE)

