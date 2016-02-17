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
#include "StreamSelector.h"
#include "Log.h"
#include <string.h>

#ifndef _cplusplus
#define _cplusplus
#include "PlaylistPlayback.h"
#endif

using namespace hls;

static CMPB_Nfy_Begin_Fct pNotifyBegin = NULL;
static CMPB_Nfy_End_Fct pNotifyEnd = NULL;
static CMPB_Nfy_Listener_Fct pNotifyListener = NULL;

class MyApp : public PlaylistPlayerListener
{
    virtual void notifyPlaylistPlayerListener(int event);
    virtual void notifyBegin();
    virtual void notifyEnd();
};

void MyApp:: notifyPlaylistPlayerListener(int event)
{
    if (pNotifyListener)
    {
        (pNotifyListener)(event);
    }
}

void MyApp::notifyBegin()
{
    if (pNotifyBegin)
    {
        (pNotifyBegin)();
    }
}

void MyApp:: notifyEnd()
{
    if (pNotifyEnd)
    {
        (pNotifyEnd)();
    }
}

void register_notify(CMPB_Nfy_End_Fct end, CMPB_Nfy_Begin_Fct begin, CMPB_Nfy_Listener_Fct listener)
{
    pNotifyBegin = begin;
    pNotifyEnd = end;
    pNotifyListener = listener;
}

void unRegister_notify()
{
    pNotifyBegin = NULL;
    pNotifyEnd = NULL;
    pNotifyListener = NULL;
}

char * g_main_argv;
int g_main_argc;

#define MAX_NUM_HANDLES      ((unsigned short) 4096)
#define SYS_MEM_SIZE ((unsigned int) 12 * 1024 * 1024)

typedef struct _THREAD_DESCR_T
{
    unsigned int  z_stack_size;
    unsigned char  ui1_priority;
    unsigned short  ui2_num_msgs;
}   THREAD_DESCR_T;

typedef struct _GEN_CONFIG_T
{
    unsigned short  ui2_version;
    void*  pv_config;
    unsigned int  z_config_size;
    THREAD_DESCR_T  t_mheg5_thread;
}   GEN_CONFIG_T;

extern "C" int c_rpc_init_client(void);
extern "C" int c_rpc_start_client(void);

extern "C" int os_init(const void *pv_addr, unsigned int z_size);
extern "C" int handle_init (unsigned short   ui2_num_handles,
                            void**   ppv_mem_addr,
                            unsigned int*  pz_mem_size);
extern "C" int x_rtos_init (GEN_CONFIG_T*  pt_config);

#define MAX_NUM_HANDLES      ((unsigned short) 4096)

using namespace std;

void init_rpc(void)
{
    GEN_CONFIG_T  t_rtos_config;
    bzero(&t_rtos_config, sizeof(GEN_CONFIG_T));
    void*       pv_mem_addr = 0;
    unsigned int z_mem_size = 0xc00000;
    int ret = 0;

    ret = x_rtos_init(&t_rtos_config);
    if (ret != 0)
    {
        printf("rtos init failed %d \n", ret);
    }
    ret = handle_init(MAX_NUM_HANDLES, &pv_mem_addr, &z_mem_size);
    if (ret != 0)
    {
        printf("handle init failed %d \n", ret);
    }
    ret = os_init(pv_mem_addr, z_mem_size);
    if (ret != 0)
    {
        printf("os init failed %d \n", ret);
    }
    ret = c_rpc_init_client();
    if (ret != 0)
    {
        printf("rpc init failed %d \n", ret);
    }
    ret = c_rpc_start_client();
    if (ret < 0)
    {
        printf("rpc start failed %d \n", ret);
    }
    printf("Rpc init OK\n");
}

static MyApp * g_myApp = NULL;
static  PlaylistPlayer * g_player = NULL;
static  Playlist * g_playlist = NULL;
static StreamSelector selector;

void playlist_play(char * url, event_callback_fct callback, int time)
{

    if (url == NULL)
    {
        return;
    }
    g_playlist = Playlist::createPlaylist(std::string(url), &selector);
    if (g_playlist == NULL)
    {
        LOG(0, "The uri:%s is not a playlist\n", url);
        return;
    }

    g_myApp = new MyApp;

    g_player = new PlaylistPlayer(g_myApp);

    if (g_player->start(*g_playlist, callback, time) != IMTK_PB_ERROR_CODE_OK)
    {
    	LOG(0, "%s start fail!!\n", url); 
    	callback(IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR,0,0);
    }
}

void playlist_stop()
{

    if (g_player)
    {
        g_player->stop();
    }

    if (g_playlist)
    {
        Playlist::releasePlaylist(g_playlist);
        delete g_playlist;
        g_playlist = NULL;
    }    
}

void playlist_pause()
{
    if (!g_player)
    {
        return;
    }
    g_player->pause();
}

void playlist_resume()
{
    if (!g_player)
    {
        return;
    }
    g_player->resume();
}

int playlist_state()
{
    if (!g_player)
    {
        return 0;
    }
    return g_player->state();
}

int playlist_duration()
{
    if (!g_player)
    {
        return -1;
    }
    return g_player->duration();
}

int playlist_timeBuffered()
{
    if (!g_player)
    {
        return -1;
    }
    return g_player->timeBuffered();
}

int playlist_bytesLoaded()
{
    if (!g_player)
    {
        return -1;
    }
    return g_player->bytesLoaded();
}


int playlist_currentTime()
{
    if (!g_player)
    {
        return -1;
    }
    return g_player->currentTime();
}

void playlist_timeseek(int time)
{
    if (!g_player)
    {
        return;
    }
    g_player->timeseek(time);
}

double playlist_downloadSpeed()
{
    if (!g_player)
    {
        return 0;
    }
    return g_player->downloadSpeed();
}

int playlist_bitRate()
{
    if (!g_player)
    {
        return 0;
    }
    return g_player->bitRate();
}

int playlist_curVidTrackInfo(void * ptInfo)
{
    if (!g_player)
    {
        return -1;
    }	
    return g_player->curVidTrackInfo(ptInfo);
}

int  playlist_curAudTrackInfo(void * ptInfo)
{
    if (!g_player)
    {
        return -1;
    }
    return g_player->curAudTrackInfo(ptInfo);
}

int  playlist_playlistContentType()
{
    if (!g_player)
    {
        return 0;
    }
    return g_player->playlistContentType();
}
	
void uninit_playlist()
{
    if (g_myApp)
    {
        delete g_myApp;
        g_myApp = NULL;
    }

    if (g_playlist)
    {
        delete g_playlist;
        g_playlist = NULL;
    }

    if (g_player)
    {
        delete g_player;
        g_player = NULL;
    }
}

