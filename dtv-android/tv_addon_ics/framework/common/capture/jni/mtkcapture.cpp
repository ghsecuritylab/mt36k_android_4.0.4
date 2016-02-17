#include <stdlib.h>
#include <stdint.h>
#include <jni.h>
#include <JNIHelp.h>

#include <android/log.h>
#include "IMtkPb_Ctrl_DTV.h"
#include "IMtkPb_ErrorCode.h"
#include <stdint.h>

#include <sys/ipc.h>
#include <sys/shm.h>

#include "core/SkBitmap.h"
#ifdef __cplusplus
extern "C" {
#endif

typedef struct _CAPTURE_ENV_T 
{
    JavaVM * vm;
    jclass capture;
    jobject thiz;
    IMTK_PB_HANDLE_T hHandle;
    
} CAPTURE_ENV_T;

int shm_id = 0;
bool b_shm = false;
char* p_map = NULL;

#define MAX_NUM_HANDLES      ((unsigned short) 4096)
#define SYS_MEM_SIZE ((unsigned int) 12 * 1024 * 1024)  
#define MMAP_BUFFER_LEN			(1920 * 1088 * 4)
    
  typedef struct _THREAD_DESCR_T
  {
      unsigned int  z_stack_size;
  
      unsigned char  ui1_priority;
  
      unsigned short  ui2_num_msgs;
  }   THREAD_DESCR_T;
  
  typedef struct _GEN_CONFIG_T
  {
      unsigned short  ui2_version;
  
      void*  pv_config;
  
      unsigned int  z_config_size;
  
      THREAD_DESCR_T  t_mheg5_thread;
  }   GEN_CONFIG_T;


extern "C" int c_rpc_init_client(void);
extern "C" int c_rpc_start_client(void);

extern "C" int os_init(const void *pv_addr, unsigned int z_size);
extern "C" int handle_init (unsigned short   ui2_num_handles,
                            void**   ppv_mem_addr,
                            unsigned int*  pz_mem_size);
extern "C" int x_rtos_init (GEN_CONFIG_T*  pt_config);

static int initialize(void)
{
    GEN_CONFIG_T  t_rtos_config;
    void*       pv_mem_addr = 0;
    unsigned int z_mem_size = 0xc00000;
    int ret = 0;

	memset(&t_rtos_config, 0, sizeof(GEN_CONFIG_T));
    ret = x_rtos_init (&t_rtos_config);
    if (ret != 0)
    {
        return ret;
    }   


    ret = handle_init (MAX_NUM_HANDLES, &pv_mem_addr, &z_mem_size);

    if (ret != 0)
    {
        return ret;
    }

    ret = os_init (pv_mem_addr, z_mem_size);

    if (ret != 0)
    {
        return ret;
    }

    ret = c_rpc_init_client();

    if (ret != 0)
    {
        return ret;
    }
    ret = c_rpc_start_client();
    return ret;
}

void CaptureCallback (IMTK_PB_HANDLE_T                  h_handle,
                                const void*             pv_tag,
                   IMTK_PB_CTRL_CAP_EVENT_TYPE_T        e_event,
                                const void*             pv_data)
{       

    CAPTURE_ENV_T*  info = (CAPTURE_ENV_T*) pv_tag;
    JNIEnv*             env;
    jclass              captureClass;
    jmethodID           BuildEventMethod; 

    jint ret = ((info->vm))->AttachCurrentThread((JNIEnv**)&env, NULL);
    captureClass = (info->capture);
    BuildEventMethod = env->GetMethodID(captureClass,"CaptureEvent","(I)V");  
    __android_log_print(ANDROID_LOG_DEBUG, "CaptureJni", "####  call CaptureEvent in MtkCaptureLogo Function");
    
    // call CaptureEvent(EVENT_UPDATE);
    __android_log_print(ANDROID_LOG_DEBUG, "CaptureJni", "####  Call MtkCaptureLogo CaptureEvent: %d", e_event);
    env->CallVoidMethod(info->thiz, BuildEventMethod, (int)e_event);
    __android_log_print(ANDROID_LOG_DEBUG, "CaptureJni", "####  Call MtkCaptureLogo end");
    
    return;
}

bool CustomCaptureCallback  (
                   IMTK_PB_CTRL_CAP_SRC_TYPE_T          e_source,
                   IMTK_PB_CTRL_CAP_EVENT_TYPE_T        e_event,
                                const void*             pv_data)
{       
    CAPTURE_ENV_T*  info = (CAPTURE_ENV_T*) pv_data;
    JNIEnv*             env;
    jclass              captureClass;
    jmethodID           BuildEventMethod; 

    jint ret = ((info->vm))->AttachCurrentThread((JNIEnv**)&env, NULL);
    captureClass = (info->capture);
    BuildEventMethod = env->GetMethodID(captureClass,"CustomCaptureEvent","(II)V");  
    __android_log_print(ANDROID_LOG_DEBUG, "CaptureJni", "####  call CustomCaptureEvent in MtkCaptureLogo Function");
    
    // call CaptureEvent(EVENT_UPDATE);
    __android_log_print(ANDROID_LOG_DEBUG, "CaptureJni", "####  Call MtkCaptureLogo CaptureEvent: %d", e_event);
    env->CallVoidMethod(info->thiz, BuildEventMethod, (int)e_source, (int)e_event);
    __android_log_print(ANDROID_LOG_DEBUG, "CaptureJni", "####  Call MtkCaptureLogo end");
    
    return true;
}
     
                              
/*
* Class:     com_mediatek_common_capture_MtkCaptureLogo
* Method:    nativeOpen
* Signature: ()I
*/
JNIEXPORT void JNICALL Java_com_mediatek_common_capture_MtkCaptureLogo_nativeInit
    (JNIEnv * env, jobject thiz)
{
	__android_log_print(ANDROID_LOG_DEBUG, "CaptureJni", "####  Call MtkCaptureLogo nativeInit ########");
	initialize();
	return;
}
	
JNIEXPORT jint JNICALL Java_com_mediatek_common_capture_MtkCaptureLogo_nativeCaptureOpen
    (JNIEnv * env, jobject thiz, int e_source)
{
	__android_log_print(ANDROID_LOG_DEBUG, "CaptureJni", "####  Call MtkCaptureLogo nativeCaptureOpen e_source = %d  ########", e_source);
	IMTK_PB_ERROR_CODE_T  e_return = IMTK_PB_ERROR_CODE_OK;
	CAPTURE_ENV_T*  envInfo = (CAPTURE_ENV_T*)malloc(sizeof(CAPTURE_ENV_T));
	jint ret = env->GetJavaVM( &envInfo->vm);
	if(ret < 0)
	{
		return 0;
	}

	jclass capture = env->FindClass("com/mediatek/common/capture/MtkCaptureLogo");
	envInfo->capture = (jclass)env->NewGlobalRef(capture); 
	envInfo->thiz = env->NewGlobalRef(thiz);

	e_return = IMtkPb_Ctrl_CapOpen((IMTK_PB_CTRL_CAP_SRC_TYPE_T)e_source,
		                            CaptureCallback,
		                            (void*)envInfo,
		                            CustomCaptureCallback,
		                            (void*)envInfo,
		                            &(envInfo->hHandle));
	            
	if(e_return != IMTK_PB_ERROR_CODE_OK)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "####  Call nativeCaptureOpen:IMtkPb_Ctrl_CapOpen fail %d", e_return);
		jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Capture Open Failed!");
		return 0;
	}

		//share memory
	if (!b_shm && e_source == IMTK_PB_CTRL_CAP_SRC_TYPE_MM_IMAGE_ANDROID)
	{
		//key_t shmkey = ftok("$HOME", 1);
		shm_id = shmget(IPC_PRIVATE, MMAP_BUFFER_LEN, IPC_CREAT | 0666);
		if (shm_id < 0)
		{
			perror("shmget error");
			jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Capture Failed when shmget!");
			return 0;
		}
		b_shm = true;			
	}

	return (jint)envInfo;		
}

