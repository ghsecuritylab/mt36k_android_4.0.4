package com.mediatek.gamekit;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import com.mediatek.gamekit.GameKitEngine.GameKitListener;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;

public class MtkGameKitView extends MtkGLSurfaceView implements
		SensorEventListener {
	private TextView mFpsText = null;
	private GameKitEngine mGameKit;
	private String mInitArg;
	private Handler mHandler = new Handler();
	private float mMultiData[];
	private static final int MULTI_DATA_STRIDE = 5;
	private static final int MULTI_MAX_INPUTS = 10;
	// accelerometer related
	private boolean mWantsAccelerometer = true;
	private SensorManager mSensorManager;
	private int mSensorDelay = SensorManager.SENSOR_DELAY_GAME; // other
	private boolean m_bRet = false;
	private Context mContext;
	 private final static String TAG = "MtkGamekitView";
	 
	private class MtkOgreRender implements MtkGLSurfaceView.Renderer {

		private String TAG = "MtkOgreRender";
		private int mSurfaceWidth;
		private int mSurfaceHeight;
		private String mInitArg;

		public void Render() {
			boolean ret = false;
			ret = render(mSurfaceWidth, mSurfaceHeight, true);
			if (!ret) {
				Log.d(TAG, "render Failed ret = " + ret);
			}
		}

		/*
		 * 
		 * Construct function
		 */
		public MtkOgreRender(String initArg) {
			mInitArg = initArg;
		}

		public void onDrawFrame(GL10 gl) {
			// TODO Auto-generated method stub
			try {
				Render();
			} finally {
			}
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Surface changed: " + width + ", " + height);
			mSurfaceWidth = width;
			mSurfaceHeight = height;
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onSurfaceCreated: ....." + mInitArg);
			if (!init(mInitArg)) {
				Log.w(TAG, "init ogrekit failed");
			}
			Log.d(TAG, " SSS REACHED ?");
		}

		@Override
		public void onDestorySurface() {
			// TODO Auto-generated method stub
			cleanup();
		}
	}

	public class MtkConfigureChooser implements
			MtkGLSurfaceView.EGLConfigChooser {
		private String TAG = "MtkConfigureChooser";

		public MtkConfigureChooser() {
		}

		/*
		 * 
		 * This EGL config specification is used to specify 2.0 rendering. We
		 * use a minimum size of 4 bits for red/green/blue, but will perform
		 * actual matching in chooseConfig() below.
		 */
		// private int EGL_OPENGL_ES2_BIT = 4;
		private int[] s_configAttribs2 = { EGL10.EGL_RED_SIZE, 4,
				EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4,
				// EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
				EGL10.EGL_SAMPLE_BUFFERS, 1, EGL10.EGL_SAMPLES, 4,
				EGL10.EGL_NONE };

		@Override
		public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
			// TODO Auto-generated method stub
			// Get the number of minimally matching EGL configurations
			int[] num_config = new int[1];
			egl.eglChooseConfig(display, s_configAttribs2, null, 0, num_config);
			int numConfigs = num_config[0];
			if (numConfigs <= 0) {
				throw new IllegalArgumentException(
						"No configs match configSpec");
			}
			// Allocate then read the array of minimally matching EGL configs
			EGLConfig[] configs = new EGLConfig[numConfigs];
			egl.eglChooseConfig(display, s_configAttribs2, configs, numConfigs,
					num_config);
			return configs[0];
		}
	}


	public MtkGameKitView(Context context, String blendPath) {
		super(context);
		// TODO Auto-generated constructor stub]
		mGameKit = new GameKitEngine();
		mContext = context;
		mInitArg = blendPath;
		mMultiData = new float[MULTI_DATA_STRIDE * MULTI_MAX_INPUTS];
		if (mWantsAccelerometer && (mSensorManager == null))
			mSensorManager = (SensorManager) context
					.getSystemService(context.SENSOR_SERVICE);
		init();
	}

	public MtkGameKitView(Context context, AttributeSet attrs){
		super(context, attrs);
		mGameKit = new GameKitEngine();
		mContext = context;
		//find my attr value.
		mInitArg = attrs.getAttributeValue(null, "blend_path");		
		Log.d("Lei", "blend_path = " + mInitArg);		
		mMultiData = new float[MULTI_DATA_STRIDE * MULTI_MAX_INPUTS];
		if (mWantsAccelerometer && (mSensorManager == null))
			mSensorManager = (SensorManager) context
					.getSystemService(context.SENSOR_SERVICE);
		if (mInitArg != null) init();	
	}
	
	public void setBlendPath(String blendPath){
		mInitArg = blendPath;
		init();
	}

	private void init() {

		this.setPreserveEGLContextOnPause(true);
		this.setEGLContextClientVersion(2);
		this.setEGLConfigChooser(new MtkConfigureChooser());
		this.setRenderer(new MtkOgreRender(mInitArg));
	}

	/**
	 * 
	 * Call this function to enable an on screen fps counter.
	 */
	public void enableOnScreenFPS() {
		if (mFpsText == null) {
			mFpsText = new TextView(mContext);
			((Activity) mContext).addContentView(mFpsText, new ViewGroup.LayoutParams(100, 100));
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Auto-generated method stub
	}

	public void onSensorChanged(SensorEvent event) {
		// Auto-generated method stub
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			sensorEvent(event.sensor.getType(), event.values[0],
					event.values[1], event.values[2]);
		}
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		final int actionL = event.getAction();
		final float xL = event.getX();
		final float yL = event.getY();
		final MotionEvent eventL = event;

		this.queueEvent(new Runnable() {
			public void run() {
				Log.v(TAG, "Mouse Event action = " + actionL + "x =" + xL
						+ "y =" + yL + "event = " + eventL);
				inputEvent(actionL, xL, yL, eventL);
			}
		});

		return m_bRet;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// m_bRet = super.onKeyDown(keyCode, event);
		Log.i(TAG, "onKeyDown pressed ret = " + m_bRet);
		final int actionL = event.getAction();
		final int unicodeCharL = event.getUnicodeChar();
		final int keyCodeL = event.getKeyCode();
		final KeyEvent eventL = event;

		queueEvent(new Runnable() {
			public void run() {
				Log.v(TAG, "keyEvent unicodeChar = " + unicodeCharL
						+ ", keyCode = " + keyCodeL + "!! ");
				keyEvent(actionL, unicodeCharL, keyCodeL, eventL);
			}
		});
		return m_bRet;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// m_bRet = super.onKeyUp(keyCode, event);
		Log.i(TAG, "onKeyUp pressed ret = " + m_bRet);
		final int actionL = event.getAction();
		final int unicodeCharL = event.getUnicodeChar();
		final int keyCodeL = event.getKeyCode();
		final KeyEvent eventL = event;
		
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// send menu-message to gkMessageManager. e.g. use message-sensor
			// with "menu" as subject to react on this
			sendMessage("android", "", "menu", "");
			return true;
		}
		
		mHandler.postDelayed(new Runnable() {
			public void run() {
				queueEvent(new Runnable() {
					public void run() {
						Log.w(TAG, "keyEvent unicodeChar = " + unicodeCharL
								+ ", keyCode = " + keyCodeL + " onKeyUp!! ");
						keyEvent(actionL, unicodeCharL, keyCodeL, eventL);
					}
				});
			}
		}, 100); // delay 100ms, because onkeyup and onkeydown was quickly send
		// when press button.
		return m_bRet;
	}
	
	public void onResume() {
		super.onResume();
		if (mSensorManager != null)
			mSensorManager.registerListener(this, mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), mSensorDelay);

	}
	

	public void onPause() {

		super.onPause();
	}


	public void onStop() {

		if (mSensorManager != null)
			mSensorManager.unregisterListener(this);
	}


	public void onDestroy() {

		mMultiData = null;
	}

	/**
	 * 
	 * Function called when app requested sensor input events like
	 * accelerometer. Right now it is only called for accelerometer event. Note
	 * this is not an abstract, so that people implementing subclasses of
	 * MtkActivity that don't use Sensors don't need to implement a native stub
	 * -- we do so here in java.
	 * 
	 * @param sensorType
	 *            : The sensor type this event.
	 * @param values0
	 *            : values[0] passed to onSensorChanged(). For accelerometer:
	 *            Acceleration minus Gx on the x-axis.
	 * @param values1
	 *            : values[1] passed to onSensorChanged(). For accelerometer:
	 *            Acceleration minus Gy on the y-axis.
	 * @param values2
	 *            : values[2] passed to onSensorChanged(). For accelerometer:
	 *            Acceleration minus Gz on the z-axis.
	 * @return True if the event was handled.
	 */
	private boolean sensorEvent(int sensorType, float values0, float values1,
			float values2) {
		return true;
	}

	private boolean render(int drawWidth, int drawHeight, boolean forceRedraw) {
		return mGameKit.render(drawWidth, drawHeight, forceRedraw);
	}

	private void cleanup() {
		mGameKit.cleanup();
	}

	private boolean init(String initArg) {
		return mGameKit.init(initArg);
	}

	public boolean inputEvent(int action, float x, float y, MotionEvent event) {
		return mGameKit.inputEvent(action, x, y, event);
	}

	public boolean keyEvent(int action, int unicodeChar, int keyCode,
			KeyEvent event) {
		return mGameKit.keyEvent(action, unicodeChar, keyCode, event);
	}

	public void setOffsets(int x, int y) {
		mGameKit.setOffsets(x, y);
	}

	public void sendSensor(int sensorType, float x, float y, float z) {
		mGameKit.sendSensor(sensorType, x, y, z);
	}

	public void sendMessage(String from, String to, String topic, String body) {
		mGameKit.sendMessage(from, to, topic, body);
	}

	public GLES2TextureInfo getGLES2TextureInfo(String path) {
		return mGameKit.getGLES2TextureInfo(path);
	}

	public void runLuaChunk(String chunk) {
		mGameKit.runLuaChunk(chunk);
	}
	
	public void runLuaChunkQueueEvent(final String chunk){		
		queueEvent(new Runnable() {
			public void run() {
				mGameKit.runLuaChunk(chunk);
			}
		});
	}

	private void runLuaFunction(String path, String func) {
		mGameKit.runLuaFunction(path, func);
	}

	public void addGamekitListener(GameKitListener gkListner){
		GameKitEngine.addListener(gkListner);
	}
	
	public void removeListener(){
		GameKitEngine.removeListener();
	}
}
