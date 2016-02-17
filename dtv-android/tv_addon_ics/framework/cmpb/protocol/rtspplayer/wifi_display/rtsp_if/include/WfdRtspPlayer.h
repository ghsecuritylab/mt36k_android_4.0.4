#ifndef WFD_RTSP_PLAYER_H
#define WFD_RTSP_PLAYER_H

#include <string>
#include <memory>

namespace rtsp
{
class WfdRtspPlayerImpl;
class WfdRtspPlayer
{
public:
    enum State
    {
        OPENING     = 0 ,
        OPENED          ,
        PLAYING         ,
        PAUSED          ,
        STOPPED         ,
        CLOSED          ,
    };

    WfdRtspPlayer();
    ~WfdRtspPlayer();
    int open(const std::string &url);   // url example: rtsp://172.26.31.66:3030
    int play();
    int pause();
    int unPause();
    int stop();
    int close();
    State state() const;

public:
    std::auto_ptr<WfdRtspPlayerImpl> mImpl;
};


}
#endif
