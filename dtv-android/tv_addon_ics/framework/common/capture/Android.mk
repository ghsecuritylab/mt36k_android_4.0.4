# This makefile shows how to build your own shared library that can be
# shipped on the system of a phone, and included additional examples of
# including JNI code with the library and writing client applications against it.

LOCAL_PATH := $(call my-dir)

# Copy the add-on library XML files in the system image.
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/com.mediatek.common.capture.xml:system/etc/permissions/com.mediatek.common.capture.xml

# the library
# ============================================================
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
            $(call all-subdir-java-files)


# This is the target being built.
LOCAL_MODULE:= com.mediatek.common.capture
LOCAL_SHARED_LIBRARIES += libskia

include $(BUILD_JAVA_LIBRARY)


# the documentation
# ============================================================
include $(CLEAR_VARS)

#LOCAL_SRC_FILES := 
LOCAL_C_INCLUDES += $(TOPDIR)external/skia/include
LOCAL_MODULE:= capture
LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_DROIDDOC_USE_STANDARD_DOCLET := true

# include $(BUILD_DROIDDOC)


# The JNI component
# ============================================================
# Also build all of the sub-targets under this one: the library's
# associated JNI code, and a sample client of the library.
include $(CLEAR_VARS)

include $(call all-makefiles-under,$(LOCAL_PATH))

