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
#include "Playlist.h"
#include <string.h>
#include "Log.h"
#include <arpa/inet.h>
#include <algorithm>
#include <unistd.h>

#ifdef PRINT_TIME
#include <sys/time.h>
#include <time.h>
#endif

using namespace std;
using namespace hls;

#define MAX_COUNT 8         //20
#define PUSH_THRESHOLD  64 * 1024

string Playlist::mtkUAStr;
PlayerProxy::~PlayerProxy()
{
}
PlaylistStream::~PlaylistStream()
{
}
StreamSelectorAbstract::~StreamSelectorAbstract()
{
}


static bool AESDecrypt(unsigned char * pucKey, unsigned int uiKeyLen,
                       unsigned char * pucIV, unsigned int uiIVLen,
                       unsigned char * pucInData, unsigned int uiInDataLen,
                       unsigned char * pucOutData);

static bool AESDecrypt(unsigned char * pucKey, unsigned int uiKeyLen,
                       unsigned char * pucIV, unsigned int uiIVLen,
                       unsigned char * pucInData, unsigned int uiInDataLen,
                       unsigned char * pucOutData)
{
    static AES_KEY t_key;

    memset(&t_key, 0, sizeof(AES_KEY));

    if (AES_set_decrypt_key(pucKey, uiKeyLen<<3, &t_key) < 0)
    {
        fprintf(stderr, "%s:%d unable to set decrypt key in AES\n", __FUNCTION__, __LINE__);
        return false;
    }

    AES_cbc_encrypt(pucInData, pucOutData, uiInDataLen, &t_key, pucIV, AES_DECRYPT);

    return true;
}

class StreamImpl : public PlaylistStream, public STREAM_INFO
{
public:
    virtual int getBandwith()
    {
        return pstrm->bandwidth;
    }

    virtual std::string getCodecs()
    {
        return std::string(pstrm->codecs);
    }
    void applyMedia(MEDIA_INFO * media)
    {
        media_info = media;
    }

    MEDIA_INFO * currentMedia()
    {
        return  media_info;
    }

    STREAM_INFO * getRawData()
    {
        return pstrm;
    }

    StreamImpl(struct STREAM_INFO * strm) : pstrm(strm), media_info(NULL)
    {
    }
private:
    struct STREAM_INFO * pstrm;
    struct MEDIA_INFO * media_info;
};

static unsigned int guiWaitedDuration = 0;

Playlist * Playlist::createPlaylist(const std::string & playlist_uri, StreamSelectorAbstract * selector)
{
    std::string url;
    string::size_type ua_pos = playlist_uri.find("?mtkUAString=");
    if (ua_pos == string::npos)
    {
        url = playlist_uri;
        mtkUAStr = string("");
    }
    else
    {
        url.assign(playlist_uri, 0, ua_pos);
        mtkUAStr = std::string(playlist_uri, ua_pos + strlen("?mtkUAString="));
    }

    fprintf(stderr, "url = %s\n", url.c_str());
    // realize uri: uri or playlist uri.
    HLS_PLAYLIST * pt_playlist = new_playlist(url.c_str()); // jinlong comment: pt_playlist which is allocated memory maybe no free after used.
    if (pt_playlist == NULL)
    {
        return NULL;
    }
    if ( get_playlist_type(pt_playlist) == PLAYLIST_UNKNOW)
    {
        LOG(5, "Cannot get playlist type by file extension!!\n\n");
        return NULL;            // todo... need free pt_hls_list
    }

    return new Playlist(pt_playlist, url, selector);
}

Playlist::Playlist(HLS_PLAYLIST * hls, const std::string & ps_uri, StreamSelectorAbstract * selector)
    : pt_hls_list(hls), pt_cur_hls_list(hls), pplayer(NULL), consumed_count(0), pending(false), uri(ps_uri), cached_count(0), downloader(http_live_streaming_thread), pusher(Playlist::pushmode_thread), alive(false), realized(false), strmSelector(selector), m_needSelectStream(false)
{
    paused = false;
    resetPushStatus = false;
    resetDownload = false;
    pt_cur_media = NULL;
    pt_checked_media = NULL;
    pt_hls_list->controller = this;
    pt_hls_list->currentStream = NULL;
    complete = false;
    total_memory = 0;
    LOG(5, "strmSelector = %p\n", strmSelector);

}

HLS_PLAYLIST * Playlist::getHLSPlaylist()
{
    /* return root playlist */
    return pt_hls_list;
}

HLS_PLAYLIST * Playlist::getCurHLSPlaylist()
{
    /* get current playlist */
    return pt_cur_hls_list;
}

void Playlist::setCurHLSPlaylist(HLS_PLAYLIST * hls)
{
    /* set current playlist */
    pt_cur_hls_list = hls;
}

bool Playlist::isAlive()
{
    return alive;
}

void Playlist::setRealized(bool bRealized)
{
    realized = bRealized;
}

void Playlist::stop()
{
    
    syncBegin();
    paused = false;
    resetDownload = false;
    alive = false;
    notifyAll();                // notify popDownloadTask to wake up if it is blocked
    syncEnd();
    
    stop_download();
    
    // todo ...  other
    LOG(0, "Before notifyAll!\n\n");
    pushWaiter.syncBegin();
    pushWaiter.notifyAll();     // notify push mode thread to wake up from waiting for cach status.
    pushWaiter.syncEnd();

    LOG(0, "Before notify mediaDataLocker!\n\n");
    //mediaDataLocker.syncBegin();
    mediaDataLocker.notifyAll();// notify push thread to wake from waiting for update status
    //mediaDataLocker.syncEnd();

    LOG(0, "Before downloader.stop()!\n");
    downloader.stop();
    LOG(0, "After downloader.stop()!\n");
    pusher.stop();
    LOG(0, "After pusher.stop()!\n");
}

