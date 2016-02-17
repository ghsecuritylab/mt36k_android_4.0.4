/*add just for test*/
#ifndef _IMTKPB_CTRL_H_
#define _IMTKPB_CTRL_H_
#include "stdint.h"
typedef int IMTK_PB_HANDLE_T;
typedef enum
{
    /* General */
    IMTK_PB_ERROR_CODE_OK                      =   0,   ///< Success.
    IMTK_PB_ERROR_CODE_NOT_OK                  =   -1,  ///< Failed.
    IMTK_PB_ERROR_CODE_INV_HANDLE              =   -2,  ///< The hHandle is invalid.
    IMTK_PB_ERROR_CODE_INV_ARG                 =   -3,  ///< The parameters are invalid.
    IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY     =   -4,  ///< Not enough memory resource.
    IMTK_PB_ERROR_CODE_NOT_INIT                =   -5,  ///< The playback control is not initialized yet.
    IMTK_PB_ERROR_CODE_NOT_IMPL                =   -6,  ///< Not implement or support this function.
    IMTK_PB_ERROR_CODE_DRV_ERROR               =   -7,  ///< The target driver has problem.
    IMTK_PB_ERROR_CODE_SYS_LIB_ERROR           =   -8,  ///< Error in system call or standard library

    /* IMtkPb_Ctrl specific (from -100) */
    IMTK_PB_ERROR_CODE_GET_BUF_PENDING         =   -100, ///< The buffer is not ready to get.
    IMTK_PB_ERROR_CODE_NO_BUFFERSINK           =   -101, ///< No buffer sink can be set for this playback

    /* IMtkPb_DRM specific (from -200) */
    IMTK_PB_ERROR_CODE_FILE_NOT_FOUND          =   -200, ///< The target file is not found.
    IMTK_PB_ERROR_CODE_FILE_READ_ERROR         =   -201, ///< The target file can not be read.
    IMTK_PB_ERROR_CODE_FILE_WRITE_ERROR        =   -202, ///< The target file can not be written.
    IMTK_PB_ERROR_CODE_FILE_NOT_CREATE         =   -203, ///< The target file can not be created.
    IMTK_PB_ERROR_CODE_FILE_NOT_DELETE         =   -204, ///< The target file can not be deleted.
    IMTK_PB_ERROR_CODE_FILE_CRYPTO_ERROR       =   -205, ///< The target file has crypto-trouble.    
    IMTK_PB_ERROR_CODE_DATA_CRYPTO_ERROR       =   -206  ///< The target data has crypto-trouble.

} IMTK_PB_ERROR_CODE_T;

/*! @enum   IMTK_PB_CTRL_BUF_SIZE_TYPE_T 
 *  @brief  Type of Buffer Size for URI model 
 */
typedef enum
{
    IMTK_PB_CTRL_BUF_SIZE_TYPE_UNKNOWN = 0,     ///< Unknown Buffer Size Type
    IMTK_PB_CTRL_BUF_SIZE_TYPE_BYTE,            ///< Buffer Size in Byte
    IMTK_PB_CTRL_BUF_SIZE_TYPE_DURATION         ///< Buffer Size in Time.  This type is only supported when the media type is ::IMTK_PB_CTRL_MEDIA_TYPE_MP4 or ::IMTK_PB_CTRL_MEDIA_TYPE_ASF, or when playing a pure audio file.
} IMTK_PB_CTRL_BUF_SIZE_TYPE_T;


/*! @struct IMTK_PB_CTRL_URI_INFO_T 
 *  @brief  URI Info structure
 */
typedef struct
{    
    uint32_t    u4URI_len;                          ///< Length of URI string
    uint8_t*    pu1URI;                             ///< Pointer of URI string.  MtkPbLib supports http, https, and file resource types.  For local files, a "file://" prefix must exist in front of the absolute path which starts with '/'.
    
    IMTK_PB_CTRL_BUF_SIZE_TYPE_T  eBufSizeType;     ///< Type of Buffer Size
    
    union {    
        uint32_t    u4Bytes;                        ///< Buffer size in Bytes
        uint32_t    u4Duration;                     ///< Buffer Duration in millisecond, not accurate in some cases
    } uBufSize;
    
    uint32_t    u4KeepBufThreshold;                 ///< Keep threshold in percentage, at least keep this percentage of data which can be used by seek backward
    uint32_t    u4ReBufThreshold;                   ///< Re-Buffer threshold in percentage, at most this percentage of fullness before pulling data from Application
} IMTK_PB_CTRL_URI_INFO_T;


