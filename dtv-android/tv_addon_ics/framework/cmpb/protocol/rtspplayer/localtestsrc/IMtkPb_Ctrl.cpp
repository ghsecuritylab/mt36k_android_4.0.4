#include "IMtkPb_Ctrl.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
static FILE *file_audio;
static FILE *file_video;

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetDisplayRectangle(IMTK_PB_HANDLE_T        hHandle,          
                                                            IMTK_PB_CTRL_RECT_T*    ptSrcRect,
                                                            IMTK_PB_CTRL_RECT_T*    ptDstRect)
{
	return IMTK_PB_ERROR_CODE_OK;
}
                                                            
                                                            
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetEngineParam(IMTK_PB_HANDLE_T             hHandle,
                                                       IMTK_PB_CTRL_ENGINE_PARAM_T*   ptParam)
{
	return IMTK_PB_ERROR_CODE_OK;
}                                                      
                                                       
IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetMediaInfo(IMTK_PB_HANDLE_T                   hHandle,
                                                     IMTK_PB_CTRL_GET_MEDIA_INFO_T*     ptMediaInfo)
{
	return IMTK_PB_ERROR_CODE_OK;
}     

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_RegCallback(IMTK_PB_HANDLE_T        hHandle,
                                                    void*                   pvAppCbTag,
                                                    IMtkPb_Ctrl_Nfy_Fct     pfnCallback)
{
	return IMTK_PB_ERROR_CODE_OK;
}

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Play(IMTK_PB_HANDLE_T   hHandle,
                                             uint32_t           u4Time)
{
	return IMTK_PB_ERROR_CODE_OK;
}

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Pause(IMTK_PB_HANDLE_T  hHandle)
{
	return IMTK_PB_ERROR_CODE_OK;
}

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Stop(IMTK_PB_HANDLE_T   hHandle)
{
	return IMTK_PB_ERROR_CODE_OK;
}

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Close(IMTK_PB_HANDLE_T  hHandle)
{
	return IMTK_PB_ERROR_CODE_OK;
}

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_ByteSeek(IMTK_PB_HANDLE_T   hHandle, 
                                                 uint64_t           u8Pos)
{
	return IMTK_PB_ERROR_CODE_OK;
}

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_TimeSeek(IMTK_PB_HANDLE_T   hHandle, 
                                                 uint32_t           u4Time)
{
	return IMTK_PB_ERROR_CODE_OK;
}

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetCurrentPos(IMTK_PB_HANDLE_T  hHandle,
                                                      uint32_t*         pu4CurTime,   
                                                      uint64_t*         pu8CurPos)
{
	return IMTK_PB_ERROR_CODE_OK;
}

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Open(IMTK_PB_HANDLE_T*              phHandle,
                                               IMTK_PB_CTRL_BUFFERING_MODEL_T eBufferingModel,
                                               IMTK_PB_CTRL_OPERATING_MODEL_T eOperatingModel,
                                               uint8_t*                       pu1Profile)
{
	*phHandle = 1;
	file_audio= fopen("./audio_cmpb_data", "w+");
	fclose(file_audio);
	file_video= fopen("./video_cmpb_data", "w+");
	fclose(file_video);
	return IMTK_PB_ERROR_CODE_OK;
}           

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_GetBuffer(IMTK_PB_HANDLE_T      hHandle, 
											uint32_t              u4BufSize,
												uint8_t**             ppu1PushBuf)
{
	static int iCount = 0; 
	*ppu1PushBuf = (unsigned char*)malloc(u4BufSize);
	if (NULL == *ppu1PushBuf)
	{
		if (iCount>2)
		{
			iCount = 0;
			return IMTK_PB_ERROR_CODE_NOT_OK;
		}
		else
		{
			iCount++;
			return IMTK_PB_ERROR_CODE_GET_BUF_PENDING;
		}
	}
	iCount = 0;
	return IMTK_PB_ERROR_CODE_OK;
}

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SendData(IMTK_PB_HANDLE_T		hHandle,
											uint32_t 			 u4BufSize,
												uint8_t*			   pu1PushBuf)
{
	if (pu1PushBuf)
	{
		free(pu1PushBuf);
		pu1PushBuf = NULL;
	}
	return IMTK_PB_ERROR_CODE_OK;
}

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_Local_SendData(IMTK_PB_HANDLE_T		hHandle,
											uint32_t 			 u4BufSize,
												uint8_t*			   pu1PushBuf,
													int type)
{
	FILE * file;
	if (type == 1)
	{
		file_audio = fopen("./audio_cmpb_data", "a+");
		file = file_audio;
	}
	else
	{
		file_video= fopen("./video_cmpb_data", "a+");
		file = file_video;
	}

	if (NULL == file)
	{
		return IMTK_PB_ERROR_CODE_NOT_OK;
	}

	unsigned int iLen = 0;
	char *ptr = (char *)pu1PushBuf;
	if (ptr == NULL)
	{
		return IMTK_PB_ERROR_CODE_NOT_OK;
	}
	while(u4BufSize > 0)
	{
		int iRet = fwrite(ptr + iLen, sizeof(unsigned char), u4BufSize, file);
		if (iRet<0)
		{
			return IMTK_PB_ERROR_CODE_NOT_OK;
		}
		iLen += iRet;
		u4BufSize -= iRet;
	}
	fclose(file);
	return IMTK_PB_ERROR_CODE_OK;
}

IMTK_PB_ERROR_CODE_T IMtkPb_Ctrl_SetMediaInfo(IMTK_PB_HANDLE_T hHandle, 
											IMTK_PB_CTRL_SET_MEDIA_INFO_T* ptMediaInfo)
{
	return IMTK_PB_ERROR_CODE_OK;
}


