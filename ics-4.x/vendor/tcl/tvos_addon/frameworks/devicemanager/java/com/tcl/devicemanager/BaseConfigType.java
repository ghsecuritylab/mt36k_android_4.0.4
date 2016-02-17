package com.tcl.devicemanager;

/**
*
*for the configuration type definition
*
*/
public class BaseConfigType {


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
private static String CFG_GRP_TTX_PREFIX     = "grp_ttx_";
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
/**

 * for tv configuration to do the get the version info model name

 */

public static String VINFO_MODEL_NAME                   = CFG_GRP_MENU_PREFIX + "model_name";
/**

 * for tv configuration to do the get the version info version

 */

public static String VINFO_VERSION                      = CFG_GRP_MENU_PREFIX + "version";
/**

 * for tv configuration to do the get the version info serial number

 */

public static String VINFO_SERIAL_NUM                   = CFG_GRP_MENU_PREFIX + "serial_num";


/**

 * for tv configuration to do the get the audio language

 */

public static String MENU_AUD_LANG                   = CFG_GRP_MENU_PREFIX + "audio_lang";


/**

 * for tv configuration to do the get the audio language 2nd

 */

public static String MENU_AUD_LANG_2ND                   = CFG_GRP_MENU_PREFIX + "audio_lang2nd";


/**

 * for tv configuration to do the bytearray set function test

 */

public static String BYTEARRAY_TEST                   = CFG_GRP_MENU_PREFIX + "byte_array_test";








/**

 * for tv configuration to turn on/off power_on music

 */

public static String POWER_ON_MUSIC                   = CFG_GRP_MENU_PREFIX + "power_on_music";


/**

 * for tv configuration to turn on/off power_off music

 */

public static String POWER_OFF_MUSIC                   = CFG_GRP_MENU_PREFIX + "power_off_music";




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

 * for tv configuration to get/set/update audio_mode

 *Range:AUDIO_MODE_DEF~AUDIO_MODE_LIVE1  def:AUDIO_MODE_DEF

