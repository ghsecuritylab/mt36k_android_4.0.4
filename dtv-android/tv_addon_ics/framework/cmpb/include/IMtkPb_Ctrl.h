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
 
/**
  @section Intro Introduction
    IMtkPb_Ctrl has two operating models and three buffering models, configured by the App by the arguments of
    IMtkPb_Ctrl_Open().  The two operating models are <b>App Master</b> and <b>Lib Master</b>.  The three
    buffering models are <b>URI</b>, <b>Pull</b>, and <b>Push</b>.  The following paragraphs briefly describe
    the distinctions of the models.

    The operating model defines the job separation between the App and the MtkPbLib.  In the App Master model,
    App needs to parse the metadata associated with the bitstream, analyze it, and then set only the necessary
    media information to MtkPbLib.  MtkPbLib then tries to play the bitstream based on the media info provided by
    the App.  In the Lib Master model, App doesn't need to have too much media format knowledge.
    It just provides the data content to MtkPbLib, and MtkPbLib will take care of almost everything.

    The buffering model tells MtkPbLib how the data bitstream will be fed into it.  In the URI model, MtkPbLib gets
    the contents from the URI provided by the App.  In the Pull model, App has to register some data feeding
    functions into MtkPbLib so MtkPbLib can call them to fetch data.  In the Push model, MtkPbLib is passive
    and the App has to actively push data into MtkPbLib.

    Not all combinations of operating and buffering models are allowed.  MtkPbLib only supports four combinations:
    - App Master
      - Pull
      - Push
    - Lib Master
      - URI
      - Pull

    Here are some sequence diagrams to depict the operations in each combination:
    - @ref LibMasterURI
    - @ref LibMasterPull
    - @ref AppMasterPull
    - @ref AppMasterPush

    This state diagram describes the transitions among different ::IMTK_PB_CTRL_STATE_T states in MtkPbLib:
    - @ref SMpage

    This diagram describe the MTK private format 0 (::IMTK_PB_CTRL_MEDIA_TYPE_MTK_P0)
    - @ref MTKP0page

  @section AppMasterNotes Notes on the App Master model
    In the App Master model, the data to be pulled or pushed are all multiplexed bitstream data to be fed into the demuxer,
    or elementary stream data to be fed into the decoder.  App shouldn't provide any metadata (index table, etc.) into MtkPbLib.
    More specifically, here's a detailed description on what MtkPbLib expects from App:
    - ::IMTK_PB_CTRL_MEDIA_TYPE_AVI: data inside the movi chunk
    - ::IMTK_PB_CTRL_MEDIA_TYPE_MPEG2_PS: the whole program stream data
    - ::IMTK_PB_CTRL_MEDIA_TYPE_MPEG2_TS: the whole transport stream data
    - ::IMTK_PB_CTRL_MEDIA_TYPE_ASF: Packets inside the Data Object
    - ::IMTK_PB_CTRL_MEDIA_TYPE_MKV: Starting from Clusters
    - ::IMTK_PB_CTRL_MEDIA_TYPE_OGG: the whole ogg multiplexed physical bitstream
    - ::IMTK_PB_CTRL_MEDIA_TYPE_FLAC: the FRAME part of the file
    - ::IMTK_PB_CTRL_MEDIA_TYPE_APE: the APE FRAMES part of the file
    - ::IMTK_PB_CTRL_MEDIA_TYPE_VIDEO_ES: the whole elementary stream
    - ::IMTK_PB_CTRL_MEDIA_TYPE_AUDIO_ES: the whole elementary stream
    - ::IMTK_PB_CTRL_MEDIA_TYPE_MTK_P0: the whole stream

  @section PushNotes Notes on the Push model
    - Normally, App should only use IMtkPb_Ctrl_GetBuffer() and IMtkPb_Ctrl_SendData(), not IMtkPb_Ctrl_ReleaseBuffer().
      IMtkPb_Ctrl_SendData() implies that the ownership of the buffer is sent back to the MtkPbLib.  IMtkPb_Ctrl_ReleaseBuffer()
      is only used when App calls IMtkPb_Ctrl_GetBuffer() but finds that it has no more data to send.
    - App is allowed to call IMtkPb_Ctrl_GetBuffer() several times, and then calls IMtkPb_Ctrl_SendData() or IMtkPb_Ctrl_ReleaseBuffer()
      several times.  It's also allowed that the order of IMtkPb_Ctrl_SendData() or IMtkPb_Ctrl_ReleaseBuffer() is
      different from the order of IMtkPb_Ctrl_GetBuffer().
    - The address of IMtkPb_Ctrl_SendData()'s pu1PushBuf must be from one of the previous IMtkPb_Ctrl_GetBuffer()'s ppu1PushBuf.
      It's not allowed that App calls IMtkPb_Ctrl_GetBuffer() once and separate the buffer to two pieces and calls IMtkPb_Ctrl_SendData() twice.
      However, it's allowed that IMtkPb_Ctrl_SendData()'s pu1BufSize is smaller than the corresponding IMtkPb_Ctrl_GetBuffer()'s pu1BufSize.
    - When an IMtkPb_Ctrl_GetBuffer() returns ::IMTK_PB_ERROR_CODE_GET_BUF_PENDING, the App shouldn't call another IMtkPb_Ctrl_GetBuffer().  The App should wait until it receives an ::IMTK_PB_CTRL_EVENT_GET_BUF_READY event before calling another IMtkPb_Ctrl_GetBuffer().
    - Because App and MtkPbLib are not synchronized, after App calls IMtkPb_Ctrl_CancelGetBufferPending(), it may still
      get a ::IMTK_PB_CTRL_EVENT_GET_BUF_READY event.  App must handle this case.

  @section SyncNotes Notes on audio/video synchronization
    - A playback control instance could be set to be synchronized or asynchronized.  A synchronized instance plays audio and video in synchronization.
      Also, a synchronized instance occupies the system's synchronization resource.
    - The system has only one synchronization resource, so if the App creates multiple playback control instances, only one of them could be a synchronized instance.
    - A Lib Master instance is always synchronized.  This implies that the App cannot create more than one Lib Master instances at the same time.
    - A video-only or audio-only App Master instance could be configured as synchronized or asynchronized by setting ::IMTK_PB_CTRL_SET_MEDIA_INFO_T.fgSynchronized to be true or false.
    - A video/audio App Master instance must be set to be synchronized.
    - IMtkPb_Ctrl_GetCurrentPTS() is only available in a synchronized App Master instance.

  @section PlayFlagNotes Notes on play flag (IMTK_PB_CTRL_ENGINE_PARAM_T.u4PlayFlag)
    - Lib Master model:
      - If App knows the nature of the file, it uses this flag to tell MtkPbLib.  For example, if App is going to play a WMA file, it should set ::IMTK_PB_CTRL_PLAY_FLAG_AUDIO and clear ::IMTK_PB_CTRL_PLAY_FLAG_VIDEO.
      - For a media type that could contain audio and video (e.g., ASF), the App may not know whether this file actually has audio or video.  In this case, the App could set ::IMTK_PB_CTRL_PLAY_FLAG_UNKNOWN, or just set both ::IMTK_PB_CTRL_PLAY_FLAG_AUDIO and ::IMTK_PB_CTRL_PLAY_FLAG_VIDEO.
      - If a file has audio and video, it's not allowed for the App to disable one and enable the other.
    - App Master model:
      - The App should know whether a file has audio and video, so the App shouldn't set ::IMTK_PB_CTRL_PLAY_FLAG_UNKNOWN.
      - If a file has audio and video, the App can use this flag to disable one and enable the other.

  @section PlayEndNotes Notes on play to end of stream
    - In both Lib Master and App Master model, when MtkPbLib finishes the playback, it sends ::IMTK_PB_CTRL_EVENT_EOS event to the App.  However, it doesn't automatically enter READY state.  The App has to call IMtkPb_Ctrl_Stop() explicitly.  See Figures 1-4, 2-4, 3-3, and 4-3 for detailed sequences.


  @section DisplayRectangleNotes Notes on IMtkPb_Ctrl_SetDisplayRectangle()
    - The ptSrcRect parameter is the clipping area of the source picture.  It indicates which part of the source video will be shown.
      - u4X, u4Y, u4W, u4H are all relative values, independent of the actual picture width and height.
      - For u4X, 0 is the leftmost column, while 9999 means the rightmost column.
      - For u4Y, 0 is the topmost row, while 9999 means the bottommost row.
      - For u4W, 10000 means the full width of the picture.
      - For u4H, 10000 means the full height of the picture.
    - The ptDstRect parameter describes where the video will be placed and with what size.  The video may already be clipped by ptSrcRect.
      - u4X, u4Y, u4W, u4H are all relative values, independent of the actual output resolution.
      - For u4X, 0 is the leftmost part of the screen, while 9999 means the rightmost side of the screen.
      - For u4Y, 0 is the topmost part of the screen, while 9999 means the bottommost part of the screen.
      - For u4W, 10000 means the full width of the screen.
      - For u4H, 10000 means the full height of the screen.

*/



/*----------------------------------------------------------------------------*/
/*! @addtogroup IMtkPb_Ctrl
 *  @{ 
 */
/*----------------------------------------------------------------------------*/
#ifndef _I_MTK_PB_CTRL_
#define _I_MTK_PB_CTRL_

#ifdef __cplusplus
extern "C" {
#endif

/*-----------------------------------------------------------------------------
                    include files
-----------------------------------------------------------------------------*/
#include "IMtkPb_ErrorCode.h"

/*-----------------------------------------------------------------------------
                    macros, defines, typedefs, enums
-----------------------------------------------------------------------------*/
#ifndef IMTK_CTRL_ISO_639_LANG_LEN
#define IMTK_CTRL_ISO_639_LANG_LEN 4
#endif

/*! @enum   IMTK_PB_CTRL_SPEED_T 
 *  @brief  Speed of Playback
 */
typedef enum
{   
    IMTK_PB_CTRL_SPEED_FR_32X   =   -3200000,   ///< Fast Rewind 32X   
    IMTK_PB_CTRL_SPEED_FR_16X   =   -1600000,   ///< Fast Rewind 16X  
    IMTK_PB_CTRL_SPEED_FR_8X    =   -800000,    ///< Fast Rewind 8X   
    IMTK_PB_CTRL_SPEED_FR_4X    =   -400000,    ///< Fast Rewind 4X   
    IMTK_PB_CTRL_SPEED_FR_3X    =   -300000,    ///< Fast Rewind 4X  
    IMTK_PB_CTRL_SPEED_FR_2X    =   -200000,    ///< Fast Rewind 2X   
    IMTK_PB_CTRL_SPEED_FR_1X    =   -100000,    ///< Fast Rewind 2X  
    IMTK_PB_CTRL_SPEED_FR_1_2X  =   -50000,     ///< Slow Rewind 1/2X
    IMTK_PB_CTRL_SPEED_FR_1_3X  =   -33333,     ///< Slow Rewind 1/3X
    IMTK_PB_CTRL_SPEED_FR_1_4X  =   -25000,     ///< Slow Rewind 1/4X
    IMTK_PB_CTRL_SPEED_FR_1_8X  =   -12500,     ///< Slow Rewind 1/8X
    IMTK_PB_CTRL_SPEED_FR_1_16X =   -6250,      ///< Slow Rewind 1/16X
    IMTK_PB_CTRL_SPEED_FR_1_32X =   -3125,      ///< Slow Rewind 1/32X
    IMTK_PB_CTRL_SPEED_ZERO     =   0,          ///< paused; only for IMtkPb_Ctrl_GetPlayParam() and not for IMtkPb_Ctrl_SetSpeed().
    IMTK_PB_CTRL_SPEED_FF_1_32X =   3125,       ///< Slow Forward 1/32X
    IMTK_PB_CTRL_SPEED_FF_1_16X =   6250,       ///< Slow Forward 1/16X
    IMTK_PB_CTRL_SPEED_FF_1_8X  =   12500,      ///< Slow Forward 1/8X
    IMTK_PB_CTRL_SPEED_FF_1_4X  =   25000,      ///< Slow Forward 1/4X
    IMTK_PB_CTRL_SPEED_FF_1_3X  =   33333,      ///< Slow Forward 1/3X
    IMTK_PB_CTRL_SPEED_FF_1_2X  =   50000,      ///< Slow Forward 1/2X
    IMTK_PB_CTRL_SPEED_1X       =   100000,     ///< normal play; only for IMtkPb_Ctrl_GetPlayParam() and not for IMtkPb_Ctrl_SetSpeed().
    IMTK_PB_CTRL_SPEED_FF_2X    =   200000,     ///< Fast Forward 2X  
    IMTK_PB_CTRL_SPEED_FF_3X    =   300000,     ///< Fast Forward 3X   
    IMTK_PB_CTRL_SPEED_FF_4X    =   400000,     ///< Fast Forward 4X  
    IMTK_PB_CTRL_SPEED_FF_8X    =   800000,     ///< Fast Forward 8X  
    IMTK_PB_CTRL_SPEED_FF_16X   =   1600000,    ///< Fast Forward 16X 
    IMTK_PB_CTRL_SPEED_FF_32X   =   3200000     ///< Fast Forward 32X 
} IMTK_PB_CTRL_SPEED_T;


/*! @enum   IMTK_PB_CTRL_STATE_T 
 *  @brief  playback state 
 */
typedef enum
{
    IMTK_PB_CTRL_UNKNOWN        =   0,  ///< Unknown state            
    IMTK_PB_CTRL_CLOSED,                ///< This CLOSED state actually doesn't exist.  IMtkPb_Ctrl_GetState() should never return this state.  As long as a playback instance exists, it should at least be in the OPENED state.               
    IMTK_PB_CTRL_OPENED,                ///< MtkPbLib enters this state when it's just opened by IMtkPb_Ctrl_Open().
    IMTK_PB_CTRL_TO_SETINFO,            ///< This state only happens in App Master model.  It means the MtkPbLib still needs an IMtkPb_Ctrl_SetMediaInfo() call to enter the READY state.
    IMTK_PB_CTRL_READY,                 ///< This state means the MtkPbLib is ready to accept IMtkPb_Ctrl_Play() command.
    IMTK_PB_CTRL_PLAYING,               ///< This state means the MtkPbLib is playing the content.  No matter what playback mode or speed it is playing, it's in this state.
    IMTK_PB_CTRL_PAUSED                 ///< This state means the playback is paused by IMtkPb_Ctrl_Pause().
} IMTK_PB_CTRL_STATE_T;


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


/*! @struct IMTK_PB_CTRL_SETTING_T 
 *  @brief  playback control setting structure
 */ 
typedef struct
{   
    uint16_t                u2ATrack;           ///< audio track number
    IMTK_PB_CTRL_SPEED_T    eSpeed;             ///< Playback speed
} IMTK_PB_CTRL_SETTING_T;


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
    IMTK_PB_CTRL_MEDIA_TYPE_MTK_P0,      ///<  MTK Private format 0 (App Master only, described in @ref MTKP0page)
    IMTK_PB_CTRL_MEDIA_TYPE_FLV
} IMTK_PB_CTRL_MEDIA_TYPE_T;


