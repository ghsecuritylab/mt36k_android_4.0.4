package com.tcl.fac.data.addon;
//import com.tcl.tv.R;
import android.content.res.Resources;

public class Core {
	public static  boolean dataComing=false;
	public static  boolean bDeviceIDOK=false;
	public static short[] receiveData;
	public static short[] tmpreceiveData={0};
	public final static short ANIMATION_DEFAULT=0;
	public final static short STATUS_OK=1;
	public final static short STATUS_ERROR=0;
	public static boolean warmupStatus = false;
	
	public final static  short  MAC_DEVICE_HDCP_OK= 0x1f;;
	public final static char FACTORY_HDCP_KEY_LENGTH=320;
	public static int pModeStatus = 0;
	public static int dModeStatus = 0;
	public static int BootLogoStatus = 0;
	public static int currentTmpType = 0;
	public static String currentSoureceType = "";
	public static boolean uartSwicher=false;//default is false
	public static boolean changeProjectId=false;//default is false
	
	public final static int HDCP_LENGTH= 330;//order + data + crc=327
	
	public final static int TMP_NORMAL= 1;
	public final static int TMP_COOL= 2;
	public final static int TMP_WARM= 3;
	public final static int SOURCE_TV= 0;
	public final static int SOURCE_AV = 1;
	public final static int SOURCE_COMP = 2;
	public final static int SOURCE_VGA = 3;
	public final static int SOURCE_HDMI = 4;
	public static short keyCodeRecord = 0;
	public static short[]  burningTime ={0,0} ;
//	public static  ProjectInfo projectInfo=new ProjectInfo();  
//	private static Resources mRes;
	
	
	public final static int ATV_START_INDEX= 899;
	
	public final static int EXIT_APP = 3;
	public final static int D_MODE= 1;
	public final static int P_MODE= 2;
	public final static int W_MODE = 3;
	public final static int S_MODE = 4;
	public final static int   OSD_SHOW=1;  
	public final static int   OSD_HIDE=0;  
	public final static int   COMMON_ON=1;  
	public final static int   COMMON_OFF=0;  
	public static int osd_flage =COMMON_OFF;
	/* pic mode */
	public final static int PM_NORMAL = 1;
	public final static int PM_VIVID = 3;
	public final static int PM_MILD = 2;
	public final static int PM_USER = 4;
//	public final static int PM_DYNAMIC = 5;
	/* pic mode */
	/* sound mode */
	public final static int SM_NORMAL = 1;
	public final static int SM_MOVIE = 2;
	public final static int SM_MUSIC = 3;
	public final static int SM_NEWS = 4;
	
	
//	public  static boolean TEST_3D_HAS_SOURCE = false;
//	public  static boolean TEST_3D_NO_SOURCE = false;
	
	
	
	
// settings for hotel menu
//	public static boolean Hotel_Flag = false;
	public static int Hotel_MaxVolume = 100;
	public static int presetChannel = 1;
	public static int keyLockStatus = 0;
//	public static int channelLockStatus = 0;
	public final static int EEPROM_ADDR_Hotel_Flag= 0xba0;
	public final static int EEPROM_ADDR_Hotel_BootLogo =0xba1;
	public final static int EEPROM_ADDR_Hotel_AutoSet =0xba2;
	public final static int EEPROM_ADDR_Hotel_ChannelLock =0xba3;
	public final static int EEPROM_ADDR_Hotel_KeyLock = 0xba4;
	public final static int EEPROM_ADDR_Hotel_PresetVolume =0xba5;
	public final static int EEPROM_ADDR_Hotel_PresetInput =0xba6;
	public final static int EEPROM_ADDR_Hotel_PresetChannel =0xba7;
	public final static int EEPROM_ADDR_Hotel_MaxVolume = 0xba8;
	public final static int EEPROM_ADDR_Hotel_PictureMode = 0xba9;	
	public final static int EEPROM_ADDR_Hotel_AudioMode = 0xbaa;
	public final static int EEPROM_ADDR_NoSignal_BuleScreen_Flag= 0xbab;
	public final static int EEPROM_ADDR_DTVChannelChange_BuleScreen_Flag= 0xbac;	
	
	
	public final static int EEPROM_ADDR_System_Update_Flag = 0xd46;
	
	
	
	
	
//	public final static int EEPROM_ADDR_MID_Verify_Flag = 0xbab;
	
	
	public static boolean KaraOK_ON = false;	
	
	
	/* sound */
	
