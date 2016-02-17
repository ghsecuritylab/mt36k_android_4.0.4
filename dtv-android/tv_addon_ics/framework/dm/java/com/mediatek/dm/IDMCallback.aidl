package com.mediatek.dm;

import com.mediatek.dm.DeviceManagerEvent;

interface IDMCallback {
    void notifyDeviceEvent(in DeviceManagerEvent event);
}