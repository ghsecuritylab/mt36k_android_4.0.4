 1.frameworks/base/services/java/com/android/server/AlarmManagerService.java
   增加函数：
   public void setPoweroffTimer(long rel);
   public void cancelPoweroffTimer();
   public long getReminderRelativeTime();
   public void onLastMinuteWarn();
   增加线程：
   TimeMonitorThread

2.frameworks/base/core/java/android/app/IAlarmManager.aidl
    增加接口：
    void setPoweroffTimer(long rel);
    void cancelPoweroffTimer();
    long getReminderRelativeTime();

3.frameworks/base/services/java/com/android/server/ShutdownActivity.java
    增加上层注入keyEvent:
		KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, eventCode, 0);
		KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, eventCode, 0);
		InputManager.getInstance().injectInputEvent(down,
		                        InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
		InputManager.getInstance().injectInputEvent(up,
		                        InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);





移植说明
0. addon_stub_defs
	+com.tcl.devicemanager.DeviceManagerEvent

1. Camera, Mic插拔通知
	frameworks/base/services/java/com/android/server/SystemServer.java

	import com.tcl.devicemanager.CameraObserver;
	import com.tcl.devicemanager.MicrophoneObserver;
	... ...
	class ServerThread extends Thread {
		... ...
		@Override
		public void run() {
			... ...
			//begin add for USB camera broadcast; qik@tcl.com;2013.03.06
			CameraObserver Camera = null;
			MicrophoneObserver Microphone = null;
			//end add for USB camera broadcast
			... ...
            //begin add for USB camera brocast;qik@tcl.com;2013.03.06
            try {
                Slog.i(TAG, "Camera Observer");
                 // Listen for Camera changes
                Camera = new CameraObserver(context);
                } catch (Throwable e) {
                Slog.e(TAG, "Failure starting CameraObserver", e);
             }

            try {
                Slog.i(TAG, "Microphone Observer");
                // Listen for Microphone changes
                 Microphone = new MicrophoneObserver(context);
            } catch (Throwable e) {
                Slog.e(TAG, "Failure starting MicrophoneObserver", e);
            }   
			// 
			... ...
			//add for USB camera brocast;qik@tcl.com;20130306
			final CameraObserver CameraF = Camera;
			final MicrophoneObserver MicrophoneF = Microphone;
			//end add;
			
		}
		... ... 
	}
