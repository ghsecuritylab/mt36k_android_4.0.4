#ifndef WFD_RTSP_CLIENT_H
#define WFD_RTSP_CLIENT_H

#include <string>
#include "RTSPClient.hh"
#include "Semaphore.h"
#include "Mutex.h"
#include "PushPlayer.h"

class UsageEnvironment;
class Authenticator;
class MediaSession;
class MediaSubsession;

namespace rtsp
{
class WfdRtspSenderInit;
class WfdRtspClient : public RTSPClient
{
public:
    static WfdRtspClient* createNew(char const* rtspURL, 
                        responseHandler* handler = wfdRtspResponseHandler, 
                        char const* username = NULL, char const* pwd = NULL);
    static void wfdRtspResponseHandler(RTSPClient* rtspClient, 
                        int resultCode, char* resultString);    
    static void subsessionAfterPlaying(void* clientData);
    int connect();
    int play();
    int pause();
    int unPause();
    int disconnect();
    int getMediaInfo(MediaInfo& media);
	~WfdRtspClient();
private:
    explicit WfdRtspClient(responseHandler* handler,  
                        char const* rtspURL, int verbosityLevel, 
						char const* applicationName, portNumBits tunnelOverHTTPPortNum);
    
    int waitForOPTIONS();
    int waitForSETUPFinished();
	int waitForTeardownFinished();
    int buildTcpConnection();
    int startEventLoop();
    int initSender();
    int replyOPTIONS(int resultCode, const char* resultString);
    int replyGET_PARAMETER(int resultCode, const char* resultString);
    int replySET_PARAMETER(int resultCode, const char* resultString);
	int replySETUP(int resultCode, const char* resultString);
    int sendOPTIONS();
    int sendSETUP();
    int sendPLAY();
	int sendPAUSE();
    int sendTEARDOWN();
    int parseResponseString(const char* respStr);

private:
    enum WfdState
    {
        SRC2SNK_OPTIONS             = 0,
        SNK2SRC_REPLY_OPTIONS,
        SNK2SRC_OPTIONS,
        SRC2SNK_REPLY_OPTIONS,
        SRC2SNK_GET_PARAMETER,
        SRC2SNK_SET_PARAMETER,
        SNK2SRC_REPLY_GET_PARAMETER,
        SNK2SRC_REPLY_SET_PARAMETER,
        SNK2SRC_SETUP,
        SRC2SNK_REPLY_SETUP,
        SNK2SRC_PLAY,
        SRC2SNK_REPLY_PLAY,
        SNK2SRC_PAUSE,
        SRC2SNK_REPLY_PAUSE,
        SNK2SRC_TEARDOWN,
        SRC2SNK_REPLY_TEARDOWN,
        UNKNOWN,
    } mState;
    Authenticator*      mAuth;
    MediaSession*       mSession;
    Semaphore           mSema;
    responseHandler*    mWfdRtspResponseHandler;  
    std::string         mUrl;
    std::string         mIp;
    Mutex               mLocker;
    WfdRtspSenderInit*  mSenderInit;
    MediaInfo*          mMediaInfo;

private:
	void updateState(WfdState next = UNKNOWN);
	void replyReq(int iErrcode = 0);
};

}

#endif
