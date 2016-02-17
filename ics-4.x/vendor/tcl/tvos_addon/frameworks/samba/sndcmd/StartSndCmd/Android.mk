LOCAL_PATH:= $(call my-dir)


include $(ANDROID_BUILD_TOP)/vendor/tcl/tvos_addon/tvos_base.mk
ifneq ($(call tcl-test-cpp-files),)

# use Source files.
# ============================================================
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
	start_sndcmdservice.cpp 

LOCAL_SHARED_LIBRARIES := \
	libutils \
	libSndCmd \
	libbinder \
	libcutils \
	liblog


LOCAL_MODULE_TAGS := optional
LOCAL_MODULE:= sndcmdservice_tcl

include $(BUILD_EXECUTABLE)

##{{cp binary
$(DEFAULT_GOAL): $(LOCAL_PATH)/sndcmdservice_tcl
all_modules: $(LOCAL_PATH)/sndcmdservice_tcl
$(LOCAL_PATH)/sndcmdservice_tcl: $(LOCAL_BUILT_MODULE)
	$(copy-file-to-target-with-cp)	
empty_dummy:
## cp binary}}

else
# use binary files.
# ============================================================
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_PREBUILT_EXECUTABLES := \
	sndcmdservice_tcl

include $(BUILD_MULTI_PREBUILT)

endif
