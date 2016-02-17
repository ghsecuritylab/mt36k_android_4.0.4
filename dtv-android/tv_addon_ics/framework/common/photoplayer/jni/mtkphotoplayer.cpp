#include <stdlib.h>
#include <stdint.h>
#include <jni.h>
#include <JNIHelp.h>

#include <android/log.h>
#include "mtimage.h"
#include "mtvdo.h"
#include "mttype.h"
#include "mtpmx.h"
#include "mtcommon.h"
#include "mtvdecex.h"
#include <stdint.h>

//#include <sys/ipc.h>
//#include <sys/shm.h>

#include "core/SkBitmap.h"
#ifdef __cplusplus
extern "C" {
#endif


static BOOL _b_inited = FALSE;
static UINT32  ScreenWidth = 0;
static UINT32  ScreenHeight = 0;
#define IMAGE_DECODE_0    ((uint8_t)0)
#define IMAGE_DECODE_1    ((uint8_t)1)

#ifndef ANDROID_HW_RETRY_HOW
#define ANDROID_HW_RETRY_HOW     2
#endif

static INT32 vdpfmt = 0;;

typedef struct IMAGE_ENV_T 
{
    JavaVM* vm;
    jobject thiz;
	jclass mtkphotoplayer;
	MTIMAGE_IMGMETA_T Meta;
    MTIMAGE_HANDLE hHandle;
	uint8_t        decode;
} IMAGE_ENV_T;

void ImageCallback (MTIMAGE_CALLBACK_TYPE_T eCallbackType, 
					   UINT32 u4ErrorType,
                       UINT32 u4Param)
{       

	__android_log_print(ANDROID_LOG_DEBUG, "MtkImagePlayerJni", "####  Call OnMtkPhotoHanderEvent:u4Param = %d", (int)u4Param);

    IMAGE_ENV_T*  info = (IMAGE_ENV_T*) u4Param;
    JNIEnv*             env;
   
	__android_log_print(ANDROID_LOG_DEBUG, "MtkImagePlayerJni", "####  Call OnMtkPhotoHanderEvent:info->mtkphotoplayer = %d", (int)info->mtkphotoplayer); 
    jmethodID           BuildEventMethod; 
	
    jint ret = ((info->vm))->AttachCurrentThread((JNIEnv**)&env, NULL);
    BuildEventMethod = env->GetMethodID(info->mtkphotoplayer,"OnMtkPhotoHandlerEvent","(I)V");  
    
    // call OnMtkPhotoHanderEvent(EVENT_UPDATE);
    __android_log_print(ANDROID_LOG_DEBUG, "MtkImagePlayerJni", "####  Call OnMtkPhotoHanderEvent: %d", (int)eCallbackType);
    env->CallVoidMethod(info->thiz, BuildEventMethod, (int)eCallbackType);
    
    return;
}
                              
/*
* Class:     Java_com_mediatek_common_PhotoPlayer_MtkPhotoPlayer_nativeVideoConnect
* Method:    nativeVideoConnect
* Signature: ()I
*/

JNIEXPORT void JNICALL Java_com_mediatek_common_PhotoPlayer_MtkPhotoPlayer_nativeVideoConnect
    (JNIEnv * env, jobject thiz, jboolean flag, jint fmt){
    
	MT_RESULT_T     e_return   = MTR_OK;
	MTIMAGE_STATUS_T status;
	MTIMAGE_HANDLE   handle = (VOID*)0xfffffff;
	int _count = 0;
	__android_log_print(ANDROID_LOG_DEBUG, "MtkImagePlayerJni", "####  Call MtkPhotoPlayer init video plane ########");

    
    //here, only connect flase once when exit fullsreen.         
    if(flag==0)
    {        
    
    	while (_count < ANDROID_HW_RETRY_HOW && (e_return = MTIMAGE_GetInfo(IMAGE_DECODE_0,handle,&status)) != MTR_OK)
    	{
    		sleep(1);
    		_count ++;
    	}
    
    	if(e_return != MTR_OK)
    	{
    		__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "####  Call nativeVideoConnect:MTIMAGE_GetInfo decode: %d return %d", IMAGE_DECODE_0, e_return);
    		jniThrowException(env,"com/mediatek/common/PhotoPlayer/NotSupportException","nativeVideoConnect Failed!");
    		return ;
    	}
    	
    	_count = 0;
    
    	while (_count < ANDROID_HW_RETRY_HOW && (e_return = MTIMAGE_GetInfo(IMAGE_DECODE_1,handle,&status)) != MTR_OK)
    	{
    		sleep(1);
    		_count ++;
    	}
    
    	if(e_return != MTR_OK)
    	{
    		__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "####  Call nativeVideoConnect:MTIMAGE_GetInfo decode: %d return %d", IMAGE_DECODE_1, e_return);
    		jniThrowException(env,"com/mediatek/common/PhotoPlayer/NotSupportException","nativeVideoConnect Failed!");
    		return ;
    	}
    	
    	e_return = MTIMAGE_Connect(IMAGE_DECODE_0,flag, (INT32)fmt);
        
    	if(e_return != MTR_OK)
    	{
    		if (!flag)
    		{
    			MTIMAGE_Connect(IMAGE_DECODE_1,flag, (INT32)fmt);
    		}
    		__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "####  Call nativePhotoOpen:MTIMAGE_Connect decode %d fail %d", IMAGE_DECODE_0, e_return);
    		jniThrowException(env,"com/mediatek/common/PhotoPlayer/NotSupportException","MTIMAGE_Connect Failed!");
    		return ;
    	}
    	else
        {
            __android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "####  Call nativePhotoOpen:MTIMAGE_Connect decode %d success %d\n", IMAGE_DECODE_0, e_return);	
        }	    
    	
    	e_return = MTIMAGE_Connect(IMAGE_DECODE_1,flag, (INT32)fmt);
    	if(e_return != MTR_OK)
    	{
    		if (flag)
    		{
    			MTIMAGE_Connect(IMAGE_DECODE_0,!flag, (INT32)fmt);
    		}
    		__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "####  Call nativePhotoOpen:MTIMAGE_Connect decode %d fail %d", IMAGE_DECODE_1, e_return);
    		jniThrowException(env,"com/mediatek/common/PhotoPlayer/NotSupportException","MTIMAGE_Connect Failed!");
    		return ;
    	}
    	else
        {
            __android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "####  Call nativePhotoOpen:MTIMAGE_Connect decode %d success %d\n", IMAGE_DECODE_1, e_return);	
        } 
    }
	if (!_b_inited)
	{
		MTAL_Init();
        MTVDO_Init();
		MTPMX_Init();
		MTPMX_PANEL_GetResolution(&ScreenWidth,&ScreenHeight);
		_b_inited = TRUE;
	}
	
	vdpfmt = (INT32)fmt;

	if (flag){

		MTVDECEX_SetVideoSrc(MTVDO_MAIN, MTVDECEX_SRC_DTV1);
	    /* Enable Video Plane and Set Video Plane to Normal Mode */
	    MTVDO_SetEnable(MTVDO_MAIN, TRUE);
	    MTVDO_SetMode(MTVDO_MAIN, VDO_MODE_NORMAL);
	}
	
	return;
}

