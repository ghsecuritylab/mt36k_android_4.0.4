package com.mediatek.ui;

import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mediatek.netcm.NetworkManager;
import com.mediatek.tv.model.CompListener;
import com.mediatek.tv.service.ComponentService;
//lkm
import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.service.InputService;
import com.mediatek.tv.service.InputService.InputServiceListener.InputSignalStatus;
//lkm
import com.mediatek.tvcm.TVContent;
import com.mediatek.tvcm.TVInputManager;
import com.mediatek.tvcm.TVInputManager.InputSourceListener;
import com.mediatek.tvcm.TVOutput;
import com.mediatek.tvcm.TVTeleTextManager;
import com.mediatek.tvcommon.TVCAMManager;
import com.mediatek.tvcommon.TVChannel;
import com.mediatek.tvcommon.TVConfigurer;
import com.mediatek.tvcommon.TVOptionRange;
import com.mediatek.ui.menu.MenuMain;
import com.mediatek.ui.menu.commonview.MessageType;
import com.mediatek.ui.menu.commonview.SleepTimerOff;
import com.mediatek.ui.menu.util.MenuConfigManager;
import com.mediatek.ui.menu.util.NetworkTime;
import com.mediatek.ui.menu.util.OSDLanguage;
import com.mediatek.ui.menu.util.SaveValue;
import com.mediatek.ui.mmp.MeidaMainActivity;
import com.mediatek.ui.mmp.multimedia.MtkFilesListActivity;
import com.mediatek.ui.mmp.util.Util;
import com.mediatek.ui.nav.CaptureLogoActivity;
import com.mediatek.ui.nav.EPGActivity;
import com.mediatek.ui.nav.NavIntegration;
import com.mediatek.ui.nav.NavIntegration.IChannelSelectorListener;
import com.mediatek.ui.nav.commonview.AdjustVolumeView;
import com.mediatek.ui.nav.commonview.BannerView;
import com.mediatek.ui.nav.commonview.InputPwdDialog;
import com.mediatek.ui.nav.commonview.NavSundryShowTextView;
import com.mediatek.ui.nav.commonview.ShowChannelListView;
import com.mediatek.ui.nav.commonview.ShowFavoriteChannelListView;
import com.mediatek.ui.nav.commonview.ShowSourceListView;
import com.mediatek.ui.nav.commonview.SnowTextView;
import com.mediatek.ui.nav.commonview.SnowTextView.ShowType;
import com.mediatek.ui.nav.commonview.ToastInfoView;
import com.mediatek.ui.nav.commonview.ZoomView;
import com.mediatek.ui.nav.util.CICardDelayNotification;
import com.mediatek.ui.nav.util.CheckLockSignalChannelState;
import com.mediatek.ui.nav.util.NavIntegrationZoom;
import com.mediatek.ui.nav.util.NavSundryImplement;
import com.mediatek.ui.nav.util.NewPipLogic;
import com.mediatek.ui.setup.SetupWizardActivity;
import com.mediatek.ui.util.AnimationManager;
import com.mediatek.ui.util.BypassWindowManager;
import com.mediatek.ui.util.GetCurrentTask;
import com.mediatek.ui.util.KeyMap;
import com.mediatek.ui.util.MtkLog;
import com.mediatek.ui.util.POSTManager;
import com.mediatek.ui.util.ScreenConstant;
//liufeng_chk
import com.opengl.jni.CaptureFrameBuffer;
import java.io.File;
import java.io.FileOutputStream;

public class TurnkeyUiMainActivity extends Activity {
	private static final String TAG = "TurnkeyUiMainActivity";
	private static NavIntegration mNavIntegration;
	private static BypassWindowManager mBypassWindowManager;
	public boolean mSetupFlag = false;
	private static ImageView mMuteImageView;
	private WindowManager wm;// manager whole screen, use to do mute
	private WindowManager.LayoutParams wmParams;

	private SaveValue saveV;

	private ToastInfoView mToastInfo;

	// CI
	private TVCAMManager mCAMManager;
	public static boolean mCIStatus = false;
	private boolean mJump = false;
	private TVContent mTV;
	private TVInputManager inpMgr = null;
	private TVConfigurer cfg = null;
	private TVOptionRange<Integer> optionRange = null;
	public static final String CISTATUS_REMOVE = "com.mediatek.ui.intent.CISTATUS_REMOVE";
	public static final String ACTION_NO_SIGNAL = "NO_SIGNAL";
    private CICardDelayNotification mCICardDelayNotification;
	// Channel
	private ShowChannelListView mShowChannelListView;
	//private ShowChannelTypeView mShowChannelTypeView;
	private ShowFavoriteChannelListView mShowFavoriteChannelListView;
	private short mSelectedChannelNum;

	private ShowSourceListView mShowSourceListView;
	private StringBuffer inputChannelNumStrBuffer = new StringBuffer();

	private boolean mNumputChangeChannel = false;
	// volume
	private static LinearLayout mAdjustVolLayout;
	private AdjustVolumeView adjustVolumeView;

	// timer sleep ...
	private static LinearLayout mSundryLayout;
	private NavSundryShowTextView mNavSundryShowTextView;

	private Animation mTipEnterAnimation;
	private int mOldShortTipType;
	// zoom view
	private ZoomView mZoomView;
	// no sign, please scan, input lock
	private SnowTextView mSpecial_SnowTextView;
	private InputPwdDialog mInputPwdView;
	public boolean mFirstShowInputPwdDialog = false;
	private CheckLockSignalChannelState mCheckLockSignalChannelState;
	private boolean mFirstTimeToSystem = true;
	private TVTeleTextManager tvTeleTextManager;
	private boolean mReturnFromThirdApp = false;

	private StatusBarManager mStatusBarManager;

	private Rect mShowSourceListViewRect;
	private Rect mShowChannelListViewRect;
	private Rect mShowFavoriteChannelListViewRect;
	public static int mShowSourceListWindowId = -1;
	public static int mShowChannelListWindowId = -1;
	public static int mShowFavoriteChannelListWindowId = -1;
	private ProgressBar mpb;
	private Rect mSundryLayoutRect;
	private int mSundryLayoutWindowId = -1;
	private Boolean isSundryLayoutFirst = true;


	private static Rect mAdjustVolumeViewRect;
	private static int mAdjustVolumeViewWindowId;
	private Boolean isAdjustVolumeViewFirst = true;
	// receive mmp broadcast message, to resolve cr that cannot play tv back
	// from mmp.
	public static boolean isResume = false; 
	public static boolean canOpenTv = false;
	private static final String PAUSE_ACTION = "com.mediatek.mediaplayer.pause";
	
	private int warmbootFlag = 0;
	private int videoBlueMuteValue = 0;
	
	//lkm
	InputSourceListener mInputSourceListener = null;
	//lkm
	
