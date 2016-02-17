package com.mediatek.ui.mmp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

//import com.mediatek.dm.DeviceManagerEvent;
//import com.mediatek.dm.MountPoint;
//import com.mediatek.mmpcm.device.DevListener;
//import com.mediatek.mmpcm.device.DevManager;
import com.mediatek.mmpcm.mmcimpl.Const;
import com.mediatek.mmpcm.mmcimpl.PlayList;
import com.mediatek.ui.mmp.util.Lists;
import com.mediatek.ui.mmp.util.LogicManager;
import com.mediatek.ui.util.DestroyApp;
import com.mediatek.ui.util.MtkLog;

public class MultiFilesManager extends FilesManager<FileAdapter> implements
		Observer {
	private static final String TAG = "MultiFilesManager";

	private static final String ROOT_PATH = "/";
	private String mCurrentUsbdisBlock = null;
	public static final int SOURCE_ALL = 0;
	public static final int SOURCE_LOCAL = 1;
	public static final int SOURCE_SMB = 2;
	public static final int SOURCE_DLNA = 3;

	public static final int NO_DEVICES = 0;
	public static final int ONE_DEVICES = 1;
	public static final int MORE_DEVICES = 2;

	public static final int SUB_DIRCTORY = 1;

	private static MultiFilesManager sInstance;

	private LocalFilesManager mLocalManager;
	private SmbFilesManager mSmbManager;
	private DlnaFilesManager mDlnaManager;
//	private DevManager mDevManager;

	private int mSourceType;

	private List<FileAdapter> mLocalDevices;
	private List<FileAdapter> mSmbDevices;
	private List<FileAdapter> mDlnaDevices;
	private List<FileAdapter> mAllDevices;

	private static boolean mSmbAvailable;
	private static boolean mDlnaAvailable;

	private List<String> mLeftLocalDevices;
	private List<String> mFoundLocalDevices;
	private List<String> mVirtualLocalDevices;

	// Add by Dan for fix bug DTV00374299
	private int mMountedIsoCount = 0;

	private MultiFilesManager(Context context, boolean smbAvailable,
			boolean dlnaAvailable) {
		super(context);
		mSmbAvailable = smbAvailable;
		mDlnaAvailable = dlnaAvailable;

		mSourceType = SOURCE_ALL;

		initDevices();
		initFilesManager(context);
		initDevicesManager();
	}

	private void initDevices() {

		mLocalDevices = Lists.newArrayList();
		mSmbDevices = Lists.newArrayList();
		mDlnaDevices = Lists.newArrayList();
		mLeftLocalDevices = Lists.newArrayList();
		mFoundLocalDevices = Lists.newArrayList();
		mVirtualLocalDevices = Lists.newArrayList();
		mAllDevices = null;
	}

	public String getCurDevName() {
		if (mSourceType != SOURCE_ALL) {
			if (null != mLocalDevices) {
				String[] paths = mCurrentPath.split("/");
				MtkLog.i(TAG, " path len :" + paths.length);
				for (String path : paths) {
					MtkLog.i(TAG, " path  :" + path);
				}
				String devName = null;
				if (paths.length > 2) {
					if ("usbdisk".equals(paths[2]) || "sdcard".equals(paths[2])) {
						devName = getCurrentUsbdisBlock();
					} else if ("usb".equals(paths[2])) {
						devName = paths[3];
					} else if ("mnt".equals(paths[1])){
						//Virtual Devices
						devName = paths[2];
					}
					return devName;
				}
			} 
		}
		return null;
	}

	private void initFilesManager(Context context) {
		mLocalManager = LocalFilesManager.getInstance(context);

		if (mSmbAvailable) {
			mSmbManager = SmbFilesManager.getInstance(context);
			mSmbManager.setRootPath("smb://");
		}

		if (mDlnaAvailable) {
			mDlnaManager = DlnaFilesManager.getInstance(context);
		}
	}

//	private DevListener mDevListener = new DevListener() {
//		public void onEvent(DeviceManagerEvent event) {
//			MtkLog.d(TAG, "Device Event : " + event.getType());
//			int type = event.getType();
//
//			switch (type) {
//			case DeviceManagerEvent.connected:
//				MtkLog.d(TAG, "Device Event Connected!!");
//				break;
//			case DeviceManagerEvent.disconnected:
//				MtkLog.d(TAG, "Device Event Disconnected!!");
//				stopDecode();
//				// onLocalDevicesStateChanged();
//				break;
//			case DeviceManagerEvent.mounted:
//				MtkLog.d(TAG, "Device Event Mounted!!");
//				String path = event.getMountPointPath();
//				if ("/mnt/sdcard".equals(path)) {
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//				onLocalDevicesStateChanged();
//				break;
//			case DeviceManagerEvent.umounted:
//				MtkLog.d(TAG, "Device Event Unmounted!!");
//				setAuidoOnlyOff();
//				stopDecode();
//				onLocalDevicesStateChanged();
//				break;
//			case DeviceManagerEvent.unsupported:
//				MtkLog.d(TAG, "Device Event Unsupported!!");
//				break;
//			default:
//				break;
//			}
//		}
//	};

	private void stopDecode() {
		MtkLog.d(TAG, "  stopdecode() begin -----------");
		LogicManager.getInstance(mContext).stopDecode();
		MtkLog.d(TAG, "stopdecode() finish ------------");

	}

	/**
	 * Close auido only.
	 */
	private void setAuidoOnlyOff() {
		LogicManager logicManager = LogicManager.getInstance(mContext);

		if (logicManager.isAudioOnly()) {
			logicManager.setAudioOnly(false);
		}
	}

	private void initDevicesManager() {
//		try {
//			mDevManager = DevManager.getInstance();
//			mDevManager.addDevListener(mDevListener);
//		} catch (ExceptionInInitializerError e) {
//			mDevManager = null;
//		}
	}

	public static MultiFilesManager getInstance(Context context) {
		return getInstance(context, mSmbAvailable, mDlnaAvailable);
	}

	public static MultiFilesManager getInstance(Context context,
			boolean smbAvailable, boolean dlnaAvailable) {
		if (sInstance == null) {
			sInstance = new MultiFilesManager(context, smbAvailable,
					dlnaAvailable);
			sInstance.setRootPath(ROOT_PATH);
		} else {
			mSmbAvailable = smbAvailable;
			mDlnaAvailable = dlnaAvailable;
		}

		return sInstance;
	}

	public boolean login(String path, String userName, String userPwd) {
		return mSmbManager.login(path, userName, userPwd);
	}

	@Override
	public List<FileAdapter> listAllFiles(String path) {
		MtkLog.d(TAG, "Source Type : " + mSourceType);
		MtkLog.d(TAG, "List Path : " + path);
		MtkLog.d(TAG, "listAllFiles start = " + mFiles.size());
		synchronized (mFiles) {
			mFiles.clear();
			if (mSourceType == SOURCE_ALL) {
				if (mAllDevices != null) {
					mAllDevices.clear();

					mAllDevices.addAll(mLocalDevices);
					mAllDevices.addAll(mSmbDevices);
					mAllDevices.addAll(mDlnaDevices);
					mFiles.addAll(mAllDevices);
					if (!mSmbAvailable && !mDlnaAvailable
							&& getAllDevicesNum() == ONE_DEVICES) {
						mHandler.sendEmptyMessage(SUB_DIRCTORY);
					}

				} else {
					getLocalDevices();

					if (mSmbAvailable) {
						getSmbDevices();
					}
					if (mDlnaAvailable) {
						getDlnaDevices();
					}

					if (mAllDevices == null) {
						mAllDevices = Lists.newArrayList();
						mAllDevices.addAll(mLocalDevices);
						mAllDevices.addAll(mSmbDevices);
					} else {
						mAllDevices.clear();
					}

					if (!mSmbAvailable && !mDlnaAvailable
							&& getAllDevicesNum() == ONE_DEVICES) {
						mHandler.sendEmptyMessage(SUB_DIRCTORY);
					}

					mFiles.addAll(mAllDevices);
				}

			} else {
				if (mSourceType == SOURCE_LOCAL) {
					mFiles = mLocalManager.listAllFiles(path);
				} else if (mSourceType == SOURCE_SMB) {
					mFiles = mSmbManager.listAllFiles(path);
				} else if (mSourceType == SOURCE_DLNA) {
					mDlnaManager.listAllFiles(path);
				}
			}
			MtkLog.d("TAG", "listAllFiles end" + mFiles.size());

			logFiles(TAG);

			return mFiles;
		}
	}

	private int delayMillis = 20;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case SUB_DIRCTORY: {
				if (getAllDevicesNum() == ONE_DEVICES) {
					setChanged();
					notifyObservers(REQUEST_SUB_DIRECTORY);
				}
			}
				break;
			default:
				break;
			}
		}
	};

	private void onLocalDevicesStateChanged() {
		MtkLog.d(TAG, "onLocalDevicesStateChanged 1 ");
		if (mAllDevices != null) {
			MtkLog.d(TAG, "onLocalDevicesStateChanged 2 ");
			findLocalLeftDevices();
			findLocalAddDevices();
			getLocalDevices();
			mAllDevices.clear();
			mAllDevices.addAll(mLocalDevices);
			mAllDevices.addAll(mSmbDevices);
			mAllDevices.addAll(mDlnaDevices);
		} else {
			return;
		}
		MtkLog.d(TAG, "onLocalDevicesStateChanged 3 ");
		if (mSourceType == SOURCE_ALL || mSourceType == SOURCE_LOCAL) {
			checkDevicesStateChanged();
		}
	}

	private void closePlayer() {
		LogicManager.getInstance(mContext).onDevUnMount();
		((DestroyApp) mContext.getApplicationContext())
				.finishMediaPlayActivity();
	}

	private void checkDevicesStateChanged() {
		int deviceNum = getAllDevicesNum();
		List<String> leftLDevs = getLocalLeftDevices();
		List<String> foundLDevs = getLocalAddDevices();
		String curDevName = getCurDevName();
		String curPath = getCurrentPath();

		if (checkVirtualDevice()) {
			closePlayer();
			setChanged();
			notifyObservers(REQUEST_BACK_TO_ROOT);
			return;
		}
		MtkLog.d(TAG, "checkDevicesStateChanged  NUM=" + deviceNum);
		switch (deviceNum) {
		case MORE_DEVICES: {
			if (leftLDevs.size() > 0) {
				if (curPath != null && curPath.equals("/")) {
					closePlayer();
					setChanged();
					notifyObservers(REQUEST_BACK_TO_ROOT);
				} else {
					for (String DevName : leftLDevs) {
						if (DevName.equals(curDevName)) {
							closePlayer();
							setChanged();
							notifyObservers(REQUEST_BACK_TO_ROOT);
							return;
						}
					}
				}
				break;
			} else if (null != curPath && foundLDevs.size() > 0) {
				if (!curPath.equals("/")) {
					break;
				}
				closePlayer();
				setChanged();
				notifyObservers(REQUEST_BACK_TO_ROOT);
			}
		}
			break;
		case NO_DEVICES: {
			MtkLog.d(TAG, "checkDevicesStateChanged 1 ");
			closePlayer();
			setChanged();
			notifyObservers(REQUEST_BACK_TO_ROOT);
		}
			break;
		case ONE_DEVICES: {
			MtkLog.d(TAG, "checkDevicesStateChanged 2");
			if (leftLDevs.size() > 0) {
				MtkLog.d(TAG, "checkDevicesStateChanged 3 curDevName  " + curDevName);
				for (String DevName : leftLDevs) {
					MtkLog.d(TAG, "checkDevicesStateChanged 3 Left DevName  " + DevName);
					if (DevName.equals(curDevName) || curPath.equals("/")) {
						MtkLog.d(TAG, "checkDevicesStateChanged 4 " + DevName);
						closePlayer();
						setChanged();
						notifyObservers(REQUEST_SUB_DIRECTORY);
						break;
					}
				}
			} else if (foundLDevs.size() == 1) {
				closePlayer();
				setChanged();
				notifyObservers(REQUEST_SUB_DIRECTORY);
			}
		}
			break;
		default:
			break;
		}
	}

	public int getAllDevicesNum() {
		if (mLocalDevices.size() == 1 && mSmbDevices.size() == 0
				&& mDlnaDevices.size() == 0) {
			MtkLog.d(TAG, "getAllDevicesNum 1");
			return ONE_DEVICES;
		} else if (mLocalDevices.size() == 0 && mSmbDevices.size() == 0
				&& mDlnaDevices.size() == 0) {
			MtkLog.d(TAG, "getAllDevicesNum 0");
			return NO_DEVICES;
		} else {
			return MORE_DEVICES;
		}

	}

	// Add by Dan for fix bug DTV00374299
	public int getMountedIsoCount() {
		return mMountedIsoCount;
	}

	public String getDeviceMountPoint() {
		if (mLocalDevices == null || mLocalDevices.size() <= 0) {
			return null;
		}
		return mLocalDevices.get(0).getPath();
	}

	public List<String> getLocalLeftDevices() {
		return mLeftLocalDevices;
	}

	public List<String> getLocalAddDevices() {
		return mFoundLocalDevices;
	}

	/**
	 * check virtual device.
	 * 
	 * @return if true, checked. else unchecked.
	 */
	public boolean checkVirtualDevice() {
//		List<String> devices = mVirtualLocalDevices;
//		if (mDevManager != null && devices != null && devices.size() > 0) {
//			for (String devName : devices) {
//				if (mDevManager.isVirtualDev(devName)) {
//					MtkLog.d(TAG, "Get Mount List :  true");
//					return true;
//				}
//			}
//		}
		MtkLog.d(TAG, "Get Mount List :  false");
		return false;
	}

	private void findLocalAddDevices() {
//		if (mFoundLocalDevices == null || mVirtualLocalDevices == null){
//			return;
//		}
//		mFoundLocalDevices.clear();
//		mVirtualLocalDevices.clear();
//		if (mDevManager != null) {
//			ArrayList<MountPoint> devices = mDevManager.getMountList();
//
//			if (null != devices && devices.size() > 0) {
//				for (MountPoint mountPoint : devices) {
//					int i = 0;
//					for (FileAdapter localD : mLocalDevices) {
//						if (localD.getName().equals(mountPoint.mDeviceName)) {
//							break;
//						}
//						i++;
//					}
//
//					if (i >= mLocalDevices.size()) {
//						mFoundLocalDevices.add(mountPoint.mDeviceName);
//						if (mDevManager.isVirtualDev(mountPoint.mMountPoint)) {
//							mVirtualLocalDevices.add(mountPoint.mMountPoint);
//						}
//					}
//				}
//			}
//		}
	}

	private void findLocalLeftDevices() {
//		mLeftLocalDevices.clear();
//		if (mDevManager != null) {
//			ArrayList<MountPoint> devices = mDevManager.getMountList();
//
//			if (null != devices && mLocalDevices.size() > 0) {
//				for (FileAdapter localD : mLocalDevices) {
//					int i = 0;
//					for (MountPoint mountPoint : devices) {
//						if (localD.getName().equals(mountPoint.mDeviceName)) {
//							break;
//						}
//						i++;
//					}
//
//					if (i >= devices.size()) {
//						mLeftLocalDevices.add(localD.getName());
//					}
//				}
//			}
//		}
	}

	public void getLocalDevices() {
//		if (mDevManager != null) {
//			// Add by Dan for fix bug DTV00374299
//			mMountedIsoCount = 0;
//			mLocalDevices.clear();
//			ArrayList<MountPoint> devices = mDevManager.getMountList();
//		//	MtkLog.d(TAG, "Get Mount List : " + devices.size());
//
//			if (null != devices && devices.size() > 0) {
//				for (MountPoint mountPoint : devices) {
//					MtkLog.d(TAG, "Device : " + mountPoint.mMountPoint);
//					// Add by Dan for fix bug DTV00374299
//					if (mDevManager.isVirtualDev(mountPoint.mMountPoint)) {
//						mMountedIsoCount++;
//					}
//					mLocalDevices.add(new LocalFileAdapter(
//							mountPoint.mMountPoint, mountPoint.mDeviceName));
//				}
//
//			}
//		} else {
//			mLocalDevices.add(new LocalFileAdapter(Environment
//					.getExternalStorageDirectory()));
//		}
	}

	private void getSmbDevices() {
		if (mSmbManager != null) {
			mSmbDevices.clear();
			mSmbDevices.addAll(mSmbManager.listAllFiles(mSmbManager
					.getRootPath()));
		}
	}

	private void getDlnaDevices() {
		if (mDlnaManager != null) {
			mDlnaManager.clearHistory();
			mDlnaManager.listAllFiles(null);
		}
	}

	private void addFiles(List<FileAdapter> files) {
		MtkLog.d(TAG, "Add Files : " + files.size());
		synchronized (mFiles) {
			mFiles.clear();
			if (mSourceType == SOURCE_ALL) {
				mDlnaDevices.clear();
				mDlnaDevices.addAll(files);
				if (mAllDevices == null) {
					mAllDevices = Lists.newArrayList();
				}
				mAllDevices.clear();
				mAllDevices.addAll(mLocalDevices);
				mAllDevices.addAll(mSmbDevices);
				mAllDevices.addAll(mDlnaDevices);

				mFiles.addAll(mAllDevices);
			} else {
				mFiles.addAll(files);
			}

			logFiles(TAG);
		}
	}

	public int getCurrentSourceType() {
		return mSourceType;
	}

	public void setCurrentSourceType(int type) {
		mSourceType = type;
	}

	public int getSourceType(String path) {
		int source = mSourceType;
		if (path == null || ROOT_PATH.equals(path)) {
			source = SOURCE_ALL;
		} else if (source == SOURCE_ALL) {
			for (FileAdapter file : mLocalDevices) {
				if (file.getAbsolutePath().equals(path)) {
					source = SOURCE_LOCAL;
					mLocalManager.setRootPath(path);
					MtkLog.d(TAG, "Source : LOCAL!!");
					return source;
				}
			}

			for (FileAdapter file : mSmbDevices) {
				if (file.getAbsolutePath().equals(path)) {
					source = SOURCE_SMB;
					MtkLog.d(TAG, "Source : SMB!!");
					return source;
				}
			}

			for (FileAdapter file : mDlnaDevices) {
				if (path != null && file.getAbsolutePath().equals(path)) {
					source = SOURCE_DLNA;
					MtkLog.d(TAG, "Source : DLNA!!");
					return source;
				}
			}
		}

		return source;
	}
	//add by xudong 
	 private void setCurrentUsbdisBlock(String path) {
//		 String[] paths = path.split("/");
//			if (paths.length > 2) {
//				if ("usbdisk".equals(paths[2]) || ("sdcard".equals(paths[2]))) {
//					// devName = "sda1";
//					if (mDevManager != null) {
//						ArrayList<MountPoint> devices = mDevManager
//								.getMountList();						
//						if (null != devices && devices.size() > 0) {							
//							mCurrentUsbdisBlock = devices.get(0).mDeviceName;							
//						}
//					}
//				} 
//			}
		}
    private String getCurrentUsbdisBlock() {
		return mCurrentUsbdisBlock;		
	}
    //end
	@Override
	public void setCurrentPath(String path) {
		MtkLog.d(TAG, "Set Path : " + path);
		mSourceType = getSourceType(path);

		switch (mSourceType) {
		case SOURCE_LOCAL:
			mLocalManager.setCurrentPath(path);	
			//add by xudong	
			setCurrentUsbdisBlock(path);
			//end			
			break;
		case SOURCE_SMB:
			mSmbManager.setCurrentPath(path);
			break;
		case SOURCE_DLNA:
			mDlnaManager.setCurrentPath(path);
			break;
		default:
			break;
		}

		mCurrentPath = path;
		if (mCurrentPath == null || ROOT_PATH.equals(mCurrentPath)) {
			mCurrentPath = mRootPath;
			mParentPath = null;
		} else {
			switch (mSourceType) {
			case SOURCE_LOCAL:
				mParentPath = mLocalManager.getParentPath();
				break;
			case SOURCE_SMB:
				mParentPath = mSmbManager.getParentPath();
				if (null != mParentPath && mParentPath.equals(mSmbManager.getRootPath())) {
					mParentPath = ROOT_PATH;
				}
				break;
			case SOURCE_DLNA:
				mParentPath = mDlnaManager.getParentPath();
				if (null != mParentPath && mParentPath.equals(mDlnaManager.getRootPath())) {
					mParentPath = ROOT_PATH;
					mDlnaManager.clearHistory();
				}
				break;
			default:
				break;
			}

			if (mParentPath == null) {
				mParentPath = ROOT_PATH;
			}
		}

		MtkLog.d(TAG, "Current Path : " + mCurrentPath);
		MtkLog.d(TAG, "Parent Path : " + mParentPath);
	}

	@Override
	public void setContentType(int contenType) {
		mLocalManager.setContentType(contenType);
		if (mSmbManager != null) {
			mSmbManager.setContentType(contenType);
		}

		if (mDlnaManager != null) {
			mDlnaManager.setContentType(contenType);
		}

		super.setContentType(contenType);
	}

	@Override
	public void setSortType(int sortType) {
		mLocalManager.setSortType(sortType);

		super.setSortType(sortType);
	}

	@Override
	public boolean canPaste(String file) {
		return mLocalManager.canPaste(file);
	}

	@Override
	public boolean isInSameFolder(String path1, String path2) {
		return mLocalManager.isInSameFolder(path1, path2);
	}

	@Override
	public void destroy() {
//		if (mDevManager != null) {
//			mDevManager.removeDevListener(mDevListener);
//		}

		mLocalManager.destroy();
		if (mSmbManager != null) {
			mSmbManager.destroy();
		}

		if (mDlnaManager != null) {
			mDlnaManager.deleteObserver(this);
			mDlnaManager.destroy();
		}

		sInstance = null;
	}

	public List<FileAdapter> listRecursiveFiles(int contentType,
			int sourceType, boolean bind) {
		List<FileAdapter> files = null;

		switch (sourceType) {
		case SOURCE_LOCAL:
			files = mLocalManager.listRecursiveFiles(contentType);
			break;
		case SOURCE_SMB:
			files = mSmbManager.listRecursiveFiles(contentType);
			break;
		case SOURCE_DLNA:
			files = mDlnaManager.listRecursiveFiles(contentType);
			break;
		default:
			break;
		}


		MtkLog.d(TAG, "List Recursive Files!!");
		logFiles(TAG, files);

		return files;
	}

	public void setFiles(List<FileAdapter> files) {
		mFiles = files;
	}

	@Override
	public List<FileAdapter> listRecursiveFiles(int contentType) {
		return listRecursiveFiles(contentType, mSourceType, true);
	}

	public PlayList getPlayList(List<FileAdapter> originalFiles,
			int currentIndex, int contentType, int sourceType) {

		if (currentIndex < 0) {
			return null;
		}

		PlayList playlist = PlayList.getPlayList();

		int type = 0;
		switch (contentType) {
		case CONTENT_AUDIO:
			type = Const.FILTER_AUDIO;
			break;
		case CONTENT_PHOTO:
		case CONTENT_THRDPHOTO:
			type = Const.FILTER_IMAGE;
			break;
		case CONTENT_VIDEO:
			type = Const.FILTER_VIDEO;
			break;
		case CONTENT_TEXT:
			type = Const.FILTER_TEXT;
			break;
		default:
			break;
		}

		int source = 0;
		switch (sourceType) {
		case SOURCE_LOCAL:
			source = Const.FILE_TYPE_USB;
			break;
		case SOURCE_SMB:
			source = Const.FILE_TYPE_SMB;
			break;
		case SOURCE_DLNA:
			source = Const.FILE_TYPE_DLNA;
			break;
		default:
			break;
		}

		synchronized (originalFiles) {
			List<String> files = Lists.newArrayList();
			int index = 0;
			int count = 0;
			FileAdapter original = originalFiles.get(currentIndex);
			switch (sourceType) {
			case SOURCE_LOCAL:
			case SOURCE_SMB:
				for (FileAdapter file : originalFiles) {
					if (file.isFile()) {
						files.add(file.getAbsolutePath());
						if (file.equals(original)) {
							index = count;
						}
						count++;
					}
				}
				break;
			case SOURCE_DLNA:
				for (FileAdapter file : originalFiles) {
					if (file.isFile()) {
						files.add(file.getName());
						if (file.equals(original)) {
							index = count;
						}
						count++;
					}
				}
				break;
			default:
				break;
			}

			MtkLog.d(TAG, "PlayList Index : " + index);

			playlist.addFiles(type, source, files);
			playlist.setCurrentIndex(type, index);
		}

		return playlist;
	}

	public PlayList getPlayList(int currentIndex) {
		return getPlayList(mFiles, currentIndex, mContentType, mSourceType);
	}

	@Override
	public void addObserver(Observer observer) {
		super.addObserver(observer);

		if (mSmbManager != null) {
			mSmbManager.addObserver(observer);
		}

		if (mDlnaManager != null) {
			mDlnaManager.addObserver(this);
		}
	}

	public void deleteObserver(Observer observer) {
		super.deleteObserver(observer);

		if (mSmbManager != null) {
			mSmbManager.deleteObserver(observer);
		}

		if (mDlnaManager != null) {
			mDlnaManager.deleteObserver(this);
		}
	}

	public void deleteObservers() {
		super.deleteObservers();

		if (mSmbManager != null) {
			mSmbManager.deleteObservers();
		}

		if (mDlnaManager != null) {
			mDlnaManager.deleteObservers();
		}
	}

	public void update(Observable observable, Object data) {
		int request = (Integer) data;
		
		if (request == REQUEST_BACK_DEVICE_LEFT) {
			mDlnaDevices.clear();
			mDlnaDevices.addAll(mDlnaManager.getDevices());

		} else if (request == REQUEST_REFRESH) {
			if (mSourceType == SOURCE_ALL || mSourceType == SOURCE_DLNA) {
				if (mDlnaAvailable) {
					List<FileAdapter> files = mDlnaManager.getCurrentFiles();

					if (mSourceType == SOURCE_DLNA && files.size() > 0) {
						if (files.get(0).isDevice()) {
							return;
						}
					}
					addFiles(files);
				}
				setChanged();
				notifyObservers(data);
			}
		} else if (request == REQUEST_DEVICE_LEFT) {

			mSourceType = SOURCE_ALL;
			mDlnaDevices.clear();
			mDlnaDevices.addAll(mDlnaManager.getCurrentFiles());
			setChanged();
			notifyObservers(data);
		}
	}
}
