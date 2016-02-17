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

#ifndef ANDROID_MTK_CAMERA_HAL_H
#define ANDROID_MTK_CAMERA_HAL_H

#include <utils/threads.h>
#include <utils/RefBase.h>
#include <binder/MemoryBase.h>
#include <binder/MemoryHeapBase.h>
#include <hardware/camera.h>
#include <hardware/gralloc.h>
#include <camera/CameraParameters.h>
#include "MtkV4L2Camera.h"

namespace android
{
#define SUPPORT_MJPEG false                      // Enable MJPEG camera support
#define USE_HW_VIDEO_ENCODER false               // true: use HW encoder, false: use SW encoder (for video recorder)
#define ASSIGN_SPECIFIC_CAMERA_PARAMETER false  // for debugging
#define INITIAL_SKIP_FRAME 3                    // skip first 3 frames
#define MAX_CONCURRENT_CAMERA_CLIENT 1          // if we run gesture service in background, set to 2

#define SUPPORT_FDFR false                       // Enable Face Detection / Face Recognition.
#define FDFR_DEMO_WORK_AROUND true              // For FDFR demo, work around solution.
#define FDFR_DEMO_WORK_AROUND_MAX_USERS 100     // For FDFR demo, work around solution.
#define FDFR_DEMO_WORK_AROUND_THRESHOLD 2.0     // For FDFR demo, work around solution.
#define FD_RE_INIT_COUNTER_THRESHOLD 0         // -1:don't re-init, 0:re-init every time
#define FDFR_FR_VECTOR_LENGTH 192               // Don't change it.


class MtkCameraHal: public virtual RefBase
{
    public:
        MtkCameraHal();
        virtual ~MtkCameraHal();
        virtual void setCallbacks(int ClientId, camera_notify_callback notify_cb, camera_data_callback data_cb, camera_data_timestamp_callback data_cb_timestamp, camera_request_memory get_memory, void *user);
        virtual status_t setParameters(const CameraParameters &params);
//by wang youlin
        virtual CameraParameters getParameters();//const;
//end
        virtual status_t setPreviewWindow(int ClientId, preview_stream_ops *w);
        virtual void enableMsgType(int ClientId, int32_t msgType);
        virtual void disableMsgType(int ClientId, int32_t msgType);
        virtual bool msgTypeEnabled(int ClientId, int32_t msgType);
        virtual status_t startPreview(int ClientId);
        virtual void stopPreview(int ClientId);
        virtual bool previewEnabled(int ClientId);
        virtual status_t startRecording(int ClientId);
        virtual void stopRecording(int ClientId);
        virtual bool recordingEnabled(int ClientId);
        virtual void releaseRecordingFrame(int ClientId, const void *opaque);
        virtual status_t storeMetaDataInBuffers(bool enable);
        virtual void release(int ClientId);
        virtual status_t autoFocus(int ClientId);
        virtual status_t cancelAutoFocus(int ClientId);
        virtual status_t takePicture(int ClientId);
        virtual status_t cancelPicture(int ClientId);
        virtual status_t sendCommand(int ClientId, int32_t cmd, int32_t arg1, int32_t arg2);
        

        
    private:
        class PreviewThread: public Thread
        {
            MtkCameraHal *mCameraHal;
        public:
            PreviewThread(MtkCameraHal *hw): Thread(false), mCameraHal(hw)
            {}
            virtual void onFirstRef()
            {
                    run("CameraPreviewThread", PRIORITY_URGENT_DISPLAY);
            }
            virtual bool threadLoop()
            {
                    mCameraHal->previewThreadWrapper();
                    return false;
            }
        };

        void initDataStructure();
        void initThreads();
        void initDefaultParameters();
        int previewThreadWrapper();
        int previewThread();
        status_t startPreviewInternal(int ClientId);
        void stopPreviewInternal(int ClientId);
        bool isSupportedPreviewSize(const int width, const int height)const;
        bool isSupportedPictureSize(const int width, const int height)const;
        bool isSupportedVideoSize(const int width, const int height)const;
//added by wang youlin
void setParametersValue();
//end by wang youlin

        mutable Mutex mPreviewWindowLock[MAX_CONCURRENT_CAMERA_CLIENT];
        preview_stream_ops *mPreviewWindow[MAX_CONCURRENT_CAMERA_CLIENT];
        
        camera_memory_t *mPreviewHeap;
        camera_memory_t *mPictureHeap[MAX_CONCURRENT_CAMERA_CLIENT];
        camera_memory_t *mRecordHeap[MAX_CONCURRENT_CAMERA_CLIENT];

        camera_notify_callback mNotifyCb[MAX_CONCURRENT_CAMERA_CLIENT];
        camera_data_callback mDataCb[MAX_CONCURRENT_CAMERA_CLIENT];
        camera_data_timestamp_callback mDataCbTimestamp[MAX_CONCURRENT_CAMERA_CLIENT];
        camera_request_memory mGetMemoryCb[MAX_CONCURRENT_CAMERA_CLIENT];
        void *mCallbackCookie[MAX_CONCURRENT_CAMERA_CLIENT];
        int32_t mMsgEnabled[MAX_CONCURRENT_CAMERA_CLIENT];
        bool ClientOpened[MAX_CONCURRENT_CAMERA_CLIENT];
        bool mHandlePreview[MAX_CONCURRENT_CAMERA_CLIENT];
        bool mHandleAutoFocus[MAX_CONCURRENT_CAMERA_CLIENT];
        bool mHandleTakePicture[MAX_CONCURRENT_CAMERA_CLIENT];
        bool mHandleRecording[MAX_CONCURRENT_CAMERA_CLIENT];

        /* used by preview thread to block until it's told to run */
        sp < PreviewThread > mPreviewThread;
        mutable Mutex mPreviewLock;
        mutable Condition mPreviewCondition;
        mutable Condition mPreviewStoppedCondition;
        bool mPreviewRunning;
        bool mExitPreviewThread;

        static gralloc_module_t const *mGrallocHal;
        CameraParameters mParameters;
        MtkV4L2Camera *mV4L2Camera;
        int mSkipFrame;
        Vector < Size > mSupportedPreviewSizes;
        Vector < Size > mSupportedPictureSizes;
        Vector < Size > mSupportedVideoSizes;
        int mRefCount;
        bool mUseMetaDataBufferMode;
//added by wang youlin
	int			mInterpolationSize;
public:	bool			openedOk;
//end by wang youlin
};

}; // namespace android

#endif
