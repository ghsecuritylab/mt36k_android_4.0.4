#ifndef ANDROID_DIAL_H

#define ANDROID_DIAL_H

#include <string>
#include <list>

namespace pppoe {
    class Dial {
    public:
        static const int Disconnect = 0;
        static const int Connecting = 1;
        static const int Connect = 2;


    public:
        int dialUp(std::string & device, std::string & username, std::string & password);
        int hangUp();
        int getStatus();
        void start(std::string & device);
        void stop(std::string & device);
        std::list<std::string> getDevices();

    public:
        Dial();
        ~Dial();
    };
};

#endif // ANDROID_DIAL_H