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
#define LOG_TAG "MtkCameraHal"
#include <utils/Log.h>


#include "MtkCameraHal.h"
#include <fcntl.h>
#include <utils/threads.h>
#include <sys/mman.h>
#include <camera/Camera.h>
#include <sys/time.h>

namespace android
{

gralloc_module_t const *MtkCameraHal::mGrallocHal;

void _MappingFaceCoordination(int src[4], int dest[4], int width, int height)
{
    // X
    dest[0] = (-1000)+(src[0]*2000/width);

    // Y
    dest[1] = (-1000)+(src[1]*2000/height);
    
    // WIDTH
    dest[2] = dest[0]+(src[2]*2000/width);

    // HEIGHT
    dest[3] = dest[1]+(src[3]*2000/height);
}

bool _CheckMoreThanOneSecond()
{
    struct timeval current_timeval;
    static int previous_second=0;

    gettimeofday(&current_timeval,NULL);
    previous_second = current_timeval.tv_sec;
    
    if ( (current_timeval.tv_sec - previous_second) > 0 )
        return true;
    else
        return false;
}

MtkCameraHal::MtkCameraHal()
{
    LOGV("MtkCameraHal(): begin");

//added by wangyoulin
	openedOk=true;
//end by wangyoulin
    initDataStructure();
    initDefaultParameters();
    initThreads();

    LOGV("MtkCameraHal(): end");
}

MtkCameraHal::~MtkCameraHal()
{
    LOGV("~MtkCameraHal(): begin");
    mV4L2Camera->deinitCamera();
    LOGV("~MtkCameraHal(): end");
}


void MtkCameraHal::initDataStructure()
{
    int i;

    for ( i = 0 ; i < MAX_CONCURRENT_CAMERA_CLIENT ; i++ )
    {
        mPreviewHeap = NULL;
        mPictureHeap[i] = NULL;
        mRecordHeap[i] = NULL;
        
        mNotifyCb[i] = NULL;
        mDataCb[i] = NULL;
        mDataCbTimestamp[i] = NULL;
        mGetMemoryCb[i] = NULL;
        mCallbackCookie[i] = NULL;

        mPreviewWindow[i] = NULL;
        mMsgEnabled[i] = 0;

        mHandlePreview[i] = false;
        mHandleAutoFocus[i] = false;
        mHandleTakePicture[i] = false;
        mHandleRecording[i] = false;
        ClientOpened[i] = false;
    }

    mPreviewThread = NULL;
    mPreviewRunning = false;
    mExitPreviewThread = false;
    mSkipFrame = INITIAL_SKIP_FRAME;
    mRefCount = 0;
    mUseMetaDataBufferMode = USE_HW_VIDEO_ENCODER;

    mV4L2Camera = MtkV4L2Camera::createInstance();

    if (!mGrallocHal)
    {
        if ( hw_get_module(GRALLOC_HARDWARE_MODULE_ID, (const hw_module_t **) &mGrallocHal) )
        {
            LOGE("ERR(MtkCameraHal):Fail on loading gralloc HAL");
        }
    }
}

void MtkCameraHal::initThreads()
{
    // Init preview thread
    mPreviewThread = new PreviewThread(this);
    
}

void MtkCameraHal::initDefaultParameters()
{
    int ClientId = 0;
    String8 parameterString;
    char* pResolutionList=NULL;
    bool bCameraExist=false;
    
    if (mV4L2Camera == NULL)
    {
        LOGE("initDefaultParameters: mV4L2Camera object is NULL");
        return ;
    }

    LOGV("initDefaultParameters(): begin");
    mV4L2Camera->setSystemSupportMjpeg(SUPPORT_MJPEG);
    bCameraExist = mV4L2Camera->isCameraExist();
//added by wangyoulin
	openedOk=bCameraExist;
//end by wangyoulin
    if ( bCameraExist )
    {
        pResolutionList=(char*)malloc(MAX_RESOLUTION_STRING_LENGTH);
        memset(pResolutionList, 0, MAX_RESOLUTION_STRING_LENGTH);
        if ( mV4L2Camera->getPixelFormat(pResolutionList) == false )
        {
            LOGE("The connected camera does not support both MJPEG & YUYV.");
        }
 //added by wang youlin
	mV4L2Camera->initCamera();
	struct v4l2_queryctrl queryctrl;
	if (mV4L2Camera->v4l2QueryControl(V4L2_CID_BRIGHTNESS, &queryctrl)!=-1)
	mParameters.setControlParameterScope(mParameters.KEY_CONTROL_BRIGHTNESS_SCOPE, mV4L2Camera->v4l2GetControl(V4L2_CID_BRIGHTNESS), queryctrl.minimum, queryctrl.maximum, queryctrl.step, queryctrl.default_value);
	else mParameters.setControlParameterScope(mParameters.KEY_CONTROL_BRIGHTNESS_SCOPE, 0, 0, 0, 0, 0);

	if (mV4L2Camera->v4l2QueryControl(V4L2_CID_CONTRAST, &queryctrl)!=-1)
	mParameters.setControlParameterScope(mParameters.KEY_CONTROL_CONTRAST_SCOPE, mV4L2Camera->v4l2GetControl(V4L2_CID_CONTRAST), queryctrl.minimum, queryctrl.maximum, queryctrl.step, queryctrl.default_value);
	else mParameters.setControlParameterScope(mParameters.KEY_CONTROL_CONTRAST_SCOPE, 0, 0, 0, 0, 0);

	if (mV4L2Camera->v4l2QueryControl(V4L2_CID_SATURATION, &queryctrl)!=-1)
	mParameters.setControlParameterScope(mParameters.KEY_CONTROL_SATURATION_SCOPE, mV4L2Camera->v4l2GetControl(V4L2_CID_SATURATION), queryctrl.minimum, queryctrl.maximum, queryctrl.step, queryctrl.default_value);
	else mParameters.setControlParameterScope(mParameters.KEY_CONTROL_SATURATION_SCOPE, 0, 0, 0, 0, 0);

	if (mV4L2Camera->v4l2QueryControl(V4L2_CID_SHARPNESS, &queryctrl)!=-1)
	mParameters.setControlParameterScope(mParameters.KEY_CONTROL_SHARPNESS_SCOPE, mV4L2Camera->v4l2GetControl(V4L2_CID_SHARPNESS), queryctrl.minimum, queryctrl.maximum, queryctrl.step, queryctrl.default_value);
	else mParameters.setControlParameterScope(mParameters.KEY_CONTROL_SHARPNESS_SCOPE, 0, 0, 0, 0, 0);

	if (mV4L2Camera->v4l2QueryControl(V4L2_CID_WHITENESS, &queryctrl)!=-1)
	mParameters.setControlParameterScope(mParameters.KEY_CONTROL_WHITENESS_SCOPE, mV4L2Camera->v4l2GetControl(V4L2_CID_WHITENESS), queryctrl.minimum, queryctrl.maximum, queryctrl.step, queryctrl.default_value);
  	else mParameters.setControlParameterScope(mParameters.KEY_CONTROL_WHITENESS_SCOPE, 0, 0, 0, 0, 0);
	mV4L2Camera->deinitCamera();
//end by wang youlin
   }

    if ( ASSIGN_SPECIFIC_CAMERA_PARAMETER )
    {
        char* MyResolution = (char*)"1280x720";
        // Preview parameters
        mParameters.set(CameraParameters::KEY_SUPPORTED_PREVIEW_SIZES, MyResolution);
        mParameters.set(CameraParameters::KEY_SUPPORTED_PREVIEW_FORMATS, CameraParameters::PIXEL_FORMAT_YUV420SP);
        mParameters.set(CameraParameters::KEY_SUPPORTED_PREVIEW_FRAME_RATES, "20");
        mParameters.set(CameraParameters::KEY_SUPPORTED_PREVIEW_FPS_RANGE, "(15000,30000)");
        mParameters.set(CameraParameters::KEY_PREVIEW_SIZE, MyResolution);
        mParameters.set(CameraParameters::KEY_PREVIEW_FORMAT, CameraParameters::PIXEL_FORMAT_YUV420SP);
        mParameters.set(CameraParameters::KEY_PREVIEW_FRAME_RATE, "20");
        mParameters.set(CameraParameters::KEY_PREVIEW_FPS_RANGE, "15000,30000");
        mParameters.set(CameraParameters::KEY_HORIZONTAL_VIEW_ANGLE, "51.2");
        mParameters.set(CameraParameters::KEY_VERTICAL_VIEW_ANGLE, "39.4");
        
        // Picture parameters
        mParameters.set(CameraParameters::KEY_SUPPORTED_PICTURE_SIZES, MyResolution);
        mParameters.set(CameraParameters::KEY_SUPPORTED_PICTURE_FORMATS, CameraParameters::PIXEL_FORMAT_JPEG);
        mParameters.set(CameraParameters::KEY_PICTURE_SIZE, MyResolution);
        mParameters.set(CameraParameters::KEY_PICTURE_FORMAT, CameraParameters::PIXEL_FORMAT_JPEG);

        // Thumbnail parameters
        mParameters.set(CameraParameters::KEY_SUPPORTED_JPEG_THUMBNAIL_SIZES, MyResolution);
        mParameters.set(CameraParameters::KEY_JPEG_THUMBNAIL_WIDTH, "640");
        mParameters.set(CameraParameters::KEY_JPEG_THUMBNAIL_HEIGHT, "480");
        mParameters.set(CameraParameters::KEY_JPEG_THUMBNAIL_QUALITY, "100");
        mParameters.set(CameraParameters::KEY_JPEG_QUALITY, "100");

        // Recording parameters
        mParameters.set(CameraParameters::KEY_SUPPORTED_VIDEO_SIZES, MyResolution);
        mParameters.set(CameraParameters::KEY_PREFERRED_PREVIEW_SIZE_FOR_VIDEO, MyResolution);
        mParameters.set(CameraParameters::KEY_VIDEO_SIZE, MyResolution);
        mParameters.set(CameraParameters::KEY_VIDEO_FRAME_FORMAT, CameraParameters::PIXEL_FORMAT_YUV420SP);
    }
    else
    {
        bool bMjpegMode = (mV4L2Camera->isCameraSupportingMjpeg() && SUPPORT_MJPEG);

        // Preview parameters
        if ( MAX_CONCURRENT_CAMERA_CLIENT == 1 && pResolutionList != NULL )
            mParameters.set(CameraParameters::KEY_SUPPORTED_PREVIEW_SIZES, pResolutionList);
        else
            mParameters.set(CameraParameters::KEY_SUPPORTED_PREVIEW_SIZES, "640x480");
        mParameters.set(CameraParameters::KEY_SUPPORTED_PREVIEW_FORMATS, CameraParameters::PIXEL_FORMAT_YUV420SP);

        if ( bMjpegMode )
        {
            mParameters.set(CameraParameters::KEY_SUPPORTED_PREVIEW_FRAME_RATES, "20,15,10,5");
            mParameters.set(CameraParameters::KEY_SUPPORTED_PREVIEW_FPS_RANGE, "(5000,20000)");
            mParameters.set(CameraParameters::KEY_PREVIEW_FRAME_RATE, "20");
            mParameters.set(CameraParameters::KEY_PREVIEW_FPS_RANGE, "5000,20000");
        }
        else
        {
            mParameters.set(CameraParameters::KEY_SUPPORTED_PREVIEW_FRAME_RATES, "30,25,20,15,10,5");
            mParameters.set(CameraParameters::KEY_SUPPORTED_PREVIEW_FPS_RANGE, "(5000,30000)");
            mParameters.set(CameraParameters::KEY_PREVIEW_FRAME_RATE, "30");
            mParameters.set(CameraParameters::KEY_PREVIEW_FPS_RANGE, "5000,30000");
        }
        mParameters.set(CameraParameters::KEY_PREVIEW_SIZE, "640x480");
        mParameters.set(CameraParameters::KEY_PREVIEW_FORMAT, CameraParameters::PIXEL_FORMAT_YUV420SP);
        mParameters.set(CameraParameters::KEY_HORIZONTAL_VIEW_ANGLE, "51.2");
        mParameters.set(CameraParameters::KEY_VERTICAL_VIEW_ANGLE, "39.4");
        
        // Picture parameters
        if ( MAX_CONCURRENT_CAMERA_CLIENT == 1 && pResolutionList != NULL )
            mParameters.set(CameraParameters::KEY_SUPPORTED_PICTURE_SIZES, pResolutionList);
        else
            mParameters.set(CameraParameters::KEY_SUPPORTED_PICTURE_SIZES, "640x480");
        mParameters.set(CameraParameters::KEY_SUPPORTED_PICTURE_FORMATS, CameraParameters::PIXEL_FORMAT_JPEG);
        mParameters.set(CameraParameters::KEY_PICTURE_SIZE, "640x480");
        mParameters.set(CameraParameters::KEY_PICTURE_FORMAT, CameraParameters::PIXEL_FORMAT_JPEG);

        // Thumbnail parameters
        if ( MAX_CONCURRENT_CAMERA_CLIENT == 1 && pResolutionList != NULL )
            mParameters.set(CameraParameters::KEY_SUPPORTED_JPEG_THUMBNAIL_SIZES, pResolutionList);
        else
            mParameters.set(CameraParameters::KEY_SUPPORTED_JPEG_THUMBNAIL_SIZES, "640x480");
        mParameters.set(CameraParameters::KEY_JPEG_THUMBNAIL_WIDTH, "640");
        mParameters.set(CameraParameters::KEY_JPEG_THUMBNAIL_HEIGHT, "480");
        mParameters.set(CameraParameters::KEY_JPEG_THUMBNAIL_QUALITY, "100");
        mParameters.set(CameraParameters::KEY_JPEG_QUALITY, "100");

        // Recording parameters
        mParameters.set(CameraParameters::KEY_VIDEO_SIZE, "640x480");
//altered by wangyoulin for full sizes
//        mParameters.set(CameraParameters::KEY_SUPPORTED_VIDEO_SIZES, "640x480");
        mParameters.set(CameraParameters::KEY_SUPPORTED_VIDEO_SIZES, pResolutionList);
//end by wangyoulin
        mParameters.set(CameraParameters::KEY_PREFERRED_PREVIEW_SIZE_FOR_VIDEO, "640x480");
        mParameters.set(CameraParameters::KEY_VIDEO_FRAME_FORMAT, CameraParameters::PIXEL_FORMAT_YUV420SP);
    }
    
    // Other parameters
    mParameters.set(CameraParameters::KEY_ZOOM_SUPPORTED, "false");
    mParameters.set(CameraParameters::KEY_ZOOM, "0");
    mParameters.set(CameraParameters::KEY_MAX_ZOOM, "0");

    mParameters.set(CameraParameters::KEY_ROTATION, 0);
    mParameters.set(CameraParameters::KEY_FOCUS_DISTANCES, "0.10,1.20,Infinity");

    mParameters.set(CameraParameters::KEY_MAX_NUM_DETECTED_FACES_HW, "0");
    mParameters.set(CameraParameters::KEY_MAX_NUM_DETECTED_FACES_SW, "0");    
    
    mParameters.set(CameraParameters::KEY_SUPPORTED_FOCUS_MODES, CameraParameters::FOCUS_MODE_AUTO);
    mParameters.set(CameraParameters::KEY_FOCUS_MODE, CameraParameters::FOCUS_MODE_AUTO);
    
    mParameters.set(CameraParameters::KEY_SUPPORTED_EFFECTS, CameraParameters::EFFECT_NONE);
    mParameters.set(CameraParameters::KEY_EFFECT, CameraParameters::EFFECT_NONE);
    
    mParameters.set(CameraParameters::KEY_SUPPORTED_FLASH_MODES, CameraParameters::FLASH_MODE_OFF);
    mParameters.set(CameraParameters::KEY_FLASH_MODE, CameraParameters::FLASH_MODE_OFF);
    
    mParameters.set(CameraParameters::KEY_SUPPORTED_SCENE_MODES, CameraParameters::SCENE_MODE_AUTO);
    mParameters.set(CameraParameters::KEY_SCENE_MODE, CameraParameters::SCENE_MODE_AUTO);
    
    mParameters.set(CameraParameters::KEY_SUPPORTED_WHITE_BALANCE, CameraParameters::WHITE_BALANCE_AUTO);
    mParameters.set(CameraParameters::KEY_WHITE_BALANCE, CameraParameters::WHITE_BALANCE_AUTO);
    
    mParameters.set(CameraParameters::KEY_SUPPORTED_ANTIBANDING, CameraParameters::ANTIBANDING_50HZ);
    mParameters.set(CameraParameters::KEY_ANTIBANDING, CameraParameters::ANTIBANDING_50HZ);


    mParameters.getSupportedPreviewSizes(mSupportedPreviewSizes);
    mParameters.getSupportedPictureSizes(mSupportedPictureSizes);
    mParameters.getSupportedVideoSizes(mSupportedVideoSizes);

    setParameters(mParameters);

    mV4L2Camera->setFrameRate(30);

    if ( pResolutionList != NULL )
    {
        free(pResolutionList);
    }
    LOGV("initDefaultParameters(): end");
}

void MtkCameraHal::setCallbacks(int ClientId, 
                                camera_notify_callback notify_cb, 
                                camera_data_callback data_cb, 
                                camera_data_timestamp_callback data_cb_timestamp, 
                                camera_request_memory get_memory, void *user)
{
    LOGD("setCallbacks(ClientId=%d): begin", ClientId);

    // We use setCallbacks() to monitor the client open event.
    // We use release() to monitor the client close event.


    if ( !ClientOpened[ClientId] )
    {
        // It's the first time the client invokes setCallbacks.
        mRefCount++;
        mMsgEnabled[ClientId] = 0;
        ClientOpened[ClientId] = true;
    }
    else
    {
        LOGI("Warning! MtkCameraHal::setCallbacks() been invoked for multiple times!");
    }

    mNotifyCb[ClientId] = notify_cb;
    mDataCb[ClientId] = data_cb;
    mDataCbTimestamp[ClientId] = data_cb_timestamp;
    mGetMemoryCb[ClientId] = get_memory;
    mCallbackCookie[ClientId] = user;

    LOGD("setCallbacks(): end");
}

status_t MtkCameraHal::setParameters(const CameraParameters &params)
{
    status_t ret = NO_ERROR;
    int current_preview_width = 0;
    int current_preview_height = 0;
    int current_frame_size = 0;
    int current_pixel_format = 0;
    mV4L2Camera->getPreviewSize(&current_preview_width,  &current_preview_height,  &current_frame_size);
    current_pixel_format = mV4L2Camera->getPreviewPixelFormat();
    
    // preview parameters
    {
        const char *new_str_preview_format;
        int new_preview_width = 0;
        int new_preview_height = 0;
        int new_preview_format = V4L2_PIX_FMT_YUYV; // Always get YUYV frames

        new_str_preview_format = params.getPreviewFormat();
        params.getPreviewSize(&new_preview_width, &new_preview_height);
        LOGD("setParameters(): Preview size: %dx%d@%dfps(%s)", new_preview_width, new_preview_height, params.getPreviewFrameRate(), new_str_preview_format);

        if ( strcmp(new_str_preview_format, CameraParameters::PIXEL_FORMAT_YUV420SP) )
        {
            LOGE("Unsupported preview color format: %s", new_str_preview_format);
            return BAD_VALUE;
        }

        if (0 < new_preview_width && 0 < new_preview_height && new_str_preview_format != NULL && 
            isSupportedPreviewSize(new_preview_width, new_preview_height) )
        {
            if (current_preview_width != new_preview_width || 
                current_preview_height != new_preview_height || 
                current_pixel_format != new_preview_format)
            {
                if (mV4L2Camera->setPreviewSize(new_preview_width, new_preview_height, new_preview_format) < 0)
                {
                    LOGE("setParameters: Fail on mV4L2Camera->setPreviewSize(width(%d), height(%d), format(%d))", new_preview_width, new_preview_height, new_preview_format);
                    ret = UNKNOWN_ERROR;
                }
                else
                {
                    mParameters.setPreviewSize(new_preview_width, new_preview_height);
                    mParameters.setPreviewFormat(new_str_preview_format);
                    
                    for ( int i = 0 ; i < MAX_CONCURRENT_CAMERA_CLIENT ; i++ )
                    {
                        if (mPreviewWindow[i])
                        {
                            if (mPreviewRunning)
                            {
                                LOGE("setParameters: preview is running, cannot change size and format!");
//deleted by wang youlin to enable setParameters during preview
//                                    ret = INVALID_OPERATION;
//end by wang youlin
                            }

                            mPreviewWindowLock[i].lock();
                            mPreviewWindow[i]->set_buffers_geometry(mPreviewWindow[i], new_preview_width, new_preview_height, new_preview_format);
                            mPreviewWindowLock[i].unlock();
                            LOGV("setParameters: DONE mPreviewWindow set_buffers_geometry");
                        }
                    }
                }
            }
        }
        else
        {
            LOGE("setParameters(): Invalid preview size(%dx%d), pixel_format(%s)", new_preview_width, new_preview_height, new_str_preview_format);
            ret = INVALID_OPERATION;
        }

        // frame rate
        int new_frame_rate = params.getPreviewFrameRate();
        if ( mPreviewRunning )
        {
            if (new_frame_rate != mParameters.getPreviewFrameRate() )
            {
                LOGE("setParameters(): request for preview frame %d not allowed when preview is running, != %d", new_frame_rate, mParameters.getPreviewFrameRate() );
            }
        }
        else
        {
            mV4L2Camera->setFrameRate(new_frame_rate);
            mParameters.setPreviewFrameRate(new_frame_rate);
        }
    }

    // Picture parameters.
    {
        int new_picture_width = 0;
        int new_picture_height = 0;
        const char *new_str_picture_format = NULL; 

        // Format
        new_str_picture_format = params.getPictureFormat();
        if ( (new_str_picture_format!=NULL) && (strcmp(new_str_picture_format, CameraParameters::PIXEL_FORMAT_JPEG)==0) )
        {
            mParameters.setPictureFormat(new_str_picture_format);
        }
        else
        {
            LOGE("setParameters(): Invalid picture pixel_format(%s)", new_str_picture_format);
            ret = INVALID_OPERATION;
        }

        // Size
        params.getPictureSize(&new_picture_width, &new_picture_height);        
        if ( isSupportedPictureSize(new_picture_width, new_picture_height) )
        {
            LOGD("setParameters(): Picture size: %dx%d(%s)", new_picture_width, new_picture_height, new_str_picture_format);
            mParameters.setPictureSize(new_picture_width, new_picture_height);\
        }
        else
        {
            LOGE("setParameters(): Invalid picture size(%dx%d)", new_picture_width, new_picture_height);
            ret = INVALID_OPERATION;
        }
    }

    // Recording parameters.
    {
        int new_video_width = 0;
        int new_video_height = 0;
        const char* new_str_video_format = NULL;

        // Format
        new_str_video_format = params.get(CameraParameters::KEY_VIDEO_FRAME_FORMAT);
        if ( (new_str_video_format!=NULL) && ((strcmp(new_str_video_format, CameraParameters::PIXEL_FORMAT_YUV420SP)==0)) )
        {
            mParameters.set(CameraParameters::KEY_VIDEO_FRAME_FORMAT, new_str_video_format);
        }
        else
        {
            LOGE("setParameters(): Invalid Recording Video pixel_format(%s)", new_str_video_format);
            ret = INVALID_OPERATION;
        }
        
        params.getVideoSize(&new_video_width, &new_video_height);
        if ( isSupportedVideoSize(new_video_width, new_video_height) )
        {
            mParameters.setVideoSize(new_video_width, new_video_height);
            LOGD("setParameters(): Recording Video size: %dx%d(%s)", 
                             new_video_width, new_video_height, new_str_video_format);
         }
         else
         {
            LOGD("setParameters(): Invalid Recording Video size: %dx%d(%s)", 
                             new_video_width, new_video_height, new_str_video_format);
            ret = INVALID_OPERATION;
         }
    }
    
//added by wang youlin
	int intpwidth,intpheight;
	params.getInterpolationSize(& intpwidth, & intpheight);
//	params.remove(mParameters.KEY_INTERPOLATION_SIZE);

	if (intpwidth==3264&&intpheight==2448)
	{
		mInterpolationSize=8;
		LOGD("setParameters****************set3264x2448");
	}else
	{
		mInterpolationSize=0;
	}

	int ctlvalue;
	params.getControlParameterValue(params.KEY_CONTROL_BRIGHTNESS_VALUE, & ctlvalue);
	if (ctlvalue!=-32768)
	{
		mParameters.set(mParameters.KEY_CONTROL_BRIGHTNESS_VALUE , ctlvalue);
	}
	
	params.getControlParameterValue(params.KEY_CONTROL_CONTRAST_VALUE, & ctlvalue);
	if (ctlvalue!=-32768)
	{
		mParameters.set(mParameters.KEY_CONTROL_CONTRAST_VALUE , ctlvalue);
	}
	
	params.getControlParameterValue(params.KEY_CONTROL_SATURATION_VALUE, & ctlvalue);
	if (ctlvalue!=-32768)
	{
		mParameters.set(mParameters.KEY_CONTROL_SATURATION_VALUE , ctlvalue);
	}
	
	params.getControlParameterValue(params.KEY_CONTROL_SHARPNESS_VALUE, & ctlvalue);
	if (ctlvalue!=-32768)
	{
		mParameters.set(mParameters.KEY_CONTROL_SHARPNESS_VALUE , ctlvalue);
	}
	
	params.getControlParameterValue(params.KEY_CONTROL_WHITENESS_VALUE, & ctlvalue);
	if (ctlvalue!=-32768)
	{
		mParameters.set(mParameters.KEY_CONTROL_WHITENESS_VALUE , ctlvalue);
	}

		setParametersValue();
//end by wang youlin
    return ret;
}

//added by wang youlin
void MtkCameraHal::setParametersValue()
{
LOGD("setParametersValue@@@@@@@@@@@@*1***************");
	if (mV4L2Camera->ifCameraOpened()<0) return;

	int ctlvalue;
	mParameters.getControlParameterValue(mParameters.KEY_CONTROL_BRIGHTNESS_VALUE, & ctlvalue);
LOGD("setParameters*1***************%d",ctlvalue);
	if (ctlvalue!=-32768)
	{
		mV4L2Camera->v4l2SetControl(V4L2_CID_BRIGHTNESS,  ctlvalue);
 		LOGD("setParameters****************@@@KEY_CONTROL_BRIGHTNESS_VALUE[%d]",ctlvalue);
//		mParameters.remove(mParameters.KEY_CONTROL_BRIGHTNESS_VALUE);
	}

	mParameters.getControlParameterValue(mParameters.KEY_CONTROL_CONTRAST_VALUE, & ctlvalue);
	if (ctlvalue!=-32768)
	{
		mV4L2Camera->v4l2SetControl(V4L2_CID_CONTRAST,  ctlvalue);
 		LOGD("setParameters****************@@@KEY_CONTROL_CONTRAST_VALUE[%d]",ctlvalue);
//		mParameters.remove(mParameters.KEY_CONTROL_CONTRAST_VALUE);
	}

	mParameters.getControlParameterValue(mParameters.KEY_CONTROL_SATURATION_VALUE, & ctlvalue);
	if (ctlvalue!=-32768)
	{
		mV4L2Camera->v4l2SetControl(V4L2_CID_SATURATION,  ctlvalue);
 		LOGD("setParameters****************@@@KEY_CONTROL_SATURATION_VALUE[%d]",ctlvalue);
//		mParameters.remove(mParameters.KEY_CONTROL_SATURATION_VALUE);
	}

	mParameters.getControlParameterValue(mParameters.KEY_CONTROL_SHARPNESS_VALUE, & ctlvalue);
	if (ctlvalue!=-32768)
	{
		mV4L2Camera->v4l2SetControl(V4L2_CID_SHARPNESS,  ctlvalue);
 		LOGD("setParameters****************@@@KEY_CONTROL_SHARPNESS_VALUE[%d]",ctlvalue);
//		mParameters.remove(mParameters.KEY_CONTROL_SHARPNESS_VALUE);
	}

	mParameters.getControlParameterValue(mParameters.KEY_CONTROL_WHITENESS_VALUE, & ctlvalue);
	if (ctlvalue!=-32768)
	{
		mV4L2Camera->v4l2SetControl(V4L2_CID_WHITENESS,  ctlvalue);
 		LOGD("setParameters****************@@@KEY_CONTROL_WHITENESS_VALUE[%d]",ctlvalue);
//		mParameters.remove(mParameters.KEY_CONTROL_WHITENESS_VALUE);
	}
}
//end by wang youlin

//altered by wang youlin
CameraParameters MtkCameraHal::getParameters()//const
//end by wang youlin
{
    return mParameters;
}

status_t MtkCameraHal::setPreviewWindow(int ClientId, preview_stream_ops *w)
{
    int min_bufs;
    int restart_preview=false;
    int preview_width;
    int preview_height;
    int hal_pixel_format = HAL_PIXEL_FORMAT_YCrCb_420_SP;   // We always use NV21 for preview
    
    LOGD("setPreviewWindow(ClientId=%d): begin", ClientId);

    if (!w)
    {
        LOGW("preview window is NULL!");
        return OK;
    }

    mPreviewLock.lock();
    mPreviewWindowLock[ClientId].lock();
    if (mPreviewRunning)
    {
        LOGV("stop preview (window change)");
        stopPreviewInternal(ClientId);
        restart_preview = true;
    }

    mPreviewWindow[ClientId] = w;
    if (w->get_min_undequeued_buffer_count(w, &min_bufs) )
    {
        LOGE("could not retrieve min undequeued buffer count");
        mPreviewWindowLock[ClientId].unlock();
        mPreviewLock.unlock();
        return INVALID_OPERATION;
    }

    if (min_bufs >= MAX_BUFFERS)
    {
        LOGE("min undequeued buffer count %d is too high (expecting at most %d)", min_bufs, MAX_BUFFERS);
        mPreviewWindowLock[ClientId].unlock();
        mPreviewLock.unlock();
        return INVALID_OPERATION;
    }

    if (w->set_buffer_count(w, MAX_BUFFERS) )
    {
        LOGE("could not set buffer count");
        mPreviewWindowLock[ClientId].unlock();
        mPreviewLock.unlock();
        return INVALID_OPERATION;
    }

    if (w->set_usage(w, GRALLOC_USAGE_SW_WRITE_OFTEN) )
    {
        LOGE("could not set usage on gralloc buffer");
        mPreviewWindowLock[ClientId].unlock();
        mPreviewLock.unlock();
        return INVALID_OPERATION;
    }

    mParameters.getPreviewSize(&preview_width, &preview_height);

    if (w->set_buffers_geometry(w, preview_width, preview_height, hal_pixel_format) )
    {
        LOGE("could not set buffers geometry to %dx%d, %d", preview_width, preview_height, hal_pixel_format);
        mPreviewWindowLock[ClientId].unlock();
        mPreviewLock.unlock();
        return INVALID_OPERATION;
    }

    if ( restart_preview )
    {
        LOGV("resume preview");
        mV4L2Camera->resetCamera(); 
        startPreviewInternal(ClientId);
    }
    mPreviewWindowLock[ClientId].unlock();
    mPreviewLock.unlock();
    LOGD("setPreviewWindow() end");
    return OK;
}

void MtkCameraHal::enableMsgType(int ClientId, int32_t msgType)
{
    mMsgEnabled[ClientId] |= msgType;
    LOGV("enableMsgType: msgType=0x%x, mMsgEnabled=0x%x", msgType, mMsgEnabled[ClientId]);
}

void MtkCameraHal::disableMsgType(int ClientId, int32_t msgType)
{
    mMsgEnabled[ClientId] &= ~msgType;
    LOGV("disableMsgType : msgType=0x%x, mMsgEnabled=0x%x", msgType, mMsgEnabled[ClientId]);
}

bool MtkCameraHal::msgTypeEnabled(int ClientId, int32_t msgType)
{
    return (mMsgEnabled[ClientId] &msgType);
}

int MtkCameraHal::previewThreadWrapper()
{
    LOGI("previewThreadWrapper(): preview thread started");
    while (1)
    {
        mPreviewLock.lock();
        while (!mPreviewRunning)
        {
            if ( mV4L2Camera->isPreviewStarted() )
            {
                mV4L2Camera->stopPreview();
                mV4L2Camera->deinitCamera();
            }
            
            mPreviewStoppedCondition.signal();

            LOGV("previewThreadWrapper(): before mPreviewCondition.wait(mPreviewLock)");
            mPreviewCondition.wait(mPreviewLock);
            LOGV("previewThreadWrapper(): after mPreviewCondition.wait(mPreviewLock)");
        }
        mPreviewLock.unlock();
        if (mExitPreviewThread)
        {
            LOGI("previewThreadWrapper(): preview thread terminated");
            return 0;
        }
        previewThread();
    }

}

int MtkCameraHal::previewThread()
{
    int index;
    int ret;
    char* rawdata;
    int width, height, frame_size, offset=0;
//added by wang youlin
	int picturesize=0;
//end by wang youlin
    int i;
    bool bMjpegMode = (mV4L2Camera->isCameraSupportingMjpeg() && SUPPORT_MJPEG);

    // get preview raw data.
    index = mV4L2Camera->getPreview(&rawdata);
    if (index < 0)
    {
        LOGE("previewThread(): Fail on V4L2Camera->getPreview()");
		if(index == -2)
		{
			if(mSkipFrame > 0)
				mSkipFrame--;
			return NO_ERROR;
		}
	}
    else if ( rawdata == NULL )
    {
        // Here menas video frame broken due to iso packet lost. We jsut skip this frame.
        mV4L2Camera->getPreviewDone(index);
        return NO_ERROR;
    }
    else
    {
        offset = frame_size * index;
        mV4L2Camera->getPreviewSize(&width, &height, &frame_size);
//added by wang youlin
		if (mInterpolationSize==8)
		{
 			picturesize = (3264*2448 * 2);
		}else
		{
			picturesize = (width * height * 2);
		}
//end by wang youlin
    }

    if (mSkipFrame > 0)
    {
        LOGV("previewThread: skipping frame %d. %s", mSkipFrame, __TIME__);
        mSkipFrame--;
        mV4L2Camera->getPreviewDone(index);
        return NO_ERROR;
    }

    for ( i = 0 ; i < MAX_CONCURRENT_CAMERA_CLIENT ; i++ )  // Loop for each client
    {
        if ( !ClientOpened[i] ) //|| !mPreviewRunning )
        {
            continue;
        }

        // Error handling: can't get data from camera.
        if ( index < 0 )
        {
            if ( mNotifyCb[i] && (mMsgEnabled[i]&CAMERA_MSG_PREVIEW_FRAME) )
            {
                LOGE("Notify camera client : CAMERA_MSG_ERROR");
                mNotifyCb[i](CAMERA_MSG_ERROR, 0, 0, mCallbackCookie[i]);
            }
            
            // This is to avoid system hang if unplug camera when video recording.
            // (MPEG4Writer.cpp:threadEntry() -> CameraSrouce.cpp:read())
            if ( mNotifyCb[i] && (mMsgEnabled[i]&CAMERA_MSG_VIDEO_FRAME) )
            {
                nsecs_t timestamp = systemTime(SYSTEM_TIME_MONOTONIC);
                mDataCbTimestamp[i](timestamp, CAMERA_MSG_VIDEO_FRAME, mRecordHeap[i], 0, mCallbackCookie[i]);
            }
            sleep(1);
            continue;
        }

        // Notify the client of a new preview frame.
        if ( mHandlePreview[i] && mDataCb[i] )
        {
            if ( mMsgEnabled[i]&CAMERA_MSG_PREVIEW_FRAME )
            {
                if (mPreviewHeap == NULL)
                {
                    mPreviewHeap = mGetMemoryCb[i](-1, frame_size, MAX_BUFFERS, NULL);
                    if ( mPreviewHeap == NULL )
                    {
                        LOGE("Failed to allocate preview heap!!!");
                        continue;
                    }
                }
            
                unsigned char *dest = (unsigned char*)(((char*)mPreviewHeap->data)+offset);
                if ( bMjpegMode )
                {
                    mV4L2Camera->NV16toNV21((unsigned char*)rawdata, dest, width, height);
                }
                else
                {
                    mV4L2Camera->YUYVtoNV21((unsigned char*)rawdata, dest, width, height);
                }
                mDataCb[i](CAMERA_MSG_PREVIEW_FRAME, mPreviewHeap, index, NULL, mCallbackCookie[i]);
            }
        }

        // Fill the video buffer for preview window
        if (mPreviewWindow[i] && mGrallocHal)
        {
            buffer_handle_t *buf_handle;
            int stride;
            void *vaddr;

            do
            {
                if ( mPreviewWindowLock[i].tryLock() )
                {
                    LOGD("previewThread(): mPreviewWindowLock[%d] is locked, skip",i);
                    break;
                }

                if (0 != mPreviewWindow[i]->dequeue_buffer(mPreviewWindow[i], &buf_handle, &stride) )
                {
                    LOGE("previewThread(): Could not dequeue gralloc buffer!");
                    mPreviewWindow[i] = NULL;
                    mPreviewWindowLock[i].unlock();
                    break;
                }

                if (!mGrallocHal->lock(mGrallocHal, *buf_handle, GRALLOC_USAGE_SW_WRITE_OFTEN, 0, 0, width, height, &vaddr) )
                {
                    unsigned char *dest = (unsigned char*)vaddr;
                    if ( bMjpegMode )
                    {
                        mV4L2Camera->NV16toNV21((unsigned char*)rawdata, dest, width, height);
                    }
                    else
                    {
                        mV4L2Camera->YUYVtoNV21((unsigned char*)rawdata, dest, width, height);
                    }

                    mGrallocHal->unlock(mGrallocHal,  *buf_handle);
                }
                else
                {
                    LOGE("previewThread(): could not obtain gralloc buffer");
                }

                if (0 != mPreviewWindow[i]->enqueue_buffer(mPreviewWindow[i], buf_handle) )
                {
                    LOGE("Could not enqueue gralloc buffer!");
                }
                mPreviewWindowLock[i].unlock();
            }while(0);
        }
        // Handle auto focus
        if ( mHandleAutoFocus[i] && mNotifyCb[i] )
        {
            if (mMsgEnabled[i] & CAMERA_MSG_FOCUS) 
            {
                LOGI("previewThread(): Handle auto focus.");
                mHandleAutoFocus[i]=false;
                LOGI("Notify CAMERA_MSG_FOCUS");
                mNotifyCb[i](CAMERA_MSG_FOCUS, 0, 0, mCallbackCookie[i]);
            }
        }

        // Handle take picture
        if ( mHandleTakePicture[i] && mDataCb[i] )
        {
            LOGI("previewThread(): Handle take picture begin previewrunning = %d.", mPreviewRunning);
            mHandleTakePicture[i] = false;
			mSkipFrame = INITIAL_SKIP_FRAME;

			// Handle take picture callback
			if ( mNotifyCb[i] && (mMsgEnabled[i] & CAMERA_MSG_SHUTTER) ) 
			{
				LOGV("takePictureThread(): Notify Callback: CAMERA_MSG_SHUTTER begin");
				mNotifyCb[i](CAMERA_MSG_SHUTTER, 0, 0, mCallbackCookie[i]);
				LOGV("takePictureThread(): Notify Callback: CAMERA_MSG_SHUTTER end");
			}
			
            if (mMsgEnabled[i] & CAMERA_MSG_COMPRESSED_IMAGE) 
            {
                if (mPictureHeap[i] == NULL)
                {
 //added by wang youlin
 //              	mPictureHeap[i] = mGetMemoryCb[i](-1, frame_size, 1, NULL);
					mPictureHeap[i] = mGetMemoryCb[i](-1, picturesize, 1, NULL);
//end by wang youlin
                    if ( mPictureHeap[i] == NULL)
                    {
                        LOGE("Failed to allocate picture heap!!!");
                        continue;
                    }
                }

                if ( bMjpegMode )
                {
                    ret = mV4L2Camera->NV16toJpegFile((unsigned char*)rawdata, (unsigned char*)(mPictureHeap[i]->data), width, height);
                }
                else
                {
//added by wang youlin
		if (mInterpolationSize==8)
		{
			ret = mV4L2Camera->YUYVtoJPEGInterpolationTo3264x2448((unsigned char*)rawdata, (unsigned char*)(mPictureHeap[i]->data), width, height);
		}else
		{
			ret = mV4L2Camera->YUYVtoJpegFile((unsigned char*)rawdata, (unsigned char*)(mPictureHeap[i]->data), width, height);
		}
//end by wang youlin
                }
                if ( ret > 0 )
                {
                    mDataCb[i](CAMERA_MSG_COMPRESSED_IMAGE, mPictureHeap[i], 0, NULL, mCallbackCookie[i]);
                }
                else
                {
                    LOGE("Convert YUYV to Jpeg File Failed!");
                }
            }
            LOGI("previewThread(): Handle take picture end.");
            
        }

        // Handle recording
        if ( mHandleRecording[i] && mDataCbTimestamp[i] )
        {
            if (mMsgEnabled[i] & CAMERA_MSG_VIDEO_FRAME) 
            {
                nsecs_t timestamp = systemTime(SYSTEM_TIME_MONOTONIC);
                unsigned char *dest = (unsigned char*)(((char*)mRecordHeap[i]->data)+offset);
                if ( bMjpegMode )
                {
                    mV4L2Camera->NV16toNV21((unsigned char*)rawdata, dest, width, height);
                }
                else
                {
                    mV4L2Camera->YUYVtoNV21((unsigned char*)rawdata, dest, width, height);
                }
                mDataCbTimestamp[i](timestamp, CAMERA_MSG_VIDEO_FRAME, mRecordHeap[i], index, mCallbackCookie[i]);
            }
        }
    }


    if ( index >= 0 )
    {
        mV4L2Camera->getPreviewDone(index);
    }

    return NO_ERROR;
}


status_t MtkCameraHal::startPreview(int ClientId)
{
    LOGD("startPreview(): begin");

    mPreviewLock.lock();
    if (mPreviewRunning)
    {
        // already running
        LOGE("startPreview: preview thread already running");
        mSkipFrame = INITIAL_SKIP_FRAME;
        mPreviewLock.unlock();
        return NO_ERROR;
    }

    //startPreviewInternal(ClientId);
    if(NO_ERROR != startPreviewInternal(ClientId))
	{
		//fail if no usb camera exist, for changhong 
		LOGE("MtkCameraHal::startPreview: fail");
		mPreviewLock.unlock();
		return UNKNOWN_ERROR;
	}
	
    mHandlePreview[ClientId] = true;

    mPreviewLock.unlock();
    LOGD("startPreview(): end");

    // always return no error
    return NO_ERROR;
}

status_t MtkCameraHal::startPreviewInternal(int ClientId)
{
    int ret;
    int width, height, frame_size;
    
    LOGV("startPreviewInternal(): begin");

    ret = mV4L2Camera->initCamera();
    if (ret < 0)
    {
        LOGE("ERR(MtkCameraHal):Fail on mV4L2Camera init");
        return UNKNOWN_ERROR;
    }
    
    ret = mV4L2Camera->startPreview();
    if (ret < 0)
    {
        LOGE("startPreviewInternal: Fail on mV4L2Camera->startPreview()");
        return UNKNOWN_ERROR;
    }

    mV4L2Camera->getPreviewSize(&width, &height, &frame_size);

    mSkipFrame = INITIAL_SKIP_FRAME;
    mPreviewRunning = true;
    mPreviewCondition.signal();
    LOGV("startPreviewInternal(): end");
    return NO_ERROR;
}

void MtkCameraHal::stopPreview(int ClientId)
{
    LOGV("stopPreview(): begin");

    // Stop any callback to upper layer.
    mHandlePreview[ClientId] = false;
    mHandleAutoFocus[ClientId] = false;
    mHandleTakePicture[ClientId] = false;
    mHandleRecording[ClientId] = false;

    if ( mRefCount > 1 )
    {
        LOGI("stopPreview(): There are more than one camera client. Don't stop preview thread.");
        return;
    }
    
    /* request that the preview thread stop. */
    mPreviewLock.lock();
    stopPreviewInternal(ClientId);
    mPreviewLock.unlock();
    LOGV("stopPreview(): end");
}

void MtkCameraHal::stopPreviewInternal(int ClientId)
{
    LOGD("stopPreviewInternal(): begin");

    /* request that the preview thread stop. */
    if (mPreviewRunning)
    {
        mPreviewRunning = false;
        mPreviewCondition.signal();
        // wait until preview thread is stopped
        LOGV("stopPreviewInternal(): before mPreviewStoppedCondition.wait");
        if ( mPreviewStoppedCondition.waitRelative(mPreviewLock, (nsecs_t)9000000000) != 0 )
        {
            LOGE("stopPreviewInternal(): wait timeout ! retry...");
            mPreviewRunning = false;
            mPreviewCondition.signal();
            
            // we don't set timeout for the retry.
            mPreviewStoppedCondition.wait(mPreviewLock);
        }
        LOGV("stopPreviewInternal(): after mPreviewStoppedCondition.wait");
    }
    else
    {
        LOGV("stopPreviewInternal(): preview not running, doing nothing");
    }
    LOGD("stopPreviewInternal(): end");
}

bool MtkCameraHal::previewEnabled(int ClientId)
{
    Mutex::Autolock lock(mPreviewLock);
    return mPreviewRunning;
}

bool MtkCameraHal::isSupportedPreviewSize(const int width, const int height)const
{
    unsigned int i;

    for (i = 0; i < mSupportedPreviewSizes.size(); i++)
    {
        if (mSupportedPreviewSizes[i].width == width && mSupportedPreviewSizes[i].height == height)
        {
            return true;
        }
    }
    return false;
}

bool MtkCameraHal::isSupportedPictureSize(const int width, const int height)const
{
    unsigned int i;

    for (i = 0; i < mSupportedPictureSizes.size(); i++)
    {
        if (mSupportedPictureSizes[i].width == width && mSupportedPictureSizes[i].height == height)
        {
            return true;
        }
    }
    return false;
}

bool MtkCameraHal::isSupportedVideoSize(const int width, const int height)const
{
    unsigned int i;

    for (i = 0; i < mSupportedVideoSizes.size(); i++)
    {
        if (mSupportedVideoSizes[i].width == width && mSupportedVideoSizes[i].height == height)
        {
            return true;
        }
    }
    return false;
}

int MtkCameraHal::startRecording(int ClientId)
{
    int iPreviewWidth, iPreviewHeight, iPreviewFramesize, iPreviewFrameRate;
    int iVideoWidth, iVideoHeight;
    LOGD("startRecording(): begin");
    if ( mV4L2Camera )
    {
        mV4L2Camera->getPreviewSize(&iPreviewWidth, &iPreviewHeight, &iPreviewFramesize);
        mParameters.getVideoSize(&iVideoWidth, &iVideoHeight);
        iPreviewFrameRate = mParameters.getPreviewFrameRate();

        if ( mRecordHeap[ClientId] == NULL )
        {
            mRecordHeap[ClientId] = mGetMemoryCb[ClientId](-1, iPreviewFramesize, MAX_BUFFERS, NULL);
            if ( mRecordHeap[ClientId] == NULL )
            {
                LOGE("startRecording() failed to allocate record heap!!!");
                return NO_MEMORY;
            }
        }
    
        mHandleRecording[ClientId]=true;
        LOGD("startRecording(): end");
        return NO_ERROR;
    }
    else
    {
        mHandleRecording[ClientId]=false;
        return UNKNOWN_ERROR;
    }
}

void MtkCameraHal::stopRecording(int ClientId)
{
    mHandleRecording[ClientId]=false;
}

bool MtkCameraHal::recordingEnabled(int ClientId)
{
    return mHandleRecording[ClientId];
}
void MtkCameraHal::releaseRecordingFrame(int ClientId, const void *opaque)
{
}

status_t MtkCameraHal::storeMetaDataInBuffers(bool enable)
{
    if ( mUseMetaDataBufferMode )
    {
        // HW Encoder Solution
        if ( enable )
            return NO_ERROR;
        else
            return INVALID_OPERATION;
    }
    else
    {
        // SW Encoder Solution
        if ( enable )
            return INVALID_OPERATION;
        else
            return NO_ERROR;
    }
    return NO_ERROR;
}

void MtkCameraHal::release(int ClientId)
{
    int i;
    LOGD("release(): begin");

    /* shut down any threads we have that might be running.  do it here
     * instead of the destructor.  we're guaranteed to be on another thread
     * than the ones below.  if we used the destructor, since the threads
     * have a reference to this object, we could wind up trying to wait
     * for ourself to exit, which is a deadlock.
     */

    if ( ClientOpened[ClientId] )
    {
        mRefCount--;
        if (mRefCount == 0 && mPreviewThread != NULL)
        {
            mPreviewThread->requestExit();
            mExitPreviewThread = true;
            mPreviewRunning = true;
            mPreviewCondition.signal();
            mPreviewThread->requestExitAndWait();
            mPreviewThread.clear();
            mPreviewThread = NULL;
        }

        if (mPreviewHeap)
        {
            mPreviewHeap->release(mPreviewHeap);
            mPreviewHeap = NULL;
        }
        if (mPictureHeap[ClientId])
        {
            mPictureHeap[ClientId]->release(mPictureHeap[ClientId]);
            mPictureHeap[ClientId] = NULL;
        }
        if (mRecordHeap[ClientId])
        {
            mRecordHeap[ClientId]->release(mRecordHeap[ClientId]);
            mRecordHeap[ClientId] = NULL;
        }

        mPreviewWindowLock[ClientId].lock();
        mPreviewWindow[ClientId] = NULL;
        mPreviewWindowLock[ClientId].unlock();

        mNotifyCb[ClientId] = NULL;
        mDataCb[ClientId] = NULL;
        mDataCbTimestamp[ClientId] = NULL;
        mGetMemoryCb[ClientId] = NULL;
        mCallbackCookie[ClientId] = NULL;
        mMsgEnabled[ClientId] = 0;
        mHandleRecording[ClientId] = false;
        ClientOpened[ClientId] = false;
    }
    else
    {
        LOGE("The releasing ClientId is not opened!");
    }

    LOGD("release(): end");
}

status_t MtkCameraHal::autoFocus(int ClientId)
{
    mHandleAutoFocus[ClientId] = true;
    return NO_ERROR;
}

status_t MtkCameraHal::cancelAutoFocus(int ClientId)
{
    return NO_ERROR;
}

status_t MtkCameraHal::takePicture(int ClientId)
{
    LOGD("takePicture(): begin");
    mHandleTakePicture[ClientId]=true;   
    LOGD("takePicture(): end");
    return NO_ERROR;
}

status_t MtkCameraHal::cancelPicture(int ClientId)
{
    return NO_ERROR;
}

status_t MtkCameraHal::sendCommand(int ClientId, int32_t cmd, int32_t arg1, int32_t arg2)
{
    switch(cmd)
    {          
        case CAMERA_CMD_START_SMOOTH_ZOOM:
        case CAMERA_CMD_STOP_SMOOTH_ZOOM:
        case CAMERA_CMD_SET_DISPLAY_ORIENTATION:
        case CAMERA_CMD_ENABLE_SHUTTER_SOUND:
        case CAMERA_CMD_PLAY_RECORDING_SOUND:
        default:
            LOGE("Command not supported %d", cmd);
            break;
    }
    return NO_ERROR;
}


static camera_device_t *g_cam_device[MAX_CONCURRENT_CAMERA_CLIENT]={0};
static void* MtkCameraHalInstance=NULL;
static int CameraClientRefCounter=0;

static inline MtkCameraHal *obj(struct camera_device *dev)
{
    return reinterpret_cast < MtkCameraHal * > (dev->priv);
} 

/** Set the preview_stream_ops to which preview frames are sent */
static int HAL_camera_device_set_preview_window(struct camera_device *dev, 
                                                struct preview_stream_ops *buf)
{
    LOGV("HAL_camera_device_set_preview_window(ClientId=%d): begin and end", dev->common.reserved[0]);
    return obj(dev)->setPreviewWindow(dev->common.reserved[0], buf);
} 

/** Set the notification and data callbacks */
static void HAL_camera_device_set_callbacks(struct camera_device *dev, 
                                            camera_notify_callback notify_cb, 
                                            camera_data_callback data_cb, 
                                            camera_data_timestamp_callback data_cb_timestamp, 
                                            camera_request_memory get_memory, 
                                            void *user)
{
    LOGV("HAL_camera_device_set_callbacks(ClientId=%d): begin", dev->common.reserved[0]);
    obj(dev)->setCallbacks(dev->common.reserved[0], notify_cb, data_cb, data_cb_timestamp, get_memory, user);
    LOGV("HAL_camera_device_set_callbacks(): end");
} 

/**
 * The following three functions all take a msg_type, which is a bitmask of
 * the messages defined in include/ui/Camera.h
 */

/**
 * Enable a message, or set of messages.
 */
static void HAL_camera_device_enable_msg_type(struct camera_device *dev, int32_t msg_type)
{
    obj(dev)->enableMsgType(dev->common.reserved[0], msg_type);
} 

/**
 * Disable a message, or a set of messages.
 *
 * Once received a call to disableMsgType(CAMERA_MSG_VIDEO_FRAME), camera
 * HAL should not rely on its client to call releaseRecordingFrame() to
 * release video recording frames sent out by the cameral HAL before and
 * after the disableMsgType(CAMERA_MSG_VIDEO_FRAME) call. Camera HAL
 * clients must not modify/access any video recording frame after calling
 * disableMsgType(CAMERA_MSG_VIDEO_FRAME).
 */
static void HAL_camera_device_disable_msg_type(struct camera_device *dev, int32_t msg_type)
{
    obj(dev)->disableMsgType(dev->common.reserved[0], msg_type);
} 

/**
 * Query whether a message, or a set of messages, is enabled.  Note that
 * this is operates as an AND, if any of the messages queried are off, this
 * will return false.
 */
static int HAL_camera_device_msg_type_enabled(struct camera_device *dev, int32_t msg_type)
{
    return obj(dev)->msgTypeEnabled(dev->common.reserved[0], msg_type);
} 

/**
 * Start preview mode.
 */
static int HAL_camera_device_start_preview(struct camera_device *dev)
{
    LOGV("HAL_camera_device_start_preview(ClientId=%d): begin and end", dev->common.reserved[0]);
    return obj(dev)->startPreview(dev->common.reserved[0]);
} 

/**
 * Stop a previously started preview.
 */
static void HAL_camera_device_stop_preview(struct camera_device *dev)
{
    LOGV("HAL_camera_device_stop_preview(ClientId=%d): begin", dev->common.reserved[0]);
    obj(dev)->stopPreview(dev->common.reserved[0]);
    LOGV("HAL_camera_device_stop_preview(): end");
} 

/**
 * Returns true if preview is enabled.
 */
static int HAL_camera_device_preview_enabled(struct camera_device *dev)
{
    bool result = obj(dev)->previewEnabled(dev->common.reserved[0]);
    LOGV("HAL_camera_device_preview_enabled(ClientId=%d): begin and end, result:%d", dev->common.reserved[0], result);
    return result;
} 

/**
 * Request the camera HAL to store meta data or real YUV data in the video
 * buffers sent out via CAMERA_MSG_VIDEO_FRAME for a recording session. If
 * it is not called, the default camera HAL behavior is to store real YUV
 * data in the video buffers.
 *
 * This method should be called before startRecording() in order to be
 * effective.
 *
 * If meta data is stored in the video buffers, it is up to the receiver of
 * the video buffers to interpret the contents and to find the actual frame
 * data with the help of the meta data in the buffer. How this is done is
 * outside of the scope of this method.
 *
 * Some camera HALs may not support storing meta data in the video buffers,
 * but all camera HALs should support storing real YUV data in the video
 * buffers. If the camera HAL does not support storing the meta data in the
 * video buffers when it is requested to do do, INVALID_OPERATION must be
 * returned. It is very useful for the camera HAL to pass meta data rather
 * than the actual frame data directly to the video encoder, since the
 * amount of the uncompressed frame data can be very large if video size is
 * large.
 *
 * @param enable if true to instruct the camera HAL to store
 *      meta data in the video buffers; false to instruct
 *      the camera HAL to store real YUV data in the video
 *      buffers.
 *
 * @return OK on success.
 */
static int HAL_camera_device_store_meta_data_in_buffers(struct camera_device *dev, int enable)
{
    LOGV("HAL_camera_device_store_meta_data_in_buffers(%d): begin and end", enable);
    return obj(dev)->storeMetaDataInBuffers(enable);
} 

/**
 * Start record mode. When a record image is available, a
 * CAMERA_MSG_VIDEO_FRAME message is sent with the corresponding
 * frame. Every record frame must be released by a camera HAL client via
 * releaseRecordingFrame() before the client calls
 * disableMsgType(CAMERA_MSG_VIDEO_FRAME). After the client calls
 * disableMsgType(CAMERA_MSG_VIDEO_FRAME), it is the camera HAL's
 * responsibility to manage the life-cycle of the video recording frames,
 * and the client must not modify/access any video recording frames.
 */
static int HAL_camera_device_start_recording(struct camera_device *dev)
{
    LOGV("HAL_camera_device_start_recording(): begin and end");
    return obj(dev)->startRecording(dev->common.reserved[0]);
} 

/**
 * Stop a previously started recording.
 */
static void HAL_camera_device_stop_recording(struct camera_device *dev)
{
    LOGV("HAL_camera_device_stop_recording(): begin and end");
    obj(dev)->stopRecording(dev->common.reserved[0]);
} 

/**
 * Returns true if recording is enabled.
 */
static int HAL_camera_device_recording_enabled(struct camera_device *dev)
{
    bool result =obj(dev)->recordingEnabled(dev->common.reserved[0]);
    LOGV("HAL_camera_device_recording_enabled(ClientId=%d): begin and end, result:%d", dev->common.reserved[0], result);
    return result;
} 

/**
 * Release a record frame previously returned by CAMERA_MSG_VIDEO_FRAME.
 *
 * It is camera HAL client's responsibility to release video recording
 * frames sent out by the camera HAL before the camera HAL receives a call
 * to disableMsgType(CAMERA_MSG_VIDEO_FRAME). After it receives the call to
 * disableMsgType(CAMERA_MSG_VIDEO_FRAME), it is the camera HAL's
 * responsibility to manage the life-cycle of the video recording frames.
 */
static void HAL_camera_device_release_recording_frame(struct camera_device *dev, const void *opaque)
{
//    LOGV("HAL_camera_device_release_recording_frame(): begin and end");
    obj(dev)->releaseRecordingFrame(dev->common.reserved[0], opaque);
} 

/**
 * Start auto focus, the notification callback routine is called with
 * CAMERA_MSG_FOCUS once when focusing is complete. autoFocus() will be
 * called again if another auto focus is needed.
 */
static int HAL_camera_device_auto_focus(struct camera_device *dev)
{
    LOGV("HAL_camera_device_auto_focus(): begin and end");
    return obj(dev)->autoFocus(dev->common.reserved[0]);
} 

/**
 * Cancels auto-focus function. If the auto-focus is still in progress,
 * this function will cancel it. Whether the auto-focus is in progress or
 * not, this function will return the focus position to the default.  If
 * the camera does not support auto-focus, this is a no-op.
 */
static int HAL_camera_device_cancel_auto_focus(struct camera_device *dev)
{
    LOGV("HAL_camera_device_cancel_auto_focus(): begin and end");
    return obj(dev)->cancelAutoFocus(dev->common.reserved[0]);
} 

/**
 * Take a picture.
 */
static int HAL_camera_device_take_picture(struct camera_device *dev)
{
    LOGV("HAL_camera_device_take_picture(): begin and end");
    return obj(dev)->takePicture(dev->common.reserved[0]);
} 

/**
 * Cancel a picture that was started with takePicture. Calling this method
 * when no picture is being taken is a no-op.
 */
static int HAL_camera_device_cancel_picture(struct camera_device *dev)
{
    LOGV("HAL_camera_device_cancel_picture(): begin and end");
    return obj(dev)->cancelPicture(dev->common.reserved[0]);
} 

/**
 * Set the camera parameters. This returns BAD_VALUE if any parameter is
 * invalid or not supported.
 */
static int HAL_camera_device_set_parameters(struct camera_device *dev, const char *parms)
{
    LOGV("HAL_camera_device_set_parameters(): begin");
    String8 str(parms);
    CameraParameters p(str);
    status_t result = obj(dev)->setParameters(p);
    LOGV("HAL_camera_device_set_parameters(): end");
    return result;
} 

/** Return the camera parameters. */
char *HAL_camera_device_get_parameters(struct camera_device *dev)
{
    LOGV("HAL_camera_device_get_parameters(): begin and end");
    String8 str;
    CameraParameters parms = obj(dev)->getParameters();
    str = parms.flatten();
    return strdup(str.string() );
} 

void HAL_camera_device_put_parameters(struct camera_device *dev, char *parms)
{
    LOGV("HAL_camera_device_put_parameters(): begin and end");

    if ( parms )
    {
        free(parms);
    }
} 

/**
 * Send command to camera driver.
 */
static int HAL_camera_device_send_command(struct camera_device *dev, int32_t cmd, int32_t arg1, int32_t arg2)
{
    LOGV("HAL_camera_device_send_command(): begin and end, %d %d %d", cmd, arg1, arg2);
    return obj(dev)->sendCommand(dev->common.reserved[0], cmd, arg1, arg2);
} 

/**
 * Release the hardware resources owned by this object.  Note that this is
 * *not* done in the destructor.
 */
static void HAL_camera_device_release(struct camera_device *dev)
{
    LOGV("HAL_camera_device_release(): begin");
    obj(dev)->release(dev->common.reserved[0]);
    LOGV("HAL_camera_device_release(): end");
} 

/**
 * Dump state of the camera hardware
 */
static int HAL_camera_device_dump(struct camera_device *dev, int fd)
{
    LOGV("HAL_camera_device_dump(): begin and end");
    //    return obj(dev)->dump(fd);
    return 0;
} 

static int HAL_camera_device_close(struct hw_device_t *device)
{
    LOGI("HAL_camera_device_close(): begin");

    if (device)
    {
        camera_device_t *cam_device = (camera_device_t*)device;
        CameraClientRefCounter--;
        if ( CameraClientRefCounter == 0 )
        {
            LOGI("Delete MtkCameraHal instance");
            delete static_cast < MtkCameraHal * > (cam_device->priv);
            MtkCameraHalInstance = NULL;
        }
        else
        {
            LOGI("CameraClientRefCounter=%d, don't delete MtkCameraHal instance", CameraClientRefCounter);
        }
        free(g_cam_device[device->reserved[0]]);
        g_cam_device[device->reserved[0]] = 0;
    } 
    LOGI("HAL_camera_device_close(): end");
    return 0;
}

static camera_device_ops_t camera_device_ops = 
{
    HAL_camera_device_set_preview_window, 
    HAL_camera_device_set_callbacks, 
    HAL_camera_device_enable_msg_type, 
    HAL_camera_device_disable_msg_type, 
    HAL_camera_device_msg_type_enabled, 
    HAL_camera_device_start_preview, 
    HAL_camera_device_stop_preview, 
    HAL_camera_device_preview_enabled, 
    HAL_camera_device_store_meta_data_in_buffers, 
    HAL_camera_device_start_recording, 
    HAL_camera_device_stop_recording, 
    HAL_camera_device_recording_enabled, 
    HAL_camera_device_release_recording_frame, 
    HAL_camera_device_auto_focus, 
    HAL_camera_device_cancel_auto_focus, 
    HAL_camera_device_take_picture, 
    HAL_camera_device_cancel_picture, 
    HAL_camera_device_set_parameters, 
    HAL_camera_device_get_parameters, 
    HAL_camera_device_put_parameters, 
    HAL_camera_device_send_command, 
    HAL_camera_device_release, 
    HAL_camera_device_dump, 
};

static int HAL_getNumberOfCameras()
{
    int NumberOfCameras = MAX_CONCURRENT_CAMERA_CLIENT;
    LOGV("HAL_getNumberOfCameras: NumberOfCameras=%d", NumberOfCameras);
    return NumberOfCameras;
}

static int HAL_getCameraInfo(int ClientId, struct camera_info *cameraInfo)
{
    // No matter what ClientId is, we always return CAMERA_FACING_FRONT.
    LOGV("HAL_getCameraInfo(): begin and end");
    cameraInfo->facing = CAMERA_FACING_FRONT;
    cameraInfo->orientation = 0;
    return 0;
} 

static int HAL_camera_device_open(  const struct hw_module_t *module, 
                                    const char *id, 
                                    struct hw_device_t **device)
{
    int ClientId = atoi(id);

