/**
 * 
 */
package com.mediatek.tvcommon;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.ScanListener;
import com.mediatek.tv.service.BroadcastService;
import com.mediatek.tv.service.ChannelService;
import com.mediatek.tv.service.ScanService;
import com.mediatek.tv.service.ChannelService.ChannelOperator;
import com.mediatek.tvcommon.TVScanner.ScannerListener;

public abstract class ScanTask {

	protected ScanService scanService;
	protected ChannelService channelService;
	protected TVConfigurer cfg;
	protected Context mContext;
	protected BroadcastService brdSrv;

	private final Object mActionDoneSync = new Object();
	private boolean mActionDone = false;
	// maximum time we wait for scan complete broadcast before going on
	private static final int MAX_UPDATE_TIME = 2000;
	protected int state = TVScanner.STATE_COMPLETED;
	protected int type = 0;

	public static final int TYPE_ATV = 0;
	public static final int TYPE_DTV = 1;

	private BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			actionDone();
		}
	};

	private void actionDone() {
		synchronized (mActionDoneSync) {
			mActionDone = true;
			mActionDoneSync.notifyAll();
		}
	}

	void onComplete() {
		flush();
		finish();
		state = TVScanner.STATE_COMPLETED;
	}

	void onCancel() {
		flush();
		finish();
		state = TVScanner.STATE_COMPLETED;
	}

	int getState() {
		return state;
	}

	private void finish() {
		synchronized (mActionDoneSync) {
			mActionDone = false;
		}
		// send broadcast to notify server to update new scanned channels.
		Intent intent = new Intent(TVCommonNative.SCAN_COMPLETE_ACTION);
		intent.putExtra("scan_type", type);
		mContext.sendOrderedBroadcast(intent, null, br, null, 0, null, null);
		final long endTime = SystemClock.elapsedRealtime() + MAX_UPDATE_TIME;
		synchronized (mActionDoneSync) {
			while (!mActionDone) {
				long delay = endTime - SystemClock.elapsedRealtime();
				if (delay < 0) {
					TVCommonNative.TVLog("Time out.");
					break;
				}
				try {
					mActionDoneSync.wait(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void flush() {
		try {
			TVCommonNative.TVLog("flush 2 DBs");
			channelService.fsStoreChannelList(ChannelCommon.DB_AIR);
			channelService.fsStoreChannelList(ChannelCommon.DB_CABEL);
		} catch (TVMException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cancel current scan task.
	 */
	public void cancel() {
		scanService.cancelScan(getRawScanMode());
	}

	public void prepareScan() {
		try {
			brdSrv.syncStopService();
		} catch (TVMException e1) {
			e1.printStackTrace();
		}
		state = TVScanner.STATE_SCANNING;
	}

	/**
	 * Start current scan task.
	 */
	public abstract boolean scan(ScannerListener listener);

	abstract String getRawScanMode();

	protected String getNativeDBName() {
		if (null == cfg) {
			TVCommonNative.TVLog("null == cfg, set default Cable DB");
			return ChannelCommon.DB_CABEL;
		} 

		/* Notice: grp is 255 when input not ready in tv start */
		int grp = cfg.getGroup();
		TVCommonNative.TVLog("grp: " + grp);
		if (ConfigType.CONFIG_VALUE_GROUP_DTV != grp) {
			return ChannelCommon.DB_CABEL;
		}

		@SuppressWarnings("unchecked")
		TVOption<Integer> option = (TVOption<Integer>) cfg
				.getOption(IntegerOption.CFG_BS_SRC);

		if(option == null){
			throw new ExceptionInInitializerError("The configuration parameters IntegerOption.CFG_BS_SRC is not found in the collection of parameters");
		}
		
		return option.get() == ConfigType.BS_SRC_AIR ? ChannelCommon.DB_AIR
				: ChannelCommon.DB_CABEL;
	}

	synchronized void preSetChannels(List<ChannelInfo> raws) {
		flush();
		finish();
	}

	class ScanListenerDelegater implements ScanListener {
		private Handler mHandler = new Handler(Looper.getMainLooper());
		private ScannerListener listener;
		private int channels;

		ScanListenerDelegater(ScannerListener listener) {
			this.listener = listener;
		}

		@Override
		public void setScanCanceled(String arg0) {
			onCancel();
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					listener.onCompleted(ScannerListener.COMPLETE_CANCEL);
				}
			});
		}

		@Override
		public void setScanCompleted(String arg0) {
			onComplete();
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					listener.onProgress(100, channels);
					listener.onCompleted(ScannerListener.COMPLETE_OK);
				}
			});
		}

		@Override
		public void setScanError(String scanMode, int error) {
			onComplete();
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					listener.onCompleted(ScannerListener.COMPLETE_ERROR);
				}
			});
		}

		@Override
		public void setScanFrequence(String scanMode, final int freq) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					listener.onFrequence(freq);
				}
			});
		}

		@Override
		public void setScanProgress(String scanMode, final int progress,
				final int channels) {
			this.channels = channels;
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					listener.onProgress(progress, channels);
				}
			});
		}

	}

}
