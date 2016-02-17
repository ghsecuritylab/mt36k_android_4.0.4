LOCAL_PATH := $(call my-dir)


include $(ANDROID_BUILD_TOP)/vendor/tcl/tvos_addon/tvos_base.mk

include $(CLEAR_VARS)

LOCAL_MODULE := com.tcl.tvos.addon
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := 

LOCAL_JAVA_LIBRARIES := services

LOCAL_STATIC_JAVA_LIBRARIES := \
	pre_com.tcl.tvos.addon

include $(BUILD_JAVA_LIBRARY)


ifneq ($(call tcl-test-java-files),)
# use Source files.
# ============================================================

# Build the Java library.
# ============================================================
include $(CLEAR_VARS)

LOCAL_MODULE := pre_com.tcl.tvos.addon
LOCAL_DEX_PREOPT := false
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    $(call all-java-files-under) \
    $(call all-Iaidl-files-under)


LOCAL_STATIC_JAVA_LIBRARIES := \
    libjcifs

LOCAL_JAVA_LIBRARIES := com.mediatekk.tvcm com.mediatek.tv com.mediatek.tv.custom

#LOCAL_SHARED_LIBRARIES := \
#	libsndcmd_jni \
#	libdeviceinfo_jni
#	libwifimultsupport_jni

#LOCAL_REQUIRED_MODULES := \
#    libethernet_jni \
#    libvideoset

LOCAL_DEX_PREOPT := false

include $(BUILD_STATIC_JAVA_LIBRARY)

$(DEFAULT_GOAL): $(LOCAL_PATH)/classes.jar
all_modules: $(LOCAL_PATH)/classes.jar
$(LOCAL_PATH)/classes.jar: $(LOCAL_BUILT_MODULE)
	$(copy-file-to-target-with-cp)

else

# use binary files.
# ============================================================

include $(CLEAR_VARS)
LOCAL_MODULE := pre_com.tcl.tvos.addon
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_MODULE_PATH := $(TARGET_OUT_JAVA_LIBRARIES)
LOCAL_SRC_FILES := classes.jar
include $(BUILD_PREBUILT)


endif


# The documentation
# ============================================================
include $(CLEAR_VARS)

LOCAL_MODULE := com.tcl.tvos.addon_doc
LOCAL_DROIDDOC_OPTIONS := com.tcl.tvos.addon
LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_DROIDDOC_USE_STANDARD_DOCLET := true

LOCAL_SRC_FILES := \
    $(call all-java-files-under) \
    $(call all-html-files-under)


include $(BUILD_DROIDDOC)

# Install the created library in the right location.
# ============================================================
include $(CLEAR_VARS)

LOCAL_MODULE := com.tcl.tvos.addon.xml
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT_ETC)/permissions
LOCAL_SRC_FILES := $(LOCAL_MODULE)

include $(BUILD_PREBUILT)

