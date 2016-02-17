package com.mediatek.tvcm;

import java.lang.reflect.Field;

import android.content.Context;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.common.ConfigValue;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.service.ConfigService;

/**
 * 
 * Configuration component implementation. A large TVOption set with TV
 * configurations.
 * 
 * </pre>
 * 
 * The component has implemented some helper class to create some common
 * configurations. The helper classes will have same name from TVManager.
 * Examples:
 * 
 * <pre>
 * 
 * </pre>
 * 
 * 
 * 
 * @Override
 * @author mtk40063
 * 
 */
public class TVConfigurer extends TVComponent {
	protected ConfigService rawSrv;

	/**
	 * constructed function
	 * 
	 * @param context
	 */
	protected TVConfigurer(Context context) {
		super(context);
		if (!TVContent.dummyMode) {
			rawSrv = (ConfigService) getTVMngr().getService(
					ConfigService.ConfigServiceName);
		}
		// TODO Auto-generated constructor stub
	}

	public static final int CFG_GRP_DTV = ConfigType.CONFIG_VALUE_GROUP_DTV;
	public static final int CFG_GRP_ATV = ConfigType.CONFIG_VALUE_GROUP_ATV;
	public static final int CFG_GRP_AV = ConfigType.CONFIG_VALUE_GROUP_AV;
	public static final int CFG_GRP_COMPONENT = ConfigType.CONFIG_VALUE_GROUP_COMPONENT;
	public static final int CFG_GRP_HDMI = ConfigType.CONFIG_VALUE_GROUP_HDMI;
	public static final int CFG_GRP_DVI = ConfigType.CONFIG_VALUE_GROUP_DVI;
	public static final int CFG_GRP_VGA = ConfigType.CONFIG_VALUE_GROUP_VGA;
	// This is a special group for : When we leave TV... ...wtf....
	public static final int CFG_GRP_LEAVE = ConfigType.CONFIG_VALUE_GROUP_MMP;
	public static final String FACTORY_TV_DBOX="factory_tv_dbox";
	
	public static final String FACTORY_TV_DBOX_CVBS_DELAY="factory_tv_dbox_cvbs_delay";
	public static final String FACTORY_TV_DBOX_TVD_CHECK="factory_tv_dbox_tvd_check";
	public static final String FACTORY_TV_DBOX_CVBS_SWING="factory_tv_dbox_cvbs_swing";
	public static final String FACTORY_TV_DBOX_SNOW_GEN="factory_tv_dbox_snow_gen";
	public static final String FACTORY_TV_DBOX_TVD_GAIN="factory_tv_dbox_tvd_gain";
	public static final String FACTORY_TV_DBOX_VLOCK_RATIO="factory_tv_dbox_vlock_ratio";

	public static final int CFG_3D_MODE = 0;
	public static final int CFG_LR_SWITCH = 1;
	public static final int CFG_DEPTH_FIELD = 2;
	public static final int CFG_PROTRUDE = 3;
	public static final int CFG_DISTANCE_TV = 4;
	public static final int CFG_3D_2D = 5;
	public static final int CFG_OSD_DEPTH = 6;
	public static final int CFG_FPR = 7;
	public static final int CFG_3D_NAV = 8;
	public static final int CFG_IMG_SAFETY = 9;
	int num3dMode = 11;

