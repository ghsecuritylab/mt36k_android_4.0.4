#include "VospPlayer.h"
#include <utils/Log.h>

#include <stdlib.h>
#include <unistd.h>
#include <dlfcn.h>
#include <sys/time.h>
#include <time.h>
#include <string.h>
#include <pthread.h>

#include "mtvosp_ipc_msgq.h"

using namespace vosp;

std::string VospPlayer::vospUrl;
VospContext* VospPlayer::s;
static void *vlibhandle = NULL; 
bool VospPlayer::firstCallPullOpen=true;

//PullTask* VospPlayer::pull_task[8];
//int VospPlayer::task_num;
static pthread_t ptd_id;
static int i4_thread_runing;
static int i4_msgq_handle = -1;
static uint32_t _u4_req_id = 1;

static const char* VOSP_LIBRARY_NAME = "/data/data/com.tcl.hdvod/lib/libvooletv.so";
static const char* VOSP_MGMT_INIT = "c2ms_mgmt_init";
static const char* VOSP_OPEN = "c2ms_sess_open";
static const char* VOSP_ATTR = "c2ms_sess_attr";
static const char* VOSP_IDXFILE = "c2ms_sess_idxfile";
static const char* VOSP_CACHE_INIT = "c2ms_sess_cache_init";
static const char* VOSP_IDX_OPEN = "c2ms_sess_idx_open";
static const char* VOSP_IDX_OPEN_ASYN = "c2ms_sess_idx_open_asyn";
static const char* VOSP_BUFFERING = "c2ms_sess_buffering";
static const char* VOSP_SET_MEDIA = "c2ms_sess_set_media";
static const char* VOSP_SEEK = "c2ms_sess_seek";
static const char* VOSP_READ = "c2ms_sess_read";
static const char* VOSP_BUFRATION= "c2ms_sess_bufratio";
static const char* VOSP_IDX_CLOSE = "c2ms_sess_idx_close";
static const char* VOSP_CLOSE = "c2ms_sess_close";
static const char* VOSP_MGMT_CLEAN = "c2ms_mgmt_clean";

void * (*c2ms_mgmt_init)(uint8 *, uint64, uint8 *, int);
void * (*c2ms_sess_open)(void *, uint8 *, int *);
int (*c2ms_sess_attr)(void *, uint8 *, uint8 *, uint64 *, uint8 *);
int (*c2ms_sess_idxfile)(void *, uint8 *, uint32 *);  
int (*c2ms_sess_cache_init)(void *, int);
int (*c2ms_sess_idx_open)(void *, uint32 *);
int (*c2ms_sess_idx_open_asyn)(void *, uint32 *);
int (*c2ms_sess_buffering)(void *, int);
int (*c2ms_sess_set_media)(void *, int, uint64, uint64);
int (*c2ms_sess_seek)(void *, uint64);
int (*c2ms_sess_read)(void *, void *, int, int);
int (*c2ms_sess_bufratio)(void *, int *);
int (*c2ms_sess_idx_close)(void *);
int (*c2ms_sess_close)(void *);
int (*c2ms_mgmt_clean)(void *);

#define NOT_USE_CACHE 0
#define USE_IDX_OPEN 1
#define VOSP_MGMT_BUFSIZE  32*1024*1024
#define VOSP_CACHE_BUFSIZE 16*1024


typedef struct
{
    IMTK_PULL_HANDLE_T		  hPullSrc;
    void*					  pvAppTag;
    uint8_t* 				  pu1Dst;
    uint32_t 				  u4DataLen;
    IMtkPb_Ctrl_Pull_Nfy_Fct   pfnNotify;
    void*					  pvRdAsyTag;
    uint32_t				  u4ReqId;
} ASYNC_READ_BLOCK_T;

int usec_sleep(unsigned long usec)
{
    struct timeval time_out;
    time_out.tv_sec = usec / 1000000;
    time_out.tv_usec = usec % 1000000;
    while (select(0, NULL, NULL, NULL, &time_out) == -1)
    {
        return -1;
    }
    return 0;
}
static long  _get_current_time_ms(void)
{
    static long _tv_sec = 0;
    static long _tv_usec = 0;
    struct timeval tv;
    struct timezone tz;

    //unsigned long  u4_tm = (unsigned long) times(NULL);
    gettimeofday(&tv, &tz);
    if (_tv_sec == 0)
    {
        _tv_sec = tv.tv_sec;
        _tv_usec = tv.tv_usec;
        return 0;
    }
    else
    {
        return ( (tv.tv_sec - _tv_sec) * 1000) + ((tv.tv_usec - _tv_usec) / 1000);
    }
}