 */

public static String CFG_AUDIO_MODE                   = CFG_GRP_AUDIO_PREFIX + "audio_mode";




/**

 * for tv configuration to get/set/update balance

 *Range:-50~50  def:0

 */

public static String CFG_BALANCE                      = CFG_GRP_AUDIO_PREFIX + "balance";


/**

 * for tv configuration to get/set/update avc_mode

 *Range:COMMON_OFF~COMMON_ON  def:COMMON_ON

 */

public static String CFG_AVCMODE                      = CFG_GRP_AUDIO_PREFIX + "avc_mode";
/**

 * for tv configuration to get/set/update speaker_mode

 *Range:SPEAKER_MODE_INTERNAL~SPEAKER_MODE_EXTERNAL    def:SPEAKER_MODE_INTERNAL

 */

public static String CFG_SPEAKER_MODE                 = CFG_GRP_AUDIO_PREFIX + "speaker_mode";
/**

 * for tv configuration to get/set/update speaker

 *Range:COMMON_ON, COMMON_OFF  def:COMMON_ON

 */

public static String CFG_SPEAKER                 = CFG_GRP_AUDIO_PREFIX + "speaker";




/**

 * for tv configuration to get/set/update bass

 *Range:1~100  def:50

 */

public static String CFG_AUD_BASS                 = CFG_GRP_AUDIO_PREFIX + "bass";




/**

 * for tv configuration to get/set/update treble

 *Range: 0~100  def:50

 */

public static String CFG_AUD_TREBLE                 = CFG_GRP_AUDIO_PREFIX + "treble";




/**

 * for tv configuration to get/set/update downmix

 *Range: [0~9]  def:  AUD_DOWNMIX_MODE_STEREO

 */

public static String CFG_AUD_DMIX                 = CFG_GRP_AUDIO_PREFIX + "downmix";




/**

 * for tv configuration to get/set/update bass

 *Range:0~11  def:2

 */

public static String CFG_AUD_MTS                 = CFG_GRP_AUDIO_PREFIX + "aud_mts";


/**

 * for tv configuration to get/set/update audio ad headphone

 *Range:0~1  def:1

 */

public static String CFG_AUD_AD_HDPHONE                    = CFG_GRP_AUDIO_PREFIX + "ad_hdphone";


/**

 * for tv configuration to get/set/update audio ad speaker

 *Range:0~1  def:1

 */

public static String CFG_AUD_AD_SPEAKER                    = CFG_GRP_AUDIO_PREFIX + "ad_speaker";


/**

 * for tv configuration to get/set/update audio ad fade pan

 *Range:0~1  def:1

 */

public static String CFG_AUD_AD_FADE_PAN                   = CFG_GRP_AUDIO_PREFIX + "ad_fade_pan";


/**

 * for tv configuration to get/set/update bass

 *Range:1~100  def:50

 */

public static String CFG_DOLBY_BANNER                 = CFG_GRP_AUDIO_PREFIX + "Dobly_banner";


/**

 * for tv configuration to get/set/update dobly cmpr factor

 *Range:1~100  def:50

 */

public static String CFG_DOLBY_CMPR_FACTOR                 = CFG_GRP_AUDIO_PREFIX + "Dobly_cmpr_factor";




/**

 * for tv configuration to get/set/update dobly cmpr mode

 *Range:1~100  def:50

 */

public static String CFG_DOLBY_CMPR_MODE                 = CFG_GRP_AUDIO_PREFIX + "Dobly_cmpr_mode";


/**

 * for tv configuration to get/set/update AUDIO_TYPE

 *Range:0~3 def:0

 */

public static String CFG_AUDIO_TYPE                  = CFG_GRP_AUDIO_PREFIX + "audio_type";


/**

 * for tv configuration to get/set/update AUDIO_ONLY

 *Range:0~1 def:0

 */

public static String CFG_AUDIO_ONLY                  = CFG_GRP_AUDIO_PREFIX + "audio_only";




/**

 * for tv configuration to get/set/update volume

 *Range:0~100  def:20

 */

public static String CFG_VOLUME                       = CFG_GRP_AUDIO_PREFIX + "volume";




/**

 * for tv configuration to get/set/update ad_volume

 *Range:0~100  def:20

 */

public static String CFG_AD_VOLUME                       = CFG_GRP_AUDIO_PREFIX + "ad_volume";


/**

 * for tv configuration to get/set/update volume

 *Range:0~100  def:20

 */

public static String CFG_VOLUME_CENTER                     = CFG_GRP_AUDIO_PREFIX + "volumea_center";
/**

 * for tv configuration to get/set/update volume

 *Range:0~100  def:20

 */

public static String CFG_VOLUME_SUB_WOOFER                     = CFG_GRP_AUDIO_PREFIX + "volume_sub_woofer";
/**

 * for tv configuration to get/set/update volume

 *Range:0~100  def:20

 */

public static String CFG_VOLUME_FRONT_LEFT            = CFG_GRP_AUDIO_PREFIX + "volume_front_l";
/**

 * for tv configuration to get/set/update volume

 *Range:0~100  def:20

 */

public static String CFG_VOLUME_FRONT_RIGHT                       = CFG_GRP_AUDIO_PREFIX + "volume_front_r";
/**

 * for tv configuration to get/set/update volume

 *Range:0~100  def:20

 */

public static String CFG_VOLUME_REAR_LEFT                       = CFG_GRP_AUDIO_PREFIX + "volume_rear_l";
/**

 * for tv configuration to get/set/update volume

 *Range:0~100  def:20

 */

public static String CFG_VOLUME_REAR_RIGHT                       = CFG_GRP_AUDIO_PREFIX + "volume_rear_r";


/**

 * for tv configuration to get/set/update hp_volume

 */

public static String CFG_HP_VOLUME                    = CFG_GRP_AUDIO_PREFIX + "hp_volume";
/**

 * for tv configuration to get/set/update srs_mode

 *Range:COMMON_OFF~COMMON_ON  def:COMMON_ON

 */

public static String CFG_SRS_MODE                     = CFG_GRP_AUDIO_PREFIX + "srs_mode";
/**

 * for tv configuration to get/set/update spdif_mode

 *Range:AUD_SPDIF_FMT_OFF~AUD_SPDIF_FMT_PCM_24  def:AUD_SPDIF_FMT_PCM_24

 */

public static String CFG_SPDIF_MODE                   = CFG_GRP_AUDIO_PREFIX + "spdif_mode";
/**

 * for tv configuration to get/set/update spdif_delay

 *Range:0-100 def:0

 */

public static String CFG_SPDIF_DELAY                  = CFG_GRP_AUDIO_PREFIX + "spdif_delay";
/**

 * for tv configuration to get/set/update audio channel

 *Range:0-2 def:0

 */

public static String CFG_AUD_CHANNEL                  = CFG_GRP_AUDIO_PREFIX + "audio_channel";
/**

 * for tv configuration to get/set/update sound_field_locate_mode

 *Range:SOUND_FIELD_LOCATE_MODE_WALL~SOUND_FIELD_LOCATE_MODE_DESK  def:SOUND_FIELD_LOCATE_MODE_DESK

 */

public static String CFG_SOUND_FIELD_LOCATE_MODE      = CFG_GRP_AUDIO_PREFIX + "sound_field_locate_mode";
/**

 * for tv configuration to get/set/update audio_delay

 *Range:0~30  def:0

 */

public static String CFG_AUDIO_DELAY                  = CFG_GRP_AUDIO_PREFIX + "audio_delay";
/**

 * for tv configuration to get/set/update equalize

 */

public static String CFG_EQUALIZE                     = CFG_GRP_AUDIO_PREFIX + "equalize";
/**

 * for tv configuration to get/set/update equalize band 1

 */

public static String CFG_EQ_BAND_1                     = CFG_GRP_AUDIO_PREFIX + "eq_band_1";
/**

 * for tv configuration to get/set/update equalize band 2

 */

public static String CFG_EQ_BAND_2                     = CFG_GRP_AUDIO_PREFIX + "eq_band_2";
/**

 * for tv configuration to get/set/update equalize band 3

 */

public static String CFG_EQ_BAND_3                     = CFG_GRP_AUDIO_PREFIX + "eq_band_3";
/**

 * for tv configuration to get/set/update equalize band 4

 */

public static String CFG_EQ_BAND_4                     = CFG_GRP_AUDIO_PREFIX + "eq_band_4";
/**

 * for tv configuration to get/set/update equalize band 5

 */

public static String CFG_EQ_BAND_5                     = CFG_GRP_AUDIO_PREFIX + "eq_band_5";
/**

 * for tv configuration to get/set/update equalize band 6

 */

public static String CFG_EQ_BAND_6                     = CFG_GRP_AUDIO_PREFIX + "eq_band_6";
/**

 * for tv configuration to get/set/update equalize band 7

 */

public static String CFG_EQ_BAND_7                     = CFG_GRP_AUDIO_PREFIX + "eq_band_7";


/**

 * for tv configuration to get/set/update output device selection

 */

public static String CFG_AUD_OUTDEV                    = CFG_GRP_AUDIO_PREFIX + "outdev_sel";




/*-----------------------------------------------------------------------------

                   GROUP DISP

-----------------------------------------------------------------------------*/

/**

 * for tv configuration to get/set/update backlight

 *Range:0~100  def:50

 */

public static String CFG_BACKLIGHT                    = CFG_GRP_VIDEO_PREFIX + "backlight";
/**

 * for tv configuration to get/set/update gamma

	 * Range:VID_GAMMA_DARK~VID_GAMMA_BRIGHT	def:VID_GAMMA_MIDDLE

 */

public static String CFG_GAMMA                        = CFG_GRP_DISP_PREFIX + "gamma";






    

/*-----------------------------------------------------------------------------

                   GROUP VIDEO

-----------------------------------------------------------------------------*/

/**

 * for tv configuration to get/set/update screen mode

 *Range:SCREEN_MODE_NORMAL~SCREEN_MODE_DOT_BY_DOT  def:SCREEN_MODE_NORMAL

 */

public static String CFG_SCREEN_MODE                  = CFG_GRP_VIDEO_PREFIX + "screen_mode";


/**

 * for tv configuration to get/set/update screen mode_ex(this '_ex' will updates overscan while updating screen mode)

 *Range:SCREEN_MODE_NORMAL~SCREEN_MODE_DOT_BY_DOT  def:SCREEN_MODE_NORMAL

 */

public static String CFG_SCREEN_MODE_EX                  = CFG_GRP_VIDEO_PREFIX + "screen_mode_ex";




/**

 * for tv configuration to get/set/update screen mode

 *Range:SCREEN_MODE_NORMAL~SCREEN_MODE_DOT_BY_DOT  def:SCREEN_MODE_NORMAL

 */

public static String CFG_GET_SCREEN_MODE_NUM          = CFG_GRP_VIDEO_PREFIX + "get_scr_mode_num";




/**

 * for tv configuration to get/set/update screen mode

 *Range:SCREEN_MODE_NORMAL~SCREEN_MODE_DOT_BY_DOT  def:SCREEN_MODE_NORMAL

 */

public static String CFG_GET_NEXT_SCREEN_MODE      = CFG_GRP_VIDEO_PREFIX + "get_next_scr_mode";


/**

 * for tv configuration to get/set/update screen mode

 *Range:SCREEN_MODE_NORMAL~SCREEN_MODE_DOT_BY_DOT  def:SCREEN_MODE_NORMAL

 */

public static String CFG_GET_PREV_SCREEN_MODE       = CFG_GRP_VIDEO_PREFIX + "get_prev_scr_mode";
public static String CFG_GET_THIS_SCREEN_MODE       = CFG_GRP_VIDEO_PREFIX + "get_this_scr_mode";
public static String CFG_GET_FIRST_SCREEN_MODE      = CFG_GRP_VIDEO_PREFIX + "get_first_scr_mode";
public static String CFG_GET_LAST_SCREEN_MODE       = CFG_GRP_VIDEO_PREFIX + "get_last_scr_mode";
public static String CFG_GET_ALL_SCREEN_MODE        = CFG_GRP_VIDEO_PREFIX + "get_available_scr_mode";




/**

 * for tv configuration to get/set/update picture_mode

 *Range:PICTURE_MODE_USER~PICTURE_MODE_STUDIO  def:PICTURE_MODE_USER

 */

public static String CFG_PICTURE_MODE                 = CFG_GRP_VIDEO_PREFIX + "picture_mode";
/**

 * for tv configuration to get/set/update brightness

 * Range:0~100    def:50

 */

public static String CFG_BRIGHTNESS                   = CFG_GRP_VIDEO_PREFIX + "brightness";
/**

 * for tv configuration to get/set/update contrast

 * Range:0~100    def:50

 */

public static String CFG_CONTRAST                     = CFG_GRP_VIDEO_PREFIX + "contrast";
/**

 * for tv configuration to get/set/update hue

 * Range:-32~32    def:0

 */

public static String CFG_HUE                          = CFG_GRP_VIDEO_PREFIX + "hue";
/**

 * for tv configuration to get/set/update saturation

 * Range:0~100    def:50

 */

public static String CFG_SATURATION                   = CFG_GRP_VIDEO_PREFIX + "saturation";


/**

 * for tv configuration to get/set/update sharpness

 * Range:0~7    def:4

 */

public static String CFG_SHARPNESS                    = CFG_GRP_VIDEO_PREFIX + "sharpness";
/**

 * for tv configuration to get/set/update blue_screen

 *Range:COMMON_OFF~COMMON_ON  def:COMMON_ON

 */

public static String CFG_BLUE_SCREEN                  = CFG_GRP_VIDEO_PREFIX + "blue_screen";
/**

 * for tv configuration to get/set/update temperature

 *Range:VID_CLR_TEMP_USER~VID_CLR_TEMP_WARM  def:VID_CLR_TEMP_USER

 */

public static String CFG_TEMPERATURE                  = CFG_GRP_VIDEO_PREFIX + "temperature";


/**

 * for tv configuration to get/set/update color_gain_r

 *Range:-20~20  def:0

 */

public static String CFG_COLOR_GAIN_R                 = CFG_GRP_VIDEO_PREFIX + "color_gain_r";
/**

 * for tv configuration to get/set/update color_gain_g

 *Range:-20~20  def:0

 */

public static String CFG_COLOR_GAIN_G                 = CFG_GRP_VIDEO_PREFIX + "color_gain_g";
/**

 * for tv configuration to get/set/update color_gain_b

 *Range:-20~20  def:0

 */

public static String CFG_COLOR_GAIN_B                 = CFG_GRP_VIDEO_PREFIX + "color_gain_b";
/**

 * for tv configuration to get/set/update color_offset_r

 *Range:-20~20  def:0

 */

public static String CFG_COLOR_OFFSET_R               = CFG_GRP_VIDEO_PREFIX + "color_offset_r";
/**

 * for tv configuration to get/set/update color_offset_g

 *Range:-20~20  def:0

 */

public static String CFG_COLOR_OFFSET_G               = CFG_GRP_VIDEO_PREFIX + "color_offset_g";
/**

 * for tv configuration to get/set/update color_offset_b

 *Range:-20~20  def:0

 */

public static String CFG_COLOR_OFFSET_B               = CFG_GRP_VIDEO_PREFIX + "color_offset_b";
/**

 * for tv configuration to get/set/update blue stretch value

 *Range:COMMON_OFF~COMMON_ON  def:COMMON_ON

 */

public static String CFG_BLUE_STRETCH                 = CFG_GRP_VIDEO_PREFIX + "blue_stretch";
/**

 * for tv configuration to get/set/update nr

 * Range:VID_DNR_OFF~VID_DNR_STRONG    def:VID_DNR_MEDIUM

 */

public static String CFG_NR                           = CFG_GRP_VIDEO_PREFIX + "nr";


public static String CFG_3DNR                         = CFG_GRP_VIDEO_PREFIX + "3dnr";


/**

 * for tv configuration to get/set/update nr

 * Range:VID_MPEG_NR_OFF~VID_MPEG_NR_STRONG    def:VID_MPEG_NR_MEDIUM

 */

public static String CFG_MPEG_NR                           = CFG_GRP_VIDEO_PREFIX + "mpeg_nr";




/**

 * for tv configuration to get/set/update Di_film_mode

 * Range:DI FILM ~DI_FILM   def:DI_FILM_MODE_ACTION_PIC

 */

public static String CFG_DI_FILM_MODE                           = CFG_GRP_VIDEO_PREFIX + "Di_film_mode";




/**

 * for tv configuration to get/set/update Di_MA

 * Range:DI FILM ~DI_FILM   def:DI_FILM_MODE_ACTION_PIC

 */

public static String CFG_DI_MA                          = CFG_GRP_VIDEO_PREFIX + "Di_MA";






/**

 * for tv configuration to get/set/update Di_edge

 * Range:Di_edge  ~Di_edge   def:Di_edge

 */

public static String CFG_DI_EDGE                          = CFG_GRP_VIDEO_PREFIX + "Di_edge";




/**

 * for tv configuration to get/set/update HDMI MODE

 * Range:HDMI_MODE_UNKNOWN  ~HDMI_MODE_VIDEO   def: HDMI_MODE_AUTO

 */

public static String CFG_HDMI_MODE                          = CFG_GRP_VIDEO_PREFIX + "HDMI_mode";




/**

 * for tv configuration to get/set/update WCG

 * Range:WCG  ~WCG   def:WCG

 */

public static String CFG_WCG                          = CFG_GRP_VIDEO_PREFIX + "WCG";


/**

 * for tv configuration to get/set/update WCG

 * Range:0  ~1   def: 0

 */

public static String CFG_GAME_MODE                          = CFG_GRP_VIDEO_PREFIX + "game_mode";




/**

 * for tv configuration to get/set/update CFG_VGAMODE

 * Range:CFG_VGAMODE ~CFG_VGAMODE   def:CFG_VGAMODE

 */

public static String CFG_VGAMODE                           = CFG_GRP_VIDEO_PREFIX + "VGA_mode";








/**

 * for tv configuration to get/set/update white_peak_lmt

 * Range:COMMON_OFF~COMMON_ON    def:COMMON_ON

 */

public static String CFG_WHITE_PEAK_LMT               = CFG_GRP_VIDEO_PREFIX + "white_peak_lmt";
/**

 * for tv configuration to get/set/update flesh_tone

 * Range:COMMON_OFF~COMMON_ON    def:COMMON_ON

 */

public static String CFG_FLESH_TONE                   = CFG_GRP_VIDEO_PREFIX + "flesh_tone";
/**

 * for tv configuration to get/set/update luma

 * Range:COMMON_OFF~COMMON_ON    def:COMMON_ON

 */

public static String CFG_LUMA                         = CFG_GRP_VIDEO_PREFIX + "luma";


/**

 * for tv configuration to get/set/update phase

 *Range:0~31  def:0

 */

public static String CFG_YPBPR_PHASE                        = CFG_GRP_VIDEO_PREFIX + "ypbpr_phase";
/**

 * for tv configuration to get/set/update h_position

 *Range:-16~16  def:0

 */

public static String CFG_H_POSITION                   = CFG_GRP_VIDEO_PREFIX + "h_position";
/**

 * for tv configuration to get/set/update v_position

 *Range:-16~16  def:0

 *Range:0~100  def:0

 */

public static String CFG_V_POSITION                   = CFG_GRP_VIDEO_PREFIX + "v_position";
/**

 * for tv configuration to get/set/update h_size

 *Range:-5~5  def:0

 */

public static String CFG_H_SIZE                       = CFG_GRP_VIDEO_PREFIX + "h_size";
/**

 * for tv configuration to get/set/update v_size

 *Range:-5~5  def:0

 */

public static String CFG_V_SIZE                       = CFG_GRP_VIDEO_PREFIX + "v_size";


/**

 * for tv configuration to get/set/update MJC function

 *Range:0~1  def:1

 */

public static String CFG_MJC_FCT                      = CFG_GRP_VIDEO_PREFIX + "MJC_fct";


/**

 * for tv configuration to get/set/update MJC mode

 *Range:CFG_MJC_MODE_0~CFG_MJC_MODE_1  def:CFG_MJC_MODE_0

 */

public static String CFG_MJC_MODE                     = CFG_GRP_VIDEO_PREFIX + "MJC_mode";


/**

 * for tv configuration to get/set/update MJC mode

 *Range:CFG_MJC_EFFECT_OFF ~CFG_MJC_EFFECT_HIGH  def:CFG_MJC_EFFECT_OFF

 */

public static String CFG_MJC_EFFECT                   = CFG_GRP_VIDEO_PREFIX + "MJC_effect";


/**

 * for tv configuration to get/set/update MJC mode

 *Range:CFG_MJC_DEMO_OFF ~CFG_MJC_DEMO_LEFT  def:CFG_MJC_DEMO_OFF

 */

public static String CFG_MJC_DEMO                     = CFG_GRP_VIDEO_PREFIX + "MJC_demo";


/**

 * for tv configuration to get/set/update PQ DEMO function

 *Range:CFG_PQ_DEMO_FUNC_OFF ~ CFG_PQ_DEMO_FUNC_ON  def:CFG_PQ_DEMO_FUNC_OFF

 */

public static String CFG_PQ_DEMO_FUNC                      = CFG_GRP_VIDEO_PREFIX + "PQ_DEMO";


/**

 * for tv configuration to get/set/update PQ DEMO TYPE

 *Range:CFG_PQ_DEMO_TYPE_OFF ~ CFG_PQ_DEMO_TYPE_LED_BL_Control  def:CFG_PQ_DEMO_TYPE_OFF

 */

public static String CFG_PQ_DEMO_TYPE                   	   = CFG_GRP_VIDEO_PREFIX + "PQ_DEMO_TYPE";


/**

 * for tv configuration to get/set/update PQ DEMO ACTION

 *Range:CFG_PQ_DEMO_ACTION_OFF ~CFG_PQ_DEMO_ACTION_LEFT  def:CFG_PQ_DEMO_ACTION_OFF

 */

public static String CFG_PQ_DEMO_ACTION                  = CFG_GRP_VIDEO_PREFIX + "PQ_DEMO_ACTION";


/**

 * for tv configuration to get/set/update image safety

 *Range:SCC_3D_IMAGE_SAFETY_OFF ~ SCC_3D_IMAGE_SAFETY_HIGH def: SCC_3D_IMAGE_SAFETY_OFF

 */

public static String CFG_IMG_SFTY                     = CFG_GRP_VIDEO_PREFIX + "img_safety";


/**

 * for tv configuration to get/set/update MJC mode

 *Range:  (SCC_3D_NAV_AUTO_CHG_MANUAL) ~ (SCC_3D_NAV_AUTO_CHG_AUTO)

 def:(SCC_3D_NAV_AUTO_CHG_SEMI_AUTO) 

 */

public static String CFG_3D_NAV_AUTO                     = CFG_GRP_VIDEO_PREFIX + "3d_nav_auto";






/**

 * for tv configuration to get/set/update 3d_mode

 *Range:CFG_3D_MODE_OFF~CFG_3D_MODE_AUTO  def:CFG_3D_MODE_OFF

 */

public static String CFG_3D_MODE                      = CFG_GRP_VIDEO_PREFIX + "3d_mode";
/**

 * for tv configuration to get/set/update 3D LR switch value

 *Range:COMMON_OFF~COMMON_ON  def:COMMON_OFF

 */

public static String CFG_3D_LR_SWITCH                 = CFG_GRP_VIDEO_PREFIX + "3d_lr_switch";
/**

 * for tv configuration to get/set/update 3D to 2D value

 *Range:CFG_3D_TO_2D_OFF~CFG_3D_TO_2D_RIGHT  def:CFG_3D_TO_2D_OFF

 */

public static String CFG_3D_TO_2D                     = CFG_GRP_VIDEO_PREFIX + "3d_to_2d";
/**

 * for tv configuration to get/set/update 3d depth of field value

 *Range:0~32  def:16 

 */

public static String CFG_3D_DEPTH_OF_FIELD            = CFG_GRP_VIDEO_PREFIX + "3d_depth_of_field";


/**

 * for tv configuration to get/set/update 3d tv distance value

 *Range:2~18  def:10 

 */

public static String CFG_3D_DISTANCE            = CFG_GRP_VIDEO_PREFIX + "3d_distance";


/**

 * for tv configuration to get/set/update 3d view point value

 */

public static String CFG_3D_VIEW_POINT                = CFG_GRP_VIDEO_PREFIX + "3d_view_point";


/**

 * for tv configuration to get 3d nav tag value

 */

public static String CFG_3D_NAV_TAG      = CFG_GRP_VIDEO_PREFIX + "3d_nav_tag";
/**

 * for tv configuration to get 3d tag3d type value

 */

public static String CFG_3D_TAG3D_TYPE	 = CFG_GRP_VIDEO_PREFIX + "tag3d_type";




/**

 * for tv configuration to get(only get) 3d mode capability (just High16 bits)

 */

public static String CFG_VIDEO_3D_MODE_CAP_HIGH16                = CFG_GRP_VIDEO_PREFIX + "3d_mode_cap_high16";


/**

* for tv configuration to get(only get) 3d mode capability (just low16 bits)

*/

public static String CFG_VIDEO_3D_MODE_CAP_LOW16                = CFG_GRP_VIDEO_PREFIX + "3d_mode_cap_low16";


/**

 * for tv configuration to get(only get) 3d ctrl capability (just High16 bits)

 */

public static String CFG_VIDEO_3D_CTRL_CAP_HIGH16                = CFG_GRP_VIDEO_PREFIX + "3d_ctrl_cap_high16";


/**

* for tv configuration to get(only get) 3d ctrl capability (just low16 bits)

*/

public static String CFG_VIDEO_3D_CTRL_CAP_LOW16                = CFG_GRP_VIDEO_PREFIX + "3d_ctrl_cap_low16";












/**

 * for tv configuration to get/set flip attribute

 *Range:0~1

 */

public static String CFG_VIDEO_FLIP                   = CFG_GRP_VIDEO_PREFIX + "flip";


/**

 * for tv configuration to get/set mirror attribute

 *Range:0~1

 */

public static String CFG_VIDEO_MIRROR                 = CFG_GRP_VIDEO_PREFIX + "mirror";




/*-----------------------------------------------------------------------------

                   GROUP BROADCAST SERVICE

-----------------------------------------------------------------------------*/ 



    

/*-----------------------------------------------------------------------------

                   GROUP VGA

-----------------------------------------------------------------------------*/

/**

 * for tv configuration to get/set/update vga clock

 *Range:0~255  def:0

 */

public static String CFG_VGA_PHASE                        = CFG_GRP_VGA_PREFIX + "phase";
public static String CFG_VGA_CLOCK                        = CFG_GRP_VGA_PREFIX + "clock";
public static String CFG_VGA_POS_H                        = CFG_GRP_VGA_PREFIX + "pos_h";
public static String CFG_VGA_POS_V                        = CFG_GRP_VGA_PREFIX + "pos_v";




/*-----------------------------------------------------------------------------

                   GROUP Factory

-----------------------------------------------------------------------------*/



    

    

/*-----------------------------------------------------------------------------

                   GROUP  CEC

-----------------------------------------------------------------------------*/



    

    

/*-----------------------------------------------------------------------------

                   GROUP  MISC

-----------------------------------------------------------------------------*/

/**

 * for tv configuration to get/set/update power save mode value

 *Range:POWER_SAVE_MODE_USER~POWER_SAVE_MODE_DYNAMIC  def:POWER_SAVE_MODE_USER

 */

public static String CFG_POWER_SAVE                   = CFG_GRP_MISC_PREFIX + "power_save";
/**

 * for tv configuration to get/set/update   pwr_on_timer value

 *Range: daytimesec ,pls convert hhmmss to daytimesecs  def: 0

 * when set daytimesecs value is 0, means turn off the pwr_on_timer fct 

 */

public static String CFG_POWER_ON_TIMER                   = CFG_GRP_MISC_PREFIX + "power_on_timer";






public static String CFG_WAKE_UP_REASON                   = CFG_GRP_MISC_PREFIX + "wake_up_reason";






public static String CFG_WAKEUP_VGA_SETUP                   = CFG_GRP_MISC_PREFIX + "vga_wakeup";




/**

 * for tv configuration to get/set FBM mode

 * Range: FBM_MODE_1_TVMM~FBM_MODE_2_ANDROID  def: FBM_MODE_1_TVMM

 */

public static String CFG_FBM_MODE                         = CFG_GRP_MISC_PREFIX + "fbm_mode";


/**

 * for tv configuration to get/set   DPMS value

 *Range: ON/OFF  def: ON

 * COMMON_ON ,COMMON_OFF

 */

public static String CFG_DPMS                             = CFG_GRP_MISC_PREFIX + "dpms";




/**

 * for tv configuration to get/set/update magic av system value

 *Range:COMMON_OFF~COMMON_ON  def:COMMON_OFF

 */

public static String CFG_MAGIC_AV_SYSTEM              = CFG_GRP_MISC_PREFIX + "magic_av";
/**

 * for tv configuration to do the user reset operation

 */

public static String RESET_USER                       = CFG_GRP_MISC_PREFIX + "userreset";
/**

 * for tv configuration to do the factory reset operation

 */

public static String RESET_FACTORY                    = CFG_GRP_MISC_PREFIX + "factoryreset";
/**

 * for tv configuration to do the factory reset operation

 */

public static String CFG_CUSTOM_PART_SIZE             = CFG_GRP_MISC_PREFIX + "custom_part_size";




/**

 *@deprecated

 *change autoAdjust to input source

 * for tv auto adjust the vga(hv pos,clock,phase)

 */

public static String AUTO_TYPE_VGA_ADJUST             = CFG_GRP_MISC_PREFIX + "auto_vga_adjust";
/**

 *@deprecated

 *change autoAdjust to input source

 * for tv auto phase

 */

public static String AUTO_TYPE_PHASE                  = CFG_GRP_MISC_PREFIX + "auot_phase";
/**

 *@deprecated

 *change autoAdjust to input source

 * for tv auto color

 */

public static String AUTO_TYPE_COLOR                  = CFG_GRP_MISC_PREFIX + "auto_color";
/**

 * for tv configuration to do the factory descramble operation

 */



public static String CFG_DESCRAMBLER                  = CFG_GRP_MISC_PREFIX + "descrambler";


/**

 * for tv configuration to get/set timezone  0~34

 */

public static String CFG_TIME_ZONE                    = CFG_GRP_TIME_PREFIX + "time_zone";
/**

 * for tv configuration to do the time_sync operation  TIME_SYNC_MODE_AUTO ~TIME_SYNC_MODE_MANUAL 

 */

public static String CFG_TIME_SYNC                    = CFG_GRP_TIME_PREFIX + "time_sync";


/**

 * for tv configuration to do the time_sync operation  TIME_SYNC_MODE_AUTO ~TIME_SYNC_MODE_MANUAL 

 */

public static String CFG_TTX_DECODE_PAGE              = CFG_GRP_TTX_PREFIX + "decode_page";


/**

 * for tv configuration to do the time_sync operation  TIME_SYNC_MODE_AUTO ~TIME_SYNC_MODE_MANUAL 

 */

public static String CFG_TTX_DIGITAL_LANG             = CFG_GRP_TTX_PREFIX + "ttx_digi_lang";


/**

 * for tv configuration to do the time_sync operation  TIME_SYNC_MODE_AUTO ~TIME_SYNC_MODE_MANUAL 

 */

public static String CFG_TTX_PRESENTATION_LVL         = CFG_GRP_TTX_PREFIX + "prst_lvl";


/**

 * for tv configuration to do the ttx_top_enable operation  COMMON_ON ~COMMON_OFF 

 */

public static String CFG_TTX_TOP_ENABLE               = CFG_GRP_TTX_PREFIX + "top_enable";


/**

 * for tv configuration to do the Broadcast source switch operation  BS_SRC_AIR ~BS_SRC_CABLE,

 * default : BS_SRC_CABLE

 */

public static String CFG_BS_SRC                       = CFG_GRP_BS_PREFIX + "bs_src";










public static String D_CFG_AUD_HEADPHOEN_VOL          = CFG_GRP_D_AUD_PREFIX + "headphone_vol";


public static String D_CFG_AUD_SPECTRUM               = CFG_GRP_D_AUD_PREFIX + "aud_spectrum";
public static String D_CFG_AUD_BLBLOCKDATA            = CFG_GRP_D_AUD_PREFIX + "aud_blblockdata";




/*********************Config Value Defined**********************

    

***************************************************************/

/**

 * common definition of off  _______________________________________________COMMON_ON_OFF



 INCLUDE :WCG (ON/OFF)

 */

public static final int COMMON_OFF = 0;
    

/**

 * common definition of on

 */

public static final int COMMON_ON = 1;


/**

 * PQ common definition of off _______________________________________________PQ

 */

public static final int PQ_COMMON_OFF = 0;
/**

 * PQ common definition of weak

 */

public static final int PQ_COMMON_WEAK = 1;
/**

 * PQ common definition of standard

 */

public static final int PQ_COMMON_STANDARD = 2;
/**

 * PQ common definition of strong

 */

public static final int PQ_COMMON_STRONG = 3;
/**

 * speaker mode definition of internal  _______________________________________________SPEAKER_MODE

 */

public static final int SPEAKER_MODE_INTERNAL = 0;
/**

 * speaker mode definition of external

 */

public static final int SPEAKER_MODE_EXTERNAL = 1;
/**

 * sound field locate mode definition of wall  _______________________________________________SOUND_FIELD_LOCATE_MODE

 */

public static final int SOUND_FIELD_LOCATE_MODE_WALL = 0;
/**

 * sound field locate mode definition of desk

 */

public static final int SOUND_FIELD_LOCATE_MODE_DESK = 1;
/**

 * 3D mode definition of off _______________________________________________3D_MODE

 */

public static final int CFG_3D_MODE_OFF = 0;
/**

 * 3D mode definition of AUTO

 */

public static final int CFG_3D_MODE_AUTO = 1;
/**

 * 3D mode definition of _2D_TO_3D

 */

public static final int CFG_3D_MODE_2D_TO_3D = 2;
/**

 * 3D mode definition of FRM_SEQ

 */

public static final int CFG_3D_MODE_FRM_SEQ = 3;
/**

 * 3D mode definition of SIDE_SIDE

 */

public static final int  CFG_3D_MODE_SIDE_SIDE = 4;
/**

 * 3D mode definition of TOP_AND_BTM

 */

public static final int CFG_3D_MODE_TOP_AND_BTM = 5;
/**

 * 3D mode definition of REALD

 */

public static final int CFG_3D_MODE_REALD   = 6;
/**

 * 3D mode definition of SENSIO

 */

public static final int CFG_3D_MODE_SENSIO  = 7;
/**

 * 3D mode definition of LINE_INTERLEAVE

 */

public static final int CFG_3D_MODE_LINE_INTERLEAVE = 8;
/**

 * 3D mode definition of DOT_ALT

 */

public static final int CFG_3D_MODE_DOT_ALT = 9;
/**

 * 3D mode definition of CHK_BOARD

 */

public static final int CFG_3D_MODE_CHK_BOARD = 10;


/**

 * EQUILIZER MODE   definition of OFF _____________________________________________________ 3D_to_2D

 */

public static final int CFG_AUD_SE_EQ_OFF       = 0;


/**

 * EQUILIZER MODE    definition of ROCK

 */

public static final int CFG_AUD_SE_EQ_ROCK      = 1;


/**

 * EQUILIZER MODE    definition of POP

 */

public static final int CFG_AUD_SE_EQ_POP       = 2;


/**

 * EQUILIZER MODE    definition of LIVE

 */

public static final int CFG_AUD_SE_EQ_LIVE      = 3;


/**

 * EQUILIZER MODE    definition of DANCE

 */

public static final int CFG_AUD_SE_EQ_DANCE     = 4;


/**

 * EQUILIZER MODE    definition of TECHNO

 */

public static final int CFG_AUD_SE_EQ_TECHNO    = 5;


/**

 * EQUILIZER MODE    definition of CLASSIC

 */

public static final int CFG_AUD_SE_EQ_CLASSIC   = 6;


/**

 * EQUILIZER MODE    definition of SOFT

 */

public static final int CFG_AUD_SE_EQ_SOFT      = 7;


/**

 * 33D_TO_2D  definition of off _____________________________________________________ 3D_to_2D

 */



public static final int CFG_SCC_3D_TO_2D_OFF  = 0;


/**

 * 3D mode definition of 3D_TO_2D_LEFT

 */

public static final int CFG_3D_TO_2D_LEFT       = 1;
/**

* 3D mode definition of 3D_TO_2D_LEFT

*/

public static final int CFG_3D_TO_2D_RIGHT       = 2;




/**

 * MJC related definition  _____________________________________________________ MJC

 */

/* Value of APP_CFG_RECID_MJC_FUNC */

public static final int CFG_MJC_OFF                        = 0;
public static final int CFG_MJC_ON                         = 1;


/* Value of APP_CFG_RECID_VID_MJC_EFFECT */

public static final int CFG_MJC_EFFECT_OFF                 = 0;
public static final int CFG_MJC_EFFECT_LOW                 = 1;
public static final int CFG_MJC_EFFECT_MIDDLE              = 2;
public static final int CFG_MJC_EFFECT_HIGH                = 3;


/* Value of NORMAL */

public static final int CFG_MJC_MODE_0                     = 0;
/* Value of FILM_ONLY */

public static final int CFG_MJC_MODE_1                     = 1;
/* Value of DEBLUR_FILM*/

public static final int CFG_MJC_MODE_2                     = 2;
/* Value of MJC NONE */

public static final int CFG_MJC_MODE_NONE                  = 255;


/* Value of ACFG_VID_MJC_DEMO */

public static final int CFG_MJC_DEMO_OFF                   = 0;
public static final int CFG_MJC_DEMO_RIGHT                 = 1;
public static final int CFG_MJC_DEMO_LEFT                  = 2;


/* Value of APP_CFG_RECID_PQ_DEMO_FUNC */

public static final int CFG_PQ_DEMO_FUNC_OFF                    = 0;
public static final int CFG_PQ_DEMO_FUNC_ON                     = 1;


/* Value of APP_CFG_RECID_PQ_DEMO_TYPE */

public static final int CFG_PQ_DEMO_TYPE_OFF                     = 0;
// Color Analysis Improvement

public static final int CFG_PQ_DEMO_TYPE_ColorAnalyImprove       = 1;
// High Speed Technology

public static final int CFG_PQ_DEMO_TYPE_HighSpeedTech           = 2;
// LED Backlight Control 

public static final int CFG_PQ_DEMO_TYPE_LED_BL_Control          = 3;


/* Value of ACFG_VID_PQ_DEMO_ACTION */

public static final int CFG_PQ_DEMO_ACTION_OFF                = 0;
public static final int CFG_PQ_DEMO_ACTION_RIGHT              = 1;
public static final int CFG_PQ_DEMO_ACTION_LEFT               = 2;


/**

 * 3D_NAV_AUTO  mode definition   _____________________________________________________  3D_NAV_AUTO

 */

public static final int CFG_VID_3D_NAV_AUTO_CHG_MANUAL                 = 0;
public static final int CFG_VID_3D_NAV_AUTO_CHG_SEMI_AUTO              = 1;
public static final int CFG_VID_3D_NAV_AUTO_CHG_AUTO                   = 2;


/**

 * 3D_IMAGE SAFETY  mode definition   _____________________________________________________ IMAGE SAFETY

 */

public static final int CFG_VID_3D_IMG_STFY_OFF                = 0;
public static final int CFG_VID_3D_IMG_STFY_LOW                = 1;
public static final int CFG_VID_3D_IMG_STFY_MID                = 2;
public static final int CFG_VID_3D_IMG_STFY_HIGH               = 3;


/**

 * spdif mode definition of pcm _____________________________________________________SPDIF_MODE

 */

public static final int SPDIF_MODE_FMT_OFF = 0;
/**

 * spdif mode definition of RAW

 */

public static final int SPDIF_MODE_FMT_RAW = 1;
/**

 * spdif mode definition of PCM16

 */

public static final int SPDIF_MODE_FMT_PCM16 = 2;
/**

 * spdif mode definition of PCM24

 */

public static final int SPDIF_MODE_FMT_PCM24 = 3;


/**

 * OLBY_DRC definition of 0 _____________________________________________________DRC_CMPR_FACTOR

 */

public static final int AUD_DOLBY_DRC_OFF = 0;


/**

* OLBY_DRC definition of 1

*/

public static final int AUD_DOLBY_DRC_7_8 = 1;
/**

* OLBY_DRC definition of 2

*/

public static final int AUD_DOLBY_DRC_3_4 = 2;
/**

* OLBY_DRC definition of 3

*/

public static final int AUD_DOLBY_DRC_5_8 = 3;
/**

* OLBY_DRC definition of 4

*/

public static final int AUD_DOLBY_DRC_1_2 = 4;
/**

* OLBY_DRC definition of 5

*/

public static final int AUD_DOLBY_DRC_3_8 = 5;
/**

* OLBY_DRC definition of 6

*/

public static final int AUD_DOLBY_DRC_1_4 = 6;
/**

* OLBY_DRC definition of 7

*/

public static final int AUD_DOLBY_DRC_1_8 = 7;
/**

* OLBY_DRC definition of 8

*/

public static final int AUD_DOLBY_DRC_FULL = 8;




/**

 * AUD_OUTDEV  definition of 0 _____________________________________________________  AUD_OUTDEV

 */

public static final int AUD_OUTDEV_SPEAKER  = 0;


/**

* AUD_OUTDEV_USB definition of 1

*/

public static final int AUD_OUTDEV_USB = 1;


/**

* AUD_OUTDEV_BLUETEETH definition of 1

*/

public static final int AUD_OUTDEV_BLUETEETH = 2;






/**

 * AUD_CMPSS_MDOE definition of 0 _____________________________________________________AUD_CMPSS_MDOE

 */

public static final int AUD_CMPSS_MDOE_LINE = 0;
/**

* AUD_CMPSS_MDOE definition of 1

*/

public static final int AUD_CMPSS_MDOE_CUSTOM_1 = 1;
/**

 * AUD_CMPSS_MDOE definition of 2

 */

public static final int AUD_CMPSS_MDOE_CUSTOM_2 = 2;
/**

 * AUD_CMPSS_MDOE definition of 3

 */

public static final int AUD_CMPSS_MDOE_RF = 3;




/**

 * AUDIO_DMIX_MODE definition of   0 _____________________________________________________ AUDIO_DMIX_MODE

 */



public static final int AUD_DOWNMIX_MODE_OFF        = 0;
/**

 * AUDIO_DMIX_MODE definition of 

 */

public static final int AUD_DOWNMIX_MODE_LT_RT      = 1;
/**

 * AUDIO_DMIX_MODE definition of 

 */

public static final int AUD_DOWNMIX_MODE_STEREO     = 2;
/**

 * AUDIO_DMIX_MODE definition of 

 */

public static final int AUD_DOWNMIX_MODE_VIR_SURR   = 3;
/**

 * AUDIO_DMIX_MODE definition of 

 */

public static final int AUD_DOWNMIX_MODE_MONO       = 4;
/**

 * AUDIO_DMIX_MODE definition of 

 */

public static final int AUD_DOWNMIX_MODE_DUAL1      = 5;
/**

 * AUDIO_DMIX_MODE definition of 

 */

public static final int AUD_DOWNMIX_MODE_DUAL2      = 6;
/**

 * AUDIO_DMIX_MODE definition of 

 *

*/

public static final int AUD_DOWNMIX_MODE_DUAL_MIX   = 7;
/**

 * AUDIO_DMIX_MODE definition of 

 */

public static final int AUD_DOWNMIX_MODE_3_STEREO   = 8;


/**

 * AUDIO_DMIX_MODE definition of 

 */

public static final int AUD_DOWNMIX_MODE_DUAL_OFF   = 9;




/**

 * AUDIO_TYPE definition of 0 _____________________________________________________ AUDIO_TYPE

 */

public static final int AUDIO_TYPE_0 = 0;
/**

 * AUDIO_TYPE definition of 1

 */

public static final int AUDIO_TYPE_1 = 3;


/**

 * AUDIO_TYPE definition of 2

 */

public static final int AUDIO_TYPE_2 = 3;


/**

 * AUDIO_TYPE definition of 3

 */

public static final int AUDIO_TYPE_3 = 3;




/**

 * AUDIO_MTS definition of 0 _____________________________________________________ AUDIO_MTS

 */

public static final int AUDIO_MTS_UNKNOW    = 0;
/**

 * AUDIO_MTS Definition of 1

 */

public static final int AUD_MTS_MONO        = 1;
/**

 * AUDIO_MTS Definition of 2

 */

public static final int AUD_MTS_STEREO      = 2;
/**

 * AUDIO_MTS Definition of 3

 */

public static final int AUD_MTS_SUB_LANG    = 3;
/**

 * AUDIO_MTS Definition of 4

 */

public static final int AUD_MTS_DUAL1       = 4;
/**

 * AUDIO_MTS Definition of 5

 */

public static final int AUD_MTS_DUAL2       = 5;
/**

 * AUDIO_MTS Definition of 6

 */

public static final int AUD_MTS_NICAM_MONO  = 6;
/**

 * AUDIO_MTS Definition of 7

 */

public static final int AUD_MTS_NICAM_STEREO    = 7;
/**

 * AUDIO_MTS Definition of 8

 */

public static final int AUD_MTS_NICAM_DUAL1     = 8;
/**

 * AUDIO_MTS Definition of 9

 */

public static final int AUD_MTS_NICAM_DUAL2     = 9;
/**

 * AUDIO_MTS Definition of 10

 */

public static final int AUD_MTS_FM_MONO         = 10;
/**

 * AUDIO_MTS Definition of 11

 */

public static final int AUD_MTS_FM_STEREO       = 11;




/**

 * TIMEZONE definition   _______________________________________________TIMEZONE

 */



public static final int   TIMEZONE_AS_BROADCAST   = 0;
public static final int   TIMEZONE_GMT_P_0000     = 1;
public static final int   TIMEZONE_GMT_P_0100     = 2;
public static final int   TIMEZONE_GMT_P_0200     = 3;
public static final int   TIMEZONE_GMT_P_0300     = 4;
public static final int   TIMEZONE_GMT_P_0330     = 5;
public static final int   TIMEZONE_GMT_P_0400     = 6;
public static final int   TIMEZONE_GMT_P_0430     = 7;
public static final int   TIMEZONE_GMT_P_0500     = 8;
public static final int   TIMEZONE_GMT_P_0530     = 9;
public static final int   TIMEZONE_GMT_P_0545     = 10;
public static final int   TIMEZONE_GMT_P_0600     = 11;
public static final int   TIMEZONE_GMT_P_0630     = 12;
public static final int   TIMEZONE_GMT_P_0700     = 13;
public static final int   TIMEZONE_GMT_P_0800     = 14;
public static final int   TIMEZONE_GMT_P_0900     = 15;
public static final int   TIMEZONE_GMT_P_0930     = 16;
public static final int   TIMEZONE_GMT_P_1000     = 17;
public static final int   TIMEZONE_GMT_P_1100     = 18;
public static final int   TIMEZONE_GMT_P_1200     = 19;
public static final int   TIMEZONE_GMT_P_1245     = 20;
public static final int   TIMEZONE_GMT_P_1300     = 21;
public static final int   TIMEZONE_GMT_M_1200     = 22;
public static final int   TIMEZONE_GMT_M_1100     = 23;
public static final int   TIMEZONE_GMT_M_1000     = 24;
public static final int   TIMEZONE_GMT_M_0900     = 25;
public static final int   TIMEZONE_GMT_M_0800     = 26;
public static final int   TIMEZONE_GMT_M_0700     = 27;
public static final int   TIMEZONE_GMT_M_0600     = 28;
public static final int   TIMEZONE_GMT_M_0500     = 29;
public static final int   TIMEZONE_GMT_M_0400     = 30;
public static final int   TIMEZONE_GMT_M_0330     = 31;
public static final int   TIMEZONE_GMT_M_0300     = 32;
public static final int   TIMEZONE_GMT_M_0200     = 33;
public static final int   TIMEZONE_GMT_M_0100     = 34;
  /**

   * TIME_SYNC_MODE  definition   _______________________________________________TIMEZONE

   */

  

public static final int   TIME_SYNC_MODE_AUTO     = 0;
public static final int   TIME_SYNC_MODE_MANUAL   = 1;


  

