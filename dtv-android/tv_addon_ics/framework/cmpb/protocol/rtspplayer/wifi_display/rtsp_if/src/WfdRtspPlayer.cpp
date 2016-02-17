#include "WfdRtspPlayer.h"
#include "WfdRtspPlayerImpl.h"

namespace rtsp
{

WfdRtspPlayer::WfdRtspPlayer():mImpl(new WfdRtspPlayerImpl)
{
}

WfdRtspPlayer::~WfdRtspPlayer()
{
}

int WfdRtspPlayer::open(const std::string &url)
{
    return mImpl->open(url);
}

int WfdRtspPlayer::play()
{
    return mImpl->play();
}

int WfdRtspPlayer::pause()
{
    return mImpl->pause();
}

int WfdRtspPlayer::unPause()
{
    return mImpl->unPause();
}

int WfdRtspPlayer::stop()
{
    return mImpl->stop();
}

int WfdRtspPlayer::close()
{
    return mImpl->close();
}

WfdRtspPlayer::State WfdRtspPlayer::state() const
{
    return mImpl->state();
}

}
