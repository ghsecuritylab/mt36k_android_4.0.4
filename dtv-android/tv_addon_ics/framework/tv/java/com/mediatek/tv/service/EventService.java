package com.mediatek.tv.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.RemoteException;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.DvbChannelInfo;
import com.mediatek.tv.model.EventInfo;
import com.mediatek.tv.model.EventActiveWindow;
import com.mediatek.tv.model.EventCommand;
import com.mediatek.tv.model.EventUpdateReason;

/**
 * This class provides event manager service
 * <ul>
 * </ul>
 */

public class EventService implements IService {
    private static final String TAG = "[J]EventService";

    /**
     * ask to load and cache events from EIT actual only (default: false)
     */
    public static int EVENT_CMD_CFG_ACTUAL_ONLY = 1 << 3;

    /**
     * restrict cache days. For EU models, the days of EIT loading will$ 150 be also restricted. (default: 8 days)
     */
    public static int EVENT_CMD_CFG_MAX_DAYS = 1 << 4;

    /**
     * Set language for retrieve EIT
     */
    public static int EVENT_CMD_CFG_PREF_LANG = 1 << 5;
    /**
     * Special country code for decode character if no coding flag in EIT
     */
    public static int EVENT_CMD_CFG_COUNTRY_CODE = 1 << 6;
    /**
     * Configure active window for monitor EIT changed
     */
    public static int EVENT_CMD_CFG_ACTIVE_WIN = 1 << 7;
    /**
     * Configure minimal second for ignore invalid event
     */
    public static int EVENT_CMD_CFG_EVENT_MIN_SECS = 1 << 8;
    /**
     * Enable/Disable fake event insert function
     */
    public static int EVENT_CMD_CFG_FAKE_EVENT_INSERTION_ENABLE = 1 << 9;
    /**
     * Configure fake event minimal second
     */
    public static int EVENT_CMD_CFG_FAKE_EVENT_MIN_SECS = 1 << 10;
    /**
     * Enable/Disable event's time conflict.
     */
    public static int EVENT_CMD_CFG_TIME_CONFLICT_ALLOW = 1 << 11;
    /**
     * Enable/Disable event's time partial conflict.
     */
    public static int EVENT_CMD_CFG_TIME_PARTIAL_OVERLAP_ALLOW = 1 << 12;
    /**
     * Custom separator between short and extended text
     */
    public static int EVENT_CMD_CFG_EVENT_DETAIL_SEPARATOR = 1 << 13;
    /**
     * No used
     */
    public static int EVENT_CMD_CFG_DVBC_OPERATOR = 1 << 14;
    /**
     * Set current channel for monitor EIT PF used
     */
    public static int EVENT_CMD_DO_CURRENT_SERVICE = 1 << 15;
    /**
     * Restart load EIT
     */
    public static int EVENT_CMD_DO_RESTART = 1 << 16;
    /**
     * Clean event database
     */
    public static int EVENT_CMD_DO_CLEAN = 1 << 17;
    /**
     * Enabled/Disable load EIT function
     */
    public static int EVENT_CMD_DO_ENABLE = 1 << 18;
    /**
     * Not used
     */
    public static int EVENT_CMD_DO_TUNER_CHANGE = 1 << 19;

    public static String EventServiceName = "EventService";

    private static ArrayList<IEventNotify> listens = new ArrayList<IEventNotify>();

    private EventActiveWindow eventActiveWindow;

    protected EventService() {
    }

    /**
     * Add listener for eventService
     * 
     * <pre>
     * eventService.addListener(eventServiceListen);
     * </pre>
     * 
     * @param eventNotify
     */
    public void addListener(IEventNotify eventNotify) {
        if (eventNotify == null) {
            Logger.e(TAG, "Invalid eventNotify");
            return;
        }

        synchronized (EventService.this) {
            if (eventNotify != null) {
                if (!listens.contains(eventNotify)) {
                    listens.add(eventNotify);
                }
            }
        }
    }

    /**
     * Remove listener for eventService
     * 
     * <pre>
     * eventService.removeListener(eventServiceListen);
     * </pre>
     * 
     * @param eventNotify
     */
    public void removeListener(IEventNotify eventNotify) {
        synchronized (EventService.this) {
            if (listens.contains(eventNotify)) {
                listens.remove(eventNotify);
            }
        }
    }

    /**
     * Set active window for event service<BR>
     * for monitor EIT-S used<BR>
     * 
     * <pre>
     * EventActiveWindow eventActiveWindow = new EventActiveWindow();
     * eventActiveWindow.setChannels(channels);
     * eventActiveWindow.setStartTime(startTime);
     * eventActiveWindow.setDuration(endTime - startTime);
     * eventService.setAciveWindow(eventActiveWindow);
     * </pre>
     * 
     * @param eventActiveWindow
     * @return
     * @throws TVMException
     */
    public int setAciveWindow(EventActiveWindow eventActiveWindow) throws TVMException {
        if (eventActiveWindow == null) {
            Logger.e(TAG, "Invalid Command");
            return -1;
        }

        EventCommand eventCommand = new EventCommand();
        eventCommand.setActiveWindow(eventActiveWindow);

        this.eventActiveWindow = eventActiveWindow;

        int commandMask = 0;
        commandMask |= EVENT_CMD_CFG_ACTIVE_WIN;
        eventCommand.setCommandMask(commandMask);
        return eventSetCommand(eventCommand);
    }