/*! @enum   IMTK_PB_CTRL_VID_ENC_T 
 *  @brief  video codec type 
 */
typedef enum
{
    IMTK_PB_CTRL_VID_ENC_UNKNOWN = 0,       ///< Unknown video format
    IMTK_PB_CTRL_VID_ENC_MPEG1_2,           ///< MPEG1 & MPEG2
    IMTK_PB_CTRL_VID_ENC_MPEG4,             ///< MPEG4               
    IMTK_PB_CTRL_VID_ENC_H264,              ///< H.264               
    IMTK_PB_CTRL_VID_ENC_H263,              ///< H.263
    IMTK_PB_CTRL_VID_ENC_VC1,               ///< VC1                 
    IMTK_PB_CTRL_VID_ENC_WMV1,              ///< Windows Media Video V7
    IMTK_PB_CTRL_VID_ENC_WMV2,              ///< Windows Media Video V8
    IMTK_PB_CTRL_VID_ENC_WMV3,              ///< Windows Media Video V9
    IMTK_PB_CTRL_VID_ENC_DIVX311,           ///< DivX 3.11, also known as MP43
    IMTK_PB_CTRL_VID_ENC_RV8,               ///< RealVideo 8
    IMTK_PB_CTRL_VID_ENC_RV9_10,            ///< RealVideo 9 & 10
    IMTK_PB_CTRL_VID_ENC_MJPEG,             ///< Motion JPEG
    IMTK_PB_CTRL_VID_ENC_SORENSON_SPARK,     ///< Sorenson Spark
    IMTK_PB_CTRL_VID_ENC_VP6,               ///< VP6
    IMTK_PB_CTRL_VID_ENC_VP8                ///< VP8
} IMTK_PB_CTRL_VID_ENC_T;


/*! @enum   IMTK_PB_CTRL_AUD_ENC_T 
 *  @brief  audio codec type
 */
typedef enum
{
    IMTK_PB_CTRL_AUD_ENC_UNKNOWN = 0,       ///< Unknown Audio Codec Type
    IMTK_PB_CTRL_AUD_ENC_MPEG,              ///< MPEG1 or MPEG2 audio, layer I or layer II
    IMTK_PB_CTRL_AUD_ENC_MP3,               ///< MPEG1 or MPEG2 audio, layer III
    IMTK_PB_CTRL_AUD_ENC_AAC,               ///< AAC, or HE-AAC
    IMTK_PB_CTRL_AUD_ENC_DD,                ///< Dolby Digital, or Dolby Digital Plus
    IMTK_PB_CTRL_AUD_ENC_TRUEHD,            ///< Dolby TrueHD
    IMTK_PB_CTRL_AUD_ENC_PCM,               ///< PCM
    IMTK_PB_CTRL_AUD_ENC_DTS,               ///< DTS
    IMTK_PB_CTRL_AUD_ENC_DTS_HD_HR,         ///< DTS-HD High Resolution
    IMTK_PB_CTRL_AUD_ENC_DTS_HD_MA,         ///< DTS-HD Master Audio
    IMTK_PB_CTRL_AUD_ENC_WMA,               ///< WMA standard; not WMA Pro nor WMA Lossless
    IMTK_PB_CTRL_AUD_ENC_COOK,              ///< Cook
    IMTK_PB_CTRL_AUD_ENC_VORBIS,            ///< Vorbis
    IMTK_PB_CTRL_AUD_ENC_FLAC,              ///< Free Lossless Audio Codec
    IMTK_PB_CTRL_AUD_ENC_MONKEY             ///< Monkey's Audio
} IMTK_PB_CTRL_AUD_ENC_T;


/*! @enum   IMTK_PB_CTRL_FRAME_RATE_T
 *  @brief  Video Frame Rate
 */
typedef enum
{
    IMTK_PB_CTRL_FRAME_RATE_UNKNOWN = 0,    ///< Specifying "unknown frame rate" means we will adopt the frame rate embedded in bitstream for playback
    IMTK_PB_CTRL_FRAME_RATE_23_976,         ///< 24000/1001 (23.976...)
    IMTK_PB_CTRL_FRAME_RATE_24,
    IMTK_PB_CTRL_FRAME_RATE_25,
    IMTK_PB_CTRL_FRAME_RATE_29_97,          ///< 30000/1001 (29.97...)
    IMTK_PB_CTRL_FRAME_RATE_30,
    IMTK_PB_CTRL_FRAME_RATE_50,
    IMTK_PB_CTRL_FRAME_RATE_59_94,          ///< 60000/1001 (59.94...)
    IMTK_PB_CTRL_FRAME_RATE_60,
    IMTK_PB_CTRL_FRAME_RATE_120,
    IMTK_PB_CTRL_FRAME_RATE_1,
    IMTK_PB_CTRL_FRAME_RATE_5,
    IMTK_PB_CTRL_FRAME_RATE_8,
    IMTK_PB_CTRL_FRAME_RATE_10,
    IMTK_PB_CTRL_FRAME_RATE_12,
    IMTK_PB_CTRL_FRAME_RATE_15,
    IMTK_PB_CTRL_FRAME_RATE_2,
    IMTK_PB_CTRL_FRAME_RATE_6,
    IMTK_PB_CTRL_FRAME_RATE_48,
    IMTK_PB_CTRL_FRAME_RATE_70,
    IMTK_PB_CTRL_FRAME_RATE_VARIABLE        ///< Specifying "variable frame rate" means we will adopt PTS specified in bitstream for playback.  The framerate specified in bitstream will be ignored.  Be sure that PTS is available for each picture.
} IMTK_PB_CTRL_FRAME_RATE_T;


/*! @enum   IMTK_PB_CTRL_SRC_ASPECT_RATIO_T
 *  @brief  source aspect ratio value
 */
typedef enum
{
    IMTK_PB_CTRL_SRC_ASPECT_RATIO_UNKNOWN = 0,  ///< aspect ratio unknown
    IMTK_PB_CTRL_SRC_ASPECT_RATIO_4_3,          ///< 4:3
    IMTK_PB_CTRL_SRC_ASPECT_RATIO_16_9,         ///< 16:9
    IMTK_PB_CTRL_SRC_ASPECT_RATIO_CUSTOMIZED    ///< other ratio
} IMTK_PB_CTRL_SRC_ASPECT_RATIO_T;


/*! @struct IMTK_PB_CTRL_SRC_ASPECT_RATIO_INFO_T
 *  @brief  source aspect ratio information, specifying the preferred display aspect ratio
 */
typedef struct
{
    IMTK_PB_CTRL_SRC_ASPECT_RATIO_T eAspectRatio;   ///< aspect ratio
    uint32_t u4DispWidth;                           ///< This value is used when eAspectRatio is ::IMTK_PB_CTRL_SRC_ASPECT_RATIO_CUSTOMIZED.  The ratio is u4DispWidth / u4DispHeight.
    uint32_t u4DispHeight;                          ///< This value is used when eAspectRatio is ::IMTK_PB_CTRL_SRC_ASPECT_RATIO_CUSTOMIZED.  The ratio is u4DispWidth / u4DispHeight.
} IMTK_PB_CTRL_SRC_ASPECT_RATIO_INFO_T;


#define IMTK_PB_UNKNOWN_VALUE       (-1)


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


/*! @struct IMTK_PB_CTRL_BUFFER_STATUS_T
 *  @brief buffer info (This buffer means the bitstream buffer inside the MtkPbLib)
 */
typedef struct
{
    uint32_t u4BufBeginTime;    ///< the playback time associated with the beginning of buffered data, in millisecond
    uint32_t u4BufEndTime;      ///< the playback time associated with the end of buffered data, in millisecond
    uint32_t u4BufRemainDur;    ///< the playback duration from current playback point to the end of buffered data, in millisecond
    bool     fgBufFull;         ///< true: buffer is absolutely full; false: buffer is not full
} IMTK_PB_CTRL_BUFFER_STATUS_T;

#define IMTK_PB_VID_CODEC_PRIV_INFO_LEN 100         ///< video codec private info length


/*! @struct IMTK_PB_CTRL_AUD_CH_NUM_T
 *  @brief  Audio channel number
 */
typedef enum
{
    IMTK_PB_CTRL_AUD_CH_MONO        =   1,
    IMTK_PB_CTRL_AUD_CH_STEREO,
    IMTK_PB_CTRL_AUD_CH_SURROUND_2CH,
    IMTK_PB_CTRL_AUD_CH_SURROUND,
    IMTK_PB_CTRL_AUD_CH_3_0,
    IMTK_PB_CTRL_AUD_CH_4_0,
    IMTK_PB_CTRL_AUD_CH_5_0,
    IMTK_PB_CTRL_AUD_CH_5_1,
    IMTK_PB_CTRL_AUD_CH_7_1
} IMTK_PB_CTRL_AUD_CH_NUM_T;

/*! @struct IMTK_PB_CTRL_AUD_SAMPLE_RATE_T
 *  @brief  Audio sample rate
 */
typedef enum
{
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_8K   =   1,
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_16K,
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_32K,
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_11K,
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_22K,
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_44K,
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_12K,
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_24K,
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_48K,
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_96K,
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_192K
} IMTK_PB_CTRL_AUD_SAMPLE_RATE_T;

/*! @struct IMTK_PB_CTRL_AUD_PCM_BIT_DEPTH_T
 *  @brief  Audio PCM bit depth
 */
typedef enum
{
    IMTK_PB_CTRL_AUD_PCM_BIT_DEPTH_8   =   1,
    IMTK_PB_CTRL_AUD_PCM_BIT_DEPTH_16,
    IMTK_PB_CTRL_AUD_PCM_BIT_DEPTH_20,
    IMTK_PB_CTRL_AUD_PCM_BIT_DEPTH_24
} IMTK_PB_CTRL_AUD_PCM_BIT_DEPTH_T;

/*! @struct IMTK_PB_CTRL_AUD_WMA_INFO_T 
 *  @brief  WMA Codec Info structure
 */    
typedef struct
{
    uint16_t                        u2FormatTag;        ///< from wFormatTag of the WAVEFORMATEX structure; must be set to 0x161.
    IMTK_PB_CTRL_AUD_CH_NUM_T       eNumCh;             ///< from nChannels of the WAVEFORMATEX structure
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_T  eSampleRate;        ///< from nSamplesPerSec of the WAVEFORMATEX structure
    uint32_t                        u4AvgBytesPerSec;   ///< from nAvgBytesPerSec of the WAVEFORMATEX structure
    uint16_t                        u2BlockAlign;       ///< from nBLockAlign of the WAVEFORMATEX structure
    uint16_t                        u2EncoderOpt;       ///< from wEncodeOptions of the WMA type-specific data
} IMTK_PB_CTRL_AUD_WMA_INFO_T;


/*! @enum   IMTK_PB_CTRL_AUD_PCM_TYPE_T 
 *  @brief  PCM type 
 */
typedef enum
{
    IMTK_PB_CTRL_AUD_PCM_TYPE_NORMAL        =   1,
    IMTK_PB_CTRL_AUD_PCM_TYPE_MS_ADPCM,
    IMTK_PB_CTRL_AUD_PCM_TYPE_IMA_ADPCM
} IMTK_PB_CTRL_AUD_PCM_TYPE_T;


/*! @struct IMTK_PB_CTRL_AUD_PCM_INFO_T 
 *  @brief  PCM Codec Info structure
 */    
typedef struct
{
    IMTK_PB_CTRL_AUD_PCM_TYPE_T     e_pcm_type;         ///< PCM type
    IMTK_PB_CTRL_AUD_CH_NUM_T         eNumCh;             ///< Channels
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_T    eSampleRate;        ///< Sampling Frequence
    uint16_t                        u2BlockAlign;       ///< BLock Alignment
    IMTK_PB_CTRL_AUD_PCM_BIT_DEPTH_T  eBitsPerSample;     ///< Bits per Sample
    bool                            fgBigEndian;        ///< Big Endian PCM
} IMTK_PB_CTRL_AUD_PCM_INFO_T;


/*! @struct IMTK_PB_CTRL_AUD_COOK_INFO_T
 *  @brief  Cook Codec Info structure
 */
typedef struct
{
    IMTK_PB_CTRL_AUD_CH_NUM_T       eNumCh;             ///< channel configurations
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_T  eSampleRate;        ///< sampling rate
    uint16_t                        u2SamplePerFrame;   ///< sample per frame
    uint32_t                        u4FrameSize;        ///< frame size in byte
    uint16_t                        u2RegionNum;        ///< Number of consecutive Modulated Lapped Transform coefficients that share the same quantizer value
    uint32_t                        u4CplRegionStart;   ///< Coupling start region: Starting point for the band of quantization bits
    uint16_t                        u2QBitsNum;         ///< Number of quantization bits for each band in the joint stereo coupling information
} IMTK_PB_CTRL_AUD_COOK_INFO_T;

/*! @struct IMTK_PB_CTRL_AUD_COOK_INFO_T
 *  @brief  Cook Codec Info structure
 */
typedef struct
{
    IMTK_PB_CTRL_AUD_CH_NUM_T       eNumCh;             ///< channel configurations
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_T  eSampleRate;        ///< sampling rate
    uint16_t                        u2SamplePerFrame;   ///< sample per frame
    uint32_t                        u4FrameSize;        ///< frame size in byte
    uint16_t                        u2RegionNum;        ///< Number of consecutive Modulated Lapped Transform coefficients that share the same quantizer value
    uint32_t                        u4CplRegionStart;   ///< Coupling start region: Starting point for the band of quantization bits
    uint16_t                        u2QBitsNum;         ///< Number of quantization bits for each band in the joint stereo coupling information
    uint16_t                        u2CookInlvFac;
    uint16_t                        u2CookBlockSz;
    uint16_t                        u2FlavorIdx;
} IMTK_PB_CTRL_AUD_RM_COOK_INFO_T;


/*! @struct IMTK_PB_CTRL_AUD_FLAC_INFO_T
 *  @brief  FLAC Codec Info structure usually defined in METADATA_BLOCK_STREAMINFO of the FLAC file format
 */
typedef struct
{
    uint16_t u2MinBlockSize;    ///< minimum block size
    uint16_t u2MaxBlockSize;    ///< maximum block size
    uint32_t u4MinFrameSize;    ///< minimum frame size
    uint32_t u4MaxFrameSize;    ///< maximum frame size
    uint16_t u2SampleNumHigh12; ///< the higher 12 bits of "total samples in stream".
    uint32_t u4SampleNumLow24;  ///< the lower 24 bits of "total samples in stream".
} IMTK_PB_CTRL_AUD_FLAC_INFO_T;


/*! @struct IMTK_PB_CTRL_AUD_MONKEY_INFO_T
 *  @brief  Monkey's audio codec info structure usually defined in APE file format headers
 */
