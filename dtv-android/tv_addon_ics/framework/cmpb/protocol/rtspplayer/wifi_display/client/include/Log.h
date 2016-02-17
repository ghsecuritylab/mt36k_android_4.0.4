#ifndef _LOG_H_
#define _LOG_H_

#include <stdio.h>

#define NORMAL  stderr
#define LOG_ERR 0
#define LOG_INF 1
#define LOG_LEV LOG_INF
            // fprintf(NORMAL, "[%s][%d]-------", __FUNCTION__, __LINE__);
#define LOG(lev, fmt...)\
    {\
        if (lev <= LOG_LEV)\
        {\
            fprintf(NORMAL, fmt);\
            fprintf(NORMAL, "\n");\
        }\
    }

#endif
