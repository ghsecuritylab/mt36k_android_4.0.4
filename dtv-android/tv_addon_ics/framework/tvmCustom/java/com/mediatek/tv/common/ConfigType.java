package com.mediatek.tv.common;

/**
*
*for the configuration type definition
*
*/
public class ConfigType extends BaseConfigType {


private static String CFG_GRP_PSWD_PREFIX     = "grp_pswd_";
private static String CFG_GRP_MENU_PREFIX     = "grp_menu_";
private static String CFG_GRP_NAV_PREFIX      = "grp_nav_";
private static String CFG_GRP_AUDIO_PREFIX    = "grp_audio_";
private static String CFG_GRP_DISP_PREFIX     = "grp_disp_";
private static String CFG_GRP_VIDEO_PREFIX    = "grp_video_";
private static String CFG_GRP_BS_PREFIX       = "grp_bs_";
private static String CFG_GRP_VGA_PREFIX      = "grp_vga_";
private static String CFG_GRP_TIME_PREFIX     = "grp_time_";
private static String CFG_GRP_FAC_PREFIX      = "grp_fac_";
private static String CFG_GRP_CEC_PREFIX      = "grp_cec_";
private static String CFG_GRP_MISC_PREFIX     = "grp_misc_";
private static String CFG_GRP_D_AUD_PREFIX    = "grp_d_aud";
private static String CFG_GRP_D_TUNER_PREFIX  = "grp_d_tuner";
private static String CFG_GRP_D_VID_PREFIX    = "grp_d_vid";
private static String CFG_GRP_D_VD_PREFIX     = "grp_d_vd";
private static String CFG_GRP_D_EXTMJC_PREFIX = "grp_d_extmjc";
private static String CFG_GRP_D_MISC_PREFIX   = "grp_d_misc";
private static String CFG_GRP_D_CUST_PREFIX   = "grp_d_cust";












/*-----------------------------------------------------------------------------

                       GROUP MENU

-----------------------------------------------------------------------------*/

/**

 * for tv configuration to get/set/ channel freeze change

 *Range:0~1  def:0

 */

public static String CFG_CH_FRZ_CHG                   = CFG_GRP_MENU_PREFIX + "channel_frz";


/*-----------------------------------------------------------------------------

                       GROUP NAV

-----------------------------------------------------------------------------*/

/**

 * for tv configuration to get/set/update input_main

 *Range:  def:0

 */

public static String CFG_INPUT_MAIN                   = CFG_GRP_NAV_PREFIX + "input_main";


/*-----------------------------------------------------------------------------

                       GROUP AUDIO

-----------------------------------------------------------------------------*/

/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update equalize 120hz

 *Range:-60~60  def:0

 */

public static String CFG_EQUALIZE_120HZ               = CFG_GRP_AUDIO_PREFIX + "equalize_120hz";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update equalize 500hz

 *Range:-60~60  def:0

 */

public static String CFG_EQUALIZE_500HZ               = CFG_GRP_AUDIO_PREFIX + "equalize_500hz";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update equalize 1500hz

 *Range:-60~60  def:0

 */

public static String CFG_EQUALIZE_1500HZ              = CFG_GRP_AUDIO_PREFIX + "equalize_1500hz";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update equalize 5khz

 *Range:-60~60  def:0

 */

public static String CFG_EQUALIZE_5KHZ                = CFG_GRP_AUDIO_PREFIX + "equalize_5khz";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update equalize 10khz

 *Range:-60~60  def:0

 */

public static String CFG_EQUALIZE_10KHZ               = CFG_GRP_AUDIO_PREFIX + "equalize_10khz";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update audio_curve max

 *Range:0~100  def:0

 */

public static String CFG_AUDIO_CURVE_MAX              = CFG_GRP_AUDIO_PREFIX + "fac_audio_max";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update audio_curve 80

 *Range:0~100  def:0

 */

public static String CFG_AUDIO_CURVE_80               = CFG_GRP_AUDIO_PREFIX + "fac_audio_80";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update audio_curve middle

 *Range:0~100  def:0

 */

public static String CFG_AUDIO_CURVE_MID              = CFG_GRP_AUDIO_PREFIX + "fac_audio_mid";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update audio_curve 20

 *Range:0~100  def:0

 */

public static String CFG_AUDIO_CURVE_20               = CFG_GRP_AUDIO_PREFIX + "fac_audio_20";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update audio_curve min

 *Range:0~100  def:0

