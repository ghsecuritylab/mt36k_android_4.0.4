#include <stdlib.h>
#include <stdint.h>
#include <jni.h>
#include <JNIHelp.h>

#include <android/log.h>
#include "IMtkPb_Ctrl_DTV.h"
#include "IMtkPb_ErrorCode.h"

#include "os/Thread.h"
#include "os/Mutex.h"
#include "os/MessageQueue.h"
#include "os/ScopedMutex.h"
#include "os/Semaphore.h"
#include <list>
#include <map>
#include <memory>
#include <stdint.h>

#include <utils/Log.h>
#include <errno.h>
#include <fcntl.h>
#include <signal.h>
#include <unistd.h>
#include <cutils/properties.h>
#include <libsonivox/eas.h>
//#include <utils/Vector.h>
#include <vector>

#define LOG_TAG "MtkMediaPlayer-JNI"


typedef size_t          SIZE_T;
typedef unsigned short  UTF16_T;
typedef unsigned int    UINT32;

extern JavaVM *g__JavaVM;

#define NORMAL  "CmpbJni"

#ifdef DEBUG_LOG
#define MMP_LOG( fmt... )\
    {\
      __android_log_print( ANDROID_LOG_DEBUG, NORMAL, fmt );\
    }
#else
#define MMP_LOG( fmt... )
#endif

#define ENABLE_PB_IO_LOG 0


#define MAKE_BIT_MASK_32(_val) (((uint32_t) 1) << (_val))

#define SBTL_ATTR_NUM      (12)

//Subtitle attribute type
#define DEFAULTTYPE  MAKE_BIT_MASK_32(0)
#define DISPLAYMODE  MAKE_BIT_MASK_32(1)
#define HILTSTYLE    MAKE_BIT_MASK_32(2)
#define TIMEOFFSET   MAKE_BIT_MASK_32(3)
#define FONTENC      MAKE_BIT_MASK_32(4)
#define SHOWHIDE     MAKE_BIT_MASK_32(5)
#define FONTINFO     MAKE_BIT_MASK_32(6)
#define BGCOLOR      MAKE_BIT_MASK_32(7)
#define TEXTCOLOR    MAKE_BIT_MASK_32(8)
#define BODERTYPE    MAKE_BIT_MASK_32(9)
#define BORDERWIDTH  MAKE_BIT_MASK_32(10)
#define ROLLTYPE     MAKE_BIT_MASK_32(11)
#define DISPLAYRECT  MAKE_BIT_MASK_32(12)

//font style
#define FNT_STYLE_REGULAR       (0)
#define FNT_STYLE_ITALIC        (1)
#define FNT_STYLE_BOLD          (2)
#define FNT_STYLE_UNDERLINE     (3)
#define FNT_STYLE_STRIKEOUT     (4)
#define FNT_STYLE_OUTLINE       (5)
#define FNT_STYLE_SHADOW_RIGHT  (6)
#define FNT_STYLE_SHADOW_LEFT   (7)
#define FNT_STYLE_DEPRESSED     (8)
#define FNT_STYLE_RAISED        (9)
#define FNT_STYLE_UNIFORM       (10)
#define FNT_STYLE_BLURRED       (11)

//font encoding
#define CMAP_ENC_NONE           (0)
#define CMAP_ENC_MS_SYMBOL      (1)
#define CMAP_ENC_UNICODE        (2)
#define CMAP_ENC_SJIS           (3)
#define CMAP_ENC_GB2312         (4)
#define CMAP_ENC_BIG5           (5)
#define CMAP_ENC_WANSUNG        (6)
#define CMAP_ENC_JOHAB          (7)
#define CMAP_ENC_ADOBE_STANDARD (8)
#define CMAP_ENC_ADOBE_EXPERT   (9)
#define CMAP_ENC_ADOBE_CUSTOM   (10)
#define CMAP_ENC_ADOBE_LATIN_1  (11)
#define CMAP_ENC_OLD_LATIN_2    (12)
#define CMAP_ENC_APPLE_ROMAN    (13)

//meta Type
#define META_TYPE_INVAL        (0)
#define META_TYPE_TITLE        (1)
#define META_TYPE_DIRECTOR     (2)
#define META_TYPE_COPYRIGHT    (3)
#define META_TYPE_YEAR         (4)
#define META_TYPE_DATE         (5)
#define META_TYPE_GENRE        (6)
#define META_TYPE_DURATION     (7)
#define META_TYPE_SIZE         (8)
#define META_TYPE_ARTIST       (9)
#define META_TYPE_ALBUM        (10)
#define META_TYPE_BITRATE      (11)
#define META_TYPE_PROTECT      (12)
#define META_TYPE_CREATE_TIME  (13)
#define META_TYPE_ACCESS_TIME  (14)
#define META_TYPE_MODIFY_TIME  (15)
#define META_TYPE_RESOLUTION   (16)
#define META_TYPE_NEXT_TITLE   (17)
#define META_TYPE_NEXT_ARTIST  (18)
#define META_TYPE_FRAME_RATE   (19)

//thumbNailColormode
#define THUMBNAIL_COLORMODE_AYUV_CLUT2    (0)
#define THUMBNAIL_COLORMODE_AYUV_CLUT4    (1)
#define THUMBNAIL_COLORMODE_AYUV_CLUT8    (2)
#define THUMBNAIL_COLORMODE_UYVY_16       (3)
#define THUMBNAIL_COLORMODE_YUYV_16       (4)
#define THUMBNAIL_COLORMODE_AYUV_D8888    (5)
#define THUMBNAIL_COLORMODE_ARGB_CLUT2    (6)
#define THUMBNAIL_COLORMODE_ARGB_CLUT4    (7)
#define THUMBNAIL_COLORMODE_ARGB_CLUT8    (8)
#define THUMBNAIL_COLORMODE_RGB_D565      (9)
#define THUMBNAIL_COLORMODE_ARGB_D1555    (10)
#define THUMBNAIL_COLORMODE_ARGB_D4444    (11)
#define THUMBNAIL_COLORMODE_ARGB_D8888    (12)
#define THUMBNAIL_COLORMODE_YUV_420_BLK   (13)
#define THUMBNAIL_COLORMODE_YUV_420_RS    (14)
#define THUMBNAIL_COLORMODE_YUV_422_BLK   (15)
#define THUMBNAIL_COLORMODE_YUV_422_RS    (16)
#define THUMBNAIL_COLORMODE_YUV_444_BLK   (17)
#define THUMBNAIL_COLORMODE_YUV_444_RS    (18)

//PbCallBack Event
#define EVENT_UNKNOWN            ((uint8_t) (0))
#define EVENT_ERROR              ((uint8_t) (1))
#define EVENT_UPDATE             ((uint8_t) (2))
#define EVENT_COMPLETION         ((uint8_t) (3))
#define EVENT_PLAYDONE           ((uint8_t) (4))
#define EVENT_SEEKCOMPLETE       ((uint8_t) (5))
#define EVENT_CUR_TIME_UPDATE    ((uint8_t) (6))
#define EVENT_TOTAL_TIME_UPDATE  ((uint8_t) (7))
#define EVENT_STEP_DONE          ((uint8_t) (8))
#define EVENT_EOF                ((uint8_t) (9))
#define EVENT_POSITION_UPDATE    ((uint8_t) (10))
#define EVENT_SPEED_UPDATE       ((uint8_t) (11))
#define EVENT_PREPARED           ((uint8_t) (12))
#define EVENT_REPLAY             ((uint8_t) (13))
#define EVENT_AUDIO_ONLY_SERVICE ((uint8_t) (14))


//mediaType
#define MEDIA_TYPE_PCM              (10)

//positionType
#define POSITION_TYPE_MILLISECOND   (1)
#define POSITION_TYPE_POSITION      (2)

//mediaPlayerMode
#define MEDIAPLAYER_MODE_URI        (1)
#define MEDIAPLAYER_MODE_PULL       (2)

//prepareType
#define PREPARE_TYPE_SYNC           (1)
#define PREPARE_TYPE_ASYNC          (2)

//seekEnable
#define SEEK_BUSY                   (-12)

#define URI_BUF_SIZE         (4 * 1024 * 1024)
#define MAX_NUM_HANDLES      ((unsigned short) 4096)
#define SYS_MEM_SIZE         ((unsigned int) 12 * 1024 * 1024)

    typedef struct _THREAD_DESCR_T
    {
        unsigned int    z_stack_size;

        unsigned char   ui1_priority;

        unsigned short  ui2_num_msgs;
    }   THREAD_DESCR_T;

    typedef struct _GEN_CONFIG_T
    {
        unsigned short  ui2_version;

        void*           pv_config;

        unsigned int    z_config_size;

        THREAD_DESCR_T  t_mheg5_thread;
    }   GEN_CONFIG_T;

    class INPUTSTREAM_ENV_T
    {
    public:
        INPUTSTREAM_ENV_T()
        {
        }
        JavaVM*          vm;
        jclass           player;
        jobject          thiz;
        IMTK_PB_HANDLE_T hHandle;
        os::Mutex        m_mutexInputStrem;
    };


    struct CMPB_HANDLE_INFO
    {
        int handle;
    };

    struct CMPBSetEngineParamEvent
    {
        os::Semaphore*     sema;
        INPUTSTREAM_ENV_T* envInfo;
        int                errorCode;
        int                mediaPlayerMode;
        int                prepareType;
        uint8_t*           path;
        bool               fgIsAudioPlayer;
    };

    extern "C" int c_rpc_init_client(void);
    extern "C" int c_rpc_start_client(void);

    extern "C" int os_init(const void *pv_addr, unsigned int z_size);
    extern "C" int handle_init (unsigned short   ui2_num_handles,
                                void**   ppv_mem_addr,
                                unsigned int*  pz_mem_size);
    extern "C" int x_rtos_init (GEN_CONFIG_T*  pt_config);

    static int initialize(void)
    {
        GEN_CONFIG_T  t_rtos_config = {0};
        void*         pv_mem_addr = 0;
        unsigned int  z_mem_size = 0xc00000;
        int           ret = 0;

        ret = x_rtos_init (&t_rtos_config);
        if (ret != 0)
        {
            return ret;
        }


        ret = handle_init (MAX_NUM_HANDLES, &pv_mem_addr, &z_mem_size);

        if (ret != 0)
        {
            return ret;
        }

        ret = os_init (pv_mem_addr, z_mem_size);

        if (ret != 0)
        {
            return ret;
        }

        ret = c_rpc_init_client();

        if (ret != 0)
        {
            return ret;
        }
        ret = c_rpc_start_client();
        return ret;
    }

namespace MTK {

struct fields_t {
    jfieldID    context;
    jmethodID   post_event;
    // the following for dealing stream
    jmethodID   read_stream;
    jfieldID    streamReadBuffer;
    jfieldID    streamSize;
    jfieldID    streamCurPos;
    jmethodID   seek_stream;
    jfieldID    streamSeekable;
    jfieldID    isAudioPlayer;
};
static fields_t s_MpJniFields;

}; // end namespace MTK

class CMPBSetEngineParamThread : public os::Thread{

    public:
        void Run();
    public:
        CMPBSetEngineParamThread();
        ~CMPBSetEngineParamThread();
    private:
        bool running;
};

static CMPBSetEngineParamThread setEngineThread;

static os::MessageQueue<CMPBSetEngineParamEvent*> engineQueue;

CMPBSetEngineParamThread::CMPBSetEngineParamThread() {
    running = false;
}

CMPBSetEngineParamThread::~CMPBSetEngineParamThread() {
    engineQueue.send(NULL);
    if(running)
    {
        Wait();
    }
}

typedef int (*SyncToAsyncRead)(void * handle, void * buffer, uint32_t size,
    uint32_t * out, IMTK_PULL_HANDLE_T hPullSrc, uint64_t u8ReadBeginPos);


class CommonFileSystemAction{
    public:
        void*                    handle;
        SyncToAsyncRead          read;
        char*                    buffer;
        int                      size;
        IMtkPb_Ctrl_Pull_Nfy_Fct listener;
        void*                    tag;
        int                      status;
        IMTK_PULL_HANDLE_T       hPullSrc;
        INPUTSTREAM_ENV_T *      m_ptrOwnerID;
        bool                     m_fgValid;
        uint64_t                 m_u8ReadBeginPos;


    public:
        static const int         Running = 0;
        static const int         Abort   = 1;
    };


class SyncToAsync : public os::Thread{
    private:
        static os::Mutex global;
    public:
        static SyncToAsync & getInstance();
        void addAction(CommonFileSystemAction * action);
        void abortAction(CommonFileSystemAction * action);

    public:
        void Run();
    private:
        SyncToAsync();
        ~SyncToAsync();
    private:
        os::Mutex mutex;
        os::MessageQueue<CommonFileSystemAction *> m_qActions;
        std::list<CommonFileSystemAction*> list;
    private:
        void deleteAction(CommonFileSystemAction * action);
    };

os::Mutex SyncToAsync::global;


SyncToAsync & SyncToAsync::getInstance()
{
    os::ScopedMutex lock(global);
    static SyncToAsync instance;
    return instance;
}

SyncToAsync::SyncToAsync()
{
    LOGI("SyncToAsync::SyncToAsync(): Enter");

    Start();

    LOGI("SyncToAsync::SyncToAsync(): Leave");
}

SyncToAsync::~SyncToAsync()
{
    LOGI("SyncToAsync::~SyncToAsync(): Enter");

    m_qActions.send(NULL);
    Wait();

    LOGI("SyncToAsync::~SyncToAsync(): Leave");
}

void SyncToAsync::addAction(CommonFileSystemAction * action)
{
    os::ScopedMutex scoped(mutex);

    if (!(action->m_fgValid))
    {
        LOGI("SyncToAsync::addAction(): action is not valid, return directly");
    }
    list.push_back(action);

    //LOGI("SyncToAsync::addAction(): action--prMtkMpContext = %d", (int)action->m_ptrOwnerID);

    m_qActions.send(action);
}

void SyncToAsync::abortAction(CommonFileSystemAction * action)
{
    os::ScopedMutex scoped(mutex);

    if (!(action->m_fgValid))
    {
        LOGI("SyncToAsync::abortAction(): action is not valid, return directly");
    }

    std::list<CommonFileSystemAction*>::iterator it;
    for (it = list.begin();
        it != list.end();
        it++)
    {
        if (action == (*it))
        {
            action->status = CommonFileSystemAction::Abort;
            break;
        }
    }

}

void SyncToAsync::deleteAction(CommonFileSystemAction * action)
{
    os::ScopedMutex scoped(mutex);
    if (action)
    {
        list.remove(action);
        delete action;
        action = NULL;
    }
}

void SyncToAsync::Run()
{
    CommonFileSystemAction * action;

    while ((action = m_qActions.recv()) != NULL)
    {
        os::ScopedMutex scoped(mutex);

        if (!(action->m_fgValid))
        {
            LOGI("SyncToAsync::Run(): action has been not valid");
            deleteAction(action);
            continue;
        }

        if (action->status == CommonFileSystemAction::Abort)
        {
            action->listener(IMTK_PB_CTRL_PULL_ABORT_OK,action->tag, (uint32_t)action, 0);
            LOGI("SyncToAsync::Run(): action has been aborted");
            deleteAction(action);
            continue;
        }

        uint32_t length = 0;
        int ret = action->read(action->handle, action->buffer, action->size, &length, action->hPullSrc, action->m_u8ReadBeginPos);

        if (ret == IMTK_PB_CB_ERROR_CODE_EOF)
        {
            LOGI("SyncToAsync::Run(), IMTK_PB_CTRL_PULL_READ_EOS\n");
            action->listener(IMTK_PB_CTRL_PULL_READ_EOS, action->tag, (uint32_t)action, length);
        }
        else if (ret == IMTK_PB_CB_ERROR_CODE_OK)
        {
            action->listener(IMTK_PB_CTRL_PULL_READ_OK, action->tag, (uint32_t)action, length);
        }
        else if (ret == IMTK_PB_CB_ERROR_CODE_NOT_OK)
        {
            LOGE("SyncToAsync::Run(), IMTK_PB_CTRL_PULL_FAIL\n");
            action->listener(IMTK_PB_CTRL_PULL_FAIL, action->tag, (uint32_t)action, length);
        } else {
            //error happen
            LOGE("SyncToAsync::Run(), Error happen!!\n");
            action->listener(IMTK_PB_CTRL_PULL_FAIL, action->tag, (uint32_t)action, length);
        }

        deleteAction(action);

    }

}

