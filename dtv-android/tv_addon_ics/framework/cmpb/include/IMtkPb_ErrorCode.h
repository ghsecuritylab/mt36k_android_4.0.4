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

#ifndef _I_MTK_PB_ERROR_CODE_
#define _I_MTK_PB_ERROR_CODE_

#ifdef __cplusplus
extern "C" {
#endif

/*-----------------------------------------------------------------------------
                    include files
-----------------------------------------------------------------------------*/
#include "stdint.h"
#include "stdbool.h"
/*-----------------------------------------------------------------------------
                    macros, defines, typedefs, enums
-----------------------------------------------------------------------------*/
typedef  uint32_t IMTK_PB_HANDLE_T;
typedef  uint32_t IMTK_PULL_HANDLE_T;
typedef  uint32_t IMTK_ENCODER_HANDLE_T;
typedef  uint32_t IMTK_MIXER_HANDLE_T;
typedef  uint32_t IMTK_MUXER_HANDLE_T;

#define IMTK_NULL_HANDLE ((uint32_t)-1)
#define IMTK_INVALID_PTS ((uint64_t)-1)

/*! @enum   IMTK_PB_ERROR_CODE_T 
 *  @brief  Error Code
 *
 */
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
    IMTK_PB_ERROR_CODE_DATA_CRYPTO_ERROR       =   -206, ///< The target data has crypto-trouble.
    
    IMTK_PB_ERROR_CODE_NEW_TRICK	       =   -300  ///< new trick flow

} IMTK_PB_ERROR_CODE_T;


/*! @enum   IMTK_PB_CB_ERROR_CODE_T 
 *  @brief  Error Code for callback functions
 *
 */
typedef enum
{
    IMTK_PB_CB_ERROR_CODE_OK                      =   0,   ///< Success.
    IMTK_PB_CB_ERROR_CODE_NOT_OK                  =   -1,  ///< Failed.    
    IMTK_PB_CB_ERROR_CODE_EOF                     =   -2,  ///< EOF. 
    IMTK_PB_CB_ERROR_CODE_NEW_TRICK               =   -3   ///< new trick flow
} IMTK_PB_CB_ERROR_CODE_T;
/*-----------------------------------------------------------------------------
                    functions declarations
-----------------------------------------------------------------------------*/

#ifdef __cplusplus
}
#endif

#endif /* _I_MTK_PB_ERROR_CODE_ */
