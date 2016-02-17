package com.mediatek.tv.common;

import java.util.HashMap;
import java.util.Map;

import com.mediatek.tv.service.Logger;

public class ChannelCommon {
	private static final String TAG = "ChannelCommon";
	/** Unknown TV (sound) system. */
	public static final int TV_SYS_UNKNOWN = 0;
	/** TV (sound) system A. */
	public static final int TV_SYS_A = 1 << 0;
	/** TV (sound) system B. */
	public static final int TV_SYS_B = 1 << 1;
	/** TV (sound) system C. */
	public static final int TV_SYS_C = 1 << 2;
	/** TV (sound) system D. */
	public static final int TV_SYS_D = 1 << 3;
	/** TV (sound) system E. */
	public static final int TV_SYS_E = 1 << 4;
	/** TV (sound) system F. */
	public static final int TV_SYS_F = 1 << 5;
	/** TV (sound) system G. */
	public static final int TV_SYS_G = 1 << 6;
	/** TV (sound) system H. */
	public static final int TV_SYS_H = 1 << 7;
	/** TV (sound) system I. */
	public static final int TV_SYS_I = 1 << 8;
	/** TV (sound) system J. */
	public static final int TV_SYS_J = 1 << 9;
	/** TV (sound) system K. */
	public static final int TV_SYS_K = 1 << 10;
	/** TV (sound) system K_PRIME. */
	public static final int TV_SYS_K_PRIME = 1 << 11;
	/** TV (sound) system L. */
	public static final int TV_SYS_L = 1 << 12;
	/** TV (sound) system L_PRIME. */
	public static final int TV_SYS_L_PRIME = 1 << 13;
	/** TV (sound) system M. */
	public static final int TV_SYS_M = 1 << 14;
	/** TV (sound) system N. */
	public static final int TV_SYS_N = 1 << 15;

	/** TV (sound) system AUTO. */
	public static final int TV_SYS_AUTO = 1 << 31;

	/** Unknown TV color system. */
	public static final int COLOR_SYS_UNKNOWN = -1;
	/** TV color system NTSC. */
	public static final int COLOR_SYS_NTSC = 0;
	/** TV color system PAL. */
	public static final int COLOR_SYS_PAL = 1;
	/** TV color system SECAM. */
	public static final int COLOR_SYS_SECAM = 2;
	/** TV color system NTSC_443. */
	public static final int COLOR_SYS_NTSC_443 = 3;
	/** TV color system PAL_M. */
	public static final int COLOR_SYS_PAL_M = 4;
	/** TV color system PAL_N. */
	public static final int COLOR_SYS_PAL_N = 5;
	/** TV color system PAL_60. */
	public static final int COLOR_SYS_PAL_60 = 6;

	public static final int AUDIO_SYS_UNKNOWN = 0;
	public static final int AUDIO_SYS_AM = 1 << 0;
	public static final int AUDIO_SYS_FM_MONO = 1 << 1;
	public static final int AUDIO_SYS_FM_EIA_J = 1 << 2;
	public static final int AUDIO_SYS_FM_A2 = 1 << 3;
	public static final int AUDIO_SYS_FM_A2_DK1 = 1 << 4;
	public static final int AUDIO_SYS_FM_A2_DK2 = 1 << 5;
	public static final int AUDIO_SYS_FM_RADIO = 1 << 6;
	public static final int AUDIO_SYS_NICAM = 1 << 7;
	public static final int AUDIO_SYS_BTSC = 1 << 8;