#ifdef __cplusplus
extern "C" {
#endif


/*
@return:
IMTK_PB_CB_ERROR_CODE_OK           : the number of bytes actually read (>=0)
IMTK_PB_CB_ERROR_CODE_EOF         : if the end of the stream has been reached
IMTK_PB_CB_ERROR_CODE_NOT_OK   : exception happened
 */
int pullPlayerSyncToAsyncRead(void * handle , void * pu1DstBuf, uint32_t u4Count,
                        uint32_t * pu4Read, IMTK_PULL_HANDLE_T hPullSrc, uint64_t u8ReadBeginPos)
{
    INPUTSTREAM_ENV_T*  prMtkMpContext = (INPUTSTREAM_ENV_T*) handle;

#if ENABLE_PB_IO_LOG
    LOGI("pullPlayerSyncToAsyncRead(): Enter, player instance = %d, hPullSrc=%d, pu1DstBuf = %x, u4Count=%d, u8ReadBeginPos=%lld\n",
        (int)prMtkMpContext, (int)hPullSrc, (uint32_t)pu1DstBuf, u4Count, u8ReadBeginPos);
#endif

    // test memory
    if (!pu1DstBuf)
    {
        LOGI("pullPlayerSyncToAsyncRead(): player instance = %d, hPullSrc=%d, pu1DstBuf==null, error happen!",
            (int)prMtkMpContext, (int)hPullSrc);

        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

    memset(pu1DstBuf, 0, u4Count);

    os::ScopedMutex scoped(prMtkMpContext->m_mutexInputStrem);

    JNIEnv*             env;
    jint                ret;
    jbyteArray          array;
    jbyte*              buffer = 0;
    jint                result ;
    jlong  lCurPos;

    uint64_t u8FinalReadBeginPos = u8ReadBeginPos;

    *pu4Read = 0;
    ret = ((prMtkMpContext->vm))->AttachCurrentThread((JNIEnv**)&env, NULL);

    if (u8ReadBeginPos == 0x7fffffffffffffff)
    {
#if ENABLE_PB_IO_LOG
        LOGI("pullPlayerSyncToAsyncRead(): player instance = %d, hPullSrc=%d, u8ReadBeginPos == 0x7fffffffffffffff", (int)prMtkMpContext, (int)hPullSrc);
#endif
        lCurPos = env->GetLongField(prMtkMpContext->thiz, MTK::s_MpJniFields.streamCurPos);
        u8FinalReadBeginPos = lCurPos;
    }

    // seek
    if (u8ReadBeginPos != 0x7fffffffffffffff)
    {
        result = env->CallLongMethod(prMtkMpContext->thiz, MTK::s_MpJniFields.seek_stream, 0, u8FinalReadBeginPos);

        if (result == -1)
        {
            *pu4Read = 0;
            ((prMtkMpContext->vm))->DetachCurrentThread();

            LOGI("pullPlayerSyncToAsyncRead(): player instance = %d, hPullSrc=%d, seek result == -1", (int)prMtkMpContext, (int)hPullSrc);

            return IMTK_PB_CB_ERROR_CODE_EOF;
        }
    }

    result = env->CallIntMethod(prMtkMpContext->thiz, MTK::s_MpJniFields.read_stream, u4Count);

#if ENABLE_PB_IO_LOG
    LOGI("pullPlayerSyncToAsyncRead(): player instance = %d, hPullSrc=%d, read result == %d, require u4Count=%d",
        (int)prMtkMpContext, (int)hPullSrc, result, u4Count);
#endif

    if (result > u4Count)
    {
        *pu4Read = 0;
        ((prMtkMpContext->vm))->DetachCurrentThread();
        LOGI("pullPlayerSyncToAsyncRead(): player instance = %d, hPullSrc=%d, result > u4Count, error", (int)prMtkMpContext, (int)hPullSrc);

        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

    if (result == 0)
    {
        *pu4Read = 0;
        ((prMtkMpContext->vm))->DetachCurrentThread();
        LOGI("pullPlayerSyncToAsyncRead(): player instance = %d, hPullSrc=%d, just return IMTK_PB_CB_ERROR_CODE_EOF!",
            (int)prMtkMpContext, (int)hPullSrc);

        return IMTK_PB_CB_ERROR_CODE_EOF;
    }
    else if (result < 0)
    {
        *pu4Read = 0;
        ((prMtkMpContext->vm))->DetachCurrentThread();

        LOGI("pullPlayerSyncToAsyncRead(): player instance = %d, hPullSrc=%d, just return IMTK_PB_CB_ERROR_CODE_NOT_OK!",
            (int)prMtkMpContext, (int)hPullSrc);

        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

    array = (jbyteArray)env->GetObjectField( prMtkMpContext->thiz, MTK::s_MpJniFields.streamReadBuffer);
    buffer = env->GetByteArrayElements( array, NULL);

#if ENABLE_PB_IO_LOG
            LOGI("pullPlayerSyncToAsyncRead(): player instance = %d, hPullSrc=%d, begin memset: u4Count=%d, pu1DstBuf = %x",
                (int)prMtkMpContext, (int)hPullSrc, u4Count, (uint32_t)pu1DstBuf);
#endif
    memset(pu1DstBuf, 0, u4Count);
#if ENABLE_PB_IO_LOG
            LOGI("pullPlayerSyncToAsyncRead(): player instance = %d, hPullSrc=%d, end memset: u4Count=%d, pu1DstBuf = %x",
                (int)prMtkMpContext, (int)hPullSrc, u4Count, (uint32_t)pu1DstBuf);
#endif


#if ENABLE_PB_IO_LOG
    LOGI("pullPlayerSyncToAsyncRead(): player instance = %d, hPullSrc=%d, memset, success", (int)prMtkMpContext, (int)hPullSrc);
#endif

    if (buffer)
    {
#if ENABLE_PB_IO_LOG
        LOGI("pullPlayerSyncToAsyncRead(): player instance = %d, hPullSrc=%d, begin memcopy: u4Count=%d, pu1DstBuf = %x, buffer = %x",
            (int)prMtkMpContext, (int)hPullSrc, u4Count, (uint32_t)pu1DstBuf, (uint32_t)buffer);
#endif

        memcpy(pu1DstBuf, buffer, result);

#if ENABLE_PB_IO_LOG
        LOGI("pullPlayerSyncToAsyncRead(): player instance = %d, hPullSrc=%d, end memcopy: u4Count=%d, pu1DstBuf = %x, buffer = %x",
            (int)prMtkMpContext, (int)hPullSrc, u4Count, (uint32_t)pu1DstBuf, (uint32_t)buffer);
#endif
    }
    else
    {
#if ENABLE_PB_IO_LOG
        LOGI("pullPlayerSyncToAsyncRead(): player instance = %d, hPullSrc=%d, buffer==null !!!!!!",
            (int)prMtkMpContext, (int)hPullSrc);
#endif
    }

    env->ReleaseByteArrayElements(array, buffer, 0);

    ((prMtkMpContext->vm))->DetachCurrentThread();

    *pu4Read = result;

#if ENABLE_PB_IO_LOG
    LOGI("pullPlayerSyncToAsyncRead(): Leave, player instance = %d, hPullSrc=%d\n", (int)prMtkMpContext, (int)hPullSrc);
#endif
    return IMTK_PB_CB_ERROR_CODE_OK;
}


    IMTK_PB_CB_ERROR_CODE_T PullOpen(IMTK_PB_HANDLE_T           hHandle,
                                        IMTK_PULL_HANDLE_T*     phPullSrc,
                                        void*                   pvAppTag)
    {
    INPUTSTREAM_ENV_T* prMtkMpContext = (INPUTSTREAM_ENV_T*) pvAppTag;

    LOGI("PullOpen(): Enter, player instance = %d\n", (int)prMtkMpContext);

        CMPB_HANDLE_INFO * info = new CMPB_HANDLE_INFO;
        info->handle = (int)info;
        *phPullSrc = info->handle;

    LOGI("PullOpen(): player instance = %d, *phPullSrc=%d, info=%d, info->handle=%d",
        (int)prMtkMpContext, (int)*phPullSrc, (int)info, info->handle);

    LOGI("PullOpen(): Leave, player instance = %d, *phPullSrc=%d\n", (int)prMtkMpContext, (int)*phPullSrc);

        return IMTK_PB_CB_ERROR_CODE_OK;
    }


    IMTK_PB_CB_ERROR_CODE_T PullClose(IMTK_PULL_HANDLE_T         hPullSrc,
                                        void*                    pvAppTag)
    {
    INPUTSTREAM_ENV_T* prMtkMpContext = (INPUTSTREAM_ENV_T*) pvAppTag;

    LOGI("PullClose(): Enter, player instance = %d, hPullSrc=%d\n", (int)prMtkMpContext, (int)hPullSrc);

        CMPB_HANDLE_INFO * info = (CMPB_HANDLE_INFO *)hPullSrc;

    LOGI("PullClose(): player instance = %d, hPullSrc = %d, info = %d, info->handle = %d\n",
        (int)prMtkMpContext, (int)hPullSrc, (int)info, info->handle);

        delete info;

    LOGI("PullClose(): Leave, player instance = %d, hPullSrc=%d\n", (int)prMtkMpContext, (int)hPullSrc);

        return IMTK_PB_CB_ERROR_CODE_OK;
    }


    IMTK_PB_CB_ERROR_CODE_T PullRead(IMTK_PULL_HANDLE_T     hPullSrc,
                                      void*                 pvAppTag,
                                      uint8_t*              pu1DstBuf,
                                      uint32_t              u4Count,
                                      uint32_t*             pu4Read)
    {

    INPUTSTREAM_ENV_T* prMtkMpContext = (INPUTSTREAM_ENV_T*) pvAppTag;

#if ENABLE_PB_IO_LOG
    LOGI("PullRead(): Enter, player instance = %d, hPullSrc=%d\n", (int)prMtkMpContext, (int)hPullSrc);
#endif
        jint ret = 0;

        ret = pullPlayerSyncToAsyncRead(pvAppTag, pu1DstBuf, u4Count, pu4Read, hPullSrc, 0x7fffffffffffffff);
        if (ret != IMTK_PB_CB_ERROR_CODE_OK)
        {
        LOGI("PullRead(): ret value of pullPlayerSyncToAsyncRead() is IMTK_PB_CB_ERROR_CODE_EOF or IMTK_PB_CB_ERROR_CODE_NOT_OK\n");
        }
#if ENABLE_PB_IO_LOG
    LOGI("PullRead(): Leave, player instance = %d, hPullSrc=%d\n", (int)prMtkMpContext, (int)hPullSrc);
#endif
        return (IMTK_PB_CB_ERROR_CODE_T)ret;

    }


    IMTK_PB_CB_ERROR_CODE_T PullReadAsync(IMTK_PULL_HANDLE_T          hPullSrc,
                                           void*                      pvAppTag,
                                           uint8_t*                   pu1Dst,
                                           uint32_t                   u4DataLen,
                                           IMtkPb_Ctrl_Pull_Nfy_Fct   pfnNotify,
                                           void*                      pvRdAsyTag,
                                           uint32_t*                  pu4ReqId)
    {
        INPUTSTREAM_ENV_T* prMtkMpContext = (INPUTSTREAM_ENV_T*) pvAppTag;
#if ENABLE_PB_IO_LOG
    LOGI("PullReadAsync(): Enter, player instance = %d, hPullSrc=%d", (int)prMtkMpContext, (int)hPullSrc);
#endif
    if (!pu1Dst)
        {
        LOGI("PullReadAsync(): Enter, player instance = %d, hPullSrc=%d, pu1Dst==null", (int)prMtkMpContext, (int)hPullSrc);
        }

    memset(pu1Dst, 0, u4DataLen);

        JNIEnv* env;
        jint ret = 0;
        jlong  lCurPos;
        {
            os::ScopedMutex scoped(prMtkMpContext->m_mutexInputStrem);

        ret = ((prMtkMpContext->vm))->AttachCurrentThread((JNIEnv**)&env, NULL);

        lCurPos = env->GetLongField(prMtkMpContext->thiz, MTK::s_MpJniFields.streamCurPos);

        ((prMtkMpContext->vm))->DetachCurrentThread();
        }

        CommonFileSystemAction* action = new CommonFileSystemAction;
        action->handle = pvAppTag;
        action->buffer = (char*)pu1Dst;
        action->size   = u4DataLen;
        action->listener = pfnNotify;
        action->read = pullPlayerSyncToAsyncRead;
        action->status = CommonFileSystemAction::Running;
        action->tag = pvRdAsyTag;
        action->hPullSrc = hPullSrc;
        action->m_ptrOwnerID = prMtkMpContext;
        action->m_fgValid = true;
        action->m_u8ReadBeginPos = lCurPos;
        *pu4ReqId = (uint32_t)action;

#if ENABLE_PB_IO_LOG
    LOGI("PullReadAsync(): prMtkMpContext = %d, hPullSrc=%d, pu1Dst=%x, u4DataLen=%d, *pu4ReqId=%d", (int)prMtkMpContext, (int)hPullSrc, (uint32_t)pu1Dst, u4DataLen, (uint32_t)action);
#endif
        SyncToAsync::getInstance().addAction(action);

#if ENABLE_PB_IO_LOG
    LOGI("PullReadAsync(): Leave, player instance = %d, hPullSrc=%d", (int)prMtkMpContext, (int)hPullSrc);
#endif
        return IMTK_PB_CB_ERROR_CODE_OK;
    }


    IMTK_PB_CB_ERROR_CODE_T PullAbortReadAsync(IMTK_PULL_HANDLE_T     hPullSrc,
                                                     void*                  pvAppTag,
                                                     uint32_t               u4ReqId)
    {
    INPUTSTREAM_ENV_T* prMtkMpContext = (INPUTSTREAM_ENV_T*) pvAppTag;

#if ENABLE_PB_IO_LOG
    LOGI("PullAbortReadAsync(): Enter, player instance = %d, hPullSrc=%d, u4ReqId=%d", (int)prMtkMpContext, (int)hPullSrc, u4ReqId);
#endif
        SyncToAsync::getInstance().abortAction((CommonFileSystemAction *)u4ReqId);
#if ENABLE_PB_IO_LOG
    LOGI("PullAbortReadAsync(): Leave, player instance = %d, hPullSrc=%d", (int)prMtkMpContext, (int)hPullSrc);
#endif
        return IMTK_PB_CB_ERROR_CODE_OK;
    }


    static const int64_t MAX_SMOOTH_SEEK_LEN = 1024*1024*1;

    bool _canDoSmoothSeek(uint8_t u1Whence, int64_t i8SeekPos, uint64_t size, uint64_t current)
    {
        int64_t i8SumSeekPos = 0;

        switch (u1Whence & ~(0x80))
        {
        case IMTK_CTRL_PULL_SEEK_BGN:
            if (i8SeekPos < 0)
            {
                return true;
            }
            else
            {
                if (i8SeekPos < current)
                {
                    if (i8SeekPos <= MAX_SMOOTH_SEEK_LEN)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    if ((i8SeekPos-current) <= MAX_SMOOTH_SEEK_LEN)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            break;

        case IMTK_CTRL_PULL_SEEK_CUR:
            if ((i8SeekPos >= 0) && ((i8SeekPos - current) <= MAX_SMOOTH_SEEK_LEN))
            {
                return true;
            }
            else
            {
                return false;
            }
            break;

        case IMTK_CTRL_PULL_SEEK_END:
            if (i8SeekPos > 0)
            {
                return true;
            }
            else
            {
                int64_t i4FinalSeekPos = i8SeekPos + size;
                if (i4FinalSeekPos < 0)
                {
                    return true;
                }
                else
                if ((i4FinalSeekPos> current) && ((i4FinalSeekPos - current) <= MAX_SMOOTH_SEEK_LEN))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            break;
        default:
                LOGI("_canDoSmoothSeek(): SEEK-Type not supported\n");
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }

        return true;
    }


    IMTK_PB_CB_ERROR_CODE_T PullByteSeek(IMTK_PULL_HANDLE_T      hPullSrc,
                                          void*                  pvAppTag,
                                          int64_t                i8SeekPos,
                                          uint8_t                u1Whence,
                                          uint64_t*              pu8CurPos)
    {

    INPUTSTREAM_ENV_T* prMtkMpContext = (INPUTSTREAM_ENV_T*)pvAppTag;
#if ENABLE_PB_IO_LOG
    LOGI("PullByteSeek(): Enter, player instance = %d, hPullSrc=%d", (int)prMtkMpContext, (int)hPullSrc);
#endif

    os::ScopedMutex scoped(prMtkMpContext->m_mutexInputStrem);

        JNIEnv*              env;
        jboolean             seekEnable;
        jint                 ret = 0;
        jlong                result = 0;

        jlong               current;
        jlong               size;

    ret = ((prMtkMpContext->vm))->AttachCurrentThread((JNIEnv**)&env, NULL);

    seekEnable = env->GetBooleanField( prMtkMpContext->thiz, MTK::s_MpJniFields.streamSeekable);

        // 1M: begin
    current = env->GetLongField( prMtkMpContext->thiz, MTK::s_MpJniFields.streamCurPos);

    size = env->GetLongField( prMtkMpContext->thiz, MTK::s_MpJniFields.streamSize);
        if(size == 0)
        {
            size = 0x7fffffffffffffff;
        }

        MMP_LOG("####  PullByteSeek: seekEnable = %d", (int)seekEnable);
        MMP_LOG("####  PullByteSeek: u1Whence & 0x80 = D:%d,X:%x", u1Whence & 0x80, u1Whence & 0x80);
        MMP_LOG("####  PullByteSeek: u1Whence & 0x7f = D:%d,X:%x", u1Whence & 0x7f, u1Whence & 0x7f);

        bool fgCanDoSmoothSeek = false;
        fgCanDoSmoothSeek = _canDoSmoothSeek(u1Whence, i8SeekPos, size, current);

        if(!seekEnable/* && (u1Whence & 0x80)*/ && !fgCanDoSmoothSeek)
        {
        ((prMtkMpContext->vm))->DetachCurrentThread();

        LOGI("PullByteSeek(): player instance = %d, hPullSrc=%d, SEEK_BUSY ******************** return SEEK_BUSY",
            (int)prMtkMpContext, (int)hPullSrc);

            return (IMTK_PB_CB_ERROR_CODE_T)SEEK_BUSY;
        }

        // 1M: end

    result = env->CallLongMethod( prMtkMpContext->thiz, MTK::s_MpJniFields.seek_stream, u1Whence & 0x7f, i8SeekPos);
        *pu8CurPos  = result;
    ((prMtkMpContext->vm))->DetachCurrentThread();

#if ENABLE_PB_IO_LOG
    LOGI("PullByteSeek(): Leave, player instance = %d, hPullSrc=%d", (int)prMtkMpContext, (int)hPullSrc);
#endif
        return IMTK_PB_CB_ERROR_CODE_OK;
    }


    IMTK_PB_CB_ERROR_CODE_T PullGetInputLen(IMTK_PULL_HANDLE_T   hPullSrc,
                                             void*               pvAppTag,
                                             uint64_t*           pu8Len)
    {

    INPUTSTREAM_ENV_T*  prMtkMpContext = (INPUTSTREAM_ENV_T*) pvAppTag;

    LOGI("PullGetInputLen(): Enter, player instance = %d, hPullSrc=%d", (int)prMtkMpContext, (int)hPullSrc);

        JNIEnv*             env;
        jint                ret;
        jlong               size;

    ret = ((prMtkMpContext->vm))->AttachCurrentThread((JNIEnv**)&env, NULL);

    size = env->GetLongField( prMtkMpContext->thiz, MTK::s_MpJniFields.streamSize);

    ((prMtkMpContext->vm))->DetachCurrentThread();

        if(size == 0)
        {
            size = 0x7fffffffffffffff;
        }
        *pu8Len = size;

    LOGI("PullGetInputLen(): player instance = %d, hPullSrc=%d, *pu8Len = %lld", (int)prMtkMpContext, (int)hPullSrc, *pu8Len);

    LOGI("PullGetInputLen(): Leave, player instance = %d, hPullSrc=%d", (int)prMtkMpContext, (int)hPullSrc);

        return IMTK_PB_CB_ERROR_CODE_OK;
    }


    #if 1 // local url by pull mode
        #include <libsonivox/eas.h>

        #ifndef UNUSED
            #define UNUSED(x)               (void)x         /**<The return value of routine is not cared*/
        #endif

        #define ENABLE_NO_CACHE 1

    #endif

    class LocalUrl_PullClient;
    LocalUrl_PullClient * g_pLocalUrl_PullClient = NULL;

    class LocalUrl_PullClient
    {
        public:
            LocalUrl_PullClient();
            ~LocalUrl_PullClient();
            void setDataSource(char *url);

        public:
            int m_fdPullClient;
            off64_t m_nLength;
            off64_t m_nOffset;
    };

    LocalUrl_PullClient::LocalUrl_PullClient()
    {
        LOGI("####  LocalUrl_PullClient(): Enter\n");

        m_fdPullClient = -1;
        m_nLength = 0;
        m_nOffset = 0;

        LOGI("####  LocalUrl_PullClient(): Leave\n");
    }

    LocalUrl_PullClient::~LocalUrl_PullClient()
    {
        LOGI("####  ~LocalUrl_PullClient(): Enter\n");

        if (m_fdPullClient >= 0)
        {
            close(m_fdPullClient);

            m_fdPullClient = -1;
            m_nLength = 0;
            m_nOffset = 0;
        }

        LOGI("####  ~LocalUrl_PullClient(): Leave\n");
    }

    void LocalUrl_PullClient::setDataSource(char *url)
    {
        LOGI("####  LocalUrl_PullClient::setDataSource(): Enter\n");
        LOGI("####  LocalUrl_PullClient::setDataSource(): url = %s\n", url);

        if(url[0] == '/') //it's local file
        {
        // open file and set paused state
            m_fdPullClient = open(url, O_RDONLY);
            if(m_fdPullClient < 0)
            {
                LOGE("####  LocalUrl_PullClient::setDataSource(): open file(%s) fail: errno=%s\n", url, strerror(errno));
                m_fdPullClient = -1;

                return ;
            }
            LOGI("####  LocalUrl_PullClient::setDataSource(): open file(%s) ok, fd = %d\n", url, m_fdPullClient);

            off64_t i8SeekRet = lseek64(m_fdPullClient, 0, SEEK_END);
            if (i8SeekRet < 0)
            {
                LOGE("####  LocalUrl_PullClient::setDataSource(): seek to file end fail: errno=%s\n", strerror(errno));
                close(m_fdPullClient);
                m_fdPullClient = -1;

                return ;
            }

            m_nLength= i8SeekRet;

            LOGI("####  LocalUrl_PullClient::setDataSource(): file length = %lld\n", m_nLength);

            i8SeekRet = lseek64(m_fdPullClient, 0, SEEK_SET);
            if (i8SeekRet < 0)
            {
                LOGE("####  LocalUrl_PullClient::setDataSource(): lseek64, SEEK_SET: errno=%s\n", strerror(errno));
                close(m_fdPullClient);
                m_fdPullClient = -1;

                return ;
            }
        }

        LOGI("####  LocalUrl_PullClient::setDataSource(): Leave\n");
    }

    int localUrl_pullPlayerSyncToAsyncRead(void * handle, void * pu1DstBuf, uint32_t u4Count,
                            uint32_t* pu4Read, IMTK_PULL_HANDLE_T hPullSrc, uint64_t u8ReadBeginPos)
    {
        MMP_LOG("####  localUrl_pullPlayerSyncToAsyncRead(): Enter\n");

        off64_t i8SeekRet;
        off64_t i8CurCursorPos;
        off64_t i8ReadBytes = 0;
        LocalUrl_PullClient*    p_this = NULL;

        p_this = (LocalUrl_PullClient*)handle;

        MMP_LOG("####  localUrl_pullPlayerSyncToAsyncRead(): hPullSrc=%d, pu1DstBuf = %x, count = %d \n", (uint32_t)(hPullSrc), (uint32_t)pu1DstBuf, (int32_t)u4Count);

        if(pu1DstBuf == NULL || u4Count == 0 || pu1DstBuf == (uint8_t*)0xFFFFFFFF || p_this == NULL)
        {
            LOGE("####  localUrl_pullPlayerSyncToAsyncRead(): parameter error\n");
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }

        i8SeekRet = lseek64((uint32_t)hPullSrc, 0, SEEK_CUR);
        if(i8SeekRet < 0)
        {
            LOGE("####  localUrl_pullPlayerSyncToAsyncRead(): i8CurCursorPos, lseek64() failed, errno = %s \n", strerror(errno));
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }
        i8CurCursorPos = i8SeekRet;
        MMP_LOG("####  localUrl_pullPlayerSyncToAsyncRead(): i8CurCursorPos = %lld \n", i8CurCursorPos);


        if (i8CurCursorPos >= p_this->m_nLength + p_this->m_nOffset)
        {
            LOGI("####  localUrl_pullPlayerSyncToAsyncRead(): have arrived at the end of file, so return IMTK_PB_CB_ERROR_CODE_EOF\n");
            return IMTK_PB_CB_ERROR_CODE_EOF;
        }
        else if (i8CurCursorPos + u4Count > p_this->m_nLength + p_this->m_nOffset)
        {
            LOGI("####  localUrl_pullPlayerSyncToAsyncRead(): return file last bytes count = %d\n", u4Count);
            u4Count = p_this->m_nLength + p_this->m_nOffset - i8CurCursorPos;
        }
        MMP_LOG("####  localUrl_pullPlayerSyncToAsyncRead(): try to read, u4Count = %d\n", u4Count);


        i8ReadBytes= read((uint32_t)hPullSrc, pu1DstBuf, u4Count);
        MMP_LOG("####  localUrl_pullPlayerSyncToAsyncRead(): after fread(), i8ReadBytes = %lld \n", i8ReadBytes);

        if (i8ReadBytes < 0)
        {
            LOGE("####  localUrl_pullPlayerSyncToAsyncRead(): read() failed: errno=%d ...\n", strerror(errno));
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }
        else if(i8ReadBytes > 0)
        {
#if ENABLE_NO_CACHE// clear cache
            off64_t i8Count_4k = i8CurCursorPos / 4096;
            off64_t i8Step = i8CurCursorPos - (i8Count_4k * 4096);

            int i4AdviseRet = 0;
            off64_t i8AdvPos = i8Count_4k*4096;
            off64_t i8AdvRemoveBytes = i8ReadBytes + i8Step;

            MMP_LOG("####  localUrl_pullPlayerSyncToAsyncRead(): i8Count_4k=%lld, i8Step=%lld, i8AdvPos=%lld, i8AdvRemoveBytes\n", i8Count_4k, i8Step, i8AdvPos, i8AdvRemoveBytes);

            i4AdviseRet = posix_fadvise64((uint32_t)hPullSrc, i8AdvPos, i8AdvRemoveBytes, POSIX_FADV_DONTNEED);
            if (i4AdviseRet < 0)
            {
                LOGE("####  localUrl_pullPlayerSyncToAsyncRead(): posix_fadvise(), i4AdviseRet=%d, failed:errno=%s!!\n", i4AdviseRet, strerror(errno));
            }
#endif
            *pu4Read = i8ReadBytes;
            return IMTK_PB_CB_ERROR_CODE_OK;
        }
        else
        {
            LOGI("####  localUrl_pullPlayerSyncToAsyncRead(): i8ReadBytes == 0, end of file, return IMTK_PB_CB_ERROR_CODE_EOF\n");
            *pu4Read = 0;
            return IMTK_PB_CB_ERROR_CODE_EOF;
        }

        MMP_LOG("####  localUrl_pullPlayerSyncToAsyncRead(): Leave\n");
    }

    IMTK_PB_CB_ERROR_CODE_T LocalUrl_PullOpen(IMTK_PB_HANDLE_T hHandle, IMTK_PULL_HANDLE_T* phPullSrc, void* pvAppTag)
    {
        LOGI("####  LocalUrl_PullOpen(): Enter\n");

        int fdDup = 0;
        LocalUrl_PullClient *p_this;

        p_this = (LocalUrl_PullClient*)pvAppTag;
        if(!p_this)
        {
            LOGE("####  LocalUrl_PullOpen(): LocalUrl_PullClient's instance not created!!!!!\n");
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }

        if(p_this->m_fdPullClient== -1)
        {
            LOGE("####  LocalUrl_PullOpen(): m_fdPullClient is empty!!!!!\n");
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }

        fdDup = dup(p_this->m_fdPullClient);
        if (fdDup == -1)
        {
            LOGE("####  LocalUrl_PullOpen(): dup(p_this->m_fdPullClient) failed: errno=%s !!!!\n", strerror(errno));
        }

#if 1 // just output log
        off64_t i8Head = 0;
        i8Head = lseek64(fdDup, 0, SEEK_CUR);
        if(-1 == i8Head)
        {
            LOGE("####  LocalUrl_PullOpen(): lseek64(fdDup, 0, SEEK_CUR) failed: errno = %s \n", strerror(errno));
        }

        LOGI("####  LocalUrl_PullOpen(): i8Head=%lld !!\n", i8Head);
#endif

#if ENABLE_NO_CACHE //clear cache
        LOGI("####  LocalUrl_PullClose(): call posix_fadvise64(fdDup, 0, 0, POSIX_FADV_SEQUENTIAL)\n");

        int i4AdviseRet = 0;
        i4AdviseRet = posix_fadvise64(fdDup, 0, 0, POSIX_FADV_SEQUENTIAL);
        if (i4AdviseRet < 0)
        {
            LOGE("####  LocalUrl_PullOpen(): posix_fadvise64() seq error, i4AdviseRet=%d, errno=%s!!\n", i4AdviseRet, strerror(errno));
        }
#endif
        *phPullSrc = (IMTK_PULL_HANDLE_T)fdDup;

        LOGI("####  LocalUrl_PullOpen(): Leave\n");

        return IMTK_PB_CB_ERROR_CODE_OK;
    }

    IMTK_PB_CB_ERROR_CODE_T LocalUrl_PullClose(IMTK_PULL_HANDLE_T hPullSrc, void* pvAppTag)
    {
        LOGI("####  LocalUrl_PullClose(): Enter\n");
        LOGI("####  LocalUrl_PullClose(): hPullSrc=%d \n", (uint32_t)hPullSrc);
        int i4_ret = 0;

#if ENABLE_NO_CACHE // clear cache
        LOGI("####  LocalUrl_PullClose(): call posix_fadvise64((uint32_t)hPullSrc, 0, 0, POSIX_FADV_DONTNEED)\n");

        int i4AdviseRet = 0;
        i4AdviseRet = posix_fadvise64((uint32_t)hPullSrc, 0, 0, POSIX_FADV_DONTNEED);
        if (i4AdviseRet < 0)
        {
           LOGE("####  LocalUrl_PullClose(): posix_fadvise(), i4AdviseRet=%d, failed, errno=%s!!\n", i4AdviseRet, strerror(errno));
        }
#endif

        i4_ret = close((uint32_t)hPullSrc);
        if(i4_ret != 0)
        {
            LOGE("####  LocalUrl_PullClose(): close() failed, errno = %s \n", strerror(errno));
        }

        LOGI("####  LocalUrl_PullClose(): Leave\n");

        return ((i4_ret == 0) ? IMTK_PB_CB_ERROR_CODE_OK : IMTK_PB_CB_ERROR_CODE_NOT_OK);
    }

    IMTK_PB_CB_ERROR_CODE_T LocalUrl_PullRead(IMTK_PULL_HANDLE_T     hPullSrc,
                                                  void*                  pvAppTag,
                                                  uint8_t*               pu1DstBuf,
                                                  uint32_t               u4Count,
                                                  uint32_t*              pu4Read)
    {
        MMP_LOG("####  LocalUrl_PullRead(): Enter\n");
        MMP_LOG("####  LocalUrl_PullRead(): hPullSrc=%d \n", (uint32_t)hPullSrc);

        int ret = localUrl_pullPlayerSyncToAsyncRead((void*)pvAppTag, pu1DstBuf, u4Count, pu4Read, hPullSrc, 0);
        if (ret != IMTK_PB_CB_ERROR_CODE_OK)
        {
            LOGE("result of calling localUrl_pullPlayerSyncToAsyncRead() is IMTK_PB_CB_ERROR_CODE_EOF or IMTK_PB_CB_ERROR_CODE_NOT_OK\n");
        }

        MMP_LOG("####  LocalUrl_PullRead: pu4Read = %d , ret = %d \n", *pu4Read, ret);
        MMP_LOG("####  LocalUrl_PullRead(): Leave\n");

        return (IMTK_PB_CB_ERROR_CODE_T)ret;
    }

    IMTK_PB_CB_ERROR_CODE_T LocalUrl_PullReadAsync(IMTK_PULL_HANDLE_T         hPullSrc,
                                                           void*                      pvAppTag,
                                                           uint8_t*                   pu1Dst,
                                                           uint32_t                   u4DataLen,
                                                           IMtkPb_Ctrl_Pull_Nfy_Fct   pfnNotify,
                                                           void*                      pvRdAsyTag,
                                                           uint32_t*                  pu4ReqId)
    {
        MMP_LOG("####  LocalUrl_PullReadAsync(): Enter\n");
        MMP_LOG("####  LocalUrl_PullReadAsync(): hPullSrc=%d \n", (uint32_t)hPullSrc);

        CommonFileSystemAction* action = new CommonFileSystemAction;
        action->handle = (void*)pvAppTag;
        action->buffer = (char*)pu1Dst;
        action->size   = u4DataLen;
        action->listener = pfnNotify;
        action->read = localUrl_pullPlayerSyncToAsyncRead;
        action->status = CommonFileSystemAction::Running;
        action->tag = pvRdAsyTag;
        action->hPullSrc = hPullSrc;
        action->m_u8ReadBeginPos = 0;// should be modified...
        *pu4ReqId = (uint32_t)action;
        SyncToAsync::getInstance().addAction(action);

        MMP_LOG("####  LocalUrl_PullReadAsync: pu4ReqId = %d\n", *pu4ReqId);
        MMP_LOG("####  LocalUrl_PullReadAsync(): Leave\n");

        return IMTK_PB_CB_ERROR_CODE_OK;
    }

    IMTK_PB_CB_ERROR_CODE_T LocalUrl_PullAbortReadAsync(IMTK_PULL_HANDLE_T     hPullSrc,
                                                                 void*                  pvAppTag,
                                                                 uint32_t               u4ReqId)
    {
        MMP_LOG("####  LocalUrl_PullAbortReadAsync(): Enter\n");
        MMP_LOG("####  LocalUrl_PullAbortReadAsync(): hPullSrc=%d \n", (uint32_t)hPullSrc);

        SyncToAsync::getInstance().abortAction((CommonFileSystemAction *)u4ReqId);

        MMP_LOG("####  LocalUrl_PullAbortReadAsync(): Leave\n");

        return IMTK_PB_CB_ERROR_CODE_OK;
    }

    IMTK_PB_CB_ERROR_CODE_T LocalUrl_PullByteSeek(IMTK_PULL_HANDLE_T         hPullSrc,
                                                         void*                      pvAppTag,
                                                         int64_t                    i8SeekPos,
                                                         uint8_t                    u1Whence,
                                                         uint64_t*                  pu8CurPos)
    {
        MMP_LOG("####  LocalUrl_PullByteSeek(): Enter\n");
        MMP_LOG("####  LocalUrl_PullByteSeek(): hPullSrc=%d, i8SeekPos=%lld, u1Whence=%d \n", (uint32_t)hPullSrc, i8SeekPos, u1Whence);

        LocalUrl_PullClient *p_this;
        off64_t i8SeekRet;
        off64_t i8OldCursorPos;
        off64_t i8NewCursorPos;
        int whence;

        p_this = (LocalUrl_PullClient*)pvAppTag;
        if(!p_this)
        {
            LOGE("####  LocalUrl_PullByteSeek(): LocalUrl_PullClient's instance not created!!!!!\n");
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }

        if(p_this->m_fdPullClient== -1)
        {
            LOGE("####  LocalUrl_PullByteSeek(): p_this->m_fdPullClient == -1, not inited or not successfully inited\n");
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }

        switch (u1Whence & ~(0x80))
        {
        case IMTK_CTRL_PULL_SEEK_BGN:
            i8SeekPos = i8SeekPos + p_this->m_nOffset;
            whence = SEEK_SET;
            break;
        case IMTK_CTRL_PULL_SEEK_CUR:
            whence = SEEK_CUR;
            break;
        case IMTK_CTRL_PULL_SEEK_END:
            i8SeekPos = p_this->m_nLength+ i8SeekPos + p_this->m_nOffset;
            whence = SEEK_SET;
            break;
        default:
                LOGE("####  LocalUrl_PullByteSeek(): SEEK-Type not supported, so return IMTK_PB_CB_ERROR_CODE_NOT_OK\n");
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }
        MMP_LOG("####  LocalUrl_PullByteSeek(): after switch, whence = %d, i8SeekPos = %lld\n",(int32_t)whence, i8SeekPos);

        i8SeekRet = lseek64((uint32_t)hPullSrc, 0, SEEK_CUR);
        if(i8SeekRet < 0)
        {
            LOGE("####  LocalUrl_PullByteSeek(): i8OldCursorPos, lseek64() failed, errno = %s \n", strerror(errno));
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }
        i8OldCursorPos = i8SeekRet;
        MMP_LOG("####  LocalUrl_PullByteSeek(): i8OldCursorPos = %lld \n", i8OldCursorPos);

#if ENABLE_NO_CACHE // clear cache
        off64_t i8Count_4k = i8OldCursorPos / 4096;

        int i4AdviseRet = 0;
        off64_t i8AdvPos = i8Count_4k*4096;
        off64_t i8AdvRemoveBytes = 4096;

        MMP_LOG("####  LocalUrl_PullByteSeek(): i8Count_4k=%lld, i8AdvPos=%lld, i8AdvRemoveBytes\n", i8Count_4k, i8AdvPos, i8AdvRemoveBytes);

        i4AdviseRet = posix_fadvise64((uint32_t)hPullSrc, i8AdvPos, i8AdvRemoveBytes, POSIX_FADV_DONTNEED);
        if (i4AdviseRet < 0)
        {
            LOGE("####  LocalUrl_PullByteSeek(): posix_fadvise(), i4AdviseRet=%d, failed:errno=%s!!\n", i4AdviseRet, strerror(errno));
        }
#endif

        i8SeekRet = lseek64((uint32_t)hPullSrc, i8SeekPos, whence);
        if(i8SeekRet < 0)
        {
            LOGE("####  LocalUrl_PullByteSeek(): lseek64() set new position, failed, errno = %s \n", strerror(errno));
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }

        i8SeekRet = lseek64((uint32_t)hPullSrc, 0, SEEK_CUR);
        if(i8SeekRet < 0)
        {
            LOGE("####  LocalUrl_PullByteSeek(): i8NewCursorPos, lseek64() failed, errno = %s \n", strerror(errno));
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }

        i8NewCursorPos = i8SeekRet;
        MMP_LOG("####  LocalUrl_PullByteSeek(): i8NewCursorPos = %lld \n", i8NewCursorPos);

        if (i8NewCursorPos < p_this->m_nOffset || i8NewCursorPos > (p_this->m_nOffset + p_this->m_nLength))
        {
            lseek64((uint32_t)hPullSrc, i8OldCursorPos, SEEK_SET); //restore original position
            LOGE("####  LocalUrl_PullByteSeek(): offset too large!!!\n");
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }

        *pu8CurPos = i8SeekPos;

        MMP_LOG("####  LocalUrl_PullByteSeek(): Leave\n");

        return IMTK_PB_CB_ERROR_CODE_OK;
    }

    IMTK_PB_CB_ERROR_CODE_T LocalUrl_PullGetInputLen(IMTK_PULL_HANDLE_T  hPullSrc,
                                                          void*               pvAppTag,
                                                          uint64_t*           pu8Len)
    {
        LOGI("####  LocalUrl_PullGetInputLen(): Enter\n");

        LocalUrl_PullClient *p_this;
        p_this = (LocalUrl_PullClient*)pvAppTag;
        if(!p_this)
        {
            LOGE("####  LocalUrl_PullGetInputLen(): LocalUrl_PullClient's instance not created!!!!!\n");
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }

        if(p_this->m_fdPullClient!= -1)
        {
            *pu8Len = p_this->m_nLength;
        }
        else
        {
            *pu8Len = 0;
        }
        LOGI("LocalUrl_PullGetInputLen(%d) ok, length = %lld \n",(uint32_t)hPullSrc, *pu8Len);

        LOGI("####  LocalUrl_PullGetInputLen(): Leave\n");

        return IMTK_PB_CB_ERROR_CODE_OK;
    }

    IMTK_PB_CB_ERROR_CODE_T MediaPbCallback(IMTK_PB_CTRL_EVENT_T   eEventType,
                                             void*                 pvTag,
                                             uint32_t              u4Data)
    {

        INPUTSTREAM_ENV_T*  info = (INPUTSTREAM_ENV_T*)pvTag;
        JNIEnv*             env;
    jclass              jclassPlayer;
    jmethodID           jmethodIdHandleMsg;
        uint32_t            ui4EventType = eEventType;

        if(!info)
        {
      LOGE("MediaPbCallback(): info=NULL, please check!");
          return IMTK_PB_CB_ERROR_CODE_NOT_OK;
        }

    jint ret = g__JavaVM->AttachCurrentThread((JNIEnv**)&env, NULL);

    jclassPlayer = (info->player);
    jmethodIdHandleMsg = env->GetMethodID(jclassPlayer, "handleMessage", "(III)V");

    int i4MsgType  = 0;
    int i4MsgWhat  = 0;
    int i4MsgExtra = 0;

        switch (ui4EventType)
        {
            case IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE:
            i4MsgType = EVENT_CUR_TIME_UPDATE;
            i4MsgWhat = u4Data;
            i4MsgExtra = 0;
                break;

            case IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW:
            LOGI("MediaPbCallback(): receive IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW");
            i4MsgType = EVENT_UPDATE;
            i4MsgWhat = EVENT_UNKNOWN;
            i4MsgExtra = 0;
                break;

            case IMTK_PB_CTRL_EVENT_GET_BUF_READY:
            LOGI("MediaPbCallback(): receive IMTK_PB_CTRL_EVENT_GET_BUF_READY");
            i4MsgType = EVENT_UPDATE;
            i4MsgWhat = EVENT_UNKNOWN;
            i4MsgExtra = 100;
                break;

            case IMTK_PB_CTRL_EVENT_TOTAL_TIME_UPDATE:
            LOGI("MediaPbCallback(): receive IMTK_PB_CTRL_EVENT_TOTAL_TIME_UPDATE");
            i4MsgType = EVENT_TOTAL_TIME_UPDATE;
            i4MsgWhat = u4Data;
            i4MsgExtra = 0;
                break;

            case IMTK_PB_CTRL_EVENT_EOS:
            LOGI("MediaPbCallback(): receive IMTK_PB_CTRL_EVENT_EOS");
            i4MsgType = EVENT_COMPLETION;
            i4MsgWhat = u4Data;
            i4MsgExtra = 0;
                break;

            case IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR:
            LOGI("MediaPbCallback(): receive IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR");
            i4MsgType = EVENT_ERROR;
            i4MsgWhat = u4Data;
            i4MsgExtra = 0;
                break;

            case IMTK_PB_CTRL_EVENT_TIMESEEK_DONE:
            LOGI("MediaPbCallback(): receive IMTK_PB_CTRL_EVENT_TIMESEEK_DONE");
            i4MsgType = EVENT_SEEKCOMPLETE;
            i4MsgWhat = EVENT_UNKNOWN;
            i4MsgExtra = 0;
                break;

            case IMTK_PB_CTRL_EVENT_STEP_DONE:
            LOGI("MediaPbCallback(): receive IMTK_PB_CTRL_EVENT_STEP_DONE");
            i4MsgType = EVENT_STEP_DONE;
            i4MsgWhat = EVENT_UNKNOWN;
            i4MsgExtra = 0;
                break;

            case IMTK_PB_CTRL_EVENT_EOF:
            LOGI("MediaPbCallback(): receive IMTK_PB_CTRL_EVENT_EOF");
            i4MsgType = EVENT_EOF;
            i4MsgWhat = EVENT_UNKNOWN;
            i4MsgExtra = 0;
                break;

            case IMTK_PB_CTRL_EVENT_PLAY_DONE:
            LOGI("MediaPbCallback(): receive IMTK_PB_CTRL_EVENT_PLAY_DONE");
            i4MsgType = EVENT_PLAYDONE;
            i4MsgWhat = EVENT_UNKNOWN;
            i4MsgExtra = 0;
                break;

            case IMTK_PB_CTRL_EVENT_POSITION_UPDATE:
            i4MsgType = EVENT_POSITION_UPDATE;
            i4MsgWhat = u4Data;
            i4MsgExtra = 0;
                break;

            case IMTK_PB_CTRL_EVENT_SPEED_UPDATE:
            LOGI("MediaPbCallback(): receive IMTK_PB_CTRL_EVENT_SPEED_UPDATE");
            i4MsgType = EVENT_SPEED_UPDATE;
            i4MsgWhat = u4Data;
            i4MsgExtra = 0;
                break;

            case IMTK_PB_CTRL_EVENT_PREPARE_DONE:
            LOGI("MediaPbCallback(): receive IMTK_PB_CTRL_EVENT_PREPARE_DONE");
            i4MsgType = EVENT_PREPARED;
            i4MsgWhat = EVENT_UNKNOWN;
            i4MsgExtra = 0;
                break;

            case IMTK_PB_CTRL_EVENT_REPLAY:
            LOGI("MediaPbCallback(): receive IMTK_PB_CTRL_EVENT_REPLAY");
            i4MsgType = EVENT_REPLAY;
            i4MsgWhat = EVENT_UNKNOWN;
            i4MsgExtra = 0;
            break;

        case IMTK_PB_CTRL_EVENT_AUDIO_ONLY_SERVICE:
            LOGI("MediaPbCallback(): receive IMTK_PB_CTRL_EVENT_AUDIO_ONLY_SERVICE");
            i4MsgType = EVENT_AUDIO_ONLY_SERVICE;
            i4MsgWhat = EVENT_UNKNOWN;
            i4MsgExtra = 0;
                break;

            default:
                break;
        }

    env->CallVoidMethod(info->thiz, jmethodIdHandleMsg, i4MsgType, i4MsgWhat, i4MsgExtra);

    g__JavaVM->DetachCurrentThread();

        return IMTK_PB_CB_ERROR_CODE_OK;
    }

   /*
    * Class:     com_mediatek_media_MtkMediaPlayer
    * Method:    nativeOpen
    * Signature: (ILjava/lang/String;I)I
    */
    static int s_i4PlayerCount = 0;

    JNIEXPORT jint JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeOpen
        (JNIEnv * env, jobject thiz , jint mediaPlayerMode, jstring profile, jint mediaType)
    {
        uint8_t*                       pu1Profile;
        const char*                    profileCharPoint;
        IMTK_PB_ERROR_CODE_T           e_return = IMTK_PB_ERROR_CODE_OK;
        IMTK_PB_CTRL_OPERATING_MODEL_T operatingModel = IMTK_PB_CTRL_LIB_MASTER;

        INPUTSTREAM_ENV_T* prMtkMpContext = new INPUTSTREAM_ENV_T();

        LOGI("nativeOpen(): Enter, player instance = %d", (int)prMtkMpContext);

        s_i4PlayerCount ++;
        LOGI("nativeOpen(): player instance = %d, s_i4PlayerCount = %d", (int)prMtkMpContext, s_i4PlayerCount);

        jint ret = env->GetJavaVM( &prMtkMpContext->vm);
        if(ret < 0)
        {
            LOGI("nativeOpen(): player instance = %d, GetJavaVM(), failed", (int)prMtkMpContext);
            return 0;
        }

        jclass player = env->FindClass("com/mediatek/media/MtkMediaPlayer");
        prMtkMpContext->player = (jclass)env->NewGlobalRef(player);
        prMtkMpContext->thiz = env->NewGlobalRef(thiz);

        profileCharPoint = env->GetStringUTFChars(profile,NULL);

        pu1Profile  = (uint8_t*)(profileCharPoint);

        LOGI("nativeOpen(): player instance = %d, mediaPlayerMode = %d, profile = %d, profileCharPoint = %s, pu1Profile = %s\n",
            (int)prMtkMpContext, mediaPlayerMode, profile, profileCharPoint, pu1Profile);

        if(MEDIA_TYPE_PCM == mediaType)
        {
          operatingModel = IMTK_PB_CTRL_APP_MASTER;
        }

        LOGI("nativeOpen(): mediaType = %d, after check, operatingModel = %d\n", mediaType, (int)operatingModel);

        e_return = IMtkPb_Ctrl_Open(&prMtkMpContext->hHandle,
                                     (IMTK_PB_CTRL_BUFFERING_MODEL_T)mediaPlayerMode,
                                     operatingModel,
                                     pu1Profile);

        env->ReleaseStringUTFChars(profile,profileCharPoint);

        if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
            LOGI("nativeOpen(): player instance = %d, IMtkPb_Ctrl_Open(), e_return=%d, failed", (int)prMtkMpContext, (int)e_return);
            return 0;
        }

        LOGI("nativeOpen(): player instance = %d, ok, CMPB handle = %x", (int)prMtkMpContext, (uint32_t)prMtkMpContext->hHandle);

        LOGI("nativeOpen(): Leave, player instance = %d", (int)prMtkMpContext);

        return (jint)prMtkMpContext;

    }

   /*
    * Class:     com_mediatek_media_MtkMediaPlayer
    * Method:    nativeClose
    * Signature: (I)V
    */
    JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeClose
        (JNIEnv * env, jobject thiz, jint handle)
    {

        INPUTSTREAM_ENV_T*  prMtkMpContext = (INPUTSTREAM_ENV_T *)handle;
        IMTK_PB_ERROR_CODE_T  e_return = IMTK_PB_ERROR_CODE_OK;

        LOGI("nativeClose(): Enter, player instance = %d", (int)prMtkMpContext);

        LOGI("nativeClose(): player instance = %d, CMPB handle = %x", (int)prMtkMpContext, (uint32_t)prMtkMpContext->hHandle);

        if (g_pLocalUrl_PullClient)
        {
            LOGI("nativeClose(): player instance = %d, delete the instance of LocalUrl_PullClient", (int)prMtkMpContext);
            delete g_pLocalUrl_PullClient;
            g_pLocalUrl_PullClient = NULL;
        }

        if(prMtkMpContext->hHandle)
        {
            e_return = IMtkPb_Ctrl_Close(prMtkMpContext->hHandle);
            if (e_return != IMTK_PB_ERROR_CODE_OK)
            {
                LOGE("nativeClose(): player instance = %d, IMtkPb_Ctrl_Close(), e_return=%d, failed\n", (int)prMtkMpContext, (int)e_return);
            }

            prMtkMpContext->hHandle = 0;
            env->DeleteGlobalRef(prMtkMpContext->thiz);
            env->DeleteGlobalRef(prMtkMpContext->player);

            s_i4PlayerCount --;
            LOGI("nativeClose(): player s_i4PlayerCount = %d", s_i4PlayerCount);

            delete prMtkMpContext;
            prMtkMpContext = NULL;
        }

        LOGI("nativeClose(): Leave, player instance = %d", (int)prMtkMpContext);
    }

   /*
    * Class:     com_mediatek_media_MtkMediaPlayer
    * Method:    nativeRegCallback
    * Signature: (I)V
    */
    JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeRegCallback
        (JNIEnv * env, jobject thiz, jint handle)
    {
        LOGI("nativeRegCallback(): Enter\n");

        INPUTSTREAM_ENV_T*    prMtkMpContext = (INPUTSTREAM_ENV_T *)handle;
        IMTK_PB_ERROR_CODE_T  e_return = IMTK_PB_ERROR_CODE_OK;

        LOGI("nativeRegCallback(): Enter, player instance = %d", (int)prMtkMpContext);

        LOGI("nativeRegCallback(): player instance = %d, CMPB handle = %x", (int)prMtkMpContext, (uint32_t)prMtkMpContext->hHandle);

        e_return = IMtkPb_Ctrl_RegCallback(prMtkMpContext->hHandle,
                                            (void*)(prMtkMpContext),
                                            MediaPbCallback
                                            );

        if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
            LOGE("nativeRegCallback(): player instance = %d, IMtkPb_Ctrl_RegCallback(), e_return=%d, failed\n", (int)prMtkMpContext, (int)e_return);

            IMtkPb_Ctrl_Close(prMtkMpContext->hHandle);
            prMtkMpContext->hHandle = 0;
            jniThrowException(env,"java/lang/IllegalStateException","IMtkPb_Ctrl_RegCallback_FAIL");
        }

        LOGI("nativeRegCallback(): Leave, player instance = %d", (int)prMtkMpContext);
    }

/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeSetEngineParam
* Signature: (III)V
*/
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeSetEngineParam
    (JNIEnv * env, jobject thiz, jint handle, jint mediaPlayerMode, jint prepareType)
{

    INPUTSTREAM_ENV_T*         prMtkMpContext = (INPUTSTREAM_ENV_T *)handle;
    CMPBSetEngineParamEvent*   e = new CMPBSetEngineParamEvent;
    jclass                     jclass_Player;
    jfieldID                   jfieldId_Path;
    jstring                    jstring_Path;
    const char*                tempPath;

    LOGI("nativeSetEngineParam(): Enter, player instance = %d", (int)prMtkMpContext);

    LOGI("nativeSetEngineParam(): player instance = %d, CMPB handle = %x", (int)prMtkMpContext, (uint32_t)prMtkMpContext->hHandle);


    jclass_Player = prMtkMpContext->player;
    jfieldId_Path   = env->GetFieldID(jclass_Player, "mDataSourceUrl", "Ljava/lang/String;");
    jstring_Path  = (jstring)env->GetObjectField(prMtkMpContext->thiz, jfieldId_Path);
    tempPath    = env->GetStringUTFChars(jstring_Path,NULL);


    jboolean fgIsAudioPlayer;
    fgIsAudioPlayer = env->GetBooleanField( prMtkMpContext->thiz, MTK::s_MpJniFields.isAudioPlayer);

    LOGI("nativeSetEngineParam(): player instance = %d, fgIsAudioPlayer=%d", (int)prMtkMpContext, fgIsAudioPlayer);

    e->envInfo = prMtkMpContext;
    e->errorCode = IMTK_PB_ERROR_CODE_OK;
    e->mediaPlayerMode = mediaPlayerMode;
    e->prepareType = prepareType;
    e->path = (uint8_t*)strdup(tempPath);
    e->sema = new os::Semaphore(0);
    e->fgIsAudioPlayer = fgIsAudioPlayer;

    LOGI("nativeSetEngineParam(): player instance = %d, mediaPlayerMode = %d, prepareType = %d, path = %s\n",
        (int)prMtkMpContext, mediaPlayerMode, prepareType, e->path);

    engineQueue.send(e);

    if (PREPARE_TYPE_SYNC == prepareType)
    {
        e->sema->acquire();

        if(e->errorCode != IMTK_PB_ERROR_CODE_OK)
        {
            env->ReleaseStringUTFChars(jstring_Path,tempPath);

            delete e->sema;
            e->sema = NULL;
            delete e;

            LOGI("nativeSetEngineParam(): player instance = %d, IMtkPb_Ctrl_SetEngineParam(), failed!", (int)prMtkMpContext);

            jniThrowException(env,"com/mediatek/media/NotSupportException","File Not Support!");
            return ;
        }

        delete e->sema;
        e->sema = NULL;
        delete e;
    }

    env->ReleaseStringUTFChars(jstring_Path,tempPath);

    LOGI("nativeSetEngineParam(): Leave, player instance = %d", (int)prMtkMpContext);
}


   /*
    * Class:     com_mediatek_media_MtkMediaPlayer
    * Method:    nativeGetDuration
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeGetDuration
        (JNIEnv * env, jobject thiz, jint handle)
    {

        INPUTSTREAM_ENV_T*            envInfo = (INPUTSTREAM_ENV_T *)handle;
        IMTK_PB_ERROR_CODE_T          e_return;
        IMTK_PB_CTRL_GET_MEDIA_INFO_T tMediaInfo;

        MMP_LOG("nativeGetDuration(): Enter\n");

        MMP_LOG("nativeGetDuration(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

        e_return = IMtkPb_Ctrl_GetMediaInfo(envInfo->hHandle, &tMediaInfo);
         if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
        LOGE("nativeGetDuration(): IMtkPb_Ctrl_RegCallback(), e_return=%d, failed\n",(int)e_return);
            return 0;
        }

        MMP_LOG("nativeGetDuration(): IMtkPb_Ctrl_GetMediaInfo(), u4TotalDuration = %d",(int)tMediaInfo.u4TotalDuration);

        MMP_LOG("nativeGetDuration(): Leave\n");

        return (int)tMediaInfo.u4TotalDuration;
    }

   /*
    * Class:     com_mediatek_media_MtkMediaPlayer
    * Method:    nativeGetCurrentPosition
    * Signature: (II)J
    */
    JNIEXPORT jlong JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeGetCurrentPosition
        (JNIEnv * env, jobject thiz, jint handle, jint positionType)
    {
        INPUTSTREAM_ENV_T*    envInfo = (INPUTSTREAM_ENV_T *)handle;
        IMTK_PB_ERROR_CODE_T  e_return;
        uint32_t              u4Time;
        uint64_t              u8CurPos;
        uint64_t              u8Position;

        MMP_LOG("nativeGetCurrentPosition(): Enter\n");

        MMP_LOG("nativeGetCurrentPosition(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

        e_return = IMtkPb_Ctrl_GetCurrentPos(envInfo->hHandle, &u4Time, &u8CurPos);
        if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
        LOGE("nativeGetCurrentPosition(): nativeGetCurrentPosition(), e_return=%d, failed\n",(int)e_return);
           return IMTK_PB_ERROR_CODE_NOT_OK;
        }

        MMP_LOG("nativeGetCurrentPosition(): IMtkPb_Ctrl_GetCurrentPos(), u4Time = %d, u8CurPos = %lld \n", u4Time, u8CurPos);

        if(POSITION_TYPE_MILLISECOND == positionType)
        {
           u8Position = (uint64_t)u4Time;
        }
        else if(POSITION_TYPE_POSITION == positionType)
        {
           u8Position = u8CurPos;
        }

        MMP_LOG("nativeGetCurrentPosition(): Leave\n");

        return u8Position;
    }

/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeSeekTo
* Signature: (II)V
*/
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeSeekTo
    (JNIEnv * env, jobject thiz, jint handle, jint position)
{

    INPUTSTREAM_ENV_T*    envInfo = (INPUTSTREAM_ENV_T *)handle;
    IMTK_PB_ERROR_CODE_T  e_return;

    LOGI("nativeSeekTo(): Enter\n");

    LOGI("nativeGetCurrentPosition(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

    LOGI("nativeSeekTo(): position = %d\n", position);

    e_return = IMtkPb_Ctrl_TimeSeek(envInfo->hHandle, position / 1000);
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGE("nativeSeekTo(): IMtkPb_Ctrl_TimeSeek(), e_return = %d\n",e_return);
        jniThrowException(env,"com/mediatek/media/NotSupportException","Not Support TimeSeek!");
    }

    LOGI("nativeSeekTo(): Leave\n");
}


   /*
    * Class:     com_mediatek_media_MtkMediaPlayer
    * Method:    nativeSetDisplay
* Signature: (IIIII)V
    */
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeSetDisplayRect
   (JNIEnv * env, jobject thiz, jint handle, jint x, jint y, jint w, jint h)
{

    INPUTSTREAM_ENV_T*    envInfo = (INPUTSTREAM_ENV_T *)handle;
    IMTK_PB_ERROR_CODE_T  e_return;

    LOGI("nativeSetDisplayRect(): Enter\n");
    LOGI("nativeSetDisplayRect(): x=%d, y=%d, w=%d, h=%d\n", x, y, w, h);

    IMTK_PB_CTRL_RECT_T tSrcRect;
    IMTK_PB_CTRL_RECT_T tDstRect;

    memset(&tSrcRect, 0, sizeof(IMTK_PB_CTRL_RECT_T));
    memset(&tDstRect, 0, sizeof(IMTK_PB_CTRL_RECT_T));

    tSrcRect.u4X = 0;
    tSrcRect.u4Y = 0;
    tSrcRect.u4W = 1000;
    tSrcRect.u4H = 1000;

    tDstRect.u4X = x;
    tDstRect.u4Y = y;
    tDstRect.u4W = w;
    tDstRect.u4H = h;

    e_return = IMtkPb_Ctrl_SetDisplayRectangle(envInfo->hHandle, &tSrcRect, &tDstRect);
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGE("nativeSetDisplay(jint x, jint y, jint w, jint h): IMtkPb_Ctrl_SetDisplayRectangle, error \n");
    }

    LOGI("nativeSetDisplayRect(): Leave\n");
}


/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativePrepare
* Signature: (I)V
*/
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativePrepare
    (JNIEnv * env, jobject thiz, jint handle)
{
    LOGI("nativePrepare(): Enter\n");
    LOGI("nativePrepare(): TODO\n");
    LOGI("nativePrepare(): Leave\n");
}

/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeStart
* Signature: (II)V
*/
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeStart
    (JNIEnv * env, jobject thiz, jint handle, jint millisecond)
{

    INPUTSTREAM_ENV_T* prMtkMpContext = (INPUTSTREAM_ENV_T *)handle;
    IMTK_PB_ERROR_CODE_T  e_return;

    LOGI("nativeStart(): Enter, player instance = %d", (int)prMtkMpContext);

    LOGI("nativeStart(): player instance = %d, CMPB handle = %x", (int)prMtkMpContext, (uint32_t)prMtkMpContext->hHandle);

    e_return = IMtkPb_Ctrl_Play(prMtkMpContext->hHandle, millisecond / 1000);
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGE("nativeStart(): IMtkPb_Ctrl_Play(), e_return = %d\n", e_return);
        jniThrowException(env, "java/lang/IllegalStateException", "IMTK_PB_PLAY_FAIL");
    }

    LOGI("nativeStart(): Leave, player instance = %d", (int)prMtkMpContext);
}

/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativePause
* Signature: (I)V
*/
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativePause
    (JNIEnv * env, jobject thiz, jint handle)
{

    INPUTSTREAM_ENV_T* prMtkMpContext = (INPUTSTREAM_ENV_T *)handle;
    IMTK_PB_ERROR_CODE_T  e_return;

    LOGI("nativePause(): Enter, player instance = %d", (int)prMtkMpContext);

    LOGI("nativePause(): player instance = %d, CMPB handle = %x", (int)prMtkMpContext, (uint32_t)prMtkMpContext->hHandle);

    e_return = IMtkPb_Ctrl_Pause(prMtkMpContext->hHandle);
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGI("nativePause(): player instance = %d, IMtkPb_Ctrl_Pause() failed, e_return = %d", (int)prMtkMpContext);

        jniThrowException(env, "java/lang/IllegalStateException", "IMTK_PB_PAUSE_FAIL");
    }

    LOGI("nativePause(): Leave, player instance = %d", (int)prMtkMpContext);
}

/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeStop
* Signature: (I)V
*/
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeStop
    (JNIEnv * env, jobject thiz, jint handle)
{

    INPUTSTREAM_ENV_T* prMtkMpContext = (INPUTSTREAM_ENV_T *)handle;
    IMTK_PB_ERROR_CODE_T  e_return;

    LOGI("nativeStop(): Enter, player instance = %d", (int)prMtkMpContext);

    LOGI("nativeStop(): player instance = %d, CMPB handle = %x", (int)prMtkMpContext, (uint32_t)prMtkMpContext->hHandle);

    e_return = IMtkPb_Ctrl_Stop(prMtkMpContext->hHandle);
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGI("nativeStop(): player instance = %d, IMtkPb_Ctrl_Stop() failed, e_return = %d", (int)prMtkMpContext);

        jniThrowException(env, "java/lang/IllegalStateException", "IMTK_PB_STOP_FAIL");
    }

    LOGI("nativeStop(): Leave, player instance = %d", (int)prMtkMpContext);
}

/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeRelease
* Signature: (I)V
*/
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeRelease
    (JNIEnv * env, jobject thiz, jint handle)
{
    LOGI("####  nativeRelease(): Enter\n");
    LOGI("####  nativeRelease(): TODO\n");
    LOGI("####  nativeRelease(): Leave\n");
}

/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeReset
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeReset
    (JNIEnv * env, jobject thiz)
{
    LOGI("####  nativeReset(): Enter\n");
    LOGI("####  nativeReset(): TODO\n");
    LOGI("####  nativeReset(): Leave\n");
}

/*
 * Class:     com_mediatek_media_MtkMediaPlayer
 * Method:    nativeStep
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeStep
    (JNIEnv * env, jobject thiz, jint handle, jint amount)
{
    INPUTSTREAM_ENV_T* prMtkMpContext = (INPUTSTREAM_ENV_T *)handle;
    IMTK_PB_ERROR_CODE_T  e_return;

    LOGI("nativeStep(): Enter, player instance = %d", (int)prMtkMpContext);

    LOGI("nativeStep(): player instance = %d, CMPB handle = %x", (int)prMtkMpContext, (uint32_t)prMtkMpContext->hHandle);

    e_return = IMtkPb_Ctrl_Step(prMtkMpContext->hHandle, amount);
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGI("nativeStep(): player instance = %d, IMtkPb_Ctrl_Step() failed, e_return = %d", (int)prMtkMpContext);

        jniThrowException(env, "java/lang/IllegalStateException", "IMTK_PB_STEP_FAIL");
    }

    LOGI("nativeStep(): Leave, player instance = %d", (int)prMtkMpContext);
}

/*
 * Class:     com_mediatek_media_MtkMediaPlayer
 * Method:    nativeSetSpeed
 * Signature: (II)V
 */
 JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeSetSpeed
     (JNIEnv * env, jobject thiz, jint handle, jint speed)
 {
    INPUTSTREAM_ENV_T* prMtkMpContext = (INPUTSTREAM_ENV_T *)handle;
    IMTK_PB_ERROR_CODE_T  e_return;

    LOGI("nativeSetSpeed(): Enter, player instance = %d", (int)prMtkMpContext);

    LOGI("nativeSetSpeed(): player instance = %d, CMPB handle = %x, speed = %d",
        (int)prMtkMpContext, (uint32_t)prMtkMpContext->hHandle, speed);

    e_return = IMtkPb_Ctrl_SetSpeed(prMtkMpContext->hHandle, (IMTK_PB_CTRL_SPEED_T)speed);
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGI("nativeSetSpeed(): player instance = %d, IMtkPb_Ctrl_SetSpeed() failed, e_return = %d", (int)prMtkMpContext);

        jniThrowException(env, "com/mediatek/media/NotSupportException", "Not Support SetSpeed!");
    }

    LOGI("nativeSetSpeed(): Leave, player instance = %d", (int)prMtkMpContext);
 }

