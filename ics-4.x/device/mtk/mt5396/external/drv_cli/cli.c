#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <pthread.h>
#include <fcntl.h>

#define DRV_CLI_DEV   "/dev/cli"
#define CMD_PASSSTR 0

int main(int argc, char *argv[])
{
    char cKey;
    int i4_fdcli;
    int cmd = CMD_PASSSTR;
    unsigned int keys = 0, quit;

    i4_fdcli = open(DRV_CLI_DEV, O_RDWR);

    if (i4_fdcli < 0)
    {
        printf("Open CLI failed\n");
        return -1;
    }
    else
    {
        printf("Open CLI successfully\n");
    }

    quit = ('q' << 24) | ('u' << 16) | ('i' << 8) | 't';

    while (1)
    {
        cKey = getchar();
        keys = (keys << 8) | cKey;
        if (keys == quit)
        {
           break;
        }

        if (ioctl(i4_fdcli, cmd, &cKey) < 0)
        {
            printf("Cannot do ioctl to CLI device!\n");
        }
    }
    close(i4_fdcli);

    return 0;
}

