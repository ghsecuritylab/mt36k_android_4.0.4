/*******************************************************************************
 * LEGAL DISCLAIMER
 *
 * (Header of MediaTek Software/Firmware Release or Documentation)
 *
 * BY OPENING OR USING THIS FILE, BUYER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND
 * AGREES THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK
 * SOFTWARE") RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO
 * BUYER ON AN "AS-IS" BASISONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH
 * RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 * INCORPORATED IN, OR SUPPLIED WITH THE MEDIATEK SOFTWARE, AND BUYER AGREES TO
 * LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 * MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE RELEASES
 * MADE TO BUYER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR STANDARD OR OPEN
 * FORUM.
 *
 * BUYER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND CUMULATIVE
 * LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY BUYER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * THE TRANSACTION CONTEMPLATED HEREUNDER SHALL BE CONSTRUED IN ACCORDANCE WITH
 * THE LAWS OF THE STATE OF CALIFORNIA, USA, EXCLUDING ITS CONFLICT OF LAWS
 * PRINCIPLES.
 ******************************************************************************/
/*------------------------------------------------------------------------------
 * Copyright (c) 2010, Mediatek Inc.
 * All rights reserved.
 *
 * Unauthorized use, practice, perform, copy, distribution, reproduction,
 * or disclosure of this information in whole or in part is prohibited.
 *----------------------------------------------------------------------------*/

/**

    IMtkPb_ExtSt defines some functions for the external subtitle feature.  The external subtitle
    is used by IMtkPb_Ctrl during the Lib Master model.  There are no IMtkPb_ExtSt_Init() nor IMtkPb_ExtSt_Terminate()
    because IMtkPb_ExtSt shall reuse the init and terminate of @ref IMtkPb_Ctrl.

    IMtkPb_ExtSt defines functions related to the external subtitle itself.  How to play it is defined by @ref IMtkPb_Ctrl.

*/

/*----------------------------------------------------------------------------*/
/*! @addtogroup IMtkPb_ExtSt
 *  @{
 */
/*----------------------------------------------------------------------------*/


#ifndef _I_MTK_PB_EXTST_H_
#define _I_MTK_PB_EXTST_H_

