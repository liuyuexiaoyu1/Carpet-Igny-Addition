package com.liuyue.igny.utils.itemFlowTracker.display;

import com.liuyue.igny.utils.display.Shapes;
import com.liuyue.igny.utils.display.VirtualDisplay;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackedEntity;
import com.liuyue.igny.utils.itemFlowTracker.core.Tracking;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
        tickHops(level);
        tickMovingEntities(level);
    }

    private static final class Leg {
        @Nullable
        private Vec3 previous;

        @Nullable
        private Vec3 from;

        private Vec3 to;
    }

    private static void tickHops(ServerLevel level) {
        Map<TrackMark, List<Leg>> legs = new LinkedHashMap<>();

        for (TrackingWatch.TrailHop hop : TrackingWatch.drainTrailHops(level)) {
            List<Leg> markLegs = legs.computeIfAbsent(hop.mark(), ignored -> new ArrayList<>());
            Leg previous = markLegs.isEmpty() ? null : markLegs.get(markLegs.size() - 1);

            if (previous != null && previous.to.equals(hop.to())) {
                if (previous.from == null || previous.from.equals(previous.to)) {
                    previous.previous = hop.previous();
                    previous.from = hop.from();
                }

                continue;
            }

            Leg leg = new Leg();
            leg.previous = hop.previous();
            leg.from = hop.from();
            leg.to = hop.to();
            markLegs.add(leg);
        }

        for (Map.Entry<TrackMark, List<Leg>> entry : legs.entrySet()) {
            for (Leg leg : entry.getValue()) {
                extend(level, entry.getKey(), leg);
            }
        }
    }

    private static void extend(ServerLevel level, TrackMark mark, Leg leg) {
        if (mark.pathInterval() <= 0 || !Tracking.isLive(mark)) {
            return;
        }

        Vec3 from = leg.from != null ? leg.from : leg.previous;

        if (leg.previous != null && from != null && !leg.previous.equals(from)) {
            append(level, mark, leg.previous, from);
        }

        append(level, mark, from, leg.to);
    }

    private static void tickMovingEntities(ServerLevel level) {
        long time = level.getGameTime();

        for (Map.Entry<Integer, TrackingWatch.Watch> entry : TrackingWatch.entities(level.dimension()).entrySet()) {
            TrackingWatch.Watch watch = entry.getValue();
            TrackMark mark = watch.mark;

            if (mark == null || mark.pathInterval() <= 0 || !Tracking.isLive(mark)) {
                continue;
            }

            if (watch.trailLast != null && time % mark.pathInterval() != 0) {
                continue;
            }

            Entity entity = level.getEntity(entry.getKey());

            if (entity != null && !entity.isRemoved()) {
                stamp(level, watch, entity, entity.position());
            }
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

    private static void stamp(ServerLevel level, TrackingWatch.Watch watch, Entity entity, Vec3 at) {
        TrackMark mark = watch.mark;

        if (mark == null) {
            return;
        }

        Vec3 previous = watch.trailLast;

        if (previous == null) {
            previous = ejectionAnchor(entity);

            if (previous == null && !handDropped(entity)) {
                previous = TrackingWatch.lostAnchor(level, mark, at);
            }
        }

        if (previous != null && previous.distanceTo(at) < MIN_SEGMENT) {
            return;
        }

        append(level, mark, previous, at);
        watch.trailLast = at;
    }

    @Nullable
    private static Vec3 ejectionAnchor(Entity entity) {
        return entity instanceof TrackedEntity tracked ? tracked.igny$ejectionAnchor() : null;
    }

    private static boolean handDropped(Entity entity) {
        return entity instanceof TrackedEntity tracked && tracked.igny$handDropped();
    }

    private static void append(ServerLevel level, TrackMark mark, @Nullable Vec3 previous, Vec3 point) {
        if (previous == null) {
            return;
        }

        double distance = previous.distanceTo(point);

        if (distance < MIN_SEGMENT || distance > MAX_SEGMENT) {
            return;
        }

        spawn(level, mark, previous, point);
    }

    private static void spawn(ServerLevel level, TrackMark mark, Vec3 start, Vec3 end) {
        List<Marker> markers = TRAILS.computeIfAbsent(mark, ignored -> new ArrayList<>());

        VirtualDisplay display = VirtualDisplay
                .block(level, start.x, start.y, start.z, MARKER_BLOCK.defaultBlockState())
                .glow(mark.rgb())
                .bright()
                .transform(Shapes.segment(start, end));

        display.sync();

        while (markers.size() >= MAX_MARKERS) {
            markers.remove(0).display.remove();
        }

        markers.add(new Marker(display));
    }
}
