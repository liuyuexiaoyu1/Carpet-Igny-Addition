package com.liuyue.igny.mixins.rule.waterCanPushSquid;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.WaterAnimal; //#replace >= 1.21.3 ? import net.minecraft.world.entity.animal.AgeableWaterCreature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaterAnimal.class) //#replace >= 1.21.3 ? @Mixin(AgeableWaterCreature.class)
public class WaterAnimalMixin {
    @Inject(method = "isPushedByFluid", at = @At(value = "HEAD"), cancellable = true)
    private void isPushedByFluid(CallbackInfoReturnable<Boolean> cir) {
        WaterAnimal waterAnimal = (WaterAnimal) (Object) this; //#replace >= 1.21.3 ? AgeableWaterCreature waterAnimal = (AgeableWaterCreature) (Object) this;
        if (IGNYSettings.WATER_CAN_PUSH_SQUID.value() && waterAnimal instanceof Squid) {
            cir.setReturnValue(true);
        }
    }
}
