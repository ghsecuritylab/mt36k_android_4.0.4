package com.mediatek.ui.mmp.multimedia;

import java.util.List;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mediatek.media.MtkMediaPlayer;
import com.mediatek.media.MtkMediaPlayer.OnCompletionListener;
import com.mediatek.mmpcm.mmcimpl.Const;
import com.mediatek.mmpcm.videoimpl.VideoConst;
import com.mediatek.netcm.dlna.FileSuffixConst;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tvcm.TVContent;
import com.mediatek.ui.R;
import com.mediatek.ui.menu.util.MenuConfigManager;
import com.mediatek.ui.menu.util.SaveValue;
import com.mediatek.ui.mmp.MeidaMainActivity;
import com.mediatek.ui.mmp.commonview.MenuListView;
import com.mediatek.ui.mmp.commonview.MtkVideoView;
import com.mediatek.ui.mmp.commonview.MusicPlayInfoView;
import com.mediatek.ui.mmp.model.FileAdapter;
import com.mediatek.ui.mmp.model.FilesManager;
import com.mediatek.ui.mmp.model.LocalFileAdapter;
import com.mediatek.ui.mmp.model.MultiFilesManager;
import com.mediatek.ui.mmp.util.AsyncLoader;
import com.mediatek.ui.mmp.util.LogicManager;
import com.mediatek.ui.mmp.util.AsyncLoader.LoadWork;
import com.mediatek.ui.util.DestroyApp;
import com.mediatek.ui.util.KeyMap;
import com.mediatek.ui.util.MtkLog;
import com.mediatek.ui.util.ScreenConstant;

public class MtkFilesListActivity extends MtkFilesBaseListActivity {

	private static final String TAG = "MtkFilesListActivity";

	private static final int MODE_FILES_LIST = 0;

	private static final int MODE_CONTENT_TYPES_LIST = 1;

	private ImageView vTopImage;

	private TextView vTopTV;

	private TextView vTopFilePath;

	private TextView vTopPageSize;

	private ImageView vThumbnail;

	private TextView vDetailInfo;

	private static MtkVideoView vVideoView;

	private TextView vTextShow;

	private LinearLayout vLinearLayout;

	private LinearLayout vMidLayout;

	private LinearLayout vTopLayout;

	private int mPlayPosition = -1;

	private int mListItemHeght = ScreenConstant.SCREEN_HEIGHT/18;

	private int mMode = MODE_CONTENT_TYPES_LIST;

	public static boolean LOCALlogV = true;

