package com.mediatek.tv.model;

import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

public class EventCommand implements Parcelable {
	private boolean actualOnly = false;
	private int maxDay = 8;
	private String[] prefLanuage = new String[4];
	private EventActiveWindow activeWindow = null;
	private int eventMinSeconds = 60;
	private boolean fakeEventInsertionEnable = false;
	private int fakeEventMinSecond = 60;
	private boolean timeConfictAllow = false;
	private boolean partialOverapAllow = true;
	private String eventDetailSeparator = "";
	private ChannelInfo currentChannelInfo = null;
	private boolean doRestart = false;
	private boolean doClean = false;
	private boolean doEnable = true;
	private String tunerName = "";
	private int commandMask = 0;

	public EventCommand() {
		super();
	}

	public boolean isActualOnly() {
		return actualOnly;
	}

	public void setActualOnly(boolean actualOnly) {
		this.actualOnly = actualOnly;
	}

	public int getMaxDay() {
		return maxDay;
	}

	public void setMaxDay(int maxDay) {
		this.maxDay = maxDay;
	}

	public String[] getPrefLanuage() {
		return prefLanuage;
	}

	public void setPrefLanuage(String[] prefLanuage) {
		if (prefLanuage != null) {
			System.arraycopy(prefLanuage, 0, this.prefLanuage, 0, prefLanuage.length);
		}
	}

	public EventActiveWindow getActiveWindow() {
		return activeWindow;
	}

	public void setActiveWindow(EventActiveWindow activeWindow) {
		this.activeWindow = activeWindow;
	}

	public int getEventMinSeconds() {
		return eventMinSeconds;
	}

	public void setEventMinSeconds(int eventMinSeconds) {
		this.eventMinSeconds = eventMinSeconds;
	}

	public boolean isFakeEventInsertionEnable() {
		return fakeEventInsertionEnable;
	}

	public void setFakeEventInsertionEnable(boolean fakeEventInsertionEnable) {
		this.fakeEventInsertionEnable = fakeEventInsertionEnable;
	}

	public int getFakeEventMinSecond() {
		return fakeEventMinSecond;
	}

	public void setFakeEventMinSecond(int fakeEventMinSecond) {
		this.fakeEventMinSecond = fakeEventMinSecond;
	}

	public boolean isTimeConfictAllow() {
		return timeConfictAllow;
	}

	public void setTimeConfictAllow(boolean timeConfictAllow) {
		this.timeConfictAllow = timeConfictAllow;
	}

	public boolean isPartialOverapAllow() {
		return partialOverapAllow;
	}

	public void setPartialOverapAllow(boolean partialOverapAllow) {
		this.partialOverapAllow = partialOverapAllow;
	}

	public String getEventDetailSeparator() {
		return eventDetailSeparator;
	}

	public void setEventDetailSeparator(String eventDetailSeparator) {
		this.eventDetailSeparator = eventDetailSeparator;
	}

	public ChannelInfo getCurrentChannelInfo() {
		return currentChannelInfo;
	}

	public void setCurrentChannelInfo(ChannelInfo currentChannelInfo) {
		this.currentChannelInfo = currentChannelInfo;
	}

	public boolean isDoRestart() {
		return doRestart;
	}

	public void setDoRestart(boolean doRestart) {
		this.doRestart = doRestart;
	}

	public boolean isDoClean() {
		return doClean;
	}

	public void setDoClean(boolean doClean) {
		this.doClean = doClean;
	}

	public boolean isDoEnable() {
		return doEnable;
	}

	public void setDoEnable(boolean doEnable) {
		this.doEnable = doEnable;
	}

	public String getTunerName() {
		return tunerName;
	}

	public void setTunerName(String tunerName) {
		this.tunerName = tunerName;
	}

	public int getCommandMask() {
		return commandMask;
	}

	public void setCommandMask(int commandMask) {
		this.commandMask = commandMask;
	}

	public int describeContents() {
		return 0;
	}

	public static final Creator<EventCommand> CREATOR = new Parcelable.Creator<EventCommand>() {
		public EventCommand createFromParcel(Parcel source) {
			return new EventCommand(source);
		}

		public EventCommand[] newArray(int size) {
			return new EventCommand[size];
		}
	};

	private EventCommand(Parcel source) {
		readFromParcel(source);
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(booleanToInt(actualOnly));
		out.writeInt(maxDay);
		out.writeStringArray(prefLanuage);
		out.writeParcelable(activeWindow, flags);
		out.writeInt(eventMinSeconds);
		out.writeInt(booleanToInt(fakeEventInsertionEnable));
		out.writeInt(fakeEventMinSecond);
		out.writeInt(booleanToInt(timeConfictAllow));
		out.writeInt(booleanToInt(partialOverapAllow));
		out.writeString(eventDetailSeparator);
		out.writeParcelable(currentChannelInfo, flags);
		out.writeInt(booleanToInt(doRestart));
		out.writeInt(booleanToInt(doClean));
		out.writeInt(booleanToInt(doEnable));
		out.writeString(tunerName);
		out.writeInt(commandMask);
	}

	public void readFromParcel(Parcel in) {
		actualOnly = intToBoolean(in.readInt());
		maxDay = in.readInt();
		in.readStringArray(prefLanuage);
		activeWindow = in.readParcelable(null);
		eventMinSeconds = in.readInt();
		fakeEventInsertionEnable = intToBoolean(in.readInt());
		fakeEventMinSecond = in.readInt();
		timeConfictAllow = intToBoolean(in.readInt());
		partialOverapAllow = intToBoolean(in.readInt());
		eventDetailSeparator = in.readString();
		currentChannelInfo = in.readParcelable(null);
		doRestart = intToBoolean(in.readInt());
		doClean = intToBoolean(in.readInt());
		doEnable = intToBoolean(in.readInt());
		tunerName = in.readString();
		commandMask = in.readInt();
	}

	public String toString() {
		return "EventCommand [actualOnly=" + actualOnly + ", maxDay=" + maxDay + ", prefLanuage="
		        + Arrays.toString(prefLanuage) + ", activeWindow=" + activeWindow + ", eventMinSeconds="
		        + eventMinSeconds + ", fakeEventInsertionEnable=" + fakeEventInsertionEnable + ", fakeEventMinSecond="
		        + fakeEventMinSecond + ", timeConfictAllow=" + timeConfictAllow + ", partialOverapAllow="
		        + partialOverapAllow + ", eventDetailSeparator=" + eventDetailSeparator + ", currentChannelInfo="
		        + currentChannelInfo + ", doRestart=" + doRestart + ", doClean=" + doClean + ", doEnable=" + doEnable
		        + ", tunerName=" + tunerName + ", commands=" + Integer.toHexString((int) commandMask) + "]";
	}

	private int booleanToInt(boolean b) {
		return ((b == true) ? 1 : 0);
	}

	private boolean intToBoolean(int i) {
		return ((i == 1) ? true : false);
	}
}