/*
 * Class:     com_mediatek_media_MtkMediaPlayer
 * Method:    nativeSetAudioTrack
 * Signature: (IS)V
 */
 JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeSetAudioTrack
     (JNIEnv * env, jobject thiz, jint handle, jshort audioTrack)
 {
    INPUTSTREAM_ENV_T*    envInfo = (INPUTSTREAM_ENV_T *)handle;
    IMTK_PB_ERROR_CODE_T  e_return;

    LOGI("####  nativeSetAudioTrack(): Enter\n");

    LOGI("####  nativeSetAudioTrack(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

    e_return = IMtkPb_Ctrl_SetAudTrack(envInfo->hHandle, audioTrack);
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGE("####  nativeSetAudioTrack(): IMtkPb_Ctrl_SetAudTrack(), e_return = %d\n", e_return);
    }

    LOGI("####  nativeSetAudioTrack(): Leave\n");
 }

 /*
 * Class:     com_mediatek_media_MtkMediaPlayer
 * Method:    nativeSetSubtitleTrack
 * Signature: (IS)V
 */
 JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeSetSubtitleTrack
     (JNIEnv * env, jobject thiz, jint handle, jshort subtitleTrack)
 {
    INPUTSTREAM_ENV_T*    envInfo = (INPUTSTREAM_ENV_T *)handle;
    IMTK_PB_ERROR_CODE_T  e_return;

    LOGI("####  nativeSetSubtitleTrack(): Enter\n");

    LOGI("####  nativeSetSubtitleTrack(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

    e_return = IMtkPb_Ctrl_SetSubtitleTrack(envInfo->hHandle, subtitleTrack);
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGE("####  nativeSetSubtitleTrack(): IMtkPb_Ctrl_SetSubtitleTrack(), e_return = %d\n", e_return);
    }

    LOGI("####  nativeSetSubtitleTrack(): Leave\n");
 }

    /*
     * Class:     com_mediatek_media_MtkMediaPlayer
     * Method:    nativeSetSubtitleAttr
     * Signature: (ILcom/mediatek/media/SubtitleAttr;)V
     */
     JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeSetSubtitleAttr
         (JNIEnv * env, jobject thiz, jint handle, jobject subtitleAttr)
     {
         INPUTSTREAM_ENV_T*     envInfo = (INPUTSTREAM_ENV_T *)handle;
         IMTK_PB_ERROR_CODE_T   e_return;
         SIZE_T                 size = SBTL_ATTR_NUM * sizeof(IMTK_PB_CTRL_SBTL_ATTR);
         int                    index = 0;
         int                    mask;
         jclass                 subtitleAttrClass;
         jfieldID               maskField ;

         jfieldID               field; //common field
         jobject                object;//common object

         jclass                 interClass;
         jfieldID               interField;
         int                    attrInt;
         short                  attrShort;
         bool                   attrBool;
         int                    fontEncoding;
         jbyte                  attrByte;
         jstring                attrString;
         const char*            attrCharPoint;
         char                   attrChar;
         IMTK_PB_CTRL_SBTL_ATTR attrs[SBTL_ATTR_NUM];
         memset(attrs, 0, sizeof(attrs));

         LOGI("####  nativeSetSubtitleAttr(): Enter\n");

         LOGI("####  nativeSetSubtitleAttr(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

         MMP_LOG("####  nativeSetSubtitleAttr(): subtitleAttr = %x", subtitleAttr);
         MMP_LOG("####  nativeSetSubtitleAttr(): size = %d", (int)size);

         subtitleAttrClass = env->FindClass("com/mediatek/media/SubtitleAttr");
         maskField = env->GetFieldID( subtitleAttrClass, "mask", "I");
         mask = (jint)env->GetIntField( subtitleAttr, maskField);

         MMP_LOG("####  nativeSetSubtitleAttr(): mask = d:%d  x:%x", mask, mask);

         if(mask & DEFAULTTYPE)
         {
            attrs[index].eSbtlAttrType = (IMTK_PB_CTRL_SBTL_ATTR_TYPE)IMTK_PB_CTRL_SBTL_ATTR_TYPE_DEFAULT;
            LOGI("####  nativeSetSubtitleAttr(): DEFAULTTYPE  attrs[index].eSbtlAttrType = %d", (int)attrs[index].eSbtlAttrType);
         }
         else
         {
            if(mask & DISPLAYMODE)
            {
                MMP_LOG("####  nativeSetSubtitleAttr(): enter DISPLAYMODE");
                field = env->GetFieldID( subtitleAttrClass, "displayMode", "Lcom/mediatek/media/SubtitleAttr$SubtitleDisplayMode;");
                MMP_LOG("####  DISPLAYMODE: field = %d", (int)field);
                object = (jobject)env->GetObjectField( subtitleAttr, field);
                MMP_LOG("####  DISPLAYMODE: object = %d", (int)object);
                //inter class
                interClass = env->FindClass("com/mediatek/media/SubtitleAttr$SubtitleDisplayMode");
                MMP_LOG("####  DISPLAYMODE: interClass = %d", (int)interClass);
                interField = env->GetFieldID( interClass, "dispMode", "I");
                MMP_LOG("####  DISPLAYMODE: interField = %d", (int)interField);
                attrInt   = (jint)env->GetIntField( object, interField);
                MMP_LOG("####  DISPLAYMODE: attrInt = %d", attrInt);
                interField = env->GetFieldID( interClass, "u2Param", "S");
                attrShort = (jshort)env->GetShortField( object, interField);
                attrs[index].eSbtlAttrType = IMTK_PB_CTRL_SBTL_ATTR_TYPE_DISP_MODE;
                attrs[index].uAttrValue.tDispMode.eDispMode = (IMTK_PB_CTRL_SBTL_DISP_MODE_TYPE_T)attrInt;
                attrs[index].uAttrValue.tDispMode.u2Param = attrShort;
                MMP_LOG("####  nativeSetSubtitleAttr(): DISPLAYMODE  dispMode = %d", attrInt);
                MMP_LOG("####  nativeSetSubtitleAttr(): DISPLAYMODE  u2Param = %d", attrShort);
                index++;
             }
             if(mask & HILTSTYLE)
             {
                field = env->GetFieldID( subtitleAttrClass, "hiltStyle", "Lcom/mediatek/media/SubtitleAttr$SubtitleHiltStyle;");
                object = (jobject)env->GetObjectField( subtitleAttr, field);
                //inter class
                interClass = env->FindClass("com/mediatek/media/SubtitleAttr$SubtitleHiltStyle");
                interField = env->GetFieldID( interClass, "hiltStyle", "I");
                attrInt   = (jint)env->GetIntField( object, interField);
                interField = env->GetFieldID( interClass, "u2Param", "S");
                attrShort = (jshort)env->GetShortField( object, interField);
                attrs[index].eSbtlAttrType = IMTK_PB_CTRL_SBTL_ATTR_TYPE_HILT_STL;
                attrs[index].uAttrValue.tHiltStl.eHltStyle = (IMTK_PB_CTRL_SBTL_HILT_STL_TYPE_T)attrInt;
                attrs[index].uAttrValue.tHiltStl.u2Param = attrShort;
                MMP_LOG("####  nativeSetSubtitleAttr(): HILTSTYLE  hiltStyle = %d", attrInt);
                MMP_LOG("####  nativeSetSubtitleAttr(): HILTSTYLE  u2Param = %d", attrShort);
                index++;
              }
              if(mask & TIMEOFFSET)
              {
                 field = env->GetFieldID( subtitleAttrClass, "timeOffset", "Lcom/mediatek/media/SubtitleAttr$SubtitleTimeOffset;");
                 object = (jobject)env->GetObjectField( subtitleAttr, field);
                 //inter class
                 interClass = env->FindClass("com/mediatek/media/SubtitleAttr$SubtitleTimeOffset");
                 interField = env->GetFieldID( interClass, "timeOffset", "I");
                 attrInt   = (jint)env->GetIntField( object, interField);
                 attrs[index].eSbtlAttrType = IMTK_PB_CTRL_SBTL_ATTR_TYPE_TIME_OFST;
                 attrs[index].uAttrValue.tTmOfst.eTimeOfst = (IMTK_PB_CTRL_SBTL_TIME_OFST_TYPE_T)attrInt;
                 MMP_LOG("####  nativeSetSubtitleAttr: TIMEOFFSET  timeOffset = %d", attrInt);
                 interField = env->GetFieldID( interClass, "offsetValue", "I");
                 attrInt   = (jint)env->GetIntField( object, interField);
                 attrs[index].uAttrValue.tTmOfst.u4OfstValue = attrInt;
                 MMP_LOG("####  nativeSetSubtitleAttr: TIMEOFFSET  offsetValue = %d", attrInt);
                 index++;
               }
               if(mask & FONTENC)
               {
                  field = env->GetFieldID( subtitleAttrClass, "fontEnc", "Lcom/mediatek/media/SubtitleAttr$SubtitleFontEnc;");
                  object = (jobject)env->GetObjectField( subtitleAttr, field);
                  //inter class
                  interClass = env->FindClass("com/mediatek/media/SubtitleAttr$SubtitleFontEnc");
                  interField = env->GetFieldID( interClass, "fontEncType", "I");
                  attrInt   = (jint)env->GetIntField( object, interField);
                  interField = env->GetFieldID( interClass, "u2Param", "S");
                  attrShort = (jshort)env->GetShortField( object, interField);
                  attrs[index].eSbtlAttrType = IMTK_PB_CTRL_SBTL_ATTR_TYPE_FONT_ENC;
                  attrs[index].uAttrValue.tFontEnc.eEncType = (IMTK_PB_CTRL_SBTL_FONT_ENC_TYPE_T)attrInt;
                  attrs[index].uAttrValue.tFontEnc.u2Param = attrShort;
                  MMP_LOG("####  nativeSetSubtitleAttr(): FONTENC  fontEncType = %d", attrInt);
                  MMP_LOG("####  nativeSetSubtitleAttr(): FONTENC  u2Param = %d", attrShort);
                  index++;
                }
                if(mask & SHOWHIDE)
                {
                   interField = env->GetFieldID( subtitleAttrClass, "showHide", "Z");
                   attrBool = (jboolean)env->GetBooleanField( subtitleAttr, interField);
                   attrs[index].eSbtlAttrType = IMTK_PB_CTRL_SBTL_ATTR_TYPE_SHOW_HIDE;
                   attrs[index].uAttrValue.tShowHide.bSbtlShow = attrBool;
                   MMP_LOG("####  Call nativeSetSubtitleAttr(): SHOWHIDE  showHide = %d", attrBool ? 1 : 0);
                   index++;
                }
                if(mask & FONTINFO)
                {
                   field = env->GetFieldID( subtitleAttrClass, "fontInfo", "Lcom/mediatek/media/SubtitleAttr$SubtitleFontInfo;");
                   object = (jobject)env->GetObjectField( subtitleAttr, field);
                   //inter class
                   interClass = env->FindClass("com/mediatek/media/SubtitleAttr$SubtitleFontInfo");
                   interField = env->GetFieldID( interClass, "fontSize", "I");
                   attrInt   = (jint)env->GetIntField( object, interField);
                   attrs[index].eSbtlAttrType = IMTK_PB_CTRL_SBTL_ATTR_TYPE_FNT_INFO;
                   attrs[index].uAttrValue.tFntInfo.eFontSize = (IMTK_PB_CTRL_FNT_SIZE)attrInt;
                   MMP_LOG("####  nativeSetSubtitleAttr(): FONTINFO  fontSize = %d", attrInt);
                   interField = env->GetFieldID( interClass, "fontStyle", "I");
                   attrInt   = (jint)env->GetIntField( object, interField);
                   MMP_LOG("####  nativeSetSubtitleAttr(): FONTINFO  fontStyle before = %d", attrInt);
                   switch(attrInt){
                        case FNT_STYLE_REGULAR     : { attrShort = IMTK_PB_CTRL_FNT_STYLE_REGULAR       ; break; }
                        case FNT_STYLE_ITALIC      : { attrShort = IMTK_PB_CTRL_FNT_STYLE_ITALIC        ; break; }
                        case FNT_STYLE_BOLD        : { attrShort = IMTK_PB_CTRL_FNT_STYLE_BOLD          ; break; }
                        case FNT_STYLE_UNDERLINE   : { attrShort = IMTK_PB_CTRL_FNT_STYLE_UNDERLINE     ; break; }
                        case FNT_STYLE_STRIKEOUT   : { attrShort = IMTK_PB_CTRL_FNT_STYLE_STRIKEOUT     ; break; }
                        case FNT_STYLE_OUTLINE     : { attrShort = IMTK_PB_CTRL_FNT_STYLE_OUTLINE       ; break; }
                        case FNT_STYLE_SHADOW_RIGHT: { attrShort = IMTK_PB_CTRL_FNT_STYLE_SHADOW_RIGHT  ; break; }
                        case FNT_STYLE_SHADOW_LEFT : { attrShort = IMTK_PB_CTRL_FNT_STYLE_SHADOW_LEFT   ; break; }
                        case FNT_STYLE_DEPRESSED   : { attrShort = IMTK_PB_CTRL_FNT_STYLE_DEPRESSED     ; break; }
                        case FNT_STYLE_RAISED      : { attrShort = IMTK_PB_CTRL_FNT_STYLE_RAISED        ; break; }
                        case FNT_STYLE_UNIFORM     : { attrShort = IMTK_PB_CTRL_FNT_STYLE_UNIFORM       ; break; }
                        case FNT_STYLE_BLURRED     : { attrShort = IMTK_PB_CTRL_FNT_STYLE_BLURRED       ; break; }
                   }
                   attrs[index].uAttrValue.tFntInfo.eFontStyle = (IMTK_PB_CTRL_FNT_STYLE)attrShort;
                   MMP_LOG("####  nativeSetSubtitleAttr(): FONTINFO  fontStyle end = %d", attrShort);
                   interField = env->GetFieldID( interClass, "cmapEncoding", "I");
                   attrInt   = (jint)env->GetIntField( object, interField);
                   MMP_LOG("####  nativeSetSubtitleAttr(): FONTINFO  cmapEncoding before = %d", attrInt);
                   switch(attrInt){
                        case CMAP_ENC_NONE          : { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_NONE            ; break; }
                        case CMAP_ENC_MS_SYMBOL     : { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_MS_SYMBOL       ; break; }
                        case CMAP_ENC_UNICODE       : { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_UNICODE         ; break; }
                        case CMAP_ENC_SJIS          : { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_SJIS            ; break; }
                        case CMAP_ENC_GB2312        : { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_GB2312          ; break; }
                        case CMAP_ENC_BIG5          : { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_BIG5            ; break; }
                        case CMAP_ENC_WANSUNG       : { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_WANSUNG         ; break; }
                        case CMAP_ENC_JOHAB         : { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_JOHAB           ; break; }
                        case CMAP_ENC_ADOBE_STANDARD: { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_ADOBE_STANDARD  ; break; }
                        case CMAP_ENC_ADOBE_EXPERT  : { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_ADOBE_EXPERT    ; break; }
                        case CMAP_ENC_ADOBE_CUSTOM  : { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_ADOBE_CUSTOM    ; break; }
                        case CMAP_ENC_ADOBE_LATIN_1 : { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_ADOBE_LATIN_1   ; break; }
                        case CMAP_ENC_OLD_LATIN_2   : { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_OLD_LATIN_2     ; break; }
                        case CMAP_ENC_APPLE_ROMAN   : { fontEncoding = IMTK_PB_CTRL_CMAP_ENC_APPLE_ROMAN     ; break; }

                    }
                    attrs[index].uAttrValue.tFntInfo.eFontCmap = (IMTK_PB_CTRL_CMAP_ENCODING)fontEncoding;
                    MMP_LOG("####  nativeSetSubtitleAttr(): FONTINFO  cmapEncoding end = %d", fontEncoding);
                    interField = env->GetFieldID( interClass, "fontName", "Ljava/lang/String;");
                    attrString = (jstring)env->GetObjectField( object, interField);
                    attrCharPoint = env->GetStringUTFChars(attrString,NULL);
                    MMP_LOG("####  nativeSetSubtitleAttr(): FONTINFO  fontName = %s", attrCharPoint);
                    strcpy(attrs[index].uAttrValue.tFntInfo.acFontName , attrCharPoint);
                    env->ReleaseStringUTFChars(attrString,attrCharPoint);

                    interField = env->GetFieldID( interClass, "width", "S");
                    attrShort  = (jshort)env->GetShortField( object, interField);
                    attrs[index].uAttrValue.tFntInfo.i2Width = attrShort;
                    MMP_LOG("####  nativeSetSubtitleAttr(): FONTINFO  width = %d", attrShort);

                    interField = env->GetFieldID( interClass, "customSize", "B");
                    attrChar  = (jbyte)env->GetByteField( object, interField);
                    attrs[index].uAttrValue.tFntInfo.u1CustomSize = attrChar;
                    MMP_LOG("####  nativeSetSubtitleAttr(): FONTINFO  customSize = %d", attrChar);
                    index++;
                 }
                 if(mask & BGCOLOR)
                 {
                    field = env->GetFieldID( subtitleAttrClass, "bgColor", "Lcom/mediatek/media/SubtitleAttr$SubtitleColor;");
                    object = (jobject)env->GetObjectField( subtitleAttr, field);
                    //inter class
                    interClass = env->FindClass("com/mediatek/media/SubtitleAttr$SubtitleColor");
                    interField = env->GetFieldID( interClass, "a", "B");
                    attrChar = (jbyte)env->GetByteField( object, interField);
                    attrs[index].eSbtlAttrType = IMTK_PB_CTRL_SBTL_ATTR_TYPE_BKG_CLR;
                    attrs[index].uAttrValue.tBkgClr.u1A = attrChar;
                    MMP_LOG("####  nativeSetSubtitleAttr(): BGCOLOR  a = %d", attrChar);
                    interField = env->GetFieldID( interClass, "r", "B");
                    attrChar = (jbyte)env->GetByteField( object, interField);
                    attrs[index].uAttrValue.tBkgClr.u1.u1R= attrChar;
                    MMP_LOG("####  nativeSetSubtitleAttr(): BGCOLOR  r = %d", attrChar);
                    interField = env->GetFieldID( interClass, "g", "B");
                    attrChar = (jbyte)env->GetByteField( object, interField);
                    attrs[index].uAttrValue.tBkgClr.u2.u1G = attrChar;
                    MMP_LOG("####  nativeSetSubtitleAttr(): BGCOLOR  g = %d", attrChar);
                    interField = env->GetFieldID( interClass, "b", "B");
                    attrChar = (jbyte)env->GetByteField( object, interField);
                    attrs[index].uAttrValue.tBkgClr.u3.u1B = attrChar;
                    MMP_LOG("####  nativeSetSubtitleAttr(): BGCOLOR  b = %d", attrChar);
                    index++;
                 }
                 if(mask & TEXTCOLOR)
                 {
                    field = env->GetFieldID( subtitleAttrClass, "textColor", "Lcom/mediatek/media/SubtitleAttr$SubtitleColor;");
                    object = (jobject)env->GetObjectField( subtitleAttr, field);
                    //inter class
                    interClass = env->FindClass("com/mediatek/media/SubtitleAttr$SubtitleColor");
                    interField = env->GetFieldID( interClass, "a", "B");
                    attrChar = (jbyte)env->GetByteField( object, interField);
                    attrs[index].eSbtlAttrType = IMTK_PB_CTRL_SBTL_ATTR_TYPE_TXT_CLR;
                    attrs[index].uAttrValue.tBkgClr.u1A = attrChar;
                    MMP_LOG("####  nativeSetSubtitleAttr(): TEXTCOLOR  a = %d", attrChar);
                    interField = env->GetFieldID( interClass, "r", "B");
                    attrChar = (jbyte)env->GetByteField( object, interField);
                    attrs[index].uAttrValue.tBkgClr.u1.u1R= attrChar;
                    MMP_LOG("####  nativeSetSubtitleAttr(): TEXTCOLOR  r = %d", attrChar);
                    interField = env->GetFieldID( interClass, "g", "B");
                    attrChar = (jbyte)env->GetByteField( object, interField);
                    attrs[index].uAttrValue.tBkgClr.u2.u1G = attrChar;
                    MMP_LOG("####  nativeSetSubtitleAttr(): TEXTCOLOR  g = %d", attrChar);
                    interField = env->GetFieldID( interClass, "b", "B");
                    attrChar = (jbyte)env->GetByteField( object, interField);
                    attrs[index].uAttrValue.tBkgClr.u3.u1B = attrChar;
                    MMP_LOG("####  nativeSetSubtitleAttr(): TEXTCOLOR  b = %d", attrChar);
                    index++;
                 }
                 if(mask & BODERTYPE)
                 {
                    field = env->GetFieldID( subtitleAttrClass, "boderType", "I");
                    attrInt = (jint)env->GetIntField( subtitleAttr, field);
                    attrs[index].eSbtlAttrType = IMTK_PB_CTRL_SBTL_ATTR_TYPE_BDR_TYPE;
                    attrs[index].uAttrValue.eBdrType = (IMTK_PB_CTRL_SBTL_BDR_TYPE_T)attrInt;
                    MMP_LOG("####  nativeSetSubtitleAttr(): BODERTYPE  boderType = %d", attrInt);
                    index++;
                 }
                 if(mask & BORDERWIDTH)
                 {
                    field = env->GetFieldID( subtitleAttrClass, "borderWidth", "I");
                    attrInt = (jint)env->GetIntField( subtitleAttr, field);
                    attrs[index].eSbtlAttrType = IMTK_PB_CTRL_SBTL_ATTR_TYPE_BDR_WIDTH;
                    attrs[index].uAttrValue.u4BdrWidth = attrInt;
                    MMP_LOG("####  nativeSetSubtitleAttr(): BORDERWIDTH  borderWidth = %d", attrInt);
                    index++;
                 }
                 if(mask & ROLLTYPE)
                 {
                    field = env->GetFieldID( subtitleAttrClass, "rollType", "I");
                    attrInt = (jint)env->GetIntField( subtitleAttr, field);
                    attrs[index].eSbtlAttrType = IMTK_PB_CTRL_SBTL_ATTR_TYPE_ROLL_TYPE;
                    attrs[index].uAttrValue.eRollType = (IMTK_PB_CTRL_SBTL_ROLL_TYPE_T)attrInt;
                    MMP_LOG("####  nativeSetSubtitleAttr(): ROLLTYPE  rollType = %d", attrInt);
                    index++;
                 }
                 if(mask & DISPLAYRECT)
                 {
                    field = env->GetFieldID( subtitleAttrClass, "displayRect", "Lcom/mediatek/media/SubtitleAttr$SubtitleDisplayRect;");
                    object = (jobject)env->GetObjectField( subtitleAttr, field);
                    //inter class
                    interClass = env->FindClass("com/mediatek/media/SubtitleAttr$SubtitleDisplayRect");
                    interField = env->GetFieldID( interClass, "u4X", "I");
                    attrInt   = (jint)env->GetIntField( object, interField);
                    attrs[index].eSbtlAttrType = IMTK_PB_CTRL_SBTL_ATTR_TYPE_DISP_RECT;
                    attrs[index].uAttrValue.tDispRect.u4X = attrInt;
                    MMP_LOG("####  nativeSetSubtitleAttr(): DISPLAYRECT  u4X = %d", attrInt);
                    interField = env->GetFieldID( interClass, "u4Y", "I");
                    attrInt   = (jint)env->GetIntField( object, interField);
                    attrs[index].uAttrValue.tDispRect.u4Y = attrInt;
                    MMP_LOG("####  nativeSetSubtitleAttr(): DISPLAYRECT  u4Y = %d", attrInt);
                    interField = env->GetFieldID( interClass, "u4W", "I");
                    attrInt   = (jint)env->GetIntField( object, interField);
                    attrs[index].uAttrValue.tDispRect.u4W = attrInt;
                    MMP_LOG("####  nativeSetSubtitleAttr(): DISPLAYRECT  u4W = %d", attrInt);
                    interField = env->GetFieldID( interClass, "u4H", "I");
                    attrInt   = (jint)env->GetIntField( object, interField);
                    attrs[index].uAttrValue.tDispRect.u4H = attrInt;
                    MMP_LOG("####  nativeSetSubtitleAttr(): DISPLAYRECT  u4H = %d", attrInt);
                    index++;
                 }

                 for(int i = 0; i < index -1; i++) {
                    attrs[i].ptNext = &attrs[i+1];
                 }
               }
            e_return = IMtkPb_Ctrl_Set(envInfo->hHandle, IMTK_PB_CTRL_SET_TYPE_SBTL_ATTR, attrs, &size);
            if (e_return != IMTK_PB_ERROR_CODE_OK)
            {
                LOGE("####  nativeSetSubtitleTrack(): IMtkPb_Ctrl_Set(), e_return = %d\n", e_return);
            }

            LOGI("####  nativeSetSubtitleAttr(): Leave\n");
     }

/*
 * Class:     com_mediatek_media_MtkMediaPlayer
 * Method:    nativeSetTS
 * Signature: (IS)I
*/
JNIEXPORT jint JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeSetTS
 (JNIEnv * env, jobject thiz, jint handle, jshort index)
{
	INPUTSTREAM_ENV_T*    envInfo = (INPUTSTREAM_ENV_T *)handle;
	IMTK_PB_ERROR_CODE_T  e_return = IMTK_PB_ERROR_CODE_OK;
	SIZE_T                size = sizeof(index);

	LOGI("nativeTS(): Enter\n");

	LOGI("nativeTS(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

	LOGI("nativeTS(): index = %d, size = %d\n", index, (int)size);

	e_return = IMtkPb_Ctrl_SetProgram(envInfo->hHandle, IMTK_PB_CTRL_SET_TYPE_TS_PROGRAM_IDX, &index, &size);
	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
	    LOGE("nativeSetSubtitleTrack(): IMtkPb_Ctrl_SetProgram(), e_return = %d\n", e_return);
	}

	LOGI("nativeTS(): Leave\n");

	return (jint)e_return;
}

/*
 * Class:     com_mediatek_media_MtkMediaPlayer
 * Method:    nativeSetPcmMediaInfo
 * Signature: (ILcom/mediatek/media/PcmMediaInfo;)V
 */
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeSetPcmMediaInfo
 (JNIEnv * env, jobject thiz, jint handle, jobject pcmMediaInfo)
{
	INPUTSTREAM_ENV_T*             envInfo = (INPUTSTREAM_ENV_T *)handle;
	IMTK_PB_ERROR_CODE_T           e_return;
	IMTK_PB_CTRL_SET_MEDIA_INFO_T  mediaInfo;
	jclass                         pcmClass;
	jclass                         interClass;
	jfieldID                       field;
	jfieldID                       interField;
	jobject                        object;
	int                            tempInt;
	long                           tempLong;
	short                          tempShort;

	LOGI("nativeSetPcmMediaInfo(): Enter\n");

	LOGI("nativeSetPcmMediaInfo(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

	pcmClass = env->FindClass("com/mediatek/media/PcmMediaInfo");
	field = env->GetFieldID( pcmClass, "mediaType", "I");
	tempInt = (jint)env->GetIntField( pcmMediaInfo, field);
	mediaInfo.eMediaType = (IMTK_PB_CTRL_MEDIA_TYPE_T)tempInt;

	field = env->GetFieldID( pcmClass, "totalDuration", "I");
	tempInt = (jint)env->GetIntField( pcmMediaInfo, field);
	mediaInfo.u4TotalDuration = tempInt;

	field = env->GetFieldID( pcmClass, "size", "J");
	tempLong = (jlong)env->GetLongField( pcmMediaInfo, field);
	mediaInfo.u8Size = tempLong;

	field = env->GetFieldID( pcmClass, "fgSynchronized", "I");
	tempInt = (jint)env->GetIntField( pcmMediaInfo, field);
	mediaInfo.fgSynchronized = (bool)tempInt;

	field = env->GetFieldID( pcmClass, "audioCodec", "I");
	tempInt = (jint)env->GetIntField( pcmMediaInfo, field);
	mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.eAudEnc = (IMTK_PB_CTRL_AUD_ENC_T)tempInt;

	field = env->GetFieldID( pcmClass, "audioPcmInfo", "Lcom/mediatek/media/PcmMediaInfo$AudioPcmInfo;");
	object = (jobject)env->GetObjectField( pcmMediaInfo, field);
	//inter class
	interClass = env->FindClass("com/mediatek/media/PcmMediaInfo$AudioPcmInfo");
	interField = env->GetFieldID( interClass, "pcm_type", "I");
	tempInt    = (jint)env->GetIntField( object, interField);
	mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.e_pcm_type = (IMTK_PB_CTRL_AUD_PCM_TYPE_T)tempInt;

	interField = env->GetFieldID( interClass, "channelNumber", "I");
	tempInt    = (jint)env->GetIntField( object, interField);
	mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.eNumCh = (IMTK_PB_CTRL_AUD_CH_NUM_T)tempInt;

	interField = env->GetFieldID( interClass, "sampleRate", "I");
	tempInt    = (jint)env->GetIntField( object, interField);
	mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.eSampleRate = (IMTK_PB_CTRL_AUD_SAMPLE_RATE_T)tempInt;

	interField = env->GetFieldID( interClass, "blockAlign", "S");
	tempShort    = (jshort)env->GetShortField( object, interField);
	mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.u2BlockAlign = tempShort;

	interField = env->GetFieldID( interClass, "bitsPerSample", "I");
	tempInt    = (jint)env->GetIntField( object, interField);
	mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.eBitsPerSample = (IMTK_PB_CTRL_AUD_PCM_BIT_DEPTH_T)tempInt;

	interField = env->GetFieldID( interClass, "bigEndian", "I");
	tempInt    = (jint)env->GetIntField( object, interField);
	mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.fgBigEndian = (bool)tempInt;

	LOGI("nativeSetPcmMediaInfo(): mediaInfo.eMediaType = %d\n", (int)mediaInfo.eMediaType);
	LOGI("nativeSetPcmMediaInfo(): mediaInfo.u4TotalDuration = %d\n", mediaInfo.u4TotalDuration);
	LOGI("nativeSetPcmMediaInfo(): mediaInfo.u8Size = %lld\n", mediaInfo.u8Size);
	LOGI("nativeSetPcmMediaInfo(): mediaInfo.fgSynchronized = %d\n", mediaInfo.fgSynchronized);
	LOGI("nativeSetPcmMediaInfo(): mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.eAudEnc = %d\n", (int)mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.eAudEnc);
	LOGI("nativeSetPcmMediaInfo(): mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.e_pcm_type = %d\n", (int)mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.e_pcm_type);
	LOGI("nativeSetPcmMediaInfo(): mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.eNumCh = %d\n", (int)mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.eNumCh);
	LOGI("nativeSetPcmMediaInfo(): mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.eSampleRate = %d\n", (int)mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.eSampleRate);
	LOGI("nativeSetPcmMediaInfo(): mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.u2BlockAlign = %d\n", mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.u2BlockAlign);
	LOGI("nativeSetPcmMediaInfo(): mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.eBitsPerSample = %d\n", (int)mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.eBitsPerSample);
	LOGI("nativeSetPcmMediaInfo(): mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.fgBigEndian = %d\n", mediaInfo.uFormatInfo.tAudioEsInfo.tAudInfo.uAudCodecInfo.t_pcm_info.fgBigEndian);

	e_return = IMtkPb_Ctrl_SetMediaInfo(envInfo->hHandle, &mediaInfo);
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGE("nativeSetPcmMediaInfo(): IMtkPb_Ctrl_SetMediaInfo(), e_return = %d\n", e_return);
    }

    LOGI("nativeSetPcmMediaInfo(): Leave\n");
}


/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeSetURL
* Signature: (IIjava/lang/String;)V
*/
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeSetURL
	(JNIEnv * env, jobject thiz, jint handle, jint urlType, jstring url)
{
	INPUTSTREAM_ENV_T*    envInfo = (INPUTSTREAM_ENV_T *)handle;
	IMTK_PB_ERROR_CODE_T  e_return;
	uint8_t*              stringURL;
	const char*           charURL;

	LOGI("nativeSetURL(): Enter\n");

	LOGI("nativeSetURL(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

	MMP_LOG("nativeSetURL(): urlType = %d, url = %d\n", urlType, url);

	charURL = env->GetStringUTFChars(url,NULL);
	MMP_LOG("nativeSetURL(): charURL = ###%s###\n", charURL);
	stringURL  = (uint8_t*)(charURL);

	LOGI("nativeSetURL(): stringURL = ###**%s**###\n", stringURL);

	e_return = IMtkPb_Ctrl_SetURL(envInfo->hHandle, (IMTK_PB_CTRL_URL_TYPE_T)urlType , stringURL);
	env->ReleaseStringUTFChars(url,charURL);

	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
		LOGE("nativeSetURL(): IMtkPb_Ctrl_SetURL(), e_return = %d\n", e_return);
	}

	LOGI("nativeSetURL(): Leave\n");
}


/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeSetByteSeek
* Signature: (IJ)V
*/
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeSetByteSeek
 (JNIEnv * env, jobject thiz, jint handle, jlong position)
{
	INPUTSTREAM_ENV_T*    envInfo = (INPUTSTREAM_ENV_T *)handle;
	IMTK_PB_ERROR_CODE_T  e_return;

	LOGI("nativeSetByteSeek(): Enter\n");

	LOGI("nativeSetByteSeek(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

	LOGI("nativeSetByteSeek(): position = %lld\n", position);

	e_return = IMtkPb_Ctrl_ByteSeek(envInfo->hHandle, position);
	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
		LOGE("nativeSetByteSeek(): IMtkPb_Ctrl_ByteSeek(), e_return = %d\n", e_return);
	}

	LOGI("nativeSetByteSeek(): Leave\n");
}


/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeSetByteSeekPts
* Signature: (IJJ)V
*/
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeSetByteSeekPts
 (JNIEnv * env, jobject thiz, jint handle, jlong position, jlong pts)
{
	INPUTSTREAM_ENV_T*    envInfo = (INPUTSTREAM_ENV_T *)handle;
	IMTK_PB_ERROR_CODE_T  e_return;

	LOGI("nativeSetByteSeekPts(): Enter\n");

	LOGI("nativeSetByteSeekPts(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

	LOGI("nativeSetByteSeekPts(): position = %lld, pts = %lld\n", position, pts);

	e_return = IMtkPb_Ctrl_ByteSeekPts(envInfo->hHandle, position , pts);
	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
		LOGE("nativeSetByteSeekPts(): IMtkPb_Ctrl_ByteSeekPts(), e_return = %d\n", e_return);
	}

	LOGI("nativeSetByteSeekPts(): Leave\n");
}


/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeSet3DVideoMode
* Signature: (II)V
*/
JNIEXPORT void JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeSet3DVideoMode
 (JNIEnv * env, jobject thiz, jint handle, jint videoMode)
{
	INPUTSTREAM_ENV_T*             envInfo = (INPUTSTREAM_ENV_T *)handle;
	IMTK_PB_ERROR_CODE_T           e_return;
	IMTK_PB_CTRL_SET_EXTRA_INFO_T  extraInfo;

	LOGI("nativeSet3DVideoMode(): Enter\n");

	LOGI("nativeSet3DVideoMode(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

	LOGI("nativeSet3DVideoMode(): videoMode = %d\n", videoMode);

	extraInfo.eExtraInfoType = IMTK_PB_CTRL_EXTRA_INFO_TYPE_3D_VIDEO;
	extraInfo.uExtraInfoParam.e3dVideoType = (IMTK_PB_CTRL_3D_VIDEO_TYPE_T)videoMode;
	e_return = IMtkPb_Ctrl_SetExtraInfo(envInfo->hHandle, &extraInfo);
	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
		LOGE("nativeSet3DVideoMode(): IMtkPb_Ctrl_SetExtraInfo(), e_return = %d\n", e_return);
	}

	LOGI("nativeSet3DVideoMode(): Leave\n");
}


/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeGetTSInfo
* Signature: (II)Lcom/mediatek/media/TsInfo;
*/
JNIEXPORT jobject JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeGetTSInfo
 (JNIEnv * env, jobject thiz, jint handle, jint length)
{
	INPUTSTREAM_ENV_T*                 envInfo = (INPUTSTREAM_ENV_T *)handle;
	IMTK_PB_ERROR_CODE_T               e_return;
	IMTK_PB_CTRL_TS_SINGLE_PAT_INFO_T  ptTSInfo;
	jint                               ret;
	jclass                             playerClass;
	jclass                             tsInfoClass;
	jclass                             pmtInfoClass;
	jclass                             streamInfoClass;
	jmethodID                          playerReadTsInfo;
	jmethodID                          tsInfoMethod;
	jmethodID                          pmtInfoMethod;
	jmethodID                          streamInfoMethod;
	jobject                            tsInfoObj;
	jobject                            pmtInfoObj;
	jobject                            streamInfoObj;
	jfieldID                           playerTsInfoBuffer;
	int                                bufferSize;
	jbyteArray                         array;
	uint8_t*                           buffer;

	LOGI("nativeGetTSInfo(): Enter\n");

	LOGI("nativeGetTSInfo(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

	playerClass = env->FindClass("com/mediatek/media/MtkMediaPlayer");
	jfieldID PlayerSize = env->GetFieldID(playerClass, "mStreamSize", "J");
	int      size = env->GetLongField(thiz, PlayerSize);//inputstream total size

	if(size > 0 && length > size)
	{
		bufferSize = size;
	}
	else if(length < 10 * 188)
	{
		bufferSize = 10 * 188;
	}
	else
	{
		bufferSize = length;
	}
	playerReadTsInfo = env->GetMethodID(playerClass,"readTsInfo","(I)I");
	ret = env->CallIntMethod(thiz, playerReadTsInfo,bufferSize);

	playerTsInfoBuffer = env->GetFieldID(playerClass, "mTsInfoBuffer", "[B");
	array = (jbyteArray)env->GetObjectField(thiz, playerTsInfoBuffer);
	buffer = (uint8_t*)env->GetByteArrayElements(array, NULL);

	MMP_LOG("nativeGetTSInfo(): inputstream total size = %d\n", size);
	MMP_LOG("nativeGetTSInfo(): length = %d\n", length);
	MMP_LOG("nativeGetTSInfo(): bufferSize = %d\n", bufferSize);
	MMP_LOG("nativeGetTSInfo(): buffer = D:%d   X:%x\n", (int)buffer,buffer);

	e_return = IMtkPb_Ctrl_GetTSInfo(envInfo->hHandle, buffer, bufferSize, &ptTSInfo);
	env->ReleaseByteArrayElements(array, (jbyte*)buffer, 0);

	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
		LOGE("nativeGetTSInfo: IMtkPb_Ctrl_GetTSInfo(), e_return = %d\n", e_return);
		jniThrowException(env,"java/lang/IllegalStateException","GetTSInfo_FAIL");
		return NULL;
	}
	else
	{
		jclass    listClass = env->FindClass("java/util/ArrayList");
		jmethodID listCon = env->GetMethodID(listClass, "<init>", "()V");
		jmethodID listAdd = env->GetMethodID(listClass, "add","(Ljava/lang/Object;)Z");
		jobject   listPmtInfo;
		jobject   listStreamInfo;

		listPmtInfo = env->NewObject(listClass, listCon);
		tsInfoClass = env->FindClass("com/mediatek/media/TsInfo");
		tsInfoMethod = env->GetMethodID(tsInfoClass, "<init>", "(SSBLjava/util/ArrayList;)V");
		LOGI("nativeGetTSInfo(): ptTSInfo  ui2_packet_size = %d\n", ptTSInfo.ui2_packet_size);
		LOGI("nativeGetTSInfo(): ptTSInfo  ui2_pat_pid = %d\n", ptTSInfo.ui2_pat_pid);
		LOGI("nativeGetTSInfo(): ptTSInfo  ui1_pmt_num = %d\n", ptTSInfo.ui1_pmt_num);

		for (int i = 0; i < ptTSInfo.ui1_pmt_num; i++)
		{
			listStreamInfo = env->NewObject(listClass, listCon);
			pmtInfoClass = env->FindClass("com/mediatek/media/TsInfo$PmtInfo");
			pmtInfoMethod = env->GetMethodID(pmtInfoClass, "<init>", "(ZSBLjava/util/ArrayList;)V");

			MMP_LOG("nativeGetTSInfo(): pmtInfoObj  i = %d,  fg_init = %d\n", i, ptTSInfo.at_pmt_info[i].fg_init);
			MMP_LOG("nativeGetTSInfo(): pmtInfoObj  i = %d,  pmt_pid = %d\n", i, ptTSInfo.at_pmt_info[i].ui2_pmt_pid);
			MMP_LOG("nativeGetTSInfo(): pmtInfoObj  i = %d,  strm_num = %d\n", i, ptTSInfo.at_pmt_info[i].ui1_strm_num);
			MMP_LOG("nativeGetTSInfo(): pmtInfoObj  at_stream_info_list[j] \n");

			for (int j = 0; j < ptTSInfo.at_pmt_info[i].ui1_strm_num; j++)
			{
				streamInfoClass = env->FindClass("com/mediatek/media/TsInfo$StreamInfo");
				streamInfoMethod = env->GetMethodID(streamInfoClass, "<init>", "(SS)V");
				streamInfoObj = env->NewObject(streamInfoClass, streamInfoMethod,ptTSInfo.at_pmt_info[i].at_stream_info_list[j].ui2_strm_pid,ptTSInfo.at_pmt_info[i].at_stream_info_list[j].ui2_strm_type);
				env->CallBooleanMethod(listStreamInfo,listAdd, streamInfoObj);
				MMP_LOG("ui1_strm_num  j = %d\n", j);
			}

			pmtInfoObj = env->NewObject(pmtInfoClass, pmtInfoMethod, ptTSInfo.at_pmt_info[i].fg_init, ptTSInfo.at_pmt_info[i].ui2_pmt_pid, ptTSInfo.at_pmt_info[i].ui1_strm_num, listStreamInfo);
			env->CallBooleanMethod(listPmtInfo,listAdd, pmtInfoObj);

		}

		tsInfoObj = env->NewObject(tsInfoClass, tsInfoMethod, ptTSInfo.ui2_packet_size, ptTSInfo.ui2_pat_pid, ptTSInfo.ui1_pmt_num, listPmtInfo);

		LOGI("nativeGetTSInfo(): Leave\n");

      return tsInfoObj;
  }
}


/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeGetMediaInfo
* Signature: (I)Lcom/mediatek/media/MediaInfo;
*/
JNIEXPORT jobject JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeGetMediaInfo
	(JNIEnv * env, jobject thiz, jint handle)
{

    INPUTSTREAM_ENV_T*         envInfo = (INPUTSTREAM_ENV_T *)handle;
    IMTK_PB_ERROR_CODE_T       e_return;
    IMTK_PB_CTRL_MEDIA_INFO_T  ptInfo;

    LOGI("nativeGetMediaInfo(): Enter\n");

    LOGI("nativeGetMediaInfo(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

    e_return = IMtkPb_Ctrl_Get(envInfo->hHandle,IMTK_PB_CTRL_GET_TYPE_MEDIA_INFO, &ptInfo, sizeof(IMTK_PB_CTRL_MEDIA_INFO_T));
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGE("nativeGetMediaInfo(): IMtkPb_Ctrl_Get(), e_return = %d\n", e_return);
    }

    jclass mediaInfoClass = env->FindClass("com/mediatek/media/MediaInfo");
    jmethodID cons = env->GetMethodID( mediaInfoClass, "<init>", "(IIJISSSI)V");
    jobject mediaInfoObj = env->NewObject( mediaInfoClass, cons, (jint)ptInfo.eMediaType,
        ptInfo.u4TotalDuration, ptInfo.u8Size, ptInfo.u4AvgBitrate,
        ptInfo.u2VideoTrackNum, ptInfo.u2AudioTrackNum, ptInfo.u2SubtlTrackNum, (jint)ptInfo.u1ProgramNum);

    LOGI("nativeGetMediaInfo(): Leave\n");

    return mediaInfoObj;
}


   /*
    * Class:     com_mediatek_media_MtkMediaPlayer
    * Method:    nativeGetMetaDataInfo
    * Signature: (II)Ljava/lang/Object;
    */
    JNIEXPORT jobject JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeGetMetaDataInfo
        (JNIEnv * env, jobject thiz, jint handle, jint metaType)
    {

        INPUTSTREAM_ENV_T*             prMtkMpContext = (INPUTSTREAM_ENV_T *)handle;
        IMTK_PB_ERROR_CODE_T           e_return;
        IMTK_PB_CTRL_META_DATA_INFO_T  pt_meta_info;

        if (!prMtkMpContext)
        {
            LOGE("nativeGetMetaDataInfo(): error happen!\n");
            return NULL;
        }

        LOGI("nativeGetMetaDataInfo(): Enter, player instance = %d", (int)prMtkMpContext);

        LOGI("nativeGetMetaDataInfo(): player instance = %d, CMPB handle = %x", (int)prMtkMpContext, (uint32_t)prMtkMpContext->hHandle);

        switch(metaType){

             case META_TYPE_TITLE:
                {

                    UTF16_T*   buffer = (UTF16_T*)malloc(64 * sizeof(UTF16_T));
                    if (!buffer)
                    {
                        LOGI("####  nativeGetMetaDataInfo(): META_TYPE_TITLE, memory allocated failed\n");
                        return NULL;
                    }

                    memset(buffer, 0, 64 * sizeof(UTF16_T));
                    pt_meta_info.e_meta_type = IMTK_PB_CTRL_META_TYPE_TITLE;
                    pt_meta_info.pv_buf = buffer;
                    pt_meta_info.ui2_buf_size = 64;
                    e_return = IMtkPb_Ctrl_Get(prMtkMpContext->hHandle,IMTK_PB_CTRL_GET_TYPE_META_DATA, &pt_meta_info, sizeof(IMTK_PB_CTRL_META_DATA_INFO_T));
                    jshortArray array = env->NewShortArray(pt_meta_info.ui2_buf_size);
                    env->SetShortArrayRegion(array, 0, pt_meta_info.ui2_buf_size, (const jshort*)pt_meta_info.pv_buf);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_TITLE: IMtkPb_Ctrl_Get e_return = %d",e_return);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_TITLE: TITLE = %d",array);
                    /*
                    for(int i=0;i<pt_meta_info.ui2_buf_size;i++)
                    {
                        MMP_LOG("####  Call nativeGetMetaDataInfo: title[%d] = %d",i,*((UTF16_T*)pt_meta_info.pv_buf + i));
                    }
                    */
                    free(buffer);
                    return array;

                }

             case META_TYPE_DIRECTOR:
                {
                    UTF16_T*   buffer = (UTF16_T*)malloc(64 * sizeof(UTF16_T));
                    if (!buffer)
                    {
                        LOGI("####  nativeGetMetaDataInfo(): META_TYPE_DIRECTOR, memory allocated failed\n");
                        return NULL;
                    }

                    memset(buffer, 0, 64 * sizeof(UTF16_T));
                    pt_meta_info.e_meta_type = IMTK_PB_CTRL_META_TYPE_DIRECTOR;
                    pt_meta_info.pv_buf = buffer;
                    pt_meta_info.ui2_buf_size = 64;
                    e_return = IMtkPb_Ctrl_Get(prMtkMpContext->hHandle,IMTK_PB_CTRL_GET_TYPE_META_DATA, &pt_meta_info, sizeof(IMTK_PB_CTRL_META_DATA_INFO_T));
                    jshortArray array = env->NewShortArray(pt_meta_info.ui2_buf_size);
                    env->SetShortArrayRegion(array, 0, pt_meta_info.ui2_buf_size, (const jshort*)pt_meta_info.pv_buf);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_DIRECTOR: IMtkPb_Ctrl_Get e_return = %d",e_return);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_DIRECTOR: DIRECTOR = %d",array);
                    free(buffer);
                    return array;

                }
             case META_TYPE_COPYRIGHT:
                {
                    UTF16_T*   buffer = (UTF16_T*)malloc(64 * sizeof(UTF16_T));
                    if (!buffer)
                    {
                        LOGI("####  nativeGetMetaDataInfo(): META_TYPE_COPYRIGHT, memory allocated failed\n");
                        return NULL;
                    }

                    memset(buffer, 0, 64 * sizeof(UTF16_T));
                    pt_meta_info.e_meta_type = IMTK_PB_CTRL_META_TYPE_COPYRIGHT;
                    pt_meta_info.pv_buf = buffer;
                    pt_meta_info.ui2_buf_size = 64;
                    e_return = IMtkPb_Ctrl_Get(prMtkMpContext->hHandle,IMTK_PB_CTRL_GET_TYPE_META_DATA, &pt_meta_info, sizeof(IMTK_PB_CTRL_META_DATA_INFO_T));
                    jshortArray array = env->NewShortArray(pt_meta_info.ui2_buf_size);
                    env->SetShortArrayRegion(array, 0, pt_meta_info.ui2_buf_size, (const jshort*)pt_meta_info.pv_buf);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_COPYRIGHT: IMtkPb_Ctrl_Get e_return = %d",e_return);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_COPYRIGHT: COPYRIGHT = %d",array);
                    free(buffer);
                    return array;

                }
             case META_TYPE_YEAR:
                {
                    UTF16_T*   buffer = (UTF16_T*)malloc(64 * sizeof(UTF16_T));
                    if (!buffer)
                    {
                        LOGI("####  nativeGetMetaDataInfo(): META_TYPE_YEAR, memory allocated failed\n");
                        return NULL;
                    }

                    memset(buffer, 0, 64 * sizeof(UTF16_T));
                    pt_meta_info.e_meta_type = IMTK_PB_CTRL_META_TYPE_YEAR;
                    pt_meta_info.pv_buf = buffer;
                    pt_meta_info.ui2_buf_size = 64;
                    e_return = IMtkPb_Ctrl_Get(prMtkMpContext->hHandle,IMTK_PB_CTRL_GET_TYPE_META_DATA, &pt_meta_info, sizeof(IMTK_PB_CTRL_META_DATA_INFO_T));
                    jshortArray array = env->NewShortArray(pt_meta_info.ui2_buf_size);
                    env->SetShortArrayRegion(array, 0, pt_meta_info.ui2_buf_size, (const jshort*)pt_meta_info.pv_buf);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_YEAR: IMtkPb_Ctrl_Get e_return = %d",e_return);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_YEAR: YEAR = %d",array);
                    free(buffer);
                    return array;

                }

             case META_TYPE_GENRE:
                {
                    UTF16_T*   buffer = (UTF16_T*)malloc(64 * sizeof(UTF16_T));
                    if (!buffer)
                    {
                        LOGI("####  nativeGetMetaDataInfo(): META_TYPE_GENRE, memory allocated failed\n");
                        return NULL;
                    }

                    memset(buffer, 0, 64 * sizeof(UTF16_T));
                    pt_meta_info.e_meta_type = IMTK_PB_CTRL_META_TYPE_GENRE;
                    pt_meta_info.pv_buf = buffer;
                    pt_meta_info.ui2_buf_size = 64;
                    e_return = IMtkPb_Ctrl_Get(prMtkMpContext->hHandle,IMTK_PB_CTRL_GET_TYPE_META_DATA, &pt_meta_info, sizeof(IMTK_PB_CTRL_META_DATA_INFO_T));
                    jshortArray array = env->NewShortArray(pt_meta_info.ui2_buf_size);
                    env->SetShortArrayRegion(array, 0, pt_meta_info.ui2_buf_size, (const jshort*)pt_meta_info.pv_buf);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_GENRE: IMtkPb_Ctrl_Get e_return = %d",e_return);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_GENRE: GENRE = %d",array);
                    free(buffer);
                    return array;

                }
             case META_TYPE_ARTIST:
                {
                    UTF16_T*   buffer = (UTF16_T*)malloc(64 * sizeof(UTF16_T));
                    if (!buffer)
                    {
                        LOGI("####  nativeGetMetaDataInfo(): META_TYPE_ARTIST, memory allocated failed\n");
                        return NULL;
                    }

                    memset(buffer, 0, 64 * sizeof(UTF16_T));
                    pt_meta_info.e_meta_type = IMTK_PB_CTRL_META_TYPE_ARTIST;
                    pt_meta_info.pv_buf = buffer;
                    pt_meta_info.ui2_buf_size = 64;
                    e_return = IMtkPb_Ctrl_Get(prMtkMpContext->hHandle,IMTK_PB_CTRL_GET_TYPE_META_DATA, &pt_meta_info, sizeof(IMTK_PB_CTRL_META_DATA_INFO_T));
                    jshortArray array = env->NewShortArray(pt_meta_info.ui2_buf_size);
                    env->SetShortArrayRegion(array, 0, pt_meta_info.ui2_buf_size, (const jshort*)pt_meta_info.pv_buf);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_ARTIST: IMtkPb_Ctrl_Get e_return = %d",e_return);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_ARTIST: ARTIST = %d",array);
                    free(buffer);
                    return array;

                }
             case META_TYPE_ALBUM:
                {
                    UTF16_T*   buffer = (UTF16_T*)malloc(64 * sizeof(UTF16_T));
                    if (!buffer)
                    {
                        LOGI("####  nativeGetMetaDataInfo(): META_TYPE_ALBUM, memory allocated failed\n");
                        return NULL;
                    }

                    memset(buffer, 0, 64 * sizeof(UTF16_T));
                    pt_meta_info.e_meta_type = IMTK_PB_CTRL_META_TYPE_ALBUM;
                    pt_meta_info.pv_buf = buffer;
                    pt_meta_info.ui2_buf_size = 64;
                    e_return = IMtkPb_Ctrl_Get(prMtkMpContext->hHandle,IMTK_PB_CTRL_GET_TYPE_META_DATA, &pt_meta_info, sizeof(IMTK_PB_CTRL_META_DATA_INFO_T));
                    jshortArray array = env->NewShortArray(pt_meta_info.ui2_buf_size);
                    env->SetShortArrayRegion(array, 0, pt_meta_info.ui2_buf_size, (const jshort*)pt_meta_info.pv_buf); //env->SetShortArrayRegion(array, 0, pt_meta_info.ui2_buf_size, (UTF16_T*)pt_meta_info.pv_buf);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_ALBUM: IMtkPb_Ctrl_Get e_return = %d",e_return);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_ALBUM: ALBUM = %d",array);
                    free(buffer);
                    return array;

                }
             case META_TYPE_DURATION:
                {
                    UINT32  buffer = 0;
                    pt_meta_info.e_meta_type = IMTK_PB_CTRL_META_TYPE_DURATION;
                    pt_meta_info.pv_buf = &buffer;
                    pt_meta_info.ui2_buf_size = sizeof(UINT32);
                    e_return = IMtkPb_Ctrl_Get(prMtkMpContext->hHandle,IMTK_PB_CTRL_GET_TYPE_META_DATA, &pt_meta_info, sizeof(IMTK_PB_CTRL_META_DATA_INFO_T));

                    jclass integerclass = env->FindClass("java/lang/Integer");
                    jmethodID integermethod = env->GetMethodID(integerclass, "<init>", "(I)V");
                    jobject durationObj = env->NewObject(integerclass, integermethod, *((UINT32*)pt_meta_info.pv_buf));
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_DURATION: IMtkPb_Ctrl_Get e_return = %d", e_return);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_DURATION: ui2_buf_size = %d", pt_meta_info.ui2_buf_size);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_DURATION: DURATION = %d", *((UINT32*)pt_meta_info.pv_buf));
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_DURATION: durationObj = %d", (int)durationObj);
                    return durationObj;

                }

             case META_TYPE_BITRATE:
                {
                    UINT32  buffer = 0;
                    pt_meta_info.e_meta_type = IMTK_PB_CTRL_META_TYPE_BITRATE;
                    pt_meta_info.pv_buf = &buffer;
                    pt_meta_info.ui2_buf_size = sizeof(uint32_t);
                    e_return = IMtkPb_Ctrl_Get(prMtkMpContext->hHandle,IMTK_PB_CTRL_GET_TYPE_META_DATA, &pt_meta_info, sizeof(IMTK_PB_CTRL_META_DATA_INFO_T));

                    jclass integerclass = env->FindClass("java/lang/Integer");
                    jmethodID integermethod = env->GetMethodID(integerclass, "<init>", "(I)V");
                    jobject bitrateObj = env->NewObject(integerclass, integermethod, *((UINT32*)pt_meta_info.pv_buf));
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_BITRATE: IMtkPb_Ctrl_Get e_return = %d",e_return);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_BITRATE: BITRATE = %d",*((UINT32*)pt_meta_info.pv_buf));
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_BITRATE: bitrateObj = %d",(int)bitrateObj);
                    return bitrateObj;

                }

             case META_TYPE_FRAME_RATE:
                {
                    static const UINT32  MAX_METADATA_LENGTH = 2;
                    UINT32               buffer[MAX_METADATA_LENGTH];
                    memset(buffer, 0, MAX_METADATA_LENGTH);
                    memset(&pt_meta_info, 0, sizeof(IMTK_PB_CTRL_META_DATA_INFO_T));
                    pt_meta_info.e_meta_type = IMTK_PB_CTRL_META_TYPE_FRAME_RATE;
                    pt_meta_info.pv_buf = (void*)buffer;
                    pt_meta_info.ui2_buf_size = MAX_METADATA_LENGTH * sizeof(UINT32);
                    e_return = IMtkPb_Ctrl_Get(prMtkMpContext->hHandle, IMTK_PB_CTRL_GET_TYPE_META_DATA, &pt_meta_info, sizeof(IMTK_PB_CTRL_META_DATA_INFO_T));

                    jintArray array = env->NewIntArray(MAX_METADATA_LENGTH);
                    env->SetIntArrayRegion(array, 0, MAX_METADATA_LENGTH, (const jint*)pt_meta_info.pv_buf);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_FRAME_RATE: IMtkPb_Ctrl_Get e_return = %d", e_return);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_FRAME_RATE: ui4_rate = %d",  buffer[0]);
                    MMP_LOG("####  nativeGetMetaDataInfo # META_TYPE_FRAME_RATE: ui4_scale = %d", buffer[1]);
                    return array;
                }

             case META_TYPE_INVAL      :
             case META_TYPE_DATE       :
             case META_TYPE_SIZE       :
             case META_TYPE_PROTECT    :
             case META_TYPE_CREATE_TIME:
             case META_TYPE_ACCESS_TIME:
             case META_TYPE_MODIFY_TIME:
             case META_TYPE_RESOLUTION :
             case META_TYPE_NEXT_TITLE :
             case META_TYPE_NEXT_ARTIST:
             default:
                   return NULL;

         }

        LOGI("nativeGetMetaDataInfo(): Leave, player instance = %d", (int)prMtkMpContext);

    }

   /*
    * Class:     com_mediatek_media_MtkMediaPlayer
    * Method:    nativeGetMp3ImageInfo
    * Signature: (I)Ljava/lang/Object;
    */
    JNIEXPORT jobject JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeGetMp3ImageInfo
        (JNIEnv * env, jobject thiz, jint handle)
    {

        INPUTSTREAM_ENV_T*             envInfo = (INPUTSTREAM_ENV_T *)handle;
        IMTK_PB_ERROR_CODE_T           e_return;
        IMTK_PB_CTRL_MP3_COVER_INFO_T  pt_mp3_img;
        void*                          pv_phy_addr = NULL;
        void*                          pv_vir_addr = NULL;

        LOGI("####  nativeGetMp3ImageInfo(): Enter\n");

        if (!envInfo)
        {
            LOGE("####  nativeGetMp3ImageInfo(): error happen!\n");
            return NULL;
        }

        LOGI("####  nativeGetMp3ImageInfo(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

        memset(&pt_mp3_img, 0, sizeof(IMTK_PB_CTRL_MP3_COVER_INFO_T));
        e_return = IMtkPb_Ctrl_Get(envInfo->hHandle,
                                   IMTK_PB_CTRL_GET_TYPE_MP3_COVER,
                                   &pt_mp3_img,
                                   sizeof(IMTK_PB_CTRL_MP3_COVER_INFO_T));
        MMP_LOG("####  nativeGetMp3ImageInfo: IMtkPb_Ctrl_Get  e_return1 = %d",e_return);
        MMP_LOG("####  nativeGetMp3ImageInfo: e_img_type = %d",pt_mp3_img.e_img_type);
        MMP_LOG("####  nativeGetMp3ImageInfo: ui4_length = %d",pt_mp3_img.ui4_length);
        if(e_return == IMTK_PB_ERROR_CODE_OK)
        {
           if((pt_mp3_img.e_img_type != IMTK_MP3_COVER_IMG_TYPE_UNKNOWN) &&
              (pt_mp3_img.ui4_length != 0))
           {

               MMP_LOG("####  start IMtkPb_Ctrl_GetSHM");
               e_return = IMtkPb_Ctrl_GetSHM(envInfo->hHandle, pt_mp3_img.ui4_length, &pv_phy_addr, &pv_vir_addr);
               MMP_LOG("####  IMtkPb_Ctrl_GetSHM  e_return = %d",e_return);
               MMP_LOG("####  IMtkPb_Ctrl_GetSHM  pv_phy_addr = %d",(int)((int*)pv_phy_addr));
               MMP_LOG("####  IMtkPb_Ctrl_GetSHM  pv_vir_addr = %d",(int)((int*)pv_vir_addr));
               if(e_return != IMTK_PB_ERROR_CODE_OK)
               {
                   return NULL;
               }
               pt_mp3_img.pui1_img_data_buf = (uint8_t*)pv_phy_addr;

           }
           else
           {
               return NULL;
           }

           /* -----------Get Mp3 cover data----------*/
           e_return = IMtkPb_Ctrl_Get(envInfo->hHandle,
                                      IMTK_PB_CTRL_GET_TYPE_MP3_COVER,
                                      &pt_mp3_img,
                                      sizeof(IMTK_PB_CTRL_MP3_COVER_INFO_T));
           MMP_LOG("####  nativeGetMp3ImageInfo: IMtkPb_Ctrl_Get  e_return2 = %d",e_return);
           MMP_LOG("####  nativeGetMp3ImageInfo: e_img_type = %d", pt_mp3_img.e_img_type);
           MMP_LOG("####  nativeGetMetaDataInfo: ui4_width = %d",pt_mp3_img.ui4_width);
           MMP_LOG("####  nativeGetMp3ImageInfo: ui4_height = %d", pt_mp3_img.ui4_height);
           MMP_LOG("####  nativeGetMetaDataInfo: ui4_length = %d",pt_mp3_img.ui4_length);
           if(e_return == IMTK_PB_ERROR_CODE_OK)
           {
               jbyteArray array = env->NewByteArray(pt_mp3_img.ui4_length);
               env->SetByteArrayRegion(array, 0, pt_mp3_img.ui4_length, (const jbyte*)pv_vir_addr);
               jclass mp3ImageClass = env->FindClass("com/mediatek/media/Mp3ImageInfo");
               jmethodID mp3ImageMethod = env->GetMethodID(mp3ImageClass, "<init>", "(IIII[B)V");
               jobject mp3ImageObj = env->NewObject(mp3ImageClass, mp3ImageMethod, pt_mp3_img.e_img_type, pt_mp3_img.ui4_width, pt_mp3_img.ui4_height, pt_mp3_img.ui4_length, array);
               IMtkPb_Ctrl_FreeSHM(envInfo->hHandle,
                                   pt_mp3_img.ui4_length,
                                   pv_phy_addr,
                                   pv_vir_addr);

               return mp3ImageObj;
            }
            else
            {
               IMtkPb_Ctrl_FreeSHM(envInfo->hHandle,
                                   pt_mp3_img.ui4_length,
                                   pv_phy_addr,
                                   pv_vir_addr);
               return NULL;
            }
         }
         else
         {
                LOGI("####  IMtkPb_Ctrl_GetCurAudTrackInfo(): IMtkPb_Ctrl_Get(), e_return = %d, failed!\n", e_return);
               return NULL;
         }

    }

   /*
    * Class:     com_mediatek_media_MtkMediaPlayer
    * Method:    nativeGetVideoTrackInfo
    * Signature: (I)Lcom/mediatek/media/VideoTrackInfo;
    */
    JNIEXPORT jobject JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeGetVideoTrackInfo
        (JNIEnv * env, jobject thiz, jint handle)
    {
        INPUTSTREAM_ENV_T*                 envInfo = (INPUTSTREAM_ENV_T *)handle;
        IMTK_PB_ERROR_CODE_T               e_return;
        IMTK_PB_CTRL_GET_VID_TRACK_INFO_T  ptInfo;

        LOGI("####  IMtkPb_Ctrl_GetVidTrackInfo(): Enter\n");

        LOGI("####  IMtkPb_Ctrl_GetVidTrackInfo(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

        e_return = IMtkPb_Ctrl_GetVidTrackInfo(envInfo->hHandle, &ptInfo);
        if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
            LOGE("####  IMtkPb_Ctrl_GetCurAudTrackInfo(): IMtkPb_Ctrl_GetVidTrackInfo(), e_return = %d, failed!\n", e_return);
        }

        MMP_LOG("####  nativeGetVideoTrackInfo(): IMtkPb_Ctrl_GetVidTrackInfo  e_return = %d\n", e_return);
        MMP_LOG("####  nativeGetVideoTrackInfo(): eVidEnc = %d\n", (jint)ptInfo.eVidEnc);
        MMP_LOG("####  nativeGetVideoTrackInfo(): u4Width = %d\n", ptInfo.u4Width);
        MMP_LOG("####  nativeGetVideoTrackInfo(): u4Height = %d\n", ptInfo.u4Height);
        MMP_LOG("####  nativeGetVideoTrackInfo(): u4InstBitRate = %d\n", ptInfo.u4InstBitRate);
        jclass videoTrackInfoClass = env->FindClass("com/mediatek/media/VideoTrackInfo");
        jmethodID cons = env->GetMethodID( videoTrackInfoClass, "<init>", "(IIII)V");
        jobject videoTrackInfoObj = env->NewObject( videoTrackInfoClass, cons, (jint)ptInfo.eVidEnc, ptInfo.u4Width, ptInfo.u4Height, ptInfo.u4InstBitRate);

        LOGI("####  IMtkPb_Ctrl_GetVidTrackInfo(): Leave\n");

        return videoTrackInfoObj;
    }

   /*
    * Class:     com_mediatek_media_MtkMediaPlayer
    * Method:    nativeGetAudioTrackInfo
    * Signature: (I)Lcom/mediatek/media/AudioTrackInfo;
    */
    JNIEXPORT jobject JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeGetAudioTrackInfo
        (JNIEnv * env, jobject thiz, jint handle)
    {
        INPUTSTREAM_ENV_T*                 envInfo = (INPUTSTREAM_ENV_T *)handle;
        IMTK_PB_ERROR_CODE_T               e_return;
        IMTK_PB_CTRL_GET_AUD_TRACK_INFO_T  ptInfo;

        LOGI("####  IMtkPb_Ctrl_GetCurAudTrackInfo(): Enter\n");

        LOGI("####  IMtkPb_Ctrl_GetCurAudTrackInfo(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

        e_return = IMtkPb_Ctrl_GetCurAudTrackInfo(envInfo->hHandle, &ptInfo);
        if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
            LOGE("####  IMtkPb_Ctrl_GetCurAudTrackInfo(): IMtkPb_Ctrl_GetCurAudTrackInfo(), e_return = %d, failed!\n", e_return);
        }

        jclass AudTrackInfoClass = env->FindClass("com/mediatek/media/AudioTrackInfo");
        jmethodID cons = env->GetMethodID( AudTrackInfoClass, "<init>", "(IIII)V");
        jobject AudTrackInfoObj = env->NewObject( AudTrackInfoClass, cons, (jint)ptInfo.eAudEnc, (jint)ptInfo.eChNum, (jint)ptInfo.eSampleRate, ptInfo.u4BitRate);

        LOGI("####  IMtkPb_Ctrl_GetCurAudTrackInfo(): Leave\n");

        return AudTrackInfoObj;
    }

   /*
    * Class:     com_mediatek_media_MtkMediaPlayer
    * Method:    nativeGetThumbNailInfo
    * Signature: (ILcom/mediatek/media/ThumbNailInfo;)Ljava/lang/Object;
    */
    JNIEXPORT jobject JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeGetThumbNailInfo
        (JNIEnv * env, jobject thiz, jint handle, jobject thumbNailInfo)
    {
        INPUTSTREAM_ENV_T*        prMtkMpContext = (INPUTSTREAM_ENV_T *)handle;
        IMTK_PB_ERROR_CODE_T      e_return;
        uint8_t*                  pui1_buf        = NULL;
        IMTK_PB_THUMBNAIL_INFO_T  pt_thumb_info;
        uint8_t                   ui1_bytes_per_pixel = 0;
        uint32_t                  u4BufLen;
        jfieldID                  field;
        int                       canvasColormode;
        int                       thumbnailWidth;
        int                       thumbnailHeight;
        jclass                    thumbNailInfoClass = env->FindClass("com/mediatek/media/ThumbNailInfo");

        LOGI("nativeGetThumbNailInfo(): Enter, player instance = %d", (int)prMtkMpContext);

        LOGI("nativeGetThumbNailInfo(): player instance = %d, CMPB handle = %x", (int)prMtkMpContext, (uint32_t)prMtkMpContext->hHandle);

        field = env->GetFieldID(thumbNailInfoClass, "canvasColormode", "I");
        canvasColormode = (jint)env->GetIntField(thumbNailInfo, field);
        field = env->GetFieldID(thumbNailInfoClass, "thumbnailWidth", "I");
        thumbnailWidth = (jint)env->GetIntField(thumbNailInfo, field);
        field = env->GetFieldID(thumbNailInfoClass, "thumbnailHeight", "I");
        thumbnailHeight = (jint)env->GetIntField(thumbNailInfo, field);
        memset(&pt_thumb_info, 0, sizeof(IMTK_PB_THUMBNAIL_INFO_T));

        switch(canvasColormode)
        {
            case THUMBNAIL_COLORMODE_UYVY_16    :
            case THUMBNAIL_COLORMODE_YUYV_16    :
            case THUMBNAIL_COLORMODE_RGB_D565   :
            case THUMBNAIL_COLORMODE_ARGB_D1555 :
            case THUMBNAIL_COLORMODE_ARGB_D4444 :
            case THUMBNAIL_COLORMODE_YUV_422_BLK:
            case THUMBNAIL_COLORMODE_YUV_422_RS :
            ui1_bytes_per_pixel = 2;
            break;

            case THUMBNAIL_COLORMODE_AYUV_D8888 :
            case THUMBNAIL_COLORMODE_ARGB_D8888 :
            case THUMBNAIL_COLORMODE_YUV_444_BLK:
            case THUMBNAIL_COLORMODE_YUV_444_RS :
            ui1_bytes_per_pixel = 4;
            break;

            //not used now, so do as 0.
            case THUMBNAIL_COLORMODE_AYUV_CLUT2 :
            case THUMBNAIL_COLORMODE_AYUV_CLUT4 :
            case THUMBNAIL_COLORMODE_AYUV_CLUT8 :
            case THUMBNAIL_COLORMODE_ARGB_CLUT2 :
            case THUMBNAIL_COLORMODE_ARGB_CLUT4 :
            case THUMBNAIL_COLORMODE_ARGB_CLUT8 :
            case THUMBNAIL_COLORMODE_YUV_420_BLK:
            case THUMBNAIL_COLORMODE_YUV_420_RS :
            default:
            ui1_bytes_per_pixel = 0;
            break;
        }
        u4BufLen = ui1_bytes_per_pixel * thumbnailWidth * thumbnailHeight;
        if(u4BufLen==0)
        {
            LOGE("####  nativeGetThumbNailInfo(): thumbnail_Colormode not used now!\n");
            return NULL;
        }

        //LOGI("####  nativeGetThumbNailInfo(): canvasColormode = %d\n", canvasColormode);
        //LOGI("####  nativeGetThumbNailInfo(): thumbnailWidth = %d\n", thumbnailWidth);
        //LOGI("####  nativeGetThumbNailInfo(): thumbnailHeight = %d\n", thumbnailHeight);
        //LOGI("####  nativeGetThumbNailInfo(): ui1_bytes_per_pixel = %d\n", ui1_bytes_per_pixel);
        //LOGI("####  nativeGetThumbNailInfo(): u4BufLen = %d\n", u4BufLen);

        pui1_buf = (uint8_t*)malloc(u4BufLen);
        if(pui1_buf == NULL)
        {
           LOGE("####  nativeGetThumbNailInfo(): Out of memory !!!\n");
           return NULL;
        }
        memset(pui1_buf, 0, u4BufLen);

        pt_thumb_info.u1CanvasBuffer     = pui1_buf;
        pt_thumb_info.eCanvasColormode   = (IMTK_PB_THUMBNAIL_COLORMODE_T)canvasColormode;
        pt_thumb_info.u4BufLen           = u4BufLen;
        pt_thumb_info.u4ThumbnailWidth   = thumbnailWidth;
        pt_thumb_info.u4ThumbnailHeight  = thumbnailHeight;

        e_return = IMtkPb_Ctrl_GetThumbNail(prMtkMpContext->hHandle, &pt_thumb_info);
        if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
            LOGI("nativeGetThumbNailInfo(): player instance = %d, IMtkPb_Ctrl_GetThumbNail() failed, e_return = %d",
                (int)prMtkMpContext, e_return);

            free(pui1_buf);
            return NULL;
        }
        jbyteArray array = env->NewByteArray(pt_thumb_info.u4BufLen);
        env->SetByteArrayRegion(array, 0, pt_thumb_info.u4BufLen, (const jbyte*)pt_thumb_info.u1CanvasBuffer);
        free(pui1_buf);

        LOGI("nativeGetThumbNailInfo(): Leave, player instance = %d", (int)prMtkMpContext);

        return array;
    }


