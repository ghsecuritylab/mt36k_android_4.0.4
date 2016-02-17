#
# Copyright (C) 2011 The Android Open Source Project
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

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

USE_TINYALSA := true

LOCAL_MODULE := audio.primary.mt5396
LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)/hw
LOCAL_MODULE_TAGS := optional

ifeq ($(BOARD_USES_MT53XX_AUDIO),true)
LOCAL_CFLAGS += -D_MT53XX_AUDIO
endif

ifeq ($(USE_TINYALSA),true)
LOCAL_SRC_FILES := audio_hw.c

LOCAL_C_INCLUDES += \
    external/tinyalsa/include \
    system/media/audio_utils/include \
    system/media/audio_effects/include

LOCAL_SHARED_LIBRARIES := liblog libcutils libdl libtinyalsa libaudioutils
else
LOCAL_SHARED_LIBRARIES := \
    libcutils \
    libutils \
    libmedia \
    libhardware_legacy

LOCAL_SHARED_LIBRARIES += libdl

LOCAL_SRC_FILES += \
    AudioHardwareGeneric.cpp

LOCAL_STATIC_LIBRARIES := \
    libmedia_helper

LOCAL_WHOLE_STATIC_LIBRARIES := \
    libaudiohw_legacy
endif

include $(BUILD_SHARED_LIBRARY)
