/*
 * Copyright (C) 2011 The TCL Project
 *
 * 
 * function : IP&ROUTE settings when IP UP
 *  author  : HQS
 *  version : v1.0
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <linux/route.h>

#include <android/log.h>
#include <cutils/properties.h>

#include "pppoecd/pppd.h"

int main(int argc, char **argv)
{
    char cmd[128] = {0};
	char * resvalue = NULL;
    char * ip_local = NULL;
    char * ip_remote = NULL;
    char * ip_dns1 = NULL;
    char * ip_dns2 = NULL;
    
    ip_local = getenv("IPLOCAL");
    ip_remote = getenv("IPREMOTE");
    ip_dns1 = getenv("DNS1");
    ip_dns2 = getenv("DNS2");
	
	printf("ip_local=%s\n",ip_local);
	
    do_cmd("echo 1 > /proc/sys/net/ipv4/ip_forward");
    if((NULL != ip_local)&&(NULL != ip_remote)){
        do_cmd("iptables -t nat -A POSTROUTING -o ppp0 -j MASQUERADE");
        do_cmd("iptables -t nat -A PREROUTING -i ppp0 -d %s -j DNAT --to %s",ip_remote,ip_local);
        do_cmd("iptables -t nat -A POSTROUTING -o ppp0 -s %s -j SNAT --to %s",ip_local,ip_remote);
        do_cmd("route add default gw %s dev ppp0",ip_remote);
    }
    if((NULL != ip_dns1)){
        do_cmd("setprop net.dns1 %s",ip_dns1);
    }
    if((NULL != ip_dns2)){
        do_cmd("setprop net.dns1 %s",ip_dns1);
    }
	return 1;
}