	BroadcastReceiver mmpBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (isResume) {
				NewPipLogic.getInstance(TurnkeyUiMainActivity.this)
						.resumeMainOutput();
			}
		}
	};
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MessageType.SHOW_NO_TTX:
				Log.e("handler", "show no ttx");
				Toast.makeText(TurnkeyUiMainActivity.this, R.string.menu_teletext_notsupport_tip, 1000).show();
				break;
			case SnowTextView.DRAW_TEXT:
				if (mSpecial_SnowTextView != null) {
					mSpecial_SnowTextView.invalidate();
				}
				break;
			case MessageType.NAV_ADUST_VOLUME_DIMISS:
				unlockAdjustVolLayoutRect();
				mCheckLockSignalChannelState
						.checkLockedSignStateOrHasChannel(false);
				break;
			case MessageType.NAV_NUMKEY_CHANGE_CHANNEL:
				inputChannelNumStrBuffer = new StringBuffer();
				mNumputChangeChannel = false;
				if (mNavIntegration.iSelectChannel(mSelectedChannelNum)) {
					mNavIntegration.iSetSourcetoTv();
				} else {
					mBannerView.show(false, -1, false);
				}
				break;
			case MessageType.NAV_BANNERVIEW_DIMISS:
				mBannerView.hideAllBanner();
				if (!mShowSourceListView.isShowing()
						&& !mShowFavoriteChannelListView.isShowing()
						&& !mShowChannelListView.isShowing()) {
					if (msg.arg1 == BannerView.SPECIAL_NO_CHANNEL
							|| msg.arg1 == BannerView.SPECIAL_NO_SIGNAL 
							|| msg.arg1 == BannerView.SPECIAL_NO_SUPPORT) {					
						setSnowTextTagByState(msg.arg1);
						hideAllOnBannerDismiss();
						mSpecial_SnowTextView.setVisibility(View.VISIBLE);
					} else {
						if (!mBannerView.isNormalState()
								&& !mInputPwdView.isShowing()) {
							mCheckLockSignalChannelState
									.checkLockedSignStateOrHasChannel(false);
						}
					}
				}
				break;
			case MessageType.NAV_INPUTPWDVIEW_DIMISS:
				mInputPwdView.dismiss();
				if (msg.arg1 == MessageType.NAV_CURRENT_SOURCE_LOCKED) {
					mSpecial_SnowTextView
							.showSpecialView(ShowType.SPECIAL_INPUT_LOCKED);
					return;
				}
				if (msg.arg1 == MessageType.NAV_CURRENT_CHANNEL_LOCKED) {
					mSpecial_SnowTextView
							.showSpecialView(ShowType.SPECIAL_CHANNEL_LOCKED);
					return;
				}
				break;
			case MessageType.NAV_CURRENT_SOURCE_LOCKED:
			case MessageType.NAV_CURRENT_CHANNEL_LOCKED:
				// mSpecial_SnowTextView.setVisibility(View.GONE);
				mBannerView.show(false, -1, false);
				if (mShowSourceListView.isShowing() == false) {
					hideChannelList();
					hideTypeList();
					hideFavoriteList();
					hideZoomView();
					hideAdustVolLayout();
					hideSundryLayout();
					hideInputPwdView();
					mInputPwdView.show();
				} else {
					mFirstShowInputPwdDialog = true;
				}

				break;
			case MessageType.NAV_SHOW_CURRENT_CHANNEL_INFO:
				mBannerView.showSimpleBar();
				mHandler.sendEmptyMessageDelayed(
						MessageType.NAV_SHOW_CHANNEL_INFO,
						MessageType.delayMillis5);
				break;

			case MessageType.NAV_SHORTTIP_TEXTVIEW_DIMISS:
				unlockSundryLayoutRect();
				mCheckLockSignalChannelState
						.checkLockedSignStateOrHasChannel(false);
				break;
			case MessageType.NAV_ZOOMVIEW_DIMISS:
				mZoomView.setVisibility(View.GONE);
				break;
			case MessageType.FORM_TK_TO_MENUMAIN:
				Bundle bundle = new Bundle();
				bundle.putInt("CIValue", 2);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(TurnkeyUiMainActivity.this, MenuMain.class);
				startActivity(intent);
				break;

			case MessageType.FBM_MODE_SWITCH_OK:
				MtkLog.i(TAG,"i have got FBM_MODE_SWITCH_OK message...");

				NewPipLogic pipLogic = NewPipLogic.getInstance(TurnkeyUiMainActivity.this);
				if (warmbootFlag == 1) {
//					pipLogic.resumeMainOutputWithNoConnect();
						warmbootFlag = 0;
				} else {
					pipLogic.resumeMainOutput();
				}
				
				if(mpb != null)				
					mpb.setVisibility(View.GONE);
				canOpenTv = true;
				break;
			case MessageType.VIDEO_BLUE_MUTE_REVERT:
				if (videoBlueMuteValue == 1) {
					mTV.setVideoBlueMute(true);
					optionRange.set(videoBlueMuteValue);
				}
				warmbootFlag = 0;
				break;
			default:
				break;
			}
		}

	};
	private BannerView mBannerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ScreenConstant.SCREEN_WIDTH = getWindowManager().getDefaultDisplay().getRawWidth();
		ScreenConstant.SCREEN_HEIGHT  = getWindowManager().getDefaultDisplay().getRawHeight();
		mNavIntegration = NavIntegration.getInstance(this);
		mBypassWindowManager = BypassWindowManager.getInstance(this);
		AnimationManager.getInstance();
		mTV = TVContent.getInstance(this);
		inpMgr = mTV.getInputManager();
		cfg = mTV.getConfigurer();
		optionRange = (TVOptionRange<Integer>)(cfg.getOption(MenuConfigManager.BLUE_MUTE));
		saveV = SaveValue.getInstance(this);
		tvTeleTextManager=mTV.getTeleTextManager();
		tvTeleTextManager.registerNotifyLisenter(myCompListener);
		OSDLanguage osdLanguage= new OSDLanguage(this.getApplicationContext());
		Log.e(TAG,"turnkyUI oncreate:"+saveV.readValue(MenuConfigManager.OSD_LANGUAGE));
		try{
		osdLanguage.setOSDLanguage(saveV.readValue(MenuConfigManager.OSD_LANGUAGE));
	}	catch(Exception e){
		Log.e(TAG,"tk oncreate setosd lan exception"+e);
		}
		initMuteImageView();
		mStatusBarManager = (StatusBarManager) getSystemService(Context.STATUS_BAR_SERVICE);

		// set auto sync time to get correct time when system booted
		mNavIntegration.setAutoSync();
		if (mNavIntegration.isStartSetupwizard()) {
			mSetupFlag = true;
			mJump = true;
			Intent intent = new Intent(this, SetupWizardActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			// startActivity(intent);
			startActivityForResult(intent, 200);
		}
		
		registerMmpReceiver();
//		startupConnect();
		IntentFilter filter = new IntentFilter();
//		filter.addAction(Intent.ACTION_STANDBY);
		filter.setPriority(IntentFilter.SYSTEM_LOW_PRIORITY);
		this.registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				Log.i(TAG, "standby TV in main activity!");
				warmbootFlag = 1;
				//MtkFilesListActivity.removeView();
//				inpMgr.getOutput("main").stop();
/*
				boolean mute = mTV.isMute();
				videoBlueMuteValue = optionRange.get();
				
				mTV.setAudioMute(true);
				mTV.setVideoBlueMute(false);
				optionRange.set(0);
*/				
				startActivity(new Intent(TurnkeyUiMainActivity.this, TurnkeyUiMainActivity.class));
				
				NewPipLogic pipLogic = NewPipLogic.getInstance(context);
				pipLogic.pauseMainOutput();
				
				ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
				List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		
				for(RunningAppProcessInfo processInfo : list){
					if(processInfo.processName.equals("com.mediatek.ui") == false){
						Log.e(TAG,"Kill processName:" + processInfo.processName);
						am.killBackgroundProcesses(processInfo.processName);
					}
				}
				List<RunningTaskInfo> taskList = am.getRunningTasks(50);
							
				for(RunningTaskInfo task : taskList){
					if (task.baseActivity.getPackageName().equals("com.mediatek.ui") == false) {
						Log.e(TAG,"Force package:" + task.baseActivity.getPackageName());
						am.forceStopPackage(task.baseActivity.getPackageName());
					}
				}

				list = am.getRunningAppProcesses();

				for (int i = 0; i < list.size(); i++) {	
					Log.e(TAG,"The running process:" + list.get(i).processName);
				}
				
				Util.exitMmpActivity(context);							
//				mTV.setAudioMute(mute);
				
//				Log.e(TAG,"*************ap ack intent!");
//				Intent apAckIntent = new Intent(Intent.ACTION_AP_ACK);
//        context.sendBroadcast(apAckIntent);
			}
		}, filter);
		
/*		IntentFilter resumeFilter = new IntentFilter();
		resumeFilter.addAction(Intent.ACTION_RESUME);
		this.registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				Log.i(TAG, "Resume TV in main activity!");
				TVOutput output = inpMgr.getOutput("main");
				if(null != output){
					output.connect(output.getInput());
				   }
			}
		}, resumeFilter);*/
		SystemProperties.set("service.bootanim.exit", "1");
	}

	private void registerMmpReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.mediatek.closeVideo");
		filter.addAction("com.mediatek.closeAudio");
		this.registerReceiver(mmpBroadcastReceiver, filter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 200) {
			mSetupFlag = false;
		}
		if(requestCode == 0){
			if(data != null && data.getBooleanExtra("FROM_MENUMAIN_TO_TK", false)){
				mHandler.sendEmptyMessageDelayed(MessageType.FORM_TK_TO_MENUMAIN, MessageType.delayForTKToMenu);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStart() {
		
		MtkLog.d(TAG, "=========onStart()");
		NavIntegration.setColorKey(false);
		TVContent.getInstance(this).setOpacity(200);// set OSD opacity
		//lkm
		registerSourceListener();
		//lkm
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MtkLog.i(TAG,"~~~~~~~~~~~~~~~~~~~~~~~enter onResume");
		Intent pauseIntent = new Intent(PAUSE_ACTION);
		sendBroadcast(pauseIntent);
		mStatusBarManager.setSystemUiBarHight(0);

		isResume = true;

		if (mNavIntegration.isMute()) {
			mMuteImageView.setVisibility(View.VISIBLE);
		}

		// setup
		if (mSetupFlag == false && mFirstTimeToSystem == true) {
			initCheckPowTimer();
			setContentView(R.layout.nav_main_layout_test);
			init();
			mpb = (ProgressBar)findViewById(R.id.fbm_mode_progressbar);
			
			mToastInfo = ToastInfoView.getInstance(this);
			mToastInfo.setmSnowTextView(mSpecial_SnowTextView);
			setTimeZone();
		}
		if (mSetupFlag == false) {
			if (mFirstTimeToSystem == true) {
				if (mNavIntegration.isCurrentSourceTv()
						&& mNavIntegration.iGetChannelLength() > 0) {
					mHandler.removeMessages(MessageType.NAV_BANNERVIEW_DIMISS);
					mBannerView.showSimpleBar();
					mHandler.sendEmptyMessageDelayed(
							MessageType.NAV_BANNERVIEW_DIMISS,
							MessageType.delayMillis4);
					mHandler.postDelayed(new Runnable() {
						public void run() {
							registeListeners();
							if (!mNavIntegration.isCurrentSourceBlocked()) {
								mHandler
										.removeMessages(MessageType.NAV_BANNERVIEW_DIMISS);
								mBannerView.showBasicBar();
								mBannerView
										.setBannerState(BannerView.CHILD_TYPE_INFO_BASIC);
								mHandler.sendEmptyMessageDelayed(
										MessageType.NAV_BANNERVIEW_DIMISS,
										MessageType.delayMillis4);
							}
							mCheckLockSignalChannelState
									.checkLockedSignStateOrHasChannel(true);
						}
					}, 1000);
				} else {
					mCheckLockSignalChannelState
							.checkLockedSignStateOrHasChannel(true);
					registeListeners();
				}

				mFirstTimeToSystem = false;
			} else {
				registeListeners();
				if (mCheckLockSignalChannelState
						.checkLockedSignStateOrHasChannel(true) == false) {
					mBannerView.show(false, BannerView.CHILD_TYPE_INFO_SIMPLE,
							false);
				}
			}
		}
		
		if(mpb != null)
			mpb.setVisibility(View.VISIBLE);

		if (!NewPipLogic.isTvNormal()) {
			new Thread(new Runnable() {
				public void run() {
					// TODO Auto-generated method stub
					if("1".equals(SystemProperties.get("ro.mtk.system.switchfbm"))){
						MtkLog.i(TAG, "try to switch fbm mode..."+SystemProperties.get("ro.mtk.system.switchfbm"));
					 	mNavIntegration.setFbmMode(1);
						MtkLog.i(TAG, "switch OK.");
					}
					mHandler.sendEmptyMessage(MessageType.FBM_MODE_SWITCH_OK);
				}
			}).start();
		} else {
			if(mpb != null)
				mpb.setVisibility(View.GONE);
		}

		//when enter TV, stop music playing in 3rd APP
		Intent mIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
		mIntent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
				KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MTKIR_STOP));
		sendOrderedBroadcast(mIntent,null);
		mIntent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
				KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MTKIR_STOP));
		sendOrderedBroadcast(mIntent,null);
