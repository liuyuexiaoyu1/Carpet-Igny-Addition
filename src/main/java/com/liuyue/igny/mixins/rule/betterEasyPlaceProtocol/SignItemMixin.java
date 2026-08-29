package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.BetterEasyPlaceProtocolHandler;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SignItem.class)
public class SignItemMixin {
    @ModifyVariable(method = "updateCustomBlockEntityTag", at = @At(value = "STORE"))
    private boolean modifySuccess(boolean success, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) Level level) {
        if (BetterEasyPlaceProtocolHandler.isEasyPlaceState()
                && BetterEasyPlaceProtocolHandler.getPlaceTargetPos().equals(pos)
                && level.getBlockState(pos).is(BetterEasyPlaceProtocolHandler.getPlaceTargetBlock())) {
            return false;
        }
        return success;
    }
}
