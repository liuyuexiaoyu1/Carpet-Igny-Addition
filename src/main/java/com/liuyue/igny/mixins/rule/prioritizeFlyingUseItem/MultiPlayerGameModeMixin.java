package com.liuyue.igny.mixins.rule.prioritizeFlyingUseItem;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void useItemOn(LocalPlayer localPlayer, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (IGNYSettings.prioritizeFlyingUseItem && this.minecraft.level != null && !this.minecraft.level.getBlockState(blockHitResult.getBlockPos()).isAir()) {
            if (localPlayer.isFallFlying() || localPlayer.isAutoSpinAttack()) {
                ItemStack mainHandStack = localPlayer.getMainHandItem();
                ItemStack offHandStack = localPlayer.getOffhandItem();
                ItemStack itemStack = localPlayer.getItemInHand(interactionHand);
                if (interactionHand == InteractionHand.MAIN_HAND && mainHandStack.isEmpty() && !offHandStack.isEmpty()) {
                    cir.setReturnValue(InteractionResult.PASS);
                    return;
                }
                if (!itemStack.isEmpty() && !(itemStack.getItem() instanceof BlockItem)) {
                    cir.setReturnValue(InteractionResult.PASS);
                }
            }
        }
    }
}
