#include "WfdRtspProtocol.h"
#include <stdio.h>
#include <string>
#include <vector>
#include <string.h>
#include "ScopedMutex.h"
#include "Mutex.h"

using namespace std;
namespace rtsp
{
Mutex  singletonLock;
const char* WfdRtspProtocol::keys[WfdRtspProtocol::WFD_SEGMENT_MAX] = 
{
    "wfd_audio_codecs",        "wfd_video_formats",    "wfd_3d_formats", 
    "wfd_content_protection",  "wfd_display_edid",     "wfd_coupled_sink", 
    "wfd_client_rtp_ports",    "wfd_presentation_URL", "wfd_trigger_method",
};

WfdRtspProtocol& WfdRtspProtocol::instance()
{
    ScopedMutex sm(singletonLock);
    static WfdRtspProtocol protocol;
    return protocol;
}

WfdRtspProtocol::WfdRtspProtocol()
{
    mState = UNKNOWN;
    mMediaInfo = new MediaInfo;
    memset(mMediaInfo, 0, sizeof(*mMediaInfo));
    mMediaInfo->type = mediatype_av;
}

WfdRtspProtocol::~WfdRtspProtocol()
{
}

int WfdRtspProtocol::parseToMeta(const string& str,const string & token, vector<string>& result)
{
    string segStr;
    string lastToken = "\r\n";
    int startPos = 0;
    int endPos = 0;

    while(startPos < str.size())
    {
        endPos = str.find_first_of(token, startPos);
        if(endPos == string::npos && token != lastToken)    // special treatment for the last segment
        {
            endPos = str.find_first_of(lastToken, startPos);
            segStr = str.substr(startPos, endPos - startPos);
            startPos = endPos + lastToken.size();
            if(segStr.size())       // prevent last segStr is null
            {
                result.push_back(segStr);
            }
            break;
        }
        segStr = str.substr(startPos, endPos - startPos);
        startPos = endPos + token.size();

        result.push_back(segStr);
    }

    return 0;
}


int WfdRtspProtocol::parse(const std::string& prtclStr)
{
    printf("WfdRtspProtocol::parse \n");
#if 0
    string token = "\r\n";
    string segStr;
    int startPos = 0;
    int endPos = 0;

    while(startPos < prtclStr.size())
    {
        endPos = prtclStr.find_first_of(token, startPos);
        segStr = prtclStr.substr(startPos, endPos - startPos);
        startPos = endPos + token.size();

        printf("endPos = %d, startPos = %d, size = %d, segStr = %s \n", 
                endPos, startPos, prtclStr.size(), segStr.c_str());
        for(int i = 0; i < WFD_SEGMENT_MAX; i++)
        {
            if(segStr.find(keys[i]) != string::npos)      // found key word
            {
                switch(i)
                {
                    case WFD_AUDIO_CODECS:
                        handleWfdAudioCodecs(segStr);
                        break;
                    case WFD_VIDEO_FORMATS:
                        handleWfdVideoFormats(segStr);
                        break;
                    case WFD_3D_FORMATS:
                        handle3DFormats(segStr);
                        break;
                    case WFD_CONTENT_PROTECTION:
                        handleContentProtection(segStr);
                        break;
                    case WFD_DISPLAY_EDID:
                        handleDisplayEdid(segStr);
                        break;
                    case WFD_COUPLED_SINK:
                        handleCoupledSink(segStr);
                        break;
                    case WFD_CLIENT_RTP_PORTS:
                        handleClientRtpPorts(segStr);
                        break;
                    case WFD_PRESENTATION_URL:
                        handlePresentationUrl(segStr);
                        break;
                    case WFD_TRIGGER_METHOD:
                        handleTriggerMethod(segStr);
                        break;
                }
                break;
            }
        }
    }
#endif

    vector<string> wfdSegs;
    parseToMeta(prtclStr, "\r\n", wfdSegs);
    
    for(vector<string>::iterator it = wfdSegs.begin(); it != wfdSegs.end(); it++)
    {
        for(int i = 0; i < WFD_SEGMENT_MAX; i++)
        {
            if((*it).find(keys[i]) != string::npos)      // found key word
            {
                switch(i)
                {
                    case WFD_AUDIO_CODECS:
                        handleWfdAudioCodecs(*it);
                        break;
                    case WFD_VIDEO_FORMATS:
                        handleWfdVideoFormats(*it);
                        break;
                    case WFD_3D_FORMATS:
                        handle3DFormats(*it);
                        break;
                    case WFD_CONTENT_PROTECTION:
                        handleContentProtection(*it);
                        break;
                    case WFD_DISPLAY_EDID:
                        handleDisplayEdid(*it);
                        break;
                    case WFD_COUPLED_SINK:
                        handleCoupledSink(*it);
                        break;
                    case WFD_CLIENT_RTP_PORTS:
                        handleClientRtpPorts(*it);
                        break;
                    case WFD_PRESENTATION_URL:
                        handlePresentationUrl(*it);
                        break;
                    case WFD_TRIGGER_METHOD:
                        handleTriggerMethod(*it);
                        break;
                }
                break;
            }
        }
    }

    return 0;
}

int WfdRtspProtocol::getResult(string & result)
{
    printf("WfdRtspProtocol::getResult \n");
    ScopedMutex sm(mLocker);

    result = mResult;

    return 0;
}

int WfdRtspProtocol::handleWfdAudioCodecs(const string & str)
{
    printf("WfdRtspProtocol::handleWfdAudioCodecs \n");
    if(str.find(":") != string::npos)       // set parameter
    {
        mState = WFD_SET_PARAMETER;
        vector<string> audSegs;
        parseToMeta(str, " ", audSegs);
#if 1        
        for(vector<string>::iterator it = audSegs.begin(); it != audSegs.end(); it++)
        {
            printf("WfdRtspProtocol::handleWfdAudioCodecs seg = %s \n", (*it).c_str());
        }
#endif       
    }
    else                                    // get parameter
    {
        string reply = "wfd_audio_codecs: AAC 00000006 03, DTS 00000003 05\r\n"; // hardcode for test
        mResult += reply;
    }
    
    return 0;
}

int WfdRtspProtocol::handleWfdVideoFormats(const string & str)
{
    printf("WfdRtspProtocol::handleWfdVideoFormats \n");
    if(str.find(":") != string::npos)       // set parameter
    {
        mState = WFD_SET_PARAMETER;
    }
    else                                    // get parameter
    {
        mState = WFD_GET_PARAMETER;
        string reply = "wfd_video_formats: 79 00 01 10 9006 0004 00000000 00000000 00000000 01 0000 00 00\r\n"; // hardcode for test       
        mResult += reply;
    }
    
    return 0;
}

int WfdRtspProtocol::handle3DFormats(const string & str)
{
    printf("WfdRtspProtocol::handle3DFormats \n");
    return 0;
}

int WfdRtspProtocol::handleContentProtection(const string & str)
{
    printf("WfdRtspProtocol::handleContentProtection \n");
    if(str.find(":") != string::npos)       // set parameter
    {
        mState = WFD_SET_PARAMETER;
    }
    else                                    // get parameter
    {
        mState = WFD_GET_PARAMETER;
        string reply = "wfd_content_protection: none\r\n"; // hardcode for test
        mResult += reply;
    }
    return 0;
}

int WfdRtspProtocol::handleDisplayEdid(const string & str)
{
    printf("WfdRtspProtocol::handleDisplayEdid \n");
    if(str.find(":") != string::npos)       // set parameter
    {
        mState = WFD_SET_PARAMETER;
    }
    else                                    // get parameter
    {
        mState = WFD_GET_PARAMETER;
        string reply = "wfd_display_edid: UmFsaW5r\r\n"; // hardcode for test
        mResult += reply;
    }
    return 0;
}

int WfdRtspProtocol::handleCoupledSink(const string & str)
{
    printf("WfdRtspProtocol::handleCoupledSink \n");
    if(str.find(":") != string::npos)       // set parameter
    {
        mState = WFD_SET_PARAMETER;
    }
    else                                    // get parameter
    {
        mState = WFD_GET_PARAMETER;
        string reply = "wfd_coupled_sink: 00\r\n"; // hardcode for test
        mResult += reply;
    }
    return 0;
}

int WfdRtspProtocol::handleClientRtpPorts(const string & str)
{
    printf("WfdRtspProtocol::handleClientRtpPorts \n");
    return 0;
}

int WfdRtspProtocol::handlePresentationUrl(const string & str)
{
    printf("WfdRtspProtocol::handlePresentationUrl \n");
    return 0;
}

int WfdRtspProtocol::handleTriggerMethod(const string & str)
{
    printf("WfdRtspProtocol::handleTriggerMethod \n");
    return 0;
}



}


