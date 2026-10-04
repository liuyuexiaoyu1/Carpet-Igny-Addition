package com.liuyue.igny.mixins.rule.vehicleNoSpearBreak;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VehicleEntity.class)
public class VehicleEntityMixin {
    @Inject(method = "hurtServer", at = @At(value = "HEAD"), cancellable = true)
    private void hurtServer(ServerLevel level, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.VEHICLE_NO_SPEAR_BREAK.value()) {
            Entity attacker = damageSource.getEntity();
            if (!(attacker instanceof LivingEntity livingAttacker)) return;
            ItemStack itemStack = livingAttacker.getMainHandItem();
            if (itemStack.is(ItemTags.SPEARS)) {
                cir.setReturnValue(false);
            }
        }
    }
}
