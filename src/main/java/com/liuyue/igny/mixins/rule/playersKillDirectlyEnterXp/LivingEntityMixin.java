package com.liuyue.igny.mixins.rule.playersKillDirectlyEnterXp;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    //#if >= 1.21.2
    /*$$@WrapOperation(method = "dropAllDeathLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropExperience(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)V"))
    private void dropExperience(LivingEntity instance, ServerLevel serverLevel, Entity entity, Operation<Void> original, @Local(argsOnly = true) DamageSource damageSource)$$*/
    //#elseif >= 1.21.1
    @WrapOperation(method = "dropAllDeathLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropExperience(Lnet/minecraft/world/entity/Entity;)V"))
    private void dropExperience(LivingEntity instance, Entity entity, Operation<Void> original, @Local(argsOnly = true) DamageSource damageSource)
    //#else
    /*$$@WrapOperation(method = "dropAllDeathLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropExperience()V"))
    private void dropExperience(LivingEntity instance, Operation<Void> original, @Local(argsOnly = true) DamageSource damageSource)$$*/
    //#endif
    {
        if (instance != null && IGNYSettings.PLAYERS_KILL_DIRECTLY_ENTER_XP.value()) {
            if (damageSource != null && !instance.level().isClientSide() && damageSource.getEntity() instanceof Player player) {
                int xp = instance.getExperienceReward((ServerLevel) instance.level(), instance); //#replace <= 1.20.6 ? int xp = instance.getExperienceReward();
                if (xp > 0) {
                    ExperienceOrb orb = new ExperienceOrb(instance.level(),
                            instance.getX(), instance.getY() + 0.5, instance.getZ(), xp);
                    player.takeXpDelay = 0;
                    orb.playerTouch(player);
                }
                return;
            }
        }
        //#if >= 1.21.2
        //$$ original.call(instance, serverLevel, entity);
        //#elseif >= 1.21.1
        original.call(instance, entity);
        //#else
        //$$ original.call(instance);
        //#endif
    }
}
