package com.liuyue.igny.mixins.rule.spectatorClickPortalTeleport;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getMenuProvider(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/MenuProvider;", shift = At.Shift.AFTER), cancellable = true)
    private void useItemOn(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir, @Local BlockState state, @Local BlockPos pos) {
        MenuProvider menuProvider = state.getMenuProvider(level, pos);
        if (IGNYSettings.SPECTATOR_CLICK_PORTAL_TELEPORT.value() && menuProvider == null) {
            if (state.is(Blocks.END_PORTAL) || state.is(Blocks.NETHER_PORTAL)) {
                ((BlockBehaviourInvoker) state.getBlock()).invokeEntityInside(state, player.level(), hitResult.getBlockPos(), player);
                if (state.is(Blocks.NETHER_PORTAL)) {
                    ((EntityInvoker) player).invokeHandleNetherPortal();
                }
            }
        }
    }
}
