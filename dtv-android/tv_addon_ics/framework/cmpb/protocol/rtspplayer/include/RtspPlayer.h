#ifndef _RTSP_PLAYER_H_
#define _RTSP_PLAYER_H_

#include "MtkRtspClient.h"
#include "Mutex.h"
#include "ThreadObject.h"
#include "Semaphore.h"

class TaskScheduler;
class UsageEnvironment;

namespace rtsp
{

typedef enum
{
	RTSPPLAYERSTATUS_PLAYED = 0,
	RTSPPLAYERSTATUS_PAUSED,
	RTSPPLAYERSTATUS_STOPPED
}RtspPlayerStatus;
	
class RtspPlayer
{
	friend void * doevent_thread(void *pv_data);
	friend void ReConnectUseTcp(void * arg);
	friend void DealNoDataCome(void * arg);
	public:
		~RtspPlayer();
		bool play(std::string & url, IMtkPb_Ctrl_Nfy_Fct fCmpbEventNfy = NULL, void* pvTag = NULL);
		bool pause(bool bBufferData = false);
		bool stop();
		bool timeseek(unsigned int uiTime = 0);
        bool bIsSeekable();
		static RtspPlayer * createNew();
		void delRtspClient();
		void setTcpRetry();
	protected:
		MtkRTSPClient * client;
		RtspPlayerStatus status;
		std::string rtspUrl;
		Semaphore doeventWaiter;
		bool bAlive;
		TaskScheduler* scheduler;
	 	UsageEnvironment * env;
		TaskToken tcpRetryTimerTask;
		TaskToken waitDataTask;
		IMtkPb_Ctrl_Nfy_Fct pCmpbEventNfy;
		void* pEventArg;
		Mutex locker;
		bool bTaskExecSuccess;
		unsigned uClientSeq;
	protected:
		RtspPlayer();		
};

}

#endif

