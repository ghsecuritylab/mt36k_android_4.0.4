#ifndef WFD_RTSP_PLAYER_IMPL_H
#define WFD_RTSP_PLAYER_IMPL_H

#include "WfdRtspPlayer.h"

namespace rtsp
{
class WfdRtspClient;
class WfdRtspPlayerImpl
{
public:
    WfdRtspPlayerImpl();
    ~WfdRtspPlayerImpl();
    int open(const std::string &url);
    int play();
    int pause();
    int unPause();
    int stop();
    int close();
    WfdRtspPlayer::State state() const;

private:
    volatile WfdRtspPlayer::State   mState;
    WfdRtspClient*                  mClient;
};

}

#endif
