/*
 ** Copyright 2008, The Android Open Source Project
 ** Copyright 2012, Mediatek
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

#ifndef ANDROID_MTK_V4L2_CAMERA_H
#define ANDROID_MTK_V4L2_CAMERA_H

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <signal.h>
#include <sys/mman.h>
#include <sys/time.h>
#include <sys/ioctl.h>
#include <sys/poll.h>
#include <sys/stat.h>

#include <utils/RefBase.h>
#include <linux/videodev2.h>
#include <utils/String8.h>


namespace android
{
#define MAX_RESOLUTION_STRING_LENGTH    512
#define MAX_BUFFERS     4
#define MINIMUM_FRAME_RATE 15

//#define TIMING_LOG 
static struct timeval dtv, dtv2;
static unsigned long long dstart_utime, dend_utime;
#define _MeasureTimeBegin()                             \
{                                                       \
    gettimeofday(&dtv,NULL);                            \
    dstart_utime = dtv.tv_sec * 1000000 + dtv.tv_usec;  \
}

#ifdef TIMING_LOG
#define _MeasureTimeEnd(arg)                                                \
{                                                                           \
    gettimeofday(&dtv2,NULL);                                               \
    dend_utime = dtv2.tv_sec * 1000000 + dtv2.tv_usec;                      \
    LOGV("%s consumes %.2fms", arg, (dend_utime - dstart_utime)/1000.0 );   \
}
#else
#define _MeasureTimeEnd(arg) {}
#endif

struct cam_parm {
	struct v4l2_captureparm capture;
	int contrast;
	int effects;
	int brightness;
	int flash_mode;
	int focus_mode;
	int iso;
	int metering;
	int saturation;
	int scene_mode;
	int sharpness;
	int white_balance;
};

struct fimc_buffer
{
    void *start;
    size_t length;
};

class MtkV4L2Camera: public virtual RefBase
{
    public:
        MtkV4L2Camera();
        virtual ~MtkV4L2Camera();
        int initCamera();
        void deinitCamera();
        void resetCamera();
        bool isCameraExist();
        bool isCameraSupportingMjpeg();
        bool isPreviewStarted();
        int startPreview(void);
        int stopPreview(void);
        int getPreview(char** rawdata);
        int getPreviewDone(int index);
        int getPreviewSize(int *width, int *height, int *frame_size);
        int setPreviewSize(int width, int height, int pixel_format);
        int getPreviewPixelFormat(void);
        int getCameraFd(void);
        int getFrameRate();
        int setFrameRate(int frame_rate);
        bool getPixelFormat(char* ResolutionList);
        void setSystemSupportMjpeg(bool bSystemSupportMjpeg);
        int YUYVtoY800(unsigned char *bufsrc, unsigned char *bufdest, int width, int height);
        int YUYVtoNV12(unsigned char *bufsrc, unsigned char *bufdest, int width, int height);
        int YUYVtoNV21(unsigned char *bufsrc, unsigned char *bufdest, int width, int height);
        int YUYVtoYV12(unsigned char *bufsrc, unsigned char *bufdest, int width, int height);
        int YUYVtoJpegFile(unsigned char *bufsrc, unsigned char *bufdest, int width, int height);
        int MJPGtoNV16(unsigned char *inputBuffer, int inputBufferLength, unsigned char **outputBuffer, int width, int height); 
        int NV16toNV21(unsigned char *bufsrc, unsigned char *bufdest, int width, int height);
        int NV16toY800(unsigned char *bufsrc, unsigned char *bufdest, int width, int height);
        int NV16toJpegFile(unsigned char *inputBuffer, unsigned char *outputBuffer, int width, int height);

//added by wang youlin
	 int ifCameraOpened();
	int v4l2QueryControl(int control, struct v4l2_queryctrl *queryctrl);
	int v4l2GetControl(int control);
	int v4l2SetControl(int control, int value);
        int YUYVtoJPEGInterpolationTo3264x2448(unsigned char *bufsrc, unsigned char *bufdest, int width, int height);
//end by wang youlin

        static MtkV4L2Camera *createInstance(void)
        {
            static MtkV4L2Camera singleton;
            return  &singleton;
        }
        
    private:
        v4l2_streamparm m_streamparm;
        int m_flag_init;
        int m_cam_fd;
        int m_preview_v4lformat;
        int m_preview_width;
        int m_preview_height;
        int m_preview_framesize;
        int m_preview_framerate;
        bool m_flag_camera_start;
        bool m_is_yuyv_supported;
        bool m_camera_support_mjpeg;
        bool m_system_support_mjpeg;
        void* m_mjpeg_tmp_buffer;       // To decompress MJPEG data
        struct fimc_buffer m_capture_buf[MAX_BUFFERS];
};


}; // namespace android

#endif // ANDROID_MTK_V4L2_CAMERA_H
