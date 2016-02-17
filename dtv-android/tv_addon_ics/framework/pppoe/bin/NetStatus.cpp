#include <sys/socket.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <net/if.h>
#include <netinet/in.h>
#include <unistd.h>

#include <android/log.h>
#include "os/Thread.h"
#include "NetStatus.h"

static const int NET_IF_NUMBER = 1024;
static const int NET_INADDR_LOOPBACK = ((unsigned int) 0x7f000001);
static const int NET_INADDR_ANY   =((unsigned int) 0x00000000);
static const int NET_INADDR_BROADCAST = ((unsigned int) 0xffffffff);
static const std::string TAG = "pppoeservice:NetStatus";
static const std::string DEVICE_NAME_PREFIX = "ppp";
static const int DEVICE_NAME_PREFIX_LENGTH = 3;

std::list <net::DeviceInfo*> net::NetStatus::getActiveDevices()
{
    int sock;
    int ret;
    struct ifreq ifr;
    int i;
    int addr;

    struct ifconf conf;
    int length;

    std::list<net::DeviceInfo*> list;

    memset(&ifr, 0, sizeof(struct ifreq));
    memset(&conf, 0, sizeof(struct ifconf));

    sock = socket(AF_INET, SOCK_STREAM, IPPROTO_IP);
    if (sock < 0)
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "open socket fail : %d \n", errno);
        return list;
    }

    length = NET_IF_NUMBER * sizeof(struct ifreq);

    conf.ifc_ifcu.ifcu_buf = (char*)malloc(length);
    if(conf.ifc_ifcu.ifcu_buf == NULL)
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "malloc fail\n");
        close(sock);
        return list;
    }
    memset(conf.ifc_ifcu.ifcu_buf, 0, length);
    conf.ifc_len = length;

    ret = ioctl(sock, SIOCGIFCONF, &conf);
    if (ret < 0)
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "get conf fail : %d \n", errno);
        perror("ioctl");
        free(conf.ifc_ifcu.ifcu_buf);
        close(sock);
        return list;
    }

    for (i = 0; i < NET_IF_NUMBER; i++)
    {
        if ((!conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name) || strlen(conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name) == 0)
        {
            continue;
        }

        addr = ntohl(((struct sockaddr_in*)(&(conf.ifc_ifcu.ifcu_req[i].ifr_ifru.ifru_addr)))->sin_addr.s_addr);
        if((addr == NET_INADDR_BROADCAST) ||
            (addr == NET_INADDR_ANY)||
            (addr == NET_INADDR_LOOPBACK))
        {
            continue;
        }

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "device : %s \n", conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name);

        memcpy(ifr.ifr_ifrn.ifrn_name, conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name, strlen(conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name)+1);
        int flags;
        ret = ioctl (sock, SIOCGIFFLAGS, &ifr);
        if (ret < 0 || ((!(ifr.ifr_flags & IFF_RUNNING)) || (!(ifr.ifr_flags & IFF_UP))))
        {
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "is not running or up");
            continue;
        }
        flags = ifr.ifr_flags;

        memcpy(ifr.ifr_ifrn.ifrn_name, conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name, strlen(conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name)+1);

        ret = ioctl(sock, SIOCGIFHWADDR, &ifr);
        if (ret < 0)
        {
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "get mac fail : %d \n", errno);
            perror("ioctl");
            free(conf.ifc_ifcu.ifcu_buf);
            close(sock);
            return list;
        }

        net::DeviceInfo * info = new net::DeviceInfo;
        info->name = conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name;
        info->addr = ((struct sockaddr_in*)(&(conf.ifc_ifcu.ifcu_req[i].ifr_ifru.ifru_addr)))->sin_addr.s_addr;
        if(flags & IFF_POINTOPOINT) {
            info->type = POINTTOPOINT;
        } else {
            info->type = ETHERNET;
        }

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "\n");
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "addr : %08x \n", ((struct sockaddr_in*)(&(conf.ifc_ifcu.ifcu_req[i].ifr_ifru.ifru_addr)))->sin_addr.s_addr);

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "mac addr : ");
        for (i = 0; i < 6 ; i++)
        {
            info->mac[i] = ifr.ifr_ifru.ifru_hwaddr.sa_data[i] & 0xff;
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "%02x ", ifr.ifr_ifru.ifru_hwaddr.sa_data[i] & 0xff);
        }
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "\n");

        list.push_back(info);
    }

    free(conf.ifc_ifcu.ifcu_buf);
    close(sock);
    return list;
}


