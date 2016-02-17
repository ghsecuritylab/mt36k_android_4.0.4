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

#ifndef _I_MTK_PB_CTRL_DTV_
#define _I_MTK_PB_CTRL_DTV_

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
#define IMTK_PB_CTRL_EVENT_SEEK_DONE        (uint32_t)(IMTK_PB_CTRL_EVENT_TIMESEEK_DONE)
#define IMTK_PB_CTRL_EVENT_ERROR            (uint32_t)(IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR)
#define IMTK_PB_CTRL_EVENT_PLAYED           (uint32_t)(IMTK_PB_CTRL_EVENT_PLAY_DONE)
#define IMTK_PB_CTRL_EVENT_REPLAY           (uint32_t)(IMTK_PB_CTRL_EVENT_FILE_REPLAY)

#define IMTK_PB_CTRL_EVENT_BITRATE_UPDATE               (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_UNKNOWN+1000)
#define IMTK_PB_CTRL_EVENT_ASP_UPDATE                   (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_UNKNOWN+1001)
#define IMTK_PB_CTRL_EVENT_RESOLUTION_NOT_SUPPORT       (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_UNKNOWN+1002)
#define IMTK_PB_CTRL_EVENT_FRAMERATE_NOT_SUPPORT        (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_UNKNOWN+1003)
#define IMTK_PB_CTRL_EVENT_BUFFER_READY                 (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_UNKNOWN+1004)
#define IMTK_PB_CTRL_EVENT_CLEAR_EOF                    (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_UNKNOWN+1005)
#define IMTK_PB_CTRL_EVENT_PREPARE_DONE                 (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_UNKNOWN+1006)
#define IMTK_PB_CTRL_EVENT_POSITION_UPDATE              (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_UNKNOWN+1007)
#define IMTK_PB_CTRL_EVENT_SPEED_UPDATE                 (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_UNKNOWN+1008)
#define IMTK_PB_CTRL_EVENT_PLAY_TO_END                  (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_UNKNOWN+1009)
#define IMTK_PB_CTRL_EVENT_AUDIO_ONLY_SERVICE           (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_UNKNOWN+1010)
#define IMTK_PB_CTRL_EVENT_VIDEO_NUM_READY              (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_UNKNOWN+1011)

/* Add DRM status */
#define IMTK_PB_CTRL_EVENT_DRM_STATUS_START             (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_UNKNOWN+1018)
#define IMTK_PB_CTRL_EVENT_DRM_NONE                     IMTK_PB_CTRL_EVENT_DRM_STATUS_START
#define IMTK_PB_CTRL_EVENT_DRM_UNKNOWN_ERR              (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+1)
#define IMTK_PB_CTRL_EVENT_DRM_LIC_EXPIRATION_ERR       (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+2)
#define IMTK_PB_CTRL_EVENT_DRM_SRVDATA_ACCESS_FAIL      (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+3)
#define IMTK_PB_CTRL_EVENT_DRM_INV_BUSINESS_TKN         (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+4)
#define IMTK_PB_CTRL_EVENT_DRM_CERT_EXPIRED             (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+5)
#define IMTK_PB_CTRL_EVENT_DRM_LIC_NOT_ALLOW            (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+6)
#define IMTK_PB_CTRL_EVENT_DRM_SRV_END                  (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+7)
#define IMTK_PB_CTRL_EVENT_DRM_SRV_UNAVAIL              (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+8)
#define IMTK_PB_CTRL_EVENT_DRM_SRV_BUSY                 (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+9)
#define IMTK_PB_CTRL_EVENT_DRM_DEV_NOT_REG              (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+10)
#define IMTK_PB_CTRL_EVENT_DRM_CONTENT_EXHAUSTED        (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+11)
#define IMTK_PB_CTRL_EVENT_DRM_UNKNOWN_SRV_ERR          (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+12)
#define IMTK_PB_CTRL_EVENT_DRM_INV_USR_AGENT            (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+13)
#define IMTK_PB_CTRL_EVENT_DRM_UNSUPPORTED_VER          (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+14)
#define IMTK_PB_CTRL_EVENT_DRM_INV_CONTENT_TYPE         (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+15)
#define IMTK_PB_CTRL_EVENT_DRM_INV_SRV_TKN              (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+16)
#define IMTK_PB_CTRL_EVENT_DRM_INV_ASSET_ID             (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+17)

#define IMTK_PB_CTRL_EVENT_DRM_INIT_ERROR                       (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+18)
#define IMTK_PB_CTRL_EVENT_DRM_LICENSE_EXPIRED_ERROR            (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+19)
#define IMTK_PB_CTRL_EVENT_DRM_LICENSE_ACQUISITION_ERROR        (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+20)
#define IMTK_PB_CTRL_EVENT_DRM_LICENSE_SERVER_CONNECTION_ERROR  (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+21)
#define IMTK_PB_CTRL_EVENT_DRM_TOKEN_ACQUISITION_ERROR          (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+22)
#define IMTK_PB_CTRL_EVENT_DRM_CAD_ACQUISITION_ERROR            (IMTK_PB_CTRL_EVENT_T)(IMTK_PB_CTRL_EVENT_DRM_STATUS_START+23)


#define IMTK_PB_CTRL_SPEED_FF_1X            (uint32_t)(IMTK_PB_CTRL_SPEED_1X)

#define IMTK_PB_CTRL_VID_ENC_NV12           (uint32_t)(IMTK_PB_CTRL_VID_ENC_UNKNOWN+1000)

#define IMTK_CTRL_SUBTITLE_OFF                   ((uint8_t) (255))
#define IMTK_CTRL_ISO_639_LANG_LEN               4
#define SUBTITLE_LANG_LENGTH    4
/*! @struct IMTK_PB_CTRL_GET_SUB_TRACK_INFO_T 
 *  @brief  Subtitle Track Info structure
 */
typedef struct
{
    uint8_t au1Lang[SUBTITLE_LANG_LENGTH];
} IMTK_PB_CTRL_GET_SUB_TRACK_INFO_T;


#define IMTK_PB_CTRL_MAX_FONT_NAME               32    /**<        */

#define IMTK_PB_CTRL_DIVX_DRM_RENTAL                 ((uint8_t)1 << 0)
#define IMTK_PB_CTRL_DIVX_DRM_RENTAL_EXPIRED         ((uint8_t)1 << 1)
#define IMTK_PB_CTRL_DIVX_DRM_AUTH_ERROR             ((uint8_t)1 << 2)

#define IMTK_PB_CTRL_MAKE_BIT_MASK_8(_val)  (((uint8_t)  1) << (_val))    /**<        */
#define IMTK_PB_CTRL_MAKE_BIT_MASK_16(_val) (((uint16_t) 1) << (_val))    /**<        */

typedef enum
{
    IMTK_PB_CTRL_META_TYPE_INVAL = 0,
    IMTK_PB_CTRL_META_TYPE_TITLE,
    IMTK_PB_CTRL_META_TYPE_DIRECTOR,
    IMTK_PB_CTRL_META_TYPE_COPYRIGHT,
    IMTK_PB_CTRL_META_TYPE_YEAR,
    IMTK_PB_CTRL_META_TYPE_DATE,
    IMTK_PB_CTRL_META_TYPE_GENRE,
    IMTK_PB_CTRL_META_TYPE_DURATION,
    IMTK_PB_CTRL_META_TYPE_SIZE,
    IMTK_PB_CTRL_META_TYPE_ARTIST,
    IMTK_PB_CTRL_META_TYPE_ALBUM,
    IMTK_PB_CTRL_META_TYPE_BITRATE,
    IMTK_PB_CTRL_META_TYPE_PROTECT,
    IMTK_PB_CTRL_META_TYPE_CREATE_TIME,
    IMTK_PB_CTRL_META_TYPE_ACCESS_TIME,
    IMTK_PB_CTRL_META_TYPE_MODIFY_TIME,
    IMTK_PB_CTRL_META_TYPE_RESOLUTION,
    IMTK_PB_CTRL_META_TYPE_NEXT_TITLE,
    IMTK_PB_CTRL_META_TYPE_NEXT_ARTIST,
    /*--- New Added For Android ---*/
    IMTK_PB_CTRL_META_TYPE_CD_TRACK_NUMBER,
    IMTK_PB_CTRL_META_TYPE_AUTHOR,
    IMTK_PB_CTRL_META_TYPE_COMPOSER,
    IMTK_PB_CTRL_META_TYPE_NUM_TRACKS,
    IMTK_PB_CTRL_META_TYPE_IS_DRM_CRIPPLED,
    IMTK_PB_CTRL_META_TYPE_CODEC,
    IMTK_PB_CTRL_META_TYPE_RATING,
    IMTK_PB_CTRL_META_TYPE_COMMENT,
    IMTK_PB_CTRL_META_TYPE_FRAME_RATE,
    IMTK_PB_CTRL_META_TYPE_VIDEO_FORMAT,
    IMTK_PB_CTRL_META_TYPE_VIDEO_HEIGHT,
    IMTK_PB_CTRL_META_TYPE_VIDEO_WIDTH,
    IMTK_PB_CTRL_META_TYPE_WRITER,
    IMTK_PB_CTRL_META_TYPE_MIMETYPE,
    IMTK_PB_CTRL_META_TYPE_DISCNUMBER,
    IMTK_PB_CTRL_META_TYPE_ALBUMARTIST
} IMTK_PB_CTRL_META_TYPE_T;


