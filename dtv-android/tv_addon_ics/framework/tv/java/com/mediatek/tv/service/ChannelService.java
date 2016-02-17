package com.mediatek.tv.service;

import java.util.ArrayList;
import java.util.List;

import android.os.RemoteException;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.AnalogChannelInfo;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.ChannelModel;

/**
 * This class provides channel manager service
 * <ul>
 * <li>Get channel list.</li>
 * <li>Update channel list.</li>
 * </ul>
 */

public class ChannelService implements IService {
    private static final String TAG = "[J]ChannelService";

    public static int COND_SVL_UPDATING = 1;/* When svl_lock is called. */
    public static int COND_SVL_UPDATED = 2;/* When svl_unlock is called. */
    public static int COND_SVL_CLOSED = 3;/* When svl_delete is called. */
    public static int COND_SVL_UNKNOWN = 4;

    public static int NFY_SVL_REASON_UNKNOWN = 0;
    public static int NFY_SVL_RECORD_ADD = 1 << 1;
    public static int NFY_SVL_RECORD_DEL = 1 << 2;
    public static int NFY_SVL_RECORD_MOD = 1 << 3;

    private static ArrayList<IChannelNotify> listens = new ArrayList<IChannelNotify>();

    /**
     * This class provides channel operator
     * <ul>
     * <li>Append.</li>
     * <li>Update.</li>
     * <li>Delete.</li>
     * </ul>
     */
    public enum ChannelOperator {
        APPEND, UPDATE, DELETE
    }

    public static String ChannelServiceName = "ChannelService";

    /**
     * Add listener for channelService
     * 
     * <pre>
     * TVManager tvManager = TVManager.getInstance(null);
     * ChannelService channelService = (ChannelService) tvManager.getService(ChannelService.ChannelServiceName);
     * channelService.addListener(channelServiceListen);
     * </pre>
     * 
     * @param channelNotify
     * 
     */
    public void addListener(IChannelNotify channelNotify) {
        if (channelNotify == null) {
            Logger.e(TAG, "Invalid eventNotify");
            return;
        }

        synchronized (ChannelService.this) {
            if (channelNotify != null) {
                if (!listens.contains(channelNotify)) {
                    listens.add(channelNotify);
                }
            }
        }
    }

    /**
     * Remove listener for channelService
     * 
     * <pre>
     * TVManager tvManager = TVManager.getInstance(null);
     * ChannelService channelService = (ChannelService) tvManager.getService(ChannelService.ChannelServiceName);
     * channelService.removeListener(channelServiceListen);
     * </pre>
     * 
     * @param channelNotify
     */
    public void removeListener(IChannelNotify channelNotify) {
        synchronized (ChannelService.this) {
            if (listens.contains(channelNotify)) {
                listens.remove(channelNotify);
            }
        }
    }

    protected ChannelService() {
    }