/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeGetPlayerStatus
* Signature: (V)Z
*/
JNIEXPORT jboolean JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeGetPlayerStatus
   (JNIEnv * env, jobject thiz)
{
   jboolean  playerStatus;

   LOGI("####  nativeGetPlayerStatus(): Enter\n");

   playerStatus = IMtkPb_Ctrl_GetPlayStatus();
   LOGI("####  nativeGetPlayerStatus(): IMtkPb_Ctrl_GetPlayStatus(), playerStatus = %d\n", (int)playerStatus);

   LOGI("####  nativeGetPlayerStatus(): Leave\n");

   return playerStatus;
}

JNIEXPORT jboolean JNICALL  Java_com_mediatek_media_MtkMediaPlayer_nativeCanDoTrick
(JNIEnv * env, jobject thiz, jint handle, jint speed)
{
    INPUTSTREAM_ENV_T*            envInfo = (INPUTSTREAM_ENV_T *)handle;
    IMTK_PB_ERROR_CODE_T          e_return;
    IMTK_PB_CTRL_UOP_CAP_INFO_T   rTrickUOP;

    LOGI("####  nativeCanDoTrick(): Enter\n");

    jboolean  fgCanDoTrick;
    fgCanDoTrick = JNI_FALSE;

    rTrickUOP.e_uop = IMTK_PB_CTRL_GET_UOP_TYPE_TRICK;
    rTrickUOP.t_speed = (IMTK_PB_CTRL_SPEED_T)speed;

    e_return = IMtkPb_Ctrl_Get(envInfo->hHandle,IMTK_PB_CTRL_GET_TYPE_UOP_CAP, &rTrickUOP, sizeof(IMTK_PB_CTRL_UOP_CAP_INFO_T));

    if (e_return == IMTK_PB_ERROR_CODE_OK)
    {
        fgCanDoTrick = JNI_TRUE;
    }

    LOGI("####  nativeCanDoTrick(): IMtkPb_Ctrl_Get(), fgCanDoTrick = %d\n", (int)fgCanDoTrick);

    LOGI("####  nativeCanDoTrick(): Leave\n");

    return fgCanDoTrick;
}

