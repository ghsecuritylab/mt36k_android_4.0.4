#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <math.h>
#include <string.h>
#include <pthread.h>
#include <assert.h>
#include <semaphore.h>

#include <JNIHelp.h>

#include <android/log.h>
#include "mtimage.h"
#include "jpgdec.h"
#include "drv_common.h"

#include <utils/Log.h>
#include <stdio.h>
#include "jni.h"
#define  LOG_TAG    "pic_test"

#include <mtal.h>
#include "pictureapi.h"

#ifdef ALL_PIC_VIDEOPATH
int fg_MTScaler_init = 0;
bool fg_videopath = false;
#endif

typedef struct{
    int tex_width, tex_height;
    pthread_mutex_t mutex;
}shared_data;
MT_RESULT_T r;

unsigned char *gpbuf  = NULL;
#define Safe_Free(p) if((p)) {free((p)); (p) = NULL;}






//-----------------------jni--------------------------

static jint add(JNIEnv *env, jobject obj, jint a, jint b) {
	int result = a + b;
    LOGE("jni test add fuction %d + %d = %d", a, b, result);
    return result;
}

static jboolean nativeGet4K2KPlayerFlag(JNIEnv * env, jobject obj)
{
	MT_RESULT_T     e_return = MTR_OK;
	jboolean e4k2kPlayerFlag = false;
	//e_return = MTIMAGE_GetDisplayState(&e4k2kPlayerFlag);
	LOGE("native  nativeGet4K2KPlayerFlag:.....e4k2kPlayerFlag: %d",e4k2kPlayerFlag);
	if (e_return != MTR_OK)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "MtkPhotoHandler", "#### nativeGet4K2KPlayerFlag is Failed!!!");
	}
	return e4k2kPlayerFlag;
}

#ifdef ALL_PIC_VIDEOPATH

MTSCALER_DISPLAY_INFO_T pic_info;
//char* buf = NULL;

bool get_fgVideoPath()
{
	return fg_videopath;
}

bool do_MTScaler_Show(MTSCLAER_RGB_TYPE_T sktype, unsigned int skdataaddr, unsigned int skdatasize, MTSCALER_DISPLAY_REGION_T skDispRegion)
{
	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "#### do_MTScaler_Show ...start...!");
	if(fg_videopath)
	{
		pic_info.eType = sktype;
		pic_info.u4DataAddr = skdataaddr;
		pic_info.u4DataSize = skdatasize;
		pic_info.rDispRegion.u4x = skDispRegion.u4x;
		pic_info.rDispRegion.u4y = skDispRegion.u4y;
		pic_info.rDispRegion.u4Width = skDispRegion.u4Width;
		pic_info.rDispRegion.u4Height = skDispRegion.u4Height;

		if(pic_info.u4DataAddr == 0)
		{
			LOGE(".....native EEEEEE do_MTScaler_Show  .....pic_info.u4DataAddr 0.... ");
			return false;
		}
		/*if(buf == NULL)
		{
			if((buf = (char*)malloc(pic_info.u4DataSize))== NULL)
			{
				LOGE(".....native EEEEEE do_MTScaler_Show  .....malloc failed..... ");
				return false;
			}
			
		}

		memset(buf,0x0,pic_info.u4DataSize);
		memcpy(buf,(char*)pic_info.u4DataAddr,pic_info.u4DataSize);*/

		LOGE(".....native EEEEEE do_MTScaler_Show:..... eType= %d ,u4DataAddr = 0x%x ,u4DataSize = %d  ",pic_info.eType,pic_info.u4DataAddr,pic_info.u4DataSize);
		LOGE(".....native EEEEEE do_MTScaler_Show:..... u4x= %d ,u4y = %d ,u4Width = %d ,u4Height = %d ",pic_info.rDispRegion.u4x,pic_info.rDispRegion.u4y,pic_info.rDispRegion.u4Width,pic_info.rDispRegion.u4Height);

		/*LOGE(".....native EEEEEE do_MTScaler_Show:..... buf= 0x%x ",(unsigned int)buf);
		if((skdataaddr == 0) || ((unsigned int)buf == 0))
		{
			LOGE(".....native EEEEEE do_MTScaler_Show  ....skdataaddr == 0 || buf == 0..... ");
			return false;
		}
		pic_info.u4DataAddr = (unsigned int)buf;*/
		if(MTR_OK != MTSCALER_SetDisplayInfo(pic_info))
		{
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "#### MTSCALER_SetDisplayInfo error!");
			return false;
		}
		if(MTR_OK != MTSCALER_Show(1,TRUE))
		{
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "#### MTSCALER_Show error!");
			return false;
		}
		LOGE(".....native EEEEEE do_MTScaler_Show  .....true.... ");
		return true;
	}	

	LOGE(".....native EEEEEE do_MTScaler_Show  .....false.... ");
	return false;
}
#endif

