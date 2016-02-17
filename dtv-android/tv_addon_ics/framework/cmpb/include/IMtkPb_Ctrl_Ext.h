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

    IMtkPb_Ctrl_Ext defines the extension of IMtkPb_Ctrl, including mainly the following functions:

    -# Extended pipe construction
    -# Playback output buffer parameters setting
    -# Encoder / muxer / mixer data sink setting
    -# Encoder operation
    -# Snapshot taking

  @section EncoderOp Notes on Encoder operation

    - The function IMtkPb_Ctrl_Ext_EncPb() should be invoked before IMtkPb_Ctrl_SetMediaInfo().
    - Once the call back function in type of IMtkPb_Ctrl_Ext_Enc_Buf_Cb_Fct() is invoked, App should copy the called back content ::IMTK_PB_CTRL_EXT_ENC_BUF_CB_INFO_T
      as follows:
      - If the eType is ::IMTK_PB_CTRL_STREAM_TYPE_VIDEO, the whole array of ptAUSegs inside ::IMTK_PB_CTRL_EXT_ENC_BUF_CB_VIDEO_INFO_T
 	should be copied for later use.  The length of the array is u4AUNs in ::IMTK_PB_CTRL_EXT_ENC_BUF_CB_VIDEO_INFO_T, and its type is
 	::IMTK_PB_CTRL_EXT_VIDEO_AU_SEG_T.  Once a segment of video AU data is processed and is ready to be released, the copied corresponding ::IMTK_PB_CTRL_EXT_VIDEO_AU_SEG_T
 	is passed into IMtkPb_Ctrl_Ext_FreeEncCbBuffer().
      - If the eType is ::IMTK_PB_CTRL_STREAM_TYPE_AUDIO, the structure of IMTK_PB_CTRL_EXT_ENC_BUF_CB_AUDIO_INFO_T should be copied for later
 	use.  Once the piece of audio data is processed and is ready to be released, the copied corresponding ::IMTK_PB_CTRL_EXT_AUDIO_AU_SEG_T is passed into
 	IMtkPb_Ctrl_Ext_FreeEncCbBuffer().
    - The function IMtkPb_Ctrl_Ext_FreeEncCbBuffer() can not be invoked in the thread context of the call back function in type of IMtkPb_Ctrl_Ext_Enc_Buf_Cb_Fct().
      It must be invoked in other thread context and after the data in the called back encoder buffer has been processed.
    - The encoder is disabled by default.  To enable the encoder, the function IMtkPb_Ctrl_Ext_EncEnable() should be invoked.
    - The function of IMtkPb_Ctrl_Ext_RequestKeyframe() can be only invoked during playback / encoding.
    - Theoretically, all the other encoder function except IMtkPb_Ctrl_Ext_RequestKeyframe() can be invoked during or before playback / encoding.  However,
      due to actual H/W implementation, it is possible that some can not be invoked during playback / encoding.  If in such case, it may return
      ::IMTK_PB_ERROR_CODE_NOT_OK to App.
    - Before the video encoder is enabled, all the initial value of video frame rate, video bit rate and video resolution should be set.
    - The function of IMtkPb_Ctrl_Ext_SetEncoderRectangle() is used to specify the clipping rectangle on the recoding frame.  It is not necessary to encode full
      source frame all the time.


  @section Snapshot Notes on Snapshot taking

    - Snapshot taking includes 2 functions: IMtkPb_Ctrl_Ext_StartSnapshot() and IMtkPb_Ctrl_Ext_StopSnapshot():
      - IMtkPb_Ctrl_Ext_StartSnapshot() can be invoked if one of the following conditions is satisfied:
      - If the playback state machine is in state of PLAYING or PAUSED, or
      - If the playback control is in App Master model and the following 2 conditions are both satisfied:
        - IMtkPb_Ctrl_Stop() has been invoked for the playback handle
        - IMtkPb_Ctrl_SetMediaInfo() with NULL ::ptMediaInfo has not been invoked
      - IMtkPb_Ctrl_Ext_StopSnapshot() can be invoked if the following 3 conditions are both satisfied:
          - IMtkPb_Ctrl_Open() of the corresponding playback control has been invoked
          - IMtkPb_Ctrl_Ext_StartSnapshot() of the corresponding playback control and snaphost buffers has been invoked
          - IMtkPb_Ctrl_Close() of the corresponding playback control has not been invoked
    - After invoking IMtkPb_Ctrl_Ext_StartSnapshot(), App can encode the snapshot data and then invokes IMtkPb_Ctrl_Ext_StopSnapshot() to free the buffer.
      The requested raw data format and the clipping rectangle on the snapshot can be also specified.
    - The data in the buffer pu1BufC of ::IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_YUV_INFO_T consists of both Cb and Cr data:
      - If it is in type of ::IMTK_PB_CTRL_EXT_SNAPSHOT_YUV420, the pu1BufC buffer consists of repeating 2-byte entities of "1 byte Cb and then 1 byte Cr".

  @section YCbuffer Instructions for user space program to access YUV420 Y/C buffers returned in ::IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_YUV_INFO_T
    - In ::IMTK_PB_CTRL_EXT_SNAPSHOT_DIMENSION_T, the width (W), height (H) and pitch (P) of the clipped image is returned.
    - Suppose F(x, y) = ((x + (y - 1)) / y) * y, F(x, y) is the value that "larger than x and can be divisible by y".
    - If W is even, the width of Y/C buffers will be F(W, P).  In the F(W, P) bytes, only first W bytes are meaningful, the rest (F(W, P) - W) bytes should be skipped.
    - If W is odd, the width of Y/C buffers will be F(W, P)
      - For Y buffer, only first W bytes are meaningful, the rest (F(W, P) - W) bytes should be skipped.
      - For C buffer, only first (W + 1) bytes are meaningful, the rest (F(W, P) - (W + 1)) bytes should be skipped.
    - If H is even, the height of C buffer will be (H / 2), and the height of Y buffer will be H.
    - If H is odd, the height of C buffer will be ((H + 1) / 2), and the height of Y buffer will be H.

  @section SequenceDiagram Sequence Diagram

    Here are some sequence diagrams to depict the operations in IMtkPb_Ctrl_Ext:
    - @ref EncoderScenarios
    - @ref SnapshotTaking

