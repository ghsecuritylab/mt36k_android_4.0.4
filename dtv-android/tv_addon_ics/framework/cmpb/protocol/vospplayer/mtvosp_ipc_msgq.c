/******************************************************************************
FileName: ipc_msgq.c
Copyright (c) 2011, MediaTek(ShenZhen)Inc. All Rights Reserved.

Version: Ver 1.0
Author: mawei.ma (mawei.ma@mediatek.com Dept:DTV1)

Description:
		ipc msgq for android

Note:
		none
History:
Version      Date                  Author       Modification
-------    ----------    -------    ------------
1.0              2011-12-28    mawei.ma        Create
*******************************************************************************/
/******************************************************************************
 *                                 Includes                                   *
 ******************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <errno.h>
#include <sys/time.h>

#include "mtvosp_ipc_msgq.h"
/******************************************************************************
 *                                 Defines                                    *
 ******************************************************************************/
#define MTVOSP_HANDLE_MSGQ_VALUE_START        (0x123456)

#if 0
#define MTVOSP_MIN_AT_MSGQ(x, y)                    \
    ( {\
        typeof(x) _x = (x); \
        typeof(y) _y = (y); \
        (void) (&_x == &_y); \
        _x < _y ? _x : _y; } \
    )
#else
#define MTVOSP_MIN_AT_MSGQ(x, y)   ( (x) < (y)? (x) : (y))
#endif

#define MTVOSP_DEBUG_LOG_ENABLE                       (1)  /*debug this model*/

#ifdef MTVOSP_LOG_FOR_LOGCAT

#if MTVOSP_DEBUG_LOG_ENABLE
#define MTVOSP_MSGQ_DBUG(fmt, ...)  LOGD( "[%s]line:%d "fmt, __func__, __LINE__, ##__VA_ARGS__)
#else
#define MTVOSP_MSGQ_DBG(...)
#endif

#define MTVOSP_MSGQ_ERR(fmt, ...)    LOGE("[%s]line:%d "fmt, __func__, __LINE__, ##__VA_ARGS__)
#define MTVOSP_MSGQ_INFO(fmt, ...)  LOGI( fmt, ##__VA_ARGS__)


#else  // MTVOSP_LOG_FOR_LOGCAT

#if MTVOSP_DEBUG_LOG_ENABLE
#define MTVOSP_MSGQ_DBG(fmt, ...)  fprintf(stderr, "[%s]line:%d "fmt, __func__, __LINE__, ##__VA_ARGS__)
//#define MTVOSP_MSGQ_DBUG(fmt, ...)  LOGD( "[%s]line:%d "fmt, __func__, __LINE__, ##__VA_ARGS__)
#else
#define MTVOSP_MSGQ_DBG(...)
#endif

#define MTVOSP_MSGQ_ERR(fmt, ...)    fprintf(stderr, "[%s]line:%d "fmt, __func__, __LINE__, ##__VA_ARGS__)
#define MTVOSP_MSGQ_INFO(fmt, ...)  fprintf(stdout, fmt, ##__VA_ARGS__)

#endif  //MTVOSP_LOG_FOR_LOGCAT


typedef struct _mtvosp_msgq_ctx
{
    struct list_head   t_head_list;
    unsigned int         ui4_msg_num;
    pthread_mutex_t  t_mutex;
    pthread_cond_t    t_cond;
} MTVOSP_MSGQ_CTX_T;

/******************************************************************************
 *                                 Global Variables                           *
 ******************************************************************************/

/******************************************************************************
 *                                 Local Variables                            *
 ******************************************************************************/
static MTVOSP_MSGQ_CTX_T* _mtvosp_pt_msgq_ctx[MTVOSP_MAX_MSGQ_NUMS];

/******************************************************************************
 *                                 Local Functions Declaring                  *
 ******************************************************************************/
static MTVOSP_MSG_BUF_T* _mtvosp_msg_buf_create(void* pv_msgq_buf, int i4_msgq_size);
static int _mtvosp_msg_buf_delete(MTVOSP_MSG_BUF_T* pt_msg_buf);

/******************************************************************************
 *                                 Global Functions                           *
 ******************************************************************************/
/**
 * mtvosp_msgq_create.
 * create a message queue, you can create less than MTVOSP_MAX_MSGQ_NUMS message queue in a process
 *
 * @param[out]    pi4_msgq_handle    a handle used by other function in this model
 *
 * @return
 * @retval 0       success
 * @retval -1      fail
 *
 * @authors    mawei.ma
 * @date         2011/12/29
 */
