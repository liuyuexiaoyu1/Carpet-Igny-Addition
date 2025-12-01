package com.liuyue.igny.mixins.rule.OnlyPlayerCreateNetherPortal;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.BlockUtil;
import net.minecraft.world.level.portal.PortalForcer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
    @WrapOperation(
            method = "getExitPortal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/portal/PortalForcer;createPortal(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction$Axis;)Ljava/util/Optional;"
            )
    )
    private static Optional<BlockUtil.FoundRectangle> createPortalHook(PortalForcer instance, BlockPos blockPos, Direction.Axis axis, Operation<Optional<BlockUtil.FoundRectangle>> original, ServerLevel level, Entity entity) {
        if (IGNYSettings.OnlyPlayerCreateNetherPortal&&entity instanceof Player){
            return Optional.empty();
        }else {
            return original.call(instance, blockPos, axis);
        }
    }
}