#include "dlna/Event.h"
#include <android/log.h>
#include <stdlib.h>

dlna::DeviceEvent::DeviceEvent(jobject source, DLNA_DEVICE_EVENT_T event, DLNA_DMP_DEVICE_T device, char*name )
    :source(source), event(event),device(device)
{
    this->name = strdup(name);
}

dlna::DeviceEvent::~DeviceEvent()
{
    free(this->name);
}

dlna::ContentEvent::ContentEvent( jobject source, DLNA_DMP_OBJECT_EVENT_T event, DLNA_DMP_OBJECT_INFO_T * content )
    :source(source),event(event),content(content)
{
}

dlna::ContentEvent::~ContentEvent()
{
}

dlna::NormalEvent::NormalEvent( jobject source )
    :source(source)
{
}

dlna::NormalEvent::~NormalEvent()
{
}


dlna::ExitEvent::ExitEvent( jobject source )
    :source(source)
{
}

dlna::ExitEvent::~ExitEvent()
{
}

dlna::Event::~Event()
{
}