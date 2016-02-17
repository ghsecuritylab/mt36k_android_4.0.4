/*
**
** Copyright 2009, The Android Open Source Project
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

#ifndef ANDROID_CMPBPLAYER_H
#define ANDROID_CMPBPLAYER_H

#include <media/MediaPlayerInterface.h>
#include <libsonivox/eas.h>
#include <utils/Vector.h>


#include "IMtkPb_ErrorCode.h"
#include "IMtkPb_Ctrl.h"
#include "IMtkPb_Ctrl_DTV.h"
#include "Cmpb_protocol.h"
#define HW_COMPOSER


//#ifdef HW_COMPOSER
//class ANativeWindow;
//#endif

namespace android {

#define ASYNC_READ      1

enum
{
   STATE_ERROR = -1,
   STATE_IDLE  = 0,
   STATE_INITED,
   STATE_PREPARING,
   STATE_PREPARED,
   STATE_STARTING,
   STATE_STARTED,
   STATE_STOPPED,
   STATE_PAUSED,
   STATE_COMPLETE,
   STATE_END   =0XFF
};

typedef enum
{
    MTK_MEDIA_INFO_BUFFER_START      = 701,
    MTK_MEDIA_INFO_BUFFER_END        = 702,

    MTK_MEDIA_INFO_FILE_NOT_SUPPORT      = 0x8001,
    MTK_MEDIA_INFO_FILE_CORRUPT          = 0x8002,
    MTK_MEDIA_INFO_VID_CODEC_NOT_SUPPORT = 0x8003,
    MTK_MEDIA_INFO_AUD_CODEC_NOT_SUPPORT = 0x8004,
    MTK_MEDIA_INFO_BUFFER_UNDERFLOW      = 0x8005,
    MTK_MEDIA_INFO_BUFFER_READY          = 0x8006,
    MTK_MEDIA_INFO_OPEN_FILE_FAILED      = 0x8007,
    MTK_MEDIA_INFO_FILE_NO_SEEKABLE      = 0x8008,
    MTK_MEDIA_INFO_GET_DATA_FAIL		 = 0x8009,
}MTK_MEDIA_INFO_TYPE;

typedef enum
{
    MTK_MEDIA_SEEK_DONE_OP_NONE       = 0,
    MTK_MEDIA_SEEK_DONE_OP_PAUSE
}MTK_MEDIA_SEEK_DONE_OP;


typedef struct
{
    IMTK_PB_HANDLE_T         hAsyncRdHdl;
    uint8_t*                 pu1BufAddr;
    uint32_t                 u4Datalen;
    int64_t                  i8Offset;

    ///////notify param///////
    IMtkPb_Ctrl_Pull_Nfy_Fct pfnNotify;
    void*                    pvRdAsyTag;
    void*                    pvAppTag;
    
    uint32_t*                pu4ReqId;

    bool                     bAbort;
}ASYNC_READ_Q_ITEM_T;

class CmpbPlayer : public MediaPlayerInterface {
public:
    CmpbPlayer();
    CmpbPlayer(bool metainfo);
    virtual ~CmpbPlayer();

    virtual status_t initCheck();

    virtual status_t setDataSource(
            const char *url, const KeyedVector<String8, String8> *headers);

    virtual status_t setDataSource(int fd, int64_t offset, int64_t length);
    //virtual status_t setVideoSurface(const sp<ISurface> &surface);            
    virtual status_t prepare();
    virtual status_t prepareAsync();
    virtual status_t start();
    virtual status_t stop();
    virtual status_t pause();
    virtual bool isPlaying();
    virtual status_t seekTo(int msec);
    virtual status_t setAudioTrack(int track_num);
    virtual status_t setSubtitleTrack(int track_num);
	virtual status_t setPlaySeamless(bool bIsSeamless);
    virtual status_t setExSubtitleURI(const char *url);
    virtual status_t getAudioTrackNs(int *trk_nums); 
    virtual status_t getSubtitleTrackNs(int *trk_nums);
    //virtual status_t seekTo(int msec, int state);
    virtual status_t getCurrentPosition(int *msec);
    virtual status_t getDuration(int *msec);
    virtual status_t reset();
    virtual status_t setLooping(int loop);
    virtual player_type playerType();
    virtual status_t invoke(const Parcel &request, Parcel *reply);
    virtual void setAudioSink(const sp<AudioSink> &audioSink);
    virtual status_t setVolume(float leftVolume, float rightVolume);
    virtual status_t suspend();
    virtual status_t resume();

    virtual status_t release();

    virtual status_t getMetadata(
            const media::Metadata::Filter& ids, Parcel *records);
    
    status_t getCmpbHandle(IMTK_PB_HANDLE_T*   PlayHdl);

    CmpbProtocolPlayer* getCmpbProtocolPlayer(){return m_pProtocolPlayer;} 

    int32_t getCmpbPlayerState(){return mState;}

    void setCmpbPlayerState(int32_t state);
#ifdef HW_COMPOSER
		status_t setVideoSurfaceTexture(const sp<ISurfaceTexture>&);
		void setSurfaceTransparency(bool transparent);
#else
    virtual status_t setVideoSurfaceTexture(const sp<ISurfaceTexture>&){return OK;}
#endif
    virtual status_t setParameter(int, const Parcel&){return OK;}
    virtual status_t getParameter(int, Parcel*){return OK;}
    status_t protocol_start();
    
private:
    CmpbPlayer(const CmpbPlayer &);
    CmpbPlayer &operator=(const CmpbPlayer &);
    
    void Init();

    status_t Internal_prepareAsync();
    
    status_t ResetNosync();
    status_t StopPlayer(bool bIsPlaying);
    status_t StartPlayer();

    IMTK_PB_ERROR_CODE_T OpenCmpbRetry(IMTK_PB_HANDLE_T*                 phHandle,
                                        IMTK_PB_CTRL_BUFFERING_MODEL_T    eBufferingModel,
                                        IMTK_PB_CTRL_OPERATING_MODEL_T    eOperatingModel,
                                        uint8_t*                          pu1Profile);

    status_t setVideoRect(int i4left, int i4top, int i4right, int bottom);
    status_t getDstSrcRect(int i4left, int i4top, int i4right, int bottom);

    void initHeaders(const KeyedVector<String8, String8> *overrides);
    void initCookie(const KeyedVector<String8, String8> *cookie);
    
    static IMTK_PB_CB_ERROR_CODE_T FmPullOpen(IMTK_PB_HANDLE_T           hHandle,
                                            IMTK_PULL_HANDLE_T*        phPullSrc,
                                            void*                      pvAppTag);
    static IMTK_PB_CB_ERROR_CODE_T FmPullClose(IMTK_PULL_HANDLE_T         hPullSrc,
                                            void*                      pvAppTag);
    static IMTK_PB_CB_ERROR_CODE_T FmPullRead(IMTK_PULL_HANDLE_T     hPullSrc,
                                          void*                  pvAppTag,
                                          uint8_t*               pu1DstBuf,
                                          uint32_t               u4Count, 
                                          uint32_t*              pu4Read);
    //void LinuxAioCallback(sigval_t sigval);
    static IMTK_PB_CB_ERROR_CODE_T FmPullReadAsync(IMTK_PULL_HANDLE_T         hPullSrc,
                                                   void*                      pvAppTag,
                                                   uint8_t*                   pu1Dst,
                                                   uint32_t                   u4DataLen,
                                                   IMtkPb_Ctrl_Pull_Nfy_Fct   pfnNotify,
                                                   void*                      pvRdAsyTag,   
                                                   uint32_t*                  pu4ReqId);
    static IMTK_PB_CB_ERROR_CODE_T FmPullAbortReadAsync(IMTK_PULL_HANDLE_T     hPullSrc,
                                                         void*                  pvAppTag,
                                                         uint32_t               u4ReqId);
    static IMTK_PB_CB_ERROR_CODE_T FmPullByteSeek(IMTK_PULL_HANDLE_T         hPullSrc,
                                                 void*                      pvAppTag,
                                                 int64_t                    i8SeekPos,
                                                 uint8_t                    u1Whence,
                                                 uint64_t*                  pu8CurPos);
    static IMTK_PB_CB_ERROR_CODE_T FmPullGetInputLen(IMTK_PULL_HANDLE_T  hPullSrc,
                                                  void*               pvAppTag,
                                                  uint64_t*           pu8Len);
    static IMTK_PB_CB_ERROR_CODE_T MediaPbCallback(IMTK_PB_CTRL_EVENT_T       eEventType,
                                               void*                      pvTag,
                                               uint32_t                   u4Data);


    bool                mForMetaInfo;
    bool                mSeekDoneNeedPlay;
    
    MTK_MEDIA_SEEK_DONE_OP  mSeekDoneOp;
    
    bool                mReseted;
    bool                mVidUnplayble;
    
    int32_t             mState;
    bool                mLoop;
    bool                mHttpURI;
    bool                mMmsURI;
    bool                mSeeking;
    bool                mStart;
    IMTK_PB_HANDLE_T    mPlayHdl;
    
    EAS_FILE            mFileLocator;
    Mutex               mMutex;
    String8             mHeaders;
    KeyedVector<String8, String8> mUriHeaders;
    String8*            m_pExsubttURI;
    String8             mCookie;   

    int64_t             mStartTime;
    uint32_t            mCurBufPercent;

    IMTK_PB_CTRL_RECT_T mDstVidRect;
    IMTK_PB_CTRL_RECT_T mSrcVidRect;

    /*Insert http living streaming player here*/
    bool                     m_bProtocolPlayerUsed;
    CmpbProtocolPlayer*      m_pProtocolPlayer;
    
    //for disable blue mute when play mmp
    IMTK_PB_CTRL_BG_COLOR_T  mColor;
    
    int16_t             m_TsProgIdx;
    
#if   ASYNC_READ
    //for read async
    static Vector <ASYNC_READ_Q_ITEM_T*> mAsyncReadQueue;    
    static Mutex                         mQueueVectorLock;
    static Mutex                         mAbortReadLock;
    static uint32_t                      mQueueItemCnt;

    bool                                 mAbortRead;
    
    static void  AsyncReadThread(void* param);
    static void AsyncReadThreadInit();
#endif

#ifdef HW_COMPOSER
		sp<ANativeWindow> mNativeWindow; 
#endif

};

}  // namespace android

#endif  // ANDROID_CMPBPLAYER_H
