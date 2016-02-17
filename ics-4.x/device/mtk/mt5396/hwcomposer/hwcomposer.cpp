/*
 * Copyright (C) 2010 The Android Open Source Project
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

#include <stdlib.h>
#include <string.h>

#include <hardware/hardware.h>
//#include <hardware/overlay.h>

#include <fcntl.h>
#include <errno.h>

#include <cutils/log.h>
#include <cutils/atomic.h>

#include <hardware/hwcomposer.h>

#include <EGL/egl.h>
#include "gralloc_priv.h"

#include <linux/ioctl.h>
#include "mtgfx.h"

/*****************************************************************************/

struct hwc_context_t {
    hwc_composer_device_t device;
    int     mMTGFX_FD;
    int 		mFBWidth;
    int 		mFBHeigh;
    /* our private state goes below here */
};

static int hwc_device_open(const struct hw_module_t* module, const char* name,
        struct hw_device_t** device);

static struct hw_module_methods_t hwc_module_methods = {
    open: hwc_device_open
};

hwc_module_t HAL_MODULE_INFO_SYM = {
    common: {
        tag: HARDWARE_MODULE_TAG,
        version_major: 1,
        version_minor: 0,
        id: HWC_HARDWARE_MODULE_ID,
        name: "Sample hwcomposer module",
        author: "The Android Open Source Project",
        methods: &hwc_module_methods,
    }
};

/*****************************************************************************/

static void dump_layer(hwc_layer_t const* l) {
    LOGD("\ttype=%d, flags=%08x, handle=%p, tr=%02x, blend=%04x, {%d,%d,%d,%d}, {%d,%d,%d,%d}",
            l->compositionType, l->flags, l->handle, l->transform, l->blending,
            l->sourceCrop.left,
            l->sourceCrop.top,
            l->sourceCrop.right,
            l->sourceCrop.bottom,
            l->displayFrame.left,
            l->displayFrame.top,
            l->displayFrame.right,
            l->displayFrame.bottom);
}

// Check whether a layer is a hole puncher used to show video in an underlying plane
static bool hwc_check_hole(hwc_context_t* ctx, hwc_layer_list_t* list,
                           size_t index) {
    hwc_layer_t& layer = list->hwLayers[index];

    // If the layer is showing a buffer with the VIDEO_HOLE format, then
    // exclude the layer from SurfaceFlinger's framebuffer and add it
    // to the transparent region.
    if (layer.handle && private_handle_t::validate(layer.handle) == 0) {
       const private_handle_t* pHandle = reinterpret_cast<const private_handle_t*>(layer.handle);
        if (pHandle->format == HAL_PIXEL_FORMAT_GTV_VIDEO_HOLE || pHandle->format == HAL_PIXEL_FORMAT_GTV_CMPB_VIDEO_HOLE) {
    //    		LOGI("[hwcomposer] check is HAL_PIXEL_FORMAT_GTV_VIDEO_HOLE");
            layer.compositionType = HWC_OVERLAY;
            layer.hints |= HWC_HINT_CLEAR_FB;
            return true;
        }
    }
    return false;
}

static int reset_mtgfx()
{
		LOGI("[hwcomposer] reset_mtgfx\n");
    mMTGFX_FD = open("/dev/mtal", O_RDWR, 0);
    if (mMTGFX_FD < 0) {
        LOGE("Error opening MTGFX device errno=%d (%s)",
             errno, strerror(errno));
        return -1;
    } //else {
        // RESET do only once after open.
      //  if (ioctl(mMTGFX_FD, MTAL_IO_VDO_RESET, (int) 0) != 0) {
       //     LOGE("Error reset MTGFX device errno=%d (%s)",
      //           errno, strerror(errno));
     //       return -1;
     //   }
   // }
    return 0;
}

