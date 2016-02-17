/*
 * Copyright (C) 2009 The Android Open Source Project
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

package android.webkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
// -- MTK_WEBKIT_FIX START --
import android.graphics.Rect;
import android.os.SystemProperties;
// -- MTK_WEBKIT_FIX END --
import android.media.MediaPlayer;
import android.net.http.EventHandler;
import android.net.http.Headers;
import android.net.http.RequestHandle;
import android.net.http.RequestQueue;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Proxy for HTML5 video views.
 */
class HTML5VideoViewProxy extends Handler
                          implements MediaPlayer.OnPreparedListener,
                          MediaPlayer.OnCompletionListener,
                          MediaPlayer.OnErrorListener,
                          MediaPlayer.OnInfoListener,
                          SurfaceTexture.OnFrameAvailableListener {
    // Logging tag.
    private static final String LOGTAG = "HTML5VideoViewProxy";

    // Message Ids for WebCore thread -> UI thread communication.
    private static final int PLAY                = 100;
    private static final int SEEK                = 101;
    private static final int PAUSE               = 102;
    private static final int ERROR               = 103;
    private static final int LOAD_DEFAULT_POSTER = 104;
    private static final int BUFFERING_START     = 105;
    private static final int BUFFERING_END       = 106;

    // Message Ids to be handled on the WebCore thread
    private static final int PREPARED          = 200;
    private static final int ENDED             = 201;
    private static final int POSTER_FETCHED    = 202;
    private static final int PAUSED            = 203;
    private static final int STOPFULLSCREEN    = 204;

    // -- MTK_WEBKIT_FIX START --
    private static final int PREPARETOPLAY     = 290;
    // -- MTK_WEBKIT_FIX END --

    // Timer thread -> UI thread
    private static final int TIMEUPDATE = 300;

    // The C++ MediaPlayerPrivateAndroid object.
    int mNativePointer;
    // The handler for WebCore thread messages;
    private Handler mWebCoreHandler;
    // The WebView instance that created this view.
    private WebView mWebView;
    // The poster image to be shown when the video is not playing.
    // This ref prevents the bitmap from being GC'ed.
    private Bitmap mPoster;
    // The poster downloader.
    private PosterDownloader mPosterDownloader;
    // The seek position.
    private int mSeekPosition;
    // A helper class to control the playback. This executes on the UI thread!
    private static final class VideoPlayer {
        // The proxy that is currently playing (if any).
        private static HTML5VideoViewProxy mCurrentProxy;
        // The VideoView instance. This is a singleton for now, at least until
        // http://b/issue?id=1973663 is fixed.
        private static HTML5VideoView mHTML5VideoView;

        private static boolean isVideoSelfEnded = false;
        // By using the baseLayer and the current video Layer ID, we can
        // identify the exact layer on the UI thread to use the SurfaceTexture.
        private static int mBaseLayer = 0;

        private static void setPlayerBuffering(boolean playerBuffering) {
            Log.e(LOGTAG, "Enter VideoPlayer.setPlayerBuffering()");

            mHTML5VideoView.setPlayerBuffering(playerBuffering);
        }

        // Every time webView setBaseLayer, this will be called.
        // When we found the Video layer, then we set the Surface Texture to it.
        // Otherwise, we may want to delete the Surface Texture to save memory.
        public static void setBaseLayer(int layer) {
            // Don't do this for full screen mode.
            if (mHTML5VideoView != null
                && !mHTML5VideoView.isFullScreenMode()
                && !mHTML5VideoView.surfaceTextureDeleted()) {
                mBaseLayer = layer;

                int currentVideoLayerId = mHTML5VideoView.getVideoLayerId();
                SurfaceTexture surfTexture = mHTML5VideoView.getSurfaceTexture(currentVideoLayerId);
                int textureName = mHTML5VideoView.getTextureName();

                if (layer != 0 && surfTexture != null && currentVideoLayerId != -1) {
                    int playerState = mHTML5VideoView.getCurrentState();
                    if (mHTML5VideoView.getPlayerBuffering())
                        playerState = HTML5VideoView.STATE_NOTPREPARED;
                    boolean foundInTree = nativeSendSurfaceTexture(surfTexture,
                            layer, currentVideoLayerId, textureName,
                            playerState);
                    if (playerState >= HTML5VideoView.STATE_PREPARED
                            && !foundInTree) {
                        Log.e(LOGTAG, "To call mHTML5VideoView.pauseAndDispatch() in VideoPlayer.setBaseLayer()");

                        mHTML5VideoView.pauseAndDispatch(mCurrentProxy);
                        mHTML5VideoView.deleteSurfaceTexture();
                    }
                }
            }
        }

        // When a WebView is paused, we also want to pause the video in it.
        public static void pauseAndDispatch() {
            Log.e(LOGTAG, "Enter VideoPlayer.pauseAndDispatch()");

            if (mHTML5VideoView != null) {
                Log.e(LOGTAG, "To call mHTML5VideoView.pauseAndDispatch() in VideoPlayer.pauseAndDispatch()");
                mHTML5VideoView.pauseAndDispatch(mCurrentProxy);
                // When switching out, clean the video content on the old page
                // by telling the layer not readyToUseSurfTex.
                setBaseLayer(mBaseLayer);
            }
        }

        public static void enterFullScreenVideo(int layerId, String url,
                HTML5VideoViewProxy proxy, WebView webView) {

                // -- MTK_WEBKIT_FIX START --

                // We would use CMPB player to playback HTML5 video,
                // and this mode would use libcurl to download the video data.
                // So we need to append the user agent string to the URL so that libcurl can
                // download the video data correctly
                int useCmpbPlayer = 0;

                if (0 != SystemProperties.get("mtk.browser.useCmpbPlayer").length()){
                    useCmpbPlayer = Integer.parseInt(SystemProperties.get("mtk.browser.useCmpbPlayer"));
                }

                if (useCmpbPlayer == 1 && webView != null && url.startsWith("http"))
                {
                    url += "?mtkUAString=";
                    url += webView.getSettings().getUserAgentString();
                }

                Log.e(LOGTAG, "Enter VideoPlayer.enterFullScreenVideo(), URL = " + url);

                WebChromeClient client = webView.getWebChromeClient();

                if (client != null)
                    client.onStartVideoStreaming(webView);

                // -- MTK_WEBKIT_FIX END --

                // Save the inline video info and inherit it in the full screen
                int savePosition = 0;
                boolean savedIsPlaying = true; //edit by xxh, default false.  no matter autoplay.
                if (mHTML5VideoView != null) {
                    // If we are playing the same video, then it is better to
                    // save the current position.
                    if (layerId == mHTML5VideoView.getVideoLayerId()) {
                        savePosition = mHTML5VideoView.getCurrentPosition();
                        savedIsPlaying = mHTML5VideoView.isPlaying();
                    }
                    Log.e(LOGTAG, "To call mHTML5VideoView.pauseAndDispatch() in VideoPlayer.enterFullScreenVideo()");
                    mHTML5VideoView.pauseAndDispatch(mCurrentProxy);
                    mHTML5VideoView.release();
                }
                mHTML5VideoView = new HTML5VideoFullScreen(proxy.getContext(),
                        layerId, savePosition, savedIsPlaying);
                mCurrentProxy = proxy;

                mHTML5VideoView.setVideoURI(url, mCurrentProxy);

                mHTML5VideoView.enterFullScreenVideoState(layerId, proxy, webView);
        }

        // This is on the UI thread.
        // When native tell Java to play, we need to check whether or not it is
        // still the same video by using videoLayerId and treat it differently.
        public static void play(String url, int time, HTML5VideoViewProxy proxy,
                WebChromeClient client, int videoLayerId) {

        	if(mHTML5VideoView == null || mHTML5VideoView.getCurrentState() < 2) {
        		return;
        	}
            if (url == null) {
                Log.e(LOGTAG, "URL is null!!!", new Exception());
                return;
            }
            // -- MTK_WEBKIT_FIX START --

            // We would use CMPB player to playback HTML5 video,
            // and this mode would use libcurl to download the video data.
            // So we need to append the user agent string to the URL so that libcurl can
            // download the video data correctly
            int useCmpbPlayer = 0;

            if (0 != SystemProperties.get("mtk.browser.useCmpbPlayer").length()){
                useCmpbPlayer = Integer.parseInt(SystemProperties.get("mtk.browser.useCmpbPlayer"));
            }

            if (useCmpbPlayer == 1 && proxy != null && url.startsWith("http"))
            {
                url += "?mtkUAString=";
                url += proxy.getWebView().getSettings().getUserAgentString();
            }

            Log.e(LOGTAG, "Enter VideoPlayer.play(), URL = " + url + mHTML5VideoView);

            if (client != null)
                client.onStartVideoStreaming(proxy.getWebView());

            // -- MTK_WEBKIT_FIX END --

            int currentVideoLayerId = -1;
            boolean backFromFullScreenMode = false;
            if (mHTML5VideoView != null) {
                currentVideoLayerId = mHTML5VideoView.getVideoLayerId();
                backFromFullScreenMode = mHTML5VideoView.fullScreenExited();
            }

            int alwaysFullscreen = 1;

            if (0 != SystemProperties.get("mtk.browser.alwaysFullscreen").length()){
                alwaysFullscreen = Integer.parseInt(SystemProperties.get("mtk.browser.alwaysFullscreen"));
            }

            if ((0 == alwaysFullscreen)
                && (backFromFullScreenMode
                    || currentVideoLayerId != videoLayerId
                    || mHTML5VideoView.surfaceTextureDeleted())) {
                // Here, we handle the case when switching to a new video,
                // either inside a WebView or across WebViews
                // For switching videos within a WebView or across the WebView,
                // we need to pause the old one and re-create a new media player
                // inside the HTML5VideoView.
                if (mHTML5VideoView != null) {
                    if (!backFromFullScreenMode) {
                        Log.e(LOGTAG, "To call mHTML5VideoView.pauseAndDispatch() in VideoPlayer.play()");
                        mHTML5VideoView.pauseAndDispatch(mCurrentProxy);
                    }
                    // release the media player to avoid finalize error
                    mHTML5VideoView.release();
                }
                mCurrentProxy = proxy;
                mHTML5VideoView = new HTML5VideoInline(videoLayerId, time, false);

                mHTML5VideoView.setVideoURI(url, mCurrentProxy);
                mHTML5VideoView.prepareDataAndDisplayMode(proxy);
            } else if (mCurrentProxy == proxy) {
                // Here, we handle the case when we keep playing with one video
                if (!mHTML5VideoView.isPlaying()) {
                    mHTML5VideoView.seekTo(time);
                    mHTML5VideoView.start();
                }
            } else if (mCurrentProxy != null) {
                // Some other video is already playing. Notify the caller that
                // its playback ended.
                proxy.dispatchOnEnded();
            }
        }

        // -- MTK_WEBKIT_FIX START --
        // This is on the UI thread.
        // When native tell Java to play, we need to check whether or not it is
        // still the same video by using videoLayerId and treat it differently.
        public static void setVideoRect(Rect video_rect) {
            if ( (mHTML5VideoView != null) &&
            	   (mHTML5VideoView.isFullScreenMode() == false) ) {
            	   mHTML5VideoView.setVideoRect(video_rect);
            }
        }
        // -- MTK_WEBKIT_FIX END --

        public static boolean isPlaying(HTML5VideoViewProxy proxy) {
            return (mCurrentProxy == proxy && mHTML5VideoView != null
                    && mHTML5VideoView.isPlaying());
        }

        public static int getCurrentPosition() {
            int currentPosMs = 0;
            if (mHTML5VideoView != null) {
                currentPosMs = mHTML5VideoView.getCurrentPosition();
            }
            return currentPosMs;
        }

        public static void seek(int time, HTML5VideoViewProxy proxy) {
            Log.e(LOGTAG, "Enter VideoPlayer.seek()");

            if (mCurrentProxy == proxy && time >= 0 && mHTML5VideoView != null) {
                mHTML5VideoView.seekTo(time);
            }
        }

        public static void pause(HTML5VideoViewProxy proxy) {
            Log.e(LOGTAG, "Enter VideoPlayer.pause()");

            if (mCurrentProxy == proxy && mHTML5VideoView != null) {
                mHTML5VideoView.pause();
            }
        }

        public static void onPrepared() {
            Log.e(LOGTAG, "Enter VideoPlayer.onPrepared()");

            if (!mHTML5VideoView.isFullScreenMode() || mHTML5VideoView.getAutostart()) {
                mHTML5VideoView.start();
            }
            if (mBaseLayer != 0) {
                setBaseLayer(mBaseLayer);
            }
        }

        public static void end() {
            Log.e(LOGTAG, "Enter VideoPlayer.end(), isVideoSelfEnded = " + isVideoSelfEnded);
            if (mCurrentProxy != null) {
                if (isVideoSelfEnded)
                    mCurrentProxy.dispatchOnEnded();
                else
                    mCurrentProxy.dispatchOnPaused();
            }
            isVideoSelfEnded = false;
        }
    }

    // A bunch event listeners for our VideoView
    // MediaPlayer.OnPreparedListener
    public void onPrepared(MediaPlayer mp) {
        Log.e(LOGTAG, "Got onPrepared() callback from MediaPlayer, proxy = " + this);

        VideoPlayer.onPrepared();
        Message msg = Message.obtain(mWebCoreHandler, PREPARED);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("dur", new Integer(mp.getDuration()));
        map.put("width", new Integer(mp.getVideoWidth()));
        map.put("height", new Integer(mp.getVideoHeight()));
        msg.obj = map;
        mWebCoreHandler.sendMessage(msg);
        Message message = obtainMessage(PLAY);
        message.arg1 = videoLayerID;
        message.obj = url;
        sendMessage(message);
    }

    // MediaPlayer.OnCompletionListener;
    public void onCompletion(MediaPlayer mp) {
        Log.e(LOGTAG, "Got onCompletion() callback from MediaPlayer, proxy = " + this);

        // The video ended by itself, so we need to
        // send a message to the UI thread to dismiss
        // the video view and to return to the WebView.
        // arg1 == 1 means the video ends by itself.
        sendMessage(obtainMessage(ENDED, 1, 0));
    }

    // MediaPlayer.OnErrorListener
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(LOGTAG, "Got onError() callback from MediaPlayer, proxy = " + this);

        sendMessage(obtainMessage(ERROR));
        return false;
    }

    public void dispatchOnEnded() {
        Message msg = Message.obtain(mWebCoreHandler, ENDED);
        mWebCoreHandler.sendMessage(msg);
    }

    public void dispatchOnPaused() {
        Message msg = Message.obtain(mWebCoreHandler, PAUSED);
        mWebCoreHandler.sendMessage(msg);
    }

    public void dispatchOnStopFullScreen() {
        Message msg = Message.obtain(mWebCoreHandler, STOPFULLSCREEN);
        mWebCoreHandler.sendMessage(msg);

        // -- MTK_WEBKIT_FIX START --
        VideoPlayer.end();
        // -- MTK_WEBKIT_FIX END --
    }

    public void onTimeupdate() {
        sendMessage(obtainMessage(TIMEUPDATE));
    }

    // When there is a frame ready from surface texture, we should tell WebView
    // to refresh.
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        // TODO: This should support partial invalidation too.
        mWebView.invalidate();
    }

    // Handler for the messages from WebCore or Timer thread to the UI thread.
    @Override
    public void handleMessage(Message msg) {
        // This executes on the UI thread.
        switch (msg.what) {
            case PLAY: {
                String url = (String) msg.obj;
                WebChromeClient client = mWebView.getWebChromeClient();
                int videoLayerID = msg.arg1;
                if (client != null) {
                    VideoPlayer.play(url, mSeekPosition, this, client, videoLayerID);
                } else {
                    Log.w(LOGTAG, "warning: getWebChromeClient()==null");
                    VideoPlayer.play(url, mSeekPosition, this, null, videoLayerID);
                }
                break;
            }
            case SEEK: {
                Integer time = (Integer) msg.obj;
                mSeekPosition = time;
                VideoPlayer.seek(mSeekPosition, this);
                break;
            }
            case PAUSE: {
                VideoPlayer.pause(this);
                break;
            }
            // -- MTK_WEBKIT_FIX START --
            case ENDED: {
                if (msg.arg1 == 1)
                    VideoPlayer.isVideoSelfEnded = true;

                // -- MTK_WEBKIT_FIX START --
                // only handle the end() if current playback is not in FullScreen mode
                if (!(VideoPlayer.mHTML5VideoView.isFullScreenMode()))
                    VideoPlayer.end();;

                WebChromeClient client = mWebView.getWebChromeClient();
                if (client != null) {
                    client.onHideCustomView();
                }
                // -- MTK_WEBKIT_FIX END --
                break;
            }
            // -- MTK_WEBKIT_FIX END --
            case ERROR: {
                WebChromeClient client = mWebView.getWebChromeClient();
                if (client != null) {
                    client.onHideCustomView();
                }
                break;
            }
            case LOAD_DEFAULT_POSTER: {
                WebChromeClient client = mWebView.getWebChromeClient();
                if (client != null) {
                    doSetPoster(client.getDefaultVideoPoster());
                }
                break;
            }
            case TIMEUPDATE: {
                if (VideoPlayer.isPlaying(this)) {
                    sendTimeupdate();
                }
                break;
            }
            case BUFFERING_START: {
                VideoPlayer.setPlayerBuffering(true);
                break;
            }
            case BUFFERING_END: {
                VideoPlayer.setPlayerBuffering(false);
                break;
            }
        }
    }

    // Everything below this comment executes on the WebCore thread, except for
    // the EventHandler methods, which are called on the network thread.

    // A helper class that knows how to download posters
    private static final class PosterDownloader implements EventHandler {
        // The request queue. This is static as we have one queue for all posters.
        private static RequestQueue mRequestQueue;
        private static int mQueueRefCount = 0;
        // The poster URL
        private URL mUrl;
        // The proxy we're doing this for.
        private final HTML5VideoViewProxy mProxy;
        // The poster bytes. We only touch this on the network thread.
        private ByteArrayOutputStream mPosterBytes;
        // The request handle. We only touch this on the WebCore thread.
        private RequestHandle mRequestHandle;
        // The response status code.
        private int mStatusCode;
        // The response headers.
        private Headers mHeaders;
        // The handler to handle messages on the WebCore thread.
        private Handler mHandler;

        public PosterDownloader(String url, HTML5VideoViewProxy proxy) {
            try {
                mUrl = new URL(url);
            } catch (MalformedURLException e) {
                mUrl = null;
            }
            mProxy = proxy;
            mHandler = new Handler();
        }
        // Start the download. Called on WebCore thread.
        public void start() {
            retainQueue();

            if (mUrl == null) {
                return;
            }

            // Only support downloading posters over http/https.
            // FIXME: Add support for other schemes. WebKit seems able to load
            // posters over other schemes e.g. file://, but gets the dimensions wrong.
            String protocol = mUrl.getProtocol();
            if ("http".equals(protocol) || "https".equals(protocol)) {
                mRequestHandle = mRequestQueue.queueRequest(mUrl.toString(), "GET", null,
                        this, null, 0);
            }
        }
        // Cancel the download if active and release the queue. Called on WebCore thread.
        public void cancelAndReleaseQueue() {
            if (mRequestHandle != null) {
                mRequestHandle.cancel();
                mRequestHandle = null;
            }
            releaseQueue();
        }
        // EventHandler methods. Executed on the network thread.
        public void status(int major_version,
                int minor_version,
                int code,
                String reason_phrase) {
            mStatusCode = code;
        }

        public void headers(Headers headers) {
            mHeaders = headers;
        }

        public void data(byte[] data, int len) {
            if (mPosterBytes == null) {
                mPosterBytes = new ByteArrayOutputStream();
            }
            mPosterBytes.write(data, 0, len);
        }

        public void endData() {
            if (mStatusCode == 200) {
                if (mPosterBytes.size() > 0) {
                    Bitmap poster = BitmapFactory.decodeByteArray(
                            mPosterBytes.toByteArray(), 0, mPosterBytes.size());
                    mProxy.doSetPoster(poster);
                }
                cleanup();
            } else if (mStatusCode >= 300 && mStatusCode < 400) {
                // We have a redirect.
                try {
                    mUrl = new URL(mHeaders.getLocation());
                } catch (MalformedURLException e) {
                    mUrl = null;
                }
                if (mUrl != null) {
                    mHandler.post(new Runnable() {
                       public void run() {
                           if (mRequestHandle != null) {
                               mRequestHandle.setupRedirect(mUrl.toString(), mStatusCode,
                                       new HashMap<String, String>());
                           }
                       }
                    });
                }
            }
        }

        public void certificate(SslCertificate certificate) {
            // Don't care.
        }

        public void error(int id, String description) {
            cleanup();
        }

        public boolean handleSslErrorRequest(SslError error) {
            // Don't care. If this happens, data() will never be called so
            // mPosterBytes will never be created, so no need to call cleanup.
            return false;
        }
        // Tears down the poster bytes stream. Called on network thread.
        private void cleanup() {
            if (mPosterBytes != null) {
                try {
                    mPosterBytes.close();
                } catch (IOException ignored) {
                    // Ignored.
                } finally {
                    mPosterBytes = null;
                }
            }
        }

        // Queue management methods. Called on WebCore thread.
        private void retainQueue() {
            if (mRequestQueue == null) {
                mRequestQueue = new RequestQueue(mProxy.getContext());
            }
            mQueueRefCount++;
        }

        private void releaseQueue() {
            if (mQueueRefCount == 0) {
                return;
            }
            if (--mQueueRefCount == 0) {
                mRequestQueue.shutdown();
                mRequestQueue = null;
            }
        }
    }

    /**
     * Private constructor.
     * @param webView is the WebView that hosts the video.
     * @param nativePtr is the C++ pointer to the MediaPlayerPrivate object.
     */
    private HTML5VideoViewProxy(WebView webView, int nativePtr) {
        // This handler is for the main (UI) thread.
        super(Looper.getMainLooper());
        // Save the WebView object.
        mWebView = webView;
        // Pass Proxy into webview, such that every time we have a setBaseLayer
        // call, we tell this Proxy to call the native to update the layer tree
        // for the Video Layer's surface texture info
        mWebView.setHTML5VideoViewProxy(this);
        // Save the native ptr
        mNativePointer = nativePtr;
        // create the message handler for this thread
        createWebCoreHandler();
    }

    private void createWebCoreHandler() {
        mWebCoreHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PREPARED: {
                        Map<String, Object> map = (Map<String, Object>) msg.obj;
                        Integer duration = (Integer) map.get("dur");
                        Integer width = (Integer) map.get("width");
                        Integer height = (Integer) map.get("height");

                        // -- MTK_WEBKIT_FIX START --
                        // Add debug message

                        Log.e(LOGTAG, "To call nativeOnPrepared(), duration = " + duration.intValue() +
                                                                ", width = " + width.intValue() +
                                                                ", height = " + height.intValue() +
                                                                ", mNativePointer = " + mNativePointer +
                                                                ", proxy = " + this);
                        // -- MTK_WEBKIT_FIX END --

                        nativeOnPrepared(duration.intValue(), width.intValue(),
                                height.intValue(), mNativePointer);
                        break;
                    }
                    case ENDED:
                        mSeekPosition = 0;
                        // -- MTK_WEBKIT_FIX START --
                        // Add debug message

                        Log.e(LOGTAG, "To call nativeOnEnded(), mNativePointer = " + mNativePointer +
                                                             ", proxy = " + this);

                        // -- MTK_WEBKIT_FIX END --

                        nativeOnEnded(mNativePointer);
                        break;
                    case PAUSED:
                        // -- MTK_WEBKIT_FIX START --
                        // Add debug message

                        Log.e(LOGTAG, "To call nativeOnPaused(), mNativePointer = " + mNativePointer +
                                                              ", proxy = " + this);

                        // -- MTK_WEBKIT_FIX END --

                        nativeOnPaused(mNativePointer);
                        break;
                    case POSTER_FETCHED:
                        // -- MTK_WEBKIT_FIX START --
                        // Add debug message

                        Log.e(LOGTAG, "To call nativeOnPosterFetched(), mNativePointer = " + mNativePointer +
                                                                     ", proxy = " + this);

                        // -- MTK_WEBKIT_FIX END --

                        Bitmap poster = (Bitmap) msg.obj;
                        nativeOnPosterFetched(poster, mNativePointer);
                        break;
                    case TIMEUPDATE:
                        // -- MTK_WEBKIT_FIX START --
                        // Add debug message

                        Log.e(LOGTAG, "To call nativeOnTimeupdate(), position: " + msg.arg1 +
                                                                  ", mNativePointer = " + mNativePointer +
                                                                  ", proxy = " + this);

                        // -- MTK_WEBKIT_FIX END --

                        nativeOnTimeupdate(msg.arg1, mNativePointer);
                        break;
                    case STOPFULLSCREEN:
                        // -- MTK_WEBKIT_FIX START --
                        // Add debug message

                        Log.e(LOGTAG, "To call nativeOnStopFullscreen(), mNativePointer = " + mNativePointer +
                                                                      ", proxy = " + this);

                        // -- MTK_WEBKIT_FIX END --

                        nativeOnStopFullscreen(mNativePointer);
                        break;
                    // -- MTK_WEBKIT_FIX START --
                    case PREPARETOPLAY:
                        Log.e(LOGTAG, "To call nativePrepareToPlay(), mNativePointer = " + mNativePointer);

                        nativePrepareToPlay(mNativePointer);
                        break;
                    // -- MTK_WEBKIT_FIX END --
                }
            }
        };
    }

    private void doSetPoster(Bitmap poster) {
        if (poster == null) {
            return;
        }
        // Save a ref to the bitmap and send it over to the WebCore thread.
        mPoster = poster;
        Message msg = Message.obtain(mWebCoreHandler, POSTER_FETCHED);
        msg.obj = poster;
        mWebCoreHandler.sendMessage(msg);
    }

    private void sendTimeupdate() {
        Message msg = Message.obtain(mWebCoreHandler, TIMEUPDATE);
        msg.arg1 = VideoPlayer.getCurrentPosition();
        mWebCoreHandler.sendMessage(msg);
    }

    // -- MTK_WEBKIT_FIX START --
    private final Runnable timerRun = new Runnable() {
        public void run() {
            Message msg = Message.obtain(mWebCoreHandler, PREPARETOPLAY);
            mWebCoreHandler.sendMessage(msg);
        }
    };
    // -- MTK_WEBKIT_FIX START --

    public Context getContext() {
        return mWebView.getContext();
    }

    // -- MTK_WEBKIT_FIX START --
    public static void setVideoRect(int left, int top, int right, int bottom) {
        Rect vidRect;
        vidRect = new Rect(left, top, right, bottom);

        VideoPlayer.setVideoRect(vidRect);
    }
    // -- MTK_WEBKIT_FIX END --

    private String url;
    private int position;
    private int videoLayerID;
    // The public methods below are all called from WebKit only.
    /**
     * Play a video stream.
     * @param url is the URL of the video stream.
     */
    public void play(String url, int position, int videoLayerID) {
		this.url = url;
    	this.position = position;
    	this.videoLayerID = videoLayerID;

        // -- MTK_WEBKIT_FIX START --
        // Add debug message

        Log.e(LOGTAG, "From WebKit: Calling play(), url = " + url +
                                                 ", position = " + url +
                                                 ", videoLayerID = " + videoLayerID);

        // -- MTK_WEBKIT_FIX END --

        if (url == null) {
            return;
        }

        if (position > 0) {
            seek(position);
        }
        Message message = obtainMessage(PLAY);
        message.arg1 = videoLayerID;
        message.obj = url;
        sendMessage(message);
    }

    /**
     * Seek into the video stream.
     * @param  time is the position in the video stream.
     */
    public void seek(int time) {
        // -- MTK_WEBKIT_FIX START --
        // Add debug message

        Log.e(LOGTAG, "From WebKit: Calling seek(), time = " + time);

        // -- MTK_WEBKIT_FIX END --

        Message message = obtainMessage(SEEK);
        message.obj = new Integer(time);
        sendMessage(message);
    }

    /**
     * Pause the playback.
     */
    public void pause() {
        // -- MTK_WEBKIT_FIX START --
        // Add debug message

        Log.e(LOGTAG, "From WebKit: Calling pause()");

        // -- MTK_WEBKIT_FIX END --

        Message message = obtainMessage(PAUSE);
        sendMessage(message);
    }

    /**
     * Tear down this proxy object.
     */
    public void teardown() {
        // -- MTK_WEBKIT_FIX START --
        // Add debug message

        Log.e(LOGTAG, "From WebKit: Calling teardown()");

        // -- MTK_WEBKIT_FIX END --

        // This is called by the C++ MediaPlayerPrivate dtor.
        // Cancel any active poster download.
        if (mPosterDownloader != null) {
            mPosterDownloader.cancelAndReleaseQueue();
        }
        mNativePointer = 0;
    }

    /**
     * Load the poster image.
     * @param url is the URL of the poster image.
     */
    public void loadPoster(String url) {
        // -- MTK_WEBKIT_FIX START --
        // Add debug message

        Log.e(LOGTAG, "From WebKit: Calling loadPoster(), url = " + url);

        // -- MTK_WEBKIT_FIX END --

        if (url == null) {
            Message message = obtainMessage(LOAD_DEFAULT_POSTER);
            sendMessage(message);
            return;
        }
        // Cancel any active poster download.
        if (mPosterDownloader != null) {
            mPosterDownloader.cancelAndReleaseQueue();
        }
        // Load the poster asynchronously
        mPosterDownloader = new PosterDownloader(url, this);
        mPosterDownloader.start();
    }

    // These three function are called from UI thread only by WebView.
    public void setBaseLayer(int layer) {
        VideoPlayer.setBaseLayer(layer);
    }

    public void pauseAndDispatch() {
        // -- MTK_WEBKIT_FIX START --
        // Add debug message

        Log.e(LOGTAG, "From UI thread: Calling pauseAndDispatch()");

        // -- MTK_WEBKIT_FIX END --

        VideoPlayer.pauseAndDispatch();
    }

    public void enterFullScreenVideo(int layerId, String url) {
        // -- MTK_WEBKIT_FIX START --
        // Add debug message

        Log.e(LOGTAG, "From UI thread: Calling enterFullScreenVideo(), layerId = " + layerId +
                                                                 ",url = " + url);

        // -- MTK_WEBKIT_FIX END --


        VideoPlayer.enterFullScreenVideo(layerId, url, this, mWebView);
    }

    // -- MTK_WEBKIT_FIX START --
    // This function is called from UI thread only by WebView.
    public void prepareToPlay() {
        Log.e(LOGTAG, "From UI thread: Calling prepareToPlay()");

        int supportAutoplay = 0;
        if (0 != SystemProperties.get("mtk.browser.supportAutoplay").length()){
            supportAutoplay = Integer.parseInt(SystemProperties.get("mtk.browser.supportAutoplay"));
        }

        if (1 == supportAutoplay) {
            Message msg = Message.obtain(mWebCoreHandler, PREPARETOPLAY);
            mWebCoreHandler.sendMessageDelayed(msg, 3000);
        }
    }
    // -- MTK_WEBKIT_FIX END --

    /**
     * The factory for HTML5VideoViewProxy instances.
     * @param webViewCore is the WebViewCore that is requesting the proxy.
     *
     * @return a new HTML5VideoViewProxy object.
     */
    public static HTML5VideoViewProxy getInstance(WebViewCore webViewCore, int nativePtr) {
        return new HTML5VideoViewProxy(webViewCore.getWebView(), nativePtr);
    }

    /* package */ WebView getWebView() {
        return mWebView;
    }

    private native void nativeOnPrepared(int duration, int width, int height, int nativePointer);
    private native void nativeOnEnded(int nativePointer);
    private native void nativeOnPaused(int nativePointer);
    private native void nativeOnPosterFetched(Bitmap poster, int nativePointer);
    private native void nativeOnTimeupdate(int position, int nativePointer);
    private native void nativeOnStopFullscreen(int nativePointer);
    private native static boolean nativeSendSurfaceTexture(SurfaceTexture texture,
            int baseLayer, int videoLayerId, int textureName,
            int playerState);
    private native void nativePrepareToPlay(int nativePointer);

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            sendMessage(obtainMessage(BUFFERING_START, what, extra));
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            sendMessage(obtainMessage(BUFFERING_END, what, extra));
        }
        return false;
    }
}
