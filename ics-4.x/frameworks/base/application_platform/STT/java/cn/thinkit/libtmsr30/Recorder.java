package cn.thinkit.libtmsr30;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tcl.stt.threadpool.MyThreadPool;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Process;
import android.util.Log;
import cn.thinkit.libtmfe.test.JNINET;

// Referenced classes of package com.msr:
//            JNI, Recorder
class Recorder implements Runnable {
	private boolean isBeginPoint;
	private boolean isLocalStop;
	private boolean isNetStop;
	private String localResult = null;
	private boolean useLocal = false;
	private int serialNum = 1;
	private Activity activity;
	private int source;
	private TclSttPlayCallBackFunc playCallBackFunc;
	private TclSttStartCallBackFunc startCallBackFunc;
	private TclSttVoiceStartPlayCallBackFunc voiceStartCallBackFunc;
	private TclSttVoiceEndPlayCallBackFunc voiceEndCallBackFunc;
	private String companyName;
	private String deviceType;
	private String deviceId;
	private String productId;
	private int frequency;
	private String mLock = "lock";
	private int channelConfiguration;
	private volatile boolean recogniseFlag = true;
	private AudioRecord recordInstance = null;
	private JNI mVREngine;
	private JNINET mVREngineNet;
	private DataOutputStream mDataOutputStreamInstance;
	private DataOutputStream mDataOutputStreamInstanceCompress;
	private int reliability;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;;
	private static boolean Debug = false;
	private String mPath = null;
	private String mPathCompress = null;
	private int bufferSize;
	long mVocHandle;

	private void setRecogniseFlag(boolean Flag) {
		this.recogniseFlag = Flag;
	}

	/**
	 * 调试接口,outPath为输出目录,如果设置了输出目录,每次识别时都会将原始的录音文件保存在outPath/test.pcm,如果是网络识别，
	 * 压缩后的录音文件保存在outPath/test2.pcm,可以分析此文件 来判断录音效果.
	 * 
	 */
	public void setDebug(String outPath) {
		Debug = true;

		if (Debug) {
			if (outPath != null) {
				File file = new File(outPath);
				if (file.exists()) {
					if (!outPath.endsWith("/")) {
						outPath = outPath + "/";
					}
					this.mPath = outPath + "test.pcm";
					this.mPathCompress = outPath + "test2.pcm";

				} else {
					Log.e("", "kang:outPaht not eixt");
				}
			} else {
				Log.e("", "kang:outPaht == null");
			}
		}

	}

	public static boolean isDebug() {
		return Debug;
	}

	public synchronized void stopRun() {
		// synchronized (mLock)
		{
			
			recogniseFlag = false;
			try {
				if (mDataOutputStreamInstance != null) {
					mDataOutputStreamInstance.close();
				}
				if (mDataOutputStreamInstanceCompress != null) {
					mDataOutputStreamInstanceCompress.close();
				}
			} catch (IOException e1) {
				MyLog.log("error");
				e1.printStackTrace();
			} finally {
				try {
					if (recordInstance != null) {
						recordInstance.stop();
						recordInstance.release();
						recordInstance = null;
					}
				} catch (IllegalStateException e) {
					MyLog.log("stop a uninit recordInstance");
				}
			}
		}
	}

	public void sttRelease() {
		// synchronized (mLock)
		{
			MyLog.log("sttRelease");
			MyThreadPool.getThreadPool().shutdown();
			MyThreadPool.init();
			recogniseFlag = false;
			try {
				if (mDataOutputStreamInstance != null) {
					mDataOutputStreamInstance.close();
				}
				if (mDataOutputStreamInstanceCompress != null) {
					mDataOutputStreamInstanceCompress.close();
				}
			} catch (IOException e1) {
				MyLog.log("error");
				e1.printStackTrace();
			} finally {
				if (recordInstance != null) {
					try {
						recordInstance.stop();
					} catch (IllegalStateException e) {
						MyLog.log("stop a uninit recordInstance");
					}
					recordInstance.release();
					recordInstance = null;
					Log.v("kang", "kang: 1   recordInstance=" + recordInstance);
				}
				mVREngine.msrRemoveVocabularyFromDecoder(mVocHandle);
				mVREngine.msrDestroyVocabulary(mVocHandle);
				mVREngine.msrClose();
				mVREngineNet.mfeCloseTcl();
				mVREngine.msrExit();
				mVREngineNet.mfeExitTcl();
				Log.v("kang", "kang: 2   recordInstance=" + recordInstance);
			}
		}
	}

