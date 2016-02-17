package com.mediatek.dlna;

/**
 *   The DLNA event source
 */
public interface DLNAEventSource {
    /**
     *
     * @param event the DLNA event
     */
    public void notifyEvent(DLNAEvent event);
}