	private AsyncLoader<Bitmap> mLoader;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//mLoader = new AsyncLoader<Bitmap>(1);
		mLoader = AsyncLoader.getInstance(1);
	}
	/**
	 * {@inheritDoc}
	 */
	protected void initMusicView() {
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.mmp_musicbackplay, null);
		vMusicView = new MusicPlayInfoView(this, contentView, 1,
				mCompletionListener);
		contentView.findViewById(R.id.mmp_music_playback_spectrum)
				.setVisibility(View.GONE);
		mPopView = new PopupWindow(contentView, vLinearLayout.getWidth(),
				vLinearLayout.getHeight());
	}

	/**
	 * Play video completion listener
	 */
	private OnCompletionListener mCompletionListener = new OnCompletionListener() {
		public boolean onCompletion(MtkMediaPlayer arg0, int flag, int arg2) {
			// flag==1 play all files finish ,flag==0 play a file finish
			if (flag == 1) {
				vMusicView.removeMessage();
				mPopView.dismiss();
			}
			return false;
		}
	};

	/**
	 * {@inheritDoc}
	 */
	protected void showMusicView() {
		super.showMusicView();

		Looper.myQueue().addIdleHandler(new IdleHandler() {
			public boolean queueIdle() {

				MtkLog.i(TAG, "  vMidLayout.getLeft()  :"
						+ vMidLayout.getLeft() + "  vLinearLayout.getTop():"
						+ vLinearLayout.getTop());
				mPopView.setWidth(vLinearLayout.getWidth());
				mPopView.setHeight(vLinearLayout.getHeight());

				mPopView.showAtLocation(vMidLayout, Gravity.LEFT | Gravity.TOP,
						vMidLayout.getLeft(), vLinearLayout.getTop()
								+ vTopLayout.getHeight() + 5);
				vMusicView.init(MtkFilesListActivity.this);
				return false;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	protected void onResume() {
		super.onResume();
		if (vList != null && vList.isInTouchMode()) {
			new Thread() {
				public void run() {
					try {
						Instrumentation inst = new Instrumentation();
						inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
						inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
					} catch (Exception e) {
						e.getStackTrace();
					}
				}
			}.start();
		}

		if ((getListContentType() != FilesManager.CONTENT_VIDEO)
				&& LogicManager.getInstance(this).isAudioStarted()) {
			vLinearLayout.setVisibility(View.VISIBLE);
			showMusicView();
		} else {
		 //modified by keke for fix DTV00380564
			LogicManager.getInstance(this).setScreenMode(ConfigType.SCREEN_MODE_NORMAL);
			vLinearLayout.setVisibility(View.INVISIBLE);

		}

		mPlayMode = 0;
	}

	//modified by keke for fix DTV00380564
	protected void onPause() {
		if (getListContentType() == FilesManager.CONTENT_VIDEO) {
			String m = TVContent.getInstance(this).getStorage().get(
					"SCREENMODE_FILELIST");
			try {
				if (!m.equals("")) {
					int u = Integer.parseInt(m);
					if (u == 1) {
					} else {
						LogicManager.getInstance(this).setScreenMode(u);
					}
				}
			} catch (Exception e) {

			}
		}
		super.onPause();
	}

	/* if from list mode to grid mode, true, else false, 
	 * for clear info and thumbnail array not in onstop function,
	 * but in press bule key. avoid clear array item when add in grid mode 
	 * Help me?*/
	private boolean mIsGridMode=false;
	/*For close preview mode video add by lei*/
	protected void onDestroy() {
		if(vVideoView!=null){
		vVideoView.onRelease();
		}
		if (!mIsGridMode) {
			if (null != mInfoLoader) {
				mInfoLoader.clearQueue();
			}

			if (null != mLoader) {
				mLoader.clearQueue();
			}
		}
		mIsGridMode = false;
		super.onDestroy();
	}

	/**
	 * {@inheritDoc}
	 */
	protected void onRestart() {
		super.onRestart();
		int position = setCurrentSelection();
		FileAdapter file = getListItem(position);
		if (null != file) {
			if (file.isFile()
					&& (getListContentType() == FilesManager.CONTENT_VIDEO)&&vVideoView!=null){
				//modified by keke for DTV00383229	
				vVideoView.init();
				playSelectionItem(file.getAbsolutePath());
			}
		}
	}

	private void showNowPlayingAudio() {
		MtkLog.d(TAG, "ShowNowPlayingAudio!!");

		String audioName = null;
		try {
			audioName = LogicManager.getInstance(this).getCurrentFileName(
					Const.FILTER_AUDIO);
		} catch (IndexOutOfBoundsException e) {
		}

		if (audioName != null) {
			MtkLog.d(TAG, "Now Playing Audio Name : " + audioName);
			for (int i = 1, length = getListItemsCount(); i <= length; i++) {
				String fileName = getListItem(i).getName();
				if (audioName.equals(fileName)) {
					mPlayPosition = i;
					MtkLog.d(TAG, "Find Playing Audio : " + mPlayPosition);

					break;
				}
			}

			if (mPlayPosition >= 0) {
				setListSelection(mPlayPosition);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected int setupContentView() {
		return R.layout.mmp_files_list;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void bindData(Intent intent) {
		int mode = intent.getIntExtra("Mode", -1);
		if (mode > -1) {
			mMode = mode;
		}

		if (mMode == MODE_CONTENT_TYPES_LIST) {
			getIntentData(intent);
			setupHeader();
			refreshListView(null);
		} else {
			super.bindData(intent);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	protected FilesAdapter getAdapter() {
		return new MtkFilesListAdapter(this);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void findViews() {
		vTopImage = (ImageView) findViewById(R.id.mmp_list_topimg);
		vTopTV = (TextView) findViewById(R.id.mmp_list_toptv);
		vTopFilePath = (TextView) findViewById(R.id.mmp_list_filepath);
		vTopPageSize = (TextView) findViewById(R.id.mmp_list_pagesize);
		vThumbnail = (ImageView) findViewById(R.id.mmp_listmode_thumnail);
		vDetailInfo = (TextView) findViewById(R.id.mmp_list_detailinfo);

		vVideoView = (MtkVideoView) findViewById(R.id.mmp_video_videoview);
		vTextShow = (TextView) findViewById(R.id.mmp_listmode_textshow);

		vLinearLayout = (LinearLayout) findViewById(R.id.mmp_listmode_musicicon);
		vMidLayout = (LinearLayout) findViewById(R.id.mmp_listmode_rl);
		vTopLayout = (LinearLayout) findViewById(R.id.multimedia_top);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void setupHeader() {
		vThumbnail.setVisibility(View.GONE);
		if(vVideoView!=null){
		vVideoView.setVisibility(View.GONE);
		}
		vTextShow.setVisibility(View.GONE);

		int contentType = getListContentType();
		if (contentType >= 0) {
			if (contentType == FilesManager.CONTENT_PHOTO
					|| contentType == FilesManager.CONTENT_AUDIO) {
				vThumbnail.setVisibility(View.VISIBLE);
			} else if (contentType == FilesManager.CONTENT_VIDEO) {
				if(vVideoView!=null){
				vVideoView.setVisibility(View.VISIBLE);
				}
			} else if (contentType == FilesManager.CONTENT_TEXT) {
				vTextShow.setVisibility(View.VISIBLE);
			} else if (contentType == FilesManager.CONTENT_THRDPHOTO){
				vThumbnail.setVisibility(View.VISIBLE);
				contentType = FilesManager.CONTENT_PHOTO;
			}

			vTopImage.setImageDrawable(mContentTypeIcons[contentType]);
			vTopTV.setText(mContentTypeNames[contentType]);
		}

		if (mMode == MODE_CONTENT_TYPES_LIST) {
			vTopFilePath.setText("");
		} else if (mMode == MODE_FILES_LIST) {
			vTopFilePath.setText(getListCurrentPath());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void customMenu(MenuListView menu) {
		menu.removeItem(2);
		// Added by Dan for fix bug DTV00379191
		menu.setFirstIndex(-1);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyMap.KEYCODE_MENU:
			if (mMode == MODE_CONTENT_TYPES_LIST) {
				return true;
			}
			break;
		case KeyMap.KEYCODE_DPAD_LEFT:
			if (mMode == MODE_CONTENT_TYPES_LIST) {
				DestroyApp destroyApp = (DestroyApp) getApplicationContext();
				destroyApp.finishAll();
			} else if (mMode == MODE_FILES_LIST) {
				onListItemClick(null, null, 0, 0);
			}
			return true;
		case KeyMap.KEYCODE_DPAD_RIGHT:
			onListItemClick(null, null, getListSelectedItemPosition() + 1, 0);
			return true;
		case KeyMap.KEYCODE_VOLUME_UP: {
			if(vVideoView!=null&&vVideoView.getVideoPlayStatus()>VideoConst.PLAY_STATUS_STARTED){
				LogicManager.getInstance(this).setVolumeUp();
				return true;
			}else{
				break;
			}
			
		
		}
		case KeyMap.KEYCODE_VOLUME_DOWN: {
	        if(vVideoView!=null&&vVideoView.getVideoPlayStatus()>VideoConst.PLAY_STATUS_STARTED){
	        	LogicManager.getInstance(this).setVolumeDown();
	        	return true;
			}else{
				break;
			}
		}
		case KeyMap.KEYCODE_MTKIR_MUTE: {
	        if(vVideoView!=null&&vVideoView.getVideoPlayStatus()>VideoConst.PLAY_STATUS_STARTED){
	        	LogicManager.getInstance(this).setMute();
	        	return true;
			}else{
				break;
			}
		}
		case KeyMap.KEYCODE_MTKIR_BLUE:
			if(!MeidaMainActivity.isValid(400)){
				break;
			}
			if (null != vVideoView && !vVideoView.isStop()) {
				vVideoView.stop();
			}
			Intent intent = new Intent();
			if (mMode == MODE_CONTENT_TYPES_LIST) {
				mFilesManager.destroy();
				mFilesManager.deleteObservers();
				intent.setClass(this, MeidaMainActivity.class);
				MeidaMainActivity.mSelection=getListSelectedItemPosition() + 1;
//				intent.putExtra("selection", getListSelectedItemPosition() + 1);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			} else if (mMode == MODE_FILES_LIST) {
				mLoader.clearQueue();
				mIsGridMode = true;
				intent.setClass(this, MtkFilesGridActivity.class);
				intent.putExtra(INTENT_NAME_PATH, getListCurrentPath());
				intent.putExtra(INTENT_NAME_SELECTION,getListSelectedItemPosition());
				intent.putStringArrayListExtra(INTENT_NAME_COPYED_FILES, mCopyedFiles);
				//add for ThumbnailSize bug
				intent.putExtra("mThumbnailSize", mThumbnailSize);
				
				MeidaMainActivity.mSelection=getListSelectedItemPosition();
			}
			startActivity(intent);
			finish();

			break;
		case KeyMap.KEYCODE_MTKIR_ANGLE:
			removeView();
			break;
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void onListItemClick(AbsListView l, View v, int position, long id) {
		if (mMode == MODE_CONTENT_TYPES_LIST) {
			
			SaveValue pref = SaveValue.getInstance(this);
			boolean smbAvailable = pref.readValue(MenuConfigManager.MY_NET_PLACE) == 0 ? false
					: true;
			MtkLog.d(TAG, "Samba Available : " + smbAvailable);
			boolean dlnaAvailable = pref.readValue(MenuConfigManager.DLNA) == 0 ? false
					: true;
			MtkLog.d(TAG, "Dlna Available : " + dlnaAvailable);		
			
			MultiFilesManager.getInstance(this).getLocalDevices();
			int deviceNum = MultiFilesManager.getInstance(this).getAllDevicesNum();			
			if (deviceNum == MultiFilesManager.NO_DEVICES && !dlnaAvailable && !smbAvailable){
				return;
			}
			
			mMode = MODE_FILES_LIST;

			mFilesManager.pushOpenedHistory(position);
			openDir(getListRootPath());

			if (getListContentType() == FilesManager.CONTENT_VIDEO) {
				LogicManager.getInstance(this).finishAudioService();
				stopMusicView();
			}

			return;
		}

		if (position == 0) {
			if (super.mMode == MODE_NORMAL) {
				MultiFilesManager multiFileManager = MultiFilesManager
						.getInstance(this);
				String path = multiFileManager.getDeviceMountPoint();
				if (multiFileManager.getAllDevicesNum() == MultiFilesManager.ONE_DEVICES) {
					if (getListCurrentPath().equals(path)) {
						onReachRoot(getListContentType());
						return;
					}
				}
				openDir(getListParentPath(), mFilesManager.popOpenedHistory());
			} else if (super.mMode == MODE_RECURSIVE) {
				onReachRoot(mFilesManager.popOpenedHistoryRoot());
			}
		} else {
			if (getListContentType() == FilesManager.CONTENT_VIDEO&&vVideoView!=null) {
				vVideoView.stop();
			}
			super.onListItemClick(l, v, position, id);
		}
	}

	/**
	 * Count page size
	 */
	protected void computePageSize() {
		if (mPageSize == 0) {
			MtkLog.d(TAG, "H : " + vList.getHeight());
			MtkLog.d(TAG, "W : " + vList.getWidth());

			int h = vList.getHeight();
			int size = mListItemHeght;
			MtkLog.d(TAG, "Size : " + size);

			int row = h / size;
			mPageSize = row;

			MtkLog.d(TAG, "PageSize : " + mPageSize);
		}

		if (mPageSize > 0) {
			int filesCount = getListItemsCount();
			mPageCount = filesCount / mPageSize;
			if (filesCount % mPageSize > 0 || filesCount == 0) {
				mPageCount += 1;
			}

			MtkLog.d(TAG, "ItemCount : " + filesCount);
			MtkLog.d(TAG, "PageCount : " + mPageCount);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void onReachRoot(int selection) {
		 //super.onReachRoot(selection);

		mMode = MODE_CONTENT_TYPES_LIST;

		Intent intent = new Intent(this, this.getClass());
		intent.putExtra(INTENT_NAME_PATH, getListParentPath());
		intent.putExtra(INTENT_NAME_SELECTION, selection);
		startActivity(intent);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void onListItemSelected(AbsListView l, View v, int position,
			long id) {
		if (mMode == MODE_CONTENT_TYPES_LIST) {
			setListContentType(position);
			setupHeader();
			vDetailInfo.setText("");
		} else {
			if (position == 0) {
				vTopPageSize.setText("");
				vThumbnail.setImageBitmap(null);
				vDetailInfo.setText("");
				if(vVideoView!=null){
				vVideoView.stop();
				}
			} else if (position > 0) {
				FileAdapter file = getListItem(position);
				int contentType = getListContentType();
				if (file != null && file.isFile()) {
					if (contentType == FilesManager.CONTENT_TEXT) {
						vTextShow.setText(LogicManager.getInstance(this)
								.getPreviewBuf(file.getAbsolutePath()));
					} else if (contentType == FilesManager.CONTENT_VIDEO) {
						// TODO paly video

						if (myVideoPlayDelay != null&&vVideoView!=null) {
							vVideoView.removeCallbacks(myVideoPlayDelay);
						}
						myVideoPlayDelay = new VideoPlayDelay(file);
						if(vVideoView!=null){
						vVideoView.postDelayed(myVideoPlayDelay, 1000);
						}
					} else {
						vThumbnail.setImageBitmap(null);
						mLoader.clearQueue();
						mLoader.addWork(new LoadCorverPic(file));
					
					}
					
					
					String info = mInforCache.get(file.getAbsolutePath());
					if (null != info) {
						vDetailInfo.setText(info);
						return;
					} else {
						vDetailInfo.setText("");
					}
					String suffix = file.getSuffix();
					if (!FileSuffixConst.DLNA_FILE_NAME_EXT_PCM
							.equalsIgnoreCase(suffix)) {
						mInfoLoader.clearQueue();
						mInfoLoader.addWorkTop(new LoadInfo(file, vDetailInfo));
					}else
					{
						vDetailInfo.setText(file.getName()+file.getSuffix());
					}
				} else {
					if (contentType == FilesManager.CONTENT_TEXT) {
						vTextShow.setText("");
					} else if (contentType == FilesManager.CONTENT_VIDEO&&vVideoView!=null) {
						vVideoView.stop();
					} else {
						vThumbnail.setImageBitmap(null);
					}
					vDetailInfo.setText("");
				}
			}
		}

		if (mPageCount == 0 || position == 0) {
			vTopPageSize.setText("");
		} else {
			vTopPageSize.setText(getListSelectedItemPosition() + 1 + "/"
					+ getListItemsCount());
		}
	}

	private class LoadCorverPic implements LoadWork<Bitmap> {

		private FileAdapter mFile;

		public LoadCorverPic(FileAdapter file) {
			mFile = file;
		}

		public Bitmap load() {

			Bitmap thumbnail = null;
			try {
				thumbnail = mFile.getThumbnail(vThumbnail.getWidth(),
						vThumbnail.getHeight());
			} catch (OutOfMemoryError e) {
				thumbnail = ((BitmapDrawable) mResources
						.getDrawable(R.drawable.mmp_thumbnail_loading_failed_big))
						.getBitmap();
			}

			return thumbnail;
		}

		public void loaded(final Bitmap result) {
			if (null != result) {
				vThumbnail.post(new Runnable() {

					public void run() {
						vThumbnail.setImageBitmap(result);
					}
				});
			}

		}

	}
	private VideoPlayDelay myVideoPlayDelay;

	private class VideoPlayDelay implements Runnable {
		private FileAdapter file;

		public VideoPlayDelay(FileAdapter file) {
			this.file = file;
		}

		public void run() {
			if (file instanceof LocalFileAdapter){
				playSelectionItem(file.getAbsolutePath());
			} else{
				playSelectionItem(file.getName());
			}
			
			myVideoPlayDelay=null;
		}
	}

	private void playSelectionItem(String path) {
		if (isValid()&&vVideoView!=null) {
			if (!vVideoView.isVideoPlaybackInit()) {
				vVideoView.init();
			}
			vVideoView.stop();
			vVideoView.setPreviewMode(true);
			vVideoView.play(path);
				//Canvas canvas = vVideoView.getHolder().lockCanvas(null);
				//vVideoView.getHolder().unlockCanvasAndPost(canvas);
				vVideoView.invalidate();
		}
	}

	@Override
	public FileAdapter getListItem(int position) {
		return super.getListItem(position - 1);
	}

	@Override
	public FileAdapter getListSelectedItem() {
		return getListItem(super.getListSelectedItemPosition());
	}

	@Override
	public int getListSelectedItemPosition() {
		return super.getListSelectedItemPosition() - 1;
	}

	@Override
	protected void refreshListView(List<FileAdapter> files) {
		super.refreshListView(files);

		if (mMode == MODE_FILES_LIST) {
			vTopPageSize.setText(mCurrentPage + "/" + mPageCount);
		}
	}

	private class MtkFilesListAdapter extends FilesAdapter {
		private Drawable mFolder;
		private Drawable mPlaying;
		private String[] mContentTypes;

		public MtkFilesListAdapter(Context context) {
			super(context);

			prepareDefaultThumbnails();

			mContentTypes = mContentTypeNames;
		}

		private void prepareDefaultThumbnails() {
			Resources resources = getResources();

			mFolder = resources
					.getDrawable(R.drawable.mmp_listmode_icon_folder);
			mPlaying = resources
					.getDrawable(R.drawable.mmp_toolbar_typeicon_paly);
		}

		/**
		 * {@inheritDoc}
		 */
		public int getCount() {
			if (mMode == MODE_CONTENT_TYPES_LIST) {
				return mContentTypes.length;
			}

			return super.getCount() + 1;
		}

		/**
		 * {@inheritDoc}
		 */
		public FileAdapter getItem(int position) {
			if (mMode == MODE_CONTENT_TYPES_LIST) {
				return null;
			}

			if (position == 0) {
				return null;
			}

			return super.getItem(position - 1);
		}

		/**
		 * {@inheritDoc}
		 */
		public long getItemId(int position) {
			if (mMode == MODE_CONTENT_TYPES_LIST) {
				return position;
			}

			if (position == 0) {
				return 0;
			}

			return super.getItemId(position - 1);
		}

		/**
		 * {@inheritDoc}
		 */
		protected int getItemLayout() {
			return R.layout.mmp_listmode_item;
		}

		/**
		 * {@inheritDoc}
		 */
		protected void initView(View v) {
			ViewHolder holder = new ViewHolder();

			holder.folderIcon = (ImageView) v
					.findViewById(R.id.mmp_listmode_icon);
			holder.folderName = (TextView) v
					.findViewById(R.id.mmp_listmode_foldername);

			holder.copyIcon = (ImageView) v
					.findViewById(R.id.mmp_listmode_copyicon);

			v.setLayoutParams(new ListView.LayoutParams(
					LayoutParams.MATCH_PARENT, mListItemHeght));
			v.setTag(holder);
		}

		/**
		 * {@inheritDoc}
		 */
		protected void bindView(View v, FileAdapter data, int position) {
			ViewHolder holder = (ViewHolder) v.getTag();
			v.setBackgroundDrawable(null);

			if (mMode == MODE_CONTENT_TYPES_LIST) {
				holder.folderIcon.setVisibility(View.VISIBLE);
				holder.folderName.setText(mContentTypes[position]);
				holder.copyIcon.setImageDrawable(null);
				return;
			}

			if (position == 0) {
				holder.folderIcon.setVisibility(View.VISIBLE);
				holder.folderName.setText("[..]");
				holder.copyIcon.setImageDrawable(null);
			} else {
				if (data.isDirectory()) {
					holder.folderIcon.setVisibility(View.VISIBLE);
					holder.folderIcon.setImageDrawable(mFolder);
					holder.folderName.setText(data.getName());
					holder.copyIcon.setImageDrawable(null);
				} else {
					if (position == mPlayPosition) {
						holder.folderIcon.setVisibility(View.VISIBLE);
						holder.folderIcon.setImageDrawable(mPlaying);
					} else {
						holder.folderIcon.setVisibility(View.INVISIBLE);
					}

					String path = data.getAbsolutePath();
					if (mCopyedFiles.size() > 0 && mCopyedFiles.contains(path)) {
						holder.copyIcon
								.setImageResource(R.drawable.mmp_listmode_icon_copy);
					} else if (mSelectedFiles.size() > 0 && mSelectedFiles.contains(path)) {
						holder.copyIcon
								.setImageResource(R.drawable.mmp_listmode_icon_select);
					} else {
						holder.copyIcon.setImageDrawable(null);
					}
					holder.folderName
							.setText(data.getName() + data.getSuffix());
				}

			}
		}

		private class ViewHolder {
			ImageView folderIcon;
			TextView folderName;
			ImageView copyIcon;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void onListEmpty() {

		MultiFilesManager multiFileManager = MultiFilesManager
				.getInstance(this);
		if (multiFileManager.getAllDevicesNum() == MultiFilesManager.NO_DEVICES) {
			super.onListEmpty();
			vList.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void onListNotEmpty() {
		super.onListNotEmpty();
		vList.setVisibility(View.VISIBLE);
	}

	/**
	 * {@inheritDoc}
	 */
	public void onBackPressed() {
		removeView();
		mFilesManager.deleteObserver(this);
		mFilesManager.destroy();
		DestroyApp destroyApp = (DestroyApp) getApplicationContext();
		destroyApp.finishAll();
	}
	public static void removeView(){
		if(vVideoView!=null){
			vVideoView.setVisibility(View.GONE);
			vVideoView.onRelease();
			vVideoView = null;
		}
	}
}
