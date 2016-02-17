LOCAL_PATH:= $(call my-dir)

# liukun make this file for make mediafile.jar
# ============================================================
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_MODULE:= mediafile

LOCAL_MODULE_TAGS := debug

#LOCAL_JAVA_LIBRARIES :=

#LOCAL_NO_EMMA_INSTRUMENT := true
#LOCAL_NO_EMMA_COMPILE := true

include $(BUILD_JAVA_LIBRARY)

include $(BUILD_DROIDDOC)
