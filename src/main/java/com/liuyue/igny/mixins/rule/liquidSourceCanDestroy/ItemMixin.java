package com.liuyue.igny.mixins.rule.liquidSourceCanDestroy;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Item.class)
public class ItemMixin {
    @ModifyVariable(method = "getPlayerPOVHitResult", at = @At(value = "RETURN"), argsOnly = true)
    private static ClipContext.Fluid getPlayerPOVHitResult(ClipContext.Fluid value) {
        if (IGNYSettings.LIQUID_SOURCE_CAN_DESTROY.value() && value.equals(ClipContext.Fluid.NONE)) {
            return ClipContext.Fluid.SOURCE_ONLY;
        }
        return value;
    }
}