static void* _thread_async_process(void* arg)
{
    ASYNC_READ_BLOCK_T t_read_block;
    int ret = -1;
    int msg_size = sizeof(ASYNC_READ_BLOCK_T);

    while (i4_thread_runing)
    {
        memset(&t_read_block, 0, sizeof(t_read_block));
        ret = mtvosp_msgq_recv(i4_msgq_handle, (void*)&t_read_block, &msg_size, (unsigned int)(-1));
        if (ret)
        {
            LOGE("mtvosp_msgq_recv() error\n");
            continue;
        }

        if (msg_size != sizeof(ASYNC_READ_BLOCK_T))
        {
            LOGE("mtvosp_msgq_recv a quit msg\n");
            continue;
        }

        ret = c2ms_sess_read((void*)t_read_block.hPullSrc, t_read_block.pu1Dst, t_read_block.u4DataLen, 0);

        if (ret > 0 && t_read_block.pfnNotify)
        {
            t_read_block.pfnNotify(IMTK_PB_CTRL_PULL_READ_OK, t_read_block.pvRdAsyTag, t_read_block.u4ReqId, ret);
            LOGD("thread_read_async(id=%d) at %ld  ret=%d\n", t_read_block.u4ReqId, _get_current_time_ms(), ret);
        }
        else if (ret < 0 && t_read_block.pfnNotify)
        {
            t_read_block.pfnNotify(IMTK_PB_CTRL_PULL_FAIL, t_read_block.pvRdAsyTag, t_read_block.u4ReqId, ret);
            LOGE("thread_read_async error  ret=%d\n", ret);
        }
    }

    LOGD("exit thread\n");
    return (void*)0;
}


VospPlayer::VospPlayer()
{
    s = (VospContext*)malloc(sizeof(VospContext));
    if (!s)
    {
        LOGD("xhg++++malloc VospContex failed++++");
    }
    memset(s, 0, sizeof(VospContext));

    if (mtvosp_msgq_create(&i4_msgq_handle))
    {
        LOGE("mtvosp_msgq_create() error\n");
    }

    i4_thread_runing = 1;
    if ( pthread_create(&ptd_id, NULL, _thread_async_process, NULL) != 0  )
    {
        mtvosp_msgq_delete(i4_msgq_handle);
        LOGE("pthread_create() error\n");
    }

    vlibhandle = dlopen(VOSP_LIBRARY_NAME, RTLD_NOW);	
    if (vlibhandle != NULL) 	
        {       
            LOGD("VOSP module loaded"); 
            }  
    else    
        {       
            LOGE("VOSP module not found");     
        }
    c2ms_mgmt_init = (void * (*)(uint8 *, uint64, uint8 *, int))dlsym(vlibhandle, VOSP_MGMT_INIT);
    c2ms_sess_open = (void * (*)(void *, uint8 *, int *))dlsym(vlibhandle, VOSP_OPEN);
    c2ms_sess_attr = (int (*)(void *, uint8 *, uint8 *, uint64 *, uint8 *))dlsym(vlibhandle, VOSP_ATTR);
    c2ms_sess_idxfile = (int (*)(void *, uint8 *, uint32 *))dlsym(vlibhandle, VOSP_IDXFILE);
    c2ms_sess_cache_init = (int (*)(void *, int))dlsym(vlibhandle, VOSP_CACHE_INIT);
    c2ms_sess_read = (int (*)(void *, void *, int, int))dlsym(vlibhandle, VOSP_READ);	
    c2ms_sess_bufratio = (int (*)(void *, int *))dlsym(vlibhandle, VOSP_BUFRATION);	
    c2ms_sess_idx_close = (int (*)(void *))dlsym(vlibhandle, VOSP_IDX_CLOSE);
    c2ms_sess_close =( int (*)(void *))dlsym(vlibhandle, VOSP_CLOSE);	
    c2ms_mgmt_clean =( int (*)(void *))dlsym(vlibhandle, VOSP_MGMT_CLEAN);	
    c2ms_sess_buffering =(int (*)(void *, int))dlsym(vlibhandle, VOSP_BUFFERING);	
    c2ms_sess_set_media = (int (*)(void *, int, uint64, uint64))dlsym(vlibhandle, VOSP_SET_MEDIA);	
    c2ms_sess_seek = (int (*)(void *, uint64))dlsym(vlibhandle, VOSP_SEEK);
    c2ms_sess_idx_open= (int (*)(void *, uint32 *))dlsym(vlibhandle, VOSP_IDX_OPEN);
    c2ms_sess_idx_open_asyn= (int (*)(void *, uint32 *))dlsym(vlibhandle, VOSP_IDX_OPEN_ASYN);

    //  pull_task[8] = {0};
    //  task_num=0;

}

