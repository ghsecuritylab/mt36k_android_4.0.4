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

    IMtkPb_Snd defines the functionality of PCM playback, including mainly the following functions:

    -# Enumerate PCM sound device count.
    -# Get capabilities(sampling rate) of PCM sound device.
    -# Set parameters(sampling rate, channel ..) of PCM sound device.
    -# Start/Stop/Pause/Resume playing PCM clip.

  @section Semantics Semantics

    Function / callback semantics of IMtkPb_Snd are worthy of further explanation as follows:

    -# IMtkPb_Snd_Count(): Acquire the number of available PCM sound devices.
    -# IMtkPb_Snd_Query_Capability(): Query feasible sampling rate for each PCM sound device.
    -# IMtkPb_Snd_Open(): Open one of the available devices and have its handle.
    -# IMtkPb_Snd_SetParameter(): Set PCM sound sampling rate to selected device before starting PCM sound playback.
    -# IMtkPb_Snd_RegCallback(): Register callback for a specific device.
    -# IMtkPb_Snd_SendClip(): Sending a PCM sound clip to device.  The clip will be copied inside IMtkPb_Snd, so App may release the clip memory right after the function returns.  Moreover, clips sent will be queued in IMtkPb_Snd before played.
    -# IMtkPb_Snd_Play(): Switch the device to playing state.  All queued clips for the device will be played automatically.
    -# IMtkPb_Snd_Pause(): Switch the device to paused state.  Currently played clips will be paused.
    -# IMtkPb_Snd_Resume(): Switch the device to playing state.  Currently paused clips will be played again.
    -# IMtkPb_Snd_Stop(): Switch the device to stopped state.  Currently played clip will be stopped and queued clips will be flushed.
    -# IMtkPb_Snd_Close(): Close opened device.
*/


/*----------------------------------------------------------------------------*/
/*! @addtogroup IMtkPb_Snd
 *  @{
 */
/*----------------------------------------------------------------------------*/


#ifndef _I_MTK_PB_SND_
#define _I_MTK_PB_SND_

