package com.mediatek.tv.model;

import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 * Event active window
 *         Start                            End
 *         -----------------------------------
 * Channel |Event |Event |Event |Event |Event |
 * Channel |Event |Event |Event |Event |Event |
 * Channel |Event |Event |Event |Event |Event |
 * Channel |Event |Event |Event |Event |Event |
 * Channel |Event |Event |Event |Event |Event |
 * Channel |Event |Event |Event |Event |Event |
 *          |                               |
 *              |                       |
 *                  |               |
 *                      |    |
 *                      VVVVVV
 *                  EventActiveWindow
 * </pre>
 */

public class EventActiveWindow implements Parcelable {
	private ChannelInfo[] channels;
	private long startTime;
	private long duration;

	public EventActiveWindow() {
		super();
	}

	public ChannelInfo[] getChannels() {
		return channels;
	}

	public void setChannels(ChannelInfo[] channels) {
		if (channels == null) {
			System.err.println("Channels is null");
			return;
		}
		if (channels.length > 32) {
			System.err.println("The channel number of activewindow grate than 32,do nothing");
			return;
		}
		this.channels = channels;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String toString() {
		return "EventActiveWindow [channels=" + Arrays.toString(channels) + ", startTime=" + startTime + ", duration="
		        + duration + "]";
	}

	public int describeContents() {
		return 0;
	}

	public static final Creator<EventActiveWindow> CREATOR = new Parcelable.Creator<EventActiveWindow>() {
		public EventActiveWindow createFromParcel(Parcel source) {
			return new EventActiveWindow(source);
		}

		public EventActiveWindow[] newArray(int size) {
			return new EventActiveWindow[size];
		}
	};

	private EventActiveWindow(Parcel source) {
		readFromParcel(source);
	}

	/* has problum */
	public void writeToParcel(Parcel out, int flags) {
		if (channels != null) {
			out.writeInt(channels.length);
			for (int i = 0; i < channels.length; i++) {
				out.writeParcelable(channels[i], flags);
			}
		}

		out.writeLong(startTime);
		out.writeLong(duration);
	}

	public void readFromParcel(Parcel in) {
		int len = in.readInt();
		if (len > 0) {
			channels = new ChannelInfo[len];
		}
		for (int i = 0; i < len; i++) {
			channels[i] = (ChannelInfo) in.readParcelable(null);
		}

		startTime = in.readLong();
		duration = in.readLong();
	}
}
