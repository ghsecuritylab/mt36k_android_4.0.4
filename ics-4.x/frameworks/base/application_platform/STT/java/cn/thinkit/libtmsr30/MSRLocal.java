package cn.thinkit.libtmsr30;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.media.AudioFormat;
import android.util.Log;
import cn.thinkit.libtmfe.test.JNINET;


class MSRLocal
{
	private Activity    activity;
	private int         source;
	private TclSttPlayCallBackFunc playCallBackFunc;
	private TclSttStartCallBackFunc startCallBackFunc;
	private TclSttVoiceEndPlayCallBackFunc voiceEndPlayCallBackFunc;
	private TclSttVoiceStartPlayCallBackFunc voiceStartPlayCallBackFunc;
	private String      companyName;
	private String      deviceType;
	private String      deviceId;
	private String      productId;
	private Thread		recorderTh;
	private String		AddActiveWordFile;
	private Recorder	recorder;
	private int			frequency;
	private int			channelConfiguration;
	private int			Penalty;
	private int			Vocabulary;
	private int			reliability;
	long				mVocHandle;
	private JNI			mVREngine	= new JNI();
	private JNINET mVREngineNet = new JNINET();
	static
	{
		System.loadLibrary("tmsr30");
		System.loadLibrary("tmfe30");
	}


	public MSRLocal(String lexiconPath,Activity activity,int source,TclSttPlayCallBackFunc playCallBackFunc,TclSttStartCallBackFunc startCallBackFunc,TclSttVoiceStartPlayCallBackFunc voiceStartCallBackFunc,TclSttVoiceEndPlayCallBackFunc  voiceEndCallBackFunc,
			String companyName,String deviceType,String deviceId,String productId) throws TclSttInitException,TclSttLexiconException 
	{
		this.activity = activity;
		this.source = source;
		this.playCallBackFunc = playCallBackFunc;
		this.companyName = companyName;
		this.deviceType = deviceType;
		this.deviceId = deviceId;
		this.productId = productId;
		this.startCallBackFunc = startCallBackFunc;
		this.voiceStartPlayCallBackFunc = voiceStartCallBackFunc;
		this.voiceEndPlayCallBackFunc = voiceEndCallBackFunc;
		AddActiveWordFile = "";
		frequency = 16000;//设置取样频率
		channelConfiguration = AudioFormat.CHANNEL_IN_MONO;//设置为麦克风取样
		Penalty = 0;//设置置信水平
		Vocabulary = 3000;//设置词条数最大为3000
		reliability = 50;
		mVocHandle = 0L;
		if ((source == TclStt.SOURCE_LOCAL ||source == TclStt.SOURCE_DEFAULT) && lexiconPath == null )
		{
			throw new TclSttLexiconException("lexiconPath=null");
		} else if ((source == TclStt.SOURCE_LOCAL ||source == TclStt.SOURCE_DEFAULT) &&!new File(lexiconPath).exists())
		{
			throw new TclSttLexiconException("lexicon is no exist");

		} else
		{
			AddActiveWordFile = lexiconPath;
			Log.i("MSROnline", "mVREngine=" + mVREngine);
			mVREngine.msrInitWithPenalty(Penalty);
			mVREngine.msrSetLogLevel(0);
			Log.i("MSROnline", "mVREngine=2" + mVREngine);
			///////////		
			//mVREngine.msrInit();
			mVREngine.msrOpen();
			mVREngine.msrSetLogLevel(0);
			mVREngineNet.mfeInitTcl();
			mVREngineNet.mfeOpenTcl();
			mVocHandle = mVREngine.msrCreateVocabulary(Vocabulary);
			addWordFromFile(AddActiveWordFile);//添加辞典文件
			recorder = new Recorder(frequency, channelConfiguration, mVREngine,mVREngineNet,
					mVocHandle, reliability,activity,source,playCallBackFunc,startCallBackFunc,voiceStartCallBackFunc,voiceEndCallBackFunc,
					companyName, deviceType, deviceId, productId);
			//////
			return;
		}
	}

	public boolean start()
	{
		Log.i("MSROnline", "====================stt start()");		
//		try {
//			recorder = new Recorder(frequency, channelConfiguration, mVREngine,mVREngineNet,
//					mVocHandle, reliability,activity,source,playCallBackFunc,startCallBackFunc,this.voiceStartPlayCallBackFunc,this.voiceEndPlayCallBackFunc,
//					companyName, deviceType, deviceId, productId);
//		} catch (TclSttInitException e) {
//			MyLog.log("error");
//			e.printStackTrace();
//		}
		recorderTh = new Thread(recorder);
		recorderTh.start();
		return true;
	}
	public void setDebug(String outPath)
	{
		if(recorder!=null)
		{
			recorder.setDebug(outPath);
		}
	}
//	public String getResult()
//	{
//		return recorder.getFinalData();
//	}
//
//	public void resetResult()
//	{
//		recorder.setFinalData("REJECT");
//	}

	public void sttRelease()
	{
		recorder.sttRelease();
	}

	public List<String> readFile(String filepath) throws IOException
	{
		List<String> filecon = new ArrayList<String>();
		String m = "";
		File f = new File(filepath);
		if (!f.exists())
			throw new IOException("can not Open file");
		BufferedReader file = new BufferedReader(new InputStreamReader(
				new FileInputStream(filepath), "utf-16"));
		while ((m = file.readLine()) != null)
			if (!m.equals("0") && !m.equals(""))
				filecon.add(m);
		file.close();
		return filecon;
	}

	private int addWordFromFile(String filepath)
	{
		int num = 0;
		try
		{
			List<String> str_list = readFile(filepath);
			for (int location = 0; location < str_list.size(); location++)
			{
				int ntemp = mVREngine.msrAddActiveWord(mVocHandle,
						(String) str_list.get(location));
				if (ntemp >= 0)
					num++;
			}

		} catch (IOException e)
		{
			num = 0;
		}
		return num;
	}


}
