package com.mediatek.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class EventComponent implements Parcelable {
    private short streamContent;
    private short componentType;
    private short componentTag;

    public short getStreamContent() {
        return streamContent;
    }

    public void setStreamContent(short streamContent) {
        this.streamContent = streamContent;
    }

    public short getComponentType() {
        return componentType;
    }

    public void setComponentType(short componentType) {
        this.componentType = componentType;
    }

    public short getComponentTag() {
        return componentTag;
    }

    public void setComponentTag(short componentTag) {
        this.componentTag = componentTag;
    }

    public EventComponent() {
        super();
    }

    public EventComponent(short streamContent, short componentType, short componentTag) {
        super();
        this.streamContent = streamContent;
        this.componentType = componentType;
        this.componentTag = componentTag;
    }

    public int describeContents() {
        return 0;
    }

    public static final Creator<EventComponent> CREATOR = new Parcelable.Creator<EventComponent>() {
        public EventComponent createFromParcel(Parcel source) {
            return new EventComponent(source);
        }

        public EventComponent[] newArray(int size) {
            return new EventComponent[size];
        }
    };

    private EventComponent(Parcel source) {
        readFromParcel(source);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(streamContent);
        out.writeInt(componentType);
        out.writeInt(componentTag);
    }

    public void readFromParcel(Parcel in) {
        streamContent = (short) in.readInt();
        componentType = (short) in.readInt();
        componentTag = (short) in.readInt();
    }
}