package com.liuyue.igny.mixins.rule.renewableNetherrack;

import com.liuyue.igny.IGNYSettings;
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
            at = @At("TAIL")
    )
    private void createPortal(
            BlockPos pos, Direction.Axis axis, CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir
    ) {
        if (cir.getReturnValue().isEmpty() || !IGNYSettings.RENEWABLE_NETHERRACK.value()) {
            return;
        }

        BlockUtil.FoundRectangle rectangle = cir.getReturnValue().get();
        BlockPos blockPos = rectangle.minCorner;
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        Direction right = direction.getClockWise();
        Direction left = right.getOpposite();

        int p = -1;
        for (int o = -1; o < 3; o++) {
            BlockPos mainPos = blockPos.offset(o * direction.getStepX(), p, o * direction.getStepZ());

            if (o == -1 || o == 2) {
                placeNetherrackForce(mainPos.relative(left, 1));
                placeNetherrackForce(mainPos.relative(right, 1));
            } else {
                placeNetherrackForce(mainPos.relative(left, 2));
                placeNetherrackForce(mainPos.relative(right, 2));
            }
        }
    }

    @Unique
    private void placeNetherrackForce(BlockPos targetPos) {
        BlockState state = this.level.getBlockState(targetPos);
        if (state.isAir() || state.is(Blocks.OBSIDIAN)) {
            this.level.setBlock(targetPos, Blocks.NETHERRACK.defaultBlockState(), 3);
        }
    }
}