
#ifndef _MTK_FACE_DETECTION_H
#define _MTK_FACE_DETECTION_H

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

static bool tmp;

#define MAX_DETECTION_FACES 5
#define MAX_FACE_VECTOR_SIZE 1024

typedef enum
{   
    MTKFD_ERR_OK = 0,                               // no error
    MTKFD_ERR_STATE,                                // calling API order is wrong
    MTKFD_ERR_PARAM,                                // input parameter is unreasonable
    MTKFD_ERR_ENUM_NUM                        
} MTKFD_ERR_ENUM;

struct FDInitEngineParam
{
    int maxium_face_to_detect;						// [IN], default: 5, 5 means the algo should try to detect at most 5 faces. (shold be not bigger than MAX_DETECTION_FACES)
    int face_rotation_degree;						// [IN], default: 0, 0 means 0 degree only, 1 means -30~+30 degree, 2 means 0, 90 -90, 3 means -30~30, 60~120, -60~-120 degree,
    int input_min_face_idx;                         // [IN], default: 0, min face size index, from 0~10
    int input_max_face_idx;                         // [IN], default: 11, max face size index, from 1~11 
    int frame_division;                             // [IN], default: 1, scan jump pixels, from 1~10
    bool save_debug;                                // save debug info   
    unsigned int working_buffer_size;
};


struct FDGetFacePositionParam
{
    void* input_frame;								// [IN], the video frame buffer
    int input_frame_width;							// [IN], the video frame width (is usually 640)
    int input_frame_height;							// [IN], the video frame height (is usually 480)
    int input_frame_detection_area_x;				// [IN], the video frame area to detect for faces.
    int input_frame_detection_area_y;				// [IN], the video frame area to detect for faces.
    int input_frame_detection_area_width;			// [IN], the video frame area to detect for faces.
    int input_frame_detection_area_height;			// [IN], the video frame area to detect for faces.
    unsigned int working_buffer_addr;
    unsigned int working_buffer_size;
    int face_num;                                   // [OUT], number of faces detected
    int detection_result[MAX_DETECTION_FACES][4];   // [OUT], if 2 faces detected: 
                                                    // detection_result[0][0] : Face1_X
                                                    // detection_result[0][1] : Face1_Y
                                                    // detection_result[0][2] : Face1_WIDTH
                                                    // detection_result[0][3] : Face1_HEIGHT
    int face_degree[MAX_DETECTION_FACES];           // [OUT], face degree for detection, 0 means 0, 1 means 30, 2 means 60, 3 means 90...etc
    
                         
    //for Debug
    int face_feature_set_index[MAX_DETECTION_FACES];
    
};


MTKFD_ERR_ENUM MtkFaceDetection_InitFDEngine(FDInitEngineParam* param);

MTKFD_ERR_ENUM MtkFaceDetection_GetFacePosition(FDGetFacePositionParam* param);

MTKFD_ERR_ENUM MtkFaceDetection_CloseFDEngine(void);

#endif