JNIEXPORT jint JNICALL Java_com_mediatek_common_PhotoPlayer_MtkPhotoPlayer_nativeGetScreenWidth
    (JNIEnv * env, jobject thiz){
	
	return (jint)ScreenWidth;
}

JNIEXPORT jint JNICALL Java_com_mediatek_common_PhotoPlayer_MtkPhotoPlayer_nativeGetScreenHeight
    (JNIEnv * env, jobject thiz){
	return (jint)ScreenHeight;
}



JNIEXPORT void JNICALL Java_com_mediatek_common_PhotoPlayer_MtkPhotoHandler_nativePlay
    (JNIEnv * env, jobject thiz, int handle, jobject display_region){
	IMAGE_ENV_T*    image_env = (IMAGE_ENV_T *)handle;
	jfieldID   	    field;
	jclass 		    rectClass = env->FindClass("android/graphics/Rect");
	jint            bottom, top, left, right;
	MTIMAGE_PARAM_T ImgSetting;
	MT_RESULT_T     e_return   = MTR_OK;
	MTVDO_REGION_T  rRegion;

	__android_log_print(ANDROID_LOG_DEBUG, "MtkImagePlayerJni", "#### Play file ########");
	
	field = env->GetFieldID(rectClass, "bottom", "I");
	bottom = env->GetIntField(display_region, field);

	field = env->GetFieldID(rectClass, "top", "I");
	top = env->GetIntField(display_region, field);

	field = env->GetFieldID(rectClass, "left", "I");
	left = env->GetIntField(display_region, field);

	field = env->GetFieldID(rectClass, "right", "I");
	right = env->GetIntField(display_region, field);

	ImgSetting.eRotation  = MT_IMAGE_ROTATE_0;
	ImgSetting.fgDisplay  = TRUE;
	ImgSetting.fgIsSlideShow = FALSE;
	ImgSetting.fgClean = FALSE;
	ImgSetting.rDstRegion.u4Height = 1080;
	ImgSetting.rDstRegion.u4Width  = 1920;
	ImgSetting.rDstRegion.u4X      = 0;
	ImgSetting.rDstRegion.u4Y      = 0;
	ImgSetting.u1PlaneId           = MTPMX_MAIN;

	 rRegion.u4X = 0;
     rRegion.u4Y = 0;
     rRegion.u4Width = 10000;
     rRegion.u4Height = 10000;
     MTVDO_SetSrcRegion(MTVDO_MAIN, &rRegion);
     MTVDO_SetOutRegion(MTVDO_MAIN, &rRegion);
	
	e_return = MTIMAGE_Display(image_env->decode, image_env->hHandle,&ImgSetting,FALSE, NULL);
	if(e_return != MTR_OK)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoPlayer", "####  Call nativePlay:MTIMAGE_Display decode %d fail %d",image_env->decode,  e_return);
		jniThrowException(env,"com/mediatek/common/PhotoPlayer/NotSupportException","Photo Display Failed!");
		return;
	}
	return;		
		
}

