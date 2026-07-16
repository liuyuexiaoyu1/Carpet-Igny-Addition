package com.liuyue.igny.mixins.rule.snowSpawnNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean setBlockAndUpdate(ServerLevel level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        if (IGNYSettings.SNOW_SPAWN_NO_BLOCK_UPDATE.value() && state.is(Blocks.SNOW)) {
            return level.setBlock(pos, state, 2 | 16);
        }
        return original.call(level, pos, state);
    }
}