    /**
     * Set current channel<BR>
     * tell event service which is current played channel<BR>
     * for monitor EIT-PF used
     * 
     * <pre>
     * TVManager tvManager = TVManager.getInstance(null);
     * EventService eventService = (EventService) tvManager.getService(EventService.EventServiceName);
     * eventService.setCurrentChannel(channeInfo);
     * </pre>
     * 
     * @param ChanneInfo
     * @return
     * @throws TVMException
     */
    public int setCurrentChannel(ChannelInfo ChanneInfo) throws TVMException {
        if (ChanneInfo == null) {
            Logger.e(TAG, "Invalid Command");
            return -1;
        }

        EventCommand eventCommand = new EventCommand();
        eventCommand.setCurrentChannelInfo(ChanneInfo);

        int commandMask = 0;
        commandMask |= EVENT_CMD_DO_CURRENT_SERVICE;
        eventCommand.setCommandMask(commandMask);
        return eventSetCommand(eventCommand);
    }

    /**
     * Set command to event service <BR>
     * these commands can change event service behavior
     * 
     * <pre>
     * TVManager tvManager = TVManager.getInstance(null);
     * EventService eventService = (EventService) tvManager.getService(EventService.EventServiceName);
     * int mask = 0;
     * EventCommand eventCommand = new EventCommand();
     * 
     * mask |= EventService.EVENT_CMD_CFG_ACTUAL_ONLY;
     * eventCommand.setActualOnly(false);
     * 
     * mask |= EventService.EVENT_CMD_CFG_MAX_DAYS;
     * eventCommand.setMaxDay(8);
     * 
     * mask |= EventService.EVENT_CMD_CFG_PREF_LANG;
     * eventCommand.setPrefLanuage(new String[] { &quot;CHN&quot;, &quot;CHI&quot; });
     * 
     * mask |= EventService.EVENT_CMD_CFG_ACTIVE_WIN;
     * EventActiveWindow activeWindow = new EventActiveWindow();
     * ChannelInfo[] channels = new ChannelInfo[5];
     * channels[0] = channelInfo;
     * channels[1] = channelInfo;
     * channels[2] = channelInfo;
     * channels[3] = channelInfo;
     * channels[4] = channelInfo;
     * activeWindow.setChannels(channels);
     * activeWindow.setStartTime(10000);
     * activeWindow.setDuration(1800);
     * eventCommand.setActiveWindow(activeWindow);
     * 
     * mask |= EventService.EVENT_CMD_CFG_EVENT_MIN_SECS;
     * eventCommand.setEventMinSeconds(60);
     * 
     * mask |= EventService.EVENT_CMD_CFG_FAKE_EVENT_INSERTION_ENABLE;
     * eventCommand.setFakeEventInsertionEnable(false);
     * 
     * mask |= EventService.EVENT_CMD_CFG_FAKE_EVENT_MIN_SECS;
     * eventCommand.setFakeEventMinSecond(1);
     * 
     * mask |= EventService.EVENT_CMD_CFG_TIME_CONFLICT_ALLOW;
     * eventCommand.setTimeConfictAllow(false);
     * 
     * mask |= EventService.EVENT_CMD_CFG_TIME_PARTIAL_OVERLAP_ALLOW;
     * eventCommand.setPartialOverapAllow(true);
     * 
     * mask |= EventService.EVENT_CMD_CFG_EVENT_DETAIL_SEPARATOR;
     * eventCommand.setEventDetailSeparator(&quot;-&quot;);
     * 
     * eventCommand.setCommandMask(mask);
     * eventService.eventSetCommand(eventCommand);
     * </pre>
     * 
     * @param eventCommand
     * @return
     * @throws TVMException
     */
    public int eventSetCommand(EventCommand eventCommand) throws TVMException {
        if (eventCommand == null) {
            Logger.e(TAG, "Invalid Command");
            return -1;
        }
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                synchronized (EventService.this) {
                    Logger.i(TAG, eventCommand.toString());
                    int ret = service.eventServiceSetCommand_proxy(eventCommand);
                    if (ret == 0) {
                        return 0;
                    } else {
                        throw new TVMException(ret, "eventSetCommand_proxy fail");
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get Present/Following events from event service
     * 
     * <pre>
     * TVManager tvManager = TVManager.getInstance(null);
     * EventService eventService = (EventService) tvManager.getService(EventService.EventServiceName);
     * eventService.addListener(eventServiceListen);
     * ArrayList<EventInfo> pfEventList = eventService.getPFEvents(currentChannelInfo);
     * 
     * <pre>
     * @param channelInfo
     * @return ArrayList<EventInfo> 0-present 1-following
     * @throws TVMException
     */
    public ArrayList<EventInfo> getPFEvents(ChannelInfo channelInfo) throws TVMException {
        ArrayList<EventInfo> events = new ArrayList<EventInfo>();
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                synchronized (EventService.this) {
                    if (channelInfo instanceof DvbChannelInfo) {
                        int ret = service.eventServiceGetPFEvents_proxy((DvbChannelInfo) channelInfo, events);
                        if (ret == 0) {
                            // if (TVCommon.debugMode) {
                            for (EventInfo event : events) {
                                if (event != null)
                                    Logger.i(TAG, event.toString());
                            }
                            // }
                            return events;
                        } else {
                            throw new TVMException(ret, "getPFEvents_proxy fail");
                        }
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return events;
    }

    /**
     * Get schedule events in active window
     * 
     * @return Map<ChannelInfo,ArrayList<Event>>
     * @throws TVMException
     * @see EventActiveWindow
     */
    public Map<ChannelInfo, ArrayList<EventInfo>> getScheduleEvents(ChannelInfo[] channels) throws TVMException {
        long startTime = 0;
        long endTime = 0;

        if (this.eventActiveWindow != null) {
            startTime = this.eventActiveWindow.getStartTime();
            endTime = startTime + this.eventActiveWindow.getDuration();
        }

        return getScheduleEvents(channels, startTime, endTime);
    }

    /**
     * Get schedule events by special channels
     * 
     * <pre>
     * Map&lt;ChannelInfo, ArrayList&lt;EventInfo&gt;&gt; eventListMap = eventService.getScheduleEvents(//
     *         channels, startTime, endTime);
     * 
     * for (Iterator&lt;ChannelInfo&gt; iterator = eventListMap.keySet().iterator(); iterator.hasNext();) {
     *     ChannelInfo channelInfo = (ChannelInfo) iterator.next();
     *     ArrayList&lt;EventInfo&gt; eventsList = eventListMap.get(channelInfo);
     *     Logger.i(TAG, channelInfo.toString());
     *     if (eventsList != null) {
     *         Logger.i(TAG, &quot;Schedule list event num=&quot; + eventsList.size());
     *         for (int i = 0; i &lt; eventsList.size(); i++) {
     *             Logger.i(TAG, &quot;-------------------------\\\\\\\\\\&quot;);
     *             EventInfo eventInfo = eventsList.get(i);
     *             Logger.i(TAG, &quot;&quot; + eventInfo.getEventId());
     *             Logger.i(TAG, &quot;&quot; + eventInfo.getEventTitle());
     *             Logger.i(TAG, &quot;&quot; + eventInfo.getEventDetail());
     *             Logger.i(TAG, &quot;&quot; + eventInfo.getStartTime());
     *             Logger.i(TAG, &quot;&quot; + eventInfo.getDuration());
     *             Logger.i(TAG, &quot;&quot; + eventInfo.toString());
     *             Logger.i(TAG, &quot;-------------------------//////////&quot;);
     *         }
     *     }
     * }
     * </pre>
     * 
     * @param channels
     * @param startTime
     * @param endTime
     * @return
     * @throws TVMException
     */
    public Map<ChannelInfo, ArrayList<EventInfo>> getScheduleEvents(ChannelInfo[] channels, long startTime, long endTime) throws TVMException {

        if (channels == null || channels.length == 0) {
            return null;
        }

        Map<ChannelInfo, ArrayList<EventInfo>> eventMap = new HashMap<ChannelInfo, ArrayList<EventInfo>>();
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                synchronized (EventService.this) {
                    for (ChannelInfo channel : channels)
                        if (channel != null && channel instanceof DvbChannelInfo) {
                            ArrayList<EventInfo> eventsList = new ArrayList<EventInfo>();
                            Logger.i(TAG, "Get schedule events by channel:" + channel.getServiceName() + //
                                    " [starttime=" + startTime + " endTime=" + endTime + "]");
                            int ret = service.eventServiceGetScheduleEvents_proxy((DvbChannelInfo) channel, startTime, endTime, eventsList);
                            eventMap.put(channel, eventsList);
                            // if (TVCommon.debugMode) {
                            Logger.i(TAG, channel.toString());
                            for (EventInfo event : eventsList) {
                                if (event != null)
                                    Logger.i(TAG, event.toString());
                            }
                            // }
                            if (ret != 0) {
                                Logger.e(TAG, "Get schedule events by channel fail:" + channel.getServiceName());
                            }
                        }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return eventMap;
    }

    /**
     * notify caller events updated<BR>
     * User should never call this function
     * 
     * @param reason
     * @param data1
     *            svlid
     * @param data2
     *            channel_id
     */
    public static void notifyUpdate(EventUpdateReason reason, int svlId, int channelId) {
        Logger.i(TAG, "notifyUpdate reason=" + reason + " svlId=" + svlId + " channelId=" + channelId);
        for (int i = 0; i < listens.size(); i++) {
            IEventNotify eventNotify = listens.get(i);
            eventNotify.notifyUpdate(reason, svlId, channelId);
        }
    }
}
