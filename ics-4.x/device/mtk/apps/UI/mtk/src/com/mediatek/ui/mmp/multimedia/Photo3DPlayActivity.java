package com.mediatek.ui.mmp.multimedia;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.mediatek.mmpcm.mmcimpl.Const;
import com.mediatek.mmpcm.photoimpl.ConstPhoto;
import com.mediatek.mmpcm.threedimen.photo.IThrdPhotoEventListener;
import com.mediatek.mmpcm.threedimen.photoimpl.MPlayback;
import com.mediatek.mmpcm.threedimen.photoimpl.PhotoManager;
import com.mediatek.ui.R;
import com.mediatek.ui.menu.MenuMain;
import com.mediatek.ui.menu.util.MenuConfigManager;
import com.mediatek.ui.menu.util.MenuDataContainer;
import com.mediatek.ui.mmp.commonview.ControlView;
import com.mediatek.ui.mmp.commonview.MenuListView;
import com.mediatek.ui.mmp.commonview.ControlView.ControlPlayState;
import com.mediatek.ui.mmp.model.MultiFilesManager;
import com.mediatek.ui.mmp.util.GetDataImp;
import com.mediatek.ui.mmp.util.LogicManager;
import com.mediatek.ui.mmp.util.MultiMediaConstant;
import com.mediatek.ui.nav.CaptureLogoActivity;
import com.mediatek.ui.util.KeyMap;
import com.mediatek.ui.util.MtkLog;

public class Photo3DPlayActivity extends MediaPlayActivity {

	private static final String TAG = "Photo3DPlayActivit";

	private static final int MESSAGE_PLAY = 0;

	private static final int MESSAGE_POPHIDE = 1;

	private static final int MESSAGE_PHOTOMODE = 2;

	private static final int MESSAGE_HIDDLE_MESSAGE = 3;

	private static final int MESSAGE_FINISH = 4;

	private static final int MESSAGE_PLAY_NEXT = 5;

	private static final int MESSAGE_FILE_NOT_SURPORT = 8;
	
	private static final int MESSAGE_HIDE_MENUDIALOG = 9;
	
	private static final int MESSAGE_TIPSDIALOG_DIMISS = 6;

	private static final int MESSAGE_POPSHOWDEL = 10000;

	public static final int DELAYED_LONG = 8000;

	public static final int DELAYED_MIDDLE = 5000;

	public static final int DELAYED_SHORT = 3000;

	public static int mDelayedTime = DELAYED_SHORT/*DELAYED_MIDDLE*/;

	private LinearLayout vLayout;

	private MenuListView menuDialog;

	private Resources mResources;

	private int playMode;

	private int isRepeatMode = 0;

	private SharedPreferences mPreferences;

	public static final String PHOTO_FRAME_PATH = "photoframe";

	public static final String PHOTO_FRAME_KEY = "photo";

	private int mImageSource = 0;

	//private boolean isZoom = false;

	private boolean isStop = false;

	private PhotoManager mPhotoManager;

	private MPlayback mPlayback;

	private boolean mLastPlayState = true;

	private MenuDataContainer mMenuDataContainer;
		
	//private static String thrdMode;
	
	private int thrdModeValue =1;
/*	private boolean is3DPhotoMpo=false;*/

	private void autoPlay() {
		mLogicManager.playThrdNextPhoto();
		setControlView();
		/*if (isZoom) {			
			mControlView.setPhotoZoom(mResources
					.getString(R.string.mmp_menu_1x));
			isZoom = false;
		}*/
//		if (!isStop && mControlView.isPalying()) {
//			mHandler.removeMessages(MESSAGE_PLAY);
//			mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY, mDelayedTime);
//			System.out.println("-shuming--play-path---"+"--"+new Exception().getStackTrace()[0]);
//		}
		
	}

