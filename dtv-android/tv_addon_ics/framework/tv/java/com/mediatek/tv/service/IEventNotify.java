package com.mediatek.tv.service;

import com.mediatek.tv.model.EventUpdateReason;

/**
 * Interface of Event notify
 *
 */
public interface IEventNotify {
    void notifyUpdate(EventUpdateReason reason,int svlid,int channelId);
}
