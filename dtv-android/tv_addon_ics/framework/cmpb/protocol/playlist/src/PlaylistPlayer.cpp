/*----------------------------------------------------------------------------*
 * Copyright Statement:                                                       *
 *                                                                            *
 *   This software/firmware and related documentation ("MediaTek Software")   *
 * are protected under international and related jurisdictions'copyright laws *
 * as unpublished works. The information contained herein is confidential and *
 * proprietary to MediaTek Inc. Without the prior written permission of       *
 * MediaTek Inc., any reproduction, modification, use or disclosure of        *
 * MediaTek Software, and information contained herein, in whole or in part,  *
 * shall be strictly prohibited.                                              *
 * MediaTek Inc. Copyright (C) 2010. All rights reserved.                     *
 *                                                                            *
 *   BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND     *
 * AGREES TO THE FOLLOWING:                                                   *
 *                                                                            *
 *   1)Any and all intellectual property rights (including without            *
 * limitation, patent, copyright, and trade secrets) in and to this           *
 * Software/firmware and related documentation ("MediaTek Software") shall    *
 * remain the exclusive property of MediaTek Inc. Any and all intellectual    *
 * property rights (including without limitation, patent, copyright, and      *
 * trade secrets) in and to any modifications and derivatives to MediaTek     *
 * Software, whoever made, shall also remain the exclusive property of        *
 * MediaTek Inc.  Nothing herein shall be construed as any transfer of any    *
 * title to any intellectual property right in MediaTek Software to Receiver. *
 *                                                                            *
 *   2)This MediaTek Software Receiver received from MediaTek Inc. and/or its *
 * representatives is provided to Receiver on an "AS IS" basis only.          *
 * MediaTek Inc. expressly disclaims all warranties, expressed or implied,    *
 * including but not limited to any implied warranties of merchantability,    *
 * non-infringement and fitness for a particular purpose and any warranties   *
 * arising out of course of performance, course of dealing or usage of trade. *
 * MediaTek Inc. does not provide any warranty whatsoever with respect to the *
 * software of any third party which may be used by, incorporated in, or      *
 * supplied with the MediaTek Software, and Receiver agrees to look only to   *
 * such third parties for any warranty claim relating thereto.  Receiver      *
 * expressly acknowledges that it is Receiver's sole responsibility to obtain *
 * from any third party all proper licenses contained in or delivered with    *
 * MediaTek Software.  MediaTek is not responsible for any MediaTek Software  *
 * releases made to Receiver's specifications or to conform to a particular   *
 * standard or open forum.                                                    *
 *                                                                            *
 *   3)Receiver further acknowledge that Receiver may, either presently       *
 * and/or in the future, instruct MediaTek Inc. to assist it in the           *
 * development and the implementation, in accordance with Receiver's designs, *
 * of certain softwares relating to Receiver's product(s) (the "Services").   *
 * Except as may be otherwise agreed to in writing, no warranties of any      *
 * kind, whether express or implied, are given by MediaTek Inc. with respect  *
 * to the Services provided, and the Services are provided on an "AS IS"      *
 * basis. Receiver further acknowledges that the Services may contain errors  *
 * that testing is important and it is solely responsible for fully testing   *
 * the Services and/or derivatives thereof before they are used, sublicensed  *
 * or distributed. Should there be any third party action brought against     *
 * MediaTek Inc. arising out of or relating to the Services, Receiver agree   *
 * to fully indemnify and hold MediaTek Inc. harmless.  If the parties        *
 * mutually agree to enter into or continue a business relationship or other  *
 * arrangement, the terms and conditions set forth herein shall remain        *
 * effective and, unless explicitly stated otherwise, shall prevail in the    *
 * event of a conflict in the terms in any agreements entered into between    *
 * the parties.                                                               *
 *                                                                            *
 *   4)Receiver's sole and exclusive remedy and MediaTek Inc.'s entire and    *
 * cumulative liability with respect to MediaTek Software released hereunder  *
 * will be, at MediaTek Inc.'s sole discretion, to replace or revise the      *
 * MediaTek Software at issue.                                                *
 *                                                                            *
 *   5)The transaction contemplated hereunder shall be construed in           *
 * accordance with the laws of Singapore, excluding its conflict of laws      *
 * principles.  Any disputes, controversies or claims arising thereof and     *
 * related thereto shall be settled via arbitration in Singapore, under the   *
 * then current rules of the International Chamber of Commerce (ICC).  The    *
 * arbitration shall be conducted in English. The awards of the arbitration   *
 * shall be final and binding upon both parties and shall be entered and      *
 * enforceable in any court of competent jurisdiction.                        *
 *---------------------------------------------------------------------------*/
