#include "RtspCmdSender.h"
#include "myLog.h"
#include "MtkRtspClient.h"
#include "RTSPClient.hh"
#include "BasicUsageEnvironment.hh"
#include "DigestAuthentication.hh"


namespace rtsp
{
#define CHECK_NULL_COND(condition, ret) \
	if (NULL == (condition)) \
	{ \
		LOG_ERR("error!"); \
		return (ret); \
	}
		

MtkRTSPClient* RtspReqSender::client= NULL;
Authenticator* RtspReqSender::auth = NULL;

SenderInit::SenderInit(MtkRTSPClient* _client, Authenticator* _auth)
{
	client = _client;
	auth = _auth;
}

SenderInit::~SenderInit()
{
    std::list<CmdSenderDecorator*>::iterator sender;
    for (sender = cmdList.begin(); sender != cmdList.end(); sender++)
        if (*sender != NULL)
            delete *sender;
}


bool SenderInit::sendRequest()
{
	//do nothing
	return true;
}

void SenderInit::RecordSender(CmdSenderDecorator *sender)
{
    if (sender != NULL)
        cmdList.push_back(sender);
}


CmdSenderDecorator::CmdSenderDecorator(RtspReqSender & sender)
{
	pRtspReqSender = NULL;
	pRtspReqSender = &sender;
	type = SENDERTYPE_UNKNOWN;
	rspHandler = NULL;		
}

bool CmdSenderDecorator::callNext()
{
	if (NULL != pRtspReqSender)
		return pRtspReqSender->sendRequest();
	return false;
}
void CmdSenderDecorator::setRtspCustomData()
{
#ifdef	ENABLE_MTK_RTSP	
	if (false != selfData.empty())
	{
		client->setCustomData(selfData);
	}
#endif
}

OptionSender::OptionSender(RtspReqSender & sender): CmdSenderDecorator(sender)
{
	type = SENDERTYPE_OPTINON;
}

bool OptionSender::sendRequest()
{
	LOG_DEBUG("Send rtsp option request!");
	CHECK_NULL_COND(client, false);

	setRtspCustomData();
	client->setCurrentSender(this);
	
	/*
	 *NOTE:sendOptionsCommand maybe failed for example the request is pending,
	 *so just return success, and if error occur, the response handle will deal it
	 */
	client->sendOptionsCommand(rspHandler, auth);

	return true;
}

DspSender::DspSender(RtspReqSender & sender):CmdSenderDecorator(sender)
{
	type = SENDERTYPE_DESCRIPTION;
}

bool DspSender::sendRequest()
{
	LOG_DEBUG("Send rtsp describe request!");
	CHECK_NULL_COND(client, false);

	setRtspCustomData();
	
	client->setCurrentSender(this);
	client->sendDescribeCommand(rspHandler, auth);

	return true;
}

PlaySender::PlaySender(RtspReqSender & sender):CmdSenderDecorator(sender), start(0.0f), end(-1.0f), scale(1.0f)
{
	type = SENDERTYPE_PLAY;
}

bool PlaySender::sendRequest()
{
	LOG_DEBUG("Send rtsp play request!");
	CHECK_NULL_COND(client, false);

	setRtspCustomData();
	
	MediaSession * session = client->getMediaSession();
	CHECK_NULL_COND(session, false);

	client->setCurrentSender(this);
	client->sendPlayCommand(*session, rspHandler, start, end, scale, auth);
	
	return true;
}

void PlaySender::setRange(double _start, double _end, float _scale)
{
	start = _start;
	end = _end;
	scale = _scale;
}


PauseSender::PauseSender(RtspReqSender & sender):CmdSenderDecorator(sender)
{
	type = SENDERTYPE_PAUSE;
}

bool PauseSender::sendRequest()
{
	LOG_DEBUG("Send rtsp pause request!");
	CHECK_NULL_COND(client, false);

	setRtspCustomData();

	MediaSession * session = client->getMediaSession();
	CHECK_NULL_COND(session, false);

	client->setCurrentSender(this);
	client->sendPauseCommand(*session, rspHandler, auth);

	return true;
}

TeardownSender::TeardownSender(RtspReqSender & sender):CmdSenderDecorator(sender)
{
	type = SENDERTYPE_TEARDOWN;
}

bool TeardownSender::sendRequest()
{
	LOG_DEBUG("Send rtsp teardown request!");
	CHECK_NULL_COND(client, false);

	setRtspCustomData();

	MediaSession * session = client->getMediaSession();
	CHECK_NULL_COND(session, false);

	client->setCurrentSender(this);
	client->sendTeardownCommand(*session, rspHandler, auth);

	return true;
}

SetupSender::SetupSender(RtspReqSender & sender):CmdSenderDecorator(sender), subSession(NULL), 
													streamOutgoing(false), streamUsingTCP(false),
														forceMulticastOnUnspecified(false)
														
													
{
	type = SENDERTYPE_SETUP;
}

bool SetupSender::sendRequest()
{
	LOG_DEBUG("Send rtsp setup request!");
	CHECK_NULL_COND(client, false);

	setRtspCustomData();

	CHECK_NULL_COND(subSession, false);

	client->setCurrentSender(this);
	client->sendSetupCommand(*subSession, rspHandler, streamOutgoing, 
												streamUsingTCP, forceMulticastOnUnspecified, auth);

	return true;
}

void SetupSender::setParam(bool _streamOutgoing, bool _streamUsingTCP, bool _forceMulticastOnUnspecified)
{
	streamOutgoing = _streamOutgoing;
	streamUsingTCP = _streamUsingTCP;
	forceMulticastOnUnspecified = _forceMulticastOnUnspecified;
}

void SetupSender::setSubsession(MediaSubsession * _subSession)
{
	subSession = _subSession;
}

SetparamSender::SetparamSender(RtspReqSender & sender):CmdSenderDecorator(sender)
{
	type = SENDERTYPE_SETPARAM;
}

bool SetparamSender::sendRequest()
{
	LOG_DEBUG("Send rtsp set param request!");
	CHECK_NULL_COND(client, false);

	setRtspCustomData();

	MediaSession * session = client->getMediaSession();
	CHECK_NULL_COND(session, false);

	if (paramName.empty() || paramValue.empty())
	{
		return false;
	}

	client->setCurrentSender(this);
	client->sendSetParameterCommand(*session, rspHandler, paramName.c_str(), paramValue.c_str(),auth);

	return true;
}

void SetparamSender::setParam(std::string & _paramName, std::string & _paramValue)
{
	paramName = _paramName;
	paramValue = _paramValue;
}


GetparamSender::GetparamSender(RtspReqSender & sender):CmdSenderDecorator(sender)
{
	type = SENDERTYPE_GETPARAM;
}

bool GetparamSender::sendRequest()
{
	LOG_DEBUG("Send rtsp get param request!");
	CHECK_NULL_COND(client, false);

	setRtspCustomData();

	MediaSession * session = client->getMediaSession();
	CHECK_NULL_COND(session, false);

	if (paramName.empty())
	{
		return false;
	}

	client->setCurrentSender(this);
	client->sendGetParameterCommand(*session, rspHandler, paramName.c_str(),auth);

	return true;
}


void GetparamSender::getParam(std::string & _paramName)
{
	paramName = _paramName;
}

}


