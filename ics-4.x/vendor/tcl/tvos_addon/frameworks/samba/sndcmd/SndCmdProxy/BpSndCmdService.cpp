/***************************************************************************
*
*	Author : liukun
*	Date : 2011-7-11
*	Description : 
*
***************************************************************************/



#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>

#include "../include/SndCmdService.h"
#include "../include/BpSndCmdService.h"
#include <utils/Log.h>



namespace android
{
    sp<IBinder> binder;
    bool BpSndCmdService::SndCmd(const char *cmd)
    {
        getSndCmdService();
        Parcel data, reply;

        data.writeCString(cmd);

        LOGD("BpSndCmdService::create remote()->transact() : cmd = %s\n",cmd);
        binder->transact(SndCmdService::SNDCMD, data, &reply);
        int result = reply.readInt32();

		if(result)
			return true;
		else
			return false;
    }
   

    const void BpSndCmdService::getSndCmdService()
    {
        sp<IServiceManager> sm = defaultServiceManager();
        binder = sm->getService(String16("SndCmdService"));

        if(binder == 0 )
        {
            LOGD("SndCmdService not published, getSndCmdService Fail");
            return ;
        }
    }
};