#include "PlaylistPlayer.h"
#include <string.h>
#include "Log.h"
#include <stdlib.h>

//#define PRINT_TIME  1
#ifdef PRINT_TIME
#include <sys/time.h>
#include <time.h>
#endif

using namespace hls;
using namespace std;

#define CMPB_MAGIC_TAG 0x19860528

#ifndef DOWNLOAD_FILE
//#define DOWNLOAD_FILE
#endif
PlaylistPlayerListener::~PlaylistPlayerListener()
{
}
PlaylistPlayer::~PlaylistPlayer()
{
}

static unsigned char paLastAesKey[16];

PlaylistPlayer::PlaylistPlayer(PlaylistPlayerListener * listener) : caller(listener)
{
    fCMPBHandle = 0;
    m_duration = 0;
    pt_playlist = NULL;
    bTsInfoReady = false;
    bHaveToSetKey = false;
    videoRect.u4X = 0;
    videoRect.u4Y = 0;
    videoRect.u4W = 1000;
    videoRect.u4H =  1000;        
}

bool PlaylistPlayer::start(Playlist &plist, event_nfy_fct cb, void* tag, int time)
{   
    IMTK_PB_ERROR_CODE_T i4_ret = IMTK_PB_ERROR_CODE_OK;
    num_of_media_file_played = 0;
    
#if 0
    MEDIA_INFO * minfo = NULL;
#endif
    /* reopen handle in app master mode */
    /* push mode */
#ifdef PRINT_TIME
    struct timeval timeV = {0};
    gettimeofday(&timeV, NULL);
    LOG(-1, "[1]Current time: [%d]s.[%d]ms!!!\n", timeV.tv_sec, timeV.tv_usec / 1000);
#endif
    pt_playlist = &plist;
    if (pt_playlist->getHLSPlaylist())
    {
        LOG(5, "playlist type = %d\n", pt_playlist->getHLSPlaylist()->playlistType);
    }
#if 0 /* MTK: Remove by Jinlong */
    // move realize step to download thread so that some time can be saved and app won't be blocked in this method.
    if (! pt_playlist->realize())
    {
        return false;
    }
#endif

    this->time = time;
    pt_playlist->setRealized(false);

    // start download and push mode engine for playback, before the player parameter is pushed.
    if ( pt_playlist->startEngine(this) == false)
    {
        // start engine failed.
        LOG(0, "pt_playlist->startEngine fail!!\n");
        return IMTK_PB_ERROR_CODE_NOT_OK;
    }
    
    m_duration = 0;

    if (pt_playlist->getCurHLSPlaylist())
    {
        m_duration = pt_playlist->getCurHLSPlaylist()->duration;
    }

    if ( fCMPBHandle )
    {
        return i4_ret;
    }
    else
    {
        fCMPBHandle = 0;
        IMtkPb_Ctrl_Open(&fCMPBHandle, IMTK_PB_CTRL_BUFFERING_MODEL_PUSH, IMTK_PB_CTRL_APP_MASTER, (uint8_t*)"quick_start");
        IMtkPb_Ctrl_RegCallback(fCMPBHandle, (void *)tag, cb);
        callback = cb;
        pvTag = tag;
    }
    
    /*reset ts info ready flag*/
    bTsInfoReady = false;    

    return i4_ret;
}


void PlaylistPlayer::setDisplayRect(IMTK_PB_CTRL_RECT_T &outRect)
{
    if (fCMPBHandle)
    {
        IMTK_PB_CTRL_RECT_T    t_SrcRect = {0,0,1000,1000};
        videoRect.u4X =outRect.u4X;
        videoRect.u4Y = outRect.u4Y;
        videoRect.u4W = outRect.u4W;
        videoRect.u4H = outRect.u4H;       
        IMtkPb_Ctrl_SetDisplayRectangle(fCMPBHandle, &t_SrcRect, &videoRect);
    }
}

IMTK_PB_ERROR_CODE_T PlaylistPlayer::getCmpbAsp(void * ptData)
{
    IMTK_PB_ERROR_CODE_T    e_return = IMTK_PB_ERROR_CODE_NOT_OK;
    IMTK_PB_ERROR_CODE_T    e_return1 = IMTK_PB_ERROR_CODE_NOT_OK;
    IMTK_PB_CTRL_ASP_T  *ptAsp = (IMTK_PB_CTRL_ASP_T  *)ptData;
   	uint32_t             ui4_width = 0;
    uint32_t             ui4_height = 0;

    if(ptAsp == NULL)
        return e_return;

    e_return = IMtkPb_Ctrl_GetASP( fCMPBHandle, ptAsp);
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOG(0, "[CMPB Error] IMtkPb_Ctrl_GetASP() ret=%d\n", e_return);                     
    }

    return e_return;
}

