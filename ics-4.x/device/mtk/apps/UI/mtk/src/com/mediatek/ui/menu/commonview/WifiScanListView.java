package com.mediatek.ui.menu.commonview;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.mediatek.netcm.wifi.WifiAccessPoint;
import com.mediatek.ui.menu.adapter.WifiAdapter;
import com.mediatek.ui.menu.util.PageImp;
import com.mediatek.ui.util.MtkLog;

public class WifiScanListView extends ListView {

	String TAG = "WifiScanListView";
	private Context mContext;
	private PageImp mPageImp;
	// save this ListView Data collection
	private List<WifiAccessPoint> mListGroup;
	// ListView selected location
	private int mCurrentSelectedPosition = 0;
	// ListView on the before correct position
	private int mBeforeSelectedPosition = 0;
	// the number of records per page
	private int pageSize = 8;
	// page number
	private int pageNum;

	private int mRemainderRecord;
	private int mLastEnableItemPosition;
	private int mFirstEnableItemPosition;

	private UpDateListView mUpdate;

	public WifiScanListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public WifiScanListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public WifiScanListView(Context context) {
		super(context);
		this.mContext = context;
	}

	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		mLastEnableItemPosition = getLastEnableItemPosition();
		mFirstEnableItemPosition = getFirstEnableItemPosition();
	}

	public int getPageNum() {
		return pageNum;
	}

	/*
	 * initialization
	 */
	public void initData(List<WifiAccessPoint> list, int perPage,
			UpDateListView update, int pageIndex) {
		mListGroup = list;
		mPageImp = new PageImp(list, perPage);
		this.pageSize = perPage;
		pageNum = mPageImp.getPageNum();
		mRemainderRecord = mPageImp.getCount() % pageSize;
		mUpdate = update;
		if (pageIndex > 0) {
			mPageImp.gotoPage(pageIndex);
		}
	}

	/*
	 * initialization
	 */
	public void initData(List<WifiAccessPoint> list, int perPage) {
		mPageImp = new PageImp(list, perPage);
	}

	// get the last available item position
	private int getLastEnableItemPosition() {
		WifiAdapter adapter = (WifiAdapter) getAdapter();
		return adapter.getLastEnableItemPosition();
	}

	// get the first available item position
	private int getFirstEnableItemPosition() {
		WifiAdapter adapter = (WifiAdapter) getAdapter();
		return adapter.getFirstEnableItemPosition();
	}

	/*
	 * get the current display data set
	 */
	public List<?> getCurrentList() {
		return mPageImp.getCurrentList();
	}

	/*
	 * get the number of the current page
	 */
	public int getCurrentPageNum() {
		return mPageImp.getCurrentPage();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		MtkLog.v(TAG, "key down");
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
			mCurrentSelectedPosition = getSelectedItemPosition();
			if (mCurrentSelectedPosition < 0) {
				mCurrentSelectedPosition = mBeforeSelectedPosition;
			}
			mBeforeSelectedPosition = mCurrentSelectedPosition;
			MtkLog.v(TAG, "current index in page  " + mBeforeSelectedPosition);

			if (mCurrentSelectedPosition == mLastEnableItemPosition) {

				if (mPageImp.getCurrentPage() != pageNum) {
					mPageImp.nextPage();
					mUpdate.updata();
					setSelection(mFirstEnableItemPosition);
					break;
				}

				if (pageNum == 1) {
					setSelection(mFirstEnableItemPosition);
					break;
				}

				if (mPageImp.getCurrentPage() == mPageImp.getPageNum()) {
					mPageImp.headPage();
					mUpdate.updata();
					setSelection(mFirstEnableItemPosition);
					break;
				}
			}
			break;

		case KeyEvent.KEYCODE_DPAD_UP:

			mCurrentSelectedPosition = getSelectedItemPosition();
			if (mCurrentSelectedPosition < 0) {
				mCurrentSelectedPosition = mBeforeSelectedPosition;
			}
			mBeforeSelectedPosition = mCurrentSelectedPosition;
			MtkLog.v(TAG, "current index in page  " + mBeforeSelectedPosition);
			if (mCurrentSelectedPosition == mFirstEnableItemPosition) {

				if (mPageImp.getCurrentPage() != 1) {
					mPageImp.prePage();
					mUpdate.updata();
					setSelection(mLastEnableItemPosition);
					break;
				}
				if (pageNum == 1) {
					setSelection(mLastEnableItemPosition);
					break;
				}

				if (pageNum > 1 && mPageImp.getCurrentPage() == 1) {
					mPageImp.lastPage();
					mUpdate.updata();
					setSelection(mLastEnableItemPosition);
					break;
				}
			}

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * data update interface
	 */
	public interface UpDateListView {
		void updata();
	}
}
