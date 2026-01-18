package com.liuyue.igny.mixins.rule.noWardenDarkness;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Warden.class)
public class WardenMixin {

    @Inject(method = "applyDarknessAround", at = @At("HEAD"), cancellable = true)
    private static void onApplyDarknessAround(ServerLevel serverLevel, Vec3 vec3, Entity entity, int radius, CallbackInfo ci) {
        if (IGNYSettings.noWardenDarkness) {
            ci.cancel();
        }
    }
}