JNIEXPORT jboolean JNICALL  Java_com_mediatek_media_MtkMediaPlayer_nativeCanDoSeek
(JNIEnv * env, jobject thiz, jint handle, jint speed)
{
    INPUTSTREAM_ENV_T*            envInfo = (INPUTSTREAM_ENV_T *)handle;
    IMTK_PB_ERROR_CODE_T          e_return;
    IMTK_PB_CTRL_UOP_CAP_INFO_T   rSeekUOP;

    LOGI("####  nativeCanDoSeek(): Enter\n");

    jboolean  fgCanDoSeek;
    fgCanDoSeek = JNI_FALSE;

    rSeekUOP.e_uop = IMTK_PB_CTRL_GET_UOP_TYPE_SEEK;
    rSeekUOP.t_speed = (IMTK_PB_CTRL_SPEED_T)speed;

    e_return = IMtkPb_Ctrl_Get(envInfo->hHandle,IMTK_PB_CTRL_GET_TYPE_UOP_CAP, &rSeekUOP, sizeof(IMTK_PB_CTRL_UOP_CAP_INFO_T));

    if (e_return == IMTK_PB_ERROR_CODE_OK)
    {
        fgCanDoSeek = JNI_TRUE;
    }

    LOGI("####  nativeCanDoSeek(): IMtkPb_Ctrl_Get(), fgCanDoTrick = %d\n", (int)fgCanDoSeek);

    LOGI("####  nativeCanDoSeek(): Leave\n");

    return fgCanDoSeek;
}

