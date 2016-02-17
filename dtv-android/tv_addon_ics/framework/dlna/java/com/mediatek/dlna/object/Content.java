package com.mediatek.dlna.object;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.InputStream;
import java.io.Serializable;

/**
 * This Class use to store the Content which found in DMS @see com.mediatek.dlna.object.MediaServer
 * If play this content (content must be the file), first get the Input Stream @see com.mediatek.dlna.object.Content#getInputStream to download the resource to local
 */

public class Content implements Parcelable, Serializable {
    private static final int DLNA_HTTP_CLIENT_FLAG_FULL_BYTEBASE_SEEKABLE          = (1 << 3);   /* object can seek by full byte */
    private static final int DLNA_HTTP_CLIENT_FLAG_FULL_TIMEBASE_SEEKABLE          = (1 << 4);   /* object can seek by full time */
    private static final int DLNA_HTTP_CLIENT_FLAG_LIMITED_BYTEBASE_SEEKABLE       = (1 << 5);   /* object can seek by limited byte */
    private static final int DLNA_HTTP_CLIENT_FLAG_LIMITED_TIMEBASE_SEEKABLE       = (1 << 6);   /* object can seek by limited time */

    private MediaServer server;
    private int sourceType;
    private ContentType type;
    private String objectId;
    private String path;
    private String title;
    private String resUri;
    private String resDuration;
    private String sampleFrequency;
    private String bitrate;
    private String colorDepth ;
    private String resolution ;
    private String nrAudioChannels ;
    private String bitsPerSample ;

    private String parentId;
    private String mimeType;
    private boolean pause;
    private boolean seek;
    long size;

    private static final int Photo = 0;
    private static final int Audio = 1;
    private static final int Video = 2;
    private static final int Playlist = 3;
    private static final int Item = 4;
    private static final int Directory = 5;

    int flag;
    int mediaType;
    int drmType;
    String dtcpInfo;
    byte[] sessionId;

    /**
     *
     * @param server        The DMS which the content in
     * @param objectId      The content object ID
     * @param path          The content object path
     * @param title         The content object title, which use to display to user
     * @param resUri        The content object resource URI which indicate the object location in DMS
     * @param resDuration   The content object duration
     * @param parentId      The content object parent object ID
     * @param mimeType      The content object MIME type
     * @param dtcpInfo      The content object DTCP info, indicate the object is protected under DTCP or not
     * @param size          The content object size
     * @param flag          The content object flag
     * @param mediaType     The content object media type
     * @param drmType       The content object DRM type which indicate the object is DRM or not
     * @param type          The content object local type, which is photo, audio, video, playlist or directory
     */

    public Content(MediaServer server, String objectId, String path, String title,
                   String resUri, String resDuration, String parentId, String mimeType,
                   String dtcpInfo, long size, int flag, int mediaType, int drmType, int type,
                   String sampleFrequency, String bitrate, String colorDepth, String resolution, String nrAudioChannels, String bitsPerSample) {
        this.server = server;
        this.objectId = objectId;
        this.path = path;
        this.title = title;
        this.resUri = resUri;
        this.size = size;
        this.resDuration = resDuration;
        this.sampleFrequency = sampleFrequency;
        this.bitrate = bitrate;
        this.colorDepth = colorDepth;
        this.resolution = resolution;
        this.nrAudioChannels = nrAudioChannels;
        this.bitsPerSample = bitsPerSample;
        this.parentId = parentId;
        this.mimeType = mimeType;
        this.flag = flag;
        this.mediaType = mediaType;
        this.drmType = drmType;
        this.dtcpInfo = dtcpInfo;
        this.sourceType = type;
        switch (type) {
            case Photo:
                this.type = ContentType.Photo;
                break;
            case Audio:
                this.type = ContentType.Audio;
                break;
            case Video:
                this.type = ContentType.Video;
                break;
            case Playlist:
                this.type = ContentType.Playlist;
                break;
            case Item:
                this.type = ContentType.Item;
                break;
            case Directory:
            default:
                this.type = ContentType.Directory;
                break;
        }
        sessionId = new byte[0];
        /*
		pause = ((flag&DLNA_HTTP_CLIENT_FLAG_FULL_BYTEBASE_SEEKABLE)|(flag&DLNA_HTTP_CLIENT_FLAG_FULL_TIMEBASE_SEEKABLE)|(flag&DLNA_HTTP_CLIENT_FLAG_LIMITED_BYTEBASE_SEEKABLE)|(flag&DLNA_HTTP_CLIENT_FLAG_LIMITED_TIMEBASE_SEEKABLE))!=0;
		seek = ((flag&DLNA_HTTP_CLIENT_FLAG_FULL_BYTEBASE_SEEKABLE)|(flag&DLNA_HTTP_CLIENT_FLAG_LIMITED_BYTEBASE_SEEKABLE))!=0;
		*/
		pause = ((flag&DLNA_HTTP_CLIENT_FLAG_FULL_BYTEBASE_SEEKABLE))!=0;
		seek = ((flag&DLNA_HTTP_CLIENT_FLAG_FULL_BYTEBASE_SEEKABLE))!=0;
	}

    /**
     * If the content protected under the WMDRM-PD, the user must set the session ID to decrypt the content
     * @param sessionId  The content object WMDRM-PD session ID
     */
    public void setSessionId(byte[] sessionId) {
        this.sessionId = sessionId;
    }

    /**
     *
     * @return  The content object in which DMS
     */
    public MediaServer getServer() {
        return server;
    }