typedef enum
{
    IMTK_PB_CTRL_GET_TYPE_NONE       =   0,
    IMTK_PB_CTRL_GET_TYPE_MEDIA_INFO,
    IMTK_PB_CTRL_GET_TYPE_META_DATA,
    IMTK_PB_CTRL_GET_TYPE_DRM_INFO,
    IMTK_PB_CTRL_GET_TYPE_MP3_COVER,
    IMTK_PB_CTRL_GET_TYPE_MEM_BAND_STATUS,
    IMTK_PB_CTRL_GET_TYPE_VIDEO_INFO,
    IMTK_PB_CTRL_GET_TYPE_3D_VIDEO_INFO,
    IMTK_PB_CTRL_GET_TYPE_DOWNLOAD_SPEED,
    IMTK_PB_CTRL_GET_TYPE_MEDIA_STRM_INFO,
    IMTK_PB_CTRL_GET_TYPE_SPEED
} IMTK_PB_CTRL_GET_TYPE_T;


typedef enum
{
    IMTK_PB_CTRL_SET_TYPE_NONE       =   0,
    IMTK_PB_CTRL_SET_TYPE_SBTL_ATTR,
    IMTK_PB_CTRL_SET_TYPE_TS_PROGRAM_IDX,
    IMTK_PB_CTRL_SET_TYPE_MP3_META_LANG,
    IMTK_PB_CTRL_SET_TYPE_SEAMLESS,
    IMTK_PB_CTRL_SET_TYPE_DOWNLOAD_SPEED,
	IMTK_PB_CTRL_SET_TYPE_BUF_CTRL_MODE,	 /* 3rd control or mw*/
	IMTK_PB_CTRL_SET_TYPE_FILE_HEADER
} IMTK_PB_CTRL_SET_TYPE_T;

/* Capture LOGO IMG Format */
typedef enum
{
    IMTK_CAP_IMG_FMT_TYPE_DEFAULT = 0,
    IMTK_CAP_IMG_FMT_TYPE_MPEG,
    IMTK_CAP_IMG_FMT_TYPE_JPEG,
    IMTK_CAP_IMG_FMT_TYPE_RAW
    /*to be extend*/ 
}  IMTK_CAP_IMG_FMT_TYPE_T;


/* MP3 Cover Picture Format */
typedef enum
{
    IMTK_MP3_COVER_IMG_TYPE_UNKNOWN = 0,
    IMTK_MP3_COVER_IMG_TYPE_PNG,
    IMTK_MP3_COVER_IMG_TYPE_JPG
} IMTK_MP3_COVER_IMG_TYPE_T;


/*! @struct IMTK_PB_CTRL_DIVX_DRM_BASIC_INFO_T 
 *  @brief  DIVX DRM basic info structure
 */
typedef struct
{
    uint8_t u1Flag;
    uint8_t u1UseCount;
    uint8_t u1UseLimit;
} IMTK_PB_CTRL_DIVX_DRM_BASIC_INFO_T;

/*! @struct IMTK_PB_CTRL_DIVX_DRM_REGISTRATION_INFO_T
 *  @brief  This structure defines the divx drm registration info.
 */
typedef struct
{
    uint8_t pu1RegistrationCode[11];
} IMTK_PB_CTRL_DIVX_DRM_REGISTRATION_INFO_T;

#define IMTK_PB_CTRL_DIVX_DRM_REGISTRATION_CODE_HIDE         ((uint32_t)1 << 0)
#define IMTK_PB_CTRL_DIVX_DRM_DEACTIVATION_CONFIRMATION      ((uint32_t)1 << 1)

/*! @struct IMTK_PB_CTRL_DIVX_DRM_UI_HELP_INFO_T
 *  @brief  This structure defines the divx drm UI info.
 */
typedef struct
{
    uint32_t u4DivxDrmUIHelpInfo;
} IMTK_PB_CTRL_DIVX_DRM_UI_HELP_INFO_T;

/*! @struct IMTK_PB_CTRL_DIVX_DRM_DEACTIVATION_INFO_T
 *  @brief  This structure defines the divx drm deactivation info.
 */
typedef struct
{
    uint8_t pu1DeactivationCode[9];
} IMTK_PB_CTRL_DIVX_DRM_DEACTIVATION_INFO_T;

/*! @enum   IMTK_PB_CTRL_PULL_EVENT_T 
 *  @brief  EVENT for PULL I/F
 */
typedef enum
{
    IMTK_PB_CTRL_DIVX_DRM_BASIC             =   0,
    IMTK_PB_CTRL_DIVX_DRM_REGISTRATION,
    IMTK_PB_CTRL_DIVX_DRM_UI_HELP
} IMTK_PB_CTRL_DIVX_DRM_INFO_TYPE_T;

typedef struct
{
    IMTK_PB_CTRL_DIVX_DRM_INFO_TYPE_T    eInfoType;
    union
    {
        IMTK_PB_CTRL_DIVX_DRM_BASIC_INFO_T          tBasic;
        IMTK_PB_CTRL_DIVX_DRM_REGISTRATION_INFO_T   tRegistration;
        IMTK_PB_CTRL_DIVX_DRM_UI_HELP_INFO_T        tUIHelp;
    }uInfo;
} IMTK_PB_CTRL_DIVX_DRM_INFO_T;

typedef enum
{
    IMTK_PB_CTRL_DRM_TYPE_NONE       =   0,
    IMTK_PB_CTRL_DRM_TYPE_DIVX_DRM,
    IMTK_PB_CTRL_DRM_TYPE_MLN
} IMTK_PB_CTRL_DRM_TYPE_T;

typedef struct 
{
    uint8_t*              pui1_act_tkn_url;                  /* action token url from CAD file */
    uint16_t              ui2_act_tkn_url_length;            /* length of action token url, include null charater '\0' */
    uint8_t*              pui1_afl_tkn;                      /* affiliation token from CAD file */
    uint16_t              ui2_afl_tkn_length;                /* length of affiliation token, include null charater '\0' */
} IMTK_PB_CTRL_MLN_DRM_INFO_T;

typedef struct 
{
    IMTK_PB_CTRL_DRM_TYPE_T       e_drm_type;
    union
    {
        IMTK_PB_CTRL_MLN_DRM_INFO_T   t_mln_drm_inf;
    }u;
} IMTK_PB_CTRL_DRM_INFO_T;


typedef enum
{
    IMTK_PB_CTRL_AUD_OUT_PORT_OFF            = 0x00,
    IMTK_PB_CTRL_AUD_OUT_PORT_2_CH           = 0x01,
    IMTK_PB_CTRL_AUD_OUT_PORT_5_1_CH         = 0x02,
    IMTK_PB_CTRL_AUD_OUT_PORT_SPDIF          = 0x04,
    IMTK_PB_CTRL_AUD_OUT_PORT_2_CH_BY_PASS   = 0x08,
    IMTK_PB_CTRL_AUD_OUT_PORT_SPEAKER        = 0x10,
    IMTK_PB_CTRL_AUD_OUT_PORT_HEADPHONE      = 0x20,
    IMTK_PB_CTRL_AUD_OUT_PORT_TVSCART        = 0x40
}   IMTK_PB_CTRL_AUD_OUT_PORT_T;

typedef enum
{
    IMTK_PB_CTRL_AUD_CHANNEL_ALL = 0,
    IMTK_PB_CTRL_AUD_CHANNEL_FRONT_LEFT,
    IMTK_PB_CTRL_AUD_CHANNEL_FRONT_RIGHT,
    IMTK_PB_CTRL_AUD_CHANNEL_REAR_LEFT,
    IMTK_PB_CTRL_AUD_CHANNEL_REAR_RIGHT,
    IMTK_PB_CTRL_AUD_CHANNEL_CENTER,
    IMTK_PB_CTRL_AUD_CHANNEL_SUB_WOOFER
} IMTK_PB_CTRL_AUD_CHANNEL_T;

typedef struct _IMTK_PB_CTRL_AUD_VOLUME_INFO_T
{
    IMTK_PB_CTRL_AUD_OUT_PORT_T  e_out_port;
    IMTK_PB_CTRL_AUD_CHANNEL_T   e_ch;
    uint8_t                      ui1_volumn;
} IMTK_PB_CTRL_AUD_VOLUME_INFO_T;

