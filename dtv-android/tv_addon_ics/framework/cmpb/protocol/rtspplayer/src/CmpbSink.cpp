#include "CmpbSink.h"
#include "myLog.h"
#include "UsageEnvironment.hh"
#include <sys/time.h>
#include <unistd.h>
#include "PushPlayer.h"
#include "Mutex.h"
#include "ScopedMutex.h"

#ifdef WFD_DEMO_SAVE_FILE
    #include <sys/types.h>
    #include <sys/stat.h>
    #include <fcntl.h>
#endif
namespace rtsp
{

static Mutex sinklocker;
static bool bSinkAlive = true;

CmpbSink::CmpbSink(UsageEnvironment& env, MediaSubsession &subsession, unsigned int type,
                    unsigned bufferSize, unsigned attrHeadSize):MediaSink(env), uiType(type), 
                            fBufferSize(bufferSize), uiAttrHeadSize(attrHeadSize), fSubsession(&subsession)
{
	fBuffer = new unsigned char[bufferSize + attrHeadSize];
	if (NULL == fBuffer)
	{
		LOG_ERR("error!");
	}
    dInitPlayTime = 0;
    dLastPktTime = 0;
    bHaveSyncUseRTCP = true;
    bSinkAlive = true;
    
#ifdef WFD_DEMO_SAVE_FILE
    static int id = 0;
    char buf[16];
    memset(buf, 0, sizeof(buf));
    sprintf(buf, "/3rd/stream%d", id++);
	fd = ::open(buf, O_RDWR | O_CREAT | O_TRUNC | O_APPEND);
#endif    
}

CmpbSink::~CmpbSink()
{
    ScopedMutex sm(sinklocker);

    bSinkAlive = false;
    
	if (fBuffer != NULL)
	{
		delete[] fBuffer;
	}
#ifdef WFD_DEMO_SAVE_FILE   
    if (fd)
        ::close(fd);
    fd = 0;
#endif    
}

CmpbSink* CmpbSink::createNew(UsageEnvironment& env, MediaSubsession &subsession, unsigned int type, unsigned bufferSize)
{	
	return  new CmpbSink(env, subsession, type, bufferSize, 0);
}

Boolean CmpbSink::continuePlaying()
{
    if (fSource == NULL) 
	{
		return False;
    }
	
    fSource->getNextFrame(fBuffer + uiAttrHeadSize, fBufferSize, afterGettingFrame, this, onSourceClosure, this);

    return True;
}

void CmpbSink::afterGettingFrame(void* clientData, unsigned frameSize, unsigned numTruncatedBytes,
				 struct timeval presentationTime, unsigned durationInMicroseconds) 
{
    /*
     *NOTE:the locker's type is PTHREAD_MUTEX_RECURSIVE_NP, so can be called more than one times in the same
     *      thread, and receive data thread different with delete cmpbsink thread
     */
    ScopedMutex sm(sinklocker);
    
    if (bSinkAlive == false)
    {
        LOG_ERR("sink already be deleted");
        return;
    }
    
  	CmpbSink* sink = (CmpbSink*)clientData;
	if (NULL == sink)
	{
		LOG_ERR("error!");
		return;
	}
#if 0	
	LOG_DEBUG("size:%d, bytes:%d,time:[%d:%d],duration:%d!", frameSize, numTruncatedBytes,(int)presentationTime.tv_sec,
						(int)presentationTime.tv_usec, durationInMicroseconds);
#endif
	sink->afterGettingFrame1(frameSize, presentationTime);
}

void CmpbSink::afterGettingFrame1(unsigned frameSize, struct timeval &presentationTime)
{
#ifdef WFD_DEMO_SAVE_FILE   
    if (fd)
        ::write(fd, fBuffer + uiAttrHeadSize, frameSize);
//    LOG_DEBUG("cmpb sink received data length:%d", frameSize);
#else    
	sendData(fBuffer + uiAttrHeadSize, frameSize, presentationTime);
#endif    
	continuePlaying();
}

void CmpbSink::sendData(unsigned char *pBuf, unsigned int iLen, struct timeval &duration)
{
    double dCurPlayTime = 0.0;

    if (1)
    {
        dCurPlayTime = fSubsession->getNormalPlayTime(duration);
        if (dCurPlayTime < 0)/*old packet, ignore it*/
        {
            LOG_ERR("old packet, ignore it, seq num=%d, rtp seq num=%d", fSubsession->rtpSource()->curPacketRTPSeqNum(), fSubsession->rtpInfo.seqNum);
            return;
        }
        else if (dCurPlayTime == 0)
        {
            dCurPlayTime =  fSubsession->playStartTime() + (double)(duration.tv_sec + duration.tv_usec/1000000.0)*fSubsession->scale();
			if (bHaveSyncUseRTCP == true)
			{
				bHaveSyncUseRTCP = false;
				dInitPlayTime = dCurPlayTime + (dInitPlayTime - dLastPktTime);
				LOG_ERR("1 reset init play time is %f", dInitPlayTime);
			}
			
            LOG_ERR("no rtp-info header");
        }
		else
		{
			if (bHaveSyncUseRTCP == false)
			{
				bHaveSyncUseRTCP = true;
				dInitPlayTime = (dInitPlayTime - dLastPktTime);/*already playing time*/
				LOG_ERR("2 reset init play time is %f", dInitPlayTime);
			}
		}

		dLastPktTime = dCurPlayTime;

		if (dInitPlayTime == 0)
        {
            dInitPlayTime = dCurPlayTime;
            LOG_ERR("init play time is %f", dInitPlayTime);
        }
    }

    LOG_DEBUG("current play time is %f", dCurPlayTime - dInitPlayTime);
	/*
	 *TO DO:add data to cmpb
	 */
	GetBufData tmp(pBuf, uiType, iLen, dCurPlayTime - dInitPlayTime);
	PushPlayer::instance().SendData(tmp);
}

}