	private OnItemClickListener mListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			isRepeatMode = parent.getChildCount();
			TextView tvTextView = (TextView) view
					.findViewById(R.id.mmp_menulist_tv);
			String content = tvTextView.getText().toString();
			controlState(content);
		}
	};

	private class SleepDialog extends Dialog {

		private TextView mSleepTime;

		private SleepDialog(Context context, int theme) {
			super(context, theme);
		}

		public SleepDialog(Context context) {
			this(context, R.style.videodialog);
		}

		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.mmp_framephoto_sleep);
			setDialogPosition();

			// mSleepTime =findViewById(R.id.)
		}

		private void setDialogPosition() {
			WindowManager m = getWindow().getWindowManager();
			Display display = m.getDefaultDisplay();
			Window window = getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.x = -(int) (display.getRawWidth() * 0.4);
			lp.y = (int) (display.getRawHeight() * 0.25);
			window.setAttributes(lp);

		}

		public void setSleepTime() {

		}

	}

	private ControlPlayState mControlImp = new ControlPlayState() {

		public void play() {
			mLastPlayState = true;
			if(mHandler.hasMessages(MESSAGE_PLAY))
			{
				mHandler.removeMessages(MESSAGE_PLAY);
			}
			mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY, mDelayedTime);
		}

		public void pause() {
			mLastPlayState = false;
			mHandler.removeMessages(MESSAGE_PLAY);
			mHandler.removeMessages(MESSAGE_PLAY_NEXT);
		}

	};

	/**
	 * {@inheritDoc}
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mmp_mediaplay);
		mPreferences = getSharedPreferences(PHOTO_FRAME_PATH, MODE_PRIVATE);
		mMenuDataContainer = MenuDataContainer.getInstance(this);
		findView();
		getIntentData();

		showPopUpWindow(vLayout);
		mResources = getResources();
		mControlView.setRepeatVisibility(Const.FILTER_IMAGE);//wait confirm
		initShowPhoto();
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

			case MESSAGE_PLAY:
				dismissNotSupprot();
				if(isSetPicture){
					MenuMain.getInstance().finish();
					isSetPicture = false;
				}
				removeFeatureMessage();
				autoPlay();
				break;
			case MESSAGE_POPHIDE:
				MtkLog.d(TAG, "MESSAGE_POPHIDE:" + msg.what);
				if (null != menuDialog && menuDialog.isShowing()) {
					break;
				}
				hideController();
				break;
			case MESSAGE_PHOTOMODE:
				break;
			case MESSAGE_HIDDLE_MESSAGE: {
				dismissNotSupprot();
				break;
			}
			case MESSAGE_FINISH:
				if (menuDialog != null && menuDialog.isShowing()) {
					menuDialog.dismiss();
				}
				removeMessage();
				mLogicManager.closeThrdPhoto();
				finish();
				break;
			case MESSAGE_PLAY_NEXT:
				// Added by Dan for fix bug DTV00384892
				if (menuDialog != null && menuDialog.isShowing()) {
					menuDialog.dismiss();
				}

				// mHandler.removeMessages(MESSAGE_PLAY_NEXT);
				// mPlayback.close();
				vLayout.removeCallbacks(mtPlayFinishDely);
				reSetController();
				mHandler.removeMessages(MESSAGE_TIPSDIALOG_DIMISS);
				sendEmptyMessage(MESSAGE_TIPSDIALOG_DIMISS);
				if(isSetPicture){
					MenuMain.getInstance().finish();
					isSetPicture = false;
				}
				removeFeatureMessage();
				mLogicManager.playThrdNextPhoto();
				setControlView();
				//mHandler.removeMessages(MESSAGE_PLAY);
				//mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY, mDelayedTime);

				break;
			case MESSAGE_TIPSDIALOG_DIMISS:
				dismissNotSupprot();
				break;
			case MESSAGE_HIDE_MENUDIALOG:
				if (menuDialog != null && menuDialog.isShowing()) {
					menuDialog.dismiss();
				}
				break;
			case MESSAGE_FILE_NOT_SURPORT:
				if (menuDialog != null && menuDialog.isShowing()) {
					menuDialog.dismiss();
				}
				onNotSuppsort(Photo3DPlayActivity.this.getResources()
						.getString(R.string.mmp_photo_type_notsupport));
				reSetController();
				setControlView();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * find view
	 */
	private void findView() {
		vLayout = (LinearLayout) findViewById(R.id.mmp_mediaplay);
		getPopView(R.layout.mmp_popupphoto, MultiMediaConstant.THRD_PHOTO,
				mControlImp);

	}

	/**
	 * {@inheritDoc}
	 */
	protected void onResume() {
		super.onResume();
		isStop = false;
        isSetPicture = false;
		if (isBackFromCapture) {
			if (mLastPlayState) {
				mControlView.play();
			}
			isBackFromCapture = false;
		}
	}

	private void onRepeat() {

		if (null == mControlView) {
			return;
		}
		int model = mLogicManager.getRepeatModel(Const.FILTER_IMAGE);

		switch (model) {
		case Const.REPEAT_ALL: {
			mControlView.setRepeatSingle();
			mLogicManager.setRepeatMode(Const.FILTER_IMAGE, Const.REPEAT_ONE);
			break;
		}
		case Const.REPEAT_ONE: {
			mControlView.setRepeatNone();
			mLogicManager.setRepeatMode(Const.FILTER_IMAGE, Const.REPEAT_NONE);
			break;
		}
		case Const.REPEAT_NONE: {
			mControlView.setRepeatAll();
			mLogicManager.setRepeatMode(Const.FILTER_IMAGE, Const.REPEAT_ALL);
			break;
		}
		default:
			break;
		}

	}

	private void getIntentData() {
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			playMode = bundle.getInt("PlayMode");
		}

		mImageSource = MultiFilesManager.getInstance(this)
				.getCurrentSourceType();

		switch (mImageSource) {
		case MultiFilesManager.SOURCE_LOCAL:
			mImageSource = ConstPhoto.LOCAL;
			break;
		case MultiFilesManager.SOURCE_SMB:
			mImageSource = ConstPhoto.SAMBA;
			break;
		case MultiFilesManager.SOURCE_DLNA:
			mImageSource = ConstPhoto.DLNA;
			break;
		default:
			break;
		}
	}

	private IThrdPhotoEventListener thrdPhotoEventlistener = new IThrdPhotoEventListener() {

		public void onPlayFailed() {
			MtkLog.d(TAG, "play failed--");
			isNotSupport = true;
/*			vLayout.post(new Runnable() {
				public void run() {
					System.out.println("-shuming---iszoom--"+new Exception().getStackTrace()[0]);
					onNotSuppsort(Photo3DPlayActivity.this.getResources()
							.getString(R.string.mmp_photo_type_notsupport));
					reSetController();
					setControlView();
				}
			});*/
			/*
			 * reSetController(); setControlView();
			 */
			if(mHandler.hasMessages(MESSAGE_FILE_NOT_SURPORT)){
				mHandler.removeMessages(MESSAGE_FILE_NOT_SURPORT);
			}
			mHandler.sendEmptyMessage(MESSAGE_FILE_NOT_SURPORT);
			if(mControlView.isPalying()){
			mHandler.removeMessages(MESSAGE_PLAY_NEXT);
			mHandler.removeMessages(MESSAGE_PLAY);
			mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY_NEXT, mDelayedTime);/* 5000 */
			}
			mPlayback.close();
		}

		public void onCompleted() {
			MtkLog.d(TAG, "onCompleted--");
			//vLayout.postDelayed(mtPlayFinishDely, DELAYED_SHORT);
			vLayout.post(mtPlayFinishDely);
		}

		public void onCloseFailed() {
			MtkLog.d(TAG, "close failed--");
		}
