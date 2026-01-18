package com.liuyue.igny.mixins.commands.clearLightQueue;

import net.minecraft.server.level.ThreadedLevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import it.unimi.dsi.fastutil.objects.ObjectList;

@Mixin(ThreadedLevelLightEngine.class)
public interface ThreadedLevelLightEngineAccessor {
    @Accessor("lightTasks")
    ObjectList<?> getLightTasks();
}