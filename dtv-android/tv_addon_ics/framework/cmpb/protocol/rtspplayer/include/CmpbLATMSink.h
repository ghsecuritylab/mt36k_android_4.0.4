#ifndef _CMPB_LATM_SINK_H_
#define _CMPB_LATM_SINK_H_
#include "CmpbSink.h"

namespace rtsp
{
class CmpbLATMSink:public CmpbSink
{
public:
    static CmpbLATMSink* createNew(UsageEnvironment& env, MediaSubsession &subsession, unsigned int type = 0, 
								 	unsigned bufferSize = 100000);
 
protected:
    CmpbLATMSink(UsageEnvironment& env,MediaSubsession &subsession, unsigned int type, unsigned bufferSize);
    virtual ~CmpbLATMSink();
 	
protected: 
 	virtual void afterGettingFrame1(unsigned frameSize, struct timeval &presentationTime);
private:
    unsigned char pucSpecialHead[8]; 
    unsigned char pucSyncCode[2];
    unsigned char ucSampleRate;
    unsigned char ucChannelID;
    unsigned int ucFrameLen;
private:
    unsigned char GetSampFreq(MediaSubsession &subsession);
};
 
}
  
#endif
  
 
