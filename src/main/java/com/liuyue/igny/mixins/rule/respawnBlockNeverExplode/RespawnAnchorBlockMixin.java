package com.liuyue.igny.mixins.rule.respawnBlockNeverExplode;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//#if MC >= 12111
//$$ import net.minecraft.server.level.ServerLevel;
//#endif

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {
    //#if MC >= 12111
    //$$ @WrapOperation(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RespawnAnchorBlock;explode(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V"))
    //#elseif MC <= 12004
    //$$ @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RespawnAnchorBlock;explode(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
    //#else
    @WrapOperation(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RespawnAnchorBlock;explode(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
    //#endif
    //#if MC >= 12111
    //$$ private void explode(RespawnAnchorBlock instance, BlockState state, ServerLevel level, BlockPos pos2, Operation<Void> original)
    //#else
    private void explode(RespawnAnchorBlock instance, BlockState state, Level level, BlockPos pos2, Operation<Void> original)
    //#endif
    {
        if (IGNYSettings.RESPAWN_BLOCK_NEVER_EXPLODE.value()) {
            return;
        }
        original.call(instance, state, level, pos2);
    }
}
