package com.mediatek.ui.menu.commonview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mediatek.ui.R;
import com.mediatek.ui.menu.MenuMain;
import com.mediatek.ui.menu.adapter.SetConfigListViewAdapter;
import com.mediatek.ui.menu.util.NetWork;

/**
 * 
 * used for IP enter,
 * 
 */

public class IPInputBox extends ListViewItemView implements RespondedKeyEvent {
	public TextView mTextViewName;
	public EditText mEditText1;
	public EditText mEditText2;
	public EditText mEditText3;
	public EditText mEditText4;
	private int mCurrentFocuseIndex = -1;
	private LinearLayout mChildLinearLayout;
	private RelativeLayout mChildRelativeLayout;
	private LinearLayout mSecondChildLinearLayout;
	private OnValueChangedListener mValueChangedListener;
	private EditText mCurrentEditText;
	private Handler handler;
	private boolean[] clear;
	private NetWork netWork;

	class EditOnTouchListener implements OnTouchListener {

		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			clearAllEditFocus();
			mCurrentFocuseIndex = -1;
			((MenuMain) context).seekMouthFocus();
			return false;
		}
	}

	EditOnTouchListener editOnTouchListener = new EditOnTouchListener();

	public void clearAllEditFocus() {
		for (int i = 0; i < 4; i++) {
			mSecondChildLinearLayout.getChildAt(i).setBackgroundResource(
					R.drawable.ipinputview_normal);
			mCurrentFocuseIndex = -1;
		}
	}

	public IPInputBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public IPInputBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public IPInputBox(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	private void init() {
//		addView(inflate(mContext, R.layout.menu_inputbox_view, null));
		LinearLayout lv = (LinearLayout) inflate(mContext,
				R.layout.menu_inputbox_view, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(lv, params);

		mTextViewName = (TextView) findViewById(R.id.common_textview);
		mEditText1 = (EditText) findViewById(R.id.common_edittext1);
		mEditText2 = (EditText) findViewById(R.id.common_edittext2);
		mEditText3 = (EditText) findViewById(R.id.common_edittext3);
		mEditText4 = (EditText) findViewById(R.id.common_edittext4);
		mEditText1.setOnTouchListener(editOnTouchListener);
		mEditText2.setOnTouchListener(editOnTouchListener);
		mEditText3.setOnTouchListener(editOnTouchListener);
		mEditText4.setOnTouchListener(editOnTouchListener);
		mChildRelativeLayout = (RelativeLayout) this.getChildAt(0);
		mChildLinearLayout = (LinearLayout) mChildRelativeLayout.getChildAt(0);
		mSecondChildLinearLayout = (LinearLayout) mChildLinearLayout
				.getChildAt(1);
		netWork = NetWork.getInstance(mContext);
	}

	public void setAdapter(SetConfigListViewAdapter.DataItem mDataItem) {
		this.mDataItem = mDataItem;
		mTextViewName.setText(mDataItem.getmName());
		if (mDataItem.mOptionValue != null
				&& mDataItem.mOptionValue.length == 4) {
			if (("manual").equals(netWork.getConnectMode())) {
				mEditText1.setText(mDataItem.mOptionValue[0]);
				mEditText2.setText(mDataItem.mOptionValue[1]);
				mEditText3.setText(mDataItem.mOptionValue[2]);
				mEditText4.setText(mDataItem.mOptionValue[3]);
			}else{
				mEditText1.setText("0");
				mEditText2.setText("0");
				mEditText3.setText("0");
				mEditText4.setText("0");
			}
		}
		if(mDataItem.mOptionValue != null){
			clear = new boolean[mDataItem.mOptionValue.length];
			for (int i = 0; i < mDataItem.mOptionValue.length; i++) {
				clear[i] = true;
			}
		}
	}

	private boolean validate(int mCurrentFocuseIndex) {
		if (mCurrentFocuseIndex != -1) {
			String ipStr = mCurrentEditText.getText().toString();
			int num = -1;
			if (ipStr != null) {
				try {
					num = Integer.valueOf(ipStr);
				} catch (NumberFormatException e) {
					num = -1;
				}
				if (mCurrentFocuseIndex == 0) {
					if (mDataItem.getmName().equals(
							getResources().getString(
									R.string.menu_setup_subnetwork_mask))) {
						if (num >= 0 && num <= 255) {
							return true;
						} else {
							handler = new Handler();
							ToastDialog toast = new ToastDialog(mContext);
							toast
									.setText(mContext
											.getString(R.string.menu_setup_ip_error_warning2));
							toast.setPositon(120, 100);
							handler.post(toast);
							return false;
						}
					} else {
						if (num >= 1 && num <= 233) {
							return true;
						} else {
							handler = new Handler();
							ToastDialog toast = new ToastDialog(mContext);
							toast
									.setText(mContext
											.getString(R.string.menu_setup_ip_error_warning1));
							toast.setPositon(120, 100);
							handler.post(toast);
							return false;
						}
					}

				} else {
					if (num >= 0 && num <= 255) {
						return true;
					} else {
						handler = new Handler();
						ToastDialog toast = new ToastDialog(mContext);
						toast
								.setText(mContext
										.getString(R.string.menu_setup_ip_error_warning2));
						toast.setPositon(120, 100);
						handler.post(toast);
						return false;
					}
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public void onKeyLeft() {
		if (validate(mCurrentFocuseIndex)) {
			if (mCurrentFocuseIndex != -1) {
				// mSecondChildLinearLayout.getChildAt(mCurrentFocuseIndex)
				// .setBackgroundResource(
				// R.drawable.tk_cm_munuitem_bg);
				mSecondChildLinearLayout.getChildAt(mCurrentFocuseIndex)
						.setBackgroundResource(R.drawable.ipinputview_normal);
				clear[mCurrentFocuseIndex] = true;
			}
			if (mCurrentFocuseIndex > 0) {
				--mCurrentFocuseIndex;
			} else {
				mCurrentFocuseIndex = 3;
			}
			// mSecondChildLinearLayout.getChildAt(mCurrentFocuseIndex)
			// .setBackgroundResource(R.drawable.tk_cm_item_selected);
			mSecondChildLinearLayout.getChildAt(mCurrentFocuseIndex)
					.setBackgroundResource(R.drawable.ipinputview_hight);
		} else {
			// mSecondChildLinearLayout.getChildAt(mCurrentFocuseIndex)
			// .setBackgroundResource(R.drawable.tk_cm_item_selected);
			mSecondChildLinearLayout.getChildAt(mCurrentFocuseIndex)
					.setBackgroundResource(R.drawable.ipinputview_hight);
		}
		mCurrentEditText = (EditText) mSecondChildLinearLayout
				.getChildAt(mCurrentFocuseIndex);
	}

	public void onKeyRight() {
		if (validate(mCurrentFocuseIndex)) {
			if (mCurrentFocuseIndex != -1) {
				// mSecondChildLinearLayout.getChildAt(mCurrentFocuseIndex)
				// .setBackgroundResource(
				// R.drawable.tk_cm_munuitem_bg);
				mSecondChildLinearLayout.getChildAt(mCurrentFocuseIndex)
						.setBackgroundResource(R.drawable.ipinputview_normal);
				clear[mCurrentFocuseIndex] = true;
			}
			if (mCurrentFocuseIndex < 3) {
				++mCurrentFocuseIndex;
			} else {
				mCurrentFocuseIndex = 0;
			}
		}
		// mSecondChildLinearLayout.getChildAt(mCurrentFocuseIndex)
		// .setBackgroundResource(R.drawable.tk_cm_item_selected);
		mSecondChildLinearLayout.getChildAt(mCurrentFocuseIndex)
				.setBackgroundResource(R.drawable.ipinputview_hight);
		mCurrentEditText = (EditText) mSecondChildLinearLayout
				.getChildAt(mCurrentFocuseIndex);
	}

	public LinearLayout getChildLinearLayout() {
		return mChildLinearLayout;
	}

	public void setChildLinearLayout(LinearLayout mChildLinearLayout) {
		this.mChildLinearLayout = mChildLinearLayout;
	}

	public void input(String numStr) {
		if (mCurrentEditText == null) {
			return;
		}
		if (true == clear[mCurrentFocuseIndex]) {
			clear[mCurrentFocuseIndex] = false;
			mCurrentEditText.setText("");
		}
		String mOldStr = mCurrentEditText.getText().toString();
		if (mOldStr != null && mOldStr.length() == 3) {
			mOldStr = mOldStr.trim().substring(1);
		}
		mCurrentEditText.setText(mOldStr + numStr);
	}

	// According to a up key to clear a mantissa
	public void clearMantissa() {
		EditText mEditText = (EditText) mSecondChildLinearLayout
				.getChildAt(mCurrentFocuseIndex);
		String mOldTextStr = mEditText.getText().toString();
		if (mOldTextStr != null && mOldTextStr.length() > 0) {
			mEditText.setText(mOldTextStr
					.substring(0, mOldTextStr.length() - 1));
		}
	}

	// press down clear all
	public void clearAll() {
		EditText mEditText = (EditText) mSecondChildLinearLayout
				.getChildAt(mCurrentFocuseIndex);
		mEditText.setText("");
	}

	public boolean onKeyBack() {
		boolean flag = validate(mCurrentFocuseIndex);
		if (flag) {
			if (mCurrentFocuseIndex != -1) {
				// mSecondChildLinearLayout.getChildAt(mCurrentFocuseIndex)
				// .setBackgroundResource(R.drawable.tk_cm_munuitem_bg);
				mSecondChildLinearLayout.getChildAt(mCurrentFocuseIndex)
						.setBackgroundResource(R.drawable.ipinputview_normal);
			}
			mCurrentFocuseIndex = -1;
		}
		return flag;
	}

	public int getValue() {
		return 0;
	}

	public void onKeyEnter() {
	}

	public void setValue(int value) {
	}

	public OnValueChangedListener getValueChangedListener() {
		return mValueChangedListener;
	}

	public void setValueChangedListener(
			OnValueChangedListener mValueChangedListener) {
		this.mValueChangedListener = mValueChangedListener;
	}

	public void showValue(int value) {
	}

	public TextView getmTextViewName() {
		return mTextViewName;
	}

	public void setmTextViewName(TextView mTextViewName) {
		this.mTextViewName = mTextViewName;
	}

	public EditText getmEditText1() {
		return mEditText1;
	}

	public void setmEditText1(EditText mEditText1) {
		this.mEditText1 = mEditText1;
	}

	public EditText getmEditText2() {
		return mEditText2;
	}

	public void setmEditText2(EditText mEditText2) {
		this.mEditText2 = mEditText2;
	}

	public EditText getmEditText3() {
		return mEditText3;
	}

	public void setmEditText3(EditText mEditText3) {
		this.mEditText3 = mEditText3;
	}

	public EditText getmEditText4() {
		return mEditText4;
	}

	public void setmEditText4(EditText mEditText4) {
		this.mEditText4 = mEditText4;
	}

}
