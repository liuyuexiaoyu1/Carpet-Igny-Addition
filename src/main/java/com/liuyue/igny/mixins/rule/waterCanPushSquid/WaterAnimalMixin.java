package com.liuyue.igny.mixins.rule.waterCanPushSquid;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.animal.Squid;
//#if MC >= 12103
//$$ import net.minecraft.world.entity.animal.AgeableWaterCreature;
//#else
import net.minecraft.world.entity.animal.WaterAnimal;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 12103
//$$ @Mixin(AgeableWaterCreature.class)
//#else
@Mixin(WaterAnimal.class)
//#endif
public class WaterAnimalMixin {
    @Inject(method = "isPushedByFluid", at = @At(value = "HEAD"), cancellable = true)
    private void isPushedByFluid(CallbackInfoReturnable<Boolean> cir) {
        //#if MC >= 12103
        //$$ AgeableWaterCreature waterAnimal = (AgeableWaterCreature) (Object) this;
        //#else
        WaterAnimal waterAnimal = (WaterAnimal) (Object) this;
        //#endif
        if (IGNYSettings.WATER_CAN_PUSH_SQUID.value() && waterAnimal instanceof Squid) {
            cir.setReturnValue(true);
        }
    }
}