  /**

   * BS_SRC  definition   _______________________________________________ BRDCST_SRC

   */



public static final int  BS_SRC_AIR               = 0;
public static final int  BS_SRC_CABLE             = 1;


/**

 * audio language  definition   _______________________________________________ AUD_LANG

 */

public static final int   AUD_LANG_STREAMLANG_OFF            = 0;
public static final int   AUD_LANG_STREAMLANG_ENGLISH        = 1;
public static final int   AUD_LANG_STREAMLANG_CHINESE        = 2;
public static final int   AUD_LANG_STREAMLANG_ZHONGGUO       = 3;
public static final int   AUD_LANG_STREAMLANG_ESTONIAN       = 4;
public static final int   AUD_LANG_STREAMLANG_FINNISH        = 5;
public static final int   AUD_LANG_STREAMLANG_FRENCH         = 6;
public static final int   AUD_LANG_STREAMLANG_GAELIC         = 7;
public static final int   AUD_LANG_STREAMLANG_GALICIAN       = 8;
public static final int   AUD_LANG_STREAMLANG_GERMAN         = 9;
public static final int   AUD_LANG_STREAMLANG_GREEK          = 10;
public static final int   AUD_LANG_STREAMLANG_HINDI          = 11;
public static final int   AUD_LANG_STREAMLANG_HUNGARIAN      = 12;
public static final int   AUD_LANG_STREAMLANG_ITALIAN        = 13;
public static final int   AUD_LANG_STREAMLANG_ICELANDIC      = 14;
public static final int   AUD_LANG_STREAMLANG_JAPANESE       = 15;
public static final int   AUD_LANG_STREAMLANG_KOREAN         = 16;
public static final int   AUD_LANG_STREAMLANG_MANDARIN       = 17;
public static final int   AUD_LANG_STREAMLANG_MAORI          = 18;
public static final int   AUD_LANG_STREAMLANG_NORWEGIAN      = 19;
public static final int   AUD_LANG_STREAMLANG_POLISH         = 20;
public static final int   AUD_LANG_STREAMLANG_PORTUGUESE     = 21;
public static final int   AUD_LANG_STREAMLANG_ROMANIAN       = 22;
public static final int   AUD_LANG_STREAMLANG_RUSSIAN        = 23;
public static final int   AUD_LANG_STREAMLANG_SAMI           = 24;
public static final int   AUD_LANG_STREAMLANG_SERBIAN        = 25;
public static final int   AUD_LANG_STREAMLANG_SLOVAK         = 26;
public static final int   AUD_LANG_STREAMLANG_SLOVENIAN      = 27;
public static final int   AUD_LANG_STREAMLANG_SPANISH        = 28;
public static final int   AUD_LANG_STREAMLANG_SWEDISH        = 29;
public static final int   AUD_LANG_STREAMLANG_TURKISH        = 30;
public static final int   AUD_LANG_STREAMLANG_WELSH          = 31;
public static final int   AUD_LANG_STREAMLANG_ORIGINALAUDIO  = 32;
public static final int   AUD_LANG_STREAMLANG_BASQUE         = 33;
public static final int   AUD_LANG_STREAMLANG_BULGARIAN      = 34;
public static final int   AUD_LANG_STREAMLANG_CANTONESE      = 35;
public static final int   AUD_LANG_STREAMLANG_CATALAN        = 36;
public static final int   AUD_LANG_STREAMLANG_CROATIAN       = 37;
public static final int   AUD_LANG_STREAMLANG_CZECH          = 38;
public static final int   AUD_LANG_STREAMLANG_DANISH         = 39;
public static final int   AUD_LANG_STREAMLANG_DUTCH          = 40;


/**

 * screen mode definition of unknown _______________________________________________SCREEN_MODE

 */

public static final int SCREEN_MODE_UNKNOWN = 0;
/**

 * screen mode definition of normal

 */

public static final int SCREEN_MODE_NORMAL = 1;
/**

 * screen mode definition of letterbox

 */

public static final int SCREEN_MODE_LETTERBOX = 2;
/**

 * screen mode definition of pan scan

 */

public static final int SCREEN_MODE_PAN_SCAN = 3;
/**

 * screen mode definition of user defined

 */

public static final int SCREEN_MODE_USER_DEFINED = 4;
/**

 * screen mode definition of non-linear zoom

 */

public static final int SCREEN_MODE_NON_LINEAR_ZOOM = 5;
/**

 * screen mode definition of dot by dot

 */

public static final int SCREEN_MODE_DOT_BY_DOT = 6;
/**

 * screen mode definition of custom defined 0

 */

public static final int SCREEN_MODE_CUSTOM_DEF_0 = 7;
/**

 * screen mode definition of custom defined 1

 */

public static final int SCREEN_MODE_CUSTOM_DEF_1 = 8;
/**

 * screen mode definition of custom defined 2

 */

public static final int SCREEN_MODE_CUSTOM_DEF_2 = 9;
/**

 * screen mode definition of custom defined 3

 */

public static final int SCREEN_MODE_CUSTOM_DEF_3 = 10;
/**

 * screen mode definition of custom defined 4

 */

public static final int SCREEN_MODE_CUSTOM_DEF_4 = 11;
/**

 * screen mode definition of custom defined 5

 */

public static final int SCREEN_MODE_CUSTOM_DEF_5 = 12;
/**

 * screen mode definition of custom defined 6

 */

public static final int SCREEN_MODE_CUSTOM_DEF_6 = 13;
/**

 * screen mode definition of custom defined 7

 */

public static final int SCREEN_MODE_CUSTOM_DEF_7 = 14;
/**

 * screen mode definition of none linear zoom 0

 */

public static final int SCREEN_MODE_NLZ_CUSTOM_DEF_0 = 15;
/**

 * screen mode definition of  none linear zoom 1

 */

public static final int SCREEN_MODE_NLZ_CUSTOM_DEF_1 = 16;
/**

 * screen mode definition of  none linear zoom 2

 */

public static final int SCREEN_MODE_NLZ_CUSTOM_DEF_2 = 17;
/**

 * screen mode definition of  none linear zoom 3

 */

public static final int SCREEN_MODE_NLZ_CUSTOM_DEF_3 = 18;




/**

 * picture mode _______________________________________________PICTURE_MODE

 */



/**

 * picture mode type user

 */

public static final int PICTURE_MODE_USER = 0;
/**

 * picture mode type cinema 

 */

public static final int PICTURE_MODE_CINEMA = 1;
/**

 * picture mode type sport

 */

public static final int PICTURE_MODE_SPORT = 2;
/**

 * picture mode type vivid

 */

public static final int PICTURE_MODE_VIVID = 3;
/**

 * picture mode type hi bright

 */

public static final int PICTURE_MODE_DYNAMIC  = 4;


/**

  *AUDIO_SPEAKER _______________________________________________AUDIO_SPEAKER

  */

public static final int AUDIO_SPEAKER_OFF = 0;


/**

  *

  */

public static final int AUDIO_SPEAKER_ON  = 1;


/**

  *

  */

public static final int AUDIO_SPEAKER_CEC = 2;






/**

 * audio mode type default  _______________________________________________AUDIO_MODE

 */

public static final int AUDIO_MODE_USER = 0;
/**

 * audio mode type standard

 */

public static final int AUDIO_MODE_STANDARD = 1;
/**

 * audio mode type live 1

 */

public static final int AUDIO_MODE_LIVE1 = 2;
  



/**

 * power save mode type USER_________________________________POWER_SAVE

 */    

public static final int POWER_SAVE_MODE_USER = 0;
/**

 * power save mode type bright

 */    

public static final int POWER_SAVE_MODE_BRI = 1;
/**

 * power save mode type soft

 */    

public static final int POWER_SAVE_MODE_SOFT = 2;
/**

 * power save mode type auto 1

 */    

public static final int POWER_SAVE_MODE_AUTO1 = 3;
/**

 * power save mode type auto 2

 */    

public static final int POWER_SAVE_MODE_AUTO2 = 4;
/**

 * power save mode type dynamic

 */    

public static final int POWER_SAVE_MODE_DYNAMIC = 5;
/**

 * power save mode type custom 1

 */    

public static final int POWER_SAVE_MODE_CUSTOM1 = 6;
/**

 * power save mode type custom 2

 */    

public static final int POWER_SAVE_MODE_CUSTOM2 = 7;
/**

 * gamma mode type  dark _________________________________________VID_GAMMA

 */    

public static final int VID_GAMMA_DARK = 1;
/**

 * gamma mode type  middle

 */    

public static final int VID_GAMMA_MIDDLE = 2;
/**

 * gamma mode type  bright

 */    

public static final int VID_GAMMA_BRIGHT = 3;
/**

 * color temp mode type    user _______________________________________VID_CLR_TEMP

 */ 

public static final int VID_CLR_TEMP_USER = 0;
/**

 * color temp mode type    cool

 */ 

public static final int VID_CLR_TEMP_COOL = 1;
/**

 * color temp mode type    standard

 */ 

public static final int VID_CLR_TEMP_STANDARD = 2;
/**

 * color temp mode type    warm

 */ 

public static final int VID_CLR_TEMP_WARM = 3;


/**

 * flesh tone  mode type    OFF _______________________________________ VID_FLESH_TONE

 */ 

public static final int VID_FLESH_TONE_OFF = 0;
/**

 * flesh tone  mode type     LOW

 */ 

public static final int VID_FLESH_TONE_LOW = 1;
/**

 * flesh tone  mode type     MIDDLE

 */ 

public static final int VID_FLESH_TONE_MIDDLE = 2;
/**

 * flesh tone  mode type    STRONG

 */ 

public static final int VID_FLESH_TONE_STRONG = 3;


/**

 * dnr mode type off _______________________________________________VID_DNR

 */ 

public static final int VID_DNR_OFF = 0;
/**

 * dnr mode type low

 */ 

public static final int VID_DNR_LOW = 1;
/**

 * dnr mode type medium

 */ 

public static final int VID_DNR_MEDIUM = 2;
/**

 * dnr mode type strong

 */ 

public static final int VID_DNR_STRONG = 3;
/**

 * dnr mode type auto

 */ 

public static final int VID_DNR_AUTO = 4;


/**

 * MPEG_NR mode type off _______________________________________________VID_MPEG_NR

 */ 

public static final int VID_MPEG_NR_OFF = 0;
/**

 * dnr mode type low

 */ 

public static final int VID_MPEG_NR_LOW = 1;
/**

 * dnr mode type medium

 */ 

public static final int VID_MPEG_NR_MEDIUM = 2;
/**

 * dnr mode type strong

 */ 

public static final int VID_MPEG_NR_STRONG = 3;


/**

 * DI_FILM_MODE mode type off _______________________________________________DI_FILM_MODE

 */ 

public static final int VID_DI_FILM_MODE_OFF = 0;


/**

 * DI_FILM_MODE  type SLOW

 */ 

public static final int VID_DI_FILM_MODE_SLOW_PIC = 1;




/**

 * DI_MA  type VID_DI_FILM_MODE_ACTION_PIC

 */ 

public static final int VID_DI_FILE_MODE_ACTION_PIC = 2;


/**

 * DI_MA mode type off _______________________________________________ DI_MA

*/



public static final int VID_DI_MA_SLOW_PIC = 0;


/**

 * DI_MA  type DI_MA

 */ 

public static final int VID_DI_MA_ACTION_PIC = 1;




/**

 * DI_MA mode type WEAK _______________________________________________ DI_EDGE

*/



public static final int VID_DI_EDGE_WEAK = 0;


/**

 * DI_MA  type STRONG

 */ 

public static final int VID_DI_EDGE_STRONG = 1;
















/**

 * VGA_MODE mode type off _______________________________________________VGA_MODE

 VID_VGA_MODE_COLORSPACE_AUTO =  0 



 */ 

public static final int VID_VGA_MODE_COLORSPACE_AUTO = 0;


/**

 * VGA_MODE  type FORCE_RGB

 */ 

public static final int VID_VGA_MODE_COLORSPACE_FORCE_RGB = 1;




/**

 * VGA_MODE  type FORCE_ycbcr

 */ 

public static final int VID_VGA_MODE_COLORSPACE_FORCE_YCBCR = 2;




/**

 * luma mode type off _______________________________________________VID_LUMA

 */ 

public static final int VID_LUMA_OFF = 0;
/**

 * luma mode type low

 */ 

public static final int VID_LUMA_LOW = 1;
/**

 * luma mode type medium

 */ 

public static final int VID_LUMA_MEDIUM = 2;
/**

 * luma mode type strong

 */ 

public static final int VID_LUMA_STRONG = 3;
/**

 * FT mode type off_______________________________________________VID_FTONE

 */ 

public static final int VID_FTONE_OFF = 0;
/**

 * FT mode type low

 */ 

public static final int VID_FTONE_LOW = 1;
/**

 * FT mode type medium

 */ 

public static final int VID_FTONE_MEDIUM = 2;
/**

 * FT mode type strong

 */ 

public static final int VID_FTONE_STRONG = 3;
/**

 * CTI mode type off_______________________________________________VID_CTI

 */ 

public static final int VID_CTI_OFF = 0;
/**

 * CTI mode type low

 */ 

public static final int VID_CTI_LOW = 1;
/**

 * CTI mode type medium

 */ 

public static final int VID_CTI_MEDIUM = 2;
/**

 *CTI mode type strong

 */ 

public static final int VID_CTI_STRONG = 3;
/**

  *CTI mode type strong

  */ 

public static final int VID_CTI_AUTO = 4;
 /**



 * HDMI mode type unknown _______________________________________________HDMI_MODE

 */

public static final int HDMI_MODE_UNKNOWN = 0;
/**

 * HDMI mode type auto

 */

public static final int HDMI_MODE_AUTO = 1;
/**

 * HDMI mode type graphics

 */

public static final int HDMI_MODE_GRAPHIC = 2;
/**

 * HDMI mode type video

 */

public static final int HDMI_MODE_VIDEO = 3;




