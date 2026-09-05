package com.liuyue.igny.mixins.rule.betterEasyPlaceProtocol;

import com.liuyue.igny.helper.betterEasyPlaceProtocol.ClientEasyPlaceProtocolHelper;
import fi.dy.masa.litematica.util.WorldUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition("litematica"))
@Mixin(WorldUtils.class)
public abstract class EasyPlaceUtilsMixin {
    @Inject(
            method = "applyCarpetProtocolHitVec",
            at = @At(value = "RETURN"),
            require = 0,
            cancellable = true)
    private static void igny_replaceHitPos(BlockPos pos, BlockState state, Vec3 hitVecIn, CallbackInfoReturnable<Vec3> cir) {
        cir.setReturnValue(ClientEasyPlaceProtocolHelper.encodeHitPosItemData(cir.getReturnValue(), pos, state));
    }

    @Inject(
            method = "applyPlacementProtocolV3",
            at = @At(value = "RETURN"),
            require = 0,
            cancellable = true)
    private static void igny_replaceHitPosV3(BlockPos pos, BlockState state, Vec3 hitVecIn, CallbackInfoReturnable<Vec3> cir) {
        cir.setReturnValue(ClientEasyPlaceProtocolHelper.encodeHitPosItemData(cir.getReturnValue(), pos, state));
    }
}
