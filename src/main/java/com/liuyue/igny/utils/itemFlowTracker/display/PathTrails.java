package com.liuyue.igny.utils.itemFlowTracker.display;

import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class PathTrails {
    //#if MC >= 26.2
    //$$ private static final Block MARKER_BLOCK = Blocks.STAINED_GLASS.black();
    //#else
    private static final Block MARKER_BLOCK = Blocks.BLACK_STAINED_GLASS;
    //#endif
    private static final double MIN_SEGMENT = 0.05D;
    private static final double MAX_SEGMENT = 8.0D;
    private static final int LIFETIME_TICKS = 200;
    private static final int MAX_MARKERS = 512;

    private static final Map<TrackMark, List<Marker>> TRAILS = new HashMap<>();

    private static final class Marker {
        private final VirtualDisplay display;
        private int life = LIFETIME_TICKS;

        private Marker(VirtualDisplay display) {
            this.display = display;
        }
    }

    public static void tick(ServerLevel level) {
        long time = level.getGameTime();

        for (Map.Entry<Integer, TrackingWatch.Watch> entry : TrackingWatch.entities(level.dimension()).entrySet()) {
            TrackingWatch.Watch watch = entry.getValue();

            if (!wantsStamp(watch.mark, time)) {
                continue;
            }

            Entity entity = level.getEntity(entry.getKey());

            if (entity != null && !entity.isRemoved()) {
                stamp(level, watch, entity.position());
            }
        }

        for (Map.Entry<BlockPos, TrackingWatch.Watch> entry : TrackingWatch.blocks(level.dimension()).entrySet()) {
            TrackingWatch.Watch watch = entry.getValue();

            if (!wantsStamp(watch.mark, time) || !level.isLoaded(entry.getKey())) {
                continue;
            }

            stamp(level, watch, Vec3.atCenterOf(entry.getKey()));
        }
    }

    public static void expire() {
        Iterator<Map.Entry<TrackMark, List<Marker>>> it = TRAILS.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<TrackMark, List<Marker>> entry = it.next();
            List<Marker> markers = entry.getValue();
            boolean alive = Tracking.isLive(entry.getKey());

            markers.removeIf(marker -> {
                if (!alive || --marker.life <= 0) {
                    marker.display.remove();
                    return true;
                }

                marker.display.sync();
                return false;
            });

            if (markers.isEmpty()) {
                it.remove();
            }
        }
    }

    public static boolean active() {
        return !TRAILS.isEmpty();
    }

    public static void clear() {
        for (List<Marker> markers : TRAILS.values()) {
            for (Marker marker : markers) {
                marker.display.remove();
            }
        }

        TRAILS.clear();
    }

    private static boolean wantsStamp(TrackMark mark, long time) {
        return mark != null
                && mark.pathInterval() > 0
                && Tracking.isLive(mark)
                && time % mark.pathInterval() == 0;
    }

    private static void stamp(ServerLevel level, TrackingWatch.Watch watch, Vec3 at) {
        TrackMark mark = watch.mark;

        if (mark == null) {
            return;
        }

        Vec3 previous = watch.trailLast;

        if (previous != null) {
            double distance = previous.distanceTo(at);

            if (distance < MIN_SEGMENT) {
                return;
            }

            if (distance <= MAX_SEGMENT) {
                spawn(level, mark, previous, at);
            }
        }

        watch.trailLast = at;
    }

    private static void spawn(ServerLevel level, TrackMark mark, Vec3 start, Vec3 end) {
        List<Marker> markers = TRAILS.computeIfAbsent(mark, ignored -> new ArrayList<>());

        if (markers.size() >= MAX_MARKERS) {
            markers.remove(0).display.remove();
        }

        VirtualDisplay display = VirtualDisplay
                .block(level, start.x, start.y, start.z, MARKER_BLOCK.defaultBlockState())
                .glow(mark)
                .bright()
                .transform(Shapes.segment(start, end));

        display.sync();
        markers.add(new Marker(display));
    }
}
