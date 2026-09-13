package com.liuyue.igny.utils.itemFlowTracker.core;

import com.liuyue.igny.utils.ColorUtil;
import com.liuyue.igny.utils.itemFlowTracker.ItemFlowTrackerSettings;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Tracking {
    private static final List<TrackMark> ACTIVE = new ArrayList<>();
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

            if (mark.generation() != generation) {
                mark.retire();
                it.remove();
            }
        }
    }

    public static TrackMark newMark(DyeColor color, int capacity) {
        return newMark(color, capacity, -1);
    }

    public static TrackMark newMark(DyeColor color, int capacity, int pathInterval) {
        return register(new TrackMark(
                ColorUtil.of(color),
                color.getName(),
                generation,
                capacity,
                resolvePathInterval(pathInterval)));
    }

    public static TrackMark newMark(int rgb, int capacity, int pathInterval) {
        return register(new TrackMark(
                rgb,
                ColorUtil.labelOf(rgb),
                generation,
                capacity,
                resolvePathInterval(pathInterval)));
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
        return markOn(holderOf(stack));
    }

    @Nullable
    public static TrackMark getRaw(@Nullable ItemStack stack) {
        List<TrackMark> marks = storedMarks(stack);
        return marks == null || marks.isEmpty() ? null : marks.get(0);
    }

    public static boolean has(@Nullable ItemStack stack, @Nullable TrackMark mark) {
        List<TrackMark> marks = storedMarks(stack);
        return mark != null && marks != null && marks.contains(mark);
    }

    public static List<TrackMark> marksOf(@Nullable ItemStack stack) {
        List<TrackMark> marks = marksOn(holderOf(stack));
        return marks == null ? Collections.emptyList() : marks;
    }

    public static boolean isLive(@Nullable TrackMark mark) {
        return mark != null && mark.generation() == generation && !mark.isRetired();
    }

    @Nullable
    public static TrackMark markOn(@Nullable TrackedStack holder) {
        List<TrackMark> marks = marksOn(holder);
        return marks == null || marks.isEmpty() ? null : marks.get(0);
    }

    @Nullable
    public static List<TrackMark> marksOn(@Nullable TrackedStack holder) {
        if (holder == null) {
            return null;
        }

        List<TrackMark> stored = holder.igny$getMarks();

        if (stored == null || stored.isEmpty()) {
            return stored;
        }

        List<TrackMark> live = null;

        for (int slot = 0; slot < stored.size(); slot++) {
            TrackMark mark = stored.get(slot);

            if (isLive(mark)) {
                if (live != null) {
                    live.add(mark);
                }

                continue;
            }

            if (live == null) {
                live = new ArrayList<>(stored.subList(0, slot));
            }
        }

        if (live == null) {
            return stored;
        }

        holder.igny$setMarks(live.isEmpty() ? null : live);
        return live;
    }

    public static boolean isMarked(@Nullable ItemStack stack) {
        return get(stack) != null;
    }

    public static void set(ItemStack stack, @Nullable TrackMark mark) {
        TrackedStack holder = holderOf(stack);

        if (holder == null) {
            return;
        }

        if (mark == null) {
            holder.igny$setMarks(null);
            return;
        }

        List<TrackMark> fresh = new ArrayList<>(1);
        fresh.add(mark);
        holder.igny$setMarks(fresh);
    }

    public static void setIfAbsent(ItemStack stack, TrackMark mark) {
        add(stack, mark);
    }

    public static void add(@Nullable ItemStack stack, @Nullable TrackMark mark) {
        if (mark == null || !isLive(mark) || stack == null || stack == ItemStack.EMPTY) {
            return;
        }

        attach(stack, mark);
    }

    public static void spread(@Nullable ItemStack from, @Nullable ItemStack to) {
        if (!ItemFlowTrackerSettings.enabled() || to == null || to == ItemStack.EMPTY) {
            return;
        }

        for (TrackMark mark : new ArrayList<>(marksOf(from))) {
            if (isLive(mark)) {
                attach(to, mark);
            }
        }
    }

    private static void attach(ItemStack stack, TrackMark mark) {
        TrackedStack holder = (TrackedStack) (Object) stack;
        List<TrackMark> marks = marksOn(holder);

        if (marks == null || marks.isEmpty()) {
            List<TrackMark> fresh = new ArrayList<>(1);
            fresh.add(mark);
            holder.igny$setMarks(fresh);
            return;
        }

        if (!marks.contains(mark)) {
            marks.add(mark);
        }
    }

    public static void onSplit(ItemStack source, ItemStack taken) {
        List<TrackMark> marks = marksOf(source);

        if (marks.isEmpty() || taken == null || taken == ItemStack.EMPTY) {
            return;
        }

        for (TrackMark mark : new ArrayList<>(marks)) {
            add(taken, mark);
        }
    }

    public static void withdrawn(ItemStack source, int amount) {
        List<TrackMark> marks = marksOf(source);

        if (marks.isEmpty() || amount <= 0) {
            return;
        }

        for (TrackMark mark : new ArrayList<>(marks)) {
            remove(source, mark);
        }
    }

    public static void remove(ItemStack stack, TrackMark mark) {
        TrackedStack holder = holderOf(stack);

        if (holder == null) {
            return;
        }

        List<TrackMark> marks = holder.igny$getMarks();

        if (marks == null || !marks.remove(mark)) {
            return;
        }

        holder.igny$setMarks(marks.isEmpty() ? null : marks);
    }

    public static void arrive(ItemStack destination, TrackMark incoming, int amount) {
        if (has(destination, incoming)) {
            incoming.refund(amount);
            return;
        }

        add(destination, incoming);
    }

    public static void arrive(ItemStack destination, @Nullable ItemStack source, int amount) {
        if (source == null) {
            return;
        }

        for (TrackMark mark : new ArrayList<>(marksOf(source))) {
            arrive(destination, mark, amount);
        }
    }

    public static void returned(ItemStack stack) {
        moved(stack);
    }

    public static void refunded(ItemStack stack, int amount) {
        refund(marksOf(stack), amount);
    }

    public static void moved(ItemStack stack) {
        refund(marksOf(stack), stack.getCount());
    }

    public static List<TrackMark> activeSessions() {
        return Collections.unmodifiableList(ACTIVE);
    }

    private static void refund(List<TrackMark> marks, int amount) {
        int left = amount;

        for (TrackMark mark : marks) {
            if (left <= 0) {
                return;
            }

            int room = Math.max(mark.capacity() - mark.budget(), 0);
            int added = Math.min(left, room);
            mark.refund(added);
            left -= added;
        }
    }

    @Nullable
    private static TrackedStack holderOf(@Nullable ItemStack stack) {
        return stack == null || stack == ItemStack.EMPTY ? null : (TrackedStack) (Object) stack;
    }

    @Nullable
    private static List<TrackMark> storedMarks(@Nullable ItemStack stack) {
        TrackedStack holder = holderOf(stack);
        return holder == null ? null : holder.igny$getMarks();
    }
}
