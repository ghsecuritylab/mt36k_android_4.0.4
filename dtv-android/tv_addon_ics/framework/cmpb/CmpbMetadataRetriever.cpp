/*
**
** Copyright 2009, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

//#define LOG_NDEBUG 0
#define LOG_TAG "CmpbMetadataRetriever"
#include <utils/Log.h>

#include "CmpbMetadataRetriever.h"
#include <media/mediametadataretriever.h>

namespace android {

#if USE_SINGLE_META_THREAD

static bool bMetaDataThreadRun = false;

#define META_DATA_QUEUE_CNT     50

typedef enum
{
    X_MSGQ_OPTION_WAIT = 1,
    X_MSGQ_OPTION_NOWAIT
}   MSGQ_OPTION_T;

typedef enum
{
    X_SEMA_TYPE_BINARY = 1,
    X_SEMA_TYPE_MUTEX,
    X_SEMA_TYPE_COUNTING
}   SEMA_TYPE_T;

typedef enum
{
    X_SEMA_OPTION_WAIT = 1,
    X_SEMA_OPTION_NOWAIT
}   SEMA_OPTION_T;


#define X_SEMA_STATE_LOCK   ((uint32_t) 0)
#define X_SEMA_STATE_UNLOCK ((uint32_t) 1)


static IMTK_PB_HANDLE_T       h_meta_que = 0;

extern "C" int32_t x_uc_w2s_to_ps(const uint16_t*  w2s_src, char* ps_dst, uint32_t ui4_dst_len);

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

extern "C" int32_t x_sema_create(IMTK_PB_HANDLE_T      *ph_sema_hdl,        /* semaphore handle */
                                      SEMA_TYPE_T   e_type,
                                      uint32_t        ui4_init_value);
extern "C" int32_t x_sema_delete(IMTK_PB_HANDLE_T h_sema_hdl);
extern "C" int32_t x_sema_lock(IMTK_PB_HANDLE_T h_sema_hdl,        /* semaphore handle */
                                   SEMA_OPTION_T e_option);
extern "C" int32_t x_sema_unlock(IMTK_PB_HANDLE_T h_sema_hdl);

typedef void (*x_os_thread_main_fct) (void*  pv_arg);
static IMTK_PB_HANDLE_T h_metadata_thd = 0;

extern "C" int32_t x_thread_create(IMTK_PB_HANDLE_T    *ph_th_hdl,         /* thread handle */
                                      const char  *ps_name,
                                      size_t       z_stacksize,
                                      uint8_t        ui1_pri,           /* thread priority */
                                      x_os_thread_main_fct pf_main,   /* thread's main function */
                                      size_t       z_arg_size,
                                      void         *pv_arg);

//////for destructor of instance sync////////
#define  MAX_INST_CNT      6

static  int32_t            i4_max_inst_cnt = 0;

typedef struct _DESTCT_INST_SYNC_T
{
    CmpbMetadataRetriever            *pt_this;
    //IMTK_PB_HANDLE_T hSema;
}   DESTCT_INST_SYNC_T;

static DESTCT_INST_SYNC_T  t_inst_sync[MAX_INST_CNT];
Mutex                      mDestInstSyncLock[MAX_INST_CNT];


#define ADD_INST(p_this)    \
{                            \
    int32_t i;               \
     for(i=0;i<MAX_INST_CNT;i++) \
     {                           \
        if(t_inst_sync[i].pt_this == NULL) \
        { \
            t_inst_sync[i].pt_this = p_this; \
            break; \
        } \
     }\
}

#define REMOVE_INST(p_this)    \
{                            \
    int32_t i;               \
     for(i=0;i<MAX_INST_CNT;i++) \
     {                           \
        if(t_inst_sync[i].pt_this == p_this) \
        { \
            t_inst_sync[i].pt_this = NULL; \
            break; \
        } \
     }\
}

#define FIND_INST(idx, p_this)    \
{                            \
    int32_t i;               \
    idx = MAX_INST_CNT;      \
     for(i=0;i<MAX_INST_CNT;i++) \
     {                           \
        if(t_inst_sync[i].pt_this == p_this) \
        { \
            idx = i; \
            break; \
        } \
     }\
}

static void _destrct_inst_sync_init()
{
    int32_t i = 0;
    
    LOGI("_destrct_inst_sync_init enter\n");
    //memset(t_inst_sync, 0, sizeof(t_inst_sync));
    for(i=0;i<MAX_INST_CNT;i++)
    {
        t_inst_sync[i].pt_this = NULL;       
    }
    LOGI("_destrct_inst_sync_init exit\n");
}

