TOP = .

ifdef RTSP_LOCAL_TEST
CCC=g++ 
LIBS = -L/usr/lib -lpthread
INCLUDES=-I/usr/include
else

include ./def.mk

#CCC=/mtkoss/gnuarm/soft_4.5.1_2.6.27_arm11-rhel4/i686/bin/armv6z-mediatek451_001_soft-linux-gnueabi-g++
#LIBS = -L/mtkoss/gnuarm/soft_4.5.1_2.6.27_arm11-rhel4/i686/sysroot/lib -lpthread
#INCLUDES=-I/mtkoss/gnuarm/soft_4.5.1_2.6.27_arm11-rhel4/i686/sysroot/usr/include

INCLUDES = -I$(CMPB)
LIBS=$(LIB_THIRD_PARTY_PATH)
endif

TARGET=test.bin
LIBWFDA = libwfd.so

SRC_FILES = ./src/main.cpp
SRC_FILES += ./src/RtspCmdSender.cpp
SRC_FILES += ./src/myLog.cpp
SRC_FILES += ./src/MtkRtspClient.cpp
SRC_FILES += ./src/ThreadObject.cpp
SRC_FILES += ./src/Semaphore.cpp
SRC_FILES += ./src/Mutex.cpp
SRC_FILES += ./src/CmpbSink.cpp
SRC_FILES += ./src/CmpbH264Sink.cpp
SRC_FILES += ./src/CmpbAACSink.cpp
SRC_FILES += ./src/PushPlayer.cpp
SRC_FILES += ./src/RtspPlayer.cpp
SRC_FILES += ./src/myCfg.cpp

INCLUDES += -I./include 
INCLUDES += -I./../liveMedia/liveMedia/include 
INCLUDES += -I./../liveMedia/BasicUsageEnvironment/include
INCLUDES += -I./../liveMedia/groupsock/include 
INCLUDES += -I./../liveMedia/UsageEnvironment/include

ifdef RTSP_LOCAL_TEST
INCLUDES += -I./localtestsrc 
SRC_FILES += ./localtestsrc/app_init.cpp
SRC_FILES += ./localtestsrc/IMtkPb_Ctrl.cpp
LIBS += -Wl,-rpath,./locallib -L./locallib -lBasicUsageEnvironment -lgroupsock -lUsageEnvironment -lliveMedia 
DEFINES+=-DRTSP_LOCAL_TEST=true
else
SRC_FILES += ./src/app_init.cpp
LIBS +=  ./libBasicUsageEnvironment.so ./libgroupsock.so ./libliveMedia.so ./libUsageEnvironment.so -lpthread -L./../../../third_party/library/gnuarm-4.5.1/expat/expat_2.0.1/lib -lexpat
endif

DEFINES+=-DENABLE_MTK_RTSP -Wall -O2
DEFINES+=-DRTSP_SAVE_FRAME

WIFI_DISPLAY_DIR = $(TOP)/wifi_display

.phony: all clean

#all : $(LIBWFDA) $(TARGET)
all : $(TARGET)
$(TARGET) : $(SRC_FILES)
	@echo "################ Building ice_socket Test ################"
	$(CCC) ${DEFINES} $(INCLUDES) $(SRC_FILES) $(LIBS) -g -o ./$@ 
        
$(LIBWFDA): 
	@echo "################ Building libwfd.so ################"
	rm -rf $(WIFI_DISPLAY_DIR)/$@
	make -C $(WIFI_DISPLAY_DIR)

install:
	@echo "################ Install ################"
	rm -rf $(THIRD_PARTY_LIB)/RtspPlayer
	mkdir -p $(THIRD_PARTY_LIB)/RtspPlayer

clean: 
	rm -rf ./$(TARGET)
	rm -rf $(WIFI_DISPLAY_DIR)/$(LIBWFDA)
	rm -f `find . -name "*.o"`
