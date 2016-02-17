package com.mediatek.tv.service;
import com.mediatek.tv.common.TVCommon;

public class Logger {
    public static boolean ANDROID_LOG = true;

    public static void d(String TAG, String content) {
    	if ((TVCommon.debugLevel & TVCommon.debugLevelDebug) == 0) {
    		return ;
    	}
        if (ANDROID_LOG) {
            android.util.Log.d(TAG, content);
        } else {
            System.out.println(TAG + "---" + content);
        }
    }

    public static void e(String TAG, String content) {
    	if ((TVCommon.debugLevel & TVCommon.debugLevelError) == 0) {
    		return ;
    	}
        if (ANDROID_LOG) {
            android.util.Log.e(TAG, content);
        } else {
            System.out.println(TAG + "---" + content);
        }
    }

    public static void i(String TAG, String content) {
    	if ((TVCommon.debugLevel & TVCommon.debugLevelInfo) == 0) {
    		return ;
    	}
        if (ANDROID_LOG) {
            android.util.Log.i(TAG, content);
        } else {
            System.out.println(TAG + "---" + content);
        }
    }

    public static void w(String TAG, String content) {
    	if ((TVCommon.debugLevel & TVCommon.debugLevelWarning) == 0) {
    		return ;
    	}
        if (ANDROID_LOG) {
            android.util.Log.w(TAG, content);
        } else {
            System.out.println(TAG + "---" + content);
        }
    }

    public static void v(String TAG, String content) {
    	if ((TVCommon.debugLevel & TVCommon.debugLevelVerbose) == 0) {
    		return ;
    	}
        if (ANDROID_LOG) {
            android.util.Log.v(TAG, content);
        } else {
            System.out.println(TAG + "---" + content);
        }
    }
}
