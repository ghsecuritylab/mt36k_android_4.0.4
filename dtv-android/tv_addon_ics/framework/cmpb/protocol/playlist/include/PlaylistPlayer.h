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
#ifndef _PLAYLISTPLAYER_H_
#define _PLAYLISTPLAYER_H_
#include "IMtkPb_Ctrl.h"
#include "IMtkPb_Ctrl_DTV.h"
#include "Playlist.h"
#include "openssl/aes.h"
#include "StreamSelector.h"

typedef IMTK_PB_CB_ERROR_CODE_T (*event_nfy_fct)(IMTK_PB_CTRL_EVENT_T, void*, unsigned int);

namespace hls
{
class PlaylistPlayerListener
{
public:
    virtual void notifyPlaylistPlayerListener(int event) = 0; // the event can be referenced in Playlist.h, event such as PLAYLIST_STATUS_CANNOT_GET_PLAYLIST ...
    virtual void notifyBegin() = 0; // the beginning of the playlist.
    virtual void notifyEnd() = 0;   // the playlist is end
    virtual ~PlaylistPlayerListener()=0;
};


class PlaylistPlayer : public PlayerProxy
{
private:
    PlaylistPlayerListener * caller;
    IMTK_PB_HANDLE_T    fCMPBHandle;
    unsigned long fCurTimeIndex;
    int m_duration;
    Playlist * pt_playlist;
    int time;
    int num_of_media_file_played;

public:
    PlaylistPlayer(PlaylistPlayerListener * listener);
    typedef enum _PLAYBACK_STATE_
    {
        PLAYBACK_UNKNOWN = 0,
        PLAYBACK_CLOSED = 1,
        PLAYBACK_OPENED = 2,
        PLAYBACK_TO_SETINFO = 3,
        PLAYBACK_READY = 4,
        PLAYBACK_PLAYING = 5,
        PLAYBACK_PAUSED = 6,
    }PLAYBACK_STATE;
    
    bool start(Playlist & list, event_nfy_fct callback, void* pvTag, int time);
    void play();
    void stop();
    void pause();
    void resume();
    int timeseek(int time);
    int state();
    void setDisplayRect(IMTK_PB_CTRL_RECT_T &outRect);
    int currentTime();
    int duration();
    int timeBuffered();
    int bytesLoaded();
    int downloadSpeed();
    int bitRate();
    int curVidTrackInfo(void * ptData);
    int curAudTrackInfo(void * ptData);
    IMTK_PB_ERROR_CODE_T getCmpbAsp(void * ptData);/*ptData is IMTK_PB_CTRL_ASP_T *, should be called after play_done*/
    int playlistContentType();
	
    virtual void notifyBegin(); // the beginning of the playlist.
    virtual void notifyEnd();   // the playlist is end
    virtual int sendData(uint8_t * pu1buffer, uint  size);    // send data to play.
    virtual void sendKeyInfo(uint8_t * puKey, uint  uiKeyLen, uint8_t * puIV, uint  uiIVLen);
    virtual void setPushModeEOS();
    virtual void notifyStatus(int event);
    virtual ~PlaylistPlayer();

    void setVideoFreeze(bool bFlag);

private:
    bool bTsInfoReady;
    Semaphore playerLocker;
    bool getMediaInfo(uint8_t * pu1buffer, uint  size, IMTK_PB_CTRL_SET_MEDIA_INFO_T &t_mediaInfo);  
    bool setMediaInfo(uint8_t * pu1buffer, uint  size);   
	bool setKeyInfo();
    bool plAESDecrypt(unsigned char * pucKey, unsigned int uiKeyLen,
                      unsigned char * pucIV, unsigned int uiIVLen,
                      unsigned char * pucInData, unsigned int uiInDataLen,
                      unsigned char * pucOutData);	
    event_nfy_fct callback;
    bool bHaveToSetKey;
    void* pvTag;
    uint8_t paKey[16];
    uint8_t paIV[16];
    IMTK_PB_CTRL_RECT_T    videoRect;
};

}


#endif /* _PLAYLISTPLAYER_H_ */