/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeGetVideoInfo
* Signature: (I)Lcom/mediatek/media/VideoInfo;
*/
JNIEXPORT jobject JNICALL Java_com_mediatek_media_MtkMediaPlayer_nativeGetVideoInfo
   (JNIEnv * env, jobject thiz, jint handle)
{
   INPUTSTREAM_ENV_T*            envInfo = (INPUTSTREAM_ENV_T *)handle;
   IMTK_PB_ERROR_CODE_T          e_return;
   IMTK_PB_CTRL_MM_VIDEO_INFO_T  ptVideoInfo;

   LOGI("####  nativeGetVideoInfo(): Enter\n");

   LOGI("####  nativeGetVideoInfo(): envInfo = %d, CMPB handle = %x\n", (int)envInfo, (uint32_t)envInfo->hHandle);

   e_return = IMtkPb_Ctrl_Get(envInfo->hHandle,IMTK_PB_CTRL_GET_TYPE_VIDEO_INFO, &ptVideoInfo, sizeof(IMTK_PB_CTRL_MM_VIDEO_INFO_T));

   MMP_LOG("####  nativeGetVideoInfo(): IMtkPb_Ctrl_Get  e_return = %d\n", e_return);
   MMP_LOG("####  nativeGetVideoInfo(): ui4_width = %d\n", ptVideoInfo.ui4_width);
   MMP_LOG("####  nativeGetVideoInfo(): ui4_height = %d\n", ptVideoInfo.ui4_height);
   MMP_LOG("####  nativeGetVideoInfo(): ui1_par_w = %d\n", ptVideoInfo.ui1_par_w);
   MMP_LOG("####  nativeGetVideoInfo(): ui1_par_h = %d\n", ptVideoInfo.ui1_par_h);
   MMP_LOG("####  nativeGetVideoInfo(): b_src_asp = %d\n", ptVideoInfo.b_src_asp);
   if (e_return != IMTK_PB_ERROR_CODE_OK)
   {
        LOGE("####  nativeGetVideoInfo(): IMtkPb_Ctrl_Get(), e_return = %d, failed!\n", e_return);
        return NULL;
   }
   else
   {
       jclass videoInfoClass = env->FindClass("com/mediatek/media/VideoInfo");
       jmethodID cons = env->GetMethodID( videoInfoClass, "<init>", "(IIBBZ)V");
       jobject videoInfoObj = env->NewObject( videoInfoClass, cons, ptVideoInfo.ui4_width, ptVideoInfo.ui4_height, (jbyte)ptVideoInfo.ui1_par_w, (jbyte)ptVideoInfo.ui1_par_h, (jboolean)ptVideoInfo.b_src_asp);

        LOGI("####  nativeGetVideoInfo(): Leave\n");

       return videoInfoObj;
   }
}

