/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <sys/stat.h>
#include <sys/types.h>
#include <fcntl.h>
#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>

#include <cutils/klog.h>

static int klog_fd = -1;
static int klog_level = KLOG_DEFAULT_LEVEL;

void klog_set_level(int level) {
    klog_level = level;
}

void klog_init(void)
{
    static const char *name = "/dev/__kmsg__";
    if (mknod(name, S_IFCHR | 0600, (1 << 8) | 11) == 0) {
        klog_fd = open(name, O_WRONLY);
        fcntl(klog_fd, F_SETFD, FD_CLOEXEC);
        unlink(name);
    }
}

#ifdef INIT_PROFILING
static int
unix_read(int  fd, void*  buff, int  len)
{
    int  ret;
    do { ret = read(fd, buff, len); } while (ret < 0 && errno == EINTR);
    return ret;
}

int get_uptime()
{
    int   fd, ret, len;
    static char buff[65];
	int ten_ms=0;

    fd = open("/proc/uptime",O_RDONLY);
    if (fd >= 0) {
        int  ret;
        ret = unix_read(fd, buff, 64);
        close(fd);
        buff[64] = 0;
        if (ret >= 0) {
            ten_ms = 100*strtod(buff,NULL);
        }
    }
    return ten_ms;
} 
#endif /* INIT_PROFILING */

#define LOG_BUF_MAX 512

void klog_write(int level, const char *fmt, ...)
{
    char buf[LOG_BUF_MAX];
    char *buf_ptr = buf;
    va_list ap;

    if ((level != KLOG_PROFLIE_LEVEL) && (level > klog_level)) return;
    if (klog_fd < 0) return;

    buf[0] = '\0';
    
#ifdef INIT_PROFILING
	static int last_uptime=0;
	int uptime = get_uptime();
	int dtime = uptime - last_uptime;
	sprintf(buf, "<%d>[%04d] [%04d] init: ", level, dtime, uptime);
	last_uptime = uptime;
#endif /* INIT_PROFILING */

    buf_ptr += strlen(buf);

    va_start(ap, fmt);
    vsnprintf(buf_ptr, LOG_BUF_MAX - strlen(buf), fmt, ap);
    buf[LOG_BUF_MAX - 1] = 0;
    va_end(ap);
    write(klog_fd, buf, strlen(buf));
}
