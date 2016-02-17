#ifndef _RTSP_CMD_SENDER_H_
#define _RTSP_CMD_SENDER_H_

#include <iostream>
#include <string>
#include <list>
class Authenticator;
class RTSPClient;
class MediaSubsession;

namespace rtsp
{
typedef enum 
{
	SENDERTYPE_OPTINON =0,
	SENDERTYPE_DESCRIPTION,
	SENDERTYPE_PLAY,
	SENDERTYPE_PAUSE,
	SENDERTYPE_TEARDOWN,
	SENDERTYPE_SETUP,
	SENDERTYPE_SETPARAM,
	SENDERTYPE_GETPARAM,
	SENDERTYPE_UNKNOWN
}SenderType;

class MtkRTSPClient;
typedef void (dealResultHandler)(RTSPClient* rtspClient, int resultCode, char* resultString);

class RtspReqSender
{
	public:
		virtual bool sendRequest() = 0;
			
	protected:
		virtual ~RtspReqSender(){}
		static MtkRTSPClient* client;
		static Authenticator* auth;	
};


class CmdSenderDecorator:public RtspReqSender
{
	private:
		RtspReqSender *pRtspReqSender;

	public:
		CmdSenderDecorator(RtspReqSender & sender);
		virtual bool sendRequest(){return true;}
		bool callNext();
		RtspReqSender * const getNext(){return pRtspReqSender;}
		void setNext(RtspReqSender *const sender){pRtspReqSender = sender;}
		void setRspHandler(dealResultHandler * _handler){rspHandler = _handler;}
		void setSelfData(std::string & _selfData){selfData = _selfData;}
		SenderType getSenderType(){return type;}

		virtual ~CmdSenderDecorator(){}
	protected:
		std::string selfData;
		dealResultHandler * rspHandler;
		SenderType type;
		void setRtspCustomData();
};

class SenderInit:public RtspReqSender
{
	public:
		SenderInit(MtkRTSPClient* _client, Authenticator* _auth);
		virtual ~SenderInit();
		bool sendRequest();
		void RecordSender(CmdSenderDecorator *sender);

	private:
		std::list<CmdSenderDecorator*> cmdList;
};

class OptionSender:public CmdSenderDecorator
{
	public:
		OptionSender(RtspReqSender & sender);
		virtual ~OptionSender(){}
		virtual bool sendRequest();
};

class DspSender:public CmdSenderDecorator
{
	public:
		DspSender(RtspReqSender & sender);
		virtual ~DspSender(){}
		virtual bool sendRequest();
};

class PlaySender:public CmdSenderDecorator
{
	public:
		PlaySender(RtspReqSender & sender);
		virtual ~PlaySender(){}
		void setRange(double _start = 0.0f, double _end = -1.0f, float _scale = 1.0f);
		
		virtual bool sendRequest();
	private:
		double start;
		double end;
		float scale;
};

class PauseSender:public CmdSenderDecorator
{
	public:
		PauseSender(RtspReqSender & sender);
		virtual ~PauseSender(){}
		virtual bool sendRequest();
};

class TeardownSender:public CmdSenderDecorator
{
	public:
		TeardownSender(RtspReqSender & sender);
		virtual ~TeardownSender(){}
		virtual bool sendRequest();
};

class SetupSender:public CmdSenderDecorator
{
	public:
		SetupSender(RtspReqSender & sender);
		virtual ~SetupSender(){}
		void setParam(bool _streamOutgoing = false, bool _streamUsingTCP = false, bool _forceMulticastOnUnspecified = false);
		void setSubsession(MediaSubsession * _subSession);
		virtual bool sendRequest();
	private:
		MediaSubsession * subSession;
		bool streamOutgoing;
		bool streamUsingTCP;
		bool forceMulticastOnUnspecified;
};

class SetparamSender:public CmdSenderDecorator
{
	public:
		SetparamSender(RtspReqSender & sender);
		virtual ~SetparamSender(){}
		void setParam(std::string & _paramName, std::string & _paramValue);

		virtual bool sendRequest();
	private:
		std::string paramName;
		std::string paramValue;
};

class GetparamSender:public CmdSenderDecorator
{
	public:
		GetparamSender(RtspReqSender & sender);
		virtual ~GetparamSender(){}
		void getParam(std::string & _paramName);
		virtual bool sendRequest();
	private:
		std::string paramName;
};

}

#endif
