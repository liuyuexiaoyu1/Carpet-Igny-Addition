package com.liuyue.igny.mixins.rule.spectatorKeepLeashConnectionReintroduced;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Leashable.class)
public interface LeashableMixin {
    @WrapOperation(method = "tickLeash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;canInteractWithLevel()Z", ordinal = 1))
    private static boolean canInteractWithLevel(Entity instance, Operation<Boolean> original) {
        if (IGNYSettings.SPECTATOR_KEEP_LEASH_CONNECTION_REINTRODUCED.value()) {
            if (instance instanceof Player && instance.isSpectator()) {
                return true;
            }
        }
        return original.call(instance);
    }
}