CmpbMetadataRetriever::CmpbMetadataRetriever()
{
    mCmpbPlayer = 0;
    mSemaMetaInfo = 0;
}

CmpbMetadataRetriever::~CmpbMetadataRetriever()
{
    LOGI("enter ~CmpbMetadataRetriever() ... this = %x \n", this);
    Mutex::Autolock lock(mLock);

    i4_max_inst_cnt --;
    if(i4_max_inst_cnt < 0)
    {
        LOGI("the count of instance < 0, Error!!!!! \n"); 
    }
    
    CmpbMetadataRetriever   *p_this = this;
    int32_t idx = 0;
    FIND_INST(idx, p_this);
    if(idx < MAX_INST_CNT)
    {
        LOGI("~~CmpbMetadataRetriever find this=%x, idx=%d", p_this, idx);
        LOGI("~CmpbMetadataRetriever() lock 3");
        mDestInstSyncLock[idx].lock();
        LOGI("~~CmpbMetadataRetriever taked lock");
        REMOVE_INST(p_this);
        LOGI("~CmpbMetadataRetriever() unlock 3");
        mDestInstSyncLock[idx].unlock();
        LOGI("~~CmpbMetadataRetriever giveup lock");
    }
    
    if(mSemaMetaInfo)
    {
        x_sema_delete(mSemaMetaInfo);
        mSemaMetaInfo = 0;
    }
    LOGI("exit ~CmpbMetadataRetriever() ... this = %x \n", this);
}

void  CmpbMetadataRetriever::MetaDataThread(void* param)
{
    int                       i4_ret;
    META_DATA_Q_ITEM_T        t_msg;
    CmpbMetadataRetriever*    p_this = NULL;
    uint16_t                  ui2_index;
    size_t                    msg_size = sizeof(t_msg);
    
    while(1)
    {
        if(h_meta_que)
        {
            int32_t i4_ret = x_msg_q_receive(&ui2_index, &t_msg, &msg_size, &h_meta_que, 1, X_MSGQ_OPTION_WAIT);
            if(0 != i4_ret)
            {
                LOGE("xxxx MetaDataThread():: x_msg_q_receive failed , ret = %d !!!!!\n", i4_ret);
                continue;
            }
        }
        else
        {
            LOGE("xxxx MetaDataThread(), error!!!! message queue create failed, return \n");
            return;
        }

        p_this = (CmpbMetadataRetriever*)(t_msg.pvObj);

        int32_t idx = 0;
        int32_t cur_idx = 0;
        FIND_INST(idx, p_this);
        if(idx >= MAX_INST_CNT)
        {
            LOGI("MetaDataThread, instance already destructed");
            continue;
        }

        cur_idx = idx;
        //mDestInstSyncLock[cur_idx].lock();
        LOGI("MetaDataThread find this=%x, idx=%d", p_this, idx);
        
        if(!p_this)
        {
            LOGE("xxxx MetaDataThread(), error!!!! p_this == NULL!!! \n");
            continue;
        }
        else
        {
            //LOGI("xxxx MetaDataThread(),  p_this == %x !!! \n", p_this);
        }

        switch(t_msg.eCmd)
        {
        case META_DATA_CMD_PREPARE:
            LOGI("xxxx MetaDataThread(), received command: META_DATA_CMD_PREPARE\n");
            if(p_this->mCmpbPlayer != 0)
            {
                p_this->mPrepareRet = p_this->mCmpbPlayer->prepare();
            }
            x_sema_unlock(p_this->mSemaMetaInfo);
            LOGI("xxxx MetaDataThread(), run command: META_DATA_CMD_PREPARE Done,this=%x, ret = %x \n",p_this, p_this->mPrepareRet);
            break;
        case META_DATA_CMD_CAPFRM:
            LOGI("xxxx MetaDataThread(), received command: META_DATA_CMD_CAPFRM\n");
            p_this->mCapFrm    = p_this->Internal_captureFrame();
            x_sema_unlock(p_this->mSemaMetaInfo);
            LOGI("xxxx MetaDataThread(), run command: META_DATA_CMD_CAPFRM Done\n");
            break;
        case META_DATA_CMD_EX_ALBART:
            LOGI("xxxx MetaDataThread(), received command: META_DATA_CMD_EX_ALBART\n");
            p_this->mAlbArt    = p_this->Internal_extractAlbumArt();
            x_sema_unlock(p_this->mSemaMetaInfo);
            LOGI("xxxx MetaDataThread(), run command: META_DATA_CMD_EX_ALBART Done\n");
            break;
        case META_DATA_CMD_GETMETA:
            LOGI("xxxx MetaDataThread(), received command: META_DATA_CMD_GETMETA\n");
            p_this->mMetaDataRet  = p_this->Internal_GetMetadataValues(t_msg.i4Param);
            LOGI("xxxx MetaDataThread(), run command: META_DATA_CMD_GETMETA Done\n");
            x_sema_unlock(p_this->mSemaMetaInfo);
            break;
        default:
            break;
        }
    }
}

