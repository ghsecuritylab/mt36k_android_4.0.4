ifeq ($(BLCR),true)

LOCAL_PATH := $(call my-dir)

vm_linux_path = $(word 1, $(subst /vm_linux/,/vm_linux /, $(shell pwd)))
$(shell (mkdir -p $(vm_linux_path)/android/ics-4.x/blcr-0.8.4/include/linux))
$(shell (ln -sf $(vm_linux_path)/chiling/kernel/linux-3.0/include/linux/kmalloc_sizes.h $(vm_linux_path)/android/ics-4.x/blcr-0.8.4/include/linux/))

ifndef my_local_flags
export my_local_flags = -DHAVE_FTB=0 -DCRI_DEBUG=0 -DCR_KERNEL_TRACING=0 -DCR_RESTORE_IDS=0 -DLIBCR_TRACING=0 -DCR_STACK_GROWTH=-1
endif

#libcr_omit.so
include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm

LOCAL_LDLIBS := -lpthread

LOCAL_SHARED_LIBRARIES := libcutils \
    libdl \
    libc

LOCAL_SRC_FILES := libcr/cr_omit.c

LOCAL_C_INCLUDES := \
    $(KERNEL_HEADERS) \
    $(LOCAL_PATH)/include \
    $(LOCAL_PATH) \
    $(LOCAL_PATH)/libcr/ \
    $(LOCAL_PATH)/libcr/arch/arm/ \
    $(LOCAL_PATH)/../frameworks/base/include/utils

LOCAL_CFLAGS := -DLIYI $(my_local_flags)
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

LOCAL_SRC_FILES:= libcr/cr_run.c

LOCAL_C_INCLUDES := \
    $(KERNEL_HEADERS) \
    $(LOCAL_PATH)/include \
    $(LOCAL_PATH)/ \
    $(LOCAL_PATH)/libcr/ \
    $(LOCAL_PATH)/libcr/arch/arm/ \
    $(LOCAL_PATH)/../frameworks/base/include/utils

LOCAL_CFLAGS := -DLIYI $(my_local_flags)
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

LOCAL_SRC_FILES:= libcr/cr_async.c \
    libcr/cr_trace.c \
    libcr/cr_core.c  \
    libcr/cr_sig_sync.c \
    libcr/cr_cs.c  \
    libcr/cr_pthread.c \
    libcr/cr_strerror.c  \
    libcr/cr_request.c \
    libcr/cr_syscall.c \
    libcr/cr_omit.c   \
    libcr/cr_run.c \
    libcr/crut_util.c \
    libcr/crut_util_libcr.c \
    libcr/crut_util_pth.c

LOCAL_C_INCLUDES := \
    $(KERNEL_HEADERS) \
    $(LOCAL_PATH)/include \
    $(LOCAL_PATH)/ \
    $(LOCAL_PATH)/libcr/ \
    $(LOCAL_PATH)/libcr/arch/arm/ \
    $(LOCAL_PATH)/../frameworks/base/include/utils

LOCAL_CFLAGS := -DLIYI -g $(my_local_flags)
LOCAL_MODULE := libcr
LOCAL_MODULE_TAGS := eng
include $(BUILD_SHARED_LIBRARY)


# cr_checkpoint
include $(CLEAR_VARS)

LOCAL_STATIC_LIBRARIES := libcutils

LOCAL_SHARED_LIBRARIES := libdl \
    libc \
    libcr_run \
    libcr

LOCAL_SRC_FILES := util/cr_checkpoint/cr_checkpoint.c

LOCAL_C_INCLUDES := \
    $(KERNEL_HEADERS) \
    $(LOCAL_PATH)/include

LOCAL_MODULE := cr_checkpoint

LOCAL_MODULE_TAGS := eng

include $(BUILD_EXECUTABLE)

$(call dist-for-goals,droid,$(LOCAL_BUILT_MODULE))


# cr_restart
include $(CLEAR_VARS)

LOCAL_STATIC_LIBRARIES := libcutils

LOCAL_SHARED_LIBRARIES := libdl \
    libc \
    libcr_run \
    libcr

LOCAL_SRC_FILES := util/cr_restart/cr_restart.c

LOCAL_C_INCLUDES := \
    $(KERNEL_HEADERS) \
    $(LOCAL_PATH)/include

LOCAL_MODULE := cr_restart

LOCAL_MODULE_TAGS := eng

include $(BUILD_EXECUTABLE)

$(call dist-for-goals,droid,$(LOCAL_BUILT_MODULE))

endif
