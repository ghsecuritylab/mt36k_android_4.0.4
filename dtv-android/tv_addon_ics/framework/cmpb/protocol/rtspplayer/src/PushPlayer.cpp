#include "PushPlayer.h"
#include "myLog.h"
#include <string.h>
#include "myCfg.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <assert.h>

namespace rtsp
{
static const unsigned int CMPB_PRIVATE_PS_BUF_SIZE = 4512;
static const unsigned int CMPB_PRIVATE_CONTAINER_HEADER_SIZE = 14;
static const unsigned int PUSH_PLAYER_LOCAL_BUFFER_SIZE = 1024*1024;
static unsigned long aTotalSize = 0;
static unsigned long vTotalSize = 0;
static IMtkPb_Ctrl_Nfy_Fct  gSelfCmpbNfy = NULL;
static void* gSelfTag;
static bool bSeeking = false;

PushPlayer PushPlayer::_instance;
extern bool bSaveLocalFile;
extern bool bSaveCmpb;
extern bool bOnlyCount;
extern bool bCmpbPlayAudio;
extern bool bCmpbPlayVideo;
static IMTK_PB_CB_ERROR_CODE_T pushPlayerCallback(IMTK_PB_CTRL_EVENT_T       eEventType,
													void*                      pvTag,
														uint32_t                   u4Data)
{   
	LOG_DEBUG("cmpb call back event(type:%d)!", (int)eEventType);

	if (eEventType == IMTK_PB_CTRL_EVENT_PLAY_DONE)
	{
		if (bSeeking == true)
		{
			bSeeking = false;
			eEventType = IMTK_PB_CTRL_EVENT_TIMESEEK_DONE;
			LOG_ERR("cmpb notify seek done!")
		}
	}
	
    if ( gSelfCmpbNfy )
    {
	    return gSelfCmpbNfy( eEventType, gSelfTag, u4Data  );
    }
	
	switch (eEventType)    
	{    
		case IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE:                    
			break;      
		case IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW:        
			break;      
		case IMTK_PB_CTRL_EVENT_TOTAL_TIME_UPDATE:        
			break;    
		case IMTK_PB_CTRL_EVENT_EOS:        
			break;           
		default :        
			break;    
	}    

	return IMTK_PB_CB_ERROR_CODE_OK;
}

PushPlayer::PushPlayer():handle(0), status(PUSHPLAYERSTATUS_UNINIT),
							pCmpbBufA(NULL), pCmpbBufV(NULL), uiAWriteLen(0), uiVWriteLen(0),
							    aFd(0), vFd(0)
{
	memset(&media_info, 0, sizeof(media_info));
    bDataFinished = true;
}

PushPlayer::~PushPlayer()
{
	/*first set the continue flag to end and next notify all */
	if (status != PUSHPLAYERSTATUS_STOPPED)
	{
		stop();
	}
}

bool PushPlayer::open(IMtkPb_Ctrl_Nfy_Fct fCmpbEventNfy, void* pvTag)
{
	ScopedMutex sm(lock);

    gSelfCmpbNfy = fCmpbEventNfy;
    gSelfTag = pvTag;
    
	IMTK_PB_ERROR_CODE_T ret = IMTK_PB_ERROR_CODE_OK;
	LOG_DEBUG("push player open begin");
	if ((status != PUSHPLAYERSTATUS_UNINIT) &&
		(status != PUSHPLAYERSTATUS_STOPPED))
	{
		LOG_ERR("can't open, current status:%d!", (int)status);
		return false;
	}

	ret = IMtkPb_Ctrl_Open(&handle, IMTK_PB_CTRL_BUFFERING_MODEL_PUSH,
									IMTK_PB_CTRL_APP_MASTER, 0);
	if (IMTK_PB_ERROR_CODE_OK != ret)
	{
		LOG_ERR("cmpb open failed[ret=%d]!", ret);
		return false;
	}
	LOG_DEBUG("push player open success");

	if (bSaveLocalFile == true)
	{
		if (aFd <= 0)
		{
			aFd = ::open("./test_audio", O_RDWR | O_CREAT | O_TRUNC | O_APPEND);
		}

		if (vFd <= 0)
		{
			vFd = ::open("./test_video", O_RDWR | O_CREAT | O_TRUNC | O_APPEND);
		}
	}

	status = PUSHPLAYERSTATUS_OPENED;

	if (bOnlyCount == true)
	{
		aTotalSize = 0;
		vTotalSize = 0;
	}

    bDataFinished = false;
	bSeeking = false;
	
	return true;
}
bool PushPlayer::play()
{
	ScopedMutex sm(lock);
	IMTK_PB_ERROR_CODE_T ret = IMTK_PB_ERROR_CODE_OK;
	LOG_DEBUG("push player play begin");
	if (status != PUSHPLAYERSTATUS_OPENED)
	{
		LOG_ERR("can't play, current status:%d!", (int)status);
		return false;
	}
	
	do
	{
		ret = IMtkPb_Ctrl_RegCallback(handle, (void *)this, pushPlayerCallback);
		if (IMTK_PB_ERROR_CODE_OK != ret)
		{
			LOG_ERR("cmpb reg callback failed[ret=%d]!", ret);
			break;
		}
		LOG_DEBUG("push player reg callback success");

		IMTK_PB_CTRL_ENGINE_PARAM_T     t_parm;

		memset(&t_parm, 0, sizeof(IMTK_PB_CTRL_ENGINE_PARAM_T));
		
		if (bCmpbPlayAudio)
		    t_parm.u4PlayFlag |= IMTK_PB_CTRL_PLAY_FLAG_AUDIO; 
		    
		if (bCmpbPlayVideo)
		    t_parm.u4PlayFlag |= IMTK_PB_CTRL_PLAY_FLAG_VIDEO; 

	    ret = IMtkPb_Ctrl_SetEngineParam(handle, &t_parm);
	    if (ret != IMTK_PB_ERROR_CODE_OK)                                
	    {
			LOG_ERR("cmpb set engine failed[ret=%d]!", ret);
			break;
	    }
		LOG_DEBUG("push player set engine success");
#ifndef RTSP_LOCAL_TEST		
		LOG_DEBUG("media type=%d", (int)media_info.eMediaType);
		LOG_DEBUG("media duration=%x", (int)media_info.u4TotalDuration);
		LOG_DEBUG("media size=%x", (int)media_info.u8Size);
		LOG_DEBUG("media vid codec =%d", media_info.uFormatInfo.tMtkP0Info.tVidInfo.eVidEnc);
		LOG_DEBUG("media aud codec =%d", media_info.uFormatInfo.tMtkP0Info.tAudInfo.eAudEnc);
#endif
        if (!bCmpbPlayVideo)
            media_info.uFormatInfo.tMtkP0Info.tVidInfo.eVidEnc = IMTK_PB_CTRL_VID_ENC_UNKNOWN;
        if (!bCmpbPlayAudio)
            media_info.uFormatInfo.tMtkP0Info.tAudInfo.eAudEnc = IMTK_PB_CTRL_AUD_ENC_UNKNOWN;
            
	    ret = IMtkPb_Ctrl_SetMediaInfo(handle, &media_info);
	    if (ret != IMTK_PB_ERROR_CODE_OK)                                
	    {
			LOG_ERR("cmpb set media info failed[ret=%d]!", ret);
	        break;
	    }
		LOG_DEBUG("push player set media success");
		
		ret = IMtkPb_Ctrl_Play(handle, 0);
	    if (ret != IMTK_PB_ERROR_CODE_OK)                                
	    {
			LOG_ERR("cmpb play failed[ret=%d]!", ret);
	        break;
	    }
		LOG_DEBUG("push player play success");
	}while(0);

	if (ret != IMTK_PB_ERROR_CODE_OK) 
	{
		LOG_ERR("error:%d", ret);
		if (handle != 0)
		{
			IMtkPb_Ctrl_Close(handle);
	        handle = 0;
		}
		return false;
	}

	status = PUSHPLAYERSTATUS_PLAYED;
	
	return true;
}
bool PushPlayer::pause()
{
	ScopedMutex sm(lock);
	IMTK_PB_ERROR_CODE_T ret = IMTK_PB_ERROR_CODE_OK;

	if (status != PUSHPLAYERSTATUS_PLAYED)
	{
		LOG_ERR("can't pause, current status:%d!", (int)status);
		return false;
	}
	
	ret = IMtkPb_Ctrl_Pause(handle);
    if (ret != IMTK_PB_ERROR_CODE_OK)                                
    {
		LOG_ERR("cmpb pause failed[ret=%d]!", ret);
		return false;
    }

	status = PUSHPLAYERSTATUS_PAUSED;
	
	return true;
}
bool PushPlayer::stop()
{
	ScopedMutex sm(lock);
	
	if ((status != PUSHPLAYERSTATUS_PAUSED) &&
		(status != PUSHPLAYERSTATUS_PLAYED) &&
		(status != PUSHPLAYERSTATUS_OPENED))
	{
		LOG_ERR("can't stop, current status:%d!", (int)status);
		return false;
	}

    setEOS();
    
	IMTK_PB_ERROR_CODE_T ret = IMTK_PB_ERROR_CODE_OK;
	
	ret = IMtkPb_Ctrl_Stop(handle);
    if (ret != IMTK_PB_ERROR_CODE_OK)                                
    {
		LOG_ERR("cmpb stop failed[ret=%d]!", ret);
    }
	ret= IMtkPb_Ctrl_Close(handle);
    if (ret != IMTK_PB_ERROR_CODE_OK)                                
    {
		LOG_ERR("cmpb close failed[ret=%d]!", ret);
    }

	status = PUSHPLAYERSTATUS_STOPPED;
	handle = 0;

	reset();
	return true;
}

void PushPlayer::reset()
{
	if (aFd > 0)
	{
		::close(aFd);
		aFd = 0;
	}
	if (vFd > 0)
	{
		::close(vFd);
		vFd = 0;
	}

	if (bOnlyCount == true)
	{
		LOG_ERR("******total audio size:%ld******", aTotalSize);
		LOG_ERR("******total video size:%ld******", vTotalSize);
		aTotalSize = 0;
		vTotalSize = 0;
	}
	
	pCmpbBufA = NULL;
	pCmpbBufV = NULL;
	uiAWriteLen = 0;
	uiVWriteLen = 0;

}
bool PushPlayer::resume()
{
	ScopedMutex sm(lock);

	if (status != PUSHPLAYERSTATUS_PAUSED)
	{
		LOG_ERR("can't resume, current status:%d!", (int)status);
		return false;
	}

	IMTK_PB_ERROR_CODE_T ret = IMTK_PB_ERROR_CODE_OK;
	
	ret = IMtkPb_Ctrl_Play(handle, 0);
    if (ret != IMTK_PB_ERROR_CODE_OK)                                
    {
		LOG_ERR("cmpb resume failed[ret=%d]!", ret);
		return false;
    }

	status = PUSHPLAYERSTATUS_PLAYED;
	
	return true;
}

bool PushPlayer::timeseek()
{
	ScopedMutex sm(lock);
	IMTK_PB_ERROR_CODE_T ret = IMTK_PB_ERROR_CODE_OK;
		
	ret = IMtkPb_Ctrl_Stop(handle);
	if (ret != IMTK_PB_ERROR_CODE_OK)								 
	{
		LOG_ERR("cmpb stop failed[ret=%d]!", ret);
		return false;
	}

	status = PUSHPLAYERSTATUS_OPENED;
	
	ret = IMtkPb_Ctrl_Play(handle, 0);
	if (ret != IMTK_PB_ERROR_CODE_OK)								 
	{
		LOG_ERR("cmpb play failed[ret=%d]!", ret);
		return false;
	}

	bSeeking = true;
	
	status = PUSHPLAYERSTATUS_PLAYED;
		
	return true;

}

bool PushPlayer::SetMediaInfo(MediaInfo &info)
{
	memset(&media_info, 0, sizeof(media_info));

	LOG_DEBUG("type:%d, audio codec:%d, video codec:%d, av codec = %d!", 
		info.type, info.audioCodec, info.videoCodec, info.avCodec);
#ifndef RTSP_LOCAL_TEST
	/*
	 *TO DO:set the media info
	 */

	media_info.u4TotalDuration = 0xFFFFFFFF;
	media_info.u8Size = -1;
#if 1

	if ((info.type & mediatype_video) == mediatype_video)
	{
		//media_info.uFormatInfo.tMtkP0Info.tVidInfo.tAspRatio.eAspectRatio = IMTK_PB_CTRL_SRC_ASPECT_RATIO_UNKNOWN;
		media_info.eMediaType = IMTK_PB_CTRL_MEDIA_TYPE_MTK_P0;
		
		switch (info.videoCodec)
		{
			case MEDIACODEC_H264:
			{
				media_info.uFormatInfo.tMtkP0Info.tVidInfo.eVidEnc = IMTK_PB_CTRL_VID_ENC_H264;
				break;
			}
			case MEDIACODEC_MPV:/*MPEG-1 or 2 video*/
			case MEDIACODEC_MP1S:/*MPEG-1 System Stream*/
			case MEDIACODEC_MP2P:/*MPEG-2 Program Stream*/
			{
				media_info.uFormatInfo.tMtkP0Info.tVidInfo.eVidEnc = IMTK_PB_CTRL_VID_ENC_MPEG1_2;
				break;
			}
#if 0
			case MEDIACODEC_MP1S:/*MPEG-1 System Stream*/
			{
			}
			case MEDIACODEC_MP2P:/*MPEG-2 Program Stream*/
			{
				break;
			}
			case MEDIACODEC_MP2T:/*MPEG-2 Transport Stream*/
			{
				media_info.eMediaType = IMTK_PB_CTRL_MEDIA_TYPE_MPEG2_TS;
				
				media_info.uFormatInfo.tTsInfo.ePacketType = IMTK_PB_CTRL_MPEG2_TS_PACKET_TYPE_188BYTE;
				media_info.uFormatInfo.tTsInfo.eVidInfo.eVidEnc = IMTK_PB_CTRL_VID_ENC_MPEG1_2;
				media_info.uFormatInfo.tTsInfo.eVidInfo.u2Pid = 160;
				break;
			}
#endif
			case MEDIACODEC_MP4V_ES:
			{
				media_info.uFormatInfo.tMtkP0Info.tVidInfo.eVidEnc = IMTK_PB_CTRL_VID_ENC_MPEG4;
				break;
			}
			case MEDIACODEC_JPEG:
			{
				media_info.uFormatInfo.tMtkP0Info.tVidInfo.eVidEnc = IMTK_PB_CTRL_VID_ENC_MJPEG;
				break;
			}
			default:
			{
				/*not support, do nothing*/
				break;
			}
		}
	}
#endif
#if 1
	if ((info.type & mediatype_audio) == mediatype_audio)
	{
		media_info.eMediaType = IMTK_PB_CTRL_MEDIA_TYPE_MTK_P0;
		
		switch (info.audioCodec)
		{
			case MEDIACODEC_MPA:/*MPEG-1 or 2 audio*/
			{
				media_info.uFormatInfo.tMtkP0Info.tAudInfo.eAudEnc = IMTK_PB_CTRL_AUD_ENC_MPEG;
				break;
			}
			case MEDIACODEC_MPA_ROBUST:
			case MEDIACODEC_X_MP3_DRAFT_00:
			{
				media_info.uFormatInfo.tMtkP0Info.tAudInfo.eAudEnc = IMTK_PB_CTRL_AUD_ENC_MP3;
				break;
			}
			case MEDIACODEC_AC3:/*AC3 audio*/
			case MEDIACODEC_EAC3:
            case MEDIACODEC_MP4A_LATM:
            case MEDIACODEC_MPEG4_GENERIC:
			{
				media_info.uFormatInfo.tMtkP0Info.tAudInfo.eAudEnc = IMTK_PB_CTRL_AUD_ENC_AAC;
				break;
			}	
			case MEDIACODEC_PCMA:/*PCM a-law audio*/
			case MEDIACODEC_PCMU:/*PCM u-law audio*/
			case MEDIACODEC_DVI4:/*DVI4 (IMA ADPCM) audio*/
			{
				media_info.uFormatInfo.tMtkP0Info.tAudInfo.eAudEnc = IMTK_PB_CTRL_AUD_ENC_PCM;
				break;
			}	
			default:
			{
				break;
			}
		}
	}
#endif
	if ((info.type & mediatype_av) == mediatype_av)/*currently only ts*/
	{
		switch(info.avCodec)
		{
			case MEDIACODEC_MP2T:
			{
				media_info.eMediaType = IMTK_PB_CTRL_MEDIA_TYPE_MPEG2_TS;
				media_info.uFormatInfo.tTsInfo.ePacketType = IMTK_PB_CTRL_MPEG2_TS_PACKET_TYPE_188BYTE;
				media_info.uFormatInfo.tTsInfo.eVidInfo.eVidEnc = IMTK_PB_CTRL_VID_ENC_MPEG1_2;
				media_info.uFormatInfo.tTsInfo.eVidInfo.u2Pid = 273;
				media_info.uFormatInfo.tTsInfo.eAudInfo.eAudEnc= IMTK_PB_CTRL_AUD_ENC_AAC;
				media_info.uFormatInfo.tTsInfo.eAudInfo.u2Pid = 274;
				break;
			}
            case MEDIACODEC_WFDAV:
            {
                LOG_DEBUG("for WFD A/V codec setting");
                // 1. media type 
                media_info.eMediaType = IMTK_PB_CTRL_MEDIA_TYPE_MPEG2_TS;
                media_info.uFormatInfo.tTsInfo.ePacketType = IMTK_PB_CTRL_MPEG2_TS_PACKET_TYPE_188BYTE;

                // 2. audio codec & pid
                switch(info.audioCodec)
                {
                    case MEDIACODEC_WFDA_AAC:
                        media_info.uFormatInfo.tTsInfo.eAudInfo.eAudEnc = IMTK_PB_CTRL_AUD_ENC_AAC;
                        break;
                    case MEDIACODEC_WFDA_AC3:
                        media_info.uFormatInfo.tTsInfo.eAudInfo.eAudEnc = IMTK_PB_CTRL_AUD_ENC_AAC;
                        break;
                    case MEDIACODEC_WFDA_DTS:
                        media_info.uFormatInfo.tTsInfo.eAudInfo.eAudEnc = IMTK_PB_CTRL_AUD_ENC_DTS;
                        break;
                    case MEDIACODEC_WFDA_LPCM:
                        media_info.uFormatInfo.tTsInfo.eAudInfo.eAudEnc = IMTK_PB_CTRL_AUD_ENC_PCM;
                        break;
                    default:
                        printf("Error PushPlayer::SetMediaInfo unknown audio codec \n");
                        assert(0);
                        break;
                }
                media_info.uFormatInfo.tTsInfo.eAudInfo.u2Pid = 274;

                // video codec & pid
                switch(info.videoCodec)
                {
                    case MEDIACODEC_WFDV_H264:
                        media_info.uFormatInfo.tTsInfo.eVidInfo.eVidEnc = IMTK_PB_CTRL_VID_ENC_H264;
                        break;
                    default:
                        printf("Error PushPlayer::SetMediaInfo unknown video codec \n");
                        assert(0);
                        break;
                }
                media_info.uFormatInfo.tTsInfo.eVidInfo.u2Pid = 273;
                
                break;
            }
			default:
			{
				break;
			}
		}

	}
#endif

	return true;
}

void PushPlayer::setEOS()
{
    printf("PushPlayer::setEOS handle = %u \n", handle);
    if(bDataFinished == false)
    {
        if(handle != 0)
        {
            printf("PushPlayer::setEOS 1. set push eos \n");
            IMtkPb_Ctrl_SetPushModelEOS(handle);
            printf("PushPlayer::setEOS 2. set push eos \n");
        }
    }
    bDataFinished = true;
}

void PushPlayer::SendData(GetBufData & sender)
{	    
	IMTK_PB_ERROR_CODE_T ret = IMTK_PB_ERROR_CODE_OK;
	
	if (bOnlyCount)
	{
		if (sender.type == mediatype_audio)
			aTotalSize += sender.iLen;
		else
			vTotalSize += sender.iLen;
	}
	if (bSaveCmpb == true)
	{
		unsigned char *pCmpbBuf = NULL;
		unsigned int *puiWriteLen = NULL;
		unsigned long long ulPTS = 0;

		{
			if (sender.type == mediatype_audio)
			{
			    if (!bCmpbPlayAudio)
			        return ;
			        
				puiWriteLen = &uiAWriteLen;
				if (*puiWriteLen == 0)
				{
					LOG_DEBUG("get cmpb buffer for audio");
					ret = IMtkPb_Ctrl_GetBuffer(handle, sender.iLen + 18, &pCmpbBufA);
				}
						
				pCmpbBuf = pCmpbBufA;
			}
			else
			{
			    if (!bCmpbPlayVideo)
			        return ;
			        
				puiWriteLen = &uiVWriteLen;
				if (*puiWriteLen == 0)
				{
					LOG_DEBUG("get cmpb buffer for video");
					ret = IMtkPb_Ctrl_GetBuffer(handle, sender.iLen + 18, &pCmpbBufV);
				}
				
				pCmpbBuf = pCmpbBufV;
			}
			if(ret != IMTK_PB_ERROR_CODE_OK)
			{
				LOG_ERR("cmpb get buffer[size=%d] failed[ret=%d]!",sender.iLen, ret);
				return;
			}
		}

		
		memcpy(pCmpbBuf + 18 + *puiWriteLen, sender.pBuf, sender.iLen);
		*puiWriteLen += sender.iLen;

		//if (*puiWriteLen > 24*1024)
		{
			unsigned int attrsize = 0;

			if (sender.type == mediatype_av)
			{
				attrsize = 0;
			}
			else
			{
				attrsize = 18;
			
				pCmpbBuf[0] = 0x3E;
				pCmpbBuf[1] = 0xD1;
				pCmpbBuf[2] = 0xA7;
				pCmpbBuf[3] = 0xE4;
				//memcpy(pCmpbBuf + 4, (void *)&ui8_pts, 8);
				memcpy(pCmpbBuf + 12, (void *)(puiWriteLen), 4);
				if (sender.type == mediatype_audio)
				{
					pCmpbBuf[16] = 0x02;
                    ulPTS = sender.dCurPlayTime * 90000;                     
					memcpy(pCmpbBuf + 4, (void *)&ulPTS, 8);
				}
				else
				{
					pCmpbBuf[16] = 0x01;
                    ulPTS = sender.dCurPlayTime * 90000;             
					memcpy(pCmpbBuf + 4, (void *)&ulPTS, 8);
				}
				pCmpbBuf[17] = 0;
			}

			ret = IMtkPb_Ctrl_SendData(handle, *puiWriteLen + attrsize , pCmpbBuf + 18 - attrsize);
			if (IMTK_PB_ERROR_CODE_OK != ret)
			{
				LOG_ERR("cmpb send data error[ret=%d]!", ret);
			}
			
			LOG_DEBUG("send cmpb data length :%d!", *puiWriteLen + attrsize);

			*puiWriteLen = 0;
		}
	}

	
	if (bSaveLocalFile == true)
	{
		if (sender.type == mediatype_audio)
		{
			::write(aFd, sender.pBuf, sender.iLen);
		}
		else
		{
			::write(vFd, sender.pBuf, sender.iLen);
		}
	}

#if 0
        if(bDataFinished)
        {
            LOG_DEBUG("PushPlayer::SendData data finished \n");
            IMtkPb_Ctrl_SetPushModelEOS(handle);
        }
#endif        
}
}

