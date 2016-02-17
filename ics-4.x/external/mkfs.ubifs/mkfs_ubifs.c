#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main(int argc, char **argv)
{
	char path[20];
	char cmd[100];

	int mtd_no, ubi_no;

	if(argc != 4){
		printf("Usage: mkfs.ubifs part_name mtd_no ubi_no\n");
		return -1;
	}
	strcpy(path, argv[1]);
	mtd_no = atoi(argv[2]);
	ubi_no = atoi(argv[3]);

	sprintf(cmd, "umount /%s", path);	
	printf("%s\n", cmd);
	system(cmd);
    sprintf(cmd, "ubidetach /dev/ubi_ctrl -m %d", mtd_no);
    printf("%s\n", cmd);
    system(cmd);
    sprintf(cmd, "ubiformat /dev/mtd/mtd%d", mtd_no);
    printf("%s\n", cmd);
    system(cmd);
    sprintf(cmd, "ubiattach /dev/ubi_ctrl -m %d -d %d", mtd_no, ubi_no);
    printf("%s\n", cmd);
    system(cmd);
    sprintf(cmd, "ubimkvol /dev/ubi%d -N %s -m", ubi_no, path);
    printf("%s\n", cmd);
    system(cmd);
    sprintf(cmd, "mount -t ubifs /dev/ubi%d_0 /%s", ubi_no, path);
    printf("%s\n", cmd);
    system(cmd);

	return 0;
}