    /**
     * Get channel list from analog database.
     * 
     * <pre>
     * try {
     *     List&lt;ChannelInfo&gt; channelList = channelService.getChannelList(//
     *             ChannelCommon.DB_ANALOG);
     *     if (channelList != null) {
     *         for (int i = 0; i &lt; channelList.size(); i++) {
     *             System.out.println(channelList.get(i));
     *         }
     *     } else {
     *         System.out.println(&quot;channel list is null&quot;);
     *     }
     * } catch (TVMException e) {
     *     e.printStackTrace();
     * }
     * </pre>
     * 
     * @param name
     *            the database name (DVBT ATSC ANALOG...)
     * @throws TVMException
     * @return List
     * @see ChannelCommon#DB_ANALOG
     * @see ChannelCommon#DB_DVB
     * @see ChannelCommon#DB_ATSC
     * @see ChannelCommon#DB_ISDB
     * @see List
     * @see ArrayList
     * @see #setChannelList
     */
    public List<ChannelInfo> getChannelList(String name) throws TVMException {
        // Find svlid by name
        if (!ChannelCommon.DBSvlMap.containsKey(name)) {
            Logger.e(TAG, "Can not find svlid by name " + name);
            return null;
        }

        int svlId = ChannelCommon.DBSvlMap.get(name);
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                synchronized (ChannelService.this) {
                    List<ChannelInfo> channelList = new ArrayList<ChannelInfo>();
                    ChannelModel channelModel = new ChannelModel(channelList);
                    int ret = service.getChannelList_proxy(svlId, channelModel);
                    Logger.i(TAG, "=======================================Get channellist by DB id:" + svlId);
                    //if (TVCommon.debugMode) {
                        for (int i = 0; i < channelList.size(); i++) {
                            Logger.d(TAG, channelList.get(i).toString());
                        }
                    //}
                    if (ret == 0) {
                        return channelList;
                    } else {
                        throw new TVMException(ret, "getChannelList_proxy fail");
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Modify channel list. The list must be locked.
     * 
     * <pre>
     * <tt>Sample code</tt>: Insert 5 Analog channels
     * 
     *      
     *     List&lt;ChannelInfo&gt; channels = new ArrayList&lt;ChannelInfo&gt;();
     * 
     *     for (int i = 1; i &lt;= 5; i++) {
     *         AnalogChannelInfo analogChannelInfo = //
     *         new AnalogChannelInfo(1, i);
     *         analogChannelInfo.setAudioSys(i);
     *         analogChannelInfo.setColorSys(i);
     *         analogChannelInfo.setFrequency(i);
     *         analogChannelInfo.setServiceName(&quot;CH &quot; + i);
     *         channels.add(analogChannelInfo);
     *         System.out.println(analogChannelInfo);
     *     }
     * 
     *     int ret;
     *     try {
     *         ret = channelService.setChannelList(//
     *                 ChannelOperator.INSERT,ChannelCommon.DB_ANALOG, channels);
     *         System.out.println(ret);
     *     } catch (TVMException e) {
     *         e.printStackTrace();
     *     }
     * 
     * 
     * </pre>
     * 
     * @return 0-success other-fail
     * @throws TVMException
     * @see ChannelOperator
     * @see AnalogChannelInfo
     * @see ChannelInfo
     * @see List
     * @see ArrayList
     */
    public int setChannelList(ChannelOperator channelOperator, String dbName, List<ChannelInfo> list) throws TVMException {
        int ret = -1;
        int svlId = -1;
        if (list == null) {
            Logger.e(TAG, "Wrong arguments list is null");
            throw new TVMException(-1, "Wrong arguments list is null");
        }

        svlId = ChannelCommon.getSvlIdByName(dbName);
        if (svlId == -1) {
            Logger.e(TAG, "Wrong arguments dbname invalid" + dbName);
            throw new TVMException(-1, "Wrong arguments dbname invalid" + dbName);
        }

        Logger.i(TAG, "[" + dbName + "] Start Opertator=" + //
                channelOperator.toString() + //
                "-----------------------------------------------------------");
        // for (int i = 0; i < list.size(); i++) {
        // Logger.i(TAG, list.get(i).toString());
        // }

        /* Call remote service */
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                synchronized (ChannelService.this) {
                    ret = service.setChannelList_proxy(channelOperator.ordinal(), svlId, new ChannelModel(list));
                }
            }
            return ret;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Store memory database to file system
     * 
     * <pre>
     * try {
     *     ret = channelService.fsStoreChannelList(ChannelCommon.DB_ANALOG);
     *     if (ret != 0) {
     *         // Fail
     *     }
     * } catch (TVMException e) {
     *     e.printStackTrace();
     * }
     * </pre>
     * 
     * @param name
     *            Database name
     * 
     * @return <tt>0</tt> success,other fail
     * @throws TVMException
     * 
     * @see ChannelCommon
     * @see ChannelCommon#DB_ANALOG
     */
    public int fsStoreChannelList(String name) throws TVMException {
        int ret = -1;
        if (!ChannelCommon.DBSvlMap.containsKey(name)) {
            Logger.e(TAG, "Can not find svlid by name " + name);
            return ret;
        }

        int svlId = ChannelCommon.DBSvlMap.get(name);

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                synchronized (ChannelService.this) {
                    ret = service.fsStoreChannelList_proxy(svlId);
                    if (ret == 0) {
                        return ret;
                    } else {
                        throw new TVMException(ret, "storeChannelList_proxy fail");
                    }
                }
            }
            return ret;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Sync memory database from file system
     * 
     * 
     * 
     * <pre>
     * int ret = 0;
     * try {
     *     ret = channelService.fsSyncChannelList(ChannelCommon.DB_ANALOG);
     *     if (ret != 0) {
     *         // Fail
     *     }
     * } catch (TVMException e) {
     *     e.printStackTrace();
     * }
     * </pre>
     * 
     * 
     * @param name
     *            Database name
     * @return <tt>0</tt> success,other fail
     * 
     * @throws TVMException
     * 
     * @see ChannelCommon
     * @see ChannelCommon#DB_ANALOG
     */
    public int fsSyncChannelList(String name) throws TVMException {
        int ret = -1;
        if (!ChannelCommon.DBSvlMap.containsKey(name)) {
            Logger.e(TAG, "Can not find svlid by name " + name);
            return ret;
        }

        int svlId = ChannelCommon.DBSvlMap.get(name);

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                synchronized (ChannelService.this) {
                    ret = service.fsSyncChannelList_proxy(svlId);
                    if (ret == 0) {
                        return ret;
                    } else {
                        throw new TVMException(ret, "fsSyncChannelList fail");
                    }
                }
            }
            return ret;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Clean digital database <BR>
     * usually when do Digital full scan should call this function to clean analog database
     * 
     * 
     * <pre>
     * int ret = 0;
     * try {
     *     ret = channelService.digitalDBClean(ChannelCommon.DB_ANALOG);
     *     if (ret != 0) {
     *         // Fail
     *     }
     * } catch (TVMException e) {
     *     e.printStackTrace();
     * }
     * </pre>
     * 
     * 
     * @param name
     *            Database name
     * @return <tt>0</tt> success,other fail
     * 
     * @throws TVMException
     * 
     * @see ChannelCommon
     * @see ChannelCommon#DB_ANALOG
     */
    public int digitalDBClean(String name) throws TVMException {
        int ret = -1;
        if (!ChannelCommon.DBSvlMap.containsKey(name)) {
            Logger.e(TAG, "Can not find svlid by name " + name);
            return ret;
        }

        int svlId = ChannelCommon.DBSvlMap.get(name);

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                synchronized (ChannelService.this) {
                    ret = service.digitalDBClean_proxy(svlId);
                    if (ret == 0) {
                        return ret;
                    }
                }
            }
            return ret;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Clean Analog database,<BR>
     * usually when do Analog full scan should call this function to clean analog database
     * 
     * 
     * 
     * <pre>
     * int ret = 0;
     * try {
     *     ret = channelService.analogDBClean(ChannelCommon.DB_ANALOG);
     *     if (ret != 0) {
     *         // Fail
     *     }
     * } catch (TVMException e) {
     *     e.printStackTrace();
     * }
     * </pre>
     * 
     * @param name
     * @return
     * @throws TVMException
     */
    public int analogDBClean(String name) throws TVMException {
        int ret = -1;
        if (!ChannelCommon.DBSvlMap.containsKey(name)) {
            Logger.e(TAG, "Can not find svlid by name " + name);
            return ret;
        }
        List<ChannelInfo> analogChannels = new ArrayList<ChannelInfo>();

        List<ChannelInfo> channels = this.getChannelList(name);
        // Remove Digital channels
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i) instanceof AnalogChannelInfo) {
                analogChannels.add(channels.get(i));
            }
        }
        return this.setChannelList(ChannelOperator.DELETE, name, analogChannels);
    }

    /**
     * Notify channel updated when channel database updated<BR>
     * User should never call this function
     * 
     * @param condition
     * @param reason
     * @param data
     */
    public static void notifyChannelUpdated(int condition, int reason, int data) {
        Logger.i(TAG, "notifyChannelUpdated condition=" + condition + " reason=" + reason + " data=" + data);
        for (int i = 0; i < listens.size(); i++) {
            IChannelNotify channelNotify = listens.get(i);
            channelNotify.notifyChannelUpdated(condition, reason, data);
        }
    }
}