/*	
		if (warmbootFlag == 1) {						
			mHandler.sendEmptyMessageDelayed(
				MessageType.VIDEO_BLUE_MUTE_REVERT,
				MessageType.delayMillis5);
		}
*/
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		
		unregisteListeners();
		MtkLog.i("fff","~~~~~~~~~~~~~~~~~~~~~~~enter onPause");

		GetCurrentTask ctask = GetCurrentTask.getInstance(this);
		NewPipLogic pipLogic = NewPipLogic.getInstance(this);

		if (!ctask.isCurTaskTKUI()) {	
			MtkLog.i("fff","~~~~~~~~~~~~~~~~~~~~~~~not tkui");
			isResume = false;
			//mStatusBarManager.setSystemUiBarHight(48);
			mStatusBarManager.setSystemUiBarHight(getApplicationContext().getResources().getDimensionPixelSize(com.android.internal.R.dimen.system_bar_height));
			pipLogic.pauseMainOutput();
			mReturnFromThirdApp = true;
			NavIntegration.setColorKey(false);
			TVContent.getInstance(this).setOpacity(255);
			setMuteGone();

			if("1".equals(SystemProperties.get("ro.mtk.system.switchfbm"))){
				mNavIntegration.setFbmMode(2);
			}
			canOpenTv = false;
		}	
	}

	public void onDestroy() {
		
		MtkLog.d(TAG, "==========onDestroy()");
		super.onDestroy();
		if (wm != null) {
			wm.removeView(mMuteImageView);
		}
		tvTeleTextManager.removeNotifyLisenter(myCompListener);
		unregisterReceiver(mmpBroadcastReceiver);
		if (mNavSundryShowTextView != null) {
			mNavSundryShowTextView.unregisterReceiver();
		}
		
		//lkm
		unregisterSourceListener();
		//lkm
	}

	private boolean isActive=false;
	
	CompListener myCompListener=new CompListener() {
		
		public void CompNotifyInfo(String MsgContent) {
			Log.e("TKUIMAIN TTX", "MsgContent:"+MsgContent);
			if(ComponentService.COMP_TTX_SHOW_NO_TELETEXT.equals(MsgContent)){
				Log.e("TKUIMAIN TTX", "show no ttx notify"+isActive);
					isActive=false;
					tvTeleTextManager.inActivateCompoent(ComponentService.COMP_TTX_NAME);
					mHandler.sendEmptyMessage(MessageType.SHOW_NO_TTX);
			}else if(ComponentService.COMP_TTX_ACTIVATE.equals(MsgContent)){
				isActive=true;
			}else if(ComponentService.COMP_TTX_COMP_INACTIVATE.equals(MsgContent)){
				isActive=false;
			}else if(ComponentService.COMP_TTX_AVAIL.equals(MsgContent)){
				Log.e("TKUIMAIN TTX", "active notify"+isActive);
			}else if(ComponentService.COMP_TTX_NOT_AVAIL.equals(MsgContent)){
				Log.e("TKUIMAIN TTX", "inactive notify"+isActive);
			}
		}
	};

	private boolean sendkeyTTXEvent(int keyCode){
		if(keyCode==KeyMap.KEYCODE_MTKIR_MUTE||keyCode==KeyMap.KEYCODE_MTKIR_CHDN||keyCode==KeyMap.KEYCODE_MTKIR_CHUP){
			return false;
		}
		return true;
	}
	
	
	private boolean isATV(){
		if(inpMgr!= null && inpMgr.getCurrInputSource()!=null && "atv".equals(inpMgr.getCurrInputSource())){
			return true;
		}
		return false;
	}
