#ifndef WFD_RTSP_CMD_SENDER_H
#define WFD_RTSP_CMD_SENDER_H

#include <iostream>
#include <string>

class Authenticator;
class RTSPClient;
class MediaSubsession;

namespace rtsp
{
typedef enum 
{
	WFDRTSP_SENDTYPE_OPTINON =0,
	WFDRTSP_SENDTYPE_DESCRIPTION,
	WFDRTSP_SENDTYPE_PLAY,
	WFDRTSP_SENDTYPE_PAUSE,
	WFDRTSP_SENDTYPE_TEARDOWN,
	WFDRTSP_SENDTYPE_SETUP,
	WFDRTSP_SENDTYPE_SETPARAM,
	WFDRTSP_SENDTYPE_GETPARAM,
	WFDRTSP_SENDTYPE_UNKNOWN
}WfdRtspSenderType;

class WfdRtspClient;
typedef void (dealResultHandler)(RTSPClient* rtspClient, int resultCode, char* resultString);

class WfdRtspReqSender
{
	public:
		virtual bool sendRequest() = 0;
			
	protected:
		virtual ~WfdRtspReqSender(){}
		static WfdRtspClient* client;
		static Authenticator* auth;			
};

class WfdRtspCmdSenderDecorator:public WfdRtspReqSender
{
	private:
		WfdRtspReqSender *pWfdRtspReqSender;

	public:
		WfdRtspCmdSenderDecorator(WfdRtspReqSender & sender);
		virtual bool sendRequest(){return true;}
		bool callNext();
		WfdRtspReqSender * const getNext(){return pWfdRtspReqSender;}
		void setNext(WfdRtspReqSender *const sender){pWfdRtspReqSender = sender;}
		void setRspHandler(dealResultHandler * _handler){rspHandler = _handler;}
		void setSelfData(std::string & _selfData){selfData = _selfData;}
		WfdRtspSenderType getWfdRtspSenderType(){return type;}

		virtual ~WfdRtspCmdSenderDecorator(){}
	protected:
		std::string selfData;
		dealResultHandler * rspHandler;
		WfdRtspSenderType type;
		void setRtspCustomData();
};


class WfdRtspSenderInit:public WfdRtspReqSender
{
public:
	WfdRtspSenderInit(WfdRtspClient* _client, Authenticator* _auth);
	virtual ~WfdRtspSenderInit();
	bool sendRequest();
};

class WfdRtspConnectionSender : public WfdRtspCmdSenderDecorator
{
public:
    WfdRtspConnectionSender(WfdRtspReqSender& sender);
    virtual ~WfdRtspConnectionSender();
    virtual bool sendRequest();
};

class WfdRtspOptionSender : public WfdRtspCmdSenderDecorator
{
public:
    WfdRtspOptionSender(WfdRtspReqSender& sender);
    virtual ~WfdRtspOptionSender();
    virtual bool sendRequest();
};

class WfdRtspSetupSender : public WfdRtspCmdSenderDecorator
{
public:
    WfdRtspSetupSender(WfdRtspReqSender& sender);
    virtual ~WfdRtspSetupSender();
    virtual bool sendRequest();
};

class WfdRtspPlaySender : public WfdRtspCmdSenderDecorator
{
public: 
    WfdRtspPlaySender(WfdRtspReqSender& sender);
    virtual ~WfdRtspPlaySender();
    virtual bool sendRequest();
};

class WfdRtspTeardownSender : public WfdRtspCmdSenderDecorator
{
public: 
    WfdRtspTeardownSender(WfdRtspReqSender& sender);
    virtual ~WfdRtspTeardownSender();
    virtual bool sendRequest();
};

}
#endif