static int hwc_prepare(hwc_composer_device_t* dev, hwc_layer_list_t* list) {
    if (!list)
        return 0;

    struct hwc_context_t* ctx = reinterpret_cast<struct hwc_context_t*>(dev);

    for (size_t i = 0; i < list->numHwLayers; i++) {
        hwc_layer_t& layer = list->hwLayers[i];

        if (!hwc_check_hole(ctx, list, i)) {
            // this is an ordinary layer composited via GLES
            layer.compositionType = HWC_FRAMEBUFFER;
            layer.hints = 0;
        }
    }
    return 0;
}
static int setFrameBuffer(struct hwc_composer_device* dev, 
															int width, 
															int heigh)
{
		struct hwc_context_t* ctx = reinterpret_cast<struct hwc_context_t*>(dev);

		LOGI("[hwcomposer] hwc_set width: %d, heigh: %d\n", width, heigh);
		
		ctx->mFBWidth = width;
		ctx->mFBHeigh = heigh;
		
		return 0;
}
static int hwc_set(hwc_composer_device_t *dev,
        hwc_display_t dpy,
        hwc_surface_t sur,
        hwc_layer_list_t* list)
{
    //for (size_t i=0 ; i<list->numHwLayers ; i++) {
    //    dump_layer(&list->hwLayers[i]);
    //}
		struct hwc_context_t* ctx = reinterpret_cast<struct hwc_context_t*>(dev);
    for (size_t i = 0; i < list->numHwLayers; i++) {
        hwc_layer_t& layer = list->hwLayers[i];
        
				if (layer.handle && private_handle_t::validate(layer.handle) == 0) 
				{
       		const private_handle_t* pHandle = reinterpret_cast<const private_handle_t*>(layer.handle);
       		if(pHandle->format == HAL_PIXEL_FORMAT_GTV_CMPB_VIDEO_HOLE)
       		{			   					
       				MTVDO_REGION_T OutRegion;
				//	LOGI("[hwcomposer] display region x: %d, y: %d, l: %d, r: %d\n", layer.displayFrame.left, layer.displayFrame.top, layer.displayFrame.right, layer.displayFrame.bottom);
       				OutRegion.u4X = (layer.displayFrame.left)*1000/(ctx->mFBWidth);
       				OutRegion.u4Y = (layer.displayFrame.top)*1000/(ctx->mFBHeigh);
							uint32_t width = (layer.displayFrame.right - layer.displayFrame.left)*1000/(ctx->mFBWidth);
        			OutRegion.u4Width = (OutRegion.u4X+width)<1000?width:(1000-OutRegion.u4X);
							uint32_t height = (layer.displayFrame.bottom - layer.displayFrame.top)*1000/(ctx->mFBHeigh);
       				OutRegion.u4Height = (OutRegion.u4Y+height)<1000?height:(1000-OutRegion.u4Y);
							OutRegion.u4X *= 10;
							OutRegion.u4Y *= 10;
							OutRegion.u4Width *= 10;
							OutRegion.u4Height *= 10;
       				MTGFX_SetOutPutRegion(&OutRegion);
//       				LOGI("[hwcomposer] set display region x: %d, y: %d, w: %d, h: %d\n", OutRegion.u4X, OutRegion.u4Y, OutRegion.u4Width, OutRegion.u4Height);     				
       		}
     		}
    }
    EGLBoolean sucess = eglSwapBuffers((EGLDisplay)dpy, (EGLSurface)sur);
    if (!sucess) {
        return HWC_EGL_ERROR;
    }
    return 0;
}

static int hwc_device_close(struct hw_device_t *dev)
{
    struct hwc_context_t* ctx = (struct hwc_context_t*)dev;
    if (ctx) {
    		if(ctx->mMTGFX_FD)
    		{
    			close(ctx->mMTGFX_FD);
				}
        free(ctx);
    }
    return 0;
}

/*****************************************************************************/

static int hwc_device_open(const struct hw_module_t* module, const char* name,
        struct hw_device_t** device)
{
    int status = -EINVAL;
    if (!strcmp(name, HWC_HARDWARE_COMPOSER)) {
        struct hwc_context_t *dev;
        dev = (hwc_context_t*)malloc(sizeof(*dev));

        /* initialize our state here */
        memset(dev, 0, sizeof(*dev));

        /* initialize the procs */
        dev->device.common.tag = HARDWARE_DEVICE_TAG;
        dev->device.common.version = 0;
        dev->device.common.module = const_cast<hw_module_t*>(module);
        dev->device.common.close = hwc_device_close;

        dev->device.prepare = hwc_prepare;
        dev->device.set = hwc_set;
        dev->device.setFrameBuffer = setFrameBuffer;
        
        int ret = reset_mtgfx();
		    if (ret == 0) {
		        dev->mMTGFX_FD = mMTGFX_FD;
		        *device = &dev->device.common;
		        status = 0;
		    } else {
		        LOGE("hwcomposer open FAIL");
		        hwc_device_close(&dev->device.common);
		    }     
    }
    return status;
}
