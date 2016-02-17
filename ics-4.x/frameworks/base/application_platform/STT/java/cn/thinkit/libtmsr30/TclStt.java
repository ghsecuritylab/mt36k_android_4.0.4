package cn.thinkit.libtmsr30;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


/**
 * 定义一个同步锁
 */
class MyLock
{
	static final String	lockOne	= "lockOne";
}


public class TclStt
{
	public static final int SOURCE_DEFAULT = 0;
	public static final int SOURCE_LOCAL = 1;
	public static final int SOURCE_ONLINE = 2;
	//public static final String ERROR_READ = "read_error";/**读取录音失败*/
	public static final String ERROR_CONNECT = "connect_error";/*连接云端失败*/
	public static final String ERROR_READ_TIME_OUT = "read_time_out_error";/*未检测到语音*/
	public static final String ERROR_UNKNOWN = "REJECT";
	private MSRLocal msrLocal = null;
	/**
	 * 
	 * @param dicPath
	 * local dictionary path,if source is TclStt.SOURCE_ONLINE,set null
	 * @param activity
	 * @param source
	 * TclStt.SOURCE_ONLINE or TclStt.SOURCE_LOCAL or TclStt.SOURCE_DEFAULT
	 * @throws TclSttLexiconException 
	 * @throws TclSttInitException 
	 * @throws Exception 
	 */
	public TclStt(String dicPath,Activity activity,int source,TclSttPlayCallBackFunc playCallBackFunc,TclSttStartCallBackFunc startCallBackFunc,TclSttVoiceStartPlayCallBackFunc voiceStartCallBackFunc,TclSttVoiceEndPlayCallBackFunc  voiceEndCallBackFunc,String companyName,String deviceType,String deviceId,String productId) throws TclSttInitException, TclSttLexiconException 
	{
		// 挂载辞典文件
		msrLocal = new MSRLocal(dicPath,activity,source,playCallBackFunc,startCallBackFunc,voiceStartCallBackFunc,voiceEndCallBackFunc,companyName,deviceType,deviceId,productId);
	}
	/**
	 * start one time recognise
	 */
	public void sttStart()
	{
		if(msrLocal!= null)
		{
		 msrLocal.start();
		}
	}
	/**
	 * this is for debug,if set the outPath,the recoder pcm file will save at outPath/test.pcm
	 * @param outPath
	 */
	public void setDebug(String outPath)
	{
		if(msrLocal!=null)
		{
			msrLocal.setDebug(outPath);
		}
	}
	/**
	 * release resource
	 */
	public void sttRelease()
	{
		if(msrLocal!= null)
		msrLocal.sttRelease();
	}

	


}
