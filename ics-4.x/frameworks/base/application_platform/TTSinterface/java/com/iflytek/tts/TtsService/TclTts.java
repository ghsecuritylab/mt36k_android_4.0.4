package com.iflytek.tts.TtsService;

import java.io.File;

import android.os.Environment;
import android.util.Log;

public class TclTts
{
	static PlayCallBackFunc playCallBackFunc ;
	public TclTts(PlayCallBackFunc playCallBackFunc,String dictPath)
	{
		int i=ttsCreate(dictPath);
		Log.d("tag","jing:load resource"+i);
		TclTts.playCallBackFunc = playCallBackFunc;
	}
	
	public TclTts(PlayCallBackFunc playCallBackFunc)
	{
		File file = new File("/system/lib/resource.irf");
		if(!file.exists())
		{
			throw new IllegalStateException("can't find /sdcard/tcltts/resource.irf");
		}
		ttsCreate("/system/lib/resource.irf");
		TclTts.playCallBackFunc = playCallBackFunc;
	}
	/**
	 * 
	 * @param resFilename
	 * tts resource file path
	 * @return
	 */
	private int ttsCreate(String resFilename)
	
	{
		Log.d("TclTts","ttsCreate");
		int n=Tts.JniCreate(resFilename);
		return  n;
	}
	public int ttsDestory()
	{
		return Tts.JniDestory();
	}
	/**
	 * 
	 * @param text
	 * the text will speak
	 * @return
	 */
	public int ttsSpeak(String text)
	{
		Log.d("jni speak","speak start ");
		 int i=Tts.JniSpeak(text);
		 Log.d("jni speak","speak end "+i);
		 return i;
	}
	
	public int ttsStop()
	{
		Log.d("jni stop","ttsStop ");
		int m=Tts.JniStop();
		Log.d("jni stop","stop status is :"+m);
		return m;
	}
	/**
	 * 
	 * @param speed
	 * e.g. 
	 * 
	 */
	public void setSpeed(int speed)
	{
		if(speed == TtsSpeed.FAST_SPEED)
		{
			ttsSetParam(0x00000502,32766);
		}
		else if(speed == TtsSpeed.NORMAL_SPEED)
		{
			ttsSetParam(0x00000502,0);
		}
		else if(speed == TtsSpeed.SLOW_SPEED)
		{
			ttsSetParam(0x00000502,-32768);
		}
		else {
			throw new IllegalStateException("error:speed is invalid");
		}
	}
	/**
	 * 
	 *  
	 * watch the file ivTTS.h
	 * 
	 * @return
	 */
	
	public int ttsIsplaying()
	{
		Log.d("jingjingjingjing","is playing");
		int p=Tts.JniIsPlaying();
		Log.d("tag","jing:jni isplaying  jjjjjjjjjjjjjjjjjjjjjjjjjjjjjj"+p);
		return p;
	}
	public int ttsSetParam(int paramId,int value)
	{
		return Tts.JniSetParam(paramId, value);
	}
	public int ttsGetParam(int paramId)
	{
		return Tts.JniGetParam(paramId);
	}
	
	public void setPlayCallBack(PlayCallBackFunc playCallBackFunc)
	{
		TclTts.playCallBackFunc = playCallBackFunc;
	}

	private boolean checkSDCard()  
	 {  
	     if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))  
	        return true;  
	    else  
	        return false;  
	 } 
//	private void copyTTSResource()
//	{
//		try
//		{
//			int byteread = 0;
//			InputStream inStream = getAssets().open("resource.png");
//			File newfile_2 = new File("/sdcard/resource.irf");
//			if (newfile_2.exists())
//			{
//				newfile_2.delete();
//			}
//			FileOutputStream fs = new FileOutputStream("/sdcard/Resource.irf");
//			byte[] buffer = new byte[1444];
//			while ((byteread = inStream.read(buffer)) != -1)
//			{
//				fs.write(buffer, 0, byteread);
//			}
//			inStream.close();
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//
//		}
//	}
}