//		@Override
		public void playDone() {
			// TODO Auto-generated method stub
			if (!isStop  && mControlView.isPalying() ) {
				mHandler.removeMessages(MESSAGE_PLAY);
				mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY, mDelayedTime);
				System.out.println("-shuming--play-path---"+"--"+new Exception().getStackTrace()[0]);
			}
		}

		public void onOpenFailed() {
			MtkLog.d(TAG, "open failed--");
			isNotSupport = true;

/*			vLayout.post(new Runnable() {
				public void run() {
					System.out.println("-shuming---iszoom--"+new Exception().getStackTrace()[0]);
					onNotSuppsort(Photo3DPlayActivity.this.getResources()
							.getString(R.string.mmp_photo_type_notsupport));

					reSetController();
					setControlView();
				}
			});*/


			if(mHandler.hasMessages(MESSAGE_FILE_NOT_SURPORT)){
				mHandler.removeMessages(MESSAGE_FILE_NOT_SURPORT);
			}
			mHandler.sendEmptyMessage(MESSAGE_FILE_NOT_SURPORT);
			 
			// reSetController();
			if(mControlView.isPalying()){
			mHandler.removeMessages(MESSAGE_PLAY_NEXT);
			mHandler.removeMessages(MESSAGE_PLAY);
			mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY_NEXT, mDelayedTime);
			
			}
			mPlayback.close();
		}
		public void decodeSuccess() {
			// TODO Auto-generated method stub
			isNotSupport = false;
			mHandler.sendEmptyMessage(MESSAGE_HIDE_MENUDIALOG);
			
		}
	};
	
	/*private ThrPhotoPlayDelay mPhotoPlayDely = new ThrPhotoPlayDelay();
	private class ThrPhotoPlayDelay implements Runnable {
		public void run() {
			MtkLog.d(TAG, "mPhotoPlayDely play next file....1");
			if (mLastPlayState){
				MtkLog.d(TAG, "mPhotoPlayDely play next file....2");
				mHandler.sendEmptyMessage(MESSAGE_PLAY_NEXT);
			}
		}
	}*/
	

	private PlayFinishDelay mtPlayFinishDely = new PlayFinishDelay();

	private class PlayFinishDelay implements Runnable {
		public void run() {
			//if (mLastPlayState) {
				mHandler.sendEmptyMessage(MESSAGE_FINISH);
			//}
		}
	}

	/**
	 * Initialize photo play
	 */
	private void initShowPhoto() {
		mLogicManager = LogicManager.getInstance(this);
		mPhotoManager = PhotoManager.getInstance(this);
		mPlayback = mPhotoManager.getPlayback();
		mLogicManager.setThrdPhotoCompelet(thrdPhotoEventlistener);
		mLogicManager.playThrdPhoto();
		setControlView();		

//		if (!isStop /* && mControlView.isPalying() */) {
//			mHandler.removeMessages(MESSAGE_PLAY);
//			mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY, mDelayedTime);
//			System.out.println("-shuming--play-path---"+"--"+new Exception().getStackTrace()[0]);
//		}

	}

	/**
	 * Set control bar info
	 */
	private void setControlView() {
		
		if (mControlView != null) {
			// TODO remove
			//if(isNotSupport){
				mControlView.setZoomEmpty();
			//}
			mControlView.setRepeat(Const.FILTER_IMAGE);
			mControlView.setFileName(mLogicManager
					.getCurrentFileName(Const.FILTER_IMAGE));
			mControlView.setFilePosition(mLogicManager.getThrdPhotoPageSize());
		}
		if (null != mInfo && mInfo.isShowing()) {
			mInfo.setPhotoView();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		MtkLog.d(TAG, "onKeyDown keyCode:" + keyCode);
		switch (keyCode) {
		case KeyMap.KEYCODE_MENU: {
			reSetController();
			showDialog();
			return true;
		}
		case KeyMap.KEYCODE_DPAD_CENTER:
		case KeyMap.KEYCODE_MTKIR_PLAYPAUSE:{
			if(isValid()){
				return super.onKeyDown(keyCode, event);
			}else{
				return true;
			}
		}
		case KeyMap.KEYCODE_MTKIR_CHDN: {
			if (isValid()) {
				mHandler.removeMessages(MESSAGE_TIPSDIALOG_DIMISS);
				mHandler.removeMessages(MESSAGE_PLAY);
				mHandler.removeMessages(MESSAGE_PLAY_NEXT);
				mHandler.sendEmptyMessage(MESSAGE_TIPSDIALOG_DIMISS);
				vLayout.removeCallbacks(mtPlayFinishDely);
				reSetController();
				removeFeatureMessage();
				mLogicManager.playThrdPrePhoto();
				setControlView();
				if(mControlView.isPalying()){
					mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY, mDelayedTime);
				}
				
			}
			return true;
		}
		case KeyMap.KEYCODE_MTKIR_CHUP: {
			if (isValid()) {
				mHandler.removeMessages(MESSAGE_TIPSDIALOG_DIMISS);
				mHandler.sendEmptyMessage(MESSAGE_TIPSDIALOG_DIMISS);
				mHandler.removeMessages(MESSAGE_PLAY);
				mHandler.removeMessages(MESSAGE_PLAY_NEXT);
				vLayout.removeCallbacks(mtPlayFinishDely);
				reSetController();
				removeFeatureMessage();
				mLogicManager.playThrdNextPhoto();
				setControlView();
				if(mControlView.isPalying()){
					mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY, mDelayedTime);
				}
			}
			return true;
		}
		case KeyMap.KEYCODE_MTKIR_REPEAT: {
			reSetController();
			onRepeat();
			break;
		}
		case KeyMap.KEYCODE_MTKIR_RECORD: {
			
			if (isNotSupport){
				break;
			} else if(MenuConfigManager.getInstance(this).getDefault(MenuConfigManager.VIDEO_3D_MODE) != 0){
				featureNotWork(getString(R.string.mmp_featue_notsupport));
			}else{
				reSetController(); 
				if(mControlView.isPalying()){
					//mControlView.pause();
					mControlView.onCapture();
					mControlView.setPlayIcon(View.INVISIBLE);
					mLastPlayState = true;
				}
				Intent intent = new Intent(this, CaptureLogoActivity.class);
				intent.putExtra(CaptureLogoActivity.FROM_MMP,
						CaptureLogoActivity.MMP_VIDEO);
				startActivity(intent);
				isBackFromCapture = true;
				return true;
			}
		}
		case KeyMap.KEYCODE_MTKIR_YELLOW: {
			return true;
		}
		case KeyMap.KEYCODE_MTKIR_GREEN: {
			reSetController();
			//add by shuming for fix CR DTV00410753
//			switchDuration();
			if (mControlView.isPalying()) {				
				switchDuration();
			} 
			return true;
		}
		case KeyMap.KEYCODE_VOLUME_DOWN:
		case KeyMap.KEYCODE_VOLUME_UP:
		case KeyMap.KEYCODE_MTKIR_MUTE: {
			if (null != mLogicManager.getAudioPlaybackService()) {
				currentVolume = mLogicManager.getVolume();
				maxVolume = mLogicManager.getMaxVolume();
				break;
			} else {
				return true;
			}
		}
		case KeyMap.KEYCODE_MTKIR_PREVIOUS:
		case KeyMap.KEYCODE_MTKIR_NEXT: {
			return true;
		}
		case KeyMap.KEYCODE_BACK:
			removeMessage();
			mLogicManager.closeThrdPhoto();
			finish();
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Switch photo play duration
	 */
	private void switchDuration() {
		if (mDelayedTime == DELAYED_SHORT) {
			mDelayedTime = DELAYED_MIDDLE;
			mControlView.setPhotoTimeType(getString(R.string.mmp_menu_medium));
		} else if (mDelayedTime == DELAYED_MIDDLE) {
			mDelayedTime = DELAYED_LONG;
			mControlView.setPhotoTimeType(getString(R.string.mmp_menu_long));
		} else {
			mDelayedTime = DELAYED_SHORT;
			mControlView.setPhotoTimeType(getString(R.string.mmp_menu_short));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void hideControllerDelay() {
		mHandler.removeMessages(MESSAGE_POPHIDE);
		mHandler.sendEmptyMessageDelayed(MESSAGE_POPHIDE, MESSAGE_POPSHOWDEL);
	}

	/**
	 * Menu right handler
	 */
	private MenuListView.MenuDismissCallBack mCallBack = new MenuListView.MenuDismissCallBack() {

		public void onDismiss() {
			hideController();
		}

		public void sendMessage() {
		}

		public void noDismissPannel() {

		};
	};

	/**
	 * {@inheritDoc}
	 */
	protected void onStop() {
		if (menuDialog != null && menuDialog.isShowing()) {
			menuDialog.dismiss();
		}
		if (!isSetPicture){
			removeMessage();
			isStop = true;
		}
		super.onStop();

	};

	/**
	 * Remove handler message
	 */
	private void removeMessage() {
		mHandler.removeMessages(MESSAGE_PHOTOMODE);
		mHandler.removeMessages(MESSAGE_PLAY);
		mHandler.removeMessages(MESSAGE_POPHIDE);
		mHandler.removeMessages(MESSAGE_PLAY_NEXT);
	}

	protected void onPause() {
		super.onPause();
	
	}

	/**
	 * {@inheritDoc}
	 */
	protected void onDestroy() {
		// mPlayback.close();
		mLogicManager.closeThrdPhoto();
		super.onDestroy();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onBackPressed() {

	}

	/**
	 * Menu item click callback
	 * 
	 * @param content
	 *            the click item content value
	 */
	protected void controlState(String content) {

		if (content.equals(mResources
				.getString(R.string.mmp_frame_photo_sleeptime))) {
			if (null != menuDialog && menuDialog.isShowing()) {
				menuDialog.dismiss();
			}
			SleepDialog dialog = new SleepDialog(this);
			dialog.show();
			dialog.setSleepTime();
			return;
		}

		if (content.equals(mResources.getString(R.string.mmp_menu_pause))) {
			showController();
			hideControllerDelay();
			mControlView.setMediaPlayState();
			MtkLog.d(TAG, "content:-----" + content);
			menuDialog.setList(0, mResources.getString(R.string.mmp_menu_play),
					false, 3, mResources.getString(R.string.mmp_menu_rotate),
					false, 4, mResources.getString(R.string.mmp_menu_zoom),
					true);
			menuDialog.setItemEnabled(3, false);			
		} else if (content.equals(mResources.getString(R.string.mmp_menu_play))) {
			showController();
			hideControllerDelay();
			mControlView.setMediaPlayState();
			menuDialog.setList(0,
					mResources.getString(R.string.mmp_menu_pause), false, 3,
					mResources.getString(R.string.mmp_menu_duration), true, 4,
					mResources.getString(R.string.mmp_menu_effect), true);
			menuDialog.setItemEnabled(3, true);
		}

		else if (content.equals(mResources.getString(R.string.mmp_menu_none))
				&& (isRepeatMode == 3)) {
			mControlView.setRepeatNone();
			mLogicManager.setRepeatMode(Const.FILTER_IMAGE, Const.REPEAT_NONE);
		} else if (content.equals(mResources
				.getString(R.string.mmp_menu_repeatone))) {
			mControlView.setRepeatSingle();
			mLogicManager.setRepeatMode(Const.FILTER_IMAGE, Const.REPEAT_ONE);

		} else if (content.equals(mResources
				.getString(R.string.mmp_menu_repeatall))) {
			mControlView.setRepeatAll();
			mLogicManager.setRepeatMode(Const.FILTER_IMAGE, Const.REPEAT_ALL);
		} else if (content.equals(mResources
				.getString(R.string.mmp_menu_shuffleon))) {
			mControlView.setShuffleVisble(View.VISIBLE);
			mLogicManager.setShuffle(Const.FILTER_IMAGE, Const.SHUFFLE_ON);
		} else if (content.equals(mResources
				.getString(R.string.mmp_menu_shuffleoff))) {
			mControlView.setShuffleVisble(View.INVISIBLE);
			mLogicManager.setShuffle(Const.FILTER_IMAGE, Const.SHUFFLE_OFF);
		}

		else if (content.equals(mResources.getString(R.string.mmp_menu_short))) {
			mDelayedTime = DELAYED_SHORT;
			mControlView.setPhotoTimeType(content);
		} else if (content.equals(mResources
				.getString(R.string.mmp_menu_medium))) {
			mDelayedTime = DELAYED_MIDDLE;
			mControlView.setPhotoTimeType(content);
		} else if (content.equals(mResources.getString(R.string.mmp_menu_long))) {
			mDelayedTime = DELAYED_LONG;
			mControlView.setPhotoTimeType(content);
		}

		else if (content.equals(mResources.getString(R.string.mmp_menu_none))) {

		} else if (content.equals(mResources
				.getString(R.string.mmp_menu_showinfo))) {
			menuDialog.dismiss();
			showinfoview(MultiMediaConstant.PHOTO);
		} else if (content
				.equals(mResources.getString(R.string.mmp_menu_frame))) {
			/*
			 * String path = mLogicManager.getCurrentPath(Const.FILTER_IMAGE);
			 * Editor editor = mPreferences.edit();
			 * editor.putString(PHOTO_FRAME_KEY, path); editor.commit();
			 */
			mPhotoFramePath = mLogicManager.getCurrentPath(Const.FILTER_IMAGE);
		} else if (content.equals(mResources
				.getString(R.string.mmp_menu_pic_setting))) {
			if (null != menuDialog && menuDialog.isShowing()) {
				menuDialog.dismiss();
			}
			if (mControlView != null && isControlBarShow) {
				hideController();
			}
			isSetPicture = true;
			Intent intent = new Intent(this, MenuMain.class);
			intent.putExtra("fromwhere", 1);
			startActivity(intent);
		}
	}
			
	/**
	 * Get photo play duration
	 * 
	 * @return int duration
	 */
	public static int getDelayedTime() {
		return mDelayedTime;
	}

	/**
	 * Show menu dialog
	 */
	private void showDialog() {

		mHandler.removeMessages(MESSAGE_POPHIDE);
		menuDialog = new MenuListView(Photo3DPlayActivity.this, GetDataImp
				.getInstance().getComMenu(Photo3DPlayActivity.this,
						R.array.mmp_menu_3d_photoplaylist,
						R.array.mmp_menu_3d_photoplaylist_enable,
						R.array.mmp_menu_3d_photoplaylist_hasnext), mListener,
				mCallBack);
		menuDialog.setItemEnabled(7, !isNotSupport);

		if (null != mControlView) {

			if (mControlView.isPalying()) {
				menuDialog.setList(0,
						mResources.getString(R.string.mmp_menu_pause), false,
						3, mResources.getString(R.string.mmp_menu_duration),
						true, 4,
						mResources.getString(R.string.mmp_menu_effect), true);
				menuDialog.setItemEnabled(3, true);
			} else {
				menuDialog.setList(0,
						mResources.getString(R.string.mmp_menu_play), false, 3,
						mResources.getString(R.string.mmp_menu_rotate), false,
						4, mResources.getString(R.string.mmp_menu_zoom), true);
			}
		}
		menuDialog.show();

	}
}