JNIEXPORT jint JNICALL Java_com_mediatek_common_capture_MtkCaptureLogo_nativeCaptureSave
    (JNIEnv * env, jobject thiz, int handle, jobject info)
{
	__android_log_print(ANDROID_LOG_DEBUG, "CaptureJni", "####  Call MtkCaptureLogo nativeCaptureSave ########");
	CAPTURE_ENV_T*    capture_env = (CAPTURE_ENV_T *)handle;
	IMTK_PB_CTRL_CAP_LOGO_SAVE_INFO_T t_save_info;
	IMTK_PB_ERROR_CODE_T  e_return = IMTK_PB_ERROR_CODE_INV_HANDLE;
	jclass Saveinfo = env->FindClass("com/mediatek/common/capture/MtkCaptureLogoSaveInfo");
	jfieldID   field;
	field = env->GetFieldID(Saveinfo, "e_device_type", "I");
	t_save_info.e_device_type = IMTK_PB_CTRL_CAP_DEVICE_TYPE_INTERNAL;

	
	field = env->GetFieldID(Saveinfo, "ui4_logo_id", "I");
	t_save_info.u.ui4_logo_id = (jint)env->GetIntField(info, field);

	if(capture_env->hHandle) 
	{
		e_return = IMtkPb_Ctrl_CapSave(capture_env->hHandle, &t_save_info);
	}
	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
		jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Capture Save Failed!");
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "####  Call nativeCaptureSave:IMtkPb_Ctrl_CapSave ok");
	}
	return e_return;
}