/* decide which is next media file need to be download */
MEDIA_INFO * Playlist::popDownloadTask()
{
    MEDIA_INFO * pt_media;
    HLS_PLAYLIST * pt_download_hls = getCurHLSPlaylist();
    
    // once the number of  cached media file is too large, maybe because the network is so widthen or the player is paused so that  consume data slowly. Then, download end / thread need to wait some while.
    syncBegin();
    if (! isAlive())
    {
        syncEnd();
        return NULL;
    }

     syncEnd();
     
    // check from current media.
    while(true)
    {
        cached_count = 0;

        if (pt_cur_media == NULL)
        {
            if (complete == true)
            {
                return NULL;
            }
            pt_media = get_next_media_file(pt_download_hls, NULL);
        }
        else
        {
            pt_media = pt_cur_media;    // current media shouldn't be true at any time.
        }

        while(pt_media != NULL)
        {
            LOG(5, "%s: pt_media = %s\n", __FUNCTION__, pt_media->ps_url);

            if (pt_media->switchStream == true)
            {
                LOG(5, "%s: need to switch after pt_media = %s\n", __FUNCTION__, pt_media->ps_url);
                pt_download_hls = pt_hls_list->currentStream->pt_playlist;
                if (pt_download_hls)
                {
                    pt_media = findMediaBySeq(pt_download_hls, pt_media->sequence_number);
                    /* this means new stream do not have this sequence number media file, get from first!! */
                    if (NULL == pt_media)
                    {
                        pt_media = get_next_media_file(pt_download_hls, pt_media);
                    }
                }
                else
                {
                    /* prepare new stream hls */

                    // haven't realized the playlist of this stream yet.
                    pt_hls_list->currentStream->pt_playlist = new_playlist(pt_hls_list->currentStream->ps_url);

                    if (pt_hls_list->currentStream->pt_playlist == NULL)
                    {
                        LOG(0, "switch stream fail due to new_playlist fail!!\n");
                    }
                    else
                    {
                        if(request_playlist(pt_hls_list->currentStream->pt_playlist, 30) != HLS_OK)
                        {
                            free_playlist(pt_hls_list->currentStream->pt_playlist);
                            pt_hls_list->currentStream->pt_playlist = NULL;
                            LOG(0, "switch stream fail due to request_playlist fail!!\n");
                        } // this playlist can't be downloaded.
                        else
                        {
                            LOG(5, "%s: switch to bandwidth %d, media file %d\n",
                                __FUNCTION__, pt_hls_list->currentStream->bandwidth, pt_media->sequence_number);
                            pt_download_hls = pt_hls_list->currentStream->pt_playlist;
                            pt_media = findMediaBySeq(pt_download_hls, pt_media->sequence_number);
                            LOG(5, "pt_download_hls= %p\n", pt_download_hls);
                            /* this means new stream do not have this sequence number media file, get from first!! */
                            if(NULL == pt_media)
                            {
                                pt_media = get_next_media_file(pt_download_hls, pt_media);
                            }

                        }
                    }
                }
            }  /* end of if */

            if (pt_media == NULL)
            {
                /* error handling - either update playlist again or exit */
                LOG(0, " something wrong !!");
                dump_playlist(pt_download_hls, DUMP_CONDITION_ALL, DUMP_TO_CONSOLE);
                break;
            }

            if (true == resetDownload)
            {
                if (pt_media != pt_cur_media)
                {
                    pt_media = pt_cur_media;
                    resetDownload = false;
                    continue;
                }
                resetDownload = false;
                //mediaDataLocker.syncBegin();
                mediaDataLocker.notifyAll();// notify push thread to wake from waiting for update status
                //mediaDataLocker.syncEnd();                    
            }   
            
            LOG(6, "Media file status = %d\n", pt_media->status);
            if ((pt_media->status != MEDIA_FILE_CACHED) &&
                    (pt_media->status != MEDIA_FILE_CONSUMED))
            {
                LOG(6, "status != MEDIA_FILE_CACHED\n");
                cached_count = 0; // so not to disturb the checking of nextToPlay's notification.                
                return pt_media;
            }
            
            // next to be play which followed the current media.
            if (pt_media->status == MEDIA_FILE_CACHED)
            {
                cached_count ++;
                LOG(5, "Current cached_count = %d!!!\n", cached_count);
            }            

                       
            if (cached_count >= MAX_COUNT)
            {
                // too many data hasn't been consumed, due to not ocupy too many memory, wait for consumed some media file.
                LOG(5, "cached_count > %d | pop wait1", MAX_COUNT);
                syncBegin(); 
                wait();
                syncEnd();
                LOG(5, "cached_count > %d | pop wait1 end", MAX_COUNT);
                if (! isAlive())
                {
                    LOG(0, "pop terminate1");
                    syncEnd(); 
                    return NULL;
                }              
                // after have waken up from a deep sleep, the media info should be checked from the current media.
                pt_media = pt_cur_media;    // current media shouldn't be true at any time.
                cached_count = 0;           // reset and count again.          
                continue;
            }
            

            pt_media = get_next_media_file(pt_download_hls, pt_media);

            if (pt_media == NULL)
            {

                /*may be the end of the playlist, try to update it*/                
                if (pt_download_hls->ext_x_endlist == 1)
                {
                    LOG(0, "Playlist download complete!!");
                    return NULL;
                }
                if (true == update_m3u_playlist())
                {
                    LOG(5, "pop again");
                    return popDownloadTask();
                }
               
            }
        }

        if (! isAlive())
        {
            LOG(0, "pop terminate2\n");            
            return NULL;
        }  

        // here is not cached pt_media_info can't be find, so block itself and wait for wake up.
        syncBegin();
        LOG(5, "pop wait2");
        if (guiWaitedDuration == 0)
            wait();
        else
            timedwait(guiWaitedDuration);/*wait for nex update*/
        syncEnd();
        LOG(5, "pop wait2 end");

    }

}

Playlist::~Playlist()
{
    // todo ...
    // 1. release playlist
    // 2. destroy http live streaming thread
    // 3. destory push mode thread.
    free_playlist(pt_hls_list);
}

/*
bool Playlist::equal(const std::string & playlist_uri)
{
    return uri == playlist_uri;
}
*/

std::string Playlist::getUri()
{
    return uri;
}

void Playlist::releasePlaylist( Playlist * pt_playlist)
{
    // once this function is called, pt_playlist shouldn't be used again.
    pt_playlist->stop();
    delete (pt_playlist);          // ~Playlist() will be called.
    pt_playlist = NULL;
}


bool Playlist::isPaused()
{
    while ( (true == paused) || (true == resetDownload) )
    {
        LOG(0, "pause playlist !!!!\n");
        mediaDataLocker.wait();// notify push thread to wake from waiting for update status    
    }    
    return paused;
}

void Playlist::pause()
{
    paused = true;
}

void Playlist::resume()
{
    paused = false;

    syncBegin();
    notifyAll();                // notify popDownloadTask to wake up if it is blocked
    syncEnd();    

    //mediaDataLocker.syncBegin();
    mediaDataLocker.notifyAll();
    //mediaDataLocker.syncEnd();    
    
    LOG(0, "resume playlist  !!!\n");
}

