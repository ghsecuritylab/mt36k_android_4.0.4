package com.mediatek.mmpcm.audioimpl;

public class AudioConst {
    /* Play Status */    
    public static final int PLAY_STATUS_UNKNOW = 0;
    public static final int PLAY_STATUS_PREPARING = 1;
    public static final int PLAY_STATUS_PREPARED = 2;
    public static final int PLAY_STATUS_STARTED = 3;
    public static final int PLAY_STATUS_PAUSED = 4;
    public static final int PLAY_STATUS_FF = 5;
    public static final int PLAY_STATUS_FR = 6;
    public static final int PLAY_STATUS_SF = 7;
    public static final int PLAY_STATUS_SR = 8;
    public static final int PLAY_STATUS_STOPPED = 9;
    public static final int PLAY_STATUS_COMPLETED = 10;
    public static final int PLAY_STATUS_END = PLAY_STATUS_UNKNOW;
    
    
    /* Message Type */
    public static final int MSG_SOURCE_PLAYNEXT = 4;
    public static final int MSG_SOURCE_COMPLETE = 3;
    public static final int MSG_PLAY_START = 2;
    public static final int MSG_SOURCE_PREPARE = 1;
    public static final int MSG_ERROR_STOP = -1;
    public static final int MSG_SOURCE_NOT_PREPARED = -2;
    public static final int MSG_SET_SOURCE_FAIL = -3;
    public static final int MSG_IS_PLAYING = -4;
    public static final int MSG_SET_DATA_SOURCE_FAIL = -5;
    public static final int MSG_SEEK_NOT_SUPPORT = -6;
    public static final int MSG_PLAY_START_FAIL = -7;
    public static final int MSG_STEP_NOT_SUPPORT = -8;
    public static final int MSG_NOT_SUPPORT = -9;
    public static final int MSG_AUDIO_NOT_SUPPORT = -10;
    public static final int MSG_VIDEO_NOT_SUPPORT = -11;
    public static final int MSG_FILE_NOT_SUPPORT = -12;
    public static final int MSG_INVALID_STATE = -13;
    public static final int MSG_INPUT_STREAM_FAIL = -14;
    public static final int MSG_AV_NOT_SUPPORT = -15;
    public static final int MSG_FILE_CORRUPT = -16;
    public static final int MSG_POSITION_UPDATE = -17;
   
    
    /* Player Mode */ 
    public static final int PLAYER_MODE_LOCAL = 0;
    public static final int PLAYER_MODE_SAMBA = 1;    
    public static final int PLAYER_MODE_DLNA = 2; 
    public static final int PLAYER_MODE_HTTP = 3;
	/* Error msg */
	public static final String MSG_ERR_CANNOTPAUSE = "CANNOTPAUSE";
	public static final String MSG_ERR_PAUSEEXCEPTION = "PAUSEEXCEPTION";
	public static final String NOT_SUPPORT = "not support!";
	
}
