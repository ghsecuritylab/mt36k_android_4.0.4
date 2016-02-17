#include "CmpbH264Sink.h"
#include "H264VideoRTPSource.hh"
#include "myLog.h"

namespace rtsp
{
static unsigned char start_code[4] = {0x00, 0x00, 0x00, 0x01};

CmpbH264Sink::CmpbH264Sink(UsageEnvironment& env, MediaSubsession &subsession, unsigned int type, unsigned bufferSize)
				                :CmpbSink(env, subsession, type, bufferSize, 4), 
				                        fHaveWrittenFirstFrame(false)
{
}

CmpbH264Sink::~CmpbH264Sink()
{
}

CmpbH264Sink* CmpbH264Sink::createNew(UsageEnvironment& env, MediaSubsession &subsession, unsigned int type, unsigned bufferSize)
{
	return new CmpbH264Sink(env, subsession, type, bufferSize);
}


void CmpbH264Sink::afterGettingFrame1(unsigned frameSize, struct timeval &presentationTime)
{
    
#if 1	
  	if (!fHaveWrittenFirstFrame) 
	{		
	    // If we have PPS/SPS NAL units encoded in a "sprop parameter string", prepend these to the file:
	    unsigned numSPropRecords;
	    SPropRecord* sPropRecords = parseSPropParameterSets(fSubsession->fmtp_spropparametersets(), numSPropRecords);
		unsigned int iLen = 0;
		for (unsigned i = 0; i < numSPropRecords; ++i) 
		{
			iLen += sPropRecords[i].sPropLength;
		}
		iLen += (numSPropRecords<<2) + 4 + frameSize;
		unsigned char *pTmp = (unsigned char *)malloc(iLen);
		if (pTmp)
		{
			unsigned int iTmpLen = 0;
			for (unsigned i = 0; i < numSPropRecords; ++i)
			{
				memcpy(pTmp+ iTmpLen, start_code, 4);
				iTmpLen += 4;
				memcpy(pTmp + iTmpLen , sPropRecords[i].sPropBytes, sPropRecords[i].sPropLength);
				iTmpLen += sPropRecords[i].sPropLength;
			}

			memcpy(pTmp + iTmpLen, start_code, 4);
            memcpy(pTmp + iTmpLen + 4, fBuffer + 4, frameSize);
			LOG_DEBUG("send data with prop  len:%d", iLen);
			sendData(pTmp, iLen, presentationTime);
			free(pTmp);
            delete[] sPropRecords;
    	    fHaveWrittenFirstFrame = True;
		}
		else
		{
			LOG_ERR("malloc size:%d failed", iLen);
            delete[] sPropRecords;
		}
    }
	else
	{
		LOG_DEBUG("send data with special header len:%d", 4 + frameSize);
        memcpy(fBuffer, start_code, 4);
		sendData(fBuffer, 4 + frameSize, presentationTime);
	}
 #endif 
	continuePlaying();
}


}

