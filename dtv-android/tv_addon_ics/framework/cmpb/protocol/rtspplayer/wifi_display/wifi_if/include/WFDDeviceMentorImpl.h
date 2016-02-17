#ifndef __WFDDeviceMentorImpl_
#define __WFDDeviceMentorImpl_
#include "WFDDeviceMentor.h"


class WFDDeviceMentorImpl : public WFDDeviceMentor
{
public:
    bool start(WFDDeviceListener * listnener); // discovery device (include found alive and byebye.)
    bool stop();
    // BasicDeviceList * get();
    bool connect(BasicDevice *dev, NetDevice  * connectedDev); // can connect more than one device meantime?
    bool disconnect();
		
    bool find();
    
    WFDDeviceCap * getCapabilities(BasicDevice *dev);

	//BasicDeviceList _device_list ;

};


#endif