bool Playlist::realize()
{
    if (realized)
    {
        if (false == pusher.isAlive())
        {
            if (! pusher.start(this))
            {
                pplayer->notifyStatus(PLAYLIST_STATUS_CORE_ERROR);
                return false;
            }
        }
        pplayer->notifyStatus(PLAYLIST_STATUS_REALIZED);
        return true;            // avoid to realize more times.
    }



    if ( HLS_FAIL == request_playlist(pt_hls_list, MIN_REQ_CNT))
    {
        LOG(0, "playlist %s download fail!!\n", uri.c_str());
        pplayer->notifyStatus(PLAYLIST_STATUS_CANNOT_GET_PLAYLIST);
        return false;
    }


    /* stream */
    if ((pt_hls_list->playlistContentType == TYPE_PROGRAM) && (pt_hls_list->duration == 0))
    {   
        struct STREAM_INFO * pt_stream_info = NULL;        
        struct ENTRY_INFO *ptr = (struct ENTRY_INFO *)pt_hls_list->pv_entry_head;;        
        if (ptr && (ptr->type == TYPE_STREAM))
        {
            pt_stream_info = (struct STREAM_INFO *)ptr;
            /* get playlist */
            if (pt_stream_info && (pt_stream_info->pt_playlist == NULL))
            {
    
                pt_stream_info->pt_playlist = new_playlist(pt_stream_info->ps_url);
    
                if (pt_stream_info->pt_playlist != NULL)
                {
                    request_playlist(pt_stream_info->pt_playlist, 30);
                    pt_hls_list->duration = pt_stream_info->pt_playlist->duration;
                    pt_cur_hls_list = pt_stream_info->pt_playlist;
                    LOG(0, "previous pt_hls_list = %p , pt_cur_hls_list = %p !!\n", pt_hls_list,  pt_cur_hls_list);
                }             
                
            } /* get next entry from playlist */    
        } /* end of if */
    }       


#ifdef PRINT_TIME
    struct timeval timeV = {0};
    gettimeofday(&timeV, NULL);
    LOG(-1, "[2]Current time: [%d]s.[%d]ms!!!\n", timeV.tv_sec, timeV.tv_usec / 1000);
#endif
#if 1
    optimize_playlist(pt_hls_list, 30);
#endif

    // only after playlist is realized, the pusher is able to start.
    if (! pusher.start(this))
    {
        pplayer->notifyStatus(PLAYLIST_STATUS_CORE_ERROR);
        return false;
    }
    setRealized(true);
    pplayer->notifyStatus(PLAYLIST_STATUS_REALIZED);

    return true;
}

bool Playlist::startEngine(PlayerProxy * player)
{
    pplayer = player;
    alive = true;

    LOG(0, "----[INFO]----Start engine!\n");
    start_download();
    if( ! downloader.start(this))
    {
        stop_download();
        LOG(0, "----[ERR]----downloader.start fail!\n");
        return false;
    }
#if 0 /* MTK: Remove by Jinlong, delay to start pusher when the playlist is downloaded, which will be done in downloader. */
    if (! pusher.start(this)) return false;
#endif
    return true;
}


bool Playlist::isMediaItem(const std::string & uri)
{
    pt_checked_media = findMedia(uri);

    return pt_checked_media == NULL ? false : true;
}

MEDIA_INFO * Playlist::findMedia(const std::string & uri)
{
    struct ENTRY_INFO * ent = NULL;
    // first to check next media info, second the current, third the previous, last others.
#if 0
    if (uri.compare((char*)pt_cur_media->ps_url) == 0) return pt_cur_media;
    if (uri.compare((char*)(MINFO_NEXT(pt_cur_media)->ps_url)) == 0)  return MINFO_NEXT(pt_cur_media);
    if (uri.compare((char*)(MINFO_PREV(pt_cur_media)->ps_url)) == 0) return MINFO_PREV(pt_cur_media);
#endif
    ent = (ENTRY_INFO*)pt_hls_list->pv_entry_head;
    while(ent != NULL)
    {
        if (ent->type == TYPE_MEDIA)
        {
            MEDIA_INFO* minfo = ( MEDIA_INFO*)ent;
            if (uri.compare((char*)minfo->ps_url) == 0)
            {
                return minfo;
            }
        }
        ent = ent->next;
    }
    return NULL;
}


MEDIA_INFO * Playlist::findMediaByTime(const int time, int * realTime)
{
    struct ENTRY_INFO * ent = NULL;
    float subTime = 0.0;
    int index = 0;

    ent = (ENTRY_INFO*)pt_cur_hls_list->pv_entry_head;
    while (ent != NULL)
    {
        if (ent->type == TYPE_MEDIA)
        {
            MEDIA_INFO* minfo = ( MEDIA_INFO*)ent;
            LOG(6, "----[INFO]----Current meida's duration is %d!\n", minfo->duration);
            subTime += minfo->duration;

            index++;
            if ((int)subTime >= time)
            {
                LOG(5, "---[INFO]---Current seek index is [%d] total[%d] files!", index, pt_hls_list->i4_numOfEntry);
                *realTime = (int)subTime-minfo->duration;
                return minfo;
            }
        }
        ent = ent->next;
    }

    return NULL;
}

MEDIA_INFO * Playlist::findMediaByTime(const int time)
{
    struct ENTRY_INFO * ent = NULL;
    float subTime = 0.0;
    int index = 0;
    
    ent = (ENTRY_INFO*)pt_hls_list->pv_entry_head;
    while (ent != NULL)
    {
        if (ent->type == TYPE_MEDIA)
        {
            MEDIA_INFO* minfo = ( MEDIA_INFO*)ent;
            LOG(6, "----[INFO]----Current meida's duration is %d!\n", minfo->duration);
            subTime += minfo->duration;

            index++;
            if ((int)subTime >= time)
            {
                LOG(0, "---[INFO]---Current seek index is [%d] total[%d] files!", index, pt_hls_list->i4_numOfEntry);
                return minfo;
            }
        }
        ent = ent->next;
    }

    return NULL;
}

/* find specific media file from specific playlist */
MEDIA_INFO * Playlist::findMediaBySeq(HLS_PLAYLIST * pt_playlist, int seq)
{
    MEDIA_INFO * pt_media;
    struct ENTRY_INFO * ptr = NULL;

    ptr = (struct ENTRY_INFO *)pt_playlist->pv_entry_head;
    while(ptr != NULL)
    {
        if (ptr->type == TYPE_MEDIA)
        {
            pt_media = (MEDIA_INFO *) ptr;
            if (pt_media->sequence_number == seq)
            {
                return (pt_media);
            }
        }
        ptr = ptr->next;
    }
    return NULL;
}

