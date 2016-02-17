package com.mediatek.mmpcm.text;
/**
* The interface manager text play exception proces.
**/
public interface ITextEventListener {
	
	/**
	 * file not support
	 */
	public void fileNotSupport();
	
	/**
	 * file load complete
	 */
	public void onComplete();
	
	/**
	 * File start to play;
	 */
	public void onPrepare();
	/**
	 * exit
	 */
	public void onExit();
}
