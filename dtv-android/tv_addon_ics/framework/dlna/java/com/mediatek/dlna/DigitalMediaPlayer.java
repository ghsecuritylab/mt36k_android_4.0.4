package com.mediatek.dlna;


import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

/**
 *  These class find content on digital media servers (DMS) and provide playback and rendering capabilities.
 * <p/>
 * <p> Applications that need to get an instance and found or played content after call start
 *
 * @author Dongzhi Yang
 * @version %I%, %G%
 * @see com.mediatek.dlna.DigitalMediaRenderer
 * @since 1.0
 */

public class DigitalMediaPlayer implements DLNAEventSource {
    private final static DigitalMediaPlayer player = new DigitalMediaPlayer();
    private DeviceEventListener listener;
    private boolean running;
    private EventManager manager;
    private ArrayList<Integer> servers;
    private boolean started;
    private static final String TAG = "DigitalMediaPlayer";


    static {
        System.loadLibrary("dlnadmp");
    }

    /** Get an instance of DigitalMediaPlayer
     * @return  The digital media player instance, using this instance to do find or play the content on DMS
     */
    public static final DigitalMediaPlayer getInstance() {
        return player;
    }


    private DigitalMediaPlayer() {
        servers = new ArrayList<Integer>();
        manager = new EventManager();
        running = true;
        manager.start();
    }

    /**
     *  Set the device listener to receive the new device found or the found device left event, call it after start
     * @param listener  the device event listener
     * see com.mediatek.dlna#start()
     */
    public void setDeviceEventListener(DeviceEventListener listener) {
        this.listener = listener;
    }

    /**
     * Start the DLNA Digital Media Player
     * @return The result of this action, successful if return 0, else failed
     */
    public int start() {
        if(started) {
            Log.e(TAG, "DigitalMediaPlayer has been already started");
            return 0;
        }
        started = true;
        return nativeStart();
    }

    /**
     *  Stop the DLNA Digital Media Player
     * @return  The result of this action, successful if return 0, else failed
     */
    public int stop() {
        if(started) {
            started = false;
        }else {
            Log.e(TAG, "DigitalMediaPlayer not started");
            return 0;
        }
        servers.clear();
        return nativeStop();
    }

    private DLNAEvent event() {
        return nativeEvent();
    }

    //@Override
    public void notifyEvent(DLNAEvent event) {
        if (event instanceof FoundDeviceEvent) {
            if (!servers.contains(((FoundDeviceEvent) event).getDevice().hashCode())) {
                servers.add(((FoundDeviceEvent) event).getDevice().hashCode());
                if (this.listener != null) {
                    this.listener.notifyDeviceFound((FoundDeviceEvent) event);
                }
            }
        } else if (event instanceof LeftDeviceEvent) {
            if (servers.contains(((LeftDeviceEvent) event).getDevice().hashCode())) {
                servers.remove(new Integer(((LeftDeviceEvent) event).getDevice().hashCode()));
                if (this.listener != null) {
                    this.listener.notifyDeviceLeft((LeftDeviceEvent) event);
                }
            }
        }
    }

    private class EventManager extends Thread {
        @Override
        public void run() {
            Log.d(TAG, "DigitalMediaPlayer event manager started " + Thread.currentThread().getName());
            while (running) {
                DLNAEvent event = event();
                if (event == null) {
                    continue;
                }
                if (event.getSource() != null) {
                    event.getSource().notifyEvent(event);
                }
            }
            Log.d(TAG, "DigitalMediaPlayer event manager exit " + Thread.currentThread().getName());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        running = false;
        nativeExit();
    }

    private native DLNAEvent nativeEvent();

    private native void nativeExit();

    private native int nativeStart();

    private native int nativeStop();

}
