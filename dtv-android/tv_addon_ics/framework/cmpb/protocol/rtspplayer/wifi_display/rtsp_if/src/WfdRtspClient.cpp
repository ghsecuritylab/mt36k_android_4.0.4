#include "WfdRtspClient.h"
#include <assert.h>
#include <stdio.h>
#include "BasicUsageEnvironment.hh"
#include "GroupsockHelper.hh"
#include "ThreadObject.h"
#include "Semaphore.h"
#include "ScopedMutex.h"
#include "WfdRtspCmdSender.h"
#include "WfdRtspProtocol.h"
#include "CmpbSink.h"
#include "RtspRingBuffer.h"
//#include "PushPlayer.h"
#include "myLog.h"

#define RTSP_PARAM_STRING_MAX 200

using namespace std;
namespace rtsp
{
static TaskScheduler* scheduler = NULL;
static UsageEnvironment* env = NULL;
static char exitEventLoop = 1;
static unsigned int fileSinkBufferSize = 100000;
const unsigned int maxBufSize = 1024*1024;
 
void* eventLoopThread(void* data)
{
    LOG_DEBUG("eventLoopThread start...");
	assert(data);

	WfdRtspClient* client = static_cast<WfdRtspClient*>(data);

	while(!exitEventLoop)
	{
	    env->taskScheduler().doEventLoop(&exitEventLoop); 
	}
	
	LOG_ERR("eventLoopThread exit...");
	return NULL;
}
static ThreadObject watcher(eventLoopThread);

void WfdRtspClient::wfdRtspResponseHandler(RTSPClient * rtspClient,int resultCode,char * resultString)
{
    assert(rtspClient);
    WfdRtspClient* client = dynamic_cast<WfdRtspClient*>(rtspClient);
    WfdState state = client->mState;
    LOG_DEBUG("WfdRtspClient::wfdRtspResponseHandler state = %d, resultCode = %d", state, resultCode);

  
    if ((resultString == NULL) 
        || ((resultString != NULL ) && (memcmp(resultString, "RTSP/", 5) == 0)))
    {
        /*it's a wfd source reply*/
        if (resultCode != 0)
        {
            /*handle the error*/
            LOG_ERR("wifi-display source reply error:%s", resultString);
        }
        else
        {         
            if (state == SNK2SRC_SETUP)
            { 
                LOG_DEBUG("source reply sink's setup request ok");
                client->replySETUP(resultCode, resultString);
                client->mSema.notifyAll();                  // build SETUP success
                LOG_DEBUG("sink send play request to source");
                client->play();
                return;
            }
            else if (state == SNK2SRC_OPTIONS)
            {
                LOG_ERR("-------Received M2 Response-------\n");
                LOG_DEBUG("source reply sink's options request ok");
            }
            else if (state == SNK2SRC_PLAY)
            {
                LOG_ERR("-------Received M7 Response-------\n");
                LOG_DEBUG("source reply sink's play request ok");
            }
            else if (state == SNK2SRC_PAUSE)
            {
                LOG_ERR("-------Received M10 Response-------\n");
                LOG_DEBUG("source reply sink's pause request ok");
            }
            else if (state == SNK2SRC_TEARDOWN)
            {
                LOG_ERR("-------Received M9 Response-------\n");
                LOG_DEBUG("source reply sink's teardown request ok");
                client->mSema.notifyAll();  
            }
        }
        
        client->updateState();
        return;
    }
   
    string request(resultString);
    if(request.find("OPTIONS") != string::npos)
    {
        LOG_DEBUG("sink receive source's options request");
        LOG_ERR("-------Received M1 Request-------\n");
        client->mSema.notifyAll();                  // connection Success
        client->replyOPTIONS(resultCode, resultString);
        LOG_DEBUG("sink reply source's options request ok");
        LOG_DEBUG("sink send options request to source");
        client->sendOPTIONS();
    }
    else if(request.find("GET_PARAMETER") != string::npos)
    {
        LOG_DEBUG("sink receive source's get_parameter request");
        LOG_ERR("-------Received M3 Request-------\n");
        client->updateState(SRC2SNK_GET_PARAMETER);
        client->replyGET_PARAMETER(resultCode, resultString);
        LOG_DEBUG("sink reply source's get_parameter request ok");
    }
    else if(request.find("SET_PARAMETER") != string::npos)
    {
        LOG_DEBUG("sink receive source's set_parameter request");
        client->updateState(SRC2SNK_SET_PARAMETER);
        client->replySET_PARAMETER(resultCode, resultString);
        LOG_DEBUG("WfdRtspClient::wfdRtspResponseHandler source SET_PARAMETER reply \n");
    }
    else
    {
        LOG_ERR("not support:%s", resultString);
    }
}

void WfdRtspClient::subsessionAfterPlaying(void * clientData)
{
    LOG_DEBUG("WfdRtspClient::subsessionAfterPlaying \n");
    // closing this media subsession's stream:
	MediaSubsession* subsession = (MediaSubsession*)clientData;
	Medium::close(subsession->sink);
	subsession->sink = NULL;
}

WfdRtspClient* WfdRtspClient::createNew(char const* rtspURL, responseHandler *handler,
                                    char const* username, char const* pwd)
{
    LOG_DEBUG("WfdRtspClient::createNew \n");
    int ret = 0;
    if(scheduler == NULL)
    {
        scheduler = BasicTaskScheduler::createNew();
    }
    assert(scheduler != NULL);
    if(env == NULL)
	{
		env = BasicUsageEnvironment::createNew(*scheduler);
	}
	assert(env != NULL);
	WfdRtspClient* client = new WfdRtspClient(handler, rtspURL, 0, "wfd.bin", 0);
	assert(client != NULL);

	if((username != NULL ) || (pwd != NULL))
	{
		Authenticator * auth = new Authenticator(username, pwd);
		client->mAuth= auth;
	}

	ret = client->startEventLoop();
	if(ret)
	{
	    LOG_ERR("Error WfdRtspClient::createNew startEventLoop failed, ret = %d \n", ret);
	    return NULL;
	}

	return client;
}

WfdRtspClient::WfdRtspClient(responseHandler * handler, char const * rtspURL,
        int verbosityLevel,char const * applicationName, 
        portNumBits tunnelOverHTTPPortNum):
        RTSPClient(*env, rtspURL, verbosityLevel, applicationName, tunnelOverHTTPPortNum),
        mWfdRtspResponseHandler(handler)
{
    mUrl = string(rtspURL);
    mIp = mUrl.substr(strlen("rtsp://"));
    mSenderInit = NULL;
    mState = UNKNOWN;
    mSession = NULL;
    mAuth = NULL;
    mMediaInfo = NULL;
}

WfdRtspClient::~WfdRtspClient()
{
    exitEventLoop = 1;
    disconnect(); 
}

int WfdRtspClient::connect()
{
    LOG_DEBUG("WfdRtspClient::connect \n");
    ScopedMutex sm(mLocker);
    int ret = 0;

    initSender();
    registerWfdCallback(wfdRtspResponseHandler);
    ret = buildTcpConnection();
    if(ret)
    {
        LOG_ERR("Error WfdRtspClient::connect buildTcpConnection failed, ret = %d \n", ret);
        return -1;
    }
    
    ret = waitForOPTIONS();
    if(ret)
    {
        LOG_ERR("Error WfdRtspClient::connect waitForOPTIONS failed, ret = %d \n", ret);
        return -2;
    }
    else
    {
        LOG_DEBUG("WfdRtspClient::connect successfully \n");
    }

    ret = waitForSETUPFinished();
    if(ret)
    {
        LOG_ERR("Error WfdRtspClient::connect SETUP failed, ret = %d \n", ret);
        return -3;
    }
    else
    {
        LOG_DEBUG("WfdRtspClient::connect SETUP successfully \n");
    }
    
    return 0;
}

int WfdRtspClient::play()
{
    ScopedMutex sm(mLocker);
    if ((mState == SNK2SRC_PLAY) || (mState == SRC2SNK_REPLY_PLAY) || (mState == UNKNOWN))
    {
        LOG_DEBUG("current stat is %d", (int)mState);
        return 0;
    }

    LOG_DEBUG("begin send play request's data...");
    sendPlayCommand(*mSession, wfdRtspResponseHandler, 0, mSession->playEndTime(), 1.0f, mAuth);
    LOG_DEBUG("send play request's data done");

    LOG_ERR("-------Sending M7 Request-------\n");
    updateState(SNK2SRC_PLAY);
    return 0;
}

int WfdRtspClient::pause()
{
    ScopedMutex sm(mLocker);
    if ((mState == SNK2SRC_PAUSE) || (mState == SRC2SNK_REPLY_PAUSE) || (mState == UNKNOWN))
    {
        LOG_DEBUG("current stat is %d", (int)mState);
        return 0;
    }

    LOG_DEBUG("begin send pause request's data...");
    sendPauseCommand(*mSession, wfdRtspResponseHandler, mAuth);
    LOG_DEBUG("send pause request's data done");
    LOG_ERR("-------Sending M10 Request-------\n");
    
    updateState(SNK2SRC_PAUSE);
    return 0;
}

int WfdRtspClient::unPause()
{
    ScopedMutex sm(mLocker);
    if ((mState == SNK2SRC_PLAY) || (mState == SRC2SNK_REPLY_PLAY) || (mState == UNKNOWN))
    {
        LOG_DEBUG("current stat is %d", (int)mState);
        return 0;
    }

    LOG_DEBUG("begin send play request's data...");
    sendPlayCommand(*mSession, wfdRtspResponseHandler, 0, mSession->playEndTime(), 1.0f, mAuth);
    LOG_DEBUG("send play request's data done");

    updateState(SNK2SRC_PLAY);
    return 0;
}

int WfdRtspClient::disconnect()
{
    ScopedMutex sm(mLocker);

    if ((mState == SNK2SRC_TEARDOWN) || (mState == SRC2SNK_REPLY_TEARDOWN) || (mState == UNKNOWN))
    {
        LOG_DEBUG("current stat is %d", (int)mState);
        return 0;
    }

    LOG_DEBUG("begin send teardown request's data...");
    sendTeardownCommand(*mSession, wfdRtspResponseHandler, mAuth);
    LOG_DEBUG("send teardown request's data done");
    updateState(SNK2SRC_TEARDOWN);
    LOG_ERR("-------Sending M9 Request-------\n");

    int ret = waitForTeardownFinished();
    if(ret)
    {
        LOG_ERR("waitForTeardownFinished failed, ret = %d \n", ret);
        return -3;
    }
   
    return 0;
}

int WfdRtspClient::getMediaInfo(MediaInfo & media)
{
    if(!mMediaInfo)
    {
        LOG_ERR("Warning WfdRtspClient::getMediaInfo media is not ready");
        return -1;
    }

    media = *mMediaInfo;
    
    return 0;
}

//======================  Private Functions ======================

int WfdRtspClient::buildTcpConnection()
{
    LOG_DEBUG("WfdRtspClient::buildTcpConnection");
    int ret = 0;
    
    ret = openConnection();
    if(ret)
    {
        LOG_ERR("WfdRtspClient::buildTcpConnection tcp connect failed");
        return -1;
    }
    LOG_DEBUG("WfdRtspClient::buildTcpConnection tcp connect success");

    return 0;
}

int WfdRtspClient::waitForOPTIONS()
{
    LOG_DEBUG("WfdRtspClient::waitForOPTIONS");
    mState = SRC2SNK_OPTIONS;
    if(!mSema.timedWait(30000))
    {
        LOG_ERR("Error WfdRtspClient::waitForOPTIONS timeout!");
        return -1;
    }
    
    return 0;
}

int WfdRtspClient::waitForSETUPFinished()
{
    LOG_DEBUG("WfdRtspClient::waitForSETUPFinished");
    if(!mSema.timedWait(60000))
    {
        LOG_ERR("Error WfdRtspClient::waitForSETUPFinished timeout!");
        return -1;
    }

    return 0;
}

int WfdRtspClient::waitForTeardownFinished()
{
    LOG_DEBUG("WfdRtspClient::waitForTeardownFinished");
    if(!mSema.timedWait(30000))
    {
        LOG_ERR("Error WfdRtspClient::waitForTeardownFinished timeout!");
        return -1;
    }

    return 0;
}


int WfdRtspClient::startEventLoop()
{
    LOG_ERR("WfdRtspClient::startEventLoop");
    exitEventLoop = 0;
    watcher.start((void*)this);    
    
    return 0;
}

int WfdRtspClient::initSender()
{
    LOG_DEBUG("WfdRtspClient::initSender");
    if(!mSenderInit)
    {
        mSenderInit = new WfdRtspSenderInit(this, mAuth);
        assert(mSenderInit != NULL);
    }

    return 0;
}

int WfdRtspClient::replyOPTIONS(int resultCode, const char* resultString)
{
    LOG_DEBUG("begin send options reply's data...");
    /*
     *MODIFY BY KUN.CHEN, if it's a request from source, the resultCode is the cseq
     */
    fCSeq = resultCode;
    char tmpBuf[2 * RTSP_PARAM_STRING_MAX];
    snprintf((char*)tmpBuf, sizeof(tmpBuf),
             "RTSP/1.0 200 OK\r\nCSeq: %d\r\nPublic: org.wfa.wfd1.0, GET_PARAMETER, SET_PARAMETER\r\n\r\n", fCSeq);
    send(fOutputSocketNum, tmpBuf, strlen(tmpBuf), 0);
    LOG_DEBUG("send options reply's data done");
    LOG_ERR("-------Sending M1 Response-------\n");

    updateState();  
    return 0;
}

int WfdRtspClient::replyGET_PARAMETER(int resultCode, const char* resultString)
{
    LOG_DEBUG("begin send get_parameter reply's data...");
    WfdRtspProtocol& protocol = WfdRtspProtocol::instance();
    if(protocol.parse(resultString))
    {
        LOG_ERR("Error:protocol parse fail");
        return -1;
    }
    /*
     *MODIFY BY KUN.CHEN, if it's a request from source, the resultCode is the cseq
     */
    fCSeq = resultCode;
    
    string sendBuf, result;
    char num[10] = { 0 };
    protocol.getResult(result);
    string transStr = "wfd_client_rtp_ports: RTP/AVP/UDP;unicast 1010 0 mode=play\r\n\r\n";
    sprintf(num, "%d", result.size() + transStr.size());
    
    sendBuf = "RTSP/1.0 200 OK\r\nCSeq: " + string(1, fCSeq + 0x30) + "\r\nContent-length: " + string(num) 
                + string("\r\n") + string("Content-Type: text/parameters\r\n\r\n");
    sendBuf += result;
    sendBuf += transStr;

    LOG_DEBUG("send content:\n%s \n", sendBuf.c_str());
    send(fOutputSocketNum, sendBuf.c_str(), sendBuf.size(), 0);
    LOG_DEBUG("send get_parameter reply's data done");
    LOG_ERR("-------Sending M3 Response-------\n");
    updateState();
    return 0;
}

/**
 *	replySET_PARAMETER  -  reply source's SET_PARAMETER
 *
 *	Returns:	1 on Need more data, 0 on Succees, -1 on Error
 */
int WfdRtspClient::replySET_PARAMETER(int resultCode, const char* resultString)
{
    WfdRtspProtocol& protocol = WfdRtspProtocol::instance();
    if(protocol.parse(resultString))
    {
        printf("Error WfdRtspClient::replyGET_PARAMETER protocol parse fail \n");
        return -1;
    }

    /*
     *MODIFY BY KUN.CHEN, if it's a request from source, the resultCode is the cseq
     */
    fCSeq = resultCode;
    
    string tmp = resultString;
    string replyStr;
    if(tmp.find("wfd_trigger_method: SETUP") != string::npos)
    {
        LOG_ERR("-------Received M5 Request (trigger SETUP)-------\n");
        replyReq();
        LOG_ERR("-------Sending M5 Resonse-------\n");
        LOG_DEBUG("sink reply source's trigger setup request ok");
        LOG_DEBUG("sink send setup request to source");
        sendSETUP();
    }
    else if(tmp.find("wfd_trigger_method: PLAY") != string::npos)
    {
        LOG_ERR("-------Received M5 Request (trigger PLAY)-------\n");
        replyReq();
        LOG_ERR("-------Sending M5 Resonse-------\n");
        LOG_DEBUG("sink reply source's trigger play request ok");
        LOG_DEBUG("sink send play request to source");
        play();
    }
    else if(tmp.find("wfd_trigger_method: PAUSE") != string::npos)
    {
        LOG_ERR("-------Received M5 Request (trigger PAUSE)-------\n");
        replyReq();
        LOG_ERR("-------Sending M5 Resonse-------\n");
        LOG_DEBUG("sink reply source's trigger pause request ok");
        LOG_DEBUG("sink send pause request to source");
        pause();
    }
    else if(tmp.find("wfd_trigger_method: TEARDOWN") != string::npos)
    {
        LOG_ERR("-------Received M5 Request (trigger TEARDOWN)-------\n");
        replyReq();
        LOG_ERR("-------Sending M5 Resonse-------\n");
        LOG_DEBUG("sink reply source's trigger teardown request ok");
        LOG_DEBUG("sink send teardown request to source");
        disconnect();
    }
    else
    {
        LOG_ERR("-------Received M4 Request-------\n");
        replyReq();        
        LOG_ERR("-------Sending M4 Response-------\n");
        LOG_DEBUG("sink reply source's set_parameter request ok");
    }
    return 0;
}

int WfdRtspClient::replySETUP(int resultCode, const char* resultString)
{   
	bool bSuccess = false;
	
	// Then, setup the "RTPSource"s for the session:
	MediaSubsessionIterator iter(*mSession);
	MediaSubsession *subsession = NULL;
	while ((subsession = iter.next()) != NULL) 
	{					
		if (subsession->readSource() == NULL) 
		{
			fprintf(stderr, "%s:%d warning\n", __FUNCTION__, __LINE__);
			continue; // was not initiated
		}

		if (subsession->sink != NULL)/*already be set*/
		{
			continue;
		}

		//unsigned int type = getBufType(subsession);
		int type = mediatype_video;
		if (type == 0)
		{
			fprintf(stderr, "%s:%d error type=%d\n", __FUNCTION__, __LINE__, type);
			continue;
		}
		
		{
			//iSetupCount--;
			/*set mediay info*/
			//client->setMediaInfo(subsession, type);
		}

         RingBuffer *ringbuffer = new RingBuffer();
    	if (ringbuffer == NULL)
    	{
    		LOG_ERR("error");
    		continue;
    	}

    	if (false == ringbuffer->allocate(type))
    	{
    		LOG_ERR("error");
    		delete ringbuffer;
    		continue;
    	}
		
		CmpbSink *sink = NULL;
        sink = CmpbSink::createNew(*env, ringbuffer, fileSinkBufferSize);
		subsession->sink = sink;
		if (subsession->sink == NULL) 
		{
			fprintf(stderr, "%s:%d error\n", __FUNCTION__, __LINE__); 
		} 
		else 
		{				
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

    updateState();
	return bSuccess ;
}

int WfdRtspClient::sendOPTIONS()
{
    LOG_DEBUG("begin send options request's data...");
    char tmpBuf[2 * RTSP_PARAM_STRING_MAX];
    snprintf((char*)tmpBuf, sizeof(tmpBuf),
             "OPTIONS * RTSP/1.0\r\nCSeq: %d\r\nRequire: org.wfa.wfd1.0\r\nUser-Agent: LIVE555 Streaming Media v2011.03.14\r\n\r\n", ++fCSeq);
    send(fOutputSocketNum, tmpBuf, strlen(tmpBuf), 0);

    LOG_DEBUG("send options request's data done");
    LOG_ERR("-------Sending M2 Request-------\n");
    updateState();
    return 0;
}

int WfdRtspClient::sendSETUP()
{
    LOG_DEBUG("begin send setup request's data...");

    string version = "v=0\r\n";
    string owner = string("o=- 1312183067511609 1 IN IP4 ") + mIp + string("\r\n");
    string sessionName = "s=MPEG Transport Stream, streamed by the LIVE555 Media Server\r\n";
    string info = "i=wfd1.0\r\n";
    string time = "t=0 0\r\n";
    string control = string("a=control:") + string("rtsp://") + mIp.substr(0, mIp.find(':')) + string("/wfd1.0/streamid=0\r\n");
    string attr = "a=tool:LIVE555 Streaming Media v2011.06.14\r\n";
    attr += "a=type:broadcast\r\n";
    attr += control;
    attr += "a=range:npt=0-\r\n";
    attr += "a=x-qt-text-nam:MPEG Transport Stream, streamed by the LIVE555 Media Server\r\n";
    attr += "a=x-qt-text-inf:wfd1.0\r\n";
    string media = "m=video 1010 RTP/AVPF/UDP 33\r\n";
    string connection = "c=IN IP4 0.0.0.0\r\n";
    string bandwidth = "b=AS:5000\r\n";
    
    
    string uri = "\r\n";
    string email = "\r\n";
    string phone = "\r\n";
    string repeat = "\r\n";
    string zone = "\r\n";
    string key = "\r\n";
    
    string sdpDescription = version + owner + sessionName + info + time + 
                        attr + media + connection + control;
    LOG_DEBUG("sdpDescription:\n%s ", sdpDescription.c_str());
    mSession = MediaSession::createNew(*env, sdpDescription.c_str());
    assert(mSession);

    if(!mSession->hasSubsessions())
    {
        LOG_ERR("no media subsession ");
        Medium::close(mSession);
        mSession = NULL;
        return -1;
    }
    MediaSubsessionIterator iter(*mSession);
	MediaSubsession *subsession = NULL;

    while((subsession = iter.next()) != NULL)
    {
        /*
         *kun.chen this only work on one subsession
         */
        //subsession->setClientPortNum(1010);
        
        if(!subsession->initiate(-1))
		{
			LOG_ERR("subsession init failed");
			continue;
		}
        if(subsession->rtpSource() != NULL)
        {
            int socketNum = subsession->rtpSource()->RTPgs()->socketNum();
		 	unsigned newBufferSize = setReceiveBufferTo(*env, socketNum, maxBufSize);
        }
      
        sendSetupCommand(*subsession, wfdRtspResponseHandler, False, False, False, mAuth);
    }

    LOG_ERR("-------Sending M6 Request-------\n");
    LOG_DEBUG("send setup request's data done");
    updateState(SNK2SRC_SETUP);
    return 0;
}

int WfdRtspClient::sendPLAY()
{
    printf("WfdRtspClient::sendPLAY \n");
   
    if(mState != SRC2SNK_REPLY_SETUP)
    {
        printf("Error WfdRtspClient::sendPLAY bad state = %d \n", mState);
        return -1;
    }

    sendPlayCommand(*mSession, wfdRtspResponseHandler, 0, mSession->playEndTime(), 1.0f, mAuth);
    updateState(SNK2SRC_PLAY);
    return 0;
}

int WfdRtspClient::sendPAUSE()
{
    printf("WfdRtspClient::sendPAUSE \n");
   
    if(mState != SRC2SNK_REPLY_SETUP)
    {
        printf("Error WfdRtspClient::sendPLAY bad state = %d \n", mState);
        return -1;
    }

    sendPauseCommand(*mSession, wfdRtspResponseHandler, mAuth);
    mState = SNK2SRC_PLAY;
    return 0;
}


int WfdRtspClient::sendTEARDOWN()
{
    printf("WfdRtspClient::sendTEARDOWN \n");
    
    if(mState != SRC2SNK_REPLY_SETUP)
    {
        printf("Error WfdRtspClient::sendPLAY bad state = %d \n", mState);
        return -1;
    }

    sendTeardownCommand(*mSession, wfdRtspResponseHandler, mAuth);
    mState = SNK2SRC_TEARDOWN;
    return 0;
}

int WfdRtspClient::parseResponseString(const char* respStr)
{
    printf("WfdRtspClient::parseResponseString \n");

    return 0;
}

void WfdRtspClient::updateState(WfdState next)
{
    LOG_DEBUG("current state : %d", mState);
    
    if (next != UNKNOWN)
    {
        mState = next;
        LOG_DEBUG("next state : %d", mState);
        return;
    }
    switch(mState)
    {
        case SRC2SNK_OPTIONS:
            mState = SNK2SRC_REPLY_OPTIONS;
            break;
        case SNK2SRC_REPLY_OPTIONS:
            mState = SNK2SRC_OPTIONS;
            break;
        case SNK2SRC_OPTIONS:
            mState = SRC2SNK_REPLY_OPTIONS;
            break;
        case SRC2SNK_GET_PARAMETER:
            mState = SNK2SRC_REPLY_GET_PARAMETER;
            break;
        case SRC2SNK_SET_PARAMETER:
            mState = SNK2SRC_REPLY_SET_PARAMETER;
            break;
        case SNK2SRC_SETUP:
            mState = SRC2SNK_REPLY_SETUP;
            break;
        case SNK2SRC_PLAY:
            mState = SRC2SNK_REPLY_PLAY;
            break;
        case SNK2SRC_PAUSE:
            mState = SRC2SNK_REPLY_PAUSE;
            break;
        case SNK2SRC_TEARDOWN:
            mState = SRC2SNK_REPLY_TEARDOWN;
            break;
        default:
            LOG_ERR("This state(%d) needn't be change", mState);
            break;
    }
    LOG_DEBUG("next state : %d", mState);
}
    
void WfdRtspClient::replyReq(int iErrcode)
{
    static char replyStr[256];
    
    memset(replyStr, 0, sizeof(replyStr));
    
    if (iErrcode == 0)
    {
        //replyStr = "RTSP/1.0 200 OK\r\nCSeq: 5\r\nDate: Mon, Jul 11 2011 06:51:04 GMT\r\n\r\n";
        sprintf(replyStr, "RTSP/1.0 200 OK\r\nCSeq: %d\r\n\r\n", fCSeq);
        send(fOutputSocketNum, replyStr, strlen(replyStr), 0);
        updateState();
    }
    else if (iErrcode == 405)
    {
        sprintf(replyStr, "RTSP/1.0 405 Method Not Allowed\r\nCSeq: %d\r\n\r\n", fCSeq);
        send(fOutputSocketNum, replyStr, strlen(replyStr), 0);
        updateState();
    }
}

}
