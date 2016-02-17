package com.mediatek.dlna.object;

import com.mediatek.dlna.DLNAEvent;

import java.util.ArrayList;

/**
 * This Class is the abstract Class which indicate the current is the Content Event
 * @see com.mediatek.dlna.object.NormalContentEvent
 * @see com.mediatek.dlna.object.FailedContentEvent
 */
public abstract class ContentEvent extends DLNAEvent {
    private MediaServer server;         /* internal use, my DMS server */

    /**
     *
     * @param server  The action triggered by Which DMS
     */
    public ContentEvent(MediaServer server) {
        super(server);
    }
}
