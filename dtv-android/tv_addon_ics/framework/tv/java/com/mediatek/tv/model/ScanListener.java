package com.mediatek.tv.model;

/* need application/user implement the function body ... */
public interface ScanListener {
    public void setScanProgress(String scanMode, int progress, int channels);

    public void setScanFrequence(String scanMode, int frequence);

    public void setScanCompleted(String scanMode);

    public void setScanCanceled(String scanMode);

    public void setScanError(String scanMode, int error);
}