	void setGroup(int idx) {
		ConfigValue configValue = new ConfigValue();
		configValue.setIntValue(idx);
		try {
			rawSrv.setCfg(ConfigType.CFG_VALUE_GROUP, configValue);
//			rawSrv.updateCfg(ConfigType.CFG_VALUE_GROUP);
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int getGroup() {
		ConfigValue configValue = new ConfigValue();
		try {
			configValue = rawSrv.getCfg(ConfigType.CFG_VALUE_GROUP);
			return configValue.getIntValue();
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return CFG_GRP_ATV;
	}

	protected boolean rawSetInt(String name, int val) {
		ConfigValue rawVal = new ConfigValue();
		rawVal.setIntValue(val);
		try {
			rawSrv.setCfg(name, rawVal);
			rawSrv.updateCfg(name);
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	protected int rawGetInt(String name) {
		ConfigValue rawVal;
		try {
			rawVal = rawSrv.getCfg(name);
			return rawVal.getIntValue();
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Reset to User settings
	 */
	public void resetUser() {
		try {
			rawSrv.resetCfgGroup(ConfigType.RESET_USER);
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Reset to Factory settings
	 */
	public void resetFactory() {
		try {
			rawSrv.resetCfgGroup(ConfigType.RESET_FACTORY);
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//add for Descrambler box ; please don't use for other thing!!!! 
	private class IntegerArrayRangeProxyOption extends TVOptionRange<Integer> {

		private ConfigService rawSrv = null;
		private int min;
		private int max;
//		private int defaultValue;
		private String name;
		private int dummyVal;
		private int indexC;


		public Integer getMax() {
			
				return new Integer(max);
			
		}

		public Integer getMin() {

				return new Integer(min);
			
		}

		public Integer get() {
			ConfigValue rawVal;
			// For dummy
			if (rawSrv == null) {
				return dummyVal;
			}
			try {
				rawVal = rawSrv.getCfg(ConfigType.CFG_DESCRAMBLER);
				
				return new Integer(rawVal.getIntArrayValue()[indexC]);
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO: Throw exception...
			return null;
		}

		public boolean set(Integer val) {
			// For dummy
			if (rawSrv == null) {
				System.out.println(" CONFIG: ConfigType.CFG_DESCRAMBLER"  + " val:" + val);
				dummyVal = val.intValue();
				return true;
			}
		
			try {
				ConfigValue rawVal = rawSrv.getCfg(ConfigType.CFG_DESCRAMBLER);;
				int[] arr=rawVal.getIntArrayValue();
				if(arr!=null){
					arr[indexC]=val.intValue();
					rawVal.setIntArrayValue(arr);
				}
				rawSrv.setCfg(ConfigType.CFG_DESCRAMBLER, rawVal);
				rawSrv.updateCfg(ConfigType.CFG_DESCRAMBLER);
			
				return true;
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO: Throw exception?...
			return false;
		}

		public IntegerArrayRangeProxyOption(TVConfigurer cfger, String cfgName,
				int defaultValue, int min, int max,int index) {
			dummyVal = defaultValue;
			this.min = min;
			this.max = max;
			this.name = cfgName;
			this.rawSrv = cfger.getRawSrv();
//			this.defaultValue = defaultValue;
			this.indexC=index;

		}
	}

	static class IntegerRangeProxyOption extends TVOptionRange<Integer> {
		private ConfigService rawSrv = null;
		private int min;
		private int max;
		private int defaultValue;
		private String name;
		private int dummyVal;

		boolean hotMinMax; // If min and max is not constant

		public Integer getMax() {
			if (hotMinMax) {
				ConfigValue rawMinMax = new ConfigValue();
				try {
					rawSrv.getCfgMinMax(name, rawMinMax);
					// TODO: FIX HERE
					return 100;
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				return new Integer(max);
			}
			// ....
			return 100;
		}

		public Integer getMin() {
			if (hotMinMax) {
				ConfigValue rawMinMax = new ConfigValue();
				try {
					rawSrv.getCfgMinMax(name, rawMinMax);
					// TODO: FIX HERE
					return 0;
				} catch (TVMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				return new Integer(min);
			}
			// ...
			return 0;
		}

		public Integer getDefault() {
			return new Integer(defaultValue);
		}

		public Integer get() {
			ConfigValue rawVal;
			// For dummy
			if (rawSrv == null) {
				return new Integer(dummyVal);
			}
			try {
				rawVal = rawSrv.getCfg(name);

				return new Integer(rawVal.getIntValue());
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO: Throw exception...
			return null;
		}

		public boolean set(Integer val) {
			// For dummy
			if (rawSrv == null) {
				System.out.println(" CONFIG:" + name + " val:" + val);
				dummyVal = val.intValue();
				return true;
			}
			ConfigValue rawVal = new ConfigValue();
			rawVal.setIntValue(val.intValue());
			try {
				rawSrv.setCfg(name, rawVal);
				rawSrv.updateCfg(name);
				return true;
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO: Throw exception?...
			return false;
		}

		public IntegerRangeProxyOption(TVConfigurer cfger, String cfgName,
				int defaultValue, int min, int max) {
			dummyVal = defaultValue;
			this.min = min;
			this.max = max;
			this.hotMinMax = false;
			this.name = cfgName;
			this.rawSrv = cfger.getRawSrv();
			this.defaultValue = defaultValue;

		}

		public IntegerRangeProxyOption(TVConfigurer cfger, String cfgName,
				int defaultValue) {
			dummyVal = defaultValue;
			this.hotMinMax = true;
			this.name = cfgName;
			this.rawSrv = cfger.getRawSrv();
			this.defaultValue = defaultValue;

		}

		// public IntegerRangeProxyOption(TVConfigurer cfger, String cfgName,
		// int defaultValue, int min, int max, boolean canSetMap, int cfgType) {
		// this(cfger, cfgName, defaultValue, min, max);
		//			
		// this.canSetMap = true;
		// this.cfgType = cfgType;
		// }

	}
	
	private void addIntegerArrayRangeProxyOption(String name,int defaultValue,int min,int max,int index){
		addOption(name, new IntegerArrayRangeProxyOption(this, name, defaultValue, min, max, index));
	}

	protected void addIntegerRangeProxyOption(String name, int defaultValue,
			int min, int max) {
		addOption(name, new IntegerRangeProxyOption(this, name, defaultValue,
				min, max));
	}

	protected void addIntegerRangeProxyOption(String name, String rawName,
			int defaultValue, int min, int max) {
		addOption(name, new IntegerRangeProxyOption(this, rawName,
				defaultValue, min, max));
	}

	protected void addIntegerRangeProxyOption(String name, String rawName,
			int defaultValue) {
		addOption(name,
				new IntegerRangeProxyOption(this, rawName, defaultValue));
	}

	protected void addIntegerRangeProxyOption(String name, int defaultValue) {
		addOption(name, new IntegerRangeProxyOption(this, name, defaultValue));
	}

	ConfigService getRawSrv() {
		return rawSrv;
	}

	void init() {
		addIntegerArrayRangeProxyOption(FACTORY_TV_DBOX, 0, 0, 1, 0);
		addIntegerArrayRangeProxyOption(FACTORY_TV_DBOX_CVBS_DELAY, 0, 0, 99999, 1);
		addIntegerArrayRangeProxyOption(FACTORY_TV_DBOX_CVBS_SWING, 0, 0, 99999, 3);
		addIntegerArrayRangeProxyOption(FACTORY_TV_DBOX_SNOW_GEN, 0, 0, 1, 4);
		addIntegerArrayRangeProxyOption(FACTORY_TV_DBOX_TVD_CHECK, 0, 0, 99999, 2);
		addIntegerArrayRangeProxyOption(FACTORY_TV_DBOX_TVD_GAIN, 0, 0, 99999, 5);
		addIntegerArrayRangeProxyOption(FACTORY_TV_DBOX_VLOCK_RATIO, 0, 0, 99999, 6);
                addIntegerRangeProxyOption(ConfigType.CFG_FBM_MODE, 1, 1, 2);
		addIntegerRangeProxyOption(ConfigType.CFG_TTX_DIGITAL_LANG, 0, 0, 40);
		addIntegerRangeProxyOption(ConfigType.CFG_TTX_DECODE_PAGE, 0, 0, 9);
		addIntegerRangeProxyOption(ConfigType.CFG_TTX_PRESENTATION_LVL, 0, 0, 1);
		addIntegerRangeProxyOption(ConfigType.CFG_3D_DEPTH_OF_FIELD, 16, 0, 32);
		addIntegerRangeProxyOption(ConfigType.CFG_3D_LR_SWITCH,
				ConfigType.COMMON_OFF, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_3D_MODE,
				ConfigType.CFG_3D_MODE_OFF, ConfigType.CFG_3D_MODE_OFF,
				ConfigType.CFG_3D_MODE_CHK_BOARD);
		addIntegerRangeProxyOption(ConfigType.CFG_3D_NAV_AUTO,
				ConfigType.CFG_VID_3D_NAV_AUTO_CHG_SEMI_AUTO,
				ConfigType.CFG_VID_3D_NAV_AUTO_CHG_MANUAL,
				ConfigType.CFG_VID_3D_NAV_AUTO_CHG_AUTO);
		// helperRangeAdd(ConfigType.CFG_3D_TO_2D, ConfigType.CFG_3D_TO_2D_OFF,
		// ConfigType.CFG_3D_TO_2D_OFF,
		// ConfigType.CFG_3D_TO_2D_RIGHT);
		addIntegerRangeProxyOption(ConfigType.CFG_3D_TO_2D,
				ConfigType.COMMON_OFF, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_3D_DISTANCE, 10, 2, 18);
		addIntegerRangeProxyOption(ConfigType.CFG_3D_NAV_TAG, 0, 0, 10); 
		addIntegerRangeProxyOption(ConfigType.CFG_IMG_SFTY,
				ConfigType.CFG_VID_3D_IMG_STFY_OFF,
				ConfigType.CFG_VID_3D_IMG_STFY_OFF,
				ConfigType.CFG_VID_3D_IMG_STFY_HIGH);
		addIntegerRangeProxyOption(ConfigType.CFG_3D_VIEW_POINT, 0, 0, 32);

		addIntegerRangeProxyOption(ConfigType.CFG_AUDIO_DELAY, 0, 0, 30);
		addIntegerRangeProxyOption(ConfigType.CFG_AUDIO_MODE,
				ConfigType.AUDIO_MODE_USER, ConfigType.AUDIO_MODE_USER,
				ConfigType.AUDIO_MODE_LIVE1);
		addIntegerRangeProxyOption(ConfigType.CFG_AVCMODE,
				ConfigType.COMMON_ON, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_BACKLIGHT, 50, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_BALANCE, 0, -50, 50);
		addIntegerRangeProxyOption(ConfigType.CFG_BLUE_SCREEN,
				ConfigType.COMMON_ON, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_BLUE_STRETCH,
				ConfigType.COMMON_OFF, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_BRIGHTNESS, 50, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_CLOCK, 0, 0, 255);
		addIntegerRangeProxyOption(ConfigType.CFG_COLOR_GAIN_B, 0, -20, 20);
		addIntegerRangeProxyOption(ConfigType.CFG_COLOR_GAIN_G, 0, -20, 20);
		addIntegerRangeProxyOption(ConfigType.CFG_COLOR_GAIN_R, 0, -20, 20);
		addIntegerRangeProxyOption(ConfigType.CFG_COLOR_OFFSET_B, 0, -20, 20);
		addIntegerRangeProxyOption(ConfigType.CFG_COLOR_OFFSET_G, 0, -20, 20);
		addIntegerRangeProxyOption(ConfigType.CFG_COLOR_OFFSET_R, 0, -20, 20);
		addIntegerRangeProxyOption(ConfigType.CFG_CONTRAST, 50, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_CTI,
				ConfigType.VID_CTI_MEDIUM, ConfigType.VID_CTI_OFF,
				ConfigType.VID_CTI_STRONG);

		addIntegerRangeProxyOption(ConfigType.CFG_EQUALIZE,
				ConfigType.CFG_AUD_SE_EQ_OFF, ConfigType.CFG_AUD_SE_EQ_OFF,
				ConfigType.CFG_AUD_SE_EQ_SOFT);
		addIntegerRangeProxyOption(ConfigType.CFG_EQ_BAND_1, 0, -60, 60);
		addIntegerRangeProxyOption(ConfigType.CFG_EQ_BAND_2, 0, -60, 60);
		addIntegerRangeProxyOption(ConfigType.CFG_EQ_BAND_3, 0, -60, 60);
		addIntegerRangeProxyOption(ConfigType.CFG_EQ_BAND_4, 0, -60, 60);
		addIntegerRangeProxyOption(ConfigType.CFG_EQ_BAND_5, 0, -60, 60);
		addIntegerRangeProxyOption(ConfigType.CFG_EQ_BAND_6, 0, -60, 60);
		addIntegerRangeProxyOption(ConfigType.CFG_EQ_BAND_7, 0, -60, 60);

		addIntegerRangeProxyOption(ConfigType.CFG_FLESH_TONE,
				ConfigType.COMMON_ON, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_GAMMA,
				ConfigType.VID_GAMMA_MIDDLE, ConfigType.VID_GAMMA_DARK,
				ConfigType.VID_GAMMA_BRIGHT);
		addIntegerRangeProxyOption(ConfigType.CFG_H_POSITION, 0, -16, 16);
		addIntegerRangeProxyOption(ConfigType.CFG_H_SIZE, 0, -5, 5);
		addIntegerRangeProxyOption(ConfigType.CFG_HUE, 0, -50, 50);
		addIntegerRangeProxyOption(ConfigType.CFG_LUMA, ConfigType.COMMON_OFF,
				ConfigType.COMMON_OFF, ConfigType.VID_LUMA_STRONG);
		addIntegerRangeProxyOption(ConfigType.CFG_MAGIC_AV_SYSTEM,
				ConfigType.COMMON_OFF, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_NR,
				ConfigType.VID_DNR_MEDIUM, ConfigType.VID_DNR_OFF,
				ConfigType.VID_DNR_AUTO);
		addIntegerRangeProxyOption(ConfigType.CFG_3DNR,
				ConfigType.VID_DNR_MEDIUM, ConfigType.VID_DNR_OFF,
				ConfigType.VID_DNR_AUTO);
		addIntegerRangeProxyOption(ConfigType.CFG_PHASE, 12, 0, 31);
		addIntegerRangeProxyOption(ConfigType.CFG_YPBPR_PHASE, 12, 0, 31);
		addIntegerRangeProxyOption(ConfigType.CFG_PICTURE_MODE,
				ConfigType.PICTURE_MODE_USER, ConfigType.PICTURE_MODE_USER,
				ConfigType.PICTURE_MODE_HI_BRIGHT);
		addIntegerRangeProxyOption(ConfigType.CFG_POWER_SAVE,
				ConfigType.POWER_SAVE_MODE_USER,
				ConfigType.POWER_SAVE_MODE_USER,
				ConfigType.POWER_SAVE_MODE_DYNAMIC);
		addIntegerRangeProxyOption(ConfigType.CFG_SATURATION, 50, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_SCREEN_MODE,
				ConfigType.SCREEN_MODE_NORMAL, ConfigType.SCREEN_MODE_UNKNOWN,
				ConfigType.SCREEN_MODE_NLZ_CUSTOM_DEF_3);
		addIntegerRangeProxyOption(ConfigType.CFG_SHARPNESS, 4, 0, 20);
		addIntegerRangeProxyOption(ConfigType.CFG_SOUND_FIELD_LOCATE_MODE,
				ConfigType.SOUND_FIELD_LOCATE_MODE_DESK,
				ConfigType.SOUND_FIELD_LOCATE_MODE_WALL,
				ConfigType.SOUND_FIELD_LOCATE_MODE_DESK);
		// helperRangeAdd(ConfigType.CFG_SPDIF_MODE,
		// ConfigType.AUD_SPDIF_FMT_PCM_24,
		// ConfigType.AUD_SPDIF_FMT_OFF,
		// ConfigType.AUD_SPDIF_FMT_PCM_24);
		addIntegerRangeProxyOption(ConfigType.CFG_SPDIF_MODE,
				ConfigType.SPDIF_MODE_FMT_PCM24, ConfigType.SPDIF_MODE_FMT_OFF,
				ConfigType.SPDIF_MODE_FMT_PCM24);
		addIntegerRangeProxyOption(ConfigType.CFG_SPEAKER_MODE,
				ConfigType.SPEAKER_MODE_INTERNAL,
				ConfigType.SPEAKER_MODE_INTERNAL,
				ConfigType.SPEAKER_MODE_EXTERNAL);
		addIntegerRangeProxyOption(ConfigType.CFG_SRS_MODE,
				ConfigType.COMMON_ON, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_TEMPERATURE,
				ConfigType.VID_CLR_TEMP_USER, ConfigType.VID_CLR_TEMP_USER,
				ConfigType.VID_CLR_TEMP_WARM);
		addIntegerRangeProxyOption(ConfigType.CFG_V_POSITION, 0, -16, 16);
		addIntegerRangeProxyOption(ConfigType.CFG_V_SIZE, 0, -5, 5);

		addIntegerRangeProxyOption(ConfigType.CFG_VOLUME, 20, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_AD_VOLUME, 20, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_WHITE_PEAK_LMT,
				ConfigType.COMMON_ON, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);

		addIntegerRangeProxyOption(ConfigType.CFG_SPDIF_DELAY, 0, 0, 25);
		addIntegerRangeProxyOption(ConfigType.CFG_AUD_CHANNEL, 0, 0, 2);
		// addIntegerRangeProxyOption(ConfigType.CFG_CH_FRZ_CHG, 0, 0, 1);

		addIntegerRangeProxyOption(ConfigType.CFG_SPEAKER,
				ConfigType.AUDIO_SPEAKER_ON, ConfigType.AUDIO_SPEAKER_OFF,
				ConfigType.AUDIO_SPEAKER_CEC);
		addIntegerRangeProxyOption(ConfigType.CFG_AUD_BASS, 50, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_DOLBY_BANNER,
				ConfigType.COMMON_OFF, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_DOLBY_CMPR_MODE,
				ConfigType.AUD_CMPSS_MDOE_LINE, ConfigType.AUD_CMPSS_MDOE_LINE,
				ConfigType.AUD_CMPSS_MDOE_RF);

		addIntegerRangeProxyOption(ConfigType.CFG_DOLBY_CMPR_FACTOR,
				ConfigType.AUD_DOLBY_DRC_FULL, ConfigType.AUD_DOLBY_DRC_OFF,
				ConfigType.AUD_DOLBY_DRC_FULL);

		addIntegerRangeProxyOption(ConfigType.CFG_AUDIO_TYPE,
				ConfigType.AUDIO_TYPE_0, ConfigType.AUDIO_TYPE_0,
				ConfigType.AUDIO_TYPE_3);

		addIntegerRangeProxyOption(ConfigType.CFG_VGA_POS_H, 0, 0, 100);

		addIntegerRangeProxyOption(ConfigType.CFG_VGA_POS_V, 0, 0, 100);

		addIntegerRangeProxyOption(ConfigType.CFG_VGA_PHASE, 0, 0, 31);
		addIntegerRangeProxyOption(ConfigType.CFG_VGA_CLOCK, 0, 0, 255);

		addIntegerRangeProxyOption(ConfigType.CFG_MPEG_NR,
				ConfigType.VID_MPEG_NR_LOW, ConfigType.VID_MPEG_NR_OFF,
				ConfigType.VID_MPEG_NR_STRONG);

		addIntegerRangeProxyOption(ConfigType.CFG_DI_FILM_MODE,
				ConfigType.VID_DI_FILE_MODE_ACTION_PIC,
				ConfigType.VID_DI_FILM_MODE_OFF,
				ConfigType.VID_DI_FILE_MODE_ACTION_PIC);

		addIntegerRangeProxyOption(ConfigType.CFG_VGAMODE,
				ConfigType.VID_VGA_MODE_COLORSPACE_FORCE_RGB,
				ConfigType.VID_VGA_MODE_COLORSPACE_AUTO,
				ConfigType.VID_VGA_MODE_COLORSPACE_FORCE_YCBCR);

		addIntegerRangeProxyOption(ConfigType.CFG_HDMI_MODE,
				ConfigType.HDMI_MODE_GRAPHIC, ConfigType.HDMI_MODE_AUTO,
				ConfigType.HDMI_MODE_VIDEO);

		addIntegerRangeProxyOption(ConfigType.CFG_DI_MA,
				ConfigType.VID_DI_MA_ACTION_PIC, ConfigType.VID_DI_MA_SLOW_PIC,
				ConfigType.VID_DI_MA_ACTION_PIC);

		addIntegerRangeProxyOption(ConfigType.CFG_DI_EDGE,
				ConfigType.VID_DI_EDGE_WEAK, ConfigType.VID_DI_EDGE_WEAK,
				ConfigType.VID_DI_EDGE_STRONG);

		addIntegerRangeProxyOption(ConfigType.CFG_WCG, ConfigType.COMMON_ON,
				ConfigType.COMMON_OFF, ConfigType.COMMON_ON);

		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_FLIP,
				ConfigType.COMMON_OFF, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_MIRROR,
				ConfigType.COMMON_OFF, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_CH_FRZ_CHG,
				ConfigType.COMMON_OFF, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);

		addIntegerRangeProxyOption(ConfigType.CFG_AUD_TREBLE, 50, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_AUD_MTS,
				ConfigType.AUD_MTS_STEREO, ConfigType.AUDIO_MTS_UNKNOW,
				ConfigType.AUD_MTS_FM_STEREO);
		addIntegerRangeProxyOption(ConfigType.CFG_WAKE_UP_REASON,
				ConfigType.WAKE_UP_REASON_UNKNOWN,
				ConfigType.WAKE_UP_REASON_UNKNOWN,
				ConfigType.WAKE_UP_REASON_CUSTOM_4);

		addIntegerRangeProxyOption(ConfigType.CFG_AUD_DMIX,
				ConfigType.AUD_DOWNMIX_MODE_DUAL_OFF,
				ConfigType.AUD_DOWNMIX_MODE_OFF,
				ConfigType.AUD_DOWNMIX_MODE_DUAL_OFF);
		addIntegerRangeProxyOption(ConfigType.CFG_AUDIO_ONLY,
				ConfigType.COMMON_OFF, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);

		// addIntegerRangeProxyOption(ConfigType.AUTO_TYPE_VGA_ADJUST,
		// 0, 0,
		// 0);

		addIntegerRangeProxyOption(ConfigType.CFG_WME_MODE,
				ConfigType.COMMON_OFF, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_GAME_MODE,
				ConfigType.COMMON_OFF, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_BRI_MAX, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_BRI_MID, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_BRI_MIN, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_CNT_MAX, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_CNT_MID, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_CNT_MIN, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_HUE_MAX, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_HUE_MID, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_HUE_MIN, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_SHP_MAX, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_SHP_MID, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_SHP_MIN, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_VID_MAX, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_VID_MID, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_VIDEO_CURVE_VID_MIN, 0, 0,
				255);
		addIntegerRangeProxyOption(ConfigType.CFG_MEMC, ConfigType.COMMON_OFF,
				ConfigType.COMMON_OFF, ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_MEMC_DEMO,
				ConfigType.COMMON_OFF, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_EQUALIZE_10KHZ, 0, -60, 60);
		addIntegerRangeProxyOption(ConfigType.CFG_EQUALIZE_120HZ, 0, -60, 60);
		addIntegerRangeProxyOption(ConfigType.CFG_EQUALIZE_1500HZ, 0, -60, 60);
		addIntegerRangeProxyOption(ConfigType.CFG_EQUALIZE_500HZ, 0, -60, 60);
		addIntegerRangeProxyOption(ConfigType.CFG_EQUALIZE_5KHZ, 0, -60, 60);
		addIntegerRangeProxyOption(ConfigType.CFG_FAC_BL_BRI, 0, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_FAC_BL_OPT_1, 0, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_FAC_BL_OPT_2, 0, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_FAC_BL_OPT_3, 0, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_FAC_BL_OPT_4, 0, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_FAC_BL_OPT_5, 0, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_FAC_BL_OPT_6, 0, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_FAC_BL_SOFT, 0, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_FAC_BL_USER, 0, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_CTI,
				ConfigType.VID_CTI_MEDIUM, ConfigType.VID_CTI_OFF,
				ConfigType.VID_CTI_STRONG);
		addIntegerRangeProxyOption(ConfigType.CFG_ASPECT_RATIO,
				ConfigType.SCREEN_MODE_NORMAL, ConfigType.SCREEN_MODE_NORMAL,
				ConfigType.SCREEN_MODE_DOT_BY_DOT);

		addIntegerRangeProxyOption(ConfigType.CFG_AUDIO_CURVE_20, 0, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_AUDIO_CURVE_80, 0, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_AUDIO_CURVE_MAX, 0, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_AUDIO_CURVE_MID, 0, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_AUDIO_CURVE_MIN, 0, 0, 100);

		addIntegerRangeProxyOption(ConfigType.CFG_VOLUME_FRONT_LEFT, 20, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_VOLUME_FRONT_RIGHT, 20, 0,
				100);
		addIntegerRangeProxyOption(ConfigType.CFG_VOLUME_REAR_LEFT, 20, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_VOLUME_REAR_RIGHT, 20, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_VOLUME_CENTER, 20, 0, 100);
		addIntegerRangeProxyOption(ConfigType.CFG_VOLUME_SUB_WOOFER, 20, 0, 100);
		addIntegerRangeProxyOption(ConfigType.POWER_ON_MUSIC,
				ConfigType.COMMON_ON, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.POWER_OFF_MUSIC,
				ConfigType.COMMON_ON, ConfigType.COMMON_OFF,
				ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_DPMS, ConfigType.COMMON_ON,
				ConfigType.COMMON_ON, ConfigType.COMMON_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_WAKEUP_VGA_SETUP,
				ConfigType.WAKE_UP_SETUP_VGA_INVALIDE,
				ConfigType.WAKE_UP_SETUP_VGA_INVALIDE,
				ConfigType.WAKE_UP_SETUP_VGA_VALIDE);

		addIntegerRangeProxyOption(ConfigType.CFG_POWER_ON_TIMER, 0, 0, 86400);
		addIntegerRangeProxyOption(ConfigType.CFG_TIME_SYNC,
				ConfigType.TIME_SYNC_MODE_AUTO, ConfigType.TIME_SYNC_MODE_AUTO,
				ConfigType.TIME_SYNC_MODE_MANUAL);

		addIntegerRangeProxyOption(ConfigType.CFG_MJC_FCT,
				ConfigType.CFG_MJC_OFF, ConfigType.CFG_MJC_OFF,
				ConfigType.CFG_MJC_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_MJC_MODE,
				ConfigType.CFG_MJC_MODE_NONE, ConfigType.CFG_MJC_MODE_0,
				ConfigType.CFG_MJC_MODE_NONE);

		addIntegerRangeProxyOption(ConfigType.CFG_MJC_EFFECT,
				ConfigType.CFG_MJC_EFFECT_OFF, ConfigType.CFG_MJC_EFFECT_OFF,
				ConfigType.CFG_MJC_EFFECT_HIGH);
		addIntegerRangeProxyOption(ConfigType.CFG_MJC_DEMO,
				ConfigType.CFG_MJC_DEMO_OFF, ConfigType.CFG_MJC_DEMO_OFF,
				ConfigType.CFG_MJC_DEMO_LEFT);
		
		//===================================//
		addIntegerRangeProxyOption(ConfigType.CFG_PQ_DEMO_FUNC,
				ConfigType.CFG_PQ_DEMO_FUNC_OFF, ConfigType.CFG_PQ_DEMO_FUNC_OFF,
				ConfigType.CFG_PQ_DEMO_FUNC_ON);
		addIntegerRangeProxyOption(ConfigType.CFG_PQ_DEMO_TYPE,
				ConfigType.CFG_PQ_DEMO_TYPE_OFF, ConfigType.CFG_PQ_DEMO_TYPE_OFF,
				ConfigType.CFG_PQ_DEMO_TYPE_LED_BL_Control);
		addIntegerRangeProxyOption(ConfigType.CFG_PQ_DEMO_ACTION,
				ConfigType.CFG_PQ_DEMO_ACTION_OFF, ConfigType.CFG_PQ_DEMO_ACTION_OFF,
				ConfigType.CFG_PQ_DEMO_ACTION_LEFT);
		//===================================//
		
		addIntegerRangeProxyOption(ConfigType.CFG_AUD_OUTDEV,
				ConfigType.AUD_OUTDEV_SPEAKER, ConfigType.CFG_MJC_DEMO_OFF,
				ConfigType.AUD_OUTDEV_BLUETEETH);

	}

	public boolean checkType(int optionName) throws TVMException {
		if (optionName < CFG_3D_MODE || optionName > CFG_IMG_SAFETY)
			return false;

		ConfigValue cfgValueHigh = rawSrv
				.getCfg(ConfigType.CFG_VIDEO_3D_CTRL_CAP_HIGH16);
		ConfigValue cfgValueLow = rawSrv
				.getCfg(ConfigType.CFG_VIDEO_3D_CTRL_CAP_LOW16);
		int cfgValue = ((cfgValueHigh.getIntValue()) << 16)
				+ (cfgValueLow.getIntValue() & 0xFFFF);

		if (((cfgValue >> (optionName * 2)) & 0x3) == 3)
			return true;
		else
			return false;
	}

	public boolean[] get3DModeState() throws TVMException {
		boolean[] optionFeature = new boolean[num3dMode];
		ConfigValue featureValueHigh = rawSrv
				.getCfg(ConfigType.CFG_VIDEO_3D_MODE_CAP_HIGH16);// get from dtv
		ConfigValue featureValueLow = rawSrv
				.getCfg(ConfigType.CFG_VIDEO_3D_MODE_CAP_LOW16);
		int featureValue = ((featureValueHigh.getIntValue()) << 16)
				+ (featureValueLow.getIntValue() & 0xFFFF);

		for (int i = 0; i < num3dMode; i++) {
			if (((featureValue >> (i * 2)) & 0x3) == 3)
				optionFeature[i] = true;
			else
				optionFeature[i] = false;
		}

		return optionFeature;
	}

	@SuppressWarnings("unchecked")
	public int check3DModeSubTpye(boolean left) throws TVMException {
		TVOptionRange<Integer> opt = (TVOptionRange<Integer>) getOption(ConfigType.CFG_3D_MODE);
		int curOpt = opt.get();
		boolean state[] = get3DModeState();

		if (left) {
			int preOpt = curOpt - 1;
			while (preOpt >= 0) {
				if (state[preOpt] == true)
					return preOpt;
				else
					preOpt--;
			}
			preOpt = num3dMode - 1;
			while (preOpt >= curOpt) {
				if (state[preOpt] == true)
					return preOpt;
				else
					preOpt--;
			}
		} else {
			int nextOpt = curOpt + 1;
			while (nextOpt < num3dMode) {
				if (state[nextOpt] == true)
					return nextOpt;
				else
					nextOpt++;
			}
			nextOpt = 0;
			while (nextOpt <= curOpt) {
				if (state[nextOpt] == true)
					return nextOpt;
				else
					nextOpt++;
			}
		}
		return curOpt;
	}

}
