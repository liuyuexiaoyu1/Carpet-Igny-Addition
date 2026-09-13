package com.liuyue.igny.utils.itemFlowTracker.display;

import com.liuyue.igny.utils.display.DisplayIndex;
import com.liuyue.igny.utils.display.VirtualDisplay;
import com.liuyue.igny.utils.itemFlowTracker.core.Nesting;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Iterator;
import java.util.Map;

public final class EntityHighlights {
    private static final long PRUNE_TICKS = 200L;
    //#if MC >= 26.2
    //$$ private static final Block OUTLINE_BLOCK = Blocks.STAINED_GLASS.white();
    //#else
    private static final Block OUTLINE_BLOCK = Blocks.WHITE_STAINED_GLASS;
    //#endif
    private static final DisplayIndex<Integer, HighlightEntry> DISPLAYS = new DisplayIndex<>();

    public static void tick(ServerLevel level) {
        ResourceKey<Level> dimension = level.dimension();
        Map<Integer, TrackingWatch.Watch> watches = TrackingWatch.entities(dimension);
        Iterator<Map.Entry<Integer, TrackingWatch.Watch>> it = watches.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Integer, TrackingWatch.Watch> watchEntry = it.next();
            int id = watchEntry.getKey();
            TrackingWatch.Watch watch = watchEntry.getValue();
            Entity entity = level.getEntity(id);
            HighlightEntry tracked = DISPLAYS.get(dimension, id);

            if (entity == null) {
                if (watch.dormantSince == 0L) {
                    watch.dormantSince = level.getGameTime();
                }

                dispose(dimension, id, tracked);

                if (level.getGameTime() - watch.dormantSince > TrackingWatch.DORMANT_TICKS) {
                    it.remove();
                }

                continue;
            }

            watch.dormantSince = 0L;

            if (entity.isRemoved()) {
                TrackMark lost = watch.mark;

                if (lost != null) {
                    TrackingWatch.rememberLoss(level, BlockPos.containing(entity.position()), lost);
                }

                watch.setMark(null);
                dispose(dimension, id, tracked);
                it.remove();
                continue;
            }

            TrackMark mark = Nesting.inEntity(entity);
            watch.setMark(mark);

            if (mark == null) {
                dispose(dimension, id, tracked);

                if (level.getGameTime() - watch.lastTouched > PRUNE_TICKS) {
                    it.remove();
                }

                continue;
            }

            watch.lastTouched = level.getGameTime();

            if (tracked == null || !mark.equals(tracked.mark)) {
                HighlightEntry fresh = new HighlightEntry();
                fresh.mark = mark;
                fresh.displays.add(VirtualDisplay
                        .block(level, entity.getX(), entity.getY(), entity.getZ(), OUTLINE_BLOCK.defaultBlockState())
                        .glow(mark.rgb())
                        .bright()
                        .ride(entity));
                DISPLAYS.put(dimension, id, fresh);

                if (tracked != null) {
                    tracked.dispose();
                }

                tracked = fresh;
            }

            follow(tracked, entity);
        }
    }

    public static boolean active() {
        return DISPLAYS.active();
    }

    public static void clear() {
        DISPLAYS.forEach(HighlightEntry::dispose);
        DISPLAYS.clear();
    }

    private static void dispose(ResourceKey<Level> dimension, int id, HighlightEntry tracked) {
        if (tracked != null) {
            tracked.dispose();
            DISPLAYS.remove(dimension, id);
        }
    }

    private static void follow(HighlightEntry entry, Entity entity) {
        for (VirtualDisplay display : entry.displays) {
            display.follow(entity);
        }
    }
}
