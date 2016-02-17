##
##
## Copyright 2008, The Android Open Source Project
##
## Redistribution and use in source and binary forms, with or without
## modification, are permitted provided that the following conditions
## are met:
##  * Redistributions of source code must retain the above copyright
##    notice, this list of conditions and the following disclaimer.
##  * Redistributions in binary form must reproduce the above copyright
##    notice, this list of conditions and the following disclaimer in the
##    documentation and/or other materials provided with the distribution.
##
## THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS ``AS IS'' AND ANY
## EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
## IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
## PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL APPLE COMPUTER, INC. OR
## CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
## EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
## PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
## PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
## OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
## (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
## OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
##

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

DEFINES += 

LOCAL_SRC_FILES := mtkphotoplayer.cpp

LOCAL_C_INCLUDES += \
	$(JNI_H_INCLUDE) \
	$(LOCAL_PATH) \
	$(LOCAL_PATH)/../../../../../../$(ANDROID_VERSION)/bionic \
	$(LOCAL_PATH)/../../../../../../$(ANDROID_VERSION)/bionic/libstdc++/include \
	$(LOCAL_PATH)/../../../../../../$(ANDROID_VERSION)/frameworks/base/include \
	$(LOCAL_PATH)/../../../../../../$(ANDROID_VERSION)/system/core/include \
	$(LOCAL_PATH)/../../../../../../../../vm_linux/chiling/app_if/mtal/mtal_inc \
	$(LOCAL_PATH)/../../../../../../../../vm_linux/chiling/app_if/mtal/include




LOCAL_SHARED_LIBRARIES := libnativehelper libutils libcutils libutils libmtal_dynamic
#LOCAL_STATIC_LIBRARIES := libmtal_static

LOCAL_CFLAGS := -g -Wall -O0 -mapcs-frame -DANDROID -pipe -fPIC -fsigned-char -D_CPU_LITTLE_ENDIAN_ -D_ANSI -D_CPU_LITTLE_ENDIAN_ -D_USE_ANDROID -D_USE_LINUX -D__arm -DDEBUG_LOG
LOCAL_CXXFLAGS := -g -Wall -O0 -mapcs-frame -DANDROID -pipe -fPIC -fsigned-char -D_CPU_LITTLE_ENDIAN_ -D_ANSI -D_CPU_LITTLE_ENDIAN_ -D_USE_ANDROID -D_USE_LINUX -D__arm -DDEBUG_LOG
LOCAL_PRELINK_MODULE:=false

LOCAL_MODULE:= libMtkPhotoPlayer

LOCAL_LDLIBS := -llog -ldl 

LOCAL_LDLIBS += -L$(LOCAL_PATH)/

#LOCAL_SHARED_LIBRARIES += libskia

#LOCAL_LDLIBS += -L$(ANDROID_TOP)/out/target/product/$(IC_SETTING)/obj/STATIC_LIBRARIES/libmtal_static_intermediates/ -lmtal_static

include $(BUILD_SHARED_LIBRARY)

