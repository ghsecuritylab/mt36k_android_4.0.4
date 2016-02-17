package com.mediatek.ui.mmp.util;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mediatek.mmpcm.photoimpl.PhotoUtil;
import com.mediatek.ui.mmp.util.AsyncLoader.LoadWork;

public class ImageManager {

	public interface ImageLoad {
		void imageLoad(PhotoUtil bitmap);
	}

	private static final int MSG_LOAD_IMAGE = 1;

	private static final int ACTION_CUR = 1;

	private static final int ACTION_PRE = 2;

	private static final int ACTION_NEXT = 3;

	private static final int ACTION_AUTO_NEXT = 4;

	private static ImageManager mInstance;

	ImageLoad mImageLoad;

	private AsyncLoader<PhotoUtil> mLoader;

	private LogicManager mLogicManager;

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case MSG_LOAD_IMAGE: {
				if (mImageLoad == null){
					break;
				}
				if (msg.obj == null) {
					mImageLoad.imageLoad(null);
				} else {
					mImageLoad.imageLoad((PhotoUtil) (msg.obj));
				}
				break;

			}
			default:
				break;
			}

		}

	};

	private class LoadBitmap implements LoadWork<PhotoUtil> {

		private int mType;

		public LoadBitmap(int type) {
			mType = type;
		}

		public PhotoUtil load() {
			if (mLogicManager == null){
				return null;
			}

			switch (mType) {
			case ACTION_CUR: {
				return mLogicManager.getCurImageBitmap();
			}
			case ACTION_NEXT: {
				return mLogicManager.getNextImageBitmap();
			}
			case ACTION_PRE: {
				return mLogicManager.getPreImageBitmap();
			}
			case ACTION_AUTO_NEXT: {
				return mLogicManager.getNextImage();
			}
			default:
				break;
			}
			return null;
		}

		public void loaded(PhotoUtil result) {
			Message msg = mHandler.obtainMessage(MSG_LOAD_IMAGE);
			msg.obj = result;
			mHandler.sendMessage(msg);
		}

	}

	public static ImageManager getInstance() {

		if (mInstance == null) {
			mInstance = new ImageManager();
		}

		return mInstance;
	}

	public void setImageLoad(ImageLoad img,LogicManager manager) {
		mImageLoad = img;
		mLogicManager=manager;
	}

	private ImageManager() {
		//mLoader = new AsyncLoader<Bitmap>(1);
		mLoader = new AsyncLoader<PhotoUtil>("DecodeBitmapThread");
	}

	public void autoPlay() {
		mLoader.clearQueue();
		mLoader.addWork(new LoadBitmap(ACTION_AUTO_NEXT));
	}

	public void playNext() {
		mLoader.clearQueue();
		mLoader.addWork(new LoadBitmap(ACTION_NEXT));
	}

	public void playPre() {
		mLoader.clearQueue();
		mLoader.addWork(new LoadBitmap(ACTION_PRE));
	}

	public void playCur() {
		mLoader.clearQueue();
		mLoader.addWork(new LoadBitmap(ACTION_CUR));
	}

	public void finish() {
		mLoader.clearQueue();
//		mLoader=null;
//		mInstance=null;
	}
}