typedef struct
{    
	uint32_t                        u4PlayFlag; 
	union    
		{        
			IMTK_PB_CTRL_URI_INFO_T     tUriInfo;  
			//IMTK_PB_CTRL_PULL_INFO_T    tPullInfo; 
		} uBufferModelParam;
} IMTK_PB_CTRL_ENGINE_PARAM_T;

/*! @enum   IMTK_PB_CTRL_MEDIA_TYPE_T 
 *  @brief  media type
 */
typedef enum
{
    IMTK_PB_CTRL_MEDIA_TYPE_UNKNOWN,    ///<  Unknown format type       
    IMTK_PB_CTRL_MEDIA_TYPE_AVI,        ///<  AVI file                  
    IMTK_PB_CTRL_MEDIA_TYPE_MPEG2_PS,   ///<  Mpeg2 program stream, or Mpeg1 system stream
    IMTK_PB_CTRL_MEDIA_TYPE_MPEG2_TS,   ///<  Mpeg2 transport stream    
    IMTK_PB_CTRL_MEDIA_TYPE_ASF,        ///<  WMV and ASF               
    IMTK_PB_CTRL_MEDIA_TYPE_MKV,        ///<  MKV file
    IMTK_PB_CTRL_MEDIA_TYPE_OGG,        ///<  Ogg file
    IMTK_PB_CTRL_MEDIA_TYPE_FLAC,       ///<  the FLAC file format, which contains the FLAC audio codec (::IMTK_PB_CTRL_AUD_ENC_FLAC)
    IMTK_PB_CTRL_MEDIA_TYPE_APE,        ///<  the APE file format, which contains Monkey's Audio (::IMTK_PB_CTRL_AUD_ENC_MONKEY)
    IMTK_PB_CTRL_MEDIA_TYPE_VIDEO_ES,   ///<  Video elementary stream
    IMTK_PB_CTRL_MEDIA_TYPE_AUDIO_ES,   ///<  Audio elementary stream
    IMTK_PB_CTRL_MEDIA_TYPE_MP4,        ///<  MP4 file (Lib Master only, for IMtkPb_Ctrl_GetMediaInfo())
    IMTK_PB_CTRL_MEDIA_TYPE_WAV,        ///<  WAV file (Lib Master only, for IMtkPb_Ctrl_GetMediaInfo())
    IMTK_PB_CTRL_MEDIA_TYPE_RM,         ///<  Real Media file (Lib Master only, for IMtkPb_Ctrl_GetMediaInfo())
    IMTK_PB_CTRL_MEDIA_TYPE_MTK_P0      ///<  MTK Private format 0 (App Master only, described in @ref MTKP0page)
} IMTK_PB_CTRL_MEDIA_TYPE_T;

/*! @enum   IMTK_PB_CB_ERROR_CODE_T 
 *  @brief  Error Code for callback functions
 *
 */
typedef enum
{
    IMTK_PB_CB_ERROR_CODE_OK                      =   0,   ///< Success.
    IMTK_PB_CB_ERROR_CODE_NOT_OK                  =   -1,  ///< Failed.    
    IMTK_PB_CB_ERROR_CODE_EOF                     =   -2   ///< EOF.    
} IMTK_PB_CB_ERROR_CODE_T;

/*! @enum   IMTK_PB_CTRL_EVENT_T 
 *  @brief  callback event type for IMtkPb_Ctrl_RegCallback()
 */
