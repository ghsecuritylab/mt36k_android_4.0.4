package cn.thinkit.libtmfe.test;


class JNI
{
	public native int  mfeInit();
	public native int  mfeExit();
	public native int  mfeOpen();
	public native int  mfeClose();
	public native int  mfeStart();
	public native int  mfeStop();
	public native void mfeSendData(short[] pDataBuf, int iLen);
	public native int  mfeGetCallbackData(byte[] pDataBuf, int iLen);
	public native int  mfeDetect();
}
public class JNINET
{

	private JNI jni;
	public JNINET()
	{
		jni = new JNI();
	}
	public int mfeInitTcl()
	{
		return jni.mfeInit();
	}
	public int mfeExitTcl()
	{
		return jni.mfeExit();
	}
	public int mfeOpenTcl()
	{
		return jni.mfeOpen();
	}
	public int mfeCloseTcl()
	{
		return jni.mfeClose();
	}
	public int mfeStartTcl()
	{
		return jni.mfeStart();
	}
	public int mfeStopTcl()
	{
		return jni.mfeStop();
	}

	public  void mfeSendDataTcl(short[] pDataBuf, int iLen)
	{
		jni.mfeSendData(pDataBuf,iLen);
	}
	public  int  mfeGetCallbackDataTcl(byte[] pDataBuf, int iLen)
	{
		 return jni.mfeGetCallbackData(pDataBuf,  iLen);
	}
	public  int  mfeDetectTcl()
	{
		return jni.mfeDetect();
	}
}
