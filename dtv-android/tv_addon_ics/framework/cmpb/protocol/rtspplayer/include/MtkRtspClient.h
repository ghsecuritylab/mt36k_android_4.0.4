#ifndef _MTK_RTSP_CLIENT_H_
#define _MTK_RTSP_CLIENT_H_
#include "RTSPClient.hh"
#include "NetAddress.hh"
#include "Mutex.h"
#include "RtspCmdSender.h"
#include "Semaphore.h"
#include "PushPlayer.h"

class UsageEnvironment;
class Authenticator;
class MediaSession;
class MediaSubsession;

namespace rtsp
{

typedef enum
{
	CLIENTSTATUS_UNINIT = 0,
	CLIENTSTATUS_CONNECTED,
	CLIENTSTATUS_PLAYED,
	CLIENTSTATUS_PAUSED,
	CLIENTSTATUS_STOPPED,
	CLIENTSTATUS_DESTROY
}ClientStatus;	

class MtkRTSPClient:public RTSPClient
{
	public: 
		static void * doevent_thread(void *pv_data);
		
		static MtkRTSPClient* createNew(char const* rtspURL, 
								dealResultHandler *handler = SenderhandResponse, 
									char const* username = NULL, char const* pwd = NULL);
				
		virtual MediaSession * const getMediaSession(){return session;}

		virtual bool resume();
		virtual bool connect();
		virtual bool play();
		virtual bool pause();
		virtual bool stop();
		virtual bool timeseek(unsigned int uiTime = 0);//in seconds
		virtual void watcherBegin();
		virtual void watcherEnd();

		bool IsPacketArrived();

		virtual ~MtkRTSPClient();

		ClientStatus getStatus(){return status;}

		bool getMediaInfo(MediaInfo & media);
		void mediaInfoReady();
		void setCurrentSender(CmdSenderDecorator *const _pRtspReqSender){pRtspReqSender = _pRtspReqSender;}
        bool IsSeekable();
	protected:
		CmdSenderDecorator *pRtspReqSender;
		Authenticator * auth;
		MediaSession * session;
		SenderInit *sender;
		Semaphore mediaInfoWaiter;

		ClientStatus status;
		MediaInfo stMediaInfo;
		
		char cIsCmdSendEnd;
		double fStartTime;
		double fEndTime;
		bool bMediaInfoReady;
		bool bResponseErr;
		
		dealResultHandler * respHandler;				
  	protected:
  		explicit MtkRTSPClient(dealResultHandler *handler,  
								char const* rtspURL, int verbosityLevel, 
									char const* applicationName, portNumBits tunnelOverHTTPPortNum);

		void setMediaInfo(MediaSubsession *subsession, unsigned int type);
		virtual void wakeupWaiters();
		static void SenderhandResponse(RTSPClient* rtspClient, int resultCode, char* resultString);
		void SenderhandResponse1(int resultCode, char* resultString);
		bool handDescription(char* resultString);
		bool handSetup(char* resultString);
		unsigned int getBufType(MediaSubsession * subsession);
		
	private:
		void destroy();
};

}

#endif

