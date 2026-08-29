package com.liuyue.igny.mixins.rule.floatingIceWater;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
//#if MC >= 26.3
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.ServerPlayer;
//#else
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
//#endif
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
public class IceBlockMixin {
    //#if MC >= 26.3
    //$$ @Inject(method = "playerDestroy",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"), cancellable = true)
    //$$ private void spawnWater(ServerLevel level, ServerPlayer player, BlockPos blockPos, BlockState state, BlockEntity blockEntity, ItemStack destroyedWith, CallbackInfo ci)
    //#else
    @Inject(method = "playerDestroy",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"), cancellable = true)
    private void spawnWater(Level level, Player player, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity, ItemStack itemStack, CallbackInfo ci)
    //#endif
    {
        if (IGNYSettings.FLOATING_ICE_WATER.value()) {
            level.setBlockAndUpdate(blockPos, Blocks.WATER.defaultBlockState());
            ci.cancel();
        }
    }
}
