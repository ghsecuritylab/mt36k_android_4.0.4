#the library
LOCAL_PATH :=$(call my-dir)
# ===========================================================
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-subdir-java-files) 

LOCAL_MODULE_TAGS := optional
LOCAL_MODULE := com.tcl.devicemanager
LOCAL_DEX_PREOPT :=false
# LOCAL_JAVA_LIBRARIES := services
LOCAL_JAVA_LIBRARIES := com.mediatekk.tvcm com.mediatek.tv com.mediatek.tv.custom
#LOCAL_STATIC_JAVA_LIBRARIES := libMtkCustom libMtkTv libMtkTvcm

include $(BUILD_JAVA_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES +=libMtkCustom:libs/com.mediatek.tv.custom.jar  libMtkTv:libs/com.mediatek.tv.jar libMtkTvcm:libs/com.mediatek.tvcm.jar
#include $(BUILD_MULTI_PREBUILT)




# the documentation
# ============================================================
include $(CLEAR_VARS)
LOCAL_SRC_FILES := $(call all-subdir-java-files) \
				   $(call all-subdir-html-files)

LOCAL_MODULE:= com.tcl.devicemanager
LOCAL_DEX_PREOPT :=false
LOCAL_DROIDDOC_OPTIONS := com.tcl.devicemanager

LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_DROIDDOC_USE_STANDARD_DOCLET := true

include $(BUILD_DROIDDOC)

# Install the created library in the right location.
# ============================================================
include $(CLEAR_VARS)

LOCAL_MODULE := com.tcl.devicemanager.xml
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT_ETC)/permissions
LOCAL_SRC_FILES := $(LOCAL_MODULE)

include $(BUILD_PREBUILT)

# ===============================================================
include $(CLEAR_VARS)

#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES +=libMtkCustom:libs/com.mediatek.tv.custom.jar  libMtkTv:libs/com.mediatek.tv.jar libMtkTvcm:libs/com.mediatek.tvcm.jar 
#include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))
