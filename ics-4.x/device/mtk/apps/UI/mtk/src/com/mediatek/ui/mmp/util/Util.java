package com.mediatek.ui.mmp.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.mediatek.mmpcm.device.DevManager;
import com.mediatek.ui.mmp.model.MultiFilesManager;
import com.mediatek.ui.mmp.multimedia.MtkFilesBaseListActivity;
import com.mediatek.ui.nav.EPGActivity;
import com.mediatek.ui.nav.NavIntegration;
import com.mediatek.ui.util.DestroyApp;
import com.mediatek.ui.util.MtkLog;


public class Util {
	public static String TAG = "Util";
	private static Activity mActivity;
	private static boolean isMMP;
	public static void startEPGActivity(Activity actvity){
		mActivity = actvity;
		NavIntegration mNavIntegration = NavIntegration.getInstance(mActivity);
		if (mNavIntegration.isCurrentSourceTv()) {

			if (mNavIntegration.isDTVHasChannels()) {

				if (!mNavIntegration.isCurrentSourceDTV()) {
					mNavIntegration.changeDTVSource();
				}
				if (mNavIntegration.iGetChannelLength() > 0
						&& !mNavIntegration.isCurrentSourceBlocking()) {
					
					LogicManager.getInstance(mActivity).restoreVideoResource();
					LogicManager.getInstance(mActivity).finishAudioService();
					MultiFilesManager.getInstance(mActivity).destroy();
					((DestroyApp) (mActivity).getApplication()).finishAll();
					MtkFilesBaseListActivity.reSetModel();
					((Activity) mActivity).startActivityForResult(new Intent(
							mActivity, EPGActivity.class),
							0);
					
				}
			}
		}
	}
	
	
	public static void exitMmpActivity(Context context){
		LogicManager.getInstance(context).finishAudioService();
		LogicManager.getInstance(context).finishVideo();
		DevManager.getInstance().destroy();
		MultiFilesManager.getInstance(context).destroy();
		DestroyApp destroyApp = (DestroyApp)(context).getApplicationContext();
		destroyApp.finishAll();
	}
	public static void setMMPFlag(boolean flag) {
		isMMP = flag;
	}
	public static boolean getMMPFlag() {
		return isMMP;
	}
}