static jint nativeExitFromPhotoPlay(JNIEnv * env, jobject obj,jboolean flag,jint index)
{
	LOGE(".....native EEEEEE nativeExitFromPhotoPlay:..... flag= %d,index = %d ",flag,index);
	MT_RESULT_T     e_return = MTR_OK;
	
//#ifdef MTM_HWDEC
	LOGE(".....native EEEEEE nativeExitFromPhotoPlay:..... getMtal_Init_Flag = %d  ",getMtal_Init_Flag());
    if (!getMtal_Init_Flag())
    {
        //if (!DRV_Init())
        //{
		//	__android_log_print(ANDROID_LOG_DEBUG,  LOG_TAG, "#### nativeExitFromPhotoPlay Driver init fail");
        //    return MTR_NOT_OK;
        //}
        MTAL_Init();
        //MTVDO_Init();
        //setMtal_Init_Flag(TRUE);
	    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "#### nativeExitFromPhotoPlay mtal_init");
    }
//#endif

	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "#### nativeExitFromPhotoPlay is called");

#ifdef ALL_PIC_VIDEOPATH	
	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "#### ALL_PIC_VIDEOPATH is ok");
#else
	//e_return = MTIMAGE_GotoOrExitFromPhotoPlay(flag,(MTIMAGE_FILE_FORMAT)index);
#endif
	
	if (e_return != MTR_OK)
	{
		__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "#### nativeExitFromPhotoPlay is Failed!!!");
		//jniThrowException(env,"com/mediatek/jni/picjni/NotSupportException","MTIMAGE_GotoOrExitFromPhotoPlay Failed!");
	}
	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "#### nativeExitFromPhotoPlay is ok");


#ifdef ALL_PIC_VIDEOPATH

	fg_videopath = flag;	
	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "#### pictureapi fg_videopath = %d ",fg_videopath);
	if(flag)
	{
		if(MTR_OK != MTSCALER_Init())
		{
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "#### MTSCALER_Init error!");
			return MTR_NOT_OK;
		}
		else
		{
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "#### MTSCALER_Init ok!");
		}
	}
	else
	{
		if(MTR_OK != MTSCALER_DeInit())
		{
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "#### MTSCALER_DeInit error!");
			return MTR_NOT_OK;
		}
		else
		{
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "#### MTSCALER_DeInit ok!");
		}
	
	}
#endif
	
	return e_return;
}

static const char *classPathName = "com/mediatek/jni/picjni";
static JNINativeMethod methods[] = {
  {"add", "(II)I", (void*)add },
  {"nativeGet4K2KPlayerFlag","()Z",(void*)nativeGet4K2KPlayerFlag},
  {"nativeExitFromPhotoPlay","(ZI)I",(void*)nativeExitFromPhotoPlay},
};

/*
 * Register several native methods for one class.
 */
static int registerNativeMethods(JNIEnv* env, const char* className,
    JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    clazz = env->FindClass(className);
    if (clazz == NULL) {
        LOGE("Native registration unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        LOGE("RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

/*
 * Register native methods for all classes we know about.
 *
 * returns JNI_TRUE on success.
 */
static int registerNatives(JNIEnv* env)
{
  if (!registerNativeMethods(env, classPathName,
                 methods, sizeof(methods) / sizeof(methods[0]))) {
    return JNI_FALSE;
  }

  return JNI_TRUE;
}

// ----------------------------------------------------------------------------

/*
 * This is called by the VM when the shared library is first loaded.
 */
 
typedef union {
    JNIEnv* env;
    void* venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv* env = NULL;
    
    LOGI("JNI_OnLoad");

    if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("ERROR: GetEnv failed");
        goto bail;
    }
    env = uenv.env;

    if (registerNatives(env) != JNI_TRUE) {
        LOGE("ERROR: registerNatives failed");
        goto bail;
    }
    
    result = JNI_VERSION_1_4;
    
bail:
    return result;
}

