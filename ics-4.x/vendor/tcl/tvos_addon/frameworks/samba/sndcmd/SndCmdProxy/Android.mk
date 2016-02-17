LOCAL_PATH:= $(call my-dir)


include $(ANDROID_BUILD_TOP)/vendor/tcl/tvos_addon/tvos_base.mk
ifneq ($(call tcl-test-cpp-files),)

# use Source files.
# ============================================================
include $(CLEAR_VARS)
LOCAL_SRC_FILES:= BpSndCmdService.cpp 
LOCAL_SHARED_LIBRARIES:= \
	libSndCmd \
	libutils \
	libcutils \
	libbinder \
	libandroid_runtime

LOCAL_MODULE:= libBpSndCmd
LOCAL_MODULE_TAGS := optional
LOCAL_PRELINK_MODULE:= false

include $(BUILD_SHARED_LIBRARY)

##{{cp binary
$(DEFAULT_GOAL): $(LOCAL_PATH)/libBpSndCmd.so
all_modules: $(LOCAL_PATH)/libBpSndCmd.so
$(LOCAL_PATH)/libBpSndCmd.so: $(LOCAL_BUILT_MODULE)
	$(copy-file-to-target-with-cp)	
empty_dummy:
## cp binary}}

else
# use binary files.
# ============================================================
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_PREBUILT_LIBS := \
	libBpSndCmd.so

include $(BUILD_MULTI_PREBUILT)

endif


