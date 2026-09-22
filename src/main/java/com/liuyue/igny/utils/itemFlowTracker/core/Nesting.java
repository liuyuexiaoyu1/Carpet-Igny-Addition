package com.liuyue.igny.utils.itemFlowTracker.core;

import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

//#if >= 1.20.5
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
//#endif
//?>= 26.1 ? import net.minecraft.world.item.ItemStackTemplate;

public final class Nesting {
    private static final int MAX_DEPTH = 4;

    @Nullable
    public static TrackMark inStack(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        TrackMark mark = Tracking.get(stack);

        if (mark != null) {
            return mark;
        }

        //#if >= 1.20.5
        return inContents(
                stack.get(DataComponents.CONTAINER),
                stack.get(DataComponents.BUNDLE_CONTENTS),
                0);
        //#else
        //$$ return null;
        //#endif
    }

    public static boolean carriesMark(@Nullable ItemStack stack) {
        return inStack(stack) != null;
    }

    @Nullable
    public static TrackMark inContainer(Container container) {
        if (container == null) return null;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            TrackMark mark = inStack(container.getItem(slot));

            if (mark != null) {
                return mark;
            }
        }

        return null;
    }

    public static boolean holds(Container container, TrackMark mark) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (inStack(container.getItem(slot)) == mark) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public static TrackMark inEntity(Entity entity) {
        if (entity instanceof ItemEntity item) {
            return inStack(item.getItem());
        }

        return entity instanceof Container container ? inContainer(container) : null;
    }

    //#if >= 1.20.5
    @Nullable
    private static TrackMark inContents(
            @Nullable ItemContainerContents container,
            @Nullable BundleContents bundle,
            int depth) {
        //#if >= 26.1
        /*$$if (container != null) {
            for (ItemStackTemplate nested : container.nonEmptyItems()) {
        TrackMark mark = inTemplate(nested, depth + 1);$$*/
        //$$
        /*$$if (mark != null) {
                    return mark;
                }
            }
        }$$*/
        //$$
        /*$$if (bundle != null) {
            for (ItemStackTemplate nested : bundle.items()) {
        TrackMark mark = inTemplate(nested, depth + 1);$$*/
        //$$
        /*$$if (mark != null) {
                    return mark;
        }
                }
        }$$*/
        //#else
        if (container != null) {
            for (ItemStack nested : container.nonEmptyItems()) {
                TrackMark mark = inStack(nested);

                if (mark != null) {
                    return mark;
                }
            }
        }

        if (bundle != null) {
            for (ItemStack nested : bundle.items()) {
                TrackMark mark = inStack(nested);

                if (mark != null) {
                    return mark;
                }
            }
        }
        //#endif

        return null;
    }
    //#endif

    //#if >= 26.1
    /*$$@Nullable
    private static TrackMark inTemplate(ItemStackTemplate template, int depth) {
        if (depth >= MAX_DEPTH) {
        return null;
    }$$*/
    //$$
    /*$$return inContents(
                template.get(DataComponents.CONTAINER),
                template.get(DataComponents.BUNDLE_CONTENTS),
                depth);
    }$$*/
    //#endif
}