bool Playlist::select(const std::string & uri)
{
    syncBegin();

    // confirm the uri is of pt_checked_media
    if (pt_checked_media && (uri.compare((char*)pt_checked_media->ps_url) == 0))
    {
        pt_cur_media = pt_checked_media;
    }
    else
    {
        // need find other matched one.
        pt_checked_media = findMedia(uri);
        if (pt_checked_media == NULL)
        {
            LOG(0, "----[INFO]----pt_checked_media is NULL!\n");
            syncEnd();

            return false;       // hasn't found this media.
        }
        pt_cur_media = pt_checked_media;
    }
    // make pt_cur_media to be enable
    // todo ...   both threads need to be improved
    // cancel previous one first
    pending = true;
    notifyAll();                // notify popDownloadTask to wake up if it is blocked    
    syncEnd();

    pushWaiter.syncBegin();
    pushWaiter.notifyAll();
    pushWaiter.syncEnd();

    //mediaDataLocker.syncBegin();
    mediaDataLocker.notifyAll();// notify push thread to wake from waiting for update status
    //mediaDataLocker.syncEnd();

    return true;
}

bool Playlist::select(MEDIA_INFO * minfo)
{

    if (minfo == NULL)
    {        
        LOG(0, "----[INFO]----minfo is NULL!\n");
        return false;       // hasn't found this media.
    }

    syncBegin();
    
    if (pt_cur_media != minfo)
    {
        LOG(0, "----[INFO]----original pt_cur_media is [%s], new [%s]!\n", (pt_cur_media ? pt_cur_media->ps_url : "NULL"), minfo->ps_url);    
        pt_cur_media = minfo;
        resetPushStatus = true;    
        resetDownload = true;
    }    
    pending = true;
    notifyAll();                // notify popDownloadTask to wake up if it is blocked    
    syncEnd();
    
    LOG(5, "----[INFO]----pt_cur_media is [%p]!\n", pt_cur_media);

    pushWaiter.syncBegin();
    pushWaiter.notifyAll();
    pushWaiter.syncEnd();

    //mediaDataLocker.syncBegin();
    mediaDataLocker.notifyAll();// notify push thread to wake from waiting for update status
    //mediaDataLocker.syncEnd();

    return true;
}

bool Playlist::needSelectStream()
{
    return m_needSelectStream;
}

void Playlist::selectStream(struct MEDIA_INFO * pt_media )
{

    StreamArray::iterator it;
    strmSelector->selectStream(streams, pt_media->downloadSpeed, &it);

    StreamImpl * stream = (StreamImpl*)*it;
    if (stream == NULL)
    {
        LOG(0, " switch stream fail!! \n");
        return;
    }

    STREAM_INFO * pt_newStream = stream->getRawData();

    if (pt_hls_list->currentStream != pt_newStream) // this should be used in get_next_media_file
    {
        /* need to switch stream */
        LOG(0, "need to switch stream to %d!!\n", stream->getRawData()->bandwidth);

        //setCurHLSPlaylist(pt_newStream->pt_playlist);
        pt_hls_list->currentStream = pt_newStream;
        pt_media->switchStream = true;
    }
    else
    {
        /*same stream, no need to switch stream */
    }

}

void Playlist::selectStreamFailed(STREAM_INFO * strm)
{
    // this stream can't work (such as, playlist can't be downloaded)
    // move it to the last of the list
    // so next select can be make scense that this stream won't be select (except only one stream).

}

void Playlist::realizeStream(HLS_PLAYLIST * plist)
{
    LOG(6, "\n");

    ENTRY_INFO * ptr = (struct ENTRY_INFO*)plist->pv_entry_head;
    if (ptr && ptr->type == TYPE_STREAM)
    {
        // there are streams to be applied.
        // old streams should be cleared.
        StreamArray::iterator it;

        for (it = streams.begin(); it != streams.end(); ++it)
        {
            PlaylistStream * stream = *it;
            delete stream;
        }
        streams.clear();
    }

    while (ptr && ptr->type == TYPE_STREAM)
    {
        // means this playlist contains more than once stream (generally, one stream no need to nested to a playlist)
        STREAM_INFO * strm = (struct STREAM_INFO*) ptr;
        StreamImpl *pstrm = new StreamImpl(strm);
        streams.push_back(pstrm);

        ptr = ptr->next;
    }

    if ((streams.size() > 1) && (strmSelector != NULL))
    {
        strmSelector->sortStreams(streams);
        m_needSelectStream = true;
    }


    // have done to realize stream if needed.
}

MEDIA_INFO * Playlist::nextToPlay()
{
    // if return NULL, means no one should to be play, the playlist playback shall end.
    syncBegin();
    if (!alive)
    {
        notifyAll();
        syncEnd();
        return NULL;            // to make pusher terminate
    }
    
    if (pending)
    {
        pending = false;
        notifyAll();            // notify popDownloadTask to recognize
        syncEnd();

        LOG(0, "----[INFO]----pt_cur_media is [%s]!\n", pt_cur_media->ps_url);

        return pt_cur_media;
    }
    else
    {

        HLS_PLAYLIST * pt_download_hls = NULL;

        // following the normal rule: play next one in the list (means no user interrupting)
        if (pt_cur_media && pt_cur_media->switchStream == true)
        {
            LOG(5, "%s: need to switch after pt_media = %s\n", __FUNCTION__, pt_cur_media->ps_url);
            pt_download_hls = pt_hls_list->currentStream->pt_playlist;
            if (pt_download_hls)
            {
                LOG(5, "%s: switch to bandwidth %d, media file %d\n",
                    __FUNCTION__, pt_hls_list->currentStream->bandwidth, pt_cur_media->sequence_number);
                pt_cur_media = findMediaBySeq(pt_download_hls, pt_cur_media->sequence_number);
                setCurHLSPlaylist(pt_download_hls);
            }
            else
            {
                /* new stream not be create completed yet */
                LOG(5, "new stream not be create completed yet!!\n");
                // haven't realized the playlist of this stream yet.
                pt_hls_list->currentStream->pt_playlist = new_playlist(pt_hls_list->currentStream->ps_url);

                if (pt_hls_list->currentStream->pt_playlist == NULL)
                {
                    LOG(0, "switch stream fail due to new_playlist fail!!\n");
                }
                else
                {
                    if(request_playlist(pt_hls_list->currentStream->pt_playlist, 30) != HLS_OK)
                    {
                        free_playlist(pt_hls_list->currentStream->pt_playlist);
                        pt_hls_list->currentStream->pt_playlist = NULL;
                        LOG(0, "switch stream fail due to request_playlist fail!!\n");
                    } // this playlist can't be downloaded.
                    else
                    {

                        LOG(5, "%s: switch to bandwidth %d, media file %d\n",
                            __FUNCTION__, pt_hls_list->currentStream->bandwidth, pt_cur_media->sequence_number);
                        pt_download_hls = pt_hls_list->currentStream->pt_playlist;
                        dump_playlist(pt_download_hls, DUMP_CONDITION_ALL, DUMP_TO_CONSOLE);
                        pt_cur_media = findMediaBySeq(pt_download_hls, pt_cur_media->sequence_number);
                        setCurHLSPlaylist(pt_download_hls);
                    }
                }
            }
        }

        HLS_PLAYLIST * pt_cur_playlist = getCurHLSPlaylist();
        if (pt_cur_playlist != NULL)
        {

            struct ENTRY_INFO * ptr = (struct ENTRY_INFO *)pt_cur_playlist->pv_entry_tail;
            if (ptr)
           {
                if ( ((ptr->type == TYPE_MEDIA) &&
                        (pt_cur_playlist->pv_entry_tail == pt_cur_media) &&
                        (pt_cur_playlist->ext_x_endlist != 1)) ||
                        ((ptr->type == TYPE_DISCONTINUITY_TAG) &&
                        ((struct MEDIA_INFO *)ptr->prev == pt_cur_media) &&
                         (pt_cur_playlist->ext_x_endlist != 1) )) /*need update*/

                {
                    LOG(5, "should wait for the playlist update\n");
                    syncEnd();
                    return NULL;
                }
            }
        }


        pt_cur_media =  get_next_media_file(pt_cur_hls_list, pt_cur_media);
        // because if the program goes here, means player is playing in order. So cached count can be available.
        if (cached_count < (MAX_COUNT / 2)) // only when cached count > 10 not wake up the download thread. //8 //5
        {
            notifyAll();            // notify popDownloadTask to recognize
        }

        cached_count --;        // no need to care whether it decreased to be less than 0.

        syncEnd();

        while (pt_cur_media == NULL)
        {
            // means the end of the playlist. if here return NULL, pushmode thread shall terminated. so wait user to select other item of the list or terminated/stopped by user through playlist::stop.
            pushWaiter.syncBegin();
            LOG(0, "Before pushWaiter.wait\n");
            pplayer->setPushModeEOS();
            pushWaiter.wait();
            LOG(0, "After pushWaiter.wait\n");
            pushWaiter.syncEnd();
            // waken up.
            // recursively .
            syncBegin();
            if (!alive)
            {
                notifyAll();
                syncEnd();
                return NULL;            // to make pusher terminate
            }
            if (pending)
            {
                pending = false;
                notifyAll();            // notify popDownloadTask to recognize
                syncEnd();
                return pt_cur_media;
            }
            syncEnd();
        }
        
        return pt_cur_media;
    }
    syncEnd();
}