typedef struct
{
    uint16_t u2Version;             ///< nVersion defined in APE_DESCRIPTOR
    uint16_t u2CompressionLevel;    ///< nCompressionLevel defined in APE_HEADER
    uint32_t u4BlockPerFrame;       ///< nBlocksPerFrame defined in APE_HEADER
    uint32_t u4FinalFrameBlocks;    ///< nFinalFrameBlocks defined in APE_HEADER
    uint32_t u4TotalFrames;         ///< nTotalFrames defined in APE_HEADER
    uint16_t u2BitsPerSample;       ///< nBitsPerSample defined in APE_HEADER
    uint16_t u2Channels;            ///< nChannels defined in APE_HEADER
    uint32_t u4SampleRate;          ///< nSampleRate defined in APE_HEADER
} IMTK_PB_CTRL_AUD_MONKEY_INFO_T;


/*! @struct IMTK_PB_CTRL_VID_WMV1_INFO_T
 *  @brief  WMV7 codec info
 */
typedef struct
{
    uint32_t u4Width;                   ///< picture width
    uint32_t u4Height;                  ///< picture height
} IMTK_PB_CTRL_VID_WMV1_INFO_T;


/*! @struct IMTK_PB_CTRL_VID_WMV2_INFO_T
 *  @brief  WMV8 codec info
 */
typedef struct
{
    uint32_t u4Width;                   ///< picture width
    uint32_t u4Height;                  ///< picture height
} IMTK_PB_CTRL_VID_WMV2_INFO_T;


/*! @struct IMTK_PB_CTRL_VID_WMV3_INFO_T
 *  @brief  WMV9 codec info
 */
typedef struct
{
    uint32_t u4Width;                   ///< picture width
    uint32_t u4Height;                  ///< picture height
} IMTK_PB_CTRL_VID_WMV3_INFO_T;


/*! @struct IMTK_PB_CTRL_VID_DX3_INFO_T
 *  @brief  DivX 3 codec info
 */
typedef struct
{
    uint32_t u4Width;                   ///< picture width
    uint32_t u4Height;                  ///< picture height
    IMTK_PB_CTRL_FRAME_RATE_T eFrmRate; ///< video frame rate
} IMTK_PB_CTRL_VID_DX3_INFO_T;


/*! @enum   IMTK_PB_CTRL_RV_BITS_VERSION_T
 *  @brief  Real Video bits version
 */
typedef enum
{
    IMTK_PB_CTRL_RV_FID_REALVIDEO30     =   1,
    IMTK_PB_CTRL_RV_FID_RV89COMBO       = 2
} IMTK_PB_CTRL_RV_BITS_VERSION_T;

#define IMTK_PB_CTRL_MAX_NUM_RPR_SIZES     16   ///< maximal number of "RPR Size"


/*! @struct IMTK_PB_CTRL_VID_RV8_INFO_T
 *  @brief  Real Video 8 codec info
 */
typedef struct
{
    uint32_t u4Width;                                   ///< picture width
    uint32_t u4Height;                                  ///< picture height
    IMTK_PB_CTRL_FRAME_RATE_T eFrmRate;                 ///< video frame rate
    IMTK_PB_CTRL_RV_BITS_VERSION_T eRvBitsVersion;      ///< RV bits version
    uint32_t u4NumOfRPRSize;                            ///< number of RPR size
    uint32_t u4RPRSize[IMTK_PB_CTRL_MAX_NUM_RPR_SIZES]; ///< RPR size array
} IMTK_PB_CTRL_VID_RV8_INFO_T;


/*! @struct IMTK_PB_CTRL_VID_RV9_10_INFO_T
 *  @brief  Real Video 9/10 codec info
 */
typedef struct
{
    uint32_t u4Width;                                   ///< picture width
    uint32_t u4Height;                                  ///< picture height
    IMTK_PB_CTRL_FRAME_RATE_T eFrmRate;                 ///< video frame rate
    IMTK_PB_CTRL_RV_BITS_VERSION_T eRvBitsVersion;      ///< RV bits version
} IMTK_PB_CTRL_VID_RV9_10_INFO_T;


/*! @struct IMTK_PB_CTRL_VID_MJPEG_INFO_T
 *  @brief  Motion jpeg codec info
 */
typedef struct
{
    IMTK_PB_CTRL_FRAME_RATE_T eFrmRate;                 ///< video frame rate
} IMTK_PB_CTRL_VID_MJPEG_INFO_T;

/*! @struct IMTK_PB_CTRL_VID_H264_INFO_T
 *  @brief  H.264 codec info
 */
typedef struct
{
    IMTK_PB_CTRL_FRAME_RATE_T eFrmRate;                 ///< video frame rate
} IMTK_PB_CTRL_VID_H264_INFO_T;


#define IMTK_PB_CTRL_VID_RESERVED_CODEC_INFO_LEN          128     ///< video reserved bytes length

/*! @struct IMTK_PB_CTRL_VID_RESERVED_CODEC_INFO_T
 *  @brief  Reserved Bytes For Video Info
 */
typedef struct
{
    uint8_t     u1Reserved[IMTK_PB_CTRL_VID_RESERVED_CODEC_INFO_LEN]; ///< Reserved Bytes Array for Video Info
} IMTK_PB_CTRL_VID_RESERVED_CODEC_INFO_T;


/*! @union IMTK_PB_CTRL_VID_CODEC_INFO_T
 *  @brief  Video codec info for each codec
 */
typedef union
{
    IMTK_PB_CTRL_VID_WMV1_INFO_T t_wmv1_info;       ///< WMV7 codec info
    IMTK_PB_CTRL_VID_WMV2_INFO_T t_wmv2_info;       ///< WMV8 codec info
    IMTK_PB_CTRL_VID_WMV3_INFO_T t_wmv3_info;       ///< WMV9 codec info
    IMTK_PB_CTRL_VID_DX3_INFO_T t_dx3_info;         ///< DivX 3 codec info
    IMTK_PB_CTRL_VID_RV8_INFO_T t_rv8_info;         ///< RV8 codec info
    IMTK_PB_CTRL_VID_RV9_10_INFO_T t_rv9_10_info;   ///< RV9/10 codec info
    IMTK_PB_CTRL_VID_MJPEG_INFO_T t_mjpeg_info;     ///< Motion JPEG codec info
    IMTK_PB_CTRL_VID_H264_INFO_T t_h264_info;      ///< H.264 codec info
    IMTK_PB_CTRL_VID_RESERVED_CODEC_INFO_T t_res_info;  ///< reserved bytes for video codec info
} IMTK_PB_CTRL_VID_CODEC_INFO_T;


#define IMTK_PB_CTRL_AUD_RESERVED_CODEC_INFO_LEN          128     ///< audio reserved bytes length

/*! @struct IMTK_PB_CTRL_AUD_RESERVED_CODEC_INFO_T
 *  @brief  Reserved Bytes For Audio Info
 */
typedef struct
{
    uint8_t     u1Reserved[IMTK_PB_CTRL_AUD_RESERVED_CODEC_INFO_LEN]; ///< Reserved Bytes Array for Audio Info
} IMTK_PB_CTRL_AUD_RESERVED_CODEC_INFO_T;


/*! @union IMTK_PB_CTRL_AUD_CODEC_INFO_T
 *  @brief  Audio codec info for each codec
 */
typedef union
{
    IMTK_PB_CTRL_AUD_PCM_INFO_T t_pcm_info;         ///< PCM codec info
    IMTK_PB_CTRL_AUD_WMA_INFO_T t_wma_info;         ///< WMA codec info
    IMTK_PB_CTRL_AUD_COOK_INFO_T t_cook_info;       ///< Cook codec info
    IMTK_PB_CTRL_AUD_FLAC_INFO_T t_flac_info;       ///< FLAC codec info
    IMTK_PB_CTRL_AUD_MONKEY_INFO_T t_monkey_info;   ///< Monkey's audio codec info
    IMTK_PB_CTRL_AUD_RESERVED_CODEC_INFO_T t_res_info;  ///< reserved bytes for audio codec info
} IMTK_PB_CTRL_AUD_CODEC_INFO_T;


/*! @struct IMTK_PB_CTRL_AVI_VID_INFO_T
 *  @brief  AVI video info structure
 */
typedef struct
{
    IMTK_PB_CTRL_VID_ENC_T              eVidEnc;            ///< video codec
    uint8_t         au1FourCC[4];       ///< video 4cc.  If 4cc is "ABCD", au1FourCC[0]=='A', au1FourCC[3]=='D'.
    uint8_t         u1StreamIdx;        ///< e.g.: 00dc: u1StrmIdx = 0, 01dc: u1StrmIdx = 1
    uint32_t        u4Scale;            ///< scale from "strh"
    uint32_t        u4Rate;             ///< rate from "strh"
    uint32_t        u4Width;            ///< picture width
    uint32_t        u4Height;           ///< picture height
    IMTK_PB_CTRL_VID_CODEC_INFO_T uVidCodecInfo;    ///< video codec info
} IMTK_PB_CTRL_AVI_VID_INFO_T;


/*! @struct IMTK_PB_CTRL_AVI_AUD_INFO_T
 *  @brief  AVI audio info structure
 */    
typedef struct
{
    IMTK_PB_CTRL_AUD_ENC_T  eAudEnc;    ///< audio codec
    uint8_t         au1FourCC[4];       ///< Audio 4cc.  If 4cc is "ABCD", au1FourCC[0]=='A', au1FourCC[3]=='D'.
    uint8_t     u1StreamIdx;            ///< e.g.: 01wb: u1StrmIdx = 1, 02wb: u1StrmIdx = 2
    bool        fgVbr;                  ///< true: VBR; false: CBR
    uint32_t    u4Scale;                ///< scale from "strh"
    uint32_t    u4Rate;                 ///< rate from "strh"
    uint32_t    u4Bps;                  ///< byte per second; only used when fgVbr==false
    IMTK_PB_CTRL_AUD_CODEC_INFO_T uAudCodecInfo;    ///< audio codec info
} IMTK_PB_CTRL_AVI_AUD_INFO_T;
     

/*! @struct IMTK_PB_CTRL_MEDIA_AVI_INFO_T
 *  @brief  AVI info structure
 */
typedef struct
{
    IMTK_PB_CTRL_AVI_VID_INFO_T tAviVidInfo;     ///< AVI video info
    IMTK_PB_CTRL_AVI_AUD_INFO_T tAviAudInfo;     ///< AVI audio info
} IMTK_PB_CTRL_MEDIA_AVI_INFO_T;


/*! @struct IMTK_PB_CTRL_FLV_VID_INFO_T
 *  @brief  FLV video info structure
 */
typedef struct
{
    IMTK_PB_CTRL_VID_ENC_T              eVidEnc;            ///< video codec
    uint8_t         u1StreamIdx;        ///< e.g.: 00dc: u1StrmIdx = 0, 01dc: u1StrmIdx = 1
    uint32_t        u4Scale;            ///< scale from "strh"
    uint32_t        u4Rate;             ///< rate from "strh"
    uint32_t        u4Width;            ///< picture width
    uint32_t        u4Height;           ///< picture height
} IMTK_PB_CTRL_FLV_VID_INFO_T;


/*! @struct IMTK_PB_CTRL_FLV_AUD_INFO_T
 *  @brief  FLV audio info structure
 */    
typedef struct
{
    IMTK_PB_CTRL_AUD_ENC_T  eAudEnc;    ///< audio codec
    uint8_t     u1StreamIdx;            ///< e.g.: 01wb: u1StrmIdx = 1, 02wb: u1StrmIdx = 2
    bool        fgVbr;                  ///< true: VBR; false: CBR
    uint32_t    u4Bps;                  ///< byte per second; only used when fgVbr==false
    IMTK_PB_CTRL_AUD_CODEC_INFO_T uAudCodecInfo;
} IMTK_PB_CTRL_FLV_AUD_INFO_T;
     

/*! @struct IMTK_PB_CTRL_MEDIA_FLV_INFO_T
 *  @brief  FLV info structure
 */
typedef struct
{
    IMTK_PB_CTRL_FLV_VID_INFO_T tFlvVidInfo;     ///< FLV video info
    IMTK_PB_CTRL_FLV_AUD_INFO_T tFlvAudInfo;     ///< FLV audio info
} IMTK_PB_CTRL_MEDIA_FLV_INFO_T;


/*! @struct IMTK_PB_CTRL_MPG_STRM_ID_INFO_T
 *  @brief  MPEG stream ID info
 */
typedef struct
{
    uint8_t u1SubStrmIdPriStrm;     ///< sub_stream_id for private_stream_1
    uint8_t u1StrmId;               ///< stream_id
    uint8_t u1SubStrmIdPriStrm2;    ///< sub_stream_id for private_stream_2
    uint8_t u1StrmIdExtension;      ///< stream_id extension
} IMTK_PB_CTRL_MPG_STRM_ID_INFO_T;


/*! @struct IMTK_PB_CTRL_MPEG2_PS_VID_INFO_T
 *  @brief  program stream video info structure
 */
typedef struct
{
    IMTK_PB_CTRL_VID_ENC_T              eVidEnc;            ///< video codec
    IMTK_PB_CTRL_MPG_STRM_ID_INFO_T     tVidStrmIdInfo;     ///< video stream ID info
    IMTK_PB_CTRL_VID_CODEC_INFO_T       uVidCodecInfo;      ///< video codec info
} IMTK_PB_CTRL_MPEG2_PS_VID_INFO_T;


/*! @struct IMTK_PB_CTRL_MPEG2_PS_AUD_INFO_T
 *  @brief  program stream audio info structure
 */
typedef struct
{
    IMTK_PB_CTRL_AUD_ENC_T              eAudEnc;            ///< audio codec
    IMTK_PB_CTRL_MPG_STRM_ID_INFO_T     tAudStrmIdInfo;     ///< audio stream ID info
    IMTK_PB_CTRL_AUD_CODEC_INFO_T       uAudCodecInfo;      ///< audio codec info
} IMTK_PB_CTRL_MPEG2_PS_AUD_INFO_T;


/*! @struct IMTK_PB_CTRL_MEDIA_MPEG2_PS_INFO_T
 *  @brief  program stream info structure
 */
typedef struct
{
    IMTK_PB_CTRL_MPEG2_PS_VID_INFO_T  eVidInfo;       ///< video info
    IMTK_PB_CTRL_MPEG2_PS_AUD_INFO_T  eAudInfo;       ///< audio info
} IMTK_PB_CTRL_MEDIA_MPEG2_PS_INFO_T;


/*! @enum   IMTK_PB_CTRL_MPEG2_TS_PACKET_TYPE_T
 *  @brief  Transport stream type
 */
typedef enum
{
    IMTK_PB_CTRL_MPEG2_TS_PACKET_TYPE_188BYTE = 1,         ///< This is normal TS whith 188-byte packets
    IMTK_PB_CTRL_MPEG2_TS_PACKET_TYPE_192BYTE = 2          ///< This is timestamped TS with 192-byte packets
} IMTK_PB_CTRL_MPEG2_TS_PACKET_TYPE_T;


/*! @struct IMTK_PB_CTRL_MPEG2_TS_VID_INFO_T
 *  @brief  transport stream video info structure
 */