	public Recorder(int frequency, int channelConfiguration, JNI mVREngine,
			JNINET mVREngineNet, long mVocHandle, int reliability,
			Activity activity, int source,
			TclSttPlayCallBackFunc playCallBackFunc,
			TclSttStartCallBackFunc startCallBackFunc,
			TclSttVoiceStartPlayCallBackFunc voiceStartCallBackFunc,
			TclSttVoiceEndPlayCallBackFunc voiceEndCallBackFunc,
			String companyName, String deviceType, String deviceId,
			String productId) throws TclSttInitException {
		this.voiceEndCallBackFunc = voiceEndCallBackFunc;
		this.voiceStartCallBackFunc = voiceStartCallBackFunc;
		this.companyName = companyName;
		this.deviceType = deviceType;
		this.deviceId = deviceId;
		this.productId = productId;
		this.playCallBackFunc = playCallBackFunc;
		this.startCallBackFunc = startCallBackFunc;
		this.activity = activity;
		this.source = source;
		this.mVocHandle = 0L;
		this.frequency = frequency;
		this.channelConfiguration = channelConfiguration;
		this.mVREngine = mVREngine;
		this.mVREngineNet = mVREngineNet;
		this.mVocHandle = mVocHandle;
		this.reliability = reliability;
		MyLog.log("kang:before new AudioRecord");
		// int bufferSize = AudioRecord.getMinBufferSize(frequency,
		// channelConfiguration, audioEncoding);
		// int bufferSize = 8192;// net
		this.bufferSize = 8192;
		// this.bufferSize = 32000;
		// 把上面大小改大点
		// Log.i("MSROnline", (new
		// StringBuilder("Frenquency=")).append(frequency)
		// .append("Configuration=").append(channelConfiguration).append(
		// "AudionEncoding=").append(2).append("bufferSize=")
		// .append(bufferSize).toString());

	}

