#include "WFDClient.h"
#include "Log.h"
#include <algorithm>

WFDClient WFDClient::_INS_;

WFDClient::WFDClient() : myListener(NULL), alive(false), connectTo(NULL), devMentor(NULL)
{
     ssMentor = NULL;
}

bool WFDClient::start(WFDDeviceMentor * deviceMentor, WFDDeviceListener *listener,
                      WFDSessionMentor *sessionMentor)
{
    alive = true;
    devMentor = deviceMentor;
    ssMentor = sessionMentor;
    myListener = listener;
    return devMentor->start(this);
}

void WFDClient::stop()
{
    alive = false;
    devMentor->stop();
    
}

class DeviceFinder
{
public:
    static void find(BasicDevice * dev)
        {
            which = dev;
        }
    static bool equal(WFDDevice * equalto)
        {
            return which->getDeviceMac() == equalto->getDeviceMac();
        }
private:
    DeviceFinder()
        {
        }
    
    static BasicDevice * which;
};

BasicDevice* DeviceFinder::which = NULL;

void WFDClient::notifyDeviceStatusChange(WFDClient_Event_e event, void * param)
{
    if (event == WFD_DEVICE_FOUND)
    {
        // add device
        // find if there is a device same as this new device found before.
        DeviceFinder::find((BasicDevice*)param);
        DeviceList::iterator it = std::find_if(devices.begin(), devices.end(), DeviceFinder::equal);
        if (it != devices.end())
        {
            // have found that the device is not new, ignore this device
        }else{
            WFDDevice * dev = new WFDDevice((BasicDevice*) param); // param must be BasicDevice * type
            devices.push_back(dev);
        }
    }else if( event == WFD_DEVICE_BYEBYE)
    {
        // remove device
        BasicDevice * rawdev = (BasicDevice*)param;
        
        DeviceList::iterator it;
        for(it = devices.begin(); it != devices.end(); ++it)
        {
            WFDDevice & dev = *(*it);
            if (dev == *rawdev)
            {
                // have found
                devices.erase(it);
                delete *it;
                
                return;
            }
        }
        // can't find, todo ...
    }else if (event == WFD_DEVICE_CONNECTED)
    {
        // connected device .. have got mac and name.
        WFDDevice * dev = (WFDDevice*)param;
        LOG(LOG_INF, "The device [%s, %s] connected", dev->name.c_str(), dev->mac.c_str());
        dev->setConnected(true);
        if (dev != connectTo)
        {
            LOG(LOG_ERR, "The callback device is error");
        }
        
    }else if(event == WFD_DEVICE_DISCONNECTED)
    {
        // todo ...
        
    }else if(event == WFD_DEVICE_IP_GET)
    {
        // use this netDev to setup WFD negotiation session
        LOG(LOG_INF, "Ip:%s, port:%d got", connectTo->getIp().c_str(), connectTo->getPort());
        
        if (ssMentor != NULL)
        {
            LOG(LOG_INF, "Begin to start session");
            ssMentor->start(connectTo, this); // todo ... return code check.
        }
    }else if(event == WFD_DEVICE_START_OK){
        
    }
    else if(event == WFD_DEVICE_START_FAILED){
        
    }
    
    if (myListener)
    {
        myListener->notifyDeviceStatusChange(event, param);
    }
}

bool WFDClient::start(WFDSessionMentor *serviceMentor, NetDevice * netdev)
{
    ssMentor = serviceMentor;
    return ssMentor->start(netdev, this);
}

bool WFDClient::find()
{
    return devMentor->find();
}

DeviceList  * WFDClient::getDevices()
{
    return &devices;
}

bool WFDClient::connect(BasicDevice * dev)
{
    WFDDevice * wdev = (WFDDevice *)dev;
    connectTo = wdev;
    return devMentor->connect(dev, wdev);
}

WFDClient & WFDClient::getInstance()
{
    return _INS_;
}

bool WFDClient::notifyToPlay()
{
    return true;
}

bool WFDClient::notifyToPause()
{
    return true;
}

bool WFDClient::notifyToStop()
{
    return true;
}

bool WFDClient::disconect()
{
    if (devMentor)
    {
        bool ret =  devMentor->disconnect();
        if (ret)
        {
            if(connectTo){
                connectTo->setConnected(false); // todo ... maybe disconnected is async method.
            }
            return true;
        }
    }
    return false;
}

bool WFDClient::mediaPlay()
{
    if (ssMentor)
    {
        return ssMentor->play();
    }
    return false;
}

bool WFDClient::mediaPause()
{
    if (ssMentor)
    {
        return ssMentor->pause();
    }
    return false;
}

bool WFDClient::mediaTearDown()
{
    if (ssMentor)
    {
        return ssMentor->stop();
    }
    return false;
}
