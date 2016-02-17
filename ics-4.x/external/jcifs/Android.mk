LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
# version 1.3.17
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/jcifs.xml:system/etc/permissions/jcifs.xml

LOCAL_MODULE_TAGS := eng

#LOCAL_PRELINK_MODULE := true

LOCAL_SRC_FILES := $(call all-java-files-under,src/jcifs)
LOCAL_JAVA_LIBRARIES := javax.obex	

LOCAL_MODULE := jcifs
LOCAL_CERTIFICATE := platform
include $(BUILD_JAVA_LIBRARY)

