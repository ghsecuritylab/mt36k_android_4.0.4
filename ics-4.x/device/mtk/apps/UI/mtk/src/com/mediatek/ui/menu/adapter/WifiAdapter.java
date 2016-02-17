package com.mediatek.ui.menu.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;

import com.mediatek.netcm.wifi.WifiAccessPoint;
import com.mediatek.ui.menu.commonview.WifiAPView;
import com.mediatek.ui.menu.commonview.WifiScanList;
import com.mediatek.ui.menu.commonview.WifiWpsAPView;
import com.mediatek.ui.menu.util.MenuConfigManager;

public class WifiAdapter extends BaseAdapter {

	private Context mContext;
	// store the data of page which is shown on screen
	private List<WifiAccessPoint> mGroup;
	private int scanMode = 0;
	private WifiScanList wifiScanList;

	/**
	 * get the current data of the page which is shown on screen
	 * 
	 * @return the current data of the current page
	 */
	public List<WifiAccessPoint> getmGroup() {
		return mGroup;
	}

	/**
	 * set the current data of the page which is to be shown on screen
	 * 
	 * @param mGroup
	 *            the current data of the page
	 */
	public void setmGroup(List<WifiAccessPoint> mGroup) {
		this.mGroup = mGroup;
	}

	/**
	 * Construct function
	 * 
	 * @param mContext
	 *            store the Context
	 */
	public WifiAdapter(Context mContext, int mode, WifiScanList wifiScanList) {
		super();
		this.mContext = mContext;
		this.scanMode = mode;
		this.wifiScanList = wifiScanList;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mGroup == null ? 0 : mGroup.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mGroup == null ? null : mGroup.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mGroup == null ? 0 : position;
	}

	/**
	 * get the last enabled item position in current shown page
	 * 
	 * @return the index of the last enabled item position
	 */
	public int getLastEnableItemPosition() {
		int position = getCount() - 1;
		return position;
	}

	/**
	 * get the first enabled item position in current shown page
	 * 
	 * @return the index of the first enabled item position
	 */
	public int getFirstEnableItemPosition() {
		int position = 0;
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			WifiAccessPoint aceessPoint = mGroup.get(position);
			if (scanMode == MenuConfigManager.WIFI_SCAN_NORMAL) {
				WifiAPView holder = new WifiAPView(mContext);
				holder.setAdapter(aceessPoint);
				convertView = holder;

			} else if (scanMode == MenuConfigManager.WIFI_SCAN_WPS) {
				WifiWpsAPView holder = new WifiWpsAPView(mContext);
				holder.setAdapter(aceessPoint);
				convertView = holder;
			}
		}
		if(convertView != null){
			LayoutParams params = convertView.getLayoutParams();
			params.height = 30;
			convertView.setLayoutParams(params);
			convertView.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub			
					wifiScanList.setFocus(position);
					return false;
				}
			});
		}
		return convertView;
	}
}