*/


 /*----------------------------------------------------------------------------*/
/*! @addtogroup IMtkPb_Ctrl_Ext
 *  @{
 */
/*----------------------------------------------------------------------------*/


#ifndef _I_MTK_PB_CTRL_EXT_
#define _I_MTK_PB_CTRL_EXT_

#ifdef __cplusplus
extern "C" {
#endif

/*-----------------------------------------------------------------------------
                    include files
-----------------------------------------------------------------------------*/
#include "IMtkPb_ErrorCode.h"
#include "IMtkPb_Ctrl.h"

/*-----------------------------------------------------------------------------
                    macros, defines, typedefs, enums
-----------------------------------------------------------------------------*/

/*! @union IMTK_PB_CTRL_EXT_AV_CODEC_T
 *  @brief  Audio / video codec
 */
typedef union
{
        IMTK_PB_CTRL_AUD_ENC_T  eAudEnc;            ///< Audio Codec
        IMTK_PB_CTRL_VID_ENC_T  eVidEnc;            ///< Video Codec
} IMTK_PB_CTRL_EXT_AV_CODEC_T;

/*! @struct IMTK_PB_CTRL_EXT_AMIX_INFO_T
 *  @brief  Audio mixer info
 */
typedef struct
{
    uint32_t u4Src1Gain;            ///< First audio source mix gain, possible value [0:0x7FFFFF]
    uint32_t u4Src2Gain;            ///< Second Audio source mix gain, possible value [0:0x7FFFFF]
} IMTK_PB_CTRL_EXT_AMIX_INFO_T;

/*! @enum IMTK_PB_CTRL_EXT_VMIX_ZORDER_T
 *  @brief  Video mixer z-order
 */

typedef enum
{
    IMTK_PB_CTRL_EXT_VMIX_SRC1_TOP              =   0,      ///< 1st source is on the top of 2nd source
    IMTK_PB_CTRL_EXT_VMIX_SRC2_TOP              =   1       ///< 2nd source is on the top of 1st source
} IMTK_PB_CTRL_EXT_VMIX_ZORDER_T;

/*! @struct IMTK_PB_CTRL_EXT_VMIX_INFO_T
 *  @brief  Video mixer info.  The height and width of the final mixed rectangle are the same as those of the clipping area of the bottom source.
 */
typedef struct
{
    IMTK_PB_CTRL_RECT_T tSrc1Rect;        ///< clipping area of 1st source
    IMTK_PB_CTRL_RECT_T tSrc2Rect;        ///< clipping area of 2nd source

    IMTK_PB_CTRL_EXT_VMIX_ZORDER_T eZorder;   ///< z-order

    IMTK_PB_CTRL_RECT_T tTopSrcRect;      ///< display area for the clipping area of the top source on the final mixed rectangle
} IMTK_PB_CTRL_EXT_VMIX_INFO_T;

/*! @union IMTK_PB_CTRL_EXT_AVMIX_INFO_T
 *  @brief  Audio / video mix info
 */
typedef union
{
        IMTK_PB_CTRL_EXT_AMIX_INFO_T  tAMixInfo;            ///< Audio Codec
        IMTK_PB_CTRL_EXT_VMIX_INFO_T  tVMixInfo;            ///< Video Codec
} IMTK_PB_CTRL_EXT_AVMIX_INFO_T;

/*! @enum IMTK_PB_CTRL_EXT_VSINK_T
 *  @brief  Video sink
 */

typedef enum
{
    IMTK_PB_CTRL_EXT_VSINK_MAIN_VIDEO              =   0,      ///< main video
    IMTK_PB_CTRL_EXT_VSINK_SUB_VIDEO               =   1       ///< sub video
} IMTK_PB_CTRL_EXT_VSINK_T;

/*! @enum IMTK_PB_CTRL_EXT_ASINK_T
 *  @brief  Audio sink
 */

typedef enum
{
    IMTK_PB_CTRL_EXT_ASINK_SPEAKER              =   0      ///< TV speaker
} IMTK_PB_CTRL_EXT_ASINK_T;

/*! @struct IMTK_PB_CTRL_EXT_VIDEO_AU_SEG_T
 *  @brief data buffer segments for a video AU.  For H.264 format, it represents a NAL unit.
 */

typedef struct
{
    	uint8_t* pu1Buf1;	      ///< 1st buffer segment start address of the video AU
    	uint32_t u4Buf1Len;	      ///< 1st buffer segment length
    	uint8_t* pu1Buf2;	      ///< 2nd buffer segment start address of the video AU.  Null if no need 2nd buffer.
    	uint32_t u4Buf2Len;    	      ///< 2nd buffer segment length.  Zero if no need 2nd buffer.
} IMTK_PB_CTRL_EXT_VIDEO_AU_SEG_T;

/*! @struct IMTK_PB_CTRL_EXT_ENC_BUF_CB_VIDEO_INFO_T
 *  @brief  The buffer callback video data specification info
 */

typedef struct
{
	uint32_t 				u4AUNs;			///< Number of video AUs called back.
	IMTK_PB_CTRL_EXT_VIDEO_AU_SEG_T*	ptAUSegs;		///< Start pointer of video AU buffer segment array.  Element number is the same as ::u4AUNs.
} IMTK_PB_CTRL_EXT_ENC_BUF_CB_VIDEO_INFO_T;

/*! @struct IMTK_PB_CTRL_EXT_ENC_BUF_CB_AUDIO_INFO_T
 *  @brief  The buffer callback audio data specification info
 */

typedef struct
{
	uint32_t u4SampleNs;		///< Audio samples called back in the callback buffer.
	uint8_t* pu1Buf;		///< Start address of called back buffer
        uint32_t u4Len;			///< Length of called back audio data
} IMTK_PB_CTRL_EXT_ENC_BUF_CB_AUDIO_INFO_T;

/*! @union IMTK_PB_CTRL_EXT_ENC_BUF_CB_INFO_T
 *  @brief  The encoder buffer callback data specification info
 */

typedef union
{
	IMTK_PB_CTRL_EXT_ENC_BUF_CB_VIDEO_INFO_T	tVideoInfo;	///< Video encoder callback data info
	IMTK_PB_CTRL_EXT_ENC_BUF_CB_AUDIO_INFO_T	tAudioInfo;	///< Audio encoder callback data info
} IMTK_PB_CTRL_EXT_ENC_BUF_CB_INFO_T;

/*! @struct IMTK_PB_CTRL_EXT_ENC_VIDEO_SETTING_T
 *  @brief  The structure of video setting
 */

typedef struct
{
	uint16_t 				u2Width;			///< width of video.
	uint16_t 				u2Height;			///< Height of video.
} IMTK_PB_CTRL_EXT_ENC_VIDEO_SETTING_T;

/*! @struct IMTK_PB_CTRL_EXT_ENC_AUDIO_SETTING_T
 *  @brief  The structure of audio setting
 */

typedef struct
{
	uint32_t u4SampleRate;		///< Audio samples rate of record.
	uint8_t  u1SampleSize;		///< Audio sample size
    uint8_t  u1Channels;		///< Audio channels
} IMTK_PB_CTRL_EXT_ENC_AUDIO_SETTING_T;

/*! @union IMTK_PB_CTRL_EXT_ENC_BUF_CB_INFO_T
 *  @brief  The encoder buffer callback data specification info
 */

typedef union
{
	IMTK_PB_CTRL_EXT_ENC_VIDEO_SETTING_T	tVideoSetting;	///< Video setting 
	IMTK_PB_CTRL_EXT_ENC_AUDIO_SETTING_T	tAudioSetting;	///< Audio setting 
} IMTK_PB_CTRL_EXT_ENC_REC_SETTING_T;


/*! @enum IMTK_PB_CTRL_EXT_SNAPSHOT_FMT_T
 *  @brief  Data format for video frame snap shot
 */

typedef enum
{
    IMTK_PB_CTRL_EXT_SNAPSHOT_YUV420              =   0,      ///< YUV 420
} IMTK_PB_CTRL_EXT_SNAPSHOT_FMT_T;

/*! @struct IMTK_PB_CTRL_EXT_SNAPSHOT_DIMENSION_T
 *  @brief  Dimension for video frame snapshot
 */

typedef struct
{
    	uint32_t u4Width;      		///< Snapshot width
    	uint32_t u4Height;		///< Snapshot height
    	uint32_t u4WidthPitch;          ///< Snapshot width pitch
} IMTK_PB_CTRL_EXT_SNAPSHOT_DIMENSION_T;

/*! @struct IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_YUV_INFO_T
 *  @brief  The YUV snapshot buffer info
 */

typedef struct
{
	uint8_t* pu1BufY;		///< Start address of buffer for Y
        uint32_t u4LenY;		///< Length of buffer for Y
	uint8_t* pu1BufC;		///< Start address of buffer for C
        uint32_t u4LenC;		///< Length of buffer for C
} IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_YUV_INFO_T;

/*! @struct IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_INFO_T
 *  @brief  The snapshot buffer info
 */

typedef struct
{
	IMTK_PB_CTRL_EXT_SNAPSHOT_FMT_T 		eFmt;
	IMTK_PB_CTRL_EXT_SNAPSHOT_DIMENSION_T           tDimInfo;

	union {
		IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_YUV_INFO_T	tYuvInfo;	///< Yuv data buffer info
	} uBufInfo;

} IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_INFO_T;

/*! @brief          Function pointer of encoder buffer callback
 *  @param [in]     pvAppCbTag      the pvAppCbTag set by IMtkPb_Ctrl_Ext_SetEncBufferParm()
 *  @param [in]     eType           Encoded stream data type, audio or video data.
 *  @param [in]     ptInfo          Encoded stream data specification info.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           App is notified by this callback function whenever the buffer is ready for App use.  Once the callback is received:
 *                  1. If the eType is ::IMTK_PB_CTRL_STREAM_TYPE_VIDEO, the whole array of ptAUSegs inside ::IMTK_PB_CTRL_EXT_ENC_BUF_CB_VIDEO_INFO_T
 *		       should be copied for later use.  The length of the array is u4AUNs in ::IMTK_PB_CTRL_EXT_ENC_BUF_CB_VIDEO_INFO_T, and its type is
 *                     ::IMTK_PB_CTRL_EXT_VIDEO_AU_SEG_T.
 *                  2. If the eType is ::IMTK_PB_CTRL_STREAM_TYPE_AUDIO, the structure of IMTK_PB_CTRL_EXT_ENC_BUF_CB_AUDIO_INFO_T should be copied for later
 *                     use.
 *  @see
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Ext_Enc_Buf_Cb_Fct)(void*                			pvAppCbTag,
                                                                 IMTK_PB_CTRL_STREAM_TYPE		eType,
                                                                 IMTK_PB_CTRL_EXT_ENC_BUF_CB_INFO_T*   	ptInfo);
#if 0
/*! @brief          Function pointer of buffer callback
 *  @param [in]     pvAppCbTag      the pvAppCbTag set by all buffer callback requesting functions.  Now the functions include:
    -# IMtkPb_Ctrl_Ext_SetBufferSink()
    -# IMtkPb_Ctrl_Ext_SetMuxBufferParm()
    -# IMtkPb_Ctrl_Ext_SetMixBufferParm()
 *  @param [in]     pu1Buf          Buffer pointer.
 *  @param [in]     u4Len           Buffer length in byte.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           App is notified by this callback function whenever the buffer is ready for application use.  Once the callback is received,
                    App should copy the content in the buffer to somewhere else before the callback function returns.
 *  @see
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Ext_Buf_Cb_Fct)(void*                pvAppCbTag,
                                                              uint8_t*             pu1Buf,
                                                              uint32_t             u4Len);
#endif
/*!
 * @name Extended pipe construction
 * @{
 */

/*! @brief          Add an encoder after a playback control.
 *  @param [out]    phHandle                Reference to the handle of encoder.
 *  @param [in]     hPbHandle               Playback control handle
 *  @param [in]     eStreamType             Audio / video selection
 *  @param [in]     ptCodec                 Audio / video codec specification
 *  @param [in]     pu1Profile              Profile string for opening a encoder
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hPbHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           The function should be invoked before IMtkPb_Ctrl_SetMediaInfo(). The encoder is disabled by default.
 *                  To enable the encoder, the function IMtkPb_Ctrl_Ext_EncEnable() should be invoked.
 *  @see            IMtkPb_Ctrl_Ext_EncEnable
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_EncPb(IMTK_ENCODER_HANDLE_T*        phHandle,
                                                  IMTK_PB_HANDLE_T              hPbHandle,
                                                  IMTK_PB_CTRL_STREAM_TYPE      eStreamType,
                                                  IMTK_PB_CTRL_EXT_AV_CODEC_T*  ptCodec,
                                                  uint8_t*                      pu1Profile);

/*! @brief          Add an encoder after a mixer.
 *  @param [out]    phHandle                Reference to the handle of encoder.
 *  @param [in]     hMixHandle              Mixer handle
 *  @param [in]     ptCodec                 Audio / video codec specification
 *  @param [in]     pu1Profile              Profile string for opening a mixer
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hPbHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_EncMixer(IMTK_ENCODER_HANDLE_T*         phHandle,
                                                     IMTK_MIXER_HANDLE_T            hMixHandle,
                                                     IMTK_PB_CTRL_EXT_AV_CODEC_T*   ptCodec,
                                                     uint8_t*                       pu1Profile);

/*! @brief          Add a muxer after encoders.
 *  @param [out]    phHandle                Reference to the handle of muxer.
 *  @param [in]     hEncHandle1             1st encoder handle
 *  @param [in]     hEncHandle2             2nd encoder handle
 *  @param [in]     ptMedia                 Mux media type
 *  @param [in]     pu1Profile              Profile string for opening a muxer
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hEncHandle1 or hEncHandle2 is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_Mux(IMTK_MUXER_HANDLE_T*        phHandle,
                                                IMTK_ENCODER_HANDLE_T       hEncHandle1,
                                                IMTK_ENCODER_HANDLE_T       hEncHandle2,
                                                IMTK_PB_CTRL_MEDIA_TYPE_T*  ptMedia,
                                                uint8_t*                    pu1Profile);

/*! @brief          Add a mixer after playback controls.
 *  @param [out]    phHandle                Reference to the handle of mixer.
 *  @param [in]     hPbHandle1              1st Playback control handle
 *  @param [in]     hPbHandle2              2nd Playback control handle
 *  @param [in]     eStreamType             Audio / video selection
 *  @param [in]     ptInfo                  Audio / video mix info
 *  @param [in]     pu1Profile              Profile string for opening a encoder
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hPbHandle1 or hPbHandle2 is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_Mix(IMTK_MIXER_HANDLE_T*            phHandle,
                                                IMTK_PB_HANDLE_T                hPbHandle1,
                                                IMTK_PB_HANDLE_T                hPbHandle2,
                                                IMTK_PB_CTRL_STREAM_TYPE        eStreamType,
                                                IMTK_PB_CTRL_EXT_AVMIX_INFO_T*  ptInfo,
                                                uint8_t*                        pu1Profile);

/*!
 * @}
 * @name Set playback parameters
 * @{
 */
#if 0
/*! @brief          Sets playback buffer sink
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     eStreamType     Audio / video selection
 *  @param [in]     pfnCb           Buffer callback function
 *  @param [in]     pvAppCbTag      Buffer callback tag
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @retval         IMTK_PB_ERROR_CODE_NO_BUFFERSINK            No buffer sink can be set for this playback
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetBufferSink(IMTK_PB_HANDLE_T            hHandle,
                                                          IMTK_PB_CTRL_STREAM_TYPE    eStreamType,
                                                          IMtkPb_Ctrl_Ext_Buf_Cb_Fct  pfnCb,
                                                          void*                       pvAppCbTag);
#endif
/*!
 * @}
 * @name Set encoder / muxer / mixer data sink
 * @{
 */

/*! @brief          Sets encoder user space output buffer parameter
 *  @param [in]     hHandle         The handle of encoder
 *  @param [in]     pfnCb           Encoder buffer callback function
 *  @param [in]     pvAppCbTag      Buffer callback tag
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetEncBufferParm(IMTK_ENCODER_HANDLE_T          hHandle,
                                                             IMtkPb_Ctrl_Ext_Enc_Buf_Cb_Fct     pfnCb,
                                                             void*                          pvAppCbTag);

/*! @brief          Free the called back encoder buffer from call back function in type of IMtkPb_Ctrl_Ext_Enc_Buf_Cb_Fct().
 *  @param [in]     hHandle         The handle of encoder
 *  @param [in]     eType	    The encoder buffer type, audio or video.
 *  @param [in]     pui1AU          The pointer of the structure illustrating the encoder buffer to be released.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @note           This function should not be invoked in the thread context of call back function in type of IMtkPb_Ctrl_Ext_Enc_Buf_Cb_Fct().  If the eType is
 		    ::IMTK_PB_CTRL_STREAM_TYPE_VIDEO, the pui1AU should be in type of ::IMTK_PB_CTRL_EXT_VIDEO_AU_SEG_T.  If the eType is
 		    ::IMTK_PB_CTRL_STREAM_TYPE_AUDIO, the pui1AU should be in type of ::IMTK_PB_CTRL_EXT_ENC_BUF_CB_AUDIO_INFO_T.
 *  @see            IMtkPb_Ctrl_Ext_SetEncBufferParm(), IMtkPb_Ctrl_Ext_Enc_Buf_Cb_Fct(), ::IMTK_PB_CTRL_EXT_VIDEO_AU_SEG_T, ::IMTK_PB_CTRL_EXT_ENC_BUF_CB_AUDIO_INFO_T
 */

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_FreeEncCbBuffer(IMTK_ENCODER_HANDLE_T          hHandle,
							    IMTK_PB_CTRL_STREAM_TYPE 	   eType,
                                                             uint8_t*		pui1AU);

/*! @brief          Sets muxer user space output buffer parameter
 *  @param [in]     hHandle         The handle of muxer
 *  @param [in]     pfnCb           Buffer callback function
 *  @param [in]     pvAppCbTag      Buffer callback tag
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetMuxBufferParm(IMTK_MUXER_HANDLE_T            hHandle,
                                                             IMtkPb_Ctrl_Ext_Buf_Cb_Fct     pfnCb,
                                                             void*                          pvAppCbTag);

/*! @brief          Sets mixer user space output buffer parameter
 *  @param [in]     hHandle         The handle of mixer
 *  @param [in]     pfnCb           Buffer callback function
 *  @param [in]     pvAppCbTag      Buffer callback tag
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetMixBufferParm(IMTK_MIXER_HANDLE_T            hHandle,
                                                             IMtkPb_Ctrl_Ext_Buf_Cb_Fct     pfnCb,
                                                             void*                          pvAppCbTag);

/*! @brief          Sets mixer audio output Parameter
 *  @param [in]     hHandle         The handle of audio mixer
 *  @param [in]     eSink           Audio sink
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetMixAudioSink(IMTK_MIXER_HANDLE_T          hHandle,
                                                            IMTK_PB_CTRL_EXT_ASINK_T     eSink);

/*! @brief          Sets mixer video output Parameter
 *  @param [in]     hHandle         The handle of video mixer
 *  @param [in]     eSink           Video sink
 *  @param [in]     ptSrcRect       References to the source rectangle for display.
 *  @param [in]     ptDstRect       References to the destination rectangle for display.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetMixVideoSink(IMTK_MIXER_HANDLE_T         hHandle,
                                                            IMTK_PB_CTRL_EXT_VSINK_T    eSink,
                                                            IMTK_PB_CTRL_RECT_T*        ptSrcRect,
                                                            IMTK_PB_CTRL_RECT_T*        ptDstRect);

/*!
 * @}
 * @name Encoder operation
 * @{
 */

/*! @brief          Enable / disable this encoder
 *  @param [in]     hHandle         The handle of video encoder
 *  @param [in]     fgEnable        Enable / disable the encoder
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note           Can be invoked before or during playback / encoding.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_EncEnable(IMTK_ENCODER_HANDLE_T      hHandle,
                                                      bool fgEnable);

/*! @brief          Request video encoder to generate a key frame
 *  @param [in]     hHandle         The handle of video encoder
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note           Can be invoked only during playback / encoding.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_RequestKeyframe(IMTK_ENCODER_HANDLE_T      hHandle);

/*! @brief          Set video encoder bitrate
 *  @param [in]     hHandle         The handle of video encoder
 *  @param [in]     u4Bitrate       The bitrate in byte
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note           Can be invoked during or before playback / encoding.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetVidBitRate(IMTK_ENCODER_HANDLE_T     hHandle,
                                                          uint32_t                  u4Bitrate);

/*! @brief          Set video encoder frame rate
 *  @param [in]     hHandle         The handle of video encoder
 *  @param [in]     u4Framerate     The frame rate
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note           Can be invoked during or before playback / encoding.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetVidFrameRate(IMTK_ENCODER_HANDLE_T   hHandle,
                                                            uint32_t                u4Framerate);

/*! @brief          Set video encoder resolution
 *  @param [in]     hHandle      The handle of video encoder
 *  @param [in]     u4X          The X-axis resultion
 *  @param [in]     u4Y          The Y-axis resultion
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note           Can be invoked during or before playback / encoding.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetVidResolution(IMTK_ENCODER_HANDLE_T      hHandle,
                                                             uint32_t                   u4X,
                                                             uint32_t                   u4Y);

/*! @brief          Set video encoder clipping rectangle
 *  @param [in]     hHandle      The handle of video encoder
 *  @param [in]     ptRec        The clipping rectagle
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note           Can be invoked during or before playback / encoding.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetEncoderRectangle(IMTK_ENCODER_HANDLE_T      hHandle,
                                                                IMTK_PB_CTRL_RECT_T*       ptRec);

/*! @brief          Set audio encoder bitrate
 *  @param [in]     hHandle         The handle of audio encoder
 *  @param [in]     u4Bitrate       The bitrate in byte
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note           Can be invoked during or before playback / encoding.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetAudBitRate(IMTK_ENCODER_HANDLE_T     hHandle,
                                                          uint32_t                  u4Bitrate);

/*! @brief          Set audio encoder sample rate
 *  @param [in]     hHandle         The handle of audio encoder
 *  @param [in]     eSampleRate     The sample rate
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note           Can be invoked during or before playback / encoding.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetAudSampleRate(IMTK_ENCODER_HANDLE_T          hHandle,
                                                             IMTK_PB_CTRL_AUD_SAMPLE_RATE_T eSampleRate);

/*! @brief          Set audio encoder channel number
 *  @param [in]     hHandle         The handle of audio encoder
 *  @param [in]     eChNum          The encoding channel number
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @note           Can be invoked during or before playback / encoding.
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_SetAudChannelNumber(IMTK_ENCODER_HANDLE_T       hHandle,
                                                                IMTK_PB_CTRL_AUD_CH_NUM_T   eChNum);
/*!
 * @}
 * @name Take snap shot
 * @{
 */

/*! @brief          Taking a snap shot on a playback control.
 *  @param [in]     hHandle                 Playback control handle
 *  @param [in]     eFmt                    Raw data format requested
 *  @param [out]    ptSrcRect               Clipping rectangle of the snapshot
 *  @param [out]    ptBuf                   Snapshot buffer info
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_OK                   Failed.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           The buffer for the snapshot raw data is prepared inside MtkPbLib.  The function can be invoked at anytime if all the following condition
                    is satisfied:
                    1. IMtkPb_Ctrl_Play has been invoked for the playback handle
                    2. IMtkPb_Ctrl_SetMediaInfo with NULL ptMediaInfo has not been invoked, even IMtkPb_Ctrl_Stop has been invoked for the playback handle.
                    Once this function is invoked, the buffer should be relased by invoking IMtkPb_Ctrl_Ext_StopSnapshot().
 *  @see            IMtkPb_Ctrl_Ext_StopSnapshot()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_StartSnapshot(IMTK_PB_HANDLE_T  hHandle,
                                                          IMTK_PB_CTRL_EXT_SNAPSHOT_FMT_T	eFmt,
                                                          IMTK_PB_CTRL_RECT_T*			ptSrcRect,
                                                          IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_INFO_T* ptBuf);

/*! @brief          Release snap shot buffer
 *  @param [in]     hHandle               Playback control handle
 *  @param [in]     ptBuf                 The snapshot buffer info to release
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see            IMtkPb_Ctrl_Ext_StartSnapshot()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_StopSnapshot(IMTK_PB_HANDLE_T  hHandle,
                                                         IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_INFO_T* ptBuf);


extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Ext_GetSetting(IMTK_PB_CTRL_STREAM_TYPE    eStreamType,
                                                       IMTK_PB_CTRL_EXT_ENC_REC_SETTING_T*  prSetting);

/*!
 * @}
 */


/*! @} */

#ifdef __cplusplus
}
#endif

#endif /* _I_MTK_PB_CTRL_EXT_ */
