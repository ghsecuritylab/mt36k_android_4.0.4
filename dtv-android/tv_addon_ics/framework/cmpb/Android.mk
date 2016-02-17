LOCAL_PATH:= $(call my-dir)

#
# libmtkcmpb
#

include $(CLEAR_VARS)

LOCAL_SRC_FILES:=               \
    CmpbPlayer.cpp              \
    CmpbMetadataRetriever.cpp   \
    Cmpb_protocol.cpp

ifeq ($(TARGET_OS)-$(TARGET_SIMULATOR),linux-true)
LOCAL_LDLIBS += -ldl -lpthread
endif

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
	libgui           							\
	libdtv_getline    
	
   
ifneq ($(TARGET_SIMULATOR),true)
LOCAL_SHARED_LIBRARIES += libdl
endif

LOCAL_SHARED_LIBRARIES += libstlport  libhttplive  librtspplayer  libvospplayer  libcrypto libcurl

LOCAL_C_INCLUDES :=                                                 \
	$(JNI_H_INCLUDE)                                                \
	$(call include-path-for, graphics corecg)                       \
	$(TOP)/external/opencore/extern_libs_v2/khronos/openmax/include \
	$(TOP)/frameworks/base/media/libstagefright/include             \
    $(TOP)/external/tremolo/Tremolo                                 \
    $(TOP)/../dtv-android/tv_addon_ics/framework/cmpb        \
    $(TOP)/../dtv-android/tv_addon_ics/framework/cmpb/include \
    $(TOP)/../dtv-android/tv_addon_ics/framework/cmpb/protocol/playlist/include \
    $(TOP)/../dtv-android/tv_addon_ics/framework/cmpb/protocol/playlist/src \
    $(TOP)/../dtv-android/tv_addon_ics/framework/cmpb/protocol/rtspplayer/include \
    $(TOP)/../dtv-android/tv_addon_ics/framework/cmpb/protocol/vospplayer/include \
    $(TOP)/../dtv-android/tv_addon_ics/framework/cmpb/protocol/inet_inc \
    $(TOP)/external/liveMedia/liveMedia/include \
    $(TOP)/external/liveMedia/BasicUsageEnvironment/include   \
    $(TOP)/external/liveMedia/groupsock/include   \
    $(TOP)/external/liveMedia/UsageEnvironment/include \
	  $(TOP)/bionic \
	  $(TOP)/bionic/libstdc++/include \
    $(TOP)/external/stlport/stlport \
    $(TOP)/external/stlport/src \
    $(TOP)/external/curl/include \
    $(TOP)/external/curl/include/curl \
    $(TOP)/external/openssl/include   \
    $(TOP)/external/expat/lib  \
    

LOCAL_CFLAGS := -g -Wall -O0 -mapcs-frame -DANDROID -pipe -fPIC -fsigned-char -D_CPU_LITTLE_ENDIAN_ -DUSE_SLIM_LIB -DHAVE_DTCP -DSLIM_BUILDING_LIBRARIES -DHAVE_WMDRM -DTARGET=10 -DUSE_SLIM_LIB -D_ANSI -D_CPU_LITTLE_ENDIAN_ -D_USE_ANDROID -D_USE_LINUX -D__arm
LOCAL_CXXFLAGS := -g -Wall -O0 -DANDROID -pipe -fPIC -fsigned-char -D_CPU_LITTLE_ENDIAN_ -mapcs-frame -DUSE_SLIM_LIB -DHAVE_DTCP -DSLIM_BUILDING_LIBRARIES -c -DHAVE_WMDRM -DTARGET=10 -DUSE_SLIM_LIB -D_ANSI -D_CPU_LITTLE_ENDIAN_ -D_USE_ANDROID -D_USE_LINUX -D__arm

ifeq ($(OSD_RESOLUTION),720p)
LOCAL_CFLAGS   += -DOSD_RESOLUTION_CMPB_720P
LOCAL_CXXFLAGS += -DOSD_RESOLUTION_CMPB_720P
else
LOCAL_CFLAGS   += -DOSD_RESOLUTION_CMPB_1080P
LOCAL_CXXFLAGS += -DOSD_RESOLUTION_CMPB_1080P
endif

#LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libcmpb.a
#LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libapp_if.a
##LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libapp_if_rpc.a
#LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libdtv_common.a    
#LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libhandle_app.a
#LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/librpc_ipc.a

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

#LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libcmpb.a
#LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libapp_if.a
#LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libapp_if_rpc.a
#LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libdtv_common.a    
#LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/libhandle_app.a
#LOCAL_LDFLAGS += $(TOP)/device/mtk/$(IC_SETTING)/DTV_OUT/static_libraries/librpc_ipc.a


#LOCAL_LDLIBS += -lcmpb
#LOCAL_LDLIBS += -lapp_if
#LOCAL_LDLIBS += -lapp_if_rpc
#LOCAL_LDLIBS += -ldtv_common
#LOCAL_LDLIBS += -lhandle_app
#LOCAL_LDLIBS += -lrpc_ipc

LOCAL_MODULE:= libmtkcmpb

include $(BUILD_SHARED_LIBRARY)

include $(call all-makefiles-under,$(LOCAL_PATH))
