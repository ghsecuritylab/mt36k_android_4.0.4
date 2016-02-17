#define LOG_TAG "Zygote"
#include <utils/Log.h>
#include <sys/stat.h>
#include "JNIHelp.h"
#include <android_runtime/AndroidRuntime.h>

#ifdef HAVE_BLCR
#include <libcr.h>
#include <crut_util.h>

#define _REALLY_INCLUDE_SYS__SYSTEM_PROPERTIES_H_
#include <sys/_system_properties.h>
#include <sys/mman.h>

#include <sys/socket.h>
#include <sys/un.h>
#include <sys/select.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <unistd.h>
#include <sys/atomics.h>
#endif

namespace android
{
#ifdef HAVE_BLCR
    static int my_callback(void* arg)
    {
        cr_checkpoint(0);
        return 0;
    }

    static void checkPoint(JNIEnv* env, jobject object, jstring file)
    {
        pid_t my_pid;
        int rc, fd;
        struct stat s;
        cr_checkpoint_handle_t my_handle;
        cr_callback_id_t cb_id;
        FILE* f;
        char filename[256] = { 0 };
        char filename2[256] = { 0 };

        jstring encode = env->NewStringUTF("utf-8");
        jmethodID methodId = env->GetMethodID(env->FindClass("java/lang/String"), "getBytes", "(Ljava/lang/String;)[B");
        jbyteArray byteArray = (jbyteArray)env->CallObjectMethod(file, methodId, encode);
        jbyte* byteFilename = env->GetByteArrayElements(byteArray, JNI_FALSE);
        if (byteFilename)
        {
            memcpy(filename, (char*)byteFilename, 256);
            memcpy(filename2, filename, 256);
            int l = strlen(filename2);
            memcpy(filename2 + l, ".2", 256-l);
        }
        else
        {
            LOGE("checkPoint file name is null");
            return;
        }
        env->ReleaseByteArrayElements(byteArray, byteFilename, 0);

        f = fopen(filename, "r");
        if (f == NULL)
        {
            my_pid = cr_init();
            if (my_pid < 0)
            {
                LOGE("cr_init failed, return %d", my_pid);
                return;
            }

            cb_id = cr_register_callback(my_callback, NULL, CR_SIGNAL_CONTEXT);
            if (cb_id < 0)
            {
                LOGE("cr_register_callback unexpectedly returned %d", cb_id);
                return;
            }
            else
            {
                LOGI("cr_register_callback correctly returned %d", cb_id);
            }

            // request a checkpoint of ourself
            fd = crut_checkpoint_request(&my_handle, filename);

            // delete the property file here, because BLCR will corrupt the file handle
            //unlink("/dev/__properties__");

            FILE* f2 = fopen(filename2, "r");
            if (f2)
            {
                __android_log_reinit_log_handle();
                LOGI("start zygote by cr_restart");
                fclose(f2);
                return;
            }
            else
            {
                f2 = fopen(filename2, "w");
                fprintf(f2, "do not remove this file!!!");
                fclose(f2);
                LOGI("start zygote normally");
            }
            if (fd < 0)
            {
                LOGE("crut_checkpoint_request unexpectedly returned 0x%x", fd);
                return;
            }

            rc = stat(filename, &s);
            if (rc != 0)
            {
                LOGE("stat unexpectedly returned %d", rc);
                return;
            }
            else
            {
                LOGI("stat(context %d) correctly returned 0", my_pid);
            }

            if (s.st_size == 0)
            {
                LOGE("context file unexpectedly empty");
                return;
            }
            else
            {
                LOGI("context %d is non-empty", my_pid);
            }

            // reap the checkpoint request
            rc = crut_checkpoint_wait(&my_handle, fd);
            if (rc < 0)
            {
                LOGE("crut_checkpoint_wait unexpectedly returned 0x%x", rc);
                return;
            }
            LOGI("Generate checkpoint file successfully");
        }
        else
        {
            fclose(f);
            LOGI("checkPoint file already exists");
        }
    }
#else
    static void checkPoint(JNIEnv* env, jobject object, jstring file)
    {
        // delete the property file here, it should be originally deleted in init.
        unlink("/dev/__properties__");
    }
#endif

    //JNI registration
    static JNINativeMethod gMethods[] =
    {
        // name, signature, funcPrt
        { "checkPoint", "(Ljava/lang/String;)V", (void*)checkPoint },
    };
    
    int register_android_blcr_checkpoint(JNIEnv* env)
    {
        return jniRegisterNativeMethods(env, "android/blcr/CheckPoint", gMethods, NELEM(gMethods));
    }
}
