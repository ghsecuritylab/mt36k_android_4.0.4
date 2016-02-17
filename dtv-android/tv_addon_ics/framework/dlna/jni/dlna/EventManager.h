#ifndef EVENT_MANAGER_H
#define EVENT_MANAGER_H

#include "dlna/Event.h"

namespace dlna {
    class EventManager {
    private:
        EventManager(){};
    public:
        static void send(Event*);
        static Event* recv();
        static jobject build(JNIEnv *,Event*);
    };
};

#endif