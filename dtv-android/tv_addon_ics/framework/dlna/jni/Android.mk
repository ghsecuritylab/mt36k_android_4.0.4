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

#async std-io or smbc-io 
DEFINES += 



LOCAL_SRC_FILES := dmp.cpp dmp_content.cpp dmp_fm.cpp \
	dlna/Event.cpp dlna/EventManager.cpp \
	os/Memory.cpp os/Mutex.cpp os/Thread.cpp os/Semaphore.cpp \

LOCAL_C_INCLUDES += \
	$(JNI_H_INCLUDE) \
	$(LOCAL_PATH) \
	$(LOCAL_PATH)/inc \
	$(LOCAL_PATH)/../../../../../$(ANDROID_VERSION)/external/stlport/stlport \
	$(LOCAL_PATH)/../../../../../$(ANDROID_VERSION)/bionic \
	$(LOCAL_PATH)/../../../../../$(ANDROID_VERSION)/bionic/libstdc++/include \
#	/usr/local/android/ndk/sources/cxx-stl/system/include/  \
#	/usr/local/android/ndk/sources/cxx-stl/gnu-libstdc++/libs/armeabi/include/ \
#	/usr/local/android/ndk/sources/cxx-stl/gnu-libstdc++/include/ \




LOCAL_SHARED_LIBRARIES := libnativehelper libutils

LOCAL_CFLAGS := -g -Wall -O0 -mapcs-frame -DANDROID -pipe -fPIC -fsigned-char -D_CPU_LITTLE_ENDIAN_ -DUSE_SLIM_LIB -DHAVE_DTCP -DSLIM_BUILDING_LIBRARIES -DHAVE_WMDRM -DTARGET=10 -DUSE_SLIM_LIB -D_ANSI -D_CPU_LITTLE_ENDIAN_ -D_USE_ANDROID -D_USE_LINUX -D__arm
LOCAL_CXXFLAGS := -g -Wall -O0 -DANDROID -pipe -fPIC -fsigned-char -D_CPU_LITTLE_ENDIAN_ -mapcs-frame -DUSE_SLIM_LIB -DHAVE_DTCP -DSLIM_BUILDING_LIBRARIES -c -DHAVE_WMDRM -DTARGET=10 -DUSE_SLIM_LIB -D_ANSI -D_CPU_LITTLE_ENDIAN_ -D_USE_ANDROID -D_USE_LINUX -D__arm
LOCAL_PRELINK_MODULE:=false

LOCAL_MODULE:= libdlnadmp

#LOCAL_MODULE_TAGS := optional

LOCAL_LDLIBS := -llog -ldl

LOCAL_LDLIBS += -L$(LOCAL_PATH)/lib/ -ldlna -ldtcp -lave_slim -lpeer

LOCAL_LDFLAGS += -L$(LOCAL_PATH)/lib/ -ldlna -ldtcp -lave_slim -lpeer

LOCAL_SHARED_LIBRARIES += libexpat libstlport

APP_STL := stlport_static
#LOCAL_LDLIBS += -L/usr/local/android/ndk/sources/cxx-stl/stlport/libs/armeabi -lstlport_static

include $(BUILD_SHARED_LIBRARY)

