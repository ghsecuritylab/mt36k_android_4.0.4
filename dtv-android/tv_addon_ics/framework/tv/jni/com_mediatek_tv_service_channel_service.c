#include "tv_jni_util.h"
#include "com_mediatek_tv_service_TVNative.h"
#include "channel/channel_service_wrapper.h"
#include "util/a_common.h"

EXTERN_C_START

#ifdef LOG_TAG
#undef LOG_TAG
#define LOG_TAG "channel_service_jni"
#endif



typedef struct _GROUP_T
{
    UINT32  ui4_start_idx;
    UINT32  ui4_end_idx;
}GROUP_T;

static VOID _svl_nfy (HANDLE_T    h_svl,
                      SVL_COND_T  e_cond,
                      UINT32      ui4_reason,
                      VOID*       pv_tag,
                      UINT32      ui4_data)
{
    jclass      jclass_TVCallBack   = NULL;
    int         ret                 = 0;
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
    
    //Notify java
    JNI_CALL_STATIC_METHODV(g__env,jclass_TVCallBack,Void,CMI(TVCallBack_def,TVCallBack_channelServiceNotifyUpdate),e_cond,ui4_reason,ui4_data);
    JNI_LOGD((LOG_TAG"e_cond=%d ui4_reason=%d ui4_data=%d\n",e_cond,ui4_reason,ui4_data));
    if (!withoutAttachDetach)
    {
        JNI_DETACH_CURRENT_THREAD(g__env);
    }
}