 /**



 * WAKE UP REASON  

 * PCL_WAKE_UP_REASON_UNKNOWN  _______________________________________________ WAKE UP REASON

 */

public static final int WAKE_UP_REASON_UNKNOWN  = 0;




public static final int WAKE_UP_REASON_VGA      = 1;
public static final int WAKE_UP_REASON_RTC      = 2;
public static final int WAKE_UP_REASON_FP       = 3;
public static final int WAKE_UP_REASON_IRRC     = 4;
public static final int WAKE_UP_REASON_UART     = 5;
public static final int WAKE_UP_REASON_AC_POWER = 6;
public static final int WAKE_UP_REASON_HDMI     = 7;
public static final int WAKE_UP_REASON_DVD      = 8;
public static final int WAKE_UP_REASON_UART_NORMAL = 9;
/**

 *Remote controller

 */

public static final int WAKE_UP_REASON_RC_DIGIT_0   = 10;
public static final int WAKE_UP_REASON_RC_DIGIT_1   = 11;
public static final int WAKE_UP_REASON_RC_DIGIT_2   = 12;
public static final int WAKE_UP_REASON_RC_DIGIT_3   = 13;
public static final int WAKE_UP_REASON_RC_DIGIT_4   = 14;
public static final int WAKE_UP_REASON_RC_DIGIT_5   = 15;
public static final int WAKE_UP_REASON_RC_DIGIT_6   = 16;
public static final int WAKE_UP_REASON_RC_DIGIT_7   = 17;
public static final int WAKE_UP_REASON_RC_DIGIT_8   = 18;
public static final int WAKE_UP_REASON_RC_DIGIT_9   = 19;
public static final int WAKE_UP_REASON_RC_PRG_UP    = 20;
public static final int WAKE_UP_REASON_RC_PRG_DOWN  = 21;
public static final int WAKE_UP_REASON_RC_INP_SRC   = 22;
public static final int WAKE_UP_REASON_RC_ANALOG    = 23;
public static final int WAKE_UP_REASON_RC_DIGITAL   = 24;
public static final int WAKE_UP_REASON_RC_DIGITAL_ANALOG = 25;
/**

 *Front panel 

 */

public static final int WAKE_UP_REASON_FP_PRG_UP    = 26;
public static final int WAKE_UP_REASON_FP_PRG_DOWN  = 27;
public static final int WAKE_UP_REASON_FP_INP_SRC   = 28;
/**

 * to aviod enter standby  When AC instable

 */

public static final int WAKE_UP_REASON_RTC_SPECIAL  = 29;


public static final int WAKE_UP_REASON_CUSTOM_1     = 61;
public static final int WAKE_UP_REASON_CUSTOM_2     = 62;
public static final int WAKE_UP_REASON_CUSTOM_3     = 63;
public static final int WAKE_UP_REASON_CUSTOM_4     = 64;






/**

 *  VGA WAKEUP SETUP VALID indicates vga sinal can wakeup TV from standby_____________________WAKEUPSETUP

 */

public static final int WAKE_UP_SETUP_VGA_VALIDE        = 1;


public static final int WAKE_UP_SETUP_VGA_INVALIDE      = 0;




/**

 *  TTX                                          _____________________________________________________________ TTX

 */

/**

 *  TTX presentation level

 */

public static final int TTXPAGE_WESTEUROPE              = 0;
public static final int TTXPAGE_EASTEUROPE              = 1;
public static final int TTXPAGE_RUSSIA                  = 2;
public static final int TTXPAGE_RUSSIA_2                = 3;
public static final int TTXPAGE_GREEK                   = 4;
public static final int TTXPAGE_TURKEY                  = 5;
public static final int TTXPAGE_ARAB_HBRW               = 6;
public static final int TTXPAGE_FARSIAN                 = 7;
public static final int TTXPAGE_ARAB                    = 8;
public static final int TTXPAGE_BYELORUSSIAN            = 9;


/**

 *  TTX presentation level

 */

public static final int TTXLEVEL_1_5                    = 0;
public static final int TTXLEVEL_2_5                    = 1;


/**

 *  D_INTERFACE    __________________________________________________________________D_INTERFACE

 */

public static final int D_INTERFACE_OP_GET_NORMAL   = 0;
public static final int D_INTERFACE_OP_GET_MIN_MAX  = 1;
public static final int D_INTERFACE_OP_SET_NORMAL   = 2;




/**

 * FBM_MODE definition

 */

public static final int FBM_MODE_1_TVMM      = 1;
public static final int FBM_MODE_2_ANDROID   = 2;


/**

 *  END _______________________________________________

 */

}

