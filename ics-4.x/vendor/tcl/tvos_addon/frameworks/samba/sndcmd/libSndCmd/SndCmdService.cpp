
/***************************************************************************
*
*	Author : liukun
*	Date : 2011-7-11
*	Description : 
*
***************************************************************************/


#include "../include/SndCmdService.h"

#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include <stdlib.h>
#include <stdio.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


namespace android {

int SndCmdService::instantiate(){
	
 	FILE *fp;
	char fpath[64] = "/data/myerrinfo";
	char logpath[64] = "/data/loginfo";
	
    int ret = defaultServiceManager()->addService(
            String16("SndCmdService"), new SndCmdService());

	LOGD("SndCmdService instantiate ---> return:%d", ret);

	if(!(fp = fopen(fpath,"w")))
	{
		LOGE("SndCmdService : open file %s failed\n",fpath);
	}

	fclose(fp);

	if(!(fp = fopen(logpath,"w")))
	{
		LOGE("SndCmdService : open file %s failed\n",logpath);
	}

	fclose(fp);
	
	return ret;
}

SndCmdService::SndCmdService(){
     LOGD("SndCmdService created");
}

SndCmdService::~SndCmdService(){
	LOGD("CalService destroyed");
}

int SndCmdService::processCmd(const char *cmd){
	char fpath[64] = "/data/myerrinfo";
	char logpath[64] = "/data/loginfo";
	
	FILE *fp;
	char cmdbuf[1024];

	sprintf(cmdbuf,"%s > %s 2> %s",cmd,logpath,fpath);

	if(-1 == system(cmdbuf))
	{
		LOGE("processCmd : system call failed!");
		return 0;
	}

	if(!(fp = fopen(fpath,"r")))
	{
		LOGE("processCmd: open file %s failed\n",fpath);
		return 0;
	}

	if(!fgets(cmdbuf,sizeof(cmdbuf),fp))//cmd successed
	{
	   LOGI("processCmd ----->cmd successed\n");
	   fclose(fp);
	   return 1;
	}
	else//fail
	{
		LOGI("processCmd ----->cmd fail :\nError: %s\n", cmdbuf);	
		fclose(fp);
		return 0;
	}
}
status_t SndCmdService::onTransact(
        uint32_t code, const Parcel& data, Parcel* reply, uint32_t flags){

	switch(code)
	{
		case SNDCMD:
		{
			int res;
			const char* cmd = data.readCString();
			LOGD("SndCmdService onTransact:SNDCMD");
			res = processCmd(cmd);				
			reply->writeInt32(res);
			return NO_ERROR;
		}
	}

	return BBinder::onTransact(code, data, reply, flags);


}


};

