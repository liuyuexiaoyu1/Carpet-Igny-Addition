package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import net.minecraft.core.Holder; //?>= 1.20.5
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {
    @Accessor("primaryPower")
    Holder<MobEffect> igny$getPrimaryPower(); //#replace < 1.20.5 ? MobEffect igny$getPrimaryPower();

    @Accessor("secondaryPower")
    Holder<MobEffect> igny$getSecondaryPower(); //#replace < 1.20.5 ? MobEffect igny$getSecondaryPower();
}
