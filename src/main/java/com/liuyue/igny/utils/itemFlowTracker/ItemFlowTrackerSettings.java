package com.liuyue.igny.utils.itemFlowTracker;

import com.liuyue.igny.IGNYSettings;

public final class ItemFlowTrackerSettings {

    public static boolean enabled() {
        return IGNYSettings.ITEM_FLOW_TRACKER.value();
    }

    public static int maxSessions() {
        return IGNYSettings.ITEM_FLOW_TRACKER_MAX_SESSIONS.value();
    }

    public static boolean fastMark() {
        return IGNYSettings.ITEM_FLOW_TRACKER_FAST_MARK.value();
    }
}