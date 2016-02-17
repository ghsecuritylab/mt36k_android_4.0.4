/* //device/libs/android_runtime/android_os_Power.cpp
**
** Copyright 2006, The Android Open Source Project
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

#include "JNIHelp.h"
#include "jni.h"
#include "android_runtime/AndroidRuntime.h"
#include <utils/misc.h>
#include <hardware_legacy/power.h>
#include <cutils/android_reboot.h>
#include <cutils/properties.h>
#include <signal.h>
#include <dirent.h>
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <fcntl.h>
#include <string.h>

namespace android
{

static void
acquireWakeLock(JNIEnv *env, jobject clazz, jint lock, jstring idObj)
{
    if (idObj == NULL) {
        jniThrowNullPointerException(env, "id is null");
        return ;
    }

    const char *id = env->GetStringUTFChars(idObj, NULL);

    acquire_wake_lock(lock, id);

    env->ReleaseStringUTFChars(idObj, id);
}

static void
releaseWakeLock(JNIEnv *env, jobject clazz, jstring idObj)
{
    if (idObj == NULL) {
        jniThrowNullPointerException(env, "id is null");
        return ;
    }

    const char *id = env->GetStringUTFChars(idObj, NULL);

    release_wake_lock(id);

    env->ReleaseStringUTFChars(idObj, id);

}

static int
setLastUserActivityTimeout(JNIEnv *env, jobject clazz, jlong timeMS)
{
    return set_last_user_activity_timeout(timeMS/1000);
}

static int
setScreenState(JNIEnv *env, jobject clazz, jboolean on)
{
    return set_screen_state(on);
}

#if 1	//liufeng_110717
extern "C" int c_pcl_set_power_down(void);

#endif

#if 1	//liufeng_110615
//#include "mtk_dtv_svc.h"
#ifdef __cplusplus
extern "C"
{
#endif

typedef void (*_app_tv_svc_status_nfy) (const char *msg,
                                       int         status,
                                       int         data1,
                                       int         data2);


extern int _tv_svc_init_client (_app_tv_svc_status_nfy pf_nfy);

#ifdef __cplusplus
}
#endif

extern "C" void _pf_jni_tv_svc_status (const char    *msg,
                       int           status,
                       int           data1,
                       int           data2)
{
    return;
}
#endif

static void android_os_Power_shutdown(JNIEnv *env, jobject clazz)
{
    LOGE("~~~~~BEFORE KILL LOGCAT~~~~~");

    DIR *d;
    struct dirent *de;
    d = opendir("/proc");
    if (d != 0)
    {
        while((de = readdir(d)) != 0)
        {
            if(isdigit(de->d_name[0]))
            {
                int pid = atoi(de->d_name);
                char cmdline[1024];
                sprintf(cmdline, "/proc/%d/cmdline", pid);
                int fd = open(cmdline, O_RDONLY);
                if (fd != 0)
                {
                    int r = read(fd, cmdline, 1023);
                    if (r <= 0 || strstr(cmdline, "logcat") == 0)
                    {
                        pid = -1;
                    }
                    close(fd);
                }
                if (pid >= 0)
                {
                    kill(pid, SIGTERM);
                }
            }
        }
        closedir(d);
    }

    LOGE("~~~~~AFTER KILL LOGCAT~~~~~");
	
    sync();
#if 1	//liufeng_110717
		_tv_svc_init_client(_pf_jni_tv_svc_status);
		/*register */
		//LOG(("Register JNI Class\r\n"));
		LOGD("------rpc init--ok-------");
#endif

#if 0 //add by kangjp for shutdown immediately
    LOGD("try to set prop");
    //system("setprop sys.unmount 1");
    property_set("sys.unmount", "1");
    
    for(int i = 0;i < 5; i++)
    {
        FILE * file = fopen("/done", "r");
        if (!file) {
            LOGD("unmount finish not yet");
            sleep(1);
        } else {
            LOGD("unmount finish!!");
            fclose(file);
            break;
        }
    }
#endif

#if 1	//liufeng_110717
	LOGD("----android_os_Power_shutdown---call mw------haha------ok----");

	c_pcl_set_power_down();
#else
	
#ifdef HAVE_ANDROID_OS
    android_reboot(ANDROID_RB_POWEROFF, 0, 0);
#endif
#endif
}

static void android_os_Power_reboot(JNIEnv *env, jobject clazz, jstring reason)
{
    if (reason == NULL) {
        android_reboot(ANDROID_RB_RESTART, 0, 0);
    } else {
        const char *chars = env->GetStringUTFChars(reason, NULL);
        android_reboot(ANDROID_RB_RESTART2, 0, (char *) chars);
        env->ReleaseStringUTFChars(reason, chars);  // In case it fails.
    }
    jniThrowIOException(env, errno);
}

static JNINativeMethod method_table[] = {
    { "acquireWakeLock", "(ILjava/lang/String;)V", (void*)acquireWakeLock },
    { "releaseWakeLock", "(Ljava/lang/String;)V", (void*)releaseWakeLock },
    { "setLastUserActivityTimeout", "(J)I", (void*)setLastUserActivityTimeout },
    { "setScreenState", "(Z)I", (void*)setScreenState },
    { "shutdown", "()V", (void*)android_os_Power_shutdown },
    { "rebootNative", "(Ljava/lang/String;)V", (void*)android_os_Power_reboot },
};

int register_android_os_Power(JNIEnv *env)
{
    return AndroidRuntime::registerNativeMethods(
        env, "android/os/Power",
        method_table, NELEM(method_table));
}

};