void PlaylistPlayer::play()
{
    if (fCMPBHandle)
    {
        IMTK_PB_CTRL_RECT_T    t_SrcRect = {0,0,1000,1000};
        IMtkPb_Ctrl_SetDisplayRectangle(fCMPBHandle, &t_SrcRect, &videoRect);
        IMtkPb_Ctrl_Play(fCMPBHandle, 0);
    }
}

void PlaylistPlayer::stop()
{
    if (fCMPBHandle)
    {
        pt_playlist = NULL;
        playerLocker.syncBegin();
        setVideoFreeze(false);
        if(bTsInfoReady)
        {
            IMtkPb_Ctrl_Stop(fCMPBHandle);
        }
        IMtkPb_Ctrl_Close(fCMPBHandle);
        fCMPBHandle = 0;
        bTsInfoReady = false;
        playerLocker.syncEnd();
    }
    num_of_media_file_played = 0;
}

void PlaylistPlayer::pause()
{
    if (bTsInfoReady && fCMPBHandle && (state() == IMTK_PB_CTRL_PLAYING))
    {
        IMtkPb_Ctrl_Pause(fCMPBHandle);
    }
}

void PlaylistPlayer::resume()
{
    if (bTsInfoReady && fCMPBHandle && (state() != IMTK_PB_CTRL_PLAYING))
    {
        IMTK_PB_CTRL_RECT_T    t_SrcRect = {0,0,1000,1000};
        IMtkPb_Ctrl_SetDisplayRectangle(fCMPBHandle, &t_SrcRect, &videoRect);
        IMtkPb_Ctrl_Play(fCMPBHandle, 0);
    }
}

int PlaylistPlayer::timeseek(int time)
{
    int realTime = time;
    
    if (!fCMPBHandle || !pt_playlist)
    {
        return realTime;
    }
    MEDIA_INFO * minfo = NULL;
#ifdef PRINT_TIME
    struct timeval timeV = {0};
    gettimeofday(&timeV, NULL);
    LOG(-1, "[8]Current time: [%d]s.[%d]ms!!!\n", timeV.tv_sec, timeV.tv_usec / 1000);
#endif
    LOG(0, "----[INFO]----Seek time is %d!Duration is %d!\n", time, m_duration);

    m_duration = pt_playlist->getCurHLSPlaylist()->duration;
    if ((time > m_duration) || (time < 0))
    {
        LOG(0, "----[WARNNING]----The time is invalid time = %d, m_duration= %d!\n",
        	time, m_duration);
        notifyStatus(PLAYLIST_STATUS_SEEK_FAIL);
        return realTime;
    }

    if(time == m_duration)
    {
        LOG(0, "----[INFO]----Seek to end!\n");
        notifyStatus(PLAYLIST_STATUS_SEEK_TO_END);
        return realTime;
    }

    pt_playlist->pause();    //puase playlist
    if(bTsInfoReady)   //stop playback
    {
        setVideoFreeze(true);
        IMtkPb_Ctrl_Stop(fCMPBHandle);  
    }
    /* seek to media file */
    minfo = pt_playlist->findMediaByTime(time, &realTime);     
    pt_playlist->select(minfo);     
    pt_playlist->resume();  //resume playlist
       
    if(bTsInfoReady)
    {
        play();   //resume playback
    }
    
#ifdef PRINT_TIME
    gettimeofday(&timeV, NULL);
    LOG(-1, "[9]Current time: [%d]s.[%d]ms!!!\n", timeV.tv_sec, timeV.tv_usec / 1000);
#endif

    return realTime;
}

int PlaylistPlayer::state()
{
    if (!fCMPBHandle)
    {
        return PLAYBACK_UNKNOWN;
    }
    int state = PLAYBACK_UNKNOWN;
    IMtkPb_Ctrl_GetState(fCMPBHandle, (IMTK_PB_CTRL_STATE_T*)&state);

    return state;
}