    LOGI("HAL_camera_device_open(ClientId=%d): begin", ClientId);

    if (ClientId < 0 || ClientId >= HAL_getNumberOfCameras() )
    {
        LOGE("Invalid camera ID %s", id);
        return -EINVAL;
    } 

    if (g_cam_device[ClientId])
    {
        LOGE("Cannot open camera %d (already running!)", ClientId);
        return -ENOSYS;
    }
    else
    {
        g_cam_device[ClientId] = (camera_device_t*)malloc(sizeof(camera_device_t) );
        if (!g_cam_device[ClientId])
        {
            return -ENOMEM;
        }

        if ( MtkCameraHalInstance == NULL )
        {
            LOGI("Create MtkCameraHal instance");
//altered by wangyoulin
//            MtkCameraHalInstance = new MtkCameraHal();
		MtkCameraHal *mtkcamera = new MtkCameraHal();
		if ( mtkcamera->openedOk==false)
		{
			free(g_cam_device[ClientId]);
			g_cam_device[ClientId] = 0;
			delete mtkcamera;
			return -EINVAL;
		}
		MtkCameraHalInstance = mtkcamera;
//end by wangyoulin
        }
        else
        {
            LOGI("MtkCameraHal instance exists, don't create.");
        }

        g_cam_device[ClientId]->common.tag = HARDWARE_DEVICE_TAG;
        g_cam_device[ClientId]->common.version = 1;
        g_cam_device[ClientId]->common.module = const_cast < hw_module_t * > (module);
        g_cam_device[ClientId]->common.close = HAL_camera_device_close;
        g_cam_device[ClientId]->common.reserved[0] = ClientId;
        g_cam_device[ClientId]->ops = &camera_device_ops;
        g_cam_device[ClientId]->priv = MtkCameraHalInstance;
        
        *device = (hw_device_t*)g_cam_device[ClientId];

        CameraClientRefCounter++;
        LOGI("HAL_camera_device_open(): end");
        return 0;
    }
}

static hw_module_methods_t camera_module_methods = 
{
    open: HAL_camera_device_open
};

extern "C"
{
    struct camera_module HAL_MODULE_INFO_SYM = 
    {
        common: 
        {
            tag: HARDWARE_MODULE_TAG, 
            version_major: 1, 
            version_minor: 0, 
            id: CAMERA_HARDWARE_MODULE_ID, 
            name: "MTK Camera HAL", 
            author: "Mediatek Corporation", 
            methods: &camera_module_methods,             
            dso: NULL,
            reserved:{0},
        } , 
        get_number_of_cameras: HAL_getNumberOfCameras, 
        get_camera_info: HAL_getCameraInfo
    };
}

}; // namespace android
