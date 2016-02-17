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


#ifndef _I_MTK_PB_COMM_
#define _I_MTK_PB_COMM_

#ifdef __cplusplus
extern "C" {
#endif

/*-----------------------------------------------------------------------------
                    include files
-----------------------------------------------------------------------------*/


/*-----------------------------------------------------------------------------
                    macros, defines, typedefs, enums
-----------------------------------------------------------------------------*/


/*! @struct IMTK_PB_COMM_ISO_639_1_CODE
 *  @brief OSD language; in ISO 639-1 format
 */
typedef struct
{
    uint8_t u1LangCode0;    ///< language code byte 0; for example, in case of English, byte 0 is 'e'.
    uint8_t u1LangCode1;    ///< language code byte 1; for example, in case of English, byte 1 is 'n'.
} IMTK_PB_COMM_ISO_639_1_CODE;


/*! @struct IMTK_PB_COMM_ISO_639_2_CODE
 *  @brief OSD language; in ISO 639-2 format
 */
typedef struct
{
    uint8_t u1LangCode0;    ///< language code byte 0; for example, in case of English, byte 0 is 'e'.
    uint8_t u1LangCode1;    ///< language code byte 1; for example, in case of English, byte 1 is 'n'.
    uint8_t u1LangCode2;    ///< language code byte 2; for example, in case of English, byte 2 is 'g'.
} IMTK_PB_COMM_ISO_639_2_CODE;


/*! @enum IMTK_PB_COMM_LANG_TYPE_T
 *  @brief language representation type
 */
typedef enum
{
    IMTK_PB_COMM_LANG_TYPE_UNKNOWN   = 0,   ///< unknown language
    IMTK_PB_COMM_LANG_TYPE_ISO_639_1 = 1,   ///< ISO 639-1 2-letter code
    IMTK_PB_COMM_LANG_TYPE_ISO_639_2 = 2    ///< ISO 639-2 3-letter code
} IMTK_PB_COMM_LANG_TYPE_T;


/*! @struct IMTK_PB_COMM_LANG_INFO_T
 *  @brief language code
 */
typedef struct
{
    IMTK_PB_COMM_LANG_TYPE_T eLangType; ///< language representation type;  If it is ::IMTK_PB_COMM_LANG_TYPE_UNKNOWN, this whole ::IMTK_PB_COMM_LANG_INFO_T means an unknown or undetermined language
    union {
        IMTK_PB_COMM_ISO_639_1_CODE tIso639_1_code; ///< ISO 639-1 code
        IMTK_PB_COMM_ISO_639_2_CODE tIso639_2_code; ///< ISO 639-2 code
    } uLangCode;
} IMTK_PB_COMM_LANG_INFO_T;


#ifdef  __cplusplus
}
#endif
#endif  /* _I_MTK_PB_COMM_H_ */
