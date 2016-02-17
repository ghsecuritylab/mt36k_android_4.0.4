
/******************************************************************************************/
/******************************************************************************************/
/******************************************************************************************/
/******************************************************************************************/
/******************************************************************************************/
/******************************************************************************************/
/******************************************************************************************/
/******************************************************************************************/
#ifndef __CMPB_PROTOCOL_H__
#define __CMPB_PROTOCOL_H__
#include "RtspPlayer.h"
#include "PlaylistPlayer.h"
#include <string>
#include <iostream>

namespace android
{

using namespace hls;
using namespace rtsp;

#define CPP_OK          ( 0 )
#define CPP_ERR         ( -1 )


typedef enum _CPP_TYPE_E
{ 
    CPP_NONE = 0,
    CPP_HLS,
    CPP_RTSP,
} CPP_TYPE_E;

typedef enum _CPP_STATE_E
{  
    CPP_NON_INIT = 0,
    CPP_INIT,
    CPP_START,
    CPP_PAUSE,
} CPP_STATE_E;

class CmpbProtocolPlayer 
{

public:

    CmpbProtocolPlayer();
    CmpbProtocolPlayer( void* pv_CmpbPlayer );
    virtual ~CmpbProtocolPlayer()= 0;
    virtual int SetDataSource( const char* sz_url );
    virtual int Prepare()= 0;
    virtual int PrepareAsync()= 0;
    virtual int Start( void )= 0;
    virtual int Pause( void )= 0;
    virtual int Stop( void )= 0;
    virtual int SeekTo( int msec )= 0;
    virtual int GetCurrentPosition( int* msec )= 0;
    virtual int GetDuration( int* msec )= 0;
    virtual int Suspend() = 0;
    virtual int Resume( void )= 0;
    virtual int SetVideoRect(IMTK_PB_CTRL_RECT_T &dispRect)= 0;
    virtual int GetVideoRect(IMTK_PB_CTRL_ASP_T &dispRect) = 0;
    virtual int Reset();
    virtual int SetLooping(int loop );
    virtual bool IsPlaying();
    virtual CPP_TYPE_E GetPlayerType() = 0;      
    void*                          m_iCmpbPlayer;

protected:
    
    std::string                    m_iUri;
    CPP_STATE_E                    m_ePlayerState;   /*no protect sema*/
    int                            m_nLoop;
    //void*                          m_iCmpbPlayer;
    
    int                            m_nCurrentTime;
    bool                           m_bSeek;
    bool                           m_bUnderflow;
};


class CmpbHttpLiveStreaming : public PlaylistPlayerListener, public CmpbProtocolPlayer
{

public:

    CmpbHttpLiveStreaming( void* pv_CmpbPlayer );
    ~CmpbHttpLiveStreaming();
    virtual void notifyPlaylistPlayerListener( int event );
    virtual void notifyBegin(); // the beginning of the playlist.
    virtual void notifyEnd();   // the playlist is end
    virtual int SetDataSource( const char* sz_url );
    virtual int Prepare();
    virtual int PrepareAsync();
    virtual int Start( void );
    virtual int Pause( void );
    virtual int Suspend();
    virtual int Resume( void );
    virtual int Stop( void );
    virtual int SetVideoRect(IMTK_PB_CTRL_RECT_T &dispRect);
    virtual int GetVideoRect(IMTK_PB_CTRL_ASP_T &dispRect);
    
    virtual int SeekTo( int msec );
    virtual int GetCurrentPosition( int* msec );
    virtual int GetDuration( int* msec );
    //virtual int Reset() ;
    //virtual int SetLooping(int loop );
    //virtual bool IsPlaying();
    virtual CPP_TYPE_E GetPlayerType(); 
    virtual void releasePlaylist(); 
    
private:
    static IMTK_PB_CB_ERROR_CODE_T __PlaylistEventNotify(
        IMTK_PB_CTRL_EVENT_T eEventType, void* pvTag, unsigned int u4Data );
private:
    
    Playlist*                      m_pList;
    PlaylistPlayer*                m_pPlayer;
    StreamSelector                 m_iSelector;
};

class CmpbRtsp : public CmpbProtocolPlayer  
{

public:

    CmpbRtsp( void* pv_CmpbPlayer );
    ~CmpbRtsp();
    virtual int SetDataSource( const char* sz_url );
    virtual int Prepare();
    virtual int PrepareAsync();
    virtual int Start( void );
    virtual int Pause( void );
    
    virtual int Suspend();
    virtual int Resume( void );
    virtual int Stop( void );
    virtual int SetVideoRect(IMTK_PB_CTRL_RECT_T &dispRect){return 0;}
    virtual int GetVideoRect(IMTK_PB_CTRL_ASP_T &dispRect){return -1;}
    
    virtual int SeekTo( int msec );
    virtual int GetCurrentPosition( int* msec );
    virtual int GetDuration( int* msec );
    
    //virtual int Reset() ;
    //virtual int SetLooping(int loop );
    //virtual bool IsPlaying();
    virtual CPP_TYPE_E GetPlayerType();  
private:
    static IMTK_PB_CB_ERROR_CODE_T __RtspEventNotify(
        IMTK_PB_CTRL_EVENT_T eEventType, void* pvTag, unsigned int u4Data );
private:
    
    RtspPlayer*                    m_pPlayer;  
    int                            m_nCurrentTime;
};


class CmpbProtocolPlayerFac
{

public:

    CmpbProtocolPlayerFac( bool bRpcInit = false );
    ~CmpbProtocolPlayerFac();
    static CPP_TYPE_E GetPlayerType();   
    static bool IsCmpbProtocolPlayer( const char* uc_url );
   
    static bool CmpbFileExtValid( const char* uc_url,  const char* uc_str );
    static CmpbProtocolPlayer*  CreatePlayer( const char* uc_url, void* pv_CmpbPlayer );
    static int  DestroyPlayer();
    
private:
    static void __Init_Rpc( void );
    
private:
    
    static CmpbProtocolPlayer*  m_pPlayer;
    bool                        m_bRpcInit;
    static CPP_TYPE_E           m_ePlayerType;
    
};

    
}

#endif  //__CMPB_PROTOCOL_H__