int mtvosp_msgq_create(int* pi4_msgq_handle)
{
    int i = 0;
    int i4_ret = 0;
    MTVOSP_MSGQ_CTX_T* pt_ctx = NULL;

    if (!pi4_msgq_handle)
    {
        MTVOSP_MSGQ_ERR("arg err: pi4_msgq_handle=NULL\n");
        return -1;
    }

    while (i < MTVOSP_MAX_MSGQ_NUMS)
    {
        if (!_mtvosp_pt_msgq_ctx[i])
        {
            break;
        }

        i++;
    }

    if (MTVOSP_MAX_MSGQ_NUMS == i)
    {
        MTVOSP_MSGQ_ERR("Error: out of MTVOSP_MAX_MSGQ_NUMS=%d\n", MTVOSP_MAX_MSGQ_NUMS);
        return -1;
    }

    pt_ctx = (MTVOSP_MSGQ_CTX_T*)calloc(sizeof(MTVOSP_MSGQ_CTX_T), 1);
    if (!pt_ctx)
    {
        MTVOSP_MSGQ_ERR("Error: out of memory\n");
        return -1;
    }

    /*init list head in current msgq*/
    INIT_LIST_HEAD(&pt_ctx->t_head_list);

    pt_ctx->ui4_msg_num = 0;
    pthread_mutex_init(&pt_ctx->t_mutex, NULL);
    pthread_cond_init(&pt_ctx->t_cond, NULL);

    _mtvosp_pt_msgq_ctx[i] = pt_ctx;

    *pi4_msgq_handle = i + MTVOSP_HANDLE_MSGQ_VALUE_START;

    MTVOSP_MSGQ_DBG("handle=0x%x\n", *pi4_msgq_handle);

    return 0;
}


/**
 * mtvosp_msgq_delete.
 * delete a created message queue by mtvosp_msgq_create()
 *
 * @param[in]    i4_msgq_handle   the handle return by mtvosp_msgq_create()
 *
 * @return
 * @retval 0       success
 * @retval -1      fail
 *
 * @authors    mawei.ma
 * @date         2011/12/29
 */
int mtvosp_msgq_delete(int i4_msgq_handle)
{
    struct list_head* pt_entry;
    int i4_handle_id = -1;
    MTVOSP_MSG_BUF_T* pt_node = NULL;
    MTVOSP_MSGQ_CTX_T* pt_ctx = NULL;

    i4_handle_id = i4_msgq_handle - MTVOSP_HANDLE_MSGQ_VALUE_START;
    if (i4_handle_id < 0 || i4_handle_id >= MTVOSP_MAX_MSGQ_NUMS)
    {
        MTVOSP_MSGQ_ERR("arg err: i4_msgq_handle=0x%x\n", i4_msgq_handle);
        return -1;
    }

    pt_ctx = _mtvosp_pt_msgq_ctx[i4_handle_id];
    if (!pt_ctx)
    {
        MTVOSP_MSGQ_ERR("arg err: i4_msgq_handle=0x%x, not created!\n", i4_msgq_handle);
        return -1;
    }

    pthread_mutex_lock(&pt_ctx->t_mutex);

    if (pt_ctx->ui4_msg_num)
    {
        MTVOSP_MSGQ_ERR("%d msg is left.\n", pt_ctx->ui4_msg_num);
        while (!mtvosp_list_empty(&pt_ctx->t_head_list))
        {
            pt_entry = mtvosp_list_pull_next(&pt_ctx->t_head_list);
            pt_node = mtvosp_list_entry(pt_entry, MTVOSP_MSG_BUF_T, list);  /*return MTVOSP_MSG_BUF_T* pt_node*/
            _mtvosp_msg_buf_delete(pt_node);
        }

        pt_ctx->ui4_msg_num = 0;
    }
    pthread_mutex_unlock(&pt_ctx->t_mutex);

    pthread_cond_destroy(&pt_ctx->t_cond);
    pthread_mutex_destroy(&pt_ctx->t_mutex);

    free(pt_ctx);
    _mtvosp_pt_msgq_ctx[i4_handle_id] = NULL;

    MTVOSP_MSGQ_DBG("return OK\n");

    return 0;

}


/**
 * mtvosp_msgq_send.
 * send a message data to a message queue
 *
 * @param[in]    i4_msgq_handle   the handle return by mtvosp_msgq_create()
 * @param[in]    pv_msgq_buf       message data buffer
 * @param[in]    i4_msgq_size       message data length
 *
 * @return
 * @retval 0       success
 * @retval -1      fail
 *
 * @see mtvosp_msgq_recv
 * @authors    mawei.ma
 * @date         2011/12/29
 */