VospPlayer::~VospPlayer()
{
    char buf[4] = {0};

    i4_thread_runing = 0;

    /*send msg to quit thread*/
    if (mtvosp_msgq_send(i4_msgq_handle, (void*)buf, 4))
    {
        LOGE("fatal error: send msg at last\n");
    }
    else
    {
        pthread_join(ptd_id, NULL);
    }

    mtvosp_msgq_delete(i4_msgq_handle);
    i4_msgq_handle = -1;

    dlclose(vlibhandle);		
    vlibhandle = NULL;
}

bool  VospPlayer::initialize(const std::string& uri, event_nfy_fct callback, void* pvTag)
{
    LOGD("enter vospplayer initialize()%s\n", uri.c_str());

    vospUrl = uri;

    IMTK_PB_ERROR_CODE_T e_return = IMtkPb_Ctrl_Open(&pbHandle,
                                                     IMTK_PB_CTRL_BUFFERING_MODEL_PULL,
                                                     IMTK_PB_CTRL_LIB_MASTER, (uint8_t*) "network");
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGE("xhg, e_return != IMTK_PB_ERROR_CODE_OK");
        return false;
    }

    e_return = IMtkPb_Ctrl_RegCallback(pbHandle, (void*)(pvTag), callback);

    IMTK_PB_CTRL_ENGINE_PARAM_T   t_parm;
    t_parm.u4PlayFlag = IMTK_PB_CTRL_PLAY_FLAG_VIDEO |
                        IMTK_PB_CTRL_PLAY_FLAG_AUDIO;

    t_parm.uBufferModelParam.tPullInfo.pvAppTag = (void*)this;
    t_parm.uBufferModelParam.tPullInfo.pfnOpen = pull_open;
    t_parm.uBufferModelParam.tPullInfo.pfnClose = pull_close;
    t_parm.uBufferModelParam.tPullInfo.pfnRead = pull_read;
    t_parm.uBufferModelParam.tPullInfo.pfnReadAsync = pull_read_async;
    t_parm.uBufferModelParam.tPullInfo.pfnAbortReadAsync = pull_abort_read_async;
    t_parm.uBufferModelParam.tPullInfo.pfnByteSeek = pull_byteseek;
    t_parm.uBufferModelParam.tPullInfo.pfnGetInputLen = pull_get_input_len;
    e_return = IMtkPb_Ctrl_SetEngineParam(pbHandle, &t_parm);

    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        LOGE("vospplayer initialize error %d", e_return);
        return false;
    }

    LOGD( "exit vospplayer initialize()");

    return true;

}

bool VospPlayer::play()
{
    LOGD("enter vosp:play()");
    IMtkPb_Ctrl_Play(pbHandle, (uint32_t)0);
    return true;
}
bool VospPlayer::pause()
{
    LOGD( "enter vosp:pause()");
    IMtkPb_Ctrl_Pause(pbHandle);
    return true;
}
bool VospPlayer::resume()
{
    LOGD( "enter vosp:resume()");
    IMtkPb_Ctrl_Play(pbHandle, 0);
    return true;
}