/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    setChannelList_native
 * Signature: (ILjava/util/List;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setChannelList_1native
(JNIEnv *env, jclass clazz, jint channelOperator,jint aimSvlId, jobject channelList)
{
    int                 i                                  = 0;
    jmethodID           jmethod_size_channelList           = NULL;
    jint                channelList_length                 = 0;
    jclass              jclass_AnalogChannelInfo           = NULL;
    jclass              jclass_DvbChannelInfo              = NULL;
    jobject             jobject_channelInfo                = NULL;
    int                 ret                                = -1;
    CHANNEL_INFO_T      channel_info_t                     = {0};
    jobject             serviceName                        = NULL;
    const char*         s_serviceName                      = NULL;
    jobject             shortName                          = NULL;
    const char*         s_shortName                        = NULL;
    jbyteArray          privateData                        = NULL;
    jsize               privateDataLen                     = 0;
    INT8                ai1_private_data[PRIVATE_DATA_LEN] = {0};


    /*check parameters*/
    if (env==NULL || clazz == NULL || channelList == NULL)
    {
        JNI_LOGE(("Invalid arguments"LOG_TAIL));
        return -1;
    }



    channelList_length = (jint)JNI_CALL_OBJECT_METHOD(env,channelList,Int,CMI(List,List_size));
    JNI_LOGD(("channelList_length = %d"LOG_TAIL,channelList_length));
    JNI_LOGI(("a_set_channel_info start Operator=%d channelList_length = %d"LOG_TAIL,channelOperator,channelList_length));

    if (channelList_length == (jint)0x0)
    {
        JNI_LOGD(("The operator list is empty,do nothing return -1"LOG_TAIL));
    }


    a_lock_database(aimSvlId);

    jclass_AnalogChannelInfo = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/AnalogChannelInfo");
    jclass_DvbChannelInfo = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/DvbChannelInfo");

    for(i=0 ; i<channelList_length ;i ++)
    {
        serviceName             = NULL;
        s_serviceName           = NULL;
        privateData             = NULL;
        privateDataLen          = 0;
        jobject_channelInfo     = JCOMV(env,channelList,Object,CMI(List,List_get),i);

        /*Channel common part*/
        JNI_MEMSET(&channel_info_t,0x0,sizeof(CHANNEL_INFO_T) ) ;

        channel_info_t.ui2_svl_id         = (UINT16) JCOM(env,jobject_channelInfo,Int,    CMI(AnalogChannelInfo,AnalogChannelInfo_getSvlId));
        channel_info_t.ui2_svl_rec_id     = (UINT16) JCOM(env,jobject_channelInfo,Int,    CMI(AnalogChannelInfo,AnalogChannelInfo_getSvlRecId));
        channel_info_t.ui4_channel_id     = (UINT32) JCOM(env,jobject_channelInfo,Int,    CMI(AnalogChannelInfo,AnalogChannelInfo_getChannelId));
        channel_info_t.ui1_brdcst_type    = (UINT8)  JCOM(env,jobject_channelInfo,Int,    CMI(AnalogChannelInfo,AnalogChannelInfo_getBrdcstType));
        channel_info_t.ui4_nw_mask        = (UINT32) JCOM(env,jobject_channelInfo,Int,    CMI(AnalogChannelInfo,AnalogChannelInfo_getNwMask));
        channel_info_t.ui4_option_mask    = (UINT32) JCOM(env,jobject_channelInfo,Int,    CMI(AnalogChannelInfo,AnalogChannelInfo_getOptionMask));
        channel_info_t.ui4_service_type   = (UINT32) JCOM(env,jobject_channelInfo,Int,    CMI(AnalogChannelInfo,AnalogChannelInfo_getServiceType));
        channel_info_t.ui4_channel_number = (UINT32) JCOM(env,jobject_channelInfo,Int,    CMI(AnalogChannelInfo,AnalogChannelInfo_getChannelNumber));



        JNI_LOGD(("From java: ac_name=%s ui2_svl_id=%d ui2_svl_rec_id=%d ui4_channel_id=%d "
             "nw_mask=%d option_mask=%d service_type=%d channel_number=%d "
             LOG_TAIL,
             channel_info_t.ac_name,
             channel_info_t.ui2_svl_id,
             channel_info_t.ui2_svl_rec_id,
             channel_info_t.ui4_channel_id,
             channel_info_t.ui4_nw_mask,
             channel_info_t.ui4_option_mask,
             channel_info_t.ui4_service_type,
             channel_info_t.ui4_channel_number
             ));


        /*Process service name*/
        serviceName    = (jstring)   JCOM(env,jobject_channelInfo,Object, CMI(AnalogChannelInfo,AnalogChannelInfo_getServiceName));
        if (serviceName != NULL)
        {
            s_serviceName = JNI_GET_STRING_UTF_CHARS(env,(jstring)serviceName);
            if (s_serviceName!= NULL){
                strncpy(channel_info_t.ac_name,s_serviceName,MAX_PROG_NAME_LEN);
            }

            JNI_RELEASE_STRING_UTF_CHARS(env,(jstring)serviceName,s_serviceName);
            JNI_DEL_LOCAL_REF(env,serviceName);
        }

        /*Process private data*/
        privateData = (jbyteArray)JCOM(env,jobject_channelInfo,Object,CMI(AnalogChannelInfo,AnalogChannelInfo_getPrivateData));
        if(privateData != NULL)
        {
            privateDataLen = JNI_GET_ARRAY_LEN(env,privateData);
            //JNI_LOGI(("privateDataLen=%d "LOG_TAIL,privateDataLen));
            JNI_GET_BYTE_ARRAY_REGION(env,privateData , 0, privateDataLen, (jbyte*)(&(channel_info_t.ai1_private_data[0])) );
            JNI_DEL_LOCAL_REF(env,privateData);
        }

        //Is Analog channel
        if ( JNI_IS_INSTANCE_OF(env,jobject_channelInfo,jclass_AnalogChannelInfo) )
        {
            channel_info_t.ui1_brdcst_medium     =(UINT8)  /*(jbyte)   */  JCOM(env,jobject_channelInfo,Byte,   CMI(AnalogChannelInfo,AnalogChannelInfo_getBrdcstMedium)); 
            channel_info_t.ui4_tv_sys            =(UINT32) /*(jint)    */  JCOM(env,jobject_channelInfo,Int,    CMI(AnalogChannelInfo,AnalogChannelInfo_getTvSys));
            channel_info_t.ui4_audio_sys         =(UINT32) /*(jint)    */  JCOM(env,jobject_channelInfo,Int,    CMI(AnalogChannelInfo,AnalogChannelInfo_getAudioSys));
            channel_info_t.e_vid_color_sys       =(INT32) /*(jint)    */  JCOM(env,jobject_channelInfo,Int,    CMI(AnalogChannelInfo,AnalogChannelInfo_getColorSys));
            channel_info_t.ui4_freq              =(UINT32) /*(jint)    */  JCOM(env,jobject_channelInfo,Int,    CMI(AnalogChannelInfo,AnalogChannelInfo_getFrequency));
            channel_info_t.b_no_auto_fine_tune   =(BOOL)   /*(jboolean)*/  JCOM(env,jobject_channelInfo,Boolean,CMI(AnalogChannelInfo,AnalogChannelInfo_isNoAutoFineTune));
           
            if (jobject_channelInfo != NULL)
            {
                JNI_DEL_LOCAL_REF(env,jobject_channelInfo);
            }

            JNI_LOGD(("From java:ui4_tv_sys=%d ui4_audio_sys=%d e_vid_color_sys=%d ui4_freq=%d ac_name=%s"
                 " ui2_svl_id=%d ui2_svl_rec_id=%d ui4_channel_id=%d channel_number=%d ui1_brdcst_medium=%d channel_info_t.ai1_private_data[0]=%d %d %d  b_no_auto_fine_tune=%d"
                 LOG_TAIL,
                 channel_info_t.ui4_tv_sys,
                 channel_info_t.ui4_audio_sys,
                 channel_info_t.e_vid_color_sys,
                 channel_info_t.ui4_freq,
                 channel_info_t.ac_name,
                 channel_info_t.ui2_svl_id,
                 channel_info_t.ui2_svl_rec_id,
                 channel_info_t.ui4_channel_id,
                 channel_info_t.ui4_channel_number,
                 channel_info_t.ui1_brdcst_medium,
                 channel_info_t.ai1_private_data[0],
                 channel_info_t.ai1_private_data[1],
                 channel_info_t.ai1_private_data[2],
                 channel_info_t.b_no_auto_fine_tune));
                 ret = a_set_channel_info((CHANNEL_OPERATOR_T)channelOperator,aimSvlId,&channel_info_t,ANALOG_CHANNEL,i);
        }
        else if ( JNI_IS_INSTANCE_OF(env,jobject_channelInfo,jclass_DvbChannelInfo) )
        {
            shortName    = (jstring)   JCOM(env,jobject_channelInfo,Object, CMI(DvbChannelInfo,DvbChannelInfo_getShortName));
            if (shortName != NULL)
            {
                s_shortName = JNI_GET_STRING_UTF_CHARS(env,(jstring)shortName);
                if (s_shortName!= NULL){
                    strncpy(channel_info_t.short_name,s_shortName,MAX_DVB_SERVICE_NAME_LEN);
                }

                JNI_RELEASE_STRING_UTF_CHARS(env,(jstring)shortName,s_shortName);
            }
            JNI_DEL_LOCAL_REF(env,shortName);

            channel_info_t.ui4_freq             = (UINT32) /*(jint)    */  JCOM(env,jobject_channelInfo,Int,    CMI(DvbChannelInfo,DvbChannelInfo_getFrequency));
            channel_info_t.ui1_brdcst_medium    = (UINT8)  /*(jbyte)   */  JCOM(env,jobject_channelInfo,Int,    CMI(DvbChannelInfo,DvbChannelInfo_getBrdcstMedium)); 

            // get para from java
            channel_info_t.e_bandwidth  = (TUNER_BANDWIDTH_T) /*(jint)    */  JCOM(env,jobject_channelInfo,Int,    CMI(DvbChannelInfo,DvbChannelInfo_getBandWidth));
            channel_info_t.ui2_nw_id    = (UINT16) /*(jint)    */  JCOM(env,jobject_channelInfo,Int,    CMI(DvbChannelInfo,DvbChannelInfo_getNwId));
            channel_info_t.ui2_on_id    = (UINT16) /*(jint)    */  JCOM(env,jobject_channelInfo,Int,    CMI(DvbChannelInfo,DvbChannelInfo_getOnId));
            channel_info_t.ui2_ts_id    = (UINT16) /*(jint)    */  JCOM(env,jobject_channelInfo,Int,    CMI(DvbChannelInfo,DvbChannelInfo_getTsId));
            channel_info_t.ui2_prog_id  = (UINT16) /*(jint)    */  JCOM(env,jobject_channelInfo,Int,    CMI(DvbChannelInfo,DvbChannelInfo_getProgId));    
            channel_info_t.ui4_sym_rate = (UINT32) /*(jint)    */  JCOM(env,jobject_channelInfo,Int,    CMI(DvbChannelInfo,DvbChannelInfo_getSymRate));
            channel_info_t.e_mod        = (TUNER_MODULATION_T) /*(jint)    */  JCOM(env,jobject_channelInfo,Int,    CMI(DvbChannelInfo,DvbChannelInfo_getMod));


            ret = a_set_channel_info((CHANNEL_OPERATOR_T)channelOperator,aimSvlId,&channel_info_t,DIGITAL_CHANNEL,i);
        }

        //pass to dtv_svc_client
        if (ret != 0)
        {
            JNI_LOGE(("a_set_channel_info fail ret=%d"LOG_TAIL,ret));
        }
    }


    a_unlock_database(aimSvlId);


    if (ret == 0)
    {
        JNI_LOGI(("a_set_channel_info success ret=%d"LOG_TAIL,ret));
    }

    if (jclass_AnalogChannelInfo != NULL)
    {
        JNI_DEL_LOCAL_REF(env,jclass_AnalogChannelInfo);
    }

    return ret;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getChannelList_native
 * Signature: (ILjava/util/List;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getChannelList_1native
  (JNIEnv *env, jclass clazz, jint svlId, jobject channelList)
{
    UINT32                      i                         = 0;
    UINT32                      j                         = 0;
    UINT32                      ui4_group_size            = 0;
    UINT16                      ui2_channel_number        = 0;
    UINT16                      ui2_partial_get_len       = 0;
    UINT32                      ui4_start_idx             = 0 ;
    UINT32                      ui4_end_idx               = 0 ;
    INT32                       i4_ret                    = 0;
    CHANNEL_INFO_T*             pt_channels               = NULL;
    jclass                      jclass_AnalogChannelInfo  = NULL;
    jclass                      jclass_DvbChannelInfo     = NULL;
    jobject                     jobject_ChannelInfo       = NULL;
    jobject                     jobject_serviceName       = NULL;
    jobject                     jobject_shortName         = NULL;
    jbyteArray                  jbyteArray_privateData    = NULL;
    UINT16                      ui2_invalid_channels      = 0;

    jclass_AnalogChannelInfo = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/AnalogChannelInfo");
    jclass_DvbChannelInfo = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/model/DvbChannelInfo");

    JNI_LOGD(("Enter Java_com_mediatek_tv_service_TVNative_getChannelList_1native"LOG_TAIL));
    JNI_LOGD(("Get channel list by svlId=%d"LOG_TAIL,svlId));

    /*For Analog channel DB*/
    a_lock_database(svlId);
    i4_ret = a_get_channel_info_number((UINT16)(svlId),&ui2_channel_number);//the channel idx is from 0 to ui2_channel_number -1
    JNI_LOGI(("svlId=%d Channel number=%d"LOG_TAIL,svlId,ui2_channel_number));
    if (ui2_channel_number == (UINT16)0)
    {
        a_unlock_database(svlId);
        i4_ret = CS_OK;
        return i4_ret;
    }


    // partial get channel info
    // create channel index get list
    {
        JNI_LOGI(("/=%d mod=%d  "LOG_TAIL,
             ui2_channel_number / PARTIAL_CHANNEL_GET_LEN,
             ui2_channel_number % PARTIAL_CHANNEL_GET_LEN
             ));

        ui4_group_size = ui2_channel_number / PARTIAL_CHANNEL_GET_LEN;
        if ((ui2_channel_number % PARTIAL_CHANNEL_GET_LEN) != 0)
        {
            ui4_group_size++;
        }

        JNI_LOGI(("ui2_channel_number=%d(%d)  ui4_group_size=%d"LOG_TAIL,ui2_channel_number,PARTIAL_CHANNEL_GET_LEN,ui4_group_size));
    }


    for (j=0;j<ui4_group_size; j++)
    {
        ui4_start_idx =  j*PARTIAL_CHANNEL_GET_LEN;
        ui4_end_idx   =  j*PARTIAL_CHANNEL_GET_LEN + PARTIAL_CHANNEL_GET_LEN -1;
        if(ui4_end_idx > (ui2_channel_number - 1))
        {
            ui4_end_idx = ui2_channel_number - 1;
        }
        
        JNI_LOGI(("Get group[%d] start_idx=%d end_idx=%d\n",j, ui4_start_idx, ui4_end_idx ));
        ui2_partial_get_len = ui4_end_idx - ui4_start_idx +1;//sample groud[0] {0..199} length=200  199 - 0 + 1

        //malloc memory for channel list
        JNI_LOGI(("ui2_partial_get_len=%d malloc memory size=%d"LOG_TAIL,
             ui2_partial_get_len,
             sizeof(CHANNEL_INFO_T)* ui2_partial_get_len));
    
        pt_channels = JNI_MALLOC(sizeof(CHANNEL_INFO_T)* ui2_partial_get_len);
        if (pt_channels == NULL)
        {
            JNI_LOGE(("Memory alloc error") );
            a_unlock_database(svlId);
            return -1;
        }

        JNI_MEMSET(pt_channels,0x0,sizeof(CHANNEL_INFO_T)* ui2_partial_get_len);
        i4_ret =  a_get_channel_info((UINT16)svlId,ui2_partial_get_len,pt_channels,
                                         ui4_start_idx ,ui4_end_idx);
        
        JNI_LOGI(("{%s %d a_get_channel_info return %d}\n", __FUNCTION__, __LINE__, i4_ret));

        if (i4_ret == CS_OK)
        {
            for (i=0;i<ui2_partial_get_len;i++)
            {
                if (pt_channels[i].b_valid == FALSE)
                {
                    ui2_invalid_channels++;
                    continue;
                }

                if (pt_channels[i].e_channel_type == ANALOG_CHANNEL)
                {
                    jobject_ChannelInfo       = JNI_NEW_OBJECTV(env,jclass_AnalogChannelInfo,
                                                                CMI(AnalogChannelInfo,AnalogChannelInfo_init),
                                                                svlId,(UINT32)pt_channels[i].ui2_svl_rec_id );
                }
                else if (pt_channels[i].e_channel_type == DIGITAL_CHANNEL)
                {
                    jobject_ChannelInfo       = JNI_NEW_OBJECTV(env,jclass_DvbChannelInfo,
                                                                CMI(DvbChannelInfo,DvbChannelInfo_init),
                                                                svlId,(UINT32)pt_channels[i].ui2_svl_rec_id );
                }

                /*ChannelInfo common part end*/

                if (pt_channels[i].e_channel_type == ANALOG_CHANNEL)
                {
                    JNI_LOGD(("From MW:tvSys=%lx audioSys=%lx colorSys=%lx frequency=%lx serviceName=%s "
                         "svlId=%lx svlRecId=%lx channelId=%lx channelNumber=%lx brdcstMedium=%d ai1_private_data[0]=%d %d %d "
                         "b_no_auto_fine_tune=%lx"
                         LOG_TAIL,
                         /*jobject_channelInfo,*/
                         pt_channels[i].ui4_tv_sys,
                         pt_channels[i].ui4_audio_sys,
                         pt_channels[i].e_vid_color_sys,
                         pt_channels[i].ui4_freq,
                         pt_channels[i].ac_name,
                         (UINT32)pt_channels[i].ui2_svl_id,
                         (UINT32)pt_channels[i].ui2_svl_rec_id,
                         pt_channels[i].ui4_channel_id,
                         pt_channels[i].ui4_channel_number,
                         pt_channels[i].ui1_brdcst_medium,
                         pt_channels[i].ai1_private_data[0],
                         pt_channels[i].ai1_private_data[1],
                         pt_channels[i].ai1_private_data[2],
                         (UINT32)pt_channels[i].b_no_auto_fine_tune
                         ));

                    /*ChannelInfo common part start*/
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setChannelId),   
                          pt_channels[i].ui4_channel_id    );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setBrdcstType),   
                          pt_channels[i].ui1_brdcst_type   );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setChannelNumber),   
                          pt_channels[i].ui4_channel_number    );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setNwMask),   
                          pt_channels[i].ui4_nw_mask    );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setOptionMask),   
                          pt_channels[i].ui4_option_mask    );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setServiceType),   
                          pt_channels[i].ui4_service_type    );

                    /*Service name*/
                    if (strlen(pt_channels[i].ac_name) > 0)
                    {
                        jobject_serviceName = JNI_NEW_STRING_UTF(env,pt_channels[i].ac_name);
                        if (jobject_serviceName != NULL)
                        {
                            JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setServiceName),   
                                  jobject_serviceName                 );
                            JNI_DEL_LOCAL_REF(env,jobject_serviceName);
                        }
                    }


                    //Set private data
                    jbyteArray_privateData = JNI_NEW_BYTE_ARRAY(env,PRIVATE_DATA_LEN);
                    JNI_SET_BYTE_ARRAY_REGION(env,jbyteArray_privateData,0,PRIVATE_DATA_LEN,((jbyte*)(pt_channels[i].ai1_private_data)) );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setPrivateData),   
                          jbyteArray_privateData                   );
                    JNI_DEL_LOCAL_REF(env,jbyteArray_privateData);



                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setBrdcstMedium),
                          pt_channels[i].ui1_brdcst_medium );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setTvSys),       
                          pt_channels[i].ui4_tv_sys        );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setAudioSys),    
                          pt_channels[i].ui4_audio_sys     );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setColorSys),    
                          pt_channels[i].e_vid_color_sys   );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setFrequency),   
                          pt_channels[i].ui4_freq          );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(AnalogChannelInfo,AnalogChannelInfo_setNoAutoFineTune),   
                          pt_channels[i].b_no_auto_fine_tune  );
                }
                else if (pt_channels[i].e_channel_type == DIGITAL_CHANNEL)
                {
                    /*ChannelInfo common part start*/
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setChannelId),   
                          pt_channels[i].ui4_channel_id    );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setBrdcstType),   
                          pt_channels[i].ui1_brdcst_type   );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setBrdcstMedium),
                          pt_channels[i].ui1_brdcst_medium );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setChannelNumber),   
                          pt_channels[i].ui4_channel_number    );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setNwMask),   
                          pt_channels[i].ui4_nw_mask    );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setOptionMask),   
                          pt_channels[i].ui4_option_mask    );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setServiceType),   
                          pt_channels[i].ui4_service_type    );


                    /*Service name*/
                    if (strlen(pt_channels[i].ac_name) > 0)
                    {
                        jobject_serviceName = JNI_NEW_STRING_UTF(env,pt_channels[i].ac_name);
                        if (jobject_serviceName != NULL)
                        {
                            JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setServiceName),   
                                  jobject_serviceName                 );
                            JNI_DEL_LOCAL_REF(env,jobject_serviceName);
                        }
                    }


                    //Set private data
                    jbyteArray_privateData = JNI_NEW_BYTE_ARRAY(env,PRIVATE_DATA_LEN);
                    JNI_SET_BYTE_ARRAY_REGION(env,jbyteArray_privateData,0,PRIVATE_DATA_LEN,((jbyte*)(pt_channels[i].ai1_private_data)) );
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setPrivateData),   
                          jbyteArray_privateData                   );
                    JNI_DEL_LOCAL_REF(env,jbyteArray_privateData);



                    /*Short name*/
                    if (strlen(pt_channels[i].short_name) > 0)
                    {
                        jobject_shortName = JNI_NEW_STRING_UTF(env,pt_channels[i].short_name);
                        if (jobject_shortName != NULL)
                        {
                            JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setShortName),   
                                  jobject_shortName                 );
                            JNI_DEL_LOCAL_REF(env,jobject_shortName);
                        }
                    }

                    /*Frequency*/
                    JCOMV(env,jobject_ChannelInfo      ,Void,   CMI(DvbChannelInfo,DvbChannelInfo_setFrequency),   
                          pt_channels[i].ui4_freq    );

                }
                
                //With position
                JCOMV(env,channelList,Void,CMI(List,List_add), j * PARTIAL_CHANNEL_GET_LEN + i - (ui2_invalid_channels),jobject_ChannelInfo      );
                JNI_DEL_LOCAL_REF(env,jobject_ChannelInfo      );
            }
        }

        JNI_FREE(pt_channels);
    }

    if (jclass_AnalogChannelInfo != NULL)
    {
        JNI_DEL_LOCAL_REF(env,jclass_AnalogChannelInfo);
    }

    if (jclass_DvbChannelInfo != NULL)
    {
        JNI_DEL_LOCAL_REF(env,jclass_DvbChannelInfo);
    }


    a_unlock_database(svlId);
    return i4_ret;
}



