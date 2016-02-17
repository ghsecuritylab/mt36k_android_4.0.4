package com.mediatekk.mmpcm.threedimen.photoimpl;

import com.mediatek.common.PhotoPlayer.MtkPhotoPlayer;
import android.R.integer;
import com.mediatek.common.PhotoPlayer.MtkPhotoHandler;
import com.mediatek.common.PhotoPlayer.NotSupportException;
import com.mediatekk.mmpcm.MmpTool;
import com.mediatekk.mmpcm.mmcimpl.Const;
import com.mediatekk.mmpcm.mmcimpl.PlayList;
import com.mediatekk.mmpcm.threedimen.photo.IPlayback;
import com.mediatekk.mmpcm.threedimen.photo.IThrdPhotoEventListener;
/**
* The class manager 3D photo.
**/
public class MPlayback implements IPlayback{
	private MtkPhotoPlayer mPhotoPlayer;
	private IThrdPhotoEventListener mEventListener;
	private PlayList mPlayList;
	public boolean dumy = false;
	
	public MPlayback() {
		mPhotoPlayer = new MtkPhotoPlayer();
		mPlayList = PlayList.getPlayList();
	}
	/**
	 * @deprecated
	 * open Will put to play.so, Ap don't call the API.
	 * conncet vdp;
	 */
	public void open(){
		int result = 0;
		if (mPhotoPlayer != null){
			try {
				if(!dumy){
					result = mPhotoPlayer.ConnetVDP();
				}
			} catch(NotSupportException e){
				if (mEventListener != null){
					mEventListener.onOpenFailed();
				}
			}
		}
		
		MmpTool.LOG_INFO("Photo open result = " + result);
	}
	/**
	 * play a 3d photo file
	 * @param path: Absolute path
	 */
	public void play(String path){
		int result = 0;
		MmpTool.LOG_INFO("Photo play path = 1" + path);
		if (mPlayList != null && path == null /*isEnd()*/){
			if (mEventListener != null){
				mEventListener.onCompleted();
				return;
			}
		}
		if (mPhotoPlayer != null){
			MmpTool.LOG_INFO("Photo play result before= " + result);
			if (path == null){
				return;
			}
			try{
				if(!dumy){
					result = mPhotoPlayer.ConnetVDP();
					result = mPhotoPlayer.Play(path);
// add by shuming
					mEventListener.playDone();
				}	
			} catch (NotSupportException e) {
				if (mEventListener != null){
					mEventListener.onPlayFailed();
				}
				e.printStackTrace();
			}
		}
		MmpTool.LOG_INFO("Photo play result end = " + result);
	}
	
	/**
	 * decode 3Dphoto
	 */
	public MtkPhotoHandler decode3DPhoto(String path){
		MtkPhotoHandler mtkPhotoHandler = null;
		int result = 0;
		MmpTool.LOG_INFO("Photo play path = 1" + path);
		if(mPlayList != null && path == null){
			   if(mEventListener != null){
			   	mEventListener.onCompleted();
			   	return null;
			  }
			
			}
			if(mPhotoPlayer != null){
				MmpTool.LOG_INFO("Photo play result before= "+result);
				if(path == null){
					return null;
				}
				if(!dumy){
					result = mPhotoPlayer.ConnetVDP();
					try{
						  if(!dumy){
						  	mtkPhotoHandler = mPhotoPlayer.Decode(path);
						  }
					}catch (NotSupportException e){
						if(mEventListener != null){
							mEventListener.onPlayFailed();
						}
						e.printStackTrace();
					}
				}
			}
			 if(!dumy && null != mtkPhotoHandler ){
			    mEventListener.decodeSuccess();
			 }
			return mtkPhotoHandler;
	}
	
	public int Display(MtkPhotoHandler mtkPhotoHandler){
		
		int mResult = 0;
		if(null != mPhotoPlayer){
			try{
				if(!dumy){
					mResult = mPhotoPlayer.Display(mtkPhotoHandler);
					mEventListener.playDone();
				}
			}catch(NotSupportException e){
				if(mEventListener != null){
					mEventListener.onPlayFailed();
				}
				e.printStackTrace();
			}
		}
		return mResult;
	}
	/**
	 * disconnect to vdp
	 */
	public void close(){
		int result = 0;
		if (mPhotoPlayer != null){
			try{
				if(!dumy){
					result = mPhotoPlayer.DisConnetVDP();
				}
			}catch (NotSupportException e){
				if (mEventListener != null){
					mEventListener.onCloseFailed();
				}
			}
		}
		MmpTool.LOG_INFO("Photo close result = " + result);
	}
	/**
	 * set event listener, notify caller.
	 * @param listener
	 */
	public void setEventListener(IThrdPhotoEventListener listener){
		mEventListener = listener;
	}
	
	/**
	 * judge wherther end or not
	 * @return
	 */
	public boolean isEnd() {
		if (mPlayList.getRepeatMode(Const.FILTER_IMAGE) == Const.REPEAT_NONE) {
			if (mPlayList.getShuffleMode(Const.FILTER_IMAGE) == Const.SHUFFLE_ON) {
				if (mPlayList.getCurShuffleIndex(Const.FILTER_IMAGE) >= (mPlayList
						.getFileNum(Const.FILTER_IMAGE) - 1)) {
					return true;
				}
			} else {
				if (mPlayList.getCurrentIndex(Const.FILTER_IMAGE) >= (mPlayList
						.getFileNum(Const.FILTER_IMAGE) - 1)) {
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * Release source
	 */
	public void onRelease(){
		if (mPhotoPlayer != null){
			mPhotoPlayer = null;
		}
	}
}