int PlaylistPlayer::currentTime()
{
	static int last_time = 0;	
    if ((pt_playlist == NULL) || (pt_playlist->getCurHLSPlaylist() == NULL))
    {
        return 0;
    }
	
    int time = 0;
    unsigned long long vidPts = 0;
    unsigned long long audPts = 0;
    IMtkPb_Ctrl_GetCurrentPTS(fCMPBHandle, &vidPts, &audPts);

    time = vidPts / 90000;
    
    m_duration = pt_playlist->getCurHLSPlaylist()->duration;
    
    if (time > m_duration)
    {
        time = m_duration;
        LOG (0, "m_duration = %d,  time = %d\n",  m_duration,  time);
    }
    
    return time;
}

int PlaylistPlayer::duration()
{

    if ((pt_playlist == NULL) || (pt_playlist->getCurHLSPlaylist() == NULL))
    {
        return 0;
    }	

    return pt_playlist->getCurHLSPlaylist()->duration;

}

int PlaylistPlayer::timeBuffered()
{
    if ((pt_playlist == NULL) || (pt_playlist->getCurHLSPlaylist() == NULL))
    {
        return 0;
    }	
    return pt_playlist->getCurHLSPlaylist()->timeBuffered; 
}

int PlaylistPlayer::bytesLoaded()
{
    if ((pt_playlist == NULL) || (pt_playlist->getCurHLSPlaylist() == NULL))
    {
        return 0;
    }	
    return pt_playlist->getCurHLSPlaylist()->byteLoaded; 
} 

int PlaylistPlayer::playlistContentType()
{
    if ((pt_playlist == NULL) || (pt_playlist->getCurHLSPlaylist() == NULL))
    {
        return 0;
    }	
	return pt_playlist->getCurHLSPlaylist()->playlistContentType; 
}

int PlaylistPlayer::downloadSpeed()
{
    if ((pt_playlist == NULL) || (pt_playlist->getCurHLSPlaylist() == NULL))
    {
        return 0;
    }
    return pt_playlist->getCurHLSPlaylist()->downloadSpeed; 
} 

int PlaylistPlayer::bitRate()
{

    IMTK_PB_CTRL_GET_AUD_TRACK_INFO_T   t_info;
    IMTK_PB_ERROR_CODE_T    e_return;
    
    memset(&t_info, 0, sizeof(IMTK_PB_CTRL_GET_AUD_TRACK_INFO_T));
    if (fCMPBHandle)
    {
        e_return = IMtkPb_Ctrl_GetCurAudTrackInfo(fCMPBHandle, &t_info);
        if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
            LOG(6, "bitrate IMtkPb_Ctrl_GetCurAudTrackInfo() ret=%d\n", e_return);            
            return 0;
        }
    }
    LOG(6, "bitrate %d\n", t_info.u4BitRate);    
    return t_info.u4BitRate;
} 

int PlaylistPlayer::curAudTrackInfo(void * ptData)
{
    IMTK_PB_ERROR_CODE_T    e_return = IMTK_PB_ERROR_CODE_OK;
    IMTK_PB_CTRL_GET_AUD_TRACK_INFO_T * ptInfo = (IMTK_PB_CTRL_GET_AUD_TRACK_INFO_T *) ptData;
    
    memset(ptInfo, 0, sizeof(IMTK_PB_CTRL_GET_AUD_TRACK_INFO_T));
    if (fCMPBHandle)
    {
        e_return = IMtkPb_Ctrl_GetCurAudTrackInfo(fCMPBHandle, ptInfo);
        if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
            LOG(6, "[CMPB Error] IMtkPb_Ctrl_GetCurAudTrackInfo() ret=%d\n", e_return);            
        }    	
    }
    return e_return;
}

int PlaylistPlayer::curVidTrackInfo(void * ptData)
{
    IMTK_PB_ERROR_CODE_T    e_return = IMTK_PB_ERROR_CODE_OK;
    IMTK_PB_CTRL_GET_VID_TRACK_INFO_T * ptInfo = (IMTK_PB_CTRL_GET_VID_TRACK_INFO_T *) ptData;
    
    memset(ptInfo, 0, sizeof(IMTK_PB_CTRL_GET_AUD_TRACK_INFO_T));
#if 1
    if (fCMPBHandle)
    {
        e_return = IMtkPb_Ctrl_GetVidTrackInfo(fCMPBHandle, ptInfo);
        if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
            LOG(0, "[CMPB Error] IMtkPb_Ctrl_GetCurVidTrackInfo() ret=%d\n", e_return);            
            return e_return;
        }	
    }