/*! @struct IMTK_PB_CTRL_MEDIA_INFO_T 
 *  @brief  Media Info structure
 */
typedef struct
{
    IMTK_PB_CTRL_MEDIA_TYPE_T       eMediaType;             ///< Media type
    uint32_t                        u4TotalDuration;        ///< total time(millisecond)
    uint64_t                        u8Size;                 ///< total size of this file, in byte
    uint32_t                        u4AvgBitrate;           ///< average bitrate of this file, in bits per second
    uint16_t                        u2VideoTrackNum;        ///< number of video tracks
    uint16_t                        u2AudioTrackNum;        ///< number of audio tracks
    uint16_t                        u2SubtlTrackNum;        ///< number of subtl tracks
    uint8_t                         u1ProgramNum;
} IMTK_PB_CTRL_MEDIA_INFO_T;

typedef struct
{
    IMTK_PB_CTRL_META_TYPE_T    e_meta_type;
    void*                       pv_buf;
    uint16_t                    ui2_buf_size; 
} IMTK_PB_CTRL_META_DATA_INFO_T;

typedef struct
{
    /*uint8_t                   ui1_pic_idx; Maybe need select specified logo*/
    IMTK_MP3_COVER_IMG_TYPE_T e_img_type;
    uint32_t                  ui4_width;
    uint32_t                  ui4_height;
    uint32_t                  ui4_length;
    uint8_t*                  pui1_img_data_buf;/*If this value not empty, it need to fill data to this*/
}IMTK_PB_CTRL_MP3_COVER_INFO_T;

typedef struct _IMTK_PB_CTRL_VIDEO_INFO_T
{
   uint32_t               u4Width;
   uint32_t               u4Height;    
#ifdef DIVX_PLUS_CER
   uint8_t                u1ParWidth;
   uint8_t                u1ParHeight;
   bool                   bSrcAsp;
#endif
} IMTK_PB_CTRL_VIDEO_INFO_T;

typedef enum
{
    IMTK_PB_CTRL_TAG3D_2D = 0,
    IMTK_PB_CTRL_TAG3D_MVC,          /* MVC = Multi-View Codec */
    IMTK_PB_CTRL_TAG3D_FP,           /* FP = Frame Packing */
    IMTK_PB_CTRL_TAG3D_FS,           /* FS = Frame Sequential */
    IMTK_PB_CTRL_TAG3D_TB,           /* TB = Top-and-Bottom */
    IMTK_PB_CTRL_TAG3D_SBS,          /* SBS = Side-by-Side */
    IMTK_PB_CTRL_TAG3D_REALD,
    IMTK_PB_CTRL_TAG3D_SENSIO,
    IMTK_PB_CTRL_TAG3D_TTDO,        /* TTD only */
    IMTK_PB_CTRL_TAG3D_NOT_SUPPORT
}   IMTK_PB_CTRL_TAG3D_TYPE_T;

typedef struct _IMTK_PB_CTRL_3D_VIDEO_INFO_T
{
    IMTK_PB_CTRL_TAG3D_TYPE_T   eTag3dType;    
}IMTK_PB_CTRL_3D_VIDEO_INFO_T;

/*! @brief          Read Divx DRM Memory
 *  @param [in]     u4Offset     Offset of Divx DRM Memory.
 *  @param [in]     u1Len        Length of this read operation
 *  @param [out]    au1Buf       Read Buffer
 *  @return     Return the error code
 *  @retval     IMTK_PB_CB_ERROR_CODE_OK                    Success.
 *  @retval     IMTK_PB_CB_ERROR_CODE_NOT_OK                Failed.
 *  @note       Get the total length of the pull source.
 *  @see
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Divx_ReadDrmMem_Fct)(uint8_t*         au1Buf,
                                                                   uint32_t         u4Offset,
                                                                   uint8_t          u1Len);


/*! @brief          Write Divx DRM Memory
 *  @param [in]     u4Offset     Offset of Divx DRM Memory.
 *  @param [in]     u1Len        Length of this write operation
 *  @param [in]     au1Buf       Read Buffer
 *  @return     Return the error code
 *  @retval     IMTK_PB_CB_ERROR_CODE_OK                    Success.
 *  @retval     IMTK_PB_CB_ERROR_CODE_NOT_OK                Failed.
 *  @note       Get the total length of the pull source.
 *  @see
 */
typedef IMTK_PB_CB_ERROR_CODE_T (*IMtkPb_Ctrl_Divx_WriteDrmMem_Fct)(uint8_t*        au1Buf,
                                                                    uint32_t        u4Offset,
                                                                    uint8_t         u1Len);

/*! @struct IMTK_PB_CTRL_DIVX_DRM_MEM_RW_T
 *  @brief  function table for divx drm memory read/write 
 *
 */
typedef struct
{
    IMtkPb_Ctrl_Divx_ReadDrmMem_Fct         pfnRead;                ///<  Read function
    IMtkPb_Ctrl_Divx_WriteDrmMem_Fct        pfnWrite;               ///<  Write function
} IMTK_PB_CTRL_DIVX_DRM_MEM_RW_T;

typedef struct
{
    uint32_t    u4Start;    ///< index of range start
    uint32_t    u4End;      ///<index of range end, ui4_end could be smaller than ui4_start when tick index wrap over
}   IMTK_PB_CTRL_BUF_RANGE_INFO_T;

typedef struct
{
    IMTK_PB_CTRL_BUF_RANGE_INFO_T     t_valid;  ///< valid range for playback */
    IMTK_PB_CTRL_BUF_RANGE_INFO_T     t_safe;   ///< safe range for playback, safe range should be a subset of valid range */
}   IMTK_PB_CTRL_REC_BUF_RANGE_INFO_T;

typedef struct
{
    void*       pvStartAddress;
    void*       pvEndAddress;
    uint32_t    u4EntryNum;
    uint32_t    u4EntrySize;
    uint32_t    u4MaxVldEntryNum;
    uint32_t    u4TickPeriod;
    uint32_t    u4LbaInit;
    uint64_t    u8FifoOffset;   
    uint32_t    u4FifoPktNum;
} IMTK_PB_TICK_CTRL_BLK_T;

typedef struct
{
    uint32_t                u4SrcW;
    uint32_t                u4SrcH;
    uint32_t                u4PixelW;
    uint32_t                u4PixelH;
    bool                    fgIsDisplay;
} IMTK_PB_CTRL_ASP_T;

/*
typedef enum
{
    MMP_VE_INFO_ID_TITLE,
    MMP_VE_INFO_ID_GENRE,
    MMP_VE_INFO_ID_ARTIST
} IMTK_PB_CTRL_META_TYPE;
*/

typedef struct
{
    uint32_t        u4TitleLen;
    uint16_t        pu2Title[128];
    uint32_t        u4GenreLen;
    uint16_t        pu2Genre[128];
    uint32_t        u4ArtistLen;
    uint16_t        pu2Artist[128];
}IMTK_PB_CTRL_META_T;


typedef struct
{
    uint32_t                u4TitleIdx;
    uint64_t                u8TitleUidHigh;
    uint64_t                u8TitleUidLow;
    uint32_t                u4PlaylistNum;
    bool                    fgHidden;
    bool                    fgDefault;
    bool                    fgEnable;
    uint32_t                u4TitleLen;
    uint16_t                pu2TitleName[128];
}IMTK_PB_CTRL_TITLE_INFO_T;

typedef struct
{
    uint32_t                u4TitleIdx;
    uint32_t                u4PlaylistIdx;
    uint64_t                u8PlaylistUid;
    uint32_t                u4ChapterNum;
    bool                    fgHidden;
    bool                    fgDefault;
    bool                    fgEnable;
}IMTK_PB_CTRL_PLAYLIST_INFO_T;

typedef struct
{
    uint32_t                u4TitleIdx;
    uint32_t                u4PlaylistIdx;
    uint32_t                u4ChapIdx;
    uint64_t                u8ChapUid;
    uint32_t                u4DispNum;
    uint64_t                u8StartTime;
    uint64_t                u8EndTime;
    uint64_t                u8PlTime;
    bool                    fgHidden;
    bool                    fgDefault;
    bool                    fgEnable;
}IMTK_PB_CTRL_CHAP_INFO_T;

typedef struct
{
    uint32_t                u4TitleIdx;
    uint32_t                u4PlaylistIdx;
    uint32_t                u4ChapIdx;
    uint32_t                u4DispIdx;
    uint32_t                u4ChapterLen;
    uint8_t                 pu1ChapterName[128];
    uint32_t                u4CountryLen;
    uint8_t                 pu1Country[128];
    uint32_t                u4LanguageLen;
    uint8_t                 pu1Language[128];
}IMTK_PB_CTRL_DISP_INFO_T;

