/**
 * 
 */
package com.mediatek.tvcm;

import android.os.Handler;

import com.mediatek.tv.model.ScanListener;
import com.mediatek.tv.service.ScanService;
import com.mediatek.tv.service.ScanService.ScanUserOperationListener;
import com.mediatek.tvcm.TVScanner.ScannerListener;
import com.mediatek.tvcm.TVScanner.WaitScannerListener;

public abstract class ScanTask {
	String type;
	boolean isUpdate;
	int freqFrom;
	int freqTo;
	int scanMode;
	ScannerListener listener = null;
	WaitScannerListener waitListener = null;
	// TVScanner scanner;
	boolean canceled = false;
	boolean isWait = false;
	boolean isSave = true;

	TVChannelManager cm;
	protected class scanListenerDelegater implements ScanListener {
		private ScannerListener usrListener = null;
		int chNum = 0;
		Handler hndlr;

		scanListenerDelegater(ScannerListener usrListener) {
			this.usrListener = usrListener;
			hndlr = scanner.getHandler();
		}

		public void setScanCanceled(String scanMode) {
			hndlr.post(new Runnable() {
				public void run() {
					onCancel();
					if (usrListener != null) {

						usrListener
								.onCompleted(ScannerListener.COMPLETE_CANCEL);
					}
				}
			});
		}

		public void setScanCompleted(String scanMode) {

			hndlr.post(new Runnable() {
				public void run() {
					onComplete();
					// We must ensure that we have 100%
					if (usrListener != null) {

						usrListener.onProgress(100, chNum);
						usrListener.onCompleted(ScannerListener.COMPLETE_OK);
					}
				}
			});

		}

		public void setScanError(String scanMode, int error) {
			// Who can tell me what the error ** is???
			hndlr.post(new Runnable() {
				public void run() {
					onComplete();

					if (usrListener != null) {
						usrListener.onCompleted(ScannerListener.COMPLETE_ERROR);
					}
				}
			});

		};

		public void setScanFrequence(java.lang.String scanMode,
				final int frequence) {
			if (usrListener != null) {
				hndlr.post(new Runnable() {
					public void run() {
						usrListener.onFrequence(frequence);
					}
				});
			}

		}

		public void setScanProgress(java.lang.String scanMode,
				final int progress, final int channels) {
			chNum = channels;
			if (usrListener != null) {
				hndlr.post(new Runnable() {
					public void run() {
						usrListener.onProgress(progress, channels);
					}
				});
			}
		}
	}

	class ScanUserOperationListenerDelegater implements
			ScanUserOperationListener {
		Handler hndlr;
		private WaitScannerListener listener;

		ScanUserOperationListenerDelegater(WaitScannerListener listener) {
			this.listener = listener;
			hndlr = scanner.getHandler();
		}

		public void onScanUserOperation(final int currentFreq,
				final int foundChNum, final int finishData) {
			if (listener != null) {
				hndlr.post(new Runnable() {
					public void run() {
						listener.onWaitProgress(currentFreq, foundChNum,
								finishData);
					}
				});
			}

		}
	}

	TVScanner scanner;
	
	// boolean isUpdate;
	// String curRawMode;
	// ScanTask task = null;

	// ConcretScanner(TVScanner scanner) {
	// this.scanner = scanner;
	// }

	void onComplete() {
		if (isSave) {
		TVChannelManager cm = scanner.getContent().getChannelManager();
		cm.update();
		cm.flush();
		}
		scanner.onCompleteScan();

	}

	void onCancel() {
		if (isSave) {
		TVChannelManager cm = scanner.getContent().getChannelManager();
		cm.update();
		cm.flush();
		}
		scanner.onCancelScan();
	}

	abstract public void cancel();

	// {
	// //Why does TVManager not remember what mode is scanning....
	// scanService.cancelScan(ScanModeOption.toRawScanMode(task.getScanMode()));
	// }

	abstract public boolean scan();

	public ScanTask(TVScanner scanner, String type, ScannerListener listener) {
		freqFrom = -1;
		freqTo = -1;
		this.type = type;
		this.listener = listener;
		this.scanner = scanner;
		this.cm = scanner.getContent().getChannelManager();
		// ScanModeOption mode = (ScanModeOption)
		// scanner.getOption(TVScanner.SCAN_OPTION_SCAN_MODE);
		// this.scanMode = mode.get();
	}

	public ScanTask(TVScanner scanner, String type, ScannerListener listener,
			WaitScannerListener waitListener) {
		freqFrom = -1;
		freqTo = -1;
		this.type = type;
		this.listener = listener;
		this.scanner = scanner;
		this.waitListener = waitListener;
		this.cm = scanner.getContent().getChannelManager();
		isWait = true;
	}

	String getType() {
		return type;
	}

	void setFreqRange(int freqFrom) {
		this.freqFrom = freqFrom;
		isUpdate = true;
	}
	void setFreqRange(int freqFrom, int freqTo) {
		this.freqFrom = freqFrom;
		this.freqTo = freqTo;
		isUpdate = true;
	}

	void dtvSetFreqRange(int freqFrom, int freqTo) {
		this.freqFrom = freqFrom;
		this.freqTo = freqTo;
		isUpdate = false;
	}
	
	int getScanMode() {
		return scanMode;
	}
	void setUpdate(boolean update) {
		if (freqFrom != -1) {
			return;
		}
		isUpdate = update;
	}

	boolean isUpdate() {
		return isUpdate;
	}

	int getFreqFrom() {
		return freqFrom;
	}
	int getFreqTo() {
		return freqTo;
	}
	ScannerListener getListener() {
		return listener;
	}

	// void onComplete() {
	// scanner.onCompleteScan();
	// }

	// void scan() {
	// ConcretScanner cs = scanner.getConcretScanner(standard);
	// cs.scan(this);
	// }
	// void cancel() {
	// ConcretScanner cs = scanner.getConcretScanner(standard);
	// cs.cancel();
	// }
	//
	// void onCancel() {
	// scanner.onCancelScan();
	// }
}