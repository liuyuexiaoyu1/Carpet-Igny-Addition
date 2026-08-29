package com.liuyue.igny.mixins.rule.waterCanPushSquid;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Squid;
//#if MC >= 12103
//$$ import net.minecraft.world.entity.animal.AgeableWaterCreature;
//#else
import net.minecraft.world.entity.animal.WaterAnimal;
//#endif
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Squid.class)
public class SquidMixin extends WaterAnimal {
    //#if MC >= 12103
    //$$ protected SquidMixin(EntityType<? extends AgeableWaterCreature> type, Level level)
    //#else
    protected SquidMixin(EntityType<? extends WaterAnimal> type, Level level)
    //#endif
    {
        super(type, level);
    }

    @Inject(method = "travel", at = @At(value = "HEAD"), cancellable = true)
    private void travel(Vec3 travelVector, CallbackInfo ci) {
        if (IGNYSettings.WATER_CAN_PUSH_SQUID.value()) {
            super.travel(travelVector);
            ci.cancel();
        }
    }
}