void  CmpbMetadataRetriever::MetaDataThreadInit()
{
    int32_t i4_ret;
    
    LOGE("MetaDataThreadInit enter\n");

    if(0 == h_meta_que)
    {
        i4_ret = x_msg_q_create(&h_meta_que, "meta_data_msgq", sizeof(META_DATA_Q_ITEM_T), META_DATA_QUEUE_CNT);
        LOGE("MetaDataThreadInit:: create message queue, ret = %d !!!!!\n", i4_ret);
    }

    if (0 == h_metadata_thd)
    {
        i4_ret = x_thread_create(&h_metadata_thd,
                                 "android_meta_data_thread",
                                 20480,
                                 10,
                                 MetaDataThread,
                                 sizeof(IMTK_PB_HANDLE_T *),
                                 (void *)&h_meta_que);
        //createThread(MetaDataThread, 0);

        LOGE("MetaDataThreadInit x_thread_create, ret = %d \n", i4_ret);
    }

    LOGE("MetaDataThreadInit end\n");
}
#endif



//uint32_t multi_char_2_ascii(char* ac_text, uint16_t* awc_text, uint32_t ui4_len);
#if 0
CmpbMetadataRetriever::CmpbMetadataRetriever()
{
#if 0 //USE_SINGLE_META_THREAD
    mPrepareRet  = 0;
    mCapFrm        = 0;
    mAlbArt      = 0;
    mMetaDataRet = 0; 
#endif
}

CmpbMetadataRetriever::~CmpbMetadataRetriever()
{
#if 0 //USE_SINGLE_META_THREAD
    mPrepareRet  = 0;
    mCapFrm        = 0;
    mAlbArt      = 0;
    mMetaDataRet = 0; 
#endif
}
#endif

void CmpbMetadataRetriever::clearMetadataValues()
{
    LOGV("cleearMetadataValues");
    mMetadataValues[0][0] = '\0';
}

status_t CmpbMetadataRetriever::setDataSource(const char *url, const KeyedVector<String8, String8> *headers)
{
    LOGI("enter setDataSource: url(%s), this = %x \n", url? url: "NULL pointer", this);
    Mutex::Autolock lock(mLock);
    clearMetadataValues();
    if (mCmpbPlayer == 0) 
    {
        mCmpbPlayer = new CmpbPlayer(true);
    }
    if(mSemaMetaInfo == 0)
    {
        int32_t i4_ret;
        i4_ret = x_sema_create(
                                &(mSemaMetaInfo),
                                X_SEMA_TYPE_BINARY,
                                X_SEMA_STATE_LOCK
                                );
        //if(0 != i4_ret)
        {
            LOGI("create own sema mSemaMetaInfo  i4_ret = %d \n", i4_ret);
        }
    }
    // TODO: support headers in MetadataRetriever interface!
    mCmpbPlayer->setDataSource(url, headers );
#if    USE_SINGLE_META_THREAD

	if(!bMetaDataThreadRun)
    {
        bMetaDataThreadRun = true;
        MetaDataThreadInit();
        _destrct_inst_sync_init();
    }

    //////////add to instance/////////////
    i4_max_inst_cnt ++;
    if(i4_max_inst_cnt >= MAX_INST_CNT)
    {
        LOGI("CmpbMetadataRetriever::the count of instance is more than max \n"); 
    }

    CmpbMetadataRetriever *p_this = this;
    int32_t idx = 0;
    FIND_INST(idx, NULL);
    if(idx < MAX_INST_CNT)
    {
        LOGI("CmpbMetadataRetriever() find this=%x, idx=%d", p_this, idx);
        LOGI("CmpbMetadataRetriever() lock 1");
        mDestInstSyncLock[idx].lock();
        ADD_INST(p_this);
        LOGI("CmpbMetadataRetriever() unlock 1");
        mDestInstSyncLock[idx].unlock();
    }
    else
    {
        LOGI("CmpbMetadataRetriever() error!!! instance is full");
    }

    mPrepareRet = (status_t)UNKNOWN_ERROR;
    if(h_meta_que)
    {
        META_DATA_Q_ITEM_T msg;

        msg.eCmd    = META_DATA_CMD_PREPARE;
        msg.pvObj   = this;
        msg.i4Param = 0;
        int32_t  i4_ret = x_msg_q_send(h_meta_que, &msg, sizeof(msg), 0);
        if(0 != i4_ret)
        {
            LOGE("enter setDataSource(): x_msg_q_send failed, ret = %d !!!!!\n", i4_ret);
        }
    }
    else
    {
        LOGE("exit setDataSource(): message queue create error!\n");
        return UNKNOWN_ERROR;
    }

    x_sema_lock(mSemaMetaInfo, X_SEMA_OPTION_WAIT);
    LOGI(" setDataSource(): RUN complete, mPrepareRet = %d\n", mPrepareRet);
    
    IMTK_PB_HANDLE_T    h_cmpb_handle = NULL;
    mCmpbPlayer->getCmpbHandle(&h_cmpb_handle);
    LOGI(" setDataSource(): get player handle = %x \n", h_cmpb_handle);
#if 1
    if(h_cmpb_handle)
    {
        char sz_lang[4] = {'c','h','i',0};
        uint32_t  u4_size = 4;
        IMTK_PB_ERROR_CODE_T eRet = 
            IMtkPb_Ctrl_Set(h_cmpb_handle,
                            IMTK_PB_CTRL_SET_TYPE_MP3_META_LANG,
                            sz_lang,&u4_size);

        LOGI(" setDataSource(): set mp3 lang, ret = %d \n", eRet);
    }
#endif
    LOGI("exit CmpbMetadataRetriever::setDataSource(): this = %x, ret = %x !\n", this, mPrepareRet);

    return mPrepareRet;
#else
    return mCmpbPlayer->prepare();
#endif
}

