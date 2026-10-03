package com.liuyue.igny.mixins.rule.minecartClientRidingStateSyncReintroduced;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecart.class)
public abstract class MinecartMixin extends AbstractMinecart {
    protected MinecartMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSecondaryUseActive()Z", shift = At.Shift.AFTER), cancellable = true)
    private void interact(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        if (IGNYSettings.MINECART_CLIENT_SIDE_RIDING_STATE_SYNC_REINTRODUCED.value()) {
            if (!(this.level().isClientSide() || player.startRiding(this))) {
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }

    @WrapOperation(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"))
    private boolean isClientSide(Level instance, Operation<Boolean> original) {
        return !IGNYSettings.MINECART_CLIENT_SIDE_RIDING_STATE_SYNC_REINTRODUCED.value() && original.call(instance);
    }


    @Inject(method = "interact", at = @At(value = "RETURN"), cancellable = true)
    private void modifyInteractRuturn(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        if (IGNYSettings.MINECART_CLIENT_SIDE_RIDING_STATE_SYNC_REINTRODUCED.value()) {
            if (this.level().isClientSide()) {
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }
            InteractionResult result = cir.getReturnValue();
            if (result == InteractionResult.SUCCESS_SERVER) {
                cir.setReturnValue(InteractionResult.CONSUME);
            }
            if (result == InteractionResult.CONSUME) {
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }
}
