LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS) 

LOCAL_C_INCLUDES:= $(JNI_H_INCLUDE) \
	$(LOCAL_PATH) \
	$(LOCAL_PATH)/../../../../../$(ANDROID_VERSION)/external/stlport/stlport \
	$(LOCAL_PATH)/../../../../../$(ANDROID_VERSION)/bionic \
	$(LOCAL_PATH)/../../../../../$(ANDROID_VERSION)/bionic/libstdc++/include \
	$(LOCAL_PATH)/../../../../../$(ANDROID_VERSION)/frameworks/base/include \
	$(LOCAL_PATH)/../../../../../$(ANDROID_VERSION)/system/core/include \	
#/usr/local/android/ndk/sources/cxx-stl/stlport/stlport/ \
#/home/mtk40058/project/opensource/froyo/frameworks/base/include/binder \
#/home/mtk40058/project/opensource/froyo/frameworks/base/include \
#/home/mtk40058/project/opensource/froyo/system/core/include
 
LOCAL_SRC_FILES:= PppoeDial.cpp PppoeService.cpp NetStatus.cpp\
os/Memory.cpp os/Mutex.cpp os/Thread.cpp os/Semaphore.cpp \


LOCAL_CFLAGS := -g -Wall -O0 -mapcs-frame -DANDROID -pipe -fPIC -fsigned-char -D_CPU_LITTLE_ENDIAN_ -D_ANSI -D_CPU_LITTLE_ENDIAN_ -D_USE_ANDROID -D_USE_LINUX -D__arm
LOCAL_CXXFLAGS := -g -Wall -O0 -DANDROID -pipe -fPIC -fsigned-char -D_CPU_LITTLE_ENDIAN_ -mapcs-frame -D_ANSI -D_CPU_LITTLE_ENDIAN_ -D_USE_ANDROID -D_USE_LINUX -D__arm



LOCAL_LDLIBS := -llog -ldl 

LOCAL_SHARED_LIBRARIES:= libutils libcutils libbinder libstlport

#LOCAL_LDLIBS += -L/home/mtk40058/project/opensource/froyo/out/target/product/generic/obj/lib -lbinder -lcutils -lutils -lc -lstlport

LOCAL_MODULE:= pppoe-dial 

include $(BUILD_EXECUTABLE)