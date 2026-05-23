package com.liuyue.igny.mixins.rule.prioritizeFlyingUseItem;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    public abstract InteractionResult useItem(ServerPlayer player, Level level, ItemStack itemStack, InteractionHand hand);

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void useItemOn(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (IGNYSettings.PRIORITIZE_FLYING_USE_ITEM.value() && level != null && !level.getBlockState(hitResult.getBlockPos()).isAir()) {
            if (player.isFallFlying() || player.isAutoSpinAttack()) {
                ItemStack mainHandStack = player.getMainHandItem();
                ItemStack offHandStack = player.getOffhandItem();
                ItemStack itemStack = player.getItemInHand(hand);
                if (hand == InteractionHand.MAIN_HAND && mainHandStack.isEmpty() && !offHandStack.isEmpty()) {
                    cir.setReturnValue(InteractionResult.PASS);
                    return;
                }
                if (!itemStack.isEmpty() && !(itemStack.getItem() instanceof BlockItem)) {
                    cir.setReturnValue(this.useItem(player, level, stack, hand));
                }
            }
        }
    }
}