status_t CmpbMetadataRetriever::setDataSource(int fd, int64_t offset, int64_t length)
{
    LOGI("enter setDataSource: fd(%d), offset(%lld), and length(%lld), this = %x \n", fd, offset, length, this);
    Mutex::Autolock lock(mLock);
    clearMetadataValues();
    if (mCmpbPlayer == 0) 
    {
        mCmpbPlayer = new CmpbPlayer(true);
    }
    mCmpbPlayer->setDataSource(fd, offset, length);
#if    USE_SINGLE_META_THREAD

	if(!bMetaDataThreadRun)
    {
        bMetaDataThreadRun = true;
        MetaDataThreadInit();
    }

    i4_max_inst_cnt ++;
    if(i4_max_inst_cnt >= MAX_INST_CNT)
    {
        LOGI("CmpbMetadataRetriever::the count of instance is more than max \n"); 
    }

    CmpbMetadataRetriever *p_this = this;
    int32_t idx = 0;
    FIND_INST(idx, NULL);
    if(idx < MAX_INST_CNT)
    {
        LOGI("CmpbMetadataRetriever() find this=%x, idx=%d", p_this, idx);
        LOGI("CmpbMetadataRetriever() lock 1");
        mDestInstSyncLock[idx].lock();
        ADD_INST(p_this);
        LOGI("CmpbMetadataRetriever() unlock 1");
        mDestInstSyncLock[idx].unlock();
    }
    else
    {
        LOGI("CmpbMetadataRetriever() error!!! instance is full");
    }

    mPrepareRet  = (status_t)UNKNOWN_ERROR;
    if(h_meta_que)
    {
        META_DATA_Q_ITEM_T msg;

        msg.eCmd    = META_DATA_CMD_PREPARE;
        msg.pvObj   = this;
        msg.i4Param = 0;
        int32_t  i4_ret = x_msg_q_send(h_meta_que, &msg, sizeof(msg), 0);
        if(0 != i4_ret)
        {
            LOGE("setDataSource(): x_msg_q_send failed, ret = %d !!!!!\n", i4_ret);
        }
    }
    else
    {
        LOGE("exit setDataSource(): message queue create error!\n");
        return UNKNOWN_ERROR;
    }

    x_sema_lock(mSemaMetaInfo, X_SEMA_OPTION_WAIT);
    //LOGI(" setDataSource(): RUN complete, mPrepareRet = %d\n", mPrepareRet);

    //IMTK_PB_HANDLE_T    h_cmpb_handle = NULL;
    //mCmpbPlayer->getCmpbHandle(&h_cmpb_handle);
    //LOGI(" setDataSource(): get player handle = %x \n", h_cmpb_handle);
#if 0
    if(h_cmpb_handle)
    {
        char sz_lang[4] = {'c','h','i',0};
        uint32_t  u4_size = 4;
        IMTK_PB_ERROR_CODE_T eRet = 
            IMtkPb_Ctrl_Set(h_cmpb_handle,
                            IMTK_PB_CTRL_SET_TYPE_MP3_META_LANG,
                            sz_lang,&u4_size);

        LOGI(" setDataSource(): set mp3 lang, ret = %d \n", eRet);
    }
#endif
    LOGI("exit setDataSource(): mPrepareRet = %x \n", mPrepareRet);

    return mPrepareRet;
#else
    return mCmpbPlayer->prepare();
#endif
}