bool Playlist::waitForMedia(MEDIA_INFO * pt_media)
{
    pushWaiter.syncBegin();
    while ((pt_media->status != MEDIA_FILE_CACHED) &&
            (pt_media->status != MEDIA_FILE_CONSUMED))
    {
        // check if cache fail.
        if( (pt_media->status == MEDIA_FILE_CACHE_FAIL) ||
        	(pt_media->status == MEDIA_FILE_DOWNLOAD_CANCEL))
        {
            pushWaiter.syncEnd();
            return false;
        }

        pushWaiter.wait();
        // have waken up from sleep.
        // check if alive.
        if( ! isAlive())
        {
            pushWaiter.syncEnd();
            return false;
        }

    }
    pushWaiter.syncEnd();

    return true;
}

/*------------------------------------------------------------------------
 * Name:  waitForMedia
 *
 * Description: wait threshold percentage of data downloaded of pt_media file
 *
 * Inputs: pt_media - media file wait for
 *            threshold - number of seconds buffer downloaded
 *
 * Outputs: -
 *
 * Returns: -
 -----------------------------------------------------------------------*/
bool Playlist::waitForMedia(MEDIA_INFO * pt_media , int threshold)
{
    pushWaiter.syncBegin();
    while ((pt_media->status != MEDIA_FILE_CACHED) &&
            (pt_media->status != MEDIA_FILE_CONSUMED))
    {
        // check if cache fail.
        if ((pt_media->status == MEDIA_FILE_CACHE_FAIL) || 
        	(pt_media->status == MEDIA_FILE_DOWNLOAD_CANCEL))
        {
            pushWaiter.syncEnd();
            return false;
        }

        /* modification for early playback - tiffany */        
        if (threshold != 0)
        {	
            /* threshold == 0 means we need whole media file downloaded */ 
            if (pt_media->url_content.i4_used_size > threshold) //((pt_media->content_length/pt_media->duration) * threshold)
            {
                pushWaiter.syncEnd();
                return true;
            }
        }    
        if( ! isAlive())
        {
            pushWaiter.syncEnd();
            return false;
        }

        pushWaiter.wait();
        // have waken up from sleep.
        // check if alive.
        if( ! isAlive())
        {
            pushWaiter.syncEnd();
            return false;
        }

    }
    pushWaiter.syncEnd();

    return true;
}

/*------------------------------------------------------------------------
 * Name:  notifyMediaStatus
 *
 * Description: wait threshold percentage of data downloaded of pt_media file
 *
 * Inputs: pt_media - media file for notify
 *            status - media file status
 *
 * Outputs: -
 *
 * Returns: -
 -----------------------------------------------------------------------*/
void Playlist::notifyMediaStatus(MEDIA_INFO * pt_media, MEDIA_FILE_STATUS status)
{
    // notify media has been cached.
    pushWaiter.syncBegin();
    pt_media->status = status;
    LOG(8, "%s: [ps_url]%s, [status]%d!!\n", __FUNCTION__, pt_media->ps_url, status);
    pushWaiter.notifyAll();
    pushWaiter.syncEnd();
}

void Playlist::analysisMemUsage()
{
    HLS_PLAYLIST *pt_cur_playlist = getCurHLSPlaylist();
    if (consumed_count > 4)
    {
        //free some media's data
        LOG(6, "free some media's data\n");
        consumed_count -= free_some_media_data(pt_cur_playlist, 2);
    }
}