	/* <svl record bit mask */
	public static int SB_VNET_ALL = (0x00000001);
	/* <svl record bit mask */
	public static int SB_VNET_ACTIVE = (1 << 1);
	/* <svl record bit mask */
	public static int SB_VNET_EPG = (1 << 2);
	/* <svl record bit mask */
	public static int SB_VNET_VISIBLE = (1 << 3);
	/* <svl record bit mask */
	public static int SB_VNET_FAVORITE1 = (1 << 4);
	/* <svl record bit mask */
	public static int SB_VNET_FAVORITE2 = (1 << 5);
	/* <svl record bit mask */
	public static int SB_VNET_FAVORITE3 = (1 << 6);
	/* <svl record bit mask */
	public static int SB_VNET_FAVORITE4 = (1 << 7);
	/* <svl record bit mask */
	public static int SB_VNET_BLOCKED = (1 << 8);
	/* <svl record bit mask */
	public static int SB_VNET_OOB_LIST = (1 << 9);
	/* <svl record bit mask */
	public static int SB_VNET_INB_LIST = (1 << 10);
	/* <svl record bit mask */
	public static int SB_VNET_SCRAMBLED = (1 << 11);
	/* <svl record bit mask */
	public static int SB_VNET_BACKUP1 = (1 << 12);
	/* <svl record bit mask */
	public static int SB_VNET_BACKUP2 = (1 << 13);
	/* <svl record bit mask */
	public static int SB_VNET_BACKUP3 = (1 << 14);
	/* <svl record bit mask */
	public static int SB_VNET_FAKE = (1 << 15);
	/* <svl record bit mask */
	public static int SB_VNET_USER_TMP_UNLOCK = (1 << 16);
	/* <svl record bit mask */
	public static int SB_VNET_CH_NAME_EDITED = (1 << 17);
	/* <svl record bit mask */
	public static int SB_VNET_LCN_APPLIED = (1 << 18);
	/* <svl record bit mask */
	public static int SB_VNET_USE_DECODER = (1 << 19);
	/* <deprecated, please use SB_VOPT_CH_NUM /NAME_EDITED */
	public static int SB_VNET_ACTIVE_EPG_EDITED = (1 << 20);
	/* <svl record bit mask */
	public static int SB_VNET_FREQ_EDITED = (1 << 21);
	/* <svl record bit mask */
	public static int SB_VNET_REMOVAL = (1 << 22);
	/* < svl record bit mask */
	public static int SB_VNET_REMOVAL_TO_CONFIRM = (1 << 23);

	/* Redefine for DVB */
	public static int SB_VNET_RADIO_SERVICE = (1 << 10);
	public static int SB_VNET_ANALOG_SERVICE = (1 << 12);
	public static int SB_VNET_TV_SERVICE = (1 << 13);
	public static int SB_VNET_USE_DECODER_2 = (1 << 14);
	
	
	public static int SB_RECORD_NOT_SAVE_CH_NUM = (1 << 1);
	public static int SB_VOPT_NOT_SAVE_CH_NUM = (1 << 1);
	public static int SB_VOPT_USER_TMP_UNLOCK = (1 << 2);
	public static int SB_VOPT_CH_NAME_EDITED = (1 << 3);
	public static int SB_VOPT_FREQ_EDITED = (1 << 4);
	public static int SB_VOPT_LCN_APPLIED = (1 << 5);
	public static int SB_VOPT_MANUAL_OBTAINED = (1 << 6);
	public static int SB_VOPT_HD_SIMULCAST = (1 << 7);
	public static int SB_VOPT_SDT_AVAILABLE = (1 << 9);
	public static int SB_VOPT_CH_NUM_EDITED = (1 << 10);
	public static int SB_VOPT_PORTUGAL_HD_SIMULCAST = (1 << 11);
	public static int SB_VOPT_CURRENT_COUNTRY = (1 << 12);
	public static int SB_VOPT_DELETED_BY_USER = (1 << 13);
	public static int SB_VOPT_NVOD_REF = (1 << 14);
	public static int SB_VOPT_NVOD_TS = (1 << 15);
	public static int SB_VOPT_SVC_REMOVE_SIMULICAST = (1 << 16);


	private static Map<Integer, String> tvSystemNameMap = new HashMap<Integer, String>();
	private static Map<Integer, String> colorSystemNameMap = new HashMap<Integer, String>();
	private static Map<Integer, String> audioSystemNameMap = new HashMap<Integer, String>();

