package com.mediatek.dlna.object;

/**
 * User: dzyang
 * Date: 11-5-18
 */
public enum DLNADeviceType {
    /**
     * Home Network Device
     */

    DigitalMediaServer,
    DigitalMediaPlayer,
    DigitalMediaRenderer,
    DigitalMediaController,
    DigitalMediaPrinter,

    /**
     * Mobile Handheld Device
     */

    MobileDigitalMediaServer,
    MobileDigitalMediaPlayer,
    MobileDigitalMediaUploader,
    MobileDigitalMediaDownloader,
    MobileDigitalMediaController,

    /**
     * Home Infrastructure Device
     */
    MobileNetworkConnectivityFunction,
    MediaInteroperabilityUnit,
}
