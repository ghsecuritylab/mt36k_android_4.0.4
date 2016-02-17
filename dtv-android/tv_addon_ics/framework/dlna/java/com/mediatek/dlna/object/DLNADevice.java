package com.mediatek.dlna.object;


import android.os.Parcelable;

import java.io.Serializable;

/**
 * This interface indicate the current object is the DLNA device
 */

public interface DLNADevice extends Parcelable, Serializable {
    /**
     * @return  The device type
     * @see com.mediatek.dlna.object.DLNADeviceType
     */
    DLNADeviceType getType();

    /**
     * @return  The device name
     */
    String getName();
}
