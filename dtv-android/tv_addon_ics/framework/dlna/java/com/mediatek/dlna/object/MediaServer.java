package com.mediatek.dlna.object;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import com.mediatek.dlna.DLNAEvent;
import com.mediatek.dlna.DLNAEventSource;

import java.io.Serializable;

/**
 *  This Class stores content and make it available to networked digital media players (DMP) and digital media renderers (DMR)
 *  Which
 */

public class MediaServer implements DLNADevice, DLNAEventSource {
    /**
     * The Media Server Object ID
     */
    public static final String OBJECT_ID = "0";


    /**
     * Browse sorted by title ascending
     */
    public static final String SORT_ASCENDING_CRITERIA_TITLE = "+dc:title";
    /**
     * Browse sorted by title descending
     */
    public static final String SORT_DESCENDING_CRITERIA_TITLE = "-dc:title";

     /**
     * Browse sorted by artist ascending
     */
    public static final String SORT_ASCENDING_CRITERIA_ARTIST = "+upnp:artist";
    /**
     * Browse sorted by artist descending
     */
    public static final String SORT_DESCENDING_CRITERIA_ARTIST = "-upnp:artist";

     /**
     * Browse sorted by date ascending
     */
    public static final String SORT_ASCENDING_CRITERIA_DATE = "+dc:date";
    /**
     * Browse sorted by date descending
     */
    public static final String SORT_DESCENDING_CRITERIA_DATE = "-dc:date";

    /**
     * Browse filter the basic info
     */
    public static final String FILTER_BASIC_INFO = "res,res@resolution,res@protocolInfo,res@size,res@duration,res@bitrate,res@sampleFrequency,res@bitsPerSample,res@nrAudioChannels,res@protection,dc:creator,dc:date,upnp:genre,upnp:album,upnp:originalTrackNumber,upnp:channelNr,upnp:scheduledStartTime,upnp:scheduledEndTime,upnp:icon,upnp:albumArtURI,upnp:artist,container@childCount";

    /**
     * Browse filter all info
     */
    public static final String FILTER_ALL_INFO = "*";


    private String name;
    private int id;
    private ContentEventListener listener;

    //@Override
    public void notifyEvent(DLNAEvent event) {
        if (this.listener != null) {
            if (event instanceof NormalContentEvent) {
                this.listener.notifyContentSuccessful((NormalContentEvent) event);
            } else if (event instanceof FailedContentEvent) {
                this.listener.notifyContentFailed((FailedContentEvent) event);
            }

        }
    }

    //@Override
    public int describeContents() {
        return 0;
    }

    //@Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(id);
    }

    public static final Parcelable.Creator<MediaServer> CREATOR = new Parcelable.Creator<MediaServer>() {

        public MediaServer createFromParcel(Parcel source) {
            String name = source.readString();
            int id = source.readInt();

            MediaServer server = new MediaServer(name, id);
            return server;
        }

        public MediaServer[] newArray(int size) {
            return new MediaServer[size];
        }

    };


    /**
     *  The browse action flag
     *  Metadata use to  browse object self
     *  BrowseDirectChildren use to browse object children
     */
    public static enum BrowseFlag {
        Metadata,                                           /* browse object self */
        BrowseDirectChildren,                              /* browse object children */
    }


    /**
     *
     * @param name Media server name
     * @param id   Media Serve ID
     */
    public MediaServer(String name, int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * Set the listener first while call browse
     * @param listener The content event listener
     */
    public void setActionEventListener(ContentEventListener listener) {
        this.listener = listener;
    }

    /**
     * Browse the object self info or sub directory info
     * @param objectId    The object ID
     * @param flag        The browse flag
     * @param startIndex  Start index, 0 as begin
     * @param request     Request object number
     * @param filter      The filter
     * @param sort        Sorted by
     * @return  The action handle more than 0 indicate the browse action successful else failed
     */
    public int browse(String objectId, BrowseFlag flag, int startIndex, int request, String filter, String sort) {
        int handle = nativeBrowse(this.id, objectId, flag.ordinal(), startIndex, request, filter, sort);
        return handle;
    }

    /**
     * @param handle  The cancel the action handle
     */
    public void cancel(int handle) {
        nativeCancel(this.id, handle);
    }

    //@Override
    public DLNADeviceType getType() {
        return DLNADeviceType.DigitalMediaServer;
    }


    public String getObjectId() {
        return OBJECT_ID;
    }

    //@Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        MediaServer s = (MediaServer) o;
        if (s == null) {
            return false;
        }
        return this.id == s.id;
    }


    private native int nativeBrowse(int id, String objectId, int flag, int startIndex, int request, String filter, String sort);

    private native void nativeCancel(int id, int handle);
}
