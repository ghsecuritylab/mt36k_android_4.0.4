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
    IMtkPb_Misc defines some miscellaneous functions not suitable for other categories.
*/


/*----------------------------------------------------------------------------*/
/*! @addtogroup IMtkPb_Misc
 *  @{
 */
/*----------------------------------------------------------------------------*/


#ifndef _I_MTK_PB_MISC_H_
#define _I_MTK_PB_MISC_H_

#ifdef  __cplusplus
extern "C" {
#endif

/*-----------------------------------------------------------------------------
                    include files
-----------------------------------------------------------------------------*/
#include "IMtkPb_ErrorCode.h"

/*-----------------------------------------------------------------------------
                    macros, defines, typedefs, enums
-----------------------------------------------------------------------------*/
/*! @enum   IMTK_PB_MISC_AUD_CHANNEL
 *  @brief  audio channel
 */
typedef enum
{
    IMTK_PB_MISC_AUD_CHANNEL_ALL        = 1,    ///< all channels
    IMTK_PB_MISC_AUD_CHANNEL_FRONT_LEFT,        ///< front left channel
    IMTK_PB_MISC_AUD_CHANNEL_FRONT_RIGHT,       ///< front right channel
} IMTK_PB_MISC_AUD_CHANNEL;


/*! @enum   IMTK_PB_MISC_USER_PREF_IDX_T
 *  @brief  user preference index
 */
typedef enum
{
    IMTK_PB_MISC_OSD_LANGUAGE           = 1,    ///< OSD language; parameter is ::IMTK_PB_MISC_OSD_LANG_INFO_T

    IMTK_PB_MISC_HTTP_PROXY_SERVER      = 101   ///< http proxy server; parameter is ::IMTK_PB_MISC_HTTP_PROXY_INFO_T
} IMTK_PB_MISC_USER_PREF_IDX_T;


/*! @struct IMTK_PB_MISC_OSD_LANG_INFO_T
 *  @brief OSD language; in ISO 639-2 format
 */
typedef struct
{
    uint8_t u1LangCode0;    ///< language code byte 0; for example, in case of English, byte 0 is 'e'.
    uint8_t u1LangCode1;    ///< language code byte 1; for example, in case of English, byte 1 is 'n'.
    uint8_t u1LangCode2;    ///< language code byte 2; for example, in case of English, byte 2 is 'g'.
} IMTK_PB_MISC_OSD_LANG_INFO_T;


#define IMTK_PB_MISC_MAX_HOST_LEN 255   ///< maximal length of a host name in byte


/*! @struct IMTK_PB_MISC_HTTP_PROXY_INFO_T
 *  @brief proxy server information
 */
typedef struct
{
    bool     fgProxyEnabled;                                        ///< true: enabled; false: disabled
    uint8_t  IMTK_PB_MISC_PROXY_SERVER[IMTK_PB_MISC_MAX_HOST_LEN];  ///< a null-terminated string containing the proxy server host name or IP address
    uint16_t u2Port;                                                ///< port number
} IMTK_PB_MISC_HTTP_PROXY_INFO_T;


/*! @union IMTK_PB_MISC_USER_PREF_VALUE_T
 *  @brief user preference value union
 */
typedef union
{
    IMTK_PB_MISC_OSD_LANG_INFO_T    tOsdLang;       ///< osd language info
    IMTK_PB_MISC_HTTP_PROXY_INFO_T  tProxyInfo;     ///< proxy info
} IMTK_PB_MISC_USER_PREF_VALUE_T;


/*-----------------------------------------------------------------------------
                    functions declarations
-----------------------------------------------------------------------------*/


/*!
 * @name Init and terminate
 * @{
 */


/*! @brief          Initializes IMtkPb_Misc
 *  @return         indicate success
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_SYS_LIB_ERROR            unexpected error in system call or standard library
 *  @retval         IMTK_PB_ERROR_CODE_FILE_NOT_FOUND           target kernel module does not exist at system.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      this size of memory does not be allocated.
 *  @retval         IMTK_PB_ERROR_CODE_FILE_READ_ERROR          target device can not be loaded.
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                target device has internal error when target device open.
 *  @note           Before a process starts using IMtkPb_Misc, it should first call this function.
 *  @see            IMtkPb_Misc_Terminate()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Misc_Init(void);


/*! @brief          Terminates IMtkPb_Misc
 *  @return         indicate success
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_SYS_LIB_ERROR            unexpected error in system call or standard library
 *  @retval         IMTK_PB_ERROR_CODE_DRV_ERROR                target device has internal error after target device load.
 *  @note           When a process finishes using IMtkPb_Misc, it should call this function.
 *  @see            IMtkPb_Misc_Init()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Misc_Terminate(void);


/*!
 * @}
 * @name Output settings
 * @{
 */


/*! @brief          Set audio volume
 *  @param [in]     eChannel    the channel whose volume is to be set
 *  @param [in]     u1Volume    volume value, [0:100]
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  incorrect input parameter.
 *  @note           If eChannel is ::IMTK_PB_MISC_AUD_CHANNEL_ALL, the setting affects all channels.  Otherwise only the specified channel is affected.
 *  @note           The u1Volume value 0 means mute, while the value 100 is the loudest.
 *  @note           This setting affects both @ref IMtkPb_Ctrl and @ref IMtkPb_Snd.
 *  @note           MtkPbLib does not store the setting into non-volatile memory.  The setting is forgotten after power off.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Misc_SetAudioVolume(IMTK_PB_MISC_AUD_CHANNEL eChannel,
                                                       uint8_t u1Volume);


/*! @brief          Get audio volume
 *  @param [in]     eChannel    the channel whose volume is to be got
 *  @param [out]    pu1Volume   volume value, [0:100]
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  incorrect input parameter.
 *  @note           It is not allowed for the eChannel to be set to ::IMTK_PB_MISC_AUD_CHANNEL_ALL.
 *  @note           The pu1Volume value 0 means mute, while the value 100 is the loudest.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Misc_GetAudioVolume(IMTK_PB_MISC_AUD_CHANNEL eChannel,
                                                       uint8_t* pu1Volume);


/*!
 * @}
 * @name User Preference Retrieval
 * @{
 */


/*! @brief          Get user preference
 *  @param [in]     ePrefIdx        user preference index
 *  @param [out]    ptUserPrefParam user preference value
 *  @param [in]     u4ValueSize     expected size of <i>ptUserPrefParam</i> for this <i>ePrefIdx</i> value, in byte
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  incorrect input parameter.
 *  @note           This function assumes the user preferences are stored by Mediatek proprietary preference management APIs.  If your product does not use Mediatek proprietary preference management APIs, you should not use this function.
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Misc_GetUserPref(IMTK_PB_MISC_USER_PREF_IDX_T      ePrefIdx,
                                                    IMTK_PB_MISC_USER_PREF_VALUE_T*   ptUserPrefValue,
                                                    uint32_t                          u4ValueSize);


/*!
 * @}
 */


/*! @} */

#ifdef  __cplusplus
}
#endif
#endif  /* _I_MTK_PB_MISC_H_ */
