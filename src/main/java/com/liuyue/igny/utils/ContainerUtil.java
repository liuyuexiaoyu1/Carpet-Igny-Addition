package com.liuyue.igny.utils;

import com.liuyue.igny.mixins.rule.itemFlowTracker.accessors.CompoundContainerAccessor;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ContainerUtil {

    public static List<Container> leaves(@Nullable Container container) {
        List<Container> leaves = new ArrayList<>();
        forEachLeaf(container, leaves::add);
        return leaves;
    }

    public static void forEachLeaf(@Nullable Container container, Consumer<Container> sink) {
        if (container == null) {
            return;
        }

        if (container instanceof CompoundContainer compound) {
            CompoundContainerAccessor accessor = (CompoundContainerAccessor) compound;
            forEachLeaf(accessor.igny$container1(), sink);
            forEachLeaf(accessor.igny$container2(), sink);
            return;
        }

        sink.accept(container);
    }
}