    /**
     *
     * @return  The content object type
     */
    public ContentType getType() {
        return type;
    }

    /**
     * @return  The content object ID
     */
    public String getObjectId() {
        return this.objectId;
    }

    /**
     *
     * @return   The content object path
     */
    public String getPath() {
        return path;
    }

    /**
     *
     * @return  The content object parent object ID
     */
    public String getParentId() {
        return parentId;
    }

    /**
     *
     * @return  The content object title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return   The content object file size
     */
    public long getSize() {
        return size;
    }

    /**
     *
     * @return   The content object resource URI
     */
    public String getResUri() {
        return resUri;
    }

    /**
     *
     * @return   The content object resource duration
     */
    public String getResDuration() {
        return resDuration;
    }

    /**
     *
     * @return   The content object MIME type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     *
     * @return   The content object DRM type
     */
    public int getDrmType() {
        return drmType;
    }

    /**
     *
     * @return  The content object media type
     */
    public int getMediaType() {
        return mediaType;
    }

    /**
     *
     * @return  The content object flag
     */
    public int getFlag() {
        return flag;
    }

    /**
     *
     * @return  The content object DTCP info
     */
    public String getDtcpInfo() {
        return dtcpInfo;
    }

    /**
     *
     * @return  The content object WMDRM-PD session ID
     */
    public byte[] getSessionId() {
        return sessionId;
    }

    /**
     *
     * @return  true if the content is directory
     */
    public boolean isDirectory() {
        return this.type == ContentType.Directory;
    }

    /**
     *
     * @return  true if the content is file
     */
    public boolean isFile() {
        return !isDirectory();
    }

    /**
     *
     * @return  the input stream which created by content object file
     */
    public InputStream getInputStream() {
        return new ContentInputStream(this);
    }

    /**
     *
     * @return  true if the object can pause
     */
    public boolean canPause() {
        return pause;
    }

    /**
     *
     * @return true if the object can seek
     */
    public boolean canSeek() {
        return seek;
    }

    public String getSampleFrequency() {
        return sampleFrequency;
    }

    public String getBitrate() {
        return bitrate;
    }

    public String getColorDepth() {
        return colorDepth;
    }

    public String getResolution() {
        return resolution;
    }

    public String getNrAudioChannels() {
        return nrAudioChannels;
    }

    public String getBitsPerSample() {
        return bitsPerSample;
    }

    //@Override
    public int describeContents() {
        return 0;
    }

    //@Override
    public void writeToParcel(Parcel parcel, int i) {
        /* String objectId, String path, String title, String resUri,  String resDuration, String parentId, String mimeType, String dtcpInfo, long size, int flag, int mediaType, int drmType, int type */

        server.writeToParcel(parcel, i);
        //b.putParcelable("server", server);

        Bundle b = new Bundle();
        b.setClassLoader(getClass().getClassLoader());

        b.putString("objectId", objectId);
        b.putString("path", path);
        b.putString("title", title);
        b.putString("resUri", resUri);
        b.putString("resDuration", resDuration);

        b.putString("sampleFrequency", sampleFrequency);
        b.putString("bitrate", bitrate);
        b.putString("colorDepth", colorDepth);
        b.putString("resolution", resolution);
        b.putString("nrAudioChannels", nrAudioChannels);
        b.putString("bitsPerSample", bitsPerSample);

        b.putString("parentId", parentId);
        b.putString("mimeType", mimeType);
        b.putString("dtcpInfo", dtcpInfo);
        b.putLong("size", size);
        b.putInt("flag", flag);
        b.putInt("mediaType", mediaType);
        b.putInt("drmType", drmType);
        b.putInt("type", sourceType);
        b.putByteArray("sessionId", sessionId);


        parcel.writeBundle(b);
    }

    public static final Parcelable.Creator<Content> CREATOR = new Parcelable.Creator<Content>() {

        public Content createFromParcel(Parcel source) {
            //MediaServer server = b.getParcelable("server");
            MediaServer server = MediaServer.CREATOR.createFromParcel(source);

            Bundle b = source.readBundle();
            b.setClassLoader(getClass().getClassLoader());


            String objectId = b.getString("objectId");
            String path = b.getString("path");
            String title = b.getString("title");
            String resUri = b.getString("resUri");
            String resDuration = b.getString("resDuration");

            String sampleFrequency = b.getString("sampleFrequency");
            String bitrate = b.getString("bitrate");
            String colorDepth = b.getString("colorDepth");
            String resolution = b.getString("resolution");
            String nrAudioChannels = b.getString("nrAudioChannels");
            String bitsPerSample = b.getString("bitsPerSample");

            String parentId = b.getString("parentId");
            String mimeType = b.getString("mimeType");
            String dtcpInfo = b.getString("dtcpInfo");
            long size = b.getLong("size");
            int flag = b.getInt("flag");
            int mediaType = b.getInt("mediaType");
            int drmType = b.getInt("drmType");
            int type = b.getInt("type");
            byte[] sessionId = b.getByteArray("sessionId");


            Content content = new Content(server, objectId, path,
                    title, resUri, resDuration,
                    parentId, mimeType, dtcpInfo, size,
                    flag, mediaType, drmType, type, sampleFrequency, bitrate, colorDepth, resolution, nrAudioChannels, bitsPerSample);
            content.setSessionId(sessionId);
            return content;
        }

        public Content[] newArray(int size) {
            return new Content[size];
        }

    };


}
