#ifndef WFD_RTSP_PROXY_H
#define WFD_RTSP_PROXY_H

#include "WFDSessionMentor.h"

namespace rtsp
{
class WfdRtspPlayer;
class WfdRtspProxy : public WFDSessionMentor
{
public:
    WfdRtspProxy();
    virtual ~WfdRtspProxy();
    virtual bool start(NetDevice * dev, WFDPlayerProxy * playerProxy);
    virtual bool play();
    virtual bool pause();
    virtual bool stop();
private:
    WfdRtspPlayer*      mPlayer;
    WFDPlayerProxy*     mPlayerProxy;
};
}

#endif