/*
* Class:     com_mediatek_media_MtkMediaPlayer
* Method:    nativeCMPBInit
* Signature: ()void;
*/
JNIEXPORT void JNICALL
Java_com_mediatek_media_MtkMediaPlayer_nativeCMPBInit(JNIEnv * env, jclass clazz)
{
    LOGI("####  native_init(): Enter\n");

    initialize();

    MTK::s_MpJniFields.context = env->GetFieldID(clazz, "mNativeContext", "I");
    if (MTK::s_MpJniFields.context == NULL) {
        LOGE("native_init(): find variable(MtkMediaPlayer, mNativeContext) failed, please check\n");
        return;
    }

    MTK::s_MpJniFields.post_event = env->GetMethodID(clazz, "handleMessage", "(III)V");
    if (MTK::s_MpJniFields.post_event == NULL) {
        LOGE("native_init(): find method(MtkMediaPlayer, handleMessage) failed, please check\n");
        return;
    }

    // the following is for dealing with stream
    MTK::s_MpJniFields.read_stream = env->GetMethodID(clazz, "read", "(I)I");
    if (MTK::s_MpJniFields.read_stream == NULL) {
        LOGE("native_init(): find method(MtkMediaPlayer, read) failed, please check\n");
        return;
    }

    MTK::s_MpJniFields.streamReadBuffer= env->GetFieldID(clazz, "mStreamReadBuffer", "[B");
    if (MTK::s_MpJniFields.streamReadBuffer == NULL) {
        LOGE("native_init(): find variable(MtkMediaPlayer, mStreamReadBuffer) failed, please check\n");
        return;
    }

    MTK::s_MpJniFields.streamSize = env->GetFieldID(clazz, "mStreamSize", "J");
    if (MTK::s_MpJniFields.streamSize == NULL) {
        LOGE("native_init(): find variable(MtkMediaPlayer, mStreamSize) failed, please check\n");
        return;
    }

    MTK::s_MpJniFields.streamCurPos = env->GetFieldID(clazz, "mStreamCurPos", "J");
    if (MTK::s_MpJniFields.streamCurPos == NULL) {
        LOGE("native_init(): find variable(MtkMediaPlayer, mStreamCurPos) failed, please check\n");
        return;
    }

    MTK::s_MpJniFields.seek_stream = env->GetMethodID(clazz, "seek", "(IJ)J");
    if (MTK::s_MpJniFields.seek_stream == NULL) {
        LOGE("native_init(): find method(MtkMediaPlayer, seek) failed, please check\n");
        return;
    }

    MTK::s_MpJniFields.streamSeekable = env->GetFieldID(clazz, "mSeekEnable", "Z");
    if (MTK::s_MpJniFields.streamSeekable == NULL) {
        LOGE("native_init(): find variable(MtkMediaPlayer, mSeekEnable) failed, please check\n");
        return;
    }

    MTK::s_MpJniFields.isAudioPlayer = env->GetFieldID(clazz, "mIsAudioPlayer", "Z");
    if (MTK::s_MpJniFields.isAudioPlayer == NULL) {
        LOGE("native_init(): find variable(MtkMediaPlayer, mIsAudioPlayer) failed, please check\n");
        return;
    }

    setEngineThread.Start();

    LOGI("####  native_init(): Leave\n");
}