typedef struct
{
    uint32_t                u4PlaylistId;
    uint32_t                u4ChapterId;
}IMTK_PB_CTRL_CUR_CHAP_ID_T;

typedef enum
{
    IMTK_PB_CTRL_NAV_TITLE_NUM    =   0,
    IMTK_PB_CTRL_NAV_TITLE_INFO,
    IMTK_PB_CTRL_NAV_PLAYLIST_INFO,
    IMTK_PB_CTRL_NAV_CHAP_INFO,
    IMTK_PB_CTRL_NAV_DISP_INFO,
    IMTK_PB_CTRL_NAV_CUR_CHAP_ID
} IMTK_PB_CTRL_NAV_MODE_T;


typedef struct
{
    IMTK_PB_CTRL_NAV_MODE_T eNavMode;
    union
    {
        uint32_t                            u4TitleNum;
        IMTK_PB_CTRL_TITLE_INFO_T           tTitle;
        IMTK_PB_CTRL_PLAYLIST_INFO_T        tPlaylist;
        IMTK_PB_CTRL_CHAP_INFO_T            tChap;
        IMTK_PB_CTRL_DISP_INFO_T            tDisp;
        IMTK_PB_CTRL_CUR_CHAP_ID_T          tCurChapID;
    }uInfo;

}IMTK_PB_CTRL_NAV_INFO_T;


typedef enum
{
    IMTK_PB_CTRL_PUSH_BUF_UNDERFLOW,        ///< playback buffer underflow, maybe need pause to buffer.
    IMTK_PB_CTRL_PUSH_BUF_LOW,              ///< playback buffer low, maybe need pause to buffer.
    IMTK_PB_CTRL_PUSH_BUF_NORMAL,           ///< playback buffer normal, can normal play.
    IMTK_PB_CTRL_PUSH_BUF_HIGH,             ///< playback buffer high, .
    IMTK_PB_CTRL_PUSH_BUF_OVERFLOW          ///< playback buffer overflow, .
#ifdef MM_SEND_BUFFER_PREPLAY_SUPPORT    
    ,IMTK_PB_CTRL_PUSH_BUF_PRESTORE
#endif
} IMTK_PB_CTRL_PUSH_BUF_STATUS_T;


/*! @struct IMTK_PB_CTRL_BUFFER_STATUS_T
 *  @brief buffer info (This buffer means the bitstream buffer inside the MtkPbLib)
 */
typedef struct
{
    uint32_t u4BufBeginTime;    ///< the playback time associated with the beginning of buffered data, in millisecond
    uint32_t u4BufEndTime;      ///< the playback time associated with the end of buffered data, in millisecond
    uint32_t u4BufRemainDur;    ///< the playback duration from current playback point to the end of buffered data, in millisecond
    bool     fgBufFull;         ///< true: buffer is absolutely full; false: buffer is not full
    IMTK_PB_CTRL_PUSH_BUF_STATUS_T e_buf_status;
} IMTK_PB_CTRL_BUFFER_STATUS_EXT_T;

#ifdef MM_SEND_BUFFER_PREPLAY_SUPPORT
typedef struct _IMTK_PB_CTRL_PRESTORE_DATA_T
{
    uint8_t* pu1Buffer;
    uint32_t u4BufferSize;
    struct _IMTK_PB_CTRL_PRESTORE_DATA_T* ptNext;
}IMTK_PB_CTRL_PRESTORE_DATA_T;
#endif

typedef enum
{
    IMTK_PB_CTRL_DVB_SBTL_UNKNOWN = 0,    ///< Specifying "unknown frame rate" means we will adopt the frame rate embedded in bitstream for playback
    IMTK_PB_CTRL_DVB_SBTL_NO_ASP_RATIO,         ///< 24000/1001 (23.976...)
    IMTK_PB_CTRL_DVB_SBTL_4_3,
    IMTK_PB_CTRL_DVB_SBTL_16_9,
    IMTK_PB_CTRL_DVB_SBTL_221_1,          ///< 30000/1001 (29.97...)
    IMTK_PB_CTRL_DVB_SBTL_HD,
    IMTK_PB_CTRL_DVB_SBTL_NO_ASP_RATIO_HOH,
    IMTK_PB_CTRL_DVB_SBTL_4_3_HOH,          ///< 60000/1001 (59.94...)
    IMTK_PB_CTRL_DVB_SBTL_16_9_HOH,
    IMTK_PB_CTRL_DVB_SBTL_221_1_HOH,
    IMTK_PB_CTRL_DVB_SBTL_HD_HOH
} IMTK_PB_CTRL_DVB_SBTL_TYPE_T;
/*! @struct IMTK_PB_CTRL_DVB_SUBTITLE_INFO_T
 *  @brief  program stream subtitle info structure
 */
typedef struct
{
    uint16_t                        ui2_pid;
    uint16_t                        ui2_comp_pg_id;
    uint16_t                        ui2_anci_pg_id;
    uint8_t                         s_lang [IMTK_CTRL_ISO_639_LANG_LEN];
    IMTK_PB_CTRL_DVB_SBTL_TYPE_T    e_sbtl_type;
    uint16_t                        ui2_pmt_index;
    uint32_t                        h_gl_plane;
} IMTK_PB_CTRL_SUBTITLE_INFO_T;

#if 0
typedef enum
{
    IMTK_PB_CTRL_SBTL_TYPE_DIVX,
    IMTK_PB_CTRL_SBTL_TYPE_MKV,
    IMTK_PB_CTRL_SBTL_TYPE_DVB
} IMTK_PB_CTRL_SBTL_TYPE_T;

/*! @union IMTK_PB_CTRL_SUBTITLE_INFO_T
 *  @brief  Video info structure used by IMtkPb_Ctrl_ChangeVideo()
 */
typedef union
{
    IMTK_PB_CTRL_SBTL_TYPE_T            e_sbtl_type;
    union
    {
        IMTK_PB_CTRL_DVB_SUBTITLE_INFO_T    tDvbSubInfo;  ///< Dvb Subtitle info
    }
} IMTK_PB_CTRL_SUBTITLE_INFO_T;
#endif


typedef enum
{
    IMTK_PB_CTRL_IBC_ENCRYPT_MODE_NONE = 0,             
    IMTK_PB_CTRL_IBC_ENCRYPT_MODE_DES_ECB,              
    IMTK_PB_CTRL_IBC_ENCRYPT_MODE_DES_CBC,              
    IMTK_PB_CTRL_IBC_ENCRYPT_MODE_3DES_ECB,             
    IMTK_PB_CTRL_IBC_ENCRYPT_MODE_3DES_CBC,             
    IMTK_PB_CTRL_IBC_ENCRYPT_MODE_DVB,                  
    IMTK_PB_CTRL_IBC_ENCRYPT_MODE_DVB_CONF,             
    IMTK_PB_CTRL_IBC_ENCRYPT_MODE_MULTI2_BIG,           
    IMTK_PB_CTRL_IBC_ENCRYPT_MODE_MULTI2_LITTLE,        
    IMTK_PB_CTRL_IBC_ENCRYPT_MODE_AES_ECB,              
    IMTK_PB_CTRL_IBC_ENCRYPT_MODE_AES_CBC               
} IMTK_PB_CTRL_IBC_ENCRYPT_MODE_T;

typedef enum
{
    IMTK_PB_CTRL_IBC_RTB_MODE_CLEAR = 0,
    IMTK_PB_CTRL_IBC_RTB_MODE_CTS,
    IMTK_PB_CTRL_IBCC_RTB_MODE_SCTE52,
    IMTK_PB_CTRL_IBC_RTB_MODE_MAX,
} IMTK_PB_CTRL_IBC_RTB_MODE_T;

#define IMTK_PB_CTRL_AES_ENCRYPT_IV        1
#define IMTK_PB_CTRL_AES_ENCRYPT_UNIQUE    2
#define IMTK_PB_CTRL_AES_ENCRYPT_EVEN      4
#define IMTK_PB_CTRL_AES_ENCRYPT_ODD       8
typedef struct
{
    uint8_t     u1Mask;
    uint8_t     u1KeyLen;
    bool        fgWarpKey;
    bool        fgWarpIV;
    uint8_t     au1IV[16];
    uint8_t     au1UniqueKey[16];
    uint8_t     au1EvenKey[32];
    uint8_t     au1Odd_key[32];
} IMTK_PB_CTRL_IBC_AES_ENCRYPT_INFO_T;

typedef struct
{
    IMTK_PB_CTRL_IBC_ENCRYPT_MODE_T eEncryptMode;

    union
    {
        IMTK_PB_CTRL_IBC_AES_ENCRYPT_INFO_T tAes;
    }uEncryptInfo;
    
    IMTK_PB_CTRL_IBC_RTB_MODE_T eRtbMode;
    
} IMTK_PB_CTRL_IBC_PARAM_SET_ENCRYPT_INFO;


