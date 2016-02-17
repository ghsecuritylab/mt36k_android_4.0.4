#-----------------------------------------------------------------------------
# Copyright (c) 2009, MediaTek Inc.
# All rights reserved.
#
# Unauthorized use, practice, perform, copy, distribution, reproduction,
# or disclosure of this information in whole or in part is prohibited.
#-----------------------------------------------------------------------------
# $RCSfile:  $
# $Revision:
# $Date:
# $Author:  $
# $CCRevision:  $
# $SWAuthor:  $
# $MD5HEX:  $
#
# Description:
#---------------------------------------------------------------------------*/

include ../../mak/target.mak

ifndef VM_LINUX_ROOT
VM_LINUX_ROOT := $(word 1, $(subst /vm_linux/,/vm_linux /, $(shell pwd)))
endif

ifndef CHILING_ROOT
CHILING_ROOT := $(word 1, $(subst /vm_linux/,/vm_linux /, $(shell pwd)))
endif
# TOOL_CHAIN=4.5.1_vfp_ca9
THIRDPARTY_ROOT     ?= $(PROJECT_ROOT)/../third_party
ifeq "$(ENABLE_VFP)" "true"
THIRDPARTY_LIB_ROOT ?= $(VM_LINUX_ROOT)/third_party/library/gnuarm-$(TOOL_CHAIN)_vfp
THIRD_PARTY_ROOT := $(VM_LINUX_ROOT)/third_party/library/gnuarm-$(TOOL_CHAIN)_vfp
else
THIRDPARTY_LIB_ROOT ?= $(VM_LINUX_ROOT)/third_party/library/gnuarm-$(TOOL_CHAIN)
THIRD_PARTY_ROOT := $(VM_LINUX_ROOT)/third_party/library/gnuarm-$(TOOL_CHAIN)
endif


# CARES
LIBCARES_LIB_PATH := $(THIRD_PARTY_ROOT)/c-ares/cares-1.7.4/lib
LIBCARES_INC_PATH := $(THIRD_PARTY_ROOT)/c-ares/cares-1.7.4/include

# expat
EXPAT_LIB_PATH   := $(THIRD_PARTY_ROOT)/expat/expat_2.0.1/lib
EXPAT_INC_PATH   := $(THIRD_PARTY_ROOT)/expat/expat_2.0.1/include


#OPENSSL
OPENSSL_LIB_PATH := $(THIRD_PARTY_ROOT)/openssl/openssl-1.0.0/lib
OPENSSL_INC_PATH := $(THIRD_PARTY_ROOT)/openssl/openssl-1.0.0/include

# libcurl
LIBCURL_LIB_PATH := $(THIRD_PARTY_ROOT)/curl/curl-7.20.0/lib
LIBCURL_INC_PATH := $(THIRD_PARTY_ROOT)/curl/curl-7.20.0/include

CMPB_INC = $(VM_LINUX_ROOT)/project_x/middleware/cmpb
MTKIF_LIB = $(THIRD_PARTY_ROOT)/mtk

WIFI_INC = $(VM_LINUX_ROOT)/project_x/middleware/inet/wifi

#Sercure Storage
SERCURE_STORAGE_PATH := $(THIRDPARTY_LIB_ROOT)/securestorage/lib

#freetype
FREETYPE_LIB_PATH := $(THIRD_PARTY_ROOT)/freetype/2.4.3/pre-install/lib
#png
PNG_LIB_PATH := $(THIRD_PARTY_ROOT)/png/1.2.43/pre-install/lib
##jpeg
JPEG_LIB_PATH := $(THIRD_PARTY_ROOT)/jpeg/6b/pre-install/lib
#zlib
ZLIB_LIB_PATH := $(THIRD_PARTY_ROOT)/zlib/1.2.3/pre-install/lib

#live555
LIVEMEDIA_PATH := $(THIRDPARTY_ROOT)/source/liveMedia
LIVEMEDIA_INCLUDES := -I$(LIVEMEDIA_PATH)/liveMedia/include -I$(LIVEMEDIA_PATH)/BasicUsageEnvironment/include -I$(LIVEMEDIA_PATH)/groupsock/include -I$(LIVEMEDIA_PATH)/UsageEnvironment/include

