# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

ifdef MOUNT_EXFAT_BUILD

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS	:= -D_FILE_OFFSET_BITS=64 -DFUSE_USE_VERSION=26 -D_CPU_LITTLE_ENDIAN
LOCAL_MODULE    := libexfat
LOCAL_SRC_FILES :=     \
libexfat/log.c       \
libexfat/cluster.c   \
libexfat/utils.c     \
libexfat/mount.c     \
libexfat/lookup.c    \
libexfat/utf.c       \
libexfat/node.c      \
libexfat/io.c        \
LOCAL_C_INCLUDES := $(LOCAL_PATH)/libexfat $(LOCAL_PATH)/../fuse-android/include


include $(BUILD_STATIC_LIBRARY)
#include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_CFLAGS	:= -D_FILE_OFFSET_BITS=64 -D_CPU_LITTLE_ENDIAN

LOCAL_C_INCLUDES := $(LOCAL_PATH)/libexfat \
$(LOCAL_PATH)/libexfat $(LOCAL_PATH)/../fuse-android/include

LOCAL_MODULE    := mount.exfat
LOCAL_SRC_FILES := fuse/main.c
LOCAL_STATIC_LIBRARIES := libexfat libfuse

include $(BUILD_EXECUTABLE)

endif