LOCAL_PATH:= $(call my-dir)

# the library
# ============================================================
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := debug
LOCAL_SRC_FILES := $(call all-java-files-under, src)
                
LOCAL_MODULE:= ad

include $(BUILD_JAVA_LIBRARY)
include $(BUILD_DROIDDOC)



