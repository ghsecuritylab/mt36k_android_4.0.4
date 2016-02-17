package com.mediatek.gamekit;


import java.util.LinkedList;
import java.util.List;

import android.view.KeyEvent;
import android.view.MotionEvent;

public class GameKitEngine {
	private static String TAG = "GameKitEngine";
	private static List<GameKitListener> listener = new LinkedList<GameKitEngine.GameKitListener>();
	
	public GameKitEngine(){
		
	}

	
	public native boolean render(int drawWidth, int drawHeight,
			boolean forceRedraw);
	public native void cleanup();
	public native boolean init(String initArg);
	public native boolean inputEvent(int action, float x, float y,
			MotionEvent event);
	public native boolean keyEvent(int action, int unicodeChar, int keyCode,
			KeyEvent event);
	public native void setOffsets(int x, int y);
	public native void sendSensor(int sensorType, float x, float y, float z);
	public native void sendMessage(String from, String to, String topic,
			String body);	
	public native GLES2TextureInfo getGLES2TextureInfo(String path);
	public native void runLuaChunk(String chunk);
	public native void runLuaFunction(String path, String func);
	
	static {
		System.loadLibrary("ogrekit");
		//addListener(mGkListener);
	}
	
	//temp use.
	public static void callbackAddr(int mode, int w, int h, int addr){

	}

	public static void fireTypeIntMessage(int type, int value) {
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).onMessage(type, value);
		}

	}

	public static void fireStringMessage(String from, String to,
			String subject, String body) {
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).onMessage(from, to, subject, body);
		}
	}

	public static void addListener(GameKitListener gkListener) {
		listener.add(gkListener);
	}

	public static void removeListener(){
		listener.clear();
	}
	public static interface GameKitListener {
		void onMessage(int type, int value);

		void onMessage(String from, String to, String subject, String body);
	}
}
