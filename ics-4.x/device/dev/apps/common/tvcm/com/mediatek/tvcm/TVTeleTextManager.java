package com.mediatek.tvcm;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;

import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.CompListener;
import com.mediatek.tv.service.ComponentService;

public class TVTeleTextManager extends TVComponent {
	

	private static final String TAG="TVTeleText CM";
	
	protected ComponentService cpsService;
	
	
	
	public TVTeleTextManager(Context context) {
		super(context);
		try {
			Log.e(TAG, "getService"+ComponentService.CompServiceName);
			cpsService=(ComponentService)getTVMngr().getService(ComponentService.CompServiceName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void init(){}
	
	
	public void ActivateComponent(String compName){
		if(cpsService!=null){
			try {
				Log.e(TAG, "ActivateComponent"+ComponentService.CompServiceName);
				cpsService.ActivateComponent(compName);
			} catch (TVMException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void InActivateCompoent(String compName){
		if(cpsService!=null){
			try {
				Log.e(TAG, "InActivateCompoent:"+ComponentService.CompServiceName);
				cpsService.InActivateCompoent(compName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void updateSysStatus(String statusName){
		
		if(cpsService!=null){
			try {
				Log.e(TAG, "updateSysStatus:"+statusName);
				cpsService.updateSysStatus(statusName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public boolean IsTTXAvail(){
		if(cpsService!=null){
			try {
				Log.e(TAG, "IsTTXAvail:"+cpsService.IsTTXAvail());
				return cpsService.IsTTXAvail();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	
	public void sendkeyEvent(int i4_keycode,int fgKeydown){
		if(cpsService!=null){
			try {
				Log.e(TAG, "sendkeyEvent:"+i4_keycode+"up/down:"+fgKeydown);
				cpsService.sendkeyEvent(i4_keycode, fgKeydown);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	public void  registerNotifyLisenter(CompListener notifyLisenter){
		if(cpsService!=null){
			Log.e("TTXManager", "add complisenter");
			cpsService.addListener(notifyLisenter);
		}
		
	}
	
	public void  removeNotifyLisenter(CompListener notifyLisenter){
		if(cpsService!=null){
			Log.e("TTXManager", "remove complisenter");
			cpsService.removeListener(notifyLisenter);
		}
	
	}
	
	
	
	

}
