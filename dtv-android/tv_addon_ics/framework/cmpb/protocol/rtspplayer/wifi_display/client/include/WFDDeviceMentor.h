#ifndef _WFDDEVICEMENTOR_H_
#define _WFDDEVICEMENTOR_H_
#include <list>
#include "WFDDevice.h"
#include "WFD_common.h"

class WFDDeviceCap
{
    
};

class WFDDeviceListener
{
public:
    virtual void notifyDeviceStatusChange(WFDClient_Event_e event, void * param) = 0;
};

class WFDDeviceMentor
{
public:
    virtual bool start(WFDDeviceListener * listnener) = 0; // discovery device (include found alive and byebye.)
    virtual bool stop() = 0;
    // next line use notify to replace
    // virtual BasicDeviceList * get() = 0;
    virtual bool connect(BasicDevice  *dev, NetDevice  * connectedDev) = 0; // can connect more than one device meantime?
    virtual bool disconnect() = 0;
    virtual bool find() = 0;
    virtual WFDDeviceCap * getCapabilities(BasicDevice *dev) = 0;
};

#endif /* _WFDDEVICEMENTOR_H_ */