int mtvosp_msgq_send(int i4_msgq_handle, void* pv_msgq_buf,  int i4_msgq_size)
{
    int i4_handle_id = -1;
    MTVOSP_MSG_BUF_T* pt_msg_buf = NULL;
    MTVOSP_MSGQ_CTX_T* pt_ctx = NULL;

    if (!pv_msgq_buf || i4_msgq_size <= 0)
    {
        MTVOSP_MSGQ_ERR("arg err: pv_msgq_buf=0x%x i4_msgq_size=%d\n", (unsigned int)pv_msgq_buf, i4_msgq_size);
        return -1;
    }

    i4_handle_id = i4_msgq_handle - MTVOSP_HANDLE_MSGQ_VALUE_START;
    if (i4_handle_id < 0 || i4_handle_id >= MTVOSP_MAX_MSGQ_NUMS)
    {
        MTVOSP_MSGQ_ERR("arg err: i4_msgq_handle=0x%x\n", i4_msgq_handle);
        return -1;
    }

    pt_ctx = _mtvosp_pt_msgq_ctx[i4_handle_id];
    if (!pt_ctx)
    {
        MTVOSP_MSGQ_ERR("arg err: i4_msgq_handle=0x%x, not created!\n", i4_msgq_handle);
        return -1;
    }

    pt_msg_buf = _mtvosp_msg_buf_create(pv_msgq_buf, i4_msgq_size);
    if (!pt_msg_buf)
    {
        return -1;
    }

    pthread_mutex_lock(&pt_ctx->t_mutex);

    /*add this msgq to tail*/
    mtvosp_list_add_tail(&pt_msg_buf->list, &pt_ctx->t_head_list);

    pt_ctx->ui4_msg_num++;

    pthread_cond_signal(&pt_ctx->t_cond);

    pthread_mutex_unlock(&pt_ctx->t_mutex);

    MTVOSP_MSGQ_DBG("send handle=0x%x  msg size: %d\n", i4_msgq_handle, i4_msgq_size);

    return 0;
}


/**
 * mtvosp_msgq_recv.
 * receive a message from a message queue
 *
 * @param[in]          i4_msgq_handle   the handle return by mtvosp_msgq_create()
 * @param[out]        pv_msgq_buf       message data buffer which is stored received message
 * @param[in/out]    pi4_msgq_size     message data length, indicate the length of pv_msgq_buf,out for real message length
 * @param[in]          ui4_timeout_ms   block time in millisecond
 *                                                0 --------------- not block
 *                                               (unsigned int)(-1) -- block until receive a message
 *                                                other value ------- block time at most, in other words, once a message had send by mtvosp_msgq_send,
 *                                                                              this function will return with a received message immediately
 *
 * @return
 * @retval 0       success
 * @retval -1      fail
 *
 * @note if i4_msgq_size is not large enough to store a sent message, some data will be dropped.
 * @see  mtvosp_msgq_send
 * @authors    mawei.ma
 * @date         2011/12/29
 */
