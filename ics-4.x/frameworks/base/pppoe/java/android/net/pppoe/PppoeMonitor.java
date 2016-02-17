/*
 * Copyright (C) 2010 The Android-X86 Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Yi Sun <beyounn@gmail.com>
 */
package android.net.pppoe;

import java.util.regex.Matcher;

import android.net.NetworkInfo;
import android.util.Config;
import android.util.Slog;
import java.util.StringTokenizer;

/**
 * Listens for events from kernel, and passes them on
 * to the {@link EtherentStateTracker} for handling. Runs in its own thread.
 *
 * @hide
 */
public class PppoeMonitor {
	private static final String TAG = "PppoeMonitor";
	private static final int CONNECTING = 0;
	private static final int CONNECTED	= 1;
	private static final int DISCONNECTED = 2;
	private static final int AUTH_FAILED	= 3;
	private static final String connectedEvent =	"CONNECTED";
	private static final String disconnectedEvent = "DISCONNECTED";
	private static final int LINK_UP = 30;
	private static final int LINK_DOWN = 31;
	private static final int LINK_AUTH_FAIL = 32;
	private static final int LINK_TIME_OUT = 33;
	private static final int LINK_PPP_FAIL = 34;
	private static final int ADD_ADDR = 6;
	private static final int RM_ADDR = 7;

	private PppoeStateTracker mTracker;

	public PppoeMonitor(PppoeStateTracker tracker) {
		mTracker = tracker;
	}

	public void startMonitoring() {
		new MonitorThread().start();
	}

	class MonitorThread extends Thread {

		public MonitorThread() {
			super("POEMonitor");
		}

		public void run() {
			int index;
			int i;

			//noinspection InfiniteLoopStatement
			for (;;)
			{
				String eventName = PppoeNative.waitEvent();
				 if (eventName == null) {
					continue;
				}
				Slog.i(TAG, "--rcv event:" + eventName+"--");

				/*
				 * Map event name into event enum
				 */
				String [] events = eventName.split(":");
				index = events.length;
				if (index < 2)
					continue;
				i = 0;
				while (index != 0 && i < index-1) 
				{
					int event = 0;
					int cmd =Integer.parseInt(events[i+1]);
					if ( cmd == LINK_DOWN) {
						event = DISCONNECTED;
						Slog.i(TAG, "--Link_down--");
						handleEvent(events[i],event);
					}
					else if (cmd == LINK_UP ) {
						event = CONNECTED;
						Slog.i(TAG, "--Link_up--");
						handleEvent(events[i],event);
					}
					else if(cmd== LINK_AUTH_FAIL)
					{
						event = AUTH_FAILED;
						Slog.i(TAG, "--Fail--");
						handleEvent(events[i],event);
					}
					i = i + 2;
				}
			}
		}
		/**
		 * Handle all supplicant events except STATE-CHANGE
		 * @param event the event type
		 * @param remainder the rest of the string following the
		 * event name and &quot;&#8195;&#8212;&#8195;&quot;
		 */
		void handleEvent(String ifname,int event) {
			switch (event) {
				case DISCONNECTED:
					mTracker.notifyStateChange(ifname,NetworkInfo.DetailedState.DISCONNECTED);
					break;
				case CONNECTED:
					mTracker.notifyStateChange(ifname,NetworkInfo.DetailedState.CONNECTED);
					break;
				case AUTH_FAILED:
					mTracker.notifyPhyConnected(ifname);
					break;
				default:
					mTracker.notifyStateChange(ifname,NetworkInfo.DetailedState.FAILED);
			}
		}

	}
}