bool Playlist::update_m3u_playlist()
{

    static int iUpdatedRetryTime = 0;

    HLS_PLAYLIST *pt_cur_playlist = getCurHLSPlaylist();
    pt_cur_playlist->dynamic_update_count++;
    LOG(5, "*** update started %s, (%d)!!***\n",
        pt_cur_playlist->ps_url, pt_cur_playlist->dynamic_update_count);

    if (NULL == pt_cur_playlist)
    {
        LOG(0, "cann't to update,exit!!***\n");
        guiWaitedDuration = 0; /*no more update waited*/
        return false;
    }
    if (pt_cur_playlist->ext_x_endlist == 1)
    {
        LOG(0, "doesn't need to update,exit!!***\n");
        guiWaitedDuration = 0; /*no more update waited*/
        return false;
    }

    HLS_PLAYLIST * pt_playlist_tmp = new_playlist(pt_cur_playlist->ps_url);
    if (pt_playlist_tmp == NULL)
    {
        LOG(0, "get new_playlist fail!!\n");
        return false;
    }

    if (HLS_FAIL == request_playlist(pt_playlist_tmp, MIN_REQ_CNT))
    {
        LOG(0, "new playlist %s download fail!!\n", pt_playlist_tmp->ps_url);
        free_playlist(pt_playlist_tmp);
        return false;
    }

    MEDIA_INFO* minfo_first = getFirstMedia(pt_playlist_tmp);
    if (minfo_first == NULL)
    {
        LOG(0, "playlist format is error!\n");
        free_playlist(pt_playlist_tmp);
        return false;
    }

    if (pt_cur_playlist->playlistContentType == TYPE_EVENT_SLIDING)
    {
        LOG (5, "--TYPE_EVENT_SLIDING-- \n");
        MEDIA_INFO* minfo_in_old = getMediaLocation(pt_cur_playlist, minfo_first->ps_url);

        /*update the new playlist's sequence number*/
        if (pt_playlist_tmp->ext_x_media_sequence == 0)
        {
            if (minfo_in_old == NULL)
            {
                LOG(0, "new playlist data is error!\n");
                free_playlist(pt_playlist_tmp);
                return false;
            }

            pt_playlist_tmp->ext_x_media_sequence = minfo_in_old->sequence_number;
        }

        if ((pt_playlist_tmp->ext_x_media_sequence == pt_cur_playlist->ext_x_media_sequence) &&
                (pt_playlist_tmp->i4_numOfEntry == pt_cur_playlist->i4_numOfEntry))
        {
            LOG(0, "new playlist %s is the same sa the last one!!\n", pt_playlist_tmp->ps_url);
            free_playlist(pt_playlist_tmp);
            LOG(5, "current update retry time:%d!!\n", iUpdatedRetryTime);
            LOG(5, "current waited duration:%d!!\n", guiWaitedDuration);
            LOG(5, "current playlist target duration:%d!!\n", pt_cur_playlist->ext_x_targetduration);
            iUpdatedRetryTime++;
            if (iUpdatedRetryTime == 1)
                guiWaitedDuration = pt_cur_playlist->ext_x_targetduration / 2;
            else if (iUpdatedRetryTime == 2)
                guiWaitedDuration = (pt_cur_playlist->ext_x_targetduration * 3) / 2;
            else
                guiWaitedDuration = pt_cur_playlist->ext_x_targetduration * 3;

            return false;
        }

        ENTRY_INFO * ent = (ENTRY_INFO *)pt_playlist_tmp->pv_entry_head;
        int i = 0;
        //mediaDataLocker.syncBegin();

        if ((NULL == minfo_in_old))
        {
            LOG(5, "reset current media to begin!\n");
            pt_cur_media = NULL;
        }

        while(ent != NULL)
        {
            if (ent->type == TYPE_MEDIA)
            {
                MEDIA_INFO* minfo_in_new = ( MEDIA_INFO*)ent;

                /*should reset the media's sequence number in this new playlist*/
                minfo_in_new->sequence_number = pt_playlist_tmp->ext_x_media_sequence + i;
                i++;

                if (NULL != minfo_in_old)
                {
                    LOG(6, "minfo_in_new sequence number is %d!\n", minfo_in_new->sequence_number);
                    LOG(6, "minfo_in_old sequence number is %d!\n", minfo_in_old->sequence_number);
                    if (minfo_in_new->sequence_number == minfo_in_old->sequence_number)
                    {
                        minfo_in_new->content_length = minfo_in_old->content_length;
                        minfo_in_new->result_code = minfo_in_old->result_code;
                        minfo_in_new->status = minfo_in_old->status;
                        moveCharBuffer(minfo_in_new->url_content, minfo_in_old->url_content);
                        moveCharBuffer(minfo_in_new->url_header, minfo_in_old->url_header);

                        ENTRY_INFO * ent_2 = (ENTRY_INFO *)minfo_in_old;
                        minfo_in_old = (MEDIA_INFO*)ent_2->next;
                    }

                    if (pt_cur_media != NULL)
                    {
                        if (pt_cur_media->sequence_number == minfo_in_new->sequence_number)
                        {
                            LOG(5, "reset current media, sequence number is %d!\n", pt_cur_media->sequence_number);
                            pt_cur_media = minfo_in_new;
                        }
                        if (pt_cur_media->sequence_number < minfo_in_new->sequence_number)
                        {
                            LOG(5, "reset current media, sequence number is %d!\n", minfo_first->sequence_number);
                            pt_cur_media = minfo_first;
                        }
                    }
                }
            }
            ent = ent->next;
        }
    }
    else if (pt_cur_playlist->playlistContentType == TYPE_EVENT_APPENDING)
    {
        LOG (5, "--TYPE_EVENT_APPENDING-- \n");
        /* reuse downloaded data */

        pt_playlist_tmp->timeBuffered = pt_cur_playlist->timeBuffered;

        /* update pt_cur_media */
        MEDIA_INFO* minfo_in_new = getMediaLocation(pt_playlist_tmp, pt_cur_media->ps_url);
        pt_cur_media = minfo_in_new;
    }
    update_playlist(pt_cur_playlist, pt_playlist_tmp);

    pt_playlist_tmp = NULL;
    iUpdatedRetryTime = 0;
    guiWaitedDuration = 0;

    mediaDataLocker.notifyAll();/*push thread may wait for updated done*/
    //mediaDataLocker.syncEnd();
#ifdef DEBUG
    dump_playlist(pt_cur_playlist, DUMP_CONDITION_ALL, DUMP_TO_FILE);
#endif
    LOG(0, "update m3u playlist success!\n");

    return true;
}

static bool key_compare(struct KEY_INFO * pt_key_info1, struct KEY_INFO * pt_key_info2)
{
    if ((NULL == pt_key_info1) || (NULL == pt_key_info2))
        return false;
    if (pt_key_info2->bHaveIV == false)
        return false;
    if (0 != memcmp(pt_key_info1->key, pt_key_info2->key, KEY_INFO_METHOD_AES_128))
        return false;
    if (0 != memcmp(pt_key_info1->iv, pt_key_info2->iv, KEY_INFO_METHOD_AES_128))
        return false;
    if (pt_key_info1->method != pt_key_info2->method)
        return false;
    return true;
}


