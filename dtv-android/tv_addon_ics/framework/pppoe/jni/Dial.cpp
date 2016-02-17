#include <binder/IServiceManager.h>

#include <binder/IPCThreadState.h>
#include <android/log.h>

#include "Dial.h"

using namespace android;

static const std::string TAG = "pppoe::Dail";

namespace pppoe {
    sp<IBinder> binder = NULL;
    static const int DIAL_UP = 0;
    static const int HANG_UP = 1;
    static const int GET_INFO = 2;
    static const int MONITOR_START = 3;
    static const int MONITOR_STOP = 4;
    static const int GET_DEVICES = 5;
    const std::string SERVICE_NAME = "pppoe.dial";

    int Dial::dialUp( std::string & device, std::string & username, std::string & password )
    {
        Parcel data;
        Parcel reply;

        if(binder == NULL) {
            return 0;
        }

        data.writeCString(device.c_str());
        data.writeCString(username.c_str());
        data.writeCString(password.c_str());

        binder->transact(DIAL_UP, data, &reply);

        int i = reply.readInt32();

        return i;
    }

    int Dial::hangUp()
    {
        Parcel data;
        Parcel reply;

        if(binder == NULL) {
            return 0;
        }

        binder->transact(HANG_UP, data, &reply);

        int i = reply.readInt32();

        return i;
    }

    int Dial::getStatus()
    {
        Parcel data;
        Parcel reply;

        if(binder == NULL) {
            return 0;
        }

        binder->transact(GET_INFO, data, &reply);

        int i = reply.readInt32();

        return i;
    }

    void Dial::start(std::string & device)
    {
        Parcel data;
        Parcel reply;

        if(binder == NULL) {
            return ;
        }

        data.writeCString(device.c_str());
        binder->transact(MONITOR_START, data, &reply);


    }

    void Dial::stop(std::string & device)
    {
        Parcel data;
        Parcel reply;

        if(binder == NULL) {
            return ;
        }
        data.writeCString(device.c_str());

        binder->transact(MONITOR_STOP, data, &reply);


    }

    std::list<std::string> Dial::getDevices()
    {
        Parcel data;
        Parcel reply;

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "GetDevices");

        std::list<std::string> list;

        if(binder == NULL) {
            return list;
        }

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "GetDevices transact");

        binder->transact(GET_DEVICES, data, &reply);

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "GetDevices transact return");
        
        int size = reply.readInt32();

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "GetDevices return %d", size);

        for (int i = 0; i < size; i++)
        {
            char * info  = (char*)reply.readCString();
            std::string dev(info);
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "GetDevices return dev %s", info);
            list.push_back(dev);
        }

        return list;
    }

    Dial::Dial()
    {
        sp<IServiceManager> sm = defaultServiceManager();

        binder = sm->getService(String16(SERVICE_NAME.c_str()));
    }

    Dial::~Dial()
    {
    }

};