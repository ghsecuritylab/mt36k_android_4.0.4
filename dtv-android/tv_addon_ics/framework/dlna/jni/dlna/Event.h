#ifndef DLNA_EVENT_H
#define DLNA_EVENT_H

#include <jni.h>
#include "u_dlna_dmp.h"

namespace dlna {
    class Event {
    public:
        virtual jobject getSource()=0;
        virtual int getType()=0;
    public:
        virtual ~Event()=0;
    };

    class NormalEvent : public Event {
    public:
        static const int NormalEventType = 0;
    private:
        jobject source;

    public:
        jobject getSource(){return source;};
        int getType(){return NormalEventType;};
    public:
        NormalEvent(jobject source);
        ~NormalEvent();
    };

    class ExitEvent : public Event {
    public:
        static const int ExitEventType = 1;
    private:
        jobject source;

    public:
        jobject getSource(){return source;};
        int getType(){return ExitEventType;};
    public:
        ExitEvent(jobject source);
        ~ExitEvent();
    };

    class DeviceEvent : public Event {
    public:
        static const int DeviceEventType = 0x1000;
    private:
        jobject source;
        DLNA_DEVICE_EVENT_T event;
        DLNA_DMP_DEVICE_T device;
        char * name;
    public:
        DeviceEvent(jobject source, DLNA_DEVICE_EVENT_T event, DLNA_DMP_DEVICE_T device, char * name);
        ~DeviceEvent();

    public:
        jobject getSource(){return source;};
        int getType(){return DeviceEventType;};
        DLNA_DEVICE_EVENT_T getEvent(){return event;};
        DLNA_DMP_DEVICE_T getDevice(){return device;};
        char * getName() {return name;};
    };

    class ContentEvent : public Event {
    public:
        static const int ContentEventType = 0x2000;
    private:
        jobject source;
        DLNA_DMP_OBJECT_EVENT_T event;
        DLNA_DMP_OBJECT_INFO_T * content;

    public:
        ContentEvent(jobject source, DLNA_DMP_OBJECT_EVENT_T event, DLNA_DMP_OBJECT_INFO_T * content);
        ~ContentEvent();

    public:
        jobject getSource(){return source;};
        int getType(){return ContentEventType;};
        DLNA_DMP_OBJECT_EVENT_T getEvent(){return event;};
        DLNA_DMP_OBJECT_INFO_T * getContent(){return content;};
    };
};

#endif