JNIEXPORT jint JNICALL Java_com_mediatek_common_PhotoPlayer_MtkPhotoHandler_nativePhotoOpen
    (JNIEnv * env, jobject thiz, jstring filename, int decode)
{
	MT_RESULT_T  e_return   = MTR_OK;
	char*        image_file = NULL;
	IMAGE_ENV_T*  envInfo   = (IMAGE_ENV_T*)malloc(sizeof(IMAGE_ENV_T));
	jint ret = env->GetJavaVM( &envInfo->vm);
	int _count = 0;
	MTIMAGE_STATUS_T status;
	MTIMAGE_HANDLE   handle = (VOID*)0xfffffff;
	if(ret < 0 || envInfo == NULL)
	{
		return 0;
	}

	envInfo->thiz = env->NewGlobalRef(thiz);
	envInfo->hHandle = NULL;

	image_file = (char*)env->GetStringUTFChars(filename, NULL);
	envInfo->decode = (uint8_t)decode;



	while (_count < 2 && (e_return = MTIMAGE_GetInfo(envInfo->decode,handle,&status)) != MTR_OK)
	{
		sleep(1);
		_count ++;
	}

	if(e_return != MTR_OK)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "####  Call nativePhotoOpen:MTIMAGE_GetInfo decode: %d return %d", envInfo->decode, e_return);
		env->ReleaseStringUTFChars(filename, image_file);      
		free (envInfo);
		jniThrowException(env,"com/mediatek/common/PhotoPlayer/NotSupportException","MTIMAGE_Open Failed!");
		return 0;
	}
	
	//here, connect ture every file
	e_return = MTIMAGE_Connect(envInfo->decode,TRUE, vdpfmt);
	if(e_return != MTR_OK)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "####  Call nativePhotoOpen:MTIMAGE_Connect decode %d fail %d", envInfo->decode, e_return);
		env->ReleaseStringUTFChars(filename, image_file);
		free (envInfo);
		jniThrowException(env,"com/mediatek/common/PhotoPlayer/NotSupportException","MTIMAGE_Open Failed!");
		return 0;
	}
	
	__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "####  Call nativePhotoOpen: %s ", image_file);

	e_return = MTIMAGE_Open(envInfo->decode,&(envInfo->hHandle), image_file);
	
	
	if(e_return != MTR_OK)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "####  Call nativePhotoOpen:MTIMAGE_Open %s fail %d", image_file, e_return);
		env->ReleaseStringUTFChars(filename, image_file);      
		free(envInfo);
		jniThrowException(env,"com/mediatek/common/PhotoPlayer/NotSupportException","MTIMAGE_Open Failed!");
		return 0;
	}
	env->ReleaseStringUTFChars(filename, image_file);
	jclass mtkphotoplayer = env->FindClass("com/mediatek/common/PhotoPlayer/MtkPhotoHandler");
	envInfo->mtkphotoplayer = (jclass)env->NewGlobalRef(mtkphotoplayer); 

	__android_log_print(ANDROID_LOG_DEBUG, "MtkImagePlayerJni", "####  Call nativePhotoOpen:envInfo = %d", (int)envInfo);
	__android_log_print(ANDROID_LOG_DEBUG, "MtkImagePlayerJni", "####  Call nativePhotoOpen:envInfo->mtkphotoplayer = %d", (int)envInfo->mtkphotoplayer);
	MTIMAGE_RegCb(envInfo->decode,envInfo->hHandle, ImageCallback, (UINT32)envInfo);

	e_return = MTIMAGE_GetImgMetaData(envInfo->decode,envInfo->hHandle, &(envInfo->Meta), NULL, 0);
	
	if(e_return != MTR_OK)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "####  Call nativePhotoOpen:MTIMAGE_GetImgMetaData fail %d", e_return);
		e_return = MTIMAGE_Close(envInfo->decode,envInfo->hHandle);
		if (e_return != MTR_OK)
		{
			__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "####  Call nativePhotoOpen:MTIMAGE_Close fail %d", e_return);  
		}
		free(envInfo);
		jniThrowException(env,"com/mediatek/common/PhotoPlayer/NotSupportException","MTIMAGE_GetImgMetaData Failed!");
		return 0;
	}
	__android_log_print(ANDROID_LOG_DEBUG, "MtkImagePlayerJni", "####  Call MTIMAGE_GetImgMetaData:width = %d", envInfo->Meta.u4Width);
	__android_log_print(ANDROID_LOG_DEBUG, "MtkImagePlayerJni", "####  Call MTIMAGE_GetImgMetaData:height = %d", envInfo->Meta.u4Height);
	
	envInfo->Meta.u4Orientation = 0;
	
	return (jint)envInfo;		
}