typedef struct
{
    IMTK_PB_CTRL_VID_ENC_T              eVidEnc;            ///< video codec
    uint16_t                            u2Pid;              ///< PID
    IMTK_PB_CTRL_VID_CODEC_INFO_T uVidCodecInfo;    ///< video codec info
} IMTK_PB_CTRL_MPEG2_TS_VID_INFO_T;


/*! @struct IMTK_PB_CTRL_MPEG2_TS_AUD_INFO_T
 *  @brief  program stream audio info structure
 */
typedef struct
{
    IMTK_PB_CTRL_AUD_ENC_T              eAudEnc;            ///< audio codec
    uint16_t                            u2Pid;              ///< PID
    IMTK_PB_CTRL_AUD_CODEC_INFO_T       uAudCodecInfo;      ///< audio codec info
} IMTK_PB_CTRL_MPEG2_TS_AUD_INFO_T;


/*! @struct IMTK_PB_CTRL_MEDIA_MPEG2_TS_INFO_T
 *  @brief  Transport stream info structure
 */
typedef struct
{
    IMTK_PB_CTRL_MPEG2_TS_PACKET_TYPE_T ePacketType;    ///< transport stream packet type
    IMTK_PB_CTRL_MPEG2_TS_VID_INFO_T    eVidInfo;       ///< video info
    IMTK_PB_CTRL_MPEG2_TS_AUD_INFO_T    eAudInfo;       ///< audio info
} IMTK_PB_CTRL_MEDIA_MPEG2_TS_INFO_T;


/*! @struct IMTK_PB_CTRL_ASF_VID_INFO_T
 *  @brief  AVI video info structure
 */
typedef struct
{
    IMTK_PB_CTRL_VID_ENC_T  eVidEnc;    ///< audio codec
    uint8_t  au1FourCC[4];                                      ///< video 4cc.  If 4cc is "ABCD", au1FourCC[0]=='A', au1FourCC[3]=='D'.
    uint8_t  u1StreamIdx;                                       ///< stream index
    uint32_t  u4_Scale;
    uint32_t  u4_Rate;
    uint16_t u2CodecPrivInfoLen;                                ///< length of valid data in au1CodecPrivInfo
    uint8_t  au1CodecPrivInfo[IMTK_PB_VID_CODEC_PRIV_INFO_LEN]; ///< video codec private info; e.g., sequence header
    IMTK_PB_CTRL_VID_CODEC_INFO_T uVidCodecInfo;                ///< video codec info
} IMTK_PB_CTRL_ASF_VID_INFO_T;


/*! @struct IMTK_PB_CTRL_ASF_AUD_INFO_T
 *  @brief  AVI audio info structure
 */
typedef struct
{
    IMTK_PB_CTRL_AUD_ENC_T  eAudEnc;    ///< audio codec
    uint8_t     u1StreamIdx;            ///< stream index
    IMTK_PB_CTRL_AUD_CODEC_INFO_T uAudCodecInfo;    ///< audio codec info
} IMTK_PB_CTRL_ASF_AUD_INFO_T;


/*! @struct IMTK_PB_CTRL_MEDIA_ASF_INFO_T
 *  @brief  ASF info structure
 */
typedef struct
{
    uint32_t u4DataPacketSize;
    uint64_t u8DataPacketCount;
    uint64_t u8PrerollTime;
    IMTK_PB_CTRL_ASF_VID_INFO_T tAsfVidInfo;     ///< ASF video info
    IMTK_PB_CTRL_ASF_AUD_INFO_T tAsfAudInfo;     ///< ASF audio info
} IMTK_PB_CTRL_MEDIA_ASF_INFO_T;


/*! @struct IMTK_PB_CTRL_MKV_VID_INFO_T
 *  @brief  MKV video info structure
 */
typedef struct
{
    bool fgUseFourCC;                                           ///< true: uVidCodec.au1FourCC is valid; false: uVidCodec.eVidEnc is valid
    union
    {
        uint8_t au1FourCC[4];                                   ///< video 4cc.  If 4cc is "ABCD", au1FourCC[0]=='A', au1FourCC[3]=='D'.
        IMTK_PB_CTRL_VID_ENC_T eVidEnc;                         ///< video codec
    } uVidCodec;
    uint64_t u8VidTrackNo;                                      ///< TrackNumber element defined in the TrackEntry element
    uint64_t u8TrackTimeCodeScale;                                ///< TrackTimeCodeScale element defined in the TrackEntry element.  A value of 0 means invalid.
    uint16_t u2CodecPrivInfoLen;                                ///< length of valid data in au1CodecPrivInfo
    uint8_t  au1CodecPrivInfo[IMTK_PB_VID_CODEC_PRIV_INFO_LEN]; ///< video codec private info; e.g., sequence header
    IMTK_PB_CTRL_VID_CODEC_INFO_T uVidCodecInfo;                ///< video codec info
} IMTK_PB_CTRL_MKV_VID_INFO_T;


/*! @struct IMTK_PB_CTRL_MKV_AUD_INFO_T
 *  @brief  MKV audio info structure
 */
typedef struct
{
    IMTK_PB_CTRL_AUD_ENC_T eAudEnc;     ///< audio codec
    uint64_t u8AudTrackNo;              ///< TrackNumber element defined in the TrackEntry element
    uint64_t u8TrackTimeCodeScale;        ///< TrackTimeCodeScale element defined in the TrackEntry element.  A value of 0 means invalid.
    IMTK_PB_CTRL_AUD_CODEC_INFO_T uAudCodecInfo;    ///< audio codec info
} IMTK_PB_CTRL_MKV_AUD_INFO_T;


/*! @struct IMTK_PB_CTRL_RM_VID_INFO_T
 *  @brief  RM video info structure
 */
typedef struct
{
    IMTK_PB_CTRL_VID_ENC_T  eVidEnc;     
    uint16_t                ui2_frm_width;
    uint16_t                ui2_frm_height;
    uint32_t                ui4_spo_extra_flags;
    uint32_t                aui4_specific[25];
    bool                    b_is_rm8;
    IMTK_PB_CTRL_VID_CODEC_INFO_T uVidCodecInfo;                ///< video codec info
} IMTK_PB_CTRL_RM_VID_INFO_T;


/*! @struct IMTK_PB_CTRL_RM_AUD_INFO_T
 *  @brief  RM audio info structure
 */
typedef struct
{
    IMTK_PB_CTRL_AUD_ENC_T              eAudEnc;                    ///< audio codec
    IMTK_PB_CTRL_AUD_CODEC_INFO_T uAudCodecInfo;    ///< audio codec info
} IMTK_PB_CTRL_RM_AUD_INFO_T;


/*! @struct IMTK_PB_CTRL_MEDIA_MKV_INFO_T
 *  @brief  MKV info structure
 */
typedef struct
{
    uint64_t                    u8TimeCodeScale;    ///< TimeCodeScale defined in SegmentInfo
    IMTK_PB_CTRL_MKV_VID_INFO_T tMkvVidInfo;        ///< MKV video info
    IMTK_PB_CTRL_MKV_AUD_INFO_T tMkvAudInfo;        ///< MKV audio info
} IMTK_PB_CTRL_MEDIA_MKV_INFO_T;


/*! @struct IMTK_PB_CTRL_MEDIA_RM_INFO_T
 *  @brief  RM info structure
 */
typedef struct
{
    IMTK_PB_CTRL_RM_VID_INFO_T  tRmVidInfo;        ///< RM video info
    IMTK_PB_CTRL_RM_AUD_INFO_T  tRmAudInfo;        ///< RM audio info
} IMTK_PB_CTRL_MEDIA_RM_INFO_T;


/*! @struct IMTK_PB_CTRL_OGG_VID_INFO_T
 *  @brief  OGG video info structure
 */
typedef struct
{
    uint32_t               u4SerialNumber;          ///< stream serial number defined in the ogg spec
    IMTK_PB_CTRL_VID_ENC_T eVidEnc;                 ///< video codec
    IMTK_PB_CTRL_VID_CODEC_INFO_T uVidCodecInfo;    ///< video codec info
} IMTK_PB_CTRL_OGG_VID_INFO_T;

/*! @struct IMTK_PB_CTRL_OGG_AUD_INFO_T
 *  @brief  OGG audio info structure
 */
typedef struct
{
    uint32_t               u4SerialNumber;          ///< stream serial number defined in the ogg spec
    IMTK_PB_CTRL_AUD_ENC_T eAudEnc;                 ///< audio codec
    IMTK_PB_CTRL_AUD_CODEC_INFO_T uAudCodecInfo;    ///< audio codec info
} IMTK_PB_CTRL_OGG_AUD_INFO_T;


/*! @struct IMTK_PB_CTRL_MEDIA_OGG_INFO_T
 *  @brief  OGG info structure
 */
typedef struct
{
    IMTK_PB_CTRL_OGG_VID_INFO_T tOggVidInfo;        ///< OGG video info
    IMTK_PB_CTRL_OGG_AUD_INFO_T tOggAudInfo;        ///< OGG audio info
} IMTK_PB_CTRL_MEDIA_OGG_INFO_T;


/*! @struct IMTK_PB_CTRL_FLAC_AUD_INFO_T
 *  @brief  OGG audio info structure
 */
typedef struct
{
    IMTK_PB_CTRL_AUD_ENC_T eAudEnc;                 ///< audio codec; the value must be ::IMTK_PB_CTRL_AUD_ENC_FLAC
    IMTK_PB_CTRL_AUD_CODEC_INFO_T uAudCodecInfo;    ///< audio codec info
} IMTK_PB_CTRL_FLAC_AUD_INFO_T;


/*! @struct IMTK_PB_CTRL_MEDIA_FLAC_INFO_T
 *  @brief  media info structure for the FLAC file format
 */
typedef struct
{
    IMTK_PB_CTRL_FLAC_AUD_INFO_T tFlacAudInfo;        ///< FLAC audio info
} IMTK_PB_CTRL_MEDIA_FLAC_INFO_T;


/*! @struct IMTK_PB_CTRL_APE_AUD_INFO_T
 *  @brief  APE audio info structure
 */
typedef struct
{
    IMTK_PB_CTRL_AUD_ENC_T eAudEnc;                 ///< audio codec; the value must be ::IMTK_PB_CTRL_AUD_ENC_MONKEY
    IMTK_PB_CTRL_AUD_CODEC_INFO_T uAudCodecInfo;    ///< audio codec info
} IMTK_PB_CTRL_APE_AUD_INFO_T;


/*! @struct IMTK_PB_CTRL_MEDIA_APE_INFO_T
 *  @brief  media info structure for the APE file format
 */
typedef struct
{
    IMTK_PB_CTRL_APE_AUD_INFO_T tApeAudInfo;        ///< APE file audio info
} IMTK_PB_CTRL_MEDIA_APE_INFO_T;


/*! @struct IMTK_PB_CTRL_MEDIA_VIDEO_ES_INFO_T
 *  @brief  Video ES info structure
 */
typedef struct
{
    IMTK_PB_CTRL_VID_ENC_T              eVidEnc;    ///< Video codec of the elementary stream; at least MPEG1, MPEG2, MPEG4, H264 have to be supported.
} IMTK_PB_CTRL_MEDIA_VIDEO_ES_INFO_T;


/*! @struct IMTK_PB_CTRL_BASIC_AUDIO_INFO_T
 *  @brief  basic audio info structure, used by used by ::IMTK_PB_CTRL_MEDIA_TYPE_AUDIO_ES and ::IMTK_PB_CTRL_MEDIA_TYPE_MTK_P0
 */
typedef struct
{
    IMTK_PB_CTRL_AUD_ENC_T              eAudEnc;            ///< Audio codec
    IMTK_PB_CTRL_AUD_CODEC_INFO_T uAudCodecInfo;            ///< audio codec info
} IMTK_PB_CTRL_BASIC_AUDIO_INFO_T;
     
     
/*! @struct IMTK_PB_CTRL_MEDIA_AUDIO_ES_INFO_T
 *  @brief  Audio ES info structure
 */
typedef struct
{
    IMTK_PB_CTRL_BASIC_AUDIO_INFO_T tAudInfo;   ///< audio info of the elementary stream; at least MP3, AAC, and PCM have to be supported
} IMTK_PB_CTRL_MEDIA_AUDIO_ES_INFO_T;


/*! @struct IMTK_PB_CTRL_MTK_P0_VID_INFO_T
 *  @brief  MTK P0 video info structure
 */
typedef struct
{
    IMTK_PB_CTRL_VID_ENC_T    eVidEnc;      ///< Video codec
    IMTK_PB_CTRL_SRC_ASPECT_RATIO_INFO_T    tAspRatio;        ///< This parameter allows App to tell MtkPbLib its preferred display aspect ratio for this content.  Unless ::IMTK_PB_CTRL_SRC_ASPECT_RATIO_UNKNOWN is specified, it will override the aspect ratio possibly specified in the video elementary stream,
    IMTK_PB_CTRL_VID_CODEC_INFO_T           uVidCodecInfo;    ///< video codec info
} IMTK_PB_CTRL_MTK_P0_VID_INFO_T;


/*! @struct IMTK_PB_CTRL_MEDIA_MTK_P0_INFO_T
 *  @brief  MTK P0 info structure
 */
typedef struct
{
    IMTK_PB_CTRL_MTK_P0_VID_INFO_T      tVidInfo;           ///< Video info
    IMTK_PB_CTRL_BASIC_AUDIO_INFO_T     tAudInfo;           ///< Audio info
} IMTK_PB_CTRL_MEDIA_MTK_P0_INFO_T;


/*! @struct IMTK_PB_CTRL_RSVT_VID_INFO_T
 *  @brief  video info structure for the fictitious RSVT media type
 */
typedef struct
{
    IMTK_PB_CTRL_VID_ENC_T                  eVidEnc;          ///< Video codec
    IMTK_PB_CTRL_VID_CODEC_INFO_T           uVidCodecInfo;    ///< video codec info
} IMTK_PB_CTRL_RSVT_VID_INFO_T;


/*! @struct IMTK_PB_CTRL_RSVT_AUD_INFO_T
 *  @brief  audio info structure for the fictitious RSVT media type
 */
typedef struct
{
    IMTK_PB_CTRL_AUD_ENC_T                  eAudEnc;          ///< audio codec
    IMTK_PB_CTRL_AUD_CODEC_INFO_T           uAudCodecInfo;    ///< audio codec info
} IMTK_PB_CTRL_RSVT_AUD_INFO_T;


#define IMTK_PB_CTRL_MEDIA_HDR_RESERVED_INFO_LEN    128     ///< Reserved bytes length for Media Info

/*! @struct IMTK_PB_CTRL_MEDIA_RSVT_INFO_T
 *  @brief  media type specific info for the fictitious RSVT media type
 */