JNIEXPORT jint JNICALL Java_com_mediatek_common_capture_MtkCaptureLogo_nativeCaptureSaveExternal
    (JNIEnv * env, jobject thiz, int handle, jstring jstr)
{
	__android_log_print(ANDROID_LOG_DEBUG, "CaptureJni", "####  Call MtkCaptureLogo nativeCaptureSaveExternal ########");
	CAPTURE_ENV_T*    capture_env = (CAPTURE_ENV_T *)handle;
	IMTK_PB_CTRL_CAP_LOGO_SAVE_INFO_T t_save_info;
	IMTK_PB_ERROR_CODE_T  e_return = IMTK_PB_ERROR_CODE_INV_HANDLE;
	
	t_save_info.e_device_type = IMTK_PB_CTRL_CAP_DEVICE_TYPE_EXTERNAL;

	t_save_info.u.ps_path = (char*)env->GetStringUTFChars(jstr, NULL);
	
	if(capture_env->hHandle) 
	{
		e_return = IMtkPb_Ctrl_CapSave(capture_env->hHandle, &t_save_info);
	}
	env->ReleaseStringUTFChars(jstr, t_save_info.u.ps_path);
	
	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
		jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Capture Save Failed!");
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "####  Call nativeCaptureSaveExternal:IMtkPb_Ctrl_CapSave ok");
	}
	return e_return;
}


JNIEXPORT jobject JNICALL Java_com_mediatek_common_capture_MtkCaptureLogo_nativeCaptureGetData
    (JNIEnv * env, jobject thiz, jint handle)
{
    CAPTURE_ENV_T*    envInfo = (CAPTURE_ENV_T *)handle;
    IMTK_PB_ERROR_CODE_T  e_return = IMTK_PB_ERROR_CODE_INV_HANDLE;
    uint32_t                  length = 0;
    uint8_t*              data = 0;
    jbyteArray            array;
	__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "####  Call nativeCaptureGetData");
	
    jclass CaptureDataInfoClass = env->FindClass("com/mediatek/common/capture/MtkCaptureDataInfo");
    jmethodID CaptureDataInfoClassMethod = env->GetMethodID(CaptureDataInfoClass, "<init>", "(I[B)V");
    
    														 
	if (envInfo->hHandle)
	{
		e_return = IMtkPb_Ctrl_CapGetData(envInfo->hHandle,
		                &data,
		                &length);
		                

#ifdef DEBUG_LOG
		__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "####  Call nativeCaptureGetData:IMtkPb_Ctrl_CapGetData e_return %d",e_return);
