#ifndef PPPOE_SERVICE_H
#define PPPOE_SERVICE_H

#include <utils/RefBase.h>

#include <binder/IInterface.h>
#include <binder/Binder.h>
#include <binder/Parcel.h>
#include <string>

namespace pppoe

{
    class PppoeService : public android::BBinder
    {
    public:
        static const std::string SERVICE_NAME;
    public:
        static const int DIAL_UP = 0;
        static const int HANG_UP = 1;
        static const int GET_INFO = 2;
        static const int MONITOR_START = 3;
        static const int MONITOR_STOP = 4;
        static const int GET_DEVICES = 5;

    public:
        static const int Disconnect = 0;
        static const int Connecting = 1;
        static const int Connect = 2;
    public:

        static int instantiate();

        PppoeService();

        virtual ~PppoeService();

        virtual android::status_t onTransact(uint32_t, const android::Parcel&, android::Parcel*, uint32_t);
    };
}

#endif