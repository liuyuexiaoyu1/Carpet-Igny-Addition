package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {
    @Accessor("primaryPower")
    Holder<MobEffect> igny$getPrimaryPower();

    @Accessor("secondaryPower")
    Holder<MobEffect> igny$getSecondaryPower();
}
