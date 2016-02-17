package com.mediatek.dlna;

import com.mediatek.dlna.object.DLNADevice;

/**
 * This Class use to notify the new device found
 * @see  com.mediatek.dlna.DigitalMediaPlayer#start()
 * @see  com.mediatek.dlna.DigitalMediaPlayer#setDeviceEventListener(DeviceEventListener)
 * @see  com.mediatek.dlna.LeftDeviceEvent
 */
public class FoundDeviceEvent extends DeviceEvent {
    /**
     *
     * @param source The event source
     * @param device Which device found
     */
    public FoundDeviceEvent(DLNAEventSource source, DLNADevice device) {
        super(source, device);
    }
}
