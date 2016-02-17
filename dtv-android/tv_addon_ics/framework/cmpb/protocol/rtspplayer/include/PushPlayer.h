#ifndef _PUSH_PLAYER_H_
#define _PUSH_PLAYER_H_
#include "ScopedMutex.h"
#include "NonCopyable.h"
#include "IMtkPb_Ctrl.h"
#include "IMtkPb_ErrorCode.h"
#include "IMtkPb_Ctrl_DTV.h"
#include <sys/time.h>
#include <unistd.h>

namespace rtsp
{

struct GetBufData
{
	GetBufData(unsigned char *_pBuf, unsigned int _type, unsigned int _iLen, double _dCurPlayTime):
		pBuf(_pBuf), type(_type), iLen(_iLen), dCurPlayTime(_dCurPlayTime)
	{
	}
	GetBufData():pBuf(NULL), type(0), iLen(0), dCurPlayTime(0)
	{
	}
	unsigned char *pBuf;
	unsigned int type;
	unsigned int iLen;
	double dCurPlayTime;
};
typedef enum
{
	PUSHPLAYERSTATUS_UNINIT = 0,
	PUSHPLAYERSTATUS_OPENED,	
	PUSHPLAYERSTATUS_PLAYED,
	PUSHPLAYERSTATUS_PAUSED,
	PUSHPLAYERSTATUS_STOPPED
}PushPlayerStatus;


const unsigned int mediatype_audio = 1;
const unsigned int mediatype_video = 2;
const unsigned int mediatype_av = 4;

typedef enum
{
	MEDIACODEC_QCELP,
	MEDIACODEC_AMR,
	MEDIACODEC_AMR_WB,
	MEDIACODEC_MPA,
	MEDIACODEC_MPA_ROBUST,
	MEDIACODEC_X_MP3_DRAFT_00,
	MEDIACODEC_MP4A_LATM,
	MEDIACODEC_AC3,
	MEDIACODEC_EAC3,
	MEDIACODEC_MP4V_ES,
	MEDIACODEC_MPEG4_GENERIC,       // 10
	MEDIACODEC_MPV,
	MEDIACODEC_MP2T,
	MEDIACODEC_H261,
	MEDIACODEC_H263_1998,
	MEDIACODEC_H263_2000,
	MEDIACODEC_H264,
	MEDIACODEC_DV,
	MEDIACODEC_JPEG,
	MEDIACODEC_X_QT,
	MEDIACODEC_PCMU,                // 20
	MEDIACODEC_GSM,
	MEDIACODEC_DVI4,
	MEDIACODEC_PCMA,
	MEDIACODEC_MP1S,
	MEDIACODEC_MP2P,
	MEDIACODEC_L8,
	MEDIACODEC_L16,
	MEDIACODEC_L20,
	MEDIACODEC_L24,
	MEDIACODEC_G726_16,             // 30
	MEDIACODEC_G726_24,
	MEDIACODEC_G726_32,
	MEDIACODEC_G726_40,
	MEDIACODEC_SPEEX,
	MEDIACODEC_T140,
	MEDIACODEC_DAT12,
	// for WFD codec
	MEDIACODEC_WFDAV,               // 37
	MEDIACODEC_WFDA_LPCM,
	MEDIACODEC_WFDA_AAC,
	MEDIACODEC_WFDA_AC3,            // 40
	MEDIACODEC_WFDA_DTS,
	MEDIACODEC_WFDV_H264,
}MediaCodec;

typedef struct
{
	int type;
	int payload_type;
	MediaCodec audioCodec;
	MediaCodec videoCodec;
	MediaCodec avCodec;
}MediaInfo;


class PushPlayer:private NonCopyable
{
	public:	
		~PushPlayer();

		bool open(IMtkPb_Ctrl_Nfy_Fct fCmpbEventNfy = NULL, void* pvTag = NULL);
		bool play();
		bool pause();
		bool stop();
		bool resume();
		bool SetMediaInfo();
		bool timeseek();
		static PushPlayer & instance(void)
		{
        	return _instance;
    	}

		void SendData(GetBufData & sender);
		bool SetMediaInfo(MediaInfo &info);/*called by rtsp client after dsp response*/
		void setEOS();
	private:
		Mutex lock;
		static PushPlayer _instance;	
		IMTK_PB_HANDLE_T handle;
		PushPlayerStatus status;
		IMTK_PB_CTRL_SET_MEDIA_INFO_T   media_info;
		bool	bDataFinished;
	private:
		void StartPlay();
		bool WaitPlay();
		void StopPlay();
		PushPlayer();
	private:
		unsigned char *pCmpbBufA;
		unsigned char *pCmpbBufV;
		unsigned int uiAWriteLen;
		unsigned int uiVWriteLen;
		int aFd;
		int vFd;
		void reset();
};


}

#endif
