#include "WfdRtspCmdSender.h"
#include <stdio.h>
#include "WfdRtspClient.h"

namespace rtsp
{
WfdRtspClient* WfdRtspReqSender::client = NULL;
Authenticator* WfdRtspReqSender::auth = NULL;
// -------------------------- WfdRtspSenderInit --------------------------
WfdRtspSenderInit::WfdRtspSenderInit(WfdRtspClient* _client, Authenticator* _auth)
{
    client = _client;
    auth = _auth;
}

WfdRtspSenderInit::~WfdRtspSenderInit()
{
}

bool WfdRtspSenderInit::sendRequest()
{
    printf("WfdRtspSenderInit::sendRequest \n");

    return true;
}

// -------------------------- WfdRtspCmdSenderDecorator --------------------------
WfdRtspCmdSenderDecorator::WfdRtspCmdSenderDecorator(WfdRtspReqSender & sender)
{
	pWfdRtspReqSender = NULL;
	pWfdRtspReqSender = &sender;
	type = WFDRTSP_SENDTYPE_UNKNOWN;
	rspHandler = NULL;		
}

bool WfdRtspCmdSenderDecorator::callNext()
{
	if (NULL != pWfdRtspReqSender)
		return pWfdRtspReqSender->sendRequest();
	return false;
}

void WfdRtspCmdSenderDecorator::setRtspCustomData()
{
#ifdef	ENABLE_MTK_RTSP	
	if (false != selfData.empty())
	{
		client->setCustomData(selfData);
	}
#endif
}

// -------------------------- WfdRtspConnectionSender --------------------------
WfdRtspConnectionSender::WfdRtspConnectionSender(WfdRtspReqSender& sender):WfdRtspCmdSenderDecorator(sender)
{
}

WfdRtspConnectionSender::~WfdRtspConnectionSender()
{
}

bool WfdRtspConnectionSender::sendRequest()
{
    printf("WfdRtspConnectionSender::sendRequest \n");

    return true;
}


// -------------------------- WfdRtspOptionSender --------------------------
WfdRtspOptionSender::WfdRtspOptionSender(WfdRtspReqSender & sender):WfdRtspCmdSenderDecorator(sender)
{
}

WfdRtspOptionSender::~WfdRtspOptionSender()
{
}

bool WfdRtspOptionSender::sendRequest()
{
    printf("WfdRtspOptionSender::sendRequest \n");
    return true;
}

// -------------------------- WfdRtspSetupSender --------------------------
WfdRtspSetupSender::WfdRtspSetupSender(WfdRtspReqSender & sender):WfdRtspCmdSenderDecorator(sender)
{
}

WfdRtspSetupSender::~WfdRtspSetupSender()
{
}

bool WfdRtspSetupSender::sendRequest()
{    
    printf("WfdRtspSetupSender::sendRequest \n");
    return true;
}

// -------------------------- WfdRtspPlaySender --------------------------
WfdRtspPlaySender::WfdRtspPlaySender(WfdRtspReqSender & sender):WfdRtspCmdSenderDecorator(sender)
{
}

WfdRtspPlaySender::~WfdRtspPlaySender()
{
}

bool WfdRtspPlaySender::sendRequest()
{
    printf("WfdRtspPlaySender::sendRequest \n");
    return true;
}

// -------------------------- WfdRtspTeardownSender --------------------------
WfdRtspTeardownSender::WfdRtspTeardownSender(WfdRtspReqSender & sender):WfdRtspCmdSenderDecorator(sender)
{
}

WfdRtspTeardownSender::~WfdRtspTeardownSender()
{
}

bool WfdRtspTeardownSender::sendRequest()
{
    printf("WfdRtspTeardownSender::sendRequest \n");
    return true;
}


}
