LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# librtspplayer
LOCAL_SRC_FILES :=      \
    src/RtspCmdSender.cpp \
    src/myLog.cpp    \
    src/MtkRtspClient.cpp   \
    src/ThreadObject.cpp  \
    src/Semaphore.cpp   \
    src/Mutex.cpp    \
    src/CmpbSink.cpp   \
    src/CmpbAACSink.cpp   \
    src/CmpbH264Sink.cpp   \
    src/CmpbLATMSink.cpp   \
    src/PushPlayer.cpp    \
    src/RtspPlayer.cpp    \
    src/myCfg.cpp       \
    
# Header files path
LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/include    \
    $(TOP)/external/liveMedia/liveMedia/include \
    $(TOP)/external/liveMedia/BasicUsageEnvironment/include   \
    $(TOP)/external/liveMedia/groupsock/include   \
    $(TOP)/external/liveMedia/UsageEnvironment/include \
    $(TOP)/external/curl/include \
    $(TOP)/external/curl/include/curl \
    $(TOP)/external/stlport/stlport \
    $(TOP)/external/stlport/src \
	  $(TOP)/bionic \
	  $(TOP)/bionic/libstdc++/include \
    $(TOP)/../dtv-android/tv_addon_ics/framework/cmpb        \
    $(TOP)/../dtv-android/tv_addon_ics/framework/cmpb/include \
    $(TOP)/external/openssl/include/openssl  \
    $(TOP)/external/expat/lib  \
    
LOCAL_LDFLAGS += -L$(ANDROID_BUILD_TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries \
	-lapp_if \
	-lapp_if_rpc \
	-lrpc_ipc \
	-lhandle_app \
	-ldtv_common \
	-lapp_if_rpc

LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libcmpb.a
LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libapp_if.a
LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libapp_if_rpc.a
LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libdtv_common.a    
LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libhandle_app.a
LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/librpc_ipc.a
 
LOCAL_SHARED_LIBRARIES :=     \
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
	libdtv_getline  \
	libjpeg \
  libz \
  libexpat \
  libssl \
  libcurl   \
  libstlport \
  libcrypto   \
  libliveMedia \
  libcrypto   

#  libfreetype \
#	libpng \
#	libpthread   \
#  libnet_info \
#  libcares \
#  libdirectfb \
#  libsecurestorage \
#	 libbasic_usage_environment \
#	 libgroupsock      \
#	 liblivemedia       \
#	 libusage_environment    \
    
LOCAL_CFLAGS := -g -Wall -O0 -mapcs-frame -DANDROID -pipe -fPIC -fsigned-char -D_CPU_LITTLE_ENDIAN_ -DUSE_SLIM_LIB -DHAVE_DTCP -DSLIM_BUILDING_LIBRARIES -DHAVE_WMDRM -DTARGET=10 -DUSE_SLIM_LIB -D_ANSI -D_CPU_LITTLE_ENDIAN_ -D_USE_ANDROID -D_USE_LINUX -D__arm
LOCAL_CXXFLAGS := -g -Wall -O0 -DANDROID -pipe -fPIC -fsigned-char -D_CPU_LITTLE_ENDIAN_ -mapcs-frame -DUSE_SLIM_LIB -DHAVE_DTCP -DSLIM_BUILDING_LIBRARIES -c -DHAVE_WMDRM -DTARGET=10 -DUSE_SLIM_LIB -D_ANSI -D_CPU_LITTLE_ENDIAN_ -D_USE_ANDROID -D_USE_LINUX -D__arm

#LOCAL_MODULE_TAGS := test

LOCAL_MODULE := librtspplayer

include $(BUILD_SHARED_LIBRARY)
ifneq "$(RLS_CUSTOM_BUILD)" "true"
include $(call all-makefiles-under,$(LOCAL_PATH))
endif