 */

public static String CFG_AUDIO_CURVE_MIN              = CFG_GRP_AUDIO_PREFIX + "fac_audio_min";


/**

 *for tv configuration to set micphone

 */

public static String CFG_AUDIO_MICPHONE              = CFG_GRP_AUDIO_PREFIX + "audio_micphone";


 /**

  *for tv configuration to set karaok micphone volume

  */

public static String CFG_AUDIO_KARAOK_MICPHONE_VOLUME     = CFG_GRP_AUDIO_PREFIX + "karaok_micphone_volume";


   /**

  *for tv configuration to set karaok backgroud music volume

  */

public static String CFG_AUDIO_KARAOK_BACKGROUND_VOLUME     = CFG_GRP_AUDIO_PREFIX + "karaok_background_volume";


/*-----------------------------------------------------------------------------

                   GROUP DISP

-----------------------------------------------------------------------------*/

/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update aspect_ratio

 */

public static String CFG_ASPECT_RATIO                 = CFG_GRP_DISP_PREFIX + "aspect_ratio";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update factory back light bright value

 *Range:0~100  def:0

 */

public static String CFG_FAC_BL_BRI                   = CFG_GRP_DISP_PREFIX + "fac_bl_bri";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update factory back light soft value

 *Range:0~100  def:0

 */

public static String CFG_FAC_BL_SOFT                  = CFG_GRP_DISP_PREFIX + "fac_bl_soft";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update factory back light optical 1 value

 *Range:0~100  def:0

 */

public static String CFG_FAC_BL_OPT_1                 = CFG_GRP_DISP_PREFIX + "fac_bl_opt_1";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update factory back light optical 2 value

 *Range:0~100  def:0

 */

public static String CFG_FAC_BL_OPT_2                 = CFG_GRP_DISP_PREFIX + "fac_bl_opt_2";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update factory back light optical 3 value

 *Range:0~100  def:0

 */

public static String CFG_FAC_BL_OPT_3                 = CFG_GRP_DISP_PREFIX + "fac_bl_opt_3";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update factory back light optical 4 value

 *Range:0~100  def:0

 */

public static String CFG_FAC_BL_OPT_4                 = CFG_GRP_DISP_PREFIX + "fac_bl_opt_4";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update factory back light optical 5 value

 *Range:0~100  def:0

 */

public static String CFG_FAC_BL_OPT_5                 = CFG_GRP_DISP_PREFIX + "fac_bl_opt_5";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update factory back light optical 6 value

 *Range:0~100  def:0

 */

public static String CFG_FAC_BL_OPT_6                 = CFG_GRP_DISP_PREFIX + "fac_bl_opt_6";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update factory back light user value

 *Range:0~100  def:0

 */

public static String CFG_FAC_BL_USER                  = CFG_GRP_DISP_PREFIX + "fac_bl_user";
    

/*-----------------------------------------------------------------------------

                   GROUP VIDEO

-----------------------------------------------------------------------------*/



/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update cti

 * Range:VID_CTI_OFF~VID_CTI_STRONG    def:VID_CTI_MEDIUM

 */

public static String CFG_CTI                          = CFG_GRP_VIDEO_PREFIX + "cti";


public static String CFG_TEMPERATURE                  = CFG_GRP_VIDEO_PREFIX + "temperature";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update color_gain

 */

public static String CFG_COLOR_GAIN                   = CFG_GRP_VIDEO_PREFIX + "color_gain";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update color_offset

 */



/**

 *@deprecated

 *turnkey will remove this type

  * for tv configuration to get/set/update wme_mode

 *Range:COMMON_OFF~COMMON_ON  def:COMMON_OFF

 */

public static String CFG_WME_MODE                     = CFG_GRP_VIDEO_PREFIX + "wme_mode";
/**

  *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update memc

 *Range:COMMON_OFF~COMMON_ON  def:COMMON_OFF     

 */

public static String CFG_MEMC                         = CFG_GRP_VIDEO_PREFIX + "memc";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update memc_demo

 *Range:COMMON_OFF~COMMON_ON  def:COMMON_OFF     

 */

public static String CFG_MEMC_DEMO                    = CFG_GRP_VIDEO_PREFIX + "memc_demo";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update dlc

 */

public static String CFG_DLC                          = CFG_GRP_VIDEO_PREFIX + "dlc";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update image_analogy

