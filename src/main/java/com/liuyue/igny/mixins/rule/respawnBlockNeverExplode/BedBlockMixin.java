package com.liuyue.igny.mixins.rule.respawnBlockNeverExplode;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if MC <= 12004
//$$ import net.minecraft.world.InteractionHand;
//#endif

@Mixin(BedBlock.class)
public class BedBlockMixin {
    //#if MC <= 12004
    //$$ @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"), cancellable = true)
    //$$ private void useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir)
    //#else
    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"), cancellable = true)
    private void useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir)
    //#endif
    {
        if (IGNYSettings.RESPAWN_BLOCK_NEVER_EXPLODE.value()) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
