#ifndef _WFDDEVICE_H_
#define _WFDDEVICE_H_
#include <string>
#include <vector>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
class BasicDevice
{
public:
    BasicDevice()
        {
        }
    void set(const std::string & addr, const std::string & basicInfo)
        {
            mac = addr;
            info = basicInfo;
        }

    std::string& getDeviceMac()
        {
            return mac;
        }
    
    std::string& getDeviceName()
        {
            return name;
        }
   
    std::string mac;
    std::string info;
    std::string name;
    bool   is_go;
    bool    is_wfd;
    unsigned int  config_methods ;
    unsigned int  dev_capab;
    unsigned int  group_capab;
    unsigned int  wfd_dev_info;
    unsigned int  rtsp_port;
    unsigned int  max_throughput;
    
};

class NetDevice
{
public:
    NetDevice()
        {
        }

     void set(int port, std::string ip)
        {
            fprintf(stderr,">>>> set port = %d ip = %s",port ,ip.c_str());
            this->port = port;
            this->ip = ip;
        }

    std::string& getIp()
        {
            return ip;
        }
    
    int getPort()
        {
            return port;
        }

 protected :       
    std::string ip;
    unsigned int port;
};
    
#endif /* _WFDDEVICE_H_ */
