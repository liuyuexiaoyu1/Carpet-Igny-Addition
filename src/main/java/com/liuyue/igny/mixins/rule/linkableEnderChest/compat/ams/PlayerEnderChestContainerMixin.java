package com.liuyue.igny.mixins.rule.linkableEnderChest.compat.ams;

import com.liuyue.igny.utils.RuleUtil;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerEnderChestContainer.class)
public class PlayerEnderChestContainerMixin {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SimpleContainer;<init>(I)V"), index = 0)
    private static int modifyContainerSize(int originalSize) {
        if ((Boolean) RuleUtil.getCarpetRulesValue("carpet-ams-addition", "largeEnderChest")) {
            return 54;
        }
        return originalSize;
    }
}
