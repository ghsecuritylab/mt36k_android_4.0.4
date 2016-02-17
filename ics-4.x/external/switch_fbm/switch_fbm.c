#include "mtvdo.h"

extern MT_RESULT_T MTVDO_SetFBMMode(UINT32 u4Mode);
extern MT_RESULT_T MTVDO_Get_NetSwap_buffer(MTVDO_VIDEO_BUFFER *pBuffer);

void main(int argc, char **argv)
{
	if(argc <= 1)
	{
		printf("switch_fbm [1|2]\n");
		printf("  * 0 to query swap position\n");
		printf("  * 1 for TV mode\n");
		printf("  * 2 for Android mode\n");
		exit(1);
	}

	MTVDO_VIDEO_BUFFER buffer;
	int mode = atoi(argv[1]);

	MTAL_Init();

	if(mode == 0)
	{
		MTVDO_Get_NetSwap_buffer(&buffer);
		printf("0x%08X 0x%08X\n", (unsigned int)(buffer.u4Addr), (unsigned int)(buffer.u4Size));
	}
	else if(mode == 1 || mode == 2)
	{
		MTVDO_SetFBMMode(mode);
	}else
	{
		printf("Not support mode %d\n", mode);
		exit(1);
	}

	return 0;
}
