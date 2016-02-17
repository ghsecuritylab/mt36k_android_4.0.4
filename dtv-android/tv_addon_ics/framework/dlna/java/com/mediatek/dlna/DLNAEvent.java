package com.mediatek.dlna;

/**
 * The DLNA Event use to indicate the current Event is triggered by DLNA source
 */
public abstract class DLNAEvent {
    protected transient DLNAEventSource source;

    protected DLNAEvent(DLNAEventSource source) {
        this.source = source;
    }

    /**
     *
     * @return  The Event source indicate which triggered
     */
    public DLNAEventSource getSource() {
        return source;
    }
}
