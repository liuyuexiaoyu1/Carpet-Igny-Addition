package com.liuyue.igny.mixins.rule.fireworksExtra;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FireworkRocketRecipe.class)
public class FireworkRocketRecipeMixin {
    @ModifyConstant(
            method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z",
            constant = @Constant(intValue = 3)
    )
    private int extendMaxGunpowder(int original) {
        return IGNYSettings.fireworksExtra ? 8 : original;
    }
}
