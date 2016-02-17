#ifndef _WFDSESSIONMENTOR_H_
#define _WFDSESSIONMENTOR_H_

#include "WFDDevice.h"
class WFDPlayerProxy
{
public:
    virtual bool notifyToPlay() = 0;
    virtual bool notifyToPause() = 0;
    virtual bool notifyToStop() = 0;
};

class WFDSessionMentor
{
public:
    virtual bool start(NetDevice * dev, WFDPlayerProxy * playerProxy) = 0;
    virtual bool play() =0 ;
    virtual bool pause() = 0;
    virtual bool stop() = 0;
};

#endif /* _WFDSESSION_H_ */