	public void run() {
		boolean netWork = isNetworkAvailable(Recorder.this.activity);
		this.isLocalStop = false;
		this.isNetStop = false;
		this.useLocal = false;
		this.localResult = null;
		this.isBeginPoint = true;
		MyLog.log("network is = " + netWork);
		JNI j = mVREngine;
		final JNINET jNet = mVREngineNet;
		j.msrSetVocabularyToDecoder(mVocHandle);
		do {
			recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC,
					frequency, channelConfiguration, audioEncoding, bufferSize);
		} while (recordInstance == null
				|| recordInstance.getState() != android.media.AudioRecord.STATE_INITIALIZED);
		MyLog.log("kang:end new AudioRecord");
		if (Debug) {
			if (mPath != null && new File(mPath).exists()) {
				new File(mPath).delete();
			}
			BufferedOutputStream bufferedStreamInstance = null;
			try {
				MyLog.log("kang:mPath=" + mPath);
				bufferedStreamInstance = new BufferedOutputStream(
						new FileOutputStream(mPath));
			} catch (FileNotFoundException e) {
				MyLog.log("error");
				e.printStackTrace();
			}
			mDataOutputStreamInstance = new DataOutputStream(
					bufferedStreamInstance);
		}
		if (Debug) {
			if (mPathCompress != null && new File(mPathCompress).exists()) {
				new File(mPathCompress).delete();
			}
			BufferedOutputStream bufferedStreamInstance = null;
			try {
				bufferedStreamInstance = new BufferedOutputStream(
						new FileOutputStream(mPathCompress));
			} catch (FileNotFoundException e) {
				MyLog.log("error");
				e.printStackTrace();
			}
			mDataOutputStreamInstanceCompress = new DataOutputStream(
					bufferedStreamInstance);
		}
		// /////////////////
		Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		final String batchSerialNum = new Integer(getRandomNum()).toString();
		// ///////////////
		short realBuffer[] = new short[bufferSize];
		byte[] datBuffer = new byte[bufferSize];// net
		String retResult;
		// synchronized (MyLock.lockOne)
		{
			if (source == TclStt.SOURCE_ONLINE) {
				jNet.mfeStartTcl();
			} else if (source == TclStt.SOURCE_LOCAL) {
				j.msrStart();
			}

			// Log.v("kang", "kang: 4   recordInstance="+recordInstance);
			while (true) {
				if (recordInstance != null
						&& recordInstance.getState() != AudioRecord.STATE_INITIALIZED) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						MyLog.log("error");
						e.printStackTrace();
					}
					continue;
				} else {
					recordInstance.startRecording();// 开始录音
					break;
				}

			}
			MyLog.log("kang:startRecording");
			startCallBackFunc.startCallBack();
			setRecogniseFlag(true);
			while (recogniseFlag) {

				int bufferRead = recordInstance.read(realBuffer, 0, bufferSize);// 从录音读数据
				if (Debug) {
					savePcm(realBuffer, bufferRead);
				}
				MyLog.log("bufferRead=" + bufferRead);
				if (bufferRead == 0) {
					MyLog.log("bufferRead=0");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						MyLog.log("error");
						e.printStackTrace();
					}
					continue;
				}
				// bufferRead = 24000;//将buf改为能存放3秒的录音
				if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION)
					MyLog
							.log("read() returned AudioRecord.ERROR_INVALID_OPERATION");
				if (bufferRead == AudioRecord.ERROR_BAD_VALUE)
					MyLog.log("read() returned AudioRecord.ERROR_BAD_VALUE");
				if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
					MyLog
							.log("read() returned AudioRecord.ERROR_INVALID_OPERATION");
				}
				if (source == TclStt.SOURCE_LOCAL)// 指定本地识别
				{
					MyLog.log("local recognize");
					j.msrSendData(realBuffer, bufferRead);// 发送录音去识别引擎

					String strResult = j.msrRecognize();// 开始识别
					if (strResult.length() > 0) {
						if (strResult.equals("STARTPOINT")) {
							voiceStartCallBackFunc.voiceStartCallBack();
							MyLog.log("local start");
						} else if (strResult.equals("ENDPOINT")) {
							MyLog.log("local end");
							voiceEndCallBackFunc.voiceEndCallBack();
						} else {
							stopRun();
							MyLog.log("stopRun");
							MyLog.log("recogniseFlag=" + recogniseFlag);
							MyLog.log("local else");
							retResult = strResult;
							j.msrStop();
							jNet.mfeStopTcl();
							Recorder.this.playCallBackFunc
									.playCallBack(retResult);
						}
					}
				} else if (source == TclStt.SOURCE_ONLINE)// 指定网络识别
				{
					MyLog.log("send to net");
					jNet.mfeSendDataTcl(realBuffer, bufferRead);// 发送语音去网络识别引擎
					// MyLog.log("bufferRead = "+ bufferRead);
					int detect_flag = jNet.mfeDetectTcl();
					// MyLog.log("detect_flag = "+detect_flag);
					int readlen = jNet.mfeGetCallbackDataTcl(datBuffer,
							bufferSize);
					if (Debug) {
						savePcmCompress(datBuffer, readlen);
					}
					// MyLog.log("readLen = "+readlen);
					if (detect_flag >= 1) {
						if ((detect_flag == 1)
								&& (Recorder.this.isBeginPoint == true))// speak
						// begin
						{
							voiceStartCallBackFunc.voiceStartCallBack();
							Recorder.this.isBeginPoint = false;
							// ///////////////
							final byte[] fdatBuffer = datBuffer;
							final int freadlen = readlen;
							MyThreadPool.getThreadPool().execute(
									new Runnable() {
										@Override
										public void run() {
											String jsonHeader = createJSONHead(
													"start", companyName,
													deviceType, deviceId, "",
													productId, batchSerialNum,
													new Integer(serialNum)
															.toString(), "", "");
											MyLog.log("jsonHeader="
													+ jsonHeader);
											serialNum++;
											String string = ttsConnect(
													jsonHeader, fdatBuffer,
													freadlen);
											// ////
											if (string.equals("TCL_FAILED"))// 连接失败
											{
												MyThreadPool.getThreadPool()
														.shutdown();
												stopRun();
												MyLog.log("stopRun");
												String result = TclStt.ERROR_CONNECT;
												jNet.mfeStopTcl();
												Recorder.this.playCallBackFunc
														.playCallBack(result);
											}
											MyLog.log("string=" + string);
										}
									});

						} else if ((detect_flag == 1)
								&& (Recorder.this.isBeginPoint == false)) {
							final byte[] fdatBuffer = datBuffer;
							final int freadlen = readlen;
							MyThreadPool.getThreadPool().execute(
									new Runnable() {

										@Override
										public void run() {
											String jsonHeader = createJSONHead(
													"mid", companyName,
													deviceType, deviceId, "",
													productId, batchSerialNum,
													new Integer(serialNum)
															.toString(), "", "");
											MyLog.log("jsonHeader="
													+ jsonHeader);
											serialNum++;
											String string = ttsConnect(
													jsonHeader, fdatBuffer,
													freadlen);
											if (string.equals("TCL_FAILED"))// 连接失败
											{
												MyThreadPool.getThreadPool()
														.shutdown();
												stopRun();
												MyLog.log("stopRun");
												String result = TclStt.ERROR_CONNECT;
												jNet.mfeStopTcl();
												Recorder.this.playCallBackFunc
														.playCallBack(result);
											}

										}
									});

						} else if (detect_flag == 2)// speak end
						{
							voiceEndCallBackFunc.voiceEndCallBack();
							stopRun();
							MyLog.log("stopRun");
							final byte[] fdatBuffer = datBuffer;
							final int freadlen = readlen;
							MyThreadPool.getThreadPool().execute(
									new Runnable() {

										@Override
										public void run() {
											String jsonHeader = createJSONHead(
													"end", companyName,
													deviceType, deviceId, "",
													productId, batchSerialNum,
													new Integer(serialNum)
															.toString(), "", "");
											MyLog.log("jsonHeader="
													+ jsonHeader);
											serialNum = 1;
											String result = ttsConnect(
													jsonHeader, fdatBuffer,
													freadlen);
											if (result.equals("TCL_FAILED"))// 连接失败
											{
												MyThreadPool.getThreadPool()
														.shutdown();
												result = TclStt.ERROR_CONNECT;
											}

											MyLog.log("ret from net is ="
													+ result);
											jNet.mfeStopTcl();
											Recorder.this.playCallBackFunc
													.playCallBack(getResultByJson(result));
										}
									});

						} else if (detect_flag == 3)// time out
						{
							MyThreadPool.getThreadPool().shutdown();
							stopRun();
							MyLog.log("stopRun");
							MyLog.log("time out");
							jNet.mfeStopTcl();
							Recorder.this.playCallBackFunc
									.playCallBack(TclStt.ERROR_READ_TIME_OUT);
						}
					}

				} else // 未指定识别方式
				{
					MyLog.log("network = " + netWork);
					if (netWork == false)// 没有网络
					{
						j.msrSendData(realBuffer, bufferRead);// 发送录音去识别引擎
						String strResult = j.msrRecognize();// 开始识别
						if (strResult.length() > 0) {
							if (strResult.equals("STARTPOINT")) {
								voiceStartCallBackFunc.voiceStartCallBack();
							} else if (strResult.equals("ENDPOINT")) {
								voiceEndCallBackFunc.voiceEndCallBack();
							} else {
								stopRun();
								MyLog.log("stopRun");
								retResult = strResult;
								j.msrStop();
								jNet.mfeStopTcl();
								Recorder.this.playCallBackFunc
										.playCallBack(retResult);
							}
						}

					} else // 有网络
					{
						int detect_flag = -1;
						if (Recorder.this.isNetStop == false) {
							jNet.mfeSendDataTcl(realBuffer, bufferRead);// 发送语音去网络识别引擎
							detect_flag = jNet.mfeDetectTcl();
						}
						MyLog.log("detect_flag=" + detect_flag);
						String strResult = "";
						if (Recorder.this.isLocalStop == false) {
							j.msrSendData(realBuffer, bufferRead);// 发送录音去识别引擎
							strResult = j.msrRecognize();// 开始识别
						}
						MyLog.log("here3");
						int readlen = jNet.mfeGetCallbackDataTcl(datBuffer,
								bufferSize);
						MyLog.log("strResult=" + strResult);
						if (strResult.length() > 0)// 本地识别
						{
							if (strResult.equals("STARTPOINT")) {
								voiceStartCallBackFunc.voiceStartCallBack();
							} else if (strResult.equals("ENDPOINT")) {
								voiceEndCallBackFunc.voiceEndCallBack();
							} else {
								j.msrStop();
								Recorder.this.isLocalStop = true;
								retResult = strResult;
								MyLog.log("this.useLocal=" + this.useLocal);
								if (this.useLocal == true) {

									stopRun();
									MyLog.log("stopRun");
									jNet.mfeStopTcl();
									Recorder.this.playCallBackFunc
											.playCallBack(retResult);
								} else {
									this.localResult = retResult;
								}
							}
						}

						if (detect_flag >= 1) {
							if ((detect_flag == 1)
									&& (Recorder.this.isBeginPoint == true))// speak
							// begin
							{
								voiceStartCallBackFunc.voiceStartCallBack();
								Recorder.this.isBeginPoint = false;
								String jsonHeader = createJSONHead("start",
										companyName, deviceType, deviceId, "",
										productId, batchSerialNum, new Integer(
												serialNum).toString(), "", "");
								MyLog.log("jsonHeader=" + jsonHeader);
								serialNum++;
								String string = ttsConnect(jsonHeader,
										datBuffer, readlen);
								if (string.equals("TCL_FAILED"))// 连接失败
								{
									jNet.mfeStopTcl();
									Recorder.this.isNetStop = true;
									this.useLocal = true;
									if (this.localResult != null) {
										stopRun();
										MyLog.log("stopRun");
										Recorder.this.playCallBackFunc
												.playCallBack(localResult);

									}
								}
								MyLog.log("string=" + string);
							} else if ((detect_flag == 1)
									&& (Recorder.this.isBeginPoint == false)) {

								String jsonHeader = createJSONHead("mid",
										companyName, deviceType, deviceId, "",
										productId, batchSerialNum, new Integer(
												serialNum).toString(), "", "");
								MyLog.log("jsonHeader=" + jsonHeader);
								serialNum++;
								String string = ttsConnect(jsonHeader,
										datBuffer, readlen);
								if (string.equals("TCL_FAILED"))// 连接失败
								{
									jNet.mfeStopTcl();
									Recorder.this.isNetStop = true;
									this.useLocal = true;
									if (this.localResult != null) {
										stopRun();
										MyLog.log("stopRun");
										Recorder.this.playCallBackFunc
												.playCallBack(localResult);

									}
								}

							} else if (detect_flag == 2)// speak end
							{
								voiceEndCallBackFunc.voiceEndCallBack();
								String jsonHeader = createJSONHead("end",
										companyName, deviceType, deviceId, "",
										productId, batchSerialNum, new Integer(
												serialNum).toString(), "", "");
								MyLog.log("jsonHeader=" + jsonHeader);
								serialNum = 1;
								String result = ttsConnect(jsonHeader,
										datBuffer, readlen);
								if (result.equals("TCL_FAILED"))// 连接失败
								{
									jNet.mfeStopTcl();
									Recorder.this.isNetStop = true;
									this.useLocal = true;
									if (this.localResult != null) {
										stopRun();
										MyLog.log("stopRun");
										Recorder.this.playCallBackFunc
												.playCallBack(localResult);
									}
								} else {
									stopRun();
									MyLog.log("stopRun");
									MyLog.log("ret from net is =" + result);
									setRecogniseFlag(false);
									recordInstance.stop();
									j.msrStop();
									jNet.mfeStopTcl();
									Recorder.this.playCallBackFunc
											.playCallBack(getResultByJson(result));
								}

							} else if (detect_flag == 3)// time out
							{

								MyLog.log("time out");
								MyLog.log("Recorder.this.localResult="
										+ Recorder.this.localResult);
								if (Recorder.this.localResult != null) {
									jNet.mfeStopTcl();
									stopRun();
									MyLog.log("stopRun");
									Recorder.this.playCallBackFunc
											.playCallBack(Recorder.this.localResult);
								} else {
									jNet.mfeStopTcl();
									Recorder.this.isNetStop = true;
									Recorder.this.useLocal = true;
								}
							} else if (detect_flag == 0) {
								jNet.mfeStopTcl();
								stopRun();
								MyLog.log("stopRun");
							}

						}
					}

				}

			}

		}

	}

	private int getRandomNum() {
		int ret;
		ret = new Random().nextInt();
		if (ret < 0)
			ret = 0 - ret;
		return ret;
	}

	private String createJSONHead(String sendFlag, String companyName,
			String deviceType, String deviceId, String deviceToken,
			String productId, String batchSerialNum, String serialNum,
			String huanid, String token) {
		String retString = null;
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("sendFlag", sendFlag);
			jsonObject.put("companyName", companyName);
			jsonObject.put("deviceType", deviceType);
			jsonObject.put("deviceId", deviceId);
			// MyLog.log("string = "+jsonObject.toString());
			jsonObject.put("deviceToken", deviceToken);
			// MyLog.log("string = "+jsonObject.toString());
			jsonObject.put("productId", productId);
			// MyLog.log("string = "+jsonObject.toString());
			jsonObject.put("batchSerialNum", batchSerialNum);
			// MyLog.log("string = "+jsonObject.toString());
			jsonObject.put("serialNum", serialNum);
			// MyLog.log("string = "+jsonObject.toString());
			jsonObject.put("huanid", huanid);
			// MyLog.log("string = "+jsonObject.toString());
			jsonObject.put("token", token);
			// MyLog.log("string = "+jsonObject.toString());
			retString = jsonObject.toString();
		} catch (JSONException e) {
			MyLog.log("error");
			e.printStackTrace();
		}

		return retString;
	}

	private String ttsConnect(String jsonHeader, byte[] buf, int len) {
		String httpUrl = "http://voice.cedock.com/hvoice/sendVoice2Engine";
		// String httpUrl = "http://www.google.com/";
		// String httpUrl = "http://www.baidu.com/";
		String retString = "";

		URL url = null;
		try {
			url = new URL(httpUrl);
		} catch (MalformedURLException e1) {
			MyLog.log("error");
			e1.printStackTrace();
		}
		HttpURLConnection urlCon = null;

		try {
			urlCon = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			MyLog.log("error");
			e1.printStackTrace();
		}
		urlCon.setDoOutput(true);
		urlCon.setDoInput(true);
		try {
			urlCon.setRequestMethod("POST");
		} catch (ProtocolException e1) {
			MyLog.log("error");
			e1.printStackTrace();
		}
		urlCon.setUseCaches(false);
		urlCon.setConnectTimeout(30000);// 设置连接超时为30s
		urlCon.setReadTimeout(30000);
		urlCon.setRequestProperty("Cookie", "");
		urlCon.setRequestProperty("Connection", "Keep-Alive");
		urlCon.setRequestProperty("Content-Type", "application/octet-stream");
		urlCon.setRequestProperty("jsonParams", jsonHeader);
		urlCon
				.setRequestProperty("Content-Length", new Integer(len)
						.toString());
		MyLog.log("start connect");
		// ////////
		try {
			urlCon.connect();
		} catch (IOException e1) {
			MyLog.log("error");
			e1.printStackTrace();
		}
		MyLog.log("end connect");
		OutputStream outputStream = null;
		try {
			outputStream = urlCon.getOutputStream();
		} catch (IOException e1) {
			MyLog.log("connect failed-2");
			return "TCL_FAILED";
		}
		MyLog.log("end urlCon.getOutputStream()");
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
				outputStream);
		try {
			bufferedOutputStream.write(buf, 0, len);
		} catch (IOException e1) {
			MyLog.log("error");
			e1.printStackTrace();
		}
		MyLog.log("here1");
		try {
			bufferedOutputStream.flush();
		} catch (IOException e1) {
			MyLog.log("error");
			e1.printStackTrace();
		}
		MyLog.log("here2");
		try {
			bufferedOutputStream.close();
		} catch (IOException e1) {
			MyLog.log("error");
			e1.printStackTrace();
		}
		;
		int code = 0;
		try {
			code = urlCon.getResponseCode();
		} catch (IOException e1) {
			MyLog.log("error");
			e1.printStackTrace();
		}
		MyLog.log("code = " + code);
		if (code != 200) {
			MyLog.log("connect failed");
			return "TCL_FAILED";
		}

		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlCon.getInputStream(), "gbk"));
			char[] retBuf = new char[1024];
			int retHasRead = 0;
			while ((retHasRead = bufferedReader.read(retBuf)) != -1) {
				retString = retString + new String(retBuf, 0, retHasRead);
			}
			bufferedReader.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			MyLog.log("error");
			e.printStackTrace();
		} catch (IOException e) {
			MyLog.log("error");
			e.printStackTrace();
		}
		return retString;
	}

	private static boolean isNetworkAvailable(Activity activity) {
		// ////
		Context context = activity.getApplicationContext();
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == conManager) {
			return false;
		} else {
			NetworkInfo[] info = conManager.getAllNetworkInfo();
			if (null != info) {
				for (int i = 0; i < info.length; i++) {
					if (NetworkInfo.State.CONNECTED == info[i].getState()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param json
	 * @return if successful return result,else return "REJECT"
	 */
	private String getResultByJson(String json) {
		MyLog.log("result json=" + json);
		String retString = "";
		JSONObject jsonObject;
		try {
			try {
				jsonObject = new JSONObject(json);
			} catch (JSONException e) {
				MyLog.log("json phase error");
				MyThreadPool.getThreadPool().shutdown();
				MyThreadPool.init();
				return retString = "REJECT";
			}

			Integer result = (Integer) jsonObject.get("result");
			if (result.intValue() == 0) {
				retString = "REJECT";
			} else {
				String message = jsonObject.getString("message");
				JSONArray jsonArray = new JSONArray(message);
				for (int i = 0; i < jsonArray.length(); i++) {
					String content = (String) ((JSONObject) jsonArray.get(i))
							.get("content");
					Integer confidence = (Integer) ((JSONObject) jsonArray
							.get(i)).get("confidence");
					if (i == 0) {
						retString = retString + content + ","
								+ String.valueOf(confidence.intValue());
					} else {
						retString = retString + "|" + content + ","
								+ String.valueOf(confidence.intValue());
					}

				}
			}
		} catch (JSONException e) {
			MyLog.log("error");
			e.printStackTrace();
		}

		return retString;
	}

	private void savePcm(short[] buf, int len) {
		int idxBuffer;
		for (idxBuffer = 0; idxBuffer < len; ++idxBuffer) {
			try {
				if (mDataOutputStreamInstance != null) {
					// MyLog.log("here");
					mDataOutputStreamInstance.writeShort(buf[idxBuffer]);
				}
			} catch (IOException e) {
				MyLog.log("error");
				e.printStackTrace();
			}
		}
	}

	private void savePcmCompress(byte[] buf, int len) {
		int idxBuffer;
		for (idxBuffer = 0; idxBuffer < len; ++idxBuffer) {
			try {
				if (mDataOutputStreamInstanceCompress != null) {
					mDataOutputStreamInstanceCompress.writeByte(buf[idxBuffer]);
				}
			} catch (IOException e) {
				MyLog.log("error");
				e.printStackTrace();
			}
		}
	}
}