#endif
	}
	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
		jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Capture GetData Failed!");
	}
	if (data == 0 || length == 0)
	{
		return 0;
	}
	array = env->NewByteArray(length); 
	env->SetByteArrayRegion(array, 0, (jint)length, (const jbyte*)data);

	jobject obj = env->NewObject(CaptureDataInfoClass, 
								 CaptureDataInfoClassMethod, 
								 length,
								 array);
								 
	return obj;
}

JNIEXPORT jobject JNICALL Java_com_mediatek_common_capture_MtkCaptureLogo_nativeCaptureQueryCapability
(JNIEnv * env, jobject thiz)
{
	__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "####  Call nativeCaptureQueryCapability#####");
	IMTK_PB_ERROR_CODE_T  e_return = IMTK_PB_ERROR_CODE_INV_HANDLE;
	IMTK_PB_CTRL_CAP_CAPABILITY_INFO_T Capability;
	e_return = IMtkPb_Ctrl_CapQueryCapability(&Capability);

	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
		jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Query Capability Failed!");
	}

	jclass CapabilityClass = env->FindClass("com/mediatek/common/capture/MtkCaptureCapability");
	jmethodID capabilitymethod = env->GetMethodID(CapabilityClass, "<init>", "(ZZBBS)V");
	jobject obj = env->NewObject(CapabilityClass, 
								 capabilitymethod, 
								 Capability.b_default,
								 Capability.b_default_exist,
								 Capability.ui1_cur_logo_index,
								 Capability.ui1_nums_logo_slots,
								 Capability.ui2_logo_valid_tag);

	return obj;
}

JNIEXPORT jint JNICALL Java_com_mediatek_common_capture_MtkCaptureLogo_nativeCaptureClose
    (JNIEnv * env, jobject thiz, jint handle)
{ 
	CAPTURE_ENV_T*    info = (CAPTURE_ENV_T *)handle;
	__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "####  Call nativeCaptureClose #####");
	if(info->hHandle) 
	{
		IMTK_PB_ERROR_CODE_T  e_return = IMtkPb_Ctrl_CapClose(info->hHandle);
		if (e_return != IMTK_PB_ERROR_CODE_OK)
		{
			jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Capture Close Failed!");
		}
		info->hHandle = 0;
		env->DeleteGlobalRef(info->thiz);
		env->DeleteGlobalRef(info->capture);
		free(info); 
	}

	if (b_shm)
	{
		if (shmctl(shm_id, IPC_RMID, NULL) == -1)
		{
			
			jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Capture Close Failed because shmctl fail!");
		}
		b_shm = false;
	}
#ifdef DEBUG_LOG
	__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "####  Call nativeOpen:IMtkPb_Ctrl_CapOpen ok");
#endif
	return 0;
}