VideoFrame* CmpbMetadataRetriever::Internal_captureFrame(void)
{
    uint8_t*                    pui1_buf        = NULL;
    IMTK_PB_HANDLE_T            h_cmpb_handle   = NULL;
    IMTK_PB_ERROR_CODE_T        e_return;
    IMTK_PB_THUMBNAIL_INFO_T    t_thumb_info;
    
#if 1 // 0 -> ENABLE THUMBNAIL
    //LOGE("Navy captureFrame start - #### SKIP,Return NULL ####");
    return NULL;
#else
    LOGI("Navy captureFrame start");
    if (mCmpbPlayer == 0) 
    {
        LOGE("Navy  %s line %d\r\n", __func__, __LINE__);
        return NULL;
    }    
    
    memset(&t_thumb_info, 0, sizeof(IMTK_PB_THUMBNAIL_INFO_T));
        
    pui1_buf = new uint8_t[THUMBNAIL_BUFFER_SIZE];
    if(pui1_buf == NULL)
    {
        LOGE("Navy  Out of memory !!! \n\r");     
        return NULL;
    }
    memset(pui1_buf, 0, THUMBNAIL_BUFFER_SIZE);
    
    t_thumb_info.u1CanvasBuffer     = pui1_buf;
    t_thumb_info.eCanvasColormode   = THUMBNAIL_COLORMODE_RGB_D565;
    t_thumb_info.u4BufLen           = THUMBNAIL_BUFFER_SIZE;
    t_thumb_info.u4ThumbnailWidth   = 176;
    t_thumb_info.u4ThumbnailHeight  = 144;
    
    mCmpbPlayer->getCmpbHandle(&h_cmpb_handle);
    e_return = IMtkPb_Ctrl_GetThumbNail(h_cmpb_handle,
                                        &t_thumb_info);
    LOGI("Navy  ======IMtkPb_Ctrl_GetThumbNail ret %d  =======\r\n", e_return);
    //if(e_return == IMTK_PB_ERROR_CODE_OK)
    {
        VideoFrame *frame = new VideoFrame();
        if(frame == NULL)
        {
            delete pui1_buf;
            pui1_buf = NULL;
            return NULL;
        }
        
        frame->mWidth           = t_thumb_info.u4ThumbnailWidth;
        frame->mHeight          = t_thumb_info.u4ThumbnailHeight;
        frame->mDisplayWidth    = t_thumb_info.u4ThumbnailWidth;
        frame->mDisplayHeight   = t_thumb_info.u4ThumbnailHeight;
        frame->mSize            = t_thumb_info.u4ThumbnailWidth * t_thumb_info.u4ThumbnailHeight * 2;
        frame->mData            = t_thumb_info.u1CanvasBuffer;
        
        return frame;
    }
    //LOGE("Navy  %s line %d\r\n", __func__, __LINE__);
    //return NULL;
#endif
}

