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

#ifndef ANDROID_CMPBMETADATARETRIEVER_H
#define ANDROID_CMPBMETADATARETRIEVER_H

#include <utils/threads.h>
#include <utils/Errors.h>
#include <media/MediaMetadataRetrieverInterface.h>
#include <private/media/VideoFrame.h>
#include <CmpbPlayer.h>

namespace android {

#define USE_SINGLE_META_THREAD   1

#if USE_SINGLE_META_THREAD
typedef enum
{
    META_DATA_CMD_PREPARE,
    META_DATA_CMD_CAPFRM,
    META_DATA_CMD_EX_ALBART,
    META_DATA_CMD_GETMETA
} META_DATA_CMD_T;

typedef struct
{
    META_DATA_CMD_T       eCmd;
    void*                 pvObj;
    int                   i4Param;    
}META_DATA_Q_ITEM_T;

#endif

class CmpbMetadataRetriever : public MediaMetadataRetrieverInterface {
public:
                                   CmpbMetadataRetriever();
                                   ~CmpbMetadataRetriever();

    virtual status_t                setDataSource(const char *url, const KeyedVector<String8, String8> *headers);
    virtual status_t                setDataSource(int fd, int64_t offset, int64_t length);
    virtual const char*             extractMetadata(int keyCode);
    virtual VideoFrame*             getFrameAtTime(int64_t timeUs, int option);
    MediaAlbumArt*                  extractAlbumArt();

private:
    static const uint32_t   MAX_METADATA_STRING_LENGTH  = 128;
    static const uint32_t   THUMBNAIL_BUFFER_SIZE       = 128*1024;
    void                    clearMetadataValues();
    status_t                GetMetadataValues(int keyCode);

    VideoFrame*             Internal_captureFrame();
    MediaAlbumArt*          Internal_extractAlbumArt(); 
    status_t                Internal_GetMetadataValues(int keyCode);

    Mutex                   mLock;
    sp<CmpbPlayer>          mCmpbPlayer;
    char                    mMetadataValues[1][MAX_METADATA_STRING_LENGTH];  
    char                    mMetadataTmpBuf[MAX_METADATA_STRING_LENGTH];  

#if USE_SINGLE_META_THREAD
    char                    mReserved[20];
    status_t                mPrepareRet; 
    VideoFrame*             mCapFrm;
    MediaAlbumArt*          mAlbArt;
    status_t                mMetaDataRet;
    char                    mReserved1[20];
    IMTK_PB_HANDLE_T        mSemaMetaInfo;
    
    static void  MetaDataThread(void* param);
    static void  MetaDataThreadInit();
#endif
};

}; // namespace android

#endif // ANDROID_CMPBMETADATARETRIEVER_H
