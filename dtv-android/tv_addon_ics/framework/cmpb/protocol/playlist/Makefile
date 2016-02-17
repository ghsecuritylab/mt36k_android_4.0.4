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

include ../mak/target.mak

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
LIBCARES_INC_PATH := $(THIRD_PARTY_ROOT)/c-ares/cares-1.7.1/include

# expat
EXPAT_LIB_PATH   := $(THIRDPARTY_LIB_ROOT)/expat/expat_2.0.1/lib
EXPAT_INC_PATH   := $(THIRDPARTY_LIB_ROOT)/expat/expat_2.0.1/include


#OPENSSL
OPENSSL_LIB_PATH := $(THIRD_PARTY_ROOT)/openssl/openssl-1.0.0/lib
OPENSSL_INC_PATH := $(THIRD_PARTY_ROOT)/openssl/openssl-1.0.0/include

# libcurl
LIBCURL_LIB_PATH := $(THIRD_PARTY_ROOT)/curl/curl-7.21.4/lib
LIBCURL_INC_PATH := $(THIRD_PARTY_ROOT)/curl/curl-7.21.4/include

CMPB_INC = $(VM_LINUX_ROOT)/project_x/middleware/cmpb
MTKIF_LIB = $(THIRD_PARTY_ROOT)/mtk


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


#ZLIB_LIB_DIR = $(THIRD_PARTY_ROOT)/zlib/$(ZLIB_VERSION)/pre-install/lib
NET_INFO_LIB_DIR = $(THIRD_PARTY_ROOT)/netutil/lib
CFLAGS = -march=armv6 -fsigned-char -fPIC -pipe -msoft-float -Wall -O2 -I. $(KFLAGS) -I$(MTAL_INC1) -I$(MTAL_INC2) -I$(CMPB_INC) -I$(OPENSSL_INC_PATH) -I$(LIBCURL_INC_PATH) -Iinclude -Isrc -L$(OPENSSL_LIB_PATH) -L$(MTKIF_LIB) -Wl,-Map,mapfile
#CFLAGS = -march=armv6 -fsigned-char -fPIC -pipe -msoft-float -Wall -O0 -g -I. $(KFLAGS) -I$(MTAL_INC1) -I$(MTAL_INC2) -I$(CMPB_INC) -I$(OPENSSL_INC_PATH) -I$(LIBCURL_INC_PATH) -Iinclude -Isrc -L$(OPENSSL_LIB_PATH) -L$(MTKIF_LIB) -Wl,-Map,mapfile 
LDFLAGS = -shared -Wl,-export-dynamic -L$(LIBCURL_LIB_PATH)

TARGET = libplaylist.so
CSOURCES =
CXXSOURCES = src/http_live_streaming.cpp \
	src/Semaphore.cpp \
	src/ThreadObject.cpp \
	src/Playlist.cpp \
	src/PlaylistPlayer.cpp \
	src/PlaylistPlayback.cpp \
	src/StreamSelector.cpp

CXXFLAGS = $(CFLAGS)

OBJECTS = $(CSOURCES:.c=.o) $(CXXSOURCES:.cpp=.o)

.PHONY: clean all main

all: $(TARGET)
#	cp *.so ..

main: playlist
playlist : src/Main.cpp libplaylist.so
LIBS_DIR=-L$(LIBCURL_LIB_PATH) -L$(LIBCARES_LIB_PATH) -L$(FREETYPE_LIB_PATH) -L$(EXPAT_LIB_PATH) -L$(ZLIB_LIB_PATH) -L$(PNG_LIB_PATH) -L$(JPEG_LIB_PATH) -L$(NET_INFO_LIB_DIR)
LIBS=-lexpat -lcurl  -lssl -lcrypto -ldirectfb -lpthread -ldl -lcares -lsecurestorage -lcmpb -lpng -ldtv_common -lhandle_app -lapp_if -lrpc_ipc -lapp_if_rpc  -ljpeg  -lz -lfreetype -lnet_info 

playlist:
	$(CXX)  src/Main.cpp $(CFLAGS)  $(LIBS_DIR) $(LIBS) -L. -lplaylist -o playlist

#-lssl -lcrypto  -lapp_if_rpc -ldtv_common -lhandle_app -lrpc_ipc -lapp_if -lcmpb  -lcurl -ldl  -lfreetype -lhandle_app  -lcares  -lexpat -o playlist  -lz -lnet_info -lsecurestorage  -lpng -ldirectfb -lpthread

$(TARGET): $(OBJECTS)
	$(CXX) $(OBJECTS) $(LDFLAGS) -o $@

.c.o:
	$(CC) $(CFLAGS) $< -c -o $@

.cpp.o:
	$(CXX) $(CFLAGS) $< -c -o $@

install:
	@echo "Installing playlist ..."	
	mkdir -p $(THIRDPARTY_LIB_ROOT)/playlist/include
	cp -f $(VM_LINUX_ROOT)/third_party/source/playlist/include/PlaylistPlayback.h  $(THIRDPARTY_LIB_ROOT)/playlist/include
	cp -f $(VM_LINUX_ROOT)/third_party/source/playlist/include/PlaylistPlayer.h $(THIRDPARTY_LIB_ROOT)/playlist/include	 
	cp -f $(VM_LINUX_ROOT)/third_party/source/playlist/*.so $(THIRDPARTY_LIB_ROOT)/playlist/

clean:
	-rm -f $(OBJECTS) $(TARGET)
	-rm -f playlist