typedef struct
{
    uint8_t     u1hdr_res_info[IMTK_PB_CTRL_MEDIA_HDR_RESERVED_INFO_LEN]; ///< Reserved Bytes Array for Media Header Info
    IMTK_PB_CTRL_RSVT_VID_INFO_T    tVidInfo;   ///< video info
    IMTK_PB_CTRL_RSVT_AUD_INFO_T    tAudInfo;   ///< audio info
} IMTK_PB_CTRL_MEDIA_RSVT_INFO_T;


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
        IMTK_PB_CTRL_MEDIA_AVI_INFO_T      tAviInfo;        ///< AVI info
        IMTK_PB_CTRL_MEDIA_MPEG2_PS_INFO_T tPsInfo;         ///< PS info
        IMTK_PB_CTRL_MEDIA_MPEG2_TS_INFO_T tTsInfo;         ///< TS info
        IMTK_PB_CTRL_MEDIA_ASF_INFO_T      tAsfInfo;        ///< ASF info
        IMTK_PB_CTRL_MEDIA_MKV_INFO_T      tMkvInfo;        ///< MKV info
        IMTK_PB_CTRL_MEDIA_RM_INFO_T       tRmInfo;         ///< RM  info
        IMTK_PB_CTRL_MEDIA_OGG_INFO_T      tOggInfo;        ///< Ogg info
        IMTK_PB_CTRL_MEDIA_FLAC_INFO_T     tFlacInfo;       ///< Flac file info
        IMTK_PB_CTRL_MEDIA_APE_INFO_T      tApeInfo;        ///< APE file info
        IMTK_PB_CTRL_MEDIA_VIDEO_ES_INFO_T tVideoEsInfo;    ///< video elementary stream info
        IMTK_PB_CTRL_MEDIA_AUDIO_ES_INFO_T tAudioEsInfo;    ///< audio elementary stream info
        IMTK_PB_CTRL_MEDIA_MTK_P0_INFO_T   tMtkP0Info;      ///< MTK Private P0 info
        IMTK_PB_CTRL_MEDIA_FLV_INFO_T      tFlvInfo;        ///< FLV info
        IMTK_PB_CTRL_MEDIA_RSVT_INFO_T     tRsvtInfo;       ///< info for a fictitious reserved media type RSVT
    } uFormatInfo;
} IMTK_PB_CTRL_SET_MEDIA_INFO_T;


/*! @enum IMTK_PB_CTRL_3D_VIDEO_TYPE_T
 *  @brief  Stereoscopic 3D video type
 */
typedef enum
{
    IMTK_PB_CTRL_3D_VIDEO_TAB_LE_T,     ///< top and bottom; left eye content on top
    IMTK_PB_CTRL_3D_VIDEO_TAB_RE_T,     ///< top and bottom; right eye content on top
    IMTK_PB_CTRL_3D_VIDEO_SBS_LE_L,     ///< side by side; left eye content on left
    IMTK_PB_CTRL_3D_VIDEO_SBS_RE_L,     ///< side by side; right eye content on left
    IMTK_PB_CTRL_3D_VIDEO_AF_LE_F,      ///< alternate frame; left eye content first
    IMTK_PB_CTRL_3D_VIDEO_AF_RE_F       ///< alternate frame; right eye content first
} IMTK_PB_CTRL_3D_VIDEO_TYPE_T;


/*! @enum IMTK_PB_CTRL_EXTRA_INFO_TYPE_T
 *  @brief  extra info types
 */
typedef enum
{
    IMTK_PB_CTRL_EXTRA_INFO_TYPE_CBR_AUDIO = 1,             ///< This type indicates that the audio to be played is of constant bit rate, used in Lib Master model.  The uExtraInfoParam.u4LimitedSeekThrd parameter is not used.
    IMTK_PB_CTRL_EXTRA_INFO_TYPE_LIMITED_SEEK,              ///< This type indicates that the IMtkPb_Ctrl_Pull_ByteSeek_Fct() function has a limitation.  There is a file offset threshold defined in uExtraInfoParam.u4LimitedSeekThrd.  When MtkPbLib's read range is under this threshold, it's free to seek to anywhere within this threshold.  Once its reading offset goes beyond this threshold, it can no longer do any seek, be it forward or backward seek.  This flag is used in Lib Master + Pull model only.  It's possible that due to this seek limitation, MtkPbLib is unable to do certain operations.  In this case, MtkPbLib may return ::IMTK_PB_ERROR_CODE_NOT_OK in IMtkPb_Ctrl_TimeSeek() and IMtkPb_Ctrl_SetSpeed(), and the u4TotalDuration returned by IMtkPb_Ctrl_GetMediaInfo() may be invalid.
    IMTK_PB_CTRL_EXTRA_INFO_TYPE_DISP_ORDER_IS_DEC_ORDER,   ///< This type allows the App to promise that the video to be played satisfies the constraint that the display order is identical to the decode order.  Only used in App Master model.  The uExtraInfoParam.u4LimitedSeekThrd parameter is not used.
    IMTK_PB_CTRL_EXTRA_INFO_TYPE_3D_VIDEO,                  ///< This type indicates that the video to be played contains stereoscopic 3D contents.  The 3D video type is defined in uExtraInfoParam.e3dVideoType.  This is used in both App Master and Lib Master model.
} IMTK_PB_CTRL_EXTRA_INFO_TYPE_T;


#define IMTK_PB_CTRL_EXTRA_INFO_PARAM_RSVD_LEN    64     ///< length for extra info parameter reserved bytes


/*! @struct IMTK_PB_CTRL_SET_EXTRA_INFO_T
 *  @brief  Extra info structure
 */
typedef struct
{
    IMTK_PB_CTRL_EXTRA_INFO_TYPE_T eExtraInfoType;  ///< extra info types

    union {
        uint32_t    u4LimitedSeekThrd;              ///< the file offset threshold for ::IMTK_PB_CTRL_EXTRA_FLAG_LIMITED_SEEK
        IMTK_PB_CTRL_3D_VIDEO_TYPE_T e3dVideoType;  ///< 3D video type for ::IMTK_PB_CTRL_EXTRA_FLAG_3D_VIDEO
        uint8_t     u1Reserved[IMTK_PB_CTRL_EXTRA_INFO_PARAM_RSVD_LEN]; ///< Reserved Bytes Array for Media Header Info
    } uExtraInfoParam;
} IMTK_PB_CTRL_SET_EXTRA_INFO_T;


/*! @union IMTK_PB_CTRL_AUD_INFO_T
 *  @brief  Audio info structure used by IMtkPb_Ctrl_ChangeAudio()
 */
typedef union
{
    IMTK_PB_CTRL_AVI_AUD_INFO_T         tAviAudInfo;    ///< AVI audio info
    IMTK_PB_CTRL_MPEG2_PS_AUD_INFO_T    tMpgPsAudInfo;  ///< Mpeg PS audio info
    IMTK_PB_CTRL_MPEG2_TS_AUD_INFO_T    tMpgTsAudInfo;  ///< Mpeg TS audio info
    IMTK_PB_CTRL_ASF_AUD_INFO_T         tAsfAudInfo;    ///< ASF audio info
    IMTK_PB_CTRL_MKV_AUD_INFO_T         tMkvAudInfo;    ///< MKV audio info
    IMTK_PB_CTRL_OGG_AUD_INFO_T         tOggAudInfo;    ///< OGG audio info
    IMTK_PB_CTRL_FLAC_AUD_INFO_T        tFlacAudInfo;   ///< FLAC audio info
    IMTK_PB_CTRL_APE_AUD_INFO_T         tApeAudInfo;    ///< APE audio info
    IMTK_PB_CTRL_BASIC_AUDIO_INFO_T     tBasicAudInfo;  ///< basic audio info (for ::IMTK_PB_CTRL_MEDIA_TYPE_AUDIO_ES and ::IMTK_PB_CTRL_MEDIA_TYPE_MTK_P0)
} IMTK_PB_CTRL_AUD_INFO_T;


/*! @union IMTK_PB_CTRL_VID_INFO_T
 *  @brief  Video info structure used by IMtkPb_Ctrl_ChangeVideo()
 */
typedef union
{
    IMTK_PB_CTRL_MPEG2_TS_VID_INFO_T    tMpgTsVidInfo;  ///< Mpeg TS video info
} IMTK_PB_CTRL_VID_INFO_T;


/*! @struct IMTK_PB_CTRL_PLAY_INFO_T
 *  @brief  play info
 */
typedef struct
{
    bool fgPaused;   ///< true: playback should be PAUSED at the first picture of the play target position.  false: playback should be PLAYING at normal speed starting from the play target position.  This flag is supported only when the file being played contains video.
} IMTK_PB_CTRL_PLAY_INFO_T;


/*! @struct IMTK_PB_CTRL_TIME_SEEK_INFO_T
 *  @brief  time seek info
 */
typedef struct
{
    bool fgPaused;   ///< true: playback should be PAUSED at the first picture of the seek target position.  false: playback should be PLAYING at normal speed starting from the seek target position.  This flag is supported only when the file being played contains video.
} IMTK_PB_CTRL_TIME_SEEK_INFO_T;


/*! @enum   IMTK_PB_CTRL_PLAY_MODE_T
 *  @brief  playback mode
 */
typedef enum
{
    IMTK_PB_CTRL_PLAY_MODE_NORMAL = 0,      ///< normal mode; every picture is played, and audio is played normally
    IMTK_PB_CTRL_PLAY_MODE_I_MODE = 1       ///< only I-picture is played; audio is not played; picture PTS is ignored.
} IMTK_PB_CTRL_PLAY_MODE_T;


/*! @struct IMTK_PB_CTRL_PLAY_MODE_INFO_T
 *  @brief  playback mode info for IMtkPb_Ctrl_SetPlayMode()
 */
typedef struct
{
    IMTK_PB_CTRL_PLAY_MODE_T ePlayMode;     ///< the playback mode
} IMTK_PB_CTRL_PLAY_MODE_INFO_T;


/*! @struct IMTK_PB_CTRL_GET_AUD_TRACK_INFO_T 
 *  @brief  Audio Track Info structure
 */
typedef struct
{
    IMTK_PB_CTRL_AUD_ENC_T  eAudEnc;            ///< Audio Codec
    IMTK_PB_CTRL_AUD_CH_NUM_T       eChNum;             ///< Channels
    IMTK_PB_CTRL_AUD_SAMPLE_RATE_T  eSampleRate;        ///< Sampling Frequence
    uint32_t                u4BitRate;          ///< Bitrate
    uint8_t                 s_lang [IMTK_CTRL_ISO_639_LANG_LEN]; ///< Language
} IMTK_PB_CTRL_GET_AUD_TRACK_INFO_T;


/*! @struct IMTK_PB_CTRL_GET_VID_TRACK_INFO_T 
 *  @brief  Video Track Info structure, for IMtkPb_Ctrl_GetVidTrackInfo().
 */
typedef struct
{
    IMTK_PB_CTRL_VID_ENC_T  eVidEnc;            ///< Video codec
    uint32_t                u4Width;            ///< picture width
    uint32_t                u4Height;           ///< picture height
    uint32_t                u4InstBitRate;      ///< video instant bitrate, in bits per second.  Only valid when the state is in PLAYING or PAUSED state, otherwise the value is undefined.
} IMTK_PB_CTRL_GET_VID_TRACK_INFO_T;


/*! @enum IMTK_PB_CTRL_STREAM_TYPE
 *  @brief  Stream type
 */
typedef enum
{
    IMTK_PB_CTRL_STREAM_TYPE_AUDIO,     ///< audio stream
    IMTK_PB_CTRL_STREAM_TYPE_VIDEO      ///< video stream
} IMTK_PB_CTRL_STREAM_TYPE;


/*! @struct IMTK_PB_CTRL_IBC_PARAM_SET_ASF_PACKET_INFO
 *  @brief  IBC parameter for the ::IMTK_PB_CTRL_IBC_TYPE_SET_ASF_PACKET_INFO IBC type
 */
typedef struct
{
    IMTK_PB_CTRL_STREAM_TYPE        eStreamtype;    ///< the stream type whose packet info is to be modified
    uint8_t                         u1StreamIdx;    ///< the stream Idx of the stream
    uint64_t                        u8Preroll;      ///< preroll
    uint32_t                        u4PacketSize;   ///< size of the packet associated with this stream type
} IMTK_PB_CTRL_IBC_PARAM_SET_ASF_PACKET_INFO;

/*! @struct IMTK_PB_CTRL_IBC_PARAM_SET_STILL_PICTURE_INFO
 *  @brief  IBC parameter for the ::IMTK_PB_CTRL_IBC_TYPE_SET_STILL_PICTURE IBC type
 */
typedef struct
{
    bool                         fgStillPic;    ///< Enable or disable still picture mode
} IMTK_PB_CTRL_IBC_PARAM_SET_STILL_PICTURE_INFO;

/*! @struct IMTK_PB_CTRL_IBC_PARAM_SET_DEC_RES_INFO
 *  @brief  IBC parameter for the ::IMTK_PB_CTRL_IBC_TYPE_SET_DEC_RES IBC type
 */
typedef struct
{
    uint32_t                u4W;            ///< Width in pixel
    uint32_t                u4H;            ///< Height in pixel
} IMTK_PB_CTRL_IBC_PARAM_SET_DEC_RES_INFO;

/*! @enum IMTK_PB_CTRL_IBC_TYPE
 *  @brief  in-band command types
 */
typedef enum
{
    IMTK_PB_CTRL_IBC_TYPE_SET_ASF_PACKET_INFO   = 1,  ///< Change the packet info of the following packets.  Only used when the media type is ::IMTK_PB_CTRL_MEDIA_TYPE_ASF.
    IMTK_PB_CTRL_IBC_TYPE_SET_STILL_PICTURE     = 2,  ///< Enable or disable the still picture video decoding mode.  Valid only if video codec is ::IMTK_PB_CTRL_VID_ENC_MJPEG.
    IMTK_PB_CTRL_IBC_TYPE_SET_DEC_RES           = 3,  ///< Specify the decoder's output resolution.  Valid only if video codec is ::IMTK_PB_CTRL_VID_ENC_MJPEG.
    IMTK_PB_CTRL_IBC_TYPE_SET_ENCRYPT_INFO      = 4   ///< Specify the encrypt info.  
} IMTK_PB_CTRL_IBC_TYPE;


/*! @union IMTK_PB_CTRL_IBC_PARAM
 *  @brief  in-band command parameters
 */
typedef union
{
    IMTK_PB_CTRL_IBC_PARAM_SET_ASF_PACKET_INFO      tSetAsfPacketInfoParam;
    IMTK_PB_CTRL_IBC_PARAM_SET_STILL_PICTURE_INFO   tSetStillPictureInfoParam;
    IMTK_PB_CTRL_IBC_PARAM_SET_DEC_RES_INFO         tSetDecResInfoParam;
    //IMTK_PB_CTRL_IBC_PARAM_SET_ENCRYPT_INFO         tSetEncryptInfoParam;
} IMTK_PB_CTRL_IBC_PARAM;


/*! @struct IMTK_PB_CTRL_IBC
 *  @brief  In Band Command Structure
 */
