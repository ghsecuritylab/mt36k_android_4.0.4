#include <stdio.h>  
#include <stdlib.h>
#include <string.h>    
#include <sys/types.h>
#include <dirent.h>
#include <unistd.h>

#define LINE 256  
char *ReadData(FILE *fp, char *buf)  
{  
    return fgets(buf, LINE, fp);  
}  
int GetDeviceInfo()
{
    FILE *fpp;  
    char *buff=NULL;
    char *p=NULL;
    DIR *my_dir;
    struct dirent *ppptr;

    if((my_dir=opendir("/sys/class/net/ppp0")) != NULL){//connected already
        printf("---connected already---");
        return 1; 
    }
    else{
        printf("---no connect---");
        return 0; 
    }
}
int main()  
{  
    FILE *fp;  
    char *buf,*p;
	char temp[128];  
    if ((fp=fopen("/data/data/ppid","r"))==NULL)
    {  
        printf("Cannot open file:dev!\n");  
        return 0;  
    }  
    buf = (char *)malloc(LINE*sizeof(char));  
    p = ReadData(fp, buf);
	printf("==%s\n",p);
	
	if(p){
        if(GetDeviceInfo() == 1){
		    sprintf(temp,"kill %s",p);
		    printf("conn-->dis:%s\n",temp);
        }
        else{
		    sprintf(temp,"kill -9 %s",p);
		    printf("timeout:%s\n",temp);
        }
		system(temp);
	}
	else{
		printf("===p null===\n");	
	}
    fclose(fp);
    return 1;  

}  
