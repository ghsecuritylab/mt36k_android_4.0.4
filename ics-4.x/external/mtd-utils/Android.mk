LOCAL_PATH := $(call my-dir)

ifeq ($(strip $(TARGET_USERIMAGES_USE_UBIFS)),true)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
	lib/libcrc32.c \
	mkfs.ubifs/compr.c \
	mkfs.ubifs/crc16.c \
	mkfs.ubifs/devtable.c \
	mkfs.ubifs/hashtable/hashtable.c \
	mkfs.ubifs/hashtable/hashtable_itr.c \
	mkfs.ubifs/lpt.c \
	mkfs.ubifs/mkfs.ubifs.c \
	ubi-utils/libubi.c \
	../e2fsprogs/lib/uuid/gen_uuid.c \
	../e2fsprogs/lib/uuid/pack.c \
	../e2fsprogs/lib/uuid/unpack.c \
	../e2fsprogs/lib/uuid/unparse.c \
	lzo-2.03/src/lzo1x_9x.c \

LOCAL_C_INCLUDES := \
	external/e2fsprogs/lib \
	external/mtd-utils/include \
	external/mtd-utils/ubi-utils/include \
	external/mtd-utils/lzo-2.03/include \

LOCAL_CFLAGS := -O2 -Wall \
	-DHAVE_INTTYPES_H \
	-DHAVE_UNISTD_H \
	-DHAVE_ERRNO_H \
	-DHAVE_NETINET_IN_H \
	-DHAVE_SYS_IOCTL_H \
	-DHAVE_SYS_MMAN_H \
	-DHAVE_SYS_MOUNT_H \
	-DHAVE_SYS_PRCTL_H \
	-DHAVE_SYS_RESOURCE_H \
	-DHAVE_SYS_SELECT_H \
	-DHAVE_SYS_STAT_H \
	-DHAVE_SYS_TYPES_H \
	-DHAVE_STDLIB_H \
	-DHAVE_STRDUP \
	-DHAVE_MMAP \
	-DHAVE_UTIME_H \
	-DHAVE_GETPAGESIZE \
	-DHAVE_LSEEK64 \
	-DHAVE_LSEEK64_PROTOTYPE \
	-DHAVE_EXT2_IOCTLS \
	-DHAVE_LINUX_FD_H \
	-DHAVE_TYPE_SSIZE_T \
	-DHAVE_SYS_TIME_H \
	-DHAVE_SYSCONF

LOCAL_STATIC_LIBRARIES := libz

LOCAL_MODULE := mkfs_ubifs
LOCAL_MODULE_TAGS := optional

include $(BUILD_HOST_EXECUTABLE)


include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
	lib/libcrc32.c \
	ubi-utils/dictionary.c \
	ubi-utils/libiniparser.c \
	ubi-utils/libubigen.c \
	ubi-utils/ubinize.c \
	ubi-utils/ubiutils-common.c \

LOCAL_C_INCLUDES := \
	external/mtd-utils/include \
	external/mtd-utils/ubi-utils/include \

LOCAL_CFLAGS := -O2 -Wall

LOCAL_MODULE := ubinize
LOCAL_MODULE_TAGS := optional

include $(BUILD_HOST_EXECUTABLE)




include $(CLEAR_VARS)

LOCAL_SRC_FILES := lib/libmtd.c \
                   lib/libmtd_legacy.c \
                   lib/libcrc32.c \
                   lib/libfec.c \

LOCAL_C_INCLUDES := \
        external/mtd-utils/include \
        external/mtd-utils/ubi-utils/include \

LOCAL_CFLAGS := -O2 -Wall

LOCAL_MODULE := libmtd

LOCAL_MODULE_TAGS := optional

include $(BUILD_STATIC_LIBRARY)



include $(CLEAR_VARS)

LOCAL_SRC_FILES := nandwrite.c

LOCAL_C_INCLUDES := \
        external/mtd-utils/include \
        external/mtd-utils/ubi-utils/include \

LOCAL_CFLAGS := -O2 -Wall

LOCAL_MODULE := nandwrite

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_LIBRARIES := libmtd 

include $(BUILD_EXECUTABLE)



include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
    ubi-utils/ubiattach.c \
    ubi-utils/libubi.c \
    ubi-utils/ubiutils-common.c

LOCAL_C_INCLUDES+= \
	external/mtd-utils/include \
	external/mtd-utils/ubi-utils/include

LOCAL_MODULE := ubiattach
LOCAL_MODULE_TAGS := optional
LOCAL_CFLAGS += -O3
LOCAL_STATIC_LIBRARIES := libcutils libc
LOCAL_FORCE_STATIC_EXECUTABLE := true
include $(BUILD_EXECUTABLE)



include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
    ubi-utils/ubidetach.c \
    ubi-utils/libubi.c \
    ubi-utils/ubiutils-common.c

LOCAL_C_INCLUDES+= \
	external/mtd-utils/include \
	external/mtd-utils/ubi-utils/include

LOCAL_MODULE := ubidetach
LOCAL_MODULE_TAGS := optional
LOCAL_CFLAGS += -O3
LOCAL_STATIC_LIBRARIES := libcutils libc
LOCAL_FORCE_STATIC_EXECUTABLE := true
include $(BUILD_EXECUTABLE)


include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
    ubi-utils/ubiformat.c \
	ubi-utils/libubi.c \
      ubi-utils/ubiutils-common.c \
	lib/libcrc32.c \
	lib/libmtd.c \
	lib/libmtd_legacy.c \
	ubi-utils/libscan.c \
	ubi-utils/libubigen.c


LOCAL_C_INCLUDES+= \
    $(LOCAL_PATH)/include \
    $(LOCAL_PATH)/ubi-utils/include \



LOCAL_MODULE:=ubiformat
LOCAL_CFLAGS+=-O3
LOCAL_MODULE_TAGS := optional
LOCAL_STATIC_LIBRARIES := libcutils libc
LOCAL_FORCE_STATIC_EXECUTABLE := true
include $(BUILD_EXECUTABLE)

#==================
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
    ubi-utils/ubimkvol.c \
	ubi-utils/libubi.c \
    ubi-utils/ubiutils-common.c 

LOCAL_C_INCLUDES+= \
    $(LOCAL_PATH)/include \
    $(LOCAL_PATH)/ubi-utils/include 


LOCAL_MODULE:=ubimkvol
LOCAL_CFLAGS+=-O3
LOCAL_MODULE_TAGS := optional
LOCAL_STATIC_LIBRARIES := libcutils libc
LOCAL_FORCE_STATIC_EXECUTABLE := true

include $(BUILD_EXECUTABLE)

endif