#ifdef __cplusplus
extern "C" {
#endif

/*-----------------------------------------------------------------------------
                    include files
-----------------------------------------------------------------------------*/
#include "IMtkPb_Ctrl.h"

/*-----------------------------------------------------------------------------
                    macros, defines, typedefs, enums
-----------------------------------------------------------------------------*/
/*! @struct IMTKPB_SND_CLIP_COND_T
 *  @brief  Sound clip playback condition
 *
 */
typedef enum
{
    IMTKPB_SND_CLIP_COND_ERROR = -1,		///< PCM sound clip is stopped abnormally.  Clip-based callback condition.  Value of u4Data in IMtkSnd_Ctrl_Nfy_Fct() for this condition is undefined.
    IMTKPB_SND_CLIP_COND_CLIP_DONE,		///< PCM sound clip has been played.  Clip-based callback condition.  Value of u4Data in IMtkSnd_Ctrl_Nfy_Fct() for this condition is undefined.
    IMTKPB_SND_CLIP_COND_CLIP_STOPPED,		///< PCM sound clip is stopped or flushed by IMtkPb_Snd_Stop().  Clip-based callback condition.  Value of u4Data in IMtkSnd_Ctrl_Nfy_Fct() for this condition is undefined.
    IMTKPB_SND_CLIP_COND_LOOP_DONE
} IMTKPB_SND_CLIP_COND_T;


/*! @struct IMTK_SND_FILE_PARM_T 
 *  @brief  Parameter for play sound clip of file type
 */ 
typedef struct
{   
    uint8_t                u1RptCnt;           ///< repeate count for play
    uint8_t                au1Reverved[3];     ///< reserved
} IMTK_SND_FILE_PARM_T;


/*! @brief          Function pointer of sound callback
 *  @param [in]     hHandle         the sound control handle
 *  @param [in]     eSndCond        notified condition.
 *  @param [in]     pvTag           the pvTag set in IMtkPb_Snd_RegCallback()
 *  @param [in]     u4Data          The data to notify for eSndCond.
 *  @param [in]     u4ClipTag       The data set in IMtkPb_Snd_SendClip() to distinguish individual clips.  If eSndCond is not clip-based callback condition, the value is undefined.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @note           Application should register a function into sound control engine for notify
 *  @see
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkSnd_Ctrl_Nfy_Fct)(IMTK_PB_HANDLE_T            hHandle,
                                                        IMTKPB_SND_CLIP_COND_T      eSndCond,
                                                        void*                       pvTag,
                                                        uint32_t                    u4Data,
                                                        uint32_t                    u4ClipTag);
/*-----------------------------------------------------------------------------
                    functions declarations
-----------------------------------------------------------------------------*/

/*!
 * @name Init and terminate
 * @{
 */


/*! @brief          Initializes IMtkPb_Snd
 *  @return         indicate success
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_SYS_LIB_ERROR            unexpected error in system call or standard library
 *  @retval         IMTK_PB_ERROR_CODE_FILE_NOT_FOUND           target kernel module does not exist at system.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      this size of memory does not be allocated.
 *  @retval         IMTK_PB_ERROR_CODE_FILE_READ_ERROR          target device can not be loaded.
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                target device has internal error when target device open.
 *  @note           Before a process starts using IMtkPb_Snd, it should first call this function.
 *  @see            IMtkPb_Snd_Terminate()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_Init(void);


/*! @brief          Terminates IMtkPb_Snd
 *  @return         indicate success
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_SYS_LIB_ERROR            unexpected error in system call or standard library
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                target device has internal error after target device load.
 *  @note           When a process finishes using IMtkPb_Snd, it should call this function.
 *  @see            IMtkPb_Snd_Init()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_Terminate(void);

/*!
 * @}
 * @name General
 * @{
 */

/*! @brief          Get sound(PCM) control count.
 *  @param [out]    pu1Count                count of sound(PCM) control instance
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_Count (uint8_t*      pu1Count);

/*! @brief          Query sound(PCM) control capability --- sample rate.
 *  @param [in]     u1SndIdx                the index of sound control
 *  @param [out]    au4SampleRate           array of supported sample rate
 *  @param [out]    pu1Count                count of supported sample rate
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_Query_Capability (uint8_t        u1SndIdx,
                                                         uint32_t*      au4SampleRate,
                                                         uint8_t*       pu1Count);

/*! @brief          Open sound(PCM) control.
 *  @param [out]    phHandle                The handle of sound(PCM) control instance
 *  @param [in]     u1SndIdx                the index of sound control
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                Driver error.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_Open (IMTK_PB_HANDLE_T*      phHandle,
                                             uint8_t                u1SndIdx);

/*! @brief          Set the PCM setting.
 *  @param [in]     hHandle                 The handle of sound(PCM) control instance
 *  @param [in]     ptPcmInfo               the PCM setting
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                Driver error.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_SetParameter (IMTK_PB_HANDLE_T               hHandle,
                                                     IMTK_PB_CTRL_AUD_PCM_INFO_T*   ptPcmInfo);

/*! @brief          Register the callback function of sound control.
 *  @param [in]     hHandle                 The handle of sound(PCM) control instance
 *  @param [in]     pfnCallback             The callback function
 *  @param [in]     pvTag                   The tag to be passed back by pfnCallback
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                Driver error.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_RegCallback (IMTK_PB_HANDLE_T        hHandle,
                                                    IMtkSnd_Ctrl_Nfy_Fct    pfnCallback,
                                                    void*                   pvTag);

/*! @brief          Get Clip buffer to store data.
 *  @param [in]     hHandle                 The handle of sound(PCM) control instance
 *  @param [in]     au1SndBuf               The buffer address to be stored
 *  @param [in]     u4SndSize               The buffer size to be stored
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_GET_BUF_PENDING          No buffer available, should wait.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_GetClipBuf( IMTK_PB_HANDLE_T  hHandle,
                                                   uint8_t**         ppau1SndBuf,
                                                   uint32_t*         pu4BufSize);


/*! @brief          Send the PCM file to play.
 *  @param [in]     hHandle                 The handle of sound(PCM) control instance
 *  @param [in]     szFilePath               The PCM file path
 *  @param [in]     prParm                  The Parameter for playing
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                Driver error.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_SendPCM_From_URL (IMTK_PB_HANDLE_T   hHandle,
                                                                     char*           szFilePath,
                                                                     IMTK_SND_FILE_PARM_T*    prParm);

/*! @brief          Send the PCM data to play.
 *  @param [in]     hHandle                 The handle of sound(PCM) control instance
 *  @param [in]     au1SndBuf               The PCM data
 *  @param [in]     u4SndSize               The PCM data size in bytes
 *  @param [in]     u4ClipTag               The data to distinguish individual clips.  It will be passed back by pfnCallback in IMtkPb_Snd_RegCallback().  App is responsible to generate unique data for all clips.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                Driver error.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_SendClip (IMTK_PB_HANDLE_T   hHandle,
                                                 uint8_t*           au1SndBuf,
                                                 uint32_t           u4SndSize,
                                                 uint32_t           u4ClipTag);

/*! @brief          Start to play PCM data from a device.
 *  @param [in]     hHandle                 The handle of sound(PCM) control instance
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                Driver error.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_Play (IMTK_PB_HANDLE_T   hHandle);

/*! @brief          Pause playing PCM data from a device.
 *  @param [in]     hHandle                 The handle of sound(PCM) control instance
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                Driver error.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_Pause(IMTK_PB_HANDLE_T   hHandle);

/*! @brief          Resume playing PCM data from a device.
 *  @param [in]     hHandle                 The handle of sound(PCM) control instance
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                Driver error.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_Resume(IMTK_PB_HANDLE_T   hHandle);

/*! @brief          Stop playing PCM data from a device.
 *  @param [in]     hHandle                 The handle of sound(PCM) control instance
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                Driver error.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_Stop (IMTK_PB_HANDLE_T   hHandle);

/*! @brief          Close the sound(PCM) control instance.
 *  @param [in]     hHandle                 The handle of sound(PCM) control instance
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                Driver error.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note
 *  @see
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Snd_Close (IMTK_PB_HANDLE_T      hHandle);

/*!
 * @}
 */


/*! @} */

#ifdef __cplusplus
}
#endif

#endif /* _I_MTK_PB_SND_ */
