#include "MtkRtspClient.h"
#include "myLog.h"
#include "BasicUsageEnvironment.hh"
#include "DigestAuthentication.hh"
#include "MPEG4LATMAudioRTPSource.hh"
#include "GroupsockHelper.hh"
#include "CmpbH264Sink.h"
#include "CmpbAACSink.h"
#include "CmpbLATMSink.h"
#include "CmpbSink.h"
#include "ScopedMutex.h"
#include "myCfg.h"

namespace rtsp
{
#define CHECK_NULL_COND(condition, ret) \
	if (NULL == (condition)) \
	{ \
		LOG_ERR("error!"); \
		return (ret); \
	}
    
static TaskScheduler* scheduler = NULL;
static UsageEnvironment * env = NULL;
static int iSetupCount = 0;
static unsigned int fileSinkBufferSize = 100000;
static Mutex clientlocker;
static bool bClientAlive = true;
const unsigned int maxBufSize = 1024*1024;
extern bool bUseTcp;

void MtkRTSPClient::SenderhandResponse(RTSPClient* rtspClient, int resultCode, char* resultString)
{
    ScopedMutex sm(clientlocker);

    if (bClientAlive == false)
    {
        LOG_ERR("rtsp client already be deleted");
        return;
    }
    //MtkRTSPClient *client = dynamic_cast<MtkRTSPClient *>(rtspClient);
    MtkRTSPClient *client = (MtkRTSPClient *)rtspClient;
	if (NULL == client)
	{
		LOG_ERR("error!");
		return;
	}

    client->SenderhandResponse1(resultCode, resultString);
}

void MtkRTSPClient::SenderhandResponse1(int resultCode, char* resultString)
{    
	bool bErr = false;

	do
	{
		if (NULL == pRtspReqSender)
		{
			LOG_ERR("error!");
			bErr = true;
			break;
		}

		bResponseErr = false;
		
		if (0 != resultCode)/*some error occur*/
		{
			LOG_ERR("response error! \nresultCode = %d", resultCode);
			bErr = true;
			bResponseErr = true;
			break;
		}	
		SenderType type = pRtspReqSender->getSenderType();
		LOG_DEBUG("response send type = %d!", type);
			
		/*
		 *TO DO: deal the result
		 */
		if (SENDERTYPE_OPTINON == type)
		{
		}
		else if (SENDERTYPE_DESCRIPTION == type)
		{
			if (false == handDescription(resultString))
			{
				LOG_ERR("error");
				bErr = true;
				break;
			}
		}
		else if (SENDERTYPE_PLAY == type)
		{		
		}
		else if (SENDERTYPE_PAUSE == type)
		{
		}
		else if (SENDERTYPE_TEARDOWN == type)
		{			
		}
		else if (SENDERTYPE_SETUP== type)
		{	
			if (false == handSetup(resultString))
			{
				LOG_ERR("error");
				bErr = true;
				break;
			}
		}
		else if (SENDERTYPE_SETPARAM== type)
		{
		}
		else if (SENDERTYPE_GETPARAM== type)
		{
		}
		else/*wrong type*/
		{
			bErr = true;
			break;
		}

	}while(0);
	
	if (NULL != resultString)
	{
		LOG_ERR("resultString=%s", resultString);
		delete[] resultString;
		resultString = NULL;
	}

	LOG_DEBUG("bErr = %d", bErr);
	
	if (false == bErr)
	{
		pRtspReqSender->callNext();
	}
	else
	{
		LOG_DEBUG("wake up waiters");
		/*wakeup all waiters */
		wakeupWaiters();
	}
}


void subsessionAfterPlaying(void* clientData) 
{
	LOG_DEBUG("sub session play end!");  
}

bool MtkRTSPClient::handDescription(char* resultString)
{
	CHECK_NULL_COND(resultString, false);

	
	char* sdpDescription = resultString;
	//LOG_DEBUG("SDP description:%s", sdpDescription);
	
	// Create a media session object from this SDP description:
	session = MediaSession::createNew(*env, sdpDescription);
	if (session == NULL) 
	{
			LOG_ERR("Failed to create a MediaSession object from the SDP description: %s", env->getResultMsg());
			return false;
	} 
	if (!session->hasSubsessions())
	{
			LOG_ERR("This session has no media subsessions (i.e., \"m=\" lines)");
			Medium::close(session);
			session = NULL;
			return false;
	}

	/*
	 *TO DO:GET THE TIME RANGE
	 */
	fStartTime = session->playStartTime();
	if (fStartTime < 0)
	{
		fStartTime = 0.0f;
	}

	fEndTime= session->playEndTime();
	if (fEndTime <= 0)
	{
		fEndTime = -1.0f;
	}

	{
		/*send setup requesst count*/
		iSetupCount = 0;
	}
	
	// Then, setup the "RTPSource"s for the session:
	MediaSubsessionIterator iter(*(session));
	MediaSubsession *subsession = NULL;
	RtspReqSender *senderSave = pRtspReqSender->getNext();
	if (senderSave == NULL)
	{
		LOG_ERR("error");
		return false;
	}
	CmdSenderDecorator *senderMove = pRtspReqSender;
	
	while ((subsession = iter.next()) != NULL)
	{
		if (!subsession->initiate(-1))
		{
			LOG_ERR("warning");
			continue;
		}

		if (subsession->rtpSource() != NULL)
		{
#if 0			
			// Because we're saving the incoming data, rather than playing
		  	// it in real time, allow an especially large time threshold
		  	// (1 second) for reordering misordered incoming packets:
			unsigned const thresh = 1000000; // 1 second
		  	subsession->rtpSource()->setPacketReorderingThresholdTime(thresh);
#endif
#if 0
			// Set the RTP source's OS socket buffer size as appropriate - either if we were explicitly asked (using -B),
		  	// or if the desired FileSink buffer size happens to be larger than the current OS socket buffer size.
		  	// (The latter case is a heuristic, on the assumption that if the user asked for a large FileSink buffer size,
		  	// then the input data rate may be large enough to justify increasing the OS socket buffer size also.)
			int socketNum = subsession->rtpSource()->RTPgs()->socketNum();
			unsigned curBufferSize = getReceiveBufferSize(*env, socketNum);
			LOG_DEBUG("old receive buffer size:%d", curBufferSize);
			if (fileSinkBufferSize > curBufferSize) 
			{
			    unsigned newBufferSize = setReceiveBufferTo(*env, socketNum, fileSinkBufferSize);
				LOG_DEBUG("new receive buffer size:%d", newBufferSize);
			}
#else		
			int socketNum = subsession->rtpSource()->RTPgs()->socketNum();
		 	unsigned newBufferSize = setReceiveBufferTo(*env, socketNum, maxBufSize);
			LOG_DEBUG("new receive buffer size:%d", newBufferSize);
#endif
		}
		
		if (subsession->readSource() == NULL) 
		{
			LOG_ERR("warning");
			continue; // was not initiated
		}

		/*
		 *TO DO:SET UP SUBSESSION
		 */
		SetupSender *setupSender = new SetupSender(*senderSave);
		if (setupSender == NULL)
		{
			LOG_ERR("warning");
			continue;
		}
        
		sender->RecordSender(setupSender);
		senderMove->setNext(setupSender);
		senderMove = setupSender;
		setupSender->setRspHandler(respHandler);
		setupSender->setSubsession(subsession);

		if (bUseTcp == true)
		{
			if (subsession->clientPortNum() != 0)
			{
				LOG_DEBUG("sub session %p using tcp port :%d!", subsession, subsession->clientPortNum());
				setupSender->setParam(false, true, false);
			}
		}

		iSetupCount++;
					
		LOG_DEBUG("subsession, name:%s, codec:%s", subsession->mediumName(), subsession->codecName());		
	}

	return true;
}

unsigned int MtkRTSPClient::getBufType(MediaSubsession * subsession)
{
	if (subsession == NULL)
		return 0;
	if (subsession->rtpSource() == NULL)
		return 0;

	int pt = (int)subsession->rtpSource()->rtpPayloadFormat();

	LOG_DEBUG("pay load type:%d!", pt);

    /*
     *get media type by payload type
     */
	/*AUDIO*/
	if ((0 <= pt) && (pt <= 18))
	{
		return mediatype_audio;
	}

	/*AV*/
	if (pt == 33)
	{
		return mediatype_av;
	}

	/*VIDEO*/
	switch (pt)
	{
		case 25:
		case 26:
		case 31:
		case 32:
		case 34:
			return mediatype_video;
	}

    /*
     *get media type by medium name
     */
    if (NULL != subsession->mediumName())
    {
        if (strncasecmp(subsession->mediumName(), "audio", 5) == 0)
        {
            return mediatype_audio;
        }
        else if (strncasecmp(subsession->mediumName(), "video", 5) == 0)
        {
            return mediatype_video;
        }
    }
    
    /*
     *get media type by codec
     */
	/*h264*/
	if (strcmp(subsession->codecName(), "H264") == 0)
	{
		return mediatype_video;
	}
	else if (strcmp(subsession->codecName(), "MP4V-ES") == 0)
	{
		return mediatype_video;
	}
	else if (strcmp(subsession->codecName(), "MPEG4-GENERIC") == 0)
	{
		return mediatype_audio;
	}
	else if (strcmp(subsession->codecName(), "AC3") == 0)
	{
		return mediatype_audio;
	}
	else if (strcmp(subsession->codecName(), "AMR") == 0)
	{
		return mediatype_audio;
	}
	else if (strcmp(subsession->codecName(), "AMR-WB") == 0)
	{
		return mediatype_audio;
	}
	else if (strcmp(subsession->codecName(), "MPA-ROBUST") == 0)
	{
		return mediatype_audio;
	}
	else if (strcmp(subsession->codecName(), "X-MP3-DRAFT-00") == 0)
	{
		return mediatype_audio;
	}
	else if (strcmp(subsession->codecName(), "MP4A-LATM") == 0)
	{
		return mediatype_audio;
	}
	
	return 0;
}
bool MtkRTSPClient::handSetup(char* resultString)
{
	CHECK_NULL_COND(session, false); 
	CHECK_NULL_COND(rtsp::env, false);

	bool bSuccess = false;
	
	// Then, setup the "RTPSource"s for the session:
	MediaSubsessionIterator iter(*(session));
	MediaSubsession *subsession = NULL;
	while ((subsession = iter.next()) != NULL) 
	{					
		if (subsession->readSource() == NULL) 
		{
			LOG_ERR("warning");
			continue; // was not initiated
		}

		if (subsession->sink != NULL)/*already be set*/
		{
			continue;
		}

		unsigned int type = getBufType(subsession);
		if (type == 0)
		{
			LOG_ERR("error type=%d", type);
			continue;
		}
		
		{
			iSetupCount--;
			/*set mediay info*/
			setMediaInfo(subsession, type);
		}

		CmpbSink *sink = NULL;
		if ((type != mediatype_audio) && (strcmp(subsession->codecName(), "H264") == 0))
		{
			sink = CmpbH264Sink::createNew(*env, *subsession, type, fileSinkBufferSize);
		}
        else if ((type == mediatype_audio) && 
                    ((stMediaInfo.audioCodec == MEDIACODEC_AC3) || 
                     (stMediaInfo.audioCodec == MEDIACODEC_EAC3) ||
                     (stMediaInfo.audioCodec == MEDIACODEC_MPEG4_GENERIC)))
		{
			sink = CmpbAACSink::createNew(*env, *subsession, type, fileSinkBufferSize);
		}
        else if ((type == mediatype_audio) && (stMediaInfo.audioCodec == MEDIACODEC_MP4A_LATM))
		{
			sink = CmpbLATMSink::createNew(*env, *subsession, type, fileSinkBufferSize);
		}
		else
		{
			sink = CmpbSink::createNew(*env, *subsession, type, fileSinkBufferSize);
		}
		subsession->sink = sink;
		if (subsession->sink == NULL) 
		{
			LOG_ERR("error!"); 
		} 
		else 
		{		
#if 0 /*this should be remove to cmpb sink*/           
			if ((type != mediatype_audio) && (strcmp(subsession->codecName(), "MP4V-ES") == 0)
				&& (subsession->fmtp_config() != NULL)) 
			{
			    // For MPEG-4 video RTP streams, the 'config' information
			    // from the SDP description contains useful VOL etc. headers.
			    // Insert this data at the front of the output file:
			    unsigned configLen;
			    unsigned char* configData
			      = parseGeneralConfigStr(subsession->fmtp_config(), configLen);
			    struct timeval timeNow;
			    gettimeofday(&timeNow, NULL);
			    sink->sendData(configData, configLen, timeNow);
			    delete[] configData;
		  	}
#endif			
			subsession->sink->startPlaying(*(subsession->readSource()),
												subsessionAfterPlaying,
													subsession);
			// Also set a handler to be called if a RTCP "BYE" arrives
			// for this subsession:
			if (subsession->rtcpInstance() != NULL) 
			{
				subsession->rtcpInstance()->setByeHandler(subsessionAfterPlaying, subsession);
			}

			bSuccess = true;
		}

		break;

	}

	if (iSetupCount == 0)
	{
		mediaInfoReady(); 
	}

	return bSuccess ;
}


 

MtkRTSPClient::MtkRTSPClient(dealResultHandler *handler, char const* rtspURL,
		       int verbosityLevel, char const* applicationName,
		       portNumBits tunnelOverHTTPPortNum): 
		       RTSPClient(*env, rtspURL, verbosityLevel, applicationName, tunnelOverHTTPPortNum),
		       pRtspReqSender(NULL), auth(NULL), session(NULL), sender(NULL),status(CLIENTSTATUS_UNINIT),
		       cIsCmdSendEnd(0), fStartTime(0.0f), fEndTime(-1.0f),
			   bMediaInfoReady(false), bResponseErr(false), respHandler(handler)
{
	memset(&stMediaInfo, 0, sizeof(stMediaInfo));
}

MtkRTSPClient::~MtkRTSPClient()
{
	pRtspReqSender = NULL;
	destroy();
}

MtkRTSPClient* MtkRTSPClient::createNew(char const* rtspURL, dealResultHandler *handler, 
											char const* username, char const* pwd)
{   
    if (scheduler != NULL)
	{
        delete scheduler;
        scheduler = NULL;
	}

    if (env != NULL)
	{
        env->reclaim();
        env = NULL;
	}
    
	if (scheduler == NULL)
	{
		scheduler = BasicTaskScheduler::createNew();
	}
	CHECK_NULL_COND(scheduler, NULL);
	if (env == NULL)
	{
		env = BasicUsageEnvironment::createNew(*scheduler);
	}	
	CHECK_NULL_COND(env, NULL);
	
  	MtkRTSPClient* client =  new MtkRTSPClient(handler, rtspURL, 0, "test.bin", 0);
	CHECK_NULL_COND(client, NULL);
		
	if ((username != NULL ) || (pwd != NULL))
	{
		Authenticator * _auth = new Authenticator(username,pwd);
		client->auth = _auth;
	}

    bClientAlive = true;
    
	return client;
}

bool MtkRTSPClient::play()
{
	ScopedMutex sm(clientlocker);

	if (status != CLIENTSTATUS_CONNECTED)
	{
		LOG_ERR("can't play, current status:%d!", (int)status);
		return false;
	}
	

	PlaySender * playSender = new PlaySender(*sender);
	CHECK_NULL_COND(playSender, false);
    sender->RecordSender(playSender);
	
	playSender->setRange(fStartTime, fEndTime);
	playSender->setRspHandler(respHandler);
	bool bRet = playSender->sendRequest();
	if (false == bRet)
	{
		LOG_ERR("error!");
		return false;
	}
	
	status = CLIENTSTATUS_PLAYED;
    iSetupCount = 0;
	
	return true;
}


bool MtkRTSPClient::connect()
{
	ScopedMutex sm(clientlocker);

	if ((status != CLIENTSTATUS_UNINIT) &&
		(status != CLIENTSTATUS_STOPPED))
	{
		LOG_ERR("can't connect, current status:%d!", (int)status);
		return false;
	}
	
	sender = new SenderInit(this, auth);
	CHECK_NULL_COND(sender, false);
	
	DspSender *dspsender = new DspSender(*sender);
	CHECK_NULL_COND(dspsender, false);
    sender->RecordSender(dspsender);
	
	dspsender->setRspHandler(respHandler);
	
	OptionSender * optionsender = new OptionSender(*dspsender);
	CHECK_NULL_COND(optionsender, false);
    sender->RecordSender(optionsender);
	
	optionsender->setRspHandler(respHandler);
	bool bRet = optionsender->sendRequest();
	if (false == bRet)
	{
		LOG_ERR("error!");
		return false;
	}

	status = CLIENTSTATUS_CONNECTED;
	
	return true;
}

bool MtkRTSPClient::pause()
{
	ScopedMutex sm(clientlocker);

	if (status != CLIENTSTATUS_PLAYED)
	{
		LOG_ERR("can't pause, current status:%d!", (int)status);
		return false;
	}
	
	CHECK_NULL_COND(sender, false);

	PauseSender * pauseSender = new PauseSender(*sender);
	CHECK_NULL_COND(pauseSender, false);
    sender->RecordSender(pauseSender);
	
	pauseSender->setRspHandler(respHandler);
	bool bRet = pauseSender->sendRequest();
	if (false == bRet)
	{
		LOG_ERR("error!");
		return false;
	}

	status = CLIENTSTATUS_PAUSED;
	
	return true;
}

bool MtkRTSPClient::resume()
{
	ScopedMutex sm(clientlocker);

	if (status != CLIENTSTATUS_PAUSED)
	{
		LOG_ERR("can't resume, current status:%d!", (int)status);
		return false;
	}
	
	CHECK_NULL_COND(sender, false);
	CHECK_NULL_COND(session, false);

	PlaySender * playSender = new PlaySender(*sender);
	CHECK_NULL_COND(playSender, false);
    sender->RecordSender(playSender);
	
	playSender->setRange(-1.0f, fEndTime);
	
	playSender->setRspHandler(respHandler);
	bool bRet = playSender->sendRequest();
	if (false == bRet)
	{
		LOG_ERR("error!");
		return false;
	}

	status = CLIENTSTATUS_PLAYED;
	
	return true;
}

bool MtkRTSPClient::stop()
{
	ScopedMutex sm(clientlocker);

	if ((status == CLIENTSTATUS_UNINIT) ||
		(status == CLIENTSTATUS_STOPPED))
	{
		LOG_ERR("can't stop, current status:%d!", (int)status);
		return false;
	}
	
	CHECK_NULL_COND(sender, false);

	TeardownSender * teardownSender = new TeardownSender(*sender);
	CHECK_NULL_COND(teardownSender, false);
    sender->RecordSender(teardownSender);
	
	teardownSender->setRspHandler(respHandler);
	bool bRet = teardownSender->sendRequest();
	if (false == bRet)
	{
		LOG_ERR("error!");
		return false;
	}

	status = CLIENTSTATUS_STOPPED;
	
	return true;
}

bool MtkRTSPClient::timeseek(unsigned int uiTime)
{
	ScopedMutex sm(clientlocker);

	if ((status != CLIENTSTATUS_PLAYED) &&
		(status != CLIENTSTATUS_PAUSED))
	{
		LOG_ERR("can't play, current status:%d!", (int)status);
		return false;
	}
	
	CHECK_NULL_COND(sender, false);
	CHECK_NULL_COND(session, false);

	PlaySender * playSender = new PlaySender(*sender);
	CHECK_NULL_COND(playSender, false);
    sender->RecordSender(playSender);

	LOG_DEBUG("start time=%f, end time=%f, seek time=%d!", fStartTime, fEndTime, uiTime);
	
	if (fEndTime> 0)
	{
		if (uiTime > fEndTime)
		{
			return false;/*seek time error*/
		}
	}
	fStartTime = uiTime + 0.0f;
	playSender->setRange(fStartTime, fEndTime);
	
	playSender->setRspHandler(respHandler);
	bool bRet = playSender->sendRequest();
	if (false == bRet)
	{
		LOG_ERR("error!");
		return false;
	}

	status = CLIENTSTATUS_PLAYED;
		
	return true;
}

bool MtkRTSPClient::IsSeekable()
{
	return fEndTime>0;
}

void MtkRTSPClient::watcherBegin()
{
	cIsCmdSendEnd = 0;
  	env->taskScheduler().doEventLoop(&cIsCmdSendEnd); 
  	//env->taskScheduler().doEventLoop(); 
}

void MtkRTSPClient::watcherEnd()
{
	cIsCmdSendEnd = 1;
}

void MtkRTSPClient::mediaInfoReady()
{
	mediaInfoWaiter.syncBegin();
	bMediaInfoReady = true;
	mediaInfoWaiter.notifyAll();
	mediaInfoWaiter.syncEnd();
}

void MtkRTSPClient::wakeupWaiters()
{
	mediaInfoWaiter.syncBegin();
	LOG_DEBUG("wake up waiters notify all");
	mediaInfoWaiter.notifyAll();
	mediaInfoWaiter.syncEnd();
}

void MtkRTSPClient::setMediaInfo(MediaSubsession *subsession, unsigned int type)
{
	MediaCodec codec;

#if 0
	if (strcmp(subsession->codecName(), "QCELP") == 0)
	{
		codec = MEDIACODEC_QCELP;
	}
	else
#endif
	if (strcmp(subsession->codecName(), "MPA") == 0)
	{
		codec = MEDIACODEC_MPA;
	}
	else if (strcmp(subsession->codecName(), "MPA-ROBUST") == 0)
	{
		codec = MEDIACODEC_MPA_ROBUST;
	}
	else if (strcmp(subsession->codecName(), "X-MP3-DRAFT-00") == 0)
	{
		codec = MEDIACODEC_X_MP3_DRAFT_00;
	}
	else if (strcmp(subsession->codecName(), "MP4A-LATM") == 0)
	{
		codec = MEDIACODEC_MP4A_LATM;
	}
	else if (strcmp(subsession->codecName(), "AC3") == 0)
	{
		codec = MEDIACODEC_AC3;
	}
	else if (strcmp(subsession->codecName(), "EAC3") == 0)
	{
		codec = MEDIACODEC_EAC3;
	}
	else if (strcmp(subsession->codecName(), "MP4V-ES") == 0)
	{
		codec = MEDIACODEC_MP4V_ES;
	}
	else if (strcmp(subsession->codecName(), "MPEG4-GENERIC") == 0)
	{
		codec = MEDIACODEC_MPEG4_GENERIC;
	}
	else if (strcmp(subsession->codecName(), "MPV") == 0)
	{
		codec = MEDIACODEC_MPV;
	}
	else if (strcmp(subsession->codecName(), "MP2T") == 0)
	{
		codec = MEDIACODEC_MP2T;
	}
#if 0	
	else if (strcmp(subsession->codecName(), "H263-1998") == 0)
	{
		codec = MEDIACODEC_H263_1998;
	}
	else if (strcmp(subsession->codecName(), "H263-2000") == 0)
	{
		codec = MEDIACODEC_H263_2000;
	}
#endif
	else if (strcmp(subsession->codecName(), "H264") == 0)
	{
		codec = MEDIACODEC_H264;
	}
#if 1	
	else if (strcmp(subsession->codecName(), "JPEG") == 0)
	{
		codec = MEDIACODEC_JPEG;
	}
#endif
	else if (strcmp(subsession->codecName(), "PCMU") == 0)
	{
		codec = MEDIACODEC_PCMU;
	}
	else if (strcmp(subsession->codecName(), "DVI4") == 0)
	{
		codec = MEDIACODEC_DVI4;
	}
	else if (strcmp(subsession->codecName(), "PCMA") == 0)
	{
		codec = MEDIACODEC_PCMA;
	}
	else if (strcmp(subsession->codecName(), "MP1S") == 0)
	{
		codec = MEDIACODEC_MP1S;
	}
	else if (strcmp(subsession->codecName(), "MP2P") == 0)
	{
		codec = MEDIACODEC_MP2P;
	}
	else
	{
		return ;
	}

	if (type == mediatype_audio)
	{
		stMediaInfo.type += mediatype_audio;
		stMediaInfo.audioCodec = codec;
	}
	else if (type == mediatype_video)
	{
		stMediaInfo.type += mediatype_video;
		stMediaInfo.videoCodec = codec;
	}
	else if (type == mediatype_av)
	{
		stMediaInfo.type += mediatype_av;
		stMediaInfo.avCodec = codec;
	}
	
}

bool MtkRTSPClient::getMediaInfo(MediaInfo & media)
{
	mediaInfoWaiter.syncBegin();
	while (bMediaInfoReady == false)
	{
		if (bResponseErr == true)
		{
			mediaInfoWaiter.syncEnd();
			return false;
		}
		
		LOG_DEBUG("get media info wait");
		mediaInfoWaiter.timedWait(30000);
		LOG_DEBUG("get media info wake up:%d", bMediaInfoReady);
		if ((bMediaInfoReady == false) || (bResponseErr == true))
		{
			mediaInfoWaiter.syncEnd();
			return false;
		}
	}
	media = stMediaInfo;
	mediaInfoWaiter.syncEnd();
	return true;
}

void MtkRTSPClient::destroy()
{
    ScopedMutex sm(clientlocker);

    if (session)
    {
        MediaSubsessionIterator iter(*(session));
        MediaSubsession* subsession;
        while ((subsession = iter.next()) != NULL) 
        {
             Medium::close(subsession->sink);
             subsession->sink = NULL;
        }

        Medium::close(session);
		session = NULL;
    }

    if (sender != NULL)
	{
		delete sender;
		sender = NULL;
	}

	if (auth != NULL)
	{
		delete auth;
		auth = NULL;
	}
    
    status = CLIENTSTATUS_DESTROY;
    bClientAlive = false;
    return ;
}

bool MtkRTSPClient::IsPacketArrived()
{
    MediaSubsessionIterator iter(*session);
    MediaSubsession* subsession;
    while ((subsession = iter.next()) != NULL) 
    {
        RTPSource* src = subsession->rtpSource();
        if (src == NULL) 
            continue;

        if (src->receptionStatsDB().numActiveSourcesSinceLastReset() > 0) 
        {
            return true;
        }
    }

    return false;
}

}