void * Playlist::pushmode_thread(void *pv_data)
{
    char * pDecryptedData = NULL;
    size_t pDecryptedDataLen = 0;
    bool bDataDecrypted = false;
    
    if (NULL == pv_data )
    {
        LOG(0, "pv_data == NULL\n");
        return NULL;
    }
    Playlist * pt_theList = ( Playlist*)pv_data;
    PlayerProxy  * pplayer = pt_theList->pplayer;
    struct MEDIA_INFO *pt_media_info;
    struct KEY_INFO t_key_info;
    HLS_PLAYLIST * pt_cur_playlist = NULL;
    unsigned char ucTmpIV[32];
    char *ptr;
    char * pt_content_buffer;
    uint length;
    uint blockSize = 128 * 1024;
    uint32_t bufsize = 0;
    static uint pushed_size = 0;

    LOG(5, "*** yw_pushmode_thread started %lu!!***\n", pthread_self());
    memset(&t_key_info, 0, sizeof(t_key_info));

    pplayer->notifyBegin();
    //pt_theList->mediaDataLocker.syncBegin();
    pt_media_info = pt_theList->nextToPlay();
    pt_cur_playlist = pt_theList->getCurHLSPlaylist();

    if (pt_cur_playlist == NULL)
    {
        //pt_theList->mediaDataLocker.syncEnd();
        LOG(5, "current playlist should not be NULL, exit push thread!\n");
        return NULL;
    }

    while((pt_media_info != NULL) || (pt_cur_playlist->ext_x_endlist != 1))
    {
        
        if ((pt_media_info == NULL) && (pt_cur_playlist->ext_x_endlist != 1))
        {
            LOG(0, "push thread wait for playlist update........!\n");
            if (!pt_theList->isAlive())
                break;
            pt_theList->mediaDataLocker.wait();
            LOG(5, "playlist has already been updated or exit or selected!\n");
            pt_media_info = pt_theList->nextToPlay();
            
            if (pt_media_info == NULL)/*should exit*/
                break;
                
            pt_cur_playlist = pt_theList->getCurHLSPlaylist();/*should reset after update*/
            if (pt_cur_playlist == NULL)
            {
                LOG(5, "current playlist should not be NULL, exit push thread!\n");
                break;
            }
        }
        else if ((pt_media_info == NULL) && (pt_cur_playlist->ext_x_endlist == 1))
        {
            break;
        }
        
        /* insert key to cmpb */
        LOG(5, "[%d] ps_url = %s %d\n",
            pt_media_info->sequence_number, pt_media_info->ps_url, pt_media_info->status);

#ifdef PRINT_TIME
        struct timeval timeV = {0};
        gettimeofday(&timeV, NULL);
        LOG(-1, "[2.5]Current time: [%d]s.[%d]ms!!!\n", timeV.tv_sec, timeV.tv_usec / 1000);
#endif

        int push_threshold = PUSH_THRESHOLD;
        if (pt_media_info->pt_media_key != NULL)
        {
            /* for encrypted data, we need whole media file downloaded before decrypt and send data */
            push_threshold = 0;
        }

        //pt_theList->mediaDataLocker.syncEnd();

        pt_theList->resetPushStatus = false;    
        
        if( ! pt_theList->waitForMedia(pt_media_info, push_threshold))
        {
            // the media data can't be gained.
            // continue try next one.
            // todo ...
            LOG(0, "wait %d fail (%d), continue try next one\n", pt_media_info->sequence_number, pt_media_info->status);            
            //pt_theList->mediaDataLocker.syncBegin();
            pt_media_info = pt_theList->nextToPlay();
            continue;
        }
          
#ifdef PRINT_TIME
        gettimeofday(&timeV, NULL);
        LOG(-1, "[2.9]Current time: [%d]s.[%d]ms!!!\n", timeV.tv_sec, timeV.tv_usec / 1000);
#endif

        LOG(0, " %d download (%d/%d)\n", pt_media_info->sequence_number, pt_media_info->url_content.i4_used_size, pt_media_info->content_length);

        //pt_theList->mediaDataLocker.syncBegin();
        
        /* download fail, get next file */
        if (pt_media_info->result_code == 404)
        {
            LOG(0, "download fail, get next file\n");        
            pt_media_info = pt_theList->nextToPlay();
            continue;
        }

        if (pt_media_info->pt_media_key != NULL)
        {
            if (KEY_INFO_METHOD_AES_128 == pt_media_info->pt_media_key->method)
            {
                t_key_info.method = pt_media_info->pt_media_key->method;
                memcpy(t_key_info.key, pt_media_info->pt_media_key->key, KEY_INFO_METHOD_AES_128);
                if (false == pt_media_info->pt_media_key->bHaveIV)
                {
                    memset(ucTmpIV, 0, sizeof(ucTmpIV));
                    memset(t_key_info.iv, 0, sizeof(t_key_info.iv));
                    LOG(8, "create key info, seq num : %d\n", pt_media_info->sequence_number);
                    sprintf((char *)ucTmpIV, "%032d", pt_media_info->sequence_number);
                    extern void chars_to_hex(unsigned char * in, unsigned char *out, int iLen);
                    chars_to_hex(ucTmpIV, (unsigned char *)t_key_info.iv, KEY_INFO_METHOD_AES_128*2);
                }
                if (pDecryptedDataLen < pt_media_info->url_content.i4_used_size)
                {
                    pDecryptedData = (char *)realloc(pDecryptedData, (pt_media_info->url_content.i4_used_size * sizeof(char)));
                }
                if (NULL == pDecryptedData )
                {
                    LOG(0, "******Data Decrypt Fail!!\n");
                    continue;
                }

                memset(pDecryptedData, pt_media_info->url_content.i4_used_size , 0);
                bDataDecrypted = AESDecrypt((unsigned char *)t_key_info.key, 16,
                                            (unsigned char *)t_key_info.iv, 16,
                                            (unsigned char *)pt_media_info->url_content.pac_buffer,
                                            (unsigned int)pt_media_info->url_content.i4_used_size,
                                            (unsigned char *)pDecryptedData);
                LOG(0, "#####Media file %s\n key %s\n size = %d \n",
                    pt_media_info->ps_url, pt_media_info->pt_media_key->ps_url,
                    pt_media_info->url_content.i4_used_size);

#ifdef DUMP_DECRYPTED_DATA
                {
                    static FILE * g_fp = NULL;
                    char mediaFileName[256];

                    memset(mediaFileName, 0, 256);
                    sprintf(mediaFileName, "/mnt/usb/sda1/download_%d.ts", pt_media_info->sequence_number);
                    g_fp = fopen(mediaFileName, "w+");

                    if (g_fp == NULL)
                    {
                        LOG(0, "----[ERROR]----open download.ts failed!\n");
                        break;
                    }

                    fwrite(pDecryptedData, pt_media_info->url_content.i4_used_size, 1, g_fp);
                    fclose(g_fp);

                    fprintf(stderr, "iv = %x %x %x %x %x %x %x %x %x %x %x %x %x %x %x %x ",
                            t_key_info.iv[0], t_key_info.iv[1],t_key_info.iv[2],t_key_info.iv[3],
                            t_key_info.iv[4],t_key_info.iv[5],t_key_info.iv[6],t_key_info.iv[7],
                            t_key_info.iv[8],t_key_info.iv[9],t_key_info.iv[10],t_key_info.iv[11],
                            t_key_info.iv[12],t_key_info.iv[13],t_key_info.iv[14],t_key_info.iv[15]);

                    fprintf(stderr, "key %x %x %x %x %x %x %x %x %x %x %x %x %x %x %x %x ",
                            t_key_info.key[0], t_key_info.key[1],t_key_info.key[2],t_key_info.key[3],
                            t_key_info.key[4],t_key_info.key[5],t_key_info.key[6],t_key_info.key[7],
                            t_key_info.key[8],t_key_info.key[9],t_key_info.key[10],t_key_info.key[11],
                            t_key_info.key[12],t_key_info.key[13],t_key_info.key[14],t_key_info.key[15]);

                }
#endif
            }

        }


        /* push data to cmpb */

        ptr = pt_media_info->url_content.pac_buffer;

        if (bDataDecrypted)
        {
            ptr = pDecryptedData;
        }
            
        pt_content_buffer = pt_media_info->url_content.pac_buffer;
        pushed_size = 0;
        
        LOG(5, "Index[%d], Content_length[%d], Total_size[%d]!!!\n", pt_media_info->sequence_number,
            pt_media_info->content_length, pt_media_info->url_content.i4_total_size);
       
       if (pt_media_info->status== MEDIA_FILE_CONSUMED)
       {
            /* re sent previous played media file */
            pt_media_info->status = MEDIA_FILE_CACHED;
       }

        LOG(0, "Index[%d] %d , Content_length[%d], status = %d, start push ps_url = %s!!!\n", 
        	pt_media_info->sequence_number, 
        	pt_media_info->url_content.i4_used_size, 
        	pt_media_info->content_length,
        	pt_media_info->status,        	
        	pt_media_info->ps_url);
        
        while ((((pushed_size < pt_media_info->content_length) || 
        	       (pushed_size < pt_media_info->url_content.i4_used_size)) && 
        	    ((pt_media_info->status== MEDIA_FILE_CACHED)  ||
        	     (pt_media_info->status== MEDIA_FILE_DOWNLOADING))) ||
        	     (pt_media_info->status== MEDIA_FILE_DOWNLOADING) )
        {

            pt_theList->isPaused();
            if (true == pt_theList->resetPushStatus)
            {
                LOG(0, "Stop to push data!\n");
                break;
            }     
            
            /* current downloaded size */
            length = pt_media_info->url_content.i4_used_size;

            bufsize = length - pushed_size;
            if((bufsize < PUSH_THRESHOLD) &&
                    (length < pt_media_info->content_length) &&
                    (pt_media_info->status== MEDIA_FILE_DOWNLOADING))
            {
               if (pt_theList->isAlive())                            
               {
                   usleep(500000);
                   LOG(5, "bufsize (%d) < PUSH_THRESHOLD (%d)!\n",bufsize,  PUSH_THRESHOLD);
                   continue;
               }
               else
               {
                    //pt_theList->mediaDataLocker.syncEnd();
                    pplayer->notifyEnd();
                    LOG(0, "Stop to push data!\n");
                    return NULL;
               }
            }

            while(pushed_size < length)
            {

                pt_theList->isPaused();
		  if (true == pt_theList->resetPushStatus)
		  {
		      LOG(0, "resetPushStatus!\n");
		      break;
		  }   
		              
                bufsize = bufsize > blockSize ? blockSize : (length - pushed_size) ;

                if (pt_theList->isAlive())
                {
                    int ret;

                    /* when realloc pac_buffer, it is possible that use incorrect pointer */
                    if (pt_content_buffer != pt_media_info->url_content.pac_buffer)
                    {
                        pt_content_buffer = pt_media_info->url_content.pac_buffer;
                        ptr = pt_media_info->url_content.pac_buffer;
                        ptr += pushed_size;
                    }

                    if ( (ret = pplayer->sendData((uint8_t*)ptr, bufsize)) != SENDDATA_SUCCESS)
                    {
                        if (ret == SENDDATA_TSINFO_FAIL)
                        {
                            LOG(0, "SENDDATA_TSINFO_FAIL !\n");
                            break;
                        }
                        if ((ret == SENDDATA_GETBUF_FAIL) || (ret == SENDDATA_SENDDATA_FAIL))
                        {                               
                            usleep(500000);
                            LOG(0, "SENDDATA_GETBUF_FAIL or SENDDATA_SENDDATA_FAIL!\n");
                            continue;
                        }
                    }
                }
                else
                {
                    //pt_theList->mediaDataLocker.syncEnd();
                    pplayer->notifyEnd();
                    if (pDecryptedData) 
                    {
                	    free(pDecryptedData);
                    }                    
                    LOG(0, "................Stop to push data!\n");
                    return NULL;
                }

                ptr += bufsize;
                pushed_size += bufsize;
                LOG(6, "pt_media_info->status = %d, send buffer size=%d, length=%d, pushed_size=%d\n", pt_media_info->status, bufsize, length, pushed_size);              
              
            }/* end of while */

            /* if content did not corrected download, exit loop */
            if ( ((pt_media_info->result_code != 200) &&  (pt_media_info->result_code != 0) ) ||
                    (pt_media_info->status == MEDIA_FILE_CACHE_FAIL) ||
                    (pt_media_info->status == MEDIA_FILE_DOWNLOAD_CANCEL) )
            {
                LOG(0, "download result %d!\n", pt_media_info->result_code);
                break;
            }          
    
        } /* end of while */

        if (true != pt_theList->resetPushStatus)
        {
            /* mark media content consumed, so we could reuse buffer */
            pt_media_info->status = MEDIA_FILE_CONSUMED;
            pt_theList->consumed_count++;
            pt_theList->analysisMemUsage();                
            pt_theList->isPaused();       
        }
		                      
        //pt_theList->mediaDataLocker.syncEnd();
        LOG(0, "[%d] complete push %d ps_url = %s\n",pt_media_info->sequence_number,  pushed_size,  pt_media_info->ps_url);
        
        //pt_theList->mediaDataLocker.syncBegin();          
        pt_media_info = pt_theList->nextToPlay();
    } /* end of while */
    
    pt_theList->complete = true;
    //pt_theList->mediaDataLocker.syncEnd();

    LOG(0, "Notify End!\n");
    pplayer->notifyEnd();

    if (pDecryptedData) 
    {
        free(pDecryptedData);
    }
    LOG(0, "*** yw_pushmode_thread ended!!***\n");
    return NULL;
} /* yw_pushmode_thread */




