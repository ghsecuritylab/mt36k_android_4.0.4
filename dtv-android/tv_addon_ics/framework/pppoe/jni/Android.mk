LOCAL_PATH:= $(call my-dir)

 

include $(CLEAR_VARS)

 

LOCAL_SRC_FILES:=Dial.cpp DialManager.cpp

LOCAL_C_INCLUDES:= $(JNI_H_INCLUDE) \
	$(LOCAL_PATH) \
	$(LOCAL_PATH)/../../../../../$(ANDROID_VERSION)/external/stlport/stlport \
	$(LOCAL_PATH)/../../../../../$(ANDROID_VERSION)/bionic \
	$(LOCAL_PATH)/../../../../../$(ANDROID_VERSION)/bionic/libstdc++/include \
	$(LOCAL_PATH)/../../../../../$(ANDROID_VERSION)/frameworks/base/include \
	$(LOCAL_PATH)/../../../../../$(ANDROID_VERSION)/system/core/include \	
#/usr/local/android/ndk/sources/cxx-stl/stlport/stlport/ \
#/home/mtk40058/project/opensource/froyo/frameworks/base/include \
#/home/mtk40058/project/opensource/froyo/system/core/include

LOCAL_SHARED_LIBRARIES := libutils libcutils libbinder libstlport

 
#LOCAL_LDLIBS += -L/home/mtk40058/project/opensource/froyo/out/target/product/generic/obj/lib -lbinder -lutils -lcutils

LOCAL_MODULE := libpppoedial
 

LOCAL_PRELINK_MODULE:= false

 
#APP_STL := stlport_static
#LOCAL_LDLIBS += -L/usr/local/android/ndk/sources/cxx-stl/stlport/libs/armeabi -lstlport_static

include $(BUILD_SHARED_LIBRARY)

