package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

//#if MC >= 12005
import net.minecraft.core.Holder;
//#endif
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {
    @Accessor("primaryPower")
        //#if MC >= 12005
    Holder<MobEffect> igny$getPrimaryPower();
    //#else
    //$$ MobEffect igny$getPrimaryPower();
    //#endif

    @Accessor("secondaryPower")
        //#if MC >= 12005
    Holder<MobEffect> igny$getSecondaryPower();
    //#else
    //$$ MobEffect igny$getSecondaryPower();
    //#endif
}
