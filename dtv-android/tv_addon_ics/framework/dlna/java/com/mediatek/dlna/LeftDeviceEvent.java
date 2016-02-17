package com.mediatek.dlna;

import com.mediatek.dlna.object.DLNADevice;

/**
 * This Class use to notify the device left
 * @see  com.mediatek.dlna.FoundDeviceEvent
 */
public class LeftDeviceEvent extends DeviceEvent {
    /**
     *
     * @param source  The event source
     * @param device  Which device left
     */
    public LeftDeviceEvent(DLNAEventSource source, DLNADevice device) {
        super(source, device);
    }
}
