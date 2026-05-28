package com.liuyue.igny.mixins.rule.renewableNetherrack;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PortalForcer.class)
public class PortalForcerMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @Inject(
            method = "createPortal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 0
            )
    )
    private void injectNetherrackInLoop(
            BlockPos pos,
            Direction.Axis axis,
            CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir,
            @Local(ordinal = 1) BlockPos blockPos,
            @Local Direction direction,
            @Local(ordinal = 2) int o,
            @Local(ordinal = 3) int p
    ) {
        if (p != -1 || o != -1 || !IGNYSettings.RENEWABLE_NETHERRACK.value()) {
            return;
        }
        Direction right = direction.getClockWise();
        blockPos = blockPos.relative(Direction.DOWN, 2);
        placeNetherrackIfAir(
                blockPos.relative(right.getOpposite())
                        .relative(direction.getOpposite())
        );
        placeNetherrackIfAir(
                blockPos.relative(right.getOpposite())
                        .relative(direction, 2)
        );
        placeNetherrackIfAir(
                blockPos.relative(right)
                        .relative(direction.getOpposite())
        );
        placeNetherrackIfAir(
                blockPos.relative(right)
                        .relative(direction, 2)
        );
        placeNetherrackIfAir(
                blockPos.relative(right.getOpposite(), 2)
        );
        placeNetherrackIfAir(
                blockPos.relative(right.getOpposite(), 2)
                        .relative(direction)
        );
        placeNetherrackIfAir(
                blockPos.relative(right, 2)
        );
        placeNetherrackIfAir(
                blockPos.relative(right, 2)
                        .relative(direction)
        );
    }

    @Unique
    private void placeNetherrackIfAir(BlockPos targetPos) {
        BlockState state = this.level.getBlockState(targetPos);
        if (state.isAir()) {
            this.level.setBlockAndUpdate(targetPos, Blocks.NETHERRACK.defaultBlockState());
        }
    }
}