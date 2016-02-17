#ifndef _WIPE_DATA_UBIFS_H_
#define _WIPE_DATA_UBIFS_H_

int wipe_ubifs(const char *path, int argc, const char* argv[]);
void scanPermForPreApks(const char* prefile);
void initPreApps(void);

#endif

