#ifndef ANDROID_SERVERS_CALC_CALSERVICE_H
#define ANDROID_SERVERS_CALC_CALSERVICE_H

#include <utils/RefBase.h>
#include <binder/IInterface.h>
#include <binder/Parcel.h>
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

namespace android {

class SndCmdService : public BBinder{

public:
	enum{
		SNDCMD,
		};
	static int instantiate();
	SndCmdService();
    virtual ~SndCmdService();
	int processCmd(const char *cmd);
	virtual status_t onTransact(
        uint32_t code, const Parcel& data, Parcel* reply, uint32_t flags);
};

};

#endif
