#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <dirent.h>
#include <limits.h>
#include <sys/stat.h>
#include <sys/types.h>


typedef struct {
	char **prebuilt;
	int cap;
	int size;
}preApps_t;


static preApps_t g_preApps;


/*
 * path: the reserved path, name: the file dir name
 * make sure the path without subfix '/'
 * etc. keep /data/logo, then /data/logo/* would be kept
*/
static int cmpdirname(const char *name, const char *path) 
{	

	if (path == NULL)
		return 0;

	while (*path && *name && *path == *name){
		path++;
		name++;
	}

	//name match with path , so keep
   if (!*path && (!*name || *name == '/')) 
	   	return 1;   

   return 0;	
}

/* return -1 on failure, with errno set to the first error */
static int unlink_recursive(const char* name, int argc, const char* argv[]) //const char *keeppath)
{
    struct stat st;
    DIR *dir;
    struct dirent *de;
    int fail = 0;
	int dirpos = 0;
	int i = 0;
	int keepsubdir = 0;
    /* is it a file or directory? */
    if (lstat(name, &st) < 0)
        return -1;

    /* a file, so unlink it */
    if (!S_ISDIR(st.st_mode)) {
    	/* vapor zhou add on 2012-08-02 for pre installed apk*/
    	for (i = 0; i < g_preApps.size; i++) {
    		if (!strcmp(g_preApps.prebuilt[i], name))
    			return 0;
    	}
        return unlink(name);
    }

    /* a directory, so open handle */
    dir = opendir(name);
    if (dir == NULL)
        return -1;

    /* recurse over components */
    errno = 0;
    while ((de = readdir(dir)) != NULL) {
        char dn[PATH_MAX];
        if (!strcmp(de->d_name, "..") || !strcmp(de->d_name, "."))
            continue;    
        sprintf(dn, "%s/%s", name, de->d_name);
        
    if (lstat(dn, &st) == 0 && S_ISDIR(st.st_mode)) {
    	int bkeeped = 0;
	    for (i = 0 ;  i < argc; i++) {
		  if (cmpdirname(dn, argv[i]))
			bkeeped = 1;
			keepsubdir = 1;
			break;
		}
		if (bkeeped)
			continue;
    	
    }
	    
        if (unlink_recursive(dn, argc, argv) < 0) {
            fail = 1;
            break;
        }
        errno = 0;
    }
    /* in case readdir or unlink_recursive failed */
    if (fail || errno < 0) {
        int save = errno;
        closedir(dir);
        errno = save;
        return -1;
    }

    /* close directory handle */
    if (closedir(dir) < 0)
        return -1;

    /* delete target directory */
    if (keepsubdir)
		return 0;
	
   	return rmdir(name);    	  
}

int wipe_ubifs(const char *path, int argc, const char* argv[]) //const char *reservepath)
{
    int ret = unlink_recursive(path, argc, argv);
    if (ret < 0) 
	{
        fprintf(stderr, "rm failed for %s, %s\n", path, strerror(errno));
        return -1;
    }

    return 0;
}

void initPreApps(void) {
	g_preApps.cap = 10;
	g_preApps.size = 0;
	g_preApps.prebuilt = (char**)malloc(g_preApps.cap*sizeof(char*));
}

void scanPermForPreApks(const char* prefile) {
	FILE *fp = fopen(prefile, "r");
	char linebuf[512];
	if (fp) {
		while (fgets(linebuf, 512, fp)) {
			char *pos = strrchr(linebuf, '\n');
			if (pos)
				*pos = 0;
				
			g_preApps.prebuilt[g_preApps.size++] = strdup(linebuf);

			if (g_preApps.size == g_preApps.cap) {
				g_preApps.cap *= 2;
				g_preApps.prebuilt = (char**)realloc((void*)g_preApps.prebuilt, sizeof(char*)*g_preApps.cap);
			}
		}
		fclose(fp);		
	}
}