bool VospPlayer::GetDuration( int* msec )
{
    LOGD( "VospPlayer::GetDuration!\n" );

    IMTK_PB_ERROR_CODE_T e_return;
    IMTK_PB_CTRL_GET_MEDIA_INFO_T tMediaInfo;
    e_return = IMtkPb_Ctrl_GetMediaInfo(pbHandle, &tMediaInfo);
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        *msec = 0;
        LOGE("IMtkPb_Ctrl_GetMediaInfo() Failed !\n");
        return true;
    }

    *msec = (int)tMediaInfo.u4TotalDuration;
    if (pbHandle)
    {
        if (*msec != 0)
        {
            *msec += 1000;
        }
        else
        {
            *msec = 0xD65CB580;  //use 999 hours instead of zero duration
        }
    }

    LOGI( "VospPlayer::GetDuration exit!\n" );
    return true;
}

bool VospPlayer::stop()
{
    LOGD( "enter vosp:stop()");
    IMtkPb_Ctrl_Stop(pbHandle);
    return true;
}
bool VospPlayer::timeseek(int time)
{
    LOGD( "enter vosp:timeseek()");
    IMTK_PB_ERROR_CODE_T e_return;
    LOGD("TimeSeek= %d\n", time / 1000);

    e_return = IMtkPb_Ctrl_TimeSeek(pbHandle, time / 1000);
    if (e_return == IMTK_PB_ERROR_CODE_OK)
    {
        LOGD("IMtkPb_Ctrl_TimeSeek() OK!\n");
        return true;

    }
    else
    {
        LOGE("IMtkPb_Ctrl_TimeSeek() Failed !\n");
        return false;
    }
}
bool VospPlayer::getCurrentPosition(int* msec)
{
    IMTK_PB_ERROR_CODE_T e_return;
    uint32_t             u4Time;
    uint64_t             u8CurPos;

    e_return = IMtkPb_Ctrl_GetCurrentPos(pbHandle, &u4Time, &u8CurPos);
    LOGI("vosp_getCurrentPosition() time = %d, pos = %d \n", u4Time,  u8CurPos);
    if (e_return != IMTK_PB_ERROR_CODE_OK)
    {
        *msec = 0;
        LOGE("IMtkPb_Ctrl_GetCurrentPos() Failed !\n");
        return false;
    }

    *msec = (int)u4Time;
    return true;
}

IMTK_PB_CB_ERROR_CODE_T VospPlayer::pull_open(IMTK_PB_HANDLE_T hHandle,
                                              IMTK_PULL_HANDLE_T* phPullSrc, void* pvAppTag)
{
    LOGD("enter pull_open");

    int        exitcode = 0;
    uint8      fid[33];
    uint8      idxfid[33];
    uint8      mname[48];
    uint8      mmime = 0;
    int ret;
    int tmp = 0;
    /*
        PullTask * mPullTask = (PullTask*) malloc (sizeof(PullTask));
            if(!mPullTask)
            {
                LOGD( "#############mPullTask is NULL!!!\n");
                return IMTK_PB_CB_ERROR_CODE_NOT_OK;
            }
            memset(mPullTask, 0, sizeof(PullTask));
    */
    if(!firstCallPullOpen)
    {
        LOGD("second call pull_open\n");
       *phPullSrc = (IMTK_PULL_HANDLE_T) s->sess;
        LOGD("exit pull_open");
       return IMTK_PB_CB_ERROR_CODE_OK;
    }

    firstCallPullOpen=false;
    
    s->mgmt = c2ms_mgmt_init(NULL, 1000, NULL, VOSP_MGMT_BUFSIZE);
    if (!s->mgmt)
    {
        LOGE("c2ms_mgmt_init failed");
        goto errexit2;
    }
    
    s->sess = c2ms_sess_open(s->mgmt, (uint8*)vospUrl.c_str(), &exitcode);
    if (!s->sess)
    {
        LOGE("c2ms_sess_open failed");
        goto errexit1;
    }

    memset(fid, 0, sizeof(fid));
    memset(idxfid, 0, sizeof(idxfid));
    memset(mname, 0, sizeof(mname));

    c2ms_sess_attr(s->sess, fid, mname, &s->msize, &mmime);
    LOGD("      fid: %s\n", fid);
    LOGD("    mname: %s\n", mname);
    LOGD("xhg, s->msize%llu\n", s->msize);
    LOGD("xhg, mmime(leixing)%d\n", mmime);
    tmp = c2ms_sess_idxfile(s->sess, idxfid, &s->idxsize);
    LOGD("s->idxsize=%d\n", s->idxsize);
    LOGD("tmp=%d\n", tmp);
    c2ms_sess_cache_init(s->sess, VOSP_CACHE_BUFSIZE);
    if ((s->idxsize > 0) && (mmime == 7) )
    {
        LOGD("(s->idxsize > 0) && (mmime == 7)");
#if USE_IDX_OPEN
        ret = c2ms_sess_idx_open(s->sess, &(s->idxsize));
        if (ret >= 0)
        {
            LOGD("c2ms_sess_idx_open%d\n", ret );
        }

#else
        while (1)
        {
            ret = c2ms_sess_idx_open_asyn(s->sess, &(s->idxsize));
            if (ret == -1011)
            {
                LOGD("xhg, ret==-1011,continue");
                usec_sleep(50000);
                continue;
            }
            else if (ret < 0)
            {
                LOGE("xhg, ret<0,error");
                break;
            }
            else if (ret == 0)
            {
                LOGD("xhg, ret==0, ok");
                break;
            }
        }
#endif
    }
    c2ms_sess_buffering(s->sess, 3 * 1024 * 1024);
    c2ms_sess_set_media(s->sess, 0, 0, s->msize);
    s->offset = 0;
    c2ms_sess_seek(s->sess, s->offset);

    *phPullSrc = (IMTK_PULL_HANDLE_T) s->sess;

    LOGD("exit pull_open");

    return IMTK_PB_CB_ERROR_CODE_OK;

errexit1:
    if (s->sess)
    {
        LOGE("errexit1");
        c2ms_sess_close(s->sess);
    }
errexit2:
    if (s->mgmt)
    {
        LOGE("errexit2");
        c2ms_mgmt_clean(s->mgmt);
    }
errexit:
    LOGE("errexit");
    free(s);
    s = NULL;
    return IMTK_PB_CB_ERROR_CODE_NOT_OK;
}

