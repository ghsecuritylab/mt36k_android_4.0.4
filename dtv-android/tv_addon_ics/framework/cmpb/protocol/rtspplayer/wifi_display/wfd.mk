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
export TOOL_CHAIN=4.5.1
export ENABLE_CA9=true
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
else ifeq "$(ENABLE_CA9)" "true" 
THIRDPARTY_LIB_ROOT ?= $(VM_LINUX_ROOT)/third_party/library/gnuarm-$(TOOL_CHAIN)_vfp_ca9
THIRD_PARTY_ROOT := $(VM_LINUX_ROOT)/third_party/library/gnuarm-$(TOOL_CHAIN)_vfp_ca9
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
LIBCURL_LIB_PATH := $(THIRD_PARTY_ROOT)/curl/curl-7.21.4/lib
LIBCURL_INC_PATH := $(THIRD_PARTY_ROOT)/curl/curl-7.21.4/include

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
#TARGET = libwfd.a
TARGET = wfd.bin
CSOURCES =
#CXXSOURCES = $(CLIENT_SRC)/WFDClient.cpp
#CXXSOURCES += $(CLIENT_SRC)/wfd_client_export.cpp
#CXXSOURCES += 	$(WIFI_SRC)/WFDDeviceMentorImpl.cpp
CXXSOURCES = 	$(WFD_RTSP_SRC)/WfdRtspClient.cpp	
CXXSOURCES += 	$(WFD_RTSP_SRC)/WfdRtspCmdSender.cpp    
CXXSOURCES += 	$(WFD_RTSP_SRC)/WfdRtspPlayer.cpp	
CXXSOURCES += 	$(WFD_RTSP_SRC)/WfdRtspPlayerImpl.cpp   
CXXSOURCES += 	$(WFD_RTSP_SRC)/WfdRtspProtocol.cpp	
CXXSOURCES += 	$(WFD_RTSP_SRC)/WfdRtspProxy.cpp	
CXXSOURCES += 	$(RTSP_SRC)/RtspCmdSender.cpp		
CXXSOURCES += 	$(RTSP_SRC)/Mutex.cpp			
CXXSOURCES += 	$(RTSP_SRC)/PushPlayer.cpp			
CXXSOURCES += 	$(RTSP_SRC)/Semaphore.cpp		
CXXSOURCES += 	$(RTSP_SRC)/ThreadObject.cpp		
CXXSOURCES += 	$(RTSP_SRC)/myCfg.cpp			
CXXSOURCES += 	$(RTSP_SRC)/myLog.cpp			
CXXSOURCES += 	$(RTSP_SRC)/CmpbSink.cpp		
CXXSOURCES += 	$(RTSP_SRC)/RtspRingBuffer.cpp	
CXXSOURCES += 	$(WFD_RTSP_SRC)/main.cpp	

CXXFLAGS = $(CFLAGS)

OBJECTS = $(CSOURCES:.c=.o) $(CXXSOURCES:.cpp=.o)

.PHONY: clean all

all: $(TARGET)
#	cp *.so ..

LIBS_DIR=-L$(LIBCURL_LIB_PATH) -L$(LIBCARES_LIB_PATH) -L$(FREETYPE_LIB_PATH) -L$(EXPAT_LIB_PATH) -L$(ZLIB_LIB_PATH) -L$(PNG_LIB_PATH) -L$(JPEG_LIB_PATH) -L$(NET_INFO_LIB_DIR)
LIBS_DIR +=-L$(MTKIF_LIB)
LIBS= $(LIBS_DIR) -lexpat -lcurl  -lssl -lcrypto -ldirectfb -lpthread -ldl -lcares -lsecurestorage -lcmpb -lpng -ldtv_common -lhandle_app -lapp_if -lrpc_ipc -lapp_if_rpc  -ljpeg  -lz -lfreetype -lnet_info 

LIBS +=-L../../RtspPlayer/lib/ca9/ -lBasicUsageEnvironment -lgroupsock -lliveMedia -lUsageEnvironment -lpthread

CFLAGS += -DWFD_DEMO_SAVE_FILE
	
.phony: all clean

all : $(TARGET)

$(TARGET) : $(SRC_FILES)
	@echo "################ Building wfd Test ################"
	$(CCC) ${CFLAGS}  $(CXXSOURCES) $(LIBS) -g -o ./$@ 
        
clean: 
	rm -fr ./$(TARGET)