#define CHECK_SYNC_BYTE_NUM             5
#define SUPPORT_PMT_MAX_NUM             16
#define SINGLE_PMT_MAX_STREAM_NUM       8
typedef struct 
{
    uint16_t        ui2_strm_pid;
    uint16_t        ui2_strm_type;
}IMTK_PB_CTRL_TS_SINGLE_STRM_INFO_T;

typedef struct 
{
    bool            fg_init;
    uint16_t        ui2_pmt_pid;
    uint8_t         ui1_strm_num;
    IMTK_PB_CTRL_TS_SINGLE_STRM_INFO_T   at_stream_info_list[SINGLE_PMT_MAX_STREAM_NUM];
}IMTK_PB_CTRL_TS_SINGLE_PMT_INFO_T;

typedef struct
{
    uint16_t        ui2_packet_size;
    uint16_t        ui2_pat_pid;//default is ZERO
    uint8_t         ui1_pmt_num;
    IMTK_PB_CTRL_TS_SINGLE_PMT_INFO_T   at_pmt_info[SUPPORT_PMT_MAX_NUM];
} IMTK_PB_CTRL_TS_SINGLE_PAT_INFO_T;

typedef enum
{
    IMTK_PB_CTRL_SBTL_DISP_MODE_OFF = 0,
    IMTK_PB_CTRL_SBTL_DISP_MODE_SINGLE_LINE,
    IMTK_PB_CTRL_SBTL_DISP_MODE_MULTI_LINE
} IMTK_PB_CTRL_SBTL_DISP_MODE_TYPE_T;

typedef struct _IMTK_PB_CTRL_SBTL_DISP_MODE_T
{
    IMTK_PB_CTRL_SBTL_DISP_MODE_TYPE_T          eDispMode;
    uint16_t                                    u2Param;
} IMTK_PB_CTRL_SBTL_DISP_MODE_T;

typedef enum
{
    IMTK_PB_CTRL_SBTL_HILT_STL_BY_LINE = 0,
    IMTK_PB_CTRL_SBTL_HILT_STL_KARAOKE
} IMTK_PB_CTRL_SBTL_HILT_STL_TYPE_T;

typedef struct _IMTK_PB_CTRL_SBTL_HLT_STL_T
{
    IMTK_PB_CTRL_SBTL_HILT_STL_TYPE_T       eHltStyle;
    uint16_t                                u2Param;
} IMTK_PB_CTRL_SBTL_HILT_STL_T;

typedef enum
{
    IMTK_PB_CTRL_SBTL_TIME_OFST_OFF = 0,
    IMTK_PB_CTRL_SBTL_TIME_OFST_AUTO,
    IMTK_PB_CTRL_SBTL_TIME_OFST_USER_DEF
} IMTK_PB_CTRL_SBTL_TIME_OFST_TYPE_T;

typedef struct _IMTK_PB_CTRL_SBTL_TIME_OFST_T
{
    IMTK_PB_CTRL_SBTL_TIME_OFST_TYPE_T       eTimeOfst;
    uint32_t                                 u4OfstValue;
} IMTK_PB_CTRL_SBTL_TIME_OFST_T;

typedef enum
{
    IMTK_PB_CTRL_SBTL_FONT_ENC_AUTO = 0,
    IMTK_PB_CTRL_SBTL_FONT_ENC_UTF8,
    IMTK_PB_CTRL_SBTL_FONT_ENC_UTF16,
    IMTK_PB_CTRL_SBTL_FONT_ENC_BIG5,
    IMTK_PB_CTRL_SBTL_FONT_ENC_GB
} IMTK_PB_CTRL_SBTL_FONT_ENC_TYPE_T;

typedef struct _IMTK_PB_CTRL_SBTL_FONT_ENC_T
{
    IMTK_PB_CTRL_SBTL_FONT_ENC_TYPE_T           eEncType;
    uint16_t                                    u2Param;
} IMTK_PB_CTRL_SBTL_FONT_ENC_T;

/* define a enum type to set border type of each sentence of lyric */
typedef enum
{
    IMTK_PB_CTRL_SBTL_BDR_TYPE_NULL = 0,
    IMTK_PB_CTRL_SBTL_BDR_TYPE_SOLID_LINE
} IMTK_PB_CTRL_SBTL_BDR_TYPE_T;

/* define a enum type to set roll type for multiline display mode */
typedef enum
{
    IMTK_PB_CTRL_SBTL_ROLL_TYPE_DEF = 0
} IMTK_PB_CTRL_SBTL_ROLL_TYPE_T;

/* set more than one attributes at a time with MM_SBTL_ATTR_T type */
typedef uint16_t IMTK_PB_CTRL_SBTL_ATTR_T;

typedef enum _IMTK_PB_CTRL_FNT_SIZE
{
    IMTK_PB_CTRL_FNT_SIZE_SMALL = 0,
    IMTK_PB_CTRL_FNT_SIZE_MEDIUM,
    IMTK_PB_CTRL_FNT_SIZE_LARGE,
    IMTK_PB_CTRL_FNT_SIZE_CUSTOM,
    IMTK_PB_CTRL_FNT_SIZE_NUMBER              /* used to count number */
} IMTK_PB_CTRL_FNT_SIZE;

typedef enum _IMTK_PB_CTRL_FNT_STYLE
{
#ifdef FE_DISABLE_EDGE_EFFECT_SUPPORT
    IMTK_PB_CTRL_FNT_STYLE_REGULAR        =   IMTK_PB_CTRL_MAKE_BIT_MASK_8 (0),
    IMTK_PB_CTRL_FNT_STYLE_ITALIC         =   IMTK_PB_CTRL_MAKE_BIT_MASK_8 (1),
    IMTK_PB_CTRL_FNT_STYLE_BOLD           =   IMTK_PB_CTRL_MAKE_BIT_MASK_8 (2),
    IMTK_PB_CTRL_FNT_STYLE_UNDERLINE      =   IMTK_PB_CTRL_MAKE_BIT_MASK_8 (3),
    IMTK_PB_CTRL_FNT_STYLE_STRIKEOUT      =   IMTK_PB_CTRL_MAKE_BIT_MASK_8 (4),
    IMTK_PB_CTRL_FNT_STYLE_OUTLINE        =   IMTK_PB_CTRL_MAKE_BIT_MASK_8 (5)
#else
    IMTK_PB_CTRL_FNT_STYLE_REGULAR        =   IMTK_PB_CTRL_MAKE_BIT_MASK_16 (0),
    IMTK_PB_CTRL_FNT_STYLE_ITALIC         =   IMTK_PB_CTRL_MAKE_BIT_MASK_16 (1),
    IMTK_PB_CTRL_FNT_STYLE_BOLD           =   IMTK_PB_CTRL_MAKE_BIT_MASK_16 (2),
    IMTK_PB_CTRL_FNT_STYLE_UNDERLINE      =   IMTK_PB_CTRL_MAKE_BIT_MASK_16 (3),
    IMTK_PB_CTRL_FNT_STYLE_STRIKEOUT      =   IMTK_PB_CTRL_MAKE_BIT_MASK_16 (4),
    IMTK_PB_CTRL_FNT_STYLE_OUTLINE        =   IMTK_PB_CTRL_MAKE_BIT_MASK_16 (5),
    IMTK_PB_CTRL_FNT_STYLE_SHADOW_RIGHT   =   IMTK_PB_CTRL_MAKE_BIT_MASK_16 (6),
    IMTK_PB_CTRL_FNT_STYLE_SHADOW_LEFT    =   IMTK_PB_CTRL_MAKE_BIT_MASK_16 (7),
    IMTK_PB_CTRL_FNT_STYLE_DEPRESSED      =   IMTK_PB_CTRL_MAKE_BIT_MASK_16 (8),
    IMTK_PB_CTRL_FNT_STYLE_RAISED         =   IMTK_PB_CTRL_MAKE_BIT_MASK_16 (9),
    IMTK_PB_CTRL_FNT_STYLE_UNIFORM        =   IMTK_PB_CTRL_MAKE_BIT_MASK_16 (10),
    IMTK_PB_CTRL_FNT_STYLE_BLURRED        =   IMTK_PB_CTRL_MAKE_BIT_MASK_16 (11)
#endif
} IMTK_PB_CTRL_FNT_STYLE;
#define IMTK_PB_CTRL_ENC_TAG(value, a, b, c, d)   value = (((uint32_t)(a) << 24) | ((uint32_t)(b) << 16) | ((uint32_t)(c) <<  8) | (uint32_t)(d))

