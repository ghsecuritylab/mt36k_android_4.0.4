package com.mediatek.ui.mmp.multimedia;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mediatek.mmpcm.CommonStorage;
import com.mediatek.mmpcm.mmcimpl.Const;
import com.mediatek.mmpcm.text.ITextEventListener;
import com.mediatek.mmpcm.textimpl.TextConst;
import com.mediatek.ui.R;
import com.mediatek.ui.mmp.commonview.ControlView.ControlPlayState;
import com.mediatek.ui.mmp.model.MultiFilesManager;
import com.mediatek.ui.mmp.util.LogicManager;
import com.mediatek.ui.mmp.util.MultiMediaConstant;
import com.mediatek.ui.util.KeyMap;
import com.mediatek.ui.util.MtkLog;

public class TextPlayActivity extends MediaPlayActivity {

	private static final String TAG = "TextPlayActivity";

	private static final int MESSAGE_PLAY = 0;

	private static final int MESSAGE_POPHIDE = 1;

	private static final int MESSAGE_ONMEASURE = 2;
	
	private static final int MESSAGE_PLAY_NEXT = 3;
	
	private static final int MESSAGE_SKIP_TO_PAGE = 4;
	
	private static final int MESSAGE_DISMISS = 5;

	private static final int MESSAGE_POPSHOWDEL = 10000;

	private static final int DELAY_TIME = 6000;

	private static final int DELAY_REQUEST_TOTALPAGE = 500;
	
	private static final int DELAY_REQUEST_SKIPTOPAGE = 6000;
	
	private static final int MAX_SIZE= 10;

	private LinearLayout vLayout;

	private TextView vTextView;
	
	private ScrollView vScrollView;

	private int mTextSource = 0;
	
	/*true play, false pause*/
	private boolean mPlayStauts = true;
	private boolean isActivityLiving = true;
	//added by keke 1.5
	private boolean mSKIPPlayStauts = false;

	private ControlPlayState mControlImp = new ControlPlayState() {

		public void play() {
			mPlayStauts = true;
			mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY, DELAY_TIME);
		}

