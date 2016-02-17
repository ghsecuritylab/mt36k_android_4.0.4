
package com.tcl.net.sndcmd;

public class SndCmdUtility{

	static {
	    	// The runtime will add "lib" on the front and ".o" on the end of
	    	// the name supplied to loadLibrary.
	        System.loadLibrary("sndcmd_jni");
	    }

	
	public static native String SndCmd(String s);
}

