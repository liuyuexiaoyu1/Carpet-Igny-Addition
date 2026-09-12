package com.liuyue.igny.utils.itemFlowTracker.display;

import com.liuyue.igny.utils.itemFlowTracker.core.Nesting;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackMark;
import com.liuyue.igny.utils.itemFlowTracker.core.TrackingWatch;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.Iterator;
import java.util.Map;

public final class EntityHighlights {
    //#if MC >= 26.2
    //$$ private static final Block OUTLINE_BLOCK = Blocks.STAINED_GLASS.white();
    //#else
    private static final Block OUTLINE_BLOCK = Blocks.WHITE_STAINED_GLASS;
    //#endif
    private static final DisplayIndex<Integer, EntityEntry> DISPLAYS = new DisplayIndex<>();

    public static void tick(ServerLevel level) {
        ResourceKey<Level> dimension = level.dimension();
        Map<Integer, TrackingWatch.Watch> watches = TrackingWatch.entities(dimension);
        Iterator<Map.Entry<Integer, TrackingWatch.Watch>> it = watches.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Integer, TrackingWatch.Watch> watchEntry = it.next();
            int id = watchEntry.getKey();
            TrackingWatch.Watch watch = watchEntry.getValue();
            Entity entity = level.getEntity(id);
            EntityEntry tracked = DISPLAYS.get(dimension, id);

            if (entity == null || entity.isRemoved()) {
                watch.mark = null;
                dispose(dimension, id, tracked);
                it.remove();
                continue;
            }

            TrackMark mark = Nesting.inEntity(entity);
            watch.mark = mark;

            if (mark == null) {
                dispose(dimension, id, tracked);
                it.remove();
                continue;
            }

            if (tracked == null || !mark.equals(tracked.mark)) {
                if (tracked != null) {
                    tracked.dispose();
                }

                tracked = new EntityEntry();
                tracked.mark = mark;
                tracked.display = VirtualDisplay
                        .block(level, entity.getX(), entity.getY(), entity.getZ(), OUTLINE_BLOCK.defaultBlockState())
                        .glow(mark)
                        .bright()
                        .ride(entity);
                DISPLAYS.put(dimension, id, tracked);
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

    private static void dispose(ResourceKey<Level> dimension, int id, EntityEntry tracked) {
        if (tracked != null) {
            tracked.dispose();
            DISPLAYS.remove(dimension, id);
        }
    }

    private static void follow(EntityEntry entry, Entity entity) {
        VirtualDisplay display = entry.display;

        if (display == null) {
            return;
        }

        AABB box = entity.getBoundingBox();

        if (entry.lastWidth != box.getXsize() || entry.lastHeight != box.getYsize()) {
            entry.lastWidth = box.getXsize();
            entry.lastHeight = box.getYsize();
            display.transform(Shapes.entity(entity, box));
        }

        display.sync();
    }
}
