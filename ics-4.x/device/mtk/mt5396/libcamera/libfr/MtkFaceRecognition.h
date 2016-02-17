#ifndef _MTKFACERECOGNITION_H_
#define _MTKFACERECOGNITION_H_

typedef enum
{   
    MTKFR_ERR_OK = 0,
    MTKFR_ERR_STATE,    
    MTKFR_ERR_PARAM,
	MTKFR_ERR_MEM,
	MTKFR_ERR_SCENE,
} MTKFR_ERR_ENUM;

typedef enum
{   
    MTKFR_SCENARIO_REGISTER = 0,
    MTKFR_SCENARIO_RECOGNITION,    
} MTKFR_SCENARIO;

//=================================
//Attribute - 2012-0515
typedef enum
{
	MTKFR_ATT_GENDER_MALE = 1,
	MTKFR_ATT_GENDER_FEMALE
}MTKFR_ATT_GENDER; //ATT0

typedef enum
{
	MTKFR_ATT_RACE_WESTERN = 1,
	MTKFR_ATT_RACE_EASTERN,
	MTKFR_ATT_RACE_BLACK
}MTKFR_ATT_RACE; //ATT1

typedef enum
{
	MTKFR_ATT_AGE_BABY = 1,
	MTKFR_ATT_AGE_KID,
	MTKFR_ATT_AGE_YOUTH,
	MTKFR_ATT_AGE_ELDER
}MTKFR_ATT_AGE; //ATT2

typedef enum
{
	MTKFR_ATT_POSE_FRONTAL = 1,
	MTKFR_ATT_POSE_LEFT,
	MTKFR_ATT_POSE_RIGHT,
}MTKFR_ATT_POSE; //ATT3

typedef enum
{
	MTKFR_ATT_SMILE_YES = 1,
	MTKFR_ATT_SMILE_NO,
}MTKFR_ATT_SMILE; //ATT4

typedef struct 
{
    unsigned int AttClass; // 1~4
    unsigned int Probability; // 50~99
}FRAttribute;
//==========================================

typedef struct 
{
	unsigned int working_buffer_addr;
	unsigned int working_buffer_size;
	MTKFR_SCENARIO scenario;
}FRInitEngineParam;

typedef struct 
{
    void* input_frame;								// [IN], the video frame buffer
    int input_frame_width;							// [IN], the video frame width (is usually 640)
    int input_frame_height;							// [IN], the video frame height (is usually 480)
    int input_fd_rec[5];							// [IN], the video frame area to detect for faces.
	float* RedFeature;								// [OUT], featur vector
	FRAttribute Attribute[5];						// [OUT], attribute
}FRGetFaceVectorParam;

typedef struct
{
	float* RedFeature1;								// [IN]
	float* RedFeature2;								// [IN]
	float score;									// [OUT]
}FRGetFaceMatchingResultParam;

/*
	Face Feature Extraction
*/
MTKFR_ERR_ENUM MtkFaceRecognition_GetFaceVector(FRGetFaceVectorParam*);

/*
	Two face comparison (Face Recognition)
*/
MTKFR_ERR_ENUM MtkFaceRecognition_GetFaceMatchingResult(FRGetFaceMatchingResultParam*);

/*
	Initialization (allocate memory, init param.)
*/
MTKFR_ERR_ENUM MtkFaceRecognition_InitFREngine(FRInitEngineParam*);

/*
	Get user info (age, color, etc...)
*/
MTKFR_ERR_ENUM MtkFaceRecognition_GetFaceAttribute(FRGetFaceVectorParam*); //2012-0515

/*
	Close FR engine (free memory)
*/
MTKFR_ERR_ENUM MtkFaceRecognition_CloseFREngine(FRInitEngineParam* init);

/*
	Query working buffer size
*/
MTKFR_ERR_ENUM MtkFaceRecognition_QueryWorkingBufferSize(FRInitEngineParam* init);
/*
	Output debug Info. of 1 Face feature
*/
MTKFR_ERR_ENUM MtkFaceRecognition_DebugInfo();

#endif