#
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

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := ip_up.c
LOCAL_SHARED_LIBRARIES := libcutils
LOCAL_MODULE := ip-up
LOCAL_MODULE_PATH := $(TARGET_OUT_ETC)/ppp
LOCAL_MODULE_TAGS := optional

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/../ \
	$(LOCAL_PATH)/../include \
	$(LOCAL_PATH)/../pppd \
	$(LOCAL_PATH)/../pppd/plugins/pppoe \
	$(LOCAL_PATH)/../../openssl/include/ \
	$(LOCAL_PATH)/../../openssl/include/openssl

include $(BUILD_EXECUTABLE)
#===================================================
include $(CLEAR_VARS)

LOCAL_SRC_FILES := ip_down.c
LOCAL_SHARED_LIBRARIES := libcutils
LOCAL_MODULE := ip-down
LOCAL_MODULE_PATH := $(TARGET_OUT_ETC)/ppp
LOCAL_MODULE_TAGS := optional

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/../ \
	$(LOCAL_PATH)/../include \
	$(LOCAL_PATH)/../pppd \
	$(LOCAL_PATH)/../pppd/plugins/pppoe \
	$(LOCAL_PATH)/../../openssl/include/ \
	$(LOCAL_PATH)/../../openssl/include/openssl

include $(BUILD_EXECUTABLE)
