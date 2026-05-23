package com.liuyue.igny.mixins.rule.liquidSourceCanDestroy;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean canDestroyLiquidSource(ServerLevel instance, BlockPos pos, boolean b, Operation<Boolean> original) {
        BlockState state = instance.getBlockState(pos);
        if (IGNYSettings.LIQUID_SOURCE_CAN_DESTROY.value() && state.getBlock() instanceof LiquidBlock) {
            return instance.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        return original.call(instance, pos, b);
    }
}
