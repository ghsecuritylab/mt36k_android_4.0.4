#include "CmpbAACSink.h"
#include "MPEG4GenericRTPSource.hh"
#include "myLog.h"
 
namespace rtsp
{
 
CmpbAACSink::CmpbAACSink(UsageEnvironment& env, MediaSubsession &subsession, unsigned int type, unsigned bufferSize)
                                :CmpbSink(env, subsession, type, bufferSize, 7)
{
    memset(pucSpecialHead, 0, sizeof(pucSpecialHead));
    ucSampleRate = GetSampFreq(subsession);
    ucChannelID = (unsigned char)subsession.numChannels();
    pucSyncCode[0] = 0xFF;
    if (strcmp(subsession.codecName(), "MPEG4-GENERIC") == 0)
    {
        pucSyncCode[1] = 0xF1;     
    }
    else
    {
        pucSyncCode[1] = 0xF9; 
    }
    LOG_DEBUG("rtp channel=%02x", ucChannelID);
    LOG_DEBUG("rtp sync code[1]=%02x", pucSyncCode[1]);
    ucFrameLen = 0;
}
 
CmpbAACSink::~CmpbAACSink()
{
}
 
CmpbAACSink* CmpbAACSink::createNew(UsageEnvironment& env, MediaSubsession &subsession, unsigned int type, unsigned bufferSize)
{
    return new CmpbAACSink(env, subsession, type, bufferSize);
}
 
 
void CmpbAACSink::afterGettingFrame1(unsigned frameSize, struct timeval &presentationTime)
{
    /*
     *NOTE:cann't get frame len, so use the framesize instead, but this may cause problem
     */
     
    ucFrameLen = frameSize+7;
    LOG_DEBUG("aac frame len = %d", ucFrameLen);
   
    pucSpecialHead[0] = pucSyncCode[0];
    pucSpecialHead[1] = pucSyncCode[1];
    pucSpecialHead[2] = (unsigned char)((1<<6)|((ucSampleRate<<2)&0x3C)|((ucChannelID>>2)&0x1));
    pucSpecialHead[3] = (unsigned char)(((ucChannelID&0x3)<<6)|((ucFrameLen>>11)&0x3));
    pucSpecialHead[4] = (unsigned char)((ucFrameLen>>3)&0xFF);
    pucSpecialHead[5] = (unsigned char)(((ucFrameLen<<5)&0xE0)|((0x7FF>>6)&0x1F));
    pucSpecialHead[6] = (unsigned char)((0x7FF<<2)&0xFC);
    memcpy(fBuffer, pucSpecialHead, 7);
   
    sendData(fBuffer, 7 + frameSize, presentationTime);

    continuePlaying();
}
 
unsigned char CmpbAACSink::GetSampFreq(MediaSubsession &subsession)
{
    unsigned rate ;
    if (strcmp(subsession.codecName(), "MPEG4-GENERIC") == 0)
    {
        rate = samplingFrequencyFromAudioSpecificConfig(subsession.fmtp_config());
    }
    else
    {
        rate = subsession.rtpTimestampFrequency();
    }
    
    LOG_DEBUG("rtp sample rate = %d", rate);
    switch (rate)
    {
        case 96000:
            return 0x0;
        case 88200:
            return 0x1;
        case 64000:
            return 0x2;
        case 48000:
            return 0x3;
        case 44100:
            return 0x4;
        case 32000:
            return 0x5;
        case 24000:
            return 0x6;
        case 22050:
            return 0x7;
        case 16000:
            return 0x8;
        case 12000:
            return 0x9;
        case 11025:
            return 0xa;
        case 8000:
            return 0xb;
        case 7350:
            return 0xc;
        default:
            return 0xFF;
    }

    return 0xFF;
}
}
 
 
