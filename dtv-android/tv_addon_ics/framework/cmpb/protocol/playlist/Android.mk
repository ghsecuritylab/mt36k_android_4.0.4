LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# libhttplive
LOCAL_SRC_FILES :=      \
    src/http_live_streaming.cpp    \
    src/Playlist.cpp  \
    src/PlaylistPlayer.cpp       \
    src/Semaphore.cpp    \
    src/ThreadObject.cpp  \
    src/StreamSelector.cpp \
    src/StreamSelectorBivl.cpp    

# Header files path
LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/include    \
    $(LOCAL_PATH)/../../include \
    $(LOCAL_PATH)/../../        \
    $(TOP)/external/curl/include \
    $(TOP)/external/curl/include/curl \
    $(TOP)/external/stlport/stlport \
    $(TOP)/external/stlport/src \
    $(TOP)/external/cmpb/protocol/inet_inc \
	  $(TOP)/bionic \
	  $(TOP)/bionic/libstdc++/include \
    $(TOP)/../dtv-android/tv_addon_ics/framework/cmpb        \
    $(TOP)/../dtv-android/tv_addon_ics/framework/cmpb/include \
    $(TOP)/external/openssl/include/   \
    $(TOP)/external/expat/lib  \
    
LOCAL_LDFLAGS += -L$(ANDROID_BUILD_TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries \
	-lapp_if \
	-lapp_if_rpc \
	-lrpc_ipc \
	-lhandle_app \
	-ldtv_common \
	-lapp_if_rpc \
	-llog

LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libcmpb.a
LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libapp_if.a
LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libapp_if_rpc.a
LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libdtv_common.a    
LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libhandle_app.a
LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/librpc_ipc.a
 
LOCAL_SHARED_LIBRARIES :=     		\
	libcutils             			\
	libutils              			\
	libbinder             			\
	libvorbisidec         			\
	libsonivox            			\
	libmedia              			\
	libandroid_runtime    			\
	libstagefright        			\
	libstagefright_omx    			\
	libsurfaceflinger_client        \
	libdtv_getline              \
	libcrypto                      
    
LOCAL_CFLAGS := -g -Wall -O0 -mapcs-frame -DANDROID -pipe -fPIC -fsigned-char -D_CPU_LITTLE_ENDIAN_ -DUSE_SLIM_LIB -DHAVE_DTCP -DSLIM_BUILDING_LIBRARIES -DHAVE_WMDRM -DTARGET=10 -DUSE_SLIM_LIB -D_ANSI -D_CPU_LITTLE_ENDIAN_ -D_USE_ANDROID -D_USE_LINUX -D__arm
LOCAL_CXXFLAGS := -g -Wall -O0 -DANDROID -pipe -fPIC -fsigned-char -D_CPU_LITTLE_ENDIAN_ -mapcs-frame -DUSE_SLIM_LIB -DHAVE_DTCP -DSLIM_BUILDING_LIBRARIES -c -DHAVE_WMDRM -DTARGET=10 -DUSE_SLIM_LIB -D_ANSI -D_CPU_LITTLE_ENDIAN_ -D_USE_ANDROID -D_USE_LINUX -D__arm
LOCAL_SHARED_LIBRARIES += libstlport libcurl 

#LOCAL_MODULE_TAGS := tests

LOCAL_MODULE := libhttplive
APP_STL := stlport_static

include $(BUILD_SHARED_LIBRARY)

include $(call all-makefiles-under,$(LOCAL_PATH))