	/* soundsence */
	public final static int VOL_HANG = 0;
	public final static int VOL_DEST = 1;	
	
	/*sound format*/
	public final static int PATT_DK= 0;
	public final static int PATT_BG = 1;
	public final static int PATT_I = 2;
	public final static int PATT_M = 3;
	
	public static int POWER_MODE_ON= 0;
	public static int POWER_MODE_STANDY= 1;
	public static int POWER_MODE_LAST= 2;
	
	/*usb clone status*/
	public final static int NVM_TO_USB_FAIL= 0;
	public final static int NVM_TO_USB_OK = 1;
	public final static int USB_TO_NVM_FAIL = 2;
	public final static int USB_TO_NVM_OK= 3;
	public final static int USB_TO_NVM = 1;
	public final static int NVM_TO_USB = 0;
	
	public static int clone_result= 0;
	public static int indexProjectID = 0;
	public static String swVer="";
	
	/*source num*/
	public final static int HDMI_NUM=2;
	public final static int AV_NUM=2;
	public final static int VGA_NUM=1;
	public final static int YPBPR_NUM=1;
	public final static int TV_NUM=1;
	/*nonstandard*///NON_STANDARD_ON
	public final static int NON_STANDARD_EEP_ADDR=0xD40;
	public final static int AREA_EEP_ADDR=0xD42;
	public final static int CITY_AREA_EEP_ADDR=0xD48;
	
	//MAC DEVICED HDCP if add 2012 1 14
	public final static int MAC_FLAGE=0xD30;
	public final static int DEVICED_FLAGE=0xD31;
	public final static int HDCP_FLAGE=0xD32;
	
	public final static int NON_STANDARD_ON=0x55;
	public final static int NON_STANDARD_OFF=0xFF;
	public final static  short  macDevicedHdcpOk= 0x1f;
	public static short  flageSourceDisplay =0;
	public static final int  DESKTOP_HEIGHT = 1080;
	public static final int  DESKTOP_WIDTH = 1920;
	
	public static long CRC_TABLE[]=
	{                
		0x0000,0x1021,0x2042,0x3063,0x4084,0x50A5,0x60C6,0x70E7,
		0x8108,0x9129,0xA14A,0xB16B,0xC18C,0xD1AD,0xE1CE,0xF1EF
	};

	public	static short ComnodOK[]=
	{                
		0xAB,0x05,0x0A,0xDF,0x4E
	};
	public	static short ComnodOrDateWrong[]=
	{                
		0xAB,0x05,0x0E,0x9F,0xCA
	};
	public	static short CRCWrong[]=
	{                
		0xAB,0x05,0x0F,0x8F,0xEB
	};

	public static  String main_menu[][]={
		{"Design mode hotkey",""},
		{"Factory menu",""},
		{"Other",""},
		{"Server menu",""},
		{"Param setting",""},
		{"Hotel menu",""}
	};
/* cyd	
	public static int P_mode_menu[][]={
			
		{R.string.factory_key,R.string.null_},
		{R.string.burning,R.string.off},
		{R.string.devicetest,R.string.right_lable},
		{R.string.wba,R.string.null_},
		{R.string.shop,R.string.right_lable},
		{R.string.nvm,R.string.right_lable},
		{R.string.powermode,R.string.null_},
		{R.string.usbcolone,R.string.null_},
		{R.string.presetchannel,R.string.null_},
		{R.string.other,R.string.null_},
	};
****************/
	public static  String paramsetting_menu[][]={
		{"0-SND Curve",""},
		{"1-Picture Setting",""},
		{"2-SSC Adjust",""},
		{"3-Sound Setting",""},
		{"4-DBC",""},
		{"5-CI Card",""}
//		{"6-WIFI CHEAK",""},
//		{"7-USB FILE",""}
	};
	public static  String picsetting_menu[][]={
		{"Picture mode",""},
		{"Brightness",""},
		{"Contrast",""},
		{"ColorTmp",""},
		{"Color",""},
		{"Sharpness",""},
		{"Backlight",""},
	};
	public static  String Area[]={
		"福建康宁","福建康宁1","福建康宁2","福建康宁3"
	};
	public static  int Area_FREQUENCE[]={
		339000000,339000000,339000000,339000000
	};
	public static  String white_balance[][]={
		{"Source",""},
		{"Color",""},
		{"R gain",""},
		{"G gain",""},
		{"B gain",""},
		{"R offset",""},
		{"G offset",""},
		{"B offset",""},
		{"White Balance init",">"},
		{"Pic.Enhance",""},
		{"Picture related",""}
	};