#else
    /* this is for sony, that getVidTrackInfo doest work */
    if (fCMPBHandle)
    {
    	IMTK_PB_CTRL_ASP_T  tAsp;
        e_return = IMtkPb_Ctrl_GetASP( fCMPBHandle, &tAsp);
        if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
            LOG(0, "[CMPB Error] IMtkPb_Ctrl_GetCurVidTrackInfo() ret=%d\n", e_return);                        
        }	        
        ptInfo->u4Height= tAsp.u4SrcH;
        ptInfo->u4Width = tAsp.u4SrcW;
    }
#endif    
    
    return e_return;
}

void PlaylistPlayer::notifyBegin()
{
    if (caller)
    {
        caller->notifyBegin();
    }
}

void PlaylistPlayer::notifyEnd()
{
    if (caller)
    {
        caller->notifyEnd();
    }
}

int PlaylistPlayer::sendData(uint8_t * pu1buffer, uint  size)
{
    uint8_t *pu1PushBuf = NULL;

#ifdef PRINT_TIME
    struct timeval timeV = {0};
    gettimeofday(&timeV, NULL);
    LOG(-1, "[3]Current time: [%d]s.[%d]ms, size[%d]!!!\n", timeV.tv_sec, timeV.tv_usec / 1000, size);
#endif

    if (false == bTsInfoReady)
    {
        playerLocker.syncBegin();
        if (false == setMediaInfo(pu1buffer, size))
        {
            LOG(0, "setMediaInfo fail!\n");
            playerLocker.syncEnd();
            return SENDDATA_TSINFO_FAIL;
        }
        bTsInfoReady = true;
        playerLocker.syncEnd();
        play();
    }    

    IMTK_PB_ERROR_CODE_T ret;


    if(fCMPBHandle == 0)
    {
        LOG(0, "fCMPBHandle == 0!!!\n");
        return SENDDATA_TSINFO_FAIL;
    }
    ret = IMtkPb_Ctrl_GetBuffer(fCMPBHandle, size, &pu1PushBuf);
    if(ret != IMTK_PB_ERROR_CODE_OK)
    {
        LOG(6, "IMtkPb_Ctrl_GetBuffer fail (%d)\n", ret);
        return SENDDATA_GETBUF_FAIL;
    }

    /* copy data to buffer, buffer size may be changed */
    memcpy(pu1PushBuf, pu1buffer, size);


    /* push data to cmpb */
    ret = IMtkPb_Ctrl_SendData(fCMPBHandle, size, pu1PushBuf);
    if (IMTK_PB_ERROR_CODE_OK != ret)
    {
        LOG(0, "IMtkPb_Ctrl_SendData buffer fail (%d)\n", ret);
        return SENDDATA_SENDDATA_FAIL;
        /* error handling */
    }   

    return SENDDATA_SUCCESS;
}

void PlaylistPlayer::setPushModeEOS()
{
    IMtkPb_Ctrl_SetPushModelEOS(fCMPBHandle);
}

void PlaylistPlayer::notifyStatus(int event)
{
	LOG(5, "event = %d\n", event);

    if ((pt_playlist == NULL) || (pt_playlist->getCurHLSPlaylist() == NULL))
    {
        return ;
    }	
    if (event == PLAYLIST_STATUS_REALIZED)
    {

        /* after realized, we could get current duration */
        m_duration = pt_playlist->getCurHLSPlaylist()->duration;   
        
        if (time)
        {
            MEDIA_INFO * minfo = NULL;
            minfo = pt_playlist->findMediaByTime(time);
            pt_playlist->select(minfo);
        }

    }
    // the first step of playlist playback is failed.
    // currently, only dispatch it to playlistplayer listener.
    // maybe something can be do here in future.
    
    if (caller)
    {
        caller->notifyPlaylistPlayerListener(event);
    }
}

