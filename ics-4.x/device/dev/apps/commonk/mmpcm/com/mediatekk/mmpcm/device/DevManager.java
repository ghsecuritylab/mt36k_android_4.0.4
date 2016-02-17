package com.mediatekk.mmpcm.device;

import java.util.ArrayList;

//import com.mediatek.dm.MountPoint;

/**
 * 
 *This class represents manager local device.
 *
 */
public class DevManager {
    private static DevManager dman = null;
//    private DeviceManager dm;
    private ArrayList<DevListener> onDevListener;
    static private final String TAG = "DevManager";
    
    private DevManager(){
        onDevListener = new ArrayList<DevListener>();
//        dm = DeviceManager.getInstance();
//        dm.addListener(dmListener);
    }
    /**
     * Get device manager instance.
     * @return
     */
    public static DevManager getInstance(){
        if (dman == null) {
            synchronized (DevManager.class) {
               // if (dman == null) {
                    dman = new DevManager();
                //}
            }
        }
        return dman;
    }
    /**
     * Get mount point count.
     * @return
     */
    public int getMountCount(){
        return 0;//dm.getMountPointCount();
    }
    /**
     * Get mount point list.
     * @return
     */
//    public ArrayList<MountPoint> getMountList(){
//        return null;//dm.getMountPointList();
//    }
    /**
     * Get mount point info by specified path.
     * @param path
     * @return
     */
//    public MountPoint getPointInfo(String path){
//        return null;//dm.getMountPoint(path);
//    }
    /**
     * Add a device notify listenr.
     * @param devListener
     */
    public void addDevListener(DevListener devListener){
//        onDevListener.add(devListener);
    }
  }