JNIEXPORT jint JNICALL Java_com_mediatek_common_capture_MtkCaptureLogo_nativeCapture
    (JNIEnv * env, jobject thiz, jint handle, jobject capture_info)
{ 
   	__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "####  Call nativeCapture  #####");
	
	CAPTURE_ENV_T*    		info = (CAPTURE_ENV_T *)handle;
	IMTK_PB_ERROR_CODE_T  	e_return = IMTK_PB_ERROR_CODE_INV_HANDLE;
	IMTK_PB_CTRL_CAP_CAPTURE_INFO_T  t_cap_info;
	int bottom, top, left, right;

	jclass 		CaptureInfoClass = env->FindClass("com/mediatek/common/capture/MtkCaptureInfo");
	jclass 		rectClass = env->FindClass("android/graphics/Rect");
	jfieldID   	field;
	jobject    	jrect;
	int			Bitmap, shared_fd, colormode, byte_written, data_len;
	char*		puc_rawdata = NULL;
	char shared_file_name[64] = "/tmp/capPhotoRawData.bin";
	SkBitmap*	skBmp;

	memset(&t_cap_info, 0, sizeof(IMTK_PB_CTRL_CAP_CAPTURE_INFO_T));

	field = env->GetFieldID(CaptureInfoClass, "format", "I");
	t_cap_info.e_format = (IMTK_PB_CTRL_CAP_IMG_FMT_TYPE_T)env->GetIntField(capture_info, field);

	field = env->GetFieldID(CaptureInfoClass, "e_res_type", "I");
	t_cap_info.e_res_type = (IMTK_PB_CTRL_CAP_OUT_RES_TYPE_T)env->GetIntField(capture_info, field);

	field = env->GetFieldID(CaptureInfoClass, "quality", "I");
	t_cap_info.ui4_quality = (uint32_t)env->GetIntField(capture_info, field);

	field = env->GetFieldID(CaptureInfoClass, "max_size", "I");
	t_cap_info.ui4_max_size = (uint32_t)env->GetIntField(capture_info, field);

	field = env->GetFieldID(CaptureInfoClass, "ui4_moveable_width", "I");
	t_cap_info.ui4_moveable_width = (uint32_t)env->GetIntField(capture_info, field);

	field = env->GetFieldID(CaptureInfoClass, "ui4_moveable_height", "I");
	t_cap_info.ui4_moveable_height = (uint32_t)env->GetIntField(capture_info, field);

	field = env->GetFieldID(CaptureInfoClass, "ui1_video_path", "B");
	t_cap_info.ui1_video_path = (uint8_t)env->GetByteField(capture_info, field);

	field = env->GetFieldID(CaptureInfoClass, "rect", "Landroid/graphics/Rect;");
	jrect = (jobject)env->GetObjectField(capture_info, field);

	field = env->GetFieldID(rectClass, "bottom", "I");
	bottom = env->GetIntField(jrect, field);

	field = env->GetFieldID(rectClass, "top", "I");
	top = env->GetIntField(jrect, field);

	field = env->GetFieldID(rectClass, "left", "I");
	left = env->GetIntField(jrect, field);

	field = env->GetFieldID(rectClass, "right", "I");
	right = env->GetIntField(jrect, field);

	t_cap_info.t_rec.ui4_left = (uint32_t)left;
	t_cap_info.t_rec.ui4_top = (uint32_t)top;
	t_cap_info.t_rec.ui4_right = (uint32_t)right;
	t_cap_info.t_rec.ui4_bottom = (uint32_t)bottom;

	field = env->GetFieldID(CaptureInfoClass, "skBitmap", "I");
	Bitmap = env->GetIntField(capture_info, field);
	if (Bitmap != 0)
	{
		skBmp = (SkBitmap*)Bitmap;
		__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "Bitmap is not NULL");
		
		t_cap_info.ui4_image_width = (uint32_t)(skBmp->width());
		t_cap_info.ui4_image_height = (uint32_t)(skBmp->height());
		t_cap_info.ui4_image_pitch = (uint32_t)(skBmp->rowBytes());
		colormode = (int)skBmp->config();
		if (colormode == SkBitmap::kRGB_565_Config)
		{
			t_cap_info.e_colormode = IMTK_PB_CTRL_CAP_COLOR_FORMAT_RGB_D565;
		}
		else if (colormode == SkBitmap::kARGB_4444_Config)
		{
			t_cap_info.e_colormode = IMTK_PB_CTRL_CAP_COLOR_FORMAT_ARGB_D4444;
		}
		else if (colormode == SkBitmap::kARGB_8888_Config)
		{
			t_cap_info.e_colormode = IMTK_PB_CTRL_CAP_COLOR_FORMAT_ARGB_D8888;
		}
		else 
		{
			jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Capture Failed because this colormode is not support!");
			return IMTK_PB_ERROR_CODE_NOT_IMPL;
		}

		__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "Bitmap colormode is %d, image_width is %d, image_height is %d, image_pitch is %d", 
						colormode, t_cap_info.ui4_image_width, t_cap_info.ui4_image_height, t_cap_info.ui4_image_pitch);

		{
			int ret;
			
			skBmp->lockPixels();
			puc_rawdata = (char*)(skBmp->getAddr(0,0));
			if (puc_rawdata == NULL)
			{
				jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Capture Failed because get skBitmap addr failed!");
				return IMTK_PB_ERROR_CODE_NOT_OK;
			}

			__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "Bitmap buf is 0x%x", puc_rawdata);
		
			data_len = t_cap_info.ui4_image_height * t_cap_info.ui4_image_pitch;

			__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "Bitmap datalen is %d", data_len);
			
			ret = (int)shmat(shm_id, NULL, 0);
			if (ret == -1)
			{
				perror("shmat error");
				jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Capture Failed when shmat!");
				return IMTK_PB_ERROR_CODE_NOT_OK;
			}
			p_map = (char*)ret;

			memset(p_map, 0, MMAP_BUFFER_LEN);
			memcpy(p_map, puc_rawdata, data_len);

			__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "!!!!!!!!!!!! Map addr is 0x%x", p_map);
			
			skBmp->unlockPixels();
			
			if (shmdt(p_map) < 0)
			{
				perror("shmdt error");
				jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Capture Failed when shmdt!");
				return IMTK_PB_ERROR_CODE_NOT_OK;
			}

			__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "do shmdt ok");		
			
			t_cap_info.i4_shm_id = shm_id;
		}
	}
	
	__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "Start to call IMtkPb_Ctrl_CapCapture 1");
	if(info->hHandle) 
	{
		__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "Start to call IMtkPb_Ctrl_CapCapture 2");
		e_return = IMtkPb_Ctrl_CapCapture(info->hHandle, &t_cap_info);
	}
	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
		jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Capture Failed!");
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "####  Call nativeCapture:IMtkPb_Ctrl_CapCapture ok");
	}
	return e_return;
}

 JNIEXPORT jint JNICALL Java_com_mediatek_common_capture_MtkCaptureLogo_nativeCaptureSyncStopCapture
    (JNIEnv * env, jobject thiz, jint handle)
{ 
	IMTK_PB_ERROR_CODE_T  e_return = IMTK_PB_ERROR_CODE_INV_HANDLE;
	CAPTURE_ENV_T*    info = (CAPTURE_ENV_T *)handle;

	if(info->hHandle) 
	{
		e_return = IMtkPb_Ctrl_CapSyncStopCap(info->hHandle);
		__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "####  Call nativeCaptureSyncStopCapture:IMtkPb_Ctrl_CapSyncStopCap fail %d",e_return);
	}
	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
		jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Sync Stop Capture Failed!");
	}
	return (jint)e_return;
}

 JNIEXPORT jint JNICALL Java_com_mediatek_common_capture_MtkCaptureLogo_nativeCaptureSyncStopSave
    (JNIEnv * env, jobject thiz, jint handle)
{ 
	IMTK_PB_ERROR_CODE_T  e_return = IMTK_PB_ERROR_CODE_INV_HANDLE;
	CAPTURE_ENV_T*    info = (CAPTURE_ENV_T *)handle;

	if(info->hHandle) 
	{
		e_return = IMtkPb_Ctrl_CapSyncStopSave(info->hHandle);
		__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "####  Call nativeCaptureSyncStopSave:IMtkPb_Ctrl_CapSyncStopSave fail %d",e_return);
	}
	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
		jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Sync Save Capture Failed!");
	}
	return (jint)e_return;
}


JNIEXPORT jint JNICALL Java_com_mediatek_common_capture_MtkCaptureLogo_nativeCaptureSelectAsBootLogo
    (JNIEnv * env, jobject thiz,jint device, jint index)
{ 
	IMTK_PB_CTRL_CAP_LOGO_SELECT_INFO_T t_save_info;

	if (device < 0 || device >=3)
	{
		return IMTK_PB_ERROR_CODE_INV_ARG;
	}
	t_save_info.e_device_type = (IMTK_PB_CTRL_CAP_DEVICE_TYPE_T) device;
	t_save_info.u.ui4_logo_id = index;

	IMTK_PB_ERROR_CODE_T  e_return = IMtkPb_Ctrl_CapSelectAsBootLogo(&t_save_info);

	__android_log_print(ANDROID_LOG_DEBUG, "CaptureLogo", "####  Call nativeCaptureSelectAsBootLogo:IMtkPb_Ctrl_CapSelectAsBootLogo fail %d", e_return);
	if (e_return != IMTK_PB_ERROR_CODE_OK)
	{
		jniThrowException(env,"com/mediatek/common/capture/NotSupportException","Capture Select As Boot Logo Failed!");
	}
	return e_return;
}

#ifdef __cplusplus

}
#endif