IMTK_PB_CB_ERROR_CODE_T VospPlayer::pull_close(IMTK_PULL_HANDLE_T hPullSrc, void* pvAppTag)
{
    LOGD("enter pull_close");

    if (s)
    {
        if (s->idxsize > 0)
        {
            LOGD("s->idxsize > 0");
            c2ms_sess_idx_close(s->sess);
        }
        if (s->sess)
        {
            LOGD("s->sess");
            c2ms_sess_close(s->sess);
        }
        if (s->mgmt)
        {
            LOGD("s->mgmt");
            c2ms_mgmt_clean(s->mgmt);
        }
        free(s);
        s = NULL;
    }

    LOGD("exit pull_close");

    return IMTK_PB_CB_ERROR_CODE_OK;
}

IMTK_PB_CB_ERROR_CODE_T VospPlayer::pull_read(IMTK_PULL_HANDLE_T  hPullSrc,
                                              void*				 pvAppTag,
                                              uint8_t*				 pu1DstBuf,
                                              uint32_t				 u4Count,
                                              uint32_t*			 pu4Read)
{
    LOGD("enter pull_read: %u  %x \n", u4Count, pu1DstBuf);

    int ret = 0;

    if (!hPullSrc || !s->sess)
    {
        LOGE("xhg, pull_read !hPullSrc ||!s->sess");
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

    //ret = c2ms_sess_read(s->sess, pu1DstBuf, u4Count, 0); //0-block,1-nonblock
    ret = c2ms_sess_read((void*)hPullSrc, pu1DstBuf, u4Count, 0);

    LOGD("exit pull_read");
    if (ret >= 0)
    {
        LOGD("ret>=0,ret=%d\n", ret);
        *pu4Read = ret;
        return IMTK_PB_CB_ERROR_CODE_OK;
    }
    else
    {
        LOGD("ret<0,ret=%d\n", ret);
        *pu4Read = 0;
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

}

IMTK_PB_CB_ERROR_CODE_T VospPlayer::pull_read_async(IMTK_PULL_HANDLE_T		  hPullSrc,
                                                    void*					  pvAppTag,
                                                    uint8_t* 				  pu1Dst,
                                                    uint32_t 				  u4DataLen,
                                                    IMtkPb_Ctrl_Pull_Nfy_Fct   pfnNotify,
                                                    void*					  pvRdAsyTag,
                                                    uint32_t*				  pu4ReqId)
{
    ASYNC_READ_BLOCK_T t_read_block;

    LOGD("enter pull_read_async: read len=%d\n", u4DataLen);

    *pu4ReqId = 0;
    memset(&t_read_block, 0, sizeof(t_read_block));

    t_read_block.hPullSrc = hPullSrc;
    t_read_block.pfnNotify = pfnNotify;
    t_read_block.pu1Dst = pu1Dst;
    t_read_block.pvAppTag = pvAppTag;
    t_read_block.pvRdAsyTag =  pvRdAsyTag;
    t_read_block.u4DataLen = u4DataLen;
    t_read_block.u4ReqId = _u4_req_id;

    if (mtvosp_msgq_send(i4_msgq_handle, (void*)&t_read_block, sizeof(t_read_block)))
    {
        LOGE("send msg error\n");
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

    *pu4ReqId = _u4_req_id++;

    LOGD("pull_read_async(id=%d) at %ld\n", *pu4ReqId, _get_current_time_ms());

    return IMTK_PB_CB_ERROR_CODE_OK;

}

IMTK_PB_CB_ERROR_CODE_T VospPlayer::pull_abort_read_async(IMTK_PULL_HANDLE_T 	hPullSrc,
                                                          void*				 pvAppTag,
                                                          uint32_t				 u4ReqId)
{

    LOGD("enter pull_abort_read_async");

    LOGD("enter pull_abort_read_async");

    return IMTK_PB_CB_ERROR_CODE_OK;
}

IMTK_PB_CB_ERROR_CODE_T VospPlayer::pull_byteseek(IMTK_PULL_HANDLE_T 		hPullSrc,
                                                  void*					 pvAppTag,
                                                  int64_t					 i8SeekPos,
                                                  uint8_t					 u1Whence,
                                                  uint64_t*					pu8CurPos)
{
    //LOGD("enter pull_seek at %llu  whence:%d\n", i8SeekPos, u1Whence);

    int ret = 0;
    uint64 noffset = 0;

    if (!hPullSrc || !s->sess)
    {
        LOGE("xhg, pull_byteseek !hPullSrc ||!s->sess");
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }

    switch (u1Whence)
    {
        case IMTK_CTRL_PULL_SEEK_BGN:
            LOGD("seek at begin");
            noffset = i8SeekPos;
            break;
        case IMTK_CTRL_PULL_SEEK_CUR:
            LOGD("seek at current");
            noffset = s->offset + i8SeekPos;
            break;
        case IMTK_CTRL_PULL_SEEK_END:
            LOGD("seek ai end");
            noffset = s->msize - i8SeekPos;
            break;
        default:
            LOGD("seek default");
            return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
    //ret = c2ms_sess_seek(s->sess, noffset);
    ret = c2ms_sess_seek((void*)hPullSrc, noffset);

    LOGD("exit pull_seek");
    if (ret >= 0)
    {
        LOGD("seek success, ret=%d\n", ret);
        s->offset = noffset;
        if (pu8CurPos)
        {
            *pu8CurPos = s->offset;
            LOGD("seek at %llu\n", *pu8CurPos);
        }
        return IMTK_PB_CB_ERROR_CODE_OK;
    }
    else
    {
        LOGE("seek fail");
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
}

IMTK_PB_CB_ERROR_CODE_T VospPlayer::pull_get_input_len(IMTK_PULL_HANDLE_T  hPullSrc,
                                                       void* 		   pvAppTag,
                                                       uint64_t* 	   pu8Len)
{
    LOGD("enter pull_get_input_len");
    if (!hPullSrc || s->msize <= 0)
    {
        LOGE("hPullSrc nul or pull get input len");
        return IMTK_PB_CB_ERROR_CODE_NOT_OK;
    }
    else
    {
        *pu8Len = s->msize;
    }

    LOGD("exit pull_get_input_len%llu\n", s->msize);

    return IMTK_PB_CB_ERROR_CODE_OK;
}

/*
int VospPlayer::pull_check_task_index(int hPullSrc)
{
    int index = -1;
    int i = 0;
    for (i = 0; i < task_num; i++)
    {
        if (pull_task[i] && (pull_task[i]->pPullHandle == (IMTK_PULL_HANDLE_T)hPullSrc))
        {
            index = i;
            break;
        }
    }
    LOGD("\n#################index = %d###############\n\n", index);
    return index;
}

*/


