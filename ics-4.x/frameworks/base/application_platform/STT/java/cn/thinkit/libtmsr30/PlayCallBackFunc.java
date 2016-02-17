package cn.thinkit.libtmsr30;
/**
 * tts callback class
 * @author Administrator
 * 
 *
 */
public interface PlayCallBackFunc
{
	/**
	 * invoked after return recognise result
	 * @param result
	 * 
	 */
	void  playCallBack(String result);
}
