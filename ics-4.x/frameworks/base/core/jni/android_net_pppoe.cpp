/*
 * Copyright 2009, The Android-x86 Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Yi Sun <beyounn@gmail.com>
 */

#define LOG_TAG "ADSL"

#include "jni.h"
#include <inttypes.h>
#include <utils/misc.h>
#include <android_runtime/AndroidRuntime.h>
#include <utils/Log.h>
#include <asm/types.h>
#include <linux/netlink.h>
#include <linux/rtnetlink.h>
#include <poll.h>
#include <net/if_arp.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include <dirent.h>

extern "C" {
#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <fcntl.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <sys/select.h>
#include <sys/wait.h>
#include <cutils/properties.h>
}

#define POE_PKG_NAME "android/net/pppoe/PppoeNative"
#define ADDRESS_PATH "/data/data/addr"
#define IFACE_PATH "/data/data/iface"

namespace android {
	
	#define PORT 9080
	
    #define LINK_UP		30
	#define LINK_DOWN   31
	#define LINK_AU_FAIL   32
	

	int sockfd, len;
    struct sockaddr_in addr;
    int addr_len=sizeof(addr);
	char iface[64];
    char buffer[256];
	char address[256];
	#define CMD_STR_LEN     256

	//================================
	extern "C"{
		int sockerver();
		int Pgetaddress();
		int adsldialer(char user[],char passwd[]);
		int adsldisconnect_exec();
	

	#define do_cmd(format, cmds...) ({ \
    int __STATAS__; \
    char __TEMP__[CMD_STR_LEN]; \
    sprintf(__TEMP__, format, ##cmds); \
    __STATAS__ = system(__TEMP__); \
    __STATAS__; \
})

	}
	static const char *ipaddr_to_string(uint32_t addr)
{
    struct in_addr in_addr;

    in_addr.s_addr = addr;
    return inet_ntoa(in_addr);
}
	int sockerver()
	{
		if((sockfd = socket(AF_INET, SOCK_DGRAM, 0))<0)
		{
			LOGI("### socket error ###");
          return 0;
		}
		bzero(&addr, sizeof(addr));
    
		addr.sin_family=AF_INET;
		addr.sin_port=htons(PORT);
		addr.sin_addr.s_addr=htonl(INADDR_ANY);
    
		if(bind(sockfd, (struct sockaddr *)&addr, sizeof(addr)) < 0)
		{
			LOGI("### bind error ###");
           return 0;
		}
		return 1;
	}
	int Pgetaddress()
	{
		FILE *addfd;
		addfd = fopen(ADDRESS_PATH,"r");
		if(addfd != NULL)
		{
			if(fgets(address,sizeof(address),addfd) == NULL)
			{
				LOGI("### get address:fgets error ###");
				return 0;
			}
		}
		return 1;

	}
	int adsldialer(char user[],char passwd[])
	{
		char cmdline[128];
		if(strcmp(user,"")&&strcmp(passwd,""))
		{
			LOGI("###username or passwd is null###");
			return 0;
		}
		memset(cmdline,'0',sizeof(cmdline));
		sprintf(cmdline,"pppoecd -i eth0 -u %s -p %s",user,passwd);
		system(cmdline);
		return 1;
	}
	int adsldisconnect_exec(int force)
	{
		int stat=-1,stat2=-1;
		char cmd[16];
		char cmddisconnect[128]={"\0"};
		char *psepr=NULL;
		FILE *pidfd;
		pidfd = fopen(IFACE_PATH,"r");
		LOGI("======adsldisconnect_exec========");
		if(pidfd != NULL)
		{
			if(force == 0){
				LOGI("======adsldisconnect_exec:not force dis========");
				system("adsldisconnect");
			}
			else{
				LOGI("======adsldisconnect_exec:force dis========");
				if((psepr=fgets(iface,sizeof(iface),pidfd)) == NULL)
				{
					LOGI("### get iface:fgets error ###");
					return 0;
				}
				if((psepr=strchr(iface,':')) != 0)
				{
					stat2 = system("ps > /data/112");
					LOGI("==============stat2=%d",stat2);
					strcpy(cmd,psepr+1);
					sprintf(cmddisconnect,"kill -9 %d",atoi(cmd));
					LOGI("==============sizeof(cmddisconnect)=%d",sizeof(cmddisconnect));
					stat = system(cmddisconnect);
					LOGI("======cmddisconnect=%s=======stat=%d",cmddisconnect,stat);
					
		
				}
			}	
		}
		else{
			LOGI("### pidfd error ###");
			return 0;
		}
		return 1;

	}

	static jint android_net_pppoe_initpppoeNative(JNIEnv *env,
                                                        jobject clazz)
    {
		if(sockerver()== 1)
		{
			LOGI("@@@@@@@@android_net_pppoe_initpppoeNative ok@@@@@@@");
			return 1;
		}
		else
		{
			LOGI("####android_net_pppoe_initpppoeNative error####");
			return 0;
		}
		
		
		
		
    }

    static jstring android_net_pppoe_waitForEvent(JNIEnv *env,
                                                     jobject clazz)
    {
		
			fd_set readfds;
			FD_ZERO(&readfds);
			FD_SET(sockfd, &readfds);
			
			LOGI("@@@@@@@@ select @@@@@@@");
			
			if(select(sockfd+1,&readfds,NULL,NULL,NULL) > 0)
			{
				bzero(buffer, sizeof(buffer));
				len=recvfrom(sockfd, buffer, sizeof(buffer), 0 , (struct sockaddr *)&addr, &addr_len);
             
				LOGI("@@@@@@@@ receive data :%s @@@@@@@" , buffer);
				//sendto(sockfd, buffer, len, 0, (struct sockaddr *)&addr, addr_len);
				
			}		
			return env->NewStringUTF(buffer);
        
	}
	static jint android_net_pppoe_adsldial(JNIEnv *env,jobject clazz,jstring username,jstring passwd)
	{
		/*if(adsldialer(username,passwd) == 1)
		{
			LOGI("adsldialer ok");
			return 1;
		}
		else
			LOGI("### adsldialer error ###");
		return 0;*/
		char cmdline[128];
		/*if(strcmp(username,"")&&strcmp(username,""))
		{
			LOGI("###username or passwd is null###");
			return 0;
		}*/
		memset(cmdline,'0',sizeof(cmdline));
		sprintf(cmdline,"pppoecd -i eth0 -u %s -p %s",username,username);
		system(cmdline);
		return 1;
	}
	static jint android_net_pppoe_adsldisconnect(JNIEnv *env,jobject clazz,jint force)
	{
		LOGI("======android_net_pppoe_adsldisconnect========");
		if(adsldisconnect_exec(force) == 1)
		{
			LOGI("disconnect ok");
			return 1;
		}
		else
			LOGI("### disconnect error ###");
		LOGI("======android_net_pppoe_adsldisconnect over========");
		return 0;
	}

    static jstring android_net_pppoe_getaddress(JNIEnv *env,
                                                     jobject clazz)
    {
		
			LOGI("@@@@@@@@ select @@@@@@@");
			
			if(Pgetaddress() > 0)
			{
				LOGI("@@@@@@@@ get address :%s @@@@@@@" , address);
				return env->NewStringUTF(address);
			}
			else
				return env->NewStringUTF(NULL);
        
	}
	static jint android_net_pppoe_configureInterface(JNIEnv* env,
        jobject clazz,
        jstring ipaddr,
        jstring gateway,
        jstring dns1,
        jstring dns2)
        {
    uint32_t lease;
	char dns_prop_name[PROPERTY_KEY_MAX];
	const char* IpAddr = env->GetStringUTFChars(ipaddr,NULL);  
	const char* GateWay = env->GetStringUTFChars(gateway,NULL);
	const char* DNS1 = env->GetStringUTFChars(dns1,NULL);
	const char* DNS2 = env->GetStringUTFChars(dns2,NULL);
	snprintf(dns_prop_name, sizeof(dns_prop_name), "net.ppp.ipaddr");
	property_set(dns_prop_name, IpAddr ? IpAddr : "");
	snprintf(dns_prop_name, sizeof(dns_prop_name), "net.ppp.gateway");
	property_set(dns_prop_name,GateWay ? GateWay: "");
	snprintf(dns_prop_name, sizeof(dns_prop_name), "net.ppp.dns1");
	property_set(dns_prop_name, DNS1 ? DNS1 : "");
	snprintf(dns_prop_name, sizeof(dns_prop_name), "net.ppp.dns2");
	property_set(dns_prop_name,DNS2 ? DNS2 : "");
    	return 1;
	
}
  

    static JNINativeMethod gpppoeMethods[] = {
		{"initpppoeNative", "()I",
         (void *)android_net_pppoe_initpppoeNative},
		{"adsldial", "(Ljava/lang/String;Ljava/lang/String;)I",
         (void *)android_net_pppoe_adsldial},
		{"adsldisconnect", "(I)I",
         (void *)android_net_pppoe_adsldisconnect},
        {"waitEvent", "()Ljava/lang/String;",
         (void *)android_net_pppoe_waitForEvent},
		{"getaddress", "()Ljava/lang/String;",
         (void *)android_net_pppoe_getaddress},
         {"pppoeconfigure", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I",
         (void *)android_net_pppoe_configureInterface}
      };

    int register_android_net_pppoe_PppoeManager(JNIEnv* env)
    {
        jclass ppoe = env->FindClass(POE_PKG_NAME);
        LOGI("Loading pppoe jni class");


       
        return AndroidRuntime::registerNativeMethods(env,
                                                     POE_PKG_NAME,
                                                     gpppoeMethods,
                                                     NELEM(gpppoeMethods));
    }
};