typedef enum
{
    IMTK_PB_CTRL_EVENT_UNKNOWN = 0,             ///< Unknown event.  not used.
    IMTK_PB_CTRL_EVENT_TOTAL_TIME_UPDATE,       ///< Total Time updated event.  The u4Data parameter is total playback time in millisecond.  This event is used only in Lib Master model.
    IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE,         ///< Current time update event.  The u4Data parameter is current playback time in millisecond.  This event is used only in Lib Master model.  App can use this event to get the current playback time, or call IMtkPb_Ctrl_GetCurrentPos() to get the same information.
    IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW,        ///< playback buffer underflow event.  The u4Data parameter is undefined.  This event is used in Lib Master and App Master models.
    IMTK_PB_CTRL_EVENT_BUFFER_HIGH,             ///< It's an edge-triggered event indicating that playback buffer fullness level goes beyond a certain threshold.  The u4Data parameter is undefined.  This event is used in Lib Master and App Master models.
    IMTK_PB_CTRL_EVENT_BUFFER_LOW,              ///< It's an edge-triggered event indicating that playback buffer fullness level goes below a certain threshold.  The u4Data parameter is undefined.  This event is used in Lib Master and App Master models.
    IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR,          ///< Various kinds of playback error.  The u4Data parameter is of the type ::IMTK_PB_CTRL_ERROR_TYPE_T.  This event is used for MtkPbLib to report certain playback error.
    IMTK_PB_CTRL_EVENT_EOS,                     ///< End of Stream event.  The u4Data parameter is undefined.  This event means the MtkPbLib has finished playback.  See @ref PlayEndNotes for details.
    IMTK_PB_CTRL_EVENT_EOF,                     ///< End of file event.  The u4Data parameter is undefined.  This event is used in Lib Master + URL model only, and only used in http and https resource types, but not in file resource type.  It means the MtkPbLib's buffered data has reached the end of file.
    IMTK_PB_CTRL_EVENT_PLAY_DONE,               ///< Playback engine finishes the IMtkPb_Ctrl_Play() or IMtkPb_Ctrl_PlayEx() operation.  The u4Data parameter is of the type ::IMTK_PB_CTRL_PLAY_RESULT_T.  Only used in Lib Master model.
    IMTK_PB_CTRL_EVENT_STEP_DONE,               ///< Playback engine finishes the IMtkPb_Ctrl_Step() operation.  The u4Data parameter is of the type ::IMTK_PB_CTRL_STEP_RESULT_T.
    IMTK_PB_CTRL_EVENT_TIMESEEK_DONE,           ///< Playback engine finishes the IMtkPb_Ctrl_TimeSeek() or IMtkPb_Ctrl_TimeSeekEx() operation.  The u4Data parameter is of the type ::IMTK_PB_CTRL_TIMESEEK_RESULT_T.  Only used in Lib Master model.
    IMTK_PB_CTRL_EVENT_GET_BUF_READY            ///< Buffer is ready for previous pending request of IMtkPb_Ctrl_GetBuffer().  The u4Data parameter is undefined.
} IMTK_PB_CTRL_EVENT_T;

/*! @struct IMTK_PB_CTRL_RECT_T 
 *  @brief  Rectangle structure
 */
typedef struct
{
    uint32_t                u4X;            ///< X Position, possible value [0:9999]
    uint32_t                u4Y;            ///< Y position, possible value [0:9999]
    uint32_t                u4W;            ///< Width, possible value [1:10000]
    uint32_t                u4H;            ///< Height, possible value [1:10000]
} IMTK_PB_CTRL_RECT_T;

/*! @struct IMTK_PB_CTRL_GET_MEDIA_INFO_T 
 *  @brief  Media Info structure
 */
typedef struct
{
    IMTK_PB_CTRL_MEDIA_TYPE_T       eMediaType;             ///< Media type
    uint32_t                        u4TotalDuration;        ///< total time(millisecond)
    uint64_t                        u8Size;                 ///< total size of this file, in byte
    uint32_t                        u4AvgBitrate;           ///< average bitrate of this file, in bits per second
    uint16_t                        u2AudioTrackNum;        ///< number of audio tracks
} IMTK_PB_CTRL_GET_MEDIA_INFO_T;

/* Playback flag */
#define IMTK_PB_CTRL_PLAY_FLAG_VIDEO            ((uint32_t) (1))         ///< indicating that the content contains video and video is to be played
#define IMTK_PB_CTRL_PLAY_FLAG_AUDIO            ((uint32_t) (1 << 1))   ///< indicating that the content contains audio and audio is to be played
#define IMTK_PB_CTRL_PLAY_FLAG_UNKNOWN          ((uint32_t) (1 << 31))   ///< indicating that App doesn't know the play flag.  If this bit is 1, all other bits are ignored.

typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Nfy_Fct)(IMTK_PB_CTRL_EVENT_T       eEventType,
                                                       void*                      pvAppCbTag,
                                                       uint32_t                   u4Data);

/*! @enum   IMTK_PB_CTRL_BUFFERING_MODEL_T 
 *  @brief  Buffering Model 
 */
typedef enum
{
    IMTK_PB_CTRL_BUFFERING_MODEL_URI    =   1,  ///< URI buffering model
    IMTK_PB_CTRL_BUFFERING_MODEL_PULL,          ///< PULL buffering model
    IMTK_PB_CTRL_BUFFERING_MODEL_PUSH           ///< PUSH buffering model
} IMTK_PB_CTRL_BUFFERING_MODEL_T;

/*! @enum   IMTK_PB_CTRL_OPERATING_MODEL_T
 *  @brief  Operating Model
 */
typedef enum
{
    IMTK_PB_CTRL_APP_MASTER    =   1,   ///< App Master operating model
    IMTK_PB_CTRL_LIB_MASTER             ///< Lib Master operating model
} IMTK_PB_CTRL_OPERATING_MODEL_T;

/*! @struct IMTK_PB_CTRL_SET_MEDIA_INFO_T 
 *  @brief  Media Info structure
 */
