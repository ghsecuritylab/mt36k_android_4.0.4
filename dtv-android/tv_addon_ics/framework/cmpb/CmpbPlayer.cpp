//#define LOG_NDEBUG 0

#define _FILE_OFFSET_BITS 64

#include <utils/Log.h>
#include <errno.h>
#include <fcntl.h>
#include <signal.h>
#include <unistd.h>
#include <surfaceflinger/ISurface.h>
//#include <surfaceflinger/ICMPBSurface.h>
#include <cutils/properties.h>

#include "CmpbPlayer.h"
#include <media/Metadata.h>
#include <media/stagefright/MediaExtractor.h>
#include "Cmpb_protocol.h"
#include <string.h>
#include <utils/String8.h>

#ifdef HW_COMPOSER
#include <gui/SurfaceTextureClient.h>
#include <ui/android_native_buffer.h>
#endif
namespace android {

#define PLAY_IN_PREPARE 1
#define SUPPORT_CTS     1

#define USE_FILE_SYSTEM_CALL 1

#define URI_BUF_SIZE    (21*1024*1024)

#ifndef UNUSED
#define UNUSED(x)               (void)x         /**<The return value of routine is not cared*/
#endif

static status_t ERROR_NOT_OPEN = -1;
static status_t ERROR_OPEN_FAILED = -2;
static status_t ERROR_EAS_FAILURE = -3;
static status_t ERROR_ALLOCATE_FAILED = -4;

#define MTK_UA_KEYWORD    "?mtkUAString="
#define COOKIE_KEYWORD1      "Cookie: "
#define COOKIE_KEYWORD2      "Cookie="

#define MTK_DF_UA_STRING  "Mozilla/5.0 AppleCoreMedia/1.0.0.8J2 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B367 Safari/531.21.10"
//#define MTK_DF_UA_STRING  "stagefright/1.2 (Linux;Android 4.0.3)"   //UA set "Ipad" cause the renrenwang.apk can't playback the mp3 files.

////for initialization//////////////
#define MAX_NUM_HANDLES      ((unsigned short) 4096)

typedef struct _THREAD_DESCR_T
{
    unsigned int    z_stack_size;

    unsigned char   ui1_priority;

    unsigned short  ui2_num_msgs;
}   THREAD_DESCR_T;

typedef struct _GEN_CONFIG_T
{
    unsigned short  ui2_version;

    void*  pv_config;

    unsigned int  z_config_size;

    THREAD_DESCR_T  t_mheg5_thread;
}   GEN_CONFIG_T;

extern "C" 
{
int c_rpc_init_client(void);
int c_rpc_start_client(void);

int os_init(const void *pv_addr, unsigned int z_size);
int handle_init (unsigned short     ui2_num_handles,
                 void**             ppv_mem_addr,
                 unsigned int*      pz_mem_size);
int x_rtos_init (GEN_CONFIG_T*      pt_config);
}

static bool bCmpbPlayerInit = false;

#if ASYNC_READ
static bool bCmpbAsyncReadThreadRun = false;

#define MAX_ASYNC_READ_QUEUE_CNT     100
Vector <ASYNC_READ_Q_ITEM_T*> CmpbPlayer::mAsyncReadQueue;    
Mutex                         CmpbPlayer::mQueueVectorLock;
Mutex                         CmpbPlayer::mAbortReadLock;
uint32_t                      CmpbPlayer::mQueueItemCnt;

typedef enum
{
    X_MSGQ_OPTION_WAIT = 1,
    X_MSGQ_OPTION_NOWAIT
}   MSGQ_OPTION_T;

static IMTK_PB_HANDLE_T       h_que = 0;

extern "C" int32_t x_msg_q_create(IMTK_PB_HANDLE_T     *ph_msg_hdl,    /* msg q handle */
                                       const char   *ps_name,
                                       size_t       z_msg_size,
                                       uint16_t       ui2_msg_count);
extern "C" int32_t x_msg_q_send(IMTK_PB_HANDLE_T   h_msg_hdl,      /* msg q handle */
                                     const void *pv_msg,
                                     size_t     z_size,
                                     uint8_t      ui1_pri);
extern "C" int32_t x_msg_q_receive(uint16_t          *pui2_index,
                                        void            *pv_msg,
                                        size_t          *pz_size,
                                        IMTK_PB_HANDLE_T  *ph_msgq_hdl,
                                        uint16_t          ui2_msgq_hdl_count,
                                        MSGQ_OPTION_T   e_option);

typedef void (*x_os_thread_main_fct) (void*  pv_arg);
static IMTK_PB_HANDLE_T h_async_read_thd = 0;

extern "C" int32_t x_thread_create(IMTK_PB_HANDLE_T    *ph_th_hdl,         /* thread handle */
                                      const char  *ps_name,
                                      size_t       z_stacksize,
                                      uint8_t        ui1_pri,           /* thread priority */
                                      x_os_thread_main_fct pf_main,   /* thread's main function */
                                      size_t       z_arg_size,
                                      void         *pv_arg);

#endif

/*-----------------------------------------------------------------------------
                    Internal functions declarations
 ----------------------------------------------------------------------------*/

static bool rpc_inited = false;
static void _rpc_env_init()
{
    if(rpc_inited)
    {
        return;
    }
    GEN_CONFIG_T  t_rtos_config;
    void*       pv_mem_addr = 0;
    unsigned int z_mem_size = 0xc00000;
    
    memset(&t_rtos_config, 0, sizeof(GEN_CONFIG_T));
    LOGI("_rpc_env_init enter\n");
    x_rtos_init (&t_rtos_config);
    handle_init (MAX_NUM_HANDLES, &pv_mem_addr, &z_mem_size);
    os_init (pv_mem_addr, z_mem_size);

    c_rpc_init_client();
    c_rpc_start_client();
    LOGI("_rpc_env_init exit\n");

    rpc_inited = true;
}

#if ASYNC_READ
void CmpbPlayer::AsyncReadThreadInit()
{
    int32_t i4_ret;
    
    LOGI("AsyncReadThreadInit enter\n");
    mAsyncReadQueue.clear();
    mQueueItemCnt = 0;
    mAsyncReadQueue.setCapacity(MAX_ASYNC_READ_QUEUE_CNT);

    if(0 == h_que)
    {
        i4_ret = x_msg_q_create(&h_que, "pull_mode_msgq", sizeof(uint32_t), MAX_ASYNC_READ_QUEUE_CNT);
        LOGI("AsyncReadThreadInit:: create message queue, ret = %d !!!!!\n", i4_ret);
    }

    if (0 == h_async_read_thd)
    {
        i4_ret = x_thread_create(&h_async_read_thd,
                                 "android_async_read_thread",
                                 20480,
                                 100,
                                 AsyncReadThread,
                                 sizeof(IMTK_PB_HANDLE_T *),
                                 (void *)&h_que);

        LOGI("AsyncReadThreadInit x_thread_create, ret = %d \n", i4_ret);
    }
    //createThread(AsyncReadThread, 0);
    
    LOGI("AsyncReadThreadInit end\n");
}
#endif


//////////////end init///////////////////

void CmpbPlayer::Init()
{
    mFileLocator.path = NULL;
    mFileLocator.fd = -1;
    mFileLocator.offset = 0;
    mFileLocator.length = 0;

    mState = STATE_IDLE;

    if(!bCmpbPlayerInit)
    {
        mQueueVectorLock.lock();
        if(!bCmpbPlayerInit)  //for multi instance protection
        {
            LOGI("CmpbPlayer init RPC \n");
            _rpc_env_init();
            
#if ASYNC_READ
            if(!bCmpbAsyncReadThreadRun)
            {
                bCmpbAsyncReadThreadRun = true;
                AsyncReadThreadInit();
            }
#endif
            bCmpbPlayerInit = true;
        }
        
        mQueueVectorLock.unlock();
    }

    mStartTime = 0;
    mCurBufPercent = 0;
    mHttpURI  = false;
    mMmsURI   = false;
    mSeeking  = false;
    mStart = false;
    mForMetaInfo = false;
    
    mSeekDoneNeedPlay = false;

    mReseted = false;
    mVidUnplayble = false;
    
    mAbortRead   = false;
    m_pExsubttURI = NULL;
    
    mDstVidRect.u4X = 0;
    mDstVidRect.u4Y = 0;
    mDstVidRect.u4W = 1000;
    mDstVidRect.u4H = 1000;

    mSrcVidRect.u4X = 0;
    mSrcVidRect.u4Y = 0;
    mSrcVidRect.u4W = 1000;
    mSrcVidRect.u4H = 1000;
    
    m_TsProgIdx = 0;
    
    /*http living streaming */
    //m_pProtocolPlayer = new CmpbProtocolPlayer;
    m_pProtocolPlayer = NULL;
    m_bProtocolPlayerUsed = false;
    LOGI("CmpbPlayer constructor get BG color \n");
    memset(&mColor, 0, sizeof(mColor));
    if(IMTK_PB_ERROR_CODE_OK == IMtkPb_Ctrl_Get_BG_Color(&mColor))
    {
        if(mColor.ui1_red == 0 && mColor.ui1_blue == 0xFF && mColor.ui1_green == 0)  //blue mute on
        {
            mColor.ui1_blue = 0; //set to black
            IMtkPb_Ctrl_Set_BG_Color(&mColor);
            mColor.ui1_blue = 0xFF; //restore color
        }
    }   
}

CmpbPlayer::CmpbPlayer():
    mState(STATE_IDLE),mLoop(false)
{
    LOGI("CmpbPlayer constructor \n"); 
    Init();    
    LOGI("CmpbPlayer constructor end\n");
}

CmpbPlayer::CmpbPlayer(bool metainfo):
    mState(STATE_IDLE),mLoop(false)
{
    LOGI("CmpbPlayer constructor (metainfo = %d) \n", metainfo); 
    Init();
    mForMetaInfo = metainfo;
    LOGI("CmpbPlayer constructor end (metainfo = %d)\n", metainfo);
}

CmpbPlayer::~CmpbPlayer() {
    LOGI("~Cmpb");
    release();
    LOGI("~Cmpb end\n");

    //restore bg color
    if(mColor.ui1_red == 0 && mColor.ui1_blue == 0xFF && mColor.ui1_green == 0)  //blue mute on
    {
        IMtkPb_Ctrl_Set_BG_Color(&mColor);
    }
}

status_t CmpbPlayer::release()
{
    LOGI("CmpbPlayer::release");
    Mutex::Autolock l(mMutex);
    ResetNosync();

    mState = STATE_END;

    return NO_ERROR;
}

// call only with mutex held
status_t CmpbPlayer::ResetNosync()
{
    LOGI("CmpbPlayer::reset_nosync");

    mReseted = true;

#if ASYNC_READ
    ASYNC_READ_Q_ITEM_T*  pt_msg;
    mQueueVectorLock.lock();
    int i = mAsyncReadQueue.size();
    while(i-- > 0)
    {
        pt_msg = mAsyncReadQueue[i];
        if(pt_msg)
        {
            free(pt_msg);
            pt_msg = 0;
        }
        mAsyncReadQueue.removeAt(i);
    }
    //mAsyncReadQueue.clear();
    mQueueItemCnt = 0;
    mQueueVectorLock.unlock();
#endif

    if(mState == STATE_STARTING || mState == STATE_STARTED || 
       mState == STATE_PAUSED  || mState == STATE_COMPLETE || mStart)
    {
        StopPlayer(true);
    }
    else
    {
        StopPlayer(false);
    }
    
    // close file
    if (mFileLocator.path) {
        free((void*)mFileLocator.path);
        mFileLocator.path = NULL;
    }
    if (mFileLocator.fd >= 0) {
        close(mFileLocator.fd);
    }

    mFileLocator.fd = -1;
    mFileLocator.offset = 0;
    mFileLocator.length = 0;

    mLoop = false;

    mStartTime = 0;
    mCurBufPercent = 0;
    mHttpURI = false;
    mMmsURI  = false;
    mSeeking = false;
    mStart = false;
    mForMetaInfo = false;

    m_TsProgIdx = 0;    

    mSeekDoneNeedPlay = false;

    mAbortRead = false;

    /*http living streaming */
    if ( m_pProtocolPlayer )
    {
        //delete m_pProtocolPlayer;
        CmpbProtocolPlayerFac::DestroyPlayer();
        m_pProtocolPlayer = NULL;
    }
    m_bProtocolPlayerUsed = false;

    return NO_ERROR;
}

IMTK_PB_ERROR_CODE_T CmpbPlayer::OpenCmpbRetry(IMTK_PB_HANDLE_T*                 phHandle,
                                        IMTK_PB_CTRL_BUFFERING_MODEL_T    eBufferingModel,
                                        IMTK_PB_CTRL_OPERATING_MODEL_T    eOperatingModel,
                                        uint8_t*                          pu1Profile)
{
    static int i = 0;
    IMTK_PB_ERROR_CODE_T e_return;

    e_return = IMtkPb_Ctrl_Open(phHandle,
                                 eBufferingModel,
                                 eOperatingModel,
                                 pu1Profile);
    if (e_return != IMTK_PB_ERROR_CODE_OK)                                
    {
        if(mForMetaInfo)
        {
            LOGE("IMtkPb_Ctrl_Open Failed, do not retry for metadata open !\n");
            return e_return;
        }
        
        LOGE("IMtkPb_Ctrl_Open Failed, force close old cmpb handle %x and retry !\n", 0x65430000 | i);
        int j = 0;

        while(j ++ < 4)
        {
            e_return = IMtkPb_Ctrl_Stop(0x65430000 | i);
            LOGE("IMtkPb_Ctrl_Stop(force stop) ret = %d !\n", e_return);

            e_return = IMtkPb_Ctrl_Close(0x65430000 | i);
            if (e_return != IMTK_PB_ERROR_CODE_OK)
            {
                LOGE("IMtkPb_Ctrl_Close(force close) Failed !\n");
            }
            else
            {
                LOGE("IMtkPb_Ctrl_Close(force close) ok !\n");
                i ++;
                i %= 4;
                break;
            }
            i ++;
            i %= 4;
        };

        e_return = IMtkPb_Ctrl_Open(phHandle,
                                     eBufferingModel,
                                     eOperatingModel,
                                     pu1Profile);
    }

    return e_return;
}

status_t CmpbPlayer::StopPlayer(bool bIsPlaying)
{    
    IMTK_PB_ERROR_CODE_T e_return;

    LOGI("CmpbPlayer::StopPlayer");

    if (m_bProtocolPlayerUsed)
    {
        LOGI("CmpbPlayer::Stop Live stream Player, bisplaying = %d, m_pProtocolPlayer = %d \n",
            bIsPlaying, m_pProtocolPlayer);
        if ( bIsPlaying && m_pProtocolPlayer )
        {
            m_pProtocolPlayer->Stop();
        }
        mStart = false;
        mState = STATE_STOPPED;
        return OK;
    }
    
    if(bIsPlaying)
    {
        e_return = IMtkPb_Ctrl_Stop(mPlayHdl);

        if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
            LOGE("IMtkPb_Ctrl_Stop() Failed !\n");
            mState = STATE_ERROR;
            //return UNKNOWN_ERROR;
        }

        LOGI("IMtkPb_Ctrl_Stop() ok !\n");
    }
    else
    {
        LOGI("StopPlayer() not in playing state, only do close() operation !\n");
    }
    mStart = false;
    