 */

public static String CFG_IMAGEANALOGY                 = CFG_GRP_VIDEO_PREFIX + "image_analogy";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve max brightness value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_BRI_MAX          = CFG_GRP_VIDEO_PREFIX + "fac_bri_max";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve min brightness value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_BRI_MIN          = CFG_GRP_VIDEO_PREFIX + "fac_bri_min";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve middle brightness value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_BRI_MID          = CFG_GRP_VIDEO_PREFIX + "fac_bri_mid";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve max contrast value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_CNT_MAX          = CFG_GRP_VIDEO_PREFIX + "fac_cnt_max";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve min contrast value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_CNT_MIN          = CFG_GRP_VIDEO_PREFIX + "fac_cnt_min";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve middle contrast value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_CNT_MID          = CFG_GRP_VIDEO_PREFIX + "fac_cnt_mid";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve max video value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_VID_MAX          = CFG_GRP_VIDEO_PREFIX + "fac_vid_max";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve min video value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_VID_MIN          = CFG_GRP_VIDEO_PREFIX + "fac_vid_min";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve middle video value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_VID_MID          = CFG_GRP_VIDEO_PREFIX + "fac_vid_mid";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve max hue value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_HUE_MAX          = CFG_GRP_VIDEO_PREFIX + "fac_hue_max";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve min hue value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_HUE_MIN          = CFG_GRP_VIDEO_PREFIX + "fac_hue_min";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve middle hue value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_HUE_MID          = CFG_GRP_VIDEO_PREFIX + "fac_hue_mid";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve max sharpness value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_SHP_MAX          = CFG_GRP_VIDEO_PREFIX + "fac_shp_max";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve min sharpness value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_SHP_MIN          = CFG_GRP_VIDEO_PREFIX + "fac_shp_min";
/**

 *@deprecated

 *turnkey will remove this type

 * for tv configuration to get/set/update video_curve middle sharpness value

 *Range:0~255  def:0

 */

public static String CFG_VIDEO_CURVE_SHP_MID          = CFG_GRP_VIDEO_PREFIX + "fac_shp_mid";


/**

 *caosm add for dynamic backlight   Return: 0or1

 */

public static String CFG_NATURELIGHT                = CFG_GRP_VIDEO_PREFIX + "naturelight";
public static String CFG_NATURELIGHT_FUNC      = CFG_GRP_VIDEO_PREFIX + "naturelight_func";
/**

  *caosm add for tv-sound set eq 

  */

public static String CFG_SOUND_SCENE_FUNC     = CFG_GRP_AUDIO_PREFIX + "sound_scene_func";
 /**

 *zhouzhen add for judge 4k2k   Return: 0or1

 */

public static String CFG_IS4K2K_FUNC                = CFG_GRP_VIDEO_PREFIX + "is4k2k_func";
 

  /**

 *lizhyuan add for judge dtmb   Return: 0or1

 */

public static String CFG_IS4DTMB_FUNC                = CFG_GRP_VIDEO_PREFIX + "isdtmb_func";
 

/**

  *add for settign audio mute 

  */

public static String CFG_MUTE_VOLUME_FUNC     = CFG_GRP_AUDIO_PREFIX + "mute_volume_func";
/**

  * caosm for tv memc-func

  */

public static String CFG_MEMC_DEMO_FUNC       = CFG_GRP_VIDEO_PREFIX + "memc_demo_func";
 /**

  *caosm add for wifi on/off

  */

public static String CFG_WIFI_SUPPLY  		  = CFG_GRP_DISP_PREFIX + "wifi_supply";
public static String CFG_WIFI_SUPPLY_FUNC     = CFG_GRP_DISP_PREFIX + "wifi_supply_func";
/**

 *caosm add for language

 */

public static String CFG_LANGUAGE			  = CFG_GRP_DISP_PREFIX + "language";
/**

 *caosm add for underground

 */

public static String CFG_BACKGROUND          = CFG_GRP_DISP_PREFIX + "underground";
/**

 *caosm add for auto-choose

 */

public static String CFG_AUTO_CHOOSE 		  = CFG_GRP_DISP_PREFIX + "auto_choose";


 /*added by zhong  */

public static String CFG_FIRST_DIANDU 		  = CFG_GRP_DISP_PREFIX + "first_diandu";
public static String CFG_DIANDU_SAVE_ADDR 		  = CFG_GRP_DISP_PREFIX + "save_addr";
public static String CFG_DIANDU_START_NOQUESTION 		  = CFG_GRP_DISP_PREFIX + "no_question";