int mtvosp_msgq_recv(int i4_msgq_handle, void* pv_msgq_buf,  int* pi4_msgq_size, unsigned int ui4_timeout_ms)
{
    struct list_head* pt_entry;
    int i4_ret = -1;
    int i4_handle_id = -1;
    MTVOSP_MSG_BUF_T* pt_node = NULL;
    MTVOSP_MSGQ_CTX_T* pt_ctx = NULL;

    if (!pv_msgq_buf || !pi4_msgq_size || *pi4_msgq_size <= 0)
    {
        if (pi4_msgq_size)
        {
            MTVOSP_MSGQ_ERR("arg err: pv_msgq_buf=0x%x i4_msgq_size=%d\n", (unsigned int)pv_msgq_buf, *pi4_msgq_size);
        }
        else
        {
            MTVOSP_MSGQ_ERR("arg err: pv_msgq_buf=0x%x pv_msgq_buf=NULL\n", (unsigned int)pv_msgq_buf);
        }
        return i4_ret;
    }

    MTVOSP_MSGQ_DBG("recv handle=0x%x  msg size: %d\n", i4_msgq_handle, *pi4_msgq_size);

    i4_handle_id = i4_msgq_handle - MTVOSP_HANDLE_MSGQ_VALUE_START;
    if (i4_handle_id < 0 || i4_handle_id >= MTVOSP_MAX_MSGQ_NUMS)
    {
        MTVOSP_MSGQ_ERR("arg err: i4_msgq_handle=0x%x\n", i4_msgq_handle);
        return i4_ret;
    }

    pt_ctx = _mtvosp_pt_msgq_ctx[i4_handle_id];
    if (!pt_ctx)
    {
        MTVOSP_MSGQ_ERR("arg err: i4_msgq_handle=0x%x, not created!\n", i4_msgq_handle);
        return i4_ret;
    }

    if (0 == ui4_timeout_ms)  /*nonblock*/
    {
        pthread_mutex_lock(&pt_ctx->t_mutex);

        if (mtvosp_list_empty(&pt_ctx->t_head_list))  /*empty message queue*/
        {
            pthread_mutex_unlock(&pt_ctx->t_mutex);
            *pi4_msgq_size = 0;
            return -2;
        }

        pt_ctx->ui4_msg_num--;

        pt_entry = mtvosp_list_pull_next(&pt_ctx->t_head_list);

        pthread_mutex_unlock(&pt_ctx->t_mutex);

        pt_node = mtvosp_list_entry(pt_entry, MTVOSP_MSG_BUF_T, list);  /*return MTVOSP_MSG_BUF_T* pt_node*/

        if (pt_node->pv_data_buf)
        {
            *pi4_msgq_size = MTVOSP_MIN_AT_MSGQ(*pi4_msgq_size, pt_node->i4_data_size);
            memcpy(pv_msgq_buf, pt_node->pv_data_buf, *pi4_msgq_size);
            i4_ret = 0;
        }
        else
        {
            MTVOSP_MSGQ_ERR("fatal error here: pv_data_buf=NULL.\n");
            *pi4_msgq_size = 0;
        }

        _mtvosp_msg_buf_delete(pt_node);

        MTVOSP_MSGQ_DBG("return %d\n", i4_ret);

        return i4_ret;
    }
    else if (((unsigned int)(-1)) == ui4_timeout_ms) /*block*/
    {
        pthread_mutex_lock(&pt_ctx->t_mutex);

        if (!mtvosp_list_empty(&pt_ctx->t_head_list))  /*not empty message queue*/
        {
            pt_ctx->ui4_msg_num--;
            pt_entry = mtvosp_list_pull_next(&pt_ctx->t_head_list);
            pthread_mutex_unlock(&pt_ctx->t_mutex);

            pt_node = mtvosp_list_entry(pt_entry, MTVOSP_MSG_BUF_T, list);  /*return MTVOSP_MSG_BUF_T* pt_node*/

            if (pt_node->pv_data_buf)
            {
                *pi4_msgq_size = MTVOSP_MIN_AT_MSGQ(*pi4_msgq_size, pt_node->i4_data_size);
                memcpy(pv_msgq_buf, pt_node->pv_data_buf, *pi4_msgq_size);
                i4_ret = 0;
            }
            else
            {
                MTVOSP_MSGQ_ERR("fatal error here: pv_data_buf=NULL.\n");
                *pi4_msgq_size = 0;
            }

            _mtvosp_msg_buf_delete(pt_node);

            return i4_ret;
        }

        while (0 == pt_ctx->ui4_msg_num)
        {
            pthread_cond_wait(&pt_ctx->t_cond, &pt_ctx->t_mutex);
        }

        pt_ctx->ui4_msg_num--;
        pt_entry = mtvosp_list_pull_next(&pt_ctx->t_head_list);
        pthread_mutex_unlock(&pt_ctx->t_mutex);

        pt_node = mtvosp_list_entry(pt_entry, MTVOSP_MSG_BUF_T, list);  /*return MTVOSP_MSG_BUF_T* pt_node*/

        if (pt_node->pv_data_buf)
        {
            *pi4_msgq_size = MTVOSP_MIN_AT_MSGQ(*pi4_msgq_size, pt_node->i4_data_size);
            memcpy(pv_msgq_buf, pt_node->pv_data_buf, *pi4_msgq_size);
            i4_ret = 0;
        }
        else
        {
            MTVOSP_MSGQ_ERR("fatal error here: pv_data_buf=NULL.\n");
            *pi4_msgq_size = 0;
        }

        _mtvosp_msg_buf_delete(pt_node);

        MTVOSP_MSGQ_DBG("return %d\n", i4_ret);

        return i4_ret;
    }
    else  /*block ui4_timeout_ms ms at most*/
    {
        struct timeval now;
        struct timezone tz;
        struct timespec timeout;
        int retcode = 0;

        pthread_mutex_lock(&pt_ctx->t_mutex);

        if (!mtvosp_list_empty(&pt_ctx->t_head_list))  /*not empty message queue*/
        {
            pt_ctx->ui4_msg_num--;
            pt_entry = mtvosp_list_pull_next(&pt_ctx->t_head_list);
            pthread_mutex_unlock(&pt_ctx->t_mutex);

            pt_node = mtvosp_list_entry(pt_entry, MTVOSP_MSG_BUF_T, list);  /*return MTVOSP_MSG_BUF_T* pt_node*/

            if (pt_node->pv_data_buf)
            {
                *pi4_msgq_size = MTVOSP_MIN_AT_MSGQ(*pi4_msgq_size, pt_node->i4_data_size);
                memcpy(pv_msgq_buf, pt_node->pv_data_buf, *pi4_msgq_size);
                i4_ret = 0;
            }
            else
            {
                MTVOSP_MSGQ_ERR("fatal error here: pv_data_buf=NULL.\n");
                *pi4_msgq_size = 0;
            }

            _mtvosp_msg_buf_delete(pt_node);

            return i4_ret;
        }

        gettimeofday(&now, &tz);
        timeout.tv_sec = now.tv_sec + (ui4_timeout_ms / 1000);
        timeout.tv_nsec = now.tv_usec * 1000 + (ui4_timeout_ms % 1000) * 1000000;  /*10E-9*/

        while (0 == pt_ctx->ui4_msg_num && retcode != ETIMEDOUT)
        {
            retcode = pthread_cond_timedwait(&pt_ctx->t_cond, &pt_ctx->t_mutex, &timeout);
        }

        if (retcode == ETIMEDOUT)
        {
            /* timeout occurred */
            pthread_mutex_unlock(&pt_ctx->t_mutex);
            MTVOSP_MSGQ_ERR("no msg get:TIMEDOUT!!\n");
            return -2;
        }

        /*wait a msg now*/
        pt_ctx->ui4_msg_num--;
        pt_entry = mtvosp_list_pull_next(&pt_ctx->t_head_list);
        pthread_mutex_unlock(&pt_ctx->t_mutex);

        pt_node = mtvosp_list_entry(pt_entry, MTVOSP_MSG_BUF_T, list);  /*return MTVOSP_MSG_BUF_T* pt_node*/

        if (pt_node->pv_data_buf)
        {
            *pi4_msgq_size = MTVOSP_MIN_AT_MSGQ(*pi4_msgq_size, pt_node->i4_data_size);
            memcpy(pv_msgq_buf, pt_node->pv_data_buf, *pi4_msgq_size);
            i4_ret = 0;
        }
        else
        {
            MTVOSP_MSGQ_ERR("fatal error here: pv_data_buf=NULL.\n");
            *pi4_msgq_size = 0;
        }

        _mtvosp_msg_buf_delete(pt_node);

        MTVOSP_MSGQ_DBG("return %d\n", i4_ret);

        return i4_ret;
    }

}


