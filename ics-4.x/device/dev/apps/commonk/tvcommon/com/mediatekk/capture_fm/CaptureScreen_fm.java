package com.mediatekk.capture_fm;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;

public class CaptureScreen_fm implements Callback {

	interface CaptureCallback {
		/**
		 * 
		 */
		void onPictureTaken(File file);
	}

	private SparseArray<File> fileArray = new SparseArray<File>();
	private Handler mHandler;
	private final static boolean DEBUG = false;

	private static final String TAG = "CaptureScreen";

	private CaptureScreen_fm() {
		if (Looper.myLooper() == null) {
			mHandler = new Handler(Looper.getMainLooper(), this);
		} else {
			mHandler = new Handler(Looper.myLooper(), this);
		}
	}

	private CaptureScreen_fm(Looper looper) {
		mHandler = new Handler(looper, this);
	}

	private static CaptureScreen_fm instance = null;

	public static CaptureScreen_fm getInstance() {
		if (instance == null) {
			instance = new CaptureScreen_fm();
		}
		return instance;
	}

	/**
	 * @param looper
	 *            the operation compress bitmap to jpg will run in the thread
	 *            who own this loop.
	 * @return @return the singleton instance of CaptureScreen
	 */
	public static CaptureScreen_fm getInstance(Looper looper) {
		if (instance == null) {
			instance = new CaptureScreen_fm(looper);
		}
		return instance;
	}
	public void captureJPGPictureOnce(int msiOn, Rect rect, File file) {
		fileArray.put(0, file);
		nativeCaptureScreenOnce_fm(msiOn, rect.top, rect.left, rect.width(),
				rect.height());
	}

	//public void captureJPGPicture(Rect rect, File file) {
	public void captureJPGPicture(Rect rect) {
		int magic = (int) SystemClock.elapsedRealtime();
		//fileArray.put(magic, file);
		nativeCaptureScreen_fm(magic, rect.top, rect.left, rect.width(),
				rect.height());
	}

	public int open(boolean msiOn) {
		nativeOpen_fm(msiOn ? 1 : 0);
		return 0;
	}

	public int close(boolean msiOn) {
		nativeClose_fm(msiOn ? 1 : 0);
		return 0;
	}

	private native final void nativeCaptureScreenOnce_fm(int msiOn, int top,
			int left, int width, int height);

	private native void nativeOpen_fm(int msiOn);

	private native void nativeClose_fm(int msiOn);

	private native void nativeCaptureScreen_fm(int magic, int top, int left,
			int width, int height);

	static {
		System.loadLibrary("capture_fm");
	}

	private void postEventFromNative(int what, int arg1, int arg2, Object obj) {
		Log.d(TAG, "postEventFromNative [what]:" + what);

		Message msg = mHandler.obtainMessage(what, arg1, arg2, obj);
		mHandler.sendMessage(msg);
//		handleMessage(msg);
	}

	private final static int ORIENTATION_NORMAL = 1;
	private final static int ORIENTATION_FLIP_HORIZONTAL = 2;
	private final static int ORIENTATION_ROTATE_180 = 3;
	private final static int ORIENTATION_VERTICAL = 4;

	@Override
	public boolean handleMessage(Message msg) {
		byte[] data = (byte[]) msg.obj;
		Log.d("lkm", "handleMessage");
		/*int flip = msg.arg1;
		int mirror = msg.arg2;
		int magic = msg.what;
		int ratation = 0;
		File file;
		FileOutputStream fos;
		file = fileArray.get(magic);

		if (DEBUG) {
			try {
				// dump raw data for debug.
				FileOutputStream dumpFos = new FileOutputStream("/mnt/usbdisk/"
						+ file.getName() + "_dump");
				dumpFos.write(data);
				dumpFos.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		long startTime = SystemClock.elapsedRealtime();
		BitmapFactory.Options options = new BitmapFactory.Options();
		/*
		 * options.inJustDecodeBounds = true; // options.inPreferredConfig =
		 * Bitmap.Config.ARGB_8888; Bitmap bitmap =
		 * BitmapFactory.decodeByteArray(data, 0, data.length, options);
		 */
		/*options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
				options);
		boolean res = false;
		if (bitmap != null) {
			try {
				int val = flip << 1 & mirror;
				switch (val) {
				case 1:
					ratation = ORIENTATION_FLIP_HORIZONTAL;
				case 2:
					ratation = ORIENTATION_VERTICAL;
				case 3:
					ratation = ORIENTATION_ROTATE_180;
					break;

				default:
					ratation = ORIENTATION_NORMAL;
					break;
				}
				Log.d(TAG, "ratation:" + ratation);
				fos = new FileOutputStream(file);
				res = bitmap.compress(CompressFormat.JPEG, 100, ratation, fos);
				fos.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		Log.d(TAG,
				"Compress bitmap to jpg consume miliseconds:"
						+ (SystemClock.elapsedRealtime() - startTime) + " res:"
						+ res);*/
		return true;
	}

}
