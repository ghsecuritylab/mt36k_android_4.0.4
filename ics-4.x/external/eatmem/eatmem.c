#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

void main(int argc, char **argv)
{
	int size=atoi(argv[1]);
	char *mem;
	int i;

	mem = (char*)malloc(size*1024*1024);
	
	if(argc <= 2)
	{
		printf("Generate Zero Filled Data!\n");
		memset(mem, 0, size*1024*1024);
	}else
	{
		printf("Generate Random Data!\n");
		srandom(time(NULL));

		for(i=0; i < size*1024*1024; i++)
		{
			mem[i] = random()%256;
		}
	}

	printf("Done\n");	

	while(1)
	{
		sleep(1);
	}
}
