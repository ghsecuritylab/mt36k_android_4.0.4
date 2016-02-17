#ifndef WFD_RTSP_PROTOCOL
#define WFD_RTSP_PROTOCOL

#include "Semaphore.h"
#include "Mutex.h"
#include "PushPlayer.h"
#include <string>
#include <vector>

namespace rtsp
{

/**
 *   This class now only support for analyzing wfd_xxx .
 */
class WfdRtspProtocol
{
public:
    static WfdRtspProtocol& instance();
    int parse(const std::string& prtclStr);
    int getResult(std::string& result);

protected:    
    WfdRtspProtocol();
    ~WfdRtspProtocol();

private:
    int parseToMeta(const std::string& str, const std::string& token, std::vector<std::string>& result);
    int handleWfdAudioCodecs(const std::string& str);
    int handleWfdVideoFormats(const std::string& str);
    int handle3DFormats(const std::string& str);
    int handleContentProtection(const std::string& str);
    int handleDisplayEdid(const std::string& str);
    int handleCoupledSink(const std::string& str);
    int handleClientRtpPorts(const std::string& str);
    int handlePresentationUrl(const std::string& str);
    int handleTriggerMethod(const std::string& str);

private:
    enum SEGMENTS
    {
        WFD_AUDIO_CODECS        = 0,
        WFD_VIDEO_FORMATS,
        WFD_3D_FORMATS,
        WFD_CONTENT_PROTECTION,
        WFD_DISPLAY_EDID,
        WFD_COUPLED_SINK,
        WFD_CLIENT_RTP_PORTS,
        WFD_PRESENTATION_URL,
        WFD_TRIGGER_METHOD,
        WFD_SEGMENT_MAX,
    };

    enum State
    {
        WFD_GET_PARAMETER   = 0,
        WFD_SET_PARAMETER,
        UNKNOWN,
    } mState;
    
    static const char* keys[WFD_SEGMENT_MAX];
        
    std::string mWfdAudioCodecs;
    std::string mWfdVideoFormats;
    std::string mWfd3DFormats;
    std::string mWfdContentProtection;
    std::string mWfdDisplayEdid;
    std::string mWfdCoupledSink;
    std::string mWfdClientRtpPorts;

    Mutex       mLocker;
    std::string mResult;

    MediaInfo*  mMediaInfo;
    
};


}

#endif
