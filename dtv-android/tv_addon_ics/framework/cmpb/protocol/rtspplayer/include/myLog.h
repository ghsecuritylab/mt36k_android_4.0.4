#ifndef _MY_LOG_H_
#define _MY_LOG_H_

#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <unistd.h>
#include <utils/Log.h>
namespace rtsp
{
#define UNUSED(x)   (void)(x)
extern bool b_debug_log;
//#define LOG_DEBUG  LOGI
//#define LOG_ERR    LOGE
#ifndef RTSP_SELF_TEST
#define LOG_DEBUG(args...) \
    { \
        if (b_debug_log) \
        { \
            LOGI(args); \
        } \
    }
#define LOG_ERR(args...) \
    { \
        if (b_debug_log) \
        { \
            LOGE(args); \
        } \
    }

#else
#define LOG_DEBUG(args...) \
    { \
        if (b_debug_log) \
        { \
            fprintf(stdout, "[%s:%s:%d]:: ", __FILE__, __FUNCTION__, __LINE__); \
            fprintf(stdout,args); \
            fprintf(stdout,"\n"); \
            sync(); \
        } \
    }
#define LOG_ERR(args...) \
    { \
         fprintf(stderr, "[%s:%s:%d]:: ", __FILE__, __FUNCTION__, __LINE__); \
         fprintf(stderr,args); \
         fprintf(stderr,"\n"); \
         sync(); \
    }
#endif

void setLog(bool flag);

}
#endif
