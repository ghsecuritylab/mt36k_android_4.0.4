package com.mediatek.dlna;

import com.mediatek.dlna.object.DLNADevice;


/**
 * The device event triggered by device found or left
 */
public abstract class DeviceEvent extends DLNAEvent {
    private DLNADevice device;

    /**
     *  To new an event
     * @param source  The event source
     * @param device  Which device status change
     */
    public DeviceEvent(DLNAEventSource source, DLNADevice device) {
        super(source);
        this.device = device;
    }

    /**
     * Return a device
     * @return  The device which triggered
     */
    public DLNADevice getDevice() {
        return device;
    }
}
