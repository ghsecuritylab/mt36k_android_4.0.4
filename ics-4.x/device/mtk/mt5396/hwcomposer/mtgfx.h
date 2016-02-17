/*
 * Copyright (C) 2010 0xlab - http://0xlab.org/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#ifndef __H_MTGFX_H__
#define __H_MTGFX_H__

typedef struct
{
    int ai4Arg[2];
} MTAL_IOCTL_2ARG_T;

typedef struct
{
		uint32_t u4X;
		uint32_t u4Y;
		uint32_t u4Width;
		uint32_t u4Height;
}MTVDO_REGION_T;

#define MTAL_IOCTYPE_MTVDO            17
#define MTAL_IO_VDO_RESET                       _IOW(MTAL_IOCTYPE_MTVDO, 2, uint32_t)
#define MTAL_IO_VDO_SET_OUTREGION               _IOW(MTAL_IOCTYPE_MTVDO, 15, MTAL_IOCTL_2ARG_T)

static int mMTGFX_FD;

int MTGFX_reset()
{
    return ioctl(mMTGFX_FD, MTAL_IO_VDO_RESET, (uint32_t)0);
}

int MTGFX_SetOutPutRegion(MTVDO_REGION_T *prOutRegion)
{
    MTAL_IOCTL_2ARG_T rArg;
    rArg.ai4Arg[0] = (int)0;  //cmpb use channel 0
    rArg.ai4Arg[1] = (int)prOutRegion;
    return ioctl(mMTGFX_FD, MTAL_IO_VDO_SET_OUTREGION, &rArg);
}

#endif
