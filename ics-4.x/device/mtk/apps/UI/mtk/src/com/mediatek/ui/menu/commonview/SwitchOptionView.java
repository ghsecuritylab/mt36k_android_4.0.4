package com.mediatek.ui.menu.commonview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

import com.mediatek.ui.R;
import com.mediatek.ui.menu.adapter.SetConfigListViewAdapter.DataItem;
import com.mediatek.ui.menu.util.MenuConfigManager;
import com.mediatek.ui.menu.util.MenuDataContainer;

/**
 * This view includes a text view (the name), the second text view(the current
 * value) and a image view. it is one item of list view items. you can press
 * LEFT or RIGHT key to change the value. the value is from an array
 * 
 * @author hs_yaoxiao
 */

public class SwitchOptionView extends OptionView {
	private List<DataItem> mChildGroup;
	private HashMap<Integer, Boolean[]> mHashMap;
	private String[] m3DModeStrArr;

	public SwitchOptionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public SwitchOptionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public SwitchOptionView(Context context) {
		super(context);
		this.context = context;
	}

	public void setAdapter(DataItem mDataItem) {
		super.setAdapter(mDataItem);
		mChildGroup = mDataItem.getmEffectGroup();
		mHashMap = mDataItem.getmSwitchHashMap();
		m3DModeStrArr = MenuDataContainer.getInstance(context)
				.getM3DModeStrArr();
		this.mDataItem = mDataItem;
	}

	protected void switchValueNext() {
		super.switchValueNext();
		if (MenuConfigManager.VIDEO_3D_MODE.equals(mDataItem.getmItemID())) {
			ArrayList<Boolean> m3DConfigList = MenuConfigManager.getInstance(
					mContext).get3DConfig();
			mDataItem.isEnable = m3DConfigList.get(0);
			int i = 1;
			for (DataItem childItem : mChildGroup) {
				childItem.isEnable = m3DConfigList.get(i);
				i++;
			}
		} else {
			Boolean[] isEnables = mHashMap.get(getValue());
			int i = 0;
			for (DataItem childItem : mChildGroup) {
				if(isEnables != null){
					childItem.isEnable = isEnables[i++];
				}
				if (MenuConfigManager.AUTO_SYNC.equals(mDataItem.getmItemID())) {
					UpdateTime updateTime = new UpdateTime();
					if (2 == mDataItem.mInitValue) {
						updateTime.setAutoState(context, 1);
					} else {
						updateTime.setAutoState(context, 0);
					}
				}
				// if
				// (MenuConfigManager.VIDEO_3D_MODE.equals(mDataItem.getmItemID())
				// && (m3DModeStrArr[mDataItem.mInitValue].equals(context
				// .getString(R.string.menu_video_3d_leftright_str)) ||
				// m3DModeStrArr[mDataItem.mInitValue]
				// .equals(context
				// .getString(R.string.menu_video_3d_updown_str)))) {
				// mChildGroup.get(0).isEnable = true;
				// }
			}
			if (MenuConfigManager.NETWORK_CONNECTION.equals(mDataItem
					.getmItemID())) {
				if(1==mDataItem.mInitValue){
					if(0==mChildGroup.get(0).mInitValue){
						mChildGroup.get(1).setEnable(false);
						mChildGroup.get(3).setEnable(true);
					}else{
						mChildGroup.get(1).setEnable(true);
						mChildGroup.get(3).setEnable(false);
					}
				}					
			} 
		}
	}

	protected void switchValuePrevious() {
		super.switchValuePrevious();
		if (MenuConfigManager.VIDEO_3D_MODE.equals(mDataItem.getmItemID())) {
			ArrayList<Boolean> m3DConfigList = MenuConfigManager.getInstance(
					mContext).get3DConfig();
			mDataItem.isEnable = m3DConfigList.get(0);
			int i = 1;
			for (DataItem childItem : mChildGroup) {
				childItem.isEnable = m3DConfigList.get(i);
				i++;
			}
		} else {
			Boolean[] isEnables = mHashMap.get(getValue());
			int i = 0;
			for (DataItem childItem : mChildGroup) {
				if(isEnables != null){
					childItem.isEnable = isEnables[i++];
				}
				if (MenuConfigManager.NETWORK_CONNECTION.equals(mDataItem
						.getmItemID())
						&& 0 == mChildGroup.get(0).mInitValue) {
					if (0 == childItem.getmInitValue()) {
						mChildGroup.get(1).isEnable = false;
					}
				} else if (MenuConfigManager.AUTO_SYNC.equals(mDataItem
						.getmItemID())) {
					UpdateTime updateTime = new UpdateTime();
					if (2 == mDataItem.mInitValue) {
						updateTime.setAutoState(context, 1);
					} else {
						updateTime.setAutoState(context, 0);
				}
				}

				// if
				// (MenuConfigManager.VIDEO_3D_MODE.equals(mDataItem.getmItemID())
				// && (m3DModeStrArr[mDataItem.mInitValue].equals(context
				// .getString(R.string.menu_video_3d_leftright_str)) ||
				// m3DModeStrArr[mDataItem.mInitValue]
				// .equals(context
				// .getString(R.string.menu_video_3d_updown_str)))) {
				// mChildGroup.get(0).isEnable = true;
				// }

			}
		}
	}

}