typedef struct
{
    IMTK_PB_CTRL_MEDIA_TYPE_T       eMediaType;             ///< Media type
    uint32_t                        u4TotalDuration;        ///< total time(millisecond)
    uint64_t                        u8Size;                 ///< total size
    bool                            fgSynchronized;         ///< Set this playback instance to be synchronized.  Only one instance in a system may be set as synchronized.  See @ref SyncNotes for more information.
    union
    {
#if 0		
        IMTK_PB_CTRL_MEDIA_AVI_INFO_T      tAviInfo;        ///< AVI info
        IMTK_PB_CTRL_MEDIA_MPEG2_PS_INFO_T tPsInfo;         ///< PS info
        IMTK_PB_CTRL_MEDIA_MPEG2_TS_INFO_T tTsInfo;         ///< TS info
        IMTK_PB_CTRL_MEDIA_ASF_INFO_T      tAsfInfo;        ///< ASF info
        IMTK_PB_CTRL_MEDIA_MKV_INFO_T      tMkvInfo;        ///< MKV info
        IMTK_PB_CTRL_MEDIA_OGG_INFO_T      tOggInfo;        ///< Ogg info
        IMTK_PB_CTRL_MEDIA_FLAC_INFO_T     tFlacInfo;       ///< Flac file info
        IMTK_PB_CTRL_MEDIA_APE_INFO_T      tApeInfo;        ///< APE file info
        IMTK_PB_CTRL_MEDIA_VIDEO_ES_INFO_T tVideoEsInfo;    ///< video elementary stream info
        IMTK_PB_CTRL_MEDIA_AUDIO_ES_INFO_T tAudioEsInfo;    ///< audio elementary stream info
        IMTK_PB_CTRL_MEDIA_MTK_P0_INFO_T   tMtkP0Info;      ///< MTK Private P0 info
        IMTK_PB_CTRL_MEDIA_RSVT_INFO_T     tRsvtInfo;       ///< info for a fictitious reserved media type RSVT
#endif
	} uFormatInfo;
} IMTK_PB_CTRL_SET_MEDIA_INFO_T;


IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetDisplayRectangle(IMTK_PB_HANDLE_T        hHandle,          
                                                            IMTK_PB_CTRL_RECT_T*    ptSrcRect,
                                                            IMTK_PB_CTRL_RECT_T*    ptDstRect);
                                                            
                                                            
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetEngineParam(IMTK_PB_HANDLE_T             hHandle,
                                                       IMTK_PB_CTRL_ENGINE_PARAM_T*   ptParam);
                                                       
                                                       
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetMediaInfo(IMTK_PB_HANDLE_T                   hHandle,
                                                     IMTK_PB_CTRL_GET_MEDIA_INFO_T*     ptMediaInfo);
                                                     
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_RegCallback(IMTK_PB_HANDLE_T        hHandle,
                                                    void*                   pvAppCbTag,
                                                    IMtkPb_Ctrl_Nfy_Fct     pfnCallback);
                                                   
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Play(IMTK_PB_HANDLE_T   hHandle,
                                             uint32_t           u4Time);
                                             
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Pause(IMTK_PB_HANDLE_T  hHandle);

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Stop(IMTK_PB_HANDLE_T   hHandle);

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Close(IMTK_PB_HANDLE_T  hHandle);

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_ByteSeek(IMTK_PB_HANDLE_T   hHandle, 
                                                 uint64_t           u8Pos);
                                                 
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_TimeSeek(IMTK_PB_HANDLE_T   hHandle, 
                                                 uint32_t           u4Time);
                                                 
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetCurrentPos(IMTK_PB_HANDLE_T  hHandle,
                                                      uint32_t*         pu4CurTime,   
                                                      uint64_t*         pu8CurPos);
                                                      
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Open(IMTK_PB_HANDLE_T*              phHandle,
                                               IMTK_PB_CTRL_BUFFERING_MODEL_T eBufferingModel,
                                               IMTK_PB_CTRL_OPERATING_MODEL_T eOperatingModel,
                                               uint8_t*                       pu1Profile);

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetMediaInfo(IMTK_PB_HANDLE_T                   hHandle,          
                                                     IMTK_PB_CTRL_SET_MEDIA_INFO_T*     ptMediaInfo);

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetBuffer(IMTK_PB_HANDLE_T      hHandle, 
											uint32_t              u4BufSize,
												uint8_t**             ppu1PushBuf);
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Local_SendData(IMTK_PB_HANDLE_T		hHandle,
											uint32_t 			 u4BufSize,
												uint8_t*			   pu1PushBuf,
													int type);
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SendData(IMTK_PB_HANDLE_T		hHandle,
											uint32_t 			 u4BufSize,
												uint8_t*			   pu1PushBuf);

#define IMTK_INVALID_PTS -1
#endif
