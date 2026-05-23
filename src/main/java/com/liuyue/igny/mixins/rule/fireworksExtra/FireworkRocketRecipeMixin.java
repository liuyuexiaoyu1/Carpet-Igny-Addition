package com.liuyue.igny.mixins.rule.fireworksExtra;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FireworkRocketRecipe.class)
public class FireworkRocketRecipeMixin {
    @ModifyConstant(
            //#if MC >= 12101
            method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z",
            //#else
            //$$ method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z",
            //#endif
            constant = @Constant(intValue = 3)
    )
    private int extendMaxGunpowder(int original) {
        return IGNYSettings.FIREWORKS_EXTRA.value() ? 8 : original;
    }
}