bool PlaylistPlayer::setMediaInfo(uint8_t * pu1buffer, uint  size)
{
    LOG(5, "setMediaInfo fCMPBHandle = %x - push mode\n", fCMPBHandle);
    IMTK_PB_ERROR_CODE_T i4_ret = IMTK_PB_ERROR_CODE_OK;
    
    IMTK_PB_CTRL_SET_MEDIA_INFO_T t_mediaInfo;
    if (false == getMediaInfo(pu1buffer, size, t_mediaInfo))
    {
        LOG(0 ,"getMediaInfo() failed!\n");
        return false;
    }
    
    i4_ret = IMtkPb_Ctrl_SetMediaInfo(fCMPBHandle, &t_mediaInfo);
    if ( i4_ret != IMTK_PB_ERROR_CODE_OK )
    {
        LOG(0 ,"SetMediaInfo ERROR i4_ret = %d- push mode\n", i4_ret);
        return false;
    }

    LOG(5 ,"---[INFO]---SetEngineParam - push mode\n");
    IMTK_PB_CTRL_ENGINE_PARAM_T   t_parm;
    memset(&t_parm, 0, sizeof(t_parm));
    t_parm.u4PlayFlag = IMTK_PB_CTRL_PLAY_FLAG_VIDEO | IMTK_PB_CTRL_PLAY_FLAG_AUDIO;

    i4_ret = IMtkPb_Ctrl_SetEngineParam(fCMPBHandle, &t_parm);
    if ( i4_ret != IMTK_PB_ERROR_CODE_OK )
    {
        LOG(0, "SetEngineParam fail, i4_ret = %d\n", i4_ret);
        return false;
    }
    
    return true;
}

bool PlaylistPlayer::getMediaInfo(uint8_t * pu1buffer, uint  size, IMTK_PB_CTRL_SET_MEDIA_INFO_T &t_mediaInfo)
{
    LOG(5, "getMediaInfo fCMPBHandle = %x - push mode\n", fCMPBHandle);
    IMTK_PB_ERROR_CODE_T i4_ret = IMTK_PB_ERROR_CODE_OK;
    
    IMTK_PB_CTRL_TS_SINGLE_PAT_INFO_T t_tsInfo;
    memset(&t_tsInfo, 0, sizeof(t_tsInfo));   

    i4_ret = IMtkPb_Ctrl_GetTSInfo(fCMPBHandle, pu1buffer, size, &t_tsInfo);
    
    if ( i4_ret != IMTK_PB_ERROR_CODE_OK )
    {
        LOG(0 ,"IMtkPb_Ctrl_GetTSInfo ERROR i4_ret = %d- push mode\n", i4_ret);
        return false;
    }
    
    IMTK_PB_CTRL_VID_ENC_T  eVidEnc = IMTK_PB_CTRL_VID_ENC_UNKNOWN;
    IMTK_PB_CTRL_AUD_ENC_T  eAudEnc = IMTK_PB_CTRL_AUD_ENC_UNKNOWN;
    int32_t i4_Vid_pid = -1;
    int32_t i4_Aud_pid = -1;
    bool b_found = false;
    
    for(unsigned int i = 0; i < t_tsInfo.ui1_pmt_num; i++)
    {  
        for(unsigned int j = 0; j < t_tsInfo.at_pmt_info[i].ui1_strm_num; j++)
        {
            i4_Vid_pid = t_tsInfo.at_pmt_info[i].at_stream_info_list[j].ui2_strm_pid;
            if (i4_Vid_pid < 0)
            {
                continue;
            }
            
            switch(t_tsInfo.at_pmt_info[i].at_stream_info_list[j].ui2_strm_type)
            {
                case 1://STREAM_TYPE_VIDEO_11172_2:
                    eVidEnc = IMTK_PB_CTRL_VID_ENC_MPEG1_2;
                    break;
            
                case 2://STREAM_TYPE_VIDEO_13818_2:
                case 128://STREAM_TYPE_VIDEO_DIGICIPHER_2:
                    eVidEnc = IMTK_PB_CTRL_VID_ENC_MPEG1_2;
                    break;
            
                case 16://STREAM_TYPE_VIDEO_14496_2:
                    eVidEnc = IMTK_PB_CTRL_VID_ENC_MPEG4;
                    break;
            
                case 27://STREAM_TYPE_VIDEO_14496_10:
                    eVidEnc = IMTK_PB_CTRL_VID_ENC_H264;
                    break;
            
                default:
                    break;
            }
            if (IMTK_PB_CTRL_VID_ENC_UNKNOWN == eVidEnc)
            {
                continue;
            }
            
            b_found = true;
            break;
        }
        
        if (true == b_found)
            break;
    }
    
    b_found = false;
    for(unsigned int i = 0; i < t_tsInfo.ui1_pmt_num; i++)
    {  
        for(unsigned int j = 0; j < t_tsInfo.at_pmt_info[i].ui1_strm_num; j++)
        {
            i4_Aud_pid = t_tsInfo.at_pmt_info[i].at_stream_info_list[j].ui2_strm_pid;
            if (i4_Aud_pid < 0)
            {
                continue;
            }
            
            switch(t_tsInfo.at_pmt_info[i].at_stream_info_list[j].ui2_strm_type)
            {
                case 3://STREAM_TYPE_AUDIO_11172_3:
                    eAudEnc = IMTK_PB_CTRL_AUD_ENC_MPEG;
                    break;
            
                case 4://STREAM_TYPE_AUDIO_13818_3:
                    eAudEnc = IMTK_PB_CTRL_AUD_ENC_MPEG;
                    break;
            
                case 15://STREAM_TYPE_AUDIO_13818_7:
                    eAudEnc = IMTK_PB_CTRL_AUD_ENC_AAC;
                    break;
            
                case 17://STREAM_TYPE_AUDIO_14496_3:
                    eAudEnc = IMTK_PB_CTRL_AUD_ENC_AAC;
                    break;
            
                case 129://STREAM_TYPE_AUDIO_ATSC_A53:
                    eAudEnc = IMTK_PB_CTRL_AUD_ENC_DD;
                    break;
            
                case 130://STREAM_TYPE_AUDIO_MSRT24:
                    eAudEnc = IMTK_PB_CTRL_AUD_ENC_DTS;
                    break;
                    
                case 131://STREAM_TYPE_AUDIO_ATT_LABS_G729A:
                    eAudEnc = IMTK_PB_CTRL_AUD_ENC_DD;
                    break;
                    
                case 134://STREAM_TYPE_AUDIO_DTS_GSM610:
                    eAudEnc = IMTK_PB_CTRL_AUD_ENC_DTS;
                    break;
            
                default:
                    break;
            }
            if (IMTK_PB_CTRL_AUD_ENC_UNKNOWN == eAudEnc)
            {
                continue;
            }
            
            b_found = true;
            break;
        }
        
        if (true == b_found)
            break;
    }
    
    memset(&t_mediaInfo, 0, sizeof(IMTK_PB_CTRL_SET_MEDIA_INFO_T));
    t_mediaInfo.eMediaType = IMTK_PB_CTRL_MEDIA_TYPE_MPEG2_TS ;
    t_mediaInfo.u4TotalDuration = 0xFFFFFFFF; //49 * 90000;
    t_mediaInfo.u8Size = -1;
    t_mediaInfo.uFormatInfo.tTsInfo.eVidInfo.eVidEnc = eVidEnc;
    t_mediaInfo.uFormatInfo.tTsInfo.eVidInfo.u2Pid   = i4_Vid_pid;
    t_mediaInfo.uFormatInfo.tTsInfo.eAudInfo.eAudEnc = eAudEnc;
    t_mediaInfo.uFormatInfo.tTsInfo.eAudInfo.u2Pid   = i4_Aud_pid;
    
    if (188 == t_tsInfo.ui2_packet_size)
        t_mediaInfo.uFormatInfo.tTsInfo.ePacketType = IMTK_PB_CTRL_MPEG2_TS_PACKET_TYPE_188BYTE;
    else
        t_mediaInfo.uFormatInfo.tTsInfo.ePacketType = IMTK_PB_CTRL_MPEG2_TS_PACKET_TYPE_192BYTE;
        
    LOG(0 ,"---[INFO]---TS MEDIA INFO:packet type:%d!\n", t_tsInfo.ui2_packet_size);
    LOG(0 ,"---[INFO]---TS MEDIA INFO:video codec:%d!\n", (int)eVidEnc);
    LOG(0 ,"---[INFO]---TS MEDIA INFO:video pid:%d!\n", (int)i4_Vid_pid);
    LOG(0 ,"---[INFO]---TS MEDIA INFO:audio codec:%d!\n", (int)eAudEnc);
    LOG(0 ,"---[INFO]---TS MEDIA INFO:audio pid:%d!\n", (int)i4_Aud_pid);
    
    return true;
}

