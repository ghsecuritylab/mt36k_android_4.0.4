#include "dlna/EventManager.h"

#include "os/MessageQueue.h"
/*#include <memory>
#include <tr1/memory>*/

#ifdef __cplusplus
extern "C" {
#endif

#include "x_dlna_dmp_api.h"
#include <android/log.h>

    char * check_valid_utf8(char * string) {
        char * bytes = strdup(string);
        char * ret = bytes;

        while (*bytes != '\0') 
        { 
            char utf8 = *bytes; 
            switch (((utf8 >> 4) & 0xf)) { 
            case 0x00: 
            case 0x01: 
            case 0x02: 
            case 0x03: 
            case 0x04: 
            case 0x05: 
            case 0x06: 
            case 0x07:
                {                     
                    break; 
                } 
            case 0x08:
            case 0x09: 
            case 0x0a:
            case 0x0b:
            case 0x0f: 
                {                     
                    *bytes = '?';  
                    break;
                } 
            case 0x0e: {  
                utf8 = *(++bytes); 
                if ((utf8 & 0xc0) != 0x80) {                    
                    *bytes = 0x80;                     
                }                 
                       } 
            case 0x0c: 
            case 0x0d: {                 
                utf8 = *(++bytes); 
                if ((utf8 & 0xc0) != 0x80) {                   
                    *bytes = 0x80; 
                } 
            }
            } 
            bytes++; 
        }
        return ret;
    }

    /*static void utf8_free(char * string) {
        free(string);
    }

    std::tr1::shared_ptr<char> get_valid_utf8(char * string) {
        char * bytes = check_valid_utf8(string);
        std::tr1::shared_ptr<char> ptr(bytes, &utf8_free);
        return ptr;
    }*/

    static os::MessageQueue<dlna::Event*> queue;

    void dlna::EventManager::send( dlna::Event* obj)
    {
        queue.send(obj);
    }

    dlna::Event* dlna::EventManager::recv()
    {
        return queue.recv();
    }

    static jobject buildDeviceEvent(JNIEnv * env, dlna::DeviceEvent & device)
    {
        jobject event = NULL;
        int result = 0;

        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "#### Start to build  device event");

        jclass ser = env->FindClass("com/mediatek/dlna/object/MediaServer");

        //std::tr1::shared_ptr<char> name = get_valid_utf8();
        char * name = check_valid_utf8(device.getName());
        jstring serName = env->NewStringUTF((const char*)/*name.get()*/name);
        free(name);
      
        jmethodID serCon = env->GetMethodID(ser, "<init>", "(Ljava/lang/String;I)V");

        jobject dev = env->NewObject(ser, serCon, serName, device.getDevice());

        if (DLNA_DEVICE_EVENT_FOUND_DEVS == device.getEvent())
        {
            __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "!!!! build found event %s", device.getName());
            jclass foundEvent = env->FindClass("com/mediatek/dlna/FoundDeviceEvent");
            jmethodID foundCon = env->GetMethodID(foundEvent, "<init>", "(Lcom/mediatek/dlna/DLNAEventSource;Lcom/mediatek/dlna/object/DLNADevice;)V");
            event = env->NewObject(foundEvent, foundCon, device.getSource(), dev);
        }
        else if (DLNA_DEVICE_EVENT_UNAVAILABLE == device.getEvent())
        {
            __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "!!!! build leave event %s", device.getName());
            jclass leftEvent = env->FindClass("com/mediatek/dlna/LeftDeviceEvent");
            jmethodID leftCon = env->GetMethodID(leftEvent, "<init>", "(Lcom/mediatek/dlna/DLNAEventSource;Lcom/mediatek/dlna/object/DLNADevice;)V");
            event = env->NewObject(leftEvent, leftCon, device.getSource(), dev);
        } else {
            __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "#### end to build  device event unknown type");
        }

        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "#### end to build  device event");
        return event;
    }

    static jobject buildContentEvent(JNIEnv * env,  dlna::ContentEvent & content )
    {
        jobject event = NULL;
        if (DLNA_DMP_EVENT_BROWSE_OK == content.getEvent())
        {
            jclass cont = env->FindClass("com/mediatek/dlna/object/Content");

            jmethodID contentCon = env->GetMethodID(cont, "<init>",
                "(Lcom/mediatek/dlna/object/MediaServer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JIIIILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");

            jclass listClass = env->FindClass("java/util/ArrayList");
            jmethodID listCon = env->GetMethodID(listClass, "<init>", "()V");
            jobject list = env->NewObject(listClass, listCon);
            jmethodID listAdd = env->GetMethodID(listClass, "add","(Ljava/lang/Object;)Z");

            /*jclass contentType = env->FindClass("com/mediatek/dlna/object/ContentType");
            jmethodID getClass = env->GetMethodID(contentType, "getClass", "()Ljava/lang/Class;");
            jobject contentClass = env->CallObjectMethod(contentType, getClass);
            jclass classClass = env->FindClass("java/lang/Class");
            jmethodID fields = env->GetMethodID(classClass, "getEnumConstants", "()[Ljava/lang/Object;");

            jobjectArray array = (jobjectArray)env->CallObjectMethod(contentClass, fields);

            jsize length = env->GetArrayLength(array);

            __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "#### Array length is %d ", length);*/

            __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "#### Enter buildContentEvent and number is %d ", (int)content.getContent()->i4_num);

            for (int i = 0; i < content.getContent()->i4_num; i++)
            {
                __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "#### Start to build %d ", i);
                jint objType = 0;
                jfieldID typeField;
                bool isDirectory = false;
                if (x_dlna_dmp_check_object_type(content.getContent()->t_list[i], DLNA_DMP_OBJECT_TYPE_IMAGE_ITEM))
                {  
                    //objType = env->GetObjectArrayElement(array, 0);
                    //PHOTO;
                    typeField = env->GetStaticFieldID(cont, "Photo", "I");
                }
                else if (x_dlna_dmp_check_object_type(content.getContent()->t_list[i], DLNA_DMP_OBJECT_TYPE_AUDIO_ITEM) ||
                    x_dlna_dmp_check_object_type(content.getContent()->t_list[i], DLNA_DMP_OBJECT_TYPE_MUSIC_VIDEO_CLIP) ||
                    x_dlna_dmp_check_object_type(content.getContent()->t_list[i],DLNA_DMP_OBJECT_TYPE_MUSIC_TRACK))
                {
                    //AUDIO;
                    //objType = env->GetObjectArrayElement(array, 1);
                    typeField = env->GetStaticFieldID(cont, "Audio", "I");
                }
                else if (x_dlna_dmp_check_object_type(content.getContent()->t_list[i], DLNA_DMP_OBJECT_TYPE_VIDEO_ITEM) ||
                    x_dlna_dmp_check_object_type(content.getContent()->t_list[i],DLNA_DMP_OBJECT_TYPE_MOVIE))
                {
                    //VIDEO;
                    //objType = env->GetObjectArrayElement(array, 2);
                    typeField = env->GetStaticFieldID(cont, "Video", "I");
                }
                else if (x_dlna_dmp_check_object_type(content.getContent()->t_list[i], DLNA_DMP_OBJECT_TYPE_PLAYLIST_ITEM))
                {
                    //PLAYLIST;
                    //objType = env->GetObjectArrayElement(array, 3);
                    typeField = env->GetStaticFieldID(cont, "Playlist", "I");
                }
                else if (x_dlna_dmp_check_object_type(content.getContent()->t_list[i], DLNA_DMP_OBJECT_TYPE_ITEM))
                {
                    //ITEM;
                    //objType = env->GetObjectArrayElement(array, 4);
                    typeField = env->GetStaticFieldID(cont, "Item", "I");
                }
                else
                {
                    //DIRECTORY;
                    //objType = env->GetObjectArrayElement(array, 5);
                    typeField = env->GetStaticFieldID(cont, "Directory", "I");
                    isDirectory = true;
                }
                objType = env->GetStaticIntField(cont, typeField);

                __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to get Info ");

                char * id = NULL;
                x_dlna_dmp_get_object_prop_info(content.getContent()->t_list[i], DLNA_DMP_OBJECT_PROP_ID, &id);
                if (id == NULL)
                {
                    break;
                }

                char * path = NULL;
                x_dlna_dmp_get_object_prop_info(content.getContent()->t_list[i], DLNA_DMP_OBJECT_PROP_PATH, &path);
                char * title = NULL;
                x_dlna_dmp_get_object_prop_info(content.getContent()->t_list[i], DLNA_DMP_OBJECT_PROP_TITLE, &title);
                char * parentId = NULL;
                x_dlna_dmp_get_object_prop_info(content.getContent()->t_list[i], DLNA_DMP_OBJECT_PROP_PARENT_ID, &parentId);

                __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to allocate FM ");

                __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String ");

                jstring sid = NULL;
                jstring spath = NULL;
                jstring stitle = NULL;
                jstring sparentId = NULL;

                __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String id %s ", id);

                /*std::tr1::shared_ptr<char> autoId = get_valid_utf8(id);*/
                char * autoId = check_valid_utf8(id);
                sid = env->NewStringUTF((const char * )/*autoId.get()*/ autoId);
                free(autoId);
                if (path != NULL)
                {
                    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String path %s ", path);
                    /*std::tr1::shared_ptr<char> autoPath = get_valid_utf8(path);*/
                     char * autoPath = check_valid_utf8(path);
                    spath = env->NewStringUTF((const char *) /*autoPath.get()*/ autoPath);
                    free(autoPath);
                }

                if (title != NULL)
                {
                    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String title %s ", title);
                    /*std::tr1::shared_ptr<char> autoTitle = get_valid_utf8(title);*/
                     char * autoTitle = check_valid_utf8(title);
                    stitle = env->NewStringUTF((const char *) /*autoTitle.get()*/ autoTitle);
                    free(autoTitle);
                }
                if (parentId != NULL)
                {
                    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String parentId %s ", parentId);
                    /*std::tr1::shared_ptr<char> autoParentId = get_valid_utf8(parentId);*/
                    char * autoParentId = check_valid_utf8(parentId);
                    sparentId = env->NewStringUTF((const char *) /*autoParentId.get()*/autoParentId);
                    free(autoParentId);
                }


                jstring suri = NULL;
                jstring sdtcp = NULL;
                jstring smime = NULL;

                jint i4_flag = 0;
                jint i4_media_type = 0;
                jint i4_drm_type = 0;
                jlong ui8_size = 0;

                jstring sduration = NULL;
                jstring ssamplefrequency = NULL;
                jstring sbitrate = NULL;
                jstring scolordepth = NULL;
                jstring sresolution = NULL;
                jstring snraudiochannels = NULL;
                jstring sbitspersample = NULL;

                if (!isDirectory)
                {
                    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String content is file");
                    char * duration = NULL;
                    x_dlna_dmp_get_object_prop_info(content.getContent()->t_list[i], DLNA_DMP_OBJECT_PROP_RES_DURATION, &duration);
                    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String duration");
                    if (duration != NULL)
                    {
                        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String duration %s ", duration);
                        /*std::tr1::shared_ptr<char> autoDuration = get_valid_utf8(duration);*/
                        char * autoDuration = check_valid_utf8(duration);
                        sduration = env->NewStringUTF(/*(const char *) autoDuration.get()*/autoDuration);
                        free(autoDuration);
                    }

                    CHAR * ps_in = NULL;
                    x_dlna_dmp_get_object_prop_info(content.getContent()->t_list[i], DLNA_DMP_OBJECT_PROP_RES_SAMPLEFREQUENCY, &ps_in);
                    if (ps_in != NULL)
                    {
                        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String sample frequency %s ", ps_in);
                        /*std::tr1::shared_ptr<char> autoSF = get_valid_utf8(ps_in);*/
                        char * autoSF = check_valid_utf8(ps_in);
                        ssamplefrequency = env->NewStringUTF((const char *) /*autoSF.get()*/autoSF);
                        free(autoSF);
                    }
                    ps_in = NULL;
                    x_dlna_dmp_get_object_prop_info(content.getContent()->t_list[i], DLNA_DMP_OBJECT_PROP_RES_BITRATE, &ps_in);
                    if (ps_in != NULL)
                    {
                        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String bitrate %s ", ps_in);
                        /*std::tr1::shared_ptr<char> autoBitrate = get_valid_utf8(ps_in);*/
                        char * autoBitrate = check_valid_utf8(ps_in);
                        sbitrate = env->NewStringUTF((const char *) /*autoBitrate.get()*/autoBitrate);
                        free(autoBitrate);
                    }
                    ps_in = NULL;

                    x_dlna_dmp_get_object_prop_info(content.getContent()->t_list[i], DLNA_DMP_OBJECT_PROP_RES_COLOR_DEPTH, &ps_in);
                    if (ps_in != NULL)
                    {
                        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String color depth %s ", ps_in);
                        /*std::tr1::shared_ptr<char> autoCD = get_valid_utf8(ps_in);*/
                        char * autoCD = check_valid_utf8(ps_in);
                        scolordepth = env->NewStringUTF((const char *) /*autoCD.get()*/autoCD);
                        free(autoCD);
                    }
                    ps_in = NULL;

                    x_dlna_dmp_get_object_prop_info(content.getContent()->t_list[i], DLNA_DMP_OBJECT_PROP_RES_RESOLUTION, &ps_in);
                    if (ps_in != NULL)
                    {
                        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String resolution %s ", ps_in);
                        /*std::tr1::shared_ptr<char> autoRes = get_valid_utf8(ps_in);*/
                        char * autoRes = check_valid_utf8(ps_in);
                        sresolution = env->NewStringUTF((const char *)/* autoRes.get()*/autoRes);
                        free(autoRes);
                    }
                    ps_in = NULL;

                    x_dlna_dmp_get_object_prop_info(content.getContent()->t_list[i], DLNA_DMP_OBJECT_PROP_RES_NRAUDIOCHANNELS, &ps_in);
                    if (ps_in != NULL)
                    {
                        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String nr audio channels %s ", ps_in);
                        /*std::tr1::shared_ptr<char> autoNR = get_valid_utf8(ps_in);*/
                         char * autoNR = check_valid_utf8(ps_in);
                        snraudiochannels = env->NewStringUTF(/*(const char *) autoNR.get()*/autoNR);
                        free(autoNR);
                    }
                    ps_in = NULL;
                    x_dlna_dmp_get_object_prop_info(content.getContent()->t_list[i], DLNA_DMP_OBJECT_PROP_RES_BITS_PER_SAMPLE, &ps_in);
                    if (ps_in != NULL)
                    {
                        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String bits per sample %s ", ps_in);
                        /*std::tr1::shared_ptr<char> autoBPS = get_valid_utf8(ps_in);*/
                        char * autoBPS = check_valid_utf8(ps_in);
                        sbitspersample = env->NewStringUTF((const char *) /*autoBPS.get()*/autoBPS);
                        free(autoBPS);
                    }
                    ps_in = NULL;

                    __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to allocate the fm ");

                    DLNA_DMP_OBJECT_FM_T * pt_fm = NULL;
                    x_dlna_dmp_fm_alloc_info(content.getContent()->t_list[i], &pt_fm);
                    if (pt_fm == NULL)
                    {
                        break;
                    }

                    i4_flag = (jint)pt_fm->i4_flag;
                    i4_media_type = (jint)pt_fm->i4_media_type;
                    i4_drm_type = (jint)pt_fm->i4_drm_type;
                    ui8_size = (jlong)pt_fm->ui8_size;

                    if (pt_fm->ps_uri != NULL)
                    {
                        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String pt_fm->ps_uri %s ", pt_fm->ps_uri);
                        /*std::tr1::shared_ptr<char> autoUri = get_valid_utf8(pt_fm->ps_uri);*/
                        char * autoUri = check_valid_utf8(pt_fm->ps_uri);
                        suri = env->NewStringUTF((const char *) /*autoUri.get()*/autoUri);
                        free(autoUri);
                    }
                    if (pt_fm->ps_dtcp_info != NULL)
                    {
                        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String pt_fm->ps_dtcp_info %s ", pt_fm->ps_dtcp_info);
                        /*std::tr1::shared_ptr<char> autoDtcp = get_valid_utf8(pt_fm->ps_dtcp_info);*/
                        char * autoDtcp = check_valid_utf8(pt_fm->ps_dtcp_info);
                        sdtcp = env->NewStringUTF((const char *) /*autoDtcp.get()*/autoDtcp);
                        free(autoDtcp);
                    }

                    if (pt_fm->ps_mime != NULL)
                    {
                        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to build String pt_fm->ps_mime %s ", pt_fm->ps_mime);
                        /*std::tr1::shared_ptr<char> autoMime = get_valid_utf8(pt_fm->ps_mime);*/
                        char * autoMime = check_valid_utf8(pt_fm->ps_mime);
                        smime = env->NewStringUTF((const char *) /*autoMime.get()*/autoMime);
                        free(autoMime);
                    }
                    x_dlna_dmp_fm_free_info(&pt_fm);
                }
                __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to New object ");

                /* String id, String path, String title, String resUri,  String resDuration, String parentId, String mimeType, String dtcpInfo, long size, int flag, int mediaType, int drmType */
                jobject conObj = env->NewObject(cont, contentCon, content.getSource(),
                    sid, spath, stitle, suri, sduration, sparentId, smime, sdtcp, (jlong)ui8_size,
                    (jint)i4_flag, (jint)i4_media_type, (jint)i4_drm_type, objType,          
                    ssamplefrequency, sbitrate, scolordepth, sresolution, snraudiochannels, sbitspersample);

                __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Add object to list ");

                /*jboolean b =*/ env->CallBooleanMethod(list,listAdd, conObj);
            }

            __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Start to new  Event ");

            jclass normalEvent = env->FindClass("com/mediatek/dlna/object/NormalContentEvent");
            jmethodID normalCon = env->GetMethodID(normalEvent, "<init>", "(Lcom/mediatek/dlna/object/MediaServer;Ljava/lang/String;IIIILjava/util/ArrayList;)V");
            /* MediaServer server, String id, int startIndex, int requestCount, int totalMatches, int updateId, ArrayList<Content> list */
            /*std::tr1::shared_ptr<char> autoContent = get_valid_utf8(content.getContent()->ps_object_id);*/
            char * autoContent = check_valid_utf8(content.getContent()->ps_object_id);
            jstring sid = env->NewStringUTF((const char*)/*autoContent.get()*/autoContent);
            free(autoContent);

            __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  New  Event ");

            event = env->NewObject(normalEvent, normalCon, content.getSource(), sid,
                (jint) content.getContent()->i4_start_index,
                (jint) content.getContent()->i4_request_cnt,
                (jint) content.getContent()->ui4_total_matches,
                (jint) content.getContent()->i4_update_id,
                list);

            
        }
        else if( DLNA_DMP_EVENT_BROWSE_FAILED == content.getEvent())
        {
            jclass failedEvent = env->FindClass("com/mediatek/dlna/object/FailedContentEvent");
            jmethodID failedCon = env->GetMethodID(failedEvent, "<init>", "(Lcom/mediatek/dlna/object/MediaServer;)V");
            event = env->NewObject(failedEvent, failedCon, content.getSource());
        }

        env->DeleteGlobalRef(content.getSource());

        __android_log_print(ANDROID_LOG_DEBUG, "DmpJni", "####  Return  Event ");
        return event;
    }

    jobject dlna::EventManager::build(JNIEnv * env, dlna::Event * event)
    {
        jobject obj = NULL;
        if (event->getType() == dlna::ContentEvent::ContentEventType)
        {
            dlna::ContentEvent * ce = (dlna::ContentEvent *)event;
            obj =  buildContentEvent(env, *ce);
        }

        if (event->getType() == dlna::DeviceEvent::DeviceEventType)
        {
            dlna::DeviceEvent * de = (dlna::DeviceEvent *)event;
            obj = buildDeviceEvent(env, *de);
        }        

        delete event;

        return obj;
    }

#ifdef __cplusplus
}
#endif