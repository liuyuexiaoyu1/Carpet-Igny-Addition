package com.liuyue.igny.mixins.rule.wifiBeacon;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {
    @Accessor("levels")
    int igny$getLevels();

    @Accessor("levels")
    void igny$setLevels(int levels);

    @Accessor("primaryPower")
    Holder<MobEffect> igny$getPrimaryPower();

    @Accessor("primaryPower")
    void igny$setPrimaryPower(Holder<MobEffect> primaryPower);

    @Accessor("secondaryPower")
    Holder<MobEffect> igny$getSecondaryPower();

    @Accessor("secondaryPower")
    void igny$setSecondaryPower(Holder<MobEffect> secondaryPower);
}
