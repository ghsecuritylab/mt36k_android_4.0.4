MTKMAK_PATH=../mak

include $(MTKMAK_PATH)/target.mak
include $(MTKMAK_PATH)/third_party_version.mak

#DEFINES += -DNDEBUG
DEFINES += -D_USE_LINUX

#ifeq ($(DEBUG), false)
#CCFLAG += -Wall -O2
#CC_FLAG += -Wall -O2
#else
#CCFLAG += -g -Wall -O0 -DDBGINFO -mapcs
#CC_FLAG += -g -Wall -O0 -DDBGINFO -mapcs
#endif

#DEFINES += -D_ANSI -DTARGET=10
#DEFINES += -DDBG=1

ifndef THIRDPARTY
export THIRDPARTY := ./../../../third_party
endif

THIRDPARTY_SRC_ROOT			?= $(THIRDPARTY)/source
THIRDPARTY_LIB_ROOT			?= $(THIRDPARTY)/library

THIRD_PARTY_LIB				= $(THIRDPARTY)/library/gnuarm-$(TOOL_CHAIN)$(VFP_SUFFIX)

VM_LINUX					= $(THIRDPARTY)/..
PROJECT_X					= $(VM_LINUX)/project_x
MIDDLEWARE					= $(PROJECT_X)/middleware
CMPB						= $(MIDDLEWARE)/cmpb


CURL_DIR					= $(THIRD_PARTY_LIB)/curl/curl-$(CURL_VERSION)
OPENSSL_DIR					= $(THIRD_PARTY_LIB)/openssl/openssl-$(OPENSSL_VERSION)
EXPAT_DIR					= $(THIRD_PARTY_LIB)/expat/expat_$(EXPAT_VERSION)
#ICONV_DIR					= $(THIRD_PARTY_LIB)/libiconv/libiconv-$(LIBICONV_VERSION)

CURL_INC_DIR				= $(CURL_DIR)/include
OPENSSL_INC_DIR				= $(OPENSSL_DIR)/include
EXPAT_INC_DIR				= $(EXPAT_DIR)/include
#ICONV_INC_DIR				= $(ICONV_DIR)/include

CURL_LIB_DIR				= $(CURL_DIR)/lib
OPENSSL_LIB_DIR				= $(OPENSSL_DIR)/lib
CARES_LIB_DIR				= $(THIRD_PARTY_LIB)/c-ares/cares-$(CARES_VERSION)/lib
PNG_LIB_DIR					= $(THIRD_PARTY_LIB)/png/$(PNG_VERSION)/pre-install/lib
JPEG_LIB_DIR				= $(THIRD_PARTY_LIB)/jpeg/$(JPEG_VERSION)/pre-install/lib
ZLIB_LIB_DIR				= $(THIRD_PARTY_LIB)/zlib/$(ZLIB_VERSION)/pre-install/lib
EXPAT_LIB_DIR				= $(EXPAT_DIR)/lib
FREETYPE_LIB_DIR			= $(THIRD_PARTY_LIB)/freetype/$(FREETYPE_VERSION)/pre-install/lib
NET_INFO_LIB_DIR			= $(THIRD_PARTY_LIB)/netutil/lib
SECURESTORAGE_LIB_DIR		= $(THIRD_PARTY_LIB)/securestorage/lib
MTK_LIB_DIR					= $(THIRD_PARTY_LIB)/mtk
#ICONV_LIB_DIR				= $(ICONV_DIR)/lib

CC_INC += -I$(CMPB) -I$(CURL_INC_DIR) -I$(OPENSSL_INC_DIR) -I$(EXPAT_INC_DIR) -I$(ICONV_INC_DIR)

export CC_INC
export DEFINES

export LIB_THIRD_PARTY_PATH     := -L$(PNG_LIB_DIR) -lpng \
                      -L$(JPEG_LIB_DIR) -ljpeg \
                      -L$(FREETYPE_LIB_DIR) -lfreetype \
                      -L$(ZLIB_LIB_DIR) -lz \
                      -L$(EXPAT_LIB_DIR) -lexpat \
                      -L$(CARES_LIB_DIR) -lcares \
                      -L$(NET_INFO_LIB_DIR) -lnet_info \
                      -L$(SECURESTORAGE_LIB_DIR) -lsecurestorage \
                      -L$(MTK_LIB_DIR) -lrpc_ipc -lhandle_app -ldtv_common -lapp_if -lapp_if_rpc -lconfig_parser -lcmpb -ldirectfb \
                      -L$(OPENSSL_LIB_DIR) -lcrypto -lssl \
                      -L$(CURL_LIB_DIR) -lcurl 