//liufeng_chk
private void CaptureScreen2File()
{
		
	CaptureFrameBuffer.nativeInitCaptureOd();
	CaptureFrameBuffer.nativeSetQuality(100);
	int ret = CaptureFrameBuffer.nativeCaptureScreen2File(0, 0, 0, 0);
}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("TKUIMAIN TTX", "keycode:"+keyCode+isActive);
		if(isActive&&sendkeyTTXEvent(keyCode)&&isATV()){
			
				Log.e("TKUIMAIN TTX", "send key to ttx !"+keyCode);
				tvTeleTextManager.sendkeyEvent(keyCode, ComponentService.KEY_EVENT_DOWN);
			return true;
		}

                if (canOpenTv == false){
			MtkLog.i(TAG,"TV not switch fbm mode ok, waiting.......");
			return false;
		}

		mToastInfo.toastVGAInfo();
		if (saveV.readValue(MenuConfigManager.AUTO_SLEEP) != 0) {
			SleepTimerOff sleepTimerOff = new SleepTimerOff(this);
			sleepTimerOff.shutDownAuto(saveV
					.readValue(MenuConfigManager.AUTO_SLEEP));
		}
		boolean handled = false;
		switch (event.getKeyCode()) {
		case KeyMap.KEYCODE_MTKIR_REWIND:
				Log.e("TKUIMAIN TTX", "activeComponent!!!!!!!"+	 inpMgr.getCurrInputSource());
				if(isATV()){
					tvTeleTextManager.activateComponent(ComponentService.COMP_TTX_NAME);
				}
				
			break;
		case KeyMap.KEYCODE_BACK:
			if (mSpecial_SnowTextView.getVisibility() != View.VISIBLE) {
				if (mNumputChangeChannel == true) {
					cancelNumChangeChannel();
				}

				hideAll();

				if (mSpecial_SnowTextView.mForcedtoGONE == true) {
					mCheckLockSignalChannelState
							.checkLockedSignStateOrHasChannel(false);
				}
			}
			break;
		case KeyMap.KEYCODE_MENU:
			// service for ci
			mCICardDelayNotification.clearDelayNotifications();
			mCICardDelayNotification.setPushToQueue(false);
			hideAll();
			NavSundryImplement nsi=NavSundryImplement.getInsNavSundryImplement(
					TurnkeyUiMainActivity.this);
			if (nsi.isFreeze()) {
				nsi.setFreeze(false);				
			}
			
			//Return "Zoom=1" when "Zoom=1/2 or Zoom=2" and press "Menu" key.
			NavIntegrationZoom.getInstance(TurnkeyUiMainActivity.this)
					.setZoomMode(NavIntegrationZoom.ZOOM_1);
			
			Intent intent = new Intent(TurnkeyUiMainActivity.this,
					MenuMain.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 0);
			break;
		case KeyMap.KEYCODE_MTKIR_ANGLE:
			if (MeidaMainActivity.isValid()) {
			// service for ci
			mCICardDelayNotification.clearDelayNotifications();
			mCICardDelayNotification.setPushToQueue(false);
			hideAll();
			mMuteImageView.setVisibility(View.GONE);

			//enter mmp, set isResume to false.
			isResume = false;
			
			NewPipLogic pl = NewPipLogic.getInstance(this);
			pl.pauseMainOutput();
			TVContent.getInstance(this).setOpacity(255);

			startActivityForResult(new Intent(TurnkeyUiMainActivity.this,
					MeidaMainActivity.class), 0);
			}
			break;
		case KeyMap.KEYCODE_MTKIR_GUIDE:
			// service for ci
			mCICardDelayNotification.clearDelayNotifications();
			mCICardDelayNotification.setPushToQueue(false);
			if (mNavIntegration.isCurrentSourceTv()) {
			if (mNavIntegration.isDTVHasChannels()) {
				if (!mNavIntegration.isCurrentSourceDTV()) {
					mNavIntegration.changeDTVSource();
				}
				if (mNavIntegration.iGetChannelLength() > 0
						&& !mNavIntegration.isCurrentSourceBlocking()) {
					hideAll();
						
					//Return "Zoom=1" when "Zoom=1/2 or Zoom=2" and press "guide" key.
					NavIntegrationZoom.getInstance(TurnkeyUiMainActivity.this)
								.setZoomMode(NavIntegrationZoom.ZOOM_1);
						
					startActivityForResult(new Intent(
								TurnkeyUiMainActivity.this, EPGActivity.class),
								0);
					}
				}
			}
			break;
		case KeyMap.KEYCODE_0:
		case KeyMap.KEYCODE_1:
		case KeyMap.KEYCODE_2:
		case KeyMap.KEYCODE_3:
		case KeyMap.KEYCODE_4:
		case KeyMap.KEYCODE_5:
		case KeyMap.KEYCODE_6:
		case KeyMap.KEYCODE_7:
		case KeyMap.KEYCODE_8:
		case KeyMap.KEYCODE_9:
			if (mNavIntegration.isCurrentSourceTv()) {
				mSpecial_SnowTextView.setVisibility(View.GONE);
				if (mNavIntegration.iGetChannelLength() <= 0) {
					mBannerView.show(false, -1, false);
					return true;
				} else {
					inputChannelNum(keyCode);
					mBannerView.updateInputting(mSelectedChannelNum + "");
					mHandler.removeMessages(MessageType.NAV_BANNERVIEW_DIMISS);
					mHandler.sendEmptyMessageDelayed(
							MessageType.NAV_BANNERVIEW_DIMISS,
							MessageType.delayMillis4);
					mHandler
							.removeMessages(MessageType.NAV_NUMKEY_CHANGE_CHANNEL);
					mHandler.sendEmptyMessageDelayed(
							MessageType.NAV_NUMKEY_CHANGE_CHANNEL,
							MessageType.delayMillis5);
				}
			} else {
				mSpecial_SnowTextView.setVisibility(View.GONE);
				inputChannelNum(keyCode);
				mBannerView.updateInputting(mSelectedChannelNum + "");
				mHandler.removeMessages(MessageType.NAV_BANNERVIEW_DIMISS);
				mHandler.sendEmptyMessageDelayed(
						MessageType.NAV_BANNERVIEW_DIMISS,
						MessageType.delayMillis4);
				mHandler.removeMessages(MessageType.NAV_NUMKEY_CHANGE_CHANNEL);
				mHandler.sendEmptyMessageDelayed(
						MessageType.NAV_NUMKEY_CHANGE_CHANNEL,
						MessageType.delayMillis5);
			}
			break;

		case KeyMap.KEYCODE_VOLUME_DOWN:
		case KeyMap.KEYCODE_VOLUME_UP:			

			handled = keyEventNoResponse();
			if (handled == true) {
				return true;
			}
			hideChannelList();
			hideTypeList();
			hideFavoriteList();
			hideZoomView();
			hideSundryLayout();
			hideInputPwdView();
			mSpecial_SnowTextView.setVisibility(View.GONE);

			if(keyCode == KeyMap.KEYCODE_VOLUME_UP)   
			 {
			  TurnkeyUiMainActivity.cancelMute();
			 }
			if (mAdjustVolLayout.getVisibility() != View.VISIBLE) {
				mAdjustVolLayout.setVisibility(View.VISIBLE);
				if(AnimationManager.getInstance().getIsAnimation()){
					AnimationManager.getInstance().adjustVolEnterAnimation( mAdjustVolLayout);
				}			 
			    adjustVolumeView.initVolume();
				getVolumeViewRec();
				if (mSundryLayout.getVisibility() == View.VISIBLE) {
					unlockSundryLayoutRect();
				}
			} else {
				adjustVolumeView.volumeEvent(keyCode, event);
			}
		
			break;

		case KeyMap.KEYCODE_DPAD_DOWN:
		case KeyMap.KEYCODE_DPAD_UP:
			if (mAdjustVolLayout.getVisibility() == View.VISIBLE) {
				adjustVolumeView.volumeEvent(keyCode, event);

			} else if (mZoomView.getVisibility() == View.VISIBLE) {
				mHandler
						.removeMessages(MessageType.NAV_SHORTTIP_TEXTVIEW_DIMISS);
				mZoomView.changPicture(keyCode);
				mHandler.sendEmptyMessageDelayed(
						MessageType.NAV_SHORTTIP_TEXTVIEW_DIMISS,
						MessageType.delayMillis4);
			} else if (!mShowChannelListView.isShowing()
					&& !mShowFavoriteChannelListView.isShowing()) {
				if (keyCode == KeyMap.KEYCODE_DPAD_UP) {
					mBannerView.detailPageUp();
				} else {
					mBannerView.detailPageDown();
				}
			}

			break;

		case KeyMap.KEYCODE_MTKIR_SOURCE:
			  
		  //lkm
		  getInputSourceStatus();
		  //lkm

			// gain current item selected position
			if (mNumputChangeChannel == true) {
				cancelNumChangeChannel();
				mCheckLockSignalChannelState
						.checkLockedSignStateOrHasChannel(false);
				return true;
			}
			if (mShowSourceListView.isShowing() == false) {
				hideAllOnSource();
				mShowSourceListView.show();
				mShowSourceListWindowId = mBypassWindowManager
						.getAvailableWindowId();
				mShowSourceListViewRect = new Rect(mShowSourceListView.x
						- mShowSourceListView.menuWidth / 2
						+ ScreenConstant.SCREEN_WIDTH / 2,
						mShowSourceListView.y - mShowSourceListView.menuHeight
								/ 2 + ScreenConstant.SCREEN_HEIGHT / 2,
						mShowSourceListView.x + mShowSourceListView.menuWidth
								/ 2 + ScreenConstant.SCREEN_WIDTH / 2,
						mShowSourceListView.y + mShowSourceListView.menuHeight
								/ 2 + ScreenConstant.SCREEN_HEIGHT / 2);
				MtkLog.i(TAG, "mShowSourceListViewRect  left:"
						+ mShowSourceListViewRect.left + "  top "
						+ mShowSourceListViewRect.top + "  right "
						+ mShowSourceListViewRect.right + " bottom"
						+ mShowSourceListViewRect.bottom);
				mBypassWindowManager.setBypassWindow(true,
						mShowSourceListWindowId, mShowSourceListViewRect);
				return true;
			}

			break;
		case KeyMap.KEYCODE_MTKIR_TIMER:
			mNavIntegration.syncTimeIfNeed();
		case KeyMap.KEYCODE_MTKIR_SLEEP:
		case KeyMap.KEYCODE_MTKIR_PEFFECT:
		case KeyMap.KEYCODE_MTKIR_SEFFECT:
		case KeyMap.KEYCODE_MTKIR_ASPECT:
		case KeyMap.KEYCODE_MTKIR_FREEZE:
		case KeyMap.KEYCODE_MTKIR_MTSAUDIO:
			handled = keyEventNoResponse();
			if (handled == true) {
				return true;
			}

			hideZoomView();
			
			if (mSundryLayout.getVisibility() != View.VISIBLE) {
				MtkLog.i(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ok");
				hideTypeList();
				hideFavoriteList();
				hideChannelList();
				mSpecial_SnowTextView.setVisibility(View.GONE);
				mSundryLayout.setVisibility(View.VISIBLE);
				mNavSundryShowTextView.setAndShowTextContent(false, keyCode);
				getSundryLayoutRec();
			} else {
				if (mSundryLayout.getVisibility() == View.VISIBLE
						&& mOldShortTipType == keyCode) {
					MtkLog.i(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~no");
					mNavSundryShowTextView.setThirdApp(mReturnFromThirdApp);
					mNavSundryShowTextView.setAndShowTextContent(true, keyCode);
				} else {
					mNavSundryShowTextView.startAnimation(mTipEnterAnimation);
					mNavSundryShowTextView.setThirdApp(mReturnFromThirdApp);
					mNavSundryShowTextView
							.setAndShowTextContent(false, keyCode);
				}
			}
			mOldShortTipType = keyCode;
			break;

		case KeyMap.KEYCODE_MTKIR_ZOOM:
			handled = keyEventNoResponse();
			if (handled == true) {
				return true;
			}
			mHandler.removeMessages(MessageType.NAV_SHORTTIP_TEXTVIEW_DIMISS);
				NavSundryShowTextView.timeFlag = false;
			if (isNormalForZoom()) {
				if (mSpecial_SnowTextView.getVisibility() == View.VISIBLE) {
					mSpecial_SnowTextView.setVisibility(View.GONE);
				}
				mSundryLayout.setVisibility(View.VISIBLE);
				mNavSundryShowTextView.setAndShowTextContent(false, keyCode);
				mNavSundryShowTextView
						.setText(getString(R.string.nav_no_function));
				getSundryLayoutRec();
			} else {
				if (mNavSundryShowTextView.isFreeze == true) {
					mZoomView.zoomSetfreeze();
					mNavSundryShowTextView.isFreeze = false;
				}
				if (mSundryLayout.getVisibility() == View.VISIBLE
						&& mOldShortTipType == keyCode) {
					mZoomView.changeZoom(mNavSundryShowTextView);
				} else if (mShowSourceListView.isShowing() == false) {
					mSundryLayout.setVisibility(View.VISIBLE);
					if (mSpecial_SnowTextView.getVisibility() == View.VISIBLE) {
						mSpecial_SnowTextView.setVisibility(View.GONE);
					}
					hideChannelList();
					hideFavoriteList();
					mNavSundryShowTextView
							.setAndShowTextContent(false, keyCode);
					mZoomView.showCurrentZoom(mNavSundryShowTextView);
					mNavSundryShowTextView.startAnimation(mTipEnterAnimation);
					getSundryLayoutRec();
				}
			}
			mOldShortTipType = keyCode;
			mHandler.sendEmptyMessageDelayed(
					MessageType.NAV_SHORTTIP_TEXTVIEW_DIMISS,
					MessageType.delayMillis4);
			break;

		case KeyMap.KEYCODE_MTKIR_YELLOW:
			handled = keyEventNoResponse();
			if (handled == true) {
				return true;
			}

			if (mNavIntegration.canDoCapLogo()) {
				hideAll();
				startActivityForResult(new Intent(TurnkeyUiMainActivity.this,
						CaptureLogoActivity.class), 0);
			} else {
				mToastInfo.toastCaptureInfo();
			}
			break;

		case KeyMap.KEYCODE_MTKIR_CHUP:
		case KeyMap.KEYCODE_MTKIR_CHDN:
			if(isActive&&isATV()){
				tvTeleTextManager.inActivateCompoent(ComponentService.COMP_TTX_NAME);
			}
			if (mNavIntegration.isCurrentSourceBlocking()) {
				return true;
			}
			hideZoomView();
			hideSundryLayout();
			handled = keyEventNoResponse();
			if (handled == true) {
				return true;
			}

			if (mNavIntegration.isCurrentSourceTv() == false) {
				mNavIntegration.iSetSourcetoTv();
				return true;
			} else {
				if (mNavIntegration.iGetChannelLength() > 0) {
					if (keyCode == KeyMap.KEYCODE_MTKIR_CHDN) {
						mNavIntegration
								.iSetChannel(NavIntegration.CHANNEL_DOWN);
					} else {
						mNavIntegration.iSetChannel(NavIntegration.CHANNEL_UP);
					}
				} else {
					mHandler.sendEmptyMessage(MessageType.NAV_SCANN_CHANNEL);
				}
			}

			break;
		case KeyMap.KEYCODE_MTKIR_PRECH:
			//add by lzj for fix bug DTV00387849
			if (mNavIntegration.isCurrentSourceBlocking()) {
				return true;
			}
			hideZoomView();
			hideSundryLayout();
			handled = keyEventNoResponse();
			if (handled == true) {
				return true;
			}
			if (!mNavIntegration.isCurrentSourceTv()) {
				break;
			}
			if (mNavIntegration.iGetChannelLength() > 0) {
				mNavIntegration.iSetChannel(NavIntegration.CHANNEL_PRE);
			} else {
				// mSpecial_SnowTextView.setVisibility(View.GONE);
				// showNullInfoBar(
				// getString(R.string.nav_please_scan_channels), false);
			}
			break;

		case KeyMap.KEYCODE_MTKIR_INFO:
			//TVChannel curChannel = mNavIntegration.iGetCurrentChannel();
			hideChannelList();
			hideTypeList();
			hideFavoriteList();
			hideAdustVolLayout();
			hideSundryLayout();
			hideInputPwdView();
			mSpecial_SnowTextView.setVisibility(View.GONE);
			mBannerView.setIsOnKeyInfo(true);
			mBannerView.show(true, -1, true);
			mBannerView.setIsOnKeyInfo(false);
			mZoomView.zoomGone();
			break;

		case KeyMap.KEYCODE_MTKIR_SUBTITLE:
			handled = keyEventNoResponse();
			if (handled == true) {
				return true;
			}
			if (mNavIntegration.isCurrentSourceTv()) {
				mSpecial_SnowTextView.setVisibility(View.GONE);
				if (mNavIntegration.iGetChannelLength() > 0) {
					hideZoomView();
					hideAdustVolLayout();
					mSpecial_SnowTextView.setVisibility(View.GONE);
					NavSundryImplement mNsi=NavSundryImplement.getInsNavSundryImplement(
							TurnkeyUiMainActivity.this);
					if (mNsi.isFreeze() ) {
						mNsi.setFreeze(false);
						String[] freezeModeName = TurnkeyUiMainActivity.this
								.getResources().getStringArray(
										R.array.nav_freeze_strings);
						mNavSundryShowTextView.setText(freezeModeName[1]);
						mBannerView.show(false, -1, false);
						// hideSundryLayout();
					}
					mBannerView.updateSubtitle();
				} else {
					mSpecial_SnowTextView.setVisibility(View.GONE);
					mBannerView.show(false, -1, false);
				}
			}
			break;
		case KeyMap.KEYCODE_DPAD_CENTER:
			if (mNumputChangeChannel) {
				cancelNumChangeChannel();
				mHandler.sendEmptyMessage(MessageType.NAV_NUMKEY_CHANGE_CHANNEL);
				return true;
			}
			if (mNavIntegration.isCurrentSourceBlocking()
					|| mNavIntegration.isCurrentChannelBlocking()) {
				if (mInputPwdView != null && mInputPwdView.isShowing() == false) {
					hideAll();
					mBannerView.show(false, -1, false);
					mInputPwdView.show();
				}
				return true;
			}
			if (mShowSourceListView.isShowing() == false
					&& mNavIntegration.isCurrentSourceTv()) {
				if (mShowChannelListView.isShowing() == false
						&& mNavIntegration.isCurrentSourceTv()) {
					if (mNavIntegration.iGetChannelLength() > 0) {
						hideZoomView();
						hideAdustVolLayout();
						hideSundryLayout();
						mSpecial_SnowTextView.setVisibility(View.GONE);
						mShowChannelListView.show();
						mShowChannelListViewRect = new Rect(
								mShowChannelListView.x
										- mShowChannelListView.menuWidth / 2
										+ ScreenConstant.SCREEN_WIDTH / 2,
								mShowChannelListView.y
										- mShowChannelListView.menuHeight / 2
										+ ScreenConstant.SCREEN_HEIGHT / 2,
								mShowChannelListView.x
										+ mShowChannelListView.menuWidth / 2
										+ ScreenConstant.SCREEN_WIDTH / 2,
								mShowChannelListView.y
										+ mShowChannelListView.menuHeight / 2
										+ ScreenConstant.SCREEN_HEIGHT / 2);
						MtkLog.i(TAG, "mShowChannelListViewRect  left:"
								+ mShowChannelListViewRect.left + "  top "
								+ mShowChannelListViewRect.top + "  right "
								+ mShowChannelListViewRect.right + " bottom"
								+ mShowChannelListViewRect.bottom);
						mShowChannelListWindowId = mBypassWindowManager
								.getAvailableWindowId();
						mBypassWindowManager.setBypassWindow(true,
								mShowChannelListWindowId,
								mShowChannelListViewRect);
					} else {
						mSpecial_SnowTextView.setVisibility(View.GONE);
						mBannerView.show(false, -1, false);
					}
				}
			}

			break;

		case KeyMap.KEYCODE_MTKIR_RED:
			mMuteImageView.setVisibility(View.GONE);
			Intent mIntent = new Intent();
			mIntent.setClassName("com.android.launcher",
					"com.android.launcher2.Launcher");
			try {
				int setValue=Settings.System.getInt(getContentResolver(), Settings.System.AUTO_TIME, 0);
				if(setValue==1){
					NetworkTime.getInstance(this).getNetTime();
				}
				startActivity(mIntent);
			} catch (Exception e) {
				Toast.makeText(this, "no Launcher.", Toast.LENGTH_SHORT).show();
			}

			break;

		case KeyMap.KEYCODE_MTKIR_EJECT:
			handled = keyEventNoResponse();
			if (handled == true) {
				return true;
			}
			if (mNavIntegration.isCurrentSourceTv()) {
				mSpecial_SnowTextView.setVisibility(View.GONE);
				if (mNavIntegration.iGetChannelLength() > 0) {
					if (!mShowFavoriteChannelListView.isShowing()) {
						mNavIntegration.isSetChannelFavorite(mNavIntegration
								.iGetCurrentChannel());
					}
					mBannerView.updateFavorite();
				} else {
					mSpecial_SnowTextView.setVisibility(View.GONE);
					mBannerView.show(false, -1, false);
				}
			}

			break;
		case KeyMap.KEYCODE_MTKIR_PLAYPAUSE:
			handled = keyEventNoResponse();
			if (handled == true) {
				return true;
			}
			if (mNavIntegration.isCurrentSourceTv()) {
				mSpecial_SnowTextView.setVisibility(View.GONE);
				if (mNavIntegration.iGetChannelLength() > 0) {
					hideZoomView();
					hideAdustVolLayout();
					hideSundryLayout();
					mSpecial_SnowTextView.setVisibility(View.GONE);
					mShowFavoriteChannelListView.show();
					mShowFavoriteChannelListViewRect = new Rect(
							mShowFavoriteChannelListView.x
									- mShowFavoriteChannelListView.menuWidth
									/ 2 + ScreenConstant.SCREEN_WIDTH / 2,
							mShowFavoriteChannelListView.y
									- mShowFavoriteChannelListView.menuHeight
									/ 2 + ScreenConstant.SCREEN_HEIGHT / 2,
							mShowFavoriteChannelListView.x
									+ mShowFavoriteChannelListView.menuWidth
									/ 2 + ScreenConstant.SCREEN_WIDTH / 2,
							mShowFavoriteChannelListView.y
									+ mShowFavoriteChannelListView.menuHeight
									/ 2 + ScreenConstant.SCREEN_HEIGHT / 2);
					MtkLog.i(TAG, "mShowFavoriteChannelListViewRect  left:"
							+ mShowFavoriteChannelListViewRect.left + "  top "
							+ mShowFavoriteChannelListViewRect.top + "  right "
							+ mShowFavoriteChannelListViewRect.right
							+ " bottom"
							+ mShowFavoriteChannelListViewRect.bottom);
					mShowFavoriteChannelListWindowId = mBypassWindowManager
							.getAvailableWindowId();
					mBypassWindowManager.setBypassWindow(true,
							mShowFavoriteChannelListWindowId,
							mShowFavoriteChannelListViewRect);
				} else {
					mSpecial_SnowTextView.setVisibility(View.GONE);
					mBannerView.show(false, -1, false);
				}
			}

			break;

		case KeyMap.KEYCODE_MTKIR_STOP:
			if (mNavIntegration.iGetFavoriteList().size() > 0) {
				// channel list and current channel not null
				hideSundryLayout();
				mNavIntegration.iSelectFavChanUp();
			}
			break;

		case KeyMap.KEYCODE_MTKIR_MUTE:
			handled = keyEventNoResponse();
			if (handled == true) {
				return true;
			}
			TurnkeyUiMainActivity.switchMute();

			break;

		// MTS/AUDIO

		// left/right to change focus
		case KeyMap.KEYCODE_DPAD_LEFT:
		case KeyMap.KEYCODE_DPAD_RIGHT:
			if (mShowSourceListView.isShowing()) {
				return true;
			}
			if (mZoomView.getVisibility() == View.VISIBLE) {
				mHandler
						.removeMessages(MessageType.NAV_SHORTTIP_TEXTVIEW_DIMISS);
				mZoomView.changPicture(keyCode);
				mHandler.sendEmptyMessageDelayed(
						MessageType.NAV_SHORTTIP_TEXTVIEW_DIMISS,
						MessageType.delayMillis4);
			}
			break;

		//liufeng_chk	
		case KeyMap.KEYCODE_MTKIR_BLUE:
			MtkLog.i(TAG, "~~~~~~~~~~~~~~~~KEYCODE_MTKIR_BLUE~~~~~~~~~~0~~~~~~~~~~~ok");
			System.loadLibrary("captureod");
			MtkLog.i(TAG, "~~~~~~~~~~~~~~~~KEYCODE_MTKIR_BLUE~~~~~~~~~~1~~~~~~~~~~ok");			
			CaptureScreen2File();
			MtkLog.i(TAG, "~~~~~~~~~~~~~~~~KEYCODE_MTKIR_BLUE~~~~~~~~~~2~~~~~~~~~~ok");
			break;
		//liufeng end
		case KeyMap.KEYCODE_MTKIR_MTKIR_SWAP:
			return false;

		default:
			break;
		}

		// Return true to prevent this event from being propagated further
		return true;

	}

	private boolean keyEventNoResponse() {
		if (mShowSourceListView.isShowing()) {
			mHandler.removeMessages(MessageType.NAV_SOURCE_LISTVIEW_DIMISS);
			mHandler.sendEmptyMessageDelayed(
					MessageType.NAV_SOURCE_LISTVIEW_DIMISS,
					MessageType.delayMillis4);
			return true;
		}
		if (mNumputChangeChannel) {
			cancelNumChangeChannel();
			return true;
		}
		return false;
	}

	private void init() {

		mCICardDelayNotification =CICardDelayNotification.getInstance();
		mCAMManager = TVContent.getInstance(this).getCAMManager();
		mCAMManager.registerCamStatusListener(mCamStatusListener);

		mShowChannelListView = new ShowChannelListView(this);
		//mShowChannelTypeView = new ShowChannelTypeView(this);
		mShowFavoriteChannelListView = new ShowFavoriteChannelListView(this);
		mShowSourceListView = new ShowSourceListView(this);

		mInputPwdView = new InputPwdDialog(this);
		mAdjustVolLayout = (LinearLayout) findViewById(R.id.nav_volume_layout);
		adjustVolumeView = (AdjustVolumeView) findViewById(R.id.nav_adjust_volume_view);
		adjustVolumeView.setHandler(mHandler);

		mSundryLayout = (LinearLayout) findViewById(R.id.nav_sundry_layout);
		mNavSundryShowTextView = (NavSundryShowTextView) findViewById(R.id.nav_tv_shortTip_textview);
		mNavSundryShowTextView.setHandler(mHandler);

		mBannerView = (BannerView) findViewById(R.id.nav_banner_info_bar);
		// mBannerView.hideAllBanner();
		mBannerView.setHandler(mHandler);
		mBannerView.setBannerState(BannerView.CHILD_TYPE_INFO_SIMPLE);
		mShowSourceListView.setBannerView(mBannerView);
		mInputPwdView.setBannerView(mBannerView);
		mNavSundryShowTextView.setBannerView(mBannerView);

		mSpecial_SnowTextView = (SnowTextView) findViewById(R.id.nav_stv_special_model);		
		mCheckLockSignalChannelState = CheckLockSignalChannelState
				.getInstance(this);
		mCheckLockSignalChannelState.setHandler(mHandler);
		mCheckLockSignalChannelState.setSnowTextView(mSpecial_SnowTextView);
		mCheckLockSignalChannelState.setBannerView(mBannerView);
		mZoomView = (ZoomView) findViewById(R.id.nav_zoomview);
		mZoomView.setmHandler(mHandler);
		mTipEnterAnimation = AnimationUtils.loadAnimation(this,
				R.anim.zoom_enter);
	}

	private void initCheckPowTimer() {
		POSTManager.getInstance(this).checkPowOnTimer();
		POSTManager.getInstance(this).checkPowOffTimer();
		POSTManager.getInstance(this).checkSleepTime();
		POSTManager.getInstance(this).checkAutoSleep();
	}

	public void startupConnect() {
		final NetworkManager networkManager = NetworkManager
		.getInstance(this);
		if (saveV.readValue(MenuConfigManager.NETWORK_CONNECTION) == 1
				&& saveV.readValue(MenuConfigManager.NETWORK_INTERFACE) == 1) {
			new Thread(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub
					 networkManager.wifiAutoConnect();
				}
			}).start();
		} else if (saveV.readValue(MenuConfigManager.NETWORK_CONNECTION) == 0
				&& saveV.readValue(MenuConfigManager.NETWORK_INTERFACE) == 0) {
			new Thread(new Runnable() {
				public void run() {
					// TODO Auto-generated method stub
					 networkManager.closeEther();
				}
			}).start();
		}
	}

	private void registeListeners() {
		mNavIntegration.iAddChSelectListener(mChannelSelectListener);
		mNavIntegration.iAddInputsourceChangeListener(mSourceChangeListener);
		// mModel_Parental_InputPwd
		// .addTextChangedListener(passwordInputTextWatcher);
	}

	private void unregisteListeners() {
		mNavIntegration.iRemoveChSelectListener(mChannelSelectListener);
		mNavIntegration.iRemoveInputsourceChangeListener(mSourceChangeListener);
	}
	
	
	//lkm
	private void getInputSourceStatus() {
		
		InputSignalStatus stat;
		TVManager tvMngr = null;
		InputService inpSrv = null;
		
		String input[] = {"av0", "av1", "component0", "hdmi0", "hdmi1", "vga0"};
    int index;
    
    tvMngr = TVManager.getInstance(getApplicationContext());
    inpSrv = (InputService) tvMngr
					.getService(InputService.InputServiceName);
		try {			
			for(index=0; index<6; index++) {
			    stat = inpSrv.getInputSignalStatus(input[index]);
			    if (stat == InputSignalStatus.SignalStatusLocked) {
				
				  MtkLog.i(TAG, "lkm******input:***"+input[index]+"**hassignal:true**" );
			    } else {
				  MtkLog.i(TAG, "lkm******input:***"+input[index]+"**hassignal:false**" );
			    }
			  }
			
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	private void registerSourceListener() {
		
		MtkLog.d(TAG,"registerSourceListener");
		mInputSourceListener = new InputSourceListener() {
			
			@Override
			public void onBlocked(String input) {
				
				MtkLog.d(TAG, "==========onBlocked===input:" + input);
				
			}
			
			@Override
			public void onInputGotSignal(String input) {
				
				MtkLog.d(TAG, "=========onInputGotSignal===input:" + input);
				
			}
			
			@Override
			public void onOutputSignalChange(String output, boolean hasSignal) {
				
				MtkLog.d(TAG, "==========onOutputSignalChange===output:" + output + " hasSignal:" + hasSignal);
				
			}
			
			@Override
			public void onSelected(String output, String input) {
				
				MtkLog.d(TAG, "===========onSelected===output:" + output + " input:" + input);
				
			}
			
			@Override
			public void onInputSignalChange(String input, boolean hasSignal) {
				
				MtkLog.d(TAG, "===========onInputSignalChange====input:" + input + " hasSignal:" + hasSignal);
				
			}
			
			
		};
		
		inpMgr.registerSourceListener(mInputSourceListener);
		
	}
	
	
	private void unregisterSourceListener() {
		
		MtkLog.d(TAG, "unregisterSourceListener");
		inpMgr.removeSourceListener(mInputSourceListener);
		mInputSourceListener = null;
	}
	//lkm

	private void initMuteImageView() {
		mMuteImageView = new ImageView(getApplicationContext());
		mMuteImageView.setImageResource(R.drawable.nav_mute);
		mMuteImageView.setBackgroundResource(R.drawable.translucent_background);
		wm = (WindowManager) getApplicationContext().getSystemService(
				WINDOW_SERVICE);
		wmParams = new LayoutParams();
		wmParams.type = LayoutParams.TYPE_PHONE;
		wmParams.flags |= LayoutParams.FLAG_NOT_FOCUSABLE;

		wmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
		wmParams.width = LayoutParams.WRAP_CONTENT;
		wmParams.height = LayoutParams.WRAP_CONTENT;
		wmParams.format = android.graphics.PixelFormat.TRANSPARENT;
		mMuteImageView.setVisibility(View.GONE);
		wm.addView(mMuteImageView, wmParams);
	}

	public static void setMuteGone() {
		mMuteImageView.setVisibility(View.GONE);
	}

	public static void setMuteVisible() {
		mMuteImageView.setVisibility(View.VISIBLE);
	}

	public static void cancelMute() {
		mNavIntegration.iSetMute(false);
		mMuteImageView.setVisibility(View.GONE);

	}

	IChannelSelectorListener mChannelSelectListener = new IChannelSelectorListener() {

		public void signalChange(boolean hasSignal) {
			MtkLog.v(TAG, "signal has changed  , hasSignal is  " + hasSignal);
			mSpecial_SnowTextView.setVisibility(View.GONE);
			mBannerView.hideAllBanner();
			if (!mSetupFlag) {
				mBannerView.show(false, -1, false);
			}
			hideInputPwdView();
		}

		public void updateChannel(TVChannel ch) {
			MtkLog.v(TAG, "updateChannel");
			mCICardDelayNotification.clearDelayNotifications();
			mCICardDelayNotification.setPushToQueue(false);
			mSpecial_SnowTextView.setVisibility(View.GONE);
			if(mNavIntegration.isCurrentChannelDTV()){
				MtkLog.d("updateChannel","come in updateChannel 2");
				mNavIntegration.resetSubtitle();
			}
			mBannerView.show(false, BannerView.CHILD_TYPE_INFO_SIMPLE, false);
			hideInputPwdView();
			NavIntegrationZoom.getInstance(TurnkeyUiMainActivity.this)
					.setZoomMode(NavIntegrationZoom.ZOOM_1);
			mHandler.removeMessages(MessageType.NAV_CURRENT_CHANNEL_LOCKED);
		}

		public void channelLocked() {
			// TODO Auto-generated method stub
			MtkLog.v(TAG, "channelLocked");
			mCICardDelayNotification.clearDelayNotifications();
			mCICardDelayNotification.setPushToQueue(true);
			mHandler.removeMessages(MessageType.NAV_CURRENT_CHANNEL_LOCKED);
			mHandler.sendEmptyMessageDelayed(
					MessageType.NAV_CURRENT_CHANNEL_LOCKED,
					MessageType.delayMillis6);
		}

		public void channelScrambled(boolean hasScramble, String state) {
			// TODO Auto-generated method stub
			if (hasScramble) {
			} else {
			}

			mSpecial_SnowTextView.setVisibility(View.GONE);
			mBannerView.show(false, -1, false);
		}

	};
	NavIntegration.IInputSourceChangeListener mSourceChangeListener = new NavIntegration.IInputSourceChangeListener() {

		public void isBlocked(String input) {
			// INPUT SOURCE LOCKE
			MtkLog.i(TAG, "isBlocked !   " + input);
			// service for ci,when source is block
			if (mNavIntegration.isCurrentSourceTv()) {
				mCICardDelayNotification.clearDelayNotifications();
				mCICardDelayNotification.setPushToQueue(true);
			}
			mHandler.removeMessages(MessageType.NAV_CURRENT_SOURCE_LOCKED);
			mHandler.sendEmptyMessage(MessageType.NAV_CURRENT_SOURCE_LOCKED);
		}

		public void isInputGotSignal(String input) {
			// INPUT SOURCE HAS SIGAL
			// INPUT IS INOUT SOURCE NAME
			MtkLog.i(TAG, "isInputGotSignal !   " + input);
			mReturnFromThirdApp = true;
			mBannerView.show(false, -1, false);
		}

		public void isOutputSignalChange(String output, boolean hasSignal) {
			// OUTPUT IS MAIN / SUB
			MtkLog.i(TAG, "isOutputSignalChange " + hasSignal);
			mBannerView.show(false, -1, false);
			if ("main".equalsIgnoreCase(output)) {
				mSpecial_SnowTextView.setVisibility(View.GONE);
				mBannerView.show(false, -1, false);

				// mToastInfo.toastVGAInfo();
			} else if ("sub".equalsIgnoreCase(output)) {
				if (hasSignal) {
					// SUB OUTPUT HAS SIGNAL
				} else {
					// SUB OUTPUT NO SIGNAL
				}

			}
			hideInputPwdView();
			
			//resolve CR DTV00371791 by jun gu 
			if (mNavIntegration.isCurrentSourceVGA()) {
				if (hasSignal) {
					mToastInfo.cancelVGAInfo();
				} 
			}
		}

		public void isSelected(String output, String input) {
			MtkLog.i(TAG, "isSelected " + input);
			mSpecial_SnowTextView.setVisibility(View.GONE);
			mNavIntegration.resetSubtitle();
			if (mNavIntegration.isCurrentSourceTv()
					&& mNavIntegration.iGetChannelLength() <= 0) {
				mBannerView.show(false, -1, false);

			} else {
				mBannerView.showSimpleBar();
			}
			mCICardDelayNotification.clearDelayNotifications();
			mCICardDelayNotification.setPushToQueue(false);
			MtkLog.i("source", "isSelected !   " + output + "  input   "
					+ input);
		}
	};

	private TVCAMManager.CamStatusUpdateListener mCamStatusListener = new TVCAMManager.CamStatusUpdateListener() {

		public void camSystemIDStatusUpdated(byte sysIdStatus) {

		}

		// send remove broadcast
		public void camRemoveUpdated() {
			Log.d(TAG,"camRemoveUpdated----------------------->"+mCICardDelayNotification.isPushToQueue());
			if(mCICardDelayNotification.isPushToQueue()){
				mCICardDelayNotification.addDelayNotifications(new Runnable() {
					
					@Override
					public void run() {
						mCamStatusListener.camRemoveUpdated();
					}
				});
				return;
			}
			mJump = false;
			mCIStatus = false;
			mCAMManager.clearMenuEnqUpdateListener();
			Intent intent = new Intent(CISTATUS_REMOVE);
			sendBroadcast(intent);
		}

		// turn to menu-CI-name
		public void camNameUpdated(final String camName) {
			Log.d(TAG,"------camNameUpdated------------------>"+mCICardDelayNotification.isPushToQueue());
			if(mCICardDelayNotification.isPushToQueue()){
				mCICardDelayNotification.addDelayNotifications(new Runnable() {
					
					@Override
					public void run() {
						mCamStatusListener.camNameUpdated(camName);
					}
				});
				return ;
			}
			boolean hasTV = false;
			for (String output : inpMgr.getOutputArray()) {
				String input = inpMgr.getCurrInputSource(output);
				if (input != null
						&& TVInputManager.INPUT_TYPE_TV.equals(
								inpMgr.getTypeFromInputSource(input))) {
					hasTV = true;
				}
			}

			if (!hasTV) {
				return;
			}
			if (Util.getMMPFlag()) {
				return;
			}
			if (mJump == false && MenuMain.mScanningStatus == false) {
				mCIStatus = true;
				mJump = true;
				Bundle bundle = new Bundle();
				bundle.putInt("CIValue", 1);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(TurnkeyUiMainActivity.this, MenuMain.class);
				startActivity(intent);
			}
		}

		public void camInsertUpdated() {
			Log.d(TAG,"camInsertUpdated----------------------->"+mCICardDelayNotification.isPushToQueue());
		}
	};


	private void inputChannelNum(int keycode) {
		mNumputChangeChannel = true;
		mHandler.removeMessages(MessageType.NAV_NUMKEY_CHANGE_CHANNEL);

		if (keycode >= KeyMap.KEYCODE_0 && keycode <= KeyMap.KEYCODE_9) {
			inputChannelNumStrBuffer.append("" + (keycode - KeyMap.KEYCODE_0));
		}

		// show data at panal

		String showString = "";
		if (inputChannelNumStrBuffer.length() <= 4) {
			showString = inputChannelNumStrBuffer.toString();
		} else {
			showString = inputChannelNumStrBuffer.substring(
					inputChannelNumStrBuffer.length() - 4,
					inputChannelNumStrBuffer.length());
		}
		mSelectedChannelNum = Short.valueOf(showString);
		mHandler.sendEmptyMessageDelayed(MessageType.NAV_NUMKEY_CHANGE_CHANNEL,
				MessageType.delayMillis6);

	}

	public void hideAll() {
		hideChannelList();
		hideTypeList();
		hideFavoriteList();
		hideSourceList();
		hideZoomView();
		hideAdustVolLayout();
		hideSundryLayout();
		hideInputPwdView();
		mBannerView.hideAllBanner();
		mSpecial_SnowTextView.setVisibility(View.GONE);
	}

	public void hideAllOnSource() {
		hideChannelList();
		hideTypeList();
		hideFavoriteList();
		hideSourceList();
		hideZoomView();
		hideAdustVolLayout();
		hideSundryLayout();
		hideInputPwdView();
		mSpecial_SnowTextView.setVisibility(View.GONE);
	}
	
	public void hideAllOnBannerDismiss(){		
		hideZoomView();			
		hideInputPwdView();
		hideAdustVolLayout();
		mSpecial_SnowTextView.setVisibility(View.GONE);
	}

	private void hideInputPwdView() {
		if (mInputPwdView != null && mInputPwdView.isShowing()) {
			mInputPwdView.dismiss();
		}
	}

	private void hideAdustVolLayout() {
		if (mAdjustVolLayout != null
				&& mAdjustVolLayout.getVisibility() != View.GONE) {
			mHandler.removeMessages(MessageType.NAV_ADUST_VOLUME_DIMISS);
			unlockAdjustVolLayoutRect();
		}
	}

	private void hideZoomView() {
		if (mZoomView != null && mZoomView.getVisibility() != View.GONE) {
			mHandler.removeMessages(MessageType.NAV_ZOOMVIEW_DIMISS);
			mZoomView.setVisibility(View.GONE);
		}
	}

	private void hideSundryLayout() {
		if (mSundryLayout != null && mSundryLayout.getVisibility() != View.GONE) {
			mHandler.removeMessages(MessageType.NAV_SHORTTIP_TEXTVIEW_DIMISS);
			unlockSundryLayoutRect();
		}
	}

	private void unlockSundryLayoutRect() {
		if (mSundryLayout.getVisibility() != View.GONE) {
		mSundryLayout.setVisibility(View.GONE);
		mBypassWindowManager.setBypassWindow(false, mSundryLayoutWindowId,
				mSundryLayoutRect);
	}
}
	private void getVolumeViewRec() {
		mAdjustVolumeViewWindowId = mBypassWindowManager.getAvailableWindowId();
		if (!isAdjustVolumeViewFirst) {
			mBypassWindowManager.setBypassWindow(true,
					mAdjustVolumeViewWindowId, mAdjustVolumeViewRect);
		} else {
			ViewTreeObserver vto = mAdjustVolLayout.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					if (isAdjustVolumeViewFirst) {
						isAdjustVolumeViewFirst = false;
						mAdjustVolumeViewRect = new Rect();
						adjustVolumeView
								.getGlobalVisibleRect(mAdjustVolumeViewRect);
						mBypassWindowManager.setBypassWindow(true,
								mAdjustVolumeViewWindowId,
								mAdjustVolumeViewRect);
					}
				}
			});
			
		}
	}
	private static void unlockAdjustVolLayoutRect() {
		if (mAdjustVolLayout.getVisibility() != View.GONE) {
			if(AnimationManager.getInstance().getIsAnimation()){
			AnimationManager.getInstance().adjustVolExitAnimation( mAdjustVolLayout, new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mAdjustVolLayout.setVisibility(View.GONE);
					super.onAnimationEnd(animation);
				}
			});
			}else{
				mAdjustVolLayout.setVisibility(View.GONE);
			}
			MtkLog.i(TAG, "hide mAdjustVolLayout unlock");
			mBypassWindowManager.setBypassWindow(false,
					mAdjustVolumeViewWindowId, mAdjustVolumeViewRect);
		}
	}
	private void getSundryLayoutRec() {
		mSundryLayoutWindowId = mBypassWindowManager.getAvailableWindowId();
		if (mSundryLayoutRect != null) {
			mBypassWindowManager.setBypassWindow(true, mSundryLayoutWindowId,
					mSundryLayoutRect);
		} else {
			ViewTreeObserver vto = mNavSundryShowTextView.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					if (isSundryLayoutFirst) {
						isSundryLayoutFirst = false;
						mSundryLayoutRect = new Rect();
						mNavSundryShowTextView
								.getGlobalVisibleRect(mSundryLayoutRect);
						mBypassWindowManager.setBypassWindow(true,
								mSundryLayoutWindowId, mSundryLayoutRect);
					}
				}
			});
		}
	}

	private void hideChannelList() {
		if (mShowChannelListView != null && mShowChannelListView.isShowing()) {
			mShowChannelListView.exit();
		}
	}

	private void hideTypeList() {
		/*if (mShowChannelTypeView != null && mShowChannelTypeView.isShowing()) {
			mShowChannelTypeView.dismiss();
		}*/
	}

	private void hideFavoriteList() {
		if (mShowFavoriteChannelListView != null
				&& mShowFavoriteChannelListView.isShowing()) {
			mShowFavoriteChannelListView.dismiss();
		}
	}

	private void hideSourceList() {
		if (mShowSourceListView != null && mShowSourceListView.isShowing()) {
			mShowSourceListView.exit();
		}
		unlockAdjustVolLayoutRect();
	}

	private void cancelNumChangeChannel() {
		mNumputChangeChannel = false;
		mBannerView.hideAllBanner();
		mHandler.removeMessages(MessageType.NAV_NUMKEY_CHANGE_CHANNEL);
		inputChannelNumStrBuffer.replace(0, inputChannelNumStrBuffer.length(),
				"");
	}

	public static void switchMute() {
		// volume no set
		unlockAdjustVolLayoutRect();
		if (!mNavIntegration.iSetMute()) {
			mMuteImageView.setVisibility(View.GONE);
		} else {
			mMuteImageView.setVisibility(View.VISIBLE);
		}
	}

	public void setSnowTextTagByState(int state) {
		switch (state) {
		case BannerView.SPECIAL_NO_CHANNEL:
			mSpecial_SnowTextView.showSpecialView(ShowType.SPECIAL_PLEASE_SCAN);
			break;
		case BannerView.SPECIAL_NO_SIGNAL:
			mSpecial_SnowTextView.showSpecialView(ShowType.SPECIAL_NO_SIGNAL);
			break;
		case BannerView.SPECIAL_NO_SUPPORT:
			mSpecial_SnowTextView.showSpecialView(ShowType.SPECIAL_NO_SUPPORT);		
			break;
			default:
				break;
		}
	}

	void setTimeZone() {
		int mApkFirstRun = saveV.readValue("apkfirstrun");
		if (mApkFirstRun == 0) {
			saveV.saveValue("apkfirstrun", 5);
			AlarmManager alarm = (AlarmManager) this
					.getSystemService(Context.ALARM_SERVICE);
			alarm.setTimeZone("Asia/Shanghai");

		}
	}
	
	boolean isNormalForZoom(){
		return mNavIntegration.isCurrentSourceBlocking()
		|| mNavIntegration.isCurrentChannelBlocking()
		|| !mNavIntegration.iCurrentInputSourceHasSignal()
		|| !mNavIntegration.iCurrentChannelHasSignal()
		|| mNavIntegration.isAudioScrambled()
		|| mNavIntegration.isCurrentSourceHDMI()
		|| mZoomView.isNotShow()
		|| (mNavIntegration.iGetChannelLength() == 0)
//		|| !mNavIntegration.aspectRatioCanSet()
		|| mNavIntegration.isCurrentChannelRadio();
	}

	/*sync time from menu->auto synchorization and Launcher ->settings ->Date &time*/
	/*private void syncSysTime() {
		int autoSyncValue = SaveValue.getInstance(this).readValue(
				MenuConfigManager.AUTO_SYNC);
		int sysSyncValue = Settings.System.getInt(getContentResolver(),
				Settings.System.AUTO_TIME, 1);
		if (autoSyncValue == 2) {
			Settings.System.putInt(getContentResolver(),
					Settings.System.AUTO_TIME, 1);
		}else{
			Settings.System.putInt(getContentResolver(),
					Settings.System.AUTO_TIME, 0);
		}
		if (sysSyncValue == 1) {
			SaveValue.getInstance(this).saveValue(MenuConfigManager.AUTO_SYNC,
					2);
		}
	}*/
}
