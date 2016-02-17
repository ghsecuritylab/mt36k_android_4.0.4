#include "WfdRtspPlayerImpl.h"
#include <assert.h>
#include "WfdRtspPlayer.h"
#include "WfdRtspClient.h"
#include "PushPlayer.h"
#include "myLog.h"
#ifdef WFD_DEMO_SAVE_FILE
#include "myCfg.h"
#endif
using namespace std;
namespace rtsp
{
WfdRtspPlayerImpl::WfdRtspPlayerImpl()
{
    mClient = NULL;
    mState = WfdRtspPlayer::CLOSED;
#ifdef WFD_DEMO_SAVE_FILE   
    setPushThreadFlag(false);
    setTcpFlag(false);
    setLog(true);
    saveCmpbPlay(false);
    saveLocalFile(true);
#endif     
}

WfdRtspPlayerImpl::~WfdRtspPlayerImpl()
{
    if (mClient)
        delete mClient;
}

int WfdRtspPlayerImpl::open(const std::string &url)
{
    printf("WfdRtspPlayerImpl::open url = %s \n", url.c_str());
    int ret = -1;

    if(mState != WfdRtspPlayer::CLOSED)
    {
        printf("WfdRtspPlayerImpl::open bad state %d \n", mState);
        return -1;
    }
    
    if(url.find("rtsp://") == std::string::npos)
    {
        printf("Error WfdRtspPlayerImpl::open url is not correct \n");
        return -1;
    }

    string ip = url.substr(strlen("rtsp://"));
    int index = url.find_last_of(":");
    string port = url.substr(index+1);  
    string newUrl;
    if(port == string("0"))
    {
        LOG_DEBUG("WfdRtspPlayerImpl::open default port set to 554 \n");
        newUrl = string("rtsp://") + ip + string(":") + string("554");
        LOG_DEBUG("WfdRtspPlayerImpl::open new url is %s \n", url.c_str());
        mClient = WfdRtspClient::createNew(newUrl.c_str());
    }
    else
    {
        mClient = WfdRtspClient::createNew(url.c_str());
    }
    assert(mClient != NULL);

    ret = mClient->connect();
    if(ret)
    {
        printf("Error WfdRtspPlayerImpl::open client connect failed, ret = %d \n", ret);      
        return -2;
    }
#ifndef WFD_DEMO_SAVE_FILE
    MediaInfo media;
    ret = mClient->getMediaInfo(media);
    if(ret)
    {
        printf("Error WfdRtspPlayerImpl::open get media info failed, ret = %d \n", ret);
        return -3;
    }

    if (false == PushPlayer::instance().SetMediaInfo(media))
	{
		printf("Error WfdRtspPlayerImpl::open set media info failed \n");
        return -4;
	}

    if (false == PushPlayer::instance().open())
	{
		printf("Error WfdRtspPlayerImpl::open PushPlayer open failed \n");
        return -5;
	}
#endif

    return 0;
}

int WfdRtspPlayerImpl::play()
{
    printf("WfdRtspPlayerImpl::play \n");
    int ret = 0;
#ifndef WFD_DEMO_SAVE_FILE
    if (false == PushPlayer::instance().play())
	{
		printf("Error WfdRtspPlayerImpl::open PushPlayer play failed \n");
        return -1;
	}
#endif
    ret = mClient->play();
    if(ret)
    {
        printf("Error WfdRtspPlayerImpl::play client play faled, ret = %d \n", ret);
        return -2;
    }
    
    return 0;
}

int WfdRtspPlayerImpl::pause()
{
    printf("WfdRtspPlayerImpl::pause \n");
    int ret = 0;

    ret = mClient->pause();
    if(ret)
    {
        printf("Error WfdRtspPlayerImpl::play client pause faled, ret = %d \n", ret);
        return -2;
    }
#ifndef WFD_DEMO_SAVE_FILE
    if (false == PushPlayer::instance().pause())
	{
		printf("Error WfdRtspPlayerImpl::open PushPlayer pause failed \n");
        return -1;
	}
#endif
    return 0;
}

int WfdRtspPlayerImpl::unPause()
{
    printf("WfdRtspPlayerImpl::unPause \n");
    int ret = 0;
#ifndef WFD_DEMO_SAVE_FILE    
    if (false == PushPlayer::instance().resume())
	{
		printf("Error WfdRtspPlayerImpl::open PushPlayer unPause failed \n");
        return -1;
	}
#endif
    ret = mClient->unPause();
    if(ret)
    {
        printf("Error WfdRtspPlayerImpl::play client unPause faled, ret = %d \n", ret);
        return -2;
    }

    return 0;
}

int WfdRtspPlayerImpl::stop()
{
    printf("WfdRtspPlayerImpl::stop \n");
    int ret = 0;
    ret = mClient->disconnect();
    if(ret)
    {
        printf("Error WfdRtspPlayerImpl::stop client disconnect failed, ret = %d \n", ret);
        return -1;
    }
  
#ifndef WFD_DEMO_SAVE_FILE
    if (false == PushPlayer::instance().stop())
	{
		printf("Error WfdRtspPlayerImpl::stop PushPlayer stop failed \n");
        return -2;
	}
#endif    
    return 0;
}

int WfdRtspPlayerImpl::close()
{
    printf("WfdRtspPlayerImpl::close \n");
    
    return 0;
}

WfdRtspPlayer::State WfdRtspPlayerImpl::state() const
{
    return mState;
}

}
