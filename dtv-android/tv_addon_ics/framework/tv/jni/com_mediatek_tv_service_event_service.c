#include "tv_jni_util.h"
#include "com_mediatek_tv_service_TVNative.h"
#include "channel/channel_service_wrapper.h"
#include "event/event_service_wrapper.h"
#include "util/a_common.h"
EXTERN_C_START

#ifdef LOG_TAG
#undef LOG_TAG
#define LOG_TAG "Event_service_jni"
#endif

static VOID _edb_nfy_fct(
        HANDLE_T                h_edb,
        EDB_NFY_REASON_T        e_reason,
        VOID*                   pv_nfy_tag,
        UINT32                  ui4_data1,
        UINT32                  ui4_data2);

jobject  edb_convert_pool_event(
        JNIEnv                      *env,
        EDB_POOL_EVENT_INFO_T*      pt_event_info,
        UINT16                      ui2_svl_id,
        UINT32                      ui4_channel_id);

INT32 edb_unserialize_pool_event(
        EDB_POOL_EVENT_INFO_T**   ppt_pool_event , 
        UINT8* pui1_serialized_event,
        UINT32* pui4_pool_event_len);

VOID edb_dvb_free_single_event_obj( VOID*       pt_event);


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    eventSetCommand
 * Signature: (Lcom/mediatek/tv/model/EventCommand;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_eventSetCommand
  (JNIEnv *env, jclass clazz, jobject eventCommand)
{
    INT32                   i4_ret                    = EDBR_OK;
    EDB_COMMAND_T           at_cmd[25];
    UINT16                  ui2_cmd_idx               = 0;
    INT32                   i4_command_mask           = 0;
    BOOL                    b_actual_only             = FALSE;
    jclass                  jclass_AnalogChannelInfo  = NULL;
    jclass                  jclass_DvbChannelInfo     = NULL;
    EDB_CACHE_ACTIVE_WIN_T  t_active_win;
    /*Check para*/
    if (env==NULL || clazz == NULL || eventCommand == NULL)
    {
        JNI_LOGE(("Invalid arguments\r\n"));
        return -1;
    }

    JNI_MEMSET(&at_cmd,0x0,sizeof(at_cmd));

    jclass_AnalogChannelInfo = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/AnalogChannelInfo");
    jclass_DvbChannelInfo    = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/DvbChannelInfo");

    /*Get command mask from java object EventCommand*/
    i4_command_mask = (INT32) JCOM(env,eventCommand,Int,    CMI(EventCommand_def,EventCommand_getCommandMask));
    JNI_LOGI(("Event command mask=0x%X\r\n",i4_command_mask));

    /*   
         EVENT_CMD_CFG_ACTUAL_ONLY                
         EVENT_CMD_CFG_MAX_DAYS                   
         EVENT_CMD_CFG_PREF_LANG                  
         EVENT_CMD_CFG_COUNTRY_CODE               
         EVENT_CMD_CFG_ACTIVE_WIN                 
         EVENT_CMD_CFG_EVENT_MIN_SECS             
         EVENT_CMD_CFG_FAKE_EVENT_INSERTION_ENABLE
         EVENT_CMD_CFG_FAKE_EVENT_MIN_SECS        
         EVENT_CMD_CFG_TIME_CONFLICT_ALLOW        
         EVENT_CMD_CFG_TIME_PARTIAL_OVERLAP_ALLOW 
         EVENT_CMD_CFG_EVENT_DETAIL_SEPARATOR    
         EVENT_CMD_CFG_DVBC_OPERATOR             //TODO for CN not support
         EVENT_CMD_DO_CURRENT_SERVICE             
         EVENT_CMD_DO_RESTART                     
         EVENT_CMD_DO_CLEAN                       
         EVENT_CMD_DO_ENABLE                      
         EVENT_CMD_DO_TUNER_CHANGE               //TODO for CN not support 
         */
    if (i4_command_mask & EVENT_CMD_CFG_ACTUAL_ONLY)
    {
        b_actual_only = (BOOL) JCOM(env,eventCommand,Boolean,CMI(EventCommand_def,EventCommand_isActualOnly));
        JNI_LOGD(("EVENT_CMD_CFG_ACTUAL_ONLY[%d]=%d\r\n",ui2_cmd_idx,b_actual_only));
        at_cmd[ui2_cmd_idx].e_code      = EDB_CMD_CFG_ACTUAL_ONLY;
        at_cmd[ui2_cmd_idx].u.b_bool    = b_actual_only;
        ui2_cmd_idx++;
    }
    if(i4_command_mask & EVENT_CMD_CFG_MAX_DAYS)
    {
        UINT8  ui1_max_days = 0;
        ui1_max_days =  JCOM(env,eventCommand,Int, CMI(EventCommand_def,EventCommand_getMaxDay));
        JNI_LOGD(("EVENT_CMD_CFG_MAX_DAYS[%d]=%d\r\n",ui2_cmd_idx,ui1_max_days));
        at_cmd[ui2_cmd_idx].e_code       = EDB_CMD_CFG_MAX_DAYS;
        at_cmd[ui2_cmd_idx].u.ui2_number = ui1_max_days;
        ui2_cmd_idx++;
    }
    if(i4_command_mask & EVENT_CMD_CFG_PREF_LANG)
    {
        INT32                i               = 0;
        jobject              jo_prefLanuages = NULL;
        EDB_PREF_LANG_T      t_pref_lang     ;//= {0};
        jobject              jo_prefLanguage = NULL;

        jo_prefLanuages = (jobject) JCOM(env,eventCommand,Object, CMI(EventCommand_def,EventCommand_getPrefLanuage));
        if (jo_prefLanuages != NULL)
        {
            JNI_LOGD(("EVENT_CMD_CFG_PREF_LANG[%d]=0x%x\r\n",ui2_cmd_idx,jo_prefLanuages));
            JNI_MEMSET(&t_pref_lang,0x0,sizeof(EDB_PREF_LANG_T));
            t_pref_lang.ui1_num = EDB_MAX_NUM_PREF_LANG;
            for (i=0; i<t_pref_lang.ui1_num; i++)
            {
                jo_prefLanguage = JNI_GET_ARRAY_ELEMENT(env,jo_prefLanuages,i);
                if (jo_prefLanguage != NULL)
                {
                    jstring2buffer(env,jo_prefLanguage, (CHAR*) (&(t_pref_lang.t_lang[i])),sizeof(ISO_639_LANG_T) );
                    JNI_LOGD(("EVENT_CMD_CFG_PREF_LANG[%d][%d]=%s\r\n",ui2_cmd_idx,i,
                              (CHAR*) (&(t_pref_lang.t_lang[i]))
                              ));
                    JNI_DEL_LOCAL_REF(env,jo_prefLanguage);
                }
            }

            JNI_DEL_LOCAL_REF(env,jo_prefLanuages);
            at_cmd[ui2_cmd_idx].e_code         = EDB_CMD_CFG_PREF_LANG;
            at_cmd[ui2_cmd_idx].u.pt_pref_lang = &t_pref_lang;
            ui2_cmd_idx++;
        }
    }
    if(i4_command_mask & EVENT_CMD_CFG_COUNTRY_CODE)
    {
        JNI_LOGD(("EVENT_CMD_CFG_COUNTRY_CODE[%d] Not implement\r\n",ui2_cmd_idx));
    }
    if(i4_command_mask & EVENT_CMD_CFG_ACTIVE_WIN)
    {
        jobject              jo_ActiveWindow = NULL;
        jobject              jo_channels     = NULL;
        jobject              jo_channel      = NULL;
        TIME_T               t_start_time    = NULL_TIME;
        TIME_T               t_duration      = NULL_TIME;
        INT32                i4_channels_len = 0;
        INT32                i               = 0;
        BRDCST_TYPE_T        e_brdcst_type;

        jo_ActiveWindow = (jobject) JCOM(env,eventCommand,Object, CMI(EventCommand_def,EventCommand_getActiveWindow));
        if (jo_ActiveWindow != NULL)
        {
            JNI_MEMSET (&t_active_win, 0, sizeof(t_active_win));
            JNI_LOGD(("EVENT_CMD_CFG_ACTIVE_WIN[%d]\r\n",ui2_cmd_idx));
            t_start_time = (TIME_T)  JCOM(env,jo_ActiveWindow,Long, CMI(EventActiveWindow_def,EventActiveWindow_getStartTime));
            t_duration   = (TIME_T)  JCOM(env,jo_ActiveWindow,Long, CMI(EventActiveWindow_def,EventActiveWindow_getDuration));

            /*Get active window channels ,jo_channels is ChannelInfo[]*/
            jo_channels  = (jobject) JCOM(env,jo_ActiveWindow,Object, CMI(EventActiveWindow_def,EventActiveWindow_getChannels));
            if (jo_channels != NULL)
            {
                i4_channels_len = (INT32)JNI_GET_ARRAY_LEN(env,jo_channels);
                t_active_win.ui1_num_channel = (UINT8)i4_channels_len;
                JNI_LOGD(("EVENT_CMD_CFG_ACTIVE_WIN[%d] channel_len=%d start=%ld end=%ld\r\n",ui2_cmd_idx,
                          i4_channels_len,t_start_time,t_duration));
                /*Get each channel from channels*/
                for(i=0; i<i4_channels_len; i++)
                {
                    jo_channel = (jobject) JNI_GET_ARRAY_ELEMENT(env,jo_channels,i);
                    if (jo_channel == NULL)
                    {
                        continue;
                    }

                    if ( JNI_IS_INSTANCE_OF(env,jo_channel,jclass_AnalogChannelInfo) )
                    {
                        e_brdcst_type = BRDCST_TYPE_ANALOG;
                    }
                    else if ( JNI_IS_INSTANCE_OF(env,jo_channel,jclass_DvbChannelInfo) )
                    {
                        e_brdcst_type = BRDCST_TYPE_DVB;
                    }
                    t_active_win.at_channel_id[i].e_brdcst_type  = e_brdcst_type;
                    t_active_win.at_channel_id[i].ui2_svl_id     = (UINT16)JCOM(env,jo_channel,Int, CMI(channel_info_def,channel_info_getSvlId));
                    t_active_win.at_channel_id[i].ui4_channel_id = (UINT32)JCOM(env,jo_channel,Int, CMI(channel_info_def,channel_info_getChannelId));
                    JNI_DEL_LOCAL_REF(env,jo_channel);
                    JNI_LOGD(("EVENT_CMD_CFG_ACTIVE_WIN[%d] no=%d brdcst_type=%d svl_id=%d channel_id=%d\r\n",ui2_cmd_idx,i,
                              t_active_win.at_channel_id[i].e_brdcst_type,
                              t_active_win.at_channel_id[i].ui2_svl_id,
                              t_active_win.at_channel_id[i].ui4_channel_id
                              ));
                }
                if (i4_channels_len > 0 )
                {
                    t_active_win.t_start_time = t_start_time;
                    t_active_win.t_duration   = t_duration;
                    at_cmd[ui2_cmd_idx].e_code = EDB_CMD_CFG_ACTIVE_WIN;
                    at_cmd[ui2_cmd_idx].u.pt_active_win = &t_active_win;
                    ui2_cmd_idx++;
                }

                JNI_DEL_LOCAL_REF(env,jo_channels);
            }
            JNI_DEL_LOCAL_REF(env,jo_ActiveWindow);
        }
    }
    if(i4_command_mask & EVENT_CMD_CFG_EVENT_MIN_SECS)
    {
        UINT16      ui2_min_sec = 0;
        ui2_min_sec =  (UINT16) JCOM(env,eventCommand,Int, CMI(EventCommand_def,EventCommand_getEventMinSeconds));
        JNI_LOGD(("EVENT_CMD_CFG_EVENT_MIN_SECS[%d]  ui2_min_sec=%d\r\n",ui2_cmd_idx,ui2_min_sec));
        at_cmd[ui2_cmd_idx].e_code       = EDB_CMD_CFG_EVENT_MIN_SECS;
        at_cmd[ui2_cmd_idx].u.ui2_number = ui2_min_sec;
        ui2_cmd_idx++;
    }
    if(i4_command_mask & EVENT_CMD_CFG_FAKE_EVENT_INSERTION_ENABLE)
    {
        BOOL    b_fake_event_insertion_able = FALSE; 
        b_fake_event_insertion_able =  (BOOL) JCOM(env,eventCommand,Boolean, CMI(EventCommand_def,EventCommand_isFakeEventInsertionEnable));
        JNI_LOGD(("EVENT_CMD_CFG_FAKE_EVENT_INSERTION_ENABLE[%d]  b_fake_event_insertion_able=%d\r\n",ui2_cmd_idx,b_fake_event_insertion_able));
        at_cmd[ui2_cmd_idx].e_code       = EDB_CMD_CFG_FAKE_EVENT_INSERTION_ENABLE;
        at_cmd[ui2_cmd_idx].u.b_bool = b_fake_event_insertion_able;
        ui2_cmd_idx++;
    }
    if(i4_command_mask & EVENT_CMD_CFG_FAKE_EVENT_MIN_SECS)
    {
        UINT16          ui2_fake_event_min_sec = 0;
        ui2_fake_event_min_sec =  (UINT16) JCOM(env,eventCommand,Int, CMI(EventCommand_def,EventCommand_getFakeEventMinSecond));
        JNI_LOGD(("EVENT_CMD_CFG_FAKE_EVENT_MIN_SECS[%d]  ui2_fake_event_min_sec=%d\r\n",ui2_cmd_idx,ui2_fake_event_min_sec));
        at_cmd[ui2_cmd_idx].e_code       = EDB_CMD_CFG_FAKE_EVENT_MIN_SECS;
        at_cmd[ui2_cmd_idx].u.ui2_number = ui2_fake_event_min_sec;
        ui2_cmd_idx++;
    }
    if(i4_command_mask & EVENT_CMD_CFG_TIME_CONFLICT_ALLOW)
    {
        BOOL          b_time_conflict_allow = FALSE;
        b_time_conflict_allow =  (BOOL) JCOM(env,eventCommand,Boolean, CMI(EventCommand_def,EventCommand_isTimeConfictAllow));
        JNI_LOGD(("EVENT_CMD_CFG_TIME_CONFLICT_ALLOW[%d]  b_time_conflict_allow=%d\r\n",ui2_cmd_idx,b_time_conflict_allow));
        at_cmd[ui2_cmd_idx].e_code       = EDB_CMD_CFG_TIME_CONFLICT_ALLOW;
        at_cmd[ui2_cmd_idx].u.b_bool     = b_time_conflict_allow;
        ui2_cmd_idx++;
    }
    if(i4_command_mask & EVENT_CMD_CFG_TIME_PARTIAL_OVERLAP_ALLOW)
    {
        BOOL          b_time_partial_overlap_allow = FALSE;
        b_time_partial_overlap_allow =  (BOOL) JCOM(env,eventCommand,Boolean, CMI(EventCommand_def,EventCommand_isPartialOverapAllow));
        JNI_LOGD(("EVENT_CMD_CFG_TIME_PARTIAL_OVERLAP_ALLOW[%d]  b_time_partial_overlap_allow=%d\r\n",ui2_cmd_idx,b_time_partial_overlap_allow));
        at_cmd[ui2_cmd_idx].e_code       = EDB_CMD_CFG_TIME_PARTIAL_OVERLAP_ALLOW;
        at_cmd[ui2_cmd_idx].u.b_bool     = b_time_partial_overlap_allow;
        ui2_cmd_idx++;
    }
    if(i4_command_mask & EVENT_CMD_CFG_EVENT_DETAIL_SEPARATOR)
    {
        jobject jo_separator = NULL;
        jo_separator = (jobject) JCOM(env,eventCommand,Object, CMI(EventCommand_def,EventCommand_getEventDetailSeparator));
        if (jo_separator != NULL)
        {
            JNI_LOGD(("EVENT_CMD_CFG_EVENT_DETAIL_SEPARATOR[%d]=0x%x\r\n",ui2_cmd_idx,jo_separator));
            at_cmd[ui2_cmd_idx].e_code       = EDB_CMD_CFG_EVENT_DETAIL_SEPARATOR;
            jstring2buffer(env,jo_separator, at_cmd[ui2_cmd_idx].u.s_separator,4 );
            JNI_DEL_LOCAL_REF(env,jo_separator);
            ui2_cmd_idx++;
        }
    }
    if(i4_command_mask & EVENT_CMD_DO_CURRENT_SERVICE)
    {
        jobject              jo_currentChannel = NULL;
        EDB_CH_ID_T          t_ch_id;
        BRDCST_TYPE_T        e_brdcst_type;

        JNI_MEMSET(&t_ch_id, 0, sizeof(t_ch_id));
        jo_currentChannel = JCOM(env,eventCommand,Object, CMI(EventCommand_def,EventCommand_getCurrentChannelInfo));
        JNI_LOGD(("EVENT_CMD_DO_CURRENT_SERVICE[%d]=%d\r\n",ui2_cmd_idx,jo_currentChannel));
        if (jo_currentChannel != NULL)
        {
            if ( JNI_IS_INSTANCE_OF(env,jo_currentChannel,jclass_AnalogChannelInfo) )
            {
                e_brdcst_type = BRDCST_TYPE_ANALOG;
            }
            else if ( JNI_IS_INSTANCE_OF(env,jo_currentChannel,jclass_DvbChannelInfo) )
            {
                e_brdcst_type = BRDCST_TYPE_DVB;
            }

            t_ch_id.e_brdcst_type  = e_brdcst_type;
            t_ch_id.ui2_svl_id     = (UINT16)JCOM(env,jo_currentChannel,Int, CMI(channel_info_def,channel_info_getSvlId));
            t_ch_id.ui4_channel_id = (UINT32)JCOM(env,jo_currentChannel,Int, CMI(channel_info_def,channel_info_getChannelId));

            JNI_LOGD(("EVENT_CMD_DO_CURRENT_SERVICE[%d] brdcst_type=%d svl_id=%d channel_id=%d\r\n",
                      ui2_cmd_idx,
                      t_ch_id.e_brdcst_type,
                      t_ch_id.ui2_svl_id,
                      t_ch_id.ui4_channel_id));

            at_cmd[ui2_cmd_idx].e_code       = EDB_CMD_DO_CURRENT_SERVICE;
            at_cmd[ui2_cmd_idx].u.pt_ch_id = &t_ch_id;
            ui2_cmd_idx++;
        }
    }
    if(i4_command_mask & EVENT_CMD_DO_RESTART)
    {
        BOOL          b_do_restart = FALSE;
        b_do_restart =  (BOOL) JCOM(env,eventCommand,Boolean, CMI(EventCommand_def,EventCommand_isDoRestart));
        JNI_LOGD(("EVENT_CMD_DO_RESTART[%d]  b_do_restart=%d\r\n",ui2_cmd_idx,b_do_restart));
        at_cmd[ui2_cmd_idx].e_code       = EDB_CMD_DO_RESTART;
        at_cmd[ui2_cmd_idx].u.b_bool     = b_do_restart;
        ui2_cmd_idx++;
    }
    if(i4_command_mask & EVENT_CMD_DO_CLEAN)
    {
        BOOL          b_do_clean = FALSE;
        b_do_clean =  (BOOL) JCOM(env,eventCommand,Boolean, CMI(EventCommand_def,EventCommand_isDoClean));
        JNI_LOGD(("EVENT_CMD_DO_CLEAN[%d]  b_do_clean=%d\r\n",ui2_cmd_idx,b_do_clean));
        at_cmd[ui2_cmd_idx].e_code       = EDB_CMD_DO_CLEAN;
        at_cmd[ui2_cmd_idx].u.b_bool     = b_do_clean;
        ui2_cmd_idx++;
    }
    if(i4_command_mask & EVENT_CMD_DO_ENABLE)
    {
        BOOL          b_do_enable = FALSE;
        b_do_enable =  (BOOL) JCOM(env,eventCommand,Boolean, CMI(EventCommand_def,EventCommand_isDoEnable));
        JNI_LOGD(("EVENT_CMD_DO_ENABLE[%d]  b_do_enable=%d\r\n",ui2_cmd_idx,b_do_enable));
        at_cmd[ui2_cmd_idx].e_code       = EDB_CMD_DO_ENABLE;
        at_cmd[ui2_cmd_idx].u.b_bool     = b_do_enable;
        ui2_cmd_idx++;
    }


    at_cmd[ui2_cmd_idx].e_code = EDB_CMD_END;
    ui2_cmd_idx++;

    i4_ret = a_edb_set_command(&(at_cmd[0]),ui2_cmd_idx);

    return i4_ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getPFEvents
 * Signature: (Lcom/mediatek/tv/model/ChannelInfo;Ljava/util/List;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getPFEvents
  (JNIEnv *env, jclass clazz, jobject channelInfo, jobject eventList)
{
    INT32                i4_ret = 0;
    EDB_CH_ID_T          t_ch_id;
    BRDCST_TYPE_T        e_brdcst_type;
    jclass               jclass_AnalogChannelInfo  = NULL;
    jclass               jclass_DvbChannelInfo     = NULL;
    SIZE_T               z_len                     = 0;
    VOID*                pv_pf_events_info         = NULL;

    /*Check para*/
    if (env==NULL || clazz == NULL || channelInfo == NULL)
    {
        JNI_LOGE(("Invalid arguments\r\n"));
        return EDBR_INV_ARG;
    }
    JNI_MEMSET(&t_ch_id,0x0,sizeof(EDB_CH_ID_T));

    jclass_AnalogChannelInfo = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/AnalogChannelInfo");
    jclass_DvbChannelInfo    = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/DvbChannelInfo");

    if ( JNI_IS_INSTANCE_OF(env,channelInfo,jclass_AnalogChannelInfo) )
    {
        e_brdcst_type = BRDCST_TYPE_ANALOG;
        JNI_LOGD(("Current channel is analog ,return\r\n"));
        return EDBR_INFO_NOT_FOUND;
    }
    else if ( JNI_IS_INSTANCE_OF(env,channelInfo,jclass_DvbChannelInfo) )
    {
        e_brdcst_type = BRDCST_TYPE_DVB;
    }

    /*Get channelInfo from java and construct EDB_CH_ID_T*/
    t_ch_id.e_brdcst_type  = e_brdcst_type;
    t_ch_id.ui2_svl_id     = (UINT16)JCOM(env,channelInfo,Int, CMI(channel_info_def,channel_info_getSvlId));
    t_ch_id.ui4_channel_id = (UINT32)JCOM(env,channelInfo,Int, CMI(channel_info_def,channel_info_getChannelId));

    JNI_LOGD(("Channel info brdcst_type=%d svl_id=%d channel_id=%d\r\n",
              t_ch_id.e_brdcst_type,
              t_ch_id.ui2_svl_id,
              t_ch_id.ui4_channel_id));

    /*Get PF event serialized length*/
    a_edb_client_lock();
    i4_ret = a_edb_get_pf_events_len( &t_ch_id,&z_len);
    if (i4_ret != EDBR_OK || z_len <= 0 )
    {
        JNI_LOGD(("Can not get PF event length by Channel  brdcst_type=%d svl_id=%d channel_id=%d i4_re=%d z_len=%d\r\n",
                  t_ch_id.e_brdcst_type,
                  t_ch_id.ui2_svl_id,
                  t_ch_id.ui4_channel_id,
                  i4_ret,
                  z_len));
        a_edb_client_unlock();
        return i4_ret;
    }

    JNI_LOGD(("Get PF event length by Channel  brdcst_type=%d svl_id=%d channel_id=%d i4_ret=%d z_len=%d\r\n",
                  t_ch_id.e_brdcst_type,
                  t_ch_id.ui2_svl_id,
                  t_ch_id.ui4_channel_id,
                  i4_ret,
                  z_len));


    /*Alloc memory for been filled by PF serialized events,Do not foget free it*/
    pv_pf_events_info = (VOID*)JNI_MALLOC(z_len);
    JNI_MEMSET(pv_pf_events_info,0x0,z_len);

    i4_ret = a_edb_get_pf_events(&t_ch_id,pv_pf_events_info,z_len);
    if (i4_ret != EDBR_OK)
    {
        JNI_LOGD(("Can not get PF event info by Channel  brdcst_type=%d svl_id=%d channel_id=%d i4_ret=%d z_len=%d\r\n",
                  t_ch_id.e_brdcst_type,
                  t_ch_id.ui2_svl_id,
                  t_ch_id.ui4_channel_id,
                  i4_ret,
                  z_len));
    }
    a_edb_client_unlock();



    JNI_LOGD(("Got PF event info by Channel  brdcst_type=%d svl_id=%d channel_id=%d i4_ret=%d z_len=%d  \r\n",
                  t_ch_id.e_brdcst_type,
                  t_ch_id.ui2_svl_id,
                  t_ch_id.ui4_channel_id,
                  i4_ret,
                  z_len
                  ));


    /*pv_pf_events_info is serialized events,deserialize it*/
    {
        UINT8*                      pui1_buffer_parse       = NULL;
        UINT32                      ui4_token               = 0;
        BOOL                        b_has_p                 = FALSE;
        BOOL                        b_has_f                 = FALSE;
        EDB_POOL_EVENT_INFO_T*      pt_pool_event           = NULL;
        UINT32                      ui4_pool_event_len      = 0;
        

        if (pv_pf_events_info!= NULL)
        {
            pui1_buffer_parse = (UINT8*)pv_pf_events_info;

            JNI_LOGD(("Got PF event info by Channel  brdcst_type=%d svl_id=%d channel_id=%d i4_ret=%d z_len=%d PFflag=%d %d \r\n",
                  t_ch_id.e_brdcst_type,
                  t_ch_id.ui2_svl_id,
                  t_ch_id.ui4_channel_id,
                  i4_ret,
                  z_len,
                  pui1_buffer_parse[0],
                  pui1_buffer_parse[1]
                  ));
            if (pui1_buffer_parse[0] == (UINT8)0x1)/*Need Read present*/
            {
                b_has_p = TRUE; 
            }
            if (pui1_buffer_parse[1] == (UINT8)0x1)/*Need Read following*/
            {
                b_has_f = TRUE; 
            }
            ui4_token += 2;
            pui1_buffer_parse += ui4_token;

            if (b_has_p == TRUE)
            {
                i4_ret = edb_unserialize_pool_event(&pt_pool_event,pui1_buffer_parse,&ui4_pool_event_len);
                pui1_buffer_parse += ui4_pool_event_len;
                JNI_LOGD(("Got present event %x\r\n",pt_pool_event)); 
                
                JCOMV(env,eventList,Void,CMI(List,List_add),0, 
                      edb_convert_pool_event(env,pt_pool_event,t_ch_id.ui2_svl_id,t_ch_id.ui4_channel_id) 
                );
                //TODO
                if (pt_pool_event != NULL)
                {
                    edb_dvb_free_single_event_obj(pt_pool_event);
                }
            }
            else/*Add null to list*/
            {
                JCOMV(env,eventList,Void,CMI(List,List_add),0,NULL );
            }


            if (b_has_f == TRUE)
            {
                i4_ret = edb_unserialize_pool_event(&pt_pool_event,pui1_buffer_parse,&ui4_pool_event_len);
                pui1_buffer_parse += ui4_pool_event_len;
                JNI_LOGD(("Got follow event %x\r\n",pt_pool_event)); 
                JCOMV(env,eventList,Void,CMI(List,List_add),1, 
                      edb_convert_pool_event(env,pt_pool_event,t_ch_id.ui2_svl_id,t_ch_id.ui4_channel_id) 
                );
                if (pt_pool_event != NULL)
                {
                    edb_dvb_free_single_event_obj(pt_pool_event);
                }
            }
            else/*Add null to list*/
            {
                JCOMV(env,eventList,Void,CMI(List,List_add),1,NULL );
            }
        }
    }

    if (pv_pf_events_info != NULL)
    {
        JNI_FREE(pv_pf_events_info);
    }
    return i4_ret;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getScheduleEvents
 * Signature: (Lcom/mediatek/tv/model/ChannelInfo;JJLjava/util/List;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getScheduleEvents
  (JNIEnv *env, jclass clazz, jobject channelInfo, jlong startTime, jlong endTime, jobject eventList)
{
    INT32                i4_ret = 0;
    EDB_CH_ID_T          t_ch_id;
    BRDCST_TYPE_T        e_brdcst_type;
    jclass               jclass_AnalogChannelInfo  = NULL;
    jclass               jclass_DvbChannelInfo     = NULL;
    SIZE_T               z_len                     = 0;
    VOID*                pv_schedule_event_buffer  = NULL;
    TIME_T               t_start_time              = NULL_TIME;
    TIME_T               t_end_time                = NULL_TIME;


    /*Check para*/
    if (env==NULL || clazz == NULL || channelInfo == NULL)
    {
        JNI_LOGE(("Invalid arguments\r\n"));
        return EDBR_INV_ARG;
    }
    JNI_MEMSET(&t_ch_id,0x0,sizeof(EDB_CH_ID_T));

    jclass_AnalogChannelInfo = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/AnalogChannelInfo");
    jclass_DvbChannelInfo    = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/DvbChannelInfo");

    if ( JNI_IS_INSTANCE_OF(env,channelInfo,jclass_AnalogChannelInfo) )
    {
        e_brdcst_type = BRDCST_TYPE_ANALOG;
        JNI_LOGD(("Current channel is analog ,return\r\n"));
        return EDBR_INFO_NOT_FOUND;
    }
    else if ( JNI_IS_INSTANCE_OF(env,channelInfo,jclass_DvbChannelInfo) )
    {
        e_brdcst_type = BRDCST_TYPE_DVB;
    }

    /*Get channelInfo from java and construct EDB_CH_ID_T*/
    t_ch_id.e_brdcst_type  = e_brdcst_type;
    t_ch_id.ui2_svl_id     = (UINT16)JCOM(env,channelInfo,Int, CMI(channel_info_def,channel_info_getSvlId));
    t_ch_id.ui4_channel_id = (UINT32)JCOM(env,channelInfo,Int, CMI(channel_info_def,channel_info_getChannelId));

    JNI_LOGD(("Channel info brdcst_type=%d svl_id=%d channel_id=%d\r\n",
              t_ch_id.e_brdcst_type,
              t_ch_id.ui2_svl_id,
              t_ch_id.ui4_channel_id));

    /*Get Schedule event serialized length*/
    a_edb_client_lock();
    t_start_time = (TIME_T)startTime;
    t_end_time   = (TIME_T)endTime;
    i4_ret = a_edb_get_schedule_events_len( &t_ch_id,&z_len,t_start_time,t_end_time);
    if (i4_ret != EDBR_OK || z_len <= 0 )
    {
        JNI_LOGD(("Can not get Schedule event length by Channel  brdcst_type=%d svl_id=%d channel_id=%d i4_re=%d z_len=%d\r\n",
                  t_ch_id.e_brdcst_type,
                  t_ch_id.ui2_svl_id,
                  t_ch_id.ui4_channel_id,
                  i4_ret,
                  z_len));
        a_edb_client_unlock();
        return i4_ret;
    }

    JNI_LOGD(("Get Schedule event length by Channel  brdcst_type=%d svl_id=%d channel_id=%d i4_ret=%d z_len=%d\r\n",
                  t_ch_id.e_brdcst_type,
                  t_ch_id.ui2_svl_id,
                  t_ch_id.ui4_channel_id,
                  i4_ret,
                  z_len));


    /*Alloc memory for been filled by Schedule serialized events,Do not foget free it*/
    pv_schedule_event_buffer = (VOID*)JNI_MALLOC(z_len);
    JNI_MEMSET(pv_schedule_event_buffer,0x0,z_len);

    i4_ret = a_edb_get_schedule_events(&t_ch_id,pv_schedule_event_buffer,z_len);
    if (i4_ret != EDBR_OK)
    {
        JNI_LOGD(("Can not get Schedule event info by Channel  brdcst_type=%d svl_id=%d channel_id=%d i4_ret=%d z_len=%d\r\n",
                  t_ch_id.e_brdcst_type,
                  t_ch_id.ui2_svl_id,
                  t_ch_id.ui4_channel_id,
                  i4_ret,
                  z_len));
    }
    a_edb_client_unlock();



    JNI_LOGD(("Got Schedule event info by Channel  brdcst_type=%d svl_id=%d channel_id=%d i4_ret=%d z_len=%d  \r\n",
                  t_ch_id.e_brdcst_type,
                  t_ch_id.ui2_svl_id,
                  t_ch_id.ui4_channel_id,
                  i4_ret,
                  z_len
                  ));


    /*pv_schedule_event_buffer is serialized events,deserialize it*/
   
    /*deserialize pool event start*/
    {
        UINT8*                      pui1_buffer_parse  = NULL;
        UINT32                      ui4_token          = 0;
        EDB_POOL_EVENT_INFO_T*      pt_pool_event      = NULL;
        UINT32                      ui4_pool_event_len = 0;
        UINT16                      ui2_event_id_num   = 0;
        INT32                       i                  = 0;
        
        //deserialize event
        if (pv_schedule_event_buffer!= NULL)
        {
            pui1_buffer_parse = pv_schedule_event_buffer;
            x_memcpy(&ui2_event_id_num,pv_schedule_event_buffer,sizeof(UINT16));
            ui4_token += 2;
            for (i=0 ; i<ui2_event_id_num; i++)
            {
                i4_ret = edb_unserialize_pool_event(&pt_pool_event,pui1_buffer_parse + ui4_token,&ui4_pool_event_len);

                JNI_LOGD(("Got schedule event %x\r\n",pt_pool_event)); 
                JCOMV(env,eventList,Void,CMI(List,List_add),i, 
                      edb_convert_pool_event(env,pt_pool_event,t_ch_id.ui2_svl_id,t_ch_id.ui4_channel_id) 
                );
                if (pt_pool_event != NULL)
                {
                    edb_dvb_free_single_event_obj(pt_pool_event);
                }
                ui4_token += ui4_pool_event_len;
            }
        }
    }
    /* deserialize pool event end*/
 

    if (pv_schedule_event_buffer != NULL)
    {
        JNI_FREE(pv_schedule_event_buffer);
    }
    return i4_ret;
}


static VOID _edb_nfy_fct(
        HANDLE_T                h_edb,
        EDB_NFY_REASON_T        e_reason,
        VOID*                   pv_nfy_tag,
        UINT32                  ui4_data1,
        UINT32                  ui4_data2)
{
    jclass      jclass_TVCallBack   = NULL;
    int         ret                 = 0;
    VOID*       pv_param1           = NULL;
    VOID*       pv_param2           = NULL;
    UINT32      ui4_reason          = 0;
    jint        withoutAttachDetach   = 0;        
    /*
     * Check if we're already one with the VM.
     */
    withoutAttachDetach = JNI_ALREADY_ONE_WITH_VM(g__env);
    if (!withoutAttachDetach)
    {
        ret = JNI_ATTACH_CURRENT_THREAD(g__env);
        if (ret < 0)
        {
            JNI_LOGD((LOG_TAG"[ERROR][%d]{%s}\n", __LINE__, __func__));
        }
    }
    else
    {
        JNI_LOGD((LOG_TAG"[Warning][%d]{%s}We are already one with VM\n", __LINE__, __func__));
    }

    jclass_TVCallBack = JNI_GET_CLASS_BY_NAME(g__env,"com/mediatek/tv/service/TVCallBack");


    JNI_LOGD(("LOG_TAG[%d]{%s}\n", __LINE__, __func__));
    if (e_reason & EDB_REASON_NOW_EVENT_UPDATED)
    {
        JNI_LOGD(("EDB_REASON_NOW_EVENT_UPDATED\r\n"));
        ui4_reason = EVENT_REASON_PF_UPDATE;
    }

    if (e_reason & EDB_REASON_NEX_EVENT_UPDATED)
    {
        JNI_LOGD(("EDB_REASON_NEX_EVENT_UPDATED\r\n"));
        ui4_reason = EVENT_REASON_PF_UPDATE;
    }

    if (e_reason & EDB_REASON_EVENT_IN_ACTIVE_WIN_UPDATED)
    {
        JNI_LOGD(("EDB_REASON_EVENT_IN_ACTIVE_WIN_UPDATED\r\n"));
        pv_param1 = (VOID*) ui4_data1;  /* SVL_ID */
        pv_param2 = (VOID*) ui4_data2;  /* Channel_ID */
        JNI_LOGD(("SVL_ID=%d Channel_ID=%d\r\n",pv_param1,pv_param2));
        ui4_reason = EVENT_REASON_SCHEDULE_UPDATE;
    }

    //Notify java
    JNI_CALL_STATIC_METHODV(g__env,jclass_TVCallBack,Void,CMI(TVCallBack_def,TVCallBack_eventServiceNotifyUpdate),ui4_reason,ui4_data1,ui4_data2);
    if (!withoutAttachDetach)
    {
        JNI_DETACH_CURRENT_THREAD(g__env);
    }
    
    return;
}

VOID x_event_service_init(VOID)
{
   a_edb_set_listener(_edb_nfy_fct,NULL);
}


#ifdef x_memcpy
#undef x_memcpy
#define x_memcpy JNI_MEMCPY
#endif

#ifdef x_memset
#undef x_memset
#define x_memset JNI_MEMSET
#endif

#define EDB_ALLOC
#ifdef EDB_ALLOC
#undef EDB_ALLOC
#define EDB_ALLOC JNI_MALLOC
#endif


#define EDB_FREE
#ifdef EDB_FREE
#undef EDB_FREE
#define EDB_FREE JNI_FREE
#endif
/*copy from edb_pool.c*/
INT32 edb_unserialize_pool_event(EDB_POOL_EVENT_INFO_T**   ppt_pool_event , UINT8* pui1_serialized_event,UINT32* pui4_pool_event_len)
{
    SIZE_T                      z_size             = 0;
    UINT8                       ui1_num            = 0;
    UINT16                      ui2_num            = 0;
    UINT16                      ui2_event_length   = 0;
    EDB_EVENT_TIME_SHIFT        t_temp_time_shift  = {0,0};
    EDB_EVENT_TIME_SHIFT        t_empty_time_shift = {0,0};//indicates this filed has not been set from eit
    UINT16                      ui2_event_id;
    INT32                       i4_ret = EDBR_OK;
    EDB_POOL_EVENT_INFO_T*      pt_pool_event = NULL;

    if (ppt_pool_event == NULL)
    {
        return EDBR_INTERNAL_ERROR;
    }    


    x_memcpy(&ui2_event_id,pui1_serialized_event,sizeof(ui2_event_id)); 
    pui1_serialized_event += sizeof(ui2_event_id);
    x_memcpy(&ui2_event_length,pui1_serialized_event,sizeof(ui2_event_length)); 
    pui1_serialized_event += sizeof(ui2_event_length);

    do
    {
        pt_pool_event = EDB_ALLOC(sizeof(EDB_POOL_EVENT_INFO_T));
        if(pt_pool_event == NULL)
        {   
            i4_ret = EDBR_OUT_OF_MEM;
            break;
        }
        x_memset(pt_pool_event ,0x0,sizeof(EDB_POOL_EVENT_INFO_T));

        pt_pool_event->ui2_event_id = ui2_event_id;

        x_memcpy(&(pt_pool_event->ui1_table_id),pui1_serialized_event,  sizeof(UINT8));
        pui1_serialized_event += sizeof(UINT8);
        x_memcpy( &(pt_pool_event->ui1_sect_num), pui1_serialized_event,sizeof(UINT8));
        pui1_serialized_event += sizeof(UINT8);
        x_memcpy( &(pt_pool_event->t_start_time),pui1_serialized_event, sizeof(TIME_T));
        pui1_serialized_event += sizeof(TIME_T);
        x_memcpy( &(pt_pool_event->t_duration), pui1_serialized_event,sizeof(TIME_T));
        pui1_serialized_event += sizeof(TIME_T);
        x_memcpy( &(pt_pool_event->b_caption),pui1_serialized_event, sizeof(BOOL));
        pui1_serialized_event += sizeof(BOOL);
        x_memcpy(&(pt_pool_event->b_free_ca_mode),pui1_serialized_event,  sizeof(BOOL));
        pui1_serialized_event += sizeof(BOOL);
        x_memcpy(&(pt_pool_event->b_has_private),pui1_serialized_event,  sizeof(BOOL));
        pui1_serialized_event += sizeof(BOOL);
        x_memcpy( &(pt_pool_event->ui1_num_rating), pui1_serialized_event,sizeof(UINT8));
        pui1_serialized_event += sizeof(UINT8);
        x_memcpy( (pt_pool_event->aui2_ca_system_id),pui1_serialized_event, sizeof(pt_pool_event->aui2_ca_system_id)); 
        pui1_serialized_event += sizeof(pt_pool_event->aui2_ca_system_id); 
        x_memcpy( &(pt_pool_event->ui1_num_event_category), pui1_serialized_event,sizeof(UINT8));
        pui1_serialized_event += sizeof(UINT8);
        x_memcpy( (pt_pool_event->aui1_event_category), pui1_serialized_event,sizeof(pt_pool_event->aui1_event_category)); 
        pui1_serialized_event += sizeof(pt_pool_event->aui1_event_category); 
        x_memcpy( &(pt_pool_event->ui1_guidance_mode),pui1_serialized_event, sizeof(UINT8));
        pui1_serialized_event += sizeof(UINT8);


        x_memcpy(&z_size,pui1_serialized_event, sizeof(SIZE_T));
        pui1_serialized_event += sizeof(SIZE_T);
        if(z_size > 0)
        {
            pt_pool_event->ps_event_title = EDB_ALLOC(z_size+1);
            if(pt_pool_event->ps_event_title == NULL)
            {   
                i4_ret = EDBR_OUT_OF_MEM;
                break;
            }
            x_memset(pt_pool_event->ps_event_title ,0x0,(z_size+1));
            x_memcpy(pt_pool_event->ps_event_title,pui1_serialized_event, z_size);
            pui1_serialized_event += z_size;
        }

        x_memcpy(&z_size,pui1_serialized_event, sizeof(SIZE_T));
        pui1_serialized_event += sizeof(SIZE_T);
        if(z_size > 0)
        {
            pt_pool_event->ps_event_detail = EDB_ALLOC(z_size+1);
            if(pt_pool_event->ps_event_detail == NULL)
            {   
                i4_ret = EDBR_OUT_OF_MEM;
                break;
            }
            x_memset(pt_pool_event->ps_event_detail ,0x0,(z_size+1));
            x_memcpy(pt_pool_event->ps_event_detail,pui1_serialized_event, z_size);
            pui1_serialized_event += z_size;
        }

        x_memcpy(&z_size,pui1_serialized_event, sizeof(SIZE_T));
        pui1_serialized_event += sizeof(SIZE_T);
        if(z_size > 0)
        {
            pt_pool_event->ps_event_guidance = EDB_ALLOC(z_size+1);
            if(pt_pool_event->ps_event_guidance == NULL)
            {   
                i4_ret = EDBR_OUT_OF_MEM;
                break;
            }
            x_memset(pt_pool_event->ps_event_guidance ,0x0,(z_size+1));
            x_memcpy(pt_pool_event->ps_event_guidance,pui1_serialized_event, z_size);
            pui1_serialized_event += z_size;
        }

        x_memcpy(&ui1_num,pui1_serialized_event, sizeof(UINT8));
        pui1_serialized_event += sizeof(UINT8); 
        if(ui1_num > 0)
        {
            pt_pool_event->pt_event_linkage_list = EDB_ALLOC(sizeof(EDB_EVENT_LINKAGE_LIST_T));
            if(pt_pool_event->pt_event_linkage_list == NULL)
            {   
                i4_ret = EDBR_OUT_OF_MEM;
                break;
            }
            pt_pool_event->pt_event_linkage_list->ui1_num_event_linkage = ui1_num;
            pt_pool_event->pt_event_linkage_list->pt_event_linkage = EDB_ALLOC(sizeof(EDB_EVENT_LINKAGE_T) * ui1_num ) ; 
            if(pt_pool_event->pt_event_linkage_list->pt_event_linkage == NULL)
            {   
                i4_ret = EDBR_OUT_OF_MEM;
                break;
            }
            x_memcpy(pt_pool_event->pt_event_linkage_list->pt_event_linkage,pui1_serialized_event, sizeof(EDB_EVENT_LINKAGE_T)*ui1_num);       
            pui1_serialized_event += sizeof(EDB_EVENT_LINKAGE_T)*ui1_num;
        }
        /*    else
              {
              pt_pool_event->pt_event_linkage_list = NULL;
              }
              */
        x_memcpy(&t_temp_time_shift,pui1_serialized_event, sizeof(EDB_EVENT_TIME_SHIFT));
        pui1_serialized_event += sizeof(EDB_EVENT_TIME_SHIFT); 

        if(x_memcmp(&t_temp_time_shift,&t_empty_time_shift,sizeof(EDB_EVENT_POOL_CH_KEY)) != 0)//indicates it is not empty ,need to sync
        {
            pt_pool_event->pt_time_shift = EDB_ALLOC(sizeof(EDB_EVENT_TIME_SHIFT));
            if(pt_pool_event->pt_time_shift == NULL)
            {   
                i4_ret = EDBR_OUT_OF_MEM;
                break;
            }
            x_memcpy(pt_pool_event->pt_time_shift,&t_temp_time_shift,sizeof(EDB_EVENT_TIME_SHIFT));
        }
        /*    else
              {
              pt_pool_event->pt_time_shift = NULL;
              }
              */


/*because changelist:1005638(edb implement the component text), we need to handle the component text when serializing and 
        unserializing, if 0 it for temporary,please change it to if 1 after testing.
    */

#if 0
        x_memcpy(&ui1_num,pui1_serialized_event,sizeof(UINT8));
        pui1_serialized_event += sizeof(UINT8); 
        if(ui1_num > 0)
        {
            EDB_EVENT_COMPONENT*                pt_event_component_list = NULL;
            UINT8                               ui1_i = 0;
        
            if(ui1_num > MAX_EVENT_DESCRIPTOR_LIST_NUMBER_FOR_CHECKING_UNSERIALIZING_EVENT)
            {
                i4_ret = EDBR_INTERNAL_ERROR; 
                break;
            }
            pt_pool_event->pt_component_list = EDB_PARTITION_ALLOC(sizeof(EDB_EVENT_COMPONENT_LIST));
            if(pt_pool_event->pt_component_list == NULL)
            {   
                i4_ret = EDBR_OUT_OF_MEM;
                break;
            }
            pt_pool_event->pt_component_list->ui1_num = ui1_num;

            
            pt_pool_event->pt_component_list->pt_event_component = EDB_PARTITION_ALLOC(sizeof(EDB_EVENT_COMPONENT)*ui1_num);
            if(pt_pool_event->pt_component_list->pt_event_component == NULL)
            {   
                pt_pool_event->pt_component_list->ui1_num = 0;
                i4_ret = EDBR_OUT_OF_MEM;
                break;
            }

            pt_event_component_list = pt_pool_event->pt_component_list->pt_event_component ;

            for(ui1_i = 0 ; ui1_i < ui1_num ; ui1_i++)
            {
                x_memcpy(&(pt_event_component_list->ui1_stream_content), pui1_serialized_event, sizeof(UINT8));
                pui1_serialized_event += sizeof(UINT8); 

                x_memcpy(&(pt_event_component_list->ui1_component_type), pui1_serialized_event , sizeof(UINT8));
                pui1_serialized_event += sizeof(UINT8); 

                x_memcpy(&(pt_event_component_list->ui1_component_tag), pui1_serialized_event , sizeof(UINT8));
                pui1_serialized_event += sizeof(UINT8); 

                x_memcpy(&(pt_event_component_list->t_lang), pui1_serialized_event , sizeof(ISO_639_LANG_T));
                pui1_serialized_event += sizeof(ISO_639_LANG_T); 

                x_memcpy(&z_size,pui1_serialized_event, sizeof(SIZE_T));
                pui1_serialized_event += sizeof(SIZE_T);
                if(z_size > 0)
                {
                    if(z_size > MAX_EVENT_TEXT_LENGTH_FOR_CHECKING_UNSERIALIZING_EVENT)
                    {
                        i4_ret = EDBR_INTERNAL_ERROR; 
                        break;
                    }
                    pt_event_component_list->ps_text= EDB_PARTITION_ALLOC(z_size+1);
                    if(pt_event_component_list->ps_text == NULL)
                    {   
                        i4_ret = EDBR_OUT_OF_MEM;
                        break;
                    }
                    x_memset(pt_event_component_list->ps_text ,0x0,(z_size+1));
                    x_memcpy(pt_event_component_list->ps_text,pui1_serialized_event, z_size);
                    pui1_serialized_event += z_size;
                }

                pt_event_component_list++;
            }
            
        }
#endif
    /*     else 
        {
            pt_pool_event->pt_component_list = NULL;
        }
        */


        x_memcpy(&ui2_num,pui1_serialized_event,sizeof(UINT16));
        pui1_serialized_event += sizeof(UINT16); 
        if(ui2_num > 0)
        {
            pt_pool_event->pt_ca_system = EDB_ALLOC(sizeof(EDB_EVENT_CA_SYSTEM_ID_LIST_T));
            if(pt_pool_event->pt_ca_system == NULL)
            {   
                i4_ret = EDBR_OUT_OF_MEM;
                break;
            }
            pt_pool_event->pt_ca_system->ui2_num_ca_ids = ui2_num;
            pt_pool_event->pt_ca_system->pui2_ca_ids = EDB_ALLOC(sizeof(UINT16)*ui2_num);
            if(pt_pool_event->pt_ca_system->pui2_ca_ids == NULL)
            {   
                i4_ret = EDBR_OUT_OF_MEM;
                break;
            }
            x_memcpy(pt_pool_event->pt_ca_system->pui2_ca_ids,pui1_serialized_event, sizeof(UINT16)*ui2_num);
            pui1_serialized_event += sizeof(UINT16)*ui2_num;
        }

        x_memcpy(&ui1_num, pui1_serialized_event,sizeof(UINT8));
        pui1_serialized_event += sizeof(UINT8); 
        if(ui1_num > 0)
        {
            pt_pool_event->pt_rating_list = EDB_ALLOC(sizeof(EDB_EVENT_RATING_LIST_T));
            if(pt_pool_event->pt_rating_list == NULL)
            {   
                i4_ret = EDBR_OUT_OF_MEM;
                break;
            }
            pt_pool_event->pt_rating_list->ui1_num = ui1_num;
            pt_pool_event->pt_rating_list->pt_rating = EDB_ALLOC(sizeof(EDB_EVENT_RATING_T)*ui1_num);
            if(pt_pool_event->pt_rating_list->pt_rating == NULL)
            {   
                i4_ret = EDBR_OUT_OF_MEM;
                break;
            }
            x_memcpy(pt_pool_event->pt_rating_list->pt_rating,pui1_serialized_event, sizeof(EDB_EVENT_RATING_T)*ui1_num);
            pui1_serialized_event += sizeof(EDB_EVENT_RATING_T)*ui1_num;
        }

        *ppt_pool_event = pt_pool_event;
    }while(0);

    *pui4_pool_event_len = sizeof(ui2_event_id) + sizeof(ui2_event_length) + ui2_event_length;
    return i4_ret;
}



VOID edb_dvb_free_single_event_obj(
        VOID*       pt_event)
{
    INT32       i           = 0;
    INT32       j           = 0;
    EDB_POOL_EVENT_INFO_T* pt_edb_event_info = NULL;


    if (pt_event==NULL)
    {
        DBG_ABORT( DBG_MOD_EDB );
    }

    pt_edb_event_info = (EDB_POOL_EVENT_INFO_T*)pt_event;
#ifdef EDB_INTERNAL_MONITOR_EVENT
    if (ui2_monitor_event_id == pt_edb_event_info->ui2_event_id)
    {
        DBG_INFO((_EDB_INFO"Begin free event object\r\n"));
    }
#endif
    /*Free memory*/
    if (pt_edb_event_info->pt_event_linkage_list != NULL)
    {
        if (pt_edb_event_info->pt_event_linkage_list->pt_event_linkage != NULL)
        {
            EDB_FREE(pt_edb_event_info->pt_event_linkage_list->pt_event_linkage);
            pt_edb_event_info->pt_event_linkage_list->pt_event_linkage = NULL;
        }
        EDB_FREE(pt_edb_event_info->pt_event_linkage_list);
        pt_edb_event_info->pt_event_linkage_list = NULL;
    }


    if (pt_edb_event_info->ps_event_title != NULL)
    {   
        EDB_FREE(pt_edb_event_info->ps_event_title);
        pt_edb_event_info->ps_event_title = NULL;
    }

    if (pt_edb_event_info->ps_event_detail != NULL)
    {
        EDB_FREE(pt_edb_event_info->ps_event_detail);
        pt_edb_event_info->ps_event_detail = NULL;

    }

    if (pt_edb_event_info->ps_event_guidance != NULL)
    {
        EDB_FREE(pt_edb_event_info->ps_event_guidance);
        pt_edb_event_info->ps_event_guidance = NULL;
    }

    if (pt_edb_event_info->pt_event_extended_list != NULL)
    {
        EDB_EVENT_EXTENDED_ITEM*        pt_event_extended_item = NULL;
        if (pt_edb_event_info->pt_event_extended_list->pt_event_extended != NULL)
        {
            for(i=0;i<pt_edb_event_info->pt_event_extended_list->ui1_event_extended_num;i++)
            {
                EDB_EVENT_EXTENDED*  pt_event_extended = pt_edb_event_info->pt_event_extended_list->pt_event_extended + i;
                if (pt_event_extended->ps_extended_text != NULL)
                {
                    EDB_FREE(pt_event_extended->ps_extended_text);
                    pt_event_extended->ps_extended_text = NULL;
                }

                if (pt_event_extended->pt_extended_item_list != NULL)
                {
                    EDB_EVENT_EXTENDED_ITEM_LIST_T*     pt_extended_item_list = 
                        pt_event_extended->pt_extended_item_list;

                    if (pt_extended_item_list->pt_extended_items != NULL)
                    {
                        for (j=0;j<pt_extended_item_list->ui1_extended_items_num;j++)
                        {
                            pt_event_extended_item  = pt_extended_item_list->pt_extended_items + j;
                            if (pt_event_extended_item != NULL)
                            {
                                if (pt_event_extended_item->ps_extended_item_descript != NULL)
                                {
                                    EDB_FREE(pt_event_extended_item->ps_extended_item_descript);
                                    pt_event_extended_item->ps_extended_item_descript = NULL;

                                }
                                if (pt_event_extended_item->ps_extended_item_text != NULL)
                                {
                                    EDB_FREE(pt_event_extended_item->ps_extended_item_text);
                                    pt_event_extended_item->ps_extended_item_text = NULL;
                                }
                            }
                        }
                        EDB_FREE(pt_extended_item_list->pt_extended_items);
                        pt_extended_item_list->pt_extended_items = NULL;

                    }
                }

                if (pt_event_extended->pt_extended_item_list != NULL)
                {
                    EDB_FREE(pt_event_extended->pt_extended_item_list);
                    pt_event_extended->pt_extended_item_list = NULL;
                }

            }

            if (pt_edb_event_info->pt_event_extended_list->pt_event_extended != NULL)
            {
                EDB_FREE(pt_edb_event_info->pt_event_extended_list->pt_event_extended);
                pt_edb_event_info->pt_event_extended_list->pt_event_extended = NULL;
            }

            EDB_FREE(pt_edb_event_info->pt_event_extended_list );
            pt_edb_event_info->pt_event_extended_list = NULL;

        }
    }


    if (pt_edb_event_info->pt_time_shift != NULL)
    {
        EDB_FREE(pt_edb_event_info->pt_time_shift);
        pt_edb_event_info->pt_time_shift = NULL;
    }

    if (pt_edb_event_info->pt_component_list != NULL)
    {
        if (pt_edb_event_info->pt_component_list->pt_event_component != NULL)
        {
            EDB_EVENT_COMPONENT*                pt_event_component = NULL;
            for (i=0;i<pt_edb_event_info->pt_component_list->ui1_num;i++)
            {
                pt_event_component = pt_edb_event_info->pt_component_list->pt_event_component + i;
                if(pt_event_component->ps_text != NULL)
                {
                    EDB_FREE(pt_event_component->ps_text);
                    pt_event_component->ps_text = NULL;

                }
            }
            EDB_FREE(pt_edb_event_info->pt_component_list->pt_event_component);
            pt_edb_event_info->pt_component_list->pt_event_component = NULL;

        }
        EDB_FREE(pt_edb_event_info->pt_component_list);
        pt_edb_event_info->pt_component_list = NULL;

    }

    if (pt_edb_event_info->pt_ca_system != NULL)
    {
        if (pt_edb_event_info->pt_ca_system->pui2_ca_ids != NULL)
        {
            EDB_FREE(pt_edb_event_info->pt_ca_system->pui2_ca_ids );
            pt_edb_event_info->pt_ca_system->pui2_ca_ids = NULL;
        }
        EDB_FREE(pt_edb_event_info->pt_ca_system );
    }

#if 0
    if (pt_edb_event_info->pt_category_list != NULL)
    {
        if (pt_edb_event_info->pt_category_list->pt_category != NULL)
        {
            EDB_FREE(pt_edb_event_info->pt_category_list->pt_category );
            pt_edb_event_info->pt_category_list->pt_category = NULL;
        }

        EDB_FREE(pt_edb_event_info->pt_category_list );
        pt_edb_event_info->pt_category_list = NULL;
    }
#endif

    if (pt_edb_event_info->pt_rating_list != NULL)
    {
        if (pt_edb_event_info->pt_rating_list->pt_rating != NULL)
        {
            EDB_FREE(pt_edb_event_info->pt_rating_list->pt_rating );
            pt_edb_event_info->pt_rating_list->pt_rating = NULL;
        }

        EDB_FREE(pt_edb_event_info->pt_rating_list);
        pt_edb_event_info->pt_rating_list = NULL;
    }


    EDB_FREE(pt_edb_event_info);
    //pt_edb_event_info = NULL;

    return ;
}


/*Convert pool event into java Eventinfo*/
jobject  edb_convert_pool_event(
        JNIEnv                      *env,
        EDB_POOL_EVENT_INFO_T*      pt_pool_event,
        UINT16                      ui2_svl_id,
        UINT32                      ui4_channel_id)
{
    INT32                       i4_ret           = 0;
    jclass                      jclass_EventInfo = NULL;
    jobject                     jo_eventInfo     = NULL;

    jclass_EventInfo = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/EventInfo");

    jo_eventInfo       = JNI_NEW_OBJECT(env,jclass_EventInfo, CMI(EventInfo_def,EventInfo_init));

    /*Process svlId*/
    JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setSvlId),     (jint)(ui2_svl_id) ); 

    /*Process channelId*/
    JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setChannelId), (jint)(ui4_channel_id) ); 


    /*Process eventId*/
    JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setEventId),   (jint)(pt_pool_event->ui2_event_id) ); 

    /*Process startTime*/
    JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setStartTime), (jlong) (pt_pool_event->t_start_time) ); 


    /*Process duration*/
    JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setDuration), (jlong) (pt_pool_event->t_duration) ); 


    /*Process caption*/
    JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setCaption), (jboolean) (pt_pool_event->b_caption) ); 

    /*Process freeCaMode*/
    JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setFreeCaMode), (jboolean) (pt_pool_event->b_free_ca_mode) ); 

    /*Process eventTitle*/
    {
        jobject jo_EventTitle = JNI_NEW_STRING_UTF(env,pt_pool_event->ps_event_title);
        if (jo_EventTitle != NULL)
        {
            JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setEventTitle),  (jo_EventTitle) ); 
            JNI_DEL_LOCAL_REF(env,jo_EventTitle);
        }
    }

    /*Process eventDetail*/
    {
        jobject jo_EventDetail = JNI_NEW_STRING_UTF(env,pt_pool_event->ps_event_detail);
        if (jo_EventDetail != NULL)
        {
            JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setEventDetail),  (jo_EventDetail) ); 
            JNI_DEL_LOCAL_REF(env,jo_EventDetail);
        }
    }

    /*Process guidanceMode*/
    JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setGuidanceMode), (jint) (pt_pool_event->ui1_guidance_mode) ); 

    /*Process guidanceText*/
    {
        jobject jo_guidanceText = JNI_NEW_STRING_UTF(env,pt_pool_event->ps_event_guidance);
        if (jo_guidanceText != NULL)
        {
            JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setGuidanceText),  (jo_guidanceText) ); 
            JNI_DEL_LOCAL_REF(env,jo_guidanceText);
        }
    }


    /*Process caSystemId*/
    {
        jintArray                  jintArray_caSystem    = NULL;
        UINT32                     aui4_ca_system_id[EDB_MAX_NUM_CA_SYSTEM] = {0};
        INT32                      i = 0;

        for(i=0; i<EDB_MAX_NUM_CA_SYSTEM; i++)
        {
            aui4_ca_system_id[i] = pt_pool_event->aui2_ca_system_id[i];
        }

        jintArray_caSystem = JNI_NEW_INT_ARRAY(env,EDB_MAX_NUM_CA_SYSTEM);
        JNI_SET_INT_ARRAY_REGION(env,jintArray_caSystem,0,EDB_MAX_NUM_CA_SYSTEM,((jint*)(aui4_ca_system_id)) );
        JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setCaSystemId), jintArray_caSystem ); 
        JNI_DEL_LOCAL_REF(env,jintArray_caSystem);
    }

    /*Process eventCategoryNum*/
    if ((pt_pool_event->ui1_num_event_category) >= EDB_MAX_NUM_CATEGORY)
    {
        pt_pool_event->ui1_num_event_category = EDB_MAX_NUM_CATEGORY;
    }
    JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setEventCategoryNum), (jint) (pt_pool_event->ui1_num_event_category) ); 


    /*Process eventCategory*/
    {
        jintArray                  jintArray_eventCategory    = NULL;
        UINT32                     aui4_event_category[EDB_MAX_NUM_CATEGORY] = {0};
        INT32                      i = 0;

        for(i=0; i<EDB_MAX_NUM_CATEGORY; i++)
        {
            aui4_event_category[i] = pt_pool_event->aui1_event_category[i];
        }

        jintArray_eventCategory = JNI_NEW_INT_ARRAY(env,EDB_MAX_NUM_CATEGORY);
        JNI_SET_INT_ARRAY_REGION(env,jintArray_eventCategory,0,EDB_MAX_NUM_CATEGORY,((jint*)(aui4_event_category)) );
        JCOMV(env,jo_eventInfo,Void,    CMI(EventInfo_def,EventInfo_setEventCategory), jintArray_eventCategory ); 
        JNI_DEL_LOCAL_REF(env,jintArray_eventCategory);
    }

    /*Process eventComponents*/
    if (pt_pool_event->pt_component_list!=NULL && pt_pool_event->pt_component_list->pt_event_component != NULL)
    {
        UINT8   ui1_idx                             = 0;
        jobject jo_components                       = NULL;
        jobject jo_component                        = NULL;

        jo_components = JCOM(env,jo_eventInfo,Object,    CMI(EventInfo_def,EventInfo_getEventComponents) ); 
        if (jo_components != NULL)
        {
            EDB_EVENT_COMPONENT*    pt_event_component  = pt_pool_event->pt_component_list->pt_event_component;
            //pt_pool_event->pt_component_list->ui1_num;
            for( ui1_idx = 0;ui1_idx <pt_pool_event->pt_component_list->ui1_num && ui1_idx < MAX_COMPONENT_INFO; ui1_idx++)
            {
                jo_component = JNI_GET_ARRAY_ELEMENT(env,jo_components,ui1_idx);

                if (jo_component!= NULL)
                {
                    JCOMV(env,jo_component,Void,    CMI(EventComponent_def,EventComponent_setStreamContent), (jshort)pt_event_component->ui1_stream_content ); 
                    JCOMV(env,jo_component,Void,    CMI(EventComponent_def,EventComponent_setComponentType), (jshort)pt_event_component->ui1_component_type ); 
                    JCOMV(env,jo_component,Void,    CMI(EventComponent_def,EventComponent_setComponentTag),  (jshort)pt_event_component->ui1_component_tag ); 
                }
                pt_event_component ++;
                JNI_DEL_LOCAL_REF(env,jo_component);
            }
            JNI_DEL_LOCAL_REF(env,jo_components);
        }
    }


    /*Process eventLinkage*/
    if(pt_pool_event->pt_event_linkage_list!=NULL && pt_pool_event->pt_event_linkage_list->pt_event_linkage!=NULL)
    {
        UINT8   ui1_idx         = 0;
        UINT8   ui1_num         = 0;
        jobject jo_linkages     = NULL;
        jobject jo_linkage      = NULL;

        jo_linkages = JCOM(env,jo_eventInfo,Object,    CMI(EventInfo_def,EventInfo_getEventLinkage) ); 
        for(ui1_idx = 0;ui1_idx<pt_pool_event->pt_event_linkage_list->ui1_num_event_linkage && ui1_num < MAX_EVENT_LINKAGE_INFO; ui1_idx++)   
        {
            if(pt_pool_event->pt_event_linkage_list->pt_event_linkage[ui1_idx].ui1_linkage_type == (UINT8)0x0D)
            {
                jo_linkage = JNI_GET_ARRAY_ELEMENT(env,jo_linkages,ui1_num);

                JCOMV(env,jo_linkage,Void,    CMI(EventLinkage_def,EventLinkage_setOnId), 
                      pt_pool_event->pt_event_linkage_list->pt_event_linkage[ui1_idx].ui2_on_id
                      ); 
                JCOMV(env,jo_linkage,Void,    CMI(EventLinkage_def,EventLinkage_setTsId), 
                      (jshort)pt_pool_event->pt_event_linkage_list->pt_event_linkage[ui1_idx].ui2_ts_id 
                      ); 
                JCOMV(env,jo_linkage,Void,    CMI(EventLinkage_def,EventLinkage_setSvcId),
                      (jshort)pt_pool_event->pt_event_linkage_list->pt_event_linkage[ui1_idx].ui2_svc_id
                      ); 

                ui1_num++;

                JNI_DEL_LOCAL_REF(env,jo_linkage);
            }
        }

        JNI_DEL_LOCAL_REF(env,jo_linkages);
    }

    return jo_eventInfo;
}


EXTERN_C_END