void CMPBSetEngineParamThread::Run()
{
    running = true;

    CMPBSetEngineParamEvent * event;

    while(true)
    {
        event=engineQueue.recv();
        if(event == NULL)
        {
            break;
        }

        INPUTSTREAM_ENV_T*            envInfo  = event->envInfo;
        int                           mediaPlayerMode = event->mediaPlayerMode;
        int                           prepareType = event->prepareType;
        uint8_t*                      path = event->path;
        IMTK_PB_CTRL_ENGINE_PARAM_T   t_parm;
        IMTK_PB_ERROR_CODE_T          e_return = IMTK_PB_ERROR_CODE_OK;

        if(MEDIAPLAYER_MODE_URI == mediaPlayerMode)
        {
            if (event->fgIsAudioPlayer)
            {
                LOGI("####  CMPBSetEngineParamThread: MEDIAPLAYER_MODE_URI == mediaPlayerMode, just for AudioPlayer!!\n");
                t_parm.u4PlayFlag = IMTK_PB_CTRL_PLAY_FLAG_AUDIO;
            }
            else
            {
                t_parm.u4PlayFlag = IMTK_PB_CTRL_PLAY_FLAG_AUDIO |
                                IMTK_PB_CTRL_PLAY_FLAG_VIDEO;
            }

            t_parm.uBufferModelParam.tUriInfo.pu1URI = path;
            t_parm.uBufferModelParam.tUriInfo.eBufSizeType = IMTK_PB_CTRL_BUF_SIZE_TYPE_BYTE;
            t_parm.uBufferModelParam.tUriInfo.uBufSize.u4Bytes= URI_BUF_SIZE;
            t_parm.uBufferModelParam.tUriInfo.u4KeepBufThreshold = URI_BUF_SIZE * 10 / 100;
            t_parm.uBufferModelParam.tUriInfo.u4ReBufThreshold = URI_BUF_SIZE * 90 / 100;
                LOGI("####  CMPBSetEngineParamThread: Enter MEDIAPLAYER_MODE_URI\n");
        }
        else if(MEDIAPLAYER_MODE_PULL == mediaPlayerMode)
        {
            if(path[0] == '/') //it's local file
            {
                if (g_pLocalUrl_PullClient)
                {
                    LOGI("####  CMPBSetEngineParamThread: the instance of LocalUrl_PullClient was forced to delete due to wrong steps done by AP. Please check!!\n");
                    delete g_pLocalUrl_PullClient;
                    g_pLocalUrl_PullClient = NULL;
                }

                if (!g_pLocalUrl_PullClient)
                {
                    LOGI("####  CMPBSetEngineParamThread: new the instance of LocalUrl_PullClient!\n");
                    g_pLocalUrl_PullClient = new LocalUrl_PullClient();
                    g_pLocalUrl_PullClient->setDataSource((char*)path);
                }

                if (event->fgIsAudioPlayer)
                {
                    LOGI("####  CMPBSetEngineParamThread: MEDIAPLAYER_MODE_PULL == mediaPlayerMode 1, just for AudioPlayer!!\n");
                    t_parm.u4PlayFlag = IMTK_PB_CTRL_PLAY_FLAG_AUDIO;
                }
                else
                {
                    t_parm.u4PlayFlag = IMTK_PB_CTRL_PLAY_FLAG_AUDIO |
                                    IMTK_PB_CTRL_PLAY_FLAG_VIDEO;
                }

                t_parm.uBufferModelParam.tPullInfo.pvAppTag = (void*)(g_pLocalUrl_PullClient);
                t_parm.uBufferModelParam.tPullInfo.pfnOpen = LocalUrl_PullOpen;
                t_parm.uBufferModelParam.tPullInfo.pfnClose = LocalUrl_PullClose;
                t_parm.uBufferModelParam.tPullInfo.pfnRead = LocalUrl_PullRead;
                t_parm.uBufferModelParam.tPullInfo.pfnReadAsync = LocalUrl_PullReadAsync;
                t_parm.uBufferModelParam.tPullInfo.pfnAbortReadAsync = LocalUrl_PullAbortReadAsync;
                t_parm.uBufferModelParam.tPullInfo.pfnByteSeek = LocalUrl_PullByteSeek;
                t_parm.uBufferModelParam.tPullInfo.pfnGetInputLen = LocalUrl_PullGetInputLen;
                LOGI("####  CMPBSetEngineParamThread: Enter MEDIAPLAYER_MODE_PULL with the URL from Ap\n");
            }
            else
            {
                if (event->fgIsAudioPlayer)
                {
                    LOGI("####  CMPBSetEngineParamThread: MEDIAPLAYER_MODE_PULL == mediaPlayerMode 2, just for AudioPlayer!!\n");
                    t_parm.u4PlayFlag = IMTK_PB_CTRL_PLAY_FLAG_AUDIO;
                }
                else
                {
                    t_parm.u4PlayFlag = IMTK_PB_CTRL_PLAY_FLAG_AUDIO |
                                    IMTK_PB_CTRL_PLAY_FLAG_VIDEO;
                }
                t_parm.uBufferModelParam.tPullInfo.pvAppTag = (void*)(envInfo);
                t_parm.uBufferModelParam.tPullInfo.pfnOpen = PullOpen;
                t_parm.uBufferModelParam.tPullInfo.pfnClose = PullClose;
                t_parm.uBufferModelParam.tPullInfo.pfnRead = PullRead;
                t_parm.uBufferModelParam.tPullInfo.pfnReadAsync = PullReadAsync;
                t_parm.uBufferModelParam.tPullInfo.pfnAbortReadAsync = PullAbortReadAsync;
                t_parm.uBufferModelParam.tPullInfo.pfnByteSeek = PullByteSeek;
                t_parm.uBufferModelParam.tPullInfo.pfnGetInputLen = PullGetInputLen;
                LOGI("####  CMPBSetEngineParamThread: Enter MEDIAPLAYER_MODE_PULL with the InputStream from Ap\n");
            }
        }

        if(PREPARE_TYPE_SYNC == prepareType)
        {
            e_return = IMtkPb_Ctrl_SetEngineParam(envInfo->hHandle,&t_parm);
            LOGI("####  nativeSetEngineParam:IMtkPb_Ctrl_SetEngineParam  e_return = %d \n", e_return);
        }
        else if(PREPARE_TYPE_ASYNC == prepareType)
        {
            e_return = IMtkPb_Ctrl_SetEngineParamAsync(envInfo->hHandle,&t_parm);
            LOGI("####  CMPBSetEngineParamThread:IMtkPb_Ctrl_SetEngineParamAsync  e_return = %d \n", e_return);
        }

        if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
            IMtkPb_Ctrl_Close(envInfo->hHandle);
            envInfo->hHandle = 0;
            LOGE("####  CMPBSetEngineParamThread:IMtkPb_Ctrl_SetEngineParam is fail\n");
            event->errorCode =  e_return;
        }

        if (PREPARE_TYPE_SYNC == prepareType)
        {
            event->sema->release();
        }
        else
        {
            delete (event->sema);
            event->sema = NULL;
            delete event;
        }
    }
}

#ifdef __cplusplus

}
#endif