#ZLIB_LIB_DIR = $(THIRD_PARTY_ROOT)/zlib/$(ZLIB_VERSION)/pre-install/lib
NET_INFO_LIB_DIR = $(THIRD_PARTY_ROOT)/netutil/lib
CFLAGS = -march=armv6 -fsigned-char -fPIC -pipe -msoft-float -Wall -O2 -I./client/include -I./wifi_if/include -I./rtsp_if/include -I../include $(KFLAGS) -I$(MTAL_INC1) -I$(MTAL_INC2) -I$(CMPB_INC) -I$(WIFI_INC) -I$(LIBCURL_INC_PATH) -Iinclude -Isrc -L$(OPENSSL_LIB_PATH) -L$(MTKIF_LIB) -Wl,-Map,mapfile -I./client/src $(LIVEMEDIA_INCLUDES)
#LDFLAGS = -shared -Wl,-export-dynamic -L$(LIBCURL_LIB_PATH)
LDFLAGS =  -L$(LIBCURL_LIB_PATH)
CLIENT_SRC=./client/src
WIFI_SRC=./wifi_if/src
WFD_RTSP_SRC=./rtsp_if/src
RTSP_SRC=../src

#TARGET = libwfd.so
TARGET = libwfd.a
CSOURCES =
CXXSOURCES = $(CLIENT_SRC)/WFDClient.cpp	\
	$(CLIENT_SRC)/wfd_client_export.cpp	\
	$(WIFI_SRC)/WFDDeviceMentorImpl.cpp	\
	$(WFD_RTSP_SRC)/WfdRtspClient.cpp	\
	$(WFD_RTSP_SRC)/WfdRtspCmdSender.cpp    \
	$(WFD_RTSP_SRC)/WfdRtspPlayer.cpp	\
	$(WFD_RTSP_SRC)/WfdRtspPlayerImpl.cpp   \
	$(WFD_RTSP_SRC)/WfdRtspProtocol.cpp	\
	$(WFD_RTSP_SRC)/WfdRtspProxy.cpp	\
	$(RTSP_SRC)/RtspCmdSender.cpp		\
	$(RTSP_SRC)/Mutex.cpp			\
	$(RTSP_SRC)/PushPlayer.cpp		\
	$(RTSP_SRC)/RtspRingBuffer.cpp		\
	$(RTSP_SRC)/Semaphore.cpp		\
	$(RTSP_SRC)/ThreadObject.cpp		\
	$(RTSP_SRC)/myCfg.cpp			\
	$(RTSP_SRC)/myLog.cpp			\
	$(RTSP_SRC)/CmpbSink.cpp		\
	$(RTSP_SRC)/RtspRingBuffer.cpp		

CFLAGS += -DWFD_DEMO_SAVE_FILE   
#CFLAGS += -DWFD_RTSP_LOCAL_TEST
CXXFLAGS = $(CFLAGS)

OBJECTS = $(CSOURCES:.c=.o) $(CXXSOURCES:.cpp=.o)

.PHONY: clean all

all: $(TARGET)
#	cp *.so ..

LIBS_DIR=-L$(LIBCURL_LIB_PATH) -L$(LIBCARES_LIB_PATH) -L$(FREETYPE_LIB_PATH) -L$(EXPAT_LIB_PATH) -L$(ZLIB_LIB_PATH) -L$(PNG_LIB_PATH) -L$(JPEG_LIB_PATH) -L$(NET_INFO_LIB_DIR)
#LIBS=-lexpat -lcurl  -lssl -lcrypto -ldirectfb -lpthread -ldl -lcares -lsecurestorage -lcmpb -lpng -ldtv_common -lhandle_app -lapp_if -lrpc_ipc -lapp_if_rpc  -ljpeg  -lz -lfreetype -lnet_info 

# playlist:
# 	$(CXX)  src/Main.cpp $(CFLAGS)  $(LIBS_DIR) $(LIBS) -L. -lplaylist -o playlist

#-lssl -lcrypto  -lapp_if_rpc -ldtv_common -lhandle_app -lrpc_ipc -lapp_if -lcmpb  -lcurl -ldl  -lfreetype -lhandle_app  -lcares  -lexpat -o playlist  -lz -lnet_info -lsecurestorage  -lpng -ldirectfb -lpthread

$(TARGET): $(OBJECTS)
	$(AR) r $@ $(OBJECTS)

.c.o:
	$(CC) $(CFLAGS) $< -c -o $@

.cpp.o:
	$(CXX) $(CFLAGS) $< -c -o $@

clean:
	-rm -f $(OBJECTS) $(TARGET)

