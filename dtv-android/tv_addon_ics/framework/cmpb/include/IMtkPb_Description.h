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
/*----------------------------------------------------------------------------*/

/**
@mainpage Mediatek Digital Home SDK

@version 0.33

@section intro Introduction
This document describes <i><b>IMtkPb</b></i>, an interface provided by MTK to allow third-parties to utilize MTK platform playabck capabilities.

@section os Operating System
This SDK is available on the Linux operating system as a library, and could be linked and used by any programs.

@section dep Dependency
This interface is dependent on some type definitions defined in the C standard header files "stdint.h" and "stdbool.h".

@section nomen Nomenclature
This section defines the meanings of some terms used throughout this document:
- IMtkPb
  - the interface defined in this document
- App
  - the party "above" the IMtkPb interface, probably consisting of third-party libraries and glue layers.  See the block diagram below.
- MtkPbLib
  - the library providing the implementation of the IMtkPb interface
- operating model
  - defining which party (App or MtkPbLib) is in charge of analyzing content metadata.  Possible models are Lib Master and App Master.
- buffering model
  - defining how bitstream data is fed into the MtkPbLib.  Possible models are URI, Pull, and Push.
- Lib Master
  - an operating model in which the MtkPbLib is in charge of analyzing content metadata
- App Master
  - an operating model in which the App is in charge of analyzing content metadata
- URI model
  - a buffering model in which the MtkPbLib retrieves the content data from the URI by itself
- Pull model
  - a buffering model in which App provides the actual data fetching implementations for the MtkPbLib to invoke
- Push model
  - a buffering model in which App actively pushes data into the MtkPbLib
- In band command
  - used in the Push model, is a command that can be inserted into the data bitstream flow in an explicit position
- MTK Private P0
  - an MTK proprietary container format defined in this document
- EOS
  - end of stream

@image html BlockDiagram.png "Block diagram"


@page ChangeNotes Change notes
@section v002to003 0.02 to 0.03
- IMtkPb_Ctrl
  - Fixed Open argument in the state diagram.
  - Change IMtkPb_Ctrl_Pull_ByteSeek_Fct() prototype.
  - Add ::IMTK_PB_CB_ERROR_CODE_EOF error code to IMtkPb_Ctrl_Pull_Read_Fct()
  - Add IMtkPb_Ctrl_Pull_Open_Fct(), IMtkPb_Ctrl_Pull_Close_Fct(), and IMtkPb_Ctrl_Pull_GetInputLen_Fct() to ::IMTK_PB_CTRL_PULL_INFO_T
  - Add pvAppTag to ::IMTK_PB_CTRL_PULL_INFO_T, to be passed back to App by IMtkPb_Ctrl_Pull_Xxx_Fct functions
  - Change pvTag argument of IMtkPb_Ctrl_Pull_Read_Async_Fct() to pvRdAsyTag to prevent confusion to other tags
  - Change pvTag argument of IMtkPb_Ctrl_RegCallback() to pvAppCbTag to prevent confusion to other tags
  - Add Audio change sequence diagrams for App Master mode and Lib Master mode.
  - Remove App Mater mode of URI.
- IMtkPb_DRM
  - initial version
@section v003to004 0.03 to 0.04
- IMtkPb_Ctrl
  - Clarified that IMtkPb_Ctrl_PreLoadData() is used only in Lib Master + URI model.
  - Added state ::IMTK_PB_CTRL_MEDIA_INFO_OK, and renamed state IMTK_PB_CTRL_SETINFO_OK to ::IMTK_PB_CTRL_ES_INFO_OK.
  - Renamed IMTK_PB_CTRL_AUD_ENC_RA to ::IMTK_PB_CTRL_AUD_ENC_COOK.
  - Clarified that u2VTrack and u2ATrack in ::IMTK_PB_CTRL_SETTING_T are used only in Lib Master model.
  - Added MTK Private media format (::IMTK_PB_CTRL_MEDIA_TYPE_MTK_P0)
  - Added IMtkPb_Ctrl_SendCmd() for push model.
- IMtkPb_DRM
  - Clarified that no cache mechanism is inside MtkPbLib's secure storage operations.
  - Added IMtkPb_DRM_Init() and IMtkPb_DRM_Terminate().
  - Added some IMtkPb_DRM specific error codes in IMtkPb_ErrorCode.h.

@section v004to005 0.04 to 0.05
- IMtkPb_Ctrl
  - Push model modifications
    - Added return code ::IMTK_PB_ERROR_CODE_GET_BUF_PENDING in ::IMTK_PB_ERROR_CODE_T for IMtkPb_Ctrl_GetBuffer().
    - Added callback notification event ::IMTK_PB_CTRL_EVENT_GET_BUF_READY in ::IMTK_PB_CTRL_EVENT_T for IMtkPb_Ctrl_GetBuffer().
    - Added an API IMtkPb_Ctrl_CancelGetBufferPending() to cancel notification event ::IMTK_PB_CTRL_EVENT_GET_BUF_READY.
  - Added a parameter in IMtkPb_Ctrl_Play() to assign the start position of playback.
  - Clarified that eSpeed in ::IMTK_PB_CTRL_SETTING_T is used only in Lib Master model.
  - Added one reserved byte in ::IMTK_PB_CTRL_MEDIA_TYPE_MTK_P0 for future use.
  - Modified the flow of audio change in App Master model.  The sequence diagrams Figure 1-3 and 2-3 were also updated.
  - Added notification event ::IMTK_PB_CTRL_EVENT_STEP_DONE for asynchronous call IMtkPb_Ctrl_Step().
  - Re-defined that IMtkPb_Ctrl_Play() / IMtkPb_Ctrl_Pause() / IMtkPb_Ctrl_Stop() / IMtkPb_Ctrl_Close() are all synchronous functions.  Removed associated events from ::IMTK_PB_CTRL_EVENT_T.
  - Clarified that ::IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW / ::IMTK_PB_CTRL_EVENT_BUFFER_FULL can only be used in Lib Master URI model.
  - Added definition of value 0xFFFFFFFFFFFFFFF for parameter pu8Len of ::IMtkPb_Ctrl_Pull_GetInputLen_Fct to support live broadcasting stream.
  - Added that ptMediaInfo could be set to NULL in IMtkPb_Ctrl_SetMediaInfo(), telling MtkPbLib to clear media info and release resources.
  - Updated @ref SMpage.  Added ::IMTK_PB_CTRL_AM_MODEL_PARAM_OK and ::IMTK_PB_CTRL_LM_MODEL_PARAM_OK states.
  - Clarified that IMtkPb_Ctrl_TimeSeek() can only be used during PLAYING state.
  - Clarified in ::IMTK_PB_CTRL_URI_INFO_T that URI model should support http, https, and file resource types.
  - Added "stdbool.h" in IMtkPb_ErrorCode.h.

@section v005to006 0.05 to 0.06
- IMtkPb_Ctrl
  - Clarified the meanings of pu4CurTime and pu8CurPos of IMtkPb_Ctrl_GetCurrentPos() in various models.
  - Fixed a typo (ES_TINFO_OK -> ES_INFO_OK) in the @ref SMpage.
  - Updated Figure 2-2: Removed IMtkPb_Ctrl_ReleaseBuffer() to prevent confusion.
  - Added @ref AppMasterNotes and @ref PushNotes for some clarifications on the App Master model and the Push model.
  - Modified @ref MTKP0page and added detailed explanations:
    - Renamed <b>Stream</b> to <b>Payload</b>, and <b>Stream Size</b> to <b>Payload Size</b>.
    - Modified the position of <b>Payload Size</b> field to be directly following the PTS field.
    - Added a <b>PT Valid</b> field in the FLAG field.

@section v006to007 0.06 to 0.07
- General
  - Added ::IMTK_PB_ERROR_CODE_SYS_LIB_ERROR for erros in system calls or standard library
- IMtkPb_Ctrl
  - Re-designed App Master media info interfaces:
    - Merged IMtkPb_Ctrl_SetVidTrackInfo() and IMtkPb_Ctrl_SetAudTrackInfo() into IMtkPb_Ctrl_SetMediaInfo().  IMTK_PB_CTRL_ES_INFO_OK state was also removed.
    - Updated the @ref SMpage and sequence diagrams to reflect the above merge.
    - Added IMtkPb_Ctrl_ChangeAudio() to change audio track during playback in App Master model.
  - Changed u4StreamId to u1StreamIdx in ::IMTK_PB_CTRL_IBC_PARAM_SET_ASF_PACKET_INFO, to be consistent with ::IMTK_PB_CTRL_ASF_VID_INFO_T and ::IMTK_PB_CTRL_ASF_AUD_INFO_T.
  - Clarified that App Master model doesn't support the MP4 media type (::IMTK_PB_CTRL_MEDIA_TYPE_MP4).
  - Added a start code in @ref MTKP0page
  - Removed IMTK_PB_CTRL_MEDIA_TYPE_MP3 and IMTK_PB_CTRL_MEDIA_TYPE_MPA, and added ::IMTK_PB_CTRL_MEDIA_TYPE_VIDEO_ES and ::IMTK_PB_CTRL_MEDIA_TYPE_AUDIO_ES in ::IMTK_PB_CTRL_MEDIA_TYPE_T.
  - Removed IMTK_PB_CTRL_MEDIA_TYPE_MPG, and clarified that ::IMTK_PB_CTRL_MEDIA_TYPE_MPEG2_PS can also support Mpeg1 system stream.
  - Removed IMTK_PB_CTRL_MEDIA_TYPE_DIVX, and renamed IMTK_PB_CTRL_VID_ENC_DIVX to ::IMTK_PB_CTRL_VID_ENC_DIVX311.
  - Updated Figure 1-2 and Figure 4-2, which were wrong.  IMtkPb_Ctrl_PreLoadData() is only used in LibMaster + URI model.
  - Added IMtkPb_Ctrl_SetPushModelEOS() to notify data finish in push model.
- IMtkPb_DRM
  - Added IMtkPb_DRM specific error codes ::IMTK_PB_ERROR_CODE_FILE_NOT_CREATE, ::IMTK_PB_ERROR_CODE_FILE_NOT_DELETE, and ::IMTK_PB_ERROR_CODE_FILE_CRYPTO_ERROR in IMtkPb_ErrorCode.h.

@section v007to008 0.07 to 0.08
- IMtkPb_Ctrl
  - Updated ::IMTK_PB_CTRL_MEDIA_MPEG2_TS_INFO_T to use PID to indicate which audio/video stream to demux.  Also updated ::IMTK_PB_CTRL_AUD_INFO_T.
  - Added ::IMTK_PB_CTRL_MEDIA_MKV_INFO_T in ::IMTK_PB_CTRL_SET_MEDIA_INFO_T, and ::IMTK_PB_CTRL_MKV_AUD_INFO_T in ::IMTK_PB_CTRL_AUD_INFO_T.
  - Added au1CodecPrivInfo in ::IMTK_PB_CTRL_ASF_VID_INFO_T to allow App to pass sequence header to MtkPbLib.
  - Renamed aFourCC to au1FourCC in various structures, to be consistent with the naming convention.
  - Clarified the endianness of au1FourCC in various structures.
  - Added bitstream detail descriptions in @ref AppMasterNotes.
- IMtkPb_DRM
  - Added detailed description on return values.

@section v008to009 0.08 to 0.09
- IMtkPb_Ctrl
  - Updated @ref SMpage : replaced SetAudTrackInfo/SetVidTrackInfo with ChangeAudio.
  - Extend IMtkPb_Ctrl_SetMediaInfo() to Lib Master model:
    - Lib Master model has to call IMtkPb_Ctrl_SetMediaInfo() too.
    - Added u4PlayFlag to ::IMTK_PB_CTRL_SET_MEDIA_INFO_T to indicate the streams to be played.
    - Added uMediaInfo union to ::IMTK_PB_CTRL_SET_MEDIA_INFO_T to contain App Master and potentially Lib Master info structures.
    - Updated @ref SMpage to reflect the change.
  - Removed IMtkPb_Ctrl_PreParse().
    - IMtkPb_Ctrl_GetMediaInfo() can be called in ::IMTK_PB_CTRL_LM_MODEL_PARAM_OK and later states, without the need of pre-parsing.
    - Removed IMTK_PB_CTRL_PREPARSING state.
    - Removed IMTK_PB_CTRL_EVENT_PRE_PARSE_DONE event.
    - Updated @ref SMpage to reflect the change.
  - Modified IMtkPb_Ctrl_GetMediaInfo() and related functions
    - Simplified ::IMTK_PB_CTRL_GET_MEDIA_INFO_T, removing music information (title, album, etc.), default tracks, and play capability masks
    - Clarified that IMtkPb_Ctrl_GetMediaInfo() is used only in the Lib Master model.
    - Clarified that IMtkPb_Ctrl_GetAudTrackInfo() and IMtkPb_Ctrl_GetVidTrackInfo() are used only in the Lib Master model.  Also, clarified that MtkPbLib may fail to get some info and will fill them with ::IMTK_PB_UNKNOWN_VALUE.
    - Added IMtkPb_Ctrl_GetCurAudTrackInfo() and IMtkPb_Ctrl_GetCurVidTrackInfo().
      - They are used to get the information about the currently playing audio or video track.
      - They're available in App Master and Lib Master models.
      - They are only available in PLAYING or PAUSED states.
  - Cleaned up parameter setting/getting functions:
    - Removed IMtkPb_Ctrl_SetPlayParam().
    - Added IMtkPb_Ctrl_SetAudTrack() to select an audio track to play.  This is for Lib Master model only, and can be called before or during playback.
    - Added IMtkPb_Ctrl_SetSpeed() to set playback speed.  This is for Lib Master model only, and can only be called during playback.
    - App Master model no longer needs parameter setting, so IMTK_PB_CTRL_AM_MEDIA_INFO_OK state was also removed.  @ref SMpage was also updated.
    - Simplified IMtkPb_Ctrl_GetPlayParam() so it can only get audio track number and playback speed.  Also clarified that it's only for Lib Master during playback.
  - Added @ref os and @ref dep sections on the Main Page.
  - Clarified that IMtkPb_Ctrl_GetBufferFullness() is available only in PLAYING and PAUSED states.
  - Changed u8SeekPos to i8SeekPos in IMtkPb_Ctrl_Pull_ByteSeek_Fct().

@section v009to010 0.09 to 0.10
- IMtkPb_Ctrl
  - Added the minimal video/audio codec support list of ::IMTK_PB_CTRL_MEDIA_TYPE_VIDEO_ES and ::IMTK_PB_CTRL_MEDIA_TYPE_AUDIO_ES media types.
    - The list is in the comment of ::IMTK_PB_CTRL_MEDIA_VIDEO_ES_INFO_T and ::IMTK_PB_CTRL_MEDIA_AUDIO_ES_INFO_T.
  - Added IMtkPb_Ctrl_SetEngineParam()
    - The setting of u4PlayFlag is moved from IMtkPb_Ctrl_SetMediaInfo() to this function.
    - IMtkPb_Ctrl_SetUriModelParam() and IMtkPb_Ctrl_SetPullModelParam() are merged into this function.
    - IMtkPb_Ctrl_SetMediaInfo() is again Lib Master only.
    - Removed IMTK_PB_CTRL_AM_MODEL_PARAM_OK and IMTK_PB_CTRL_LM_MODEL_PARAM_OK states.
  - Updated @ref SMpage to reflect recent interface changes.
  - Updated Figures 1-1, 2-1, 3-1, 3-3, 3-4, 4-1, 4-3, 4-4 to reflect recent interface changes.
  - Added a @ref nomen section in the main page.

@section v010to011 0.10 to 0.11
- IMtkPb_Ctrl
  - Added/Modified the video codec support list of ::IMTK_PB_CTRL_VID_ENC_T.
    - ::IMTK_PB_CTRL_VID_ENC_MPEG1_2
    - ::IMTK_PB_CTRL_VID_ENC_H263
    - ::IMTK_PB_CTRL_VID_ENC_WMV1
    - ::IMTK_PB_CTRL_VID_ENC_WMV2
    - ::IMTK_PB_CTRL_VID_ENC_WMV3
    - ::IMTK_PB_CTRL_VID_ENC_RV8
    - ::IMTK_PB_CTRL_VID_ENC_RV9_10
    - ::IMTK_PB_CTRL_VID_ENC_MJPEG
    - ::IMTK_PB_CTRL_VID_ENC_SORENSON_SPARK
  - Clarified how to specify that no timestamp is assigned to a packet of a @ref MTKP0page.
  - Added a summary of video codec vs. <b>PT Valid</b> value in @ref MTKP0page.
  - Added ::IMTK_PB_CTRL_MTK_P0_VID_INFO_T into ::IMTK_PB_CTRL_MEDIA_MTK_P0_INFO_T.
  - Renamed IMTK_PB_CTRL_ENGINE_PARAM to ::IMTK_PB_CTRL_ENGINE_PARAM_T.
  - Added channel number / sample rate / bit depth enumerations
    - ::IMTK_PB_CTRL_AUD_CH_NUM_T
    - ::IMTK_PB_CTRL_AUD_SAMPLE_RATE_T
    - ::IMTK_PB_CTRL_AUD_PCM_BIT_DEPTH_T
  - The following structures are modified for channel number, sample rate and bit depth enumeration
    - ::IMTK_PB_CTRL_AUD_WMA_INFO_T
    - ::IMTK_PB_CTRL_AUD_AAC_INFO_T
    - ::IMTK_PB_CTRL_AUD_PCM_INFO_T
    - ::IMTK_PB_CTRL_GET_AUD_TRACK_INFO_T
  - A parameter pu1Profile in function IMtkPb_Ctrl_Open() is added for scenario and component selection.
  - Added a attribute of <b>fgSynchronized</b> into ::IMTK_PB_CTRL_SET_MEDIA_INFO_T
- Added @ref IMtkPb_Snd
  - Added sound(PCM) control I/F
    - IMtkPb_Snd_Count()
    - IMtkPb_Snd_Query_Capability()
    - IMtkPb_Snd_Open()
    - IMtkPb_Snd_SetParameter()
    - IMtkPb_Snd_RegCallback()
    - IMtkPb_Snd_SendClip()
    - IMtkPb_Snd_Play()
    - IMtkPb_Snd_Stop()
    - IMtkPb_Snd_Close()
- Added IMtkPb_Ctrl_Ext.h
  - Added handle types for mixer / muxer / encoder in IMtkPb_ErrorCode.h.
    - ::IMTK_ENCODER_HANDLE_T
    - ::IMTK_MIXER_HANDLE_T
    - ::IMTK_MUXER_HANDLE_T
  - Added the following structure / enumerations for encoder / mixer
    - ::IMTK_PB_CTRL_EXT_AV_CODEC_T
    - ::IMTK_PB_CTRL_EXT_AMIX_INFO_T
    - ::IMTK_PB_CTRL_EXT_VMIX_ZORDER_T
    - ::IMTK_PB_CTRL_EXT_VMIX_INFO_T
    - ::IMTK_PB_CTRL_EXT_AVMIX_INFO_T
    - ::IMTK_PB_CTRL_EXT_VSINK_T
    - ::IMTK_PB_CTRL_EXT_ASINK_T
  - Added buffer callback function prototype
    - IMtkPb_Ctrl_Ext_Buf_Cb_Fct()
  - Added extended pipe construction functions
    - IMtkPb_Ctrl_Ext_EncPb()
    - IMtkPb_Ctrl_Ext_EncMixer()
    - IMtkPb_Ctrl_Ext_Mux()
    - IMtkPb_Ctrl_Ext_Mix()
  - Added function to set buffer sink in quick pipe
    - IMtkPb_Ctrl_Ext_SetBufferSink()
  - Added functions to set encoder / mixer / muxer output sink
    - IMtkPb_Ctrl_Ext_SetEncBufferParm()
    - IMtkPb_Ctrl_Ext_SetMuxBufferParm()
    - IMtkPb_Ctrl_Ext_SetMixBufferParm()
    - IMtkPb_Ctrl_Ext_SetMixAudioSink()
    - IMtkPb_Ctrl_Ext_SetMixVideoSink()
  - Added encoder operations
    - IMtkPb_Ctrl_Ext_RequestKeyframe()
    - IMtkPb_Ctrl_Ext_SetVidBitRate()
    - IMtkPb_Ctrl_Ext_SetVidFrameRate()
    - IMtkPb_Ctrl_Ext_SetVidResolution()
    - IMtkPb_Ctrl_Ext_SetAudBitRate()
    - IMtkPb_Ctrl_Ext_SetAudSampleRate()
    - IMtkPb_Ctrl_Ext_SetAudChannelNumber()
  - Added snapshot operations
    - IMtkPb_Ctrl_Ext_StartSnapshot()
    - IMtkPb_Ctrl_Ext_StopSnapshot()

@section v011to012 0.11 to 0.12
- IMtkPb_ErrorCode.h
  - Renamed NULL_HANDLE to ::IMTK_NULL_HANDLE.
  - Added ::IMTK_INVALID_PTS, to be used in @ref MTKP0page.
- @ref IMtkPb_Ctrl
  - Added IMtkPb_Ctrl_Init() and IMtkPb_Ctrl_Terminate().
  - Updated @ref MTKP0page.
    - Removed <b>Picture Type</b> from the FLAGS field.
    - Renamed <b>PT Valid</b> field to <b>OPPP</b>, and moved it to bit 3.
    - Added detailed description on how to put video and audio bitstremas into this format.
  - Defined ::IMTK_PB_CTRL_VID_CODEC_INFO_T, and added it into most format infos in ::IMTK_PB_CTRL_SET_MEDIA_INFO_T.
  - Defined ::IMTK_PB_CTRL_AUD_CODEC_INFO_T, and added it into most format infos in ::IMTK_PB_CTRL_SET_MEDIA_INFO_T.
  - Added IMtkPb_Ctrl_GenerateP0Header() for generating a @ref MTKP0page packet header.
  - Added IMtkPb_Ctrl_GenerateRVPicHdr() for generating RealVideo picture header.
- Removed IMtkPb_Sys.h
- @ref IMtkPb_Ctrl_Ext
  - Sequence diagrams for IMtkPb_Ctrl_Ext are added
    - @ref EncoderScenarios
    - @ref SnapshotTaking
  - The following data structures are added
    - ::IMTK_PB_CTRL_EXT_VIDEO_FRAME_SEG_T
    - ::IMTK_PB_CTRL_EXT_ENC_BUF_CB_VIDEO_INFO_T
    - ::IMTK_PB_CTRL_EXT_ENC_BUF_CB_AUDIO_INFO_T
    - ::IMTK_PB_CTRL_EXT_ENC_BUF_CB_INFO_T
  - A new buffer call back function type for encoder is added
    - ::IMtkPb_Ctrl_Ext_Enc_Buf_Cb_Fct
  - The call back function type set for IMtkPb_Ctrl_Ext_SetEncBufferParm() is changed from ::IMtkPb_Ctrl_Ext_Buf_Cb_Fct to ::IMtkPb_Ctrl_Ext_Enc_Buf_Cb_Fct.
  - New function to enable / disable encoder is added
  	- IMtkPb_Ctrl_Ext_EncEnable()

@section v012to013 0.12 to 0.13
- @ref IMtkPb_Ctrl
  - Updated ::IMTK_PB_CTRL_AUD_COOK_INFO_T.
  - Updated ::IMTK_PB_CTRL_AUD_WMA_INFO_T.  Also updated the WMA part of @ref MTKP0page.
  - Updated the @ref MTKP0page.  Now the endianness of <b>Start code</b>, <b>PTS</b>, and <b>Payload size</b> are well defined.
  - Limited the use of IMtkPb_Ctrl_GetCurrentPos() to be in Lib Master model only, and added IMtkPb_Ctrl_GetCurrentPTS() for App Master model.
  - Removed IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE.  App should call IMtkPb_Ctrl_GetCurrentPTS() or IMtkPb_Ctrl_GetCurrentPos() to get the current playback time.
  - Clarified that ::IMTK_PB_CTRL_EVENT_TOTAL_TIME_UPDATE is only used in the Lib Master model.
  - Clarified that the u4Time parameter in IMtkPb_Ctrl_Play() is only useful in Lib Master model and when the playback state is in the READY state.  In all other cases, u4Time is ignored by MtkPbLib.
- @ref IMtkPb_Ctrl_Ext
  - Add limitation: The function IMtkPb_Ctrl_Ext_EncPb() should be invoked before IMtkPb_Ctrl_SetMediaInfo().

@section v013to014 0.13 to 0.14
- @ref IMtkPb_Ctrl
  - Clarified that when the App calls IMtkPb_Ctrl_GetCurrentPTS(), if any PTS is unavailable, MtkPbLib should fill ::IMTK_INVALID_PTS to the output parameter.
  - Added u2CodecPrivInfoLen and au1CodecPrivInfo back into ::IMTK_PB_CTRL_ASF_VID_INFO_T and ::IMTK_PB_CTRL_MKV_VID_INFO_T.  They were mistakenly removed in v0.12.
  - Added IMtkPb_Ctrl_ClearVideo().
  - Added @ref SyncNotes explaining audio/video synchronization details and limitations.
  - Added @ref PlayFlagNotes explaining how to set ::IMTK_PB_CTRL_ENGINE_PARAM_T.u4PlayFlag in Lib Master and App Master models.
  - Added a note in @ref PushNotes clarifying that the App shouldn't call another IMtkPb_Ctrl_GetBuffer() when the previous one returns ::IMTK_PB_ERROR_CODE_GET_BUF_PENDING.
  - Added a note in the IMTK_PB_CTRL_AUD_ENC_COOK section of @ref MTKP0page explaining the data must be aligned to a frame.
  - Added IMtkPb_Ctrl_SetExtraInfo().
  - Removed IMTK_PB_CTRL_EVENT_BUFFER_FULL.
  - Changed ::IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW to be available also in both Lib Master and App Master model.
- @ref IMtkPb_Ctrl_Ext
  - The type of ::IMTK_PB_CTRL_EXT_VIDEO_FRAME_SEG_T is renamed as ::IMTK_PB_CTRL_EXT_VIDEO_AU_SEG_T.
  - IMtkPb_Ctrl_Ext_FreeEncCbBuffer() function is added to free the encoder buffer called back.

@section v014to015 0.14 to 0.15
- @ref IMtkPb_Ctrl
  - Added back ::IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE that was removed in v0.13.  Also added explanations in IMtkPb_Ctrl_GetCurrentPos() and in ::IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE to explain the usages.

@section v015to016 0.15 to 0.16
- @ref IMtkPb_Ctrl
  - Clarified that after IMtkPb_Ctrl_Stop(), the last picture remains on the screen.  The App could call IMtkPb_Ctrl_ClearVideo() to clear it.
  - Added Figure 1-4 in @ref AppMasterPull to explain "from Play to end of stream" in App Master + Pull model.
  - Added Figure 2-4 in @ref AppMasterPush to explain "from Play to end of stream" in App Master + Push model.
  - Added @ref PlayEndNotes to explain the behaviors of "play to end of stream".
  - Added ::IMTK_PB_CTRL_STEP_OK and ::IMTK_PB_CTRL_STEP_FAIL, which could be set to the u4Data of ::IMTK_PB_CTRL_EVENT_STEP_DONE.
  - Clarified that if the pu1URI member in ::IMTK_PB_CTRL_URI_INFO_T represents a local file, a "file://" prefix must exist in front of the absolute path which starts with '/'.
  - Changed the value of ::IMTK_NULL_HANDLE from 0 to ((uint32_t)-1).
  - Added IMtkPb_Ctrl_GetBufferStatus() to allow App to get various info about the playback buffer.
  - Added ::IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR to indicate various kinds of playback error.
  - Added ::IMTK_PB_CTRL_EVENT_EOF to indicate that MtkPbLib has buffered to the end of file.
  - Limited the support of ::IMTK_PB_CTRL_BUF_SIZE_TYPE_DURATION to certain conditions.
- @ref IMtkPb_Ctrl_Ext
  - Added chart "Encoder Initialize" in @ref EncoderScenarios.  Video resolution / video bit rate / video frame rate should be set before encoder is enabled.
- @ref IMtkPb_DRM
  - Added IMtkPb_DRM_AES_CTR() for AES counter mode.

@section v016to017 0.16 to 0.17
- @ref IMtkPb_Ctrl
  - Updated @ref SMpage explaining that IMtkPb_Ctrl_Close() could be called in any state, and the state machine should always change to the CLOSED state.
  - Added ::IMTK_PB_CTRL_EVENT_BUFFER_HIGH and ::IMTK_PB_CTRL_EVENT_BUFFER_LOW events.
  - Limited the event ::IMTK_PB_CTRL_EVENT_EOF to http and https resource types only.  The file resource type doesn't support it.
  - Limited the support of IMtkPb_Ctrl_GetBufferStatus() to http and https resource types only.  The file resource type doesn't support it.
  - Added ::IMTK_PB_CTRL_EVENT_PLAY_DONE, and changed the behavior of IMtkPb_Ctrl_Play() in the Lib Master model.  See the Note section of IMtkPb_Ctrl_Play() for details.
  - Changed the value of ::IMTK_PB_CTRL_STEP_FAIL from 0 to ((uint32_t)-1).
  - Added some Lib Master only media types to ::IMTK_PB_CTRL_MEDIA_TYPE_T.
  - Added ::IMTK_PB_CTRL_EXTRA_FLAG_LIMITED_SEEK.
- @ref IMtkPb_DRM
  - Added ::IMTK_PB_ERROR_CODE_DATA_CRYPTO_ERROR which should be in v0.16 together with IMtkPb_DRM_AES_CTR().

@section v017to018 0.17 to 0.18
- @ref IMtkPb_Ctrl
  - Changed the type of the StepAmount parameter in IMtkPb_Ctrl_Step() from int32_t to uint32_t.
  - Function call condition clarifications:
    - Clarified that IMtkPb_Ctrl_Step() can only be called in the PAUSED state.
    - Clarified that IMtkPb_Ctrl_PreLoadData() can only be called in the READY state.
    - Clarified that IMtkPb_Ctrl_Pause() can only be called in the PLAYING state.
    - Clarified that IMtkPb_Ctrl_TimeSeek() can also be called in the PAUSED state, in addition to the PLAYING state.
    - Clarified that IMtkPb_Ctrl_GetCurrentPos() and IMtkPb_Ctrl_GetCurrentPTS() are only available in PLAYING and PAUSED states.
  - Added uExtraFlagParam in ::IMTK_PB_CTRL_SET_EXTRA_INFO_T, now only for ::IMTK_PB_CTRL_EXTRA_FLAG_LIMITED_SEEK.  Also, changed the explanations for ::IMTK_PB_CTRL_EXTRA_FLAG_LIMITED_SEEK so it's clearer.
  - ::IMTK_PB_CTRL_SPEED_T changes:
    - Removed IMTK_PB_CTRL_SPEED_FR_1X
    - Renamed IMTK_PB_CTRL_SPEED_FF_1X to ::IMTK_PB_CTRL_SPEED_1X
    - Clarified that ::IMTK_PB_CTRL_SPEED_ZERO and ::IMTK_PB_CTRL_SPEED_1X are only for IMtkPb_Ctrl_GetPlayParam() and not for IMtkPb_Ctrl_SetSpeed().
  - Changed the u4Data parameters of ::IMTK_PB_CTRL_EVENT_PLAY_DONE and ::IMTK_PB_CTRL_EVENT_STEP_DONE to enumerations.
  - Added ::IMTK_PB_CTRL_ERROR_TYPE_T, as the u4Data parameter of ::IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR.
  - Added more usage explanations and clarifications to IMtkPb_Ctrl_Play().  See the Note section of IMtkPb_Ctrl_Play() for details.

@section v018to019 0.18 to 0.19
- @ref IMtkPb_Ctrl
  - Added ::IMTK_PB_CTRL_IBC_TYPE_SET_STILL_PICTURE and ::IMTK_PB_CTRL_IBC_TYPE_SET_DEC_RES in-band command types.
    - ::IMTK_PB_CTRL_IBC_TYPE_SET_STILL_PICTURE is to enable or disable the still picture video decoding mode.  Once the still picture video decoding mode
      is enabled, video display and video encoder will keep displaying / encoding the same frame until the next frame is available.
      Corresponding in-band command structure is ::IMTK_PB_CTRL_IBC_PARAM_SET_STILL_PICTURE_INFO.
    - ::IMTK_PB_CTRL_IBC_TYPE_SET_DEC_RES is to specify the decoder's output resolution.  Corresponding in-band command structure is ::IMTK_PB_CTRL_IBC_PARAM_SET_DEC_RES_INFO.
  - @ref MTKP0page changes:
    - ::IMTK_PB_CTRL_P0_PPR_TYPE is added to specify the <b>PPR</b> value in P0 private format.  <b>OPPP</b> field in P0 private format is renamed to <b>PPR</b> field.
    - ::IMTK_PB_CTRL_P0_PS_TYPE is added to specify the <b>PS</b> value in P0 private format.
    - In ::IMTK_PB_CTRL_P0_PKT_INFO_T, the field of fgOPPP is replaced by ePPR (in type of IMTK_PB_CTRL_P0_PPR_TYPE).  The field of ePS (in type of IMTK_PB_CTRL_P0_PS_TYPE) is also added.
    - ::IMTK_PB_CTRL_P0_PPR_INFO_T is added for retrieving the valid <b>PPR</b> value and <b>PS</b> segment size for each video codec.
    - Function of IMtkPb_Ctrl_GetP0PPRInfo() is added to retrieve <b>PPR</b> information for each video codec.
    - The flags in private format P0 is changed.  THe original field of <b>OPPP</b> is replaced by <b>PPR</b>, and new field of <b>PS</b> is created.
    - The valid <b>PPR</b> value for each video codec is also changed.
  - Added ::IMTK_PB_CTRL_VID_H264_INFO_T in ::IMTK_PB_CTRL_VID_CODEC_INFO_T to specify frame rate for H.264 video codec.  This affects all App Master media types that could contain H.264 video codec.
  - Added more detailed explanations to ::IMTK_PB_CTRL_FRAME_RATE_UNKNOWN and ::IMTK_PB_CTRL_FRAME_RATE_VARIABLE.
  - Added warning sections in IMtkPb_Ctrl_GetAudTrackInfo(), IMtkPb_Ctrl_GetCurAudTrackInfo(), IMtkPb_Ctrl_GetVidTrackInfo(), and IMtkPb_Ctrl_GetCurVidTrackInfo().
  - Added ::IMTK_PB_CTRL_ERROR_VIDEO_UNPLAYABLE to ::IMTK_PB_CTRL_ERROR_TYPE_T.
  - In IMtkPb_Ctrl_Play(), added Precondition and Postcondition sections and removed the Note section.
- @ref IMtkPb_Ctrl_Ext
  - The chart of "Taking Snapshot" is modified in @ref SnapshotTaking.  IMtkPb_Ctrl_Ext_StartSnapshot() can be invoked without pausing the playback.
  - The clipping rectangle info and requested raw data format can be specified in function IMtkPb_Ctrl_Ext_StartSnapshot().
  - ::IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_INFO_T is specified in IMtkPb_Ctrl_Ext_StopSnapshot() to release raw data buffer.
  - The function of IMtkPb_Ctrl_Ext_SetEncoderRectangle() is added to specify the encoding rectangle.
  - ::IMTK_PB_CTRL_EXT_SNAPSHOT_FMT_T is added to specify the requested snapshot raw data format.
  - ::IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_YUV_INFO_T is added and can be used if the requested snapshot raw data format is ::IMTK_PB_CTRL_EXT_SNAPSHOT_YUV420.
  - ::IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_INFO_T is added to retrieve raw data buffer info.
- @ref IMtkPb_Snd
  - ::IMTKPB_SND_CLIP_COND_CLIP_STOPPED is added in ::IMTKPB_SND_CLIP_COND_T.
  - Added a semantics section.
  - Added u4Data and u4ClipTag parameters in IMtkSnd_Ctrl_Nfy_Fct().
  - Added u4ClipTag parameter in IMtkPb_Snd_SendClip().

@section v019to020 0.19 to 0.20
- @ref IMtkPb_Ctrl
  - Changed the behavior of play to end of stream in the case of Lib Master.  MtkPbLib will not enter READY state automatically.  Figures 3-3 and 4-3 were updated.  See @ref PlayEndNotes for details.
  - Clarified that after IMtkPb_Ctrl_Step() and before the ::IMTK_PB_CTRL_EVENT_STEP_DONE, MtkPbLib ignores all IMtkPb function calls except for IMtkPb_Ctrl_Stop() and IMtkPb_Ctrl_Close().
  - Added IMtkPb_Ctrl_TimeSeekEx() to support directly entering a PAUSED state after a time seek.
  - Added ::IMTK_PB_CTRL_EVENT_TIMESEEK_DONE event for IMtkPb_Ctrl_TimeSeek() and IMtkPb_Ctrl_TimeSeekEx().
  - Added IMtkPb_Ctrl_PlayEx() to support directly entering a PAUSED state after play.
  - Updated Figures 3-1, 3-2, 3-3, 3-4, 4-1, 4-2, 4-3, and 4-4 to reflect recent interface changes, especially for the event notifications.

@section v020to021 0.20 to 0.21
- @ref IMtkPb_Ctrl
  - Added IMtkPb_Ctrl_AppendHttpReqHdrString().
- @ref IMtkPb_Ctrl_Ext
  - Added hHandle parameter for function IMtkPb_Ctrl_Ext_StopSnapshot().
  - Snapshot operational condition is changed.
- @ref IMtkPb_Snd
  - Added function IMtkPb_Snd_Pause() and IMtkPb_Snd_Resume().

@section v021to022 0.21 to 0.22
- @ref IMtkPb_Ctrl
  - Renamed IMtkPb_Ctrl_AppendHttpReqHdrString() to IMtkPb_Ctrl_CustomizeHttpReqHdr().
  - Modified ::IMTK_PB_CTRL_GET_MEDIA_INFO_T
    - Removed u2VideoTrackNum.
    - Added u4AvgBitrate.
  - Modified ::IMTK_PB_CTRL_GET_VID_TRACK_INFO_T
    - Removed IMTK_PB_CTRL_VID_FMT_T
    - Changed u4BitRate to u4InstBitRate, and changed the meaning to the instant bit rate during playback.
  - Modified IMtkPb_Ctrl_GetVidTrackInfo():
    - Removed the u4TrackNum parameter.
    - Removed the warning description.
    - Modified some explanations.
  - Removed IMtkPb_Ctrl_GetCurVidTrackInfo().
- @ref IMtkPb_Ctrl_Ext
  - Snapshot operational condition is changed.
- @ref IMtkPb_Snd
  - Change the type of second parameter IMtkPb_Snd_SetParameter() to pointer of ::IMTK_PB_CTRL_AUD_PCM_INFO_T.

@section v022to023 0.22 to 0.23
- @ref IMtkPb_Ctrl
  - Added ::IMTK_PB_CTRL_INFINITE_LEN and defined its use by IMtkPb_Ctrl_Pull_GetInputLen_Fct().
  - Added preconditions and notes to IMtkPb_Ctrl_GetMediaInfo().
  - Changed the value ranges of ::IMTK_PB_CTRL_RECT_T to [0:9999] and [1:10000].
- @ref IMtkPb_Ctrl_Ext
  - Added ::IMTK_PB_CTRL_EXT_SNAPSHOT_DIMENSION_T to return the width and height of snapshot taken
  - Added tDimInfo of type ::IMTK_PB_CTRL_EXT_SNAPSHOT_DIMENSION_T in ::IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_INFO_T
  - ::IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_YUV_INFO_T is modified.  Only 2 buffers are returned.

@section v023to024 0.23 to 0.24
- @ref IMtkPb_Ctrl
  - Clarified that IMtkPb_Ctrl_SetEngineParam()'s precondition is in the OPENED or the READY states.  The @ref SMpage is also updated.
  - Added @ref DisplayRectangleNotes to explain the usage of IMtkPb_Ctrl_SetDisplayRectangle().
- @ref IMtkPb_Snd
  - Added IMtkPb_Snd_Init() and IMtkPb_Snd_Terminate().
- @ref IMtkPb_Ctrl_Ext
  - Added u4WidthPitch in ::IMTK_PB_CTRL_EXT_SNAPSHOT_DIMENSION_T for width pitch used in underlying h/w
  - Added instructions about how to use Y/C buffer returned ::IMTK_PB_CTRL_EXT_SNAPSHOT_BUF_YUV_INFO_T

@section v024to025 0.24 to 0.25
- Added @ref IMtkPb_Misc
  - Added IMtkPb_Misc_Init(), IMtkPb_Misc_Terminate(), IMtkPb_Misc_SetAudioVolume(), IMtkPb_Misc_GetAudioVolume().
- @ref IMtkPb_Ctrl
  - Added ::IMTK_PB_CTRL_EXTRA_FLAG_DISP_ORDER_IS_DEC_ORDER that can be set by IMtkPb_Ctrl_SetExtraInfo().
  - Removed IMtkPb_Ctrl_PreLoadData().  Also removed the state IMTK_PB_CTRL_PRELOADING.  The @ref SMpage is also updated.
  - Added a note in IMtkPb_Ctrl_ChangeAudio() to clarify that video shall keep playing and is not affected by this function.
  - Added new function: IMtkPb_Ctrl_ChangeVideo().
  - Added more friendly explanations to some @ref MTKP0page related texts.

@section v025to026 0.25 to 0.26
- @ref IMtkPb_Misc
  - Clarified that the setting by IMtkPb_Misc_SetAudioVolume() is not stored in non-volatile memory and will be forgotten after power off.
- @ref IMtkPb_Ctrl
  - Added Vorbis, FLAC, and Monkey's Audio.
    - Added ::IMTK_PB_CTRL_AUD_ENC_VORBIS, ::IMTK_PB_CTRL_AUD_ENC_FLAC, and ::IMTK_PB_CTRL_AUD_ENC_MONKEY.
    - Added instructions on how to put these formats into the @ref MTKP0page.
  - Added Ogg support in App Master model.  Added ::IMTK_PB_CTRL_MEDIA_OGG_INFO_T in ::IMTK_PB_CTRL_SET_MEDIA_INFO_T, and added ::IMTK_PB_CTRL_OGG_AUD_INFO_T in ::IMTK_PB_CTRL_AUD_INFO_T.
  - Added IMtkPb_Ctrl_SetPlayMode(), adding I-mode support in App Master model.
  - Modified Figure 3-2.  Replaced IMtkPb_Ctrl_PreLoadData() with IMtkPb_Ctrl_PlayEx().
  - Added more explanations in each state of ::IMTK_PB_CTRL_STATE_T.
  - Added some conditions in some state transitions in @ref SMpage.
  - Added ::IMTK_PB_CTRL_SRC_ASPECT_RATIO_INFO_T in ::IMTK_PB_CTRL_MTK_P0_VID_INFO_T, allowing the App to specify its preferred display aspect ratio for the content when using the @ref MTKP0page.

@section v026to027 0.26 to 0.27
- @ref IMtkPb_Ctrl
  - Added ::IMTK_PB_CTRL_MEDIA_TYPE_OGG to @ref AppMasterNotes.
  - Refined App Master support for FLAC and Monkey's Audio.
    - Modified instructions on how to put these formats into the @ref MTKP0page.
    - Added ::IMTK_PB_CTRL_MEDIA_TYPE_FLAC and ::IMTK_PB_CTRL_MEDIA_TYPE_APE.
    - Added ::IMTK_PB_CTRL_MEDIA_FLAC_INFO_T and ::IMTK_PB_CTRL_MEDIA_APE_INFO_T in ::IMTK_PB_CTRL_SET_MEDIA_INFO_T.
    - Added ::IMTK_PB_CTRL_FLAC_AUD_INFO_T and ::IMTK_PB_CTRL_APE_AUD_INFO_T in ::IMTK_PB_CTRL_AUD_INFO_T.
    - Added them to @ref AppMasterNotes.
  - Clarified that when putting ::IMTK_PB_CTRL_AUD_ENC_COOK into the @ref MTKP0page, the audio frames must already be deinterleaved.
  - Clarified the pre-condition of IMtkPb_Ctrl_SetAudTrack().
  - Added ::IMTK_PB_CTRL_EXTRA_FLAG_3D_VIDEO so the App can specify 3D video contents by IMtkPb_Ctrl_SetExtraInfo().
  - Added two new audio codecs: ::IMTK_PB_CTRL_AUD_ENC_DTS_HD_HR and ::IMTK_PB_CTRL_AUD_ENC_DTS_HD_MA.
- @ref IMtkPb_Misc
  - Fixed a typo in ::IMTK_PB_MISC_AUD_CHANNEL_FRONT_RIGHT.

@section v027to028 0.27 to 0.28
- @ref IMtkPb_Ctrl
  - Refined the explanations on how to put ::IMTK_PB_CTRL_VID_ENC_WMV1, ::IMTK_PB_CTRL_VID_ENC_WMV2, ::IMTK_PB_CTRL_VID_ENC_WMV3, ::IMTK_PB_CTRL_VID_ENC_RV8, and ::IMTK_PB_CTRL_VID_ENC_RV9_10 into the @ref MTKP0page.
  - Codec clarifications and additions
    - Clarified that ::IMTK_PB_CTRL_AUD_ENC_WMA only means WMA Standard, not WMA Pro nor WMA Lossless.
    - Added wFormatTag in ::IMTK_PB_CTRL_AUD_WMA_INFO_T, however it must be set to 0x161 which means WMA Standard.
    - Added ::IMTK_PB_CTRL_AUD_ENC_TRUEHD for the Dolby TrueHD audio codec.
    - Clarified that ::IMTK_PB_CTRL_AUD_ENC_DD also supports Dolby Digital Plus
    - Clarified that ::IMTK_PB_CTRL_AUD_ENC_AAC also supports HE-AAC
  - Reserved space for ::IMTK_PB_CTRL_SET_MEDIA_INFO_T
    - Added ::IMTK_PB_CTRL_AUD_RESERVED_CODEC_INFO_T in ::IMTK_PB_CTRL_AUD_CODEC_INFO_T to reserve space.
    - Added ::IMTK_PB_CTRL_VID_RESERVED_CODEC_INFO_T in ::IMTK_PB_CTRL_VID_CODEC_INFO_T to reserve space.
    - Added ::IMTK_PB_CTRL_MEDIA_RSVT_INFO_T in ::IMTK_PB_CTRL_SET_MEDIA_INFO_T to reserve space.
  - Modified typo in ::IMTK_PB_CTRL_MPEG2_PS_AUD_INFO_T field tAudStrmIdInfo.
  - IMtkPb_Ctrl_SetExtraInfo() modifications
    - Modified the structure and usage of ::IMTK_PB_CTRL_SET_EXTRA_INFO_T.  If the App wants to set more than one extra info type, it shall call this function multiple times.
    - Reserved space in ::IMTK_PB_CTRL_SET_EXTRA_INFO_T.
- @ref IMtkPb_Misc
  - Added IMtkPb_Misc_GetUserPref() for user preference retrieval.

@section v028to029 0.28 to 0.29
- @ref IMtkPb_Ctrl
  - Added ::IMTK_PB_CTRL_EXTRA_INFO_TYPE_MMS.
  - Refined the explanations of ::IMTK_PB_CTRL_EVENT_EOF.
  - Added @ref LibMasterNotes.

@section v029to030 0.29 to 0.30
- @ref IMtkPb_Ctrl
  - Refined the definitions of IMtkPb_Ctrl_GetAudTrackInfo() and IMtkPb_Ctrl_GetCurAudTrackInfo().  Removed the waring in IMtkPb_Ctrl_GetAudTrackInfo().
  - Clarified that ::IMTK_PB_CTRL_AUD_COOK_INFO_T.eNumCh must be ::IMTK_PB_CTRL_AUD_CH_MONO or ::IMTK_PB_CTRL_AUD_CH_STEREO.
  - Clarified that when putting ::IMTK_PB_CTRL_AUD_ENC_COOK into the @ref MTKP0page, the audio data must already be descrambled.
  - Added ::IMTK_PB_CTRL_AUD_ENC_DDPLUS, and clarified that ::IMTK_PB_CTRL_AUD_ENC_DD is for Dolby Digital only.
  - Added ::IMTK_PB_CTRL_EVENT_BOS.
  - Added a precondition use case to IMtkPb_Ctrl_Play().
  - Clarified that IMtkPb_Ctrl_SetSpeed() shouldn't set ::IMTK_PB_CTRL_SPEED_ZERO nor ::IMTK_PB_CTRL_SPEED_1X.

@section v030to031 0.30 to 0.31
- @ref IMtkPb_Ctrl
  - Added IMtkPb_Ctrl_HttpsAuthenticateServer() and IMtkPb_Ctrl_HttpsAuthenticateClient().
  - Removed the warning in IMtkPb_Ctrl_GetCurAudTrackInfo(), and changed its pre-condition.
- @ref IMtkPb_Ctrl_Ext
  - Added ::IMTK_PB_CTRL_EXT_SNAPSHOT_SIZE_T to specify requested snapshot width and height.
  - Added ptSize in IMtkPb_Ctrl_Ext_StartSnapshot()

@section v031to032 0.31 to 0.32
- @ref IMtkPb_Ctrl
  - Added explanations and clarifications to the usage of IMtkPb_Ctrl_HttpsAuthenticateServer() and IMtkPb_Ctrl_HttpsAuthenticateClient().
  - Modified variable names in ::IMTK_PB_CTRL_MEDIA_OGG_INFO_T.
  - Added ::IMTK_PB_CTRL_MEDIA_TYPE_RM support to the App Master model.
  - Added two new error types: ::IMTK_PB_CTRL_ERROR_UNSUPPORTED_DRM and ::IMTK_PB_CTRL_ERROR_SERVER_DISCONNECTED.

@section v032to033 0.32 to 0.33
- @ref IMtkPb_Ctrl
  - Added warning in IMtkPb_Ctrl_ChangeAudio() that changing audio in ::IMTK_PB_CTRL_MEDIA_TYPE_AVI is not supported.
  - Added external subtitle support
    - Added IMtkPb_Ctrl_AttachExtSt(), IMtkPb_Ctrl_SetExtStTrack(), and IMtkPb_Ctrl_GetAttachedExtSt().
    - Some clarification in IMtkPb_Ctrl_Close() to take external subtitle into consideration.
- Added @ref IMtkPb_ExtSt for external subtitle support.
- Added @ref IMtkPb_Comm.h
- @ref IMtkPb_Misc
  - Modified ::IMTK_PB_MISC_USER_PREF_VALUE_T to use ::IMTK_PB_COMM_LANG_INFO_T as the structure of osd language info.
- @ref IMtkPb_ErrorCode.h
  - Added some external subtitle specific error codes and handle types.

@page AppMasterPull Sequence diagrams: App Master + Pull
@section OpenToPlay Open to Play
@image html AppMaster_Pull_Open_to_Play.png "Figure 1-1 From Open to Play in App Master + Pull mode"
@section Buffering Buffering before play
@image html AppMaster_Pull_Buffering_Before_Play.png "Figure 1-2 Buffering before play in App Master + Pull mode"
@section Audio Audio Change after play
@image html AppMaster_Audio_Change.png "Figure 1-3 Audio Change in App Master + Pull mode"
@section from Play to end of stream
@image html AppMaster_Pull_Play_to_EndOfStream.png "Figure 1-4 from Play to end of stream"

@page AppMasterPush Sequence diagrams: App Master + Push
@section OpenToPlay Open to Play
@image html AppMaster_Push_Open_to_Play.png "Figure 2-1 From Open to Play in App Master + Push mode"
@section Buffering Buffering before play
@image html AppMaster_Push_Buffering_Before_Play.png "Figure 2-2 Buffering before play in App Master + Push mode"
@section Audio Audio Change after play
@image html AppMaster_Audio_Change.png "Figure 2-3 Audio Change in App Master + Push mode"
@section from Play to end of stream
@image html AppMaster_Push_Play_to_EndOfStream.png "Figure 2-4 from Play to end of stream"

@page LibMasterURI Sequence diagrams: Lib Master + URI
@section OpenToPlay Open to Play
@image html LibMaster_URI_Open_to_Play.png "Figure 3-1 From Open to Play in Lib Master + URI mode"
@section Buffering Buffering before play
@image html LibMaster_URI_Buffering_Before_Play.png "Figure 3-2 Buffering before play in Lib Master + URI mode"
@section PlayToClose Play to Close
@image html LibMaster_Play_Stop.png "Figure 3-3 From Play to Close in Lib Master mode"
@section Audio Audio Change after play
@image html LibMaster_Audio_Change.png "Figure 3-4 Audio Change in Lib Master + URI mode"

@page LibMasterPull Sequence diagrams: Lib Master + Pull
@section OpenToPlay Open to Play
@image html LibMaster_Pull_Open_to_Play.png "Figure 4-1 Lib Master/Pull: From Open to Play"
@section Buffering Buffering before play
@image html LibMaster_Pull_Buffering_Before_Play.png "Figure 4-2 Buffering before play in Lib Master + Pull mode"
@section PlayToClose Play to Close
@image html LibMaster_Play_Stop.png "Figure 4-3 From Play to Close in Lib Master mode"
@section Audio Audio Change after play
@image html LibMaster_Audio_Change.png "Figure 4-4 Audio Change in Lib Master + Pull mode"

@page EncoderScenarios Sequence diagrams: Encoder Scenarios
@section ConnectToPlayback Connect to Playback
@image html Enc_Connect_Encoder_to_Playback.png "Figure 5-1 Connect encoder to playback"
@section EncoderInitialize Encoder Initialize
@image html Enc_Encoder_Initialize.png "Figure 5-2 Initialize encoder"
@section EncoderOperations Encoder Operations
@image html Enc_Encoder_Operations.png "Figure 5-3 Encoder operations"
@section RequestKeyFrame Request key frame
@image html Enc_Request_Key_Frame.png "Figure 5-4 Request key frame"
@section SetEncoderBuffer Set encoder buffer
@image html Enc_Set_Encoder_Buffer.png "Figure 5-5 Set encoder buffer"

@page SnapshotTaking Sequence diagrams: Taking Snapshot
@section TakingSnapshot Taking Snapshot
@image html Snapshot_Take_Snapshot.png "Figure 6-1 Take snapshot"

@page SMpage Playback State Machine

@section StateMachineS State Machine Diagram
@image html State_Machine.png "Figure (5-1) Playback State Machine "

@page MTKP0page MTK Private Format P0

@section MTKP0 MTK Private Format P0
The MtkPbLib supports a format called MTK Private Format P0.  It is only supported in the App Master operating model (not in Lib Master model).
App could set IMtkPb_Ctrl_SetMediaInfo()'s eMediaType to ::IMTK_PB_CTRL_MEDIA_TYPE_MTK_P0 to tell MtkPbLib that
App will feed such format to MtkPbLib.

@image html MTK_Private_Format_P0.png "Figure (6-1) MTK Private Format P0 "

In this format, the data is divided into individual packets.  For each packet, there's a 18-bytes header followed by
a payload, which may contain audio or video elementary streams.  The detail of the format is depicted in Figure 6-1.
The meanings of each field in the packet header are:
- <b>Start Code</b>: This field occupies bytes 0 to 3, and the value is 0x3ED1A7E4, while byte 0 == 0x3E, and byte 4 == 0xE4.
- <b>PTS</b>: This field occupies bytes 4 to 11.  Byte 11 is most significant and byte 4 is least significant.  This is the presentation time stamp of the first access unit starting from inside this packet, in the unit of 90kHz.
For a valid PTS value, only the least significant 33 bits are relevant, and bits 33~63 must be filled with 0.  A value of ::IMTK_INVALID_PTS means that no timestamp is assigned to this packet.
- <b>Payload size</b>: This field occupies bytes 12 to 15.  Byte 15 is most significant and byte 12 is least significant.  This is the  size of the payload field, not including the FLAGS and the Reserved fields.
- <b>FLAGS</b>: This field occupies byte 16.  See Figure 6-1 and next paragraph for details.
- <b>Reserved</b>: This field cocupies byte 17.  It's reserved for future use and must be filled with 0.
- <b>Payload</b>: Starting from byte 18, it contains the real elementary stream.  Detailed formats are explained below.

Bites 0~2 of the <b>FLAGS</b> field is <b>Stream Type</b>, indicating the stream type of this packet.

When <b>Stream Type</b> is Video, the App must also specify the <b>PPR</b> field:
- When <b>PPR</b> == ::IMTK_PB_CTRL_P0_PPR_TYPE_FREE, the payload of this packet doesn't need to align with the picture boundary, and could contain any number of pictures or a partial picture.
- When <b>PPR</b> == ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP, the payload of this packet should contain exactly one picture.
- When <b>PPR</b> == ::IMTK_PB_CTRL_P0_PPR_TYPE_SEGMENT, the payload of this packet should contain one segment of one picture.

<b>PPR</b> stands for Picture Packet Relationship.
<b>OPPP</b> stands for One Picture Per Packet.

<b>PS</b> stands for Picture Segment, which is only meaningful when <b>PPR</b> == ::IMTK_PB_CTRL_P0_PPR_TYPE_SEGMENT.  A picture can be divided into segments only if the underlying decoder
supports partial decoding of one picture.  <b>PS</b> value of each segment can be:
- <b>PS</b> == ::IMTK_PB_CTRL_P0_PS_TYPE_FIRST, the segment is the first picture segment
- <b>PS</b> == ::IMTK_PB_CTRL_P0_PS_TYPE_INTERMEDIATE, the segment is the intermediate picture segment
- <b>PS</b> == ::IMTK_PB_CTRL_P0_PS_TYPE_LAST, the segment is the last picture segment

The possible <b>PPR</b> value for each video bitstream type and the recommended segment size (if <b>PPR</b> == ::IMTK_PB_CTRL_P0_PPR_TYPE_SEGMENT) can be retrieved from function IMtkPb_Ctrl_GetP0PPRInfo().

The following is a summary of how to put video bitstream into this format:
- ::IMTK_PB_CTRL_VID_ENC_MPEG1_2
  - PPR could be ::IMTK_PB_CTRL_P0_PPR_TYPE_FREE or ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP.
  - When the playback state moves from READY to PLAYING, a <b>sequence header</b> must exist in front of any other pictures.
  - No additional info is required in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_VID_ENC_MPEG4
  - PPR could be ::IMTK_PB_CTRL_P0_PPR_TYPE_FREE or ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP.
  - When the playback state moves from READY to PLAYING, a <b>VideoObjectLayer</b> must exist in front of any other pictures.
  - No addition info is required in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_VID_ENC_H264
  - The bitstream must be in the Byte stream format.
  - PPR could be ::IMTK_PB_CTRL_P0_PPR_TYPE_FREE or ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP.
  - When the playback state moves from READY to PLAYING, a <b>sequence parameter set</b> and a <b>picture parameter set</b> must exist in front of any other pictures.
  - The App must set ::IMTK_PB_CTRL_VID_H264_INFO_T in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_VID_ENC_H263
  - PPR could be ::IMTK_PB_CTRL_P0_PPR_TYPE_FREE or ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP.
  - No addition info is required in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_VID_ENC_VC1
  - PPR could be ::IMTK_PB_CTRL_P0_PPR_TYPE_FREE or ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP.
  - When the playback state moves from READY to PLAYING, a <b>SEQUENCE LAYER</b> must exist in front of any other pictures.
  - No addition info is required in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_VID_ENC_WMV1
  - PPR must be ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP.
  - The App must set ::IMTK_PB_CTRL_VID_WMV1_INFO_T in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_VID_ENC_WMV2
  - PPR must be ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP.
  - When the playback state moves from READY to PLAYING, a <b>SEQUENCE LAYER (from FRAMERATE to SLICECODE, 4 bytes)</b> must exist in front of the first picture, and in the same packet with the first picture.  All subsequent packets may not contain the <b>SEQUENCE LAYER</b>.
  - The App must set ::IMTK_PB_CTRL_VID_WMV2_INFO_T in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_VID_ENC_WMV3
  - PPR must be ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP.
  - When the playback state moves from READY to PLAYING, a <b>SEQUENCE LAYER (4 bytes)</b> must exist in front of the first picture, and in the same packet with the first picture.  All subsequent packets may not contain the <b>SEQUENCE LAYER</b>.
  - The App must set ::IMTK_PB_CTRL_VID_WMV3_INFO_T in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_VID_ENC_DIVX311
  - PPR must be ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP.
  - The App must set ::IMTK_PB_CTRL_VID_DX3_INFO_T in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_VID_ENC_RV8
  - PPR must be ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP.
  - The App must set ::IMTK_PB_CTRL_VID_RV8_INFO_T in IMtkPb_Ctrl_SetMediaInfo().
  - For each packet, a header generated by calling IMtkPb_Ctrl_GenerateRVPicHdr() must exist in front of every picture, and in the same packet with the corresponding picture data.
- ::IMTK_PB_CTRL_VID_ENC_RV9_10
  - PPR must be ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP.
  - The App must set ::IMTK_PB_CTRL_VID_RV9_10_INFO_T in IMtkPb_Ctrl_SetMediaInfo().
  - For each packet, a header generated by calling IMtkPb_Ctrl_GenerateRVPicHdr() must exist in front of every picture, and in the same packet with the corresponding picture data.
- ::IMTK_PB_CTRL_VID_ENC_MJPEG
  - PPR could be ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP or ::IMTK_PB_CTRL_P0_PPR_TYPE_SEGMENT.
  - When PPR == ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP, each packet contains an individual JPEG picture.
  - When PPR == ::IMTK_PB_CTRL_P0_PPR_TYPE_SEGMENT, each packet contains a segment of a JPEG picture, and PS should be correctly specified.
  - The App must set ::IMTK_PB_CTRL_VID_MJPEG_INFO_T in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_VID_ENC_SORENSON_SPARK
  - PPR could be ::IMTK_PB_CTRL_P0_PPR_TYPE_FREE or ::IMTK_PB_CTRL_P0_PPR_TYPE_OPPP.
  - No addition info is required in IMtkPb_Ctrl_SetMediaInfo().

The following is a summary of how to put audio bitstream into this format:
- ::IMTK_PB_CTRL_AUD_ENC_MPEG
  - No special consideration required.  Just put the elementary stream into the packet with no limitation.
  - No addition info is required in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_AUD_ENC_MP3
  - No special consideration required.  Just put the elementary stream into the packet with no limitation.
  - No addition info is required in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_AUD_ENC_AAC
  - The AAC bitstream must have the ADTS header.
  - No addition info is required in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_AUD_ENC_DD
  - No special consideration required.  Just put the elementary stream into the packet with no limitation.
  - No addition info is required in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_AUD_ENC_TRUEHD
  - No special consideration required.  Just put the elementary stream into the packet with no limitation.
  - No addition info is required in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_AUD_ENC_PCM
  - When the playback state moves from READY to PLAYING, the bitstream must start with the boundary of a sample.
  - The App must set ::IMTK_PB_CTRL_AUD_PCM_INFO_T in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_AUD_ENC_DTS
  - No special consideration required.  Just put the elementary stream into the packet with no limitation.
  - No addition info is required in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_AUD_ENC_DTS_HD_HR
  - No special consideration required.  Just put the elementary stream into the packet with no limitation.
  - No addition info is required in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_AUD_ENC_DTS_HD_MA
  - No special consideration required.  Just put the elementary stream into the packet with no limitation.
  - No addition info is required in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_AUD_ENC_WMA
  - When the playback state moves from READY to PLAYING, the bitstream must start with the boundary of a full WMA packet.
  - The App must set ::IMTK_PB_CTRL_AUD_WMA_INFO_T in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_AUD_ENC_COOK
  - When the playback state moves from READY to PLAYING, the bitstream must start with the boundary of a frame.
  - The audio frames must already be deinterleaved.
  - The audio data must already be descrambled.
  - The App must set ::IMTK_PB_CTRL_AUD_COOK_INFO_T in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_AUD_ENC_VORBIS
  - The bitstream encapsulated inside the Private Format must also contain the ogg container, instead of only the vorbis elementary bitstream.
  - When the playback state moves from READY to PLAYING, the bitstream must start with pages containing the identification header, comment header, and setup header.
  - No addition info is required in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_AUD_ENC_FLAC
  - No special consideration required.  Just put the elementary stream into the packet with no limitation.
  - The App must set ::IMTK_PB_CTRL_AUD_FLAC_INFO_T in IMtkPb_Ctrl_SetMediaInfo().
- ::IMTK_PB_CTRL_AUD_ENC_MONKEY
  - When the playback state moves from READY to PLAYING, the bitstream must start with the boundary of a frame.
  - The App must set ::IMTK_PB_CTRL_AUD_MONKEY_INFO_T in IMtkPb_Ctrl_SetMediaInfo().

@page DummyPage x
@image html MediaTekConfidential.png


*/
