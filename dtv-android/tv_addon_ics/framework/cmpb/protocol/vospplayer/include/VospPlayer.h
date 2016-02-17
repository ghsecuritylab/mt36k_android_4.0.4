#ifndef _VOSP_PLAYER_H_
#define _VOSP_PLAYER_H_

#include "btype.h"
/*

#ifdef __cplusplus
extern "C" {
#endif

#include "c2ms.h"

#ifdef __cplusplus
}
#endif
*/
#include <string>
#include "IMtkPb_Ctrl.h"

typedef IMTK_PB_CB_ERROR_CODE_T (*event_nfy_fct)(IMTK_PB_CTRL_EVENT_T, void*, unsigned int);

namespace vosp
{
typedef enum
{
    VOSPPLAYERSTATUS_PLAYED = 0,
    VOSPPLAYERSTATUS_PAUSED,
    VOSPPLAYERSTATUS_STOPPED
} VospPlayerStatus;

typedef struct pull_task
{
    IMTK_PB_HANDLE_T pHandle;
    IMTK_PULL_HANDLE_T pPullHandle;
    uint64 msize;
} PullTask;

typedef struct
{
    void* mgmt;
    void* sess;        // session pointer
    uint32 idxsize;  // idx size after unziped
    uint64 msize;	 // file size
    uint64 offset;   // file offset
} VospContext;

class VospPlayer
{
public:
    VospPlayer();
    bool  initialize(const std::string& uri, event_nfy_fct callback, void* pvTag);
    bool play();
    bool pause();
    bool resume();
    bool stop();
    bool timeseek(int time);
    bool GetDuration( int* msec );
    bool getCurrentPosition(int* msec);
    ~VospPlayer();
    static IMTK_PB_CB_ERROR_CODE_T pull_open(IMTK_PB_HANDLE_T hHandle,
                                             IMTK_PULL_HANDLE_T* phPullSrc, void* pvAppTag);
    static IMTK_PB_CB_ERROR_CODE_T pull_close(IMTK_PULL_HANDLE_T hPullSrc, void* pvAppTag);
    static IMTK_PB_CB_ERROR_CODE_T pull_read(IMTK_PULL_HANDLE_T	 hPullSrc,
                                             void* 				 pvAppTag,
                                             uint8_t*				 pu1DstBuf,
                                             uint32_t				 u4Count,
                                             uint32_t* 			 pu4Read);
    static IMTK_PB_CB_ERROR_CODE_T pull_read_async(IMTK_PULL_HANDLE_T         hPullSrc,
                                                   void*                      pvAppTag,
                                                   uint8_t*                   pu1Dst,
                                                   uint32_t                   u4DataLen,
                                                   IMtkPb_Ctrl_Pull_Nfy_Fct   pfnNotify,
                                                   void*                      pvRdAsyTag,
                                                   uint32_t*                  pu4ReqId);
    static IMTK_PB_CB_ERROR_CODE_T pull_abort_read_async(IMTK_PULL_HANDLE_T 	hPullSrc,
                                                         void* 				 pvAppTag,
                                                         uint32_t				 u4ReqId);
    static IMTK_PB_CB_ERROR_CODE_T pull_byteseek(IMTK_PULL_HANDLE_T 		hPullSrc,
                                                 void* 					 pvAppTag,
                                                 int64_t					 i8SeekPos,
                                                 uint8_t					 u1Whence,
                                                 uint64_t*					pu8CurPos);
    static IMTK_PB_CB_ERROR_CODE_T pull_get_input_len(IMTK_PULL_HANDLE_T  hPullSrc,
                                                      void*			   pvAppTag,
                                                      uint64_t*		   pu8Len);
    static int pull_check_task_index(int hPullSrc);

private:
    VospPlayerStatus status;
    static std::string vospUrl;
    IMTK_PB_HANDLE_T  pbHandle;
    static VospContext* s ;
    static bool firstCallPullOpen;
    //static int task_num ;
    //static PullTask * pull_task[8];
  
};

}
#endif /* _VOSP_PLAYER_H_ */

