package com.mediatek.dlna.object;

/**
 * This Class use to notify the browse action which called in Media Server is successful or failed
 * @see com.mediatek.dlna.object.MediaServer#setActionEventListener(ContentEventListener)
 * @see  com.mediatek.dlna.object.MediaServer#browse(String, com.mediatek.dlna.object.MediaServer.BrowseFlag, int, int, String, String)
 */

public interface ContentEventListener {
    /**
     * @param event Notify the browse action successful event
     */
    void notifyContentSuccessful(NormalContentEvent event);

    /**
     *
     * @param event Notify the browse failed event
     */
    void notifyContentFailed(FailedContentEvent event);
}