	static {
		/* Init TV system resource start */
		tvSystemNameMap.put(TV_SYS_UNKNOWN, "Unknow");
		tvSystemNameMap.put(TV_SYS_A, "A");
		tvSystemNameMap.put(TV_SYS_B, "B");
		tvSystemNameMap.put(TV_SYS_C, "C");
		tvSystemNameMap.put(TV_SYS_D, "D");
		tvSystemNameMap.put(TV_SYS_E, "E");
		tvSystemNameMap.put(TV_SYS_F, "F");
		tvSystemNameMap.put(TV_SYS_G, "G");
		tvSystemNameMap.put(TV_SYS_H, "H");
		tvSystemNameMap.put(TV_SYS_I, "I");
		tvSystemNameMap.put(TV_SYS_J, "J");
		tvSystemNameMap.put(TV_SYS_K, "K");
		tvSystemNameMap.put(TV_SYS_K, "K");
		tvSystemNameMap.put(TV_SYS_K_PRIME, "K'");
		tvSystemNameMap.put(TV_SYS_L, "L");
		tvSystemNameMap.put(TV_SYS_L_PRIME, "L'");
		tvSystemNameMap.put(TV_SYS_M, "M");
		tvSystemNameMap.put(TV_SYS_N, "N");
		tvSystemNameMap.put(TV_SYS_AUTO, "AUTO");
		/* Init TV system resource end */

		/* Init Color system resource start */
		colorSystemNameMap.put(COLOR_SYS_UNKNOWN, "Unknow");
		colorSystemNameMap.put(COLOR_SYS_NTSC, "NTSC");
		colorSystemNameMap.put(COLOR_SYS_PAL, "PAL");
		colorSystemNameMap.put(COLOR_SYS_SECAM, "SECAM");
		colorSystemNameMap.put(COLOR_SYS_NTSC_443, "NTSC_443");
		colorSystemNameMap.put(COLOR_SYS_PAL_M, "PAL M");
		colorSystemNameMap.put(COLOR_SYS_PAL_N, "PAL N");
		/* Init Color system resource end */

		/* Init Sound system resource start */
		audioSystemNameMap.put(AUDIO_SYS_UNKNOWN, "Unknow");
		audioSystemNameMap.put(AUDIO_SYS_AM, "AM");
		audioSystemNameMap.put(AUDIO_SYS_FM_MONO, "FM MONO");
		audioSystemNameMap.put(AUDIO_SYS_FM_EIA_J, "FM EIA J");
		audioSystemNameMap.put(AUDIO_SYS_FM_A2, "FM A2");
		audioSystemNameMap.put(AUDIO_SYS_FM_A2_DK1, "FM A2 DK1");
		audioSystemNameMap.put(AUDIO_SYS_FM_A2_DK2, "FM A2 DK2");
		audioSystemNameMap.put(AUDIO_SYS_FM_RADIO, "FM RADIO");
		audioSystemNameMap.put(AUDIO_SYS_NICAM, "NICAM");
		audioSystemNameMap.put(AUDIO_SYS_BTSC, "BTSC");
		/* Init Sound system resource end */
	}

	public static int SVL_SERVICE_TYPE_TV = (1); /* < Television service type. */
	public static int SVL_SERVICE_TYPE_RADIO = (2); /* < Radio service type. */
	public static int SVL_SERVICE_TYPE_APP = (3); /* < Application service type. */

	/**
	 * @param tvSystem
	 * @return the name string of tv system
	 */
	public static String getTvSystemName(int tvSystem) {
		return tvSystemNameMap.get(tvSystem);
	}

	/**
	 * @param colorSystem
	 * @return the name string of color system
	 */
	public static String getColorSystemName(int colorSystem) {
		return colorSystemNameMap.get(colorSystem);
	}

	/**
	 * @param audioSystem
	 * @return the name string of audio system
	 */
	public static String getAudioSystemName(int audioSystem) {
		return audioSystemNameMap.get(audioSystem);
	}

	public static String DB_ANALOG = "DB_ANALOG";
	public static String DB_ANALOG_TEMP = "DB_ANALOG_TEMP";

	/** AIR database name */
	public static String DB_AIR = "DB_AIR";
	/** DVB database name */
	public static String DB_CABEL = "DB_CABEL";

	/** Analog temp database name */
	public static String DB_AIR_TEMP = "DB_AIR_TEMP";

	public static Map<String, Integer> DBSvlMap = new HashMap<String, Integer>();

	// sync with channel_service_wrapper.c channel_db[]
	static {
		DBSvlMap.put(DB_ANALOG, 2);
		DBSvlMap.put(DB_ANALOG_TEMP, 801);
		DBSvlMap.put(DB_AIR, 1);
		DBSvlMap.put(DB_CABEL, 2);
		DBSvlMap.put(DB_AIR_TEMP, 801);
	}

	public static int getSvlIdByName(String name) {
		if (!DBSvlMap.containsKey(name)) {
			Logger.e(TAG, "Can not find svlid by name " + name);
			return -1;
		} else {
			return DBSvlMap.get(name);
		}
	}

	public static void main(String[] args) {
		System.out.println(getTvSystemName(TV_SYS_B) + "|" + getTvSystemName(TV_SYS_G));
	}

}
