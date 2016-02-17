package com.mediatek.ui.mmp.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.util.Log;

import com.mediatek.dlna.object.DLNADevice;
import com.mediatek.mmpcm.audioimpl.AudioInfo;
import com.mediatek.mmpcm.videoimpl.FileInfo;
import com.mediatek.netcm.dlna.DLNAFile;
import com.mediatek.netcm.dlna.DLNAManager;
import com.mediatek.netcm.dlna.FileEvent;
import com.mediatek.netcm.dlna.FileEventListener;
import com.mediatek.ui.mmp.util.Lists;
import com.mediatek.ui.util.MtkLog;

public class DlnaFilesManager extends FilesManager<FileAdapter> {
	private static final String TAG = "DlnaFilesManager";
	private static final boolean LocalLog = false;
	private static DlnaFilesManager sInstance;
	private DLNAManager mOperator;

	private Stack<String> mHistory;
	private String mTempParent;
	private AudioInfo mAudioInfo;
	private FileInfo mFileInfo;
	
	protected DlnaFilesManager(Context context) {
		super(context);
		mHistory = Lists.newStack();

		mOperator = DLNAManager.getInstance();
		mOperator.setOnFileEventListener(new FileEventListener() {
			public void onFileLeft(FileEvent event) {
				MtkLog.i(TAG, "OnFileLeft");
				String currentDeviceName;
				LinkedList<DLNAFile> files = event.getFileList(getType());
				for (DLNAFile file : files) {
					MtkLog.d(TAG, "OnFileLeft : " + file.getPath());
				}
				int sourceType = MultiFilesManager.getInstance(mContext)
						.getCurrentSourceType();
				if (sourceType == MultiFilesManager.SOURCE_ALL) {
					onFileFound(event);
				} else if (sourceType == MultiFilesManager.SOURCE_DLNA) {
					DLNADevice device = event.getLeftDevice();
					if (null != device) {
						String leftDeviceName = device.getName();
						MtkLog
								.i(TAG, " DLNA device  leave  :"
										+ leftDeviceName);
						synchronized (mFiles) {
							if (mFiles.size() > 0) {
								FileAdapter currentFile = mFiles.get(0);

								MtkLog.i(TAG, "  Current file  path  :"
										+ currentFile.getPath());

								if (currentFile.isDevice()) {
									currentDeviceName = currentFile
											.getDeviceName();
								} else {
									currentDeviceName = mOperator
											.getDevice(currentFile.getPath());
								}
								MtkLog.i(TAG, "  Current DLNA device  name  :"
										+ currentDeviceName);
								if (currentFile.getDeviceName().equals(
										leftDeviceName)) {
									// TODO leave
									MtkLog.i(TAG, "goto  the root dic");
									addFiles(files, REQUEST_DEVICE_LEFT);
								} else {
									addFiles(files, REQUEST_BACK_DEVICE_LEFT);
								}
							} else {
								currentDeviceName = mOperator
										.getDevice(getCurrentPath());
								if (currentDeviceName == null
										|| (null != event
												.getLeftDevice() && currentDeviceName.equals(event
												.getLeftDevice().getName()))) {
									addFiles(files, REQUEST_DEVICE_LEFT);
								} else {
									addFiles(files, REQUEST_BACK_DEVICE_LEFT);
								}
							}

						}
					}
				}
			}

			public void onFileFound(FileEvent event) {
				MtkLog.i(TAG, "OnFileFound");
				LinkedList<DLNAFile> files = new LinkedList<DLNAFile>();
				files.addAll(event.getFileList(getType()));
				for (DLNAFile file : files) {
					MtkLog.d(TAG, "OnFileFound : " + file.getName());
				}

				List<FileAdapter> wrapedFiles = wrapFiles(files);
				synchronized (mFiles) {
					mFiles.clear();
					mFiles.addAll(wrapedFiles);

					setChanged();
					notifyObservers(REQUEST_REFRESH);
				}
			}

			public void onFileFailed(FileEvent event) {
				MtkLog.i(TAG, "OnFileFailed");
				setChanged();
				notifyObservers(REQUEST_REFRESH);
			}
		});

		mAudioInfo = AudioInfo.getInstance();
		mFileInfo = FileInfo.getInstance();
	}

