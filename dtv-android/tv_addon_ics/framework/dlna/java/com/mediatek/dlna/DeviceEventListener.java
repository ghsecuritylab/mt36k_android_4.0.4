package com.mediatek.dlna;


/**
 * This Class use to monitor the DMP found or left device
 * If the device found, notify the listener the device found event
 * or the device left, the left event would be send to listener
 *  * @see  com.mediatek.dlna.DigitalMediaPlayer#setDeviceEventListener(DeviceEventListener)
 */
public interface DeviceEventListener {
    /**
     * Notify the listener new device found
     * @param event The new device found event
     */
    public void notifyDeviceFound(FoundDeviceEvent event);

    /**
     * Notify the listener the device left event
     * @param event  The device left event
     */
    public void notifyDeviceLeft(LeftDeviceEvent event);
}
