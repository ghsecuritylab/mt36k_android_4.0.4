#ifndef _LOG_HTTPLS_H_
#define _LOG_HTTPLS_H_

#include <stdio.h>
static char * httplslog = (char *)"/tmp/httpls.log";
static bool log_to_file = false;
static FILE * g_fp = NULL;

#define START_LOG_TO_FILE()\
    {\
        g_fp = fopen(httplslog, "a+");\
        log_to_file = true;\
    }\

#define STOP_LOG_TO_FILE()\
    {\
        fclose(g_fp);\
        log_to_file =false;\
    }\

#ifndef HLS_SELF_TEST
#ifndef ANDROID_DEBUG_LOG
#define ANDROID_DEBUG_LOG
#endif




#define NORMAL  stderr
#define LOG_LEV 0

#ifdef ANDROID_DEBUG_LOG
#include <android/log.h>

#define NORMAL  "Playlist"


#define LOG(lev, fmt...)\
    {\
        if (log_to_file && g_fp)\
        {\
            fprintf(g_fp, "[%s][%d]-------", __FUNCTION__, __LINE__);\
            fprintf(g_fp, fmt);\
            fprintf(g_fp, "\n");\
        }\
        else if (lev <= LOG_LEV)\
        {\
            __android_log_print(ANDROID_LOG_DEBUG, NORMAL, "[%s][%d]-------", __FUNCTION__, __LINE__);\
            __android_log_print(ANDROID_LOG_DEBUG, NORMAL, fmt);\
            __android_log_print(ANDROID_LOG_DEBUG, NORMAL, "\n");\
        }\
    }
#else
#include <stdio.h>

#define NORMAL  stderr

#define LOG(lev, fmt...)\
    {\
        if (log_to_file && g_fp)\
        {\
            fprintf(g_fp, "[%s][%d]-------", __FUNCTION__, __LINE__);\
            fprintf(g_fp, fmt);\
            fprintf(g_fp, "\n");\
        }\
        else if (lev <= LOG_LEV)\
        {\
            fprintf(NORMAL, "[%s][%d]-------", __FUNCTION__, __LINE__);\
            fprintf(NORMAL, fmt);\
            fprintf(NORMAL, "\n");\
        }\
    }
#endif
#else
#include <stdio.h>

#define NORMAL  "Playlist"
extern int log_level;
#define LOG(lev, fmt...)\
    {\
        if (lev <= log_level)\
        {\
            fprintf(stderr, "[%s][%d]-------", __FUNCTION__, __LINE__);\
            fprintf(stderr, fmt);\
            fprintf(stderr, "\n");\
        }\
    }

#endif
#endif
