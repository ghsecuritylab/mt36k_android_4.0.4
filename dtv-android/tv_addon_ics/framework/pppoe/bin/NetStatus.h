#ifndef NET_NETSTATUS_H
#define NET_NETSTATUS_H

#include <list>
#include <string>

namespace net
{
    static const int MAC_LENGTH = 6;

    static const int LOCAL        = 0;
    static const int ETHERNET     = 1;
    static const int WIFI         = 2;
    static const int POINTTOPOINT = 4;

    struct DeviceInfo {
        std::string name;
        int addr;
        int mac[MAC_LENGTH];
        int type;
    };

    class NetStatus
    {
    public:
        static std::list <DeviceInfo*> getActiveDevices() ;
        static bool isPPPReady();
        static std::list<std::string> getDevices();
    private:
        NetStatus(){};
    };
};

#endif