/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <dirent.h>
#include <errno.h>
#include <fcntl.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/mman.h>
#include <sys/mount.h>

#include <linux/kdev_t.h>
#include <linux/fs.h>

#define LOG_TAG "Vold"

#include <cutils/log.h>
#include <cutils/properties.h>

#include "Extfs.h"

extern "C" int logwrap(int argc, const char **argv, int background);
extern "C" int mount(const char *, const char *, const char *, unsigned long, const void *);

int Extfs::doMount(const char * fsPath, const char * mountPoint,
			bool ro, bool remount){


	int rc;
	unsigned long flags;

	flags = MS_NODEV | MS_NOEXEC | MS_NOSUID |MS_DIRSYNC;

	flags |= (ro ? MS_RDONLY : 0);
	flags |= (remount ? MS_REMOUNT : 0);

	if(mount(fsPath,mountPoint, "ext3",flags,NULL) == 0){
		rc = 0;
	}else if(mount(fsPath, mountPoint,"ext2",flags,NULL) == 0){
		rc = 0;
	}else if(mount(fsPath, mountPoint, "ext4",flags,NULL) == 0){
		rc = 0;
	}

	if(rc == 0){
		if(chmod(mountPoint, 0777) == -1){
			SLOGE("Failed to chmod %s", mountPoint);
		}
	}
	return rc;
}


