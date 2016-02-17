LOCAL_PATH:= $(call my-dir)


include $(ANDROID_BUILD_TOP)/vendor/tcl/tvos_addon/tvos_base.mk
ifneq ($(call tcl-test-cpp-files),)

# use Source files.
# ============================================================
include $(CLEAR_VARS)
LOCAL_SRC_FILES:= SndCmdService.cpp 
LOCAL_SHARED_LIBRARIES:= \
	libutils \
	libcutils \
	libbinder \
	libandroid_runtime

LOCAL_MODULE:= libSndCmd
LOCAL_PRELINK_MODULE:= false
LOCAL_MODULE_TAGS := optional
include $(BUILD_SHARED_LIBRARY)

##{{cp binary
$(DEFAULT_GOAL): $(LOCAL_PATH)/libSndCmd.so
all_modules: $(LOCAL_PATH)/libSndCmd.so
$(LOCAL_PATH)/libSndCmd.so: $(LOCAL_BUILT_MODULE)
	$(copy-file-to-target-with-cp)	
empty_dummy:
## cp binary}}

else
# use binary files.
# ============================================================
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_PREBUILT_LIBS := \
	libSndCmd.so

include $(BUILD_MULTI_PREBUILT)

endif