JNIEXPORT jint JNICALL Java_com_mediatek_common_PhotoPlayer_MtkPhotoHandler_nativePhotoClose
    (JNIEnv * env, jobject thiz, int handle)
{
	IMAGE_ENV_T*    envInfo = (IMAGE_ENV_T *)handle;
	MT_RESULT_T     e_return = MTR_OK;

	__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "####  Call nativePhotoClose");

	if(envInfo->hHandle) 
	{
		e_return = MTIMAGE_Stop(envInfo->decode,envInfo->hHandle);
		if (e_return != MTR_OK)
		{
			free(envInfo);
			jniThrowException(env,"com/mediatek/common/PhotoPlayer/NotSupportException","MTIMAGE_Stop Failed!");
		}

		e_return = MTIMAGE_Close(envInfo->decode,envInfo->hHandle);
		
		free(envInfo);
	if (e_return != MTR_OK)
	{
		jniThrowException(env,"com/mediatek/common/PhotoPlayer/NotSupportException","MTIMAGE_Close Failed!");
	}
	}
	return e_return;
}


JNIEXPORT jint JNICALL Java_com_mediatek_common_PhotoPlayer_MtkPhotoHandler_nativePhotoDecode
    (JNIEnv * env, jobject thiz, int handle, jint w, jint h)
{
	IMAGE_ENV_T*    envInfo  = (IMAGE_ENV_T *)handle;
	MT_RESULT_T     e_return = MTR_OK;

	__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "#### Decode w: %d h: %d", w, h);
	
	if(envInfo->hHandle) 
	{
	
		MTIMAGE_SetDecResolution(envInfo->decode,envInfo->hHandle, 0, 0, 0);
	
		e_return = MTIMAGE_Start(envInfo->decode,envInfo->hHandle);
	
		if (e_return != MTR_OK)
		{
			jniThrowException(env,"com/mediatek/common/PhotoPlayer/NotSupportException","MTIMAGE_SetDecResolution Failed!");
		}

		MTIMAGE_WaitDecFinish(envInfo->decode);
	}
	
	return e_return;
}