typedef enum _IMTK_PB_CTRL_CMAP_ENCODING
{
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_NONE,           0,   0,   0,   0  ),
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_MS_SYMBOL,      's', 'y', 'm', 'b'),
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_UNICODE,        'u', 'n', 'i', 'c'),
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_SJIS,           's', 'j', 'i', 's'),
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_GB2312,         'g', 'b', ' ', ' '),
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_BIG5,           'b', 'i', 'g', '5'),
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_WANSUNG,        'w', 'a', 'n', 's'),
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_JOHAB,          'j', 'o', 'h', 'a'),
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_ADOBE_STANDARD, 'A', 'D', 'O', 'B'),
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_ADOBE_EXPERT,   'A', 'D', 'B', 'E'),
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_ADOBE_CUSTOM,   'A', 'D', 'B', 'C'),
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_ADOBE_LATIN_1,  'l', 'a', 't', '1'),
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_OLD_LATIN_2,    'l', 'a', 't', '2'),
    IMTK_PB_CTRL_ENC_TAG(IMTK_PB_CTRL_CMAP_ENC_APPLE_ROMAN,    'a', 'r', 'm', 'n')
} IMTK_PB_CTRL_CMAP_ENCODING;

typedef struct _IMTK_PB_CTRL_FONT_INFO_T
{
    IMTK_PB_CTRL_FNT_SIZE                     eFontSize;
    IMTK_PB_CTRL_FNT_STYLE                    eFontStyle;
    IMTK_PB_CTRL_CMAP_ENCODING                eFontCmap;
    char                                      acFontName[IMTK_PB_CTRL_MAX_FONT_NAME];
    int16_t                                   i2Width;                          
    uint8_t                                   u1CustomSize;
} IMTK_PB_CTRL_FONT_INFO_T;

typedef struct _IMTK_PB_CTRL_COLOR_T
{
    uint8_t   u1A;

    union {
        uint8_t   u1R;
        uint8_t   u1Y;
    } u1;

    union {
        uint8_t   u1G;
        uint8_t   u1U;
    } u2;
    
    union {
        uint8_t   u1B;
        uint8_t   u1V;
        uint8_t   u1Index;
    } u3;
} IMTK_PB_CTRL_COLOR_T;

typedef struct _IMTK_PB_CTRL_SBTL_ATTR_T
{
    IMTK_PB_CTRL_SBTL_ATTR_T            tAttrField;
    IMTK_PB_CTRL_SBTL_BDR_TYPE_T        eBdrType;
    IMTK_PB_CTRL_SBTL_ROLL_TYPE_T       eRollType;
    IMTK_PB_CTRL_FONT_INFO_T            tFontInfo;
    IMTK_PB_CTRL_COLOR_T                tBkgClr;
    IMTK_PB_CTRL_COLOR_T                tTxtClr;
    uint32_t                            u4BdrWidth;
    IMTK_PB_CTRL_RECT_T                 tDispRect;
} IMTK_PB_CTRL_SBTL_DISP_ATTR_T;

/* set subtitle to be show(TRUE) or hide(FALSE) */
typedef struct _IMTK_PB_CTRL_SBTL_SHOW_HIDE_T
{
    bool                      bSbtlShow;
} IMTK_PB_CTRL_SBTL_SHOW_HIDE_T;

typedef enum
{
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_UNKNOWN = 0,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_DEFAULT,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_DISP_MODE,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_HILT_STL ,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_TIME_OFST,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_FONT_ENC ,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_HILT_ATTR,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_SHOW_HIDE,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_FNT_INFO ,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_BKG_CLR  ,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_TXT_CLR  ,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_EDG_CLR  ,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_BDR_TYPE ,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_BDR_WIDTH,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_ROLL_TYPE,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_DISP_RECT,
    IMTK_PB_CTRL_SBTL_ATTR_TYPE_OSD_OFFSET
}IMTK_PB_CTRL_SBTL_ATTR_TYPE; 

typedef struct _IMTK_PB_CTRL_SBTL_ATTR
{
    IMTK_PB_CTRL_SBTL_ATTR_TYPE         eSbtlAttrType;
    union
    {
        IMTK_PB_CTRL_SBTL_DISP_MODE_T   tDispMode;
        IMTK_PB_CTRL_SBTL_HILT_STL_T    tHiltStl;
        IMTK_PB_CTRL_SBTL_TIME_OFST_T   tTmOfst;
        IMTK_PB_CTRL_SBTL_FONT_ENC_T    tFontEnc;
        IMTK_PB_CTRL_SBTL_SHOW_HIDE_T   tShowHide;
        IMTK_PB_CTRL_FONT_INFO_T        tFntInfo;
        IMTK_PB_CTRL_COLOR_T            tBkgClr;
        IMTK_PB_CTRL_COLOR_T            tTxtClr;
        IMTK_PB_CTRL_COLOR_T            tEdgClr;
        IMTK_PB_CTRL_SBTL_BDR_TYPE_T    eBdrType;
        uint32_t                        u4BdrWidth;
        IMTK_PB_CTRL_SBTL_ROLL_TYPE_T   eRollType;
        IMTK_PB_CTRL_RECT_T             tDispRect;
        IMTK_PB_CTRL_POINT_T            tOsdOffset;
    }uAttrValue;
    struct _IMTK_PB_CTRL_SBTL_ATTR*     ptNext;
}IMTK_PB_CTRL_SBTL_ATTR;

typedef struct 
{
   uint32_t             ui4_width;
   uint32_t             ui4_height;    

   uint8_t              ui1_par_w;
   uint8_t              ui1_par_h;
   bool                 b_src_asp;
} IMTK_PB_CTRL_MM_VIDEO_INFO_T;

typedef enum
{
    IMTK_PB_THUMBNAIL_RESULT_UNKNOWN     = 0,
    IMTK_PB_THUMBNAIL_RESULT_OK,
    IMTK_PB_THUMBNAIL_RESULT_FAIL,
} IMTK_PB_THUMBNAIL_RESULT_T;

typedef enum
{
    THUMBNAIL_COLORMODE_AYUV_CLUT2     = 0,
    THUMBNAIL_COLORMODE_AYUV_CLUT4     = 1,
    THUMBNAIL_COLORMODE_AYUV_CLUT8     = 2,
    THUMBNAIL_COLORMODE_UYVY_16        = 3,
    THUMBNAIL_COLORMODE_YUYV_16        = 4,
    THUMBNAIL_COLORMODE_AYUV_D8888     = 5,
    THUMBNAIL_COLORMODE_ARGB_CLUT2     = 6,
    THUMBNAIL_COLORMODE_ARGB_CLUT4     = 7,
    THUMBNAIL_COLORMODE_ARGB_CLUT8     = 8,
    THUMBNAIL_COLORMODE_RGB_D565       = 9,
    THUMBNAIL_COLORMODE_ARGB_D1555     = 10,
    THUMBNAIL_COLORMODE_ARGB_D4444     = 11,
    THUMBNAIL_COLORMODE_ARGB_D8888     = 12,
    THUMBNAIL_COLORMODE_YUV_420_BLK    = 13,
    THUMBNAIL_COLORMODE_YUV_420_RS     = 14,
    THUMBNAIL_COLORMODE_YUV_422_BLK    = 15,
    THUMBNAIL_COLORMODE_YUV_422_RS     = 16,
    THUMBNAIL_COLORMODE_YUV_444_BLK    = 17,
    THUMBNAIL_COLORMODE_YUV_444_RS     = 18
} IMTK_PB_THUMBNAIL_COLORMODE_T;

typedef struct
{
    uint8_t*                        u1CanvasBuffer;    /* should fill with share mem id */
    uint32_t                        u4BufLen;          /* should fill with share mem length */
    IMTK_PB_THUMBNAIL_COLORMODE_T   eCanvasColormode;
    uint32_t                        u4ThumbnailWidth;  /* in pixels */
    uint32_t                        u4ThumbnailHeight; /* in pixels */
}IMTK_PB_THUMBNAIL_INFO_T;


#if 1/* Capture Logo Related Start */
/* Type Define For Capture Source *******************************/
typedef enum
{
    IMTK_PB_CTRL_CAP_SRC_TYPE_DEFAULT = 0,
    IMTK_PB_CTRL_CAP_SRC_TYPE_TV_VIDEO,
    IMTK_PB_CTRL_CAP_SRC_TYPE_MM_VIDEO,
    IMTK_PB_CTRL_CAP_SRC_TYPE_MM_IMAGE,
    IMTK_PB_CTRL_CAP_SRC_TYPE_MM_IMAGE_ANDROID 
}   IMTK_PB_CTRL_CAP_SRC_TYPE_T;

/* Event Define For Capture Operation *******************************/
typedef enum
{
    IMTK_PB_CTRL_CAP_EVENT_TYPE_OPEN_DONE = 1,
    IMTK_PB_CTRL_CAP_EVENT_TYPE_OPEN_ERROR,
    IMTK_PB_CTRL_CAP_EVENT_TYPE_CAP_DONE,
    IMTK_PB_CTRL_CAP_EVENT_TYPE_CAP_ERR,
    IMTK_PB_CTRL_CAP_EVENT_TYPE_SAVE_DONE,
    IMTK_PB_CTRL_CAP_EVENT_TYPE_SAVE_ERROR,
    IMTK_PB_CTRL_CAP_EVENT_TYPE_DO_CAPTURING,
    IMTK_PB_CTRL_CAP_EVENT_TYPE_DO_SAVING 
    /*to be extend*/
}   IMTK_PB_CTRL_CAP_EVENT_TYPE_T;