MediaAlbumArt* CmpbMetadataRetriever::Internal_extractAlbumArt(void)
{
    void*                   pv_phy_addr = NULL;
    void*                   pv_vir_addr = NULL;
    IMTK_PB_HANDLE_T        h_cmpb_handle     = NULL;
    IMTK_PB_ERROR_CODE_T    e_return;
    IMTK_PB_CTRL_MP3_COVER_INFO_T   t_mp3_img;
    
    //LOGV("extractAlbumArt start");
    
    if (mCmpbPlayer == 0) 
    {
        return NULL;
    }
            
    /* -----------Check Mp3 Cover Exist or Not, If exist, get info---------- */   
    memset(&t_mp3_img, 0, sizeof(IMTK_PB_CTRL_MP3_COVER_INFO_T));
    mCmpbPlayer->getCmpbHandle(&h_cmpb_handle);
    e_return = IMtkPb_Ctrl_Get(h_cmpb_handle,
                               IMTK_PB_CTRL_GET_TYPE_MP3_COVER,
                               &t_mp3_img,
                               sizeof(IMTK_PB_CTRL_MP3_COVER_INFO_T));
    if(e_return == IMTK_PB_ERROR_CODE_OK)
    {
        if((t_mp3_img.e_img_type != IMTK_MP3_COVER_IMG_TYPE_UNKNOWN) &&
           (t_mp3_img.ui4_length != 0))
        {
            e_return = IMtkPb_Ctrl_GetSHM(  h_cmpb_handle, 
                                            t_mp3_img.ui4_length, 
                                            &pv_phy_addr, 
                                            &pv_vir_addr);            
            if(e_return == IMTK_PB_ERROR_CODE_OK)
            {
                t_mp3_img.pui1_img_data_buf = (uint8_t*)pv_phy_addr;
            }
            else
            {
                return NULL;
            }
        }
        else
        {
            return NULL;
        }
            LOGE("========line %d  GotMp3CoverSuccess W=%d, H=%d, ===========\r\n", 
                __LINE__,t_mp3_img.ui4_width, t_mp3_img.ui4_height);
        
        /* -----------Get Mp3 cover data---------- */  
        e_return = IMtkPb_Ctrl_Get(h_cmpb_handle,
                                   IMTK_PB_CTRL_GET_TYPE_MP3_COVER,
                                   &t_mp3_img,
                                   sizeof(IMTK_PB_CTRL_MP3_COVER_INFO_T));
        if(e_return == IMTK_PB_ERROR_CODE_OK)
        {
            MediaAlbumArt *mp3cover = new MediaAlbumArt();
            if(mp3cover == NULL)
            {
                IMtkPb_Ctrl_FreeSHM(h_cmpb_handle,
                                    t_mp3_img.ui4_length,
                                    pv_phy_addr,
                                    pv_vir_addr);
                return NULL;
            }
            mp3cover->mSize = t_mp3_img.ui4_length;
            mp3cover->mData = new uint8_t[t_mp3_img.ui4_length];
            memcpy(mp3cover->mData, pv_vir_addr, t_mp3_img.ui4_length);

            IMtkPb_Ctrl_FreeSHM(h_cmpb_handle,
                                t_mp3_img.ui4_length,
                                pv_phy_addr,
                                pv_vir_addr);
            
            LOGE("========line %d  GotMp3CoverSuccess W=%d, H=%d, size = %d ===========\r\n", 
                __LINE__,t_mp3_img.ui4_width, t_mp3_img.ui4_height, mp3cover->mSize);
            return mp3cover;
        }
    }
    return NULL;
}


status_t CmpbMetadataRetriever::Internal_GetMetadataValues(int keyCode)
{
    IMTK_PB_ERROR_CODE_T            e_return;
    IMTK_PB_CTRL_META_TYPE_T        e_meta_type;
    IMTK_PB_CTRL_META_DATA_INFO_T   t_meta_info;
    //IMTK_PB_CTRL_MEDIA_INFO_T       t_media_info;
    IMTK_PB_HANDLE_T                h_cmpb_handle = NULL;
    //bool                            b_get_media_info = false;
    //bool                            b_get_audio_strm_nums = false;    
    memset(mMetadataTmpBuf, 0, MAX_METADATA_STRING_LENGTH);
    memset(&t_meta_info,    0, sizeof(IMTK_PB_CTRL_META_DATA_INFO_T));
    //memset(&t_media_info,   0, sizeof(IMTK_PB_CTRL_MEDIA_INFO_T));

    switch(keyCode)
    {
    case METADATA_KEY_CD_TRACK_NUMBER:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_CD_TRACK_NUMBER;
        break;
    case METADATA_KEY_ALBUM:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_ALBUM;
        break;
    case METADATA_KEY_ARTIST:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_ARTIST;
        break;
    case METADATA_KEY_AUTHOR:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_AUTHOR;
        break;
    case METADATA_KEY_COMPOSER:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_COMPOSER;
        break;
    case METADATA_KEY_DATE:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_DATE;
        break;
    case METADATA_KEY_GENRE:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_GENRE;
        break;
    case METADATA_KEY_TITLE:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_TITLE;
        break;
    case METADATA_KEY_YEAR:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_YEAR;
        break;
    case METADATA_KEY_DURATION:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_DURATION;
        break;
    case METADATA_KEY_NUM_TRACKS:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_NUM_TRACKS;
        break;
    case METADATA_KEY_IS_DRM:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_IS_DRM_CRIPPLED;
        break;
        #if 0
    case METADATA_KEY_CODEC:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_CODEC;
        break;
    case METADATA_KEY_RATING:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_RATING;
        break;
    case METADATA_KEY_COPYRIGHT:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_COPYRIGHT;
        break;
    case METADATA_KEY_FRAME_RATE:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_FRAME_RATE;
        break;
    case METADATA_KEY_HAS_VIDEO:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_VIDEO_FORMAT;
        break;
        #endif
    case METADATA_KEY_COMPILATION:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_COMMENT;
        break;
    
    case METADATA_KEY_BITRATE:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_BITRATE;
        break;    
    
    case METADATA_KEY_VIDEO_HEIGHT:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_VIDEO_HEIGHT;
        break;
    case METADATA_KEY_VIDEO_WIDTH:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_VIDEO_WIDTH;
        break;
    case METADATA_KEY_WRITER:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_WRITER;
        break;
    case METADATA_KEY_MIMETYPE:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_MIMETYPE;
        break;
    case METADATA_KEY_DISC_NUMBER:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_DISCNUMBER;
        break;
    case METADATA_KEY_ALBUMARTIST:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_ALBUMARTIST;
        break;
        
    default:
        e_meta_type = IMTK_PB_CTRL_META_TYPE_INVAL;
        return UNKNOWN_ERROR;
    }
    mCmpbPlayer->getCmpbHandle(&h_cmpb_handle);
    
        
    t_meta_info.e_meta_type = e_meta_type;
    t_meta_info.pv_buf      = (void*)mMetadataTmpBuf;
    t_meta_info.ui2_buf_size= MAX_METADATA_STRING_LENGTH;
    e_return = IMtkPb_Ctrl_Get(h_cmpb_handle,
                               IMTK_PB_CTRL_GET_TYPE_META_DATA,
                               (void*)&t_meta_info,
                               sizeof(IMTK_PB_CTRL_META_DATA_INFO_T));
    
    if(e_return == IMTK_PB_ERROR_CODE_OK)
    {
#if 0
        uint8_t ui1_i = 0;
        for(ui1_i = 0; ui1_i < MAX_METADATA_STRING_LENGTH/2; ui1_i++)
        {
            mMetadataTmpBuf[ui1_i] = mMetadataTmpBuf[2*ui1_i];
            if(mMetadataTmpBuf[ui1_i] == 0)
            {
                break;
            }
        }
#endif
        return OK;
    }
    else
    {
        return UNKNOWN_ERROR;
    }
}