	private void addFiles(LinkedList<DLNAFile> files, int flag){
		if (files == null ){
			return;
		}
		List<FileAdapter> wrapedFiles = wrapFiles(files);
		if (flag == REQUEST_BACK_DEVICE_LEFT){
			
			synchronized (mDevices) {
				mDevices.clear();
				mDevices.addAll(wrapedFiles);
			}
			setChanged();
			notifyObservers(REQUEST_BACK_DEVICE_LEFT);
			
		} else if (flag == REQUEST_DEVICE_LEFT){
			mHistory.clear();
			synchronized (mFiles) {
				mFiles.clear();
				mFiles.addAll(wrapedFiles);
			}
			setChanged();
			notifyObservers(REQUEST_DEVICE_LEFT);

		}
	}

	private int getType() {
		int type = FileEvent.FILTER_TYPE_ALL;
		switch (mContentType) {
		case CONTENT_PHOTO:
			type = FileEvent.FILTER_TYPE_IMAGE;
			break;
		case CONTENT_AUDIO:
			type = FileEvent.FILTER_TYPE_AUDIO;
			break;
		case CONTENT_VIDEO:
			type = FileEvent.FILTER_TYPE_VIDEO;
			break;
		case CONTENT_TEXT:
			type = FileEvent.FILTER_TYPE_TEXT;
			break;
		default:
			break;
		}
		return type;
	}

	public static DlnaFilesManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new DlnaFilesManager(context);
		}

		return sInstance;
	}

	@Override
	public void setCurrentPath(String path) {
		mTempParent = mParentPath;

		super.setCurrentPath(path);
	}

	public List<FileAdapter> getDevices() {
		synchronized (mDevices) {
			return mDevices;
		}
	}

	@Override
	public List<FileAdapter> listAllFiles(String path) {
		MtkLog.d(TAG, "List Path : " + path);
		MtkLog.d(TAG, "Parent Path : " + mTempParent);

		synchronized (mFiles) {
			mFiles.clear();

			try {
				String name = retriveName(path);
				if (path != null && path.equals(mTempParent)) {
					MtkLog.i(TAG, "Back!!");
					mOperator.parseDLNAFile(name, false);
					mHistory.pop();
				} else {
					MtkLog.i(TAG, "Into");
					mOperator.parseDLNAFile(name, true);
					if (name != null) {
						mHistory.push(name);
					}

					mRootPath = "";
				}
			} catch (ExceptionInInitializerError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return mFiles;
		}
	}

	@Override
	protected FileAdapter newWrapFile(Object originalFile) {
		DLNAFile file = (DLNAFile) originalFile;
		String name = file.getName();
		String absolutePath = retriveAbsolutePath(name);

		return new DlnaFileAdapter(file, absolutePath, mAudioInfo, mFileInfo);
	}

	private String retriveAbsolutePath(String name) {
		String absolute;
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < mHistory.size(); i++) {
			builder.append("/").append(mHistory.get(i));
		}

		builder.append("/").append(name);
		absolute = builder.toString();
		MtkLog.d(TAG, "RetriveAbsolutePath : " + absolute);

		return absolute;
	}

	private String retriveName(String path) {
		if (path == null) {
			return null;
		}

		String name = path.substring(path.lastIndexOf("/") + 1, path.length());
		MtkLog.d(TAG, "RetriveName : " + name);

		return name;
	}

	public void clearHistory() {
		mHistory.clear();
	}

	@Override
	public void destroy() {
		try {
			MtkLog.i(TAG, "Destory!!");
			mOperator.destroy();
			sInstance = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<FileAdapter> listRecursiveFiles(int contentType) {
		return null;
	}
}