std::list <std::string> net::NetStatus::getDevices()
{
    int sock;
    int ret;
    struct ifreq ifr;
    int i;
    int addr;

    struct ifconf conf;
    int length;

    std::list<std::string> list;

    memset(&ifr, 0, sizeof(struct ifreq));
    memset(&conf, 0, sizeof(struct ifconf));

    sock = socket(AF_INET, SOCK_STREAM, IPPROTO_IP);
    if (sock < 0)
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "open socket fail : %d \n", errno);
        return list;
    }

    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "Get Devices \n");

    length = NET_IF_NUMBER * sizeof(struct ifreq);

    conf.ifc_ifcu.ifcu_buf = (char*)malloc(length);
    if(conf.ifc_ifcu.ifcu_buf == NULL)
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "malloc fail\n");
        close(sock);
        return list;
    }
    memset(conf.ifc_ifcu.ifcu_buf, 0, length);
    conf.ifc_len = length;

    ret = ioctl(sock, SIOCGIFCONF, &conf);
    if (ret < 0)
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "get conf fail : %d \n", errno);
        perror("ioctl");
        free(conf.ifc_ifcu.ifcu_buf);
        close(sock);
        return list;
    }

    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "Get Devices and start to cycle\n");

    for (i = 0; i < NET_IF_NUMBER; i++)
    {
        if ((!conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name) || (strlen(conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name) == 0))
        {
            continue;
        }

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "Get Devices step 1 continue \n");


        addr = ntohl(((struct sockaddr_in*)(&(conf.ifc_ifcu.ifcu_req[i].ifr_ifru.ifru_addr)))->sin_addr.s_addr);
        if((addr == NET_INADDR_BROADCAST) ||
            (addr == NET_INADDR_ANY)||
            (addr == NET_INADDR_LOOPBACK))
        {
            continue;
        }

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "Get Devices step 2 continue \n");


        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "device : %s \n", conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name);

        memcpy(ifr.ifr_ifrn.ifrn_name, conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name, strlen(conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name)+1);
      
        ret = ioctl (sock, SIOCGIFFLAGS, &ifr);
        if (ret < 0 || ((!(ifr.ifr_flags & IFF_RUNNING))&& (!(ifr.ifr_flags & IFF_UP))))
        {
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "is not running or up");
            continue;
        }

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "Get Devices step 3 continue \n");


        std::string info(conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name);

        list.push_back(info);
    }

    free(conf.ifc_ifcu.ifcu_buf);
    close(sock);
    return list;
}



static bool checkDeviceReady(std::string deviceName) {
    int sock;
    int ret;
    struct ifreq ifr;   
    int addr;

    memset(&ifr, 0, sizeof(struct ifreq));

    sock = socket(AF_INET, SOCK_STREAM, IPPROTO_IP);
    if (sock < 0)
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "open socket fail : %d \n", errno);
        return false;
    }

    memcpy(ifr.ifr_ifrn.ifrn_name, deviceName.c_str(), strlen(deviceName.c_str())+1);

    ret = ioctl (sock, SIOCGIFFLAGS, &ifr);
    if (ret < 0 || ((!(ifr.ifr_flags & IFF_RUNNING))&& (!(ifr.ifr_flags & IFF_UP)) && (!(ifr.ifr_flags & IFF_POINTOPOINT))))
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "is not running up or ppp");
        close(sock);
        return false;
    }

    memcpy(ifr.ifr_ifrn.ifrn_name, deviceName.c_str(), strlen(deviceName.c_str())+1);

    ret = ioctl(sock, SIOCGIFADDR, &ifr);
    if (ret < 0)
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "get addr fail : %d \n", errno);

        close(sock);
        return false;
    }

    addr = ntohl(((struct sockaddr_in*)(&(ifr.ifr_ifru.ifru_addr)))->sin_addr.s_addr);
    if((addr == NET_INADDR_BROADCAST) ||
        (addr == NET_INADDR_ANY)||
        (addr == NET_INADDR_LOOPBACK))
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "addr is broadcast any or loopback");
        close(sock);
        return false;
    }

    close(sock);

    return true;
} 

bool net::NetStatus::isPPPReady() {
    int sock;
    int ret;
    struct ifreq ifr;
    int i;
    int addr;

    struct ifconf conf;
    int length;

    bool value = false;

    memset(&ifr, 0, sizeof(struct ifreq));
    memset(&conf, 0, sizeof(struct ifconf));

    sock = socket(AF_INET, SOCK_STREAM, IPPROTO_IP);
    if (sock < 0)
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "open socket fail : %d \n", errno);
        return value;
    }

    length = NET_IF_NUMBER * sizeof(struct ifreq);

    conf.ifc_ifcu.ifcu_buf = (char*)malloc(length);
    if(conf.ifc_ifcu.ifcu_buf == NULL)
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "malloc fail\n");
        close(sock);
        return value;
    }
    memset(conf.ifc_ifcu.ifcu_buf, 0, length);
    conf.ifc_len = length;

    ret = ioctl(sock, SIOCGIFCONF, &conf);
    if (ret < 0)
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "get conf fail : %d \n", errno);
        perror("ioctl");
        free(conf.ifc_ifcu.ifcu_buf);
        close(sock);
        return value;
    }

    for (i = 0; i < NET_IF_NUMBER; i++)
    {
        if ((!conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name) && strlen(conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name) == 0)
        {
            continue;
        }

        addr = ntohl(((struct sockaddr_in*)(&(conf.ifc_ifcu.ifcu_req[i].ifr_ifru.ifru_addr)))->sin_addr.s_addr);
        if((addr == NET_INADDR_BROADCAST) ||
            (addr == NET_INADDR_ANY)||
            (addr == NET_INADDR_LOOPBACK))
        {
            continue;
        }

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "device : %s \n", conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name);

        if ((strlen(conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name) > DEVICE_NAME_PREFIX_LENGTH) &&
            (memcmp(conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name, DEVICE_NAME_PREFIX.c_str(), DEVICE_NAME_PREFIX_LENGTH) == 0))
        {
            memcpy(ifr.ifr_ifrn.ifrn_name, conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name, strlen(conf.ifc_ifcu.ifcu_req[i].ifr_ifrn.ifrn_name)+1);
            ret = ioctl (sock, SIOCGIFFLAGS, &ifr);
            if (ret < 0 || ((!(ifr.ifr_flags & IFF_RUNNING))&& (!(ifr.ifr_flags & IFF_UP))))
            {
                __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "is not running or up");
                
            } else {
                value = true;
                break;
            }
        }
    }

    free(conf.ifc_ifcu.ifcu_buf);
    close(sock);
    return value;  
}