void PlaylistPlayer::sendKeyInfo(uint8_t * puKey, uint  uiKeyLen, uint8_t * puIV, uint  uiIVLen)
{
    memset(paKey, 0, sizeof(paKey));
    memset(paIV, 0, sizeof(paIV));

    memcpy(paKey, puKey, 16);
    memcpy(paIV, puIV, 16);

    bHaveToSetKey = true;

}

bool PlaylistPlayer::setKeyInfo()
{
     //send aes key to cmpb
    IMTK_PB_ERROR_CODE_T ret = IMTK_PB_ERROR_CODE_OK;
 
    IMTK_PB_CTRL_IBC_PARAM_SET_ENCRYPT_INFO t_encrypt_info;

	memset(&t_encrypt_info, 0, sizeof(t_encrypt_info));
	t_encrypt_info.eEncryptMode = IMTK_PB_CTRL_IBC_ENCRYPT_MODE_AES_CBC;
	t_encrypt_info.eRtbMode = IMTK_PB_CTRL_IBCC_RTB_MODE_SCTE52;

	t_encrypt_info.uEncryptInfo.tAes.u1Mask=(IMTK_PB_CTRL_AES_ENCRYPT_IV | IMTK_PB_CTRL_AES_ENCRYPT_EVEN);
	t_encrypt_info.uEncryptInfo.tAes.u1KeyLen = 16;
	t_encrypt_info.uEncryptInfo.tAes.fgWarpKey = 0;
	t_encrypt_info.uEncryptInfo.tAes.fgWarpIV = 0;
	memcpy(t_encrypt_info.uEncryptInfo.tAes.au1IV, paIV, 16);
    memcpy(t_encrypt_info.uEncryptInfo.tAes.au1Odd_key, paKey, 16);
    memcpy(t_encrypt_info.uEncryptInfo.tAes.au1EvenKey, paKey, 16);

    ret = IMtkPb_Ctrl_SetEncryptInfo(fCMPBHandle, &t_encrypt_info);
    if (ret != IMTK_PB_ERROR_CODE_OK)                                
    {
        fprintf(stderr, "IMtkPb_Ctrl_SetEncryptInfo() Failed as %d!\n", ret);
    }

    memset(&t_encrypt_info, 0, sizeof(t_encrypt_info));
	t_encrypt_info.eEncryptMode = IMTK_PB_CTRL_IBC_ENCRYPT_MODE_AES_CBC;
	t_encrypt_info.eRtbMode = IMTK_PB_CTRL_IBCC_RTB_MODE_SCTE52;

	t_encrypt_info.uEncryptInfo.tAes.u1Mask=(IMTK_PB_CTRL_AES_ENCRYPT_IV | IMTK_PB_CTRL_AES_ENCRYPT_ODD);
	t_encrypt_info.uEncryptInfo.tAes.u1KeyLen = 16;
	t_encrypt_info.uEncryptInfo.tAes.fgWarpKey = 0;
	t_encrypt_info.uEncryptInfo.tAes.fgWarpIV = 0;
	memcpy(t_encrypt_info.uEncryptInfo.tAes.au1IV, paIV, 16);
    memcpy(t_encrypt_info.uEncryptInfo.tAes.au1Odd_key, paKey, 16);
    memcpy(t_encrypt_info.uEncryptInfo.tAes.au1EvenKey, paKey, 16);

    ret = IMtkPb_Ctrl_SetEncryptInfo(fCMPBHandle, &t_encrypt_info);
    if (ret != IMTK_PB_ERROR_CODE_OK)                                
    {
        fprintf(stderr, "IMtkPb_Ctrl_SetEncryptInfo() Failed as %d!\n", ret);
    }

    //bHaveToSetKey = false;

    return true;
}

