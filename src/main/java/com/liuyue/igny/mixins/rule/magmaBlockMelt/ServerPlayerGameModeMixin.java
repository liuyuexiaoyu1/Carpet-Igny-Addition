package com.liuyue.igny.mixins.rule.magmaBlockMelt;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean hasCorrectToolForDrops(ServerPlayer instance, BlockState state, Operation<Boolean> original) {
        if (state.is(Blocks.MAGMA_BLOCK)) {
            return IGNYSettings.MAGMA_BLOCK_MELT.value() || original.call(instance, state);
        }
        return original.call(instance, state);
    }
}