/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    fsSyncChannelList_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_fsSyncChannelList_1native
(JNIEnv * env, jclass clazz, jint svlId)
{
    INT32   i4_ret = CS_INTER_ERROR;
    JNI_LOGI(("fsSyncChannelList by svlId=%d"LOG_TAIL,svlId));

    i4_ret = a_fs_sync_channel_list((UINT16)svlId);
    return i4_ret;
}

/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    fsStoreChannelList_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_fsStoreChannelList_1native
(JNIEnv * env, jclass clazz, jint svlId)
{
    INT32   i4_ret = CS_INTER_ERROR;
    JNI_LOGI(("fsStoreChannelList by svlId=%d"LOG_TAIL,svlId));
    i4_ret = a_fs_store_channel_list((UINT16)svlId);
    return i4_ret;
}


/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    digitalDBClean_native
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_digitalDBClean_1native
  (JNIEnv *env, jclass clazz, jint svlId)
{
    INT32   i4_ret = CS_INTER_ERROR;
    a_lock_database(svlId);

    JNI_LOGI(("digitalDBClean by svlId=%d"LOG_TAIL,svlId));
    i4_ret = a_clean_digital_db((UINT16)svlId);
    a_unlock_database(svlId);
    return i4_ret;
}


VOID x_channel_service_init(VOID)
{
    JNI_LOGD(("a_channel_set_listener listem addr=0x%x\r\n",_svl_nfy));
    a_channel_set_listener(_svl_nfy,NULL);
}

EXTERN_C_END
