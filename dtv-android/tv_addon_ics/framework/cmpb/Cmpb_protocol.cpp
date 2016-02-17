

/******************************************************************************************/
/******************************************************************************************/
/******************************************************************************************/
/******************************************************************************************/
/******************************************************************************************/
/******************************************************************************************/
/******************************************************************************************/
/******************************************************************************************/
#include <utils/Log.h>
#include "PlaylistPlayer.h"
#include "RtspPlayer.h"
#include "curl/curl.h"
#include "myLog.h"
#include "myCfg.h"
#include <string>
#include <iostream>
#include "Cmpb_protocol.h"
#include "CmpbPlayer.h"

namespace android
{
using namespace hls;
using namespace rtsp;
    
#ifdef __cplusplus
#define SYS_MEM_SIZE          ( ( unsigned int ) 12 * 1024 * 1024 )
#define MAX_NUM_HANDLES       ( ( unsigned short ) 4096 )
    
Playlist*         playlist_temp = NULL;
    
typedef struct _THREAD_DESCR_T
{
    unsigned int    z_stack_size;
    unsigned char    ui1_priority;
    unsigned short    ui2_num_msgs;
}    THREAD_DESCR_T;

/* Generic configuration structure. */

static bool bCmpbProtocolSendSize = false;

typedef struct _GEN_CONFIG_T
{
    unsigned short int    ui2_version;

    void*                pv_config;

    size_t                z_config_size;

    THREAD_DESCR_T        t_mheg5_thread;
}    GEN_CONFIG_T;

extern "C" int c_rpc_init_client( void );
extern "C" int c_rpc_start_client( void );

extern "C" int os_init( const void *pv_addr, unsigned int z_size );
extern "C" int handle_init ( unsigned short   ui2_num_handles,
                               void**            ppv_mem_addr,
                               unsigned int*    pz_mem_size );
extern "C" int x_rtos_init ( GEN_CONFIG_T*    pt_config );
#endif
static void _Init_Rpc( void )
{
    GEN_CONFIG_T      t_rtos_config;
    void*              pv_mem_addr = 0;
    unsigned int      z_mem_size  = 0xc00000;
    int               ret          = 0;
    static bool       b_init = FALSE;

    if ( b_init )
    {
        LOGI( "RPC has been inited!\n" );
        return;
    }
    memset( &t_rtos_config, 0, sizeof( GEN_CONFIG_T ) );

    ret = x_rtos_init( &t_rtos_config );
    if ( ret != 0 )
    {
        LOGE( "rtos init failed %d \n", ret );
    }
    ret = handle_init( MAX_NUM_HANDLES, &pv_mem_addr, &z_mem_size );
    if ( ret != 0 )
    {
        LOGE( "handle init failed %d \n", ret );
    }
    ret = os_init( pv_mem_addr, z_mem_size );
    if ( ret != 0 )
    {
        LOGE( "os init failed %d \n", ret );
    }
    ret = c_rpc_init_client();
    if ( ret != 0 )
    {
        LOGE( "rpc init failed %d \n", ret );
    }
    ret = c_rpc_start_client();
    if ( ret != 0 )
    {
        LOGE( "rpc start failed %d \n", ret );
    }
    LOGI( "Rpc init OK\n" );
    
    b_init = true;

    return;
}

/************************************************************************************
                                                     Real Streaming Player interface
************************************************************************************/
CmpbProtocolPlayer::CmpbProtocolPlayer()
    :m_iUri( " " ), m_ePlayerState( CPP_NON_INIT ), m_nLoop( 0 ), m_nCurrentTime( 0 ), 
    m_bSeek(false), m_bUnderflow(false)
{
}
CmpbProtocolPlayer::CmpbProtocolPlayer( void* pv_CmpbPlayer )
    :m_iUri( " " ), m_ePlayerState( CPP_NON_INIT ), m_nLoop( 0 ),
    m_iCmpbPlayer( pv_CmpbPlayer ), m_nCurrentTime( 0 ), m_bSeek(false), m_bUnderflow(false)
{
}

CmpbProtocolPlayer::~CmpbProtocolPlayer()
{
}

int CmpbProtocolPlayer::SetDataSource( const char* sz_url )
{
    LOGI( "CmpbProtocolPlayer::SetDataSource(%s)\n", sz_url ); 
    m_iUri = sz_url;
    return CPP_OK;
}

int CmpbProtocolPlayer::Reset() 
{
    LOGI( "CmpbProtocolPlayer:Reset\n" );
    return CPP_OK;
}

int CmpbProtocolPlayer::SetLooping(int loop )
{
    m_nLoop = loop;
    LOGI( "CmpbProtocolPlayer:SetLooping(%d)\n", loop );
    return CPP_OK;
}
bool CmpbProtocolPlayer::IsPlaying()
{
    if ( m_ePlayerState == CPP_START )
    {
        LOGI( "CmpbProtocolPlayer:IsPlaying\n" );
        return true;
    }
    else
    {
        LOGI( "CmpbProtocolPlayer:Is not Playing\n" );
        return false;
    }
}

/************************************************************************************
                                                     Http live streaming
************************************************************************************/
CmpbHttpLiveStreaming::CmpbHttpLiveStreaming( void* pv_CmpbPlayer )
    : CmpbProtocolPlayer( pv_CmpbPlayer ) , m_pList( NULL ), m_pPlayer( NULL )
{
    //m_iUri = std::string( "http://meta.video.qiyi.com/89/112356cbe72c56cc07e0f5bfdbf03232.m3u8" );
}
CmpbHttpLiveStreaming::~CmpbHttpLiveStreaming()
{
#if 0
    if ( m_pList )
    {
        Playlist::releasePlaylist( m_pList );
        m_pList = NULL;
    }

    if ( m_pPlayer != NULL )
    {
        delete m_pPlayer;
        m_pPlayer = NULL;
    }
    
    m_ePlayerState = CPP_NON_INIT;
    
#endif
    releasePlaylist();
}

void CmpbHttpLiveStreaming:: notifyPlaylistPlayerListener( int event )
{
    LOGI( "CmpbHttpLiveStreaming::event(%d)\n", event );
    return;
}

void CmpbHttpLiveStreaming::notifyBegin()
{
    LOGI( "CmpbHttpLiveStreaming::notifyBegin: play begin\n" );
    return;
}

void CmpbHttpLiveStreaming:: notifyEnd()
{
    LOGI( "CmpbHttpLiveStreaming::notifyBegin: play end\n" );
    return;
}

int CmpbHttpLiveStreaming::GetVideoRect(IMTK_PB_CTRL_ASP_T &dispRect)
{
    if ( m_pPlayer )
    {
        if(0 == m_pPlayer->getCmpbAsp(&dispRect))
        {
            LOGI( "CmpbHttpLiveStreaming::GetRect player=%x, rect {%d,%d},{%d,%d} !\n", 
            (unsigned int)m_pPlayer, dispRect.u4SrcW,dispRect.u4SrcH,dispRect.u4PixelW,dispRect.u4PixelH);
            return 0;
        }
        else
        {
            LOGI( "CmpbHttpLiveStreaming::GetRect failed !\n"); 
            return -1;
        }
    }   

    return -1;
}

IMTK_PB_CB_ERROR_CODE_T CmpbHttpLiveStreaming::__PlaylistEventNotify(
    IMTK_PB_CTRL_EVENT_T eEventType, void* pvTag, unsigned int u4Data )
{
    uint32_t    ui4_temp = eEventType;
    assert( pvTag );
    CmpbHttpLiveStreaming* pt_CmpbHls = ( android::CmpbHttpLiveStreaming* )( pvTag );
    assert( pt_CmpbHls->m_iCmpbPlayer );
    CmpbPlayer*   pt_Cmpblayer = ( android::CmpbPlayer* )( pt_CmpbHls->m_iCmpbPlayer );
    
    switch ( ui4_temp )    
    {
        case IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE:
        {
            //static UINT32  gui4_get_time_interval = 500;    cmpb_ctrl.c
            if ( ( pt_CmpbHls->m_ePlayerState == CPP_START ) 
                && ( !( pt_CmpbHls->m_bSeek ) ) 
                && ( !( pt_CmpbHls->m_bUnderflow ) ))
            {
                pt_CmpbHls->m_nCurrentTime += 500; 
            }
                        
            break;
        }
        case IMTK_PB_CTRL_EVENT_TOTAL_TIME_UPDATE: 
        {
            LOGI( "IMTK_PB_CTRL_EVENT_TOTAL_TIME_UPDATE \n" );
            break;
        }
        case IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW:
        {
            LOGI( "IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW \n" );
            pt_Cmpblayer->sendEvent( MEDIA_INFO, MTK_MEDIA_INFO_BUFFER_START );
            pt_CmpbHls->m_bUnderflow = true;
            break;
        }
        case IMTK_PB_CTRL_EVENT_EOS: 
        {
            LOGI( "IMTK_PB_CTRL_EVENT_EOS \n" );
            pt_Cmpblayer->sendEvent( MEDIA_PLAYBACK_COMPLETE );
            break;
        }
        case IMTK_PB_CTRL_EVENT_STEP_DONE: 
        {
            LOGI( "IMTK_PB_CTRL_EVENT_STEP_DONE \n" );
            break;
        }
        case IMTK_PB_CTRL_EVENT_GET_BUF_READY:
        {
            LOGI( "IMTK_PB_CTRL_EVENT_GET_BUF_READY \n" );
            break;
        }
        case IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR:
        {
            LOGI( "IMTK_PB_CTRL_EVENT_ERROR \n" );
            pt_Cmpblayer->sendEvent( MEDIA_ERROR );
            break;
        }
        case IMTK_PB_CTRL_EVENT_PLAY_DONE:
        {
            LOGI( "IMTK_PB_CTRL_EVENT_PLAYED \n" );
            if ( pt_CmpbHls->m_bSeek )
            {
                LOGI( "------Seek Done!!!m_ePlayerState = %d! \n", pt_CmpbHls->m_ePlayerState );

                if(pt_CmpbHls->m_pPlayer)
                {
                    pt_CmpbHls->m_pPlayer->setVideoFreeze(false);
                }
                if(pt_CmpbHls->m_ePlayerState == CPP_PAUSE)
                {
                    LOGI( "------Pause playback after seek done!!! \n" );
                    pt_CmpbHls->m_ePlayerState = CPP_START;
                    pt_CmpbHls->Pause();
                }
                pt_CmpbHls->m_bSeek = false;
                pt_Cmpblayer->sendEvent( MEDIA_SEEK_COMPLETE );

                if(pt_CmpbHls->m_bUnderflow)
                { 
                    LOGI( "hls send buffer end due to underflow flag is true \n" );
                    pt_Cmpblayer->sendEvent( MEDIA_INFO, MTK_MEDIA_INFO_BUFFER_END );
                    pt_CmpbHls->m_bUnderflow = false;
                }
            }
            else
            {
            	  IMTK_PB_CTRL_ASP_T tAsp;

                if(0 == pt_CmpbHls->GetVideoRect(tAsp))
                {
                    LOGI("__PlaylistEventNotify() get video width and height %d, %d !\n", tAsp.u4SrcW,tAsp.u4SrcH);
                    if(tAsp.u4SrcW != 0 && tAsp.u4SrcH != 0)
                    {
                        pt_Cmpblayer->sendEvent(MEDIA_SET_VIDEO_SIZE, tAsp.u4SrcW,tAsp.u4SrcH);
                        bCmpbProtocolSendSize = true;
                    }
                }
                pt_CmpbHls->Pause();             
                if(!bCmpbProtocolSendSize)
                {  
                	LOGI("callback send MEDIA_PREPARED!\n");
					pt_Cmpblayer->setCmpbPlayerState(STATE_PREPARED);
                	pt_Cmpblayer->sendEvent(MEDIA_PREPARED);
                }
            }
            break;
        }
        case IMTK_PB_CTRL_EVENT_TIMESEEK_DONE:
        {
            LOGI( "IMTK_PB_CTRL_EVENT_TIMESEEK_DONE \n" );
            //pt_CmpbHls->m_nCurrentTime = 0;
            //pt_CmpbHls->m_bSeek = true;
            break;
        }    
        case IMTK_PB_CTRL_EVENT_BUFFER_READY:
        {
            LOGI( "IMTK_PB_CTRL_EVENT_BUFFER_READY \n" );
            pt_Cmpblayer->sendEvent( MEDIA_INFO, MTK_MEDIA_INFO_BUFFER_END );
            pt_CmpbHls->m_bUnderflow = false;
            break;
        }
        case IMTK_PB_CTRL_EVENT_ASP_UPDATE:
        {
            LOGI( "20140321 IMTK_PB_CTRL_EVENT_ASP_UPDATE \n" );
	        {
	            IMTK_PB_CTRL_ASP_T tAsp;
	            
	            if(0 == pt_CmpbHls->GetVideoRect(tAsp))
	            {
	            	LOGI("20140321 ASP_UPDATE get video width and height %d, %d !\n", tAsp.u4SrcW,tAsp.u4SrcH);
	            	if(tAsp.u4SrcW != 0 && tAsp.u4SrcH != 0)
	            	{
	            		pt_Cmpblayer->sendEvent(MEDIA_SET_VIDEO_SIZE, tAsp.u4SrcW,tAsp.u4SrcH);
	            		bCmpbProtocolSendSize = false;
	            	}
	            }
				#if 0
	            pt_CmpbHls->Pause();			 
	            if(!bCmpbProtocolSendSize)
	            {  
	            	LOGI("20140321 ASP_UPDATE callback send MEDIA_PREPARED!\n");
	            	pt_Cmpblayer->setCmpbPlayerState(STATE_PREPARED);
	            	pt_Cmpblayer->sendEvent(MEDIA_PREPARED);
	            }
				#endif
	         }
            break;
        }
        default :
        {
            LOGE( "call back default %d \n", ui4_temp );
            break;
        }
    }

    return IMTK_PB_CB_ERROR_CODE_OK;
}

int CmpbHttpLiveStreaming::SetDataSource( const char* sz_url )
{
    //LOGI( "CmpbHttpLiveStreaming::SetDataSource(%s)\n", sz_url ); 
    CmpbProtocolPlayer::SetDataSource( sz_url );
    	
    return 0;
}

int CmpbHttpLiveStreaming::SetVideoRect(IMTK_PB_CTRL_RECT_T &dispRect)
{
    LOGI("CmpbHttpLiveStreaming::SetRect player=%x,rect{%d,%d},{%d,%d},bCmpbProtocolSendSize(%d)!\n", 
        m_pPlayer, dispRect.u4X,dispRect.u4Y,dispRect.u4W,dispRect.u4H,bCmpbProtocolSendSize);
    if ( m_pPlayer )
    {
        m_pPlayer->setDisplayRect(dispRect);
    }
  	if(bCmpbProtocolSendSize)
  	{      
  		LOGI("SetVideoRect send MEDIA_PREPARED!\n");
  		CmpbPlayer*   pt_Cmpblayer = ( android::CmpbPlayer* )( this->m_iCmpbPlayer );
		pt_Cmpblayer->setCmpbPlayerState(STATE_PREPARED);
  		pt_Cmpblayer->sendEvent(MEDIA_PREPARED);
  		bCmpbProtocolSendSize = false;
  	}
    return 0;
}

int CmpbHttpLiveStreaming::Prepare()
{
    LOGI( "CmpbHttpLiveStreaming::Prepare enter!\n" ); 
    
    if ( !m_pPlayer )
    {
        m_pPlayer = new PlaylistPlayer( this );
        if ( !m_pPlayer )
        {
            LOGE( "CmpbHttpLiveStreaming::SetDataSource: create PlaylistPlayer failed\n" ); 
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }
    }
    
    if ( m_pList )
    {
        Playlist::releasePlaylist( m_pList );
    }
    if (playlist_temp != NULL)
    {
        m_pList = playlist_temp;
        playlist_temp = NULL;
    }
    else
    {
    m_pList = Playlist::createPlaylist( m_iUri, &m_iSelector );
    if ( !m_pList )
    {
        LOGE( "The uri:%s is not a playlist\n", m_iUri.c_str() );
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
    }
    m_ePlayerState = CPP_INIT;

    LOGI( "CmpbHttpLiveStreaming::Prepare exit!\n" ); 
    return CPP_OK;
    
}

int CmpbHttpLiveStreaming::PrepareAsync()
{
    LOGI( "CmpbHttpLiveStreaming::PrepareAsync enter!\n" ); 
    return this->Prepare();
}

int CmpbHttpLiveStreaming::Start( void )
{
    LOGI( "CmpbHttpLiveStreaming::Start enter!\n" ); 
    
    if ( ( m_ePlayerState != CPP_INIT ) && ( m_ePlayerState != CPP_PAUSE ) )
    {
        LOGE( "Http living streaming is not CPP_INIT or CPP_INIT!\n" );
        return CPP_ERR;
    }
    
    if ( m_ePlayerState == CPP_PAUSE )
    {
        Resume();
        return CPP_OK;
    }
     
    m_pPlayer->start( *m_pList , reinterpret_cast< event_nfy_fct >( CmpbHttpLiveStreaming::__PlaylistEventNotify ) , static_cast< void* >( this ), 0 );
    m_ePlayerState = CPP_START;

    LOGI( "CmpbHttpLiveStreaming::Start exit!\n" ); 
    return CPP_OK;
}

int CmpbHttpLiveStreaming::Pause( void )
{
    LOGI( "CmpbHttpLiveStreaming::Pause enter!\n" ); 

    if ( m_ePlayerState != CPP_START )
    {
        LOGE( "Http living streaming is not CPP_START!\n" );
        return CPP_ERR;
    }
    m_pPlayer->pause();
    m_ePlayerState = CPP_PAUSE;
    
    LOGI( "CmpbHttpLiveStreaming::Pause exit!\n" );
    return CPP_OK;
}
int CmpbHttpLiveStreaming::Resume( void )
{
    LOGI( "CmpbHttpLiveStreaming::Resume enter!\n" ); 

    if ( m_ePlayerState != CPP_PAUSE )
    {
        LOGE( "Http living streaming is not CPP_PAUSE!\n" );
        return CPP_ERR;
    }
    m_pPlayer->resume();
    m_ePlayerState = CPP_START;

    LOGI( "CmpbHttpLiveStreaming::Resume exit!\n" ); 
    return CPP_OK;
}
int CmpbHttpLiveStreaming::Suspend()
{
    LOGI( "CmpbHttpLiveStreaming::Suspend enter!\n" ); 
    return CPP_OK;
}

int CmpbHttpLiveStreaming::Stop( void )
{
    LOGI( "CmpbHttpLiveStreaming::Stop enter!\n" ); 

    if ( ( m_ePlayerState != CPP_START ) && ( m_ePlayerState != CPP_PAUSE ) )
    {
        LOGE( "Http living streaming is not CPP_START!\n" );
        return CPP_ERR;
    }
    m_pPlayer->stop();

    m_ePlayerState = CPP_INIT;
    m_bSeek = FALSE;
	bCmpbProtocolSendSize = false;
    
    LOGI( "CmpbHttpLiveStreaming::Stop exit!\n" ); 
    return CPP_OK;
}
int CmpbHttpLiveStreaming::SeekTo( int msec )
{
    CmpbPlayer*   pt_Cmpblayer = ( android::CmpbPlayer* )( this->m_iCmpbPlayer );
    assert( pt_Cmpblayer );
    LOGI( "CmpbHttpLiveStreaming::SeekTo(%d)!\n", msec ); 
    #if 1 //def __CMPB_PRO_TIME_SUPPORT__
    m_bSeek = true;
    m_pPlayer->timeseek( msec / 1000 );
    m_nCurrentTime = msec;
    #else
    pt_Cmpblayer->sendEvent( MEDIA_SEEK_COMPLETE );
    #endif
    LOGI( "CmpbHttpLiveStreaming::SeekTo exit!\n" ); 
    return CPP_OK;
}

int CmpbHttpLiveStreaming::GetCurrentPosition( int* msec )
{
    //int nCurPosition;
    int nDuration = 0;
    if ( !msec )
    {
        LOGI( "CmpbHttpLiveStreaming::GetCurrentPosition error exit!\n" ); 
        return CPP_ERR;
    }
    LOGI( "CmpbHttpLiveStreaming::getCurrentPosition!\n" ); 
    #if 1//def __CMPB_PRO_TIME_SUPPORT__
    if (m_pPlayer->playlistContentType() == TYPE_EVENT_SLIDING ||
        m_pPlayer->playlistContentType() == TYPE_EVENT_APPENDING)
    {
        LOGI("\n This playlist is slide or appending , duration must be 0!!\n");
        *msec = 0;
    }
    else if ( msec )
    {
        nDuration = m_pPlayer->duration() * 1000 ;
        if ( m_nCurrentTime < nDuration )
        {
            *msec = m_nCurrentTime; 
        }
        else
        {
            *msec = nDuration; 
        }
    }
    #else
    if ( msec )
    {
        *msec = 0;
    }
    #endif
    LOGI( "CmpbHttpLiveStreaming::getCurrentPosition(return %d) exit!\n", *msec ); 
    return CPP_OK;
}

int CmpbHttpLiveStreaming::GetDuration( int* msec )
{
    int nDuration = 0;

    if ( !msec )
    {
        LOGI( "CmpbHttpLiveStreaming::getDuration error exit!\n" ); 
        return CPP_ERR;
    }

    LOGI( "CmpbHttpLiveStreaming::getDuration!\n" ); 
    #if 1//def __CMPB_PRO_TIME_SUPPORT__
    if (m_pPlayer->playlistContentType() == TYPE_EVENT_SLIDING ||
        m_pPlayer->playlistContentType() == TYPE_EVENT_APPENDING)
    {
        LOGI("\n This playlist is slide or appending , duration must be 0!!\n");
        *msec = 0;
    }
    else
    {
        nDuration = m_pPlayer->duration();
        *msec = (nDuration + 1) * 1000 ;
    }
    #else
    *msec = 0;
    #endif
    LOGI( "CmpbHttpLiveStreaming::getDuration(return %d) exit!\n", nDuration ); 
    return CPP_OK;
}

CPP_TYPE_E CmpbHttpLiveStreaming::GetPlayerType()
{
    LOGI( "CmpbHttpLiveStreaming::GetPlayerType!\n" ); 
    return CPP_HLS;
}

void CmpbHttpLiveStreaming::releasePlaylist()
{    
    if ( playlist_temp != NULL )
	{
		Playlist::releasePlaylist( playlist_temp );
		playlist_temp = NULL;
	}
    if ( m_pList != NULL )
    {
        Playlist::releasePlaylist( m_pList );
        m_pList = NULL;
    }

    if ( m_pPlayer != NULL )
    {
        delete m_pPlayer;
        m_pPlayer = NULL;
    }

    m_ePlayerState = CPP_NON_INIT;
}

/************************************************************************************
                                                     RTSP
************************************************************************************/
CmpbRtsp::CmpbRtsp( void* pv_CmpbPlayer )
    : CmpbProtocolPlayer( pv_CmpbPlayer ), m_pPlayer( NULL )
{
}

CmpbRtsp::~CmpbRtsp()
{
    if ( m_pPlayer )
    {
        delete m_pPlayer;
        m_pPlayer = NULL;
    }
    
    m_ePlayerState = CPP_NON_INIT;
}
IMTK_PB_CB_ERROR_CODE_T CmpbRtsp::__RtspEventNotify(
    IMTK_PB_CTRL_EVENT_T eEventType, void* pvTag, unsigned int u4Data )
{
    assert( pvTag );
    CmpbRtsp* pt_CmpbRtsp = ( android::CmpbRtsp* )( pvTag );
    assert( pt_CmpbRtsp->m_iCmpbPlayer );
    CmpbPlayer*   pt_Cmpblayer = ( android::CmpbPlayer* )( pt_CmpbRtsp->m_iCmpbPlayer );

    switch ( eEventType )    
    {
        case IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE:
        {
            //static UINT32  gui4_get_time_interval = 500;  cmpb_ctrl.c
            if ( ( pt_CmpbRtsp->m_ePlayerState == CPP_START ) 
                && ( !( pt_CmpbRtsp->m_bSeek ) )
                &&  ( !( pt_CmpbRtsp->m_bUnderflow ) ) )
            {
                pt_CmpbRtsp->m_nCurrentTime += 500; 
            }
            break;
        }
        case IMTK_PB_CTRL_EVENT_TOTAL_TIME_UPDATE: 
        {
            LOGI( "IMTK_PB_CTRL_EVENT_TOTAL_TIME_UPDATE \n" );
            break;
        }
        case IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW:
        {
            LOGI( "IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW \n" );
            pt_CmpbRtsp->m_bUnderflow = true;
            break;
        }
        case IMTK_PB_CTRL_EVENT_EOS: 
        {
            LOGI( "IMTK_PB_CTRL_EVENT_EOS \n" );
            pt_Cmpblayer->sendEvent( MEDIA_PLAYBACK_COMPLETE );
            break;
        }
        case IMTK_PB_CTRL_EVENT_STEP_DONE: 
        {
            LOGI( "IMTK_PB_CTRL_EVENT_STEP_DONE \n" );
            break;
        }
        case IMTK_PB_CTRL_EVENT_GET_BUF_READY:
        {
            LOGI( "IMTK_PB_CTRL_EVENT_GET_BUF_READY \n" );
            break;
        }
        case IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR:
        {
            LOGI( "IMTK_PB_CTRL_EVENT_ERROR \n" );
            pt_Cmpblayer->sendEvent( MEDIA_ERROR );
            break;
        }
        case IMTK_PB_CTRL_EVENT_PLAY_DONE:
        {
            LOGI( "IMTK_PB_CTRL_EVENT_PLAYED \n" );
                        pt_Cmpblayer->setCmpbPlayerState(STATE_PREPARED);
                        pt_Cmpblayer->sendEvent(MEDIA_PREPARED);
            break;
        }
        case IMTK_PB_CTRL_EVENT_TIMESEEK_DONE:
        {
            LOGI( "IMTK_PB_CTRL_EVENT_TIMESEEK_DONE \n" );
            pt_CmpbRtsp->m_nCurrentTime = 0;
            break;
        }    
        case IMTK_PB_CTRL_EVENT_BUFFER_READY:
        {
            LOGI( "IMTK_PB_CTRL_EVENT_BUFFER_READY \n" );
            pt_CmpbRtsp->m_bUnderflow = false;
            break;
        }    
        default :
        {
            LOGE( "call back default %d \n", eEventType );
            break;
        }
    }

    return IMTK_PB_CB_ERROR_CODE_OK;
}

int CmpbRtsp::SetDataSource( const char* sz_url )
{
    //LOGI( "CmpbRtsp::SetDataSource(%s)\n", sz_url ); 
    CmpbProtocolPlayer::SetDataSource( sz_url );
    return CPP_OK;
}
int CmpbRtsp::Prepare()
{
    LOGI( "CmpbRtsp::Prepare enter!\n" ); 

    rtsp::setLog( false );
    rtsp::saveLocalFile( false );
    rtsp::saveCmpbPlay( true );
    rtsp::setTcpFlag( false );
    rtsp::setOnlyCount( false );
        
    m_pPlayer = RtspPlayer::createNew();
    if (NULL == m_pPlayer)
    {
        LOGE( "create m_pPlayer error!\n" );
        return CPP_OK;
    }
    m_ePlayerState = CPP_INIT;
    
    LOGI( "CmpbRtsp::Prepare exit!\n" ); 
    return CPP_OK;
    
}

int CmpbRtsp::PrepareAsync()
{
    LOGI( "CmpbRtsp::PrepareAsync enter!\n" ); 
    return this->Prepare();
}

int CmpbRtsp::Start( void )
{
    LOGI( "CmpbRtsp::Start enter!\n" ); 
    
    if ( ( m_ePlayerState != CPP_INIT ) && ( m_ePlayerState != CPP_PAUSE ))
    {
        LOGE( "CmpbRtspis not CPP_INIT!\n" );
        return CPP_ERR;
    }
    
    if ( m_ePlayerState == CPP_PAUSE )
    {
        Resume();
        return CPP_OK;
    }
    
    m_pPlayer->setTcpRetry();
    m_pPlayer->play( m_iUri, reinterpret_cast< IMtkPb_Ctrl_Nfy_Fct >( CmpbRtsp::__RtspEventNotify ), static_cast< void* >( this ) );
    m_ePlayerState = CPP_START;
    LOGI( "CmpbRtsp::Start exit!\n" ); 
    return CPP_OK;
}

int CmpbRtsp::Pause( void )
{
    LOGI( "CmpbRtsp::Pause enter!\n" ); 
#if 0
    if ( m_ePlayerState != CPP_START )
    {
        LOGE( "CmpbRtsp is not CPP_START!\n" );
        return CPP_ERR;
    }
    m_pPlayer->pause( true );
    m_ePlayerState = CPP_PAUSE;
#endif
    LOGI( "CmpbRtsp::Pause exit!\n" );
    return CPP_OK;
}
int CmpbRtsp::Resume( void )
{
    LOGI( "CmpbRtsp::Resume enter!\n" ); 
#if 0

    if ( m_ePlayerState != CPP_PAUSE )
    {
        LOGE( "CmpbRtspis not CPP_PAUSE!\n" );
        return CPP_ERR;
    }
    //m_pPlayer->resume();
    m_pPlayer->play( m_iUri, reinterpret_cast< IMtkPb_Ctrl_Nfy_Fct >( CmpbRtsp::__RtspEventNotify ), static_cast< void* >( this ) );
    m_ePlayerState = CPP_START;
#endif

    LOGI( "CmpbRtsp::Resume exit!\n" ); 
    return CPP_OK;
}

int CmpbRtsp::Suspend()
{
    LOGI( "CmpbRtsp::Suspend enter!\n" ); 
    return CPP_OK;
}

int CmpbRtsp::Stop( void )
{
    LOGI( "CmpbRtsp::Stop enter!\n" ); 

    if ( ( m_ePlayerState != CPP_START ) && ( m_ePlayerState != CPP_PAUSE ) )
    {
        LOGE( "CmpbRtsp is not CPP_START!\n" );
        return CPP_ERR;
    }
    m_pPlayer->stop();

    m_ePlayerState = CPP_INIT;
    
    LOGI( "CmpbRtsp::Stop exit!\n" ); 
    return CPP_OK;
}
int CmpbRtsp::SeekTo( int msec )
{
    CmpbPlayer*   pt_Cmpblayer = ( android::CmpbPlayer* )( this->m_iCmpbPlayer );
    assert( pt_Cmpblayer );
    LOGI( "CmpbRtsp::SeekTo(%d)!\n", msec ); 
    #ifdef __CMPB_PRO_TIME_SUPPORT__
    m_bSeek = true;
    m_pPlayer->timeseek( msec );
    m_nCurrentTime = msec;
    pt_Cmpblayer->sendEvent( MEDIA_SEEK_COMPLETE );
    m_bSeek = false;
    #else
    pt_Cmpblayer->sendEvent( MEDIA_SEEK_COMPLETE );
    #endif

    LOGI( "CmpbRtsp::IMTK_PB_CTRL_EVENT_TIMESEEK_DONE \n" );
    LOGI( "CmpbRtsp::SeekTo exit!\n" ); 
    return CPP_OK;
}

int CmpbRtsp::GetCurrentPosition( int* msec )
{
    LOGI( "CmpbRtsp::getCurrentPosition!\n" ); 
    #ifdef __CMPB_PRO_TIME_SUPPORT__ 
    if ( msec )
    {
        *msec = m_nCurrentTime; 
    }
    #else
    if ( msec )
    {
        *msec = 0; 
    }
    #endif
    LOGI( "CmpbRtsp::getCurrentPosition(%d) exit!\n", m_nCurrentTime ); 
    return CPP_OK;
}

int CmpbRtsp::GetDuration( int* msec )
{
    LOGI( "CmpbRtsp::getDuration!\n" ); 
    LOGI( "CmpbRtsp::getDuration exit!\n" ); 
    if ( msec )
    {
        *msec = 0;
    }
    return CPP_OK;
}
CPP_TYPE_E CmpbRtsp::GetPlayerType()
{
    LOGI( "CmpbRtsp::GetPlayerType!\n" ); 
    return CPP_RTSP;
}

/************************************************************************************
                                                     Real Streaming Player factory
************************************************************************************/
CmpbProtocolPlayer* CmpbProtocolPlayerFac::m_pPlayer = NULL;
CPP_TYPE_E CmpbProtocolPlayerFac::m_ePlayerType = CPP_NONE;
CmpbProtocolPlayerFac::CmpbProtocolPlayerFac( bool bRpcInit )
    : m_bRpcInit( bRpcInit )
{
    if ( m_bRpcInit )
    {
        _Init_Rpc();
    }
}
CmpbProtocolPlayerFac::~CmpbProtocolPlayerFac( )
{
}
CPP_TYPE_E CmpbProtocolPlayerFac::GetPlayerType()
{
    return m_ePlayerType;
}
/////////////for get mime type//////////////////
static char* pc_redirect_url = NULL;
char* getRedirectUrl()
{
    char *pcTmp = pc_redirect_url;
    pc_redirect_url = NULL;////////free in cmpbplayer.cpp,  need not free here
    return pcTmp; 
}

static char * CopyHeaderValueIfNameMatches(const char * pHeaderName, const char * pHTTPResponseLine)
{
    const char * pData = pHTTPResponseLine;
    const int nHeaderLength = strlen(pHeaderName);

    if (strcasestr(pData, pHeaderName) != NULL )
    {
        pData += nHeaderLength;
        if (strncmp(": ", pData, 2) == 0)
        {
            pData += 2; 
            if (*pData)
            {
                // need to strip out "\r\n" at end of buffer
                const char * pEOL = strstr(pData, "\r\n");
                if (pEOL)
                {
                    char * pResult = strdup(pData);
                    pResult[pEOL - pData] = '\0'; 
                    return pResult;  
                }
            }
        }
    }
    return NULL;
}

static size_t
_header_wr_function(void *ptr, size_t z_size, size_t z_nmemb, void *pv_tag)
{
    const char * pData = (const char *) ptr;
    char ** ppt_contentType = (char **) pv_tag;
    size_t z_len = z_size * z_nmemb;

    if ( pData )
    {
        char * pValue = CopyHeaderValueIfNameMatches("Content-Type", pData);
        LOGI("cmpb_protocol.cpp: pData=%s", pData);
        if (pValue)
        {  //get last content-type
            if (*ppt_contentType) free(*ppt_contentType);
            *ppt_contentType = pValue;
        }             
        ///////for redirect url change
        char * pRdrctURL = CopyHeaderValueIfNameMatches("Location", pData);
        if(pRdrctURL)
        {
            if(strncasecmp(pRdrctURL, "http://", 7) == 0)
            {
                pc_redirect_url = pRdrctURL;
                LOGI("cmpb_protocol.cpp: found redirect url and save it");
            }
            else 
            {
                LOGI("cmpb_protocol.cpp: it's not redirect url and free it");
                free(pRdrctURL);
                pRdrctURL = NULL;
            }
        }
    }

    return(z_len);
} /* end of _header_wr_function */

static size_t _body_wr_function(void *ptr, size_t z_size,  size_t z_nmemb, void *pv_tag)
{
    return z_size * z_nmemb -1;
}

char * get_url_mine_type(char * ps_url, char * ps_user_agent, char * ps_cookie)
{
        struct CHAR_BUFFER  url_header;     
        char * pt_contentType = NULL;
        struct curl_slist   *palias_hdr_slist =NULL;
        CURL *pt_curl = curl_easy_init();
        //if (1) 
        {
            curl_easy_setopt(pt_curl, CURLOPT_VERBOSE, 1L);
        }
        curl_easy_setopt(pt_curl, CURLOPT_FOLLOWLOCATION, 1L);
        curl_easy_setopt(pt_curl, CURLOPT_MAXREDIRS, 10L);
        curl_easy_setopt(pt_curl, CURLOPT_NOPROGRESS, 0L);
        curl_easy_setopt(pt_curl, CURLOPT_CONNECTTIMEOUT, 30);
        curl_easy_setopt(pt_curl, CURLOPT_FRESH_CONNECT, 0L);         
        curl_easy_setopt(pt_curl, CURLOPT_HEADERFUNCTION, _header_wr_function);
        curl_easy_setopt(pt_curl, CURLOPT_WRITEHEADER, &(pt_contentType));          
        curl_easy_setopt(pt_curl, CURLOPT_WRITEFUNCTION, _body_wr_function);
        curl_easy_setopt(pt_curl, CURLOPT_WRITEDATA, NULL);
        curl_easy_setopt(pt_curl, CURLOPT_URL, ps_url);    
        curl_easy_setopt(pt_curl, CURLOPT_TIMEOUT, 30);         
        
        if(ps_user_agent)
        {
            curl_easy_setopt(pt_curl, CURLOPT_USERAGENT, ps_user_agent);            
        }
        if(ps_cookie)
        {
            curl_easy_setopt(pt_curl, CURLOPT_COOKIE, ps_cookie);
        }
        //https_ssl_setting(ps_url, pt_curl);

        /* create alias header, for file which format is .nsv (ShoutCast) */
        palias_hdr_slist = curl_slist_append(palias_hdr_slist, "ICY 200 OK");
        curl_easy_setopt(pt_curl, CURLOPT_HTTP200ALIASES, palias_hdr_slist);
        curl_easy_perform(pt_curl);
        
    curl_easy_cleanup(pt_curl);
    pt_curl = NULL;
    
    curl_slist_free_all(palias_hdr_slist);
    palias_hdr_slist = NULL;  

   // _strlwr_s(pt_contentType);

    return pt_contentType;
}

class HlsUrlInfo
{
public:
    std::string strUrl;
	std::string strUserAgent;
	std::string strCookie;
};

bool CmpbProtocolPlayerFac::IsCmpbProtocolPlayer( const char* uc_url )
{
    /*if live streaming*/
    if ( CmpbFileExtValid ( uc_url, "m3u8" ) || strstr(uc_url, ".m3u8?"))
    {
        LOGI("IsCmpbProtocolPlayer m3u8, return true");
        return true;
    }
    LOGI("IsCmpbProtocolPlayer not m3u8");
    HlsUrlInfo url_info;
    std::string strUrl = uc_url;
    std::string::size_type ua_pos = strUrl.find("?mtkUAString=");
    if (ua_pos == std::string::npos)
    {
        url_info.strUrl = strUrl;
    }
    else
    {
        url_info.strUrl.assign(strUrl, 0, ua_pos);
        url_info.strUserAgent = std::string(strUrl, ua_pos + strlen("?mtkUAString=")); 
    }
#if 1
    if(pc_redirect_url != NULL)
    {
        free(pc_redirect_url); /////maybe exception here
        pc_redirect_url = NULL;
        LOGI("cmpb_protocol::IsCmpbProtocolPlayer() free old redirect url");
    }
    LOGI("IsCmpbProtocolPlayer before get_url_mine_type");
    char * pt_contentType = 
                  get_url_mine_type((char*)url_info.strUrl.c_str(), 
                                    (char*)url_info.strUserAgent.c_str(), 
                                    (char*)url_info.strCookie.c_str());
    LOGI("cmpb_protocol: url = %s\n, ua=%s\n,cookie=%s", (char*)url_info.strUrl.c_str(),
                                    (char*)url_info.strUserAgent.c_str(), 
                                    (char*)url_info.strCookie.c_str());
    if(pt_contentType)
    {
        LOGI("cmpb_protocol: content type = %s", pt_contentType);
        if(strcasestr(pt_contentType, "Application/vnd") || 
           strcasestr(pt_contentType, "apple.mpegurl") || 
           strcasestr(pt_contentType, "audio.mpegurl") || 
           strcasestr(pt_contentType, "audio.x_mpegurl") || 
           strcasestr(pt_contentType, "audio/x-mpegurl") || 
           strcasestr(pt_contentType, "application/vnd.apple.mpegurl") || 
           strcasestr(pt_contentType, "application/x-mpegURL"))
        {
            free(pt_contentType);
            pt_contentType = NULL;
            return true;
        }
        free(pt_contentType);
        pt_contentType = NULL;
    }
#else
    Playlist* temp = Playlist::createPlaylist(uc_url, &m_iSelector);
    if (temp != NULL)
    {
        delete temp;
        LOGI("--------------IsCmpbProtocolPlayer-------------------");
        return TRUE;
    }
#endif
    std::string   url( uc_url );
    if ( url.find( "rtsp://", 0 )  == 0 )
    {
        return true;
    }

    return false;
    
}

bool CmpbProtocolPlayerFac::CmpbFileExtValid( const char* uc_url,    const char* uc_str )
{
    std::string   url;
    std::string   url_src(uc_url);
    std::string::size_type ua_pos = url_src.find("?mtkUAString=");
    if (ua_pos == std::string::npos)
    {
        url = std::string(uc_url);
    }
    else
    {
        url.assign(uc_url, 0, ua_pos);
    }
    if ( url.substr( url.find_last_of( "." ) + 1 ) == uc_str )
    {
        return true;
    }
    else
    {
        return false;
    }
    
}
CmpbProtocolPlayer*  CmpbProtocolPlayerFac::CreatePlayer( const char* uc_url, void* pv_CmpbPlayer  )
{

    CmpbHttpLiveStreaming *hlsHandle = NULL;

    if (m_ePlayerType == CPP_HLS)
    {
        if (m_pPlayer != NULL)
        {
            hlsHandle = (CmpbHttpLiveStreaming *)m_pPlayer;
        }
    }

    if (hlsHandle != NULL)
    {
        LOGI("IsCmpbProtocolPlayer stop playerlist player and release handle\n");
        hlsHandle->Stop();
        CmpbPlayer* pt_Cmpblayer = ( android::CmpbPlayer*)(hlsHandle->m_iCmpbPlayer);
        pt_Cmpblayer->setCmpbPlayerState(STATE_STOPPED);
        hlsHandle->releasePlaylist();
        LOGI("IsCmpbProtocolPlayer stop playerlist player and release handle successfully\n");
    }
    /*if live streaming*/
    if ( CmpbFileExtValid ( uc_url, "m3u8" ) )
    {
        m_pPlayer = new CmpbHttpLiveStreaming( pv_CmpbPlayer );
        m_ePlayerType = CPP_HLS;
        return m_pPlayer;
    }

    /*if rtsp*/
    
    std::string   url( uc_url );
    if ( url.find( "rtsp://", 0 )  == 0 )
    {
        m_pPlayer = new CmpbRtsp( pv_CmpbPlayer );
        m_ePlayerType = CPP_RTSP;
        LOGI("-------------create CmpbRtsp player--------------");
        return m_pPlayer;
    }
    else
    {
        m_pPlayer = new CmpbHttpLiveStreaming( pv_CmpbPlayer );
        m_ePlayerType = CPP_HLS;
        LOGI("-------------create CmpbHttpLiveStreaming player--------------");
        return m_pPlayer;
    }

    return NULL;
}
int CmpbProtocolPlayerFac::DestroyPlayer()
{
    CmpbProtocolPlayer* m_pTmpPlayer = m_pPlayer;
    m_pPlayer = NULL;
    if ( m_pTmpPlayer )
    {
        delete m_pTmpPlayer;
    }

    return CPP_OK;
     
}
}