#ifdef  __cplusplus
extern "C" {
#endif

/*-----------------------------------------------------------------------------
                    include files
-----------------------------------------------------------------------------*/
#include "IMtkPb_ErrorCode.h"
#include "IMtkPb_Comm.h"

/*-----------------------------------------------------------------------------
                    macros, defines, typedefs, enums
-----------------------------------------------------------------------------*/


/*! @enum   IMTK_PB_EXTST_BUFFERING_MODEL_T
 *  @brief  Buffering Model
 */
typedef enum
{
    IMTK_PB_EXTST_BUFFERING_MODEL_URI    =   1,  ///< URI buffering model
} IMTK_PB_EXTST_BUFFERING_MODEL_T;


/*! @struct   IMTK_PB_EXTST_URI_PARAM_T
 *  @brief  URI model parameters
 */
typedef struct
{
    uint32_t    u4URI_len;                          ///< Length of URI string
    uint8_t*    pu1URI;                             ///< Pointer of URI string.  The resource type could be http, https, or file.  For local files, a "file://" prefix must exist in front of the absolute path which starts with '/'.
} IMTK_PB_EXTST_URI_PARAM_T;


/*! @union   IMTK_PB_EXTST_BUFFERING_PARAM_T
 *  @brief   Buffering model parameters
 */
typedef union
{
    IMTK_PB_EXTST_URI_PARAM_T tURIParam;        ///< URI model parameters
} IMTK_PB_EXTST_BUFFERING_PARAM_T;


/*! @struct IMTK_PB_EXTST_GET_ST_INFO_T
 *  @brief  subtitle info structure
 */
typedef struct
{
    uint16_t                        u2StTrackNum;        ///< number of subtitle tracks
} IMTK_PB_EXTST_GET_ST_INFO_T;


/*! @struct IMTK_PB_EXTST_GET_ST_TRACK_INFO_T
 *  @brief  subtitle track Info structure
 */
typedef struct
{
    IMTK_PB_COMM_LANG_INFO_T        tLangInfo;          ///< language of this subtitle track
} IMTK_PB_EXTST_GET_ST_TRACK_INFO_T;


/*-----------------------------------------------------------------------------
                    functions declarations
-----------------------------------------------------------------------------*/


/*!
 * @name General
 * @{
 */


/*! @brief          opens an external subtitle instance
 *  @param [out]    phExtStHandle           Reference to the handle of external subtitle
 *  @param [in]     eBufferingModel         buffering model
 *  @param [in]     ptBufModelParam         pointer to the buffering model parameter union
 *  @return         success or error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @see            IMtkPb_ExtSt_Close()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_ExtSt_Open(IMTK_EXTST_HANDLE_T *phExtStHandle,
                                              IMTK_PB_EXTST_BUFFERING_MODEL_T eBufferingModel,
                                              IMTK_PB_EXTST_BUFFERING_PARAM_T* ptBufModelParam);


/*! @brief          closes an external subtitle instance
 *  @param [in]     hExtStHandle           a handle of external subtitle instance
 *  @return         success or error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_STILL_ATTACHED           The external subtitle handle is still attached with a playback instance and cannot be closed
 *  @note           If this hExtStHandle is still attached with a playback instance, this function will return ::IMTK_PB_ERROR_CODE_STILL_ATTACHED.
 *  @see            IMtkPb_ExtSt_Open()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_ExtSt_Close(IMTK_EXTST_HANDLE_T hExtStHandle);


/*! @brief          Parse an external subtitle instance
 *  @param [in]     hExtStHandle           a handle of external subtitle instance
 *  @return         success or error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @note           Parse the contents of an external subtitle instance.  Only after an instance is parsed can the App attach it to a playback instance, or get any information from it.
 *  @note           This function is synchronous but may take some time.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_ExtSt_Parse(IMTK_EXTST_HANDLE_T hExtStHandle);


/*!
 * @name Get information of the subtitle
 * @{
 */


/*! @brief          Get subtitle information from the subtitle instance
 *  @param [in]     hExtStHandle           a handle of external subtitle instance
 *  @param [out]    ptGetStInfo            pointer to the subtitle info structure
 *  @return         success or error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @note           This function must be called after IMtkPb_ExtSt_Parse() is issued.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_ExtSt_GetStInfo(IMTK_EXTST_HANDLE_T hExtStHandle,
                                                   IMTK_PB_EXTST_GET_ST_INFO_T* ptGetStInfo);


/*! @brief          Gets subtitle track info of a specified track index
 *  @param [in]     hExtStHandle    a handle of external subtitle instance
 *  @param [in]     u2TracIdx       The subtitle  track index, starting from 0
 *  @param [out]    ptInfo          References to the subtitle track information.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @note           This function must be called after IMtkPb_ExtSt_Parse() is issued.
 *  @note           The number of subtitle tracks in this file can be retrieved in the u2StTrackNum parameter by IMtkPb_ExtSt_GetStInfo().  u2TrackIdx should range from 0 to (u2StTrackNum-1).
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_ExtSt_GetStTrackInfo(IMTK_EXTST_HANDLE_T                 hExtStHandle,
                                                        uint16_t                            u2TracIdx,
                                                        IMTK_PB_EXTST_GET_ST_TRACK_INFO_T*  ptInfo);


/*!
 * @name Get information for attached playback instance
 * @{
 */


/*! @brief          Get the playback instance that this external subtitle isntance is attached to
 *  @param [in]     hExtStHandle          The handle of external subtitle instance
 *  @param [out]    phPbHandle            the playback instance that this external subtitle isntance is attached to.  If this external subtitle instance hasn't attached to any playback instance, this shall be set to ::IMTK_NULL_HANDLE.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetAttachedPb(IMTK_EXTST_HANDLE_T     hExtStHandle,
                                                      IMTK_PB_HANDLE_T*       phPbHandle);


/*!
 * @}
 */


/*! @} */

#ifdef  __cplusplus
}
#endif
#endif  /* _I_MTK_PB_EXTST_H_ */
