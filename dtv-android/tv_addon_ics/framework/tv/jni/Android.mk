#
# Copyright (C) 2008 The Android Open Source Project
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

# This makefile supplies the rules for building a library of JNI code for
# use by our example platform shared library.

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# LOCAL_MODULE_TAGS := optional

# This is the target being built.
LOCAL_MODULE:= libcom_mediatek_tv_jni

# All of the source files that we will compile.
LOCAL_SRC_FILES:= \
	com_mediatek_tv_service_config_service.c \
	com_mediatek_tv_service_channel_service.c \
	com_mediatek_tv_service_broadcast_service.c \
	com_mediatek_tv_service_scan_service.c \
	com_mediatek_tv_service_dvbc_scan_service.c \
	com_mediatek_tv_service_dtmb_scan_service.c	\
	com_mediatek_tv_service_input_service.c \
	com_mediatek_tv_service_osd_service.c \
	com_mediatek_tv_service_factory_service.c \
	com_mediatek_tv_service_event_service.c \
	com_mediatek_tv_service_ci_service.c \
	com_mediatek_tv_service_component_service.c \
	tv_jni_util.c \
	onload.c
	
# All of the shared libraries we link against.
LOCAL_SHARED_LIBRARIES := \
	libandroid_runtime \
	libnativehelper \
	libcutils \
	libutils \
	libsqlite \
	libdtv_getline

# definiing LOCAL_STATIC_LIBRARIES causes errors:
# No rule to make target "NOTICE-TARGET-STATIC_LIBRARIES-libXYZ" ???
# where libXYZ is ecah library in LOCAL_STATIC_LIBRARIES
#
LOCAL_LDFLAGS += -L$(ANDROID_BUILD_TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries \
	-lapp_if \
	-lapp_if_rpc \
	-lrpc_ipc \
	-lhandle_app \
	-ldtv_common

## LOCAL_PREBUILT_LIBS

#LOCAL_WHOLE_STATIC_LIBRARIES := \
#	libapp_if \
#	libapp_if_rpc \
#	librpc_ipc \
#	libhandle_app \
#	libdtv_common

# No static libraries.
#LOCAL_STATIC_LIBRARIES :=

ifdef INC_ROOT
C_INC = $(INC_ROOT)/c_inc
X_INC = $(INC_ROOT)/x_inc
else
C_INC = $(VM_LINUX_ROOT)/project_x/c_inc
X_INC = $(VM_LINUX_ROOT)/project_x/x_inc
endif


ifndef OBJECT_TYPE
ifndef BUILD_CFG
	export OBJECT_TYPE := rel
else
	export OBJECT_TYPE := $(BUILD_CFG)
endif
endif

# Also need the JNI headers.
ifneq "$(ANDROID_BUILD)" "true"
LOCAL_C_INCLUDES += \
	$(C_INC) \
	$(X_INC) \
	$(VM_LINUX_ROOT)/project_x/middleware \
	$(VM_LINUX_ROOT)/project_x/middleware/inc \
	$(VM_LINUX_ROOT)/dtv_linux/project_x_linux/dtv_svc_client \
	$(VM_LINUX_ROOT)/dtv_linux/project_x_linux/dtv_svc_client/custom/$(ANDROID_CUSTOMER) \
	$(ANDROID_BUILD_TOP)/dalvik/vm \
	$(JNI_H_INCLUDE)
else
LOCAL_C_INCLUDES += \
	$(ANDROID_BUILD_ROOT)/../../output/$(CUSTOMER)/$(MODEL_NAME)/$(OBJECT_TYPE)/inc/c_inc \
	$(ANDROID_BUILD_ROOT)/../../output/$(CUSTOMER)/$(MODEL_NAME)/$(OBJECT_TYPE)/inc/x_inc \
	$(ANDROID_BUILD_ROOT)/../../project_x/middleware \
	$(ANDROID_BUILD_ROOT)/../../project_x/middleware/inc \
	$(ANDROID_BUILD_ROOT)/../../dtv_linux/project_x_linux/dtv_svc_client \
	$(ANDROID_BUILD_ROOT)/../../dtv_linux/project_x_linux/dtv_svc_client/custom/$(ANDROID_CUSTOMER) \
	$(ANDROID_BUILD_TOP)/dalvik/vm \
	$(JNI_H_INCLUDE)
endif

# No specia compiler flags.
LOCAL_CFLAGS += -D_CPU_LITTLE_ENDIAN_

# Don't prelink this library.  For more efficient code, you may want
# to add this library to the prelink map and set this to true.
#LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)