/* Event Define For Capture Format *******************************/
typedef enum
{
    IMTK_PB_CTRL_CAP_IMG_FMT_TYPE_DEFAULT = 0,
    IMTK_PB_CTRL_CAP_IMG_FMT_TYPE_MPEG,
    IMTK_PB_CTRL_CAP_IMG_FMT_TYPE_JPEG,
    IMTK_PB_CTRL_CAP_IMG_FMT_TYPE_RAW
    /*to be extend*/ 
}   IMTK_PB_CTRL_CAP_IMG_FMT_TYPE_T;


/* Event Define For Capture Output Resolution *******************************/
typedef enum
{
    IMTK_PB_CTRL_CAP_OUT_RES_TYPE_SD = 0,
    IMTK_PB_CTRL_CAP_OUT_RES_TYPE_HD,
    IMTK_PB_CTRL_CAP_OUT_RES_TYPE_USER
    /*to be extend*/ 
}   IMTK_PB_CTRL_CAP_OUT_RES_TYPE_T;

typedef enum 
{
    /*For video*/
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_8BIT_RGB422 = 0,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_8BIT_RGB444,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_8BIT_YCbCr422,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_8BIT_YCbCr444,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_10BIT_RGB422,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_10BIT_RGB444,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_10BIT_YCbCr422,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_10BIT_YCbCr444,
    
    /*For photo*/
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_AYCbCr_CLUT2  = 10,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_AYCbCr_CLUT4,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_AYCbCr_CLUT8,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_CbYCrY_16,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_YCbYCr_16,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_AYCbCr_D8888,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_ARGB_CLUT2,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_ARGB_CLUT4,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_ARGB_CLUT8,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_RGB_D565,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_ARGB_D1555,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_ARGB_D4444,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_ARGB_D8888,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_YUV_420_BLK,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_YUV_420_RS,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_YUV_422_BLK,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_YUV_422_RS,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_YUV_444_BLK,
    IMTK_PB_CTRL_CAP_COLOR_FORMAT_YUV_444_RS
}IMTK_PB_CTRL_CAP_COLOR_FORMAT_T;

typedef struct 
{
	uint32_t	ui4_left;
	uint32_t	ui4_top;
	uint32_t 	ui4_right;
	uint32_t	ui4_bottom;
}IMTK_PB_CTRL_GL_RECT_T;


/* Capture information definition *******************************/
typedef struct 
{
    IMTK_PB_CTRL_CAP_IMG_FMT_TYPE_T     e_format;  
    uint32_t                            ui4_quality;
    uint32_t                            ui4_max_size;
    IMTK_PB_HANDLE_T                    h_src_surf;/* For capture photo use*/
    IMTK_PB_HANDLE_T                    h_working_surf;/* For capture photo use*/
    IMTK_PB_CTRL_CAP_OUT_RES_TYPE_T     e_res_type;
    uint8_t                             ui1_video_path;
    IMTK_PB_CTRL_GL_RECT_T              t_rec;
    uint32_t                            ui4_moveable_width;
    uint32_t                            ui4_moveable_height;
	//for android image
	uint32_t	ui4_image_width;
	uint32_t	ui4_image_height;
	uint32_t	ui4_image_pitch;
	IMTK_PB_CTRL_CAP_COLOR_FORMAT_T  e_colormode;
	int32_t		i4_shm_id;
    union 
    {/* used for IMG_CAP_RESOLUTION(HW & SW) */        
        struct 
        {
            uint32_t  ui4_res_width;
            uint32_t  ui4_res_height;
        } t_resolution;
    }u;
} IMTK_PB_CTRL_CAP_CAPTURE_INFO_T;


typedef struct 
{
    bool        b_default;      /* enable default boot logo */
    bool        b_default_exist;/* is default boot logo exist*/
    uint8_t     ui1_cur_logo_index;
    uint8_t     ui1_nums_logo_slots;
    uint16_t    ui2_logo_valid_tag;
  /*to be extend*/
} IMTK_PB_CTRL_CAP_CAPABILITY_INFO_T;

typedef struct 
{
    uint8_t     ui1_red;
    uint8_t     ui1_green;
    uint8_t     ui1_blue;
  /*to be extend*/
} IMTK_PB_CTRL_BG_COLOR_T;


/* Event Define For Capture Output Resolution *******************************/
typedef enum
{
    IMTK_PB_CTRL_CAP_DEVICE_TYPE_DEFAULT,
    IMTK_PB_CTRL_CAP_DEVICE_TYPE_EXTERNAL,
    IMTK_PB_CTRL_CAP_DEVICE_TYPE_INTERNAL
    /*to be extend*/ 
} IMTK_PB_CTRL_CAP_DEVICE_TYPE_T;


/* Capture information definition *******************************/
typedef struct 
{
    IMTK_PB_CTRL_CAP_DEVICE_TYPE_T       e_device_type;  
    union 
    {
        uint32_t    ui4_logo_id;
        char*       ps_path;
    } u;
} IMTK_PB_CTRL_CAP_LOGO_SAVE_INFO_T;

typedef IMTK_PB_CTRL_CAP_LOGO_SAVE_INFO_T IMTK_PB_CTRL_CAP_LOGO_SELECT_INFO_T;

typedef void (*IMtkPb_Ctrl_Cap_Nfy_Fct) (
                        IMTK_PB_HANDLE_T                h_handle,
                                const void*             pv_tag,
                   IMTK_PB_CTRL_CAP_EVENT_TYPE_T        e_event,
                                const void*             pv_data);
typedef bool (*IMtkPb_Ctrl_Cap_Custom_Nfy_Fct) (
                   IMTK_PB_CTRL_CAP_SRC_TYPE_T          e_source,
                   IMTK_PB_CTRL_CAP_EVENT_TYPE_T        e_event,
                                const void*             pv_data); 


extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_CapOpen(
                            IMTK_PB_CTRL_CAP_SRC_TYPE_T     e_source,
                            IMtkPb_Ctrl_Cap_Nfy_Fct         pf_nfy,
                            const void*                     pv_nfy_tag,
                            IMtkPb_Ctrl_Cap_Custom_Nfy_Fct  pf_cust,
                            const void*                     pv_cust_tag,
                            IMTK_PB_HANDLE_T*               ph_cap);
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_CapCapture(
                                    IMTK_PB_HANDLE_T                        h_cap,
                                    const IMTK_PB_CTRL_CAP_CAPTURE_INFO_T*  pt_cap_info);
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_CapGetData(IMTK_PB_HANDLE_T    h_cap,
                                            uint8_t**           pBuf,
                                            uint32_t*           len);
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_CapClose(IMTK_PB_HANDLE_T  h_cap);
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_CapSave(IMTK_PB_HANDLE_T                    h_cap,
                                         const IMTK_PB_CTRL_CAP_LOGO_SAVE_INFO_T*   pt_save_inf);
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_CapSyncStopCap(IMTK_PB_HANDLE_T   h_cap);
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_CapSyncStopSave(IMTK_PB_HANDLE_T   h_cap);
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_CapSelectAsBootLogo(
                        const IMTK_PB_CTRL_CAP_LOGO_SELECT_INFO_T* pt_select_info);
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_CapQueryCapability(
                                IMTK_PB_CTRL_CAP_CAPABILITY_INFO_T*  pt_cap_info);
#endif

typedef enum
{   
    IMTK_PB_CTRL_PROFILE_DEFAULT = 0,
    IMTK_PB_CTRL_PROFILE_MAIN_SVCTX = IMTK_PB_CTRL_PROFILE_DEFAULT,
    IMTK_PB_CTRL_PROFILE_SUB_SVCTX,
    IMTK_PB_CTRL_PROFILE_THRD_SVCTX,
    IMTK_PB_CTRL_PROFILE_NETFLIX_NRD,
    IMTK_PB_CTRL_PROFILE_MAIN_MP3_ONLY,
    IMTK_PB_CTRL_PROFILE_SKYPE_MAIN_VIDEO,
    IMTK_PB_CTRL_PROFILE_SKYPE_SUB_VIDEO,
    IMTK_PB_CTRL_PROFILE_SKYPE_REMOTE_AUDIO,
    IMTK_PB_CTRL_PROFILE_MAIN_PUSH_ON_SUB,
    IMTK_PB_CTRL_PROFILE_MAIN_PUSH_MHP,
    IMTK_PB_CTRL_PROFILE_MAIN_PUSH_MHP_SUB,
    IMTK_PB_CTRL_PROFILE_SUB_FREE_RUN,
    IMTK_PB_CTRL_PROFILE_AUTO_DETECT_NEXT_VID,
    IMTK_PB_CTRL_PROFILE_VUDU
}   IMTK_PB_CTRL_PROFILE_TYPE_T;


