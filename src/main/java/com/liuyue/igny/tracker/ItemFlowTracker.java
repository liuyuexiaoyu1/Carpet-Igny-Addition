package com.liuyue.igny.tracker;

import com.liuyue.igny.utils.itemFlowTracker.ItemFlowTrackerSettings;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import com.liuyue.igny.utils.itemFlowTracker.display.BlockHighlights;
import com.liuyue.igny.utils.itemFlowTracker.display.EntityHighlights;
import com.liuyue.igny.utils.itemFlowTracker.display.PathTrails;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public final class ItemFlowTracker {

    public static void tick(MinecraftServer server) {
        Tracking.sweepExhaustedSessions();

        if (!ItemFlowTrackerSettings.enabled()) {
            if (BlockHighlights.active() || EntityHighlights.active() || PathTrails.active() || TrackingWatch.pending()) {
                clearAll();
            }
            return;
        }

        for (ServerLevel level : server.getAllLevels()) {
            BlockHighlights.tick(level);
            EntityHighlights.tick(level);
            PathTrails.tick(level);
        }

        PathTrails.expire();
    }

    public static void clearAll() {
        BlockHighlights.clear();
        EntityHighlights.clear();
        PathTrails.clear();
        TrackingWatch.clearAll();
        Tracking.clearAll();
    }
}