JNIEXPORT jint JNICALL Java_com_mediatek_common_PhotoPlayer_MtkPhotoHandler_nativePhotoGetWidth
    (JNIEnv * env, jobject thiz, jint handle)
{
    IMAGE_ENV_T*    envInfo  = (IMAGE_ENV_T *)handle;

	if (envInfo->hHandle)
	{
		return ((envInfo->Meta.u4Width != 0) ? envInfo->Meta.u4Width : ScreenWidth);
	}
	return 0;
}


JNIEXPORT jint JNICALL Java_com_mediatek_common_PhotoPlayer_MtkPhotoHandler_nativePhotoGetHeight
    (JNIEnv * env, jobject thiz, jint handle)
{
    IMAGE_ENV_T*    envInfo  = (IMAGE_ENV_T *)handle;

	if (envInfo->hHandle)
	{
		return ((envInfo->Meta.u4Height != 0) ? envInfo->Meta.u4Height : ScreenHeight);
	}
	return 0;
}


JNIEXPORT jint JNICALL Java_com_mediatek_common_PhotoPlayer_MtkPhotoHandler_nativePhotoGetRotation
    (JNIEnv * env, jobject thiz, jint handle)
{
    IMAGE_ENV_T*    envInfo  = (IMAGE_ENV_T *)handle;

	if (envInfo->hHandle)
	{
		switch(envInfo->Meta.u4Orientation)
		{
			case 2:
				return 4;
			case 3:
				return 2;
			case 4:
				return 6;
			case 5:
				return 7;
			case 6:
				return 1;
			case 7:
				return 5;
			case 8:
				return 3;
			case 0:
			case 1:
			default:
				return 0;

		}
	}
	return 0;
}

JNIEXPORT jint JNICALL Java_com_mediatek_common_PhotoPlayer_MtkPhotoHandler_nativePhotoSetRatio
    (JNIEnv * env, jobject thiz, jint handle, jshort ratio)
{
    IMAGE_ENV_T*    envInfo  = (IMAGE_ENV_T *)handle;
	MT_RESULT_T     e_return = MTR_OK;

	if (envInfo->hHandle)
	{
		e_return = MTIMAGE_SetDecRatio(envInfo->decode,envInfo->hHandle, (UINT16)ratio);
	}

	
	if (e_return != MTR_OK)
	{
		jniThrowException(env,"com/mediatek/common/PhotoPlayer/NotSupportException","MTIMAGE_SetDecRatio Failed!");
	}
	return e_return;
}


#ifdef __cplusplus

}
#endif