typedef struct
{
    IMTK_PB_CTRL_IBC_TYPE   eCmdType;
    uint32_t                u4Id;           ///< reserved for future use
    IMTK_PB_CTRL_IBC_PARAM  tParam;
} IMTK_PB_CTRL_IBC;


/*! @enum IMTK_PB_CTRL_P0_PPR_TYPE
 *  @brief  MTK P0 PPR (picture/packet relationship) type
 */
typedef enum
{
    IMTK_PB_CTRL_P0_PPR_TYPE_FREE    = 0,   ///< pictures can be freely put into packets
    IMTK_PB_CTRL_P0_PPR_TYPE_OPPP    = 1,   ///< one picture per packet
    IMTK_PB_CTRL_P0_PPR_TYPE_SEGMENT = 2    ///< one picture is segmented into several packets
} IMTK_PB_CTRL_P0_PPR_TYPE;

/*! @enum IMTK_PB_CTRL_P0_PS_TYPE
 *  @brief  MTK P0 PS type
 */
typedef enum
{
    IMTK_PB_CTRL_P0_PS_TYPE_FIRST         = 0,  ///< first segment
    IMTK_PB_CTRL_P0_PS_TYPE_INTERMEDIATE  = 1,  ///< intermediate segment
    IMTK_PB_CTRL_P0_PS_TYPE_LAST          = 2   ///< last segment
} IMTK_PB_CTRL_P0_PS_TYPE;

/*! @struct IMTK_PB_CTRL_P0_PKT_INFO_T
 *  @brief  MTK P0 packet info
 */
typedef struct
{
    uint64_t u8Pts;                         ///< Presentation time stamp of this packet; or ::IMTK_INVALID_PTS
    uint32_t u4PayloadSz;                   ///< size of this payload, in byte
    IMTK_PB_CTRL_STREAM_TYPE eStreamType;   ///< stream type of this packet
    IMTK_PB_CTRL_P0_PPR_TYPE ePPR;          ///< only valid when eStreamType == ::IMTK_PB_CTRL_STREAM_TYPE_VIDEO.
    IMTK_PB_CTRL_P0_PS_TYPE ePS;            ///< only valid when ePPR == ::IMTK_PB_CTRL_P0_PPR_TYPE_SEGMENT.
} IMTK_PB_CTRL_P0_PKT_INFO_T;

/*! @struct IMTK_PB_CTRL_P0_PPR_INFO_T
 *  @brief  MTK P0 available PPR (picture/packet relationship) value for each bitstream type
 */
typedef struct
{
    bool fgFree;                  ///< true if this bitstream type can be pushed in PPR type "free"
    bool fgOPPP;                  ///< true if this bitstream type can be pushed in PPR type "OPPP"
    bool fgSegment;               ///< true if this bitstream type can be pushed in PPR type "segment"
    uint32_t u4SegPayloadSz;      ///< recommended payload size for segment.  Only valid when fgSegment is true.
} IMTK_PB_CTRL_P0_PPR_INFO_T;

#define IMTK_PB_RM_SLICE_MAX_NUM    128     ///< maximal number of slice elements in an RealVideo picture


/*! @struct IMTK_PB_CTRL_P0_RV_PIC_INFO_T
 *  @brief  MTK P0 RealVideo picture info for each RV8 and RV9_10 picture
 */
typedef struct
{
    uint8_t u1TotalSliceNum;                            ///< total slice number; maximal value is ::IMTK_PB_RM_SLICE_MAX_NUM
    uint16_t u2SliceElmSz[IMTK_PB_RM_SLICE_MAX_NUM];    ///< slice element sizes
} IMTK_PB_CTRL_P0_RV_PIC_INFO_T;


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

/*! @struct IMTK_PB_CTRL_POINT_T
 *  @brief  POINT structure
 */
typedef struct
{
    uint32_t                u4X;            ///< X Position, possible value [0:9999]
    uint32_t                u4Y;            ///< Y position, possible value [0:9999]
} IMTK_PB_CTRL_POINT_T;


/*! @enum   IMTK_PB_CTRL_PULL_EVENT_T 
 *  @brief  EVENT for PULL I/F
 */
typedef enum
{
    IMTK_PB_CTRL_PULL_ABORT_FAIL            =   -2,     ///< Pull Model Read Abort Fail, u4Param should be ignored 
    IMTK_PB_CTRL_PULL_FAIL                  =   -1,     ///< Pull Model Read Fail, u4Param should be ignored 
    IMTK_PB_CTRL_PULL_READ_OK               =   0,      ///< Pull Model Read OK, u4Param should be the length of data in bytes
    IMTK_PB_CTRL_PULL_ABORT_OK              =   1,      ///< Pull Model Read Abort OK, u4Param should be ignored    
    IMTK_PB_CTRL_PULL_READ_EOS              =   2       ///< Pull Model Read EOS, u4Param should be the length of data in bytes
} IMTK_PB_CTRL_PULL_EVENT_T;

#define IMTK_CTRL_PULL_SEEK_BGN                  ((uint8_t) (1))     ///< Seek from beginning of file
#define IMTK_CTRL_PULL_SEEK_CUR                  ((uint8_t) (2))     ///< Seek from current position of file
#define IMTK_CTRL_PULL_SEEK_END                  ((uint8_t) (3))     ///< Seek from end of file

/*! @brief          Notify function for Pull Model Async Read
 *  @param [in]     eEventType      The callback event type.
 *  @param [in]     pvRdAsyTag      the pvRdAsyTag set in IMtkPb_Ctrl_Pull_Read_Async_Fct()
 *  @param [in]     u4ReqId         The request id.
 *  @param [in]     u4Param         The parameter of callback.
 *  @return     Return the error code
 *  @retval     IMTK_PB_CB_ERROR_CODE_OK                    Success.
 *  @retval     IMTK_PB_CB_ERROR_CODE_NOT_OK                Failed.
 *  @note
 *  @see
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Pull_Nfy_Fct)(IMTK_PB_CTRL_PULL_EVENT_T   eEventType,
                                                            void*                       pvRdAsyTag,
                                                            uint32_t                    u4ReqId,
                                                            uint32_t                    u4Param);


/*! @brief          Open the pull source function
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [out]    phPullSrc       The handle of pull source.
 *  @param [in]     pvAppTag        the pvAppTag passed in ::IMTK_PB_CTRL_PULL_INFO_T by IMtkPb_Ctrl_SetEngineParam()
 *  @return     Return the error code
 *  @retval     IMTK_PB_CB_ERROR_CODE_OK                    Success.
 *  @retval     IMTK_PB_CB_ERROR_CODE_NOT_OK                Failed.
 *  @note       Application should implement this function and register into playback control engine
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Pull_Open_Fct)(IMTK_PB_HANDLE_T           hHandle,
                                                             IMTK_PULL_HANDLE_T*        phPullSrc,
                                                             void*                      pvAppTag);


/*! @brief          Close the pull source function
 *  @param [in]     hPullSrc         The handle of pull source.
 *  @param [in]     pvAppTag        the pvAppTag passed in ::IMTK_PB_CTRL_PULL_INFO_T by IMtkPb_Ctrl_SetEngineParam()
 *  @return     Return the error code
 *  @retval     IMTK_PB_CB_ERROR_CODE_OK                    Success.
 *  @retval     IMTK_PB_CB_ERROR_CODE_NOT_OK                Failed.
 *  @note       Application should implement this function and register into playback control engine
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Pull_Close_Fct)(IMTK_PULL_HANDLE_T     hPullSrc,
                                                              void*                  pvAppTag);


/*! @brief          Read data function
 *  @param [in]     hPullSrc        The handle of pull source.
 *  @param [in]     pvAppTag        the pvAppTag passed in ::IMTK_PB_CTRL_PULL_INFO_T by IMtkPb_Ctrl_SetEngineParam()
 *  @param [in]     pu1DstBuf       References to the pointer of destination buffer address.
 *  @param [in]     u4Count         The destination buffer size.
 *  @param [out]    pu4Read         Reference to the actually return data length.
 *  @return     Return the error code
 *  @retval     IMTK_PB_CB_ERROR_CODE_OK                    Success.
 *  @retval     IMTK_PB_CB_ERROR_CODE_NOT_OK                Failed.
 *  @retval     IMTK_PB_CB_ERROR_CODE_EOF                   EOF encountered
 *  @note       Application should implement this function and register into playback control engine
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Pull_Read_Fct)(IMTK_PULL_HANDLE_T     hPullSrc,
                                                             void*                  pvAppTag,
                                                             uint8_t*               pu1DstBuf,
                                                             uint32_t               u4Count,
                                                             uint32_t*              pu4Read);


/*! @brief          Async read data function
 *  @param [in]     hPullSrc        The handle of pull source.
 *  @param [in]     pvAppTag        the pvAppTag passed in ::IMTK_PB_CTRL_PULL_INFO_T by IMtkPb_Ctrl_SetEngineParam()
 *  @param [in]     pu1Dst          References to the pointer of destination address.
 *  @param [in]     u4DataLen       The data length.
 *  @param [in]     pfnNotify       References to the callback function for async retun.
 *  @param [in]     pvRdAsyTag      tag to be passed back by pfnNotify
 *  @param [out]    pu4ReqId        References to the request id of async read.
 *  @return     Return the error code
 *  @retval     IMTK_PB_CB_ERROR_CODE_OK                    Success.
 *  @retval     IMTK_PB_CB_ERROR_CODE_NOT_OK                Failed.
 *  @note       Application should implement this function and register into playback control engine
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Pull_Read_Async_Fct)(IMTK_PULL_HANDLE_T           hPullSrc,
                                                                   void*                        pvAppTag,
                                                                   uint8_t*                     pu1Dst,
                                                                   uint32_t                     u4DataLen,
                                                                   IMtkPb_Ctrl_Pull_Nfy_Fct     pfnNotify,
                                                                   void*                        pvRdAsyTag,
                                                                   uint32_t*                    pu4ReqId);


/*! @brief          Abort Async read function
 *  @param [in]     hPullSrc        The handle of pull source.
 *  @param [in]     pvAppTag        the pvAppTag passed in ::IMTK_PB_CTRL_PULL_INFO_T by IMtkPb_Ctrl_SetEngineParam()
 *  @param [in]     u4ReqId         The request id of async read return from async read
 *  @return     Return the error code
 *  @retval     IMTK_PB_CB_ERROR_CODE_OK                    Success.
 *  @retval     IMTK_PB_CB_ERROR_CODE_NOT_OK                Failed.
 *  @note       Application should implement this function and register into playback control engine
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Pull_Abort_Read_Async_Fct)(IMTK_PULL_HANDLE_T     hPullSrc,
                                                                         void*                  pvAppTag,
                                                                         uint32_t               u4ReqId);


/*! @brief          Byte Seek function
 *  @param [in]     hPullSrc        The handle of pull source.
 *  @param [in]     pvAppTag        the pvAppTag passed in ::IMTK_PB_CTRL_PULL_INFO_T by IMtkPb_Ctrl_SetEngineParam()
 *  @param [in]     i8SeekPos       The file offset to seek.
 *  @param [in]     u1Whence        The start point of seek. Possible value is IMTK_CTRL_PULL_SEEK_BGN, IMTK_CTRL_PULL_SEEK_CUR, IMTK_CTRL_PULL_SEEK_END.
 *  @param [out]    pu8CurPos       The file offset of current position after seek.
 *  @return     Return the error code
 *  @retval     IMTK_PB_CB_ERROR_CODE_OK                    Success.
 *  @retval     IMTK_PB_CB_ERROR_CODE_NOT_OK                Failed.
 *  @note       Application should implement this function and register into playback control engine
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Pull_ByteSeek_Fct)(IMTK_PULL_HANDLE_T         hPullSrc,
                                                                 void*                      pvAppTag,
                                                                 int64_t                    i8SeekPos,
                                                                 uint8_t                    u1Whence,
                                                                 uint64_t*                  pu8CurPos);



#define IMTK_PB_CTRL_INFINITE_LEN 0xffffffffffffffffULL     ///< for IMtkPb_Ctrl_Pull_GetInputLen_Fct() to return an unknown length

/*! @brief          Get the total data length of pull source function
 *  @param [in]     hPullSrc        The handle of pull source.
 *  @param [in]     pvAppTag        the pvAppTag passed in ::IMTK_PB_CTRL_PULL_INFO_T by IMtkPb_Ctrl_SetEngineParam()
 *  @param [out]    pu8Len          length of the pull source.  The value ::IMTK_PB_CTRL_INFINITE_LEN means the App cannot determine the length.  It may happen when the source is live streaming.
 *  @return     Return the error code
 *  @retval     IMTK_PB_CB_ERROR_CODE_OK                    Success.
 *  @retval     IMTK_PB_CB_ERROR_CODE_NOT_OK                Failed.
 *  @note       Get the total length of the pull source.
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Pull_GetInputLen_Fct)(IMTK_PULL_HANDLE_T  hPullSrc,
                                                                    void*               pvAppTag,
                                                                    uint64_t*           pu8Len);



/*! @struct IMTK_PB_CTRL_PULL_INFO_T
 *  @brief  function table for input source
 *
 */
typedef struct
{
    void*                                   pvAppTag;               ///<  the app tag to be passed back in all pull callback functions
    IMtkPb_Ctrl_Pull_Open_Fct               pfnOpen;                ///<  Close function
    IMtkPb_Ctrl_Pull_Close_Fct              pfnClose;               ///<  Close function
    IMtkPb_Ctrl_Pull_Read_Fct               pfnRead;                ///<  Read function
    IMtkPb_Ctrl_Pull_Read_Async_Fct         pfnReadAsync;           ///<  Async Read function
    IMtkPb_Ctrl_Pull_Abort_Read_Async_Fct   pfnAbortReadAsync;      ///<  Abort Async Read function
    IMtkPb_Ctrl_Pull_ByteSeek_Fct           pfnByteSeek;            ///<  Byte Seek function
    IMtkPb_Ctrl_Pull_GetInputLen_Fct        pfnGetInputLen;         ///<  Get Input Length function
} IMTK_PB_CTRL_PULL_INFO_T;


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


/* Playback flag */
#define IMTK_PB_CTRL_PLAY_FLAG_VIDEO            ((uint32_t) (1))         ///< indicating that the content contains video and video is to be played
#define IMTK_PB_CTRL_PLAY_FLAG_AUDIO            ((uint32_t) (1 << 1))   ///< indicating that the content contains audio and audio is to be played
#define IMTK_PB_CTRL_PLAY_FLAG_UNKNOWN          ((uint32_t) (1 << 31))   ///< indicating that App doesn't know the play flag.  If this bit is 1, all other bits are ignored.


/*! @struct IMTK_PB_CTRL_ENGINE_PARAM_T
 *  @brief  engine parameters
 */
typedef struct
{
    uint32_t                        u4PlayFlag; ///< Telling the MtkPbLib the nature of the content: whether it naturally contains audio or video.  Possible values are bit masks of IMTK_PB_CTRL_PLAY_FLAG_XXXXX.  See @ref PlayFlagNotes for more information.

    union
    {
        IMTK_PB_CTRL_URI_INFO_T     tUriInfo;   ///< URI model parameters
        IMTK_PB_CTRL_PULL_INFO_T    tPullInfo;  ///< Pull model parameters
    } uBufferModelParam;

} IMTK_PB_CTRL_ENGINE_PARAM_T;