VideoFrame* CmpbMetadataRetriever::getFrameAtTime(int64_t timeUs, int option)
{
#if 0  //remove thumbnails
    return NULL;
#else
#if    USE_SINGLE_META_THREAD
    LOGI("enter captureFrame(): this = %x \n", this);
    Mutex::Autolock lock(mLock);

    mCapFrm = (VideoFrame*)0;

    if(h_meta_que)
    {
        META_DATA_Q_ITEM_T msg;

        msg.eCmd    = META_DATA_CMD_CAPFRM;
        msg.pvObj   = this;
        msg.i4Param = 0;
        int32_t  i4_ret = x_msg_q_send(h_meta_que, &msg, sizeof(msg), 0);
        if(0 != i4_ret)
        {
            LOGE(" captureFrame(): x_msg_q_send failed, ret = %d !!!!!\n", i4_ret);
        }
    }
    else
    {
        LOGE("exit captureFrame(): message queue create error!\n");
        return 0;
    }

    x_sema_lock(mSemaMetaInfo, X_SEMA_OPTION_WAIT);
    LOGI("exit captureFrame(): RUN complete, mCapFrm = %x\n", mCapFrm);    

    return mCapFrm;
#else
    return Internal_captureFrame();
#endif
#endif
}

MediaAlbumArt* CmpbMetadataRetriever::extractAlbumArt(void)
{
#if    USE_SINGLE_META_THREAD

    LOGI("enter extractAlbumArt(): this = %x \n", this);
    Mutex::Autolock lock(mLock);

    mAlbArt = (MediaAlbumArt*)0;
    
    if(h_meta_que)
    {
        META_DATA_Q_ITEM_T msg;

        msg.eCmd    = META_DATA_CMD_EX_ALBART;
        msg.pvObj   = this;
        msg.i4Param = 0;
        int32_t  i4_ret = x_msg_q_send(h_meta_que, &msg, sizeof(msg), 0);
        if(0 != i4_ret)
        {
            LOGE(" extractAlbumArt(): x_msg_q_send failed, ret = %d !!!!!\n", i4_ret);
        }
    }
    else
    {
        LOGE("exit extractAlbumArt(): message queue create error!\n");
        return 0;
    }

    x_sema_lock(mSemaMetaInfo, X_SEMA_OPTION_WAIT);
    LOGI("exit extractAlbumArt(): RUN complete, mAlbArt = %x\n", mAlbArt);    

    return mAlbArt;
#else
    return Internal_extractAlbumArt();
#endif
}

