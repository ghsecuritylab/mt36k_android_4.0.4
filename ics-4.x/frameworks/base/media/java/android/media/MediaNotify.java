package android.media;

import java.lang.reflect.Method;
import android.os.IBinder;
import android.util.Log;

/**
 * @hide
 */
public class MediaNotify {
    public static String TAG = "MediaNotify";
    public static final int ANDROID_MEDIA_PLAYING = 1;
    public static final int ANDROID_PLAYING_VIDEO = 2;
    public static final int ANDROID_MEDIA_STOPED = 3;
    public static final int ANDROID_MEDIA_RELEASE = 4;
    private static Object tvRemoteService = null;
    private static Method method_send3rdMediaState = null;
    private static Method method_setmutestate = null;
    private static Method method_getmutestate = null;

    private static final String SERVICE_NAME = "TVRemoteService";

    /**
     * @hide
     */
    private static Object getTVRemoteService() {
        if (tvRemoteService != null) {
            return tvRemoteService;
        } else {
            IBinder binder = android.os.ServiceManager.getService(SERVICE_NAME);
            if (binder != null) {
                try {
                    Class cl = Class.forName("com.mediatek.tv.service.ITVRemoteService$Stub");
                    Class[] parameterTypes = new Class[] { IBinder.class };
                    Method method_asinterface = null;
                    Object[] args = new Object[] { binder };
                    method_asinterface = cl.getMethod("asInterface", parameterTypes);
                    if (method_asinterface != null) {
                        tvRemoteService = method_asinterface.invoke(cl, args);
                        return tvRemoteService;
                    }
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    /**
     * @hide
     */
    public static void tvNotifyMediaStatus(int state) {
        try {
            Object service = getTVRemoteService();
            if (service != null) {
                if (method_send3rdMediaState == null) {
                    Class cl = Class.forName("com.mediatek.tv.service.ITVRemoteService");
                    Class[] parameterTypes = new Class[] { int.class };
                    method_send3rdMediaState = cl.getMethod("send3rdMassage_proxy", parameterTypes);
                    Log.d(TAG, "method_send3rdMediaState=" + method_send3rdMediaState);
                }
                if (method_send3rdMediaState != null) {
                    Object[] args = new Object[] { state };
                    method_send3rdMediaState.invoke(service, args);
                }
            }
        } catch (Exception e) {
        }
    }
	
    /**
     * @hide
     */
    public static void tvSetMuteState(boolean b_mute) {
        try {
            Object service = getTVRemoteService();
            if (service != null) {
                if (method_setmutestate == null) {
                    Class cl = Class.forName("com.mediatek.tv.service.ITVRemoteService");
                    Class[] parameterTypes = new Class[] { boolean.class };
                    method_setmutestate = cl.getMethod("setMuteState_proxy", parameterTypes);
                    Log.d(TAG, "method_setmutestate=" + method_setmutestate);
                }
                if (method_setmutestate != null) {
                    Object[] args = new Object[] { b_mute};
                    method_setmutestate.invoke(service, args);
                }
            }
        } catch (Exception e) {
        }
    }  
    
    /**
     * @hide
     */
    public static boolean tvGetMuteState() {
    	boolean mRet =false;
        try {
            Object service = getTVRemoteService();
            if (service != null) {
                if (method_getmutestate == null) {
                    Class cl = Class.forName("com.mediatek.tv.service.ITVRemoteService");
                    method_getmutestate = cl.getMethod("getMuteState_proxy", null);
                    Log.d(TAG, "method_getmutestate=" + method_getmutestate);
                }
                if (method_getmutestate != null) {
                	mRet = (Boolean)method_getmutestate.invoke(service, null);
                }
            }
        } catch (Exception e) {
        }
        return mRet;
    }
}