#include "WfdRtspProxy.h"
#include <stdio.h>
#include <assert.h>
#include <string>
#include "WfdRtspPlayer.h"
#include "WFDDevice.h"

using namespace std;
namespace rtsp
{
WfdRtspProxy::WfdRtspProxy()
{
    mPlayer = NULL;
    mPlayerProxy = NULL;
}

WfdRtspProxy::~WfdRtspProxy()
{
}

bool WfdRtspProxy::start(NetDevice * dev, WFDPlayerProxy * playerProxy)
{
    fprintf(stderr, "WfdRtspProxy::start \n");
    assert(dev && playerProxy);
    int ret = 0;
    
    if(mPlayer)
    {
        fprintf(stderr, "Warning WfdRtspProxy::start already start \n");
        delete mPlayer;
    }
        
    if(mPlayerProxy)
    {
        fprintf(stderr, "Warning WfdRtspProxy::start playerProxy already start \n");
        //return false;
    }
    
    mPlayer = new WfdRtspPlayer();
    mPlayerProxy = playerProxy;
    
    char tmp[6] = { 0 };
    sprintf(tmp, "%d", dev->getPort());
    string port = tmp;
    string ip = dev->getIp();
    string url = string("rtsp://") + ip + string(":") + port;

    ret = mPlayer->open(url);
    if(ret)
    {
        fprintf(stderr, "WfdRtspProxy::start connection failed \n");
        return false;
    }

    return true;
}

bool WfdRtspProxy::play()
{
    fprintf(stderr, "WfdRtspProxy::play \n");
    assert(mPlayer);
    int ret = 0;
    
    ret = mPlayer->play();
    if(ret)
    {
        fprintf(stderr, "WfdRtspProxy::play play failed \n");
        return false;
    }
    mPlayerProxy->notifyToPlay();

    return true;
}

bool WfdRtspProxy::pause()
{
    fprintf(stderr, "WfdRtspProxy::pause \n");
    assert(mPlayer);
    int ret = 0;
    
    ret = mPlayer->pause();
    if(ret)
    {
        fprintf(stderr, "WfdRtspProxy::pause pause failed \n");
        return false;
    }
    mPlayerProxy->notifyToPause();

    return true;
}

bool WfdRtspProxy::stop()
{
    fprintf(stderr, "WfdRtspProxy::stop \n");
    assert(mPlayer);
    int ret = 0;
    
    ret = mPlayer->stop();
    if(ret)
    {
        fprintf(stderr, "WfdRtspProxy::stop stop failed \n");
        return false;
    }
    ret = mPlayer->close();
    if(ret)
    {
        fprintf(stderr, "WfdRtspProxy::stop close failed \n");
        return false;
    }
    mPlayerProxy->notifyToStop();

    return true;
}
}
