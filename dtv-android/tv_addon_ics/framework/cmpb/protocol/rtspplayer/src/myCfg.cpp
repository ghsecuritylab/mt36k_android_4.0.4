#include "myCfg.h"
#include "myLog.h"
#include "PushPlayer.h"
namespace rtsp
{
bool bUseTcp = false;
bool bSaveLocalFile = false;
bool bSaveCmpb = true;
bool bOnlyCount = false;
bool bCmpbPlayAudio = true;
bool bCmpbPlayVideo = true;
void setLog(bool flag)
{
	b_debug_log = flag;
}

void saveLocalFile(bool flag)
{
	bSaveLocalFile = flag;
}
void saveCmpbPlay(bool flag)
{
	bSaveCmpb = flag;
}

void setTcpFlag(bool flag)
{
	bUseTcp = flag;
}

void setOnlyCount(bool flag)
{
	bOnlyCount = flag;
}

void setCmpbPlayAudio(bool flag)
{
	bCmpbPlayAudio = flag;
}

void setCmpbPlayVideo(bool flag)
{
	bCmpbPlayVideo = flag;
}
}

