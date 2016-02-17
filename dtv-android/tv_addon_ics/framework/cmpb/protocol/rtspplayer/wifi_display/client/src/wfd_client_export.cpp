/////////////////////////////////////////////////////////// C-API implementaion
#ifndef _cplusplus
#define _cplusplus
#include "WFDClient.h"
#include "WFDDeviceMentorImpl.h"
#include "WfdRtspProxy.h"

#endif

class WFDAppProxy: public WFDDeviceListener
{
public:
    WFDAppProxy()
        {
            plist = WFDClient::getInstance().getDevices();
        }
    
    virtual void notifyDeviceStatusChange(WFDClient_Event_e event, void * param)
        {
            // param needn't handle here, maybe future need it.
            if (callback)
            {
                callback(event); 
            }
        }
    
    void setClientCallback(WFD_Client_callback pf_callback)
        {
            callback = pf_callback;
        }
        
    WFDDevice * getDevice(int index)
        {
            if (plist != NULL)
            {
                return plist->at(index);
            }
            return NULL;
        }

    int getDeviceNumber()
        {
            if (plist != NULL)
            {
                return plist->size();
            }
            return 0;
        }
    
    WFDDeviceMentor * getDeviceMentor()
        {
            return &devMentor;
        }
    
    WFDSessionMentor * getSessionMentor()
        {
            return &ssMentor;        // todo ...
        }
    
private:
    WFD_Client_callback callback;
    DeviceList * plist;
    WFDDeviceMentorImpl devMentor; 
    rtsp::WfdRtspProxy ssMentor; // todo... implemented by Wen tan
};

static bool wfd_client_started = false;
static WFDAppProxy wfdProxy;
#if 0
    rtsp::WfdRtspProxy *ssMentor;
    rtsp::WfdRtspProxy ssM;
#endif        
#ifdef _cplusplus
extern "C"
{
#endif
int  wfd_client_start(WFD_Client_callback pf_callback)
{
    if (wfd_client_started)
    {
        return false;
    }
    wfdProxy.setClientCallback(pf_callback);
    wfd_client_started = true;
    return WFDClient::getInstance().start(wfdProxy.getDeviceMentor(), &wfdProxy, wfdProxy.getSessionMentor());
}

int wfd_client_get_deviceNumber()
{
    return wfdProxy.getDeviceNumber();
}

const char* wfd_client_get_deviceName(int index)
{
    BasicDevice * dev = wfdProxy.getDevice(index);
    if (dev != NULL)
    {
        std::string &name = dev->getDeviceName();
        return name.c_str();
    }
    return "";
}

const char* wfd_client_get_deviceMac(int index)
{
    BasicDevice * dev = wfdProxy.getDevice(index);
    if (dev != NULL)
    {
        std::string & mac = dev->getDeviceMac();
        return mac.c_str();
    }
    return "";
}
    
const char* wfd_client_get_deviceIp(int index)
{
    WFDDevice * dev = wfdProxy.getDevice(index);
    if (dev != NULL)
    {
        std::string & ip = dev->getIp();
        return ip.c_str();
    }
    return "";
}
    
int wfd_client_get_devicePort(int index)
{
    WFDDevice * dev = wfdProxy.getDevice(index);
    if (dev != NULL)
    {
        return dev->getPort();
    }
    return 0;
}
    
int  wfd_client_connect_device(int index)
{
     BasicDevice * dev = wfdProxy.getDevice(index);
     if (dev != NULL)
     {
         return WFDClient::getInstance().connect(dev);
     }
     return -1;
}

int wfd_client_is_connected(int index)    
{
    WFDDevice * dev = wfdProxy.getDevice(index);
    if (dev != NULL)
    {
        return dev->isConnected();
    }
    return 0;
}

void wfd_client_stop()
{
    WFDClient::getInstance().stop();
    wfd_client_started = false;
}

int wfd_client_find()
{
    return WFDClient::getInstance().find();
}

int  wfd_client_disconnect_device()
{
    return WFDClient::getInstance().disconect();
}

int wfd_client_media_play()
{
    return WFDClient::getInstance().mediaPlay();
}
    
int wfd_client_media_pause()
{
    return WFDClient::getInstance().mediaPause();
}
    
int wfd_client_media_teardown()
{
    return WFDClient::getInstance().mediaTearDown();
}
    
    // for debug rtsp
#if 1
     
        
int wfd_client_debug_rtsp(char* ip, int port)
{

    BasicDevice device;
    device.set("xx:xx:xx:xx:xx:xx", "wfd device");
    
    WFDDevice dev(&device); 
    NetDevice *netdev = &dev;
    
    netdev->set(port, std::string(ip));
    return WFDClient::getInstance().start(wfdProxy.getSessionMentor(), netdev);

}
#endif

#ifdef _cplusplus
}
#endif
