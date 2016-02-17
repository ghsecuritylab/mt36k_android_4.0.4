#include "RtspPlayer.h"
#include "PushPlayer.h"
#include "myLog.h"
#include "myCfg.h"
#include "BasicUsageEnvironment.hh"
namespace rtsp
{

static char cWaitTcpTryEnd = 0;
static char cWaitDataCome = 0;
static int iDataWaitCounter = 0;
static int iUdpDataWaitCounter = 0;

void * doevent_thread(void *pv_data)
{
	if(NULL == pv_data)
	{
		LOG_ERR("error!");
		return NULL;
	}

	LOG_DEBUG("do event loop thread start!");

	RtspPlayer *player = (RtspPlayer *)pv_data;        

    player->doeventWaiter.syncBegin();
    
	while(1)
	{
		if (player->client == NULL)
		{
			player->doeventWaiter.wait();
			if ((player->bAlive == false) ||
				(player->client == NULL))
			{
                LOG_DEBUG("do event loop thread exit1!");
				player->doeventWaiter.syncEnd();
				break;
			}
		}
		player->doeventWaiter.syncEnd();
		
		player->client->watcherBegin();
        
        player->doeventWaiter.syncBegin();
        unsigned seq = player->uClientSeq;
        while(player->client != NULL)/*wait for client be deleted*/
        {  
            player->doeventWaiter.notifyAll();
            player->doeventWaiter.syncEnd();
            player->doeventWaiter.syncBegin();
            if (player->bAlive == false)
            {
                player->doeventWaiter.syncEnd();
                LOG_DEBUG("do event loop thread exit2!");
                return NULL;
            }
            if (player->uClientSeq != seq)
            {
                break;
            }
        }
        
	}
	
	LOG_DEBUG("do event loop thread exit3!");
	return NULL;
}

static ThreadObject watcher(doevent_thread);

#define CHECK_FALSE_COND(condition, ret) \
	if (false == (condition)) \
	{ \
		LOG_ERR("error!"); \
		return (ret); \
	}
	
RtspPlayer::RtspPlayer():client(NULL), status(RTSPPLAYERSTATUS_STOPPED), bAlive(true),
                        tcpRetryTimerTask(NULL), pCmpbEventNfy(NULL), pEventArg(NULL), 
                        bTaskExecSuccess(true), uClientSeq(0)
{
    scheduler = BasicTaskScheduler::createNew();
	if (!scheduler)
	{
        LOG_ERR("error");
	}
	else
	{
		env = BasicUsageEnvironment::createNew(*scheduler);
	}	
    
	if (!env)
	{
        LOG_ERR("error");
	}
    
	watcher.start(this);
}

RtspPlayer::~RtspPlayer()
{
	doeventWaiter.syncBegin();
	bAlive = false;
	doeventWaiter.notifyAll();
	doeventWaiter.syncEnd();

    stop();

    watcher.stop();
    
    if (scheduler!= NULL)
    {
        delete scheduler;
        scheduler = NULL;
    }
    
    if (env!= NULL)
    {
        env->reclaim();
        env = NULL;
    }  
}

RtspPlayer * RtspPlayer::createNew()
{    
	RtspPlayer * player = new RtspPlayer();
	if (NULL == player)
	{
		LOG_ERR("error!");
	}

    rtsp::setTcpFlag(false);
    
	return player;
}

void RtspPlayer::delRtspClient()
{

    if (client)
    {
        client->stop();
        
        doeventWaiter.syncBegin();
        client->watcherEnd();
        doeventWaiter.wait();
	    Medium::close(client);
	    client = NULL;
        LOG_DEBUG("close rtsp client");
        doeventWaiter.syncEnd();
    }
	//
	
}

void ReConnectUseTcp(void * arg)
{
    LOG_DEBUG("enter ReConnectUseTcp");
    
    if (!arg)
    { 
        LOG_ERR("ReConnectUseTcp arg error");   
        return;
    }
    
    RtspPlayer * player = (RtspPlayer *)arg;

    if (player->client)
    {
        if (player->client->IsPacketArrived())
        {
            LOG_DEBUG("udp can received data");
            player->bTaskExecSuccess = true;
            iUdpDataWaitCounter = 0;
            cWaitTcpTryEnd = 1;
            player->tcpRetryTimerTask = NULL;/*doesn't need to delete*/
            return;
        }
    }

    if (iUdpDataWaitCounter < 10)
    {
        iUdpDataWaitCounter++;
        LOG_ERR("should continue wait");
        player->tcpRetryTimerTask = player->env->taskScheduler().scheduleDelayedTask(1000000, (TaskFunc *)ReConnectUseTcp, arg);
        return;
    }

    iUdpDataWaitCounter = 0;
    cWaitTcpTryEnd = 1;
    player->tcpRetryTimerTask = NULL;/*doesn't need to delete*/

    if (player->client != NULL)
	{
        player->delRtspClient();
    }

    PushPlayer::instance().stop();

	player->status = RTSPPLAYERSTATUS_STOPPED;

    do
    {
        player->doeventWaiter.syncBegin();
    	player->client = MtkRTSPClient::createNew(player->rtspUrl.c_str());
    	if (NULL == player->client)
    	{
            LOG_ERR("create rtsp client failed");
    		player->doeventWaiter.syncEnd();
    		break ;
    	}
    	player->uClientSeq++;
        LOG_ERR("client %d was created", player->uClientSeq);
    	player->doeventWaiter.notifyAll();
    	player->doeventWaiter.syncEnd();

        rtsp::setTcpFlag(true);
         
    	if (false == player->client->connect())
    	{
    		break;
    	}

    	MediaInfo media;
    	if (false == player->client->getMediaInfo(media))
    	{
    		break;
    	}

    	if (false == PushPlayer::instance().SetMediaInfo(media))
    	{
    		break;
    	}

    	if (false == PushPlayer::instance().open(player->pCmpbEventNfy, player->pEventArg))
    	{
    		break;
    	}

    	if (false == PushPlayer::instance().play())
    	{
    		break;
    	}

    	if (false == player->client->play())
    	{	
    		break;
    	}
            
    	LOG_DEBUG("player restarted!");
    	player->status = RTSPPLAYERSTATUS_PLAYED;
        player->bTaskExecSuccess = true;
        return;
    }while(0);

    LOG_ERR("error!");
    player->bTaskExecSuccess = false;
	return ;    
}

void DealNoDataCome(void * arg)
{
    LOG_DEBUG("enter DealNoDataCome");
    
    if (!arg)
    { 
        LOG_ERR("DealNoDataCome arg error");   
        return;
    }
    
    RtspPlayer * player = (RtspPlayer *)arg;

    player->waitDataTask = NULL;/*doesn't need to delete*/
    
    if (player->client)
    {
        if (player->client->IsPacketArrived())
        {
            LOG_DEBUG("can received data");
            player->bTaskExecSuccess = true;
        }
        else
        {
            if (iDataWaitCounter < 10)
            { 
                iDataWaitCounter++;
                LOG_ERR("should continue wait data");
                player->waitDataTask = player->env->taskScheduler().scheduleDelayedTask(1000000, (TaskFunc *)DealNoDataCome, arg);
                return;
            }

            player->bTaskExecSuccess = false;/*alse false because no data come*/
        }
    }

    iDataWaitCounter = 0;
    cWaitDataCome = 1;
    
	return ;    
}


void RtspPlayer::setTcpRetry()
{
    ScopedMutex sm(locker);

    if (!tcpRetryTimerTask)
    {
        tcpRetryTimerTask = env->taskScheduler().scheduleDelayedTask(1000000, (TaskFunc *)ReConnectUseTcp, this);
    }
    if (!tcpRetryTimerTask)
    {
        LOG_ERR("set scheduleDelayedTask failed");
    }
    else
    {
        LOG_DEBUG("set scheduleDelayedTask success");
    }
    
    cWaitTcpTryEnd = 0;
}


bool RtspPlayer::play(std::string & url, IMtkPb_Ctrl_Nfy_Fct fCmpbEventNfy, void* pvTag)
{
	ScopedMutex sm(locker);

    pCmpbEventNfy = fCmpbEventNfy;
    pEventArg = pvTag;

	bool ret = true;

	/*the same url & current status is paused*/
	if ((rtspUrl.compare(url) == 0) && (rtspUrl.length() != 0))
	{
		if (status == RTSPPLAYERSTATUS_PAUSED)
		{
			ret = client->resume();
			CHECK_FALSE_COND(ret, false);

			ret = PushPlayer::instance().resume();
			CHECK_FALSE_COND(ret, false);

			LOG_DEBUG("player resume!");
			status = RTSPPLAYERSTATUS_PLAYED;
			return true;
		}
		else if (status == RTSPPLAYERSTATUS_PLAYED)
		{
			return true;
		}
		else if (status == RTSPPLAYERSTATUS_STOPPED)
		{
			//do nothing, not return here
		}
		else
		{
			LOG_ERR("player current status:%d!", status);
			return false;
		}
	}

	/*different url or current type is stopped*/
	if (url.empty())
	{
		LOG_ERR("error!");
		return false;
	}

	if (client != NULL)
	{
		delRtspClient();

        PushPlayer::instance().stop();
        
		status = RTSPPLAYERSTATUS_STOPPED;
	}

    rtspUrl = url;
    fprintf(stderr, "play item url is:%s\n", rtspUrl.c_str());
    
	doeventWaiter.syncBegin();
	client = MtkRTSPClient::createNew(url.c_str());
	if (NULL == client)
	{
		LOG_ERR("error!");
		doeventWaiter.syncEnd();
		return false;
	}
    uClientSeq++;
	LOG_ERR("client %d was created", uClientSeq);
	doeventWaiter.notifyAll();
	doeventWaiter.syncEnd();

	do 
	{	
        rtsp::setTcpFlag(false);
        
		if (false == client->connect())
		{
			LOG_ERR("error!");
			break;
		}

		MediaInfo media;
		if (false == client->getMediaInfo(media))
		{
			LOG_ERR("error!");
			break;
		}

		if (false == PushPlayer::instance().SetMediaInfo(media))
		{
			LOG_ERR("not support!");
			break;
		}

		if (false == PushPlayer::instance().open(fCmpbEventNfy, pvTag))
		{
			LOG_ERR("error!");
			break;
		}

		if (false == PushPlayer::instance().play())
		{
			LOG_ERR("error!");
			break;
		}

		if (false == client->play())
		{
			LOG_ERR("error!");
			break;
		}

		LOG_DEBUG("player started!");
		
		if (tcpRetryTimerTask && env)
		{
            bTaskExecSuccess = true;
		    LOG_DEBUG("begin wait env task exec");
		    env->taskScheduler().doEventLoop(&cWaitTcpTryEnd);
		    LOG_DEBUG("end wait env task exec");

            if (!bTaskExecSuccess)
            {
                break;
            }
		}

        if (env)
        {
            waitDataTask = env->taskScheduler().scheduleDelayedTask(1000000, (TaskFunc *)DealNoDataCome, this);
            if (waitDataTask)
            {
                bTaskExecSuccess = true;
                cWaitDataCome = 0;
                LOG_DEBUG("begin wait data come task exec");
		        env->taskScheduler().doEventLoop(&cWaitDataCome);
		        LOG_DEBUG("end wait data come task exec");

                if (!bTaskExecSuccess)
                {
                    break;
                }
            }
        }
        
        status = RTSPPLAYERSTATUS_PLAYED;
		return true;
		
	}while(0);

    if (client)
    {
        delRtspClient();
    }
    
    PushPlayer::instance().stop();
	return false;
}

bool RtspPlayer::pause(bool bBufferData)
{
	ScopedMutex sm(locker);
	bool ret = true;

	if (status != RTSPPLAYERSTATUS_PLAYED)
	{
		LOG_ERR("player current status:%d!", status);
		return false;
	}
#if 0	
	if (false == bBufferData)
	{
		ret = client->pause();
		CHECK_FALSE_COND(ret, false);
	}
#else
    ret = client->pause();
    CHECK_FALSE_COND(ret, false);
#endif
	ret = PushPlayer::instance().pause();
	CHECK_FALSE_COND(ret, false);

	status = RTSPPLAYERSTATUS_PAUSED;
	return true;
}

bool RtspPlayer::stop()
{
    cWaitTcpTryEnd = 1;
    cWaitDataCome = 1;
    
	ScopedMutex sm(locker);

    if (tcpRetryTimerTask && env)
    {
        LOG_DEBUG("remove taskScheduler reconnect use tcp");
        env->taskScheduler().unscheduleDelayedTask(tcpRetryTimerTask);
        tcpRetryTimerTask = NULL;
        iUdpDataWaitCounter = 0;
    }

    if (waitDataTask && env)
    {
        LOG_DEBUG("remove taskScheduler waitDataTask");
        env->taskScheduler().unscheduleDelayedTask(waitDataTask);
        waitDataTask = NULL;
        iDataWaitCounter = 0;
    }
#if 0    
	if ((status != RTSPPLAYERSTATUS_PLAYED) &&
		(status != RTSPPLAYERSTATUS_PAUSED))
	{
		LOG_ERR("player current status:%d!", status);
		return false;
	}
#else
    LOG_DEBUG("current status is %d", status);
#endif
	LOG_DEBUG("rtsp player stop begin");

    if (client)
    {
    	delRtspClient();
    }
    PushPlayer::instance().stop();

    LOG_DEBUG("rtsp push player stop end");

	status = RTSPPLAYERSTATUS_STOPPED;
    
	return true;
}

bool RtspPlayer::timeseek(unsigned int uiTime)
{
	ScopedMutex sm(locker);
#if 0	
	bool ret = client->pause();
	CHECK_FALSE_COND(ret, false);
#endif

	bool ret = true;

	if ((status != RTSPPLAYERSTATUS_PLAYED) &&
		(status != RTSPPLAYERSTATUS_PAUSED))
	{
		LOG_ERR("player current status:%d!", status);
		return false;
	}
	
	if (client->IsSeekable() == false)
	{
		LOG_ERR("rtsp server not support seek!");
		return false;
	}
	ret = client->timeseek(uiTime);
	CHECK_FALSE_COND(ret, false);

	ret = PushPlayer::instance().timeseek();
	CHECK_FALSE_COND(ret, false);

	status = RTSPPLAYERSTATUS_PLAYED;
	return true;
}

bool RtspPlayer::bIsSeekable()
{
	ScopedMutex sm(locker);
	if (client)
		return client->IsSeekable();
	return false;
}

}
