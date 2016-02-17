#ifndef ANDROID_VOLD_H
#define ANDROID_VOLD_H

#include <utils/RefBase.h>

#include <binder/IInterface.h>
#include <binder/Binder.h>
#include <binder/Parcel.h>

struct mountiso_ex_param
{
    char* isoFilePath;
    char* isoLabel;
};

class Vold 
{
public: 
    static const int UMOUNT_DEVICE_CMD = 1;
	static const int MOUNT_ISO_CMD = 2;
    static const int MOUNT_ISO_EX_CMD = 4;
	static const int UMOUNT_ISO_CMD = 3;
    
public:
    int start();
    int get(void **value);
    int doCmd(int command, void* value);
    int stop();

    public:
        Vold();
        ~Vold();
};

#endif // ANDROID_VOLD_H