    e_return = IMtkPb_Ctrl_Close(mPlayHdl);

    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGE("IMtkPb_Ctrl_Close() Failed !\n");
        mState = STATE_ERROR;
        return UNKNOWN_ERROR;
    }
    
    LOGI("IMtkPb_Ctrl_Close() ok !\n");

    mStartTime = 0;
    mCurBufPercent = 0;

    mState = STATE_STOPPED;

    return OK;
}


IMTK_PB_CB_ERROR_CODE_T CmpbPlayer::FmPullOpen(IMTK_PB_HANDLE_T           hHandle,
                                    IMTK_PULL_HANDLE_T*        phPullSrc,
                                    void*                      pvAppTag)
{
    FILE* fp = 0;
    int fd = 0;
    CmpbPlayer *p_this;

    UNUSED(hHandle);

    p_this = (CmpbPlayer*)pvAppTag;
    if(!p_this)
    {
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

    if(p_this->mFileLocator.path != NULL)
    {
        LOGE("ERROR:mFileLocator.path is not NULL, should not use PULL mode\n");
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
    
    if(p_this->mFileLocator.fd == -1)
    {
        LOGE("ERROR:mFileLocator.fd is empty, can't open file!!!!!\n");
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

    fd = dup(p_this->mFileLocator.fd);
#if   USE_FILE_SYSTEM_CALL
    *phPullSrc = (IMTK_PULL_HANDLE_T)fd;
    
    LOGI("FmPullOpen ok handle = %x \n", (uint32_t)fd);
#else
    fp = fdopen(fd, "rb");
    if(fp == 0)
    {
        LOGE("FmPullOpen() open file failed !!\n");
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

    *phPullSrc = (IMTK_PULL_HANDLE_T)fp;
    
    LOGI("FmPullOpen ok handle = %x \n", (uint32_t)fp);
#endif
    return IMTK_PB_CB_ERROR_CODE_OK;
}

IMTK_PB_CB_ERROR_CODE_T CmpbPlayer::FmPullClose(IMTK_PULL_HANDLE_T         hPullSrc,
                                    void*                      pvAppTag)
{
    int i4_ret = 0;    
    
    UNUSED(pvAppTag);

#if 0
    if(mFileLocator.fd == -1)
    {
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
#endif
#if   USE_FILE_SYSTEM_CALL
    i4_ret = close((int)hPullSrc);
#else
    i4_ret = fclose((FILE*)hPullSrc);
#endif
    LOGI("FmPullClose(%x), i4_ret = %d \n", (uint32_t)hPullSrc, (int32_t)i4_ret);
    return ((i4_ret == 0) ? IMTK_PB_CB_ERROR_CODE_OK : IMTK_PB_CB_ERROR_CODE_NOT_OK);
}

IMTK_PB_CB_ERROR_CODE_T CmpbPlayer::FmPullRead(IMTK_PULL_HANDLE_T     hPullSrc,
                                  void*                  pvAppTag,
                                  uint8_t*               pu1DstBuf,
                                  uint32_t               u4Count, 
                                  uint32_t*              pu4Read)
{
    int i4_ret = 0;
    uint64_t    ui8_cur_pos = 0;
    CmpbPlayer*    p_this = NULL;

    p_this = (CmpbPlayer*)pvAppTag;
#if 0
    if(mFileLocator.fd == -1)
    {
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
#endif   
    //LOGI("enter FmPullRead(%x), buffer = %x, count = %d, i4_ret = %d \n", (uint32_t)hPullSrc, (uint32_t)pu1DstBuf, (int32_t)u4Count, (int32_t)i4_ret);
    if(pu1DstBuf == NULL || u4Count == 0 || pu1DstBuf == (uint8_t*)0xFFFFFFFF)
    {
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
#if   USE_FILE_SYSTEM_CALL
    ui8_cur_pos = (uint64_t)lseek64((int)hPullSrc, 0, SEEK_CUR);
#else
    ui8_cur_pos = ftello((FILE*)hPullSrc);
#endif
    if (ui8_cur_pos >= p_this->mFileLocator.length + p_this->mFileLocator.offset)
    {
        LOGI("FmPullRead() return file EOF");
        *pu4Read = 0;
        return IMTK_PB_CB_ERROR_CODE_EOF;
    }
    else if (ui8_cur_pos + u4Count > p_this->mFileLocator.length + p_this->mFileLocator.offset)
    {
        LOGI("FmPullRead() return file last bytes count = %d", u4Count);
        u4Count = p_this->mFileLocator.length + p_this->mFileLocator.offset - ui8_cur_pos;
    }
#if   USE_FILE_SYSTEM_CALL
    i4_ret = (int32_t)read((int)hPullSrc, pu1DstBuf, u4Count);
#else
    i4_ret = fread(pu1DstBuf, 1, u4Count, (FILE*)hPullSrc);
#endif

    //LOGI("FmPullRead(%x), buffer = %x, count = %d, i4_ret = %d \n", (uint32_t)hPullSrc, (uint32_t)pu1DstBuf, (int32_t)u4Count, (int32_t)i4_ret);

    if (i4_ret<0)
    {
        LOGE("FmPullRead() failed, ret = %d ...\n", i4_ret);
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
    else if(i4_ret > 0)
    {
        *pu4Read = i4_ret;
        return IMTK_PB_CB_ERROR_CODE_OK;
    }
    else
    {
        LOGI("FmPullRead(), end of file, return EOF ...\n");
        *pu4Read = 0;
        return IMTK_PB_CB_ERROR_CODE_EOF;
    }
}

#if ASYNC_READ
void CmpbPlayer::AsyncReadThread(void* param)
{
    int i4_ret;
    ASYNC_READ_Q_ITEM_T* pt_msg;
    CmpbPlayer*          p_this = NULL;    
    
    while(1)
    {
        uint16_t ui2_index;
        uint32_t msg;
        size_t   msg_size = sizeof(msg);
        if(h_que)
        {
            int32_t i4_ret = x_msg_q_receive(&ui2_index, &msg, &msg_size, &h_que, 1, X_MSGQ_OPTION_WAIT);
            if(0 != i4_ret)
            {
                LOGE("xxxx AsyncReadThread():: x_msg_q_receive failed , ret = %d !!!!!\n", i4_ret);
            }
            if(msg != 0) //prepare async cmd
            {
                LOGE("xxxx AsyncReadThread():: x_msg_q_receive prepare async command: this = %x !!!!!\n", msg);
                p_this = (CmpbPlayer*)msg;
                p_this->Internal_prepareAsync();
                continue;
            }
        }
        else
        {
            LOGE("xxxx AsyncReadThread(), error!!!! message queue create failed, return \n");
            return;
        }
//LOGE("xxxx AsyncReadThread(), before get msg, mQueueItemCnt = %d \n", mQueueItemCnt);

        mQueueVectorLock.lock();
        
        if(mQueueItemCnt > 0)
        {
            pt_msg = mAsyncReadQueue[0];
            mAsyncReadQueue.removeAt(0);
            mQueueItemCnt --;
        }
        else
        {
            LOGE("xxxx AsyncReadThread(), ERROR::mQueueItemCnt = %d \n", mQueueItemCnt);
            mQueueVectorLock.unlock();
            continue;
        }
        mQueueVectorLock.unlock();
//LOGE("xxxx AsyncReadThread(), get msg = %x, offset = %lld, size = %d !!!!! \n", pt_msg, pt_msg->i8Offset, pt_msg->u4Datalen, );

        if(pt_msg)
        {
            p_this = (CmpbPlayer*)pt_msg->pvAppTag;
            if(!p_this)
            {
                LOGE("FmPullReadAsync thread() ERROR!!!!!, p_this == NULL!!!");
                continue;
            }
            mAbortReadLock.lock();       

            if(p_this->mAbortRead)  //abort read
            {
                LOGE("xxxx AsyncReadThread(), received abort read msg, skip this read request now... \n");
                mAbortReadLock.unlock();
                continue;
            }
            
            if(!pt_msg->bAbort)
            {
                if (pt_msg->i8Offset >= p_this->mFileLocator.length + p_this->mFileLocator.offset)
                {
                    LOGI("FmPullReadAsync thread() return file EOF");
                    pt_msg->pfnNotify(IMTK_PB_CTRL_PULL_READ_EOS,
                              pt_msg->pvRdAsyTag,
                              (uint32_t)pt_msg->pu1BufAddr,
                              (uint32_t)0);

                    free(pt_msg);
                    pt_msg = 0;
                    mAbortReadLock.unlock();
                    continue;
                }
                else if (pt_msg->i8Offset + pt_msg->u4Datalen > p_this->mFileLocator.length + p_this->mFileLocator.offset)
                {
                    LOGI("FmPullRead() return file last bytes count = %d", pt_msg->u4Datalen);
                    pt_msg->u4Datalen = p_this->mFileLocator.length + p_this->mFileLocator.offset - pt_msg->i8Offset;
                }                    
#if   USE_FILE_SYSTEM_CALL
                off64_t  off64_ret;
                if(0 > (off64_ret = lseek64((int)pt_msg->hAsyncRdHdl, (off64_t)pt_msg->i8Offset, SEEK_SET)))
                {
                    LOGE("AsyncReadThread() seek failed, seek_ret = %d, offset = %lld errno = %s \n", 
                        off64_ret, pt_msg->i8Offset, strerror(errno));
                    pt_msg->pfnNotify(IMTK_PB_CTRL_PULL_FAIL,
                              pt_msg->pvRdAsyTag,
                              (uint32_t)pt_msg->pu1BufAddr,
                                    0);
                }
                else
                {
                    off64_ret = read((int)pt_msg->hAsyncRdHdl, pt_msg->pu1BufAddr, pt_msg->u4Datalen);
                    i4_ret = (int32_t)off64_ret; //read size must be less than 4G
#else
                if(0 != (i4_ret = fseeko((FILE*)pt_msg->hAsyncRdHdl, pt_msg->i8Offset, SEEK_SET)))
                {
                    LOGE("AsyncReadThread() seek failed, i4_ret = %d, offset = %lld errno = %s \n", 
                        i4_ret, pt_msg->i8Offset, strerror(errno));
                    pt_msg->pfnNotify(IMTK_PB_CTRL_PULL_FAIL,
                              pt_msg->pvRdAsyTag,
                              (uint32_t)pt_msg->pu1BufAddr,
                                    0);
                }
                else
                {
                    i4_ret = fread(pt_msg->pu1BufAddr, 1, pt_msg->u4Datalen, (FILE*)pt_msg->hAsyncRdHdl);
#endif
                    //LOGI("FmPullReadAsync(%x), buffer = %x, count = %d, i4_ret = %d \n", pt_msg->hAsyncRdHdl, (uint32_t)pt_msg->pu1BufAddr, (int32_t)pt_msg->u4Datalen, (int32_t)i4_ret);
                    if (i4_ret<0)
                    {
                        LOGE("AsyncReadThread() read failed, i4_ret = %d, offset = %lld errno = %s \n", 
                        i4_ret, pt_msg->i8Offset, strerror(errno));
                        pt_msg->pfnNotify(IMTK_PB_CTRL_PULL_FAIL,
                                  pt_msg->pvRdAsyTag,
                                  (uint32_t)pt_msg->pu1BufAddr,
                                    0);
                    }
                    else
                    {
                        if((uint32_t)i4_ret == pt_msg->u4Datalen)
                        {
                            pt_msg->pfnNotify(IMTK_PB_CTRL_PULL_READ_OK,
                                  pt_msg->pvRdAsyTag,
                                  (uint32_t)pt_msg->pu1BufAddr,
                                  (uint32_t)i4_ret);
                        }
                        else
                        {
                            LOGI("FmPullReadAsync(): send EOS \n");
                            pt_msg->pfnNotify(IMTK_PB_CTRL_PULL_READ_EOS,
                                  pt_msg->pvRdAsyTag,
                                  (uint32_t)pt_msg->pu1BufAddr,
                                    (uint32_t)i4_ret);
                        }
                    }
                }
            }
            else
            {   //abort read
                LOGI("FmPullReadAsync(): abort ok ID = %x\n", pt_msg);
                pt_msg->pfnNotify(IMTK_PB_CTRL_PULL_ABORT_OK,
                              pt_msg->pvRdAsyTag,
                              (uint32_t)pt_msg->pu1BufAddr,
                              (uint32_t)0);
            }

            free(pt_msg);
            pt_msg = 0;
            
            mAbortReadLock.unlock();
        }   
    }

    return;
}
#endif

IMTK_PB_CB_ERROR_CODE_T CmpbPlayer::FmPullReadAsync(IMTK_PULL_HANDLE_T         hPullSrc,
                                           void*                      pvAppTag,
                                           uint8_t*                   pu1Dst,
                                           uint32_t                   u4DataLen,
                                           IMtkPb_Ctrl_Pull_Nfy_Fct   pfnNotify,
                                           void*                      pvRdAsyTag,   
                                           uint32_t*                  pu4ReqId)
{
#if ASYNC_READ
    int             i4_ret;
    ASYNC_READ_Q_ITEM_T*  pt_queue_item = NULL;
    off64_t         offset;

#if 0
    if(mFileLocator.fd == -1)
    {
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
#endif
#if   USE_FILE_SYSTEM_CALL
    offset = lseek64((int)hPullSrc, 0, SEEK_CUR);
#else
    offset = ftello((FILE*)hPullSrc);
#endif

    pt_queue_item = (ASYNC_READ_Q_ITEM_T*)malloc(sizeof(ASYNC_READ_Q_ITEM_T));
    if (pt_queue_item == 0)
    {
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

    memset(pt_queue_item, 0, sizeof(ASYNC_READ_Q_ITEM_T));

    pt_queue_item->hAsyncRdHdl = hPullSrc;
    pt_queue_item->pu1BufAddr  = pu1Dst;
    pt_queue_item->u4Datalen   = u4DataLen;
    pt_queue_item->i8Offset    = (int64_t)offset;

    pt_queue_item->pfnNotify   = pfnNotify;
    pt_queue_item->pvRdAsyTag  = pvRdAsyTag;
    pt_queue_item->pvAppTag    = pvAppTag;

    *pu4ReqId = (uint32_t)pt_queue_item;

    while (mQueueItemCnt >= MAX_ASYNC_READ_QUEUE_CNT) 
    {
        LOGI("xxxx AsyncReadThread(), queue is full!!!!! \n");
        usleep(100000);
    }

    mQueueVectorLock.lock();
    mAsyncReadQueue.push(pt_queue_item);
    mQueueItemCnt ++;

    mQueueVectorLock.unlock();

    if(h_que)
    {
        uint32_t msg = 0;
        int32_t  i4_ret = x_msg_q_send(h_que, &msg, sizeof(msg), 0);
        if(0 != i4_ret)
        {
            LOGE("enter FmPullReadAsync(): x_msg_q_send failed, ret = %d !!!!!\n", i4_ret);
        }
    }
    else
    {
        LOGE("enter FmPullReadAsync(): message queue create error!\n");
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

    //LOGI("enter FmPullReadAsync(): data len = %d\n", u4DataLen);
    
#else  //use sync read instead
    int i4_ret = 0;

    UNUSED(pvAppTag);
    
    LOGI("ENTER FmPullReadAsync(%x), buffer = %x, count = %d, i4_ret = %d \n", (uint32_t)hPullSrc, (uint32_t)pu1Dst, (int32_t)u4DataLen, (int32_t)i4_ret);
    if(pu1Dst == NULL || u4DataLen == 0 || pu1Dst == (uint8_t*)0xFFFFFFFF)
    {
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
    
#if   USE_FILE_SYSTEM_CALL
    i4_ret = (int32_t)read((int)hPullSrc, pu1Dst, u4DataLen);
#else
    i4_ret = fread(pu1Dst, 1, u4DataLen, (FILE*)hPullSrc);
#endif
    
    LOGI("FmPullReadAsync(%x), buffer = %x, count = %d, i4_ret = %d \n", (uint32_t)hPullSrc, (uint32_t)pu1Dst, (int32_t)u4DataLen, (int32_t)i4_ret);
    
    if (i4_ret<0)
    {
        pfnNotify(IMTK_PB_CTRL_PULL_FAIL,
                  pvRdAsyTag,
                  (uint32_t)pu1Dst,
                  0);
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
    else
    {
        if((uint32_t)i4_ret == u4DataLen)
        {
            pfnNotify(IMTK_PB_CTRL_PULL_READ_OK,
                  pvRdAsyTag,
                  (uint32_t)pu1Dst,
                  (uint32_t)i4_ret);
        }
        else
        {
            LOGI("FmPullReadAsync(): send EOS \n");
            pfnNotify(IMTK_PB_CTRL_PULL_READ_EOS,
                  pvRdAsyTag,
                  (uint32_t)pu1Dst,
                  (uint32_t)i4_ret);            
        }
    }
#endif

    return IMTK_PB_CB_ERROR_CODE_OK;
}

IMTK_PB_CB_ERROR_CODE_T CmpbPlayer::FmPullAbortReadAsync(IMTK_PULL_HANDLE_T     hPullSrc,
                                                 void*                  pvAppTag,
                                                 uint32_t               u4ReqId)
{
    CmpbPlayer*            p_this = NULL;
    IMTK_PB_CB_ERROR_CODE_T e_ret = IMTK_PB_CB_ERROR_CODE_OK;
    
    p_this = (CmpbPlayer*)pvAppTag;

    if(!p_this)
    {
        LOGE("FmPullAbortReadAsync(ID=%x) error: this = NULL !!!!! \n", u4ReqId);
    }
#if 0
    if(mFileLocator.fd == -1)
    {
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
#endif
#if ASYNC_READ
    LOGI("FmPullAbortReadAsync(ID=%x) hPullSrc = %x, pvAppTag=%x \n", u4ReqId, hPullSrc, pvAppTag);

    mQueueVectorLock.lock();
    uint32_t u4QueItemCnt = mQueueItemCnt; //mAsyncReadQueue.size();
    uint32_t i = 0;
    while (i < u4QueItemCnt) 
    {
        ASYNC_READ_Q_ITEM_T* pt_msg;
    
        pt_msg = mAsyncReadQueue[i];
        if((uint32_t)pt_msg == u4ReqId)
        {
            pt_msg->bAbort = true;
            LOGI("FmPullAbortReadAsync(ID=%x) found, hPullSrc = %x, pvAppTag=%x \n", u4ReqId, hPullSrc, pvAppTag);
            break;
        }
        i ++;
    }
    if(i >= u4QueItemCnt)
    {
        LOGE("FmPullAbortReadAsync(ID=%x) failed, hPullSrc = %x, pvAppTag=%x \n", u4ReqId, hPullSrc, pvAppTag);
        e_ret = IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
    mQueueVectorLock.unlock();

    mAbortReadLock.lock();
    p_this->mAbortRead = true;
    mAbortReadLock.unlock();
#endif

    return e_ret;
}

IMTK_PB_CB_ERROR_CODE_T CmpbPlayer::FmPullByteSeek(IMTK_PULL_HANDLE_T         hPullSrc,
                                         void*                      pvAppTag,
                                         int64_t                    i8SeekPos,
                                         uint8_t                    u1Whence,
                                         uint64_t*                  pu8CurPos)
{
    int whence;
    CmpbPlayer *p_this;
    int   i4_ret;

    p_this = (CmpbPlayer*)pvAppTag;
    if(!p_this)
    {
        LOGE("FmPullByteSeek() !p_this");
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
    
    UNUSED(pvAppTag);
    
    if(p_this->mFileLocator.fd == -1)
    {
        LOGE("FmPullByteSeek() p_this->mFileLocator.fd == -1");
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

    switch (u1Whence & ~(0x80))
    {
    case IMTK_CTRL_PULL_SEEK_BGN:
            i8SeekPos = i8SeekPos + p_this->mFileLocator.offset;
        whence = SEEK_SET;
        break;
    case IMTK_CTRL_PULL_SEEK_CUR:
        whence = SEEK_CUR;
        break;
    case IMTK_CTRL_PULL_SEEK_END:
            i8SeekPos = p_this->mFileLocator.length + i8SeekPos + p_this->mFileLocator.offset;
        whence = SEEK_SET;
        break;
    default:
            LOGE("FmPullByteSeek() default IMTK_PB_CB_ERROR_CODE_NOT_OK");
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
    
#if   USE_FILE_SYSTEM_CALL
    off64_t  old_offset;
    off64_t  new_offset;

    old_offset = lseek64((int)hPullSrc, 0, SEEK_CUR);
    if(0 > old_offset)
    {
        LOGE("FmPullByteSeek() ftello failed, errno = %s \n", strerror(errno));
    }
    
    //LOGI("FmPullByteSeek(%x), u1Whence = %d, i8SeekPos = %lld, current offset = %lld \n", (uint32_t)hPullSrc,(int32_t)whence, i8SeekPos, old_offset);
    if(0 > (new_offset = lseek64((int)hPullSrc, i8SeekPos, whence)))
    {
        LOGE("FmPullByteSeek() failed, i4_ret = %lld, errno = %s \n", new_offset, strerror(errno));
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }        

    //LOGI("FmPullByteSeek() ok, new offset = %d, this->offset = %d, this->len = %d \n", 
        //(int32_t)offset, (int32_t)p_this->mFileLocator.offset, (int32_t)p_this->mFileLocator.length);
    if (new_offset < p_this->mFileLocator.offset || new_offset > (p_this->mFileLocator.offset + p_this->mFileLocator.length))
    {
        LOGE("FmPullByteSeek() offset too large!!!\n");
    }

    if(new_offset != i8SeekPos)
    {
        LOGE("FmPullByteSeek(warning!!) current position is not equal to the given seek position!!!\n");
    }
#else
    off_t old_offset;
    off_t offset;
    
    old_offset = ftello((FILE*)hPullSrc);
    if(-1 == old_offset)
    {
        LOGE("FmPullByteSeek() ftello failed, errno = %s \n", strerror(errno));
    }
    
    //LOGI("FmPullByteSeek(%x), u1Whence = %d, i8SeekPos = %lld, current offset = %d \n", (uint32_t)hPullSrc,(int32_t)whence, i8SeekPos, old_offset);
    if(0 != (i4_ret = fseeko((FILE*)hPullSrc, i8SeekPos, whence)))
    {
        LOGE("FmPullByteSeek() failed, i4_ret = %d, errno = %s \n", i4_ret, strerror(errno));
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }        

    offset = ftello((FILE*)hPullSrc);
    //LOGI("FmPullByteSeek() ok, new offset = %d, this->offset = %d, this->len = %d \n", 
        //(int32_t)offset, (int32_t)p_this->mFileLocator.offset, (int32_t)p_this->mFileLocator.length);
    if (offset < p_this->mFileLocator.offset || offset > (p_this->mFileLocator.offset + p_this->mFileLocator.length))
    {
        fseeko((FILE*)hPullSrc, old_offset, SEEK_SET); //restore original position
        LOGE("FmPullByteSeek() offset too large!!!\n");
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
#endif

    *pu8CurPos = i8SeekPos;

    return IMTK_PB_CB_ERROR_CODE_OK;
}

IMTK_PB_CB_ERROR_CODE_T CmpbPlayer::FmPullGetInputLen(IMTK_PULL_HANDLE_T  hPullSrc,
                                                      void*               pvAppTag,
                                                      uint64_t*           pu8Len)
{
    CmpbPlayer *p_this;

    UNUSED(hPullSrc);

    p_this = (CmpbPlayer*)pvAppTag;
    if(!p_this)
    {
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

    if(p_this->mFileLocator.fd != -1)
    {
        *pu8Len = p_this->mFileLocator.length;
    }
    else
    {
        *pu8Len = 0;
    }    
    LOGI("FmPullGetInputLen(%x) ok, length = %lld \n",(uint32_t)hPullSrc, *pu8Len);

    return IMTK_PB_CB_ERROR_CODE_OK;
}

IMTK_PB_CB_ERROR_CODE_T CmpbPlayer::MediaPbCallback(IMTK_PB_CTRL_EVENT_T       eEventType,
                                               void*                      pvTag,
                                               uint32_t                   u4Data)
{
    CmpbPlayer          *p_this;
    int32_t              i4_event = 0;
    IMTK_PB_ERROR_CODE_T e_return;
    uint32_t             ui4_width = 0;
    uint32_t             ui4_height = 0;
    IMTK_PB_CTRL_GET_MEDIA_INFO_T t_mediainfo;

    p_this = (CmpbPlayer*)pvTag;
    if(!p_this)
    {
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }  

     if((p_this->mState == STATE_ERROR && eEventType != IMTK_PB_CTRL_EVENT_EOS) //for stress test
        || p_this->mState == STATE_STOPPED)
    {
        LOGE("CmpbPlayer::media_pb_cb, STATE ERROR!!!!, event = %d, state = %d \n", eEventType, p_this->mState);
        return IMTK_PB_CB_ERROR_CODE_OK;
    }

    if(eEventType != IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE)
    {
        LOGI("CmpbPlayer::media_pb_cb, event = %d \n", eEventType);
    }

    i4_event = (int32_t)eEventType;
    
    switch (i4_event)
    {
        case IMTK_PB_CTRL_EVENT_PLAY_TO_END:
            LOGI("CmpbPlayer::media_pb_cb, IMTK_PB_CTRL_EVENT_PLAY_TO_END, mLoop = %d \n", p_this->mLoop);
            if(p_this->mLoop)
            {
                //e_return = IMtkPb_Ctrl_Seek_For_RptPlay(p_this->mPlayHdl, 0);
                LOGI("CmpbPlayer::media_pb_cb, IMtkPb_Ctrl_Seek_For_RptPlay = %d \n", e_return);
            }
            break;
        case IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE:
            //LOGI("CmpbPlayer::media_pb_cb, received current time updated = %d \n", u4Data);
            #if 0
            if(p_this->mHttpURI || p_this->mMmsURI)
            {
                IMTK_PB_CTRL_GET_MEDIA_INFO_T tMediaInfo;
                uint32_t u4Percent;
                uint32_t u4TmPercent;
                
                e_return = IMtkPb_Ctrl_GetMediaInfo(p_this->mPlayHdl, &tMediaInfo);
                if (e_return != IMTK_PB_ERROR_CODE_OK)
                {
                    tMediaInfo.u4TotalDuration = 0;
                }

                IMtkPb_Ctrl_GetBufferFullness(p_this->mPlayHdl, &u4Percent);        
                if(tMediaInfo.u4TotalDuration != 0 && tMediaInfo.u8Size != 0)
                {
                    u4Percent = (URI_BUF_SIZE * u4Percent / 100 + tMediaInfo.u8Size * u4Data / tMediaInfo.u4TotalDuration) \
                                      * 100 / tMediaInfo.u8Size;

                    u4TmPercent = u4Data * 100 / tMediaInfo.u4TotalDuration;

                    if(u4Percent <= u4TmPercent)
                    {
                        u4Percent = u4TmPercent + 2;
                    }

                    if(u4Percent > 100)
                    {
                        u4Percent = 100;
                    }

                    if(u4Percent > p_this->mCurBufPercent && u4Percent <= 100) //only send 99% here, 100% will send when receive EOS.
                    {
                        p_this->mCurBufPercent = u4Percent;
                        p_this->sendEvent(MEDIA_BUFFERING_UPDATE, p_this->mCurBufPercent);
                        LOGE("cmpb_getCurrentPosition(): send percentage(%d)%% !\n", p_this->mCurBufPercent);
                    }
                }
            } 
            #endif
            break;

        case IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW:
            LOGI("CmpbPlayer::media_pb_cb, received IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW\n");
            if(p_this->mHttpURI || p_this->mMmsURI)
            {
                p_this->sendEvent(MEDIA_INFO, MTK_MEDIA_INFO_BUFFER_UNDERFLOW); 
                p_this->sendEvent(MEDIA_INFO, MTK_MEDIA_INFO_BUFFER_START);                
            }
            break;

        case IMTK_PB_CTRL_EVENT_BUFFER_READY:
            LOGI("CmpbPlayer::media_pb_cb, received IMTK_PB_CTRL_EVENT_BUFFER_READY\n");
            if(p_this->mHttpURI || p_this->mMmsURI)
            {
                p_this->sendEvent(MEDIA_INFO, MTK_MEDIA_INFO_BUFFER_READY);
                p_this->sendEvent(MEDIA_INFO, MTK_MEDIA_INFO_BUFFER_END); 
            }
            break;

        case IMTK_PB_CTRL_EVENT_TOTAL_TIME_UPDATE:
            LOGI("CmpbPlayer::media_pb_cb, received Total duration updated = %d \n", u4Data);
            break;
            
        case IMTK_PB_CTRL_EVENT_EOS:
            p_this->mSeeking = false;
            LOGI("CmpbPlayer::MediaPbCallback() received eos, need loop = %d \n", p_this->mLoop);
            if(p_this->mLoop)
            {                
                LOGI("CmpbPlayer::MediaPbCallback() received eos, do repeate play now \n");
                e_return = IMtkPb_Ctrl_Stop(p_this->mPlayHdl);

                if (e_return != IMTK_PB_ERROR_CODE_OK)
                {
                    LOGE("IMtkPb_Ctrl_Stop() Failed !\n");
                    p_this->mState = STATE_ERROR;
                    //return UNKNOWN_ERROR;
                }
                
                e_return = IMtkPb_Ctrl_Play(p_this->mPlayHdl, 0);
                if (e_return != IMTK_PB_ERROR_CODE_OK)
                {
                    LOGE("IMtkPb_Ctrl_Play() Failed !\n");
                    p_this->mState = STATE_ERROR;
                    //return UNKNOWN_ERROR;
                }
                
            }
            else
            {
                p_this->mState = STATE_COMPLETE;
                p_this->sendEvent(MEDIA_PLAYBACK_COMPLETE);
            }
            break;

        case IMTK_PB_CTRL_EVENT_TIMESEEK_DONE:
            if(p_this->mSeeking)
            {
                p_this->mSeeking = false;
                LOGI("CmpbPlayer::MediaPbCallback() received IMTK_PB_CTRL_EVENT_TIMESEEK_DONE \n");
                p_this->sendEvent(MEDIA_SEEK_COMPLETE);
            }
            if(p_this->mSeekDoneNeedPlay)
            {
                LOGI("CmpbPlayer::MediaPbCallback() Play after seek done... \n");
                p_this->StartPlayer();
                LOGI("CmpbPlayer::MediaPbCallback() Play after seek done...  ok \n");
                p_this->mSeekDoneNeedPlay = false;
            }
            break;
        case IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR:
            LOGI("CmpbPlayer::MediaPbCallback() received IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR, u4Data = %d \n", u4Data);
            if(u4Data & IMTK_PB_CTRL_ERROR_VIDEO_UNPLAYABLE)
            {
                p_this->mVidUnplayble = true;
                LOGI("CmpbPlayer::MediaPbCallback() send event: MTK_MEDIA_INFO_VID_CODEC_NOT_SUPPORT \n");
                p_this->sendEvent(MEDIA_ERROR, MTK_MEDIA_INFO_VID_CODEC_NOT_SUPPORT);
            }
            if(u4Data & IMTK_PB_CTRL_ERROR_AUDIO_UNPLAYABLE)
            {
                LOGI("CmpbPlayer::MediaPbCallback() send event: MTK_MEDIA_INFO_AUD_CODEC_NOT_SUPPORT \n");
                p_this->sendEvent(MEDIA_INFO, MTK_MEDIA_INFO_AUD_CODEC_NOT_SUPPORT);               
            }
            if(u4Data != (uint32_t)IMTK_PB_CTRL_ERROR_VIDEO_UNPLAYABLE &&
               u4Data != (uint32_t)IMTK_PB_CTRL_ERROR_AUDIO_UNPLAYABLE)
            {
                if(u4Data == (IMTK_PB_CTRL_ERROR_VIDEO_UNPLAYABLE | IMTK_PB_CTRL_ERROR_AUDIO_UNPLAYABLE))
                {
                    LOGI("CmpbPlayer::MediaPbCallback() both video & audio not support !!!!! \n");
                    LOGI("CmpbPlayer::MediaPbCallback() send event: MTK_MEDIA_INFO_FILE_NOT_SUPPORT \n");
                    p_this->sendEvent(MEDIA_ERROR, MTK_MEDIA_INFO_FILE_NOT_SUPPORT);
#if 0
                    LOGI("CmpbPlayer::MediaPbCallback() received IMTK_PB_CTRL_EVENT_PLAY_DONE\n");
                    ////////for ts switch program
                    IMTK_PB_CTRL_MEDIA_INFO_T tMediaInfo;
                    uint32_t    i4_size;
                    e_return = IMtkPb_Ctrl_Get(p_this->mPlayHdl,
                                               IMTK_PB_CTRL_GET_TYPE_MEDIA_INFO,
                                               &tMediaInfo,
                                               sizeof(tMediaInfo));
                    if(e_return == IMTK_PB_ERROR_CODE_OK && 
                       tMediaInfo.eMediaType == IMTK_PB_CTRL_MEDIA_TYPE_MPEG2_TS &&
                       tMediaInfo.u1ProgramNum > 1 && p_this->m_TsProgIdx < tMediaInfo.u1ProgramNum)
                    {
                        p_this->m_TsProgIdx ++;
                        LOGE("MediaPbCallback() set ts program idx= %d !\n", p_this->m_TsProgIdx);
                        i4_size = sizeof(p_this->m_TsProgIdx);
                        IMtkPb_Ctrl_SetProgram(p_this->mPlayHdl,
                             IMTK_PB_CTRL_SET_TYPE_TS_PROGRAM_IDX,
                             &(p_this->m_TsProgIdx), &i4_size);
                        break;
                    }   
#endif
                }
                else if(u4Data == IMTK_PB_CTRL_ERROR_FILE_NOT_SUPPORT)
                {
                    LOGI("CmpbPlayer::MediaPbCallback() send event: MTK_MEDIA_INFO_FILE_NOT_SUPPORT \n");
                    p_this->sendEvent(MEDIA_ERROR, MTK_MEDIA_INFO_FILE_NOT_SUPPORT);
                }
                else if(u4Data == IMTK_PB_CTRL_ERROR_OPEN_FILE_FAIL)                
                {
                    LOGI("CmpbPlayer::MediaPbCallback() send event: IMTK_PB_CTRL_ERROR_OPEN_FILE_FAIL \n");
                    p_this->sendEvent(MEDIA_ERROR, MTK_MEDIA_INFO_OPEN_FILE_FAILED);
                }
                else if(u4Data == IMTK_PB_CTRL_ERROR_FILE_CORRUPT)                
                {
                    LOGI("CmpbPlayer::MediaPbCallback() send event: MTK_MEDIA_INFO_FILE_CORRUPT \n");
                    p_this->sendEvent(MEDIA_ERROR, MTK_MEDIA_INFO_FILE_CORRUPT);
                }
                else if(IMTK_PB_CTRL_ERROR_GET_DATA_FAIL == u4Data)
                {
                    LOGI("CmpbPlayer::MediaPbCallback() send event: IMTK_PB_CTRL_ERROR_GET_DATA_FAIL \n");
                    p_this->sendEvent(MEDIA_ERROR, MTK_MEDIA_INFO_GET_DATA_FAIL);
                }
                else
                {
                    LOGI("CmpbPlayer::MediaPbCallback() send event: MEDIA_ERROR_UNKNOWN \n");
                    p_this->mState = STATE_ERROR;
                    p_this->sendEvent(MEDIA_ERROR, MEDIA_ERROR_UNKNOWN);
                }
#if 0
                if(u4Data == IMTK_PB_CTRL_ERROR_FILE_NOT_SUPPORT ||
                   u4Data == IMTK_PB_CTRL_ERROR_OPEN_FILE_FAIL   ||
                   u4Data == IMTK_PB_CTRL_ERROR_FILE_CORRUPT)
                {
                    LOGE("MediaPbCallback() received file corrupt msg !!!!!!!!\n");
                    e_return = IMtkPb_Ctrl_Close(p_this->mPlayHdl);                                    
                    if (e_return != IMTK_PB_ERROR_CODE_OK)
                    {
                        LOGE("IMtkPb_Ctrl_Close() Failed , e_return = %d!\n", e_return);
                    }
                    p_this->mPlayHdl = 0;
                }
#endif
            }
            else
            {
                p_this->mState = STATE_STARTED;
            }
            break;
        case IMTK_PB_CTRL_EVENT_EOF:
            LOGI("CmpbPlayer::MediaPbCallback() received IMTK_PB_CTRL_EVENT_EOF\n");
            if(p_this->mHttpURI || p_this->mMmsURI)
            {
                p_this->mCurBufPercent = 100;
                p_this->sendEvent(MEDIA_BUFFERING_UPDATE, 100);
            }
            break;
        case IMTK_PB_CTRL_EVENT_STEP_DONE:
        case IMTK_PB_CTRL_EVENT_GET_BUF_READY:
            LOGI("CmpbPlayer::MediaPbCallback() received STEP_DONE:GET_BUF_READY\n");
            break;

        case IMTK_PB_CTRL_EVENT_PLAY_DONE:
            LOGI("CmpbPlayer::MediaPbCallback() received IMTK_PB_CTRL_EVENT_PLAY_DONE\n");
            if(p_this->mState == STATE_ERROR)
            {
                LOGI("CmpbPlayer::MediaPbCallback() already in error state\n");
                return IMTK_PB_CB_ERROR_CODE_OK;
            }
            p_this->mState = STATE_STARTED;
            e_return = IMtkPb_Ctrl_GetMediaInfo(p_this->mPlayHdl,
                                                &t_mediainfo);
            if(e_return != IMTK_PB_ERROR_CODE_OK)
            {
                LOGE("MediaPbCallback() get media info failed !\n");
                t_mediainfo.eMediaType = IMTK_PB_CTRL_MEDIA_TYPE_UNKNOWN;
            }
            else
            {
                LOGI("MediaPbCallback() : file format = %d !\n", t_mediainfo.eMediaType);
            }
            #if PLAY_IN_PREPARE
            if(p_this->mHttpURI ||// p_this->mMmsURI ||  mms do not auto pause
               t_mediainfo.eMediaType == IMTK_PB_CTRL_MEDIA_TYPE_MPEG2_TS)
            {
                int i = 0;
                while((e_return = IMtkPb_Ctrl_Pause(p_this->mPlayHdl)) != IMTK_PB_ERROR_CODE_OK)
                {
                    if(u4Data == 0)
                    {
                        LOGI("CmpbPlayer::MediaPbCallback() play done with error param!!!!! \n");  
                        break;
                    }
                    if(p_this->mVidUnplayble) //break here
                    {
                        LOGI("CmpbPlayer::MediaPbCallback() video unplayalbe, do not need pause \n");  
                        break;
                    }
                    i ++;
                    LOGI("CmpbPlayer::MediaPbCallback() wait for pause...\n");
                    usleep(500000);
                    if(p_this->mReseted) //user exit
                    {
                        LOGI("CmpbPlayer::MediaPbCallback() ap call reset, return directly\n");
                        return IMTK_PB_CB_ERROR_CODE_OK;
                    }
                    if(i > 10)  //5s
                    {
                        break;
                    }
                }

                if (e_return != IMTK_PB_ERROR_CODE_OK)                                
                {
                    LOGE("MediaPbCallback() auto pause failed !\n");
                }
                else
                {
                    LOGI("MediaPbCallback() auto pause ok !\n");
                    p_this->mState = STATE_PAUSED;
                }
                if(u4Data == 0)
                {
                    p_this->sendEvent(MEDIA_ERROR, MEDIA_ERROR_UNKNOWN);
                    LOGI("MediaPbCallback() send error event to ap ok !\n");
                }
                else
                {
                p_this->sendEvent(MEDIA_PREPARED);
                LOGI("MediaPbCallback() send MEDIA_PREPARED event to ap ok !\n");
            }
            }
            #endif
            break;

        case IMTK_PB_CTRL_EVENT_PREPARE_DONE:
            LOGI("CmpbPlayer::MediaPbCallback() received IMTK_PB_CTRL_EVENT_PREPARE_DONE\n");
            p_this->mState = STATE_PREPARED;
            e_return = IMtkPb_Ctrl_GetVideoResolution(p_this->mPlayHdl, &ui4_width, &ui4_height);
            if (e_return != IMTK_PB_ERROR_CODE_OK)
            {
                LOGE("MediaPbCallback() get video width and height failed !\n");
            }
            else
            {
        #if SUPPORT_CTS
                LOGI("MediaPbCallback() get video width and height %d, %d !\n", ui4_width, ui4_height);
                if(ui4_width != 0 && ui4_height != 0)
                {
                    p_this->sendEvent(MEDIA_SET_VIDEO_SIZE, ui4_width, ui4_height);
                }
                //p_this->sendEvent(MEDIA_SET_VIDEO_SIZE, 1920, 1080);  /* for CR: DTV00410141*/
        #endif
            }

            e_return = IMtkPb_Ctrl_GetMediaInfo(p_this->mPlayHdl,
                                                &t_mediainfo);
            if(e_return != IMTK_PB_ERROR_CODE_OK)
            {
                LOGE("MediaPbCallback() get media info failed !\n");
                t_mediainfo.eMediaType = IMTK_PB_CTRL_MEDIA_TYPE_UNKNOWN;
            }
            else
            {
                LOGI("MediaPbCallback() : file format = %d !\n", t_mediainfo.eMediaType);
            }
            #if PLAY_IN_PREPARE
            if(p_this->mHttpURI || p_this->mMmsURI || 
               t_mediainfo.eMediaType == IMTK_PB_CTRL_MEDIA_TYPE_MPEG2_TS)
            {
                if(p_this->mReseted) //user exit
                {
                    LOGI("CmpbPlayer::MediaPbCallback() ap call reset, return directly\n");
                    return IMTK_PB_CB_ERROR_CODE_OK;
                }
                  p_this->mStart = true;
                e_return = IMtkPb_Ctrl_Play(p_this->mPlayHdl, p_this->mStartTime);
                if(p_this->mStartTime != 0)
                {
                    LOGI("cmpb_start() start from time: %d seconds\n", (int32_t)p_this->mStartTime);
                    p_this->mStartTime = 0;
                    p_this->sendEvent(MEDIA_SEEK_COMPLETE); //seek before play started, will send seek done here.
                }
                    
                if (e_return != IMTK_PB_ERROR_CODE_OK)                                
                {
                    LOGE("MediaPbCallback() auto play fail !\n");
                    p_this->mState = STATE_ERROR;
                    p_this->sendEvent(MEDIA_ERROR, MEDIA_ERROR_UNKNOWN);
                }
                else
                {
                    if(p_this->mMmsURI)
                    {
                        p_this->sendEvent(MEDIA_PREPARED);
                        LOGI("MediaPbCallback(MMS) send MEDIA_PREPARED to AP ok !\n");
                    }
                    LOGI("MediaPbCallback() auto play ok !\n");
                }
            }
            else
            #endif
            {
                p_this->sendEvent(MEDIA_PREPARED);
                LOGI("MediaPbCallback() send MEDIA_PREPARED to AP ok !\n");
            }
            
            break;

        default :
            LOGI("CmpbPlayer::MediaPbCallback() default case \n");
            break;
    }   
    
    return IMTK_PB_CB_ERROR_CODE_OK;
}


status_t CmpbPlayer::initCheck() 
{
    LOGI("cmpb_initCheck");
    
    if (mState == STATE_ERROR)
    {
        return ERROR_EAS_FAILURE;
    }
    return OK;
}

void CmpbPlayer::initCookie(
        const KeyedVector<String8, String8> *cookie) 
{
    mCookie = String8();

    if (cookie == NULL) {
        return;
    }

    for (size_t i = 0; i < cookie->size(); ++i) {
        String8 line;
        line.append(cookie->keyAt(i));
        line.append("=");
        line.append(cookie->valueAt(i));
        line.append(";");

        mCookie.append(line);
    }
}


void CmpbPlayer::initHeaders(
        const KeyedVector<String8, String8> *overrides) 
{
    mHeaders = String8();

    mHeaders.append("User-Agent: stagefright/1.0 (Linux;Android ");

#if (PROPERTY_VALUE_MAX < 8)
#error "PROPERTY_VALUE_MAX must be at least 8"
#endif

    char value[PROPERTY_VALUE_MAX];
    property_get("ro.build.version.release", value, "Unknown");
    mHeaders.append(value);
    mHeaders.append(")\r\n");

    if (overrides == NULL) {
        return;
    }

    for (size_t i = 0; i < overrides->size(); ++i) {
        String8 line;
        line.append(overrides->keyAt(i));
        line.append(": ");
        line.append(overrides->valueAt(i));
        line.append("\r\n");

        mHeaders.append(line);
    }
}


status_t CmpbPlayer::setDataSource(
        const char *url, const KeyedVector<String8, String8> *headers) 
{
    LOGI("cmpb_setDataSource(%s)", url);    
    
    Mutex::Autolock lock(mMutex);

    // file still open?
    if (STATE_IDLE != mState || url == NULL) 
    {
        return INVALID_OPERATION;
    }

    /*http living streaming */
    //if ( CmpbFileExtValid ( url, "m3u8" ) )
    if (url[0] != '/' && CmpbProtocolPlayerFac::IsCmpbProtocolPlayer( url ) )
    {
        LOGI("\n is cmpb_protocol player\n");
        m_pProtocolPlayer = CmpbProtocolPlayerFac::CreatePlayer( url , ( void* )this );
        m_bProtocolPlayerUsed = true;
        m_pProtocolPlayer->SetDataSource( url );
        mState = STATE_INITED;
        return OK;
    }
    else
    {
        m_bProtocolPlayerUsed = false;
        LOGI("\n not cmpb_protocol player\n");
    }

    if(url[0] == '/') //it's local file
    {
    // open file and set paused state
        mFileLocator.fd = open(url, O_RDONLY);
        if(mFileLocator.fd < 0)
        {
            LOGE("cmpb_setDataSource open file(%s) fail", url); 
            LOGE("ret = %d, errno = %s \n", mFileLocator.fd, strerror(errno));
            mFileLocator.fd = -1;
            return UNKNOWN_ERROR;
        }
        LOGI("cmpb_setDataSource open file(%s) ok, fd = %d", url, mFileLocator.fd); 
        mFileLocator.offset = 0;

        off64_t offset = lseek64(mFileLocator.fd, 0, SEEK_END);
        if (offset < 0)
        {
            LOGE("cmpb_setDataSource seek to file end fail"); 
            close(mFileLocator.fd);
            mFileLocator.fd = -1;
            return UNKNOWN_ERROR;
        }
        
        mFileLocator.length = offset;        
        mFileLocator.path   = NULL;

        LOGI("cmpb_setDataSource file length = %d", (int32_t)mFileLocator.length); 

        lseek64(mFileLocator.fd, 0, SEEK_SET);
    }
    else
    {
        char * puc_mtk_ua = strstr(url, MTK_UA_KEYWORD);        
        if(puc_mtk_ua)
        {
            mHeaders = String8();
            mHeaders.append(puc_mtk_ua+strlen((const char*)MTK_UA_KEYWORD));
            mFileLocator.path = (char*)malloc(puc_mtk_ua - url + 4);
            memset((void*)mFileLocator.path, 0, puc_mtk_ua - url + 4);
            strncpy((char*)mFileLocator.path, (char*)url, puc_mtk_ua - url);
            LOGI("cmpb_setDataSource use mtk user agent \n"); 

            if(headers)
            {
                LOGI("cmpb_setDataSource with cookie: %s \n", headers); 
		initCookie(headers);
            }
            else
            {
                LOGI("cmpb_setDataSource cookie is null \n"); 
            }
        }
        else 
        {
            initCookie(0);
            
            if(headers)
            {
                initHeaders(headers);
            }
            mFileLocator.path = strdup(url);
        }
        //mFileLocator.path = strdup("mmst://211.89.225.104/cnr014");
        //mFileLocator.path = strdup("mms://bcr.media.hinet.net/RA000072");
        if (!strncasecmp("http://", mFileLocator.path, 7))  //http uri mode
        {
            mHttpURI = true;            
        }
        else if(!strncasecmp("mms://", mFileLocator.path, 6))  //mms uri mode
        {
            mMmsURI  = true;
        }
        else if(!strncasecmp("mmst://", mFileLocator.path, 7) ||
                !strncasecmp("mmsh://", mFileLocator.path, 7))
        {
            strcpy((char*)mFileLocator.path + 3, (char*)mFileLocator.path + 4);
            mMmsURI  = true;

            LOGI("cmpb_setDataSource change mmst/mmsh to mms %s\n", mFileLocator.path); 
        }
        
        // open file and set paused state    
        mFileLocator.fd = -1;
        mFileLocator.offset = 0;
        mFileLocator.length = 0;
    }
    
    mState = STATE_INITED;

    LOGI("cmpb_setDataSource OK !!!\n"); 

    return OK;
}

// Warning: The filedescriptor passed into this method will only be valid until
// the method returns, if you want to keep it, dup it!
status_t CmpbPlayer::setDataSource(int fd, int64_t offset, int64_t length)
{
    LOGI("cmpb_setDataSource(%d, %lld, %lld)", fd, offset, length);

    Mutex::Autolock lock(mMutex);

    // file still open?
    if (STATE_IDLE != mState) 
    {
        return INVALID_OPERATION;
    }

    // open file and set paused state
    mFileLocator.path = NULL;
    mFileLocator.fd = dup(fd);
    mFileLocator.offset = offset;
    mFileLocator.length = length;
    
    mState = STATE_INITED;

    return OK;
}

status_t CmpbPlayer::getDstSrcRect(int i4left, int i4top, int i4right, int i4bottom)
{
 //   LOGI("cmpb_getDstSrcRect: %d, %d, %d, %d \n", i4left, i4top, i4right, i4bottom);
    int intersect_left   = 0;
    int intersect_right  = 0;
    int intersect_top    = 0;
    int intersect_bottom = 0;

    int intersect_width   = 0;
    int intersect_hight   = 0;

    int input_rect_width  = 0;
    int input_rect_height = 0;

    int max_surface_width  = 1280;
    int max_surface_height = 720;

#ifdef OSD_RESOLUTION_CMPB_1080P
    LOGI("cmpb_getDstSrcRect: OSD_RESOLUTION is:1920x1080p !!!!!\n");
    max_surface_width  = 1920;
    max_surface_height = 1080;
#endif

    if(i4left < -max_surface_width || i4right > max_surface_width*2 || 
       i4top < -max_surface_height || i4bottom > max_surface_height*2)
    {
        LOGI("cmpb_getDstSrcRect: outof screen !!!!!\n");
        mDstVidRect.u4X = 0;
        mDstVidRect.u4Y = 0;
        mDstVidRect.u4W = 0;
        mDstVidRect.u4H = 0;
        return OK;  //invalid rect
    }

    if(i4left >= i4right || i4top >= i4bottom)
    {
        LOGI("cmpb_getDstSrcRect: INAVALID RECT!!!!!\n");
        return UNKNOWN_ERROR;  //invalid rect
    }

    intersect_left    = (i4left    <= 0)    ? 0    : i4left;
    intersect_top     = (i4top     <= 0)    ? 0    : i4top;
    intersect_right   = (i4right   >  max_surface_width) ? max_surface_width : i4right;
    intersect_bottom  = (i4bottom  >  max_surface_height)  ? max_surface_height  : i4bottom;

    intersect_width   = intersect_right - intersect_left;
    intersect_hight   = intersect_bottom - intersect_top;
    
    input_rect_width  = i4right - i4left;
    input_rect_height = i4bottom - i4top;
    if(input_rect_width > max_surface_width)
    {
        input_rect_width = max_surface_width;
    }
    if(input_rect_height > max_surface_height)
    {
        input_rect_width = max_surface_height;
    }

    mSrcVidRect.u4X = (intersect_left-i4left)*1000/input_rect_width;
    mSrcVidRect.u4Y = (intersect_top-i4top)*1000/input_rect_height;
    mSrcVidRect.u4W = intersect_width*1000/input_rect_width;
    mSrcVidRect.u4H = intersect_hight*1000/input_rect_height;
    
    mDstVidRect.u4X = intersect_left*1000/max_surface_width;
    mDstVidRect.u4Y = intersect_top*1000/max_surface_height;
    mDstVidRect.u4W = intersect_width*1000/max_surface_width;
    mDstVidRect.u4H = intersect_hight*1000/max_surface_height;


    if(mSrcVidRect.u4W > 1000)
    {
        mSrcVidRect.u4W = 1000;
    }
    
    if(mSrcVidRect.u4H > 1000)
    {
        mSrcVidRect.u4H = 1000;
    } 

    LOGI("cmpb_setVideoRect: final src rect(%d, %d, %d, %d) \n", mSrcVidRect.u4X, mSrcVidRect.u4Y, mSrcVidRect.u4W, mSrcVidRect.u4H);
    LOGI("cmpb_setVideoRect: final dst rect(%d, %d, %d, %d) \n", mDstVidRect.u4X, mDstVidRect.u4Y, mDstVidRect.u4W, mDstVidRect.u4H);
    return OK;  //invalid rect
}

status_t CmpbPlayer::setVideoRect(int i4left, int i4top, int i4right, int bottom)
{
    LOGI("cmpb_setVideoRect: %d, %d, %d, %d ,at state %d\n", i4left, i4top, i4right, bottom,mState);

    if(getDstSrcRect(i4left, i4top, i4right, bottom) != OK)
    {
        return OK;
    }
    
    if ( m_bProtocolPlayerUsed )
    {
        m_pProtocolPlayer->SetVideoRect(mDstVidRect);
    }
    else if(mState >= STATE_PREPARING)
    {    

        IMTK_PB_ERROR_CODE_T  e_return = IMTK_PB_ERROR_CODE_OK;
        //IMTK_PB_CTRL_RECT_T   t_SrcRect = {0,0,1000,1000};
        e_return = IMtkPb_Ctrl_SetDisplayRectangle(mPlayHdl, &mSrcVidRect, &mDstVidRect);
        LOGI("Cmpbplayer:Set cmpb rectangle ret %d\n",e_return);
    }
    
#ifdef HW_COMPOSER
        setSurfaceTransparency(true);
#endif 
    //LOGI("cmpb_setVideoRect: final rect(%d, %d, %d, %d) \n", mDstVidRect.u4X, mDstVidRect.u4Y, mDstVidRect.u4W, mDstVidRect.u4H);
    return OK;  //invalid rect
}

status_t CmpbPlayer::prepare() 
{
    IMTK_PB_CTRL_ENGINE_PARAM_T   t_parm;    
    IMTK_PB_ERROR_CODE_T           e_return = IMTK_PB_ERROR_CODE_OK;

    LOGI("cmpb_prepare");
    
    Mutex::Autolock lock(mMutex);

    /*http living streaming */
    if ( m_bProtocolPlayerUsed )
    {
        m_pProtocolPlayer->Prepare();
        mState = STATE_PREPARED;
        sendEvent(MEDIA_PREPARED);
        return NO_ERROR;
    }
    
    do
    {
        if(mFileLocator.path) //use uri mode
        {
            e_return = OpenCmpbRetry(&mPlayHdl,
                                IMTK_PB_CTRL_BUFFERING_MODEL_URI,
                                IMTK_PB_CTRL_LIB_MASTER, 0);
                                    
            if (e_return != IMTK_PB_ERROR_CODE_OK)
            {
                LOGE("OpenCmpbRetry(URI) Failed !\n");
                mState = STATE_ERROR;
                return UNKNOWN_ERROR;
            }

            e_return = IMtkPb_Ctrl_RegCallback(mPlayHdl,
                                (void*)this,
                                MediaPbCallback
                                );
                                
            if (e_return != IMTK_PB_ERROR_CODE_OK)                                
            {
                LOGE("CMPB_player:IMtkPb_Ctrl_RegCallback() Failed !\n");
                break;
            }                        

            t_parm.u4PlayFlag = IMTK_PB_CTRL_PLAY_FLAG_AUDIO | 
                                IMTK_PB_CTRL_PLAY_FLAG_VIDEO;
            t_parm.uBufferModelParam.tUriInfo.pu1URI = (unsigned char*)mFileLocator.path;
            t_parm.uBufferModelParam.tUriInfo.eBufSizeType = IMTK_PB_CTRL_BUF_SIZE_TYPE_BYTE;
            if(!mMmsURI)
            {
                t_parm.uBufferModelParam.tUriInfo.uBufSize.u4Bytes= URI_BUF_SIZE;
            }
            else
            {
                t_parm.uBufferModelParam.tUriInfo.uBufSize.u4Bytes= URI_BUF_SIZE / 2 ;
                LOGI("CMPB_player: it's mms uri,set buffer to %d !\n", t_parm.uBufferModelParam.tUriInfo.uBufSize.u4Bytes);
            }
            t_parm.uBufferModelParam.tUriInfo.u4KeepBufThreshold = 0x700000;
            t_parm.uBufferModelParam.tUriInfo.u4ReBufThreshold = 0xE00000;

            if (mHeaders.length() == 0)
            {
                mHeaders.append(MTK_DF_UA_STRING);
            }
                
            e_return = IMtkPb_Ctrl_SetURL(mPlayHdl, 
                                          IMTK_PB_CTRL_URL_TYPE_AGENT,
                                          (uint8_t*)mHeaders.string());
            LOGI("CMPB_player: IMtkPb_Ctrl_SetURL(%s) UA , ret = %d !\n", mHeaders.string(), e_return);

            e_return = IMtkPb_Ctrl_SetURL(mPlayHdl, 
                                          IMTK_PB_CTRL_URL_TYPE_COOKIE,
                                          (uint8_t*)mCookie.string());
            LOGI("CMPB_player: IMtkPb_Ctrl_SetURL(%s) Cookie , ret = %d !\n", mCookie.string(), e_return);

            e_return = IMtkPb_Ctrl_SetEngineParam(mPlayHdl,
                                                  &t_parm
                                                  );

            if (e_return != IMTK_PB_ERROR_CODE_OK)                                
            {
                LOGE("CMPB_player: IMtkPb_Ctrl_SetEngineParam(URI) Failed !\n");
                break;
            }

            LOGI("CMPB_player: IMtkPb_Ctrl_SetEngineParam(URI) OK !\n");
        }
        else if(mFileLocator.fd != -1) //use pull mode
        {
            uint8_t* pu1_profile;
            if(mForMetaInfo)
            {
                pu1_profile = (uint8_t*)"USB";//"SUB_USB";
                LOGI("CMPB_player: IMtkPb_Ctrl_SetEngineParam(PULL) : use sub_svctx !\n");
            }
            else
            {
                pu1_profile = (uint8_t*)"USB";
                LOGI("CMPB_player: IMtkPb_Ctrl_SetEngineParam(PULL) : use main_svctx !\n");
            }
            e_return = OpenCmpbRetry(&mPlayHdl,
                                        IMTK_PB_CTRL_BUFFERING_MODEL_PULL,
                                        IMTK_PB_CTRL_LIB_MASTER,
                                        pu1_profile);
            if (e_return != IMTK_PB_ERROR_CODE_OK)                                
            {
                LOGE("OpenCmpbRetry(PULL) Failed !\n");
                mState = STATE_ERROR;
                return UNKNOWN_ERROR;
            }
            
            e_return = IMtkPb_Ctrl_RegCallback(mPlayHdl,
                                                (void*)this,
                                                MediaPbCallback
                                                );
            if (e_return != IMTK_PB_ERROR_CODE_OK)                                
            {
                LOGE("CMPB_player:IMtkPb_Ctrl_RegCallback() Failed !\n");
                break;
            }  

            if (m_pExsubttURI != NULL)
            {
                LOGE("CmpbPlayer Parpare:setExSubtitleURI %s!\n",m_pExsubttURI->string());                     
                e_return = IMtkPb_Ctrl_SetURL(mPlayHdl,IMTK_PB_CTRL_URL_TYPE_EXT_SBTL_FULL_PATH,(uint8_t*)(m_pExsubttURI->string()));
                if (e_return != IMTK_PB_ERROR_CODE_OK)                                
                {
                     LOGE("setExSubtitleURI Failed !\n");
                     delete m_pExsubttURI;
                     m_pExsubttURI = NULL;
                     return UNKNOWN_ERROR;
                }                                 
                delete m_pExsubttURI;                     
                m_pExsubttURI = NULL;                                  
            }
            
            t_parm.u4PlayFlag = IMTK_PB_CTRL_PLAY_FLAG_VIDEO | 
                                IMTK_PB_CTRL_PLAY_FLAG_AUDIO;

            t_parm.uBufferModelParam.tPullInfo.pvAppTag = (void*)this;
            t_parm.uBufferModelParam.tPullInfo.pfnOpen = CmpbPlayer::FmPullOpen;
            t_parm.uBufferModelParam.tPullInfo.pfnClose = CmpbPlayer::FmPullClose;
            t_parm.uBufferModelParam.tPullInfo.pfnRead = CmpbPlayer::FmPullRead;
            t_parm.uBufferModelParam.tPullInfo.pfnReadAsync = CmpbPlayer::FmPullReadAsync;
            t_parm.uBufferModelParam.tPullInfo.pfnAbortReadAsync = CmpbPlayer::FmPullAbortReadAsync;
            t_parm.uBufferModelParam.tPullInfo.pfnByteSeek = CmpbPlayer::FmPullByteSeek;
            t_parm.uBufferModelParam.tPullInfo.pfnGetInputLen = CmpbPlayer::FmPullGetInputLen;
            e_return = IMtkPb_Ctrl_SetEngineParam(mPlayHdl,
                                                    &t_parm
                                                    );
        
            if (e_return != IMTK_PB_ERROR_CODE_OK)
            {
                LOGE("IMtkPb_Ctrl_SetEngineParam(PULL) Failed !\n");                
                break;
            }
            LOGI("IMtkPb_Ctrl_SetEngineParam(PULL) OK !\n");
        }
    }while(0);

    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        mState = STATE_ERROR;
        //IMtkPb_Ctrl_Close(mPlayHdl);
        //mPlayHdl = 0;
        sendEvent(MEDIA_ERROR, MEDIA_ERROR_UNKNOWN);
        return UNKNOWN_ERROR;
    }
    else
    {
        mState = STATE_PREPARED;
        sendEvent(MEDIA_PREPARED);        
    }
    
    //IMTK_PB_CTRL_RECT_T    t_SrcRect = {0,0,1000,1000};
    IMtkPb_Ctrl_SetDisplayRectangle(mPlayHdl, &mSrcVidRect, &mDstVidRect);
    
    LOGI("prepare ok \n");
    
    return OK;
}

status_t CmpbPlayer::prepareAsync()
{
    int32_t  i4_ret;

    LOGI("cmpb_prepareAsync");
    
    Mutex::Autolock lock(mMutex);

    if(h_que)
    {
        uint32_t msg = (uint32_t)this;
        int32_t  i4_ret = x_msg_q_send(h_que, &msg, sizeof(msg), 0);
        if(0 != i4_ret)
        {
            LOGE("enter prepareAsync(): x_msg_q_send failed, ret = %d !!!!!\n", i4_ret);
        }
    }
    else
    {
        LOGE("enter prepareAsync(): message queue create error!\n");
    }

    LOGI("cmpb_prepareAsync return ok ");

    return OK;
}

status_t CmpbPlayer::Internal_prepareAsync()
{
    LOGI("Internal_prepareAsync");

    IMTK_PB_CTRL_ENGINE_PARAM_T   t_parm;    
    IMTK_PB_ERROR_CODE_T           e_return = IMTK_PB_ERROR_CODE_OK;
    
    if(mReseted)
    {
        LOGI("cmpbplayer:Internal_prepareAsync: already reset, need not do prepareasync\n ");
        return IMTK_PB_ERROR_CODE_OK;
    }

    /*http living streaming */
    if ( m_bProtocolPlayerUsed )
    {
        LOGI("live stream prepareAsync, mReseted = %d \n", mReseted);
        m_pProtocolPlayer->Prepare();
        mState = STATE_PREPARED;
        if(m_pProtocolPlayer && !mReseted)
        {
            protocol_start();
        }
        else
        {
            mReseted = false;
            LOGI("already destructed, do not start player!!!\n");
        }

        return NO_ERROR;
    }
    
    do
    {
        if(mFileLocator.path) //use uri mode
        {
            e_return = OpenCmpbRetry(&mPlayHdl,
                                IMTK_PB_CTRL_BUFFERING_MODEL_URI,
                                IMTK_PB_CTRL_LIB_MASTER, 0);
                                    
            if (e_return != IMTK_PB_ERROR_CODE_OK)
            {
                LOGE("OpenCmpbRetry(URI) Failed !\n");
                mState = STATE_ERROR;
                return UNKNOWN_ERROR;
            }

            e_return = IMtkPb_Ctrl_RegCallback(mPlayHdl,
                                (void*)this,
                                MediaPbCallback
                                );
                                
            if (e_return != IMTK_PB_ERROR_CODE_OK)                                
            {
                LOGE("CMPB_player:IMtkPb_Ctrl_RegCallback() Failed !\n");
                break;
            }                        

            t_parm.u4PlayFlag = IMTK_PB_CTRL_PLAY_FLAG_AUDIO | 
                                IMTK_PB_CTRL_PLAY_FLAG_VIDEO;
            t_parm.uBufferModelParam.tUriInfo.pu1URI = (unsigned char*)mFileLocator.path;
            t_parm.uBufferModelParam.tUriInfo.eBufSizeType = IMTK_PB_CTRL_BUF_SIZE_TYPE_BYTE;
            if(!mMmsURI)
            {
                t_parm.uBufferModelParam.tUriInfo.uBufSize.u4Bytes= URI_BUF_SIZE;
            }
            else
            {
                t_parm.uBufferModelParam.tUriInfo.uBufSize.u4Bytes= URI_BUF_SIZE / 2;
                LOGI("CMPB_player: it's mms uri,set buffer to %d !\n", t_parm.uBufferModelParam.tUriInfo.uBufSize.u4Bytes);
            }
            t_parm.uBufferModelParam.tUriInfo.u4KeepBufThreshold = 0x700000;
            t_parm.uBufferModelParam.tUriInfo.u4ReBufThreshold = 0xE00000;
                
            if (mHeaders.length() == 0)
            {
                mHeaders.append(MTK_DF_UA_STRING);
            }

            e_return = IMtkPb_Ctrl_SetURL(mPlayHdl, 
                                          IMTK_PB_CTRL_URL_TYPE_AGENT,
                                          (uint8_t*)mHeaders.string());
            LOGI("CMPB_player: IMtkPb_Ctrl_SetURL(%s) , ret = %d !\n", mHeaders.string(), e_return);

            e_return = IMtkPb_Ctrl_SetURL(mPlayHdl, 
                                          IMTK_PB_CTRL_URL_TYPE_COOKIE,
                                          (uint8_t*)mCookie.string());
            LOGI("CMPB_player: IMtkPb_Ctrl_SetURL(%s) Cookie , ret = %d !\n", mCookie.string(), e_return);
               
            e_return = IMtkPb_Ctrl_SetEngineParamAsync(mPlayHdl,
                                                  &t_parm
                                                  );

            if (e_return != IMTK_PB_ERROR_CODE_OK)                                
            {
                LOGE("CMPB_player: IMtkPb_Ctrl_SetEngineParamAsync(URI) Failed !\n");
                break;
            }

            LOGI("CMPB_player: IMtkPb_Ctrl_SetEngineParamAsync(URI) OK !\n");
        }
        else if(mFileLocator.fd != -1) //use pull mode
        {
            uint8_t* pu1_profile;
            if(mForMetaInfo)
            {
                pu1_profile = (uint8_t*)"USB"; //"SUB_USB";
                LOGI("CMPB_player: IMtkPb_Ctrl_SetEngineParam(PULL) : use sub_svctx !\n");
            }
            else
            {
                pu1_profile = (uint8_t*)"USB";
                LOGI("CMPB_player: IMtkPb_Ctrl_SetEngineParam(PULL) : use main_svctx !\n");
            }
            e_return = OpenCmpbRetry(&mPlayHdl,
                                        IMTK_PB_CTRL_BUFFERING_MODEL_PULL,
                                        IMTK_PB_CTRL_LIB_MASTER,
                                        pu1_profile);
            if (e_return != IMTK_PB_ERROR_CODE_OK)                                
            {
                LOGE("OpenCmpbRetry(PULL) Failed !\n");
                mState = STATE_ERROR;
                return UNKNOWN_ERROR;
            }
            
            e_return = IMtkPb_Ctrl_RegCallback(mPlayHdl,
                                                (void*)this,
                                                MediaPbCallback
                                                );
            if (e_return != IMTK_PB_ERROR_CODE_OK)                                
            {
                LOGE("CMPB_player:IMtkPb_Ctrl_RegCallback() Failed !\n");
                break;
            }  

            if (m_pExsubttURI != NULL)
            {
                LOGE("CmpbPlayer Parpare:setExSubtitleURI %s!\n",m_pExsubttURI->string());                     
                e_return = IMtkPb_Ctrl_SetURL(mPlayHdl,IMTK_PB_CTRL_URL_TYPE_EXT_SBTL_FULL_PATH,(uint8_t*)(m_pExsubttURI->string()));
                if (e_return != IMTK_PB_ERROR_CODE_OK)                                
                {
                     LOGE("setExSubtitleURI Failed !\n");
                     delete m_pExsubttURI;
                     m_pExsubttURI = NULL;
                     return UNKNOWN_ERROR;
                }                                 
                delete m_pExsubttURI;                     
                m_pExsubttURI = NULL;                                  
            }
            t_parm.u4PlayFlag = IMTK_PB_CTRL_PLAY_FLAG_VIDEO | 
                                IMTK_PB_CTRL_PLAY_FLAG_AUDIO;

            t_parm.uBufferModelParam.tPullInfo.pvAppTag = (void*)this;
            t_parm.uBufferModelParam.tPullInfo.pfnOpen = CmpbPlayer::FmPullOpen;
            t_parm.uBufferModelParam.tPullInfo.pfnClose = CmpbPlayer::FmPullClose;
            t_parm.uBufferModelParam.tPullInfo.pfnRead = CmpbPlayer::FmPullRead;
            t_parm.uBufferModelParam.tPullInfo.pfnReadAsync = CmpbPlayer::FmPullReadAsync;
            t_parm.uBufferModelParam.tPullInfo.pfnAbortReadAsync = CmpbPlayer::FmPullAbortReadAsync;
            t_parm.uBufferModelParam.tPullInfo.pfnByteSeek = CmpbPlayer::FmPullByteSeek;
            t_parm.uBufferModelParam.tPullInfo.pfnGetInputLen = CmpbPlayer::FmPullGetInputLen;
            e_return = IMtkPb_Ctrl_SetEngineParamAsync(mPlayHdl,
                                                    &t_parm
                                                    );
        
            if (e_return != IMTK_PB_ERROR_CODE_OK)
            {
                LOGE("IMtkPb_Ctrl_SetEngineParamAsync(PULL) Failed !\n");                
                break;
            }
            LOGI("IMtkPb_Ctrl_SetEngineParamAsync(PULL) OK !\n");
        }
    }while(0);

    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        mState = STATE_ERROR;
        //IMtkPb_Ctrl_Close(mPlayHdl);
        //mPlayHdl = 0;
        sendEvent(MEDIA_ERROR, MEDIA_ERROR_UNKNOWN);
        return UNKNOWN_ERROR;
    }
    else
    {
        #if 0
        mState = STATE_PREPARED;
        sendEvent(MEDIA_PREPARED);
        #else
        mState = STATE_PREPARING;
        #endif
    }

    //IMTK_PB_CTRL_RECT_T    t_SrcRect = {0,0,1000,1000};
    IMtkPb_Ctrl_SetDisplayRectangle(mPlayHdl, &mSrcVidRect, &mDstVidRect);

    LOGI("prepare async OK !\n");
    
    return NO_ERROR;
}

status_t CmpbPlayer::StartPlayer()
{
    IMTK_PB_ERROR_CODE_T e_return;
    
    LOGI("StartPlayer");

    if(mState == STATE_STARTED)
    {
        return OK;
    }
		    
    mStart = true;
    e_return = IMtkPb_Ctrl_Play(mPlayHdl, mStartTime);
    mSeekDoneNeedPlay = false;
    if(mStartTime != 0)
    {
        LOGI("cmpb_start() start from time: %d seconds\n", (int32_t)mStartTime);
        mStartTime = 0;
        sendEvent(MEDIA_SEEK_COMPLETE); //seek before play started, will send seek done here.
    }
        
    if (e_return != IMTK_PB_ERROR_CODE_OK)                                
    {
        LOGE("IMtkPb_Ctrl_Play() Failed !\n");
        mState = STATE_ERROR;
        return UNKNOWN_ERROR;
    }

    LOGI("IMtkPb_Ctrl_Play() ok !\n");
    
    if((mState != STATE_PAUSED)/* && mHttpURI*/)
    {
        mState = STATE_STARTING;
    }
    else
    {
        LOGI("resume play now!");
        mState = STATE_STARTED;
    }

    if(mMmsURI)
    {
        LOGI("resume play for mms!");
        mState = STATE_STARTED;
        return OK;
    }
    
    int wait_cnt = 100;
    if(!mHttpURI && !mMmsURI)
    {
        wait_cnt = 8;
    }
    LOGE("CmpbPlayer::start() wait for started timeout = %d... \n", wait_cnt);
    //if(mHttpURI || mMmsURI)  //both local and network use sync play(android only support sync play)
    {
        int i = 0;
        
        while(mState == STATE_STARTING && i < wait_cnt) //wait for stated, look as sync call, timeout is 100s
        {
            LOGI("CmpbPlayer::start() wait for started state = %d... \n", mState);
            i ++;
            sleep(1);
        }

        if(mState != STATE_STARTED &&
           mState != STATE_STOPPED && //stopped by ap
           mState != STATE_COMPLETE)  
        {
            LOGE("CmpbPlayer::start() error! state = %d... \n", mState);
            mState = STATE_ERROR;
            sendEvent(MEDIA_ERROR, MTK_MEDIA_INFO_FILE_CORRUPT);
        }
    }
    
    return OK;
}

status_t CmpbPlayer::protocol_start()
{
    IMTK_PB_ERROR_CODE_T e_return;
    
    LOGE("protocol_start");

    Mutex::Autolock lock(mMutex);

    /*http living streaming */
    if ( m_bProtocolPlayerUsed )
    {
        mStart = true;        
        m_pProtocolPlayer->Start();
        m_pProtocolPlayer->SetVideoRect(mDstVidRect);
    }
    
    return OK;
}

status_t CmpbPlayer::start()
{
    IMTK_PB_ERROR_CODE_T e_return;
    
    LOGI("cmpb_start");

    Mutex::Autolock lock(mMutex);

    if(mState == STATE_STARTED)
    {
        return OK;
    }

    /*http living streaming */
    if ( m_bProtocolPlayerUsed )
    {
        m_pProtocolPlayer->Start();
        mState = STATE_STARTED;
        return OK;
    }
#if 0
    while(mSeeking)
    {
        LOGI("CmpbPlayer::start() wait for seek complete ... \n");
        sleep(1);
    }
 #else
    if (mSeeking)
    {
        LOGE("CmpbPlayer::start() seeking..., just play after seek done ... \n");
        mSeekDoneNeedPlay = true;
        return OK;
    }
#endif
        
    return StartPlayer();
}

status_t CmpbPlayer::stop()
{
    LOGI("cmpb_stop");

    Mutex::Autolock lock(mMutex);

    if(mPlayHdl == 0)
    {
        LOGE("CMPBplayer,no playing is running, mPlayHdl == NULL \n");
        return OK;
    }

    if(mState != STATE_STARTING && mState != STATE_STARTED && 
       mState != STATE_PAUSED  && mState != STATE_COMPLETE)
    {
        LOGI("CMPBplayer,need not do stopping at this state: %d\n", mState);
        return OK;
    }

    mSeeking = false;
    mSeekDoneNeedPlay = false;
        
    /*http living streaming */
    if ( m_bProtocolPlayerUsed )
    {
        if ( m_pProtocolPlayer )
        {
            m_pProtocolPlayer->Stop();
        }
        mState = STATE_STOPPED;
        return OK;
    }
    
    return StopPlayer(true);    
}

status_t CmpbPlayer::pause()
{
    IMTK_PB_ERROR_CODE_T e_return;
    int32_t              wait_cnt = 2;  //wait for seek done 15s

    LOGI("cmpb_pause");

    Mutex::Autolock lock(mMutex);

    /*http living streaming */
    if ( m_bProtocolPlayerUsed )
    {
        m_pProtocolPlayer->Pause();
        mState = STATE_PAUSED;
        return OK;
    }
    
    while(mSeeking && (wait_cnt > 0))
    {
        LOGI("CmpbPlayer::pause() wait for seek complete ... \n");
        wait_cnt --;
        sleep(1);
    }

	if(wait_cnt <= 0)
    {
        LOGI("CmpbPlayer::pause() before seek done, only save this op ... \n");
        mSeekDoneOp = MTK_MEDIA_SEEK_DONE_OP_PAUSE;
        mState = STATE_PAUSED;
        return OK;
    }
    
    e_return = IMtkPb_Ctrl_Pause(mPlayHdl);

    if (e_return != IMTK_PB_ERROR_CODE_OK)                                
    {
        LOGE("IMtkPb_Ctrl_Pause() Failed !\n");
        return UNKNOWN_ERROR;
    }

    mState = STATE_PAUSED;
    
    return OK;
}

bool CmpbPlayer::isPlaying()
{
//    LOGI("cmpb_isPlaying = %d, mSeekDoneNeedPlay = %d \n", 
//         STATE_STARTED == mState, mSeekDoneNeedPlay);
    return (STATE_STARTED == mState || mSeekDoneNeedPlay);
}

status_t CmpbPlayer::setAudioTrack(int track_num)    
{
    IMTK_PB_ERROR_CODE_T  e_return;
    LOGE("cmpb set audio track: %d \n", track_num);
    
    Mutex::Autolock lock(mMutex);
       
    e_return = IMtkPb_Ctrl_SetAudTrack(mPlayHdl, track_num);
    if (e_return != IMTK_PB_ERROR_CODE_OK)                                
    {
        LOGE("IMtkPb_Ctrl_SetAudTrack Failed !\n");
        return UNKNOWN_ERROR;
    }        
    return  OK;
}

status_t CmpbPlayer::setSubtitleTrack(int track_num)    
{
    IMTK_PB_ERROR_CODE_T  e_return;
    LOGE("cmpb set subtitle track: %d \n", track_num);
    
    Mutex::Autolock lock(mMutex);
       
    e_return = IMtkPb_Ctrl_SetSubtitleTrack(mPlayHdl, track_num);
    if (e_return != IMTK_PB_ERROR_CODE_OK)                                
    {
        LOGE("IMtkPb_Ctrl_SetSubtitleTrack Failed !\n");
        return UNKNOWN_ERROR;
    }        
    return  OK;
}

status_t CmpbPlayer::setPlaySeamless(bool bIsSeamless)
{
    IMTK_PB_ERROR_CODE_T  e_return;
    bool bSetInfo = bIsSeamless;
    uint32_t  u4_size = 4;
    
    LOGE("zhanghongkan setPlaySeamless bIsSeamless %d\n", (uint32_t)bIsSeamless);
    Mutex::Autolock lock(mMutex);

	 e_return = IMtkPb_Ctrl_Set(mPlayHdl, IMTK_PB_CTRL_SET_TYPE_SEAMLESS, (void*)&bSetInfo, &u4_size);
#if 0    
    if (bIsSeamless)
    {
        bSetInfo = true;
        e_return = IMtkPb_Ctrl_Set(mPlayHdl, IMTK_PB_CTRL_SET_TYPE_SEAMLESS, (void*)&bSetInfo, &u4_size);
    }
    else
    {
        bSetInfo = false;
        e_return = IMtkPb_Ctrl_Set(mPlayHdl, IMTK_PB_CTRL_SET_TYPE_SEAMLESS, (void*)&bSetInfo, &u4_size);
    }
#endif
    if (e_return != IMTK_PB_ERROR_CODE_OK)                                
    {
        LOGE("setPlaySeamless Failed !\n");
        return UNKNOWN_ERROR;
    }
    return OK;
}

status_t CmpbPlayer::setExSubtitleURI(const char *url)
{
    IMTK_PB_ERROR_CODE_T  e_return;    
    
    LOGE("cmpb setExSubtitleURI: %s \n", url);    
    if(url==NULL)
    {
        return UNKNOWN_ERROR;  
    }
    Mutex::Autolock lock(mMutex);
    m_pExsubttURI = new String8(url);
    /*e_return = IMtkPb_Ctrl_SetURL(mPlayHdl,IMTK_PB_CTRL_URL_TYPE_EXT_SBTL_FULL_PATH,(uint8_t*)url);
    if (e_return != IMTK_PB_ERROR_CODE_OK)                                
    {
        LOGE("setExSubtitleURI Failed !\n");
        return UNKNOWN_ERROR;
    } */       
    return  OK;                   
}

status_t CmpbPlayer::seekTo(int msec)
{
    IMTK_PB_ERROR_CODE_T e_return;
    
    LOGI("cmpb_seekTo time: %d ms \n", msec);

    int duration = 0;
    getDuration(&duration);
    LOGI("cmpb_seekTo duration: %d ms \n", duration);
    if(duration != 0 && msec >= duration) //seek to the end of file
    {
        sendEvent( MEDIA_SEEK_COMPLETE );
        //play finished
        //sendEvent( MEDIA_PLAYBACK_COMPLETE );

        return OK;
    }

    Mutex::Autolock lock(mMutex);
    /*http living streaming */
    if ( m_bProtocolPlayerUsed )
    {
    	if(mState != STATE_PAUSED && mState != STATE_STARTED)
        {
            mStartTime = msec/1000; //convert to STC
            if (mStartTime == 0)
            {
                LOGI("cmpb_seekTo not in play state, skip this seek(protocol)!!! \n");
                sendEvent( MEDIA_SEEK_COMPLETE );
                return OK;
            }
        }
        
        mSeeking = true;
        m_pProtocolPlayer->SeekTo( msec );
        mSeeking = false;
        //LOGI( "CmpbPlayer::m_pProtocolPlayer received IMTK_PB_CTRL_EVENT_TIMESEEK_DONE \n" );
        //sendEvent( MEDIA_SEEK_COMPLETE );
        return OK;
    }
    #if 1
    ////check if seekable or not, valid after prepare/prepareasync /////
    bool    bSeekable;
    e_return = IMtkPb_Ctrl_Get_File_Seekable(mPlayHdl, &bSeekable);
    if (e_return == IMTK_PB_ERROR_CODE_OK && !bSeekable)  //can't do seek operations for this file
    {
        LOGE("cmpb:: can't do seek for no index table file!!!\n");
        sendEvent( MEDIA_SEEK_COMPLETE ); 
        sendEvent(MEDIA_INFO, MTK_MEDIA_INFO_FILE_NO_SEEKABLE);  
        return OK;
    }
    #endif
    if(mState != STATE_PAUSED && mState != STATE_STARTED)
    {
        LOGI("cmpb_seekTo not in play state, will do seek later!!! \n");
        mStartTime = msec/1000; //convert to STC
        if (mStartTime == 0)
        {
            sendEvent( MEDIA_SEEK_COMPLETE );
        }
        return OK;
    }

    mSeeking = true;
    e_return = IMtkPb_Ctrl_TimeSeek(mPlayHdl, msec / 1000);
    if (e_return != IMTK_PB_ERROR_CODE_OK)                                
    {
        mSeeking = false;
        LOGE("IMtkPb_Ctrl_TimeSeek() Failed !\n");

        sendEvent( MEDIA_SEEK_COMPLETE ); /*do not return error here, or android AP will exit*/
        //return INVALID_OPERATION;
    }
    
    return OK;
}

status_t CmpbPlayer::getCurrentPosition(int *msec)
{
    IMTK_PB_ERROR_CODE_T e_return;
    uint32_t             u4Time;
    uint64_t             u8CurPos;
    
    //LOGI("cmpb_getCurrentPosition");

    //Mutex::Autolock lock(mMutex);
    /*http living streaming */
    if ( m_bProtocolPlayerUsed )
    {
        m_pProtocolPlayer->GetCurrentPosition( msec );
        return OK;
    }
    
    e_return = IMtkPb_Ctrl_GetCurrentPos(mPlayHdl, &u4Time, &u8CurPos);
    LOGI("cmpb_getCurrentPosition() time = %d, pos = %d \n",u4Time,  u8CurPos);
    if (e_return != IMTK_PB_ERROR_CODE_OK)                                
    {
        *msec = 0;
        LOGE("IMtkPb_Ctrl_GetCurrentPos() Failed !\n");
        return OK;
    }

    *msec = (int)u4Time;
#if 0
    if(mHttpURI || mMmsURI)
    {
        IMTK_PB_CTRL_GET_MEDIA_INFO_T tMediaInfo;
        uint32_t u4Percent;
        uint32_t u4TmPercent;
        
        e_return = IMtkPb_Ctrl_GetMediaInfo(mPlayHdl, &tMediaInfo);
        if (e_return != IMTK_PB_ERROR_CODE_OK)
        {
            tMediaInfo.u4TotalDuration = 0;
        }

        IMtkPb_Ctrl_GetBufferFullness(mPlayHdl, &u4Percent);        
        if(tMediaInfo.u4TotalDuration != 0 && tMediaInfo.u8Size != 0)
        {
            u4Percent = (URI_BUF_SIZE * u4Percent / 100 + tMediaInfo.u8Size * u4Time / tMediaInfo.u4TotalDuration) \
                              * 100 / tMediaInfo.u8Size;

            u4TmPercent = u4Time * 100 / tMediaInfo.u4TotalDuration;

            if(u4Percent <= u4TmPercent)
            {
                u4Percent = u4TmPercent + 2;
            }

            if(u4Percent > 100)
            {
                u4Percent = 100;
            }

            if(u4Percent > mCurBufPercent && u4Percent <= 100) //only send 99% here, 100% will send when receive EOS.
            {
                mCurBufPercent = u4Percent;
                sendEvent(MEDIA_BUFFERING_UPDATE, mCurBufPercent);
                LOGI("cmpb_getCurrentPosition(): send percentage(%d)%% !\n", mCurBufPercent);
            }
        }
    }    
#endif
    return OK;
}

status_t CmpbPlayer::getAudioTrackNs(int *trk_nums)
{
    
   IMTK_PB_ERROR_CODE_T            e_return;
   IMTK_PB_CTRL_MEDIA_INFO_T       t_media_info;
   e_return = IMtkPb_Ctrl_Get(mPlayHdl,
                   IMTK_PB_CTRL_GET_TYPE_MEDIA_INFO,
                   (void*)&t_media_info,
                   sizeof(IMTK_PB_CTRL_MEDIA_INFO_T));            
   *trk_nums = t_media_info.u2AudioTrackNum;       
   LOGE("cmpb getAudioTrackNs()(%d)%% !\n", t_media_info.u2AudioTrackNum);
    if(e_return == IMTK_PB_ERROR_CODE_OK)
    {
        return OK;
    }
    else
    {
        return UNKNOWN_ERROR;
    }
   
}

status_t CmpbPlayer::getSubtitleTrackNs(int *trk_nums)
{
    
   IMTK_PB_ERROR_CODE_T            e_return;
   IMTK_PB_CTRL_MEDIA_INFO_T       t_media_info;   
   e_return = IMtkPb_Ctrl_Get(mPlayHdl,
                   IMTK_PB_CTRL_GET_TYPE_MEDIA_INFO,
                   (void*)&t_media_info,
                   sizeof(IMTK_PB_CTRL_MEDIA_INFO_T));            
   *trk_nums = t_media_info.u2SubtlTrackNum;       
   LOGE("cmpb getSubtitleTrackNs()(%d)%% !\n", t_media_info.u2SubtlTrackNum);
   
    if(e_return == IMTK_PB_ERROR_CODE_OK)
    {
        return OK;
    }
    else
    {
        return UNKNOWN_ERROR;
    }   
}
    
status_t CmpbPlayer::getDuration(int *msec)
{
    IMTK_PB_ERROR_CODE_T e_return;
    IMTK_PB_CTRL_GET_MEDIA_INFO_T tMediaInfo;
    
    LOGI("cmpb_getDuration");

    //Mutex::Autolock lock(mMutex);
    /*http living streaming */
    if ( m_bProtocolPlayerUsed )
    {
        m_pProtocolPlayer->GetDuration( msec );
        return OK;
    }

    e_return = IMtkPb_Ctrl_GetMediaInfo(mPlayHdl, &tMediaInfo);
    if (e_return != IMTK_PB_ERROR_CODE_OK)                                
    {
        *msec = 0;
        LOGE("IMtkPb_Ctrl_GetMediaInfo() Failed !\n");
        return OK;
    }

    *msec = (int)tMediaInfo.u4TotalDuration;
    if(mHttpURI || mMmsURI)
    {
        if(*msec != 0)
        {
            //*msec += 1000;
        }
        else
        {
            *msec = 0xD65CB580;  //use 999 hours instead of zero duration
        }
    }

    LOGI("cmpb_getDuration() : = %d \n", tMediaInfo.u4TotalDuration);

    return OK;   
}

status_t CmpbPlayer::reset() 
{
    status_t e_ret;
    
    LOGI("CmpbPlayer::reset");
    Mutex::Autolock lock(mMutex);
    e_ret = ResetNosync();

    mState = STATE_IDLE;

    return e_ret;
}

status_t CmpbPlayer::setLooping(int loop) {
    LOGI("cmpb_setLooping");

    mLoop = loop;
    
    return OK;
}

player_type CmpbPlayer::playerType() {
    LOGI("cmpb_playerType");
    return CMPB_PLAYER;      
}

status_t CmpbPlayer::suspend() {
    LOGI("cmpb_suspend");
    return INVALID_OPERATION;      
}

status_t CmpbPlayer::resume() {
    LOGI("cmpb_resume");   
    return INVALID_OPERATION;   
}

status_t CmpbPlayer::invoke(const Parcel &request, Parcel *reply) {
     LOGI("cmpb_invoke");   
     return OK;   
}

void CmpbPlayer::setAudioSink(const sp<AudioSink> &audioSink) {   
}

status_t CmpbPlayer::setVolume(float leftVolume, float rightVolume)
{
#if 0
    IMTK_PB_ERROR_CODE_T e_return;
    IMTK_PB_CTRL_AUD_VOLUME_INFO_T t_volume_info;    

    LOGI("cmpb_setVolume, leftVolume = %.2f, rightVolume = %.2f", leftVolume, rightVolume);
    memset(&t_volume_info, 0, sizeof(IMTK_PB_CTRL_AUD_VOLUME_INFO_T));
    t_volume_info.e_out_port = IMTK_PB_CTRL_AUD_OUT_PORT_SPEAKER;
    t_volume_info.e_ch       = IMTK_PB_CTRL_AUD_CHANNEL_ALL;
    t_volume_info.ui1_volumn = 50;
  
    e_return = IMtkPb_Ctrl_SetAudioVolume(t_volume_info);
    if(e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGE("Set Audio volume failed(%d)!\n",e_return);
        return e_return;
    }
#endif
    return OK;
}

status_t CmpbPlayer::getMetadata(
        const media::Metadata::Filter& ids, Parcel *records) 
{
    
    LOGI("cmpb_getMetadata");

    using media::Metadata;

    uint32_t flags = MediaExtractor::CAN_PAUSE | MediaExtractor::CAN_SEEK_BACKWARD | MediaExtractor::CAN_SEEK_FORWARD;

    Metadata metadata(records);

    metadata.appendBool(
            Metadata::kPauseAvailable,
            flags & MediaExtractor::CAN_PAUSE);

    metadata.appendBool(
            Metadata::kSeekBackwardAvailable,
            flags & MediaExtractor::CAN_SEEK_BACKWARD);

    metadata.appendBool(
            Metadata::kSeekForwardAvailable,
            flags & MediaExtractor::CAN_SEEK_FORWARD);

    return OK;
}

status_t CmpbPlayer::getCmpbHandle(IMTK_PB_HANDLE_T*   PlayHdl) 
{
    LOGI("getCmpbHandle PlayHdl = %x", PlayHdl);
    Mutex::Autolock l(mMutex);
    if(PlayHdl)
    {
        *PlayHdl = mPlayHdl;
    }
    return OK;
}

void CmpbPlayer::setCmpbPlayerState(int32_t state)
{
    LOGI("setCmpbPlayerState state = %d", state);
    if(STATE_PREPARED == state) //for playlist & rtsp
    {
        LOGI("setCmpbPlayerState pause video after preprared");
        if(m_pProtocolPlayer)
        {
            m_pProtocolPlayer->Pause();
            mState = STATE_PAUSED;
        }        
        
       // mState = state;
    }
}

#ifdef HW_COMPOSER
status_t CmpbPlayer::setVideoSurfaceTexture(const sp<ISurfaceTexture> &surfaceTexture)
{
        LOGI("setVideoSurfaceTexture");
        if(surfaceTexture == 0)
        {
                mNativeWindow = NULL;
        }
        else
        {
                mNativeWindow = new SurfaceTextureClient(surfaceTexture);
        }
        #ifdef HW_COMPOSER
	        setSurfaceTransparency(true);
				#endif        
        return OK;
}

void CmpbPlayer::setSurfaceTransparency(bool transparent)
{
        LOGI("setSurfaceTransparency %d", transparent);
        if(mNativeWindow == NULL)
                return;
                
        int pixel_format = transparent? HAL_PIXEL_FORMAT_GTV_CMPB_VIDEO_HOLE: HAL_PIXEL_FORMAT_GTV_OPAQUE_BLACK;
                    
    int res = native_window_set_buffers_format(mNativeWindow.get(), pixel_format);
          
   	if(res)
    {
            LOGE("[CmpbPlayer] %s: native_window_set_buffers_format failed (%d)", __FUNCTION__, res);
            return;
    }
    
	  res = native_window_set_scaling_mode(mNativeWindow.get(), NATIVE_WINDOW_SCALING_MODE_SCALE_TO_WINDOW);
   	if(res)
    {
            LOGE("[CmpbPlayer] %s: native_window_set_scaling_mode failed (%d)", __FUNCTION__, res);
            return;
    }
    android_native_buffer_t* buf = NULL;
    
    res = mNativeWindow->dequeueBuffer(mNativeWindow.get(), &buf);
    if(res)
    {
            LOGE("[CmpbPlayer] %s: dequeueBuffer failed (%d)", __FUNCTION__, res);
            return;
    }

    res = mNativeWindow->queueBuffer(mNativeWindow.get(), buf);
    if(res)
    {
            LOGE("[CmpbPlayer] %s: queueBuffer failed (%d)", __FUNCTION__, res);
            return;
    }
}
#endif
}  // namespace android

