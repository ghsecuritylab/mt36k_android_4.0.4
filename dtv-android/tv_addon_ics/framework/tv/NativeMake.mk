export LD_LIBRARY_PATH := ./bin
export LD_LIBRARY_PATH

LOCAL_PATH := `pwd`
MW_ROOT := ${LOCAL_PATH}/../../../../../

LOCAL_SRC_FILES:= \
	jni/com_mediatek_tv_service_channel_service.c \
	jni/tv_jni_util.c \
	jni/onload.c


#	jni/com_mediatek_tv_service_config_service.c 
#	jni/com_mediatek_tv_service_broadcast_service.c 
#	jni/com_mediatek_tv_service_scan_service.c 
all:
	clear;
	@echo LOCAL_PATH=${LOCAL_PATH}
	@echo MW_ROOT=${MW_ROOT}

	echo --------------Compile java code--------------
	ant -f build.xml clean
	ant -f build.xml 

	echo --------------Compile libcom_mediatek_tv_jni.so--------------
	rm -rf ./bin/libcom_mediatek_tv_jni.so;
	gcc -o ./bin/libcom_mediatek_tv_jni.so -fPIC -m32 -shared -Wl,-soname,libnative.so  -static -lc \
	    -DNATIVE_DEBUG	-D_CPU_LITTLE_ENDIAN_ \
	    -I/usr/local/jdk1.6.0_02/include \
	    -I/usr/local/jdk1.6.0_02/include/linux \
	    -I../../../../../vm_linux/project_x/middleware/ \
		-I../../../../../vm_linux/dtv_linux/project_x_linux/dtv_svc_client/ \
	    ${LOCAL_SRC_FILES}
	
ChannelServiceTest: all
	@echo "Run ChannelService"
	ant -f build.xml ChannelServiceTest
	
ConfigServiceTest: all
	@echo "Run ConfigService"
	ant -f build.xml ConfigServiceTest
