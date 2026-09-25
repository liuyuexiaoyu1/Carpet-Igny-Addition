package com.liuyue.igny.utils.itemFlowTracker.display;

import com.liuyue.igny.utils.display.Shapes;
import com.liuyue.igny.utils.display.VirtualDisplay;
import com.liuyue.igny.utils.itemFlowTracker.ItemFlowTrackerSettings;
import com.liuyue.igny.utils.itemFlowTracker.core.*;
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
    private static final Block MARKER_BLOCK = Blocks.BLACK_CONCRETE; //#replace >= 26.2 ? private static final Block MARKER_BLOCK = Blocks.CONCRETE.black();
    private static final double MIN_SEGMENT = 0.05D;
    private static final double GUESS_MIN_SEGMENT = 0.75D;
    private static final double MAX_SEGMENT = 8.0D;
    private static final double MAX_MOTION_SEGMENT = 64.0D;
    private static final double MERGE_DEVIATION_SQR = 0.001D;
    private static final double MERGE_MAX_LENGTH = 10.0D;
    private static final double DUPLICATE_SQR = 0.0001D;
    private static final double DEFAULT_SPEED = 0.25D;
    private static final int LIFETIME_TICKS = 200;
    private static final int MAX_MARKERS = 4096;

    private static final Map<TrackMark, List<Marker>> TRAILS = new HashMap<>();

    private static final class Marker {
        private final VirtualDisplay display;
        private final Vec3 start;
        private Vec3 end;
        private double trimmed;

        private double speed;
        private int life = LIFETIME_TICKS;

        private Marker(VirtualDisplay display, Vec3 start, Vec3 end) {
            this.display = display;
            this.start = start;
            this.end = end;
            this.speed = Math.max(DEFAULT_SPEED, Math.min(1.0D, start.distanceTo(end)));
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
            Leg previous = markLegs.isEmpty() ? null : markLegs.getLast();

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

        if (leg.previous != null && !leg.previous.equals(from)) {
            append(level, mark, leg.previous, from);
        }

        append(level, mark, from, leg.to);
    }

    private static void tickMovingEntities(ServerLevel level) {
        long time = level.getGameTime();

        for (Map.Entry<Integer, TrackingWatch.Watch> entry : TrackingWatch.entities(level.dimension()).entrySet()) {
            TrackingWatch.Watch watch = entry.getValue();
            Entity entity = level.getEntity(entry.getKey());

            if (entity == null || entity.isRemoved()) {
                continue;
            }

            TrackMark mark = Nesting.inEntity(entity);

            if (mark == null || mark.pathInterval() <= 0 || !Tracking.isLive(mark)) {
                continue;
            }

            if (watch.trailLast != null && time % mark.pathInterval() != 0) {
                continue;
            }

            stamp(level, watch, entity, entity.position(), mark);
        }
    }

    public static void expire() {
        if (ItemFlowTrackerSettings.persistentTrail()) {
            for (List<Marker> markers : TRAILS.values()) {
                for (Marker marker : markers) {
                    marker.display.sync();
                }
            }

            return;
        }

        Iterator<Map.Entry<TrackMark, List<Marker>>> it = TRAILS.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<TrackMark, List<Marker>> entry = it.next();
            TrackMark mark = entry.getKey();
            List<Marker> markers = entry.getValue();

            if (!Tracking.isLive(mark)) {
                for (Marker marker : markers) {
                    marker.display.remove();
                }

                markers.clear();
                it.remove();
                continue;
            }

            List<Marker> kept = new ArrayList<>(markers.size());
            boolean blocked = false;

            for (Marker marker : markers) {
                --marker.life;

                if (blocked || marker.life > 0) {
                    blocked = true;
                    marker.display.sync();
                    kept.add(marker);
                    continue;
                }

                marker.trimmed += marker.speed;
                double span = marker.end.distanceTo(marker.start);

                if (marker.trimmed >= span - MIN_SEGMENT) {
                    marker.display.remove();
                    continue;
                }

                marker.display.transform(Shapes.segment(marker.start, marker.end, marker.trimmed));
                marker.display.sync();
                kept.add(marker);
                blocked = true;
            }

            markers.clear();
            markers.addAll(kept);

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

    private static void stamp(ServerLevel level, TrackingWatch.Watch watch, Entity entity, Vec3 at, TrackMark mark) {
        Vec3 previous = watch.trailLast;
        boolean guessed = false;

        if (previous == null) {
            previous = ejectionAnchor(entity);

            if (previous == null && !handDropped(entity)) {
                previous = TrackingWatch.lostAnchor(level, mark, at);
                guessed = previous != null;
            }
        }

        if (previous != null) {
            double distance = previous.distanceTo(at);

            if (distance < MIN_SEGMENT || (guessed && distance < GUESS_MIN_SEGMENT)) {
                watch.trailLast = at;
                return;
            }
        }
        append(level, mark, previous, at, MAX_MOTION_SEGMENT);
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
        append(level, mark, previous, point, MAX_SEGMENT);
    }

    private static void append(ServerLevel level, TrackMark mark, @Nullable Vec3 previous, Vec3 point, double limit) {
        if (previous == null) {
            return;
        }

        double distance = previous.distanceTo(point);

        if (distance < MIN_SEGMENT || distance > limit) {
            return;
        }

        spawn(level, mark, previous, point);
    }

    private static void spawn(ServerLevel level, TrackMark mark, Vec3 start, Vec3 end) {
        List<Marker> markers = TRAILS.computeIfAbsent(mark, ignored -> new ArrayList<>());

        if (!markers.isEmpty()) {
            Marker last = markers.getLast();

            if (last.start.distanceToSqr(start) < DUPLICATE_SQR && last.end.distanceToSqr(end) < DUPLICATE_SQR) {
                return;
            }

            if (canExtend(last, end)) {
                last.speed = Math.max(MIN_SEGMENT, last.end.distanceTo(end));
                last.display.transform(Shapes.segment(last.start, end, last.trimmed));
                last.display.sync();
                last.end = end;
                last.life = LIFETIME_TICKS;
                return;
            }
        }

        VirtualDisplay display = VirtualDisplay
                .block(level, start.x, start.y, start.z, MARKER_BLOCK.defaultBlockState())
                .glow(mark.rgb())
                .bright()
                .transform(Shapes.segment(start, end));

        display.sync();

        while (markers.size() >= MAX_MARKERS) {
            markers.removeFirst().display.remove();
        }

        markers.add(new Marker(display, start, end));
    }

    private static boolean canExtend(Marker last, Vec3 end) {
        Vec3 direction = last.end.subtract(last.start);
        double length = direction.length();
        Vec3 toEnd = end.subtract(last.start);
        double total = toEnd.length();

        if (length <= 0.0D || total <= length || total > MERGE_MAX_LENGTH) {
            return false;
        }

        Vec3 deviation = toEnd.subtract(direction.scale(total / length));
        return deviation.lengthSqr() <= MERGE_DEVIATION_SQR;
    }
}
