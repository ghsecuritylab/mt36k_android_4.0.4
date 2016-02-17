#ifndef ANDROID_BPCALSERVICE_H
#define ANDROID_BPCALSERVICE_H

#include <utils/RefBase.h>
#include <binder/IInterface.h>
#include <binder/Parcel.h>
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


namespace android
{
    class BpSndCmdService
    {
        public:
            bool SndCmd(const char *cmd);
           
        private:
            static const void getSndCmdService();
    };
};
#endif