typedef enum
{
    IMTK_PB_CTRL_URL_TYPE_PROXY = 0,
    IMTK_PB_CTRL_URL_TYPE_AGENT,
    IMTK_PB_CTRL_URL_TYPE_COOKIE,
    IMTK_PB_CTRL_URL_TYPE_EXT_SBTL_FULL_PATH,
    IMTK_PB_CTRL_URL_TYPE_LYRIC_FULL_PATH,
    IMTK_PB_CTRL_URL_TYPE_MAX
}   IMTK_PB_CTRL_URL_TYPE_T;

/*-----------------------------------------------------------------------------
                    functions declarations
-----------------------------------------------------------------------------*/

/*! @brief          Gets Subtitle track info.
 *  @param [in]     hHandle         The handle of playback control instance.
 *  @param [in]     u4TrackNum      The video track number.
 *  @param [out]    ptInfo          References to the subtitle track information.
 *  @return         Return the error code
 *  @retval         IMTK_PB_ERROR_CODE_OK                       Success.
 *  @retval         IMTK_PB_ERROR_CODE_INV_HANDLE               The hHandle is invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INV_ARG                  The parameters are invalid.
 *  @retval         IMTK_PB_ERROR_CODE_INSUFFICIENT_MEMORY      Not enough memory resource.
 *  @retval         IMTK_PB_ERROR_CODE_NOT_INIT                 The playback control is not initialized yet.
 *  @note           only used in Lib Master model.  MtkPbLib will try to get the info, but is not guaranteed.  If an info entry is not available, MtkPbLib shall fill it with ::IMTK_PB_UNKNOWN_VALUE.
 *  @see            IMtkPb_Ctrl_GetSubTrackInfo()
 */
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetSubTrackInfo(IMTK_PB_HANDLE_T                    hHandle,
                                                        uint32_t                            u4TrackNum,
                                                        IMTK_PB_CTRL_SUBTITLE_INFO_T*       ptInfo);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetSubtitleShowHide(IMTK_PB_HANDLE_T        hHandle,
                                                        bool    b_sbtl_show_hide);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetSubtitleTrack(IMTK_PB_HANDLE_T            hHandle,
                                                         uint16_t                    u2STrack);
                                                  
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetTimeshiftBufRange(IMTK_PB_HANDLE_T                       hHandle,
                                                             IMTK_PB_CTRL_REC_BUF_RANGE_INFO_T*     ptRangeInfo);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetTimeshiftCtrlBlk(IMTK_PB_HANDLE_T                hHandle,
                                                            IMTK_PB_TICK_CTRL_BLK_T*        ptCtrlBlk);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetDivxDRMInfo(IMTK_PB_HANDLE_T                 hHandle,
                                                       IMTK_PB_CTRL_DIVX_DRM_INFO_T*    ptDRMInfo);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_DivxDRMDeactivation(IMTK_PB_HANDLE_T                            hHandle,
                                                            IMTK_PB_CTRL_DIVX_DRM_DEACTIVATION_INFO_T*  ptDeactivation);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetDivxDRMRW(IMTK_PB_HANDLE_T                       hHandle,
                                                     IMTK_PB_CTRL_DIVX_DRM_MEM_RW_T*        ptDrmMemRW);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetASP(IMTK_PB_HANDLE_T     hHandle,
                                               IMTK_PB_CTRL_ASP_T*  ptAsp);
/*
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetMetaInfo(IMTK_PB_HANDLE_T        hHandle,
                                                    IMTK_PB_CTRL_META_T*    ptMeta);
*/
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetNavigateInfo(IMTK_PB_HANDLE_T                hHandle,
                                                        IMTK_PB_CTRL_NAV_INFO_T*        ptNavInfo);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetAFifoFullness(IMTK_PB_HANDLE_T      hHandle, 
                                                   uint32_t*             pui4_data_sz);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_ChapSeek(IMTK_PB_HANDLE_T   hHandle,
                                                 uint32_t           u4PlaylistIdx,
                                                 uint32_t           u4ChapIdx);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetNavigateInfo(IMTK_PB_HANDLE_T    hHandle,
                                                        uint32_t            u4TitleIdx,
                                                        uint32_t            u4PlaylistIdx,
                                                        uint32_t            u4ChapIdx);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_ByteSeek(IMTK_PB_HANDLE_T   hHandle, 
                                                 uint64_t           u8Pos);
/*add cmpb pts seek*/
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_ByteSeekPts(IMTK_PB_HANDLE_T   hHandle, 
                                                 uint64_t           u8Pos,
                                                 uint64_t           u8Pts);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Get(IMTK_PB_HANDLE_T            hHandle,
                                            IMTK_PB_CTRL_GET_TYPE_T     e_set_type,
                                            void*                       pv_set_info,
                                            uint32_t                    z_set_info_size);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetVideoResolution(IMTK_PB_HANDLE_T                   hHandle,
                                              uint32_t*     ptWidth,
                                              uint32_t*     ptHeight);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Set(IMTK_PB_HANDLE_T            hHandle,
                                            IMTK_PB_CTRL_SET_TYPE_T     e_get_type,
                                            void*                       pv_get_info,
                                            uint32_t*                   pz_get_info_size);
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetProgram(IMTK_PB_HANDLE_T            hHandle,
                                            IMTK_PB_CTRL_SET_TYPE_T     e_get_type,
                                            void*                       pv_get_info,
                                            uint32_t*                   pz_get_info_size);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetEngineParamAsync(IMTK_PB_HANDLE_T             hHandle,     
                                                            IMTK_PB_CTRL_ENGINE_PARAM_T*   ptParam);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetSubtitleInfo(IMTK_PB_HANDLE_T                hHandle, 
                                                              IMTK_PB_CTRL_SUBTITLE_INFO_T*   ptSubInfo);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_ChangeSubtitle(IMTK_PB_HANDLE_T              hHandle, 
                                                       IMTK_PB_CTRL_SUBTITLE_INFO_T* ptSubInfo);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetEncryptInfo(IMTK_PB_HANDLE_T                            hHandle,
                                                              IMTK_PB_CTRL_IBC_PARAM_SET_ENCRYPT_INFO*    ptInfo);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetBufferStatus_Ext(IMTK_PB_HANDLE_T                   hHandle, 
                                                                     IMTK_PB_CTRL_BUFFER_STATUS_EXT_T*  ptBufferStatus);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetCurAudTrackIndex(IMTK_PB_HANDLE_T      hHandle,
                                                            uint16_t*             pu2ATrack);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetTSInfo( IMTK_PB_HANDLE_T                    hHandle,
                                                    uint8_t*                           pui1_buf, 
                                                    uint32_t                           ui4_buf_len,
                                                    IMTK_PB_CTRL_TS_SINGLE_PAT_INFO_T* ptTSInfo);
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetURL(IMTK_PB_HANDLE_T            hHandle,
                                        IMTK_PB_CTRL_URL_TYPE_T     e_url_type,
                                        uint8_t*                    pu1String);


extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Set_DRM_Info(IMTK_PB_HANDLE_T           hHandle,
                                                     IMTK_PB_CTRL_DRM_INFO_T*   pt_drm_inf);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetAudioVolume(IMTK_PB_CTRL_AUD_VOLUME_INFO_T t_volume_info);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetAudioVolume(IMTK_PB_CTRL_AUD_VOLUME_INFO_T* t_volume_info);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetThumbNail(IMTK_PB_HANDLE_T                hHandle,
                                                     IMTK_PB_THUMBNAIL_INFO_T*       ptThumbnail);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetSHM( IMTK_PB_HANDLE_T    hHandle,
                                                uint32_t            ui4_buf_len,
                                                void**              ppv_phy_addr,
                                                void**              ppv_vir_addr);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_FreeSHM( IMTK_PB_HANDLE_T    hHandle,
                                                 uint32_t            ui4_buf_len,
                                                 void*               pv_phy_addr,
                                                 void*               pv_vir_addr);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Get_BG_Color(IMTK_PB_CTRL_BG_COLOR_T* pt_clr);
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Set_BG_Color(IMTK_PB_CTRL_BG_COLOR_T* pt_clr);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Get_PushBufCnt(IMTK_PB_HANDLE_T    hHandle,
                                                       int32_t*            pi4_cnt);

extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetPlayStatus(void);
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetAudioMute(bool b_mute);
extern IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Get_File_Seekable(IMTK_PB_HANDLE_T    hHandle,
                                                           bool*              bSeekable);


#ifdef __cplusplus
}
#endif

#endif /* _I_MTK_PB_CTRL_DTV_ */
