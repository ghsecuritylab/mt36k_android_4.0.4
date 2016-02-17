#include "RtspPlayer.h"
#include <stdio.h>
#include <string.h>
#include <iostream>
#include <stdlib.h>
#include "myLog.h"
#include "myCfg.h"
#include "app_init.h"

using namespace rtsp;
using namespace std; 

int main(int argc, char **argv)
{
	if (argc < 2)
	{
		cout<<"Usage:./test rtsp://xxxx [t=1] [l=1] [f=1] [c=0] [o=1] [a=0] [v=0]"<<endl;
		cout<<"t=1:enable use tcp"<<endl;
		cout<<"l=1:enable print log"<<endl;
		cout<<"f=1:enable save local file"<<endl;
		cout<<"c=0:disable cmpb play"<<endl;
		cout<<"o=1:only count the total size"<<endl;
		cout<<"a=0:doesn't play aduio"<<endl;
		cout<<"v=0:doesn't play video"<<endl;
		return 0;
	}
		
	string strUrl;
	bool bTcp = false;
	bool bLog = false;
	bool bFile = false;
	bool bCmpb = true;
	bool bOnlyCount = false;
	bool bCmpbPlayAudio = true;
	bool bCmpbPlayVideo = true;
	strUrl = string(argv[1]);
	cout<<"The Url: "<<strUrl<<endl;
		
	argc -= 2;
	argv++;
	argv++;

	while (argc > 0)
	{
		if (strcmp(argv[0], "t=1") == 0)
			bTcp = true;
		else if (strcmp(argv[0], "l=1") == 0)
			bLog = true;
		else if (strcmp(argv[0], "f=1") == 0)
			bFile = true;
		else if (strcmp(argv[0], "c=0") == 0)
			bCmpb = false;
		else if (strcmp(argv[0], "o=1") == 0)
			bOnlyCount = true;
	    else if (strcmp(argv[0], "a=0") == 0)
			bCmpbPlayAudio = false;
		else if (strcmp(argv[0], "v=0") == 0)
			bCmpbPlayVideo = false;
		argc--;
		argv++;
	}

	rtsp::setLog(bLog);
	rtsp::saveLocalFile(bFile);
	rtsp::saveCmpbPlay(bCmpb);
	rtsp::setTcpFlag(bTcp);
	rtsp::setOnlyCount(bOnlyCount);
	rtsp::setCmpbPlayAudio(bCmpbPlayAudio);
	rtsp::setCmpbPlayVideo(bCmpbPlayVideo);

    init_rpc();
	
	string  cmd;

	RtspPlayer *player = RtspPlayer::createNew();
	if (NULL == player)
	{
		cout<<"create player error!"<<endl;
		return 0;
	}
	
	bool bRet = true;
	
	while(1)
	{		
		cout<<"Please input the cmd:"<<endl;
		cout<<"exit"<<endl;
		cout<<"play"<<endl;
		cout<<"pause"<<endl;
		cout<<"stop"<<endl;
		cout<<"timeseek"<<endl;
        cout<<"reseturl"<<endl;
		cin>>cmd;

		if (0 == cmd.compare("exit"))
		{
            if (player)
                delete player;
            cout<<"delete player success"<<endl;
			return 0;
		}
		else if (0 == cmd.compare("play"))
		{
			if (strUrl.length() == 0)
			{
				cout<<"Please input the url:"<<endl;
				cin>>strUrl;
			}
            player->setTcpRetry();
			bRet = player->play(strUrl);
		}
		else if (0 == cmd.compare("pause"))
		{
			bRet = player->pause(true);
		}
		else if (0 == cmd.compare("stop"))
		{
			player->stop();
		}
		else if (0 == cmd.compare("timeseek"))
		{
			cout<<"Please input the time:"<<endl;
			string time;
			cin>>time;
			bRet = player->timeseek(atoi(time.c_str()));
		}
        else if (0 == cmd.compare("reseturl"))
		{
            bRet = true;
            strUrl.clear();
		}
		else
		{
			//
		}
		
		if (false == bRet)
		{
			cout<<"send cmd error!"<<endl;
		}
	}
	
	return 0;
}
 