/*! @enum   IMTK_PB_CTRL_PLAY_RESULT_T
 *  @brief  IMtkPb_Ctrl_Play() or IMtkPb_Ctrl_PlayEx() result, as u4Data of ::IMTK_PB_CTRL_EVENT_PLAY_DONE
 */
typedef enum
{
    IMTK_PB_CTRL_PLAY_OK   = 1,                 ///< play success
    IMTK_PB_CTRL_PLAY_FAIL = -1                 ///< play fail
} IMTK_PB_CTRL_PLAY_RESULT_T;

/*! @enum   IMTK_PB_CTRL_STEP_RESULT_T
 *  @brief  IMtkPb_Ctrl_Step() result, as u4Data of ::IMTK_PB_CTRL_EVENT_STEP_DONE
 */
typedef enum
{
    IMTK_PB_CTRL_STEP_OK   = 1,                 ///< step success
    IMTK_PB_CTRL_STEP_FAIL = -1                 ///< step fail
} IMTK_PB_CTRL_STEP_RESULT_T;

/*! @enum   IMTK_PB_CTRL_TIMESEEK_RESULT_T 
 *  @brief  IMtkPb_Ctrl_TimeSeek() or IMtkPb_Ctrl_TimeSeekEx() result, as u4Data of ::IMTK_PB_CTRL_EVENT_TIMESEEK_DONE
 */
typedef enum 
{ 
    IMTK_PB_CTRL_TIMESEEK_OK   = 1,             ///< time seek success
    IMTK_PB_CTRL_TIMESEEK_FAIL = -1             ///< time seek fail
} IMTK_PB_CTRL_TIMESEEK_RESULT_T;

/*! @enum   IMTK_PB_CTRL_ERROR_TYPE_T
 *  @brief  u4Data of ::IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR
 */
typedef enum
{
    IMTK_PB_CTRL_ERROR_UNKNOWN          = 0,    ///< unknown error
    IMTK_PB_CTRL_ERROR_AUDIO_UNPLAYABLE = 1,    ///< audio is unplayable
    IMTK_PB_CTRL_ERROR_VIDEO_UNPLAYABLE = 2,    ///< video is unplayable
    IMTK_PB_CTRL_ERROR_FILE_NOT_SUPPORT = 4,    ///< file not support(file recognize failed)
    IMTK_PB_CTRL_ERROR_FILE_CORRUPT     = 8,    ///< file is corrupt(file parse failed)
    IMTK_PB_CTRL_ERROR_OPEN_FILE_FAIL   = 16,   ///< open network file failed
    IMTK_PB_CTRL_ERROR_GET_DATA_FAIL    = 32	///< get data from network failed
} IMTK_PB_CTRL_ERROR_TYPE_T;


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
    IMTK_PB_CTRL_EVENT_GET_BUF_READY,           ///< Buffer is ready for previous pending request of IMtkPb_Ctrl_GetBuffer().  The u4Data parameter is undefined.
    IMTK_PB_CTRL_EVENT_FILE_REPLAY		///< media FR to begin, replay this file.
} IMTK_PB_CTRL_EVENT_T;


/*! @brief          Call back function pointer of notification of playback control engine
 *  @param [in]     eEventType      The callback event type.
 *  @param [in]     pvAppCbTag      the pvAppCbTag set in IMtkPb_Ctrl_RegCallback()
 *  @param [in]     u4Data          The data to notify.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           Application should register a function into playback control engine for notify
 *  @see
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Nfy_Fct)(IMTK_PB_CTRL_EVENT_T       eEventType,
                                                       void*                      pvAppCbTag,
                                                       uint32_t                   u4Data);

/*-----------------------------------------------------------------------------
                    functions declarations
-----------------------------------------------------------------------------*/

/*!
 * @name Init and terminate
 * @{
 */


/*! @brief          Initializes IMtkPb_Ctrl
 *  @return         indicate success
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @note           Before a process starts using IMtkPb_Ctrl, it should first call this function.
 *  @see            IMtkPb_Ctrl_Terminate()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Init(void);


/*! @brief          Terminates IMtkPb_Ctrl
 *  @return         indicate success
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @note           When a process finishes using IMtkPb_Ctrl, it should call this function.
 *  @see            IMtkPb_Ctrl_Init()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Terminate(void);


/*!
 * @}
 * @name General
 * @{
 */

/*! @brief          Opens a playback control.
 *  @param [out]    phHandle                Reference to the handle of playback control instance.
 *  @param [in]     eBufferingModel       buffering model
 *  @param [in]     eOperatingModel      operating model
 *  @param [in]     pu1Profile              Profile string for opening a playback control
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see            IMtkPb_Ctrl_Close() 
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Open(IMTK_PB_HANDLE_T*              phHandle,
                                               IMTK_PB_CTRL_BUFFERING_MODEL_T eBufferingModel,
                                               IMTK_PB_CTRL_OPERATING_MODEL_T eOperatingModel,
                                               uint8_t*                       pu1Profile);


/*! @brief          Closes a playback control.
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           This function is a synchronous function call.
 *  @see            IMtkPb_Ctrl_Open()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Close(IMTK_PB_HANDLE_T  hHandle);


/*! @brief          Registers a callback function for notification from playback.
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     pvAppCbTag     the tag to be passed back by pfnCallback
 *  @param [in]     pfnCallback    References to the function pointer of callback.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see 
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_RegCallback(IMTK_PB_HANDLE_T        hHandle,
                                                    void*                   pvAppCbTag,
                                                    IMtkPb_Ctrl_Nfy_Fct     pfnCallback);


/*! @brief          Sets engine parameters; see parameter structures for details
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     ptParam         reference to the engine parameters
 *  @return         Return the error code
 *  @pre            OPENED or READY state
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           Calling this function is mandatory for App.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetEngineParam(IMTK_PB_HANDLE_T             hHandle,
                                                       IMTK_PB_CTRL_ENGINE_PARAM_T*   ptParam);

/*!
 * @}
 * @name Set playback parameters
 * @{
 */

/*! @brief          Sets media information
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     ptMediaInfo     References to the media info.  If it points to NULL, it clears any media info stored in MtkPbLib.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           only used in App Master model.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetMediaInfo(IMTK_PB_HANDLE_T                   hHandle,          
                                                     IMTK_PB_CTRL_SET_MEDIA_INFO_T*     ptMediaInfo);


/*! @brief          Sets extra info
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     ptExtraInfo     reference to the extra info structure
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           This function is used in App Master and Lib Master model.  This function allows the App to set extra info to MtkPbLib.  Calling it is optional for playback.
 *  @note           If the App wants to set more than one extra info type, it shall call this function multiple times.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetExtraInfo(IMTK_PB_HANDLE_T                   hHandle,
                                                     IMTK_PB_CTRL_SET_EXTRA_INFO_T*     ptExtraInfo);


/*! @brief          Append or modify an http header to the http request message to be sent to the http server indicated by the URI by IMtkPb_Ctrl_SetEngineParam()
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     pu1String       a null-terminated string containing one http header string
 *  @return         Return the error code
 *  @pre            Lib Master + URI model, in READY state.
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           This function could be called multiple times to append or modify multiple strings.
 *  @note           If the header contained in the string is already used internally, it will replace the internal one.  If the header is not used internally, it will be appended.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_CustomizeHttpReqHdr(IMTK_PB_HANDLE_T hHandle,
                                                            uint8_t*         pu1String);


/*! @brief          Selects an audio track to be played
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     u2ATrack        the audio track number to be played
 *  @return         Return the error code
 *  @pre            Lib Master model, READY or PLAYING states
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetAudTrack(IMTK_PB_HANDLE_T            hHandle,
                                                    uint16_t                    u2ATrack);


/*! @brief          Sets source clipping area and destination display area of playback
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     ptSrcRect       References to the source rectangle for display.
 *  @param [in]     ptDstRect       References to the destination rectangle for display.
 *  @return         Return the error code
 *  @pre            READY, PLAYING, or PAUSED states.
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           See @ref DisplayRectangleNotes for details.
 *  @see            
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetDisplayRectangle(IMTK_PB_HANDLE_T        hHandle,          
                                                            IMTK_PB_CTRL_RECT_T*    ptSrcRect,
                                                            IMTK_PB_CTRL_RECT_T*    ptDstRect);

/*!
 * @}
 * @name Push model buffering
 * @{
 */
                                                  
/*! @brief          Gets a buffer for pushing data into
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     u4BufSize       The Buffer Size.
 *  @param [out]    ppu1PushBuf     References to the Buffer Pointer.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @retval         IMTK_PB_ERROR_CODE_GET_BUF_PENDING          The buffer is not ready. Should wait callback event ::IMTK_PB_CTRL_EVENT_GET_BUF_READY.
 *  @note           used only in Push model. 
 *  @see            IMtkPb_Ctrl_SendData(), IMtkPb_Ctrl_ReleaseBuffer()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetBuffer(IMTK_PB_HANDLE_T      hHandle, 
                                                  uint32_t              u4BufSize,
                                                  uint8_t**             ppu1PushBuf);


/*! @brief          Pushes data into MtkPbLib
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     u4BufSize       The Buffer Size.
 *  @param [in]     pu1PushBuf      References to the Buffer Pointer.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           This function gives the ownership of the buffer back to MtkPbLib.  App doesn't need to call IMtkPb_Ctrl_ReleaseBuffer() after calling IMtkPb_Ctrl_SendData().  Used only in Push model.
 *  @see            IMtkPb_Ctrl_GetBuffer(), IMtkPb_Ctrl_ReleaseBuffer()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SendData(IMTK_PB_HANDLE_T      hHandle, 
                                                 uint32_t              u4BufSize,
                                                 uint8_t*              pu1PushBuf);


/*! @brief          Cancel a pending request of previous IMtkPb_Ctrl_GetBuffer().
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           used only in Push model. After calling this function, MtkPbLib will not notify ::IMTK_PB_CTRL_EVENT_GET_BUF_READY.  See @ref PushNotes for more details.
 *  @see            IMtkPb_Ctrl_GetBuffer(), IMTK_PB_ERROR_CODE_GET_BUF_PENDING
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_CancelGetBufferPending(IMTK_PB_HANDLE_T      hHandle);


/*! @brief          Pushes command into MtkPbLib
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     ptIBC           References to the IBC data structure.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           This function pushes an in band command to MtkPbLib. Used only in Push model.
 *  @see            IMTK_PB_CTRL_IBC
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SendCmd(IMTK_PB_HANDLE_T        hHandle,
                                                IMTK_PB_CTRL_IBC*       ptIBC);


/*! @brief          Releases a buffer got by IMtkPb_Ctrl_GetBuffer()
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     pu1PushBuf      References to the Buffer Pointer.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           This function is used when App calls IMtkPb_Ctrl_GetBuffer() but finds that it has no more data to push.  Used only in Push model.
 *  @see            IMtkPb_Ctrl_GetBuffer()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_ReleaseBuffer(IMTK_PB_HANDLE_T      hHandle, 
                                                      uint8_t*              pu1PushBuf);


/*! @brief          Notifies MtkPbLib playback engine EOS condition of push data
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           After this function is called, App shouldn't send any data unless it calls Stop and Play again.  Used only in Push model.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetPushModelEOS(IMTK_PB_HANDLE_T    hHandle);


/*!
 * @}
 * @name Private format utility
 * @{
 */

/*! @brief          Generate the 18-byte packet header for the @ref MTKP0page.
 *  @param [out]    pu1PktHdrBuf    a buffer with at least 18-byte size.  The buffer will be filled with the 18-byte defined in @ref MTKP0page.
 *  @param [in]     ptPktInfo       information regarding this packet
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GenerateP0Header(uint8_t* pu1PktHdrBuf,
                                                         IMTK_PB_CTRL_P0_PKT_INFO_T* ptPktInfo);

/*! @brief          Get possible PPR (picture/packet relationship) value for each bitstream type.
 *  @param [in]     eVidEnc            video bitstream type to enquire possible PPR values
 *  @param [out]    ptPPRInfo          information about possible PPR values
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetP0PPRInfo(IMTK_PB_CTRL_VID_ENC_T eVidEnc,
                                                     IMTK_PB_CTRL_P0_PPR_INFO_T* ptPPRInfo);

/*! @brief          Generate the picture header for RV8 and RV9_10 pictures in the @ref MTKP0page
 *  @param [out]    pu1RVPicHdr    This buffer will be filled with RealVideo picture buffer
 *  @param [in]     ptRvPicInfo    information regarding this RealVideo picture
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed
 *  @note           The size of pu1RVPicHdr must be at least (1 + 2*u1TotalSliceNum) bytes.  The pu1RVPicHdr contents after calling this function must be inserted in front of the associated picture and altogether they form the payload part of an @ref MTKP0page video packet.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GenerateRVPicHdr(uint8_t* pu1RVPicHdr,
                                                         IMTK_PB_CTRL_P0_RV_PIC_INFO_T* ptRvPicInfo);


/*!
 * @}
 * @name Playback control
 * @{
 */


