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
#ifndef _PLAYLIST_H_
#define _PLAYLIST_H_

#include "http_live_streaming.h"
#include "ThreadObject.h"
#include <string>
#include <vector>
#include "openssl/aes.h"

namespace hls
{
    enum {                      // FOR notifyFailure.
        PLAYLIST_STATUS_CANNOT_GET_PLAYLIST,
        PLAYLIST_STATUE_CANNOT_GET_MEDIA,
        PLAYLIST_STATUS_CORE_ERROR,
        PLAYLIST_STATUS_REALIZED,
        PLAYLIST_STATUS_SEEK_TO_END,
        PLAYLIST_STATUS_SEEK_FAIL
    };

    enum {
    	SENDDATA_SUCCESS,    	
    	SENDDATA_TSINFO_FAIL,
    	SENDDATA_GETBUF_FAIL,
    	SENDDATA_SENDDATA_FAIL
    };
        
class Playlist;

class PlayerProxy
{
public:
    virtual void notifyBegin() = 0; // the beginning of the playlist.
    virtual void notifyEnd() = 0;   // the playlist is end
    virtual int sendData(uint8_t * pu1buffer, uint  size) = 0;    // send data to play.
    virtual void sendKeyInfo(uint8_t * puKey, uint  uiKeyLen, uint8_t * puIV, uint  uiIVLen) = 0;
    virtual void notifyStatus(int event) = 0;
    
    virtual void setPushModeEOS() = 0;
  
    virtual ~PlayerProxy() = 0;
};

class PlaylistStream
{
public:
    virtual int getBandwith() = 0;
    virtual std::string getCodecs() =0;
    virtual ~PlaylistStream()=0;
};

typedef std::vector<PlaylistStream*> StreamArray;
    
class StreamSelectorAbstract
{
public:
    virtual void selectStream(StreamArray & strms, int downloadBandWith, StreamArray::iterator * selectWhich) = 0;
    virtual void sortStreams(StreamArray & strm) = 0;
    virtual ~StreamSelectorAbstract()=0;
};
    
    
class Playlist : public Semaphore
{
public:
    Playlist(HLS_PLAYLIST * hls, const std::string & ps_uri,StreamSelectorAbstract * selector = NULL);
    ~ Playlist();
    HLS_PLAYLIST * pt_hls_list;
    HLS_PLAYLIST * pt_cur_hls_list;
    PlayerProxy * pplayer;
    Semaphore mediaDataLocker;
    int consumed_count;
    MEDIA_INFO * pt_cur_dl_media;
    volatile bool resetPushStatus ;
    volatile bool resetDownload;
    static void *pushmode_thread(void *pv_data);

    static Playlist * createPlaylist(const std::string & playlist_uri, StreamSelectorAbstract * selector = NULL); // create playlist by uri, if return null, the content identified by the uri isn't a playlist.
    void notifyMediaStatus(MEDIA_INFO * pt_media, MEDIA_FILE_STATUS status);
    bool realize();                                            // Note: this function needn't call outside, it will be done in another threaed context. So that the duration of the start of player will be shorter. 
    bool startEngine(PlayerProxy * player);

    // when player stopped (user interrupts by exit button, or playlist was finsished playing.),this function should be called for release related resource.
    static void releasePlaylist( Playlist * pt_playlist);
    bool isPlaying();
    void confirmPlaying();
    void stopPlaying();
    std::string getUri();
    HLS_PLAYLIST * getHLSPlaylist();
    HLS_PLAYLIST * getCurHLSPlaylist();
    void setCurHLSPlaylist(HLS_PLAYLIST * );
    MEDIA_INFO * popDownloadTask();
    bool waitForMedia(MEDIA_INFO * pt_media);
    bool waitForMedia(MEDIA_INFO * pt_media , int threshold);

    bool isMediaItem(const std::string & uri);
    std::string nextMediaUri();
    std::string previousMediaUri();
    void realizeStream(HLS_PLAYLIST * plist);
    void selectStreamFailed(STREAM_INFO * strm);
    void selectStream(struct MEDIA_INFO * pt_media );
    bool needSelectStream();
    bool select(const std::string & uri);
    bool select(MEDIA_INFO * minfo);

    MEDIA_INFO * nextToPlay();
    bool isAlive();
    bool isPaused();
    void setRealized(bool bRealized);
    void stop();
    void pause();
    void resume();
    MEDIA_INFO * findMediaByTime(const int time);
    MEDIA_INFO * findMediaByTime(const int time, int * realTime);    
	MEDIA_INFO * findMediaBySeq(HLS_PLAYLIST * pt_playlist, int seq);
    static std::string &GetUAString(){return mtkUAStr;};
    
private:
    MEDIA_INFO * pt_cur_media;     // pointer to current playback media file
    MEDIA_INFO * pt_checked_media; // which has been checked through isMediaItem, it means this is mostly to be selected by select function.

    bool pending;                  // when new uri was select by user (means media file selected), this variable
    // will be set to true. And once pushmode thread calls nextToPlay and finds it's true, it will change to new media to play other than next one in the list.
    bool playing;
    std::string uri;
    int cached_count;
    ThreadObject downloader;    
    ThreadObject pusher;    
    bool alive;    
    bool realized;
    bool paused;
    StreamSelectorAbstract * strmSelector;
    StreamArray streams;        
    bool m_needSelectStream;
    Semaphore pushWaiter;    
    
    void appendMedia(MEDIA_INFO * pt_media);
    MEDIA_INFO * findMedia(const std::string & uri);
	bool update_m3u_playlist();
	void analysisMemUsage();
    static const int MIN_REQ_CNT = 30;
    bool complete;
    int    total_memory;

    static std::string mtkUAStr;    
};
}

#endif /* _PLAYLIST_H_ */
