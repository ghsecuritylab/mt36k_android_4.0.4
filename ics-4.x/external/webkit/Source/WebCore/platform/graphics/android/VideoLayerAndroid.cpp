/*
 * Copyright 2011 The Android Open Source Project
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include "config.h"
#include "VideoLayerAndroid.h"

#ifdef MTK_WEBKIT_FIX
#include "WebCoreJni.h"
#include <GraphicsJNI.h>
#include <JNIHelp.h>
#include <JNIUtility.h>
#include <cutils/properties.h>
#endif // MTK_WEBKIT_FIX

#include "RenderSkinMediaButton.h"
#include "TilesManager.h"
#include <GLES2/gl2.h>
#include <gui/SurfaceTexture.h>

#if USE(ACCELERATED_COMPOSITING)

#ifdef DEBUG
#include <cutils/log.h>
#include <wtf/text/CString.h>

#undef XLOG
#define XLOG(...) android_printLog(ANDROID_LOG_DEBUG, "VideoLayerAndroid", __VA_ARGS__)

#else

#undef XLOG
#define XLOG(...)

#endif // DEBUG

namespace WebCore {

GLuint VideoLayerAndroid::m_spinnerOuterTextureId = 0;
GLuint VideoLayerAndroid::m_spinnerInnerTextureId = 0;
GLuint VideoLayerAndroid::m_posterTextureId = 0;
GLuint VideoLayerAndroid::m_backgroundTextureId = 0;
bool VideoLayerAndroid::m_createdTexture = false;

double VideoLayerAndroid::m_rotateDegree = 0;

#ifdef MTK_WEBKIT_FIX
static const char* g_ProxyJavaClass = "android/webkit/HTML5VideoViewProxy";
bool VideoLayerAndroid::m_useCmpbPlayer = false;
#endif

const IntRect VideoLayerAndroid::buttonRect(0, 0, IMAGESIZE, IMAGESIZE);

VideoLayerAndroid::VideoLayerAndroid()
    : LayerAndroid((RenderLayer*)0)
{
    init();
}

VideoLayerAndroid::VideoLayerAndroid(const VideoLayerAndroid& layer)
    : LayerAndroid(layer)
{
    init();
}

void VideoLayerAndroid::init()
{
    // m_surfaceTexture is only useful on UI thread, no need to copy.
    // And it will be set at setBaseLayer timeframe
    m_playerState = INITIALIZED;

#ifdef MTK_WEBKIT_FIX
    JNIEnv* env = JSC::Bindings::getJNIEnv();
    if (!env)
        return;

    jclass clazz = env->FindClass(g_ProxyJavaClass);

    if (!clazz)
        return;

    m_setVideoRect = env->GetStaticMethodID(clazz, "setVideoRect", "(IIII)V");

    env->DeleteLocalRef(clazz);
    // An exception is raised if any of the above fails.
    checkException(env);

    char useCmpbPlayer[32];
    memset(useCmpbPlayer, 0, sizeof(useCmpbPlayer));

    property_get("mtk.browser.useCmpbPlayer", useCmpbPlayer, NULL);

    m_useCmpbPlayer = (0 == strcmp(useCmpbPlayer, "1")) ? true : false;
#endif
}

// We can use this function to set the Layer to point to surface texture.
void VideoLayerAndroid::setSurfaceTexture(sp<SurfaceTexture> texture,
                                          int textureName, PlayerState playerState)
{
    m_surfaceTexture = texture;
    m_playerState = playerState;
    TilesManager::instance()->videoLayerManager()->registerTexture(uniqueId(), textureName);
}

GLuint VideoLayerAndroid::createSpinnerInnerTexture()
{
    return createTextureFromImage(RenderSkinMediaButton::SPINNER_INNER);
}

GLuint VideoLayerAndroid::createSpinnerOuterTexture()
{
    return createTextureFromImage(RenderSkinMediaButton::SPINNER_OUTER);
}

GLuint VideoLayerAndroid::createPosterTexture()
{
    return createTextureFromImage(RenderSkinMediaButton::VIDEO);
}

GLuint VideoLayerAndroid::createTextureFromImage(int buttonType)
{
    SkRect rect = SkRect(buttonRect);
    SkBitmap bitmap;
    bitmap.setConfig(SkBitmap::kARGB_8888_Config, rect.width(), rect.height());
    bitmap.allocPixels();
    bitmap.eraseColor(0);

    SkCanvas canvas(bitmap);
    canvas.drawARGB(0, 0, 0, 0, SkXfermode::kClear_Mode);
    RenderSkinMediaButton::Draw(&canvas, buttonRect, buttonType, true);

    GLuint texture;
    glGenTextures(1, &texture);

    GLUtils::createTextureWithBitmap(texture, bitmap);
    bitmap.reset();
    return texture;
}

GLuint VideoLayerAndroid::createBackgroundTexture()
{
    GLuint texture;
    glGenTextures(1, &texture);
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
    GLubyte pixels[4 *3] = {
        128, 128, 128,
        128, 128, 128,
        128, 128, 128,
        128, 128, 128
    };
    glBindTexture(GL_TEXTURE_2D, texture);
    GLUtils::checkGlError("glBindTexture");
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 2, 2, 0, GL_RGB, GL_UNSIGNED_BYTE, pixels);
    GLUtils::checkGlError("glTexImage2D");
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    return texture;
}

bool VideoLayerAndroid::drawGL()
{
    // Lazily allocated the textures.
    if (!m_createdTexture) {
        m_backgroundTextureId = createBackgroundTexture();
        m_spinnerOuterTextureId = createSpinnerOuterTexture();
        m_spinnerInnerTextureId = createSpinnerInnerTexture();
        m_posterTextureId = createPosterTexture();
        m_createdTexture = true;
    }

    SkRect rect = SkRect::MakeSize(getSize());
    GLfloat surfaceMatrix[16];

    SkRect innerRect = SkRect(buttonRect);
    if (innerRect.contains(rect))
        innerRect = rect;

    innerRect.offset((rect.width() - IMAGESIZE) / 2 , (rect.height() - IMAGESIZE) / 2);

    // Draw the poster image, the progressing image or the Video depending
    // on the player's state.
    if (m_playerState == PREPARING) {
        // Show the progressing animation, with two rotating circles
        TilesManager::instance()->shader()->drawLayerQuad(m_drawTransform, rect,
                                                          m_backgroundTextureId,
                                                          1, true);

        TransformationMatrix addReverseRotation;
        TransformationMatrix addRotation = m_drawTransform;
        addRotation.translate(innerRect.fLeft, innerRect.fTop);
        addRotation.translate(IMAGESIZE / 2, IMAGESIZE / 2);
        addReverseRotation = addRotation;
        addRotation.rotate(m_rotateDegree);
        addRotation.translate(-IMAGESIZE / 2, -IMAGESIZE / 2);

        SkRect size = SkRect::MakeWH(innerRect.width(), innerRect.height());
        TilesManager::instance()->shader()->drawLayerQuad(addRotation, size,
                                                          m_spinnerOuterTextureId,
                                                          1, true);

        addReverseRotation.rotate(-m_rotateDegree);
        addReverseRotation.translate(-IMAGESIZE / 2, -IMAGESIZE / 2);

        TilesManager::instance()->shader()->drawLayerQuad(addReverseRotation, size,
                                                          m_spinnerInnerTextureId,
                                                          1, true);

        m_rotateDegree += ROTATESTEP;

    } else if (m_playerState == PLAYING && m_surfaceTexture.get()) {
        // Show the real video.
        m_surfaceTexture->updateTexImage();
        m_surfaceTexture->getTransformMatrix(surfaceMatrix);
        GLuint textureId =
            TilesManager::instance()->videoLayerManager()->getTextureId(uniqueId());
        TilesManager::instance()->shader()->drawVideoLayerQuad(m_drawTransform,
                                                               surfaceMatrix,
                                                               rect, textureId);
        TilesManager::instance()->videoLayerManager()->updateMatrix(uniqueId(),
                                                                    surfaceMatrix);
    } else {
        GLuint textureId =
            TilesManager::instance()->videoLayerManager()->getTextureId(uniqueId());
        GLfloat* matrix =
            TilesManager::instance()->videoLayerManager()->getMatrix(uniqueId());
        if (textureId && matrix) {
            // Show the screen shot for each video.
            TilesManager::instance()->shader()->drawVideoLayerQuad(m_drawTransform,
                                                               matrix,
                                                               rect, textureId);
        } else {
            // Show the static poster b/c there is no screen shot available.
            TilesManager::instance()->shader()->drawLayerQuad(m_drawTransform, rect,
                                                              m_backgroundTextureId,
                                                              1, true);
            TilesManager::instance()->shader()->drawLayerQuad(m_drawTransform, innerRect,
                                                              m_posterTextureId,
                                                              1, true);
        }
    }
#ifdef MTK_WEBKIT_FIX
    if ((m_surfaceTexture != NULL) && (m_surfaceTexture.get()) && m_useCmpbPlayer)
    {
        SkRect videoRect = SkRect::MakeXYWH(getPosition().x(), getPosition().y(), getWidth(), getHeight());

        // We need to know the position of its parent since the position of the
        // origin of the layer, relative to the parent layer
        Layer* parent = getParent();
        int idx = 0;

        while (parent)
        {
            videoRect.offset(parent->getPosition());
            parent = parent->getParent();
        }

        FloatRect mapVidRect;
        SkRect newVideoRect;

        mapVidRect = TilesManager::instance()->shader()->videoRectInScreenCoord(videoRect);

        newVideoRect.fLeft = mapVidRect.x();
        newVideoRect.fTop = mapVidRect.y();
        newVideoRect.fRight = mapVidRect.maxX();
        newVideoRect.fBottom = mapVidRect.maxY();

        if( newVideoRect != m_videoRect )
        {
            m_videoRect.set(newVideoRect.fLeft, newVideoRect.fTop, newVideoRect.fRight, newVideoRect.fBottom);

            JNIEnv* env = JSC::Bindings::getJNIEnv();
            if (env)
            {
                jclass clazz = env->FindClass(g_ProxyJavaClass);

                if (clazz)
                {
                    env->CallStaticVoidMethod(clazz, m_setVideoRect, static_cast<jint>(m_videoRect.fLeft),
                                                                     static_cast<jint>(m_videoRect.fTop),
                                                                     static_cast<jint>(m_videoRect.fRight),
                                                                     static_cast<jint>(m_videoRect.fBottom));
                    env->DeleteLocalRef(clazz);
                    checkException(env);
                }
            }
        }
    }
#endif // MTK_WEBKIT_FIX

    return drawChildrenGL();
}

}
#endif // USE(ACCELERATED_COMPOSITING)