status_t CmpbMetadataRetriever::GetMetadataValues(int keyCode)
{
#if    USE_SINGLE_META_THREAD
    mMetaDataRet = (status_t)UNKNOWN_ERROR;

    LOGI("enter GetMetadataValues(): this = %x, keycode = %d \n", this, keyCode);
    
    if(h_meta_que)
    {
        META_DATA_Q_ITEM_T msg;

        msg.eCmd    = META_DATA_CMD_GETMETA;
        msg.pvObj   = this;
        msg.i4Param = keyCode;
        int32_t  i4_ret = x_msg_q_send(h_meta_que, &msg, sizeof(msg), 0);
        if(0 != i4_ret)
        {
            LOGE(" GetMetadataValues(): x_msg_q_send failed, ret = %d !!!!!\n", i4_ret);
        }
    }
    else
    {
        LOGE("exit GetMetadataValues(): message queue create error!\n");
        return UNKNOWN_ERROR;
    }

    x_sema_lock(mSemaMetaInfo, X_SEMA_OPTION_WAIT);
    LOGI("exit GetMetadataValues(): RUN complete, mMetaDataRet = %d\n", mMetaDataRet);    

    return mMetaDataRet;
#else
    return Internal_GetMetadataValues(keyCode);
#endif
}

#if 0
uint32_t multi_char_2_ascii(char* ac_text, uint16_t* awc_text, uint32_t ui4_len)
{
    uint32_t ui4_cnt;
    uint32_t ui4_dst_cnt;

    ui4_dst_cnt = 0;
    for(ui4_cnt = 0; ui4_cnt < (ui4_len / 2) && awc_text[ui4_cnt]; )
    {
        if(awc_text[ui4_cnt] < 0x80)
        {
            ac_text[ui4_dst_cnt++] = (char)awc_text[ui4_cnt];
        }
        ui4_cnt ++;
    }
    ac_text[ui4_dst_cnt] = 0;

    return ui4_dst_cnt;
}
#endif

const char* CmpbMetadataRetriever::extractMetadata(int keyCode)
{
    int        i4_tmp = 0;
    LOGI("enter extractMetadata: key(%d)", keyCode);
    Mutex::Autolock lock(mLock);
    if (mCmpbPlayer == 0 || mCmpbPlayer->initCheck() != NO_ERROR) 
    {
        LOGE("exit  no vorbis player is initialized yet");
        return NULL;
    }

    if(GetMetadataValues(keyCode) != OK)
    {
        LOGE("exit GetMetadataValues(%d) Failed !!!", keyCode);
        return NULL;    
    }

    switch(keyCode)
    {
    case METADATA_KEY_CD_TRACK_NUMBER:
    case METADATA_KEY_DURATION:
    case METADATA_KEY_NUM_TRACKS:
    case METADATA_KEY_BITRATE:
    case METADATA_KEY_VIDEO_HEIGHT:
    case METADATA_KEY_VIDEO_WIDTH:
        i4_tmp = *((int*)mMetadataTmpBuf);
        snprintf(mMetadataValues[0], MAX_METADATA_STRING_LENGTH, "%d", i4_tmp);
        break;
        #if 0
    case METADATA_KEY_FRAME_RATE:
    {
        int rate  = *((int*)mMetadataTmpBuf);
        int scale = *(((int*)mMetadataTmpBuf)+ 1);
        snprintf(mMetadataValues[0], MAX_METADATA_STRING_LENGTH, "%d.%d", rate/scale, rate%scale);
    }
        break;
        #endif
    case METADATA_KEY_ALBUM:
    case METADATA_KEY_ARTIST:
    case METADATA_KEY_AUTHOR:
    case METADATA_KEY_GENRE:
    case METADATA_KEY_TITLE:
    case METADATA_KEY_YEAR:
    //case METADATA_KEY_COPYRIGHT:
        //memcpy(mMetadataValues[0], mMetadataTmpBuf, MAX_METADATA_STRING_LENGTH);
        x_uc_w2s_to_ps((const uint16_t*)mMetadataTmpBuf, (char*)(mMetadataValues[0]), (uint32_t)MAX_METADATA_STRING_LENGTH);
        if((char*)(mMetadataValues[0]))
        {
            LOGI("extractMetadata, value = %s \n", (char*)(mMetadataValues[0]));
        }
        break;
    case METADATA_KEY_COMPOSER:/* not implemented */
    case METADATA_KEY_DATE:/* TBD: Should convert ??? */
    case METADATA_KEY_IS_DRM:
    case METADATA_KEY_COMPILATION:
    case METADATA_KEY_WRITER:
    case METADATA_KEY_MIMETYPE:
    case METADATA_KEY_DISC_NUMBER:
    case METADATA_KEY_ALBUMARTIST:
    case METADATA_KEY_LOCATION:
    case METADATA_KEY_TIMED_TEXT_LANGUAGES:
    case METADATA_KEY_HAS_AUDIO:
    case METADATA_KEY_HAS_VIDEO:
    default:
        LOGI("exit extractMetadata\n");
        return NULL;
    }

    LOGI("exit extractMetadata\n");
    return mMetadataValues[0];
}

};

