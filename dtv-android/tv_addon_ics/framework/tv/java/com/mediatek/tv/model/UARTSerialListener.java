package com.mediatek.tv.model;

/* need application/user implement the function body ... */
public interface UARTSerialListener {
    
    public void onUARTSerialNfy(int ioNotifyCond, int eventCode, byte[] data);
}
