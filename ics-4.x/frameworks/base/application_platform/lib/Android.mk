

# liukun make this file for prebuilt libs & jars

LOCAL_PATH := $(call my-dir)


#copy res
include $(CLEAR_VARS)

#ifeq ($(TARGET_ARCH), arm)
#PRODUCT_COPY_FILES += arm/Resource.irf:system/etc/Resource.irf
#else
#PRODUCT_COPY_FILES += mips/Resource.irf:system/etc/Resource.irf
#endif

LOCAL_MODULE := Resource
ifeq ($(TARGET_ARCH), arm)
LOCAL_SRC_FILES := arm/Resource.irf
else
LOCAL_SRC_FILES := mips/Resource.irf
endif
 
LOCAL_MODULE_PATH := $(TARGET_OUT)/etc
 
LOCAL_MODULE_TAGS := debug 
LOCAL_MODULE_CLASS := SHARED_LIBRARIES 
LOCAL_MODULE_SUFFIX:= .irf 
 

#LOCAL_CERTIFICATE:= PRESIGNED 
#LOCAL_PRELINK_MODULE := false 
 
include $(BUILD_PREBUILT)  



# build for libs
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := debug


ifeq ($(TARGET_ARCH), arm)
LOCAL_PREBUILT_LIBS := arm/libAisound.so \
			arm/libtmfe30.so \
			arm/libtmsr30.so
else
LOCAL_PREBUILT_LIBS := mips/libAisound.so \
			mips/libtmfe30.so \
			mips/libtmsr30.so
endif

LOCAL_PREBUILT_LIBS += libforcetv.so \
			libfaceDetection.so \
			libsim-mouse.so \
			haarstage.data

include $(BUILD_MULTI_PREBUILT)

#build for jars
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := debug

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := libgesture:gesturelib.jar \
					libhomegallery:home_gallery_lib.jar \
					lib4CS:lib4CS.jar \
					lib4DBG:lib4DBG.jar \
					lib4XML:lib4XML.jar \
					libshakeanim:shake_anim_lib.jar \
					libsumf:umfservice.jar \
					libSandUtils:SandUtils.jar \
					libMTdm:com.mediatek.dm.jar \
					libMTcustom:com.mediatek.tv.custom.jar \
					libMTtv:com.mediatek.tv.jar

include $(BUILD_MULTI_PREBUILT)

