package com.liuyue.igny.utils.itemFlowTracker.core;

import com.liuyue.igny.utils.itemFlowTracker.ItemFlowTrackerSettings;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
//#if MC >= 26.1
//$$ import net.minecraft.core.component.DataComponents;
//#else
import net.minecraft.world.item.DyeItem;
//#endif
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Tracking {
    private static final List<TrackMark> ACTIVE = new ArrayList<>();
    private static final int EXHAUSTED_GRACE_TICKS = 40;
    private static final int DEFAULT_PATH_INTERVAL = 5;

    private static int generation;

    public static void clearAll() {
        generation++;
        ACTIVE.clear();
    }

    public static void sweepExhaustedSessions() {
        Iterator<TrackMark> it = ACTIVE.iterator();

        while (it.hasNext()) {
            TrackMark mark = it.next();

            if (mark.generation() != generation || mark.tickExhausted() > EXHAUSTED_GRACE_TICKS) {
                mark.retire();
                it.remove();
            }
        }
    }

    public static TrackMark newMark(DyeColor color, int capacity) {
        return newMark(color, capacity, -1);
    }

    public static TrackMark newMark(DyeColor color, int capacity, int pathInterval) {
        //#if MC >= 12000
        int colorInt = color.getTextureDiffuseColor();
        //#else
        //$$ float[] rgb = color.getTextureDiffuseColors();
        //$$ int colorInt = 0xFF000000
        //$$         | ((int)(rgb[0] * 255.0F) << 16)
        //$$         | ((int)(rgb[1] * 255.0F) << 8)
        //$$         |  (int)(rgb[2] * 255.0F);
        //#endif
        return register(new TrackMark(
                colorInt,
                color.getName(),
                generation,
                capacity,
                resolvePathInterval(pathInterval)));
    }

    public static TrackMark newMark(int rgb, int capacity, int pathInterval) {
        return register(new TrackMark(
                rgb,
                String.format("#%06X", rgb & 0xFFFFFF),
                generation,
                capacity,
                resolvePathInterval(pathInterval)));
    }

    public static TrackMark handOver(TrackMark parent, int capacity) {
        parent.spend(parent.capacity());
        return derive(parent, capacity);
    }

    public static TrackMark derive(TrackMark parent, int capacity) {
        return register(new TrackMark(parent.rgb(), parent.label(), generation, capacity, parent.pathInterval()));
    }

    private static int resolvePathInterval(int pathInterval) {
        return pathInterval < 0 ? DEFAULT_PATH_INTERVAL : pathInterval;
    }

    private static TrackMark register(TrackMark mark) {
        if (!ItemFlowTrackerSettings.enabled()) {
            mark.retire();
            return mark;
        }

        int max = ItemFlowTrackerSettings.maxSessions();

        if (max > 0 && ACTIVE.size() >= max) {
            ACTIVE.remove(0).retire();
        }

        ACTIVE.add(mark);
        return mark;
    }

    @Nullable
    public static TrackMark get(@Nullable ItemStack stack) {
        return stack == null || stack.isEmpty() ? null : getRaw(stack);
    }

    @Nullable
    public static TrackMark getRaw(@Nullable ItemStack stack) {
        return stack == null || stack == ItemStack.EMPTY ? null : markOn((TrackedStack) (Object) stack);
    }

    public static boolean isLive(@Nullable TrackMark mark) {
        return mark != null && mark.generation() == generation && !mark.isRetired();
    }

    @Nullable
    public static TrackMark markOn(@Nullable TrackedStack holder) {
        if (holder == null) {
            return null;
        }

        TrackMark mark = holder.igny$getMark();
        return isLive(mark) ? mark : null;
    }

    public static void transfer(@Nullable TrackedStack from, @Nullable TrackedStack to) {
        TrackMark mark = markOn(from);

        if (mark != null && to != null && markOn(to) == null) {
            to.igny$setMark(mark);
        }
    }

    public static boolean isMarked(@Nullable ItemStack stack) {
        return get(stack) != null;
    }

    public static void set(ItemStack stack, @Nullable TrackMark mark) {
        if (stack != ItemStack.EMPTY) {
            ((TrackedStack) (Object) stack).igny$setMark(mark);
        }
    }

    public static void setIfAbsent(ItemStack stack, TrackMark mark) {
        if (getRaw(stack) == null) {
            set(stack, mark);
        }
    }

    public static void spread(@Nullable ItemStack from, @Nullable ItemStack to) {
        if (!ItemFlowTrackerSettings.enabled()) {
            return;
        }

        TrackMark mark = getRaw(from);

        if (mark != null && to != null) {
            setIfAbsent(to, mark);
        }
    }

    public static void onSplit(ItemStack source, ItemStack taken) {
        TrackMark mark = getRaw(source);

        if (mark == null) {
            return;
        }

        setIfAbsent(taken, mark);
        mark.spend(taken.getCount());
    }

    public static void arrive(ItemStack destination, TrackMark incoming, int amount) {
        TrackMark current = getRaw(destination);

        if (current == incoming) {
            incoming.refund(amount);
        } else if (current == null) {
            set(destination, incoming);
        }
    }

    public static void returned(ItemStack stack) {
        moved(stack);
    }

    public static void moved(ItemStack stack) {
        TrackMark mark = getRaw(stack);

        if (mark != null) {
            mark.refund(stack.getCount());
        }
    }

    public static List<TrackMark> activeSessions() {
        return Collections.unmodifiableList(ACTIVE);
    }

    @Nullable
    public static DyeColor dyeOf(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        //#if MC >= 26.1
        //$$ return stack.get(DataComponents.DYE);
        //#else
        return stack.getItem() instanceof DyeItem dyeItem ? dyeItem.getDyeColor() : null;
        //#endif
    }
}
