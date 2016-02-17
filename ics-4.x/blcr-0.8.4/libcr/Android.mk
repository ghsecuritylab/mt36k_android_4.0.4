# Copyright 2006 The Android Open Source Project

LOCAL_PATH := $(call my-dir)

#libcr_omit.so
include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm

LOCAL_LDLIBS := -lpthread

LOCAL_SHARED_LIBRARIES := libcutils \
	libdl \
	libc

LOCAL_SRC_FILES := cr_omit.c

LOCAL_C_INCLUDES := \
	$(KERNEL_HEADERS) \
	$(kernel_blcr_headers) \
	$(LOCAL_PATH)/../include \
	$(LOCAL_PATH)/../ \
	$(LOCAL_PATH)/arch/arm/ \
	$(LOCAL_PATH)/../../frameworks/base/include/utils

LOCAL_CFLAGS := -DLIYI $(my_local_flags)
#LOCAL_PRELINK_MODULE := false
LOCAL_MODULE := libcr_omit
LOCAL_MODULE_TAGS := eng
include $(BUILD_SHARED_LIBRARY)

#libcr_run.so
include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm

LOCAL_LDLIBS := -lpthread

LOCAL_SHARED_LIBRARIES := libcutils \
	libdl \
	libc

LOCAL_SRC_FILES:= cr_run.c

LOCAL_C_INCLUDES := \
	$(KERNEL_HEADERS) \
	$(kernel_blcr_headers) \
	$(LOCAL_PATH)/../include \
	$(LOCAL_PATH)/../ \
	$(LOCAL_PATH)/arch/arm/ \
	$(LOCAL_PATH)/../../frameworks/base/include/utils

LOCAL_CFLAGS := -DLIYI $(my_local_flags)
#LOCAL_PRELINK_MODULE := false
LOCAL_MODULE := libcr_run
LOCAL_MODULE_TAGS := eng
include $(BUILD_SHARED_LIBRARY)

#libcr.so
include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm

LOCAL_LDLIBS := -lpthread

LOCAL_SHARED_LIBRARIES := libcutils \
	libdl \
	libc

LOCAL_SRC_FILES:= cr_async.c \
	cr_trace.c \
	cr_core.c  \
	cr_sig_sync.c \
	cr_cs.c  \
	cr_pthread.c \
	cr_strerror.c  \
	cr_request.c \
	cr_syscall.c \
	cr_omit.c   \
	cr_run.c \
	crut_util.c \
	crut_util_libcr.c \
	crut_util_pth.c

LOCAL_C_INCLUDES := \
	$(KERNEL_HEADERS) \
	$(kernel_blcr_headers) \
	$(LOCAL_PATH)/../include \
	$(LOCAL_PATH)/../ \
	$(LOCAL_PATH)/arch/arm/ \
	$(LOCAL_PATH)/../../frameworks/base/include/utils

LOCAL_CFLAGS := -DLIYI -g $(my_local_flags)
#LOCAL_PRELINK_MODULE := false
LOCAL_MODULE := libcr
LOCAL_MODULE_TAGS := eng
include $(BUILD_SHARED_LIBRARY)
