#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
 
#define THE_PORT 8090
#define CLIENT_NUM 10
#define BUFFSZ 1024

int main()
{
	int socketfd;
	int clientfd;
	char buff[BUFFSZ];
	struct sockaddr_in sa;
	struct sockaddr_in cliAdd;
	
	socketfd = socket(AF_INET, SOCK_STREAM, 0);
	
	if(socketfd == -1)
	{
		printf("socket create error\n");
		exit(1);
	}
	
	bzero(&sa, sizeof(sa));
	
	sa.sin_family = AF_INET;
	sa.sin_port = htons(THE_PORT);
	sa.sin_addr.s_addr = htons(INADDR_ANY);
	
	bzero(&(sa.sin_zero), 8);
	if(bind(socketfd, (struct sockaddr *)&sa, sizeof(sa))!= 0)
	{
		printf("bind failed\n");
		exit(1);
	}
	
	if(listen(socketfd ,CLIENT_NUM) != 0)
	{
		printf("listen error\n");
		exit(1);
	}
	else
	{
		printf("listening\n");
	}
	socklen_t len = sizeof(cliAdd);

	int closing =0;
	
	while( closing == 0 && (clientfd = accept(socketfd, (struct sockaddr *)&cliAdd, &len)) >0 )
	{
		int n;
		while((n = recv(clientfd,buff, BUFFSZ-1,0 )) > 0)
		{
			printf("received data = %s\n",buff);
			printf("received bytes = %d\n", n);
			buff[n] = '\0';
			proc_cmd(clientfd , buff);
			if(strcmp(buff, "quit") == 0)
			{
				//connection close
				break;
			}
			else if(strcmp(buff, "close") == 0)
			{
				//server close
				closing = 1;
				printf("server is closing\n");
				break;
			}
		}
		close(clientfd);
	}
	close(socketfd);
	return 0;
}
void proc_cmd(int sockfd,char buf[])
{
		char cmdbuf[BUFFSZ];
		char fpath[64] = "/data/mtinfo";
		FILE *fp;
		printf("proc_cmd now\n");
		sprintf(cmdbuf,"%s 2> %s",buf,fpath);
		
		if(-1 == system(cmdbuf))
		{
			printf("system call failed\n");
			exit(1);
		}
		if((fp = fopen(fpath,"r")) == -1)
		{
			printf("open file %s failed\n",fpath);
			exit(1);
		}
		if(!fgets(cmdbuf,sizeof(buf),fp))//cmd successed
		{
		   printf("----->cmd successed\n");
		   if(send(sockfd,"ok",2,0)<0)
		   {
			perror("send error");
			exit(1);
		   }		
		}
		else//fail
		{
			printf("----->cmd fail\n");
			if(send(sockfd,"fail",4,0)<0)
			{
				perror("send error");
				exit(1);	
			}		
		}
}