bool PlaylistPlayer::plAESDecrypt(unsigned char * pucKey, unsigned int uiKeyLen,
                                unsigned char * pucIV, unsigned int uiIVLen,
                                unsigned char * pucInData, unsigned int uiInDataLen,
                                unsigned char * pucOutData)
{
    static AES_KEY t_key;
    if (memcmp(paLastAesKey, pucKey, 16) != 0)
    {
        memset(&t_key, 0, sizeof(AES_KEY));
        
        if (AES_set_decrypt_key(pucKey, uiKeyLen<<3, &t_key) < 0)
        {
            fprintf(stderr, "%s:%d unable to set decrypt key in AES\n", __FUNCTION__, __LINE__);
            return false;
        }
        memcpy(paLastAesKey, pucKey, 16);
    }
    
    unsigned char pucIVTmp[16];
    memcpy(pucIVTmp, pucIV, 16);/*IV's value will be changed by aes_cbc_encrypt*/
    AES_cbc_encrypt(pucInData, pucOutData, uiInDataLen, &t_key, pucIVTmp, AES_DECRYPT);
    
    return true;
}

void PlaylistPlayer::setVideoFreeze(bool bFlag)
{
    LOG(5 ,"PlaylistPlayer::setVideoFreeze bFlag = %d)\n", (int)bFlag);

    IMTK_PB_ERROR_CODE_T i4_ret = IMtkPb_Ctrl_SetVideoFreeze(bFlag);
    if (i4_ret != IMTK_PB_ERROR_CODE_OK)
    {
        LOG(0 ,"IMtkPb_Ctrl_SetVideoFreeze return failed:%d!\n", (int)i4_ret);
    }
}

