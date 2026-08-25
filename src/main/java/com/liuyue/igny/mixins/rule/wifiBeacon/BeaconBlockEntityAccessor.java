package com.liuyue.igny.mixins.rule.wifiBeacon;

//#if MC >= 12005
import net.minecraft.core.Holder;
//#endif
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
        //#if MC <= 12004
        //$$ MobEffect igny$getPrimaryPower();
        //#else
    Holder<MobEffect> igny$getPrimaryPower();
    //#endif

    @Accessor("primaryPower")
        //#if MC <= 12004
        //$$ void igny$setPrimaryPower(MobEffect primaryPower);
        //#else
    void igny$setPrimaryPower(Holder<MobEffect> primaryPower);
    //#endif

    @Accessor("secondaryPower")
        //#if MC <= 12004
        //$$ MobEffect igny$getSecondaryPower();
        //#else
    Holder<MobEffect> igny$getSecondaryPower();
    //#endif

    @Accessor("secondaryPower")
        //#if MC <= 12004
        //$$ void igny$setSecondaryPower(MobEffect primaryPower);
        //#else
    void igny$setSecondaryPower(Holder<MobEffect> secondaryPower);
    //#endif
}