		public void pause() {
			mPlayStauts = false;
			mHandler.removeMessages(MESSAGE_PLAY);
		}
	};

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_PLAY:				
				mLogicManager.pageDown();
  				if(isFileNotSupport()){
  					mHandler.removeCallbacks(mTextPlayDely);
  					mHandler.postDelayed(mTextPlayDely, DELAY_TIME);
				}
				if(null != mControlView ){
					mControlView.setPhotoTimeType(mLogicManager.getPageNum());
				}
				sendEmptyMessageDelayed(MESSAGE_PLAY, DELAY_TIME);

				break;
			case MESSAGE_PLAY_NEXT:
				//mHandler.removeMessages(MESSAGE_PLAY);
				if (isActivityLiving) {
					dismissNotSupprot();
				}
				// changed by xudong 111207 fix DTV00380648
				try {
					menuDialog.dismiss();
					menuDialogFontList.dismiss();
				} catch (Exception ex) {
					// ignore the exception				
				}
				//end
				
				dismissNotSupprot();
				mLogicManager.playTextNext();
				reSetController();
				setControlView();
				//sendEmptyMessageDelayed(MESSAGE_PLAY, DELAY_TIME);
				break;
			case MESSAGE_DISMISS:
				dismissNotSupprot();
				break;
			case MESSAGE_ONMEASURE: {
				if (null != mControlView) {
					mControlView.setPhotoTimeType(mLogicManager.getPageNum());
				}
				if (mLogicManager.getTextCurrentPage() > 0) {
					return;
				}
				mHandler.sendEmptyMessageDelayed(MESSAGE_ONMEASURE,
						DELAY_REQUEST_TOTALPAGE);
			}
				break;
			case MESSAGE_SKIP_TO_PAGE:
				skipToPage();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * Set control bar info
	 */
	public void setControlView() {
		if (mControlView != null) {
			mControlView.setRepeat(Const.FILTER_TEXT);
			mControlView.setPhotoTimeType(mLogicManager.getPageNum());
			mControlView.setFileName(mLogicManager
					.getCurrentFileName(Const.FILTER_TEXT));
			mControlView.setFilePosition(mLogicManager.getTextPageSize());
		}
		if(null!=mInfo && mInfo.isShowing())
		{
			mInfo.setTextView();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mmp_textplay);
		findView();
		getIntentData();
		init();
		//add by keke for fix DTV00381264
		mControlView.setRepeatVisibility(Const.FILTER_TEXT);
		showPopUpWindow(vLayout);
	}

	private void init() {
		int screenHeight = getWindowManager().getDefaultDisplay().getRawHeight();
		
		mLogicManager = LogicManager.getInstance(this);
		mLogicManager.setTextListener(mTextEventListener);
		mLogicManager.initText(mTextSource, vTextView, vScrollView, screenHeight);
		
	/*remove by lei for not support file*/		
//		mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY, DELAY_TIME);
//		mHandler.sendEmptyMessageDelayed(MESSAGE_ONMEASURE,
//				DELAY_REQUEST_TOTALPAGE);
		initFontStyle();
		setControlView();
	}


	private ITextEventListener mTextEventListener = new ITextEventListener(){
		public void fileNotSupport(){
			mHandler.postDelayed(mTextPlayDely, DELAY_TIME);
			setIsFileNotSupport(true);
			onNotSuppsort(TextPlayActivity.this.getResources().getString(
					R.string.mmp_file_notsupport));
		}

		public void onComplete() {
			// modified by keke for fix DTV00381257
			if (mPlayStauts) {
				mHandler.sendEmptyMessage(MESSAGE_PLAY_NEXT);

				try {
					dismissNotSupprot();
				} catch (Exception ex) {
					// ignore the exception
					ex.printStackTrace();
				}
			}

		}

		public void onExit() {
			finish();
		}

	/*file start play by add*/		
		public void onPrepare() {
			if (mPlayStauts) {
				if (mHandler.hasMessages(MESSAGE_PLAY)) {
					mHandler.removeMessages(MESSAGE_PLAY);
				}
				mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY, DELAY_TIME);
			}
			mHandler.sendEmptyMessageDelayed(MESSAGE_ONMEASURE,
					DELAY_REQUEST_TOTALPAGE);
			setIsFileNotSupport(false);
		}	
	};
	
	private TextPlayDelay mTextPlayDely = new TextPlayDelay();
	private class TextPlayDelay implements Runnable {
		public void run() {
			if (mPlayStauts){
				//mHandler.sendEmptyMessage(MESSAGE_PLAY_NEXT);
				//changed by xudong 111207 fix DTV00380648
				if(isActivityLiving == true){
					mHandler.sendEmptyMessage(MESSAGE_PLAY_NEXT);
				}
				//end
			}
		}
	}
	
	private void getIntentData() {
		
		mTextSource = MultiFilesManager.getInstance(this)
				.getCurrentSourceType();

		switch (mTextSource) {
		case MultiFilesManager.SOURCE_LOCAL:
			mTextSource = TextConst.PLAYER_MODE_LOCAL;
			break;
		case MultiFilesManager.SOURCE_SMB:
			mTextSource = TextConst.PLAYER_MODE_SAMBA;
			break;
		case MultiFilesManager.SOURCE_DLNA:
			mTextSource = TextConst.PLAYER_MODE_DLNA;
			break;
		default:
			break;
		}
	}

	private void findView() {
		vLayout = (LinearLayout) findViewById(R.id.mmp_text);
		vTextView = (TextView) findViewById(R.id.mmp_text_show);
		vScrollView = (ScrollView)findViewById(R.id.mmp_scroll_show);
		getPopView(R.layout.mmp_popuptext, MultiMediaConstant.TEXT, mControlImp);
		
		vScrollView.setOnKeyListener(mOnkeyListener);
	}
	
	private OnKeyListener mOnkeyListener = new OnKeyListener() {

		public boolean onKey(View v, int keyCode, KeyEvent event) {

			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
				case KeyMap.KEYCODE_DPAD_UP:
				case KeyMap.KEYCODE_DPAD_DOWN:
					TextPlayActivity.this.onKeyDown(keyCode, event);
					return true;
				}
			}
			return false;
		}
	};
	
	
	/**
	 * {@inheritDoc}
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		MtkLog.d(TAG, "onKeyDown:" + keyCode);
		
		switch (keyCode) {
		//added by keke 1.5 for cr DTV00388058
		case KeyMap.KEYCODE_DPAD_CENTER:
			if (mHandler.hasMessages(MESSAGE_SKIP_TO_PAGE)) {
				mHandler.removeMessages(MESSAGE_SKIP_TO_PAGE);
				skipToPage();
				return true;
			}
			break;
		//end 
		case KeyMap.KEYCODE_DPAD_UP:			
			reSetController();
			mLogicManager.scrollLnUp();
			setPageSize();
			return true;
		case KeyMap.KEYCODE_DPAD_DOWN:
			reSetController();
			mLogicManager.scrollLnDown();
			setPageSize();
			return true;
		case KeyMap.KEYCODE_DPAD_LEFT:
			reSetController();
			mLogicManager.pageUp();
			setPageSize();
			return true;
		case KeyMap.KEYCODE_DPAD_RIGHT:
			reSetController();
			mLogicManager.pageDown();
			setPageSize();
			return true;
		case KeyMap.KEYCODE_MTKIR_PREVIOUS:
		case KeyMap.KEYCODE_MTKIR_CHDN:
			if (!isValid()){
				return true;
			}
			dismissNotSupprot();
			reSetController();
			mLogicManager.playTextPrev();
			setFontSize();
			setFontColor();
			setFontStyle();
			setControlView();
			return true;
		case KeyMap.KEYCODE_MTKIR_NEXT:
		case KeyMap.KEYCODE_MTKIR_CHUP:
			if (!isValid()){
				return true;
			}
			mHandler.removeCallbacks(mTextPlayDely);
			dismissNotSupprot();
			reSetController();
			mLogicManager.playTextNext();
			setFontSize();
			setFontColor();
			setFontStyle();
			setControlView();
			return true;
		case KeyMap.KEYCODE_VOLUME_DOWN:
		case KeyMap.KEYCODE_VOLUME_UP:
		case KeyMap.KEYCODE_MTKIR_MUTE: {
			if (null!=mLogicManager.getAudioPlaybackService()) {
				currentVolume=mLogicManager.getVolume();
				maxVolume=mLogicManager.getMaxVolume();
				break;
			} else {
				return true;
			}
		}
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
			reSetController();
			parseSkipToPage(keyCode);
			break;
			
		case KeyMap.KEYCODE_BACK: {
			finish();
			break;
		}	
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}
	
//	public void onStop(){
//		removeMessage();
//		super.onStop();
//		
//	}
	
	private void removeMessage() {
		mHandler.removeMessages(MESSAGE_PLAY);
		mHandler.removeMessages(MESSAGE_ONMEASURE);
		mHandler.removeMessages(MESSAGE_PLAY_NEXT);
		mHandler.removeMessages(MESSAGE_DISMISS);
		dismissNotSupprot();
	}

	private int mSkipPage = 0;
	private ArrayList<Integer> mPageNum = new ArrayList<Integer>();
	protected void parseSkipToPage(int keyCode){
		int pageNum;
		if (keyCode < KeyMap.KEYCODE_0 || keyCode > KeyMap.KEYCODE_9){
			return;
		}
		pageNum = keyCode - KeyMap.KEYCODE_0;

		mPageNum.add(new Integer(pageNum));
		if (setSkipToPage(getSkipPage())) {
			mHandler.removeMessages(MESSAGE_SKIP_TO_PAGE);
			mHandler.sendEmptyMessageDelayed(MESSAGE_SKIP_TO_PAGE,
					DELAY_REQUEST_SKIPTOPAGE);
			
			// added by keke 1.05 for cr DTV00388058 and  DTV00387862
			if (mHandler.hasMessages(MESSAGE_PLAY)) {
				mHandler.removeMessages(MESSAGE_PLAY);
				mSKIPPlayStauts = true;
			}
			//end
		}
	}
	/**
	 * Get to skip to page.
	 * @return
	 */
	protected int getSkipPage(){
		int pageNum = 0;
		for (Integer p : mPageNum){
			pageNum *= 10;
			pageNum += p.intValue();
		}
		if (pageNum > Integer.MAX_VALUE){
			pageNum = Integer.MAX_VALUE;
		}
		if (pageNum <= getTotalPage()){
			mSkipPage = pageNum;
		}
		return mSkipPage;
	}
	
	protected void skipToPage(){		
		int pageNum = mSkipPage;
		if (pageNum <= 0){
			return;
		}
		mPageNum.clear();
		reSetController();
		mLogicManager.skipToPage(pageNum);
		setPageSize();	
		// added by keke 1.5 for fix cr DTV00388058 and  DTV00387862
		if (mSKIPPlayStauts) {
			mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY, DELAY_TIME);
			mSKIPPlayStauts = false;
		}
		//end
	}
	/**
	 * May be skip to page.
	 * @param num
	 * @return true to success, flase to failed
	 */
	protected boolean setSkipToPage(int num) {
		if (mControlView != null) {
			String result = "";
			int currentPos = num;
			int count = getTotalPage();
			if (currentPos > 0 && count > 0 && num <= count) {
				result = currentPos + "/" + count;
				mControlView.setPhotoTimeType(result);
				return true;
			} 
		}
		
		return false;
	}
	
	protected int getTotalPage(){
		return mLogicManager.getTextTotalPage();
	}
	
	protected void reflashPageNumber(){
		if (mHandler != null){
			mHandler.sendEmptyMessage(MESSAGE_ONMEASURE);
		}
	}
	
	private void setPageSize() {
		if (mControlView != null) {
			mControlView.setPhotoTimeType(mLogicManager.getPageNum());
		}
	}

	private void initFontStyle(){

		switch (mLogicManager.getFontStyle()) {
		case 1:	
			mLogicManager.setFontStyle("default", "ITALIC");
			break;
		case 2:	
			mLogicManager.setFontStyle("default", "BOLD");
			break;
		case 3:	
			mLogicManager.setFontStyle("default", "BOLD_ITALIC");
			break;
		default:
			mLogicManager.setFontStyle("default", "regular");
			break;
		}
	}
	private boolean mIsTextFileNotSupport;
	protected boolean isFileNotSupport() {
		return mIsTextFileNotSupport;
	}
	/**
	 * Determine the file type not supported,
	 *  
	 * @param isFileNotSupport, true is not support, false, support
	 */
	protected void setIsFileNotSupport(boolean isFileNotSupport) {
		mIsTextFileNotSupport = isFileNotSupport;
	}
	
	@Override
	protected void onDestroy() {
		
		isActivityLiving = false; 
		
		removeMessage();
		super.onDestroy();
	}
	private void setFontStyle() {
		String style = CommonStorage.getInstance(this).get(TEXT_FONTSTYLE, "0");
		if (style.equalsIgnoreCase("Normal")) {
			mLogicManager.setFontStyle("default", "Normal");
		} else if (style.equalsIgnoreCase("ITALIC")) {
			mLogicManager.setFontStyle("default", "ITALIC");
		} else if (style.equalsIgnoreCase("BOLD")) {
			mLogicManager.setFontStyle("default", "BOLD");
		} else if (style.equalsIgnoreCase("BOLD_ITALIC")) {
			mLogicManager.setFontStyle("default", "BOLD_ITALIC");
		}
	}

	private void setFontColor() {
		String color = CommonStorage.getInstance(this).get(TEXT_FONTCOLOR, "0");
		if (color.equalsIgnoreCase("red")) {
			mLogicManager.setFontColor("red");
		} else if (color.equalsIgnoreCase("green")) {
			mLogicManager.setFontColor("green");
		} else if (color.equalsIgnoreCase("black")) {
			mLogicManager.setFontColor("black");
		} else if (color.equalsIgnoreCase("white")) {
			mLogicManager.setFontColor("white");
		} else if (color.equalsIgnoreCase("blue")) {
			mLogicManager.setFontColor("blue");
		}
	}
}
