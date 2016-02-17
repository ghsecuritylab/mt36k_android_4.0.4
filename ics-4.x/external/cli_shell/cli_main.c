#include <stdio.h>
#include <sys/ioctl.h>
#include <stdlib.h>
#include <fcntl.h>
#include <time.h>
#include <errno.h>

void readkmsg(int);

int main(int argc, char** argv)
{
	int handle = open("/dev/cli", O_RDWR);
	char command[200];

	if(argc <= 1)
	{
		printf("Usage: cli_shell \"command\"\n");
		return -1;
	}

	readkmsg(0);

	system("echo 0 > /proc/sys/kernel/printk");

	strcpy(command, argv[1]);
	if (handle < 0)
	{
		printf("Cannot open /dev/cli");
		return -1;
	}

	char c_key;
	int len=strlen(command);
	int i;
	for(i=0; i < len; i++)
	{
		c_key = command[i];
    	ioctl(handle, 0, &c_key);
	}
	
	c_key = '\r';
	ioctl(handle, 0, &c_key);	
    c_key = '\n';
    ioctl(handle, 0, &c_key);
	close(handle);

	system("echo 7 > /proc/sys/kernel/printk");

	usleep(200*1000);
	readkmsg(1);
	return 0;
}

#define MAX_BUF_SIZE 512*1024*1024
void readkmsg(int print)
{
    char *buf = (char*)malloc(MAX_BUF_SIZE);
    int result;
    fd_set readset;
    int fd = open("/proc/kmsg", O_RDONLY);
	struct timeval timeout = { 0, 50000 /* 5ms */ }; // If we oversleep it's ok, i.e. ignore EINTR.
    
	while(1)
    {
        do {
            FD_ZERO(&readset);
            FD_SET(fd, &readset);
            result = select(fd + 1, &readset, NULL, NULL, &timeout);
        } while (result == -1 && errno == EINTR);

        if (result > 0) {
            if (FD_ISSET(fd, &readset)) {
                int ret = read(fd, buf, MAX_BUF_SIZE-1);

                if (ret > 0) {
                    buf[ret]=0;
					if(print)
					{
						char *pch;
						pch = strtok(buf, "\n");
						while(pch != NULL)
						{
							printf("%s\n",&pch[3]);
							pch = strtok(NULL, "\n");
	
						}
//	                    printf("%s", buf);
					}
                }
            }

        }else if (result == 0) {
            break;
        }
    }

	free(buf);

	close(fd);
}
