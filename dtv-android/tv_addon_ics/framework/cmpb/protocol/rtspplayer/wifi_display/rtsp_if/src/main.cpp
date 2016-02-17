#include <stdio.h>
#include <string.h>
#include <iostream>
#include <stdlib.h>
#include "WfdRtspPlayer.h"

using namespace rtsp;
using namespace std; 

int main(int argc, char **argv)
{
	if (argc < 3)
	{
		cout<<"Usage:./wfd.bin IP PORT"<<endl;
		return 0;
	}
    
	string strUrl;
   
	strUrl = string("rtsp://") + string(argv[1]) + string(":") + string(argv[2]);
	cout<<"The Url: "<<strUrl<<endl;
	
	string  cmd;

	WfdRtspPlayer *player = new WfdRtspPlayer();
	if (NULL == player)
	{
		cout<<"create player error!"<<endl;
		return 0;
	}
	
	int iRet = 0;
	
	while(1)
	{		
		cout<<"Please input the cmd:"<<endl;
		cout<<"exit"<<endl;
		cout<<"open"<<endl;
		cout<<"play"<<endl;
		cout<<"pause"<<endl;
		cout<<"unpuase"<<endl;
        cout<<"stop"<<endl;
        cout<<"close"<<endl;
		cin>>cmd;

		if (0 == cmd.compare("exit"))
		{
            player->stop();
            player->close();
			return 0;
		}
		else if (0 == cmd.compare("open"))
		{
			iRet = player->open(strUrl);
		}
        else if (0 == cmd.compare("play"))
		{
			iRet = player->play();
		}
		else if (0 == cmd.compare("pause"))
		{
			iRet = player->pause();
		}
        else if (0 == cmd.compare("unpause"))
		{
			iRet = player->unPause();
		}
		else if (0 == cmd.compare("stop"))
		{
			iRet = player->stop();
		}
        else if (0 == cmd.compare("close"))
		{
			iRet = player->close();
		}
		else
		{
			//
		}
		
		if (0 != iRet)
		{
			cout<<"send cmd error!"<<endl;
            player->stop();
            player->close();
			return 0;
		}
	}
	
	return 0;
}

