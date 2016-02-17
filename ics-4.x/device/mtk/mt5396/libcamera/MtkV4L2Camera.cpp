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


#define LOG_NDEBUG 0
#define LOG_TAG "MtkV4L2Camera"

#include "MtkV4L2Camera.h"
#include <math.h>
#include <string.h>
#include <stdlib.h>
#include <sys/poll.h>
#include <utils/Log.h>

extern "C" 
{
    #include <jpeglib.h>
}

using namespace android;

#define CHECK(return_value)                                     \
if (return_value < 0)                                           \
{                                                               \
    LOGE("%s::%d fail. errno: %s, \n",                          \
    __func__, __LINE__, strerror(errno));                       \
    return -1;                                                  \
}

#define CHECK_NR(return_value)                                  \
if (return_value < 0)                                           \
{                                                               \
    LOGE("%s::%d fail. errno: %s, \n",                          \
    __func__, __LINE__, strerror(errno));                       \
}

namespace android
{

// Set capture format.
static int v4l2_s_fmt(int fd, int width, int height, unsigned int fmt)
{
    struct v4l2_format v4l2_fmt;
    int ret;
    int iRetryCounter = 0;

    LOGV("v4l2_s_fmt(): width=%d, height=%d, fmt=0x%x", width, height, fmt);

    v4l2_fmt.fmt.pix.pixelformat = fmt;
    v4l2_fmt.fmt.pix.width = width;
    v4l2_fmt.fmt.pix.height = height;
    v4l2_fmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;


    while ( ioctl(fd, VIDIOC_S_FMT, &v4l2_fmt) < 0 )
    {
        LOGE("v4l2_s_fmt(): VIDIOC_S_FMT failed\n");
        sleep(1);
        iRetryCounter++;
        if ( iRetryCounter > 10 ) 
        {
            return -1;
        }
    }

    if ( iRetryCounter != 0 )
    {
        LOGE("v4l2_s_fmt(): VIDIOC_S_FMT retry OK\n");
    }

    return 0;
}

// To set stream parameter.
static int v4l2_s_parm(int fd, struct v4l2_streamparm *streamparm)
{
    int ret;

    streamparm->type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

    ret = ioctl(fd, VIDIOC_S_PARM, streamparm);
    if (ret < 0)
    {
        LOGE("v4l2_s_parm(): VIDIOC_S_PARM failed\n");
        return ret;
    } 

    return 0;
}

// To check whether the format is supported by the camera or not.
static int v4l2_query_fmt(int fd, unsigned int fmt)
{
    struct v4l2_fmtdesc fmtdesc;
    int found = 0;

    LOGV("v4l2_query_fmt()");

    fmtdesc.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    fmtdesc.index = 0;

    while (ioctl(fd, VIDIOC_ENUM_FMT, &fmtdesc) == 0)
    {
        if (fmtdesc.pixelformat == fmt)
        {
            LOGV("v4l2_query_fmt(): passed fmt = %#x found pixel format[%d]: %s\n", fmt, fmtdesc.index, fmtdesc.description);
            found = 1;
            break;
        } 

        fmtdesc.index++;
    }

    if (!found)
    {
        LOGE("unsupported pixel format\n");
        return -1;
    }

    return 0;
}

// To check whether the capability is supported by the camera or not.
static int v4l2_query_cap(int fd, int capabilities)
{
    struct v4l2_capability cap;
    int ret = 0;

    LOGV("v4l2_query_cap()");

    ret = ioctl(fd, VIDIOC_QUERYCAP, &cap);

    if (ret < 0)
    {
        LOGE("v4l2_query_cap():VIDIOC_QUERYCAP failed");
        return -1;
    } 

    if (!(cap.capabilities & capabilities))
    {
        LOGE("v4l2_query_cap(): capability 0x%x not supported by camera.", capabilities);
        return -1;
    }

    return ret;
}

// To query the buffer from v4l2. We do memory map here. 
// This API should be invoked after v4l2_req_bufs().
static int v4l2_query_buf(int fd, struct fimc_buffer *buffer, enum v4l2_buf_type type, int nr_buf)
{
    v4l2_buffer v4l2_buf;
    int ret;
    int i;

    LOGV("v4l2_query_buf()");

    for (i = 0; i < nr_buf; i++)
    {
        memset(&v4l2_buf, 0, sizeof(struct v4l2_buffer) );

        v4l2_buf.type = type;
        v4l2_buf.memory = V4L2_MEMORY_MMAP;
        v4l2_buf.index = i;

        ret = ioctl(fd, VIDIOC_QUERYBUF, &v4l2_buf);
        if (ret < 0)
        {
            LOGE("v4l2_query_buf():VIDIOC_QUERYBUF failed\n");
            return -1;
        } 

        buffer[i].length = v4l2_buf.length;
        buffer[i].start = mmap(0, v4l2_buf.length, PROT_READ | PROT_WRITE, MAP_SHARED, fd, v4l2_buf.m.offset);
        if (MAP_FAILED == buffer[i].start)
        {
            LOGE("v4l2_query_buf(): mmap() failed, buffer[i].start=%p\n", buffer[i].start);
            return -1;
        }

        ret = ioctl(fd, VIDIOC_QBUF, &v4l2_buf);
        if (ret < 0)
        {
            LOGE("v4l2_query_buf(): VIDIOC_QBUF failed\n");
            return ret;
        }
    }

    return 0;
}

// To ask v4l2/uvc to prepare the buffer.
static int v4l2_req_bufs(int fd, enum v4l2_buf_type type, int nr_bufs)
{
    struct v4l2_requestbuffers req;
    int ret;

    LOGV("v4l2_req_bufs()");

    req.count = nr_bufs;
    req.type = type;
    req.memory = V4L2_MEMORY_MMAP;

    ret = ioctl(fd, VIDIOC_REQBUFS, &req);
    if (ret < 0)
    {
        LOGE("v4l2_req_bufs(): VIDIOC_REQBUFS failed\n");
        return -1;
    } 

    return req.count;
}

// Start video streaming.
static int v4l2_stream_on(int fd)
{
    enum v4l2_buf_type type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    int ret;

    LOGV("v4l2_stream_on()");

    ret = ioctl(fd, VIDIOC_STREAMON, &type);
    if (ret < 0)
    {
        LOGE("v4l2_stream_on(): VIDIOC_STREAMON failed\n");
        return ret;
    } 

    return ret;
}

// Stop video streaming.
static int v4l2_stream_off(int fd, struct fimc_buffer *buffer, int nr_buf)
{
    enum v4l2_buf_type type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    int ret, i;

    LOGV("v4l2_stream_off()");

    ret = ioctl(fd, VIDIOC_STREAMOFF, &type);
    if (ret < 0)
    {
        LOGE("v4l2_stream_off(): VIDIOC_STREAMOFF failed\n");
        return ret;
    } 

    for (i = 0; i < nr_buf; i++)
    {
        if (munmap(buffer[i].start, buffer[i].length) == -1)
        {
            LOGE("munmap failed: %p %d\n", buffer[i].start, buffer[i].length);
        }
    }

    return ret;
}

// Enqueue the buffer to V4L2. 
// The V4L2 will retrieve video data from camera and store to the buffer.
static int v4l2_qbuf(int fd, int index)
{
    v4l2_buffer v4l2_buf;
    int ret;

    v4l2_buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    v4l2_buf.memory = V4L2_MEMORY_MMAP;
    v4l2_buf.index = index;

    ret = ioctl(fd, VIDIOC_QBUF, &v4l2_buf);
    if (ret < 0)
    {
        LOGE("v4l2_qbuf(): VIDIOC_QBUF failed\n");
        return ret;
    }

    return 0;
}

// To get the video data.
static int v4l2_dqbuf(int fd)
{
    v4l2_buffer v4l2_buf;
    int ret;

    v4l2_buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    v4l2_buf.memory = V4L2_MEMORY_MMAP;

    ret = ioctl(fd, VIDIOC_DQBUF, &v4l2_buf);
    if (ret < 0)
    {
        LOGE("v4l2_dqbuf(): VIDIOC_DQBUF failed, dropped frame\n");
        return ret;
    }
	
    if ( v4l2_buf.bytesused == 0 )
    {
        // This is MTK proprietary mechanism.
        // bytesused == 0 menas this is a broken frame, we should skip it.
        return MAX_BUFFERS+v4l2_buf.index;
    }

    return v4l2_buf.index;
}

static int get_resolution_string(int width, int height, char* resolution)
{
    int length=1;
    int index=0;
    if ( width >= 1000 && width < 10000 )
    {
        length+=4;
        resolution[index]=(width/1000)+'0';         index++;
        resolution[index]=((width%1000)/100)+'0';   index++;
        resolution[index]=((width%100)/10)+'0';     index++;
        resolution[index]=((width%10))+'0';         index++;
    }
    else if ( width >= 100 && width < 1000 )
    {
        length+=3;
        resolution[index]=(width/100)+'0';      index++;
        resolution[index]=((width%100)/10)+'0'; index++;
        resolution[index]=(width%10)+'0';       index++;
    }
    else if ( width >= 10 && width < 100 )
    {
        length+=2;
        resolution[index]=(width/10)+'0'; index++;
        resolution[index]=(width%10)+'0'; index++;
    }
    else
    {
        length+=1;
        resolution[index]=width+'0'; index++;
    }
    resolution[index]='x'; index++;
    if ( height >= 1000 && height < 10000 )
    {
        length+=4;
        resolution[index]=(height/1000)+'0';         index++;
        resolution[index]=((height%1000)/100)+'0';   index++;
        resolution[index]=((height%100)/10)+'0';     index++;
        resolution[index]=((height%10))+'0';         index++;
    }
    else if ( height >= 100 && height < 1000 )
    {
        length+=3;
        resolution[index]=(height/100)+'0';      index++;
        resolution[index]=((height%100)/10)+'0'; index++;
        resolution[index]=(height%10)+'0';       index++;
    }
    else if ( height >= 10 && height < 100 )
    {
        length+=2;
        resolution[index]=(height/10)+'0'; index++;
        resolution[index]=(height%10)+'0'; index++;
    }
    else
    {
        length+=1;
        resolution[index]=height+'0'; index++;
    }
    return length;
}

// ======================================================================
// Constructor & Destructor

MtkV4L2Camera::MtkV4L2Camera(): m_flag_init(0), 
                                m_cam_fd(-1), 
                                m_preview_v4lformat(V4L2_PIX_FMT_YUYV), 
                                m_preview_width(0), 
                                m_preview_height(0),
                                m_preview_framerate(0),
                                m_flag_camera_start(false), 
                                m_is_yuyv_supported(false),
                                m_camera_support_mjpeg(false), 
                                m_system_support_mjpeg(false),
                                m_mjpeg_tmp_buffer(NULL)
{
    LOGI("MtkV4L2Camera::MtkV4L2Camera(): begin");
    memset(&m_capture_buf, 0, sizeof(m_capture_buf) );
    LOGI("MtkV4L2Camera::MtkV4L2Camera(): end");
}

MtkV4L2Camera::~MtkV4L2Camera()
{
    LOGI("MtkV4L2Camera::~MtkV4L2Camera(): begin and end");
} 

// Just open the camera and confirm the camera supports V4L2_CAP_VIDEO_CAPTURE.
int MtkV4L2Camera::initCamera()
{
    int ret = 0;
    int i;
    LOGV("MtkV4L2Camera::initCamera(): begin");

    if (!m_flag_init)
    {
        char DevName[13]="/dev/video";
        for ( i = 0 ; i < 10 ; i++ )
        {
            DevName[10] = i + '0';
            m_cam_fd = open(DevName, O_RDWR | O_NONBLOCK);
            if (m_cam_fd < 0)
            {
                LOGE("initCamera(): Cannot open %s (error : %s)\n", DevName, strerror(errno) );
                continue;
            }
            LOGV("initCamera(): open(%s) --> m_cam_fd %d", DevName, m_cam_fd);

            ret = v4l2_query_cap(m_cam_fd, V4L2_CAP_VIDEO_CAPTURE);
            CHECK(ret);

            m_flag_init = 1;
            break;
        }

        if ( m_cam_fd < 0 )
        {
            return -1;
        }
    }
    else
    {
        LOGV("Already inited. Do nothing.");
    }
    
    LOGV("MtkV4L2Camera::initCamera(): end");
    return 0;
}

void MtkV4L2Camera::deinitCamera()
{
    int ret;
    LOGV("MtkV4L2Camera::deinitCamera(): begin");

    if (m_flag_init)
    {
        if (m_cam_fd > -1)
        {
            LOGV("deinitCamera: m_cam_fd(%d)", m_cam_fd);
            ret = close(m_cam_fd);
            m_cam_fd = -1;
            CHECK_NR(ret);
        }

        m_flag_init = 0;
    }
    else
    {
        LOGV("Already deinitialized. Do nothing");
    }

    LOGV("MtkV4L2Camera::deinitCamera(): end");
}

void MtkV4L2Camera::resetCamera()
{
    LOGV("MtkV4L2Camera::resetCamera(): begin");
    deinitCamera();
    initCamera();
    LOGV("MtkV4L2Camera::resetCamera(): end");
}

bool MtkV4L2Camera::isCameraExist()
{
    int i;
    int cam_fd;
    char DevName[13]="/dev/video";
    for ( i = 0 ; i < 10 ; i++ )
    {
        DevName[10] = i + '0';
        cam_fd = open(DevName, O_RDWR | O_NONBLOCK);
        if (cam_fd < 0)
        {
            LOGE("isCameraExist(): Cannot open %s (error : %s)\n", DevName, strerror(errno) );
            continue;
        }
//added by wangyoulin
	if (v4l2_s_fmt(cam_fd,640,480,V4L2_PIX_FMT_YUYV)!=0)
	{
		close(cam_fd);
		return false;
	}
//end by wangyoulin
        LOGV("isCameraExist(): Camera exist on %s.", DevName);
        close(cam_fd);
        return true;
    }

    LOGE("isCameraExist(): Camera does not exist!");
    return false;
}

bool MtkV4L2Camera::isCameraSupportingMjpeg()
{
    return m_camera_support_mjpeg;
}

bool MtkV4L2Camera::isPreviewStarted()
{
    return m_flag_camera_start;
}

// To start capture the video from camera.
// Should be invoked after initCamera().
int MtkV4L2Camera::startPreview(void)
{
    int ret;
    LOGI("MtkV4L2Camera::startPreview(): begin (%dx%d@%dfps)", m_preview_width, m_preview_height, m_preview_framerate);

    // aleady started
    if (m_flag_camera_start)
    {
        LOGI("Preview was already started");
        return 0;
    } 

    // camera closed
    if (m_cam_fd <= 0)
    {
        LOGE("Error: Camera was closed!");
        return -1;
    }

    // Preview Size not initialized.
    if ( m_preview_width==0 || m_preview_height==0 || m_preview_v4lformat==-1)
    {
        LOGE("Error: setPreviewSize not invoked!");
        return -1;
    }

    if ( m_system_support_mjpeg && m_camera_support_mjpeg )
    {

    }
    else if ( m_is_yuyv_supported )
    {
        ret = v4l2_query_fmt(m_cam_fd,m_preview_v4lformat);
        CHECK(ret);
        ret = v4l2_s_fmt(m_cam_fd, m_preview_width, m_preview_height, m_preview_v4lformat);
        CHECK(ret);
    }
    else
    {
        LOGE("Both YUYV & MJPEG are not supported by camera!");
        return -1;
    }
    ret = v4l2_req_bufs(m_cam_fd, V4L2_BUF_TYPE_VIDEO_CAPTURE, MAX_BUFFERS);
    CHECK(ret);
    ret = v4l2_query_buf(m_cam_fd, m_capture_buf, V4L2_BUF_TYPE_VIDEO_CAPTURE, MAX_BUFFERS);
    CHECK(ret);
    ret = v4l2_s_parm(m_cam_fd, &m_streamparm);
    CHECK(ret);
    ret = v4l2_stream_on(m_cam_fd);
    CHECK(ret);

    m_flag_camera_start = true;


    LOGI("MtkV4L2Camera::startPreview(): end");

    return 0;
}

// Just stop camera video capturing.
// We don't close camera here. Camera is closed in deinitCamera.
int MtkV4L2Camera::stopPreview(void)
{
    int ret;

    LOGI("MtkV4L2Camera::stopPreview(): begin");

    if (m_flag_camera_start)
    {
        m_flag_camera_start = false;
    }
    else
    {
        LOGW("stopPreview(): doing nothing because camera preview is not started");
        return 0;
    }

    if (m_cam_fd <= 0)
    {
        LOGE("stopPreview(): Camera was closed\n");
        return -1;
    }

    ret = v4l2_stream_off(m_cam_fd, m_capture_buf, MAX_BUFFERS);
    CHECK(ret);

    LOGI("MtkV4L2Camera::stopPreview(): end");
    return ret;
}

// To get one video frame.
int MtkV4L2Camera::getPreview(char** rawdata)
{
    int index;
    int ret;
	fd_set fds;
	struct timeval tv;

	FD_ZERO(&fds);
	FD_SET(m_cam_fd, &fds);
	
	/* Timeout */
	tv.tv_sec = 3;
	tv.tv_usec = 0;
    if (m_flag_camera_start == false)
    {
        LOGE("MtkV4L2Camera::getPreview() Error: MtkV4L2Camera::startPreview not invoked!");
    }

	// confirm buf has data to avoid camer hal lock by ioctl
	ret = select(m_cam_fd + 1, &fds, NULL, NULL, &tv);
	if(-1 == ret)
	{
		LOGE("getPreview() select fail");
		// do not anything.
	}
	else if(0 == ret)
	{
		LOGE("getPreview() select timeout");
		return -2;
	}
    index = v4l2_dqbuf(m_cam_fd);
    if ( index >= MAX_BUFFERS )
    {
        // Here menas the video frame is broken due to iso packet lost.
        *rawdata = NULL;
        return index-MAX_BUFFERS;
    }
    else if (!(0 <= index && index < MAX_BUFFERS) )
    {
        LOGE("getPreview() error: wrong index = %d", index);
        return -1;
    }

    *rawdata = (char*)(m_capture_buf[index].start);

    if ( m_system_support_mjpeg && m_camera_support_mjpeg )
    {

    }
    
    return index;
}

int MtkV4L2Camera::getPreviewDone(int index)
{
    int ret;
    ret = v4l2_qbuf(m_cam_fd, index);
    CHECK(ret);
    return index;
}

int MtkV4L2Camera::getPreviewSize(int *width, int *height, int *frame_size)
{
    *width = m_preview_width;
    *height = m_preview_height; 
    *frame_size = m_preview_framesize;
    return 0;
}

int MtkV4L2Camera::setPreviewSize(int width, int height, int pixel_format)
{
    LOGV("MtkV4L2Camera::setPreviewSize(): begin, width(%d), height(%d), format(0x%x)", width, height, pixel_format);
    
    m_preview_width = width;
    m_preview_height = height;
    m_preview_v4lformat = pixel_format;

    switch (m_preview_v4lformat)
    {
        case V4L2_PIX_FMT_YUV420:
        case V4L2_PIX_FMT_NV12:
        case V4L2_PIX_FMT_NV21:
        case V4L2_PIX_FMT_YUYV:
        case V4L2_PIX_FMT_YVYU:
        case V4L2_PIX_FMT_UYVY:
        case V4L2_PIX_FMT_VYUY:
            m_preview_framesize = (m_preview_width *m_preview_height * 3/2);
            break;

        case V4L2_PIX_FMT_RGB565:
        case V4L2_PIX_FMT_NV16:
        case V4L2_PIX_FMT_NV61:
        case V4L2_PIX_FMT_YUV422P:
            m_preview_framesize = (m_preview_width *m_preview_height * 2);
            break;

        default:
            LOGE("Invalid V4L2 pixel format(0x%x)\n", m_preview_v4lformat);
            break;
            
    } 

    LOGV("MtkV4L2Camera::setPreviewSize(): end");
    return 0;
}

int MtkV4L2Camera::getPreviewPixelFormat(void)
{
    return m_preview_v4lformat;
}

int MtkV4L2Camera::getCameraFd(void)
{
    return m_cam_fd;
}

int MtkV4L2Camera::getFrameRate()
{
    LOGV("MtkV4L2Camera::getFrameRate(): current frame rate is %d", m_preview_framerate);
    return m_preview_framerate;
}

int MtkV4L2Camera::setFrameRate(int frame_rate)
{
    LOGV("MtkV4L2Camera::setFrameRate(%d)", frame_rate);
    m_preview_framerate = frame_rate;
    m_streamparm.parm.capture.timeperframe.numerator = 1;
    m_streamparm.parm.capture.timeperframe.denominator = m_preview_framerate;
    return 0;
}

bool MtkV4L2Camera::getPixelFormat(char* pResolutionList)
{
    bool GotList = false;
    char fourcc[5];
    struct v4l2_fmtdesc fmt;
    struct v4l2_frmsizeenum fsize;
	struct v4l2_frmivalenum fival;
    memset(&fmt, 0, sizeof(fmt));
    memset(&fsize, 0, sizeof(fsize));
    int i=0;
    int index=0;
    char resolution_string[10]={0};
    int resolution_length=0;
    bool deviceOpenedByThisFunction = false;
    bool bIsMjpegSupported = false;
    bool bIsYuyvSupported = false;
    bool bIsSupportedResolution = false;
    enum 
    {
        HANDLE_FORMAT_UNKNOWN, 
        HANDLE_FORMAT_YUYV, 
        HANDLE_FORMAT_MJPG
    };
    int iHandleWhichFmt = HANDLE_FORMAT_UNKNOWN;
    m_camera_support_mjpeg = false;
    m_is_yuyv_supported = false;
    
    LOGV("V4L2Camera::GetPixelFormat\n");

    if ( m_cam_fd == -1 )
    {
        char DevName[13]="/dev/video";
        for ( i = 0 ; i < 10 ; i++ )
        {
            DevName[10] = i + '0';
            m_cam_fd = open(DevName, O_RDWR | O_NONBLOCK);
            if (m_cam_fd < 0)
            {
                LOGE("getPixelFormat(): Cannot open %s (error : %s)\n", DevName, strerror(errno) );
                continue;
            }
            LOGV("getPixelFormat(): open(%s) --> m_cam_fd %d", DevName, m_cam_fd);
            deviceOpenedByThisFunction = true;            
            break;
        }

        if ( m_cam_fd == -1 )
        {
            LOGE("getPixelFormat(): Could not open camera!");
            return false;
        }
    }
    
    // Get supported format    
    fmt.index = 0;
    fmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    while(ioctl(m_cam_fd, VIDIOC_ENUM_FMT, &fmt) == 0) 
    {
        sprintf(fourcc, "%c%c%c%c",
                (fmt.pixelformat) & 0xFF, (fmt.pixelformat >> 8) & 0xFF,
                (fmt.pixelformat >> 16) & 0xFF, (fmt.pixelformat >> 24) & 0xFF);
        if ( (fourcc[0] == 'Y' || fourcc[0] == 'y') &&
             (fourcc[1] == 'U' || fourcc[1] == 'u') &&
             (fourcc[2] == 'Y' || fourcc[2] == 'y') &&
             (fourcc[3] == 'V' || fourcc[3] == 'v') )
        {
            LOGI("    Camera Supported Format : %s", fourcc);
            m_is_yuyv_supported = true;
        }
        else if( (fourcc[0] == 'M' || fourcc[0] == 'm') &&
                 (fourcc[1] == 'J' || fourcc[1] == 'j') &&
                 (fourcc[2] == 'P' || fourcc[2] == 'p') &&
                 (fourcc[3] == 'G' || fourcc[3] == 'g') )
        {
            LOGI("    Camera Supported Format : %s", fourcc);
            m_camera_support_mjpeg = true;
        }

        fmt.index++;
    }
    if ( m_camera_support_mjpeg && m_is_yuyv_supported )
    {
        if ( m_system_support_mjpeg )
            iHandleWhichFmt = HANDLE_FORMAT_MJPG;
        else
            iHandleWhichFmt = HANDLE_FORMAT_YUYV;
    }
    else if ( m_camera_support_mjpeg && !m_is_yuyv_supported )
    {
        if ( m_system_support_mjpeg )
            iHandleWhichFmt = HANDLE_FORMAT_MJPG;
        else
        {
            LOGE("This camera does not support neither YUYV nor MJPEG!");
            iHandleWhichFmt = HANDLE_FORMAT_UNKNOWN;
            return false;
        }
    }
    else if ( !m_camera_support_mjpeg && m_is_yuyv_supported )
    {
        iHandleWhichFmt = HANDLE_FORMAT_YUYV;
    }
    else if ( !m_camera_support_mjpeg && !m_is_yuyv_supported )
    {
        LOGE("This camera does not support neither YUYV nor MJPEG!");
        iHandleWhichFmt = HANDLE_FORMAT_UNKNOWN;
        return false;
    }

    // Get supported resolution
    fmt.index = 0;
    fmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    while(ioctl(m_cam_fd, VIDIOC_ENUM_FMT, &fmt) == 0) 
    {
        bool bShouldHandleFmt = false;
        sprintf(fourcc, "%c%c%c%c",
                (fmt.pixelformat) & 0xFF, (fmt.pixelformat >> 8) & 0xFF,
                (fmt.pixelformat >> 16) & 0xFF, (fmt.pixelformat >> 24) & 0xFF);

        // Get supported resolution
        fsize.index = 0;
        fsize.pixel_format = fourcc[0] | (unsigned long)fourcc[1] << 8 |
            (unsigned long)fourcc[2] << 16 | (unsigned long)fourcc[3] << 24;

        if ( (((fourcc[0] == 'Y' || fourcc[0] == 'y') &&
               (fourcc[1] == 'U' || fourcc[1] == 'u') &&
               (fourcc[2] == 'Y' || fourcc[2] == 'y') &&
               (fourcc[3] == 'V' || fourcc[3] == 'v')) && iHandleWhichFmt==1)||
             (((fourcc[0] == 'M' || fourcc[0] == 'm') &&
               (fourcc[1] == 'J' || fourcc[1] == 'j') &&
               (fourcc[2] == 'P' || fourcc[2] == 'p') &&
               (fourcc[3] == 'G' || fourcc[3] == 'g')) && iHandleWhichFmt==2))
        {
            LOGI("    Selected Camera Format : %s", fourcc);
            while(ioctl(m_cam_fd, VIDIOC_ENUM_FRAMESIZES, &fsize) == 0) 
            {
                if(fsize.type == V4L2_FRMSIZE_TYPE_DISCRETE) 
                {
                	memset(&fival, 0, sizeof(fival));
                	fival.index = 0;
                	fival.pixel_format = fourcc[0] |
                			(unsigned long)fourcc[1] << 8 |
                			(unsigned long)fourcc[2] << 16 |
                			(unsigned long)fourcc[3] << 24;
                	fival.width = fsize.discrete.width;
                	fival.height = fsize.discrete.height;
                	fival.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
                    bIsSupportedResolution = false;
                	while( (ioctl(m_cam_fd, VIDIOC_ENUM_FRAMEINTERVALS, &fival) == 0) && 
                	       (fival.index < 10) ) 
                	{
                        fival.index++;
                        if(fival.type == V4L2_FRMIVAL_TYPE_DISCRETE) 
                        {
                            if ( (fival.discrete.denominator / fival.discrete.numerator) >= MINIMUM_FRAME_RATE )
                            {
                                // We only support resolutions which could be higher than MINIMUM_FRAME_RATE fps.
                                bIsSupportedResolution = true;
                            }
                        }
                        else if(fival.type == V4L2_FRMIVAL_TYPE_CONTINUOUS) 
                        {
                            LOGE("Warning! V4L2_FRMIVAL_TYPE_CONTINUOUS not supported !\n");
                        }
                        else if(fival.type == V4L2_FRMIVAL_TYPE_STEPWISE) 
                        {
                            LOGE("Warning! V4L2_FRMIVAL_TYPE_STEPWISE not supported !\n");
                        }
                	}

                	if ( bIsSupportedResolution )
                	{
                        if ( index != 0 )
                        {
                            pResolutionList[index]=','; index++;
                        }

                        memset(resolution_string, 0, 10);
                        resolution_length = get_resolution_string(fsize.discrete.width, fsize.discrete.height, resolution_string);
                        if ( (index+resolution_length) < MAX_RESOLUTION_STRING_LENGTH)
                        {
                            memcpy(&pResolutionList[index], resolution_string, resolution_length);
                            index+=resolution_length;
                            GotList = true;
                        }
                        else
                        {
                            LOGE("The camera resolution list is too long! (%d > %d)", (index+resolution_length), MAX_RESOLUTION_STRING_LENGTH);
                            LOGE("Camera supported resolution : %s\n", pResolutionList);
                            return GotList;
                        }
                    }
                }
                else if(fsize.type == V4L2_FRMSIZE_TYPE_CONTINUOUS) 
                {
                    LOGE("Warning! V4L2_FRMSIZE_TYPE_CONTINUOUS not supported !\n");
                }
                else if(fsize.type == V4L2_FRMSIZE_TYPE_STEPWISE)
                {
                    LOGE("Warning! V4L2_FRMSIZE_TYPE_CONTINUOUS not supported !\n");
                }
                fsize.index++;
            }
        }
        fmt.index++;
    }

    if ( deviceOpenedByThisFunction )
    {
        close(m_cam_fd);
        m_cam_fd = -1;
    }

    LOGI("Camera supported resolution : %s\n", pResolutionList);
    return GotList;
}

void MtkV4L2Camera::setSystemSupportMjpeg(bool bSystemSupportMjpeg)
{
    m_system_support_mjpeg = bSystemSupportMjpeg;
}


// Y800:
//  Y Planar
//  CbCr is not included
int MtkV4L2Camera::YUYVtoY800(unsigned char *bufsrc, unsigned char *bufdest, int width, int height)
{
    unsigned char *ptrsrcy1, *ptrsrcy2;
    unsigned char *ptrsrcy3, *ptrsrcy4;
    int srcystride;

    ptrsrcy1  = bufsrc ;
    ptrsrcy2  = bufsrc + (width<<1) ;
    ptrsrcy3  = bufsrc + (width<<1)*2 ;
    ptrsrcy4  = bufsrc + (width<<1)*3 ;

    srcystride  = (width<<1)*3;

    unsigned char *ptrdesty1, *ptrdesty2;
    unsigned char *ptrdesty3, *ptrdesty4;
    int destystride;

    ptrdesty1 = bufdest;
    ptrdesty2 = bufdest + width;
    ptrdesty3 = bufdest + width*2;
    ptrdesty4 = bufdest + width*3;

    destystride  = (width)*3;

    int i, j;

    for(j=0; j<(height/4); j++)
    {
        for(i=0;i<(width/2);i++)
        {
            (*ptrdesty1++) = (*ptrsrcy1);
            (*ptrdesty2++) = (*ptrsrcy2);
            (*ptrdesty3++) = (*ptrsrcy3);
            (*ptrdesty4++) = (*ptrsrcy4);

            ptrsrcy1 += 2;
            ptrsrcy2 += 2;
            ptrsrcy3 += 2;
            ptrsrcy4 += 2;

            (*ptrdesty1++) = (*ptrsrcy1);
            (*ptrdesty2++) = (*ptrsrcy2);
            (*ptrdesty3++) = (*ptrsrcy3);
            (*ptrdesty4++) = (*ptrsrcy4);

            ptrsrcy1 += 2;
            ptrsrcy2 += 2;
            ptrsrcy3 += 2;
            ptrsrcy4 += 2;
        }


        /* Update src pointers */
        ptrsrcy1  += srcystride;
        ptrsrcy2  += srcystride;
        ptrsrcy3  += srcystride;
        ptrsrcy4  += srcystride;

        /* Update dest pointers */
        ptrdesty1 += destystride;
        ptrdesty2 += destystride;
        ptrdesty3 += destystride;
        ptrdesty4 += destystride;
    }
    return 0;
}

// NV12: 
//  Y Planar
//  CbCr interlaced
int MtkV4L2Camera::YUYVtoNV12(unsigned char *bufsrc, unsigned char *bufdest, int width, int height)
{
    unsigned char *ptrsrcy1, *ptrsrcy2;
    unsigned char *ptrsrcy3, *ptrsrcy4;
    unsigned char *ptrsrccb1, *ptrsrccb2;
    unsigned char *ptrsrccb3, *ptrsrccb4;
    unsigned char *ptrsrccr1, *ptrsrccr2;
    unsigned char *ptrsrccr3, *ptrsrccr4;
    int srcystride, srcccstride;

    ptrsrcy1  = bufsrc ;
    ptrsrcy2  = bufsrc + (width<<1) ;
    ptrsrcy3  = bufsrc + (width<<1)*2 ;
    ptrsrcy4  = bufsrc + (width<<1)*3 ;

    ptrsrccb1 = bufsrc + 1;
    ptrsrccb2 = bufsrc + (width<<1) + 1;
    ptrsrccb3 = bufsrc + (width<<1)*2 + 1;
    ptrsrccb4 = bufsrc + (width<<1)*3 + 1;

    ptrsrccr1 = bufsrc + 3;
    ptrsrccr2 = bufsrc + (width<<1) + 3;
    ptrsrccr3 = bufsrc + (width<<1)*2 + 3;
    ptrsrccr4 = bufsrc + (width<<1)*3 + 3;

    srcystride  = (width<<1)*3;
    srcccstride = (width<<1)*3;

    unsigned char *ptrdesty1, *ptrdesty2;
    unsigned char *ptrdesty3, *ptrdesty4;
    unsigned char *ptrdestcb1, *ptrdestcb2;
    unsigned char *ptrdestcr1, *ptrdestcr2;
    int destystride, destccstride;

    ptrdesty1 = bufdest;
    ptrdesty2 = bufdest + width;
    ptrdesty3 = bufdest + width*2;
    ptrdesty4 = bufdest + width*3;

    ptrdestcb1 = bufdest + width*height;
    ptrdestcb2 = bufdest + width*height + width;

    ptrdestcr1 = bufdest + width*height + 1;
    ptrdestcr2 = bufdest + width*height + width + 1;

    destystride  = (width)*3;
    destccstride = width;

    int i, j;

    for(j=0; j<(height/4); j++)
    {
        for(i=0;i<(width/2);i++)
        {
            (*ptrdesty1++) = (*ptrsrcy1);
            (*ptrdesty2++) = (*ptrsrcy2);
            (*ptrdesty3++) = (*ptrsrcy3);
            (*ptrdesty4++) = (*ptrsrcy4);

            ptrsrcy1 += 2;
            ptrsrcy2 += 2;
            ptrsrcy3 += 2;
            ptrsrcy4 += 2;

            (*ptrdesty1++) = (*ptrsrcy1);
            (*ptrdesty2++) = (*ptrsrcy2);
            (*ptrdesty3++) = (*ptrsrcy3);
            (*ptrdesty4++) = (*ptrsrcy4);

            ptrsrcy1 += 2;
            ptrsrcy2 += 2;
            ptrsrcy3 += 2;
            ptrsrcy4 += 2;

            (*ptrdestcb1) = (*ptrsrccb1);
            (*ptrdestcb2) = (*ptrsrccb3);
            ptrdestcb1 += 2;
            ptrdestcb2 += 2;

            ptrsrccb1 += 4;
            ptrsrccb3 += 4;

            (*ptrdestcr1) = (*ptrsrccr1);
            (*ptrdestcr2) = (*ptrsrccr3);
            ptrdestcr1 += 2;
            ptrdestcr2 += 2;

            ptrsrccr1 += 4;
            ptrsrccr3 += 4;

        }


        /* Update src pointers */
        ptrsrcy1  += srcystride;
        ptrsrcy2  += srcystride;
        ptrsrcy3  += srcystride;
        ptrsrcy4  += srcystride;

        ptrsrccb1 += srcccstride;
        ptrsrccb3 += srcccstride;

        ptrsrccr1 += srcccstride;
        ptrsrccr3 += srcccstride;


        /* Update dest pointers */
        ptrdesty1 += destystride;
        ptrdesty2 += destystride;
        ptrdesty3 += destystride;
        ptrdesty4 += destystride;

        ptrdestcb1 += destccstride;
        ptrdestcb2 += destccstride;

        ptrdestcr1 += destccstride;
        ptrdestcr2 += destccstride;
    }
    return 0;
}

// NV21 (HAL_PIXEL_FORMAT_YCrCb_420_SP): 
//  Y Planar
//  CrCb interlaced
int MtkV4L2Camera::YUYVtoNV21(unsigned char *bufsrc, unsigned char *bufdest, int width, int height)
{
    unsigned char *ptrsrcy1, *ptrsrcy2;
    unsigned char *ptrsrcy3, *ptrsrcy4;
    unsigned char *ptrsrccb1, *ptrsrccb2;
    unsigned char *ptrsrccb3, *ptrsrccb4;
    unsigned char *ptrsrccr1, *ptrsrccr2;
    unsigned char *ptrsrccr3, *ptrsrccr4;
    int srcystride, srcccstride;

    ptrsrcy1  = bufsrc ;
    ptrsrcy2  = bufsrc + (width<<1) ;
    ptrsrcy3  = bufsrc + (width<<1)*2 ;
    ptrsrcy4  = bufsrc + (width<<1)*3 ;

    ptrsrccb1 = bufsrc + 1;
    ptrsrccb2 = bufsrc + (width<<1) + 1;
    ptrsrccb3 = bufsrc + (width<<1)*2 + 1;
    ptrsrccb4 = bufsrc + (width<<1)*3 + 1;

    ptrsrccr1 = bufsrc + 3;
    ptrsrccr2 = bufsrc + (width<<1) + 3;
    ptrsrccr3 = bufsrc + (width<<1)*2 + 3;
    ptrsrccr4 = bufsrc + (width<<1)*3 + 3;

    srcystride  = (width<<1)*3;
    srcccstride = (width<<1)*3;

    unsigned char *ptrdesty1, *ptrdesty2;
    unsigned char *ptrdesty3, *ptrdesty4;
    unsigned char *ptrdestcb1, *ptrdestcb2;
    unsigned char *ptrdestcr1, *ptrdestcr2;
    int destystride, destccstride;

    ptrdesty1 = bufdest;
    ptrdesty2 = bufdest + width;
    ptrdesty3 = bufdest + width*2;
    ptrdesty4 = bufdest + width*3;

    ptrdestcb1 = bufdest + width*height;
    ptrdestcb2 = bufdest + width*height + width;

    ptrdestcr1 = bufdest + width*height + 1;
    ptrdestcr2 = bufdest + width*height + width + 1;

    destystride  = (width)*3;
    destccstride = width;

    int i, j;

    for(j=0; j<(height/4); j++)
    {
        for(i=0;i<(width/2);i++)
        {
            (*ptrdesty1++) = (*ptrsrcy1);
            (*ptrdesty2++) = (*ptrsrcy2);
            (*ptrdesty3++) = (*ptrsrcy3);
            (*ptrdesty4++) = (*ptrsrcy4);

            ptrsrcy1 += 2;
            ptrsrcy2 += 2;
            ptrsrcy3 += 2;
            ptrsrcy4 += 2;

            (*ptrdesty1++) = (*ptrsrcy1);
            (*ptrdesty2++) = (*ptrsrcy2);
            (*ptrdesty3++) = (*ptrsrcy3);
            (*ptrdesty4++) = (*ptrsrcy4);

            ptrsrcy1 += 2;
            ptrsrcy2 += 2;
            ptrsrcy3 += 2;
            ptrsrcy4 += 2;

            (*ptrdestcr1) = (*ptrsrccb1);
            (*ptrdestcr2) = (*ptrsrccb3);
            ptrdestcr1 += 2;
            ptrdestcr2 += 2;

            ptrsrccb1 += 4;
            ptrsrccb3 += 4;

            (*ptrdestcb1) = (*ptrsrccr1);
            (*ptrdestcb2) = (*ptrsrccr3);
            ptrdestcb1 += 2;
            ptrdestcb2 += 2;

            ptrsrccr1 += 4;
            ptrsrccr3 += 4;

        }


        /* Update src pointers */
        ptrsrcy1  += srcystride;
        ptrsrcy2  += srcystride;
        ptrsrcy3  += srcystride;
        ptrsrcy4  += srcystride;

        ptrsrccb1 += srcccstride;
        ptrsrccb3 += srcccstride;

        ptrsrccr1 += srcccstride;
        ptrsrccr3 += srcccstride;


        /* Update dest pointers */
        ptrdesty1 += destystride;
        ptrdesty2 += destystride;
        ptrdesty3 += destystride;
        ptrdesty4 += destystride;

        ptrdestcb1 += destccstride;
        ptrdestcb2 += destccstride;

        ptrdestcr1 += destccstride;
        ptrdestcr2 += destccstride;

    }
    return 0;
}

// YV12 (HAL_PIXEL_FORMAT_YV12): 
//  Y Planar
//  Cr Planar
//  Cb Planar
//  It's also called YV12
int MtkV4L2Camera::YUYVtoYV12(unsigned char *bufsrc, unsigned char *bufdest, int width, int height)
{
    unsigned char *ptrsrcy1,  *ptrsrcy2;
    unsigned char *ptrsrcy3,  *ptrsrcy4;
    unsigned char *ptrsrccb1,  *ptrsrccb2;
    unsigned char *ptrsrccb3,  *ptrsrccb4;
    unsigned char *ptrsrccr1,  *ptrsrccr2;
    unsigned char *ptrsrccr3,  *ptrsrccr4;
    int srcystride, srcccstride;

    ptrsrcy1 = bufsrc;
    ptrsrcy2 = bufsrc+(width << 1);
    ptrsrcy3 = bufsrc+(width << 1) *2;
    ptrsrcy4 = bufsrc+(width << 1) *3;

    ptrsrccb1 = bufsrc+1;
    ptrsrccb2 = bufsrc+(width << 1)+1;
    ptrsrccb3 = bufsrc+(width << 1) *2+1;
    ptrsrccb4 = bufsrc+(width << 1) *3+1;

    ptrsrccr1 = bufsrc+3;
    ptrsrccr2 = bufsrc+(width << 1)+3;
    ptrsrccr3 = bufsrc+(width << 1) *2+3;
    ptrsrccr4 = bufsrc+(width << 1) *3+3;

    srcystride = (width << 1) *3;
    srcccstride = (width << 1) *3;

    unsigned char *ptrdesty1,  *ptrdesty2;
    unsigned char *ptrdesty3,  *ptrdesty4;
    unsigned char *ptrdestcb1,  *ptrdestcb2;
    unsigned char *ptrdestcr1,  *ptrdestcr2;
    int destystride, destccstride;

    ptrdesty1 = bufdest;
    ptrdesty2 = bufdest+width;
    ptrdesty3 = bufdest+width * 2;
    ptrdesty4 = bufdest+width * 3;

    ptrdestcb1 = bufdest+width * height;
    ptrdestcb2 = bufdest+width * height+(width >> 1);

    ptrdestcr1 = bufdest+width * height+( (width *height) >> 2);
    ptrdestcr2 = bufdest+width * height+( (width *height) >> 2)+(width >> 1);

    destystride = (width) *3;
    destccstride = (width >> 1);

    int i, j;

    for (j = 0; j < (height/4); j++)
    {
        for (i = 0; i < (width/2); i++)
        {
            (*ptrdesty1++) = (*ptrsrcy1);
            (*ptrdesty2++) = (*ptrsrcy2);
            (*ptrdesty3++) = (*ptrsrcy3);
            (*ptrdesty4++) = (*ptrsrcy4);

            ptrsrcy1 += 2;
            ptrsrcy2 += 2;
            ptrsrcy3 += 2;
            ptrsrcy4 += 2;

            (*ptrdesty1++) = (*ptrsrcy1);
            (*ptrdesty2++) = (*ptrsrcy2);
            (*ptrdesty3++) = (*ptrsrcy3);
            (*ptrdesty4++) = (*ptrsrcy4);

            ptrsrcy1 += 2;
            ptrsrcy2 += 2;
            ptrsrcy3 += 2;
            ptrsrcy4 += 2;

            (*ptrdestcr1++) = (*ptrsrccb1);
            (*ptrdestcr2++) = (*ptrsrccb3);

            ptrsrccb1 += 4;
            ptrsrccb3 += 4;

            (*ptrdestcb1++) = (*ptrsrccr1);
            (*ptrdestcb2++) = (*ptrsrccr3);

            ptrsrccr1 += 4;
            ptrsrccr3 += 4;

        }


        /* Update src pointers */
        ptrsrcy1 += srcystride;
        ptrsrcy2 += srcystride;
        ptrsrcy3 += srcystride;
        ptrsrcy4 += srcystride;

        ptrsrccb1 += srcccstride;
        ptrsrccb3 += srcccstride;

        ptrsrccr1 += srcccstride;
        ptrsrccr3 += srcccstride;


        /* Update dest pointers */
        ptrdesty1 += destystride;
        ptrdesty2 += destystride;
        ptrdesty3 += destystride;
        ptrdesty4 += destystride;

        ptrdestcb1 += destccstride;
        ptrdestcb2 += destccstride;

        ptrdestcr1 += destccstride;
        ptrdestcr2 += destccstride;

    }
    return 0;
}

int MtkV4L2Camera::YUYVtoJpegFile(unsigned char *inputBuffer, unsigned char *outputBuffer, int width, int height)
{
    #define JPEG_TMPFILE   "/tmp/tmp.jpg"
    FILE *output_fd, *input_fd;
    struct jpeg_compress_struct cinfo;
    struct jpeg_error_mgr jerr;
    JSAMPROW row_pointer[1];
    unsigned char *line_buffer, *yuyv;
    int z;
    int fileSize;


    output_fd= fopen(JPEG_TMPFILE, "wb");
    if (output_fd == NULL) 
    {
        LOGE("MtkV4L2Camera::YUYVtoJpegFile(): Failed to open tmp file: %s!\n", JPEG_TMPFILE);
        return 0;
    }

    line_buffer = (unsigned char *) calloc (width * 3, 1);
    yuyv = inputBuffer;

    cinfo.err = jpeg_std_error (&jerr);
    jpeg_create_compress (&cinfo);
    jpeg_stdio_dest (&cinfo, output_fd);

    cinfo.image_width = width;
    cinfo.image_height = height;
    cinfo.input_components = 3;
    cinfo.in_color_space = JCS_RGB;

    jpeg_set_defaults (&cinfo);
    jpeg_set_quality (&cinfo, 100, TRUE);

    jpeg_start_compress (&cinfo, TRUE);

    z = 0;
    while (cinfo.next_scanline < cinfo.image_height) {
        int x;
        unsigned char *ptr = line_buffer;

        for (x = 0; x < width; x++) {
            int r, g, b;
            int y, u, v;

            if (!z)
                y = yuyv[0] << 8;
            else
                y = yuyv[2] << 8;

            u = yuyv[1] - 128;
            v = yuyv[3] - 128;

            r = (y + (359 * v)) >> 8;
            g = (y - (88 * u) - (183 * v)) >> 8;
            b = (y + (454 * u)) >> 8;

            *(ptr++) = (r > 255) ? 255 : ((r < 0) ? 0 : r);
            *(ptr++) = (g > 255) ? 255 : ((g < 0) ? 0 : g);
            *(ptr++) = (b > 255) ? 255 : ((b < 0) ? 0 : b);

            if (z++) {
                z = 0;
                yuyv += 4;
            }
        }

        row_pointer[0] = line_buffer;
        jpeg_write_scanlines (&cinfo, row_pointer, 1);
    }

    jpeg_finish_compress (&cinfo);
    fileSize = ftell(output_fd);
    jpeg_destroy_compress (&cinfo);
    free (line_buffer);
    fclose(output_fd);


    input_fd= fopen(JPEG_TMPFILE, "rb");
    if (input_fd == NULL) 
    {
        LOGE("MtkV4L2Camera::YUYVtoJpegFile(): Failed to open tmp file: %s!\n", JPEG_TMPFILE);
        return 0;
    }
    fread(outputBuffer, 1, fileSize, input_fd);
    fclose(input_fd);
    
    return fileSize;
}

// NV16 : 
//  Similar to NV21 but is 422 not 420.
//  Y Planar
//  CbCr interlaced
//
// NV21 (HAL_PIXEL_FORMAT_YCrCb_420_SP): 
//  Y Planar
//  CrCb interlaced
int MtkV4L2Camera::NV16toNV21(unsigned char *bufsrc, unsigned char *bufdest, int width, int height)
{
    unsigned char *ptrsrcy1, *ptrsrcy2;
    unsigned char *ptrsrcy3, *ptrsrcy4;
    unsigned char *ptrsrccb1, *ptrsrccb2;
    unsigned char *ptrsrccb3, *ptrsrccb4;
    unsigned char *ptrsrccr1, *ptrsrccr2;
    unsigned char *ptrsrccr3, *ptrsrccr4;
    int srcystride, srcccstride;

    ptrsrcy1  = bufsrc ;
    ptrsrcy2  = bufsrc + width ;
    ptrsrcy3  = bufsrc + width*2 ;
    ptrsrcy4  = bufsrc + width*3 ;

    ptrsrccb1 = bufsrc + (width*height);
    ptrsrccb2 = bufsrc + (width*height) + width;
    ptrsrccb3 = bufsrc + (width*height) + width*2;
    ptrsrccb4 = bufsrc + (width*height) + width*3;

    ptrsrccr1 = bufsrc + (width*height) + 1;
    ptrsrccr2 = bufsrc + (width*height) + width + 1;
    ptrsrccr3 = bufsrc + (width*height) + width*2 + 1;
    ptrsrccr4 = bufsrc + (width*height) + width*3 + 1;

    srcystride  = width*3;
    srcccstride = width*3;

    unsigned char *ptrdesty1, *ptrdesty2;
    unsigned char *ptrdesty3, *ptrdesty4;
    unsigned char *ptrdestcb1, *ptrdestcb2;
    unsigned char *ptrdestcr1, *ptrdestcr2;
    int destystride, destccstride;

    ptrdesty1 = bufdest;
    ptrdesty2 = bufdest + width;
    ptrdesty3 = bufdest + width*2;
    ptrdesty4 = bufdest + width*3;

    ptrdestcb1 = bufdest + (width*height);
    ptrdestcb2 = bufdest + (width*height) + width;

    ptrdestcr1 = bufdest + (width*height) + 1;
    ptrdestcr2 = bufdest + (width*height) + width + 1;

    destystride  = (width)*3;
    destccstride = width;

    int i, j;

    for(j=0; j<(height/4); j++)
    {
        for(i=0;i<(width/2);i++)
        {
            (*ptrdesty1++) = (*ptrsrcy1);
            (*ptrdesty2++) = (*ptrsrcy2);
            (*ptrdesty3++) = (*ptrsrcy3);
            (*ptrdesty4++) = (*ptrsrcy4);

            ptrsrcy1 += 1;
            ptrsrcy2 += 1;
            ptrsrcy3 += 1;
            ptrsrcy4 += 1;

            (*ptrdesty1++) = (*ptrsrcy1);
            (*ptrdesty2++) = (*ptrsrcy2);
            (*ptrdesty3++) = (*ptrsrcy3);
            (*ptrdesty4++) = (*ptrsrcy4);

            ptrsrcy1 += 1;
            ptrsrcy2 += 1;
            ptrsrcy3 += 1;
            ptrsrcy4 += 1;

            (*ptrdestcr1) = (*ptrsrccb1);
            (*ptrdestcr2) = (*ptrsrccb3);
            ptrdestcr1 += 2;
            ptrdestcr2 += 2;

            ptrsrccb1 += 2;
            ptrsrccb3 += 2;

            (*ptrdestcb1) = (*ptrsrccr1);
            (*ptrdestcb2) = (*ptrsrccr3);
            ptrdestcb1 += 2;
            ptrdestcb2 += 2;

            ptrsrccr1 += 2;
            ptrsrccr3 += 2;

        }


        /* Update src pointers */
        ptrsrcy1  += srcystride;
        ptrsrcy2  += srcystride;
        ptrsrcy3  += srcystride;
        ptrsrcy4  += srcystride;

        ptrsrccb1 += srcccstride;
        ptrsrccb3 += srcccstride;

        ptrsrccr1 += srcccstride;
        ptrsrccr3 += srcccstride;


        /* Update dest pointers */
        ptrdesty1 += destystride;
        ptrdesty2 += destystride;
        ptrdesty3 += destystride;
        ptrdesty4 += destystride;

        ptrdestcb1 += destccstride;
        ptrdestcb2 += destccstride;

        ptrdestcr1 += destccstride;
        ptrdestcr2 += destccstride;
    }
    return 0;
}

// Y800:
//  Y Planar
//  CbCr is not included
int MtkV4L2Camera::NV16toY800(unsigned char *bufsrc, unsigned char *bufdest, int width, int height)
{
    unsigned char *ptrsrcy1, *ptrsrcy2;
    unsigned char *ptrsrcy3, *ptrsrcy4;
    int srcystride;

    ptrsrcy1  = bufsrc ;
    ptrsrcy2  = bufsrc + width ;
    ptrsrcy3  = bufsrc + width*2 ;
    ptrsrcy4  = bufsrc + width*3 ;

    srcystride  = width*3;

    unsigned char *ptrdesty1, *ptrdesty2;
    unsigned char *ptrdesty3, *ptrdesty4;
    int destystride;

    ptrdesty1 = bufdest;
    ptrdesty2 = bufdest + width;
    ptrdesty3 = bufdest + width*2;
    ptrdesty4 = bufdest + width*3;

    destystride  = (width)*3;

    int i, j;

    for(j=0; j<(height/4); j++)
    {
        for(i=0;i<(width/2);i++)
        {
            (*ptrdesty1++) = (*ptrsrcy1);
            (*ptrdesty2++) = (*ptrsrcy2);
            (*ptrdesty3++) = (*ptrsrcy3);
            (*ptrdesty4++) = (*ptrsrcy4);

            ptrsrcy1 += 1;
            ptrsrcy2 += 1;
            ptrsrcy3 += 1;
            ptrsrcy4 += 1;

            (*ptrdesty1++) = (*ptrsrcy1);
            (*ptrdesty2++) = (*ptrsrcy2);
            (*ptrdesty3++) = (*ptrsrcy3);
            (*ptrdesty4++) = (*ptrsrcy4);

            ptrsrcy1 += 1;
            ptrsrcy2 += 1;
            ptrsrcy3 += 1;
            ptrsrcy4 += 1;
        }


        /* Update src pointers */
        ptrsrcy1  += srcystride;
        ptrsrcy2  += srcystride;
        ptrsrcy3  += srcystride;
        ptrsrcy4  += srcystride;


        /* Update dest pointers */
        ptrdesty1 += destystride;
        ptrdesty2 += destystride;
        ptrdesty3 += destystride;
        ptrdesty4 += destystride;
    }
    return 0;
}

int MtkV4L2Camera::NV16toJpegFile(unsigned char *inputBuffer, unsigned char *outputBuffer, int width, int height)
{
    #define JPEG_TMPFILE   "/tmp/tmp.jpg"
    FILE *output_fd, *input_fd;
    struct jpeg_compress_struct cinfo;
    struct jpeg_error_mgr jerr;
    JSAMPROW row_pointer[1];
    unsigned char *line_buffer, *yuyv;
    int z;
    int fileSize;
    int iPixelNum = width*height;


    output_fd= fopen(JPEG_TMPFILE, "wb");
    if (output_fd == NULL) 
    {
        LOGE("MtkV4L2Camera::YUYVtoJpegFile(): Failed to open tmp file: %s!\n", JPEG_TMPFILE);
        return 0;
    }

    line_buffer = (unsigned char *) calloc (width * 3, 1);
    yuyv = inputBuffer;

    cinfo.err = jpeg_std_error (&jerr);
    jpeg_create_compress (&cinfo);
    jpeg_stdio_dest (&cinfo, output_fd);

    cinfo.image_width = width;
    cinfo.image_height = height;
    cinfo.input_components = 3;
    cinfo.in_color_space = JCS_RGB;

    jpeg_set_defaults (&cinfo);
    jpeg_set_quality (&cinfo, 100, TRUE);

    jpeg_start_compress (&cinfo, TRUE);

    z = 0;
    while (cinfo.next_scanline < cinfo.image_height) 
    {
        int x;
        unsigned char *ptr = line_buffer;

        for (x = 0; x < width; x++) 
        {
            int r, g, b;
            int y, u, v;

            if (!z)
                y = yuyv[0] << 8;
            else
                y = yuyv[1] << 8;

            u = (yuyv+iPixelNum)[0] - 128;
            v = (yuyv+iPixelNum)[1] - 128;

            r = (y + (359 * v)) >> 8;
            g = (y - (88 * u) - (183 * v)) >> 8;
            b = (y + (454 * u)) >> 8;

            *(ptr++) = (r > 255) ? 255 : ((r < 0) ? 0 : r);
            *(ptr++) = (g > 255) ? 255 : ((g < 0) ? 0 : g);
            *(ptr++) = (b > 255) ? 255 : ((b < 0) ? 0 : b);

            if (z++) {
                z = 0;
                yuyv += 2;
            }
        }

        row_pointer[0] = line_buffer;
        jpeg_write_scanlines (&cinfo, row_pointer, 1);
    }

    jpeg_finish_compress (&cinfo);
    fileSize = ftell(output_fd);
    jpeg_destroy_compress (&cinfo);
    free (line_buffer);
    fclose(output_fd);


    input_fd= fopen(JPEG_TMPFILE, "rb");
    if (input_fd == NULL) 
    {
        LOGE("MtkV4L2Camera::YUYVtoJpegFile(): Failed to open tmp file: %s!\n", JPEG_TMPFILE);
        return 0;
    }
    fread(outputBuffer, 1, fileSize, input_fd);
    fclose(input_fd);
    
    return fileSize;
}



//added by wang youlin
/* return >= 0 ok otherwhise -1 */
int MtkV4L2Camera::v4l2QueryControl(int control, struct v4l2_queryctrl *queryctrl)
{
int err =0;
    queryctrl->id = control;
    if ((err= ioctl(m_cam_fd, VIDIOC_QUERYCTRL, queryctrl)) < 0) {
	LOGE("ioctl querycontrol error %d,%d \n",errno,control);
    } else if (queryctrl->flags & V4L2_CTRL_FLAG_DISABLED) {
	LOGE("control %s disabled \n", (char *) queryctrl->name);
    } else if (queryctrl->flags & V4L2_CTRL_TYPE_BOOLEAN) {
	return 1;
    } else if (queryctrl->type & V4L2_CTRL_TYPE_INTEGER) {
	return 0;
    } else {
	LOGE("contol %s unsupported  \n", (char *) queryctrl->name);
    }
    return -1;
}

int MtkV4L2Camera::v4l2GetControl(int control)
{
    struct v4l2_queryctrl queryctrl;
    struct v4l2_control control_s;
    int err;
    if (v4l2QueryControl(control, &queryctrl) < 0)
	return -1;
//  		LOGE("v4l2GetControl@@@@****************@@[%d][%d][%d]",queryctrl.maximum,queryctrl.minimum,queryctrl.step);
   control_s.id = control;
    if ((err = ioctl(m_cam_fd, VIDIOC_G_CTRL, &control_s)) < 0) {
	LOGE("ioctl get control error\n");
	return -1;
    }
    return control_s.value;
}

int MtkV4L2Camera::v4l2SetControl(int control, int value)
{
    struct v4l2_control control_s;
    struct v4l2_queryctrl queryctrl;
    int min, max, step, val_def;
    int err;
    if (v4l2QueryControl(control, &queryctrl) < 0)
	return -1;
    min = queryctrl.minimum;
    max = queryctrl.maximum;
    step = queryctrl.step;
    val_def = queryctrl.default_value;
    if ((value >= min) && (value <= max)) {
	control_s.id = control;
	control_s.value = value;
	if ((err = ioctl(m_cam_fd, VIDIOC_S_CTRL, &control_s)) < 0) {
	    LOGE("ioctl set control error\n");
	    return -1;
	}
    }
    return 0;
}

int MtkV4L2Camera::ifCameraOpened()
{
       if ( m_cam_fd <= 0 )
        {
            return -1;
        }else  return 1;
	   
}

int MtkV4L2Camera::YUYVtoJPEGInterpolationTo3264x2448(unsigned char *inputBuffer, unsigned char *outputBuffer, int width, int height)
{
    #define JPEG_TMPFILE   "/tmp/tmp.jpg"
    FILE *output_fd, *input_fd;
    struct jpeg_compress_struct cinfo;
    struct jpeg_error_mgr jerr;
    JSAMPROW row_pointer[1];
    unsigned char *line_buffer, *yuyv,*inputbuffercatch;
	unsigned char *ptr;
    int z;
    int fileSize;


    output_fd= fopen(JPEG_TMPFILE, "wb");
    if (output_fd == NULL) 
    {
        LOGE("YUYVtoJpegFile(): Failed to open tmp file: %s!", JPEG_TMPFILE);
        return 0;
    }

	line_buffer = (unsigned char *) calloc (3264 * 3, 1);
	inputbuffercatch= (unsigned char *) calloc (640*480 * 2+10, 1);

	yuyv = inputbuffercatch;
	{
		unsigned char *inbuf, *catchbuf;
		inbuf=inputBuffer;
		catchbuf=inputbuffercatch;
		for  (int i=0; i<640*480*2; i++)
		{
			*catchbuf++=*inbuf++;
		}
	}

    cinfo.err = jpeg_std_error (&jerr);
    jpeg_create_compress (&cinfo);
    jpeg_stdio_dest (&cinfo, output_fd);

	cinfo.image_width = 3264;
	cinfo.image_height = 2448;
    cinfo.input_components = 3;
    cinfo.in_color_space = JCS_RGB;

    jpeg_set_defaults (&cinfo);
    jpeg_set_quality (&cinfo, 100, TRUE);

    jpeg_start_compress (&cinfo, TRUE);

	int i,j,ratex,ratey=0;
//	ALOGE("camerautil@@@@@img640x480to3264x2448");
	while (cinfo.next_scanline < cinfo.image_height) 
	{

		if ((ratey-=10)<0)
		{
//			LOGE("img640x480to3264x2448*1 *[%d]",ratey);
			ratey+=51;
			
			ratex=0;
			ptr=line_buffer;
			z = 0;
			for (j=0; j<3264; j++)
			{
				if ((ratex-=10)<0)
				{
//if (j>3255)			LOGE("img640x480to3264x2448*2  *[%d][%d]y:[%d]j:[%d]",ptr-line_buffer,ratex,cinfo.next_scanline,j);
					ratex+=51;
					{
						int r, g, b;
						int y, u, v;

						if (!z)
						    y = yuyv[0] << 8;
						else
						    y = yuyv[2] << 8;

						u = yuyv[1] - 128;
						v = yuyv[3] - 128;

						r = (y + (359 * v)) >> 8;
						g = (y - (88 * u) - (183 * v)) >> 8;
						b = (y + (454 * u)) >> 8;

						*(ptr++) = (r > 255) ? 255 : ((r < 0) ? 0 : r);
						*(ptr++) = (g > 255) ? 255 : ((g < 0) ? 0 : g);
						*(ptr++) = (b > 255) ? 255 : ((b < 0) ? 0 : b);

						if (z++) 
						{
							z = 0;
							yuyv += 4;
						}
					}
				}else
				{
//if (j>3255)			ALOGE("img640x480to3264x2448*3                                     *[%d][%d]j:[%d]",ptr-line_buffer,ratex,j);
					register unsigned char rgbchar;
					rgbchar=*(ptr-3);
					*(ptr++)=rgbchar;
					rgbchar=*(ptr-3);
					*(ptr++)=rgbchar;
					rgbchar=*(ptr-3);
					*(ptr++)=rgbchar;
				}

			}
		}else
		{
//			ALOGE("img640x480to3264x2448*4  *[%d][%d]",ptr-line_buffer,cinfo.next_scanline);
		}

//if(cinfo.next_scanline>2445)			ALOGE("img640x480to3264x2448*5  *[%d][%d]",ptr-line_buffer,cinfo.next_scanline);
		row_pointer[0] = line_buffer;
		jpeg_write_scanlines (&cinfo, row_pointer, 1);
    }

    jpeg_finish_compress (&cinfo);
    fileSize = ftell(output_fd);
    jpeg_destroy_compress (&cinfo);
    free (line_buffer);
    free (inputbuffercatch);
    fclose(output_fd);


    input_fd= fopen(JPEG_TMPFILE, "rb");
    if (input_fd == NULL) 
    {
        LOGE("YUYVtoJpegFile(): Failed to open tmp file: %s!", JPEG_TMPFILE);
        return 0;
    }
    fread(outputBuffer, 1, fileSize, input_fd);
    fclose(input_fd);
    
    return fileSize;
}

//end by wang youlin



}; // namespace android
