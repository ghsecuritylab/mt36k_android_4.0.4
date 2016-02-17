/*
 * Copyright (C) 2006, 2007 Apple Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY APPLE COMPUTER, INC. ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL APPLE COMPUTER, INC. OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include "config.h"

#include "Page.h"
#include "PluginMainThreadScheduler.h"
#include "PluginView.h"
#include "npruntime_internal.h"

using namespace WebCore;

// The plugin view is always the ndata of the instance,. Sometimes, plug-ins will call an instance-specific function
// with a NULL instance. To workaround this, call the last plug-in view that made a call to a plug-in.
// Currently, the current plug-in view is only set before NPP_New in PluginView::start.
// This specifically works around Flash and Shockwave. When we call NPP_New, they call NPN_Useragent with a NULL instance.
static PluginView* pluginViewForInstance(NPP instance)
{
    if (instance && instance->ndata)
        return static_cast<PluginView*>(instance->ndata);
    return PluginView::currentPluginView();
}

void* NPN_MemAlloc(uint32_t size)
{
    return malloc(size);
}

void NPN_MemFree(void* ptr)
{
    free(ptr);
}

uint32_t NPN_MemFlush(uint32_t size)
{
    // Do nothing
    return 0;
}

void NPN_ReloadPlugins(NPBool reloadPages)
{
    Page::refreshPlugins(reloadPages);
}

NPError NPN_RequestRead(NPStream* stream, NPByteRange* rangeList)
{
    return NPERR_STREAM_NOT_SEEKABLE;
}

NPError NPN_GetURLNotify(NPP instance, const char* url, const char* target, void* notifyData)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        return pluginViewForInstance(instance)->getURLNotify(url, target, notifyData);
    }
    else
        return NPERR_INVALID_INSTANCE_ERROR;
#else
    return pluginViewForInstance(instance)->getURLNotify(url, target, notifyData);
#endif // MTK_WEBKIT_FIX
}

NPError NPN_GetURL(NPP instance, const char* url, const char* target)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        return pluginViewForInstance(instance)->getURL(url, target);
    }
    else
        return NPERR_INVALID_INSTANCE_ERROR;
#else
    return pluginViewForInstance(instance)->getURL(url, target);
#endif // MTK_WEBKIT_FIX
}

NPError NPN_PostURLNotify(NPP instance, const char* url, const char* target, uint32_t len, const char* buf, NPBool file, void* notifyData)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        return pluginViewForInstance(instance)->postURLNotify(url, target, len, buf, file, notifyData);
    }
    else
        return NPERR_INVALID_INSTANCE_ERROR;
#else
    return pluginViewForInstance(instance)->postURLNotify(url, target, len, buf, file, notifyData);
#endif // MTK_WEBKIT_FIX
}

NPError NPN_PostURL(NPP instance, const char* url, const char* target, uint32_t len, const char* buf, NPBool file)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        return pluginViewForInstance(instance)->postURL(url, target, len, buf, file);
    }
    else
        return NPERR_INVALID_INSTANCE_ERROR;
#else
    return pluginViewForInstance(instance)->postURL(url, target, len, buf, file);
#endif
}

NPError NPN_NewStream(NPP instance, NPMIMEType type, const char* target, NPStream** stream)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        return pluginViewForInstance(instance)->newStream(type, target, stream);
    }
    else
        return NPERR_INVALID_INSTANCE_ERROR;
#else
    return pluginViewForInstance(instance)->newStream(type, target, stream);
#endif // MTK_WEBKIT_FIX
}

int32_t NPN_Write(NPP instance, NPStream* stream, int32_t len, void* buffer)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        return pluginViewForInstance(instance)->write(stream, len, buffer);
    }
    else
        return 0;
#else
    return pluginViewForInstance(instance)->write(stream, len, buffer);
#endif // MTK_WEBKIT_FIX
}

NPError NPN_DestroyStream(NPP instance, NPStream* stream, NPReason reason)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        return pluginViewForInstance(instance)->destroyStream(stream, reason);
    }
    else
        return NPERR_INVALID_INSTANCE_ERROR;
#else
    return pluginViewForInstance(instance)->destroyStream(stream, reason);
#endif // MTK_WEBKIT_FIX
}

const char* NPN_UserAgent(NPP instance)
{
    PluginView* view = pluginViewForInstance(instance);

     if (!view)
         return PluginView::userAgentStatic();

    return view->userAgent();
}

void NPN_Status(NPP instance, const char* message)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        pluginViewForInstance(instance)->status(message);
    }
#else
    pluginViewForInstance(instance)->status(message);
#endif // MTK_WEBKIT_FIX
}

void NPN_InvalidateRect(NPP instance, NPRect* invalidRect)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        pluginViewForInstance(instance)->invalidateRect(invalidRect);
    }
#else
    pluginViewForInstance(instance)->invalidateRect(invalidRect);
#endif // MTK_WEBKIT_FIX
}

void NPN_InvalidateRegion(NPP instance, NPRegion invalidRegion)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        pluginViewForInstance(instance)->invalidateRegion(invalidRegion);
    }
#else
    pluginViewForInstance(instance)->invalidateRegion(invalidRegion);
#endif // MTK_WEBKIT_FIX
}

void NPN_ForceRedraw(NPP instance)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        pluginViewForInstance(instance)->forceRedraw();
    }
#else
    pluginViewForInstance(instance)->forceRedraw();
#endif // MTK_WEBKIT_FIX
}

NPError NPN_GetValue(NPP instance, NPNVariable variable, void* value)
{
    PluginView* view = pluginViewForInstance(instance);

     if (!view)
         return PluginView::getValueStatic(variable, value);

    return pluginViewForInstance(instance)->getValue(variable, value);
}

NPError NPN_SetValue(NPP instance, NPPVariable variable, void* value)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        return pluginViewForInstance(instance)->setValue(variable, value);
    }
    else
        return NPERR_INVALID_INSTANCE_ERROR;
#else
    return pluginViewForInstance(instance)->setValue(variable, value);
#endif // MTK_WEBKIT_FIX
}

void* NPN_GetJavaEnv()
{
    // Unsupported
    return 0;
}

void* NPN_GetJavaPeer(NPP instance)
{
    // Unsupported
    return 0;
}

void NPN_PushPopupsEnabledState(NPP instance, NPBool enabled)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        pluginViewForInstance(instance)->pushPopupsEnabledState(enabled);
    }
#else
    pluginViewForInstance(instance)->pushPopupsEnabledState(enabled);
#endif // MTK_WEBKIT_FIX
}

void NPN_PopPopupsEnabledState(NPP instance)
{
    pluginViewForInstance(instance)->popPopupsEnabledState();
}

extern "C" typedef void PluginThreadAsyncCallFunction(void*);
void NPN_PluginThreadAsyncCall(NPP instance, PluginThreadAsyncCallFunction func, void* userData)
{
    // Callback function type only differs from MainThreadFunction by being extern "C", which doesn't affect calling convention on any compilers we use.
    PluginMainThreadScheduler::scheduler().scheduleCall(instance, reinterpret_cast<PluginMainThreadScheduler::MainThreadFunction*>(func), userData);
}

NPError NPN_GetValueForURL(NPP instance, NPNURLVariable variable, const char* url, char** value, uint32_t* len)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        return pluginViewForInstance(instance)->getValueForURL(variable, url, value, len);
    }
    else
        return NPERR_INVALID_INSTANCE_ERROR;
#else
    return pluginViewForInstance(instance)->getValueForURL(variable, url, value, len);
#endif // MTK_WEBKIT_FIX
}

NPError NPN_SetValueForURL(NPP instance, NPNURLVariable variable, const char* url, const char* value, uint32_t len)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        return pluginViewForInstance(instance)->setValueForURL(variable, url, value, len);
    }
    else
        return NPERR_INVALID_INSTANCE_ERROR;
#else
    return pluginViewForInstance(instance)->setValueForURL(variable, url, value, len);
#endif // MTK_WEBKIT_FIX
}

NPError NPN_GetAuthenticationInfo(NPP instance, const char* protocol, const char* host, int32_t port, const char* scheme, const char* realm, char** username, uint32_t* ulen, char** password, uint32_t* plen)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        return pluginViewForInstance(instance)->getAuthenticationInfo(protocol, host, port, scheme, realm, username, ulen, password, plen);
    }
    else
        return NPERR_INVALID_INSTANCE_ERROR;
#else
    return pluginViewForInstance(instance)->getAuthenticationInfo(protocol, host, port, scheme, realm, username, ulen, password, plen);
#endif // MTK_WEBKIT_FIX
}

#ifdef PLUGIN_SCHEDULE_TIMER
uint32_t NPN_ScheduleTimer(NPP instance, uint32_t interval, NPBool repeat,
                         void (*timerFunc)(NPP npp, uint32_t timerID))
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        return pluginViewForInstance(instance)->scheduleTimer(instance, interval,
                                                           repeat != 0, timerFunc);
    }
    else
        return 0;
#else
    return pluginViewForInstance(instance)->scheduleTimer(instance, interval,
#endif // MTK_WEBKIT_FIX
}

void NPN_UnscheduleTimer(NPP instance, uint32_t timerID)
{
#ifdef MTK_WEBKIT_FIX
    if (pluginViewForInstance(instance)) {
        pluginViewForInstance(instance)->unscheduleTimer(instance, timerID);
    }
#else
    pluginViewForInstance(instance)->unscheduleTimer(instance, timerID);
#endif // MTK_WEBKIT_FIX
}
#endif
