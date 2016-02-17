#ifndef _CMPB_H264_SINK_H_
#define _CMPB_H264_SINK_H_
#include "CmpbSink.h"

namespace rtsp
{
class CmpbH264Sink:public CmpbSink
{
public:
	static CmpbH264Sink* createNew(UsageEnvironment& env, MediaSubsession &subsession,
				      				unsigned int type = 0, unsigned bufferSize = 100000);

protected:
	CmpbH264Sink(UsageEnvironment& env, MediaSubsession &subsession, unsigned int type, unsigned bufferSize);
	virtual ~CmpbH264Sink();

protected: 
	virtual void afterGettingFrame1(unsigned frameSize, struct timeval &presentationTime);
private:
  	bool fHaveWrittenFirstFrame;
};

}

#endif
