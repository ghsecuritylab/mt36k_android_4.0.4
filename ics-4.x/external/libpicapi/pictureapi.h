#ifdef __cplusplus
extern "C"
{
#endif

#ifndef EXTERN
    #ifdef __cplusplus
        #define EXTERN          extern "C"
    #else
        #define EXTERN          extern
    #endif
#endif  // EXTERN

//#define ALL_PIC_VIDEOPATH

#ifdef ALL_PIC_VIDEOPATH
#include "mtscaler.h"


EXTERN bool get_fgVideoPath();
EXTERN bool do_MTScaler_Show(MTSCLAER_RGB_TYPE_T sktype, unsigned int skdataaddr, unsigned int skdatasize, MTSCALER_DISPLAY_REGION_T skDispRegion);
#endif

#ifdef __cplusplus
}
#endif 