/******************************************************************************
 *                                 Local Functions                            *
 ******************************************************************************/
MTVOSP_MSG_BUF_T* _mtvosp_msg_buf_create(void* pv_msgq_buf, int i4_msgq_size)
{
    void* pv_data_buf = NULL;

    MTVOSP_MSG_BUF_T* pt_msg_buf = NULL;

    pv_data_buf = malloc(i4_msgq_size);
    if (!pv_data_buf)
    {
        MTVOSP_MSGQ_ERR("Error: out of memory for %d\n", i4_msgq_size);
        return NULL;
    }

    pt_msg_buf = (MTVOSP_MSG_BUF_T*)malloc(sizeof(MTVOSP_MSG_BUF_T));
    if (!pt_msg_buf)
    {
        MTVOSP_MSGQ_ERR("Error: out of memory for %d\n", sizeof(MTVOSP_MSG_BUF_T));
        free(pv_data_buf);
        return NULL;
    }

    if (pv_msgq_buf)
    {
        memcpy(pv_data_buf, pv_msgq_buf, i4_msgq_size);
    }

    pt_msg_buf->pv_data_buf = pv_data_buf;
    pt_msg_buf->i4_data_size = i4_msgq_size;
    INIT_LIST_HEAD(&pt_msg_buf->list);

    return pt_msg_buf;
}

int _mtvosp_msg_buf_delete(MTVOSP_MSG_BUF_T* pt_msg_buf)
{
    if (pt_msg_buf)
    {
        if (pt_msg_buf->pv_data_buf)
        {
            free(pt_msg_buf->pv_data_buf);
        }
        else
        {
            MTVOSP_MSGQ_ERR("maybe Error: pv_data_buf=NULL\n");
        }

        free(pt_msg_buf);
        return 0;
    }

    return -1;
}

/* EOF */

