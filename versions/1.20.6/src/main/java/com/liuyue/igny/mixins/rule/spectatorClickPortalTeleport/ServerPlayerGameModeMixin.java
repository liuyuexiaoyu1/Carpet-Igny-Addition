package com.liuyue.igny.mixins.rule.spectatorClickPortalTeleport;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void useItemOn(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!IGNYSettings.SPECTATOR_CLICK_PORTAL_TELEPORT.value() || !player.isSpectator()) {
            return;
        }
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.getMenuProvider(level, pos) != null) {
            return;
        }
        if (state.is(Blocks.END_PORTAL) || state.is(Blocks.NETHER_PORTAL) || state.is(Blocks.END_GATEWAY)) {
            ((BlockBehaviourInvoker) state.getBlock()).invokeEntityInside(state, player.level(), pos, player);
            if (state.is(Blocks.NETHER_PORTAL)) {
                ((EntityInvoker) player).invokeHandleNetherPortal();
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