 //SUPPORT_3D_PIC_MODE for zhoutao



public static String CFG_3D_SATURATION      = CFG_GRP_VIDEO_PREFIX + "3d_sat";
public static String CFG_3D_BRIGHTNESS     = CFG_GRP_VIDEO_PREFIX + "3d_bri";
public static String CFG_3D_CONTRAST      = CFG_GRP_VIDEO_PREFIX + "3d_cnt";
public static String CFG_3D_COLOR_TEMP      = CFG_GRP_VIDEO_PREFIX + "3d_clr_temp";
public static String CFG_3D_PIC_SIZE      = CFG_GRP_VIDEO_PREFIX + "3d_pic_size";
public static String CFG_3D_PIC_MODE      = CFG_GRP_VIDEO_PREFIX + "3d_pic_mode";


public static String CFG_SYS_REBOOT      = CFG_GRP_VIDEO_PREFIX + "sys_reboot";


public static String CFG_VID_VIEW          = CFG_GRP_VIDEO_PREFIX + "view";
public static String CFG_VID_UIBACKLIGHT	 = CFG_GRP_VIDEO_PREFIX + "uibacklight";


public static String CFG_PQ_DEMO_FLAG_FUNC    = CFG_GRP_D_CUST_PREFIX + "pq_demo_func";
/*-----------------------------------------------------------------------------

                   GROUP BROADCAST SERVICE

-----------------------------------------------------------------------------*/ 



    

/*-----------------------------------------------------------------------------

                   GROUP VGA

-----------------------------------------------------------------------------*/



public static String CFG_PHASE                        = CFG_GRP_VIDEO_PREFIX + "__phase";
public static String CFG_CLOCK                        = CFG_GRP_VIDEO_PREFIX + "__clock";




/*-----------------------------------------------------------------------------

                   GROUP Factory

-----------------------------------------------------------------------------*/

//lulu_1008

public static String CFG_POWER_CTRL                   = CFG_GRP_FAC_PREFIX + "power_ctrl";
public static String CFG_POWER_CTRL_FUNC              = CFG_GRP_D_CUST_PREFIX + "_power_ctrl_func";


    

public static String CFG_DMODE_HOT_KEY                      = CFG_GRP_VIDEO_PREFIX + "designe_hotkey";
public static String CFG_PMODE_HOT_KEY                      = CFG_GRP_VIDEO_PREFIX + "factory_hotkey";
public static String CFG_WARM_STATUS                        = CFG_GRP_VIDEO_PREFIX + "warm_status";
public static String CFG_FAC_MAC_ADDR                       = CFG_GRP_VIDEO_PREFIX + "mac_addr";
public static String CFG_FAC_DEVICE_ID                      = CFG_GRP_VIDEO_PREFIX + "device_id";
public static String CFG_FAC_ACTIVE_KEY                     = CFG_GRP_VIDEO_PREFIX + "active_key";
public static String CFG_FAC_POTAL_FLAG                     = CFG_GRP_VIDEO_PREFIX + "portal_flag";
public static String CFG_FAC_AUTO_P_MODE                    = CFG_GRP_VIDEO_PREFIX + "auto_p_mode";
public static String CFG_FAC_SCAN_CHANNEL_SPECIAL           = CFG_GRP_VIDEO_PREFIX + "channel_special";
public static String CFG_FAC_LOGO_SELECT                    = CFG_GRP_VIDEO_PREFIX + "logo_select";
public static String CFG_FAC_FAC_TEST_STATUS_ADDR           = CFG_GRP_VIDEO_PREFIX + "addr_test";
public static String CFG_FAC_KEY_FLAG                       = CFG_GRP_VIDEO_PREFIX + "key_flag";
public static String CFG_FAC_SN                             = CFG_GRP_VIDEO_PREFIX + "sn";
public static String CFG_FAC_WR_ADDR				           = CFG_GRP_FAC_PREFIX + "wr_addr";
public static String CFG_FAC_WR_DATA				           = CFG_GRP_FAC_PREFIX + "wr_data";
public static String CFG_FAC_WR_HDCP				           = CFG_GRP_FAC_PREFIX + "hdcp_key";
public static String CFG_FAC_MAC_HDCP_ID				       = CFG_GRP_FAC_PREFIX + "mac_device_hdcp_status";
public static String CFG_FAC_SW				               = CFG_GRP_FAC_PREFIX + "software_ver";
public static String CFG_FAC_USB_CLONE				       = CFG_GRP_FAC_PREFIX + "usb_clone";
public static String CFG_FAC_FAC_KEY				           = CFG_GRP_FAC_PREFIX + "remout_key";
public static String CFG_FAC_LED_CTRL			                 = CFG_GRP_FAC_PREFIX + "led_ctrl";
public static String CFG_FAC_BLUETOOTH_CTRL              = CFG_GRP_VIDEO_PREFIX + "bluetooth_ctrl";
public static String CFG_PROJECT_ID                         = CFG_GRP_VIDEO_PREFIX + "project_id";
public static String CFG_FAC_BP                             = CFG_GRP_VIDEO_PREFIX + "dbc_bp";
public static String CFG_FAC_CP                             = CFG_GRP_VIDEO_PREFIX + "dbc_cp";
public static String CFG_FAC_APL                            = CFG_GRP_VIDEO_PREFIX + "dbc_apl";
public static String CFG_PROJECT_ID_FUNC                    = CFG_GRP_FAC_PREFIX + "project_id_func";
public static String CFG_FAC_BP_FUNC                        = CFG_GRP_FAC_PREFIX + "dbc_bp_func";
public static String CFG_FAC_CP_FUNC                        = CFG_GRP_FAC_PREFIX + "dbc_cp_func";
public static String CFG_FAC_APL_FUNC                       = CFG_GRP_FAC_PREFIX + "dbc_apl_func";
public static String CFG_VID_DB_FUNC				           = CFG_GRP_FAC_PREFIX + "dbc_db_func";
public static String CFG_VID_DC_FUNC				           = CFG_GRP_FAC_PREFIX + "dbc_dc_func";
public static String CFG_FAC_APL_1						   = CFG_GRP_FAC_PREFIX + "dbc_apl_1";
public static String CFG_FAC_APL_1_FUNC                     = CFG_GRP_FAC_PREFIX + "dbc_apl_1_func";
public static String CFG_FAC_DBC_PRINT_FLAG                 = CFG_GRP_VIDEO_PREFIX + "dbc_print_flag";
public static String CFG_FAC_DBC_PRINT_FLAG_FUNC            = CFG_GRP_FAC_PREFIX + "dbc_print_flag_func";
public static String CFG_FAC_STD_BACKLIGHT                  = CFG_GRP_VIDEO_PREFIX + "dbc_standard_backlight";
public static String CFG_FAC_STD_BACKLIGHT_FUNC             = CFG_GRP_FAC_PREFIX + "dbc_standard_backlight_func";
public static String CFG_STORE_UUID                         = CFG_GRP_FAC_PREFIX + "store_uuid";


//2013 1 5 add for tuner liuqiao

public static String CFG_FAC_TUNER_STATUS				        = CFG_GRP_FAC_PREFIX + "tuner_status";
public static String CFG_FAC_TUNER_TMP				               = CFG_GRP_FAC_PREFIX + "tuner_tmp";
public static String CFG_FAC_TUNER_VL0				               = CFG_GRP_FAC_PREFIX + "tuner_vl0";
public static String CFG_FAC_TUNER_VL1				               = CFG_GRP_FAC_PREFIX + "tuner_vl1";
public static String CFG_FAC_TUNER_VH0				               = CFG_GRP_FAC_PREFIX + "tuner_vh0";
public static String CFG_FAC_TUNER_VH1				               = CFG_GRP_FAC_PREFIX + "tuner_vh1";
public static String CFG_FAC_TUNER_UL0				               = CFG_GRP_FAC_PREFIX + "tuner_ul0";
public static String CFG_FAC_TUNER_UL1				               = CFG_GRP_FAC_PREFIX + "tuner_ul1";
public static String CFG_FAC_TUNER_UH0				               = CFG_GRP_FAC_PREFIX + "tuner_uh0";
public static String CFG_FAC_TUNER_UH1				               = CFG_GRP_FAC_PREFIX + "tuner_uh1";
//2013 1 29

public static String CFG_FAC_PATTERN_R				               = CFG_GRP_FAC_PREFIX + "pattern_r";
public static String CFG_FAC_PATTERN_G					         = CFG_GRP_FAC_PREFIX + "pattern_g";
public static String CFG_FAC_PATTERN_B					           = CFG_GRP_FAC_PREFIX + "pattern_b";


/**

 * caosm add for dtv

 */

public static String CFG_NIT_MAIN_FRE   				   = CFG_GRP_DISP_PREFIX + "nit_main_fre";
public static String CFG_TS_NUM         				   = CFG_GRP_DISP_PREFIX + "ts_num";
public static String CFG_NIT_VERSION    				   = CFG_GRP_DISP_PREFIX + "nit_version";


public static String CFG_CLOSE_BACKLIGHT_FUNC	          = CFG_GRP_D_CUST_PREFIX + "close_backlight";
public static String CFG_BG_COLOR_FUNC	                        = CFG_GRP_D_CUST_PREFIX + "back_ground_color";




/*-----------------------------------------------------------------------------

                   GROUP BOOTLOGO

-----------------------------------------------------------------------------*/



public static String CFG_BOOT_LOGO                   = CFG_GRP_MISC_PREFIX + "bootlogo";
/*-----------------------------------------------------------------------------

                   GROUP  CEC

-----------------------------------------------------------------------------*/



    

    

/*-----------------------------------------------------------------------------

                   GROUP  MISC

-----------------------------------------------------------------------------*/

public static String CFG_VALUE_GROUP                  = CFG_GRP_MISC_PREFIX + "__value_group";
















/**

 *@deprecated

 *turnkey will remove this type

 * picture mode type studio

 */

public static final int PICTURE_MODE_STUDIO     = 5;
/**

 *@deprecated

 *turnkey will remove this type

 * picture mode type default

 */

public static final int PICTURE_MODE_DEFAULT    = 6;
/**

 *@deprecated

 *turnkey will remove this type

 * picture mode type standard

 */

public static final int PICTURE_MODE_STANDARD   = 7;
/**

 *@deprecated

 *turnkey will remove this type

 * picture mode type game

 */

public static final int PICTURE_MODE_GAME       = 8;
/**

 *@deprecated

 *turnkey will remove this type

 * picture mode type concert

 */

public static final int PICTURE_MODE_CONCERT    = 9;




  

/**

 *@deprecated

 *turnkey will remove this type

 * audio mode type default

 */

public static final int AUDIO_MODE_DEFAULT = 3;
/**

 *@deprecated

 *turnkey will remove this type

 * audio mode type live2

 */

public static final int AUDIO_MODE_LIVE2   = 4;
/**

 *@deprecated

 *turnkey will remove this type

 * audio mode type theater

 */

public static final int AUDIO_MODE_THEATER = 5;
/**

 *@deprecated

 *turnkey will remove this type

 * audio mode type music

 */

public static final int AUDIO_MODE_MUSIC   = 6;
/**

 *@deprecated

 *turnkey will remove this type

 * audio mode speech

 */

public static final int AUDIO_MODE_SPEECH  = 7;
/**

 *@deprecated

 *turnkey will remove this type

 * volume state is not mute

 */

public static final int VOLUME_NOT_MUTE  = 0;
/**

 *@deprecated

 *turnkey will remove this type

 * volume state is mute

 */

public static final int VOLUME_MUTE  = 1;






/**

 *  AP expect the setting value as ATV Group,

 * Note the real group will be customized, and it may be different as the setting value.

 */

public static final int CONFIG_VALUE_GROUP_ATV           = 0;
/**

 *  AP expect the setting value as DTV Group

 * Note the real group will be customized, and it may be different as the setting value.

 */

public static final int CONFIG_VALUE_GROUP_DTV           = 1;
/**

 *  AP expect the setting value as AV Group

  * Note the real group will be customized, and it may be different as the setting value.

 */

public static final int CONFIG_VALUE_GROUP_AV            = 2;
/**

 *  AP expect the setting value as COMPONENT Group

 * Note the real group will be customized, and it may be different as the setting value.

 */

public static final int CONFIG_VALUE_GROUP_COMPONENT     = 3;
/**

 *  AP expect the setting value as HDMI Group

 * Note the real group will be customized, and it may be different as the setting value.

 */

public static final int CONFIG_VALUE_GROUP_HDMI          = 4;
/**

 *  AP expect the setting value as DVI Group

 * Note the real group will be customized, and it may be different as the setting value.

 */

public static final int CONFIG_VALUE_GROUP_DVI           = 5;
/**

 *  AP expect the setting value as VGA Group

 * Note the real group will be customized, and it may be different as the setting value.

 */

public static final int CONFIG_VALUE_GROUP_VGA           = 6;
/**

 *  AP expect the setting value as MMP Group

 * Note the real group will be customized, and it may be different as the setting value.

 */

public static final int CONFIG_VALUE_GROUP_MMP           = 7;


/**

*  Test for view

*  Note the real group will be customized, and it may be different as the setting value.

*/

public static final int CONFIG_VID_VIEW_NORMAL        = 0;
public static final int CONFIG_VID_VIEW_FULL             = 1;










}

