#ifndef _WFDCLIENT_H_
#define _WFDCLIENT_H_
#include "WFDDeviceMentor.h"
#include "WFDSessionMentor.h"


class WFDDevice : public BasicDevice, public NetDevice
{
public:
    WFDDevice(BasicDevice * basicDev):connected(false)
        {
            name = basicDev->getDeviceName();
            mac = basicDev->getDeviceMac();
        }

    bool operator == (BasicDevice &  basic)
        {
            return (basic.getDeviceName() == info && basic.getDeviceMac() == mac);
        }

    bool isConnected()
        {
            return connected;
        }
    
    void setConnected(bool yes)
        {
            connected = yes;
        }
    
private:
    bool connected;
};

typedef std::vector<WFDDevice *> DeviceList;  
    
class WFDClient: public WFDDeviceListener, public WFDPlayerProxy
{
public:
    
    static WFDClient & getInstance();
    
    bool start(WFDDeviceMentor * deviceMentor, WFDDeviceListener *listener, 
               WFDSessionMentor *serviceMentor);
    bool start(WFDSessionMentor *serviceMentor, NetDevice * netdev);
    
    
    DeviceList * getDevices();
    
    bool connect(BasicDevice *dev);
    bool disconect();
    
    void stop();
    bool find();
    bool mediaPlay();
    bool mediaPause();
    bool mediaTearDown();
    
    virtual void notifyDeviceStatusChange(WFDClient_Event_e event, void * param);
    virtual bool notifyToPlay();
    virtual bool notifyToPause();
    virtual bool notifyToStop();
    WFDClient();

private:
    static WFDClient _INS_;
    WFDDeviceListener * myListener;
    
    bool alive;
    DeviceList devices;
    WFDDevice * connectTo;
    
    WFDDeviceMentor * devMentor;


    WFDSessionMentor * ssMentor;

};

    // #include "WfdRtspProxy.h"
    // extern rtsp::WfdRtspProxy* ssMentor;

#endif /* _WFDCLIENT_H_ */