/*! @brief          Starts playback from READY or PAUSED state.
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     u4Time          The time of the start playback position, in second. This parameter is only useful in Lib Master model and when the playback state is in the READY state.  In all other cases, this parameter is ignored by MtkPbLib.
 *  @return         Return the error code
 *  @pre            Case 1. Lib Master, state is READY
 *  @pre            Case 2. Lib Master, state is PAUSED; or App Master, state is READY or PAUSED.
 *  @post           Case 1. The ::IMTK_PB_ERROR_CODE_OK return of this function doesn't guarantee the success of the play.  MtkPbLib will notify ::IMTK_PB_CTRL_EVENT_PLAY_DONE when this operation finishes.  Until the ::IMTK_PB_CTRL_EVENT_PLAY_DONE event, MtkPbLib ignores all IMtkPb function calls except for IMtkPb_Ctrl_Stop() and IMtkPb_Ctrl_Close().  If ::IMTK_PB_CTRL_EVENT_PLAY_DONE comes with ::IMTK_PB_CTRL_PLAY_FAIL, the state remains in READY state, otherwise it goes to PLAYING state.
 *  @post           Case 2. The ::IMTK_PB_ERROR_CODE_OK return of this function indicats the success of the play.  No ::IMTK_PB_CTRL_EVENT_PLAY_DONE will be notified by MtkPbLib.  The state goes to PLAYING state.
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @retval         IMTK_PB_ERROR_CODE_CODEC_ERROR              The av codec can not init.
 *  @see 
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Play(IMTK_PB_HANDLE_T   hHandle,
                                             uint32_t           u4Time);


/*! @brief          Starts playback from READY state, with some additional parameters
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     u4Time          The time of the start playback position, in second. This parameter is only useful in Lib Master model and when the playback state is in the READY state.  In all other cases, this parameter is ignored by MtkPbLib.
 *  @param [in]     ptPlayInfo      Extra parameters regarding to this Play
 *  @return         Return the error code
 *  @pre            Lib Master, state is READY
 *  @post           The ::IMTK_PB_ERROR_CODE_OK return of this function doesn't guarantee the success of the play.  MtkPbLib will notify ::IMTK_PB_CTRL_EVENT_PLAY_DONE when this operation finishes.  Until the ::IMTK_PB_CTRL_EVENT_PLAY_DONE event, MtkPbLib ignores all IMtkPb function calls except for IMtkPb_Ctrl_Stop() and IMtkPb_Ctrl_Close().  If ::IMTK_PB_CTRL_EVENT_PLAY_DONE comes with ::IMTK_PB_CTRL_PLAY_FAIL, the state remains in READY state, otherwise it goes to PLAYING or PAUSED state, depending on the value of ptPlayInfo->fgPaused.
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @retval         IMTK_PB_ERROR_CODE_CODEC_ERROR              The av codec can not init.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_PlayEx(IMTK_PB_HANDLE_T   hHandle,
                                               uint32_t           u4Time,
                                               IMTK_PB_CTRL_PLAY_INFO_T* ptPlayInfo);


/*! @brief          Pauses playback
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           This function can only be called during the PLAYING state.
 *  @see 
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Pause(IMTK_PB_HANDLE_T  hHandle);


/*! @brief          Stops playback
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           This function is a synchronous function call.  After this function call, the last picture of the playback will remain on the screen.  If the App wants to clear the picture, it can call IMtkPb_Ctrl_ClearVideo().
 *  @see 
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Stop(IMTK_PB_HANDLE_T   hHandle);


/*! @brief          Steps some frames
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     u4StepAmount    The frame number for stepping.
 *  @return         Return the error code
 *  @pre            state is PAUSED.
 *  @post           The ::IMTK_PB_ERROR_CODE_OK return of this function doesn't guarantee the success of the time seek.  MtkPbLib will notify ::IMTK_PB_CTRL_EVENT_STEP_DONE when this operation finishes.  Until the ::IMTK_PB_CTRL_EVENT_SEEK_DONE event, MtkPbLib ignores all IMtkPb function calls except for IMtkPb_Ctrl_Stop() and IMtkPb_Ctrl_Close().
 *  @post           The state remains in PAUSED state.
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           This is a asynchronous function call.  MtkPbLib shall notify ::IMTK_PB_CTRL_EVENT_STEP_DONE when this operation finishes.
 *  @note           This function can only be called during the PAUSED state.
 *  @see 
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Step(IMTK_PB_HANDLE_T   hHandle,
                                             uint32_t           u4StepAmount);


/*! @brief          Seeks playback time
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     u4Time          The time for seeking, in second.
 *  @return         Return the error code
 *  @pre            Lib Master, state is PLAYING or PAUSED
 *  @post           The ::IMTK_PB_ERROR_CODE_OK return of this function doesn't guarantee the success of the time seek.  MtkPbLib will notify ::IMTK_PB_CTRL_EVENT_TIMESEEK_DONE when this operation finishes.  Until the ::IMTK_PB_CTRL_EVENT_TIMESEEK_DONE event, MtkPbLib ignores all IMtkPb function calls except for IMtkPb_Ctrl_Stop() and IMtkPb_Ctrl_Close().  If ::IMTK_PB_CTRL_EVENT_TIMESEEK_DONE comes with ::IMTK_PB_CTRL_TIMESEEK_FAIL, the state remains in the original state, otherwise it goes to PLAYING state.
 *  @post           The playback speed will go back to ::IMTK_PB_CTRL_SPEED_1X.
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @see 
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_TimeSeek(IMTK_PB_HANDLE_T   hHandle, 
                                                 uint32_t           u4Time);


/*! @brief          Seeks playback time, with some additional parameters
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     u4Time          The time for seeking, in second.
 *  @param [in]     ptTimeSeekInfo  Extra parameters regarding to this time seek.
 *  @return         Return the error code
 *  @pre            Lib Master, state is PLAYING or PAUSED
 *  @post           The ::IMTK_PB_ERROR_CODE_OK return of this function doesn't guarantee the success of the time seek.  MtkPbLib will notify ::IMTK_PB_CTRL_EVENT_TIMESEEK_DONE when this operation finishes.  Until the ::IMTK_PB_CTRL_EVENT_TIMESEEK_DONE event, MtkPbLib ignores all IMtkPb function calls except for IMtkPb_Ctrl_Stop() and IMtkPb_Ctrl_Close().  If ::IMTK_PB_CTRL_EVENT_TIMESEEK_DONE comes with ::IMTK_PB_CTRL_TIMESEEK_FAIL, the state remains in the original state, otherwise it goes to PLAYING or PAUSED state, depending on the value of ptTimeSeekInfo->fgPaused.
 *  @post           The playback speed will go back to ::IMTK_PB_CTRL_SPEED_1X.
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_TimeSeekEx(IMTK_PB_HANDLE_T   hHandle,
                                                   uint32_t           u4Time,
                                                   IMTK_PB_CTRL_TIME_SEEK_INFO_T* ptTimeSeekInfo);


/*! @brief          Change audio stream
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     ptAudInfo       information of the new audio track
 *  @return         Return the error code
 *  @pre            App Master; PLAYING state.
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           The App should then feed audio data corresponding to the new audio info.
 *  @note           The video keeps playing and is not affected by this function.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_ChangeAudio(IMTK_PB_HANDLE_T   hHandle,
                                                    IMTK_PB_CTRL_AUD_INFO_T* ptAudInfo);


/*! @brief          Change video stream
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     ptVidInfo       information of the new video track
 *  @return         Return the error code
 *  @pre            App Master; PLAYING state.  Media type is ::IMTK_PB_CTRL_MEDIA_TYPE_MPEG2_TS.
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           The App should then feed video data corresponding to the new video info.
 *  @note           The audio keeps playing and is not affected by this function.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_ChangeVideo(IMTK_PB_HANDLE_T         hHandle,
                                                    IMTK_PB_CTRL_VID_INFO_T* ptVidInfo);


/*! @brief          Set playback speed
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     eSpeed          playback speed
 *  @return         Return the error code
 *  @pre            Lib Master model, PLAYING state
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetSpeed(IMTK_PB_HANDLE_T        hHandle,
                                                 IMTK_PB_CTRL_SPEED_T    eSpeed);


/*! @brief          Set playback mode
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     ptPlayModeInfo  reference to the play mode info structure
 *  @return         Return the error code
 *  @pre            App Master model, PLAYING state
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetPlayMode(IMTK_PB_HANDLE_T                hHandle,
                                                    IMTK_PB_CTRL_PLAY_MODE_INFO_T*  ptPlayModeInfo);


/*! @brief          Clear the video in the screen
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           Since IMtkPb_Ctrl_Stop() doesn't clear the last picture, the App can call this function to clear the last picture.  This function is used only in the READY state.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_ClearVideo(IMTK_PB_HANDLE_T        hHandle);


/*!
 * @}
 * @name Get status/info
 * @{
 */


/*! @brief          Gets media info information
 *  @param [in]     hHandle         the handle of playback control instance.
 *  @param [out]    ptMediaInfo     References to the media info.
 *  @return         Return the error code
 *  @pre            Lib Master model, PLAYING, PAUSED, or READY state.
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           If ::IMTK_PB_CTRL_EXTRA_FLAG_LIMITED_SEEK was set, u4TotalDuration, u8Size, and u4AvgBitrate may be unavailable.
 *  @note           If in Pull model, and IMtkPb_Ctrl_Pull_GetInputLen_Fct() returns ::IMTK_PB_CTRL_INFINITE_LEN, u4TotalDuration, u8Size, and u4AvgBitrate may be unavailable.
 *  @note           If a field is unavailable, MtkPbLib shall fill it with ::IMTK_PB_UNKNOWN_VALUE.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetMediaInfo(IMTK_PB_HANDLE_T                   hHandle,
                                                     IMTK_PB_CTRL_GET_MEDIA_INFO_T*     ptMediaInfo);


/*! @brief          Gets buffer fullness information
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [out]    pu4Percentage   The Percentage of Buffer.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           used only in Lib Master + URI model; only available in PLAYING and PAUSED states
 *  @see 
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetBufferFullness(IMTK_PB_HANDLE_T      hHandle, 
                                                          uint32_t*             pu4Percentage);


/*! @brief          Gets playback buffer status
 *  @param [in]     hHandle         handle of the playback control instance
 *  @param [out]    ptBufferStatus  buffer status structure
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           This function is used only in Lib Master + URI model; only supported in http and https, but not in file resource type; only available in PLAYING and PAUSED states; only valid when the media type is ::IMTK_PB_CTRL_MEDIA_TYPE_MP4 or ::IMTK_PB_CTRL_MEDIA_TYPE_ASF, or when playing a pure audio file.
 *  @note           If App calls IMtkPb_Ctrl_GetCurrentPos() to get the current playback time, and subtract it from u4BufEndTime, it should get u4BufRemainDur.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetBufferStatus(IMTK_PB_HANDLE_T               hHandle,
                                                        IMTK_PB_CTRL_BUFFER_STATUS_T*  ptBufferStatus);


/*! @brief          Gets playback parameteres
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [out]    ptSetting       References to the settings of playback control.
 *  @return         Return the error code
 *  @pre            Lib Master model, PLAYING or PAUSED state
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetPlayParam(IMTK_PB_HANDLE_T            hHandle,
                                                     IMTK_PB_CTRL_SETTING_T*     ptSetting);

                                            
/*! @brief          Gets audio track info of a specified track number
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     u4TrackNum      The audio track number
 *  @param [out]    ptInfo          References to the audio track information.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           only used in Lib Master model.  MtkPbLib will try to get the info, but is not guaranteed.  If an info entry is not available, MtkPbLib shall fill it with ::IMTK_PB_UNKNOWN_VALUE.
 *  @warning        This API interface is not confirmed yet.  Calling this function is not recommended.
 *  @see            IMtkPb_Ctrl_GetCurAudInfo()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetAudTrackInfo(IMTK_PB_HANDLE_T                    hHandle,
                                                        uint32_t                            u4TrackNum,
                                                        IMTK_PB_CTRL_GET_AUD_TRACK_INFO_T*  ptInfo);


/*! @brief          Gets audio track info of the currently playing audio track.
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [out]    ptInfo          References to the audio track information.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           only available in PLAYING and PAUSED states, in both App Master and Lib Master models
 *  @warning        This API interface is not confirmed yet.  Calling this function is not recommended.
 *  @see            IMtkPb_Ctrl_GetAudTrackInfo()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetCurAudTrackInfo(IMTK_PB_HANDLE_T                    hHandle,
                                                           IMTK_PB_CTRL_GET_AUD_TRACK_INFO_T*  ptInfo);


/*! @brief          Gets video track info.
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [out]    ptInfo          References to the video track information.
 *  @return         Return the error code
 *  @pre            Lib Master model, PLAYING, PAUSED, or READY state.
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   not success
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           If the file contains no video, this function will return IMTK_PB_ERROR_CODE_NOT_OK.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetVidTrackInfo(IMTK_PB_HANDLE_T                    hHandle,
                                                        IMTK_PB_CTRL_GET_VID_TRACK_INFO_T*  ptInfo);

 
/** @brief          Gets current playback position
 *  @param [in]     hHandle     The handle of playback control instance.
 *  @param [out]    pu4CurTime  current playback time in millisecond
 *  @param [out]    pu8CurPos   References to the current playback file position in byte offset
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           only available in Lib Master model in PLAYING or PAUSED states.  App can use this function to get current playback time in the polling style, or it can wait for the ::IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE event to get the same information.
 *  @see            IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetCurrentPos(IMTK_PB_HANDLE_T  hHandle,
                                                      uint32_t*         pu4CurTime,   
                                                      uint64_t*         pu8CurPos);


/** @brief          Gets current playback time stamp
 *  @param [in]     hHandle     The handle of playback control instance.
 *  @param [out]    pu8CurVidPTS   the PTS of the picture that is currently being displayed (if available), in 90kHz.
 *  @param [out]    pu8CurAudPTS   the PTS of the audio sample that is currently being played (if available), in 90kHz.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           only available in App Master model in PLAYING or PAUSED states and when ::IMTK_PB_CTRL_SET_MEDIA_INFO_T.fgSynchronized was set to true.  If any PTS is unavailable, MtkPbLib should fill ::IMTK_INVALID_PTS to the output parameter.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetCurrentPTS(IMTK_PB_HANDLE_T  hHandle,
                                                      uint64_t*         pu8CurVidPTS,
                                                      uint64_t*         pu8CurAudPTS);
                                                                                                              

/*! @brief          Gets current playback state
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [out]    ptState         References to the current playback status.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see 
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetState(IMTK_PB_HANDLE_T       hHandle,
                                                 IMTK_PB_CTRL_STATE_T*  ptState);

/*!
 * @}
 */
/*! @brief          Set capture parameter.
 *  @param [in]     b_on_off            The pvAppCbTag set by all buffer callback requesting functions. 
 *  @param [in]     ui4_sample_rate     The sampling frequency of the audio, recommended is 44100&48000, default is 44100.
 *  @param [in]     ui1_sample_depth    The sample bit depth of the audio, recommended is 16.
 *  @param [in]     ui1_sample_channels The number of audio channels, valid value is 1 or 2.
 *  @return         Return true if HACS interface implementd, otherwise false.
 *  @retval         TRUE   Success.
 *  @retval         FAIL   Failed.
 *  @note
 *  @see 
 */
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_Capture_Setting(bool      b_on_off,
                                                     uint32_t  ui4_sample_rate,
                                                     uint8_t   ui1_sample_depth,
                                                     uint8_t   ui1_sample_channels);


/*! @brief          Send the buffer data to the upper layer.
 *  @param [in]     pvAppCbTag      The pvAppCbTag set by all buffer callback requesting functions. 
 *  @param [in]     pu1Buf          The buffer address to be send.
 *  @param [in]     u4Len           The buffer length in bytes.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK   Success.
 *  @note
 *  @see 
 */
typedef void (*IMtkPb_Ctrl_Ext_Buf_Cb_Fct)(const char*  pu1Buf,
                                              long        u4Len,
                                              void*     pvAppCbTag);


/*! @brief          Sets playback buffer sink. 
 *  @param [in]     eStreamType     The stream type to be monitored.
 *  @param [in]     pfnCb           When data arrives, it will use this pfnCb to send the data to upper layer.
 *  @param [in]     pvAppCbTag      The pvAppCbTag set by all buffer callback requesting functions. 
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @note
 *  @see 
 */
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetBufferSink(IMTK_PB_CTRL_STREAM_TYPE    eStreamType,
                                                   IMtkPb_Ctrl_Ext_Buf_Cb_Fct  pfnCb,
                                                   void*                       pvAppCbTag);

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetVideoFreeze(uint8_t b_enable);

/*! @} */

#ifdef __cplusplus
}
#endif

#endif /* _I_MTK_PB_CTRL_ */
