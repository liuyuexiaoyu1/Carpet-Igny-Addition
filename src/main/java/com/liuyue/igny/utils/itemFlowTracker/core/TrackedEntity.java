package com.liuyue.igny.utils.itemFlowTracker.core;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface TrackedEntity {
    boolean igny$handDropped();

    @Nullable
    Vec3 igny$ejectionAnchor();
}
