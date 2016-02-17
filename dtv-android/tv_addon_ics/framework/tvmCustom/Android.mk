# This makefile shows how to build your own shared library that can be
# shipped on the system of a phone, and included additional examples of
# including JNI code with the library and writing client applications against it.

LOCAL_PATH := $(call my-dir)

# Copy the add-on library XML files in the system image.
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/com.mediatek.tv.custom.xml:system/etc/permissions/com.mediatek.tv.custom.xml

# the library
# ============================================================
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
            $(call all-java-files-under,java) 

# LOCAL_MODULE_TAGS := optional

LOCAL_MODULE:= com.mediatek.tv.custom

include $(BUILD_JAVA_LIBRARY)


# the documentation
# ============================================================
include $(CLEAR_VARS)


LOCAL_MODULE:= tvCustom
LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_DROIDDOC_USE_STANDARD_DOCLET := true

LOCAL_SRC_FILES := \
            $(call all-java-files-under,java)


# include $(BUILD_DROIDDOC)


# The JNI component
# ============================================================
# Also build all of the sub-targets under this one: the library's
# associated JNI code, and a sample client of the library.
include $(CLEAR_VARS)

include $(call all-makefiles-under,$(LOCAL_PATH))

