package com.iflytek.tts.TtsService;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioData {
	private static AudioTrack mAudio = null;
	private static final String TAG = "TtsService(audio)";
	private static int mStreamType = AudioManager.STREAM_MUSIC;
	private static int mSampleRate = 16000;
	private static int buffersize;

	/**
	 * For C call
	 */
	public static int creatAudiotrack() {
		buffersize = AudioTrack.getMinBufferSize(mSampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		Log.d("jing", "wangjing add" + buffersize);

		try {
			mAudio = new AudioTrack(mStreamType, mSampleRate,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, buffersize,
					AudioTrack.MODE_STREAM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAudio.play();

		return 0;
	}

	public static void onJniOutData(int len, byte[] data) {

		if (null == mAudio) {
			Log.e(TAG, " mAudio null");
			return;
		}
		if (mAudio.getState() != AudioTrack.STATE_INITIALIZED) {
			Log.e(TAG, " mAudio STATE_INITIALIZED" + mAudio.getState());
			return;
		}
		try {

			mAudio.write(data, 0, len);

			mAudio.setStereoVolume(1.0f, 1.0f);

		} catch (Exception e) {
			Log.e(TAG, e.toString());
			Log.v("kang", "kang:" + e.toString());
		}

	}

	/**
	 * For C Watch Call back
	 * 
	 * @param nProcBegin
	 */
	public static void onJniWatchCB(int nProcBegin) {
		TclTts.playCallBackFunc.playCallBack(nProcBegin);
	}

	public static void stop() {
		if (mAudio != null) {
			mAudio.stop();
		}
	}

	public static void pause() {
		if (mAudio != null) {
			mAudio.pause();
		}
	}

	public static void flush() {
		if (mAudio != null) {
			mAudio.flush();
		}
	}

	public static void release() {
		if (null == mAudio) {
			Log.e(TAG, " mAudio null");
			return;
		}
		if (mAudio.getState() != AudioTrack.STATE_INITIALIZED) {
			Log.e(TAG, " mAudio STATE_INITIALIZED" + mAudio.getState());
			return;
		}
		if (mAudio != null) {
			mAudio.release();
			mAudio = null;

		}
	}
}
