#ifndef _CMPB_SINK_H_
#define _CMPB_SINK_H_

#include "MediaSink.hh"
#include "MediaSession.hh" 

namespace rtsp
{
class CmpbSink:public MediaSink
{
protected:
	CmpbSink(UsageEnvironment& env, MediaSubsession &subsession, unsigned int type, 
				unsigned bufferSize, unsigned attrHeadSize); // abstract base class
    virtual ~CmpbSink(); // instances are deleted using close() only

protected:
    static void afterGettingFrame(void* clientData, unsigned frameSize, unsigned numTruncatedBytes,
    								struct timeval presentationTime, unsigned durationInMicroseconds);
	virtual void afterGettingFrame1(unsigned frameSize, struct timeval &presentationTime);

public:
  	static CmpbSink* createNew(UsageEnvironment& env,  MediaSubsession &subsession,
									unsigned int type = 0, unsigned bufferSize = 100000);
	void sendData(unsigned char *pBuf, unsigned int iLen, struct timeval& duration);
protected:
    virtual Boolean continuePlaying();

protected:
	unsigned int uiType;
    unsigned char* fBuffer;
    unsigned fBufferSize;
	unsigned uiAttrHeadSize;
	MediaSubsession * fSubsession;
	double dInitPlayTime;
	double dLastPktTime;
	bool bHaveSyncUseRTCP;
#ifdef WFD_DEMO_SAVE_FILE
	int fd;
#endif
};
}
#endif

