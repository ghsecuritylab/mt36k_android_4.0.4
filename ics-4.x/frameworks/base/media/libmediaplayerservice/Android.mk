LOCAL_PATH:= $(call my-dir)

#
# libmediaplayerservice
#

include $(CLEAR_VARS)

LOCAL_SRC_FILES:=               \
    MediaRecorderClient.cpp     \
    MediaPlayerService.cpp      \
    MetadataRetrieverClient.cpp \
    TestPlayerStub.cpp          \
    MidiMetadataRetriever.cpp   \
    MidiFile.cpp                \
    StagefrightPlayer.cpp       \
    StagefrightRecorder.cpp

LOCAL_SHARED_LIBRARIES :=     		\
	libcutils             			\
	libutils              			\
	libbinder             			\
	libvorbisidec         			\
	libsonivox            			\
	libmedia              			\
	libcamera_client      			\
	libandroid_runtime    			\
	libstagefright        			\
	libstagefright_omx    			\
	libstagefright_foundation       \
	libgui                          \
	libdl

LOCAL_STATIC_LIBRARIES := \
        libstagefright_nuplayer                 \
        libstagefright_rtsp                     \

LOCAL_SHARED_LIBRARIES += libmtkcmpb

LOCAL_C_INCLUDES :=                                                 \
	$(JNI_H_INCLUDE)                                                \
	$(call include-path-for, graphics corecg)                       \
	$(TOP)/frameworks/base/include/media/stagefright/openmax \
	$(TOP)/frameworks/base/media/libstagefright/include             \
	$(TOP)/frameworks/base/media/libstagefright/rtsp                \
        $(TOP)/external/tremolo/Tremolo \
        ${TV_ADDON_TOP}/framework/cmpb/include \
    ${TV_ADDON_TOP}/framework/cmpb/ \
    ${TV_ADDON_TOP}/framework/cmpb/protocol/playlist/include \
    ${TV_ADDON_TOP}/framework/cmpb/protocol/playlist/src \
    ${TV_ADDON_TOP}/framework/cmpb/protocol/rtspplayer/include \
    ${TV_ADDON_TOP}/framework/cmpb/protocol/vospplayer/include \
    ${TV_ADDON_TOP}/framework/cmpb/protocol/inet_inc \
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

LOCAL_MODULE:= libmediaplayerservice

include $(BUILD_SHARED_LIBRARY)

include $(call all-makefiles-under,$(LOCAL_PATH))

