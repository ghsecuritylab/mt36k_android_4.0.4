package com.iflytek.tts.TtsService;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioData_old {
	private static AudioTrack mAudio /*= null*/;
	private static final String TAG = "TtsService(audio)";
	private static int mStreamType = AudioManager.STREAM_MUSIC;
	private static int mSampleRate = 16000;
	private static int buffersize;
	static {
		
		buffersize = 
			AudioTrack.getMinBufferSize(mSampleRate, 
		    AudioFormat.CHANNEL_CONFIGURATION_MONO, 
		    AudioFormat.ENCODING_PCM_16BIT);
		mAudio = new AudioTrack(mStreamType
				,mSampleRate,AudioFormat.CHANNEL_CONFIGURATION_MONO 
				,AudioFormat.ENCODING_PCM_16BIT
				,buffersize,AudioTrack.MODE_STREAM );
		Log.d("jing","wangjing add"+buffersize);
		Log.d(TAG," AudioTrack create ok");
	}	
	/**
	 * For C call 
	 */
	public static  void onJniOutData(int len,byte [] data){	
		
			if (null == mAudio){
				Log.e(TAG," mAudio null");
				return;
			}
			if (mAudio.getState() != AudioTrack.STATE_INITIALIZED ){
				Log.e(TAG," mAudio STATE_INITIALIZED"+mAudio.getState());
				return;
			}			
			try{
				mAudio.write(data, 0, len);	
				mAudio.play();		
			}catch (Exception e){
				Log.e(TAG,e.toString());
				Log.v("kang","kang:" + e.toString());
			}
			
}
	
	/**
	 * For C Watch Call back
	 * @param nProcBegin
	 */
	public static void onJniWatchCB(int nProcBegin){
	//	Log.v("kang","kang : onJniWatchCB  process begin = " + nProcBegin);		
		TclTts.playCallBackFunc.playCallBack(nProcBegin);
	}
	
	public static void stop(){
		mAudio.stop();
	}
	
	public static void pause(){
		mAudio.pause();
	}
	
	public static void release(){
		mAudio.release();
	}
}