	public static  String pic_related[][]={
		{"Flesh",""},
		{"Adaptive luma control",""},
		{"Back Light",""},	
	};
	public static  String pic_mode[]={
		"Vivid","Normal","Mild","User"
	};
	public static  String usb_clone_menu[][]={
		{"USB Colne Mode","All"},
		{"TV TO USB",">"},
		{"USB TO TV",">"}
	};
	public static  String channel_preset_menu[][]={
		{"Factory Area",""},
		{"Channel Preset",">"},
	};
	public static  String other_item[][]={
		{"Project Info",""},
		{"NVM 种子标志","OFF"},
		{"DBC Prama",""},
		{"Flip-Mirror","OFF"},
		{"upgrade pkg file","-->"},
		//{"NoSignal_BuleScreen",""},
		//{"DTVChannelChange",""},
	};

	public static  String main_other[][]={
		{"IIC Tool",""},
		{"Test Host",""},
		{"Disable Host",""},
		{"Disable Panel KeV",""}
	};
	public static  String server_menu[][]={
		{"Project ID",""},
		{"Project SN",""},
		{"EPolisy Number",""},
		{"Nonstandard",""},
		{"MainFreAtea",""},
		{"DownUpgradeFile",""},
		{"城市运营商",""},
		{"下载升级文件","->"},
		{"地区城市",""},
		{"Hotel Menu",""},
		{"NoSignal-BlueScreen", ""},
		{"DTVChannelChange-BlueScreen", ""},
		
	};
	public   String tserver_menu[][]={
		{"Project ID",""},
		{"Project SN",""},
		{"Nonstandard",""},
		{"MainFreAtea",""},
		{"DownUpgradeFile",""},
		{"AreaAndCity",""},
	//	{mRes.getString(R.string.MainFreAtea),""},			
	//	{mRes.getString(R.string.DownUpgradeFile),"->"},
	//	{mRes.getString(R.string.AreaAndCity) ,""}
	};
	
	public static	int FreQuenceArray[]=
	{0,339,474};
	
	public static  String FreQuenceArea[]={
		"默认",
		"福建宁德",
		"晋江地区"
	};
	
	public static  String dtv_param[][]={
		{"Tcl_NetWork swither",""},
		{"Tcl_NetWork ID",""},
		{"Tcl Network name",""},		
	};
	public static  String project_info_menu[][]={
		{"Project ID",""},
		{"Name",""},
		{"Pannel",""},
		{"RCU",""},
		{"Region",""}
	};
	public static  String serv_menu[]={
		"Project ID","RC dongle update","Get RC Version"
	};
	public static  String info_lable_list[]={
		"SW NO :","Project Name","Panel Name","SIAP Version","Date:"
	};
	public static  String info_list_value[]={
		"","","","","","",""
	};
	public static  String setting_snd_menu[][]={
		{"Curve_0",""},
		{"Curve_20",""},
		{"Curve_50",""},
		{"Curve_80",""},
		{"Curve_100",""},	
	};
	public static  String setting_sound_menu[][]={
		{"Sound mode",""},
		{"Balance",""},
		{"Auto audio",""},
		{"Sound Scene",""},
		{"Sys_audio",""},	
	};
	public static  String setting_dbc_menu[][]={
		{"BP",""},
		{"BK_E",""},
		{"APL_2",""},
		{"Standard Backlight",""},
		{"Mode",""},
		{"DBC Enable",""},
		{"Print Enable",""},
		{"Reset",">"},
	};
	public static String panel_rotate_str[]={
		"OFF", "ON",
	};
/*** cyd
	public static int colorIDIndexTable[]={
		R.drawable.hd_15circle,
		R.color.white0_8_color,
		R.color.white0_2_color,
		R.color.black_color,
		R.color.red_color,
		R.color.green_color,
		R.color.blue_color,
		R.drawable.trianglegrey,
		R.drawable.picpalette
	};
	
*/
	
	
	public static  String midInfoTable[]={
		"MAC",
		"DID",
		"HDCP",
		"ULPK",
		"ESN",
		"NETFLIX",
		"CI+",
		"RID",
		"SN",
		"WIDI"	
	};
	
}
