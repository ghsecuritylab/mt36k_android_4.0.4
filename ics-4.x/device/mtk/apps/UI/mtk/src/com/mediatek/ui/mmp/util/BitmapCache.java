package com.mediatek.ui.mmp.util;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.util.Log;

public final class BitmapCache {
	private static final String TAG = "BitmapCache";
	private static BitmapCache mInstance;
	private ConcurrentHashMap<String, SoftReference<Bitmap>> mCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();

	private BitmapCache() {
	}

	public static BitmapCache createCache(boolean clear) {
		if (mInstance == null) {
			mInstance = new BitmapCache();
		} else {
			if (clear) {
				mInstance.mCache.clear();
			}
		}
		return mInstance;
	}

	public Bitmap get(String key) {
		SoftReference<Bitmap> ref = mCache.get(key);
		Bitmap target = null;
		if (ref != null) {
			if (ref.get() != null) {
				// MtkLog.w(TAG, "Hit Cache!!");
			}
			target = ref.get();
		}
		return target;
	}

	public void put(String key, Bitmap bitmap) {
		mCache.put(key, new SoftReference<Bitmap>(bitmap));
	}

	public void clear() {
		mCache.clear();
	}
	public ConcurrentHashMap<String, SoftReference<Bitmap>> getCache()
	{
		return mCache;
	}
}
